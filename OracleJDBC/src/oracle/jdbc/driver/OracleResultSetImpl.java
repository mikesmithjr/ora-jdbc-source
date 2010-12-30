package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

class OracleResultSetImpl extends BaseResultSet {
    PhysicalConnection connection;
    OracleStatement statement;
    boolean closed;
    boolean explicitly_closed;
    boolean m_emptyRset;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    OracleResultSetImpl(PhysicalConnection c, OracleStatement s) throws SQLException {
        this.connection = c;
        this.statement = s;
        this.close_statement_on_close = false;
        this.closed = false;
        this.explicitly_closed = false;
        this.m_emptyRset = false;
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.close()", this);

            OracleLog.recursiveTrace = false;
        }

        internal_close(false);

        if (this.close_statement_on_close) {
            try {
                this.statement.close();
            } catch (SQLException e) {
            }

        }

        this.explicitly_closed = true;
    }

    public synchronized boolean wasNull() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.wasNull()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.statement.wasNullValue();
    }

    public synchronized ResultSetMetaData getMetaData() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getMetaData()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.explicitly_closed) {
            DatabaseError.throwSqlException(10, "getMetaData");
        }

        if (this.statement.closed) {
            DatabaseError.throwSqlException(9, "getMetaData");
        }

        if (!this.statement.isOpen) {
            DatabaseError.throwSqlException(144, "getMetaData");
        }

        return new OracleResultSetMetaData(this.connection, this.statement);
    }

    public synchronized Statement getStatement() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetImpl.getStatement(): return: "
                    + this.statement, this);

            OracleLog.recursiveTrace = false;
        }

        return this.statement;
    }

    public synchronized boolean next() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetImpl.next(): closed="
                    + this.closed, this);

            OracleLog.recursiveTrace = false;
        }

        boolean result = true;

        PhysicalConnection l_connection = this.statement.connection;

        if (this.explicitly_closed) {
            DatabaseError.throwSqlException(10, "next");
        }

        if ((l_connection == null) || (l_connection.lifecycle != 1)) {
            DatabaseError.throwSqlException(8, "next");
        }

        if (this.statement.closed) {
            DatabaseError.throwSqlException(9, "next");
        }

        if ((this.statement.sqlKind == 1) || (this.statement.sqlKind == 4)) {
            DatabaseError.throwSqlException(166, "next");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "closed=" + this.closed
                    + ", statement.currentRow=" + this.statement.currentRow
                    + ", statement.totalRowsVisited=" + this.statement.totalRowsVisited
                    + ", statement.maxRows=" + this.statement.maxRows + ", statement.validRows="
                    + this.statement.validRows + ", statement.gotLastBatch="
                    + this.statement.gotLastBatch, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            return false;
        }
        this.statement.currentRow += 1;
        this.statement.totalRowsVisited += 1;

        if ((this.statement.maxRows != 0)
                && (this.statement.totalRowsVisited > this.statement.maxRows)) {
            internal_close(false);

            return false;
        }

        if (this.statement.currentRow >= this.statement.validRows) {
            result = close_or_fetch_from_next(false);
        }
        if ((result) && (l_connection.useFetchSizeWithLongColumn)) {
            this.statement.reopenStreams();
        }
        return result;
    }

    private boolean close_or_fetch_from_next(boolean close) throws SQLException {
        if (close) {
            internal_close(false);

            return false;
        }

        if (this.statement.gotLastBatch) {
            internal_close(false);

            return false;
        }

        this.statement.check_row_prefetch_changed();

        PhysicalConnection l_connection = this.statement.connection;

        if (l_connection.protocolId == 3) {
            this.sqlWarning = null;
        } else {
            if (this.statement.streamList != null) {
                while (this.statement.nextStream != null) {
                    try {
                        this.statement.nextStream.close();
                    } catch (IOException exc) {
                        DatabaseError.throwSqlException(exc);
                    }

                    this.statement.nextStream = this.statement.nextStream.nextStream;
                }
            }

            clearWarnings();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "OracleResultSetImpl.next about to call fetch", this);

                OracleLog.recursiveTrace = false;
            }

            l_connection.needLine();
        }

        synchronized (l_connection) {
            this.statement.fetch();
        }

        if (this.statement.validRows == 0) {
            internal_close(false);

            return false;
        }

        this.statement.currentRow = 0;

        this.statement.checkValidRowsStatus();

        return true;
    }

    public boolean isBeforeFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleResultSetImpl.isBeforeFirst: return: "
                                 + ((!isEmptyResultSet()) && (this.statement.currentRow == -1) && (!this.closed)),
                         this);

            OracleLog.recursiveTrace = false;
        }

        return (!isEmptyResultSet()) && (this.statement.currentRow == -1) && (!this.closed);
    }

    public boolean isAfterLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetImpl.isAfterLast: return: "
                    + ((!isEmptyResultSet()) && (this.closed)), this);

            OracleLog.recursiveTrace = false;
        }

        return (!isEmptyResultSet()) && (this.closed);
    }

    public boolean isFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetImpl.isFirst: return: "
                    + (getRow() == 1), this);

            OracleLog.recursiveTrace = false;
        }

        return getRow() == 1;
    }

    public boolean isLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.isLast()", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(75, "isLast");

        return false;
    }

    public int getRow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetImpl.getRow: return: "
                    + this.statement.totalRowsVisited, this);

            OracleLog.recursiveTrace = false;
        }

        return this.statement.totalRowsVisited;
    }

    public synchronized String getString(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getString(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getString(current_row);
    }

    public synchronized boolean getBoolean(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getBoolean(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBoolean(current_row);
    }

    public synchronized byte getByte(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getByte(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getByte(current_row);
    }

    public synchronized short getShort(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getShort(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getShort(current_row);
    }

    public synchronized int getInt(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getInt(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getInt(current_row);
    }

    public synchronized long getLong(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getLong(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getLong(current_row);
    }

    public synchronized float getFloat(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getFloat(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getFloat(current_row);
    }

    public synchronized double getDouble(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getDouble(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getDouble(current_row);
    }

    public synchronized BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getBigDecimal(columnIndex="
                    + columnIndex + ", scale=" + scale + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBigDecimal(this.statement.currentRow,
                                                                         scale);
    }

    synchronized byte[] privateGetBytes(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResultSetImpl.privateGetBytes(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].privateGetBytes(current_row);
    }

    public synchronized byte[] getBytes(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getBytes(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBytes(current_row);
    }

    public synchronized Date getDate(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getDate(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getDate(current_row);
    }

    public synchronized Time getTime(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getTime(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTime(current_row);
    }

    public synchronized Timestamp getTimestamp(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getTimestamp(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTimestamp(current_row);
    }

    public synchronized InputStream getAsciiStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResultSetImpl.getAsciiStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getAsciiStream(current_row);
    }

    public synchronized InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResultSetImpl.getUnicodeStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getUnicodeStream(current_row);
    }

    public synchronized InputStream getBinaryStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResultSetImpl.getBinaryStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBinaryStream(current_row);
    }

    public synchronized Object getObject(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getObject(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getObject(current_row);
    }

    public synchronized ResultSet getCursor(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getCursor(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getCursor(current_row);
    }

    public synchronized Datum getOracleObject(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResultSetImpl.getOracleObject(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getOracleObject(current_row);
    }

    public synchronized ROWID getROWID(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getROWID(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getROWID(current_row);
    }

    public synchronized NUMBER getNUMBER(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getNUMBER(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getNUMBER(current_row);
    }

    public synchronized DATE getDATE(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getDATE(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getDATE(current_row);
    }

    public synchronized ARRAY getARRAY(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getARRAY(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getARRAY(current_row);
    }

    public synchronized STRUCT getSTRUCT(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getSTRUCT(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getSTRUCT(current_row);
    }

    public synchronized OPAQUE getOPAQUE(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getOPAQUE(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getOPAQUE(current_row);
    }

    public synchronized REF getREF(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getREF(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getREF(current_row);
    }

    public synchronized CHAR getCHAR(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getCHAR(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getCHAR(current_row);
    }

    public synchronized RAW getRAW(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getRAW(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getRAW(current_row);
    }

    public synchronized BLOB getBLOB(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getBLOB(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBLOB(current_row);
    }

    public synchronized CLOB getCLOB(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getCLOB(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getCLOB(current_row);
    }

    public synchronized BFILE getBFILE(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getBFILE(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBFILE(current_row);
    }

    public synchronized BFILE getBfile(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getBfile(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getBFILE(columnIndex);
    }

    public synchronized CustomDatum getCustomDatum(int columnIndex, CustomDatumFactory factory)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleResultSetImpl.getCustomDatum(columnIndex="
                                               + columnIndex + ", factory=" + factory + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)]
                .getCustomDatum(this.statement.currentRow, factory);
    }

    public synchronized ORAData getORAData(int columnIndex, ORADataFactory factory)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getORAData(columnIndex="
                    + columnIndex + ", factory=" + factory + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getORAData(this.statement.currentRow,
                                                                      factory);
    }

    public synchronized Object getObject(int columnIndex, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getObject(columnIndex="
                    + columnIndex + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)]
                .getObject(this.statement.currentRow, map);
    }

    public synchronized Ref getRef(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getRef(columnIndex=" + columnIndex
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getREF(columnIndex);
    }

    public synchronized Blob getBlob(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getBlog(columnIndex=" + columnIndex
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getBLOB(columnIndex);
    }

    public synchronized Clob getClob(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getClob(columnIndex=" + columnIndex
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getCLOB(columnIndex);
    }

    public synchronized Array getArray(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getArray(columnIndex=" + columnIndex
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getARRAY(columnIndex);
    }

    public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getCharacterStream(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getCharacterStream(current_row);
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getBigDecimal(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getBigDecimal(current_row);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getDate(columnIndex=" + columnIndex
                    + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getDate(this.statement.currentRow, cal);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getTime(columnIndex=" + columnIndex
                    + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTime(this.statement.currentRow, cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getTimestamp(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTimestamp(this.statement.currentRow,
                                                                        cal);
    }

    public INTERVALYM getINTERVALYM(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getINTERVALYM(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getINTERVALYM(current_row);
    }

    public INTERVALDS getINTERVALDS(int columnIndex) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 32, 1, "ResultSet.getINTERVALDS(columnIndex=" + columnIndex
                    + ")");
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getINTERVALDS(current_row);
    }

    public TIMESTAMP getTIMESTAMP(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getTIMESTAMP(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTIMESTAMP(current_row);
    }

    public TIMESTAMPTZ getTIMESTAMPTZ(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getTIMESTAMPTZ(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTIMESTAMPTZ(current_row);
    }

    public TIMESTAMPLTZ getTIMESTAMPLTZ(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getTIMESTAMPLTZ(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getTIMESTAMPLTZ(current_row);
    }

    public synchronized URL getURL(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.getURL(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((columnIndex <= 0) || (columnIndex > this.statement.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        if (this.closed) {
            DatabaseError.throwSqlException(11);
        }
        int current_row = this.statement.currentRow;

        if (current_row < 0) {
            DatabaseError.throwSqlException(14);
        }
        this.statement.lastIndex = columnIndex;

        if (this.statement.streamList != null) {
            this.statement.closeUsedStreams(columnIndex);
        }

        return this.statement.accessors[(columnIndex - 1)].getURL(current_row);
    }

    public void setFetchSize(int rows) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.setFetchSize(rows=" + rows + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        this.statement.setPrefetchInternal(rows, false, false);
    }

    public int getFetchSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ResultSet.getFetchSize()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.statement.getPrefetchInternal(false);
    }

    void internal_close(boolean from_prepare_for_new_result) throws SQLException {
        if (this.closed) {
            return;
        }
        this.closed = true;

        if ((this.statement.gotLastBatch) && (this.statement.validRows == 0)) {
            this.m_emptyRset = true;
        }
        PhysicalConnection l_connection = this.statement.connection;
        try {
            l_connection.needLine();

            synchronized (l_connection) {
                this.statement.closeQuery();
            }

        } catch (SQLException e1) {
        }

        this.statement.endOfResultSet(from_prepare_for_new_result);
    }

    public synchronized int findColumn(String columnName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleResultSetImpl.findColumn(columnName="
                    + columnName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return this.statement.getColumnIndex(columnName);
    }

    boolean isEmptyResultSet() {
        return (this.m_emptyRset)
                || ((!this.m_emptyRset) && (this.statement.gotLastBatch) && (this.statement.validRows == 0));
    }

    int getValidRows() {
        return this.statement.validRows;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleResultSetImpl"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleResultSetImpl JD-Core Version: 0.6.0
 */