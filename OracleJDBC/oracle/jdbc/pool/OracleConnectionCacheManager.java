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
import javax.sql.ConnectionPoolDataSource;
import oracle.jdbc.driver.DatabaseError;
import oracle.ons.ONS;
import oracle.ons.ONSException;

public class OracleConnectionCacheManager
{
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  private OracleConnectionCacheManager()
  {
    this.m_connCache = new Hashtable();
  }

  public static synchronized OracleConnectionCacheManager getConnectionCacheManagerInstance()
    throws SQLException
  {
    try
    {
      if (cacheManagerInstance == null) {
        cacheManagerInstance = new OracleConnectionCacheManager();
      }

    }
    catch (RuntimeException localRuntimeException)
    {
    }

    return cacheManagerInstance;
  }

  public String createCache(OracleDataSource paramOracleDataSource, Properties paramProperties)
    throws SQLException
  {
    String str = null;

    if ((paramOracleDataSource == null) || (!paramOracleDataSource.getConnectionCachingEnabled()))
    {
      DatabaseError.throwSqlException(137);
    }

    if (paramOracleDataSource.connCacheName != null)
    {
      str = paramOracleDataSource.connCacheName;
    }
    else
    {
      str = paramOracleDataSource.dataSourceName + "#0x" + Integer.toHexString(this.m_connCache.size());
    }

    createCache(str, paramOracleDataSource, paramProperties);

    return str;
  }

  public void createCache(String paramString, OracleDataSource paramOracleDataSource, Properties paramProperties)
    throws SQLException
  {
    if ((paramOracleDataSource == null) || (!paramOracleDataSource.getConnectionCachingEnabled()))
    {
      DatabaseError.throwSqlException(137);
    }

    if (paramString == null)
    {
      DatabaseError.throwSqlException(138);
    }

    if (this.m_connCache.containsKey(paramString))
    {
      DatabaseError.throwSqlException(140);
    }

    boolean bool = paramOracleDataSource.getFastConnectionFailoverEnabled();

    if ((bool) && (this.failoverEventHandlerThread == null))
    {
      localObject1 = paramOracleDataSource.getONSConfiguration();

      if ((localObject1 != null) && (!((String)localObject1).equals("")))
      {
        synchronized (this)
        {
          if (!isONSInitializedForRemoteSubscription)
          {
            try
            {
              AccessController.doPrivileged(new PrivilegedExceptionAction((String)localObject1)
              {
                private final String val$onsConfigStr;

                public Object run()
                  throws ONSException
                {
                  ONS localONS = new ONS(this.val$onsConfigStr);
                  return null;
                }
              });
            }
            catch (PrivilegedActionException localPrivilegedActionException) {
              DatabaseError.throwSqlException(175, localPrivilegedActionException);
            }

            isONSInitializedForRemoteSubscription = true;
          }
        }
      }

      this.failoverEventHandlerThread = new OracleFailoverEventHandlerThread();
    }

    Object localObject1 = new OracleImplicitConnectionCache(paramOracleDataSource, paramProperties);

    ((OracleImplicitConnectionCache)localObject1).cacheName = paramString;
    paramOracleDataSource.odsCache = ((OracleImplicitConnectionCache)localObject1);

    this.m_connCache.put(paramString, localObject1);

    if (bool)
    {
      checkAndStartThread(this.failoverEventHandlerThread);
    }
  }

