package oracle.sql;

import java.sql.SQLException;

class CharacterSetAL32UTF8 extends CharacterSet implements CharacterRepConstants {
    private static int[] m_byteLen = { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 3, 4 };

    CharacterSetAL32UTF8(int id) {
        super(id);

        this.rep = 6;
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
            char[] chars = new char[bytes.length];

            int[] countArr = new int[1];

            countArr[0] = count;

            int chars_index = CharacterSet.convertAL32UTF8BytesToJavaChars(bytes, offset, chars, 0,
                                                                           countArr, true);

            return new String(chars, 0, chars_index);
        } catch (SQLException e) {
        }

        return "";
    }

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        try {
            char[] chars = new char[bytes.length];
            int[] countArr = new int[1];

            countArr[0] = count;

            int clen = CharacterSet.convertAL32UTF8BytesToJavaChars(bytes, offset, chars, 0,
                                                                    countArr, false);

            return new String(chars, 0, clen);
        } catch (SQLException e) {
            failUTFConversion();
        }
        return "";
    }

    public byte[] convertWithReplacement(String str) {
        return stringToAL32UTF8(str);
    }

    public byte[] convert(String str) throws SQLException {
        return stringToAL32UTF8(str);
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        byte[] result;
        if (from.rep == 6) {
            result = useOrCopy(source, offset, count);
        } else {
            String s = from.toString(source, offset, count);

            result = stringToAL32UTF8(s);
        }

        return result;
    }

    int decode(CharacterWalker walker) throws SQLException {
        byte[] bytes = walker.bytes;
        int bytes_index = walker.next;
        int bytes_end = walker.end;

        if (bytes_index >= bytes_end) {
            failUTFConversion();
        }

        int c = bytes[bytes_index];
        int len = getUTFByteLength((byte) c);

        if ((len == 0) || (bytes_index + (len - 1) >= bytes_end)) {
            failUTFConversion();
        }

        try {
            char[] chars = new char[2];
            int[] lenArr = new int[1];

            lenArr[0] = len;

            int chars_len = CharacterSet.convertAL32UTF8BytesToJavaChars(bytes, bytes_index, chars,
                                                                         0, lenArr, false);

            walker.next += len;

            if (chars_len == 1) {
                return chars[0];
            }

            return chars[0] << '\020' | chars[1];
        } catch (SQLException e) {
            failUTFConversion();
        }

        return 0;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        int n;
        if ((c & 0xFFFF0000) != 0) {
            need(buffer, 4);

            char[] chars = { (char) (c >>> 16), (char) c };

            n = CharacterSet
                    .convertJavaCharsToAL32UTF8Bytes(chars, 0, buffer.bytes, buffer.next, 2);
        } else {
            need(buffer, 3);

            char[] chars = { (char) c };

            n = CharacterSet
                    .convertJavaCharsToAL32UTF8Bytes(chars, 0, buffer.bytes, buffer.next, 1);
        }

        buffer.next += n;
    }

    private static int getUTFByteLength(byte b) {
        return m_byteLen[(b >>> 4 & 0xF)];
    }

    public int encodedByteLength(String s) {
        return CharacterSet.string32UTF8Length(s);
    }

    public int encodedByteLength(char[] carray) {
        return CharacterSet.charArray32UTF8Length(carray);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetAL32UTF8 JD-Core Version: 0.6.0
 */