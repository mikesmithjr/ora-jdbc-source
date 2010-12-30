package oracle.sql;

import java.io.PrintStream;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DATE extends Datum {
    public static final int BDA = 1;
    public static final int BDAL = 2;
    public static final int BMO = 4;
    public static final int BMOL = 8;
    public static final int BYR = 16;
    public static final int BYRL = 32;
    public static final int BHR = 64;
    public static final int BHRL = 128;
    public static final int BMN = 256;
    public static final int BMNL = 512;
    public static final int BSC = 1024;
    public static final int BSCL = 2048;
    public static final int MSD = 4096;
    public static final int YR0 = 8192;
    public static final int BDT = 32768;
    public static final int HRZER0 = 65536;
    public static final int MIZERO = 131072;
    public static final int SEZERO = 262144;
    private static final byte LDXTCE = 0;
    private static final byte LDXTYE = 1;
    private static final byte LDXTMO = 2;
    private static final byte LDXTDA = 3;
    private static final byte LDXTHO = 4;
    private static final byte LDXTMI = 5;
    private static final byte LDXTSE = 6;
    private static LdxLib _sldxlib;

    public DATE() {
        super(_initDate());
    }

    public DATE(byte[] paramArrayOfByte) {
        super(paramArrayOfByte);
    }

    public DATE(java.sql.Date paramDate) {
        super(toBytes(paramDate));
    }

    public DATE(Time paramTime) {
        super(toBytes(paramTime));
    }

    public DATE(Timestamp paramTimestamp) {
        super(toBytes(paramTimestamp));
    }

    public DATE(java.sql.Date paramDate, Calendar paramCalendar) {
        super(toBytes(paramDate, paramCalendar));
    }

    public DATE(Time paramTime, Calendar paramCalendar) {
        super(toBytes(paramTime, paramCalendar));
    }

    public DATE(Timestamp paramTimestamp, Calendar paramCalendar) {
        super(toBytes(paramTimestamp, paramCalendar));
    }

    public DATE(String paramString) {
        super(toBytes(paramString));
    }

    public DATE(String paramString, boolean paramBoolean) throws ParseException {
        super(toBytes(paramString));
        if (!paramBoolean) {
            SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            localSimpleDateFormat.setLenient(false);

            java.util.Date localDate = localSimpleDateFormat.parse(paramString);
        }
    }

    public DATE(String paramString, Calendar paramCalendar) {
        super(toBytes(paramString, paramCalendar));
    }

    public DATE(Object paramObject) throws SQLException {
        if ((paramObject instanceof java.sql.Date))
            setShareBytes(toBytes((java.sql.Date) paramObject));
        else if ((paramObject instanceof Time))
            setShareBytes(toBytes((Time) paramObject));
        else if ((paramObject instanceof Timestamp))
            setShareBytes(toBytes((Timestamp) paramObject));
        else if ((paramObject instanceof String))
            setShareBytes(toBytes((String) paramObject));
        else
            throw new SQLException("Initialization failed");
    }

    public DATE(Object paramObject, Calendar paramCalendar) throws SQLException {
        if ((paramObject instanceof java.sql.Date))
            setShareBytes(toBytes((java.sql.Date) paramObject, paramCalendar));
        else if ((paramObject instanceof Time))
            setShareBytes(toBytes((Time) paramObject, paramCalendar));
        else if ((paramObject instanceof Timestamp))
            setShareBytes(toBytes((Timestamp) paramObject, paramCalendar));
        else if ((paramObject instanceof String))
            setShareBytes(toBytes((String) paramObject, paramCalendar));
        else
            throw new SQLException("Initialization failed");
    }

    public static java.sql.Date toDate(byte[] paramArrayOfByte) {
        int[] arrayOfInt = new int[7];
        int i = 0;

        for (i = 0; i < 7; i++) {
            paramArrayOfByte[i] &= 255;
        }

        i = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);
        int j = i - 1900;

        if (i <= 0) {
            j++;
        }
        return new java.sql.Date(j, arrayOfInt[2] - 1, arrayOfInt[3]);
    }

    public static Time toTime(byte[] paramArrayOfByte) {
        int[] arrayOfInt = new int[7];

        for (int i = 0; i < 7; i++) {
            paramArrayOfByte[i] &= 255;
        }
        return new Time(arrayOfInt[4] - 1, arrayOfInt[5] - 1, arrayOfInt[6] - 1);
    }

    public static Timestamp toTimestamp(byte[] paramArrayOfByte) {
        int[] arrayOfInt = new int[7];
        int i = 0;

        for (i = 0; i < 7; i++) {
            paramArrayOfByte[i] &= 255;
        }

        i = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);
        int j = i - 1900;

        if (i <= 0) {
            j++;
        }
        return new Timestamp(j, arrayOfInt[2] - 1, arrayOfInt[3], arrayOfInt[4] - 1,
                arrayOfInt[5] - 1, arrayOfInt[6] - 1, 0);
    }

    public static java.sql.Date toDate(byte[] paramArrayOfByte, Calendar paramCalendar) {
        int[] arrayOfInt = new int[7];
        int i = 0;

        for (i = 0; i < 7; i++) {
            paramArrayOfByte[i] &= 255;
        }

        i = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        if (paramCalendar == null) {
            paramCalendar = Calendar.getInstance();
        }
        paramCalendar.clear();

        paramCalendar.set(1, i);
        paramCalendar.set(2, arrayOfInt[2] - 1);
        paramCalendar.set(5, arrayOfInt[3]);
        paramCalendar.set(11, 0);
        paramCalendar.set(12, 0);
        paramCalendar.set(13, 0);
        paramCalendar.set(14, 0);

        java.sql.Date localDate = new java.sql.Date(paramCalendar.getTime().getTime());

        return localDate;
    }

    public static Time toTime(byte[] paramArrayOfByte, Calendar paramCalendar) {
        int[] arrayOfInt = new int[7];
        int i = 0;

        for (i = 0; i < 7; i++) {
            paramArrayOfByte[i] &= 255;
        }
        i = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        if (paramCalendar == null) {
            paramCalendar = Calendar.getInstance();
        }
        paramCalendar.clear();

        paramCalendar.set(1, 1970);
        paramCalendar.set(2, 0);
        paramCalendar.set(5, 1);
        paramCalendar.set(11, arrayOfInt[4] - 1);
        paramCalendar.set(12, arrayOfInt[5] - 1);
        paramCalendar.set(13, arrayOfInt[6] - 1);
        paramCalendar.set(14, 0);

        Time localTime = new Time(paramCalendar.getTime().getTime());

        return localTime;
    }

    public static Timestamp toTimestamp(byte[] paramArrayOfByte, Calendar paramCalendar) {
        int[] arrayOfInt = new int[7];
        int i = 0;

        for (i = 0; i < 7; i++) {
            paramArrayOfByte[i] &= 255;
        }

        i = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        if (paramCalendar == null) {
            paramCalendar = Calendar.getInstance();
        }
        paramCalendar.clear();

        paramCalendar.set(1, i);
        paramCalendar.set(2, arrayOfInt[2] - 1);
        paramCalendar.set(5, arrayOfInt[3]);
        paramCalendar.set(11, arrayOfInt[4] - 1);
        paramCalendar.set(12, arrayOfInt[5] - 1);
        paramCalendar.set(13, arrayOfInt[6] - 1);
        paramCalendar.set(14, 0);

        Timestamp localTimestamp = new Timestamp(paramCalendar.getTime().getTime());

        return localTimestamp;
    }

    public static String toString(byte[] paramArrayOfByte) {
        int[] arrayOfInt = new int[7];

        for (int i = 0; i < 7; i++) {
            if (paramArrayOfByte[i] < 0)
                paramArrayOfByte[i] += 256;
            else {
                arrayOfInt[i] = paramArrayOfByte[i];
            }
        }
        arrayOfInt[4] -= 1;
        arrayOfInt[5] -= 1;
        arrayOfInt[6] -= 1;

        int j = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        return arrayOfInt[2] + "/" + arrayOfInt[3] + "/" + j + " " + arrayOfInt[4] + ":"
                + arrayOfInt[5] + ":" + arrayOfInt[6];
    }

    public byte[] toBytes() {
        return getBytes();
    }

    public static byte[] toBytes(java.sql.Date paramDate) {
        if (paramDate == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[7];
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramDate);

        int i = localCalendar.get(1);

        if (localCalendar.get(0) == 0) {
            i = -(i - 1);
        }
        arrayOfByte[0] = (byte) (i / 100 + 100);
        arrayOfByte[1] = (byte) (i % 100 + 100);
        arrayOfByte[2] = (byte) (localCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) localCalendar.get(5);
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 1;
        arrayOfByte[6] = 1;

        return arrayOfByte;
    }

    public static byte[] toBytes(Time paramTime) {
        if (paramTime == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[7];
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramTime);

        int i = localCalendar.get(1);

        arrayOfByte[0] = 119;
        arrayOfByte[1] = 100;
        arrayOfByte[2] = 1;
        arrayOfByte[3] = 1;
        arrayOfByte[4] = (byte) (localCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (localCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (localCalendar.get(13) + 1);

        return arrayOfByte;
    }

    public static byte[] toBytes(Timestamp paramTimestamp) {
        if (paramTimestamp == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[7];
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramTimestamp);

        int i = localCalendar.get(1);

        if (localCalendar.get(0) == 0)
            i = -(i - 1);
        arrayOfByte[0] = (byte) (i / 100 + 100);
        arrayOfByte[1] = (byte) (i % 100 + 100);
        arrayOfByte[2] = (byte) (localCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) localCalendar.get(5);
        arrayOfByte[4] = (byte) (localCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (localCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (localCalendar.get(13) + 1);

        return arrayOfByte;
    }

    public static byte[] toBytes(java.sql.Date paramDate, Calendar paramCalendar) {
        if (paramDate == null) {
            return null;
        }
        if (paramCalendar == null) {
            paramCalendar = Calendar.getInstance();
        }
        paramCalendar.clear();
        paramCalendar.setTime(paramDate);

        byte[] arrayOfByte = new byte[7];

        int i = paramCalendar.get(1);

        if (paramCalendar.get(0) == 0) {
            i = -(i - 1);
        }
        arrayOfByte[0] = (byte) (i / 100 + 100);
        arrayOfByte[1] = (byte) (i % 100 + 100);
        arrayOfByte[2] = (byte) (paramCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) paramCalendar.get(5);
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 1;
        arrayOfByte[6] = 1;

        return arrayOfByte;
    }

    public static byte[] toBytes(Time paramTime, Calendar paramCalendar) {
        if (paramTime == null) {
            return null;
        }
        if (paramCalendar == null) {
            paramCalendar = Calendar.getInstance();
        }
        paramCalendar.clear();
        paramCalendar.setTime(paramTime);

        byte[] arrayOfByte = new byte[7];

        arrayOfByte[0] = 119;
        arrayOfByte[1] = 100;
        arrayOfByte[2] = 1;
        arrayOfByte[3] = 1;
        arrayOfByte[4] = (byte) (paramCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (paramCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (paramCalendar.get(13) + 1);

        return arrayOfByte;
    }

    public static byte[] toBytes(Timestamp paramTimestamp, Calendar paramCalendar) {
        if (paramTimestamp == null) {
            return null;
        }
        if (paramCalendar == null) {
            paramCalendar = Calendar.getInstance();
        }
        paramCalendar.clear();
        paramCalendar.setTime(paramTimestamp);

        byte[] arrayOfByte = new byte[7];

        int i = paramCalendar.get(1);

        if (paramCalendar.get(0) == 0) {
            i = -(i - 1);
        }
        arrayOfByte[0] = (byte) (i / 100 + 100);
        arrayOfByte[1] = (byte) (i % 100 + 100);
        arrayOfByte[2] = (byte) (paramCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) paramCalendar.get(5);
        arrayOfByte[4] = (byte) (paramCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (paramCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (paramCalendar.get(13) + 1);

        return arrayOfByte;
    }

    public static byte[] toBytes(String paramString) {
        return toBytes(Timestamp.valueOf(paramString));
    }

    public static byte[] toBytes(String paramString, Calendar paramCalendar) {
        return toBytes(Timestamp.valueOf(paramString), paramCalendar);
    }

    public java.sql.Date dateValue() {
        return toDate(getBytes());
    }

    public Time timeValue() {
        return toTime(getBytes());
    }

    public Timestamp timestampValue() {
        return toTimestamp(getBytes());
    }

    public java.sql.Date dateValue(Calendar paramCalendar) {
        return toDate(getBytes(), paramCalendar);
    }

    public Time timeValue(Calendar paramCalendar) {
        return toTime(getBytes(), paramCalendar);
    }

    public Timestamp timestampValue(Calendar paramCalendar) {
        return toTimestamp(getBytes(), paramCalendar);
    }

    public String stringValue() {
        return toString(getBytes());
    }

    public Object toJdbc() {
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

    public DATE addJulianDays(int paramInt1, int paramInt2) throws SQLException {
        return new DATE(_getLdxLib().ldxads(shareBytes(), paramInt1, paramInt2));
    }

    public DATE addMonths(int paramInt) throws SQLException {
        return new DATE(_getLdxLib().ldxadm(shareBytes(), paramInt));
    }

    public void diffInJulianDays(DATE paramDATE, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
            throws SQLException {
        _getLdxLib().ldxsub(shareBytes(), paramDATE.shareBytes(), paramArrayOfInt1,
                            paramArrayOfInt2);
    }

    public NUMBER diffInMonths(DATE paramDATE) throws SQLException {
        return new NUMBER(_getLdxLib().ldxsbm(shareBytes(), paramDATE.shareBytes()));
    }

    public static DATE getCurrentDate() throws SQLException {
        return new DATE(_getLdxLib().ldxgdt());
    }

    public static int checkValidity(byte[] paramArrayOfByte) throws SQLException {
        return _getLdxLib().ldxchk(paramArrayOfByte);
    }

    public static DATE fromJulianDays(int paramInt1, int paramInt2) throws SQLException {
        return new DATE(_getLdxLib().ldxdfd(paramInt1, paramInt2));
    }

    public static DATE fromText(String paramString1, String paramString2, String paramString3)
            throws SQLException {
        return new DATE(_getLdxLib().ldxstd(paramString1, paramString2, paramString3));
    }

    public DATE lastDayOfMonth() throws SQLException {
        return new DATE(_getLdxLib().ldxldd(shareBytes()));
    }

    public static void numberToJulianDays(NUMBER paramNUMBER, int[] paramArrayOfInt1,
            int[] paramArrayOfInt2) throws SQLException {
        _getLdxLib().ldxftd(paramNUMBER.toBytes(), paramArrayOfInt1, paramArrayOfInt2);
    }

    public DATE round(String paramString) throws SQLException {
        return new DATE(_getLdxLib().ldxrnd(shareBytes(), paramString));
    }

    public DATE setDayOfWeek(int paramInt) throws SQLException {
        return new DATE(_getLdxLib().ldxnxd(shareBytes(), paramInt));
    }

    public void toJulianDays(int[] paramArrayOfInt1, int[] paramArrayOfInt2) throws SQLException {
        _getLdxLib().ldxdtd(shareBytes(), paramArrayOfInt1, paramArrayOfInt2);
    }

    public NUMBER toNumber() throws SQLException {
        return new NUMBER(_getLdxLib().ldxdyf(shareBytes()));
    }

    public String toText(String paramString1, String paramString2) throws SQLException {
        return _getLdxLib().ldxdts(shareBytes(), paramString1, paramString2);
    }

    public String toText(byte[] paramArrayOfByte, String paramString) throws SQLException {
        return _getLdxLib().ldxdts(shareBytes(), paramArrayOfByte, paramString);
    }

    public static byte[] parseFormat(String paramString1, String paramString2) throws SQLException {
        return (byte[]) _getLdxLib().ldxsto(paramString1, paramString2);
    }

    public DATE truncate(String paramString) throws SQLException {
        return new DATE(_getLdxLib().ldxtrn(shareBytes(), paramString));
    }

    public int compareTo(DATE paramDATE) {
        return compareBytes(shareBytes(), paramDATE.shareBytes());
    }

    private static byte[] _initDate() {
        byte[] arrayOfByte = new byte[7];

        arrayOfByte[0] = 119;
        arrayOfByte[1] = -86;
        arrayOfByte[2] = 1;
        arrayOfByte[3] = 1;
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 1;
        arrayOfByte[6] = 1;

        return arrayOfByte;
    }

    private static LdxLib _getLdxLib() {
        if (_sldxlib == null) {
            try {
                if (System.getProperty("oracle.jserver.version") != null) {
                    _sldxlib = new LdxLibServer();
                } else {
                    _sldxlib = new LdxLibThin();
                }
            } catch (SecurityException localSecurityException) {
                _sldxlib = new LdxLibThin();
            }
        }

        return _sldxlib;
    }

    private static void _printBytes(byte[] paramArrayOfByte) {
        System.out.println(toString(paramArrayOfByte));
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.DATE JD-Core Version: 0.6.0
 */