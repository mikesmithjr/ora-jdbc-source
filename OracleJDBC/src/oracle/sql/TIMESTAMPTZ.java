// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: TIMESTAMPTZ.java

package oracle.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import oracle.jdbc.OracleConnection;

// Referenced classes of package oracle.sql:
// Datum, DATE, OffsetDST, TIMEZONETAB,
// ZONEIDMAP

public class TIMESTAMPTZ extends Datum {

    private static int CENTURY_DEFAULT = 119;
    private static int DECADE_DEFAULT = 100;
    private static int MONTH_DEFAULT = 1;
    private static int DAY_DEFAULT = 1;
    private static int DECADE_INIT = 170;
    private static int HOUR_MILLISECOND = 0x36ee80;
    private static int MINUTE_MILLISECOND = 60000;
    private static int JAVA_YEAR = 1970;
    private static int JAVA_MONTH = 0;
    private static int JAVA_DATE = 1;
    private static int SIZE_TIMESTAMPTZ = 13;
    private static int SIZE_TIMESTAMP = 11;
    private static int OFFSET_HOUR = 20;
    private static int OFFSET_MINUTE = 60;
    private static byte REGIONIDBIT = -128;

    public TIMESTAMPTZ() {
        super(initTimestamptz());
    }

    public TIMESTAMPTZ(byte abyte0[]) {
        super(abyte0);
    }

    public TIMESTAMPTZ(Connection connection, Date date) throws SQLException {
        super(toBytes(connection, date));
    }

    public TIMESTAMPTZ(Connection connection, Date date, Calendar calendar) throws SQLException {
        super(toBytes(connection, date, calendar));
    }

    public TIMESTAMPTZ(Connection connection, Time time) throws SQLException {
        super(toBytes(connection, time));
    }

    public TIMESTAMPTZ(Connection connection, Time time, Calendar calendar) throws SQLException {
        super(toBytes(connection, time, calendar));
    }

    public TIMESTAMPTZ(Connection connection, Timestamp timestamp) throws SQLException {
        super(toBytes(connection, timestamp));
    }

    public TIMESTAMPTZ(Connection connection, Timestamp timestamp, Calendar calendar)
            throws SQLException {
        super(toBytes(connection, timestamp, calendar));
    }

    public TIMESTAMPTZ(Connection connection, DATE date) throws SQLException {
        super(toBytes(connection, date));
    }

    public TIMESTAMPTZ(Connection connection, String s) throws SQLException {
        super(toBytes(connection, s));
    }

    public TIMESTAMPTZ(Connection connection, String s, Calendar calendar) throws SQLException {
        super(toBytes(connection, s, calendar));
    }

    public static Date toDate(Connection connection, byte abyte0[]) throws SQLException {
        int ai[] = new int[SIZE_TIMESTAMPTZ];
        for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
            ai[j] = abyte0[j] & 0xff;
        }

