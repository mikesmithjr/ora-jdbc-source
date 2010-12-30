package oracle.jdbc.driver;

import java.sql.SQLException;

class VarcharAccessor extends CharCommonAccessor {
    VarcharAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        int maxLength = 4000;

        if (stmt.sqlKind == 1) {
            maxLength = 32512;
        }
        init(stmt, 1, 9, max_len, form, external_type, forBind, maxLength, 2000);
    }

    VarcharAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        int maxLength = 4000;

        if (stmt.sqlKind == 1) {
            maxLength = 32512;
        }
        init(stmt, 1, 9, max_len, nullable, flags, precision, scale, contflag, total_elems, form,
             maxLength, 2000);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.VarcharAccessor JD-Core Version: 0.6.0
 */