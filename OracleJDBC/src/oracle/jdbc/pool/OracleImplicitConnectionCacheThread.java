// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: OracleImplicitConnectionCacheThread.java

package oracle.jdbc.pool;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

// Referenced classes of package oracle.jdbc.pool:
// OraclePooledConnection, OracleImplicitConnectionCache, OracleConnectionCacheCallback

class OracleImplicitConnectionCacheThread extends Thread {

    private OracleImplicitConnectionCache implicitCache;
    protected boolean timeToLive;
    protected boolean isSleeping;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    OracleImplicitConnectionCacheThread(OracleImplicitConnectionCache oicc) throws SQLException {
        implicitCache = null;
        timeToLive = true;
        isSleeping = false;
        implicitCache = oicc;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleImplicitConnectionCacheThread(oicc)", this);
            OracleLog.recursiveTrace = false;
        }
    }

    public void run() {
        long threadTTLTimeout = 0L;
        long threadInactivityTimeout = 0L;
        long threadAbandonedTimeout = 0L;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleImplicitConnectionCacheThread:run()", this);
            OracleLog.recursiveTrace = false;
        }
        do {
            if (!timeToLive) {
                break;
            }
            try {
                if (timeToLive
                        && (threadTTLTimeout = implicitCache.getCacheTimeToLiveTimeout()) > 0L) {
                    runTimeToLiveTimeout(threadTTLTimeout);
                }
                if (timeToLive
                        && (threadInactivityTimeout = implicitCache.getCacheInactivityTimeout()) > 0L) {
                    runInactivityTimeout();
                }
                if (timeToLive
                        && (threadAbandonedTimeout = implicitCache.getCacheAbandonedTimeout()) > 0L) {
                    runAbandonedTimeout(threadAbandonedTimeout);
                }
                if (timeToLive) {
                    isSleeping = true;
                    try {
                        sleep(implicitCache.getCachePropertyCheckInterval() * 1000);
                    } catch (InterruptedException ie) {
                        if (TRACE && !OracleLog.recursiveTrace) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleImplicitConnectionCacheThread.run()"
                                                             + ie, this);
                            OracleLog.recursiveTrace = false;
                        }
                    }
                    isSleeping = false;
                }
                if (implicitCache == null || threadTTLTimeout <= 0L
                        && threadInactivityTimeout <= 0L && threadAbandonedTimeout <= 0L) {
                    timeToLive = false;
                }
            } catch (SQLException e) {
                if (TRACE && !OracleLog.recursiveTrace) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleImplicitConnectionCacheThread.run()" + e, this);
                    OracleLog.recursiveTrace = false;
                }
            }
        } while (true);
    }

    private void runTimeToLiveTimeout(long threadTTLTimeout) throws SQLException {
        long connCurrTime = 0L;
        long connStartTime = 0L;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "runTimeToLiveTimeout():timeout=<"
                    + threadTTLTimeout + ">" + ", activeSize=<"
                    + implicitCache.getNumberOfCheckedOutConnections() + ">", this);
            OracleLog.recursiveTrace = false;
        }
        if (implicitCache.getNumberOfCheckedOutConnections() > 0) {
            OraclePooledConnection pc = null;
            synchronized (implicitCache) {
                Object ObjArr[] = implicitCache.checkedOutConnectionList.toArray();
                int sz = implicitCache.checkedOutConnectionList.size();
                for (int k = 0; k < sz; k++) {
                    pc = (OraclePooledConnection) ObjArr[k];
                    java.sql.Connection conn = pc.getLogicalHandle();
                    if (conn == null) {
                        continue;
                    }
                    connStartTime = ((OracleConnection) conn).getStartTime();
                    connCurrTime = System.currentTimeMillis();
                    if (connCurrTime - connStartTime <= threadTTLTimeout * 1000L) {
                        continue;
                    }
                    if (TRACE && !OracleLog.recursiveTrace) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger
                                .log(
                                     Level.FINER,
                                     "OracleImplicitConnectionCacheThread.runTimeToLiveTimeout():TTL Timeout expired... Executing cancel()/close()",
                                     this);
                        OracleLog.recursiveTrace = false;
                    }
                    try {
                        implicitCache.closeCheckedOutConnection(pc, true);
                        continue;
                    } catch (SQLException e) {
                        if (TRACE && !OracleLog.recursiveTrace) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleImplicitConnectionCacheThread.runTimeToLiveTimeout()"
                                                             + e, this);
                            OracleLog.recursiveTrace = false;
                        }
                    }
                }

            }
        }
    }

    private void runInactivityTimeout() {
        try {
            OracleImplicitConnectionCache _tmp = implicitCache;
            implicitCache.doForEveryCachedConnection(4);
        } catch (SQLException e) {
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCacheThread.runInactivityTimeout()"
                                                 + e, this);
                OracleLog.recursiveTrace = false;
            }
        }
    }

    private void runAbandonedTimeout(long abandonedTimeout) throws SQLException {
        if (implicitCache.getNumberOfCheckedOutConnections() > 0) {
            OraclePooledConnection pc = null;
            synchronized (implicitCache) {
                Object ObjArr[] = implicitCache.checkedOutConnectionList.toArray();
                for (int k = 0; k < ObjArr.length; k++) {
                    pc = (OraclePooledConnection) ObjArr[k];
                    OracleConnection conn = (OracleConnection) pc.getLogicalHandle();
                    if (conn == null) {
                        continue;
                    }
                    OracleConnectionCacheCallback occc = conn.getConnectionCacheCallbackObj();
                    if (occc != null
                            && (conn.getConnectionCacheCallbackFlag() == 4 || conn
                                    .getConnectionCacheCallbackFlag() == 1)) {
                        try {
                            occc.handleAbandonedConnection(conn, conn
                                    .getConnectionCacheCallbackPrivObj());
                            continue;
                        } catch (SQLException e) {
                            if (TRACE && !OracleLog.recursiveTrace) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.poolLogger.log(Level.FINER,
                                                         "OracleImplicitConnectionCacheThread.runAbandonedTimeout()"
                                                                 + e, this);
                                OracleLog.recursiveTrace = false;
                            }
                        }
                        continue;
                    }
                    if ((long) (conn.getHeartbeatNoChangeCount() * implicitCache
                            .getCachePropertyCheckInterval()) <= abandonedTimeout) {
                        continue;
                    }
                    if (TRACE && !OracleLog.recursiveTrace) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger
                                .log(
                                     Level.FINER,
                                     "OracleImplicitConnectionCacheThread.runAbandonedTimeout():Abandoned Timeout expired...Executing cancel()/close()",
                                     this);
                        OracleLog.recursiveTrace = false;
                    }
                    try {
                        implicitCache.closeCheckedOutConnection(pc, true);
                        continue;
                    } catch (SQLException e) {
                        if (TRACE && !OracleLog.recursiveTrace) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleImplicitConnectionCacheThread.runAbandonedTimeout()"
                                                             + e, this);
                            OracleLog.recursiveTrace = false;
                        }
                    }
                }

            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleImplicitConnectionCacheThread"));
        } catch (Exception e) {
        }
    }
}
