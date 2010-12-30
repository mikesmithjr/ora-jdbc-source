package oracle.sql.converter;

public class CharacterConverterFactoryJDBC extends CharacterConverterFactory
{
  public CharacterConverters make(int paramInt)
  {
    return CharacterConverterJDBC.getInstance(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.converter.CharacterConverterFactoryJDBC
 * JD-Core Version:    0.6.0
 */