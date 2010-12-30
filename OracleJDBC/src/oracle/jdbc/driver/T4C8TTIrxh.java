package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8TTIrxh extends T4CTTIMsg {
    short flags;
    int numRqsts;
    int iterNum;
    int numItersThisTime;
    int uacBufLength;
    static final byte FU2O = 1;
    static final byte FEOR = 2;
    static final byte PLSV = 4;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTIrxh(T4CMAREngine _mrengine) {
        setMarshalingEngine(_mrengine);
    }

    void unmarshalV10(T4CTTIrxd rxd) throws SQLException, IOException {
        this.flags = this.meg.unmarshalUB1();
        this.numRqsts = this.meg.unmarshalUB2();
        this.iterNum = this.meg.unmarshalUB2();

        this.numRqsts += this.iterNum * 256;

        this.numItersThisTime = this.meg.unmarshalUB2();
        this.uacBufLength = this.meg.unmarshalUB2();

        byte[] bitVector = this.meg.unmarshalDALC();
        rxd.readBitVector(bitVector);

        byte[] rowid = this.meg.unmarshalDALC();
    }

    void init() {
        this.flags = 0;
        this.numRqsts = 0;
        this.iterNum = 0;
        this.numItersThisTime = 0;
        this.uacBufLength = 0;
    }

    void print(int mod, int submod, int cat) {
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTIrxh"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTIrxh JD-Core Version: 0.6.0
 */