  public void removeCache(String paramString, long paramLong)
    throws SQLException
  {
    OracleImplicitConnectionCache localOracleImplicitConnectionCache = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.remove(paramString) : null;

    if (localOracleImplicitConnectionCache != null)
    {
      localOracleImplicitConnectionCache.disableConnectionCache();

      if (paramLong > 0L)
      {
        try
        {
          Thread.currentThread(); Thread.sleep(paramLong * 1000L);
        }
        catch (InterruptedException localInterruptedException)
        {
        }

      }

      if (localOracleImplicitConnectionCache.cacheEnabledDS.getFastConnectionFailoverEnabled()) {
        cleanupFCFThreads(localOracleImplicitConnectionCache);
      }

      localOracleImplicitConnectionCache.closeConnectionCache();

      localOracleImplicitConnectionCache = null;
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  public void reinitializeCache(String paramString, Properties paramProperties)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      disableCache(paramString);
      localObject.reinitializeCacheConnections(paramProperties);
      enableCache(paramString);
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  public boolean existsCache(String paramString)
    throws SQLException
  {
    return this.m_connCache.containsKey(paramString);
  }

  public void enableCache(String paramString)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      localObject.enableConnectionCache();
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  public void disableCache(String paramString)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      localObject.disableConnectionCache();
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  public void refreshCache(String paramString, int paramInt)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      if ((paramInt == 4096) || (paramInt == 8192))
      {
        localObject.refreshCacheConnections(paramInt);
      }
      else DatabaseError.throwSqlException(68);
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  public void purgeCache(String paramString, boolean paramBoolean)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      localObject.purgeCacheConnections(paramBoolean);
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  public Properties getCacheProperties(String paramString)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      return localObject.getConnectionCacheProperties();
    }

    DatabaseError.throwSqlException(141);

    return null;
  }

  public String[] getCacheNameList()
    throws SQLException
  {
    return (String[])this.m_connCache.keySet().toArray(new String[this.m_connCache.size()]);
  }

