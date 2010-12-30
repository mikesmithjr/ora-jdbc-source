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
import java.sql.SQLWarning;
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

class UpdatableResultSet extends BaseResultSet {
    static final int concurrencyType = 1008;
    static final int beginColumnIndex = 1;
    PhysicalConnection connection;
    OracleResultSet resultSet;
    boolean isCachedRset;
    ScrollRsetStatement scrollStmt;
    ResultSetMetaData rsetMetaData;
    private int rsetType;
    private int columnCount;
    private OraclePreparedStatement deleteStmt;
    private OraclePreparedStatement insertStmt;
    private OraclePreparedStatement updateStmt;
    private int[] indexColsChanged;
    private Object[] rowBuffer;
    private boolean[] m_nullIndicator;
    private int[][] typeInfo;
    private boolean isInserting;
    private boolean isUpdating;
    private int wasNull;
    private static final int VALUE_NULL = 1;
    private static final int VALUE_NOT_NULL = 2;
    private static final int VALUE_UNKNOWN = 3;
    private static final int VALUE_IN_RSET = 4;
    private static final int ASCII_STREAM = 1;
    private static final int BINARY_STREAM = 2;
    private static final int UNICODE_STREAM = 3;
    private static int _MIN_STREAM_SIZE = 4000;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    UpdatableResultSet(ScrollRsetStatement stmt, ScrollableResultSet rset, int type, int update)
            throws SQLException {
        init(stmt, rset, type, update);

        rset.resetBeginColumnIndex();

        this.isCachedRset = true;
    }

    UpdatableResultSet(ScrollRsetStatement stmt, OracleResultSetImpl rset, int type, int update)
            throws SQLException {
        init(stmt, rset, type, update);

        this.isCachedRset = false;
    }

    private void init(ScrollRsetStatement stmt, OracleResultSet rset, int type, int update)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "UpdatableResultSet.UpdatableResultSet(stmt, rset, type= "
                                               + type + " update=" + update + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((stmt == null) || (rset == null) || (update != 1008)) {
            DatabaseError.throwSqlException(68);
        }
        this.connection = ((OracleStatement) stmt).connection;
        this.resultSet = rset;
        this.scrollStmt = stmt;
        this.rsetType = type;
        this.autoRefetch = stmt.getAutoRefetch();
        this.deleteStmt = null;
        this.insertStmt = null;
        this.updateStmt = null;
        this.indexColsChanged = null;
        this.rowBuffer = null;
        this.m_nullIndicator = null;
        this.typeInfo = ((int[][]) null);
        this.isInserting = false;
        this.isUpdating = false;
        this.wasNull = -1;
        this.rsetMetaData = null;
        this.columnCount = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "UpdatableResultSet.UpdatableResultSet:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void close() throws SQLException {
        if (this.resultSet != null) {
            this.resultSet.close();
        }
        if (this.insertStmt != null) {
            this.insertStmt.close();
        }
        if (this.updateStmt != null) {
            this.updateStmt.close();
        }
        if (this.deleteStmt != null) {
            this.deleteStmt.close();
        }
        if (this.scrollStmt != null) {
            this.scrollStmt.notifyCloseRset();
        }
        cancelRowInserts();

