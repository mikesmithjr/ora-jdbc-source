package oracle.jdbc.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class OracleConnectionPoolDataSource extends OracleDataSource implements
        ConnectionPoolDataSource {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    public OracleConnectionPoolDataSource() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE,
                         "OracleConnectionPoolDataSource.OracleConnectionPoolDataSource()", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSourceName = "OracleConnectionPoolDataSource";
        this.isOracleDataSource = false;

        this.connCachingEnabled = false;

        this.fastConnFailover = false;
    }

    public PooledConnection getPooledConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionPoolDataSource.getPooledConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        String localUser = null;
        String localPassword = null;
        synchronized (this) {
            localUser = this.user;
            localPassword = this.password;
        }
        return getPooledConnection(localUser, localPassword);
    }

    public PooledConnection getPooledConnection(String _user, String _passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionPoolDataSource.getPooledConnection(user="
                                             + _user + ", passwd=" + _passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = getPhysicalConnection(this.url, _user, _passwd);
        OraclePooledConnection opc = new OraclePooledConnection(conn);

        if (_passwd == null)
            _passwd = this.password;
        opc.setUserName(!_user.startsWith("\"") ? _user.toLowerCase() : _user, _passwd
                .toUpperCase());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionPoolDataSource.getPooledConnection(user, passwd): returned "
                                             + opc, this);

            OracleLog.recursiveTrace = false;
        }

        return opc;
    }

    PooledConnection getPooledConnection(Properties prop) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConnectionPoolDataSource.getPooledConnection(prop="
                                               + prop + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Connection conn = getPhysicalConnection(prop);
        OraclePooledConnection opc = new OraclePooledConnection(conn);

        String _user = prop.getProperty("user");
        if (_user == null)
            _user = ((OracleConnection) conn).getUserName();
        String _passwd = prop.getProperty("password");
        if (_passwd == null)
            _passwd = this.password;
        opc.setUserName(!_user.startsWith("\"") ? _user.toLowerCase() : _user, _passwd
                .toUpperCase());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleConnectionPoolDataSource.getPooledConnection(prop): returned "
                                               + opc, this);

            OracleLog.recursiveTrace = false;
        }

        return opc;
    }

    protected Connection getPhysicalConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleConnectionPoolDataSource.getPhysicalConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        return super.getConnection(this.user, this.password);
    }

    protected Connection getPhysicalConnection(String _url, String _user, String _passwd)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger
                    .log(Level.FINE, "OracleConnectionPoolDataSource.getPhysicalConnection(url="
                            + _url + ", user=" + _user + "passwd=" + _passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.url = _url;

        return super.getConnection(_user, _passwd);
    }

    protected Connection getPhysicalConnection(String _user, String _passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleConnectionPoolDataSource.getPhysicalConnection(user="
                                             + _user + ", passwd=" + _passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return super.getConnection(_user, _passwd);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleConnectionPoolDataSource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleConnectionPoolDataSource JD-Core Version: 0.6.0
 */