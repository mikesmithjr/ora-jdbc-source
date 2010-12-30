package oracle.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;

public class TIMESTAMPLTZ extends Datum {
    private static int SIZE_TIMESTAMPLTZ = 11;
    private static int SIZE_TIMESTAMPLTZ_NOFRAC = 7;

    private static int SIZE_DATE = 7;

    private static int CENTURY_DEFAULT = 119;
    private static int DECADE_DEFAULT = 100;
    private static int MONTH_DEFAULT = 1;
    private static int DAY_DEFAULT = 1;

    private static int DECADE_INIT = 170;

    private static int HOUR_MILLISECOND = 3600000;

    private static int MINUTE_MILLISECOND = 60000;

    private static int JAVA_YEAR = 1970;
    private static int JAVA_MONTH = 0;
    private static int JAVA_DATE = 1;

    private static boolean cached = false;
    private static Calendar dbtz;

    public TIMESTAMPLTZ() {
        super(initTimestampltz());
    }

    public TIMESTAMPLTZ(byte[] paramArrayOfByte) {
        super(paramArrayOfByte);
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, Time paramTime, Calendar paramCalendar)
            throws SQLException {
        super(toBytes(paramConnection, paramTime, paramCalendar));
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, java.sql.Date paramDate, Calendar paramCalendar)
            throws SQLException {
        super(toBytes(paramConnection, paramDate, paramCalendar));
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, Timestamp paramTimestamp, Calendar paramCalendar)
            throws SQLException {
        super(toBytes(paramConnection, paramTimestamp, paramCalendar));
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, DATE paramDATE, Calendar paramCalendar)
            throws SQLException {
        super(toBytes(paramConnection, paramDATE, paramCalendar));
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, String paramString, Calendar paramCalendar)
            throws SQLException {
        super(toBytes(paramConnection, paramString, paramCalendar));
    }

    public TIMESTAMPLTZ(Connection paramConnection, Calendar paramCalendar, Time paramTime)
            throws SQLException {
        super(toBytes(paramConnection, paramCalendar, paramTime));
    }

    public TIMESTAMPLTZ(Connection paramConnection, Calendar paramCalendar, java.sql.Date paramDate)
            throws SQLException {
        super(toBytes(paramConnection, paramCalendar, paramDate));
    }

    public TIMESTAMPLTZ(Connection paramConnection, Calendar paramCalendar, Timestamp paramTimestamp)
            throws SQLException {
        super(toBytes(paramConnection, paramCalendar, paramTimestamp));
    }

    public TIMESTAMPLTZ(Connection paramConnection, Calendar paramCalendar, DATE paramDATE)
            throws SQLException {
        super(toBytes(paramConnection, paramCalendar, paramDATE));
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, Calendar paramCalendar, String paramString)
            throws SQLException {
        super(toBytes(paramConnection, getSessCalendar(paramConnection), paramString));
    }

    public TIMESTAMPLTZ(Connection paramConnection, Time paramTime) throws SQLException {
        super(toBytes(paramConnection, getSessCalendar(paramConnection), paramTime));
    }

    public TIMESTAMPLTZ(Connection paramConnection, java.sql.Date paramDate) throws SQLException {
        super(toBytes(paramConnection, getSessCalendar(paramConnection), paramDate));
    }

    public TIMESTAMPLTZ(Connection paramConnection, Timestamp paramTimestamp) throws SQLException {
        super(toBytes(paramConnection, getSessCalendar(paramConnection), paramTimestamp));
    }

    public TIMESTAMPLTZ(Connection paramConnection, DATE paramDATE) throws SQLException {
        super(toBytes(paramConnection, getSessCalendar(paramConnection), paramDATE));
    }

    /** @deprecated */
    public TIMESTAMPLTZ(Connection paramConnection, String paramString) throws SQLException {
        super(toBytes(paramConnection, getSessCalendar(paramConnection), Timestamp
                .valueOf(paramString)));
    }

