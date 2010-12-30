package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;

public class OracleTypeINTERVAL extends OracleType implements Serializable {
    static final long serialVersionUID = 1394800182554224957L;
    final int LDIINTYEARMONTH = 7;
    final int LDIINTDAYSECOND = 10;

    final int SIZE_INTERVAL_YM = 5;
    final int SIZE_INTERVAL_DS = 11;

    byte typeId = 0;
    int scale = 0;
    int precision = 0;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    protected OracleTypeINTERVAL() {
    }

    public OracleTypeINTERVAL(OracleConnection connection) {
    }

    public int getTypeCode() {
        if (this.typeId == 7)
            return -103;
        if (this.typeId == 10) {
            return -104;
        }
        return 1111;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeINTERVAL.parseTDSrec( tdsReader  = "
                    + tdsReader + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.typeId = tdsReader.readByte();
        this.precision = tdsReader.readByte();
        this.scale = tdsReader.readByte();
    }

    public int getScale() throws SQLException {
        return this.scale;
    }

    public int getPrecision() throws SQLException {
        return this.precision;
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeINTERVAL.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.typeId = in.readByte();
        this.precision = in.readByte();
        this.scale = in.readByte();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeINTERVAL.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeByte(this.typeId);
        out.writeByte(this.precision);
        out.writeByte(this.scale);
    }

    protected Object toObject(byte[] bytes, int otype, Map map) throws SQLException {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        switch (otype) {
        case 1:
            if (bytes.length == 5)
                return new INTERVALYM(bytes);
            if (bytes.length != 11)
                break;
            return new INTERVALDS(bytes);
        case 2:
            if (bytes.length == 5)
                return INTERVALYM.toString(bytes);
            if (bytes.length != 11)
                break;
            return INTERVALDS.toString(bytes);
        case 3:
            return bytes;
        default:
            DatabaseError.throwSqlException(59);
        }

        return null;
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeINTERVAL.toDatum( value = " + value
                    + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum retVal = null;

        if (value != null) {
            if (((value instanceof INTERVALYM)) || ((value instanceof INTERVALDS))) {
                retVal = (Datum) value;
            } else if ((value instanceof String)) {
                try {
                    retVal = new INTERVALDS((String) value);
                } catch (StringIndexOutOfBoundsException ea) {
                    retVal = new INTERVALYM((String) value);
                }
            } else {
                DatabaseError.throwSqlException(59, value);
            }
        }

        return retVal;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int otype, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeINTERVAL.unpickle80rec( context = "
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
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeINTERVAL.unpickle81rec( context = "
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
                    .forName("oracle.jdbc.oracore.OracleTypeINTERVAL"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeINTERVAL JD-Core Version: 0.6.0
 */