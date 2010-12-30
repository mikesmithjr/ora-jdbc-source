package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;

class TimestampAccessor extends DateTimeCommonAccessor {
    TimestampAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 180, 180, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    TimestampAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 180, 180, form, false);
        initForDescribe(180, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 11;

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
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;
            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            int nanos = 0;

            if (len == 11) {
                nanos = oracleNanos(off);
            }

            result = year + "-" + this.rowSpaceByte[(2 + off)] + "-" + this.rowSpaceByte[(3 + off)]
                    + "." + (this.rowSpaceByte[(4 + off)] - 1) + "."
                    + (this.rowSpaceByte[(5 + off)] - 1) + ". "
                    + (this.rowSpaceByte[(6 + off)] - 1) + ". " + nanos;
        }

        return result;
    }

    Timestamp getTimestamp(int currentRow) throws SQLException {
        Timestamp result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;

            TimeZone zone = this.statement.getDefaultTimeZone();

            int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                    + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

            if (year <= 0) {
                year++;
            }
            result = new Timestamp(getMillis(year, oracleMonth(off), oracleDay(off),
                                             oracleTime(off), zone));

            if (len == 11) {
                result.setNanos(oracleNanos(off));
            }

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
                if (this.statement.connection.j2ee13Compliant) {
                    result = getTimestamp(currentRow);
                } else {
                    result = getTIMESTAMP(currentRow);
                }
            } else {
                switch (this.externalType) {
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
        return getTIMESTAMP(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getObject(currentRow);
    }

    TIMESTAMP getTIMESTAMP(int currentRow) throws SQLException {
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
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TimestampAccessor JD-Core Version: 0.6.0
 */