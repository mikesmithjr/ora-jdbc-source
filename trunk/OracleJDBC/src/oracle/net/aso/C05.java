// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: C05

package oracle.net.aso;

// Referenced classes of package oracle.net.aso:
// C03, C00

public class C05 implements C00 {

    private static final byte k[] = { 56, 48, 40, 32, 24, 16, 8, 0, 57, 49, 41, 33, 25, 17, 9, 1,
            58, 50, 42, 34, 26, 18, 10, 2, 59, 51, 43, 35, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53,
            45, 37, 29, 21, 13, 5, 60, 52, 44, 36, 28, 20, 12, 4, 27, 19, 11, 3 };
    private int l[];
    private byte m[];
    private static final int n[] = { 0x200000, 0x4200002, 0x4000802, 0, 2048, 0x4000802, 0x200802,
            0x4200800, 0x4200802, 0x200000, 0, 0x4000002, 2, 0x4000000, 0x4200002, 2050, 0x4000800,
            0x200802, 0x200002, 0x4000800, 0x4000002, 0x4200000, 0x4200800, 0x200002, 0x4200000,
            2048, 2050, 0x4200802, 0x200800, 2, 0x4000000, 0x200800, 0x4000000, 0x200800, 0x200000,
            0x4000802, 0x4000802, 0x4200002, 0x4200002, 2, 0x200002, 0x4000000, 0x4000800,
            0x200000, 0x4200800, 2050, 0x200802, 0x4200800, 2050, 0x4000002, 0x4200802, 0x4200000,
            0x200800, 0, 2, 0x4200802, 0, 0x200802, 0x4200000, 2048, 0x4000002, 0x4000800, 2048,
            0x200002 };
    private static final byte o[] = { 13, 16, 10, 23, 0, 4, 2, 27, 14, 5, 20, 9, 22, 18, 11, 3, 25,
            7, 15, 6, 26, 19, 12, 1, 40, 51, 30, 36, 46, 54, 29, 39, 50, 44, 32, 47, 43, 48, 38,
            55, 33, 52, 45, 41, 49, 35, 28, 31 };
    private static final short p[] = { 128, 64, 32, 16, 8, 4, 2, 1 };
    private static final byte q[] = { 1, 2, 4, 6, 8, 10, 12, 14, 15, 17, 19, 21, 23, 25, 27, 28 };
    private static final int r[] = { 0x10001040, 4096, 0x40000, 0x10041040, 0x10000000, 0x10001040,
            64, 0x10000000, 0x40040, 0x10040000, 0x10041040, 0x41000, 0x10041000, 0x41040, 4096,
            64, 0x10040000, 0x10000040, 0x10001000, 4160, 0x41000, 0x40040, 0x10040040, 0x10041000,
            4160, 0, 0, 0x10040040, 0x10000040, 0x10001000, 0x41040, 0x40000, 0x41040, 0x40000,
            0x10041000, 4096, 64, 0x10040040, 4096, 0x41040, 0x10001000, 64, 0x10000040,
            0x10040000, 0x10040040, 0x10000000, 0x40000, 0x10001040, 0, 0x10041040, 0x40040,
            0x10000040, 0x10040000, 0x10001000, 0x10001040, 0, 0x10041040, 0x41000, 0x41000, 4160,
            4160, 0x40040, 0x10000000, 0x10041000 };
    protected static final int s = 1;
    private int t[];
    private int u[];
    protected boolean v;
    private static final int w[] = { 0x1010400, 0, 0x10000, 0x1010404, 0x1010004, 0x10404, 4,
            0x10000, 1024, 0x1010400, 0x1010404, 1024, 0x1000404, 0x1010004, 0x1000000, 4, 1028,
            0x1000400, 0x1000400, 0x10400, 0x10400, 0x1010000, 0x1010000, 0x1000404, 0x10004,
            0x1000004, 0x1000004, 0x10004, 0, 1028, 0x10404, 0x1000000, 0x10000, 0x1010404, 4,
            0x1010000, 0x1010400, 0x1000000, 0x1000000, 1024, 0x1010004, 0x10000, 0x10400,
            0x1000004, 1024, 4, 0x1000404, 0x10404, 0x1010404, 0x10004, 0x1010000, 0x1000404,
            0x1000004, 1028, 0x10404, 0x1010400, 1028, 0x1000400, 0x1000400, 0, 0x10004, 0x10400,
            0, 0x1010004 };
    protected static final byte x = 1;
    protected static final int y = 8;
    private static final int z[] = { 0x80108020, 0x80008000, 32768, 0x108020, 0x100000, 32,
            0x80100020, 0x80008020, 0x80000020, 0x80108020, 0x80108000, 0x80000000, 0x80008000,
            0x100000, 32, 0x80100020, 0x108000, 0x100020, 0x80008020, 0, 0x80000000, 32768,
            0x108020, 0x80100000, 0x100020, 0x80000020, 0, 0x108000, 32800, 0x80108000, 0x80100000,
            32800, 0, 0x108020, 0x80100020, 0x100000, 0x80008020, 0x80100000, 0x80108000, 32768,
            0x80100000, 0x80008000, 32, 0x80108020, 0x108020, 32, 32768, 0x80000000, 32800,
            0x80108000, 0x100000, 0x80000020, 0x100020, 0x80008020, 0x80000020, 0x100020, 0x108000,
            0, 0x80008000, 32800, 0x80000000, 0x80100020, 0x80108020, 0x108000 };
    protected byte A[];
    private static final int B[] = { 520, 0x8020200, 0, 0x8020008, 0x8000200, 0, 0x20208,
            0x8000200, 0x20008, 0x8000008, 0x8000008, 0x20000, 0x8020208, 0x20008, 0x8020000, 520,
            0x8000000, 8, 0x8020200, 512, 0x20200, 0x8020000, 0x8020008, 0x20208, 0x8000208,
            0x20200, 0x20000, 0x8000208, 8, 0x8020208, 512, 0x8000000, 0x8020200, 0x8000000,
            0x20008, 520, 0x20000, 0x8020200, 0x8000200, 0, 512, 0x20008, 0x8020208, 0x8000200,
            0x8000008, 512, 0, 0x8020008, 0x8000208, 0x20000, 0x8000000, 0x8020208, 8, 0x20208,
            0x20200, 0x8000008, 0x8020000, 0x8000208, 520, 0x8020000, 0x20208, 8, 0x8020008,
            0x20200 };
    protected byte C[];
    protected static final byte D = 0;
    private static final int E[] = { 0x802001, 8321, 8321, 128, 0x802080, 0x800081, 0x800001, 8193,
            0, 0x802000, 0x802000, 0x802081, 129, 0, 0x800080, 0x800001, 1, 8192, 0x800000,
            0x802001, 128, 0x800000, 8193, 8320, 0x800081, 1, 8320, 0x800080, 8192, 0x802080,
            0x802081, 129, 0x800080, 0x800001, 0x802000, 0x802081, 129, 0, 0, 0x802000, 8320,
            0x800080, 0x800081, 1, 0x802001, 8321, 8321, 128, 0x802081, 129, 1, 8192, 0x800001,
            8193, 0x802080, 0x800081, 8193, 8320, 0x800000, 0x802001, 128, 0x800000, 8192, 0x802080 };
    protected byte F[];
    private int G[];
    private static final int H[] = { 256, 0x2080100, 0x2080000, 0x42000100, 0x80000, 256,
            0x40000000, 0x2080000, 0x40080100, 0x80000, 0x2000100, 0x40080100, 0x42000100,
            0x42080000, 0x80100, 0x40000000, 0x2000000, 0x40080000, 0x40080000, 0, 0x40000100,
            0x42080100, 0x42080100, 0x2000100, 0x42080000, 0x40000100, 0, 0x42000000, 0x2080100,
            0x2000000, 0x42000000, 0x80100, 0x80000, 0x42000100, 256, 0x2000000, 0x40000000,
            0x2080000, 0x42000100, 0x40080100, 0x2000100, 0x40000000, 0x42080000, 0x2080100,
            0x40080100, 256, 0x2000000, 0x42080000, 0x42080100, 0x80100, 0x42000000, 0x42080100,
            0x2080000, 0, 0x40080000, 0x42000000, 0x80100, 0x2000100, 0x40000100, 0x80000, 0,
            0x40080000, 0x2080100, 0x40000100 };
    private int I[];
    private static final int J[] = { 0x20000010, 0x20400000, 16384, 0x20404010, 0x20400000, 16,
            0x20404010, 0x400000, 0x20004000, 0x404010, 0x400000, 0x20000010, 0x400010, 0x20004000,
            0x20000000, 16400, 0, 0x400010, 0x20004010, 16384, 0x404000, 0x20004010, 16,
            0x20400010, 0x20400010, 0, 0x404010, 0x20404000, 16400, 0x404000, 0x20404000,
            0x20000000, 0x20004000, 16, 0x20400010, 0x404000, 0x20404010, 0x400000, 16400,
            0x20000010, 0x400000, 0x20004000, 0x20000000, 16400, 0x20000010, 0x20404010, 0x404000,
            0x20400000, 0x404010, 0x20404000, 0, 0x20400010, 16, 16384, 0x20400000, 0x404010,
            16384, 0x400010, 0x20004010, 0, 0x20404000, 0x20000000, 0x400010, 0x20004010 };
    private static final byte K[] = { 1, 35, 69, 103, -119, -85, -51, -17 };
    private static final int L[] = { 0x800000, 0x400000, 0x200000, 0x100000, 0x80000, 0x40000,
            0x20000, 0x10000, 32768, 16384, 8192, 4096, 2048, 1024, 512, 256, 128, 64, 32, 16, 8,
            4, 2, 1 };
    private int M[];
    protected static final int N = 2;

