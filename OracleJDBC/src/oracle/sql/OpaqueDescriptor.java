package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeOPAQUE;

public class OpaqueDescriptor extends TypeDescriptor implements Serializable {
    static final boolean DEBUG = false;
    static final long serialVersionUID = 1013921343538311063L;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public static OpaqueDescriptor createDescriptor(String name, Connection conn)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.createDescriptor( name=" + name
                    + ", conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((name == null) || (name.length() == 0) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "OpaqueDescriptor.createDescriptor: Invalid argument, 'name' shouldn't be null nor an empty string and 'conn' should not be null as well. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        SQLName sqlName = new SQLName(name, (oracle.jdbc.OracleConnection) conn);
        return createDescriptor(sqlName, conn);
    }

    public static OpaqueDescriptor createDescriptor(SQLName sqlName, Connection conn)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.createDescriptor( sqlName="
                    + sqlName + ", conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        String qualifiedName = sqlName.getName();

        OpaqueDescriptor descriptor = (OpaqueDescriptor) ((oracle.jdbc.OracleConnection) conn)
                .getDescriptor(qualifiedName);

        if (descriptor == null) {
            descriptor = new OpaqueDescriptor(sqlName, conn);

            ((oracle.jdbc.OracleConnection) conn).putDescriptor(qualifiedName, descriptor);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.createDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public OpaqueDescriptor(String name, Connection conn) throws SQLException {
        super(name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.OpaqueDescriptor( name=" + name
                    + ", conn=" + conn + ") " + "-- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        initPickler();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.OpaqueDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OpaqueDescriptor(SQLName name, Connection conn) throws SQLException {
        super(name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.OpaqueDescriptor( name=" + name
                    + ", conn=" + conn + ") " + "-- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        initPickler();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.OpaqueDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OpaqueDescriptor(SQLName name, OracleTypeOPAQUE type, Connection conn)
            throws SQLException {
        super(name, type, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.OpaqueDescriptor( name=" + name
                    + ", type=" + type + ", conn=" + conn + ")" + " -- after super() -- return",
                                      this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void initPickler() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.initPickler()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.pickler = new OracleTypeADT(getName(), this.connection);

            ((OracleTypeADT) this.pickler).init(this.connection);

            this.pickler = ((OracleTypeOPAQUE) ((OracleTypeADT) this.pickler).cleanup());

            this.pickler.setDescriptor(this);
        } catch (Exception e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "OpaqueDescriptor.initPickler: exception caught and thrown."
                                                  + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            if ((e instanceof SQLException)) {
                throw ((SQLException) e);
            }
            DatabaseError.throwSqlException(60, "Unable to resolve type \"" + getName() + "\"");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.initPickler: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OpaqueDescriptor(OracleTypeADT type, Connection conn) throws SQLException {
        super(type, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.OpaqueDescriptor( type=" + type
                    + ", conn=" + conn + ")" + ": return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    byte[] toBytes(OPAQUE obj, boolean keepLocalCopy) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.toBytes( obj=" + obj
                    + ", keepLocalCopy=" + keepLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] ret = null;

        if (obj.shareBytes() != null) {
            ret = obj.shareBytes();
        } else {
            try {
                ret = this.pickler.linearize(obj);
            } finally {
                if (!keepLocalCopy) {
                    obj.setShareBytes(null);
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.toBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    byte[] toValue(OPAQUE obj, boolean keepLocalCopy) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.toValue( obj=" + obj
                    + ", keepLocalCopy=" + keepLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] ret = null;

        if (obj.value != null) {
            ret = obj.value;
        } else {
            try {
                this.pickler.unlinearize(obj.shareBytes(), 0L, obj, 1, null);

                ret = obj.value;
            } finally {
                if (!keepLocalCopy) {
                    obj.value = null;
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.toValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int getTypeCode() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.getTypeCode(): return: 2007",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return 2007;
    }

    public long getMaxLength() throws SQLException {
        long ret = hasUnboundedSize() ? 0L : ((OracleTypeOPAQUE) this.pickler).getMaxLength();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OpaqueDescriptor.getMaxLength(): return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isTrustedLibrary() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OpaqueDescriptor.isTrustedLibrary() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ((OracleTypeOPAQUE) this.pickler).isTrustedLibrary();
    }

    public boolean isModeledInC() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.isModeledInC() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return ((OracleTypeOPAQUE) this.pickler).isModeledInC();
    }

    public boolean hasUnboundedSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "OpaqueDescriptor.hasUnboundedSize() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ((OracleTypeOPAQUE) this.pickler).isUnboundedSized();
    }

    public boolean hasFixedSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.hasFixedSize() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return ((OracleTypeOPAQUE) this.pickler).isFixedSized();
    }

    public String descType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.descType()", this);

            OracleLog.recursiveTrace = false;
        }

        StringBuffer strBuf = new StringBuffer();
        String ret = descType(strBuf, 0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.descType: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    String descType(StringBuffer strBuf, int level) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.descType( strBuf=" + strBuf
                    + ", level=" + level + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String level_one = "";

        for (int i = 0; i < level; i++) {
            level_one = level_one + "  ";
        }
        String level_two = level_one + "  ";

        strBuf.append(level_one);
        strBuf.append(getTypeName());
        strBuf.append(" maxLen=" + getMaxLength() + " isTrusted=" + isTrustedLibrary()
                + " hasUnboundedSize=" + hasUnboundedSize() + " hasFixedSize=" + hasFixedSize());

        strBuf.append("\n");

        String ret = strBuf.toString();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.descType: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Class getClass(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.getClass( map=" + map + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        Class ret = null;

        String qualifiedName = getName();

        Class c = (Class) map.get(qualifiedName);

        String schema = getSchemaName();
        String type = getTypeName();

        if (c == null) {
            if (this.connection.getUserName().equals(schema)) {
                c = (Class) map.get(type);
            }
        }
        if (!SQLName.s_parseAllFormat) {
            ret = c;
        } else {
            if (c == null) {
                if (this.connection.getUserName().equals(schema)) {
                    c = (Class) map.get("\"" + type + "\"");
                }
            }
            if (c == null) {
                c = (Class) map.get("\"" + schema + "\"" + "." + "\"" + type + "\"");
            }

            if (c == null) {
                c = (Class) map.get("\"" + schema + "\"" + "." + type);
            }

            if (c == null) {
                c = (Class) map.get(schema + "." + "\"" + type + "\"");
            }

            ret = c;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "OpaqueDescriptor.getClass: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.writeObject( out=" + out
                    + ") -- this does nothing --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "OpaqueDescriptor.readObject( in=" + in
                    + ") -- this does nothing --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.OpaqueDescriptor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.OpaqueDescriptor JD-Core Version: 0.6.0
 */