package oracle.sql.converter;

import java.util.HashMap;
import java.util.Hashtable;
import oracle.sql.ConverterArchive;

public abstract class CharacterConverterJDBC extends CharacterConverters {
    static final String CONVERTERNAMEPREFIX = "converter_xcharset/lx2";
    static final String CONVERTERIDPREFIX = "0000";
    static final int HIBYTEMASK = 65280;
    static final int LOWBYTEMASK = 255;
    static final int STORE_INCREMENT = 10;
    static final int INVALID_ORA_CHAR = -1;
    static final int FIRSTBSHIFT = 24;
    static final int SECONDBSHIFT = 16;
    static final int THIRDBSHIFT = 8;
    static final int UB2MASK = 65535;
    static final int UB4MASK = 65535;
    static final HashMap m_converterStore = new HashMap();

    public static CharacterConverters getInstance(int oracleId) {
        CharacterConverterJDBC charConverter = null;
        int storeIndex = 0;
        int storeSize = 0;
        String numStr = Integer.toHexString(oracleId);

        synchronized (m_converterStore) {
            charConverter = (CharacterConverterJDBC) m_converterStore.get(numStr);

            if (charConverter != null) {
                return charConverter;
            }

            String charConvClassName = "converter_xcharset/lx2"
                    + "0000".substring(0, 4 - numStr.length()) + numStr;

            ConverterArchive ca = new ConverterArchive();

            charConverter = (CharacterConverterJDBC) ca.readObj(charConvClassName + ".glb");

            if (charConverter == null) {
                return null;
            }

            charConverter.buildUnicodeToOracleMapping();
            m_converterStore.put(numStr, charConverter);

            return charConverter;
        }
    }

    protected void storeMappingRange(int ucsCodePt, Hashtable htable, Hashtable htable2) {
        int l1Key = ucsCodePt >> 24 & 0xFF;
        int l2Key = ucsCodePt >> 16 & 0xFF;
        int l3Key = ucsCodePt >> 8 & 0xFF;
        int l4Key = ucsCodePt & 0xFF;
        Integer int1Key = new Integer(l1Key);
        Integer int12Key = new Integer(ucsCodePt >> 16 & 0xFFFF);
        Integer int123Key = new Integer(ucsCodePt >> 8 & 0xFFFFFF);

        if (ucsCodePt >>> 26 == 54) {
            char[] range = (char[]) htable.get(int1Key);

            if (range == null) {
                range = new char[] { 'ÿ', '\000' };
            }

            if ((range[0] == 'ÿ') && (range[1] == 0)) {
                range[0] = (char) l2Key;
                range[1] = (char) l2Key;
            } else {
                if (l2Key < (range[0] & 0xFFFF)) {
                    range[0] = (char) l2Key;
                }

                if (l2Key > (range[0] & 0xFFFF)) {
                    range[1] = (char) l2Key;
                }
            }

            htable.put(int1Key, range);

            range = (char[]) htable.get(int12Key);

            if (range == null) {
                range = new char[] { 'ÿ', '\000' };
            }

            if ((range[0] == 'ÿ') && (range[1] == 0)) {
                range[0] = (char) l3Key;
                range[1] = (char) l3Key;
            } else {
                if (l3Key < (range[0] & 0xFFFF)) {
                    range[0] = (char) l3Key;
                }

                if (l3Key > (range[0] & 0xFFFF)) {
                    range[1] = (char) l3Key;
                }
            }

            htable.put(int12Key, range);
        }

        char[] range = (char[]) htable2.get(int123Key);

        if (range == null) {
            range = new char[] { 'ÿ', '\000' };
        }

        if ((range[0] == 'ÿ') && (range[1] == 0)) {
            range[0] = (char) l4Key;
            range[1] = (char) l4Key;
        } else {
            if (l4Key < (range[0] & 0xFFFF)) {
                range[0] = (char) l4Key;
            }

            if (l4Key > (range[0] & 0xFFFF)) {
                range[1] = (char) l4Key;
            }
        }

        htable2.put(int123Key, range);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.converter.CharacterConverterJDBC JD-Core Version: 0.6.0
 */