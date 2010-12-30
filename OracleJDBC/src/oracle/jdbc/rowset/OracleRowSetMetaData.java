package oracle.jdbc.rowset;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.sql.RowSetMetaData;
import oracle.jdbc.driver.OracleLog;

public class OracleRowSetMetaData implements RowSetMetaData, Serializable {
    private int columnCount;
    private int[] nullable;
    private int[] columnDisplaySize;
    private int[] precision;
    private int[] scale;
    private int[] columnType;
    private boolean[] searchable;
    private boolean[] caseSensitive;
    private boolean[] readOnly;
    private boolean[] writable;
    private boolean[] definatelyWritable;
    private boolean[] currency;
    private boolean[] autoIncrement;
    private boolean[] signed;
    private String[] columnLabel;
    private String[] schemaName;
    private String[] columnName;
    private String[] tableName;
    private String[] columnTypeName;
    private String[] catalogName;
    private String[] columnClassName;

    OracleRowSetMetaData(int colCount) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSetMetaData.OracleRowSetMetaData()");

            OracleLog.print(this, 1, 256, 64, "OracleRowSetMetaData.OracleRowSetMetaData()");
        }

        this.columnCount = colCount;
        this.searchable = new boolean[this.columnCount];
        this.caseSensitive = new boolean[this.columnCount];
        this.readOnly = new boolean[this.columnCount];
        this.nullable = new int[this.columnCount];
        this.signed = new boolean[this.columnCount];
        this.columnDisplaySize = new int[this.columnCount];
        this.columnType = new int[this.columnCount];
        this.columnLabel = new String[this.columnCount];
        this.columnName = new String[this.columnCount];
        this.schemaName = new String[this.columnCount];
        this.precision = new int[this.columnCount];
        this.scale = new int[this.columnCount];
        this.tableName = new String[this.columnCount];
        this.columnTypeName = new String[this.columnCount];
        this.writable = new boolean[this.columnCount];
        this.definatelyWritable = new boolean[this.columnCount];
        this.currency = new boolean[this.columnCount];
        this.autoIncrement = new boolean[this.columnCount];
        this.catalogName = new String[this.columnCount];
        this.columnClassName = new String[this.columnCount];

        for (int i = 0; i < this.columnCount; i++) {
            this.searchable[i] = false;
            this.caseSensitive[i] = false;
            this.readOnly[i] = false;
            this.nullable[i] = 1;
            this.signed[i] = false;
            this.columnDisplaySize[i] = 0;
            this.columnType[i] = 0;
            this.columnLabel[i] = "";
            this.columnName[i] = "";
            this.schemaName[i] = "";
            this.precision[i] = 0;
            this.scale[i] = 0;
            this.tableName[i] = "";
            this.columnTypeName[i] = "";
            this.writable[i] = false;
            this.definatelyWritable[i] = false;
            this.currency[i] = false;
            this.autoIncrement[i] = true;
            this.catalogName[i] = "";
            this.columnClassName[i] = "";
        }
    }

    OracleRowSetMetaData(ResultSetMetaData rsmd) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16,
                            "OracleRowSetMetaData.OracleRowSetMetaData(ResultSetMetaData)");

            OracleLog.print(this, 1, 256, 64, "OracleRowSetMetaData.OracleRowSetMetaData(" + rsmd
                    + ")");
        }

        this.columnCount = rsmd.getColumnCount();
        this.searchable = new boolean[this.columnCount];
        this.caseSensitive = new boolean[this.columnCount];
        this.readOnly = new boolean[this.columnCount];
        this.nullable = new int[this.columnCount];
        this.signed = new boolean[this.columnCount];
        this.columnDisplaySize = new int[this.columnCount];
        this.columnType = new int[this.columnCount];
        this.columnLabel = new String[this.columnCount];
        this.columnName = new String[this.columnCount];
        this.schemaName = new String[this.columnCount];
        this.precision = new int[this.columnCount];
        this.scale = new int[this.columnCount];
        this.tableName = new String[this.columnCount];
        this.columnTypeName = new String[this.columnCount];
        this.writable = new boolean[this.columnCount];
        this.definatelyWritable = new boolean[this.columnCount];
        this.currency = new boolean[this.columnCount];
        this.autoIncrement = new boolean[this.columnCount];
        this.catalogName = new String[this.columnCount];
        this.columnClassName = new String[this.columnCount];

        for (int i = 0; i < this.columnCount; i++) {
            this.searchable[i] = rsmd.isSearchable(i + 1);
            this.caseSensitive[i] = rsmd.isCaseSensitive(i + 1);
            this.readOnly[i] = rsmd.isReadOnly(i + 1);
            this.nullable[i] = rsmd.isNullable(i + 1);
            this.signed[i] = rsmd.isSigned(i + 1);
            this.columnDisplaySize[i] = rsmd.getColumnDisplaySize(i + 1);
            this.columnType[i] = rsmd.getColumnType(i + 1);
            this.columnLabel[i] = rsmd.getColumnLabel(i + 1);
            this.columnName[i] = rsmd.getColumnName(i + 1);
            this.schemaName[i] = rsmd.getSchemaName(i + 1);

            if ((this.columnType[i] == 2) || (this.columnType[i] == 2)
                    || (this.columnType[i] == -5) || (this.columnType[i] == 3)
                    || (this.columnType[i] == 8) || (this.columnType[i] == 6)
                    || (this.columnType[i] == 4)) {
                this.precision[i] = rsmd.getPrecision(i + 1);
                this.scale[i] = rsmd.getScale(i + 1);
            } else {
                this.precision[i] = 0;
                this.scale[i] = 0;
            }

            this.tableName[i] = rsmd.getTableName(i + 1);
            this.columnTypeName[i] = rsmd.getColumnTypeName(i + 1);
            this.writable[i] = rsmd.isWritable(i + 1);
            this.definatelyWritable[i] = rsmd.isDefinitelyWritable(i + 1);
            this.currency[i] = rsmd.isCurrency(i + 1);
            this.autoIncrement[i] = rsmd.isAutoIncrement(i + 1);
            this.catalogName[i] = rsmd.getCatalogName(i + 1);
            this.columnClassName[i] = rsmd.getColumnClassName(i + 1);
        }
    }

    private void validateColumnIndex(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSetMetaData.validateColumnIndex(" + column
                    + "), columnCount=" + this.columnCount);
        }

        if ((column < 1) || (column > this.columnCount))
            throw new SQLException("Invalid column index : " + column);
    }

    public int getColumnCount() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnCount()");

            OracleLog.print(this, 1, 256, 16, "OracleRowSetMetaData.getColumnCount(), return "
                    + this.columnCount);
        }

        return this.columnCount;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleRowSetMetaData.isAutoIncrement(" + column + ")");
        }

        validateColumnIndex(column);
        return this.autoIncrement[(column - 1)];
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleRowSetMetaData.isCaseSensitive(" + column + ")");
        }

        validateColumnIndex(column);
        return this.caseSensitive[(column - 1)];
    }

    public boolean isSearchable(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isSearchable(" + column + ")");
        }

        validateColumnIndex(column);
        return this.searchable[(column - 1)];
    }

    public boolean isCurrency(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isCurrency(" + column + ")");
        }

        validateColumnIndex(column);
        return this.currency[(column - 1)];
    }

    public int isNullable(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isNullable(" + column + ")");
        }

        validateColumnIndex(column);
        return this.nullable[(column - 1)];
    }

    public boolean isSigned(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isSigned(" + column + ")");
        }

        validateColumnIndex(column);
        return this.signed[(column - 1)];
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnDisplaySize(" + column
                    + ")");
        }

        validateColumnIndex(column);
        return this.columnDisplaySize[(column - 1)];
    }

    public String getColumnLabel(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnLabel(" + column + ")");
        }

        validateColumnIndex(column);
        return this.columnLabel[(column - 1)];
    }

    public String getColumnName(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnName(" + column + ")");
        }

        validateColumnIndex(column);
        return this.columnName[(column - 1)];
    }

    public String getSchemaName(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getSchemaName(" + column + ")");
        }

        validateColumnIndex(column);
        return this.schemaName[(column - 1)];
    }

    public int getPrecision(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getPrecision(" + column + ")");
        }

        validateColumnIndex(column);
        return this.precision[(column - 1)];
    }

    public int getScale(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getScale(" + column + ")");
        }

        validateColumnIndex(column);
        return this.scale[(column - 1)];
    }

    public String getTableName(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getTableName(" + column + ")");
        }

        validateColumnIndex(column);
        return this.tableName[(column - 1)];
    }

    public String getCatalogName(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getCatalogName(" + column + ")");
        }

        validateColumnIndex(column);
        return this.catalogName[(column - 1)];
    }

    public int getColumnType(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnType(" + column + ")");
        }

        validateColumnIndex(column);
        return this.columnType[(column - 1)];
    }

    public String getColumnTypeName(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnTypeName(" + column
                    + ")");
        }

        validateColumnIndex(column);
        return this.columnTypeName[(column - 1)];
    }

    public boolean isReadOnly(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isReadOnly(" + column + ")");
        }

        validateColumnIndex(column);
        return this.readOnly[(column - 1)];
    }

    public boolean isWritable(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isWritable(" + column + ")");
        }

        validateColumnIndex(column);
        return this.writable[(column - 1)];
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.isDefinitelyWritable(" + column
                    + ")");
        }

        validateColumnIndex(column);
        return this.definatelyWritable[(column - 1)];
    }

    public String getColumnClassName(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.getColumnClassName(" + column
                    + ")");
        }

        validateColumnIndex(column);
        return this.columnClassName[(column - 1)];
    }

    public void setAutoIncrement(int columnIndex, boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setAutoIncrement(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.autoIncrement[(columnIndex - 1)] = value;
    }

    public void setCaseSensitive(int columnIndex, boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setCaseSensitive(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.caseSensitive[(columnIndex - 1)] = value;
    }

    public void setCatalogName(int columnIndex, String value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setCatalogName(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.catalogName[(columnIndex - 1)] = value;
    }

    public void setColumnCount(int value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setColumnCount(" + value + ")");
        }

        this.columnCount = value;
    }

    public void setColumnDisplaySize(int columnIndex, int value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setColumnDisplaySize("
                    + columnIndex + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.columnDisplaySize[(columnIndex - 1)] = value;
    }

    public void setColumnLabel(int columnIndex, String value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setColumnLabel(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.columnLabel[(columnIndex - 1)] = value;
    }

    public void setColumnName(int columnIndex, String value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setColumnName(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.columnName[(columnIndex - 1)] = value;
    }

    public void setColumnType(int columnIndex, int value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setColumnType(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.columnType[(columnIndex - 1)] = value;
    }

    public void setColumnTypeName(int columnIndex, String value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setColumnTypeName("
                    + columnIndex + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.columnTypeName[(columnIndex - 1)] = value;
    }

    public void setCurrency(int columnIndex, boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setCurrency(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.currency[(columnIndex - 1)] = value;
    }

    public void setNullable(int columnIndex, int value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setNullable(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.nullable[(columnIndex - 1)] = value;
    }

    public void setPrecision(int columnIndex, int value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setPrecision(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.precision[(columnIndex - 1)] = value;
    }

    public void setScale(int columnIndex, int value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setScale(" + columnIndex + ", "
                    + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.scale[(columnIndex - 1)] = value;
    }

    public void setSchemaName(int columnIndex, String value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setSchemaName(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.schemaName[(columnIndex - 1)] = value;
    }

    public void setSearchable(int columnIndex, boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setSearchable(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.searchable[(columnIndex - 1)] = value;
    }

    public void setSigned(int columnIndex, boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setSigned(" + columnIndex + ", "
                    + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.signed[(columnIndex - 1)] = value;
    }

    public void setTableName(int columnIndex, String value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSetMetaData.setTableName(" + columnIndex
                    + ", " + value + ")");
        }

        validateColumnIndex(columnIndex);
        this.tableName[(columnIndex - 1)] = value;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleRowSetMetaData JD-Core Version: 0.6.0
 */