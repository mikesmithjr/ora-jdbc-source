package oracle.jdbc.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

public class SQLStateRange {
    public int low;
    public int high;
    public String sqlState;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public SQLStateRange(int l, int h, String s) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "SQLStateRange.SQLStateRange(" + l + ", " + h
                    + ", " + s + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.low = l;
        this.high = h;
        this.sqlState = s;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.util.SQLStateRange"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.util.SQLStateRange JD-Core Version: 0.6.0
 */