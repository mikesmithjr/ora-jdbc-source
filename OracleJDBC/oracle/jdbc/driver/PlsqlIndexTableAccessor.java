package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;
import oracle.sql.NUMBER;

class PlsqlIndexTableAccessor extends Accessor
{
  int elementInternalType;
  int maxNumberOfElements;
  int elementMaxLen;
  int ibtValueIndex;
  int ibtIndicatorIndex;
  int ibtLengthIndex;
  int ibtMetaIndex;
  int ibtByteLength;
  int ibtCharLength;

  PlsqlIndexTableAccessor(OracleStatement paramOracleStatement, int paramInt1, int paramInt2, int paramInt3, int paramInt4, short paramShort, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 998, 998, paramShort, paramBoolean);

    this.elementInternalType = paramInt2;
    this.maxNumberOfElements = paramInt4;
    this.elementMaxLen = paramInt3;

    initForDataAccess(paramInt1, paramInt3, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    switch (this.elementInternalType)
    {
    case 1:
    case 96:
      this.internalTypeMaxLength = 2000;

      this.elementMaxLen = ((paramInt2 == 0 ? this.internalTypeMaxLength : paramInt2) + 1);
      this.ibtCharLength = (this.elementMaxLen * this.maxNumberOfElements);
      this.elementInternalType = 9;

      break;
    case 6:
      this.internalTypeMaxLength = 21;
      this.elementMaxLen = (this.internalTypeMaxLength + 1);
      this.ibtByteLength = (this.elementMaxLen * this.maxNumberOfElements);

      break;
    default:
      DatabaseError.throwSqlException(97);
    }
  }

  Object[] getPlsqlIndexTable(int paramInt)
    throws SQLException
  {
    Object localObject = null;
    short[] arrayOfShort = this.statement.ibtBindIndicators;
    int i = (arrayOfShort[(this.ibtMetaIndex + 4)] >> 16) + (arrayOfShort[(this.ibtMetaIndex + 5)] & 0xFFFF);

    int j = this.ibtValueIndex;
    char[] arrayOfChar;
    int k;
    switch (this.elementInternalType)
    {
    case 9:
      localObject = new String[i];
      arrayOfChar = this.statement.ibtBindChars;

      for (k = 0; k < i; )
      {
        if (arrayOfShort[(this.ibtIndicatorIndex + k)] == -1)
        {
          localObject[k] = null;
        }
        else
        {
          localObject[k] = new String(arrayOfChar, j + 1, arrayOfChar[j] >> '\001');
        }

        j += this.elementMaxLen;

        k++; continue;

        localObject = new BigDecimal[i];
        arrayOfByte1 = this.statement.ibtBindBytes;

        for (m = 0; m < i; )
        {
          if (arrayOfShort[(this.ibtIndicatorIndex + m)] == -1)
          {
            localObject[m] = null;
          }
          else
          {
            int n = arrayOfByte1[j];
            byte[] arrayOfByte2 = new byte[n];

            System.arraycopy(arrayOfByte1, j + 1, arrayOfByte2, 0, n);

            localObject[m] = NUMBER.toBigDecimal(arrayOfByte2);
          }

          j += this.elementMaxLen;

          m++; continue;

          DatabaseError.throwSqlException(97);
        }
      }
    case 6:
    }
    byte[] arrayOfByte1;
    int m;
    return (Object)localObject;
  }

  Datum[] getOraclePlsqlIndexTable(int paramInt)
    throws SQLException
  {
    Object localObject = null;
    short[] arrayOfShort = this.statement.ibtBindIndicators;
    int i = (arrayOfShort[(this.ibtMetaIndex + 4)] >> 16) + (arrayOfShort[(this.ibtMetaIndex + 5)] & 0xFFFF);

    int j = this.ibtValueIndex;
    CharacterSet localCharacterSet;
    char[] arrayOfChar;
    int k;
    switch (this.elementInternalType)
    {
    case 9:
      localObject = new CHAR[i];

      localCharacterSet = CharacterSet.make(2000);
      arrayOfChar = this.statement.ibtBindChars;

      for (k = 0; k < i; )
      {
        if (arrayOfShort[(this.ibtIndicatorIndex + k)] == -1)
        {
          localObject[k] = null;
        }
        else
        {
          m = arrayOfChar[j];
          byte[] arrayOfByte2 = new byte[m];

          DBConversion.javaCharsToUcs2Bytes(arrayOfChar, j + 1, arrayOfByte2, 0, m >> 1);

          localObject[k] = new CHAR(arrayOfByte2, localCharacterSet);
        }

        j += this.elementMaxLen;

        k++; continue;

        localObject = new NUMBER[i];
        arrayOfByte1 = this.statement.ibtBindBytes;

        for (m = 0; m < i; )
        {
          if (arrayOfShort[(this.ibtIndicatorIndex + m)] == -1)
          {
            localObject[m] = null;
          }
          else
          {
            int n = arrayOfByte1[j];
            byte[] arrayOfByte3 = new byte[n];

            System.arraycopy(arrayOfByte1, j + 1, arrayOfByte3, 0, n);

            localObject[m] = new NUMBER(arrayOfByte3);
          }

          j += this.elementMaxLen;

          m++; continue;

          DatabaseError.throwSqlException(97);
        }
      }
    case 6:
    }
    int m;
    byte[] arrayOfByte1;
    return (Datum)localObject;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.PlsqlIndexTableAccessor
 * JD-Core Version:    0.6.0
 */