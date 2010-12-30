package oracle.sql;

import java.sql.SQLException;

class CharacterSetUTFE extends CharacterSet implements CharacterRepConstants {
    static final int MAXBYTEPERCHAR = 4;
    static byte[][] utf8m2utfe = {
            { 0, 1, 2, 3, 55, 45, 46, 47, 22, 5, 21, 11, 12, 13, 14, 15 },
            { 16, 17, 18, 19, 60, 61, 50, 38, 24, 25, 63, 39, 28, 29, 30, 31 },
            { 64, 90, 127, 123, 91, 108, 80, 125, 77, 93, 92, 78, 107, 96, 75, 97 },
            { -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, 122, 94, 76, 126, 110, 111 },
            { 124, -63, -62, -61, -60, -59, -58, -57, -56, -55, -47, -46, -45, -44, -43, -42 },
            { -41, -40, -39, -30, -29, -28, -27, -26, -25, -24, -23, -83, -32, -67, 95, 109 },
            { 121, -127, -126, -125, -124, -123, -122, -121, -120, -119, -111, -110, -109, -108,
                    -107, -106 },
            { -105, -104, -103, -94, -93, -92, -91, -90, -89, -88, -87, -64, 79, -48, -95, 7 },
            { 32, 33, 34, 35, 36, 37, 6, 23, 40, 41, 42, 43, 44, 9, 10, 27 },
            { 48, 49, 26, 51, 52, 53, 54, 8, 56, 57, 58, 59, 4, 20, 62, -1 },
            { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 81, 82, 83, 84, 85, 86 },
            { 87, 88, 89, 98, 99, 100, 101, 102, 103, 104, 105, 106, 112, 113, 114, 115 },
            { 116, 117, 118, 119, 120, -128, -118, -117, -116, -115, -114, -113, -112, -102, -101,
                    -100 },
            { -99, -98, -97, -96, -86, -85, -84, -82, -81, -80, -79, -78, -77, -76, -75, -74 },
            { -73, -72, -71, -70, -69, -68, -66, -65, -54, -53, -52, -51, -50, -49, -38, -37 },
            { -36, -35, -34, -33, -31, -22, -21, -20, -19, -18, -17, -6, -5, -4, -3, -2 } };

    static byte[][] utfe2utf8m = {
            { 0, 1, 2, 3, -100, 9, -122, 127, -105, -115, -114, 11, 12, 13, 14, 15 },
            { 16, 17, 18, 19, -99, 10, 8, -121, 24, 25, -110, -113, 28, 29, 30, 31 },
            { -128, -127, -126, -125, -124, -123, 23, 27, -120, -119, -118, -117, -116, 5, 6, 7 },
            { -112, -111, 22, -109, -108, -107, -106, 4, -104, -103, -102, -101, 20, 21, -98, 26 },
            { 32, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, 46, 60, 40, 43, 124 },
            { 38, -86, -85, -84, -83, -82, -81, -80, -79, -78, 33, 36, 42, 41, 59, 94 },
            { 45, 47, -77, -76, -75, -74, -73, -72, -71, -70, -69, 44, 37, 95, 62, 63 },
            { -68, -67, -66, -65, -64, -63, -62, -61, -60, 96, 58, 35, 64, 39, 61, 34 },
            { -59, 97, 98, 99, 100, 101, 102, 103, 104, 105, -58, -57, -56, -55, -54, -53 },
            { -52, 106, 107, 108, 109, 110, 111, 112, 113, 114, -51, -50, -49, -48, -47, -46 },
            { -45, 126, 115, 116, 117, 118, 119, 120, 121, 122, -44, -43, -42, 88, -41, -40 },
            { -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, 93, -26, -25 },
            { 123, 65, 66, 67, 68, 69, 70, 71, 72, 73, -24, -23, -22, -21, -20, -19 },
            { 13, 74, 75, 76, 77, 78, 79, 80, 81, 82, -18, -17, -16, -15, -14, -13 },
            { 92, -12, 83, 84, 85, 86, 87, 88, 89, 90, -11, -10, -9, -8, -7, -6 },
            { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, -5, -4, -3, -2, -1, -97 } };

