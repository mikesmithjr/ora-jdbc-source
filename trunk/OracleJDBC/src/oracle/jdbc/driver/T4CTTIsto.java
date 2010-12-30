package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIsto extends T4CTTIfun {
    static final int OV6STRT = 48;
    static final int OV6STOP = 49;
    static final int STOMFDBA = 1;
    static final int STOMFACA = 2;
    static final int STOMFALO = 4;
    static final int STOMFSHU = 8;
    static final int STOMFFRC = 16;
    static final int STOMFPOL = 32;
    static final int STOMFABO = 64;
    static final int STOMFATX = 128;
    static final int STOMFLTX = 256;
    static final int STOSDONE = 1;
    static final int STOSINPR = 2;
    static final int STOSERR = 3;
    T4CTTIoer oer;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    T4CTTIsto(T4CMAREngine _mrengine, T4CTTIoer _oer) throws IOException, SQLException {
        super((byte)3, 0, (short) 0);

        this.oer = _oer;

        setMarshalingEngine(_mrengine);
    }

    void marshal(int mode) throws IOException, SQLException {
        if ((mode == 1) || (mode == 16))
            this.funCode = 48;
        else if ((mode == 2) || (mode == 4) || (mode == 8) || (mode == 32) || (mode == 64)
                || (mode == 128) || (mode == 256)) {
            this.funCode = 49;
        } else
            throw new SQLException("Invalid mode.");

        super.marshalFunHeader();
        this.meg.marshalSWORD(mode);
        this.meg.marshalPTR();
    }

    int receive() throws SQLException, IOException {
        int mode = 0;
        try {
            byte code = this.meg.unmarshalSB1();
            int ign1;
            if (code == 8) {
                mode = (int) this.meg.unmarshalUB4();

                if (mode == 3) {
                    throw new SQLException("An error occurred during startup/shutdown");
                }
                ign1 = this.meg.unmarshalUB1();
            } else if (code == 4) {
                this.oer.init();
                this.oer.unmarshal();
                try {
                    this.oer.processError();
                } catch (SQLException sqlex) {
                    throw sqlex;
                }
            } else {
                DatabaseError.throwSqlException(401);
            }

        } catch (BreakNetException ea) {
            byte code = this.meg.unmarshalSB1();
            if (code == 4) {
                this.oer.init();
                this.oer.unmarshal();
                this.oer.processError();
            }
        }

        return mode;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIsto"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIsto JD-Core Version: 0.6.0
 */