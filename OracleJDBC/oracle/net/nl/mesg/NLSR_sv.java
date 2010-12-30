package oracle.net.nl.mesg;

import java.util.ListResourceBundle;

public class NLSR_sv extends ListResourceBundle
{
  static final Object[][] contents = { { "NoFile-04600", "TNS-04600: Filen finns inte: {0}" }, { "FileReadError-04601", "TNS-04601: Fel vid l\u00E4sning av f\u00F6ljande parameterfil: {0}, felet \u00E4r: {1}" }, { "SyntaxError-04602", "TNS-04602: Ogiltig syntax: \"{0}\" f\u00F6rv\u00E4ntades innan eller vid {1}" }, { "UnexpectedChar-04603", "TNS-04603: Ogiltig syntax: Ov\u00E4ntat tecken, \"{0}\", vid analys av {1}" }, { "ParseError-04604", "TNS-04604: Analysobjektet \u00E4r inte initierat" }, { "UnexpectedChar-04605", "TNS-04605: Ogiltig syntax: Ov\u00E4ntat tecken eller LITERAL, \"{0}\", innan eller vid {1}" }, { "NoLiterals-04610", "TNS-04610: Inga literaler \u00E4r kvar, slutet av NV-paret n\u00E5ddes" }, { "InvalidChar-04611", "TNS-04611: Ogiltigt forts\u00E4ttningstecken efter kommentar" }, { "NullRHS-04612", "TNS-04612: Null-RHS f\u00F6r \"{0}\"" }, { "Internal-04613", "TNS-04613: Internt fel: {0}" }, { "NoNVPair-04614", "TNS-04614: NV-paret {0} hittades inte" }, { "InvalidRHS-04615", "TNS-04615: Ogiltig RHS f\u00F6r {0}" } };

  public Object[][] getContents()
  {
    return contents;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar
 * Qualified Name:     oracle.net.nl.mesg.NLSR_sv
 * JD-Core Version:    0.6.0
 */