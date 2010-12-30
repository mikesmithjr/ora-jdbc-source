package oracle.jdbc.driver;

import java.util.logging.Level;
import java.util.logging.Logger;

class OracleTimeoutPollingThread extends Thread {
    protected static final String threadName = "OracleTimeoutPollingThread";
    public static final String pollIntervalProperty = "oracle.jdbc.TimeoutPollInterval";
    public static final String pollIntervalDefault = "1000";
    private OracleTimeoutThreadPerVM[] knownTimeouts;
    private int count;
    private long sleepMillis;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    public OracleTimeoutPollingThread() {
        super("OracleTimeoutPollingThread");

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleTimeoutPollingThread.OracleTimeoutPollingThread()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        setDaemon(true);
        setPriority(10);

        this.knownTimeouts = new OracleTimeoutThreadPerVM[2];
        this.count = 0;
        this.sleepMillis = Long.parseLong(OracleDriver.getSystemPropertyPollInterval());

        start();
    }

    public synchronized void addTimeout(OracleTimeoutThreadPerVM t) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleTimeoutPollingThread.addTimeout(" + t
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int i = 0;

        if (this.count >= this.knownTimeouts.length) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINE,
                                           "OracleTimeoutPollingThread.addTimeout:growing knownTimeouts--new size = "
                                                   + this.knownTimeouts.length * 4, this);

                OracleLog.recursiveTrace = false;
            }

            OracleTimeoutThreadPerVM[] bigger = new OracleTimeoutThreadPerVM[this.knownTimeouts.length * 4];

            System.arraycopy(this.knownTimeouts, 0, bigger, 0, this.knownTimeouts.length);

            i = this.knownTimeouts.length;
            this.knownTimeouts = bigger;
        }

        for (; i < this.knownTimeouts.length; i++) {
            if (this.knownTimeouts[i] != null)
                continue;
            this.knownTimeouts[i] = t;
            this.count += 1;

            if ((!TRACE) || (OracleLog.recursiveTrace))
                break;
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleTimeoutPollingThread.addTimeout:added at " + i, this);

            OracleLog.recursiveTrace = false;
            break;
        }
    }

    public synchronized void removeTimeout(OracleTimeoutThreadPerVM t) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleTimeoutPollingThread.removeTimeout(" + t
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        for (int i = 0; i < this.knownTimeouts.length; i++) {
            if (this.knownTimeouts[i] != t)
                continue;
            this.knownTimeouts[i] = null;
            this.count -= 1;

            if ((!TRACE) || (OracleLog.recursiveTrace))
                break;
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "OracleTimeoutPollingThread.removeTimeout:removed from " + i,
                         this);

            OracleLog.recursiveTrace = false;
            break;
        }
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(this.sleepMillis);
            } catch (InterruptedException e) {
            }

            pollOnce();
        }
    }

    private void pollOnce() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OracleTimeoutPollingThread.pollOnce()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.count > 0) {
            long now = System.currentTimeMillis();

            for (int i = 0; i < this.knownTimeouts.length; i++) {
                try {
                    if (this.knownTimeouts[i] != null)
                        this.knownTimeouts[i].interruptIfAppropriate(now);
                } catch (NullPointerException e) {
                }
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleTimeoutPollingThread"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleTimeoutPollingThread JD-Core Version: 0.6.0
 */