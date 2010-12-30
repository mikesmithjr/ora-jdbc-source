package oracle.sql.converter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import oracle.jdbc.driver.DatabaseError;

public class CharacterSetMetaData {
    static final short WIDTH_SIZE = 8;
    static final short WIDTH_MASK = 255;
    static final short FLAG_FIXEDWIDTH = 256;
    public static final int ST_BADCODESET = 0;
    private static final HashMap JAVALOC2NLSLOC = createJavaLocale2NLSLocale();

    private static final short[][] m_maxCharWidth = { { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 },
            { 5, 1 }, { 6, 1 }, { 7, 1 }, { 8, 1 }, { 9, 1 }, { 10, 1 }, { 11, 1 }, { 12, 1 },
            { 13, 1 }, { 14, 1 }, { 15, 1 }, { 16, 1 }, { 17, 1 }, { 18, 1 }, { 19, 1 }, { 20, 1 },
            { 21, 1 }, { 22, 1 }, { 23, 1 }, { 25, 1 }, { 27, 1 }, { 28, 1 }, { 31, 1 }, { 32, 1 },
            { 33, 1 }, { 34, 1 }, { 35, 1 }, { 36, 1 }, { 37, 1 }, { 38, 1 }, { 39, 1 }, { 40, 1 },
            { 41, 1 }, { 42, 1 }, { 43, 1 }, { 44, 1 }, { 45, 1 }, { 46, 1 }, { 47, 1 }, { 48, 1 },
            { 49, 1 }, { 50, 1 }, { 51, 1 }, { 61, 1 }, { 70, 1 }, { 72, 1 }, { 81, 1 }, { 82, 1 },
            { 90, 1 }, { 91, 1 }, { 92, 1 }, { 93, 1 }, { 94, 1 }, { 95, 1 }, { 96, 1 }, { 97, 1 },
            { 98, 1 }, { 99, 1 }, { 100, 1 }, { 101, 1 }, { 110, 1 }, { 113, 1 }, { 114, 1 },
            { 140, 1 }, { 150, 1 }, { 152, 1 }, { 153, 1 }, { 154, 1 }, { 155, 1 }, { 156, 1 },
            { 158, 1 }, { 159, 1 }, { 160, 1 }, { 161, 1 }, { 162, 1 }, { 163, 1 }, { 164, 1 },
            { 165, 1 }, { 166, 1 }, { 167, 1 }, { 170, 1 }, { 171, 1 }, { 172, 1 }, { 173, 1 },
            { 174, 1 }, { 175, 1 }, { 176, 1 }, { 177, 1 }, { 178, 1 }, { 179, 1 }, { 180, 1 },
            { 181, 1 }, { 182, 1 }, { 183, 1 }, { 184, 1 }, { 185, 1 }, { 186, 1 }, { 187, 1 },
            { 188, 1 }, { 189, 1 }, { 190, 1 }, { 191, 1 }, { 192, 1 }, { 193, 1 }, { 194, 1 },
            { 195, 1 }, { 196, 1 }, { 197, 1 }, { 198, 1 }, { 199, 1 }, { 200, 1 }, { 201, 1 },
            { 202, 1 }, { 203, 1 }, { 204, 1 }, { 205, 1 }, { 206, 1 }, { 207, 1 }, { 208, 1 },
            { 210, 1 }, { 211, 1 }, { 221, 1 }, { 222, 1 }, { 223, 1 }, { 224, 1 }, { 225, 1 },
            { 226, 1 }, { 230, 1 }, { 231, 1 }, { 232, 1 }, { 233, 1 }, { 235, 1 }, { 239, 1 },
            { 241, 1 }, { 251, 1 }, { 261, 1 }, { 262, 1 }, { 263, 1 }, { 264, 1 }, { 265, 1 },
            { 266, 1 }, { 267, 1 }, { 277, 1 }, { 278, 1 }, { 279, 1 }, { 301, 1 }, { 311, 1 },
            { 312, 1 }, { 314, 1 }, { 315, 1 }, { 316, 1 }, { 317, 1 }, { 319, 1 }, { 320, 1 },
            { 322, 1 }, { 323, 1 }, { 324, 1 }, { 351, 1 }, { 352, 1 }, { 353, 1 }, { 354, 1 },
            { 368, 1 }, { 380, 1 }, { 381, 1 }, { 382, 1 }, { 383, 1 }, { 384, 1 }, { 385, 1 },
            { 386, 1 }, { 390, 1 }, { 401, 1 }, { 500, 1 }, { 504, 1 }, { 505, 1 }, { 506, 1 },
            { 507, 1 }, { 508, 1 }, { 509, 1 }, { 511, 1 }, { 514, 1 }, { 554, 1 }, { 555, 1 },
            { 556, 1 }, { 557, 1 }, { 558, 1 }, { 559, 1 }, { 560, 1 }, { 561, 1 }, { 563, 1 },
            { 565, 1 }, { 566, 1 }, { 567, 1 }, { 590, 1 }, { 798, 1 }, { 799, 258 }, { 829, 2 },
            { 830, 3 }, { 831, 3 }, { 832, 2 }, { 833, 3 }, { 834, 2 }, { 835, 3 }, { 836, 2 },
            { 837, 3 }, { 838, 2 }, { 840, 2 }, { 842, 3 }, { 845, 2 }, { 846, 2 }, { 850, 2 },
            { 851, 2 }, { 852, 2 }, { 853, 3 }, { 854, 4 }, { 860, 4 }, { 861, 4 }, { 862, 2 },
            { 863, 4 }, { 864, 3 }, { 865, 2 }, { 866, 2 }, { 867, 2 }, { 868, 2 }, { 870, 3 },
            { 871, 3 }, { 872, 4 }, { 873, 4 }, { 994, 2 }, { 995, 2 }, { 996, 3 }, { 997, 2 },
            { 998, 3 }, { 1001, 258 }, { 1830, 258 }, { 1832, 258 }, { 1833, 258 }, { 1840, 258 },
            { 1842, 258 }, { 1850, 258 }, { 1852, 258 }, { 1853, 258 }, { 1860, 258 },
            { 1863, 260 }, { 1864, 258 }, { 1865, 258 }, { 2000, 258 }, { 2002, 258 }, { 9996, 3 },
            { 9997, 3 }, { 9998, 3 }, { 9999, 3 } };

