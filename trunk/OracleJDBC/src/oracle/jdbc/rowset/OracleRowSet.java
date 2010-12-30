package oracle.jdbc.rowset;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.Joinable;
import oracle.jdbc.driver.OracleLog;

abstract class OracleRowSet implements Serializable, Cloneable, Joinable {
    protected String dataSource;
    protected String dataSourceName;
    protected String url;
    protected String username;
    protected String password;
    protected Map typeMap;
    protected int maxFieldSize;
    protected int maxRows;
    protected int queryTimeout;
    protected int fetchSize;
    protected int transactionIsolation;
    protected boolean escapeProcessing;
    protected String command;
    protected int concurrency;
    protected boolean readOnly;
    protected int fetchDirection;
    protected int rowsetType;
    protected boolean showDeleted;
    protected Vector listener;
    protected RowSetEvent rowsetEvent;
    protected Vector matchColumnIndexes;
    protected Vector matchColumnNames;

    protected OracleRowSet() throws SQLException {
        initializeProperties();

        this.matchColumnIndexes = new Vector(10);
        this.matchColumnNames = new Vector(10);

        this.listener = new Vector();
        this.rowsetEvent = new RowSetEvent((RowSet) this);
    }

    protected void initializeProperties() {
        this.command = null;
        this.concurrency = 1007;
        this.dataSource = null;
        this.dataSourceName = null;

        this.escapeProcessing = true;
        this.fetchDirection = 1002;
        this.fetchSize = 0;
        this.maxFieldSize = 0;
        this.maxRows = 0;
        this.queryTimeout = 0;
        this.readOnly = true;
        this.showDeleted = false;
        this.transactionIsolation = 2;
        this.rowsetType = 1005;
        this.typeMap = new HashMap();
        this.username = null;
        this.password = null;
        this.url = null;
    }

