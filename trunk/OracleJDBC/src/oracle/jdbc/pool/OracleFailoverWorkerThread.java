package oracle.jdbc.pool;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

class OracleFailoverWorkerThread extends Thread {
    protected OracleImplicitConnectionCache implicitCache = null;
    protected int eventType = 0;
    protected String eventServiceName = null;
    protected String instanceNameKey = null;
    protected String databaseNameKey = null;
    protected String hostNameKey = null;
    protected String status = null;
    protected int cardinality = 0;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    OracleFailoverWorkerThread(OracleImplicitConnectionCache oicc, int eventType,
            String instNameKey, String dbUniqNameKey, String hostNameKey, String status, int card)
            throws SQLException {
        this.implicitCache = oicc;
        this.eventType = eventType;
        this.instanceNameKey = instNameKey;
        this.databaseNameKey = dbUniqNameKey;
        this.hostNameKey = hostNameKey;
        this.status = status;
        this.cardinality = card;
    }

    public void run() {
        try {
            if (this.status != null) {
                this.implicitCache.processFailoverEvent(this.eventType, this.instanceNameKey,
                                                        this.databaseNameKey, this.hostNameKey,
                                                        this.status, this.cardinality);
            }

        } catch (Exception ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.poolLogger.log(Level.FINER,
                                         "OracleImplicitConnectionCache.OracleFailoverWorkerThread()\neventType="
                                                 + this.eventType + "\ninstanceName="
                                                 + this.instanceNameKey + "\ndatabaseName="
                                                 + this.databaseNameKey + "\nhostName="
                                                 + this.hostNameKey + "\nstatus=" + this.status
                                                 + "\ncardinality=" + this.cardinality + "\n" + ex,
                                         this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleFailoverWorkerThread"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleFailoverWorkerThread JD-Core Version: 0.6.0
 */