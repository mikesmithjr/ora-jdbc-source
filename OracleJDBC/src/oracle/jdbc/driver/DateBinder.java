package oracle.jdbc.driver;

import java.sql.Date;

class DateBinder extends DateCommonBinder {
    Binder theDateCopyingBinder = OraclePreparedStatementReadOnly.theStaticDateCopyingBinder;

    static void init(Binder x) {
        x.type = 12;
        x.bytelen = 7;
    }

    DateBinder() {
        init(this);
    }

    Binder copyingBinder() {
        return this.theDateCopyingBinder;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) {
        Date[] dates = stmt.parameterDate[rank];
        Date value = dates[bindPosition];

        if (clearPriorBindValues) {
            dates[bindPosition] = null;
        }
        if (value == null) {
            bindIndicators[indoffset] = -1;
        } else {
            bindIndicators[indoffset] = 0;

            long millis = setOracleCYMD(value.getTime(), bindBytes, byteoffset, stmt);

            bindBytes[(6 + byteoffset)] = 1;
            bindBytes[(5 + byteoffset)] = 1;
            bindBytes[(4 + byteoffset)] = 1;

            bindIndicators[lenoffset] = (short) bytePitch;
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.DateBinder JD-Core Version: 0.6.0
 */