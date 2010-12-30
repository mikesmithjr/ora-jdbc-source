package oracle.sql;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;

public class ROWID extends Datum {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public ROWID() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.ROWID(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public ROWID(byte[] raw_bytes) {
        super(raw_bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.ROWID( raw_bytes=" + raw_bytes
                    + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.toJdbc(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.isConvertibleTo( jClass=" + jClass
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        String class_name = jClass.getName();

        return class_name.compareTo("java.lang.String") == 0;
    }

    public String stringValue() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.stringValue()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] array = null;

        array = getBytes();

        String ret = new String(array, 0, 0, array.length);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.stringValue: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ROWID.makeJdbcArray( arraySize=" + arraySize
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new byte[arraySize][];
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.ROWID"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.ROWID JD-Core Version: 0.6.0
 */