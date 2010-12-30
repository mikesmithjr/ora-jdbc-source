package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleType;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.JAVA_STRUCT;
import oracle.sql.OPAQUE;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

class T2CNamedTypeAccessor extends NamedTypeAccessor {
    int columnNumber = 0;

    T2CNamedTypeAccessor(OracleStatement stmt, String typeName, short form, int external_type,
            boolean forBind, int columnNumber) throws SQLException {
        super(stmt, typeName, form, external_type, forBind);

        this.columnNumber = columnNumber;
        this.isColumnNumberAware = true;
    }

    T2CNamedTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form, String typeName)
            throws SQLException {
        super(stmt, max_len, nullable, flags, precision, scale, contflag, total_elems, form,
                typeName);
    }

    T2CNamedTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form, String typeName,
            OracleType otype) throws SQLException {
        super(stmt, max_len, nullable, flags, precision, scale, contflag, total_elems, form,
                typeName, otype);
    }

    OracleType otypeFromName(String typeName) throws SQLException {
        if ("SYS.ANYDATA".equals(typeName)) {
            return StructDescriptor.createDescriptor(typeName, this.statement.connection)
                    .getPickler();
        }

        return super.otypeFromName(typeName);
    }

    Object getAnyDataEmbeddedObject(int currentRow) throws SQLException {
        byte[] bytes = getBytes(currentRow);

        if (bytes == null) {
            return null;
        }
        TypeDescriptor desc = null;

        int[] typecodeAndOffset = new int[2];

        String adt_name = getAllAnydataTypeInfo(this.statement.c_state, currentRow,
                                                this.columnNumber, typecodeAndOffset);

        int typecode = typecodeAndOffset[0];
        OracleConnection conn = ((OracleNamedType) this.internalOtype).getConnection();

        switch (typecode) {
        case 2003:
            desc = ArrayDescriptor.createDescriptor(adt_name, conn);

            break;
        case 2002:
        case 2008:
            desc = StructDescriptor.createDescriptor(adt_name, conn);

            break;
        case 2007:
            desc = OpaqueDescriptor.createDescriptor(adt_name, conn);

            break;
        case 2004:
        case 2005:
        case 2006:
        default:
            DatabaseError.throwSqlException(23);
        }

        Datum dat = null;

        int embDataPos = typecodeAndOffset[1];

        int sizeofEmbData = bytes.length - embDataPos;
        byte[] embBytes = new byte[sizeofEmbData];

        System.arraycopy(bytes, embDataPos, embBytes, 0, sizeofEmbData);

        switch (desc.getTypeCode()) {
        case 2003:
            dat = new ARRAY((ArrayDescriptor) desc, embBytes, this.statement.connection);

            break;
        case 2002:
            dat = new STRUCT((StructDescriptor) desc, embBytes, this.statement.connection);

            break;
        case 2007:
            dat = new OPAQUE((OpaqueDescriptor) desc, embBytes, this.statement.connection);

            break;
        case 2008:
            dat = new JAVA_STRUCT((StructDescriptor) desc, embBytes, this.statement.connection);

            break;
        case 2004:
        case 2005:
        case 2006:
        default:
            DatabaseError.throwSqlException(4);
        }

        return dat.toJdbc();
    }

    native String getAllAnydataTypeInfo(long paramLong, int paramInt1, int paramInt2,
            int[] paramArrayOfInt);
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T2CNamedTypeAccessor JD-Core Version: 0.6.0
 */