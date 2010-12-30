package oracle.jdbc.driver;

class StringBinder extends VarcharBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        String[] strs = stmt.parameterString[rank];
        String value = strs[bindPosition];

        if (clearPriorBindValues) {
            strs[bindPosition] = null;
        }
        if (value == null) {
            bindIndicators[indoffset] = -1;
        } else {
            bindIndicators[indoffset] = 0;

            int l = value.length();

            value.getChars(0, l, bindChars, charoffset + 1);

            l <<= 1;
            bindChars[charoffset] = (char) l;

            if (l > 65532)
                bindIndicators[lenoffset] = -2;
            else
                bindIndicators[lenoffset] = (short) (l + 2);
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.StringBinder JD-Core Version: 0.6.0
 */