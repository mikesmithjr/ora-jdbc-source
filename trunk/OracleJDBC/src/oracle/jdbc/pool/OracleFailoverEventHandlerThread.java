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

class OracleFailoverEventHandlerThread extends Thread {
    private Notification event = null;
    private OracleConnectionCacheManager cacheManager = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    OracleFailoverEventHandlerThread() throws SQLException {
        this.cacheManager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
    }

    public void run() {
        Subscriber sub = null;

        String eventStr = "(%\"eventType=database/event/service\")|(%\"eventType=database/event/host\")";

        while (this.cacheManager.failoverEnabledCacheExists()) {
            try {
                sub = (Subscriber) AccessController.doPrivileged(new PrivilegedExceptionAction() {
                    public Object run() throws ONSException {
                        return new Subscriber(
                                "(%\"eventType=database/event/service\")|(%\"eventType=database/event/host\")",
                                "", 30000L);
                    }

                });
            } catch (PrivilegedActionException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.poolLogger.log(Level.FINER, "OracleFailoverEventHandlerThread.run()"
                            + e, this);

                    OracleLog.recursiveTrace = false;
                }

            }

            if (sub != null) {
                try {
                    while (this.cacheManager.failoverEnabledCacheExists()) {
                        if ((this.event = sub.receive(true)) != null)
                            handleEvent(this.event);
                    }
                } catch (ONSException e) {
                    sub.close();

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.poolLogger
                                .log(Level.FINER, "OracleFailoverEventHandlerThread.run()" + e,
                                     this);

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
                                             "OracleFailoverEventHandlerThread.runGot an InterruptedException"
                                                     + ea, this);

                    OracleLog.recursiveTrace = false;
                }
            }
        }
    }

    void handleEvent(Notification event) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleFailoverEventHandlerThread.handleEvent():<"
                    + event.type() + ">", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            int eventType = 0;

            if (event.type().equalsIgnoreCase("database/event/service"))
                eventType = 256;
            else if (event.type().equalsIgnoreCase("database/event/host")) {
                eventType = 512;
            }
            if (eventType != 0) {
                this.cacheManager.verifyAndHandleEvent(eventType, event.body());
            }

        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger
                        .log(Level.FINER, "OracleFailoverEventHandlerThread.handleEvent()" + e,
                             this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleFailoverEventHandlerThread"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleFailoverEventHandlerThread JD-Core Version: 0.6.0
 */