package oracle.sql;

class CharacterSetFactoryThin extends CharacterSetFactory {
    public CharacterSet make(int oracleId) {
        if (oracleId == -1) {
            oracleId = 31;
        }

        if (oracleId == 2000) {
            return new CharacterSetAL16UTF16(oracleId);
        }
        if ((oracleId == 870) || (oracleId == 871)) {
            return new CharacterSetUTF(oracleId);
        }
        if (oracleId == 873) {
            return new CharacterSetAL32UTF8(oracleId);
        }
        if (oracleId == 872) {
            return new CharacterSetUTFE(oracleId);
        }
        if (oracleId == 2002) {
            return new CharacterSetAL16UTF16LE(oracleId);
        }

        CharacterSet charSet = CharacterSetWithConverter.getInstance(oracleId);

        if (charSet != null) {
            return charSet;
        }

        return new CharacterSetUnknown(oracleId);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.CharacterSetFactoryThin JD-Core Version: 0.6.0
 */