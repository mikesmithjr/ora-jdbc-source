package oracle.jdbc.driver;

class BinaryFloatBinder extends Binder {
    Binder theBinaryFloatCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatCopyingBinder;

    static void init(Binder x) {
        x.type = 100;
        x.bytelen = 4;
    }

    BinaryFloatBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theBinaryFloatCopyingBinder;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] b = bindBytes;
        int offset = byteoffset;
        float val = stmt.parameterFloat[rank][bindPosition];

        if (val == 0.0D)
            val = 0.0F;
        else if (val != val) {
            val = (0.0F / 0.0F);
        }
        int intBits = Float.floatToIntBits(val);

        int b3 = intBits;

        intBits >>= 8;

        int b2 = intBits;

        intBits >>= 8;

        int b1 = intBits;

        intBits >>= 8;

        int b0 = intBits;

        if ((b0 & 0x80) == 0) {
            b0 |= 128;
        } else {
            b0 ^= -1;
            b1 ^= -1;
            b2 ^= -1;
            b3 ^= -1;
        }

        b[(offset + 3)] = (byte) b3;
        b[(offset + 2)] = (byte) b2;
        b[(offset + 1)] = (byte) b1;
        b[offset] = (byte) b0;

        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = 4;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BinaryFloatBinder JD-Core Version: 0.6.0
 */