    public static String getNLSLanguage(Locale javaLocale) {
        String nlsLanguage = null;

        nlsLanguage = (String) JAVALOC2NLSLOC.get(javaLocale);

        if (nlsLanguage == null) {
            return null;
        }

        return nlsLanguage.substring(0, nlsLanguage.indexOf('_'));
    }

    public static String getNLSTerritory(Locale javaLocale) {
        String nlsTerritory = null;

        nlsTerritory = (String) JAVALOC2NLSLOC.get(javaLocale);

        if (nlsTerritory == null) {
            return null;
        }

        return nlsTerritory.substring(nlsTerritory.indexOf('_') + 1);
    }

    public static boolean isFixedWidth(int encodingId) throws SQLException {
        if (encodingId == 0)
            return false;

        int index = -1;
        int lower = 0;
        int upper = m_maxCharWidth.length - 1;
        int mid = -1;

        while (lower <= upper) {
            mid = (lower + upper) / 2;

            if (encodingId < m_maxCharWidth[mid][0]) {
                upper = mid - 1;
                continue;
            }
            if (encodingId > m_maxCharWidth[mid][0]) {
                lower = mid + 1;
                continue;
            }

            index = mid;
        }

        if (index < 0) {
            DatabaseError.throwSqlException(35);
            return false;
        }

        return (m_maxCharWidth[index][1] & 0x100) != 0;
    }

