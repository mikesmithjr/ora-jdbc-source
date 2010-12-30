package oracle.jdbc.driver;

class LongStreamBinder extends Binder {
    LongStreamBinder() {
        this.type = 8;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
    }

    Binder copyingBinder() {
        return OraclePreparedStatementReadOnly.theStaticVarcharNullBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.LongStreamBinder JD-Core Version: 0.6.0
 */