package oracle.jdbc;

import java.sql.SQLException;

public abstract interface StructMetaData extends OracleResultSetMetaData
{
  public abstract String getAttributeJavaName(int paramInt)
    throws SQLException;

  public abstract String getOracleColumnClassName(int paramInt)
    throws SQLException;

  public abstract boolean isInherited(int paramInt)
    throws SQLException;

  public abstract int getLocalColumnCount()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.StructMetaData
 * JD-Core Version:    0.6.0
 */