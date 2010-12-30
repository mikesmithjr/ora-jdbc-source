package oracle.net.aso;

class C01 {
    private int c;
    private final C12 d;
    private int e;
    private byte[] f;
    private final char g = '\u0100';

    public byte[] a(byte[] paramArrayOfByte, int paramInt) {
        byte[] arrayOfByte = new byte[paramInt];
        int i = this.c;
        int j = this.e;
        for (int k = 0; k < paramInt; k++) {
            i = i + 1 & 0xFF;
            int m = this.f[i];
            j = j + m & 0xFF;
            int n = this.f[j];
            this.f[i] = (byte) (n & 0xFF);
            this.f[j] = (byte) (m & 0xFF);
            int i1 = m + n & 0xFF;
            arrayOfByte[k] = (byte) ((paramArrayOfByte[k] ^ this.f[i1]) & 0xFF);
        }
        this.c = i;
        this.e = j;
        return arrayOfByte;
    }

    public void b(byte[] paramArrayOfByte, int paramInt) {
        for (int j = 0; j < 256; j++) {
            f[j] = (byte)j;
        }

        c = e = 0;
        int k = 0;
        int l = 0;
        for (int i1 = 0; k < 256; i1++) {
            byte byte0 = f[k];
            if (i1 == paramInt) {
                i1 = 0;
            }
            l = l + byte0 + paramArrayOfByte[i1] & 0xff;
            f[k] = f[l];
            f[l] = byte0;
            k++;
        }

    }

    public C01(C12 paramC12) {
        this.d = paramC12;
        this.f = new byte[256];
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.net.aso.C01 JD-Core Version: 0.6.0
 */