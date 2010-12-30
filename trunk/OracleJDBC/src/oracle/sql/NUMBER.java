// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: NUMBER.java

package oracle.sql;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import oracle.core.lmx.CoreException;

// Referenced classes of package oracle.sql:
// Datum, LnxLibServer, LnxLibThin, LnxLib

public class NUMBER extends Datum {

    static byte MAX_LONG[] = toBytes(0x7fffffffffffffffL);
    static byte MIN_LONG[] = toBytes(0x8000000000000000L);
    private static final int CHARACTER_ZERO = 48;
    private static final BigDecimal BIGDEC_NEGZERO = new BigDecimal("-0");
    private static final BigDecimal BIGDEC_ZERO = BigDecimal.valueOf(0L);
    private static final BigDecimal BIGDEC_ONE = BigDecimal.valueOf(1L);
    private static final BigInteger BIGINT_ZERO = BigInteger.valueOf(0L);
    private static final BigInteger BIGINT_HUND = BigInteger.valueOf(100L);
    private static final byte DIGEND = 21;
    private static final byte ODIGEND = 9;
    private static final int HUNDIGMAX = 66;
    private static final int BIGINTARRAYMAX = 54;
    private static final double BIGRATIO = 0.1505149978319906D;
    private static final int BIGLENMAX = 22;
    static final byte LNXM_NUM = 22;
    static final int LNXSGNBT = 128;
    static final byte LNXDIGS = 20;
    static final byte LNXEXPBS = 64;
    static final double ORANUM_FBASE = 100D;
    static final int LNXBASE = 100;
    static final byte IEEE_DBL_DIG = 15;
    private static final byte IEEE_FLT_DIG = 6;
    static final int LNXEXPMX = 127;
    static final int LNXEXPMN = 0;
    static final int LNXMXOUT = 40;
    static final int LNXMXFMT = 64;
    private static final byte BYTE_MAX_VALUE = 127;
    private static final byte BYTE_MIN_VALUE = -128;
    private static final short SHORT_MAX_VALUE = 32767;
    private static final short SHORT_MIN_VALUE = -32768;
    private static final byte PI[] = { -63, 4, 15, 16, 93, 66, 36, 90, 80, 33, 39, 47, 27, 44, 39,
            33, 80, 51, 29, 85, 21 };
    private static final byte E[] = { -63, 3, 72, 83, 82, 83, 85, 60, 5, 53, 36, 37, 3, 88, 48, 14,
            53, 67, 25, 98, 77 };
    private static final byte LN10[] = { -63, 3, 31, 26, 86, 10, 30, 95, 5, 57, 85, 2, 80, 92, 46,
            47, 85, 37, 43, 8, 61 };
    private static LnxLib _slnxlib;
    private static LnxLib _thinlib = null;
    private static int DBL_MAX = 40;
    private static int INT_MAX = 15;
    private static float FLOAT_MAX_INT = 2.147484E+009F;
    private static float FLOAT_MIN_INT = -2.147484E+009F;
    private static double DOUBLE_MAX_INT = 2147483647D;
    private static double DOUBLE_MIN_INT = -2147483648D;
    private static double DOUBLE_MAX_INT_2 = 2147483649D;
    private static double DOUBLE_MIN_INT_2 = -2147483649D;
    private static Object drvType = null;
    private static String LANGID = "AMERICAN";

    public NUMBER() {
        super(_makeZero());
    }

    public NUMBER(byte abyte0[]) {
        super(abyte0);
    }

    public NUMBER(byte byte0) {
        super(toBytes(byte0));
    }

    public NUMBER(int i) {
        super(toBytes(i));
    }

    public NUMBER(long l) {
        super(toBytes(l));
    }

    public NUMBER(short word0) {
        super(toBytes(word0));
    }

    public NUMBER(float f) {
        super(toBytes(f));
    }

    public NUMBER(double d) throws SQLException {
        super(toBytes(d));
    }

    public NUMBER(BigDecimal bigdecimal) throws SQLException {
        super(toBytes(bigdecimal));
    }

    public NUMBER(BigInteger biginteger) throws SQLException {
        super(toBytes(biginteger));
    }

    public NUMBER(String s, int i) throws SQLException {
        super(toBytes(s, i));
    }

    public NUMBER(boolean flag) {
        super(toBytes(flag));
    }

    public NUMBER(Object obj) throws SQLException {
        if (obj instanceof Integer) {
            setShareBytes(toBytes(((Integer) obj).intValue()));
        } else if (obj instanceof Long) {
            setShareBytes(toBytes(((Long) obj).longValue()));
        } else if (obj instanceof Float) {
            setShareBytes(toBytes(((Float) obj).floatValue()));
        } else if (obj instanceof Double) {
            setShareBytes(toBytes(((Double) obj).doubleValue()));
        } else if (obj instanceof BigInteger) {
            setShareBytes(toBytes((BigInteger) obj));
        } else if (obj instanceof BigDecimal) {
            setShareBytes(toBytes((BigDecimal) obj));
        } else if (obj instanceof Boolean) {
            setShareBytes(toBytes(((Boolean) obj).booleanValue()));
        } else if (obj instanceof String) {
            setShareBytes(stringToBytes((String) obj));
        } else {
            throw new SQLException("Initialization failed");
        }
    }

    public static double toDouble(byte abyte0[]) {
        if (_isZero(abyte0)) {
            return 0.0D;
        }
        if (_isPosInf(abyte0)) {
            return (1.0D / 0.0D);
        }
        if (_isNegInf(abyte0)) {
            return (-1.0D / 0.0D);
        }
        String s = null;
        try {
            if (drvType == null) {
                s = _slnxlib.lnxnuc(abyte0, DBL_MAX, null);
            } else {
                s = _slnxlib.lnxnuc(abyte0, DBL_MAX, LANGID);
            }
        } catch (Exception exception) {
        }
        double d = Double.valueOf(s).doubleValue();
        return d;
    }

    public static float toFloat(byte abyte0[]) {
        return (float) toDouble(abyte0);
    }

