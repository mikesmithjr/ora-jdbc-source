package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.OracleLog;

public class OracleConnectionEventListener implements ConnectionEventListener, Serializable {
    static final int _CLOSED_EVENT = 1;
    static final int _ERROROCCURED_EVENT = 2;
    private DataSource dataSource = null;

    protected long lastCleanupTime = -1L;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    public OracleConnectionEventListener() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "OracleConnectionEventListener.OracleConnectionEventListener()", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSource = null;
    }

    public OracleConnectionEventListener(DataSource ds) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionEventListener.OracleConnectionEventListener("
                                             + ds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSource = ds;
    }

    public void setDataSource(DataSource ds) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionEventListener.setDataSource("
                    + ds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSource = ds;
    }

    public void connectionClosed(ConnectionEvent ce) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleConnectionEventListener.connectionClosed("
                    + ce + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if ((this.dataSource != null) && ((this.dataSource instanceof OracleConnectionCache)))
                ((OracleConnectionCache) this.dataSource)
                        .reusePooledConnection((PooledConnection) ce.getSource());
        } catch (SQLException se) {
        }
    }

    public void connectionErrorOccurred(ConnectionEvent ce) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionEventListener.connectionErrorOccurred(" + ce
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if ((this.dataSource != null) && ((this.dataSource instanceof OracleConnectionCache)))
                ((OracleConnectionCache) this.dataSource)
                        .closePooledConnection((PooledConnection) ce.getSource());
        } catch (SQLException se) {
            cleanupInvalidConnections(se);
        }
    }

    protected synchronized void cleanupInvalidConnections(SQLException se) {
        try {
            if ((this.dataSource != null)
                    && ((this.dataSource instanceof OracleConnectionCacheImpl))) {
                long currTime = System.currentTimeMillis();
                long interval = ((OracleConnectionCacheImpl) this.dataSource)
                        .getConnectionCleanupInterval();

                if (currTime - this.lastCleanupTime > interval * 1000L) {
                    if (((OracleConnectionCacheImpl) this.dataSource).isFatalConnectionError(se)) {
                        ((OracleConnectionCacheImpl) this.dataSource).closeConnections();
                    }
                    this.lastCleanupTime = System.currentTimeMillis();
                }
            }
        } catch (Exception e) {
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleConnectionEventListener"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleConnectionEventListener JD-Core Version: 0.6.0
 */