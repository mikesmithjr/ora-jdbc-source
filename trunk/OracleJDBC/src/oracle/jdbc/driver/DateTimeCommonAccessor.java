package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import oracle.sql.DATE;

abstract class DateTimeCommonAccessor extends Accessor {
    static final int GREGORIAN_CUTOVER_YEAR = 1582;
    static final long GREGORIAN_CUTOVER = -12219292800000L;
    static final int JAN_1_1_JULIAN_DAY = 1721426;
    static final int EPOCH_JULIAN_DAY = 2440588;
    static final int ONE_SECOND = 1000;
    static final int ONE_MINUTE = 60000;
    static final int ONE_HOUR = 3600000;
    static final long ONE_DAY = 86400000L;
    static final int[] NUM_DAYS = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };

    static final int[] LEAP_NUM_DAYS = { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };
    static final int ORACLE_CENTURY = 0;
    static final int ORACLE_YEAR = 1;
    static final int ORACLE_MONTH = 2;
    static final int ORACLE_DAY = 3;
    static final int ORACLE_HOUR = 4;
    static final int ORACLE_MIN = 5;
    static final int ORACLE_SEC = 6;
    static final int ORACLE_NANO1 = 7;
    static final int ORACLE_NANO2 = 8;
    static final int ORACLE_NANO3 = 9;
    static final int ORACLE_NANO4 = 10;
    static final int ORACLE_TZ1 = 11;
    static final int ORACLE_TZ2 = 12;
    static final int SIZE_DATE = 7;
    static final int MAX_TIMESTAMP_LENGTH = 11;
    static TimeZone epochTimeZone;
    static long epochTimeZoneOffset;

    java.sql.Date getDate(int currentRow) throws SQLException {
        java.sql.Date result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;

            TimeZone zone = this.statement.getDefaultTimeZone();

            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            if (year <= 0) {
                year++;
            }
            result = new java.sql.Date(getMillis(year, oracleMonth(off), oracleDay(off), 0, zone));
        }

        return result;
    }

    Time getTime(int currentRow) throws SQLException {
        Time result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;

            TimeZone zone = this.statement.getDefaultTimeZone();

            if (zone != epochTimeZone) {
                epochTimeZoneOffset = calculateEpochOffset(zone);
                epochTimeZone = zone;
            }

            result = new Time(oracleTime(off) - epochTimeZoneOffset);
        }

        return result;
    }

    java.sql.Date getDate(int currentRow, Calendar cal) throws SQLException {
        if (cal == null) {
            return getDate(currentRow);
        }
        java.sql.Date result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;
            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            cal.set(1, year);
            cal.set(2, oracleMonth(off));
            cal.set(5, oracleDay(off));
            cal.set(11, oracleHour(off));
            cal.set(12, oracleMin(off));
            cal.set(13, oracleSec(off));
            cal.set(14, 0);

            result = new java.sql.Date(cal.getTime().getTime());
        }

        return result;
    }

    Time getTime(int currentRow, Calendar cal) throws SQLException {
        if (cal == null) {
            return getTime(currentRow);
        }
        Time result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;
            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            cal.set(1, 1970);
            cal.set(2, 0);
            cal.set(5, 1);
            cal.set(11, oracleHour(off));
            cal.set(12, oracleMin(off));
            cal.set(13, oracleSec(off));
            cal.set(14, 0);

            result = new Time(cal.getTime().getTime());
        }

        return result;
    }

    Timestamp getTimestamp(int currentRow, Calendar cal) throws SQLException {
        if (cal == null) {
            return getTimestamp(currentRow);
        }
        Timestamp result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;
            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            cal.set(1, year);
            cal.set(2, oracleMonth(off));
            cal.set(5, oracleDay(off));
            cal.set(11, oracleHour(off));
            cal.set(12, oracleMin(off));
            cal.set(13, oracleSec(off));
            cal.set(14, 0);

            result = new Timestamp(cal.getTime().getTime());

            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            if (len >= 11) {
                result.setNanos(oracleNanos(off));
            }

        }

        return result;
    }

    DATE getDATE(int currentRow) throws SQLException {
        DATE result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;
            byte[] data = new byte[7];

            System.arraycopy(this.rowSpaceByte, off, data, 0, 7);

            result = new DATE(data);
        }

        return result;
    }

    final int oracleYear(int off) {
        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        return year <= 0 ? year + 1 : year;
    }

    final int oracleMonth(int off) {
        return this.rowSpaceByte[(2 + off)] - 1;
    }

    final int oracleDay(int off) {
        return this.rowSpaceByte[(3 + off)];
    }

    final int oracleHour(int off) {
        return this.rowSpaceByte[(4 + off)] - 1;
    }

    final int oracleMin(int off) {
        return this.rowSpaceByte[(5 + off)] - 1;
    }

    final int oracleSec(int off) {
        return this.rowSpaceByte[(6 + off)] - 1;
    }

    final int oracleTZ1(int off) {
        return this.rowSpaceByte[(11 + off)];
    }

    final int oracleTZ2(int off) {
        return this.rowSpaceByte[(12 + off)];
    }

    final int oracleTime(int off) {
        int millisInDay = oracleHour(off);

        millisInDay *= 60;
        millisInDay += oracleMin(off);
        millisInDay *= 60;
        millisInDay += oracleSec(off);
        millisInDay *= 1000;

        return millisInDay;
    }

    final int oracleNanos(int off) {
        int nanos = (this.rowSpaceByte[(7 + off)] & 0xFF) << 24;

        nanos |= (this.rowSpaceByte[(8 + off)] & 0xFF) << 16;
        nanos |= (this.rowSpaceByte[(9 + off)] & 0xFF) << 8;
        nanos |= this.rowSpaceByte[(10 + off)] & 0xFF & 0xFF;

        return nanos;
    }

    static final long computeJulianDay(boolean isGregorian, int year, int month, int date) {
        boolean isLeap = year % 4 == 0;
        int y = year - 1;
        long julianDay = 365L * y + floorDivide(y, 4L) + 1721423L;

        if (isGregorian) {
            isLeap = (isLeap) && ((year % 100 != 0) || (year % 400 == 0));

            julianDay += floorDivide(y, 400L) - floorDivide(y, 100L) + 2L;
        }

        return julianDay + date + (isLeap ? LEAP_NUM_DAYS[month] : NUM_DAYS[month]);
    }

    static final long floorDivide(long numerator, long denominator) {
        return numerator >= 0L ? numerator / denominator : (numerator + 1L) / denominator - 1L;
    }

    static final long julianDayToMillis(long julian) {
        return (julian - 2440588L) * 86400000L;
    }

    static final long zoneOffset(TimeZone zone, int year, int month, int day, int dow,
            int millisInDay) {
        return zone.getOffset(year < 0 ? 0 : 1, year, month, day, dow, millisInDay);
    }

    static long getMillis(int year, int month, int day, int millisInDay, TimeZone zone) {
        boolean isGregorian = year >= 1582;
        long julianDay = computeJulianDay(isGregorian, year, month, day);
        long millis = (julianDay - 2440588L) * 86400000L;

        if (isGregorian != millis >= -12219292800000L) {
            julianDay = computeJulianDay(!isGregorian, year, month, day);
            millis = (julianDay - 2440588L) * 86400000L;
        }

        millis += millisInDay;

        return millis
                - zoneOffset(zone, year, month, day, julianDayToDayOfWeek(julianDay), millisInDay);
    }

    static final int julianDayToDayOfWeek(long julian) {
        int dayOfWeek = (int) ((julian + 1L) % 7L);

        return dayOfWeek + (dayOfWeek < 0 ? 8 : 1);
    }

    static long calculateEpochOffset(TimeZone zone) {
        return zoneOffset(zone, 1970, 0, 1, 5, 0);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.DateTimeCommonAccessor JD-Core Version: 0.6.0
 */