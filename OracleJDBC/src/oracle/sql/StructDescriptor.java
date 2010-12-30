package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLOutput;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;

public class StructDescriptor extends TypeDescriptor implements Serializable {
    static final boolean DEBUG = false;
    static final long serialVersionUID = 1013921343538311063L;
    transient Boolean isInstanciable = null;
    transient String supertype = null;
    transient int numLocalAttrs = -1;
    transient String[] subtypes = null;
    transient String[] attrJavaNames = null;

    final int LOCAL_TYPE = 0;
    final int LOOK_FOR_USER_SYNONYM = 1;
    final int LOOK_FOR_PUBLIC_SYNONYM = 2;
    final String[] initMetaData1_9_0_SQL = {
            "SELECT INSTANTIABLE, supertype_owner, supertype_name, LOCAL_ATTRIBUTES FROM all_types WHERE type_name = :1 AND owner = :2 ",
            "DECLARE \n bind_synonym_name user_synonyms.synonym_name%type := :1; \n the_table_owner  user_synonyms.table_owner%type; \n the_table_name   user_synonyms.table_name%type; \n the_db_link      user_synonyms.db_link%type; \n sql_string       VARCHAR2(1000); \nBEGIN \n   SELECT /*+RULE*/ TABLE_NAME, TABLE_OWNER, DB_LINK INTO  \n         the_table_name, the_table_owner, the_db_link \n         FROM USER_SYNONYMS WHERE \n         SYNONYM_NAME = bind_synonym_name; \n \n   sql_string := 'SELECT /*+RULE*/ INSTANTIABLE, SUPERTYPE_OWNER,      SUPERTYPE_NAME, LOCAL_ATTRIBUTES FROM ALL_TYPES'; \n \n   IF the_db_link IS NOT NULL  \n   THEN \n     sql_string := sql_string || '@' || the_db_link; \n   END IF; \n   sql_string := sql_string       || ' WHERE TYPE_NAME = '''       || the_table_name   || ''' AND OWNER = '''       || the_table_owner  || ''''; \n   OPEN :2 FOR sql_string; \nEND;",
            "DECLARE \n bind_synonym_name user_synonyms.synonym_name%type := :1; \n the_table_owner  user_synonyms.table_owner%type; \n the_table_name   user_synonyms.table_name%type; \n the_db_link      user_synonyms.db_link%type; \n sql_string       VARCHAR2(1000); \nBEGIN \n   SELECT /*+RULE*/ TABLE_NAME, TABLE_OWNER, DB_LINK INTO  \n         the_table_name, the_table_owner, the_db_link \n         FROM ALL_SYNONYMS WHERE \n         OWNER = 'PUBLIC' AND \n         SYNONYM_NAME = bind_synonym_name; \n \n   sql_string := 'SELECT /*+RULE*/ INSTANTIABLE, SUPERTYPE_OWNER,      SUPERTYPE_NAME, LOCAL_ATTRIBUTES FROM ALL_TYPES'; \n \n   IF the_db_link IS NOT NULL  \n   THEN \n     sql_string := sql_string || '@' || the_db_link; \n   END IF; \n   sql_string := sql_string       || ' WHERE TYPE_NAME = '''       || the_table_name   || ''' AND OWNER = '''       || the_table_owner  || ''''; \n   OPEN :2 FOR sql_string; \nEND;" };

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public static StructDescriptor createDescriptor(String name, Connection conn)
            throws SQLException {
        return createDescriptor(name, conn, false, false);
    }

    public static StructDescriptor createDescriptor(String name, Connection conn, boolean recurse,
            boolean force) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.createDescriptor( name=" + name
                    + ", conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((name == null) || (name.length() == 0) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "StructDescriptor.createDescriptor: Invalid arguments, 'name' should not be an empty string and conn should not be null. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        SQLName sqlName = new SQLName(name, (oracle.jdbc.OracleConnection) conn);

        return createDescriptor(sqlName, conn, recurse, force);
    }

