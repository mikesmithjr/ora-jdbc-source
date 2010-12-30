package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4C7Oversion extends T4CTTIfun {
    byte[] rdbmsVersion = { 78, 111, 116, 32, 100, 101, 116, 101, 114, 109, 105, 110, 101, 100, 32,
            121, 101, 116 };

    boolean rdbmsVersionO2U = false;

    int bufLen = 256;
    boolean retVerLenO2U = false;
    int retVerLen = 0;
    boolean retVerNumO2U = false;
    long retVerNum = 0L;
    T4CConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C7Oversion(T4CMAREngine _mrengine, T4CTTIoer _oer, T4CConnection _connection)
            throws IOException, SQLException {
        super((byte) 3, 0, (short) 59);

        setMarshalingEngine(_mrengine);

        this.oer = _oer;

        this.rdbmsVersionO2U = true;
        this.retVerLenO2U = true;
        this.retVerNumO2U = true;
        this.connection = _connection;
    }

    void receive() throws SQLException, IOException {
        boolean rpaProcessed = false;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    if (rpaProcessed) {
                        DatabaseError.throwSqlException(401);
                    }

                    this.retVerLen = this.meg.unmarshalUB2();
                    this.rdbmsVersion = this.meg.unmarshalCHR(this.retVerLen);

                    if (this.rdbmsVersion == null) {
                        DatabaseError.throwSqlException(438);
                    }
                    this.retVerNum = this.meg.unmarshalUB4();

                    rpaProcessed = true;

                    break;
                case 4:
                    this.oer.init();
                    this.oer.unmarshal();

                    this.oer.processError();

                    break;
                case 9:
                    if (getVersionNumber() < 10000)
                        continue;
                    short _endToEndECIDSequenceNumber = (short) this.meg.unmarshalUB2();

                    this.connection.endToEndECIDSequenceNumber = _endToEndECIDSequenceNumber;

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
            }
        }
    }

    byte[] getVersion() {
        return this.rdbmsVersion;
    }

    short getVersionNumber() {
        int ver = 0;

        ver = (int) (ver + (this.retVerNum >>> 24 & 0xFF) * 1000L);
        ver = (int) (ver + (this.retVerNum >>> 20 & 0xF) * 100L);
        ver = (int) (ver + (this.retVerNum >>> 12 & 0xF) * 10L);
        ver = (int) (ver + (this.retVerNum >>> 8 & 0xF));

        return (short) ver;
    }

    long getVersionNumberasIs() {
        return this.retVerNum;
    }

    void marshal() throws IOException {
        marshalFunHeader();

        this.meg.marshalO2U(this.rdbmsVersionO2U);
        this.meg.marshalSWORD(this.bufLen);
        this.meg.marshalO2U(this.retVerLenO2U);
        this.meg.marshalO2U(this.retVerNumO2U);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C7Oversion"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C7Oversion JD-Core Version: 0.6.0
 */