    protected void a(byte abyte0[], int ai[]) {
        int i1 = 0;
        ai[0] = (abyte0[i1] & 0xff) << 24;
        i1++;
        ai[0] |= (abyte0[i1] & 0xff) << 16;
        i1++;
        ai[0] |= (abyte0[i1] & 0xff) << 8;
        i1++;
        ai[0] |= abyte0[i1] & 0xff;
        i1++;
        ai[1] = (abyte0[i1] & 0xff) << 24;
        i1++;
        ai[1] |= (abyte0[i1] & 0xff) << 16;
        i1++;
        ai[1] |= (abyte0[i1] & 0xff) << 8;
        i1++;
        ai[1] |= abyte0[i1] & 0xff;
    }

    private int[] b(int ai[]) {
        int ai3[] = new int[32];
        int ai1[] = ai3;
        int ai2[] = ai;
        int i1 = 0;
        boolean flag = false;
        int k1 = 0;
        int l1 = 0;
        while (i1 < 16) {
            int j1 = k1++;
            ai1[l1] = (ai2[j1] & 0xfc0000) << 6;
            ai1[l1] |= (ai2[j1] & 0xfc0) << 10;
            ai1[l1] |= (ai[k1] & 0xfc0000) >> 10;
            ai1[l1] |= (ai[k1] & 0xfc0) >> 6;
            l1++;
            ai1[l1] = (ai2[j1] & 0x3f000) << 12;
            ai1[l1] |= (ai2[j1] & 0x3f) << 16;
            ai1[l1] |= (ai[k1] & 0x3f000) >> 4;
            ai1[l1] |= ai[k1] & 0x3f;
            l1++;
            i1++;
            k1++;
        }
        return ai1;
    }

