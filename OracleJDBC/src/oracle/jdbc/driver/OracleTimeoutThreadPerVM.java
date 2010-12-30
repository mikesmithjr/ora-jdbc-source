package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class OracleTimeoutThreadPerVM extends OracleTimeout {
    private static final OracleTimeoutPollingThread watchdog = new OracleTimeoutPollingThread();
    private OracleStatement statement;
    private long interruptAfter;
    private String name;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    OracleTimeoutThreadPerVM(String name) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleTimeoutThreadPerVM(" + name + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.name = name;
        this.interruptAfter = 9223372036854775807L;

        watchdog.addTimeout(this);
    }

    void close() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleTimeoutThreadPerVM.close<" + this.name
                    + ">()", this);

            OracleLog.recursiveTrace = false;
        }

        watchdog.removeTimeout(this);
    }

    synchronized void setTimeout(long milliseconds, OracleStatement stmt) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleTimeoutThreadPerVM.setTimeout<"
                    + this.name + ">(" + milliseconds + ", " + stmt + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.interruptAfter != 9223372036854775807L) {
            DatabaseError.throwSqlException(131);
        }

        this.statement = stmt;
        this.interruptAfter = (System.currentTimeMillis() + milliseconds);
    }

    synchronized void cancelTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleTimeoutThreadPerVM.cancelTimeout<"
                    + this.name + ">()", this);

            OracleLog.recursiveTrace = false;
        }

        this.statement = null;
        this.interruptAfter = 9223372036854775807L;
    }

    void interruptIfAppropriate(long now) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "OracleTimeoutThreadPerVM.interruptIfAppropriate<"
                                               + this.name + ">(" + now + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (now > this.interruptAfter) {
            synchronized (this) {
                if (now > this.interruptAfter) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.SEVERE,
                                                   "OracleTimeoutThreadPerVM.interruptIfAppropriate<"
                                                           + this.name + ">:INTERRUPTING", this);

                        OracleLog.recursiveTrace = false;
                    }

                    if (this.statement.connection.spawnNewThreadToCancel) {
                        final OracleStatement s = this.statement;
                        Thread t = new Thread(new Runnable() {

                            public void run() {
                                try {
                                    s.cancel();
                                } catch (Throwable e) {
                                    if ((OracleTimeoutThreadPerVM.TRACE)
                                            && (!OracleLog.recursiveTrace)) {
                                        OracleLog.recursiveTrace = true;
                                        OracleLog.driverLogger.log(Level.SEVERE,
                                                                   "OracleTimeoutThreadPerVM.interruptIfAppropriate(spawned thread) failed: "
                                                                           + e, s);

                                        OracleLog.recursiveTrace = false;
                                    }
                                }
                            }
                        });
                        t.setName("interruptIfAppropriate_" + this);
                        t.setDaemon(true);
                        t.setPriority(10);
                        t.start();
                    } else {
                        try {
                            this.statement.cancel();
                        } catch (Throwable e) {
                            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.driverLogger.log(Level.SEVERE,
                                                           "OracleTimeoutThreadPerVM.interruptIfAppropriate failed: "
                                                                   + e, this.statement);

                                OracleLog.recursiveTrace = false;
                            }
                        }

                    }

                    this.statement = null;
                    this.interruptAfter = 9223372036854775807L;
                }
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleTimeoutThreadPerVM"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleTimeoutThreadPerVM JD-Core Version: 0.6.0
 */