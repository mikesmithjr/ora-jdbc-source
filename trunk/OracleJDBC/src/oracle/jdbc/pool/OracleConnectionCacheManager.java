package oracle.jdbc.pool;

import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.ons.ONS;
import oracle.ons.ONSException;

public class OracleConnectionCacheManager {
    private static OracleConnectionCacheManager cacheManagerInstance = null;

    protected Hashtable m_connCache = null;
    public static final int REFRESH_INVALID_CONNECTIONS = 4096;
    public static final int REFRESH_ALL_CONNECTIONS = 8192;
    protected static final int FAILOVER_EVENT_TYPE_SERVICE = 256;
    protected static final int FAILOVER_EVENT_TYPE_HOST = 512;
    protected static final String EVENT_DELIMITER = "{} =";
    protected OracleFailoverEventHandlerThread failoverEventHandlerThread = null;

    private static boolean isONSInitializedForRemoteSubscription = false;
    static final int ORAERROR_END_OF_FILE_ON_COM_CHANNEL = 3113;
    static final int ORAERROR_NOT_CONNECTED_TO_ORACLE = 3114;
    static final int ORAERROR_INIT_SHUTDOWN_IN_PROGRESS = 1033;
    static final int ORAERROR_ORACLE_NOT_AVAILABLE = 1034;
    static final int ORAERROR_IMMEDIATE_SHUTDOWN_IN_PROGRESS = 1089;
    static final int ORAERROR_SHUTDOWN_IN_PROGRESS_NO_CONN = 1090;
    static final int ORAERROR_NET_IO_EXCEPTION = 17002;
    protected int[] fatalErrorCodes = null;
    protected int failoverEnabledCacheCount = 0;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    private OracleConnectionCacheManager() {
        this.m_connCache = new Hashtable();
    }

    public static synchronized OracleConnectionCacheManager getConnectionCacheManagerInstance()
            throws SQLException {
        try {
            if (cacheManagerInstance == null) {
                cacheManagerInstance = new OracleConnectionCacheManager();
            }

        } catch (RuntimeException re) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleConnectionCacheManager.getConnectionCacheManagerInstanceRuntimeException"
                                                 + re);

