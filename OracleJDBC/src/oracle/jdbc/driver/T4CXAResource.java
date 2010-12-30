// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T4CXAResource.java

package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import oracle.jdbc.xa.OracleXAConnection;
import oracle.jdbc.xa.OracleXid;
import oracle.jdbc.xa.client.OracleXADataSource;
import oracle.jdbc.xa.client.OracleXAResource;

// Referenced classes of package oracle.jdbc.driver:
// T4CConnection, T4CTTIOtxse, DatabaseError, T4CTTIOtxen,
// T4CTTIk2rpc, OracleLog

class T4CXAResource extends OracleXAResource {

    T4CConnection physicalConn;
    int applicationValueArr[];
    boolean isTransLoose;
    byte context[];
    int errorNumber[];
    private String password;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    T4CXAResource(T4CConnection _physicalConn, OracleXAConnection xaconn, boolean _isTransLoose)
            throws XAException {
        super(_physicalConn, xaconn);
        applicationValueArr = new int[1];
        isTransLoose = false;
        physicalConn = _physicalConn;
        isTransLoose = _isTransLoose;
        errorNumber = new int[1];
    }

    protected int doStart(Xid xid, int flag) throws XAException {
        int returnVal = -1;

        synchronized (this.physicalConn) {
            synchronized (this) {
                if (this.isTransLoose) {
                    flag |= 65536;
                }

                int swtch = flag & 0x8200000;

                if ((swtch == 134217728) && (OracleXid.isLocalTransaction(xid))) {
                    return 0;
                }

                this.applicationValueArr[0] = 0;
                try {
                    try {
                        T4CTTIOtxse otxse = this.physicalConn.otxse;
                        byte[] xidxid = null;
                        byte[] gtrid = xid.getGlobalTransactionId();
                        byte[] bqual = xid.getBranchQualifier();

                        int gtrid_l = 0;
                        int bqual_l = 0;

                        if ((gtrid != null) && (bqual != null)) {
                            gtrid_l = Math.min(gtrid.length, 64);
                            bqual_l = Math.min(bqual.length, 64);
                            xidxid = new byte['\u0080'];

                            System.arraycopy(gtrid, 0, xidxid, 0, gtrid_l);
                            System.arraycopy(bqual, 0, xidxid, gtrid_l, bqual_l);
                        }

                        int t4cflag = 0;

                        if (((flag & 0x200000) != 0) || ((flag & 0x8000000) != 0))
                            t4cflag |= 4;
                        else {
                            t4cflag |= 1;
                        }
                        if ((flag & 0x100) != 0) {
                            t4cflag |= 256;
                        }
                        if ((flag & 0x200) != 0) {
                            t4cflag |= 512;
                        }
                        if ((flag & 0x400) != 0) {
                            t4cflag |= 1024;
                        }
                        if ((flag & 0x10000) != 0) {
                            t4cflag |= 65536;
                        }
                        this.physicalConn.sendPiggyBackedMessages();
                        otxse.marshal(1, null, xidxid, xid.getFormatId(), gtrid_l, bqual_l,
                                      this.timeout, t4cflag);

                        byte[] ctx = otxse.receive(this.applicationValueArr);

                        if (ctx != null) {
                            this.context = ctx;
                        }
                        returnVal = 0;
                    } catch (IOException ioe) {
                        DatabaseError.throwSqlException(ioe);
                    }

                } catch (SQLException s) {
                    returnVal = s.getErrorCode();

                    if (returnVal == 0) {
                        throw new XAException(-6);
                    }

                }

            }

        }

        return returnVal;
    }

    protected int doEnd(Xid xid, int flag) throws XAException {
        int returnVal = -1;
        synchronized (physicalConn) {
            synchronized (this) {
                try {
                    try {
                        T4CTTIOtxse otxse = physicalConn.otxse;
                        byte xidxid[] = null;
                        byte gtrid[] = xid.getGlobalTransactionId();
                        byte bqual[] = xid.getBranchQualifier();
                        int gtrid_l = 0;
                        int bqual_l = 0;
                        if (gtrid != null && bqual != null) {
                            gtrid_l = Math.min(gtrid.length, 64);
                            bqual_l = Math.min(bqual.length, 64);
                            xidxid = new byte[128];
                            System.arraycopy(gtrid, 0, xidxid, 0, gtrid_l);
                            System.arraycopy(bqual, 0, xidxid, gtrid_l, bqual_l);
                        }
                        byte txctx[] = context;
                        int t4cflag = 0;
                        if (((flag & 2) == 2 || (flag & 0x100000) != 0x100000)
                                && (flag & 0x2000000) == 0x2000000) {
                            t4cflag = 0x100000;
                        }
                        physicalConn.sendPiggyBackedMessages();
                        otxse.marshal(2, txctx, xidxid, xid.getFormatId(), gtrid_l, bqual_l,
                                      timeout, t4cflag);
                        byte ctx[] = otxse.receive(applicationValueArr);
                        if (ctx != null) {
                            context = ctx;
                        }
                        returnVal = 0;
                    } catch (IOException ioe) {
                        DatabaseError.throwSqlException(ioe);
                    }
                } catch (SQLException s) {
                    returnVal = s.getErrorCode();
                    if (returnVal == 0) {
                        throw new XAException(-6);
                    }
                }
            }
        }
        return returnVal;
    }

