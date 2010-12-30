package oracle.jdbc.driver;

import java.sql.SQLException;

class AutoKeyInfo extends OracleResultSetMetaData {
    String originalSql;
    String newSql;
    String tableName;
    String[] columnNames;
    int[] columnIndexes;
    int numColumns;
    String[] tableColumnNames;
    int[] tableColumnTypes;
    int[] tableMaxLengths;
    boolean[] tableNullables;
    short[] tableFormOfUses;
    int[] tablePrecisions;
    int[] tableScales;
    String[] tableTypeNames;
    int autoKeyType;
    static final int KEYFLAG = 0;
    static final int COLUMNAME = 1;
    static final int COLUMNINDEX = 2;
    int[] returnTypes;
    Accessor[] returnAccessors;

    AutoKeyInfo(String sql) {
        this.originalSql = sql;
        this.autoKeyType = 0;
    }

    AutoKeyInfo(String sql, String[] colNames) {
        this.originalSql = sql;
        this.columnNames = colNames;
        this.autoKeyType = 1;
    }

    AutoKeyInfo(String sql, int[] colIndexes) {
        this.originalSql = sql;
        this.columnIndexes = colIndexes;
        this.autoKeyType = 2;
    }

    String getNewSql() throws SQLException {
        if (this.newSql != null)
            return this.newSql;

        switch (this.autoKeyType) {
        case 0:
            this.newSql = (this.originalSql + " RETURNING ROWID INTO ?");
            this.returnTypes = new int[1];
            this.returnTypes[0] = 104;
            return this.newSql;
        case 1:
            return getNewSqlByColumnName();
        case 2:
            return getNewSqlByColumnIndexes();
        }

        DatabaseError.throwSqlException(89);
        return null;
    }

    private String getNewSqlByColumnName() throws SQLException {
        this.returnTypes = new int[this.columnNames.length];

        this.columnIndexes = new int[this.columnNames.length];

        StringBuffer newSqlBuf = new StringBuffer(this.originalSql);
        newSqlBuf.append(" RETURNING ");

        for (int i = 0; i < this.columnNames.length; i++) {
            int type = getReturnParamTypeCode(i, this.columnNames[i], this.columnIndexes);
            this.returnTypes[i] = type;

            newSqlBuf.append(this.columnNames[i]);

            if (i >= this.columnNames.length - 1)
                continue;
            newSqlBuf.append(", ");
        }

        newSqlBuf.append(" INTO ");

        for (int i = 0; i < this.columnNames.length - 1; i++) {
            newSqlBuf.append("?, ");
        }

        newSqlBuf.append("?");

        this.newSql = new String(newSqlBuf);
        return this.newSql;
    }

    private String getNewSqlByColumnIndexes() throws SQLException {
        this.returnTypes = new int[this.columnIndexes.length];

        StringBuffer newSqlBuf = new StringBuffer(this.originalSql);
        newSqlBuf.append(" RETURNING ");

        for (int i = 0; i < this.columnIndexes.length; i++) {
            int index = this.columnIndexes[i] - 1;
            if ((index < 0) || (index > this.tableColumnNames.length)) {
                DatabaseError.throwSqlException(68);
            }

            int type = this.tableColumnTypes[index];
            String name = this.tableColumnNames[index];
            this.returnTypes[i] = type;

            newSqlBuf.append(name);

            if (i >= this.columnIndexes.length - 1)
                continue;
            newSqlBuf.append(", ");
        }

        newSqlBuf.append(" INTO ");

        for (int i = 0; i < this.columnIndexes.length - 1; i++) {
            newSqlBuf.append("?, ");
        }

        newSqlBuf.append("?");

        this.newSql = new String(newSqlBuf);
        return this.newSql;
    }

    private final int getReturnParamTypeCode(int pos, String columnName, int[] columnIndexes)
            throws SQLException {
        for (int i = 0; i < this.tableColumnNames.length; i++) {
            if (!columnName.equalsIgnoreCase(this.tableColumnNames[i]))
                continue;
            columnIndexes[pos] = (i + 1);
            return this.tableColumnTypes[i];
        }

        DatabaseError.throwSqlException(68);

        return -1;
    }

    static final boolean isInsertSqlStmt(String sql) throws SQLException {
        if (sql == null) {
            DatabaseError.throwSqlException(68);
        }

        return sql.trim().toUpperCase().startsWith("INSERT");
    }

