package oracle.jdbc.pool;

import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

public abstract interface OracleConnectionCache extends DataSource
{
  public abstract void reusePooledConnection(PooledConnection paramPooledConnection)
    throws SQLException;

  public abstract void closePooledConnection(PooledConnection paramPooledConnection)
    throws SQLException;

  public abstract void close()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionCache
 * JD-Core Version:    0.6.0
 */