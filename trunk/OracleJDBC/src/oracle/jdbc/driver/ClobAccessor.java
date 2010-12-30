package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Map;
import oracle.sql.CLOB;
import oracle.sql.Datum;

class ClobAccessor extends Accessor {
    static final int maxLength = 4000;

    ClobAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, 112, 112, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    ClobAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        init(stmt, 112, 112, form, false);
        initForDescribe(112, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 4000;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    Object getObject(int currentRow) throws SQLException {
        return getCLOB(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getCLOB(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getCLOB(currentRow);
    }

    CLOB getCLOB(int currentRow) throws SQLException {
        CLOB result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int offset = this.columnIndex + this.byteLength * currentRow;
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, offset, data, 0, len);

            result = new CLOB(this.statement.connection, data, this.formOfUse);
        }

        return result;
    }

    InputStream getAsciiStream(int currentRow) throws SQLException {
        CLOB clob = getCLOB(currentRow);

        if (clob == null) {
            return null;
        }
        return clob.getAsciiStream();
    }

    Reader getCharacterStream(int currentRow) throws SQLException {
        CLOB clob = getCLOB(currentRow);

        if (clob == null) {
            return null;
        }
        return clob.getCharacterStream();
    }

    InputStream getBinaryStream(int currentRow) throws SQLException {
        Clob clob = getCLOB(currentRow);

        if (clob == null) {
            return null;
        }
        return clob.getAsciiStream();
    }

    String getString(int currentRow) throws SQLException {
        CLOB clob = getCLOB(currentRow);

        if (clob == null) {
            return null;
        }
        Reader r = clob.getCharacterStream();
        int size = clob.getBufferSize();
        int length = 0;
        StringWriter w = new StringWriter(size);
        char[] buffer = new char[size];
        try {
            while ((length = r.read(buffer)) != -1) {
                w.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        } catch (IndexOutOfBoundsException x) {
            DatabaseError.throwSqlException(151);
        }

        if (clob.isTemporary())
            this.statement.addToTempLobsToFree(clob);
        return w.toString();
    }

    byte[] privateGetBytes(int currentRow) throws SQLException {
        return super.getBytes(currentRow);
    }

    byte[] getBytes(int currentRow) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return null;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ClobAccessor JD-Core Version: 0.6.0
 */