        this.connection = null;
        this.resultSet = null;
        this.scrollStmt = null;
        this.rsetMetaData = null;
        this.scrollStmt = null;
        this.deleteStmt = null;
        this.insertStmt = null;
        this.updateStmt = null;
        this.indexColsChanged = null;
        this.rowBuffer = null;
        this.m_nullIndicator = null;
        this.typeInfo = ((int[][]) null);
    }

    public synchronized boolean wasNull() throws SQLException {
        switch (this.wasNull) {
        case 1:
            return true;
        case 2:
            return false;
        case 4:
            return this.resultSet.wasNull();
        case 3:
        }

        DatabaseError.throwSqlException(24);

        return false;
    }

    int getFirstUserColumnIndex() {
        return 1;
    }

    public synchronized Statement getStatement() throws SQLException {
        return (Statement) this.scrollStmt;
    }

    public SQLWarning getWarnings() throws SQLException {
        SQLWarning innerWarnings = this.resultSet.getWarnings();

        if (this.sqlWarning == null) {
            return innerWarnings;
        }

        SQLWarning thisWarning = this.sqlWarning;

        while (thisWarning.getNextWarning() != null) {
            thisWarning = thisWarning.getNextWarning();
        }
        thisWarning.setNextWarning(innerWarnings);

        return this.sqlWarning;
    }

    public void clearWarnings() throws SQLException {
        this.sqlWarning = null;

        this.resultSet.clearWarnings();
    }

    public synchronized boolean next() throws SQLException {
        cancelRowChanges();

        return this.resultSet.next();
    }

    public synchronized boolean isBeforeFirst() throws SQLException {
        return this.resultSet.isBeforeFirst();
    }

    public synchronized boolean isAfterLast() throws SQLException {
        return this.resultSet.isAfterLast();
    }

    public synchronized boolean isFirst() throws SQLException {
        return this.resultSet.isFirst();
    }

    public synchronized boolean isLast() throws SQLException {
        return this.resultSet.isLast();
    }

    public synchronized void beforeFirst() throws SQLException {
        cancelRowChanges();
        this.resultSet.beforeFirst();
    }

    public synchronized void afterLast() throws SQLException {
        cancelRowChanges();
        this.resultSet.afterLast();
    }

    public synchronized boolean first() throws SQLException {
        cancelRowChanges();

        return this.resultSet.first();
    }

    public synchronized boolean last() throws SQLException {
        cancelRowChanges();

        return this.resultSet.last();
    }

    public synchronized int getRow() throws SQLException {
        return this.resultSet.getRow();
    }

    public synchronized boolean absolute(int row) throws SQLException {
        cancelRowChanges();

        return this.resultSet.absolute(row);
    }

    public synchronized boolean relative(int rows) throws SQLException {
        cancelRowChanges();

        return this.resultSet.relative(rows);
    }

    public synchronized boolean previous() throws SQLException {
        cancelRowChanges();

        return this.resultSet.previous();
    }

    public synchronized Datum getOracleObject(int columnIndex) throws SQLException {
        Datum value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            setIsNull(value == null);

            value = getRowBufferDatumAt(columnIndex);
        } else {
            setIsNull(4);

            value = this.resultSet.getOracleObject(columnIndex + 1);
        }

        return value;
    }

    public synchronized String getString(int columnIndex) throws SQLException {
        String value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.stringValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getString(columnIndex + 1);
        }

        return value;
    }

    public synchronized boolean getBoolean(int columnIndex) throws SQLException {
        boolean value = false;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.booleanValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getBoolean(columnIndex + 1);
        }

        return value;
    }

    public synchronized byte getByte(int columnIndex) throws SQLException {
        byte value = 0;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.byteValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getByte(columnIndex + 1);
        }

        return value;
    }

    public synchronized short getShort(int columnIndex) throws SQLException {
        short value = 0;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            long longvalue = getLong(columnIndex);

            if ((longvalue > 65537L) || (longvalue < -65538L)) {
                DatabaseError.throwSqlException(26, "getShort");
            }

            value = (short) (int) longvalue;
        } else {
            setIsNull(4);

            value = this.resultSet.getShort(columnIndex + 1);
        }

        return value;
    }

    public synchronized int getInt(int columnIndex) throws SQLException {
        int value = 0;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.intValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getInt(columnIndex + 1);
        }

        return value;
    }

    public synchronized long getLong(int columnIndex) throws SQLException {
        long value = 0L;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.longValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getLong(columnIndex + 1);
        }

        return value;
    }

    public synchronized float getFloat(int columnIndex) throws SQLException {
        float value = 0.0F;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.floatValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getFloat(columnIndex + 1);
        }

        return value;
    }

    public synchronized double getDouble(int columnIndex) throws SQLException {
        double value = 0.0D;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.doubleValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getDouble(columnIndex + 1);
        }

        return value;
    }

    public synchronized BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        BigDecimal value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.bigDecimalValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getBigDecimal(columnIndex + 1);
        }

        return value;
    }

    public synchronized byte[] getBytes(int columnIndex) throws SQLException {
        byte[] value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.getBytes();
        } else {
            setIsNull(4);

            value = this.resultSet.getBytes(columnIndex + 1);
        }

        return value;
    }

    public synchronized Date getDate(int columnIndex) throws SQLException {
        Date value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.dateValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getDate(columnIndex + 1);
        }

        return value;
    }

    public synchronized Time getTime(int columnIndex) throws SQLException {
        Time value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.timeValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getTime(columnIndex + 1);
        }

        return value;
    }

    public synchronized Timestamp getTimestamp(int columnIndex) throws SQLException {
        Timestamp value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.timestampValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getTimestamp(columnIndex + 1);
        }

        return value;
    }

    public synchronized InputStream getAsciiStream(int columnIndex) throws SQLException {
        InputStream value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Object obj = getRowBufferAt(columnIndex);

            setIsNull(obj == null);

            if (obj != null) {
                if ((obj instanceof InputStream)) {
                    value = (InputStream) obj;
                } else {
                    Datum datum = getRowBufferDatumAt(columnIndex);

                    value = datum.asciiStreamValue();
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getAsciiStream(columnIndex + 1);
        }

        return value;
    }

    public synchronized InputStream getUnicodeStream(int columnIndex) throws SQLException {
        InputStream value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Object obj = getRowBufferAt(columnIndex);

            setIsNull(obj == null);

            if (obj != null) {
                if ((obj instanceof InputStream)) {
                    value = (InputStream) obj;
                } else {
                    Datum datum = getRowBufferDatumAt(columnIndex);
                    DBConversion dbconv = this.connection.conversion;
                    byte[] bytes = datum.shareBytes();

                    if ((datum instanceof RAW)) {
                        value = dbconv.ConvertStream(new ByteArrayInputStream(bytes), 3);
                    } else if ((datum instanceof CHAR)) {
                        value = dbconv.ConvertStream(new ByteArrayInputStream(bytes), 1);
                    } else {
                        DatabaseError.throwSqlException(4, "getUnicodeStream");
                    }
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getUnicodeStream(columnIndex + 1);
        }

        return value;
    }

    public synchronized InputStream getBinaryStream(int columnIndex) throws SQLException {
        InputStream value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Object obj = getRowBufferAt(columnIndex);

            setIsNull(obj == null);

            if (obj != null) {
                if ((obj instanceof InputStream)) {
                    value = (InputStream) obj;
                } else {
                    Datum datum = getRowBufferDatumAt(columnIndex);

                    value = datum.binaryStreamValue();
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getBinaryStream(columnIndex + 1);
        }

        return value;
    }

    public synchronized Object getObject(int columnIndex) throws SQLException {
        Object value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getOracleObject(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.toJdbc();
        } else {
            setIsNull(4);

            value = this.resultSet.getObject(columnIndex + 1);
        }

        return value;
    }

    public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
        Reader value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Object obj = getRowBufferAt(columnIndex);

            setIsNull(obj == null);

            if (obj != null) {
                if ((obj instanceof Reader)) {
                    value = (Reader) obj;
                } else {
                    Datum datum = getRowBufferDatumAt(columnIndex);

                    value = datum.characterStreamValue();
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getCharacterStream(columnIndex + 1);
        }

        return value;
    }

    public synchronized BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        BigDecimal value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if (datum != null)
                value = datum.bigDecimalValue();
        } else {
            setIsNull(4);

            value = this.resultSet.getBigDecimal(columnIndex + 1);
        }

        return value;
    }

    public synchronized Object getObject(int columnIndex, Map map) throws SQLException {
        Object value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getOracleObject(columnIndex);

            setIsNull(datum == null);

            if (datum != null) {
                if ((datum instanceof STRUCT))
                    value = ((STRUCT) datum).toJdbc(map);
                else
                    value = datum.toJdbc();
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getObject(columnIndex + 1, map);
        }

        return value;
    }

    public synchronized Ref getRef(int columnIndex) throws SQLException {
        return getREF(columnIndex);
    }

    public synchronized Blob getBlob(int columnIndex) throws SQLException {
        return getBLOB(columnIndex);
    }

    public synchronized Clob getClob(int columnIndex) throws SQLException {
        return getCLOB(columnIndex);
    }

    public synchronized Array getArray(int columnIndex) throws SQLException {
        return getARRAY(columnIndex);
    }

    public synchronized Date getDate(int columnIndex, Calendar cal) throws SQLException {
        Date value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getOracleObject(columnIndex);

            setIsNull(datum == null);

            if (datum != null) {
                if ((datum instanceof DATE)) {
                    value = ((DATE) datum).dateValue(cal);
                } else {
                    DATE d = new DATE(datum.stringValue());

                    if (d != null)
                        value = d.dateValue(cal);
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getDate(columnIndex + 1, cal);
        }

        return value;
    }

    public synchronized Time getTime(int columnIndex, Calendar cal) throws SQLException {
        Time value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getOracleObject(columnIndex);

            setIsNull(datum == null);

            if (datum != null) {
                if ((datum instanceof DATE)) {
                    value = ((DATE) datum).timeValue(cal);
                } else {
                    DATE d = new DATE(datum.stringValue());

                    if (d != null)
                        value = d.timeValue(cal);
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getTime(columnIndex + 1, cal);
        }

        return value;
    }

    public synchronized Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        Timestamp value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getOracleObject(columnIndex);

            setIsNull(datum == null);

            if (datum != null) {
                if ((datum instanceof DATE)) {
                    value = ((DATE) datum).timestampValue(cal);
                } else {
                    DATE d = new DATE(datum.stringValue());

                    if (d != null)
                        value = d.timestampValue(cal);
                }
            }
        } else {
            setIsNull(4);

            value = this.resultSet.getTimestamp(columnIndex + 1, cal);
        }

        return value;
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
        ResultSet value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getOracleObject(columnIndex);

            setIsNull(datum == null);
            DatabaseError.throwSqlException(4, "getCursor");
        } else {
            setIsNull(4);

            value = this.resultSet.getCursor(columnIndex + 1);
        }

        return value;
    }

    public synchronized ROWID getROWID(int columnIndex) throws SQLException {
        ROWID value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof ROWID))) {
                DatabaseError.throwSqlException(4, "getROWID");
            }

            value = (ROWID) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getROWID(columnIndex + 1);
        }

        return value;
    }

    public synchronized NUMBER getNUMBER(int columnIndex) throws SQLException {
        NUMBER value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof NUMBER))) {
                DatabaseError.throwSqlException(4, "getNUMBER");
            }

            value = (NUMBER) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getNUMBER(columnIndex + 1);
        }

        return value;
    }

    public synchronized DATE getDATE(int columnIndex) throws SQLException {
        DATE value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof DATE))) {
                DatabaseError.throwSqlException(4, "getDATE");
            }

            value = (DATE) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getDATE(columnIndex + 1);
        }

        return value;
    }

    public synchronized TIMESTAMP getTIMESTAMP(int columnIndex) throws SQLException {
        TIMESTAMP value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof TIMESTAMP))) {
                DatabaseError.throwSqlException(4, "getTIMESTAMP");
            }

            value = (TIMESTAMP) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getTIMESTAMP(columnIndex + 1);
        }

        return value;
    }

    public synchronized TIMESTAMPTZ getTIMESTAMPTZ(int columnIndex) throws SQLException {
        TIMESTAMPTZ value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof TIMESTAMPTZ))) {
                DatabaseError.throwSqlException(4, "getTIMESTAMPTZ");
            }

            value = (TIMESTAMPTZ) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getTIMESTAMPTZ(columnIndex + 1);
        }

        return value;
    }

    public synchronized TIMESTAMPLTZ getTIMESTAMPLTZ(int columnIndex) throws SQLException {
        TIMESTAMPLTZ value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof TIMESTAMPLTZ))) {
                DatabaseError.throwSqlException(4, "getTIMESTAMPLTZ");
            }

            value = (TIMESTAMPLTZ) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getTIMESTAMPLTZ(columnIndex + 1);
        }

        return value;
    }

    public synchronized INTERVALDS getINTERVALDS(int columnIndex) throws SQLException {
        INTERVALDS value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof INTERVALDS))) {
                DatabaseError.throwSqlException(4, "getINTERVALDS");
            }

            value = (INTERVALDS) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getINTERVALDS(columnIndex + 1);
        }

        return value;
    }

    public synchronized INTERVALYM getINTERVALYM(int columnIndex) throws SQLException {
        INTERVALYM value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof INTERVALYM))) {
                DatabaseError.throwSqlException(4, "getINTERVALYM");
            }

            value = (INTERVALYM) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getINTERVALYM(columnIndex + 1);
        }

        return value;
    }

    public synchronized ARRAY getARRAY(int columnIndex) throws SQLException {
        ARRAY value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof ARRAY))) {
                DatabaseError.throwSqlException(4, "getARRAY");
            }

            value = (ARRAY) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getARRAY(columnIndex + 1);
        }

        return value;
    }

    public synchronized STRUCT getSTRUCT(int columnIndex) throws SQLException {
        STRUCT value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof STRUCT))) {
                DatabaseError.throwSqlException(4, "getSTRUCT");
            }

            value = (STRUCT) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getSTRUCT(columnIndex + 1);
        }

        return value;
    }

    public synchronized OPAQUE getOPAQUE(int columnIndex) throws SQLException {
        OPAQUE value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof OPAQUE))) {
                DatabaseError.throwSqlException(4, "getOPAQUE");
            }

            value = (OPAQUE) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getOPAQUE(columnIndex + 1);
        }

        return value;
    }

    public synchronized REF getREF(int columnIndex) throws SQLException {
        REF value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof REF))) {
                DatabaseError.throwSqlException(4, "getREF");
            }

            value = (REF) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getREF(columnIndex + 1);
        }

        return value;
    }

    public synchronized CHAR getCHAR(int columnIndex) throws SQLException {
        CHAR value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof CHAR))) {
                DatabaseError.throwSqlException(4, "getCHAR");
            }

            value = (CHAR) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getCHAR(columnIndex + 1);
        }

        return value;
    }

    public synchronized RAW getRAW(int columnIndex) throws SQLException {
        RAW value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof RAW))) {
                DatabaseError.throwSqlException(4, "getRAW");
            }

            value = (RAW) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getRAW(columnIndex + 1);
        }

        return value;
    }

    public synchronized BLOB getBLOB(int columnIndex) throws SQLException {
        BLOB value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof BLOB))) {
                DatabaseError.throwSqlException(4, "getBLOB");
            }

            value = (BLOB) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getBLOB(columnIndex + 1);
        }

        return value;
    }

    public synchronized CLOB getCLOB(int columnIndex) throws SQLException {
        CLOB value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof CLOB))) {
                DatabaseError.throwSqlException(4, "getCLOB");
            }

            value = (CLOB) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getCLOB(columnIndex + 1);
        }

        return value;
    }

    public synchronized BFILE getBFILE(int columnIndex) throws SQLException {
        BFILE value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            if ((datum != null) && (!(datum instanceof BFILE))) {
                DatabaseError.throwSqlException(4, "getBFILE");
            }

            value = (BFILE) datum;
        } else {
            setIsNull(4);

            value = this.resultSet.getBFILE(columnIndex + 1);
        }

        return value;
    }

    public synchronized BFILE getBfile(int columnIndex) throws SQLException {
        return getBFILE(columnIndex);
    }

    public synchronized CustomDatum getCustomDatum(int columnIndex, CustomDatumFactory factory)
            throws SQLException {
        if (factory == null) {
            DatabaseError.throwSqlException(68);
        }
        CustomDatum value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            value = factory.create(datum, 0);
        } else {
            setIsNull(4);

            value = this.resultSet.getCustomDatum(columnIndex + 1, factory);
        }

        return value;
    }

    public synchronized ORAData getORAData(int columnIndex, ORADataFactory factory)
            throws SQLException {
        if (factory == null) {
            DatabaseError.throwSqlException(68);
        }
        ORAData value = null;

        setIsNull(3);

        if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(columnIndex)))) {
            Datum datum = getRowBufferDatumAt(columnIndex);

            setIsNull(datum == null);

            value = factory.create(datum, 0);
        } else {
            setIsNull(4);

            value = this.resultSet.getORAData(columnIndex + 1, factory);
        }

        return value;
    }

    public ResultSetMetaData getMetaData()
    throws SQLException
  {
    if (((OracleStatement)this.scrollStmt).closed) {
      DatabaseError.throwSqlException(9, "getMetaData");
    }

    synchronized (this.connection)
    {
      synchronized (this)
      {
          return new OracleResultSetMetaData(connection, (OracleStatement)scrollStmt, 1);
      }
    }
  }

    public synchronized int findColumn(String columnName) throws SQLException {
        return this.resultSet.findColumn(columnName) - 1;
    }

    public synchronized void setFetchDirection(int direction) throws SQLException {
        this.resultSet.setFetchDirection(direction);
    }

    public synchronized int getFetchDirection() throws SQLException {
        return this.resultSet.getFetchDirection();
    }

    public synchronized void setFetchSize(int rows) throws SQLException {
        this.resultSet.setFetchSize(rows);
    }

    public synchronized int getFetchSize() throws SQLException {
        return this.resultSet.getFetchSize();
    }

    public int getType() throws SQLException {
        return this.rsetType;
    }

    public int getConcurrency() throws SQLException {
        return 1008;
    }

    public boolean rowUpdated() throws SQLException {
        return false;
    }

    public boolean rowInserted() throws SQLException {
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        return false;
    }

    public synchronized void insertRow() throws SQLException {
        if (!isOnInsertRow()) {
            DatabaseError.throwSqlException(83);
        }
        prepareInsertRowStatement();
        prepareInsertRowBinds();
        executeInsertRow();
    }

    public synchronized void updateRow() throws SQLException {
        if (isOnInsertRow()) {
            DatabaseError.throwSqlException(84);
        }
        int _numColsChanged = getNumColumnsChanged();

        if (_numColsChanged > 0) {
            prepareUpdateRowStatement(_numColsChanged);
            prepareUpdateRowBinds(_numColsChanged);
            executeUpdateRow();
        }
    }

    public synchronized void deleteRow() throws SQLException {
        if (isOnInsertRow()) {
            DatabaseError.throwSqlException(84);
        }
        prepareDeleteRowStatement();
        prepareDeleteRowBinds();
        executeDeleteRow();
    }

    public synchronized void refreshRow() throws SQLException {
        if (isOnInsertRow()) {
            DatabaseError.throwSqlException(84);
        }
        this.resultSet.refreshRow();
    }

    public synchronized void cancelRowUpdates() throws SQLException {
        if (this.isUpdating) {
            this.isUpdating = false;

            clearRowBuffer();
        }
    }

    public synchronized void moveToInsertRow() throws SQLException {
        if (isOnInsertRow()) {
            return;
        }
        this.isInserting = true;

        if (this.rowBuffer == null) {
            this.rowBuffer = new Object[getColumnCount()];
        }
        if (this.m_nullIndicator == null) {
            this.m_nullIndicator = new boolean[getColumnCount()];
        }
        clearRowBuffer();
    }

    public synchronized void moveToCurrentRow() throws SQLException {
        cancelRowInserts();
    }

    public synchronized void updateString(int columnIndex, String x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public synchronized void updateNull(int columnIndex) throws SQLException {
        setRowBufferAt(columnIndex, null);
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        updateObject(columnIndex, new Boolean(x));
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        updateObject(columnIndex, new Integer(x));
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        updateObject(columnIndex, new Integer(x));
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        updateObject(columnIndex, new Integer(x));
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        updateObject(columnIndex, new Long(x));
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        updateObject(columnIndex, new Float(x));
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        updateObject(columnIndex, new Double(x));
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        updateObject(columnIndex, x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        int type = getInternalMetadata().getColumnType(1 + columnIndex);

        if ((x != null) && (length > 0)) {
            if (!isStreamType(type)) {
                byte[] asciiBytes = new byte[length];
                try {
                    int len = x.read(asciiBytes);

                    x.close();

                    String str = new String(asciiBytes, 0, len);

                    updateString(columnIndex, str);
                } catch (IOException e) {
                    DatabaseError.throwSqlException(e);
                }
            } else {
                int[] infoArray = { length, 1 };

                setRowBufferAt(columnIndex, x, infoArray);
            }
        } else {
            setRowBufferAt(columnIndex, null);
        }
    }

    final boolean isStreamType(int type) {
        return (type == 2004) || (type == 2005) || (type == -4) || (type == -1);
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        int type = getInternalMetadata().getColumnType(1 + columnIndex);

        if ((x != null) && (length > 0)) {
            if (!isStreamType(type)) {
                byte[] rawBytes = new byte[length];
                try {
                    int len = x.read(rawBytes);

                    x.close();
                    updateBytes(columnIndex, rawBytes);
                } catch (IOException e) {
                    DatabaseError.throwSqlException(e);
                }
            } else {
                int[] infoArray = { length, 2 };

                setRowBufferAt(columnIndex, x, infoArray);
            }
        } else {
            setRowBufferAt(columnIndex, null);
        }
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        int type = getInternalMetadata().getColumnType(1 + columnIndex);

        if ((x != null) && (length > 0)) {
            if (!isStreamType(type)) {
                char[] chars = new char[length];
                try {
                    int len = x.read(chars);

                    x.close();
                    updateString(columnIndex, new String(chars));
                } catch (IOException e) {
                    DatabaseError.throwSqlException(e);
                }
            } else {
                int[] infoArray = { length };

                setRowBufferAt(columnIndex, x, infoArray);
            }
        } else {
            setRowBufferAt(columnIndex, null);
        }
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        updateObject(columnIndex, x);
    }

    public synchronized void updateObject(int columnIndex, Object x) throws SQLException {
        Datum datum = null;

        if (x != null) {
            if ((x instanceof Datum)) {
                datum = (Datum) x;
            } else {
                OracleResultSetMetaData rsmd = (OracleResultSetMetaData) getInternalMetadata();
                int idx = columnIndex + 1;
                datum = SQLUtil.makeOracleDatum(this.connection, x, rsmd.getColumnType(idx), null,
                                                rsmd.isNCHAR(idx));
            }

        }

        setRowBufferAt(columnIndex, datum);
    }

    public synchronized void updateOracleObject(int columnIndex, Datum x) throws SQLException {
        setRowBufferAt(columnIndex, x);
    }

    public void updateROWID(int columnIndex, ROWID x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateNUMBER(int columnIndex, NUMBER x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateDATE(int columnIndex, DATE x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateINTERVALYM(int columnIndex, INTERVALYM x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateINTERVALDS(int columnIndex, INTERVALDS x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateTIMESTAMP(int columnIndex, TIMESTAMP x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateTIMESTAMPTZ(int columnIndex, TIMESTAMPTZ x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateTIMESTAMPLTZ(int columnIndex, TIMESTAMPLTZ x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateARRAY(int columnIndex, ARRAY x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateSTRUCT(int columnIndex, STRUCT x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateOPAQUE(int columnIndex, OPAQUE x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateREF(int columnIndex, REF x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateCHAR(int columnIndex, CHAR x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateRAW(int columnIndex, RAW x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateBLOB(int columnIndex, BLOB x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateCLOB(int columnIndex, CLOB x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateBFILE(int columnIndex, BFILE x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateBfile(int columnIndex, BFILE x) throws SQLException {
        updateOracleObject(columnIndex, x);
    }

    public void updateCustomDatum(int columnIndex, CustomDatum x) throws SQLException {
        throw new Error("wanna do datum = ((CustomDatum) x).toDatum(m_comm)");
    }

    public void updateORAData(int columnIndex, ORAData x) throws SQLException {
        Datum d = x.toDatum(this.connection);

        updateOracleObject(columnIndex, d);
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        updateREF(columnIndex, (REF) x);
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        updateBLOB(columnIndex, (BLOB) x);
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        updateCLOB(columnIndex, (CLOB) x);
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        updateARRAY(columnIndex, (ARRAY) x);
    }

    int getColumnCount() throws SQLException {
        if (this.columnCount == 0) {
            if ((this.resultSet instanceof OracleResultSetImpl)) {
                if (((OracleResultSetImpl) this.resultSet).statement.accessors != null) {
                    this.columnCount = ((OracleResultSetImpl) this.resultSet).statement.numberOfDefinePositions;
                } else
                    this.columnCount = getInternalMetadata().getColumnCount();
            } else {
                this.columnCount = ((ScrollableResultSet) this.resultSet).getColumnCount();
            }
        }
        return this.columnCount;
    }

    ResultSetMetaData getInternalMetadata() throws SQLException {
        if (this.rsetMetaData == null) {
            this.rsetMetaData = this.resultSet.getMetaData();
        }
        return this.rsetMetaData;
    }

    private void cancelRowChanges() throws SQLException {
        if (this.isInserting) {
            cancelRowInserts();
        }
        if (this.isUpdating)
            cancelRowUpdates();
    }

    boolean isOnInsertRow() {
        return this.isInserting;
    }

    private void cancelRowInserts() {
        if (this.isInserting) {
            this.isInserting = false;

            clearRowBuffer();
        }
    }

    boolean isUpdatingRow() {
        return this.isUpdating;
    }

    private void clearRowBuffer() {
        if (this.rowBuffer != null) {
            for (int i = 0; i < this.rowBuffer.length; i++) {
                this.rowBuffer[i] = null;
            }
        }
        if (this.m_nullIndicator != null) {
            for (int i = 0; i < this.m_nullIndicator.length; i++) {
                this.m_nullIndicator[i] = false;
            }
        }
        if (this.typeInfo != null) {
            for (int i = 0; i < this.typeInfo.length; i++)
                if (this.typeInfo[i] != null)
                    for (int j = 0; j < this.typeInfo[i].length; j++)
                        this.typeInfo[i][j] = 0;
        }
    }

    private void setRowBufferAt(int idx, Datum value) throws SQLException {
        setRowBufferAt(idx, value, (int[]) null);
    }

    private void setRowBufferAt(int idx, Object value, int[] info) throws SQLException {
        if (!this.isInserting) {
            if ((isBeforeFirst()) || (isAfterLast()) || (getRow() == 0))
                DatabaseError.throwSqlException(82);
            else {
                this.isUpdating = true;
            }
        }
        if ((idx < 1) || (idx > getColumnCount() - 1)) {
            DatabaseError.throwSqlException(68, "setRowBufferAt");
        }

        if (this.rowBuffer == null) {
            this.rowBuffer = new Object[getColumnCount()];
        }
        if (this.m_nullIndicator == null) {
            this.m_nullIndicator = new boolean[getColumnCount()];

            for (int i = 0; i < getColumnCount(); i++) {
                this.m_nullIndicator[i] = false;
            }
        }
        if (info != null) {
            if (this.typeInfo == null) {
                this.typeInfo = new int[getColumnCount()][];
            }

            this.typeInfo[idx] = info;
        }

        this.rowBuffer[idx] = value;
        this.m_nullIndicator[idx] = (value == null ? true : false);
    }

    private Datum getRowBufferDatumAt(int idx) throws SQLException {
        if ((idx < 1) || (idx > getColumnCount() - 1)) {
            DatabaseError.throwSqlException(68, "getRowBufferDatumAt");
        }

        Datum datum = null;

        if (this.rowBuffer != null) {
            Object value = this.rowBuffer[idx];

            if (value != null) {
                if ((value instanceof Datum)) {
                    datum = (Datum) value;
                } else {
                    OracleResultSetMetaData rsmd = (OracleResultSetMetaData) getInternalMetadata();
                    int index = idx + 1;
                    datum = SQLUtil.makeOracleDatum(this.connection, value, rsmd
                            .getColumnType(index), null, rsmd.isNCHAR(index));

                    this.rowBuffer[idx] = datum;
                }
            }
        }

        return datum;
    }

    private Object getRowBufferAt(int idx) throws SQLException {
        if ((idx < 1) || (idx > getColumnCount() - 1)) {
            DatabaseError.throwSqlException(68, "getRowBufferDatumAt");
        }

        if (this.rowBuffer != null) {
            return this.rowBuffer[idx];
        }

        return null;
    }

    private boolean isRowBufferUpdatedAt(int idx) {
        if (this.rowBuffer == null) {
            return false;
        }
        return (this.rowBuffer[idx] != null) || (this.m_nullIndicator[idx] != false);
    }

    private void prepareInsertRowStatement() throws SQLException {
        if (this.insertStmt == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.CONFIG, "insertSql: "
                        + ((OracleStatement) this.scrollStmt).sqlObject
                                .getInsertSqlForUpdatableResultSet(this), this);

                OracleLog.recursiveTrace = false;
            }

            this.insertStmt = ((OraclePreparedStatement) this.connection
                    .prepareStatement(((OracleStatement) this.scrollStmt).sqlObject
                            .getInsertSqlForUpdatableResultSet(this)));

            this.insertStmt.setQueryTimeout(((Statement) this.scrollStmt).getQueryTimeout());
        }
    }

    private void prepareInsertRowBinds() throws SQLException {
        int idxBound = 1;

        idxBound = prepareSubqueryBinds(this.insertStmt, idxBound);

        OracleResultSetMetaData rsmd = (OracleResultSetMetaData) getInternalMetadata();

        for (int i = 1; i < getColumnCount(); i++) {
            Object value = getRowBufferAt(i);

            if (value != null) {
                if ((value instanceof Reader)) {
                    this.insertStmt.setCharacterStream(idxBound + i - 1, (Reader) value,
                                                       this.typeInfo[i][0]);
                } else if ((value instanceof InputStream)) {
                    if (this.typeInfo[i][1] == 2) {
                        this.insertStmt.setBinaryStream(idxBound + i - 1, (InputStream) value,
                                                        this.typeInfo[i][0]);
                    } else if (this.typeInfo[i][1] == 1) {
                        this.insertStmt.setAsciiStream(idxBound + i - 1, (InputStream) value,
                                                       this.typeInfo[i][0]);
                    }

                } else {
                    Datum datum = getRowBufferDatumAt(i);

                    if (rsmd.isNCHAR(i + 1))
                        this.insertStmt.setFormOfUse(idxBound + i - 1, (short) 2);
                    this.insertStmt.setOracleObject(idxBound + i - 1, datum);
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.CONFIG, "bind insert stmt to null at "
                            + (idxBound + i - 1) + " with type"
                            + getInternalMetadata().getColumnType(i + 1), this);

                    OracleLog.recursiveTrace = false;
                }

                int colType = getInternalMetadata().getColumnType(i + 1);

                if ((colType == 2006) || (colType == 2002) || (colType == 2008)
                        || (colType == 2007) || (colType == 2003)) {
                    this.insertStmt.setNull(idxBound + i - 1, colType, getInternalMetadata()
                            .getColumnTypeName(i + 1));
                } else {
                    this.insertStmt.setNull(idxBound + i - 1, colType);
                }
            }
        }
    }

    private void executeInsertRow() throws SQLException {
        if (this.insertStmt.executeUpdate() != 1)
            DatabaseError.throwSqlException(85);
    }

    private int getNumColumnsChanged() throws SQLException {
        int _numColsChanged = 0;

        if (this.indexColsChanged == null) {
            this.indexColsChanged = new int[getColumnCount()];
        }
        if (this.rowBuffer != null) {
            for (int i = 1; i < getColumnCount(); i++) {
                if ((this.rowBuffer[i] == null)
                        && ((this.rowBuffer[i] != null) || (this.m_nullIndicator[i] == false))) {
                    continue;
                }
                this.indexColsChanged[(_numColsChanged++)] = i;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "numColsChanged = " + _numColsChanged, this);

            OracleLog.recursiveTrace = false;
        }

        return _numColsChanged;
    }

    private void prepareUpdateRowStatement(int _numColsChanged) throws SQLException {
        if (this.updateStmt != null) {
            this.updateStmt.close();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.CONFIG, "updateSql = "
                    + ((OracleStatement) this.scrollStmt).sqlObject
                            .getUpdateSqlForUpdatableResultSet(this, _numColsChanged,
                                                               this.rowBuffer,
                                                               this.indexColsChanged), this);

            OracleLog.recursiveTrace = false;
        }

        this.updateStmt = ((OraclePreparedStatement) this.connection
                .prepareStatement(((OracleStatement) this.scrollStmt).sqlObject
                        .getUpdateSqlForUpdatableResultSet(this, _numColsChanged, this.rowBuffer,
                                                           this.indexColsChanged)));

        this.updateStmt.setQueryTimeout(((Statement) this.scrollStmt).getQueryTimeout());
    }

    private void prepareUpdateRowBinds(int _numColsChanged) throws SQLException {
        int idxBound = 1;

        idxBound = prepareSubqueryBinds(this.updateStmt, idxBound);

        OracleResultSetMetaData rsmd = (OracleResultSetMetaData) getInternalMetadata();

        for (int i = 0; i < _numColsChanged; i++) {
            int idx = this.indexColsChanged[i];
            Object value = getRowBufferAt(idx);

            if (value != null) {
                if ((value instanceof Reader)) {
                    this.updateStmt.setCharacterStream(idxBound++, (Reader) value,
                                                       this.typeInfo[idx][0]);
                } else if ((value instanceof InputStream)) {
                    if (this.typeInfo[idx][1] == 2) {
                        this.updateStmt.setBinaryStream(idxBound++, (InputStream) value,
                                                        this.typeInfo[idx][0]);
                    } else if (this.typeInfo[idx][1] == 1) {
                        this.updateStmt.setAsciiStream(idxBound++, (InputStream) value,
                                                       this.typeInfo[idx][0]);
                    }
                } else {
                    Datum datum = getRowBufferDatumAt(idx);

                    if (rsmd.isNCHAR(idx + 1))
                        this.updateStmt.setFormOfUse(idxBound, (short) 2);
                    this.updateStmt.setOracleObject(idxBound++, datum);
                }

            } else {
                int colType = getInternalMetadata().getColumnType(idx + 1);

                if ((colType == 2006) || (colType == 2002) || (colType == 2008)
                        || (colType == 2007) || (colType == 2003)) {
                    this.updateStmt.setNull(idxBound++, colType, getInternalMetadata()
                            .getColumnTypeName(idx + 1));
                } else {
                    this.updateStmt.setNull(idxBound++, colType);
                }
            }

        }

        prepareCompareSelfBinds(this.updateStmt, idxBound);
    }

    private void executeUpdateRow() throws SQLException {
        try {
            if (this.updateStmt.executeUpdate() == 0) {
                DatabaseError.throwSqlException(85);
            }

            if (this.isCachedRset) {
                if (this.autoRefetch) {
                    ((ScrollableResultSet) this.resultSet).refreshRowsInCache(getRow(), 1, 1000);

                    cancelRowUpdates();
                } else {
                    if (this.rowBuffer != null) {
                        for (int i = 1; i < getColumnCount(); i++) {
                            if ((this.rowBuffer[i] == null)
                                    && ((this.rowBuffer[i] != null) || (this.m_nullIndicator[i] == false))) {
                                continue;
                            }
                            ((ScrollableResultSet) this.resultSet)
                                    .setCurrentRowValueAt(this, i + 1, this.rowBuffer[i]);
                        }

                    }

                    cancelRowUpdates();
                }

            }

        } finally {
            if (this.updateStmt != null) {
                this.updateStmt.close();

                this.updateStmt = null;
            }
        }
    }

    private void prepareDeleteRowStatement() throws SQLException {
        if (this.deleteStmt == null) {
            StringBuffer deleteSql = new StringBuffer();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.CONFIG, "deleteSql = "
                        + ((OracleStatement) this.scrollStmt).sqlObject
                                .getDeleteSqlForUpdatableResultSet(this), this);

                OracleLog.recursiveTrace = false;
            }

            this.deleteStmt = ((OraclePreparedStatement) this.connection
                    .prepareStatement(((OracleStatement) this.scrollStmt).sqlObject
                            .getDeleteSqlForUpdatableResultSet(this)));

            this.deleteStmt.setQueryTimeout(((Statement) this.scrollStmt).getQueryTimeout());
        }
    }

    private void prepareDeleteRowBinds() throws SQLException {
        int idxBound = 1;

        idxBound = prepareSubqueryBinds(this.deleteStmt, idxBound);

        prepareCompareSelfBinds(this.deleteStmt, idxBound);
    }

    private void executeDeleteRow() throws SQLException {
        if (this.deleteStmt.executeUpdate() == 0) {
            DatabaseError.throwSqlException(85);
        }

        if (this.isCachedRset)
            ((ScrollableResultSet) this.resultSet).removeRowInCache(getRow());
    }

    private int prepareCompareSelfBinds(OraclePreparedStatement pstmt, int idxBound)
            throws SQLException {
        Datum datum = this.resultSet.getOracleObject(1);

        pstmt.setOracleObject(idxBound, this.resultSet.getOracleObject(1));

        return idxBound + 1;
    }

    private int prepareSubqueryBinds(OraclePreparedStatement pstmt, int idxBound)
            throws SQLException {
        int _numSubQueryBinds = this.scrollStmt.copyBinds(pstmt, idxBound - 1);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINER, "numSubQueryBinds = " + _numSubQueryBinds, this);

            OracleLog.recursiveTrace = false;
        }

        return _numSubQueryBinds + 1;
    }

    private void setIsNull(int status) {
        this.wasNull = status;
    }

    private void setIsNull(boolean isNull) {
        if (isNull)
            this.wasNull = 1;
        else
            this.wasNull = 2;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.UpdatableResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.UpdatableResultSet JD-Core Version: 0.6.0
 */