    public static java.sql.Date toDate(Connection paramConnection, byte[] paramArrayOfByte,
            Calendar paramCalendar) throws SQLException {
        int i = paramArrayOfByte.length;
        int j = 0;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        j = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        Calendar localCalendar = Calendar.getInstance();

        TimeZone.setDefault(paramCalendar.getTimeZone());

        paramCalendar.set(1, j);
        paramCalendar.set(2, arrayOfInt[2] - 1);
        paramCalendar.set(5, arrayOfInt[3]);
        paramCalendar.set(11, arrayOfInt[4] - 1);
        paramCalendar.set(12, arrayOfInt[5] - 1);
        paramCalendar.set(13, arrayOfInt[6] - 1);
        paramCalendar.set(14, 0);

        TimeZoneAdjust(paramConnection, paramCalendar, localCalendar);

        long l = localCalendar.getTime().getTime();

        return new java.sql.Date(l);
    }

    public static Time toTime(Connection paramConnection, byte[] paramArrayOfByte,
            Calendar paramCalendar) throws SQLException {
        int i = paramArrayOfByte.length;
        int j = 0;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        j = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        Calendar localCalendar = Calendar.getInstance();

        TimeZone.setDefault(paramCalendar.getTimeZone());

        paramCalendar.set(1, j);
        paramCalendar.set(2, arrayOfInt[2] - 1);
        paramCalendar.set(5, arrayOfInt[3]);
        paramCalendar.set(11, arrayOfInt[4] - 1);
        paramCalendar.set(12, arrayOfInt[5] - 1);
        paramCalendar.set(13, arrayOfInt[6] - 1);

        TimeZoneAdjust(paramConnection, paramCalendar, localCalendar);

        TimeZone.setDefault(localCalendar.getTimeZone());

        return new Time(localCalendar.get(11), localCalendar.get(12), localCalendar.get(13));
    }

    public static Timestamp toTimestamp(Connection paramConnection, byte[] paramArrayOfByte,
            Calendar paramCalendar) throws SQLException {
        int i = paramArrayOfByte.length;
        int j = 0;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        j = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        Calendar localCalendar = Calendar.getInstance();

        TimeZone.setDefault(paramCalendar.getTimeZone());

        paramCalendar.set(1, j);
        paramCalendar.set(2, arrayOfInt[2] - 1);
        paramCalendar.set(5, arrayOfInt[3]);
        paramCalendar.set(11, arrayOfInt[4] - 1);
        paramCalendar.set(12, arrayOfInt[5] - 1);
        paramCalendar.set(13, arrayOfInt[6] - 1);
        paramCalendar.set(14, 0);

        TimeZoneAdjust(paramConnection, paramCalendar, localCalendar);

        long l = localCalendar.getTime().getTime();

        TimeZone.setDefault(localCalendar.getTimeZone());

        Timestamp localTimestamp = new Timestamp(l);
        int k = 0;

        if (paramArrayOfByte.length == SIZE_TIMESTAMPLTZ) {
            k = arrayOfInt[7] << 24;
            k |= arrayOfInt[8] << 16;
            k |= arrayOfInt[9] << 8;
            k |= arrayOfInt[10] & 0xFF;
            localTimestamp.setNanos(k);
        } else {
            localTimestamp.setNanos(0);
        }
        return localTimestamp;
    }

    public static DATE toDATE(Connection paramConnection, byte[] paramArrayOfByte,
            Calendar paramCalendar) throws SQLException {
        int[] arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        int i = 0;

        for (i = 0; i < paramArrayOfByte.length; i++) {
            paramArrayOfByte[i] &= 255;
        }

        i = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        Calendar localCalendar = Calendar.getInstance();

        TimeZone.setDefault(paramCalendar.getTimeZone());

        paramCalendar.set(1, i);
        paramCalendar.set(2, arrayOfInt[2] - 1);
        paramCalendar.set(5, arrayOfInt[3]);
        paramCalendar.set(11, arrayOfInt[4] - 1);
        paramCalendar.set(12, arrayOfInt[5] - 1);
        paramCalendar.set(13, arrayOfInt[6] - 1);

        TimeZoneAdjust(paramConnection, paramCalendar, localCalendar);

        long l = localCalendar.getTime().getTime();

        TimeZone.setDefault(localCalendar.getTimeZone());

        return new DATE(new java.sql.Date(l));
    }

