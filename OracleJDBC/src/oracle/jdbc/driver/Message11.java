package oracle.jdbc.driver;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Message11 implements Message {
    private static ResourceBundle bundle;
    private static final String messageFile = "oracle.jdbc.driver.Messages";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public String msg(String key, Object what) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "Message11.msg(key=" + key + ", what=" + what
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("oracle.jdbc.driver.Messages");
            } catch (Exception e) {
                return "Message file 'oracle.jdbc.driver.Messages' is missing.";
            }

        }

        try {
            if (what != null) {
                return bundle.getString(key) + ": " + what;
            }
            return bundle.getString(key);
        } catch (Exception e) {
        }
        return "Message [" + key + "] not found in '" + "oracle.jdbc.driver.Messages" + "'.";
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.Message11"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.Message11 JD-Core Version: 0.6.0
 */