package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
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
import java.util.Vector;
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

class ScrollableResultSet extends BaseResultSet {
    PhysicalConnection connection;
    OracleResultSetImpl resultSet;
    ScrollRsetStatement scrollStmt;
    ResultSetMetaData metadata;
    private int rsetType;
    private int rsetConcurency;
    private int beginColumnIndex;
    private int columnCount;
    private int wasNull;
    OracleResultSetCache rsetCache;
    int currentRow;
    private int numRowsCached;
    private boolean allRowsCached;
    private int lastRefetchSz;
    private Vector refetchRowids;
    private OraclePreparedStatement refetchStmt;
    private int usrFetchDirection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    ScrollableResultSet(ScrollRsetStatement stmt, OracleResultSetImpl rset, int type, int update)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.ScrollableResultSet(scrollStmt="
                                               + this.scrollStmt + ", rset=" + rset + ", type="
                                               + type + ", update=" + update, this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = ((OracleStatement) stmt).connection;
        this.resultSet = rset;
        this.metadata = null;
        this.scrollStmt = stmt;
        this.rsetType = type;
        this.rsetConcurency = update;
        this.autoRefetch = stmt.getAutoRefetch();
        this.beginColumnIndex = (needIdentifier(type, update) ? 1 : 0);
        this.columnCount = 0;
        this.wasNull = -1;
        this.rsetCache = stmt.getResultSetCache();

        if (this.rsetCache == null) {
            this.rsetCache = new OracleResultSetCacheImpl();
        } else {
            try {
                this.rsetCache.clear();
            } catch (IOException e) {
                DatabaseError.throwSqlException(e);
            }
        }

        this.currentRow = 0;
        this.numRowsCached = 0;
        this.allRowsCached = false;
        this.lastRefetchSz = 0;
        this.refetchRowids = null;
        this.refetchStmt = null;
        this.usrFetchDirection = 1000;
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.close()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.resultSet != null) {
            this.resultSet.close();
        }
        if (this.refetchStmt != null) {
            this.refetchStmt.close();
        }
        if (this.scrollStmt != null) {
            this.scrollStmt.notifyCloseRset();
        }
        if (this.refetchRowids != null) {
            this.refetchRowids.removeAllElements();
        }
        this.resultSet = null;
        this.scrollStmt = null;
        this.refetchStmt = null;
        this.refetchRowids = null;
        this.metadata = null;
        this.connection = null;
        try {
            if (this.rsetCache != null) {
                this.rsetCache.clear();
                this.rsetCache.close();
            }
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }

        this.rsetCache = null;
    }

    public synchronized boolean wasNull() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.wasNull()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.wasNull == -1) {
            DatabaseError.throwSqlException(24);
        }
        return this.wasNull == 1;
    }

    public synchronized Statement getStatement() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.getStatement: return: "
                    + (Statement) this.scrollStmt, this);

            OracleLog.recursiveTrace = false;
        }

        return (Statement) this.scrollStmt;
    }

    synchronized void resetBeginColumnIndex() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.resetBeginColumnIndex()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        this.beginColumnIndex = 0;
    }

    synchronized ResultSet getResultSet() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.getResultSet: return: "
                    + this.resultSet, this);

            OracleLog.recursiveTrace = false;
        }

        return this.resultSet;
    }

    synchronized int removeRowInCache(int rowIdx) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.removeRowInCache(rowIdx="
                    + rowIdx + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((!isEmptyResultSet()) && (isValidRow(rowIdx))) {
            removeCachedRowAt(rowIdx);

            this.numRowsCached -= 1;

            if (rowIdx >= this.currentRow) {
                this.currentRow -= 1;
            }
            return 1;
        }

        return 0;
    }

    synchronized int refreshRowsInCache(int beginIdx, int count, int direction) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.removeRowInCache(beginIdx="
                    + beginIdx + ", count=" + count + ", direction=" + direction + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleResultSet refetchRset = null;
        int actualRefetchSize = 0;

        actualRefetchSize = get_refetch_size(beginIdx, count, direction);
        try {
            if (actualRefetchSize > 0) {
                if (actualRefetchSize != this.lastRefetchSz) {
                    if (this.refetchStmt != null) {
                        this.refetchStmt.close();
                    }
                    this.refetchStmt = prepare_refetch_statement(actualRefetchSize);
                    this.refetchStmt.setQueryTimeout(((OracleStatement) this.scrollStmt)
                            .getQueryTimeout());
                    this.lastRefetchSz = actualRefetchSize;
                }

                prepare_refetch_binds(this.refetchStmt, actualRefetchSize);

                refetchRset = (OracleResultSet) this.refetchStmt.executeQuery();

                save_refetch_results(refetchRset, beginIdx, actualRefetchSize, direction);
            }

        } finally {
            if (refetchRset != null) {
                refetchRset.close();
            }
        }
        return actualRefetchSize;
    }

    public synchronized boolean next() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.next()", this);

            OracleLog.recursiveTrace = false;
        }

        if (isEmptyResultSet()) {
            return false;
        }

        if (this.currentRow < 1) {
            this.currentRow = 1;
        } else {
            this.currentRow += 1;
        }

        return isValidRow(this.currentRow);
    }

    public synchronized boolean isBeforeFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.isBeforeFirst()", this);

            OracleLog.recursiveTrace = false;
        }

        return (!isEmptyResultSet()) && (this.currentRow < 1);
    }

    public synchronized boolean isAfterLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.isAfterLast()", this);

            OracleLog.recursiveTrace = false;
        }

        return (!isEmptyResultSet()) && (this.currentRow > 0) && (!isValidRow(this.currentRow));
    }

    public synchronized boolean isFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.isFirst: return: "
                    + (this.currentRow == 1), this);

            OracleLog.recursiveTrace = false;
        }

        return this.currentRow == 1;
    }

    public synchronized boolean isLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.isLast()", this);

            OracleLog.recursiveTrace = false;
        }

        return (!isEmptyResultSet()) && (isValidRow(this.currentRow))
                && (!isValidRow(this.currentRow + 1));
    }

    public synchronized void beforeFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.beforeFirst()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isEmptyResultSet())
            this.currentRow = 0;
    }

    public synchronized void afterLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.afterLast()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!isEmptyResultSet())
            this.currentRow = (getLastRow() + 1);
    }

    public synchronized boolean first() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.first()", this);

            OracleLog.recursiveTrace = false;
        }

        if (isEmptyResultSet()) {
            return false;
        }

        this.currentRow = 1;

        return isValidRow(this.currentRow);
    }

    public synchronized boolean last() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.last()", this);

            OracleLog.recursiveTrace = false;
        }

        if (isEmptyResultSet()) {
            return false;
        }

        this.currentRow = getLastRow();

        return isValidRow(this.currentRow);
    }

    public synchronized int getRow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getRow()", this);

            OracleLog.recursiveTrace = false;
        }

        if (isValidRow(this.currentRow)) {
            return this.currentRow;
        }
        return 0;
    }

    public synchronized boolean absolute(int row) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.absolute(row=" + row + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (row == 0) {
            DatabaseError.throwSqlException(68, "absolute (0)");
        }

        if (isEmptyResultSet()) {
            return false;
        }
        if (row > 0) {
            this.currentRow = row;
        } else if (row < 0) {
            this.currentRow = (getLastRow() + 1 + row);
        }

        return isValidRow(this.currentRow);
    }

    public synchronized boolean relative(int rows) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.relative(rows=" + rows
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (isEmptyResultSet()) {
            return false;
        }
        if (isValidRow(this.currentRow)) {
            this.currentRow += rows;

            return isValidRow(this.currentRow);
        }

        DatabaseError.throwSqlException(82, "relative");

        return false;
    }

    public synchronized boolean previous() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.previous()", this);

            OracleLog.recursiveTrace = false;
        }

        if (isEmptyResultSet()) {
            return false;
        }
        if (isAfterLast()) {
            this.currentRow = getLastRow();
        } else {
            this.currentRow -= 1;
        }

        return isValidRow(this.currentRow);
    }

    public synchronized Datum getOracleObject(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getOracleObject(columnIndex="
                                               + columnIndex + "): currentRow=" + this.currentRow,
                                       this);

            OracleLog.recursiveTrace = false;
        }

        this.wasNull = -1;

        if (!isValidRow(this.currentRow)) {
            DatabaseError.throwSqlException(11);
        }
        if ((columnIndex < 1) || (columnIndex > getColumnCount())) {
            DatabaseError.throwSqlException(3);
        }
        Datum value = getCachedDatumValueAt(this.currentRow, columnIndex + this.beginColumnIndex);

        this.wasNull = (value == null ? 1 : 0);

        return value;
    }

    public synchronized String getString(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getString(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum value = getOracleObject(columnIndex);

        if (value != null) {
            return value.stringValue();
        }
        return null;
    }

    public synchronized boolean getBoolean(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBoolean(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.booleanValue();
        }

        return false;
    }

    public synchronized byte getByte(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getByte(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.byteValue();
        }
        return 0;
    }

    public synchronized short getShort(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getShort(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        long result = getLong(columnIndex);

        if ((result > 65537L) || (result < -65538L)) {
            DatabaseError.throwSqlException(26, "getShort");
        }

        return (short) (int) result;
    }

    public synchronized int getInt(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getInt(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.intValue();
        }

        return 0;
    }

    public synchronized long getLong(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getLong(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.longValue();
        }

        return 0L;
    }

    public synchronized float getFloat(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getFloat(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.floatValue();
        }

        return 0.0F;
    }

    public synchronized double getDouble(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBouble(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.doubleValue();
        }

        return 0.0D;
    }

    public synchronized BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBigDecimal(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.bigDecimalValue();
        }

        return null;
    }

    public synchronized byte[] getBytes(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBytes(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof RAW)) {
                return ((RAW) datum).shareBytes();
            }
            DatabaseError.throwSqlException(4, "getBytes");
        }

        return null;
    }

    public synchronized Date getDate(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getDate(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.dateValue();
        }

        return null;
    }

    public synchronized Time getTime(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getTime(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.timeValue();
        }

        return null;
    }

    public synchronized Timestamp getTimestamp(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getTimestamp(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.timestampValue();
        }

        return null;
    }

    public synchronized InputStream getAsciiStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getAsciiStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.asciiStreamValue();
        }

        return null;
    }

    public synchronized InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getUnicodeStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            DBConversion dbconv = this.connection.conversion;
            byte[] bytes = datum.shareBytes();

            if ((datum instanceof RAW)) {
                return dbconv.ConvertStream(new ByteArrayInputStream(bytes), 3);
            }

            if ((datum instanceof CHAR)) {
                return dbconv.ConvertStream(new ByteArrayInputStream(bytes), 1);
            }

            DatabaseError.throwSqlException(4, "getUnicodeStream");
        }

        return null;
    }

    public synchronized InputStream getBinaryStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getBinaryStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.binaryStreamValue();
        }

        return null;
    }

    public synchronized Object getObject(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getObject(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getObject(columnIndex, this.connection.getTypeMap());
    }

    public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getCharacterStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.characterStreamValue();
        }

        return null;
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBigDecimal(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getBigDecimal(columnIndex, 0);
    }

    public synchronized Object getObject(int columnIndex, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getObject(columnIndex="
                    + columnIndex + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof STRUCT)) {
                return ((STRUCT) datum).toJdbc(map);
            }
            return datum.toJdbc();
        }

        return null;
    }

    public synchronized Ref getRef(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getRef(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getREF(columnIndex);
    }

    public synchronized Blob getBlob(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBlob(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getBLOB(columnIndex);
    }

    public synchronized Clob getClob(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getClob(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getCLOB(columnIndex);
    }

    public synchronized Array getArray(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getArray(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getARRAY(columnIndex);
    }

    public synchronized Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getDate(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DATE d = getDATE(columnIndex);

        return d != null ? d.dateValue(cal) : null;
    }

    public synchronized Time getTime(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getTime(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DATE d = getDATE(columnIndex);

        return d != null ? d.timeValue(cal) : null;
    }

    public synchronized Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getTimestamp(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DATE d = getDATE(columnIndex);

        return d != null ? d.timestampValue(cal) : null;
    }

    public synchronized URL getURL(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getURL(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        URL result = null;

        int colType = getInternalMetadata().getColumnType(columnIndex);
        int dbTypeCode = SQLUtil.getInternalType(colType);

        if ((dbTypeCode == 96) || (dbTypeCode == 1) || (dbTypeCode == 8)) {
            try {
                result = new URL(getString(columnIndex));
            } catch (MalformedURLException exc) {
                DatabaseError.throwSqlException(136);
            }
        } else {
            throw new SQLException("Conversion to java.net.URL not supported.");
        }

        return result;
    }

    public synchronized ResultSet getCursor(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getCursor(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(4, "getCursor");

        return null;
    }

    public synchronized ROWID getROWID(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getROWID(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof ROWID)) {
                return (ROWID) datum;
            }
            DatabaseError.throwSqlException(4, "getROWID");
        }

        return null;
    }

    public synchronized NUMBER getNUMBER(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getNUMBER(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof NUMBER)) {
                return (NUMBER) datum;
            }
            DatabaseError.throwSqlException(4, "getNUMBER");
        }

        return null;
    }

    public synchronized DATE getDATE(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getDATE(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof DATE)) {
                return (DATE) datum;
            }
            DatabaseError.throwSqlException(4, "getDATE");
        }

        return null;
    }

    public synchronized TIMESTAMP getTIMESTAMP(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getTIMESTAMP(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof TIMESTAMP)) {
                return (TIMESTAMP) datum;
            }
            DatabaseError.throwSqlException(4, "getTIMESTAMP");
        }

        return null;
    }

    public synchronized TIMESTAMPTZ getTIMESTAMPTZ(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getTIMESTAMPTZ(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof TIMESTAMPTZ)) {
                return (TIMESTAMPTZ) datum;
            }
            DatabaseError.throwSqlException(4, "getTIMESTAMPTZ");
        }

        return null;
    }

    public synchronized TIMESTAMPLTZ getTIMESTAMPLTZ(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.getTIMESTAMPLTZ(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof TIMESTAMPLTZ)) {
                return (TIMESTAMPLTZ) datum;
            }
            DatabaseError.throwSqlException(4, "getTIMESTAMPLTZ");
        }

        return null;
    }

    public synchronized INTERVALDS getINTERVALDS(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getINTERVALDS(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof INTERVALDS)) {
                return (INTERVALDS) datum;
            }
            DatabaseError.throwSqlException(4, "getINTERVALDS");
        }

        return null;
    }

    public synchronized INTERVALYM getINTERVALYM(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getINTERVALYM(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof INTERVALYM)) {
                return (INTERVALYM) datum;
            }
            DatabaseError.throwSqlException(4, "getINTERVALYM");
        }

        return null;
    }

    public synchronized ARRAY getARRAY(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getARRAY(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof ARRAY)) {
                return (ARRAY) datum;
            }
            DatabaseError.throwSqlException(4, "getARRAY");
        }

        return null;
    }

    public synchronized STRUCT getSTRUCT(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getSTRUCT(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof STRUCT)) {
                return (STRUCT) datum;
            }
            DatabaseError.throwSqlException(4, "getSTRUCT");
        }

        return null;
    }

    public synchronized OPAQUE getOPAQUE(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getOPAQUE(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof OPAQUE)) {
                return (OPAQUE) datum;
            }
            DatabaseError.throwSqlException(4, "getOPAQUE");
        }

        return null;
    }

    public synchronized REF getREF(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getREF(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof REF)) {
                return (REF) datum;
            }
            DatabaseError.throwSqlException(4, "getREF");
        }

        return null;
    }

    public synchronized CHAR getCHAR(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getCHAR(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof CHAR)) {
                return (CHAR) datum;
            }
            DatabaseError.throwSqlException(4, "getCHAR");
        }

        return null;
    }

    public synchronized RAW getRAW(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getRAW(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof RAW)) {
                return (RAW) datum;
            }
            DatabaseError.throwSqlException(4, "getRAW");
        }

        return null;
    }

    public synchronized BLOB getBLOB(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBLOB(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof BLOB)) {
                return (BLOB) datum;
            }
            DatabaseError.throwSqlException(4, "getBLOB");
        }

        return null;
    }

    public synchronized CLOB getCLOB(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getCLOB(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof CLOB)) {
                return (CLOB) datum;
            }
            DatabaseError.throwSqlException(4, "getCLOB");
        }

        return null;
    }

    public synchronized BFILE getBFILE(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBFILE(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            if ((datum instanceof BFILE)) {
                return (BFILE) datum;
            }
            DatabaseError.throwSqlException(4, "getBFILE");
        }

        return null;
    }

    public synchronized BFILE getBfile(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getBfile(columnIndex="
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
                                       "ScrollableResultSet.getCustomDatum(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum d = getOracleObject(columnIndex);

        return factory.create(d, 0);
    }

    public synchronized ORAData getORAData(int columnIndex, ORADataFactory factory)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getORAData(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum d = getOracleObject(columnIndex);

        return factory.create(d, 0);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getMetaData()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                return new OracleResultSetMetaData(connection, (OracleStatement) scrollStmt,
                        beginColumnIndex);
            }

        }
    }

    public synchronized int findColumn(String columnName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.findColumn(columnName="
                    + columnName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return this.resultSet.findColumn(columnName) - this.beginColumnIndex;
    }

    public synchronized void setFetchDirection(int direction) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.setFetchDirection(direction="
                                               + direction + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (direction == 1000) {
            this.usrFetchDirection = direction;
        } else if ((direction == 1001) || (direction == 1002)) {
            this.usrFetchDirection = direction;

            this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 87);
        } else {
            DatabaseError.throwSqlException(68, "setFetchDirection");
        }
    }

    public synchronized int getFetchDirection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getFetchDirection()", this);

            OracleLog.recursiveTrace = false;
        }

        return 1000;
    }

    public synchronized void setFetchSize(int rows) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.setFetchSize(rows=" + rows
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.resultSet.setFetchSize(rows);
    }

    public synchronized int getFetchSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getFetchSize()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.resultSet.getFetchSize();
    }

    public int getType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.getType: return: "
                    + this.rsetType, this);

            OracleLog.recursiveTrace = false;
        }

        return this.rsetType;
    }

    public int getConcurrency() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.getConcurrency: return: "
                    + this.rsetConcurency, this);

            OracleLog.recursiveTrace = false;
        }

        return this.rsetConcurency;
    }

    public void refreshRow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.refreshRow()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!needIdentifier(this.rsetType, this.rsetConcurency)) {
            DatabaseError.throwSqlException(23, "refreshRow");
        }

        if (isValidRow(this.currentRow)) {
            int direction = getFetchDirection();
            try {
                refreshRowsInCache(this.currentRow, getFetchSize(), direction);
            } catch (SQLException e) {
                DatabaseError.throwSqlException(e, 90, "Unsupported syntax for refreshRow()");
            }
        } else {
            DatabaseError.throwSqlException(82, "refreshRow");
        }
    }

    public void setCurrentRowValueAt(Object caller, int columnIdx, Object value)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ScrollableResultSet.setCurrentRowValueAt(caller=" + caller
                                               + ", columnIdx=" + columnIdx + ", value=" + value,
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if ((caller instanceof UpdatableResultSet))
            putCachedValueAt(this.currentRow, columnIdx, value);
        else
            DatabaseError.throwSqlException(1);
    }

    private boolean isEmptyResultSet() throws SQLException {
        if (this.numRowsCached != 0) {
            return false;
        }
        if ((this.numRowsCached == 0) && (this.allRowsCached)) {
            return true;
        }

        return !isValidRow(1);
    }

    boolean isValidRow(int idx) throws SQLException {
        if ((idx > 0) && (idx <= this.numRowsCached)) {
            return true;
        }
        if (idx <= 0) {
            return false;
        }

        return cacheRowAt(idx);
    }

    private boolean cacheRowAt(int idx) throws SQLException {
        while ((this.numRowsCached < idx) && (this.resultSet.next())) {
            for (int i = 0; i < getColumnCount(); i++) {
                byte[] bytes = this.resultSet.privateGetBytes(i + 1);

                putCachedValueAt(this.numRowsCached + 1, i + 1, bytes);
            }

            this.numRowsCached += 1;
        }

        if (this.numRowsCached < idx) {
            this.allRowsCached = true;

            return false;
        }

        return true;
    }

    private int cacheAllRows() throws SQLException {
        while (this.resultSet.next()) {
            for (int i = 0; i < getColumnCount(); i++) {
                putCachedValueAt(this.numRowsCached + 1, i + 1, this.resultSet
                        .privateGetBytes(i + 1));
            }
            this.numRowsCached += 1;
        }

        this.allRowsCached = true;

        return this.numRowsCached;
    }

    int getColumnCount() throws SQLException {
        if (this.columnCount == 0) {
            int numberOfArgs = this.resultSet.statement.numberOfDefinePositions;

            if ((this.resultSet.statement.accessors != null) && (numberOfArgs > 0))
                this.columnCount = numberOfArgs;
            else {
                this.columnCount = getInternalMetadata().getColumnCount();
            }
        }
        return this.columnCount;
    }

    private ResultSetMetaData getInternalMetadata() throws SQLException {
        if (this.metadata == null) {
            this.metadata = this.resultSet.getMetaData();
        }
        return this.metadata;
    }

    private int getLastRow() throws SQLException {
        if (!this.allRowsCached) {
            cacheAllRows();
        }
        return this.numRowsCached;
    }

    private int get_refetch_size(int beginIdx, int count, int direction) throws SQLException {
        int _direction = direction == 1001 ? -1 : 1;

        int realRefreshSz = 0;

        if (this.refetchRowids == null)
            this.refetchRowids = new Vector(10);
        else {
            this.refetchRowids.removeAllElements();
        }

        while ((realRefreshSz < count) && (isValidRow(beginIdx + realRefreshSz * _direction))) {
            this.refetchRowids.addElement(getCachedDatumValueAt(beginIdx + realRefreshSz
                    * _direction, 1));

            realRefreshSz++;
        }

        return realRefreshSz;
    }

    private OraclePreparedStatement prepare_refetch_statement(int realRefreshSz)
            throws SQLException {
        if (realRefreshSz < 1) {
            DatabaseError.throwSqlException(68);
        }

        return (OraclePreparedStatement) this.connection
                .prepareStatement(((OracleStatement) this.scrollStmt).sqlObject
                        .getRefetchSqlForScrollableResultSet(this, realRefreshSz));
    }

    private void prepare_refetch_binds(OraclePreparedStatement refetchStmt, int realRefreshSz)
            throws SQLException {
        int _numSubQueryBinds = this.scrollStmt.copyBinds(refetchStmt, 0);

        for (int i = 0; i < realRefreshSz; i++) {
            refetchStmt
                    .setROWID(_numSubQueryBinds + i + 1, (ROWID) this.refetchRowids.elementAt(i));
        }
    }

    private void save_refetch_results(OracleResultSet refetchRset, int _beginIdx, int _realFetchSz,
            int direction) throws SQLException {
        int _direction = direction == 1001 ? -1 : 1;

        while (refetchRset.next()) {
            ROWID thisRowid = refetchRset.getROWID(1);

            boolean found = false;
            int thisRow = _beginIdx;

            while ((!found) && (thisRow < _beginIdx + _realFetchSz * _direction)) {
                if (((ROWID) getCachedDatumValueAt(thisRow, 1)).stringValue()
                        .equals(thisRowid.stringValue())) {
                    found = true;
                    continue;
                }
                thisRow += _direction;
            }

            if (found) {
                for (int i = 0; i < getColumnCount(); i++) {
                    putCachedValueAt(thisRow, i + 1, refetchRset.getOracleObject(i + 1));
                }
            }
        }
    }

    private Object getCachedValueAt(int row, int column) throws SQLException {
        try {
            return this.rsetCache.get(row, column);
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }

        return null;
    }

    private Datum getCachedDatumValueAt(int row, int column) throws SQLException {
        Object value = null;
        try {
            value = this.rsetCache.get(row, column);
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }

        Datum datumValue = null;

        if (value != null) {
            if ((value instanceof Datum)) {
                datumValue = (Datum) value;
            } else if (((byte[]) value).length > 0) {
                int _colType = getInternalMetadata().getColumnType(column);
                int _colLength = getInternalMetadata().getColumnDisplaySize(column);

                int max = this.scrollStmt.getMaxFieldSize();

                if ((max > 0) && (max < _colLength)) {
                    _colLength = max;
                }
                String _sqlType = null;

                if ((_colType == 2006) || (_colType == 2002) || (_colType == 2008)
                        || (_colType == 2007) || (_colType == 2003)) {
                    _sqlType = getInternalMetadata().getColumnTypeName(column);
                }
                int sqlTypeCode = SQLUtil.getInternalType(_colType);

                short form = this.resultSet.statement.accessors[(column - 1)].formOfUse;

                if ((form == 2)
                        && ((sqlTypeCode == 96) || (sqlTypeCode == 1) || (sqlTypeCode == 8) || (sqlTypeCode == 112))) {
                    datumValue = SQLUtil.makeNDatum(this.connection, (byte[]) value, sqlTypeCode,
                                                    _sqlType, form, _colLength);
                } else {
                    datumValue = SQLUtil.makeDatum(this.connection, (byte[]) value, sqlTypeCode,
                                                   _sqlType, _colLength);
                }

                try {
                    this.rsetCache.put(row, column, datumValue);
                } catch (IOException e) {
                    DatabaseError.throwSqlException(e);
                }
            } else {
                try {
                    this.rsetCache.put(row, column, null);
                } catch (IOException e) {
                    DatabaseError.throwSqlException(e);
                }

            }

        }

        return datumValue;
    }

    private void putCachedValueAt(int row, int column, Object value) throws SQLException {
        try {
            this.rsetCache.put(row, column, value);
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
    }

    private void removeCachedRowAt(int row) throws SQLException {
        try {
            this.rsetCache.remove(row);
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
    }

    public static boolean needIdentifier(int type, int concur) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.needIdentifier(type="
                    + type + ", concur=" + concur + ")");

            OracleLog.recursiveTrace = false;
        }

        return ((type != 1003) || (concur != 1007)) && ((type != 1004) || (concur != 1007));
    }

    public static boolean needCache(int type, int concur) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.needCache(type=" + type
                    + ", concur=" + concur + ")");

            OracleLog.recursiveTrace = false;
        }

        return (type != 1003) && ((type != 1004) || (concur != 1007));
    }

    public static boolean supportRefreshRow(int type, int concur) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "ScrollableResultSet.supportRefreshRow(type="
                    + type + ", concur=" + concur + ")");

            OracleLog.recursiveTrace = false;
        }

        return (type != 1003) && ((type != 1004) || (concur != 1007));
    }

    int getFirstUserColumnIndex() {
        return this.beginColumnIndex;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.ScrollableResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ScrollableResultSet JD-Core Version: 0.6.0
 */