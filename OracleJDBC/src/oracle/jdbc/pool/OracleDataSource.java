package oracle.jdbc.pool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;

public class OracleDataSource implements DataSource, Serializable, Referenceable {
    protected PrintWriter logWriter = null;
    protected int loginTimeout = 0;

    protected String databaseName = null;
    protected String serviceName = null;
    protected String dataSourceName = "OracleDataSource";
    protected String description = null;
    protected String networkProtocol = "tcp";
    protected int portNumber = 0;
    protected String user = null;
    protected String password = null;
    protected String serverName = null;
    protected String url = null;
    protected String driverType = null;
    protected String tnsEntry = null;
    protected int maxStatements = 0;
    protected boolean implicitCachingEnabled = false;
    protected boolean explicitCachingEnabled = false;

    protected transient OracleImplicitConnectionCache odsCache = null;
    protected transient OracleConnectionCacheManager cacheManager = null;
    protected String connCacheName = null;
    protected Properties connCacheProperties = null;
    protected Properties connectionProperties = null;
    protected boolean connCachingEnabled = false;
    protected boolean fastConnFailover = false;
    protected String onsConfigStr = null;
    public boolean isOracleDataSource = true;

    private static final boolean fastConnectionFailoverSysProperty = "true"
            .equalsIgnoreCase(OracleDriver.getSystemPropertyFastConnectionFailover("false"));

    private boolean urlExplicit = false;
    private boolean useDefaultConnection = false;
    protected transient OracleDriver driver = new OracleDriver();
    private static final String spawnNewThreadToCancelProperty = "oracle.jdbc.spawnNewThreadToCancel";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    public OracleDataSource() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.OracleDataSource()", this);