    public void a(byte abyte0[], byte abyte1[]) throws C03 {
        v = true;
        if (abyte0 == null && abyte1 == null) {
            if (A == null) {
                throw new C03(102);
            } else {
                return;
            }
        }
        if (abyte0.length < 24) {
            throw new C03(102);
        } else {
            System.arraycopy(abyte0, 0, A, 0, 8);
            System.arraycopy(abyte0, 8, C, 0, 8);
            System.arraycopy(abyte0, 16, F, 0, 8);
            return;
        }
    }

    public void c() {
    }

    public C05() {
        A = new byte[8];
        C = new byte[8];
        F = new byte[8];
        m = new byte[8];
        v = false;
    }

    protected void d(int ai[], byte abyte0[]) {
        int i1 = 0;
        abyte0[i1] = (byte) (ai[0] >> 24 & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[0] >> 16 & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[0] >> 8 & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[0] & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[1] >> 24 & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[1] >> 16 & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[1] >> 8 & 0xff);
        i1++;
        abyte0[i1] = (byte) (ai[1] & 0xff);
    }

    protected void e(byte abyte0[], int ai[]) {
        int ai1[] = new int[2];
        a(abyte0, ai1);
        i(ai1, ai);
        d(ai1, abyte0);
    }

    public void c(byte abyte0[], byte abyte1[]) throws C03 {
        if (abyte0 == null && abyte1 == null) {
            throw new C03(102);
        }
        if (abyte0.length < 24) {
            throw new C03(102);
        } else {
            System.arraycopy(abyte0, 0, A, 0, 8);
            System.arraycopy(abyte0, 8, C, 0, 8);
            System.arraycopy(abyte0, 16, F, 0, 8);
            v = true;
            return;
        }
    }