    public static int getRatio(int dstEncodingId, int srcEncodingId) {
        int dstIndex = -1;
        int lower = 0;
        int upper = m_maxCharWidth.length - 1;

        if (srcEncodingId == dstEncodingId) {
            return 1;
        }

        while (lower <= upper) {
            int mid = (lower + upper) / 2;

            if (dstEncodingId < m_maxCharWidth[mid][0]) {
                upper = mid - 1;
                continue;
            }
            if (dstEncodingId > m_maxCharWidth[mid][0]) {
                lower = mid + 1;
                continue;
            }

            dstIndex = mid;
        }

        if (dstIndex < 0) {
            return 0;
        }

        int srcIndex = -1;

        lower = 0;
        upper = m_maxCharWidth.length - 1;

        while (lower <= upper) {
            int mid = (lower + upper) / 2;

            if (srcEncodingId < m_maxCharWidth[mid][0]) {
                upper = mid - 1;
                continue;
            }
            if (srcEncodingId > m_maxCharWidth[mid][0]) {
                lower = mid + 1;
                continue;
            }

            srcIndex = mid;
        }

        if (srcIndex < 0) {
            return 0;
        }

        int dstMaxWidth = m_maxCharWidth[dstIndex][1] & 0xFF;

        if (dstMaxWidth == 1) {
            return 1;
        }

        if (m_maxCharWidth[srcIndex][1] >> 8 == 0) {
            return dstMaxWidth;
        }

        int srcMaxWidth = m_maxCharWidth[srcIndex][1] & 0xFF;
        int ratio = dstMaxWidth / srcMaxWidth;

        if (dstMaxWidth % srcMaxWidth != 0) {
            ratio++;
        }

        return ratio;
    }

