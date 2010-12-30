package oracle.sql;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

public class BINARY_DOUBLE extends Datum {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    public BINARY_DOUBLE() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.BINARY_DOUBLE(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BINARY_DOUBLE(byte[] _bytes) {
        super(_bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.BINARY_DOUBLE( _bytes=" + _bytes
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BINARY_DOUBLE(double d) {
        super(doubleToCanonicalFormatBytes(d));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.BINARY_DOUBLE( d =" + d
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BINARY_DOUBLE(Double d) {
        super(doubleToCanonicalFormatBytes(d.doubleValue()));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.BINARY_DOUBLE( d =" + d
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.toJdbc() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return new Double(canonicalFormatBytesToDouble(getBytes()));
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.isConvertibleTo( jClass=" + jClass
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        return (class_name.compareTo("java.lang.String") == 0)
                || (class_name.compareTo("java.lang.Double") == 0);
    }

    public String stringValue() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.stringValue()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = Double.toString(canonicalFormatBytesToDouble(getBytes()));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINEST, "BINARY_DOUBLE.stringValue: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_DOUBLE.makeJdbcArray( arraySize="
                    + arraySize + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new Double[arraySize];
    }

    static byte[] doubleToCanonicalFormatBytes(double _d) {
        double d = _d;

        if (d == 0.0D)
            d = 0.0D;
        else if (d != d) {
            d = (0.0D / 0.0D);
        }
        long longBits = Double.doubleToLongBits(d);
        byte[] b = new byte[8];
        int lowInt = (int) longBits;
        int highInt = (int) (longBits >> 32);

        int b7 = lowInt;

        lowInt >>= 8;

        int b6 = lowInt;

        lowInt >>= 8;

        int b5 = lowInt;

        lowInt >>= 8;

        int b4 = lowInt;

        int b3 = highInt;

        highInt >>= 8;

        int b2 = highInt;

        highInt >>= 8;

        int b1 = highInt;

        highInt >>= 8;

        int b0 = highInt;

        if ((b0 & 0x80) == 0) {
            b0 |= 128;
        } else {
            b0 ^= -1;
            b1 ^= -1;
            b2 ^= -1;
            b3 ^= -1;
            b4 ^= -1;
            b5 ^= -1;
            b6 ^= -1;
            b7 ^= -1;
        }

        b[7] = (byte) b7;
        b[6] = (byte) b6;
        b[5] = (byte) b5;
        b[4] = (byte) b4;
        b[3] = (byte) b3;
        b[2] = (byte) b2;
        b[1] = (byte) b1;
        b[0] = (byte) b0;

        return b;
    }

    static double canonicalFormatBytesToDouble(byte[] b) {
        int b0 = b[0];
        int b1 = b[1];
        int b2 = b[2];
        int b3 = b[3];
        int b4 = b[4];
        int b5 = b[5];
        int b6 = b[6];
        int b7 = b[7];

        if ((b0 & 0x80) != 0) {
            b0 &= 127;
            b1 &= 255;
            b2 &= 255;
            b3 &= 255;
            b4 &= 255;
            b5 &= 255;
            b6 &= 255;
            b7 &= 255;
        } else {
            b0 = (b0 ^ 0xFFFFFFFF) & 0xFF;
            b1 = (b1 ^ 0xFFFFFFFF) & 0xFF;
            b2 = (b2 ^ 0xFFFFFFFF) & 0xFF;
            b3 = (b3 ^ 0xFFFFFFFF) & 0xFF;
            b4 = (b4 ^ 0xFFFFFFFF) & 0xFF;
            b5 = (b5 ^ 0xFFFFFFFF) & 0xFF;
            b6 = (b6 ^ 0xFFFFFFFF) & 0xFF;
            b7 = (b7 ^ 0xFFFFFFFF) & 0xFF;
        }

        int hiBits = b0 << 24 | b1 << 16 | b2 << 8 | b3;
        int loBits = b4 << 24 | b5 << 16 | b6 << 8 | b7;
        long longBits = hiBits << 32 | loBits & 0xFFFFFFFF;

        return Double.longBitsToDouble(longBits);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.BINARY_DOUBLE"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.BINARY_DOUBLE JD-Core Version: 0.6.0
 */