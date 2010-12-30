// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: C07

package oracle.net.aso;

// Referenced classes of package oracle.net.aso:
// C02, C03, C00

public class C07 extends C02 implements C00 {

    private static final byte f[] = { 14, -2, 14, -2, 14, -2, 14, -2 };
    private static final byte g[] = { 88, -46, 26, -119, 7, 0, -59, -68 };
    private static final byte h[] = { -2, -2, -2, -2, -2, -2, -2, -2 };
    private static final byte i[] = { 103, 98, -82, -38, 116, -21, -92, -87 };

    public int b() {
        return 8;
    }

    public void a() {
    }

    public void b(byte abyte0[]) {
        byte abyte1[] = new byte[8];
        byte abyte2[] = new byte[8];
        byte abyte3[] = new byte[8];
        int ai[] = new int[32];
        k(abyte1, abyte0, h, 1);
        ai = t(i, (byte) 0);
        d(ai, abyte1, abyte3);
        ai = t(g, (byte) 0);
        d(ai, abyte1, abyte2);
        k(abyte1, abyte3, abyte2, 2);
        k(abyte0, abyte1, f, 1);
    }

    public byte[] c(byte abyte0[], int j) {
        if (A) {
            e(U, (byte) 0);
        }
        return super.c(abyte0, j);
    }

    private void d(int ai[], byte abyte0[], byte abyte1[]) {
        int ai1[] = new int[2];
        r(abyte0, ai1);
        h(ai1, ai);
        j(ai1, abyte1);
    }

    public void a(byte abyte0[], byte abyte1[]) throws C03 {
        super.a(abyte0, abyte1);
    }

    public void c(byte abyte0[], byte abyte1[]) throws C03 {
        super.c(abyte0, abyte1);
    }

    public void e(byte abyte0[], byte byte0) {
        System.arraycopy(abyte0, 0, U, 0, 8);
        b(U);
        super.e(U, byte0);
    }

    public byte[] e(byte abyte0[]) {
        if (A) {
            e(U, (byte) 1);
        }
        return super.e(abyte0);
    }

}
