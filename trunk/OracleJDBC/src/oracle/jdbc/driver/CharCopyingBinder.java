package oracle.jdbc.driver;

abstract class CharCopyingBinder extends Binder {
    Binder copyingBinder() {
        return this;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        char[] fromChars;
        int fromCharOffset;
        int len;
        if (rankInBuffer == 0) {
            fromChars = stmt.lastBoundChars;
            fromCharOffset = stmt.lastBoundCharOffsets[bindPosition];
            bindIndicators[indoffset] = stmt.lastBoundInds[bindPosition];
            bindIndicators[lenoffset] = stmt.lastBoundLens[bindPosition];

            if ((fromChars == bindChars) && (fromCharOffset == charoffset)) {
                return;
            }
            len = stmt.lastBoundCharLens[bindPosition];

            if (len > charPitch)
                len = charPitch;
        } else {
            fromChars = bindChars;
            fromCharOffset = charoffset - charPitch;
            bindIndicators[indoffset] = bindIndicators[(indoffset - 1)];
            bindIndicators[lenoffset] = bindIndicators[(lenoffset - 1)];
            len = charPitch;
        }

        System.arraycopy(fromChars, fromCharOffset, bindChars, charoffset, len);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.CharCopyingBinder JD-Core Version: 0.6.0
 */