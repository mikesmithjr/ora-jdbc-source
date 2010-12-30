package oracle.sql;

import java.sql.SQLException;

class CharacterSetUTF extends CharacterSet implements CharacterRepConstants {
    private static int[] m_byteLen = { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 2, 2, 3, 0 };

    CharacterSetUTF(int id) {
        super(id);

        this.rep = 2;
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

            int chars_len = CharacterSet.convertUTFBytesToJavaChars(bytes, offset, chars, 0,
                                                                    countArr, true);

            return new String(chars, 0, chars_len);
        } catch (SQLException e) {
        }

        return "";
    }

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        try {
            char[] chars = new char[bytes.length];
            int[] countArr = new int[1];

            countArr[0] = count;

            int chars_len = CharacterSet.convertUTFBytesToJavaChars(bytes, offset, chars, 0,
                                                                    countArr, false);

            return new String(chars, 0, chars_len);
        } catch (SQLException e) {
            failUTFConversion();
        }
        return "";
    }

    public byte[] convertWithReplacement(String str) {
        return stringToUTF(str);
    }

    public byte[] convert(String str) throws SQLException {
        return stringToUTF(str);
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        byte[] result;
        if (from.rep == 2) {
            result = useOrCopy(source, offset, count);
        } else {
            String s = from.toString(source, offset, count);

            result = stringToUTF(s);
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

        if ((len == 3) && (isHiSurrogate((byte) c, bytes[(bytes_index + 1)]))
                && (bytes_index + 5 < bytes_end)) {
            len = 6;
        }

        try {
            char[] chars = new char[2];
            int[] lenArr = new int[1];

            lenArr[0] = len;

            int chars_len = CharacterSet.convertUTFBytesToJavaChars(bytes, bytes_index, chars, 0,
                                                                    lenArr, false);

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
            need(buffer, 6);

            char[] chars = { (char) (c >>> 16), (char) c };

            n = CharacterSet.convertJavaCharsToUTFBytes(chars, 0, buffer.bytes, buffer.next, 2);
        } else {
            need(buffer, 3);

            char[] chars = { (char) c };

            n = CharacterSet.convertJavaCharsToUTFBytes(chars, 0, buffer.bytes, buffer.next, 1);
        }

        buffer.next += n;
    }

    private static int getUTFByteLength(byte b) {
        return m_byteLen[(b >>> 4 & 0xF)];
    }

    private static boolean isHiSurrogate(byte b, byte b2) {
        return (b == -19) && (b2 >= -96);
    }

    public int encodedByteLength(String s) {
        return CharacterSet.stringUTFLength(s);
    }

    public int encodedByteLength(char[] carray) {
        return CharacterSet.charArrayUTF8Length(carray);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetUTF JD-Core Version: 0.6.0
 */