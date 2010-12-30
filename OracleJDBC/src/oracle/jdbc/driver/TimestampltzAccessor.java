package oracle.jdbc.driver;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import oracle.sql.Datum;
import oracle.sql.OffsetDST;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMEZONETAB;
import oracle.sql.ZONEIDMAP;

class TimestampltzAccessor extends DateTimeCommonAccessor {
    static int INV_ZONEID = -1;

    TimestampltzAccessor(OracleStatement stmt, int max_len, short form, int external_type,
            boolean forBind) throws SQLException {
        init(stmt, 231, 231, form, forBind);
        initForDataAccess(external_type, max_len, null);
    }

    TimestampltzAccessor(OracleStatement stmt, int max_len, boolean nullable, int flags,
            int precision, int scale, int contflag, int total_elems, short form)
            throws SQLException {
        init(stmt, 231, 231, form, false);
        initForDescribe(231, max_len, nullable, flags, precision, scale, contflag, total_elems,
                        form, null);

        initForDataAccess(0, max_len, null);
    }

    void initForDataAccess(int external_type, int max_len, String typeName) throws SQLException {
        if (external_type != 0) {
            this.externalType = external_type;
        }
        this.internalTypeMaxLength = 11;

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

        Calendar dbTzCal = this.statement.connection.getDbTzCalendar();

        String sessTzStr = this.statement.connection.getSessionTimeZone();

        if (sessTzStr == null) {
            throw new SQLException("Session Time Zone not set!");
        }

        TimeZone zone = this.statement.getDefaultTimeZone();

        zone.setID(sessTzStr);

        Calendar sessTzCal = Calendar.getInstance(zone);

        int off = this.columnIndex + this.byteLength * currentRow;
        int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        dbTzCal.set(1, year);
        dbTzCal.set(2, oracleMonth(off));
        dbTzCal.set(5, oracleDay(off));
        dbTzCal.set(11, oracleHour(off));
        dbTzCal.set(12, oracleMin(off));
        dbTzCal.set(13, oracleSec(off));
        dbTzCal.set(14, 0);

        TimeZoneAdjust(dbTzCal, sessTzCal);

        year = sessTzCal.get(1);

        int month = sessTzCal.get(2) + 1;
        int date = sessTzCal.get(5);
        int hour = sessTzCal.get(11);
        int minute = sessTzCal.get(12);
        int second = sessTzCal.get(13);
        int nanos = 0;

        if (len == 11) {
            nanos = oracleNanos(off);
        }

        return year + "-" + month + "-" + date + " " + hour + "." + minute + "." + second + "."
                + nanos;
    }

    java.sql.Date getDate(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }

        Calendar dbTzCal = this.statement.connection.getDbTzCalendar();

        String sessTzStr = this.statement.connection.getSessionTimeZone();

        if (sessTzStr == null) {
            throw new SQLException("Session Time Zone not set!");
        }

        TimeZone zone = this.statement.getDefaultTimeZone();

        zone.setID(sessTzStr);

        Calendar sessTzCal = Calendar.getInstance(zone);

        int off = this.columnIndex + this.byteLength * currentRow;
        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        dbTzCal.set(1, year);
        dbTzCal.set(2, oracleMonth(off));
        dbTzCal.set(5, oracleDay(off));
        dbTzCal.set(11, oracleHour(off));
        dbTzCal.set(12, oracleMin(off));
        dbTzCal.set(13, oracleSec(off));
        dbTzCal.set(14, 0);

        TimeZoneAdjust(dbTzCal, sessTzCal);

        long millis = sessTzCal.getTime().getTime();

