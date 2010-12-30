package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.Datum;
import oracle.sql.NUMBER;

class BinaryFloatAccessor extends Accessor {
    static final int MAXLENGTH = 4;

    BinaryFloatAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 100, 100, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    BinaryFloatAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 100, 100, form, false);
        initForDescribe(100, max_len, nullable, flags, precision, scale, contflag, total_elems,
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
        this.internalTypeMaxLength = 4;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    float getFloat(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return 0.0F;
        }
        int off = this.columnIndex + this.byteLength * currentRow;
        int b0 = this.rowSpaceByte[off];
        int b1 = this.rowSpaceByte[(off + 1)];
        int b2 = this.rowSpaceByte[(off + 2)];
        int b3 = this.rowSpaceByte[(off + 3)];

        if ((b0 & 0x80) != 0) {
            b0 &= 127;
            b1 &= 255;
            b2 &= 255;
            b3 &= 255;
        } else {
            b0 = (b0 ^ 0xFFFFFFFF) & 0xFF;
            b1 = (b1 ^ 0xFFFFFFFF) & 0xFF;
            b2 = (b2 ^ 0xFFFFFFFF) & 0xFF;
            b3 = (b3 ^ 0xFFFFFFFF) & 0xFF;
        }

        int intBits = b0 << 24 | b1 << 16 | b2 << 8 | b3;

        return Float.intBitsToFloat(intBits);
    }

    String getString(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return Float.toString(getFloat(currentRow));
        }
        return null;
    }

    Object getObject(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new Float(getFloat(currentRow));
        }
        return null;
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new Float(getFloat(currentRow));
        }
        return null;
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getBINARY_FLOAT(currentRow);
    }

    BINARY_FLOAT getBINARY_FLOAT(int currentRow) throws SQLException {
        BINARY_FLOAT result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;
            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, off, data, 0, len);

            result = new BINARY_FLOAT(data);
        }

        return result;
    }

    NUMBER getNUMBER(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            return new NUMBER(getFloat(currentRow));
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
        return (byte) (int) getFloat(currentRow);
    }

    short getShort(int currentRow) throws SQLException {
        return (short) (int) getFloat(currentRow);
    }

    int getInt(int currentRow) throws SQLException {
        return (int) getFloat(currentRow);
    }

    long getLong(int currentRow) throws SQLException {
        return (long) getFloat(currentRow);
    }

    double getDouble(int currentRow) throws SQLException {
        return getFloat(currentRow);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BinaryFloatAccessor JD-Core Version: 0.6.0
 */