package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.sql.BLOB;
import oracle.sql.Datum;

class T4C8TTIBlob extends T4C8TTILob {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTIBlob(T4CMAREngine mengine, T4CTTIoer _oer) {
        super(mengine, _oer);
    }

    Datum createTemporaryLob(Connection conn, boolean cache, int duration) throws SQLException,
            IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Entering T4C8TTIBlob.createTemporaryLob. cache is " + cache
                                             + ". duration is " + duration, this);

            OracleLog.recursiveTrace = false;
        }

        if (duration == 12) {
            DatabaseError.throwSqlException(158);
        }

        BLOB blob = null;

        initializeLobdef();

        this.lobops = 272L;
        this.sourceLobLocator = new byte[86];
        this.sourceLobLocator[1] = 84;

        this.characterSet = 1;

        this.lobamt = duration;
        this.sendLobamt = true;

        this.destinationOffset = 113L;

        this.nullO2U = true;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        if (this.sourceLobLocator != null) {
            blob = new BLOB((OracleConnection) conn, this.sourceLobLocator);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Exiting T4C8TTIBlob.createTemporaryLob. oracle.sql.BLOB is "
                                             + blob, this);

            OracleLog.recursiveTrace = false;
        }

        return blob;
    }

    boolean open(byte[] lobLocator, int mode) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBlob.open. lobLocator is "
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
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBlob.open. wasOpened is "
                    + wasOpened, this);

            OracleLog.recursiveTrace = false;
        }

        return wasOpened;
    }

    boolean close(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBlob.close. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean wasClosed = false;

        wasClosed = _close(lobLocator, 65536);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBlob.close. wasClosed is "
                    + wasClosed, this);

            OracleLog.recursiveTrace = false;
        }

        return wasClosed;
    }

    boolean isOpen(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBlob.isOpen. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean open = false;

        open = _isOpen(lobLocator, 69632);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBlob.isOpen. open is " + open,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return open;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTIBlob"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTIBlob JD-Core Version: 0.6.0
 */