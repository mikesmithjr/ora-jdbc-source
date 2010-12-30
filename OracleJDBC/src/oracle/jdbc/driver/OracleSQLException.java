package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleSQLException extends SQLException {
    private Object[] m_parameters;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    public OracleSQLException() {
        this(null, null, 0);
    }

    public OracleSQLException(String reason) {
        this(reason, null, 0);
    }

    public OracleSQLException(String reason, String sqlState) {
        this(reason, sqlState, 0);
    }

    public OracleSQLException(String reason, String sqlState, int vendorCode) {
        this(reason, sqlState, vendorCode, null);
    }

    public OracleSQLException(String reason, String sqlState, int vendorCode, Object[] parameters) {
        super(reason, sqlState, vendorCode);

        this.m_parameters = parameters;
    }

    public Object[] getParameters() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleSQLException.getParameters()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.m_parameters == null) {
            this.m_parameters = new Object[0];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleSQLException.getParameters: return: "
                    + this.m_parameters, this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_parameters;
    }

    public int getNumParameters() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleSQLException.getNumParameters()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.m_parameters == null) {
            this.m_parameters = new Object[0];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleSQLException.getNumParameters: return: "
                    + this.m_parameters.length, this);

            OracleLog.recursiveTrace = false;
        }

        return this.m_parameters.length;
    }

    public void setParameters(Object[] parameters) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleSQLException.setParameters(parameters="
                    + parameters + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.m_parameters = parameters;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleSQLException"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleSQLException JD-Core Version: 0.6.0
 */