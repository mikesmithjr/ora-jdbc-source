package oracle.jdbc.driver;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.SQLException;

class T4C8TTIpro extends T4CTTIMsg {
    short svrCharSet;
    short svrCharSetElem;
    byte svrFlags;
    byte[] proSvrStr;
    byte proSvrVer;
    short oVersion = -1;

    boolean svrInfoAvailable = false;

    byte[] proCliVerTTC8 = { 6, 5, 4, 3, 2, 1, 0 };

    byte[] proCliStrTTC8 = { 74, 97, 118, 97, 95, 84, 84, 67, 45, 56, 46, 50, 46, 48, 0 };

    short NCHAR_CHARSET = 0;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTIpro(T4CMAREngine mrengine) throws SQLException, IOException {
        super((byte) 1);

        setMarshalingEngine(mrengine);
    }

    void receive() throws SQLException, IOException {
        if (this.meg.unmarshalSB1() != 1) {
            DatabaseError.throwSqlException(401);
        }
        this.proSvrVer = this.meg.unmarshalSB1();
        this.meg.proSvrVer = this.proSvrVer;

        switch (this.proSvrVer) {
        case 4:
            this.oVersion = 7230;

            break;
        case 5:
            this.oVersion = 8030;

            break;
        case 6:
            this.oVersion = 8100;

            break;
        default:
            DatabaseError.throwSqlException(444);
        }

        this.meg.unmarshalSB1();

        this.proSvrStr = this.meg.unmarshalTEXT(50);
        this.oVersion = getOracleVersion();

        this.svrCharSet = (short) this.meg.unmarshalUB2();
        this.svrFlags = (byte) this.meg.unmarshalUB1();

        if ((this.svrCharSetElem = (short) this.meg.unmarshalUB2()) > 0) {
            this.meg.unmarshalNBytes(this.svrCharSetElem * 5);
        }

        this.svrInfoAvailable = true;

        if (this.proSvrVer < 5) {
            return;
        }

        byte rep = this.meg.types.getRep((byte) 1);

        this.meg.types.setRep((byte)1, (byte)0);

        int fdoLength = this.meg.unmarshalUB2();

        this.meg.types.setRep((byte)1, (byte)rep);

        byte[] fdo = this.meg.unmarshalNBytes(fdoLength);

        int i = 6 + (fdo[5] & 0xFF) + (fdo[6] & 0xFF);

        this.NCHAR_CHARSET = (short) ((fdo[(i + 3)] & 0xFF) << 8);
        this.NCHAR_CHARSET = (short) (this.NCHAR_CHARSET | (short) (fdo[(i + 4)] & 0xFF));

        if (this.proSvrVer < 6) {
            return;
        }

        int len = this.meg.unmarshalUB1();

        for (int j = 0; j < len; j++) {
            this.meg.unmarshalUB1();
        }

        len = this.meg.unmarshalUB1();

        for (int j = 0; j < len; j++)
            this.meg.unmarshalUB1();
    }

    short getOracleVersion() {
        return this.oVersion;
    }

    short getCharacterSet() {
        return this.svrCharSet;
    }

    short getncharCHARSET() {
        return this.NCHAR_CHARSET;
    }

    byte getFlags() {
        return this.svrFlags;
    }

    void marshal() throws SQLException, IOException {
        marshalTTCcode();

        this.meg.marshalB1Array(this.proCliVerTTC8);
        this.meg.marshalB1Array(this.proCliStrTTC8);
    }

    void printServerInfo(int mod, int submod, int category) {
        OracleLog.print(this, mod, submod, category, " ---- Server's Information ---- ");

        if (this.svrInfoAvailable) {
            int i = 0;

            OracleLog.print(this, mod, submod, category, "Protocol version :" + this.proSvrVer);

            OracleLog.print(this, mod, submod, category, "oVersion :" + this.oVersion);

            StringWriter s = new StringWriter();

            s.write("Protocol string  :");

            while (i < this.proSvrStr.length) {
                s.write((char) this.proSvrStr[(i++)]);
            }
            OracleLog.print(this, mod, submod, category, s.toString());
            OracleLog.print(this, mod, submod, category, "Caracter Set ID  :" + this.svrCharSet);

            OracleLog.print(this, mod, submod, category, "Remote flags     :" + this.svrFlags);

            OracleLog.print(this, mod, submod, category,
                            "Number of Elements in Server's Character Set Graph :"
                                    + this.svrCharSetElem);

            OracleLog.print(this, mod, submod, category,
                            "Don't expect the graph, we threw it away :-)");
        } else {
            OracleLog.print(this, mod, submod, category, " Not Available !!");
        }
        OracleLog.print(this, mod, submod, category, " ---- -------------------- ---- ");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTIpro"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTIpro JD-Core Version: 0.6.0
 */