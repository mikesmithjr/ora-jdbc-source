package oracle.jdbc.driver;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.CLOB;

public class OracleClobReader extends Reader {
    CLOB clob;
    DBConversion dbConversion;
    long lobOffset;
    long markedChar;
    char[] buf;
    int pos;
    int count;
    int chunkSize;
    boolean isClosed;
    boolean endOfStream;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleClobReader(CLOB clob) throws SQLException {
        this(clob,
                ((PhysicalConnection) clob.getInternalConnection()).getDefaultStreamChunkSize() / 3);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleClobReader.OracleClobReader(clob=" + clob
                    + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobReader(CLOB clob, int bufferSize) throws SQLException {
        this(clob, bufferSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleClobReader.OracleClobReader(clob=" + clob
                    + ", bufferSize=" + bufferSize + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobReader(CLOB clob, int bufferSize, long beginOffset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleClobReader.OracleClobReader(clob=" + clob
                    + ", bufferSize=" + bufferSize + ", beginOffset=" + beginOffset
                    + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((clob == null) || (bufferSize <= 0) || (clob.getInternalConnection() == null)
                || (beginOffset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.dbConversion = ((PhysicalConnection) clob.getInternalConnection()).conversion;

        this.clob = clob;
        this.lobOffset = beginOffset;
        this.markedChar = -1L;

        this.buf = new char[bufferSize];
        this.pos = (this.count = 0);
        this.chunkSize = bufferSize;

        this.isClosed = false;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.read(cbuf=" + cbuf + ", off="
                    + off + ", len=" + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        if (!needChars()) {
            return -1;
        }
        int start = off;
        int end = start + Math.min(len, cbuf.length - off);

        start += writeChars(cbuf, start, end - start);

        while ((start < end) && (needChars())) {
            start += writeChars(cbuf, start, end - start);
        }

        return start - off;
    }

    protected boolean needChars() throws IOException {
        ensureOpen();

        if (this.pos >= this.count) {
            if (!this.endOfStream) {
                try {
                    this.count = this.clob.getChars(this.lobOffset, this.chunkSize, this.buf);

                    if (this.count < this.chunkSize) {
                        this.endOfStream = true;
                    }
                    if (this.count > 0) {
                        this.pos = 0;
                        this.lobOffset += this.count;

                        return true;
                    }
                } catch (SQLException e) {
                    DatabaseError.SQLToIOException(e);
                }
            }

            return false;
        }

        return true;
    }

    protected int writeChars(char[] destbuf, int offset, int length) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.writeChars(destbuf=" + destbuf
                    + ", offset=" + offset + ", length=" + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int availableLength = Math.min(length, this.count - this.pos);

        System.arraycopy(this.buf, this.pos, destbuf, offset, availableLength);

        this.pos += availableLength;

        return availableLength;
    }

    public boolean ready() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.ready()", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        return this.pos < this.count;
    }

    public void close() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.close()", this);

            OracleLog.recursiveTrace = false;
        }

        this.isClosed = true;
    }

    void ensureOpen() throws IOException {
        try {
            if (this.isClosed)
                DatabaseError.throwSqlException(57, null);
        } catch (SQLException e) {
            DatabaseError.SQLToIOException(e);
        }
    }

    public boolean markSupported() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.markSupported()", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public void mark(int readAheadLimit) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.mark(readAheadLimit="
                    + readAheadLimit + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (readAheadLimit < 0) {
            throw new IllegalArgumentException("Read-ahead limit < 0");
        }
        this.markedChar = (this.lobOffset - this.count + this.pos);
    }

    public void reset() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.reset()", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        if (this.markedChar < 0L) {
            throw new IOException("Mark invalid or stream not marked.");
        }
        this.lobOffset = this.markedChar;
        this.pos = this.count;
        this.endOfStream = false;
    }

    public long skip(long n) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobReader.skip(n=" + n + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        long start = 0L;

        if (this.count - this.pos >= n) {
            this.pos = (int) (this.pos + n);
            start += n;
        } else {
            start += this.count - this.pos;
            this.pos = this.count;
            try {
                long remainLen = this.clob.length() - this.lobOffset + 1L;

                if (remainLen >= n - start) {
                    this.lobOffset += n - start;
                    start += n - start;
                } else {
                    this.lobOffset += remainLen;
                    start += remainLen;
                }
            } catch (SQLException e) {
                DatabaseError.SQLToIOException(e);
            }
        }

        return start;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleClobReader"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleClobReader JD-Core Version: 0.6.0
 */