package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.oracore.OracleNamedType;
import oracle.jdbc.oracore.OracleType;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCOLLECTION;
import oracle.jdbc.oracore.OracleTypeFLOAT;
import oracle.jdbc.oracore.OracleTypeNUMBER;
import oracle.jdbc.oracore.OracleTypeREF;

public class ArrayDescriptor extends TypeDescriptor implements Serializable {
    public static final int TYPE_VARRAY = 3;
    public static final int TYPE_NESTED_TABLE = 2;
    public static final int CACHE_NONE = 0;
    public static final int CACHE_ALL = 1;
    public static final int CACHE_LAST = 2;
    static final long serialVersionUID = 3838105394346513809L;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    public static ArrayDescriptor createDescriptor(String name, Connection conn)
            throws SQLException {
        return createDescriptor(name, conn, false, false);
    }

    public static ArrayDescriptor createDescriptor(String name, Connection conn, boolean recurse,
            boolean force) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.createDescriptor( name=" + name
                    + " conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((name == null) || (name.length() == 0) || (conn == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ArrayDescriptor.createDescriptor: Invalid argument, 'name' should not be an empty string and 'conn' should not be null. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        SQLName sqlName = new SQLName(name, (oracle.jdbc.OracleConnection) conn);
        return createDescriptor(sqlName, conn);
    }

    public static ArrayDescriptor createDescriptor(SQLName sqlName, Connection conn)
            throws SQLException {
        return createDescriptor(sqlName, conn, false, false);
    }

    public static ArrayDescriptor createDescriptor(SQLName sqlName, Connection conn,
            boolean recurse, boolean force) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.createDescriptor( sqlName="
                    + sqlName + " conn=" + conn + ")");

