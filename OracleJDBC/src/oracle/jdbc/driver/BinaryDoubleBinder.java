package oracle.jdbc.driver;

class BinaryDoubleBinder extends Binder {
    Binder theBinaryDoubleCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleCopyingBinder;

    static void init(Binder x) {
        x.type = 101;
        x.bytelen = 8;
    }

    BinaryDoubleBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theBinaryDoubleCopyingBinder;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] b = bindBytes;
        int offset = byteoffset;
        double val = stmt.parameterDouble[rank][bindPosition];

        if (val == 0.0D)
            val = 0.0D;
        else if (val != val) {
            val = (0.0D / 0.0D);
        }
        long longBits = Double.doubleToLongBits(val);

        int lowInt = (int) longBits;
        int highInt = (int) (longBits >> 32);

        int b7 = lowInt;

        lowInt >>= 8;

        int b6 = lowInt;

        lowInt >>= 8;

        int b5 = lowInt;

        lowInt >>= 8;

        int b4 = lowInt;

        int b3 = highInt;

        highInt >>= 8;

        int b2 = highInt;

        highInt >>= 8;

        int b1 = highInt;

        highInt >>= 8;

        int b0 = highInt;

        if ((b0 & 0x80) == 0) {
            b0 |= 128;
        } else {
            b0 ^= -1;
            b1 ^= -1;
            b2 ^= -1;
            b3 ^= -1;
            b4 ^= -1;
            b5 ^= -1;
            b6 ^= -1;
            b7 ^= -1;
        }

        b[(offset + 7)] = (byte) b7;
        b[(offset + 6)] = (byte) b6;
        b[(offset + 5)] = (byte) b5;
        b[(offset + 4)] = (byte) b4;
        b[(offset + 3)] = (byte) b3;
        b[(offset + 2)] = (byte) b2;
        b[(offset + 1)] = (byte) b1;
        b[offset] = (byte) b0;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = 8;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BinaryDoubleBinder JD-Core Version: 0.6.0
 */