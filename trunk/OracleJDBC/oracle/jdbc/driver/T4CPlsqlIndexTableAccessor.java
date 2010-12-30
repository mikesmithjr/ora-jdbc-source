package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CPlsqlIndexTableAccessor extends PlsqlIndexTableAccessor
{
  T4CMAREngine mare;
  final int[] meta = new int[1];
  final int[] tmp = new int[1];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4CPlsqlIndexTableAccessor(OracleStatement paramOracleStatement, int paramInt1, int paramInt2, int paramInt3, int paramInt4, short paramShort, boolean paramBoolean, T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(paramOracleStatement, paramInt1, paramInt2, paramInt3, paramInt4, paramShort, paramBoolean);

    calculateSizeTmpByteArray();

    this.mare = paramT4CMAREngine;
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

    if (this.rowSpaceIndicator == null)
    {
      byte[] arrayOfByte1 = new byte[16000];

      this.mare.unmarshalCLR(arrayOfByte1, 0, this.meta);
      processIndicator(this.meta[0]);

      this.lastRowProcessed += 1;

      return false;
    }

    int i = this.indicatorIndex + this.lastRowProcessed;
    int j = this.lengthIndex + this.lastRowProcessed;
    byte[] arrayOfByte2 = this.statement.ibtBindBytes;
    char[] arrayOfChar = this.statement.ibtBindChars;
    short[] arrayOfShort = this.statement.ibtBindIndicators;
    int k = this.statement.ibtBindByteOffset;
    int m = this.statement.ibtBindCharOffset;
    int n = this.statement.ibtBindIndicatorOffset;

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

    int i1 = (int)this.mare.unmarshalUB4();

    arrayOfShort[(this.ibtMetaIndex + 4)] = (short)(i1 & 0xFFFFFFFF & 0xFFFF);

    arrayOfShort[(this.ibtMetaIndex + 5)] = (short)(i1 & 0xFFFF);
    byte[] arrayOfByte3;
    int i3;
    if ((this.elementInternalType == 9) || (this.elementInternalType == 96) || (this.elementInternalType == 1))
    {
      arrayOfByte3 = this.statement.tmpByteArray;

      for (i3 = 0; i3 < i1; )
      {
        int i4 = this.ibtValueIndex + this.elementMaxLen * i3;

        this.mare.unmarshalCLR(arrayOfByte3, 0, this.meta);

        this.tmp[0] = this.meta[0];

        int i5 = this.statement.connection.conversion.CHARBytesToJavaChars(arrayOfByte3, 0, arrayOfChar, i4 + 1, this.tmp, arrayOfChar.length - i4 - 1);

        arrayOfChar[i4] = (char)(i5 * 2);

        processIndicator(this.meta[0]);

        if (this.meta[0] == 0)
        {
          arrayOfShort[(this.ibtIndicatorIndex + i3)] = -1;
          arrayOfShort[(this.ibtLengthIndex + i3)] = 0;
        }
        else
        {
          arrayOfShort[(this.ibtLengthIndex + i3)] = (short)(this.meta[0] * 2);

          arrayOfShort[(this.ibtIndicatorIndex + i3)] = 0;
        }
        i3++; continue;

        for (int i2 = 0; i2 < i1; i2++)
        {
          i3 = this.ibtValueIndex + this.elementMaxLen * i2;

          this.mare.unmarshalCLR(arrayOfByte2, i3 + 1, this.meta);

          arrayOfByte2[i3] = (byte)this.meta[0];

          processIndicator(this.meta[0]);

          if (this.meta[0] == 0)
          {
            arrayOfShort[(this.ibtIndicatorIndex + i2)] = -1;
            arrayOfShort[(this.ibtLengthIndex + i2)] = 0;
          }
          else
          {
            arrayOfShort[(this.ibtLengthIndex + i2)] = (short)this.meta[0];
            arrayOfShort[(this.ibtIndicatorIndex + i2)] = 0;
          }
        }
      }
    }
    this.lastRowProcessed += 1;

    return false;
  }

  void calculateSizeTmpByteArray()
  {
    if ((this.elementInternalType == 9) || (this.elementInternalType == 96) || (this.elementInternalType == 1))
    {
      int i = this.ibtCharLength * this.statement.connection.conversion.cMaxCharSize / this.maxNumberOfElements;

      if (this.statement.sizeTmpByteArray < i)
        this.statement.sizeTmpByteArray = i;
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
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CPlsqlIndexTableAccessor
 * JD-Core Version:    0.6.0
 */