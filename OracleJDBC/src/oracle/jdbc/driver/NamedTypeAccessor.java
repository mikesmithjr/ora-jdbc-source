package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.JAVA_STRUCT;
import oracle.sql.OPAQUE;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

class NamedTypeAccessor extends TypeAccessor {
    NamedTypeAccessor(OracleStatement stmt, String typeName, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 109, 109, form, forBind);
        initForDataAccess(external_type, 0, typeName);
    }

    NamedTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form, String typeName)
            throws SQLException {
        init(stmt, 109, 109, form, false);
        initForDescribe(109, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, typeName);

        initForDataAccess(0, max_len, typeName);
    }

    NamedTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form, String typeName,
            OracleType otype) throws SQLException {
        init(stmt, 109, 109, form, false);

        this.describeOtype = otype;

        initForDescribe(109, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, typeName);

        this.internalOtype = otype;

        initForDataAccess(0, max_len, typeName);
    }

    OracleType otypeFromName(String typeName) throws SQLException {
        if (!this.outBind) {
            return TypeDescriptor.getTypeDescriptor(typeName, this.statement.connection)
                    .getPickler();
        }
        if (this.externalType == 2003) {
            return ArrayDescriptor.createDescriptor(typeName, this.statement.connection)
                    .getOracleTypeCOLLECTION();
        }
        if (this.externalType == 2007) {
            return OpaqueDescriptor.createDescriptor(typeName, this.statement.connection)
                    .getPickler();
        }

        return StructDescriptor.createDescriptor(typeName, this.statement.connection)
                .getOracleTypeADT();
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        super.initForDataAccess(external_type, max_len, typeName);

        this.byteLength = this.statement.connection.namedTypeAccessorByteLen;
    }

    Object getObject(int currentRow) throws SQLException {
        return getObject(currentRow, this.statement.connection.getTypeMap());
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            if (this.externalType == 0) {
                Datum named_obj = getOracleObject(currentRow);

                if (named_obj == null) {
                    return null;
                }
                if ((named_obj instanceof STRUCT)) {
                    return ((STRUCT) named_obj).toJdbc(map);
                }
                if ((named_obj instanceof OPAQUE)) {
                    return ((OPAQUE) named_obj).toJdbc(map);
                }
                return named_obj.toJdbc();
            }

            switch (this.externalType) {
            case 2008:
                map = null;
            case 2000:
            case 2002:
            case 2003:
            case 2007:
                Datum named_obj = getOracleObject(currentRow);

                if (named_obj == null) {
                    return null;
                }
                if ((named_obj instanceof STRUCT)) {
                    return ((STRUCT) named_obj).toJdbc(map);
                }
                return named_obj.toJdbc();
            case 2001:
            case 2004:
            case 2005:
            case 2006:
            }

            DatabaseError.throwSqlException(4);

            return null;
        }

        return null;
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        Datum result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            byte[] data = pickledBytes(currentRow);

            if ((data == null) || (data.length == 0)) {
                return null;
            }
            OracleConnection conn = this.statement.connection;
            OracleTypeADT otype = (OracleTypeADT) this.internalOtype;
            TypeDescriptor desc = TypeDescriptor.getTypeDescriptor(otype.getFullName(), conn, data,
                                                                   0L);

            switch (desc.getTypeCode()) {
            case 2003:
                result = new ARRAY((ArrayDescriptor) desc, data, conn);

                break;
            case 2002:
                result = new STRUCT((StructDescriptor) desc, data, conn);

                break;
            case 2007:
                result = new OPAQUE((OpaqueDescriptor) desc, data, conn);

                break;
            case 2008:
                result = new JAVA_STRUCT((StructDescriptor) desc, data, conn);

                break;
            case 2004:
            case 2005:
            case 2006:
            default:
                DatabaseError.throwSqlException(1);
            }

        }

        return result;
    }

    ARRAY getARRAY(int currentRow) throws SQLException {
        return (ARRAY) getOracleObject(currentRow);
    }

    STRUCT getSTRUCT(int currentRow) throws SQLException {
        return (STRUCT) getOracleObject(currentRow);
    }

    OPAQUE getOPAQUE(int currentRow) throws SQLException {
        return (OPAQUE) getOracleObject(currentRow);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.NamedTypeAccessor JD-Core Version: 0.6.0
 */