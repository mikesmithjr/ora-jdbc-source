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
import oracle.sql.TIMESTAMPTZ;

public class OracleTypeTIMESTAMPTZ extends OracleType implements Serializable {
    static final long serialVersionUID = 5643686037837085645L;
    int precision = 0;
    transient OracleConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    protected OracleTypeTIMESTAMPTZ() {
    }

    public OracleTypeTIMESTAMPTZ(OracleConnection _connection) {
        this.connection = _connection;
    }

    public int getTypeCode() {
        return -101;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeTIMESTAMPTZ.parseTDSrec( tdsReader  = " + tdsReader
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
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeTIMESTAMPTZ.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.precision = in.readByte();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeTIMESTAMPTZ.writeObject()", this);

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
            return new TIMESTAMPTZ(bytes);
        case 2:
            return TIMESTAMPTZ.toTimestamp(this.connection, bytes);
        case 3:
            return bytes;
        }

        DatabaseError.throwSqlException(59);

        return null;
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeTIMESTAMPTZ.toDatum( value = " + value
                    + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum datum = null;

        if (value != null) {
            try {
                if ((value instanceof TIMESTAMPTZ))
                    datum = (TIMESTAMPTZ) value;
                else if ((value instanceof byte[]))
                    datum = new TIMESTAMPTZ((byte[]) value);
                else if ((value instanceof Timestamp))
                    datum = new TIMESTAMPTZ(conn, (Timestamp) value);
                else if ((value instanceof DATE))
                    datum = new TIMESTAMPTZ(conn, (DATE) value);
                else if ((value instanceof String))
                    datum = new TIMESTAMPTZ(conn, (String) value);
                else if ((value instanceof Date))
                    datum = new TIMESTAMPTZ(conn, (Date) value);
                else if ((value instanceof Time))
                    datum = new TIMESTAMPTZ(conn, (Time) value);
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
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeTIMESTAMPTZ.unpickle80rec( context = "
                    + context + ", format = " + format + ", otype = " + otype + ", map = " + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(90);

        return null;
    }

    protected Object unpickle81rec(UnpickleContext context, int format, int otype, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeTIMESTAMPTZ.unpickle81rec( context = "
                    + context + ", format = " + format + ", otype = " + otype + ", map = " + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(90);

        return null;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeTIMESTAMPTZ"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeTIMESTAMPTZ JD-Core Version: 0.6.0
 */