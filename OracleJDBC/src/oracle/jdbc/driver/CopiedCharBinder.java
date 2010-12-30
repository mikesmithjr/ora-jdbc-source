package oracle.jdbc.driver;

class CopiedCharBinder extends Binder {
    char[] value;
    short len;

    CopiedCharBinder(short type, char[] val, short l) {
        this.type = type;
        this.bytelen = 0;
        this.value = val;
        this.len = l;
    }

    Binder copyingBinder() {
        return this;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = this.len;

        System.arraycopy(this.value, 0, bindChars, charoffset, this.value.length);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.CopiedCharBinder JD-Core Version: 0.6.0
 */