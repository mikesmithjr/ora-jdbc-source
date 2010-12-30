package oracle.jdbc;

import java.sql.ParameterMetaData;
import java.sql.SQLException;

public abstract interface OracleParameterMetaData extends ParameterMetaData
{
  public static final int parameterNoNulls = 0;
  public static final int parameterNullable = 1;
  public static final int parameterNullableUnknown = 2;
  public static final int parameterModeUnknown = 0;
  public static final int parameterModeIn = 1;
  public static final int parameterModeInOut = 2;
  public static final int parameterModeOut = 4;

  public abstract int getParameterCount()
    throws SQLException;

  public abstract int isNullable(int paramInt)
    throws SQLException;

  public abstract boolean isSigned(int paramInt)
    throws SQLException;

  public abstract int getPrecision(int paramInt)
    throws SQLException;

  public abstract int getScale(int paramInt)
    throws SQLException;

  public abstract int getParameterType(int paramInt)
    throws SQLException;

  public abstract String getParameterTypeName(int paramInt)
    throws SQLException;

  public abstract String getParameterClassName(int paramInt)
    throws SQLException;

  public abstract int getParameterMode(int paramInt)
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.OracleParameterMetaData
 * JD-Core Version:    0.6.0
 */