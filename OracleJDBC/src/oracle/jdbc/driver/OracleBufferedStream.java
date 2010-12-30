package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleBufferedStream extends InputStream {
    byte[] buf;
    int pos;
    int count;
    boolean closed;
    int chunkSize;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public OracleBufferedStream(int chunkSize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleBufferedStream.OracleBufferedStream("
                    + chunkSize + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.pos = 0;
        this.count = 0;
        this.closed = false;
        this.chunkSize = chunkSize;
        this.buf = new byte[chunkSize];

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleBufferedStream.OracleBufferedStream:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void close() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleBufferedStream.close()", this);

            OracleLog.recursiveTrace = false;
        }

        this.closed = true;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleBufferedStream.close():return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean needBytes() throws IOException {
        throw new IOException("You should not call this method");
    }

    public int flushBytes(int n) {
        int availableLength = n > this.count - this.pos ? this.count - this.pos : n;

        this.pos += availableLength;

        return availableLength;
    }

    public int writeBytes(byte[] destbuf, int offset, int length) {
        int availableLength = length > this.count - this.pos ? this.count - this.pos : length;

        System.arraycopy(this.buf, this.pos, destbuf, offset, availableLength);

        this.pos += availableLength;

        return availableLength;
    }

    public synchronized int read() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBufferedStream.read()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.closed) || (isNull())) {
            return -1;
        }
        if (needBytes()) {
            return this.buf[(this.pos++)] & 0xFF;
        }
        return -1;
    }

    public int read(byte[] b) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBufferedStream.read(b)", this);

            OracleLog.recursiveTrace = false;
        }

        return read(b, 0, b.length);
    }

    public synchronized int read(byte[] destbuf, int offset, int length) throws IOException {
        int start = offset;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBufferedStream.read(destbuf, offset="
                    + offset + ", length=" + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.closed) || (isNull()))
            return -1;
        int end;
        if (length > destbuf.length)
            end = start + destbuf.length;
        else {
            end = start + length;
        }
        if (!needBytes()) {
            return -1;
        }
        start += writeBytes(destbuf, start, end - start);

        while ((start < end) && (needBytes())) {
            start += writeBytes(destbuf, start, end - start);
        }

        return start - offset;
    }

    public int available() throws IOException {
        if ((this.closed) || (isNull())) {
            return 0;
        }
        return this.count - this.pos;
    }

    public boolean isNull() throws IOException {
        return false;
    }

    public synchronized void mark(int readlimit) {
    }

    public synchronized void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public boolean markSupported() {
        return false;
    }

    public long skip(int n) throws IOException {
        int start = 0;
        int end = n;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleBufferedStream.skip(n=" + n + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.closed) || (isNull())) {
            return -1L;
        }
        if (!needBytes()) {
            return -1L;
        }
        while ((start < end) && (needBytes())) {
            start += flushBytes(end - start);
        }

        return start;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleBufferedStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleBufferedStream JD-Core Version: 0.6.0
 */