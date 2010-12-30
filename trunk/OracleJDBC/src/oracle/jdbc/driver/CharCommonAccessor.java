package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;

abstract class CharCommonAccessor extends Accessor {
    int internalMaxLengthNewer;
    int internalMaxLengthOlder;
    static final int MAX_NB_CHAR_PLSQL = 32512;

    void setOffsets(int nrows) {
        this.columnIndex = this.statement.defineCharSubRange;
        this.statement.defineCharSubRange = (this.columnIndex + nrows * this.charLength);
    }

    void init(OracleStatement stmt, int internal_type, int database_type, int max_len, short form,
            int external_type, boolean forBind, int newer, int older) throws SQLException {
        if (forBind) {
            if (internal_type != 23) {
                internal_type = 1;
            }
            if ((max_len == -1) || (max_len < stmt.maxFieldSize)) {
                max_len = stmt.maxFieldSize;
            }
        }
        init(stmt, internal_type, database_type, form, forBind);

        if ((forBind) && (stmt.connection.defaultNChar)) {
            this.formOfUse = 2;
        }
        this.internalMaxLengthNewer = newer;
        this.internalMaxLengthOlder = older;

        initForDataAccess(external_type, max_len, null);
    }

    void init(OracleStatement stmt, int internal_type, int database_type, int max_len,
            boolean nullable, int flags, int precision, int scale, int contflag, int total_elems,
            short form, int newer, int older) throws SQLException {
        init(stmt, internal_type, database_type, form, false);
        initForDescribe(internal_type, max_len, nullable, flags, precision, scale, contflag,
                        total_elems, form, null);

        int max_field_size = stmt.maxFieldSize;

        if ((max_field_size != 0) && (max_field_size <= max_len)) {
            max_len = max_field_size;
        }
        this.internalMaxLengthNewer = newer;
        this.internalMaxLengthOlder = older;

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        if (this.statement.connection.getVersionNumber() >= 8000)
            this.internalTypeMaxLength = this.internalMaxLengthNewer;
        else {
            this.internalTypeMaxLength = this.internalMaxLengthOlder;
        }
        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.charLength = (this.internalTypeMaxLength + 1);
    }

