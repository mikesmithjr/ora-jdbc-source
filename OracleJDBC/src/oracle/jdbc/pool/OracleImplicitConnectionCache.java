package oracle.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.xa.client.OracleXADataSource;

class OracleImplicitConnectionCache {
    protected OracleDataSource cacheEnabledDS = null;
    protected String cacheName = null;
    protected OracleConnectionPoolDataSource connectionPoolDS = null;
    protected boolean fastConnectionFailoverEnabled = false;

    protected String defaultUser = null;
    protected String defaultPassword = null;
    protected static final int DEFAULT_MIN_LIMIT = 0;
    protected static final int DEFAULT_MAX_LIMIT = 2147483647;
    protected static final int DEFAULT_INITIAL_LIMIT = 0;
    protected static final int DEFAULT_MAX_STATEMENTS_LIMIT = 0;
    protected static final int DEFAULT_INACTIVITY_TIMEOUT = 0;
    protected static final int DEFAULT_TIMETOLIVE_TIMEOUT = 0;
    protected static final int DEFAULT_ABANDONED_CONN_TIMEOUT = 0;
    protected static final int DEFAULT_CONNECTION_WAIT_TIMEOUT = 0;
    protected static final String DEFAULT_ATTRIBUTE_WEIGHT = "0";
    protected static final int DEFAULT_LOWER_THRESHOLD_LIMIT = 20;
    protected static final int DEFAULT_PROPERTY_CHECK_INTERVAL = 900;
    protected static final int CLOSE_AND_REMOVE_ALL_CONNECTIONS = 1;
    protected static final int CLOSE_AND_REMOVE_FAILOVER_CONNECTIONS = 2;
    protected static final int PROCESS_INACTIVITY_TIMEOUT = 4;
    protected static final int CLOSE_AND_REMOVE_N_CONNECTIONS = 8;
    protected static final int DISABLE_STATEMENT_CACHING = 16;
    protected static final int RESET_STATEMENT_CACHE_SIZE = 18;
    protected static final int CLOSE_AND_REMOVE_RLB_CONNECTIONS = 24;
    private static final String ATTRKEY_DELIM = "0xffff";
    protected int cacheMinLimit = 0;
    protected int cacheMaxLimit = 2147483647;
    protected int cacheInitialLimit = 0;
    protected int cacheMaxStatementsLimit = 0;
    protected Properties cacheAttributeWeights = null;
    protected int cacheInactivityTimeout = 0;
    protected int cacheTimeToLiveTimeout = 0;
    protected int cacheAbandonedConnectionTimeout = 0;
    protected int cacheLowerThresholdLimit = 20;
    protected int cachePropertyCheckInterval = 900;
    protected boolean cacheClosestConnectionMatch = false;
    protected boolean cacheValidateConnection = false;
    protected int cacheConnectionWaitTimeout = 0;
    static final String MIN_LIMIT_KEY = "MinLimit";
    static final String MAX_LIMIT_KEY = "MaxLimit";
    static final String INITIAL_LIMIT_KEY = "InitialLimit";
    static final String MAX_STATEMENTS_LIMIT_KEY = "MaxStatementsLimit";
    static final String ATTRIBUTE_WEIGHTS_KEY = "AttributeWeights";
    static final String INACTIVITY_TIMEOUT_KEY = "InactivityTimeout";
    static final String TIME_TO_LIVE_TIMEOUT_KEY = "TimeToLiveTimeout";
    static final String ABANDONED_CONNECTION_TIMEOUT_KEY = "AbandonedConnectionTimeout";
    static final String LOWER_THRESHOLD_LIMIT_KEY = "LowerThresholdLimit";
    static final String PROPERTY_CHECK_INTERVAL_KEY = "PropertyCheckInterval";
    static final String VALIDATE_CONNECTION_KEY = "ValidateConnection";
    static final String CLOSEST_CONNECTION_MATCH_KEY = "ClosestConnectionMatch";
    static final String CONNECTION_WAIT_TIMEOUT_KEY = "ConnectionWaitTimeout";
    static final int INSTANCE_GOOD = 1;
    static final int INSTANCE_UNKNOWN = 2;
    static final int INSTANCE_VIOLATING = 3;
    static final int INSTANCE_NO_DATA = 4;
    static final int INSTANCE_BLOCKED = 5;
    static final int RLB_NUMBER_OF_HITS_PER_INSTANCE = 1000;
    int dbInstancePercentTotal = 0;
    boolean useGoodGroup = false;
    LinkedList instancesToRetireList = null;
    OracleDatabaseInstance instanceToRetire = null;
    int retireConnectionsCount = 0;
    int countTotal = 0;

    protected OracleConnectionCacheManager cacheManager = null;
    protected boolean disableConnectionRequest = false;
    protected OracleImplicitConnectionCacheThread timeoutThread = null;

    protected OracleRuntimeLoadBalancingEventHandlerThread runtimeLoadBalancingThread = null;

    protected OracleGravitateConnectionCacheThread gravitateCacheThread = null;
    protected int connectionsToRemove = 0;

    private HashMap userMap = null;
    Vector checkedOutConnectionList = null;

    LinkedList databaseInstancesList = null;

    int cacheSize = 0;
    protected static final String EVENT_DELIMITER = " ";
    protected boolean isEntireServiceDownProcessed = false;
    protected int defaultUserPreFailureSize = 0;
    protected String dataSourceServiceName = null;
    protected OracleFailoverWorkerThread failoverWorkerThread = null;
    protected Random rand = null;
    protected int downEventCount = 0;
    protected int upEventCount = 0;
    protected int pendingCreationRequests = 0;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    OracleImplicitConnectionCache(OracleDataSource ds, Properties cacheProps) throws SQLException {
        this.cacheEnabledDS = ds;

        initializeConnectionCache();
        setConnectionCacheProperties(cacheProps);

        defaultUserPrePopulateCache(this.cacheInitialLimit);
    }

    private void defaultUserPrePopulateCache(int sz) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.defaultUserPrePopulateCache(sz="
                                             + sz + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (sz > 0) {
            String user = this.defaultUser;
            String passwd = this.defaultPassword;

            validateUser(user, passwd);

            OraclePooledConnection pc = null;

            for (int i = 0; i < sz; i++) {
                if (getTotalCachedConnections() >= this.cacheMaxLimit)
                    continue;
                pc = makeCacheConnection(user, passwd);

                if (pc == null)
                    continue;
                synchronized (this) {
                    this.cacheSize -= 1;
                }
                storeCacheConnection(user, null, pc);
            }
        }
    }

    protected void initializeConnectionCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.initializeConnectionCache()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        this.userMap = new HashMap();
        this.checkedOutConnectionList = new Vector();

        if (this.cacheManager == null) {
            this.cacheManager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
        }

        this.defaultUser = this.cacheEnabledDS.getUser();
        this.defaultPassword = this.cacheEnabledDS.getPassword();

        if (this.connectionPoolDS == null) {
            if ((this.cacheEnabledDS instanceof OracleXADataSource)) {
                this.connectionPoolDS = new OracleXADataSource();
            } else {
                this.connectionPoolDS = new OracleConnectionPoolDataSource();
            }

            this.cacheEnabledDS.copy(this.connectionPoolDS);
        }

