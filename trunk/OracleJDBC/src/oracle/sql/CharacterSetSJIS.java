package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetSJIS extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterSJIS";
    static final short MAX_7BIT = 127;
    static final short MIN_8BIT_SB = 161;
    static final short MAX_8BIT_SB = 223;
    static Class m_charConvSuperclass;

    CharacterSetSJIS(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);
    }

    static CharacterSetSJIS getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 4) {
            return new CharacterSetSJIS(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        int c = walker.bytes[walker.next] & 0xFF;

        walker.next += 1;

        if ((c > 223) || ((c > 127) && (c < 161))) {
            if (walker.bytes.length > walker.next) {
                c = c << 8 | walker.bytes[walker.next];
                walker.next += 1;
            } else {
                throw new SQLException("destination too small");
            }
        }

        return c;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        short bytesToShift = 0;
        short bytesNeeded = 1;

        while (c >> bytesToShift != 0) {
            bytesToShift = (short) (bytesToShift + 8);
            bytesNeeded = (short) (bytesNeeded + 1);
        }

        need(buffer, bytesNeeded);

        while (bytesToShift >= 0) {
            buffer.bytes[(buffer.next++)] = (byte) (c >> bytesToShift & 0xFF);
            bytesToShift = (short) (bytesToShift - 8);
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetSJIS JD-Core Version: 0.6.0
 */