    protected void f(byte abyte0[], byte abyte1[], byte abyte2[], int i1) {
        if (i1 == 1) {
            for (int j1 = 0; j1 < 8; j1++) {
                abyte0[j1] = (byte) (abyte1[j1] & abyte2[j1]);
            }

        } else if (i1 == 2) {
            for (int k1 = 0; k1 < 8; k1++) {
                abyte0[k1] = (byte) (abyte1[k1] ^ abyte2[k1]);
            }

        }
    }

    protected int[] g(byte abyte0[], byte byte0) {
        byte abyte1[] = new byte[56];
        byte abyte2[] = new byte[56];
        int ai[] = new int[32];
        for (int j1 = 0; j1 < 56; j1++) {
            byte byte1 = k[j1];
            int l2 = byte1 & 7;
            abyte1[j1] = (byte) ((abyte0[byte1 >> 3] & p[l2]) == 0 ? 0 : 1);
        }

        for (int i1 = 0; i1 < 16; i1++) {
            int i3;
            if (byte0 == 1) {
                i3 = 15 - i1 << 1;
            } else {
                i3 = i1 << 1;
            }
            int j3 = i3 + 1;
            ai[i3] = ai[j3] = 0;
            for (int k1 = 0; k1 < 28; k1++) {
                int j2 = k1 + q[i1];
                if (j2 < 28) {
                    abyte2[k1] = abyte1[j2];
                } else {
                    abyte2[k1] = abyte1[j2 - 28];
                }
            }

            for (int l1 = 28; l1 < 56; l1++) {
                int k2 = l1 + q[i1];
                if (k2 < 56) {
                    abyte2[l1] = abyte1[k2];
                } else {
                    abyte2[l1] = abyte1[k2 - 28];
                }
            }

            for (int i2 = 0; i2 < 24; i2++) {
                if (abyte2[o[i2]] != 0) {
                    ai[i3] |= L[i2];
                }
                if (abyte2[o[i2 + 24]] != 0) {
                    ai[j3] |= L[i2];
                }
            }

        }

        return b(ai);
    }

    public byte[] e(byte abyte0[]) throws C03 {
        int i1 = abyte0.length;
        byte byte0 = abyte0[i1 - 1];
        if (byte0 < 0 || byte0 > 8) {
            return null;
        }
        int j1 = i1 - byte0;
        byte abyte1[] = new byte[i1 - 1];
        i1--;
        if (v) {
            j(A, C, F, (byte) 1);
        }
        for (int k1 = 0; k1 < i1; k1 += 8) {
            byte abyte3[] = new byte[8];
            System.arraycopy(abyte0, k1, abyte3, 0, 8);
            byte abyte4[] = h(abyte3, (byte) 1);
            System.arraycopy(abyte4, 0, abyte1, k1, 8);
        }

        byte abyte2[] = new byte[j1];
        System.arraycopy(abyte1, 0, abyte2, 0, j1);
        return abyte2;
    }

    public int b() {
        return 8;
    }

    private byte[] h(byte abyte0[], byte byte0) {
        byte abyte1[] = new byte[8];
        if (byte0 == 0) {
            f(abyte1, m, abyte0, 2);
            e(abyte1, u);
            e(abyte1, I);
            e(abyte1, l);
            System.arraycopy(abyte1, 0, m, 0, 8);
        } else {
            byte abyte2[] = new byte[8];
            System.arraycopy(abyte0, 0, abyte2, 0, 8);
            e(abyte0, t);
            e(abyte0, G);
            e(abyte0, M);
            f(abyte1, m, abyte0, 2);
            System.arraycopy(abyte2, 0, m, 0, 8);
        }
        return abyte1;
    }

