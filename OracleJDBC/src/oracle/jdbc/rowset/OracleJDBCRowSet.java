package oracle.jdbc.rowset;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetWarning;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;

public class OracleJDBCRowSet extends OracleRowSet implements RowSet, JdbcRowSet {
    private Connection connection;
    private static boolean driverManagerInitialized;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public OracleJDBCRowSet() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.OracleJDBCRowSet()");
        }

        driverManagerInitialized = false;
    }

    public OracleJDBCRowSet(Connection conn) throws SQLException {
        this();
        this.connection = conn;
    }

    public void execute() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.execute()");
        }

        this.connection = getConnection(this);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.execute(), connection="
                    + this.connection);
        }

        this.connection.setTransactionIsolation(getTransactionIsolation());
        this.connection.setTypeMap(getTypeMap());

        if (this.preparedStatement == null) {
            this.preparedStatement = this.connection.prepareStatement(getCommand(), getType(),
                                                                      getConcurrency());
        }
        this.preparedStatement.setFetchSize(getFetchSize());
        this.preparedStatement.setFetchDirection(getFetchDirection());
        this.preparedStatement.setMaxFieldSize(getMaxFieldSize());
        this.preparedStatement.setMaxRows(getMaxRows());
        this.preparedStatement.setQueryTimeout(getQueryTimeout());
        this.preparedStatement.setEscapeProcessing(getEscapeProcessing());

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.execute(), preparedStatement="
                    + this.preparedStatement);
        }

        this.resultSet = this.preparedStatement.executeQuery();
        notifyRowSetChanged();
    }

    public void close() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.close()");

            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.close(), resultSet="
                    + this.resultSet + ", preparedStatement=" + this.preparedStatement
                    + ", connection=" + this.connection);
        }

        if (this.resultSet != null) {
            this.resultSet.close();
        }
        if (this.preparedStatement != null) {
            this.preparedStatement.close();
        }
        if ((this.connection != null) && (!this.connection.isClosed())) {
            this.connection.commit();
            this.connection.close();
        }
        notifyRowSetChanged();
    }

    private Connection getConnection(RowSet rowset) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getConnection(" + rowset + ")");
        }

        Connection con = null;

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleJDBCRowSet.getConnection(RowSet),  connection="
                                    + this.connection);
        }

        if ((this.connection != null) && (!this.connection.isClosed())) {
            con = this.connection;
        } else if (rowset.getDataSourceName() != null) {
            try {
                InitialContext initialcontext = new InitialContext();
                DataSource datasource = (DataSource) initialcontext.lookup(rowset
                        .getDataSourceName());

                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleJDBCRowSet.getConnection(RowSet),  initialcontext="
                                            + initialcontext + ", datasource=" + datasource);
                }

                if ((rowset.getUsername() == null) || (rowset.getPassword() == null)) {
                    con = datasource.getConnection();
                } else {
                    con = datasource.getConnection(rowset.getUsername(), rowset.getPassword());
                }

            } catch (NamingException ea) {
                throw new SQLException("Unable to connect through the DataSource\n"
                        + ea.getMessage());
            }

        } else if (rowset.getUrl() != null) {
            if (!driverManagerInitialized) {
                DriverManager.registerDriver(new OracleDriver());
                driverManagerInitialized = true;
            }
            String url = rowset.getUrl();
            String userName = rowset.getUsername();
            String password = rowset.getPassword();

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.getConnection(RowSet), url = "
                        + url + ", userName = " + userName + ", password = " + password);
            }

            if ((url.equals("")) || (userName.equals("")) || (password.equals(""))) {
                throw new SQLException("One or more of the authenticating parameter not set");
            }

            con = DriverManager.getConnection(url, userName, password);
        }

        return con;
    }

    public boolean wasNull() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.wasNull()");
        }

        return this.resultSet.wasNull();
    }

    public SQLWarning getWarnings() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getWarnings()");
        }

        return this.resultSet.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.clearWarnings()");
        }

        this.resultSet.clearWarnings();
    }

    public String getCursorName() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getCursorName()");
        }

        return this.resultSet.getCursorName();
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getMetaData()");
        }

        return new OracleRowSetMetaData(this.resultSet.getMetaData());
    }

    public int findColumn(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.findColumn(" + columnName + ")");
        }

        return this.resultSet.findColumn(columnName);
    }

    public void clearParameters() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.clearParameters()");
        }

        this.preparedStatement.clearParameters();
    }

    public Statement getStatement() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getStatement()");
        }

        return this.resultSet.getStatement();
    }

    public void setCommand(String cmd) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setCommand(" + cmd + ")");
        }

        super.setCommand(cmd);

        if ((this.connection == null) || (this.connection.isClosed())) {
            this.connection = getConnection(this);
        }
        if (this.preparedStatement != null) {
            try {
                this.preparedStatement.close();
                this.preparedStatement = null;
            } catch (SQLException ea) {
            }
        }
        this.preparedStatement = this.connection.prepareStatement(cmd, getType(), getConcurrency());
    }

    public void setReadOnly(boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setReadOnly(" + value + ")");
        }

        super.setReadOnly(value);

        if (this.connection != null) {
            this.connection.setReadOnly(value);
        } else {
            throw new SQLException("Connection not open");
        }
    }

    public void setFetchDirection(int direction) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setFetchDirection(" + direction
                    + ")");
        }

        super.setFetchDirection(direction);

        this.resultSet.setFetchDirection(this.fetchDirection);
    }

    public void setShowDeleted(boolean value) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setShowDeleted(" + value + ")");
        }

        if (value) {
            throw new SQLException(
                    "This JdbcRowSet implementation does not allow deleted rows to be visible");
        }

        super.setShowDeleted(value);
    }

    public boolean next() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.next()");
        }

        boolean result = this.resultSet.next();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.next(). result=" + result);
        }

        if (result)
            notifyCursorMoved();
        return result;
    }

    public boolean previous() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.previous()");
        }

        boolean result = this.resultSet.previous();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.previous(), result=" + result);
        }

        if (result)
            notifyCursorMoved();
        return result;
    }

    public void beforeFirst() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.beforeFirst()");
        }

        if (!isBeforeFirst()) {
            this.resultSet.beforeFirst();
            notifyCursorMoved();
        }
    }

    public void afterLast() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.afterLast()");
        }

        if (!isAfterLast()) {
            this.resultSet.afterLast();
            notifyCursorMoved();
        }
    }

    public boolean first() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.first()");
        }

        boolean result = this.resultSet.first();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.first(), result=" + result);
        }

        if (result)
            notifyCursorMoved();
        return result;
    }

    public boolean last() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.last()");
        }

        boolean result = this.resultSet.last();

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.last(), result=" + result);
        }

        if (result)
            notifyCursorMoved();
        return result;
    }

    public boolean absolute(int row) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.absolute(" + row + ")");
        }

        boolean result = this.resultSet.absolute(row);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.absolute(" + row + "), result="
                    + result);
        }

        if (result)
            notifyCursorMoved();
        return result;
    }

    public boolean relative(int rows) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.relative(" + rows + ")");
        }

        boolean result = this.resultSet.relative(rows);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.relative(" + rows + "), result="
                    + result);
        }

        if (result)
            notifyCursorMoved();
        return result;
    }

    public boolean isBeforeFirst() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.isBeforeFirst()");
        }

        return this.resultSet.isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.isAfterLast()");
        }

        return this.resultSet.isAfterLast();
    }

    public boolean isFirst() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.isFirst()");
        }

        return this.resultSet.isFirst();
    }

    public boolean isLast() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.isLast()");
        }

        return this.resultSet.isLast();
    }

    public void insertRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.insertRow()");
        }

        this.resultSet.insertRow();
        notifyRowChanged();
    }

    public void updateRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateRow()");
        }

        this.resultSet.updateRow();
        notifyRowChanged();
    }

    public void deleteRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.deleteRow()");
        }

        this.resultSet.deleteRow();
        notifyRowChanged();
    }

    public void refreshRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.refreshRow()");
        }

        this.resultSet.refreshRow();
    }

    public void cancelRowUpdates() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.cancelRowUpdates()");
        }

        this.resultSet.cancelRowUpdates();
        notifyRowChanged();
    }

    public void moveToInsertRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.moveToInsertRow()");
        }

        this.resultSet.moveToInsertRow();
    }

    public void moveToCurrentRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.moveToCurrentRow()");
        }

        this.resultSet.moveToCurrentRow();
    }

    public int getRow() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getRow()");
        }

        return this.resultSet.getRow();
    }

    public boolean rowUpdated() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.rowUpdated()");
        }

        return this.resultSet.rowUpdated();
    }

    public boolean rowInserted() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.rowInserted()");
        }

        return this.resultSet.rowInserted();
    }

    public boolean rowDeleted() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.rowDeleted()");
        }

        return this.resultSet.rowDeleted();
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setNull(" + parameterIndex + ", "
                    + sqlType + ")");
        }

        this.preparedStatement.setNull(parameterIndex, sqlType);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setNull(" + parameterIndex + ", "
                    + sqlType + ", " + typeName + ")");
        }

        this.preparedStatement.setNull(parameterIndex, sqlType, typeName);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setBoolean(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setByte(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setByte(parameterIndex, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setShort(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setShort(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setInt(" + parameterIndex + ", " + x
                    + ")");
        }

        this.preparedStatement.setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setLong(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setLong(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setFloat(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setFloat(parameterIndex, x);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setDouble(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setDouble(parameterIndex, x);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setBigDecimal(" + parameterIndex
                    + ", " + x + ")");
        }

        this.preparedStatement.setBigDecimal(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setString(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setString(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setBytes(" + parameterIndex
                    + ", byte x[])");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setBytes(" + parameterIndex + ", "
                    + OracleLog.bytesToPrintableForm("x=", x) + ")");
        }

        this.preparedStatement.setBytes(parameterIndex, x);
    }

    public void setDate(int parameterIndex, Date x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setDate(" + parameterIndex
                    + ", java.sql.Date x)");

            OracleLog.print(this, 1, 256, 64, "OracleJDBCRowSet.setDate(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setDate(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setTime(" + parameterIndex
                    + ", java.sql.Time x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setTime(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setTime(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setObject(" + parameterIndex
                    + ", Object x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setObject(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setObject(parameterIndex, x);
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setRef(" + parameterIndex
                    + ", Ref x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setRef(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setRef(parameterIndex, x);
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setBlob(" + parameterIndex
                    + ", Blob x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setBlob(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setBlob(parameterIndex, x);
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setClob(" + parameterIndex
                    + ", Clob x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setClob(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setClob(parameterIndex, x);
    }

    public void setArray(int parameterIndex, Array x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setArray(" + parameterIndex + ", "
                    + x + ")");
        }

        this.preparedStatement.setArray(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setBinaryStream(" + parameterIndex
                    + ", java.io.InputStream x, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setBinaryStream(" + parameterIndex
                    + ", " + x + ", " + length + ")");
        }

        this.preparedStatement.setBinaryStream(parameterIndex, x, length);
    }

    public void setTime(int parameterIndex, Time time, Calendar calendar) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setTime(" + parameterIndex + ", "
                    + time + ", Calendar)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setTime(" + parameterIndex + ", "
                    + time + ", " + calendar + ")");
        }

        this.preparedStatement.setTime(parameterIndex, time, calendar);
    }

    public void setTimestamp(int parameterIndex, Timestamp timestamp, Calendar calendar)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setTimestamp(" + parameterIndex
                    + ", Timestamp, Calendar)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setTimestamp(" + parameterIndex
                    + ", " + timestamp + ", " + calendar + ")");
        }

        this.preparedStatement.setTimestamp(parameterIndex, timestamp, calendar);
    }

    public void setTimestamp(int parameterIndex, Timestamp timestamp) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setTimestamp(" + parameterIndex
                    + ", " + timestamp + ")");
        }

        this.preparedStatement.setTimestamp(parameterIndex, timestamp);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setAsciiStream(" + parameterIndex
                    + ", java.io.InputStream x, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setAsciiStream(" + parameterIndex
                    + ", " + x + ", " + length + ")");
        }

        this.preparedStatement.setAsciiStream(parameterIndex, x, length);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setCharacterStream("
                    + parameterIndex + ", " + reader + ", " + length + ")");
        }

        this.preparedStatement.setCharacterStream(parameterIndex, reader, length);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setObject(" + parameterIndex
                    + ", Object x, " + targetSqlType + ", " + scale + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setObject(" + parameterIndex + ", "
                    + x + ", " + targetSqlType + ", " + scale + ")");
        }

        this.preparedStatement.setObject(parameterIndex, x, targetSqlType, scale);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setObject(" + parameterIndex
                    + ", Object x, " + targetSqlType + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setObject(" + parameterIndex + ", "
                    + x + ", " + targetSqlType + ")");
        }

        this.preparedStatement.setObject(parameterIndex, x, targetSqlType);
    }

    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.setDate(" + parameterIndex
                    + ", java.sql.Date x, Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.setDate(" + parameterIndex + ", "
                    + x + ", " + cal + ")");
        }

        this.preparedStatement.setDate(parameterIndex, x, cal);
    }

    public Object getObject(int parameterIndex, Map map) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getObject(" + parameterIndex
                    + ", java.util.Map map)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getObject(" + parameterIndex + ", "
                    + map + ")");
        }

        return this.resultSet.getObject(parameterIndex, map);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBigDecimal(" + columnIndex + ")");
        }

        return this.resultSet.getBigDecimal(columnIndex);
    }

    public Ref getRef(int parameterIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getRef(" + parameterIndex + ")");
        }

        return this.resultSet.getRef(parameterIndex);
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBlob(" + parameterIndex + ")");
        }

        return this.resultSet.getBlob(parameterIndex);
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getClob(" + parameterIndex + ")");
        }

        return this.resultSet.getClob(parameterIndex);
    }

    public Array getArray(int parameterIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getArray(" + parameterIndex + ")");
        }

        return this.resultSet.getArray(parameterIndex);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getDate(" + columnIndex
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getDate(" + columnIndex + ", "
                    + cal + ")");
        }

        return this.resultSet.getDate(columnIndex, cal);
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getCharacterStream(" + columnIndex
                    + ")");
        }

        return this.resultSet.getCharacterStream(columnIndex);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTime(" + columnIndex
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getTime(" + columnIndex + ", "
                    + cal + ")");
        }

        return this.resultSet.getTime(columnIndex, cal);
    }

    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBinaryStream(" + columnIndex
                    + ")");
        }

        return this.resultSet.getBinaryStream(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTimestamp(" + columnIndex
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getTimestamp(" + columnIndex + ", "
                    + cal + ")");
        }

        return this.resultSet.getTimestamp(columnIndex, cal);
    }

    public String getString(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getString(" + columnIndex + ")");
        }

        return this.resultSet.getString(columnIndex);
    }

    public boolean getBoolean(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBoolean(" + columnIndex + ")");
        }

        return this.resultSet.getBoolean(columnIndex);
    }

    public byte getByte(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getByte(" + columnIndex + ")");
        }

        return this.resultSet.getByte(columnIndex);
    }

    public short getShort(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getShort(" + columnIndex + ")");
        }

        return this.resultSet.getShort(columnIndex);
    }

    public long getLong(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getLong(" + columnIndex + ")");
        }

        return this.resultSet.getLong(columnIndex);
    }

    public float getFloat(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getFloat(" + columnIndex + ")");
        }

        return this.resultSet.getFloat(columnIndex);
    }

    public double getDouble(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getDouble(" + columnIndex + ")");
        }

        return this.resultSet.getDouble(columnIndex);
    }

    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBigDecimal(" + columnIndex + ", "
                    + scale + ")");
        }

        return this.resultSet.getBigDecimal(columnIndex, scale);
    }

    public byte[] getBytes(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBytes(" + columnIndex + ")");
        }

        return this.resultSet.getBytes(columnIndex);
    }

    public Date getDate(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getDate(" + columnIndex + ")");
        }

        return this.resultSet.getDate(columnIndex);
    }

    public Time getTime(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTime(" + columnIndex + ")");
        }

        return this.resultSet.getTime(columnIndex);
    }

    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTimestamp(" + columnIndex + ")");
        }

        return this.resultSet.getTimestamp(columnIndex);
    }

    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleJDBCRowSet.getAsciiStream(" + columnIndex + ")");
        }

        return this.resultSet.getAsciiStream(columnIndex);
    }

    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getUnicodeStream(" + columnIndex
                    + ")");
        }

        return this.resultSet.getUnicodeStream(columnIndex);
    }

    public int getInt(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getInt(" + columnIndex + ")");
        }

        return this.resultSet.getInt(columnIndex);
    }

    public Object getObject(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getObject(" + columnIndex + ")");
        }

        return this.resultSet.getObject(columnIndex);
    }

    public int getInt(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getInt(" + columnName + ")");
        }

        return this.resultSet.getInt(columnName);
    }

    public long getLong(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getLong(" + columnName + ")");
        }

        return this.resultSet.getLong(columnName);
    }

    public float getFloat(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getFloat(" + columnName + ")");
        }

        return this.resultSet.getFloat(columnName);
    }

    public double getDouble(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getDouble(" + columnName + ")");
        }

        return this.resultSet.getDouble(columnName);
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBigDecimal(" + columnName + ", "
                    + scale + ")");
        }

        return this.resultSet.getBigDecimal(columnName, scale);
    }

    public byte[] getBytes(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBytes(" + columnName + ")");
        }

        return this.resultSet.getBytes(columnName);
    }

    public Date getDate(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getDate(" + columnName + ")");
        }

        return this.resultSet.getDate(columnName);
    }

    public Time getTime(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTime(" + columnName + ")");
        }

        return this.resultSet.getTime(columnName);
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTimestamp(" + columnName + ")");
        }

        return this.resultSet.getTimestamp(columnName);
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getAsciiStream(" + columnName + ")");
        }

        return this.resultSet.getAsciiStream(columnName);
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getUnicodeStream(" + columnName
                    + ")");
        }

        return this.resultSet.getUnicodeStream(columnName);
    }

    public Object getObject(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getObject(" + columnName + ")");
        }

        return this.resultSet.getObject(columnName);
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getCharacterStream(" + columnName
                    + ")");
        }

        return this.resultSet.getCharacterStream(columnName);
    }

    public Object getObject(String colName, Map map) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getObject(" + colName
                    + ", java.util.Map map)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getObject(" + colName + ", " + map
                    + ")");
        }

        return this.resultSet.getObject(colName, map);
    }

    public Ref getRef(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getRef(" + colName + ")");
        }

        return this.resultSet.getRef(colName);
    }

    public Blob getBlob(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBlob(" + colName + ")");
        }

        return this.resultSet.getBlob(colName);
    }

    public Clob getClob(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getClob(" + colName + ")");
        }

        return this.resultSet.getClob(colName);
    }

    public Array getArray(String colName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getArray(" + colName + ")");
        }

        return this.resultSet.getArray(colName);
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBigDecimal(" + columnName + ")");
        }

        return this.resultSet.getBigDecimal(columnName);
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getDate(" + columnName
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getDate(" + columnName + ", " + cal
                    + ")");
        }

        return this.resultSet.getDate(columnName, cal);
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTime(" + columnName
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getTime(" + columnName + ", " + cal
                    + ")");
        }

        return this.resultSet.getTime(columnName, cal);
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog
                    .print(this, 1, 256, 1, "OracleJDBCRowSet.getBinaryStream(" + columnName + ")");
        }

        return this.resultSet.getBinaryStream(columnName);
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getTimestamp(" + columnName
                    + ", Calendar cal)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.getTimestamp(" + columnName + ", "
                    + cal + ")");
        }

        return this.resultSet.getTimestamp(columnName, cal);
    }

    public String getString(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getString(" + columnName + ")");
        }

        return this.resultSet.getString(columnName);
    }

    public boolean getBoolean(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getBoolean(" + columnName + ")");
        }

        return this.resultSet.getBoolean(columnName);
    }

    public byte getByte(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getByte(" + columnName + ")");
        }

        return this.resultSet.getByte(columnName);
    }

    public short getShort(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.getShort(" + columnName + ")");
        }

        return this.resultSet.getShort(columnName);
    }

    public void updateNull(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateNull(" + columnIndex + ")");
        }

        this.resultSet.updateNull(columnIndex);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateCharacterStream("
                    + columnIndex + ", " + x + ", " + length + ")");
        }

        this.resultSet.updateCharacterStream(columnIndex, x, length);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateTimestamp(" + columnIndex
                    + ", " + x + ")");
        }

        this.resultSet.updateTimestamp(columnIndex, x);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBinaryStream(" + columnIndex
                    + ", java.io.InputStream x, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateBinaryStream(" + columnIndex
                    + ", " + x + ", " + length + ")");
        }

        this.resultSet.updateBinaryStream(columnIndex, x, length);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateAsciiStream(" + columnIndex
                    + ", java.io.InputStream x, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateAsciiStream(" + columnIndex
                    + ", " + x + ", " + length + ")");
        }

        this.resultSet.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBoolean(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateBoolean(columnIndex, x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateByte(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateByte(columnIndex, x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateShort(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateShort(columnIndex, x);
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateInt(" + columnIndex + ", " + x
                    + ")");
        }

        this.resultSet.updateInt(columnIndex, x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateLong(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateLong(columnIndex, x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateFloat(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateFloat(columnIndex, x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateDouble(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateDouble(columnIndex, x);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBigDecimal(" + columnIndex
                    + ", " + x + ")");
        }

        this.resultSet.updateBigDecimal(columnIndex, x);
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateString(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateString(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBytes(" + columnIndex
                    + ", byte x[])");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateBytes(" + columnIndex + ", "
                    + OracleLog.bytesToPrintableForm("x[]=", x) + ")");
        }

        this.resultSet.updateBytes(columnIndex, x);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateDate(" + columnIndex
                    + ", java.sql.Date x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateDate(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateDate(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateTime(" + columnIndex
                    + ", java.sql.Time x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateTime(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateTime(columnIndex, x);
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateObject(" + columnIndex
                    + ", Object x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateObject(" + columnIndex + ", "
                    + x + ")");
        }

        this.resultSet.updateObject(columnIndex, x);
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateObject(" + columnIndex
                    + ", Object x, " + scale + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateObject(" + columnIndex + ", "
                    + x + ", " + scale + ")");
        }

        this.resultSet.updateObject(columnIndex, x, scale);
    }

    public void updateNull(String columnName) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateNull(" + columnName + ")");
        }

        this.resultSet.updateNull(columnName);
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBoolean(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateBoolean(columnName, x);
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateByte(" + columnName + ", " + x
                    + ")");
        }

        this.resultSet.updateByte(columnName, x);
    }

    public void updateShort(String columnName, short x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateShort(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateShort(columnName, x);
    }

    public void updateInt(String columnName, int x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateInt(" + columnName + ", " + x
                    + ")");
        }

        this.resultSet.updateInt(columnName, x);
    }

    public void updateLong(String columnName, long x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateLong(" + columnName + ", " + x
                    + ")");
        }

        this.resultSet.updateLong(columnName, x);
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateFloat(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateFloat(columnName, x);
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateDouble(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateDouble(columnName, x);
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBigDecimal(" + columnName
                    + ", " + x + ")");
        }

        this.resultSet.updateBigDecimal(columnName, x);
    }

    public void updateString(String columnName, String x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateString(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateString(columnName, x);
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBytes(" + columnName
                    + ", byte x[])");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateBytes(" + columnName + ", "
                    + OracleLog.bytesToPrintableForm("x[]=", x) + ")");
        }

        this.resultSet.updateBytes(columnName, x);
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateDate(" + columnName
                    + ", java.sql.Date x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateDate(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateDate(columnName, x);
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateTime(" + columnName
                    + ", java.sql.Time x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateTime(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateTime(columnName, x);
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateObject(" + columnName
                    + ", Object x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateObject(" + columnName + ", "
                    + x + ")");
        }

        this.resultSet.updateObject(columnName, x);
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateObject(" + columnName + ", "
                    + x + ", " + scale + ")");
        }

        this.resultSet.updateObject(columnName, x, scale);
    }

    public void updateBinaryStream(String columnName, InputStream x, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateBinaryStream(" + columnName
                    + ", java.io.InputStream x, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateBinaryStream(" + columnName
                    + ", " + x + ", " + length + ")");
        }

        this.resultSet.updateBinaryStream(columnName, x, length);
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateAsciiStream(" + columnName
                    + ", java.io.InputStream x, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateAsciiStream(" + columnName
                    + ", " + x + ", " + length + ")");
        }

        this.resultSet.updateAsciiStream(columnName, x, length);
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateTimestamp(" + columnName
                    + ", java.sql.Timestamp x)");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateTimestamp(" + columnName
                    + ", " + x + ")");
        }

        this.resultSet.updateTimestamp(columnName, x);
    }

    public void updateCharacterStream(String columnName, Reader reader, int length)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleJDBCRowSet.updateCharacterStream(" + columnName
                    + ", java.io.Reader, " + length + ")");

            OracleLog.print(this, 1, 256, 16, "OracleJDBCRowSet.updateCharacterStream("
                    + columnName + ", " + reader + ", " + length + ")");
        }

        this.resultSet.updateCharacterStream(columnName, reader, length);
    }

    public URL getURL(int columnIndex) throws SQLException {
        return ((OracleResultSet) this.resultSet).getURL(columnIndex);
    }

    public URL getURL(String columnName) throws SQLException {
        return ((OracleResultSet) this.resultSet).getURL(columnName);
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateRef(columnIndex, x);
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateRef(columnName, x);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateBlob(columnIndex, x);
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateBlob(columnName, x);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateClob(columnIndex, x);
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateClob(columnName, x);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateArray(columnIndex, x);
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        ((OracleResultSet) this.resultSet).updateArray(columnName, x);
    }

    public void commit() throws SQLException {
        if (this.connection != null) {
            this.connection.commit();
        } else {
            throw new SQLException("Connection not open");
        }
    }

    public void rollback() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback();
        } else {
            throw new SQLException("Connection not open");
        }
    }

    public void rollback(Savepoint svpt) throws SQLException {
        if (this.connection != null) {
            this.connection.rollback(svpt);
        } else {
            throw new SQLException("Connection not open");
        }
    }

    public void oracleRollback(OracleSavepoint svpt) throws SQLException {
        if (this.connection != null) {
            ((OracleConnection) this.connection).oracleRollback(svpt);
        } else {
            throw new SQLException("Connection not open");
        }
    }

    public boolean getAutoCommit() throws SQLException {
        if (this.connection != null) {
            return this.connection.getAutoCommit();
        }

        throw new SQLException("Connection not open");
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (this.connection != null) {
            this.connection.setAutoCommit(autoCommit);
        } else {
            throw new SQLException("Connection not open");
        }
    }

    public RowSetWarning getRowSetWarnings() throws SQLException {
        return null;
    }

    String getTableName() throws SQLException {
        return getMetaData().getTableName(getMatchColumnIndexes()[0]);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleJDBCRowSet JD-Core Version: 0.6.0
 */