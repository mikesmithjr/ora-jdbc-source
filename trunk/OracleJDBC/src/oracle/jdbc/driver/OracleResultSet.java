package oracle.jdbc.driver;

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
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
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

public abstract class OracleResultSet implements oracle.jdbc.internal.OracleResultSet {
    static final boolean DEBUG = false;
    public static final int FETCH_FORWARD = 1000;
    public static final int FETCH_REVERSE = 1001;
    public static final int FETCH_UNKNOWN = 1002;
    public static final int TYPE_FORWARD_ONLY = 1003;
    public static final int TYPE_SCROLL_INSENSITIVE = 1004;
    public static final int TYPE_SCROLL_SENSITIVE = 1005;
    public static final int CONCUR_READ_ONLY = 1007;
    public static final int CONCUR_UPDATABLE = 1008;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    int getFirstUserColumnIndex() throws SQLException {
        return 0;
    }

    public abstract void closeStatementOnClose() throws SQLException;

    public abstract ResultSet getCursor(int paramInt) throws SQLException;

    public abstract Datum getOracleObject(int paramInt) throws SQLException;

    public abstract ROWID getROWID(int paramInt) throws SQLException;

    public abstract NUMBER getNUMBER(int paramInt) throws SQLException;

    public abstract DATE getDATE(int paramInt) throws SQLException;

    public abstract ARRAY getARRAY(int paramInt) throws SQLException;

    public abstract STRUCT getSTRUCT(int paramInt) throws SQLException;

    public abstract OPAQUE getOPAQUE(int paramInt) throws SQLException;

    public abstract REF getREF(int paramInt) throws SQLException;

    public abstract CHAR getCHAR(int paramInt) throws SQLException;

    public abstract RAW getRAW(int paramInt) throws SQLException;

    public abstract BLOB getBLOB(int paramInt) throws SQLException;

    public abstract CLOB getCLOB(int paramInt) throws SQLException;

    public abstract BFILE getBFILE(int paramInt) throws SQLException;

    public abstract BFILE getBfile(int paramInt) throws SQLException;

    /** @deprecated */
    public abstract CustomDatum getCustomDatum(int paramInt,
            CustomDatumFactory paramCustomDatumFactory) throws SQLException;

    public abstract ORAData getORAData(int paramInt, ORADataFactory paramORADataFactory)
            throws SQLException;

    public ResultSet getCursor(String columnName) throws SQLException {
        return getCursor(findColumn(columnName));
    }

    public ROWID getROWID(String columnName) throws SQLException {
        return getROWID(findColumn(columnName));
    }

    public NUMBER getNUMBER(String columnName) throws SQLException {
        return getNUMBER(findColumn(columnName));
    }

    public DATE getDATE(String columnName) throws SQLException {
        return getDATE(findColumn(columnName));
    }

    public Datum getOracleObject(String columnName) throws SQLException {
        return getOracleObject(findColumn(columnName));
    }

    public ARRAY getARRAY(String columnName) throws SQLException {
        return getARRAY(findColumn(columnName));
    }

    public STRUCT getSTRUCT(String columnName) throws SQLException {
        return getSTRUCT(findColumn(columnName));
    }

    public OPAQUE getOPAQUE(String columnName) throws SQLException {
        return getOPAQUE(findColumn(columnName));
    }

    public REF getREF(String columnName) throws SQLException {
        return getREF(findColumn(columnName));
    }

    public CHAR getCHAR(String columnName) throws SQLException {
        return getCHAR(findColumn(columnName));
    }

    public RAW getRAW(String columnName) throws SQLException {
        return getRAW(findColumn(columnName));
    }

    public BLOB getBLOB(String columnName) throws SQLException {
        return getBLOB(findColumn(columnName));
    }

    public CLOB getCLOB(String columnName) throws SQLException {
        return getCLOB(findColumn(columnName));
    }

    public BFILE getBFILE(String columnName) throws SQLException {
        return getBFILE(findColumn(columnName));
    }

    public BFILE getBfile(String columnName) throws SQLException {
        return getBfile(findColumn(columnName));
    }

    /** @deprecated */
    public CustomDatum getCustomDatum(String columnName, CustomDatumFactory factory)
            throws SQLException {
        return getCustomDatum(findColumn(columnName), factory);
    }

