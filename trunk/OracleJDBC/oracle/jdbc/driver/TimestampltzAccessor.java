package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.OffsetDST;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMEZONETAB;
import oracle.sql.ZONEIDMAP;

class TimestampltzAccessor extends DateTimeCommonAccessor
{
  static int INV_ZONEID = -1;

  TimestampltzAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 231, 231, paramShort, paramBoolean);
    initForDataAccess(paramInt2, paramInt1, null);
  }

  TimestampltzAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    init(paramOracleStatement, 231, 231, paramShort, false);
    initForDescribe(231, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, null);

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
    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] == -1) {
      return null;
    }

    Calendar localCalendar1 = this.statement.connection.getDbTzCalendar();

    String str = this.statement.connection.getSessionTimeZone();

    if (str == null)
    {
      throw new SQLException("Session Time Zone not set!");
    }

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();

    localTimeZone.setID(str);

    Calendar localCalendar2 = Calendar.getInstance(localTimeZone);

    int i = this.columnIndex + this.byteLength * paramInt;
    int j = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];

    int k = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar1.set(1, k);
    localCalendar1.set(2, oracleMonth(i));
    localCalendar1.set(5, oracleDay(i));
    localCalendar1.set(11, oracleHour(i));
    localCalendar1.set(12, oracleMin(i));
    localCalendar1.set(13, oracleSec(i));
    localCalendar1.set(14, 0);

    TimeZoneAdjust(localCalendar1, localCalendar2);

    k = localCalendar2.get(1);

    int m = localCalendar2.get(2) + 1;
    int n = localCalendar2.get(5);
    int i1 = localCalendar2.get(11);
    int i2 = localCalendar2.get(12);
    int i3 = localCalendar2.get(13);
    int i4 = 0;

    if (j == 11)
    {
      i4 = oracleNanos(i);
    }

    return k + "-" + m + "-" + n + " " + i1 + "." + i2 + "." + i3 + "." + i4;
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

    Calendar localCalendar1 = this.statement.connection.getDbTzCalendar();

    String str = this.statement.connection.getSessionTimeZone();

    if (str == null)
    {
      throw new SQLException("Session Time Zone not set!");
    }

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();

    localTimeZone.setID(str);

    Calendar localCalendar2 = Calendar.getInstance(localTimeZone);

    int i = this.columnIndex + this.byteLength * paramInt;
    int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar1.set(1, j);
    localCalendar1.set(2, oracleMonth(i));
    localCalendar1.set(5, oracleDay(i));
    localCalendar1.set(11, oracleHour(i));
    localCalendar1.set(12, oracleMin(i));
    localCalendar1.set(13, oracleSec(i));
    localCalendar1.set(14, 0);

    TimeZoneAdjust(localCalendar1, localCalendar2);

    long l = localCalendar2.getTime().getTime();

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

    Calendar localCalendar1 = this.statement.connection.getDbTzCalendar();

    String str = this.statement.connection.getSessionTimeZone();

    if (str == null)
    {
      throw new SQLException("Session Time Zone not set!");
    }

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();

    localTimeZone.setID(str);

    Calendar localCalendar2 = Calendar.getInstance(localTimeZone);

    int i = this.columnIndex + this.byteLength * paramInt;
    int j = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar1.set(1, j);
    localCalendar1.set(2, oracleMonth(i));
    localCalendar1.set(5, oracleDay(i));
    localCalendar1.set(11, oracleHour(i));
    localCalendar1.set(12, oracleMin(i));
    localCalendar1.set(13, oracleSec(i));
    localCalendar1.set(14, 0);

    TimeZoneAdjust(localCalendar1, localCalendar2);

    long l = localCalendar2.getTime().getTime();

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

    Calendar localCalendar1 = this.statement.connection.getDbTzCalendar();

    String str = this.statement.connection.getSessionTimeZone();

    if (str == null)
    {
      throw new SQLException("Session Time Zone not set!");
    }

    TimeZone localTimeZone = this.statement.getDefaultTimeZone();

    localTimeZone.setID(str);

    Calendar localCalendar2 = Calendar.getInstance(localTimeZone);

    int i = this.columnIndex + this.byteLength * paramInt;
    int j = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];

    int k = ((this.rowSpaceByte[(0 + i)] & 0xFF) - 100) * 100 + (this.rowSpaceByte[(1 + i)] & 0xFF) - 100;

    localCalendar1.set(1, k);
    localCalendar1.set(2, oracleMonth(i));
    localCalendar1.set(5, oracleDay(i));
    localCalendar1.set(11, oracleHour(i));
    localCalendar1.set(12, oracleMin(i));
    localCalendar1.set(13, oracleSec(i));
    localCalendar1.set(14, 0);

    TimeZoneAdjust(localCalendar1, localCalendar2);

    long l = localCalendar2.getTime().getTime();
    Timestamp localTimestamp = new Timestamp(l);

    if (j == 11)
    {
      localTimestamp.setNanos(oracleNanos(i));
    }

    return localTimestamp;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getTIMESTAMPLTZ(paramInt);
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getTIMESTAMPLTZ(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getTIMESTAMPLTZ(paramInt);
  }

  TIMESTAMPLTZ getTIMESTAMPLTZ(int paramInt)
    throws SQLException
  {
    TIMESTAMPLTZ localTIMESTAMPLTZ = null;

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

      localTIMESTAMPLTZ = new TIMESTAMPLTZ(arrayOfByte);
    }

    return localTIMESTAMPLTZ;
  }

  void TimeZoneAdjust(Calendar paramCalendar1, Calendar paramCalendar2)
    throws SQLException
  {
    String str1 = paramCalendar1.getTimeZone().getID();
    String str2 = paramCalendar2.getTimeZone().getID();

    if (!str2.equals(str1))
    {
      OffsetDST localOffsetDST = new OffsetDST();

      k = getZoneOffset(paramCalendar1, localOffsetDST);

      m = localOffsetDST.getOFFSET();

      paramCalendar1.add(11, -(m / 3600000));
      paramCalendar1.add(12, -(m % 3600000) / 60000);
      int i;
      if ((str2.equals("Custom")) || ((str2.startsWith("GMT")) && (str2.length() > 3)))
      {
        i = paramCalendar2.getTimeZone().getRawOffset();
      }
      else
      {
        n = ZONEIDMAP.getID(str2);

        if (n == INV_ZONEID) {
          throw new SQLException("Timezone not supported");
        }
        if (TIMEZONETAB.checkID(n)) {
          TIMEZONETAB.updateTable(this.statement.connection, n);
        }

        i = TIMEZONETAB.getOffset(paramCalendar1, n);
      }

      paramCalendar1.add(11, i / 3600000);
      paramCalendar1.add(12, i % 3600000 / 60000);
    }

    if (((str2.equals("Custom")) && (str1.equals("Custom"))) || ((str2.startsWith("GMT")) && (str2.length() > 3) && (str1.startsWith("GMT")) && (str1.length() > 3)))
    {
      j = paramCalendar1.getTimeZone().getRawOffset();
      k = paramCalendar2.getTimeZone().getRawOffset();
      m = 0;

      if (j != k)
      {
        m = j - k;
        m = m > 0 ? m : -m;
      }

      if (j > k) {
        m = -m;
      }
      paramCalendar1.add(11, m / 3600000);
      paramCalendar1.add(12, m % 3600000 / 60000);
    }

    int j = paramCalendar1.get(1);
    int k = paramCalendar1.get(2);
    int m = paramCalendar1.get(5);
    int n = paramCalendar1.get(11);
    int i1 = paramCalendar1.get(12);
    int i2 = paramCalendar1.get(13);
    int i3 = paramCalendar1.get(14);

    paramCalendar2.set(1, j);
    paramCalendar2.set(2, k);
    paramCalendar2.set(5, m);
    paramCalendar2.set(11, n);
    paramCalendar2.set(12, i1);
    paramCalendar2.set(13, i2);
    paramCalendar2.set(14, i3);
  }

  byte getZoneOffset(Calendar paramCalendar, OffsetDST paramOffsetDST)
    throws SQLException
  {
    int i = 0;

    String str = paramCalendar.getTimeZone().getID();

    if ((str == "Custom") || ((str.startsWith("GMT")) && (str.length() > 3)))
    {
      paramOffsetDST.setOFFSET(paramCalendar.getTimeZone().getRawOffset());
    }
    else
    {
      int j = ZONEIDMAP.getID(str);

      if (j == INV_ZONEID) {
        throw new SQLException("Timezone not supported");
      }
      if (TIMEZONETAB.checkID(j)) {
        TIMEZONETAB.updateTable(this.statement.connection, j);
      }

      i = TIMEZONETAB.getLocalOffset(paramCalendar, j, paramOffsetDST);
    }

    return i;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TimestampltzAccessor
 * JD-Core Version:    0.6.0
 */