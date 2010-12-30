package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.xa.OracleXAConnection;
import oracle.jdbc.xa.OracleXADataSource;

public class OracleXAConnectionCacheImpl extends OracleConnectionCacheImpl implements XADataSource,
        Serializable {
    private boolean nativeXA = false;
    private static final String clientXADS = "oracle.jdbc.xa.client.OracleXADataSource";
    private static final String serverXADS = "oracle.jdbc.xa.server.OracleXADataSource";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXAConnectionCacheImpl() throws SQLException {
        this(null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.OracleXAConnectionCacheImpl()",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleXAConnectionCacheImpl(ConnectionPoolDataSource cpds) throws SQLException {
        super(cpds);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.OracleXAConnectionCacheImpl("
                                             + cpds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSourceName = "OracleXAConnectionCacheImpl";
    }

    void initializeConnectionPoolDataSource() throws SQLException {
        if (this.cpds == null) {
            if ((this.user == null) || (this.password == null)) {
                DatabaseError.throwSqlException(79);
            }
            String xadsClassName = null;

            if (OracleDriver.getSystemPropertyJserverVersion() == null)
                xadsClassName = "oracle.jdbc.xa.client.OracleXADataSource";
            else {
                xadsClassName = "oracle.jdbc.xa.server.OracleXADataSource";
            }
            try {
                this.cpds = ((OracleXADataSource) Class.forName(xadsClassName).newInstance());
            } catch (Exception exc) {
                exc.printStackTrace();
                DatabaseError.throwSqlException(1);
            }

            copy((OracleDataSource) this.cpds);

            ((OracleXADataSource) this.cpds).setNativeXA(this.nativeXA);
        }
    }

    PooledConnection getNewPoolOrXAConnection(Properties prop) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleXAConnectionCacheImpl.getNewPoolOrXAConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        PooledConnection xac = ((OracleXADataSource) this.cpds).getXAConnection(prop);

        ((OracleXAConnection) xac).setStmtCacheSize(this.stmtCacheSize, this.stmtClearMetaData);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.getNewPoolOrXAConnection(): returned "
                                             + xac, this);

            OracleLog.recursiveTrace = false;
        }

        return xac;
    }

    public synchronized XAConnection getXAConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleXAConnectionCacheImpl.getXAConnection()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        XAConnection xc = (XAConnection) super.getPooledConnection(this.user, this.password);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.getXAConnection(): returned "
                                             + xc, this);

            OracleLog.recursiveTrace = false;
        }

        return xc;
    }

    public synchronized XAConnection getXAConnection(String user, String passwd)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.getXAConnection(user=" + user
                                             + ", passwd=" + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        XAConnection xc = (XAConnection) super.getPooledConnection(user, passwd);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.getXAConnection(user, passwd): returned "
                                             + xc, this);

            OracleLog.recursiveTrace = false;
        }

        return xc;
    }

    public synchronized boolean getNativeXA() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleXAConnectionCacheImpl.getNativeXA()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.nativeXA;
    }

    public synchronized void setNativeXA(boolean nativeXA) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE, "OracleXAConnectionCacheImpl.setNativeXA("
                    + nativeXA + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.nativeXA = nativeXA;
    }

    public synchronized void closeActualConnection(PooledConnection xac) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleXAConnectionCacheImpl.closeActualConnection(" + xac
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ((OracleXAConnection) xac).close();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleXAConnectionCacheImpl"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleXAConnectionCacheImpl JD-Core Version: 0.6.0
 */