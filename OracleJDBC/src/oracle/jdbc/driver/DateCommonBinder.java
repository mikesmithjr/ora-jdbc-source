package oracle.jdbc.driver;

import java.util.TimeZone;

abstract class DateCommonBinder extends Binder {
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

    static final int[] MONTH_LENGTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    static final int[] LEAP_MONTH_LENGTH = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    static final int ORACLE_DATE_CENTURY = 0;
    static final int ORACLE_DATE_YEAR = 1;
    static final int ORACLE_DATE_MONTH = 2;
    static final int ORACLE_DATE_DAY = 3;
    static final int ORACLE_DATE_HOUR = 4;
    static final int ORACLE_DATE_MIN = 5;
    static final int ORACLE_DATE_SEC = 6;
    static final int ORACLE_DATE_NANO1 = 7;
    static final int ORACLE_DATE_NANO2 = 8;
    static final int ORACLE_DATE_NANO3 = 9;
    static final int ORACLE_DATE_NANO4 = 10;

    static final long floorDivide(long numerator, long denominator) {
        return numerator >= 0L ? numerator / denominator : (numerator + 1L) / denominator - 1L;
    }

    static final int floorDivide(int numerator, int denominator) {
        return numerator >= 0 ? numerator / denominator : (numerator + 1) / denominator - 1;
    }

    static final int floorDivide(int numerator, int denominator, int[] remainder) {
        if (numerator >= 0) {
            remainder[0] = (numerator % denominator);

            return numerator / denominator;
        }

        int quotient = (numerator + 1) / denominator - 1;

        remainder[0] = (numerator - quotient * denominator);

        return quotient;
    }

    static final int floorDivide(long numerator, int denominator, int[] remainder) {
        if (numerator >= 0L) {
            remainder[0] = (int) (numerator % denominator);

            return (int) (numerator / denominator);
        }

        int quotient = (int) ((numerator + 1L) / denominator - 1L);

        remainder[0] = (int) (numerator - quotient * denominator);

        return quotient;
    }

    static final long zoneOffset(TimeZone zone, int year, int month, int day, int dow,
            int millisInDay) {
        return zone.getOffset(year < 0 ? 0 : 1, year, month, day, dow, millisInDay);
    }

    static void setOracleNanos(long nanos, byte[] oracleDate, int off) {
        oracleDate[(10 + off)] = (byte) (int) (nanos & 0xFF);
        oracleDate[(9 + off)] = (byte) (int) (nanos >> 8 & 0xFF);
        oracleDate[(8 + off)] = (byte) (int) (nanos >> 16 & 0xFF);
        oracleDate[(7 + off)] = (byte) (int) (nanos >> 24 & 0xFF);
    }

    static void setOracleHMS(int millis, byte[] oracleDate, int off) {
        millis /= 1000;
        oracleDate[(6 + off)] = (byte) (millis % 60 + 1);
        millis /= 60;
        oracleDate[(5 + off)] = (byte) (millis % 60 + 1);
        millis /= 60;
        oracleDate[(4 + off)] = (byte) (millis + 1);
    }

    static int setOracleCYMD(long time, byte[] oracleDate, int off, OraclePreparedStatement stmt) {
        TimeZone zone = stmt.getDefaultTimeZone();

        int rawOffset = zone.getRawOffset();
        long localMillis = time + rawOffset;
        int dayOfWeek;
        int year;
        int dayOfYear;
        boolean isLeap;
        if (localMillis >= -12219292800000L) {
            long gregorianEpochDay = 2440588L + floorDivide(localMillis, 86400000L) - 1721426L;
            int n400;
            int n100;
            int n4;
            int n1;
            if (gregorianEpochDay > 0L) {
                n400 = (int) (gregorianEpochDay / 146097L);
                dayOfYear = (int) (gregorianEpochDay % 146097L);
                n100 = dayOfYear / 36524;
                dayOfYear %= 36524;
                n4 = dayOfYear / 1461;
                dayOfYear %= 1461;
                n1 = dayOfYear / 365;
                dayOfYear %= 365;
            } else {
                int[] rem = new int[1];

                n400 = floorDivide(gregorianEpochDay, 146097, rem);
                n100 = floorDivide(rem[0], 36524, rem);
                n4 = floorDivide(rem[0], 1461, rem);
                n1 = floorDivide(rem[0], 365, rem);
                dayOfYear = rem[0];
            }

            year = 400 * n400 + 100 * n100 + 4 * n4 + n1;

            if ((n100 == 4) || (n1 == 4)) {
                dayOfYear = 365;
            } else {
                year++;
            }

            isLeap = ((year & 0x3) == 0) && ((year % 100 != 0) || (year % 400 == 0));

            dayOfWeek = (int) ((gregorianEpochDay + 1L) % 7L);
        } else {
            long julianEpochDay = 2440588L + floorDivide(localMillis, 86400000L) - 1721424L;

            year = (int) floorDivide(4L * julianEpochDay + 1464L, 1461L);

            long january1 = 365 * (year - 1) + floorDivide(year - 1, 4);

            dayOfYear = (int) (julianEpochDay - january1);
            isLeap = (year & 0x3) == 0;

            dayOfWeek = (int) ((julianEpochDay - 1L) % 7L);
        }

        int correction = 0;
        int march1 = isLeap ? 60 : 59;

        if (dayOfYear >= march1) {
            correction = isLeap ? 1 : 2;
        }

        int month = (12 * (dayOfYear + correction) + 6) / 367;
        int date = dayOfYear - (isLeap ? LEAP_NUM_DAYS[month] : NUM_DAYS[month]) + 1;

        dayOfWeek += (dayOfWeek < 0 ? 8 : 1);

        long days = localMillis / 86400000L;
        int millisInDay = (int) (localMillis - days * 86400000L);

        if (millisInDay < 0) {
            millisInDay = (int) (millisInDay + 86400000L);
        }

        long dstOffset = zoneOffset(zone, year, month, date, dayOfWeek, millisInDay) - rawOffset;

        millisInDay = (int) (millisInDay + dstOffset);

        if (millisInDay >= 86400000L) {
            millisInDay = (int) (millisInDay - 86400000L);

            date++;
            if (date > (isLeap ? LEAP_MONTH_LENGTH[month] : MONTH_LENGTH[month])) {
                date = 1;

                month++;
                if (month == 12) {
                    month = 0;
                    year++;
                }
            }

        }

        if (year <= 0) {
            year--;
        }
        oracleDate[(0 + off)] = (byte) (year / 100 + 100);
        oracleDate[(1 + off)] = (byte) (year % 100 + 100);
        oracleDate[(2 + off)] = (byte) (month + 1);
        oracleDate[(3 + off)] = (byte) date;

        return millisInDay;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.DateCommonBinder JD-Core Version: 0.6.0
 */