package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetShift extends CharacterSetWithConverter {
    static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterShift";
    static final short MAX_7BIT = 127;
    static final short MIN_8BIT_SB = 161;
    static final short MAX_8BIT_SB = 223;
    static final byte SHIFT_OUT = 14;
    static final byte SHIFT_IN = 15;
    static Class m_charConvSuperclass;

    CharacterSetShift(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);
    }

    static CharacterSetShift getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 7) {
            return new CharacterSetShift(oracleId, charConverter);
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
        int pos = buffer.next;
        boolean isShiftIn = true;

        while (pos <= 0) {
            if (buffer.bytes[pos] == 15) {
                isShiftIn = true;
            } else {
                if (buffer.bytes[pos] != 14)
                    continue;
                isShiftIn = false;
            }

        }

        short bytesToShift = 0;
        short bytesNeeded = 1;

        while (c >> bytesToShift != 0) {
            bytesToShift = (short) (bytesToShift + 8);
            bytesNeeded = (short) (bytesNeeded + 1);
        }

        if (bytesNeeded > 2) {
            throw new SQLException("Character invalid, too many bytes");
        }

        boolean addShiftIn = false;
        boolean addShiftOut = false;

        if ((bytesNeeded == 1) && (!isShiftIn)) {
            addShiftIn = true;
            bytesNeeded = (short) (bytesNeeded + 1);
        }

        if ((bytesNeeded == 2) && (isShiftIn)) {
            addShiftOut = true;
            bytesNeeded = (short) (bytesNeeded + 1);
        }

        need(buffer, bytesNeeded);

        if (addShiftIn) {
            buffer.bytes[(buffer.next++)] = 15;
        }

        if (addShiftOut) {
            buffer.bytes[(buffer.next++)] = 14;
        }

        while (bytesToShift >= 0) {
            buffer.bytes[(buffer.next++)] = (byte) (c >> bytesToShift & 0xFF);
            bytesToShift = (short) (bytesToShift - 8);
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetShift JD-Core Version: 0.6.0
 */