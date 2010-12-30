package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIOtxen extends T4CTTIfun {
    static final int OTXCOMIT = 1;
    static final int OTXABORT = 2;
    static final int OTXPREPA = 3;
    static final int OTXFORGT = 4;
    static final int OTXRECOV = 5;
    static final int OTXMLPRE = 6;
    static final int K2CMDprepare = 0;
    static final int K2CMDrqcommit = 1;
    static final int K2CMDcommit = 2;
    static final int K2CMDabort = 3;
    static final int K2CMDrdonly = 4;
    static final int K2CMDforget = 5;
    static final int K2CMDrecovered = 7;
    static final int K2CMDtimeout = 8;
    static final int K2STAidle = 0;
    static final int K2STAcollecting = 1;
    static final int K2STAprepared = 2;
    static final int K2STAcommitted = 3;
    static final int K2STAhabort = 4;
    static final int K2STAhcommit = 5;
    static final int K2STAhdamage = 6;
    static final int K2STAtimeout = 7;
    static final int K2STAinactive = 9;
    static final int K2STAactive = 10;
    static final int K2STAptprepared = 11;
    static final int K2STAptcommitted = 12;
    static final int K2STAmax = 13;
    T4CConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIOtxen(T4CMAREngine _mrengine, T4CTTIoer _oer, T4CConnection _conn) throws SQLException {
        super((byte)3, 0, (short) 104);

        this.oer = _oer;

        setMarshalingEngine(_mrengine);

        this.connection = _conn;
    }

    void marshal(int operation, byte[] transactionContext, byte[] xid, int formatId,
            int gtridLength, int bqualLength, int timeout, int inState) throws IOException,
            SQLException {
        if ((operation != 1) && (operation != 2) && (operation != 3) && (operation != 4)
                && (operation != 5) && (operation != 6)) {
            throw new SQLException("Invalid operation.");
        }
        super.marshalFunHeader();

        int txnopc = operation;

        this.meg.marshalSWORD(txnopc);

        if (transactionContext == null)
            this.meg.marshalNULLPTR();
        else {
            this.meg.marshalPTR();
        }

        if (transactionContext == null)
            this.meg.marshalUB4(0L);
        else {
            this.meg.marshalUB4(transactionContext.length);
        }

        this.meg.marshalUB4(formatId);

        this.meg.marshalUB4(gtridLength);

        this.meg.marshalUB4(bqualLength);

        if (xid != null)
            this.meg.marshalPTR();
        else {
            this.meg.marshalNULLPTR();
        }

        if (xid != null)
            this.meg.marshalUB4(xid.length);
        else {
            this.meg.marshalUB4(0L);
        }

        this.meg.marshalUWORD(timeout);

        this.meg.marshalUB4(inState);

        this.meg.marshalPTR();

        if (transactionContext != null) {
            this.meg.marshalB1Array(transactionContext);
        }
        if (xid != null)
            this.meg.marshalB1Array(xid);
    }

    int receive(int[] errorNumber) throws IOException, SQLException {
        int outState = -1;

        errorNumber[0] = -1;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    outState = (int) this.meg.unmarshalUB4();

                    break;
                case 9:
                    if (this.meg.versionNumber < 10000)
                        continue;
                    short _endToEndECIDSequenceNumber = (short) this.meg.unmarshalUB2();

                    this.connection.endToEndECIDSequenceNumber = _endToEndECIDSequenceNumber;

                    break;
                case 4:
                    this.oer.init();
                    this.oer.unmarshal();

                    this.oer.processError(false);

                    errorNumber[0] = this.oer.retCode;

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
                return outState;
            }
        }

    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIOtxen"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIOtxen JD-Core Version: 0.6.0
 */