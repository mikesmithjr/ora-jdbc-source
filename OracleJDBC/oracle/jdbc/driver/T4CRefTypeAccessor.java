package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CRefTypeAccessor extends RefTypeAccessor
{
  static final int maxLength = 4000;
  T4CMAREngine mare;
  final int[] meta = new int[1];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4CRefTypeAccessor(OracleStatement paramOracleStatement, String paramString, short paramShort, int paramInt, boolean paramBoolean, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramString, paramShort, paramInt, paramBoolean);
    this.mare = paramT4CMAREngine;
    this.byteLength = 4000;
  }

  T4CRefTypeAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, String paramString, int paramInt7, int paramInt8, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, paramString);

    this.mare = paramT4CMAREngine;
    this.definedColumnType = paramInt7;
    this.definedColumnSize = paramInt8;
    this.byteLength = 4000;
  }

  void processIndicator(int paramInt)
    throws IOException, SQLException
  {
    if (((this.internalType == 1) && (this.describeType == 112)) || ((this.internalType == 23) && (this.describeType == 113)))
    {
      this.mare.unmarshalUB2();
      this.mare.unmarshalUB2();
    }
    else if (this.mare.versionNumber < 9200)
    {
      this.mare.unmarshalSB2();

      if ((this.statement.sqlKind != 1) && (this.statement.sqlKind != 4))
      {
        this.mare.unmarshalSB2();
      }
    } else if ((this.statement.sqlKind == 1) || (this.statement.sqlKind == 4) || (this.isDMLReturnedParam))
    {
      this.mare.processIndicator(paramInt <= 0, paramInt);
    }
  }

  String getString(int paramInt)
    throws SQLException
  {
    String str = super.getString(paramInt);

    if ((str != null) && (this.definedColumnSize > 0) && (str.length() > this.definedColumnSize))
    {
      str = str.substring(0, this.definedColumnSize);
    }
    return str;
  }

  boolean unmarshalOneRow()
    throws SQLException, IOException
  {
    if (this.isUseLess)
    {
      this.lastRowProcessed += 1;

      return false;
    }

    int i = this.columnIndex + this.lastRowProcessed * this.byteLength;

    byte[] arrayOfByte = this.mare.unmarshalCLRforREFS();

    if (arrayOfByte == null) {
      arrayOfByte = new byte[0];
    }
    this.pickledBytes[this.lastRowProcessed] = arrayOfByte;
    this.meta[0] = arrayOfByte.length;

    processIndicator(this.meta[0]);

    int j = this.indicatorIndex + this.lastRowProcessed;
    int k = this.lengthIndex + this.lastRowProcessed;

    if (this.meta[0] == 0)
    {
      this.rowSpaceIndicator[j] = -1;
      this.rowSpaceIndicator[k] = 0;
    }
    else
    {
      this.rowSpaceIndicator[k] = (short)this.meta[0];

      this.rowSpaceIndicator[j] = 0;
    }

    this.lastRowProcessed += 1;

    return false;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CRefTypeAccessor
 * JD-Core Version:    0.6.0
 */