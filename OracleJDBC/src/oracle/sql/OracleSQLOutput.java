package oracle.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class OracleSQLOutput implements SQLOutput {
    private StructDescriptor descriptor;
    private Object[] attributes;
    private int index;
    private OracleConnection conn;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public OracleSQLOutput(StructDescriptor descriptor, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.OracleSQLOutput( descriptor="
                    + descriptor + ", conn=" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.descriptor = descriptor;
        this.attributes = new Object[descriptor.getLength()];
        this.conn = conn;
        this.index = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.OracleSQLOutput: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public STRUCT getSTRUCT() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OracleSQLOutput.getSTRUCT() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return new STRUCT(this.descriptor, this.conn, this.attributes);
    }

    public void writeString(String x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeString( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeBoolean(boolean x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBoolean( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Boolean(x);
    }

    public void writeByte(byte x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeByte( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Integer(x);
    }

    public void writeShort(short x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeShort( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Integer(x);
    }

    public void writeInt(int x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeInt( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Integer(x);
    }

    public void writeLong(long x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeLong( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Long(x);
    }

    public void writeFloat(float x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeFloat( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Float(x);
    }

    public void writeDouble(double x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeDouble( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = new Double(x);
    }

    public void writeBigDecimal(BigDecimal x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBigDecimal( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeBytes(byte[] x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBytes( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeDate(Date x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeDate( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeTime(Time x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeTime( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeTimestamp(Timestamp x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeTimestamp( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeCharacterStream(Reader x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeCharacterStream( x=" + x
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        StringBuffer stringBuf = new StringBuffer();

        char[] charBuf = new char[100];
        int count = 0;
        try {
            while ((count = x.read(charBuf)) != -1) {
                stringBuf.append(charBuf, 0, count);
            }

        } catch (IOException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "OracleSQLOutput.writeCharacterStream: an IO exception has been caught while reading in 'x'. The exception is thrown."
                                     + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(e);
        }

        String s = stringBuf.substring(0, stringBuf.length());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINEST,
                                      "OracleSQLOutput.writeCharacterStream: Here is what will be written: "
                                              + s, this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = s;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeCharacterStream: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void writeAsciiStream(InputStream x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeAsciiStream( x=" + x + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        StringBuffer stringBuf = new StringBuffer();

        byte[] binaryBuf = new byte[100];
        char[] charBuf = new char[100];
        int count = 0;
        try {
            while ((count = x.read(binaryBuf)) != -1) {
                for (int i = 0; i < count; i++) {
                    charBuf[i] = (char) binaryBuf[i];
                }
                stringBuf.append(charBuf, 0, count);
            }

        } catch (IOException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "OracleSQLOutput.writeAsciiStream: an IO exception has been caught while reading in 'x'. The exception is thrown."
                                     + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(e);
        }

        String s = stringBuf.substring(0, stringBuf.length());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINEST,
                                      "OracleSQLOutput.writeAsciiStream: Here is what will be written: "
                                              + s, this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = s;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeAsciiStream: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void writeBinaryStream(InputStream x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBinaryStream( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        writeAsciiStream(x);
    }

    public void writeObject(SQLData x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OracleSQLOutput.writeObject( x=" + x + ")", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = null;

        if (x != null) {
            StructDescriptor desc = StructDescriptor
                    .createDescriptor(x.getSQLTypeName(), this.conn);

            SQLOutput sqlOutput = desc.toJdbc2SQLOutput();

            x.writeSQL(sqlOutput);

            s = ((OracleSQLOutput) sqlOutput).getSTRUCT();
        }

        writeStruct(s);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void writeObject(Object x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OracleSQLOutput.writeObject( x=" + x + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((x != null) && ((x instanceof SQLData)))
            writeObject((SQLData) x);
        else {
            this.attributes[(this.index++)] = x;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void writeRef(Ref x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeRef( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeBlob(Blob x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBlob( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeClob(Clob x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeClob( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeStruct(Struct x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeStruct( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeArray(Array x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeArray( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeOracleObject(Datum x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeOracleObject( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeRef(REF x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeRef( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeBlob(BLOB x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBlob( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeBfile(BFILE x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeBfile( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeClob(CLOB x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeClob( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeStruct(STRUCT x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeStruct( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeArray(ARRAY x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeArray( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeNUMBER(NUMBER x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeNUMBER( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeCHAR(CHAR x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeCHAR( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeDATE(DATE x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeDATE( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeRAW(RAW x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeRAW( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeROWID(ROWID x) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OracleSQLOutput.writeROWID( x=" + x
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.attributes[(this.index++)] = x;
    }

    public void writeURL(URL x) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.OracleSQLOutput"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.OracleSQLOutput JD-Core Version: 0.6.0
 */