package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class JAVA_STRUCT extends STRUCT {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public JAVA_STRUCT(StructDescriptor type, Connection conn, Object[] attributes)
            throws SQLException {
        super(type, conn, attributes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "JAVA_STRUCT.JAVA_STRUCT( type=" + type
                    + ", conn=" + conn + ", attributes=" + attributes + ")"
                    + ": return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public JAVA_STRUCT(StructDescriptor type, byte[] elements, Connection conn) throws SQLException {
        super(type, elements, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "JAVA_STRUCT.JAVA_STRUCT( type=" + type + ", elements="
                                              + elements + ", conn=" + conn
                                              + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "JAVA_STRUCT.toJdbc()", this);

            OracleLog.recursiveTrace = false;
        }

        Map map = getInternalConnection().getJavaObjectTypeMap();

        Class c = null;

        if (map != null) {
            c = this.descriptor.getClass(map);
        } else {
            map = new Hashtable(10);

            getInternalConnection().setJavaObjectTypeMap(map);
        }

        if (c == null) {
            String externalName = StructDescriptor.getJavaObjectClassName(getInternalConnection(),
                                                                          getDescriptor());

            String schemaName = getDescriptor().getSchemaName();

            if ((externalName == null) || (externalName.length() == 0)) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "JAVA_STRUCT.toJdbc: Internal error, 'externalName' is either null or an empty string. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

            try {
                c = getInternalConnection().classForNameAndSchema(externalName, schemaName);
            } catch (ClassNotFoundException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "JAVA_STRUCT.toJdbc: The oracle.jdbc.driver.OracleConnection class was not found. An exception is thrown. "
                                         + e.getMessage(), this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(49, "ClassNotFoundException: " + e.getMessage());
            }

            map.put(getSQLTypeName(), c);
        }

        Object ret = toClass(c, getMap());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "JAVA_STRUCT.toJdbc: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object toJdbc(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "JAVA_STRUCT.toJdbc( map=" + map
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return toJdbc();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.JAVA_STRUCT"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.JAVA_STRUCT JD-Core Version: 0.6.0
 */