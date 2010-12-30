package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;

class DateAccessor extends DateTimeCommonAccessor
{
  static final int maxLength = 7;

  DateAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 12, 12, paramShort, paramBoolean);
    initForDataAccess(paramInt2, paramInt1, null);
  }

  DateAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    init(paramOracleStatement, 12, 12, paramShort, false);
    initForDescribe(12, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, null);

    initForDataAccess(0, paramInt1, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    this.internalTypeMaxLength = 7;

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
      int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

      str = j + "-" + toStr(this.rowSpaceByte[(2 + i)]) + "-" + toStr(this.rowSpaceByte[(3 + i)]) + " " + toStr(this.rowSpaceByte[(4 + i)] - 1) + ":" + toStr(this.rowSpaceByte[(5 + i)] - 1) + ":" + toStr(this.rowSpaceByte[(6 + i)] - 1) + ".0";
    }

    return str;
  }

  Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    Timestamp localTimestamp = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.byteLength * paramInt;

      TimeZone localTimeZone = this.statement.getDefaultTimeZone();

      int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

      if (j <= 0) {
        j++;
      }
      localTimestamp = new Timestamp(getMillis(j, oracleMonth(i), oracleDay(i), oracleTime(i), localTimeZone));
    }

    return localTimestamp;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    Object localObject = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      if (this.externalType == 0)
      {
        if (this.statement.connection.v8Compatible)
          localObject = getTimestamp(paramInt);
        else
          localObject = getDate(paramInt);
      }
      else
      {
        switch (this.externalType)
        {
        case 91:
          return getDate(paramInt);
        case 92:
          return getTime(paramInt);
        case 93:
          return getTimestamp(paramInt);
        }

        DatabaseError.throwSqlException(4);

        return null;
      }

    }

    return localObject;
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getDATE(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getObject(paramInt);
  }

  TIMESTAMP getTIMESTAMP(int paramInt)
    throws SQLException
  {
    if ((this.statement.connection.v8Compatible != true) || (this.externalType != 93))
    {
      return super.getTIMESTAMP(paramInt);
    }

    TIMESTAMP localTIMESTAMP = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];
      int j = this.columnIndex + this.byteLength * paramInt;
      byte[] arrayOfByte = new byte[i];

      System.arraycopy(this.rowSpaceByte, j, arrayOfByte, 0, i);

      localTIMESTAMP = new TIMESTAMP(arrayOfByte);
    }

    return localTIMESTAMP;
  }

  static String toStr(int paramInt)
  {
    return paramInt < 10 ? "0" + paramInt : Integer.toString(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.DateAccessor
 * JD-Core Version:    0.6.0
 */