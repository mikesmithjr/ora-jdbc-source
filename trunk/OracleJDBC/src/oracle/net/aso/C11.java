// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: C11

package oracle.net.aso;

public class C11 {

    private static final int D = 65;

    private static void a(char ac[], char ac1[], char ac2[], char ac3[], char ac4[], int i1) {
        char ac5[] = new char[134];
        char ac6[] = new char[134];
        char ac7[] = new char[132];
        int j2 = e(ac3, i1);
        int l1 = h(2 * j2);
        int i2 = l1 / 16;
        int k2 = (j2 - 2) / 16;
        int j1 = i2 - k2 - 3;
        if (j1 < 0) {
            j1 = 0;
        }
        b(ac7, 2 * i1 + 2);
        A(ac7, ac2, 2 * i1);
        y(ac6, ac4, ac7, k2, j1, i1 + 2);
        for (int k1 = 0; k1 < i1; k1++) {
            ac[k1] = ac6[k1 + (i2 - k2)];
        }

        c(ac5, ac, ac3, i1);
        f(ac1, ac2, ac5, 0, i1);
        for (; v(ac1, ac3, i1) >= 0; q(ac, i1)) {
            f(ac1, ac1, ac3, 0, i1);
        }

    }

    private static void b(char ac[], int i1) {
        for (int j1 = 0; j1 < i1;) {
            ac[j1++] = '\0';
        }

    }

    private static void c(char ac[], char ac1[], char ac2[], int i1) {
        b(ac, i1);
        int j1 = u(ac2, 0, i1);
        for (int k1 = 0; k1 < i1; k1++) {
            if (j1 < i1 - k1) {
                ac[j1 + k1] = i(ac, k1, ac1[k1], ac2, 0, j1);
            } else {
                i(ac, k1, ac1[k1], ac2, 0, i1 - k1);
            }
        }

    }

    public static void d(char ac[], int i1, byte abyte0[], int j1) {
        if (t(abyte0, j1) / 16 + 1 <= i1)
            ;
        int k1 = j1 - 1;
        int l1 = 0;
        int i2 = i1 >= j1 / 2 ? j1 / 2 : i1;
        i1 -= i2;
        j1 -= 2 * i2;
        while (i2-- > 0) {
            ac[l1] = (char) ((0xff & (char) abyte0[k1]) + ((0xff & (char) abyte0[k1 - 1]) << 8));
            l1++;
            k1 -= 2;
        }
        if (i1 > 0 && j1 % 2 == 1) {
            ac[l1] = (char) (0xff & (char) abyte0[k1]);
            k1--;
            l1++;
            i1--;
            j1--;
        }
        while (i1-- > 0) {
            ac[l1++] = '\0';
        }
    }

    private static int e(char ac[], int i1) {
        char c1 = (char) ((ac[i1 - 1] & 0x8000) <= 0 ? 0 : -1);
        int j1;
        for (j1 = i1 - 1; j1 >= 0 && ac[j1] == c1; j1--) {
        }
        if (j1 == -1) {
            return 1;
        }
        int l1 = 16;
        for (int k1 = 32768; l1 >= 0 && 0 == (k1 & (c1 ^ ac[j1])); k1 >>>= 1) {
            l1--;
        }

        return 16 * j1 + l1;
    }

    private static void f(char ac[], char ac1[], char ac2[], int i1, int j1) {
        int k1 = 1;
        for (int l1 = 0; l1 < j1; l1++) {
            k1 += ac1[l1];
            k1 += ~ac2[l1 + i1] & 0xffff;
            ac[l1] = (char) k1;
            k1 >>>= 16;
        }

    }

    private static void g(char ac[], int i1, int j1) {
        for (int k1 = 0; k1 < j1; k1++) {
            ac[k1] = '\0';
        }

        ac[i1 / 16] = (char) (1 << i1 % 16);
    }

    private static int h(int i1) {
        return 16 * ((i1 + 1 + 15) / 16);
    }

    private static char i(char ac[], int i1, char c1, char ac1[], int j1, int k1) {
        int l1 = 0;
        if (c1 <= 0) {
            return '\0';
        }
        char c2 = c1;
        for (int i2 = 0; i2 < k1; i2++) {
            l1 += c2 * ac1[i2 + j1];
            l1 += ac[i2 + i1];
            ac[i2 + i1] = (char) l1;
            l1 >>>= 16;
        }

        return (char) l1;
    }

    private static void j(char ac[], char ac1[], char ac2[], int i1) {
        b(ac, 2 * i1);
        int j1 = u(ac2, 0, i1);
        for (int k1 = 0; k1 < i1; k1++) {
            ac[j1 + k1] = i(ac, k1, ac1[k1], ac2, 0, j1);
        }

    }

