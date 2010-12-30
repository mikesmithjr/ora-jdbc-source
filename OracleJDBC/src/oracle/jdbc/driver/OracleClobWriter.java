package oracle.jdbc.driver;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.CLOB;

public class OracleClobWriter extends Writer {
    DBConversion dbConversion;
    CLOB clob;
    long lobOffset;
    char[] charBuf;
    byte[] nativeBuf;
    int pos;
    int count;
    int chunkSize;
    boolean isClosed;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleClobWriter(CLOB clob) throws SQLException {
        this(
                clob,
                ((PhysicalConnection) clob.getInternalConnection()).getDefaultStreamChunkSize() / 3,
                1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleClobWriter.OracleClobWriter(clob=" + clob
                    + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobWriter(CLOB clob, int bufferSize) throws SQLException {
        this(clob, bufferSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleClobWriter.OracleClobWriter(clob=" + clob
                    + ", bufferSize=" + bufferSize + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobWriter(CLOB clob, int bufferSize, long beginOffset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleClobWriter.OracleClobWriter(clob=" + clob
                    + ", bufferSize=" + bufferSize + ", beginOffset=" + beginOffset
                    + ") -- after supper()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((clob == null) || (bufferSize <= 0) || (clob.getJavaSqlConnection() == null)
                || (beginOffset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.dbConversion = ((PhysicalConnection) clob.getInternalConnection()).conversion;

        this.clob = clob;
        this.lobOffset = beginOffset;

        this.charBuf = new char[bufferSize];
        this.nativeBuf = new byte[bufferSize * 3];
        this.pos = (this.count = 0);
        this.chunkSize = bufferSize;

        this.isClosed = false;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobWriter.write(cbuf=" + cbuf + ", off="
                    + off + ", len=" + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.lock) {
            ensureOpen();

            int start = off;
            int end = start + Math.min(len, cbuf.length - off);

            while (start < end) {
                int charsWritten = Math.min(this.chunkSize - this.count, end - start);

                System.arraycopy(cbuf, start, this.charBuf, this.count, charsWritten);

                start += charsWritten;
                this.count += charsWritten;

                if (this.count >= this.chunkSize)
                    flushBuffer();
            }
        }
    }

    public void flush() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobWriter.flush()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.lock) {
            ensureOpen();
            flushBuffer();
        }
    }

    public void close() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobWriter.close()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.lock) {
            flushBuffer();

            this.isClosed = true;
        }
    }

    private void flushBuffer() throws IOException {
        synchronized (this.lock) {
            try {
                if (this.count > 0) {
                    this.clob.setString(this.lobOffset, new String(this.charBuf, 0, this.count));

                    this.lobOffset += this.count;

                    this.count = 0;
                }
            } catch (SQLException e) {
                DatabaseError.SQLToIOException(e);
            }
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
                    .forName("oracle.jdbc.driver.OracleClobWriter"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleClobWriter JD-Core Version: 0.6.0
 */