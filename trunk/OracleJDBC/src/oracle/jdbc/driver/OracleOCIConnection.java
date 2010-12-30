package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.pool.OracleOCIConnectionPool;

public abstract class OracleOCIConnection extends T2CConnection {
    OracleOCIConnectionPool ociConnectionPool = null;
    boolean isPool = false;
    boolean aliasing = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleOCIConnection(String ur, String us, String p, String db, Properties info,
            Object ext) throws SQLException {
        this(ur, us, p, db, info, (OracleDriverExtension) ext);
    }

    OracleOCIConnection(String ur, String us, String p, String db, Properties info,
            OracleDriverExtension ext) throws SQLException {
        super(ur, us, p, db, info, ext);
    }

    public synchronized byte[] getConnectionId() throws SQLException {
        byte[] connId = t2cGetConnectionId(this.m_nativeState);

        this.aliasing = true;

        return connId;
    }

    public synchronized void passwordChange(String user, String oldPassword, String newPassword)
            throws SQLException, IOException {
        ociPasswordChange(user, oldPassword, newPassword);
    }

    public synchronized void close() throws SQLException {
        if ((this.lifecycle == 2) || (this.lifecycle == 4) || (this.aliasing)) {
            return;
        }
        super.close();

        this.ociConnectionPool.connectionClosed((oracle.jdbc.oci.OracleOCIConnection) this);
    }

    public synchronized void setConnectionPool(OracleOCIConnectionPool cpool) {
        this.ociConnectionPool = cpool;
    }

    public synchronized void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
        super.setStmtCacheSize(size, clearMetaData);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleOCIConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleOCIConnection JD-Core Version: 0.6.0
 */