package oracle.jdbc.xa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.pool.OraclePooledConnection;

public abstract class OracleXAConnection extends OraclePooledConnection implements XAConnection {
    protected XAResource xaResource = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAConnection() throws XAException {
        this(null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.OracleXAConnection()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXAConnection(Connection pc) throws XAException {
        super(pc);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.OracleXAConnection(pc = " + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public abstract XAResource getXAResource();

    public synchronized Connection getConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAConnection.getConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = super.getConnection();

        if (this.xaResource != null) {
            ((OracleXAResource) this.xaResource).setLogicalConnection(conn);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAConnection.getConnection() return: " + conn, this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    boolean getAutoCommit() throws SQLException {
        return this.autoCommit;
    }

    void setAutoCommit(boolean value) throws SQLException {
        this.autoCommit = value;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.OracleXAConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.OracleXAConnection JD-Core Version: 0.6.0
 */