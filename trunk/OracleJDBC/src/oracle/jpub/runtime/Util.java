package oracle.jpub.runtime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;

public class Util {
    static short lastCsId = 870;
    static CharacterSet lastCS = CharacterSet.make(870);

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public static Object convertToObject(Datum d, int sqlType, Object f) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "Util.convertToObject (datum = " + d
                    + ", sqlType = " + sqlType + ", object = " + f + ")");

            OracleLog.recursiveTrace = false;
        }

        Object result = _convertToObject(d, sqlType, f);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "Util.convertToObject:return " + result);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public static Object _convertToObject(Datum d, int sqlType, Object f) throws SQLException {
        if (d == null) {
            return null;
        }
        if ((d instanceof STRUCT)) {
            if ((f instanceof ORADataFactory)) {
                return ((ORADataFactory) f).create(d, 2002);
            }

            return ((CustomDatumFactory) f).create(d, 2002);
        }

        if ((d instanceof REF)) {
            if ((f instanceof ORADataFactory)) {
                return ((ORADataFactory) f).create(d, 2006);
            }

            return ((CustomDatumFactory) f).create(d, 2006);
        }

        if ((d instanceof ARRAY)) {
            if ((f instanceof ORADataFactory)) {
                return ((ORADataFactory) f).create(d, 2003);
            }

            return ((CustomDatumFactory) f).create(d, 2003);
        }

        if ((d instanceof OPAQUE)) {
            if ((f instanceof ORADataFactory)) {
                return ((ORADataFactory) f).create(d, 2007);
            }

            return ((CustomDatumFactory) f).create(d, 2007);
        }

        if (f != null) {
            if ((f instanceof ORADataFactory)) {
                return ((ORADataFactory) f).create(d, sqlType);
            }

            return ((CustomDatumFactory) f).create(d, sqlType);
        }

        if ((d instanceof NUMBER)) {
            if ((sqlType == 2) || (sqlType == 3)) {
                return ((NUMBER) d).bigDecimalValue();
            }
            if ((sqlType == 8) || (sqlType == 6)) {
                return new Double(((NUMBER) d).doubleValue());
            }
            if ((sqlType == 4) || (sqlType == 5)) {
                return new Integer(((NUMBER) d).intValue());
            }
            if (sqlType == 7) {
                return new Float(((NUMBER) d).floatValue());
            }
            if (sqlType == 16) {
                return new Boolean(((NUMBER) d).booleanValue());
            }
            throw new SQLException("Unexpected java.sql.OracleTypes type: " + sqlType);
        }

        return d.toJdbc();
    }

    public static Datum convertToOracle(Object d, Connection c) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "Util.convertToOracle (datum = " + d
                    + ", connection - " + c + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum result = _convertToOracle(d, c);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.jpubLogger.log(Level.FINEST, "Util.convertToOracle:return " + result);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    private static Datum _convertToOracle(Object d, Connection c) throws SQLException {
        if (d == null) {
            return null;
        }
        if ((d instanceof ORAData)) {
            return ((ORAData) d).toDatum(c);
        }
        if ((d instanceof CustomDatum)) {
            return ((CustomDatum) d).toDatum((oracle.jdbc.driver.OracleConnection) c);
        }
        if ((d instanceof String)) {
            short csId = (c == null) || (!(c instanceof oracle.jdbc.driver.OracleConnection)) ? 870
                    : ((oracle.jdbc.driver.OracleConnection) c).getDbCsId();

            if (csId != lastCsId) {
                lastCsId = csId;
                lastCS = CharacterSet.make(lastCsId);
            }

            return new CHAR((String) d, lastCS);
        }

        if ((d instanceof Character)) {
            short csId = (c == null) || (!(c instanceof oracle.jdbc.driver.OracleConnection)) ? 870
                    : ((oracle.jdbc.driver.OracleConnection) c).getDbCsId();

            if (csId != lastCsId) {
                lastCsId = csId;
                lastCS = CharacterSet.make(lastCsId);
            }

            return new CHAR(((Character) d).toString(), lastCS);
        }

        if ((d instanceof BigDecimal)) {
            return new NUMBER((BigDecimal) d);
        }
        if ((d instanceof BigInteger)) {
            return new NUMBER((BigInteger) d);
        }
        if ((d instanceof Double)) {
            return new NUMBER(((Double) d).doubleValue());
        }
        if ((d instanceof Float)) {
            return new NUMBER(((Float) d).floatValue());
        }
        if ((d instanceof Integer)) {
            return new NUMBER(((Integer) d).intValue());
        }
        if ((d instanceof Boolean)) {
            return new NUMBER(((Boolean) d).booleanValue());
        }
        if ((d instanceof Short)) {
            return new NUMBER(((Short) d).shortValue());
        }
        if ((d instanceof Byte)) {
            return new NUMBER(((Byte) d).byteValue());
        }
        if ((d instanceof Long)) {
            return new NUMBER(((Long) d).longValue());
        }
        if ((d instanceof Timestamp)) {
            if (((oracle.jdbc.OracleConnection) c).physicalConnectionWithin().isV8Compatible()) {
                return new DATE((Timestamp) d);
            }
            return new TIMESTAMP((Timestamp) d);
        }

        if ((d instanceof java.sql.Date)) {
            return new DATE((java.sql.Date) d);
        }
        if ((d instanceof java.util.Date)) {
            return new DATE(new java.sql.Date(((java.util.Date) d).getTime()));
        }
        if ((d instanceof byte[])) {
            return new RAW((byte[]) d);
        }
        if ((d instanceof Datum)) {
            return (Datum) d;
        }
        throw new SQLException("Unable to convert object to oracle.sql.Datum: " + d);
    }

    static boolean isMutable(Datum d, ORADataFactory f) {
        if (d == null) {
            return false;
        }

        return ((d instanceof BFILE))
                || ((d instanceof BLOB))
                || ((d instanceof CLOB))
                || ((f != null) && (((d instanceof STRUCT)) || ((d instanceof OPAQUE)) || ((d instanceof ARRAY))));
    }

    static boolean isMutable(Datum d, CustomDatumFactory f) {
        if (d == null) {
            return false;
        }

        return ((d instanceof BFILE))
                || ((d instanceof BLOB))
                || ((d instanceof CLOB))
                || ((f != null) && (((d instanceof STRUCT)) || ((d instanceof OPAQUE)) || ((d instanceof ARRAY))));
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jpub.runtime.Util"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jpub.runtime.Util JD-Core Version: 0.6.0
 */