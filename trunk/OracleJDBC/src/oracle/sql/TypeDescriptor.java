package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCOLLECTION;
import oracle.jdbc.oracore.OracleTypeOPAQUE;

public abstract class TypeDescriptor implements Serializable {
    public static boolean DEBUG_SERIALIZATION = false;
    static final long serialVersionUID = 2022598722047823723L;
    SQLName sqlName;
    OracleNamedType pickler;
    transient oracle.jdbc.internal.OracleConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    protected TypeDescriptor() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected TypeDescriptor(String name, Connection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor( name=" + name
                    + ", conn=" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((name == null) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "TypeDescriptor.TypeDescriptor: Invalid arguments, 'name' should not be an empty string and conn should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        setPhysicalConnectionOf(conn);

        this.sqlName = new SQLName(name, getInternalConnection());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected TypeDescriptor(SQLName name, Connection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor( name=" + name
                    + ", conn=" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((name == null) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "TypeDescriptor.TypeDescriptor: Invalid arguments, 'name' and  'conn'  should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        this.sqlName = name;

        setPhysicalConnectionOf(conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected TypeDescriptor(SQLName name, OracleTypeADT type, Connection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor( name=" + name
                    + ", type=" + type + ", conn=" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((name == null) || (type == null) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "TypeDescriptor.TypeDescriptor: Invalid arguments, 'name', 'type' and 'conn'  should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        this.sqlName = name;

        setPhysicalConnectionOf(conn);

        this.pickler = type;

        this.pickler.setDescriptor(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected TypeDescriptor(OracleTypeADT type, Connection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor( type=" + type
                    + ", conn=" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((type == null) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "TypeDescriptor.TypeDescriptor: Invalid arguments, 'type' and  'conn'  should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        setPhysicalConnectionOf(conn);

        this.sqlName = null;
        this.pickler = type;

        this.pickler.setDescriptor(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.TypeDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized String getName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getName()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sqlName == null) {
            initSQLName();
        }
        String ret = this.sqlName.getName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized SQLName getSQLName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getSQLName()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sqlName == null) {
            initSQLName();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getSQLName: return: "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlName;
    }

    void initSQLName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.initSQLName()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.pickler == null) || (getInternalConnection() == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "TypeDescriptor.initSQLName: Internal error. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1);
        }

        this.sqlName = new SQLName(this.pickler.getFullName(), getInternalConnection());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.initSQLName: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public String getSchemaName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getSchemaName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = getSQLName().getSchema();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getSchemaName: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = getSQLName().getSimpleName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getTypeName: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public OracleNamedType getPickler() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getPickler: return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.pickler;
    }

    public oracle.jdbc.internal.OracleConnection getInternalConnection() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getInternalConnection: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.connection;
    }

    public void setPhysicalConnectionOf(Connection conn) {
        this.connection = ((oracle.jdbc.OracleConnection) conn).physicalConnectionWithin();
    }

    public abstract int getTypeCode() throws SQLException;

    public static TypeDescriptor getTypeDescriptor(String name, oracle.jdbc.OracleConnection conn)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getTypeDescriptor( name=" + name
                    + ", conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        TypeDescriptor descriptor = null;
        try {
            SQLName sqlName = new SQLName(name, conn);
            String qname = sqlName.getName();

            descriptor = (TypeDescriptor) conn.getDescriptor(qname);

            if (descriptor == null) {
                OracleTypeADT otype = new OracleTypeADT(qname, conn);
                oracle.jdbc.internal.OracleConnection iconn = (oracle.jdbc.internal.OracleConnection) conn;

                otype.init(iconn);

                OracleNamedType realType = otype.cleanup();

                switch (realType.getTypeCode()) {
                case 2002:
                case 2008:
                    descriptor = new StructDescriptor(sqlName, (OracleTypeADT) realType, conn);

                    break;
                case 2003:
                    descriptor = new ArrayDescriptor(sqlName, (OracleTypeCOLLECTION) realType, conn);

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
                                .log(
                                     Level.SEVERE,
                                     "TypeDescriptor.getTypeDescriptor: The real type should be either STRUCT, JAVA_STRUCT, ARRAY or OPAQUE. An exception is thrown.");

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(1);
                }

                conn.putDescriptor(qname, descriptor);

                realType.setDescriptor(descriptor);
            }

        } catch (Exception e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "TypeDescriptor.getTypeDescriptor: Exception caught and thrown."
                                                  + e.getMessage());

                OracleLog.recursiveTrace = false;
            }

            if ((e instanceof SQLException)) {
                DatabaseError.throwSqlException((SQLException) e, 60, "Unable to resolve type \""
                        + name + "\"");
            } else {
                DatabaseError.throwSqlException(60, "Unable to resolve type \"" + name + "\"");
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getTypeDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public static TypeDescriptor getTypeDescriptor(String qualifiedName,
            oracle.jdbc.OracleConnection conn, byte[] image, long offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "TypeDescriptor.getTypeDescriptor( qualifiedName="
                                              + qualifiedName + ", conn=" + conn + ", image="
                                              + image + ", offset=" + offset + ")");

            OracleLog.recursiveTrace = false;
        }

        TypeDescriptor descriptor = null;
        byte[][] hashKey = new byte[1][];

        String qname = getSubtypeName(conn, image, offset);

        if (qname == null) {
            qname = qualifiedName;
        }

        descriptor = (TypeDescriptor) conn.getDescriptor(qname);

        if (descriptor == null) {
            SQLName sqlName = new SQLName(qname, conn);

            OracleTypeADT otype = new OracleTypeADT(qname, conn);
            oracle.jdbc.internal.OracleConnection iconn = (oracle.jdbc.internal.OracleConnection) conn;

            otype.init(iconn);

            OracleNamedType realType = otype.cleanup();

            switch (realType.getTypeCode()) {
            case 2002:
            case 2008:
                descriptor = new StructDescriptor(sqlName, (OracleTypeADT) realType, conn);

                break;
            case 2003:
                descriptor = new ArrayDescriptor(sqlName, (OracleTypeCOLLECTION) realType, conn);

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
                            .log(
                                 Level.SEVERE,
                                 "TypeDescriptor.getTypeDescriptor: The real type should be either STRUCT, JAVA_STRUCT, ARRAY or OPAQUE. An exception is thrown.");

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

            conn.putDescriptor(qname, descriptor);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getTypeDescriptor: return: "
                    + descriptor);

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public boolean isInHierarchyOf(String checkThisName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.isInHierarchyOf( checkThisName="
                    + checkThisName + "): return: false(always)", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.writeObject( out=" + out + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.sqlName == null) {
                initSQLName();
            }

        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "TypeDescriptor.writeObject: Exception caught and thrown."
                                                  + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.SQLToIOException(e);
        }

        out.writeObject(this.sqlName);
        out.writeObject(this.pickler);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.writeObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "TypeDescriptor.readObject( in=" + in + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.sqlName = ((SQLName) in.readObject());
        this.pickler = ((OracleNamedType) in.readObject());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.readObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setConnection(Connection connection) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.setConnection( connection="
                    + connection + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setPhysicalConnectionOf(connection);
        this.pickler.setConnection(getInternalConnection());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.setConnection: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public static String getSubtypeName(oracle.jdbc.OracleConnection conn, byte[] image, long offset)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getSubtypeName( conn=" + conn
                    + ", image=" + image + ", offset=" + offset + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((image == null) || (image.length == 0) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.FINE,
                             "TypeDescriptor.getSubtypeName: Invalid arguments, 'image' should not be empty and 'conn' should not be null. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        String ret = OracleTypeADT.getSubtypeName(conn, image, offset);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "TypeDescriptor.getSubtypeName: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public void initMetadataRecursively() throws SQLException {
        if (this.pickler != null)
            this.pickler.initMetadataRecursively();
    }

    public void initNamesRecursively() throws SQLException {
        if (this.pickler != null)
            this.pickler.initNamesRecursively();
    }

    public void fixupConnection(oracle.jdbc.internal.OracleConnection fixupConn)
            throws SQLException {
        if (this.connection == null)
            this.connection = fixupConn;
        if (this.pickler != null)
            this.pickler.fixupConnection(fixupConn);
    }

    public String toXMLString() throws SQLException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printXMLHeader(pw);
        printXML(pw, 0);
        return sw.toString();
    }

    public void printXML(PrintStream s) throws SQLException {
        PrintWriter pw = new PrintWriter(s, true);
        printXMLHeader(pw);
        printXML(pw, 0);
    }

    void printXML(PrintWriter pw, int indent) throws SQLException {
        String clname = getClass().getName();
        int hc = hashCode();
        pw.println("<" + clname + " hashCode=\"" + hc + "\" >");
        if (this.pickler != null)
            this.pickler.printXML(pw, indent + 1);
        pw.println("</" + clname + ">");
    }

    void printXMLHeader(PrintWriter pw) throws SQLException {
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.TypeDescriptor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.TypeDescriptor JD-Core Version: 0.6.0
 */