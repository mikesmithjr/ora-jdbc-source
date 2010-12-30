package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CRowidAccessor extends RowidAccessor {
    T4CMAREngine mare;
    static final int maxLength = 128;
    final int[] meta = new int[1];
    static final int KGRD_EXTENDED_OBJECT = 6;
    static final int KGRD_EXTENDED_BLOCK = 6;
    static final int KGRD_EXTENDED_FILE = 3;
    static final int KGRD_EXTENDED_SLOT = 3;
    static final int kd4_ubridtype_physicall = 1;
    static final int kd4_ubridtype_logical = 2;
    static final int kd4_ubridtype_remote = 3;
    static final int kd4_ubridtype_exttab = 4;
    static final int kd4_ubridlen_typeind = 1;
    static final byte[] kgrd_indbyte_char = { 65, 42, 45, 40, 41 };

    static final byte[] kgrd_basis_64 = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78,
            79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104,
            105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121,
            122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };

    static final byte[] kgrd_index_64 = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61,
            -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
            17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32,
            33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1,
            -1, -1 };

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CRowidAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, form, external_type, forBind);

        this.mare = _mare;
        this.defineType = 104;
    }

    T4CRowidAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, int _definedColumnType,
            int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, nullable, flags, precision, scale, contflag, total_elems, form);

        this.mare = _mare;
        this.definedColumnType = _definedColumnType;
        this.definedColumnSize = _definedColumnSize;
        this.defineType = 104;
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
            short len = this.mare.unmarshalUB1();
            long rba = 0L;
            int partitionID = 0;
            short tableID = 0;
            long blockNumber = 0L;
            int slotNumber = 0;

            if (len > 0) {
                rba = this.mare.unmarshalUB4();
                partitionID = this.mare.unmarshalUB2();
                tableID = this.mare.unmarshalUB1();
                blockNumber = this.mare.unmarshalUB4();
                slotNumber = this.mare.unmarshalUB2();
            }

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

            if (this.mare.versionNumber < 9200) {
                processIndicator(0);
            }
            return false;
        }

        int tmpSpaceByteOffset = this.columnIndex + this.lastRowProcessed * this.byteLength;

        if (this.describeType != 208) {
            short len = this.mare.unmarshalUB1();
            long rba = 0L;
            int partitionID = 0;
            short tableID = 0;
            long blockNumber = 0L;
            int slotNumber = 0;

            if (len > 0) {
                rba = this.mare.unmarshalUB4();
                partitionID = this.mare.unmarshalUB2();
                tableID = this.mare.unmarshalUB1();
                blockNumber = this.mare.unmarshalUB4();
                slotNumber = this.mare.unmarshalUB2();
            }

            if ((rba == 0L) && (partitionID == 0) && (tableID == 0) && (blockNumber == 0L)
                    && (slotNumber == 0)) {
                this.meta[0] = 0;
            } else {
                long[] args = { rba, partitionID, blockNumber, slotNumber };

                byte[] base64format = rowidToString(args);
                int nbBytesToKeep = 18;

                if (this.byteLength - 2 < 18) {
                    nbBytesToKeep = this.byteLength - 2;
                }
                System.arraycopy(base64format, 0, this.rowSpaceByte, tmpSpaceByteOffset + 2,
                                 nbBytesToKeep);

                this.meta[0] = nbBytesToKeep;
            }

        } else if ((this.meta[0] = (int) this.mare.unmarshalUB4()) > 0) {
            this.mare.unmarshalCLR(this.rowSpaceByte, tmpSpaceByteOffset + 2, this.meta);
        }

        this.rowSpaceByte[tmpSpaceByteOffset] = (byte) ((this.meta[0] & 0xFF00) >> 8);
        this.rowSpaceByte[(tmpSpaceByteOffset + 1)] = (byte) (this.meta[0] & 0xFF);

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

    String getString(int currentRow) throws SQLException {
        String result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;

            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

            if ((this.describeType != 208) || (this.rowSpaceByte[off] == 1)) {
                result = new String(this.rowSpaceByte, off + 2, len);

                long[] result1 = stringToRowid(result.getBytes(), 0, result.length());

                result = new String(rowidToString(result1));
            } else {
                result = kgrdub2c(this.rowSpaceByte, len, off + 2);
            }
        }

        return result;
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
            case -8:
                return getROWID(currentRow);
            }

            DatabaseError.throwSqlException(4);

            return null;
        }

        return result;
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

    static final byte[] rowidToString(long[] rowid) {
        long rba = rowid[0];

        long tidpid = rowid[1];

        long ridrba = rowid[2];

        long ridsqn = rowid[3];

        int size = 18;

        byte[] ret = new byte[size];
        int offset = 0;

        offset = kgrd42b(ret, rba, 6, offset);

        offset = kgrd42b(ret, tidpid, 3, offset);

        offset = kgrd42b(ret, ridrba, 6, offset);

        offset = kgrd42b(ret, ridsqn, 3, offset);

        return ret;
    }

    static final long[] rcToRowid(byte[] data, int offset, int length) throws SQLException {
        int size = 18;

        if (length != size) {
            throw new SQLException("Rowid size incorrect.");
        }

        long[] ret = new long[3];
        String value = new String(data, offset, length);

        long blockNumber = Long.parseLong(value.substring(0, 8), 16);
        long slotNumber = Long.parseLong(value.substring(9, 13), 16);
        long partitionID = Long.parseLong(value.substring(14, 8), 16);

        ret[0] = partitionID;
        ret[1] = blockNumber;
        ret[2] = slotNumber;

        return ret;
    }

    static final byte[] kgrdr2rc(long[] rowid) {
        long partitionID = rowid[1];

        long blockNumber = rowid[2];

        long slotNumber = rowid[3];
        int size = 18;
        byte[] ret = new byte[size];
        int offset = 0;

        offset = lmx42h(ret, blockNumber, 8, offset);
        ret[(offset++)] = 46;
        offset = lmx42h(ret, slotNumber, 4, offset);
        ret[(offset++)] = 46;
        offset = lmx42h(ret, partitionID, 4, offset);

        return ret;
    }

    static final int lmx42h(byte[] charsAsBytes, long value, int size, int offset) {
        String hexRep = Long.toHexString(value);

        int size_t = size;
        int hexRepOff = 0;
        do {
            if (hexRepOff < hexRep.length()) {
                charsAsBytes[(offset + size - 1)] = (byte) hexRep.charAt(hexRep.length()
                        - hexRepOff - 1);

                hexRepOff++;
            } else {
                charsAsBytes[(offset + size - 1)] = 48;
            }

            size--;
        } while (size > 0);

        return size_t + offset;
    }

    static final long[] stringToRowid(byte[] data, int offset, int length) throws SQLException {
        int size = 18;

        if (length != size) {
            throw new SQLException("Rowid size incorrect.");
        }

        long[] ret = new long[4];
        try {
            ret[0] = kgrdb42(data, 6, offset);

            offset += 6;

            ret[1] = kgrdb42(data, 3, offset);

            offset += 3;

            ret[2] = kgrdb42(data, 6, offset);

            offset += 6;

            ret[3] = kgrdb42(data, 3, offset);

            offset += 3;
        } catch (Exception e) {
            ret[0] = 0L;
            ret[1] = 0L;
            ret[2] = 0L;
            ret[3] = 0L;
        }

        return ret;
    }

    static final int kgrd42b(byte[] charsAsBytes, long value, int size, int offset) {
        int size_t = size;
        long val = value;

        for (; size > 0; size--) {
            charsAsBytes[(offset + size - 1)] = kgrd_basis_64[((int) val & 0x3F)];
            val = val >>> 6 & 0x3FFFFFF;
        }

        return size_t + offset;
    }

    static final long kgrdb42(byte[] charsAsBytes, int size, int offset) throws SQLException {
        long ret = 0L;

        for (int i = 0; i < size; i++) {
            byte value = charsAsBytes[(offset + i)];

            value = kgrd_index_64[value];

            if (value == -1) {
                throw new SQLException("Char data to rowid conversion failed.");
            }
            ret <<= 6;
            ret |= value;
        }

        return ret;
    }

    static final String kgrdub2c(byte[] bytes, int size, int offset) throws SQLException {
        int rid_type = bytes[offset];
        char[] chars = new char[512];
        int next_output_byte = 0;
        int bytes_to_convert = size - 1;
        int body_length = 4 * (size / 3) + (size % 3 == 0 ? size % 3 + 1 : 0);

        int char_length = 1 + body_length - 1;

        if (char_length != 0) {
            chars[0] = (char) kgrd_indbyte_char[(rid_type - 1)];

            int next_input_byte = offset + 1;

            next_output_byte = 1;

            byte second_byte = 0;

            while (bytes_to_convert > 0) {
                chars[(next_output_byte++)] = (char) kgrd_basis_64[((bytes[next_input_byte] & 0xFF) >> 2)];

                if (bytes_to_convert == 1) {
                    chars[(next_output_byte++)] = (char) kgrd_basis_64[((bytes[next_input_byte] & 0x3) << 4)];

                    break;
                }

                second_byte = (byte) (bytes[(next_input_byte + 1)] & 0xFF);

                chars[(next_output_byte++)] = (char) kgrd_basis_64[((bytes[next_input_byte] & 0x3) << 4 | (second_byte & 0xF0) >> 4)];

                if (bytes_to_convert == 2) {
                    chars[(next_output_byte++)] = (char) kgrd_basis_64[((second_byte & 0xF) << 2)];

                    break;
                }

                next_input_byte += 2;
                chars[(next_output_byte++)] = (char) kgrd_basis_64[((second_byte & 0xF) << 2 | (bytes[next_input_byte] & 0xC0) >> 6)];

                chars[next_output_byte] = (char) kgrd_basis_64[(bytes[next_input_byte] & 0x3F)];

                bytes_to_convert -= 3;
                next_input_byte++;
                next_output_byte++;
            }
        }

        return new String(chars, 0, next_output_byte);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CRowidAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CRowidAccessor JD-Core Version: 0.6.0
 */