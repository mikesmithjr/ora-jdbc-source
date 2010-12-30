package oracle.jdbc.internal;

import java.sql.SQLException;

public abstract interface OraclePreparedStatement extends oracle.jdbc.OraclePreparedStatement, OracleStatement
{
  public abstract void setCheckBindTypes(boolean paramBoolean);

  public abstract void setInternalBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws SQLException;

  public abstract void enterImplicitCache()
    throws SQLException;

  public abstract void enterExplicitCache()
    throws SQLException;

  public abstract void exitImplicitCacheToActive()
    throws SQLException;

  public abstract void exitExplicitCacheToActive()
    throws SQLException;

  public abstract void exitImplicitCacheToClose()
    throws SQLException;

  public abstract void exitExplicitCacheToClose()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.internal.OraclePreparedStatement
 * JD-Core Version:    0.6.0
 */