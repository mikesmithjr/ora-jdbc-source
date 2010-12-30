package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.Datum;
import oracle.sql.NUMBER;

class BinaryDoubleAccessor extends Accessor {
    static final int MAXLENGTH = 8;

    BinaryDoubleAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 101, 101, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    BinaryDoubleAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 101, 101, form, false);
        initForDescribe(101, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        int max_field_size = stmt.maxFieldSize;

        if ((max_field_size > 0) && ((max_len == 0) || (max_field_size < max_len))) {
            max_len = max_field_size;
        }
        initForDataAccess(0, max_len, null);
    }

    void init(OracleStatement stmt, int internal_type, int database_type, int max_len, short form,
            int external_type) throws SQLException {
        init(stmt, internal_type, database_type, form, false);
        initForDataAccess(external_type, max_len, null);
    }

    void init(OracleStatement stmt, int internal_type, int database_type, int max_len,
            boolean nullable, int flags, int precision, int scale, int contflag, int total_elems,
            short form) throws SQLException {
        init(stmt, internal_type, database_type, form, false);
        initForDescribe(internal_type, max_len, nullable, flags, precision, scale, contflag,
                        total_elems, form, null);

        int max_field_size = stmt.maxFieldSize;

        if ((max_field_size > 0) && ((max_len == 0) || (max_field_size < max_len))) {
            max_len = max_field_size;
        }
        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 8;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    double getDouble(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0.0D;
        }
        int off = this.columnIndex + this.byteLength * currentRow;
        int b0 = this.rowSpaceByte[off];
        int b1 = this.rowSpaceByte[(off + 1)];
        int b2 = this.rowSpaceByte[(off + 2)];
        int b3 = this.rowSpaceByte[(off + 3)];
        int b4 = this.rowSpaceByte[(off + 4)];
        int b5 = this.rowSpaceByte[(off + 5)];
        int b6 = this.rowSpaceByte[(off + 6)];
        int b7 = this.rowSpaceByte[(off + 7)];

        if ((b0 & 0x80) != 0) {
            b0 &= 127;
            b1 &= 255;
            b2 &= 255;
            b3 &= 255;
            b4 &= 255;
            b5 &= 255;
            b6 &= 255;
            b7 &= 255;
        } else {
            b0 = (b0 ^ 0xFFFFFFFF) & 0xFF;
            b1 = (b1 ^ 0xFFFFFFFF) & 0xFF;
            b2 = (b2 ^ 0xFFFFFFFF) & 0xFF;
            b3 = (b3 ^ 0xFFFFFFFF) & 0xFF;
            b4 = (b4 ^ 0xFFFFFFFF) & 0xFF;
            b5 = (b5 ^ 0xFFFFFFFF) & 0xFF;
            b6 = (b6 ^ 0xFFFFFFFF) & 0xFF;
            b7 = (b7 ^ 0xFFFFFFFF) & 0xFF;
        }

        int hiBits = b0 << 24 | b1 << 16 | b2 << 8 | b3;
        int loBits = b4 << 24 | b5 << 16 | b6 << 8 | b7;
        long longBits = hiBits << 32 | loBits & 0xFFFFFFFF;

        return Double.longBitsToDouble(longBits);
    }

    String getString(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return Double.toString(getDouble(currentRow));
        }
        return null;
    }

    Object getObject(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new Double(getDouble(currentRow));
        }
        return null;
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return new Double(getDouble(currentRow));
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getBINARY_DOUBLE(currentRow);
    }

    BINARY_DOUBLE getBINARY_DOUBLE(int currentRow) throws SQLException {
        BINARY_DOUBLE result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;
            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, off, data, 0, len);

            result = new BINARY_DOUBLE(data);
        }

        return result;
    }

    NUMBER getNUMBER(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new NUMBER(getDouble(currentRow));
        }
        return null;
    }

    BigInteger getBigInteger(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new BigInteger(getString(currentRow));
        }
        return null;
    }

    BigDecimal getBigDecimal(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new BigDecimal(getString(currentRow));
        }
        return null;
    }

    byte getByte(int currentRow) throws SQLException {
        return (byte) (int) getDouble(currentRow);
    }

    short getShort(int currentRow) throws SQLException {
        return (short) (int) getDouble(currentRow);
    }

    int getInt(int currentRow) throws SQLException {
        return (int) getDouble(currentRow);
    }

    long getLong(int currentRow) throws SQLException {
        return (long) getDouble(currentRow);
    }

    float getFloat(int currentRow) throws SQLException {
        return (float) getDouble(currentRow);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BinaryDoubleAccessor JD-Core Version: 0.6.0
 */