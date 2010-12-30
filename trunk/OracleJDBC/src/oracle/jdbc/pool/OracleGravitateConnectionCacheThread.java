package oracle.jdbc.pool;

import java.sql.SQLException;
import oracle.jdbc.driver.OracleLog;

class OracleGravitateConnectionCacheThread extends Thread {
    protected OracleImplicitConnectionCache implicitCache = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    OracleGravitateConnectionCacheThread(OracleImplicitConnectionCache oicc) throws SQLException {
        this.implicitCache = oicc;
    }

    public void run() {
        this.implicitCache.gravitateCache();
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
 * oracle.jdbc.pool.OracleGravitateConnectionCacheThread JD-Core Version: 0.6.0
 */