package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CLongAccessor extends LongAccessor {
    T4CMAREngine mare;
    static final int t4MaxLength = 2147483647;
    static final int t4PlsqlMaxLength = 32760;
    byte[][] data = (byte[][]) null;
    int[] nbBytesRead = null;
    int[] bytesReadSoFar = null;

    final int[] escapeSequenceArr = new int[1];
    final boolean[] readHeaderArr = new boolean[1];
    final boolean[] readAsNonStreamArr = new boolean[1];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CLongAccessor(OracleStatement stmt, int column_pos, int max_len, short form,
            int external_type, T4CMAREngine _mare) throws SQLException {
        super(stmt, column_pos, max_len, form, external_type);

        this.mare = _mare;

        if (stmt.connection.useFetchSizeWithLongColumn) {
            this.data = new byte[stmt.rowPrefetch][];

            for (int i = 0; i < stmt.rowPrefetch; i++) {
                this.data[i] = new byte[4080];
            }
            this.nbBytesRead = new int[stmt.rowPrefetch];
            this.bytesReadSoFar = new int[stmt.rowPrefetch];
        }
    }

    T4CLongAccessor(OracleStatement stmt, int column_pos, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form,
            int _definedColumnType, int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
        super(stmt, column_pos, max_len, nullable, flags, precision, scale, contflag, total_elems,
                form);

        this.mare = _mare;
        this.definedColumnType = _definedColumnType;
        this.definedColumnSize = _definedColumnSize;

        if (stmt.connection.useFetchSizeWithLongColumn) {
            this.data = new byte[stmt.rowPrefetch][];

            for (int i = 0; i < stmt.rowPrefetch; i++) {
                this.data[i] = new byte[4080];
            }
            this.nbBytesRead = new int[stmt.rowPrefetch];
            this.bytesReadSoFar = new int[stmt.rowPrefetch];
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

        boolean isStream = false;
        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;

        this.escapeSequenceArr[0] = this.mare.unmarshalUB1();

        if (this.mare.escapeSequenceNull(this.escapeSequenceArr[0])) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;

            this.mare.processIndicator(false, 0);

            int ignore = (int) this.mare.unmarshalUB4();

            isStream = false;
            this.escapeSequenceArr[0] = 0;
            this.lastRowProcessed += 1;
        } else {
            this.rowSpaceIndicator[tmpIndicatorOffset] = 0;
            this.readHeaderArr[0] = true;
            this.readAsNonStreamArr[0] = false;

            if (this.statement.connection.useFetchSizeWithLongColumn) {
                int nbBytesReadTemp = 0;

                while (nbBytesReadTemp != -1) {
                    if (this.data[this.lastRowProcessed].length < this.nbBytesRead[this.lastRowProcessed] + 255) {
                        byte[] tempByteArr = new byte[this.data[this.lastRowProcessed].length * 4];

                        System.arraycopy(this.data[this.lastRowProcessed], 0, tempByteArr, 0,
                                         this.nbBytesRead[this.lastRowProcessed]);

                        this.data[this.lastRowProcessed] = tempByteArr;
                    }

                    nbBytesReadTemp = readStreamFromWire(
                                                         this.data[this.lastRowProcessed],
                                                         this.nbBytesRead[this.lastRowProcessed],
                                                         255,
                                                         this.escapeSequenceArr,
                                                         this.readHeaderArr,
                                                         this.readAsNonStreamArr,
                                                         this.mare,
                                                         ((T4CConnection) this.statement.connection).oer);

                    if (nbBytesReadTemp == -1)
                        continue;
                    this.nbBytesRead[this.lastRowProcessed] += nbBytesReadTemp;
                }

                this.lastRowProcessed += 1;
            } else {
                isStream = true;
            }

        }

        return isStream;
    }

    void fetchNextColumns() throws SQLException {
        this.statement.continueReadRow(this.columnPosition);
    }

    int readStream(byte[] buffer, int length) throws SQLException, IOException {
        int currentRow = this.statement.currentRow;

        if (this.statement.connection.useFetchSizeWithLongColumn) {
            byte[] data_l = this.data[currentRow];
            int totalNbBytes = this.nbBytesRead[currentRow];
            int bytesAlreadyRead = this.bytesReadSoFar[currentRow];

            if (bytesAlreadyRead == totalNbBytes) {
                return -1;
            }

            int len = 0;

            if (length <= totalNbBytes - bytesAlreadyRead)
                len = length;
            else {
                len = totalNbBytes - bytesAlreadyRead;
            }
            System.arraycopy(data_l, bytesAlreadyRead, buffer, 0, len);

            this.bytesReadSoFar[currentRow] += len;

            return len;
        }

        int len = readStreamFromWire(buffer, 0, length, this.escapeSequenceArr, this.readHeaderArr,
                                     this.readAsNonStreamArr, this.mare,
                                     ((T4CConnection) this.statement.connection).oer);

        return len;
    }

    protected static final int readStreamFromWire(byte[] buffer, int offset, int length,
            int[] escapeSequenceArr, boolean[] readHeaderArr, boolean[] readAsNonStreamArr,
            T4CMAREngine mare, T4CTTIoer oer) throws SQLException, IOException {
        int bytesToRead = -1;
        try {
            if (readAsNonStreamArr[0] == false) {
                if ((length > 255) || (length < 0)) {
                    DatabaseError.throwSqlException(433);
                }

                if (readHeaderArr[0] != false) {
                    if (escapeSequenceArr[0] == 254) {
                        bytesToRead = mare.unmarshalUB1();
                    } else {
                        if (escapeSequenceArr[0] == 0) {
                            return 0;
                        }

                        readAsNonStreamArr[0] = true;
                        bytesToRead = escapeSequenceArr[0];
                    }

                    readHeaderArr[0] = false;
                    escapeSequenceArr[0] = 0;
                } else {
                    bytesToRead = mare.unmarshalUB1();
                }

            } else {
                readAsNonStreamArr[0] = false;
            }

            if (bytesToRead > 0) {
                mare.unmarshalNBytes(buffer, offset, bytesToRead);
            } else {
                bytesToRead = -1;
            }

        } catch (BreakNetException ea) {
            bytesToRead = mare.unmarshalSB1();
            if (bytesToRead == 4) {
                oer.init();
                oer.processError();
            }

        }

        if (bytesToRead == -1) {
            readHeaderArr[0] = true;

            mare.unmarshalUB2();
            mare.unmarshalUB2();
        }

        return bytesToRead;
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize)) {
            ret = ret.substring(0, this.definedColumnSize);
        }
        return ret;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CLongAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CLongAccessor JD-Core Version: 0.6.0
 */