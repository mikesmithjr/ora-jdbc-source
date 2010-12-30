package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.util.RepConversion;
import oracle.sql.Datum;
import oracle.sql.RAW;

class RawCommonAccessor extends Accessor {
    void init(OracleStatement stmt, int internal_type, int database_type, int max_len, short form,
            int external_type) throws SQLException {
        init(stmt, internal_type, database_type, form, false);
        initForDataAccess(external_type, max_len, null);
    }

    void init(OracleStatement stmt, int internal_type, int database_type, int max_len,
            boolean nullable, int flags, int precision, int scale, int contflag, int total_elems,
            short form) throws SQLException {
        init(stmt, internal_type, database_type, form, false);
        initForDescribe(internal_type, max_len, nullable, flags, precision, scale, contflag,
                        total_elems, form, null);

        int max_field_size = stmt.maxFieldSize;

        if ((max_field_size > 0) && ((max_len == 0) || (max_field_size < max_len))) {
            max_len = max_field_size;
        }
        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 2147483647;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength))
            this.internalTypeMaxLength = max_len;
    }

    String getString(int currentRow) throws SQLException {
        byte[] b_array = getBytes(currentRow);

        if (b_array == null) {
            return null;
        }
        int len = b_array.length;

        if (len == 0) {
            return null;
        }
        return RepConversion.bArray2String(b_array);
    }

    InputStream getAsciiStream(int currentRow) throws SQLException {
        byte[] b = getBytes(currentRow);

        if (b == null) {
            return null;
        }
        PhysicalConnection conn = this.statement.connection;

        return conn.conversion.ConvertStream(new ByteArrayInputStream(b), 2);
    }

    InputStream getUnicodeStream(int currentRow) throws SQLException {
        byte[] b = getBytes(currentRow);

        if (b == null) {
            return null;
        }
        PhysicalConnection conn = this.statement.connection;

        return conn.conversion.ConvertStream(new ByteArrayInputStream(b), 3);
    }

    Reader getCharacterStream(int currentRow) throws SQLException {
        byte[] b = getBytes(currentRow);

        if (b == null) {
            return null;
        }
        int rlen = b.length;

        char[] charBuf = new char[rlen << 1];

        int chars_read = DBConversion.RAWBytesToHexChars(b, rlen, charBuf);

        return new CharArrayReader(charBuf, 0, chars_read);
    }

    InputStream getBinaryStream(int currentRow) throws SQLException {
        byte[] b = getBytes(currentRow);

        if (b == null) {
            return null;
        }
        return new ByteArrayInputStream(b);
    }

    Object getObject(int currentRow) throws SQLException {
        return getBytes(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getBytes(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getRAW(currentRow);
    }

    RAW getRAW(int currentRow) throws SQLException {
        byte[] b = getBytes(currentRow);

        if (b == null) {
            return null;
        }
        return new RAW(b);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.RawCommonAccessor JD-Core Version: 0.6.0
 */