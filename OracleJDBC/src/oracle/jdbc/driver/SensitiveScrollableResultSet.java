package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class SensitiveScrollableResultSet extends ScrollableResultSet {
    int beginLastFetchedIndex;
    int endLastFetchedIndex;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    SensitiveScrollableResultSet(ScrollRsetStatement scrollStmt, OracleResultSetImpl rset,
            int type, int update) throws SQLException {
        super(scrollStmt, rset, type, update);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "SensitiveScrollableResultSet.SensitiveScrollableResultSet(scrollStmt, rset, type= "
                                               + type + " update=" + update + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int valid_rows = rset.getValidRows();

        if (valid_rows > 0) {
            this.beginLastFetchedIndex = 1;
            this.endLastFetchedIndex = valid_rows;
        } else {
            this.beginLastFetchedIndex = 0;
            this.endLastFetchedIndex = 0;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "SensitiveScrollableResultSet.SensitiveScrollableResultSet:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized boolean next() throws SQLException {
        if (super.next()) {
            handle_refetch();

            return true;
        }

        return false;
    }

    public synchronized boolean first() throws SQLException {
        if (super.first()) {
            handle_refetch();

            return true;
        }

        return false;
    }

    public synchronized boolean last() throws SQLException {
        if (super.last()) {
            handle_refetch();

            return true;
        }

        return false;
    }

    public synchronized boolean absolute(int row) throws SQLException {
        if (super.absolute(row)) {
            handle_refetch();

            return true;
        }

        return false;
    }

    public synchronized boolean relative(int rows) throws SQLException {
        if (super.relative(rows)) {
            handle_refetch();

            return true;
        }

        return false;
    }

    public synchronized boolean previous() throws SQLException {
        if (super.previous()) {
            handle_refetch();

            return true;
        }

        return false;
    }

    public synchronized void refreshRow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "refreshRow()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isValidRow(this.currentRow)) {
            DatabaseError.throwSqlException(11);
        }
        int direction = getFetchDirection();
        int realSz = 0;
        try {
            realSz = refreshRowsInCache(this.currentRow, getFetchSize(), direction);
        } catch (SQLException e) {
            DatabaseError.throwSqlException(e, 90, "Unsupported syntax for refreshRow()");
        }

        if (realSz != 0) {
            this.beginLastFetchedIndex = this.currentRow;

            this.endLastFetchedIndex = (this.currentRow + realSz - 1);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "refreshRow:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized int removeRowInCache(int rowIdx) throws SQLException {
        int retval = super.removeRowInCache(rowIdx);

        if (retval != 0) {
            if ((rowIdx >= this.beginLastFetchedIndex) && (rowIdx <= this.endLastFetchedIndex)
                    && (this.beginLastFetchedIndex != this.endLastFetchedIndex)) {
                this.endLastFetchedIndex -= 1;
            } else {
                this.beginLastFetchedIndex = (this.endLastFetchedIndex = 0);
            }
        }
        return retval;
    }

    private boolean handle_refetch() throws SQLException {
        if (((this.currentRow >= this.beginLastFetchedIndex) && (this.currentRow <= this.endLastFetchedIndex))
                || ((this.currentRow >= this.endLastFetchedIndex) && (this.currentRow <= this.beginLastFetchedIndex))) {
            return false;
        }

        refreshRow();

        return true;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.SensitiveScrollableResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.SensitiveScrollableResultSet JD-Core Version: 0.6.0
 */