    public static long toLong(byte abyte0[]) throws SQLException {
        if (_isZero(abyte0)) {
            return 0L;
        }
        if (_isInf(abyte0) || compareBytes(abyte0, MAX_LONG) > 0
                || compareBytes(abyte0, MIN_LONG) < 0) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        } else {
            return _getLnxLib().lnxsni(abyte0);
        }
    }

    public static int toInt(byte abyte0[]) throws SQLException {
        if (_isInf(abyte0)) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        String s;
        if (drvType == null) {
            s = _slnxlib.lnxnuc(abyte0, INT_MAX, null);
        } else {
            s = _slnxlib.lnxnuc(abyte0, INT_MAX, LANGID);
        }
        double d = Double.valueOf(s).doubleValue();
        if ((float) d > FLOAT_MAX_INT || (float) d < FLOAT_MIN_INT) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        if (d > DOUBLE_MAX_INT && d <= DOUBLE_MAX_INT_2) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        if (d < DOUBLE_MIN_INT && d >= DOUBLE_MIN_INT_2) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        } else {
            int i = (int) d;
            return i;
        }
    }

    public static short toShort(byte[] paramArrayOfByte) throws SQLException {
        long l = 0L;
        try {
            l = toLong(paramArrayOfByte);

            if ((l > 32767L) || (l < -32768L)) {
                throw new SQLException(CoreException.getMessage((byte) 3));
            }
        } finally {
        }

        return (short) (int) l;
    }

    public static byte toByte(byte[] paramArrayOfByte) throws SQLException {
        long l = 0L;
        try {
            l = toLong(paramArrayOfByte);

            if ((l > 127L) || (l < -128L)) {
                throw new SQLException(CoreException.getMessage((byte) 3));
            }
        } finally {
        }

        return (byte) (int) l;
    }

    public static BigInteger toBigInteger(byte abyte0[]) throws SQLException {
        long al[] = new long[10];
        byte byte0 = 9;
        byte byte2 = 1;
        int k = 0;
        if (_isZero(abyte0)) {
            return BIGINT_ZERO;
        }
        if (_isInf(abyte0)) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        boolean flag = _isPositive(abyte0);
        byte abyte1[] = _fromLnxFmt(abyte0);
        if (abyte1[0] < 0) {
            return BIGINT_ZERO;
        }
        int i1 = Math.min(abyte1[0] + 1, abyte1.length - 1);
        int i = i1;
        if ((i1 & 1) == 1) {
            al[byte0] = abyte1[byte2];
            byte2++;
            i--;
        } else {
            al[byte0] = abyte1[byte2] * 100 + abyte1[byte2 + 1];
            byte2 += 2;
            i -= 2;
        }
        byte byte3 = byte0;
        for (; i != 0; i -= 2) {
            long l = abyte1[byte2] * 100 + abyte1[byte2 + 1];
            for (byte byte1 = 9; byte1 >= byte3; byte1--) {
                l += al[byte1] * 10000L;
                al[byte1] = l & 65535L;
                l >>= 16;
            }

            if (l == 0L)
                ;
            byte3--;
            al[byte3] = l;
            byte2 += 2;
        }

        int j;
        if (al[byte3] >> 8 != 0L) {
            j = 2 * (9 - byte3) + 2;
        } else {
            j = 2 * (9 - byte3) + 1;
        }
        byte abyte2[] = new byte[j];
        if ((j & 1) == 1) {
            abyte2[k] = (byte) (int) al[byte3];
            k++;
        } else {
            abyte2[k] = (byte) (int) (al[byte3] >> 8);
            k++;
            abyte2[k] = (byte) (int) (al[byte3] & 255L);
            k++;
        }
        for (byte3++; byte3 <= 9; byte3++) {
            abyte2[k] = (byte) (int) (al[byte3] >> 8);
            abyte2[k + 1] = (byte) (int) (al[byte3] & 255L);
            k += 2;
        }

        BigInteger biginteger = new BigInteger(flag ? 1 : -1, abyte2);
        int j1 = abyte1[0] - (i1 - 1);
        return biginteger.multiply(BIGINT_HUND.pow(j1));
    }

    public static BigDecimal toBigDecimal(byte abyte0[]) throws SQLException {
        long al[] = new long[10];
        byte byte0 = 9;
        byte byte2 = 1;
        int k = 0;
        if (_isZero(abyte0)) {
            return BIGDEC_ZERO;
        }
        if (_isInf(abyte0)) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        boolean flag = _isPositive(abyte0);
        byte abyte1[] = _fromLnxFmt(abyte0);
        int i1;
        int i = i1 = abyte1.length - 1;
        if ((i1 & 1) == 1) {
            al[byte0] = abyte1[byte2];
            byte2++;
            i--;
        } else {
            al[byte0] = abyte1[byte2] * 100 + abyte1[byte2 + 1];
            byte2 += 2;
            i -= 2;
        }
        byte byte3 = byte0;
        for (; i != 0; i -= 2) {
            long l = abyte1[byte2] * 100 + abyte1[byte2 + 1];
            for (byte byte1 = 9; byte1 >= byte3; byte1--) {
                l += al[byte1] * 10000L;
                al[byte1] = l & 65535L;
                l >>= 16;
            }

            if (l == 0L)
                ;
            byte3--;
            al[byte3] = l;
            byte2 += 2;
        }

        int j;
        if (al[byte3] >> 8 != 0L) {
            j = 2 * (9 - byte3) + 2;
        } else {
            j = 2 * (9 - byte3) + 1;
        }
        byte abyte2[] = new byte[j];
        if ((j & 1) == 1) {
            abyte2[k] = (byte) (int) al[byte3];
            k++;
        } else {
            abyte2[k] = (byte) (int) (al[byte3] >> 8);
            k++;
            abyte2[k] = (byte) (int) (al[byte3] & 255L);
            k++;
        }
        for (byte3++; byte3 <= 9; byte3++) {
            abyte2[k] = (byte) (int) (al[byte3] >> 8);
            abyte2[k + 1] = (byte) (int) (al[byte3] & 255L);
            k += 2;
        }

        BigInteger biginteger = new BigInteger(flag ? 1 : -1, abyte2);
        BigDecimal bigdecimal = new BigDecimal(biginteger);
        int j1 = (abyte1[0] - i1) + 1;
        bigdecimal = bigdecimal.movePointRight(j1 * 2);
        if (j1 < 0 && abyte1[i1] % 10 == 0) {
            bigdecimal = bigdecimal.setScale(-(j1 * 2 + 1));
        }
        return bigdecimal;
    }

    public static String toString(byte abyte0[]) {
        int i = 0;
        if (_isZero(abyte0)) {
            return new String("0");
        }
        if (_isPosInf(abyte0)) {
            return (new Double((1.0D / 0.0D))).toString();
        }
        if (_isNegInf(abyte0)) {
            return (new Double((-1.0D / 0.0D))).toString();
        }
        byte abyte1[] = _fromLnxFmt(abyte0);
        int i1 = abyte1[0];
        int j1 = abyte1.length - 1;
        int k1 = i1 - (j1 - 1);
        int i2;
        if (k1 >= 0) {
            i2 = 2 * (i1 + 1) + 1;
        } else if (i1 >= 0) {
            i2 = 2 * (j1 + 1);
        } else {
            i2 = 2 * (j1 - i1) + 3;
        }
        char ac[] = new char[i2];
        if (!_isPositive(abyte0)) {
            ac[i++] = '-';
        }
        if (k1 >= 0) {
            i += _byteToChars(abyte1[1], ac, i);
            for (int j = 2; j <= j1;) {
                _byteTo2Chars(abyte1[j], ac, i);
                i += 2;
                j++;
                i1--;
            }

            if (i1 > 0) {
                for (; i1 > 0; i1--) {
                    ac[i++] = '0';
                    ac[i++] = '0';
                }

            }
        } else {
            int l1 = j1 + k1;
            if (l1 > 0) {
                i += _byteToChars(abyte1[1], ac, i);
                if (l1 == 1) {
                    ac[i++] = '.';
                }
                int k;
                for (k = 2; k < j1; k++) {
                    _byteTo2Chars(abyte1[k], ac, i);
                    i += 2;
                    if (l1 == k) {
                        ac[i++] = '.';
                    }
                }

                if (abyte1[k] % 10 == 0) {
                    i += _byteToChars((byte) (abyte1[k] / 10), ac, i);
                } else {
                    _byteTo2Chars(abyte1[k], ac, i);
                    i += 2;
                }
            } else {
                ac[i++] = '0';
                ac[i++] = '.';
                for (; l1 < 0; l1++) {
                    ac[i++] = '0';
                    ac[i++] = '0';
                }

                int l;
                for (l = 1; l < j1; l++) {
                    _byteTo2Chars(abyte1[l], ac, i);
                    i += 2;
                }

                if (abyte1[l] % 10 == 0) {
                    i += _byteToChars((byte) (abyte1[l] / 10), ac, i);
                } else {
                    _byteTo2Chars(abyte1[l], ac, i);
                    i += 2;
                }
            }
        }
        return new String(ac, 0, i);
    }

    public static boolean toBoolean(byte abyte0[]) {
        return !_isZero(abyte0);
    }

    public static byte[] toBytes(double d) throws SQLException {
        if (Double.isNaN(d)) {
            throw new IllegalArgumentException(CoreException.getMessage((byte) 11));
        }
        if (d == 0.0D || d == 0D) {
            return _makeZero();
        }
        if (d == (1.0D / 0.0D)) {
            return _makePosInf();
        }
        if (d == (-1.0D / 0.0D)) {
            return _makeNegInf();
        } else {
            return _getThinLib().lnxren(d);
        }
    }

    public static byte[] toBytes(float paramFloat) {
        if (Float.isNaN(paramFloat)) {
            throw new IllegalArgumentException(CoreException.getMessage((byte) 11));
        }

        if ((paramFloat == 0.0F) || (paramFloat == -0.0F)) {
            return _makeZero();
        }
        if (paramFloat == (1.0F / 1.0F)) {
            return _makePosInf();
        }
        if (paramFloat == (1.0F / -1.0F)) {
            return _makeNegInf();
        }

        String str = Float.toString(paramFloat);
        try {
            return _getLnxLib().lnxcpn(str, false, 0, false, 0, "AMERICAN_AMERICA");
        } catch (Exception localException) {
        }
        return null;
    }

    public static byte[] toBytes(long l) {
        return _getLnxLib().lnxmin(l);
    }

    public static byte[] toBytes(int i) {
        return toBytes(i);
    }

    public static byte[] toBytes(short word0) {
        return toBytes(word0);
    }

    public static byte[] toBytes(byte byte0) {
        return toBytes(byte0);
    }

    public static byte[] toBytes(BigInteger biginteger) throws SQLException {
        byte abyte0[] = new byte[66];
        long al[] = new long[54];
        long al1[] = new long[22];
        byte byte0 = 21;
        byte byte4 = 0;
        byte byte5 = 21;
        boolean flag = false;
        int k = 0;
        boolean flag1 = true;
        if (biginteger.signum() == 0) {
            return _makeZero();
        }
        byte abyte1[];
        int i1;
        if (biginteger.signum() == -1) {
            BigInteger biginteger1 = biginteger.abs();
            flag1 = false;
            abyte1 = biginteger1.toByteArray();
            i1 = (int) Math.floor((double) biginteger1.bitLength() * 0.1505149978319906D);
        } else {
            abyte1 = biginteger.toByteArray();
            i1 = (int) Math.floor((double) biginteger.bitLength() * 0.1505149978319906D);
        }
        if (biginteger.compareTo(BIGINT_HUND.pow(i1)) < 0) {
            i1--;
        }
        if (abyte1.length > 54) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        for (int j1 = 0; j1 < abyte1.length; j1++) {
            if (abyte1[j1] < 0) {
                al[j1] = abyte1[j1] + 256;
            } else {
                al[j1] = abyte1[j1];
            }
        }

        int i = abyte1.length;
        switch (i % 3) {
        case 2: // '\002'
            al1[byte0] = (al[byte4] << 8) + al[byte4 + 1];
            byte4 += 2;
            i -= 2;
            break;

        case 1: // '\001'
            al1[byte0] = al[byte4];
            byte4++;
            i--;
            break;

        default:
            long l = (al[byte4] << 16) + (al[byte4 + 1] << 8) + al[byte4 + 2];
            al1[byte0] = l % 0xf4240L;
            al1[byte0 - 1] = l / 0xf4240L;
            byte5 -= ((byte) (al1[byte0 - 1] == 0L ? 0 : 1));
            byte4 += 3;
            i -= 3;
            break;
        }
        for (; i != 0; i -= 3) {
            long l1 = (al[byte4] << 4) + (al[byte4 + 1] >> 4);
            for (byte byte1 = 21; byte1 >= byte5; byte1--) {
                l1 += al1[byte1] << 12;
                al1[byte1] = l1 % 0xf4240L;
                l1 /= 0xf4240L;
            }

            if (l1 != 0L) {
                byte5--;
                al1[byte5] = l1;
            }
            l1 = ((al[byte4 + 1] & 15L) << 8) + al[byte4 + 2];
            for (byte byte2 = 21; byte2 >= byte5; byte2--) {
                l1 += al1[byte2] << 12;
                al1[byte2] = l1 % 0xf4240L;
                l1 /= 0xf4240L;
            }

            if (l1 != 0L) {
                byte5--;
                al1[byte5] = l1;
            }
            byte4 += 3;
        }

        int j;
        if ((abyte0[k] = (byte) (int) (al1[byte5] / 10000L)) != 0) {
            j = 3 * (21 - byte5) + 3;
            abyte0[k + 1] = (byte) (int) ((al1[byte5] % 10000L) / 100L);
            abyte0[k + 2] = (byte) (int) (al1[byte5] % 100L);
            k += 3;
        } else if ((abyte0[k] = (byte) (int) ((al1[byte5] % 10000L) / 100L)) != 0) {
            j = 3 * (21 - byte5) + 2;
            abyte0[k + 1] = (byte) (int) (al1[byte5] % 100L);
            k += 2;
        } else {
            abyte0[k] = (byte) (int) al1[byte5];
            j = 3 * (21 - byte5) + 1;
            k++;
        }
        for (byte byte3 = (byte) (byte5 + 1); byte3 <= 21; byte3++) {
            abyte0[k] = (byte) (int) (al1[byte3] / 10000L);
            abyte0[k + 1] = (byte) (int) ((al1[byte3] % 10000L) / 100L);
            abyte0[k + 2] = (byte) (int) (al1[byte3] % 100L);
            k += 3;
        }

        for (int k1 = k - 1; k1 >= 0 && abyte0[k1] == 0; k1--) {
            j--;
        }

        if (j > 19) {
            int i2 = 20;
            j = 19;
            if (abyte0[i2] >= 50) {
                i2--;
                abyte0[i2]++;
                do {
                    if (abyte0[i2] != 100) {
                        break;
                    }
                    if (i2 == 0) {
                        i1++;
                        abyte0[i2] = 1;
                        break;
                    }
                    abyte0[i2] = 0;
                    i2--;
                    abyte0[i2]++;
                } while (true);
                for (int j2 = j - 1; j2 >= 0 && abyte0[j2] == 0; j2--) {
                    j--;
                }

            }
        }
        if (i1 > 62) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        } else {
            byte abyte2[] = new byte[j + 1];
            abyte2[0] = (byte) i1;
            System.arraycopy(abyte0, 0, abyte2, 1, j);
            return _toLnxFmt(abyte2, flag1);
        }
    }

    public static byte[] toBytes(BigDecimal bigdecimal) throws SQLException {
        byte abyte0[] = new byte[66];
        long al[] = new long[54];
        long al1[] = new long[22];
        byte byte0 = 21;
        byte byte4 = 0;
        byte byte5 = 21;
        int k = 0;
        int i1 = 0;
        BigDecimal bigdecimal1 = bigdecimal.abs();
        int k3 = 0;
        if (bigdecimal.signum() == 0) {
            return _makeZero();
        }
        boolean flag = bigdecimal.signum() != -1;
        int i3 = bigdecimal.scale();
        int j3 = bigdecimal1.compareTo(BIGDEC_ONE);
        int l3 = 0;
        if (j3 == -1) {
            BigDecimal bigdecimal2;
            do {
                l3++;
                bigdecimal2 = bigdecimal1.movePointRight(l3);
            } while (bigdecimal2.compareTo(BIGDEC_ONE) < 0);
            k3 = -l3;
        } else {
            BigDecimal bigdecimal3;
            do {
                l3++;
                bigdecimal3 = bigdecimal1.movePointLeft(l3);
            } while (bigdecimal3.compareTo(BIGDEC_ONE) >= 0);
            k3 = l3;
        }
        byte abyte1[] = bigdecimal1.movePointRight(i3).toBigInteger().toByteArray();
        if (abyte1.length > 54) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        for (int j1 = 0; j1 < abyte1.length; j1++) {
            if (abyte1[j1] < 0) {
                al[j1] = abyte1[j1] + 256;
            } else {
                al[j1] = abyte1[j1];
            }
        }

        int i = abyte1.length;
        switch (i % 3) {
        case 2: // '\002'
            al1[byte0] = (al[byte4] << 8) + al[byte4 + 1];
            byte4 += 2;
            i -= 2;
            break;

        case 1: // '\001'
            al1[byte0] = al[byte4];
            byte4++;
            i--;
            break;

        default:
            long l = (al[byte4] << 16) + (al[byte4 + 1] << 8) + al[byte4 + 2];
            al1[byte0] = l % 0xf4240L;
            al1[byte0 - 1] = l / 0xf4240L;
            byte5 -= ((byte) (al1[byte0 - 1] == 0L ? 0 : 1));
            byte4 += 3;
            i -= 3;
            break;
        }
        for (; i != 0; i -= 3) {
            long l1 = (al[byte4] << 4) + (al[byte4 + 1] >> 4);
            for (byte byte1 = 21; byte1 >= byte5; byte1--) {
                l1 += al1[byte1] << 12;
                al1[byte1] = l1 % 0xf4240L;
                l1 /= 0xf4240L;
            }

            if (l1 != 0L) {
                byte5--;
                al1[byte5] = l1;
            }
            l1 = ((al[byte4 + 1] & 15L) << 8) + al[byte4 + 2];
            for (byte byte2 = 21; byte2 >= byte5; byte2--) {
                l1 += al1[byte2] << 12;
                al1[byte2] = l1 % 0xf4240L;
                l1 /= 0xf4240L;
            }

            if (l1 != 0L) {
                byte5--;
                al1[byte5] = l1;
            }
            byte4 += 3;
        }

        int j;
        if ((abyte0[k] = (byte) (int) (al1[byte5] / 10000L)) != 0) {
            j = 3 * (21 - byte5) + 3;
            abyte0[k + 1] = (byte) (int) ((al1[byte5] % 10000L) / 100L);
            abyte0[k + 2] = (byte) (int) (al1[byte5] % 100L);
            k += 3;
        } else if ((abyte0[k] = (byte) (int) ((al1[byte5] % 10000L) / 100L)) != 0) {
            j = 3 * (21 - byte5) + 2;
            abyte0[k + 1] = (byte) (int) (al1[byte5] % 100L);
            k += 2;
        } else {
            abyte0[k] = (byte) (int) al1[byte5];
            j = 3 * (21 - byte5) + 1;
            k++;
        }
        for (byte byte3 = (byte) (byte5 + 1); byte3 <= 21; byte3++) {
            abyte0[k] = (byte) (int) (al1[byte3] / 10000L);
            abyte0[k + 1] = (byte) (int) ((al1[byte3] % 10000L) / 100L);
            abyte0[k + 2] = (byte) (int) (al1[byte3] % 100L);
            k += 3;
        }

        for (int k1 = k - 1; k1 >= 0 && abyte0[k1] == 0; k1--) {
            j--;
        }

        if (i3 > 0 && (i3 & 1) != 0) {
            int i4 = j;
            byte abyte3[] = new byte[i4 + 1];
            if (abyte0[0] <= 9) {
                int i2;
                for (i2 = 0; i2 < i4 - 1; i2++) {
                    abyte3[i2] = (byte) ((abyte0[i2] % 10) * 10 + abyte0[i2 + 1] / 10);
                }

                abyte3[i2] = (byte) ((abyte0[i2] % 10) * 10);
                if (abyte3[i4 - 1] == 0) {
                    j--;
                }
            } else {
                abyte3[i4] = (byte) ((abyte0[i4 - 1] % 10) * 10);
                int j2;
                for (j2 = i4 - 1; j2 > 0; j2--) {
                    abyte3[j2] = (byte) (abyte0[j2] / 10 + (abyte0[j2 - 1] % 10) * 10);
                }

                abyte3[j2] = (byte) (abyte0[j2] / 10);
                if (abyte3[i4] > 0) {
                    j++;
                }
            }
            System.arraycopy(abyte3, 0, abyte0, 0, j);
        }
        if (j > 20) {
            int k2 = 20;
            j = 20;
            if (abyte0[k2] >= 50) {
                k2--;
                abyte0[k2]++;
                do {
                    if (abyte0[k2] != 100) {
                        break;
                    }
                    if (k2 == 0) {
                        k3++;
                        abyte0[k2] = 1;
                        break;
                    }
                    abyte0[k2] = 0;
                    k2--;
                    abyte0[k2]++;
                } while (true);
                for (int l2 = j - 1; l2 >= 0 && abyte0[l2] == 0; l2--) {
                    j--;
                }

            }
        }
        if (k3 <= 0) {
            if (abyte0[0] < 10) {
                i1 = -(2 - k3) / 2 + 1;
            } else {
                i1 = -(2 - k3) / 2;
            }
        } else {
            i1 = (k3 - 1) / 2;
        }
        if (i1 > 62) {
            throw new SQLException(CoreException.getMessage((byte) 3));
        }
        if (i1 <= -65) {
            throw new SQLException(CoreException.getMessage((byte) 2));
        } else {
            byte abyte2[] = new byte[j + 1];
            abyte2[0] = (byte) i1;
            System.arraycopy(abyte0, 0, abyte2, 1, j);
            return _toLnxFmt(abyte2, flag);
        }
    }

    public static byte[] toBytes(String s, int i) throws SQLException {
        int j = 0;
        int k = 0;
        byte abyte0[] = new byte[22];
        int l1 = 0;
        boolean flag = true;
        boolean flag1 = false;
        boolean flag2 = false;
        int i2 = 0;
        int k2 = 0;
        int l2 = 0;
        byte byte0 = 40;
        int i3 = 0;
        boolean flag3 = false;
        int l3 = 0;
        int j4 = 0;
        s = s.trim();
        int i4 = s.length();
        if (s.charAt(0) == '-') {
            i4--;
            flag = false;
            l3 = 1;
        }
        j = i4;
        char ac[] = new char[i4];
        s.getChars(l3, i4 + l3, ac, 0);
        int l = 0;
        do {
            if (l >= i4) {
                break;
            }
            if (ac[l] == '.') {
                flag2 = true;
                break;
            }
            l++;
        } while (true);
        do {
            if (k >= j || ac[k] != '0') {
                break;
            }
            k++;
            if (flag2) {
                j4++;
            }
        } while (true);
        if (k == j) {
            return _makeZero();
        }
        if (i4 >= 2 && ac[k] == '.') {
            for (; j > 0 && ac[j - 1] == '0'; j--) {
            }
            if (++k == j) {
                return _makeZero();
            }
            i2--;
            for (; k < j - 1 && ac[k] == '0' && ac[k + 1] == '0'; k += 2) {
                i2--;
                l2 += 2;
            }

            if (i2 <= -65) {
                throw new SQLException(CoreException.getMessage((byte) 2));
            }
            if (j - k > byte0) {
                int j1 = 2 + byte0;
                if (l2 > 0) {
                    j1 += l2;
                }
                if (j1 <= j) {
                    j = j1;
                }
                i3 = j;
                flag1 = true;
            }
            l1 = j - k >> 1;
            if ((j - k) % 2 != 0) {
                abyte0[l1] = (byte) (Integer.parseInt(new String(ac, j - 1, 1)) * 10);
                k2++;
                j--;
            }
            while (j > k) {
                l1--;
                abyte0[l1] = (byte) Integer.parseInt(new String(ac, j - 2, 2));
                j -= 2;
                k2++;
            }
        } else {
            for (; i > 0 && j > 0 && ac[j - 1] == '0'; i--) {
                j--;
            }

            if (i == 0 && j > 1) {
                if (ac[j - 1] == '.') {
                    j--;
                }
                if (k == j) {
                    return _makeZero();
                }
                while (j > 1 && ac[j - 2] == '0' && ac[j - 1] == '0') {
                    j -= 2;
                    i2++;
                }
            }
            if (i2 > 62) {
                throw new SQLException(CoreException.getMessage((byte) 3));
            }
            if (j - k - (flag2 ? 1 : 0) > byte0) {
                int k1 = byte0 + (flag2 ? 1 : 0);
                int k4 = j - k1;
                j = k1;
                i -= k4;
                if (i < 0) {
                    i = 0;
                }
                flag1 = true;
                i3 = j;
            }
            int j2 = i != 0 ? j - i - 1 : j - k;
            if (j4 > 0) {
                j2 -= j4;
            }
            int j3;
            if (j2 % 2 != 0) {
                j3 = Integer.parseInt(new String(ac, k, 1));
                k++;
                j2--;
                if (j - 1 == byte0) {
                    i--;
                    j--;
                    flag1 = true;
                    i3 = j;
                }
            } else {
                j3 = Integer.parseInt(new String(ac, k, 2));
                k += 2;
                j2 -= 2;
            }
            abyte0[l1] = (byte) j3;
            l1++;
            for (k2++; j2 > 0; k2++) {
                abyte0[l1] = (byte) Integer.parseInt(new String(ac, k, 2));
                l1++;
                k += 2;
                i2++;
                j2 -= 2;
            }

            if (k < j) {
                if (i % 2 != 0) {
                    l1 += i / 2;
                    abyte0[l1] = (byte) (Integer.parseInt(new String(ac, j - 1, 1)) * 10);
                    j--;
                    i--;
                } else {
                    l1 += i / 2 - 1;
                    abyte0[l1] = (byte) Integer.parseInt(new String(ac, j - 2, 2));
                    j -= 2;
                    i -= 2;
                }
                k2++;
                l1--;
            }
            while (i > 0) {
                abyte0[l1] = (byte) Integer.parseInt(new String(ac, j - 2, 2));
                l1--;
                j -= 2;
                i -= 2;
                k2++;
            }
        }
        if (flag1) {
            int l4 = k2;
            int k3 = Integer.parseInt(new String(ac, i3, 1));
            if (k3 >= 5) {
                l4--;
                abyte0[l4]++;
                do {
                    if (abyte0[l4] != 100) {
                        break;
                    }
                    if (l4 == 0) {
                        i2++;
                        abyte0[l4] = 1;
                        break;
                    }
                    abyte0[l4] = 0;
                    l4--;
                    abyte0[l4]++;
                } while (true);
                for (int i1 = k2 - 1; i1 >= 0 && abyte0[i1] == 0; i1--) {
                    k2--;
                }

            }
        }
        byte abyte1[] = new byte[k2 + 1];
        abyte1[0] = (byte) i2;
        System.arraycopy(abyte0, 0, abyte1, 1, k2);
        return _toLnxFmt(abyte1, flag);
    }

    public static byte[] toBytes(boolean flag) {
        if (flag) {
            return toBytes(1L);
        } else {
            return toBytes(0L);
        }
    }

    public byte[] toBytes() {
        return getBytes();
    }

    public double doubleValue() {
        return toDouble(shareBytes());
    }

    public float floatValue() {
        return toFloat(shareBytes());
    }

    public long longValue() throws SQLException {
        return toLong(shareBytes());
    }

    public int intValue() throws SQLException {
        return toInt(shareBytes());
    }

    public short shortValue() throws SQLException {
        return toShort(shareBytes());
    }

    public byte byteValue() throws SQLException {
        return toByte(shareBytes());
    }

    public BigInteger bigIntegerValue() throws SQLException {
        return toBigInteger(shareBytes());
    }

    public BigDecimal bigDecimalValue() throws SQLException {
        return toBigDecimal(shareBytes());
    }

    public String stringValue() {
        return toString(shareBytes());
    }

    public boolean booleanValue() {
        return toBoolean(shareBytes());
    }

    public Object toJdbc() throws SQLException {
        try {
            return bigDecimalValue();
        } catch (SQLException localSQLException) {
            return new SQLException(localSQLException.getMessage());
        }

    }

    public Object makeJdbcArray(int i) {
        BigDecimal abigdecimal[] = new BigDecimal[i];
        return abigdecimal;
    }

    public boolean isConvertibleTo(Class class1) {
        String s = class1.getName();
        return s.compareTo("java.lang.Integer") == 0 || s.compareTo("java.lang.Long") == 0
                || s.compareTo("java.lang.Float") == 0 || s.compareTo("java.lang.Double") == 0
                || s.compareTo("java.math.BigInteger") == 0
                || s.compareTo("java.math.BigDecimal") == 0 || s.compareTo("java.lang.String") == 0
                || s.compareTo("java.lang.Boolean") == 0;
    }

    public NUMBER abs() throws SQLException {
        return new NUMBER(_getLnxLib().lnxabs(shareBytes()));
    }

    public NUMBER acos() throws SQLException {
        return new NUMBER(_getLnxLib().lnxacos(shareBytes()));
    }

    public NUMBER add(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxadd(shareBytes(), number.shareBytes()));
    }

    public NUMBER asin() throws SQLException {
        return new NUMBER(_getLnxLib().lnxasin(shareBytes()));
    }

    public NUMBER atan() throws SQLException {
        return new NUMBER(_getLnxLib().lnxatan(shareBytes()));
    }

    public NUMBER atan2(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxatan2(shareBytes(), number.shareBytes()));
    }

    public NUMBER ceil() throws SQLException {
        return new NUMBER(_getLnxLib().lnxceil(shareBytes()));
    }

    public NUMBER cos() throws SQLException {
        return new NUMBER(_getLnxLib().lnxcos(shareBytes()));
    }

    public NUMBER cosh() throws SQLException {
        return new NUMBER(_getLnxLib().lnxcsh(shareBytes()));
    }

    public NUMBER decrement() throws SQLException {
        return new NUMBER(_getLnxLib().lnxdec(shareBytes()));
    }

    public NUMBER div(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxdiv(shareBytes(), number.shareBytes()));
    }

    public NUMBER exp() throws SQLException {
        return new NUMBER(_getLnxLib().lnxexp(shareBytes()));
    }

    public NUMBER floatingPointRound(int i) throws SQLException {
        return new NUMBER(_getLnxLib().lnxfpr(shareBytes(), i));
    }

    public NUMBER floor() throws SQLException {
        return new NUMBER(_getLnxLib().lnxflo(shareBytes()));
    }

    public NUMBER increment() throws SQLException {
        return new NUMBER(_getLnxLib().lnxinc(shareBytes()));
    }

    public NUMBER ln() throws SQLException {
        return new NUMBER(_getLnxLib().lnxln(shareBytes()));
    }

    public NUMBER log(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxlog(shareBytes(), number.shareBytes()));
    }

    public NUMBER mod(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxmod(shareBytes(), number.shareBytes()));
    }

    public NUMBER mul(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxmul(shareBytes(), number.shareBytes()));
    }

    public NUMBER negate() throws SQLException {
        return new NUMBER(_getLnxLib().lnxneg(shareBytes()));
    }

    public NUMBER pow(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxbex(shareBytes(), number.shareBytes()));
    }

    public NUMBER pow(int i) throws SQLException {
        return new NUMBER(_getLnxLib().lnxpow(shareBytes(), i));
    }

    public NUMBER round(int i) throws SQLException {
        return new NUMBER(_getLnxLib().lnxrou(shareBytes(), i));
    }

    public NUMBER scale(int i, int j, boolean aflag[]) throws SQLException {
        return new NUMBER(_getLnxLib().lnxsca(shareBytes(), i, j, aflag));
    }

    public NUMBER shift(int i) throws SQLException {
        return new NUMBER(_getLnxLib().lnxshift(shareBytes(), i));
    }

    public NUMBER sin() throws SQLException {
        return new NUMBER(_getLnxLib().lnxsin(shareBytes()));
    }

    public NUMBER sinh() throws SQLException {
        return new NUMBER(_getLnxLib().lnxsnh(shareBytes()));
    }

    public NUMBER sqroot() throws SQLException {
        return new NUMBER(_getLnxLib().lnxsqr(shareBytes()));
    }

    public NUMBER sub(NUMBER number) throws SQLException {
        return new NUMBER(_getLnxLib().lnxsub(shareBytes(), number.shareBytes()));
    }

    public NUMBER tan() throws SQLException {
        return new NUMBER(_getLnxLib().lnxtan(shareBytes()));
    }

    public NUMBER tanh() throws SQLException {
        return new NUMBER(_getLnxLib().lnxtnh(shareBytes()));
    }

    public NUMBER truncate(int i) throws SQLException {
        return new NUMBER(_getLnxLib().lnxtru(shareBytes(), i));
    }

    public static NUMBER formattedTextToNumber(String s, String s1, String s2) throws SQLException {
        return new NUMBER(_getLnxLib().lnxfcn(s, s1, s2));
    }

    public static NUMBER textToPrecisionNumber(String s, boolean flag, int i, boolean flag1, int j,
            String s1) throws SQLException {
        return new NUMBER(_getLnxLib().lnxcpn(s, flag, i, flag1, j, s1));
    }

    public String toFormattedText(String s, String s1) throws SQLException {
        return _getLnxLib().lnxnfn(shareBytes(), s, s1);
    }

    public String toText(int i, String s) throws SQLException {
        return _getLnxLib().lnxnuc(shareBytes(), i, s);
    }

    public int compareTo(NUMBER number) {
        return compareBytes(shareBytes(), number.shareBytes());
    }

    public boolean isInf() {
        return _isInf(shareBytes());
    }

    public boolean isNegInf() {
        return _isNegInf(shareBytes());
    }

    public boolean isPosInf() {
        return _isPosInf(shareBytes());
    }

    public boolean isInt() {
        return _isInt(shareBytes());
    }

    public static boolean isValid(byte abyte0[]) {
        byte byte0 = (byte) abyte0.length;
        if (_isPositive(abyte0)) {
            if (byte0 == 1) {
                return _isZero(abyte0);
            }
            if (abyte0[0] == -1 && abyte0[1] == 101) {
                return byte0 == 2;
            }
            if (byte0 > 21) {
                return false;
            }
            if (abyte0[1] < 2 || abyte0[byte0 - 1] < 2) {
                return false;
            }
            for (int i = 1; i < byte0; i++) {
                byte byte1 = abyte0[i];
                if (byte1 < 1 || byte1 > 100) {
                    return false;
                }
            }

            return true;
        }
        if (byte0 < 3) {
            return _isNegInf(abyte0);
        }
        if (byte0 > 21) {
            return false;
        }
        if (abyte0[byte0 - 1] != 102) {
            if (byte0 <= 20) {
                return false;
            }
        } else {
            byte0--;
        }
        if (abyte0[1] > 100 || abyte0[byte0 - 1] > 100) {
            return false;
        }
        for (int j = 1; j < byte0; j++) {
            byte byte2 = abyte0[j];
            if (byte2 < 2 || byte2 > 101) {
                return false;
            }
        }

        return true;
    }

    public boolean isZero() {
        return _isZero(shareBytes());
    }

    public static NUMBER e() {
        return new NUMBER(E);
    }

    public static NUMBER ln10() {
        return new NUMBER(LN10);
    }

    public static NUMBER negInf() {
        return new NUMBER(_makeNegInf());
    }

    public static NUMBER pi() {
        return new NUMBER(PI);
    }

    public static NUMBER posInf() {
        return new NUMBER(_makePosInf());
    }

    public static NUMBER zero() {
        return new NUMBER(_makeZero());
    }

    public int sign() {
        if (_isZero(shareBytes())) {
            return 0;
        } else {
            return _isPositive(shareBytes()) ? 1 : -1;
        }
    }

    static boolean _isInf(byte abyte0[]) {
        return abyte0.length == 2 && abyte0[0] == -1 && abyte0[1] == 101 || abyte0[0] == 0
                && abyte0.length == 1;
    }

    private static boolean _isInt(byte abyte0[]) {
        if (_isZero(abyte0)) {
            return true;
        }
        if (_isInf(abyte0)) {
            return false;
        }
        byte abyte1[] = _fromLnxFmt(abyte0);
        byte byte0 = abyte1[0];
        byte byte1 = (byte) (abyte1.length - 1);
        return byte1 <= byte0 + 1;
    }

    static boolean _isNegInf(byte abyte0[]) {
        return abyte0[0] == 0 && abyte0.length == 1;
    }

    static boolean _isPosInf(byte abyte0[]) {
        return abyte0.length == 2 && abyte0[0] == -1 && abyte0[1] == 101;
    }

    static boolean _isPositive(byte abyte0[]) {
        return (abyte0[0] & 0xffffff80) != 0;
    }

    static boolean _isZero(byte abyte0[]) {
        return abyte0[0] == -128 && abyte0.length == 1;
    }

    static byte[] _makePosInf() {
        byte abyte0[] = new byte[2];
        abyte0[0] = -1;
        abyte0[1] = 101;
        return abyte0;
    }

    static byte[] _makeNegInf() {
        byte abyte0[] = new byte[1];
        abyte0[0] = 0;
        return abyte0;
    }

    static byte[] _makeZero() {
        byte abyte0[] = new byte[1];
        abyte0[0] = -128;
        return abyte0;
    }

    static byte[] _fromLnxFmt(byte abyte0[]) {
        int k = abyte0.length;
        byte abyte1[];
        if (_isPositive(abyte0)) {
            abyte1 = new byte[k];
            abyte1[0] = (byte) ((abyte0[0] & 0xffffff7f) - 65);
            for (int i = 1; i < k; i++) {
                abyte1[i] = (byte) (abyte0[i] - 1);
            }

        } else {
            if (k - 1 == 20 && abyte0[k - 1] != 102) {
                abyte1 = new byte[k];
            } else {
                abyte1 = new byte[k - 1];
            }
            abyte1[0] = (byte) ((~abyte0[0] & 0xffffff7f) - 65);
            for (int j = 1; j < abyte1.length; j++) {
                abyte1[j] = (byte) (101 - abyte0[j]);
            }

        }
        return abyte1;
    }

    static byte[] _toLnxFmt(byte abyte0[], boolean flag) {
        int k = abyte0.length;
        byte abyte1[];
        if (flag) {
            abyte1 = new byte[k];
            abyte1[0] = (byte) (abyte0[0] + 128 + 64 + 1);
            for (int i = 1; i < k; i++) {
                abyte1[i] = (byte) (abyte0[i] + 1);
            }

        } else {
            if (k - 1 < 20) {
                abyte1 = new byte[k + 1];
            } else {
                abyte1 = new byte[k];
            }
            abyte1[0] = (byte) (~(abyte0[0] + 128 + 64 + 1));
            int j;
            for (j = 1; j < k; j++) {
                abyte1[j] = (byte) (101 - abyte0[j]);
            }

            if (j <= 20) {
                abyte1[j] = 102;
            }
        }
        return abyte1;
    }

    private static LnxLib _getLnxLib() {
        if (_slnxlib == null) {
            try {
                if (System.getProperty("oracle.jserver.version") != null) {
                    _slnxlib = new LnxLibServer();
                } else {
                    _slnxlib = new LnxLibThin();
                }
            } catch (SecurityException securityexception) {
                _slnxlib = new LnxLibThin();
            }
        }
        return _slnxlib;
    }

    private static LnxLib _getThinLib() {
        if (_thinlib == null) {
            _thinlib = new LnxLibThin();
        }
        return _thinlib;
    }

    private static int _byteToChars(byte byte0, char ac[], int i) {
        if (byte0 < 0) {
            return 0;
        }
        if (byte0 < 10) {
            ac[i] = (char) (48 + byte0);
            return 1;
        }
        if (byte0 < 100) {
            ac[i] = (char) (48 + byte0 / 10);
            ac[i + 1] = (char) (48 + byte0 % 10);
            return 2;
        } else {
            ac[i] = '1';
            ac[i + 1] = (char) ((48 + byte0 / 10) - 10);
            ac[i + 2] = (char) (48 + byte0 % 10);
            return 3;
        }
    }

    private static void _byteTo2Chars(byte byte0, char ac[], int i) {
        if (byte0 < 0) {
            ac[i] = '0';
            ac[i + 1] = '0';
        } else if (byte0 < 10) {
            ac[i] = '0';
            ac[i + 1] = (char) (48 + byte0);
        } else if (byte0 < 100) {
            ac[i] = (char) (48 + byte0 / 10);
            ac[i + 1] = (char) (48 + byte0 % 10);
        } else {
            ac[i] = '0';
            ac[i + 1] = '0';
        }
    }

    private static void _printBytes(byte abyte0[]) {
        int i = abyte0.length;
        System.out.print(i + ": ");
        for (int j = 0; j < i; j++) {
            System.out.print(abyte0[j] + " ");
        }

        System.out.println();
    }

    private byte[] stringToBytes(String s) throws SQLException {
        int i = 0;
        s = s.trim();
        if (s.indexOf('.') >= 0) {
            i = s.length() - 1 - s.indexOf('.');
        }
        return toBytes(s, i);
    }

    static {
        try {
            drvType = System.getProperty("oracle.jserver.version");
        } catch (SecurityException securityexception) {
            drvType = null;
        }
    }
}
