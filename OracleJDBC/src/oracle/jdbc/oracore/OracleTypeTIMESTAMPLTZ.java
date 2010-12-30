package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMPLTZ;

public class OracleTypeTIMESTAMPLTZ extends OracleType implements Serializable {
    static final long serialVersionUID = 1615519855865602397L;
    int precision = 0;
    transient OracleConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    protected OracleTypeTIMESTAMPLTZ() {
    }

    public OracleTypeTIMESTAMPLTZ(OracleConnection _connection) {
        this.connection = _connection;
    }

    public int getTypeCode() {
        return -102;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeTIMESTAMPLTZ.parseTDSrec( tdsReader  = " + tdsReader
                                            + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.precision = tdsReader.readByte();
    }

    public int getScale() throws SQLException {
        return 0;
    }

    public int getPrecision() throws SQLException {
        return this.precision;
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeTIMESTAMPLTZ.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.precision = in.readByte();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeTIMESTAMPLTZ.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeByte(this.precision);
    }

    protected Object toObject(byte[] bytes, int otype, Map map) throws SQLException {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        switch (otype) {
        case 1:
            return new TIMESTAMPLTZ(bytes);
        case 2:
            return TIMESTAMPLTZ.toTimestamp(this.connection, bytes);
        case 3:
            return bytes;
        }

        DatabaseError.throwSqlException(59);

        return null;
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeTIMESTAMPLTZ.toDatum( value = "
                    + value + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = null;

        if (value != null) {
            try {
                if ((value instanceof TIMESTAMPLTZ))
                    datum = (TIMESTAMPLTZ) value;
                else if ((value instanceof byte[]))
                    datum = new TIMESTAMPLTZ((byte[]) value);
                else if ((value instanceof Timestamp))
                    datum = new TIMESTAMPLTZ(conn, (Timestamp) value);
                else if ((value instanceof DATE))
                    datum = new TIMESTAMPLTZ(conn, (DATE) value);
                else if ((value instanceof String))
                    datum = new TIMESTAMPLTZ(conn, (String) value);
                else if ((value instanceof Date))
                    datum = new TIMESTAMPLTZ(conn, (Date) value);
                else if ((value instanceof Time))
                    datum = new TIMESTAMPLTZ(conn, (Time) value);
                else {
                    DatabaseError.throwSqlException(59, value);
                }
            } catch (Exception ea) {
                DatabaseError.throwSqlException(59, value);
            }

        }

        return datum;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int otype, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeTIMESTAMPLTZ.unpickle80rec( context = " + context
                                            + ", format = " + format + ", otype = " + otype
                                            + ", map = " + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(90);

        return null;
    }

    protected Object unpickle81rec(UnpickleContext context, int format, int otype, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeTIMESTAMPLTZ.unpickle81rec( context = " + context
                                            + ", format = " + format + ", otype = " + otype
                                            + ", map = " + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(90);

        return null;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeTIMESTAMPLTZ"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeTIMESTAMPLTZ JD-Core Version: 0.6.0
 */