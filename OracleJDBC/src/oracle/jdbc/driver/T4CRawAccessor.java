package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CRawAccessor extends RawAccessor {
    T4CMAREngine mare;
    boolean underlyingLongRaw = false;

    final int[] meta = new int[1];
    final int[] escapeSequenceArr = new int[1];
    final boolean[] readHeaderArr = new boolean[1];
    final boolean[] readAsNonStreamArr = new boolean[1];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CRawAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, form, external_type, forBind);

        this.mare = _mare;
    }

    T4CRawAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, int _definedColumnType,
            int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
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

    boolean unmarshalOneRow() throws SQLException, IOException {
        if (this.isUseLess) {
            this.lastRowProcessed += 1;

            return false;
        }

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;
        int tmpSpaceByteOffset = this.columnIndex + this.lastRowProcessed * this.byteLength;

        if (!this.underlyingLongRaw) {
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

            this.mare.unmarshalCLR(this.rowSpaceByte, tmpSpaceByteOffset + 2, this.meta,
                                   this.byteLength - 2);
        } else {
            this.escapeSequenceArr[0] = this.mare.unmarshalUB1();
            int ignore;
            if (this.mare.escapeSequenceNull(this.escapeSequenceArr[0])) {
                this.meta[0] = 0;

                this.mare.processIndicator(false, 0);

                ignore = (int) this.mare.unmarshalUB4();
            } else {
                int nbBytesReadTemp = 0;
                int nbBytesRead = 0;
                byte[] bufferToUse = this.rowSpaceByte;
                int offset = tmpSpaceByteOffset + 2;

                this.readHeaderArr[0] = true;
                this.readAsNonStreamArr[0] = false;

                while (nbBytesReadTemp != -1) {
                    if ((bufferToUse == this.rowSpaceByte)
                            && (nbBytesRead + 255 > this.byteLength - 2)) {
                        bufferToUse = new byte['ÿ'];
                    }
                    if (bufferToUse != this.rowSpaceByte) {
                        offset = 0;
                    }
                    nbBytesReadTemp = T4CLongRawAccessor
                            .readStreamFromWire(bufferToUse, offset, 255, this.escapeSequenceArr,
                                                this.readHeaderArr, this.readAsNonStreamArr,
                                                this.mare,
                                                ((T4CConnection) this.statement.connection).oer);

                    if (nbBytesReadTemp == -1)
                        continue;
                    if (bufferToUse == this.rowSpaceByte) {
                        nbBytesRead += nbBytesReadTemp;
                        offset += nbBytesReadTemp;
                        continue;
                    }
                    if (this.byteLength - 2 - nbBytesRead <= 0) {
                        continue;
                    }
                    int nbBytesAvailable = this.byteLength - 2 - nbBytesRead;

                    System.arraycopy(bufferToUse, 0, this.rowSpaceByte, offset, nbBytesAvailable);

                    nbBytesRead += nbBytesAvailable;
                }

                if (bufferToUse != this.rowSpaceByte) {
                    bufferToUse = null;
                }
                this.meta[0] = nbBytesRead;
            }

        }

        this.rowSpaceByte[tmpSpaceByteOffset] = (byte) ((this.meta[0] & 0xFF00) >> 8);
        this.rowSpaceByte[(tmpSpaceByteOffset + 1)] = (byte) (this.meta[0] & 0xFF);

        if (!this.underlyingLongRaw) {
            processIndicator(this.meta[0]);
        }
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
                         tmpSpaceByteOffset, nbBytes + 2);

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
                             tmpSpaceByteOffset, nbBytes + 2);
        }
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize * 2)) {
            ret = ret.substring(0, this.definedColumnSize * 2);
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
            case -2:
                return getRAW(currentRow);
            }

            DatabaseError.throwSqlException(4);

            return null;
        }

        return result;
    }

    byte[] getBytes(int currentRow) throws SQLException {
        byte[] result = super.getBytes(currentRow);

        if ((this.definedColumnSize > 0) && (this.definedColumnSize < result.length)) {
            byte[] res2 = new byte[this.definedColumnSize];

            System.arraycopy(result, 0, res2, 0, this.definedColumnSize);

            result = res2;
        }

        return result;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CRawAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CRawAccessor JD-Core Version: 0.6.0
 */