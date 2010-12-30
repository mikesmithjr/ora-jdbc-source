package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCHAR;
import oracle.jdbc.oracore.OracleTypeFLOAT;
import oracle.jdbc.oracore.OracleTypeNUMBER;
import oracle.jdbc.oracore.OracleTypeRAW;
import oracle.jdbc.oracore.OracleTypeREF;
import oracle.sql.StructDescriptor;

public class StructMetaData implements oracle.jdbc.internal.StructMetaData {
    StructDescriptor descriptor;
    OracleTypeADT otype;
    OracleType[] types;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    public StructMetaData(StructDescriptor desc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.StructMetaData(desc=" + desc
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (desc == null) {
            DatabaseError.throwSqlException(1, "illegal operation: descriptor is null");
        }

        this.descriptor = desc;
        this.otype = desc.getOracleTypeADT();
        this.types = this.otype.getAttrTypes();
    }

    public int getColumnCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnCount()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.types.length;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isAutoIncrement(column="
                    + column + "): return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean isSearchable(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isSearchable(column=" + column
                    + "): TODO return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean isCurrency(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isCurrency(column=" + column
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return ((this.types[idx] instanceof OracleTypeNUMBER))
                || ((this.types[idx] instanceof OracleTypeFLOAT));
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isCaseSensitive(column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return this.types[idx] instanceof OracleTypeCHAR;
    }

    public int isNullable(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isNullable(column=" + column
                    + "): return: " + 1, this);

            OracleLog.recursiveTrace = false;
        }

        return 1;
    }

    public boolean isSigned(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isSigned(column=" + column
                    + "): return: true", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnDisplaySize(column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        if ((this.types[idx] instanceof OracleTypeCHAR))
            return ((OracleTypeCHAR) this.types[idx]).getLength();
        if ((this.types[idx] instanceof OracleTypeRAW)) {
            return ((OracleTypeRAW) this.types[idx]).getLength();
        }
        return 0;
    }

    public String getColumnLabel(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnLabel(column=" + column
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return getColumnName(column);
    }

    public String getColumnName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnName(column=" + column
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return this.otype.getAttributeName(column);
    }

    public String getSchemaName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getSchemaName(column=" + column
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        if ((this.types[idx] instanceof OracleTypeADT)) {
            return ((OracleTypeADT) this.types[idx]).getSchemaName();
        }
        return "";
    }

    public int getPrecision(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getPrecision(column=" + column
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return this.types[idx].getPrecision();
    }

    public int getScale(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "StructMetaData.getScale(column=" + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return this.types[idx].getScale();
    }

    public String getTableName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.getTableName(column=" + column
                    + "): return: null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public String getCatalogName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.getCatalogName(column=" + column
                    + "): return: null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public int getColumnType(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnType(column=" + column
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return this.types[idx].getTypeCode();
    }

    public String getColumnTypeName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnTypeName(column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int type = getColumnType(column);
        int idx = getValidColumnIndex(column);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINEST, "StructMetaData.getColumnTypeName(column="
                    + column + "): " + "type=" + type, this);

            OracleLog.recursiveTrace = false;
        }

        switch (type) {
        case 12:
            return "VARCHAR";
        case 1:
            return "CHAR";
        case -2:
            return "RAW";
        case 6:
            return "FLOAT";
        case 2:
            return "NUMBER";
        case 8:
            return "DOUBLE";
        case 3:
            return "DECIMAL";
        case 100:
            return "BINARY_FLOAT";
        case 101:
            return "BINARY_DOUBLE";
        case 91:
            return "DATE";
        case -104:
            return "INTERVALDS";
        case -103:
            return "INTERVALYM";
        case 93:
            return "TIMESTAMP";
        case -101:
            return "TIMESTAMPTZ";
        case -102:
            return "TIMESTAMPLTZ";
        case 2004:
            return "BLOB";
        case 2005:
            return "CLOB";
        case -13:
            return "BFILE";
        case 2002:
        case 2003:
        case 2007:
        case 2008:
            return ((OracleTypeADT) this.types[idx]).getFullName();
        case 2006:
            return "REF " + ((OracleTypeREF) this.types[idx]).getFullName();
        case 1111:
        }

        return null;
    }

    public boolean isReadOnly(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.isReadOnly(column=" + column
                    + "): return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean isWritable(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.isWritable(column=" + column
                    + "): return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.isDefinitelyWritable(column="
                    + column + "): return: false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public String getColumnClassName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getColumnClassName(column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int type = getColumnType(column);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINEST, "StructMetaData.getColumnClassName(column="
                    + column + "): " + "type=" + type, this);

            OracleLog.recursiveTrace = false;
        }

        switch (type) {
        case 1:
        case 12:
            return "java.lang.String";
        case -2:
            return "byte[]";
        case 2:
        case 3:
        case 6:
        case 8:
            return "java.math.BigDecimal";
        case 91:
            return "java.sql.Timestamp";
        case -103:
            return "oracle.sql.INTERVALYM";
        case -104:
            return "oracle.sql.INTERVALDS";
        case 93:
            return "oracle.sql.TIMESTAMP";
        case -101:
            return "oracle.sql.TIMESTAMPTZ";
        case -102:
            return "oracle.sql.TIMESTAMPLTZ";
        case 2004:
            return "oracle.sql.BLOB";
        case 2005:
            return "oracle.sql.CLOB";
        case -13:
            return "oracle.sql.BFILE";
        case 2002:
        case 2008:
            return "oracle.sql.STRUCT";
        case 2007:
            return "oracle.sql.OPAQUE";
        case 2003:
            return "oracle.sql.ARRAY";
        case 2006:
            return "oracle.sql.REF";
        case 1111:
        }

        return null;
    }

    public String getOracleColumnClassName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "StructMetaData.getOracleColumnClassName(column=" + column
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int type = getColumnType(column);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINEST,
                                       "StructMetaData.getOracleColumnClassName(column=" + column
                                               + "): " + "type=" + type, this);

            OracleLog.recursiveTrace = false;
        }

        switch (type) {
        case 1:
        case 12:
            return "CHAR";
        case -2:
            return "RAW";
        case 2:
        case 3:
        case 6:
        case 8:
            return "NUMBER";
        case 91:
            return "DATE";
        case -103:
            return "INTERVALYM";
        case -104:
            return "INTERVALDS";
        case 93:
            return "TIMESTAMP";
        case -101:
            return "TIMESTAMPTZ";
        case -102:
            return "TIMESTAMPLTZ";
        case 2004:
            return "BLOB";
        case 2005:
            return "CLOB";
        case -13:
            return "BFILE";
        case 2002:
            return "STRUCT";
        case 2008:
            return "JAVA_STRUCT";
        case 2007:
            return "OPAQUE";
        case 2003:
            return "ARRAY";
        case 2006:
            return "REF";
        case 1111:
        }

        return null;
    }

    public int getLocalColumnCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.getLocalColumnCount(): return: "
                    + this.descriptor.getLocalAttributeCount(), this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.getLocalAttributeCount();
    }

    public boolean isInherited(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.isInherited(): return: "
                    + (column <= getColumnCount() - getLocalColumnCount()), this);

            OracleLog.recursiveTrace = false;
        }

        return column <= getColumnCount() - getLocalColumnCount();
    }

    public String getAttributeJavaName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getAttributeJavaName(column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.getAttributeJavaName(column="
                    + column + "): return: " + this.descriptor.getAttributeJavaName(idx), this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.getAttributeJavaName(idx);
    }

    private int getValidColumnIndex(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.getValidColumnIndex(column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int index = column - 1;

        if ((index < 0) || (index >= this.types.length)) {
            DatabaseError.throwSqlException(3, "getValidColumnIndex");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "StructMetaData.getValidColumnIndex: return: "
                    + index, this);

            OracleLog.recursiveTrace = false;
        }

        return index;
    }

    public boolean isNCHAR(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "StructMetaData.isNCHAR(column=" + column + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        int idx = getValidColumnIndex(column);

        return this.types[idx].isNCHAR();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.StructMetaData"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.StructMetaData JD-Core Version: 0.6.0
 */