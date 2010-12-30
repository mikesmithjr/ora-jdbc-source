package oracle.jdbc.driver;

import java.sql.SQLException;

class T2CResultSetAccessor extends ResultSetAccessor {
    T2CResultSetAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        super(stmt, max_len * 2, form, external_type, forBind);
    }

    T2CResultSetAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        super(stmt, max_len * 2, nullable, flags, precision, scale, contflag, total_elems, form);
    }

    byte[] getBytes(int currentRow) throws SQLException {
        byte[] result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int align = ((T2CConnection) this.statement.connection).byteAlign;
            int adjusted_columnIndex = this.columnIndex + (align - 1) & (align - 1 ^ 0xFFFFFFFF);

            int off = adjusted_columnIndex + len * currentRow;

            result = new byte[len];
            System.arraycopy(this.rowSpaceByte, off, result, 0, len);
        }

        return result;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T2CResultSetAccessor JD-Core Version: 0.6.0
 */