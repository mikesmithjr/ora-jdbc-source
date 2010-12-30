package oracle.sql;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class CHAR extends Datum {
    public static final CharacterSet DEFAULT_CHARSET = CharacterSet.make(-1);
    private CharacterSet charSet;
    private int oracleId;
    private static final byte[] empty = new byte[0];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    protected CHAR() {
    }

    public CHAR(byte[] bytes, CharacterSet charSet) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.CHAR( bytes=" + bytes + ", charSet="
                    + charSet + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        setValue(bytes, charSet);
    }

    public CHAR(byte[] bytes, int offset, int count, CharacterSet charSet) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.CHAR( bytes=" + bytes + ", offset="
                    + offset + ", count=" + count + ", charSet=" + charSet
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] data = new byte[count];

        System.arraycopy(bytes, offset, data, 0, count);
        setValue(data, charSet);
    }

    public CHAR(String str, CharacterSet charSet) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.CHAR( str=" + str + ", charSet=" + charSet
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (charSet == null) {
            charSet = DEFAULT_CHARSET;
        }

        setValue(charSet.convertWithReplacement(str), charSet);
    }

    public CHAR(Object obj, CharacterSet charSet) throws SQLException {
        this(obj.toString(), charSet);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.CHAR( obj=" + obj + ", charSet=" + charSet
                    + "): return -- after this() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public CharacterSet getCharacterSet() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.getCharacterSet() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        if (this.charSet == null) {
            if (this.oracleId == 0) {
                this.oracleId = -1;
            }

            if ((DEFAULT_CHARSET != null)
                    && ((this.oracleId == -1) || (this.oracleId == DEFAULT_CHARSET.getOracleId()))) {
                this.charSet = DEFAULT_CHARSET;
            } else
                this.charSet = CharacterSet.make(this.oracleId);
        }

        return this.charSet;
    }

    public int oracleId() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.oracleId() this = " + this + "-- returns "
                    + this.oracleId, this);

            OracleLog.recursiveTrace = false;
        }

        return this.oracleId;
    }

    public String getString() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.getString()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = getCharacterSet().toString(shareBytes(), 0, (int) getLength());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.getString: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getStringWithReplacement() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.getStringWithReplacement()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] bytes = shareBytes();
        String ret = getCharacterSet().toStringWithReplacement(bytes, 0, bytes.length);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.getStringWithReplacement: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String toString() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.toString()", this);

            OracleLog.recursiveTrace = false;
        }

        return getStringWithReplacement();
    }

    public boolean equals(Object other) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.equals( other=" + other
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return ((other instanceof CHAR))
                && (getCharacterSet().equals(((CHAR) other).getCharacterSet()))
                && (super.equals(other));
    }

    void setValue(byte[] bytes, CharacterSet charSet) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.setValue( bytes=" + bytes + ", charSet="
                    + charSet + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.charSet = (charSet == null ? DEFAULT_CHARSET : charSet);
        this.oracleId = this.charSet.getOracleId();

        setShareBytes(bytes == null ? empty : bytes);
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.toJdbc()  -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return stringValue();
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.isConvertibleTo( jClass=" + jClass
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        return (class_name.compareTo("java.lang.String") == 0)
                || (class_name.compareTo("java.lang.Long") == 0)
                || (class_name.compareTo("java.math.BigDecimal") == 0)
                || (class_name.compareTo("java.io.InputStream") == 0)
                || (class_name.compareTo("java.sql.Date") == 0)
                || (class_name.compareTo("java.sql.Time") == 0)
                || (class_name.compareTo("java.sql.Timestamp") == 0)
                || (class_name.compareTo("java.io.Reader") == 0);
    }

    public String stringValue() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.stringValue() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return toString();
    }

    public boolean booleanValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.booleanValue()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = false;

        String str = stringValue();

        if (str == null) {
            ret = false;
        } else if ((str.length() == 1) && (str.charAt(0) == '0')) {
            ret = false;
        } else {
            ret = true;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.booleanValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int intValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.intValue()", this);

            OracleLog.recursiveTrace = false;
        }

        long result = longValue();

        if ((result > 2147483647L) || (result < -2147483648L)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CHAR.intValue: Numeric overflow in the calculation. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(26);
        }

        int ret = (int) result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.intValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public long longValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.longValue()", this);

            OracleLog.recursiveTrace = false;
        }

        long ret_val = 0L;
        try {
            ret_val = Long.valueOf(stringValue()).longValue();
        } catch (NumberFormatException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "CHAR.longValue: Conversion Java error. An exception is thrown.", this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(59);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.longValue: return: " + ret_val, this);

            OracleLog.recursiveTrace = false;
        }

        return ret_val;
    }

    public float floatValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.floatValue()", this);

            OracleLog.recursiveTrace = false;
        }

        float ret_val = 0.0F;
        try {
            ret_val = Float.valueOf(stringValue()).floatValue();
        } catch (NumberFormatException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "CHAR.floatValue: Conversion Java error. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(59);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.floatValue: return: " + ret_val, this);

            OracleLog.recursiveTrace = false;
        }

        return ret_val;
    }

    public double doubleValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.doubleValue()", this);

            OracleLog.recursiveTrace = false;
        }

        double ret_val = 0.0D;
        try {
            ret_val = Double.valueOf(stringValue()).doubleValue();
        } catch (NumberFormatException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "CHAR.doubleValue: Conversion Java error. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(59);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.doubleValue: return: " + ret_val, this);

            OracleLog.recursiveTrace = false;
        }

        return ret_val;
    }

    public byte byteValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.byteValue()", this);

            OracleLog.recursiveTrace = false;
        }

        long result = longValue();

        if ((result > 127L) || (result < -128L)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CHAR.byteValue: Numeric overflow in the calculation. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(26);
        }

        byte ret = (byte) (int) result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.byteValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Date dateValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.dateValue()", this);

            OracleLog.recursiveTrace = false;
        }

        Date ret = Date.valueOf(stringValue());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.dateValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Time timeValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.timeValue()", this);

            OracleLog.recursiveTrace = false;
        }

        Time ret = Time.valueOf(stringValue());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.timeValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Timestamp timestampValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.timestampValue()", this);

            OracleLog.recursiveTrace = false;
        }

        Timestamp ret = Timestamp.valueOf(stringValue());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.timestampValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public BigDecimal bigDecimalValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.bigDecimalValue()", this);

            OracleLog.recursiveTrace = false;
        }

        BigDecimal big_dec_val = null;
        try {
            big_dec_val = new BigDecimal(stringValue());
        } catch (NumberFormatException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "CHAR.bigDecimalValue: Type conflict. An exception is thrown.", this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(12, "bigDecimalValue");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.bigDecimalValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return big_dec_val;
    }

    public Reader characterStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.characterStreamValue()", this);

            OracleLog.recursiveTrace = false;
        }

        Reader ret = new StringReader(getString());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.characterStreamValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream asciiStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.asciiStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getStream();
    }

    public InputStream binaryStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.binaryStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getStream();
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CHAR.makeJdbcArray( arraySize=" + arraySize
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return new String[arraySize];
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.CHAR"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CHAR JD-Core Version: 0.6.0
 */