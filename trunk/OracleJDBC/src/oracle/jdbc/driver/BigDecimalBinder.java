package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.core.lmx.CoreException;

class BigDecimalBinder extends VarnumBinder {
    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) throws SQLException {
        byte[] b = bindBytes;
        int offset = byteoffset + 1;
        BigDecimal val = stmt.parameterBigDecimal[rank][bindPosition];
        int rlen = 0;

        String sval = val.toString();
        int eIndex;
        if ((eIndex = sval.indexOf("E")) != -1) {
            String s = "";
            int zeros = 0;
            BigDecimal val2 = null;
            String eValue = sval.substring(eIndex + 1);
            String intVal = sval.substring(0, eIndex);
            val2 = new BigDecimal(intVal);
            boolean isNegative = eValue.charAt(0) == '-';
            eValue = eValue.substring(1);
            zeros = Integer.parseInt(eValue);

            int dotIndex = val2.toString().indexOf(".");
            if (dotIndex != -1) {
                double pow = Math.pow(10.0D, dotIndex);
                val2 = val2.multiply(new BigDecimal(pow));
                zeros -= dotIndex;
            } else if (isNegative) {
                zeros--;
            }
            String sval2 = val2.toString();
            sval2 = sval2.substring(0, sval2.length() - (dotIndex + 1));
            s = isNegative ? "0." : sval2;

            for (int i = 0; i < zeros; i++) {
                s = s + "0";
            }
            if (isNegative) {
                s = s + sval2;
            }
            sval = s;
        }
        int len = sval.length();
        int ppos = sval.indexOf('.');
        int signed = sval.charAt(0) == '-' ? 1 : 0;
        int nzpos = signed;

        int nlen = 2;
        int lim1 = len;

        if (ppos == -1)
            ppos = len;
        else if ((len - ppos & 0x1) != 0)
            lim1 = len + 1;
        char c;
        while ((nzpos < len) && (((c = sval.charAt(nzpos)) < '1') || (c > '9'))) {
            nzpos++;
        }
        if (nzpos >= len) {
            b[offset] = -128;
            rlen = 1;
        } else {
            int diglen;
            if (nzpos < ppos)
                diglen = 2 - (ppos - nzpos & 0x1);
            else {
                diglen = 1 + (nzpos - ppos & 0x1);
            }
            int exp = (ppos - nzpos - 1) / 2;

            if (exp > 62) {
                SQLException ex = new SQLException(CoreException.getMessage((byte)3) + " trying to bind "
                        + val);

                throw ex;
            }

            if (exp < -65) {
                SQLException ex = new SQLException(CoreException.getMessage((byte)2) + " trying to bind "
                        + val);

                throw ex;
            }

            int lim2 = nzpos + diglen + 38;

            if (lim2 > len) {
                lim2 = len;
            }
            for (int pos = nzpos + diglen; pos < lim2; pos += 2) {
                if (pos == ppos) {
                    pos--;

                    if (lim2 < len) {
                        lim2++;
                    }
                } else {
                    if ((sval.charAt(pos) == '0')
                            && ((pos + 1 >= len) || (sval.charAt(pos + 1) == '0'))) {
                        continue;
                    }
                    nlen = (pos - nzpos - diglen) / 2 + 3;
                }
            }

            int i = offset + 2;

            int pos = nzpos + diglen;

            if (signed == 0) {
                b[offset] = (byte) (192 + exp + 1);
                int dig = sval.charAt(nzpos) - '0';

                if (diglen == 2) {
                    dig = dig * 10 + (nzpos + 1 < len ? sval.charAt(nzpos + 1) - '0' : 0);
                }

                b[(offset + 1)] = (byte) (dig + 1);

                while (i < offset + nlen) {
                    if (pos == ppos) {
                        pos++;
                    }
                    dig = (sval.charAt(pos) - '0') * 10;

                    if (pos + 1 < len) {
                        dig += sval.charAt(pos + 1) - '0';
                    }
                    b[(i++)] = (byte) (dig + 1);
                    pos += 2;
                }

            }

            b[offset] = (byte) (62 - exp);

            int dig = sval.charAt(nzpos) - '0';

            if (diglen == 2) {
                dig = dig * 10 + (nzpos + 1 < len ? sval.charAt(nzpos + 1) - '0' : 0);
            }

            b[(offset + 1)] = (byte) (101 - dig);

            while (i < offset + nlen) {
                if (pos == ppos) {
                    pos++;
                }
                dig = (sval.charAt(pos) - '0') * 10;

                if (pos + 1 < len) {
                    dig += sval.charAt(pos + 1) - '0';
                }
                b[(i++)] = (byte) (101 - dig);
                pos += 2;
            }

            if (nlen < 21) {
                b[(offset + nlen++)] = 102;
            }

            rlen = nlen;
        }

        b[byteoffset] = (byte) rlen;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = (short) (rlen + 1);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BigDecimalBinder JD-Core Version: 0.6.0
 */