        return new java.sql.Date(millis);
    }

    Time getTime(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }

        Calendar dbTzCal = this.statement.connection.getDbTzCalendar();

        String sessTzStr = this.statement.connection.getSessionTimeZone();

        if (sessTzStr == null) {
            throw new SQLException("Session Time Zone not set!");
        }

        TimeZone zone = this.statement.getDefaultTimeZone();

        zone.setID(sessTzStr);

        Calendar sessTzCal = Calendar.getInstance(zone);

        int off = this.columnIndex + this.byteLength * currentRow;
        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        dbTzCal.set(1, year);
        dbTzCal.set(2, oracleMonth(off));
        dbTzCal.set(5, oracleDay(off));
        dbTzCal.set(11, oracleHour(off));
        dbTzCal.set(12, oracleMin(off));
        dbTzCal.set(13, oracleSec(off));
        dbTzCal.set(14, 0);

        TimeZoneAdjust(dbTzCal, sessTzCal);

        long millis = sessTzCal.getTime().getTime();

        return new Time(millis);
    }

    Timestamp getTimestamp(int currentRow) throws SQLException {
        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] == -1) {
            return null;
        }

        Calendar dbTzCal = this.statement.connection.getDbTzCalendar();

        String sessTzStr = this.statement.connection.getSessionTimeZone();

        if (sessTzStr == null) {
            throw new SQLException("Session Time Zone not set!");
        }

        TimeZone zone = this.statement.getDefaultTimeZone();

        zone.setID(sessTzStr);

        Calendar sessTzCal = Calendar.getInstance(zone);

        int off = this.columnIndex + this.byteLength * currentRow;
        int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];

        int year = ((this.rowSpaceByte[(0 + off)] & 0xFF) - 100) * 100
                + (this.rowSpaceByte[(1 + off)] & 0xFF) - 100;

        dbTzCal.set(1, year);
        dbTzCal.set(2, oracleMonth(off));
        dbTzCal.set(5, oracleDay(off));
        dbTzCal.set(11, oracleHour(off));
        dbTzCal.set(12, oracleMin(off));
        dbTzCal.set(13, oracleSec(off));
        dbTzCal.set(14, 0);

        TimeZoneAdjust(dbTzCal, sessTzCal);

        long millis = sessTzCal.getTime().getTime();
        Timestamp result = new Timestamp(millis);

        if (len == 11) {
            result.setNanos(oracleNanos(off));
        }

        return result;
    }

    Object getObject(int currentRow) throws SQLException {
        return getTIMESTAMPLTZ(currentRow);
    }

    Datum getOracleObject(int currentRow) throws SQLException {
        return getTIMESTAMPLTZ(currentRow);
    }

    Object getObject(int currentRow, Map map) throws SQLException {
        return getTIMESTAMPLTZ(currentRow);
    }

    TIMESTAMPLTZ getTIMESTAMPLTZ(int currentRow) throws SQLException {
        TIMESTAMPLTZ result = null;

        if (this.rowSpaceIndicator == null) {
            DatabaseError.throwSqlException(21);
        }

        if (this.rowSpaceIndicator[(this.indicatorIndex + currentRow)] != -1) {
            int len = this.rowSpaceIndicator[(this.lengthIndex + currentRow)];
            int off = this.columnIndex + this.byteLength * currentRow;
            byte[] data = new byte[len];

            System.arraycopy(this.rowSpaceByte, off, data, 0, len);

            result = new TIMESTAMPLTZ(data);
        }

        return result;
    }

    void TimeZoneAdjust(Calendar cal1, Calendar cal2) throws SQLException {
        String cal1String = cal1.getTimeZone().getID();
        String cal2String = cal2.getTimeZone().getID();

        if (!cal2String.equals(cal1String)) {
            OffsetDST trans_db = new OffsetDST();

            byte dbolap = getZoneOffset(cal1, trans_db);

            int offset = trans_db.getOFFSET();

            cal1.add(11, -(offset / 3600000));
            cal1.add(12, -(offset % 3600000) / 60000);
            int Offset_gmt;
            if ((cal2String.equals("Custom"))
                    || ((cal2String.startsWith("GMT")) && (cal2String.length() > 3))) {
                Offset_gmt = cal2.getTimeZone().getRawOffset();
            } else {
                int regionID = ZONEIDMAP.getID(cal2String);

                if (regionID == INV_ZONEID) {
                    throw new SQLException("Timezone not supported");
                }
                if (TIMEZONETAB.checkID(regionID)) {
                    TIMEZONETAB.updateTable(this.statement.connection, regionID);
                }

                Offset_gmt = TIMEZONETAB.getOffset(cal1, regionID);
            }

            cal1.add(11, Offset_gmt / 3600000);
            cal1.add(12, Offset_gmt % 3600000 / 60000);
        }

        if (((cal2String.equals("Custom")) && (cal1String.equals("Custom")))
                || ((cal2String.startsWith("GMT")) && (cal2String.length() > 3)
                        && (cal1String.startsWith("GMT")) && (cal1String.length() > 3))) {
            int offset1 = cal1.getTimeZone().getRawOffset();
            int offset2 = cal2.getTimeZone().getRawOffset();
            int net_offset = 0;

            if (offset1 != offset2) {
                net_offset = offset1 - offset2;
                net_offset = net_offset > 0 ? net_offset : -net_offset;
            }

            if (offset1 > offset2) {
                net_offset = -net_offset;
            }
            cal1.add(11, net_offset / 3600000);
            cal1.add(12, net_offset % 3600000 / 60000);
        }

        int year = cal1.get(1);
        int month = cal1.get(2);
        int day = cal1.get(5);
        int hour = cal1.get(11);
        int minute = cal1.get(12);
        int second = cal1.get(13);
        int msecond = cal1.get(14);

        cal2.set(1, year);
        cal2.set(2, month);
        cal2.set(5, day);
        cal2.set(11, hour);
        cal2.set(12, minute);
        cal2.set(13, second);
        cal2.set(14, msecond);
    }

    byte getZoneOffset(Calendar cal, OffsetDST tempVar) throws SQLException {
        byte olap = 0;

        String timeZone = cal.getTimeZone().getID();

        if ((timeZone == "Custom") || ((timeZone.startsWith("GMT")) && (timeZone.length() > 3))) {
            tempVar.setOFFSET(cal.getTimeZone().getRawOffset());
        } else {
            int regionID = ZONEIDMAP.getID(timeZone);

            if (regionID == INV_ZONEID) {
                throw new SQLException("Timezone not supported");
            }
            if (TIMEZONETAB.checkID(regionID)) {
                TIMEZONETAB.updateTable(this.statement.connection, regionID);
            }

            olap = TIMEZONETAB.getLocalOffset(cal, regionID, tempVar);
        }

        return olap;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.TimestampltzAccessor JD-Core Version: 0.6.0
 */