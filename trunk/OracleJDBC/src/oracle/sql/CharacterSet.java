package oracle.sql;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.sql.converter.CharacterConverterFactoryOGS;

public abstract class CharacterSet {
    public static final short DEFAULT_CHARSET = -1;
    public static final short ASCII_CHARSET = 1;
    public static final short ISO_LATIN_1_CHARSET = 31;
    public static final short UNICODE_1_CHARSET = 870;
    public static final short US7ASCII_CHARSET = 1;
    public static final short WE8DEC_CHARSET = 2;
    public static final short WE8HP_CHARSET = 3;
    public static final short US8PC437_CHARSET = 4;
    public static final short WE8EBCDIC37_CHARSET = 5;
    public static final short WE8EBCDIC500_CHARSET = 6;
    public static final short WE8EBCDIC285_CHARSET = 8;
    public static final short WE8PC850_CHARSET = 10;
    public static final short D7DEC_CHARSET = 11;
    public static final short F7DEC_CHARSET = 12;
    public static final short S7DEC_CHARSET = 13;
    public static final short E7DEC_CHARSET = 14;
    public static final short SF7ASCII_CHARSET = 15;
    public static final short NDK7DEC_CHARSET = 16;
    public static final short I7DEC_CHARSET = 17;
    public static final short NL7DEC_CHARSET = 18;
    public static final short CH7DEC_CHARSET = 19;
    public static final short YUG7ASCII_CHARSET = 20;
    public static final short SF7DEC_CHARSET = 21;
    public static final short TR7DEC_CHARSET = 22;
    public static final short IW7IS960_CHARSET = 23;
    public static final short IN8ISCII_CHARSET = 25;
    public static final short WE8ISO8859P1_CHARSET = 31;
    public static final short EE8ISO8859P2_CHARSET = 32;
    public static final short SE8ISO8859P3_CHARSET = 33;
    public static final short NEE8ISO8859P4_CHARSET = 34;
    public static final short CL8ISO8859P5_CHARSET = 35;
    public static final short AR8ISO8859P6_CHARSET = 36;
    public static final short EL8ISO8859P7_CHARSET = 37;
    public static final short IW8ISO8859P8_CHARSET = 38;
    public static final short WE8ISO8859P9_CHARSET = 39;
    public static final short NE8ISO8859P10_CHARSET = 40;
    public static final short TH8TISASCII_CHARSET = 41;
    public static final short TH8TISEBCDIC_CHARSET = 42;
    public static final short BN8BSCII_CHARSET = 43;
    public static final short VN8VN3_CHARSET = 44;
    public static final short VN8MSWIN1258_CHARSET = 45;
    public static final short WE8NEXTSTEP_CHARSET = 50;
    public static final short AR8ASMO708PLUS_CHARSET = 61;
    public static final short AR8EBCDICX_CHARSET = 70;
    public static final short AR8XBASIC_CHARSET = 72;
    public static final short EL8DEC_CHARSET = 81;
    public static final short TR8DEC_CHARSET = 82;
    public static final short WE8EBCDIC37C_CHARSET = 90;
    public static final short WE8EBCDIC500C_CHARSET = 91;
    public static final short IW8EBCDIC424_CHARSET = 92;
    public static final short TR8EBCDIC1026_CHARSET = 93;
    public static final short WE8EBCDIC871_CHARSET = 94;
    public static final short WE8EBCDIC284_CHARSET = 95;
    public static final short WE8EBCDIC1047_CHARSET = 96;
    public static final short EEC8EUROASCI_CHARSET = 110;
    public static final short EEC8EUROPA3_CHARSET = 113;
    public static final short LA8PASSPORT_CHARSET = 114;
    public static final short BG8PC437S_CHARSET = 140;
    public static final short EE8PC852_CHARSET = 150;
    public static final short RU8PC866_CHARSET = 152;
    public static final short RU8BESTA_CHARSET = 153;
    public static final short IW8PC1507_CHARSET = 154;
    public static final short RU8PC855_CHARSET = 155;
    public static final short TR8PC857_CHARSET = 156;
    public static final short CL8MACCYRILLIC_CHARSET = 158;
    public static final short CL8MACCYRILLICS_CHARSET = 159;
    public static final short WE8PC860_CHARSET = 160;
    public static final short IS8PC861_CHARSET = 161;
    public static final short EE8MACCES_CHARSET = 162;
    public static final short EE8MACCROATIANS_CHARSET = 163;
    public static final short TR8MACTURKISHS_CHARSET = 164;
    public static final short IS8MACICELANDICS_CHARSET = 165;
    public static final short EL8MACGREEKS_CHARSET = 166;
    public static final short IW8MACHEBREWS_CHARSET = 167;
    public static final short EE8MSWIN1250_CHARSET = 170;
    public static final short CL8MSWIN1251_CHARSET = 171;
    public static final short ET8MSWIN923_CHARSET = 172;
    public static final short BG8MSWIN_CHARSET = 173;
    public static final short EL8MSWIN1253_CHARSET = 174;
    public static final short IW8MSWIN1255_CHARSET = 175;
    public static final short LT8MSWIN921_CHARSET = 176;
    public static final short TR8MSWIN1254_CHARSET = 177;
    public static final short WE8MSWIN1252_CHARSET = 178;
    public static final short BLT8MSWIN1257_CHARSET = 179;
    public static final short D8EBCDIC273_CHARSET = 180;
    public static final short I8EBCDIC280_CHARSET = 181;
    public static final short DK8EBCDIC277_CHARSET = 182;
    public static final short S8EBCDIC278_CHARSET = 183;
    public static final short EE8EBCDIC870_CHARSET = 184;
    public static final short CL8EBCDIC1025_CHARSET = 185;
    public static final short F8EBCDIC297_CHARSET = 186;
    public static final short IW8EBCDIC1086_CHARSET = 187;
    public static final short CL8EBCDIC1025X_CHARSET = 188;
    public static final short N8PC865_CHARSET = 190;
    public static final short BLT8CP921_CHARSET = 191;
    public static final short LV8PC1117_CHARSET = 192;
    public static final short LV8PC8LR_CHARSET = 193;
    public static final short BLT8EBCDIC1112_CHARSET = 194;
    public static final short LV8RST104090_CHARSET = 195;
    public static final short CL8KOI8R_CHARSET = 196;
    public static final short BLT8PC775_CHARSET = 197;
    public static final short F7SIEMENS9780X_CHARSET = 201;
    public static final short E7SIEMENS9780X_CHARSET = 202;
    public static final short S7SIEMENS9780X_CHARSET = 203;
    public static final short DK7SIEMENS9780X_CHARSET = 204;
    public static final short N7SIEMENS9780X_CHARSET = 205;
    public static final short I7SIEMENS9780X_CHARSET = 206;
    public static final short D7SIEMENS9780X_CHARSET = 207;
    public static final short WE8GCOS7_CHARSET = 210;
    public static final short EL8GCOS7_CHARSET = 211;
    public static final short US8BS2000_CHARSET = 221;
    public static final short D8BS2000_CHARSET = 222;
    public static final short F8BS2000_CHARSET = 223;
    public static final short E8BS2000_CHARSET = 224;
    public static final short DK8BS2000_CHARSET = 225;
    public static final short S8BS2000_CHARSET = 226;
    public static final short WE8BS2000_CHARSET = 231;
    public static final short CL8BS2000_CHARSET = 235;
    public static final short WE8BS2000L5_CHARSET = 239;
    public static final short WE8DG_CHARSET = 241;
    public static final short WE8NCR4970_CHARSET = 251;
    public static final short WE8ROMAN8_CHARSET = 261;
    public static final short EE8MACCE_CHARSET = 262;
    public static final short EE8MACCROATIAN_CHARSET = 263;
    public static final short TR8MACTURKISH_CHARSET = 264;
    public static final short IS8MACICELANDIC_CHARSET = 265;
    public static final short EL8MACGREEK_CHARSET = 266;
    public static final short IW8MACHEBREW_CHARSET = 267;
    public static final short US8ICL_CHARSET = 277;
    public static final short WE8ICL_CHARSET = 278;
    public static final short WE8ISOICLUK_CHARSET = 279;
    public static final short WE8MACROMAN8_CHARSET = 351;
    public static final short WE8MACROMAN8S_CHARSET = 352;
    public static final short TH8MACTHAI_CHARSET = 353;
    public static final short TH8MACTHAIS_CHARSET = 354;
    public static final short HU8CWI2_CHARSET = 368;
    public static final short EL8PC437S_CHARSET = 380;
    public static final short EL8EBCDIC875_CHARSET = 381;
    public static final short EL8PC737_CHARSET = 382;
    public static final short LT8PC772_CHARSET = 383;
    public static final short LT8PC774_CHARSET = 384;
    public static final short EL8PC869_CHARSET = 385;
    public static final short EL8PC851_CHARSET = 386;
    public static final short CDN8PC863_CHARSET = 390;
    public static final short HU8ABMOD_CHARSET = 401;
    public static final short AR8ASMO8X_CHARSET = 500;
    public static final short AR8NAFITHA711T_CHARSET = 504;
    public static final short AR8SAKHR707T_CHARSET = 505;
    public static final short AR8MUSSAD768T_CHARSET = 506;
    public static final short AR8ADOS710T_CHARSET = 507;
    public static final short AR8ADOS720T_CHARSET = 508;
    public static final short AR8APTEC715T_CHARSET = 509;
    public static final short AR8NAFITHA721T_CHARSET = 511;
    public static final short AR8HPARABIC8T_CHARSET = 514;
    public static final short AR8NAFITHA711_CHARSET = 554;
    public static final short AR8SAKHR707_CHARSET = 555;
    public static final short AR8MUSSAD768_CHARSET = 556;
    public static final short AR8ADOS710_CHARSET = 557;
    public static final short AR8ADOS720_CHARSET = 558;
    public static final short AR8APTEC715_CHARSET = 559;
    public static final short AR8MSAWIN_CHARSET = 560;
    public static final short AR8NAFITHA721_CHARSET = 561;
    public static final short AR8SAKHR706_CHARSET = 563;
    public static final short AR8ARABICMAC_CHARSET = 565;
    public static final short AR8ARABICMACS_CHARSET = 566;
    public static final short AR8ARABICMACT_CHARSET = 567;
    public static final short LA8ISO6937_CHARSET = 590;
    public static final short US8NOOP_CHARSET = 797;
    public static final short WE8DECTST_CHARSET = 798;
    public static final short JA16VMS_CHARSET = 829;
    public static final short JA16EUC_CHARSET = 830;
    public static final short JA16EUCYEN_CHARSET = 831;
    public static final short JA16SJIS_CHARSET = 832;
    public static final short JA16DBCS_CHARSET = 833;
    public static final short JA16SJISYEN_CHARSET = 834;
    public static final short JA16EBCDIC930_CHARSET = 835;
    public static final short JA16MACSJIS_CHARSET = 836;
    public static final short JA16EUCTILDE_CHARSET = 837;
    public static final short JA16SJISTILDE_CHARSET = 838;
    public static final short KO16KSC5601_CHARSET = 840;
    public static final short KO16DBCS_CHARSET = 842;
    public static final short KO16KSCCS_CHARSET = 845;
    public static final short KO16MSWIN949_CHARSET = 846;
    public static final short ZHS16CGB231280_CHARSET = 850;
    public static final short ZHS16MACCGB231280_CHARSET = 851;
    public static final short ZHS16GBK_CHARSET = 852;
    public static final short ZHS16DBCS_CHARSET = 853;
    public static final short ZHS32GB18030 = 854;
    public static final short ZHS16MSWIN936_CHARSET = 854;
    public static final short ZHT32EUC_CHARSET = 860;
    public static final short ZHT32SOPS_CHARSET = 861;
    public static final short ZHT16DBT_CHARSET = 862;
    public static final short ZHT32TRIS_CHARSET = 863;
    public static final short ZHT16DBCS_CHARSET = 864;
    public static final short ZHT16BIG5_CHARSET = 865;
    public static final short ZHT16CCDC_CHARSET = 866;
    public static final short ZHT16MSWIN950_CHARSET = 867;
    public static final short AL24UTFFSS_CHARSET = 870;
    public static final short UTF8_CHARSET = 871;
    public static final short UTFE_CHARSET = 872;
    public static final short AL32UTF8_CHARSET = 873;
    public static final short KO16TSTSET_CHARSET = 996;
    public static final short JA16TSTSET2_CHARSET = 997;
    public static final short JA16TSTSET_CHARSET = 998;
    public static final short US16TSTFIXED_CHARSET = 1001;
    public static final short JA16EUCFIXED_CHARSET = 1830;
    public static final short JA16SJISFIXED_CHARSET = 1832;
    public static final short JA16DBCSFIXED_CHARSET = 1833;
    public static final short KO16KSC5601FIXED_CHARSET = 1840;
    public static final short KO16DBCSFIXED_CHARSET = 1842;
    public static final short ZHS16CGB231280FIXED_CHARSET = 1850;
    public static final short ZHS16GBKFIXED_CHARSET = 1852;
    public static final short ZHS16DBCSFIXED_CHARSET = 1853;
    public static final short ZHT32EUCFIXED_CHARSET = 1860;
    public static final short ZHT32TRISFIXED_CHARSET = 1863;
    public static final short ZHT16DBCSFIXED_CHARSET = 1864;
    public static final short ZHT16BIG5FIXED_CHARSET = 1865;
    public static final short AL16UTF16_CHARSET = 2000;
    public static final short AL16UTF16LE_CHARSET = 2002;
    public static final short UNICODE_2_CHARSET = 871;
    static CharacterSetFactory factory;
    private int oracleId;
    int rep;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_;
    public static boolean TRACE;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    CharacterSet(int oracleId) {
        this.oracleId = oracleId;
    }

