package oracle.sql;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class OracleJdbc2SQLInput implements SQLInput {
    private int index;
    private Datum[] attributes;
    private Map map;
    private OracleConnection conn;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public OracleJdbc2SQLInput(Datum[] attributes, Map map, OracleConnection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.OracleJdbc2SQLInput( attributes="
                                              + attributes + ", map=" + map + ", conn=" + conn
                                              + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes = attributes;
        this.map = map;
        this.conn = conn;
        this.index = 0;
    }

    public String readString() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readString()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].stringValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readString: return:" + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean readBoolean() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBoolean()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = false;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].booleanValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readBoolean: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public byte readByte() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readByte()", this);

            OracleLog.recursiveTrace = false;
        }

        byte ret = 0;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].byteValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readByte: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public short readShort() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readShort()", this);

            OracleLog.recursiveTrace = false;
        }

        long result = readLong();

        if ((result > 65537L) || (result < -65538L)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.FINER,
                             "OracleJdbc2SQLInput.readShort: numeric overflow, the result is either > 65537 or < -65538. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(26, "readShort");
        }

        short ret = (short) (int) result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readShort: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int readInt() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readInt()", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = 0;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].intValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readInt: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public long readLong() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readLong()", this);

            OracleLog.recursiveTrace = false;
        }

        long ret = 0L;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].longValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readLong: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public float readFloat() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readFloat()", this);

            OracleLog.recursiveTrace = false;
        }

        float ret = 0.0F;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].floatValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readFloat: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public double readDouble() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readDouble()", this);

            OracleLog.recursiveTrace = false;
        }

        double ret = 0.0D;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].doubleValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readDouble: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public BigDecimal readBigDecimal() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBigDecimal()", this);

            OracleLog.recursiveTrace = false;
        }

        BigDecimal ret = null;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].bigDecimalValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBigDecimal: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public byte[] readBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof RAW)) {
                    ret = ((RAW) this.attributes[this.index]).shareBytes();
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.SEVERE,
                                                  "OracleJdbc2SQLInput.readBytes: invalid column type, it should be RAW. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Date readDate() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readDate()", this);

            OracleLog.recursiveTrace = false;
        }

        Date ret = null;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].dateValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readDate: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Time readTime() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readTime()", this);

            OracleLog.recursiveTrace = false;
        }

        Time ret = null;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].timeValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readTime: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Timestamp readTimestamp() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readTimestamp()", this);

            OracleLog.recursiveTrace = false;
        }

        Timestamp ret = null;
        try {
            if (this.attributes[this.index] != null) {
                ret = this.attributes[this.index].timestampValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readTimestamp()", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Reader readCharacterStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OracleJdbc2SQLInput.readCharacterStream()", this);

            OracleLog.recursiveTrace = false;
        }

        Reader ret = null;
        try {
            Datum datum = this.attributes[this.index];

            if (datum != null) {
                ret = datum.characterStreamValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readCharacterStream: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream readAsciiStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readAsciiStream()", this);

            OracleLog.recursiveTrace = false;
        }

        InputStream ret = null;
        try {
            Datum datum = this.attributes[this.index];

            if (datum != null) {
                ret = datum.asciiStreamValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readAsciiStream: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream readBinaryStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBinaryStream()", this);

            OracleLog.recursiveTrace = false;
        }

        InputStream ret = null;
        try {
            Datum datum = this.attributes[this.index];

            if (datum != null) {
                ret = datum.binaryStreamValue();
            }
        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBinaryStream: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object readObject() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readObject() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = (Datum) readOracleObject();

        if (datum != null) {
            if ((datum instanceof STRUCT)) {
                return ((STRUCT) datum).toJdbc(this.map);
            }
            datum.toJdbc();
        }

        return null;
    }

    public Ref readRef() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readRef() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return readREF();
    }

    public Blob readBlob() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readBlob() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return readBLOB();
    }

    public Clob readClob() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readClob() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return readCLOB();
    }

    public Array readArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OracleJdbc2SQLInput.readArray() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return readARRAY();
    }

    public Struct readStruct() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleJdbc2SQLInput.readStruct() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return readSTRUCT();
    }

    public boolean wasNull() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.wasNull()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.index == 0) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.FINER,
                             "OracleJdbc2SQLInput.wasNull: an exception is thrown because 'index' = 0.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(24);
        }

        boolean ret = this.attributes[(this.index - 1)] == null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.wasNull: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object readOracleObject() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readOracleObject()", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = this.attributes[(this.index++)];

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readOracleObject: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public NUMBER readNUMBER() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readNUMBER()", this);

            OracleLog.recursiveTrace = false;
        }

        NUMBER ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof NUMBER)) {
                    ret = (NUMBER) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readNUMBER: invalid column type, it should be NUMBER. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readNUMBER: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public CHAR readCHAR() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readCHAR()", this);

            OracleLog.recursiveTrace = false;
        }

        CHAR ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof CHAR)) {
                    ret = (CHAR) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readCHAR: invalid column type, it should be CHAR. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readCHAR: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public DATE readDATE() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readDATE()", this);

            OracleLog.recursiveTrace = false;
        }

        DATE ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof DATE)) {
                    ret = (DATE) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readDATE: invalid column type, it should be DATE. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readDATE: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public BFILE readBFILE() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBFILE()", this);

            OracleLog.recursiveTrace = false;
        }

        BFILE ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof BFILE)) {
                    ret = (BFILE) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readBFILE: invalid column type, it should be BFILE. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBFILE: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public BLOB readBLOB() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBLOB()", this);

            OracleLog.recursiveTrace = false;
        }

        BLOB ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof BLOB)) {
                    ret = (BLOB) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readBLOB: invalid column type, it should be BLOB. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readBLOB: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public CLOB readCLOB() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readCLOB()", this);

            OracleLog.recursiveTrace = false;
        }

        CLOB ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof CLOB)) {
                    ret = (CLOB) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readCLOB: invalid column type, it should be CLOB. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readCLOB: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public RAW readRAW() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readRAW()", this);

            OracleLog.recursiveTrace = false;
        }

        RAW ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof RAW)) {
                    ret = (RAW) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readRAW: invalid column type, it should be RAW. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readRAW: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public REF readREF() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readREF()", this);

            OracleLog.recursiveTrace = false;
        }

        REF ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof REF)) {
                    ret = (REF) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readREF: invalid column type, it should be REF. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readREF: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ROWID readROWID() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readROWID()", this);

            OracleLog.recursiveTrace = false;
        }

        ROWID ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof ROWID)) {
                    ret = (ROWID) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readROWID: invalid column type, it should be ROWID. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readROWID: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ARRAY readARRAY() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readARRAY()", this);

            OracleLog.recursiveTrace = false;
        }

        ARRAY ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof ARRAY)) {
                    ret = (ARRAY) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readARRAY: invalid column type, it should be ARRAY. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readARRAY: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public STRUCT readSTRUCT() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readSTRUCT()", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT ret = null;
        try {
            if (this.attributes[this.index] != null) {
                if ((this.attributes[this.index] instanceof STRUCT)) {
                    ret = (STRUCT) this.attributes[this.index];
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger.log(Level.FINER,
                                                  "OracleJdbc2SQLInput.readSTRUCT: invalid column type, it should be STRUCT. attributes[index="
                                                          + this.index + "]="
                                                          + this.attributes[this.index]
                                                          + ". An exception is thrown.", this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(4, null);
                }
            }

        } finally {
            this.index += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleJdbc2SQLInput.readSTRUCT: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public URL readURL() throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.OracleJdbc2SQLInput"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.OracleJdbc2SQLInput JD-Core Version: 0.6.0
 */