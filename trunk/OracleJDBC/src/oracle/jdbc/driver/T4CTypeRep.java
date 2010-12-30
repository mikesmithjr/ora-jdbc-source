package oracle.jdbc.driver;

import java.sql.SQLException;

class T4CTypeRep {
    short oVersion;
    static final byte PRO = 1;
    static final byte DTY = 2;
    static final byte RXH = 3;
    static final byte UDS = 4;
    static final byte OAC = 5;
    static final byte DSC = 1;
    static final byte DTYDTY = 20;
    static final byte DTYRXH8 = 21;
    static final byte DTYUDS8 = 22;
    static final byte DTYOAC8 = 23;
    static final byte NATIVE = 0;
    static final byte UNIVERSAL = 1;
    static final byte LSB = 2;
    static final byte MAXREP = 3;
    static final byte B1 = 0;
    static final byte B2 = 1;
    static final byte B4 = 2;
    static final byte B8 = 3;
    static final byte PTR = 4;
    static final byte MAXTYPE = 4;
    byte[] rep;
    final byte NUMREPS = 5;
    byte conversionFlags;
    boolean serverConversion;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    T4CTypeRep() {
        this.conversionFlags = 0;
        this.serverConversion = false;
        this.rep = new byte[5];

        this.rep[0] = 0;
        this.rep[1] = 1;
        this.rep[2] = 1;
        this.rep[3] = 1;
        this.rep[4] = 1;
    }

    void setRep(byte type, byte rep) throws SQLException {
        if ((type < 0) || (type > 4) || (rep > 3)) {
            DatabaseError.throwSqlException(407);
        }

        this.rep[type] = rep;
    }

    byte getRep(byte type) throws SQLException {
        if ((type < 0) || (type > 4)) {
            DatabaseError.throwSqlException(408);
        }

        return this.rep[type];
    }

    void setFlags(byte flags) {
        this.conversionFlags = flags;
    }

    byte getFlags() {
        return this.conversionFlags;
    }

    boolean isConvNeeded() {
        boolean result = (this.conversionFlags & 0x2) > 0;

        return result;
    }

    void setServerConversion(boolean conv) {
        this.serverConversion = conv;
    }

    boolean isServerConversion() {
        return this.serverConversion;
    }

    void setVersion(short version) {
        this.oVersion = version;
    }

    short getVersion() {
        return this.oVersion;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTypeRep"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CTypeRep JD-Core Version: 0.6.0
 */