package oracle.jdbc.connector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import oracle.jdbc.driver.OracleLog;

public class OracleConnectionManager implements ConnectionManager {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    public OracleConnectionManager() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleConnectionManager.OracleConnectionManager()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object allocateConnection(ManagedConnectionFactory mcf,
            ConnectionRequestInfo cxRequestInfo) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleConnectionManager.allocateConnection(mcf = " + mcf
                                               + ", cxRequestInfo = " + cxRequestInfo + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ManagedConnection mc = mcf.createManagedConnection(null, cxRequestInfo);

        return mc.getConnection(null, cxRequestInfo);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleConnectionManager"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleConnectionManager JD-Core Version: 0.6.0
 */