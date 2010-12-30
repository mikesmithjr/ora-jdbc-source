package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.SQLName;
import oracle.sql.TypeDescriptor;

public abstract class OracleNamedType extends OracleType implements Serializable {
    transient OracleConnection connection;
    SQLName sqlName = null;
    transient OracleTypeADT parent = null;
    transient int idx;
    transient TypeDescriptor descriptor = null;

    static String getUserTypeTreeSql = "/*+ RULE */select level depth, parent_type, child_type, ATTR_NO, child_type_owner from  (select TYPE_NAME parent_type, ELEM_TYPE_NAME child_type, 0 ATTR_NO,       ELEM_TYPE_OWNER child_type_owner     from USER_COLL_TYPES  union   select TYPE_NAME parent_type, ATTR_TYPE_NAME child_type, ATTR_NO,       ATTR_TYPE_OWNER child_type_owner     from USER_TYPE_ATTRS  ) start with parent_type  = ?  connect by prior  child_type = parent_type";

    static String getAllTypeTreeSql = "/*+ RULE */select parent_type, parent_type_owner, child_type, ATTR_NO, child_type_owner from ( select TYPE_NAME parent_type,  OWNER parent_type_owner,     ELEM_TYPE_NAME child_type, 0 ATTR_NO,     ELEM_TYPE_OWNER child_type_owner   from ALL_COLL_TYPES union   select TYPE_NAME parent_type, OWNER parent_type_owner,     ATTR_TYPE_NAME child_type, ATTR_NO,     ATTR_TYPE_OWNER child_type_owner   from ALL_TYPE_ATTRS ) start with parent_type  = ?  and parent_type_owner = ? connect by prior child_type = parent_type   and ( child_type_owner = parent_type_owner or child_type_owner is null )";

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    protected OracleNamedType() {
    }

    public OracleNamedType(String name, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleNamedType(name = " + name
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setConnectionInternal(conn);
        this.sqlName = new SQLName(name, conn);
    }

    protected OracleNamedType(OracleTypeADT parent, int idx, OracleConnection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleNamedType(parent = " + parent + ", idx = "
                    + idx + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setConnectionInternal(conn);
        this.parent = parent;
        this.idx = idx;
    }

    public String getFullName() throws SQLException {
        return getFullName(false);
    }

    public String getFullName(boolean force) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "getFullName(" + force + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String temp_fullName = null;

        if ((force | this.sqlName == null)) {
            if ((this.parent != null)
                    && ((temp_fullName = this.parent.getAttributeType(this.idx)) != null)) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.adtLogger.log(Level.FINEST, "getFullName going to db.", this);

                    OracleLog.recursiveTrace = false;
                }

                this.sqlName = new SQLName(temp_fullName, this.connection);
            } else {
                DatabaseError.throwSqlException(1, "Unable to resolve name");
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "getFullName:return " + this.sqlName.getName(),
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlName.getName();
    }

    public String getSchemaName() throws SQLException {
        if (this.sqlName == null)
            getFullName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "getSchemaName:return "
                    + this.sqlName.getSchema(), this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlName.getSchema();
    }

    public String getSimpleName() throws SQLException {
        if (this.sqlName == null)
            getFullName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "getSimpleName:return "
                    + this.sqlName.getSimpleName(), this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlName.getSimpleName();
    }

    public boolean hasName() throws SQLException {
        return this.sqlName != null;
    }

    public OracleTypeADT getParent() throws SQLException {
        return this.parent;
    }

    public void setParent(OracleTypeADT parent) throws SQLException {
        this.parent = parent;
    }

    public int getOrder() throws SQLException {
        return this.idx;
    }

    public void setOrder(int order) throws SQLException {
        this.idx = order;
    }

    public OracleConnection getConnection() throws SQLException {
        return this.connection;
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        setConnectionInternal(conn);
    }

    public void setConnectionInternal(OracleConnection conn) {
        this.connection = conn;
    }

    public Datum unlinearize(byte[] pickled_bytes, long offset, Datum container, int type,
            Map objmap) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public Datum unlinearize(byte[] pickled_bytes, long offset, Datum container, long idx, int cnt,
            int type, Map objmap) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public byte[] linearize(Datum data) throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public TypeDescriptor getDescriptor() throws SQLException {
        return this.descriptor;
    }

    public void setDescriptor(TypeDescriptor desc) throws SQLException {
        this.descriptor = desc;
    }

    public int getTypeVersion() {
        return 1;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleNamedType.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            out.writeUTF(getFullName());
        } catch (SQLException e) {
            DatabaseError.SQLToIOException(e);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleNamedType.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        String name = in.readUTF();
        try {
            this.sqlName = new SQLName(name, null);
        } catch (SQLException ex) {
        }
        this.parent = null;
        this.idx = -1;
    }

    public void fixupConnection(OracleConnection fixupConn) throws SQLException {
        if (this.connection == null)
            setConnection(fixupConn);
    }

    public void printXML(PrintWriter pw, int indent) throws SQLException {
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("<OracleNamedType/>");
    }

    public void initNamesRecursively() throws SQLException {
        Map typesTreeMap = createTypesTreeMap();
        if (typesTreeMap.size() > 0)
            initChildNamesRecursively(typesTreeMap);
    }

    public void setNames(String schemaName, String typeName) throws SQLException {
        this.sqlName = new SQLName(schemaName, typeName, this.connection);
    }

    public void setSqlName(SQLName x) {
        this.sqlName = x;
    }

    public Map createTypesTreeMap() throws SQLException {
        Map nodeMap = null;
        String currentUserName = this.connection.getUserName();
        if (this.sqlName.getSchema().equals(currentUserName)) {
            nodeMap = getNodeMapFromUserTypes();
        }
        if (nodeMap == null)
            nodeMap = getNodeMapFromAllTypes();
        return nodeMap;
    }

    Map getNodeMapFromUserTypes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "getNodeMapFromUserTypes()", this);

