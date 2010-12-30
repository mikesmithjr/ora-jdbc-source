package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TIMEZONETAB;
import oracle.sql.ZONEIDMAP;

class TimestamptzAccessor extends DateTimeCommonAccessor
{
  static final int maxLength = 13;
  static int OFFSET_HOUR = 20;
  static int OFFSET_MINUTE = 60;

  static byte REGIONIDBIT = -128;

  TimestamptzAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 181, 181, paramShort, paramBoolean);
    initForDataAccess(paramInt2, paramInt1, null);
  }

  TimestamptzAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    init(paramOracleStatement, 181, 181, paramShort, false);
    initForDescribe(181, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, null);

    initForDataAccess(0, paramInt1, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    this.internalTypeMaxLength = 13;

    if ((paramInt2 > 0) && (paramInt2 < this.internalTypeMaxLength)) {
      this.internalTypeMaxLength = paramInt2;
    }
    this.byteLength = this.internalTypeMaxLength;
  }

  String getString(int paramInt)
    throws SQLException
  {
    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] == -1) {
      return null;
    }
    int i = this.columnIndex + this.byteLength * paramInt;

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();
    Calendar localCalendar = Calendar.getInstance(localTimeZone);

    int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar.set(1, j);
    localCalendar.set(2, oracleMonth(i));
    localCalendar.set(5, oracleDay(i));
    localCalendar.set(11, oracleHour(i));
    localCalendar.set(12, oracleMin(i));
    localCalendar.set(13, oracleSec(i));
    localCalendar.set(14, 0);
    String str;
    if ((oracleTZ1(i) & REGIONIDBIT) != 0)
    {
      k = getHighOrderbits(oracleTZ1(i));
      k += getLowOrderbits(oracleTZ2(i));

      if (TIMEZONETAB.checkID(k)) {
        TIMEZONETAB.updateTable(this.statement.connection, k);
      }
      m = TIMEZONETAB.getOffset(localCalendar, k);

      localCalendar.add(10, m / 3600000);
      localCalendar.add(12, m % 3600000 / 60000);

      str = new String(ZONEIDMAP.getRegion(k));
    }
    else
    {
      localCalendar.add(10, oracleTZ1(i) - OFFSET_HOUR);
      localCalendar.add(12, oracleTZ2(i) - OFFSET_MINUTE);

      k = oracleTZ1(i) - OFFSET_HOUR;
      m = oracleTZ2(i) - OFFSET_MINUTE;

      str = new String(k + ":" + m);
    }

    j = localCalendar.get(1);

    int k = localCalendar.get(2) + 1;
    int m = localCalendar.get(5);
    int n = localCalendar.get(11);
    int i1 = localCalendar.get(12);
    int i2 = localCalendar.get(13);
    int i3 = oracleNanos(i);

    return j + "-" + k + "-" + m + " " + n + "." + i1 + "." + i2 + "." + i3 + " " + str;
  }

  java.sql.Date getDate(int paramInt)
    throws SQLException
  {
    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] == -1) {
      return null;
    }
    int i = this.columnIndex + this.byteLength * paramInt;

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();
    Calendar localCalendar = Calendar.getInstance(localTimeZone);

    int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar.set(1, j);
    localCalendar.set(2, oracleMonth(i));
    localCalendar.set(5, oracleDay(i));
    localCalendar.set(11, oracleHour(i));
    localCalendar.set(12, oracleMin(i));
    localCalendar.set(13, oracleSec(i));
    localCalendar.set(14, 0);

    if ((oracleTZ1(i) & REGIONIDBIT) != 0)
    {
      int k = getHighOrderbits(oracleTZ1(i));
      k += getLowOrderbits(oracleTZ2(i));

      if (TIMEZONETAB.checkID(k)) {
        TIMEZONETAB.updateTable(this.statement.connection, k);
      }
      int m = TIMEZONETAB.getOffset(localCalendar, k);

      localCalendar.add(10, m / 3600000);
      localCalendar.add(12, m % 3600000 / 60000);
    }
    else
    {
      localCalendar.add(10, oracleTZ1(i) - OFFSET_HOUR);
      localCalendar.add(12, oracleTZ2(i) - OFFSET_MINUTE);
    }

    long l = localCalendar.getTime().getTime();

    return new java.sql.Date(l);
  }

  Time getTime(int paramInt)
    throws SQLException
  {
    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] == -1) {
      return null;
    }
    int i = this.columnIndex + this.byteLength * paramInt;

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();
    Calendar localCalendar = Calendar.getInstance(localTimeZone);

    int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar.set(1, j);
    localCalendar.set(2, oracleMonth(i));
    localCalendar.set(5, oracleDay(i));
    localCalendar.set(11, oracleHour(i));
    localCalendar.set(12, oracleMin(i));
    localCalendar.set(13, oracleSec(i));
    localCalendar.set(14, 0);

    if ((oracleTZ1(i) & REGIONIDBIT) != 0)
    {
      int k = getHighOrderbits(oracleTZ1(i));
      k += getLowOrderbits(oracleTZ2(i));

      if (TIMEZONETAB.checkID(k)) {
        TIMEZONETAB.updateTable(this.statement.connection, k);
      }
      int m = TIMEZONETAB.getOffset(localCalendar, k);

      localCalendar.add(10, m / 3600000);
      localCalendar.add(12, m % 3600000 / 60000);
    }
    else
    {
      localCalendar.add(10, oracleTZ1(i) - OFFSET_HOUR);
      localCalendar.add(12, oracleTZ2(i) - OFFSET_MINUTE);
    }

    long l = localCalendar.getTime().getTime();

    return new Time(l);
  }

  Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] == -1) {
      return null;
    }
    int i = this.columnIndex + this.byteLength * paramInt;

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();
    Calendar localCalendar = Calendar.getInstance(localTimeZone);

    int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar.set(1, j);
    localCalendar.set(2, oracleMonth(i));
    localCalendar.set(5, oracleDay(i));
    localCalendar.set(11, oracleHour(i));
    localCalendar.set(12, oracleMin(i));
    localCalendar.set(13, oracleSec(i));
    localCalendar.set(14, 0);

    if ((oracleTZ1(i) & REGIONIDBIT) != 0)
    {
      int k = getHighOrderbits(oracleTZ1(i));
      k += getLowOrderbits(oracleTZ2(i));

      if (TIMEZONETAB.checkID(k)) {
        TIMEZONETAB.updateTable(this.statement.connection, k);
      }
      int m = TIMEZONETAB.getOffset(localCalendar, k);

      localCalendar.add(10, m / 3600000);
      localCalendar.add(12, m % 3600000 / 60000);
    }
    else
    {
      localCalendar.add(10, oracleTZ1(i) - OFFSET_HOUR);
      localCalendar.add(12, oracleTZ2(i) - OFFSET_MINUTE);
    }

    long l = localCalendar.getTime().getTime();

    Timestamp localTimestamp = new Timestamp(l);

    int n = oracleNanos(i);

    localTimestamp.setNanos(n);

    return localTimestamp;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getTIMESTAMPTZ(paramInt);
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getTIMESTAMPTZ(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getTIMESTAMPTZ(paramInt);
  }

  TIMESTAMPTZ getTIMESTAMPTZ(int paramInt)
    throws SQLException
  {
    TIMESTAMPTZ localTIMESTAMPTZ = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.byteLength * paramInt;
      byte[] arrayOfByte = new byte[13];

      System.arraycopy(this.rowSpaceByte, i, arrayOfByte, 0, 13);

      localTIMESTAMPTZ = new TIMESTAMPTZ(arrayOfByte);
    }

    return localTIMESTAMPTZ;
  }

  static int setHighOrderbits(int paramInt)
  {
    return (paramInt & 0x1FC0) >> 6;
  }

  static int setLowOrderbits(int paramInt)
  {
    return (paramInt & 0x3F) << 2;
  }

  static int getHighOrderbits(int paramInt)
  {
    return (paramInt & 0x7F) << 6;
  }

  static int getLowOrderbits(int paramInt)
  {
    return (paramInt & 0xFC) >> 2;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TimestamptzAccessor
 * JD-Core Version:    0.6.0
 */