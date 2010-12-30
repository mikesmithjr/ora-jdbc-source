package oracle.jdbc.driver;

class RowidBinder extends Binder {
    Binder theRowidCopyingBinder = OraclePreparedStatementReadOnly.theStaticRowidCopyingBinder;

    static void init(Binder x) {
        x.type = 9;
        x.bytelen = 130;
    }

    RowidBinder() {
        init(this);
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

            int l = value.length;

            System.arraycopy(value, 0, bindBytes, byteoffset + 2, l);

            bindBytes[byteoffset] = (byte) (l >> 8);
            bindBytes[(byteoffset + 1)] = (byte) (l & 0xFF);
            bindIndicators[lenoffset] = (short) (l + 2);
        }
    }

    Binder copyingBinder() {
        return this.theRowidCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RowidBinder JD-Core Version: 0.6.0
 */