    protected int doCommit(Xid xid, int cPhase) throws XAException {
        int returnVal = -1;
        synchronized (physicalConn) {
            synchronized (this) {
                int command = cPhase != 1 ? 2 : 4;
                returnVal = doTransaction(xid, 1, command);
                if (cPhase == 1 && (returnVal == 2 || returnVal == 4)) {
                    returnVal = 0;
                } else if (cPhase != 1 && returnVal == 5) {
                    returnVal = 0;
                } else if (returnVal == 8) {
                    throw new XAException(106);
                }
                if (returnVal == 24756) {
                    returnVal = kputxrec(xid, 1, timeout + 120);
                } else if (returnVal == 24780) {
                    OracleXADataSource oxds = null;
                    XAConnection pc = null;
                    try {
                        oxds = new OracleXADataSource();
                        oxds.setURL(physicalConn.url);
                        oxds.setUser(physicalConn.user);
                        physicalConn.getPasswordInternal(this);
                        oxds.setPassword(password);
                        pc = oxds.getXAConnection();
                        XAResource oxar = pc.getXAResource();
                        oxar.commit(xid, cPhase == 1);
                        returnVal = 0;
                    } catch (SQLException e) {
                        throw new XAException(-6);
                    } finally {
                        try {
                            if (pc != null) {
                                pc.close();
                            }
                            if (oxds != null) {
                                oxds.close();
                            }
                        } catch (Exception ee) {
                        }
                    }
                }
            }
        }
        return returnVal;
    }

    protected int doPrepare(Xid xid) throws XAException {
        int returnVal = -1;

        synchronized (this.physicalConn) {
            synchronized (this) {
                returnVal = doTransaction(xid, 3, 0);

                if (returnVal == 8) {
                    throw new XAException(106);
                }
                if (returnVal == 4) {
                    returnVal = 24767;
                } else if (returnVal == 1) {
                    returnVal = 0;
                } else if (returnVal == 3) {
                    returnVal = 24761;
                } else {
                    if ((returnVal == 2054) || (returnVal == 24756)) {
                        return returnVal;
                    }

                    returnVal = 24768;
                }

            }

        }

        return returnVal;
    }

    protected int doForget(Xid xid) throws XAException {
        int returnVal = -1;
        int e = 0;

        synchronized (this.physicalConn) {
            synchronized (this) {
                try {
                    if (OracleXid.isLocalTransaction(xid)) {
                        int i = 24771;
                        return i;
                    }
                    int resumeReturn = doStart(xid, 134217728);

                    if (resumeReturn != 24756) {
                        if (resumeReturn == 0) {
                            try {
                                doEnd(xid, 0);
                            } catch (Exception ex) {
                            }

                        }

                        if ((resumeReturn == 0) || (resumeReturn == 2079)
                                || (resumeReturn == 24754) || (resumeReturn == 24761)
                                || (resumeReturn == 24774) || (resumeReturn == 24776)
                                || (resumeReturn == 25351)) {
                            e = 24769;
                            return e;
                        }
                        if (resumeReturn == 24752) {
                            e = 24771;
                            return e;
                        }
                        e = resumeReturn;
                        return e;
                    }
                    returnVal = kputxrec(xid, 4, 1);
                } finally {
                }
            }
        }

        return returnVal;
    }

    protected int doRollback(Xid xid) throws XAException {
        int returnVal = -1;
        synchronized (physicalConn) {
            synchronized (this) {
                returnVal = doTransaction(xid, 2, 3);
                if (returnVal == 8) {
                    throw new XAException(106);
                }
                if (returnVal == 3 || returnVal == 25402) {
                    returnVal = 0;
                }
                if (returnVal == 24756) {
                    returnVal = kputxrec(xid, 2, timeout + 120);
                } else if (returnVal == 24780) {
                    OracleXADataSource oxds = null;
                    XAConnection pc = null;
                    try {
                        oxds = new OracleXADataSource();
                        oxds.setURL(physicalConn.url);
                        oxds.setUser(physicalConn.user);
                        physicalConn.getPasswordInternal(this);
                        oxds.setPassword(password);
                        pc = oxds.getXAConnection();
                        XAResource oxar = pc.getXAResource();
                        oxar.rollback(xid);
                        returnVal = 0;
                    } catch (SQLException e) {
                        throw new XAException(-6);
                    } finally {
                        try {
                            if (pc != null) {
                                pc.close();
                            }
                            if (oxds != null) {
                                oxds.close();
                            }
                        } catch (Exception ee) {
                        }
                    }
                }
            }
        }
        return returnVal;
    }

