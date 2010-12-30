package oracle.jdbc;

import java.sql.SQLException;
import java.sql.Savepoint;

public abstract interface OracleSavepoint extends Savepoint
{
  public abstract int getSavepointId()
    throws SQLException;

  public abstract String getSavepointName()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.OracleSavepoint
 * JD-Core Version:    0.6.0
 */