// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: OraclePreparedStatement.java

package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.core.lmx.CoreException;

// Referenced classes of package oracle.jdbc.driver:
// VarnumBinder, OraclePreparedStatement

class FloatBinder extends VarnumBinder {

    void bind(OraclePreparedStatement stmt, int bindPosition, int rankInBuffer, int rank,
            byte bindBytes[], char bindChars[], short bindIndicators[], int bytePitch,
            int charPitch, int byteoffset, int charoffset, int lenoffset, int indoffset,
            boolean clearPriorBindValues) throws SQLException {
        byte b[] = bindBytes;
        int offset = byteoffset + 1;
        double val = stmt.parameterDouble[rank][bindPosition];
        int len = 0;
        if (val == 0.0D) {
            b[offset] = -128;
            len = 1;
        } else if (val == (1.0D / 0.0D)) {
            b[offset] = -1;
            b[offset + 1] = 101;
            len = 2;
        } else if (val == (-1.0D / 0.0D)) {
            b[offset] = 0;
            len = 1;
        } else {
            boolean neg = val < 0.0D;
            if (neg) {
                val = -val;
            }
            long bits = Double.doubleToLongBits(val);
            int rawExponent = (int) (bits >> 52 & 2047L);
            int guess = (rawExponent <= 1023 ? 127 : 126)
                    - (int) ((double) (rawExponent - 1023) / 6.6438561897747253D);
            if (guess < 0) {
                SQLException ex = new SQLException(CoreException.getMessage((byte) 3)
                        + " trying to bind " + val);
                throw ex;
            }
            if (guess > 192) {
                SQLException ex = new SQLException(CoreException.getMessage((byte) 2)
                        + " trying to bind " + val);
                throw ex;
            }
            if (val > factorTable[guess]) {
                while (guess > 0 && val > factorTable[--guess])
                    ;
            } else {
                for (; guess < 193 && val <= factorTable[guess + 1]; guess++) {
                }
            }
            if (val == factorTable[guess]) {
                if (guess < 65) {
                    SQLException ex = new SQLException(CoreException.getMessage((byte) 3)
                            + " trying to bind " + val);
                    throw ex;
                }
                if (guess > 192) {
                    SQLException ex = new SQLException(CoreException.getMessage((byte) 2)
                            + " trying to bind " + val);
                    throw ex;
                }
                if (neg) {
                    b[offset] = (byte) (62 - (127 - guess));
                    b[offset + 1] = 100;
                    b[offset + 2] = 102;
                    len = 3;
                } else {
                    b[offset] = (byte) (192 + (128 - guess));
                    b[offset + 1] = 2;
                    len = 2;
                }
            } else {
                if (guess < 64) {
                    SQLException ex = new SQLException(CoreException.getMessage((byte) 3)
                            + " trying to bind " + val);
                    throw ex;
                }
                if (guess > 191) {
                    SQLException ex = new SQLException(CoreException.getMessage((byte) 2)
                            + " trying to bind " + val);
                    throw ex;
                }
                int fBits = Float.floatToIntBits((float) val);
                int fractBits = fBits & 0x7fffff;
                int binExp = fBits >> 23 & 0xff;
                char digits[] = stmt.digits;
                int nSignificantBits;
                if (binExp == 0) {
                    while ((long) (fractBits & 0x800000) == 0L) {
                        fractBits <<= 1;
                        binExp--;
                    }
                    nSignificantBits = 24 + binExp;
                    binExp++;
                } else {
                    fractBits |= 0x800000;
                    nSignificantBits = 24;
                }
                binExp -= 127;
                len = dtoa(b, offset, val, neg, true, digits, binExp, (long) fractBits << 29,
                           nSignificantBits);
            }
        }
        b[byteoffset] = (byte) len;
        bindIndicators[indoffset] = 0;
        bindIndicators[lenoffset] = (short) (len + 1);
    }
}