    private static HashMap createJavaLocale2NLSLocale() {
        HashMap tab = new HashMap(159, 1.0F);

        tab.put(new Locale("ar", ""), "ARABIC_SAUDI ARABIA");
        tab.put(new Locale("ar", "AE"), "ARABIC_UNITED ARAB EMIRATES");
        tab.put(new Locale("ar", "BH"), "ARABIC_BAHRAIN");
        tab.put(new Locale("ar", "DJ"), "ARABIC_DJIBOUTI");
        tab.put(new Locale("ar", "DZ"), "ARABIC_ALGERIA");
        tab.put(new Locale("ar", "EG"), "EGYPTIAN_EGYPT");
        tab.put(new Locale("ar", "IQ"), "ARABIC_IRAQ");
        tab.put(new Locale("ar", "JO"), "ARABIC_JORDAN");
        tab.put(new Locale("ar", "KW"), "ARABIC_KUWAIT");
        tab.put(new Locale("ar", "LB"), "ARABIC_LEBANON");
        tab.put(new Locale("ar", "LY"), "ARABIC_LIBYA");
        tab.put(new Locale("ar", "MA"), "ARABIC_MOROCCO");
        tab.put(new Locale("ar", "OM"), "ARABIC_OMAN");
        tab.put(new Locale("ar", "QA"), "ARABIC_QATAR");
        tab.put(new Locale("ar", "SA"), "ARABIC_SAUDI ARABIA");
        tab.put(new Locale("ar", "SD"), "ARABIC_SUDAN");
        tab.put(new Locale("ar", "SO"), "ARABIC_SOMALIA");
        tab.put(new Locale("ar", "SY"), "ARABIC_SYRIA");
        tab.put(new Locale("ar", "TN"), "ARABIC_TUNISIA");
        tab.put(new Locale("ar", "YE"), "ARABIC_YEMEN");
        tab.put(new Locale("as", ""), "ASSAMESE_INDIA");
        tab.put(new Locale("as", "IN"), "ASSAMESE_INDIA");
        tab.put(new Locale("bg", ""), "BULGARIAN_BULGARIA");
        tab.put(new Locale("bg", "BG"), "BULGARIAN_BULGARIA");
        tab.put(new Locale("bn", ""), "BANGLA_BANGLADESH");
        tab.put(new Locale("bn", "BD"), "BANGLA_BANGLADESH");
        tab.put(new Locale("bn", "IN"), "BANGLA_INDIA");
        tab.put(new Locale("ca", ""), "CATALAN_CATALONIA");
        tab.put(new Locale("ca", "ES"), "CATALAN_CATALONIA");
        tab.put(new Locale("cs", ""), "CZECH_CZECH REPUBLIC");
        tab.put(new Locale("cs", "CZ"), "CZECH_CZECH REPUBLIC");
        tab.put(new Locale("da", ""), "DANISH_DENMARK");
        tab.put(new Locale("da", "DK"), "DANISH_DENMARK");
        tab.put(new Locale("de", ""), "GERMAN_GERMANY");
        tab.put(new Locale("de", "AT"), "GERMAN_AUSTRIA");
        tab.put(new Locale("de", "BE"), "GERMAN_BELGIUM");
        tab.put(new Locale("de", "CH"), "GERMAN_SWITZERLAND");
        tab.put(new Locale("de", "DE"), "GERMAN_GERMANY");
        tab.put(new Locale("de", "LU"), "GERMAN_LUXEMBOURG");
        tab.put(new Locale("el", ""), "GREEK_GREECE");
        tab.put(new Locale("el", "CY"), "GREEK_CYPRUS");
        tab.put(new Locale("el", "GR"), "GREEK_GREECE");
        tab.put(new Locale("en", ""), "AMERICAN_AMERICA");
        tab.put(new Locale("en", "AU"), "ENGLISH_AUSTRALIA");
        tab.put(new Locale("en", "CA"), "ENGLISH_CANADA");
        tab.put(new Locale("en", "GB"), "ENGLISH_UNITED KINGDOM");
        tab.put(new Locale("en", "HK"), "ENGLISH_HONG KONG");
        tab.put(new Locale("en", "IE"), "ENGLISH_IRELAND");
        tab.put(new Locale("en", "IN"), "ENGLISH_INDIA");
        tab.put(new Locale("en", "NZ"), "ENGLISH_NEW ZEALAND");
        tab.put(new Locale("en", "SG"), "ENGLISH_SINGAPORE");
        tab.put(new Locale("en", "US"), "AMERICAN_AMERICA");
        tab.put(new Locale("en", "ZA"), "ENGLISH_SOUTH AFRICA");
        tab.put(new Locale("es", ""), "LATIN AMERICAN SPANISH_AMERICA");
        tab.put(new Locale("es", "CL"), "LATIN AMERICAN SPANISH_CHILE");
        tab.put(new Locale("es", "CO"), "LATIN AMERICAN SPANISH_COLOMBIA");
        tab.put(new Locale("es", "CR"), "LATIN AMERICAN SPANISH_COSTA RICA");
        tab.put(new Locale("es", "ES"), "SPANISH_SPAIN");
        tab.put(new Locale("es", "GT"), "LATIN AMERICAN SPANISH_GUATEMALA");
        tab.put(new Locale("es", "MX"), "MEXICAN SPANISH_MEXICO");
        tab.put(new Locale("es", "NI"), "LATIN AMERICAN SPANISH_NICARAGUA");
        tab.put(new Locale("es", "PA"), "LATIN AMERICAN SPANISH_PANAMA");
        tab.put(new Locale("es", "PE"), "LATIN AMERICAN SPANISH_PERU");
        tab.put(new Locale("es", "PR"), "LATIN AMERICAN SPANISH_PUERTO RICO");
        tab.put(new Locale("es", "SV"), "LATIN AMERICAN SPANISH_EL SALVADOR");
        tab.put(new Locale("es", "VE"), "LATIN AMERICAN SPANISH_VENEZUELA");
        tab.put(new Locale("et", ""), "ESTONIAN_ESTONIA");
        tab.put(new Locale("et", "EE"), "ESTONIAN_ESTONIA");
        tab.put(new Locale("fi", ""), "FINNISH_FINLAND");
        tab.put(new Locale("fi", "FI"), "FINNISH_FINLAND");
        tab.put(new Locale("fr", ""), "FRENCH_FRANCE");
        tab.put(new Locale("fr", "BE"), "FRENCH_BELGIUM");
        tab.put(new Locale("fr", "CA"), "CANADIAN FRENCH_CANADA");
        tab.put(new Locale("fr", "CH"), "FRENCH_SWITZERLAND");
        tab.put(new Locale("fr", "DJ"), "FRENCH_DJIBOUTI");
        tab.put(new Locale("fr", "FR"), "FRENCH_FRANCE");
        tab.put(new Locale("fr", "LU"), "FRENCH_LUXEMBOURG");
        tab.put(new Locale("fr", "MR"), "FRENCH_MAURITANIA");
        tab.put(new Locale("gu", ""), "GUJARATI_INDIA");
        tab.put(new Locale("gu", "IN"), "GUJARATI_INDIA");
        tab.put(new Locale("he", ""), "HEBREW_ISRAEL");
        tab.put(new Locale("he", "IL"), "HEBREW_ISRAEL");
        tab.put(new Locale("hi", ""), "HINDI_INDIA");
        tab.put(new Locale("hi", "IN"), "HINDI_INDIA");
        tab.put(new Locale("hr", ""), "CROATIAN_CROATIA");
        tab.put(new Locale("hr", "HR"), "CROATIAN_CROATIA");
        tab.put(new Locale("hu", ""), "HUNGARIAN_HUNGARY");
        tab.put(new Locale("hu", "HU"), "HUNGARIAN_HUNGARY");
        tab.put(new Locale("id", ""), "INDONESIAN_INDONESIA");
        tab.put(new Locale("id", "ID"), "INDONESIAN_INDONESIA");
        tab.put(new Locale("in", ""), "INDONESIAN_INDONESIA");
        tab.put(new Locale("in", "ID"), "INDONESIAN_INDONESIA");
        tab.put(new Locale("is", ""), "ICELANDIC_ICELAND");
        tab.put(new Locale("is", "IS"), "ICELANDIC_ICELAND");
        tab.put(new Locale("it", ""), "ITALIAN_ITALY");
        tab.put(new Locale("it", "CH"), "ITALIAN_SWITZERLAND");
        tab.put(new Locale("it", "IT"), "ITALIAN_ITALY");
        tab.put(new Locale("iw", ""), "HEBREW_ISRAEL");
        tab.put(new Locale("iw", "IL"), "HEBREW_ISRAEL");
        tab.put(new Locale("ja", ""), "JAPANESE_JAPAN");
        tab.put(new Locale("ja", "JP"), "JAPANESE_JAPAN");
        tab.put(new Locale("kn", ""), "KANNADA_INDIA");
        tab.put(new Locale("kn", "IN"), "KANNADA_INDIA");
        tab.put(new Locale("ko", ""), "KOREAN_KOREA");
        tab.put(new Locale("ko", "KR"), "KOREAN_KOREA");
        tab.put(new Locale("lt", ""), "LITHUANIAN_LITHUANIA");
        tab.put(new Locale("lt", "LT"), "LITHUANIAN_LITHUANIA");
        tab.put(new Locale("lv", ""), "LATVIAN_LATVIA");
        tab.put(new Locale("lv", "LV"), "LATVIAN_LATVIA");
        tab.put(new Locale("ml", ""), "MALAYALAM_INDIA");
        tab.put(new Locale("ml", "IN"), "MALAYALAM_INDIA");
        tab.put(new Locale("mr", ""), "MARATHI_INDIA");
        tab.put(new Locale("mr", "IN"), "MARATHI_INDIA");
        tab.put(new Locale("ms", ""), "MALAY_MALAYSIA");
        tab.put(new Locale("ms", "MY"), "MALAY_MALAYSIA");
        tab.put(new Locale("ms", "SG"), "MALAY_SINGAPORE");
        tab.put(new Locale("nl", ""), "DUTCH_THE NETHERLANDS");
        tab.put(new Locale("nl", "BE"), "DUTCH_BELGIUM");
        tab.put(new Locale("nl", "NL"), "DUTCH_THE NETHERLANDS");
        tab.put(new Locale("no", ""), "NORWEGIAN_NORWAY");
        tab.put(new Locale("no", "NO"), "NORWEGIAN_NORWAY");
        tab.put(new Locale("or", ""), "ORIYA_INDIA");
        tab.put(new Locale("or", "IN"), "ORIYA_INDIA");
        tab.put(new Locale("pa", ""), "PUNJABI_INDIA");
        tab.put(new Locale("pa", "IN"), "PUNJABI_INDIA");
        tab.put(new Locale("pl", ""), "POLISH_POLAND");
        tab.put(new Locale("pl", "PL"), "POLISH_POLAND");
        tab.put(new Locale("pt", ""), "PORTUGUESE_PORTUGAL");
        tab.put(new Locale("pt", "BR"), "BRAZILIAN PORTUGUESE_BRAZIL");
        tab.put(new Locale("pt", "PT"), "PORTUGUESE_PORTUGAL");
        tab.put(new Locale("ro", ""), "ROMANIAN_ROMANIA");
        tab.put(new Locale("ro", "RO"), "ROMANIAN_ROMANIA");
        tab.put(new Locale("ru", ""), "RUSSIAN_CIS");
        tab.put(new Locale("ru", "RU"), "RUSSIAN_CIS");
        tab.put(new Locale("sk", ""), "SLOVAK_SLOVAKIA");
        tab.put(new Locale("sk", "SK"), "SLOVAK_SLOVAKIA");
        tab.put(new Locale("sl", ""), "SLOVENIAN_SLOVENIA");
        tab.put(new Locale("sl", "SI"), "SLOVENIAN_SLOVENIA");
        tab.put(new Locale("sv", ""), "SWEDISH_SWEDEN");
        tab.put(new Locale("sv", "FI"), "SWEDISH_FINLAND");
        tab.put(new Locale("sv", "SE"), "SWEDISH_SWEDEN");
        tab.put(new Locale("ta", ""), "TAMIL_INDIA");
        tab.put(new Locale("ta", "IN"), "TAMIL_INDIA");
        tab.put(new Locale("ta", "SG"), "TAMIL_SINGAPORE");
        tab.put(new Locale("te", ""), "TELUGU_INDIA");
        tab.put(new Locale("te", "IN"), "TELUGU_INDIA");
        tab.put(new Locale("th", ""), "THAI_THAILAND");
        tab.put(new Locale("th", "TH"), "THAI_THAILAND");
        tab.put(new Locale("tr", ""), "TURKISH_TURKEY");
        tab.put(new Locale("tr", "TR"), "TURKISH_TURKEY");
        tab.put(new Locale("uk", ""), "UKRAINIAN_UKRAINE");
        tab.put(new Locale("uk", "UA"), "UKRAINIAN_UKRAINE");
        tab.put(new Locale("vi", ""), "VIETNAMESE_VIETNAM");
        tab.put(new Locale("vi", "VN"), "VIETNAMESE_VIETNAM");
        tab.put(new Locale("zh", ""), "SIMPLIFIED CHINESE_CHINA");
        tab.put(new Locale("zh", "CN"), "SIMPLIFIED CHINESE_CHINA");
        tab.put(new Locale("zh", "HK"), "TRADITIONAL CHINESE_HONG KONG");
        tab.put(new Locale("zh", "SG"), "SIMPLIFIED CHINESE_SINGAPORE");
        tab.put(new Locale("zh", "TW"), "TRADITIONAL CHINESE_TAIWAN");

        return tab;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.converter.CharacterSetMetaData JD-Core Version: 0.6.0
 */