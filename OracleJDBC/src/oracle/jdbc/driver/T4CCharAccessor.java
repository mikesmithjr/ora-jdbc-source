package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CCharAccessor extends CharAccessor {
    T4CMAREngine mare;
    boolean underlyingLong = false;

    final int[] meta = new int[1];
    final int[] tmp = new int[1];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4CCharAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, form, external_type, forBind);

        this.mare = _mare;

        calculateSizeTmpByteArray();
    }

    T4CCharAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, int _oacmxl,
            int _definedColumnType, int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, nullable, flags, precision, scale, contflag, total_elems, form);

        this.mare = _mare;
        this.definedColumnType = _definedColumnType;
        this.definedColumnSize = _definedColumnSize;

        calculateSizeTmpByteArray();

        this.oacmxl = _oacmxl;

        if (this.oacmxl == -1) {
            this.underlyingLong = true;
            this.oacmxl = 4000;
        }
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

    boolean unmarshalOneRow() throws SQLException, IOException {
        if (this.isUseLess) {
            this.lastRowProcessed += 1;

            return false;
        }

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;

        byte[] bytesBuffer = this.statement.tmpByteArray;
        int tmpSpaceCharOffset = this.columnIndex + this.lastRowProcessed * this.charLength;

        if (this.rowSpaceIndicator == null) {
            byte[] buff = new byte[16000];

            this.mare.unmarshalCLR(buff, 0, this.meta);
            processIndicator(this.meta[0]);

            this.lastRowProcessed += 1;

            return false;
        }

        if (this.isNullByDescribe) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
            this.lastRowProcessed += 1;

            if (this.mare.versionNumber < 9200) {
                processIndicator(0);
            }

            return false;
        }

        if (this.statement.maxFieldSize > 0)
            this.mare.unmarshalCLR(bytesBuffer, 0, this.meta, this.statement.maxFieldSize);
        else {
            this.mare.unmarshalCLR(bytesBuffer, 0, this.meta);
        }
        this.tmp[0] = this.meta[0];

        int nbOfCharsConverted = 0;

        if (this.formOfUse == 2) {
            nbOfCharsConverted = this.statement.connection.conversion
                    .NCHARBytesToJavaChars(bytesBuffer, 0, this.rowSpaceChar,
                                           tmpSpaceCharOffset + 1, this.tmp, this.charLength - 1);
        } else {
            nbOfCharsConverted = this.statement.connection.conversion
                    .CHARBytesToJavaChars(bytesBuffer, 0, this.rowSpaceChar,
                                          tmpSpaceCharOffset + 1, this.tmp, this.charLength - 1);
        }

        this.rowSpaceChar[tmpSpaceCharOffset] = (char) (nbOfCharsConverted * 2);

        processIndicator(this.meta[0]);

        if (this.meta[0] == 0) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
        } else {
            this.rowSpaceIndicator[tmpLengthOffset] = (short) (this.meta[0] * 2);

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
        int tmpSpaceCharOffset = this.columnIndex + this.lastRowProcessed * this.charLength;
        int tmpSpaceCharOffsetLastRow = this.columnIndex + (rowIdSource - 1) * this.charLength;

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpIndicatorOffsetLastRow = this.indicatorIndex + rowIdSource - 1;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;
        int tmpLengthOffsetLastRow = this.lengthIndex + rowIdSource - 1;
        int nbBytes = this.rowSpaceIndicator[tmpLengthOffsetLastRow];

        this.rowSpaceIndicator[tmpLengthOffset] = (short) nbBytes;
        this.rowSpaceIndicator[tmpIndicatorOffset] = this.rowSpaceIndicator[tmpIndicatorOffsetLastRow];

        System.arraycopy(this.rowSpaceChar, tmpSpaceCharOffsetLastRow, this.rowSpaceChar,
                         tmpSpaceCharOffset,
                         this.rowSpaceChar[tmpSpaceCharOffsetLastRow] / '\002' + 1);

        this.lastRowProcessed += 1;
    }

    void saveDataFromOldDefineBuffers(byte[] rowSpaceByteLastRow, char[] rowSpaceCharLastRow,
            short[] rowSpaceIndicatorLastRow, int oldPrefetchSize, int newPrefetchSize)
            throws SQLException {
        int tmpSpaceCharOffset = this.columnIndex + (newPrefetchSize - 1) * this.charLength;

        int tmpSpaceCharOffsetLastRow = this.columnIndexLastRow + (oldPrefetchSize - 1)
                * this.charLength;

        int tmpIndicatorOffset = this.indicatorIndex + newPrefetchSize - 1;
        int tmpIndicatorOffsetLastRow = this.indicatorIndexLastRow + oldPrefetchSize - 1;
        int tmpLengthOffset = this.lengthIndex + newPrefetchSize - 1;
        int tmpLengthOffsetLastRow = this.lengthIndexLastRow + oldPrefetchSize - 1;
        int nbBytes = rowSpaceIndicatorLastRow[tmpLengthOffsetLastRow];

        this.rowSpaceIndicator[tmpLengthOffset] = (short) nbBytes;
        this.rowSpaceIndicator[tmpIndicatorOffset] = rowSpaceIndicatorLastRow[tmpIndicatorOffsetLastRow];

        if (nbBytes != 0) {
            System.arraycopy(rowSpaceCharLastRow, tmpSpaceCharOffsetLastRow, this.rowSpaceChar,
                             tmpSpaceCharOffset,
                             rowSpaceCharLastRow[tmpSpaceCharOffsetLastRow] / '\002' + 1);
        }
    }

    void calculateSizeTmpByteArray() {
        int maxNbBytes;
        if (this.formOfUse == 2) {
            maxNbBytes = (this.charLength - 1) * this.statement.connection.conversion.maxNCharSize;
        } else {
            maxNbBytes = (this.charLength - 1) * this.statement.connection.conversion.cMaxCharSize;
        }

        if (this.statement.sizeTmpByteArray < maxNbBytes)
            this.statement.sizeTmpByteArray = maxNbBytes;
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize)) {
            ret = ret.substring(0, this.definedColumnSize);
        }
        return ret;
    }

    Object getObject(int currentRow) throws SQLException {
        if (this.definedColumnType == 0) {
            return super.getObject(currentRow);
        }

        Object result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }
        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            switch (this.definedColumnType) {
            case -1:
            case 1:
            case 12:
                return getString(currentRow);
            case 2:
            case 3:
                return getBigDecimal(currentRow);
            case 4:
                return new Integer(getInt(currentRow));
            case -6:
                return new Byte(getByte(currentRow));
            case 5:
                return new Short(getShort(currentRow));
            case -7:
            case 16:
                return new Boolean(getBoolean(currentRow));
            case -5:
                return new Long(getLong(currentRow));
            case 7:
                return new Float(getFloat(currentRow));
            case 6:
            case 8:
                return new Double(getDouble(currentRow));
            case 91:
                return getDate(currentRow);
            case 92:
                return getTime(currentRow);
            case 93:
                return getTimestamp(currentRow);
            case -4:
            case -3:
            case -2:
                return getBytes(currentRow);
            }

            DatabaseError.throwSqlException(4);

            return null;
        }

        return result;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CCharAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CCharAccessor JD-Core Version: 0.6.0
 */