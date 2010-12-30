package oracle.jdbc.xa;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import oracle.jdbc.driver.OracleLog;

public class OracleXid implements Xid, Serializable {
    private int formatId;
    private byte[] gtrid = null;
    private byte[] bqual = null;
    private byte[] txctx = null;
    public static final int MAXGTRIDSIZE = 64;
    public static final int MAXBQUALSIZE = 64;
    private int state;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXid(int fId, byte[] gId, byte[] bId) throws XAException {
        this(fId, gId, bId, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.OracleXid(fId = " + fId + ", gId = "
                    + gId + ", bId = " + bId + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXid(int fId, byte[] gId, byte[] bId, byte[] context) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.OracleXid(fId = " + fId + ", gId = "
                    + gId + ", bId = " + bId + ", context = " + context + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.formatId = fId;

        if ((gId != null) && (gId.length > 64)) {
            throw new XAException(-4);
        }
        this.gtrid = gId;

        if ((bId != null) && (bId.length > 64)) {
            throw new XAException(-4);
        }
        this.bqual = bId;
        this.txctx = context;
        this.state = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.OracleXid(fId, gId, bId, context) return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setState(int k) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.setState(k = " + k + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.state = k;
    }

    public int getState() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.getState() return: " + this.state, this);

            OracleLog.recursiveTrace = false;
        }

        return this.state;
    }

    public int getFormatId() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.getFormatId() return: " + this.formatId,
                                   this);

            OracleLog.recursiveTrace = false;
        }

        return this.formatId;
    }

    public byte[] getGlobalTransactionId() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.getGlobalTransactionId() return: "
                    + this.gtrid, this);

            OracleLog.recursiveTrace = false;
        }

        return this.gtrid;
    }

    public byte[] getBranchQualifier() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.getBranchQualifier() return: "
                    + this.bqual, this);

            OracleLog.recursiveTrace = false;
        }

        return this.bqual;
    }

    public byte[] getTxContext() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.getTxContext() return: " + this.txctx,
                                   this);

            OracleLog.recursiveTrace = false;
        }

        return this.txctx;
    }

    public void setTxContext(byte[] context) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXid.setTxContext(context = " + context + ")",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        this.txctx = context;
    }

    public static final boolean isLocalTransaction(Xid xid) {
        byte[] gtrid = xid.getGlobalTransactionId();

        if (gtrid == null) {
            return true;
        }
        for (int i = 0; i < gtrid.length; i++) {
            if (gtrid[i] != 0) {
                return false;
            }
        }
        return true;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.OracleXid"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.OracleXid JD-Core Version: 0.6.0
 */