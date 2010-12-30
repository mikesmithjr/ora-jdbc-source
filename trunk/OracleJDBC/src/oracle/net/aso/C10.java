// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc 
// Source File Name:   C10

package oracle.net.aso;


// Referenced classes of package oracle.net.aso:
//            C05, C03, C00

public class C10 extends C05
    implements C00 {


    public void c(byte abyte0[], byte abyte1[]) throws C03 {
        if (abyte0 == null && abyte1 == null) {
            throw new C03(102);
        }
        if (abyte0.length < 16) {
            throw new C03(102);
        } else {
            System.arraycopy(abyte0, 0, A, 0, 8);
            System.arraycopy(abyte0, 8, C, 0, 8);
            System.arraycopy(A, 0, F, 0, 8);
            super.v = true;
            return;
        }
    }

    public void a() {
    }

    public void a(byte abyte0[], byte abyte1[]) throws C03 {
        super.v = true;
        if (abyte0 == null && abyte1 == null) {
            if (A == null) {
                throw new C03(102);
            } else {
                return;
            }
        }
        if (abyte0.length < 16) {
            throw new C03(102);
        } else {
            System.arraycopy(abyte0, 0, A, 0, 8);
            System.arraycopy(abyte0, 8, C, 0, 8);
            System.arraycopy(A, 0, F, 0, 8);
            return;
        }
    }
}
