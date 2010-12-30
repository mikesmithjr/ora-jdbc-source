package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIOtxse extends T4CTTIfun {
    static final int OTXSTA = 1;
    static final int OTXDET = 2;
    static final int OCI_TRANS_NEW = 1;
    static final int OCI_TRANS_JOIN = 2;
    static final int OCI_TRANS_RESUME = 4;
    static final int OCI_TRANS_STARTMASK = 255;
    static final int OCI_TRANS_READONLY = 256;
    static final int OCI_TRANS_READWRITE = 512;
    static final int OCI_TRANS_SERIALIZABLE = 1024;
    static final int OCI_TRANS_ISOLMASK = 65280;
    static final int OCI_TRANS_LOOSE = 65536;
    static final int OCI_TRANS_TIGHT = 131072;
    static final int OCI_TRANS_TYPEMASK = 983040;
    static final int OCI_TRANS_NOMIGRATE = 1048576;
    static final int OCI_TRANS_SEPARABLE = 2097152;
    boolean sendTransactionContext = false;
    T4CConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIOtxse(T4CMAREngine _mrengine, T4CTTIoer _oer, T4CConnection _conn) throws SQLException {
        super((byte)3, 0, (short) 103);

        this.oer = _oer;

        setMarshalingEngine(_mrengine);

        this.connection = _conn;
    }

    void marshal(int operation, byte[] transactionContext, byte[] xid, int formatId,
            int gtridLength, int bqualLength, int timeout, int flag) throws IOException,
            SQLException {
        if ((operation != 1) && (operation != 2)) {
            throw new SQLException("Invalid operation.");
        }
        super.marshalFunHeader();

        int xidopc = operation;

        this.meg.marshalSWORD(xidopc);

        if (operation == 2) {
            if (transactionContext == null) {
                throw new SQLException("Transaction context cannot be null when detach is called.");
            }
            this.sendTransactionContext = true;

            this.meg.marshalPTR();
        } else {
            this.sendTransactionContext = false;

            this.meg.marshalNULLPTR();
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

        this.meg.marshalUB4(flag);

        this.meg.marshalUWORD(timeout);

        this.meg.marshalPTR();

        this.meg.marshalPTR();
        this.meg.marshalPTR();

        if (this.sendTransactionContext) {
            this.meg.marshalB1Array(transactionContext);
        }
        if (xid != null) {
            this.meg.marshalB1Array(xid);
        }
        this.meg.marshalUB4(0L);
    }

    byte[] receive(int[] applicationValueArr) throws SQLException, IOException {
        byte[] ctx = null;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    applicationValueArr[0] = (int) this.meg.unmarshalUB4();

                    int length = this.meg.unmarshalUB2();

                    ctx = new byte[length];

                    int i = 0;
                    if (i < length) {
                        ctx[i] = (byte) this.meg.unmarshalUB1();

                        i++;
                        continue;
                    }

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
                    try {
                        this.oer.processError();
                    } catch (SQLException sqlex) {
                        throw sqlex;
                    }

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
                return ctx;
            }
        }

    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIOtxse"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIOtxse JD-Core Version: 0.6.0
 */