package oracle.sql;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Struct;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class STRUCT extends DatumWithConnection implements Struct {
    StructDescriptor descriptor;
    Datum[] datumArray;
    Object[] objectArray;
    boolean enableLocalCache = false;
    long imageOffset;
    long imageLength;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public STRUCT(StructDescriptor type, Connection conn, Object[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.STRUCT( type=" + type + ", conn=" + conn
                    + ", attributes=" + attributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(type);

        this.descriptor = type;

        assertNotNull(conn);

        if (!type.getInternalConnection()
                .isDescriptorSharable(
                                      ((oracle.jdbc.OracleConnection) conn)
                                              .physicalConnectionWithin())) {
            throw new SQLException("Cannot construct STRUCT instance, invalid connection");
        }

        type.setConnection(conn);

        if (!this.descriptor.isInstantiable()) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "STRUCT.STRUCT: Cannot construct STRUCT instance for a non-instantiable object type. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new SQLException(
                    "Cannot construct STRUCT instance for a non-instantiable object type");
        }

        setPhysicalConnectionOf(conn);

        if (attributes != null)
            this.datumArray = this.descriptor.toArray(attributes);
        else {
            this.datumArray = new Datum[0];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.STRUCT: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public STRUCT(StructDescriptor type, Connection conn, Map attrList) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.STRUCT( type=" + type + ", conn=" + conn
                    + ", attributes(Map)=" + attrList + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(type);

        this.descriptor = type;

        assertNotNull(conn);

        if (!type.getInternalConnection()
                .isDescriptorSharable(
                                      ((oracle.jdbc.OracleConnection) conn)
                                              .physicalConnectionWithin())) {
            throw new SQLException("Cannot construct STRUCT instance, invalid connection");
        }

        type.setConnection(conn);

        if (!this.descriptor.isInstantiable()) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "STRUCT.STRUCT: Cannot construct STRUCT instance for a non-instantiable object type. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new SQLException(
                    "Cannot construct STRUCT instance for a non-instantiable object type");
        }

        setPhysicalConnectionOf(conn);

        this.datumArray = this.descriptor.toOracleArray(attrList);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.STRUCT: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public STRUCT(StructDescriptor type, byte[] elements, Connection conn) throws SQLException {
        super(elements);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.STRUCT( type=" + type + ", elements="
                    + elements + ", conn=" + conn + ") -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(type);

        this.descriptor = type;

        assertNotNull(conn);

        if (!type.getInternalConnection()
                .isDescriptorSharable(
                                      ((oracle.jdbc.OracleConnection) conn)
                                              .physicalConnectionWithin())) {
            throw new SQLException("Cannot construct STRUCT instance, invalid connection");
        }

        type.setConnection(conn);
        setPhysicalConnectionOf(conn);

        this.datumArray = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.STRUCT: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized String getSQLTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getSQLTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = this.descriptor.getName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getSQLTypeName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized Object[] getAttributes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getAttributes()", this);

            OracleLog.recursiveTrace = false;
        }

        Object[] ret = getAttributes(getMap());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getAttributes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized Object[] getAttributes(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getAttributes( map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object[] ret = this.descriptor.toArray(this, map, this.enableLocalCache);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getAttributes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized StructDescriptor getDescriptor() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getDescriptor(): return: "
                    + this.descriptor, this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor;
    }

    public synchronized void setDescriptor(StructDescriptor desc) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setDescriptor( desc=" + desc
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.descriptor = desc;
    }

    public synchronized Datum[] getOracleAttributes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getOracleAttributes()", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] ret = this.descriptor.toOracleArray(this, this.enableLocalCache);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getOracleAttributes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Map getMap() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getMap()", this);

            OracleLog.recursiveTrace = false;
        }

        Map ret = null;
        try {
            ret = getInternalConnection().getTypeMap();
        } catch (SQLException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE, "STRUCT.getMap: Internal error. "
                        + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getMap: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized byte[] toBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toBytes()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] ret = this.descriptor.toBytes(this, this.enableLocalCache);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "STRUCT.toBytes: return: " + OracleLog.toHex(ret), this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized void setDatumArray(Datum[] darray) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setDatumArray( darray=" + darray
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        this.datumArray = (darray == null ? new Datum[0] : darray);
    }

    public synchronized void setObjArray(Object[] oarray) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setObjArray( oarray=" + oarray + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        this.objectArray = (oarray == null ? new Object[0] : oarray);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setObjArray: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public static STRUCT toSTRUCT(Object obj, oracle.jdbc.OracleConnection conn)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toSTRUCT( obj=" + obj + ", conn=" + conn
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = null;

        if (obj != null) {
            if ((obj instanceof STRUCT)) {
                s = (STRUCT) obj;
            } else if ((obj instanceof ORAData)) {
                s = (STRUCT) ((ORAData) obj).toDatum(conn);
            } else if ((obj instanceof CustomDatum)) {
                s = (STRUCT) ((oracle.jdbc.internal.OracleConnection) conn)
                        .toDatum((CustomDatum) obj);
            } else if ((obj instanceof SQLData)) {
                SQLData sqldataObj = (SQLData) obj;

                StructDescriptor desc = StructDescriptor.createDescriptor(sqldataObj
                        .getSQLTypeName(), conn);

                SQLOutput sqlOutput = desc.toJdbc2SQLOutput();

                sqldataObj.writeSQL(sqlOutput);

                s = ((OracleSQLOutput) sqlOutput).getSTRUCT();
            } else {
                DatabaseError.throwSqlException(59, obj);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toSTRUCT: return");

            OracleLog.recursiveTrace = false;
        }

        return s;
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toJdbc()", this);

            OracleLog.recursiveTrace = false;
        }

        Map map = getMap();
        Object ret = toJdbc(map);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toJdbc: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object toJdbc(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toJdbc( map=" + map + ")", this);

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
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toJdbc: return", this);

            OracleLog.recursiveTrace = false;
        }

        return jdbcObject;
    }

    public Object toClass(Class clazz) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toClass( clazz=" + clazz + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = toClass(clazz, getMap());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toClass: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object toClass(Class clazz, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toClass( clazz=" + clazz + ", map=" + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object obj = null;
        try {
            if ((clazz == null) || (clazz == STRUCT.class) || (clazz == Struct.class)) {
                obj = this;
            } else {
                Object i = clazz.newInstance();

                if ((i instanceof SQLData)) {
                    ((SQLData) i).readSQL(this.descriptor.toJdbc2SQLInput(this, map),
                                          this.descriptor.getName());

                    obj = i;
                } else if ((i instanceof ORADataFactory)) {
                    ORADataFactory f = (ORADataFactory) i;

                    obj = f.create(this, 2002);
                } else if ((i instanceof CustomDatumFactory)) {
                    CustomDatumFactory f = (CustomDatumFactory) i;

                    obj = f.create(this, 2002);
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger
                                .log(Level.SEVERE,
                                     "STRUCT.toClass: Fail to convert. An exception is thrown.",
                                     this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(49, this.descriptor.getName());
                }

            }

        } catch (InstantiationException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "STRUCT.toClass: Instantiation exception caught and thrown."
                                                  + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(49, "InstantiationException: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "STRUCT.toClass: IllegalAccess exception caught and thrown."
                                                  + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(49, "IllegalAccessException: " + ex.getMessage());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.toClass: return", this);

            OracleLog.recursiveTrace = false;
        }

        return obj;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.isConvertibleTo( jClass=" + jClass
                    + "): return : false (allways)", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.makeJdbcArray( arraySize=" + arraySize
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new Object[arraySize];
    }

    public synchronized void setAutoBuffering(boolean enable) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setAutoBuffering( enable=" + enable
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.enableLocalCache = enable;
    }

    public synchronized boolean getAutoBuffering() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getAutoBuffering(): return: "
                    + this.enableLocalCache, this);

            OracleLog.recursiveTrace = false;
        }

        return this.enableLocalCache;
    }

    public void setImage(byte[] image, long offset, long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setImage( image=" + image + ", offset="
                    + offset + ", length=" + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setShareBytes(image);

        this.imageOffset = offset;
        this.imageLength = length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setImage: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setImageLength(long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.setImageLength( length=" + length
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.imageLength = length;
    }

    public long getImageOffset() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getImageOffset(): return: "
                    + this.imageOffset, this);

            OracleLog.recursiveTrace = false;
        }

        return this.imageOffset;
    }

    public long getImageLength() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getImageLength(): return: "
                    + this.imageLength, this);

            OracleLog.recursiveTrace = false;
        }

        return this.imageLength;
    }

    public CustomDatumFactory getFactory(Hashtable map, String classname) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getFactory( map=" + map + ", classname="
                    + classname + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String sqlTypeName = getSQLTypeName();
        Object factory = map.get(sqlTypeName);

        if (factory == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "STRUCT.getFactory: An exception is thrown.", this);

                OracleLog.recursiveTrace = false;
            }

            throw new SQLException("Unable to convert a \"" + sqlTypeName + "\" to a \""
                    + classname + "\" or a subclass of \"" + classname + "\"");
        }

        CustomDatumFactory ret = (CustomDatumFactory) factory;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getFactory: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ORADataFactory getORADataFactory(Hashtable map, String classname) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getORADataFactory( map=" + map
                    + ", classname=" + classname + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String sqlTypeName = getSQLTypeName();
        Object factory = map.get(sqlTypeName);

        if (factory == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE, "STRUCT.getORADataFactory: An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            throw new SQLException("Unable to convert a \"" + sqlTypeName + "\" to a \""
                    + classname + "\" or a subclass of \"" + classname + "\"");
        }

        ORADataFactory ret = (ORADataFactory) factory;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.getORADataFactory: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String debugString() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.debugString()", this);

            OracleLog.recursiveTrace = false;
        }

        StringWriter result = new StringWriter();
        String ret = null;
        try {
            StructDescriptor desc = getDescriptor();

            result.write("name = " + desc.getName());
            int length;
            result.write(" length = " + (length = desc.getLength()));

            Object[] attr = getAttributes();

            for (int i = 0; i < length; i++) {
                result.write(" attribute[" + i + "] = " + attr[i]);
            }

        } catch (SQLException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "STRUCT.debugString: StructDescriptor missing or bad formatted.", this);

                OracleLog.recursiveTrace = false;
            }

            ret = "StructDescriptor missing or bad";
        }

        ret = result.toString();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.debugString: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isInHierarchyOf(String checkThisName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.isInHierarchyOf( checkThisName="
                    + checkThisName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = getDescriptor().isInHierarchyOf(checkThisName);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "STRUCT.isInHierarchyOf: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    public String dump() throws SQLException {
        return dump(this);
    }

    public static String dump(Object o) throws SQLException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        dump(o, pw);
        return sw.toString();
    }

    public static void dump(Object o, PrintStream ps) throws SQLException {
        dump(o, new PrintWriter(ps, true));
    }

    public static void dump(Object o, PrintWriter pw) throws SQLException {
        dump(o, pw, 0);
    }

    static void dump(Object o, PrintWriter pw, int indent) throws SQLException {
        if ((o instanceof STRUCT)) {
            dump((STRUCT) o, pw, indent);
            return;
        }
        if ((o instanceof ARRAY)) {
            ARRAY.dump((ARRAY) o, pw, indent);
            return;
        }
        pw.println(o.toString());
    }

    static void dump(STRUCT x, PrintWriter pw, int indent) throws SQLException {
        StructDescriptor desc = x.getDescriptor();
        ResultSetMetaData md = desc.getMetaData();
        int i = 0;

        for (i = 0; i < indent; i++)
            pw.print(' ');
        pw.println("name = " + desc.getName());

        for (i = 0; i < indent; i++)
            pw.print(' ');
        int length;
        pw.println("length = " + (length = desc.getLength()));
        Object[] attr = x.getAttributes();
        for (i = 0; i < length; i++) {
            for (int j = 0; j < indent; j++)
                pw.print(' ');
            pw.print(md.getColumnName(i + 1) + " = ");
            dump(attr[i], pw, indent + 1);
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.STRUCT"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.STRUCT JD-Core Version: 0.6.0
 */