package oracle.jdbc.rowset;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetWriter;
import oracle.jdbc.driver.OracleLog;

public class OracleCachedRowSetWriter implements RowSetWriter, Serializable {
    private StringBuffer updateClause = new StringBuffer("");

    private StringBuffer deleteClause = new StringBuffer("");

    private StringBuffer insertClause = new StringBuffer("");
    private PreparedStatement insertStmt;
    private PreparedStatement updateStmt;
    private PreparedStatement deleteStmt;
    private ResultSetMetaData rsmd;
    private transient Connection connection;
    private int columnCount;

    private String getSchemaName(RowSet rowset) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetReader.getSchemaName(RowSet)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSetReader.getSchemaName(" + rowset
                    + ")");
        }

        return rowset.getUsername();
    }

    private String getTableName(RowSet rowset) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.getTableName(RowSet)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSetWriter.getTableName(" + rowset
                    + ")");
        }

        String tableName = ((OracleCachedRowSet) rowset).getTableName();
        if (tableName != null) {
            return tableName;
        }
        String cmd = rowset.getCommand().toUpperCase();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSetWriter.getTableName(RowSet),cmd="
                    + cmd);
        }

        int index = cmd.indexOf(" FROM ");

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSetWriter.getTableName(RowSet),index=" + index);
        }

        if (index == -1) {
            throw new SQLException("Could not parse the SQL String to get the table name.\n"
                    + (cmd != "" ? cmd
                            : "Please use RowSet.setCommand (String) to set the SQL query string."));
        }

        String tab = cmd.substring(index + 6).trim();

        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 64, "OracleCachedRowSetWriter.getTableName(), tab=" + tab);
        }

        StringTokenizer st = new StringTokenizer(tab);
        if (st.hasMoreTokens()) {
            tab = st.nextToken();
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.getTableName(), return "
                    + tab);
        }

        return tab;
    }

    private void initSQLStatement(RowSet rowset) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.initSQLStatement(" + rowset
                    + ")");
        }

        this.insertClause = new StringBuffer("INSERT INTO " + getTableName(rowset) + "(");
        this.updateClause = new StringBuffer("UPDATE " + getTableName(rowset) + " SET ");
        this.deleteClause = new StringBuffer("DELETE FROM " + getTableName(rowset) + " WHERE ");

        this.rsmd = rowset.getMetaData();
        this.columnCount = this.rsmd.getColumnCount();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSetWriter.initSQLStatement(RowSet), insertClause="
                                    + this.insertClause + ", updateClause=" + this.updateClause
                                    + ", deleteClause=" + this.deleteClause + ", rsmd=" + this.rsmd
                                    + ", columnCount=" + this.columnCount);
        }

        for (int i = 0; i < this.columnCount; i++) {
            if (i != 0)
                this.insertClause.append(", ");
            this.insertClause.append(this.rsmd.getColumnName(i + 1));

            if (i != 0)
                this.updateClause.append(", ");
            this.updateClause.append(this.rsmd.getColumnName(i + 1) + " = :" + i);

            if (i != 0)
                this.deleteClause.append(" AND ");
            this.deleteClause.append(this.rsmd.getColumnName(i + 1) + " = :" + i);
        }
        this.insertClause.append(") VALUES (");
        this.updateClause.append(" WHERE ");

        for (int i = 0; i < this.columnCount; i++) {
            if (i != 0)
                this.insertClause.append(", ");
            this.insertClause.append(":" + i);

            if (i != 0)
                this.updateClause.append(" AND ");
            this.updateClause.append(this.rsmd.getColumnName(i + 1) + " = :" + i);
        }
        this.insertClause.append(")");

        this.insertStmt = this.connection.prepareStatement(this.insertClause
                .substring(0, this.insertClause.length()));

        this.updateStmt = this.connection.prepareStatement(this.updateClause
                .substring(0, this.updateClause.length()));

        this.deleteStmt = this.connection.prepareStatement(this.deleteClause
                .substring(0, this.deleteClause.length()));

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16,
                            "OracleCachedRowSetWriter.initSQLStatement(RowSet), insertStmt="
                                    + this.insertStmt + ", updateStmt=" + this.updateStmt
                                    + ", deleteStmt=" + this.deleteStmt);
        }
    }

    private boolean insertRow(OracleRow row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.insertRow(" + row + ")");
        }

        this.insertStmt.clearParameters();
        for (int i = 1; i <= this.columnCount; i++) {
            Object o = null;
            o = row.isColumnChanged(i) ? row.getModifiedColumn(i) : row.getColumn(i);

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 64,
                                "OracleCachedRowSetWriter.insertRow(OracleRow), o=" + o);
            }

            if (o == null)
                this.insertStmt.setNull(i, this.rsmd.getColumnType(i));
            else
                this.insertStmt.setObject(i, o);
        }
        return this.insertStmt.executeUpdate() == 1;
    }

    private boolean updateRow(RowSet rowset, OracleRow row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.updateRow(" + row + ")");
        }

        this.updateStmt.clearParameters();
        for (int i = 1; i <= this.columnCount; i++) {
            Object o = null;
            o = row.isColumnChanged(i) ? row.getModifiedColumn(i) : row.getColumn(i);

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 64,
                                "OracleCachedRowSetWriter.updateRow(OracleRow), o=" + o);
            }

            if (o == null)
                this.updateStmt.setNull(i, this.rsmd.getColumnType(i));
            else
                this.updateStmt.setObject(i, o);
        }
        for (int i = 1; i <= this.columnCount; i++) {
            if (row.isOriginalNull(i)) {
                return updateRowWithNull(rowset, row);
            }
            this.updateStmt.setObject(i + this.columnCount, row.getColumn(i));
        }

        return this.updateStmt.executeUpdate() == 1;
    }

    private boolean updateRowWithNull(RowSet rowset, OracleRow row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.updateRowWithNull(rowset="
                    + rowset + ", row=" + row + ")");
        }

        boolean returnValue = false;
        StringBuffer updateClauseWithNull = new StringBuffer("UPDATE " + getTableName(rowset)
                + " SET ");

        for (int i = 1; i <= this.columnCount; i++) {
            if (i != 1) {
                updateClauseWithNull.append(", ");
            }
            updateClauseWithNull.append(this.rsmd.getColumnName(i) + " = :" + i);
        }

        updateClauseWithNull.append(" WHERE ");

        for (int i = 1; i <= this.columnCount; i++) {
            if (i != 1)
                updateClauseWithNull.append(" AND ");
            if (row.isOriginalNull(i))
                updateClauseWithNull.append(this.rsmd.getColumnName(i) + " IS NULL ");
            else {
                updateClauseWithNull.append(this.rsmd.getColumnName(i) + " = :" + i);
            }
        }
        PreparedStatement stmt = null;
        try {
            stmt = this.connection.prepareStatement(updateClauseWithNull
                    .substring(0, updateClauseWithNull.length()));

            for (int i = 1; i <= this.columnCount; i++) {
                Object o = null;
                o = row.isColumnChanged(i) ? row.getModifiedColumn(i) : row.getColumn(i);

                if (OracleLog.TRACE) {
                    OracleLog
                            .print(this, 1, 256, 64,
                                   "OracleCachedRowSetWriter.updateRowWithNull(OracleRow), o=" + o);
                }

                if (o == null)
                    stmt.setNull(i, this.rsmd.getColumnType(i));
                else {
                    stmt.setObject(i, o);
                }
            }
            int i = 1;
            for (int columnIndex = 1; i <= this.columnCount; i++) {
                if (row.isOriginalNull(i)) {
                    continue;
                }
                stmt.setObject(columnIndex + this.columnCount, row.getColumn(i));

                columnIndex++;
            }
            returnValue = stmt.executeUpdate() == 1;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return returnValue;
    }

    private boolean deleteRow(RowSet rowset, OracleRow row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.deleteRow(" + row + ")");
        }

        this.deleteStmt.clearParameters();
        for (int i = 1; i <= this.columnCount; i++) {
            if (row.isOriginalNull(i)) {
                return deleteRowWithNull(rowset, row);
            }
            Object o = row.getColumn(i);
            if (o == null)
                this.deleteStmt.setNull(i, this.rsmd.getColumnType(i));
            else {
                this.deleteStmt.setObject(i, o);
            }
        }
        return this.deleteStmt.executeUpdate() == 1;
    }

    private boolean deleteRowWithNull(RowSet rowset, OracleRow row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.deleteRowWithNull(rowset="
                    + rowset + ", row=" + row + ")");
        }

        boolean returnValue = false;
        StringBuffer deleteClauseWithNull = new StringBuffer("DELETE FROM " + getTableName(rowset)
                + " WHERE ");

        for (int i = 1; i <= this.columnCount; i++) {
            if (i != 1)
                deleteClauseWithNull.append(" AND ");
            if (row.isOriginalNull(i))
                deleteClauseWithNull.append(this.rsmd.getColumnName(i) + " IS NULL ");
            else {
                deleteClauseWithNull.append(this.rsmd.getColumnName(i) + " = :" + i);
            }
        }
        PreparedStatement stmt = null;
        try {
            stmt = this.connection.prepareStatement(deleteClauseWithNull
                    .substring(0, deleteClauseWithNull.length()));

            int i = 1;
            for (int columnIndex = 1; i <= this.columnCount; i++) {
                if (row.isOriginalNull(i)) {
                    continue;
                }
                stmt.setObject(columnIndex++, row.getColumn(i));
            }
            returnValue = stmt.executeUpdate() == 1;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return returnValue;
    }

    public synchronized boolean writeData(RowSetInternal rowsetInternal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetWriter.writeData("
                    + rowsetInternal + ")");
        }

        OracleCachedRowSet rowset = (OracleCachedRowSet) rowsetInternal;
        this.connection = ((OracleCachedRowSetReader) rowset.getReader())
                .getConnection(rowsetInternal);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSetWriter.writeData(RowSetInternal),connection="
                                    + this.connection + ",columnCount=" + this.columnCount);
        }

        if (this.connection == null)
            throw new SQLException("Unable to get Connection");
        if (this.connection.getAutoCommit())
            this.connection.setAutoCommit(false);
        this.connection.setTransactionIsolation(rowset.getTransactionIsolation());
        initSQLStatement(rowset);
        if (this.columnCount < 1) {
            this.connection.close();
            return true;
        }
        boolean oldFlag = rowset.getShowDeleted();
        rowset.setShowDeleted(true);
        rowset.beforeFirst();
        boolean updateFlag = true;
        boolean insertFlag = true;
        boolean deleteFlag = true;
        OracleRow row = null;
        while (rowset.next()) {
            if (rowset.rowInserted()) {
                if (rowset.rowDeleted())
                    continue;
                row = rowset.getCurrentRow();

                insertFlag = (insertRow(row)) || (insertFlag);
                continue;
            }
            if (rowset.rowUpdated()) {
                row = rowset.getCurrentRow();

                updateFlag = (updateRow(rowset, row)) || (updateFlag);
                continue;
            }
            if (!rowset.rowDeleted())
                continue;
            row = rowset.getCurrentRow();

            deleteFlag = (deleteRow(rowset, row)) || (deleteFlag);
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSetWriter.writeData(RowSetInternal),oldFlag=" + oldFlag
                                    + ", updateFlag=" + updateFlag + ", insertFlag=" + insertFlag
                                    + ", deleteFlag" + deleteFlag + ", row=" + row);
        }

        if ((updateFlag) && (insertFlag) && (deleteFlag)) {
            this.connection.commit();

            rowset.setOriginal();
        } else {
            this.connection.rollback();
        }
        this.insertStmt.close();
        this.updateStmt.close();
        this.deleteStmt.close();

        if (!rowset.isConnectionStayingOpen()) {
            this.connection.close();
        }

        rowset.setShowDeleted(oldFlag);
        return true;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleCachedRowSetWriter JD-Core Version: 0.6.0
 */