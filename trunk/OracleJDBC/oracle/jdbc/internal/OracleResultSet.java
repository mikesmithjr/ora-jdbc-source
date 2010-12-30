package oracle.jdbc.internal;

import java.sql.SQLException;

public abstract interface OracleResultSet extends oracle.jdbc.OracleResultSet
{
  public abstract void closeStatementOnClose()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.internal.OracleResultSet
 * JD-Core Version:    0.6.0
 */