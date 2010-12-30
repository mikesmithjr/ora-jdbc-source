package oracle.jdbc.internal;

import java.sql.SQLException;

public abstract interface OracleCallableStatement extends oracle.jdbc.OracleCallableStatement,
        OraclePreparedStatement {
    public abstract byte[] privateGetBytes(int paramInt) throws SQLException;
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.internal.OracleCallableStatement JD-Core Version: 0.6.0
 */