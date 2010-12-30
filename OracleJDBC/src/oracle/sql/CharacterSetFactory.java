package oracle.sql;

import java.io.PrintStream;
import java.sql.SQLException;

abstract class CharacterSetFactory {
    public static final short DEFAULT_CHARSET = -1;
    public static final short ASCII_CHARSET = 1;
    public static final short ISO_LATIN_1_CHARSET = 31;
    public static final short UNICODE_1_CHARSET = 870;
    public static final short UNICODE_2_CHARSET = 871;

    public abstract CharacterSet make(int paramInt);

    public static void main(String[] argv) {
        CharacterSet unicode = CharacterSet.make(871);
        int[] charSets = { 1, 31, 870, 871 };

        for (int idX = 0; idX < charSets.length; idX++) {
            CharacterSet cs = CharacterSet.make(charSets[idX]);

            String longString = "longlonglonglong";
            longString = longString + longString + longString + longString;
            longString = longString + longString + longString + longString;
            longString = longString + longString + longString + longString;
            longString = longString + longString + longString + longString;

            String[] testStrings = { "abc", "ab?c", "XYZ", longString };

            for (int idS = 0; idS < testStrings.length; idS++) {
                String testString = testStrings[idS];
                String shortString = testString;

                if (testString.length() > 16) {
                    shortString = shortString.substring(0, 16) + "...";
                }

                System.out.println("testing " + cs + " against <" + shortString + ">");

                boolean ok = true;
                try {
                    byte[] raw = cs.convertWithReplacement(testString);
                    String withReplacement = cs.toStringWithReplacement(raw, 0, raw.length);

                    raw = cs.convert(withReplacement);

                    String roundTrip = cs.toString(raw, 0, raw.length);

                    if (!withReplacement.equals(roundTrip)) {
                        System.out.println("    FAILED roundTrip " + roundTrip);

                        ok = false;
                    }

                    if (cs.isLossyFrom(unicode)) {
                        try {
                            byte[] bad = cs.convert(testString);
                            String badString = cs.toString(bad, 0, bad.length);

                            if (!badString.equals(roundTrip)) {
                                System.out.println("    FAILED roundtrip, no throw");
                            }
                        } catch (SQLException ex) {
                        }
                    } else {
                        if (!roundTrip.equals(testString)) {
                            System.out.println("    FAILED roundTrip " + roundTrip);

                            ok = false;
                        }

                        byte[] utf = unicode.convert(testString);
                        byte[] rawWithoutReplacement = cs.convert(unicode, utf, 0, utf.length);

                        String withoutReplacement = cs.toString(rawWithoutReplacement, 0,
                                                                rawWithoutReplacement.length);

                        if (!withoutReplacement.equals(testString)) {
                            System.out.println("    FAILED withoutReplacement "
                                    + withoutReplacement);

                            ok = false;
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("    FAILED with Exception " + ex);
                }

                if (!ok)
                    continue;
                System.out.println("    PASSED " + (cs.isLossyFrom(unicode) ? "LOSSY" : ""));
            }
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetFactory JD-Core Version: 0.6.0
 */