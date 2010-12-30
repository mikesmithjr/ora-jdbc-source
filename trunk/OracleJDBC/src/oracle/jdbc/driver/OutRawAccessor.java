package oracle.jdbc.driver;

import java.sql.SQLException;

class OutRawAccessor extends RawCommonAccessor {
    static final int MAXLENGTH_NEW = 32767;
    static final int MAXLENGTH_OLD = 32767;

    OutRawAccessor(OracleStatement stmt, int max_len, short form, int external_type)
            throws SQLException {
        init(stmt, 23, 23, form, true);
        initForDataAccess(external_type, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        if (this.statement.connection.getVersionNumber() >= 8000)
            this.internalTypeMaxLength = 32767;
        else {
            this.internalTypeMaxLength = 32767;
        }
        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
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

            System.arraycopy(this.rowSpaceByte, off, result, 0, len);
        }

        return result;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OutRawAccessor JD-Core Version: 0.6.0
 */