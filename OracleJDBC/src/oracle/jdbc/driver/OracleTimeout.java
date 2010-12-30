package oracle.jdbc.driver;

import java.sql.SQLException;

abstract class OracleTimeout {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    static OracleTimeout newTimeout(String name) throws SQLException {
        OracleTimeout instance = new OracleTimeoutThreadPerVM(name);

        return instance;
    }

    abstract void setTimeout(long paramLong, OracleStatement paramOracleStatement)
            throws SQLException;

    abstract void cancelTimeout() throws SQLException;

    abstract void close() throws SQLException;

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleTimeout"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleTimeout JD-Core Version: 0.6.0
 */