    public static CharacterSet make(int oracleId) {
        return factory.make(oracleId);
    }

    public String toString() {
        return "oracle-character-set-" + this.oracleId;
    }

    public abstract boolean isLossyFrom(CharacterSet paramCharacterSet);

    public abstract boolean isConvertibleFrom(CharacterSet paramCharacterSet);

    public boolean isUnicode() {
        return false;
    }

    boolean isWellFormed(byte[] bytes, int offset, int count) {
        return true;
    }

    public int getOracleId() {
        return this.oracleId;
    }

    int getRep() {
        return this.rep;
    }

    public int getRatioTo(CharacterSet to) {
        throw new Error("oracle.sql.CharacterSet.getRationTo Not Implemented");
    }

    public boolean equals(Object rhs) {
        return ((rhs instanceof CharacterSet)) && (this.oracleId == ((CharacterSet) rhs).oracleId);
    }

    public int hashCode() {
        return this.oracleId;
    }

    public abstract String toStringWithReplacement(byte[] paramArrayOfByte, int paramInt1,
            int paramInt2);

    public String toString(byte[] bytes, int offset, int count) throws SQLException {
        String str = toStringWithReplacement(bytes, offset, count);
        byte[] copy = convert(str);

        if (count != copy.length) {
            failCharacterConversion(this);
        }

        for (int x = 0; x < count; x++) {
            if (copy[x] == bytes[(offset + x)])
                continue;
            failCharacterConversion(this);
        }

        return null;
    }

