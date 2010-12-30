package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class ResultSetUtil {
    static final int[][] allRsetTypes = { { 0, 0 }, { 1003, 1007 }, { 1003, 1008 }, { 1004, 1007 },
            { 1004, 1008 }, { 1005, 1007 }, { 1005, 1008 } };

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    static OracleResultSet createScrollResultSet(ScrollRsetStatement owner,
            OracleResultSet referencedRset, int rsetType) throws SQLException {
        switch (rsetType) {
        case 1:
            return referencedRset;
        case 2:
            return new UpdatableResultSet(owner, (OracleResultSetImpl) referencedRset,
                    getScrollType(rsetType), getUpdateConcurrency(rsetType));
        case 3:
            return new ScrollableResultSet(owner, (OracleResultSetImpl) referencedRset,
                    getScrollType(rsetType), getUpdateConcurrency(rsetType));
        case 4:
            ScrollableResultSet _rset = new ScrollableResultSet(owner,
                    (OracleResultSetImpl) referencedRset, getScrollType(rsetType),
                    getUpdateConcurrency(rsetType));

            return new UpdatableResultSet(owner, _rset, getScrollType(rsetType),
                    getUpdateConcurrency(rsetType));
        case 5:
            return new SensitiveScrollableResultSet(owner, (OracleResultSetImpl) referencedRset,
                    getScrollType(rsetType), getUpdateConcurrency(rsetType));
        case 6:
            ScrollableResultSet _rset1 = new SensitiveScrollableResultSet(owner,
                    (OracleResultSetImpl) referencedRset, getScrollType(rsetType),
                    getUpdateConcurrency(rsetType));

            return new UpdatableResultSet(owner, _rset1, getScrollType(rsetType),
                    getUpdateConcurrency(rsetType));
        }

        DatabaseError.throwSqlException(23, null);

        return null;
    }

    static int getScrollType(int typeCode) {
        return allRsetTypes[typeCode][0];
    }

    static int getUpdateConcurrency(int typeCode) {
        return allRsetTypes[typeCode][1];
    }

    static int getRsetTypeCode(int scrollType, int updateConcurrency) throws SQLException {
        for (int i = 0; i < allRsetTypes.length; i++) {
            if ((allRsetTypes[i][0] == scrollType) && (allRsetTypes[i][1] == updateConcurrency)) {
                return i;
            }
        }

        DatabaseError.throwSqlException(68);

        return 0;
    }

    static boolean needIdentifier(int typeCode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSetUtil.needIdentifier(typeCode="
                    + typeCode + "): return: " + ((typeCode != 1) && (typeCode != 3)));

            OracleLog.recursiveTrace = false;
        }

        return (typeCode != 1) && (typeCode != 3);
    }

    static boolean needIdentifier(int type, int concur) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSetUtil.needIdentifier(type=" + type
                    + ", concur=" + concur + "): return: "
                    + needIdentifier(getRsetTypeCode(type, concur)));

            OracleLog.recursiveTrace = false;
        }

        return needIdentifier(getRsetTypeCode(type, concur));
    }

    static boolean needCache(int typeCode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSetUtil.needCache(typeCode=" + typeCode
                    + "): return: " + (typeCode >= 3));

            OracleLog.recursiveTrace = false;
        }

        return typeCode >= 3;
    }

    static boolean needCache(int type, int concur) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSetUtil.needCache(type=" + type
                    + ", concur=" + concur + "); return: "
                    + needCache(getRsetTypeCode(type, concur)));

            OracleLog.recursiveTrace = false;
        }

        return needCache(getRsetTypeCode(type, concur));
    }

    static boolean supportRefreshRow(int typeCode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSetUtil.supportRefreshRow(typeCode="
                    + typeCode + "): return: " + (typeCode >= 4));

            OracleLog.recursiveTrace = false;
        }

        return typeCode >= 4;
    }

    static boolean supportRefreshRow(int type, int concur) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSetUtil.supportRefreshRow(type=" + type
                    + ", concur=" + concur + "): return: "
                    + supportRefreshRow(getRsetTypeCode(type, concur)));

            OracleLog.recursiveTrace = false;
        }

        return supportRefreshRow(getRsetTypeCode(type, concur));
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.ResultSetUtil"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ResultSetUtil JD-Core Version: 0.6.0
 */