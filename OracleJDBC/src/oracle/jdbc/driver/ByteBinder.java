package oracle.jdbc.driver;

class ByteBinder extends VarnumBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] b = bindBytes;
        int offset = byteoffset + 1;
        int val = stmt.parameterInt[rank][bindPosition];
        int len = 0;

        if (val == 0) {
            b[offset] = -128;
            len = 1;
        } else if (val < 0) {
            if (-val < 100) {
                b[offset] = 62;
                b[(offset + 1)] = (byte) (101 + val);
                b[(offset + 2)] = 102;
                len = 3;
            } else {
                b[offset] = 61;
                b[(offset + 1)] = (byte) (101 - -val / 100);
                int x = -val % 100;

                if (x != 0) {
                    b[(offset + 2)] = (byte) (101 - x);
                    b[(offset + 3)] = 102;
                    len = 4;
                } else {
                    b[(offset + 2)] = 102;
                    len = 3;
                }

            }

        } else if (val < 100) {
            b[offset] = -63;
            b[(offset + 1)] = (byte) (val + 1);
            len = 2;
        } else {
            b[offset] = -62;
            b[(offset + 1)] = (byte) (val / 100 + 1);
            int x = val % 100;

            if (x != 0) {
                b[(offset + 2)] = (byte) (x + 1);
                len = 3;
            } else {
                b[(offset + 2)] = 102;
                len = 2;
            }

        }

        b[byteoffset] = (byte) len;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = (short) (len + 1);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ByteBinder JD-Core Version: 0.6.0
 */