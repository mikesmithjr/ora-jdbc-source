package oracle.jdbc.driver;

import java.sql.SQLException;

class RawAccessor extends RawCommonAccessor {
    static final int MAXLENGTH_NEW = 2000;
    static final int MAXLENGTH_OLD = 255;

    RawAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, 23, 15, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    RawAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        init(stmt, 23, 15, form, false);
        initForDescribe(23, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

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
        if (this.statement.connection.getVersionNumber() >= 8000)
            this.internalTypeMaxLength = 2000;
        else {
            this.internalTypeMaxLength = 255;
        }
        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = (this.internalTypeMaxLength + 2);
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

            System.arraycopy(this.rowSpaceByte, off + 2, result, 0, len);
        }

        return result;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RawAccessor JD-Core Version: 0.6.0
 */