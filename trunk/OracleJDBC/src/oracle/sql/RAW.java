package oracle.sql;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DBConversion;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.util.RepConversion;

public class RAW extends Datum {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    static int hexDigit2Nibble(char hex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.hexDigit2Nibble(" + hex + ")");

            OracleLog.recursiveTrace = false;
        }

        int result = Character.digit(hex, 16);

        if (result == -1) {
            throw new SQLException("Invalid hex digit: " + hex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.hexDigit2Nibble:return: " + result);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public static byte[] hexString2Bytes(String hexString) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.hexString2Bytes(" + hexString + ")");

            OracleLog.recursiveTrace = false;
        }

        int len = hexString.length();
        char[] hexChars = new char[len];

        hexString.getChars(0, len, hexChars, 0);

        int i = 0;
        int j = 0;

        if (len == 0)
            return new byte[0];
        byte[] bytes;
        if (len % 2 > 0) {
            bytes = new byte[(len + 1) / 2];
            bytes[(i++)] = (byte) hexDigit2Nibble(hexChars[(j++)]);
        } else {
            bytes = new byte[len / 2];
        }

        for (; i < bytes.length; i++) {
            bytes[i] = (byte) (hexDigit2Nibble(hexChars[(j++)]) << 4 | hexDigit2Nibble(hexChars[(j++)]));
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.hexString2Bytes:return: " + bytes);

            OracleLog.recursiveTrace = false;
        }

        return bytes;
    }

    public static RAW newRAW(Object obj) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.newRAW(" + obj + ")");

            OracleLog.recursiveTrace = false;
        }

        RAW result = new RAW(obj);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.newRAW:return: " + result);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public static RAW oldRAW(Object obj) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.oldRAW(" + obj + ")");

            OracleLog.recursiveTrace = false;
        }
        RAW result;
        if ((obj instanceof String)) {
            String s = (String) obj;
            byte[] bytes = null;
            try {
                bytes = s.getBytes("ISO8859_1");
            } catch (UnsupportedEncodingException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.SEVERE,
                                              "RAW.oldRAW: Exception caught and thrown."
                                                      + e.getMessage());

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(109, "ISO8859_1 character encoding not found");
            }

            result = new RAW(bytes);
        } else {
            result = new RAW(obj);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.oldRAW:return: " + result);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public RAW() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.RAW(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public RAW(byte[] raw_bytes) {
        super(raw_bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.RAW( raw_bytes=" + raw_bytes
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    /** @deprecated */
    public RAW(Object val) throws SQLException {
        this();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.RAW( val=" + val + ") -- after this() --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        if ((val instanceof byte[])) {
            setShareBytes((byte[]) val);
        } else if ((val instanceof String)) {
            setShareBytes(hexString2Bytes((String) val));
        } else {
            DatabaseError.throwSqlException(59, val);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.RAW( val=" + val + "): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.toJdbc() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getBytes();
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.isConvertibleTo( jClass=" + jClass
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        return (class_name.compareTo("java.lang.String") == 0)
                || (class_name.compareTo("java.io.InputStream") == 0)
                || (class_name.compareTo("java.io.Reader") == 0);
    }

    public String stringValue() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.stringValue()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = RepConversion.bArray2String(getBytes());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINEST, "RAW.stringValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Reader characterStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.characterStreamValue()", this);

            OracleLog.recursiveTrace = false;
        }

        int nbytes = (int) getLength();
        char[] chars = new char[nbytes * 2];
        byte[] bytes = shareBytes();

        DBConversion.RAWBytesToHexChars(bytes, nbytes, chars);

        Reader ret = new CharArrayReader(chars);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.characterStreamValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream asciiStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.asciiStreamValue()", this);

            OracleLog.recursiveTrace = false;
        }

        int nbytes = (int) getLength();
        char[] chars = new char[nbytes * 2];
        byte[] bytes = shareBytes();

        DBConversion.RAWBytesToHexChars(bytes, nbytes, chars);

        byte[] buf = new byte[nbytes * 2];

        DBConversion.javaCharsToAsciiBytes(chars, nbytes * 2, buf);

        InputStream ret = new ByteArrayInputStream(buf);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.asciiStreamValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream binaryStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.binaryStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getStream();
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "RAW.makeJdbcArray( arraySize=" + arraySize
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return new byte[arraySize][];
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.RAW"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name: oracle.sql.RAW
 * JD-Core Version: 0.6.0
 */