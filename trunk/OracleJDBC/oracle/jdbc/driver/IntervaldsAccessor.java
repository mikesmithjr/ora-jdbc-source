package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;

class IntervaldsAccessor extends Accessor
{
  static final int maxLength = 11;

  IntervaldsAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 183, 183, paramShort, paramBoolean);
    initForDataAccess(paramInt2, paramInt1, null);
  }

  IntervaldsAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    init(paramOracleStatement, 183, 183, paramShort, false);
    initForDescribe(183, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, null);

    initForDataAccess(0, paramInt1, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    this.internalTypeMaxLength = 11;

    if ((paramInt2 > 0) && (paramInt2 < this.internalTypeMaxLength)) {
      this.internalTypeMaxLength = paramInt2;
    }
    this.byteLength = this.internalTypeMaxLength;
  }

  String getString(int paramInt)
    throws SQLException
  {
    String str = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.byteLength * paramInt;
      int j = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];

      byte[] arrayOfByte = new byte[j];

      System.arraycopy(this.rowSpaceByte, i, arrayOfByte, 0, j);

      str = new INTERVALDS(arrayOfByte).toString();
    }

    return str;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getINTERVALDS(paramInt);
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getINTERVALDS(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getINTERVALDS(paramInt);
  }

  INTERVALDS getINTERVALDS(int paramInt)
    throws SQLException
  {
    INTERVALDS localINTERVALDS = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.byteLength * paramInt;
      int j = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];

      byte[] arrayOfByte = new byte[j];

      System.arraycopy(this.rowSpaceByte, i, arrayOfByte, 0, j);

      localINTERVALDS = new INTERVALDS(arrayOfByte);
    }

    return localINTERVALDS;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.IntervaldsAccessor
 * JD-Core Version:    0.6.0
 */