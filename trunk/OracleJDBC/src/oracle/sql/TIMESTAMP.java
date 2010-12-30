package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

public class TIMESTAMP extends Datum implements Serializable {
    private static final int CENTURY_DEFAULT = 119;
    private static final int DECADE_DEFAULT = 100;
    private static final int MONTH_DEFAULT = 1;
    private static final int DAY_DEFAULT = 1;
    private static final int DECADE_INIT = 170;
    private static final int JAVA_YEAR = 1970;
    private static final int JAVA_MONTH = 0;
    private static final int JAVA_DATE = 1;
    private static final int SIZE_TIMESTAMP = 11;
    private static final int SIZE_TIMESTAMP_NOFRAC = 7;
    private static final int SIZE_DATE = 7;
    private static final int MINYEAR = -4712;
    private static final int MAXYEAR = 9999;
    private static final int JANMONTH = 1;
    private static final int DECMONTH = 12;
    private static final int MINDAYS = 1;
    private static final int MAXDAYS = 31;
    private static final int MINHOURS = 1;
    private static final int MAXHOURS = 24;
    private static final int MINMINUTES = 1;
    private static final int MAXMINUTES = 60;
    private static final int MINSECONDS = 1;
    private static final int MAXSECONDS = 60;
    private static final int[] daysInMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
    static final long serialVersionUID = -7964732752952728545L;

    public TIMESTAMP() {
        super(initTimestamp());
    }

    public TIMESTAMP(byte[] paramArrayOfByte) {
        super(paramArrayOfByte);
    }

    public TIMESTAMP(Time paramTime) {
        super(toBytes(paramTime));
    }

    public TIMESTAMP(java.sql.Date paramDate) {
        super(toBytes(paramDate));
    }

    public TIMESTAMP(Timestamp paramTimestamp) {
        super(toBytes(paramTimestamp));
    }

    public TIMESTAMP(DATE paramDATE) {
        super(toBytes(paramDATE));
    }

    public TIMESTAMP(String paramString) {
        super(toBytes(paramString));
    }

    public static java.sql.Date toDate(byte[] paramArrayOfByte) throws SQLException {
        int i = paramArrayOfByte.length;
        int[] arrayOfInt;
        int j = 0;
        if (i == 11)
            arrayOfInt = new int[11];
        else {
            arrayOfInt = new int[7];
        }
        for (j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        j = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        Calendar localCalendar = Calendar.getInstance();

        localCalendar.set(1, j);
        localCalendar.set(2, arrayOfInt[2] - 1);
        localCalendar.set(5, arrayOfInt[3]);
        localCalendar.set(11, arrayOfInt[4] - 1);
        localCalendar.set(12, arrayOfInt[5] - 1);
        localCalendar.set(13, arrayOfInt[6] - 1);
        localCalendar.set(14, 0);

        long l = localCalendar.getTime().getTime();

        return new java.sql.Date(l);
    }

    public static Time toTime(byte[] paramArrayOfByte) throws SQLException {
        int i = paramArrayOfByte[4] & 0xFF;
        int j = paramArrayOfByte[5] & 0xFF;
        int k = paramArrayOfByte[6] & 0xFF;

        return new Time(i - 1, j - 1, k - 1);
    }

    public static Timestamp toTimestamp(byte[] paramArrayOfByte) throws SQLException {
        int i = paramArrayOfByte.length;
        int[] arrayOfInt;
        int j = 0;
        if (i == 11)
            arrayOfInt = new int[11];
        else {
            arrayOfInt = new int[7];
        }
        for (j = 0; j < paramArrayOfByte.length; j++) {
            paramArrayOfByte[j] &= 255;
        }

        j = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        Calendar localCalendar = Calendar.getInstance();

        localCalendar.set(1, j);
        localCalendar.set(2, arrayOfInt[2] - 1);
        localCalendar.set(5, arrayOfInt[3]);
        localCalendar.set(11, arrayOfInt[4] - 1);
        localCalendar.set(12, arrayOfInt[5] - 1);
        localCalendar.set(13, arrayOfInt[6] - 1);
        localCalendar.set(14, 0);

        long l = localCalendar.getTime().getTime();

        Timestamp localTimestamp = new Timestamp(l);

        if (i == 11) {
            int k = arrayOfInt[7] << 24;
            k |= arrayOfInt[8] << 16;
            k |= arrayOfInt[9] << 8;
            k |= arrayOfInt[10] & 0xFF;

            localTimestamp.setNanos(k);
        } else {
            localTimestamp.setNanos(0);
        }

        return localTimestamp;
    }

    public static DATE toDATE(byte[] paramArrayOfByte) throws SQLException {
        byte[] arrayOfByte = new byte[7];

        System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 7);

        return new DATE(arrayOfByte);
    }

