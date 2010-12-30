package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CTTIoac {
    static final short UACFIND = 1;
    static final short UACFALN = 2;
    static final short UACFRCP = 4;
    static final short UACFBBV = 8;
    static final short UACFNCP = 16;
    static final short UACFBLP = 32;
    static final short UACFARR = 64;
    static final short UACFIGN = 128;
    static final short UACFNSCL = 1;
    static final short UACFBUC = 2;
    static final short UACFSKP = 4;
    static final short UACFCHRCNT = 8;
    static final short UACFNOADJ = 16;
    static final short UACFCUS = 4096;
    static final byte[] NO_BYTES = new byte[0];
    boolean isStream;
    int ncs;
    short formOfUse;
    static int maxBindArrayLength;
    T4CMAREngine meg;
    short oacdty;
    short oacflg;
    short oacpre;
    short oacscl;
    int oacmxl;
    int oacmxlc = 0;
    int oacmal;
    int oacfl2;
    byte[] oactoid;
    int oactoidl;
    int oacvsn;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIoac(T4CMAREngine _mrengine) {
        this.meg = _mrengine;
    }

    T4CTTIoac(T4CTTIoac _oac) {
        this.meg = _oac.meg;
        this.isStream = _oac.isStream;
        this.ncs = _oac.ncs;
        this.formOfUse = _oac.formOfUse;
        this.oacdty = _oac.oacdty;
        this.oacflg = _oac.oacflg;
        this.oacpre = _oac.oacpre;
        this.oacscl = _oac.oacscl;
        this.oacmxl = _oac.oacmxl;
        this.oacmal = _oac.oacmal;
        this.oacfl2 = _oac.oacfl2;
        this.oactoid = _oac.oactoid;
        this.oactoidl = _oac.oactoidl;
        this.oacvsn = _oac.oacvsn;
    }

    boolean isOldSufficient(T4CTTIoac _oac) {
        boolean isSame = false;

        if ((this.oactoidl != 0) || (_oac.oactoidl != 0)) {
            return false;
        }
        if ((this.isStream == _oac.isStream)
                && (this.ncs == _oac.ncs)
                && (this.formOfUse == _oac.formOfUse)
                && (this.oacdty == _oac.oacdty)
                && (this.oacflg == _oac.oacflg)
                && (this.oacpre == _oac.oacpre)
                && (this.oacscl == _oac.oacscl)
                && ((this.oacmxl == _oac.oacmxl) || ((_oac.oacmxl > this.oacmxl) && (_oac.oacmxl < 4000)))
                && (this.oacmxlc == _oac.oacmxlc) && (this.oacmal == _oac.oacmal)
                && (this.oacfl2 == _oac.oacfl2) && (this.oacvsn == _oac.oacvsn)) {
            isSame = true;
        }
        return isSame;
    }

    boolean isNType() {
        return this.formOfUse == 2;
    }

    void unmarshal() throws IOException, SQLException {
        this.oacdty = this.meg.unmarshalUB1();
        this.oacflg = this.meg.unmarshalUB1();
        this.oacpre = this.meg.unmarshalUB1();

        if ((this.oacdty == 2) || (this.oacdty == 180) || (this.oacdty == 181)
                || (this.oacdty == 231) || (this.oacdty == 183)) {
            this.oacscl = (short) this.meg.unmarshalUB2();
        } else
            this.oacscl = this.meg.unmarshalUB1();

        this.oacmxl = this.meg.unmarshalSB4();
        this.oacmal = this.meg.unmarshalSB4();
        this.oacfl2 = this.meg.unmarshalSB4();

        if (this.oacmxl > 0) {
            switch (this.oacdty) {
            case 2:
                this.oacmxl = 22;

                break;
            case 12:
                this.oacmxl = 7;

                break;
            case 181:
                this.oacmxl = 13;
            }

        }

        if (this.oacdty == 11) {
            this.oacdty = 104;
        }
        this.oactoid = this.meg.unmarshalDALC();
        this.oactoidl = (this.oactoid == null ? 0 : this.oactoid.length);
        this.oacvsn = this.meg.unmarshalUB2();
        this.ncs = this.meg.unmarshalUB2();
        this.formOfUse = this.meg.unmarshalUB1();

        if (this.meg.versionNumber >= 9000) {
            this.oacmxlc = (int) this.meg.unmarshalUB4();
        }
    }

    void init(NamedTypeAccessor accessor) {
        this.oacdty = (short) accessor.internalType;
        this.oacflg = 3;
        this.oacpre = 0;
        this.oacscl = 0;
        this.oacmxl = accessor.internalTypeMaxLength;
        this.oacmal = 0;
        this.oacfl2 = 0;
        this.isStream = accessor.isStream;

        OracleTypeADT tmp_otype = (OracleTypeADT) accessor.internalOtype;

        if (tmp_otype != null) {
            this.oactoid = tmp_otype.getTOID();
            this.oacvsn = tmp_otype.getTypeVersion();
            this.ncs = tmp_otype.getCharSet();
            this.formOfUse = (short) tmp_otype.getCharSetForm();
        } else {
            this.oactoid = NO_BYTES;
            this.oactoidl = this.oactoid.length;
            this.oacvsn = 0;
            this.formOfUse = accessor.formOfUse;

            if (isNType())
                this.ncs = this.meg.conv.getNCharSetId();
            else {
                this.ncs = this.meg.conv.getServerCharSetId();
            }
        }
        if (this.oacdty == 102)
            this.oacmxl = 1;
    }

    void init(PlsqlIndexTableAccessor accessor) {
        initIbt((short) accessor.elementInternalType, accessor.maxNumberOfElements,
                accessor.elementMaxLen);
    }

    void initIbt(short _oacdty, int _oacmal, int _oacmxl) {
        this.oacflg = 67;
        this.oacpre = 0;
        this.oacscl = 0;
        this.oacmal = _oacmal;
        this.oacfl2 = 0;
        this.oacmxl = _oacmxl;
        this.oactoid = null;
        this.oacvsn = 0;
        this.ncs = 0;
        this.formOfUse = 0;

        if ((_oacdty == 9) || (_oacdty == 96) || (_oacdty == 1)) {
            if (this.oacdty != 96) {
                this.oacdty = 1;
            }
            this.oacfl2 = 16;
        } else {
            this.oacdty = 2;
        }
    }

    void init(OracleTypeADT otype, int type, int max_len) {
        this.oacdty = (short) type;
        this.oacflg = 3;
        this.oacpre = 0;
        this.oacscl = 0;
        this.oacmxl = max_len;
        this.oacmal = 0;
        this.oacfl2 = 0;
        this.isStream = false;

        this.oactoid = otype.getTOID();
        this.oacvsn = otype.getTypeVersion();
        this.ncs = 2;
        this.formOfUse = (short) otype.getCharSetForm();

        if (this.oacdty == 102)
            this.oacmxl = 1;
    }

    void init(short _oacdty, int _oacmxl, short DBCS, short NCS, short _formOfUse)
            throws IOException, SQLException {
        this.oacflg = 3;
        this.oacpre = 0;
        this.oacscl = 0;
        this.oacmal = 0;
        this.oacfl2 = 0;
        this.oacdty = _oacdty;
        this.oacmxl = _oacmxl;

        if ((this.oacdty == 96) || (this.oacdty == 9) || (this.oacdty == 1)) {
            if (this.oacdty != 96) {
                this.oacdty = 1;
            }
            this.oacfl2 = 16;
        } else if (this.oacdty == 104) {
            this.oacdty = 11;
        } else if (this.oacdty == 102) {
            this.oacmxl = 1;
        }
        this.oactoid = NO_BYTES;
        this.oactoidl = 0;
        this.oacvsn = 0;

        this.formOfUse = _formOfUse;

        this.ncs = DBCS;

        if (isNType())
            this.ncs = NCS;
    }

    void marshal() throws IOException, SQLException {
        this.meg.marshalUB1(this.oacdty);
        this.meg.marshalUB1(this.oacflg);
        this.meg.marshalUB1(this.oacpre);
        this.meg.marshalUB1(this.oacscl);

        this.meg.marshalUB4(this.oacmxl);

        this.meg.marshalSB4(this.oacmal);
        this.meg.marshalSB4(this.oacfl2);

        this.meg.marshalDALC(this.oactoid);

        this.meg.marshalUB2(this.oacvsn);
        this.meg.marshalUB2(this.ncs);
        this.meg.marshalUB1(this.formOfUse);

        if (this.meg.versionNumber >= 9000) {
            this.meg.marshalUB4(0L);
        }
    }

    boolean isStream() throws SQLException {
        return this.isStream;
    }

    void print(int mod, int submod, int cat) {
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIoac"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIoac JD-Core Version: 0.6.0
 */