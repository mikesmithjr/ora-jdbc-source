package oracle.jdbc.connector;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.resource.spi.ConnectionRequestInfo;
import oracle.jdbc.driver.OracleLog;

public class OracleConnectionRequestInfo implements ConnectionRequestInfo {
    private String user = null;
    private String password = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:57_PDT_2005";

    public OracleConnectionRequestInfo(String user, String password) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleConnectionRequestInfo.OracleConnectionRequestInfo(user = "
                                               + user + ", password = " + password + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.user = user;
        this.password = password;
    }

    public String getUser() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConnectionRequestInfo.getUser() returns "
                    + this.user, this);

            OracleLog.recursiveTrace = false;
        }

        return this.user;
    }

    public void setUser(String user) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConnectionRequestInfo.setUser(user = "
                    + user + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.user = user;
    }

    public String getPassword() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleConnectionRequestInfo.getPassword() returns "
                                               + this.password, this);

            OracleLog.recursiveTrace = false;
        }

        return this.password;
    }

    public void setPassword(String password) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleConnectionRequestInfo.setPassword(password = "
                                               + password + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.password = password;
    }

    public boolean equals(Object other) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleConnectionRequestInfo.equals(other = "
                    + other + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!(other instanceof OracleConnectionRequestInfo)) {
            return false;
        }
        OracleConnectionRequestInfo info = (OracleConnectionRequestInfo) other;

        return (this.user == info.getUser()) && (this.password == info.getPassword());
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.connector.OracleConnectionRequestInfo"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.connector.OracleConnectionRequestInfo JD-Core Version: 0.6.0
 */