        int k = getJavaYear(ai[0], ai[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, k);
        calendar.set(2, ai[2] - 1);
        calendar.set(5, ai[3]);
        calendar.set(11, ai[4] - 1);
        calendar.set(12, ai[5] - 1);
        calendar.set(13, ai[6] - 1);
        calendar.set(14, 0);
        if ((ai[11] & REGIONIDBIT) != 0) {
            int l = getHighOrderbits(ai[11]);
            l += getLowOrderbits(ai[12]);
            if (TIMEZONETAB.checkID(l)) {
                TIMEZONETAB.updateTable(connection, l);
            }
            int i = TIMEZONETAB.getOffset(calendar, l);
            calendar.add(10, i / HOUR_MILLISECOND);
            calendar.add(12, (i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        } else {
            calendar.add(10, ai[11] - OFFSET_HOUR);
            calendar.add(12, ai[12] - OFFSET_MINUTE);
        }
        long l1 = calendar.getTime().getTime();
        return new Date(l1);
    }

    public static Time toTime(Connection connection, byte abyte0[]) throws SQLException {
        int ai[] = new int[SIZE_TIMESTAMPTZ];
        for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
            ai[j] = abyte0[j] & 0xff;
        }

        Calendar calendar = Calendar.getInstance();
        int k = getJavaYear(ai[0], ai[1]);
        calendar.set(1, k);
        calendar.set(2, ai[2] - 1);
        calendar.set(5, ai[3]);
        calendar.set(11, ai[4] - 1);
        calendar.set(12, ai[5] - 1);
        calendar.set(13, ai[6] - 1);
        calendar.set(14, 0);
        if ((ai[11] & REGIONIDBIT) != 0) {
            int l = getHighOrderbits(ai[11]);
            l += getLowOrderbits(ai[12]);
            if (TIMEZONETAB.checkID(l)) {
                TIMEZONETAB.updateTable(connection, l);
            }
            int i = TIMEZONETAB.getOffset(calendar, l);
            calendar.add(10, i / HOUR_MILLISECOND);
            calendar.add(12, (i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        } else {
            calendar.add(10, ai[11] - OFFSET_HOUR);
            calendar.add(12, ai[12] - OFFSET_MINUTE);
        }
        return new Time(calendar.get(11), calendar.get(12), calendar.get(13));
    }

    public static DATE toDATE(Connection connection, byte abyte0[]) throws SQLException {
        int ai[] = new int[SIZE_TIMESTAMPTZ];
        for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
            ai[j] = abyte0[j] & 0xff;
        }

        Calendar calendar = Calendar.getInstance();
        int k = getJavaYear(ai[0], ai[1]);
        calendar.set(1, k);
        calendar.set(2, ai[2] - 1);
        calendar.set(5, ai[3]);
        calendar.set(11, ai[4] - 1);
        calendar.set(12, ai[5] - 1);
        calendar.set(13, ai[6] - 1);
        calendar.set(14, 0);
        if ((ai[11] & REGIONIDBIT) != 0) {
            int l = getHighOrderbits(ai[11]);
            l += getLowOrderbits(ai[12]);
            if (TIMEZONETAB.checkID(l)) {
                TIMEZONETAB.updateTable(connection, l);
            }
            int i = TIMEZONETAB.getOffset(calendar, l);
            calendar.add(10, i / HOUR_MILLISECOND);
            calendar.add(12, (i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        } else {
            calendar.add(10, ai[11] - OFFSET_HOUR);
            calendar.add(12, ai[12] - OFFSET_MINUTE);
        }
        long l1 = calendar.getTime().getTime();
        return new DATE(new Date(l1));
    }

    public static Timestamp toTimestamp(Connection connection, byte abyte0[]) throws SQLException {
        int ai[] = new int[SIZE_TIMESTAMPTZ];
        for (int j = 0; j < SIZE_TIMESTAMPTZ; j++) {
            ai[j] = abyte0[j] & 0xff;
        }

        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        int k = getJavaYear(ai[0], ai[1]);
        calendar.set(1, k);
        calendar.set(2, ai[2] - 1);
        calendar.set(5, ai[3]);
        calendar.set(11, ai[4] - 1);
        calendar.set(12, ai[5] - 1);
        calendar.set(13, ai[6] - 1);
        calendar.set(14, 0);
        long l = calendar.getTime().getTime();
        if ((ai[11] & REGIONIDBIT) != 0) {
            int i1 = getHighOrderbits(ai[11]);
            i1 += getLowOrderbits(ai[12]);
            if (TIMEZONETAB.checkID(i1)) {
                TIMEZONETAB.updateTable(connection, i1);
            }
            int i = TIMEZONETAB.getOffset(calendar, i1);
            l += i;
            if (!calendar.getTimeZone().inDaylightTime(calendar.getTime())
                    && calendar1.getTimeZone().inDaylightTime(new Timestamp(l))) {
                TimeZone timezone = calendar1.getTimeZone();
                if (timezone instanceof SimpleTimeZone) {
                    l -= ((SimpleTimeZone) timezone).getDSTSavings();
                } else {
                    l -= 0x36ee80L;
                }
            }
            if (calendar.getTimeZone().inDaylightTime(calendar.getTime())
                    && !calendar1.getTimeZone().inDaylightTime(new Timestamp(l))) {
                TimeZone timezone1 = calendar1.getTimeZone();
                if (timezone1 instanceof SimpleTimeZone) {
                    l += ((SimpleTimeZone) timezone1).getDSTSavings();
                } else {
                    l += 0x36ee80L;
                }
            }
        } else {
            calendar.add(10, ai[11] - OFFSET_HOUR);
            calendar.add(12, ai[12] - OFFSET_MINUTE);
            l = calendar.getTime().getTime();
        }
        Timestamp timestamp = new Timestamp(l);
        int j1 = ai[7] << 24;
        j1 |= ai[8] << 16;
        j1 |= ai[9] << 8;
        j1 |= ai[10] & 0xff;
        timestamp.setNanos(j1);
        return timestamp;
    }

    public static String toString(Connection connection, byte abyte0[]) throws SQLException {
        Timestamp timestamp = toTimestamp(connection, abyte0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int i = calendar.get(1);
        int j = calendar.get(2) + 1;
        int k = calendar.get(5);
        int l = calendar.get(11);
        int i1 = calendar.get(12);
        int j1 = calendar.get(13);
        int k1 = 0;
        k1 = (abyte0[7] & 0xff) << 24;
        k1 |= (abyte0[8] & 0xff) << 16;
        k1 |= (abyte0[9] & 0xff) << 8;
        k1 |= abyte0[10] & 0xff & 0xff;
        String s;
        if ((abyte0[11] & REGIONIDBIT) != 0) {
            int l1 = getHighOrderbits(abyte0[11]);
            l1 += getLowOrderbits(abyte0[12]);
            s = new String(ZONEIDMAP.getRegion(l1));
        } else {
            int i2 = abyte0[11] - OFFSET_HOUR;
            int j2 = abyte0[12] - OFFSET_MINUTE;
            s = new String(i2 + ":" + j2);
        }
        return i + "-" + j + "-" + k + " " + l + "." + i1 + "." + j1 + "." + k1 + " " + s;
    }

    public Timestamp timestampValue(Connection connection) throws SQLException {
        return toTimestamp(connection, getBytes());
    }

    public byte[] toBytes() {
        return getBytes();
    }

    public static byte[] toBytes(Connection connection, Date date) throws SQLException {
        if (date == null) {
            return null;
        }
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        String s = ((OracleConnection) connection).getSessionTimeZone();
        Calendar calendar;
        if (s == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(s));
        }
        calendar.setTime(date);
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(date)) {
            flag = true;
        } else {
            flag = false;
        }
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s1 = new String(calendar.getTimeZone().getID());
            int j = ZONEIDMAP.getID(s1);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(connection, j);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar, j, offsetdst);
                i = offsetdst.getOFFSET();
                if (flag && byte0 == 1) {
                    if (offsetdst.getDSTFLAG() == 0) {
                        i += HOUR_MILLISECOND;
                    } else {
                        throw new SQLException();
                    }
                }
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s1));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s1));
            }
        }
        calendar.add(10, -(i / HOUR_MILLISECOND));
        calendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        abyte0[0] = (byte) (calendar.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar.get(2) + 1);
        abyte0[3] = (byte) calendar.get(5);
        abyte0[4] = (byte) (calendar.get(11) + 1);
        abyte0[5] = (byte) (calendar.get(12) + 1);
        abyte0[6] = (byte) (calendar.get(13) + 1);
        abyte0[7] = 0;
        abyte0[8] = 0;
        abyte0[9] = 0;
        abyte0[10] = 0;
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, Date date, Calendar calendar)
            throws SQLException {
        if (date == null) {
            return null;
        }
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(date)) {
            flag = true;
        } else {
            flag = false;
        }
        calendar1.set(11, 0);
        calendar1.set(12, 0);
        calendar1.set(13, 0);
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s = new String(calendar.getTimeZone().getID());
            int j = ZONEIDMAP.getID(s);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(connection, j);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar1, j, offsetdst);
                i = offsetdst.getOFFSET();
                if (flag && byte0 == 1) {
                    if (offsetdst.getDSTFLAG() == 0) {
                        i += HOUR_MILLISECOND;
                    } else {
                        throw new SQLException();
                    }
                }
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s));
            }
        }
        calendar1.add(10, -(i / HOUR_MILLISECOND));
        calendar1.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        abyte0[0] = (byte) (calendar1.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar1.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar1.get(2) + 1);
        abyte0[3] = (byte) calendar1.get(5);
        abyte0[4] = (byte) (calendar1.get(11) + 1);
        abyte0[5] = (byte) (calendar1.get(12) + 1);
        abyte0[6] = (byte) (calendar1.get(13) + 1);
        abyte0[7] = 0;
        abyte0[8] = 0;
        abyte0[9] = 0;
        abyte0[10] = 0;
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, Time time) throws SQLException {
        if (time == null) {
            return null;
        }
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        String s = ((OracleConnection) connection).getSessionTimeZone();
        Calendar calendar;
        if (s == null) {
            calendar = Calendar.getInstance();
        } else {
            calendar = Calendar.getInstance(TimeZone.getTimeZone(s));
        }
        calendar.setTime(time);
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(time)) {
            flag = true;
        } else {
            flag = false;
        }
        calendar.set(1, (CENTURY_DEFAULT - 100) * 100 + DECADE_DEFAULT % 100);
        calendar.set(2, MONTH_DEFAULT - 1);
        calendar.set(5, DAY_DEFAULT);
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s1 = new String(calendar.getTimeZone().getID());
            int j = ZONEIDMAP.getID(s1);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(connection, j);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar, j, offsetdst);
                i = offsetdst.getOFFSET();
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s1));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s1));
            }
        }
        calendar.add(10, -(i / HOUR_MILLISECOND));
        calendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        abyte0[0] = (byte) (calendar.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar.get(2) + 1);
        abyte0[3] = (byte) calendar.get(5);
        abyte0[4] = (byte) (calendar.get(11) + 1);
        abyte0[5] = (byte) (calendar.get(12) + 1);
        abyte0[6] = (byte) (calendar.get(13) + 1);
        abyte0[7] = 0;
        abyte0[8] = 0;
        abyte0[9] = 0;
        abyte0[10] = 0;
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, Time time, Calendar calendar)
            throws SQLException {
        if (time == null) {
            return null;
        }
        Calendar calendar1 = Calendar.getInstance();
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        calendar1.setTime(time);
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(time)) {
            flag = true;
        } else {
            flag = false;
        }
        calendar1.set(1, (CENTURY_DEFAULT - 100) * 100 + DECADE_DEFAULT % 100);
        calendar1.set(2, MONTH_DEFAULT - 1);
        calendar1.set(5, DAY_DEFAULT);
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s = new String(calendar.getTimeZone().getID());
            int j = ZONEIDMAP.getID(s);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(connection, j);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar1, j, offsetdst);
                i = offsetdst.getOFFSET();
                if (flag && byte0 == 1) {
                    if (offsetdst.getDSTFLAG() == 0) {
                        i += HOUR_MILLISECOND;
                    } else {
                        throw new SQLException();
                    }
                }
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s));
            }
        }
        calendar1.add(11, -(i / HOUR_MILLISECOND));
        calendar1.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        abyte0[0] = (byte) (calendar1.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar1.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar1.get(2) + 1);
        abyte0[3] = (byte) calendar1.get(5);
        abyte0[4] = (byte) (calendar1.get(11) + 1);
        abyte0[5] = (byte) (calendar1.get(12) + 1);
        abyte0[6] = (byte) (calendar1.get(13) + 1);
        abyte0[7] = 0;
        abyte0[8] = 0;
        abyte0[9] = 0;
        abyte0[10] = 0;
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, Timestamp timestamp) throws SQLException {
        if (timestamp == null) {
            return null;
        }
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        long l = 0L;
        String s = ((OracleConnection) connection).getSessionTimeZone();
        if (s == null) {
            s = TimeZone.getDefault().getID();
            l = timestamp.getTime();
        } else {
            int j = timestamp.getNanos();
            Calendar calendar1 = Calendar.getInstance();
            TimeZone timezone = TimeZone.getTimeZone(s);
            calendar1.setTimeZone(timezone);
            calendar1.setTime(timestamp);
            int i1 = calendar1.get(1);
            int j1 = calendar1.get(2) + 1;
            int k1 = calendar1.get(5);
            int l1 = calendar1.get(11);
            int i2 = calendar1.get(12);
            int j2 = calendar1.get(13);
            double d = j / 0xf4240;
            String s2 = (new Integer(i1)).toString() + "/" + (new Integer(j1)).toString() + "/"
                    + (new Integer(k1)).toString() + " " + (new Integer(l1)).toString() + ":"
                    + (new Integer(i2)).toString() + ":" + (new Integer(j2)).toString() + ":"
                    + (new Double(d)).toString();
            SimpleDateFormat simpledateformat = new SimpleDateFormat("y/M/d H:m:s:S");
            java.util.Date date = null;
            try {
                date = simpledateformat.parse(s2);
            } catch (ParseException parseexception) {
                throw new SQLException(parseexception.getMessage());
            }
            l = date.getTime();
        }
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(s));
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(timestamp)) {
            flag = true;
        } else {
            flag = false;
        }
        calendar.setTime(timestamp);
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s1 = new String(calendar.getTimeZone().getID());
            int k = ZONEIDMAP.getID(s1);
            if (k == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(k)) {
                    TIMEZONETAB.updateTable(connection, k);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar, k, offsetdst);
                i = offsetdst.getOFFSET();
                if (flag && byte0 == 1) {
                    if (offsetdst.getDSTFLAG() == 0) {
                        i += HOUR_MILLISECOND;
                    } else {
                        throw new SQLException();
                    }
                }
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s1));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s1));
            }
        }
        l -= i;
        Timestamp timestamp1 = new Timestamp(l);
        calendar.setTime(timestamp1);
        calendar.setTimeZone(TimeZone.getDefault());
        abyte0[0] = (byte) (calendar.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar.get(2) + 1);
        abyte0[3] = (byte) calendar.get(5);
        abyte0[4] = (byte) (calendar.get(11) + 1);
        abyte0[5] = (byte) (calendar.get(12) + 1);
        abyte0[6] = (byte) (calendar.get(13) + 1);
        abyte0[7] = (byte) (timestamp.getNanos() >> 24);
        abyte0[8] = (byte) (timestamp.getNanos() >> 16 & 0xff);
        abyte0[9] = (byte) (timestamp.getNanos() >> 8 & 0xff);
        abyte0[10] = (byte) (timestamp.getNanos() & 0xff);
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, Timestamp timestamp, Calendar calendar)
            throws SQLException {
        if (timestamp == null) {
            return null;
        }
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(timestamp);
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(timestamp)) {
            flag = true;
        } else {
            flag = false;
        }
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s = new String(calendar.getTimeZone().getID());
            int j = ZONEIDMAP.getID(s);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(connection, j);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar1, j, offsetdst);
                i = offsetdst.getOFFSET();
                if (flag && byte0 == 1) {
                    if (offsetdst.getDSTFLAG() == 0) {
                        i += HOUR_MILLISECOND;
                    } else {
                        throw new SQLException();
                    }
                }
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s));
            }
        }
        calendar1.add(10, -(i / HOUR_MILLISECOND));
        calendar1.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        abyte0[0] = (byte) (calendar1.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar1.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar1.get(2) + 1);
        abyte0[3] = (byte) calendar1.get(5);
        abyte0[4] = (byte) (calendar1.get(11) + 1);
        abyte0[5] = (byte) (calendar1.get(12) + 1);
        abyte0[6] = (byte) (calendar1.get(13) + 1);
        abyte0[7] = (byte) (timestamp.getNanos() >> 24);
        abyte0[8] = (byte) (timestamp.getNanos() >> 16 & 0xff);
        abyte0[9] = (byte) (timestamp.getNanos() >> 8 & 0xff);
        abyte0[10] = (byte) (timestamp.getNanos() & 0xff);
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, DATE date) throws SQLException {
        if (date == null) {
            return null;
        }
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DATE.toDate(date.toBytes()));
        boolean flag;
        if (calendar.getTimeZone().inDaylightTime(DATE.toDate(date.toBytes()))) {
            flag = true;
        } else {
            flag = false;
        }
        int i;
        if (calendar.getTimeZone().getID() == "Custom") {
            i = calendar.getTimeZone().getRawOffset();
            abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
            abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
        } else {
            String s = new String(calendar.getTimeZone().getID());
            int j = ZONEIDMAP.getID(s);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (calendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                i = calendar.getTimeZone().getRawOffset();
                abyte0[11] = (byte) (i / HOUR_MILLISECOND + OFFSET_HOUR);
                abyte0[12] = (byte) ((i % HOUR_MILLISECOND) / MINUTE_MILLISECOND + OFFSET_MINUTE);
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(connection, j);
                }
                OffsetDST offsetdst = new OffsetDST();
                byte byte0 = TIMEZONETAB.getLocalOffset(calendar, j, offsetdst);
                i = offsetdst.getOFFSET();
                if (flag && byte0 == 1) {
                    if (offsetdst.getDSTFLAG() == 0) {
                        i += HOUR_MILLISECOND;
                    } else {
                        throw new SQLException();
                    }
                }
                abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s));
                abyte0[11] |= REGIONIDBIT;
                abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s));
            }
        }
        calendar.add(10, -(i / HOUR_MILLISECOND));
        calendar.add(12, -(i % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
        abyte0[0] = (byte) (calendar.get(1) / 100 + 100);
        abyte0[1] = (byte) (calendar.get(1) % 100 + 100);
        abyte0[2] = (byte) (calendar.get(2) + 1);
        abyte0[3] = (byte) calendar.get(5);
        abyte0[4] = (byte) (calendar.get(11) + 1);
        abyte0[5] = (byte) (calendar.get(12) + 1);
        abyte0[6] = (byte) (calendar.get(13) + 1);
        abyte0[7] = 0;
        abyte0[8] = 0;
        abyte0[9] = 0;
        abyte0[10] = 0;
        return abyte0;
    }

    public static byte[] toBytes(Connection connection, String s) throws SQLException {
        return toBytes(connection, Timestamp.valueOf(s));
    }

    public static byte[] toBytes(Connection connection, String s, Calendar calendar)
            throws SQLException {
        return toBytes(connection, Timestamp.valueOf(s), calendar);
    }

    public String stringValue(Connection connection) throws SQLException {
        return toString(connection, getBytes());
    }

    public Date dateValue(Connection connection) throws SQLException {
        return toDate(connection, getBytes());
    }

    public Time timeValue(Connection connection) throws SQLException {
        return toTime(connection, getBytes());
    }

    private static byte[] initTimestamptz() {
        byte abyte0[] = new byte[SIZE_TIMESTAMPTZ];
        Calendar calendar = Calendar.getInstance();
        abyte0[0] = (byte) CENTURY_DEFAULT;
        abyte0[1] = (byte) DECADE_INIT;
        abyte0[2] = (byte) MONTH_DEFAULT;
        abyte0[3] = (byte) DAY_DEFAULT;
        abyte0[4] = 1;
        abyte0[5] = 1;
        abyte0[6] = 1;
        abyte0[7] = 0;
        abyte0[8] = 0;
        abyte0[9] = 0;
        abyte0[10] = 0;
        String s = new String(calendar.getTimeZone().getID());
        abyte0[11] = (byte) setHighOrderbits(ZONEIDMAP.getID(s));
        abyte0[11] |= REGIONIDBIT;
        abyte0[12] = (byte) setLowOrderbits(ZONEIDMAP.getID(s));
        return abyte0;
    }

    public Object toJdbc() throws SQLException {
        return null;
    }

    public Object makeJdbcArray(int i) {
        Timestamp atimestamp[] = new Timestamp[i];
        return atimestamp;
    }

    public boolean isConvertibleTo(Class class1) {
        return class1.getName().compareTo("java.sql.Date") == 0
                || class1.getName().compareTo("java.sql.Time") == 0
                || class1.getName().compareTo("java.sql.Timestamp") == 0
                || class1.getName().compareTo("java.lang.String") == 0;
    }

    private static int setHighOrderbits(int i) {
        return (i & 0x1fc0) >> 6;
    }

    private static int setLowOrderbits(int i) {
        return (i & 0x3f) << 2;
    }

    private static int getHighOrderbits(int i) {
        return (i & 0x7f) << 6;
    }

    private static int getLowOrderbits(int i) {
        return (i & 0xfc) >> 2;
    }

    private static int getJavaYear(int i, int j) {
        return (i - 100) * 100 + (j - 100);
    }

}
