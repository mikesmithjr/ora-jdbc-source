package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;
import oracle.sql.Datum;

abstract class T4C8TTILob extends T4CTTIfun {
    static final int KPLOB_READ = 2;
    static final int KPLOB_WRITE = 64;
    static final int KPLOB_WRITE_APPEND = 8192;
    static final int KPLOB_PAGE_SIZE = 16384;
    static final int KPLOB_FILE_OPEN = 256;
    static final int KPLOB_FILE_ISOPEN = 1024;
    static final int KPLOB_FILE_EXISTS = 2048;
    static final int KPLOB_FILE_CLOSE = 512;
    static final int KPLOB_OPEN = 32768;
    static final int KPLOB_CLOSE = 65536;
    static final int KPLOB_ISOPEN = 69632;
    static final int KPLOB_TMP_CREATE = 272;
    static final int KPLOB_TMP_FREE = 273;
    static final int KPLOB_GET_LEN = 1;
    static final int KPLOB_TRIM = 32;
    static final int KOKL_ORDONLY = 1;
    static final int KOKL_ORDWR = 2;
    static final int KOLF_ORDONLY = 11;
    static final byte KOLBLOPEN = 8;
    static final byte KOLBLTMP = 1;
    static final byte KOLBLRDWR = 16;
    static final byte KOLBLABS = 64;
    static final byte ALLFLAGS = -1;
    static final byte KOLBLFLGB = 4;
    static final byte KOLLFLG = 4;
    static final byte KOLL3FLG = 7;
    static final byte KOLBLVLE = 64;
    static final int DTYCLOB = 112;
    static final int DTYBLOB = 113;
    byte[] sourceLobLocator = null;
    byte[] destinationLobLocator = null;
    long sourceOffset = 0L;
    long destinationOffset = 0L;
    int destinationLength = 0;
    short characterSet = 0;
    long lobamt = 0L;
    boolean lobnull = false;
    long lobops = 0L;
    int[] lobscn = null;
    int lobscnl = 0;

    boolean nullO2U = false;

    boolean sendLobamt = false;

    byte[] inBuffer = null;
    byte[] outBuffer = null;
    int rowsProcessed = 0;
    long lobBytesRead = 0L;
    boolean varWidthChar = false;
    boolean littleEndianClob = false;
    T4C8TTILobd lobd = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTILob(T4CMAREngine mengine, T4CTTIoer _oer) {
        super((byte) 3, 0, (short) 96);

        setMarshalingEngine(mengine);

        this.oer = _oer;

        this.lobd = new T4C8TTILobd(mengine);
    }

    long read(byte[] lobLocator, long offset, long numBytes, byte[] outBuffer) throws SQLException,
            IOException {
        initializeLobdef();

        this.lobops = 2L;
        this.sourceLobLocator = lobLocator;
        this.sourceOffset = offset;
        this.lobamt = numBytes;
        this.sendLobamt = true;
        this.outBuffer = outBuffer;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        return this.lobBytesRead;
    }

    long write(byte[] lobLocator, long offset, byte[] inBuffer, long inBufferOffset, long numBytes)
            throws SQLException, IOException {
        long bytesWritten = 0L;

        initializeLobdef();

        this.lobops = 64L;
        this.sourceLobLocator = lobLocator;
        this.sourceOffset = offset;
        this.lobamt = numBytes;
        this.sendLobamt = true;
        this.inBuffer = inBuffer;

        marshalFunHeader();

        marshalOlobops();

        this.lobd.marshalLobData(inBuffer, inBufferOffset, numBytes);

        receiveReply();

        bytesWritten = this.lobamt;

        return bytesWritten;
    }

    long getLength(byte[] lobLocator) throws SQLException, IOException {
        long lobLength = 0L;

        initializeLobdef();

        this.lobops = 1L;
        this.sourceLobLocator = lobLocator;

        this.sendLobamt = true;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        lobLength = this.lobamt;

        return lobLength;
    }

    long getChunkSize(byte[] lobLocator) throws SQLException, IOException {
        long chunkSize = 0L;

        initializeLobdef();

        this.lobops = 16384L;
        this.sourceLobLocator = lobLocator;

        marshalFunHeader();

        this.sendLobamt = true;

        marshalOlobops();

        receiveReply();

        chunkSize = this.lobamt;

        return chunkSize;
    }

    long trim(byte[] lobLocator, long newLength) throws SQLException, IOException {
        long newLengthfromServer = 0L;

        initializeLobdef();

        this.lobops = 32L;
        this.sourceLobLocator = lobLocator;
        this.lobamt = newLength;
        this.sendLobamt = true;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        newLengthfromServer = this.lobamt;

        return newLengthfromServer;
    }

