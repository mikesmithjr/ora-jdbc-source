package oracle.jdbc.xa.client;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import oracle.jdbc.driver.OracleLog;

public class OracleXAConnection extends oracle.jdbc.xa.OracleXAConnection {
    protected boolean isXAResourceTransLoose = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAConnection() throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.client.OracleXAConnection() -- after super()",
                                   this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXAConnection(Connection pc) throws XAException {
        super(pc);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.client.OracleXAConnection(pc = "
                    + pc + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized XAResource getXAResource() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAConnection.getXAResource()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.xaResource = new OracleXAResource(this.physicalConn, this);
            ((OracleXAResource) this.xaResource).isTransLoose = this.isXAResourceTransLoose;

            if (this.logicalHandle != null) {
                ((oracle.jdbc.xa.OracleXAResource) this.xaResource)
                        .setLogicalConnection(this.logicalHandle);
            }
        } catch (XAException xae) {
            this.xaResource = null;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAConnection.getXAResource() return: "
                    + this.xaResource, this);

            OracleLog.recursiveTrace = false;
        }

        return this.xaResource;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.client.OracleXAConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.client.OracleXAConnection JD-Core Version: 0.6.0
 */