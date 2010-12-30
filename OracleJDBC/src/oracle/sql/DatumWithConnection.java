package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;

public abstract class DatumWithConnection extends Datum {
    private oracle.jdbc.internal.OracleConnection physicalConnection = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    oracle.jdbc.internal.OracleConnection getPhysicalConnection() {
        if (this.physicalConnection == null) {
            try {
                this.physicalConnection = ((oracle.jdbc.internal.OracleConnection) new OracleDriver()
                        .defaultConnection());
            } catch (SQLException ex) {
            }

        }

        return this.physicalConnection;
    }

    public DatumWithConnection(byte[] elements) throws SQLException {
        super(elements);
    }

    public DatumWithConnection() {
    }

    public static void assertNotNull(Connection conn) throws SQLException {
        if (conn == null)
            DatabaseError.throwSqlException(68, "Connection is null");
    }

    public static void assertNotNull(TypeDescriptor desc) throws SQLException {
        if (desc == null)
            DatabaseError.throwSqlException(61);
    }

    public void setPhysicalConnectionOf(Connection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "DatumWithConnection.setPhysicalConnectionOf( conn=" + conn
                                              + ")");

            OracleLog.recursiveTrace = false;
        }

        this.physicalConnection = ((oracle.jdbc.OracleConnection) conn).physicalConnectionWithin();
    }

    public Connection getJavaSqlConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "DatumWithConnection.getJavaSqlConnection(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return getPhysicalConnection().getWrapper();
    }

    public oracle.jdbc.OracleConnection getOracleConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getOracleConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        return getPhysicalConnection().getWrapper();
    }

    public oracle.jdbc.internal.OracleConnection getInternalConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getInternalConnection(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return getPhysicalConnection();
    }

    /** @deprecated */
    public oracle.jdbc.driver.OracleConnection getConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(
                         Level.FINE,
                         "DatumWithConnection.getConnection() --- DEPRECATED --- Use getJavaSqlConnection() or get instead.",
                         this);

            OracleLog.recursiveTrace = false;
        }

        oracle.jdbc.driver.OracleConnection ret = null;
        try {
            ret = (oracle.jdbc.driver.OracleConnection) ((oracle.jdbc.driver.OracleConnection) this.physicalConnection)
                    .getWrapper();
        } catch (ClassCastException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "DatumWithConnection.getConnection: Wrong connection type. An exception is thrown."
                                                  + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(103);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "DatumWithConnection.getConnection: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.DatumWithConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.DatumWithConnection JD-Core Version: 0.6.0
 */