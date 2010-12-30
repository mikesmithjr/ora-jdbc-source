package oracle.jdbc.driver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class LongAccessor extends CharCommonAccessor {
    OracleInputStream stream;
    int columnPosition = 0;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    LongAccessor(OracleStatement stmt, int column_pos, int max_len, short form, int external_type)
            throws SQLException {
        init(stmt, 8, 8, form, false);

        this.columnPosition = column_pos;

        initForDataAccess(external_type, max_len, null);
    }

    LongAccessor(OracleStatement stmt, int column_pos, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 8, 8, form, false);

        this.columnPosition = column_pos;

        initForDescribe(8, max_len, nullable, flags, precision, scale, contflag, total_elems, form,
                        null);

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
        this.isStream = true;
        this.isColumnNumberAware = true;
        this.internalTypeMaxLength = 2147483647;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }

        this.charLength = 0;

        this.stream = this.statement.connection.driverExtension
                .createInputStream(this.statement, this.columnPosition, this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.initForDataAccess accessor = "
                    + this + ", stream = " + this.stream, this);

            OracleLog.recursiveTrace = false;
        }
    }

    OracleInputStream initForNewRow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "initForNewRow accessor " + this
                    + ", old stream " + this.stream, this);

            OracleLog.recursiveTrace = false;
        }

        this.stream = this.statement.connection.driverExtension
                .createInputStream(this.statement, this.columnPosition, this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "initForNewRow accessor " + this
                    + ", new stream " + this.stream, this);

            OracleLog.recursiveTrace = false;
        }

        return this.stream;
    }

    void updateColumnNumber(int colNumber) {
        colNumber++;

        this.columnPosition = colNumber;

        if (this.stream != null)
            this.stream.columnIndex = colNumber;
    }

    byte[] getBytes(int currentRow) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getBytes("
                    + currentRow
                    + ") "
                    + "accessor = "
                    + this
                    + ", stream = "
                    + this.stream
                    + ", nullIndicator = "
                    + (this.rowSpaceIndicator == null ? "null" : new StringBuffer().append("")
                            .append(this.rowSpaceIndicator[(this.indicatorIndex + currentRow)])
                            .toString()), this);

            OracleLog.recursiveTrace = false;
        }

        byte[] result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            if (this.stream == null) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER,
                                               "stream is null in LongAccessor.getBytes accessor = "
                                                       + this, this);

                    OracleLog.recursiveTrace = false;
                }

            } else {
                if (this.stream.closed) {
                    DatabaseError.throwSqlException(27);
                }
                ByteArrayOutputStream outs = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                try {
                    int length;
                    while ((length = this.stream.read(buffer)) != -1) {
                        outs.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    DatabaseError.throwSqlException(e);
                }

                result = outs.toByteArray();
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getBytes(" + currentRow + ") "
                    + "accessor = " + this + " returns " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    String getString(int currentRow) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getString("
                    + currentRow
                    + ") "
                    + "accessor = "
                    + this
                    + ", stream = "
                    + this.stream
                    + ", nullIndicator = "
                    + (this.rowSpaceIndicator == null ? "null" : new StringBuffer().append("")
                            .append(this.rowSpaceIndicator[(this.indicatorIndex + currentRow)])
                            .toString()), this);

            OracleLog.recursiveTrace = false;
        }

        String result = null;

        byte[] b_array = getBytes(currentRow);

        if (b_array != null) {
            int len = Math.min(b_array.length, this.internalTypeMaxLength);

            if (len == 0) {
                result = "";
            } else if (this.formOfUse == 2) {
                result = this.statement.connection.conversion.NCharBytesToString(b_array, len);
            } else {
                result = this.statement.connection.conversion.CharBytesToString(b_array, len);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getString(" + currentRow + ") "
                    + "accessor = " + this + " returns " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    InputStream getAsciiStream(int currentRow) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getAsciiStream("
                    + currentRow
                    + ") "
                    + "accessor = "
                    + this
                    + ", stream = "
                    + this.stream
                    + ", nullIndicator = "
                    + (this.rowSpaceIndicator == null ? "null" : new StringBuffer().append("")
                            .append(this.rowSpaceIndicator[(this.indicatorIndex + currentRow)])
                            .toString()), this);

            OracleLog.recursiveTrace = false;
        }

        InputStream result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if ((this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1)
                && (this.stream != null)) {
            if (this.stream.closed) {
                DatabaseError.throwSqlException(27);
            }
            PhysicalConnection conn = this.statement.connection;

            result = conn.conversion.ConvertStream(this.stream, 0);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getAsciiStream(" + currentRow
                    + ") " + "accessor = " + this + " returns " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    InputStream getUnicodeStream(int currentRow) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getUnicodeStream("
                    + currentRow
                    + ") "
                    + "accessor = "
                    + this
                    + ", stream = "
                    + this.stream
                    + ", nullIndicator = "
                    + (this.rowSpaceIndicator == null ? "null" : new StringBuffer().append("")
                            .append(this.rowSpaceIndicator[(this.indicatorIndex + currentRow)])
                            .toString()), this);

            OracleLog.recursiveTrace = false;
        }

        InputStream result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if ((this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1)
                && (this.stream != null)) {
            if (this.stream.closed) {
                DatabaseError.throwSqlException(27);
            }
            PhysicalConnection conn = this.statement.connection;

            result = conn.conversion.ConvertStream(this.stream, 1);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getUnicodeStream(" + currentRow
                    + ") " + "accessor = " + this + " returns " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    Reader getCharacterStream(int currentRow) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getCharacterStream("
                    + currentRow
                    + ") "
                    + "accessor = "
                    + this
                    + ", stream = "
                    + this.stream
                    + ", nullIndicator = "
                    + (this.rowSpaceIndicator == null ? "null" : new StringBuffer().append("")
                            .append(this.rowSpaceIndicator[(this.indicatorIndex + currentRow)])
                            .toString()), this);

            OracleLog.recursiveTrace = false;
        }

        Reader result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if ((this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1)
                && (this.stream != null)) {
            if (this.stream.closed) {
                DatabaseError.throwSqlException(27);
            }
            PhysicalConnection conn = this.statement.connection;

            result = conn.conversion.ConvertCharacterStream(this.stream, 9, this.formOfUse);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getCharacterStream(" + currentRow
                    + ") " + "accessor = " + this + " returns " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    InputStream getBinaryStream(int currentRow) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getBinaryStream("
                    + currentRow
                    + ") "
                    + "accessor = "
                    + this
                    + ", stream = "
                    + this.stream
                    + ", nullIndicator = "
                    + (this.rowSpaceIndicator == null ? "null" : new StringBuffer().append("")
                            .append(this.rowSpaceIndicator[(this.indicatorIndex + currentRow)])
                            .toString()), this);

            OracleLog.recursiveTrace = false;
        }

        InputStream result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if ((this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1)
                && (this.stream != null)) {
            if (this.stream.closed) {
                DatabaseError.throwSqlException(27);
            }
            PhysicalConnection conn = this.statement.connection;

            result = conn.conversion.ConvertStream(this.stream, 6);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "LongAccessor.getBinaryStream(" + currentRow
                    + ") " + "accessor = " + this + " returns " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public String toString() {
        return "LongAccessor@" + Integer.toHexString(hashCode()) + "{columnPosition = "
                + this.columnPosition + "}";
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.LongAccessor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.LongAccessor JD-Core Version: 0.6.0
 */