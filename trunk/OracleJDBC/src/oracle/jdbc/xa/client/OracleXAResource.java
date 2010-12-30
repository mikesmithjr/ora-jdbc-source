package oracle.jdbc.xa.client;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.Util;
import oracle.jdbc.xa.OracleXAConnection;
import oracle.jdbc.xa.OracleXAException;

public class OracleXAResource extends oracle.jdbc.xa.OracleXAResource {
    private short m_version = 0;

    private static String xa_start_816 = "begin ? := JAVA_XA.xa_start(?,?,?,?); end;";

    private static String xa_start_post_816 = "begin ? := JAVA_XA.xa_start_new(?,?,?,?,?); end;";

    private static String xa_end_816 = "begin ? := JAVA_XA.xa_end(?,?); end;";
    private static String xa_end_post_816 = "begin ? := JAVA_XA.xa_end_new(?,?,?,?); end;";

    private static String xa_commit_816 = "begin ? := JAVA_XA.xa_commit (?,?,?); end;";

    private static String xa_commit_post_816 = "begin ? := JAVA_XA.xa_commit_new (?,?,?,?); end;";

    private static String xa_prepare_816 = "begin ? := JAVA_XA.xa_prepare (?,?); end;";

    private static String xa_prepare_post_816 = "begin ? := JAVA_XA.xa_prepare_new (?,?,?); end;";

    private static String xa_rollback_816 = "begin ? := JAVA_XA.xa_rollback (?,?); end;";

    private static String xa_rollback_post_816 = "begin ? := JAVA_XA.xa_rollback_new (?,?,?); end;";

    private static String xa_forget_816 = "begin ? := JAVA_XA.xa_forget (?,?); end;";

    private static String xa_forget_post_816 = "begin ? := JAVA_XA.xa_forget_new (?,?,?); end;";

    boolean isTransLoose = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAResource() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.client.OracleXAResource()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXAResource(Connection pm_conn, OracleXAConnection xaconn) throws XAException {
        super(pm_conn, xaconn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.client.OracleXAResource(pm_conn = "
                    + pm_conn + ", xaconn = " + xaconn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.m_version = ((OracleConnection) pm_conn).getVersionNumber();
        } catch (SQLException sqe) {
        }
        if (this.m_version < 8170) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(
                             Level.SEVERE,
                             "OracleXAResource(pm_conn): Java XA not supported for this server version",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-6);
        }
    }

