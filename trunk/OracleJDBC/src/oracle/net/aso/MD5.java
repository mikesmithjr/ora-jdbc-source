// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: MD5

package oracle.net.aso;

// Referenced classes of package oracle.net.aso:
// C06

public class MD5 implements C06 {
    /* member class not found */
    private class SD5 {
        private int c;
        private byte[] d = new byte[256];
        private byte[] e = new byte[256];
        private final char f = '\u0100';
        private int g;

        public void SD5ed(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt) {
            for (int i = 0; i < paramInt; i++) {
                int j = a();
                paramArrayOfByte1[i] = (byte) ((paramArrayOfByte2[i] ^ j) & 0xFF);
            }
        }

        public SD5() {
        }

        public void SboxInit(byte[] paramArrayOfByte, int paramInt) {
            int i = 0;
            for (i = 0; i < 256; i++) {
                this.d[i] = (byte) i;
                this.e[i] = paramArrayOfByte[(i % paramInt)];
            }
            this.g = (this.c = 0);
            i = 0;
            int j = 0;
            while (i < 256) {
                byte k = this.d[i];
                j = j + k + this.e[i] & 0xFF;
                this.d[i] = this.d[j];
                this.d[j] = k;
                i++;
            }
        }

        private byte a() {
            int i = this.g;
            int j = this.c;
            i = i + 1 & 0xFF;
            j = j + this.d[i] & 0xFF;
            byte k = this.d[i];
            this.d[i] = this.d[j];
            this.d[j] = k;
            int m = this.d[i] + this.d[j] & 0xFF;
            this.g = i;
            this.c = j;
            return this.d[m];
        }
    }

    private byte p[];
    private static final char q[] = { '\200', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',
            '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',
            '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',
            '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0',
            '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0', '\0' };
    private static final int r = 90;
    private SD5 s;
    private static final char t = 255;
    private long u[];
    private byte v[];
    private SD5 w;
    private long x[];
    private static final int y = 5;
    private char z[];
    private SD5 A;
    private boolean B;
    private char C[];
    private static final int D = 16;
    private static final int E = 180;

    private void a(char ac[], byte abyte0[], int i1) {
        for (int j1 = 0; j1 < i1; j1++) {
            ac[j1] = (char) abyte0[j1];
        }

    }

    private long b(long l1, long l2, long l3) {
        return l1 & l3 | l2 & ~l3;
    }

    private long c(long l1, int i1) {
        long l2 = -1L;
        long l3 = l1 << i1;
        long l4 = l2 << i1;
        long l5 = ~l4 & l2;
        long l6 = l1 >>> 32 - i1 & l5;
        return l6 | l3;
    }

    private void d(char ac[], long al[], int i1) {
        int j1 = 0;
        for (int k1 = 0; j1 < i1; k1 += 4) {
            ac[k1] = (char) (int) (al[j1] & 255L);
            ac[k1 + 1] = (char) (int) (al[j1] >> 8 & 255L);
            ac[k1 + 2] = (char) (int) (al[j1] >> 16 & 255L);
            ac[k1 + 3] = (char) (int) (al[j1] >> 24 & 255L);
            j1++;
        }

    }

    private void e(long al[], char ac[], int i1) {
        int j1 = 0;
        for (int k1 = 0; k1 < i1; k1 += 4) {
            al[j1] = (long) ac[k1] & 255L | (long) ac[k1 + 1] << 8 & 65280L
                    | (long) ac[k1 + 2] << 16 & 0xff0000L | (long) ac[k1 + 3] << 24
                    & 0xffffffffff000000L;
            j1++;
        }

    }

    public void initKeyedMD5(boolean flag) {
        B = flag;
        takeSessionKey(v, p);
    }

    private long f(long l1, long l2, long l3, long l4, long l5, int i1, long l6) {
        l1 += k(l2, l3, l4) + l5 + l6;
        l1 = c(l1, i1);
        l1 += l2;
        return l1;
    }

    public void init(byte abyte0[], byte abyte1[]) {
        B = true;
        n();
        if (abyte0 != null || abyte1 != null) {
            v = abyte0;
            p = abyte1;
            initKeyedMD5(true);
        }
    }

