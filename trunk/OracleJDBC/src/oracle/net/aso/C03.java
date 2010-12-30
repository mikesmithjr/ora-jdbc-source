package oracle.net.aso;

import java.io.IOException;

public class C03 extends IOException {
    public static final int b = 102;
    public static final int c = 104;
    static final int d = 300;
    public static final int e = 103;
    public static final int f = 100;
    public static final int g = 105;
    public static final int h = 101;
    static final int i = 100;
    public static final int j = 106;
    private int k;

    public C03(int paramInt) {
        this.k = paramInt;
    }

    public int a() {
        return this.k;
    }

    public String getMessage() {
        return Integer.toString(this.k);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.net.aso.C03 JD-Core Version: 0.6.0
 */