    public ORAData getORAData(String columnName, ORADataFactory factory) throws SQLException {
        return getORAData(findColumn(columnName), factory);
    }

    public abstract void updateOracleObject(int paramInt, Datum paramDatum) throws SQLException;

    public abstract void updateROWID(int paramInt, ROWID paramROWID) throws SQLException;

    public abstract void updateNUMBER(int paramInt, NUMBER paramNUMBER) throws SQLException;

    public abstract void updateDATE(int paramInt, DATE paramDATE) throws SQLException;

    public abstract void updateINTERVALYM(int paramInt, INTERVALYM paramINTERVALYM)
            throws SQLException;

    public abstract void updateINTERVALDS(int paramInt, INTERVALDS paramINTERVALDS)
            throws SQLException;

    public abstract void updateTIMESTAMP(int paramInt, TIMESTAMP paramTIMESTAMP)
            throws SQLException;

    public abstract void updateTIMESTAMPTZ(int paramInt, TIMESTAMPTZ paramTIMESTAMPTZ)
            throws SQLException;

    public abstract void updateTIMESTAMPLTZ(int paramInt, TIMESTAMPLTZ paramTIMESTAMPLTZ)
            throws SQLException;

    public abstract void updateARRAY(int paramInt, ARRAY paramARRAY) throws SQLException;

    public abstract void updateSTRUCT(int paramInt, STRUCT paramSTRUCT) throws SQLException;

    public abstract void updateOPAQUE(int paramInt, OPAQUE paramOPAQUE) throws SQLException;

    public abstract void updateREF(int paramInt, REF paramREF) throws SQLException;

    public abstract void updateCHAR(int paramInt, CHAR paramCHAR) throws SQLException;

    public abstract void updateRAW(int paramInt, RAW paramRAW) throws SQLException;

    public abstract void updateBLOB(int paramInt, BLOB paramBLOB) throws SQLException;

    public abstract void updateCLOB(int paramInt, CLOB paramCLOB) throws SQLException;

    public abstract void updateBFILE(int paramInt, BFILE paramBFILE) throws SQLException;

    public abstract void updateBfile(int paramInt, BFILE paramBFILE) throws SQLException;

    /** @deprecated */
    public abstract void updateCustomDatum(int paramInt, CustomDatum paramCustomDatum)
            throws SQLException;

    public abstract void updateORAData(int paramInt, ORAData paramORAData) throws SQLException;

    public abstract void updateRef(int paramInt, Ref paramRef) throws SQLException;

    public abstract void updateBlob(int paramInt, Blob paramBlob) throws SQLException;

    public abstract void updateClob(int paramInt, Clob paramClob) throws SQLException;

    public abstract void updateArray(int paramInt, Array paramArray) throws SQLException;

    public void updateROWID(String columnName, ROWID x) throws SQLException {
        updateROWID(findColumn(columnName), x);
    }

    public void updateNUMBER(String columnName, NUMBER x) throws SQLException {
        updateNUMBER(findColumn(columnName), x);
    }

    public void updateDATE(String columnName, DATE x) throws SQLException {
        updateDATE(findColumn(columnName), x);
    }

    public void updateOracleObject(String columnName, Datum x) throws SQLException {
        updateOracleObject(findColumn(columnName), x);
    }

    public void updateARRAY(String columnName, ARRAY x) throws SQLException {
        updateARRAY(findColumn(columnName), x);
    }

    public void updateSTRUCT(String columnName, STRUCT x) throws SQLException {
        updateSTRUCT(findColumn(columnName), x);
    }

    public void updateOPAQUE(String columnName, OPAQUE x) throws SQLException {
        updateOPAQUE(findColumn(columnName), x);
    }

    public void updateREF(String columnName, REF x) throws SQLException {
        updateREF(findColumn(columnName), x);
    }

    public void updateCHAR(String columnName, CHAR x) throws SQLException {
        updateCHAR(findColumn(columnName), x);
    }

    public void updateRAW(String columnName, RAW x) throws SQLException {
        updateRAW(findColumn(columnName), x);
    }

    public void updateBLOB(String columnName, BLOB x) throws SQLException {
        updateBLOB(findColumn(columnName), x);
    }

