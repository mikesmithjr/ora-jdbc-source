package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.SQLException;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

class OracleConnectionCacheEventListener
  implements ConnectionEventListener, Serializable
{
  static final int CONNECTION_CLOSED_EVENT = 101;
  static final int CONNECTION_ERROROCCURED_EVENT = 102;
  protected OracleImplicitConnectionCache implicitCache = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleConnectionCacheEventListener()
  {
    this(null);
  }

  public OracleConnectionCacheEventListener(OracleImplicitConnectionCache paramOracleImplicitConnectionCache)
  {
    this.implicitCache = paramOracleImplicitConnectionCache;
  }

  public synchronized void connectionClosed(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      if (this.implicitCache != null)
      {
        this.implicitCache.reusePooledConnection((PooledConnection)paramConnectionEvent.getSource());
      }
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public synchronized void connectionErrorOccurred(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      if (this.implicitCache != null)
      {
        this.implicitCache.closePooledConnection((PooledConnection)paramConnectionEvent.getSource());
      }
    }
    catch (SQLException localSQLException)
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionCacheEventListener
 * JD-Core Version:    0.6.0
 */