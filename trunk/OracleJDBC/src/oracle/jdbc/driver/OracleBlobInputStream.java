package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.Datum;

public class OracleBlobInputStream extends OracleBufferedStream {
    long lobOffset;
    Datum lob;
    long markedByte;
    boolean endOfStream = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public OracleBlobInputStream(BLOB blob) throws SQLException {
        this(blob, ((PhysicalConnection) blob.getJavaSqlConnection()).getDefaultStreamChunkSize(),
                1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobInputStream.OracleBlobInputStream(blob=" + blob
                                               + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleBlobInputStream(BLOB blob, int chunkSize) throws SQLException {
        this(blob, chunkSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobInputStream.OracleBlobInputStream(blob=" + blob
                                               + ", chunkSize=" + chunkSize + ") -- after this()",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleBlobInputStream(BLOB blob, int chunkSize, long beginOffset) throws SQLException {
        super(chunkSize);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobInputStream.OracleBlobInputStream(blob=" + blob
                                               + ", chunkSize=" + chunkSize + ", beginOffset="
                                               + beginOffset + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((blob == null) || (chunkSize <= 0) || (beginOffset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.lob = blob;
        this.markedByte = -1L;
        this.lobOffset = beginOffset;
    }

    public OracleBlobInputStream(BFILE bfile) throws SQLException {
        this(bfile,
                ((PhysicalConnection) bfile.getJavaSqlConnection()).getDefaultStreamChunkSize(), 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobInputStream.OracleBlobInputStream(bfile=" + bfile
                                               + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleBlobInputStream(BFILE bfile, int chunkSize) throws SQLException {
        this(bfile, chunkSize, 1L);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobInputStream.OracleBlobInputStream(bfile=" + bfile
                                               + ", chunkSize=" + chunkSize + ") -- after this()",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleBlobInputStream(BFILE bfile, int chunkSize, long beginOffset) throws SQLException {
        super(chunkSize);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBlobInputStream.OracleBlobInputStream(bfile=" + bfile
                                               + ", chunkSize=" + chunkSize + ", beginOffset="
                                               + beginOffset + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((bfile == null) || (chunkSize <= 0) || (beginOffset < 1L)) {
            throw new IllegalArgumentException("Illegal Arguments");
        }

        this.lob = bfile;
        this.markedByte = -1L;
        this.lobOffset = beginOffset;
    }

    public boolean needBytes() throws IOException {
        ensureOpen();

        if (this.pos >= this.count) {
            if (!this.endOfStream) {
                try {
                    if ((this.lob instanceof BLOB))
                        this.count = ((BLOB) this.lob).getBytes(this.lobOffset, this.chunkSize,
                                                                this.buf);
                    else {
                        this.count = ((BFILE) this.lob).getBytes(this.lobOffset, this.chunkSize,
                                                                 this.buf);
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

    void ensureOpen() throws IOException {
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
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobInputStream.markSupported()", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public synchronized void mark(int readLimit) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobInputStream.mark(readLimit="
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
            OracleLog.driverLogger.log(Level.INFO, "OracleBlobInputStream.reset()", this);

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
            OracleLog.driverLogger.log(Level.INFO, "OracleBlogInputStream.skip(n=" + n + ")", this);

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

                if ((this.lob instanceof BLOB))
                    remainLen = ((BLOB) this.lob).length() - this.lobOffset + 1L;
                else {
                    remainLen = ((BFILE) this.lob).length() - this.lobOffset + 1L;
                }
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
                    .forName("oracle.jdbc.driver.OracleBlobInputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleBlobInputStream JD-Core Version: 0.6.0
 */