    public String getCommand() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getCommand () return " + this.command);
        }

        return this.command;
    }

    public int getConcurrency() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getConcurrency() return "
                    + this.concurrency);
        }

        return this.concurrency;
    }

    /** @deprecated */
    public String getDataSource() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.getDataSource () return "
                    + this.dataSource);
        }

        return this.dataSource;
    }

    public String getDataSourceName() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getDataSourceName () return "
                    + this.dataSourceName);
        }

        return this.dataSourceName;
    }

    public boolean getEscapeProcessing() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getEscapeProcessing() return "
                    + this.escapeProcessing);
        }

        return this.escapeProcessing;
    }

    public int getFetchDirection() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getFetchDirection() return "
                    + this.fetchDirection);
        }

        return this.fetchDirection;
    }

    public int getFetchSize() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleRowSet.getFetchSize() return " + this.fetchSize);
        }

        return this.fetchSize;
    }

    public int getMaxFieldSize() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getMaxFieldSize() return "
                    + this.maxFieldSize);
        }

        return this.maxFieldSize;
    }

    public int getMaxRows() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getMaxRows() return " + this.maxRows);
        }

        return this.maxRows;
    }

    public String getPassword() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getPassword () return " + this.password);
        }

        return this.password;
    }

    public int getQueryTimeout() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getQueryTimeout() return "
                    + this.queryTimeout);
        }

        return this.queryTimeout;
    }

    public boolean getReadOnly() {
        return isReadOnly();
    }

    public boolean isReadOnly() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.isReadOnly()");

            OracleLog.print(this, 1, 256, 16, "OracleRowSet.isReadOnly(), return " + this.readOnly);
        }

        return this.readOnly;
    }

    public boolean getShowDeleted() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.getShowDeleted(), return "
                    + this.showDeleted);
        }

        return this.showDeleted;
    }

    public int getTransactionIsolation() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getTransactionIsolation() return "
                    + this.transactionIsolation);
        }

        return this.transactionIsolation;
    }

    public int getType() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getType() return " + this.rowsetType);
        }

        return this.rowsetType;
    }

    public Map getTypeMap() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getTypeMap()");

            OracleLog.print(this, 1, 256, 64, "OracleRowSet.getTypeMap() return " + this.typeMap);
        }

        return this.typeMap;
    }

    public String getUrl() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getUrl () return " + this.url);
        }

        return this.url;
    }

    public String getUsername() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.getUsername () return " + this.username);
        }

        return this.username;
    }

    public void setCommand(String cmd) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setCommand( " + cmd + " ) ");
        }

        this.command = cmd;
    }

    public void setConcurrency(int con) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setConcurrency( " + con + " )");
        }

        if ((con == 1007) || (con == 1008))
            this.concurrency = con;
        else
            throw new SQLException("Invalid concurrancy mode");
    }

    /** @deprecated */
    public void setDataSource(String dataSource) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.setDataSource( " + dataSource + " )");
        }

        this.dataSource = dataSource;
    }

    public void setDataSourceName(String name) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setDataSourceName( " + name + " )");
        }

        this.dataSourceName = name;
    }

    public void setEscapeProcessing(boolean enable) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setEscapeProcessing( " + enable + " )");
        }

        this.escapeProcessing = enable;
    }

    public void setFetchDirection(int direction) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setFetchDirection( " + direction + " )");
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleRowSet.setFetchDirection(), rowsetType="
                    + this.rowsetType);
        }

        this.fetchDirection = direction;
    }

    public void setFetchSize(int rows) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setFetchSize( " + rows + " )");
        }

        this.fetchSize = rows;
    }

    public void setMaxFieldSize(int max) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setMaxFieldSize( " + max + " )");
        }

        this.maxFieldSize = max;
    }

    public void setMaxRows(int max) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setMaxRows( " + max + " )");
        }

        this.maxRows = max;
    }

    public void setPassword(String passwd) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setPassword( " + passwd + " ) ");
        }

        this.password = passwd;
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setQueryTimeout( " + seconds + " )");
        }

        this.queryTimeout = seconds;
    }

    public void setReadOnly(boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setReadOnly( " + value + " )");
        }

        this.readOnly = value;
    }

    public void setShowDeleted(boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.setShowDeleted(" + value + ")");
        }

        this.showDeleted = value;
    }

    public void setTransactionIsolation(int level) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setTransactionIsolation( " + level
                    + " )");
        }

        this.transactionIsolation = level;
    }

    public void setType(int ty) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setType( " + ty + " )");
        }

        if ((ty == 1003) || (ty == 1004) || (ty == 1005)) {
            this.rowsetType = ty;
        } else
            throw new SQLException("Unknown RowSet type");
    }

    public void setTypeMap(Map map) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setTypeMap(" + map + ")");
        }

        this.typeMap = map;
    }

    public void setUrl(String url) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setUrl( " + url + " ) ");
        }

        this.url = url;
    }

    public void setUsername(String user) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.setUsername( " + user + " ) ");
        }

        this.username = user;
    }

    public void addRowSetListener(RowSetListener rsListener) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.addRowSetListener(" + rsListener + ")");
        }

        for (int i = 0; i < this.listener.size(); i++)
            if (this.listener.elementAt(i).equals(rsListener))
                return;
        this.listener.add(rsListener);
    }

    public void removeRowSetListener(RowSetListener rowSetListener) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleRowSet.removeRowSetListener(" + rowSetListener
                    + ")");
        }

        for (int i = 0; i < this.listener.size(); i++)
            if (this.listener.elementAt(i).equals(rowSetListener))
                this.listener.remove(i);
    }

    protected synchronized void notifyCursorMoved() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.notifyCursorMoved()");
        }

        int size = this.listener.size();
        if (size > 0)
            for (int i = 0; i < size; i++)
                ((RowSetListener) this.listener.elementAt(i)).cursorMoved(this.rowsetEvent);
    }

    protected void notifyRowChanged() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.notifyRowChanged()");
        }

        int size = this.listener.size();
        if (size > 0)
            for (int i = 0; i < size; i++) {
                ((RowSetListener) this.listener.elementAt(i)).rowChanged(this.rowsetEvent);
            }
    }

    protected void notifyRowSetChanged() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleRowSet.notifyRowSetChanged()");
        }

        int size = this.listener.size();
        if (size > 0)
            for (int i = 0; i < size; i++) {
                ((RowSetListener) this.listener.elementAt(i)).rowSetChanged(this.rowsetEvent);
            }
    }

    public int[] getMatchColumnIndexes() throws SQLException {
        if ((this.matchColumnIndexes.size() == 0) && (this.matchColumnNames.size() == 0))
            throw new SQLException("No match column indexes were set");
        int[] indexes;
        if (this.matchColumnNames.size() > 0) {
            String[] names = getMatchColumnNames();
            int indexNum = names.length;
            indexes = new int[indexNum];

            for (int i = 0; i < indexNum; i++) {
                indexes[i] = findColumn(names[i]);
            }
        } else {
            int indexNum = this.matchColumnIndexes.size();
            indexes = new int[indexNum];
            int colIndex = -1;

            for (int i = 0; i < indexNum; i++) {
                try {
                    colIndex = ((Integer) this.matchColumnIndexes.get(i)).intValue();
                } catch (Exception exc) {
                    throw new SQLException("Invalid match column index");
                }

                if (colIndex <= 0) {
                    throw new SQLException("Invalid match column index");
                }

                indexes[i] = colIndex;
            }

        }

        return indexes;
    }

    public String[] getMatchColumnNames() throws SQLException {
        checkIfMatchColumnNamesSet();

        int nameNum = this.matchColumnNames.size();
        String[] names = new String[nameNum];
        String colName = null;

        for (int i = 0; i < nameNum; i++) {
            try {
                colName = (String) this.matchColumnNames.get(i);
            } catch (Exception exc) {
                throw new SQLException("Invalid match column name");
            }

            if ((colName == null) || (colName.equals(""))) {
                throw new SQLException("Invalid match column name");
            }

            names[i] = colName;
        }

        return names;
    }

    public void setMatchColumn(int columnIndex) throws SQLException {
        if (columnIndex <= 0) {
            throw new SQLException("The match column index should be greater than 0");
        }

        try {
            this.matchColumnIndexes.clear();
            this.matchColumnNames.clear();

            this.matchColumnIndexes.add(0, new Integer(columnIndex));
        } catch (Exception exc) {
            throw new SQLException("The match column index could not be set");
        }
    }

    public void setMatchColumn(int[] columnIndexes) throws SQLException {
        this.matchColumnIndexes.clear();
        this.matchColumnNames.clear();

        if (columnIndexes == null) {
            throw new SQLException("The match column parameter is null");
        }

        for (int i = 0; i < columnIndexes.length; i++) {
            if (columnIndexes[i] <= 0) {
                throw new SQLException("The match column index should be greater than 0");
            }

            try {
                this.matchColumnIndexes.add(i, new Integer(columnIndexes[i]));
            } catch (Exception exc) {
                throw new SQLException("The match column index could not be set");
            }
        }
    }

    public void setMatchColumn(String columnName) throws SQLException {
        if ((columnName == null) || (columnName.equals(""))) {
            throw new SQLException("The match column name should be non-empty");
        }

        try {
            this.matchColumnIndexes.clear();
            this.matchColumnNames.clear();

            this.matchColumnNames.add(0, columnName.trim());
        } catch (Exception exc) {
            throw new SQLException("The match column name could not be set");
        }
    }

    public void setMatchColumn(String[] columnNames) throws SQLException {
        this.matchColumnIndexes.clear();
        this.matchColumnNames.clear();

        for (int i = 0; i < columnNames.length; i++) {
            if ((columnNames[i] == null) || (columnNames[i].equals(""))) {
                throw new SQLException("The match column name should be non-empty");
            }

            try {
                this.matchColumnNames.add(i, columnNames[i].trim());
            } catch (Exception exc) {
                throw new SQLException("The match column name could not be set");
            }
        }
    }

    public void unsetMatchColumn(int columnIndex) throws SQLException {
        checkIfMatchColumnIndexesSet();

        if (columnIndex <= 0) {
            throw new SQLException("The match column index should be greater than 0");
        }

        int setColumnIndex = -1;
        try {
            setColumnIndex = ((Integer) this.matchColumnIndexes.get(0)).intValue();
        } catch (Exception exc) {
            throw new SQLException("No match column indexes were set");
        }

        if (setColumnIndex != columnIndex) {
            throw new SQLException("The column index being unset has not been set");
        }

        this.matchColumnIndexes.clear();
        this.matchColumnNames.clear();
    }

    public void unsetMatchColumn(int[] columnIndexes) throws SQLException {
        checkIfMatchColumnIndexesSet();

        if (columnIndexes == null) {
            throw new SQLException("The match column parameter is null");
        }

        int setColumnIndex = -1;

        for (int i = 0; i < columnIndexes.length; i++) {
            if (columnIndexes[i] <= 0) {
                throw new SQLException("The match column index should be greater than 0");
            }

            try {
                setColumnIndex = ((Integer) this.matchColumnIndexes.get(i)).intValue();
            } catch (Exception exc) {
                throw new SQLException("No match column indexes were set");
            }

            if (setColumnIndex == columnIndexes[i])
                continue;
            throw new SQLException("The column index being unset has not been set");
        }

        this.matchColumnIndexes.clear();
        this.matchColumnNames.clear();
    }

    public void unsetMatchColumn(String columnName) throws SQLException {
        checkIfMatchColumnNamesSet();

        if ((columnName == null) || (columnName.equals(""))) {
            throw new SQLException("The match column name should be non-empty");
        }

        String setColumnName = null;
        try {
            setColumnName = (String) this.matchColumnNames.get(0);
        } catch (Exception exc) {
            throw new SQLException("No match column names were set");
        }

        if (!setColumnName.equals(columnName.trim())) {
            throw new SQLException("The column name being unset has not been set");
        }

        this.matchColumnIndexes.clear();
        this.matchColumnNames.clear();
    }

    public void unsetMatchColumn(String[] columnNames) throws SQLException {
        checkIfMatchColumnNamesSet();

        if (columnNames == null) {
            throw new SQLException("The match column parameter is null");
        }

        String setColumnName = null;

        for (int i = 0; i < columnNames.length; i++) {
            if ((columnNames[i] == null) || (columnNames[i].equals(""))) {
                throw new SQLException("The match column name should be non-empty");
            }

            try {
                setColumnName = (String) this.matchColumnNames.get(i);
            } catch (Exception exc) {
                throw new SQLException("No match column names were set");
            }

            if (setColumnName.equals(columnNames[i]))
                continue;
            throw new SQLException("The column name being unset has not been set");
        }

        this.matchColumnIndexes.clear();
        this.matchColumnNames.clear();
    }

    protected void checkIfMatchColumnIndexesSet() throws SQLException {
        if (this.matchColumnIndexes.size() == 0)
            throw new SQLException("No match column indexes were set");
    }

    protected void checkIfMatchColumnNamesSet() throws SQLException {
        if (this.matchColumnNames.size() == 0)
            throw new SQLException("No match column names were set");
    }

    public abstract int findColumn(String paramString) throws SQLException;

    public abstract ResultSetMetaData getMetaData() throws SQLException;

    abstract String getTableName() throws SQLException;
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleRowSet JD-Core Version: 0.6.0
 */