    int getInt(int currentRow) throws SQLException {
        int result = 0;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = Integer.parseInt(getString(currentRow).trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    boolean getBoolean(int currentRow) throws SQLException {
        BigDecimal val = getBigDecimal(currentRow);

        return (val != null) && (val.signum() != 0);
    }

    short getShort(int currentRow) throws SQLException {
        short result = 0;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = Short.parseShort(getString(currentRow).trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    byte getByte(int currentRow) throws SQLException {
        byte result = 0;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = Byte.parseByte(getString(currentRow).trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    long getLong(int currentRow) throws SQLException {
        long result = 0L;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = Long.parseLong(getString(currentRow).trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    float getFloat(int currentRow) throws SQLException {
        float result = 0.0F;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = Float.parseFloat(getString(currentRow).trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    double getDouble(int currentRow) throws SQLException {
        double result = 0.0D;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = Double.parseDouble(getString(currentRow).trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    BigDecimal getBigDecimal(int currentRow) throws SQLException {
        BigDecimal result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                String ss = getString(currentRow);

                if (ss != null)
                    result = new BigDecimal(ss.trim());
            } catch (NumberFormatException e) {
                DatabaseError.throwSqlException(59);
            }

        }

        return result;
    }

    BigDecimal getBigDecimal(int currentRow, int scale) throws SQLException {
        BigDecimal bd = getBigDecimal(currentRow);

        if (bd != null) {
            bd.setScale(scale, 6);
        }
        return bd;
    }

    String getString(int currentRow) throws SQLException {
        String result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int lenoffset = this.columnIndex + this.charLength * currentRow;
            int len = this.rowSpaceChar[lenoffset] >> '\001';

            if (len > this.internalTypeMaxLength) {
                len = this.internalTypeMaxLength;
            }
            result = new String(this.rowSpaceChar, lenoffset + 1, len);
        }

        return result;
    }

    Date getDate(int currentRow) throws SQLException {
        Date result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            result = Date.valueOf(getString(currentRow).trim());
        }

        return result;
    }

    Time getTime(int currentRow) throws SQLException {
        Time result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            result = Time.valueOf(getString(currentRow).trim());
        }

        return result;
    }

    Timestamp getTimestamp(int currentRow) throws SQLException {
        Timestamp result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            result = Timestamp.valueOf(getString(currentRow).trim());
        }

        return result;
    }

    byte[] getBytes(int currentRow) throws SQLException {
        byte[] result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int lenoffset = this.columnIndex + this.charLength * currentRow;
            int len = this.rowSpaceChar[lenoffset] >> '\001';

            if (len > this.internalTypeMaxLength) {
                len = this.internalTypeMaxLength;
            }
            DBConversion dbconv = this.statement.connection.conversion;

            byte[] buf = new byte[len * 6];
            int nbytes = this.formOfUse == 2 ? dbconv.javaCharsToNCHARBytes(this.rowSpaceChar,
                                                                            lenoffset + 1, buf, 0,
                                                                            len) : dbconv
                    .javaCharsToCHARBytes(this.rowSpaceChar, lenoffset + 1, buf, 0, len);

            result = new byte[nbytes];

            System.arraycopy(buf, 0, result, 0, nbytes);
        }

        return result;
    }

    InputStream getAsciiStream(int currentRow) throws SQLException {
        InputStream result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int lenoffset = this.columnIndex + this.charLength * currentRow;
            int len = this.rowSpaceChar[lenoffset] >> '\001';

            if (len > this.internalTypeMaxLength) {
                len = this.internalTypeMaxLength;
            }
            PhysicalConnection conn = this.statement.connection;

            result = conn.conversion.CharsToStream(this.rowSpaceChar, lenoffset + 1, len, 10);
        }

        return result;
    }

    InputStream getUnicodeStream(int currentRow) throws SQLException {
        InputStream result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int lenoffset = this.columnIndex + this.charLength * currentRow;
            int len = this.rowSpaceChar[lenoffset] >> '\001';

            if (len > this.internalTypeMaxLength) {
                len = this.internalTypeMaxLength;
            }
            PhysicalConnection conn = this.statement.connection;

            result = conn.conversion.CharsToStream(this.rowSpaceChar, lenoffset + 1, len << 1, 11);
        }

        return result;
    }

    Reader getCharacterStream(int currentRow) throws SQLException {
        CharArrayReader result = null;
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int lenoffset = this.columnIndex + this.charLength * currentRow;
            int len = this.rowSpaceChar[lenoffset] >> '\001';

            if (len > this.internalTypeMaxLength) {
                len = this.internalTypeMaxLength;
            }
            result = new CharArrayReader(this.rowSpaceChar, lenoffset + 1, len);
        }
        return result;
    }

    InputStream getBinaryStream(int currentRow) throws SQLException {
        InputStream result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int lenoffset = this.columnIndex + this.charLength * currentRow;
            int len = this.rowSpaceChar[lenoffset] >> '\001';

            if (len > this.internalTypeMaxLength) {
                len = this.internalTypeMaxLength;
            }
            DBConversion dbconv = this.statement.connection.conversion;

            byte[] buf = new byte[len * 6];
            int nbytes = this.formOfUse == 2 ? dbconv.javaCharsToNCHARBytes(this.rowSpaceChar,
                                                                            lenoffset + 1, buf, 0,
                                                                            len) : dbconv
                    .javaCharsToCHARBytes(this.rowSpaceChar, lenoffset + 1, buf, 0, len);

            result = new ByteArrayInputStream(buf, 0, nbytes);
        }

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        return getString(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getString(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getCHAR(currentRow);
    }

    CHAR getCHAR(int currentRow) throws SQLException {
        byte[] bytes = getBytes(currentRow);

        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        CharacterSet cs;
        if (this.formOfUse == 2) {
            cs = this.statement.connection.conversion.getDriverNCharSetObj();
        } else {
            cs = this.statement.connection.conversion.getDriverCharSetObj();
        }

        return new CHAR(bytes, cs);
    }

    URL getURL(int currentRow) throws SQLException {
        URL result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            try {
                result = new URL(getString(currentRow));
            } catch (MalformedURLException exc) {
                DatabaseError.throwSqlException(136);
            }
        }

        return result;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.CharCommonAccessor JD-Core Version: 0.6.0
 */