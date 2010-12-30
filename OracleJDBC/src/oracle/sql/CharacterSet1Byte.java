package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSet1Byte extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverter1Byte";
    static Class m_charConvSuperclass;

    CharacterSet1Byte(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);
    }

    static CharacterSet1Byte getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 0) {
            return new CharacterSet1Byte(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        int c = walker.bytes[walker.next] & 0xFF;

        walker.next += 1;

        return c;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        need(buffer, 1);

        if (c < 256) {
            buffer.bytes[buffer.next] = (byte) c;
            buffer.next += 1;
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSet1Byte JD-Core Version: 0.6.0
 */