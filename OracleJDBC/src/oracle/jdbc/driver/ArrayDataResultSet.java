package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
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
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;

public class ArrayDataResultSet extends BaseResultSet {
    Datum[] data;
    Map map;
    private int currentIndex;
    private int lastIndex;
    boolean closed;
    PhysicalConnection connection;
    private Boolean wasNull;
    private static Boolean BOOLEAN_TRUE = new Boolean(true);
    private static Boolean BOOLEAN_FALSE = new Boolean(false);
    private int fetchSize;
    ARRAY array;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public ArrayDataResultSet(OracleConnection conn, Datum[] data, Map map) throws SQLException {
        this.connection = ((PhysicalConnection) conn);
        this.data = data;
        this.map = map;
        this.currentIndex = 0;
        this.lastIndex = (this.data == null ? 0 : this.data.length);
        this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
    }

    public ArrayDataResultSet(OracleConnection conn, Datum[] data, long index, int count, Map map)
            throws SQLException {
        this.connection = ((PhysicalConnection) conn);
        this.data = data;
        this.map = map;
        this.currentIndex = ((int) index - 1);

        int length = this.data == null ? 0 : this.data.length;

        this.lastIndex = (this.currentIndex + Math.min(length - this.currentIndex, count));

        this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
    }

    public ArrayDataResultSet(OracleConnection conn, ARRAY array, long index, int count, Map map)
            throws SQLException {
        this.connection = ((PhysicalConnection) conn);
        this.array = array;
        this.map = map;
        this.currentIndex = ((int) index - 1);

        int length = this.array == null ? 0 : array.length();

        this.lastIndex = (this.currentIndex + (count == -1 ? length - this.currentIndex : Math
                .min(length - this.currentIndex, count)));

        this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
    }

