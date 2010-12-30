package oracle.jdbc.driver;

abstract class ByteCopyingBinder extends Binder {
    Binder copyingBinder() {
        return this;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        byte[] fromBytes;
        int fromByteOffset;
        int len;
        if (rankInBuffer == 0) {
            fromBytes = stmt.lastBoundBytes;
            fromByteOffset = stmt.lastBoundByteOffsets[bindPosition];
            bindIndicators[indoffset] = stmt.lastBoundInds[bindPosition];
            bindIndicators[lenoffset] = stmt.lastBoundLens[bindPosition];

            if ((fromBytes == bindBytes) && (fromByteOffset == byteoffset)) {
                return;
            }
            len = stmt.lastBoundByteLens[bindPosition];

            if (len > bytePitch)
                len = bytePitch;
        } else {
            fromBytes = bindBytes;
            fromByteOffset = byteoffset - bytePitch;
            bindIndicators[indoffset] = bindIndicators[(indoffset - 1)];
            bindIndicators[lenoffset] = bindIndicators[(lenoffset - 1)];
            len = bytePitch;
        }

        System.arraycopy(fromBytes, fromByteOffset, bindBytes, byteoffset, len);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ByteCopyingBinder JD-Core Version: 0.6.0
 */