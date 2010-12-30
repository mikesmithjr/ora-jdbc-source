package oracle.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class BLOB extends DatumWithConnection implements Blob {
    public static final int MAX_CHUNK_SIZE = 32768;
    public static final int DURATION_SESSION = 10;
    public static final int DURATION_CALL = 12;
    static final int OLD_WRONG_DURATION_SESSION = 1;
    static final int OLD_WRONG_DURATION_CALL = 2;
    public static final int MODE_READONLY = 0;
    public static final int MODE_READWRITE = 1;
    BlobDBAccess dbaccess;
    int dbChunkSize;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    protected BLOB() {
    }

    public BLOB(oracle.jdbc.OracleConnection conn) throws SQLException {
        this(conn, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.BLOB( conn=" + conn
                    + "): return -- after this() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BLOB(oracle.jdbc.OracleConnection conn, byte[] lob_descriptor) throws SQLException {
        super(lob_descriptor);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.BLOB( conn=" + conn + ", lob_descriptor="
                    + lob_descriptor + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(conn);
        setPhysicalConnectionOf(conn);

        this.dbaccess = getPhysicalConnection().createBlobDBAccess();
        this.dbChunkSize = -1;
    }

    public long length() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.length() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().length(this);
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBytes( pos=" + pos + ", length="
                    + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((length < 0) || (pos < 1L)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "BLOB.length: Invalid argument, 'length' should be >=0 and 'pos' >=1. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "getBytes()");
        }

        byte[] ret = null;

        if (length == 0) {
            ret = new byte[0];
        } else {
            long num_bytes_read = 0L;
            byte[] bytes_read = new byte[length];

            num_bytes_read = getBytes(pos, length, bytes_read);

            if (num_bytes_read > 0L) {
                if (num_bytes_read == length) {
                    ret = bytes_read;
                } else {
                    ret = new byte[(int) num_bytes_read];

                    System.arraycopy(bytes_read, 0, ret, 0, (int) num_bytes_read);
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream getBinaryStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBinaryStream() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newInputStream(this, getBufferSize(), 0L);
    }

    public long position(byte[] pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.position( pattern=" + pattern + ", start="
                    + start + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().position(this, pattern, start);
    }

    public long position(Blob pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.position( pattern=" + pattern + ", start="
                    + start + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().position(this, (BLOB) pattern, start);
    }

    public int getBytes(long pos, int length, byte[] buf) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBytes( pos=" + pos + ", length="
                    + length + ", buf=" + buf + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().getBytes(this, pos, length, buf);
    }

    /** @deprecated */
    public int putBytes(long pos, byte[] bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.putBytes( pos=" + pos + ", bytes=" + bytes
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setBytes(pos, bytes);
    }

    /** @deprecated */
    public int putBytes(long pos, byte[] bytes, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.putBytes( pos=" + pos + ", bytes=" + bytes
                    + ", length=" + length + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setBytes(pos, bytes, 0, length);
    }

    /** @deprecated */
    public OutputStream getBinaryOutputStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "BLOB.getBinaryOutputStream() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setBinaryStream(0L);
    }

    public byte[] getLocator() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getLocator() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getBytes();
    }

    public void setLocator(byte[] locator) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.setLocator( locator=" + locator
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        setBytes(locator);
    }

    public int getChunkSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getChunkSize()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.dbChunkSize <= 0) {
            this.dbChunkSize = getDBAccess().getChunkSize(this);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getChunkSize: return: " + this.dbChunkSize,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.dbChunkSize;
    }

    public int getBufferSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBufferSize()", this);

            OracleLog.recursiveTrace = false;
        }

        int size = getChunkSize();
        int ret = size;

        if ((size >= 32768) || (size <= 0)) {
            ret = 32768;
        } else {
            ret = 32768 / size * size;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBufferSize: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    /** @deprecated */
    public static BLOB empty_lob() throws SQLException {
        return getEmptyBLOB();
    }

    public static BLOB getEmptyBLOB() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getEmptyBLOB()");

            OracleLog.recursiveTrace = false;
        }

        byte[] locator = new byte[86];

        locator[1] = 84;
        locator[5] = 24;

        BLOB blob = new BLOB();

        blob.setShareBytes(locator);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getEmptyBLOB: return");

            OracleLog.recursiveTrace = false;
        }

        return blob;
    }

    public boolean isEmptyLob() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.isEmptyLob()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = (shareBytes()[5] & 0x10) != 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.isEmptyLob(): return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    /** @deprecated */
    public OutputStream getBinaryOutputStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBinaryOutputStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return setBinaryStream(pos);
    }

    public InputStream getBinaryStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getBinaryStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newInputStream(this, getBufferSize(), pos);
    }

    /** @deprecated */
    public void trim(long newlen) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.trim( newlen=" + newlen
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        truncate(newlen);
    }

    public static BLOB createTemporary(Connection conn, boolean cache, int _duration)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.createTemporary( conn=" + conn + ", cache="
                    + cache + ", duration=" + _duration + ")" + " -- no return trace --");

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
                             "BLOB.createTemporary: Invalid argument, 'conn' should not be null and 'duration' should either be equal to DURATION_SESSION or to DURATION_CALL. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        oracle.jdbc.internal.OracleConnection physConn = ((oracle.jdbc.OracleConnection) conn)
                .physicalConnectionWithin();

        return getDBAccess(physConn).createTemporaryBlob(physConn, cache, duration);
    }

    public static void freeTemporary(BLOB temp_lob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.freeTemporary( temp_lob=" + temp_lob
                    + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if (temp_lob == null) {
            return;
        }
        temp_lob.freeTemporary();
    }

    public static boolean isTemporary(BLOB lob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.isTemporary( lob=" + lob
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
            OracleLog.datumLogger.log(Level.FINE, "BLOB.freeTemporary() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().freeTemporary(this);
    }

    public boolean isTemporary() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.isTemporary() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().isTemporary(this);
    }

    public void open(int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.open( mode=" + mode
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().open(this, mode);
    }

    public void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.close() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().close(this);
    }

    public boolean isOpen() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.isOpen() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().isOpen(this);
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.setBytes( pos=" + pos + ", bytes=" + bytes
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().putBytes(this, pos, bytes, 0, bytes != null ? bytes.length : 0);
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.setBytes( pos=" + pos + ", bytes=" + bytes
                    + ", offset=" + offset + ", len=" + len + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().putBytes(this, pos, bytes, offset, len);
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "BLOB.getBinaryOutputStream() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newOutputStream(this, getBufferSize(), pos);
    }

    public void truncate(long len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.truncate( len=" + len
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (len < 0L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "BLOB.truncate: Invalid argument, 'len' should be >= 0. An exception is thrown.",
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
            OracleLog.datumLogger.log(Level.FINE, "BLOB.toJdbc(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.isConvertibleTo( jClass=" + jClass
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
                                      "BLOB.characterStreamValue() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getInternalConnection();
        return getDBAccess().newConversionReader(this, 8);
    }

    public InputStream asciiStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.asciiStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        getInternalConnection();
        return getDBAccess().newConversionInputStream(this, 2);
    }

    public InputStream binaryStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.binaryStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getBinaryStream();
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.makeJdbcArray( arraySize=" + arraySize
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new BLOB[arraySize];
    }

    public BlobDBAccess getDBAccess() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getDBAccess()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.dbaccess == null) {
            if (isEmptyLob()) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "BLOB.getDBAccess: Invalid empty lob. En exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(98);
            }

            this.dbaccess = getInternalConnection().createBlobDBAccess();
        }

        if (getPhysicalConnection().isClosed()) {
            DatabaseError.throwSqlException(8);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getDBAccess: return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.dbaccess;
    }

    public static BlobDBAccess getDBAccess(Connection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BLOB.getDBAccess()  -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return ((oracle.jdbc.OracleConnection) conn).physicalConnectionWithin()
                .createBlobDBAccess();
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.BLOB"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.BLOB JD-Core Version: 0.6.0
 */