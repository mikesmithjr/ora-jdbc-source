package oracle.net.nl.mesg;

import java.util.ListResourceBundle;

public class NLSR_fr extends ListResourceBundle
{
  static final Object[][] contents = { { "NoFile-04600", "TNS-04600 : Fichier introuvable : {0}" }, { "FileReadError-04601", "TNS-04601 : Erreur lors de la lecture du fichier de param\u00E8tres {0}. Erreur : {1}" }, { "SyntaxError-04602", "TNS-04602 : Erreur de syntaxe : \"{0}\" est attendu avant ou \u00E0 {1}" }, { "UnexpectedChar-04603", "TNS-04603 : Erreur de syntaxe : caract\u00E8re inattendu \"{0}\" d\u00E9tect\u00E9 lors de l''analyse de {1}" }, { "ParseError-04604", "TNS-04604 : L'objet d'analyse n'est pas initialis\u00E9" }, { "UnexpectedChar-04605", "TNS-04605 : Erreur de syntaxe : le caract\u00E8re ou le litt\u00E9ral \"{0}\" ne doit pas se trouver avant ou \u00E0 {1}" }, { "NoLiterals-04610", "TNS-04610 : Il ne reste aucun litt\u00E9ral, la fin de la paire NV a \u00E9t\u00E9 atteinte" }, { "InvalidChar-04611", "TNS-04611 : Caract\u00E8re de suite non valide apr\u00E8s un commentaire" }, { "NullRHS-04612", "TNS-04612 : RHS null pour \"{0}\"" }, { "Internal-04613", "TNS-04613 : Erreur interne : {0}" }, { "NoNVPair-04614", "TNS-04614 : Paire NV {0} introuvable" }, { "InvalidRHS-04615", "TNS-04615 : RHS non valide pour {0}" } };

  public Object[][] getContents()
  {
    return contents;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar
 * Qualified Name:     oracle.net.nl.mesg.NLSR_fr
 * JD-Core Version:    0.6.0
 */