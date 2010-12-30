package oracle.jdbc.driver;

class SetCHARBinder extends Binder {
    Binder theSetCHARCopyingBinder = OraclePreparedStatementReadOnly.theStaticSetCHARCopyingBinder;

    static void init(Binder x) {
        x.type = 996;
        x.bytelen = 0;
    }

    SetCHARBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theSetCHARCopyingBinder;
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

            bindChars[charoffset] = (char) l;

            if (l > 65532)
                bindIndicators[lenoffset] = -2;
            else {
                bindIndicators[lenoffset] = (short) (l + 2);
            }
            int cpos = charoffset + (l >> 1);

            if (l % 2 == 1) {
                l--;
                bindChars[(cpos + 1)] = (char) (value[l] << 8);
            }
            while (l > 0) {
                l -= 2;
                bindChars[(cpos--)] = (char) (value[l] << 8 | value[(l + 1)] & 0xFF);
            }
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.SetCHARBinder JD-Core Version: 0.6.0
 */