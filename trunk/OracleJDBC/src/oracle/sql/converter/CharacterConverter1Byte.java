package oracle.sql.converter;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import oracle.jdbc.driver.DatabaseError;

public class CharacterConverter1Byte extends CharacterConverterJDBC {
    static final int ORACHARMASK = 255;
    static final int UCSCHARWIDTH = 16;
    public int m_ucsReplacement = 0;
    public int[] m_ucsChar = null;
    public char[] m_oraCharLevel1 = null;
    public char[] m_oraCharSurrogateLevel = null;
    public char[] m_oraCharLevel2 = null;
    public byte m_oraCharReplacement = 0;
    protected transient boolean noSurrogate = true;
    protected transient boolean strictASCII = true;
    protected transient int m_oraCharLevel2Size = 0;

    public CharacterConverter1Byte() {
        this.m_groupId = 0;
    }

    int toUnicode(byte srcChar) throws SQLException {
        int ucsChar = this.m_ucsChar[(srcChar & 0xFF)];

        if (ucsChar == -1) {
            DatabaseError.throwSqlException(154);

            return -1;
        }

        return ucsChar;
    }

    int toUnicodeWithReplacement(byte srcChar) {
        int ucsChar = this.m_ucsChar[(srcChar & 0xFF)];

        if (ucsChar == -1) {
            return this.m_ucsReplacement;
        }

        return ucsChar;
    }

