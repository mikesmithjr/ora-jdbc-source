package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIk2rpc extends T4CTTIfun {
    T4CConnection connection;
    static final int K2RPClogon = 1;
    static final int K2RPCbegin = 2;
    static final int K2RPCend = 3;
    static final int K2RPCrecover = 4;
    static final int K2RPCsession = 5;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIk2rpc(T4CMAREngine _mrengine, T4CTTIoer _oer, T4CConnection _conn) throws SQLException {
        super((byte)3, 0, (short) 67);

        this.oer = _oer;

        setMarshalingEngine(_mrengine);

        this.connection = _conn;
    }

    void marshal(int k2rpctyp, int command) throws IOException, SQLException {
        super.marshalFunHeader();

        this.meg.marshalUB4(0L);
        this.meg.marshalUB4(k2rpctyp);
        this.meg.marshalPTR();
        this.meg.marshalUB4(3L);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalPTR();
        this.meg.marshalUB4(3L);
        this.meg.marshalPTR();
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();

        this.meg.marshalUB4(command);
        this.meg.marshalUB4(0L);
        this.meg.marshalUB4(0L);
    }

    void receive() throws SQLException, IOException {
        while (true)
            try {
                int code = this.meg.unmarshalSB1();

                switch (code) {
                case 9:
                    if (this.meg.versionNumber < 10000)
                        continue;
                    short _endToEndECIDSequenceNumber = (short) this.meg.unmarshalUB2();

                    this.connection.endToEndECIDSequenceNumber = _endToEndECIDSequenceNumber;

                    break;
                case 8:
                    int k2rpco4l = this.meg.unmarshalUB2();

                    int i = 0;
                    if (i >= k2rpco4l)
                        continue;
                    this.meg.unmarshalUB4();

                    i++;
                    continue;

                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
            }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIk2rpc"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIk2rpc JD-Core Version: 0.6.0
 */