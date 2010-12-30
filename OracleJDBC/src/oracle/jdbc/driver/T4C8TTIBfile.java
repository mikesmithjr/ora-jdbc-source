package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.BFILE;
import oracle.sql.Datum;

class T4C8TTIBfile extends T4C8TTILob {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTIBfile(T4CMAREngine mengine, T4CTTIoer _oer) {
        super(mengine, _oer);
    }

    Datum createTemporaryLob(Connection conn, boolean cache, int duration) throws SQLException,
            IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Entering TT4C8TIBfile.createTemporaryLob. cache is " + cache
                                             + ". duration is " + duration, this);

            OracleLog.recursiveTrace = false;
        }

        BFILE bfile = null;

        DatabaseError.throwSqlException("cannot create a temporary BFILE", DatabaseError
                .ErrorToSQLState(-1), -1);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "Exiting T4C8TTIBfile.createTemporaryLob. oracle.sql.BFILE is "
                                             + bfile, this);

            OracleLog.recursiveTrace = false;
        }

        return bfile;
    }

    boolean open(byte[] lobLocator, int mode) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBfile.open. lobLocator is "
                    + lobLocator + ". mode is " + mode, this);

            OracleLog.recursiveTrace = false;
        }

        boolean wasOpened = false;

        wasOpened = _open(lobLocator, 11, 256);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBfile.open. wasOpened is "
                    + wasOpened, this);

            OracleLog.recursiveTrace = false;
        }

        return wasOpened;
    }

    boolean close(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBfile.close. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean wasClosed = false;

        wasClosed = _close(lobLocator, 512);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBfile.close. wasClosed is "
                    + wasClosed, this);

            OracleLog.recursiveTrace = false;
        }

        return wasClosed;
    }

    boolean isOpen(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBfile.isOpen. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean open = _isOpen(lobLocator, 1024);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBfile.isOpen. open is " + open,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return open;
    }

    boolean doesExist(byte[] lobLocator) throws SQLException, IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Entering T4C8TTIBfile.doesExist. lobLocator is "
                    + lobLocator, this);

            OracleLog.recursiveTrace = false;
        }

        boolean exists = false;

        initializeLobdef();

        this.sourceLobLocator = lobLocator;
        this.lobops = 2048L;
        this.nullO2U = true;

        marshalFunHeader();

        marshalOlobops();

        receiveReply();

        exists = this.lobnull;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "Exiting T4C8TTIBfile.doesExist. exists is "
                    + exists, this);

            OracleLog.recursiveTrace = false;
        }

        return exists;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTIBfile"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTIBfile JD-Core Version: 0.6.0
 */