    byte toOracleCharacter(char srcChar, char lowSurrogate) throws SQLException {
        int l1Key = srcChar >>> '\b' & 0xFF;
        int l2Key = srcChar & 0xFF;
        int l3Key = lowSurrogate >>> '\b' & 0xFF;
        int l4Key = lowSurrogate & 0xFF;

        if ((this.m_oraCharLevel1[l1Key] != (char) this.m_oraCharLevel2Size)
                && (this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] != 65535)
                && (this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] != 65535)
                && (this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)] != 65535)) {
            return (byte) this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)];
        }

        DatabaseError.throwSqlException(155);

        return 0;
    }

    byte toOracleCharacter(char srcChar) throws SQLException {
        int l3Key = srcChar >>> '\b';
        int l4Key = srcChar & 0xFF;
        char ret;
        if ((ret = this.m_oraCharLevel2[(this.m_oraCharLevel1[l3Key] + l4Key)]) != 65535) {
            return (byte) ret;
        }

        DatabaseError.throwSqlException(155);

        return 0;
    }

    byte toOracleCharacterWithReplacement(char srcChar, char lowSurrogate) {
        int l1Key = srcChar >>> '\b' & 0xFF;
        int l2Key = srcChar & 0xFF;
        int l3Key = lowSurrogate >>> '\b' & 0xFF;
        int l4Key = lowSurrogate & 0xFF;

        if ((this.m_oraCharLevel1[l1Key] != (char) this.m_oraCharLevel2Size)
                && (this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] != 65535)
                && (this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] != 65535)
                && (this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)] != 65535)) {
            return (byte) this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)];
        }

        return this.m_oraCharReplacement;
    }

    byte toOracleCharacterWithReplacement(char srcChar) {
        int l3Key = srcChar >>> '\b';
        int l4Key = srcChar & 0xFF;
        char ret;
        if ((ret = this.m_oraCharLevel2[(this.m_oraCharLevel1[l3Key] + l4Key)]) != 65535) {
            return (byte) ret;
        }

        return this.m_oraCharReplacement;
    }

    public String toUnicodeString(byte[] srcChar, int offset, int count) throws SQLException {
        int endPos = offset + count;

        StringBuffer ucsChars = new StringBuffer(count);
        int ucs2 = 0;

        for (int i = offset; i < endPos; i++) {
            ucs2 = this.m_ucsChar[(srcChar[i] & 0xFF)];

            if (ucs2 == this.m_ucsReplacement) {
                DatabaseError.throwSqlException(154);
            }

            if ((ucs2 & 0xFFFFFFFF) > 65535L) {
                ucsChars.append((char) (ucs2 >>> 16));
                ucsChars.append((char) (ucs2 & 0xFFFF));
            } else {
                ucsChars.append((char) ucs2);
            }
        }

        return ucsChars.toString();
    }

    public String toUnicodeStringWithReplacement(byte[] srcChar, int offset, int count) {
        int endPos = offset + count;
        StringBuffer ucsChars = new StringBuffer(count);
        int ucs2 = 0;

        for (int i = offset; i < endPos; i++) {
            ucs2 = this.m_ucsChar[(srcChar[i] & 0xFF)];

            if (ucs2 == -1)
                ucsChars.append((char) this.m_ucsReplacement);
            else {
                ucsChars.append((char) ucs2);
            }
        }
        return ucsChars.toString();
    }

    public byte[] toOracleString(String srcString) throws SQLException {
        int srcLength = srcString.length();

        if (srcLength == 0) {
            return new byte[0];
        }

        char[] srcChars = new char[srcLength];

        srcString.getChars(0, srcLength, srcChars, 0);

        byte[] oracleChars = new byte[srcLength * 4];

        int oraCharIdx = 0;

        for (int i = 0; i < srcLength; i++) {
            if ((srcChars[i] >= 55296) && (srcChars[i] < 56320)) {
                if ((i + 1 < srcLength) && (srcChars[(i + 1)] >= 56320)
                        && (srcChars[(i + 1)] <= 57343)) {
                    if (this.noSurrogate) {
                        DatabaseError.throwSqlException(155);
                    } else {
                        oracleChars[(oraCharIdx++)] = toOracleCharacter(srcChars[i],
                                                                        srcChars[(i + 1)]);
                    }

                    i++;
                } else {
                    DatabaseError.throwSqlException(155);
                }

            } else if ((srcChars[i] < '') && (this.strictASCII)) {
                oracleChars[(oraCharIdx++)] = (byte) srcChars[i];
            } else {
                oracleChars[(oraCharIdx++)] = toOracleCharacter(srcChars[i]);
            }

        }

        if (oraCharIdx < oracleChars.length) {
            byte[] returnArray = new byte[oraCharIdx];

            System.arraycopy(oracleChars, 0, returnArray, 0, oraCharIdx);

            return returnArray;
        }

        return oracleChars;
    }

    public byte[] toOracleStringWithReplacement(String srcString) {
        int srcLength = srcString.length();

        if (srcLength == 0) {
            return new byte[0];
        }

        char[] srcChars = new char[srcLength];

        srcString.getChars(0, srcLength, srcChars, 0);

        byte[] oracleChars = new byte[srcLength * 4];

        int oraCharIdx = 0;

        for (int i = 0; i < srcLength; i++) {
            if ((srcChars[i] >= 55296) && (srcChars[i] < 56320)) {
                if ((i + 1 < srcLength) && (srcChars[(i + 1)] >= 56320)
                        && (srcChars[(i + 1)] <= 57343)) {
                    if (this.noSurrogate) {
                        oracleChars[(oraCharIdx++)] = this.m_oraCharReplacement;
                    } else {
                        oracleChars[(oraCharIdx++)] = toOracleCharacterWithReplacement(
                                                                                       srcChars[i],
                                                                                       srcChars[(i + 1)]);
                    }

                    i++;
                } else {
                    oracleChars[(oraCharIdx++)] = this.m_oraCharReplacement;
                }

            } else if ((srcChars[i] < '') && (this.strictASCII)) {
                oracleChars[(oraCharIdx++)] = (byte) srcChars[i];
            } else {
                oracleChars[(oraCharIdx++)] = toOracleCharacterWithReplacement(srcChars[i]);
            }

        }

        if (oraCharIdx < oracleChars.length) {
            byte[] returnArray = new byte[oraCharIdx];

            System.arraycopy(oracleChars, 0, returnArray, 0, oraCharIdx);

            return returnArray;
        }

        return oracleChars;
    }

    public void buildUnicodeToOracleMapping() {
        this.m_oraCharLevel1 = new char[256];
        this.m_oraCharSurrogateLevel = null;
        this.m_oraCharLevel2 = null;
        int i = 0;

        Vector mapStore = new Vector(45055, 12287);
        Hashtable htable = new Hashtable();
        Hashtable htable2 = new Hashtable();

        int mapCount = this.m_ucsChar.length;
        char lev2Freeptr = '\000';
        char surLevFreeptr = '\000';

        for (i = 0; i < 256; i++) {
            this.m_oraCharLevel1[i] = 65535;
        }

        for (i = 0; i < mapCount; i++) {
            int ucsCodePt = this.m_ucsChar[i];

            if (ucsCodePt == -1) {
                continue;
            }

            int[] store = new int[2];

            store[0] = ucsCodePt;
            store[1] = i;

            mapStore.addElement(store);
            storeMappingRange(ucsCodePt, htable, htable2);
        }

        if (this.extraUnicodeToOracleMapping != null) {
            mapCount = this.extraUnicodeToOracleMapping.length;

            for (i = 0; i < mapCount; i++) {
                int ucsCodePt = this.extraUnicodeToOracleMapping[i][0];

                storeMappingRange(ucsCodePt, htable, htable2);
            }
        }

        Enumeration enum = htable.keys();

        int surLevSize = 0;
        int oraCharLevel2Size = 0;

        while (enum.hasMoreElements()) {
            Object key = enum.nextElement();
            char[] range = (char[]) htable.get(key);

            if (range == null) {
                continue;
            }
            surLevSize += 256;
        }

        enum = htable2.keys();

        while (enum.hasMoreElements()) {
            Object key = enum.nextElement();
            char[] range = (char[]) htable2.get(key);

            if (range == null) {
                continue;
            }
            oraCharLevel2Size += 256;
        }

        if (surLevSize != 0) {
            this.m_oraCharSurrogateLevel = new char[surLevSize];
        }

        if (oraCharLevel2Size != 0) {
            this.m_oraCharLevel2 = new char[oraCharLevel2Size + 256];
        }

        for (i = 0; i < surLevSize; i++) {
            this.m_oraCharSurrogateLevel[i] = 65535;
        }

        for (i = 0; i < oraCharLevel2Size + 256; i++) {
            this.m_oraCharLevel2[i] = 65535;
        }

        for (i = 0; i < mapStore.size(); i++) {
            int[] store = (int[]) mapStore.elementAt(i);

            int l1Key = store[0] >> 24 & 0xFF;
            int l2Key = store[0] >> 16 & 0xFF;
            int l3Key = store[0] >> 8 & 0xFF;
            int l4Key = store[0] & 0xFF;

            if ((l1Key >= 216) && (l1Key < 220)) {
                if (this.m_oraCharLevel1[l1Key] == 65535) {
                    this.m_oraCharLevel1[l1Key] = surLevFreeptr;
                    surLevFreeptr = (char) (surLevFreeptr + 'Ā');
                }

                if (this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] == 65535) {
                    this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] = surLevFreeptr;

                    surLevFreeptr = (char) (surLevFreeptr + 'Ā');
                }

                if (this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] == 65535) {
                    this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] = lev2Freeptr;

                    lev2Freeptr = (char) (lev2Freeptr + 'Ā');
                }

                if (this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)] != 65535) {
                    continue;
                }
                this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)] = (char) (store[1] & 0xFFFF);
            } else {
                if (this.m_oraCharLevel1[l3Key] == 65535) {
                    this.m_oraCharLevel1[l3Key] = lev2Freeptr;
                    lev2Freeptr = (char) (lev2Freeptr + 'Ā');
                }

                if (this.m_oraCharLevel2[(this.m_oraCharLevel1[l3Key] + l4Key)] != 65535) {
                    continue;
                }
                this.m_oraCharLevel2[(this.m_oraCharLevel1[l3Key] + l4Key)] = (char) (store[1] & 0xFFFF);
            }

        }

        if (this.extraUnicodeToOracleMapping != null) {
            mapCount = this.extraUnicodeToOracleMapping.length;

            for (i = 0; i < mapCount; i++) {
                int ucsCodePt = this.extraUnicodeToOracleMapping[i][0];

                int l1Key = ucsCodePt >>> 24 & 0xFF;
                int l2Key = ucsCodePt >>> 16 & 0xFF;
                int l3Key = ucsCodePt >>> 8 & 0xFF;
                int l4Key = ucsCodePt & 0xFF;

                if ((l1Key >= 216) && (l1Key < 220)) {
                    if (this.m_oraCharLevel1[l1Key] == 65535) {
                        this.m_oraCharLevel1[l1Key] = surLevFreeptr;
                        surLevFreeptr = (char) (surLevFreeptr + 'Ā');
                    }

                    if (this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] == 65535) {
                        this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] = surLevFreeptr;

                        surLevFreeptr = (char) (surLevFreeptr + 'Ā');
                    }

                    if (this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] == 65535) {
                        this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] = lev2Freeptr;

                        lev2Freeptr = (char) (lev2Freeptr + 'Ā');
                    }

                    this.m_oraCharLevel2[(this.m_oraCharSurrogateLevel[(this.m_oraCharSurrogateLevel[(this.m_oraCharLevel1[l1Key] + l2Key)] + l3Key)] + l4Key)] = (char) (this.extraUnicodeToOracleMapping[i][1] & 0xFF);
                } else {
                    if (this.m_oraCharLevel1[l3Key] == 65535) {
                        this.m_oraCharLevel1[l3Key] = lev2Freeptr;
                        lev2Freeptr = (char) (lev2Freeptr + 'Ā');
                    }

                    this.m_oraCharLevel2[(this.m_oraCharLevel1[l3Key] + l4Key)] = (char) (this.extraUnicodeToOracleMapping[i][1] & 0xFFFF);
                }
            }

        }

        if (this.m_oraCharSurrogateLevel == null)
            this.noSurrogate = true;
        else {
            this.noSurrogate = false;
        }
        this.strictASCII = true;

        for (i = 0; i < 128; i++) {
            if (this.m_oraCharLevel2[i] == i)
                continue;
            this.strictASCII = false;

            break;
        }

        for (i = 0; i < 256; i++) {
            if (this.m_oraCharLevel1[i] == 65535) {
                this.m_oraCharLevel1[i] = (char) oraCharLevel2Size;
            }
        }
        this.m_oraCharLevel2Size = oraCharLevel2Size;
    }

    public void extractCodepoints(Vector vtable) {
        int begin = 0;
        int end = 255;

        for (int x = begin; x <= end; x++) {
            try {
                int[] codepointPair = new int[2];
                codepointPair[0] = x;
                codepointPair[1] = toUnicode((byte) x);

                vtable.addElement(codepointPair);
            } catch (SQLException ex) {
            }
        }
    }

    public void extractExtraMappings(Vector vtable) {
        if (this.extraUnicodeToOracleMapping == null) {
            return;
        }

        for (int x = 0; x < this.extraUnicodeToOracleMapping.length; x++) {
            int[] codepointPair = new int[2];
            codepointPair[0] = this.extraUnicodeToOracleMapping[x][0];
            codepointPair[1] = this.extraUnicodeToOracleMapping[x][1];

            vtable.addElement(codepointPair);
        }
    }

    public boolean hasExtraMappings() {
        return this.extraUnicodeToOracleMapping != null;
    }

    public char getOraChar1ByteRep() {
        return (char) this.m_oraCharReplacement;
    }

    public char getOraChar2ByteRep() {
        return '\000';
    }

    public int getUCS2CharRep() {
        return this.m_ucsReplacement;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.converter.CharacterConverter1Byte JD-Core Version: 0.6.0
 */