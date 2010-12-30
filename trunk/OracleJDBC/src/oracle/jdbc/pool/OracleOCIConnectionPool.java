package oracle.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oci.OracleOCIConnection;

public class OracleOCIConnectionPool extends OracleDataSource {
    public OracleOCIConnection m_connection_pool;
    public static final String IS_CONNECTION_POOLING = "is_connection_pooling";
    private int m_conn_min_limit = 0;
    private int m_conn_max_limit = 0;
    private int m_conn_increment = 0;
    private int m_conn_active_size = 0;
    private int m_conn_pool_size = 0;
    private int m_conn_timeout = 0;
    private String m_conn_nowait = "false";
    private int m_is_transactions_distributed = 0;
    public static final String CONNPOOL_OBJECT = "connpool_object";
    public static final String CONNPOOL_LOGON_MODE = "connection_pool";
    public static final String CONNECTION_POOL = "connection_pool";
    public static final String CONNPOOL_CONNECTION = "connpool_connection";
    public static final String CONNPOOL_PROXY_CONNECTION = "connpool_proxy_connection";
    public static final String CONNPOOL_ALIASED_CONNECTION = "connpool_alias_connection";
    public static final String PROXY_USER_NAME = "proxy_user_name";
    public static final String PROXY_DISTINGUISHED_NAME = "proxy_distinguished_name";
    public static final String PROXY_CERTIFICATE = "proxy_certificate";
    public static final String PROXY_ROLES = "proxy_roles";
    public static final String PROXY_NUM_ROLES = "proxy_num_roles";
    public static final String PROXY_PASSWORD = "proxy_password";
    public static final String PROXYTYPE = "proxytype";
    public static final String PROXYTYPE_USER_NAME = "proxytype_user_name";
    public static final String PROXYTYPE_DISTINGUISHED_NAME = "proxytype_distinguished_name";
    public static final String PROXYTYPE_CERTIFICATE = "proxytype_certificate";
    public static final String CONNECTION_ID = "connection_id";
    public static String CONNPOOL_MIN_LIMIT = "connpool_min_limit";
    public static String CONNPOOL_MAX_LIMIT = "connpool_max_limit";
    public static String CONNPOOL_INCREMENT = "connpool_increment";
    public static String CONNPOOL_ACTIVE_SIZE = "connpool_active_size";
    public static String CONNPOOL_POOL_SIZE = "connpool_pool_size";
    public static String CONNPOOL_TIMEOUT = "connpool_timeout";
    public static String CONNPOOL_NOWAIT = "connpool_nowait";
    public static String CONNPOOL_IS_POOLCREATED = "connpool_is_poolcreated";
    public static final String TRANSACTIONS_DISTRIBUTED = "transactions_distributed";
    private Hashtable m_lconnections = null;

    private boolean m_poolCreated = false;

    private OracleDriver m_oracleDriver = new OracleDriver();

    protected int m_stmtCacheSize = 0;
    protected boolean m_stmtClearMetaData = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleOCIConnectionPool(String us, String p, String url, Properties info)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleOCIConnectionPool.OracleOCIConnectionPool(us=" + us
                            + ", p=" + p + ", url=" + url + ", info=" + info + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.isOracleDataSource = false;
        this.m_lconnections = new Hashtable(10);

        setDriverType("oci8");
        setURL(url);
        setUser(us);
        setPassword(p);

        createConnectionPool(info);
    }

    /** @deprecated */
    public OracleOCIConnectionPool(String us, String p, String url) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.OracleOCIConnectionPool(us=" + us
                                             + ", p=" + p + ", url=" + url + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.isOracleDataSource = false;
        this.m_lconnections = new Hashtable(10);

        setDriverType("oci8");
        setURL(url);
        setUser(us);
        setPassword(p);

        createConnectionPool(null);
    }

    public OracleOCIConnectionPool() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.OracleOCIConnectionPool()", this);