    abstract Datum createTemporaryLob(Connection paramConnection, boolean paramBoolean, int paramInt)
            throws SQLException, IOException;

    void freeTemporaryLob(byte[] lobLocator) throws SQLException, IOException {
        initializeLobdef();

        this.lobops = 273L;
        this.sourceLobLocator = lobLocator;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();
    }

    abstract boolean open(byte[] paramArrayOfByte, int paramInt) throws SQLException, IOException;

    boolean _open(byte[] lobLocator, int mode, int lobops) throws SQLException, IOException {
        boolean didOpen = false;

        if (((lobLocator[7] & 0x1) == 1) || ((lobLocator[4] & 0x40) == 64)) {
            if ((lobLocator[7] & 0x8) == 8) {
                DatabaseError.throwSqlException(445);
            } else {
                byte[] tmp48_45 = lobLocator;
                tmp48_45[7] = (byte) (tmp48_45[7] | 0x8);

                if (mode == 2) {
                    byte[] tmp63_60 = lobLocator;
                    tmp63_60[7] = (byte) (tmp63_60[7] | 0x10);
                }
                didOpen = true;
            }

        } else {
            initializeLobdef();

            this.sourceLobLocator = lobLocator;
            this.lobops = lobops;
            this.lobamt = mode;
            this.sendLobamt = true;

            marshalFunHeader();

            marshalOlobops();

            receiveReply();

            if (this.lobamt != 0L) {
                didOpen = true;
            }

        }

        return didOpen;
    }

    abstract boolean close(byte[] paramArrayOfByte) throws SQLException, IOException;

    boolean _close(byte[] lobLocator, int lobops) throws SQLException, IOException {
        boolean isClosed = true;

        if (((lobLocator[7] & 0x1) == 1) || ((lobLocator[4] & 0x40) == 64)) {
            if ((lobLocator[7] & 0x8) != 8) {
                DatabaseError.throwSqlException(446);
            } else {
                byte[] tmp47_44 = lobLocator;
                tmp47_44[7] = (byte) (tmp47_44[7] & 0xFFFFFFE7);
            }

        } else {
            initializeLobdef();

            this.sourceLobLocator = lobLocator;
            this.lobops = lobops;

            marshalFunHeader();

            marshalOlobops();

            receiveReply();
        }

        return isClosed;
    }

    abstract boolean isOpen(byte[] paramArrayOfByte) throws SQLException, IOException;

    boolean _isOpen(byte[] lobLocator, int lobops) throws SQLException, IOException {
        boolean lobOpen = false;

        if (((lobLocator[7] & 0x1) == 1) || ((lobLocator[4] & 0x40) == 64)) {
            if ((lobLocator[7] & 0x8) == 8) {
                lobOpen = true;
            }

        } else {
            initializeLobdef();

            this.sourceLobLocator = lobLocator;
            this.lobops = lobops;
            this.nullO2U = true;

            marshalFunHeader();

            marshalOlobops();

            receiveReply();

            lobOpen = this.lobnull;
        }

        return lobOpen;
    }

    void initializeLobdef() {
        this.sourceLobLocator = null;
        this.destinationLobLocator = null;
        this.sourceOffset = 0L;
        this.destinationOffset = 0L;
        this.destinationLength = 0;
        this.characterSet = 0;
        this.lobamt = 0L;
        this.lobnull = false;
        this.lobops = 0L;
        this.lobscn = null;
        this.lobscnl = 0;
        this.inBuffer = null;
        this.outBuffer = null;
        this.nullO2U = false;
        this.sendLobamt = false;
        this.varWidthChar = false;
        this.littleEndianClob = false;
        this.lobBytesRead = 0L;
    }