    String getTableName() throws SQLException {
        if (this.tableName != null)
            return this.tableName;

        String s = this.originalSql.trim().toUpperCase();

        int pos = s.indexOf("INSERT");
        pos = s.indexOf("INTO", pos);

        if (pos < 0) {
            DatabaseError.throwSqlException(68);
        }
        int len = s.length();
        int start = pos + 5;

        while ((start < len) && (s.charAt(start) == ' '))
            start++;

        if (start >= len) {
            DatabaseError.throwSqlException(68);
        }
        int end = start + 1;

        while ((end < len) && (s.charAt(end) != ' ') && (s.charAt(end) != '('))
            end++;

        if (start == end - 1) {
            DatabaseError.throwSqlException(68);
        }
        this.tableName = s.substring(start, end);

        return this.tableName;
    }

    void allocateSpaceForDescribedData(int numColumns) throws SQLException {
        this.numColumns = numColumns;

        this.tableColumnNames = new String[numColumns];
        this.tableColumnTypes = new int[numColumns];
        this.tableMaxLengths = new int[numColumns];
        this.tableNullables = new boolean[numColumns];
        this.tableFormOfUses = new short[numColumns];
        this.tablePrecisions = new int[numColumns];
        this.tableScales = new int[numColumns];
        this.tableTypeNames = new String[numColumns];
    }

    void fillDescribedData(int index, String colName, int type, int maxLength, boolean nullable,
            short form, int precision, int scale, String typeName) throws SQLException {
        this.tableColumnNames[index] = colName;
        this.tableColumnTypes[index] = type;
        this.tableMaxLengths[index] = maxLength;
        this.tableNullables[index] = nullable;
        this.tableFormOfUses[index] = form;
        this.tablePrecisions[index] = precision;
        this.tableScales[index] = scale;
        this.tableTypeNames[index] = typeName;
    }

    void initMetaData(OracleReturnResultSet rset) throws SQLException {
        if (this.returnAccessors != null)
            return;

        this.returnAccessors = rset.returnAccessors;

        switch (this.autoKeyType) {
        case 0:
            initMetaDataKeyFlag();
            break;
        case 1:
        case 2:
            initMetaDataColumnIndexes();
        }
    }

    void initMetaDataKeyFlag() throws SQLException {
        this.returnAccessors[0].columnName = "ROWID";
        this.returnAccessors[0].describeType = 104;
        this.returnAccessors[0].describeMaxLength = 4;
        this.returnAccessors[0].nullable = true;
        this.returnAccessors[0].precision = 0;
        this.returnAccessors[0].scale = 0;
        this.returnAccessors[0].formOfUse = 0;
    }

    void initMetaDataColumnIndexes() throws SQLException {
        for (int i = 0; i < this.returnAccessors.length; i++) {
            Accessor accessor = this.returnAccessors[i];
            int index = this.columnIndexes[i] - 1;

            accessor.columnName = this.tableColumnNames[index];
            accessor.describeType = this.tableColumnTypes[index];
            accessor.describeMaxLength = this.tableMaxLengths[index];
            accessor.nullable = this.tableNullables[index];
            accessor.precision = this.tablePrecisions[index];
            accessor.scale = this.tablePrecisions[index];
            accessor.formOfUse = this.tableFormOfUses[index];
        }
    }

    int getValidColumnIndex(int column) throws SQLException {
        if ((column <= 0) || (column > this.returnAccessors.length)) {
            DatabaseError.throwSqlException(3);
        }
        return column - 1;
    }

    public int getColumnCount() throws SQLException {
        return this.returnAccessors.length;
    }

    public String getColumnName(int column) throws SQLException {
        if ((column <= 0) || (column > this.returnAccessors.length)) {
            DatabaseError.throwSqlException(3);
        }
        return this.returnAccessors[(column - 1)].columnName;
    }

    public String getTableName(int column) throws SQLException {
        if ((column <= 0) || (column > this.returnAccessors.length)) {
            DatabaseError.throwSqlException(3);
        }
        return getTableName();
    }

    Accessor[] getDescription() throws SQLException {
        return this.returnAccessors;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.AutoKeyInfo JD-Core Version: 0.6.0
 */