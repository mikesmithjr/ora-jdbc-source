package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetZHTEUC extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterZHTEUC";
    static final int MAX_7BIT = 127;
    static final int CHARLENGTH = 4;
    static Class m_charConvSuperclass;
    char[] m_leadingCodes;

    CharacterSetZHTEUC(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);

        this.m_leadingCodes = charConverter.getLeadingCodes();
    }

    static CharacterSetZHTEUC getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 5) {
            return new CharacterSetZHTEUC(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        if (walker.next + 1 < walker.bytes.length) {
            int leadingCode = walker.bytes[walker.next] << 8 | walker.bytes[(walker.next + 1)];

            for (int i = 0; i < this.m_leadingCodes.length; i++) {
                if (leadingCode != this.m_leadingCodes[i]) {
                    continue;
                }
                if (walker.bytes.length - walker.next < 4) {
                    throw new SQLException("destination too small");
                }

                int c = 0;

                for (int j = 0; j < 4; j++) {
                    c = c << 8 | walker.bytes[(walker.next++)];
                }

                return c;
            }

        }

        int c = walker.bytes[walker.next] & 0xFF;

        walker.next += 1;

        if (c > 127) {
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
        int leadingCode = c >> 16;

        for (int i = 0; i < this.m_leadingCodes.length; i++) {
            if (leadingCode != this.m_leadingCodes[i]) {
                continue;
            }
            need(buffer, 4);

            for (int j = 0; j < 4; j++) {
                buffer.bytes[(buffer.next++)] = (byte) c;
                c >>= 8;
            }

            return;
        }

        throw new SQLException();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetZHTEUC JD-Core Version: 0.6.0
 */