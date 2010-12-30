package oracle.jdbc.driver;

class OracleNumberBinder extends DatumBinder {
    Binder theVarnumCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarnumCopyingBinder;

    static void init(Binder x) {
        x.type = 6;
        x.bytelen = 22;
    }

    OracleNumberBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theVarnumCopyingBinder;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[][] datums = stmt.parameterDatum[rank];
        byte[] value = datums[bindPosition];

        if (clearPriorBindValues) {
            datums[bindPosition] = null;
        }
        if (value == null) {
            bindIndicators[indoffset] = -1;
        } else {
            bindIndicators[indoffset] = 0;
            bindBytes[byteoffset] = (byte) value.length;

            System.arraycopy(value, 0, bindBytes, byteoffset + 1, value.length);

            bindIndicators[lenoffset] = (short) (value.length + 1);
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleNumberBinder JD-Core Version: 0.6.0
 */