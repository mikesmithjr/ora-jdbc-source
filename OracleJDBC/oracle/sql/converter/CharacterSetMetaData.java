package oracle.sql.converter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import oracle.jdbc.driver.DatabaseError;

public class CharacterSetMetaData
{
  static final short WIDTH_SIZE = 8;
  static final short WIDTH_MASK = 255;
  static final short FLAG_FIXEDWIDTH = 256;
  public static final int ST_BADCODESET = 0;
  private static final HashMap JAVALOC2NLSLOC = createJavaLocale2NLSLocale();

  private static final short[][] m_maxCharWidth = { { 1, 1 }, { 2, 1 }, { 3, 1 }, { 4, 1 }, { 5, 1 }, { 6, 1 }, { 7, 1 }, { 8, 1 }, { 9, 1 }, { 10, 1 }, { 11, 1 }, { 12, 1 }, { 13, 1 }, { 14, 1 }, { 15, 1 }, { 16, 1 }, { 17, 1 }, { 18, 1 }, { 19, 1 }, { 20, 1 }, { 21, 1 }, { 22, 1 }, { 23, 1 }, { 25, 1 }, { 27, 1 }, { 28, 1 }, { 31, 1 }, { 32, 1 }, { 33, 1 }, { 34, 1 }, { 35, 1 }, { 36, 1 }, { 37, 1 }, { 38, 1 }, { 39, 1 }, { 40, 1 }, { 41, 1 }, { 42, 1 }, { 43, 1 }, { 44, 1 }, { 45, 1 }, { 46, 1 }, { 47, 1 }, { 48, 1 }, { 49, 1 }, { 50, 1 }, { 51, 1 }, { 61, 1 }, { 70, 1 }, { 72, 1 }, { 81, 1 }, { 82, 1 }, { 90, 1 }, { 91, 1 }, { 92, 1 }, { 93, 1 }, { 94, 1 }, { 95, 1 }, { 96, 1 }, { 97, 1 }, { 98, 1 }, { 99, 1 }, { 100, 1 }, { 101, 1 }, { 110, 1 }, { 113, 1 }, { 114, 1 }, { 140, 1 }, { 150, 1 }, { 152, 1 }, { 153, 1 }, { 154, 1 }, { 155, 1 }, { 156, 1 }, { 158, 1 }, { 159, 1 }, { 160, 1 }, { 161, 1 }, { 162, 1 }, { 163, 1 }, { 164, 1 }, { 165, 1 }, { 166, 1 }, { 167, 1 }, { 170, 1 }, { 171, 1 }, { 172, 1 }, { 173, 1 }, { 174, 1 }, { 175, 1 }, { 176, 1 }, { 177, 1 }, { 178, 1 }, { 179, 1 }, { 180, 1 }, { 181, 1 }, { 182, 1 }, { 183, 1 }, { 184, 1 }, { 185, 1 }, { 186, 1 }, { 187, 1 }, { 188, 1 }, { 189, 1 }, { 190, 1 }, { 191, 1 }, { 192, 1 }, { 193, 1 }, { 194, 1 }, { 195, 1 }, { 196, 1 }, { 197, 1 }, { 198, 1 }, { 199, 1 }, { 200, 1 }, { 201, 1 }, { 202, 1 }, { 203, 1 }, { 204, 1 }, { 205, 1 }, { 206, 1 }, { 207, 1 }, { 208, 1 }, { 210, 1 }, { 211, 1 }, { 221, 1 }, { 222, 1 }, { 223, 1 }, { 224, 1 }, { 225, 1 }, { 226, 1 }, { 230, 1 }, { 231, 1 }, { 232, 1 }, { 233, 1 }, { 235, 1 }, { 239, 1 }, { 241, 1 }, { 251, 1 }, { 261, 1 }, { 262, 1 }, { 263, 1 }, { 264, 1 }, { 265, 1 }, { 266, 1 }, { 267, 1 }, { 277, 1 }, { 278, 1 }, { 279, 1 }, { 301, 1 }, { 311, 1 }, { 312, 1 }, { 314, 1 }, { 315, 1 }, { 316, 1 }, { 317, 1 }, { 319, 1 }, { 320, 1 }, { 322, 1 }, { 323, 1 }, { 324, 1 }, { 351, 1 }, { 352, 1 }, { 353, 1 }, { 354, 1 }, { 368, 1 }, { 380, 1 }, { 381, 1 }, { 382, 1 }, { 383, 1 }, { 384, 1 }, { 385, 1 }, { 386, 1 }, { 390, 1 }, { 401, 1 }, { 500, 1 }, { 504, 1 }, { 505, 1 }, { 506, 1 }, { 507, 1 }, { 508, 1 }, { 509, 1 }, { 511, 1 }, { 514, 1 }, { 554, 1 }, { 555, 1 }, { 556, 1 }, { 557, 1 }, { 558, 1 }, { 559, 1 }, { 560, 1 }, { 561, 1 }, { 563, 1 }, { 565, 1 }, { 566, 1 }, { 567, 1 }, { 590, 1 }, { 798, 1 }, { 799, 258 }, { 829, 2 }, { 830, 3 }, { 831, 3 }, { 832, 2 }, { 833, 3 }, { 834, 2 }, { 835, 3 }, { 836, 2 }, { 837, 3 }, { 838, 2 }, { 840, 2 }, { 842, 3 }, { 845, 2 }, { 846, 2 }, { 850, 2 }, { 851, 2 }, { 852, 2 }, { 853, 3 }, { 854, 4 }, { 860, 4 }, { 861, 4 }, { 862, 2 }, { 863, 4 }, { 864, 3 }, { 865, 2 }, { 866, 2 }, { 867, 2 }, { 868, 2 }, { 870, 3 }, { 871, 3 }, { 872, 4 }, { 873, 4 }, { 994, 2 }, { 995, 2 }, { 996, 3 }, { 997, 2 }, { 998, 3 }, { 1001, 258 }, { 1830, 258 }, { 1832, 258 }, { 1833, 258 }, { 1840, 258 }, { 1842, 258 }, { 1850, 258 }, { 1852, 258 }, { 1853, 258 }, { 1860, 258 }, { 1863, 260 }, { 1864, 258 }, { 1865, 258 }, { 2000, 258 }, { 2002, 258 }, { 9996, 3 }, { 9997, 3 }, { 9998, 3 }, { 9999, 3 } };

