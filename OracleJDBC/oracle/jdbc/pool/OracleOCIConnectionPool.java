package oracle.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oci.OracleOCIConnection;

public class OracleOCIConnectionPool extends OracleDataSource
{
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleOCIConnectionPool(String paramString1, String paramString2, String paramString3, Properties paramProperties)
    throws SQLException
  {
    this.isOracleDataSource = false;
    this.m_lconnections = new Hashtable(10);

    setDriverType("oci8");
    setURL(paramString3);
    setUser(paramString1);
    setPassword(paramString2);

    createConnectionPool(paramProperties);
  }

  /** @deprecated */
  public OracleOCIConnectionPool(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    this.isOracleDataSource = false;
    this.m_lconnections = new Hashtable(10);

    setDriverType("oci8");
    setURL(paramString3);
    setUser(paramString1);
    setPassword(paramString2);

    createConnectionPool(null);
  }

  public OracleOCIConnectionPool()
    throws SQLException
  {
    this.m_lconnections = new Hashtable(10);

    setDriverType("oci8");
  }

  public synchronized Connection getConnection()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }

    Connection localConnection = getConnection(this.user, this.password);

    return localConnection;
  }

  public synchronized Connection getConnection(String paramString1, String paramString2)
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = new Properties();

    localProperties.put("is_connection_pooling", "true");
    localProperties.put("user", paramString1);
    localProperties.put("password", paramString2);
    localProperties.put("connection_pool", "connpool_connection");
    localProperties.put("connpool_object", this.m_connection_pool);

    OracleOCIConnection localOracleOCIConnection = (OracleOCIConnection)this.m_oracleDriver.connect(this.url, localProperties);

    if (localOracleOCIConnection == null) {
      DatabaseError.throwSqlException(67);
    }

    localOracleOCIConnection.setStmtCacheSize(this.m_stmtCacheSize, this.m_stmtClearMetaData);

    this.m_lconnections.put(localOracleOCIConnection, localOracleOCIConnection);
    localOracleOCIConnection.setConnectionPool(this);

    return localOracleOCIConnection;
  }

  public synchronized Reference getReference()
    throws NamingException
  {
    Reference localReference = new Reference(getClass().getName(), "oracle.jdbc.pool.OracleDataSourceFactory", null);

    super.addRefProperties(localReference);

    localReference.add(new StringRefAddr(CONNPOOL_MIN_LIMIT, String.valueOf(this.m_conn_min_limit)));

    localReference.add(new StringRefAddr(CONNPOOL_MAX_LIMIT, String.valueOf(this.m_conn_max_limit)));

    localReference.add(new StringRefAddr(CONNPOOL_INCREMENT, String.valueOf(this.m_conn_increment)));

    localReference.add(new StringRefAddr(CONNPOOL_ACTIVE_SIZE, String.valueOf(this.m_conn_active_size)));

    localReference.add(new StringRefAddr(CONNPOOL_POOL_SIZE, String.valueOf(this.m_conn_pool_size)));

    localReference.add(new StringRefAddr(CONNPOOL_TIMEOUT, String.valueOf(this.m_conn_timeout)));

    localReference.add(new StringRefAddr(CONNPOOL_NOWAIT, this.m_conn_nowait));

    localReference.add(new StringRefAddr(CONNPOOL_IS_POOLCREATED, String.valueOf(isPoolCreated())));

    localReference.add(new StringRefAddr("transactions_distributed", String.valueOf(isDistributedTransEnabled())));

    return localReference;
  }

  public synchronized OracleConnection getProxyConnection(String paramString, Properties paramProperties)
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }

    if (paramString == "proxytype_user_name")
      paramProperties.put("user", paramProperties.getProperty("proxy_user_name"));
    else if (paramString == "proxytype_distinguished_name") {
      paramProperties.put("user", paramProperties.getProperty("proxy_distinguished_name"));
    }
    else if (paramString == "proxytype_certificate") {
      paramProperties.put("user", String.valueOf(paramProperties.getProperty("proxy_user_name")));
    }
    else {
      DatabaseError.throwSqlException(107, "null properties");
    }

    paramProperties.put("is_connection_pooling", "true");
    paramProperties.put("proxytype", paramString);
    String[] arrayOfString;
    if ((arrayOfString = (String[])paramProperties.get("proxy_roles")) != null)
    {
      paramProperties.put("proxy_num_roles", new Integer(arrayOfString.length));
    }
    else
    {
      paramProperties.put("proxy_num_roles", new Integer(0));
    }

    paramProperties.put("connection_pool", "connpool_proxy_connection");
    paramProperties.put("connpool_object", this.m_connection_pool);

    OracleOCIConnection localOracleOCIConnection = (OracleOCIConnection)this.m_oracleDriver.connect(this.url, paramProperties);

    if (localOracleOCIConnection == null) {
      DatabaseError.throwSqlException(67);
    }

    localOracleOCIConnection.setStmtCacheSize(this.m_stmtCacheSize, this.m_stmtClearMetaData);

    this.m_lconnections.put(localOracleOCIConnection, localOracleOCIConnection);
    localOracleOCIConnection.setConnectionPool(this);

    return localOracleOCIConnection;
  }

  public synchronized OracleConnection getAliasedConnection(byte[] paramArrayOfByte)
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = new Properties();

    localProperties.put("is_connection_pooling", "true");
    localProperties.put("connection_id", paramArrayOfByte);
    localProperties.put("connection_pool", "connpool_alias_connection");
    localProperties.put("connpool_object", this.m_connection_pool);

    OracleOCIConnection localOracleOCIConnection = (OracleOCIConnection)this.m_oracleDriver.connect(this.url, localProperties);

    if (localOracleOCIConnection == null) {
      DatabaseError.throwSqlException(67);
    }

    localOracleOCIConnection.setStmtCacheSize(this.m_stmtCacheSize, this.m_stmtClearMetaData);

    this.m_lconnections.put(localOracleOCIConnection, localOracleOCIConnection);
    localOracleOCIConnection.setConnectionPool(this);

    return localOracleOCIConnection;
  }

  public synchronized void close()
    throws SQLException
  {
    if (!isPoolCreated()) {
      return;
    }

    Enumeration localEnumeration = this.m_lconnections.elements();

    while (localEnumeration.hasMoreElements())
    {
      OracleOCIConnection localOracleOCIConnection = (OracleOCIConnection)localEnumeration.nextElement();

      if ((localOracleOCIConnection == null) || (localOracleOCIConnection == this.m_connection_pool))
        continue;
      localOracleOCIConnection.close();
    }

    this.m_connection_pool.close();
  }

  public synchronized void setPoolConfig(Properties paramProperties)
    throws SQLException
  {
    if (paramProperties == null) {
      DatabaseError.throwSqlException(106, "null properties");
    }

    if (!isPoolCreated())
    {
      createConnectionPool(paramProperties);
    }
    else
    {
      Properties localProperties = new Properties();

      checkPoolConfig(paramProperties, localProperties);

      int[] arrayOfInt = new int[6];

      readPoolConfig(localProperties, arrayOfInt);

      this.m_connection_pool.setConnectionPoolInfo(arrayOfInt[0], arrayOfInt[1], arrayOfInt[2], arrayOfInt[3], arrayOfInt[4], arrayOfInt[5]);
    }

    storePoolProperties();
  }

  public static void readPoolConfig(Properties paramProperties, int[] paramArrayOfInt)
  {
    for (int i = 0; i < 6; i++) {
      paramArrayOfInt[i] = 0;
    }
    String str = paramProperties.getProperty(CONNPOOL_MIN_LIMIT);

    if (str != null) {
      paramArrayOfInt[0] = Integer.parseInt(str);
    }
    str = paramProperties.getProperty(CONNPOOL_MAX_LIMIT);

    if (str != null) {
      paramArrayOfInt[1] = Integer.parseInt(str);
    }
    str = paramProperties.getProperty(CONNPOOL_INCREMENT);

    if (str != null) {
      paramArrayOfInt[2] = Integer.parseInt(str);
    }
    str = paramProperties.getProperty(CONNPOOL_TIMEOUT);

    if (str != null) {
      paramArrayOfInt[3] = Integer.parseInt(str);
    }
    str = paramProperties.getProperty(CONNPOOL_NOWAIT);

    if ((str != null) && (str.equalsIgnoreCase("true"))) {
      paramArrayOfInt[4] = 1;
    }
    str = paramProperties.getProperty("transactions_distributed");

    if ((str != null) && (str.equalsIgnoreCase("true")))
      paramArrayOfInt[5] = 1;
  }

  private void checkPoolConfig(Properties paramProperties1, Properties paramProperties2)
    throws SQLException
  {
    String str1 = (String)paramProperties1.get("transactions_distributed");
    String str2 = (String)paramProperties1.get(CONNPOOL_NOWAIT);

    if (((str1 != null) && (!str1.equalsIgnoreCase("true"))) || ((str2 != null) && (!str2.equalsIgnoreCase("true"))) || (paramProperties1.get(CONNPOOL_MIN_LIMIT) == null) || (paramProperties1.get(CONNPOOL_MAX_LIMIT) == null) || (paramProperties1.get(CONNPOOL_INCREMENT) == null) || (Integer.decode((String)paramProperties1.get(CONNPOOL_MIN_LIMIT)).intValue() < 0) || (Integer.decode((String)paramProperties1.get(CONNPOOL_MAX_LIMIT)).intValue() < 0) || (Integer.decode((String)paramProperties1.get(CONNPOOL_INCREMENT)).intValue() < 0))
    {
      DatabaseError.throwSqlException(106, "");
    }

    Enumeration localEnumeration = paramProperties1.propertyNames();

    while (localEnumeration.hasMoreElements())
    {
      String str3 = (String)localEnumeration.nextElement();
      String str4 = paramProperties1.getProperty(str3);

      if ((str3 == "transactions_distributed") || (str3 == CONNPOOL_NOWAIT)) {
        paramProperties2.put(str3, "true"); continue;
      }
      paramProperties2.put(str3, str4);
    }
  }

  private synchronized void storePoolProperties()
    throws SQLException
  {
    Properties localProperties = getPoolConfig();

    this.m_conn_min_limit = Integer.decode(localProperties.getProperty(CONNPOOL_MIN_LIMIT)).intValue();

    this.m_conn_max_limit = Integer.decode(localProperties.getProperty(CONNPOOL_MAX_LIMIT)).intValue();

    this.m_conn_increment = Integer.decode(localProperties.getProperty(CONNPOOL_INCREMENT)).intValue();

    this.m_conn_active_size = Integer.decode(localProperties.getProperty(CONNPOOL_ACTIVE_SIZE)).intValue();

    this.m_conn_pool_size = Integer.decode(localProperties.getProperty(CONNPOOL_POOL_SIZE)).intValue();

    this.m_conn_timeout = Integer.decode(localProperties.getProperty(CONNPOOL_TIMEOUT)).intValue();

    this.m_conn_nowait = localProperties.getProperty(CONNPOOL_NOWAIT);
  }

  public synchronized Properties getPoolConfig()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }

    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    localProperties.put(CONNPOOL_IS_POOLCREATED, String.valueOf(isPoolCreated()));

    return localProperties;
  }

  public synchronized int getActiveSize()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    String str = localProperties.getProperty(CONNPOOL_ACTIVE_SIZE);
    int i = Integer.decode(str).intValue();

    return i;
  }

  public synchronized int getPoolSize()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    String str = localProperties.getProperty(CONNPOOL_POOL_SIZE);
    int i = Integer.decode(str).intValue();

    return i;
  }

  public synchronized int getTimeout()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    String str = localProperties.getProperty(CONNPOOL_TIMEOUT);
    int i = Integer.decode(str).intValue();

    return i;
  }

  public synchronized String getNoWait()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    return localProperties.getProperty(CONNPOOL_NOWAIT);
  }

  public synchronized int getMinLimit()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    String str = localProperties.getProperty(CONNPOOL_MIN_LIMIT);
    int i = Integer.decode(str).intValue();

    return i;
  }

  public synchronized int getMaxLimit()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    String str = localProperties.getProperty(CONNPOOL_MAX_LIMIT);
    int i = Integer.decode(str).intValue();

    return i;
  }

  public synchronized int getConnectionIncrement()
    throws SQLException
  {
    if (!isPoolCreated()) {
      createConnectionPool(null);
    }
    Properties localProperties = this.m_connection_pool.getConnectionPoolInfo();

    String str = localProperties.getProperty(CONNPOOL_INCREMENT);
    int i = Integer.decode(str).intValue();

    return i;
  }

  public synchronized boolean isDistributedTransEnabled()
  {
    return this.m_is_transactions_distributed == 1;
  }

  private void createConnectionPool(Properties paramProperties)
    throws SQLException
  {
    if (isPoolCreated()) {
      return;
    }
    if ((this.user == null) || (this.password == null))
    {
      DatabaseError.throwSqlException(106, " ");
    }
    else
    {
      Properties localProperties = new Properties();

      if (paramProperties != null) {
        checkPoolConfig(paramProperties, localProperties);
      }
      localProperties.put("is_connection_pooling", "true");
      localProperties.put("user", this.user);
      localProperties.put("password", this.password);
      localProperties.put("connection_pool", "connection_pool");

      if (getURL() == null) {
        makeURL();
      }

      this.m_connection_pool = ((OracleOCIConnection)this.m_oracleDriver.connect(this.url, localProperties));

      if (this.m_connection_pool == null) {
        DatabaseError.throwSqlException(67);
      }

      this.m_poolCreated = true;

      this.m_connection_pool.setConnectionPool(this);

      this.m_lconnections.put(this.m_connection_pool, this.m_connection_pool);

      storePoolProperties();

      if (paramProperties != null)
      {
        if (paramProperties.getProperty("transactions_distributed") != null)
          this.m_is_transactions_distributed = 1;
      }
    }
  }

  public synchronized boolean isPoolCreated()
  {
    return this.m_poolCreated;
  }

  public synchronized void connectionClosed(OracleOCIConnection paramOracleOCIConnection)
    throws SQLException
  {
    if (this.m_lconnections.remove(paramOracleOCIConnection) == null)
      DatabaseError.throwSqlException(1, "internal OracleOCIConnectionPool error");
  }

  public synchronized void setStmtCacheSize(int paramInt)
    throws SQLException
  {
    setStmtCacheSize(paramInt, false);
  }

  public synchronized void setStmtCacheSize(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    if (paramInt < 0) {
      DatabaseError.throwSqlException(68);
    }
    this.m_stmtCacheSize = paramInt;
    this.m_stmtClearMetaData = paramBoolean;
  }

  public synchronized int getStmtCacheSize()
  {
    return this.m_stmtCacheSize;
  }

  public synchronized boolean isStmtCacheEnabled()
  {
    return this.m_stmtCacheSize > 0;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleOCIConnectionPool
 * JD-Core Version:    0.6.0
 */