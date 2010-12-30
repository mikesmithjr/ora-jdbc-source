package oracle.jdbc.driver;

import java.sql.SQLException;

class CharAccessor extends CharCommonAccessor {
    CharAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        int maxLength = 2000;

        if (stmt.sqlKind == 1) {
            maxLength = 32512;
        }
        init(stmt, 96, 9, max_len, form, external_type, forBind, maxLength, 255);
    }

    CharAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        int maxLength = 2000;

        if (stmt.sqlKind == 1) {
            maxLength = 32512;
        }
        init(stmt, 96, 9, max_len, nullable, flags, precision, scale, contflag, total_elems, form,
             maxLength, 255);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.CharAccessor JD-Core Version: 0.6.0
 */