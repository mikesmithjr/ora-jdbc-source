package oracle.jdbc.driver;

class LongBinder extends VarnumBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] b = bindBytes;
        int offset = byteoffset + 1;
        long val = stmt.parameterLong[rank][bindPosition];
        int len = setLongInternal(b, offset, val);

        b[byteoffset] = (byte) len;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = (short) (len + 1);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.LongBinder JD-Core Version: 0.6.0
 */