            OracleLog.recursiveTrace = false;
        }

        Map nodeMap = new HashMap();
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            pstmt = this.connection.prepareStatement(getUserTypeTreeSql);
            pstmt.setString(1, this.sqlName.getSimpleName());
            rst = pstmt.executeQuery();

            while (rst.next()) {
                int depth = rst.getInt(1);
                String parentTypeName = rst.getString(2);
                String childTypeName = rst.getString(3);
                int attrNumber = rst.getInt(4);
                String childTypeOwner = rst.getString(5);
                if ((childTypeOwner != null) && (!childTypeOwner.equals(this.sqlName.getSchema())))
                    throw new NodeMapException();
                if (parentTypeName.length() > 0) {
                    SQLName parentSqlName = new SQLName(this.sqlName.getSchema(), parentTypeName,
                            this.connection);
                    TypeTreeElement node = null;
                    if (nodeMap.containsKey(parentSqlName)) {
                        node = (TypeTreeElement) nodeMap.get(parentSqlName);
                    } else {
                        node = new TypeTreeElement(this.sqlName.getSchema(), parentTypeName);
                        nodeMap.put(parentSqlName, node);
                    }
                    node.putChild(this.sqlName.getSchema(), childTypeName, attrNumber);
                }
            }
        } catch (NodeMapException e) {
            nodeMap = null;
            e.printStackTrace(System.err);
        } finally {
            if (rst != null)
                rst.close();
            if (pstmt != null)
                pstmt.close();
        }
        return nodeMap;
    }

    Map getNodeMapFromAllTypes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "getNodeMapFromAllTypes()", this);

            OracleLog.recursiveTrace = false;
        }

        Map nodeMap = new HashMap();
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        try {
            pstmt = this.connection.prepareStatement(getAllTypeTreeSql);
            pstmt.setString(1, this.sqlName.getSimpleName());
            pstmt.setString(2, this.sqlName.getSchema());
            rst = pstmt.executeQuery();

            while (rst.next()) {
                String parentTypeName = rst.getString(1);
                String parentOwnerName = rst.getString(2);
                String childTypeName = rst.getString(3);
                int attrNumber = rst.getInt(4);
                String childOwnerName = rst.getString(5);
                if (childOwnerName == null)
                    childOwnerName = "SYS";
                if (parentTypeName.length() > 0) {
                    SQLName parentSQLName = new SQLName(parentOwnerName, parentTypeName,
                            this.connection);
                    TypeTreeElement node = null;
                    if (nodeMap.containsKey(parentSQLName)) {
                        node = (TypeTreeElement) nodeMap.get(parentSQLName);
                    } else {
                        node = new TypeTreeElement(parentOwnerName, parentTypeName);
                        nodeMap.put(parentSQLName, node);
                    }
                    node.putChild(childOwnerName, childTypeName, attrNumber);
                }
            }
        } finally {
            if (rst != null)
                rst.close();
            if (pstmt != null)
                pstmt.close();
        }
        return nodeMap;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleNamedType"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleNamedType JD-Core Version: 0.6.0
 */