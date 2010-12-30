package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;

/** @deprecated */
public class OracleConnectionCacheImpl extends OracleDataSource
  implements OracleConnectionCache, Serializable, Referenceable
{
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  /** @deprecated */
  public OracleConnectionCacheImpl()
    throws SQLException
  {
    this(null);
  }

  /** @deprecated */
  public OracleConnectionCacheImpl(ConnectionPoolDataSource paramConnectionPoolDataSource)
    throws SQLException
  {
    this.cacheScheme = 1;

    this.cpds = paramConnectionPoolDataSource;

    this.ocel = new OracleConnectionEventListener(this);

    this.dataSourceName = "OracleConnectionCacheImpl";
    this.isOracleDataSource = false;
  }

  /** @deprecated */
  public synchronized void setConnectionPoolDataSource(ConnectionPoolDataSource paramConnectionPoolDataSource)
    throws SQLException
  {
    if (this.cacheSize > 0) {
      DatabaseError.throwSqlException(78);
    }
    this.cpds = paramConnectionPoolDataSource;
  }

  /** @deprecated */
  public Connection getConnection()
    throws SQLException
  {
    return getConnection(this.user, this.password);
  }

  /** @deprecated */
  public Connection getConnection(String paramString1, String paramString2)
    throws SQLException
  {
    Connection localConnection = null;

    PooledConnection localPooledConnection = getPooledConnection(paramString1, paramString2);

    if (localPooledConnection != null)
    {
      localConnection = localPooledConnection.getConnection();
    }

    if (localConnection != null) {
      ((OracleConnection)localConnection).setStartTime(System.currentTimeMillis());
    }
    return localConnection;
  }

  protected PooledConnection getPooledConnection(String paramString1, String paramString2)
    throws SQLException
  {
    PooledConnection localPooledConnection = null;
    int i = 0;
    int j = 0;
    long l = 9223372036854775807L;
    Properties localProperties = null;

    synchronized (this)
    {
      if (!this.cache.empty())
      {
        checkCredentials(paramString1, paramString2);

        localPooledConnection = removeConnectionFromCache();
      }
      else if ((this.cacheSize < this._MAX_LIMIT) || (this.cacheScheme == 1))
      {
        String str1 = null;
        String str2 = null;

        if (this.cpds != null)
        {
          str1 = ((OracleConnectionPoolDataSource)this.cpds).getUser();
          str2 = ((OracleConnectionPoolDataSource)this.cpds).getPassword();
        }

        if ((this.cacheSize > 0) && (paramString1 != null) && (!paramString1.equalsIgnoreCase(str1)))
        {
          DatabaseError.throwSqlException(79);
        }

        if ((this.cacheSize > 0) && (paramString2 != null) && (!paramString2.equalsIgnoreCase(str2)))
        {
          DatabaseError.throwSqlException(79);
        }

        i = 1;
        localProperties = new Properties();
        if (this.url != null)
          localProperties.setProperty("connection_url", this.url);
        if (this.user != null)
          localProperties.setProperty("user", this.user);
        else if ((this.cpds != null) && (((OracleDataSource)this.cpds).user != null)) {
          localProperties.setProperty("user", ((OracleDataSource)this.cpds).user);
        }
        if (this.password != null)
          localProperties.setProperty("password", this.password);
        else if ((this.cpds != null) && (((OracleDataSource)this.cpds).password != null)) {
          localProperties.setProperty("password", ((OracleDataSource)this.cpds).password);
        }
        if ((this.stmtCacheSize == 0) && (this.cpds != null) && (((OracleDataSource)this.cpds).maxStatements != 0))
        {
          localProperties.setProperty("stmt_cache_size", "" + ((OracleDataSource)this.cpds).maxStatements);
        }
        else
        {
          localProperties.setProperty("stmt_cache_size", "" + this.stmtCacheSize);

          localProperties.setProperty("stmt_cache_clear_metadata", "" + this.stmtClearMetaData);

          localProperties.setProperty("ImplicitStatementCachingEnabled", "" + this.implicitCachingEnabled);

          localProperties.setProperty("ExplicitStatementCachingEnabled", "" + this.explicitCachingEnabled);
        }

        if (this.loginTimeout != 0) {
          localProperties.setProperty("LoginTimeout", "" + this.loginTimeout);
        }
        synchronized (this.CACHE_SIZE_LOCK) {
          this.cacheSize += 1;
        }
        if (this.cpds == null)
        {
          initializeConnectionPoolDataSource();
        }

      }
      else if (this.cacheScheme != 3)
      {
        checkCredentials(paramString1, paramString2);

        l = System.currentTimeMillis();
        j = 1;
      }

    }

    if (i != 0)
    {
      try
      {
        localPooledConnection = getNewPoolOrXAConnection(localProperties);
      }
      catch (SQLException ) {
        synchronized (this.CACHE_SIZE_LOCK)
        {
          this.cacheSize -= 1;
        }

        throw ???;
      }
    }
    else {
      if (j != 0)
      {
        while ((localPooledConnection == null) && (this.cache.empty()))
        {
          synchronized (this)
          {
            if ((this.cacheFixedWaitTimeOut > 0L) && (System.currentTimeMillis() - l > this.cacheFixedWaitTimeOut * 1000L))
            {
              DatabaseError.throwSqlException(126);
            }

          }

          synchronized (this.cache)
          {
            try
            {
              this.cache.wait((this.fixedWaitIdleTime == -1L ? 30L : this.fixedWaitIdleTime) * 1000L);
            }
            catch (InterruptedException localInterruptedException)
            {
            }

            if (!this.cache.empty()) {
              localPooledConnection = removeConnectionFromCache();
            }
          }
        }

      }

      if ((localPooledConnection != null) && (this.stmtCacheSize > 0))
      {
        if ((!this.explicitCachingEnabled) && (!this.implicitCachingEnabled))
        {
          ((OraclePooledConnection)localPooledConnection).setStmtCacheSize(this.stmtCacheSize);
        }
        else {
          ((OraclePooledConnection)localPooledConnection).setStatementCacheSize(this.stmtCacheSize);
          ((OraclePooledConnection)localPooledConnection).setExplicitCachingEnabled(this.explicitCachingEnabled);

          ((OraclePooledConnection)localPooledConnection).setImplicitCachingEnabled(this.implicitCachingEnabled);
        }
      }

    }

    if (localPooledConnection != null)
    {
      if (i == 0)
      {
        ((OraclePooledConnection)localPooledConnection).physicalConn.setDefaultRowPrefetch(10);
        ((OraclePooledConnection)localPooledConnection).physicalConn.setDefaultExecuteBatch(1);
      }

      localPooledConnection.addConnectionEventListener(this.ocel);

      this.activeCache.put(localPooledConnection, localPooledConnection);

      synchronized (this)
      {
        this.activeSize = this.activeCache.size();
      }

    }

    return localPooledConnection;
  }

  PooledConnection getNewPoolOrXAConnection(Properties paramProperties)
    throws SQLException
  {
    PooledConnection localPooledConnection = ((OracleConnectionPoolDataSource)this.cpds).getPooledConnection(paramProperties);

    return localPooledConnection;
  }

  /** @deprecated */
  public void reusePooledConnection(PooledConnection paramPooledConnection)
    throws SQLException
  {
    detachSingleConnection(paramPooledConnection);

    if ((this.cache.size() >= this._MAX_LIMIT) && (this.cacheScheme == 1))
    {
      closeSingleConnection(paramPooledConnection, false);
    }
    else putConnectionToCache(paramPooledConnection);
  }

  /** @deprecated */
  public void closePooledConnection(PooledConnection paramPooledConnection)
    throws SQLException
  {
    detachSingleConnection(paramPooledConnection);

    closeSingleConnection(paramPooledConnection, false);
  }

  private void detachSingleConnection(PooledConnection paramPooledConnection)
  {
    paramPooledConnection.removeConnectionEventListener(this.ocel);

    this.activeCache.remove(paramPooledConnection);

    this.activeSize = this.activeCache.size();
  }

  /** @deprecated */
  public void closeSingleConnection(PooledConnection paramPooledConnection)
    throws SQLException
  {
    closeSingleConnection(paramPooledConnection, true);
  }

  final void closeSingleConnection(PooledConnection paramPooledConnection, boolean paramBoolean)
    throws SQLException
  {
    if ((!removeConnectionFromCache(paramPooledConnection)) && (paramBoolean)) {
      return;
    }

    try
    {
      paramPooledConnection.close();
    }
    catch (SQLException localSQLException)
    {
      this.warning = DatabaseError.addSqlWarning(this.warning, new SQLWarning(localSQLException.getMessage()));
    }

    synchronized (this.CACHE_SIZE_LOCK)
    {
      this.cacheSize -= 1;
    }
  }

  /** @deprecated */
  public synchronized void close()
    throws SQLException
  {
    closeConnections();

    this.cache = null;
    this.activeCache = null;
    this.ocel = null;
    this.cpds = null;
    this.timeOutThread = null;

    clearWarnings();
  }

  public void closeConnections()
  {
    Enumeration localEnumeration = this.activeCache.keys();

    while (localEnumeration.hasMoreElements())
    {
      try
      {
        OraclePooledConnection localOraclePooledConnection2 = (OraclePooledConnection)localEnumeration.nextElement();
        OraclePooledConnection localOraclePooledConnection1 = (OraclePooledConnection)this.activeCache.get(localOraclePooledConnection2);

        if (localOraclePooledConnection1 == null) {
          continue;
        }
        OracleConnection localOracleConnection = (OracleConnection)localOraclePooledConnection1.getLogicalHandle();

        localOracleConnection.close();
      }
      catch (Exception localException)
      {
      }

    }

    while (!this.cache.empty())
    {
      try
      {
        closeSingleConnection((PooledConnection)this.cache.peek(), false);
      }
      catch (SQLException localSQLException)
      {
      }
    }
  }

  public synchronized void setConnectionCleanupInterval(long paramLong)
    throws SQLException
  {
    if (paramLong > 0L)
      this.cleanupInterval = paramLong;
  }

  public long getConnectionCleanupInterval()
    throws SQLException
  {
    return this.cleanupInterval;
  }

  public synchronized void setConnectionErrorCodes(int[] paramArrayOfInt)
    throws SQLException
  {
    if (paramArrayOfInt != null)
      paramArrayOfInt = paramArrayOfInt;
  }

  public int[] getConnectionErrorCodes()
    throws SQLException
  {
    return this.fatalErrorCodes;
  }

  public boolean isFatalConnectionError(SQLException paramSQLException)
  {
    if (this.cleanupInterval < 0L) {
      return false;
    }
    int i = 0;
    int j = paramSQLException.getErrorCode();

    if ((j == 3113) || (j == 3114) || (j == 1033) || (j == 1034) || (j == 1089) || (j == 1090) || (j == 17002))
    {
      i = 1;
    }
    else if (this.fatalErrorCodes != null)
    {
      for (int k = 0; k < this.fatalErrorCodes.length; k++) {
        if (j == this.fatalErrorCodes[k]) {
          i = 1;
        }
      }
    }
    return i;
  }

  /** @deprecated */
  public synchronized void setMinLimit(int paramInt)
    throws SQLException
  {
    if ((paramInt < 0) || (paramInt > this._MAX_LIMIT)) {
      DatabaseError.throwSqlException(68);
    }
    this._MIN_LIMIT = paramInt;
    if (this.cpds == null)
    {
      initializeConnectionPoolDataSource();
    }

    if (this.cacheSize < this._MIN_LIMIT)
    {
      Properties localProperties = new Properties();
      if (this.url != null)
        localProperties.setProperty("connection_url", this.url);
      if (this.user != null)
        localProperties.setProperty("user", this.user);
      if (this.password != null) {
        localProperties.setProperty("password", this.password);
      }
      if ((this.stmtCacheSize == 0) && (this.maxStatements != 0))
      {
        localProperties.setProperty("stmt_cache_size", "" + this.maxStatements);
      }
      else
      {
        localProperties.setProperty("stmt_cache_size", "" + this.stmtCacheSize);
        localProperties.setProperty("stmt_cache_clear_metadata", "" + this.stmtClearMetaData);

        localProperties.setProperty("ImplicitStatementCachingEnabled", "" + this.implicitCachingEnabled);

        localProperties.setProperty("ExplicitStatementCachingEnabled", "" + this.explicitCachingEnabled);
      }

      localProperties.setProperty("LoginTimeout", "" + this.loginTimeout);

      for (int i = this.cacheSize; i < this._MIN_LIMIT; i++)
      {
        PooledConnection localPooledConnection = getNewPoolOrXAConnection(localProperties);
        putConnectionToCache(localPooledConnection);
      }

      this.cacheSize = this._MIN_LIMIT;
    }
  }

  void initializeConnectionPoolDataSource()
    throws SQLException
  {
    if (this.cpds == null)
    {
      if ((this.user == null) || (this.password == null)) {
        DatabaseError.throwSqlException(79);
      }
      this.cpds = new OracleConnectionPoolDataSource();

      copy((OracleDataSource)this.cpds);
    }
  }

  /** @deprecated */
  public synchronized int getMinLimit()
  {
    return this._MIN_LIMIT;
  }

  /** @deprecated */
  public synchronized void setMaxLimit(int paramInt)
    throws SQLException
  {
    if ((paramInt < 0) || (paramInt < this._MIN_LIMIT)) {
      DatabaseError.throwSqlException(68);
    }
    this._MAX_LIMIT = paramInt;

    if ((this.cacheSize > this._MAX_LIMIT) && (this.cacheScheme != 1))
    {
      for (int i = this._MAX_LIMIT; i < this.cacheSize; i++)
      {
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
  public synchronized int getMaxLimit()
  {
    return this._MAX_LIMIT;
  }

  /** @deprecated */
  public synchronized int getCacheScheme()
  {
    return this.cacheScheme;
  }

  /** @deprecated */
  public synchronized void setCacheScheme(int paramInt)
    throws SQLException
  {
    if ((paramInt == 1) || (paramInt == 3) || (paramInt == 2))
    {
      this.cacheScheme = paramInt;

      return;
    }

    DatabaseError.throwSqlException(68);
  }

  /** @deprecated */
  public synchronized void setCacheScheme(String paramString)
    throws SQLException
  {
    if (paramString.equalsIgnoreCase("DYNAMIC_SCHEME"))
      this.cacheScheme = 1;
    else if (paramString.equalsIgnoreCase("FIXED_RETURN_NULL_SCHEME"))
      this.cacheScheme = 3;
    else if (paramString.equalsIgnoreCase("FIXED_WAIT_SCHEME"))
      this.cacheScheme = 2;
    else
      DatabaseError.throwSqlException(68);
  }

  /** @deprecated */
  public synchronized int getActiveSize()
  {
    return this.activeSize;
  }

  /** @deprecated */
  public synchronized int getCacheSize()
  {
    return this.cacheSize;
  }

  /** @deprecated */
  public synchronized void setCacheTimeToLiveTimeout(long paramLong)
    throws SQLException
  {
    if (this.timeOutThread == null) {
      this.timeOutThread = new OracleConnectionCacheTimeOutThread(this);
    }

    if (paramLong <= 0L)
    {
      this.cacheTTLTimeOut = -1L;
      this.warning = DatabaseError.addSqlWarning(this.warning, 111);
    }
    else
    {
      this.cacheTTLTimeOut = paramLong;

      checkAndStartTimeOutThread();
    }
  }

  /** @deprecated */
  public synchronized void setCacheInactivityTimeout(long paramLong)
    throws SQLException
  {
    if (this.timeOutThread == null) {
      this.timeOutThread = new OracleConnectionCacheTimeOutThread(this);
    }

    if (paramLong <= 0L)
    {
      this.cacheInactivityTimeOut = -1L;
      this.warning = DatabaseError.addSqlWarning(this.warning, 124);
    }
    else
    {
      this.cacheInactivityTimeOut = paramLong;

      checkAndStartTimeOutThread();
    }
  }

  /** @deprecated */
  public synchronized void setCacheFixedWaitTimeout(long paramLong)
    throws SQLException
  {
    if (paramLong <= 0L)
    {
      this.cacheFixedWaitTimeOut = -1L;
      this.warning = DatabaseError.addSqlWarning(this.warning, 127);
    }
    else
    {
      this.cacheFixedWaitTimeOut = paramLong;
    }
  }

  /** @deprecated */
  public long getCacheTimeToLiveTimeout()
    throws SQLException
  {
    return this.cacheTTLTimeOut;
  }

  /** @deprecated */
  public long getCacheInactivityTimeout()
    throws SQLException
  {
    return this.cacheInactivityTimeOut;
  }

  /** @deprecated */
  public long getCacheFixedWaitTimeout()
    throws SQLException
  {
    return this.cacheFixedWaitTimeOut;
  }

  /** @deprecated */
  public synchronized void setThreadWakeUpInterval(long paramLong)
    throws SQLException
  {
    if (paramLong <= 0L)
    {
      this.threadInterval = 900L;
      this.warning = DatabaseError.addSqlWarning(this.warning, 112);
    }
    else
    {
      this.threadInterval = paramLong;
    }

    if (((this.cacheTTLTimeOut > 0L) && (this.threadInterval > this.cacheTTLTimeOut)) || ((this.cacheInactivityTimeOut > 0L) && (this.threadInterval > this.cacheInactivityTimeOut)))
    {
      this.warning = DatabaseError.addSqlWarning(this.warning, 113);
    }
  }

  /** @deprecated */
  public long getThreadWakeUpInterval()
    throws SQLException
  {
    return this.threadInterval;
  }

  private void checkAndStartTimeOutThread()
    throws SQLException
  {
    try
    {
      if (!this.timeOutThread.isAlive())
      {
        this.timeOutThread.setDaemon(true);
        this.timeOutThread.start();
      }
    }
    catch (IllegalThreadStateException localIllegalThreadStateException)
    {
    }
  }

  /** @deprecated */
  public SQLWarning getWarnings()
    throws SQLException
  {
    return this.warning;
  }

  /** @deprecated */
  public void clearWarnings()
    throws SQLException
  {
    this.warning = null;
  }

  private final void checkCredentials(String paramString1, String paramString2)
    throws SQLException
  {
    String str1 = null;
    String str2 = null;

    if (this.cpds != null)
    {
      str1 = ((OracleConnectionPoolDataSource)this.cpds).getUser();
      str2 = ((OracleConnectionPoolDataSource)this.cpds).getPassword();
    }

    if (((paramString1 != null) && (!paramString1.equals(str1))) || ((paramString2 != null) && (!paramString2.equals(str2))))
    {
      DatabaseError.throwSqlException(79);
    }
  }

  public synchronized Reference getReference()
    throws NamingException
  {
    Reference localReference = new Reference(getClass().getName(), "oracle.jdbc.pool.OracleDataSourceFactory", null);

    super.addRefProperties(localReference);

    if (this._MIN_LIMIT != _DEFAULT_MIN_LIMIT) {
      localReference.add(new StringRefAddr("minLimit", Integer.toString(this._MIN_LIMIT)));
    }
    if (this._MAX_LIMIT != _DEFAULT_MAX_LIMIT) {
      localReference.add(new StringRefAddr("maxLimit", Integer.toString(this._MAX_LIMIT)));
    }
    if (this.cacheScheme != 1) {
      localReference.add(new StringRefAddr("cacheScheme", Integer.toString(this.cacheScheme)));
    }

    return localReference;
  }

  /** @deprecated */
  public synchronized void setStmtCacheSize(int paramInt)
    throws SQLException
  {
    setStmtCacheSize(paramInt, false);
  }

  /** @deprecated */
  public synchronized void setStmtCacheSize(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    if (paramInt < 0) {
      DatabaseError.throwSqlException(68);
    }
    this.stmtCacheSize = paramInt;
    this.stmtClearMetaData = paramBoolean;
  }

  /** @deprecated */
  public synchronized int getStmtCacheSize()
  {
    return this.stmtCacheSize;
  }

  synchronized boolean isStmtCacheEnabled()
  {
    return this.stmtCacheSize > 0;
  }

  private void putConnectionToCache(PooledConnection paramPooledConnection)
    throws SQLException
  {
    ((OraclePooledConnection)paramPooledConnection).setLastAccessedTime(System.currentTimeMillis());
    this.cache.push(paramPooledConnection);

    synchronized (this.cache)
    {
      this.cache.notify();
    }
  }

  private PooledConnection removeConnectionFromCache()
    throws SQLException
  {
    return (PooledConnection)this.cache.pop();
  }

  private boolean removeConnectionFromCache(PooledConnection paramPooledConnection)
    throws SQLException
  {
    return this.cache.removeElement(paramPooledConnection);
  }

  /** @deprecated */
  public synchronized void setCacheFixedWaitIdleTime(long paramLong)
    throws SQLException
  {
    if (this.cacheScheme == 2)
    {
      if (paramLong <= 0L)
      {
        DatabaseError.addSqlWarning(this.warning, 68);

        this.fixedWaitIdleTime = 30L;
      }
      else {
        this.fixedWaitIdleTime = paramLong;
      }
    }
    else DatabaseError.addSqlWarning(this.warning, new SQLWarning("Caching scheme is not FIXED_WAIT_SCHEME"));
  }

  /** @deprecated */
  public long getCacheFixedWaitIdleTime()
    throws SQLException
  {
    return this.fixedWaitIdleTime == -1L ? 30L : this.fixedWaitIdleTime;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionCacheImpl
 * JD-Core Version:    0.6.0
 */