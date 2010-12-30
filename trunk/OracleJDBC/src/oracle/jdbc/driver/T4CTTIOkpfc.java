package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIOkpfc extends T4CTTIfun {
    static final int KPFCI_CONNECT = 1;
    static final int KPFCI_REVERT = 2;
    static final int KPFCI_TEST = 4;
    static final int KPFCI_STRM = 8;
    static final int KPFCI_PING = 16;
    static final int KPFCI_TCPCONNECT = 32;
    static final int KPFCI_TCPREVERT = 64;
    static final int KPFCV_VENDOR = 0;
    static final int KPFCV_PROTO = 1;
    static final int KPFCV_MAJOR = 2;
    static final int KPFCV_MINOR = 3;
    static final int KPFCO_CONNECT = 1;
    static final int KPFCO_REVERT = 2;
    static final int KPFCO_TCPCONNECT = 4;
    static final int KPFCO_TCPREVERT = 8;

    T4CTTIOkpfc(T4CMAREngine _mrengine) throws SQLException {
        super((byte)3, 0, (short) 139);

        setMarshalingEngine(_mrengine);
    }

    void marshal(int kpfcflgi) throws IOException, SQLException {
        if ((kpfcflgi != 1) && (kpfcflgi != 2) && (kpfcflgi != 4) && (kpfcflgi != 8)
                && (kpfcflgi != 16) && (kpfcflgi != 32) && (kpfcflgi != 64)) {
            throw new SQLException("Invalid operation.");
        }
        super.marshalFunHeader();
        this.meg.marshalUB4(kpfcflgi);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalUB4(0L);
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalUB4(0L);
        this.meg.marshalNULLPTR();
        this.meg.marshalPTR();
    }

    void receive() throws SQLException, IOException {
        byte[] ctx = null;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    int length = (int) this.meg.unmarshalUB4();

                    ctx = new byte[length];

                    int i = 0;
                    if (i < length) {
                        ctx[i] = (byte) (int) this.meg.unmarshalUB4();

                        i++;
                        continue;
                    }

                    break;
                case 4:
                    this.oer.init();
                    this.oer.unmarshal();

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
            }
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIOkpfc JD-Core Version: 0.6.0
 */