package oracle.sql.converter;

public class CharacterConverterFactoryJDBC extends CharacterConverterFactory {
    public CharacterConverters make(int oracleId) {
        return CharacterConverterJDBC.getInstance(oracleId);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.converter.CharacterConverterFactoryJDBC JD-Core Version: 0.6.0
 */