    public abstract byte[] convert(String paramString) throws SQLException;

    public abstract byte[] convertWithReplacement(String paramString);

    public abstract byte[] convert(CharacterSet paramCharacterSet, byte[] paramArrayOfByte,
            int paramInt1, int paramInt2) throws SQLException;

    public byte[] convertUnshared(CharacterSet from, byte[] source, int offset, int count)
            throws SQLException {
        byte[] result = convert(from, source, offset, count);

        if (result == source) {
            result = new byte[source.length];

            System.arraycopy(source, 0, result, 0, count);
        }

        return result;
    }

    abstract int decode(CharacterWalker paramCharacterWalker) throws SQLException;

    abstract void encode(CharacterBuffer paramCharacterBuffer, int paramInt) throws SQLException;

    static final void failCharacterConversion(CharacterSet which) throws SQLException {
        DatabaseError.throwSqlException(55, which);
    }

    static final byte[] useOrCopy(byte[] bytes, int offset, int count) {
        byte[] result;
        if ((bytes.length == count) && (offset == 0)) {
            result = bytes;
        } else {
            result = new byte[count];

            System.arraycopy(bytes, offset, result, 0, count);
        }

        return result;
    }

    static final void need(CharacterBuffer buffer, int n) {
        int wanted = buffer.bytes.length;
        int needed = n + buffer.next;

        if (needed <= wanted) {
            return;
        }

        while (needed > wanted) {
            wanted = 2 * wanted;
        }

        byte[] old = buffer.bytes;

        buffer.bytes = new byte[wanted];

        System.arraycopy(old, 0, buffer.bytes, 0, buffer.next);
    }

    public static final String UTFToString(byte[] bytes, int offset, int nbytes,
            boolean useReplacementChar) throws SQLException {
        return new String(UTFToJavaChar(bytes, offset, nbytes, useReplacementChar));
    }

    public static final String UTFToString(byte[] bytes, int offset, int nbytes)
            throws SQLException {
        return UTFToString(bytes, offset, nbytes, false);
    }

    public static final char[] UTFToJavaChar(byte[] bytes, int offset, int count)
            throws SQLException {
        return UTFToJavaChar(bytes, offset, count, false);
    }

    public static final char[] UTFToJavaChar(byte[] bytes, int offset, int count,
            boolean useReplacementChar) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.UTFToJavaChar(bytes (length="
                    + bytes.length + "),offset=" + offset + ", count=" + count + ")");

            OracleLog.recursiveTrace = false;
        }

        char[] chars = null;

        chars = new char[count];

        int[] countArr = new int[1];

        countArr[0] = count;

        int chars_index = convertUTFBytesToJavaChars(bytes, offset, chars, 0, countArr,
                                                     useReplacementChar);

        char[] rchars = new char[chars_index];

        System.arraycopy(chars, 0, rchars, 0, chars_index);

        chars = null;