  public int getNumberOfAvailableConnections(String paramString)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      return localObject.cacheSize;
    }

    DatabaseError.throwSqlException(141);

    return 0;
  }

  public int getNumberOfActiveConnections(String paramString)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      return localObject.getNumberOfCheckedOutConnections();
    }

    DatabaseError.throwSqlException(141);

    return 0;
  }

  public synchronized void setConnectionPoolDataSource(String paramString, ConnectionPoolDataSource paramConnectionPoolDataSource)
    throws SQLException
  {
    Object localObject = paramString != null ? (OracleImplicitConnectionCache)this.m_connCache.get(paramString) : null;

    if (localObject != null)
    {
      if (localObject.cacheSize > 0)
      {
        DatabaseError.throwSqlException(78);
      }
      else
      {
        ((OracleConnectionPoolDataSource)paramConnectionPoolDataSource).makeURL();
        ((OracleConnectionPoolDataSource)paramConnectionPoolDataSource).setURL(((OracleConnectionPoolDataSource)paramConnectionPoolDataSource).url);

        localObject.connectionPoolDS = ((OracleConnectionPoolDataSource)paramConnectionPoolDataSource);
      }
    }
    else
    {
      DatabaseError.throwSqlException(141);
    }
  }

  protected void verifyAndHandleEvent(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    Object localObject1 = null;
    String str1 = null;
    String str2 = null;
    String str3 = null;
    Object localObject2 = null;

    int i = 0;
    StringTokenizer localStringTokenizer = null;
    try
    {
      localStringTokenizer = new StringTokenizer(new String(paramArrayOfByte, "UTF-8"), "{} =", true);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }

    String str4 = null;
    String str5 = null;
    String str6 = null;

    while (localStringTokenizer.hasMoreTokens())
    {
      str5 = null;
      str4 = localStringTokenizer.nextToken();
      if ((str4.equals("=")) && (localStringTokenizer.hasMoreTokens()))
      {
        str5 = localStringTokenizer.nextToken();
      }
      else
      {
        str6 = str4;
      }

      if ((str6.equalsIgnoreCase("version")) && (str5 != null) && (!str5.equals("1.0")))
      {
        DatabaseError.throwSqlException(146);
      }

      if ((str6.equalsIgnoreCase("service")) && (str5 != null)) {
        localObject1 = str5;
      }
      if ((str6.equalsIgnoreCase("instance")) && (str5 != null) && (!str5.equals(" ")))
      {
        str1 = str5.intern();
      }

      if ((str6.equalsIgnoreCase("database")) && (str5 != null)) {
        str2 = str5.intern();
      }
      if ((str6.equalsIgnoreCase("host")) && (str5 != null)) {
        str3 = str5.intern();
      }
      if ((str6.equalsIgnoreCase("status")) && (str5 != null)) {
        localObject2 = str5;
      }
      if ((!str6.equalsIgnoreCase("card")) || (str5 == null))
        continue;
      try
      {
        i = Integer.parseInt(str5);
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }

    }

    invokeFailoverProcessingThreads(paramInt, localObject1, str1, str2, str3, localObject2, i);

    localStringTokenizer = null;
  }

  private void invokeFailoverProcessingThreads(int paramInt1, String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, int paramInt2)
    throws SQLException
  {
    OracleImplicitConnectionCache localOracleImplicitConnectionCache = null;
    int i = 0;
    int j = 0;

    if (paramInt1 == 256) {
      i = 1;
    }
    if (paramInt1 == 512) {
      j = 1;
    }
    Iterator localIterator = this.m_connCache.values().iterator();

    while (localIterator.hasNext())
    {
      localOracleImplicitConnectionCache = (OracleImplicitConnectionCache)localIterator.next();

      if (((i == 0) || (!paramString1.equalsIgnoreCase(localOracleImplicitConnectionCache.dataSourceServiceName))) && (j == 0))
      {
        continue;
      }
      OracleFailoverWorkerThread localOracleFailoverWorkerThread = new OracleFailoverWorkerThread(localOracleImplicitConnectionCache, paramInt1, paramString2, paramString3, paramString4, paramString5, paramInt2);

      checkAndStartThread(localOracleFailoverWorkerThread);

      localOracleImplicitConnectionCache.failoverWorkerThread = localOracleFailoverWorkerThread;
    }
  }

  protected void checkAndStartThread(Thread paramThread)
    throws SQLException
  {
    try
    {
      if (!paramThread.isAlive())
      {
        paramThread.setDaemon(true);
        paramThread.start();
      }
    }
    catch (IllegalThreadStateException localIllegalThreadStateException)
    {
    }
  }

  protected boolean failoverEnabledCacheExists()
  {
    return this.failoverEnabledCacheCount > 0;
  }

  protected void parseRuntimeLoadBalancingEvent(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    OracleImplicitConnectionCache localOracleImplicitConnectionCache = null;
    Enumeration localEnumeration = this.m_connCache.elements();

    while (localEnumeration.hasMoreElements())
    {
      try
      {
        localOracleImplicitConnectionCache = (OracleImplicitConnectionCache)localEnumeration.nextElement();
        if (paramString.equalsIgnoreCase(localOracleImplicitConnectionCache.dataSourceServiceName))
        {
          if (paramArrayOfByte == null)
            localOracleImplicitConnectionCache.zapRLBInfo();
          else
            retrieveServiceMetrics(localOracleImplicitConnectionCache, paramArrayOfByte);
        }
      }
      catch (Exception localException)
      {
      }
    }
  }

  private void retrieveServiceMetrics(OracleImplicitConnectionCache paramOracleImplicitConnectionCache, byte[] paramArrayOfByte)
    throws SQLException
  {
    StringTokenizer localStringTokenizer = null;
    String str1 = null;
    String str2 = null;
    int i = 0;
    int j = 0;
    int k = 0;
    try
    {
      localStringTokenizer = new StringTokenizer(new String(paramArrayOfByte, "UTF-8"), "{} =", true);
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }

    String str3 = null;
    String str4 = null;
    String str5 = null;

    while (localStringTokenizer.hasMoreTokens())
    {
      str4 = null;
      str3 = localStringTokenizer.nextToken();

      if ((str3.equals("=")) && (localStringTokenizer.hasMoreTokens()))
      {
        str4 = localStringTokenizer.nextToken();
      } else {
        if (str3.equals("}"))
        {
          if (k == 0)
            continue;
          paramOracleImplicitConnectionCache.updateDatabaseInstance(str2, str1, i, j);
          k = 0; continue;
        }

        if ((str3.equals("{")) || (str3.equals(" ")))
        {
          continue;
        }

        str5 = str3;
        k = 1;
      }

      if ((str5.equalsIgnoreCase("version")) && (str4 != null))
      {
        if (!str4.equals("1.0"))
        {
          DatabaseError.throwSqlException(146);
        }
      }

      if ((str5.equalsIgnoreCase("database")) && (str4 != null)) {
        str2 = str4.intern();
      }
      if ((str5.equalsIgnoreCase("instance")) && (str4 != null)) {
        str1 = str4.intern();
      }
      if ((str5.equalsIgnoreCase("percent")) && (str4 != null))
      {
        try
        {
          i = Integer.parseInt(str4);
          if (i == 0) i = 1;

        }
        catch (NumberFormatException localNumberFormatException)
        {
        }

      }

      if ((!str5.equalsIgnoreCase("flag")) || (str4 == null))
        continue;
      if (str4.equalsIgnoreCase("good")) {
        j = 1; continue;
      }if (str4.equalsIgnoreCase("violating")) {
        j = 3; continue;
      }if (str4.equalsIgnoreCase("NO_DATA")) {
        j = 4; continue;
      }if (str4.equalsIgnoreCase("UNKNOWN")) {
        j = 2; continue;
      }if (str4.equalsIgnoreCase("BLOCKED")) {
        j = 5;
      }

    }

    paramOracleImplicitConnectionCache.processDatabaseInstances();
  }

  protected void cleanupFCFThreads(OracleImplicitConnectionCache paramOracleImplicitConnectionCache)
    throws SQLException
  {
    cleanupFCFWorkerThread(paramOracleImplicitConnectionCache);
    paramOracleImplicitConnectionCache.cleanupRLBThreads();

    if (this.failoverEnabledCacheCount <= 0) {
      cleanupFCFEventHandlerThread();
    }

    this.failoverEnabledCacheCount -= 1;
  }

  protected void cleanupFCFWorkerThread(OracleImplicitConnectionCache paramOracleImplicitConnectionCache)
    throws SQLException
  {
    if (paramOracleImplicitConnectionCache.failoverWorkerThread != null)
    {
      try
      {
        if (paramOracleImplicitConnectionCache.failoverWorkerThread.isAlive()) {
          paramOracleImplicitConnectionCache.failoverWorkerThread.join();
        }

      }
      catch (InterruptedException localInterruptedException)
      {
      }

      paramOracleImplicitConnectionCache.failoverWorkerThread = null;
    }
  }

  protected void cleanupFCFEventHandlerThread()
    throws SQLException
  {
    if (this.failoverEventHandlerThread != null)
    {
      try
      {
        this.failoverEventHandlerThread.interrupt();
      }
      catch (Exception localException)
      {
      }

      this.failoverEventHandlerThread = null;
    }
  }

  public boolean isFatalConnectionError(SQLException paramSQLException)
  {
    int i = 0;
    int j = paramSQLException.getErrorCode();

    if ((j == 3113) || (j == 3114) || (j == 1033) || (j == 1034) || (j == 1089) || (j == 1090) || (j == 17002))
    {
      i = 1;
    }

    if ((i == 0) && (this.fatalErrorCodes != null))
    {
      for (int k = 0; k < this.fatalErrorCodes.length; k++) {
        if (j != this.fatalErrorCodes[k])
          continue;
        i = 1;
        break;
      }
    }

    return i;
  }

  public synchronized void setConnectionErrorCodes(int[] paramArrayOfInt)
    throws SQLException
  {
    if (paramArrayOfInt != null)
      this.fatalErrorCodes = paramArrayOfInt;
  }

  public int[] getConnectionErrorCodes()
    throws SQLException
  {
    return this.fatalErrorCodes;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionCacheManager
 * JD-Core Version:    0.6.0
 */