    public boolean compare(byte abyte0[], byte abyte1[]) {
        MD5 md5 = new MD5();
        byte abyte2[] = new byte[16];
        byte abyte3[] = new byte[16];
        s.SD5ed(abyte3, abyte2, 16);
        md5.n();
        char ac[] = new char[abyte0.length];
        a(ac, abyte0, abyte0.length);
        md5.MD5Update(ac, abyte0.length);
        char ac1[] = new char[abyte3.length];
        a(ac1, abyte3, abyte3.length);
        md5.MD5Update(ac1, 16);
        md5.MD5Final();
        for (int i1 = 0; i1 < 16; i1++) {
            if (abyte1[i1] != (byte) (md5.C[i1] & 0xff)) {
                return true;
            }
        }

        return false;
    }

    private long g(long l1, long l2, long l3, long l4, long l5, int i1, long l6) {
        l1 += b(l2, l3, l4) + l5 + l6;
        l1 = c(l1, i1);
        l1 += l2;
        return l1;
    }

    private long h(long l1, long l2, long l3) {
        return l1 ^ l2 ^ l3;
    }

    public int size() {
        return 16;
    }

    private long i(long l1, long l2, long l3) {
        return l1 & l2 | ~l1 & l3;
    }

    private void j(long al[], long al1[]) {
        long l1 = al[0];
        long l2 = al[1];
        long l3 = al[2];
        long l4 = al[3];
        l1 = o(l1, l2, l3, l4, al1[0], 7, 0xffffffffd76aa478L);
        l4 = o(l4, l1, l2, l3, al1[1], 12, 0xffffffffe8c7b756L);
        l3 = o(l3, l4, l1, l2, al1[2], 17, 0x242070dbL);
        l2 = o(l2, l3, l4, l1, al1[3], 22, 0xffffffffc1bdceeeL);
        l1 = o(l1, l2, l3, l4, al1[4], 7, 0xfffffffff57c0fafL);
        l4 = o(l4, l1, l2, l3, al1[5], 12, 0x4787c62aL);
        l3 = o(l3, l4, l1, l2, al1[6], 17, 0xffffffffa8304613L);
        l2 = o(l2, l3, l4, l1, al1[7], 22, 0xfffffffffd469501L);
        l1 = o(l1, l2, l3, l4, al1[8], 7, 0x698098d8L);
        l4 = o(l4, l1, l2, l3, al1[9], 12, 0xffffffff8b44f7afL);
        l3 = o(l3, l4, l1, l2, al1[10], 17, -42063L);
        l2 = o(l2, l3, l4, l1, al1[11], 22, 0xffffffff895cd7beL);
        l1 = o(l1, l2, l3, l4, al1[12], 7, 0x6b901122L);
        l4 = o(l4, l1, l2, l3, al1[13], 12, 0xfffffffffd987193L);
        l3 = o(l3, l4, l1, l2, al1[14], 17, 0xffffffffa679438eL);
        l2 = o(l2, l3, l4, l1, al1[15], 22, 0x49b40821L);
        l1 = g(l1, l2, l3, l4, al1[1], 5, 0xfffffffff61e2562L);
        l4 = g(l4, l1, l2, l3, al1[6], 9, 0xffffffffc040b340L);
        l3 = g(l3, l4, l1, l2, al1[11], 14, 0x265e5a51L);
        l2 = g(l2, l3, l4, l1, al1[0], 20, 0xffffffffe9b6c7aaL);
        l1 = g(l1, l2, l3, l4, al1[5], 5, 0xffffffffd62f105dL);
        l4 = g(l4, l1, l2, l3, al1[10], 9, 0x2441453L);
        l3 = g(l3, l4, l1, l2, al1[15], 14, 0xffffffffd8a1e681L);
        l2 = g(l2, l3, l4, l1, al1[4], 20, 0xffffffffe7d3fbc8L);
        l1 = g(l1, l2, l3, l4, al1[9], 5, 0x21e1cde6L);
        l4 = g(l4, l1, l2, l3, al1[14], 9, 0xffffffffc33707d6L);
        l3 = g(l3, l4, l1, l2, al1[3], 14, 0xfffffffff4d50d87L);
        l2 = g(l2, l3, l4, l1, al1[8], 20, 0x455a14edL);
        l1 = g(l1, l2, l3, l4, al1[13], 5, 0xffffffffa9e3e905L);
        l4 = g(l4, l1, l2, l3, al1[2], 9, 0xfffffffffcefa3f8L);
        l3 = g(l3, l4, l1, l2, al1[7], 14, 0x676f02d9L);
        l2 = g(l2, l3, l4, l1, al1[12], 20, 0xffffffff8d2a4c8aL);
        l1 = m(l1, l2, l3, l4, al1[5], 4, 0xfffffffffffa3942L);
        l4 = m(l4, l1, l2, l3, al1[8], 11, 0xffffffff8771f681L);
        l3 = m(l3, l4, l1, l2, al1[11], 16, 0x6d9d6122L);
        l2 = m(l2, l3, l4, l1, al1[14], 23, 0xfffffffffde5380cL);
        l1 = m(l1, l2, l3, l4, al1[1], 4, 0xffffffffa4beea44L);
        l4 = m(l4, l1, l2, l3, al1[4], 11, 0x4bdecfa9L);
        l3 = m(l3, l4, l1, l2, al1[7], 16, 0xfffffffff6bb4b60L);
        l2 = m(l2, l3, l4, l1, al1[10], 23, 0xffffffffbebfbc70L);
        l1 = m(l1, l2, l3, l4, al1[13], 4, 0x289b7ec6L);
        l4 = m(l4, l1, l2, l3, al1[0], 11, 0xffffffffeaa127faL);
        l3 = m(l3, l4, l1, l2, al1[3], 16, 0xffffffffd4ef3085L);
        l2 = m(l2, l3, l4, l1, al1[6], 23, 0x4881d05L);
        l1 = m(l1, l2, l3, l4, al1[9], 4, 0xffffffffd9d4d039L);
        l4 = m(l4, l1, l2, l3, al1[12], 11, 0xffffffffe6db99e5L);
        l3 = m(l3, l4, l1, l2, al1[15], 16, 0x1fa27cf8L);
        l2 = m(l2, l3, l4, l1, al1[2], 23, 0xffffffffc4ac5665L);
        l1 = f(l1, l2, l3, l4, al1[0], 6, 0xfffffffff4292244L);
        l4 = f(l4, l1, l2, l3, al1[7], 10, 0x432aff97L);
        l3 = f(l3, l4, l1, l2, al1[14], 15, 0xffffffffab9423a7L);
        l2 = f(l2, l3, l4, l1, al1[5], 21, 0xfffffffffc93a039L);
        l1 = f(l1, l2, l3, l4, al1[12], 6, 0x655b59c3L);
        l4 = f(l4, l1, l2, l3, al1[3], 10, 0xffffffff8f0ccc92L);
        l3 = f(l3, l4, l1, l2, al1[10], 15, 0xffffffffffeff47dL);
        l2 = f(l2, l3, l4, l1, al1[1], 21, 0xffffffff85845dd1L);
        l1 = f(l1, l2, l3, l4, al1[8], 6, 0x6fa87e4fL);
        l4 = f(l4, l1, l2, l3, al1[15], 10, 0xfffffffffe2ce6e0L);
        l3 = f(l3, l4, l1, l2, al1[6], 15, 0xffffffffa3014314L);
        l2 = f(l2, l3, l4, l1, al1[13], 21, 0x4e0811a1L);
        l1 = f(l1, l2, l3, l4, al1[4], 6, 0xfffffffff7537e82L);
        l4 = f(l4, l1, l2, l3, al1[11], 10, 0xffffffffbd3af235L);
        l3 = f(l3, l4, l1, l2, al1[2], 15, 0x2ad7d2bbL);
        l2 = f(l2, l3, l4, l1, al1[9], 21, 0xffffffffeb86d391L);
        al[0] += l1;
        al[1] += l2;
        al[2] += l3;
        al[3] += l4;
    }