    int doTransaction(Xid xid, int mode, int command) throws XAException {
        int returnVal = -1;
        try {
            try {
                T4CTTIOtxen otxen = physicalConn.otxen;
                byte xidxid[] = null;
                byte gtrid[] = xid.getGlobalTransactionId();
                byte bqual[] = xid.getBranchQualifier();
                int gtrid_l = 0;
                int bqual_l = 0;
                if (gtrid != null && bqual != null) {
                    gtrid_l = Math.min(gtrid.length, 64);
                    bqual_l = Math.min(bqual.length, 64);
                    xidxid = new byte[128];
                    System.arraycopy(gtrid, 0, xidxid, 0, gtrid_l);
                    System.arraycopy(bqual, 0, xidxid, gtrid_l, bqual_l);
                }
                byte txctx[] = context;
                physicalConn.sendPiggyBackedMessages();
                otxen.marshal(mode, txctx, xidxid, xid.getFormatId(), gtrid_l, bqual_l, timeout,
                              command);
                returnVal = otxen.receive(errorNumber);
            } catch (IOException ioe) {
                DatabaseError.throwSqlException(ioe);
            }
        } catch (SQLException s) {
            errorNumber[0] = s.getErrorCode();
        }
        if (errorNumber[0] == 0) {
            throw new XAException(-6);
        }
        if (returnVal == -1) {
            returnVal = errorNumber[0];
        }
        return returnVal;
    }

    protected int kputxrec(Xid xid, int opcode, int tries) throws XAException {
        int rtnst = 0;
        int command;
        switch (opcode) {
        case 1: // '\001'
            command = 3;
            break;

        case 4: // '\004'
            command = 2;
            break;

        default:
            command = 0;
            break;
        }
        int endstate = 0;
        do {
            if (tries-- <= 0) {
                break;
            }
            endstate = doTransaction(xid, 5, command);
            if (endstate != 7) {
                break;
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception e) {
            }
        } while (true);
        if (endstate == 7) {
            return 24763;
        }
        if (endstate == 24756) {
            return 24756;
        }
        int incmd;
        switch (endstate) {
        case 3: // '\003'
            if (opcode == 1) {
                incmd = 7;
            } else {
                incmd = 8;
                rtnst = -24762;
            }
            break;

        case 0: // '\0'
            if (opcode == 4) {
                incmd = 8;
                rtnst = 24762;
            } else {
                incmd = 7;
                rtnst = opcode != 1 ? 0 : 24756;
            }
            break;

        case 2: // '\002'
            if (opcode == 4) {
                incmd = 8;
                rtnst = 24770;
                break;
            }
            // fall through

        case 1: // '\001'
        default:
            int heuristic[] = new int[1];
            rtnst = kpuho2oc(endstate, heuristic);
            if (opcode == 4 && heuristic[0] == 1) {
                incmd = 7;
                rtnst = 0;
            } else {
                incmd = 8;
            }
            break;
        }
        T4CTTIk2rpc k2rpc = physicalConn.k2rpc;
        try {
            k2rpc.marshal(3, incmd);
            k2rpc.receive();
        } catch (IOException ioe) {
            throw new XAException(-7);
        } catch (SQLException s) {
            throw new XAException(-6);
        }
        return rtnst;
    }

    int kpuho2oc(int state, int heuristic[]) {
        int rtnst = 0;
        switch (state) {
        case 5: // '\005'
            heuristic[0] = 1;
            rtnst = 24764;
            break;

        case 4: // '\004'
            heuristic[0] = 1;
            rtnst = 24765;
            break;

        case 6: // '\006'
            heuristic[0] = 1;
            rtnst = 24766;
            break;

        case 0: // '\0'
        case 1: // '\001'
        case 2: // '\002'
        case 3: // '\003'
        case 7: // '\007'
        case 8: // '\b'
        case 9: // '\t'
        case 10: // '\n'
        case 11: // '\013'
        case 12: // '\f'
        case 13: // '\r'
        default:
            rtnst = 24762;
            break;
        }
        return rtnst;
    }

    final void setPasswordInternal(String p) {
        password = p;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CXAResource"));
        } catch (Exception e) {
        }
    }
}
