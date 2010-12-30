package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.sql.Datum;
import oracle.sql.ROWID;

class RowidAccessor extends Accessor {
    static final int maxLength = 128;

    RowidAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, 104, 9, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    RowidAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        init(stmt, 104, 9, form, false);
        initForDescribe(104, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 128;

        this.byteLength = (this.internalTypeMaxLength + 2);
    }

    String getString(int currentRow) throws SQLException {
        String result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;

            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

            result = new String(this.rowSpaceByte, off + 2, len);
        }

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        return getROWID(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getROWID(currentRow);
    }

    ROWID getROWID(int currentRow) throws SQLException {
        byte[] b = getBytes(currentRow);

        return b == null ? null : new ROWID(b);
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

    Object getObject(int currentRow, Map map) throws SQLException {
        return getROWID(currentRow);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RowidAccessor JD-Core Version: 0.6.0
 */