        return rchars;
    }

    public static final char[] UTFToJavaCharWithReplacement(byte[] bytes, int offset, int count) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.UTFToJavaCharWithReplacement(bytes (length="
                                                   + bytes.length + "),offset=" + offset
                                                   + ", count=" + count + ")");

            OracleLog.recursiveTrace = false;
        }

        char[] chars = null;
        try {
            chars = new char[count];

            int[] countArr = new int[1];

            countArr[0] = count;

            int chars_index = convertUTFBytesToJavaChars(bytes, offset, chars, 0, countArr, true);

            char[] rchars = new char[chars_index];

            System.arraycopy(chars, 0, rchars, 0, chars_index);

            chars = null;

            return rchars;
        } catch (SQLException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }

    public static final int convertUTFBytesToJavaChars(byte[] bytes, int offset, char[] chars,
            int chars_offset, int[] countArr, boolean convertWithReplacement) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertUTFBytesToJavaChars(bytes (length="
                                                   + bytes.length + "),offset=" + offset
                                                   + ",chars (length=" + chars.length
                                                   + "), chars_offset=" + chars_offset
                                                   + ", countArr[0]=" + countArr[0]
                                                   + ",convertWithReplacement="
                                                   + convertWithReplacement + ")");

            OracleLog.recursiveTrace = false;
        }

        return convertUTFBytesToJavaChars(bytes, offset, chars, chars_offset, countArr,
                                          convertWithReplacement, chars.length - chars_offset);
    }

    public static final int convertUTFBytesToJavaChars(byte[] bytes, int offset, char[] chars,
            int chars_offset, int[] countArr, boolean convertWithReplacement, int charSize)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertUTFBytesToJavaChars(bytes (length="
                                                   + bytes.length + "),offset=" + offset
                                                   + ",chars (length=" + chars.length
                                                   + "), chars_offset=" + chars_offset
                                                   + ", countArr[0]=" + countArr[0]
                                                   + ",convertWithReplacement="
                                                   + convertWithReplacement + ",charSize="
                                                   + charSize + ")");

            OracleLog.recursiveTrace = false;
        }

        CharacterConverterBehavior ccb = convertWithReplacement ? CharacterConverterBehavior.REPLACEMENT
                : CharacterConverterBehavior.REPORT_ERROR;

        int count = countArr[0];

        countArr[0] = 0;

        int bytes_index = offset;
        int bytes_end = offset + count;
        int chars_index = chars_offset;
        int charsLength = chars_offset + charSize;

        label708: while (bytes_index < bytes_end) {
            byte c = bytes[(bytes_index++)];
            int b = c & 0xF0;

            switch (b / 16) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                if (chars_index < charsLength) {
                    chars[(chars_index++)] = (char) (c & 0xFFFFFFFF);
                    continue;
                }

                countArr[0] = (bytes_end - bytes_index + 2);

                break;
            case 12:
            case 13:
                if (bytes_index >= bytes_end) {
                    countArr[0] = 1;

                    ccb.onFailConversion();

                    break label708;
                }

                char c2 = conv2ByteUTFtoUTF16(c, bytes[(bytes_index++)]);

                if (chars_index < charsLength) {
                    chars[(chars_index++)] = c2;
                } else {
                    countArr[0] = (bytes_end - bytes_index + 3);

                    break label708;
                }

                ccb.onFailConversion(c2);

                break;
            case 14:
                if (bytes_index + 1 >= bytes_end) {
                    countArr[0] = (bytes_end - bytes_index + 1);

                    ccb.onFailConversion();

                    break label708;
                }

                char c1 = conv3ByteUTFtoUTF16(c, bytes[(bytes_index++)], bytes[(bytes_index++)]);

                if ((b != 244) && (bytes[(bytes_index - 2)] != -65)
                        && (bytes[(bytes_index - 1)] != -67)) {
                    ccb.onFailConversion(c1);
                }

                if (isHiSurrogate(c1)) {
                    if (chars_index > charsLength - 2) {
                        countArr[0] = (bytes_end - bytes_index + 4);

                        break label708;
                    }

                    if (bytes_index >= bytes_end)
                        continue;
                    c = bytes[bytes_index];

                    if ((byte) (c & 0xF0) != -32) {
                        chars[(chars_index++)] = 65533;

                        ccb.onFailConversion();

                        continue;
                    }

                    bytes_index++;

                    if (bytes_index + 1 >= bytes_end) {
                        countArr[0] = (bytes_end - bytes_index + 1);

                        ccb.onFailConversion();

                        break label708;
                    }

                    char c21 = conv3ByteUTFtoUTF16(c, bytes[(bytes_index++)],
                                                   bytes[(bytes_index++)]);

                    if (isLoSurrogate(c21)) {
                        chars[(chars_index++)] = c1;
                    } else {
                        chars[(chars_index++)] = 65533;

                        ccb.onFailConversion();
                    }

                    chars[(chars_index++)] = c21;
                    continue;
                }

                if (chars_index < charsLength) {
                    chars[(chars_index++)] = c1;
                    continue;
                }

                countArr[0] = (bytes_end - bytes_index + 4);

                break;
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                if (chars_index < charsLength) {
                    chars[(chars_index++)] = 65533;
                } else {
                    countArr[0] = (bytes_end - bytes_index + 2);

                    break label708;
                }

                ccb.onFailConversion();
            }
        }

        int b = chars_index - chars_offset;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertUTFBytesToJavaChars: return=" + b);

            OracleLog.recursiveTrace = false;
        }

        return b;
    }

    public static final byte[] stringToUTF(String str) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToUTF(str=" + str + ")");

            OracleLog.recursiveTrace = false;
        }

        char[] chararr = str.toCharArray();
        int maxNbBytes = chararr.length * 3;
        byte[] bytearr = null;
        byte[] rbytearr = null;

        bytearr = new byte[maxNbBytes];

        int byte_len = convertJavaCharsToUTFBytes(chararr, 0, bytearr, 0, chararr.length);

        rbytearr = new byte[byte_len];

        System.arraycopy(bytearr, 0, rbytearr, 0, byte_len);

        bytearr = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToUTF:return");

            OracleLog.recursiveTrace = false;
        }

        return rbytearr;
    }

    public static final int convertJavaCharsToUTFBytes(char[] chars, int chars_offset,
            byte[] bytes, int bytes_begin, int chars_count) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToUTFBytes(chars (length="
                                                   + chars.length + "),chars_offset="
                                                   + chars_offset + ",bytes (length="
                                                   + bytes.length + "), bytes_begin=" + bytes_begin
                                                   + ", chars_count=" + chars_count + ")");

            OracleLog.recursiveTrace = false;
        }

        int chars_begin = chars_offset;
        int chars_end = chars_offset + chars_count;

        int byte_index = bytes_begin;

        for (int i = chars_begin; i < chars_end; i++) {
            int c = chars[i];

            if ((c >= 0) && (c <= 127)) {
                bytes[(byte_index++)] = (byte) c;
            } else if (c > 2047) {
                bytes[(byte_index++)] = (byte) (0xE0 | c >>> 12 & 0xF);
                bytes[(byte_index++)] = (byte) (0x80 | c >>> 6 & 0x3F);
                bytes[(byte_index++)] = (byte) (0x80 | c >>> 0 & 0x3F);
            } else {
                bytes[(byte_index++)] = (byte) (0xC0 | c >>> 6 & 0x1F);
                bytes[(byte_index++)] = (byte) (0x80 | c >>> 0 & 0x3F);
            }
        }

        int c = byte_index - bytes_begin;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToUTFBytes: return=" + c);

            OracleLog.recursiveTrace = false;
        }

        return c;
    }

    public static final int UTFStringLength(byte[] bytes, int offset, int count) {
        int strlen = 0;
        int bytes_index = offset;
        int bytes_end = offset + count;

        while (bytes_index < bytes_end) {
            switch ((bytes[bytes_index] & 0xF0) >>> 4) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                bytes_index++;
                strlen++;

                break;
            case 12:
            case 13:
                if (bytes_index + 1 >= bytes_end) {
                    bytes_index = bytes_end;
                    continue;
                }

                strlen++;
                bytes_index += 2;

                break;
            case 14:
                if (bytes_index + 2 >= bytes_end) {
                    bytes_index = bytes_end;
                    continue;
                }

                strlen++;
                bytes_index += 3;

                break;
            case 15:
                if (bytes_index + 3 >= bytes_end) {
                    bytes_index = bytes_end;
                    continue;
                }

                strlen += 2;
                bytes_index += 4;

                break;
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                bytes_index++;
                strlen++;
            }
        }

        return strlen;
    }

    public static final int stringUTFLength(String s) {
        char[] carr = s.toCharArray();
        return charArrayUTF8Length(carr);
    }

    static final int charArrayUTF8Length(char[] carr) {
        int utflen = 0;
        int slen = carr.length;

        for (int i = 0; i < slen; i++) {
            int c = carr[i];

            if ((c >= 0) && (c <= 127)) {
                utflen++;
            } else if (c > 2047) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }

        return utflen;
    }

    public static final String AL32UTF8ToString(byte[] bytes, int offset, int nbytes) {
        return AL32UTF8ToString(bytes, offset, nbytes, false);
    }

    public static final String AL32UTF8ToString(byte[] bytes, int offset, int nbytes,
            boolean useReplacementCharacter) {
        char[] chars = null;
        try {
            chars = AL32UTF8ToJavaChar(bytes, offset, nbytes, useReplacementCharacter);
        } catch (SQLException e) {
        }
        return new String(chars);
    }

    public static final char[] AL32UTF8ToJavaChar(byte[] bytes, int offset, int count,
            boolean useReplacementCharacter) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.AL32UTF8ToJavaChar(bytes (length="
                                                   + bytes.length + "),offset=" + offset
                                                   + ", count=" + count + ")");

            OracleLog.recursiveTrace = false;
        }

        char[] chars = null;
        try {
            chars = new char[count];

            int[] countArr = new int[1];

            countArr[0] = count;

            int clen = convertAL32UTF8BytesToJavaChars(bytes, offset, chars, 0, countArr,
                                                       useReplacementCharacter);

            char[] rchars = new char[clen];

            System.arraycopy(chars, 0, rchars, 0, clen);

            chars = null;

            return rchars;
        } catch (SQLException e) {
            failUTFConversion();
        }
        return new char[0];
    }

    public static final int convertAL32UTF8BytesToJavaChars(byte[] bytes, int offsetBytes,
            char[] chars, int offsetChars, int[] countArr, boolean convertWithReplacement)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertAL32UTF8BytesToJavaChars(bytes (length="
                                                   + bytes.length + "),offsetBytes=" + offsetBytes
                                                   + ",chars (length=" + chars.length
                                                   + "), offsetChars=" + offsetChars
                                                   + ", countArr[0]=" + countArr[0]
                                                   + ",convertWithReplacement="
                                                   + convertWithReplacement + ")");

            OracleLog.recursiveTrace = false;
        }

        return convertAL32UTF8BytesToJavaChars(bytes, offsetBytes, chars, offsetChars, countArr,
                                               convertWithReplacement, chars.length - offsetChars);
    }

    public static final int convertAL32UTF8BytesToJavaChars(byte[] bytes, int offsetBytes,
            char[] chars, int offsetChars, int[] countArr, boolean convertWithReplacement,
            int charSize) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertAL32UTF8BytesToJavaChars(bytes (length="
                                                   + bytes.length + "),offsetBytes=" + offsetBytes
                                                   + ",chars (length=" + chars.length
                                                   + "), offsetChars=" + offsetChars
                                                   + ", countArr[0]=" + countArr[0]
                                                   + ",convertWithReplacement="
                                                   + convertWithReplacement + ",charSize="
                                                   + charSize + ")");

            OracleLog.recursiveTrace = false;
        }

        CharacterConverterBehavior ccb = convertWithReplacement ? CharacterConverterBehavior.REPLACEMENT
                : CharacterConverterBehavior.REPORT_ERROR;

        int count = countArr[0];

        countArr[0] = 0;

        int bytes_index = offsetBytes;
        int bytes_end = offsetBytes + count;
        int chars_index = offsetChars;
        int charsLength = offsetChars + charSize;

        label619: while (bytes_index < bytes_end) {
            byte c = bytes[(bytes_index++)];
            int b = c & 0xF0;

            switch (b / 16) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                if (chars_index < charsLength) {
                    chars[(chars_index++)] = (char) (c & 0xFFFFFFFF);
                    continue;
                }

                countArr[0] = (bytes_end - bytes_index + 2);

                break;
            case 12:
            case 13:
                if (bytes_index >= bytes_end) {
                    countArr[0] = 1;

                    ccb.onFailConversion();

                    break label619;
                }

                char c2 = conv2ByteUTFtoUTF16(c, bytes[(bytes_index++)]);

                if (chars_index < charsLength) {
                    chars[(chars_index++)] = c2;
                } else {
                    countArr[0] = (bytes_end - bytes_index + 3);

                    break label619;
                }

                ccb.onFailConversion(c2);

                break;
            case 14:
                if (bytes_index + 1 >= bytes_end) {
                    countArr[0] = (bytes_end - bytes_index + 1);

                    ccb.onFailConversion();

                    break label619;
                }

                char c21 = conv3ByteAL32UTF8toUTF16(c, bytes[(bytes_index++)],
                                                    bytes[(bytes_index++)]);

                if (chars_index < charsLength) {
                    chars[(chars_index++)] = c21;
                } else {
                    countArr[0] = (bytes_end - bytes_index + 4);

                    break label619;
                }

                ccb.onFailConversion(c21);

                break;
            case 15:
                if (bytes_index + 2 >= bytes_end) {
                    countArr[0] = (bytes_end - bytes_index + 1);

                    ccb.onFailConversion();

                    break label619;
                }

                if (chars_index > charsLength - 2) {
                    countArr[0] = (bytes_end - bytes_index + 2);

                    break label619;
                }

                int w = conv4ByteAL32UTF8toUTF16(c, bytes[(bytes_index++)], bytes[(bytes_index++)],
                                                 bytes[(bytes_index++)], chars, chars_index);

                if (w == 1) {
                    ccb.onFailConversion();

                    chars_index++;
                    continue;
                }

                chars_index += 2;

                break;
            case 8:
            case 9:
            case 10:
            case 11:
            default:
                if (chars_index < charsLength) {
                    chars[(chars_index++)] = 65533;
                } else {
                    countArr[0] = (bytes_end - bytes_index + 2);

                    break label619;
                }

                ccb.onFailConversion();
            }
        }

        int w = chars_index - offsetChars;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertAL32UTF8BytesToJavaChars: return="
                                                   + w);

            OracleLog.recursiveTrace = false;
        }

        return w;
    }

    public static final byte[] stringToAL32UTF8(String str) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToAL32UTF8(str=" + str
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        char[] chararr = str.toCharArray();
        int maxNbBytes = chararr.length * 3;
        byte[] bytearr = null;
        byte[] rbytearr = null;

        bytearr = new byte[maxNbBytes];

        int byte_len = convertJavaCharsToAL32UTF8Bytes(chararr, 0, bytearr, 0, chararr.length);

        rbytearr = new byte[byte_len];

        System.arraycopy(bytearr, 0, rbytearr, 0, byte_len);

        bytearr = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToAL32UTF8:return");

            OracleLog.recursiveTrace = false;
        }

        return rbytearr;
    }

    public static final int convertJavaCharsToAL32UTF8Bytes(char[] chars, int chars_offset,
            byte[] bytes, int bytes_begin, int chars_count) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToAL32UTF8Bytes(chars (length="
                                                   + chars.length + ")chars_offset,="
                                                   + chars_offset + ",bytes (length="
                                                   + bytes.length + "), bytes_begin=" + bytes_begin
                                                   + ", chars_count=" + chars_count + ")");

            OracleLog.recursiveTrace = false;
        }

        int chars_begin = chars_offset;
        int chars_end = chars_offset + chars_count;
        int byte_index = bytes_begin;

        for (int i = chars_begin; i < chars_end; i++) {
            int c = chars[i];
            int c2 = 0;

            if ((c >= 0) && (c <= 127)) {
                bytes[(byte_index++)] = (byte) c;
            } else if (isHiSurrogate((char) c)) {
                if ((i + 1 < chars_end) && (isLoSurrogate((char) (c2 = chars[(i + 1)])))) {
                    int uuuuu = (c >>> 6 & 0xF) + 1;
                    bytes[(byte_index++)] = (byte) (uuuuu >>> 2 | 0xF0);
                    bytes[(byte_index++)] = (byte) ((uuuuu & 0x3) << 4 | c >>> 2 & 0xF | 0x80);

                    bytes[(byte_index++)] = (byte) ((c & 0x3) << 4 | c2 >>> 6 & 0xF | 0x80);

                    bytes[(byte_index++)] = (byte) (c2 & 0x3F | 0x80);
                    i++;
                } else {
                    bytes[(byte_index++)] = -17;
                    bytes[(byte_index++)] = -65;
                    bytes[(byte_index++)] = -67;
                }
            } else if (c > 2047) {
                bytes[(byte_index++)] = (byte) (0xE0 | c >>> 12 & 0xF);
                bytes[(byte_index++)] = (byte) (0x80 | c >>> 6 & 0x3F);
                bytes[(byte_index++)] = (byte) (0x80 | c >>> 0 & 0x3F);
            } else {
                bytes[(byte_index++)] = (byte) (0xC0 | c >>> 6 & 0x1F);
                bytes[(byte_index++)] = (byte) (0x80 | c >>> 0 & 0x3F);
            }
        }

        int uuuuu = byte_index - bytes_begin;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToAL32UTF8Bytes: return="
                                                   + uuuuu);

            OracleLog.recursiveTrace = false;
        }

        return uuuuu;
    }

    public static final int string32UTF8Length(String s) {
        return charArray32UTF8Length(s.toCharArray());
    }

    static final int charArray32UTF8Length(char[] carr) {
        int utflen = 0;
        int slen = carr.length;

        for (int i = 0; i < slen; i++) {
            int c = carr[i];

            if ((c >= 0) && (c <= 127)) {
                utflen++;
            } else if (c > 2047) {
                if (isHiSurrogate((char) c)) {
                    if (i + 1 >= slen)
                        continue;
                    utflen += 4;
                    i++;
                } else {
                    utflen += 3;
                }
            } else {
                utflen += 2;
            }
        }

        return utflen;
    }

    public static final String AL16UTF16BytesToString(byte[] bytes, int nbytes) {
        char[] chars = new char[nbytes >>> 1];

        AL16UTF16BytesToJavaChars(bytes, nbytes, chars);

        return new String(chars);
    }

    public static final int AL16UTF16BytesToJavaChars(byte[] bytes, int nbytes, char[] chars) {
        int charMax = nbytes >>> 1;

        int chars_i = 0;
        for (int bytes_i = 0; chars_i < charMax; chars_i++) {
            int hibyte = bytes[bytes_i] << 8;
            chars[chars_i] = (char) (hibyte | bytes[(bytes_i + 1)] & 0xFF);

            bytes_i += 2;
        }

        return chars_i;
    }

    public static final int convertAL16UTF16BytesToJavaChars(byte[] bytes, int offset,
            char[] chars, int chars_offset, int count, boolean convertWithReplacement)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertAL16UTF16BytesToJavaChars(bytes (length="
                                                   + bytes.length + "),offset=" + offset
                                                   + ",chars (length=" + chars.length
                                                   + "), chars_offset=" + chars_offset + ", count="
                                                   + count + ",convertWithReplacement="
                                                   + convertWithReplacement + ")");

            OracleLog.recursiveTrace = false;
        }

        CharacterConverterBehavior ccb = convertWithReplacement ? CharacterConverterBehavior.REPLACEMENT
                : CharacterConverterBehavior.REPORT_ERROR;

        int chars_i = chars_offset;
        int bytes_i = offset;
        int bytes_end = offset + count;

        for (; bytes_i + 1 < bytes_end; bytes_i += 2) {
            int hibyte = bytes[bytes_i] << 8;
            char c = (char) (hibyte | bytes[(bytes_i + 1)] & 0xFF);

            if (isHiSurrogate(c)) {
                bytes_i += 2;

                if (bytes_i + 1 >= bytes_end)
                    continue;
                char c2 = (char) ((bytes[bytes_i] << 8) + (bytes[(bytes_i + 1)] & 0xFF));

                if (isLoSurrogate(c2)) {
                    chars[(chars_i++)] = c;
                } else {
                    chars[(chars_i++)] = 65533;
                }

                chars[(chars_i++)] = c2;
            } else {
                chars[(chars_i++)] = c;
            }
        }

        bytes_i = chars_i - chars_offset;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertAL16UTF16BytesToJavaChars: return="
                                                   + bytes_i);

            OracleLog.recursiveTrace = false;
        }

        return bytes_i;
    }

    public static final int convertAL16UTF16LEBytesToJavaChars(byte[] bytes, int offset,
            char[] chars, int chars_offset, int count, boolean convertWithReplacement)
            throws SQLException {
        CharacterConverterBehavior ccb = convertWithReplacement ? CharacterConverterBehavior.REPLACEMENT
                : CharacterConverterBehavior.REPORT_ERROR;

        int chars_i = chars_offset;
        int bytes_i = offset;
        int bytes_end = offset + count;

        for (; bytes_i + 1 < bytes_end; bytes_i += 2) {
            int hibyte = bytes[(bytes_i + 1)] << 8;
            char c = (char) (hibyte | bytes[bytes_i] & 0xFF);

            if (isHiSurrogate(c)) {
                bytes_i += 2;

                if (bytes_i + 1 >= bytes_end)
                    continue;
                char c2 = (char) ((bytes[(bytes_i + 1)] << 8) + (bytes[bytes_i] & 0xFF));

                if (isLoSurrogate(c2)) {
                    chars[(chars_i++)] = c;
                } else {
                    chars[(chars_i++)] = 65533;
                }

                chars[(chars_i++)] = c2;
            } else {
                chars[(chars_i++)] = c;
            }
        }

        bytes_i = chars_i - chars_offset;

        return bytes_i;
    }

    public static final byte[] stringToAL16UTF16Bytes(String str) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToAL16UTF16Bytes(str="
                    + str + ")");

            OracleLog.recursiveTrace = false;
        }

        char[] chars = str.toCharArray();
        int nchars = chars.length;
        byte[] bytes = new byte[nchars * 2];

        javaCharsToAL16UTF16Bytes(chars, nchars, bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger
                    .log(Level.FINE, "CharacterSet.stringToAL16UTF16Bytes:return");

            OracleLog.recursiveTrace = false;
        }

        return bytes;
    }

    public static final int javaCharsToAL16UTF16Bytes(char[] chars, int nchars, byte[] bytes) {
        int charsMax = Math.min(nchars, bytes.length >>> 1);

        return convertJavaCharsToAL16UTF16Bytes(chars, 0, bytes, 0, charsMax);
    }

    public static final int convertJavaCharsToAL16UTF16Bytes(char[] chars, int chars_offset,
            byte[] bytes, int bytes_offset, int nchars) {
        int chars_i = chars_offset;
        int bytes_i = bytes_offset;
        int char_end = chars_offset + nchars;

        for (; chars_i < char_end; bytes_i += 2) {
            bytes[bytes_i] = (byte) (chars[chars_i] >>> '\b' & 0xFF);
            bytes[(bytes_i + 1)] = (byte) (chars[chars_i] & 0xFF);

            chars_i++;
        }

        return bytes_i - bytes_offset;
    }

    public static final byte[] stringToAL16UTF16LEBytes(String str) {
        char[] chars = str.toCharArray();
        byte[] bytes = new byte[chars.length * 2];

        javaCharsToAL16UTF16LEBytes(chars, chars.length, bytes);

        return bytes;
    }

    public static final int javaCharsToAL16UTF16LEBytes(char[] chars, int nchars, byte[] bytes) {
        return convertJavaCharsToAL16UTF16LEBytes(chars, 0, bytes, 0, nchars);
    }

    public static final int convertJavaCharsToAL16UTF16LEBytes(char[] chars, int chars_offset,
            byte[] bytes, int bytes_offset, int nchars) {
        int chars_i = chars_offset;
        int bytes_i = bytes_offset;
        int char_end = chars_offset + nchars;

        for (; chars_i < char_end; bytes_i += 2) {
            bytes[bytes_i] = (byte) (chars[chars_i] & 0xFF);
            bytes[(bytes_i + 1)] = (byte) (chars[chars_i] >>> '\b');

            chars_i++;
        }

        return bytes_i - bytes_offset;
    }

    public static final int convertASCIIBytesToJavaChars(byte[] bytes, int bytes_offset,
            char[] chars, int chars_offset, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertASCIIBytesToJavaChars(bytes (length="
                                                   + bytes.length + "),bytes_offset="
                                                   + bytes_offset + ",chars (length="
                                                   + chars.length + "), chars_offset="
                                                   + chars_offset + ", count=" + count + ")");

            OracleLog.recursiveTrace = false;
        }

        int lastChar = chars_offset + count;

        int i = chars_offset;
        for (int j = bytes_offset; i < lastChar; j++) {
            chars[i] = (char) (0xFF & bytes[j]);

            i++;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertASCIIBytesToJavaChars: return="
                                                   + count);

            OracleLog.recursiveTrace = false;
        }

        return count;
    }

    public static final int convertJavaCharsToASCIIBytes(char[] chars, int chars_offset,
            byte[] bytes, int bytes_offset, int nchars) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToASCIIBytes(chars (length="
                                                   + chars.length + "),chars_offset="
                                                   + chars_offset + ",bytes (length="
                                                   + bytes.length + "), bytes_offset="
                                                   + bytes_offset + ", nchars=" + nchars + ")");

            OracleLog.recursiveTrace = false;
        }

        for (int i = 0; i < nchars; i++) {
            bytes[(bytes_offset + i)] = (byte) chars[(chars_offset + i)];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToASCIIBytes: return="
                                                   + nchars);

            OracleLog.recursiveTrace = false;
        }

        return nchars;
    }

    public static final int convertJavaCharsToISOLATIN1Bytes(char[] chars, int chars_offset,
            byte[] bytes, int bytes_offset, int nchars) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToASCIIBytes(chars (length="
                                                   + chars.length + "),chars_offset="
                                                   + chars_offset + ",bytes (length="
                                                   + bytes.length + "), bytes_offset="
                                                   + bytes_offset + ", nchars=" + nchars + ")");

            OracleLog.recursiveTrace = false;
        }

        for (int i = 0; i < nchars; i++) {
            char c = chars[(chars_offset + i)];

            if (c > 'ÿ') {
                bytes[(bytes_offset + i)] = -65;
            } else
                bytes[(bytes_offset + i)] = (byte) c;

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "CharacterSet.convertJavaCharsToASCIIBytes: return="
                                                   + nchars);

            OracleLog.recursiveTrace = false;
        }

        return nchars;
    }

    public static final byte[] stringToASCII(String str) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToASCII(str=" + str
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        byte[] bytearr = new byte[str.length()];

        str.getBytes(0, str.length(), bytearr, 0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "CharacterSet.stringToASCII:return");

            OracleLog.recursiveTrace = false;
        }

        return bytearr;
    }

    public static final long convertUTF32toUTF16(long ucs4ch) {
        if (ucs4ch > 65535L) {
            long utf16str = 0xD8 | ucs4ch - 65536L >> 18 & 0xFF;
            utf16str = ucs4ch - 65536L >> 10 & 0xFF | utf16str << 8;
            utf16str = 0xDC | (ucs4ch & 0x3FF) >> 8 & 0xFF | utf16str << 8;
            utf16str = ucs4ch & 0xFF | utf16str << 8;

            return utf16str;
        }

        return ucs4ch;
    }

    static final boolean isHiSurrogate(char c) {
        return (char) (c & 0xFC00) == 55296;
    }

    static final boolean isLoSurrogate(char c) {
        return (char) (c & 0xFC00) == 56320;
    }

    static final boolean check80toBF(byte b) {
        return (b & 0xFFFFFFC0) == -128;
    }

    static final boolean check80to8F(byte b) {
        return (b & 0xFFFFFFF0) == -128;
    }

    static final boolean check80to9F(byte b) {
        return (b & 0xFFFFFFE0) == -128;
    }

    static final boolean checkA0toBF(byte b) {
        return (b & 0xFFFFFFE0) == -96;
    }

    static final boolean check90toBF(byte b) {
        return ((b & 0xFFFFFFC0) == -128) && ((b & 0x30) != 0);
    }

    static final char conv2ByteUTFtoUTF16(byte c, byte c2) {
        if ((c < -62) || (c > -33) || (!check80toBF(c2))) {
            return 65533;
        }

        return (char) ((c & 0x1F) << 6 | c2 & 0x3F);
    }

    static final char conv3ByteUTFtoUTF16(byte c, byte c2, byte c3) {
        if (((c != -32) || (!checkA0toBF(c2)) || (!check80toBF(c3)))
                && ((c < -31) || (c > -17) || (!check80toBF(c2)) || (!check80toBF(c3)))) {
            return 65533;
        }

        return (char) ((c & 0xF) << 12 | (c2 & 0x3F) << 6 | c3 & 0x3F);
    }

    static final char conv3ByteAL32UTF8toUTF16(byte c, byte c2, byte c3) {
        if (((c != -32) || (!checkA0toBF(c2)) || (!check80toBF(c3)))
                && ((c < -31) || (c > -20) || (!check80toBF(c2)) || (!check80toBF(c3)))
                && ((c != -19) || (!check80to9F(c2)) || (!check80toBF(c3)))
                && ((c < -18) || (c > -17) || (!check80toBF(c2)) || (!check80toBF(c3)))) {
            return 65533;
        }

        return (char) ((c & 0xF) << 12 | (c2 & 0x3F) << 6 | c3 & 0x3F);
    }

    static final int conv4ByteAL32UTF8toUTF16(byte c, byte c2, byte c3, byte c4, char[] chars,
            int cpos) {
        int chars_count = 0;

        if (((c != -16) || (!check90toBF(c2)) || (!check80toBF(c3)) || (!check80toBF(c4)))
                && ((c < -15) || (c > -13) || (!check80toBF(c2)) || (!check80toBF(c3)) || (!check80toBF(c4)))
                && ((c != -12) || (!check80to8F(c2)) || (!check80toBF(c3)) || (!check80toBF(c4)))) {
            chars[cpos] = 65533;

            return 1;
        }

        chars[cpos] = (char) ((((c & 0x7) << 2 | c2 >>> 4 & 0x3) - 1 & 0xF) << 6 | (c2 & 0xF) << 2
                | c3 >>> 4 & 0x3 | 0xD800);

        chars[(cpos + 1)] = (char) ((c3 & 0xF) << 6 | c4 & 0x3F | 0xDC00);

        return 2;
    }

    static void failUTFConversion() throws SQLException {
        DatabaseError.throwSqlException(55);
    }

    public int encodedByteLength(String s) {
        if ((s == null) || (s.length() == 0))
            return 0;
        return convertWithReplacement(s).length;
    }

    public int encodedByteLength(char[] carray) {
        if ((carray == null) || (carray.length == 0))
            return 0;
        return convertWithReplacement(new String(carray)).length;
    }

    static {
        try {
            Class.forName("oracle.i18n.text.converter.CharacterConverterSJIS");

            CharacterSetWithConverter.ccFactory = new CharacterConverterFactoryOGS();
        } catch (ClassNotFoundException e) {
        }

        factory = new CharacterSetFactoryDefault();

        _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

        TRACE = false;
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.CharacterSet"));
        } catch (Exception e) {
        }
    }

    static abstract class CharacterConverterBehavior {
        public static final char[] NULL_CHARS = new char[1];
        public static final char UTF16_REPLACEMENT_CHAR = '\uFFFD';
        public static final CharacterConverterBehavior REPORT_ERROR = new CharacterConverterBehavior(
                "Report Error") {
            public void onFailConversion() throws SQLException {
                DatabaseError.throwSqlException(55);
            }

            public void onFailConversion(char c) throws SQLException {
                if (c == 65533) {
                    DatabaseError.throwSqlException(55);
                }
            }
        };

        public static final CharacterConverterBehavior REPLACEMENT = new CharacterConverterBehavior(
                "Replacement") {
            public void onFailConversion() throws SQLException {
            }

            public void onFailConversion(char c) throws SQLException {
            }
        };
        private final String m_name;

        public CharacterConverterBehavior(String name) {
            this.m_name = name;
        }

        public abstract void onFailConversion(char paramChar) throws SQLException;

        public abstract void onFailConversion() throws SQLException;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSet JD-Core Version: 0.6.0
 */