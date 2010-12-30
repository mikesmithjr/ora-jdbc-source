package oracle.jdbc.driver;

import java.sql.ResultSet;
import java.sql.SQLException;

class ResultSetAccessor extends Accessor {
    static final int maxLength = 16;

    ResultSetAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 102, 116, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    ResultSetAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 102, 116, form, false);
        initForDescribe(102, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 16;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    ResultSet getCursor(int currentRow) throws SQLException {
        byte[] bytes = getBytes(currentRow);
        OracleStatement newstmt = this.statement.connection
                .RefCursorBytesToStatement(bytes, this.statement);

        newstmt.doDescribe(false);
        newstmt.prepareAccessors();

        OracleResultSetImpl rset = new OracleResultSetImpl(newstmt.connection, newstmt);

        rset.close_statement_on_close = true;
        newstmt.currentResultSet = rset;

        return rset;
    }

    Object getObject(int currentRow) throws SQLException {
        return getCursor(currentRow);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ResultSetAccessor JD-Core Version: 0.6.0
 */