package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.SQLException;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

public class OracleConnectionEventListener
  implements ConnectionEventListener, Serializable
{
  static final int _CLOSED_EVENT = 1;
  static final int _ERROROCCURED_EVENT = 2;
  private DataSource dataSource = null;

  protected long lastCleanupTime = -1L;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleConnectionEventListener()
  {
    this.dataSource = null;
  }

  public OracleConnectionEventListener(DataSource paramDataSource)
  {
    this.dataSource = paramDataSource;
  }

  public void setDataSource(DataSource paramDataSource)
  {
    this.dataSource = paramDataSource;
  }

  public void connectionClosed(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      if ((this.dataSource != null) && ((this.dataSource instanceof OracleConnectionCache)))
        ((OracleConnectionCache)this.dataSource).reusePooledConnection((PooledConnection)paramConnectionEvent.getSource());
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public void connectionErrorOccurred(ConnectionEvent paramConnectionEvent)
  {
    try
    {
      if ((this.dataSource != null) && ((this.dataSource instanceof OracleConnectionCache)))
        ((OracleConnectionCache)this.dataSource).closePooledConnection((PooledConnection)paramConnectionEvent.getSource());
    }
    catch (SQLException localSQLException)
    {
      cleanupInvalidConnections(localSQLException);
    }
  }

  protected synchronized void cleanupInvalidConnections(SQLException paramSQLException)
  {
    try
    {
      if ((this.dataSource != null) && ((this.dataSource instanceof OracleConnectionCacheImpl)))
      {
        long l1 = System.currentTimeMillis();
        long l2 = ((OracleConnectionCacheImpl)this.dataSource).getConnectionCleanupInterval();

        if (l1 - this.lastCleanupTime > l2 * 1000L)
        {
          if (((OracleConnectionCacheImpl)this.dataSource).isFatalConnectionError(paramSQLException)) {
            ((OracleConnectionCacheImpl)this.dataSource).closeConnections();
          }
          this.lastCleanupTime = System.currentTimeMillis();
        }
      }
    }
    catch (Exception localException)
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionEventListener
 * JD-Core Version:    0.6.0
 */