package oracle.sql;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class BFILE extends DatumWithConnection {
    public static final int MAX_CHUNK_SIZE = 32512;
    public static final int MODE_READONLY = 0;
    public static final int MODE_READWRITE = 1;
    BfileDBAccess dbaccess;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    protected BFILE() {
    }

    public BFILE(oracle.jdbc.OracleConnection conn) throws SQLException {
        this(conn, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.BFILE( conn=" + conn
                    + ") -- after this() -- : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public BFILE(oracle.jdbc.OracleConnection conn, byte[] lob_descriptor) throws SQLException {
        super(lob_descriptor);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.BFILE( conn=" + conn + ", lob_descriptor="
                    + lob_descriptor + ") -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        setPhysicalConnectionOf(conn);

        this.dbaccess = getInternalConnection().createBfileDBAccess();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.BFILE: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public long length() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.length() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().length(this);
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBytes( pos=" + pos + ", length="
                    + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((pos < 1L) || (length < 0)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "BFILE.getBytes: Invalid arguments, 'pos' and 'length' should be >0. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, null);
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
            } else {
                ret = new byte[0];
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int getBytes(long pos, int length, byte[] buf) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBytes( pos=" + pos + ", length="
                    + length + ", buf=" + buf + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = getDBAccess().getBytes(this, pos, length, buf);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBytes: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public InputStream getBinaryStream() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBinaryStream()", this);

            OracleLog.recursiveTrace = false;
        }

        InputStream ret = getDBAccess().newInputStream(this, 32512, 0L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBinaryStream: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public long position(byte[] pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.position( pattern=" + pattern + ", start="
                    + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        long ret = getDBAccess().position(this, pattern, start);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.position: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public long position(BFILE pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.position( pattern=" + pattern + ", start="
                    + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        long ret = getDBAccess().position(this, pattern, start);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.position: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = getDBAccess().getName(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getDirAlias() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getDirAlias()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = getDBAccess().getDirAlias(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getDirAlias: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public void openFile() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.openFile() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().openFile(this);
    }

    public boolean isFileOpen() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.isFileOpen()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = getDBAccess().isFileOpen(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.isFileOpen: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean fileExists() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.fileExists()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = getDBAccess().fileExists(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.fileExists: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public void closeFile() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.closeFile() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().closeFile(this);
    }

    public byte[] getLocator() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getLocator() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getBytes();
    }

    public void setLocator(byte[] locator) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.setLocator( locator=" + locator
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        setBytes(locator);
    }

    public InputStream getBinaryStream(long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.getBinaryStream( pos=" + pos
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().newInputStream(this, 32512, pos);
    }

    public void open() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.open() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().open(this, 0);
    }

    public void open(int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.open( mode=" + mode
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (mode != 0) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "BFILE.open: LOB should be in READONLY mode. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(102);
        }

        getDBAccess().open(this, mode);
    }

    public void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.close() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getDBAccess().close(this);
    }

    public boolean isOpen() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.isOpen() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getDBAccess().isOpen(this);
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.toJdbc(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.isConvertibleTo( jClass=" + jClass + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        boolean ret = (class_name.compareTo("java.io.InputStream") == 0)
                || (class_name.compareTo("java.io.Reader") == 0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.isConvertibleTo: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Reader characterStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "BFILE.characterStreamValue() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        getInternalConnection();
        return getDBAccess().newConversionReader(this, 8);
    }

    public InputStream asciiStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.asciiStreamValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        getInternalConnection();
        return getDBAccess().newConversionInputStream(this, 2);
    }

    public InputStream binaryStreamValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "BFILE.binaryStreamValue() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getBinaryStream();
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "BFILE.makeJdbcArray( arraySize=" + arraySize
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return new BFILE[arraySize];
    }

    public BfileDBAccess getDBAccess() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "BFILE.getDBAccess() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.dbaccess == null) {
            this.dbaccess = getInternalConnection().createBfileDBAccess();
        }
        if (getPhysicalConnection().isClosed()) {
            DatabaseError.throwSqlException(8);
        }
        return this.dbaccess;
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.BFILE"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.BFILE JD-Core Version: 0.6.0
 */