        if ((this.fastConnectionFailoverEnabled = this.cacheEnabledDS
                .getFastConnectionFailoverEnabled())) {
            this.rand = new Random(0L);
            this.cacheManager.failoverEnabledCacheCount += 1;
        }
    }

    private void validateUser(String user, String passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleImplicitConnectionCache.validateUser(user="
                    + user + "passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((user == null) || (passwd == null))
            DatabaseError.throwSqlException(79);
    }

    protected Connection getConnection(String user, String passwd, Properties connAttr)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.getConnection(user=" + user
                                             + "passwd=" + passwd + "connAttr="
                                             + (connAttr != null ? connAttr.toString() : null)
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection pc = null;
        Connection conn = null;
        try {
            if (this.disableConnectionRequest) {
                DatabaseError.throwSqlException(142);
            }

            validateUser(user, passwd);

            if (!user.startsWith("\"")) {
                user = user.toLowerCase();
            }

            if (getNumberOfCheckedOutConnections() < this.cacheMaxLimit) {
                pc = getCacheConnection(user, passwd, connAttr);
            }

            if (pc == null) {
                processConnectionCacheCallback();

                if (this.cacheSize > 0) {
                    pc = getCacheConnection(user, passwd, connAttr);
                }

                if ((pc == null) && (this.cacheConnectionWaitTimeout > 0)) {
                    long localCacheConnectionWaitTimeout = this.cacheConnectionWaitTimeout * 1000L;
                    long beforeWait = System.currentTimeMillis();
                    long afterWait = 0L;
                    do {
                        processConnectionWaitTimeout(localCacheConnectionWaitTimeout);

                        if (this.cacheSize > 0) {
                            pc = getCacheConnection(user, passwd, connAttr);
                        }
                        afterWait = System.currentTimeMillis();
                        localCacheConnectionWaitTimeout -= System.currentTimeMillis() - beforeWait;
                        beforeWait = afterWait;
                    }

                    while ((pc == null) && (localCacheConnectionWaitTimeout > 0L));
                }

            }

            if ((pc != null) && (pc.physicalConn != null)) {
                conn = pc.getConnection();

                if (conn != null) {
                    if ((this.cacheValidateConnection)
                            && (testDatabaseConnection((OracleConnection) conn) != 0)) {
                        ((OracleConnection) conn).close(4096);
                        DatabaseError.throwSqlException(143);
                    }

                    if (this.cacheAbandonedConnectionTimeout > 0) {
                        ((OracleConnection) conn).setAbandonedTimeoutEnabled(true);
                    }

                    if (this.cacheTimeToLiveTimeout > 0) {
                        ((OracleConnection) conn).setStartTime(System.currentTimeMillis());
                    }
                    synchronized (this) {
                        this.cacheSize -= 1;
                        this.checkedOutConnectionList.addElement(pc);
                    }
                }
            }
        } catch (SQLException ea) {
            synchronized (this) {
                if (pc != null) {
                    this.cacheSize -= 1;
                    abortConnection(pc);
                }
            }
            throw ea;
        }

        return conn;
    }

    private OraclePooledConnection getCacheConnection(String user, String passwd,
            Properties connAttr) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.getCacheConnection(user="
                                             + user + ",passwd=" + passwd + ", connAttr="
                                             + (connAttr != null ? connAttr.toString() : null)
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection pc = retrieveCacheConnection(user, passwd, connAttr);
        boolean doMakeCacheConnection = false;

        if (pc == null) {
            synchronized (this) {
                if (getTotalCachedConnections() + this.pendingCreationRequests < this.cacheMaxLimit) {
                    this.pendingCreationRequests += 1;
                    doMakeCacheConnection = true;
                }
            }
        }

        if (doMakeCacheConnection) {
            try {
                pc = makeCacheConnection(user, passwd);
            } finally {
                synchronized (this) {
                    this.pendingCreationRequests -= 1;
                }

            }

            if ((connAttr != null) && (!connAttr.isEmpty())) {
                setUnMatchedAttributes(connAttr, pc);
            }

        }

        return pc;
    }

    protected int getTotalCachedConnections() {
        return this.cacheSize + getNumberOfCheckedOutConnections();
    }

    protected int getNumberOfCheckedOutConnections() {
        return this.checkedOutConnectionList.size();
    }

    private synchronized OraclePooledConnection retrieveCacheConnection(String user, String passwd,
            Properties connAttr) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.retrieveCacheConnection(user="
                                             + user + ", passwd=xxxx, connAttr="
                                             + (connAttr != null ? connAttr.toString() : null)
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection pc = null;
        OracleConnectionCacheEntry m_userConnEntry = (OracleConnectionCacheEntry) this.userMap
                .get(user + passwd.toUpperCase());

        if (m_userConnEntry != null) {
            if ((connAttr == null) || ((connAttr != null) && (connAttr.isEmpty()))) {
                if (m_userConnEntry.userConnList != null)
                    pc = retrieveFromConnectionList(m_userConnEntry.userConnList);
            } else if (m_userConnEntry.attrConnMap != null) {
                String attrKey = buildAttrKey(connAttr);
                Vector attrMapEntry = (Vector) m_userConnEntry.attrConnMap.get(attrKey);

                if (attrMapEntry != null) {
                    pc = retrieveFromConnectionList(attrMapEntry);
                }

                if ((pc == null) && (this.cacheClosestConnectionMatch)) {
                    pc = retrieveClosestConnectionMatch(m_userConnEntry.attrConnMap, connAttr);
                }

                if ((pc == null) && (m_userConnEntry.userConnList != null)) {
                    pc = retrieveFromConnectionList(m_userConnEntry.userConnList);
                }
            }
        }

        if (pc != null) {
            if ((connAttr != null) && (!connAttr.isEmpty())) {
                setUnMatchedAttributes(connAttr, pc);
            }

        }

        return pc;
    }

    private OraclePooledConnection retrieveClosestConnectionMatch(HashMap attrConnMap,
            Properties inAttr) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.retrieveClosestConnectionMatch(attrConnMap="
                                             + attrConnMap + "inAttr="
                                             + (inAttr != null ? inAttr.toString() : null) + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection opc = null;
        OraclePooledConnection currOpc = null;

        int reqMatchCount = inAttr.size();
        int reqWeightCount = 0;
        int actualWeightCount = 0;
        int currWeightCount = 0;
        int actualMatchCount = 0;
        int currMatchCount = 0;

        if (this.cacheAttributeWeights != null) {
            reqWeightCount = getAttributesWeightCount(inAttr, null);
        }
        if ((attrConnMap != null) && (!attrConnMap.isEmpty())) {
            Iterator itr = attrConnMap.entrySet().iterator();

            while (itr.hasNext()) {
                Map.Entry me = (Map.Entry) itr.next();

                Vector aList = (Vector) me.getValue();
                Object[] aObjArr = aList.toArray();
                int asz = aList.size();

                for (int l = 0; l < asz; l++) {
                    opc = (OraclePooledConnection) aObjArr[l];

                    if ((opc.cachedConnectionAttributes == null)
                            || (opc.cachedConnectionAttributes.isEmpty())
                            || (opc.cachedConnectionAttributes.size() > reqMatchCount)) {
                        continue;
                    }

                    if (reqWeightCount > 0) {
                        currWeightCount = getAttributesWeightCount(inAttr,
                                                                   opc.cachedConnectionAttributes);

                        if (currWeightCount <= actualWeightCount)
                            continue;
                        currOpc = opc;
                        actualWeightCount = currWeightCount;
                    } else {
                        currMatchCount = getAttributesMatchCount(inAttr,
                                                                 opc.cachedConnectionAttributes);

                        if (currMatchCount <= actualMatchCount)
                            continue;
                        currOpc = opc;
                        actualMatchCount = currMatchCount;
                    }
                }

            }

        }

        return currOpc;
    }

    private int getAttributesMatchCount(Properties in, Properties curr) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.getAttributesMatchCount("
                                             + in.toString() + ", curr="
                                             + (curr != null ? curr.toString() : null) + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int matchCount = 0;
        Map.Entry me = null;
        Object inKeyObj = null;
        Object inValueObj = null;

        Iterator inSet = in.entrySet().iterator();

        while (inSet.hasNext()) {
            me = (Map.Entry) inSet.next();
            inKeyObj = me.getKey();
            inValueObj = me.getValue();

            if ((!curr.containsKey(inKeyObj)) || (!inValueObj.equals(curr.get(inKeyObj))))
                continue;
            matchCount++;
        }

        return matchCount;
    }

    private int getAttributesWeightCount(Properties inAttr, Properties currAttr)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.getAttributesWeightCount(inAttr="
                                             + inAttr.toString() + ", currAttr="
                                             + (currAttr != null ? currAttr.toString() : null)
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Map.Entry me = null;
        Object inKeyObj = null;
        Object inValueObj = null;
        int weightCount = 0;

        Iterator inSet = inAttr.entrySet().iterator();

        while (inSet.hasNext()) {
            me = (Map.Entry) inSet.next();
            inKeyObj = me.getKey();
            inValueObj = me.getValue();

            if (currAttr == null) {
                if (!this.cacheAttributeWeights.containsKey(inKeyObj))
                    continue;
                weightCount += Integer.parseInt((String) this.cacheAttributeWeights.get(inKeyObj));
                continue;
            }

            if ((!currAttr.containsKey(inKeyObj)) || (!inValueObj.equals(currAttr.get(inKeyObj)))) {
                continue;
            }
            if (this.cacheAttributeWeights.containsKey(inKeyObj)) {
                weightCount += Integer.parseInt((String) this.cacheAttributeWeights.get(inKeyObj));
                continue;
            }

            weightCount++;
        }

        return weightCount;
    }

    private void setUnMatchedAttributes(Properties inAttr, OraclePooledConnection opc)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.setUnMatchedAttributes(inAttr="
                                             + (inAttr != null ? inAttr.toString() : null)
                                             + ",opc=" + opc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (opc.unMatchedCachedConnAttr == null)
            opc.unMatchedCachedConnAttr = new Properties();
        else {
            opc.unMatchedCachedConnAttr.clear();
        }
        if (!this.cacheClosestConnectionMatch) {
            opc.unMatchedCachedConnAttr.putAll(inAttr);
        } else {
            Properties curr = opc.cachedConnectionAttributes;
            Map.Entry me = null;
            Object inKeyObj = null;
            Object inValueObj = null;

            Iterator inSet = inAttr.entrySet().iterator();

            while (inSet.hasNext()) {
                me = (Map.Entry) inSet.next();
                inKeyObj = me.getKey();
                inValueObj = me.getValue();

                if ((curr.containsKey(inKeyObj)) || (inValueObj.equals(curr.get(inKeyObj))))
                    continue;
                opc.unMatchedCachedConnAttr.put(inKeyObj, inValueObj);
            }
        }
    }

    private OraclePooledConnection retrieveFromConnectionList(Vector list) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.retrieveFromConnectionList(# of Conns="
                                             + list.size() + ", list=" + list + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (list.isEmpty()) {
            return null;
        }
        OraclePooledConnection pc = null;

        if (this.fastConnectionFailoverEnabled) {
            if ((this.useGoodGroup) && (this.databaseInstancesList != null)
                    && (this.databaseInstancesList.size() > 0)) {
                label360: synchronized (this.databaseInstancesList) {
                    int numInstances = this.databaseInstancesList.size();
                    OracleDatabaseInstance dbInstance = null;
                    int randomPercent = 0;

                    boolean[] tried = new boolean[numInstances];
                    int total = this.dbInstancePercentTotal;

                    for (int j = 0; j < numInstances; j++) {
                        int percentSum = 0;

                        if (total <= 1)
                            randomPercent = 0;
                        else {
                            randomPercent = this.rand.nextInt(total - 1);
                        }

                        for (int i = 0; i < numInstances; i++) {
                            dbInstance = (OracleDatabaseInstance) this.databaseInstancesList.get(i);

                            if ((tried[i] != false) || (dbInstance.flag > 3))
                                continue;
                            percentSum += dbInstance.percent;

                            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.poolLogger.log(Level.FINER,
                                                         "OracleImplicitConnectionCache.retrieveFromConnectionList()RandomPercent="
                                                                 + randomPercent + ", percentSum="
                                                                 + percentSum, this);

                                OracleLog.recursiveTrace = false;
                            }

                            if (randomPercent > percentSum) {
                                continue;
                            }
                            if (j == 0)
                                dbInstance.attemptedConnRequestCount += 1;

                            if ((pc = selectConnectionFromList(list, dbInstance)) != null) {
                                break label360;
                            }

                            total -= dbInstance.percent;
                            tried[i] = true;
                            break;
                        }

                    }

                }

            }

            int sz = list.size();
            int pos = this.rand.nextInt(sz);
            OraclePooledConnection tmpPc = null;

            for (int i = 0; i < sz; i++) {
                tmpPc = (OraclePooledConnection) list.get((pos++ + sz) % sz);

                if (tmpPc.connectionMarkedDown)
                    continue;
                pc = tmpPc;
                list.remove(pc);
                break;
            }

        } else {
            pc = (OraclePooledConnection) list.remove(0);
        }

        return pc;
    }

    private OraclePooledConnection selectConnectionFromList(Vector list,
            OracleDatabaseInstance dbInstance) {
        OraclePooledConnection pc = null;
        OraclePooledConnection tmpPc = null;

        int sz = list.size();
        for (int i = 0; i < sz; i++) {
            tmpPc = (OraclePooledConnection) list.get(i);

            if ((tmpPc.connectionMarkedDown)
                    || (tmpPc.dataSourceDbUniqNameKey != dbInstance.databaseUniqName)
                    || (tmpPc.dataSourceInstanceNameKey != dbInstance.instanceName)) {
                continue;
            }
            pc = tmpPc;
            list.remove(pc);
            break;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "(RLB) OracleImplicitConnectionCache.selectConnectionFromList(pc="
                                             + pc + ")" + " <<< ServiceName="
                                             + this.dataSourceServiceName + ", Connections to "
                                             + dbInstance.instanceName + " = "
                                             + dbInstance.numberOfConnectionsCount
                                             + ", Attempted Connection Requests to this instance="
                                             + dbInstance.attemptedConnRequestCount
                                             + ", Total Connections=" + getTotalCachedConnections()
                                             + " >>>", this);

            OracleLog.recursiveTrace = false;
        }

        return pc;
    }

    private void removeCacheConnection(OraclePooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.removeCacheConnection(pc=" + pc
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean pcRemoved = false;

        String user = pc.pcUser;

        OracleConnectionCacheEntry m_userConnEntry = pc.removeFromImplictCache(this.userMap);

        if (m_userConnEntry != null) {
            Properties connAttr = pc.cachedConnectionAttributes;

            if ((connAttr == null) || ((connAttr != null) && (connAttr.isEmpty()))) {
                if (m_userConnEntry.userConnList != null)
                    pcRemoved = m_userConnEntry.userConnList.removeElement(pc);
            } else if (m_userConnEntry.attrConnMap != null) {
                String attrKey = buildAttrKey(connAttr);

                Vector attrMapEntry = (Vector) m_userConnEntry.attrConnMap.get(attrKey);

                if (attrMapEntry != null) {
                    if (pc.unMatchedCachedConnAttr != null) {
                        pc.unMatchedCachedConnAttr.clear();
                        pc.unMatchedCachedConnAttr = null;
                    }

                    if (pc.cachedConnectionAttributes != null) {
                        pc.cachedConnectionAttributes.clear();
                        pc.cachedConnectionAttributes = null;
                    }

                    connAttr = null;
                    pcRemoved = attrMapEntry.removeElement(pc);
                }
            }

        }

        if (pcRemoved) {
            this.cacheSize -= 1;
        }
    }

    protected void doForEveryCachedConnection(int task) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.doForEveryCachedConnection(task="
                                             + task + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int repSize = 0;

        synchronized (this) {
            if ((this.userMap != null) && (!this.userMap.isEmpty())) {
                Iterator i = this.userMap.entrySet().iterator();

                while (i.hasNext()) {
                    Map.Entry e = (Map.Entry) i.next();
                    OracleConnectionCacheEntry occe = (OracleConnectionCacheEntry) e.getValue();

                    if ((occe.userConnList != null) && (!occe.userConnList.isEmpty())) {
                        Vector uList = occe.userConnList;
                        Object[] objArr = uList.toArray();

                        for (int k = 0; k < objArr.length; k++) {
                            OraclePooledConnection pc = (OraclePooledConnection) objArr[k];

                            if ((pc != null) && (performPooledConnectionTask(pc, task))) {
                                repSize++;
                            }
                        }
                    }
                    if ((occe.attrConnMap != null) && (!occe.attrConnMap.isEmpty())) {
                        Iterator j = occe.attrConnMap.entrySet().iterator();

                        while (j.hasNext()) {
                            Map.Entry me = (Map.Entry) j.next();

                            Vector aList = (Vector) me.getValue();
                            Object[] objArr = aList.toArray();

                            for (int l = 0; l < objArr.length; l++) {
                                OraclePooledConnection pc = (OraclePooledConnection) objArr[l];

                                if ((pc != null) && (performPooledConnectionTask(pc, task))) {
                                    repSize++;
                                }
                            }
                        }
                        if (task == 1) {
                            occe.attrConnMap.clear();
                        }
                    }
                }

                if (task == 1) {
                    this.userMap.clear();

                    this.cacheSize = 0;
                }

            }

        }

        if (repSize > 0) {
            defaultUserPrePopulateCache(repSize);
        }
    }

    private boolean performPooledConnectionTask(OraclePooledConnection pc, int task)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.performPooledConnectionTask(pc="
                                             + pc + "task=" + task + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean mustReplace = false;

        switch (task) {
        case 2:
            if (!pc.connectionMarkedDown)
                break;
            closeAndRemovePooledConnection(pc);
            break;
        case 8:
            if (this.connectionsToRemove <= 0)
                break;
            closeAndRemovePooledConnection(pc);

            this.connectionsToRemove -= 1;
            break;
        case 24:
            if (this.retireConnectionsCount <= 0)
                break;
            if ((this.instanceToRetire.databaseUniqName != pc.dataSourceDbUniqNameKey)
                    || (this.instanceToRetire.instanceName != pc.dataSourceInstanceNameKey)) {
                break;
            }
            closeAndRemovePooledConnection(pc);
            this.retireConnectionsCount -= 1;

            if (getTotalCachedConnections() >= this.cacheMinLimit)
                break;
            mustReplace = true;
            break;
        case 4096:
            Connection conn = pc.getLogicalHandle();

            if (conn == null)
                break;
            if (testDatabaseConnection((OracleConnection) conn) == 0) {
                break;
            }
            closeAndRemovePooledConnection(pc);

            mustReplace = true;
            break;
        case 8192:
            closeAndRemovePooledConnection(pc);

            mustReplace = true;

            break;
        case 1:
            closeAndRemovePooledConnection(pc);

            break;
        case 4:
            processInactivityTimeout(pc);

            break;
        case 16:
            setStatementCaching(pc, this.cacheMaxStatementsLimit, false);

            break;
        case 18:
            setStatementCaching(pc, this.cacheMaxStatementsLimit, true);

            break;
        }

        return mustReplace;
    }

    protected synchronized void doForEveryCheckedOutConnection(int task) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.doForEveryCheckedOutConnection(task="
                                             + task + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int sz = this.checkedOutConnectionList.size();

        if (task == 1) {
            for (int i = 0; i < sz; i++) {
                closeCheckedOutConnection((OraclePooledConnection) this.checkedOutConnectionList
                        .get(i), false);
            }

            this.checkedOutConnectionList.removeAllElements();
        } else if (task == 24) {
            for (int i = 0; (i < sz) && (this.retireConnectionsCount > 0); i++) {
                OraclePooledConnection pc = (OraclePooledConnection) this.checkedOutConnectionList
                        .get(i);
                if ((this.instanceToRetire.databaseUniqName != pc.dataSourceDbUniqNameKey)
                        || (this.instanceToRetire.instanceName != pc.dataSourceInstanceNameKey)) {
                    continue;
                }
                pc.closeOption = 4096;

                this.retireConnectionsCount -= 2;
            }
        }
    }

    protected void closeCheckedOutConnection(OraclePooledConnection pc, boolean isReuse)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.closeCheckedOutConnection(pc="
                                             + pc + ",isReuse=" + isReuse + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pc != null) {
            OracleConnection conn = (OracleConnection) pc.getLogicalHandle();
            try {
                if ((!conn.getAutoCommit()) && (!pc.isHostDown)) {
                    conn.rollback();
                }

            } catch (SQLException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleImplicitConnectionCache.closeCheckedOutConnection():rollback()"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

            if (isReuse) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleImplicitConnectionCache.closeCheckedOutConnection():close()"
                                                         + e, this);

                        OracleLog.recursiveTrace = false;
                    }
                }

            } else {
                actualPooledConnectionClose(pc);
            }
        }
    }

    private synchronized void storeCacheConnection(String user, Properties connAttr,
            OraclePooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.storeCacheConnection(user="
                                             + user + ", connAttr="
                                             + (connAttr != null ? connAttr.toString() : null)
                                             + ", pc=" + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean pcAdded = false;

        if ((pc == null) || (pc.physicalConn == null)) {
            return;
        }

        if (this.cacheInactivityTimeout > 0) {
            pc.setLastAccessedTime(System.currentTimeMillis());
        }

        if (pc.unMatchedCachedConnAttr != null) {
            pc.unMatchedCachedConnAttr.clear();
            pc.unMatchedCachedConnAttr = null;
        }

        if (!user.startsWith("\"")) {
            user = user.toLowerCase();
        }
        OracleConnectionCacheEntry m_userConnEntry = pc.removeFromImplictCache(this.userMap);

        if (m_userConnEntry != null) {
            if ((connAttr == null) || ((connAttr != null) && (connAttr.isEmpty()))) {
                if (m_userConnEntry.userConnList == null) {
                    m_userConnEntry.userConnList = new Vector();
                }
                pcAdded = m_userConnEntry.userConnList.add(pc);
            } else {
                pc.cachedConnectionAttributes = connAttr;

                if (m_userConnEntry.attrConnMap == null) {
                    m_userConnEntry.attrConnMap = new HashMap();
                }
                String attrKey = buildAttrKey(connAttr);
                Vector attrMapEntry = (Vector) m_userConnEntry.attrConnMap.get(attrKey);

                if (attrMapEntry != null) {
                    pcAdded = attrMapEntry.add(pc);
                } else {
                    attrMapEntry = new Vector();

                    pcAdded = attrMapEntry.add(pc);
                    m_userConnEntry.attrConnMap.put(attrKey, attrMapEntry);
                }
            }
        } else {
            m_userConnEntry = new OracleConnectionCacheEntry();

            pc.addToImplicitCache(this.userMap, m_userConnEntry);

            if ((connAttr == null) || ((connAttr != null) && (connAttr.isEmpty()))) {
                Vector userMapEntry = new Vector();

                pcAdded = userMapEntry.add(pc);

                m_userConnEntry.userConnList = userMapEntry;
            } else {
                String attrKey = buildAttrKey(connAttr);

                pc.cachedConnectionAttributes = connAttr;

                HashMap userMapEntry = new HashMap();
                Vector attrMapEntry = new Vector();

                pcAdded = attrMapEntry.add(pc);
                userMapEntry.put(attrKey, attrMapEntry);

                m_userConnEntry.attrConnMap = userMapEntry;
            }

        }

        if (pcAdded) {
            this.cacheSize += 1;
        }

        if (this.cacheConnectionWaitTimeout > 0) {
            notifyAll();
        }
    }

    private String buildAttrKey(Properties connAttr) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.buildAttrKey(connAttr="
                                             + connAttr.toString() + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int sz = connAttr.keySet().size();
        Object[] origArray = connAttr.keySet().toArray();
        boolean isChanged = true;
        StringBuffer buf = new StringBuffer();

        while (isChanged) {
            isChanged = false;

            for (int i = 0; i < sz - 1; i++) {
                if (((String) origArray[i]).compareTo((String) origArray[(i + 1)]) <= 0)
                    continue;
                isChanged = true;

                Object tempObj = origArray[i];

                origArray[i] = origArray[(i + 1)];
                origArray[(i + 1)] = tempObj;
            }

        }

        for (int i = 0; i < sz; i++) {
            buf.append(origArray[i] + "0xffff" + connAttr.get(origArray[i]));
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.buildAttrKey() Return Key="
                                             + buf.toString(), this);

            OracleLog.recursiveTrace = false;
        }

        return buf.toString();
    }

    protected OraclePooledConnection makeCacheConnection(String user, String passwd)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.makeCacheConnection(user="
                                             + user + "passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection pc = (OraclePooledConnection) this.connectionPoolDS
                .getPooledConnection(user, passwd);

        if (pc != null) {
            if (this.cacheMaxStatementsLimit > 0) {
                setStatementCaching(pc, this.cacheMaxStatementsLimit, true);
            }

            pc.registerImplicitCacheConnectionEventListener(new OracleConnectionCacheEventListener(
                    this));

            pc.cachedConnectionAttributes = new Properties();

            if (this.fastConnectionFailoverEnabled) {
                initFailoverParameters(pc);
            }

            synchronized (this) {
                this.cacheSize += 1;

                if ((this.fastConnectionFailoverEnabled)
                        && (this.runtimeLoadBalancingThread == null)) {
                    this.runtimeLoadBalancingThread = new OracleRuntimeLoadBalancingEventHandlerThread(
                            this.dataSourceServiceName);

                    this.cacheManager.checkAndStartThread(this.runtimeLoadBalancingThread);
                }
            }

        }

        return pc;
    }

    private void setStatementCaching(OraclePooledConnection pc, int maxLimit, boolean flag)
            throws SQLException {
        if (maxLimit > 0) {
            pc.setStatementCacheSize(maxLimit);
        }
        pc.setImplicitCachingEnabled(flag);
        pc.setExplicitCachingEnabled(flag);
    }

    protected synchronized void reusePooledConnection(PooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.reusePooledConnection(pc=" + pc
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection opc = (OraclePooledConnection) pc;
        if ((opc != null) && (opc.physicalConn != null)) {
            storeCacheConnection(opc.pcUser, opc.cachedConnectionAttributes, opc);

            this.checkedOutConnectionList.removeElement(opc);
        }
    }

    protected void closePooledConnection(PooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.closePooledConnection(pc=" + pc
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pc != null) {
            actualPooledConnectionClose((OraclePooledConnection) pc);

            if (((OraclePooledConnection) pc).closeOption == 4096) {
                this.checkedOutConnectionList.removeElement(pc);
            }
            pc = null;

            if (getTotalCachedConnections() < this.cacheMinLimit)
                defaultUserPrePopulateCache(1);
        }
    }

    protected void refreshCacheConnections(int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.refreshCacheConnections(mode="
                                             + mode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        doForEveryCachedConnection(mode);
    }

    protected synchronized void reinitializeCacheConnections(Properties cp) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.reinitializeCacheConnections(cp="
                                             + cp + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.defaultUser = this.cacheEnabledDS.getUser();
        this.defaultPassword = this.cacheEnabledDS.getPassword();
        this.fastConnectionFailoverEnabled = this.cacheEnabledDS.getFastConnectionFailoverEnabled();

        cleanupTimeoutThread();

        doForEveryCheckedOutConnection(1);

        int origCacheInitialLimit = this.cacheInitialLimit;
        int origCacheMaxLimit = this.cacheMaxLimit;
        int origCacheMaxStatementsLimit = this.cacheMaxStatementsLimit;

        setConnectionCacheProperties(cp);

        if (this.cacheInitialLimit > origCacheInitialLimit) {
            int sz = this.cacheInitialLimit - origCacheInitialLimit;

            defaultUserPrePopulateCache(sz);
        }

        if (origCacheMaxLimit != 2147483647) {
            if ((this.cacheMaxLimit < origCacheMaxLimit) && (this.cacheSize > this.cacheMaxLimit)) {
                this.connectionsToRemove = (this.cacheSize - this.cacheMaxLimit);

                doForEveryCachedConnection(8);

                this.connectionsToRemove = 0;
            }

        }

        if (this.cacheMaxStatementsLimit != origCacheMaxStatementsLimit) {
            if (this.cacheMaxStatementsLimit == 0)
                doForEveryCachedConnection(16);
            else
                doForEveryCachedConnection(18);
        }
    }

    protected synchronized void setConnectionCacheProperties(Properties cp) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.setConnectionCacheProperties(cp="
                                             + (cp != null ? cp.toString() : null) + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (cp != null) {
                String inVal = null;

                if ((inVal = cp.getProperty("MinLimit")) != null) {
                    if ((this.cacheMinLimit = Integer.parseInt(inVal)) < 0) {
                        this.cacheMinLimit = 0;
                    }
                }

                if ((inVal = cp.getProperty("MaxLimit")) != null) {
                    if ((this.cacheMaxLimit = Integer.parseInt(inVal)) < 0) {
                        this.cacheMaxLimit = 2147483647;
                    }
                }

                if (this.cacheMaxLimit < this.cacheMinLimit) {
                    this.cacheMinLimit = this.cacheMaxLimit;
                }

                if ((inVal = cp.getProperty("InitialLimit")) != null) {
                    if ((this.cacheInitialLimit = Integer.parseInt(inVal)) < 0) {
                        this.cacheInitialLimit = 0;
                    }
                }
                if (this.cacheInitialLimit > this.cacheMaxLimit) {
                    this.cacheInitialLimit = this.cacheMaxLimit;
                }

                if ((inVal = cp.getProperty("MaxStatementsLimit")) != null) {
                    if ((this.cacheMaxStatementsLimit = Integer.parseInt(inVal)) < 0) {
                        this.cacheMaxStatementsLimit = 0;
                    }
                }

                Properties caw = (Properties) cp.get("AttributeWeights");

                if (caw != null) {
                    Map.Entry me = null;
                    int weight = 0;
                    Object inKeyObj = null;

                    Iterator inSet = caw.entrySet().iterator();

                    while (inSet.hasNext()) {
                        me = (Map.Entry) inSet.next();
                        inKeyObj = me.getKey();

                        if (((inVal = (String) caw.get(inKeyObj)) == null)
                                || ((weight = Integer.parseInt(inVal)) >= 0))
                            continue;
                        caw.put(inKeyObj, "0");
                    }

                    if (this.cacheAttributeWeights == null) {
                        this.cacheAttributeWeights = new Properties();
                    }
                    this.cacheAttributeWeights.putAll(caw);
                }

                if ((inVal = cp.getProperty("InactivityTimeout")) != null) {
                    if ((this.cacheInactivityTimeout = Integer.parseInt(inVal)) < 0) {
                        this.cacheInactivityTimeout = 0;
                    }
                }

                if ((inVal = cp.getProperty("TimeToLiveTimeout")) != null) {
                    if ((this.cacheTimeToLiveTimeout = Integer.parseInt(inVal)) < 0) {
                        this.cacheTimeToLiveTimeout = 0;
                    }
                }

                if ((inVal = cp.getProperty("AbandonedConnectionTimeout")) != null) {
                    if ((this.cacheAbandonedConnectionTimeout = Integer.parseInt(inVal)) < 0) {
                        this.cacheAbandonedConnectionTimeout = 0;
                    }
                }

                if ((inVal = cp.getProperty("LowerThresholdLimit")) != null) {
                    this.cacheLowerThresholdLimit = Integer.parseInt(inVal);

                    if ((this.cacheLowerThresholdLimit < 0)
                            || (this.cacheLowerThresholdLimit > 100)) {
                        this.cacheLowerThresholdLimit = 20;
                    }
                }

                if ((inVal = cp.getProperty("PropertyCheckInterval")) != null) {
                    if ((this.cachePropertyCheckInterval = Integer.parseInt(inVal)) < 0) {
                        this.cachePropertyCheckInterval = 900;
                    }
                }

                if ((inVal = cp.getProperty("ValidateConnection")) != null) {
                    this.cacheValidateConnection = Boolean.valueOf(inVal).booleanValue();
                }

                if ((inVal = cp.getProperty("ClosestConnectionMatch")) != null) {
                    this.cacheClosestConnectionMatch = Boolean.valueOf(inVal).booleanValue();
                }

                if ((inVal = cp.getProperty("ConnectionWaitTimeout")) != null) {
                    if ((this.cacheConnectionWaitTimeout = Integer.parseInt(inVal)) < 0) {
                        this.cacheConnectionWaitTimeout = 0;
                    }
                }
            } else {
                this.cacheMinLimit = 0;
                this.cacheMaxLimit = 2147483647;
                this.cacheInitialLimit = 0;
                this.cacheMaxStatementsLimit = 0;
                this.cacheAttributeWeights = null;
                this.cacheInactivityTimeout = 0;
                this.cacheTimeToLiveTimeout = 0;
                this.cacheAbandonedConnectionTimeout = 0;
                this.cacheLowerThresholdLimit = 20;
                this.cachePropertyCheckInterval = 900;
                this.cacheClosestConnectionMatch = false;
                this.cacheValidateConnection = false;
                this.cacheConnectionWaitTimeout = 0;
            }

            if ((this.cacheInactivityTimeout > 0) || (this.cacheTimeToLiveTimeout > 0)
                    || (this.cacheAbandonedConnectionTimeout > 0)) {
                if (this.timeoutThread == null) {
                    this.timeoutThread = new OracleImplicitConnectionCacheThread(this);
                }
                this.cacheManager.checkAndStartThread(this.timeoutThread);
            }
        } catch (NumberFormatException nfe) {
            DatabaseError.throwSqlException(139);
        }
    }

    protected Properties getConnectionCacheProperties() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "OracleImplicitConnectionCache.getConnectionCacheProperties()", this);

            OracleLog.recursiveTrace = false;
        }

        Properties cacheProp = new Properties();

        cacheProp.setProperty("MinLimit", String.valueOf(this.cacheMinLimit));
        cacheProp.setProperty("MaxLimit", String.valueOf(this.cacheMaxLimit));
        cacheProp.setProperty("InitialLimit", String.valueOf(this.cacheInitialLimit));
        cacheProp.setProperty("MaxStatementsLimit", String.valueOf(this.cacheMaxStatementsLimit));

        if (this.cacheAttributeWeights != null)
            cacheProp.put("AttributeWeights", this.cacheAttributeWeights);
        else {
            cacheProp.setProperty("AttributeWeights", "NULL");
        }
        cacheProp.setProperty("InactivityTimeout", String.valueOf(this.cacheInactivityTimeout));

        cacheProp.setProperty("TimeToLiveTimeout", String.valueOf(this.cacheTimeToLiveTimeout));

        cacheProp.setProperty("AbandonedConnectionTimeout", String
                .valueOf(this.cacheAbandonedConnectionTimeout));

        cacheProp.setProperty("LowerThresholdLimit", String.valueOf(this.cacheLowerThresholdLimit));

        cacheProp.setProperty("PropertyCheckInterval", String
                .valueOf(this.cachePropertyCheckInterval));

        cacheProp.setProperty("ConnectionWaitTimeout", String
                .valueOf(this.cacheConnectionWaitTimeout));

        cacheProp.setProperty("ValidateConnection", String.valueOf(this.cacheValidateConnection));

        cacheProp.setProperty("ClosestConnectionMatch", String
                .valueOf(this.cacheClosestConnectionMatch));

        return cacheProp;
    }

    protected int testDatabaseConnection(OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.testDatabaseConnection(conn="
                                             + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return conn.pingDatabase(0);
    }

    protected synchronized void closeConnectionCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.closeConnectionCache()", this);

            OracleLog.recursiveTrace = false;
        }

        cleanupTimeoutThread();

        purgeCacheConnections(true);

        this.connectionPoolDS = null;
        this.cacheEnabledDS = null;
        this.checkedOutConnectionList = null;
        this.userMap = null;
        this.cacheManager = null;
    }

    protected synchronized void disableConnectionCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleImplicitConnectionCache.disableConnectionCache()", this);

            OracleLog.recursiveTrace = false;
        }

        this.disableConnectionRequest = true;
    }

    protected synchronized void enableConnectionCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.enableConnectionCache()", this);

            OracleLog.recursiveTrace = false;
        }

        this.disableConnectionRequest = false;
    }

    protected void initFailoverParameters(OraclePooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleImplicitConnectionCache.initFailoverParameters()", this);

            OracleLog.recursiveTrace = false;
        }

        String instanceNameKey = null;
        String DBUniqNameKey = null;
        String val = null;

        Properties prop = ((OracleConnection) pc.getPhysicalHandle()).getServerSessionInfo();

        val = prop.getProperty("INSTANCE_NAME");
        if (val != null) {
            instanceNameKey = pc.dataSourceInstanceNameKey = val.trim().intern();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "Instance Name = " + val, this);

            OracleLog.recursiveTrace = false;
        }

        val = prop.getProperty("SERVER_HOST");
        if (val != null) {
            pc.dataSourceHostNameKey = val.trim().intern();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "Host Name = " + val, this);

            OracleLog.recursiveTrace = false;
        }

        val = prop.getProperty("SERVICE_NAME");
        if (val != null) {
            this.dataSourceServiceName = val.trim();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "Service Name = " + val, this);

            OracleLog.recursiveTrace = false;
        }

        val = prop.getProperty("DATABASE_NAME");
        if (val != null) {
            DBUniqNameKey = pc.dataSourceDbUniqNameKey = val.trim().intern();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "DBUniq Name = " + val, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.databaseInstancesList == null) {
            this.databaseInstancesList = new LinkedList();
        }
        int numInstances = this.databaseInstancesList.size();
        synchronized (this.databaseInstancesList) {
            OracleDatabaseInstance dbInstance = null;
            boolean found = false;

            for (int i = 0; i < numInstances; i++) {
                dbInstance = (OracleDatabaseInstance) this.databaseInstancesList.get(i);
                if ((dbInstance.databaseUniqName != DBUniqNameKey)
                        || (dbInstance.instanceName != instanceNameKey)) {
                    continue;
                }
                dbInstance.numberOfConnectionsCount += 1;
                found = true;
                break;
            }

            if (!found) {
                OracleDatabaseInstance tmpInstance = new OracleDatabaseInstance(DBUniqNameKey,
                        instanceNameKey);

                tmpInstance.numberOfConnectionsCount += 1;
                this.databaseInstancesList.add(tmpInstance);
            }
        }
    }

    protected void processFailoverEvent(int eventType, String instNameKey, String dbUniqNameKey,
            String hostNameKey, String status, int card) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.processFailoverEvent(eventType="
                                             + eventType + ",instName=" + instNameKey
                                             + ",dbUniqName=" + dbUniqNameKey + ",hostName="
                                             + hostNameKey + ",status=" + status + ", card=" + card
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (eventType == 256) {
            if ((status.equalsIgnoreCase("down")) || (status.equalsIgnoreCase("not_restarting"))
                    || (status.equalsIgnoreCase("restart_failed"))) {
                this.downEventCount += 1;

                markDownLostConnections(true, false, instNameKey, dbUniqNameKey, hostNameKey,
                                        status);

                cleanupFailoverConnections(true, false, instNameKey, dbUniqNameKey, hostNameKey,
                                           status);
            } else if (status.equalsIgnoreCase("up")) {
                if (this.downEventCount > 0) {
                    this.upEventCount += 1;
                }
                try {
                    processUpEvent(card);
                } catch (Exception e) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleImplicitConnectionCache.processFailoverEvent()"
                                                         + e, this);

                        OracleLog.recursiveTrace = false;
                    }

                }

                this.isEntireServiceDownProcessed = false;
            }
        } else if ((eventType == 512) && (status.equalsIgnoreCase("nodedown"))) {
            markDownLostConnections(false, true, instNameKey, dbUniqNameKey, hostNameKey, status);

            cleanupFailoverConnections(false, true, instNameKey, dbUniqNameKey, hostNameKey, status);
        } else if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER,
                                     "OracleImplicitConnectionCache:processFailoverEventUnKnown Event:"
                                             + eventType, this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized void processUpEvent(int card) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.processUpEvent(Cardinality="
                                             + card + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int selectedConnections = 0;
        int connectionsToLoadBalance = 0;
        int totalConnections = getTotalCachedConnections();
        boolean isRecreate = false;

        if (card <= 1) {
            card = 2;
        }

        if ((this.downEventCount == 0) && (this.upEventCount == 0)
                && (getNumberOfDefaultUserConnections() > 0)) {
            selectedConnections = (int) (this.cacheSize * 0.25D);
        } else {
            selectedConnections = this.defaultUserPreFailureSize;
        }

        if (selectedConnections <= 0) {
            if (getNumberOfDefaultUserConnections() > 0) {
                connectionsToLoadBalance = (int) (this.cacheSize * 0.25D);
                isRecreate = true;
            } else {
                return;
            }
        } else {
            connectionsToLoadBalance = selectedConnections / card;

            if (connectionsToLoadBalance + totalConnections > this.cacheMaxLimit) {
                isRecreate = true;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER,
                                     "OracleImplicitConnectionCache.processUpEvent()selectedConnections="
                                             + selectedConnections + ", connectionsToLoadBalance="
                                             + connectionsToLoadBalance, this);

            OracleLog.recursiveTrace = false;
        }

        if (connectionsToLoadBalance > 0) {
            loadBalanceConnections(connectionsToLoadBalance, isRecreate);
        }

        if (this.downEventCount == this.upEventCount) {
            this.defaultUserPreFailureSize = 0;
            this.downEventCount = 0;
            this.upEventCount = 0;
        }
    }

    private void loadBalanceConnections(int connectionsToLoadBalance, boolean isRecreate)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.loadBalanceConnections(connectionsToLoadBalance="
                                             + connectionsToLoadBalance + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (isRecreate) {
            this.connectionsToRemove = connectionsToLoadBalance;

            doForEveryCachedConnection(8);

            this.connectionsToRemove = 0;
        }

        if (connectionsToLoadBalance <= 10) {
            try {
                defaultUserPrePopulateCache(connectionsToLoadBalance);
            } catch (Exception e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleImplicitConnectionCache:loadBalanceConnections"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

        } else {
            int val = (int) (connectionsToLoadBalance * 0.25D);

            for (int i = 0; i < 4; i++) {
                try {
                    defaultUserPrePopulateCache(val);
                } catch (Exception e) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleImplicitConnectionCache:loadBalanceConnections"
                                                         + e, this);

                        OracleLog.recursiveTrace = false;
                    }
                }
            }
        }
    }

    private int getNumberOfDefaultUserConnections() {
        int defaultUserConnections = 0;

        if ((this.userMap != null) && (!this.userMap.isEmpty())) {
            OracleConnectionCacheEntry occe = (OracleConnectionCacheEntry) this.userMap
                    .get(this.defaultUser + this.defaultPassword.toUpperCase());

            if ((occe != null) && (occe.userConnList != null) && (!occe.userConnList.isEmpty())) {
                defaultUserConnections = occe.userConnList.size();
            }
        }

        return defaultUserConnections;
    }

    synchronized void markDownLostConnections(boolean serviceDownEvent, boolean hostDownEvent,
            String instNameKey, String dbUniqNameKey, String hostNameKey, String status) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.markDownLostConnections(serviceDownEvent="
                                             + serviceDownEvent + "hostDownEvent=" + hostDownEvent
                                             + ",instName=" + instNameKey + ",dbUniqName="
                                             + dbUniqNameKey + ",hostName=" + hostNameKey
                                             + ",status=" + status + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.isEntireServiceDownProcessed) {
            if ((this.userMap != null) && (!this.userMap.isEmpty())) {
                Iterator i = this.userMap.entrySet().iterator();

                while (i.hasNext()) {
                    boolean isDefaultUser = false;

                    Map.Entry e = (Map.Entry) i.next();

                    if ((this.defaultUser != null)
                            && (this.defaultUser.equalsIgnoreCase((String) e.getKey()))) {
                        isDefaultUser = true;
                    }
                    OracleConnectionCacheEntry occe = (OracleConnectionCacheEntry) e.getValue();

                    if ((occe != null) && (occe.userConnList != null)
                            && (!occe.userConnList.isEmpty())) {
                        boolean markedDown = false;
                        Iterator itr = occe.userConnList.iterator();

                        while (itr.hasNext()) {
                            OraclePooledConnection pc = (OraclePooledConnection) itr.next();

                            if (serviceDownEvent) {
                                markedDown = markDownConnectionsForServiceEvent(instNameKey,
                                                                                dbUniqNameKey, pc);
                            } else if (hostDownEvent) {
                                markedDown = markDownConnectionsForHostEvent(hostNameKey, pc);
                            }

                            if ((markedDown) && (isDefaultUser)) {
                                this.defaultUserPreFailureSize += 1;
                            }
                        }
                    }

                    if ((occe != null) && (occe.attrConnMap != null)
                            && (!occe.attrConnMap.isEmpty())) {
                        Iterator j = occe.attrConnMap.entrySet().iterator();

                        while (j.hasNext()) {
                            Map.Entry me = (Map.Entry) j.next();
                            Iterator itr = ((Vector) me.getValue()).iterator();

                            while (itr.hasNext()) {
                                OraclePooledConnection pc = (OraclePooledConnection) itr.next();

                                if (serviceDownEvent) {
                                    markDownConnectionsForServiceEvent(instNameKey, dbUniqNameKey,
                                                                       pc);
                                } else if (hostDownEvent) {
                                    markDownConnectionsForHostEvent(hostNameKey, pc);
                                }
                            }
                        }
                    }
                }
            }
            if (instNameKey == null)
                this.isEntireServiceDownProcessed = true;
        }
    }

    private boolean markDownConnectionsForServiceEvent(String instanceNameKey,
            String dbUniqNameKey, OraclePooledConnection pc) {
        boolean markedDown = false;

        if ((instanceNameKey == null)
                || ((dbUniqNameKey == pc.dataSourceDbUniqNameKey) && (instanceNameKey == pc.dataSourceInstanceNameKey))) {
            pc.connectionMarkedDown = true;
            markedDown = true;
        }

        return markedDown;
    }

    private boolean markDownConnectionsForHostEvent(String hostNameKey, OraclePooledConnection pc) {
        boolean markedDown = false;

        if (hostNameKey == pc.dataSourceHostNameKey) {
            pc.connectionMarkedDown = true;
            pc.isHostDown = true;
            markedDown = true;
        }

        return markedDown;
    }

    synchronized void cleanupFailoverConnections(boolean serviceDownEvent, boolean hostDownEvent,
            String instNameKey, String dbUniqNameKey, String hostNameKey, String status) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.cleanupFailoverConnections(serviceDownEvent="
                                             + serviceDownEvent + "hostDownEvent=" + hostDownEvent
                                             + ",instName=" + instNameKey + ",dbUniqName="
                                             + dbUniqNameKey + ",hostName=" + hostNameKey
                                             + ",status=" + status + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OraclePooledConnection pc = null;
        Object[] objArr = this.checkedOutConnectionList.toArray();
        int sz = this.checkedOutConnectionList.size();

        OraclePooledConnection[] abortedConnections = new OraclePooledConnection[sz];
        int abortedConnectionsCount = 0;

        for (int k = 0; k < sz; k++) {
            try {
                pc = (OraclePooledConnection) objArr[k];

                if (((serviceDownEvent)
                        && ((instNameKey == null) || (instNameKey == pc.dataSourceInstanceNameKey)) && (dbUniqNameKey == pc.dataSourceDbUniqNameKey))
                        || ((hostDownEvent) && (hostNameKey == pc.dataSourceHostNameKey))) {
                    if ((this.defaultUser != null)
                            && (this.defaultUser.equalsIgnoreCase(pc.pcUser))
                            && (pc.cachedConnectionAttributes != null)
                            && (pc.cachedConnectionAttributes.isEmpty())) {
                        this.defaultUserPreFailureSize += 1;
                    }

                    this.checkedOutConnectionList.removeElement(pc);

                    abortConnection(pc);
                    pc.isHostDown = true;

                    abortedConnections[(abortedConnectionsCount++)] = pc;
                }

            } catch (Exception e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINE,
                                             "OracleImplicitConnectionCache:cleanupFailoverConnections"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

        }

        for (int k = 0; k < abortedConnectionsCount; k++) {
            try {
                closeCheckedOutConnection(abortedConnections[k], false);
            } catch (SQLException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINE,
                                               "OracleImplicitConnectionCache:cleanupFailoverConnections"
                                                       + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

        }

        if ((this.checkedOutConnectionList.size() < sz) && (this.cacheConnectionWaitTimeout > 0)) {
            notifyAll();
        }

        try {
            doForEveryCachedConnection(2);
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCache.cleanupFailoverConnections()"
                                                 + e, this);

                OracleLog.recursiveTrace = false;
            }

        }

        if ((this.databaseInstancesList != null) && ((sz = this.databaseInstancesList.size()) > 0)) {
            synchronized (this.databaseInstancesList) {
                OracleDatabaseInstance dbInstance = null;
                objArr = this.databaseInstancesList.toArray();

                for (int k = 0; k < sz; k++) {
                    dbInstance = (OracleDatabaseInstance) objArr[k];

                    if ((dbInstance.databaseUniqName != dbUniqNameKey)
                            || (dbInstance.instanceName != instNameKey)) {
                        continue;
                    }
                    if (dbInstance.flag <= 3)
                        this.dbInstancePercentTotal -= dbInstance.percent;
                    this.databaseInstancesList.remove(dbInstance);
                }
            }
        }
    }

    void zapRLBInfo() {
        this.databaseInstancesList.clear();
    }

    protected synchronized void closeAndRemovePooledConnection(OraclePooledConnection pc)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.closeAndRemovePooledConnection(pc="
                                             + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pc != null) {
            if (pc.isHostDown) {
                abortConnection(pc);
            }
            actualPooledConnectionClose(pc);
            removeCacheConnection(pc);
        }
    }

    private void abortConnection(OraclePooledConnection pc) {
        try {
            ((OracleConnection) pc.getPhysicalHandle()).abort();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER, "abortConnection(): " + pc, this);

                OracleLog.recursiveTrace = false;
            }

        } catch (Exception ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCache.abortConnection()" + ex,
                                         this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    private void actualPooledConnectionClose(OraclePooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.actualPooledConnectionClose(pc="
                                             + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int sz = 0;
        if ((this.databaseInstancesList != null) && ((sz = this.databaseInstancesList.size()) > 0)) {
            synchronized (this.databaseInstancesList) {
                OracleDatabaseInstance dbInstance = null;

                for (int i = 0; i < sz; i++) {
                    dbInstance = (OracleDatabaseInstance) this.databaseInstancesList.get(i);
                    if ((dbInstance.databaseUniqName != pc.dataSourceDbUniqNameKey)
                            || (dbInstance.instanceName != pc.dataSourceInstanceNameKey)) {
                        continue;
                    }
                    if (dbInstance.numberOfConnectionsCount <= 0)
                        break;
                    dbInstance.numberOfConnectionsCount -= 1;
                    break;
                }

            }

        }

        try {
            pc.close();
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCache.actualPooledConnectionClose()"
                                                 + e, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    protected int getCacheTimeToLiveTimeout() {
        return this.cacheTimeToLiveTimeout;
    }

    protected int getCacheInactivityTimeout() {
        return this.cacheInactivityTimeout;
    }

    protected int getCachePropertyCheckInterval() {
        return this.cachePropertyCheckInterval;
    }

    protected int getCacheAbandonedTimeout() {
        return this.cacheAbandonedConnectionTimeout;
    }

    private synchronized void processConnectionCacheCallback() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "OracleImplicitConnectionCache.processConnectionCacheCallback()", this);

            OracleLog.recursiveTrace = false;
        }

        float cachePct = this.cacheMaxLimit / 100.0F;
        int expectedCacheSize = (int) (this.cacheLowerThresholdLimit * cachePct);

        releaseBasedOnPriority(1024, expectedCacheSize);

        if (this.cacheSize < expectedCacheSize)
            releaseBasedOnPriority(512, expectedCacheSize);
    }

    private void releaseBasedOnPriority(int priority, int expCacheSz) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.releaseBasedOnPriority(priority="
                                             + priority + ", expCacheSz = " + expCacheSz + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        Object[] objArr = this.checkedOutConnectionList.toArray();

        for (int i = 0; (i < objArr.length) && (this.cacheSize < expCacheSz); i++) {
            OraclePooledConnection pc = (OraclePooledConnection) objArr[i];
            OracleConnection conn = null;

            if (pc != null) {
                conn = (OracleConnection) pc.getLogicalHandle();
            }
            if (conn == null)
                continue;
            OracleConnectionCacheCallback occc = conn.getConnectionCacheCallbackObj();

            if ((occc == null)
                    || ((conn.getConnectionCacheCallbackFlag() != 2) && (conn
                            .getConnectionCacheCallbackFlag() != 4))) {
                continue;
            }

            if (priority != conn.getConnectionReleasePriority())
                continue;
            Object userObj = conn.getConnectionCacheCallbackPrivObj();
            occc.releaseConnection(conn, userObj);
        }
    }

    private synchronized void processConnectionWaitTimeout(long waitTimeout) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.processConnectionWaitTimeout("
                                             + waitTimeout + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            wait(waitTimeout);
        } catch (InterruptedException ea) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE,
                                         "OracleImplicitConnectionCache.processConnectionWaitTimeoutGot an InterruptedException"
                                                 + ea, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    private void processInactivityTimeout(OraclePooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.processInactivityTimeout(pc="
                                             + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        long lastAccessedTime = pc.getLastAccessedTime();
        long currentTime = System.currentTimeMillis();

        if ((getTotalCachedConnections() > this.cacheMinLimit)
                && (currentTime - lastAccessedTime > this.cacheInactivityTimeout * 1000)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCache.processInactivityTimeout():PooledConnection in cache closed:"
                                                 + pc, this);

                OracleLog.recursiveTrace = false;
            }

            closeAndRemovePooledConnection(pc);
        }
    }

    private void cleanupTimeoutThread() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleImplicitConnectionCache.cleanupTimeoutThread()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.timeoutThread != null) {
            this.timeoutThread.timeToLive = false;

            if (this.timeoutThread.isSleeping) {
                this.timeoutThread.interrupt();
            }
            this.timeoutThread = null;
        }
    }

    protected void purgeCacheConnections(boolean cleanupCheckedOutConnections) {
        try {
            if (cleanupCheckedOutConnections) {
                doForEveryCheckedOutConnection(1);
            }

            doForEveryCachedConnection(1);
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCache.purgeCacheConnections()"
                                                 + e, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    protected void updateDatabaseInstance(String dbUniqNameKey, String instNameKey, int percent,
            int flag) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "(RLB)OracleImplicitConnectionCache.updateDatabaseInstance(), DBUniqKey="
                                             + dbUniqNameKey + ", InstanceNameKey=" + instNameKey
                                             + ", Percent=" + percent + ", flag=" + flag, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.databaseInstancesList == null) {
            this.databaseInstancesList = new LinkedList();
        }
        synchronized (this.databaseInstancesList) {
            int numInstances = this.databaseInstancesList.size();
            boolean updated = false;

            for (int i = 0; i < numInstances; i++) {
                OracleDatabaseInstance dbInstance = (OracleDatabaseInstance) this.databaseInstancesList
                        .get(i);
                if ((dbInstance.databaseUniqName != dbUniqNameKey)
                        || (dbInstance.instanceName != instNameKey)) {
                    continue;
                }
                dbInstance.percent = percent;
                dbInstance.flag = flag;
                updated = true;
                break;
            }

            if (!updated) {
                OracleDatabaseInstance tmpInstance = new OracleDatabaseInstance(dbUniqNameKey,
                        instNameKey);

                tmpInstance.percent = percent;
                tmpInstance.flag = flag;

                this.databaseInstancesList.add(tmpInstance);
            }
        }
    }

    protected void processDatabaseInstances() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "(RLB)OracleImplicitConnectionCache.processDatabaseInstances()", this);

            OracleLog.recursiveTrace = false;
        }

        OracleDatabaseInstance dbInstance = null;
        LinkedList retireList = new LinkedList();

        if (this.databaseInstancesList != null) {
            synchronized (this.databaseInstancesList) {
                int goodGroupSum = 0;
                boolean resetDBInstanceValues = false;

                this.useGoodGroup = false;

                int numInstances = this.databaseInstancesList.size();

                for (int i = 0; i < numInstances; i++) {
                    dbInstance = (OracleDatabaseInstance) this.databaseInstancesList.get(i);

                    if (dbInstance.flag <= 3) {
                        goodGroupSum += dbInstance.percent;
                    }

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger
                                .log(Level.FINE,
                                     "(RLB) OracleImplicitConnectionCache.processDatabaseInstances: <<< ServiceName="
                                             + this.dataSourceServiceName + ", Connections to "
                                             + dbInstance.instanceName + " = "
                                             + dbInstance.numberOfConnectionsCount
                                             + ", Attempted Connection Requests to this instance="
                                             + dbInstance.attemptedConnRequestCount
                                             + ", Total Connections=" + getTotalCachedConnections()
                                             + " >>>", this);

                        OracleLog.recursiveTrace = false;
                    }

                }

                if (goodGroupSum > 0) {
                    this.dbInstancePercentTotal = goodGroupSum;
                    this.useGoodGroup = true;
                }

                if (numInstances > 1) {
                    for (int i = 0; i < numInstances; i++) {
                        dbInstance = (OracleDatabaseInstance) this.databaseInstancesList.get(i);
                        this.countTotal += dbInstance.attemptedConnRequestCount;
                    }

                    if (this.countTotal > numInstances * 1000) {
                        for (int i = 0; i < numInstances; i++) {
                            dbInstance = (OracleDatabaseInstance) this.databaseInstancesList.get(i);

                            float ACRRatio = dbInstance.attemptedConnRequestCount / this.countTotal;

                            float connRatio = dbInstance.numberOfConnectionsCount
                                    / getTotalCachedConnections();

                            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.poolLogger
                                        .log(
                                             Level.FINER,
                                             "(RLB)OracleImplicitConnectionCache.processDatabaseInstances()\nTotal ACR count since last gravitation="
                                                     + this.countTotal
                                                     + "\nNumberofInstances="
                                                     + numInstances
                                                     + "\nInstanceName="
                                                     + dbInstance.instanceName
                                                     + "\nAttemptedConnectionRequestRatio="
                                                     + ACRRatio
                                                     + "\nConnectionRequestRatio="
                                                     + connRatio, this);

                                OracleLog.recursiveTrace = false;
                            }

                            if (connRatio <= ACRRatio * 2.0F) {
                                continue;
                            }

                            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.poolLogger
                                        .log(
                                             Level.FINER,
                                             "(RLB)OracleImplicitConnectionCache.processDatabaseInstances:For Instance being retired::\nInstanceName="
                                                     + dbInstance.instanceName
                                                     + "\nAttemptedConnectionRequestRatio="
                                                     + ACRRatio
                                                     + "\nConnectionRequestRatio="
                                                     + connRatio, this);

                                OracleLog.recursiveTrace = false;
                            }

                            if ((int) (dbInstance.numberOfConnectionsCount * 0.25D) >= 1) {
                                retireList.add(dbInstance);
                            }
                            resetDBInstanceValues = true;
                        }

                        if (resetDBInstanceValues) {
                            for (int i = 0; i < numInstances; i++) {
                                dbInstance = (OracleDatabaseInstance) this.databaseInstancesList
                                        .get(i);
                                dbInstance.attemptedConnRequestCount = 0;
                            }
                            resetDBInstanceValues = false;
                        }

                    }

                }

            }

            if (retireList.size() > 0) {
                this.instancesToRetireList = retireList;

                if (this.gravitateCacheThread != null) {
                    try {
                        this.gravitateCacheThread.interrupt();
                        this.gravitateCacheThread.join();
                    } catch (InterruptedException ie) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "(RLB)OracleImplicitConnectionCache.processDatabaseInstances()"
                                                             + ie, this);

                            OracleLog.recursiveTrace = false;
                        }

                    }

                    this.gravitateCacheThread = null;
                }

                this.gravitateCacheThread = new OracleGravitateConnectionCacheThread(this);

                this.cacheManager.checkAndStartThread(this.gravitateCacheThread);
            }
        }
    }

    protected void gravitateCache() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "(RLB)OracleImplicitConnectionCache.gravitateCache()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.instancesToRetireList != null) {
            synchronized (this.instancesToRetireList) {
                int numInstances = this.instancesToRetireList.size();

                for (int i = 0; i < numInstances; i++) {
                    this.instanceToRetire = ((OracleDatabaseInstance) this.instancesToRetireList
                            .get(i));
                    this.retireConnectionsCount = (int) (this.instanceToRetire.numberOfConnectionsCount * 0.25D);

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "(RLB)OracleImplicitConnectionCache.gravitateCache , Available conns to retire from: "
                                                         + this.instanceToRetire.instanceName
                                                         + " = " + this.retireConnectionsCount,
                                                 this);

                        OracleLog.recursiveTrace = false;
                    }

                    try {
                        doForEveryCachedConnection(24);
                    } catch (SQLException e) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "(RLB)OracleImplicitConnectionCache.gravitateCache()"
                                                             + e, this);

                            OracleLog.recursiveTrace = false;
                        }

                    }

                    if (this.retireConnectionsCount <= 0) {
                        continue;
                    }

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "(RLB)OracleImplicitConnectionCache.gravitateCache , checkedout conns to retire from: "
                                                         + this.instanceToRetire.instanceName
                                                         + " = " + this.retireConnectionsCount,
                                                 this);

                        OracleLog.recursiveTrace = false;
                    }

                    try {
                        doForEveryCheckedOutConnection(24);
                    } catch (SQLException e) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "(RLB)OracleImplicitConnectionCache.gravitateCache()"
                                                             + e, this);

                            OracleLog.recursiveTrace = false;
                        }
                    }

                }

            }

        }

        this.retireConnectionsCount = 0;
        this.instanceToRetire = null;
        this.instancesToRetireList = null;
        this.countTotal = 0;
    }

    protected void cleanupRLBThreads() {
        if (this.gravitateCacheThread != null) {
            try {
                this.gravitateCacheThread.interrupt();
                this.gravitateCacheThread.join();
            } catch (InterruptedException ie) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "(RLB)OracleImplicitConnectionCache.cleanupRLBThreads"
                                                     + ie, this);

                    OracleLog.recursiveTrace = false;
                }

            }

            this.gravitateCacheThread = null;
        }

        if (this.runtimeLoadBalancingThread != null) {
            try {
                this.runtimeLoadBalancingThread.interrupt();
            } catch (Exception e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "(RLB)OracleImplicitConnectionCache.cleanupRLBThreads"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

            this.runtimeLoadBalancingThread = null;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleImplicitConnectionCache"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleImplicitConnectionCache JD-Core Version: 0.6.0
 */