package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIoexec extends T4CTTIfun {
    static final int EXE_COMMIT_ON_SUCCESS = 1;
    static final int EXE_LEAVE_CUR_MAPPED = 2;
    static final int EXE_BATCH_DML_ERRORS = 4;
    static final int EXE_SCROL_READ_ONLY = 8;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIoexec(T4CMAREngine _mrengine) throws IOException, SQLException {
        super((byte)3, 0, (short) 4);

        setMarshalingEngine(_mrengine);
    }

    void marshal(int cursorId, int nbOfIter, int flag) throws IOException, SQLException {
        super.marshalFunHeader();
        this.meg.marshalSWORD(cursorId);
        this.meg.marshalSWORD(nbOfIter);
        this.meg.marshalSWORD(0);
        this.meg.marshalSWORD(flag);
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
 * oracle.jdbc.driver.T4CTTIoexec JD-Core Version: 0.6.0
 */