package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.sql.Datum;

class PlsqlIbtBinder extends Binder {
    Binder thePlsqlIbtCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtCopyingBinder;

    PlsqlIbtBinder() {
        init(this);
    }

    static void init(Binder x) {
        x.type = 998;
    }

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) throws SQLException {
        PlsqlIbtBindInfo bindInfo = stmt.parameterPlsqlIbt[rank][bindPosition];

        if (clearPriorBindValues) {
            stmt.parameterPlsqlIbt[rank][bindPosition] = null;
        }
        int offset = bindInfo.ibtValueIndex;

        switch (bindInfo.element_internal_type) {
        case 9:
            for (int i = 0; i < bindInfo.curLen; i++) {
                int len = 0;
                String s = (String) bindInfo.arrayData[i];

                if (s != null) {
                    len = s.length();

                    if (len > bindInfo.elemMaxLen - 1) {
                        len = bindInfo.elemMaxLen - 1;
                    }
                    s.getChars(0, len, stmt.ibtBindChars, offset + 1);

                    stmt.ibtBindIndicators[(bindInfo.ibtIndicatorIndex + i)] = 0;
                    len <<= 1;
                    stmt.ibtBindChars[offset] = (char) len;

                    stmt.ibtBindIndicators[(bindInfo.ibtLengthIndex + i)] = (len == 0 ? 3
                            : (short) (len + 2));
                } else {
                    stmt.ibtBindIndicators[(bindInfo.ibtIndicatorIndex + i)] = -1;
                }
                offset += bindInfo.elemMaxLen;
            }

            break;
        case 6:
            for (int i = 0; i < bindInfo.curLen; i++) {
                byte[] bytes = null;

                if (bindInfo.arrayData[i] != null) {
                    bytes = ((Datum) bindInfo.arrayData[i]).getBytes();
                }
                if (bytes == null) {
                    stmt.ibtBindIndicators[(bindInfo.ibtIndicatorIndex + i)] = -1;
                } else {
                    stmt.ibtBindIndicators[(bindInfo.ibtIndicatorIndex + i)] = 0;
                    stmt.ibtBindIndicators[(bindInfo.ibtLengthIndex + i)] = (short) (bytes.length + 1);

                    stmt.ibtBindBytes[offset] = (byte) bytes.length;

                    System.arraycopy(bytes, 0, stmt.ibtBindBytes, offset + 1, bytes.length);
                }

                offset += bindInfo.elemMaxLen;
            }

            break;
        default:
            DatabaseError.throwSqlException(97);
        }
    }

    Binder copyingBinder() {
        return this.thePlsqlIbtCopyingBinder;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.PlsqlIbtBinder JD-Core Version: 0.6.0
 */