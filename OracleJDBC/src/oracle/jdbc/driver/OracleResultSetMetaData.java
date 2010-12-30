package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public class OracleResultSetMetaData implements oracle.jdbc.internal.OracleResultSetMetaData {
    PhysicalConnection connection;
    OracleStatement statement;
    int m_beginColumnIndex;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleResultSetMetaData() {
    }

    public OracleResultSetMetaData(PhysicalConnection conn, OracleStatement stmt)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "OracleResultSetMetaData.OracleResultSetMetaData(conn, stmt)",
                         this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = conn;
        this.statement = stmt;

        stmt.describe();

        this.m_beginColumnIndex = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleResultSetMetaData.OracleResultSetMetaData:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    OracleResultSetMetaData(PhysicalConnection conn, OracleStatement stmt, int beginColumnIndex)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleResultSetMetaData.OracleResultSetMetaData(conn, stmt, ColIndex:"
                                               + beginColumnIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = conn;
        this.statement = stmt;

        stmt.describe();

        this.m_beginColumnIndex = beginColumnIndex;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleResultSetMetaData.OracleResultSetMetaData:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleResultSetMetaData(OracleResultSet rs) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleResultSetMetaData.OracleResultSetMetaData(rs)", this);

            OracleLog.recursiveTrace = false;
        }

        this.statement = ((OracleStatement) rs.getStatement());
        this.connection = ((PhysicalConnection) this.statement.getConnection());

        this.statement.describe();

        this.m_beginColumnIndex = rs.getFirstUserColumnIndex();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleResultSetMetaData.OracleResultSetMetaData:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public int getColumnCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Rset Column count: "
                    + (this.statement.getNumberOfColumns() - this.m_beginColumnIndex), this);

            OracleLog.recursiveTrace = false;
        }

        return this.statement.getNumberOfColumns() - this.m_beginColumnIndex;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    int getValidColumnIndex(int column) throws SQLException {
        int index = column + this.m_beginColumnIndex - 1;

        if ((index < 0) || (index >= this.statement.getNumberOfColumns())) {
            DatabaseError.throwSqlException(3, "getValidColumnIndex");
        }

        return index;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        int type = getColumnType(column);

        return (type == 1) || (type == 12) || (type == -1);
    }

    public boolean isSearchable(int column) throws SQLException {
        int type = getColumnType(column);

        return (type != -4) && (type != -1) && (type != 2004) && (type != 2005) && (type != -13)
                && (type != 2002) && (type != 2008) && (type != 2007) && (type != 2003)
                && (type != 2006) && (type != -10);
    }

    public boolean isCurrency(int column) throws SQLException {
        int l_type = getColumnType(column);

        return (l_type == 2) || (l_type == 6);
    }

    public int isNullable(int column) throws SQLException {
        int index = getValidColumnIndex(column);

        return getDescription()[index].nullable ? 1 : 0;
    }

    public boolean isSigned(int column) throws SQLException {
        return true;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        int index = getValidColumnIndex(column);

        return getDescription()[index].describeMaxLength;
    }

    public String getColumnLabel(int column) throws SQLException {
        return getColumnName(column);
    }

    public String getColumnName(int column) throws SQLException {
        int index = getValidColumnIndex(column);

        return this.statement.getDescriptionWithNames()[index].columnName;
    }

    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    public int getPrecision(int column) throws SQLException {
        int index = getValidColumnIndex(column);

        int type = getDescription()[index].describeType;

        switch (type) {
        case 112:
        case 113:
            return -1;
        case 8:
        case 24:
            return 2147483647;
        case 1:
        case 96:
            return getDescription()[index].describeMaxLength;
        }

        return getDescription()[index].precision;
    }

    public int getScale(int column) throws SQLException {
        int index = getValidColumnIndex(column);
        int _scale = getDescription()[index].scale;

        return (_scale == -127) && (this.statement.connection.j2ee13Compliant) ? 0 : _scale;
    }

    public String getTableName(int column) throws SQLException {
        return "";
    }

    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    public int getColumnType(int column) throws SQLException {
        int index = getValidColumnIndex(column);

        int type = getDescription()[index].describeType;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Rset Column Type:<" + type + ">", this);

            OracleLog.recursiveTrace = false;
        }

        switch (type) {
        case 96:
            return 1;
        case 1:
            return 12;
        case 8:
            return -1;
        case 2:
        case 6:
            if ((this.statement.connection.j2ee13Compliant)
                    && (getDescription()[index].precision != 0)
                    && (getDescription()[index].scale == -127)) {
                return 6;
            }
            return 2;
        case 100:
            return 100;
        case 101:
            return 101;
        case 23:
            return -3;
        case 24:
            return -4;
        case 104:
            return -8;
        case 102:
            return -10;
        case 12:
            return this.connection.v8Compatible ? 93 : 91;
        case 180:
            return 93;
        case 181:
            return -101;
        case 231:
            return -102;
        case 113:
            return 2004;
        case 112:
            return 2005;
        case 114:
            return -13;
        case 109:
            OracleNamedType ntypeObj = (OracleNamedType) getDescription()[index].describeOtype;

            TypeDescriptor descriptor = TypeDescriptor.getTypeDescriptor(ntypeObj.getFullName(),
                                                                         this.connection);

            if (descriptor != null) {
                return descriptor.getTypeCode();
            }
            DatabaseError.throwSqlException(60);

            return -1;
        case 111:
            return 2006;
        case 182:
            return -103;
        case 183:
            return -104;
        }

        return 1111;
    }

    public String getColumnTypeName(int column) throws SQLException {
        int type = getColumnType(column);

        switch (type) {
        case 1:
            return "CHAR";
        case 12:
            return "VARCHAR2";
        case -1:
            return "LONG";
        case -3:
            return "RAW";
        case -4:
            return "LONG RAW";
        case 2:
            return "NUMBER";
        case 6:
            return "FLOAT";
        case 100:
            return "BINARY_FLOAT";
        case 101:
            return "BINARY_DOUBLE";
        case 91:
            return "DATE";
        case 93:
            return this.connection.v8Compatible ? "DATE" : "TIMESTAMP";
        case -101:
            return "TIMESTAMPTZ";
        case -102:
            return "TIMESTAMPLTZ";
        case -8:
            return "ROWID";
        case -10:
            return "REFCURSOR";
        case 2004:
            return "BLOB";
        case 2005:
            return "CLOB";
        case -13:
            return "BFILE";
        case -103:
            return "INTERVALYM";
        case -104:
            return "INTERVALDS";
        case 2002:
        case 2003:
        case 2007:
        case 2008:
        {
            int index = getValidColumnIndex(column);
            OracleTypeADT otype = (OracleTypeADT) getDescription()[index].describeOtype;

            return otype.getFullName();
        }
        case 2006:
        {
            int index = getValidColumnIndex(column);
            OracleTypeADT otype = (OracleTypeADT) getDescription()[index].describeOtype;

            return otype.getFullName();
        }
        case 1111:
        }

        return null;
    }

    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    public boolean isWritable(int column) throws SQLException {
        return true;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    public String getColumnClassName(int column) throws SQLException {
        int index = getValidColumnIndex(column);

        int type = getDescription()[index].describeType;

        switch (type) {
        case 1:
        case 8:
        case 96:
        case 999:
            return "java.lang.String";
        case 2:
        case 6:
            if ((getDescription()[index].precision != 0) && (getDescription()[index].scale == -127)) {
                return "java.lang.Double";
            }
            return "java.math.BigDecimal";
        case 23:
        case 24:
            return "byte[]";
        case 12:
            return "java.sql.Timestamp";
        case 180:
            return "oracle.sql.TIMESTAMP";
        case 181:
            return "oracle.sql.TIMESTAMPTZ";
        case 231:
            return "oracle.sql.TIMESTAMPLTZ";
        case 182:
            return "oracle.sql.INTERVALYM";
        case 183:
            return "oracle.sql.INTERVALDS";
        case 104:
            return "oracle.sql.ROWID";
        case 113:
            return "oracle.sql.BLOB";
        case 112:
            return "oracle.sql.CLOB";
        case 114:
            return "oracle.sql.BFILE";
        case 102:
            return "OracleResultSet";
        case 109:
            switch (getColumnType(column)) {
            case 2003:
                return "oracle.sql.ARRAY";
            case 2007:
                return "oracle.sql.OPAQUE";
            case 2008:
            {
                OracleNamedType ntype = (OracleNamedType) getDescription()[index].describeOtype;

                Map map = this.connection.getJavaObjectTypeMap();

                if (map != null) {
                    Class c = (Class) map.get(ntype.getFullName());

                    if (c != null) {
                        return c.getName();
                    }
                }
                return StructDescriptor.getJavaObjectClassName(this.connection, ntype
                        .getSchemaName(), ntype.getSimpleName());
            }
            case 2002:
            {
                Map map = this.connection.getTypeMap();

                if (map != null) {
                    Class c = (Class) map
                            .get(((OracleNamedType) getDescription()[index].describeOtype)
                                    .getFullName());

                    if (c != null) {
                        return c.getName();
                    }
                }
                return "oracle.sql.STRUCT";
            }
            case 2004:
            case 2005:
            case 2006:
            }
            DatabaseError.throwSqlException(1);
        case 111:
            return "oracle.sql.REF";
        }

        return null;
    }

    public boolean isNCHAR(int columnIndex) throws SQLException {
        int index = getValidColumnIndex(columnIndex);

        return getDescription()[index].formOfUse == 2;
    }

    Accessor[] getDescription() throws SQLException {
        return this.statement.getDescription();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleResultSetMetaData"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleResultSetMetaData JD-Core Version: 0.6.0
 */