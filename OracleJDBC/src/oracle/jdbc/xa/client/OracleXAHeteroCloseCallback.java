package oracle.jdbc.xa.client;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleCloseCallback;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class OracleXAHeteroCloseCallback implements OracleCloseCallback {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public synchronized void beforeClose(OracleConnection conn, Object obj) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroCloseCallback.beforeClose(conn = "
                    + conn + ", obj = " + obj + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void afterClose(Object privData) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroCloseCallback.afterClose(privData = "
                    + privData + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int rmid = ((OracleXAHeteroConnection) privData).getRmid();
        String xaCloseString = ((OracleXAHeteroConnection) privData).getXaCloseString();
        try {
            int status = doXaClose(xaCloseString, rmid, 0, 0);

            if (status != 0) {
                DatabaseError.throwSqlException(-1 * status);
            }

        } catch (SQLException sqlexc) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "OracleXAHeteroCloseCallback.afterClose(): SQLException: "
                                               + sqlexc.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXAHeteroCloseCallback.afterClose() return",
                                   this);

            OracleLog.recursiveTrace = false;
        }
    }

    private native int doXaClose(String paramString, int paramInt1, int paramInt2, int paramInt3);

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.client.OracleXAHeteroCloseCallback"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.client.OracleXAHeteroCloseCallback JD-Core Version: 0.6.0
 */