    public void updateCLOB(String columnName, CLOB x) throws SQLException {
        updateCLOB(findColumn(columnName), x);
    }

    public void updateBFILE(String columnName, BFILE x) throws SQLException {
        updateBFILE(findColumn(columnName), x);
    }

    public void updateBfile(String columnName, BFILE x) throws SQLException {
        updateBfile(findColumn(columnName), x);
    }

    /** @deprecated */
    public void updateCustomDatum(String columnName, CustomDatum x) throws SQLException {
        updateCustomDatum(findColumn(columnName), x);
    }

    public void updateORAData(String columnName, ORAData x) throws SQLException {
        updateORAData(findColumn(columnName), x);
    }

    public void updateRef(String columnName, Ref x) throws SQLException {
        updateRef(findColumn(columnName), x);
    }

    public void updateBlob(String columnName, Blob x) throws SQLException {
        updateBlob(findColumn(columnName), x);
    }

    public void updateClob(String columnName, Clob x) throws SQLException {
        updateClob(findColumn(columnName), x);
    }

    public void updateArray(String columnName, Array x) throws SQLException {
        updateArray(findColumn(columnName), x);
    }

    public abstract void setAutoRefetch(boolean paramBoolean) throws SQLException;

    public abstract boolean getAutoRefetch() throws SQLException;

    public abstract SQLWarning getWarnings() throws SQLException;

    public abstract void clearWarnings() throws SQLException;

    public abstract String getCursorName() throws SQLException;

    public abstract ResultSetMetaData getMetaData() throws SQLException;

    public abstract int findColumn(String paramString) throws SQLException;

    public abstract boolean next() throws SQLException;

    public abstract void close() throws SQLException;

    public abstract boolean wasNull() throws SQLException;

    public abstract Object getObject(int paramInt) throws SQLException;

    public abstract String getString(int paramInt) throws SQLException;

    public abstract boolean getBoolean(int paramInt) throws SQLException;

    public abstract byte getByte(int paramInt) throws SQLException;

    public abstract short getShort(int paramInt) throws SQLException;

    public abstract int getInt(int paramInt) throws SQLException;

    public abstract long getLong(int paramInt) throws SQLException;

    public abstract float getFloat(int paramInt) throws SQLException;

    public abstract double getDouble(int paramInt) throws SQLException;

    public abstract BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException;

    public abstract byte[] getBytes(int paramInt) throws SQLException;

    public abstract Date getDate(int paramInt) throws SQLException;

    public abstract Time getTime(int paramInt) throws SQLException;

    public abstract Timestamp getTimestamp(int paramInt) throws SQLException;

    public abstract InputStream getAsciiStream(int paramInt) throws SQLException;

    public abstract InputStream getUnicodeStream(int paramInt) throws SQLException;

    public abstract InputStream getBinaryStream(int paramInt) throws SQLException;

    public Object getObject(String columnName) throws SQLException {
        return getObject(findColumn(columnName));
    }

    public String getString(String columnName) throws SQLException {
        return getString(findColumn(columnName));
    }

    public boolean getBoolean(String columnName) throws SQLException {
        return getBoolean(findColumn(columnName));
    }

    public byte getByte(String columnName) throws SQLException {
        return getByte(findColumn(columnName));
    }

    public short getShort(String columnName) throws SQLException {
        return getShort(findColumn(columnName));
    }

    public int getInt(String columnName) throws SQLException {
        return getInt(findColumn(columnName));
    }

    public long getLong(String columnName) throws SQLException {
        return getLong(findColumn(columnName));
    }

    public float getFloat(String columnName) throws SQLException {
        return getFloat(findColumn(columnName));
    }

    public double getDouble(String columnName) throws SQLException {
        return getDouble(findColumn(columnName));
    }

    public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    public byte[] getBytes(String columnName) throws SQLException {
        return getBytes(findColumn(columnName));
    }

    public Date getDate(String columnName) throws SQLException {
        return getDate(findColumn(columnName));
    }

    public Time getTime(String columnName) throws SQLException {
        return getTime(findColumn(columnName));
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        return getTimestamp(findColumn(columnName));
    }

