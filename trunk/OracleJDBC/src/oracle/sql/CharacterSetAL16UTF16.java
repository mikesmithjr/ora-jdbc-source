package oracle.sql;

import java.sql.SQLException;

class CharacterSetAL16UTF16 extends CharacterSet implements CharacterRepConstants {
    CharacterSetAL16UTF16(int id) {
        super(id);

        this.rep = 4;
    }

    public boolean isLossyFrom(CharacterSet from) {
        return !from.isUnicode();
    }

    public boolean isConvertibleFrom(CharacterSet source) {
        boolean ok = source.rep <= 1024;

        return ok;
    }

    public boolean isUnicode() {
        return true;
    }

    public String toStringWithReplacement(byte[] bytes, int offset, int count) {
        try {
            char[] chars = new char[count >>> 1];
            int chars_len = CharacterSet.convertAL16UTF16BytesToJavaChars(bytes, offset, chars, 0,
                                                                          count, true);

            return new String(chars, 0, chars_len);
        } catch (SQLException e) {
        }

        return "";
    }

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        try {
            char[] chars = new char[count >>> 1];

            int chars_len = CharacterSet.convertAL16UTF16BytesToJavaChars(bytes, offset, chars, 0,
                                                                          count, false);

            return new String(chars, 0, chars_len);
        } catch (SQLException e) {
            failUTFConversion();
        }
        return "";
    }

    public byte[] convert(String s) throws SQLException {
        return stringToAL16UTF16Bytes(s);
    }

    public byte[] convertWithReplacement(String s) {
        return stringToAL16UTF16Bytes(s);
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        byte[] result;
        if (from.rep == 4) {
            result = useOrCopy(source, offset, count);
        } else {
            String s = from.toString(source, offset, count);

            result = stringToAL16UTF16Bytes(s);
        }

        return result;
    }

    int decode(CharacterWalker walker) throws SQLException {
        byte[] bytes = walker.bytes;
        int bytes_index = walker.next;
        int bytes_end = walker.end;

        if (bytes_index + 2 >= bytes_end) {
            failUTFConversion();
        }

        byte c1 = bytes[(bytes_index++)];
        byte c2 = bytes[(bytes_index++)];
        int result = c1 << 8 & 0xFF00 | c2;
        walker.next = bytes_index;

        return result;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        if (c > 65535) {
            failUTFConversion();
        } else {
            need(buffer, 2);

            buffer.bytes[(buffer.next++)] = (byte) (c >> 8 & 0xFF);
            buffer.bytes[(buffer.next++)] = (byte) (c & 0xFF);
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetAL16UTF16 JD-Core Version: 0.6.0
 */