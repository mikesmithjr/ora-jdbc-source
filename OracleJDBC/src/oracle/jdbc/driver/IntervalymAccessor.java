package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.sql.Datum;
import oracle.sql.INTERVALYM;

class IntervalymAccessor extends Accessor {
    static final int maxLength = 5;

    IntervalymAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 182, 182, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    IntervalymAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 182, 182, form, false);
        initForDescribe(182, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 5;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    String getString(int currentRow) throws SQLException {
        String result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int offset = this.columnIndex + this.byteLength * currentRow;
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

            byte[] data = new byte[len];
            System.arraycopy(this.rowSpaceByte, offset, data, 0, len);

            result = new INTERVALYM(data).toString();
        }

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        return getINTERVALYM(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getINTERVALYM(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getINTERVALYM(currentRow);
    }

    INTERVALYM getINTERVALYM(int currentRow) throws SQLException {
        INTERVALYM result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int offset = this.columnIndex + this.byteLength * currentRow;
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, offset, data, 0, len);

            result = new INTERVALYM(data);
        }

        return result;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.IntervalymAccessor JD-Core Version: 0.6.0
 */