    public boolean next() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.next()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            DatabaseError.throwSqlException(10, "next");
        }

        this.currentIndex += 1;

        return this.currentIndex <= this.lastIndex;
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.close()", this);

            OracleLog.recursiveTrace = false;
        }

        this.closed = true;
    }

    public synchronized boolean wasNull() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.wasNull()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.wasNull == null) {
            DatabaseError.throwSqlException(24, null);
        }
        return this.wasNull.booleanValue();
    }

    public synchronized String getString(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getString(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.stringValue();
        }
        return null;
    }

    public synchronized ResultSet getCursor(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getCursor(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(4, "getCursor");

        return null;
    }

    public synchronized Datum getOracleObject(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ArrayDataResultSet.getOracleObject(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.currentIndex <= 0) {
            DatabaseError.throwSqlException(14, null);
        }

        if (columnIndex == 1) {
            this.wasNull = BOOLEAN_FALSE;

            return new NUMBER(this.currentIndex);
        }
        if (columnIndex == 2) {
            if (this.data != null) {
                this.wasNull = (this.data[(this.currentIndex - 1)] == null ? BOOLEAN_TRUE
                        : BOOLEAN_FALSE);

                return this.data[(this.currentIndex - 1)];
            }
            if (this.array != null) {
                Datum[] darray = this.array.getOracleArray(this.currentIndex, 1);

                if ((darray != null) && (darray.length >= 1)) {
                    this.wasNull = (darray[0] == null ? BOOLEAN_TRUE : BOOLEAN_FALSE);

                    return darray[0];
                }
            }

            DatabaseError.throwSqlException(1, "Out of sync");
        }

        DatabaseError.throwSqlException(3, null);

        return null;
    }

    public synchronized ROWID getROWID(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getROWID(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getNUMBER(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getDATE(columnIndex="
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

    public synchronized ARRAY getARRAY(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getARRAY(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getSTRUCT(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getOPAQUE(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getREF(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getCHAR(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getRAW(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBLOB(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getCLOB(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBFILE(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBfile(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getBFILE(columnIndex);
    }

    public synchronized boolean getBoolean(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBoolean(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getByte(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getShort(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getInt(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getLong(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getFloat(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getDouble(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBigDecimal(columnIndex="
                    + columnIndex + ", scale=" + scale + ")", this);

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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBytes(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getDate(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getTime(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getTimestamp(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getAsciiStream(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            datum.asciiStreamValue();
        }

        return null;
    }

    public synchronized InputStream getUnicodeStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ArrayDataResultSet.getUnicodeStream(columnIndex="
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
                                       "ArrayDataResultSet.getBinaryStream(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getObject(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getObject(columnIndex, this.map);
    }

    /** @deprecated */
    public synchronized CustomDatum getCustomDatum(int columnIndex, CustomDatumFactory factory)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getCustomDatum(columnIndex="
                    + columnIndex + ", factory=" + factory + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum d = getOracleObject(columnIndex);

        return factory.create(d, 0);
    }

    public synchronized ORAData getORAData(int columnIndex, ORADataFactory factory)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getORAData(columnIndex="
                    + columnIndex + ", factory=" + factory + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum d = getOracleObject(columnIndex);

        return factory.create(d, 0);
    }

    public synchronized ResultSetMetaData getMetaData() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getMetaData()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            DatabaseError.throwSqlException(10, "getMetaData");
        }

        DatabaseError.throwSqlException(23, "getMetaData");

        return null;
    }

    public synchronized int findColumn(String columnName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.findColumn(columnName="
                    + columnName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (columnName.equalsIgnoreCase("index"))
            return 1;
        if (columnName.equalsIgnoreCase("value")) {
            return 2;
        }
        DatabaseError.throwSqlException(6, "get_column_index");

        return 0;
    }

    public synchronized Statement getStatement() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getStatement()", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public synchronized Object getObject(int columnIndex, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getObject(columnIndex="
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
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getRef(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getREF(columnIndex);
    }

    public synchronized Blob getBlob(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBlob(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getBLOB(columnIndex);
    }

    public synchronized Clob getClob(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getClob(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getCLOB(columnIndex);
    }

    public synchronized Array getArray(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getArray(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getARRAY(columnIndex);
    }

    public synchronized Reader getCharacterStream(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "ArrayDataResultSet.getCharacterStream(columnIndex="
                                               + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.characterStreamValue();
        }

        return null;
    }

    public synchronized BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getBigDecimal(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            return datum.bigDecimalValue();
        }

        return null;
    }

    public synchronized Date getDate(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getDate(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            DATE dateValue = null;

            if ((datum instanceof DATE))
                dateValue = (DATE) datum;
            else {
                dateValue = new DATE(datum.stringValue());
            }
            if (dateValue != null) {
                return dateValue.dateValue(cal);
            }
        }
        return null;
    }

    public synchronized Time getTime(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getTime(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            DATE dateValue = null;

            if ((datum instanceof DATE))
                dateValue = (DATE) datum;
            else {
                dateValue = new DATE(datum.stringValue());
            }
            if (dateValue != null) {
                return dateValue.timeValue(cal);
            }
        }
        return null;
    }

    public synchronized Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getTimestamp(columnIndex="
                    + columnIndex + ", cal=" + cal + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = getOracleObject(columnIndex);

        if (datum != null) {
            DATE dateValue = null;

            if ((datum instanceof DATE))
                dateValue = (DATE) datum;
            else {
                dateValue = new DATE(datum.stringValue());
            }
            if (dateValue != null) {
                return dateValue.timestampValue(cal);
            }
        }
        return null;
    }

    public synchronized URL getURL(int columnIndex) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ScrollableResultSet.getURL(columnIndex="
                    + columnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        throw new SQLException("Conversion to java.net.URL not supported.");
    }

    public boolean isBeforeFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.isBeforeFirst(): return: "
                    + (this.currentIndex < 1), this);

            OracleLog.recursiveTrace = false;
        }

        return this.currentIndex < 1;
    }

    public boolean isAfterLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.isAfterLast(): return: "
                    + (this.currentIndex > this.lastIndex), this);

            OracleLog.recursiveTrace = false;
        }

        return this.currentIndex > this.lastIndex;
    }

    public boolean isFirst() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.isFirst(): return: "
                    + (this.currentIndex == 1), this);

            OracleLog.recursiveTrace = false;
        }

        return this.currentIndex == 1;
    }

    public boolean isLast() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.isLast(): "
                    + (this.currentIndex == this.lastIndex), this);

            OracleLog.recursiveTrace = false;
        }

        return this.currentIndex == this.lastIndex;
    }

    public int getRow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getRow(): return: "
                    + this.currentIndex, this);

            OracleLog.recursiveTrace = false;
        }

        return this.currentIndex;
    }

    public void setFetchSize(int rows) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.setFetchSize(rows=" + rows
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (rows < 0)
            DatabaseError.throwSqlException(68);
        else if (rows == 0)
            this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
        else
            this.fetchSize = rows;
    }

    public int getFetchSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "ArrayDataResultSet.getFetchSize(): "
                    + this.fetchSize, this);

            OracleLog.recursiveTrace = false;
        }

        return this.fetchSize;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.ArrayDataResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ArrayDataResultSet JD-Core Version: 0.6.0
 */