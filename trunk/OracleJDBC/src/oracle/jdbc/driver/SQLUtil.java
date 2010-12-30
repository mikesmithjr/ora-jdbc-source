package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCOLLECTION;
import oracle.jdbc.oracore.OracleTypeOPAQUE;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.SQLName;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;
import oracle.sql.TypeDescriptor;
import oracle.sql.converter.CharacterSetMetaData;

public class SQLUtil {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";
    private static final int CLASS_NOT_FOUND = -1;
    private static final int CLASS_STRING = 0;
    private static final int CLASS_BOOLEAN = 1;
    private static final int CLASS_INTEGER = 2;
    private static final int CLASS_LONG = 3;
    private static final int CLASS_FLOAT = 4;
    private static final int CLASS_DOUBLE = 5;
    private static final int CLASS_BIGDECIMAL = 6;
    private static final int CLASS_DATE = 7;
    private static final int CLASS_TIME = 8;
    private static final int CLASS_TIMESTAMP = 9;
    private static final int TOTAL_CLASSES = 10;
    private static Hashtable classTable;

    public static Object SQLToJava(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, Class javaClass, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava( connection=" + connection
                    + ", sqlData=" + sqlData + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ", javaClass=" + javaClass + ", map=" + map + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum datum = makeDatum(connection, sqlData, sqlTypeCode, sqlTypeName, 0);
        Object ret = SQLToJava(connection, datum, javaClass, map);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static CustomDatum SQLToJava(OracleConnection connection, byte[] sqlData,
            int sqlTypeCode, String sqlTypeName, CustomDatumFactory factory) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava( connection=" + connection
                    + ", sqlData=" + sqlData + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ", factory=" + factory + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum datum = makeDatum(connection, sqlData, sqlTypeCode, sqlTypeName, 0);
        CustomDatum ret = factory.create(datum, sqlTypeCode);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static ORAData SQLToJava(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, ORADataFactory factory) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava( connection=" + connection
                    + ", sqlData=" + sqlData + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ", factory=" + factory + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum datum = makeDatum(connection, sqlData, sqlTypeCode, sqlTypeName, 0);
        ORAData ret = factory.create(datum, sqlTypeCode);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static Object SQLToJava(OracleConnection connection, Datum datum, Class javaClass,
            Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava( connection=" + connection
                    + ", datum=" + datum + ", javaClass=" + javaClass + ", map=" + map + ")");

            OracleLog.recursiveTrace = false;
        }

        Object ret_obj = null;

        if ((datum instanceof STRUCT)) {
            if (javaClass == null) {
                ret_obj = map != null ? ((STRUCT) datum).toJdbc(map) : datum.toJdbc();
            } else {
                ret_obj = map != null ? ((STRUCT) datum).toClass(javaClass, map) : ((STRUCT) datum)
                        .toClass(javaClass);
            }

        } else if (javaClass == null) {
            ret_obj = datum.toJdbc();
        } else {
            int class_num = classNumber(javaClass);

            switch (class_num) {
            case 0:
                ret_obj = datum.stringValue();

                break;
            case 1:
                ret_obj = new Boolean(datum.longValue() != 0L);

                break;
            case 2:
                ret_obj = new Integer((int) datum.longValue());

                break;
            case 3:
                ret_obj = new Long(datum.longValue());

                break;
            case 4:
                ret_obj = new Float(datum.bigDecimalValue().floatValue());

                break;
            case 5:
                ret_obj = new Double(datum.bigDecimalValue().doubleValue());

                break;
            case 6:
                ret_obj = datum.bigDecimalValue();

                break;
            case 7:
                ret_obj = datum.dateValue();

                break;
            case 8:
                ret_obj = datum.timeValue();

                break;
            case 9:
                ret_obj = datum.timestampValue();

                break;
            case -1:
            default:
                ret_obj = datum.toJdbc();

                if (javaClass.isInstance(ret_obj)) {
                    break;
                }

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "SQLUtil.SQLToJava: Invalid data conversion. An exception is thrown.");

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(59, "invalid data conversion");
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.SQLToJava: return");

            OracleLog.recursiveTrace = false;
        }

        return ret_obj;
    }

    public static byte[] JavaToSQL(OracleConnection connection, Object inObject, int sqlTypeCode,
            String sqlTypeName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.JavaToSQL( connection=" + connection
                    + ", inObject=" + inObject + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ")");

            OracleLog.recursiveTrace = false;
        }

        if (inObject == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.FINE, "SQLUtil.JavaToSQL: return: null");

                OracleLog.recursiveTrace = false;
            }

            return null;
        }

        Datum datum = null;

        if ((inObject instanceof Datum)) {
            datum = (Datum) inObject;
        } else if ((inObject instanceof ORAData)) {
            datum = ((ORAData) inObject).toDatum(connection);
        } else if ((inObject instanceof CustomDatum)) {
            datum = connection.toDatum((CustomDatum) inObject);
        } else if ((inObject instanceof SQLData)) {
            datum = STRUCT.toSTRUCT(inObject, connection);
        }

        if (datum != null) {
            if (!checkDatumType(datum, sqlTypeCode, sqlTypeName)) {
                datum = null;
            }

        } else {
            datum = makeDatum(connection, inObject, sqlTypeCode, sqlTypeName);
        }

        byte[] ret = null;

        if (datum != null) {
            if ((datum instanceof STRUCT))
                ret = ((STRUCT) datum).toBytes();
            else if ((datum instanceof ARRAY))
                ret = ((ARRAY) datum).toBytes();
            else if ((datum instanceof OPAQUE))
                ret = ((OPAQUE) datum).toBytes();
            else {
                ret = datum.shareBytes();
            }

        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.JavaToSQL: Attempt to convert a Datum to incompatible SQL type. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1,
                                            "attempt to convert a Datum to incompatible SQL type");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.JavaToSQL: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static Datum makeDatum(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, int maxLen) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeDatum( connection=" + connection
                    + ", sqlData=" + sqlData + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ", maxLen=" + maxLen + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum ret_datum = null;

        short dbCharSet = connection.getDbCsId();
        int nlsChrSetRatio = CharacterSetMetaData.getRatio(dbCharSet, 1);

        switch (sqlTypeCode) {
        case 96:
            if ((maxLen != 0) && (maxLen < sqlData.length) && (nlsChrSetRatio == 1)) {
                ret_datum = new CHAR(sqlData, 0, maxLen, CharacterSet
                        .make(connection.getJdbcCsId()));
            } else {
                ret_datum = new CHAR(sqlData, CharacterSet.make(connection.getJdbcCsId()));
            }

            break;
        case 1:
        case 8:
            ret_datum = new CHAR(sqlData, CharacterSet.make(connection.getJdbcCsId()));

            break;
        case 2:
        case 6:
            ret_datum = new NUMBER(sqlData);

            break;
        case 100:
            ret_datum = new BINARY_FLOAT(sqlData);

            break;
        case 101:
            ret_datum = new BINARY_DOUBLE(sqlData);

            break;
        case 23:
        case 24:
            ret_datum = new RAW(sqlData);

            break;
        case 104:
            ret_datum = new ROWID(sqlData);

            break;
        case 102:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.makeDatum: Accessor.RESULT_SET type is not handled. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "need resolution: do we want to handle ResultSet?");

            break;
        case 12:
            ret_datum = new DATE(sqlData);

            break;
        case 182:
            ret_datum = new INTERVALYM(sqlData);

            break;
        case 183:
            ret_datum = new INTERVALDS(sqlData);

            break;
        case 180:
            ret_datum = new TIMESTAMP(sqlData);

            break;
        case 181:
            ret_datum = new TIMESTAMPTZ(sqlData);

            break;
        case 231:
            ret_datum = new TIMESTAMPLTZ(sqlData);

            break;
        case 113:
            ret_datum = connection.createBlob(sqlData);

            break;
        case 112:
            ret_datum = connection.createClob(sqlData);

            break;
        case 114:
            ret_datum = connection.createBfile(sqlData);

            break;
        case 109:
        {
            Object desc = TypeDescriptor.getTypeDescriptor(sqlTypeName, connection, sqlData, 0L);

            if ((desc instanceof StructDescriptor)) {
                ret_datum = new STRUCT((StructDescriptor) desc, sqlData, connection);
            } else if ((desc instanceof ArrayDescriptor)) {
                ret_datum = new ARRAY((ArrayDescriptor) desc, sqlData, connection);
            } else if ((desc instanceof OpaqueDescriptor)) {
                ret_datum = new OPAQUE((OpaqueDescriptor) desc, sqlData, connection);
            }

            break;
        }
        case 111:
        {
            Object desc = getTypeDescriptor(sqlTypeName, connection);

            if ((desc instanceof StructDescriptor)) {
                ret_datum = new REF((StructDescriptor) desc, connection, sqlData);
            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "SQLUtil.makeDatum: REF points to a non-STRUCT. An exception is thrown.");

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1, "program error: REF points to a non-STRUCT");
            }

            break;
        }
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.FINE,
                             "SQLUtil.makeDatum: Invalid SQL type code. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "program error: invalid SQL type code");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeDatum: return: " + ret_datum);

            OracleLog.recursiveTrace = false;
        }

        return ret_datum;
    }

    public static Datum makeNDatum(OracleConnection connection, byte[] sqlData, int sqlTypeCode,
            String sqlTypeName, short form, int maxLen) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeNDatum( connection=" + connection
                    + ", sqlData=" + sqlData + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ", form=" + form + ", maxLen=" + maxLen + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum ret_datum = null;

        switch (sqlTypeCode) {
        case 96:
            int len = maxLen * CharacterSetMetaData.getRatio(connection.getNCharSet(), 1);

            if ((maxLen != 0) && (len < sqlData.length)) {
                ret_datum = new CHAR(sqlData, 0, maxLen, CharacterSet
                        .make(connection.getNCharSet()));
            } else {
                ret_datum = new CHAR(sqlData, CharacterSet.make(connection.getNCharSet()));
            }

            break;
        case 1:
        case 8:
            ret_datum = new CHAR(sqlData, CharacterSet.make(connection.getNCharSet()));

            break;
        case 112:
            ret_datum = connection.createClob(sqlData, form);

            break;
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.makeNDatum: Invalid SQL type code. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "program error: invalid SQL type code");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeNDatum: return: " + ret_datum);

            OracleLog.recursiveTrace = false;
        }

        return ret_datum;
    }

    public static Datum makeDatum(OracleConnection connection, Object inObject, int sqlTypeCode,
            String sqlTypeName) throws SQLException {
        return makeDatum(connection, inObject, sqlTypeCode, sqlTypeName, false);
    }

    public static Datum makeDatum(OracleConnection connection, Object inObject, int sqlTypeCode,
            String sqlTypeName, boolean isNChar) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeDatum( connection=" + connection
                    + ", inObject=" + inObject + ", sqlTypeCode=" + sqlTypeCode + ", sqlTypeName="
                    + sqlTypeName + ", isNChar=" + isNChar + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum ret_datum = null;

        switch (sqlTypeCode) {
        case 1:
        case 8:
        case 96:
            ret_datum = new CHAR(inObject, CharacterSet.make(isNChar ? connection.getNCharSet()
                    : connection.getJdbcCsId()));

            break;
        case 2:
        case 6:
            ret_datum = new NUMBER(inObject);

            break;
        case 100:
            ret_datum = new BINARY_FLOAT((Float) inObject);

            break;
        case 101:
            ret_datum = new BINARY_DOUBLE((Double) inObject);

            break;
        case 23:
        case 24:
            ret_datum = new RAW(inObject);

            break;
        case 104:
            ret_datum = new ROWID((byte[]) inObject);

            break;
        case 102:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.makeDatum: Accessor.RESULT_SET type is not handled. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "need resolution: do we want to handle ResultSet");

            break;
        case 12:
            ret_datum = new DATE(inObject);

            break;
        case 180:
            if ((inObject instanceof TIMESTAMP)) {
                ret_datum = (Datum) inObject;
            } else if ((inObject instanceof Timestamp)) {
                ret_datum = new TIMESTAMP((Timestamp) inObject);
            } else if ((inObject instanceof Date)) {
                ret_datum = new TIMESTAMP((Date) inObject);
            } else if ((inObject instanceof Time)) {
                ret_datum = new TIMESTAMP((Time) inObject);
            } else if ((inObject instanceof DATE)) {
                ret_datum = new TIMESTAMP((DATE) inObject);
            } else if ((inObject instanceof String)) {
                ret_datum = new TIMESTAMP((String) inObject);
            } else {
                if (!(inObject instanceof byte[]))
                    break;
                ret_datum = new TIMESTAMP((byte[]) inObject);
            }
            break;
        case 181:
            if ((inObject instanceof TIMESTAMPTZ)) {
                ret_datum = (Datum) inObject;
            } else if ((inObject instanceof Timestamp)) {
                ret_datum = new TIMESTAMPTZ(connection, (Timestamp) inObject);
            } else if ((inObject instanceof Date)) {
                ret_datum = new TIMESTAMPTZ(connection, (Date) inObject);
            } else if ((inObject instanceof Time)) {
                ret_datum = new TIMESTAMPTZ(connection, (Time) inObject);
            } else if ((inObject instanceof DATE)) {
                ret_datum = new TIMESTAMPTZ(connection, (DATE) inObject);
            } else if ((inObject instanceof String)) {
                ret_datum = new TIMESTAMPTZ(connection, (String) inObject);
            } else {
                if (!(inObject instanceof byte[]))
                    break;
                ret_datum = new TIMESTAMPTZ((byte[]) inObject);
            }
            break;
        case 231:
            if ((inObject instanceof TIMESTAMPLTZ)) {
                ret_datum = (Datum) inObject;
            } else if ((inObject instanceof Timestamp)) {
                ret_datum = new TIMESTAMPLTZ(connection, (Timestamp) inObject);
            } else if ((inObject instanceof Date)) {
                ret_datum = new TIMESTAMPLTZ(connection, (Date) inObject);
            } else if ((inObject instanceof Time)) {
                ret_datum = new TIMESTAMPLTZ(connection, (Time) inObject);
            } else if ((inObject instanceof DATE)) {
                ret_datum = new TIMESTAMPLTZ(connection, (DATE) inObject);
            } else if ((inObject instanceof String)) {
                ret_datum = new TIMESTAMPLTZ(connection, (String) inObject);
            } else {
                if (!(inObject instanceof byte[]))
                    break;
                ret_datum = new TIMESTAMPLTZ((byte[]) inObject);
            }
            break;
        case 113:
            if (!(inObject instanceof BLOB))
                break;
            ret_datum = (Datum) inObject;
            break;
        case 112:
            if (!(inObject instanceof CLOB))
                break;
            ret_datum = (Datum) inObject;
            break;
        case 114:
            if (!(inObject instanceof BFILE))
                break;
            ret_datum = (Datum) inObject;
            break;
        case 109:
            if ((!(inObject instanceof STRUCT)) && (!(inObject instanceof ARRAY))
                    && (!(inObject instanceof OPAQUE))) {
                break;
            }
            ret_datum = (Datum) inObject;
            break;
        case 111:
            if (!(inObject instanceof REF))
                break;
            ret_datum = (Datum) inObject;
            break;
        }

        if (ret_datum == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.makeDatum: Unable to construct a Datum from the specified input. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1,
                                            "Unable to construct a Datum from the specified input");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeDatum: return: " + ret_datum);

            OracleLog.recursiveTrace = false;
        }

        return ret_datum;
    }

    private static int classNumber(Class inClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.classNumber( inClass=" + inClass + ")");

            OracleLog.recursiveTrace = false;
        }

        int ret = -1;
        Integer class_num = (Integer) classTable.get(inClass);

        if (class_num != null) {
            ret = class_num.intValue();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.classNumber: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static Object getTypeDescriptor(String name, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.getTypeDescriptor( name=" + name
                    + ", conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        Object descriptor = null;

        SQLName sqlName = new SQLName(name, conn);
        String qname = sqlName.getName();

        descriptor = conn.getDescriptor(qname);

        if (descriptor != null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.FINE, "SQLUtil.getTypeDescriptor: return");

                OracleLog.recursiveTrace = false;
            }

            return descriptor;
        }

        OracleTypeADT otype = new OracleTypeADT(qname, conn);
        otype.init(conn);

        OracleNamedType realType = otype.cleanup();

        switch (realType.getTypeCode()) {
        case 2003:
            descriptor = new ArrayDescriptor(sqlName, (OracleTypeCOLLECTION) realType, conn);

            break;
        case 2002:
        case 2008:
            descriptor = new StructDescriptor(sqlName, (OracleTypeADT) realType, conn);

            break;
        case 2007:
            descriptor = new OpaqueDescriptor(sqlName, (OracleTypeOPAQUE) realType, conn);

            break;
        case 2004:
        case 2005:
        case 2006:
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.getTypeDescriptor: Unrecognized type code. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "Unrecognized type code");
        }

        conn.putDescriptor(qname, descriptor);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.getTypeDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public static boolean checkDatumType(Datum datum, int sqlType, String sqlTypeName)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.checkDatumType( datum=" + datum
                    + ", sqlType=" + sqlType + ", sqlTypeName=" + sqlTypeName + ")");

            OracleLog.recursiveTrace = false;
        }

        boolean ret = false;

        switch (sqlType) {
        case 1:
        case 8:
        case 96:
            ret = datum instanceof CHAR;

            break;
        case 2:
        case 6:
            ret = datum instanceof NUMBER;

            break;
        case 100:
            ret = datum instanceof BINARY_FLOAT;

            break;
        case 101:
            ret = datum instanceof BINARY_DOUBLE;

            break;
        case 23:
        case 24:
            ret = datum instanceof RAW;

            break;
        case 104:
            ret = datum instanceof ROWID;

            break;
        case 12:
            ret = datum instanceof DATE;

            break;
        case 180:
            ret = datum instanceof TIMESTAMP;

            break;
        case 181:
            ret = datum instanceof TIMESTAMPTZ;

            break;
        case 231:
            ret = datum instanceof TIMESTAMPLTZ;

            break;
        case 113:
            ret = datum instanceof BLOB;

            break;
        case 112:
            ret = datum instanceof CLOB;

            break;
        case 114:
            ret = datum instanceof BFILE;

            break;
        case 111:
            ret = ((datum instanceof REF)) && (((REF) datum).getBaseTypeName().equals(sqlTypeName));

            break;
        case 109:
            if ((datum instanceof STRUCT)) {
                ret = ((STRUCT) datum).isInHierarchyOf(sqlTypeName);
            } else if ((datum instanceof ARRAY)) {
                ret = ((ARRAY) datum).getSQLTypeName().equals(sqlTypeName);
            } else {
                if (!(datum instanceof OPAQUE))
                    break;
                ret = ((OPAQUE) datum).getSQLTypeName().equals(sqlTypeName);
            }
            break;
        case 102:
        default:
            ret = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.checkDatumType: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static boolean implementsInterface(Class clazz, Class interfaze) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.implementsInterface( clazz=" + clazz
                    + ", interfaze=" + interfaze + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if (clazz == null) {
            return false;
        }

        if (clazz == interfaze) {
            return true;
        }

        Class[] interfazes = clazz.getInterfaces();

        for (int i = 0; i < interfazes.length; i++) {
            if (implementsInterface(interfazes[i], interfaze)) {
                return true;
            }
        }

        return implementsInterface(clazz.getSuperclass(), interfaze);
    }

    public static Datum makeOracleDatum(OracleConnection connection, Object inObject, int typeCode,
            String sqlTypeName) throws SQLException {
        return makeOracleDatum(connection, inObject, typeCode, sqlTypeName, false);
    }

    public static Datum makeOracleDatum(OracleConnection connection, Object inObject, int typeCode,
            String sqlTypeName, boolean isNChar) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeOracleDatum( connection="
                    + connection + ", inObject=" + inObject + ", typeCode=" + typeCode
                    + ", sqlTypeName=" + sqlTypeName + ", isNChar=" + isNChar + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum ret = makeDatum(connection, inObject, getInternalType(typeCode), sqlTypeName, isNChar);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.makeOracleDatum: return");

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static int getInternalType(int external_type) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.get_internal_type( external_type="
                    + external_type + ")");

            OracleLog.recursiveTrace = false;
        }

        int ret = 0;

        switch (external_type) {
        case -7:
        case -6:
        case -5:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
            ret = 6;

            break;
        case 100:
            ret = 100;

            break;
        case 101:
            ret = 101;

            break;
        case 999:
            ret = 999;

            break;
        case 1:
            ret = 96;

            break;
        case 12:
            ret = 1;

            break;
        case -1:
            ret = 8;

            break;
        case 91:
        case 92:
            ret = 12;

            break;
        case -100:
        case 93:
            ret = 180;

            break;
        case -101:
            ret = 181;

            break;
        case -102:
            ret = 231;

            break;
        case -104:
            ret = 183;

            break;
        case -103:
            ret = 182;

            break;
        case -3:
        case -2:
            ret = 23;

            break;
        case -4:
            ret = 24;

            break;
        case -8:
            ret = 104;

            break;
        case 2004:
            ret = 113;

            break;
        case 2005:
            ret = 112;

            break;
        case -13:
            ret = 114;

            break;
        case -10:
            ret = 102;

            break;
        case 2002:
        case 2003:
        case 2007:
        case 2008:
            ret = 109;

            break;
        case 2006:
            ret = 111;

            break;
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "SQLUtil.get_internal_type: Invalid column type. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(4, "get_internal_type");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLUtil.get_internal_type: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.SQLUtil"));
        } catch (Exception e) {
        }

        classTable = new Hashtable(10);
        try {
            classTable.put(Class.forName("java.lang.String"), new Integer(0));

            classTable.put(Class.forName("java.lang.Boolean"), new Integer(1));

            classTable.put(Class.forName("java.lang.Integer"), new Integer(2));

            classTable.put(Class.forName("java.lang.Long"), new Integer(3));
            classTable.put(Class.forName("java.lang.Float"), new Integer(4));

            classTable.put(Class.forName("java.lang.Double"), new Integer(5));

            classTable.put(Class.forName("java.math.BigDecimal"), new Integer(6));

            classTable.put(Class.forName("java.sql.Date"), new Integer(7));
            classTable.put(Class.forName("java.sql.Time"), new Integer(8));
            classTable.put(Class.forName("java.sql.Timestamp"), new Integer(9));
        } catch (ClassNotFoundException e) {
            if (TRACE) {
                OracleLog.print(null, 64, 4,
                                "SQLUtil :  Unexpected ClassNotFoundException in static bloc");
            }
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.SQLUtil JD-Core Version: 0.6.0
 */