package oracle.jdbc.rowset;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncFactoryException;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

public class OracleCachedRowSet extends OracleRowSet implements RowSet, RowSetInternal,
        Serializable, Cloneable, CachedRowSet {
    private SQLWarning sqlWarning;
    private RowSetWarning rowsetWarning;
    protected int presentRow;
    private int currentPage;
    private boolean isPopulateDone;
    private boolean previousColumnWasNull;
    private OracleRow insertRow;
    private int insertRowPosition;
    private boolean insertRowFlag;
    private int updateRowPosition;
    private boolean updateRowFlag;
    protected ResultSetMetaData rowsetMetaData;
    private transient ResultSet resultSet;
    private transient Connection connection;
    private transient boolean isConnectionStayingOpenForTxnControl = false;
    protected Vector rows;
    private Vector param;
    private String[] metaData;
    protected int colCount;
    protected int rowCount;
    private RowSetReader reader;
    private RowSetWriter writer;
    private int[] keyColumns;
    private int pageSize;
    private SyncProvider syncProvider;
    private static final String DEFAULT_SYNCPROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
    private String tableName;
    private boolean driverManagerInitialized = false;

    public OracleCachedRowSet() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.OracleCachedRowSet ()");
        }

        this.insertRowFlag = false;
        this.updateRowFlag = false;

        this.presentRow = 0;
        this.previousColumnWasNull = false;

        this.param = new Vector();

        this.rows = new Vector();

        this.sqlWarning = new SQLWarning();
        try {
            this.syncProvider = SyncFactory
                    .getInstance("com.sun.rowset.providers.RIOptimisticProvider");
        } catch (SyncFactoryException exc) {
            throw new SQLException("SyncProvider instance not constructed.");
        }

        setReader(new OracleCachedRowSetReader());
        setWriter(new OracleCachedRowSetWriter());

        this.currentPage = 0;
        this.pageSize = 0;
        this.isPopulateDone = false;

        this.keyColumns = null;
        this.tableName = null;
    }

    public Connection getConnection() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getConnection ()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getConnection () return "
                    + this.connection);
        }

        return getConnectionInternal();
    }

    Connection getConnectionInternal() throws SQLException {
        if ((this.connection == null) || (this.connection.isClosed())) {
            String userName = getUsername();
            String password = getPassword();
            if (getDataSourceName() != null) {
                try {
                    InitialContext initialcontext = null;

                    if (OracleLog.TRACE) {
                        OracleLog.print(this, 1, 256, 64,
                                        "OracleCachedRowSet.getConnectionInternal(), initialcontext="
                                                + initialcontext);
                    }

                    try {
                        Properties props = System.getProperties();
                        initialcontext = new InitialContext(props);
                    } catch (SecurityException ea) {
                    }
                    if (initialcontext == null)
                        initialcontext = new InitialContext();
                    DataSource datasource = (DataSource) initialcontext.lookup(getDataSourceName());

                    if (OracleLog.TRACE) {
                        OracleLog.print(this, 1, 256, 64,
                                        "OracleCachedRowSe.getConnectionInternal(),datasource="
                                                + datasource);
                    }

                    if ((this.username == null) || (password == null))
                        this.connection = datasource.getConnection();
                    else
                        this.connection = datasource.getConnection(this.username, password);
                } catch (NamingException ea) {
                    throw new SQLException("Unable to connect through the DataSource\n"
                            + ea.getMessage());
                }

            } else if (getUrl() != null) {
                if (!this.driverManagerInitialized) {
                    DriverManager.registerDriver(new OracleDriver());
                    this.driverManagerInitialized = true;
                }
                String url = getUrl();

                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleCachedRowSet.getConnectionInternal(): url = " + url
                                            + ", userName = " + userName + ", password = "
                                            + password);
                }

                if ((url.equals("")) || (userName.equals("")) || (password.equals(""))) {
                    throw new SQLException("One or more of the authenticating parameter not set");
                }

                this.connection = DriverManager.getConnection(url, userName, password);
            } else {
                throw new SQLException("Authentication parameters not set");
            }
        }

        return this.connection;
    }

    public Statement getStatement() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getStatement()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getStatement(), connenction="
                    + this.connection + ", " + "resultSet=" + this.resultSet);
        }

        if (this.resultSet == null) {
            throw new SQLException("ResultSet not open");
        }

        return this.resultSet.getStatement();
    }

    public RowSetReader getReader() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getReader() return "
                    + this.reader);
        }

        return this.reader;
    }

    public RowSetWriter getWriter() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getWriter() ");
        }

        return this.writer;
    }

    public void setFetchDirection(int direction) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setFetchDirection( " + direction
                    + " )");
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.setFetchDirection(), rowsetType="
                    + this.rowsetType);
        }

        if (this.rowsetType == 1005) {
            throw new SQLException(
                    "Fetch direction cannot be applied when RowSet type is TYPE_SCROLL_SENSITIVE");
        }
        switch (direction) {
        case 1000:
        case 1002:
            this.presentRow = 0;
            break;
        case 1001:
            if (this.rowsetType == 1003) {
                throw new SQLException(
                        "FETCH_REVERSE cannot be applied when RowSet type is TYPE_FORWARD_ONLY");
            }
            this.presentRow = (this.rowCount + 1);
            break;
        default:
            throw new SQLException("Illegal fetch direction");
        }

        super.setFetchDirection(direction);
    }

    public void setReader(RowSetReader r) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setReader(" + r + ")");
        }

        this.reader = r;
    }

    public void setWriter(RowSetWriter w) {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setWriter(" + w + ")");
        }

        this.writer = w;
    }

    private final int getColumnIndex(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getColumnIndex(" + columnName
                    + ")");
        }

        columnName = columnName.toUpperCase();
        int columnIndex = 0;
        for (; columnIndex < this.metaData.length; columnIndex++) {
            if (columnName.equals(this.metaData[columnIndex])) {
                break;
            }

        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getColumnIndex(" + columnName
                    + "),columnIndex=" + columnIndex + ", return " + (columnIndex + 1));
        }

        if (columnIndex >= this.metaData.length) {
            throw new SQLException("Invalid column name: " + columnName);
        }
        return columnIndex + 1;
    }

    private final void checkColumnIndex(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.checkColumnIndex(" + columnIndex
                    + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.checkColumnIndex ( "
                    + columnIndex + " ), readOnly = " + this.readOnly);
        }

        if (this.readOnly)
            throw new SQLException("The RowSet is not write enabled");
        if ((columnIndex < 1) || (columnIndex > this.colCount))
            throw new SQLException("invalid index : " + columnIndex);
    }

    private final boolean isUpdated(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.isUpdated ( " + columnIndex
                    + " )");

            OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.isUpdated ( " + columnIndex
                    + " ), colCount=" + this.colCount);
        }

        if ((columnIndex < 1) || (columnIndex > this.colCount))
            throw new SQLException("Invalid index : " + columnIndex);
        return getCurrentRow().isColumnChanged(columnIndex);
    }

    private final void checkParamIndex(int parameterIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.checkParamIndex ( "
                    + parameterIndex + " )");
        }

        if (parameterIndex < 1)
            throw new SQLException("Invalid parameter index : " + parameterIndex);
    }

    private final void populateInit(ResultSet rs) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.populateInit (" + rs + ")");
        }

        this.resultSet = rs;
        Statement stmt = rs.getStatement();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.populateInit (" + rs + "), stmt="
                    + stmt);
        }

        this.maxFieldSize = stmt.getMaxFieldSize();

        this.fetchSize = stmt.getFetchSize();
        this.queryTimeout = stmt.getQueryTimeout();

        this.connection = stmt.getConnection();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.populateInit (" + rs
                    + "), connection=" + this.connection);
        }

        this.transactionIsolation = this.connection.getTransactionIsolation();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.populateInit (" + rs
                    + "), connection=" + this.connection);
        }

        this.typeMap = this.connection.getTypeMap();
        DatabaseMetaData dbmd = this.connection.getMetaData();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.populateInit (" + rs
                    + "), typeMap=" + this.typeMap + ", dbmd=" + dbmd);
        }

        this.url = dbmd.getURL();
        this.username = dbmd.getUserName();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.populateInit (" + rs + "),url="
                    + this.url + ",username=" + this.username);
        }
    }

    private synchronized InputStream getStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getStream( " + columnIndex + ")");
        }

        Object o = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getStream( " + columnIndex
                    + " ), this.getObject(" + columnIndex + ")=" + o);
        }

        if (o == null) {
            return null;
        }
        if ((o instanceof InputStream)) {
            return (InputStream) o;
        }
        if ((o instanceof String)) {
            return new ByteArrayInputStream(((String) o).getBytes());
        }
        if ((o instanceof byte[])) {
            return new ByteArrayInputStream((byte[]) o);
        }
        if ((o instanceof OracleSerialClob))
            return ((OracleSerialClob) o).getAsciiStream();
        if ((o instanceof OracleSerialBlob))
            return ((OracleSerialBlob) o).getBinaryStream();
        if ((o instanceof Reader)) {
            try {
                Reader r = new BufferedReader((Reader) o);
                int b = 0;
                PipedInputStream istream = new PipedInputStream();
                PipedOutputStream ostream = new PipedOutputStream(istream);
                while ((b = r.read()) != -1)
                    ostream.write(b);
                ostream.close();
                return istream;
            } catch (IOException ea) {
                throw new SQLException("Error during conversion: " + ea.getMessage());
            }
        }

        throw new SQLException("Could not convert the column into a stream type");
    }

    protected synchronized void notifyCursorMoved() {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.notifyCursorMoved()");

            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSet.notifyCursorMoved(), insertRowFlag= "
                                    + this.insertRowFlag + ", updateRowFlag=" + this.updateRowFlag);
        }

        if (this.insertRowFlag) {
            this.insertRowFlag = false;
            this.insertRow.setRowUpdated(false);
            this.sqlWarning.setNextWarning(new SQLWarning(
                    "Cancelling insertion, due to cursor movement."));
        } else if (this.updateRowFlag) {
            try {
                this.updateRowFlag = false;
                int temp = this.presentRow;
                this.presentRow = this.updateRowPosition;
                getCurrentRow().setRowUpdated(false);
                this.presentRow = temp;
                this.sqlWarning.setNextWarning(new SQLWarning(
                        "Cancelling all updates, due to cursor movement."));
            } catch (SQLException ea) {
            }

        }

        super.notifyCursorMoved();
    }

    protected void checkAndFilterObject(int columnIndex, Object obj) throws SQLException {
    }

    OracleRow getCurrentRow() throws SQLException {
        int rowno = this.presentRow - 1;

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getCurrentRow ()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getCurrentRow (), rowno = "
                    + rowno + ", presentRow=" + this.presentRow + ",rowCount=" + this.rowCount);
        }

        if ((this.presentRow < 1) || (this.presentRow > this.rowCount)) {
            throw new SQLException("Operation with out calling next/previous");
        }

        return (OracleRow) this.rows.elementAt(this.presentRow - 1);
    }

    boolean isConnectionStayingOpen() {
        return this.isConnectionStayingOpenForTxnControl;
    }

    void setOriginal() throws SQLException {
        int rowPosition = 1;
        do {
            boolean _isRowDeleted = setOriginalRowInternal(rowPosition);

            if (_isRowDeleted)
                continue;
            rowPosition++;
        }

        while (rowPosition <= this.rowCount);

        notifyRowSetChanged();

        this.presentRow = 0;
    }

    boolean setOriginalRowInternal(int rowPosition) throws SQLException {
        if ((rowPosition < 1) || (rowPosition > this.rowCount)) {
            throw new SQLException("Invalid cursor position, try next/previous first");
        }
        boolean _rowDeleted = false;

        OracleRow row = (OracleRow) this.rows.elementAt(rowPosition - 1);

        if (row.isRowDeleted()) {
            this.rows.remove(rowPosition - 1);
            this.rowCount -= 1;
            _rowDeleted = true;
        } else {
            if (row.isRowInserted()) {
                row.setInsertedFlag(false);
            }

            if (row.isRowUpdated()) {
                row.makeUpdatesOriginal();
            }

        }

        return _rowDeleted;
    }

    public boolean next() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.next()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.next(), rowCount="
                    + this.rowCount + ", fetchDirection=" + this.fetchDirection + ", presentRow="
                    + this.presentRow);
        }

        if (this.rowCount < 0) {
            return false;
        }

        if ((this.fetchDirection == 1000) || (this.fetchDirection == 1002)) {
            if (this.presentRow + 1 <= this.rowCount) {
                this.presentRow += 1;
                if ((!this.showDeleted) && (getCurrentRow().isRowDeleted())) {
                    return next();
                }
                notifyCursorMoved();
                return true;
            }

            this.presentRow = (this.rowCount + 1);
            return false;
        }

        if (this.fetchDirection == 1001) {
            if (this.presentRow - 1 > 0) {
                this.presentRow -= 1;
                if ((!this.showDeleted) && (getCurrentRow().isRowDeleted()))
                    return next();
                notifyCursorMoved();
                return true;
            }

            this.presentRow = 0;
            return false;
        }

        return false;
    }

    public boolean previous() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.previous()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.previous(), rowCount="
                    + this.rowCount + ", fetchDirection=" + this.fetchDirection + ", presentRow="
                    + this.presentRow);
        }

        if (this.rowCount < 0) {
            return false;
        }

        if (this.fetchDirection == 1001) {
            if (this.presentRow + 1 <= this.rowCount) {
                this.presentRow += 1;
                if ((!this.showDeleted) && (getCurrentRow().isRowDeleted()))
                    return previous();
                notifyCursorMoved();
                return true;
            }

            this.presentRow = (this.rowCount + 1);
            return false;
        }

        if ((this.fetchDirection == 1000) || (this.fetchDirection == 1002)) {
            if (this.presentRow - 1 > 0) {
                this.presentRow -= 1;
                if ((!this.showDeleted) && (getCurrentRow().isRowDeleted()))
                    return previous();
                notifyCursorMoved();
                return true;
            }

            this.presentRow = 0;
            return false;
        }

        return false;
    }

    public boolean isBeforeFirst() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.isBeforeFirst()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.isBeforeFirst(), rowCount="
                    + this.rowCount + ", presentRow=" + this.presentRow);
        }

        return (this.rowCount > 0) && (this.presentRow == 0);
    }

    public boolean isAfterLast() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.isAfterLast()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.isAfterLast(), rowCount="
                    + this.rowCount + ", presentRow=" + this.presentRow);
        }

        return (this.rowCount > 0) && (this.presentRow == this.rowCount + 1);
    }

    public boolean isFirst() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.isFirst(), presentRow="
                    + this.presentRow);
        }

        return this.presentRow == 1;
    }

    public boolean isLast() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.isLast()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.isLast(), presentRow="
                    + this.presentRow + ", rowCount=" + this.rowCount);
        }

        return this.presentRow == this.rowCount;
    }

    public void beforeFirst() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.beforeFirst()");

            OracleLog.print(this, 1, 256, 32,
                            "OracleCachedRowSet.beforeFirst(), set presentRow to 0");
        }

        this.presentRow = 0;
    }

    public void afterLast() throws SQLException {
        this.presentRow = (this.rowCount + 1);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.afterLast()");

            OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.afterLast(), set presentRow to "
                    + this.presentRow);
        }
    }

    public boolean first() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.first()");
        }

        return absolute(1);
    }

    public boolean last() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.last()");
        }

        return absolute(-1);
    }

    public boolean absolute(int row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.absolute(" + row + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.absolute(" + row
                    + "), rowsetType=" + this.rowsetType + ", presentRow=" + this.presentRow);
        }

        if (this.rowsetType == 1003)
            throw new SQLException("The RowSet type is TYPE_FORWARD_ONLY");
        if ((row == 0) || (Math.abs(row) > this.rowCount))
            return false;
        this.presentRow = (row < 0 ? this.rowCount + row + 1 : row);
        notifyCursorMoved();
        return true;
    }

    public boolean relative(int rows) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.relative(" + rows + ")");
        }

        return absolute(this.presentRow + rows);
    }

    public synchronized void populate(ResultSet rs) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.populate(" + rs + ")");
        }

        if (this.rows == null) {
            this.rows = new Vector(50, 10);
        } else {
            this.rows.clear();
        }
        this.rowsetMetaData = new OracleRowSetMetaData(rs.getMetaData());
        this.metaData = new String[this.colCount = this.rowsetMetaData.getColumnCount()];
        for (int i = 0; i < this.colCount; i++) {
            this.metaData[i] = this.rowsetMetaData.getColumnName(i + 1);
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.populate(ResultSet), metaData="
                    + this.metaData + ", process with populateInit(ResultSet)");
        }

        if (!(rs instanceof OracleCachedRowSet)) {
            populateInit(rs);
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 32,
                            "OracleCachedRowSet.populate(ResultSet), finished of populateInit(rs)");
        }

        boolean isForwardFetch = (this.fetchDirection == 1000) || (this.fetchDirection == 1002);

        this.rowCount = 0;
        OracleRow or = null;
        int rowCountLimit;
        if ((this.maxRows == 0) && (this.pageSize == 0)) {
            rowCountLimit = 2147483647;
        } else {
            if ((this.maxRows == 0) || (this.pageSize == 0)) {
                rowCountLimit = Math.max(this.maxRows, this.pageSize);
            } else {
                rowCountLimit = Math.min(this.maxRows, this.pageSize);
            }

        }

        if ((rs.getType() != 1003) && (this.rows.size() == 0)) {
            if (!isForwardFetch) {
                rs.afterLast();
            }
        }

        while (this.rowCount < rowCountLimit) {
            if (isForwardFetch ? !rs.next() : !rs.previous()) {
                break;
            }
            or = new OracleRow(this.colCount);
            for (int i = 1; i <= this.colCount; i++) {
                Object obj = null;
                try {
                    obj = rs.getObject(i, this.typeMap);
                } catch (Exception ex) {
                    obj = rs.getObject(i);
                } catch (AbstractMethodError ex) {
                    obj = rs.getObject(i);
                }

                if (((obj instanceof Clob)) || ((obj instanceof CLOB))) {
                    or.setColumnValue(i, new OracleSerialClob((Clob) obj));
                } else if (((obj instanceof Blob)) || ((obj instanceof BLOB))) {
                    or.setColumnValue(i, new OracleSerialBlob((Blob) obj));
                } else {
                    or.setColumnValue(i, obj);
                }
                or.markOriginalNull(i, rs.wasNull());
            }

            if (isForwardFetch) {
                this.rows.add(or);
            } else {
                this.rows.add(1, or);
            }

            this.rowCount += 1;
        }

        if (((isForwardFetch) && (rs.isAfterLast())) || ((!isForwardFetch) && (rs.isBeforeFirst()))) {
            this.isPopulateDone = true;
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.populate(ResultSet) "
                    + this.rowCount + " rows populated");
        }

        this.connection = null;

        notifyRowSetChanged();
    }

    public String getCursorName() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getCursorName()");
        }

        throw new SQLException("Getting the cursor name is not supported.");
    }

    public synchronized void clearParameters() throws SQLException {
        this.param = null;
        this.param = new Vector();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "racleCachedRowSet.clearParameters()");
        }
    }

    public boolean wasNull() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.wasNull()");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.wasNull(), return "
                    + this.previousColumnWasNull);
        }

        return this.previousColumnWasNull;
    }

    public void close() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.close()");
        }

        release();
    }

    public SQLWarning getWarnings() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getWarnings()");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getWarnings(), return "
                    + this.sqlWarning);
        }

        return this.sqlWarning;
    }

    public void clearWarnings() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.clearWarnings()");
        }

        this.sqlWarning = null;
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getMetaData()");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getMetaData(), return "
                    + this.rowsetMetaData);
        }

        return this.rowsetMetaData;
    }

    public int findColumn(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.findColumn(" + columnName + ")");
        }

        return getColumnIndex(columnName);
    }

    public Object[] getParams() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.addRowSetListener()");
        }

        return this.param.toArray();
    }

    public void setMetaData(RowSetMetaData md) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setMetaData(" + md + ")");
        }

        this.rowsetMetaData = md;

        if (md != null) {
            this.colCount = md.getColumnCount();
        }
    }

    public synchronized void execute() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.execute()");
        }

        this.isConnectionStayingOpenForTxnControl = false;
        getReader().readData(this);
    }

    public void acceptChanges() throws SyncProviderException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.acceptChanges()");
        }

        try {
            getWriter().writeData(this);
        } catch (SQLException sqlexc) {
            throw new SyncProviderException(sqlexc.getMessage());
        }
    }

    public void acceptChanges(Connection con) throws SyncProviderException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.acceptChanges(" + con + ")");
        }

        this.connection = con;

        this.isConnectionStayingOpenForTxnControl = true;
        acceptChanges();
    }

    public Object clone() throws CloneNotSupportedException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.clone()");
        }

        try {
            return createCopy();
        } catch (SQLException ea) {
            throw new CloneNotSupportedException("SQL Error occured while cloning,\n"
                    + ea.getMessage());
        }

    }

    public CachedRowSet createCopy() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.createCopy()");
        }

        OracleCachedRowSet ocrs = (OracleCachedRowSet) createShared();
        int size = this.rows.size();
        ocrs.rows = new Vector(size);
        for (int i = 0; i < size; i++) {
            ocrs.rows.add(((OracleRow) this.rows.elementAt(i)).createCopy());
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.createCopy(), return " + ocrs);
        }

        return ocrs;
    }

    public RowSet createShared() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.createShared()");
        }

        OracleCachedRowSet ocrs = new OracleCachedRowSet();

        ocrs.rows = this.rows;

        ocrs.setDataSource(getDataSource());
        ocrs.setDataSourceName(getDataSourceName());
        ocrs.setUsername(getUsername());
        ocrs.setPassword(getPassword());
        ocrs.setUrl(getUrl());
        ocrs.setTypeMap(getTypeMap());
        ocrs.setMaxFieldSize(getMaxFieldSize());
        ocrs.setMaxRows(getMaxRows());
        ocrs.setQueryTimeout(getQueryTimeout());
        ocrs.setFetchSize(getFetchSize());
        ocrs.setEscapeProcessing(getEscapeProcessing());
        ocrs.setConcurrency(getConcurrency());
        ocrs.setReadOnly(this.readOnly);

        this.rowsetType = getType();
        this.fetchDirection = getFetchDirection();
        ocrs.setCommand(getCommand());
        ocrs.setTransactionIsolation(getTransactionIsolation());

        ocrs.presentRow = this.presentRow;
        ocrs.colCount = this.colCount;
        ocrs.rowCount = this.rowCount;
        ocrs.showDeleted = this.showDeleted;

        ocrs.syncProvider = this.syncProvider;

        ocrs.currentPage = this.currentPage;
        ocrs.pageSize = this.pageSize;
        ocrs.tableName = (this.tableName == null ? null : new String(this.tableName));
        ocrs.keyColumns = (this.keyColumns == null ? null : (int[]) this.keyColumns.clone());

        int size = this.listener.size();
        for (int i = 0; i < size; i++) {
            ocrs.listener.add(this.listener.elementAt(i));
        }

        ocrs.rowsetMetaData = new OracleRowSetMetaData(this.rowsetMetaData);

        size = this.param.size();
        for (int i = 0; i < size; i++) {
            ocrs.param.add(this.param.elementAt(i));
        }
        ocrs.metaData = new String[this.metaData.length];
        System.arraycopy(this.metaData, 0, ocrs.metaData, 0, this.metaData.length);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.createShared(), rowsetType="
                    + this.rowsetType + ", size=" + size + ", fetchDirection="
                    + this.fetchDirection + ", return " + ocrs);
        }

        return ocrs;
    }

    public void release() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.release()");
        }

        this.rows = null;
        this.rows = new Vector();
        if ((this.connection != null) && (!this.connection.isClosed()))
            this.connection.close();
        this.rowCount = 0;
        this.presentRow = 0;
        notifyRowSetChanged();
    }

    public void restoreOriginal() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.restoreOriginal()");
        }

        boolean rowsetChanged = false;
        for (int i = 0; i < this.rowCount; i++) {
            OracleRow row = (OracleRow) this.rows.elementAt(i);
            if (row.isRowInserted()) {
                this.rows.remove(i);
                this.rowCount -= 1;

                i--;
                rowsetChanged = true;
            } else if (row.isRowUpdated()) {
                row.setRowUpdated(false);
                rowsetChanged = true;
            } else {
                if (!row.isRowDeleted())
                    continue;
                row.setRowDeleted(false);
                rowsetChanged = true;
            }

        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSet.restoreOriginal(), rowsetChanged=" + rowsetChanged
                                    + ", presentRow=" + this.presentRow);
        }

        if (!rowsetChanged) {
            throw new SQLException("None of the rows are changed");
        }

        notifyRowSetChanged();

        this.presentRow = 0;
    }

    public Collection toCollection() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.toCollection()");
        }

        Map collection = Collections.synchronizedMap(new TreeMap());
        try {
            for (int i = 0; i < this.rowCount; i++) {
                collection.put(new Integer(i), ((OracleRow) this.rows.elementAt(i)).toCollection());
            }

        } catch (Exception exc) {
            collection = null;
            throw new SQLException("Map operation failed in toCollection()");
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.toCollection(), return "
                    + collection.values());
        }

        return collection.values();
    }

    public Collection toCollection(int column) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.toCollection(" + column + ")");
        }

        if ((column < 1) || (column > this.colCount)) {
            throw new SQLException("invalid index : " + column);
        }
        Vector collection = new Vector(this.rowCount);
        for (int i = 0; i < this.rowCount; i++) {
            OracleRow row = (OracleRow) this.rows.elementAt(i);
            Object obj = row.isColumnChanged(column) ? row.getModifiedColumn(column) : row
                    .getColumn(column);

            collection.add(obj);
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.toCollection(" + column
                    + "), return " + collection);
        }

        return collection;
    }

    public Collection toCollection(String columnName) throws SQLException {
        return toCollection(getColumnIndex(columnName));
    }

    public int getRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getRow()");

            OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.getRow(), presentRow="
                    + this.presentRow + ", rowCount=" + this.rowCount);
        }

        if (this.presentRow > this.rowCount) {
            return this.rowCount;
        }

        return this.presentRow;
    }

    public void cancelRowInsert() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.cancelRowInsert()");
        }

        if (getCurrentRow().isRowInserted()) {
            this.rows.remove(--this.presentRow);
            this.rowCount -= 1;
            notifyRowChanged();
        } else {
            throw new SQLException("The row is not inserted");
        }
    }

    public void cancelRowDelete() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.cancelRowDelete()");
        }

        if (getCurrentRow().isRowDeleted()) {
            getCurrentRow().setRowDeleted(false);
            notifyRowChanged();
        } else {
            throw new SQLException("The row is not deleted");
        }
    }

    public void cancelRowUpdates() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.cancelRowUpdates()");
        }

        if (getCurrentRow().isRowUpdated()) {
            this.updateRowFlag = false;
            getCurrentRow().setRowUpdated(false);
            notifyRowChanged();
        } else {
            throw new SQLException("The row is not updated.");
        }
    }

    public void insertRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.cancelRowUpdates()");

            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSet.cancelRowUpdates(), insertRowFlag="
                                    + this.insertRowFlag);
        }

        if (!this.insertRowFlag) {
            throw new SQLException("Current row not inserted/updated.");
        }
        if (!this.insertRow.isRowFullyPopulated()) {
            throw new SQLException("All the columns of the row are not set");
        }

        this.insertRow.insertRow();
        this.rows.insertElementAt(this.insertRow, this.insertRowPosition - 1);
        this.insertRowFlag = false;
        this.rowCount += 1;
        notifyRowChanged();
    }

    public void updateRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateRow()");

            OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.updateRow(), updateRowFlag="
                    + this.updateRowFlag);
        }

        if (this.updateRowFlag) {
            this.updateRowFlag = false;
            getCurrentRow().setRowUpdated(true);
            notifyRowChanged();
        } else {
            throw new SQLException("Current row not updated");
        }
    }

    public void deleteRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.deleteRow()");
        }

        getCurrentRow().setRowDeleted(true);
        notifyRowChanged();
    }

    public void refreshRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.refreshRow()");
        }

        OracleRow row = getCurrentRow();
        if (row.isRowUpdated()) {
            row.cancelRowUpdates();
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.refreshRow(), row=" + row);
        }
    }

    public void moveToInsertRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.moveToInsertRow()");
        }

        this.insertRow = new OracleRow(this.colCount, true);
        this.insertRowFlag = true;

        if (isAfterLast())
            this.insertRowPosition = this.presentRow;
        else {
            this.insertRowPosition = (this.presentRow + 1);
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSet.moveToInsertRow(), insertRowPosition="
                                    + this.insertRowPosition + ", presentRow=" + this.presentRow);
        }
    }

    public void moveToCurrentRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.moveToCurrentRow()");
        }

        this.insertRowFlag = false;
        this.updateRowFlag = false;
        absolute(this.presentRow);
    }

    public boolean rowUpdated() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.rowUpdated()");
        }

        return getCurrentRow().isRowUpdated();
    }

    public boolean rowInserted() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.rowInserted()");
        }

        return getCurrentRow().isRowInserted();
    }

    public boolean rowDeleted() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.rowDeleted()");
        }

        return getCurrentRow().isRowDeleted();
    }

    public ResultSet getOriginalRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getOriginalRow()");
        }

        OracleCachedRowSet cachedrowset = new OracleCachedRowSet();
        cachedrowset.rowsetMetaData = this.rowsetMetaData;
        cachedrowset.rowCount = 1;
        cachedrowset.colCount = this.colCount;
        cachedrowset.presentRow = 0;
        cachedrowset.setReader(null);
        cachedrowset.setWriter(null);
        OracleRow row = new OracleRow(this.rowsetMetaData.getColumnCount(), getCurrentRow()
                .getOriginalRow());

        cachedrowset.rows.add(row);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getOriginalRow(), return "
                    + cachedrowset);
        }

        return cachedrowset;
    }

    public ResultSet getOriginal() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getOriginal()");
        }

        OracleCachedRowSet cachedrowset = new OracleCachedRowSet();
        cachedrowset.rowsetMetaData = this.rowsetMetaData;
        cachedrowset.rowCount = this.rowCount;
        cachedrowset.colCount = this.colCount;

        cachedrowset.presentRow = 0;

        cachedrowset.setType(1004);
        cachedrowset.setConcurrency(1008);

        cachedrowset.setReader(null);
        cachedrowset.setWriter(null);
        int i = this.rowsetMetaData.getColumnCount();
        OracleRow row = null;

        Iterator iterator = this.rows.iterator();
        while (iterator.hasNext()) {
            row = new OracleRow(i, ((OracleRow) iterator.next()).getOriginalRow());

            cachedrowset.rows.add(row);
        }

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getOriginal(), return "
                    + cachedrowset);
        }

        return cachedrowset;
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setNull(int, int)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setNull(" + parameterIndex + ", "
                    + sqlType + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, null);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setNull(int, int, String)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setNull(" + parameterIndex + ", "
                    + sqlType + ", " + typeName + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { new Integer(sqlType), typeName };
        this.param.add(parameterIndex - 1, obj);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setBoolean(int, boolean)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setBoolean(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Boolean(x));
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setByte(int, byte)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setByte(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Byte(x));
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setShort(int, short)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setShort(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Short(x));
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setInt(int, int)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setInt(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Integer(x));
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setLong(int, long)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setLong(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Long(x));
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setFloat(int, float)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setFloat(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Float(x));
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setDouble(int, double)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setDouble(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, new Double(x));
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setBigDecimal(int, BigDecimal)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setBigDecimal(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setString(int, String)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setString(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setBytes(int, byte[])");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setBytes(" + parameterIndex
                    + ", " + OracleLog.bytesToPrintableForm("byte[] ", x) + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setDate(int, java.sql.Date)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setDate(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setTime(int, java.sql.Time)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setTime(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setObject(int, Object)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setObject(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setRef(int, Ref)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setRef(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setBlob(int, Blob)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setBlob(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setClob(int, Clob)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setClob(" + parameterIndex + ", "
                    + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setArray(int parameterIndex, Array x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setArray(int, Array)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setArray(" + parameterIndex
                    + ", " + x + ")");
        }

        checkParamIndex(parameterIndex);
        this.param.add(parameterIndex - 1, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setBinaryStream(" + parameterIndex
                    + ", " + x + ", " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setBinaryStream("
                    + parameterIndex + ", " + x + ", " + length + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { x, new Integer(length), new Integer(546) };

        this.param.add(parameterIndex - 1, obj);
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setTime(" + i + ", " + time + ", "
                    + calendar + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setTime(" + i + ", " + time
                    + ", " + calendar + ")");
        }

        checkParamIndex(i);
        Object[] obj = { time, calendar };
        this.param.add(i - 1, obj);
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setTimestamp(" + i + ", "
                    + timestamp + ", " + calendar + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setTimestamp(" + i + ", "
                    + timestamp + ", " + calendar + ")");
        }

        checkParamIndex(i);
        Object[] obj = { timestamp, calendar };
        this.param.add(i - 1, obj);
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setTimestamp(" + i + ", "
                    + timestamp + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setTimestamp(" + i + ", "
                    + timestamp + ")");
        }

        checkParamIndex(i);
        this.param.add(i - 1, timestamp);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setAsciiStream(" + parameterIndex
                    + ", " + x + ", " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setAsciiStream(" + parameterIndex
                    + ", " + x + ", " + length + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { x, new Integer(length), new Integer(819) };

        this.param.add(parameterIndex - 1, obj);
    }

    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setUnicodeStream("
                    + parameterIndex + ", " + x + ", " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setUnicodeStream("
                    + parameterIndex + ", " + x + ", " + length + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { x, new Integer(length), new Integer(273) };

        this.param.add(parameterIndex - 1, obj);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setCharacterStream("
                    + parameterIndex + ", " + reader + ", " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setCharacterStream("
                    + parameterIndex + ", " + reader + ", " + length + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { reader, new Integer(length) };
        this.param.add(parameterIndex - 1, obj);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setObject(" + parameterIndex
                    + ",  " + x + ", " + targetSqlType + ", " + scale + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setObject(" + parameterIndex
                    + ",  " + x + ", " + targetSqlType + ", " + scale + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { x, new Integer(targetSqlType), new Integer(scale) };
        this.param.add(parameterIndex - 1, obj);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setObject(" + parameterIndex
                    + ", " + x + ", " + targetSqlType + ")");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setObject(" + parameterIndex
                    + ", " + x + ", " + targetSqlType + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { x, new Integer(targetSqlType) };
        this.param.add(parameterIndex - 1, obj);
    }

    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.setDate(" + parameterIndex
                    + ", Date, Calendar)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.setDate(" + parameterIndex + ", "
                    + x + ", " + cal + ")");
        }

        checkParamIndex(parameterIndex);
        Object[] obj = { x, cal };
        this.param.add(parameterIndex - 1, obj);
    }

    public synchronized Object getObject(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getObject(" + columnIndex + ")");
        }

        int index = this.presentRow * this.colCount + columnIndex - 1;
        Object obj = null;

        if (!isUpdated(columnIndex))
            obj = getCurrentRow().getColumn(columnIndex);
        else
            obj = getCurrentRow().getModifiedColumn(columnIndex);
        this.previousColumnWasNull = (obj == null);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getObject(" + columnIndex
                    + "), return " + obj);
        }

        return obj;
    }

    private synchronized Number getNumber(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getNumber(" + columnIndex + ")");
        }

        Object n = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getNumber(" + columnIndex
                    + "), getObject(" + columnIndex + ")=" + n);
        }

        if ((n == null) || ((n instanceof BigDecimal)) || ((n instanceof Number))) {
            return (Number) n;
        }
        if ((n instanceof Boolean))
            return new Integer(((Boolean) n).booleanValue() ? 1 : 0);
        if ((n instanceof String)) {
            try {
                return new BigDecimal((String) n);
            } catch (NumberFormatException ea) {
                throw new SQLException("Fail to convert to internal representation");
            }

        }

        throw new SQLException("Fail to convert to internal representation");
    }

    public Object getObject(int i, Map map) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getObject(" + i
                    + ", java.util.Map)");

            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getObject(" + i + ", " + map
                    + ")");
        }

        return getObject(i);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBoolean(" + columnIndex + ")");
        }

        Object b = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getBoolean(" + columnIndex
                    + "),  this.getObject(" + columnIndex + ")=" + b);
        }

        if (b == null) {
            return false;
        }
        if ((b instanceof Boolean)) {
            return ((Boolean) b).booleanValue();
        }
        if ((b instanceof Number)) {
            return ((Number) b).doubleValue() != 0.0D;
        }

        throw new SQLException("Fail to convert to internal representation");
    }

    public byte getByte(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getByte(" + columnIndex + ")");
        }

        Object b = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getByte(" + columnIndex
                    + "), this.getObject(" + columnIndex + ")=" + b);
        }

        if (b == null) {
            return 0;
        }
        if ((b instanceof Number)) {
            return ((Number) b).byteValue();
        }
        if ((b instanceof String)) {
            return ((String) b).getBytes()[0];
        }
        if ((b instanceof OracleSerialBlob)) {
            OracleSerialBlob c = (OracleSerialBlob) b;
            return c.getBytes(0L, 1)[0];
        }
        if ((b instanceof OracleSerialClob)) {
            OracleSerialClob c = (OracleSerialClob) b;
            return c.getSubString(0L, 1).getBytes()[0];
        }

        throw new SQLException("Fail to convert to internal representation");
    }

    public short getShort(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getShort(" + columnIndex + ")");
        }

        Number b = getNumber(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getShort(" + columnIndex
                    + "), this.getNumber(" + columnIndex + ")=" + b);
        }

        return b == null ? 0 : b.shortValue();
    }

    public int getInt(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getInt(" + columnIndex + ")");
        }

        Number b = getNumber(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getInt(" + columnIndex
                    + "), this.getNumber(" + columnIndex + ")=" + b);
        }

        return b == null ? 0 : b.intValue();
    }

    public long getLong(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getLong(" + columnIndex + ")");
        }

        Number b = getNumber(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getLong(" + columnIndex
                    + "), this.getNumber(" + columnIndex + ")=" + b);
        }

        return b == null ? 0L : b.longValue();
    }

    public float getFloat(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getFloat(" + columnIndex + ")");
        }

        Number b = getNumber(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getFloat(" + columnIndex
                    + "), this.getNumber(" + columnIndex + ")=" + b);
        }

        return b == null ? 0.0F : b.floatValue();
    }

    public double getDouble(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getDouble(" + columnIndex + ")");
        }

        Number b = getNumber(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getDouble(" + columnIndex
                    + "), this.getNumber(" + columnIndex + ")=" + b);
        }

        return b == null ? 0.0D : b.doubleValue();
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBigDecimal(" + columnIndex
                    + ")");
        }

        Object b = getNumber(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getBigDecimal(" + columnIndex
                    + "), this.getNumber(" + columnIndex + ")=" + b);
        }

        if ((b == null) || ((b instanceof BigDecimal)))
            return (BigDecimal) b;
        if ((b instanceof Number)) {
            return new BigDecimal(((Number) b).doubleValue());
        }

        return null;
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBigDecimal(" + columnIndex
                    + ", " + scale + ")");
        }

        return getBigDecimal(columnIndex);
    }

    public java.sql.Date getDate(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getDate(" + columnIndex + ")");
        }

        Object d = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getDate(" + columnIndex
                    + "), this.getObject(" + columnIndex + ")= " + d);
        }

        if (d == null) {
            return (java.sql.Date) d;
        }
        if ((d instanceof Time)) {
            Time t = (Time) d;
            return new java.sql.Date(t.getTime());
        }

        if ((d instanceof java.util.Date)) {
            java.util.Date t = (java.util.Date) d;
            return new java.sql.Date(t.getYear(), t.getMonth(), t.getDate());
        }

        throw new SQLException("Invalid column type");
    }

    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getDate(" + columnIndex + ", "
                    + cal + ")");
        }

        return getDate(columnIndex);
    }

    public Time getTime(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTime(" + columnIndex + ")");
        }

        Object d = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getTime(" + columnIndex
                    + "), this.getObject(" + columnIndex + ")=" + d);
        }

        if (d == null)
            return (Time) d;
        if ((d instanceof java.util.Date)) {
            java.util.Date t = (java.util.Date) d;
            return new Time(t.getHours(), t.getMinutes(), t.getSeconds());
        }

        throw new SQLException("Invalid column type");
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTime(" + columnIndex + ", "
                    + cal + ")");
        }

        return getTime(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleCachedRowSet.getTimestamp(" + columnIndex + ")");
        }

        Object d = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getTimestamp(" + columnIndex
                    + "), this.getObject(" + columnIndex + ")=" + d);
        }

        if ((d == null) || ((d instanceof Timestamp)))
            return (Timestamp) d;
        if ((d instanceof java.util.Date)) {
            return new Timestamp(((java.util.Date) d).getTime());
        }
        throw new SQLException("Invalid column type");
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTimestamp(" + columnIndex
                    + ", " + cal + ")");
        }

        return getTimestamp(columnIndex);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBytes(" + columnIndex + ")");
        }

        Object b = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getBytes(" + columnIndex
                    + "), this.getObject(" + columnIndex + ")=" + b);
        }

        if (b == null) {
            return (byte[]) b;
        }
        if ((b instanceof byte[])) {
            return (byte[]) b;
        }
        if ((b instanceof String)) {
            return (byte[]) ((String) b).getBytes();
        }
        if ((b instanceof Number)) {
            return (byte[]) ((Number) b).toString().getBytes();
        }
        if ((b instanceof BigDecimal)) {
            return (byte[]) ((BigDecimal) b).toString().getBytes();
        }
        if ((b instanceof OracleSerialBlob)) {
            OracleSerialBlob c = (OracleSerialBlob) b;
            return c.getBytes(0L, (int) c.length());
        }
        if ((b instanceof OracleSerialClob)) {
            OracleSerialClob c = (OracleSerialClob) b;
            return c.getSubString(0L, (int) c.length()).getBytes();
        }

        throw new SQLException("Fail to convert to internal representation");
    }

    public Ref getRef(int i) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getRef(" + i + ")");
        }

        Object b = getObject(i);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getRef(" + i
                    + "), this.getObject(" + i + ")=" + b);
        }

        if ((b == null) || ((b instanceof Ref))) {
            return (Ref) b;
        }
        throw new SQLException("Invalid column type");
    }

    public Blob getBlob(int i) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBlob(" + i + ")");
        }

        Object b = getObject(i);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getBlob(" + i
                    + "), this.getObject(" + i + ")=" + b);
        }

        if ((b instanceof OracleSerialBlob)) {
            return b == null ? null : (Blob) b;
        }
        throw new SQLException("Invalid column type");
    }

    public Clob getClob(int i) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getClob(" + i + ")");
        }

        Object b = getObject(i);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getClob(" + i
                    + "), this.getObject(" + i + ")=" + b);
        }

        if ((b instanceof OracleSerialClob)) {
            return b == null ? null : (Clob) b;
        }
        throw new SQLException("Invalid column type");
    }

    public Array getArray(int i) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getArray(" + i + ")");
        }

        Object b = getObject(i);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getArray(" + i
                    + "), this.getObject(" + i + ")=" + b);
        }

        if ((b == null) || ((b instanceof Array))) {
            return (Array) b;
        }
        throw new SQLException("Invalid column type");
    }

    public String getString(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getString(" + columnIndex + ")");
        }

        Object b = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getString(" + columnIndex
                    + "), this.getObject(" + columnIndex + ")=" + b);
        }

        if (b == null) {
            return (String) b;
        }
        if ((b instanceof String)) {
            return (String) b;
        }
        if (((b instanceof Number)) || ((b instanceof BigDecimal))) {
            return b.toString();
        }
        if ((b instanceof java.sql.Date)) {
            return ((java.sql.Date) b).toString();
        }
        if ((b instanceof Timestamp)) {
            return ((java.sql.Date) b).toString();
        }
        if ((b instanceof byte[])) {
            return new String((byte[]) b);
        }
        if ((b instanceof OracleSerialClob)) {
            OracleSerialClob c = (OracleSerialClob) b;
            return c.getSubString(0L, (int) c.length());
        }

        if ((b instanceof OracleSerialBlob)) {
            OracleSerialBlob c = (OracleSerialBlob) b;
            return new String(c.getBytes(0L, (int) c.length()));
        }

        if ((b instanceof URL)) {
            return ((URL) b).toString();
        }
        if ((b instanceof Reader)) {
            try {
                Reader reader = (Reader) b;
                char[] c = new char[1024];
                int bytesRead = 0;
                StringBuffer sb = new StringBuffer(1024);
                while ((bytesRead = reader.read(c)) > 0)
                    sb.append(c, 0, bytesRead);
                return sb.substring(0, sb.length());
            } catch (IOException ea) {
                throw new SQLException("Error during conversion: " + ea.getMessage());
            }
        }

        throw new SQLException("Fail to convert to internal representation");
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getAsciiStream(" + columnIndex
                    + ")");
        }

        InputStream b = getStream(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getAsciiStream(" + columnIndex
                    + "), getStream(" + columnIndex + ")=" + b);
        }

        return b == null ? null : b;
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getUnicodeStream(" + columnIndex
                    + ")");
        }

        Object b = getObject(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getUnicodeStream(" + columnIndex
                    + "), getObject(" + columnIndex + ")=" + b);
        }

        if (b == null) {
            return (InputStream) b;
        }

        if ((b instanceof String)) {
            return new StringBufferInputStream((String) b);
        }
        throw new SQLException("Fail to convert to internal representation");
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBinaryStream(" + columnIndex
                    + ")");
        }

        InputStream b = getStream(columnIndex);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getBinaryStream(" + columnIndex
                    + "), getStream(" + columnIndex + ")=" + b);
        }

        return b == null ? null : b;
    }

    public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getCharacterStream(" + columnIndex
                    + ")");
        }

        try {
            InputStream x = getAsciiStream(columnIndex);

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getCharacterStream("
                        + columnIndex + "), this.getAsciiStream(" + columnIndex + ")=" + x);
            }

            if (x == null)
                return null;
            StringBuffer sb = new StringBuffer();
            int i = 0;
            while ((i = x.read()) != -1) {
                sb.append((char) i);
            }

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getCharacterStream("
                        + columnIndex + "), StringBuffer sb=" + sb);
            }

            char[] c = new char[sb.length()];
            sb.getChars(0, sb.length(), c, 0);
            Reader r = new CharArrayReader(c);
            c = null;

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 16, "OracleCachedRowSet.getCharacterStream("
                        + columnIndex + "), return Reader " + r);
            }

            return r;
        } catch (IOException ea) {
        }

        throw new SQLException("Error: could not read from the stream");
    }

    public synchronized Object getObject(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getObject(" + columnName + ")");
        }

        return getObject(getColumnIndex(columnName));
    }

    public boolean getBoolean(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBoolean(" + columnName + ")");
        }

        return getBoolean(getColumnIndex(columnName));
    }

    public byte getByte(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getByte(" + columnName + ")");
        }

        return getByte(getColumnIndex(columnName));
    }

    public short getShort(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getShort(" + columnName + ")");
        }

        return getShort(getColumnIndex(columnName));
    }

    public int getInt(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getInt(" + columnName + ")");
        }

        return getInt(getColumnIndex(columnName));
    }

    public long getLong(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getLong(" + columnName + ")");
        }

        return getLong(getColumnIndex(columnName));
    }

    public float getFloat(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getFloat(" + columnName + ")");
        }

        return getFloat(getColumnIndex(columnName));
    }

    public double getDouble(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getDouble(" + columnName + ")");
        }

        return getDouble(getColumnIndex(columnName));
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBigDecimal(" + columnName
                    + ", " + scale + ")");
        }

        return getBigDecimal(getColumnIndex(columnName), scale);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBytes(" + columnName + ")");
        }

        return getBytes(getColumnIndex(columnName));
    }

    public java.sql.Date getDate(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getDate(" + columnName + ")");
        }

        return getDate(getColumnIndex(columnName));
    }

    public Time getTime(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTime(" + columnName + ")");
        }

        return getTime(getColumnIndex(columnName));
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTimestamp(" + columnName + ")");
        }

        return getTimestamp(getColumnIndex(columnName));
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTime(" + columnName
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getTime(" + columnName + ", "
                    + cal + ")");
        }

        return getTime(getColumnIndex(columnName), cal);
    }

    public java.sql.Date getDate(String columnName, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getDate(" + columnName
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getDate(" + columnName + ", "
                    + cal + ")");
        }

        return getDate(getColumnIndex(columnName), cal);
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getAsciiStream(" + columnName
                    + ")");
        }

        return getAsciiStream(getColumnIndex(columnName));
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getUnicodeStream(" + columnName
                    + ")");
        }

        return getUnicodeStream(getColumnIndex(columnName));
    }

    public String getString(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getString(" + columnName + ")");
        }

        return getString(getColumnIndex(columnName));
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBinaryStream(" + columnName
                    + ")");
        }

        return getBinaryStream(getColumnIndex(columnName));
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getCharacterStream(" + columnName
                    + ")");
        }

        return getCharacterStream(getColumnIndex(columnName));
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleCachedRowSet.getBigDecimal(" + columnName + ")");
        }

        return getBigDecimal(getColumnIndex(columnName));
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getTimestamp(" + columnName
                    + ",  Calendar cal)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getTimestamp(" + columnName
                    + ", " + cal + ")");
        }

        return getTimestamp(getColumnIndex(columnName), cal);
    }

    public Object getObject(String colName, Map map) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getObject(" + colName
                    + ", Map map)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.getObject(" + colName + ", "
                    + map + ")");
        }

        return getObject(getColumnIndex(colName), map);
    }

    public Ref getRef(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getRef(" + colName + ")");
        }

        return getRef(getColumnIndex(colName));
    }

    public Blob getBlob(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getBlob(" + colName + ")");
        }

        return getBlob(getColumnIndex(colName));
    }

    public Clob getClob(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getClob(" + colName + ")");
        }

        return getClob(getColumnIndex(colName));
    }

    public Array getArray(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.getArray(" + colName + ")");
        }

        return getArray(getColumnIndex(colName));
    }

    public synchronized void updateObject(int columnIndex, Object x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateObject(" + columnIndex
                    + ", Object)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateObject(" + columnIndex
                    + ", " + x + "), and insertRowFlag=" + this.insertRowFlag);
        }

        checkColumnIndex(columnIndex);
        if (this.insertRowFlag) {
            checkAndFilterObject(columnIndex, x);
            this.insertRow.updateObject(columnIndex, x);
        } else if ((!isBeforeFirst()) && (!isAfterLast())) {
            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 32, "OracleCachedRowSet.updateObject(" + columnIndex
                        + ", " + x + "), isBeforeFirst()=false and isAfterLast()=false");
            }

            this.updateRowFlag = true;
            this.updateRowPosition = this.presentRow;
            getCurrentRow().updateObject(columnIndex, x);
        } else {
            throw new SQLException("Updation not allowed on this column");
        }
    }

    public void updateNull(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateNull(" + columnIndex + ")");
        }

        updateObject(columnIndex, null);
    }

    public synchronized void updateCharacterStream(int columnIndex, Reader x, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateCharacterStream("
                    + columnIndex + ", Reader, " + length + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateCharacterStream("
                    + columnIndex + ", " + x + ", " + length + ")");
        }

        checkColumnIndex(columnIndex);
        try {
            char[] c = new char[length];
            int l_totalCharsRead = 0;
            do {
                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleCachedRowSet.updateCharacterStream(int, Reader, int), inside do-whlie loop, totalCharsRead="
                                            + l_totalCharsRead);
                }

                l_totalCharsRead += x.read(c, l_totalCharsRead, length - l_totalCharsRead);
            } while (l_totalCharsRead < length);
            updateObject(columnIndex, new CharArrayReader(c));
            c = null;
        } catch (IOException ea) {
            throw new SQLException("Error while reading the Stream\n" + ea.getMessage());
        }
    }

    public void updateCharacterStream(String columnName, Reader x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateCharacterStream("
                    + columnName + ", Reader, " + length + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateCharacterStream("
                    + columnName + ", " + x + ", " + length + ")");
        }

        updateCharacterStream(getColumnIndex(columnName), x, length);
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateTimestamp(" + columnName
                    + ", Timestamp x)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateTimestamp(" + columnName
                    + ", " + x + ")");
        }

        updateTimestamp(getColumnIndex(columnName), x);
    }

    public void updateBinaryStream(String columnName, InputStream x, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateBinaryStream(" + columnName
                    + ",Timestamp x, " + length + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateBinaryStream(" + columnName
                    + ", " + x + ", " + length + ")");
        }

        updateBinaryStream(getColumnIndex(columnName), x, length);
    }

    public synchronized void updateBinaryStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateBinaryStream(" + columnIndex
                    + ", java.io.InputStream, " + length + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateBinaryStream("
                    + columnIndex + ", " + x + ", " + length + ")");
        }

        try {
            byte[] b = new byte[length];
            int l_totalBytesRead = 0;
            do {
                if (OracleLog.TRACE) {
                    OracleLog
                            .print(
                                   this,
                                   1,
                                   256,
                                   64,
                                   "OracleCachedRowSet.updateBinaryStream(int, InputStream, int), inside do-while loop, totalBytesRead="
                                           + l_totalBytesRead);
                }

                l_totalBytesRead += x.read(b, l_totalBytesRead, length - l_totalBytesRead);
            } while (l_totalBytesRead < length);
            updateObject(columnIndex, new ByteArrayInputStream(b));
            b = null;
        } catch (IOException ea) {
            throw new SQLException("Error while reading the Stream\n" + ea.getMessage());
        }
    }

    public synchronized void updateAsciiStream(int columnIndex, InputStream x, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateAsciiStream(" + columnIndex
                    + ", java.io.InputStream, " + length + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateAsciiStream(" + columnIndex
                    + ", " + x + ", " + length + ")");
        }

        try {
            InputStreamReader x1 = new InputStreamReader(x);
            char[] b = new char[length];
            int l_totalCharsRead = 0;
            do {
                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleCachedRowSet.updateAsciiStream(int,InputStream,int), inside do-while loop, totalCharsRead="
                                            + l_totalCharsRead);
                }

                l_totalCharsRead += x1.read(b, l_totalCharsRead, length - l_totalCharsRead);
            } while (l_totalCharsRead < length);
            updateObject(columnIndex, new CharArrayReader(b));
            b = null;
        } catch (IOException ea) {
            throw new SQLException("Error while reading the Stream\n" + ea.getMessage());
        }
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateTimestamp(" + columnIndex
                    + ", java.sql.Timestamp)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateTimestamp(" + columnIndex
                    + ", " + x + ")");
        }

        updateObject(columnIndex, x);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateBoolean(" + columnIndex
                    + ", " + x + ")");
        }

        updateObject(columnIndex, new Boolean(x));
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateByte(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, new Byte(x));
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateShort(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, new Short(x));
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateInt(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, new Integer(x));
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateLong(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, new Long(x));
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateFloat(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, new Float(x));
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateDouble(" + columnIndex
                    + ", " + x + ")");
        }

        updateObject(columnIndex, new Double(x));
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateBigDecimal(" + columnIndex
                    + ", " + x + ")");
        }

        updateObject(columnIndex, x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateString(" + columnIndex
                    + ", " + x + ")");
        }

        updateObject(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateBytes(" + columnIndex + ", "
                    + OracleLog.bytesToPrintableForm("byte[] ", x) + ")");
        }

        updateObject(columnIndex, x);
    }

    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateDate(" + columnIndex
                    + ", java.sql.Date)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateDate(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateTime(" + columnIndex
                    + ", java.sql.Time)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateTime(" + columnIndex + ", "
                    + x + ")");
        }

        updateObject(columnIndex, new Timestamp(0, 0, 0, x.getHours(), x.getMinutes(), x
                .getSeconds(), 0));
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateTime(" + columnIndex
                    + ", Object, " + scale + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateTime(" + columnIndex + ", "
                    + x + ", " + scale + ")");
        }

        if (!(x instanceof Number))
            throw new SQLException("Passed object is not Numeric type");
        updateObject(columnIndex, new BigDecimal(new BigInteger(((Number) x).toString()), scale));
    }

    public void updateNull(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateNull(" + columnName + ")");
        }

        updateNull(getColumnIndex(columnName));
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateAsciiStream(" + columnName
                    + ", java.io.InputStrea  x, " + length);

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateAsciiStream(" + columnName
                    + ", " + x + ", " + length + ")");
        }

        updateAsciiStream(getColumnIndex(columnName), x, length);
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateBoolean( " + columnName
                    + ", " + x + ")");
        }

        updateBoolean(getColumnIndex(columnName), x);
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateByte( " + columnName + ", "
                    + x + ")");
        }

        updateByte(getColumnIndex(columnName), x);
    }

    public void updateShort(String columnName, short x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateShort( " + columnName + ", "
                    + x + ")");
        }

        updateShort(getColumnIndex(columnName), x);
    }

    public void updateInt(String columnName, int x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateInt( " + columnName + ", "
                    + x + ")");
        }

        updateInt(getColumnIndex(columnName), x);
    }

    public void updateLong(String columnName, long x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateLong( " + columnName + ", "
                    + x + ")");
        }

        updateLong(getColumnIndex(columnName), x);
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateFloat( " + columnName + ", "
                    + x + ")");
        }

        updateFloat(getColumnIndex(columnName), x);
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateDouble( " + columnName
                    + ", " + x + ")");
        }

        updateDouble(getColumnIndex(columnName), x);
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateBigDecimal(" + columnName
                    + ", " + x + ")");
        }

        updateBigDecimal(getColumnIndex(columnName), x);
    }

    public void updateString(String columnName, String x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateString( " + columnName
                    + ", " + x + ")");
        }

        updateString(getColumnIndex(columnName), x);
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateString( " + columnName
                    + ", " + OracleLog.bytesToPrintableForm("byte[]=", x) + ")");
        }

        updateBytes(getColumnIndex(columnName), x);
    }

    public void updateDate(String columnName, java.sql.Date x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateDate(" + columnName
                    + ", java.sql.Date x)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateDate( " + columnName + ", "
                    + x + ")");
        }

        updateDate(getColumnIndex(columnName), x);
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateTime(" + columnName
                    + ", Time x)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateTime( " + columnName + ", "
                    + x + ")");
        }

        updateTime(getColumnIndex(columnName), x);
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateObject(" + columnName
                    + ", Object x)");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateObject( " + columnName
                    + ", " + x + ")");
        }

        updateObject(getColumnIndex(columnName), x);
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSet.updateObject(" + columnName
                    + ", Object x, " + scale + ")");

            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSet.updateObject( " + columnName
                    + ", " + x + ", " + scale + ")");
        }

        updateObject(getColumnIndex(columnName), x, scale);
    }

    public URL getURL(int columnIndex) throws SQLException {
        Object u = getObject(columnIndex);

        if (u == null) {
            return (URL) u;
        }
        if ((u instanceof URL)) {
            return (URL) u;
        }
        throw new SQLException("Invalid column type");
    }

    public URL getURL(String columnName) throws SQLException {
        return getURL(getColumnIndex(columnName));
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        updateRef(getColumnIndex(columnName), x);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        updateBlob(getColumnIndex(columnName), x);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        updateClob(getColumnIndex(columnName), x);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        updateArray(getColumnIndex(columnName), x);
    }

    public int[] getKeyColumns() throws SQLException {
        return this.keyColumns;
    }

    public void setKeyColumns(int[] keyCols) throws SQLException {
        int colNum = 0;

        if (this.rowsetMetaData != null) {
            colNum = this.rowsetMetaData.getColumnCount();
            if ((keyCols == null) || (keyCols.length > colNum)) {
                throw new SQLException("Invalid number of key columns");
            }
        }

        int keyColNum = keyCols.length;
        this.keyColumns = new int[keyColNum];

        for (int i = 0; i < keyColNum; i++) {
            if ((keyCols[i] <= 0) || (keyCols[i] > colNum)) {
                throw new SQLException("Invalid column index: " + keyCols[i]);
            }

            this.keyColumns[i] = keyCols[i];
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pSize) throws SQLException {
        if ((pSize < 0) || ((this.maxRows > 0) && (pSize > this.maxRows))) {
            throw new SQLException("Invalid page size specified");
        }

        this.pageSize = pSize;
    }

    public SyncProvider getSyncProvider() throws SQLException {
        return this.syncProvider;
    }

    public void setSyncProvider(String providerName) throws SQLException {
        this.syncProvider = SyncFactory.getInstance(providerName);
        this.reader = this.syncProvider.getRowSetReader();
        this.writer = this.syncProvider.getRowSetWriter();
    }

    public String getTableName() throws SQLException {
        return this.tableName;
    }

    public void setTableName(String tblName) throws SQLException {
        this.tableName = tblName;
    }

    public CachedRowSet createCopyNoConstraints() throws SQLException {
        OracleCachedRowSet ocrs = (OracleCachedRowSet) createCopy();

        ocrs.initializeProperties();

        ocrs.listener = new Vector();
        try {
            ocrs.unsetMatchColumn(ocrs.getMatchColumnIndexes());
            ocrs.unsetMatchColumn(ocrs.getMatchColumnNames());
        } catch (SQLException exc) {
        }

        return ocrs;
    }

    public CachedRowSet createCopySchema() throws SQLException {
        OracleCachedRowSet ocrs = (OracleCachedRowSet) createCopy();

        ocrs.rows = null;
        ocrs.rowCount = 0;
        ocrs.currentPage = 0;

        return ocrs;
    }

    public boolean columnUpdated(int columnIndex) throws SQLException {
        if (this.insertRowFlag) {
            throw new SQLException("Trying to mark an inserted row as original");
        }

        return getCurrentRow().isColumnChanged(columnIndex);
    }

    public boolean columnUpdated(String columnName) throws SQLException {
        return columnUpdated(getColumnIndex(columnName));
    }

    public synchronized void execute(Connection conn) throws SQLException {
        this.connection = conn;
        execute();
    }

    public void commit() throws SQLException {
        getConnectionInternal().commit();
    }

    public void rollback() throws SQLException {
        getConnectionInternal().rollback();
    }

    public void rollback(Savepoint svpt) throws SQLException {
        Connection localConnection = getConnectionInternal();
        boolean autoCommitStatus = localConnection.getAutoCommit();
        try {
            localConnection.setAutoCommit(false);
            localConnection.rollback(svpt);
        } finally {
            localConnection.setAutoCommit(autoCommitStatus);
        }
    }

    public void oracleRollback(OracleSavepoint svpt) throws SQLException {
        ((OracleConnection) getConnectionInternal()).oracleRollback(svpt);
    }

    public void setOriginalRow() throws SQLException {
        if (this.insertRowFlag) {
            throw new SQLException("Invalid operation on this row before insertRow is called");
        }

        setOriginalRowInternal(this.presentRow);
    }

    public int size() {
        return this.rowCount;
    }

    public boolean nextPage() throws SQLException {
        if ((this.fetchDirection == 1001) && (this.resultSet != null)
                && (this.resultSet.getType() == 1003)) {
            throw new SQLException("The underlying ResultSet does not support this operation");
        }
        if ((this.rows.size() == 0) && (!this.isPopulateDone)) {
            throw new SQLException(
                    "This operation can not be called without previous paging operations");
        }

        populate(this.resultSet);
        this.currentPage += 1;

        return !this.isPopulateDone;
    }

    public boolean previousPage() throws SQLException {
        if ((this.resultSet != null) && (this.resultSet.getType() == 1003)) {
            throw new SQLException("The underlying ResultSet does not support this operation");
        }

        if ((this.rows.size() == 0) && (!this.isPopulateDone)) {
            throw new SQLException(
                    "This operation can not be called without previous paging operations");
        }

        if (this.fetchDirection == 1001) {
            this.resultSet.relative(this.pageSize * 2);
        } else {
            this.resultSet.relative(-2 * this.pageSize);
        }

        populate(this.resultSet);

        if (this.currentPage > 0) {
            this.currentPage -= 1;
        }

        return this.currentPage != 0;
    }

    public void rowSetPopulated(RowSetEvent event, int numRows) throws SQLException {
        if ((numRows <= 0) || (numRows < this.fetchSize)) {
            throw new SQLException("Invalid number of row parameter specified");
        }

        if (this.rowCount % numRows == 0) {
            this.rowsetEvent = new RowSetEvent(this);
            notifyRowSetChanged();
        }
    }

    public RowSetWarning getRowSetWarnings() throws SQLException {
        return this.rowsetWarning;
    }

    public void populate(ResultSet rset, int startRow) throws SQLException {
        if (startRow < 0) {
            throw new SQLException("The start position should not be negative");
        }
        if (rset == null) {
            throw new SQLException("Null ResultSet supplied to populate");
        }
        int rsetType = rset.getType();

        if (rsetType == 1003) {
            int skipRow = 0;
            while ((rset.next()) && (skipRow < startRow)) {
                skipRow++;
            }

            if (skipRow < startRow)
                throw new SQLException("Too few rows to start populating at this position");
        } else {
            rset.absolute(startRow);
        }

        populate(rset);
    }

    public void undoDelete() throws SQLException {
        cancelRowDelete();
    }

    public void undoInsert() throws SQLException {
        cancelRowInsert();
    }

    public void undoUpdate() throws SQLException {
        cancelRowUpdates();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleCachedRowSet JD-Core Version: 0.6.0
 */