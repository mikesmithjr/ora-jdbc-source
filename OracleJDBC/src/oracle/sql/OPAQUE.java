package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;

public class OPAQUE extends DatumWithConnection {
    OpaqueDescriptor descriptor;
    byte[] value;
    long imageOffset;
    long imageLength;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public OPAQUE(OpaqueDescriptor type, Connection conn, Object value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.OPAQUE( type=" + type + ", conn=" + conn
                    + ", value=" + value + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (type != null) {
            this.descriptor = type;
        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "OPAQUE.OPAQUE: Invalid argument, 'type' should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(61, "OPAQUE");
        }

        setPhysicalConnectionOf(conn);

        if ((value instanceof byte[])) {
            this.value = ((byte[]) value);
        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "OPAQUE.OPAQUE: Invalid argument, the type of 'value' should be byte[]. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(59, "OPAQUE()");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.OPAQUE: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OPAQUE(OpaqueDescriptor type, byte[] bytes, Connection conn) throws SQLException {
        super(bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.OPAQUE( type=" + type + ", bytes="
                    + bytes + ", conn=" + conn + ") -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        setPhysicalConnectionOf(conn);

        this.descriptor = type;
        this.value = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.OPAQUE: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public String getSQLTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getSQLTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = this.descriptor.getName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getSQLTypeName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public OpaqueDescriptor getDescriptor() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getDescriptor(): return: "
                    + this.descriptor, this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor;
    }

    public void setDescriptor(OpaqueDescriptor desc) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.setDescriptor( desc=" + desc
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.descriptor = desc;
    }

    public byte[] toBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toBytes() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toBytes(this, false);
    }

    public Object getValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getValue() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toValue(this, false);
    }

    public byte[] getBytesValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getBytesValue() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toValue(this, false);
    }

    public void setValue(byte[] value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.setValue( value=" + value
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.value = value;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.isConvertibleTo( jClass=" + jClass
                    + "): return: false (always)", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.makeJdbcArray( arraySize=" + arraySize
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return new Object[arraySize];
    }

    public Map getMap() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getMap() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            return getInternalConnection().getTypeMap();
        } catch (SQLException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.FINER, "OPAQUE.getMap: exception caught."
                        + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }
        }
        return null;
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toJdbc()", this);

            OracleLog.recursiveTrace = false;
        }

        Map map = getMap();
        Object ret = toJdbc(map);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toJdbc: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object toJdbc(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toJdbc( map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object jdbcObject = this;

        if (map != null) {
            Class c = this.descriptor.getClass(map);

            if (c != null) {
                jdbcObject = toClass(c, map);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toJdbc: return", this);

            OracleLog.recursiveTrace = false;
        }

        return jdbcObject;
    }

    public Object toClass(Class clazz) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toClass( clazz=" + clazz + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = toClass(clazz, getMap());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toClass: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object toClass(Class clazz, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toClass( clazz=" + clazz + ", map=" + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = null;
        try {
            if ((clazz == null) || (clazz == OPAQUE.class)) {
                ret = this;
            } else {
                Object obj = null;
                Object i = clazz.newInstance();

                if ((i instanceof ORADataFactory)) {
                    ORADataFactory f = (ORADataFactory) i;

                    obj = f.create(this, 2007);
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger
                                .log(
                                     Level.SEVERE,
                                     "OPAQUE.toClass: 'clazz' should be oracle.sql.OPAQUE or 'clazz' should have a constructor that takes an oracle.sql.OPAQUE or 'clazz' should implement ORADataFactory. Here it is not the case, thus an exception is thrown.",
                                     this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(49, this.descriptor.getName());
                }

                ret = obj;
            }

        } catch (InstantiationException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "OPAQUE.toClass: exception caught and thrown."
                                                  + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(49, "InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "OPAQUE.toClass: exception caught and thrown."
                                                  + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(49, "IllegalAccessException: " + ex.getMessage());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.toClass: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public void setImage(byte[] image, long offset, long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.setImage( image=" + image + ", offset="
                    + offset + ", length=" + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setShareBytes(image);

        this.imageOffset = offset;
        this.imageLength = length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.setImage: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setImageLength(long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.setImageLength( length=" + length
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.imageLength = length;
    }

    public long getImageOffset() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getImageOffset(): return: "
                    + this.imageOffset, this);

            OracleLog.recursiveTrace = false;
        }

        return this.imageOffset;
    }

    public long getImageLength() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OPAQUE.getImageLength(): return: "
                    + this.imageLength, this);

            OracleLog.recursiveTrace = false;
        }

        return this.imageLength;
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.OPAQUE"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.OPAQUE JD-Core Version: 0.6.0
 */