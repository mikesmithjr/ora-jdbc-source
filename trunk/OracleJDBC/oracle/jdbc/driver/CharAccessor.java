package oracle.jdbc.driver;

import java.sql.SQLException;

class CharAccessor extends CharCommonAccessor
{
  CharAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    int i = 2000;

    if (paramOracleStatement.sqlKind == 1) {
      i = 32512;
    }
    init(paramOracleStatement, 96, 9, paramInt1, paramShort, paramInt2, paramBoolean, i, 255);
  }

  CharAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    int i = 2000;

    if (paramOracleStatement.sqlKind == 1) {
      i = 32512;
    }
    init(paramOracleStatement, 96, 9, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, i, 255);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.CharAccessor
 * JD-Core Version:    0.6.0
 */