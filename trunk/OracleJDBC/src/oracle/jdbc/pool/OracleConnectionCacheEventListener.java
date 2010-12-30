package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.OracleLog;

class OracleConnectionCacheEventListener implements ConnectionEventListener, Serializable {
    static final int CONNECTION_CLOSED_EVENT = 101;
    static final int CONNECTION_ERROROCCURED_EVENT = 102;
    protected OracleImplicitConnectionCache implicitCache = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    public OracleConnectionCacheEventListener() {
        this(null);
    }

    public OracleConnectionCacheEventListener(OracleImplicitConnectionCache icc) {
        this.implicitCache = icc;
    }

    public synchronized void connectionClosed(ConnectionEvent ce) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionEventListener.connectionClosed("
                    + ce + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.implicitCache != null) {
                this.implicitCache.reusePooledConnection((PooledConnection) ce.getSource());
            }
        } catch (SQLException se) {
        }
    }

    public synchronized void connectionErrorOccurred(ConnectionEvent ce) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionEventListener.connectionErrorOccurred(" + ce
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.implicitCache != null) {
                this.implicitCache.closePooledConnection((PooledConnection) ce.getSource());
            }
        } catch (SQLException se) {
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleConnectionCachEventListener"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleConnectionCacheEventListener JD-Core Version: 0.6.0
 */