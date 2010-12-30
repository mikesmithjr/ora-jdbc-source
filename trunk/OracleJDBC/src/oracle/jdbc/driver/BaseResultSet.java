package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

abstract class BaseResultSet extends OracleResultSet {
    SQLWarning sqlWarning = null;
    boolean autoRefetch = true;

    boolean close_statement_on_close = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public synchronized String getCursorName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.getCursorName()", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(23, "getCursorName");

        return null;
    }

    public void closeStatementOnClose() {
        this.close_statement_on_close = true;
    }

    public void beforeFirst() throws SQLException {
        DatabaseError.throwSqlException(75, "beforeFirst");
    }

    public void afterLast() throws SQLException {
        DatabaseError.throwSqlException(75, "afterLast");
    }

    public boolean first() throws SQLException {
        DatabaseError.throwSqlException(75, "first");

        return false;
    }

    public boolean last() throws SQLException {
        DatabaseError.throwSqlException(75, "last");

        return false;
    }

    public boolean absolute(int row) throws SQLException {
        DatabaseError.throwSqlException(75, "absolute");

        return false;
    }

    public boolean relative(int rows) throws SQLException {
        DatabaseError.throwSqlException(75, "relative");

        return false;
    }

    public boolean previous() throws SQLException {
        DatabaseError.throwSqlException(75, "previous");

        return false;
    }

    public void setFetchDirection(int direction) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.setFetchDirection(direction="
                    + direction + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (direction == 1000)
            return;
        if ((direction == 1001) || (direction == 1002)) {
            DatabaseError.throwSqlException(75, "setFetchDirection(FETCH_REVERSE, FETCH_UNKNOWN)");
        } else
            DatabaseError.throwSqlException(68, "setFetchDirection");
    }

    public int getFetchDirection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.getFetchDirection: return: 1000",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return 1000;
    }

    public int getType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.getType: return: 1003", this);

            OracleLog.recursiveTrace = false;
        }

        return 1003;
    }

    public int getConcurrency() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.getConcurrency: return: 1007",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return 1007;
    }

    public SQLWarning getWarnings() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.getWarnings: return: "
                    + this.sqlWarning, this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlWarning;
    }

    public void clearWarnings() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.clearWarnings()", this);

            OracleLog.recursiveTrace = false;
        }

        this.sqlWarning = null;
    }

    public boolean rowUpdated() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.rowUpdated: return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean rowInserted() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "BaseResultSet.rowInserted: return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean rowDeleted() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.rowDeleted: return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public void updateNull(int columnIndex) throws SQLException {
        DatabaseError.throwSqlException(76, "updateNull");
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBoolean");
    }

    public void updateByte(int columnIndex, byte x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateByte");
    }

    public void updateShort(int columnIndex, short x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateShort");
    }

    public void updateInt(int columnIndex, int x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateInt");
    }

    public void updateLong(int columnIndex, long x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateLong");
    }

    public void updateFloat(int columnIndex, float x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateFloat");
    }

    public void updateDouble(int columnIndex, double x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateDouble");
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBigDecimal");
    }

    public void updateString(int columnIndex, String x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateString");
    }

    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBytes");
    }

    public void updateDate(int columnIndex, Date x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateDate");
    }

    public void updateTime(int columnIndex, Time x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateTime");
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateTimestamp");
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        DatabaseError.throwSqlException(76, "updateAsciiStream");
    }

    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBinaryStream");
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        DatabaseError.throwSqlException(76, "updateCharacterStream");
    }

    public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
        DatabaseError.throwSqlException(76, "updateObject");
    }

    public void updateObject(int columnIndex, Object x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateObject");
    }

    public void updateOracleObject(int columnIndex, Datum x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateOracleObject");
    }

    public void updateROWID(int columnIndex, ROWID x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateROWID");
    }

    public void updateNUMBER(int columnIndex, NUMBER x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateNUMBER");
    }

    public void updateDATE(int columnIndex, DATE x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateDATE");
    }

    public void updateARRAY(int columnIndex, ARRAY x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateARRAY");
    }

    public void updateSTRUCT(int columnIndex, STRUCT x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateSTRUCT");
    }

    public void updateOPAQUE(int columnIndex, OPAQUE x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateOPAQUE");
    }

    public void updateREF(int columnIndex, REF x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateREF");
    }

    public void updateCHAR(int columnIndex, CHAR x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateCHAR");
    }

    public void updateRAW(int columnIndex, RAW x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateRAW");
    }

    public void updateBLOB(int columnIndex, BLOB x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBLOB");
    }

    public void updateCLOB(int columnIndex, CLOB x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateCLOB");
    }

    public void updateBFILE(int columnIndex, BFILE x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBFILE");
    }

    public void updateINTERVALYM(int columnIndex, INTERVALYM x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateINTERVALYM");
    }

    public void updateINTERVALDS(int columnIndex, INTERVALDS x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateINTERVALDS");
    }

    public void updateTIMESTAMP(int columnIndex, TIMESTAMP x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateTIMESTAMP");
    }

    public void updateTIMESTAMPTZ(int columnIndex, TIMESTAMPTZ x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateTIMESTAMPTZ");
    }

    public void updateTIMESTAMPLTZ(int columnIndex, TIMESTAMPLTZ x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateTIMESTAMPLTZ");
    }

    public void updateBfile(int columnIndex, BFILE x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBfile");
    }

    public void updateCustomDatum(int columnIndex, CustomDatum x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateCustomDatum");
    }

    public void updateORAData(int columnIndex, ORAData x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateORAData");
    }

    public void updateRef(int columnIndex, Ref x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateRef");
    }

    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateBlob");
    }

    public void updateClob(int columnIndex, Clob x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateClob");
    }

    public void updateArray(int columnIndex, Array x) throws SQLException {
        DatabaseError.throwSqlException(76, "updateArray");
    }

    public void insertRow() throws SQLException {
        DatabaseError.throwSqlException(76, "insertRow");
    }

    public void updateRow() throws SQLException {
        DatabaseError.throwSqlException(76, "updateRow");
    }

    public void deleteRow() throws SQLException {
        DatabaseError.throwSqlException(76, "deleteRow");
    }

    public void refreshRow() throws SQLException {
        DatabaseError.throwSqlException(23, null);
    }

    public void cancelRowUpdates() throws SQLException {
        DatabaseError.throwSqlException(76, "cancelRowUpdates");
    }

    public void moveToInsertRow() throws SQLException {
        DatabaseError.throwSqlException(76, "moveToInsertRow");
    }

    public void moveToCurrentRow() throws SQLException {
        DatabaseError.throwSqlException(76, "moveToCurrentRow");
    }

    public void setAutoRefetch(boolean autoRefetch) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.setAutoRefetch(autoRefetch="
                    + autoRefetch + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.autoRefetch = autoRefetch;
    }

    public boolean getAutoRefetch() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "BaseResultSet.getAutoRefetch: return: "
                    + this.autoRefetch, this);

            OracleLog.recursiveTrace = false;
        }

        return this.autoRefetch;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.BaseResultSet"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BaseResultSet JD-Core Version: 0.6.0
 */