package oracle.jdbc.driver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Map;
import oracle.sql.BFILE;
import oracle.sql.Datum;

class BfileAccessor extends Accessor {
    static final int maxLength = 530;

    BfileAccessor(OracleStatement stmt, int max_len, short form, int external_type, boolean forBind)
            throws SQLException {
        init(stmt, 114, 114, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    BfileAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags, int precision,
            int scale, int contflag, int total_elems, short form) throws SQLException {
        init(stmt, 114, 114, form, false);
        initForDescribe(114, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 530;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    Object getObject(int currentRow) throws SQLException {
        return getBFILE(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getBFILE(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getBFILE(currentRow);
    }

    BFILE getBFILE(int currentRow) throws SQLException {
        BFILE result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int offset = this.columnIndex + this.byteLength * currentRow;
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, offset, data, 0, len);

            result = new BFILE(this.statement.connection, data);
        }

        return result;
    }

    InputStream getAsciiStream(int currentRow) throws SQLException {
        BFILE file = getBFILE(currentRow);

        if (file == null) {
            return null;
        }
        return file.asciiStreamValue();
    }

    Reader getCharacterStream(int currentRow) throws SQLException {
        BFILE file = getBFILE(currentRow);

        if (file == null) {
            return null;
        }
        return file.characterStreamValue();
    }

    InputStream getBinaryStream(int currentRow) throws SQLException {
        BFILE file = getBFILE(currentRow);

        if (file == null) {
            return null;
        }
        return file.getBinaryStream();
    }

    byte[] privateGetBytes(int currentRow) throws SQLException {
        return super.getBytes(currentRow);
    }

    byte[] getBytes(int currentRow) throws SQLException {
        BFILE lob = getBFILE(currentRow);

        if (lob == null) {
            return null;
        }
        InputStream r = lob.getBinaryStream();
        int size = 4096;
        int length = 0;
        ByteArrayOutputStream w = new ByteArrayOutputStream(size);
        byte[] buffer = new byte[size];
        try {
            while ((length = r.read(buffer)) != -1) {
                w.write(buffer, 0, length);
            }
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        } catch (IndexOutOfBoundsException x) {
            DatabaseError.throwSqlException(151);
        }

        return w.toByteArray();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.BfileAccessor JD-Core Version: 0.6.0
 */