    public static void k(char ac[], char ac1[], char ac2[], char ac3[], int i1) {
        char ac4[] = new char[67];
        boolean aflag[] = new boolean[64];
        char ac5[][] = new char[16][65];
        char ac6[] = new char[65];
        s(ac4, ac3, i1);
        int j1 = e(ac2, i1);
        byte byte0;
        if (j1 < 4) {
            byte0 = 1;
        } else if (j1 < 16) {
            byte0 = 2;
        } else if (j1 < 64) {
            byte0 = 3;
        } else {
            byte0 = 4;
        }
        z(ac5[0], 1, i1);
        A(ac5[1], ac1, i1);
        aflag[0] = true;
        aflag[1] = true;
        for (int k1 = 2; k1 < 64; k1++) {
            aflag[k1] = false;
        }

        int l1 = 0;
        boolean flag = false;
        char c1 = (char) (1 << j1 % 16);
        for (int i2 = j1; i2 >= 0; i2--) {
            if (flag) {
                m(ac6, ac6, ac3, ac4, i1);
            }
            l1 <<= 1;
            if (!aflag[l1]) {
                m(ac5[l1], ac5[l1 / 2], ac3, ac4, i1);
                aflag[l1] = true;
            }
            if ((ac2[i2 / 16] & c1) > 0) {
                l1++;
            }
            if (c1 == '\001') {
                c1 = '\u8000';
            } else {
                c1 = (char) (c1 >>> 1 & 0x7fff);
            }
            if (!aflag[l1]) {
                l(ac5[l1], ac5[l1 - 1], ac1, ac3, ac4, i1);
                aflag[l1] = true;
            }
            if (i2 != 0 && l1 < 1 << byte0 - 1) {
                continue;
            }
            if (flag) {
                l(ac6, ac6, ac5[l1], ac3, ac4, i1);
            } else {
                A(ac6, ac5[l1], i1);
            }
            l1 = 0;
            flag = true;
        }

        A(ac, ac6, i1);
    }

    private static void l(char ac[], char ac1[], char ac2[], char ac3[], char ac4[], int i1) {
        char ac5[] = new char[130];
        j(ac5, ac1, ac2, i1);
        p(ac, ac5, ac3, ac4, i1);
    }

    private static void m(char ac[], char ac1[], char ac2[], char ac3[], int i1) {
        char ac4[] = new char[130];
        x(ac4, ac1, i1);
        p(ac, ac4, ac2, ac3, i1);
    }

    private static void n(char ac[], int i1) {
        boolean flag = true;
        int j1;
        for (j1 = 0; j1 < i1 - 1 && flag; j1++) {
            ac[j1] = (char) (ac[j1] - 1);
            if (ac[j1] != '\uFFFF') {
                flag = false;
            }
        }

        if (flag) {
            ac[j1] = (char) (ac[j1] - 1);
        }
    }

    private static int o(int i1) {
        i1--;
        int j1 = 0;
        for (; i1 > 0; i1 >>>= 1) {
            j1++;
        }

        return j1;
    }

    private static void p(char ac[], char ac1[], char ac2[], char ac3[], int i1) {
        char ac4[] = new char[65];
        a(ac4, ac, ac1, ac2, ac3, i1);
    }

    private static void q(char ac[], int i1) {
        boolean flag = true;
        int j1;
        for (j1 = 0; j1 < i1 - 1 && flag; j1++) {
            ac[j1] = (char) (ac[j1] + 1);
            if (ac[j1] > 0) {
                flag = false;
            }
        }

        if (flag) {
            ac[j1] = (char) (ac[j1] + 1);
        }
    }

    public static void r(byte abyte0[], int i1, char ac[], int j1) {
        int k1 = i1 - 1;
        int l1 = 0;
        int i2 = j1 >= i1 / 2 ? i1 / 2 : j1;
        j1 -= i2;
        i1 -= 2 * i2;
        while (i2-- > 0) {
            abyte0[k1--] = (byte) (0xff & (byte) ac[l1]);
            abyte0[k1--] = (byte) (ac[l1] >>> 8);
            l1++;
        }
        if (j1 > 0 && i1 % 2 == 1) {
            abyte0[k1--] = (byte) (0xff & (byte) ac[l1]);
            l1++;
            j1--;
            i1--;
        }
        while (k1-- > 0) {
            abyte0[k1--] = 0;
        }
    }

