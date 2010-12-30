package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CBinaryFloatAccessor extends BinaryFloatAccessor
{
  T4CMAREngine mare;
  boolean underlyingLongRaw = false;

  final int[] meta = new int[1];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4CBinaryFloatAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramShort, paramInt2, paramBoolean);

    this.mare = paramT4CMAREngine;
  }

  T4CBinaryFloatAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, int paramInt7, int paramInt8, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1 == -1 ? paramInt8 : paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort);

    this.mare = paramT4CMAREngine;
    this.definedColumnType = paramInt7;
    this.definedColumnSize = paramInt8;

    if (paramInt1 == -1)
      this.underlyingLongRaw = true;
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

    if (this.rowSpaceIndicator == null)
    {
      byte[] arrayOfByte = new byte[16000];

      this.mare.unmarshalCLR(arrayOfByte, 0, this.meta);
      processIndicator(this.meta[0]);

      this.lastRowProcessed += 1;

      return false;
    }

    int i = this.indicatorIndex + this.lastRowProcessed;
    int j = this.lengthIndex + this.lastRowProcessed;

    if (this.isNullByDescribe)
    {
      this.rowSpaceIndicator[i] = -1;
      this.rowSpaceIndicator[j] = 0;
      this.lastRowProcessed += 1;

      if (this.mare.versionNumber < 9200) {
        processIndicator(0);
      }

      return false;
    }

    int k = this.columnIndex + this.lastRowProcessed * this.byteLength;

    this.mare.unmarshalCLR(this.rowSpaceByte, k, this.meta, this.byteLength);

    processIndicator(this.meta[0]);

    if (this.meta[0] == 0)
    {
      this.rowSpaceIndicator[i] = -1;
      this.rowSpaceIndicator[j] = 0;
    }
    else
    {
      this.rowSpaceIndicator[j] = (short)this.meta[0];
      this.rowSpaceIndicator[i] = 0;
    }

    this.lastRowProcessed += 1;

    return false;
  }

  void copyRow()
    throws SQLException, IOException
  {
    int i;
    if (this.lastRowProcessed == 0)
      i = this.statement.rowPrefetch;
    else {
      i = this.lastRowProcessed;
    }
    int j = this.columnIndex + this.lastRowProcessed * this.byteLength;
    int k = this.columnIndex + (i - 1) * this.byteLength;

    int m = this.indicatorIndex + this.lastRowProcessed;
    int n = this.indicatorIndex + i - 1;
    int i1 = this.lengthIndex + this.lastRowProcessed;
    int i2 = this.lengthIndex + i - 1;
    int i3 = this.rowSpaceIndicator[i2];

    this.rowSpaceIndicator[i1] = (short)i3;
    this.rowSpaceIndicator[m] = this.rowSpaceIndicator[n];

    System.arraycopy(this.rowSpaceByte, k, this.rowSpaceByte, j, i3);

    this.lastRowProcessed += 1;
  }

  void saveDataFromOldDefineBuffers(byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws SQLException
  {
    int i = this.columnIndex + (paramInt2 - 1) * this.byteLength;

    int j = this.columnIndexLastRow + (paramInt1 - 1) * this.byteLength;

    int k = this.indicatorIndex + paramInt2 - 1;
    int m = this.indicatorIndexLastRow + paramInt1 - 1;
    int n = this.lengthIndex + paramInt2 - 1;
    int i1 = this.lengthIndexLastRow + paramInt1 - 1;
    int i2 = paramArrayOfShort[i1];

    this.rowSpaceIndicator[n] = (short)i2;
    this.rowSpaceIndicator[k] = paramArrayOfShort[m];

    if (i2 != 0)
    {
      System.arraycopy(paramArrayOfByte, j, this.rowSpaceByte, i, i2);
    }
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    if (this.definedColumnType == 0) {
      return super.getObject(paramInt);
    }

    Object localObject = null;

    if (this.rowSpaceIndicator == null) {
      DatabaseError.throwSqlException(21);
    }
    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      switch (this.definedColumnType)
      {
      case -1:
      case 1:
      case 12:
        return getString(paramInt);
      case 4:
        return new Integer(getInt(paramInt));
      case -6:
        return new Byte(getByte(paramInt));
      case 5:
        return new Short(getShort(paramInt));
      case 6:
      case 8:
        return new Double(getDouble(paramInt));
      case 2:
      case 3:
        return getBigDecimal(paramInt);
      case 7:
        return new Float(getFloat(paramInt));
      case 100:
        return getBINARY_FLOAT(paramInt);
      case -5:
        return new Long(getLong(paramInt));
      case -4:
      case -3:
      case -2:
        return getBytes(paramInt);
      }

      DatabaseError.throwSqlException(4);

      return null;
    }

    return localObject;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CBinaryFloatAccessor
 * JD-Core Version:    0.6.0
 */