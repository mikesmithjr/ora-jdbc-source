package oracle.jdbc.driver;

abstract class TypeCopyingBinder extends Binder {
    Binder copyingBinder() {
        return this;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        if (rankInBuffer == 0) {
            stmt.parameterDatum[0][bindPosition] = stmt.lastBoundTypeBytes[bindPosition];

            stmt.parameterOtype[0][bindPosition] = stmt.lastBoundTypeOtypes[bindPosition];
        } else {
            stmt.parameterDatum[rankInBuffer][bindPosition] = stmt.parameterDatum[(rankInBuffer - 1)][bindPosition];

            stmt.parameterOtype[rankInBuffer][bindPosition] = stmt.parameterOtype[(rankInBuffer - 1)][bindPosition];
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TypeCopyingBinder JD-Core Version: 0.6.0
 */