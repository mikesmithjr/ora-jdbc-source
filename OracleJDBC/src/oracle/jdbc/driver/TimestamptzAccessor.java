package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TIMEZONETAB;
import oracle.sql.ZONEIDMAP;

class TimestamptzAccessor extends DateTimeCommonAccessor {
    static final int maxLength = 13;
    static int OFFSET_HOUR = 20;
    static int OFFSET_MINUTE = 60;

    static byte REGIONIDBIT = -128;

    TimestamptzAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 181, 181, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    TimestamptzAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 181, 181, form, false);
        initForDescribe(181, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 13;

        if ((max_len > 0) && (max_len < this.internalTypeMaxLength)) {
            this.internalTypeMaxLength = max_len;
        }
        this.byteLength = this.internalTypeMaxLength;
    }

    String getString(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        int off = this.columnIndex + this.byteLength * currentRow;

        TimeZone zone = this.statement.getDefaultTimeZone();
        Calendar cal = Calendar.getInstance(zone);

        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        cal.set(1, year);
        cal.set(2, oracleMonth(off));
        cal.set(5, oracleDay(off));
        cal.set(11, oracleHour(off));
        cal.set(12, oracleMin(off));
        cal.set(13, oracleSec(off));
        cal.set(14, 0);
        String regname;
        if ((oracleTZ1(off) & REGIONIDBIT) != 0) {
            int regionID = getHighOrderbits(oracleTZ1(off));
            regionID += getLowOrderbits(oracleTZ2(off));

            if (TIMEZONETAB.checkID(regionID)) {
                TIMEZONETAB.updateTable(this.statement.connection, regionID);
            }
            int offset = TIMEZONETAB.getOffset(cal, regionID);

            cal.add(10, offset / 3600000);
            cal.add(12, offset % 3600000 / 60000);

            regname = new String(ZONEIDMAP.getRegion(regionID));
        } else {
            cal.add(10, oracleTZ1(off) - OFFSET_HOUR);
            cal.add(12, oracleTZ2(off) - OFFSET_MINUTE);

            int off_hour = oracleTZ1(off) - OFFSET_HOUR;
            int off_minute = oracleTZ2(off) - OFFSET_MINUTE;

            regname = new String(off_hour + ":" + off_minute);
        }

        year = cal.get(1);

        int month = cal.get(2) + 1;
        int date = cal.get(5);
        int hour = cal.get(11);
        int min = cal.get(12);
        int sec = cal.get(13);
        int nanos = oracleNanos(off);

        return year + "-" + month + "-" + date + " " + hour + "." + min + "." + sec + "." + nanos
                + " " + regname;
    }

    java.sql.Date getDate(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        int off = this.columnIndex + this.byteLength * currentRow;

        TimeZone zone = this.statement.getDefaultTimeZone();
        Calendar cal = Calendar.getInstance(zone);

        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        cal.set(1, year);
        cal.set(2, oracleMonth(off));
        cal.set(5, oracleDay(off));
        cal.set(11, oracleHour(off));
        cal.set(12, oracleMin(off));
        cal.set(13, oracleSec(off));
        cal.set(14, 0);

        if ((oracleTZ1(off) & REGIONIDBIT) != 0) {
            int regionID = getHighOrderbits(oracleTZ1(off));
            regionID += getLowOrderbits(oracleTZ2(off));

            if (TIMEZONETAB.checkID(regionID)) {
                TIMEZONETAB.updateTable(this.statement.connection, regionID);
            }
            int offset = TIMEZONETAB.getOffset(cal, regionID);

            cal.add(10, offset / 3600000);
            cal.add(12, offset % 3600000 / 60000);
        } else {
            cal.add(10, oracleTZ1(off) - OFFSET_HOUR);
            cal.add(12, oracleTZ2(off) - OFFSET_MINUTE);
        }

        long millis = cal.getTime().getTime();

        return new java.sql.Date(millis);
    }

    Time getTime(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        int off = this.columnIndex + this.byteLength * currentRow;

        TimeZone zone = this.statement.getDefaultTimeZone();
        Calendar cal = Calendar.getInstance(zone);

        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        cal.set(1, year);
        cal.set(2, oracleMonth(off));
        cal.set(5, oracleDay(off));
        cal.set(11, oracleHour(off));
        cal.set(12, oracleMin(off));
        cal.set(13, oracleSec(off));
        cal.set(14, 0);

        if ((oracleTZ1(off) & REGIONIDBIT) != 0) {
            int regionID = getHighOrderbits(oracleTZ1(off));
            regionID += getLowOrderbits(oracleTZ2(off));

            if (TIMEZONETAB.checkID(regionID)) {
                TIMEZONETAB.updateTable(this.statement.connection, regionID);
            }
            int offset = TIMEZONETAB.getOffset(cal, regionID);

            cal.add(10, offset / 3600000);
            cal.add(12, offset % 3600000 / 60000);
        } else {
            cal.add(10, oracleTZ1(off) - OFFSET_HOUR);
            cal.add(12, oracleTZ2(off) - OFFSET_MINUTE);
        }

        long millis = cal.getTime().getTime();

        return new Time(millis);
    }

    Timestamp getTimestamp(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }
        int off = this.columnIndex + this.byteLength * currentRow;

        TimeZone zone = this.statement.getDefaultTimeZone();
        Calendar cal = Calendar.getInstance(zone);

        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        cal.set(1, year);
        cal.set(2, oracleMonth(off));
        cal.set(5, oracleDay(off));
        cal.set(11, oracleHour(off));
        cal.set(12, oracleMin(off));
        cal.set(13, oracleSec(off));
        cal.set(14, 0);

        if ((oracleTZ1(off) & REGIONIDBIT) != 0) {
            int regionID = getHighOrderbits(oracleTZ1(off));
            regionID += getLowOrderbits(oracleTZ2(off));

            if (TIMEZONETAB.checkID(regionID)) {
                TIMEZONETAB.updateTable(this.statement.connection, regionID);
            }
            int offset = TIMEZONETAB.getOffset(cal, regionID);

            cal.add(10, offset / 3600000);
            cal.add(12, offset % 3600000 / 60000);
        } else {
            cal.add(10, oracleTZ1(off) - OFFSET_HOUR);
            cal.add(12, oracleTZ2(off) - OFFSET_MINUTE);
        }

        long millis = cal.getTime().getTime();

        Timestamp result = new Timestamp(millis);

        int nanos = oracleNanos(off);

        result.setNanos(nanos);

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        return getTIMESTAMPTZ(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getTIMESTAMPTZ(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getTIMESTAMPTZ(currentRow);
    }

    TIMESTAMPTZ getTIMESTAMPTZ(int currentRow) throws SQLException {
        TIMESTAMPTZ result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int off = this.columnIndex + this.byteLength * currentRow;
            byte[] data = new byte[13];

            System.arraycopy(this.rowSpaceByte, off, data, 0, 13);

            result = new TIMESTAMPTZ(data);
        }

        return result;
    }

    static int setHighOrderbits(int ID) {
        return (ID & 0x1FC0) >> 6;
    }

    static int setLowOrderbits(int ID) {
        return (ID & 0x3F) << 2;
    }

    static int getHighOrderbits(int ID) {
        return (ID & 0x7F) << 6;
    }

    static int getLowOrderbits(int ID) {
        return (ID & 0xFC) >> 2;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TimestamptzAccessor JD-Core Version: 0.6.0
 */