    public Timestamp timestampValue() throws SQLException {
        return toTimestamp(getBytes());
    }

    public static String toString(byte[] paramArrayOfByte) {
        int i = 0;

        int j = paramArrayOfByte.length;
        int[] arrayOfInt;
        if (j == 11)
            arrayOfInt = new int[11];
        else {
            arrayOfInt = new int[7];
        }

        for (int k = 0; k < paramArrayOfByte.length; k++) {
            if (paramArrayOfByte[k] < 0)
                paramArrayOfByte[k] += 256;
            else {
                arrayOfInt[k] = paramArrayOfByte[k];
            }
        }
        arrayOfInt[4] -= 1;
        arrayOfInt[5] -= 1;
        arrayOfInt[6] -= 1;

        int m = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        if (j == 11) {
            i = (arrayOfInt[7] & 0xFF) << 24;
            i |= (arrayOfInt[8] & 0xFF) << 16;
            i |= (arrayOfInt[9] & 0xFF) << 8;
            i |= arrayOfInt[10] & 0xFF & 0xFF;
        }

        return m + "-" + arrayOfInt[2] + "-" + arrayOfInt[3] + " " + arrayOfInt[4] + "."
                + arrayOfInt[5] + "." + arrayOfInt[6] + "." + i;
    }

    public byte[] toBytes() {
        return getBytes();
    }

