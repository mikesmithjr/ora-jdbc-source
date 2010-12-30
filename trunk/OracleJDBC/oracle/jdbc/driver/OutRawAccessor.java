package oracle.jdbc.driver;

import java.sql.SQLException;

class OutRawAccessor extends RawCommonAccessor
{
  static final int MAXLENGTH_NEW = 32767;
  static final int MAXLENGTH_OLD = 32767;

  OutRawAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2)
    throws SQLException
  {
    init(paramOracleStatement, 23, 23, paramShort, true);
    initForDataAccess(paramInt2, paramInt1, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    if (this.statement.connection.getVersionNumber() >= 8000)
      this.internalTypeMaxLength = 32767;
    else {
      this.internalTypeMaxLength = 32767;
    }
    if ((paramInt2 > 0) && (paramInt2 < this.internalTypeMaxLength)) {
      this.internalTypeMaxLength = paramInt2;
    }
    this.byteLength = this.internalTypeMaxLength;
  }

  byte[] getBytes(int paramInt) throws SQLException
  {
    byte[] arrayOfByte = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];
      int j = this.columnIndex + this.byteLength * paramInt;

      arrayOfByte = new byte[i];

      System.arraycopy(this.rowSpaceByte, j, arrayOfByte, 0, i);
    }

    return arrayOfByte;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OutRawAccessor
 * JD-Core Version:    0.6.0
 */