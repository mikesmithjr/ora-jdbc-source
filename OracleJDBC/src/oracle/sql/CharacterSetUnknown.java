package oracle.sql;

import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

class CharacterSetUnknown extends CharacterSet implements CharacterRepConstants {
    CharacterSetUnknown(int id) {
        super(id);

        this.rep = (1024 + id);
    }

    public boolean isLossyFrom(CharacterSet from) {
        return from.getOracleId() != getOracleId();
    }

    public boolean isConvertibleFrom(CharacterSet source) {
        return source.getOracleId() == getOracleId();
    }

    public String toStringWithReplacement(byte[] bytes, int offset, int count) {
        return "???";
    }

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        failCharsetUnknown(this);

        return null;
    }

    public byte[] convert(String s) throws SQLException {
        failCharsetUnknown(this);

        return null;
    }

    public byte[] convertWithReplacement(String s) {
        return new byte[0];
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        if (from.getOracleId() == getOracleId()) {
            return useOrCopy(source, offset, count);
        }

        failCharsetUnknown(this);

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        failCharsetUnknown(this);

        return 0;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        failCharsetUnknown(this);
    }

    static void failCharsetUnknown(CharacterSet what) throws SQLException {
        DatabaseError.throwSqlException(56, what);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetUnknown JD-Core Version: 0.6.0
 */