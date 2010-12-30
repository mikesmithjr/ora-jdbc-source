package oracle.jdbc.connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.EISSystemException;
import javax.resource.spi.IllegalStateException;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.LocalTransactionException;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class OracleLocalTransaction implements LocalTransaction {
    private OracleManagedConnection managedConnection = null;
    private Connection connection = null;
    boolean isBeginCalled = false;
    private static final String RAERR_LTXN_COMMIT = "commit without begin";
    private static final String RAERR_LTXN_ROLLBACK = "rollback without begin";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    OracleLocalTransaction(OracleManagedConnection omc) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleLocalTransaction.OracleLocalTransaction(omc = " + omc
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.managedConnection = omc;
        this.connection = omc.getPhysicalConnection();
        this.isBeginCalled = false;
    }

    public void begin() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleLocalTransaction.begin()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (((OracleConnection) this.connection).getTxnMode() == 1) {
                ResourceException rexc = new IllegalStateException(
                        "Could not start a new transaction inside an active transaction");

                throw rexc;
            }

            if (this.connection.getAutoCommit()) {
                this.connection.setAutoCommit(false);
            }
            this.isBeginCalled = true;
        } catch (SQLException exc) {
            ResourceException rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }

        this.managedConnection.eventOccurred(2);
    }

    public void commit() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleLocalTransaction.commit()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.isBeginCalled) {
            throw new LocalTransactionException("begin() must be called before commit()",
                    "commit without begin");
        }

        try {
            this.connection.commit();
        } catch (SQLException exc) {
            ResourceException rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }

        this.isBeginCalled = false;

        this.managedConnection.eventOccurred(3);
    }

    public void rollback() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleLocalTransaction.rollback()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.isBeginCalled) {
            throw new LocalTransactionException("begin() must be called before rollback()",
                    "rollback without begin");
        }

        try {
            this.connection.rollback();
        } catch (SQLException exc) {
            ResourceException rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }

        this.isBeginCalled = false;

        this.managedConnection.eventOccurred(4);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleLocalTransaction"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleLocalTransaction JD-Core Version: 0.6.0
 */