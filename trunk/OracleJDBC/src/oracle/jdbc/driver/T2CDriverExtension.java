package oracle.jdbc.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.oci.OracleOCIConnection;

class T2CDriverExtension extends OracleDriverExtension {
    static final int T2C_DEFAULT_BATCHSIZE = 1;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    Connection getConnection(String url, String user, String password, String database,
            Properties info) throws SQLException {
        T2CConnection t2Conn = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CDriverExtension.getConnection: (user = " + user
                    + ", password = " + password + ", database = " + database + ", info = " + info
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (info.getProperty("is_connection_pooling") == "true") {
            info.put("database", database == null ? "" : database);

            t2Conn = new OracleOCIConnection(url, user, password, database, info, this);
        } else {
            t2Conn = new T2CConnection(url, user, password, database, info, this);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CDriverExtension.getConnection(user, passwd, db, info) returns: "
                                            + t2Conn, this);

            OracleLog.recursiveTrace = false;
        }

        return t2Conn;
    }

    OracleStatement allocateStatement(PhysicalConnection conn, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        T2CStatement t2Stmt = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CDriverExtension.allocateStatement: (conn = "
                    + conn + ", resultSetType = " + resultSetType + ", resultSetConcurrency = "
                    + resultSetConcurrency + ")", this);

            OracleLog.recursiveTrace = false;
        }

        t2Stmt = new T2CStatement((T2CConnection) conn, 1, conn.defaultRowPrefetch, resultSetType,
                resultSetConcurrency);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CDriverExtension.allocateStatement() returns: "
                    + t2Stmt, this);

            OracleLog.recursiveTrace = false;
        }

        return t2Stmt;
    }

    OraclePreparedStatement allocatePreparedStatement(PhysicalConnection conn, String sql,
            int resultSetType, int resultSetConcurrency) throws SQLException {
        T2CPreparedStatement t2prepStmt = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CDriverExtension.allocatePreparedStatement: (conn = " + conn
                                            + ", sql = " + sql + ", resultSetType = "
                                            + resultSetType + ", resultSetConcurrency = "
                                            + resultSetConcurrency + ")", this);

            OracleLog.recursiveTrace = false;
        }

        t2prepStmt = new T2CPreparedStatement((T2CConnection) conn, sql, conn.defaultBatch,
                conn.defaultRowPrefetch, resultSetType, resultSetConcurrency);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CDriverExtension.allocatePreparedStatement() returns: "
                                            + t2prepStmt, this);

            OracleLog.recursiveTrace = false;
        }

        return t2prepStmt;
    }

    OracleCallableStatement allocateCallableStatement(PhysicalConnection conn, String sql,
            int resultSetType, int resultSetConcurrency) throws SQLException {
        T2CCallableStatement t2callStmt = null;

        OracleCallableStatement cs = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CDriverExtension.allocateCallableStatement: (conn = " + conn
                                            + ", sql = " + sql + ", resultSetType = "
                                            + resultSetType + ", resultSetConcurrency = "
                                            + resultSetConcurrency + ")", this);

            OracleLog.recursiveTrace = false;
        }

        t2callStmt = new T2CCallableStatement((T2CConnection) conn, sql, conn.defaultBatch,
                conn.defaultRowPrefetch, resultSetType, resultSetConcurrency);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger
                    .log(Level.FINE, "T2CDriverExtension.allocateCallableStatement() returns: "
                            + cs, this);

            OracleLog.recursiveTrace = false;
        }

        return t2callStmt;
    }

    OracleInputStream createInputStream(OracleStatement stmt, int index, Accessor accessor)
            throws SQLException {
        OracleInputStream istream = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CDriverExtension.createInputStream: (stmt = "
                    + stmt + ", index = " + index + ", accessor = " + accessor + ")", this);

            OracleLog.recursiveTrace = false;
        }

        istream = new T2CInputStream(stmt, index, accessor);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CDriverExtension.createInputStream() returns: "
                    + istream, this);

            OracleLog.recursiveTrace = false;
        }

        return istream;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T2CDriverExtension"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T2CDriverExtension JD-Core Version: 0.6.0
 */