    public InputStream getAsciiStream(String columnName) throws SQLException {
        return getAsciiStream(findColumn(columnName));
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException {
        return getUnicodeStream(findColumn(columnName));
    }

    public InputStream getBinaryStream(String columnName) throws SQLException {
        return getBinaryStream(findColumn(columnName));
    }

    public abstract Object getObject(int paramInt, Map paramMap) throws SQLException;

    public abstract Ref getRef(int paramInt) throws SQLException;

    public abstract Blob getBlob(int paramInt) throws SQLException;

    public abstract Clob getClob(int paramInt) throws SQLException;

    public abstract Array getArray(int paramInt) throws SQLException;

    public abstract Reader getCharacterStream(int paramInt) throws SQLException;

    public abstract BigDecimal getBigDecimal(int paramInt) throws SQLException;

    public abstract Date getDate(int paramInt, Calendar paramCalendar) throws SQLException;

    public abstract Time getTime(int paramInt, Calendar paramCalendar) throws SQLException;

    public abstract Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
            throws SQLException;

    public TIMESTAMP getTIMESTAMP(int columnIndex) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public TIMESTAMP getTIMESTAMP(String columnName) throws SQLException {
        return getTIMESTAMP(findColumn(columnName));
    }

    public TIMESTAMPTZ getTIMESTAMPTZ(int columnIndex) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public TIMESTAMPTZ getTIMESTAMPTZ(String columnName) throws SQLException {
        return getTIMESTAMPTZ(findColumn(columnName));
    }

    public TIMESTAMPLTZ getTIMESTAMPLTZ(int columnIndex) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public TIMESTAMPLTZ getTIMESTAMPLTZ(String columnName) throws SQLException {
        return getTIMESTAMPLTZ(findColumn(columnName));
    }

    public INTERVALYM getINTERVALYM(int columnIndex) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public INTERVALYM getINTERVALYM(String columnName) throws SQLException {
        return getINTERVALYM(findColumn(columnName));
    }

    public INTERVALDS getINTERVALDS(int columnIndex) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public INTERVALDS getINTERVALDS(String columnName) throws SQLException {
        return getINTERVALDS(findColumn(columnName));
    }

    public Object getObject(String columnName, Map map) throws SQLException {
        return getObject(findColumn(columnName), map);
    }

    public Ref getRef(String columnName) throws SQLException {
        return getRef(findColumn(columnName));
    }

    public Blob getBlob(String columnName) throws SQLException {
        return getBlob(findColumn(columnName));
    }

    public Clob getClob(String columnName) throws SQLException {
        return getClob(findColumn(columnName));
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        return getCharacterStream(findColumn(columnName));
    }

    public Array getArray(String columnName) throws SQLException {
        return getARRAY(findColumn(columnName));
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return getBigDecimal(findColumn(columnName));
    }

    public Date getDate(String columnName, Calendar cal) throws SQLException {
        return getDate(findColumn(columnName), cal);
    }

    public Time getTime(String columnName, Calendar cal) throws SQLException {
        return getTime(findColumn(columnName), cal);
    }

    public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
        return getTimestamp(findColumn(columnName), cal);
    }

    public abstract boolean isBeforeFirst() throws SQLException;

    public abstract boolean isAfterLast() throws SQLException;

    public abstract boolean isFirst() throws SQLException;

    public abstract boolean isLast() throws SQLException;

    public abstract void beforeFirst() throws SQLException;

    public abstract void afterLast() throws SQLException;

    public abstract boolean first() throws SQLException;

    public abstract boolean last() throws SQLException;

    public abstract int getRow() throws SQLException;

    public abstract boolean absolute(int paramInt) throws SQLException;

    public abstract boolean relative(int paramInt) throws SQLException;

    public abstract boolean previous() throws SQLException;

    public abstract void setFetchDirection(int paramInt) throws SQLException;

    public abstract int getFetchDirection() throws SQLException;

    public abstract void setFetchSize(int paramInt) throws SQLException;

    public abstract int getFetchSize() throws SQLException;

    public abstract int getType() throws SQLException;

    public abstract int getConcurrency() throws SQLException;

    public abstract void insertRow() throws SQLException;

    public abstract void updateRow() throws SQLException;

    public abstract void deleteRow() throws SQLException;

