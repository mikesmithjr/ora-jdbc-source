package oracle.sql;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

class CharacterSetByte extends CharacterSet implements CharacterRepConstants {
    CharacterSetByte(int id) {
        super(id);

        this.rep = 1;
    }

    public boolean isLossyFrom(CharacterSet from) {
        return from.rep != 1;
    }

    public boolean isConvertibleFrom(CharacterSet source) {
        return source.rep <= 1024;
    }

    private String toString(byte[] bytes, int offset, int count, char replacement)
            throws SQLException {
        try {
            return new String(bytes, offset, count, "ASCII");
        } catch (UnsupportedEncodingException ex) {
        }

        throw new SQLException("ascii not supported");
    }

    public String toStringWithReplacement(byte[] bytes, int offset, int count) {
        try {
            return toString(bytes, offset, count, '?');
        } catch (SQLException ex) {
        }

        throw new Error("CharacterSetByte.toString");
    }

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        return toString(bytes, offset, count, '\000');
    }

    public byte[] convert(String s) throws SQLException {
        int sLength = s.length();
        char[] chars = new char[s.length()];

        s.getChars(0, sLength, chars, 0);

        return charsToBytes(chars, (byte)0);
    }

    public byte[] convertWithReplacement(String s) {
        int sLength = s.length();
        char[] chars = new char[s.length()];

        s.getChars(0, sLength, chars, 0);
        try {
            return charsToBytes(chars, (byte) 63);
        } catch (SQLException ex) {
        }

        return new byte[0];
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        byte[] result;
        if (from.rep == 1) {
            result = useOrCopy(source, offset, count);
        } else {
            if (from.rep == 2) {
                char[] chars = CharacterSetUTF.UTFToJavaChar(source, offset, count);

                result = charsToBytes(chars, (byte) 0);
            } else {
                String s = from.toString(source, offset, count);
                char[] chars = s.toCharArray();

                result = charsToBytes(chars, (byte) 0);
            }
        }
        return result;
    }

    int decode(CharacterWalker walker) {
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

    static byte[] charsToBytes(char[] chars, byte replacement) throws SQLException {
        byte[] bytes = new byte[chars.length];

        for (int x = 0; x < chars.length; x++) {
            if (chars[x] > 'ÿ') {
                bytes[x] = replacement;

                if (replacement != 0)
                    continue;
                failCharacterConversion(CharacterSet.make(31));
            } else {
                bytes[x] = (byte) chars[x];
            }
        }

        return bytes;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetByte JD-Core Version: 0.6.0
 */