    public static StructDescriptor createDescriptor(SQLName sqlName, Connection conn,
            boolean recurse, boolean force) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.createDescriptor( sqlName="
                    + sqlName + ", conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        String qualifiedName = sqlName.getName();
        StructDescriptor descriptor = null;
        if (!force) {
            descriptor = (StructDescriptor) ((oracle.jdbc.OracleConnection) conn)
                    .getDescriptor(qualifiedName);

            if (descriptor == null) {
                descriptor = new StructDescriptor(sqlName, conn);
                if (recurse)
                    descriptor.initNamesRecursively();
                ((oracle.jdbc.OracleConnection) conn).putDescriptor(qualifiedName, descriptor);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.createDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public static StructDescriptor createDescriptor(SQLName name, Connection conn)
            throws SQLException {
        return createDescriptor(name, conn, false, false);
    }

    public static StructDescriptor createDescriptor(OracleTypeADT otype) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.createDescriptor( otype="
                    + otype + ")");

            OracleLog.recursiveTrace = false;
        }

        String fullName = otype.getFullName();
        oracle.jdbc.OracleConnection conn = otype.getConnection();
        StructDescriptor descriptor = (StructDescriptor) conn.getDescriptor(fullName);

        if (descriptor == null) {
            SQLName sqlName = new SQLName(otype.getSchemaName(), otype.getSimpleName(), otype
                    .getConnection());

            descriptor = new StructDescriptor(sqlName, otype, conn);
            descriptor.initNamesRecursively();
            conn.putDescriptor(fullName, descriptor);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.createDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public StructDescriptor(String name, Connection conn) throws SQLException {
        super(name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.StructDescriptor( name=" + name
                    + ", conn=" + conn + ")" + " -- after super() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        initPickler();
    }

    public StructDescriptor(SQLName name, Connection conn) throws SQLException {
        super(name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.StructDescriptor( name=" + name
                    + ", conn=" + conn + ")" + " -- after super() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        initPickler();
    }

    public StructDescriptor(SQLName name, OracleTypeADT type, Connection conn) throws SQLException {
        super(name, type, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.StructDescriptor( name=" + name
                    + ", type=" + type + ", conn=" + conn + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static StructDescriptor createDescriptor(SQLName sqlName, byte[] typoid, int version,
            byte[] tds, byte[] lds, oracle.jdbc.internal.OracleConnection conn, byte[] fdo)
            throws SQLException {
        OracleTypeADT pickler = new OracleTypeADT(sqlName, typoid, version, tds, lds, conn, fdo);
        return new StructDescriptor(sqlName, pickler, conn);
    }

    private void initPickler() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initPickler()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.pickler = new OracleTypeADT(getName(), this.connection);

            ((OracleTypeADT) this.pickler).init(this.connection);

            this.pickler.setDescriptor(this);
        } catch (Exception e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "StructDescriptor.initPickler: Fail to create descriptor. Exception caught and thrown."
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
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initPickler: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public StructDescriptor(OracleTypeADT type, Connection conn) throws SQLException {
        super(type, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.StructDescriptor( type=" + type
                    + ", conn=" + conn + "): return -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public int getTypeCode() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getTypeCode()", this);

            OracleLog.recursiveTrace = false;
        }

        int typeCode = this.pickler.getTypeCode();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getTypeCode: return: "
                    + typeCode, this);

            OracleLog.recursiveTrace = false;
        }

        return typeCode;
    }

    public int getTypeVersion() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getTypeVersion()", this);

            OracleLog.recursiveTrace = false;
        }

        int typeVersion = this.pickler.getTypeVersion();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getTypeVersion: return: "
                    + typeVersion, this);

            OracleLog.recursiveTrace = false;
        }

        return typeVersion;
    }

    byte[] toBytes(STRUCT s, boolean keepLocalCopy) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toBytes( s=" + s
                    + ", keepLocalCopy=" + keepLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINER, "StructDescriptor.toBytes: name of this =  "
                    + getName() + " typeCode = " + getTypeCode(), this);

            OracleLog.recursiveTrace = false;
        }

        byte[] bytes = s.shareBytes();

        if (bytes == null) {
            if (s.datumArray != null) {
                bytes = this.pickler.linearize(s);

                if (!keepLocalCopy) {
                    s.setShareBytes(null);
                }

            } else if (s.objectArray != null) {
                s.datumArray = toOracleArray(s.objectArray);
                bytes = this.pickler.linearize(s);

                if (!keepLocalCopy) {
                    s.datumArray = null;

                    s.setShareBytes(null);
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "StructDescriptor.toBytes: EOJ error. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

        } else if (s.imageLength != 0L) {
            if ((s.imageOffset != 0L) || (s.imageLength != bytes.length)) {
                byte[] image = new byte[(int) s.imageLength];

                System.arraycopy(bytes, (int) s.imageOffset, image, 0, (int) s.imageLength);

                s.setImage(image, 0L, 0L);

                bytes = image;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return bytes;
    }

    Datum[] toOracleArray(STRUCT s, boolean keepLocalCopy) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toOracleArray( s=" + s
                    + ", keepLocalCopy=" + keepLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = s.datumArray;
        Datum[] ret = null;

        if (datumArray == null) {
            if (s.objectArray != null) {
                datumArray = toOracleArray(s.objectArray);
            } else if (s.shareBytes() != null) {
                if (((s.shareBytes()[0] & 0x80) <= 0)
                        && (((OracleTypeADT) this.pickler).isEmbeddedADT())) {
                    this.pickler = OracleTypeADT.shallowClone((OracleTypeADT) this.pickler);
                }

                this.pickler.unlinearize(s.shareBytes(), s.imageOffset, s, 1, null);

                datumArray = s.datumArray;

                if (!keepLocalCopy) {
                    s.datumArray = null;
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "StructDescriptor.toOracleArray: EOJ error. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

        }

        if (keepLocalCopy) {
            s.datumArray = datumArray;
            ret = (Datum[]) datumArray.clone();
        } else {
            ret = datumArray;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toOracleArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    Object[] toArray(STRUCT s, Map map, boolean saveLocalCopy) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toArray( s=" + s + ", map="
                    + map + ", saveLocalCopy=" + saveLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object[] objArray = null;

        if (s.objectArray == null) {
            if (s.datumArray != null) {
                objArray = new Object[s.datumArray.length];

                for (int i = 0; i < s.datumArray.length; i++) {
                    if (s.datumArray[i] == null)
                        continue;
                    if ((s.datumArray[i] instanceof STRUCT))
                        objArray[i] = ((STRUCT) s.datumArray[i]).toJdbc(map);
                    else {
                        objArray[i] = s.datumArray[i].toJdbc();
                    }
                }
            } else if (s.shareBytes() != null) {
                if (((s.shareBytes()[0] & 0x80) <= 0)
                        && (((OracleTypeADT) this.pickler).isEmbeddedADT())) {
                    this.pickler = OracleTypeADT.shallowClone((OracleTypeADT) this.pickler);
                }

                this.pickler.unlinearize(s.shareBytes(), s.imageOffset, s, 2, map);

                objArray = s.objectArray;

                s.objectArray = null;
            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "StructDescriptor.toArray: EOJ error. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

        } else {
            objArray = (Object[]) s.objectArray.clone();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return objArray;
    }

    public int getLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getLength()", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = getFieldTypes().length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getLength: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public OracleTypeADT getOracleTypeADT() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.getOracleTypeADT() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        OracleTypeADT ret = (OracleTypeADT) this.pickler;

        return ret;
    }

    private OracleType[] getFieldTypes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getFieldTypes()", this);

            OracleLog.recursiveTrace = false;
        }

        OracleType[] ret = ((OracleTypeADT) this.pickler).getAttrTypes();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getFieldTypes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public SQLInput toJdbc2SQLInput(STRUCT s, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toJdbc2SQLInput( s=" + s
                    + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        SQLInput ret = new OracleJdbc2SQLInput(toOracleArray(s, false), map, this.connection);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toJdbc2SQLInput: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public SQLOutput toJdbc2SQLOutput() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toJdbc2SQLOutput()", this);

            OracleLog.recursiveTrace = false;
        }

        SQLOutput ret = new OracleSQLOutput(this, this.connection);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "StructDescriptor.toJdbc2SQLOutput: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Datum[] toOracleArray(Object[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toOracleArray( attributes="
                    + attributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datum = null;

        if (attributes != null) {
            OracleType[] oracleTypes = getFieldTypes();
            int oracleTypesLen = oracleTypes.length;

            if (attributes.length != oracleTypesLen) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "StructDescriptor.toOracleArray: Inconsistent ADT. An exception in thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(49, null);
            }

            datum = new Datum[oracleTypesLen];

            oracle.jdbc.internal.OracleConnection iconn = this.connection;

            for (int i = 0; i < oracleTypesLen; i++) {
                datum[i] = oracleTypes[i].toDatum(attributes[i], iconn);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toOracleArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return datum;
    }

    public Datum[] toOracleArray(Map attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toOracleArray( attributes="
                    + attributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datum = null;
        int nonNullAttrCount = 0;

        if (attributes != null) {
            OracleType[] oracleTypes = getFieldTypes();
            int oracleTypesLen = oracleTypes.length;
            int attributesSize = attributes.size();

            datum = new Datum[oracleTypesLen];

            oracle.jdbc.internal.OracleConnection iconn = this.connection;

            for (int i = 0; i < oracleTypesLen; i++) {
                Object o = attributes.get(((OracleTypeADT) this.pickler).getAttributeName(i + 1));

                datum[i] = oracleTypes[i].toDatum(o, iconn);

                if ((o == null)
                        && (!attributes.containsKey(((OracleTypeADT) this.pickler)
                                .getAttributeName(i + 1)))) {
                    continue;
                }
                nonNullAttrCount++;
            }

            if (nonNullAttrCount < attributesSize) {
                DatabaseError.throwSqlException(68, null);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toOracleArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return datum;
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getMetaData()", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSetMetaData ret = this.connection.newStructMetaData(this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getMetaData: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isFinalType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isFinalType()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = getOracleTypeADT().isFinalType();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isFinalType: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isSubtype() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isSubtype()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = getOracleTypeADT().isSubType();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isSubtype: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isInHierarchyOf(String checkThisName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.isInHierarchyOf( checkThisName="
                                              + checkThisName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        StructDescriptor currentDescriptor = this;
        String currentName = currentDescriptor.getName();
        boolean ret = false;

        if (checkThisName.equals(currentName)) {
            ret = true;
        } else {
            while (true) {
                currentName = currentDescriptor.getSupertypeName();

                if (currentName == null) {
                    ret = false;

                    break;
                }

                if (checkThisName.equals(currentName)) {
                    ret = true;

                    break;
                }

                currentDescriptor = createDescriptor(currentName, this.connection);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isInHierarchyOf: return: "
                    + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isInstantiable() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isInstantiable()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.isInstanciable == null) {
            initMetaData1();
        }
        boolean ret = this.isInstanciable.booleanValue();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.isInstantiable: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean isJavaObject() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isJavaObject()", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = getOracleTypeADT().isJavaObject();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.isJavaObject: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getSupertypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getSupertypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;

        if (isSubtype()) {
            if (this.supertype == null) {
                initMetaData1();
            }
            ret = this.supertype;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getSupertypeName: return: "
                    + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int getLocalAttributeCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "StructDescriptor.getLocalAttributeCount()", this);

            OracleLog.recursiveTrace = false;
        }
        int ret;
        if (!isSubtype()) {
            ret = getOracleTypeADT().getAttrTypes().length;
        } else {
            if (this.numLocalAttrs == -1) {
                initMetaData1();
            }
            ret = this.numLocalAttrs;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.getLocalAttributeCount: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String[] getSubtypeNames() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getSubtypeNames()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.subtypes == null) {
            initMetaData2();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getSubtypeNames: return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.subtypes;
    }

    public String getJavaClassName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getJavaClassName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;

        if (isJavaObject()) {
            ret = getJavaObjectClassName(this.connection, this);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getJavaClassName: return: "
                    + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getAttributeJavaName(int column) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getAttributeJavaName( column="
                    + column + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;

        if (isJavaObject()) {
            if (this.attrJavaNames == null) {
                initMetaData3();
            }
            ret = this.attrJavaNames[column];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getAttributeJavaName: return: "
                    + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String[] getAttributeJavaNames() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getAttributeJavaNames()", this);

            OracleLog.recursiveTrace = false;
        }

        String[] ret = null;

        if (isJavaObject()) {
            if (this.attrJavaNames == null) {
                initMetaData3();
            }
            ret = this.attrJavaNames;
        } else {
            ret = new String[0];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.getAttributeJavaNames: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getLanguage() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getLanguage()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;

        if (isJavaObject())
            ret = "JAVA";
        else {
            ret = "SQL";
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getLanguage: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Class getClass(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getClass( map=" + map + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        String qualifiedName = getName();

        Class c = (Class) map.get(qualifiedName);

        String schema = getSchemaName();
        String type = getTypeName();

        if (c == null) {
            if (this.connection.getUserName().equals(schema)) {
                c = (Class) map.get(type);
            }
        }
        if (SQLName.s_parseAllFormat) {
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

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getClass: return", this);

            OracleLog.recursiveTrace = false;
        }

        return c;
    }

    public static String getJavaObjectClassName(Connection conn, StructDescriptor desc)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getJavaObjectClassName( conn="
                    + conn + ", desc=" + desc + ")");

            OracleLog.recursiveTrace = false;
        }

        String ret = getJavaObjectClassName(conn, desc.getSchemaName(), desc.getTypeName());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.getJavaObjectClassName: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static String getJavaObjectClassName(Connection conn, String schema, String typename)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.getJavaObjectClassName( conn="
                    + conn + ", schema=" + schema + ", typename=" + typename + ")");

            OracleLog.recursiveTrace = false;
        }

        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String ret = null;
        try {
            pstmt = conn
                    .prepareStatement("select external_name from all_sqlj_types where owner = :1 and type_name = :2");

            pstmt.setString(1, schema);
            pstmt.setString(2, typename);

            rset = pstmt.executeQuery();

            if (rset.next()) {
                ret = rset.getString(1);
            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "StructDescriptor.getJavaObjectClassName: Invalid Java object. An excption is thrown.");

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(100);
            }

        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "StructDescriptor.getJavaObjectClassName: SQL exception caught."
                                                  + e.getMessage());

                OracleLog.recursiveTrace = false;
            }

        } finally {
            if (rset != null) {
                rset.close();
            }
            if (pstmt != null) {
                pstmt.close();
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.getJavaObjectClassName: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String descType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.descType() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        StringBuffer strBuf = new StringBuffer();

        return descType(strBuf, 0);
    }

    String descType(StringBuffer strBuf, int level) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.descType( strBuf=" + strBuf
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
        strBuf.append("\n");
        strBuf.append(level_one);
        strBuf.append("Subtype=" + getOracleTypeADT().isSubType());
        strBuf.append(" JavaObject=" + getOracleTypeADT().isJavaObject());
        strBuf.append(" FinalType=" + getOracleTypeADT().isFinalType());
        strBuf.append("\n");

        ResultSetMetaData md = getMetaData();
        int numCols = md.getColumnCount();

        for (int i = 0; i < numCols; i++) {
            int tcode = md.getColumnType(i + 1);

            if ((tcode == 2002) || (tcode == 2008)) {
                StructDescriptor adt_desc = createDescriptor(md.getColumnTypeName(i + 1),
                                                             this.connection);

                adt_desc.descType(strBuf, level + 1);
            } else if (tcode == 2003) {
                ArrayDescriptor array_desc = ArrayDescriptor.createDescriptor(md
                        .getColumnTypeName(i + 1), this.connection);

                array_desc.descType(strBuf, level + 1);
            } else if (tcode == 2007) {
                OpaqueDescriptor opq_desc = OpaqueDescriptor.createDescriptor(md
                        .getColumnTypeName(i + 1), this.connection);

                opq_desc.descType(strBuf, level + 1);
            } else {
                strBuf.append(level_two);
                strBuf.append(md.getColumnTypeName(i + 1));
                strBuf.append("\n");
            }
        }

        String ret = strBuf.substring(0, strBuf.length());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "StructDescriptor.descType: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public byte[] toBytes(Object[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toBytes( attributes="
                    + attributes + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datums = toOracleArray(attributes);

        return toBytes(datums);
    }

    /** @deprecated */
    public byte[] toBytes(Datum[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toBytes( attributes="
                    + attributes + ") -- DEPRECATED --" + " no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = new STRUCT(this, (byte[]) null, this.connection);

        s.setDatumArray(attributes);

        return this.pickler.linearize(s);
    }

    public Datum[] toArray(Object[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toArray( attributes="
                    + attributes + ") --" + " no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return toOracleArray(attributes);
    }

    public Datum[] toArray(byte[] bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.toArray( bytes=" + bytes
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = new STRUCT(this, bytes, this.connection);

        return toOracleArray(s, false);
    }

    private void initMetaData1() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.initMetaData1() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        short version = this.connection.getVersionNumber();

        if (version >= 9000)
            initMetaData1_9_0();
        else
            initMetaData1_pre_9_0();
    }

    private void initMetaData1_9_0() throws SQLException {
        int state = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initMetaData1_9_0()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                if (this.numLocalAttrs == -1) {
                    PreparedStatement pstmt = null;
                    OracleCallableStatement cstmt = null;
                    ResultSet rset = null;
                    int temp_numLocalAttrs = -1;
                    try {
                        label463: 
                        while (true) {
                            switch (state) {
                            case 0:
                                pstmt = this.connection
                                        .prepareStatement(this.initMetaData1_9_0_SQL[state]);

                                pstmt.setString(1, getTypeName());
                                pstmt.setString(2, getSchemaName());

                                pstmt.setFetchSize(1);
                                rset = pstmt.executeQuery();

                                break;
                            case 1:
                            case 2:
                                try {
                                    cstmt = (OracleCallableStatement) this.connection
                                            .prepareCall(this.initMetaData1_9_0_SQL[state]);

                                    cstmt.setString(1, getTypeName());
                                    cstmt.registerOutParameter(2, -10);

                                    cstmt.execute();

                                    rset = cstmt.getCursor(2);
                                    rset.setFetchSize(1);
                                } catch (SQLException ea) {
                                    if (ea.getErrorCode() == 1403) {
                                        if (state == 1) {
                                            cstmt.close();
                                            state++;
                                            continue;
                                        }

                                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                            OracleLog.recursiveTrace = true;
                                            OracleLog.datumLogger
                                                    .log(
                                                         Level.SEVERE,
                                                         "StructDescriptor.initMetaData1_9_0: Inconsistent catalog. An exception is thrown.",
                                                         this);

                                            OracleLog.recursiveTrace = false;
                                        }

                                        DatabaseError
                                                .throwSqlException(1, "Inconsistent catalog view");
                                    } else {
                                        throw ea;
                                    }

                                }

                            default:
                                if (rset.next()) {
                                    this.isInstanciable = new Boolean(rset.getString(1)
                                            .equals("YES"));
                                    this.supertype = (rset.getString(2) + "." + rset.getString(3));
                                    temp_numLocalAttrs = rset.getInt(4);

                                    break label463;
                                }

                                if (state == 2) {
                                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                        OracleLog.recursiveTrace = true;
                                        OracleLog.datumLogger
                                                .log(
                                                     Level.SEVERE,
                                                     "StructDescriptor.initMetaData1_9_0: Inconsistent catalog. An exception is thrown.",
                                                     this);

                                        OracleLog.recursiveTrace = false;
                                    }

                                    DatabaseError.throwSqlException(1, "Inconsistent catalog view");
                                    continue;
                                }

                                rset.close();
                                if (cstmt != null)
                                    cstmt.close();
                                state++;
                            }
                        }

                    } finally {
                        if (rset != null) {
                            rset.close();
                        }
                        if (pstmt != null) {
                            pstmt.close();
                        }
                        if (cstmt != null) {
                            cstmt.close();
                        }
                    }
                    this.numLocalAttrs = temp_numLocalAttrs;
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initMetaData1_9_0: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }
    }

    private synchronized void initMetaData1_pre_9_0() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.initMetaData1_pre_9_0(): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.isInstanciable = new Boolean(true);
        this.supertype = "";
        this.numLocalAttrs = 0;
    }

    private void initMetaData2() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.initMetaData2() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        short version = this.connection.getVersionNumber();

        if (version >= 9000) {
            initMetaData2_9_0();
        } else {
            initMetaData2_pre_9_0();
        }
    }

    private void initMetaData2_9_0() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initMetaData2_9_0()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                if (this.subtypes == null) {
                    PreparedStatement pstmt = null;
                    ResultSet rset = null;
                    String[] temp_names_array = null;
                    try {
                        pstmt = this.connection
                                .prepareStatement("select owner, type_name from all_types where supertype_name = :1 and supertype_owner = :2");

                        pstmt.setString(1, getTypeName());
                        pstmt.setString(2, getSchemaName());

                        rset = pstmt.executeQuery();

                        Vector _vector = new Vector();

                        while (rset.next()) {
                            _vector.addElement(rset.getString(1) + "." + rset.getString(2));
                        }
                        temp_names_array = new String[_vector.size()];

                        for (int i = 0; i < temp_names_array.length; i++) {
                            temp_names_array[i] = ((String) _vector.elementAt(i));
                        }
                        _vector.removeAllElements();

                        _vector = null;
                    } finally {
                        if (rset != null) {
                            rset.close();
                        }
                        if (pstmt != null) {
                            pstmt.close();
                        }
                    }
                    this.subtypes = temp_names_array;
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initMetaData2_9_0: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void initMetaData2_pre_9_0() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "StructDescriptor.initMetaData2_pre_9_0(): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.subtypes = new String[0];
    }

    private void initMetaData3() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initMetaData3()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                if (this.attrJavaNames == null) {
                    String[] temp_attrJavaNames = null;
                    PreparedStatement pstmt = null;
                    ResultSet rset = null;
                    try {
                        pstmt = this.connection
                                .prepareStatement("select EXTERNAL_ATTR_NAME from all_sqlj_type_attrs where owner = :1 and type_name = :2");

                        pstmt.setString(1, getSchemaName());
                        pstmt.setString(2, getTypeName());

                        rset = pstmt.executeQuery();
                        temp_attrJavaNames = new String[getOracleTypeADT().getAttrTypes().length];

                        for (int i = 0; rset.next(); i++)
                            temp_attrJavaNames[i] = rset.getString(1);
                    } finally {
                        if (rset != null) {
                            rset.close();
                        }
                        if (pstmt != null) {
                            pstmt.close();
                        }
                    }
                    this.attrJavaNames = temp_attrJavaNames;
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.initMetaData3: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.writeObject( out=" + out
                    + "): return -- do nothing --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "StructDescriptor.readObject( in=" + in
                    + "): return -- do nothing --", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.StructDescriptor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.StructDescriptor JD-Core Version: 0.6.0
 */