    private static void s(char ac[], char ac1[], int i1) {
        char ac2[] = new char[134];
        char ac3[] = new char[136];
        char ac4[] = new char[68];
        int j1 = e(ac1, i1);
        int k1 = h(2 * j1);
        int l1 = k1 / 16;
        int i2 = (j1 - 2) / 16;
        g(ac, k1 - j1, i1 + 2);
        q(ac, i1 + 2);
        b(ac4, i1 + 3);
        A(ac4, ac1, i1);
        for (int j2 = 1 + o((k1 - j1) + 1); j2 > 0; j2--) {
            x(ac2, ac, i1 + 2);
            w(ac3, ac4, ac2, i2, i1 + 3);
            C(ac, ac, ac, i1 + 2);
            f(ac, ac, ac3, l1 - i2, i1 + 2);
        }

        q(ac, i1 + 2);
        do {
            j(ac2, ac, ac4, i1 + 2);
            n(ac2, 2 * (i1 + 2));
            if (e(ac2, 2 * (i1 + 2)) > k1) {
                n(ac, i1 + 2);
            } else {
                return;
            }
        } while (true);
    }

    private static int t(byte abyte0[], int i1) {
        int j1;
        for (j1 = 0; j1 < i1 && abyte0[j1] == 0; j1++) {
        }
        if (j1 == i1) {
            return 0;
        }
        byte byte0 = abyte0[j1++];
        int k1 = 8;
        for (byte byte1 = -128; (byte0 & byte1) == 0; byte1 >>>= 1) {
            k1--;
        }

        return 8 * (i1 - j1) + k1;
    }

    private static int u(char ac[], int i1, int j1) {
        for (int k1 = j1 - 1; k1 >= 0; k1--) {
            if (ac[k1 + i1] > 0) {
                return k1 + 1;
            }
        }

        return 0;
    }

    private static int v(char ac[], char ac1[], int i1) {
        int j1 = B(ac, i1);
        int k1 = B(ac1, i1);
        if (j1 > k1) {
            return 1;
        }
        if (j1 < k1) {
            return -1;
        }
        int l1;
        for (l1 = i1 - 1; l1 >= 0 && ac[l1] == ac1[l1]; l1--) {
        }
        if (l1 == -1) {
            return 0;
        }
        return ac[l1] <= ac1[l1] ? -1 : 1;
    }

    private static void w(char ac[], char ac1[], char ac2[], int i1, int j1) {
        b(ac, 2 * j1);
        int k1 = u(ac2, 0 + i1, j1);
        for (int l1 = 0; l1 < j1; l1++) {
            ac[k1 + l1] = i(ac, l1, ac1[l1], ac2, 0 + i1, k1);
        }

    }

    private static void x(char ac[], char ac1[], int i1) {
        int j1 = 0;
        b(ac, 2 * i1);
        int k1 = u(ac1, 0, i1);
        if (k1 <= 0) {
            return;
        }
        for (int l1 = 0; l1 < k1 - 1; l1++) {
            ac[k1 + l1] = i(ac, 2 * l1 + 1, ac1[l1], ac1, l1 + 1, k1 - l1 - 1);
        }

        C(ac, ac, ac, 2 * i1);
        int i2;
        for (i2 = 0; i2 < k1; i2++) {
            j1 += ac1[i2] * ac1[i2];
            j1 += ac[2 * i2];
            ac[2 * i2] = (char) j1;
            j1 >>>= 16;
            j1 += ac[2 * i2 + 1];
            ac[2 * i2 + 1] = (char) j1;
            j1 >>>= 16;
        }

        ac[2 * i2] = (char) j1;
    }

    private static void y(char ac[], char ac1[], char ac2[], int i1, int j1, int k1) {
        b(ac, 2 * k1);
        int i2 = u(ac2, i1, k1);
        int j2 = j1 < k1 - 1 ? 0 : j1 - (k1 - 1);
        for (int k2 = j2; k2 < k1; k2++) {
            int l1 = j1 < k2 ? 0 : j1 - k2;
            ac[i2 + k2] = i(ac, k2 + l1, ac1[k2], ac2, l1 + i1, i2 < l1 ? 0 : i2 - l1);
        }

    }

    private static void z(char ac[], int i1, int j1) {
        char c1 = (char) ((i1 & 0x8000) <= 0 ? 0 : -1);
        ac[0] = (char) i1;
        for (int k1 = 1; k1 < j1; k1++) {
            ac[k1] = c1;
        }

    }

    private static void A(char ac[], char ac1[], int i1) {
        for (int j1 = 0; j1 < i1; j1++) {
            ac[j1] = ac1[j1];
        }

    }

    private static int B(char ac[], int i1) {
        if ((ac[i1 - 1] & 0x8000) > 0) {
            return -1;
        }
        for (int j1 = i1 - 1; j1 >= 0; j1--) {
            if (ac[j1] > 0) {
                return 1;
            }
        }

        return 0;
    }

    private static void C(char ac[], char ac1[], char ac2[], int i1) {
        int j1 = 0;
        for (int k1 = 0; k1 < i1; k1++) {
            j1 += ac1[k1];
            j1 += ac2[k1];
            ac[k1] = (char) j1;
            j1 >>>= 16;
        }

    }
}