    public void MD5Update(char ac[], int i1) {
        long al[] = new long[16];
        int j1 = (int) (u[0] >>> 3 & 63L);
        if (u[0] + (long) (i1 << 3) < u[0]) {
            u[1]++;
        }
        u[0] += (long) i1 << 3;
        u[1] += (long) i1 >>> 29;
        int k1 = 0;
        do {
            if (i1-- <= 0) {
                break;
            }
            z[j1++] = ac[k1++];
            if (j1 == 64) {
                e(al, z, 64);
                j(x, al);
                j1 = 0;
            }
        } while (true);
    }

    public byte[] compute(byte abyte0[], int i1) {
        if (abyte0.length < i1) {
            return null;
        } else {
            MD5 md5 = new MD5();
            byte abyte1[] = new byte[16];
            byte abyte2[] = new byte[16];
            w.SD5ed(abyte2, abyte1, 16);
            md5.n();
            char ac[] = new char[abyte2.length];
            char ac1[] = new char[i1];
            a(ac1, abyte0, i1);
            a(ac, abyte2, 16);
            md5.MD5Update(ac1, ac1.length);
            md5.MD5Update(ac, ac.length);
            md5.MD5Final();
            l(abyte2, md5.C, md5.C.length);
            return abyte2;
        }
    }

