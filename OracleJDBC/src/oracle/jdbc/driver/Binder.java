package oracle.jdbc.driver;

import java.sql.SQLException;

abstract class Binder {
    short type;
    int bytelen;

    abstract Binder copyingBinder();

    abstract void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1,
            int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar,
            short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7,
            int paramInt8, int paramInt9, boolean paramBoolean) throws SQLException;

    public String toString() {
        return getClass() + " [type = " + this.type + ", bytelen = " + this.bytelen + "]";
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.Binder JD-Core Version: 0.6.0
 */