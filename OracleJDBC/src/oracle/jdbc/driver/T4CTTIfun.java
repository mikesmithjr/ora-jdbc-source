package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIfun extends T4CTTIMsg {
    static final short OOPEN = 2;
    static final short OEXEC = 4;
    static final short OFETCH = 5;
    static final short OCLOSE = 8;
    static final short OLOGOFF = 9;
    static final short OCOMON = 12;
    static final short OCOMOFF = 13;
    static final short OCOMMIT = 14;
    static final short OROLLBACK = 15;
    static final short OCANCEL = 20;
    static final short ODSCRARR = 43;
    static final short OVERSION = 59;
    static final short OK2RPC = 67;
    static final short OALL7 = 71;
    static final short OSQL7 = 74;
    static final short O3LOGON = 81;
    static final short O3LOGA = 82;
    static final short OKOD = 92;
    static final short OALL8 = 94;
    static final short OLOBOPS = 96;
    static final short ODNY = 98;
    static final short OTXSE = 103;
    static final short OTXEN = 104;
    static final short OCCA = 105;
    static final short O80SES = 107;
    static final short OAUTH = 115;
    static final short OSESSKEY = 118;
    static final short OCANA = 120;
    static final short OOTCM = 127;
    static final short OSCID = 135;
    static final short OKPFC = 139;
    static final short OKEYVAL = 154;
    short funCode;
    byte seqNumber;
    T4CTTIoer oer;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIfun(byte _ttcCode, int _seqNumber) {
        super(_ttcCode);

        this.seqNumber = (byte) _seqNumber;
    }

    T4CTTIfun(byte _ttcCode, int _seqNumber, short _funCode) {
        super(_ttcCode);

        this.funCode = _funCode;
        this.seqNumber = (byte) _seqNumber;
    }

    void setFunCode(short _funCode) {
        this.funCode = _funCode;
    }

    void marshalFunHeader() throws IOException {
        marshalTTCcode();
        this.meg.marshalUB1(this.funCode);
        this.meg.marshalUB1((short) this.seqNumber);
    }

    void marshal() throws IOException, SQLException {
        marshalFunHeader();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIfun"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIfun JD-Core Version: 0.6.0
 */