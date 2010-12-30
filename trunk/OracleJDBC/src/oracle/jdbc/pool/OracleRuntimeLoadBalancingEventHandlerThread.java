package oracle.jdbc.pool;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;
import oracle.ons.Notification;
import oracle.ons.ONSException;
import oracle.ons.Subscriber;

class OracleRuntimeLoadBalancingEventHandlerThread extends Thread {
    private Notification event = null;
    private OracleConnectionCacheManager cacheManager = null;
    String m_service;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    OracleRuntimeLoadBalancingEventHandlerThread(String service) throws SQLException {
        this.m_service = service;

        this.cacheManager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
    }

    public void run() {
        Subscriber sub = null;

        final String type = "%\"eventType=database/event/servicemetrics/" + this.m_service + "\"";

        while (this.cacheManager.failoverEnabledCacheExists()) {
            try {
                sub = (Subscriber) AccessController
                        .doPrivileged(new PrivilegedExceptionAction() {

                            public Object run() throws ONSException {
                                return new Subscriber(type, "", 30000L);
                            }

                        });
            } catch (PrivilegedActionException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleRuntimeLoadBalancingEventHandlerThread.run()"
                                                     + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

            if (sub != null) {
                try {
                    while (this.cacheManager.failoverEnabledCacheExists()) {
                        if ((this.event = sub.receive(300000L)) != null)
                            handleEvent(this.event);
                    }
                } catch (ONSException e) {
                    sub.close();

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger.log(Level.FINER,
                                                 "OracleRuntimeLoadBalancingEventHandlerThread.run()"
                                                         + e, this);

                        OracleLog.recursiveTrace = false;
                    }

                }

            }

            try {
                Thread.currentThread();
                Thread.sleep(10000L);
            } catch (InterruptedException ea) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER,
                                             "OracleRuntimeLoadBalancingEventHandlerThread.runGot an InterruptedException"
                                                     + ea, this);

                    OracleLog.recursiveTrace = false;
                }
            }
        }
    }

    void handleEvent(Notification event) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, event.type() + ">", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.cacheManager.parseRuntimeLoadBalancingEvent(this.m_service, event == null ? null
                    : event.body());
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleRuntimeLoadBalancingEventHandlerThread.handleEvent()"
                                                 + e, this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleRuntimeLoadBalancingEventHandlerThread"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleRuntimeLoadBalancingEventHandlerThread JD-Core Version: 0.6.0
 */