            OracleLog.recursiveTrace = false;
        }

        String qualifiedName = sqlName.getName();

        ArrayDescriptor descriptor = null;
        if (!force) {
            descriptor = (ArrayDescriptor) ((oracle.jdbc.OracleConnection) conn)
                    .getDescriptor(qualifiedName);
        }

        if (descriptor == null) {
            descriptor = new ArrayDescriptor(sqlName, conn);
            if (recurse)
                descriptor.initNamesRecursively();
            ((oracle.jdbc.OracleConnection) conn).putDescriptor(qualifiedName, descriptor);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.createDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public static ArrayDescriptor createDescriptor(OracleTypeCOLLECTION otype) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.createDescriptor( otype="
                    + otype + ")");

            OracleLog.recursiveTrace = false;
        }

        String fullName = otype.getFullName();
        oracle.jdbc.OracleConnection conn = otype.getConnection();
        ArrayDescriptor descriptor = (ArrayDescriptor) conn.getDescriptor(fullName);

        if (descriptor == null) {
            SQLName sqlName = new SQLName(otype.getSchemaName(), otype.getSimpleName(), otype
                    .getConnection());

            descriptor = new ArrayDescriptor(sqlName, otype, conn);
            conn.putDescriptor(fullName, descriptor);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.createDescriptor: return");

            OracleLog.recursiveTrace = false;
        }

        return descriptor;
    }

    public ArrayDescriptor(String name, Connection conn) throws SQLException {
        super(name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.ArrayDescriptor( name=" + name
                    + ", conn=" + conn + ")" + " -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        initPickler();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.ArrayDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public ArrayDescriptor(SQLName sqlName, Connection conn) throws SQLException {
        super(sqlName, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.ArrayDescriptor( sqlName="
                    + sqlName + ", conn=" + conn + ") -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        initPickler();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.ArrayDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public ArrayDescriptor(SQLName sqlName, OracleTypeCOLLECTION type, Connection conn)
            throws SQLException {
        super(sqlName, type, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.ArrayDescriptor( sqlName="
                    + sqlName + ", type=" + type + ", conn=" + conn
                    + ") -- after super() -- : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public ArrayDescriptor(OracleTypeCOLLECTION type, Connection conn) throws SQLException {
        super(type, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.ArrayDescriptor( type=" + type
                    + ", conn=" + conn + ") -- after super() -- : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static ArrayDescriptor createDescriptor(SQLName sqlName, byte[] typoid, int version,
            byte[] tds, byte[] lds, oracle.jdbc.internal.OracleConnection conn, byte[] fdo)
            throws SQLException {
        OracleTypeCOLLECTION pickler = new OracleTypeCOLLECTION(sqlName, typoid, version, tds, lds,
                conn, fdo);
        return new ArrayDescriptor(sqlName, pickler, conn);
    }

    public int getBaseType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getBaseType()", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = ((OracleTypeCOLLECTION) this.pickler).getElementType().getTypeCode();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getBaseType: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String getBaseName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getBaseName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;

        switch (getBaseType()) {
        case 12:
            ret = "VARCHAR";

            break;
        case 1:
            ret = "CHAR";

            break;
        case -2:
            ret = "RAW";

            break;
        case 6:
            ret = "FLOAT";

            break;
        case 2:
            ret = "NUMBER";

            break;
        case 8:
            ret = "DOUBLE";

            break;
        case 3:
            ret = "DECIMAL";

            break;
        case 91:
            ret = "DATE";

            break;
        case 93:
            ret = "TIMESTAMP";

            break;
        case -101:
            ret = "TIMESTAMPTZ";

            break;
        case -102:
            ret = "TIMESTAMPLTZ";

            break;
        case 2004:
            ret = "BLOB";

            break;
        case 2005:
            ret = "CLOB";

            break;
        case -13:
            ret = "BFILE";

            break;
        case 2002:
        case 2003:
        case 2007:
        case 2008:
        {
            OracleNamedType otype = (OracleNamedType) ((OracleTypeCOLLECTION) this.pickler)
                    .getElementType();

            ret = otype.getFullName();

            break;
        }
        case 2006:
        {
            OracleNamedType otype = (OracleNamedType) ((OracleTypeCOLLECTION) this.pickler)
                    .getElementType();

            ret = "REF " + ((OracleTypeREF) otype).getFullName();

            break;
        }
        case 1111:
        default:
            ret = null;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getBaseName: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public OracleTypeCOLLECTION getOracleTypeCOLLECTION() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE,
                         "ArrayDescriptor.OracleTypeCOLLECTION() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (OracleTypeCOLLECTION) this.pickler;
    }

    public int getArrayType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getArrayType()", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = ((OracleTypeCOLLECTION) this.pickler).getUserCode();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getArrayType: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public long getMaxLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getMaxLength()", this);

            OracleLog.recursiveTrace = false;
        }

        long ret = getArrayType() == 3 ? ((OracleTypeCOLLECTION) this.pickler).getMaxLength() : 0L;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getMaxLength: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public String descType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.descType()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;
        StringBuffer strBuf = new StringBuffer();

        ret = descType(strBuf, 0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.descType: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    String descType(StringBuffer strBuf, int level) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.descType( strBuf=" + strBuf
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

        int tcode = getBaseType();

        if ((tcode == 2002) || (tcode == 2008)) {
            StructDescriptor adt_desc = StructDescriptor.createDescriptor(getBaseName(),
                                                                          this.connection);

            adt_desc.descType(strBuf, level + 1);
        } else if (tcode == 2003) {
            ArrayDescriptor array_desc = createDescriptor(getBaseName(), this.connection);

            array_desc.descType(strBuf, level + 1);
        } else if (tcode == 2007) {
            OpaqueDescriptor opaque_desc = OpaqueDescriptor.createDescriptor(getBaseName(),
                                                                             this.connection);

            opaque_desc.descType(strBuf, level + 1);
        } else {
            strBuf.append(level_two);
            strBuf.append(getBaseName());
            strBuf.append("\n");
        }

        String ret = strBuf.substring(0, strBuf.length());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.descType: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    int toLength(ARRAY array) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toLength( array=" + array + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        if (array.numElems == -1) {
            if (array.datumArray != null) {
                array.numElems = array.datumArray.length;
            } else if (array.objArray != null) {
                if ((array.objArray instanceof Object[]))
                    array.numElems = ((Object[]) array.objArray).length;
                else if ((array.objArray instanceof int[]))
                    array.numElems = ((long[]) array.objArray).length;
                else if ((array.objArray instanceof long[]))
                    array.numElems = ((float[]) array.objArray).length;
                else if ((array.objArray instanceof float[]))
                    array.numElems = ((double[]) array.objArray).length;
                else if ((array.objArray instanceof double[]))
                    array.numElems = ((boolean[]) array.objArray).length;
                else if ((array.objArray instanceof boolean[]))
                    array.numElems = ((int[]) array.objArray).length;
                else if ((array.objArray instanceof byte[]))
                    array.numElems = ((byte[]) array.objArray).length;
                else if ((array.objArray instanceof short[]))
                    array.numElems = ((short[]) array.objArray).length;
                else if ((array.objArray instanceof char[]))
                    array.numElems = ((char[]) array.objArray).length;
            } else if (array.locator != null) {
                array.numElems = toLengthFromLocator(array.locator);
            } else if (array.shareBytes() != null) {
                this.pickler.unlinearize(array.shareBytes(), array.imageOffset, array, 0, null);

                if (array.numElems == -1) {
                    if (array.locator != null) {
                        array.numElems = toLengthFromLocator(array.locator);
                    } else {
                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.datumLogger
                                    .log(
                                         Level.SEVERE,
                                         "ArrayDescriptor.toLength: Unable to get array length. An exception is thrown.",
                                         this);

                            OracleLog.recursiveTrace = false;
                        }

                        DatabaseError.throwSqlException(1, "Unable to get array length");
                    }

                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "ArrayDescriptor.toLength: Array is in inconsistent status. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1, "Array is in inconsistent status");
            }

        }

        int ret = array.numElems;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toLength: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    byte[] toBytes(ARRAY s, boolean keepLocalCopy) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toBytes( s=" + s
                    + ", keepLocalCopy=" + keepLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] bytes = s.shareBytes();

        if (bytes == null) {
            if ((s.datumArray != null) || (s.locator != null)) {
                bytes = this.pickler.linearize(s);

                if (!keepLocalCopy) {
                    s.setShareBytes(null);
                }

            } else if (s.objArray != null) {
                s.datumArray = toOracleArray(s.objArray, 1L, -1);
                bytes = this.pickler.linearize(s);

                if (!keepLocalCopy) {
                    s.datumArray = null;

                    s.setShareBytes(null);
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "ArrayDescriptor.toBytes: Array is in inconsistent status. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1, "Array is in inconsistent status");
            }

        } else if (s.imageLength != 0L) {
            if ((s.imageOffset != 0L) || (s.imageLength != bytes.length)) {
                byte[] image = new byte[(int) s.imageLength];

                System.arraycopy(bytes, (int) s.imageOffset, image, 0, (int) s.imageLength);

                s.setImage(image, 0L, 0L);

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toBytes: return", this);

                    OracleLog.recursiveTrace = false;
                }

                return image;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return bytes;
    }

    Datum[] toOracleArray(ARRAY s, long beginIdx, int count, boolean keepLocalCopy)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toOracleArray( s=" + s
                    + ", beginIdx=" + beginIdx + ", count=" + count + ", keepLocalCopy="
                    + keepLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = s.datumArray;

        if (datumArray == null) {
            if (s.objArray != null) {
                datumArray = toOracleArray(s.objArray, beginIdx, count);
            } else if (s.locator != null) {
                datumArray = toOracleArrayFromLocator(s.locator, beginIdx, count, null);
            } else if (s.shareBytes() != null) {
                this.pickler
                        .unlinearize(s.shareBytes(), s.imageOffset, s, beginIdx, count, 1, null);

                if (s.locator != null) {
                    datumArray = toOracleArrayFromLocator(s.locator, beginIdx, count, null);
                } else {
                    datumArray = s.datumArray;
                }

                if (!keepLocalCopy) {
                    s.datumArray = null;
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "ArrayDescriptor.toOracleArray: Array is in inconsistent status. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }
        } else {
            if (beginIdx > datumArray.length) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toOracleArray: return",
                                              this);

                    OracleLog.recursiveTrace = false;
                }

                return new Datum[0];
            }

            int length = (int) (count == -1 ? datumArray.length - beginIdx + 1L : Math
                    .min(datumArray.length - beginIdx + 1L, count));

            datumArray = new Datum[length];

            System.arraycopy(s.datumArray, (int) beginIdx - 1, datumArray, 0, length);
        }

        Datum[] ret = null;

        if (keepLocalCopy) {
            s.datumArray = datumArray;
            ret = (Datum[]) datumArray.clone();
        } else {
            ret = datumArray;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toOracleArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    Object[] toJavaArray(ARRAY s, long beginIdx, int count, Map map, boolean saveLocalCopy)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toJavaArray( s=" + s
                    + ", beginIdx=" + beginIdx + ", count=" + count + ", map=" + map
                    + ", saveLocalCopy=" + saveLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object[] objArray = null;

        if (s.objArray != null) {
            objArray = (Object[]) ((Object[]) s.objArray).clone();

            int nbElem = objArray.length;
            int length = (int) (count == -1 ? nbElem - beginIdx + 1L : Math.min(nbElem - beginIdx
                    + 1L, count));

            if (length <= 0) {
                Object[] ret = (Object[]) makeJavaArray(length, getBaseType());

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toJavaArray: return",
                                              this);

                    OracleLog.recursiveTrace = false;
                }

                return ret;
            }

            objArray = (Object[]) makeJavaArray(length, getBaseType());

            System.arraycopy(s.objArray, (int) beginIdx - 1, objArray, 0, length);
        } else {
            if (s.datumArray != null) {
                objArray = (Object[]) toJavaArray(s.datumArray, beginIdx, count, map);
            } else if (s.locator != null) {
                objArray = toArrayFromLocator(s.locator, beginIdx, count, map);
            } else if (s.shareBytes() != null) {
                this.pickler.unlinearize(s.shareBytes(), s.imageOffset, s, beginIdx, count, 2, map);

                if (s.locator != null)
                    objArray = toArrayFromLocator(s.locator, beginIdx, count, map);
                else {
                    objArray = (Object[]) s.objArray;
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "ArrayDescriptor.toJavaArray: Array is in inconsistent status. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

            if ((saveLocalCopy) && (getBaseType() != 2002) && (getBaseType() != 2008)
                    && (objArray != null)) {
                s.objArray = objArray.clone();
            } else
                s.objArray = null;

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toJavaArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return objArray;
    }

    private Datum[] toOracleArrayFromLocator(byte[] locator_bytes, long beginIdx, int count, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toOracleArrayFromLocator( locator_bytes="
                                              + locator_bytes + ", beginIdx=" + beginIdx
                                              + ", count=" + count + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int nbElem = toLengthFromLocator(locator_bytes);
        int length = (int) (count == -1 ? nbElem - beginIdx + 1L : Math.min(nbElem - beginIdx + 1L,
                                                                            count));

        Datum[] datumArray = null;

        if (length <= 0) {
            datumArray = new Datum[0];
        } else {
            datumArray = new Datum[length];

            ResultSet rset = toResultSetFromLocator(locator_bytes, beginIdx, count, map);

            for (int i = 0; rset.next(); i++) {
                datumArray[i] = ((OracleResultSet) rset).getOracleObject(2);
            }
            rset.close();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toOracleArrayFromLocator: return", this);

            OracleLog.recursiveTrace = false;
        }

        return datumArray;
    }

    private Object[] toArrayFromLocator(byte[] locator_bytes, long beginIdx, int count, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toArrayFromLocator( locator_bytes="
                                              + locator_bytes + ", beginIdx=" + beginIdx
                                              + ", count=" + count + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int nbElem = toLengthFromLocator(locator_bytes);
        int length = (int) (count == -1 ? nbElem - beginIdx + 1L : Math.min(nbElem - beginIdx + 1L,
                                                                            count));

        Object[] objArray = null;

        if (length <= 0) {
            objArray = (Object[]) makeJavaArray(0, getBaseType());
        } else {
            objArray = (Object[]) makeJavaArray(length, getBaseType());

            ResultSet rset = toResultSetFromLocator(locator_bytes, beginIdx, count, map);

            for (int i = 0; rset.next(); i++) {
                objArray[i] = ((OracleResultSet) rset).getObject(2, map);
            }
            rset.close();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toArrayFromLocator: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return objArray;
    }

    public ResultSet toResultSet(ARRAY array, long index, int count, Map map, boolean saveLocalCopy)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet( array=" + array
                    + ", index=" + index + ", count=" + count + ", map=" + map + ", saveLocalCopy="
                    + saveLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSet rset = null;

        if (array.datumArray != null) {
            rset = toResultSet(array.datumArray, index, count, map);
        } else if (array.locator != null) {
            rset = toResultSetFromLocator(array.locator, index, count, map);
        } else if (array.objArray != null) {
            rset = toResultSet(toOracleArray(array.objArray, index, count), 1L, -1, map);
        } else if (array.shareBytes() != null) {
            if (((OracleTypeCOLLECTION) this.pickler).isInlineImage(array.shareBytes(),
                                                                    (int) array.imageOffset)) {
                rset = toResultSetFromImage(array, index, count, map);
            } else {
                this.pickler.unlinearize(array.shareBytes(), array.imageOffset, array, 1, null);

                if (array.locator != null) {
                    rset = toResultSetFromLocator(array.locator, index, count, map);
                } else {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.datumLogger
                                .log(
                                     Level.SEVERE,
                                     "ArrayDescriptor.toResultSet: Array is in inconsistent status. An exception is thrown.",
                                     this);

                        OracleLog.recursiveTrace = false;
                    }

                    DatabaseError.throwSqlException(1);
                }
            }
        }

        if (rset == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ArrayDescriptor.toResultSet: Unable to create array ResultSet. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "Unable to create array ResultSet");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet: return", this);

            OracleLog.recursiveTrace = false;
        }

        return rset;
    }

    public ResultSet toResultSet(Datum[] data, long index, int count, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet( data=" + data
                    + ", index=" + index + ", count=" + count + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSet ret = null;

        if (count == -1)
            ret = this.connection.newArrayDataResultSet(data, index, data.length, map);
        else {
            ret = this.connection.newArrayDataResultSet(data, index, count, map);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ResultSet toResultSetFromLocator(byte[] locator, long index, int count, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toResultSetFromLocator( locator=" + locator
                                              + ", index=" + index + ", count=" + count + ", map="
                                              + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSet ret = this.connection.newArrayLocatorResultSet(this, locator, index, count, map);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSetFromLocator: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ResultSet toResultSetFromImage(ARRAY array, long index, int count, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSetFromImage( array="
                    + array + ", index=" + index + ", count=" + count + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSet ret = this.connection.newArrayDataResultSet(array, index, count, map);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSetFromImage: return",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static Object[] makeJavaArray(int length, int otype) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.makeJavaArray( length=" + length
                    + ", otype=" + otype + ")");

            OracleLog.recursiveTrace = false;
        }

        Object[] ret = null;

        switch (otype) {
        case -7:
        case -6:
        case -5:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
            ret = new BigDecimal[length];

            break;
        case 1:
        case 12:
            ret = new String[length];

            break;
        case -102:
        case -101:
        case 91:
        case 92:
        case 93:
            ret = new Timestamp[length];

            break;
        case 2002:
        case 2008:
            ret = new Object[length];

            break;
        case -13:
            ret = new BFILE[length];

            break;
        case 2004:
            ret = new BLOB[length];

            break;
        case 2005:
            ret = new CLOB[length];

            break;
        case -3:
        case -2:
            ret = new byte[length][];

            break;
        case 2006:
            ret = new REF[length];

            break;
        case 2003:
            ret = new Object[length];

            break;
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "ArrayDescriptor.makeJavaArray: makeJavaArray doesn't support type "
                                                  + otype + ". An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "makeJavaArray doesn't support type " + otype);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.makeJavaArray: return");

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    private int toLengthFromLocator(byte[] locator_bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toLengthFromLocator( locator_bytes="
                                              + locator_bytes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ARRAY tmpObj = new ARRAY(this, this.connection, (byte[]) null);

        tmpObj.setLocator(locator_bytes);

        int numElems = 0;

        OraclePreparedStatement pstmt = null;
        OracleResultSet rs = null;

        pstmt = (OraclePreparedStatement) this.connection
                .prepareStatement("SELECT count(*) FROM TABLE( CAST(:1 AS " + getName() + ") )");

        pstmt.setArray(1, tmpObj);

        rs = (OracleResultSet) pstmt.executeQuery();

        if (rs.next()) {
            numElems = rs.getInt(1);
        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ArrayDescriptor.toLengthFromLocator: Fail to access array storage table. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1, "Fail to access array storage table");
        }

        rs.close();
        pstmt.close();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toLengthFromLocator: return: "
                    + numElems, this);

            OracleLog.recursiveTrace = false;
        }

        return numElems;
    }

    Datum[] toOracleArray(Object elements, long beginIdx, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toOracle( elements=" + elements
                    + ", beginIdx=" + beginIdx + ", count=" + count + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] ret = null;

        if (elements != null) {
            OracleType otype = getElementType();

            ret = otype.toDatumArray(elements, this.connection, beginIdx, count);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toOracle: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    private Object toJavaArray(Datum[] elements, long beginIdx, int count, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toJavaArray( elements="
                    + elements + ", beginIdx=" + beginIdx + ", count=" + count + ", map=" + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int length = (int) (count == -1 ? elements.length - beginIdx + 1L : Math
                .min(elements.length - beginIdx + 1L, count));

        if (length < 0) {
            length = 0;
        }
        Object[] objArray = (Object[]) makeJavaArray(length, getBaseType());

        if (getBaseType() == 2002) {
            STRUCT s = null;

            for (int i = 0; i < length; i++) {
                s = (STRUCT) elements[((int) beginIdx + i - 1)];
                objArray[i] = (s != null ? s.toJdbc(map) : null);
            }
        } else {
            Datum d = null;

            for (int i = 0; i < length; i++) {
                d = elements[((int) beginIdx + i - 1)];
                objArray[i] = (d != null ? d.toJdbc() : null);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toJavaArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return objArray;
    }

    private Object toNumericArray(Datum[] darray, long beginIdx, int count, int type)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toNumericArray( darray="
                    + darray + ", beginIdx=" + beginIdx + ", count=" + count + ", type=" + type
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = null;

        int length = (int) (count == -1 ? darray.length - beginIdx + 1L : Math.min(darray.length
                - beginIdx + 1L, count));

        if (length < 0) {
            length = 0;
        }
        switch (type) {
        case 4:
        {
            int[] objArray = new int[length];

            for (int i = 0; i < length; i++) {
                Datum d = darray[((int) beginIdx + i - 1)];

                if (d != null) {
                    objArray[i] = d.intValue();
                }
            }
            ret = objArray;
            break;
        }
        case 5:
        {
            double[] objArray = new double[length];

            for (int i = 0; i < length; i++) {
                Datum d = darray[((int) beginIdx + i - 1)];

                if (d != null) {
                    objArray[i] = d.doubleValue();
                }
            }
            ret = objArray;
            break;
        }
        case 6:
        {
            float[] objArray = new float[length];

            for (int i = 0; i < length; i++) {
                Datum d = darray[((int) beginIdx + i - 1)];

                if (d != null) {
                    objArray[i] = d.floatValue();
                }
            }
            ret = objArray;
            break;
        }
        case 7:
        {
            long[] objArray = new long[length];

            for (int i = 0; i < length; i++) {
                Datum d = darray[((int) beginIdx + i - 1)];

                if (d != null) {
                    objArray[i] = d.longValue();
                }
            }
            ret = objArray;
            break;
        }
        case 8:
            short[] objArray = new short[length];

            for (int i = 0; i < length; i++) {
                Datum d = darray[((int) beginIdx + i - 1)];

                if (d != null) {
                    objArray[i] = ((NUMBER) d).shortValue();
                }
            }
            ret = objArray;
            break;
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ArrayDescriptor.toNumericArray: This feature is not supported. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(23);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toNumericArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    private Object toNumericArrayFromLocator(byte[] locator_bytes, long beginIdx, int count,
            int type) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toNumericArrayFromLocator( locator_bytes="
                                              + locator_bytes + ", beginIdx=" + beginIdx
                                              + ", count=" + count + ", type=" + type + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = null;

        int nbElem = toLengthFromLocator(locator_bytes);

        ResultSet rs = toResultSetFromLocator(locator_bytes, beginIdx, count, null);

        int idx = 0;

        switch (type) {
        case 4:
        {
            int[] objArray = new int[nbElem];

            while ((rs.next()) && (idx < nbElem)) {
                objArray[(idx++)] = ((OracleResultSet) rs).getInt(2);
            }
            rs.close();

            ret = objArray;
            break;
        }
        case 5:
        {
            double[] objArray = new double[nbElem];

            while ((rs.next()) && (idx < nbElem)) {
                objArray[(idx++)] = ((OracleResultSet) rs).getDouble(2);
            }
            rs.close();

            ret = objArray;
            break;
        }
        case 6:
        {
            float[] objArray = new float[nbElem];

            while ((rs.next()) && (idx < nbElem)) {
                objArray[(idx++)] = ((OracleResultSet) rs).getFloat(2);
            }
            rs.close();

            ret = objArray;
            break;
        }
        case 7:
        {
            long[] objArray = new long[nbElem];

            while ((rs.next()) && (idx < nbElem)) {
                objArray[(idx++)] = ((OracleResultSet) rs).getLong(2);
            }
            rs.close();

            ret = objArray;
            break;
        }
        case 8:
        {
            short[] objArray = new short[nbElem];

            while ((rs.next()) && (idx < nbElem)) {
                objArray[(idx++)] = ((OracleResultSet) rs).getShort(2);
            }
            rs.close();

            ret = objArray;
            break;
        }
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ArrayDescriptor.toNumericArrayFromLocator: Unsupported feature. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(23);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ArrayDescriptor.toNumericArrayFromLocator: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    Object toNumericArray(ARRAY array, long beginIdx, int count, int type, boolean saveLocalCopy)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toNumericArray( array=" + array
                    + ", beginIdx=" + beginIdx + ", count=" + count + ", type=" + type
                    + ", saveLocalCopy=" + saveLocalCopy + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((!(getElementType() instanceof OracleTypeNUMBER))
                && (!(getElementType() instanceof OracleTypeFLOAT))) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ArrayDescriptor.toNumericArray(ARRAY, long, int, int, boolean): Unsupported feature. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(23);
        }

        Object objArray = null;

        if (array.objArray != null) {
            if ((type == 4) && ((array.objArray instanceof int[]))) {
                int length = ((int[]) array.objArray).length;

                if (beginIdx > length) {
                    return new int[0];
                }
                length = (int) (count == -1 ? length - beginIdx + 1L : Math.min(length - beginIdx
                        + 1L, count));

                int[] newOarray = new int[length];

                System.arraycopy(array.objArray, (int) beginIdx - 1, newOarray, 0, length);

                objArray = newOarray;
            } else if ((type == 5) && ((array.objArray instanceof double[]))) {
                int length = ((double[]) array.objArray).length;

                if (beginIdx > length) {
                    return new double[0];
                }
                length = (int) (count == -1 ? length - beginIdx + 1L : Math.min(length - beginIdx
                        + 1L, count));

                double[] newOarray = new double[length];

                System.arraycopy(array.objArray, (int) beginIdx - 1, newOarray, 0, length);

                objArray = newOarray;
            } else if ((type == 6) && ((array.objArray instanceof float[]))) {
                int length = ((float[]) array.objArray).length;

                if (beginIdx > length) {
                    return new float[0];
                }
                length = (int) (count == -1 ? length - beginIdx + 1L : Math.min(length - beginIdx
                        + 1L, count));

                float[] newOarray = new float[length];

                System.arraycopy(array.objArray, (int) beginIdx - 1, newOarray, 0, length);

                objArray = newOarray;
            } else if ((type == 7) && ((array.objArray instanceof long[]))) {
                int length = ((long[]) array.objArray).length;

                if (beginIdx > length) {
                    return new long[0];
                }
                length = (int) (count == -1 ? length - beginIdx + 1L : Math.min(length - beginIdx
                        + 1L, count));

                long[] newOarray = new long[length];

                System.arraycopy(array.objArray, (int) beginIdx - 1, newOarray, 0, length);

                objArray = newOarray;
            } else if ((type == 8) && ((array.objArray instanceof short[]))) {
                int length = ((short[]) array.objArray).length;

                if (beginIdx > length) {
                    return new short[0];
                }
                length = (int) (count == -1 ? length - beginIdx + 1L : Math.min(length - beginIdx
                        + 1L, count));

                short[] newOarray = new short[length];

                System.arraycopy(array.objArray, (int) beginIdx - 1, newOarray, 0, length);

                objArray = newOarray;
            }

        } else {
            if (array.datumArray != null) {
                objArray = toNumericArray(array.datumArray, beginIdx, count, type);
            } else if (array.locator != null) {
                objArray = toNumericArrayFromLocator(array.locator, beginIdx, count, type);
            } else if (array.shareBytes() != null) {
                this.pickler.unlinearize(array.shareBytes(), array.imageOffset, array, beginIdx,
                                         count, type, null);

                if (array.locator != null) {
                    objArray = toNumericArrayFromLocator(array.locator, beginIdx, count, type);
                } else {
                    objArray = array.objArray;
                }

            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "ArrayDescriptor.toNumericArray(ARRAY, long, int, int, boolean): Unsupported feature. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(1);
            }

            if (!saveLocalCopy) {
                array.objArray = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toNumericArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return objArray;
    }

    private void initPickler() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.initPickler()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            OracleTypeADT adt = new OracleTypeADT(getName(), this.connection);

            adt.init(this.connection);

            this.pickler = ((OracleTypeCOLLECTION) adt.cleanup());

            this.pickler.setDescriptor(this);
        } catch (Exception e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "ArrayDescriptor.initPickler: Exception caught and thrown."
                                                  + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            if ((e instanceof SQLException)) {
                throw ((SQLException) e);
            }

            DatabaseError.throwSqlException(60, "Unable to resolve type: \"" + getName() + "\"");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.initPickler: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private OracleType getElementType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getElementType()", this);

            OracleLog.recursiveTrace = false;
        }

        OracleType ret = ((OracleTypeCOLLECTION) this.pickler).getElementType();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getElementType: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int getTypeCode() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getTypeCode()", this);

            OracleLog.recursiveTrace = false;
        }

        int ret = 2003;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getTypeCode: return: " + ret,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public byte[] toBytes(Datum[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toBytes( attributes="
                    + attributes + ") -- DEPRECATED --, -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        ARRAY s = new ARRAY(this, this.connection, attributes);

        return this.pickler.linearize(s);
    }

    public byte[] toBytes(Object[] attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toBytes( attributes="
                    + attributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datums = toArray(attributes);
        byte[] ret = toBytes(datums);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toBytes: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int length(byte[] bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.length( bytes=" + bytes + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        ARRAY tmpObj = new ARRAY(this, this.connection, bytes);
        int ret = toLength(tmpObj);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.length: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Datum[] toArray(byte[] bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toArray( bytes=" + bytes + ")",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] ret = null;
        if (bytes != null) {
            ARRAY tmpObj = new ARRAY(this, this.connection, bytes);

            ret = toOracleArray(tmpObj, 1L, -1, false);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Datum[] toArray(Object attributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toArray( attributes="
                    + attributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] ret = toOracleArray(attributes, 1L, -1);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toArray: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ResultSet toResultSet(byte[] bytes, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet( bytes=" + bytes
                    + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSet ret = null;
        if (bytes != null) {
            ARRAY tmpObj = (ARRAY) this.pickler.unlinearize(bytes, 0L, null, 1, null);

            ret = toResultSet(tmpObj, 1L, -1, map, false);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public ResultSet toResultSet(byte[] bytes, long index, int count, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet( bytes=" + bytes
                    + ", index=" + index + ", count=" + count + ", map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        ResultSet ret = null;

        if (bytes != null) {
            ARRAY tmpObj = (ARRAY) this.pickler.unlinearize(bytes, 0L, (ARRAY) null, 1, null);

            ret = toResultSet(tmpObj, index, count, map, false);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.toResultSet: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static int getCacheStyle(ARRAY array) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getCacheStyle( array=" + array
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        int ret = 2;

        if ((array.getAutoIndexing())
                && ((array.getAccessDirection() == 2) || (array.getAccessDirection() == 3))) {
            ret = 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.getCacheStyle: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.writeObject( out=" + out
                    + ") -- do nothing", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ArrayDescriptor.readObject( in=" + in
                    + ") -- do nothing", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.ArrayDescriptor"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.ArrayDescriptor JD-Core Version: 0.6.0
 */