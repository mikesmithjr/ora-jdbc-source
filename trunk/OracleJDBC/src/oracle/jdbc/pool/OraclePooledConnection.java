package oracle.jdbc.pool;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.transaction.xa.XAResource;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleCloseCallback;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class OraclePooledConnection implements PooledConnection, Serializable {
    public static final String url_string = "connection_url";
    public static final String pool_auto_commit_string = "pool_auto_commit";
    public static final String object_type_map = "obj_type_map";
    public static final String transaction_isolation = "trans_isolation";
    public static final String statement_cache_size = "stmt_cache_size";
    public static final String isClearMetaData = "stmt_cache_clear_metadata";
    public static final String ImplicitStatementCachingEnabled = "ImplicitStatementCachingEnabled";
    public static final String ExplicitStatementCachingEnabled = "ExplicitStatementCachingEnabled";
    public static final String LoginTimeout = "LoginTimeout";
    public static final String connect_auto_commit_string = "connect_auto_commit";
    public static final String implicit_caching_enabled = "implicit_cache_enabled";
    public static final String explicit_caching_enabled = "explict_cache_enabled";
    public static final String connection_properties_string = "connection_properties";
    public static final String event_listener_string = "event_listener";
    public static final String sql_exception_string = "sql_exception";
    public static final String close_callback_string = "close_callback";
    public static final String private_data = "private_data";
    private Hashtable eventListeners = null;
    private SQLException sqlException = null;
    protected boolean autoCommit = true;

    private ConnectionEventListener iccEventListener = null;

    protected transient OracleConnection logicalHandle = null;

    protected transient OracleConnection physicalConn = null;

    private Hashtable connectionProperty = null;

    public Properties cachedConnectionAttributes = null;
    public Properties unMatchedCachedConnAttr = null;
    public int closeOption = 0;
    protected String pcUser = null;

    private String pcKey = null;

    private OracleCloseCallback closeCallback = null;
    private Object privateData = null;

    private long lastAccessedTime = 0L;

    protected String dataSourceInstanceNameKey = null;
    protected String dataSourceHostNameKey = null;
    protected String dataSourceDbUniqNameKey = null;
    protected boolean connectionMarkedDown = false;
    protected boolean isHostDown = false;

    protected transient OracleDriver oracleDriver = new OracleDriver();

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OraclePooledConnection() {
        this((Connection) null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.OraclePooledConnection()",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OraclePooledConnection(String url) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.OraclePooledConnection("
                    + url + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = this.oracleDriver.connect(url, new Properties());

        if (conn == null) {
            DatabaseError.throwSqlException(67);
        }
        initialize(conn);
    }

    public OraclePooledConnection(String url, String user, String passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.OraclePooledConnection(url=" + url
                                             + ", user=" + user + ", passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Properties props = new Properties();

        props.put("user", user);
        props.put("password", passwd);

        Connection conn = this.oracleDriver.connect(url, props);

        if (conn == null) {
            DatabaseError.throwSqlException(67);
        }
        initialize(conn);
    }

    public OraclePooledConnection(Connection pc) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.OraclePooledConnection("
                    + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        initialize(pc);
    }

    public OraclePooledConnection(Connection pc, boolean ac) {
        this(pc);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.OraclePooledConnection(pc=" + pc
                                             + ", ac=" + ac + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.autoCommit = ac;
    }

    private void initialize(Connection pc) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.initialize(" + pc + ")",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        this.physicalConn = ((OracleConnection) pc);
        this.eventListeners = new Hashtable(10);

        this.closeCallback = null;
        this.privateData = null;
        this.lastAccessedTime = 0L;
    }

    public synchronized void addConnectionEventListener(ConnectionEventListener cel) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.addConnectionEventListener(" + cel
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.eventListeners == null)
            this.sqlException = new SQLException("Listener Hashtable Null");
        else
            this.eventListeners.put(cel, cel);
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.close()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closeCallback != null) {
            this.closeCallback.beforeClose(this.physicalConn, this.privateData);
        }
        if (this.physicalConn != null) {
            this.physicalConn.close();

            this.physicalConn = null;
        }

        if (this.closeCallback != null) {
            this.closeCallback.afterClose(this.privateData);
        }

        this.lastAccessedTime = 0L;

        this.iccEventListener = null;

        callListener(2);
    }

    public synchronized Connection getConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.getConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn == null) {
            this.sqlException = new SQLException("Physical Connection doesn't exis");

            callListener(2);

            DatabaseError.throwSqlException(8);

            return null;
        }

        try {
            if (this.logicalHandle != null) {
                this.logicalHandle.closeInternal(false);
            }

            this.logicalHandle = ((OracleConnection) this.physicalConn
                    .getLogicalConnection(this, this.autoCommit));
        } catch (SQLException se) {
            this.sqlException = se;

            callListener(2);
            callImplicitCacheListener(102);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINE,
                                         "OraclePooledConnection.getConnection(): got exception "
                                                 + se.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(8);

            return null;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getConnection(): returned "
                                             + this.logicalHandle, this);

            OracleLog.recursiveTrace = false;
        }

        return this.logicalHandle;
    }

    public Connection getLogicalHandle() throws SQLException {
        return this.logicalHandle;
    }

    public Connection getPhysicalHandle() throws SQLException {
        return this.physicalConn;
    }

    public synchronized void setLastAccessedTime(long lastAccessedTime) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.setLastAccessedTime("
                    + lastAccessedTime + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.lastAccessedTime = lastAccessedTime;
    }

    public long getLastAccessedTime() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getLastAccessedTime(): returned "
                                             + this.lastAccessedTime, this);

            OracleLog.recursiveTrace = false;
        }

        return this.lastAccessedTime;
    }

    public synchronized void registerCloseCallback(OracleCloseCallback occ, Object privData) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.registerCloseCallback(occ=" + occ
                                             + ", privData=" + privData + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.closeCallback = occ;
        this.privateData = privData;
    }

    public synchronized void removeConnectionEventListener(ConnectionEventListener cel) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.removeConnectionEventListener(" + cel
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.eventListeners == null)
            this.sqlException = new SQLException("Listener Hashtable Null");
        else
            this.eventListeners.remove(cel);
    }

    public synchronized void registerImplicitCacheConnectionEventListener(
            ConnectionEventListener cel) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.registerImplicitCacheConnectionEventListener("
                                             + cel + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.iccEventListener != null) {
            this.sqlException = new SQLException("Implicit cache listener already registered");
        } else
            this.iccEventListener = cel;
    }

    public void logicalCloseForImplicitConnectionCache() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "OraclePooledConnection.logicalCloseForImplicitConnectionCache()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closeOption == 4096) {
            callImplicitCacheListener(102);
        } else {
            callImplicitCacheListener(101);
        }
    }

    public void logicalClose() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.logicalClose()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.cachedConnectionAttributes != null) {
            logicalCloseForImplicitConnectionCache();
        } else {
            synchronized (this) {
                callListener(1);
            }
        }
    }

    private void callListener(int callcode) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.callListener(" + callcode
                    + "): eventListeners=" + this.eventListeners, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.eventListeners == null) {
            return;
        }

        Enumeration listeners = this.eventListeners.keys();

        ConnectionEvent ce = new ConnectionEvent(this, this.sqlException);

        while (listeners.hasMoreElements()) {
            ConnectionEventListener listKey = (ConnectionEventListener) listeners.nextElement();

            ConnectionEventListener cev = (ConnectionEventListener) this.eventListeners
                    .get(listKey);

            if (callcode == 1)
                cev.connectionClosed(ce);
            else if (callcode == 2)
                cev.connectionErrorOccurred(ce);
        }
    }

    private void callImplicitCacheListener(int callcode) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OraclePooledConnection.callImplicitCacheListener(" + callcode
                            + "): iccEventListener=" + this.iccEventListener, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.iccEventListener == null) {
            return;
        }
        ConnectionEvent ce = new ConnectionEvent(this, this.sqlException);

        switch (callcode) {
        case 101:
            this.iccEventListener.connectionClosed(ce);

            break;
        case 102:
            this.iccEventListener.connectionErrorOccurred(ce);
        }
    }

    /** @deprecated */
    public synchronized void setStmtCacheSize(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.setStmtCacheSize(" + size
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setStmtCacheSize(size, false);
    }

    /** @deprecated */
    public synchronized void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.setStmtCacheSize(size="
                    + size + ", clearMetaData=" + clearMetaData + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (size < 0) {
            DatabaseError.throwSqlException(68);
        }

        if (this.physicalConn != null)
            this.physicalConn.setStmtCacheSize(size, clearMetaData);
    }

    /** @deprecated */
    public synchronized int getStmtCacheSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getStmtCacheSize(): physicalConn="
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null) {
            return this.physicalConn.getStmtCacheSize();
        }
        return 0;
    }

    public void setStatementCacheSize(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.setStatementCacheSize("
                    + size + "), physicalConn = " + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null)
            this.physicalConn.setStatementCacheSize(size);
    }

    public int getStatementCacheSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getStatementCacheSize(), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null) {
            return this.physicalConn.getStatementCacheSize();
        }
        return 0;
    }

    public void setImplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.setImplicitCachingEnabled(" + cache
                                             + "), physicalConn = " + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null)
            this.physicalConn.setImplicitCachingEnabled(cache);
    }

    public boolean getImplicitCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getImplicitCachingEnabled(), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null) {
            return this.physicalConn.getImplicitCachingEnabled();
        }
        return false;
    }

    public void setExplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.setExplicitCachingEnabled(" + cache
                                             + "), physicalConn = " + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null)
            this.physicalConn.setExplicitCachingEnabled(cache);
    }

    public boolean getExplicitCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getExplicitCachingEnabled(), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null) {
            return this.physicalConn.getExplicitCachingEnabled();
        }
        return false;
    }

    public void purgeImplicitCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.purgeImplicitCache(), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null)
            this.physicalConn.purgeImplicitCache();
    }

    public void purgeExplicitCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.purgeExplicitCache(), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null)
            this.physicalConn.purgeExplicitCache();
    }

    public PreparedStatement getStatementWithKey(String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getStatementWithKey(key), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null) {
            return this.physicalConn.getStatementWithKey(key);
        }
        return null;
    }

    public CallableStatement getCallWithKey(String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OraclePooledConnection.getCallWithKey(key), physicalConn = "
                                             + this.physicalConn, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.physicalConn != null) {
            return this.physicalConn.getCallWithKey(key);
        }
        return null;
    }

    public boolean isStatementCacheInitialized() {
        if (this.physicalConn != null) {
            return this.physicalConn.isStatementCacheInitialized();
        }
        return false;
    }

    public final void setProperties(Hashtable prop) {
        this.connectionProperty = prop;
    }

    public final void setUserName(String userName, String _pcKey) {
        this.pcUser = userName;
        this.pcKey = (this.pcUser + _pcKey);
    }

    final OracleConnectionCacheEntry addToImplicitCache(HashMap userMap,
            OracleConnectionCacheEntry userConnEntry) {
        return (OracleConnectionCacheEntry) userMap.put(this.pcKey, userConnEntry);
    }

    final OracleConnectionCacheEntry removeFromImplictCache(HashMap userMap) {
        return (OracleConnectionCacheEntry) userMap.get(this.pcKey);
    }

    public XAResource getXAResource() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OraclePooledConnection.getXAResource()", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(23);

        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        try {
            this.physicalConn.getPropertyForPooledConnection(this);

            if (this.eventListeners != null) {
                this.connectionProperty.put("event_listener", this.eventListeners);
            }
            if (this.sqlException != null) {
                this.connectionProperty.put("sql_exception", this.sqlException);
            }
            this.connectionProperty.put("pool_auto_commit", "" + this.autoCommit);

            if (this.closeCallback != null) {
                this.connectionProperty.put("close_callback", this.closeCallback);
            }
            if (this.privateData != null) {
                this.connectionProperty.put("private_data", this.privateData);
            }
            out.writeObject(this.connectionProperty);
            this.physicalConn.close();
        } catch (SQLException ea) {
            ea.printStackTrace();
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException,
            SQLException {
        in.defaultReadObject();

        this.connectionProperty = ((Hashtable) in.readObject());
        try {
            Properties prop = (Properties) this.connectionProperty.get("connection_properties");

            String l_url = prop.getProperty("connection_url");

            this.oracleDriver = new OracleDriver();

            Connection conn = this.oracleDriver.connect(l_url, prop);

            initialize(conn);

            this.eventListeners = ((Hashtable) this.connectionProperty.get("event_listener"));

            this.sqlException = ((SQLException) this.connectionProperty.get("sql_exception"));

            this.autoCommit = ((String) this.connectionProperty.get("pool_auto_commit"))
                    .equals("true");

            this.closeCallback = ((OracleCloseCallback) this.connectionProperty
                    .get("close_callback"));

            this.privateData = this.connectionProperty.get("private_data");

            Map l_map = (Map) this.connectionProperty.get("obj_type_map");

            if (l_map != null) {
                ((OracleConnection) conn).setTypeMap(l_map);
            }
            String l_tmp = prop.getProperty("trans_isolation");

            conn.setTransactionIsolation(Integer.parseInt(l_tmp));

            l_tmp = prop.getProperty("stmt_cache_size");

            int l_statementCacheSize = Integer.parseInt(l_tmp);

            if (l_statementCacheSize != -1) {
                setStatementCacheSize(l_statementCacheSize);

                l_tmp = prop.getProperty("implicit_cache_enabled");
                if ((l_tmp != null) && (l_tmp.equalsIgnoreCase("true")))
                    setImplicitCachingEnabled(true);
                else {
                    setImplicitCachingEnabled(false);
                }
                l_tmp = prop.getProperty("explict_cache_enabled");
                if ((l_tmp != null) && (l_tmp.equalsIgnoreCase("true")))
                    setExplicitCachingEnabled(true);
                else
                    setExplicitCachingEnabled(false);
            }
            this.physicalConn.setAutoCommit(((String) prop.get("connect_auto_commit"))
                    .equals("true"));
        } catch (Exception ea) {
            ea.printStackTrace();
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OraclePooledConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OraclePooledConnection JD-Core Version: 0.6.0
 */