    void marshalOlobops() throws IOException {
        int slength = 0;

        if (this.sourceLobLocator != null) {
            slength = this.sourceLobLocator.length;

            this.meg.marshalPTR();
        } else {
            this.meg.marshalNULLPTR();
        }

        this.meg.marshalSB4(slength);

        if (this.destinationLobLocator != null) {
            this.destinationLength = this.destinationLobLocator.length;

            this.meg.marshalPTR();
        } else {
            this.meg.marshalNULLPTR();
        }

        this.meg.marshalSB4(this.destinationLength);

        if (this.meg.versionNumber >= 10000)
            this.meg.marshalUB4(0L);
        else {
            this.meg.marshalUB4(this.sourceOffset);
        }

        if (this.meg.versionNumber >= 10000)
            this.meg.marshalUB4(0L);
        else {
            this.meg.marshalUB4(this.destinationOffset);
        }

        if (this.characterSet != 0)
            this.meg.marshalPTR();
        else {
            this.meg.marshalNULLPTR();
        }

        if ((this.sendLobamt == true) && (this.meg.versionNumber < 10000))
            this.meg.marshalPTR();
        else {
            this.meg.marshalNULLPTR();
        }

        if (this.nullO2U == true)
            this.meg.marshalPTR();
        else {
            this.meg.marshalNULLPTR();
        }

        this.meg.marshalUB4(this.lobops);

        if (this.lobscnl != 0)
            this.meg.marshalPTR();
        else {
            this.meg.marshalNULLPTR();
        }

        this.meg.marshalSB4(this.lobscnl);

        if (this.meg.versionNumber >= 10000) {
            this.meg.marshalSB8(this.sourceOffset);
            this.meg.marshalSB8(this.destinationOffset);

            if (this.sendLobamt == true)
                this.meg.marshalPTR();
            else {
                this.meg.marshalNULLPTR();
            }

        }

        if (this.sourceLobLocator != null) {
            this.meg.marshalB1Array(this.sourceLobLocator);
        }

        if (this.destinationLobLocator != null) {
            this.meg.marshalB1Array(this.destinationLobLocator);
        }

        if (this.characterSet != 0) {
            this.meg.marshalUB2(this.characterSet);
        }

        if ((this.sendLobamt == true) && (this.meg.versionNumber < 10000)) {
            this.meg.marshalUB4(this.lobamt);
        }

        if (this.lobscnl != 0) {
            for (int i = 0; i < this.lobscnl; i++) {
                this.meg.marshalUB4(this.lobscn[i]);
            }
        }

        if ((this.sendLobamt == true) && (this.meg.versionNumber >= 10000)) {
            this.meg.marshalSB8(this.lobamt);
        }
    }

    void receiveReply() throws IOException, SQLException {
        byte code = 0;
        while (true) {
            try {
                code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    unmarshalTTIRPA();

                    break;
                case 14:
                    //              if (!this.varWidthChar) {
                    //                  this.lobBytesRead = this.lobd.unmarshalLobData(this.outBuffer);
                    //              } else {
                    //                  if (this.meg.versionNumber >= 10101) {
                    //                      continue;
                    //                  }
                    //
                    //                  this.lobBytesRead = this.lobd.unmarshalClobUB2(this.outBuffer);
                    //                  continue;
                    //
                    //                  this.lobBytesRead = this.lobd.unmarshalLobData(this.outBuffer);
                    //              }
                    //
                    //              break;
                    if (!varWidthChar) {
                        lobBytesRead = lobd.unmarshalLobData(outBuffer);
                    } else if (meg.versionNumber < 10101) {
                        lobBytesRead = lobd.unmarshalClobUB2(outBuffer);
                    } else {
                        lobBytesRead = lobd.unmarshalLobData(outBuffer);
                    }
                    continue;

                case 4:
                    this.oer.init();
                    this.oer.unmarshal();

                    this.rowsProcessed = this.oer.getCurRowNumber();

                    if (this.oer.getRetCode() == 1403) {
                        continue;
                    }
                    this.oer.processError();

                    break;
                case 9:
                    break;
                default:
                    DatabaseError.throwSqlException(401);

                    break;
                }

                continue;
            } catch (BreakNetException ea) {
            }
        }
    }

    void unmarshalTTIRPA() throws SQLException, IOException {
        if (this.sourceLobLocator != null) {
            int length = this.sourceLobLocator.length;

            this.meg.getNBytes(this.sourceLobLocator, 0, length);
        }

        if (this.destinationLobLocator != null) {
            short length = this.meg.unmarshalSB2();

            this.destinationLobLocator = this.meg.unmarshalNBytes(length);
        }

        if (this.characterSet != 0) {
            this.characterSet = this.meg.unmarshalSB2();
        }

        if (this.sendLobamt == true) {
            if (this.meg.versionNumber >= 10000)
                this.lobamt = this.meg.unmarshalSB8();
            else {
                this.lobamt = this.meg.unmarshalUB4();
            }
        }

        if (this.nullO2U == true) {
            short isNull = this.meg.unmarshalSB2();

            if (isNull != 0) {
                this.lobnull = true;
            }
        }

        if (this.lobscnl != 0) {
            for (int i = 0; i < this.lobscnl; i++) {
                this.lobscn[i] = this.meg.unmarshalSB4();
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTILob"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTILob JD-Core Version: 0.6.0
 */