package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public final class PickleContext {
    private PickleOutputStream outStream;
    byte[] image;
    int imageOffset;
    private byte[] lengthBuffer;
    static short KOPI20_LN_ELNL = 255;
    static short KOPI20_LN_5BLN = 254;
    static short KOPI20_LN_ATMN = 253;
    static short KOPI20_LN_IEMN = 252;
    static short KOPI20_LN_MAXV = 245;

    static short KOPI20_IF_IS81 = 128;
    static short KOPI20_IF_CMSB = 64;
    static short KOPI20_IF_CLSB = 32;
    static short KOPI20_IF_DEGN = 16;
    static short KOPI20_IF_COLL = 8;
    static short KOPI20_IF_NOPS = 4;
    static short KOPI20_IF_ANY = 2;
    static short KOPI20_IF_NONL = 1;

    static short KOPI20_CF_CMSB = 64;
    static short KOPI20_CF_CLSB = 32;
    static short KOPI20_CF_INDX = 16;
    static short KOPI20_CF_NOLN = 8;

    static short KOPI20_VERSION = 1;
    static final byte KOPUP_INLINE_COLL = 1;
    static final byte KOPUP_TYPEINFO_NONE = 0;
    static final byte KOPUP_TYPEINFO_TOID = 4;
    static final byte KOPUP_TYPEINFO_TOBJN = 8;
    static final byte KOPUP_TYPEINFO_TDS = 12;
    static final byte KOPUP_VSN_PRESENT = 16;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public PickleContext() {
        this.lengthBuffer = new byte[5];
    }

    public PickleContext(byte[] pickled_bytes) {
        this.lengthBuffer = new byte[5];
        this.image = pickled_bytes;
        this.imageOffset = 0;
    }

    public PickleContext(byte[] pickled_bytes, long offset) {
        this.lengthBuffer = new byte[5];
        this.image = pickled_bytes;
        this.imageOffset = (int) offset;
    }

    public void initStream(int imglen) {
        this.outStream = new PickleOutputStream(imglen);
    }

    public void initStream() {
        this.outStream = new PickleOutputStream();
    }

    public int lengthInBytes(int v) {
        return v <= KOPI20_LN_MAXV ? 1 : 5;
    }

    public int writeElementNull() throws SQLException {
        this.outStream.write(KOPI20_LN_ELNL);

        return 1;
    }

    public int writeAtomicNull() throws SQLException {
        this.outStream.write(KOPI20_LN_ATMN);

        return 1;
    }

    public int writeImmediatelyEmbeddedElementNull(byte null_adtno) throws SQLException {
        this.lengthBuffer[0] = (byte) KOPI20_LN_IEMN;
        this.lengthBuffer[1] = null_adtno;

        this.outStream.write(this.lengthBuffer, 0, 2);

        return 2;
    }

    public int writeLength(int len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.writeLength_pctx (" + len + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if (len <= KOPI20_LN_MAXV) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger
                        .log(Level.FINEST, "OracleTypeADT.writeLength_pctx 1 byte length format",
                             this);

                OracleLog.recursiveTrace = false;
            }

            this.outStream.write((byte) len);

            return 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeADT.writeLength_pctx 5 byte length format", this);

            OracleLog.recursiveTrace = false;
        }

        this.lengthBuffer[0] = (byte) KOPI20_LN_5BLN;
        this.lengthBuffer[1] = (byte) (len >> 24);
        len &= 16777215;
        this.lengthBuffer[2] = (byte) (len >> 16);
        len &= 65535;
        this.lengthBuffer[3] = (byte) (len >> 8);
        len &= 255;
        this.lengthBuffer[4] = (byte) len;
        try {
            this.outStream.write(this.lengthBuffer);
        } catch (IOException ex) {
            if (TRACE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                ex.printStackTrace(pw);
                OracleLog.print(this, 16, 32, 64, "OracleTypeADT.writeLength_pctx " + ex.toString()
                        + sw.toString());
            }

            DatabaseError.throwSqlException(ex);
        }

        return 5;
    }

    public int writeLength(int datalen, boolean include) throws SQLException {
        if (!include) {
            return writeLength(datalen);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeADT.writeLength_pctx(" + datalen + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (datalen <= KOPI20_LN_MAXV - 1) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger
                        .log(Level.FINEST, "OracleTypeADT.writeLength_pctx 1 byte length format",
                             this);

                OracleLog.recursiveTrace = false;
            }

            this.outStream.write((byte) datalen + 1);

            return 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeADT.writeLength_pctx 5 byte length format", this);

            OracleLog.recursiveTrace = false;
        }

        datalen += 5;
        this.lengthBuffer[0] = (byte) KOPI20_LN_5BLN;
        this.lengthBuffer[1] = (byte) (datalen >> 24);
        datalen &= 16777215;
        this.lengthBuffer[2] = (byte) (datalen >> 16);
        datalen &= 65535;
        this.lengthBuffer[3] = (byte) (datalen >> 8);
        datalen &= 255;
        this.lengthBuffer[4] = (byte) datalen;
        try {
            this.outStream.write(this.lengthBuffer);
        } catch (IOException ex) {
            if (TRACE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                ex.printStackTrace(pw);
                OracleLog.print(this, 16, 32, 64, "OracleTypeADT.writeLength_pctx " + ex.toString()
                        + sw.toString());
            }

            DatabaseError.throwSqlException(ex);
        }

        return 5;
    }

    public byte[] to5bLengthBytes_pctx(int len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.to5bLengthBytes_pct(" + len
                    + ")5 byte length format", this);

            OracleLog.recursiveTrace = false;
        }

        this.lengthBuffer[0] = (byte) KOPI20_LN_5BLN;
        this.lengthBuffer[1] = (byte) (len >> 24);
        len &= 16777215;
        this.lengthBuffer[2] = (byte) (len >> 16);
        len &= 65535;
        this.lengthBuffer[3] = (byte) (len >> 8);
        len &= 255;
        this.lengthBuffer[4] = (byte) len;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.to5bLengthBytes_pct:return "
                    + this.lengthBuffer, this);

            OracleLog.recursiveTrace = false;
        }

        return this.lengthBuffer;
    }

    public int writeData(byte b) throws SQLException {
        this.outStream.write(b);

        return 1;
    }

    public int writeData(byte[] b) throws SQLException {
        try {
            this.outStream.write(b);
        } catch (IOException ex) {
            if (TRACE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                ex.printStackTrace(pw);
                OracleLog.print(this, 16, 32, 64, "OracleTypeADT.write_data_pctx" + ex.toString()
                        + sw.toString());
            }

            DatabaseError.throwSqlException(ex);
        }

        return b.length;
    }

    public void patchImageLen(int offset, int image_length) throws SQLException {
        byte[] lenbuf = to5bLengthBytes_pctx(image_length);

        this.outStream.overwrite(offset, lenbuf, 0, lenbuf.length);
    }

    public int writeImageHeader(boolean withPrefix) throws SQLException {
        return writeImageHeader(KOPI20_LN_MAXV + 1, withPrefix);
    }

    public int writeOpaqueImageHeader(int data_length) throws SQLException {
        int count = 2;

        this.lengthBuffer[0] = (byte) (KOPI20_IF_IS81 | KOPI20_IF_NOPS | KOPI20_IF_NONL);
        this.lengthBuffer[1] = (byte) KOPI20_VERSION;

        this.outStream.write(this.lengthBuffer, 0, 2);

        count += writeLength(data_length + 2, true);

        return count;
    }

    public int writeImageHeader(int image_length, boolean withPrefix) throws SQLException {
        int count = 2;

        if (withPrefix)
            this.lengthBuffer[0] = (byte) KOPI20_IF_IS81;
        else {
            this.lengthBuffer[0] = (byte) (KOPI20_IF_IS81 | KOPI20_IF_NOPS);
        }
        this.lengthBuffer[1] = (byte) KOPI20_VERSION;

        this.outStream.write(this.lengthBuffer, 0, 2);

        count += writeLength(image_length);

        return count;
    }

    public int writeCollImageHeader(int num_collection_items) throws SQLException {
        return writeCollImageHeader(KOPI20_LN_MAXV + 1, num_collection_items);
    }

    public int writeCollImageHeader(int image_length, int num_collection_items) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeADT.WriteCollImageHeader_pctx( length = "
                                            + image_length + ", number of items ="
                                            + num_collection_items + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int count = 5;

        this.lengthBuffer[0] = (byte) (KOPI20_IF_IS81 | KOPI20_IF_COLL);
        this.lengthBuffer[1] = (byte) KOPI20_VERSION;

        this.outStream.write(this.lengthBuffer, 0, 2);

        count += writeLength(image_length);
        this.lengthBuffer[0] = 1;
        this.lengthBuffer[1] = 1;

        this.outStream.write(this.lengthBuffer, 0, 2);

        this.lengthBuffer[0] = 0;

        this.outStream.write(this.lengthBuffer, 0, 1);

        count += writeLength(num_collection_items);

        return count;
    }

    public int writeCollImageHeader(byte[] prefix_segment) throws SQLException {
        return writeCollImageHeader(KOPI20_LN_MAXV + 1, prefix_segment);
    }

    public int writeCollImageHeader(int image_length, byte[] prefix_segment) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeADT.writeCollImageHeader_pctx( length = "
                                            + image_length + ", bytes =" + prefix_segment + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        int psegLen = prefix_segment.length;

        int count = 3 + psegLen;

        this.lengthBuffer[0] = (byte) (KOPI20_IF_IS81 | KOPI20_IF_DEGN);
        this.lengthBuffer[1] = (byte) KOPI20_VERSION;

        this.outStream.write(this.lengthBuffer, 0, 2);

        count += writeLength(image_length);
        count += writeLength(psegLen + 1);

        this.lengthBuffer[0] = 0;

        this.outStream.write(this.lengthBuffer, 0, 1);
        this.outStream.write(prefix_segment, 0, psegLen);

        return count;
    }

    public byte[] stream2Bytes() throws SQLException {
        return this.outStream.toByteArray();
    }

    public byte readByte() throws SQLException {
        try {
            byte i = this.image[this.imageOffset];
            return i;
        } finally {
            this.imageOffset += 1;
        }
    }

    public boolean readAndCheckVersion() throws SQLException {
        try {
            boolean i = (this.image[this.imageOffset] & 0xFF) <= KOPI20_VERSION ? true : false;
            return i;
        } finally {
            this.imageOffset += 1;
        }
    }

    public int readLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readLength_pctx()", this);

            OracleLog.recursiveTrace = false;
        }

        int len = this.image[this.imageOffset] & 0xFF;

        if (len > KOPI20_LN_MAXV) {
            if (len == KOPI20_LN_ELNL) {
                DatabaseError.throwSqlException(1, "Invalid null flag read");
            }

            len = (((this.image[(this.imageOffset + 1)] & 0xFF) * 256 + (this.image[(this.imageOffset + 2)] & 0xFF)) * 256 + (this.image[(this.imageOffset + 3)] & 0xFF))
                    * 256 + (this.image[(this.imageOffset + 4)] & 0xFF);

            this.imageOffset += 5;
        } else {
            this.imageOffset += 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readLength_pctx:return " + len,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return len;
    }

    public void skipLength() throws SQLException {
        int len = this.image[this.imageOffset] & 0xFF;

        if (len > KOPI20_LN_MAXV)
            this.imageOffset += 5;
        else
            this.imageOffset += 1;
    }

    public int readRestOfLength(byte len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readRestOfLength_pctx( length = "
                    + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((len & 0xFF) != KOPI20_LN_5BLN) {
            return len & 0xFF;
        }
        try {
            int i = (((this.image[this.imageOffset] & 0xFF) * 256 + (this.image[(this.imageOffset + 1)] & 0xFF)) * 256 + (this.image[(this.imageOffset + 2)] & 0xFF))
                    * 256 + (this.image[(this.imageOffset + 3)] & 0xFF);
            return i;
        } finally {
            this.imageOffset += 4;
        }
    }

    public void skipRestOfLength(byte len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.skipRestOfLength_pctx( length = "
                    + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((len & 0xFF) > KOPI20_LN_MAXV) {
            if ((len & 0xFF) == KOPI20_LN_5BLN)
                this.imageOffset += 4;
            else
                DatabaseError.throwSqlException(1, "Invalid first length byte");
        }
    }

    public int readLength(boolean exclude) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.reaLength_pctx( exclude = "
                    + exclude + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int len = this.image[this.imageOffset] & 0xFF;

        if (len > KOPI20_LN_MAXV) {
            len = (((this.image[(this.imageOffset + 1)] & 0xFF) * 256 + (this.image[(this.imageOffset + 2)] & 0xFF)) * 256 + (this.image[(this.imageOffset + 3)] & 0xFF))
                    * 256 + (this.image[(this.imageOffset + 4)] & 0xFF);

            if (exclude) {
                len -= 5;
            }
            this.imageOffset += 5;
        } else {
            if (exclude) {
                len--;
            }
            this.imageOffset += 1;
        }

        return len;
    }

    public byte[] readPrefixSegment() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readPrefixSegment_pctx()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] b = new byte[readLength()];

        System.arraycopy(this.image, this.imageOffset, b, 0, b.length);

        this.imageOffset += b.length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readPrefixSegment_pctx:return "
                    + b, this);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public byte[] readDataValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readDataValue_pctx()", this);

            OracleLog.recursiveTrace = false;
        }

        int len = this.image[this.imageOffset] & 0xFF;

        if (len == KOPI20_LN_ELNL) {
            this.imageOffset += 1;

            return null;
        }

        if (len > KOPI20_LN_MAXV) {
            len = (((this.image[(this.imageOffset + 1)] & 0xFF) * 256 + (this.image[(this.imageOffset + 2)] & 0xFF)) * 256 + (this.image[(this.imageOffset + 3)] & 0xFF))
                    * 256 + (this.image[(this.imageOffset + 4)] & 0xFF);

            this.imageOffset += 5;
        } else {
            this.imageOffset += 1;
        }

        byte[] b = new byte[len];

        System.arraycopy(this.image, this.imageOffset, b, 0, b.length);

        this.imageOffset += b.length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readDataValue_pctx:return " + b,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public byte[] readBytes(int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readBytes_pctx( length = "
                    + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] b = new byte[length];

        System.arraycopy(this.image, this.imageOffset, b, 0, length);

        this.imageOffset += length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readBytes_pctx:return " + b, this);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public byte[] read1ByteDataValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.read1byteDataValue_pctx()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.image[this.imageOffset] & 0xFF) == KOPI20_LN_ELNL) {
            return null;
        }

        byte[] b = new byte[this.image[this.imageOffset] & 0xFF];

        System.arraycopy(this.image, this.imageOffset + 1, b, 0, b.length);

        this.imageOffset += b.length + 1;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.read1byteDataValue_pctx:return "
                    + b, this);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public byte[] readDataValue(byte byte1) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeADT.readDataValue_pctx( length byte = " + byte1
                                            + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] b = new byte[readRestOfLength(byte1)];

        System.arraycopy(this.image, this.imageOffset, b, 0, b.length);

        this.imageOffset += b.length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readDataValue_pctx:return " + b,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public byte[] readDataValue(int len) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readDataValue_pctx( length  = "
                    + len + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] b = new byte[len];

        System.arraycopy(this.image, this.imageOffset, b, 0, len);

        this.imageOffset += len;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.readDataValue_pctx:return " + b,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public void skipDataValue() throws SQLException {
        if ((this.image[this.imageOffset] & 0xFF) == KOPI20_LN_ELNL)
            this.imageOffset += 1;
        else
            skipBytes(readLength());
    }

    public void skipDataValue(byte b) throws SQLException {
        skipBytes(readRestOfLength(b));
    }

    public void skipBytes(int b) throws SQLException {
        if (b > 0)
            this.imageOffset += b;
    }

    public int offset() throws SQLException {
        if (this.outStream != null) {
            return this.outStream.offset();
        }
        return this.imageOffset;
    }

    public int absoluteOffset() throws SQLException {
        return this.imageOffset;
    }

    public void skipTo(long offset) throws SQLException {
        if (offset > this.imageOffset)
            this.imageOffset = (int) offset;
    }

    public byte[] image() throws SQLException {
        return this.image;
    }

    public static boolean is81format(byte flag) throws SQLException {
        return (flag & 0xFF & KOPI20_IF_IS81) != 0;
    }

    public static boolean isCollectionImage_pctx(byte flag) throws SQLException {
        return (flag & 0xFF & KOPI20_IF_COLL) != 0;
    }

    public static boolean isDegenerateImage_pctx(byte flag) throws SQLException {
        return (flag & 0xFF & KOPI20_IF_DEGN) != 0;
    }

    public static boolean hasPrefix(byte flag) throws SQLException {
        return (flag & 0xFF & KOPI20_IF_NOPS) == 0;
    }

    public static boolean isAtomicNull(byte flag) throws SQLException {
        return (flag & 0xFF) == KOPI20_LN_ATMN;
    }

    public static boolean isImmediatelyEmbeddedNull(byte flag) throws SQLException {
        return (flag & 0xFF) == KOPI20_LN_IEMN;
    }

    public static boolean isElementNull(byte flag) throws SQLException {
        return (flag & 0xFF) == KOPI20_LN_ELNL;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.PickleContext"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.PickleContext JD-Core Version: 0.6.0
 */