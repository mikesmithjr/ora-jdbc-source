package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import oracle.jdbc.oracore.OracleType;
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

abstract class Accessor {
    static final int FIXED_CHAR = 999;
    static final int CHAR = 96;
    static final int VARCHAR = 1;
    static final int VCS = 9;
    static final int LONG = 8;
    static final int NUMBER = 2;
    static final int VARNUM = 6;
    static final int BINARY_FLOAT = 100;
    static final int BINARY_DOUBLE = 101;
    static final int RAW = 23;
    static final int VBI = 15;
    static final int LONG_RAW = 24;
    static final int ROWID = 104;
    static final int ROWID_THIN = 11;
    static final int RESULT_SET = 102;
    static final int RSET = 116;
    static final int DATE = 12;
    static final int BLOB = 113;
    static final int CLOB = 112;
    static final int BFILE = 114;
    static final int NAMED_TYPE = 109;
    static final int REF_TYPE = 111;
    static final int TIMESTAMP = 180;
    static final int TIMESTAMPTZ = 181;
    static final int TIMESTAMPLTZ = 231;
    static final int INTERVALYM = 182;
    static final int INTERVALDS = 183;
    static final int UROWID = 208;
    static final int PLSQL_INDEX_TABLE = 998;
    static final int T2S_OVERLONG_RAW = 997;
    static final int SET_CHAR_BYTES = 996;
    static final int NULL_TYPE = 995;
    static final int DML_RETURN_PARAM = 994;
    static final int ONLY_FORM_USABLE = 0;
    static final int NOT_USABLE = 1;
    static final int NO_NEED_TO_PREPARE = 2;
    static final int NEED_TO_PREPARE = 3;
    OracleStatement statement;
    boolean outBind;
    int internalType;
    int internalTypeMaxLength;
    boolean isStream = false;

    boolean isColumnNumberAware = false;

    short formOfUse = 2;
    OracleType internalOtype;
    int externalType;
    String internalTypeName;
    String columnName;
    int describeType;
    int describeMaxLength;
    boolean nullable;
    int precision;
    int scale;
    int flags;
    int contflag;
    int total_elems;
    OracleType describeOtype;
    String describeTypeName;
    int definedColumnType = 0;
    int definedColumnSize = 0;
    int oacmxl = 0;

    byte[] rowSpaceByte = null;
    char[] rowSpaceChar = null;
    short[] rowSpaceIndicator = null;
    int columnIndex = 0;
    int lengthIndex = 0;
    int indicatorIndex = 0;
    int columnIndexLastRow = 0;
    int lengthIndexLastRow = 0;
    int indicatorIndexLastRow = 0;
    int byteLength = 0;
    int charLength = 0;
    int defineType;
    boolean isDMLReturnedParam = false;

    int lastRowProcessed = 0;

    boolean isUseLess = false;

    int physicalColumnIndex = -2;

    boolean isNullByDescribe = false;

    void setOffsets(int nrows) {
        this.columnIndex = this.statement.defineByteSubRange;
        this.statement.defineByteSubRange = (this.columnIndex + nrows * this.byteLength);
    }

    void init(OracleStatement stmt, int internal_type, int database_type, short form,
            boolean forBind) throws SQLException {
        this.statement = stmt;
        this.outBind = forBind;
        this.internalType = internal_type;
        this.defineType = database_type;
        this.formOfUse = form;
    }

    abstract void initForDataAccess(int paramInt1, int paramInt2, String paramString)
            throws SQLException;

    void initForDescribe(int type, int maxLength, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        this.describeType = type;
        this.describeMaxLength = maxLength;
        this.nullable = nullable;
        this.precision = precision;
        this.scale = scale;
        this.flags = flags;
        this.contflag = contflag;
        this.total_elems = total_elems;
        this.formOfUse = form;
    }

    void initForDescribe(int type, int maxLength, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, String typeName)
            throws SQLException {
        this.describeTypeName = typeName;
        this.describeOtype = null;

        initForDescribe(type, maxLength, nullable, flags, precision, scale, contflag, total_elems,
                        form);
    }

