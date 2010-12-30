package oracle.jdbc.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

public class Login {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public static byte[] generateResponse(String user, String passwd, byte[] challenge) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "Login.generateResponse(user=" + user
                    + ", passwd=" + passwd + ", challenge)");

            OracleLog.recursiveTrace = false;
        }

        return new byte[0];
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.util.Login"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.util.Login JD-Core Version: 0.6.0
 */