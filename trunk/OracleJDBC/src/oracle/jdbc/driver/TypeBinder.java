package oracle.jdbc.driver;

abstract class TypeBinder extends Binder {
    public static final int BYTELEN = 24;

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[][] datums = stmt.parameterDatum[rank];
        byte[] value = datums[bindPosition];

        if (value == null)
            bindIndicators[indoffset] = -1;
        else
            bindIndicators[indoffset] = 0;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TypeBinder JD-Core Version: 0.6.0
 */