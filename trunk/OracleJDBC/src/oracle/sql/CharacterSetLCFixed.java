package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetLCFixed extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterLCFixed";
    static final int CHARLENGTH = 4;
    static Class m_charConvSuperclass;
    char[] m_leadingCodes;

    CharacterSetLCFixed(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);

        this.m_leadingCodes = charConverter.getLeadingCodes();
    }

    static CharacterSetLCFixed getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 3) {
            return new CharacterSetLCFixed(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        if (walker.bytes.length - walker.next < 4) {
            throw new SQLException("destination too small");
        }

        int leadingCode = walker.bytes[walker.next] << 8 | walker.bytes[(walker.next + 1)];

        for (int i = 0; i < this.m_leadingCodes.length; i++) {
            if (leadingCode != this.m_leadingCodes[i]) {
                continue;
            }
            int c = 0;

            for (int j = 0; j < 4; j++) {
                c = c << 8 | walker.bytes[(walker.next++)];
            }

            return c;
        }

        throw new SQLException("Leading code invalid");
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        int leadingCode = c >> 16;

        for (int i = 0; i < this.m_leadingCodes.length; i++) {
            if (leadingCode != this.m_leadingCodes[i]) {
                continue;
            }
            need(buffer, 4);

            for (int j = 3; j >= 0; j--) {
                buffer.bytes[(buffer.next++)] = (byte) (c >> 8 * j & 0xFF);
            }

            return;
        }

        throw new SQLException("Leading code invalid");
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetLCFixed JD-Core Version: 0.6.0
 */