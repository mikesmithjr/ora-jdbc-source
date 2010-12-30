// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: OraclePreparedStatement.java

package oracle.jdbc.driver;

// Referenced classes of package oracle.jdbc.driver:
// Binder, FDBigInt, OraclePreparedStatementReadOnly

abstract class VarnumBinder extends Binder {

    static final boolean DEBUG = false;
    static final boolean SLOW_CONVERSIONS = true;
    Binder theVarnumCopyingBinder;
    static final int LNXSGNBT = 128;
    static final byte LNXDIGS = 20;
    static final byte LNXEXPBS = 64;
    static final int LNXEXPMX = 127;
    static final double factorTable[] = { 9.9999999999999994E+253D, 1.0000000000000001E+252D,
            9.9999999999999992E+249D, 1E+248D, 1.0000000000000001E+246D, 1.0000000000000001E+244D,
            1.0000000000000001E+242D, 1E+240D, 1E+238D, 1.0000000000000001E+236D, 1E+234D,
            1.0000000000000001E+232D, 1.0000000000000001E+230D, 9.9999999999999992E+227D,
            9.9999999999999996E+225D, 9.9999999999999997E+223D, 1E+222D, 1E+220D,
            1.0000000000000001E+218D, 1E+216D, 9.9999999999999995E+213D, 9.9999999999999991E+211D,
            9.9999999999999993E+209D, 9.9999999999999998E+207D, 1E+206D, 9.9999999999999999E+203D,
            9.999999999999999E+201D, 9.9999999999999997E+199D, 1E+198D, 9.9999999999999995E+195D,
            9.9999999999999994E+193D, 1E+192D, 1.0000000000000001E+190D, 1E+188D,
            9.9999999999999998E+185D, 1E+184D, 1.0000000000000001E+182D, 1E+180D,
            1.0000000000000001E+178D, 1E+176D, 1.0000000000000001E+174D, 1.0000000000000001E+172D,
            1E+170D, 9.9999999999999993E+167D, 9.9999999999999994E+165D, 1E+164D,
            9.9999999999999994E+161D, 1E+160D, 9.9999999999999995E+157D, 9.9999999999999998E+155D,
            1E+154D, 1E+152D, 9.9999999999999998E+149D, 1E+148D, 9.9999999999999993E+145D, 1E+144D,
            1.0000000000000001E+142D, 1.0000000000000001E+140D, 1E+138D, 1.0000000000000001E+136D,
            9.9999999999999992E+133D, 9.9999999999999999E+131D, 1.0000000000000001E+130D,
            1.0000000000000001E+128D, 9.9999999999999992E+125D, 9.9999999999999995E+123D, 1E+122D,
            9.9999999999999998E+119D, 9.9999999999999997E+117D, 1E+116D, 1E+114D,
            9.9999999999999993E+111D, 1E+110D, 1E+108D, 1.0000000000000001E+106D, 1E+104D,
            9.9999999999999998E+101D, 1E+100D, 1E+098D, 1E+096D, 1E+094D, 1E+092D,
            9.9999999999999997E+089D, 9.9999999999999996E+087D, 1E+086D, 1.0000000000000001E+084D,
            9.9999999999999996E+081D, 1E+080D, 1E+078D, 1E+076D, 9.9999999999999995E+073D,
            9.9999999999999994E+071D, 1.0000000000000001E+070D, 9.9999999999999995E+067D,
            9.9999999999999995E+065D, 1E+064D, 1E+062D, 9.9999999999999995E+059D,
            9.9999999999999994E+057D, 1.0000000000000001E+056D, 1.0000000000000001E+054D,
            9.9999999999999999E+051D, 1.0000000000000001E+050D, 1E+048D, 9.9999999999999999E+045D,
            1.0000000000000001E+044D, 1E+042D, 1E+040D, 9.9999999999999998E+037D, 1E+036D,
            9.9999999999999995E+033D, 1.0000000000000001E+032D, 1E+030D, 9.9999999999999996E+027D,
            1E+026D, 9.9999999999999998E+023D, 1E+022D, 1E+020D, 1E+018D, 10000000000000000D,
            100000000000000D, 1000000000000D, 10000000000D, 100000000D, 1000000D, 10000D, 100D,
            1.0D, 0.01D, 0.0001D, 9.9999999999999995E-007D, 1E-008D, 1E-010D,
            9.9999999999999998E-013D, 1E-014D, 9.9999999999999998E-017D, 1.0000000000000001E-018D,
            9.9999999999999995E-021D, 1E-022D, 9.9999999999999992E-025D, 1E-026D,
            9.9999999999999997E-029D, 1.0000000000000001E-030D, 1.0000000000000001E-032D,
            9.9999999999999993E-035D, 9.9999999999999994E-037D, 9.9999999999999996E-039D,
            9.9999999999999993E-041D, 1E-042D, 9.9999999999999995E-045D, 1E-046D,
            9.9999999999999997E-049D, 1E-050D, 1E-052D, 1E-054D, 1E-056D, 1E-058D,
            9.9999999999999997E-061D, 1E-062D, 9.9999999999999997E-065D, 9.9999999999999998E-067D,
            1.0000000000000001E-068D, 1E-070D, 9.9999999999999997E-073D, 9.9999999999999996E-075D,
            9.9999999999999993E-077D, 1E-078D, 9.9999999999999996E-081D, 9.9999999999999996E-083D,
            1E-084D, 1.0000000000000001E-086D, 9.9999999999999993E-089D, 9.9999999999999999E-091D,
            9.9999999999999999E-093D, 9.9999999999999996E-095D, 9.9999999999999991E-097D,
            9.9999999999999994E-099D, 1E-100D, 9.9999999999999993E-103D, 9.9999999999999993E-105D,
            9.9999999999999994E-107D, 1E-108D, 1.0000000000000001E-110D, 9.9999999999999995E-113D,
            1.0000000000000001E-114D, 9.9999999999999999E-117D, 9.9999999999999999E-119D,
            9.9999999999999998E-121D, 1.0000000000000001E-122D, 9.9999999999999993E-125D,
            9.9999999999999995E-127D, 1.0000000000000001E-128D, 1.0000000000000001E-130D,
            9.9999999999999999E-133D, 1E-134D, 1E-136D, 1.0000000000000001E-138D,
            9.9999999999999998E-141D, 1E-142D, 9.9999999999999995E-145D, 1E-146D,
            9.9999999999999994E-149D, 1E-150D, 1.0000000000000001E-152D, 9.9999999999999997E-155D,
            1E-156D, 1.0000000000000001E-158D, 9.9999999999999999E-161D, 9.9999999999999995E-163D,
            9.9999999999999996E-165D, 1E-166D, 1E-168D, 9.9999999999999998E-171D, 1E-172D, 1E-174D,
            1E-176D, 9.9999999999999995E-179D, 1E-180D, 1E-182D, 1.0000000000000001E-184D,
            9.9999999999999991E-187D, 9.9999999999999995E-189D, 1E-190D, 1.0000000000000001E-192D,
            1E-194D, 1E-196D, 9.9999999999999991E-199D, 9.9999999999999998E-201D, 1E-202D, 1E-204D,
            1E-206D, 1.0000000000000001E-208D, 1E-210D, 9.9999999999999995E-213D,
            9.9999999999999991E-215D, 1E-216D, 1E-218D, 9.9999999999999999E-221D, 1E-222D, 1E-224D,
            9.9999999999999992E-227D, 1E-228D, 1E-230D, 1E-232D, 9.9999999999999996E-235D, 1E-236D,
            9.9999999999999999E-239D, 9.9999999999999997E-241D, 9.9999999999999997E-243D,
            9.9999999999999993E-245D, 9.9999999999999996E-247D, 9.9999999999999998E-249D,
            1.0000000000000001E-250D, 9.9999999999999994E-253D, 9.9999999999999991E-255D };
    static final int MANTISSA_SIZE = 53;
    static final int expShift = 52;
    static final long fractHOB = 0x10000000000000L;
    static final long fractMask = 0xfffffffffffffL;
    static final int expBias = 1023;
    static final int maxSmallBinExp = 62;
    static final int minSmallBinExp = -21;
    static final long expOne = 0x3ff0000000000000L;
    static final long highbyte = 0xff00000000000000L;
    static final long highbit = 0x8000000000000000L;
    static final long lowbytes = 0xffffffffffffffL;
    static final int small5pow[] = { 1, 5, 25, 125, 625, 3125, 15625, 0x1312d, 0x5f5e1, 0x1dcd65,
            0x9502f9, 0x2e90edd, 0xe8d4a51, 0x48c27395 };
    static final long long5pow[] = { 1L, 5L, 25L, 125L, 625L, 3125L, 15625L, 0x1312dL, 0x5f5e1L,
            0x1dcd65L, 0x9502f9L, 0x2e90eddL, 0xe8d4a51L, 0x48c27395L, 0x16bcc41e9L, 0x71afd498dL,
            0x2386f26fc1L, 0xb1a2bc2ec5L, 0x3782dace9d9L, 0x1158e460913dL, 0x56bc75e2d631L,
            0x1b1ae4d6e2ef5L, 0x878678326eac9L, 0x2a5a058fc295edL, 0xd3c21bcecceda1L,
            0x422ca8b0a00a425L, 0x14adf4b7320334b9L };
    static final int n5bits[] = { 0, 3, 5, 7, 10, 12, 14, 17, 19, 21, 24, 26, 28, 31, 33, 35, 38,
            40, 42, 45, 47, 49, 52, 54, 56, 59, 61 };
    static FDBigInt b5p[];