    public void start(Xid xid, int flag) throws XAException {
        int returnVal = -1;
        CallableStatement cstmt = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.start(xid = " + xid + ", flag = "
                    + flag + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (xid == null) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.SEVERE,
                                           "OracleXAResource.start(): XAER_INVAL: xid is null",
                                           this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            int isolFlag = flag & 0xFF00;

            flag &= -65281;

            int otherFlag = flag & 0x10000 | (this.isTransLoose ? 65536 : 0);

            flag &= -65537;

            if (((flag & 0x8200002) != flag)
                    || ((otherFlag != 0) && ((otherFlag & 0x10000) != 65536))) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.SEVERE,
                                           "OracleXAResource.start(): XAER_INVAL: invalid flag",
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
                                 "OracleXAResource.start(): XAER_INVAL: invalid Isolation flag",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            if (((flag & 0x8200000) != 0)
                    && (((isolFlag & 0xFF00) != 0) || ((otherFlag & 0x10000) != 0))) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger
                            .log(
                                 Level.SEVERE,
                                 "OracleXAResource.start(): XAER_INVAL: Isolation flags not allowed for JOIN/RESUME",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            flag |= isolFlag | otherFlag;

            saveAndAlterAutoCommitModeForGlobalTransaction();

            returnVal = doStart(xid, flag);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER,
                                       "OracleXAResource.start(): return status returnVal = "
                                               + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            checkError(returnVal);

            super.push(xid);
        } catch (XAException ea) {
            restoreAutoCommitModeForGlobalTransaction();

            throw ea;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.start(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected int doStart(Xid xid, int flag) throws XAException {
        int returnVal = -1;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_start_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());
            cstmt.setInt(5, this.timeout);
            cstmt.setInt(6, flag);

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER, "OracleXAResource.start() returnVal="
                        + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            if (returnVal == 0) {
                throw new XAException(-6);
            }

        } finally {
            try {
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public void end(Xid xid, int flag) throws XAException {
        int returnVal = -1;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.end(xid = " + xid + ", flag = "
                    + flag + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (xid == null) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.SEVERE,
                                           "OracleXAResource.end(): XAER_INVAL: xid is null", this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            if ((flag != 33554432) && (flag != 67108864) && (flag != 536870912)
                    && ((flag & 0x2) != 2)) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger
                            .log(Level.SEVERE, "OracleXAResource.end(): XAER_INVAL: invalid flag",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                throw new XAException(-5);
            }

            Xid stackedXid = super.suspendStacked(xid, flag);

            super.pop();

            returnVal = doEnd(xid, flag);

            super.resumeStacked(stackedXid);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER,
                                       "OracleXAResource.end(): return status returnVal = "
                                               + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            checkError(returnVal);
        } finally {
            restoreAutoCommitModeForGlobalTransaction();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.end(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected int doEnd(Xid xid, int flag) throws XAException {
        CallableStatement cstmt = null;
        int returnVal = -1;
        try {
            cstmt = this.connection.prepareCall(xa_end_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());
            cstmt.setInt(5, flag);
            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER,
                                       "OracleXAResource.end() returnVal=" + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            if (returnVal == 0) {
                throw new XAException(-6);
            }

        } finally {
            try {
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public void commit(Xid xid, boolean onePhase) throws XAException {
        int returnVal = -1;
        int stateout = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.commit(xid = " + xid
                    + ", onePhase = " + onePhase + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAResource.commit(): XAER_INVAL: xid is null", this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }
        int cPhase;
        if (onePhase)
            cPhase = 1;
        else {
            cPhase = 0;
        }

        Xid stackedXid = super.suspendStacked(xid);

        returnVal = doCommit(xid, cPhase);

        super.resumeStacked(stackedXid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER,
                                   "OracleXAResource.commit(): return status returnVal = "
                                           + returnVal, this);

            OracleLog.recursiveTrace = false;
        }

        checkError(returnVal);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.commit(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected int doCommit(Xid xid, int cPhase) throws XAException {
        int returnVal = -1;
        CallableStatement cstmt = null;
        int stateout = 0;
        try {
            cstmt = this.connection.prepareCall(xa_commit_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());
            cstmt.setInt(5, cPhase);

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER, "OracleXAResource.commit() returnVal="
                        + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            if (returnVal == 0) {
                throw new XAException(-6);
            }

        } finally {
            try {
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public int prepare(Xid xid) throws XAException {
        int returnVal = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.prepare(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAResource.prepare(): XAER_INVAL: xid is null", this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        Xid stackedXid = super.suspendStacked(xid);

        returnVal = doPrepare(xid);

        super.resumeStacked(stackedXid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER,
                                   "OracleXAResource.prepare(): return status returnVal = "
                                           + returnVal, this);

            OracleLog.recursiveTrace = false;
        }

        int x_e = returnVal == 0 ? 0 : OracleXAException.errorConvert(returnVal);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINEST, "OracleXAResource.prepare(): x_e = " + x_e, this);

            OracleLog.recursiveTrace = false;
        }

        if ((x_e != 0) && (x_e != 3)) {
            throw new OracleXAException(returnVal);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.prepare(): return: " + x_e, this);

            OracleLog.recursiveTrace = false;
        }

        return x_e;
    }

    protected int doPrepare(Xid xid) throws XAException {
        int returnVal = 0;
        int stateout = 0;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_prepare_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER, "OracleXAResource.prepare() returnVal="
                        + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            if (returnVal == 0) {
                throw new XAException(-6);
            }

        } finally {
            try {
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public void forget(Xid xid) throws XAException {
        int returnVal = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.forget(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAResource.forget(): XAER_INVAL: xid is null", this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        returnVal = doForget(xid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER,
                                   "OracleXAResource.forget(): return status returnVal = "
                                           + returnVal, this);

            OracleLog.recursiveTrace = false;
        }

        checkError(returnVal);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.forget(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected int doForget(Xid xid) throws XAException {
        int returnVal = 0;
        int stateout = 0;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_forget_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER, "OracleXAResource.forget() returnVal="
                        + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            if (returnVal == 0) {
                throw new XAException(-6);
            }

        } finally {
            try {
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public void rollback(Xid xid) throws XAException {
        int returnVal = 0;
        int stateout = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger
                    .log(Level.INFO, "OracleXAResource.rollback(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.SEVERE, "OracleXAResource.rollback(): XAER_INVAL: xid is null",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        Xid stackedXid = super.suspendStacked(xid);

        returnVal = doRollback(xid);

        super.resumeStacked(stackedXid);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINER,
                                   "OracleXAResource.rollback(): return status returnVal = "
                                           + returnVal, this);

            OracleLog.recursiveTrace = false;
        }

        checkError(returnVal);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.rollback(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected int doRollback(Xid xid) throws XAException {
        int returnVal = 0;
        int stateout = 0;
        CallableStatement cstmt = null;
        try {
            cstmt = this.connection.prepareCall(xa_rollback_post_816);

            cstmt.registerOutParameter(1, 2);
            cstmt.setInt(2, xid.getFormatId());
            cstmt.setBytes(3, xid.getGlobalTransactionId());
            cstmt.setBytes(4, xid.getBranchQualifier());

            cstmt.execute();

            returnVal = cstmt.getInt(1);
        } catch (SQLException s) {
            returnVal = s.getErrorCode();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER, "OracleXAResource.rollback() returnVal="
                        + returnVal, this);

                OracleLog.recursiveTrace = false;
            }

            if (returnVal == 0) {
                throw new XAException(-6);
            }

        } finally {
            try {
                if (cstmt != null)
                    cstmt.close();
            } catch (SQLException s) {
            }
            cstmt = null;
        }

        return returnVal;
    }

    public void doTwoPhaseAction(int nsites, int action, String[] dbnames, Xid[] xids)
            throws XAException {
        doDoTwoPhaseAction(nsites, action, dbnames, xids);
    }

    protected int doDoTwoPhaseAction(int nsites, int action, String[] dbnames, Xid[] xids)
            throws XAException {
        throw new XAException(-6);
    }

    private static byte[] getSerializedBytes(Xid xid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.getSerializedBytes(xid = " + xid
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        try {
            return Util.serializeObject(xid);
        } catch (IOException ioe) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINER,
                                       "OracleXAResource.getSerializedBytes() got exception: "
                                               + ioe);

                OracleLog.recursiveTrace = false;
            }

        }

        return null;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.client.OracleXAResource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.client.OracleXAResource JD-Core Version: 0.6.0
 */