package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

class T4CResultSetAccessor extends ResultSetAccessor {
    T4CMAREngine mare;
    OracleStatement[] newstmt = new OracleStatement[10];
    byte[] empty = { 0 };

    boolean underlyingLongRaw = false;

    final int[] meta = new int[1];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CResultSetAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, form, external_type, forBind);

        this.mare = _mare;
    }

    T4CResultSetAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form,
            int _definedColumnType, int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len == -1 ? _definedColumnSize : max_len, nullable, flags, precision,
                scale, contflag, total_elems, form);

        this.mare = _mare;
        this.definedColumnType = _definedColumnType;
        this.definedColumnSize = _definedColumnSize;

        if (max_len == -1)
            this.underlyingLongRaw = true;
    }

    void processIndicator(int size) throws IOException, SQLException {
        if (((this.internalType == 1) && (this.describeType == 112))
                || ((this.internalType == 23) && (this.describeType == 113))) {
            this.mare.unmarshalUB2();
            this.mare.unmarshalUB2();
        } else if (this.mare.versionNumber < 9200) {
            this.mare.unmarshalSB2();

            if ((this.statement.sqlKind != 1) && (this.statement.sqlKind != 4)) {
                this.mare.unmarshalSB2();
            }
        } else if ((this.statement.sqlKind == 1) || (this.statement.sqlKind == 4)
                || (this.isDMLReturnedParam)) {
            this.mare.processIndicator(size <= 0, size);
        }
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize)) {
            ret = ret.substring(0, this.definedColumnSize);
        }
        return ret;
    }

    boolean unmarshalOneRow() throws SQLException, IOException {
        if (this.isUseLess) {
            this.lastRowProcessed += 1;

            return false;
        }

        if (this.rowSpaceIndicator == null) {
            byte[] buff = new byte[16000];

            this.mare.unmarshalCLR(buff, 0, this.meta);
            processIndicator(this.meta[0]);

            this.lastRowProcessed += 1;

            return false;
        }

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;

        if (this.isNullByDescribe) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
            this.lastRowProcessed += 1;

            processIndicator(0);

            return false;
        }

        int tmpSpaceByteOffset = this.columnIndex + this.lastRowProcessed * this.byteLength;

        if (this.newstmt.length <= this.lastRowProcessed) {
            OracleStatement[] newstmt1 = new OracleStatement[this.newstmt.length * 4];

            System.arraycopy(this.newstmt, 0, newstmt1, 0, this.newstmt.length);

            this.newstmt = newstmt1;
        }

        this.newstmt[this.lastRowProcessed] = this.statement.connection
                .RefCursorBytesToStatement(this.empty, this.statement);

        this.newstmt[this.lastRowProcessed].needToSendOalToFetch = true;

        T4CTTIdcb dcb = new T4CTTIdcb(this.mare);

        dcb.init(this.newstmt[this.lastRowProcessed], 0);

        this.newstmt[this.lastRowProcessed].accessors = dcb
                .receiveFromRefCursor(this.newstmt[this.lastRowProcessed].accessors);

        this.newstmt[this.lastRowProcessed].numberOfDefinePositions = this.newstmt[this.lastRowProcessed].accessors.length;

        this.newstmt[this.lastRowProcessed].describedWithNames = true;
        this.newstmt[this.lastRowProcessed].described = true;

        int cursorId = (int) this.mare.unmarshalUB4();

        this.newstmt[this.lastRowProcessed].setCursorId(cursorId);

        if (cursorId > 0) {
            this.rowSpaceByte[tmpSpaceByteOffset] = 1;
            this.rowSpaceByte[(tmpSpaceByteOffset + 1)] = (byte) cursorId;

            this.meta[0] = 2;
        } else {
            this.newstmt[this.lastRowProcessed].close();

            this.newstmt[this.lastRowProcessed] = null;
            this.meta[0] = 0;
        }

        processIndicator(this.meta[0]);

        if (this.meta[0] == 0) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
        } else {
            this.rowSpaceIndicator[tmpLengthOffset] = (short) this.meta[0];
            this.rowSpaceIndicator[tmpIndicatorOffset] = 0;
        }

        this.lastRowProcessed += 1;

        return false;
    }

    void copyRow() throws SQLException, IOException {
        int rowIdSource;
        if (this.lastRowProcessed == 0)
            rowIdSource = this.statement.rowPrefetch;
        else {
            rowIdSource = this.lastRowProcessed;
        }
        int tmpSpaceByteOffset = this.columnIndex + this.lastRowProcessed * this.byteLength;
        int tmpSpaceByteOffsetLastRow = this.columnIndex + (rowIdSource - 1) * this.byteLength;

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpIndicatorOffsetLastRow = this.indicatorIndex + rowIdSource - 1;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;
        int tmpLengthOffsetLastRow = this.lengthIndex + rowIdSource - 1;
        int nbBytes = this.rowSpaceIndicator[tmpLengthOffsetLastRow];

        this.rowSpaceIndicator[tmpLengthOffset] = (short) nbBytes;
        this.rowSpaceIndicator[tmpIndicatorOffset] = this.rowSpaceIndicator[tmpIndicatorOffsetLastRow];

        System.arraycopy(this.rowSpaceByte, tmpSpaceByteOffsetLastRow, this.rowSpaceByte,
                         tmpSpaceByteOffset, nbBytes);

        this.lastRowProcessed += 1;
    }

    void saveDataFromOldDefineBuffers(byte[] rowSpaceByteLastRow, char[] rowSpaceCharLastRow,
            short[] rowSpaceIndicatorLastRow, int oldPrefetchSize, int newPrefetchSize)
            throws SQLException {
        int tmpSpaceByteOffset = this.columnIndex + (newPrefetchSize - 1) * this.byteLength;

        int tmpSpaceByteOffsetLastRow = this.columnIndexLastRow + (oldPrefetchSize - 1)
                * this.byteLength;

        int tmpIndicatorOffset = this.indicatorIndex + newPrefetchSize - 1;
        int tmpIndicatorOffsetLastRow = this.indicatorIndexLastRow + oldPrefetchSize - 1;
        int tmpLengthOffset = this.lengthIndex + newPrefetchSize - 1;
        int tmpLengthOffsetLastRow = this.lengthIndexLastRow + oldPrefetchSize - 1;
        int nbBytes = rowSpaceIndicatorLastRow[tmpLengthOffsetLastRow];

        this.rowSpaceIndicator[tmpLengthOffset] = (short) nbBytes;
        this.rowSpaceIndicator[tmpIndicatorOffset] = rowSpaceIndicatorLastRow[tmpIndicatorOffsetLastRow];

        if (nbBytes != 0) {
            System.arraycopy(rowSpaceByteLastRow, tmpSpaceByteOffsetLastRow, this.rowSpaceByte,
                             tmpSpaceByteOffset, nbBytes);
        }
    }

    ResultSet getCursor(int currentRow) throws SQLException {
        ResultSet rset;
        if (this.newstmt[currentRow] != null) {
            for (int i = 0; i < this.newstmt[currentRow].numberOfDefinePositions; i++) {
                this.newstmt[currentRow].accessors[i].initMetadata();
            }
            this.newstmt[currentRow].prepareAccessors();

            OracleResultSetImpl rsetimpl = new OracleResultSetImpl(
                    this.newstmt[currentRow].connection, this.newstmt[currentRow]);

            rsetimpl.close_statement_on_close = true;
            this.newstmt[currentRow].currentResultSet = rsetimpl;
            rset = rsetimpl;
        } else {
            throw new SQLException("Cursor is closed.");
        }
        return rset;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CResultSetAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CResultSetAccessor JD-Core Version: 0.6.0
 */