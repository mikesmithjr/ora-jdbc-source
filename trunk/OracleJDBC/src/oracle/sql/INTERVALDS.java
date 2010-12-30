package oracle.sql;

import java.util.StringTokenizer;

public class INTERVALDS extends Datum {
    private static int MAXLEADPREC = 9;
    private static int MAXHOUR = 23;
    private static int MAXMINUTE = 59;
    private static int MAXSECOND = 59;
    private static int INTERVALDSMAXLENGTH = 11;
    private static int INTERVALDSOFFSET = 60;
    private static int INTERVALDAYOFFSET = -2147483648;

    public INTERVALDS() {
        super(_initIntervalDS());
    }

    public INTERVALDS(byte[] paramArrayOfByte) {
        super(paramArrayOfByte);
    }

    public INTERVALDS(String paramString) {
        super(toBytes(paramString));
    }

    public byte[] toBytes() {
        return getBytes();
    }

    public static byte[] toBytes(String paramString) {
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;
        int i1 = 0;
        byte[] arrayOfByte = new byte[INTERVALDSMAXLENGTH];

        String str5 = null;

        String str6 = paramString.trim();

        int i2 = str6.charAt(0);
        int i;
        if ((i2 != 45) && (i2 != 43))
            i = 0;
        else {
            i = 1;
        }

        str6 = str6.substring(i);

        int i3 = str6.indexOf(' ');

        String str1 = str6.substring(0, i3);

        if (str1.length() > MAXLEADPREC) {
            throw new NumberFormatException();
        }
        int i4 = Integer.valueOf(str1).intValue();

        String str7 = str6.substring(i3 + 1);

        StringTokenizer localStringTokenizer = new StringTokenizer(str7, ":.");

        if (localStringTokenizer.hasMoreTokens()) {
            String str2;
            String str3;
            String str4;
            try {
                str2 = localStringTokenizer.nextToken();
                str3 = localStringTokenizer.nextToken();
                str4 = localStringTokenizer.nextToken();
                str5 = localStringTokenizer.nextToken();
            } catch (Exception localException) {
                throw new NumberFormatException();
            }

            j = Integer.valueOf(str1).intValue();
            k = Integer.valueOf(str2).intValue();
            m = Integer.valueOf(str3).intValue();
            n = Integer.valueOf(str4).intValue();

            if (k > MAXHOUR) {
                throw new NumberFormatException();
            }
            if (m > MAXMINUTE) {
                throw new NumberFormatException();
            }
            if (n > MAXSECOND) {
                throw new NumberFormatException();
            }
            if (str5.length() <= MAXLEADPREC)
                i1 = Integer.valueOf(str5).intValue();
            else
                throw new NumberFormatException();
        } else {
            throw new NumberFormatException();
        }

        if (i2 == 45) {
            j = -j;
            k = -k;
            m = -m;
            n = -n;
            i1 = -i1;
        }

        j += INTERVALDAYOFFSET;

        arrayOfByte[0] = utilpack.RIGHTSHIFTFIRSTNIBBLE(j);
        arrayOfByte[1] = utilpack.RIGHTSHIFTSECONDNIBBLE(j);
        arrayOfByte[2] = utilpack.RIGHTSHIFTTHIRDNIBBLE(j);
        arrayOfByte[3] = utilpack.RIGHTSHIFTFOURTHNIBBLE(j);

        arrayOfByte[4] = (byte) (k + INTERVALDSOFFSET);

        arrayOfByte[5] = (byte) (m + INTERVALDSOFFSET);

        arrayOfByte[6] = (byte) (n + INTERVALDSOFFSET);

        i1 += INTERVALDAYOFFSET;

        arrayOfByte[7] = utilpack.RIGHTSHIFTFIRSTNIBBLE(i1);
        arrayOfByte[8] = utilpack.RIGHTSHIFTSECONDNIBBLE(i1);
        arrayOfByte[9] = utilpack.RIGHTSHIFTTHIRDNIBBLE(i1);
        arrayOfByte[10] = utilpack.RIGHTSHIFTFOURTHNIBBLE(i1);

        return arrayOfByte;
    }

    public static String toString(byte[] paramArrayOfByte) {
        int i = 1;

        int i1 = 0;

        int j = utilpack.LEFTSHIFTFIRSTNIBBLE(paramArrayOfByte[0]);
        j |= utilpack.LEFTSHIFTSECONDNIBBLE(paramArrayOfByte[1]);
        j |= utilpack.LEFTSHIFTTHIRDNIBBLE(paramArrayOfByte[2]);
        j |= paramArrayOfByte[3] & 0xFF;

        j -= INTERVALDAYOFFSET;

        int k = paramArrayOfByte[4] - INTERVALDSOFFSET;

        int m = paramArrayOfByte[5] - INTERVALDSOFFSET;

        int n = paramArrayOfByte[6] - INTERVALDSOFFSET;

        i1 = utilpack.LEFTSHIFTFIRSTNIBBLE(paramArrayOfByte[7]);
        i1 |= utilpack.LEFTSHIFTSECONDNIBBLE(paramArrayOfByte[8]);
        i1 |= utilpack.LEFTSHIFTTHIRDNIBBLE(paramArrayOfByte[9]);
        i1 |= paramArrayOfByte[10] & 0xFF;

        i1 -= INTERVALDAYOFFSET;

        if (j < 0) {
            i = 0;
            j = -j;
            k = -k;
            m = -m;
            n = -n;
            i1 = -i1;
        }

        if (i != 0) {
            return new String(j + " " + k + ":" + m + ":" + n + "." + i1);
        }

        return new String("-" + j + " " + k + ":" + m + ":" + n + "." + i1);
    }

    public Object toJdbc() {
        return this;
    }

    public String stringValue() {
        return toString(getBytes());
    }

    public String toString() {
        return toString(getBytes());
    }

    public boolean isConvertibleTo(Class paramClass) {
        return paramClass.getName().compareTo("java.lang.String") == 0;
    }

    public Object makeJdbcArray(int paramInt) {
        INTERVALDS[] arrayOfINTERVALDS = new INTERVALDS[paramInt];

        return arrayOfINTERVALDS;
    }

    private static byte[] _initIntervalDS() {
        byte[] arrayOfByte = new byte[INTERVALDSMAXLENGTH];

        int i = 0;
        int j = 0;
        int k = 0;
        int m = 0;
        int n = 0;

        i += INTERVALDAYOFFSET;

        arrayOfByte[0] = utilpack.RIGHTSHIFTFIRSTNIBBLE(i);
        arrayOfByte[1] = utilpack.RIGHTSHIFTSECONDNIBBLE(i);
        arrayOfByte[2] = utilpack.RIGHTSHIFTTHIRDNIBBLE(i);
        arrayOfByte[3] = utilpack.RIGHTSHIFTFOURTHNIBBLE(i);

        arrayOfByte[4] = (byte) (j + INTERVALDSOFFSET);
        arrayOfByte[5] = (byte) (k + INTERVALDSOFFSET);
        arrayOfByte[6] = (byte) (m + INTERVALDSOFFSET);

        n += INTERVALDAYOFFSET;

        arrayOfByte[7] = utilpack.RIGHTSHIFTFIRSTNIBBLE(n);
        arrayOfByte[8] = utilpack.RIGHTSHIFTSECONDNIBBLE(n);
        arrayOfByte[9] = utilpack.RIGHTSHIFTTHIRDNIBBLE(n);
        arrayOfByte[10] = utilpack.RIGHTSHIFTFOURTHNIBBLE(n);

        return arrayOfByte;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.INTERVALDS JD-Core Version: 0.6.0
 */