    public void renew() {
        byte abyte0[] = { 0, 0, 0, 0, 0 };
        byte abyte1[] = new byte[6];
        A.SD5ed(abyte1, abyte0, 5);
        abyte1[5] = (byte) (B ? 180 : 90);
        s.SboxInit(abyte1, 6);
        abyte1[5] = (byte) (B ? 'Z' : 180);
        w.SboxInit(abyte1, 6);
    }

    public byte[] digest(byte abyte0[], int i1) {
        if (abyte0.length < i1) {
            return null;
        } else {
            char ac[] = new char[i1];
            a(ac, abyte0, i1);
            MD5Update(ac, ac.length);
            MD5Final();
            byte abyte1[] = new byte[C.length];
            l(abyte1, C, C.length);
            return abyte1;
        }
    }

    private long k(long l1, long l2, long l3) {
        return l2 ^ (l1 | ~l3);
    }

    private void l(byte abyte0[], char ac[], int i1) {
        for (int j1 = 0; j1 < i1; j1++) {
            abyte0[j1] = (byte) (ac[j1] & 0xff);
        }

    }

    private long m(long l1, long l2, long l3, long l4, long l5, int i1, long l6) {
        l1 += h(l2, l3, l4) + l5 + l6;
        l1 = c(l1, i1);
        l1 += l2;
        return l1;
    }

    private void n() {
        u[0] = 0L;
        u[1] = 0L;
        x[0] = 0x67452301L;
        x[1] = 0xffffffffefcdab89L;
        x[2] = 0xffffffff98badcfeL;
        x[3] = 0x10325476L;
    }

    public void MD5Final() {
        long al[] = new long[16];
        al[14] = u[0];
        al[15] = u[1];
        int i1 = (int) (u[0] >>> 3 & 63L);
        int j1 = i1 >= 56 ? 120 - i1 : 56 - i1;
        MD5Update(q, j1);
        e(al, z, 56);
        j(x, al);
        d(C, x, 4);
    }

    private long o(long l1, long l2, long l3, long l4, long l5, int i1, long l6) {
        l1 += i(l2, l3, l4) + l5 + l6;
        l1 = c(l1, i1);
        l1 += l2;
        return l1;
    }

    public int takeSessionKey(byte abyte0[], byte abyte1[]) {
        int i1 = abyte0.length;
        int j1 = abyte1.length;
        int i2 = 6 + j1;
        int j2 = i1 - 5;
        byte abyte2[] = new byte[i2];
        if (j2 < 0) {
            return -1;
        }
        int k1 = 0;
        int l1 = 0;
        for (; k1 < i2; k1++) {
            if (j2 < abyte0.length) {
                abyte2[k1] = abyte0[j2++];
                continue;
            }
            if (j2++ == abyte0.length) {
                abyte2[k1] = -1;
            } else {
                abyte2[k1] = abyte1[l1++];
            }
        }

        A.SboxInit(abyte2, i2);
        renew();
        return 0;
    }

    public MD5() {
        u = new long[2];
        x = new long[4];
        z = new char[64];
        C = new char[16];
        s = new SD5();
        w = new SD5();
        A = new SD5();
    }

}