    protected void i(int ai[], int ai1[]) {
        boolean flag = false;
        int j2 = 0;
        int l1 = ai[0];
        int k1 = ai[1];
        int j1 = (l1 >>> 4 ^ k1) & 0xf0f0f0f;
        k1 ^= j1;
        l1 ^= j1 << 4;
        j1 = (l1 >>> 16 ^ k1) & 0xffff;
        k1 ^= j1;
        l1 ^= j1 << 16;
        j1 = (k1 >>> 2 ^ l1) & 0x33333333;
        l1 ^= j1;
        k1 ^= j1 << 2;
        j1 = (k1 >>> 8 ^ l1) & 0xff00ff;
        l1 ^= j1;
        k1 ^= j1 << 8;
        k1 = (k1 << 1 | k1 >>> 31 & 1) & -1;
        j1 = (l1 ^ k1) & 0xaaaaaaaa;
        l1 ^= j1;
        k1 ^= j1;
        l1 = (l1 << 1 | l1 >>> 31 & 1) & -1;
        for (int i2 = 0; i2 < 8; i2++) {
            j1 = k1 << 28 | k1 >>> 4;
            long l2 = 0L;
            l2 = ai1[j2] | 0;
            j1 ^= ai1[j2];
            j2++;
            int i1 = n[j1 & 0x3f];
            i1 |= H[j1 >>> 8 & 0x3f];
            i1 |= B[j1 >>> 16 & 0x3f];
            i1 |= w[j1 >>> 24 & 0x3f];
            j1 = k1 ^ ai1[j2];
            j2++;
            i1 |= r[j1 & 0x3f];
            i1 |= J[j1 >>> 8 & 0x3f];
            i1 |= E[j1 >>> 16 & 0x3f];
            i1 |= z[j1 >>> 24 & 0x3f];
            l1 ^= i1;
            j1 = l1 << 28 | l1 >>> 4;
            j1 ^= ai1[j2];
            j2++;
            i1 = n[j1 & 0x3f];
            i1 |= H[j1 >>> 8 & 0x3f];
            i1 |= B[j1 >>> 16 & 0x3f];
            i1 |= w[j1 >>> 24 & 0x3f];
            j1 = l1 ^ ai1[j2];
            j2++;
            i1 |= r[j1 & 0x3f];
            i1 |= J[j1 >>> 8 & 0x3f];
            i1 |= E[j1 >>> 16 & 0x3f];
            i1 |= z[j1 >>> 24 & 0x3f];
            k1 ^= i1;
        }

        k1 = k1 << 31 | k1 >>> 1;
        j1 = (l1 ^ k1) & 0xaaaaaaaa;
        l1 ^= j1;
        k1 ^= j1;
        l1 = l1 << 31 | l1 >>> 1;
        j1 = (l1 >>> 8 ^ k1) & 0xff00ff;
        k1 ^= j1;
        l1 ^= j1 << 8;
        j1 = (l1 >>> 2 ^ k1) & 0x33333333;
        k1 ^= j1;
        l1 ^= j1 << 2;
        j1 = (k1 >>> 16 ^ l1) & 0xffff;
        l1 ^= j1;
        k1 ^= j1 << 16;
        j1 = (k1 >>> 4 ^ l1) & 0xf0f0f0f;
        l1 ^= j1;
        k1 ^= j1 << 4;
        ai[0] = k1;
        ai[1] = l1;
    }

    public byte[] d(byte abyte0[]) throws C03 {
        int i1 = abyte0.length;
        byte byte0 = (byte) (i1 % 8 != 0 ? 8 - i1 % 8 : 0);
        int j1 = i1 + byte0;
        byte abyte1[] = new byte[j1 + 1];
        if (v) {
            j(A, C, F, (byte) 0);
        }
        for (int k1 = 0; k1 < i1; k1 += 8) {
            byte abyte2[] = new byte[8];
            if (k1 <= i1 - 8) {
                System.arraycopy(abyte0, k1, abyte2, 0, 8);
            } else {
                System.arraycopy(abyte0, k1, abyte2, 0, i1 & 7);
            }
            byte abyte3[] = h(abyte2, (byte) 0);
            System.arraycopy(abyte3, 0, abyte1, k1, 8);
        }

        abyte1[j1] = (byte) (byte0 + 1);
        return abyte1;
    }

    protected void j(byte abyte0[], byte abyte1[], byte abyte2[], byte byte0) {
        System.arraycopy(abyte0, 0, A, 0, 8);
        System.arraycopy(abyte1, 0, C, 0, 8);
        System.arraycopy(abyte2, 0, F, 0, 8);
        u = g(A, byte0);
        I = g(C, ((byte) (byte0 != 0 ? 0 : 1)));
        l = g(F, byte0);
        t = g(A, byte0);
        G = g(C, ((byte) (byte0 != 0 ? 0 : 1)));
        M = g(F, byte0);
        System.arraycopy(K, 0, m, 0, 8);
        v = false;
    }

}
