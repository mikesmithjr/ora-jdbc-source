package oracle.net.nl.mesg;

import java.util.ListResourceBundle;

public class NLSR_pt_BR extends ListResourceBundle
{
  static final Object[][] contents = { { "NoFile-04600", "TNS-04600: Arquivo n\u00E3o encontrado: {0}" }, { "FileReadError-04601", "TNS-04601: Erro ao ler o arquivo de par\u00E2metros: {0}, o erro \u00E9: {1}" }, { "SyntaxError-04602", "TNS-04602: Erro de sintaxe inv\u00E1lido: Esperava \"{0}\" antes ou em {1}" }, { "UnexpectedChar-04603", "TNS-04603: Erro de sintaxe inv\u00E1lida: Caractere inesperado \"{0}\" durante o parse {1}" }, { "ParseError-04604", "TNS-04604: Objeto de parse n\u00E3o inicializado" }, { "UnexpectedChar-04605", "TNS-04605: Erro de sintaxe inv\u00E1lido: Caractere ou LITERAL inesperado\"{0}\" antes de ou em {1}" }, { "NoLiterals-04610", "TNS-04610: N\u00E3o h\u00E1 literais; chegou ao fim do par NV" }, { "InvalidChar-04611", "TNS-04611: Caractere de continua\u00E7\u00E3o inv\u00E1lido ap\u00F3s o Coment\u00E1rio" }, { "NullRHS-04612", "TNS-04612: RHS nulo para \"{0}\"" }, { "Internal-04613", "TNS-04613: Erro interno: {0}" }, { "NoNVPair-04614", "TNS-04614: Par NV {0} n\u00E3o encontrado" }, { "InvalidRHS-04615", "TNS-04615: RHS inv\u00E1lido para {0}" } };

  public Object[][] getContents()
  {
    return contents;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar
 * Qualified Name:     oracle.net.nl.mesg.NLSR_pt_BR
 * JD-Core Version:    0.6.0
 */