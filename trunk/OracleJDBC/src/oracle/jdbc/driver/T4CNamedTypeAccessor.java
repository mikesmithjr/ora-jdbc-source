package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CNamedTypeAccessor extends NamedTypeAccessor {
    static final int maxLength = 2147483647;
    final int[] meta = new int[1];
    T4CMAREngine mare;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    void processIndicator(int size) throws IOException, SQLException {
        if (((this.internalType == 1) && (this.describeType == 112))
                || ((this.internalType == 23) && (this.describeType == 113))) {
            this.mare.unmarshalUB2();
            this.mare.unmarshalUB2();
        } else if (this.mare.versionNumber < 9200) {
            this.mare.unmarshalSB2();

            if ((this.statement.sqlKind != 1) && (this.statement.sqlKind != 4)) {
                this.mare.unmarshalSB2();
            }
        } else if ((this.statement.sqlKind == 1) || (this.statement.sqlKind == 4)
                || (this.isDMLReturnedParam)) {
            this.mare.processIndicator(size <= 0, size);
        }
    }

    String getString(int currentRow) throws SQLException {
        String ret = super.getString(currentRow);

        if ((ret != null) && (this.definedColumnSize > 0)
                && (ret.length() > this.definedColumnSize)) {
            ret = ret.substring(0, this.definedColumnSize);
        }
        return ret;
    }

    T4CNamedTypeAccessor(OracleStatement stmt, String typeName, short form, int external_type,
            boolean forBind, T4CMAREngine _mare) throws SQLException {
        super(stmt, typeName, form, external_type, forBind);

        this.mare = _mare;
    }

    T4CNamedTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form, String typeName,
            int _definedColumnType, int _definedColumnSize, T4CMAREngine _mare) throws SQLException {
        super(stmt, max_len, nullable, flags, precision, scale, contflag, total_elems, form,
                typeName);

        this.mare = _mare;
        this.definedColumnType = _definedColumnType;
        this.definedColumnSize = _definedColumnSize;
    }

    boolean unmarshalOneRow() throws SQLException, IOException {
        if (this.isUseLess) {
            this.lastRowProcessed += 1;

            return false;
        }

        byte[] toid = this.mare.unmarshalDALC();

        byte[] oid = this.mare.unmarshalDALC();

        byte[] snp = this.mare.unmarshalDALC();

        int vsn = this.mare.unmarshalUB2();
        long imagel = this.mare.unmarshalUB4();
        int flags = this.mare.unmarshalUB2();

        byte[] byte_value = null;

        if (imagel > 0L)
            byte_value = this.mare.unmarshalCLR((int) imagel, this.meta);
        else {
            byte_value = new byte[0];
        }
        this.pickledBytes[this.lastRowProcessed] = byte_value;

        processIndicator(this.meta[0]);

        int tmpIndicatorOffset = this.indicatorIndex + this.lastRowProcessed;
        int tmpLengthOffset = this.lengthIndex + this.lastRowProcessed;

        if (this.meta[0] == 0) {
            this.rowSpaceIndicator[tmpIndicatorOffset] = -1;
            this.rowSpaceIndicator[tmpLengthOffset] = 0;
        } else {
            this.rowSpaceIndicator[tmpLengthOffset] = (short) this.meta[0];

            this.rowSpaceIndicator[tmpIndicatorOffset] = 0;
        }

        this.lastRowProcessed += 1;

        return false;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CNamedTypeAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CNamedTypeAccessor JD-Core Version: 0.6.0
 */