package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIoses extends T4CTTIfun {
    static final int OSESSWS = 1;
    static final int OSESDET = 3;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIoses(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte) 17, 0);

        setMarshalingEngine(_mrengine);
        setFunCode((short) 107);
    }

    void marshal(int sididx, int sidser, int sidopc) throws IOException, SQLException {
        if ((sidopc != 1) && (sidopc != 3)) {
            throw new SQLException("Wrong operation : can only do switch or detach");
        }
        marshalFunHeader();
        this.meg.marshalUB4(sididx);
        this.meg.marshalUB4(sidser);
        this.meg.marshalUB4(sidopc);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIoses"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIoses JD-Core Version: 0.6.0
 */