  public static String getNLSLanguage(Locale paramLocale)
  {
    String str = null;

    str = (String)JAVALOC2NLSLOC.get(paramLocale);

    if (str == null)
    {
      return null;
    }

    return str.substring(0, str.indexOf('_'));
  }

  public static String getNLSTerritory(Locale paramLocale)
  {
    String str = null;

    str = (String)JAVALOC2NLSLOC.get(paramLocale);

    if (str == null)
    {
      return null;
    }

    return str.substring(str.indexOf('_') + 1);
  }

  public static boolean isFixedWidth(int paramInt)
    throws SQLException
  {
    if (paramInt == 0) return false;

    int i = -1;
    int j = 0;
    int k = m_maxCharWidth.length - 1;
    int m = -1;

    while (j <= k)
    {
      m = (j + k) / 2;

      if (paramInt < m_maxCharWidth[m][0])
      {
        k = m - 1; continue;
      }
      if (paramInt > m_maxCharWidth[m][0])
      {
        j = m + 1; continue;
      }

      i = m;
    }

    if (i < 0)
    {
      DatabaseError.throwSqlException(35);
      return false;
    }

    return (m_maxCharWidth[i][1] & 0x100) != 0;
  }

  public static int getRatio(int paramInt1, int paramInt2)
  {
    int i = -1;
    int j = 0;
    int k = m_maxCharWidth.length - 1;

    if (paramInt2 == paramInt1)
    {
      return 1;
    }
    int m;
    while (j <= k)
    {
      m = (j + k) / 2;

      if (paramInt1 < m_maxCharWidth[m][0])
      {
        k = m - 1; continue;
      }
      if (paramInt1 > m_maxCharWidth[m][0])
      {
        j = m + 1; continue;
      }

      i = m;
    }

    if (i < 0)
    {
      return 0;
    }

    int n = -1;

    j = 0;
    k = m_maxCharWidth.length - 1;

    while (j <= k)
    {
      m = (j + k) / 2;

      if (paramInt2 < m_maxCharWidth[m][0])
      {
        k = m - 1; continue;
      }
      if (paramInt2 > m_maxCharWidth[m][0])
      {
        j = m + 1; continue;
      }

      n = m;
    }

    if (n < 0)
    {
      return 0;
    }

    int i1 = m_maxCharWidth[i][1] & 0xFF;

    if (i1 == 1)
    {
      return 1;
    }

    if (m_maxCharWidth[n][1] >> 8 == 0)
    {
      return i1;
    }

    int i2 = m_maxCharWidth[n][1] & 0xFF;
    int i3 = i1 / i2;

    if (i1 % i2 != 0)
    {
      i3++;
    }

    return i3;
  }

