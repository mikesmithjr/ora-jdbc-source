package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetGB18030 extends CharacterSetWithConverter {
    static final int MAX_7BIT = 127;
    static Class m_charConvSuperclass;

    CharacterSetGB18030(int oracleId, CharacterConverters charConverter) {
        super(oracleId, charConverter);
    }

    static CharacterSetGB18030 getInstance(int oracleId, CharacterConverters charConverter) {
        if (charConverter.getGroupId() == 9) {
            return new CharacterSetGB18030(oracleId, charConverter);
        }

        return null;
    }

    int decode(CharacterWalker walker) throws SQLException {
        int c = walker.bytes[walker.next] & 0xFF;

        if (c > 127) {
            if (walker.bytes.length > walker.next + 1) {
                if (((walker.bytes[walker.next] & 0xFF) >= 129)
                        && ((walker.bytes[walker.next] & 0xFF) <= 254)
                        && ((walker.bytes[(walker.next + 1)] & 0xFF) >= 48)
                        && ((walker.bytes[(walker.next + 1)] & 0xFF) <= 57)) {
                    if (walker.bytes.length > walker.next + 3) {
                        if (((walker.bytes[(walker.next + 2)] & 0xFF) >= 129)
                                && ((walker.bytes[(walker.next + 2)] & 0xFF) <= 254)
                                && ((walker.bytes[(walker.next + 3)] & 0xFF) >= 48)
                                && ((walker.bytes[(walker.next + 3)] & 0xFF) <= 57)) {
                            c = (walker.bytes[walker.next] & 0xFF) << 24
                                    | (walker.bytes[(walker.next + 1)] & 0xFF) << 16
                                    | (walker.bytes[(walker.next + 2)] & 0xFF) << 8
                                    | walker.bytes[(walker.next + 3)] & 0xFF;

                            walker.next += 4;
                        } else {
                            c = walker.bytes[walker.next] & 0xFF;
                            walker.next += 1;
                        }
                    } else {
                        throw new SQLException("destination too small");
                    }

                } else {
                    c = (walker.bytes[walker.next] & 0xFF) << 8 | walker.bytes[(walker.next + 1)]
                            & 0xFF;

                    walker.next += 2;
                }
            } else {
                throw new SQLException("destination too small");
            }
        }

        return c;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        short bytesToShift = 0;
        short bytesNeeded = 0;

        while (c >> bytesToShift != 0) {
            bytesToShift = (short) (bytesToShift + 8);
            bytesNeeded = (short) (bytesNeeded + 1);
        }

        if (c >> 16 != 0) {
            bytesToShift = 3;
            bytesNeeded = 4;
        } else if (c >> 8 != 0) {
            bytesToShift = 1;
            bytesNeeded = 2;
        } else {
            bytesToShift = 0;
            bytesNeeded = 1;
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
 * oracle.sql.CharacterSetGB18030 JD-Core Version: 0.6.0
 */