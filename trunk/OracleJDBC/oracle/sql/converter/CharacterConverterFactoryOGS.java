package oracle.sql.converter;

import oracle.i18n.text.converter.CharacterConverterOGS;

public class CharacterConverterFactoryOGS extends CharacterConverterFactory
{
  public CharacterConverters make(int paramInt)
  {
    return CharacterConverterOGS.getInstance(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.converter.CharacterConverterFactoryOGS
 * JD-Core Version:    0.6.0
 */