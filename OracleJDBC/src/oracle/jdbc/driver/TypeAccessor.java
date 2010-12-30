package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.oracore.OracleType;

abstract class TypeAccessor extends Accessor {
    byte[][] pickledBytes;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    abstract OracleType otypeFromName(String paramString) throws SQLException;

    void initForDescribe(int type, int max_length, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, String typeName)
            throws SQLException {
        this.describeTypeName = typeName;

        initForDescribe(type, max_length, nullable, precision, scale, flags, contflag, total_elems,
                        form);
    }

    void setOffsets(int nrows) {
        if (!this.outBind) {
            this.columnIndex = this.statement.defineByteSubRange;
            this.statement.defineByteSubRange = (this.columnIndex + nrows * this.byteLength);
        }

        if ((this.pickledBytes == null) || (this.pickledBytes.length < nrows))
            this.pickledBytes = new byte[nrows][];
    }

    byte[] pickledBytes(int currentRow) {
        return this.pickledBytes[currentRow];
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 0;
        this.internalTypeName = typeName;
    }

    void initMetadata() throws SQLException {
        if ((this.describeOtype == null) && (this.describeTypeName != null)) {
            this.describeOtype = otypeFromName(this.describeTypeName);
        }
        if ((this.internalOtype == null) && (this.internalTypeName != null))
            this.internalOtype = otypeFromName(this.internalTypeName);
    }

    byte[] getBytes(int currentRow) throws SQLException {
        byte[] result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            byte[] b = pickledBytes(currentRow);
            int len = b.length;

            result = new byte[len];

            System.arraycopy(b, 0, result, 0, len);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "TypeAccessor.getBytes(columnIndex="
                    + this.columnIndex + ") => " + result + " "
                    + (result == null ? 0 : result.length), this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.TypeAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TypeAccessor JD-Core Version: 0.6.0
 */