package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.CLOB;

public class OracleClobInputStream extends OracleBufferedStream {
    protected long lobOffset;
    protected CLOB clob;
    protected long markedByte;
    protected boolean endOfStream;
    protected char[] charBuf;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleClobInputStream(CLOB clob) throws SQLException {
        this(clob, ((PhysicalConnection) clob.getJavaSqlConnection()).getDefaultStreamChunkSize(),
                1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleClobInputStream.OracleClobInputStream(clob=" + clob
                                               + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobInputStream(CLOB clob, int chunkSize) throws SQLException {
        this(clob, chunkSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleClobInputStream.OracleClobInputStream(clob=" + clob
                                               + ", chunkSize=" + chunkSize + ") -- after this()",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleClobInputStream(CLOB clob, int chunkSize, long beginOffset) throws SQLException {
        super(chunkSize);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleClobInputStream.OracleClobInputStream(clob=" + clob
                                               + ", chunkSize=" + chunkSize + ", beginOffset="
                                               + beginOffset + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((clob == null) || (chunkSize <= 0) || (beginOffset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.lobOffset = beginOffset;
        this.clob = clob;
        this.markedByte = -1L;
        this.endOfStream = false;
        this.charBuf = new char[chunkSize];
    }

    public boolean needBytes() throws IOException {
        ensureOpen();

        if (this.pos >= this.count) {
            if (!this.endOfStream) {
                try {
                    this.count = this.clob.getChars(this.lobOffset, this.chunkSize, this.charBuf);

                    for (int i = 0; i < this.count; i++) {
                        this.buf[i] = (byte) this.charBuf[i];
                    }
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

    protected void ensureOpen() throws IOException {
        try {
            if (this.closed)
                DatabaseError.throwSqlException(57, null);
        } catch (SQLException e) {
            DatabaseError.SQLToIOException(e);
        }
    }

    public boolean markSupported() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobInputStream.markSupported()", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public synchronized void mark(int readLimit) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobInputStream.mark(readLimit="
                    + readLimit + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (readLimit < 0) {
            throw new IllegalArgumentException("Read-ahead limit < 0");
        }
        this.markedByte = (this.lobOffset - this.count + this.pos);
    }

    public synchronized void reset() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobInputStream.reset()", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        if (this.markedByte < 0L) {
            throw new IOException("Mark invalid or stream not marked.");
        }
        this.lobOffset = this.markedByte;
        this.pos = this.count;
        this.endOfStream = false;
    }

    public long skip(long n) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleClobInputStream.skip(n=" + n + ")", this);

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
                long remainLen = 0L;

                remainLen = this.clob.length() - this.lobOffset + 1L;

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
                    .forName("oracle.jdbc.driver.OracleClobInputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleClobInputStream JD-Core Version: 0.6.0
 */