            OracleLog.recursiveTrace = false;
        }

        this.m_lconnections = new Hashtable(10);

        setDriverType("oci8");
    }

    public synchronized Connection getConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }

        Connection conn = getConnection(this.user, this.password);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getConnection(): returned " + conn,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public synchronized Connection getConnection(String us, String p) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getConnection(us=" + us
                    + ", p=" + p + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = new Properties();

        info.put("is_connection_pooling", "true");
        info.put("user", us);
        info.put("password", p);
        info.put("connection_pool", "connpool_connection");
        info.put("connpool_object", this.m_connection_pool);

        OracleOCIConnection conn = (OracleOCIConnection) this.m_oracleDriver
                .connect(this.url, info);

        if (conn == null) {
            DatabaseError.throwSqlException(67);
        }

        conn.setStmtCacheSize(this.m_stmtCacheSize, this.m_stmtClearMetaData);

        this.m_lconnections.put(conn, conn);
        conn.setConnectionPool(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getConnection(us, p): returned "
                                             + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public synchronized Reference getReference() throws NamingException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getReference()", this);

            OracleLog.recursiveTrace = false;
        }

        Reference ref = new Reference(getClass().getName(),
                "oracle.jdbc.pool.OracleDataSourceFactory", null);

        super.addRefProperties(ref);

        ref.add(new StringRefAddr(CONNPOOL_MIN_LIMIT, String.valueOf(this.m_conn_min_limit)));

        ref.add(new StringRefAddr(CONNPOOL_MAX_LIMIT, String.valueOf(this.m_conn_max_limit)));

        ref.add(new StringRefAddr(CONNPOOL_INCREMENT, String.valueOf(this.m_conn_increment)));

        ref.add(new StringRefAddr(CONNPOOL_ACTIVE_SIZE, String.valueOf(this.m_conn_active_size)));

        ref.add(new StringRefAddr(CONNPOOL_POOL_SIZE, String.valueOf(this.m_conn_pool_size)));

        ref.add(new StringRefAddr(CONNPOOL_TIMEOUT, String.valueOf(this.m_conn_timeout)));

        ref.add(new StringRefAddr(CONNPOOL_NOWAIT, this.m_conn_nowait));

        ref.add(new StringRefAddr(CONNPOOL_IS_POOLCREATED, String.valueOf(isPoolCreated())));

        ref.add(new StringRefAddr("transactions_distributed", String
                .valueOf(isDistributedTransEnabled())));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getReference(): returned " + ref,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return ref;
    }

    public synchronized OracleConnection getProxyConnection(String proxytype, Properties prop)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getProxyConnection(proxytype="
                                             + proxytype + ", prop=" + prop + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }

        if (proxytype == "proxytype_user_name")
            prop.put("user", prop.getProperty("proxy_user_name"));
        else if (proxytype == "proxytype_distinguished_name") {
            prop.put("user", prop.getProperty("proxy_distinguished_name"));
        } else if (proxytype == "proxytype_certificate") {
            prop.put("user", String.valueOf(prop.getProperty("proxy_user_name")));
        } else {
            DatabaseError.throwSqlException(107, "null properties");
        }

        prop.put("is_connection_pooling", "true");
        prop.put("proxytype", proxytype);
        String[] proxyRoles;
        if ((proxyRoles = (String[]) prop.get("proxy_roles")) != null) {
            prop.put("proxy_num_roles", new Integer(proxyRoles.length));
        } else {
            prop.put("proxy_num_roles", new Integer(0));
        }

        prop.put("connection_pool", "connpool_proxy_connection");
        prop.put("connpool_object", this.m_connection_pool);

        OracleOCIConnection conn = (OracleOCIConnection) this.m_oracleDriver
                .connect(this.url, prop);

        if (conn == null) {
            DatabaseError.throwSqlException(67);
        }

        conn.setStmtCacheSize(this.m_stmtCacheSize, this.m_stmtClearMetaData);

        this.m_lconnections.put(conn, conn);
        conn.setConnectionPool(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getProxyConnection(proxytype, prop): returned "
                                             + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public synchronized OracleConnection getAliasedConnection(byte[] conId) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getAliasedConnection("
                    + OracleLog.bytesToPrintableForm("conId[]=", conId) + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = new Properties();

        info.put("is_connection_pooling", "true");
        info.put("connection_id", conId);
        info.put("connection_pool", "connpool_alias_connection");
        info.put("connpool_object", this.m_connection_pool);

        OracleOCIConnection conn = (OracleOCIConnection) this.m_oracleDriver
                .connect(this.url, info);

        if (conn == null) {
            DatabaseError.throwSqlException(67);
        }

        conn.setStmtCacheSize(this.m_stmtCacheSize, this.m_stmtClearMetaData);

        this.m_lconnections.put(conn, conn);
        conn.setConnectionPool(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getAliasedConnection(conId[]): returned "
                                             + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.close()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            return;
        }

        Enumeration e = this.m_lconnections.elements();

        while (e.hasMoreElements()) {
            OracleOCIConnection conn = (OracleOCIConnection) e.nextElement();

            if ((conn != null) && (conn != this.m_connection_pool)) {
                conn.close();
            }

        }

        this.m_connection_pool.close();
    }

    public synchronized void setPoolConfig(Properties prop) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.setPoolConfig(" + prop
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (prop == null) {
            DatabaseError.throwSqlException(106, "null properties");
        }

        if (!isPoolCreated()) {
            createConnectionPool(prop);
        } else {
            Properties info = new Properties();

            checkPoolConfig(prop, info);

            int[] p = new int[6];

            readPoolConfig(info, p);

            this.m_connection_pool.setConnectionPoolInfo(p[0], p[1], p[2], p[3], p[4], p[5]);
        }

        storePoolProperties();
    }

    public static void readPoolConfig(Properties info, int[] p) {
        for (int i = 0; i < 6; i++) {
            p[i] = 0;
        }
        String value = info.getProperty(CONNPOOL_MIN_LIMIT);

        if (value != null) {
            p[0] = Integer.parseInt(value);
        }
        value = info.getProperty(CONNPOOL_MAX_LIMIT);

        if (value != null) {
            p[1] = Integer.parseInt(value);
        }
        value = info.getProperty(CONNPOOL_INCREMENT);

        if (value != null) {
            p[2] = Integer.parseInt(value);
        }
        value = info.getProperty(CONNPOOL_TIMEOUT);

        if (value != null) {
            p[3] = Integer.parseInt(value);
        }
        value = info.getProperty(CONNPOOL_NOWAIT);

        if ((value != null) && (value.equalsIgnoreCase("true"))) {
            p[4] = 1;
        }
        value = info.getProperty("transactions_distributed");

        if ((value != null) && (value.equalsIgnoreCase("true")))
            p[5] = 1;
    }

    private void checkPoolConfig(Properties pc_in, Properties pc_out) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.checkPoolConfig(pc_in="
                    + pc_in + ", pc_out=" + pc_out + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String dist_txn = (String) pc_in.get("transactions_distributed");
        String conn_nowait = (String) pc_in.get(CONNPOOL_NOWAIT);

        if (((dist_txn != null) && (!dist_txn.equalsIgnoreCase("true")))
                || ((conn_nowait != null) && (!conn_nowait.equalsIgnoreCase("true")))
                || (pc_in.get(CONNPOOL_MIN_LIMIT) == null)
                || (pc_in.get(CONNPOOL_MAX_LIMIT) == null)
                || (pc_in.get(CONNPOOL_INCREMENT) == null)
                || (Integer.decode((String) pc_in.get(CONNPOOL_MIN_LIMIT)).intValue() < 0)
                || (Integer.decode((String) pc_in.get(CONNPOOL_MAX_LIMIT)).intValue() < 0)
                || (Integer.decode((String) pc_in.get(CONNPOOL_INCREMENT)).intValue() < 0)) {
            DatabaseError.throwSqlException(106, "");
        }

        Enumeration e = pc_in.propertyNames();

        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = pc_in.getProperty(key);

            if ((key == "transactions_distributed") || (key == CONNPOOL_NOWAIT)) {
                pc_out.put(key, "true");
                continue;
            }
            pc_out.put(key, value);
        }
    }

    private synchronized void storePoolProperties() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.storePoolProperties()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        Properties info = getPoolConfig();

        this.m_conn_min_limit = Integer.decode(info.getProperty(CONNPOOL_MIN_LIMIT)).intValue();

        this.m_conn_max_limit = Integer.decode(info.getProperty(CONNPOOL_MAX_LIMIT)).intValue();

        this.m_conn_increment = Integer.decode(info.getProperty(CONNPOOL_INCREMENT)).intValue();

        this.m_conn_active_size = Integer.decode(info.getProperty(CONNPOOL_ACTIVE_SIZE)).intValue();

        this.m_conn_pool_size = Integer.decode(info.getProperty(CONNPOOL_POOL_SIZE)).intValue();

        this.m_conn_timeout = Integer.decode(info.getProperty(CONNPOOL_TIMEOUT)).intValue();

        this.m_conn_nowait = info.getProperty(CONNPOOL_NOWAIT);
    }

    public synchronized Properties getPoolConfig() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getPoolConfig()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }

        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        info.put(CONNPOOL_IS_POOLCREATED, String.valueOf(isPoolCreated()));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getPoolConfig(): returned " + info,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return info;
    }

    public synchronized int getActiveSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getActiveSize()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        String value = info.getProperty(CONNPOOL_ACTIVE_SIZE);
        int res = Integer.decode(value).intValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getActiveSize(): returned " + res,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return res;
    }

    public synchronized int getPoolSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getPoolSize()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        String value = info.getProperty(CONNPOOL_POOL_SIZE);
        int res = Integer.decode(value).intValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getPoolSize(): returned "
                    + res, this);

            OracleLog.recursiveTrace = false;
        }

        return res;
    }

    public synchronized int getTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getTimeout()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        String value = info.getProperty(CONNPOOL_TIMEOUT);
        int res = Integer.decode(value).intValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getTimeout(): returned "
                    + res, this);

            OracleLog.recursiveTrace = false;
        }

        return res;
    }

    public synchronized String getNoWait() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getNoWait()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        return info.getProperty(CONNPOOL_NOWAIT);
    }

    public synchronized int getMinLimit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getMinLimit()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        String value = info.getProperty(CONNPOOL_MIN_LIMIT);
        int res = Integer.decode(value).intValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getMinLimit(): returned "
                    + res, this);

            OracleLog.recursiveTrace = false;
        }

        return res;
    }

    public synchronized int getMaxLimit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getMaxLimit()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        String value = info.getProperty(CONNPOOL_MAX_LIMIT);
        int res = Integer.decode(value).intValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.getMaxLimit(): returned "
                    + res, this);

            OracleLog.recursiveTrace = false;
        }

        return res;
    }

    public synchronized int getConnectionIncrement() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getConnectionIncrement()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isPoolCreated()) {
            createConnectionPool(null);
        }
        Properties info = this.m_connection_pool.getConnectionPoolInfo();

        String value = info.getProperty(CONNPOOL_INCREMENT);
        int res = Integer.decode(value).intValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getConnectionIncrement(): returned "
                                             + res, this);

            OracleLog.recursiveTrace = false;
        }

        return res;
    }

    public synchronized boolean isDistributedTransEnabled() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.isDistributedTransEnabled()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_is_transactions_distributed == 1;
    }

    private void createConnectionPool(Properties poolConfig) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.createConnectionPool("
                    + poolConfig + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (isPoolCreated()) {
            return;
        }
        if ((this.user == null) || (this.password == null)) {
            DatabaseError.throwSqlException(106, " ");
        } else {
            Properties info = new Properties();

            if (poolConfig != null) {
                checkPoolConfig(poolConfig, info);
            }
            info.put("is_connection_pooling", "true");
            info.put("user", this.user);
            info.put("password", this.password);
            info.put("connection_pool", "connection_pool");

            if (getURL() == null) {
                makeURL();
            }

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE,
                                         "OracleOCIConnectionPool.createConnectionPool(User us "
                                                 + this.user + "URL is " + getURL(), this);

                OracleLog.recursiveTrace = false;
            }

            this.m_connection_pool = ((OracleOCIConnection) this.m_oracleDriver.connect(this.url,
                                                                                        info));

            if (this.m_connection_pool == null) {
                DatabaseError.throwSqlException(67);
            }

            this.m_poolCreated = true;

            this.m_connection_pool.setConnectionPool(this);

            this.m_lconnections.put(this.m_connection_pool, this.m_connection_pool);

            storePoolProperties();

            if (poolConfig != null) {
                if (poolConfig.getProperty("transactions_distributed") != null)
                    this.m_is_transactions_distributed = 1;
            }
        }
    }

    public synchronized boolean isPoolCreated() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.isPoolCreated(): returned "
                                             + this.m_poolCreated, this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_poolCreated;
    }

    public synchronized void connectionClosed(OracleOCIConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.connectionClosed(" + conn
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.m_lconnections.remove(conn) == null)
            DatabaseError.throwSqlException(1, "internal OracleOCIConnectionPool error");
    }

    public synchronized void setStmtCacheSize(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.setStmtCacheSize(" + size
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setStmtCacheSize(size, false);
    }

    public synchronized void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleOCIConnectionPool.setStmtCacheSize(size="
                    + size + ", clearMetaData=" + clearMetaData + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (size < 0) {
            DatabaseError.throwSqlException(68);
        }
        this.m_stmtCacheSize = size;
        this.m_stmtClearMetaData = clearMetaData;
    }

    public synchronized int getStmtCacheSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.getStmtCacheSize(): returned "
                                             + this.m_stmtCacheSize, this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_stmtCacheSize;
    }

    public synchronized boolean isStmtCacheEnabled() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleOCIConnectionPool.isStmtCacheEnabled(): m_stmtCacheSize="
                                             + this.m_stmtCacheSize, this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_stmtCacheSize > 0;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleOCIConnectionPool"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleOCIConnectionPool JD-Core Version: 0.6.0
 */