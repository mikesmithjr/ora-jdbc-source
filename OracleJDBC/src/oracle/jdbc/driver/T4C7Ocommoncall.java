package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4C7Ocommoncall extends T4CTTIfun {
    T4CConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C7Ocommoncall(T4CMAREngine _mrengine, T4CTTIoer _oer, T4CConnection _connection)
            throws IOException, SQLException {
        super((byte) 3, 0);

        setMarshalingEngine(_mrengine);

        this.oer = _oer;
        this.connection = _connection;
    }

    void init(short _funCode) throws IOException, SQLException {
        this.oer.init();

        this.funCode = _funCode;
    }

    void receive() throws SQLException, IOException {
        boolean rpaProcessed = false;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 9:
                    if (this.meg.versionNumber < 10000)
                        continue;
                    short _endToEndECIDSequenceNumber = (short) this.meg.unmarshalUB2();

                    this.connection.endToEndECIDSequenceNumber = _endToEndECIDSequenceNumber;

                    break;
                case 4:
                    this.oer.init();
                    this.oer.unmarshal();

                    if (this.oer.retCode != 2089) {
                        this.oer.processError();
                    }
                    break;
                default:
                    DatabaseError.throwSqlException(401);

                    continue;
                }
            } catch (BreakNetException ea) {
            }
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C7Ocommoncall"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C7Ocommoncall JD-Core Version: 0.6.0
 */