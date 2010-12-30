package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.sql.Datum;
import oracle.sql.REF;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

class RefTypeAccessor extends TypeAccessor {
    RefTypeAccessor(OracleStatement stmt, String typeName, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 111, 111, form, forBind);
        initForDataAccess(external_type, 0, typeName);
    }

    RefTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, String typeName)
            throws SQLException {
        init(stmt, 111, 111, form, false);
        initForDescribe(111, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, typeName);

        initForDataAccess(0, max_len, typeName);
    }

    RefTypeAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form, String typeName, OracleType otype)
            throws SQLException {
        init(stmt, 111, 111, form, false);

        this.describeOtype = otype;

        initForDescribe(111, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, typeName);

        this.internalOtype = otype;

        initForDataAccess(0, max_len, typeName);
    }

    OracleType otypeFromName(String typeName) throws SQLException {
        if (!this.outBind) {
            return TypeDescriptor.getTypeDescriptor(typeName, this.statement.connection)
                    .getPickler();
        }

        return StructDescriptor.createDescriptor(typeName, this.statement.connection)
                .getOracleTypeADT();
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        super.initForDataAccess(external_type, max_len, typeName);

        this.byteLength = this.statement.connection.refTypeAccessorByteLen;
    }

    REF getREF(int currentRow) throws SQLException {
        REF result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            byte[] data = pickledBytes(currentRow);
            OracleTypeADT otype = (OracleTypeADT) this.internalOtype;

            result = new REF(otype.getFullName(), this.statement.connection, data);
        }

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        return getREF(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getREF(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getREF(currentRow);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RefTypeAccessor JD-Core Version: 0.6.0
 */