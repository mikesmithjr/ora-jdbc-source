package oracle.net.nl.mesg;

import java.util.ListResourceBundle;

public class NLSR_fi extends ListResourceBundle
{
  static final Object[][] contents = { { "NoFile-04600", "TNS-04600: Tiedostoa ei l\u00F6ydy: {0}" }, { "FileReadError-04601", "TNS-04601: Virhe luettaessa parametritiedostoa: {0}, virhe: {1}" }, { "SyntaxError-04602", "TNS-04602: Virheellinen syntaksi: Odotettiin kohdetta \"{0}\" kohteessa {1} tai ennen sit\u00E4" }, { "UnexpectedChar-04603", "TNS-04603: Virheellinen syntaksi: Odottamaton merkki \"{0}\" j\u00E4sennett\u00E4ess\u00E4 kohdetta {1}" }, { "ParseError-04604", "TNS-04604: J\u00E4sennysobjektia ei ole alustettu" }, { "UnexpectedChar-04605", "TNS-04605: Virheellinen syntaksi: Odottamaton merkki tai LITERAL \"{0}\" kohteessa {1} tai ennen sit\u00E4" }, { "NoLiterals-04610", "TNS-04610: Literaaleja ei ole j\u00E4ljell\u00E4, NV-parin loppu on saavutettu" }, { "InvalidChar-04611", "TNS-04611: Virheellinen jatkomerkki kommentin j\u00E4lkeen" }, { "NullRHS-04612", "TNS-04612: Tyhj\u00E4 RHS kohteessa \"{0}\"" }, { "Internal-04613", "TNS-04613: Sis\u00E4inen virhe: {0}" }, { "NoNVPair-04614", "TNS-04614: NV-paria {0} ei l\u00F6ydy" }, { "InvalidRHS-04615", "TNS-04615: Virheellinen RHS kohteessa {0}" } };

  public Object[][] getContents()
  {
    return contents;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar
 * Qualified Name:     oracle.net.nl.mesg.NLSR_fi
 * JD-Core Version:    0.6.0
 */