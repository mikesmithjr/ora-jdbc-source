package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetJAEUC extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterJAEUC";
    static final transient short MAX_7BIT = 127;
    static final transient short LEADINGCODE = 143;
    static Class m_charConvSuperclass;

    CharacterSetJAEUC(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);
    }

    static CharacterSetJAEUC getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 2) {
            return new CharacterSetJAEUC(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        int c = walker.bytes[walker.next] & 0xFF;

        walker.next += 1;

        if (c > 127) {
            if (c != 143) {
                if (walker.bytes.length > walker.next) {
                    c = c << 8 | walker.bytes[walker.next];
                    walker.next += 1;
                }

            } else {
                for (int i = 0; i < 2; i++) {
                    if (walker.bytes.length <= walker.next)
                        continue;
                    c = c << 8 | walker.bytes[walker.next];
                    walker.next += 1;
                }
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
 * oracle.sql.CharacterSetJAEUC JD-Core Version: 0.6.0
 */