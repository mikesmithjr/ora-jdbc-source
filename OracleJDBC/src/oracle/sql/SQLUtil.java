package oracle.sql;

import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class SQLUtil {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public static Object SQLToJava(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, Class javaClass, Map map) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.SQLToJava(connection, sqlData, sqlTypeCode, sqlTypeName,
                                                    javaClass, map);
    }

    public static CustomDatum SQLToJava(OracleConnection connection, byte[] sqlData,
            int sqlTypeCode, String sqlTypeName, CustomDatumFactory factory) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.SQLToJava(connection, sqlData, sqlTypeCode, sqlTypeName,
                                                    factory);
    }

    public static ORAData SQLToJava(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, ORADataFactory factory) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.SQLToJava(connection, sqlData, sqlTypeCode, sqlTypeName,
                                                    factory);
    }

    public static Object SQLToJava(OracleConnection connection, Datum datum, Class javaClass,
            Map map) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.SQLToJava(connection, datum, javaClass, map);
    }

    public static byte[] JavaToSQL(OracleConnection connection, Object inObject, int sqlTypeCode,
            String sqlTypeName) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.JavaToSQL(connection, inObject, sqlTypeCode, sqlTypeName);
    }

    public static Datum makeDatum(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, int maxLen) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.makeDatum(connection, sqlData, sqlTypeCode, sqlTypeName,
                                                    maxLen);
    }

    public static Datum makeDatum(OracleConnection connection, Object inObject, int sqlTypeCode,
            String sqlTypeName) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.makeDatum(connection, inObject, sqlTypeCode, sqlTypeName);
    }

    public static Object getTypeDescriptor(String name, OracleConnection conn) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.getTypeDescriptor(name, conn);
    }

    public static boolean checkDatumType(Datum datum, int sqlType, String sqlTypeName)
            throws SQLException {
        return oracle.jdbc.driver.SQLUtil.checkDatumType(datum, sqlType, sqlTypeName);
    }

    public static boolean implementsInterface(Class clazz, Class interfaze) {
        return oracle.jdbc.driver.SQLUtil.implementsInterface(clazz, interfaze);
    }

    public static Datum makeOracleDatum(OracleConnection connection, Object inObject, int typeCode,
            String sqlTypeName) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.makeOracleDatum(connection, inObject, typeCode,
                                                          sqlTypeName);
    }

    public static int getInternalType(int externalType) throws SQLException {
        return oracle.jdbc.driver.SQLUtil.getInternalType(externalType);
    }

    /** @deprecated */
    public static int get_internal_type(int external_type) throws SQLException {
        return getInternalType(external_type);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.SQLUtil"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.SQLUtil JD-Core Version: 0.6.0
 */