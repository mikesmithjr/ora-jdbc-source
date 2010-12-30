package oracle.jdbc.driver;

import java.sql.SQLException;

class T2CResultSetAccessor extends ResultSetAccessor
{
  T2CResultSetAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1 * 2, paramShort, paramInt2, paramBoolean);
  }

  T2CResultSetAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1 * 2, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort);
  }

  byte[] getBytes(int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];
      int j = ((T2CConnection)this.statement.connection).byteAlign;
      int k = this.columnIndex + (j - 1) & (j - 1 ^ 0xFFFFFFFF);

      int m = k + i * paramInt;

      arrayOfByte = new byte[i];
      System.arraycopy(this.rowSpaceByte, m, arrayOfByte, 0, i);
    }

    return arrayOfByte;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T2CResultSetAccessor
 * JD-Core Version:    0.6.0
 */