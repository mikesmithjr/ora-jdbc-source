package oracle.jdbc.xa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public abstract class OracleXAResource implements XAResource {
    public static final int XA_OK = 0;
    public static final short DEFAULT_XA_TIMEOUT = 60;
    protected boolean savedConnectionAutoCommit = false;
    protected boolean savedXAConnectionAutoCommit = false;
    public static final int TMNOFLAGS = 0;
    public static final int TMNOMIGRATE = 2;
    public static final int TMENDRSCAN = 8388608;
    public static final int TMFAIL = 536870912;
    public static final int TMMIGRATE = 1048576;
    public static final int TMJOIN = 2097152;
    public static final int TMONEPHASE = 1073741824;
    public static final int TMRESUME = 134217728;
    public static final int TMSTARTRSCAN = 16777216;
    public static final int TMSUCCESS = 67108864;
    public static final int TMSUSPEND = 33554432;
    public static final int ORATMREADONLY = 256;
    public static final int ORATMREADWRITE = 512;
    public static final int ORATMSERIALIZABLE = 1024;
    public static final int ORAISOLATIONMASK = 65280;
    public static final int ORATRANSLOOSE = 65536;
    protected Connection connection = null;
    protected OracleXAConnection xaconnection = null;
    protected int timeout = 60;
    protected String dblink = null;

    private Connection logicalConnection = null;

    private String synchronizeBeforeRecover = "BEGIN sys.dbms_system.dist_txn_sync(0); END;";

    private String recoverySqlRows = "SELECT formatid, globalid, branchid FROM SYS.DBA_PENDING_TRANSACTIONS";

    protected Vector locallySuspendedTransactions = new Vector();

    protected boolean canBeMigratablySuspended = false;

    protected Xid currentStackedXid = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAResource() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.OracleXAResource()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXAResource(Connection pm_conn, OracleXAConnection xaconn) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "oracle.jdbc.xa.OracleXAResource(pm_conn = "
                    + pm_conn + ", xaconn = " + xaconn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = pm_conn;
        this.xaconnection = xaconn;

        if (this.connection == null)
            throw new XAException(-7);
    }

    public synchronized void setConnection(Connection pm_conn) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.setConnection(pm_conn = "
                    + pm_conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = pm_conn;

        if (this.connection == null)
            throw new XAException(-7);
    }

    protected void push(Xid xid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.push(xid = " + xid
                    + "): stacking xid", this);

            OracleLog.recursiveTrace = false;
        }

        this.currentStackedXid = xid;

        enterGlobalTxnMode();
    }

    protected void pop() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.pop(): clearing stacked xid "
                    + this.currentStackedXid, this);

            OracleLog.recursiveTrace = false;
        }

        this.currentStackedXid = null;

        exitGlobalTxnMode();
    }

    protected Xid suspendStacked(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAResource.suspendStacked(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Xid stackedXid = null;

        if ((this.currentStackedXid != null) && (this.currentStackedXid != xid)) {
            stackedXid = this.currentStackedXid;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.FINER,
                             "OracleXAResource.suspendStacked(xid): Before calling end()", this);

                OracleLog.recursiveTrace = false;
            }

            end(stackedXid, 33554432);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.suspendStacked(xid) return: "
                    + stackedXid, this);

            OracleLog.recursiveTrace = false;
        }

        return stackedXid;
    }

    protected Xid suspendStacked(Xid xid, int endFlags) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.suspendStacked(xid = " + xid
                    + ", endFlags = " + Integer.toHexString(endFlags) + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Xid stackedXid = null;

        allowGlobalTxnModeOnly(-3);

        if ((endFlags == 67108864) && (this.currentStackedXid != null)
                && (xid != this.currentStackedXid)) {
            stackedXid = this.currentStackedXid;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.FINER,
                             "OracleXAResource.suspendStacked(xid, endFlags): Before end()", this);

                OracleLog.recursiveTrace = false;
            }

            end(stackedXid, 33554432);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.FINER,
                             "OracleXAResource.suspendStacked(xid, endFlags): Before start()", this);

                OracleLog.recursiveTrace = false;
            }

            start(xid, 134217728);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "suspendStacked(xid, endFlags) return: "
                    + stackedXid, this);

            OracleLog.recursiveTrace = false;
        }

        return stackedXid;
    }

    protected void resumeStacked(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "resumeStacked(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (xid != null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.FINE,
                             "OracleXAResource.resumeStacked(xid): Before calling start()", this);

                OracleLog.recursiveTrace = false;
            }

            start(xid, 134217728);
        }
    }

    public abstract void start(Xid paramXid, int paramInt) throws XAException;

    public abstract void end(Xid paramXid, int paramInt) throws XAException;

    public abstract void commit(Xid paramXid, boolean paramBoolean) throws XAException;

    public abstract int prepare(Xid paramXid) throws XAException;

    public abstract void forget(Xid paramXid) throws XAException;

    public abstract void rollback(Xid paramXid) throws XAException;

    public Xid[] recover(int flag) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.recover(flag = " + flag + ")",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        if ((flag & 0x1800000) != flag) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAResource.recover(flag): XAER_INVAL, invalid flag",
                                       this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-5);
        }

        Statement stmt = null;
        ResultSet rset = null;
        ArrayList xidCollection = new ArrayList(50);
        try {
            stmt = this.connection.createStatement();
            stmt.execute(this.synchronizeBeforeRecover);

            rset = stmt.executeQuery(this.recoverySqlRows);

            while (rset.next()) {
                xidCollection
                        .add(new OracleXid(rset.getInt(1), rset.getBytes(2), rset.getBytes(3)));
            }

        } catch (SQLException sqe) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAResource.recover(flag): SQLException(internal) "
                                               + sqe, this);

                OracleLog.recursiveTrace = false;
            }

            throw new XAException(-3);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rset != null)
                    rset.close();
            } catch (Exception ignore) {
            }
        }
        int xidSize = xidCollection.size();
        Xid[] xids = new Xid[xidSize];
        System.arraycopy(xidCollection.toArray(), 0, xids, 0, xidSize);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.recover(flag) return: " + xids,
                                   this);

            OracleLog.recursiveTrace = false;
        }

        return xids;
    }

    protected void restoreAutoCommitModeForGlobalTransaction() throws XAException {
        if ((this.savedConnectionAutoCommit)
                && (((OracleConnection) this.connection).getTxnMode() != 1)) {
            try {
                this.connection.setAutoCommit(this.savedConnectionAutoCommit);
                this.xaconnection.setAutoCommit(this.savedXAConnectionAutoCommit);
            } catch (SQLException ignoreException) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.FINE,
                                           "OracleXAResource.restoreAutoCommitModeForGlobalTransaction got exception: "
                                                   + ignoreException, this);

                    OracleLog.recursiveTrace = false;
                }
            }
        }
    }

    protected void saveAndAlterAutoCommitModeForGlobalTransaction() throws XAException {
        try {
            this.savedConnectionAutoCommit = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);
            this.savedXAConnectionAutoCommit = this.xaconnection.getAutoCommit();
            this.xaconnection.setAutoCommit(false);
        } catch (SQLException ignoreException) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINE,
                                       "OracleXAResource.saveAndAlterAutoCommitModeForGlobalTransaction got exception: "
                                               + ignoreException, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    public void resume(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.resume(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        start(xid, 134217728);
    }

    public void join(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.join(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        start(xid, 2097152);
    }

    public void suspend(Xid xid) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.suspend(xid = " + xid + ")", this);

            OracleLog.recursiveTrace = false;
        }

        end(xid, 33554432);
    }

    public void join(Xid xid, int timeout) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.join(xid = " + xid
                    + ", timeout = " + timeout + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.timeout = timeout;

        start(xid, 2097152);
    }

    public void resume(Xid xid, int timeout) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.resume(xid = " + xid
                    + ", timeout = " + timeout + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.timeout = timeout;

        start(xid, 134217728);
    }

    public Connection getConnection() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.getConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.getConnection() return: "
                    + this.connection, this);

            OracleLog.recursiveTrace = false;
        }

        return this.connection;
    }

    public int getTransactionTimeout() throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.getTransactionTimeout()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.getTransactionTimeout() return: "
                    + this.timeout, this);

            OracleLog.recursiveTrace = false;
        }

        return this.timeout;
    }

    public boolean isSameRM(XAResource xares) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.isSameRM(xares = " + xares + ")",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn1 = null;

        if ((xares instanceof OracleXAResource)) {
            conn1 = ((OracleXAResource) xares).getConnection();
        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.FINE,
                                       "OracleXAResource.isSameRM(xares) return: false", this);

                OracleLog.recursiveTrace = false;
            }

            return false;
        }

        try {
            String l_url = ((OracleConnection) this.connection).getURL();
            String l_prt = ((OracleConnection) this.connection).getProtocolType();

            if (conn1 != null) {
                boolean _returnVal = (conn1.equals(this.connection))
                        || (((OracleConnection) conn1).getURL().equals(l_url))
                        || ((((OracleConnection) conn1).getProtocolType().equals(l_prt)) && (l_prt
                                .equals("kprb")));

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.isSameRM(xares) return: "
                            + _returnVal, this);

                    OracleLog.recursiveTrace = false;
                }

                return _returnVal;
            }

        } catch (SQLException sqe) {
            throw new XAException(-3);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.isSameRM(xares) return: false",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean setTransactionTimeout(int seconds) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXAResource.setTransactionTimeout(seconds = "
                    + seconds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (seconds < 0) {
            throw new XAException(-5);
        }
        this.timeout = seconds;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAResource.setTransactionTimeout(seconds) return: true",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public String getDBLink() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.getDBLink()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.getDBLink() return: "
                    + this.dblink, this);

            OracleLog.recursiveTrace = false;
        }

        return this.dblink;
    }

    public void setDBLink(String dblink) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.setDBLink(dblink = " + dblink
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.dblink = dblink;
    }

    public void setLogicalConnection(Connection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAResource.setLogicalConnection(conn = "
                    + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.logicalConnection = conn;
    }

    protected void allowGlobalTxnModeOnly(int errorCode) throws XAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAResource.allowGlobalTxnModeOnly(errorCode = "
                                           + errorCode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (((OracleConnection) this.connection).getTxnMode() != 1) {
            throw new XAException(errorCode);
        }
    }

    protected void exitGlobalTxnMode() {
        ((OracleConnection) this.connection).setTxnMode(0);
    }

    protected void enterGlobalTxnMode() {
        ((OracleConnection) this.connection).setTxnMode(1);
    }

    protected void checkError(int error) throws OracleXAException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "OracleXAResource.checkError(error = " + error + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((error & 0xFFFF) != 0)
            throw new OracleXAException(error);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.OracleXAResource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.OracleXAResource JD-Core Version: 0.6.0
 */