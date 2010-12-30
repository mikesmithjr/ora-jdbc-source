package oracle.jdbc.driver;

import java.sql.Timestamp;

class TimestampBinder extends DateCommonBinder {
    Binder theTimestampCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimestampCopyingBinder;

    static void init(Binder x) {
        x.type = 180;
        x.bytelen = 11;
    }

    TimestampBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theTimestampCopyingBinder;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        Timestamp[] timestamps = stmt.parameterTimestamp[rank];
        Timestamp value = timestamps[bindPosition];

        if (clearPriorBindValues) {
            timestamps[bindPosition] = null;
        }
        if (value == null) {
            bindIndicators[indoffset] = -1;
        } else {
            bindIndicators[indoffset] = 0;

            setOracleHMS(setOracleCYMD(value.getTime(), bindBytes, byteoffset, stmt), bindBytes,
                         byteoffset);

            int nanos = value.getNanos();

            if (nanos != 0) {
                setOracleNanos(nanos, bindBytes, byteoffset);

                bindIndicators[lenoffset] = (short) bytePitch;
            } else {
                bindIndicators[lenoffset] = 7;
            }
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TimestampBinder JD-Core Version: 0.6.0
 */