    public Timestamp timestampValue(Connection paramConnection, Calendar paramCalendar)
            throws SQLException {
        return toTimestamp(paramConnection, getBytes(), paramCalendar);
    }

    /** @deprecated */
    public static String toString(Connection paramConnection, byte[] paramArrayOfByte,
            Calendar paramCalendar) throws SQLException {
        Timestamp localTimestamp = toTimestamp(paramConnection, paramArrayOfByte, paramCalendar);

        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(localTimestamp);

        int i = localCalendar.get(1);
        int j = localCalendar.get(2) + 1;
        int k = localCalendar.get(5);
        int m = localCalendar.get(11);
        int n = localCalendar.get(12);
        int i1 = localCalendar.get(13);
        int i2 = 0;

        if (paramArrayOfByte.length == SIZE_TIMESTAMPLTZ) {
            i2 = (paramArrayOfByte[7] & 0xFF) << 24;
            i2 |= (paramArrayOfByte[8] & 0xFF) << 16;
            i2 |= (paramArrayOfByte[9] & 0xFF) << 8;
            i2 |= paramArrayOfByte[10] & 0xFF & 0xFF;
        }

        return i + "-" + j + "-" + k + " " + m + "." + n + "." + i1 + "." + i2;
    }

    public byte[] toBytes() {
        return getBytes();
    }

