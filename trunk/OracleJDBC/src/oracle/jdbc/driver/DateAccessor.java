package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;

class DateAccessor extends DateTimeCommonAccessor {
    static final int maxLength = 7;

    DateAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, 12, 12, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    DateAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        init(stmt, 12, 12, form, false);
        initForDescribe(12, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 7;

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
            int off = this.columnIndex + this.byteLength * currentRow;
            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            result = year + "-" + toStr(this.rowSpaceByte[(2 + off)]) + "-"
                    + toStr(this.rowSpaceByte[(3 + off)]) + " "
                    + toStr(this.rowSpaceByte[(4 + off)] - 1) + ":"
                    + toStr(this.rowSpaceByte[(5 + off)] - 1) + ":"
                    + toStr(this.rowSpaceByte[(6 + off)] - 1) + ".0";
        }

        return result;
    }

    Timestamp getTimestamp(int currentRow) throws SQLException {
        Timestamp result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;

            TimeZone zone = this.statement.getDefaultTimeZone();

            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            if (year <= 0) {
                year++;
            }
            result = new Timestamp(getMillis(year, oracleMonth(off), oracleDay(off),
                                             oracleTime(off), zone));
        }

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        Object result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            if (this.externalType == 0) {
                if (this.statement.connection.v8Compatible)
                    result = getTimestamp(currentRow);
                else
                    result = getDate(currentRow);
            } else {
                switch (this.externalType) {
                case 91:
                    return getDate(currentRow);
                case 92:
                    return getTime(currentRow);
                case 93:
                    return getTimestamp(currentRow);
                }

                DatabaseError.throwSqlException(4);

                return null;
            }

        }

        return result;
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getDATE(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getObject(currentRow);
    }

    TIMESTAMP getTIMESTAMP(int currentRow) throws SQLException {
        if ((this.statement.connection.v8Compatible != true) || (this.externalType != 93)) {
            return super.getTIMESTAMP(currentRow);
        }

        TIMESTAMP result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;
            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, off, data, 0, len);

            result = new TIMESTAMP(data);
        }

        return result;
    }

    static String toStr(int x) {
        return x < 10 ? "0" + x : Integer.toString(x);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.DateAccessor JD-Core Version: 0.6.0
 */