                OracleLog.recursiveTrace = false;
            }

        }

        return cacheManagerInstance;
    }

    public String createCache(OracleDataSource ods, Properties cacheProperties) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheManager.createCache(ods="
                    + ods + "cacheProperties=" + cacheProperties + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String connectionCacheName = null;

        if ((ods == null) || (!ods.getConnectionCachingEnabled())) {
            DatabaseError.throwSqlException(137);
        }

        if (ods.connCacheName != null) {
            connectionCacheName = ods.connCacheName;
        } else {
            connectionCacheName = ods.dataSourceName + "#0x"
                    + Integer.toHexString(this.m_connCache.size());
        }

        createCache(connectionCacheName, ods, cacheProperties);

        return connectionCacheName;
    }

    public void createCache(String cacheName, OracleDataSource ods, Properties cacheProperties)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.createCache(cacheName="
                                             + cacheName + "ods=" + ods + "cacheProperties="
                                             + cacheProperties + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((ods == null) || (!ods.getConnectionCachingEnabled())) {
            DatabaseError.throwSqlException(137);
        }

        if (cacheName == null) {
            DatabaseError.throwSqlException(138);
        }

        if (this.m_connCache.containsKey(cacheName)) {
            DatabaseError.throwSqlException(140);
        }

        boolean _odsIsFcfEnabled = ods.getFastConnectionFailoverEnabled();

        if ((_odsIsFcfEnabled) && (this.failoverEventHandlerThread == null)) {
            final String onsConfigStr = ods.getONSConfiguration();

            if ((onsConfigStr != null) && (!onsConfigStr.equals(""))) {
                synchronized (this) {
                    if (!isONSInitializedForRemoteSubscription) {
                        try {
                            AccessController.doPrivileged(new PrivilegedExceptionAction(
                                    ) {

                                public Object run() throws ONSException {
                                    ONS ons = new ONS(onsConfigStr);
                                    return null;
                                }
                            });
                        } catch (PrivilegedActionException onsexc) {
                            DatabaseError.throwSqlException(175, onsexc);
                        }

                        isONSInitializedForRemoteSubscription = true;
                    }
                }
            }

            this.failoverEventHandlerThread = new OracleFailoverEventHandlerThread();
        }

        OracleImplicitConnectionCache icc = new OracleImplicitConnectionCache(ods, cacheProperties);

        icc.cacheName = cacheName;
        ods.odsCache = icc;

        this.m_connCache.put(cacheName, icc);

        if (_odsIsFcfEnabled) {
            checkAndStartThread(this.failoverEventHandlerThread);
        }
    }

    public void removeCache(String cacheName, long waitTimeout) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleConnectionCacheManager.removeCache(cacheName="
                            + cacheName + "waitTimeout=" + waitTimeout + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .remove(cacheName)
                : null;

        if (oicc != null) {
            oicc.disableConnectionCache();

            if (waitTimeout > 0L) {
                try {
                    Thread.currentThread();
                    Thread.sleep(waitTimeout * 1000L);
                } catch (InterruptedException ea) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleConnectionCacheManager.removeCacheGot an InterruptedException"
                                                         + ea, this);

                        OracleLog.recursiveTrace = false;
                    }

                }

            }

            if (oicc.cacheEnabledDS.getFastConnectionFailoverEnabled()) {
                cleanupFCFThreads(oicc);
            }

            oicc.closeConnectionCache();

            oicc = null;
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    public void reinitializeCache(String cacheName, Properties cacheProperties) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.reinitializeCache(cacheName="
                                             + cacheName + "cacheProperties=" + cacheProperties
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            disableCache(cacheName);
            oicc.reinitializeCacheConnections(cacheProperties);
            enableCache(cacheName);
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    public boolean existsCache(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.existsCache(cacheName="
                                             + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_connCache.containsKey(cacheName);
    }

    public void enableCache(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.enabledCache(cacheName="
                                             + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            oicc.enableConnectionCache();
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    public void disableCache(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.disableCache(cacheName="
                                             + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            oicc.disableConnectionCache();
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    public void refreshCache(String cacheName, int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.refreshCache(cacheName="
                                             + cacheName + "mode=" + mode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            if ((mode == 4096) || (mode == 8192)) {
                oicc.refreshCacheConnections(mode);
            } else
                DatabaseError.throwSqlException(68);
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    public void purgeCache(String cacheName, boolean cleanupCheckedOutConnections)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.purgeCache(cacheName="
                                             + cacheName + "cleanupCheckedOutConnections="
                                             + cleanupCheckedOutConnections + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            oicc.purgeCacheConnections(cleanupCheckedOutConnections);
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    public Properties getCacheProperties(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.getCacheProperties(cacheName="
                                             + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            return oicc.getConnectionCacheProperties();
        }

        DatabaseError.throwSqlException(141);

        return null;
    }

    public String[] getCacheNameList() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheManager.getCacheNameList()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return (String[]) this.m_connCache.keySet().toArray(new String[this.m_connCache.size()]);
    }

    public int getNumberOfAvailableConnections(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.getNumberOfAvailableConnections(cacheName="
                                             + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            return oicc.cacheSize;
        }

        DatabaseError.throwSqlException(141);

        return 0;
    }

    public int getNumberOfActiveConnections(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.getNumberOfActiveConnections(cacheName="
                                             + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            return oicc.getNumberOfCheckedOutConnections();
        }

        DatabaseError.throwSqlException(141);

        return 0;
    }

    public synchronized void setConnectionPoolDataSource(String cacheName,
            ConnectionPoolDataSource ds) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.setConnectionPoolDataSource(cacheName="
                                             + cacheName + ", DataSource=" + ds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache oicc = cacheName != null ? (OracleImplicitConnectionCache) this.m_connCache
                .get(cacheName)
                : null;

        if (oicc != null) {
            if (oicc.cacheSize > 0) {
                DatabaseError.throwSqlException(78);
            } else {
                ((OracleConnectionPoolDataSource) ds).makeURL();
                ((OracleConnectionPoolDataSource) ds)
                        .setURL(((OracleConnectionPoolDataSource) ds).url);

                oicc.connectionPoolDS = ((OracleConnectionPoolDataSource) ds);
            }
        } else {
            DatabaseError.throwSqlException(141);
        }
    }

    protected void verifyAndHandleEvent(int eventType, byte[] eventBody) throws SQLException {
        String svcName = null;
        String instNameKey = null;
        String dbUniqNameKey = null;
        String hostNameKey = null;
        String status = null;

        int cardinality = 0;
        StringTokenizer strTokens = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "eventType=" + eventType + "eventBody=<"
                    + new String(eventBody) + ">", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            strTokens = new StringTokenizer(new String(eventBody, "UTF-8"), "{} =", true);
        } catch (UnsupportedEncodingException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER, "UTF-8 Encoding Exception " + e, this);

                OracleLog.recursiveTrace = false;
            }

        }

        String st = null;
        String val = null;
        String key = null;

        while (strTokens.hasMoreTokens()) {
            val = null;
            st = strTokens.nextToken();
            if ((st.equals("=")) && (strTokens.hasMoreTokens())) {
                val = strTokens.nextToken();
            } else {
                key = st;
            }

            if ((key.equalsIgnoreCase("version")) && (val != null) && (!val.equals("1.0"))) {
                DatabaseError.throwSqlException(146);
            }

            if ((key.equalsIgnoreCase("service")) && (val != null)) {
                svcName = val;
            }
            if ((key.equalsIgnoreCase("instance")) && (val != null) && (!val.equals(" "))) {
                instNameKey = val.intern();
            }

            if ((key.equalsIgnoreCase("database")) && (val != null)) {
                dbUniqNameKey = val.intern();
            }
            if ((key.equalsIgnoreCase("host")) && (val != null)) {
                hostNameKey = val.intern();
            }
            if ((key.equalsIgnoreCase("status")) && (val != null)) {
                status = val;
            }
            if ((!key.equalsIgnoreCase("card")) || (val == null))
                continue;
            try {
                cardinality = Integer.parseInt(val);
            } catch (NumberFormatException nfe) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER, "Cardinality value= " + val + nfe, this);

                    OracleLog.recursiveTrace = false;
                }

            }

        }

        invokeFailoverProcessingThreads(eventType, svcName, instNameKey, dbUniqNameKey,
                                        hostNameKey, status, cardinality);

        strTokens = null;
    }

    private void invokeFailoverProcessingThreads(int eventType, String svcName, String instNameKey,
            String dbUniqNameKey, String hostNameKey, String status, int card) throws SQLException {
        OracleImplicitConnectionCache oicc = null;
        boolean serviceEvent = false;
        boolean hostEvent = false;

        if (eventType == 256) {
            serviceEvent = true;
        }
        if (eventType == 512) {
            hostEvent = true;
        }
        Iterator itr = this.m_connCache.values().iterator();

        while (itr.hasNext()) {
            oicc = (OracleImplicitConnectionCache) itr.next();

            if (((!serviceEvent) || (!svcName.equalsIgnoreCase(oicc.dataSourceServiceName)))
                    && (!hostEvent)) {
                continue;
            }
            OracleFailoverWorkerThread workerTh = new OracleFailoverWorkerThread(oicc, eventType,
                    instNameKey, dbUniqNameKey, hostNameKey, status, card);

            checkAndStartThread(workerTh);

            oicc.failoverWorkerThread = workerTh;
        }
    }

    protected void checkAndStartThread(Thread thr) throws SQLException {
        try {
            if (!thr.isAlive()) {
                thr.setDaemon(true);
                thr.start();
            }

        } catch (IllegalThreadStateException ie) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleConnectionCacheManager.checkAndStartThread()" + ie,
                                         this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    protected boolean failoverEnabledCacheExists() {
        return this.failoverEnabledCacheCount > 0;
    }

    protected void parseRuntimeLoadBalancingEvent(String service, byte[] eventBody)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, new String(eventBody) + ">", this);

            OracleLog.recursiveTrace = false;
        }

        OracleImplicitConnectionCache icc = null;
        Enumeration iccSet = this.m_connCache.elements();

        while (iccSet.hasMoreElements()) {
            try {
                icc = (OracleImplicitConnectionCache) iccSet.nextElement();
                if (service.equalsIgnoreCase(icc.dataSourceServiceName)) {
                    if (eventBody == null)
                        icc.zapRLBInfo();
                    else {
                        retrieveServiceMetrics(icc, eventBody);
                    }

                }

            } catch (Exception e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "(RLB)OracleConnectionCacheManager.parseRuntimeLoadBalancingEvent()"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }
            }
        }
    }

    private void retrieveServiceMetrics(OracleImplicitConnectionCache icc, byte[] eventBody)
            throws SQLException {
        StringTokenizer strTokens = null;
        String instNameKey = null;
        String dbUniqNameKey = null;
        int percent = 0;
        int flag = 0;
        boolean updateNecessary = false;
        try {
            strTokens = new StringTokenizer(new String(eventBody, "UTF-8"), "{} =", true);
        } catch (UnsupportedEncodingException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER, "(RLB)UTF-8 Encoding Exception " + e, this);

                OracleLog.recursiveTrace = false;
            }

        }

        String st = null;
        String val = null;
        String key = null;

        while (strTokens.hasMoreTokens()) {
            val = null;
            st = strTokens.nextToken();

            if ((st.equals("=")) && (strTokens.hasMoreTokens())) {
                val = strTokens.nextToken();
            } else {
                if (st.equals("}")) {
                    if (!updateNecessary)
                        continue;
                    icc.updateDatabaseInstance(dbUniqNameKey, instNameKey, percent, flag);
                    updateNecessary = false;
                    continue;
                }

                if ((st.equals("{")) || (st.equals(" "))) {
                    continue;
                }

                key = st;
                updateNecessary = true;
            }

            if ((key.equalsIgnoreCase("version")) && (val != null)) {
                if (!val.equals("1.0")) {
                    DatabaseError.throwSqlException(146);
                }
            }

            if ((key.equalsIgnoreCase("database")) && (val != null)) {
                dbUniqNameKey = val.intern();
            }
            if ((key.equalsIgnoreCase("instance")) && (val != null)) {
                instNameKey = val.intern();
            }
            if ((key.equalsIgnoreCase("percent")) && (val != null)) {
                try {
                    percent = Integer.parseInt(val);
                    if (percent == 0)
                        percent = 1;

                } catch (NumberFormatException nfe) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER, "(RLB)percent value= " + val + nfe,
                                                 this);

                        OracleLog.recursiveTrace = false;
                    }
                }

            }

            if ((!key.equalsIgnoreCase("flag")) || (val == null))
                continue;
            if (val.equalsIgnoreCase("good")) {
                flag = 1;
                continue;
            }
            if (val.equalsIgnoreCase("violating")) {
                flag = 3;
                continue;
            }
            if (val.equalsIgnoreCase("NO_DATA")) {
                flag = 4;
                continue;
            }
            if (val.equalsIgnoreCase("UNKNOWN")) {
                flag = 2;
                continue;
            }
            if (val.equalsIgnoreCase("BLOCKED")) {
                flag = 5;
            }

        }

        icc.processDatabaseInstances();
    }

    protected void cleanupFCFThreads(OracleImplicitConnectionCache oicc) throws SQLException {
        cleanupFCFWorkerThread(oicc);
        oicc.cleanupRLBThreads();

        if (this.failoverEnabledCacheCount <= 0) {
            cleanupFCFEventHandlerThread();
        }

        this.failoverEnabledCacheCount -= 1;
    }

    protected void cleanupFCFWorkerThread(OracleImplicitConnectionCache oicc) throws SQLException {
        if (oicc.failoverWorkerThread != null) {
            try {
                if (oicc.failoverWorkerThread.isAlive()) {
                    oicc.failoverWorkerThread.join();
                }

            } catch (InterruptedException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleConnectionCacheManager.cleanupFCFWorkerThread()"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

            oicc.failoverWorkerThread = null;
        }
    }

    protected void cleanupFCFEventHandlerThread() throws SQLException {
        if (this.failoverEventHandlerThread != null) {
            try {
                this.failoverEventHandlerThread.interrupt();
            } catch (Exception e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleConnectionCacheManager.cleanupFCFEventHandlerThreadException"
                                                     + e);

                    OracleLog.recursiveTrace = false;
                }

            }

            this.failoverEventHandlerThread = null;
        }
    }

    public boolean isFatalConnectionError(SQLException se) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.isFatalConnectionError()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean retCode = false;
        int errCode = se.getErrorCode();

        if ((errCode == 3113) || (errCode == 3114) || (errCode == 1033) || (errCode == 1034)
                || (errCode == 1089) || (errCode == 1090) || (errCode == 17002)) {
            retCode = true;
        }

        if ((!retCode) && (this.fatalErrorCodes != null)) {
            for (int i = 0; i < this.fatalErrorCodes.length; i++) {
                if (errCode != this.fatalErrorCodes[i])
                    continue;
                retCode = true;
                break;
            }
        }

        return retCode;
    }

    public synchronized void setConnectionErrorCodes(int[] fatalErrorCodes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheManager.setConnectionErrorCodes("
                                             + fatalErrorCodes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (fatalErrorCodes != null)
            this.fatalErrorCodes = fatalErrorCodes;
    }

    public int[] getConnectionErrorCodes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleConnectionCacheManager.getConnectionErrorCodes()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.fatalErrorCodes;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleConnectionCacheManager"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleConnectionCacheManager JD-Core Version: 0.6.0
 */