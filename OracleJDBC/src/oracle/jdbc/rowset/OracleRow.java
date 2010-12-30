package oracle.jdbc.rowset;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Vector;
import oracle.jdbc.driver.OracleLog;

public class OracleRow implements Serializable, Cloneable {
    private Object[] column;
    private Object[] changedColumn;
    private boolean[] isOriginalNull;
    private byte[] columnChangeFlag;
    private int noColumn = 0;
    private int noColumnsInserted;
    private boolean rowDeleted = false;

    private boolean rowInserted = false;

    private final byte COLUMN_CHANGED = 17;

    private boolean rowUpdated = false;

    public OracleRow(int noColumn) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.OracleRow(" + noColumn + ")");
        }

        this.noColumn = noColumn;
        this.column = new Object[noColumn];
        this.changedColumn = new Object[noColumn];
        this.columnChangeFlag = new byte[noColumn];
        this.isOriginalNull = new boolean[noColumn];
        for (int i = 0; i < noColumn; i++)
            this.columnChangeFlag[i] = 0;
    }

    public OracleRow(int noColumn, boolean inserted) {
        this(noColumn);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.OracleRow(" + noColumn + ", " + inserted
                    + ")");
        }

        this.rowInserted = inserted;
        this.noColumnsInserted = 0;
    }

    public OracleRow(int noColumn, Object[] obj) {
        this(noColumn);

        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 16, "OracleRow.OracleRow(" + noColumn + ", Object[] obj)");

            OracleLog.print(this, 1, 256, 64, "OracleRow.OracleRow(" + noColumn + ", " + obj + ")");
        }

        System.arraycopy(obj, 0, this.column, 0, noColumn);
    }

    public void setColumnValue(int columnIndex, Object value) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.setColumnValue(" + columnIndex + ", "
                    + value + ")");
        }

        if (this.rowInserted)
            this.noColumnsInserted += 1;
        this.column[(columnIndex - 1)] = value;
    }

    void markOriginalNull(int columnIndex, boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.markOriginalNull(" + columnIndex + ", "
                    + value + ")");
        }

        this.isOriginalNull[(columnIndex - 1)] = value;
    }

    boolean isOriginalNull(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.isOriginalNull(" + columnIndex
                    + ") returned " + this.isOriginalNull[(columnIndex - 1)]);
        }

        return this.isOriginalNull[(columnIndex - 1)];
    }

    public void updateObject(int columnIndex, Object value) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.updateObject(" + columnIndex + ", "
                    + value + ")");
        }

        if (this.rowInserted)
            this.noColumnsInserted += 1;
        this.columnChangeFlag[(columnIndex - 1)] = 17;
        this.changedColumn[(columnIndex - 1)] = value;
    }

    public void cancelRowUpdates() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.cancelRowUpdates()");
        }

        this.noColumnsInserted = 0;
        for (int i = 0; i < this.noColumn; i++)
            this.columnChangeFlag[i] = 0;
        this.changedColumn = null;
        this.changedColumn = new Object[this.noColumn];
    }

    public Object getColumn(int columnIndex) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.getColumn(" + columnIndex + ")");
        }

        return this.column[(columnIndex - 1)];
    }

    public Object getModifiedColumn(int columnIndex) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.getModifiedColumn(" + columnIndex + ")");
        }

        return this.changedColumn[(columnIndex - 1)];
    }

    public boolean isColumnChanged(int columnIndex) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.isColumnChanged(" + columnIndex + ")");
        }

        return this.columnChangeFlag[(columnIndex - 1)] == 17;
    }

    public boolean isRowUpdated() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.isRowUpdated()");

            OracleLog.print(this, 1, 256, 64, "OracleRow.isRowUpdated(), rowInserted="
                    + this.rowInserted + ", rowDeleted=" + this.rowDeleted);
        }

        if ((this.rowInserted) || (this.rowDeleted)) {
            return false;
        }
        for (int i = 0; i < this.noColumn; i++) {
            if (this.columnChangeFlag[i] == 17) {
                return true;
            }

        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.isRowUpdated(), return false");
        }

        return false;
    }

    public void setRowUpdated(boolean value) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.setRowUpdated(" + value + ")");
        }

        this.rowUpdated = value;
        if (!value)
            cancelRowUpdates();
    }

    public boolean isRowInserted() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.isRowInserted(), return "
                    + this.rowInserted);
        }

        return this.rowInserted;
    }

    public void cancelRowDeletion() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.cancelRowDeletion()");
        }

        this.rowDeleted = false;
    }

    public void setRowDeleted(boolean value) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.setRowDeleted(" + value + ")");
        }

        this.rowDeleted = value;
    }

    public boolean isRowDeleted() {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 16, "OracleRow.isRowDeleted(), return " + this.rowDeleted);
        }

        return this.rowDeleted;
    }

    public Object[] getOriginalRow() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.getOriginalRow()");

            OracleLog.print(this, 1, 256, 16, "OracleRow.getOriginalRow(), return " + this.column);
        }

        return this.column;
    }

    public boolean isRowFullyPopulated() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.isRowFullyPopulated(), rowInserted="
                    + this.rowInserted);
        }

        if (!this.rowInserted) {
            return false;
        }
        return this.noColumnsInserted == this.noColumn;
    }

    public void setInsertedFlag(boolean value) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.setInsertedFlag(" + value + ")");
        }

        this.rowInserted = value;
    }

    void makeUpdatesOriginal() {
        for (int col = 0; col < this.noColumn; col++) {
            if (this.columnChangeFlag[col] != 17) {
                continue;
            }
            this.column[col] = this.changedColumn[col];
            this.changedColumn[col] = null;
            this.columnChangeFlag[col] = 0;
        }

        this.rowUpdated = false;
    }

    public void insertRow() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.insertRow()");
        }

        this.columnChangeFlag = null;
        this.columnChangeFlag = new byte[this.noColumn];
        System.arraycopy(this.changedColumn, 0, this.column, 0, this.noColumn);
        this.changedColumn = null;
        this.changedColumn = new Object[this.noColumn];
    }

    public Collection toCollection() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.toCollection()");
        }

        Vector collection = new Vector(this.noColumn);
        for (int i = 1; i <= this.noColumn; i++) {
            collection.add(isColumnChanged(i) ? getModifiedColumn(i) : getColumn(i));
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.toCollection(), collection=" + collection);
        }

        return collection;
    }

    public OracleRow createCopy() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.createCopy()");
        }

        OracleRow or = new OracleRow(this.noColumn);
        for (int i = 0; i < this.noColumn; i++) {
            or.column[i] = getCopy(this.column[i]);
            or.changedColumn[i] = getCopy(this.changedColumn[i]);
        }

        System.arraycopy(this.columnChangeFlag, 0, or.columnChangeFlag, 0, this.noColumn);
        or.noColumnsInserted = this.noColumnsInserted;
        or.rowDeleted = this.rowDeleted;
        or.rowInserted = this.rowInserted;
        or.rowUpdated = this.rowUpdated;

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.createCopy(), return " + or);
        }

        return or;
    }

    public Object getCopy(Object obj) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.getCopy(" + obj + ")");
        }

        Object objectCopy = null;
        try {
            if (obj == null) {
                return null;
            }
            if ((obj instanceof String)) {
                objectCopy = new String((String) obj);
            } else if ((obj instanceof Number)) {
                objectCopy = new BigDecimal(((Number) obj).toString());
            } else if ((obj instanceof Date)) {
                objectCopy = new Date(((Date) obj).getTime());
            } else if ((obj instanceof Timestamp)) {
                objectCopy = new Timestamp(((Timestamp) obj).getTime());
            } else if ((obj instanceof InputStream)) {
                objectCopy = new DataInputStream((InputStream) obj);
            } else if ((obj instanceof OutputStream)) {
                objectCopy = new DataOutputStream((OutputStream) obj);
            } else {
                throw new SQLException("Error, could not reproduce the copy of the object, "
                        + obj.getClass().getName());
            }
        } catch (Exception ea) {
            throw new SQLException("Error while creating a copy of the column of type, "
                    + obj.getClass().getName() + "\n" + ea.getMessage());
        }

        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 16, "OracleRow.getCopy(Object obj), return " + objectCopy);
        }

        return objectCopy;
    }

    public Object clone() throws CloneNotSupportedException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRow.clone()");
        }

        try {
            return createCopy();
        } catch (SQLException ea) {
            throw new CloneNotSupportedException("Error while cloning\n" + ea.getMessage());
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleRow JD-Core Version: 0.6.0
 */