  private static HashMap createJavaLocale2NLSLocale()
  {
    HashMap localHashMap = new HashMap(159, 1.0F);

    localHashMap.put(new Locale("ar", ""), "ARABIC_SAUDI ARABIA");
    localHashMap.put(new Locale("ar", "AE"), "ARABIC_UNITED ARAB EMIRATES");
    localHashMap.put(new Locale("ar", "BH"), "ARABIC_BAHRAIN");
    localHashMap.put(new Locale("ar", "DJ"), "ARABIC_DJIBOUTI");
    localHashMap.put(new Locale("ar", "DZ"), "ARABIC_ALGERIA");
    localHashMap.put(new Locale("ar", "EG"), "EGYPTIAN_EGYPT");
    localHashMap.put(new Locale("ar", "IQ"), "ARABIC_IRAQ");
    localHashMap.put(new Locale("ar", "JO"), "ARABIC_JORDAN");
    localHashMap.put(new Locale("ar", "KW"), "ARABIC_KUWAIT");
    localHashMap.put(new Locale("ar", "LB"), "ARABIC_LEBANON");
    localHashMap.put(new Locale("ar", "LY"), "ARABIC_LIBYA");
    localHashMap.put(new Locale("ar", "MA"), "ARABIC_MOROCCO");
    localHashMap.put(new Locale("ar", "OM"), "ARABIC_OMAN");
    localHashMap.put(new Locale("ar", "QA"), "ARABIC_QATAR");
    localHashMap.put(new Locale("ar", "SA"), "ARABIC_SAUDI ARABIA");
    localHashMap.put(new Locale("ar", "SD"), "ARABIC_SUDAN");
    localHashMap.put(new Locale("ar", "SO"), "ARABIC_SOMALIA");
    localHashMap.put(new Locale("ar", "SY"), "ARABIC_SYRIA");
    localHashMap.put(new Locale("ar", "TN"), "ARABIC_TUNISIA");
    localHashMap.put(new Locale("ar", "YE"), "ARABIC_YEMEN");
    localHashMap.put(new Locale("as", ""), "ASSAMESE_INDIA");
    localHashMap.put(new Locale("as", "IN"), "ASSAMESE_INDIA");
    localHashMap.put(new Locale("bg", ""), "BULGARIAN_BULGARIA");
    localHashMap.put(new Locale("bg", "BG"), "BULGARIAN_BULGARIA");
    localHashMap.put(new Locale("bn", ""), "BANGLA_BANGLADESH");
    localHashMap.put(new Locale("bn", "BD"), "BANGLA_BANGLADESH");
    localHashMap.put(new Locale("bn", "IN"), "BANGLA_INDIA");
    localHashMap.put(new Locale("ca", ""), "CATALAN_CATALONIA");
    localHashMap.put(new Locale("ca", "ES"), "CATALAN_CATALONIA");
    localHashMap.put(new Locale("cs", ""), "CZECH_CZECH REPUBLIC");
    localHashMap.put(new Locale("cs", "CZ"), "CZECH_CZECH REPUBLIC");
    localHashMap.put(new Locale("da", ""), "DANISH_DENMARK");
    localHashMap.put(new Locale("da", "DK"), "DANISH_DENMARK");
    localHashMap.put(new Locale("de", ""), "GERMAN_GERMANY");
    localHashMap.put(new Locale("de", "AT"), "GERMAN_AUSTRIA");
    localHashMap.put(new Locale("de", "BE"), "GERMAN_BELGIUM");
    localHashMap.put(new Locale("de", "CH"), "GERMAN_SWITZERLAND");
    localHashMap.put(new Locale("de", "DE"), "GERMAN_GERMANY");
    localHashMap.put(new Locale("de", "LU"), "GERMAN_LUXEMBOURG");
    localHashMap.put(new Locale("el", ""), "GREEK_GREECE");
    localHashMap.put(new Locale("el", "CY"), "GREEK_CYPRUS");
    localHashMap.put(new Locale("el", "GR"), "GREEK_GREECE");
    localHashMap.put(new Locale("en", ""), "AMERICAN_AMERICA");
    localHashMap.put(new Locale("en", "AU"), "ENGLISH_AUSTRALIA");
    localHashMap.put(new Locale("en", "CA"), "ENGLISH_CANADA");
    localHashMap.put(new Locale("en", "GB"), "ENGLISH_UNITED KINGDOM");
    localHashMap.put(new Locale("en", "HK"), "ENGLISH_HONG KONG");
    localHashMap.put(new Locale("en", "IE"), "ENGLISH_IRELAND");
    localHashMap.put(new Locale("en", "IN"), "ENGLISH_INDIA");
    localHashMap.put(new Locale("en", "NZ"), "ENGLISH_NEW ZEALAND");
    localHashMap.put(new Locale("en", "SG"), "ENGLISH_SINGAPORE");
    localHashMap.put(new Locale("en", "US"), "AMERICAN_AMERICA");
    localHashMap.put(new Locale("en", "ZA"), "ENGLISH_SOUTH AFRICA");
    localHashMap.put(new Locale("es", ""), "LATIN AMERICAN SPANISH_AMERICA");
    localHashMap.put(new Locale("es", "CL"), "LATIN AMERICAN SPANISH_CHILE");
    localHashMap.put(new Locale("es", "CO"), "LATIN AMERICAN SPANISH_COLOMBIA");
    localHashMap.put(new Locale("es", "CR"), "LATIN AMERICAN SPANISH_COSTA RICA");
    localHashMap.put(new Locale("es", "ES"), "SPANISH_SPAIN");
    localHashMap.put(new Locale("es", "GT"), "LATIN AMERICAN SPANISH_GUATEMALA");
    localHashMap.put(new Locale("es", "MX"), "MEXICAN SPANISH_MEXICO");
    localHashMap.put(new Locale("es", "NI"), "LATIN AMERICAN SPANISH_NICARAGUA");
    localHashMap.put(new Locale("es", "PA"), "LATIN AMERICAN SPANISH_PANAMA");
    localHashMap.put(new Locale("es", "PE"), "LATIN AMERICAN SPANISH_PERU");
    localHashMap.put(new Locale("es", "PR"), "LATIN AMERICAN SPANISH_PUERTO RICO");
    localHashMap.put(new Locale("es", "SV"), "LATIN AMERICAN SPANISH_EL SALVADOR");
    localHashMap.put(new Locale("es", "VE"), "LATIN AMERICAN SPANISH_VENEZUELA");
    localHashMap.put(new Locale("et", ""), "ESTONIAN_ESTONIA");
    localHashMap.put(new Locale("et", "EE"), "ESTONIAN_ESTONIA");
    localHashMap.put(new Locale("fi", ""), "FINNISH_FINLAND");
    localHashMap.put(new Locale("fi", "FI"), "FINNISH_FINLAND");
    localHashMap.put(new Locale("fr", ""), "FRENCH_FRANCE");
    localHashMap.put(new Locale("fr", "BE"), "FRENCH_BELGIUM");
    localHashMap.put(new Locale("fr", "CA"), "CANADIAN FRENCH_CANADA");
    localHashMap.put(new Locale("fr", "CH"), "FRENCH_SWITZERLAND");
    localHashMap.put(new Locale("fr", "DJ"), "FRENCH_DJIBOUTI");
    localHashMap.put(new Locale("fr", "FR"), "FRENCH_FRANCE");
    localHashMap.put(new Locale("fr", "LU"), "FRENCH_LUXEMBOURG");
    localHashMap.put(new Locale("fr", "MR"), "FRENCH_MAURITANIA");
    localHashMap.put(new Locale("gu", ""), "GUJARATI_INDIA");
    localHashMap.put(new Locale("gu", "IN"), "GUJARATI_INDIA");
    localHashMap.put(new Locale("he", ""), "HEBREW_ISRAEL");
    localHashMap.put(new Locale("he", "IL"), "HEBREW_ISRAEL");
    localHashMap.put(new Locale("hi", ""), "HINDI_INDIA");
    localHashMap.put(new Locale("hi", "IN"), "HINDI_INDIA");
    localHashMap.put(new Locale("hr", ""), "CROATIAN_CROATIA");
    localHashMap.put(new Locale("hr", "HR"), "CROATIAN_CROATIA");
    localHashMap.put(new Locale("hu", ""), "HUNGARIAN_HUNGARY");
    localHashMap.put(new Locale("hu", "HU"), "HUNGARIAN_HUNGARY");
    localHashMap.put(new Locale("id", ""), "INDONESIAN_INDONESIA");
    localHashMap.put(new Locale("id", "ID"), "INDONESIAN_INDONESIA");
    localHashMap.put(new Locale("in", ""), "INDONESIAN_INDONESIA");
    localHashMap.put(new Locale("in", "ID"), "INDONESIAN_INDONESIA");
    localHashMap.put(new Locale("is", ""), "ICELANDIC_ICELAND");
    localHashMap.put(new Locale("is", "IS"), "ICELANDIC_ICELAND");
    localHashMap.put(new Locale("it", ""), "ITALIAN_ITALY");
    localHashMap.put(new Locale("it", "CH"), "ITALIAN_SWITZERLAND");
    localHashMap.put(new Locale("it", "IT"), "ITALIAN_ITALY");
    localHashMap.put(new Locale("iw", ""), "HEBREW_ISRAEL");
    localHashMap.put(new Locale("iw", "IL"), "HEBREW_ISRAEL");
    localHashMap.put(new Locale("ja", ""), "JAPANESE_JAPAN");
    localHashMap.put(new Locale("ja", "JP"), "JAPANESE_JAPAN");
    localHashMap.put(new Locale("kn", ""), "KANNADA_INDIA");
    localHashMap.put(new Locale("kn", "IN"), "KANNADA_INDIA");
    localHashMap.put(new Locale("ko", ""), "KOREAN_KOREA");
    localHashMap.put(new Locale("ko", "KR"), "KOREAN_KOREA");
    localHashMap.put(new Locale("lt", ""), "LITHUANIAN_LITHUANIA");
    localHashMap.put(new Locale("lt", "LT"), "LITHUANIAN_LITHUANIA");
    localHashMap.put(new Locale("lv", ""), "LATVIAN_LATVIA");
    localHashMap.put(new Locale("lv", "LV"), "LATVIAN_LATVIA");
    localHashMap.put(new Locale("ml", ""), "MALAYALAM_INDIA");
    localHashMap.put(new Locale("ml", "IN"), "MALAYALAM_INDIA");
    localHashMap.put(new Locale("mr", ""), "MARATHI_INDIA");
    localHashMap.put(new Locale("mr", "IN"), "MARATHI_INDIA");
    localHashMap.put(new Locale("ms", ""), "MALAY_MALAYSIA");
    localHashMap.put(new Locale("ms", "MY"), "MALAY_MALAYSIA");
    localHashMap.put(new Locale("ms", "SG"), "MALAY_SINGAPORE");
    localHashMap.put(new Locale("nl", ""), "DUTCH_THE NETHERLANDS");
    localHashMap.put(new Locale("nl", "BE"), "DUTCH_BELGIUM");
    localHashMap.put(new Locale("nl", "NL"), "DUTCH_THE NETHERLANDS");
    localHashMap.put(new Locale("no", ""), "NORWEGIAN_NORWAY");
    localHashMap.put(new Locale("no", "NO"), "NORWEGIAN_NORWAY");
    localHashMap.put(new Locale("or", ""), "ORIYA_INDIA");
    localHashMap.put(new Locale("or", "IN"), "ORIYA_INDIA");
    localHashMap.put(new Locale("pa", ""), "PUNJABI_INDIA");
    localHashMap.put(new Locale("pa", "IN"), "PUNJABI_INDIA");
    localHashMap.put(new Locale("pl", ""), "POLISH_POLAND");
    localHashMap.put(new Locale("pl", "PL"), "POLISH_POLAND");
    localHashMap.put(new Locale("pt", ""), "PORTUGUESE_PORTUGAL");
    localHashMap.put(new Locale("pt", "BR"), "BRAZILIAN PORTUGUESE_BRAZIL");
    localHashMap.put(new Locale("pt", "PT"), "PORTUGUESE_PORTUGAL");
    localHashMap.put(new Locale("ro", ""), "ROMANIAN_ROMANIA");
    localHashMap.put(new Locale("ro", "RO"), "ROMANIAN_ROMANIA");
    localHashMap.put(new Locale("ru", ""), "RUSSIAN_CIS");
    localHashMap.put(new Locale("ru", "RU"), "RUSSIAN_CIS");
    localHashMap.put(new Locale("sk", ""), "SLOVAK_SLOVAKIA");
    localHashMap.put(new Locale("sk", "SK"), "SLOVAK_SLOVAKIA");
    localHashMap.put(new Locale("sl", ""), "SLOVENIAN_SLOVENIA");
    localHashMap.put(new Locale("sl", "SI"), "SLOVENIAN_SLOVENIA");
    localHashMap.put(new Locale("sv", ""), "SWEDISH_SWEDEN");
    localHashMap.put(new Locale("sv", "FI"), "SWEDISH_FINLAND");
    localHashMap.put(new Locale("sv", "SE"), "SWEDISH_SWEDEN");
    localHashMap.put(new Locale("ta", ""), "TAMIL_INDIA");
    localHashMap.put(new Locale("ta", "IN"), "TAMIL_INDIA");
    localHashMap.put(new Locale("ta", "SG"), "TAMIL_SINGAPORE");
    localHashMap.put(new Locale("te", ""), "TELUGU_INDIA");
    localHashMap.put(new Locale("te", "IN"), "TELUGU_INDIA");
    localHashMap.put(new Locale("th", ""), "THAI_THAILAND");
    localHashMap.put(new Locale("th", "TH"), "THAI_THAILAND");
    localHashMap.put(new Locale("tr", ""), "TURKISH_TURKEY");
    localHashMap.put(new Locale("tr", "TR"), "TURKISH_TURKEY");
    localHashMap.put(new Locale("uk", ""), "UKRAINIAN_UKRAINE");
    localHashMap.put(new Locale("uk", "UA"), "UKRAINIAN_UKRAINE");
    localHashMap.put(new Locale("vi", ""), "VIETNAMESE_VIETNAM");
    localHashMap.put(new Locale("vi", "VN"), "VIETNAMESE_VIETNAM");
    localHashMap.put(new Locale("zh", ""), "SIMPLIFIED CHINESE_CHINA");
    localHashMap.put(new Locale("zh", "CN"), "SIMPLIFIED CHINESE_CHINA");
    localHashMap.put(new Locale("zh", "HK"), "TRADITIONAL CHINESE_HONG KONG");
    localHashMap.put(new Locale("zh", "SG"), "SIMPLIFIED CHINESE_SINGAPORE");
    localHashMap.put(new Locale("zh", "TW"), "TRADITIONAL CHINESE_TAIWAN");

    return localHashMap;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.converter.CharacterSetMetaData
 * JD-Core Version:    0.6.0
 */