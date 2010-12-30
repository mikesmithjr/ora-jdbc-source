package oracle.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public abstract interface OracleResultSetMetaData extends ResultSetMetaData {
    public abstract boolean isNCHAR(int paramInt) throws SQLException;
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.OracleResultSetMetaData JD-Core Version: 0.6.0
 */