    OracleInputStream initForNewRow() throws SQLException {
        unimpl("initForNewRow");
        return null;
    }

    int useForDataAccessIfPossible(int internal_type, int external_type, int max_len,
            String typeName) throws SQLException {
        int result = 3;
        int oldByteLength = 0;
        int oldCharLength = 0;

        if (this.internalType != 0) {
            if (this.internalType != internal_type) {
                result = 0;
            } else if (this.rowSpaceIndicator != null) {
                oldByteLength = this.byteLength;
                oldCharLength = this.charLength;
            }
        }

        if (result == 3) {
            initForDataAccess(external_type, max_len, typeName);

            if ((!this.outBind) && (oldByteLength >= this.byteLength)
                    && (oldCharLength >= this.charLength)) {
                result = 2;
            }
        }
        return result;
    }

    boolean useForDescribeIfPossible(int type, int maxLength, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short formOfUse,
            String typeName) throws SQLException {
        if ((this.externalType == 0) && (type != this.describeType)) {
            return false;
        }
        initForDescribe(type, maxLength, nullable, flags, precision, scale, contflag, total_elems,
                        formOfUse, typeName);

        return true;
    }

    void setFormOfUse(short form) {
        this.formOfUse = form;
    }

    void updateColumnNumber(int colNumber) {
    }

    public String toString() {
        return super.toString() + ", statement=" + this.statement + ", outBind=" + this.outBind
                + ", internalType=" + this.internalType + ", internalTypeMaxLength="
                + this.internalTypeMaxLength + ", isStream=" + this.isStream + ", formOfUse="
                + this.formOfUse + ", internalOtype=" + this.internalOtype + ", externalType="
                + this.externalType + ", internalTypeName=" + this.internalTypeName
                + ", columnName=" + this.columnName + ", describeType=" + this.describeType
                + ", describeMaxLength=" + this.describeMaxLength + ", nullable=" + this.nullable
                + ", precision=" + this.precision + ", scale=" + this.scale + ", flags="
                + this.flags + ", contflag=" + this.contflag + ", total_elems=" + this.total_elems
                + ", describeOtype=" + this.describeOtype + ", describeTypeName="
                + this.describeTypeName + ", rowSpaceByte=" + this.rowSpaceByte + ", rowSpaceChar="
                + this.rowSpaceChar + ", rowSpaceIndicator=" + this.rowSpaceIndicator
                + ", columnIndex=" + this.columnIndex + ", lengthIndex=" + this.lengthIndex
                + ", indicatorIndex=" + this.indicatorIndex + ", byteLength=" + this.byteLength
                + ", charLength=" + this.charLength;
    }

    void unimpl(String methodName) throws SQLException {
        DatabaseError.throwSqlException(4, methodName + " not implemented for " + getClass());
    }

    boolean getBoolean(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return false;
        }
        unimpl("getBoolean");

