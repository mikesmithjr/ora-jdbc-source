package oracle.jdbc.driver;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.CLOB;

public class OracleClobOutputStream extends OutputStream {
    long lobOffset;
    CLOB clob;
    byte[] buf;
    int count;
    int bufSize;
    boolean isClosed;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleClobOutputStream(CLOB clob) throws SQLException {
        this(clob, ((PhysicalConnection) clob.getJavaSqlConnection()).getDefaultStreamChunkSize(),
                1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleClobOutputStream.OracleClobOutputStream(clob=" + clob
                                               + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobOutputStream(CLOB clob, int bufferSize) throws SQLException {
        this(clob, bufferSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "OracleClobOutputStream.OracleClobOutputStream(clob=" + clob
                            + ", bufferSize=" + bufferSize + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobOutputStream(CLOB clob, int bufferSize, long beginOffset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleClobOutputStream.OracleClobOutputStream(clob=" + clob
                                               + ", bufferSize=" + bufferSize + ", beginOffset="
                                               + beginOffset + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((clob == null) || (bufferSize <= 0) || (beginOffset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.clob = clob;
        this.lobOffset = beginOffset;

        this.buf = new byte[bufferSize];
        this.count = 0;
        this.bufSize = bufferSize;

        this.isClosed = false;
    }

    public void write(int b) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobOutputStream.write(b=" + b + ")",
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
            OracleLog.driverLogger.log(Level.INFO, "OracleClobOutputStream.write(b=" + b + ", off="
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
            OracleLog.driverLogger.log(Level.INFO, "OracleClobOutputStream.flush()", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        flushBuffer();
    }

    public void close() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobOutputStream.close()", this);

            OracleLog.recursiveTrace = false;
        }

        flushBuffer();

        this.isClosed = true;
    }

    private void flushBuffer() throws IOException {
        try {
            if (this.count > 0) {
                char[] charArr = new char[this.count];

                for (int i = 0; i < this.count; i++) {
                    charArr[i] = (char) this.buf[i];
                }
                this.lobOffset += this.clob.putChars(this.lobOffset, charArr);

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
                    .forName("oracle.jdbc.driver.OracleClobOutputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleClobOutputStream JD-Core Version: 0.6.0
 */