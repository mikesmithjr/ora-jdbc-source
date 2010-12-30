package oracle.jdbc.driver;

class TSTZNullBinder extends TSTZBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        bindIndicators[indoffset] = -1;
    }

    Binder copyingBinder() {
        return this;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TSTZNullBinder JD-Core Version: 0.6.0
 */