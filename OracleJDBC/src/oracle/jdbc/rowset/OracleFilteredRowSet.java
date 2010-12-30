package oracle.jdbc.rowset;

import java.sql.SQLException;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;
import oracle.jdbc.driver.OracleLog;

public class OracleFilteredRowSet extends OracleWebRowSet implements FilteredRowSet {
    private Predicate predicate;

    public OracleFilteredRowSet() throws SQLException {
    }

    public void setFilter(Predicate p) throws SQLException {
        this.predicate = p;
    }

    public Predicate getFilter() {
        return this.predicate;
    }

    public boolean next() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleFilteredRowSet.next()");

            OracleLog.print(this, 1, 256, 64, "OracleFilteredRowSet.next(), rowCount="
                    + this.rowCount + ", fetchDirection=" + this.fetchDirection + ", presentRow="
                    + this.presentRow);
        }

        if (this.rowCount <= 0) {
            return false;
        }
        if (this.presentRow >= this.rowCount) {
            return false;
        }
        boolean isPresentRowQualified = false;
        do {
            this.presentRow += 1;

            if ((this.predicate != null)
                    && ((this.predicate == null) || (!this.predicate.evaluate(this)))) {
                continue;
            }
            isPresentRowQualified = true;
            break;
        }

        while (this.presentRow <= this.rowCount);

        if (isPresentRowQualified) {
            notifyCursorMoved();
            return true;
        }

        return false;
    }

    public boolean previous() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleFilteredRowSet.previous()");

            OracleLog.print(this, 1, 256, 64, "OracleFilteredRowSet.previous(), rowCount="
                    + this.rowCount + ", fetchDirection=" + this.fetchDirection + ", presentRow="
                    + this.presentRow);
        }

        if (this.rowsetType == 1003) {
            throw new SQLException("The RowSet type is TYPE_FORWARD_ONLY");
        }
        if (this.rowCount <= 0) {
            return false;
        }
        if (this.presentRow <= 1) {
            return false;
        }
        boolean isPresentRowQualified = false;
        do {
            this.presentRow -= 1;

            if ((this.predicate != null)
                    && ((this.predicate == null) || (!this.predicate.evaluate(this)))) {
                continue;
            }
            isPresentRowQualified = true;
            break;
        }

        while (this.presentRow >= 1);

        if (isPresentRowQualified) {
            notifyCursorMoved();
            return true;
        }

        return false;
    }

    public boolean absolute(int row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleFilteredRowSet.absolute(" + row + ")");

            OracleLog.print(this, 1, 256, 64, "OracleFilteredRowSet.absolute(" + row
                    + "), rowsetType=" + this.rowsetType + ", presentRow=" + this.presentRow);
        }

        if (this.rowsetType == 1003) {
            throw new SQLException("The RowSet type is TYPE_FORWARD_ONLY");
        }
        if ((row == 0) || (Math.abs(row) > this.rowCount)) {
            return false;
        }

        int positiveRow = row < 0 ? this.rowCount + row + 1 : row;

        int num = 0;
        this.presentRow = 0;

        while ((num < positiveRow) && (this.presentRow <= this.rowCount)) {
            if (next()) {
                num++;
                continue;
            }
            return false;
        }

        if (num == positiveRow) {
            notifyCursorMoved();
            return true;
        }

        return false;
    }

    protected void checkAndFilterObject(int columnIndex, Object obj) throws SQLException {
        if ((this.predicate != null) && (!this.predicate.evaluate(obj, columnIndex)))
            throw new SQLException("The object does not satisfy filtering criterion");
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleFilteredRowSet JD-Core Version: 0.6.0
 */