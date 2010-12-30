package oracle.jdbc.driver;

import java.sql.Time;

class TimeBinder extends DateCommonBinder {
    Binder theTimeCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimeCopyingBinder;

    static void init(Binder x) {
        x.type = 12;
        x.bytelen = 7;
    }

    TimeBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theTimeCopyingBinder;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        Time[] times = stmt.parameterTime[rank];
        Time value = times[bindPosition];

        if (clearPriorBindValues) {
            times[bindPosition] = null;
        }
        if (value == null) {
            bindIndicators[indoffset] = -1;
        } else {
            bindIndicators[indoffset] = 0;

            setOracleHMS(setOracleCYMD(value.getTime(), bindBytes, byteoffset, stmt), bindBytes,
                         byteoffset);

            bindIndicators[lenoffset] = (short) bytePitch;
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TimeBinder JD-Core Version: 0.6.0
 */