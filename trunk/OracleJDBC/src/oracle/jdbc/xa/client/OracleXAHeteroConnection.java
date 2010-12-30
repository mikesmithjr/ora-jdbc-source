package oracle.jdbc.xa.client;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.xa.OracleXAResource;

public class OracleXAHeteroConnection extends OracleXAConnection {
    private int rmid = -1;
    private String xaCloseString = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAHeteroConnection() throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger
                    .log(Level.FINE,
                         "oracle.jdbc.xa.client.OracleXAHeteroConnection() -- after super()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXAHeteroConnection(Connection pc) throws XAException {
        super(pc);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.client.OracleXAHeteroConnection(pc = " + pc
                                           + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized XAResource getXAResource() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAHeteroConnection.getXAResource()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.xaResource = new OracleXAHeteroResource(this.physicalConn, this);

            ((OracleXAHeteroResource) this.xaResource).setRmid(this.rmid);

            if (this.logicalHandle != null) {
                ((OracleXAResource) this.xaResource).setLogicalConnection(this.logicalHandle);
            }
        } catch (XAException xae) {
            this.xaResource = null;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroConnection.getXAResource() return: "
                    + this.xaResource, this);

            OracleLog.recursiveTrace = false;
        }

        return this.xaResource;
    }

    synchronized void setRmid(int rmid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroConnection.setRmid(rmid = " + rmid
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.rmid = rmid;
    }

    synchronized int getRmid() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroConnection.getRmid() return: "
                    + this.rmid, this);

            OracleLog.recursiveTrace = false;
        }

        return this.rmid;
    }

    synchronized void setXaCloseString(String xaCloseString) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAHeteroConnection.setXaCloseString(xaCloseString = "
                                           + xaCloseString + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.xaCloseString = xaCloseString;
    }

    synchronized String getXaCloseString() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAHeteroConnection.getXaCloseString() return: "
                                           + this.xaCloseString, this);

            OracleLog.recursiveTrace = false;
        }

        return this.xaCloseString;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.client.OracleXAHeteroConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.client.OracleXAHeteroConnection JD-Core Version: 0.6.0
 */