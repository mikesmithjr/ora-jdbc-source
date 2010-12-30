package oracle.jdbc.driver;

import java.sql.SQLException;

class NumberAccessor extends NumberCommonAccessor {
    NumberAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, max_len, form, external_type, forBind);
    }

    NumberAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        init(stmt, 2, max_len, nullable, flags, precision, scale, contflag, total_elems, form);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.NumberAccessor JD-Core Version: 0.6.0
 */