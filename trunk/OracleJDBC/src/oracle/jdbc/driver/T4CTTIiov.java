package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CTTIiov extends T4CTTIMsg {
    T4C8TTIrxh rxh;
    T4CTTIrxd rxd;
    byte bindtype = 0;
    byte[] iovector;
    int bindcnt = 0;
    int inbinds = 0;
    int outbinds = 0;
    static final byte BV_IN_V = 32;
    static final byte BV_OUT_V = 16;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIiov(T4CMAREngine _mrengine, T4C8TTIrxh _rxh, T4CTTIrxd _rxd) throws SQLException,
            IOException {
        setMarshalingEngine(_mrengine);

        this.rxh = _rxh;
        this.rxd = _rxd;
    }

    void init() throws SQLException, IOException {
    }

    Accessor[] processRXD(Accessor[] outBindAccessors, int number_of_bind_positions,
            byte[] bindBytes, char[] bindChars, short[] bindIndicators, int bindIndicatorSubRange,
            DBConversion conversion, byte[] tmpBindsByteArray, byte[] ioVector,
            InputStream[][] parameterStream, byte[][][] parameterDatum,
            OracleTypeADT[][] parameterOtype, OracleStatement oracleStatement, byte[] ibtBindBytes,
            char[] ibtBindChars, short[] ibtBindIndicators) throws SQLException, IOException {
        if (ioVector != null) {
            for (int i = 0; i < ioVector.length; i++) {
                if (((ioVector[i] & 0x10) != 0)
                        && ((outBindAccessors == null) || (outBindAccessors.length <= i) || (outBindAccessors[i] == null))) {
                    int subRangeOffset = bindIndicatorSubRange + 3 + 10 * i;

                    int type = bindIndicators[(subRangeOffset + 0)] & 0xFFFF;

                    int external_type = type;

                    if (type == 9) {
                        type = 1;
                    }
                    Accessor acc = oracleStatement.allocateAccessor(type, type, i, 0, (short) 0, null,
                                                                    false);

                    acc.rowSpaceIndicator = null;

                    if (outBindAccessors == null) {
                        outBindAccessors = new Accessor[i + 1];
                        outBindAccessors[i] = acc;
                    } else if (outBindAccessors.length <= i) {
                        Accessor[] outBindAccessorsNew = new Accessor[i + 1];

                        outBindAccessorsNew[i] = acc;

                        for (int ii = 0; ii < outBindAccessors.length; ii++) {
                            if (outBindAccessors[ii] != null) {
                                outBindAccessorsNew[ii] = outBindAccessors[ii];
                            }
                        }
                        outBindAccessors = outBindAccessorsNew;
                    } else {
                        outBindAccessors[i] = acc;
                    }
                } else {
                    if (((ioVector[i] & 0x10) != 0) || (outBindAccessors == null)
                            || (i >= outBindAccessors.length) || (outBindAccessors[i] == null)) {
                        continue;
                    }

                    outBindAccessors[i].isUseLess = true;
                }

            }

        }

        return outBindAccessors;
    }

    void unmarshalV10() throws IOException, SQLException {
        this.rxh.unmarshalV10(this.rxd);

        this.bindcnt = this.rxh.numRqsts;

        this.iovector = new byte[this.bindcnt];

        for (int i = 0; i < this.bindcnt; i++) {
            if ((this.bindtype = this.meg.unmarshalSB1()) == 0) {
                DatabaseError.throwSqlException(401);
            }

            if ((this.bindtype & 0x20) > 0) {
                int tmp78_77 = i;
                byte[] tmp78_74 = this.iovector;
                tmp78_74[tmp78_77] = (byte) (tmp78_74[tmp78_77] | 0x20);
                this.inbinds += 1;
            }

            if ((this.bindtype & 0x10) <= 0)
                continue;
            int tmp110_109 = i;
            byte[] tmp110_106 = this.iovector;
            tmp110_106[tmp110_109] = (byte) (tmp110_106[tmp110_109] | 0x10);
            this.outbinds += 1;
        }
    }

    byte[] getIOVector() {
        return this.iovector;
    }

    boolean isIOVectorEmpty() {
        return this.iovector.length == 0;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIiov"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTTIiov JD-Core Version: 0.6.0
 */