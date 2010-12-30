package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

/** @deprecated */
public class OracleConnectionCacheImpl extends OracleDataSource implements OracleConnectionCache,
        Serializable, Referenceable {
    protected ConnectionPoolDataSource cpds = null;

    protected static int _DEFAULT_MIN_LIMIT = 0;

    protected static int _DEFAULT_MAX_LIMIT = 2147483647;

    protected int _MIN_LIMIT = _DEFAULT_MIN_LIMIT;
    protected int _MAX_LIMIT = _DEFAULT_MAX_LIMIT;
    protected static final int DEFAULT_CACHE_TIMEOUT = -1;
    protected static final int DEFAULT_THREAD_INTERVAL = 900;
    public static final int ORAERROR_END_OF_FILE_ON_COCHANNEL = 3113;
    public static final int ORAERROR_NOT_CONNECTED_TO_ORACLE = 3114;
    public static final int ORAERROR_INIT_SHUTDOWN_IN_PROGRESS = 1033;
    public static final int ORAERROR_ORACLE_NOT_AVAILABLE = 1034;
    public static final int ORAERROR_IMMEDIATE_SHUTDOWN_IN_PROGRESS = 1089;
    public static final int ORAERROR_SHUTDOWN_IN_PROGRESS_NO_CONN = 1090;
    public static final int ORAERROR_NET_IO_EXCEPTION = 17002;
    protected long cacheTTLTimeOut = -1L;
    protected long cacheInactivityTimeOut = -1L;
    protected long cacheFixedWaitTimeOut = -1L;
    protected long threadInterval = 900L;

    Stack cache = new Stack();
    Hashtable activeCache = new Hashtable(50);

    private Object CACHE_SIZE_LOCK = new String("");
    protected int cacheSize = 0;
    protected int activeSize = 0;
    protected int cacheScheme;
    protected long cleanupInterval = 30L;
    protected int[] fatalErrorCodes = null;
    public static final long DEFAULT_FIXED_WAIT_IDLE_TIME = 30L;
    protected long fixedWaitIdleTime = -1L;
    public static final int DYNAMIC_SCHEME = 1;
    public static final int FIXED_WAIT_SCHEME = 2;
    public static final int FIXED_RETURN_NULL_SCHEME = 3;
    protected OracleConnectionEventListener ocel = null;

    protected int stmtCacheSize = 0;
    protected boolean stmtClearMetaData = false;

    protected OracleConnectionCacheTimeOutThread timeOutThread = null;

    SQLWarning warning = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    /** @deprecated */
    public OracleConnectionCacheImpl() throws SQLException {
        this(null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.OracleConnectionCacheImpl()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    /** @deprecated */
    public OracleConnectionCacheImpl(ConnectionPoolDataSource ds) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.OracleConnectionCacheImpl(" + ds
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.cacheScheme = 1;

        this.cpds = ds;

        this.ocel = new OracleConnectionEventListener(this);

        this.dataSourceName = "OracleConnectionCacheImpl";
        this.isOracleDataSource = false;
    }

    /** @deprecated */
    public synchronized void setConnectionPoolDataSource(ConnectionPoolDataSource ds)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.setConnectionPoolDataSource(" + ds
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.cacheSize > 0) {
            DatabaseError.throwSqlException(78);
        }
        this.cpds = ds;
    }

    /** @deprecated */
    public Connection getConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.getConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        return getConnection(this.user, this.password);
    }

    /** @deprecated */
    public Connection getConnection(String user, String passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.getConnection(user="
                    + user + ", passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = null;

        PooledConnection pc = getPooledConnection(user, passwd);

        if (pc != null) {
            conn = pc.getConnection();
        }

        if (conn != null) {
            ((OracleConnection) conn).setStartTime(System.currentTimeMillis());
        }
        return conn;
    }

    protected PooledConnection getPooledConnection(String userName, String passwd)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getPooledConnection(user="
                                             + this.user + ", passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        PooledConnection pc = null;
        boolean createNewConnection = false;
        boolean tryFixedWaitScheme = false;
        long fixedWaitStartTime = 9223372036854775807L;
        Properties prop = null;

        synchronized (this) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleConnectionCacheImpl.getPooledConnection(user,passwd): -start cacheSize="
                                                 + this.cacheSize, this);

                OracleLog.recursiveTrace = false;
            }

            if (!this.cache.empty()) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleConnectionCacheImpl.getPooledConnection(user,passwd):  Active Count"
                                                     + this.activeSize, this);

                    OracleLog.recursiveTrace = false;
                }

                checkCredentials(userName, passwd);

                pc = removeConnectionFromCache();
            } else if ((this.cacheSize < this._MAX_LIMIT) || (this.cacheScheme == 1)) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleConnectionCacheImpl.getPooledConnection(user,passwd):  Getting a new one cacheSize "
                                                     + this.cacheSize + " activeSize "
                                                     + this.activeSize, this);

                    OracleLog.recursiveTrace = false;
                }

                String mc_user = null;
                String mc_passwd = null;

                if (this.cpds != null) {
                    mc_user = ((OracleConnectionPoolDataSource) this.cpds).getUser();
                    mc_passwd = ((OracleConnectionPoolDataSource) this.cpds).getPassword();
                }

                if ((this.cacheSize > 0) && (userName != null)
                        && (!userName.equalsIgnoreCase(mc_user))) {
                    DatabaseError.throwSqlException(79);
                }

                if ((this.cacheSize > 0) && (passwd != null)
                        && (!passwd.equalsIgnoreCase(mc_passwd))) {
                    DatabaseError.throwSqlException(79);
                }

                createNewConnection = true;
                prop = new Properties();
                if (this.url != null)
                    prop.setProperty("connection_url", this.url);
                if (this.user != null)
                    prop.setProperty("user", this.user);
                else if ((this.cpds != null) && (((OracleDataSource) this.cpds).user != null)) {
                    prop.setProperty("user", ((OracleDataSource) this.cpds).user);
                }
                if (this.password != null)
                    prop.setProperty("password", this.password);
                else if ((this.cpds != null) && (((OracleDataSource) this.cpds).password != null)) {
                    prop.setProperty("password", ((OracleDataSource) this.cpds).password);
                }
                if ((this.stmtCacheSize == 0) && (this.cpds != null)
                        && (((OracleDataSource) this.cpds).maxStatements != 0)) {
                    prop.setProperty("stmt_cache_size", ""
                            + ((OracleDataSource) this.cpds).maxStatements);
                } else {
                    prop.setProperty("stmt_cache_size", "" + this.stmtCacheSize);

                    prop.setProperty("stmt_cache_clear_metadata", "" + this.stmtClearMetaData);

                    prop.setProperty("ImplicitStatementCachingEnabled", ""
                            + this.implicitCachingEnabled);

                    prop.setProperty("ExplicitStatementCachingEnabled", ""
                            + this.explicitCachingEnabled);
                }

                if (this.loginTimeout != 0) {
                    prop.setProperty("LoginTimeout", "" + this.loginTimeout);
                }
                synchronized (this.CACHE_SIZE_LOCK) {
                    this.cacheSize += 1;
                }
                if (this.cpds == null) {
                    initializeConnectionPoolDataSource();
                }

            } else if (this.cacheScheme != 3) {
                checkCredentials(userName, passwd);

                fixedWaitStartTime = System.currentTimeMillis();
                tryFixedWaitScheme = true;
            }

        }

        if (createNewConnection) {
            try {
                pc = getNewPoolOrXAConnection(prop);
            } catch (SQLException sqlException) {
                synchronized (this.CACHE_SIZE_LOCK) {
                    this.cacheSize -= 1;
                }

                throw sqlException;
            }
        } else {
            if (tryFixedWaitScheme) {
                while ((pc == null) && (this.cache.empty())) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger
                                .log(
                                     Level.FINE,
                                     "OracleConnectionCacheImpl.getPooledConnection(user, passwd): Waiting...",
                                     this);

                        OracleLog.recursiveTrace = false;
                    }

                    synchronized (this) {
                        if ((this.cacheFixedWaitTimeOut > 0L)
                                && (System.currentTimeMillis() - fixedWaitStartTime > this.cacheFixedWaitTimeOut * 1000L)) {
                            DatabaseError.throwSqlException(126);
                        }

                    }

                    synchronized (this.cache) {
                        try {
                            this.cache.wait((this.fixedWaitIdleTime == -1L ? 30L
                                    : this.fixedWaitIdleTime) * 1000L);
                        } catch (InterruptedException ea) {
                            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.poolLogger.log(Level.FINE,
                                                         "OracleConnectionCacheImpl.getPooledConnection  (use, passwd): got an InterruptedException"
                                                                 + ea, this);

                                OracleLog.recursiveTrace = false;
                            }

                        }

                        if (!this.cache.empty()) {
                            pc = removeConnectionFromCache();
                        }
                    }
                }

            }

            if ((pc != null) && (this.stmtCacheSize > 0)) {
                if ((!this.explicitCachingEnabled) && (!this.implicitCachingEnabled)) {
                    ((OraclePooledConnection) pc).setStmtCacheSize(this.stmtCacheSize);
                } else {
                    ((OraclePooledConnection) pc).setStatementCacheSize(this.stmtCacheSize);
                    ((OraclePooledConnection) pc)
                            .setExplicitCachingEnabled(this.explicitCachingEnabled);

                    ((OraclePooledConnection) pc)
                            .setImplicitCachingEnabled(this.implicitCachingEnabled);
                }
            }

        }

        if (pc != null) {
            if (!createNewConnection) {
                ((OraclePooledConnection) pc).physicalConn.setDefaultRowPrefetch(10);
                ((OraclePooledConnection) pc).physicalConn.setDefaultExecuteBatch(1);
            }

            pc.addConnectionEventListener(this.ocel);

            this.activeCache.put(pc, pc);

            synchronized (this) {
                this.activeSize = this.activeCache.size();
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getPooledConnection(use, passwd): returned "
                                             + pc, this);

            OracleLog.recursiveTrace = false;
        }

        return pc;
    }

    PooledConnection getNewPoolOrXAConnection(Properties prop) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getNewPoolOrXAConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        PooledConnection pc = ((OracleConnectionPoolDataSource) this.cpds)
                .getPooledConnection(prop);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getNewPoolOrXAConnection(): returned "
                                             + pc, this);

            OracleLog.recursiveTrace = false;
        }

        return pc;
    }

    /** @deprecated */
    public void reusePooledConnection(PooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.reusePooledConnection("
                    + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        detachSingleConnection(pc);

        if ((this.cache.size() >= this._MAX_LIMIT) && (this.cacheScheme == 1)) {
            closeSingleConnection(pc, false);
        } else
            putConnectionToCache(pc);
    }

    /** @deprecated */
    public void closePooledConnection(PooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.closePooledConnection("
                    + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        detachSingleConnection(pc);

        closeSingleConnection(pc, false);
    }

    private void detachSingleConnection(PooledConnection pc) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "OracleConnectionCacheImpl.detachSingleConnection(" + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        pc.removeConnectionEventListener(this.ocel);

        this.activeCache.remove(pc);

        this.activeSize = this.activeCache.size();
    }

    /** @deprecated */
    public void closeSingleConnection(PooledConnection pc) throws SQLException {
        closeSingleConnection(pc, true);
    }

    final void closeSingleConnection(PooledConnection pc, boolean isCached) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.closeSingleConnection("
                    + pc + ", isCached=" + isCached + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((!removeConnectionFromCache(pc)) && (isCached)) {
            return;
        }

        try {
            pc.close();
        } catch (SQLException ea) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE,
                                         "OracleConnectionCacheImpl.closeSingleConnection(" + pc
                                                 + ") : Error while closing the connection " + ea,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            this.warning = DatabaseError.addSqlWarning(this.warning,
                                                       new SQLWarning(ea.getMessage()));
        }

        synchronized (this.CACHE_SIZE_LOCK) {
            this.cacheSize -= 1;
        }
    }

    /** @deprecated */
    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.close()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER,
                                     "OracleConnectionCacheImpl.close(): - start - Active Cache Size is "
                                             + this.activeSize + " Cache Size is " + this.cacheSize
                                             + " Cache Empty : " + this.cache.empty(), this);

            OracleLog.recursiveTrace = false;
        }

        closeConnections();

        this.cache = null;
        this.activeCache = null;
        this.ocel = null;
        this.cpds = null;
        this.timeOutThread = null;

        clearWarnings();
    }

    public void closeConnections() {
        Enumeration connSet = this.activeCache.keys();

        while (connSet.hasMoreElements()) {
            try {
                OraclePooledConnection opcKey = (OraclePooledConnection) connSet.nextElement();
                OraclePooledConnection opc = (OraclePooledConnection) this.activeCache.get(opcKey);

                if (opc == null) {
                    continue;
                }
                OracleConnection conn = (OracleConnection) opc.getLogicalHandle();

                conn.close();
            } catch (Exception e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleConnectionCacheImpl.closeConnections()" + e,
                                             this);

                    OracleLog.recursiveTrace = false;
                }

            }

        }

        while (!this.cache.empty()) {
            try {
                closeSingleConnection((PooledConnection) this.cache.peek(), false);
            } catch (SQLException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleConnectionCacheImpl.closeConnections()" + e,
                                             this);

                    OracleLog.recursiveTrace = false;
                }
            }
        }
    }

    public synchronized void setConnectionCleanupInterval(long secs) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.setConnectionCleanupInterval("
                                             + secs + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (secs > 0L)
            this.cleanupInterval = secs;
    }

    public long getConnectionCleanupInterval() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getConnectionCleanupInterval()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return this.cleanupInterval;
    }

    public synchronized void setConnectionErrorCodes(int[] fatalErrorCodes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.setConnectionErrorCodes("
                                             + fatalErrorCodes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (fatalErrorCodes != null)
            fatalErrorCodes = fatalErrorCodes;
    }

    public int[] getConnectionErrorCodes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getConnectionErrorCodes()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.fatalErrorCodes;
    }

    public boolean isFatalConnectionError(SQLException se) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.isFatalConnectionError()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.cleanupInterval < 0L) {
            return false;
        }
        boolean retCode = false;
        int errCode = se.getErrorCode();

        if ((errCode == 3113) || (errCode == 3114) || (errCode == 1033) || (errCode == 1034)
                || (errCode == 1089) || (errCode == 1090) || (errCode == 17002)) {
            retCode = true;
        } else if (this.fatalErrorCodes != null) {
            for (int i = 0; i < this.fatalErrorCodes.length; i++) {
                if (errCode == this.fatalErrorCodes[i]) {
                    retCode = true;
                }
            }
        }
        return retCode;
    }

    /** @deprecated */
    public synchronized void setMinLimit(int l) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.setMinLimit(" + l + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((l < 0) || (l > this._MAX_LIMIT)) {
            DatabaseError.throwSqlException(68);
        }
        this._MIN_LIMIT = l;
        if (this.cpds == null) {
            initializeConnectionPoolDataSource();
        }

        if (this.cacheSize < this._MIN_LIMIT) {
            Properties prop = new Properties();
            if (this.url != null)
                prop.setProperty("connection_url", this.url);
            if (this.user != null)
                prop.setProperty("user", this.user);
            if (this.password != null) {
                prop.setProperty("password", this.password);
            }
            if ((this.stmtCacheSize == 0) && (this.maxStatements != 0)) {
                prop.setProperty("stmt_cache_size", "" + this.maxStatements);
            } else {
                prop.setProperty("stmt_cache_size", "" + this.stmtCacheSize);
                prop.setProperty("stmt_cache_clear_metadata", "" + this.stmtClearMetaData);

                prop.setProperty("ImplicitStatementCachingEnabled", ""
                        + this.implicitCachingEnabled);

                prop.setProperty("ExplicitStatementCachingEnabled", ""
                        + this.explicitCachingEnabled);
            }

            prop.setProperty("LoginTimeout", "" + this.loginTimeout);

            for (int i = this.cacheSize; i < this._MIN_LIMIT; i++) {
                PooledConnection pc = getNewPoolOrXAConnection(prop);
                putConnectionToCache(pc);
            }

            this.cacheSize = this._MIN_LIMIT;
        }
    }

    void initializeConnectionPoolDataSource() throws SQLException {
        if (this.cpds == null) {
            if ((this.user == null) || (this.password == null)) {
                DatabaseError.throwSqlException(79);
            }
            this.cpds = new OracleConnectionPoolDataSource();

            copy((OracleDataSource) this.cpds);
        }
    }

    /** @deprecated */
    public synchronized int getMinLimit() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getMinLimit(): returned "
                                             + this._MIN_LIMIT, this);

            OracleLog.recursiveTrace = false;
        }

        return this._MIN_LIMIT;
    }

    /** @deprecated */
    public synchronized void setMaxLimit(int l) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.setMaxLimit(" + l + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((l < 0) || (l < this._MIN_LIMIT)) {
            DatabaseError.throwSqlException(68);
        }
        this._MAX_LIMIT = l;

        if ((this.cacheSize > this._MAX_LIMIT) && (this.cacheScheme != 1)) {
            for (int i = this._MAX_LIMIT; i < this.cacheSize; i++) {
                if (this.cache.empty())
                    DatabaseError.throwSqlException(78);
                else {
                    removeConnectionFromCache().close();
                }
            }
            this.cacheSize = this._MAX_LIMIT;
        }
    }

    /** @deprecated */
    public synchronized int getMaxLimit() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getMaxLimit(): returned "
                                             + this._MAX_LIMIT, this);

            OracleLog.recursiveTrace = false;
        }

        return this._MAX_LIMIT;
    }

    /** @deprecated */
    public synchronized int getCacheScheme() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getCacheScheme(): returned "
                                             + this.cacheScheme, this);

            OracleLog.recursiveTrace = false;
        }

        return this.cacheScheme;
    }

    /** @deprecated */
    public synchronized void setCacheScheme(int s) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.setCacheScheme(" + s
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((s == 1) || (s == 3) || (s == 2)) {
            this.cacheScheme = s;

            return;
        }

        DatabaseError.throwSqlException(68);
    }

    /** @deprecated */
    public synchronized void setCacheScheme(String s) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.setCacheScheme(" + s
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (s.equalsIgnoreCase("DYNAMIC_SCHEME"))
            this.cacheScheme = 1;
        else if (s.equalsIgnoreCase("FIXED_RETURN_NULL_SCHEME"))
            this.cacheScheme = 3;
        else if (s.equalsIgnoreCase("FIXED_WAIT_SCHEME"))
            this.cacheScheme = 2;
        else
            DatabaseError.throwSqlException(68);
    }

    /** @deprecated */
    public synchronized int getActiveSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getActiveSize(): returned "
                                             + this.activeSize, this);

            OracleLog.recursiveTrace = false;
        }

        return this.activeSize;
    }

    /** @deprecated */
    public synchronized int getCacheSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getCacheSize(): returned "
                                             + this.cacheSize, this);

            OracleLog.recursiveTrace = false;
        }

        return this.cacheSize;
    }

    /** @deprecated */
    public synchronized void setCacheTimeToLiveTimeout(long timeOut) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.setCacheTimeToLiveTimeout("
                                             + timeOut + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.timeOutThread == null) {
            this.timeOutThread = new OracleConnectionCacheTimeOutThread(this);
        }

        if (timeOut <= 0L) {
            this.cacheTTLTimeOut = -1L;
            this.warning = DatabaseError.addSqlWarning(this.warning, 111);
        } else {
            this.cacheTTLTimeOut = timeOut;

            checkAndStartTimeOutThread();
        }
    }

    /** @deprecated */
    public synchronized void setCacheInactivityTimeout(long timeout) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.setCacheInactivityTimeout("
                                             + timeout + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.timeOutThread == null) {
            this.timeOutThread = new OracleConnectionCacheTimeOutThread(this);
        }

        if (timeout <= 0L) {
            this.cacheInactivityTimeOut = -1L;
            this.warning = DatabaseError.addSqlWarning(this.warning, 124);
        } else {
            this.cacheInactivityTimeOut = timeout;

            checkAndStartTimeOutThread();
        }
    }

    /** @deprecated */
    public synchronized void setCacheFixedWaitTimeout(long timeout) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.setCacheFixedWaitTimeout("
                                             + timeout + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (timeout <= 0L) {
            this.cacheFixedWaitTimeOut = -1L;
            this.warning = DatabaseError.addSqlWarning(this.warning, 127);
        } else {
            this.cacheFixedWaitTimeOut = timeout;
        }
    }

    /** @deprecated */
    public long getCacheTimeToLiveTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.getCacheTimeToLiveTimeout(): returned "
                                             + this.cacheTTLTimeOut, this);

            OracleLog.recursiveTrace = false;
        }

        return this.cacheTTLTimeOut;
    }

    /** @deprecated */
    public long getCacheInactivityTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.getCacheInactivityTimeout(): returned "
                                             + this.cacheInactivityTimeOut, this);

            OracleLog.recursiveTrace = false;
        }

        return this.cacheInactivityTimeOut;
    }

    /** @deprecated */
    public long getCacheFixedWaitTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.getCacheFixedWaitTimeout(): returned "
                                             + this.cacheFixedWaitTimeOut, this);

            OracleLog.recursiveTrace = false;
        }

        return this.cacheFixedWaitTimeOut;
    }

    /** @deprecated */
    public synchronized void setThreadWakeUpInterval(long interval) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.setThreadWakeUpInterval("
                                             + interval + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (interval <= 0L) {
            this.threadInterval = 900L;
            this.warning = DatabaseError.addSqlWarning(this.warning, 112);
        } else {
            this.threadInterval = interval;
        }

        if (((this.cacheTTLTimeOut > 0L) && (this.threadInterval > this.cacheTTLTimeOut))
                || ((this.cacheInactivityTimeOut > 0L) && (this.threadInterval > this.cacheInactivityTimeOut))) {
            this.warning = DatabaseError.addSqlWarning(this.warning, 113);
        }
    }

    /** @deprecated */
    public long getThreadWakeUpInterval() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.INFO,
                                     "OracleConnectionCacheImpl.getThreadWakeUpInterval(): returned "
                                             + this.threadInterval, this);

            OracleLog.recursiveTrace = false;
        }

        return this.threadInterval;
    }

    private void checkAndStartTimeOutThread() throws SQLException {
        try {
            if (!this.timeOutThread.isAlive()) {
                this.timeOutThread.setDaemon(true);
                this.timeOutThread.start();
            }

        } catch (IllegalThreadStateException ie) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleConnectionCacheImpl.checkAndStartTimeOutThread()"
                                                 + ie, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    /** @deprecated */
    public SQLWarning getWarnings() throws SQLException {
        return this.warning;
    }

    /** @deprecated */
    public void clearWarnings() throws SQLException {
        this.warning = null;
    }

    private final void checkCredentials(String user, String passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.checkCredentials(user="
                    + user + ", passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String mc_user = null;
        String mc_passwd = null;

        if (this.cpds != null) {
            mc_user = ((OracleConnectionPoolDataSource) this.cpds).getUser();
            mc_passwd = ((OracleConnectionPoolDataSource) this.cpds).getPassword();
        }

        if (((user != null) && (!user.equals(mc_user)))
                || ((passwd != null) && (!passwd.equals(mc_passwd)))) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger
                        .log(
                             Level.FINE,
                             "OracleConnectionCacheImpl.checkCredentials(user, passwd): \nCannot create a connection with user as "
                                     + user
                                     + " as it is doesnt match the existing user "
                                     + user
                                     + " Or the Password", this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(79);
        }
    }

    public synchronized Reference getReference() throws NamingException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.getReference()", this);

            OracleLog.recursiveTrace = false;
        }

        Reference ref = new Reference(getClass().getName(),
                "oracle.jdbc.pool.OracleDataSourceFactory", null);

        super.addRefProperties(ref);

        if (this._MIN_LIMIT != _DEFAULT_MIN_LIMIT) {
            ref.add(new StringRefAddr("minLimit", Integer.toString(this._MIN_LIMIT)));
        }
        if (this._MAX_LIMIT != _DEFAULT_MAX_LIMIT) {
            ref.add(new StringRefAddr("maxLimit", Integer.toString(this._MAX_LIMIT)));
        }
        if (this.cacheScheme != 1) {
            ref.add(new StringRefAddr("cacheScheme", Integer.toString(this.cacheScheme)));
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getReference(): returned " + ref,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return ref;
    }

    /** @deprecated */
    public synchronized void setStmtCacheSize(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.setStmtCacheSize"
                    + size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setStmtCacheSize(size, false);
    }

    /** @deprecated */
    public synchronized void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.setStmtCacheSize"
                    + size + ", clearMetaData=" + clearMetaData + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (size < 0) {
            DatabaseError.throwSqlException(68);
        }
        this.stmtCacheSize = size;
        this.stmtClearMetaData = clearMetaData;
    }

    /** @deprecated */
    public synchronized int getStmtCacheSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.getStmtCacheSize()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return this.stmtCacheSize;
    }

    synchronized boolean isStmtCacheEnabled() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.isStmtCacheEnabled(): stmtCacheSize="
                                             + this.stmtCacheSize, this);

            OracleLog.recursiveTrace = false;
        }

        return this.stmtCacheSize > 0;
    }

    private void putConnectionToCache(PooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheImpl.putConnectionToCache("
                    + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ((OraclePooledConnection) pc).setLastAccessedTime(System.currentTimeMillis());
        this.cache.push(pc);

        synchronized (this.cache) {
            this.cache.notify();
        }
    }

    private PooledConnection removeConnectionFromCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.removeConnectionFromCache()", this);

            OracleLog.recursiveTrace = false;
        }

        return (PooledConnection) this.cache.pop();
    }

    private boolean removeConnectionFromCache(PooledConnection pc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.removeConnectionFromCache(" + pc
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return this.cache.removeElement(pc);
    }

    /** @deprecated */
    public synchronized void setCacheFixedWaitIdleTime(long idleTime) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.setCacheFixedWaitIdleTime("
                                             + idleTime + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.cacheScheme == 2) {
            if (idleTime <= 0L) {
                DatabaseError.addSqlWarning(this.warning, 68);

                this.fixedWaitIdleTime = 30L;
            } else {
                this.fixedWaitIdleTime = idleTime;
            }
        } else
            DatabaseError.addSqlWarning(this.warning, new SQLWarning(
                    "Caching scheme is not FIXED_WAIT_SCHEME"));
    }

    /** @deprecated */
    public long getCacheFixedWaitIdleTime() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionCacheImpl.getCacheFixedWaitIdleTime()returned "
                                             + (this.fixedWaitIdleTime == -1L ? 30L
                                                     : this.fixedWaitIdleTime), this);

            OracleLog.recursiveTrace = false;
        }

        return this.fixedWaitIdleTime == -1L ? 30L : this.fixedWaitIdleTime;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleConnectionCacheImpl"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleConnectionCacheImpl JD-Core Version: 0.6.0
 */