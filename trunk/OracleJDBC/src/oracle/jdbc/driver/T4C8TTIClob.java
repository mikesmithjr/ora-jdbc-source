package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;

class T4C8TTIClob extends T4C8TTILob {
    int[] nBytes;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTIClob(T4CMAREngine mengine, T4CTTIoer _oer) {
        super(mengine, _oer);

        this.nBytes = new int[1];
    }

    long read(byte[] lobLocator, long offset, long numChars, boolean isNCLOB, char[] outBuffer)
            throws SQLException, IOException {
        long bytesRead = 0L;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIClob.read. lobLocator is "
                    + lobLocator + ". offset is " + offset + ". numChars is " + numChars
                    + ". outBuffer is " + outBuffer, this);

            OracleLog.recursiveTrace = false;
        }

        byte[] myBuffer = null;

        initializeLobdef();

        if ((lobLocator[6] & 0x80) == 128) {
            this.varWidthChar = true;
        }
        if (this.varWidthChar == true)
            myBuffer = new byte[(int) numChars * 2];
        else {
            myBuffer = new byte[(int) numChars * 3];
        }

        if ((lobLocator[7] & 0x40) > 0) {
            this.littleEndianClob = true;
        }

        this.lobops = 2L;
        this.sourceLobLocator = lobLocator;
        this.sourceOffset = offset;
        this.lobamt = numChars;
        this.sendLobamt = true;
        this.outBuffer = myBuffer;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        long charsRead = this.lobamt;

        long bytesConverted = 0L;

        if (this.varWidthChar == true) {
            if (this.meg.versionNumber < 10101) {
                DBConversion.ucs2BytesToJavaChars(myBuffer, myBuffer.length, outBuffer);
            } else if (this.littleEndianClob) {
                CharacterSet.convertAL16UTF16LEBytesToJavaChars(myBuffer, 0, outBuffer, 0,
                                                                (int) this.lobBytesRead, true);
            } else {
                CharacterSet.convertAL16UTF16BytesToJavaChars(myBuffer, 0, outBuffer, 0,
                                                              (int) this.lobBytesRead, true);
            }

        } else if (!isNCLOB) {
            this.nBytes[0] = (int) this.lobBytesRead;

            this.meg.conv.CHARBytesToJavaChars(myBuffer, 0, outBuffer, 0, this.nBytes,
                                               outBuffer.length);
        } else {
            this.nBytes[0] = (int) this.lobBytesRead;

            this.meg.conv.NCHARBytesToJavaChars(myBuffer, 0, outBuffer, 0, this.nBytes,
                                                outBuffer.length);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIClob.read -- lobBytesRead is "
                    + this.lobBytesRead, this);

            OracleLog.recursiveTrace = false;
        }

        return charsRead;
    }

    long write(byte[] lobLocator, long offset, boolean isNCLOB, char[] inBuffer,
            long offsetInBuffer, long numChars) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIClob.write. lobLocator is "
                    + lobLocator + ". offset is " + offset + ". numChars is " + numChars
                    + ". inBuffer is " + inBuffer, this);

            OracleLog.recursiveTrace = false;
        }

        boolean varChar = false;

        if ((lobLocator[6] & 0x80) == 128) {
            varChar = true;
        }
        if ((lobLocator[7] & 0x40) == 64) {
            this.littleEndianClob = true;
        }

        long bytesConverted = 0L;
        byte[] myBuffer = null;

        if (varChar == true) {
            myBuffer = new byte[(int) numChars * 2];

            if (this.meg.versionNumber < 10101) {
                DBConversion.javaCharsToUcs2Bytes(inBuffer, (int) offsetInBuffer, myBuffer, 0,
                                                  (int) numChars);
            } else if (this.littleEndianClob) {
                CharacterSet.convertJavaCharsToAL16UTF16LEBytes(inBuffer, (int) offsetInBuffer,
                                                                myBuffer, 0, (int) numChars);
            } else {
                CharacterSet.convertJavaCharsToAL16UTF16Bytes(inBuffer, (int) offsetInBuffer,
                                                              myBuffer, 0, (int) numChars);
            }

        } else {
            myBuffer = new byte[(int) numChars * 3];

            if (!isNCLOB) {
                bytesConverted = this.meg.conv.javaCharsToCHARBytes(inBuffer, (int) offsetInBuffer,
                                                                    myBuffer, 0, (int) numChars);
            } else {
                bytesConverted = this.meg.conv.javaCharsToNCHARBytes(inBuffer,
                                                                     (int) offsetInBuffer,
                                                                     myBuffer, 0, (int) numChars);
            }

        }

        initializeLobdef();

        this.lobops = 64L;
        this.sourceLobLocator = lobLocator;
        this.sourceOffset = offset;
        this.lobamt = numChars;
        this.sendLobamt = true;
        this.inBuffer = myBuffer;

        marshalFunHeader();

        marshalOlobops();

        if (varChar == true) {
            if (this.meg.versionNumber < 10101)
                this.lobd.marshalLobDataUB2(myBuffer, 0L, numChars);
            else {
                this.lobd.marshalLobData(myBuffer, 0L, numChars * 2L);
            }

        } else {
            this.lobd.marshalLobData(myBuffer, 0L, bytesConverted);
        }

        receiveReply();

        long charsWritten = this.lobamt;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting v8TTILob.write -- charsWritten is "
                    + charsWritten, this);

            OracleLog.recursiveTrace = false;
        }

        return charsWritten;
    }

    Datum createTemporaryLob(Connection conn, boolean cache, int duration) throws SQLException,
            IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Entering T4C8TTIClob.createTemporaryLob. cache is " + cache
                                             + ". duration is " + duration, this);

            OracleLog.recursiveTrace = false;
        }

        return createTemporaryLob(conn, cache, duration, (short) 1);
    }

    Datum createTemporaryLob(Connection conn, boolean cache, int duration, short form_of_use)
            throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Entering T4C8TTIClob.createTemporaryLob. cache is " + cache
                                             + ". duration is " + duration + ". form_of_use is "
                                             + form_of_use, this);

            OracleLog.recursiveTrace = false;
        }

        if (duration == 12) {
            DatabaseError.throwSqlException(158);
        }

        CLOB clob = null;

        initializeLobdef();

        this.lobops = 272L;
        this.sourceLobLocator = new byte[86];
        this.sourceLobLocator[1] = 84;

        this.lobamt = 10L;
        this.sendLobamt = true;

        if (form_of_use == 1)
            this.sourceOffset = 1L;
        else {
            this.sourceOffset = 2L;
        }

        this.destinationOffset = 112L;

        this.destinationLength = duration;

        this.nullO2U = true;

        this.characterSet = (form_of_use == 2 ? this.meg.conv.getNCharSetId() : this.meg.conv
                .getServerCharSetId());

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        if (this.sourceLobLocator != null) {
            clob = new CLOB((OracleConnection) conn, this.sourceLobLocator);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Exiting T4C8TTIClob.createTemporaryLob. oracle.sql.CLOB is "
                                             + clob, this);

            OracleLog.recursiveTrace = false;
        }

        return clob;
    }

    boolean open(byte[] lobLocator, int mode) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIClob.open. lobLocator is "
                    + lobLocator + ". mode is " + mode, this);

            OracleLog.recursiveTrace = false;
        }

        boolean wasOpened = false;

        int kokl_mode = 2;

        if (mode == 0) {
            kokl_mode = 1;
        }
        wasOpened = _open(lobLocator, kokl_mode, 32768);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIClob.open. wasOpened is "
                    + wasOpened, this);

            OracleLog.recursiveTrace = false;
        }

        return wasOpened;
    }

    boolean close(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIClob.close. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean wasClosed = false;

        wasClosed = _close(lobLocator, 65536);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIClob.close. wasClosed is "
                    + wasClosed, this);

            OracleLog.recursiveTrace = false;
        }

        return wasClosed;
    }

    boolean isOpen(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIClob.isOpen. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean open = false;

        open = _isOpen(lobLocator, 69632);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIClob.isOpen. open is " + open,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return open;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTIClob"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTIClob JD-Core Version: 0.6.0
 */