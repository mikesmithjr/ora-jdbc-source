package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.OPAQUE;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class OracleTypeUPT extends OracleTypeADT implements Serializable {
    static final long serialVersionUID = -1994358478872378695L;
    static final byte KOPU_UPT_ADT = -6;
    static final byte KOPU_UPT_COLL = -5;
    static final byte KOPU_UPT_REFCUR = 102;
    static final byte KOTTCOPQ = 58;
    byte uptCode = 0;
    OracleNamedType realType = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    protected OracleTypeUPT() {
    }

    public OracleTypeUPT(String sql_name, OracleConnection conn) throws SQLException {
        super(sql_name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT (sql_name = " + sql_name
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeUPT(OracleTypeADT parent, int id, OracleConnection conn) throws SQLException {
        super(parent, id, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT (parent = " + parent + ", id = "
                    + id + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.toDatum(value = " + value
                    + ", connection =" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value != null) {
            return this.realType.toDatum(value, conn);
        }
        return null;
    }

    public Datum[] toDatumArray(Object value, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.toDatum(value = " + value
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value != null) {
            return this.realType.toDatumArray(value, conn, beginIdx, count);
        }
        return null;
    }

    public int getTypeCode() throws SQLException {
        switch (this.uptCode) {
        case -6:
            return this.realType.getTypeCode();
        case -5:
            return 2003;
        case 58:
            return 2007;
        }

        DatabaseError.throwSqlException(1, "Invalid type code");

        return 0;
    }

    public boolean isInHierarchyOf(OracleType anOracleType) throws SQLException {
        return false;
    }

    public boolean isInHierarchyOf(StructDescriptor aStructDescriptor) throws SQLException {
        return false;
    }

    public boolean isObjectType() {
        return false;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.parseTDSrec(" + tdsReader + ") "
                    + tdsReader, this);

            OracleLog.recursiveTrace = false;
        }

        this.nullOffset = tdsReader.nullOffset;
        tdsReader.nullOffset += 1;
        this.ldsOffset = tdsReader.ldsOffset;
        tdsReader.ldsOffset += 1;

        long offsetPosition = tdsReader.readLong();

        this.uptCode = tdsReader.readByte();

        tdsReader.addNormalPatch(offsetPosition, this.uptCode, this);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeUPT.parseTDSrec() " + this.sqlName
                    + " uptCode=" + this.uptCode, this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int style, Map map)
            throws SQLException {
        switch (format) {
        case 2:
            switch (this.uptCode) {
            case -5:
                if ((context.readByte() & 0x1) == 1) {
                    context.skipBytes(2);

                    return null;
                }

                return ((OracleTypeCOLLECTION) this.realType).unpickle80(context, (ARRAY) null,
                                                                         style == 9 ? style : 3,
                                                                         style, map);
            case -6:
                if ((context.readByte() & 0x1) == 1) {
                    context.skipBytes(4);

                    return null;
                }

                switch (style) {
                case 1:
                    return ((OracleTypeADT) this.realType).unpickle80(context, (STRUCT) null, 3,
                                                                      style, map);
                case 2:
                    STRUCT result = ((OracleTypeADT) this.realType).unpickle80(context,
                                                                               (STRUCT) null, 1,
                                                                               style, map);

                    return result == null ? result : result.toJdbc(map);
                case 9:
                    return ((OracleTypeADT) this.realType).unpickle80(context, (STRUCT) null, 9, 1,
                                                                      map);
                }

                DatabaseError.throwSqlException(1);

                break;
            default:
                DatabaseError.throwSqlException(48, "upt_type");
            }

            break;
        case 3:
            switch (this.uptCode) {
            case -5:
                return ((OracleTypeCOLLECTION) this.realType).unpickle80(context, (ARRAY) null,
                                                                         style == 9 ? style : 3,
                                                                         style, map);
            case -6:
                switch (style) {
                case 1:
                    return ((OracleTypeADT) this.realType).unpickle80(context, (STRUCT) null, 3,
                                                                      style, map);
                case 2:
                    STRUCT result = ((OracleTypeADT) this.realType).unpickle80(context,
                                                                               (STRUCT) null, 1,
                                                                               style, map);

                    return result == null ? result : result.toJdbc(map);
                case 9:
                    return ((OracleTypeADT) this.realType).unpickle80(context, (STRUCT) null, 9, 1,
                                                                      map);
                }

                DatabaseError.throwSqlException(1);

                break;
            default:
                DatabaseError.throwSqlException(48, "upt_type");
            }

            break;
        case 1:
            switch (this.uptCode) {
            case -5:
                if (context.isNull(this.nullOffset)) {
                    context.skipBytes(4);

                    return null;
                }

                context.skipTo(context.ldsOffsets[this.ldsOffset]);

                if (style == 9) {
                    context.skipBytes(4);

                    return null;
                }

                context.markAndSkip();

                ARRAY result = ((OracleTypeCOLLECTION) this.realType).unpickle80(context,
                                                                                 (ARRAY) null, 3,
                                                                                 style, map);

                context.reset();

                return result;
            }

            DatabaseError.throwSqlException(1, "upt_type");

            break;
        default:
            DatabaseError.throwSqlException(1, "upt_type");
        }

        return null;
    }

    protected int pickle81(PickleContext ctx, Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.pickle81(context = " + ctx
                    + ", datum = " + data + ") " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        int imglen = 0;

        if (data == null) {
            imglen += ctx.writeElementNull();
        } else {
            int lenOffset = ctx.offset();

            imglen += ctx.writeLength(PickleContext.KOPI20_LN_MAXV + 1);

            int realLen = 0;

            if ((this.uptCode == -6) && (!((OracleTypeADT) this.realType).isFinalType())) {
                if ((data instanceof STRUCT)) {
                    realLen = ((STRUCT) data).getDescriptor().getOracleTypeADT()
                            .pickle81(ctx, data);
                } else {
                    DatabaseError.throwSqlException(1, "invalid upt state");
                }
            } else {
                realLen = this.realType.pickle81(ctx, data);
            }
            imglen += realLen;

            ctx.patchImageLen(lenOffset, realLen);
        }

        return imglen;
    }

    protected Object unpickle81rec(PickleContext context, int type, Map map) throws SQLException {
        OracleLog.print(this, 16, 8, 32, "OracleTypeUPT.unpickle81rec(" + context + ") "
                + this.sqlName);

        byte lengthbyte = context.readByte();

        if (PickleContext.isElementNull(lengthbyte)) {
            return null;
        }
        if (type == 9) {
            context.skipBytes(context.readRestOfLength(lengthbyte));

            return null;
        }

        context.skipRestOfLength(lengthbyte);

        return unpickle81UPT(context, type, map);
    }

    protected Object unpickle81rec(PickleContext context, byte length, int type, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.unpickle81rec(context = " + context
                    + ", length =" + length + ", sqlName = " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        long len = context.readRestOfLength(length);

        if (type == 9) {
            context.skipBytes((int) len);

            return null;
        }

        return unpickle81UPT(context, type, map);
    }

    private Object unpickle81UPT(PickleContext context, int style, Map map) throws SQLException {
        switch (this.uptCode) {
        case -6:
            switch (style) {
            case 1:
                return ((OracleTypeADT) this.realType).unpickle81(context, (STRUCT) null, 3, style,
                                                                  map);
            case 2:
                STRUCT result = ((OracleTypeADT) this.realType).unpickle81(context, (STRUCT) null,
                                                                           1, style, map);

                return result == null ? result : result.toJdbc(map);
            case 9:
                return ((OracleTypeADT) this.realType)
                        .unpickle81(context, (STRUCT) null, 9, 1, map);
            }

            DatabaseError.throwSqlException(1);

            break;
        case -5:
            return ((OracleTypeCOLLECTION) this.realType).unpickle81(context, (ARRAY) null,
                                                                     style == 9 ? style : 3, style,
                                                                     map);
        case 58:
            switch (style) {
            case 1:
            case 9:
                return ((OracleTypeOPAQUE) this.realType).unpickle81(context, (OPAQUE) null, style,
                                                                     map);
            case 2:
                OPAQUE result = ((OracleTypeOPAQUE) this.realType).unpickle81(context,
                                                                              (OPAQUE) null, style,
                                                                              map);

                return result == null ? result : result.toJdbc(map);
            }

            DatabaseError.throwSqlException(1);
        default:
            DatabaseError.throwSqlException(1, "Unrecognized UPT code");
        }

        return null;
    }

    protected Datum unpickle81datumAsNull(PickleContext context, byte len_flags, byte immemb)
            throws SQLException {
        return null;
    }

    StructDescriptor createStructDescriptor() throws SQLException {
        StructDescriptor desc = null;

        if (this.sqlName == null)
            desc = new StructDescriptor((OracleTypeADT) this.realType, this.connection);
        else {
            desc = StructDescriptor.createDescriptor(this.sqlName, this.connection);
        }
        return desc;
    }

    ArrayDescriptor createArrayDescriptor() throws SQLException {
        ArrayDescriptor desc = null;

        if (this.sqlName == null)
            desc = new ArrayDescriptor((OracleTypeCOLLECTION) this.realType, this.connection);
        else {
            desc = ArrayDescriptor.createDescriptor(this.sqlName, this.connection);
        }
        return desc;
    }

    public OracleType getRealType() throws SQLException {
        return this.realType;
    }

    public int getNumAttrs() throws SQLException {
        return ((OracleTypeADT) this.realType).getNumAttrs();
    }

    public OracleType getAttrTypeAt(int idx) throws SQLException {
        return ((OracleTypeADT) this.realType).getAttrTypeAt(idx);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeByte(this.uptCode);
        out.writeObject(this.realType);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeUPT.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.uptCode = in.readByte();
        this.realType = ((OracleNamedType) in.readObject());
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;

        this.realType.setConnection(conn);
    }

    public void initChildNamesRecursively(Map typesMap) throws SQLException {
        if (this.realType != null) {
            this.realType.setSqlName(this.sqlName);
            this.realType.initChildNamesRecursively(typesMap);
        }
    }

    public void initMetadataRecursively() throws SQLException {
        initMetadata(this.connection);
        if (this.realType != null)
            this.realType.initMetadataRecursively();
    }

    public void cacheDescriptor() throws SQLException {
    }

    public void printXML(PrintWriter pw, int indent) throws SQLException {
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("<OracleTypeUPT> sqlName=\"" + this.sqlName + "\" " + " toid=\"" + this.toid
                + "\" " + ">");

        if (this.realType != null)
            this.realType.printXML(pw, indent + 1);
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("</OracleTypeUPT>");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeUPT"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeUPT JD-Core Version: 0.6.0
 */