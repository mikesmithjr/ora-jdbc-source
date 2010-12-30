package oracle.jdbc.driver;

class FixedCHARBinder extends Binder {
    Binder theFixedCHARCopyingBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARCopyingBinder;

    static void init(Binder x) {
        x.type = 96;
        x.bytelen = 0;
    }

    FixedCHARBinder() {
        init(this);
    }

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

            value.getChars(0, l, bindChars, charoffset);

            l <<= 1;

            if (l > 65534) {
                l = 65534;
            }
            bindIndicators[lenoffset] = (short) l;
        }
    }

    Binder copyingBinder() {
        return this.theFixedCHARCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.FixedCHARBinder JD-Core Version: 0.6.0
 */