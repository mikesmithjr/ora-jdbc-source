package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8TTIdty extends T4CTTIMsg {
    short cliRIN = 1;
    short cliROUT = 1;
    static final int TTCLXMULTI = 1;
    static final int TTCLXMCONV = 2;
    static final int TTCLXNOCNV = 4;
    static final int TTCLXPCEFC = 8;
    static final int TTCLXSCSID = 16;
    static final int TTCLXSIGNCNV = 32;
    static final int TTCLXRSBCF = 64;
    byte cliFlags = 0;

    final int NUMTYPES = 175;
    static final byte TTC_FLD_VSN_820 = 1;
    static final byte TTC_FLD_VSN_902 = 2;
    static final byte TTC_FLD_VSN_1000 = 3;
    static final byte TTC_FLD_VSN_MAX = 3;
    static final byte KPULMAXL = 6;
    static final byte KPCCAP_CTB_TTC1_EOCS = 1;
    static final byte KPCCAP_CTB_TTC1_PBLB = 2;
    static final byte KPCCAP_CTB_TTC1_FNTY = 4;
    static final byte KPCCAP_CTB_TTC1_INRC = 8;
    static final byte KPCCAP_CTB_TTC1_FCSC = 16;
    static final byte KPCCAP_CTB_TTC1_FBVC = 32;
    static final byte KPCCAP_CTB_TTC1_NTEC = 64;
    static final byte KPCCAP_CTB_TTC1_RSHP = -128;
    static final byte KPCCAP_CTB_OCI1_DTME = 1;
    static final byte KPCCAP_CTB_OCI1_NOMIP = 2;
    static final byte KPCCAP_CTB_OCI1_PDFC = 4;
    static final byte KPCCAP_CTB_OCI1_FEXF = 8;
    static final byte KPCCAP_CTB_OCI1_FSAP = 16;
    static final byte KPCCAP_CTB_OCI1_BFLTDBL = 32;
    static final byte KPCCAP_CTB_OCI1_CPSSDML = 64;
    static final byte KPCCAP_CTB_OCI1_APCTX = -128;
    static final byte KOLE_LOB_CAP_UB8_SIZE = 1;
    static final byte KOLE_LOB_CAP_ENCS = 2;
    static final byte KOLE_LOB_CAP_DIL = 4;
    static final byte KOLE_LOB_CAP_TMPLOC_SZ = 8;
    static final byte KOLE_LOB_CAP_ALL = 15;
    static final byte KOPT_VNFT = 3;
    static final byte KPCCAP_CTB_TTC2_ZLNP = 4;
    byte[] CTcap = { 6, 1, 0, 0, 0, 1, 1, 3, 1, 1, 1, 1, 1, 0, 0, 40, -112, 3, 7, 3, 0, 1, 0, 15,
            1, 0, 4, 0, 0 };

    byte[] RTcap = { 2 };

    byte[] typeReps10 = { 1, 1, 1, 0, 2, 2, 10, 0, 8, 8, 1, 0, 12, 12, 10, 0, 100, 100, 1, 0, 21,
            100, 1, 0, 101, 101, 1, 0, 22, 101, 1, 0, 23, 23, 1, 0, 24, 24, 1, 0, 25, 25, 1, 0, 26,
            26, 1, 0, 27, 27, 1, 0, 28, 28, 1, 0, 29, 29, 1, 0, 30, 30, 1, 0, 31, 31, 1, 0, 32, 32,
            1, 0, 33, 33, 1, 0, 10, 10, 1, 0, 11, 11, 1, 0, 34, 34, 1, 0, 35, 35, 1, 0, 36, 36, 1,
            0, 37, 37, 1, 0, 38, 38, 1, 0, 40, 40, 1, 0, 41, 41, 1, 0, 42, 42, 1, 0, 43, 43, 1, 0,
            44, 44, 1, 0, 45, 45, 1, 0, 46, 46, 1, 0, 47, 47, 1, 0, 48, 48, 1, 0, 49, 49, 1, 0, 50,
            50, 1, 0, 51, 51, 1, 0, 52, 52, 1, 0, 53, 53, 1, 0, 54, 54, 1, 0, 55, 55, 1, 0, 56, 56,
            1, 0, 57, 57, 1, 0, 58, 58, 1, 0, 59, 59, 1, 0, 60, 60, 1, 0, 61, 61, 1, 0, 62, 62, 1,
            0, 63, 63, 1, 0, 64, 64, 1, 0, 65, 65, 1, 0, 66, 66, 1, 0, 67, 67, 1, 0, 71, 71, 1, 0,
            72, 72, 1, 0, 73, 73, 1, 0, 75, 75, 1, 0, 77, 77, 1, 0, 78, 78, 1, 0, 79, 79, 1, 0, 80,
            80, 1, 0, 81, 81, 1, 0, 82, 82, 1, 0, 83, 83, 1, 0, 84, 84, 1, 0, 85, 85, 1, 0, 86, 86,
            1, 0, 87, 87, 1, 0, 89, 89, 1, 0, 90, 90, 1, 0, 92, 92, 1, 0, 93, 93, 1, 0, 98, 98, 1,
            0, 99, 99, 1, 0, 103, 103, 1, 0, 107, 107, 1, 0, 117, 117, 1, 0, 120, 120, 1, 0, 124,
            124, 1, 0, 125, 125, 1, 0, 126, 126, 1, 0, 127, 127, 1, 0, -128, -128, 1, 0, -127,
            -127, 1, 0, -126, -126, 1, 0, -125, -125, 1, 0, -124, -124, 1, 0, -123, -123, 1, 0,
            -122, -122, 1, 0, -121, -121, 1, 0, -120, -120, 1, 0, -119, -119, 1, 0, -118, -118, 1,
            0, -117, -117, 1, 0, -116, -116, 1, 0, -115, -115, 1, 0, -114, -114, 1, 0, -113, -113,
            1, 0, -112, -112, 1, 0, -111, -111, 1, 0, -108, -108, 1, 0, -107, -107, 1, 0, -106,
            -106, 1, 0, -105, -105, 1, 0, -103, -103, 1, 0, -99, -99, 1, 0, -98, -98, 1, 0, -97,
            -97, 1, 0, -96, -96, 1, 0, -95, -95, 1, 0, -94, -94, 1, 0, -93, -93, 1, 0, -92, -92, 1,
            0, -91, -91, 1, 0, -90, -90, 1, 0, -89, -89, 1, 0, -88, -88, 1, 0, -87, -87, 1, 0, -86,
            -86, 1, 0, -85, -85, 1, 0, -83, -83, 1, 0, -82, -82, 1, 0, -81, -81, 1, 0, -80, -80, 1,
            0, -79, -79, 1, 0, -76, -76, 1, 0, -75, -75, 1, 0, -74, -74, 1, 0, -73, -73, 1, 0, -48,
            -48, 1, 0, -25, -25, 1, 0, -23, -23, 1, 0, 3, 2, 10, 0, 4, 2, 10, 0, 5, 1, 1, 0, 6, 2,
            10, 0, 7, 2, 10, 0, 9, 1, 1, 0, 13, 0, 14, 0, 15, 23, 1, 0, 16, 0, 17, 0, 18, 0, 19, 0,
            20, 0, 21, 0, 22, 0, 39, 120, 1, 93, 1, 38, 1, 0, 58, 0, 68, 2, 10, 0, 69, 0, 70, 0,
            74, 0, 76, 0, 88, 0, 91, 2, 10, 0, 94, 1, 1, 0, 95, 23, 1, 0, 96, 96, 1, 0, 97, 96, 1,
            0, 102, 102, 1, 0, 104, 0, 105, 0, 106, 106, 1, 0, 107, 0, 108, 109, 1, 0, 109, 109, 1,
            0, 110, 111, 1, 0, 111, 111, 1, 0, 112, 112, 1, 0, 113, 113, 1, 0, 114, 114, 1, 0, 115,
            115, 1, 0, 116, 102, 1, 0, 118, 0, 119, 0, 121, 0, 122, 0, 123, 0, -120, 0, -116, 0,
            -110, -110, 1, 0, -109, 0, -104, 2, 10, 0, -103, 2, 10, 0, -102, 2, 10, 0, -101, 1, 1,
            0, -100, 12, 10, 0, -84, 2, 10, 0, -82, 0, -62, -62, 1, 0, -45, -45, 1, 0, -19, -19, 1,
            0, 0 };

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTIdty(T4CMAREngine _mrengine) throws SQLException, IOException {
        super((byte) 2);

        setMarshalingEngine(_mrengine);

        this.cliRIN = this.meg.conv.getClientCharSet();
        this.cliROUT = this.meg.conv.getClientCharSet();
        this.cliFlags = this.meg.types.getFlags();
    }

    void marshalTypeReps() throws SQLException, IOException {
        this.meg.marshalB1Array(this.typeReps10);
    }

    void marshal() throws SQLException, IOException {
        marshalTTCcode();

        this.meg.marshalUB2(this.cliRIN);
        this.meg.marshalUB2(this.cliROUT);
        this.meg.marshalUB1((short) this.cliFlags);

        if (this.meg.proSvrVer >= 6) {
            this.meg.marshalUB1((short) this.CTcap.length);
            this.meg.marshalB1Array(this.CTcap);
            this.meg.marshalUB1((short) this.RTcap.length);
            this.meg.marshalB1Array(this.RTcap);
        }

        marshalTypeReps();
    }

    void receive() throws SQLException, IOException {
        if (this.meg.unmarshalSB1() != 2) {
            DatabaseError.throwSqlException(401);
        }

        if (!validTypeReps()) {
            DatabaseError.throwSqlException(411);
        }

        setBasicTypes(this.meg.types);
    }

    boolean validTypeReps() throws SQLException, IOException {
        boolean inTypeBlock = false;
        int pairCount = 0;
        while (true) {
            byte b = (byte) this.meg.unmarshalUB1();

            if (!inTypeBlock) {
                if (b == 0) {
                    return true;
                }
                inTypeBlock = true;
                continue;
            }

            switch (pairCount) {
            case 0:
                if (b == 0)
                    inTypeBlock = false;
                else {
                    pairCount = 1;
                }
                break;
            case 1:
                pairCount = 0;
            }
        }
    }

    void setBasicTypes(T4CTypeRep types) throws SQLException {
        types.setRep((byte)0, (byte)0);
        types.setRep((byte)1, (byte)1);
        types.setRep((byte)2, (byte)1);
        types.setRep((byte)3, (byte)1);
        types.setRep((byte)4, (byte)1);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTIdty"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTIdty JD-Core Version: 0.6.0
 */