    /** @deprecated */
    public static byte[] toBytes(Connection paramConnection, Time paramTime, Calendar paramCalendar)
            throws SQLException {
        if (paramTime == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramTime);
        int i;
        if (paramCalendar.getTimeZone().inDaylightTime(paramTime))
            i = 1;
        else {
            i = 0;
        }
        localCalendar.set(1, (CENTURY_DEFAULT - 100) * 100 + DECADE_DEFAULT % 100);
        localCalendar.set(2, MONTH_DEFAULT - 1);
        localCalendar.set(5, DAY_DEFAULT);

        TimeZoneAdjust(paramConnection, localCalendar, paramCalendar);

        arrayOfByte[0] = (byte) (paramCalendar.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (paramCalendar.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (paramCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) paramCalendar.get(5);
        arrayOfByte[4] = (byte) (paramCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (paramCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (paramCalendar.get(13) + 1);

        TimeZone.setDefault(localCalendar.getTimeZone());

        return arrayOfByte;
    }

    /** @deprecated */
    public static byte[] toBytes(Connection paramConnection, java.sql.Date paramDate,
            Calendar paramCalendar) throws SQLException {
        if (paramDate == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramDate);
        int i;
        if (localCalendar.getTimeZone().inDaylightTime(paramDate))
            i = 1;
        else {
            i = 0;
        }
        localCalendar.set(11, 0);
        localCalendar.set(12, 0);
        localCalendar.set(13, 0);

        TimeZoneAdjust(paramConnection, localCalendar, paramCalendar);

        arrayOfByte[0] = (byte) (paramCalendar.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (paramCalendar.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (paramCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) paramCalendar.get(5);
        arrayOfByte[4] = (byte) (paramCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (paramCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (paramCalendar.get(13) + 1);

        TimeZone.setDefault(localCalendar.getTimeZone());
        return arrayOfByte;
    }

    /** @deprecated */
    public static byte[] toBytes(Connection paramConnection, Timestamp paramTimestamp,
            Calendar paramCalendar) throws SQLException {
        if (paramTimestamp == null) {
            return null;
        }

        int i = paramTimestamp.getNanos();
        byte[] arrayOfByte;
        if (i == 0)
            arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];
        else {
            arrayOfByte = new byte[SIZE_TIMESTAMPLTZ];
        }

        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramTimestamp);
        int j;
        if (localCalendar.getTimeZone().inDaylightTime(paramTimestamp))
            j = 1;
        else {
            j = 0;
        }

        TimeZoneAdjust(paramConnection, localCalendar, paramCalendar);

        arrayOfByte[0] = (byte) (paramCalendar.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (paramCalendar.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (paramCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) paramCalendar.get(5);
        arrayOfByte[4] = (byte) (paramCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (paramCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (paramCalendar.get(13) + 1);

        if (i != 0) {
            arrayOfByte[7] = (byte) (paramTimestamp.getNanos() >> 24);
            arrayOfByte[8] = (byte) (paramTimestamp.getNanos() >> 16 & 0xFF);
            arrayOfByte[9] = (byte) (paramTimestamp.getNanos() >> 8 & 0xFF);
            arrayOfByte[10] = (byte) (paramTimestamp.getNanos() & 0xFF);
        }

        TimeZone.setDefault(localCalendar.getTimeZone());

        return arrayOfByte;
    }

    /** @deprecated */
    public static byte[] toBytes(Connection paramConnection, DATE paramDATE, Calendar paramCalendar)
            throws SQLException {
        if (paramDATE == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];

        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(DATE.toDate(paramDATE.toBytes()));
        int i;
        if (localCalendar.getTimeZone().inDaylightTime(
                                                       new java.sql.Date(localCalendar.getTime()
                                                               .getTime())))
            i = 1;
        else {
            i = 0;
        }

        TimeZoneAdjust(paramConnection, localCalendar, paramCalendar);

        arrayOfByte[0] = (byte) (paramCalendar.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (paramCalendar.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (paramCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) paramCalendar.get(5);
        arrayOfByte[4] = (byte) (paramCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (paramCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (paramCalendar.get(13) + 1);

        TimeZone.setDefault(localCalendar.getTimeZone());
        return arrayOfByte;
    }

    public static byte[] toBytes(Connection paramConnection, String paramString,
            Calendar paramCalendar) throws SQLException {
        return toBytes(paramConnection, Timestamp.valueOf(paramString), paramCalendar);
    }

    public static java.sql.Date toDate(Connection paramConnection, byte[] paramArrayOfByte)
            throws SQLException {
        int i = paramArrayOfByte.length;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (int j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        Calendar localCalendar = getSessCalendar(paramConnection);

        int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        TimeZone.setDefault(dbtz.getTimeZone());

        dbtz.set(1, k);
        dbtz.set(2, arrayOfInt[2] - 1);
        dbtz.set(5, arrayOfInt[3]);
        dbtz.set(11, arrayOfInt[4] - 1);
        dbtz.set(12, arrayOfInt[5] - 1);
        dbtz.set(13, arrayOfInt[6] - 1);
        dbtz.set(14, 0);

        TimeZoneAdjust(paramConnection, dbtz, localCalendar);

        long l = localCalendar.getTime().getTime();

        TimeZone.setDefault(localTimeZone);

        return new java.sql.Date(l);
    }

    public static Time toTime(Connection paramConnection, byte[] paramArrayOfByte)
            throws SQLException {
        int i = paramArrayOfByte.length;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (int j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        Calendar localCalendar = getSessCalendar(paramConnection);

        int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        TimeZone.setDefault(dbtz.getTimeZone());

        dbtz.set(1, k);
        dbtz.set(2, arrayOfInt[2] - 1);
        dbtz.set(5, arrayOfInt[3]);
        dbtz.set(11, arrayOfInt[4] - 1);
        dbtz.set(12, arrayOfInt[5] - 1);
        dbtz.set(13, arrayOfInt[6] - 1);

        TimeZoneAdjust(paramConnection, dbtz, localCalendar);

        TimeZone.setDefault(localTimeZone);

        return new Time(localCalendar.get(11), localCalendar.get(12), localCalendar.get(13));
    }

    public static Timestamp toTimestamp(Connection paramConnection, byte[] paramArrayOfByte)
            throws SQLException {
        int i = paramArrayOfByte.length;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (int j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        Calendar localCalendar = getSessCalendar(paramConnection);

        int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        TimeZone.setDefault(dbtz.getTimeZone());

        dbtz.set(1, k);
        dbtz.set(2, arrayOfInt[2] - 1);
        dbtz.set(5, arrayOfInt[3]);
        dbtz.set(11, arrayOfInt[4] - 1);
        dbtz.set(12, arrayOfInt[5] - 1);
        dbtz.set(13, arrayOfInt[6] - 1);
        dbtz.set(14, 0);

        TimeZoneAdjust(paramConnection, dbtz, localCalendar);

        long l = localCalendar.getTime().getTime();

        TimeZone.setDefault(localTimeZone);

        Timestamp localTimestamp = new Timestamp(l);
        int m = 0;

        if (paramArrayOfByte.length == SIZE_TIMESTAMPLTZ) {
            m = arrayOfInt[7] << 24;
            m |= arrayOfInt[8] << 16;
            m |= arrayOfInt[9] << 8;
            m |= arrayOfInt[10] & 0xFF;
            localTimestamp.setNanos(m);
        } else {
            localTimestamp.setNanos(m);
        }
        return localTimestamp;
    }

    public static DATE toDATE(Connection paramConnection, byte[] paramArrayOfByte)
            throws SQLException {
        int i = paramArrayOfByte.length;
        int[] arrayOfInt;
        if (i == SIZE_TIMESTAMPLTZ)
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ];
        else {
            arrayOfInt = new int[SIZE_TIMESTAMPLTZ_NOFRAC];
        }
        for (int j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        Calendar localCalendar = getSessCalendar(paramConnection);

        int k = getJavaYear(arrayOfInt[0], arrayOfInt[1]);

        TimeZone.setDefault(dbtz.getTimeZone());

        dbtz.set(1, k);
        dbtz.set(2, arrayOfInt[2] - 1);
        dbtz.set(5, arrayOfInt[3]);
        dbtz.set(11, arrayOfInt[4] - 1);
        dbtz.set(12, arrayOfInt[5] - 1);
        dbtz.set(13, arrayOfInt[6] - 1);

        TimeZoneAdjust(paramConnection, dbtz, localCalendar);

        long l = localCalendar.getTime().getTime();

        TimeZone.setDefault(localTimeZone);

        return new DATE(new java.sql.Date(l));
    }

    public static String toString(Connection paramConnection, byte[] paramArrayOfByte)
            throws SQLException {
        Timestamp localTimestamp = toTimestamp(paramConnection, paramArrayOfByte);
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(localTimestamp);

        int i = localCalendar.get(1);
        int j = localCalendar.get(2) + 1;
        int k = localCalendar.get(5);
        int m = localCalendar.get(11);
        int n = localCalendar.get(12);
        int i1 = localCalendar.get(13);
        int i2 = 0;

        if (paramArrayOfByte.length == SIZE_TIMESTAMPLTZ) {
            i2 = (paramArrayOfByte[7] & 0xFF) << 24;
            i2 |= (paramArrayOfByte[8] & 0xFF) << 16;
            i2 |= (paramArrayOfByte[9] & 0xFF) << 8;
            i2 |= paramArrayOfByte[10] & 0xFF;
        }

        return i + "-" + j + "-" + k + " " + m + "." + n + "." + i1 + "." + i2;
    }

    public static byte[] toBytes(Connection paramConnection, Calendar paramCalendar, Time paramTime)
            throws SQLException {
        if (paramTime == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        paramCalendar.setTime(paramTime);
        int i;
        if (dbtz.getTimeZone().inDaylightTime(paramTime))
            i = 1;
        else {
            i = 0;
        }
        paramCalendar.set(1, (CENTURY_DEFAULT - 100) * 100 + DECADE_DEFAULT % 100);
        paramCalendar.set(2, MONTH_DEFAULT - 1);
        paramCalendar.set(5, DAY_DEFAULT);

        TimeZoneAdjust(paramConnection, paramCalendar, dbtz);

        arrayOfByte[0] = (byte) (dbtz.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (dbtz.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (dbtz.get(2) + 1);
        arrayOfByte[3] = (byte) dbtz.get(5);
        arrayOfByte[4] = (byte) (dbtz.get(11) + 1);
        arrayOfByte[5] = (byte) (dbtz.get(12) + 1);
        arrayOfByte[6] = (byte) (dbtz.get(13) + 1);

        TimeZone.setDefault(localTimeZone);

        return arrayOfByte;
    }

    public static byte[] toBytes(Connection paramConnection, Calendar paramCalendar,
            java.sql.Date paramDate) throws SQLException {
        if (paramDate == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        paramCalendar.setTime(paramDate);
        int i;
        if (paramCalendar.getTimeZone().inDaylightTime(paramDate))
            i = 1;
        else {
            i = 0;
        }
        paramCalendar.set(11, 0);
        paramCalendar.set(12, 0);
        paramCalendar.set(13, 0);

        TimeZoneAdjust(paramConnection, paramCalendar, dbtz);

        arrayOfByte[0] = (byte) (dbtz.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (dbtz.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (dbtz.get(2) + 1);
        arrayOfByte[3] = (byte) dbtz.get(5);
        arrayOfByte[4] = (byte) (dbtz.get(11) + 1);
        arrayOfByte[5] = (byte) (dbtz.get(12) + 1);
        arrayOfByte[6] = (byte) (dbtz.get(13) + 1);

        TimeZone.setDefault(localTimeZone);
        return arrayOfByte;
    }

    public static byte[] toBytes(Connection paramConnection, Calendar paramCalendar,
            Timestamp paramTimestamp) throws SQLException {
        if (paramTimestamp == null) {
            return null;
        }

        int i = paramTimestamp.getNanos();
        byte[] arrayOfByte;
        if (i == 0)
            arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];
        else {
            arrayOfByte = new byte[SIZE_TIMESTAMPLTZ];
        }

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        paramCalendar.setTime(paramTimestamp);
        int j;
        if (paramCalendar.getTimeZone().inDaylightTime(paramTimestamp))
            j = 1;
        else {
            j = 0;
        }

        TimeZoneAdjust(paramConnection, paramCalendar, dbtz);

        arrayOfByte[0] = (byte) (dbtz.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (dbtz.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (dbtz.get(2) + 1);
        arrayOfByte[3] = (byte) dbtz.get(5);
        arrayOfByte[4] = (byte) (dbtz.get(11) + 1);
        arrayOfByte[5] = (byte) (dbtz.get(12) + 1);
        arrayOfByte[6] = (byte) (dbtz.get(13) + 1);

        if (i != 0) {
            arrayOfByte[7] = (byte) (paramTimestamp.getNanos() >> 24);
            arrayOfByte[8] = (byte) (paramTimestamp.getNanos() >> 16 & 0xFF);
            arrayOfByte[9] = (byte) (paramTimestamp.getNanos() >> 8 & 0xFF);
            arrayOfByte[10] = (byte) (paramTimestamp.getNanos() & 0xFF);
        }

        TimeZone.setDefault(localTimeZone);

        return arrayOfByte;
    }

    public static byte[] toBytes(Connection paramConnection, Calendar paramCalendar, DATE paramDATE)
            throws SQLException {
        if (paramDATE == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ_NOFRAC];

        TimeZone localTimeZone = TimeZone.getDefault();

        getDbTimeZone(paramConnection);

        paramCalendar.setTime(DATE.toDate(paramDATE.toBytes()));
        int i;
        if (paramCalendar.getTimeZone().inDaylightTime(
                                                       new java.sql.Date(paramCalendar.getTime()
                                                               .getTime())))
            i = 1;
        else {
            i = 0;
        }

        TimeZoneAdjust(paramConnection, paramCalendar, dbtz);

        arrayOfByte[0] = (byte) (dbtz.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (dbtz.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (dbtz.get(2) + 1);
        arrayOfByte[3] = (byte) dbtz.get(5);
        arrayOfByte[4] = (byte) (dbtz.get(11) + 1);
        arrayOfByte[5] = (byte) (dbtz.get(12) + 1);
        arrayOfByte[6] = (byte) (dbtz.get(13) + 1);

        TimeZone.setDefault(localTimeZone);
        return arrayOfByte;
    }

    public static byte[] toBytes(Connection paramConnection, Calendar paramCalendar,
            String paramString) throws SQLException {
        return toBytes(paramConnection, paramCalendar, Timestamp.valueOf(paramString));
    }

    public String stringValue(Connection paramConnection) throws SQLException {
        return toString(paramConnection, getBytes());
    }

    public String stringValue(Connection paramConnection, Calendar paramCalendar)
            throws SQLException {
        return toString(paramConnection, getBytes(), paramCalendar);
    }

    public java.sql.Date dateValue(Connection paramConnection, Calendar paramCalendar)
            throws SQLException {
        return toDate(paramConnection, getBytes(), paramCalendar);
    }

    public java.sql.Date dateValue(Connection paramConnection) throws SQLException {
        return toDate(paramConnection, getBytes());
    }

    public Time timeValue(Connection paramConnection) throws SQLException {
        return toTime(paramConnection, getBytes());
    }

    public Time timeValue(Connection paramConnection, Calendar paramCalendar) throws SQLException {
        return toTime(paramConnection, getBytes(), paramCalendar);
    }

    private static byte[] initTimestampltz() {
        byte[] arrayOfByte = new byte[SIZE_TIMESTAMPLTZ];

        arrayOfByte[0] = (byte) CENTURY_DEFAULT;
        arrayOfByte[1] = (byte) DECADE_INIT;
        arrayOfByte[2] = (byte) MONTH_DEFAULT;
        arrayOfByte[3] = (byte) DAY_DEFAULT;
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 1;
        arrayOfByte[6] = 1;

        return arrayOfByte;
    }

    public Object toJdbc() throws SQLException {
        return timestampValue();
    }

    public Object makeJdbcArray(int paramInt) {
        Timestamp[] arrayOfTimestamp = new Timestamp[paramInt];

        return arrayOfTimestamp;
    }

    public boolean isConvertibleTo(Class paramClass) {
        return (paramClass.getName().compareTo("java.sql.Date") == 0)
                || (paramClass.getName().compareTo("java.sql.Time") == 0)
                || (paramClass.getName().compareTo("java.sql.Timestamp") == 0)
                || (paramClass.getName().compareTo("java.lang.String") == 0);
    }

    static void TimeZoneAdjust(Connection paramConnection, Calendar paramCalendar1,
            Calendar paramCalendar2) throws SQLException {
        String str1 = new String(paramCalendar1.getTimeZone().getID());
        String str2 = new String(paramCalendar2.getTimeZone().getID());
        int i=0, j=0, m=0, k=0, n=0;

        if ((!str2.equals(str1)) && ((!str2.equals("Custom")) || (!str1.equals("Custom")))) {
            OffsetDST localOffsetDST = new OffsetDST();

            k = getZoneOffset(paramConnection, paramCalendar1, localOffsetDST);

            m = localOffsetDST.getOFFSET();

            paramCalendar1.add(11, -(m / HOUR_MILLISECOND));
            paramCalendar1.add(12, -(m % HOUR_MILLISECOND) / MINUTE_MILLISECOND);
            if (str2.equals("Custom")) {
                i = paramCalendar2.getTimeZone().getRawOffset();
            } else {
                n = ZONEIDMAP.getID(str2);

                if (n == ZONEIDMAP.INV_ZONEID) {
                    if (paramCalendar2.getTimeZone().useDaylightTime())
                        throw new SQLException("Timezone not supported");
                    i = paramCalendar2.getTimeZone().getRawOffset();
                } else {
                    if (TIMEZONETAB.checkID(n)) {
                        TIMEZONETAB.updateTable(paramConnection, n);
                    }

                    i = TIMEZONETAB.getOffset(paramCalendar1, n);
                }

            }

            paramCalendar1.add(11, i / HOUR_MILLISECOND);
            paramCalendar1.add(12, i % HOUR_MILLISECOND / MINUTE_MILLISECOND);
        }

        if ((str2.equals("Custom")) && (str1.equals("Custom"))) {
            j = paramCalendar1.getTimeZone().getRawOffset();
            k = paramCalendar2.getTimeZone().getRawOffset();
            m = 0;

            if (j != k) {
                m = j - k;
                m = m > 0 ? m : -m;
            }

            if (j > k) {
                m = -m;
            }
            paramCalendar1.add(11, m / HOUR_MILLISECOND);
            paramCalendar1.add(12, m % HOUR_MILLISECOND / MINUTE_MILLISECOND);
        }

        j = paramCalendar1.get(1);
        k = paramCalendar1.get(2);
        m = paramCalendar1.get(5);
        n = paramCalendar1.get(11);
        int i1 = paramCalendar1.get(12);
        int i2 = paramCalendar1.get(13);
        int i3 = paramCalendar1.get(14);

        TimeZone.setDefault(paramCalendar2.getTimeZone());

        paramCalendar2.set(1, j);
        paramCalendar2.set(2, k);
        paramCalendar2.set(5, m);
        paramCalendar2.set(11, n);
        paramCalendar2.set(12, i1);
        paramCalendar2.set(13, i2);
        paramCalendar2.set(14, i3);
    }

    private static int getJavaYear(int paramInt1, int paramInt2) {
        return (paramInt1 - 100) * 100 + (paramInt2 - 100);
    }

    private static byte getZoneOffset(Connection paramConnection, Calendar paramCalendar,
            OffsetDST paramOffsetDST) throws SQLException {
        byte i = 0;

        if (paramCalendar.getTimeZone().getID() == "Custom") {
            paramOffsetDST.setOFFSET(paramCalendar.getTimeZone().getRawOffset());
        } else {
            String str = new String(paramCalendar.getTimeZone().getID());

            int j = ZONEIDMAP.getID(str);
            if (j == ZONEIDMAP.INV_ZONEID) {
                if (paramCalendar.getTimeZone().useDaylightTime()) {
                    throw new SQLException("Timezone not supported");
                }
                paramOffsetDST.setOFFSET(paramCalendar.getTimeZone().getRawOffset());
            } else {
                if (TIMEZONETAB.checkID(j)) {
                    TIMEZONETAB.updateTable(paramConnection, j);
                }

                i = TIMEZONETAB.getLocalOffset(paramCalendar, j, paramOffsetDST);
            }
        }

        return i;
    }

    private static Calendar getDbTzCalendar(String paramString) {
        int i = paramString.charAt(0);
        String str;
        if ((i == 43) || (i == 45)) {
            str = "GMT" + paramString;
        } else {
            str = paramString;
        }

        TimeZone localTimeZone = TimeZone.getTimeZone(str);

        return new GregorianCalendar(localTimeZone);
    }

    private static Calendar getSessCalendar(Connection paramConnection) {
        String str = ((OracleConnection) paramConnection).getSessionTimeZone();
        Calendar localCalendar;
        if (str == null) {
            localCalendar = Calendar.getInstance();
        } else {
            TimeZone localTimeZone = TimeZone.getDefault();
            localTimeZone.setID(str);
            localCalendar = Calendar.getInstance(localTimeZone);
        }

        return localCalendar;
    }

    private static synchronized void getDbTimeZone(Connection paramConnection) throws SQLException {
        if (!cached) {
            OraclePreparedStatement localOraclePreparedStatement = null;
            ResultSet localResultSet = null;
            localOraclePreparedStatement = (OraclePreparedStatement) paramConnection
                    .prepareStatement("SELECT DBTIMEZONE FROM DUAL");

            localResultSet = localOraclePreparedStatement.executeQuery();
            localResultSet.next();
            String str = localResultSet.getString(1);
            localResultSet.close();
            localOraclePreparedStatement.close();
            dbtz = getDbTzCalendar(str);
            cached = true;
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.TIMESTAMPLTZ JD-Core Version: 0.6.0
 */