    public static byte[] toBytes(Time paramTime) {
        if (paramTime == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[7];
        Calendar localCalendar = Calendar.getInstance();
        localCalendar.setTime(paramTime);

        arrayOfByte[0] = 119;
        arrayOfByte[1] = 100;
        arrayOfByte[2] = 1;
        arrayOfByte[3] = 1;
        arrayOfByte[4] = (byte) (localCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (localCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (localCalendar.get(13) + 1);

        return arrayOfByte;
    }

    public static byte[] toBytes(java.sql.Date paramDate) {
        if (paramDate == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[7];
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramDate);

        arrayOfByte[0] = (byte) (localCalendar.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (localCalendar.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (localCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) localCalendar.get(5);
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 1;
        arrayOfByte[6] = 1;

        return arrayOfByte;
    }

    public static byte[] toBytes(Timestamp paramTimestamp) {
        if (paramTimestamp == null) {
            return null;
        }

        int i = paramTimestamp.getNanos();
        byte[] arrayOfByte;
        if (i == 0)
            arrayOfByte = new byte[7];
        else {
            arrayOfByte = new byte[11];
        }
        Calendar localCalendar = Calendar.getInstance();

        localCalendar.setTime(paramTimestamp);

        arrayOfByte[0] = (byte) (localCalendar.get(1) / 100 + 100);
        arrayOfByte[1] = (byte) (localCalendar.get(1) % 100 + 100);
        arrayOfByte[2] = (byte) (localCalendar.get(2) + 1);
        arrayOfByte[3] = (byte) localCalendar.get(5);
        arrayOfByte[4] = (byte) (localCalendar.get(11) + 1);
        arrayOfByte[5] = (byte) (localCalendar.get(12) + 1);
        arrayOfByte[6] = (byte) (localCalendar.get(13) + 1);

        if (i != 0) {
            arrayOfByte[7] = (byte) (i >> 24);
            arrayOfByte[8] = (byte) (i >> 16 & 0xFF);
            arrayOfByte[9] = (byte) (i >> 8 & 0xFF);
            arrayOfByte[10] = (byte) (i & 0xFF);
        }

        return arrayOfByte;
    }

    public static byte[] toBytes(DATE paramDATE) {
        if (paramDATE == null) {
            return null;
        }
        byte[] arrayOfByte = new byte[7];

        System.arraycopy(paramDATE.getBytes(), 0, arrayOfByte, 0, 7);

        return arrayOfByte;
    }

    public static byte[] toBytes(String paramString) {
        return toBytes(Timestamp.valueOf(paramString));
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

    public static TIMESTAMP TimeZoneConvert(Connection paramConnection, TIMESTAMP paramTIMESTAMP,
            TimeZone paramTimeZone1, TimeZone paramTimeZone2) throws SQLException {
        byte[] arrayOfByte = paramTIMESTAMP.getBytes();

        int i = arrayOfByte.length;
        int[] arrayOfInt;
        if (i == 11)
            arrayOfInt = new int[11];
        else {
            arrayOfInt = new int[7];
        }

        for (int j = 0; j < i; j++) {
            arrayOfByte[j] &= 255;
        }

        TimeZone localTimeZone = TimeZone.getDefault();

        int k = (arrayOfInt[0] - 100) * 100 + (arrayOfInt[1] - 100);

        Calendar localCalendar1 = Calendar.getInstance(paramTimeZone1);

        Calendar localCalendar2 = Calendar.getInstance(paramTimeZone2);

        TimeZone.setDefault(localCalendar1.getTimeZone());

        localCalendar1.set(1, k);
        localCalendar1.set(2, arrayOfInt[2] - 1);
        localCalendar1.set(5, arrayOfInt[3]);
        localCalendar1.set(11, arrayOfInt[4] - 1);
        localCalendar1.set(12, arrayOfInt[5] - 1);
        localCalendar1.set(13, arrayOfInt[6] - 1);

        localCalendar1.set(14, 0);

        TIMESTAMPLTZ.TimeZoneAdjust(paramConnection, localCalendar1, localCalendar2);

        long l = localCalendar2.getTime().getTime();

        TimeZone.setDefault(localTimeZone);

        Timestamp localTimestamp = new Timestamp(l);
        int m = 0;

        if (i == 11) {
            m = arrayOfInt[7] << 24;
            m |= arrayOfInt[8] << 16;
            m |= arrayOfInt[9] << 8;
            m |= arrayOfInt[10] & 0xFF;
            localTimestamp.setNanos(m);
        } else {
            localTimestamp.setNanos(0);
        }

        return new TIMESTAMP(localTimestamp);
    }

    public String stringValue() {
        return toString(getBytes());
    }

    public java.sql.Date dateValue() throws SQLException {
        return toDate(getBytes());
    }

    public Time timeValue() throws SQLException {
        return toTime(getBytes());
    }

    private static byte[] initTimestamp() {
        byte[] arrayOfByte = new byte[11];

        arrayOfByte[0] = 119;
        arrayOfByte[1] = -86;
        arrayOfByte[2] = 1;
        arrayOfByte[3] = 1;
        arrayOfByte[4] = 1;
        arrayOfByte[5] = 1;
        arrayOfByte[6] = 1;

        return arrayOfByte;
    }

    private boolean isLeapYear(int paramInt) {
        return (paramInt % 4 == 0)
                && (paramInt <= 1582 ? paramInt == -4712 : (paramInt % 100 != 0)
                        || (paramInt % 400 == 0));
    }

    private boolean isValid() {
        byte[] arrayOfByte = getBytes();
        if ((arrayOfByte.length != 11) && (arrayOfByte.length != 7)) {
            return false;
        }

        int i = ((arrayOfByte[0] & 0xFF) - 100) * 100 + ((arrayOfByte[1] & 0xFF) - 100);
        if ((i < -4712) || (i > 9999)) {
            return false;
        }

        if (i == 0) {
            return false;
        }

        int j = arrayOfByte[2] & 0xFF;
        if ((j < 1) || (j > 12)) {
            return false;
        }

        int k = arrayOfByte[3] & 0xFF;
        if ((k < 1) || (k > 31))
            return false;
        if ((k > daysInMonth[(j - 1)]) && ((!isLeapYear(i)) || (j != 2) || (k != 29))) {
            return false;
        }

        if ((i == 1582) && (j == 10) && (k >= 5) && (k < 15)) {
            return false;
        }

        int m = arrayOfByte[4] & 0xFF;
        if ((m < 1) || (m > 24)) {
            return false;
        }

        int n = arrayOfByte[5] & 0xFF;
        if ((n < 1) || (n > 60)) {
            return false;
        }

        int i1 = arrayOfByte[6] & 0xFF;

        return (i1 >= 1) && (i1 <= 60);
    }

    private void readObject(ObjectInputStream paramObjectInputStream) throws IOException,
            ClassNotFoundException {
        paramObjectInputStream.defaultReadObject();
        if (!isValid())
            throw new IOException("Invalid TIMESTAMP");
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.TIMESTAMP JD-Core Version: 0.6.0
 */