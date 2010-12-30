package oracle.jdbc.driver;

class BooleanBinder extends VarnumBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] b = bindBytes;
        int offset = byteoffset + 1;
        int val = stmt.parameterInt[rank][bindPosition];
        int len = 0;

        if (val != 0) {
            b[offset] = -63;
            b[(offset + 1)] = 2;
            len = 2;
        } else {
            b[offset] = -128;
            len = 1;
        }

        b[byteoffset] = (byte) len;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = (short) (len + 1);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BooleanBinder JD-Core Version: 0.6.0
 */