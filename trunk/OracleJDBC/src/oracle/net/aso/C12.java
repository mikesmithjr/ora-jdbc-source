// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: C12

package oracle.net.aso;

// Referenced classes of package oracle.net.aso:
// C03, C01, C00

public class C12 implements C00 {

    private int d;
    private static final int e = 170;
    private boolean f;
    private static final byte g = 123;
    private C01 h;
    private C01 i;
    private static final int j = 85;
    private C01 k;

    public int b() {
        return 1;
    }

    private void a() {
        byte abyte0[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 32, 32, 32, 32, 32, 32, 32,
                32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32 };
        int l = d / 8;
        byte abyte1[] = k.a(abyte0, l);
        if (f) {
            abyte1[l - 1] ^= 0xaa;
        }
        i.b(abyte1, l);
        abyte1[l - 1] ^= 0xaa;
        h.b(abyte1, l);
    }

    public C12(boolean flag, int l) throws C03 {
        f = true;
        d = 40;
        switch (l) {
        case 40: // '('
        case 56: // '8'
        case 128:
        case 256:
            d = l;
            break;

        default:
            throw new C03(100);
        }
        f = flag;
    }

    public byte[] b(byte abyte0[], int l) throws C03 {
        if (l > abyte0.length) {
            throw new C03(104);
        }
        byte abyte1[] = h.a(abyte0, l);
        if (f) {
            byte abyte2[] = new byte[l + 1];
            System.arraycopy(abyte1, 0, abyte2, 0, l);
            abyte2[l] = 0;
            return abyte2;
        } else {
            return abyte1;
        }
    }

    public void a(byte abyte0[], byte abyte1[]) throws C03 {
        if (abyte0 == null && abyte1 == null) {
            a();
            return;
        }
        int l = d / 8;
        if (abyte0.length < l) {
            throw new C03(102);
        }
        int i1 = 0;
        if (abyte1 != null) {
            i1 = abyte1.length;
        }
        byte abyte2[] = new byte[l + 1 + i1];
        System.arraycopy(abyte0, abyte0.length - l, abyte2, 0, l);
        abyte2[l] = 123;
        if (abyte1 != null) {
            System.arraycopy(abyte1, 0, abyte2, l + 1, abyte1.length);
        }
        k.b(abyte2, abyte2.length);
        a();
    }

    public void c(byte abyte0[], byte abyte1[]) throws C03 {
        k = new C01(this);
        h = new C01(this);
        i = new C01(this);
        a(abyte0, abyte1);
    }

    public C12() {
        f = true;
        d = 40;
    }

    public byte[] d(byte abyte0[]) {
        byte abyte1[] = h.a(abyte0, abyte0.length);
        if (f) {
            byte abyte2[] = new byte[abyte0.length + 1];
            System.arraycopy(abyte1, 0, abyte2, 0, abyte0.length);
            abyte2[abyte0.length] = 0;
            return abyte2;
        } else {
            return abyte1;
        }
    }

    public byte[] c(byte abyte0[], int l) throws C03 {
        if (l > abyte0.length) {
            throw new C03(104);
        }
        if (f) {
            byte abyte1[] = i.a(abyte0, l - 1);
            byte abyte3[] = new byte[l - 1];
            System.arraycopy(abyte1, 0, abyte3, 0, l - 1);
            return abyte3;
        } else {
            byte abyte2[] = i.a(abyte0, l);
            return abyte2;
        }
    }

    public C12(int l, byte abyte0[], byte abyte1[]) throws C03 {
        f = true;
        d = 40;
        switch (l) {
        case 40: // '('
        case 56: // '8'
        case 128:
        case 256:
            d = l;
            break;

        default:
            throw new C03(100);
        }
        c(abyte0, abyte1);
    }

    public byte[] e(byte abyte0[]) {
        if (f) {
            byte abyte1[] = i.a(abyte0, abyte0.length - 1);
            byte abyte3[] = new byte[abyte0.length - 1];
            System.arraycopy(abyte1, 0, abyte3, 0, abyte0.length - 1);
            return abyte3;
        } else {
            byte abyte2[] = i.a(abyte0, abyte0.length);
            return abyte2;
        }
    }
}