    private static int[] m_byteLen = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 2, 2, 3, 4 };

    CharacterSetUTFE(int id) {
        super(id);

        this.rep = 3;
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

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        try {
            char[] chars = new char[bytes.length];
            int chars_index = UTFEToJavaChar(bytes, offset, count, chars,
                                             CharacterSet.CharacterConverterBehavior.REPORT_ERROR);

            return new String(chars, 0, chars_index);
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }

    }

    public String toStringWithReplacement(byte[] bytes, int offset, int count) {
        try {
            char[] chars = new char[bytes.length];
            int chars_index = UTFEToJavaChar(bytes, offset, count, chars,
                                             CharacterSet.CharacterConverterBehavior.REPLACEMENT);

            return new String(chars, 0, chars_index);
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    int UTFEToJavaChar(byte[] bytes, int offset, int count, char[] chars,
            CharacterSet.CharacterConverterBehavior ccb) throws SQLException {
        int bytes_index = offset;
        int bytes_end = offset + count;
        int chars_index = 0;

        while (bytes_index < bytes_end) {
            byte c1 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];

            switch (c1 >>> 4 & 0xF) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                chars[(chars_index++)] = (char) (c1 & 0x7F);

                break;
            case 8:
            case 9:
                chars[(chars_index++)] = (char) (c1 & 0x1F);

                break;
            case 12:
            case 13:
                if (bytes_index >= bytes_end) {
                    ccb.onFailConversion();

                    bytes_index = bytes_end;

                    continue;
                }

                c1 = (byte) (c1 & 0x1F);
                byte c2 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];

                if (!is101xxxxx(c2)) {
                    ccb.onFailConversion();

                    chars[(chars_index++)] = 65533;

                    continue;
                }

                chars[(chars_index++)] = (char) (c1 << 5 | c2 & 0x1F);

                break;
            case 14:
                if (bytes_index + 1 >= bytes_end) {
                    ccb.onFailConversion();

                    bytes_index = bytes_end;

                    continue;
                }

                c1 = (byte) (c1 & 0xF);
                byte c21 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];
                byte c3 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];

                if ((!is101xxxxx(c21)) || (!is101xxxxx(c3))) {
                    ccb.onFailConversion();

                    chars[(chars_index++)] = 65533;

                    continue;
                }

                chars[(chars_index++)] = (char) (c1 << 10 | (c21 & 0x1F) << 5 | c3 & 0x1F);

                break;
            case 15:
                if (bytes_index + 2 >= bytes_end) {
                    ccb.onFailConversion();

                    bytes_index = bytes_end;

                    continue;
                }

                c1 = (byte) (c1 & 0x1);
                byte c211 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];
                byte c31 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];
                byte c4 = utfe2utf8m[high(bytes[bytes_index])][low(bytes[(bytes_index++)])];

                if ((!is101xxxxx(c211)) || (!is101xxxxx(c31)) || (!is101xxxxx(c4))) {
                    ccb.onFailConversion();

                    chars[(chars_index++)] = 65533;

                    continue;
                }

                chars[(chars_index++)] = (char) (c1 << 15 | (c211 & 0x1F) << 10 | (c31 & 0x1F) << 5 | c4 & 0x1F);

                break;
            case 10:
            case 11:
            default:
                ccb.onFailConversion();

                chars[(chars_index++)] = 65533;
            }

        }

        return chars_index;
    }

    public byte[] convertWithReplacement(String str) {
        char[] chararr = str.toCharArray();
        byte[] bytearr = new byte[chararr.length * 4];

        int byte_len = javaCharsToUTFE(chararr, 0, chararr.length, bytearr, 0);
        byte[] rbytearr = new byte[byte_len];

        System.arraycopy(bytearr, 0, rbytearr, 0, byte_len);

        return rbytearr;
    }

    public byte[] convert(String str) throws SQLException {
        return convertWithReplacement(str);
    }

    public byte[] convert(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        byte[] result;
        if (from.rep == 3) {
            result = useOrCopy(source, offset, count);
        } else {
            String s = from.toString(source, offset, count);

            result = convert(s);
        }

        return result;
    }

    int javaCharsToUTFE(char[] chars, int chars_offset, int chars_count, byte[] bytes,
            int bytes_begin) {
        int chars_end = chars_offset + chars_count;

        int bytes_index = 0;

        for (int i = chars_offset; i < chars_end; i++) {
            int c = chars[i];

            if (c <= 31) {
                int temp = c | 0x80;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
            } else if (c <= 127) {
                bytes[(bytes_index++)] = utf8m2utfe[high(c)][low(c)];
            } else if (c <= 1023) {
                int temp = (c & 0x3E0) >> 5 | 0xC0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = c & 0x1F | 0xA0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
            } else if (c <= 16383) {
                int temp = (c & 0x3C00) >> 10 | 0xE0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = (c & 0x3E0) >> 5 | 0xA0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = c & 0x1F | 0xA0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
            } else {
                int temp = (c & 0x8000) >> 15 | 0xF0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = (c & 0x7C00) >> 10 | 0xA0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = (c & 0x3E0) >> 5 | 0xA0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = c & 0x1F | 0xA0;
                bytes[(bytes_index++)] = utf8m2utfe[high(temp)][low(temp)];
            }
        }

        return bytes_index;
    }

    int decode(CharacterWalker walker) throws SQLException {
        byte[] bytes = walker.bytes;
        int bytes_index = walker.next;
        int bytes_end = walker.end;
        int result = 0;

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
            int chars_len = UTFEToJavaChar(bytes, bytes_index, len, chars,
                                           CharacterSet.CharacterConverterBehavior.REPORT_ERROR);

            walker.next += len;

            if (chars_len == 1) {
                return chars[0];
            }

            return chars[0] << '\020' | chars[1];
        } catch (SQLException e) {
            failUTFConversion();

            walker.next = bytes_index;
        }
        return result;
    }

    void encode(CharacterBuffer buffer, int c) throws SQLException {
        if ((c & 0xFFFF0000) != 0) {
            failUTFConversion();
        } else {
            char[] chars = { (char) c };

            if (c <= 31) {
                need(buffer, 1);

                int temp = c | 0x80;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
            } else if (c <= 127) {
                need(buffer, 1);

                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(c)][low(c)];
            } else if (c <= 1023) {
                need(buffer, 2);

                int temp = (c & 0x3E0) >> 5 | 0xC0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = c & 0x1F | 0xA0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
            } else if (c <= 16383) {
                need(buffer, 3);

                int temp = (c & 0x3C00) >> 10 | 0xE0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = (c & 0x3E0) >> 5 | 0xA0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = c & 0x1F | 0xA0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
            } else {
                need(buffer, 4);

                int temp = (c & 0x8000) >> 15 | 0xF0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = (c & 0x7C00) >> 10 | 0xA0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = (c & 0x3E0) >> 5 | 0xA0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
                temp = c & 0x1F | 0xA0;
                buffer.bytes[(buffer.next++)] = utf8m2utfe[high(temp)][low(temp)];
            }
        }
    }

    private static int high(int b) {
        return b >> 4 & 0xF;
    }

    private static int low(int b) {
        return b & 0xF;
    }

    private static boolean is101xxxxx(byte c) {
        return (c & 0xFFFFFFE0) == -96;
    }

    private static int getUTFByteLength(byte b) {
        return m_byteLen[(utfe2utf8m[high(b)][low(b)] >>> 4 & 0xF)];
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetUTFE JD-Core Version: 0.6.0
 */