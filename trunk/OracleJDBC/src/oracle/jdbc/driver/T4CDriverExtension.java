package oracle.jdbc.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

class T4CDriverExtension extends OracleDriverExtension {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    Connection getConnection(String url, String user, String password, String database,
            Properties info) throws SQLException {
        return new T4CConnection(url, user, password, database, info, this);
    }

    OracleStatement allocateStatement(PhysicalConnection conn, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        return new T4CStatement(conn, resultSetType, resultSetConcurrency);
    }

    OraclePreparedStatement allocatePreparedStatement(PhysicalConnection conn, String sql,
            int resultSetType, int resultSetConcurrency) throws SQLException {
        return new T4CPreparedStatement(conn, sql, resultSetType, resultSetConcurrency);
    }

    OracleCallableStatement allocateCallableStatement(PhysicalConnection conn, String sql,
            int resultSetType, int resultSetConcurrency) throws SQLException {
        return new T4CCallableStatement(conn, sql, resultSetType, resultSetConcurrency);
    }

    OracleInputStream createInputStream(OracleStatement stmt, int index, Accessor accessor)
            throws SQLException {
        return new T4CInputStream(stmt, index, accessor);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CStatement"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CDriverExtension JD-Core Version: 0.6.0
 */