package oracle.jdbc.oracore;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.Datum;

public class OracleTypeBINARY_FLOAT extends OracleType implements Serializable {
    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeBINARY_FLOAT.toDatum (value = "
                    + value + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        BINARY_FLOAT datum = null;

        if (value != null) {
            if ((value instanceof BINARY_FLOAT))
                datum = (BINARY_FLOAT) value;
            else if ((value instanceof Float))
                datum = new BINARY_FLOAT((Float) value);
            else if ((value instanceof byte[]))
                datum = new BINARY_FLOAT((byte[]) value);
            else {
                DatabaseError.throwSqlException(59, value);
            }
        }

        return datum;
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeBINARY_FLOAT.toDatumArray( object = "
                    + obj + ", connection = " + conn + ", begin index = " + beginIdx + ", count = "
                    + count + ")");

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = null;

        if (obj != null) {
            if ((obj instanceof Object[])) {
                Object[] objArray = (Object[]) obj;

                int length = (int) (count == -1 ? objArray.length : Math.min(objArray.length
                        - beginIdx + 1L, count));

                datumArray = new Datum[length];

                for (int i = 0; i < length; i++) {
                    Object o = objArray[((int) beginIdx + i - 1)];

                    if (o != null) {
                        if ((o instanceof Float)) {
                            datumArray[i] = new BINARY_FLOAT(((Float) o).floatValue());
                        } else if ((o instanceof BINARY_FLOAT))
                            datumArray[i] = ((BINARY_FLOAT) o);
                        else
                            DatabaseError.throwSqlException(68);
                    } else {
                        DatabaseError.throwSqlException(68);
                    }
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeBINARY_FLOAT.toDatumArray:return "
                    + datumArray);

            OracleLog.recursiveTrace = false;
        }

        return datumArray;
    }

    public int getTypeCode() {
        return 100;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int style, Map map)
            throws SQLException {
        DatabaseError.throwSqlException(23, "no 8.0 pickle format for BINARY_FLOAT");

        return null;
    }

    protected Object toObject(byte[] bytes, int type, Map map) throws SQLException {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        if (type == 1)
            return new BINARY_FLOAT(bytes);
        if (type == 2)
            return new BINARY_FLOAT(bytes).toJdbc();
        if (type == 3) {
            return bytes;
        }
        DatabaseError.throwSqlException(59, bytes);

        return null;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeBINARY_FLOAT JD-Core Version: 0.6.0
 */