            OracleLog.recursiveTrace = false;
        }

        processFastConnectionFailoverSysProperty();
    }

    void processFastConnectionFailoverSysProperty() {
        if ((this.isOracleDataSource) && (fastConnectionFailoverSysProperty)) {
            this.connCachingEnabled = true;

            if (this.cacheManager == null) {
                try {
                    this.cacheManager = OracleConnectionCacheManager
                            .getConnectionCacheManagerInstance();
                } catch (SQLException e) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleDataSource.processFastConnectionFailoverSysProperty()"
                                                         + e, this);

                        OracleLog.recursiveTrace = false;
                    }

                }

            }

            this.fastConnFailover = true;
            setSpawnNewThreadToCancel(true);
        }
    }

    public Connection getConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        String localUser = null;
        String localPassword = null;
        synchronized (this) {
            localUser = this.user;
            localPassword = this.password;
        }

        return getConnection(localUser, localPassword);
    }

    public Connection getConnection(String _user, String _passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getConnection(user=" + _user
                    + ", passwd=" + _passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = null;
        Properties prop = null;
        if (this.connCachingEnabled) {
            conn = getConnection(_user, _passwd, null);
        } else {
            synchronized (this) {
                makeURL();

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleDataSource.getConnection(user, passwd): URL is"
                                                     + this.url, this);

                    OracleLog.recursiveTrace = false;
                }

                prop = this.connectionProperties == null ? new Properties()
                        : (Properties) this.connectionProperties.clone();

                if (this.url != null)
                    prop.setProperty("connection_url", this.url);
                if (_user != null)
                    prop.setProperty("user", _user);
                if (_passwd != null)
                    prop.setProperty("password", _passwd);
                if (this.loginTimeout != 0) {
                    prop.setProperty("LoginTimeout", "" + this.loginTimeout);
                }
                if (this.maxStatements != 0) {
                    prop.setProperty("stmt_cache_size", "" + this.maxStatements);
                }
            }
            conn = getPhysicalConnection(prop);
            if (conn == null) {
                DatabaseError.throwSqlException(67);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getConnection(user, passwd): returned "
                                             + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    protected Connection getPhysicalConnection(Properties prop) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleDataSource.getPhysicalConnection(prop="
                    + prop + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = null;
        Properties localProps = prop;
        String localUrl = prop.getProperty("connection_url");
        String localUser = prop.getProperty("user");
        String localPassword = localProps.getProperty("password");
        String temp = null;
        boolean localUseDefaultConnection = false;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "OracleDataSource.getPhysicalConnection(prop): URL is"
                                               + localUrl + ", user: " + localUser + ", password: "
                                               + localPassword, this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this) {
            if (this.connectionProperties != null) {
                localProps = (Properties) this.connectionProperties.clone();

                if (localUser != null) {
                    localProps.put("user", localUser);
                }
                if (localPassword != null)
                    localProps.put("password", localPassword);
            }
            if ((localUser == null) && (this.user != null))
                localProps.put("user", this.user);
            if ((localPassword == null) && (this.password != null)) {
                localProps.put("password", this.password);
            }
            if (localUrl == null) {
                localUrl = this.url;
            }
            String localLoginTimeout = prop.getProperty("LoginTimeout");

            if (localLoginTimeout != null) {
                localProps.put("oracle.net.CONNECT_TIMEOUT", ""
                        + Integer.parseInt(localLoginTimeout) * 1000);
            }

            localUseDefaultConnection = this.useDefaultConnection;

            if (this.driver == null) {
                this.driver = new OracleDriver();
            }

        }

        if (localUseDefaultConnection) {
            conn = this.driver.defaultConnection();
        } else {
            conn = this.driver.connect(localUrl, localProps);
        }

        if (conn == null) {
            DatabaseError.throwSqlException(67);
        }

        temp = prop.getProperty("stmt_cache_size");

        int statementCacheSize = 0;
        if (temp != null) {
            ((OracleConnection) conn).setStatementCacheSize(statementCacheSize = Integer
                    .parseInt(temp));
        }

        boolean explicitStatementCachingEnabled = false;
        temp = prop.getProperty("ExplicitStatementCachingEnabled");

        if (temp != null) {
            ((OracleConnection) conn)
                    .setExplicitCachingEnabled(explicitStatementCachingEnabled = temp
                            .equals("true"));
        }

        boolean implicitStatementCachingEnabled = false;
        temp = prop.getProperty("ImplicitStatementCachingEnabled");

        if (temp != null) {
            ((OracleConnection) conn)
                    .setImplicitCachingEnabled(implicitStatementCachingEnabled = temp
                            .equals("true"));
        }

        if ((statementCacheSize > 0) && (!explicitStatementCachingEnabled)
                && (!implicitStatementCachingEnabled)) {
            ((OracleConnection) conn).setImplicitCachingEnabled(true);
            ((OracleConnection) conn).setExplicitCachingEnabled(true);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleDataSource.getPhysicalConnection(Properties): returned "
                                               + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public Connection getConnection(Properties cachedConnectionAttributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getConnection(cachedConnectionAttributes="
                                             + cachedConnectionAttributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String localUser = null;
        String localPassword = null;
        synchronized (this) {
            if (!this.connCachingEnabled) {
                DatabaseError.throwSqlException(137);
            }
            localUser = this.user;
            localPassword = this.password;
        }

        Connection conn = getConnection(localUser, localPassword, cachedConnectionAttributes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleDataSource.getConnection(cachedConnectionAttributes) returned"
                                               + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public Connection getConnection(String _user, String _passwd,
            Properties cachedConnectionAttributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getConnection(user=" + _user
                    + ", passwd=" + _passwd + ", cachedConnectionAttributes="
                    + cachedConnectionAttributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.connCachingEnabled) {
            DatabaseError.throwSqlException(137);
        }

        if (this.odsCache == null) {
            cacheInitialize();
        }
        Connection conn = this.odsCache.getConnection(_user, _passwd, cachedConnectionAttributes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleDataSource.getConnection(user, password, cachedConnectionAttributes) returned"
                                               + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    private synchronized void cacheInitialize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.cacheInitialize()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.odsCache == null) {
            if (this.connCacheName != null)
                this.cacheManager.createCache(this.connCacheName, this, this.connCacheProperties);
            else
                this.connCacheName = this.cacheManager.createCache(this, this.connCacheProperties);
        }
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.close()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.connCachingEnabled) && (this.odsCache != null)) {
            this.cacheManager.removeCache(this.odsCache.cacheName, 0L);

            this.odsCache = null;
        }
    }

    public synchronized void setConnectionCachingEnabled(boolean flag) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setConnectionCachingEnabled("
                    + flag + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.isOracleDataSource) {
            if (flag) {
                this.connCachingEnabled = true;

                if (this.cacheManager == null) {
                    this.cacheManager = OracleConnectionCacheManager
                            .getConnectionCacheManagerInstance();
                }

            } else if (this.odsCache == null) {
                this.connCachingEnabled = false;
                this.fastConnFailover = false;
                setSpawnNewThreadToCancel(false);
                this.connCacheName = null;
                this.connCacheProperties = null;
            } else if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger
                        .log(
                             Level.FINER,
                             "OracleDataSource.setConnectionCachingEnabled()Can't disable connection caching without calling close()",
                             this);

                OracleLog.recursiveTrace = false;
            }

        } else {
            DatabaseError.throwSqlException(137);
        }
    }

    public boolean getConnectionCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getConnectionCachingEnabled()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return this.connCachingEnabled;
    }

    public synchronized void setConnectionCacheName(String cacheName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setCacheName(cacheName="
                    + cacheName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.connCachingEnabled) {
            if (cacheName == null) {
                DatabaseError.throwSqlException(138);
            } else {
                this.connCacheName = cacheName;
            }
        }
    }

    public String getConnectionCacheName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getCacheName()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.connCachingEnabled) && (this.odsCache != null)) {
            return this.odsCache.cacheName;
        }
        return this.connCacheName;
    }

    public synchronized void setConnectionCacheProperties(Properties cp) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setCacheProperties(cp=" + cp
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.connCachingEnabled)
            this.connCacheProperties = cp;
    }

    public Properties getConnectionCacheProperties() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getCacheProperties()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.connCachingEnabled) && (this.odsCache != null)) {
            return this.odsCache.getConnectionCacheProperties();
        }
        return this.connCacheProperties;
    }

    public synchronized void setFastConnectionFailoverEnabled(boolean flag) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.setFastConnectionFailoverEnabled(" + flag
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.connCachingEnabled) && (!this.fastConnFailover)) {
            this.fastConnFailover = flag;
            setSpawnNewThreadToCancel(flag);
        } else {
            DatabaseError.throwSqlException(137);
        }
    }

    public boolean getFastConnectionFailoverEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getFastConnectionFailoverEnabled()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.fastConnFailover;
    }

    public String getONSConfiguration() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getONSConfiguration()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.onsConfigStr;
    }

    public synchronized void setONSConfiguration(String onsConfigStr) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setONSConfiguration("
                    + onsConfigStr + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.onsConfigStr = onsConfigStr;
    }

    public synchronized int getLoginTimeout() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getLoginTimeout(): returned "
                    + this.loginTimeout, this);

            OracleLog.recursiveTrace = false;
        }

        return this.loginTimeout;
    }

    public synchronized void setLoginTimeout(int timeout) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setLoginTimeout(" + timeout
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.loginTimeout = timeout;
    }

    public synchronized void setLogWriter(PrintWriter pw) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setLogWriter(" + pw + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.logWriter = pw;

        OracleLog.setLogWriter(pw);
    }

    public synchronized PrintWriter getLogWriter() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getLogWriter(): returned "
                    + this.logWriter, this);

            OracleLog.recursiveTrace = false;
        }

        return this.logWriter;
    }

    public synchronized void setTNSEntryName(String dbname) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.setTNSEntryName(" + dbname + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.tnsEntry = dbname;
    }

    public synchronized String getTNSEntryName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getTNSEntryName(): returned "
                    + this.tnsEntry, this);

            OracleLog.recursiveTrace = false;
        }

        return this.tnsEntry;
    }

    public synchronized void setDataSourceName(String dsname) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setDataSourceName(" + dsname
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSourceName = dsname;
    }

    public synchronized String getDataSourceName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getDataSourceName(): returned "
                    + this.dataSourceName, this);

            OracleLog.recursiveTrace = false;
        }

        return this.dataSourceName;
    }

    public synchronized String getDatabaseName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getDatabaseName(): returned "
                    + this.databaseName, this);

            OracleLog.recursiveTrace = false;
        }

        return this.databaseName;
    }

    public synchronized void setDatabaseName(String dsname) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setDatabaseName(" + dsname
                    + "): returned " + dsname, this);

            OracleLog.recursiveTrace = false;
        }

        this.databaseName = dsname;
    }

    public synchronized void setServiceName(String svcname) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.setServiceName(" + svcname + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.serviceName = svcname;
    }

    public synchronized String getServiceName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getServiceName: returned "
                    + this.serviceName, this);

            OracleLog.recursiveTrace = false;
        }

        return this.serviceName;
    }

    public synchronized void setServerName(String sn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setServerName(" + sn
                    + "): returned " + sn, this);

            OracleLog.recursiveTrace = false;
        }

        this.serverName = sn;
    }

    public synchronized String getServerName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getServerName(): returned "
                    + this.serverName, this);

            OracleLog.recursiveTrace = false;
        }

        return this.serverName;
    }

    public synchronized void setURL(String url) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setURL(" + url + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.url = url;

        if (this.url != null)
            this.urlExplicit = true;
    }

    public synchronized String getURL() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getURL(): urlExplicit="
                    + this.urlExplicit, this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.urlExplicit) {
            makeURL();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getURL(): returned " + this.url,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return this.url;
    }

    public synchronized void setUser(String userName) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setUser(" + this.user
                    + "): returned " + this.user, this);

            OracleLog.recursiveTrace = false;
        }

        this.user = userName;
    }

    public synchronized String getUser() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getUser(): returned "
                    + this.user, this);

            OracleLog.recursiveTrace = false;
        }

        return this.user;
    }

    public synchronized void setPassword(String pd) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setPassword(" + pd
                    + "): returned " + pd, this);

            OracleLog.recursiveTrace = false;
        }

        this.password = pd;
    }

    protected synchronized String getPassword() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getPassword(): returned "
                    + this.password, this);

            OracleLog.recursiveTrace = false;
        }

        return this.password;
    }

    public synchronized String getDescription() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getDescription(): returned "
                    + this.description, this);

            OracleLog.recursiveTrace = false;
        }

        return this.description;
    }

    public synchronized void setDescription(String des) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setDescription(" + des
                    + "): returned " + des, this);

            OracleLog.recursiveTrace = false;
        }

        this.description = des;
    }

    public synchronized String getDriverType() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getDriverType(): returned "
                    + this.driverType, this);

            OracleLog.recursiveTrace = false;
        }

        return this.driverType;
    }

    public synchronized void setDriverType(String dt) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleDataSource.setDriverType(" + dt + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.driverType = dt;
    }

    public synchronized String getNetworkProtocol() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getNetworkProtocol(): returned "
                    + this.networkProtocol, this);

            OracleLog.recursiveTrace = false;
        }

        return this.networkProtocol;
    }

    public synchronized void setNetworkProtocol(String np) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setNetworkProtocol(" + np + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        this.networkProtocol = np;
    }

    public synchronized void setPortNumber(int pn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleDataSource.setPortNumber(" + pn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.portNumber = pn;
    }

    public synchronized int getPortNumber() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getPortNumber(): returned "
                    + this.portNumber, this);

            OracleLog.recursiveTrace = false;
        }

        return this.portNumber;
    }

    public synchronized Reference getReference() throws NamingException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getReference()", this);

            OracleLog.recursiveTrace = false;
        }

        Reference ref = new Reference(getClass().getName(),
                "oracle.jdbc.pool.OracleDataSourceFactory", null);

        addRefProperties(ref);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getReference(): returned " + ref, this);

            OracleLog.recursiveTrace = false;
        }

        return ref;
    }

    protected void addRefProperties(Reference ref) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.addRefProperties(" + ref + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        if (this.url != null) {
            ref.add(new StringRefAddr("url", this.url));
        }
        if (this.user != null) {
            ref.add(new StringRefAddr("userName", this.user));
        }
        if (this.password != null) {
            ref.add(new StringRefAddr("passWord", this.password));
        }
        if (this.description != null) {
            ref.add(new StringRefAddr("description", this.description));
        }
        if (this.driverType != null) {
            ref.add(new StringRefAddr("driverType", this.driverType));
        }
        if (this.serverName != null) {
            ref.add(new StringRefAddr("serverName", this.serverName));
        }
        if (this.databaseName != null) {
            ref.add(new StringRefAddr("databaseName", this.databaseName));
        }
        if (this.serviceName != null) {
            ref.add(new StringRefAddr("serviceName", this.serviceName));
        }
        if (this.networkProtocol != null) {
            ref.add(new StringRefAddr("networkProtocol", this.networkProtocol));
        }
        if (this.portNumber != 0) {
            ref.add(new StringRefAddr("portNumber", Integer.toString(this.portNumber)));
        }
        if (this.tnsEntry != null) {
            ref.add(new StringRefAddr("tnsentryname", this.tnsEntry));
        }
        if (this.maxStatements != 0) {
            ref.add(new StringRefAddr("maxStatements", Integer.toString(this.maxStatements)));
        }

        if (this.implicitCachingEnabled) {
            ref.add(new StringRefAddr("implicitCachingEnabled", "true"));
        }
        if (this.explicitCachingEnabled) {
            ref.add(new StringRefAddr("explicitCachingEnabled", "true"));
        }

        if (this.connCachingEnabled) {
            ref.add(new StringRefAddr("connectionCachingEnabled", "true"));
        }
        if (this.connCacheName != null) {
            ref.add(new StringRefAddr("connectionCacheName", this.connCacheName));
        }
        if (this.connCacheProperties != null) {
            ref.add(new StringRefAddr("connectionCacheProperties", this.connCacheProperties
                    .toString()));
        }

        if (this.fastConnFailover) {
            ref.add(new StringRefAddr("fastConnectionFailoverEnabled", "true"));
        }
        if (this.onsConfigStr != null)
            ref.add(new StringRefAddr("onsConfigStr", this.onsConfigStr));
    }

    void makeURL() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.makeURL()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.urlExplicit) {
            return;
        }

        if ((this.driverType == null)
                || ((!this.driverType.equals("oci8")) && (!this.driverType.equals("oci"))
                        && (!this.driverType.equals("thin")) && (!this.driverType.equals("kprb")))) {
            DatabaseError.throwSqlException(67, "OracleDataSource.makeURL");
        }

        if (this.driverType.equals("kprb")) {
            this.useDefaultConnection = true;
            this.url = "jdbc:oracle:kprb:@";

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE,
                                         "OracleDataSource.makeURL(): useDefaultConnection="
                                                 + this.useDefaultConnection + ", url=" + this.url,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if (((this.driverType.equals("oci8")) || (this.driverType.equals("oci")))
                && (this.networkProtocol != null) && (this.networkProtocol.equals("ipc"))) {
            this.url = "jdbc:oracle:oci:@";

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.makeURL(): url=" + this.url,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if (this.tnsEntry != null) {
            this.url = ("jdbc:oracle:" + this.driverType + ":@" + this.tnsEntry);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.makeURL(): url=" + this.url,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if (this.serviceName != null) {
            this.url = ("jdbc:oracle:" + this.driverType + ":@(DESCRIPTION=(ADDRESS=(PROTOCOL="
                    + this.networkProtocol + ")(PORT=" + this.portNumber + ")(HOST="
                    + this.serverName + "))(CONNECT_DATA=(SERVICE_NAME=" + this.serviceName + ")))");
        } else {
            this.url = ("jdbc:oracle:" + this.driverType + ":@(DESCRIPTION=(ADDRESS=(PROTOCOL="
                    + this.networkProtocol + ")(PORT=" + this.portNumber + ")(HOST="
                    + this.serverName + "))(CONNECT_DATA=(SID=" + this.databaseName + ")))");

            DatabaseError
                    .addSqlWarning(
                                   null,
                                   new SQLWarning(
                                           "URL with SID jdbc:subprotocol:@host:port:sid will be deprecated in 10i\nPlease use URL with SERVICE_NAME as jdbc:subprotocol:@//host:port/service_name"));

            if (this.fastConnFailover) {
                DatabaseError.throwSqlException(67, "OracleDataSource.makeURL");
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.makeURL(): url=" + this.url,
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected void trace(String s) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.trace(" + s + "): logWriter="
                    + this.logWriter, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.logWriter != null) {
            OracleLog.print(this, 2, 2, 32, "OracleDataSource.trace(s): logWriter is not null");
        }
    }

    protected void copy(OracleDataSource ds) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.copy(" + ds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ds.setUser(this.user);
        ds.setPassword(this.password);
        ds.setTNSEntryName(this.tnsEntry);
        makeURL();
        ds.setURL(this.url);
        ds.connectionProperties = this.connectionProperties;
    }

    /** @deprecated */
    public void setMaxStatements(int max) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setMaxStatements(" + max + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        this.maxStatements = max;
    }

    public int getMaxStatements() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.getMaxStatements(), returning "
                    + this.maxStatements, this);

            OracleLog.recursiveTrace = false;
        }

        return this.maxStatements;
    }

    public void setImplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setImplicitCachingEnabled("
                    + cache + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.implicitCachingEnabled = cache;
    }

    public boolean getImplicitCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getImplicitCachingEnabled(), returning "
                                             + this.implicitCachingEnabled, this);

            OracleLog.recursiveTrace = false;
        }

        return this.implicitCachingEnabled;
    }

    public void setExplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setExplicitCachingEnabled("
                    + cache + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.explicitCachingEnabled = cache;
    }

    public boolean getExplicitCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getExplicitCachingEnabled(), returning "
                                             + this.explicitCachingEnabled, this);

            OracleLog.recursiveTrace = false;
        }

        return this.explicitCachingEnabled;
    }

    public void setConnectionProperties(Properties value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleDataSource.setConnectionProperties (value="
                    + value + " )", this);

            OracleLog.recursiveTrace = false;
        }

        if (value == null)
            this.connectionProperties = value;
        else
            this.connectionProperties = ((Properties) value.clone());
        setSpawnNewThreadToCancel(this.fastConnFailover);
    }

    public Properties getConnectionProperties() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSource.getConnectionProperties (), returning "
                                             + this.connectionProperties, this);

            OracleLog.recursiveTrace = false;
        }

        return filterConnectionProperties(this.connectionProperties);
    }

    public static final Properties filterConnectionProperties(Properties prop) {
        Properties result = null;

        if (prop != null) {
            result = (Properties) prop.clone();
            Enumeration enu = result.propertyNames();
            char[] c = null;
            while (enu.hasMoreElements()) {
                String key = (String) enu.nextElement();

                if ((key != null) && (key.matches(".*[P,p][A,a][S,s][S,s][W,w][O,o][R,r][D,d].*"))) {
                    result.remove(key);
                }

            }

            prop.remove("oracle.jdbc.spawnNewThreadToCancel");
        }

        return result;
    }

    private void setSpawnNewThreadToCancel(boolean enable) {
        if (enable) {
            if (this.connectionProperties == null)
                this.connectionProperties = new Properties();
            this.connectionProperties.setProperty("oracle.jdbc.spawnNewThreadToCancel", "true");
        } else if (this.connectionProperties != null) {
            this.connectionProperties.remove("oracle.jdbc.spawnNewThreadToCancel");
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException,
            SQLException {
        in.defaultReadObject();

        if (this.connCachingEnabled)
            setConnectionCachingEnabled(this.connCachingEnabled);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleDataSource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleDataSource JD-Core Version: 0.6.0
 */