package oracle.jdbc.oracore;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

public final class UnpickleContext {
    byte[] image;
    int absoluteOffset;
    int beginOffset;
    int markedOffset;
    Vector patches;
    long[] ldsOffsets;
    boolean[] nullIndicators;
    boolean bigEndian;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public UnpickleContext() {
    }

    public UnpickleContext(byte[] image, int begin_offset, boolean[] null_bytes,
            long[] lds_offset_array, boolean big_endian) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "UnpickleContext (image " + image
                    + ", begin_offset" + begin_offset + ", null_bytes" + null_bytes
                    + ",lds_offset_array " + lds_offset_array + ", bigEndian" + this.bigEndian
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.image = image;
        this.beginOffset = begin_offset;
        this.absoluteOffset = begin_offset;
        this.bigEndian = big_endian;
        this.nullIndicators = null_bytes;
        this.patches = null;
        this.ldsOffsets = lds_offset_array;
    }

    public byte readByte() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_bytes()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            byte i = this.image[this.absoluteOffset];
            return i;
        } finally {
            this.absoluteOffset += 1;
        }
    }

    public byte[] readVarNumBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_varNumBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] varNumBytes = new byte[this.image[this.absoluteOffset] & 0xFF];
        try {
            System.arraycopy(this.image, this.absoluteOffset + 1, varNumBytes, 0,
                             varNumBytes.length);
        } finally {
            this.absoluteOffset += 22;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_varNumBytes:return "
                    + varNumBytes, this);

            OracleLog.recursiveTrace = false;
        }

        return varNumBytes;
    }

    public byte[] readPtrBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_ptrBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] bytes = new byte[(this.image[this.absoluteOffset] & 0xFF) * 256
                + (this.image[(this.absoluteOffset + 1)] & 0xFF) + 2];

        System.arraycopy(this.image, this.absoluteOffset, bytes, 0, bytes.length);

        this.absoluteOffset += bytes.length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_ptrBytes:return " + bytes,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return bytes;
    }

    public void skipPtrBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.skip_ptrBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        this.absoluteOffset += (this.image[this.absoluteOffset] & 0xFF) * 256
                + (this.image[(this.absoluteOffset + 1)] & 0xFF) + 2;
    }

    public byte[] readBytes(int n) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_bytes( n = " + n + " )",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        try {
            byte[] bytes = new byte[n];

            System.arraycopy(this.image, this.absoluteOffset, bytes, 0, n);

            byte[] arrayOfByte1 = bytes;
            return arrayOfByte1;
        } finally {
            this.absoluteOffset += n;
        }
    }

    public long readLong() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_long()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            long l = (((this.image[this.absoluteOffset] & 0xFF) * 256 + (this.image[(this.absoluteOffset + 1)] & 0xFF)) * 256 + (this.image[(this.absoluteOffset + 2)] & 0xFF))
                    * 256 + (this.image[(this.absoluteOffset + 3)] & 0xFF);
            return l;
        } finally {
            this.absoluteOffset += 4;
        }
    }

    public short readShort() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_short()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            short i = (short) ((this.image[this.absoluteOffset] & 0xFF) * 256 + (this.image[(this.absoluteOffset + 1)] & 0xFF));
            return i;
        } finally {
            this.absoluteOffset += 2;
        }
    }

    public byte[] readLengthBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.read_lengthBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        long variable_length = readLong();

        return readBytes((int) variable_length);
    }

    public void skipLengthBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.skip_lengthBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        long variable_length = readLong();

        this.absoluteOffset = (int) (this.absoluteOffset + variable_length);
    }

    public void skipTo(long offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.skip_to( offset = " + offset
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (offset > this.absoluteOffset - this.beginOffset)
            this.absoluteOffset = (this.beginOffset + (int) offset);
    }

    public void skipTo(int offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.skip_to( offset = " + offset
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (offset > this.absoluteOffset - this.beginOffset)
            this.absoluteOffset = (this.beginOffset + offset);
    }

    public void mark() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.mark()", this);

            OracleLog.recursiveTrace = false;
        }

        this.markedOffset = this.absoluteOffset;
    }

    public void reset() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.reset()", this);

            OracleLog.recursiveTrace = false;
        }

        this.absoluteOffset = this.markedOffset;
    }

    public void markAndSkip() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.markAndSkip()", this);

            OracleLog.recursiveTrace = false;
        }

        this.markedOffset = (this.absoluteOffset + 4);
        this.absoluteOffset = (this.beginOffset + (int) readLong());
    }

    public void markAndSkip(long offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.markAndSkip( offset = " + offset
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.markedOffset = this.absoluteOffset;
        this.absoluteOffset = (this.beginOffset + (int) offset);
    }

    public void skipBytes(int n) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.skip_bytes( n = " + n + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if (n >= 0)
            this.absoluteOffset += n;
    }

    public boolean isNull(int idx) {
        return this.nullIndicators[idx];
    }

    public int absoluteOffset() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.absolute_offset:return "
                    + this.absoluteOffset, this);

            OracleLog.recursiveTrace = false;
        }

        return this.absoluteOffset;
    }

    public int offset() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "UnpickleContext.offset:return "
                    + (this.absoluteOffset - this.beginOffset), this);

            OracleLog.recursiveTrace = false;
        }

        return this.absoluteOffset - this.beginOffset;
    }

    public byte[] image() throws SQLException {
        return this.image;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.UnpickleContext"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.UnpickleContext JD-Core Version: 0.6.0
 */