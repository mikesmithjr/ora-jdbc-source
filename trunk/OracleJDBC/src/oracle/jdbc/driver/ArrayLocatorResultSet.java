package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class ArrayLocatorResultSet extends OracleResultSetImpl {
    static int COUNT_UNLIMITED = -1;
    Map map;
    long beginIndex;
    int count;
    long currentIndex;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public ArrayLocatorResultSet(OracleConnection conn, ArrayDescriptor descriptor, byte[] locator,
            Map map) throws SQLException {
        this(conn, descriptor, locator, 0L, COUNT_UNLIMITED, map);
    }

    public ArrayLocatorResultSet(OracleConnection conn, ArrayDescriptor descriptor, byte[] locator,
            long beginIndex, int count, Map map) throws SQLException {
        super((PhysicalConnection) conn, (OracleStatement) null);

        if ((descriptor == null) || (conn == null)) {
            DatabaseError.throwSqlException(1, "Invalid arguments");
        }

        this.close_statement_on_close = true;

        this.count = count;
        this.currentIndex = 0L;
        this.beginIndex = beginIndex;

        this.map = map;

        OraclePreparedStatement pstmt = null;

        ARRAY tmpObj = new ARRAY(descriptor, conn, (byte[]) null);

        tmpObj.setLocator(locator);

        if ((descriptor.getBaseType() == 2002) || (descriptor.getBaseType() == 2008)) {
            pstmt = (OraclePreparedStatement) conn
                    .prepareStatement("SELECT ROWNUM, SYS_NC_ROWINFO$ FROM TABLE( CAST(:1 AS "
                            + descriptor.getName() + ") )");
        } else {
            pstmt = (OraclePreparedStatement) conn
                    .prepareStatement("SELECT ROWNUM, COLUMN_VALUE FROM TABLE( CAST(:1 AS "
                            + descriptor.getName() + ") )");
        }

        pstmt.setArray(1, tmpObj);
        pstmt.executeQuery();

        this.statement = pstmt;
    }

    public synchronized boolean next() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.next()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.currentIndex < this.beginIndex) {
            while (this.currentIndex < this.beginIndex) {
                this.currentIndex += 1L;

                if (!super.next()) {
                    return false;
                }
            }
            return true;
        }

        if (this.count == COUNT_UNLIMITED) {
            return super.next();
        }
        if (this.currentIndex < this.beginIndex + this.count - 1L) {
            this.currentIndex += 1L;

            return super.next();
        }

        return false;
    }

    public synchronized Object getObject(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getObject(columnIndex=" + columnIndex
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getObject(columnIndex, this.map);
    }

    public synchronized int findColumn(String columnName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.findColumn(columnName=" + columnName
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (columnName.equalsIgnoreCase("index"))
            return 1;
        if (columnName.equalsIgnoreCase("value")) {
            return 2;
        }
        DatabaseError.throwSqlException(6, "get_column_index");

        return 0;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.ArrayLocatorResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ArrayLocatorResultSet JD-Core Version: 0.6.0
 */