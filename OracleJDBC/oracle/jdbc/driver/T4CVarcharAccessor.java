package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CVarcharAccessor extends VarcharAccessor
{
  T4CMAREngine mare;
  static final int t4MaxLength = 4000;
  static final int t4CallMaxLength = 4001;
  static final int t4PlsqlMaxLength = 32512;
  boolean underlyingLong = false;

  final int[] meta = new int[1];
  final int[] tmp = new int[1];
  final int[] escapeSequenceArr = new int[1];
  final boolean[] readHeaderArr = new boolean[1];
  final boolean[] readAsNonStreamArr = new boolean[1];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CVarcharAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramShort, paramInt2, paramBoolean);

    this.mare = paramT4CMAREngine;

    calculateSizeTmpByteArray();
  }

  T4CVarcharAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort, int paramInt7, int paramInt8, int paramInt9, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort);

    this.mare = paramT4CMAREngine;
    this.definedColumnType = paramInt8;
    this.definedColumnSize = paramInt9;

    calculateSizeTmpByteArray();

    this.oacmxl = paramInt7;

    if (this.oacmxl == -1)
    {
      this.underlyingLong = true;
      this.oacmxl = 4000;
    }
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

  boolean unmarshalOneRow()
    throws SQLException, IOException
  {
    if (this.isUseLess)
    {
      this.lastRowProcessed += 1;

      return false;
    }

    int i = this.indicatorIndex + this.lastRowProcessed;
    int j = this.lengthIndex + this.lastRowProcessed;

    byte[] arrayOfByte1 = this.statement.tmpByteArray;
    int k = this.columnIndex + this.lastRowProcessed * this.charLength;

    if (!this.underlyingLong)
    {
      if (this.rowSpaceIndicator == null)
      {
        byte[] arrayOfByte2 = new byte[16000];

        this.mare.unmarshalCLR(arrayOfByte2, 0, this.meta);
        processIndicator(this.meta[0]);

        this.lastRowProcessed += 1;

        return false;
      }

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

      if (this.statement.maxFieldSize > 0)
        this.mare.unmarshalCLR(arrayOfByte1, 0, this.meta, this.statement.maxFieldSize);
      else {
        this.mare.unmarshalCLR(arrayOfByte1, 0, this.meta);
      }

    }
    else
    {
      this.escapeSequenceArr[0] = this.mare.unmarshalUB1();

      if (this.mare.escapeSequenceNull(this.escapeSequenceArr[0]))
      {
        this.meta[0] = 0;

        this.mare.processIndicator(false, 0);

        m = this.mare.unmarshalUB2();
      }
      else
      {
        m = 0;
        int n = 0;
        byte[] arrayOfByte3 = arrayOfByte1;
        int i1 = 0;

        this.readHeaderArr[0] = true;
        this.readAsNonStreamArr[0] = false;

        while (m != -1)
        {
          if ((arrayOfByte3 == arrayOfByte1) && (n + 255 > arrayOfByte1.length))
          {
            arrayOfByte3 = new byte['ÿ'];
          }
          if (arrayOfByte3 == arrayOfByte1)
            i1 = n;
          else {
            i1 = 0;
          }
          m = T4CLongAccessor.readStreamFromWire(arrayOfByte3, i1, 255, this.escapeSequenceArr, this.readHeaderArr, this.readAsNonStreamArr, this.mare, ((T4CConnection)this.statement.connection).oer);

          if (m == -1)
            continue;
          if (arrayOfByte3 == arrayOfByte1) {
            n += m; continue;
          }if (arrayOfByte1.length - n <= 0)
          {
            continue;
          }
          int i2 = arrayOfByte1.length - n;

          System.arraycopy(arrayOfByte3, 0, arrayOfByte1, n, i2);

          n += i2;
        }

        if (arrayOfByte3 != arrayOfByte1) {
          arrayOfByte3 = null;
        }
        this.meta[0] = n;
      }

    }

    this.tmp[0] = this.meta[0];

    int m = 0;

    if (this.formOfUse == 2) {
      m = this.statement.connection.conversion.NCHARBytesToJavaChars(arrayOfByte1, 0, this.rowSpaceChar, k + 1, this.tmp, this.charLength - 1);
    }
    else
    {
      m = this.statement.connection.conversion.CHARBytesToJavaChars(arrayOfByte1, 0, this.rowSpaceChar, k + 1, this.tmp, this.charLength - 1);
    }

    this.rowSpaceChar[k] = (char)(m * 2);

    if (!this.underlyingLong) {
      processIndicator(this.meta[0]);
    }
    if (this.meta[0] == 0)
    {
      this.rowSpaceIndicator[i] = -1;
      this.rowSpaceIndicator[j] = 0;
    }
    else
    {
      this.rowSpaceIndicator[j] = (short)(this.meta[0] * 2);

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
    int j = this.columnIndex + this.lastRowProcessed * this.charLength;
    int k = this.columnIndex + (i - 1) * this.charLength;

    int m = this.indicatorIndex + this.lastRowProcessed;
    int n = this.indicatorIndex + i - 1;
    int i1 = this.lengthIndex + this.lastRowProcessed;
    int i2 = this.lengthIndex + i - 1;
    int i3 = this.rowSpaceIndicator[i2];

    this.rowSpaceIndicator[i1] = (short)i3;
    this.rowSpaceIndicator[m] = this.rowSpaceIndicator[n];

    System.arraycopy(this.rowSpaceChar, k, this.rowSpaceChar, j, this.rowSpaceChar[k] / '\002' + 1);

    this.lastRowProcessed += 1;
  }

  void saveDataFromOldDefineBuffers(byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt1, int paramInt2)
    throws SQLException
  {
    int i = this.columnIndex + (paramInt2 - 1) * this.charLength;

    int j = this.columnIndexLastRow + (paramInt1 - 1) * this.charLength;

    int k = this.indicatorIndex + paramInt2 - 1;
    int m = this.indicatorIndexLastRow + paramInt1 - 1;
    int n = this.lengthIndex + paramInt2 - 1;
    int i1 = this.lengthIndexLastRow + paramInt1 - 1;
    int i2 = paramArrayOfShort[i1];

    this.rowSpaceIndicator[n] = (short)i2;
    this.rowSpaceIndicator[k] = paramArrayOfShort[m];

    if (i2 != 0)
    {
      System.arraycopy(paramArrayOfChar, j, this.rowSpaceChar, i, paramArrayOfChar[j] / '\002' + 1);
    }
  }

  void calculateSizeTmpByteArray()
  {
    int i;
    if (this.formOfUse == 2)
    {
      i = (this.charLength - 1) * this.statement.connection.conversion.maxNCharSize;
    }
    else
    {
      i = (this.charLength - 1) * this.statement.connection.conversion.cMaxCharSize;
    }

    if (this.statement.sizeTmpByteArray < i)
      this.statement.sizeTmpByteArray = i;
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
      case 2:
      case 3:
        return getBigDecimal(paramInt);
      case 4:
        return new Integer(getInt(paramInt));
      case -6:
        return new Byte(getByte(paramInt));
      case 5:
        return new Short(getShort(paramInt));
      case -7:
      case 16:
        return new Boolean(getBoolean(paramInt));
      case -5:
        return new Long(getLong(paramInt));
      case 7:
        return new Float(getFloat(paramInt));
      case 6:
      case 8:
        return new Double(getDouble(paramInt));
      case 91:
        return getDate(paramInt);
      case 92:
        return getTime(paramInt);
      case 93:
        return getTimestamp(paramInt);
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
 * Qualified Name:     oracle.jdbc.driver.T4CVarcharAccessor
 * JD-Core Version:    0.6.0
 */