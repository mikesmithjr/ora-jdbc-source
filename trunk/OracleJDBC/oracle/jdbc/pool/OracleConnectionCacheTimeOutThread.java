package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import javax.sql.PooledConnection;
import oracle.jdbc.internal.OracleConnection;

public class OracleConnectionCacheTimeOutThread extends Thread
  implements Serializable
{
  private OracleConnectionCacheImpl occi = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  OracleConnectionCacheTimeOutThread(OracleConnectionCacheImpl paramOracleConnectionCacheImpl)
    throws SQLException
  {
    this.occi = paramOracleConnectionCacheImpl;
  }

  public void run()
  {
    long l1 = 0L;
    long l2 = 0L;
    int i = 1;
    try
    {
      while (i != 0)
      {
        if ((l1 = this.occi.getCacheTimeToLiveTimeout()) > 0L) {
          runTimeToLiveTimeOut(l1);
        }

        if ((l2 = this.occi.getCacheInactivityTimeout()) > 0L) {
          runInactivityTimeOut(l2);
        }

        sleep(this.occi.getThreadWakeUpInterval() * 1000L);

        if ((this.occi.cache != null) && ((l1 > 0L) || (l2 > 0L)))
          continue;
        i = 0;
      }
    }
    catch (SQLException localSQLException)
    {
    }
    catch (InterruptedException localInterruptedException)
    {
    }
  }

  public void runTimeToLiveTimeOut(long paramLong)
    throws SQLException
  {
    long l1 = 0L;
    long l2 = 0L;
    int i = 0;
    PooledConnection localPooledConnection = null;

    if ((i = this.occi.getActiveSize()) > 0)
    {
      Enumeration localEnumeration = this.occi.activeCache.keys();

      while (localEnumeration.hasMoreElements())
      {
        localPooledConnection = (PooledConnection)localEnumeration.nextElement();

        Connection localConnection = ((OraclePooledConnection)localPooledConnection).getLogicalHandle();

        if (localConnection != null) {
          l2 = ((OracleConnection)localConnection).getStartTime();
        }
        l1 = System.currentTimeMillis();

        if (l1 - l2 <= paramLong * 1000L)
        {
          continue;
        }

        try
        {
          ((OracleConnection)localConnection).cancel();
        }
        catch (SQLException localSQLException1)
        {
        }

        try
        {
          if (!localConnection.getAutoCommit()) {
            ((OracleConnection)localConnection).rollback();
          }

        }
        catch (SQLException localSQLException2)
        {
        }

        try
        {
          localConnection.close();
        }
        catch (SQLException localSQLException3)
        {
        }
      }
    }
  }

  public void runInactivityTimeOut(long paramLong)
    throws SQLException
  {
    long l1 = 0L;
    long l2 = 0L;
    long l3 = paramLong * 1000L;
    OraclePooledConnection localOraclePooledConnection = null;
    try
    {
      if ((this.occi.cache != null) && (this.occi.cache.size() > 0) && (this.occi.cacheSize > this.occi._MIN_LIMIT))
      {
        Enumeration localEnumeration = this.occi.cache.elements();

        while ((this.occi.cacheSize > this.occi._MIN_LIMIT) && (localEnumeration.hasMoreElements()))
        {
          localOraclePooledConnection = (OraclePooledConnection)localEnumeration.nextElement();
          l1 = localOraclePooledConnection.getLastAccessedTime();
          l2 = System.currentTimeMillis();

          if (l2 - l1 <= l3)
          {
            continue;
          }

          try
          {
            this.occi.closeSingleConnection(localOraclePooledConnection, false);
          }
          catch (SQLException localSQLException)
          {
          }
        }
      }
    }
    catch (NoSuchElementException localNoSuchElementException)
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionCacheTimeOutThread
 * JD-Core Version:    0.6.0
 */