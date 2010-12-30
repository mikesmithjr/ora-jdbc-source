package oracle.jdbc.connector;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.EISSystemException;
import javax.resource.spi.IllegalStateException;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.resource.spi.security.PasswordCredential;
import javax.security.auth.Subject;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.xa.OracleXAConnection;

public class OracleManagedConnection implements ManagedConnection {
    private OracleXAConnection xaConnection = null;
    private Hashtable connectionListeners = null;
    private Connection connection = null;
    private PrintWriter logWriter = null;
    private PasswordCredential passwordCredential = null;
    private OracleLocalTransaction localTxn = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    OracleManagedConnection(XAConnection xaconn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnection.OracleManagedConnection(xaconn = "
                                               + xaconn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.xaConnection = ((OracleXAConnection) xaconn);
        this.connectionListeners = new Hashtable(10);
    }

    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnection.getConnection(subject = " + subject
                                               + ", cxRequestInfo = " + cxRequestInfo + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            if (this.connection != null) {
                this.connection.close();
            }
            this.connection = this.xaConnection.getConnection();

            return this.connection;
        } catch (SQLException exc) {
            rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    public void destroy() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.destroy()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.xaConnection != null) {
                Connection _pconn = this.xaConnection.getPhysicalHandle();

                if (((this.localTxn != null) && (this.localTxn.isBeginCalled))
                        || (((OracleConnection) _pconn).getTxnMode() == 1)) {
                    ResourceException rexc = new IllegalStateException(
                            "Could not close connection while transaction is active");

                    throw rexc;
                }
            }

            if (this.connection != null) {
                this.connection.close();
            }
            if (this.xaConnection != null) {
                this.xaConnection.close();
            }

        } catch (SQLException exc) {
            ResourceException rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }
    }

    public void cleanup() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.cleanup()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.connection != null) {
                if (((this.localTxn != null) && (this.localTxn.isBeginCalled))
                        || (((OracleConnection) this.connection).getTxnMode() == 1)) {
                    ResourceException rexc = new IllegalStateException(
                            "Could not close connection while transaction is active");

                    throw rexc;
                }

                this.connection.close();
            }

        } catch (SQLException exc) {
            ResourceException rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }
    }

    public void associateConnection(Object connection) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnection.associateConnection(connection = "
                                               + connection + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void addConnectionEventListener(ConnectionEventListener listener) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnection.addConnectionEventListener(listener = "
                                               + listener + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connectionListeners.put(listener, listener);
    }

    public void removeConnectionEventListener(ConnectionEventListener listener) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnection.removeConnectionEventListener(listener = "
                                               + listener + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connectionListeners.remove(listener);
    }

    public XAResource getXAResource() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.getXAResource()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.xaConnection.getXAResource();
    }

    public LocalTransaction getLocalTransaction() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.getLocalTransaction()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (this.localTxn == null) {
            this.localTxn = new OracleLocalTransaction(this);
        }

        return this.localTxn;
    }

    public ManagedConnectionMetaData getMetaData() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.getMetaData()", this);

            OracleLog.recursiveTrace = false;
        }

        return new OracleManagedConnectionMetaData(this);
    }

    public void setLogWriter(PrintWriter out) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.setLogWriter(out = "
                    + out + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.logWriter = out;

        OracleLog.setLogWriter(out);
    }

    public PrintWriter getLogWriter() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnection.getLogWriter()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.logWriter;
    }

    Connection getPhysicalConnection() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnection.getPhysicalConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            return this.xaConnection.getPhysicalHandle();
        } catch (Exception exc) {
            rexc = new EISSystemException("Exception: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    void setPasswordCredential(PasswordCredential pcred) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnection.setPasswordCredential(pcred = "
                                               + pcred + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.passwordCredential = pcred;
    }

    PasswordCredential getPasswordCredential() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnection.getPasswordCredential()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.passwordCredential;
    }

    void eventOccurred(int eventType) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnection.eventOccurred(eventType = "
                                               + eventType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Enumeration allListeners = this.connectionListeners.keys();

        while (allListeners.hasMoreElements()) {
            ConnectionEventListener listener = (ConnectionEventListener) allListeners.nextElement();

            ConnectionEvent ce = new ConnectionEvent(this, eventType);

            switch (eventType) {
            case 1:
                listener.connectionClosed(ce);

                break;
            case 2:
                listener.localTransactionStarted(ce);

                break;
            case 3:
                listener.localTransactionCommitted(ce);

                break;
            case 4:
                listener.localTransactionRolledback(ce);

                break;
            case 5:
                listener.connectionErrorOccurred(ce);

                break;
            default:
                throw new IllegalArgumentException("Illegal eventType in eventOccurred(): "
                        + eventType);
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleManagedConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleManagedConnection JD-Core Version: 0.6.0
 */