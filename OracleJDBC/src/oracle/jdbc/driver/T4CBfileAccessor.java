package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CBfileAccessor extends BfileAccessor {
    static final int maxLength = 530;
    T4CMAREngine mare;
    final int[] meta = new int[1];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4CBfileAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind, T4CMAREngine _mare) throws SQLException {
        super(stmt, 530, form, external_type, forBind);

        this.mare = _mare;
    }

    T4CBfileAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, int _definedColumnType,
            int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
        super(stmt, 530, nullable, flags, precision, scale, contflag, total_elems, form);

        this.mare = _mare;
        this.definedColumnType = _definedColumnType;
        this.definedColumnSize = _definedColumnSize;
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize)) {
            ret = ret.substring(0, this.definedColumnSize);
        }
        return ret;
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

        if (this.rowSpaceIndicator == null) {
            int len = (int) this.mare.unmarshalUB4();

            if (len == 0) {
                this.meta[0] = -1;

                processIndicator(0);

                this.lastRowProcessed += 1;

                return false;
            }

            byte[] buff = new byte[16000];

            this.mare.unmarshalCLR(buff, 0, this.meta);
            processIndicator(this.meta[0]);

            this.lastRowProcessed += 1;

            return false;
        }

        int tmpSpaceByteOffset = this.columnIndex + this.lastRowProcessed * this.byteLength;
        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;

        if (this.isNullByDescribe) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
            this.lastRowProcessed += 1;

            if (this.mare.versionNumber < 9200) {
                processIndicator(0);
            }

            return false;
        }

        int len = (int) this.mare.unmarshalUB4();

        if (len == 0) {
            this.meta[0] = -1;

            processIndicator(0);

            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;

            this.lastRowProcessed += 1;

            return false;
        }

        this.mare.unmarshalCLR(this.rowSpaceByte, tmpSpaceByteOffset, this.meta, this.byteLength);

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
            case -13:
                return getBFILE(currentRow);
            }

            DatabaseError.throwSqlException(4);

            return null;
        }

        return result;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CBfileAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CBfileAccessor JD-Core Version: 0.6.0
 */