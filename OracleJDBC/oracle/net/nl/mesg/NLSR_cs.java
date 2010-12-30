package oracle.net.nl.mesg;

import java.util.ListResourceBundle;

public class NLSR_cs extends ListResourceBundle
{
  static final Object[][] contents = { { "NoFile-04600", "TNS-04600: Soubor nebyl nalezen: {0}" }, { "FileReadError-04601", "TNS-04601: Chyba p\u0159i \u010Dten\u00ED souboru s parametry: {0}, chyba: {1}" }, { "SyntaxError-04602", "TNS-04602: Chyba neplatn\u00E9 syntaxe: Byo o\u010Dek\u00E1v\u00E1no \"{0}\" p\u0159ed nebo na {1}" }, { "UnexpectedChar-04603", "TNS-04603: Chyba neplatn\u00E9 syntaxe: Neo\u010Dek\u00E1van\u00FD znak \"{0}\" p\u0159i syntaktick\u00E9 anal\u00FDze {1}" }, { "ParseError-04604", "TNS-04604: Objekt syntaktick\u00E9 anal\u00FDzy nebyl inicializov\u00E1n" }, { "UnexpectedChar-04605", "TNS-04605: Chyba neplatn\u00E9 syntaxe: Neo\u010Dek\u00E1van\u00FD znak nebo LITERAL \"{0}\" p\u0159ed nebo na {1}" }, { "NoLiterals-04610", "TNS-04610: Nezbyly \u017E\u00E1dn\u00E9 liter\u00E1ly, bylo dosa\u017Eeno konce p\u00E1ru NV" }, { "InvalidChar-04611", "TNS-04611: Neplatn\u00FD pokra\u010Dovac\u00ED znak za koment\u00E1\u0159em" }, { "NullRHS-04612", "TNS-04612: RHS s hodnotou null pro \"{0}\"" }, { "Internal-04613", "TNS-04613: Vnit\u0159n\u00ED chyba: {0}" }, { "NoNVPair-04614", "TNS-04614: P\u00E1r NV {0} nebyl nalezen" }, { "InvalidRHS-04615", "TNS-04615: Neplatn\u00E9 RHS pro {0}" } };

  public Object[][] getContents()
  {
    return contents;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar
 * Qualified Name:     oracle.net.nl.mesg.NLSR_cs
 * JD-Core Version:    0.6.0
 */