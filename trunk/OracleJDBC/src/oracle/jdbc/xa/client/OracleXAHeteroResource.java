package oracle.jdbc.xa.client;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.oracore.Util;

public class OracleXAHeteroResource extends OracleXAResource {
    private int rmid = -1;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAHeteroResource(Connection pm_conn, OracleXAConnection xaconn) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.client.OracleXAHeteroResource(pm_conn = "
                                           + pm_conn + ", xaconn = " + xaconn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = pm_conn;
        this.xaconnection = xaconn;

        if (this.connection == null)
            throw new XAException(-7);
    }

    public void start(Xid xid, int flags) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAHeteroResource.start(xid = " + xid
                    + ", flags = " + flags + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAHeteroResource.start(): XAER_INVAL: xid is null",
                                       this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        int isolFlag = flags & 0xFF00;

        flags &= -65281;

        if ((flags & 0x8200002) != flags) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAHeteroResource.start(): XAER_INVAL: invalid flag",
                                       this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        if (((isolFlag & 0xFF00) != 0) && (isolFlag != 256) && (isolFlag != 512)
                && (isolFlag != 1024)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.SEVERE,
                             "OracleXAHeteroResource.start():XAER_INVAL: invalid Isolation flag",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        if (((isolFlag & 0xFF00) != 0) && ((flags & 0x8200000) != 0)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(
                             Level.SEVERE,
                             "OracleXAHeteroResource.start(): XAER_INVAL: Isolation flags not allowed for JOIN/RESUME",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        try {
            saveAndAlterAutoCommitModeForGlobalTransaction();

            flags |= isolFlag;

            int formatId = xid.getFormatId();
            byte[] gTrid = xid.getGlobalTransactionId();
            byte[] bQual = xid.getBranchQualifier();

            int status = doXaStart(formatId, gTrid, bQual, this.rmid, flags, 0);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER,
                                       "OracleXAHeteroResource.start(): return status = " + status,
                                       this);

                OracleLog.recursiveTrace = false;
            }

            checkStatus(status);

            super.push(xid);
        } catch (XAException ea) {
            restoreAutoCommitModeForGlobalTransaction();
            throw ea;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.start() return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void end(Xid xid, int flag) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAHeteroResource.end(xid = " + xid
                    + ", flag = " + flag + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (xid == null) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.SEVERE,
                                           "OracleXAHeteroResource.end(): XAER_INVAL: xid is null",
                                           this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            if ((flag != 33554432) && (flag != 67108864) && (flag != 536870912)
                    && ((flag & 0x2) != 2)) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger
                            .log(Level.SEVERE,
                                 "OracleXAHeteroResource.end(): XAER_INVAL: invalid flag", this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            int formatId = xid.getFormatId();
            byte[] gTrid = xid.getGlobalTransactionId();
            byte[] bQual = xid.getBranchQualifier();

            Xid stackedXid = super.suspendStacked(xid, flag);

            super.pop();

            int status = doXaEnd(formatId, gTrid, bQual, this.rmid, flag, 0);

            super.resumeStacked(stackedXid);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER,
                                       "OracleXAHeteroResource.end(): return status = " + status,
                                       this);

                OracleLog.recursiveTrace = false;
            }

            checkStatus(status);
        } finally {
            restoreAutoCommitModeForGlobalTransaction();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.end() return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void commit(Xid xid, boolean onePhase) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAHeteroResource.commit(xid = " + xid
                    + ", onePhase = " + onePhase + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAHeteroResource.commit(): XAER_INVAL: xid is null",
                                       this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        int flags = onePhase ? 1073741824 : 0;

        int formatId = xid.getFormatId();
        byte[] gTrid = xid.getGlobalTransactionId();
        byte[] bQual = xid.getBranchQualifier();

        Xid stackedXid = super.suspendStacked(xid);

        int status = doXaCommit(formatId, gTrid, bQual, this.rmid, flags, 0);

        super.resumeStacked(stackedXid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER, "OracleXAHeteroResource.commit(): return status = "
                    + status, this);

            OracleLog.recursiveTrace = false;
        }

        checkStatus(status);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.commit() return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public int prepare(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAHeteroResource.prepare(xid = " + xid + ")",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAHeteroResource.prepare(): XAER_INVAL: xid is null",
                                       this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        int formatId = xid.getFormatId();
        byte[] gTrid = xid.getGlobalTransactionId();
        byte[] bQual = xid.getBranchQualifier();

        Xid stackedXid = super.suspendStacked(xid);

        int status = doXaPrepare(formatId, gTrid, bQual, this.rmid, 0, 0);

        super.resumeStacked(stackedXid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER,
                                   "OracleXAHeteroResource.prepare(): return status = " + status,
                                   this);

            OracleLog.recursiveTrace = false;
        }

        if ((status != 0) && (status != 3)) {
            checkStatus(status);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAHeteroResource.prepare() return: " + status, this);

            OracleLog.recursiveTrace = false;
        }

        return status;
    }

    public void forget(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAHeteroResource.forget(xid = " + xid + ")",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAHeteroResource.forget(): XAER_INVAL: xid is null",
                                       this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        int formatId = xid.getFormatId();
        byte[] gTrid = xid.getGlobalTransactionId();
        byte[] bQual = xid.getBranchQualifier();

        int status = doXaForget(formatId, gTrid, bQual, this.rmid, 0, 0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER, "OracleXAHeteroResource.forget(): return status = "
                    + status, this);

            OracleLog.recursiveTrace = false;
        }

        checkStatus(status);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.forget() return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void rollback(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO,
                                   "OracleXAHeteroResource.rollback(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.SEVERE,
                             "OracleXAHeteroResource.rollback(): XAER_INVAL: xid is null", this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        int formatId = xid.getFormatId();
        byte[] gTrid = xid.getGlobalTransactionId();
        byte[] bQual = xid.getBranchQualifier();

        Xid stackedXid = super.suspendStacked(xid);

        int status = doXaRollback(formatId, gTrid, bQual, this.rmid, 0, 0);

        super.resumeStacked(stackedXid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER,
                                   "OracleXAHeteroResource.rollback(): return status = " + status,
                                   this);

            OracleLog.recursiveTrace = false;
        }

        checkStatus(status);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.rollback() return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private native int doXaStart(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2,
            int paramInt2, int paramInt3, int paramInt4);

    private native int doXaEnd(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2,
            int paramInt2, int paramInt3, int paramInt4);

    private native int doXaCommit(int paramInt1, byte[] paramArrayOfByte1,
            byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4);

    private native int doXaPrepare(int paramInt1, byte[] paramArrayOfByte1,
            byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4);

    private native int doXaForget(int paramInt1, byte[] paramArrayOfByte1,
            byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4);

    private native int doXaRollback(int paramInt1, byte[] paramArrayOfByte1,
            byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4);

    synchronized void setRmid(int rmid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.setRmid(rmid = " + rmid
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.rmid = rmid;
    }

    synchronized int getRmid() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.getRmid() return: "
                    + this.rmid, this);

            OracleLog.recursiveTrace = false;
        }

        return this.rmid;
    }

    private static byte[] getSerializedBytes(Xid xid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.getSerializedBytes(xid = "
                    + xid + ")");

            OracleLog.recursiveTrace = false;
        }

        try {
            return Util.serializeObject(xid);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    private void checkStatus(int status) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroResource.checkStatus(status = "
                    + status + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (status != 0)
            throw new XAException(status);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.client.OracleXAHeteroResource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.client.OracleXAHeteroResource JD-Core Version: 0.6.0
 */