        return false;
    }

    byte getByte(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0;
        }
        unimpl("getByte");

        return 0;
    }

    short getShort(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0;
        }
        unimpl("getShort");

        return 0;
    }

    int getInt(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0;
        }
        unimpl("getInt");

        return 0;
    }

    long getLong(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0L;
        }
        unimpl("getLong");

        return 0L;
    }

    float getFloat(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0.0F;
        }
        unimpl("getFloat");

        return 0.0F;
    }

    double getDouble(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0.0D;
        }
        unimpl("getDouble");

        return 0.0D;
    }

    BigDecimal getBigDecimal(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getBigDecimal");

        return null;
    }

    BigDecimal getBigDecimal(int currentRow, int scale) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getBigDecimal");

        return null;
    }

    String getString(int currentRow) throws SQLException {
        return null;
    }

    Date getDate(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getDate");

        return null;
    }

    Date getDate(int currentRow, Calendar cal) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getDate");

        return null;
    }

    Time getTime(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTime");

        return null;
    }

    Time getTime(int currentRow, Calendar cal) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTime");

        return null;
    }

    Timestamp getTimestamp(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTimestamp");

        return null;
    }

    Timestamp getTimestamp(int currentRow, Calendar cal) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTimestamp");

        return null;
    }

    byte[] privateGetBytes(int currentRow) throws SQLException {
        return getBytes(currentRow);
    }

    byte[] getBytes(int currentRow) throws SQLException {
        byte[] result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;

            result = new byte[len];
            System.arraycopy(this.rowSpaceByte, off, result, 0, len);
        }

        return result;
    }

    InputStream getAsciiStream(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getAsciiStream");

        return null;
    }

    InputStream getUnicodeStream(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getUnicodeStream");

        return null;
    }

    InputStream getBinaryStream(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getBinaryStream");

        return null;
    }

    Object getObject(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getObject");

        return null;
    }

    Object getAnyDataEmbeddedObject(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getAnyDataEmbeddedObject");

        return null;
    }

    ResultSet getCursor(int currentRow) throws SQLException {
        DatabaseError.throwSqlException(4);

        return null;
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getOracleObject");

        return null;
    }

    ROWID getROWID(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        DatabaseError.throwSqlException(4);

        return null;
    }

    NUMBER getNUMBER(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getNUMBER");

        return null;
    }

    DATE getDATE(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getDATE");

        return null;
    }

    ARRAY getARRAY(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getARRAY");

        return null;
    }

    STRUCT getSTRUCT(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getSTRUCT");

        return null;
    }

    OPAQUE getOPAQUE(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getOPAQUE");

        return null;
    }

    REF getREF(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getREF");

        return null;
    }

    CHAR getCHAR(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getCHAR");

        return null;
    }

    RAW getRAW(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getRAW");

        return null;
    }

    BLOB getBLOB(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getBLOB");

        return null;
    }

    CLOB getCLOB(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getCLOB");

        return null;
    }

    BFILE getBFILE(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getBFILE");

        return null;
    }

    CustomDatum getCustomDatum(int currentRow, CustomDatumFactory factory) throws SQLException {
        Datum d = getOracleObject(currentRow);

        return factory.create(d, 0);
    }

    ORAData getORAData(int currentRow, ORADataFactory factory) throws SQLException {
        Datum d = getOracleObject(currentRow);

        return factory.create(d, 0);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getObject");

        return null;
    }

    Reader getCharacterStream(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getCharacterStream");

        return null;
    }

    INTERVALYM getINTERVALYM(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getINTERVALYM");

        return null;
    }

    INTERVALDS getINTERVALDS(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getINTERVALDS");

        return null;
    }

    TIMESTAMP getTIMESTAMP(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTIMESTAMP");

        return null;
    }

    TIMESTAMPTZ getTIMESTAMPTZ(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTIMESTAMPTZ");

        return null;
    }

    TIMESTAMPLTZ getTIMESTAMPLTZ(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getTIMESTAMPLTZ");

        return null;
    }

    URL getURL(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getURL");

        return null;
    }

    Datum[] getOraclePlsqlIndexTable(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        unimpl("getOraclePlsqlIndexTable");

        return null;
    }

    boolean isNull(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        return this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1;
    }

    void setNull(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] = -1;
    }

    void fetchNextColumns() throws SQLException {
    }

    void calculateSizeTmpByteArray() {
    }

    boolean unmarshalOneRow() throws SQLException, IOException {
        DatabaseError.throwSqlException(148);
        return false;
    }

    void copyRow() throws SQLException, IOException {
        DatabaseError.throwSqlException(148);
    }

    int readStream(byte[] buffer, int length) throws SQLException, IOException {
        DatabaseError.throwSqlException(148);
        return -1;
    }

    void initMetadata() throws SQLException {
    }

    void setDisplaySize(int max_len) throws SQLException {
        this.describeMaxLength = max_len;
    }

    void saveDataFromOldDefineBuffers(byte[] tmpDefineByte, char[] tmpDefineChar,
            short[] tmpDefineIndicator, int oldPrefetchSize, int newPrefetchSize)
            throws SQLException {
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.Accessor JD-Core Version: 0.6.0
 */