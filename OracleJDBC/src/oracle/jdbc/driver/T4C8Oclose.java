package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8Oclose extends T4CTTIfun {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8Oclose(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte) 17, 0);

        setMarshalingEngine(_mrengine);
        setFunCode((short) 120);
    }

    void initCloseQuery() throws IOException, SQLException {
        setFunCode((short) 120);
    }

    void initCloseStatement() throws IOException, SQLException {
        setFunCode((short) 105);
    }

    void marshal(int[] cursorId, int offset) throws IOException {
        marshalFunHeader();

        this.meg.marshalPTR();
        this.meg.marshalUB4(offset);

        for (int i = 0; i < offset; i++) {
            this.meg.marshalUB4(cursorId[i]);
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8Oclose"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8Oclose JD-Core Version: 0.6.0
 */