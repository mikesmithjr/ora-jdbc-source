package oracle.jdbc.connector;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.ResourceException;
import javax.resource.spi.EISSystemException;
import javax.resource.spi.ManagedConnectionMetaData;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDatabaseMetaData;
import oracle.jdbc.driver.OracleLog;

public class OracleManagedConnectionMetaData implements ManagedConnectionMetaData {
    private OracleManagedConnection managedConnection = null;
    private OracleDatabaseMetaData databaseMetaData = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    OracleManagedConnectionMetaData(OracleManagedConnection omc) throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleManagedConnectionMetaData.OracleManagedConnectionMetaData(omc = "
                                               + omc + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.managedConnection = omc;

            OracleConnection conn = (OracleConnection) omc.getPhysicalConnection();

            this.databaseMetaData = ((OracleDatabaseMetaData) conn.getMetaData());
        } catch (Exception exc) {
            ResourceException rexc = new EISSystemException("Exception: " + exc.getMessage());

            rexc.setLinkedException(exc);

            throw rexc;
        }
    }

    public String getEISProductName() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionMetaData.getEISProductName()", this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            return this.databaseMetaData.getDatabaseProductName();
        } catch (SQLException exc) {
            rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    public String getEISProductVersion() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionMetaData.getEISProductVersion()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            return this.databaseMetaData.getDatabaseProductVersion();
        } catch (Exception exc) {
            rexc = new EISSystemException("Exception: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    public int getMaxConnections() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleManagedConnectionMetaData.getMaxConnections()", this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            return this.databaseMetaData.getMaxConnections();
        } catch (SQLException exc) {
            rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    public String getUserName() throws ResourceException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleManagedConnectionMetaData.getUserName()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        ResourceException rexc;
        try {
            return this.databaseMetaData.getUserName();
        } catch (SQLException exc) {
            rexc = new EISSystemException("SQLException: " + exc.getMessage());

            rexc.setLinkedException(exc);
        }
        throw rexc;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleManagedConnectionMetaData"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleManagedConnectionMetaData JD-Core Version: 0.6.0
 */