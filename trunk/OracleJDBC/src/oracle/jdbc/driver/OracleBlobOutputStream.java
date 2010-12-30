package oracle.jdbc.driver;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.BLOB;

public class OracleBlobOutputStream extends OutputStream {
    long lobOffset;
    BLOB blob;
    byte[] buf;
    int count;
    int bufSize;
    boolean isClosed;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public OracleBlobOutputStream(BLOB blob) throws SQLException {
        this(blob, ((PhysicalConnection) blob.getJavaSqlConnection()).getDefaultStreamChunkSize());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobOutputStream.OracleBlobOutputStream(blob=" + blob
                                               + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleBlobOutputStream(BLOB blob, int bufferSize) throws SQLException {
        this(blob, bufferSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "OracleBlobOutputStream.OracleBlobOutputStream(blob=" + blob
                            + ", bufferSize=" + bufferSize + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleBlobOutputStream(BLOB blob, int bufferSize, long offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobOutputStream.OracleBlobOutputStream(blob=" + blob
                                               + ", bufferSize=" + bufferSize + ", offset="
                                               + offset + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((blob == null) || (bufferSize <= 0) || (offset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.blob = blob;
        this.lobOffset = offset;

        this.buf = new byte[bufferSize];
        this.count = 0;
        this.bufSize = bufferSize;

        this.isClosed = false;
    }

    public void write(int b) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobOutputStream.write(b=" + b + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        if (this.count >= this.buf.length) {
            flushBuffer();
        }
        this.buf[(this.count++)] = (byte) b;
    }

    public void write(byte[] b, int off, int len) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobOutputStream.write(b=" + b + ", off="
                    + off + ", len=" + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        int start = off;
        int end = start + Math.min(len, b.length - off);

        while (start < end) {
            int bytesWritten = Math.min(this.bufSize - this.count, end - start);

            System.arraycopy(b, start, this.buf, this.count, bytesWritten);

            start += bytesWritten;
            this.count += bytesWritten;

            if (this.count >= this.bufSize)
                flushBuffer();
        }
    }

    public void flush() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobOutputStream.flush()", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        flushBuffer();
    }

    public void close() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobOutputStream.close()", this);

            OracleLog.recursiveTrace = false;
        }

        flushBuffer();

        this.isClosed = true;
    }

    private void flushBuffer() throws IOException {
        try {
            if (this.count > 0) {
                if (this.count < this.buf.length) {
                    byte[] tmp_buf = new byte[this.count];

                    System.arraycopy(this.buf, 0, tmp_buf, 0, this.count);

                    this.lobOffset += this.blob.putBytes(this.lobOffset, tmp_buf);
                } else {
                    this.lobOffset += this.blob.putBytes(this.lobOffset, this.buf);
                }
                this.count = 0;
            }
        } catch (SQLException e) {
            DatabaseError.SQLToIOException(e);
        }
    }

    void ensureOpen() throws IOException {
        try {
            if (this.isClosed)
                DatabaseError.throwSqlException(57, null);
        } catch (SQLException e) {
            DatabaseError.SQLToIOException(e);
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleBlobOutputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleBlobOutputStream JD-Core Version: 0.6.0
 */