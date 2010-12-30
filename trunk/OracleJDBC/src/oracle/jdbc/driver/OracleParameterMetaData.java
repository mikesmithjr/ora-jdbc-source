package oracle.jdbc.driver;

import java.sql.SQLException;

public class OracleParameterMetaData implements oracle.jdbc.OracleParameterMetaData {
    int parameterCount = 0;

    int parameterNoNulls = 0;

    int parameterNullable = 1;

    int parameterNullableUnknown = 2;

    int parameterModeUnknown = 0;

    int parameterModeIn = 1;

    int parameterModeInOut = 2;

    int parameterModeOut = 4;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    OracleParameterMetaData(int _parameterCount) throws SQLException {
        this.parameterCount = _parameterCount;
    }

    public int getParameterCount() throws SQLException {
        return this.parameterCount;
    }

    public int isNullable(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return 2;
    }

    public boolean isSigned(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return false;
    }

    public int getPrecision(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return -1;
    }

    public int getScale(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return -1;
    }

    public int getParameterType(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return 9999;
    }

    public String getParameterTypeName(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public String getParameterClassName(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public int getParameterMode(int param) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return -1;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleParameterMetaData"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleParameterMetaData JD-Core Version: 0.6.0
 */