    static void init(Binder x) {
        x.type = 6;
        x.bytelen = 22;
    }

    VarnumBinder() {
        theVarnumCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarnumCopyingBinder;
        init(this);
    }

    Binder copyingBinder() {
        return theVarnumCopyingBinder;
    }

    static int setLongInternal(byte b[], int offset, long val) {
        if (val == 0L) {
            b[offset] = -128;
            return 1;
        }
        if (val == 0x8000000000000000L) {
            b[offset] = 53;
            b[offset + 1] = 92;
            b[offset + 2] = 79;
            b[offset + 3] = 68;
            b[offset + 4] = 29;
            b[offset + 5] = 98;
            b[offset + 6] = 33;
            b[offset + 7] = 47;
            b[offset + 8] = 24;
            b[offset + 9] = 43;
            b[offset + 10] = 93;
            b[offset + 11] = 102;
            return 12;
        }
        int exp = 10;
        int len = 0;
        if (val / 0xde0b6b3a7640000L == 0L) {
            exp--;
            if (val / 0x2386f26fc10000L == 0L) {
                exp--;
                if (val / 0x5af3107a4000L == 0L) {
                    exp--;
                    if (val / 0xe8d4a51000L == 0L) {
                        exp--;
                        if (val / 0x2540be400L == 0L) {
                            exp--;
                            if (val / 0x5f5e100L == 0L) {
                                exp--;
                                if (val / 0xf4240L == 0L) {
                                    exp--;
                                    if (val / 10000L == 0L) {
                                        exp--;
                                        if (val / 100L == 0L) {
                                            exp--;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        int idx = offset + exp;
        if (val < 0L) {
            val = -val;
            b[offset] = (byte) (63 - exp);
            do {
                int x = (int) (val % 100L);
                if (len == 0) {
                    if (x != 0) {
                        b[idx + 1] = 102;
                        len = (idx + 2) - offset;
                        b[idx] = (byte) (101 - x);
                    }
                } else {
                    b[idx] = (byte) (101 - x);
                }
                if (--idx == offset) {
                    break;
                }
                val /= 100L;
            } while (true);
        } else {
            b[offset] = (byte) (192 + exp);
            do {
                int x = (int) (val % 100L);
                if (len == 0) {
                    if (x != 0) {
                        len = (idx + 1) - offset;
                        b[idx] = (byte) (x + 1);
                    }
                } else {
                    b[idx] = (byte) (x + 1);
                }
                if (--idx == offset) {
                    break;
                }
                val /= 100L;
            } while (true);
        }
        return len;
    }

    static int countBits(long v) {
        if (v == 0L) {
            return 0;
        }
        for (; (v & 0xff00000000000000L) == 0L; v <<= 8) {
        }
        for (; v > 0L; v <<= 1) {
        }
        int n;
        for (n = 0; (v & 0xffffffffffffffL) != 0L; n += 8) {
            v <<= 8;
        }

        while (v != 0L) {
            v <<= 1;
            n++;
        }
        return n;
    }

    boolean roundup(char digits[], int nDigits) {
        int i = nDigits - 1;
        int q = digits[i];
        if (q == 57) {
            for (; q == 57 && i > 0; q = digits[--i]) {
                digits[i] = '0';
            }

            if (q == 57) {
                digits[0] = '1';
                return true;
            }
        }
        digits[i] = (char) (q + 1);
        return false;
    }

    static synchronized FDBigInt big5pow(int p) {
        if (p < 0) {
            throw new RuntimeException("Assertion botch: negative power of 5");
        }
        if (b5p == null) {
            b5p = new FDBigInt[p + 1];
        } else if (b5p.length <= p) {
            FDBigInt t[] = new FDBigInt[p + 1];
            System.arraycopy(b5p, 0, t, 0, b5p.length);
            b5p = t;
        }
        if (b5p[p] != null) {
            return b5p[p];
        }
        if (p < small5pow.length) {
            return b5p[p] = new FDBigInt(small5pow[p]);
        }
        if (p < long5pow.length) {
            return b5p[p] = new FDBigInt(long5pow[p]);
        }
        int q = p >> 1;
        int r = p - q;
        FDBigInt bigq = b5p[q];
        if (bigq == null) {
            bigq = big5pow(q);
        }
        if (r < small5pow.length) {
            return b5p[p] = bigq.mult(small5pow[r]);
        }
        FDBigInt bigr = b5p[r];
        if (bigr == null) {
            bigr = big5pow(r);
        }
        return b5p[p] = bigq.mult(bigr);
    }

    static FDBigInt multPow52(FDBigInt v, int p5, int p2) {
        if (p5 != 0) {
            if (p5 < small5pow.length) {
                v = v.mult(small5pow[p5]);
            } else {
                v = v.mult(big5pow(p5));
            }
        }
        if (p2 != 0) {
            v.lshiftMe(p2);
        }
        return v;
    }

    static FDBigInt constructPow52(int p5, int p2) {
        FDBigInt v = new FDBigInt(big5pow(p5));
        if (p2 != 0) {
            v.lshiftMe(p2);
        }
        return v;
    }

    int dtoa(byte b[], int offset, double val, boolean neg, boolean forFloat, char digits[],
            int binExp, long fractBits, int nSignificantBits) {
        int decExponent = 0x7fffffff;
        int nDigits = -1;
        int nFractBits = countBits(fractBits);
        int nTinyBits = nFractBits - binExp - 1;
        boolean done = false;
        if (nTinyBits < 0) {
            nTinyBits = 0;
        }
        if (binExp <= 62 && binExp >= -21 && nTinyBits < long5pow.length
                && nFractBits + n5bits[nTinyBits] < 64 && nTinyBits == 0) {
            long halfULP;
            if (binExp > nSignificantBits) {
                halfULP = 1L << binExp - nSignificantBits - 1;
            } else {
                halfULP = 0L;
            }
            if (binExp >= 52) {
                fractBits <<= binExp - 52;
            } else {
                fractBits >>>= 52 - binExp;
            }
            decExponent = 0;
            long lvalue = fractBits;
            long insignificant = halfULP;
            int i;
            for (i = 0; insignificant >= 10L; i++) {
                insignificant /= 10L;
            }

            if (i != 0) {
                long pow10 = long5pow[i] << i;
                long residue = lvalue % pow10;
                lvalue /= pow10;
                decExponent += i;
                if (residue >= pow10 >> 1) {
                    lvalue++;
                }
            }
            int ndigits;
            int digitno;
            if (lvalue <= 0x7fffffffL) {
                int ivalue = (int) lvalue;
                ndigits = 10;
                digitno = ndigits - 1;
                int c = ivalue % 10;
                for (ivalue /= 10; c == 0; ivalue /= 10) {
                    decExponent++;
                    c = ivalue % 10;
                }

                for (; ivalue != 0; ivalue /= 10) {
                    digits[digitno--] = (char) (c + 48);
                    decExponent++;
                    c = ivalue % 10;
                }

                digits[digitno] = (char) (c + 48);
            } else {
                ndigits = 20;
                digitno = ndigits - 1;
                int c = (int) (lvalue % 10L);
                for (lvalue /= 10L; c == 0; lvalue /= 10L) {
                    decExponent++;
                    c = (int) (lvalue % 10L);
                }

                for (; lvalue != 0L; lvalue /= 10L) {
                    digits[digitno--] = (char) (c + 48);
                    decExponent++;
                    c = (int) (lvalue % 10L);
                }

                digits[digitno] = (char) (c + 48);
            }
            ndigits -= digitno;
            if (digitno != 0) {
                System.arraycopy(digits, digitno, digits, 0, ndigits);
            }
            decExponent++;
            nDigits = ndigits;
            done = true;
        }
        if (!done) {
            double d2 = Double
                    .longBitsToDouble(0x3ff0000000000000L | fractBits & 0xffefffffffffffffL);
            int decExp = (int) Math.floor((d2 - 1.5D) * 0.28952965400000003D + 0.176091259D
                    + (double) binExp * 0.30102999566398098D);
            int B5 = Math.max(0, -decExp);
            int B2 = B5 + nTinyBits + binExp;
            int S5 = Math.max(0, decExp);
            int S2 = S5 + nTinyBits;
            int M5 = B5;
            int M2 = B2 - nSignificantBits;
            fractBits >>>= 53 - nFractBits;
            B2 -= nFractBits - 1;
            int common2factor = Math.min(B2, S2);
            B2 -= common2factor;
            S2 -= common2factor;
            M2 -= common2factor;
            if (nFractBits == 1) {
                M2--;
            }
            if (M2 < 0) {
                B2 -= M2;
                S2 -= M2;
                M2 = 0;
            }
            int ndigit = 0;
            int Bbits = nFractBits + B2 + (B5 >= n5bits.length ? B5 * 3 : n5bits[B5]);
            int tenSbits = S2 + 1 + (S5 + 1 >= n5bits.length ? (S5 + 1) * 3 : n5bits[S5 + 1]);
            boolean low;
            boolean high;
            long lowDigitDifference;
            if (Bbits < 64 && tenSbits < 64) {
                if (Bbits < 32 && tenSbits < 32) {
                    int bi = (int) fractBits * small5pow[B5] << B2;
                    int s = small5pow[S5] << S2;
                    int m = small5pow[M5] << M2;
                    int tens = s * 10;
                    ndigit = 0;
                    int q = bi / s;
                    bi = 10 * (bi % s);
                    m *= 10;
                    low = bi < m;
                    high = bi + m > tens;
                    if (q == 0 && !high) {
                        decExp--;
                    } else {
                        digits[ndigit++] = (char) (48 + q);
                    }
                    if (decExp <= -3 || decExp >= 8) {
                        high = low = false;
                    }
                    while (!low && !high) {
                        q = bi / s;
                        bi = 10 * (bi % s);
                        m *= 10;
                        if ((long) m > 0L) {
                            low = bi < m;
                            high = bi + m > tens;
                        } else {
                            low = true;
                            high = true;
                        }
                        digits[ndigit++] = (char) (48 + q);
                    }
                    lowDigitDifference = (bi << 1) - tens;
                } else {
                    long bl = fractBits * long5pow[B5] << B2;
                    long s = long5pow[S5] << S2;
                    long m = long5pow[M5] << M2;
                    long tens = s * 10L;
                    ndigit = 0;
                    int q = (int) (bl / s);
                    bl = 10L * (bl % s);
                    m *= 10L;
                    low = bl < m;
                    high = bl + m > tens;
                    if (q == 0 && !high) {
                        decExp--;
                    } else {
                        digits[ndigit++] = (char) (48 + q);
                    }
                    if (decExp <= -3 || decExp >= 8) {
                        high = low = false;
                    }
                    while (!low && !high) {
                        q = (int) (bl / s);
                        bl = 10L * (bl % s);
                        m *= 10L;
                        if (m > 0L) {
                            low = bl < m;
                            high = bl + m > tens;
                        } else {
                            low = true;
                            high = true;
                        }
                        digits[ndigit++] = (char) (48 + q);
                    }
                    lowDigitDifference = (bl << 1) - tens;
                }
            } else {
                FDBigInt Bval = multPow52(new FDBigInt(fractBits), B5, B2);
                FDBigInt Sval = constructPow52(S5, S2);
                FDBigInt Mval = constructPow52(M5, M2);
                int shiftBias;
                Bval.lshiftMe(shiftBias = Sval.normalizeMe());
                Mval.lshiftMe(shiftBias);
                FDBigInt tenSval = Sval.mult(10);
                ndigit = 0;
                int q = Bval.quoRemIteration(Sval);
                Mval = Mval.mult(10);
                low = Bval.cmp(Mval) < 0;
                high = Bval.add(Mval).cmp(tenSval) > 0;
                if (q == 0 && !high) {
                    decExp--;
                } else {
                    digits[ndigit++] = (char) (48 + q);
                }
                if (decExp <= -3 || decExp >= 8) {
                    high = low = false;
                }
                while (!low && !high) {
                    q = Bval.quoRemIteration(Sval);
                    Mval = Mval.mult(10);
                    low = Bval.cmp(Mval) < 0;
                    high = Bval.add(Mval).cmp(tenSval) > 0;
                    digits[ndigit++] = (char) (48 + q);
                }
                if (high && low) {
                    Bval.lshiftMe(1);
                    lowDigitDifference = Bval.cmp(tenSval);
                } else {
                    lowDigitDifference = 0L;
                }
            }
            decExponent = decExp + 1;
            nDigits = ndigit;
            if (high) {
                if (low) {
                    if (lowDigitDifference == 0L) {
                        if ((digits[nDigits - 1] & '\001') != 0 && roundup(digits, nDigits)) {
                            decExponent++;
                        }
                    } else if (lowDigitDifference > 0L && roundup(digits, nDigits)) {
                        decExponent++;
                    }
                } else if (roundup(digits, nDigits)) {
                    decExponent++;
                }
            }
        }
        for (; nDigits - decExponent > 0 && digits[nDigits - 1] == '0'; nDigits--) {
        }
        boolean oddExp = decExponent % 2 != 0;
        int exp100;
        if (oddExp) {
            if (nDigits % 2 == 0) {
                digits[nDigits++] = '0';
            }
            exp100 = (decExponent - 1) / 2;
        } else {
            if (nDigits % 2 == 1) {
                digits[nDigits++] = '0';
            }
            exp100 = (decExponent - 2) / 2;
        }
        int expola = 117 - exp100;
        int digidx = 0;
        int len = 1;
        if (neg) {
            b[offset] = (byte) (62 - exp100);
            if (oddExp) {
                b[offset + len] = (byte) (101 - (digits[digidx++] - 48));
                len++;
            }
            while (digidx < nDigits) {
                b[offset + len] = (byte) (101 - ((digits[digidx] - 48) * 10 + (digits[digidx + 1] - 48)));
                digidx += 2;
                len++;
            }
            b[offset + len++] = 102;
        } else {
            b[offset] = (byte) (192 + (exp100 + 1));
            if (oddExp) {
                b[offset + len] = (byte) ((digits[digidx++] - 48) + 1);
                len++;
            }
            while (digidx < nDigits) {
                b[offset + len] = (byte) ((digits[digidx] - 48) * 10 + (digits[digidx + 1] - 48) + 1);
                digidx += 2;
                len++;
            }
        }
        return len;
    }

}
