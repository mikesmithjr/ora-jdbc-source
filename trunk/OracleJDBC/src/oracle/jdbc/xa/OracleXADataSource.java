package oracle.jdbc.xa;

import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;

public abstract class OracleXADataSource extends OracleConnectionPoolDataSource implements
        XADataSource {
    protected boolean useNativeXA = false;
    protected boolean thinUseNativeXA = true;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXADataSource() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.OracleXADataSource() -- after super()", this);

            OracleLog.recursiveTrace = false;
        }

        this.dataSourceName = "OracleXADataSource";
    }

    public abstract XAConnection getXAConnection() throws SQLException;

    public abstract XAConnection getXAConnection(String paramString1, String paramString2)
            throws SQLException;

    public abstract XAConnection getXAConnection(Properties paramProperties) throws SQLException;

    public synchronized void setNativeXA(boolean nativeXA) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.setNativeXA(nativeXA = "
                    + nativeXA + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.useNativeXA = nativeXA;
        this.thinUseNativeXA = nativeXA;
    }

    public synchronized boolean getNativeXA() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.getNativeXA() return: "
                    + this.useNativeXA, this);

            OracleLog.recursiveTrace = false;
        }

        return this.useNativeXA;
    }

    protected void copy(OracleDataSource ds) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.copy(" + ds + ")", this);

            OracleLog.recursiveTrace = false;
        }

        super.copy(ds);

        ((OracleXADataSource) ds).useNativeXA = this.useNativeXA;
        ((OracleXADataSource) ds).thinUseNativeXA = this.thinUseNativeXA;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.OracleXADataSource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.OracleXADataSource JD-Core Version: 0.6.0
 */