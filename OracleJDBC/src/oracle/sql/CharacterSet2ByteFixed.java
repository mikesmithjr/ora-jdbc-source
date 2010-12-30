package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSet2ByteFixed extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverter2ByteFixed";
    static final short MAX_7BIT = 127;
    static final short MIN_8BIT_SB = 161;
    static final short MAX_8BIT_SB = 223;
    static final short CHARLENGTH = 2;
    static Class m_charConvSuperclass;

    CharacterSet2ByteFixed(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);
    }

    static CharacterSet2ByteFixed getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 6) {
            return new CharacterSet2ByteFixed(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        int c = walker.bytes[walker.next] & 0xFF;

        walker.next += 1;

        if (walker.bytes.length > walker.next) {
            c = c << 8 | walker.bytes[walker.next];
            walker.next += 1;
        } else {
            throw new SQLException("destination too small");
        }

        return c;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        need(buffer, 2);

        buffer.bytes[(buffer.next++)] = (byte) (c >> 8 & 0xFF);
        buffer.bytes[(buffer.next++)] = (byte) (c & 0xFF);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSet2ByteFixed JD-Core Version: 0.6.0
 */