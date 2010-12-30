package oracle.jdbc.oracore;

import java.io.ByteArrayOutputStream;
import oracle.jdbc.driver.OracleLog;

public class PickleOutputStream extends ByteArrayOutputStream {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public PickleOutputStream() {
    }

    public PickleOutputStream(int size) {
        super(size);
    }

    public synchronized int offset() {
        return this.count;
    }

    public synchronized void overwrite(int beginOff, byte[] b, int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) || (off + len > b.length) || (off + len < 0)
                || (beginOff + len > this.buf.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }

        for (int i = 0; i < len; i++) {
            this.buf[(beginOff + i)] = b[(off + i)];
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.PickleOutputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.PickleOutputStream JD-Core Version: 0.6.0
 */