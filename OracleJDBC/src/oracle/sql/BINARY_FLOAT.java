package oracle.sql;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

public class BINARY_FLOAT extends Datum {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    public BINARY_FLOAT() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.BINARY_FLOAT(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BINARY_FLOAT(byte[] _bytes) {
        super(_bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.BINARY_FLOAT( _bytes=" + _bytes
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BINARY_FLOAT(float f) {
        super(floatToCanonicalFormatBytes(f));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.BINARY_FLOAT( f =" + f
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BINARY_FLOAT(Float f) {
        super(floatToCanonicalFormatBytes(f.floatValue()));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.BINARY_FLOAT( f =" + f
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.toJdbc() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return new Float(canonicalFormatBytesToFloat(getBytes()));
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.isConvertibleTo( jClass=" + jClass
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        return (class_name.compareTo("java.lang.String") == 0)
                || (class_name.compareTo("java.lang.Float") == 0);
    }

    public String stringValue() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BINARY_FLOAT.stringValue()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = Float.toString(canonicalFormatBytesToFloat(getBytes()));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINEST, "BINARY_FLOAT.stringValue: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BDOUBLE.makeJdbcArray( arraySize=" + arraySize
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new Float[arraySize];
    }

    static byte[] floatToCanonicalFormatBytes(float _f) {
        float f = _f;

        if (f == 0.0F)
            f = 0.0F;
        else if (f != f) {
            f = (0.0F / 0.0F);
        }
        int intBits = Float.floatToIntBits(f);
        byte[] b = new byte[4];

        int b3 = intBits;

        intBits >>= 8;

        int b2 = intBits;

        intBits >>= 8;

        int b1 = intBits;

        intBits >>= 8;

        int b0 = intBits;

        if ((b0 & 0x80) == 0) {
            b0 |= 128;
        } else {
            b0 ^= -1;
            b1 ^= -1;
            b2 ^= -1;
            b3 ^= -1;
        }

        b[3] = (byte) b3;
        b[2] = (byte) b2;
        b[1] = (byte) b1;
        b[0] = (byte) b0;

        return b;
    }

    static float canonicalFormatBytesToFloat(byte[] b) {
        int b0 = b[0];
        int b1 = b[1];
        int b2 = b[2];
        int b3 = b[3];

        if ((b0 & 0x80) != 0) {
            b0 &= 127;
            b1 &= 255;
            b2 &= 255;
            b3 &= 255;
        } else {
            b0 = (b0 ^ 0xFFFFFFFF) & 0xFF;
            b1 = (b1 ^ 0xFFFFFFFF) & 0xFF;
            b2 = (b2 ^ 0xFFFFFFFF) & 0xFF;
            b3 = (b3 ^ 0xFFFFFFFF) & 0xFF;
        }

        int intBits = b0 << 24 | b1 << 16 | b2 << 8 | b3;

        return Float.intBitsToFloat(intBits);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.BINARY_FLOAT"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.BINARY_FLOAT JD-Core Version: 0.6.0
 */