    public abstract void refreshRow() throws SQLException;

    public abstract void moveToInsertRow() throws SQLException;

    public abstract void cancelRowUpdates() throws SQLException;

    public abstract void moveToCurrentRow() throws SQLException;

    public abstract Statement getStatement() throws SQLException;

    public abstract boolean rowUpdated() throws SQLException;

    public abstract boolean rowInserted() throws SQLException;

    public abstract boolean rowDeleted() throws SQLException;

    public abstract void updateNull(int paramInt) throws SQLException;

    public abstract void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException;

    public abstract void updateByte(int paramInt, byte paramByte) throws SQLException;

    public abstract void updateShort(int paramInt, short paramShort) throws SQLException;

    public abstract void updateInt(int paramInt1, int paramInt2) throws SQLException;

    public abstract void updateLong(int paramInt, long paramLong) throws SQLException;

    public abstract void updateFloat(int paramInt, float paramFloat) throws SQLException;

    public abstract void updateDouble(int paramInt, double paramDouble) throws SQLException;

    public abstract void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
            throws SQLException;

    public abstract void updateString(int paramInt, String paramString) throws SQLException;

    public abstract void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException;

    public abstract void updateDate(int paramInt, Date paramDate) throws SQLException;

    public abstract void updateTime(int paramInt, Time paramTime) throws SQLException;

    public abstract void updateTimestamp(int paramInt, Timestamp paramTimestamp)
            throws SQLException;

    public abstract void updateAsciiStream(int paramInt1, InputStream paramInputStream,
            int paramInt2) throws SQLException;

    public abstract void updateBinaryStream(int paramInt1, InputStream paramInputStream,
            int paramInt2) throws SQLException;

    public abstract void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
            throws SQLException;

    public abstract void updateObject(int paramInt1, Object paramObject, int paramInt2)
            throws SQLException;

    public abstract void updateObject(int paramInt, Object paramObject) throws SQLException;

    public void updateNull(String columnName) throws SQLException {
        updateNull(findColumn(columnName));
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException {
        updateBoolean(findColumn(columnName), x);
    }

    public void updateByte(String columnName, byte x) throws SQLException {
        updateByte(findColumn(columnName), x);
    }

    public void updateShort(String columnName, short x) throws SQLException {
        updateShort(findColumn(columnName), x);
    }

    public void updateInt(String columnName, int x) throws SQLException {
        updateInt(findColumn(columnName), x);
    }

    public void updateLong(String columnName, long x) throws SQLException {
        updateLong(findColumn(columnName), x);
    }

    public void updateFloat(String columnName, float x) throws SQLException {
        updateFloat(findColumn(columnName), x);
    }

    public void updateDouble(String columnName, double x) throws SQLException {
        updateDouble(findColumn(columnName), x);
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
        updateBigDecimal(findColumn(columnName), x);
    }

    public void updateString(String columnName, String x) throws SQLException {
        updateString(findColumn(columnName), x);
    }

    public void updateBytes(String columnName, byte[] x) throws SQLException {
        updateBytes(findColumn(columnName), x);
    }

    public void updateDate(String columnName, Date x) throws SQLException {
        updateDate(findColumn(columnName), x);
    }

    public void updateTime(String columnName, Time x) throws SQLException {
        updateTime(findColumn(columnName), x);
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
        updateTimestamp(findColumn(columnName), x);
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
        updateAsciiStream(findColumn(columnName), x, length);
    }

    public void updateBinaryStream(String columnName, InputStream x, int length)
            throws SQLException {
        updateBinaryStream(findColumn(columnName), x, length);
    }

    public void updateCharacterStream(String columnName, Reader reader, int length)
            throws SQLException {
        updateCharacterStream(findColumn(columnName), reader, length);
    }

    public void updateObject(String columnName, Object x, int scale) throws SQLException {
        updateObject(findColumn(columnName), x, scale);
    }

    public void updateObject(String columnName, Object x) throws SQLException {
        updateObject(findColumn(columnName), x);
    }

    public abstract URL getURL(int paramInt) throws SQLException;

    public URL getURL(String columnName) throws SQLException {
        return getURL(findColumn(columnName));
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleResultSet JD-Core Version: 0.6.0
 */