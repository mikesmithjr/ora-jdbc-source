package oracle.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class CLOB extends DatumWithConnection implements Clob {
    public static final int MAX_CHUNK_SIZE = 32768;
    public static final int DURATION_SESSION = 10;
    public static final int DURATION_CALL = 12;
    static final int OLD_WRONG_DURATION_SESSION = 1;
    static final int OLD_WRONG_DURATION_CALL = 2;
    public static final int MODE_READONLY = 0;
    public static final int MODE_READWRITE = 1;
    ClobDBAccess dbaccess;
    private int dbChunkSize;
    private short csform;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    protected CLOB() {
    }

    public CLOB(oracle.jdbc.OracleConnection conn) throws SQLException {
        this(conn, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.CLOB( conn=" + conn
                    + "): return -- after this() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public CLOB(oracle.jdbc.OracleConnection conn, byte[] lob_descriptor) throws SQLException {
        super(lob_descriptor);

        if (lob_descriptor != null) {
            if ((lob_descriptor[5] & 0xC0) == 64)
                this.csform = 2;
            else {
                this.csform = 1;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.CLOB( conn=" + conn + ", lob_descriptor="
                    + lob_descriptor + ") " + "-- after super() -- csform = " + this.csform, this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(conn);
        setPhysicalConnectionOf(conn);

        this.dbaccess = ((oracle.jdbc.internal.OracleConnection) conn).createClobDBAccess();

        this.dbChunkSize = -1;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.CLOB: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public CLOB(oracle.jdbc.OracleConnection conn, byte[] lob_descriptor, short csform)
            throws SQLException {
        this(conn, lob_descriptor);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.CLOB( conn=" + conn + ", lob_descriptor="
                    + lob_descriptor + ", csform=" + csform + "): return -- after this() --", this);

            OracleLog.recursiveTrace = false;
        }

        this.csform = csform;
    }

    public boolean isNCLOB() {
        boolean ret = this.csform == 2;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.isNCLOB(): return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public long length() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.length() -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().length(this);
    }

    public String getSubString(long pos, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getSubString( pos=" + pos + ", length="
                    + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((length < 0) || (pos < 1L)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.getSubString: Invalid argument, 'length' should be >= 0 and 'pos' >=1. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "getSubString");
        }

        String ret = null;

        if (length == 0) {
            ret = new String();
        } else {
            char[] chars_read = new char[length];

            int num_chars_rd = getChars(pos, length, chars_read);

            if (num_chars_rd > 0) {
                ret = new String(chars_read, 0, num_chars_rd);
            } else {
                ret = new String();
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getSubString: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Reader getCharacterStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "CLOB.getCharacterStream() -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newReader(this, getBufferSize(), 0L);
    }

    public InputStream getAsciiStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getAsciiStream() -- no result trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newInputStream(this, getBufferSize(), 0L);
    }

    public long position(String searchstr, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.position( searchstr=" + searchstr
                    + ", start=" + start + ") -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().position(this, searchstr, start);
    }

    public long position(Clob searchstr, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.position( searchstr=" + searchstr
                    + ", start=" + start + ") -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().position(this, (CLOB) searchstr, start);
    }

    public int getChars(long pos, int length, char[] buffer) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getChars( pos=" + pos + ", length="
                    + length + ", buffer=" + buffer + ") -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().getChars(this, pos, length, buffer);
    }

    /** @deprecated */
    public Writer getCharacterOutputStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "CLOB.getCharacterOutputStream() -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setCharacterStream(0L);
    }

    /** @deprecated */
    public OutputStream getAsciiOutputStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "CLOB.getAsciiOutputStream() -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setAsciiStream(0L);
    }

    public byte[] getLocator() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getLocator() -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getBytes();
    }

    public void setLocator(byte[] locator) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setLocator( locator=" + locator
                    + ") -- no result trace --", this);

            OracleLog.recursiveTrace = false;
        }

        setBytes(locator);
    }

    public int putChars(long pos, char[] chars) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.putChars( pos=" + pos + ", chars=" + chars
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = getDBAccess().putChars(this, pos, chars, 0, chars != null ? chars.length : 0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.putChars: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int putChars(long pos, char[] chars, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.putChars( pos=" + pos + ", chars=" + chars
                    + ", length=" + length + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().putChars(this, pos, chars, 0, length);
    }

    public int putChars(long pos, char[] chars, int offset, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "CLOB.putChars( pos=" + pos + ", chars=" + chars + ", offset="
                            + offset + ", length=" + length + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().putChars(this, pos, chars, offset, length);
    }

    /** @deprecated */
    public int putString(long pos, String str) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.putString( pos=" + pos + ", str=" + str
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return setString(pos, str);
    }

    public int getChunkSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "CLOB.getChunkSize() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.dbChunkSize <= 0) {
            this.dbChunkSize = getDBAccess().getChunkSize(this);
        }

        return this.dbChunkSize;
    }

    public int getBufferSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getBufferSize()", this);

            OracleLog.recursiveTrace = false;
        }

        int size = getChunkSize();
        int ret = 0;

        if ((size >= 32768) || (size <= 0)) {
            ret = 32768;
        } else {
            ret = 32768 / size * size;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getBufferSize: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    /** @deprecated */
    public static CLOB empty_lob() throws SQLException {
        return getEmptyCLOB();
    }

    public static CLOB getEmptyCLOB() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getEmptyCLOB() -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        byte[] locator = new byte[86];

        locator[1] = 84;
        locator[5] = 24;

        CLOB clob = new CLOB();

        clob.setShareBytes(locator);

        return clob;
    }

    public boolean isEmptyLob() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.isEmptyLob() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (shareBytes()[5] & 0x10) != 0;
    }

    /** @deprecated */
    public OutputStream getAsciiOutputStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getAsciiOutputStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setAsciiStream(pos);
    }

    /** @deprecated */
    public Writer getCharacterOutputStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getCharacterOutputStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setCharacterStream(pos);
    }

    public InputStream getAsciiStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getAsciiStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newInputStream(this, getBufferSize(), pos);
    }

    public Reader getCharacterStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getCharacterStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newReader(this, getBufferSize(), pos);
    }

    /** @deprecated */
    public void trim(long newlen) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.trim( newlen=" + newlen
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        truncate(newlen);
    }

    public static CLOB createTemporary(Connection conn, boolean cache, int _duration)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.createTemporary( conn=" + conn + ", cache="
                    + cache + "duration=" + _duration + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return createTemporary(conn, cache, _duration, (short) 1);
    }

    public static CLOB createTemporary(Connection conn, boolean cache, int _duration,
            short form_of_use) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.createTemporary( conn=" + conn + ", cache="
                    + cache + "duration=" + _duration + ", form_of_use=" + form_of_use
                    + " ) -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        int duration = _duration;

        if (_duration == 1) {
            duration = 10;
        }
        if (_duration == 2) {
            duration = 12;
        }
        if ((conn == null) || ((duration != 10) && (duration != 12))) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.createTemporary: Invalid argument, 'conn' should not be null and 'duration' should either be equal to DURATION_SESSION or DURATION_CALL. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        oracle.jdbc.internal.OracleConnection physConn = ((oracle.jdbc.OracleConnection) conn)
                .physicalConnectionWithin();

        CLOB result = getDBAccess(physConn).createTemporaryClob(physConn, cache, duration,
                                                                form_of_use);

        result.csform = form_of_use;

        return result;
    }

    public static void freeTemporary(CLOB temp_lob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.freeTemporary( temp_lob=" + temp_lob
                    + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if (temp_lob == null) {
            return;
        }
        temp_lob.freeTemporary();
    }

    public static boolean isTemporary(CLOB lob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.isTemporary( lob=" + lob
                    + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if (lob == null) {
            return false;
        }
        return lob.isTemporary();
    }

    public void freeTemporary() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.freeTemporary() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().freeTemporary(this);
    }

    public boolean isTemporary() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.isTemporary() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().isTemporary(this);
    }

    public void open(int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.open( mode=" + mode
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().open(this, mode);
    }

    public void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.close() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().close(this);
    }

    public boolean isOpen() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.isOpen() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().isOpen(this);
    }

    public int setString(long pos, String str) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setString( pos=" + pos + ", str=" + str
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.setString: Invalid argument, 'pos' should not be < 1. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "setString()");
        }

        int ret = 0;

        if ((str != null) && (str.length() != 0)) {
            ret = putChars(pos, str.toCharArray());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setString: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setString( pos=" + pos + ", str=" + str
                    + ", offset=" + offset + ", len=" + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.setString: Invalid argument, 'pos' should not be < 1. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "setString()");
        }

        if (offset < 0) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.setString: Invalid argument, 'offset' should not be < 0. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "setString()");
        }

        if (offset + len > str.length()) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.setString: Invalid argument, 'offset + len' should not be exceed string length. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "setString()");
        }

        int ret = 0;

        if ((str != null) && (str.length() != 0)) {
            ret = putChars(pos, str.toCharArray(), offset, len);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setString: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setAsciiStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newOutputStream(this, getBufferSize(), pos);
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.setCharacterStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newWriter(this, getBufferSize(), pos);
    }

    public void truncate(long len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.truncate( len=" + len
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (len < 0L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "CLOB.truncate: Invalid argument, 'len' should not be < 0. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        getDBAccess().trim(this, len);
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.toJdbc(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.isConvertibleTo( jClass=" + jClass
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        return (class_name.compareTo("java.io.InputStream") == 0)
                || (class_name.compareTo("java.io.Reader") == 0);
    }

    public Reader characterStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "CLOB.characterStreamValue() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getCharacterStream();
    }

    public InputStream asciiStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.asciiStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getAsciiStream();
    }

    public InputStream binaryStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.binaryStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getAsciiStream();
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.makeJdbcArray( arraySize=" + arraySize
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return new CLOB[arraySize];
    }

    public ClobDBAccess getDBAccess() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getDBAccess()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.dbaccess == null) {
            if (isEmptyLob()) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "CLOB.getDBAccess: Invalid empty LOB. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(98);
            }

            this.dbaccess = getInternalConnection().createClobDBAccess();
        }

        if (getPhysicalConnection().isClosed()) {
            DatabaseError.throwSqlException(8);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getDBAccess: return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.dbaccess;
    }

    public static ClobDBAccess getDBAccess(Connection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "CLOB.getDBAccess( conn=" + conn
                    + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return ((oracle.jdbc.OracleConnection) conn).physicalConnectionWithin()
                .createClobDBAccess();
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.CLOB"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CLOB JD-Core Version: 0.6.0
 */