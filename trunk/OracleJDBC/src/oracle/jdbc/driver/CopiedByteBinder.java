package oracle.jdbc.driver;

class CopiedByteBinder extends Binder {
    byte[] value;
    short len;

    CopiedByteBinder(short type, int bytelen, byte[] val, short l) {
        this.type = type;
        this.bytelen = bytelen;
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

        System.arraycopy(this.value, 0, bindBytes, byteoffset, this.value.length);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.CopiedByteBinder JD-Core Version: 0.6.0
 */