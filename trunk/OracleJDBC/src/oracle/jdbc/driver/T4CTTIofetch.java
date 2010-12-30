package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIofetch extends T4CTTIfun {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIofetch(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte)3, 0, (short) 5);

        setMarshalingEngine(_mrengine);
    }

    void marshal(int cursorId, int nbOfRows) throws IOException, SQLException {
        super.marshalFunHeader();
        this.meg.marshalSWORD(cursorId);
        this.meg.marshalSWORD(nbOfRows);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIofetch"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIofetch JD-Core Version: 0.6.0
 */