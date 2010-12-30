package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleConversionInputStream extends OracleBufferedStream {
    static final int CHUNK_SIZE = 4096;
    DBConversion converter;
    int conversion;
    InputStream istream;
    Reader reader;
    byte[] convbuf;
    char[] javaChars;
    int maxSize;
    int totalSize;
    int numUnconvertedBytes;
    boolean endOfStream;
    private short csform;
    int[] nbytes;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleConversionInputStream(DBConversion conv_object, InputStream istream, int conversion) {
        this(conv_object, istream, conversion, (short) 1);
    }

    public OracleConversionInputStream(DBConversion conv_object, InputStream istream,
            int conversion, short formOfUse) {
        super(4096);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionInputStream.OracleConversionInputStream(conv_object="
                                               + conv_object + ", istream=" + istream
                                               + ", conversion=" + conversion + ", formOfUse="
                                               + formOfUse + ") -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        this.istream = istream;
        this.conversion = conversion;
        this.converter = conv_object;
        this.maxSize = 0;
        this.totalSize = 0;
        this.numUnconvertedBytes = 0;
        this.endOfStream = false;
        this.nbytes = new int[1];
        this.csform = formOfUse;

        switch (conversion) {
        case 0:
            this.javaChars = new char[4096];
            this.convbuf = new byte[4096];

            break;
        case 1:
            this.convbuf = new byte[2048];
            this.javaChars = new char[2048];

            break;
        case 2:
            this.convbuf = new byte[2048];
            this.javaChars = new char[4096];

            break;
        case 3:
            this.convbuf = new byte[1024];
            this.javaChars = new char[2048];

            break;
        case 4: {
            int size = 4096 / this.converter.getMaxCharbyteSize();

            this.convbuf = new byte[size * 2];
            this.javaChars = new char[size];

            break;
        }
        case 5: {
            if (this.converter.isUcs2CharSet()) {
                this.convbuf = new byte[2048];
                this.javaChars = new char[2048];
            } else {
                this.convbuf = new byte[4096];
                this.javaChars = new char[4096];
            }

            break;
        }
        case 7: {
            int size = 4096 / (formOfUse == 2 ? this.converter.getMaxNCharbyteSize()
                    : this.converter.getMaxCharbyteSize());

            this.javaChars = new char[size];

            break;
        }
        case 6:
        default:
            this.convbuf = new byte[4096];
            this.javaChars = new char[4096];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleConversionInputStream.OracleConversionInputStream: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleConversionInputStream(DBConversion conv_object, InputStream istream,
            int conversion, int max_bytesize) {
        this(conv_object, istream, conversion, (short) 1);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionInputStream.OracleConversionInputStream(conv_object="
                                               + conv_object + ", istream=" + istream
                                               + ", conversion=" + conversion + ", max_bytesize="
                                               + max_bytesize + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }

        this.maxSize = max_bytesize;
        this.totalSize = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleConversionInputStream.OracleConversionInputStream: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleConversionInputStream(DBConversion conv_object, Reader reader, int conversion,
            int max_charsize, short formOfUse) {
        this(conv_object, (InputStream) null, conversion, formOfUse);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionInputStream.OracleConversionInputStream(conv_object="
                                               + conv_object + ", reader=" + reader
                                               + ", conversion=" + conversion + ", max_charsize="
                                               + max_charsize + ") -- after this()", this);

            OracleLog.recursiveTrace = false;
        }

        this.reader = reader;
        this.maxSize = max_charsize;
        this.totalSize = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleConversionInputStream.OracleConversionInputStream: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setFormOfUse(short csform) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionInputStream.setFormOfUse(csform=" + csform
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.csform = csform;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConversionInputStream.setFormOfUse: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean needBytes() throws IOException {
        if (this.closed) {
            return false;
        }

        if (this.pos < this.count) {
            return true;
        }
        if (this.istream != null) {
            return needBytesFromStream();
        }
        if (this.reader != null) {
            return needBytesFromReader();
        }

        return false;
    }

    public boolean needBytesFromReader() throws IOException {
        try {
            int read_size = 0;

            if (this.maxSize == 0) {
                read_size = this.javaChars.length;
            } else {
                read_size = Math.min(this.maxSize - this.totalSize, this.javaChars.length);
            }

            if (read_size <= 0) {
                this.reader.close();
                close();

                return false;
            }

            int chars_read = this.reader.read(this.javaChars, 0, read_size);

            if (chars_read == -1) {
                this.reader.close();
                close();

                return false;
            }

            this.totalSize += chars_read;

            switch (this.conversion) {
            case 7:
                if (this.csform == 2)
                    this.count = this.converter.javaCharsToNCHARBytes(this.javaChars, chars_read,
                                                                      this.buf);
                else {
                    this.count = this.converter.javaCharsToCHARBytes(this.javaChars, chars_read,
                                                                     this.buf);
                }

                break;
            default:
                System.arraycopy(this.convbuf, 0, this.buf, 0, chars_read);

                this.count = chars_read;
            }

        } catch (SQLException e) {
            DatabaseError.SQLToIOException(e);
        }

        this.pos = 0;

        return true;
    }

    public boolean needBytesFromStream() throws IOException {
        if (!this.endOfStream) {
            try {
                int read_size = 0;

                if (this.maxSize == 0) {
                    read_size = this.convbuf.length;
                } else {
                    read_size = Math.min(this.maxSize - this.totalSize, this.convbuf.length);
                }

                int bytes_read = 0;

                if (read_size <= 0) {
                    this.endOfStream = true;

                    this.istream.close();

                    if (this.numUnconvertedBytes != 0) {
                        DatabaseError.throwSqlException(55);
                    }

                } else {
                    bytes_read = this.istream.read(this.convbuf, this.numUnconvertedBytes,
                                                   read_size - this.numUnconvertedBytes);

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.FINER, OracleLog
                                .bytesToPrintableForm("Read " + bytes_read + " bytes into convbuf",
                                                      this.convbuf, bytes_read), this);

                        OracleLog.recursiveTrace = false;
                    }

                }

                if (bytes_read == -1) {
                    this.endOfStream = true;

                    this.istream.close();

                    if (this.numUnconvertedBytes != 0)
                        DatabaseError.throwSqlException(55);
                } else {
                    bytes_read += this.numUnconvertedBytes;
                    this.totalSize += bytes_read;
                }

                if (bytes_read <= 0) {
                    return false;
                }

                switch (this.conversion) {
                case 0: {
                    this.nbytes[0] = bytes_read;

                    int chars_read = this.converter.CHARBytesToJavaChars(this.convbuf, 0,
                                                                         this.javaChars, 0,
                                                                         this.nbytes,
                                                                         this.javaChars.length);

                    this.numUnconvertedBytes = this.nbytes[0];

                    for (int i = 0; i < this.numUnconvertedBytes; i++) {
                        this.convbuf[i] = this.convbuf[(bytes_read - this.numUnconvertedBytes)];
                    }

                    this.count = DBConversion.javaCharsToAsciiBytes(this.javaChars, chars_read,
                                                                    this.buf);

                    break;
                }
                case 1: {
                    this.nbytes[0] = bytes_read;

                    int chars_read = this.converter.CHARBytesToJavaChars(this.convbuf, 0,
                                                                         this.javaChars, 0,
                                                                         this.nbytes,
                                                                         this.javaChars.length);

                    this.numUnconvertedBytes = this.nbytes[0];

                    for (int i = 0; i < this.numUnconvertedBytes; i++) {
                        this.convbuf[i] = this.convbuf[(bytes_read - this.numUnconvertedBytes)];
                    }

                    this.count = DBConversion.javaCharsToUcs2Bytes(this.javaChars, chars_read,
                                                                   this.buf);

                    break;
                }
                case 2: {
                    int chars_read = DBConversion.RAWBytesToHexChars(this.convbuf, bytes_read,
                                                                     this.javaChars);

                    this.count = DBConversion.javaCharsToAsciiBytes(this.javaChars, chars_read,
                                                                    this.buf);

                    break;
                }
                case 3: {
                    int chars_read = DBConversion.RAWBytesToHexChars(this.convbuf, bytes_read,
                                                                     this.javaChars);

                    this.count = DBConversion.javaCharsToUcs2Bytes(this.javaChars, chars_read,
                                                                   this.buf);

                    break;
                }
                case 4: {
                    int chars_read = DBConversion.ucs2BytesToJavaChars(this.convbuf, bytes_read,
                                                                       this.javaChars);

                    this.count = this.converter.javaCharsToCHARBytes(this.javaChars, chars_read,
                                                                     this.buf);

                    break;
                }
                case 5: {
                    DBConversion.asciiBytesToJavaChars(this.convbuf, bytes_read, this.javaChars);

                    this.count = this.converter.javaCharsToCHARBytes(this.javaChars, bytes_read,
                                                                     this.buf);

                    break;
                }
                default:
                    System.arraycopy(this.convbuf, 0, this.buf, 0, bytes_read);

                    this.count = bytes_read;
                }

            } catch (SQLException e) {
                DatabaseError.SQLToIOException(e);
            }

            this.pos = 0;

            return true;
        }

        return false;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleConversionInputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleConversionInputStream JD-Core Version: 0.6.0
 */