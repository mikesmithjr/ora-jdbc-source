package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class OracleConnectionCacheTimeOutThread extends Thread implements Serializable {
    private OracleConnectionCacheImpl occi = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    OracleConnectionCacheTimeOutThread(OracleConnectionCacheImpl occimpl) throws SQLException {
        this.occi = occimpl;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheTimeOutThread(occimpl)",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void run() {
        long threadTTLTimeOut = 0L;
        long threadInactivityTimeOut = 0L;
        boolean timeToLive = true;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionCacheTimeOutThread:run()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            while (timeToLive) {
                if ((threadTTLTimeOut = this.occi.getCacheTimeToLiveTimeout()) > 0L) {
                    runTimeToLiveTimeOut(threadTTLTimeOut);
                }

                if ((threadInactivityTimeOut = this.occi.getCacheInactivityTimeout()) > 0L) {
                    runInactivityTimeOut(threadInactivityTimeOut);
                }

                sleep(this.occi.getThreadWakeUpInterval() * 1000L);

                if ((this.occi.cache != null)
                        && ((threadTTLTimeOut > 0L) || (threadInactivityTimeOut > 0L)))
                    continue;
                timeToLive = false;
            }

        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER, "OracleConnectionCacheTimeOutThread.run()"
                        + e, this);

                OracleLog.recursiveTrace = false;
            }

        } catch (InterruptedException ie) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER, "OracleConnectionCacheTimeOutThread.run()"
                        + ie, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    public void runTimeToLiveTimeOut(long threadTTLTimeOut) throws SQLException {
        long connCurrTime = 0L;
        long connStartTime = 0L;
        int activeCacheSize = 0;
        PooledConnection pooledConn = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER, "runTimeToLiveTimeOut():timeOut=<"
                    + threadTTLTimeOut + ">" + ", activeSize=<" + this.occi.getActiveSize() + ">",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        if ((activeCacheSize = this.occi.getActiveSize()) > 0) {
            Enumeration connSet = this.occi.activeCache.keys();

            while (connSet.hasMoreElements()) {
                pooledConn = (PooledConnection) connSet.nextElement();

                Connection conn = ((OraclePooledConnection) pooledConn).getLogicalHandle();

                if (conn != null) {
                    connStartTime = ((OracleConnection) conn).getStartTime();
                }
                connCurrTime = System.currentTimeMillis();

                if (connCurrTime - connStartTime > threadTTLTimeOut * 1000L) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger
                                .log(
                                     Level.FINER,
                                     "OracleConnectionCacheTimeOutThread.runTimeToLiveTimeOut():TTL Timeout expired... Executing cancel()/close()",
                                     this);

                        OracleLog.recursiveTrace = false;
                    }

                    try {
                        ((OracleConnection) conn).cancel();
                    } catch (SQLException e) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleConnectionCacheTimeOutThread.runTimeToLiveTimeout()"
                                                             + e, this);

                            OracleLog.recursiveTrace = false;
                        }

                    }

                    try {
                        if (!conn.getAutoCommit()) {
                            ((OracleConnection) conn).rollback();
                        }

                    } catch (SQLException e) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleConnectionCacheTimeOutThread.runTimeToLiveTimeout(): rollback"
                                                             + e, this);

                            OracleLog.recursiveTrace = false;
                        }

                    }

                    try {
                        conn.close();
                    } catch (SQLException e) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleConnectionCacheTimeOutThread.runTimeToLiveTimeout()"
                                                             + e, this);

                            OracleLog.recursiveTrace = false;
                        }

                    }

                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER,
                                     "OracleConnectionCacheTimeOutThread.runTimeToLiveTimeOut():activeCacheSize="
                                             + activeCacheSize + "TTLTimeOut=" + threadTTLTimeOut,
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void runInactivityTimeOut(long threadInactivityTimeOut) throws SQLException {
        long lastAccessedTime = 0L;
        long currentTime = 0L;
        long timeOut = threadInactivityTimeOut * 1000L;
        OraclePooledConnection pooledConn = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINER,
                                     "OracleConnectionCacheTimeOutThread.runInactivityTimeOut():threadInactivityTimeOut="
                                             + threadInactivityTimeOut, this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if ((this.occi.cache != null) && (this.occi.cache.size() > 0)
                    && (this.occi.cacheSize > this.occi._MIN_LIMIT)) {
                Enumeration pooledConnSet = this.occi.cache.elements();

                while ((this.occi.cacheSize > this.occi._MIN_LIMIT)
                        && (pooledConnSet.hasMoreElements())) {
                    pooledConn = (OraclePooledConnection) pooledConnSet.nextElement();
                    lastAccessedTime = pooledConn.getLastAccessedTime();
                    currentTime = System.currentTimeMillis();

                    if (currentTime - lastAccessedTime <= timeOut) {
                        continue;
                    }

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleConnectionCacheTimeOutThread.runInactivityTimeOut():PooledConnection in cache closed:"
                                                         + pooledConn, this);

                        OracleLog.recursiveTrace = false;
                    }

                    try {
                        this.occi.closeSingleConnection(pooledConn, false);
                    } catch (SQLException ea) {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.poolLogger.log(Level.FINER,
                                                     "OracleConnectionCacheTimeOutThread.runInactivityTimeout()"
                                                             + ea, this);

                            OracleLog.recursiveTrace = false;
                        }

                    }

                }

            }

        } catch (NoSuchElementException ne) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleConnectionCacheTimeOutThread.runInactivityTimeOut()"
                                                 + ne, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleConnectionCacheTimeOutThread"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleConnectionCacheTimeOutThread JD-Core Version: 0.6.0
 */