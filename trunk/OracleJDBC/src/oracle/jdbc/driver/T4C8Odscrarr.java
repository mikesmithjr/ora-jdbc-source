package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4C8Odscrarr extends T4CTTIfun {
    byte operationflags = 7;
    byte[] sqltext = new byte[0];
    long sqlparseversion = 2L;
    T4CTTIdcb dcb;
    int cursor_id = 0;

    int numuds = 0;

    boolean numitemsO2U = true;

    boolean udsarrayO2U = true;
    boolean numudsO2U = true;
    boolean colnameO2U = true;
    boolean lencolsO2U = true;

    OracleStatement statement = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8Odscrarr(T4CMAREngine _mrengine, T4CTTIoer _oer) throws IOException, SQLException {
        super((byte)3, 0, (short) 43);

        setMarshalingEngine(_mrengine);

        this.oer = _oer;
        this.funCode = 98;
        this.dcb = new T4CTTIdcb(_mrengine);
    }

    void init(OracleStatement stmt, int _offset) throws IOException, SQLException {
        this.numuds = 0;

        this.numitemsO2U = true;
        this.udsarrayO2U = true;
        this.numudsO2U = true;
        this.colnameO2U = true;
        this.lencolsO2U = true;

        this.oer.init();

        this.cursor_id = stmt.cursorId;
        this.statement = stmt;

        this.operationflags = 7;
        this.sqltext = new byte[0];
        this.sqlparseversion = 2L;

        this.dcb.init(stmt, _offset);
    }

    void marshal() throws IOException {
        marshalFunHeader();

        this.meg.marshalUB1((short) this.operationflags);
        this.meg.marshalSWORD(this.cursor_id);

        if (this.sqltext.length == 0)
            this.meg.marshalNULLPTR();
        else {
            this.meg.marshalPTR();
        }
        this.meg.marshalSB4(this.sqltext.length);

        this.meg.marshalUB4(this.sqlparseversion);

        this.meg.marshalO2U(this.udsarrayO2U);
        this.meg.marshalO2U(this.numudsO2U);

        this.meg.marshalCHR(this.sqltext);
    }

    Accessor[] receive(Accessor[] accessors) throws SQLException, IOException {
        boolean descinfo_read = false;

        while (!descinfo_read) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    accessors = this.dcb.receiveCommon(accessors, true);
                    this.numuds = this.dcb.numuds;

                    break;
                case 4:
                    this.oer.init();
                    this.oer.unmarshal();
                    this.oer.processError();

                    descinfo_read = true;

                    break;
                case 9:
                    descinfo_read = true;

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

            } catch (BreakNetException ea) {
            }

        }

        return accessors;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8Odscrarr"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8Odscrarr JD-Core Version: 0.6.0
 */