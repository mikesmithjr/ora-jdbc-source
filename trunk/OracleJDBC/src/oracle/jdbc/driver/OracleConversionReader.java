package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleConversionReader extends Reader {
    static final int CHUNK_SIZE = 4096;
    DBConversion dbConversion;
    int conversion;
    InputStream istream;
    char[] buf;
    byte[] byteBuf;
    int pos;
    int count;
    int numUnconvertedBytes;
    boolean isClosed;
    boolean endOfStream;
    private short csform;
    int[] nbytes;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleConversionReader(DBConversion conv_object, InputStream istream, int conversion)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionReader.OracleConversionReader(conv_object="
                                               + conv_object + ", istream=" + istream
                                               + ", conversion=" + conversion
                                               + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((conv_object == null) || (istream == null) || ((conversion != 8) && (conversion != 9))) {
            DatabaseError.throwSqlException(68);
        }
        this.dbConversion = conv_object;
        this.conversion = conversion;
        this.istream = istream;
        this.pos = (this.count = 0);
        this.numUnconvertedBytes = 0;

        this.isClosed = false;
        this.nbytes = new int[1];

        if (conversion == 8) {
            this.byteBuf = new byte[2048];
            this.buf = new char[4096];
        } else if (conversion == 9) {
            this.byteBuf = new byte[4096];
            this.buf = new char[4096];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionReader.OracleConversionReader: return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setFormOfUse(short csform) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleConversionReader.setFormOfUse(csform="
                    + csform + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.csform = csform;
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConversionReader.read(csform="
                    + this.csform + ")", this);

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
                    int bytes_read = this.istream.read(this.byteBuf, this.numUnconvertedBytes,
                                                       this.byteBuf.length
                                                               - this.numUnconvertedBytes);

                    if (bytes_read == -1) {
                        this.endOfStream = true;

                        this.istream.close();

                        if (this.numUnconvertedBytes != 0) {
                            DatabaseError.throwSqlException(55);
                        }
                    }
                    bytes_read += this.numUnconvertedBytes;

                    if (bytes_read > 0) {
                        switch (this.conversion) {
                        case 8:
                            this.count = DBConversion.RAWBytesToHexChars(this.byteBuf, bytes_read,
                                                                         this.buf);

                            break;
                        case 9:
                            this.nbytes[0] = bytes_read;

                            if (this.csform == 2) {
                                this.count = this.dbConversion
                                        .NCHARBytesToJavaChars(this.byteBuf, 0, this.buf, 0,
                                                               this.nbytes, this.buf.length);
                            } else {
                                this.count = this.dbConversion
                                        .CHARBytesToJavaChars(this.byteBuf, 0, this.buf, 0,
                                                              this.nbytes, this.buf.length);
                            }

                            this.numUnconvertedBytes = this.nbytes[0];

                            for (int i = 0; i < this.numUnconvertedBytes; i++) {
                                this.byteBuf[i] = this.byteBuf[(bytes_read
                                        - this.numUnconvertedBytes + i)];
                            }
                            break;
                        default:
                            DatabaseError.throwSqlException(23);
                        }

                        if (this.count > 0) {
                            this.pos = 0;

                            return true;
                        }

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
        int availableLength = Math.min(length, this.count - this.pos);

        System.arraycopy(this.buf, this.pos, destbuf, offset, availableLength);

        this.pos += availableLength;

        return availableLength;
    }

    public boolean ready() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConversionReader.ready()", this);

            OracleLog.recursiveTrace = false;
        }

        ensureOpen();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConversionReader.ready: return: "
                    + (this.pos < this.count), this);

            OracleLog.recursiveTrace = false;
        }

        return this.pos < this.count;
    }

    public void close() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConversionReader.close(): isClosed="
                    + this.isClosed, this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.isClosed) {
            this.isClosed = true;

            this.istream.close();
        }
    }

    void ensureOpen() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConversionReader.ensureOpen(): isClosed="
                    + this.isClosed, this);

            OracleLog.recursiveTrace = false;
        }

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
                    .forName("oracle.jdbc.driver.OracleConversionReader"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleConversionReader JD-Core Version: 0.6.0
 */