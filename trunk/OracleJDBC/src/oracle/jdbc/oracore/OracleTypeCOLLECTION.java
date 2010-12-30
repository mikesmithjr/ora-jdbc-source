package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.SQLName;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public class OracleTypeCOLLECTION extends OracleTypeADT implements Serializable {
    static final long serialVersionUID = -7279638692691669378L;
    public static final int TYPE_PLSQL_INDEX_TABLE = 1;
    public static final int TYPE_NESTED_TABLE = 2;
    public static final int TYPE_VARRAY = 3;
    int userCode = 0;
    long maxSize = 0L;
    OracleType elementType = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    public OracleTypeCOLLECTION(String sql_name, OracleConnection conn) throws SQLException {
        super(sql_name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION (sql_name = " + sql_name
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeCOLLECTION(OracleTypeADT parent, int idx, OracleConnection conn)
            throws SQLException {
        super(parent, idx, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION (parent = " + parent
                    + ", idx = " + idx + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeCOLLECTION(SQLName sqlName, byte[] typoid, int version, byte[] tds,
            byte[] lds, OracleConnection conn, byte[] fdo) throws SQLException {
        super(sqlName, typoid, version, tds, lds, conn, fdo);
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.toDatum(value = " + value
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value != null) {
            if ((value instanceof ARRAY)) {
                return (ARRAY) value;
            }

            ArrayDescriptor desc = createArrayDescriptor();

            return new ARRAY(desc, this.connection, value);
        }

        return null;
    }

    public int getTypeCode() {
        return 2003;
    }

    public boolean isInHierarchyOf(OracleType anOracleType) throws SQLException {
        if (anOracleType == null) {
            return false;
        }
        if (anOracleType == this) {
            return true;
        }
        if (anOracleType.getClass() != getClass()) {
            return false;
        }
        return anOracleType.getTypeDescriptor().getName().equals(this.descriptor.getName());
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
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.parseTDSrec(" + tdsReader
                    + ") " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        long elementPos = tdsReader.readLong();

        this.maxSize = tdsReader.readLong();

        this.userCode = tdsReader.readByte();

        tdsReader.addSimplePatch(elementPos, this);
    }

    public Datum unlinearize(byte[] pickled_bytes, long offset, Datum container, int type,
            Map objmap) throws SQLException {
        return unlinearize(pickled_bytes, offset, container, 1L, -1, type, objmap);
    }

    public Datum unlinearize(byte[] pickled_bytes, long offset, Datum container, long idx, int cnt,
            int style, Map objmap) throws SQLException {
        OracleConnection mc = getConnection();
        Datum ret = null;

        if (mc == null) {
            ret = unlinearizeInternal(pickled_bytes, offset, container, idx, cnt, style, objmap);
        } else {
            synchronized (mc) {
                ret = unlinearizeInternal(pickled_bytes, offset, container, idx, cnt, style, objmap);
            }

        }

        return ret;
    }

    public synchronized Datum unlinearizeInternal(byte[] pickled_bytes, long offset,
            Datum container, long idx, int cnt, int style, Map objmap) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unlinearize(" + pickled_bytes
                    + ") " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        if (pickled_bytes == null) {
            return null;
        }
        if ((pickled_bytes[0] & 0x80) > 0) {
            PickleContext context = new PickleContext(pickled_bytes, offset);

            return unpickle81(context, (ARRAY) container, idx, cnt, 1, style, objmap);
        }

        UnpickleContext newContext = new UnpickleContext(pickled_bytes, (int) offset, null, null,
                this.bigEndian);

        return unpickle80(newContext, (ARRAY) container, idx, cnt, 1, style, objmap);
    }

    public synchronized boolean isInlineImage(byte[] pickled_bytes, int offset) throws SQLException {
        if (pickled_bytes == null) {
            return false;
        }
        if ((pickled_bytes[offset] & 0x80) > 0) {
            if (PickleContext.isCollectionImage_pctx(pickled_bytes[offset]))
                return true;
            if (PickleContext.isDegenerateImage_pctx(pickled_bytes[offset])) {
                return false;
            }
            DatabaseError.throwSqlException(1, "Image is not a collection image");

            return false;
        }

        if ((pickled_bytes[(offset + 0)] == 0) && (pickled_bytes[(offset + 1)] == 0)
                && (pickled_bytes[(offset + 2)] == 0) && (pickled_bytes[(offset + 3)] == 0)) {
            return false;
        }
        return (pickled_bytes[(offset + 10)] & 0x1) == 1;
    }

    protected ARRAY unpickle80(UnpickleContext context, ARRAY collDatum, int style, int elemStyle,
            Map elemMap) throws SQLException {
        return unpickle80(context, collDatum, 1L, -1, style, elemStyle, elemMap);
    }

    protected ARRAY unpickle80(UnpickleContext context, ARRAY container, long beginIdx, int count,
            int style, int elemStyle, Map elemMap) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.unpickle(context = "
                    + context + ", container = " + container + ", sqlName = " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        ARRAY coll_obj = container;

        if (style == 3) {
            if (coll_obj == null) {
                ArrayDescriptor desc = createArrayDescriptor();

                coll_obj = new ARRAY(desc, (byte[]) null, this.connection);
            }

            coll_obj.setImage(context.image(), context.absoluteOffset(), 0L);
        }

        long _length = context.readLong();

        if (_length == 0L) {
            return null;
        }
        if (style == 9) {
            context.skipBytes((int) _length);

            return coll_obj;
        }
        if (style == 3) {
            coll_obj.setImageLength(_length + 4L);
            context.skipBytes((int) _length);

            return coll_obj;
        }

        if (coll_obj == null) {
            ArrayDescriptor desc = createArrayDescriptor();

            coll_obj = new ARRAY(desc, (byte[]) null, this.connection);
        }

        context.skipBytes(2);

        long prefixLength = context.readLong();
        byte psegFlag = context.readByte();

        coll_obj.setLocator(context.readBytes((int) prefixLength - 1));

        boolean inline = (psegFlag & 0x1) == 1;

        if (inline) {
            long endOffset = context.readLong() + context.offset();

            UnpickleContext newContext = new UnpickleContext(context.image(), context
                    .absoluteOffset(), null, null, this.bigEndian);

            if ((beginIdx == 1L) && (count == -1)) {
                coll_obj = (ARRAY) unpickle80rec(newContext, coll_obj, elemStyle, elemMap);
            } else {
                coll_obj = (ARRAY) unpickle80rec(newContext, coll_obj, beginIdx, count, elemStyle,
                                                 elemMap);
            }

            context.skipTo(endOffset);
        }

        return coll_obj;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.unpicklerec(" + context
                    + ") " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        return unpickle80rec(context, (ARRAY) null, type, map);
    }

    private Object unpickle80rec(UnpickleContext context, ARRAY container, int elemStyle,
            Map elemMap) throws SQLException {
        ARRAY coll_obj = container;

        if (coll_obj == null) {
            ArrayDescriptor desc = createArrayDescriptor();

            coll_obj = new ARRAY(desc, (byte[]) null, this.connection);
        }

        int length = (int) context.readLong();

        coll_obj.setLength(length);

        if (elemStyle == 0) {
            return coll_obj;
        }

        byte flag = context.readByte();

        if ((flag != 0) && (flag != 2)) {
            DatabaseError.throwSqlException(23, "collection flag = " + flag);
        }

        int format = flag == 0 ? 2 : 3;

        unpickle80rec_elems(context, coll_obj, 1, length, elemStyle, elemMap, format);

        return coll_obj;
    }

    private Object unpickle80rec(UnpickleContext context, ARRAY container, long beginIdx,
            int count, int elemStyle, Map elemMap) throws SQLException {
        ARRAY coll_obj = container;
        if (coll_obj == null) {
            ArrayDescriptor desc = createArrayDescriptor();
            coll_obj = new ARRAY(desc, (byte[]) null, connection);
        }
        long nbelem = context.readLong();
        coll_obj.setLength((int) nbelem);
        byte flag = context.readByte();
        if (flag != 0 && flag != 2) {
            DatabaseError.throwSqlException(23, "collection flag = " + flag);
        }
        int format = flag != 0 ? 3 : 2;
        int length = (int) getAccessLength(nbelem, beginIdx, count);
        boolean cacheAll = ArrayDescriptor.getCacheStyle(coll_obj) == 1;
        if (beginIdx > 1L && length > 0) {
            if ((elementType instanceof OracleTypeNUMBER)
                    || (elementType instanceof OracleTypeFLOAT)
                    || (elementType instanceof OracleTypeSINT32)) {
                switch (format) {
                case 2: // '\002'
                    context.skipBytes(23 * ((int) beginIdx - 1));
                    break;

                case 3: // '\003'
                    context.skipBytes(22 * ((int) beginIdx - 1));
                    break;

                default:
                    DatabaseError.throwSqlException(1);
                    break;
                }
            } else {
                long lastIdx = coll_obj.getLastIndex();
                if (lastIdx < beginIdx) {
                    if (lastIdx > 0L) {
                        context.skipTo(coll_obj.getLastOffset());
                    } else {
                        lastIdx = 1L;
                    }
                    if (cacheAll) {
                        for (long i = lastIdx; i < beginIdx; i++) {
                            coll_obj.setIndexOffset(i, context.offset());
                            elementType.unpickle80rec(context, format, 9, null);
                        }

                    } else {
                        for (long i = lastIdx; i < beginIdx; i++) {
                            elementType.unpickle80rec(context, format, 9, null);
                        }

                    }
                } else if (lastIdx > beginIdx) {
                    long offset = coll_obj.getOffset(beginIdx);
                    if (offset != -1L) {
                        context.skipTo(offset);
                    } else if (cacheAll) {
                        for (long i = 1L; i < beginIdx; i++) {
                            coll_obj.setIndexOffset(i, context.offset());
                            elementType.unpickle80rec(context, format, 9, null);
                        }

                    } else {
                        for (int i = 1; (long) i < beginIdx; i++) {
                            elementType.unpickle80rec(context, format, 9, null);
                        }

                    }
                } else {
                    context.skipTo(coll_obj.getLastOffset());
                }
                coll_obj.setLastIndexOffset(beginIdx, context.offset());
            }
        }
        unpickle80rec_elems(context, coll_obj, (int) beginIdx, length, elemStyle, elemMap, format);
        return coll_obj;
    }

    private Object unpickle80rec_elems(UnpickleContext context, ARRAY container, int beginIdx,
            int length, int elemStyle, Map elemMap, int format) throws SQLException {
        boolean cacheAll = ArrayDescriptor.getCacheStyle(container) == 1;

        switch (elemStyle) {
        case 1:
            Datum[] darray = new Datum[length];

            if (cacheAll) {
                for (int i = 0; i < length; i++) {
                    container.setIndexOffset(beginIdx + i, context.offset());

                    darray[i] = ((Datum) this.elementType.unpickle80rec(context, format, elemStyle,
                                                                        elemMap));
                }

            } else {
                for (int i = 0; i < length; i++) {
                    darray[i] = ((Datum) this.elementType.unpickle80rec(context, format, elemStyle,
                                                                        elemMap));
                }

            }

            container.setDatumArray(darray);

            break;
        case 2:
            Object[] oarray = ArrayDescriptor.makeJavaArray(length, this.elementType.getTypeCode());

            if (cacheAll) {
                for (int i = 0; i < length; i++) {
                    container.setIndexOffset(beginIdx + i, context.offset());

                    oarray[i] = this.elementType.unpickle80rec(context, format, elemStyle, elemMap);
                }

            } else {
                for (int i = 0; i < length; i++) {
                    oarray[i] = this.elementType.unpickle80rec(context, format, elemStyle, elemMap);
                }
            }

            container.setObjArray(oarray);

            break;
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
            if (((this.elementType instanceof OracleTypeNUMBER))
                    || ((this.elementType instanceof OracleTypeFLOAT))) {
                container.setObjArray(OracleTypeNUMBER.unpickle80NativeArray(context, 1L, length,
                                                                             elemStyle, format));
            } else {
                DatabaseError.throwSqlException(23);
            }

            break;
        case 3:
        default:
            DatabaseError.throwSqlException(1);
        }

        container.setLastIndexOffset(beginIdx + length, context.offset());

        return container;
    }

    protected int pickle81(PickleContext ctx, Datum datum) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.pickle81(context = " + ctx
                    + ", datum = " + datum + ") sqlName " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        ARRAY data = (ARRAY) datum;

        boolean inline = data.hasDataSeg();
        int imglen = 0;
        int lenOffset = ctx.offset() + 2;

        if (inline) {
            Datum[] dataValues = data.getOracleArray();

            if (this.userCode == 3) {
                if (dataValues.length > this.maxSize) {
                    DatabaseError.throwSqlException(71, null);
                }
            }

            imglen += ctx.writeCollImageHeader(dataValues.length);

            for (int ctr = 0; ctr < dataValues.length; ctr++) {
                if (dataValues[ctr] == null)
                    imglen += ctx.writeElementNull();
                else {
                    imglen += this.elementType.pickle81(ctx, dataValues[ctr]);
                }

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCOLLECTION.pickle81(): idx="
                            + ctr + " is " + dataValues[ctr], this);

                    OracleLog.recursiveTrace = false;
                }
            }

        } else {
            imglen += ctx.writeCollImageHeader(data.getLocator());
        }

        ctx.patchImageLen(lenOffset, imglen);

        return imglen;
    }

    ARRAY unpickle81(PickleContext context, ARRAY container, int style, int elemStyle, Map elemMap)
            throws SQLException {
        return unpickle81(context, container, 1L, -1, style, elemStyle, elemMap);
    }

    ARRAY unpickle81(PickleContext context, ARRAY container, long beginIdx, int count, int style,
            int elemStyle, Map elemMap) throws SQLException {
        ARRAY coll_obj = container;

        if (coll_obj == null) {
            ArrayDescriptor desc = createArrayDescriptor();

            coll_obj = new ARRAY(desc, (byte[]) null, this.connection);
        }

        if (unpickle81ImgHeader(context, coll_obj, style, elemStyle)) {
            if ((beginIdx == 1L) && (count == -1))
                unpickle81ImgBody(context, coll_obj, elemStyle, elemMap);
            else {
                unpickle81ImgBody(context, coll_obj, beginIdx, count, elemStyle, elemMap);
            }
        }

        return coll_obj;
    }

    boolean unpickle81ImgHeader(PickleContext context, ARRAY container, int style, int elemStyle)
            throws SQLException {
        boolean inline = true;

        if (style == 3) {
            container.setImage(context.image(), context.absoluteOffset(), 0L);
        }

        byte flags = context.readByte();

        if (!PickleContext.is81format(flags)) {
            DatabaseError.throwSqlException(1, "Image is not in 8.1 format");
        }

        if (!PickleContext.hasPrefix(flags)) {
            DatabaseError.throwSqlException(1, "Image has no prefix segment");
        }

        if (PickleContext.isCollectionImage_pctx(flags))
            inline = true;
        else if (PickleContext.isDegenerateImage_pctx(flags))
            inline = false;
        else {
            DatabaseError.throwSqlException(1, "Image is not a collection image");
        }

        context.readByte();

        if (style == 9) {
            context.skipBytes(context.readLength(true) - 2);

            return false;
        }
        if (style == 3) {
            long length = context.readLength();

            container.setImageLength(length);
            context.skipTo(container.getImageOffset() + length);

            return false;
        }

        context.skipLength();

        int psegLen = context.readLength();

        container.setPrefixFlag(context.readByte());

        if (container.isInline()) {
            context.readDataValue(psegLen - 1);
        } else {
            container.setLocator(context.readDataValue(psegLen - 1));
        }

        return container.isInline();
    }

    void unpickle81ImgBody(PickleContext context, ARRAY container, long beginIdx, int count,
            int elemStyle, Map elemMap) throws SQLException {
        context.readByte();

        int coll_len = context.readLength();

        container.setLength(coll_len);

        if (elemStyle == 0) {
            return;
        }

        int length = (int) getAccessLength(coll_len, beginIdx, count);
        boolean cacheAll = ArrayDescriptor.getCacheStyle(container) == 1;

        if ((beginIdx > 1L) && (length > 0)) {
            long lastIdx = container.getLastIndex();

            if (lastIdx < beginIdx) {
                if (lastIdx > 0L)
                    context.skipTo(container.getLastOffset());
                else {
                    lastIdx = 1L;
                }
                if (cacheAll) {
                    for (long i = lastIdx; i < beginIdx; i += 1L) {
                        container.setIndexOffset(i, context.offset());
                        this.elementType.unpickle81rec(context, 9, null);
                    }
                } else {
                    for (long i = lastIdx; i < beginIdx; i += 1L)
                        this.elementType.unpickle81rec(context, 9, null);
                }
            } else if (lastIdx > beginIdx) {
                long offset = container.getOffset(beginIdx);

                if (offset != -1L) {
                    context.skipTo(offset);
                } else if (cacheAll) {
                    for (int i = 1; i < beginIdx; i++) {
                        container.setIndexOffset(i, context.offset());
                        this.elementType.unpickle81rec(context, 9, null);
                    }
                } else {
                    for (int i = 1; i < beginIdx; i++)
                        this.elementType.unpickle81rec(context, 9, null);
                }
            } else {
                context.skipTo(container.getLastOffset());
            }
            container.setLastIndexOffset(beginIdx, context.offset());
        }

        unpickle81ImgBodyElements(context, container, (int) beginIdx, length, elemStyle, elemMap);
    }

    void unpickle81ImgBody(PickleContext context, ARRAY container, int elemStyle, Map elemMap)
            throws SQLException {
        context.readByte();

        int length = context.readLength();

        container.setLength(length);

        if (elemStyle == 0) {
            return;
        }

        unpickle81ImgBodyElements(context, container, 1, length, elemStyle, elemMap);
    }

    private void unpickle81ImgBodyElements(PickleContext context, ARRAY container, int beginIdx,
            int length, int elemStyle, Map elemMap) throws SQLException {
        boolean cacheAll = ArrayDescriptor.getCacheStyle(container) == 1;

        switch (elemStyle) {
        case 1:
            Datum[] datumArray = new Datum[length];

            if (cacheAll) {
                for (int i = 0; i < length; i++) {
                    container.setIndexOffset(beginIdx + i, context.offset());

                    datumArray[i] = ((Datum) this.elementType.unpickle81rec(context, elemStyle,
                                                                            elemMap));
                }

            } else {
                for (int i = 0; i < length; i++) {
                    datumArray[i] = ((Datum) this.elementType.unpickle81rec(context, elemStyle,
                                                                            elemMap));
                }
            }

            container.setDatumArray(datumArray);

            break;
        case 2:
            Object[] darray = ArrayDescriptor.makeJavaArray(length, this.elementType.getTypeCode());

            if (cacheAll) {
                for (int i = 0; i < length; i++) {
                    container.setIndexOffset(beginIdx + i, context.offset());

                    darray[i] = this.elementType.unpickle81rec(context, elemStyle, elemMap);
                }
            } else {
                for (int i = 0; i < length; i++) {
                    darray[i] = this.elementType.unpickle81rec(context, elemStyle, elemMap);
                }
            }
            container.setObjArray(darray);

            break;
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
            if (((this.elementType instanceof OracleTypeNUMBER))
                    || ((this.elementType instanceof OracleTypeFLOAT))) {
                container.setObjArray(OracleTypeNUMBER.unpickle81NativeArray(context, 1L, length,
                                                                             elemStyle));
            } else {
                DatabaseError
                        .throwSqlException(23, "This feature is limited to numeric collection");
            }

            break;
        case 3:
        default:
            DatabaseError.throwSqlException(68, "Invalid conversion type " + this.elementType);
        }

        container.setLastIndexOffset(beginIdx + length, context.offset());
    }

    private synchronized void initCollElemTypeName() throws SQLException {
        Statement stmt = this.connection.createStatement();
        ResultSet rset = stmt
                .executeQuery("SELECT ELEM_TYPE_NAME, ELEM_TYPE_OWNER FROM ALL_COLL_TYPES WHERE TYPE_NAME='"
                        + this.sqlName.getSimpleName()
                        + "' AND OWNER='"
                        + this.sqlName.getSchema()
                        + "'");

        if (rset.next()) {
            if (this.attrTypeNames == null) {
                this.attrTypeNames = new String[1];
            }
            this.attrTypeNames[0] = (rset.getString(2) + "." + rset.getString(1));
        } else {
            DatabaseError.throwSqlException(1);
        }
        rset.close();
        stmt.close();
    }

    public String getAttributeName(int idx) throws SQLException {
        DatabaseError.throwSqlException(1);

        return null;
    }

    public String getAttributeName(int idx, boolean force) throws SQLException {
        return getAttributeName(idx);
    }

    public String getAttributeType(int idx) throws SQLException {
        if (idx != 1) {
            DatabaseError.throwSqlException(68);
        }
        if (this.sqlName == null) {
            getFullName();
        }
        if (this.attrTypeNames == null) {
            initCollElemTypeName();
        }
        return this.attrTypeNames[0];
    }

    public String getAttributeType(int idx, boolean force) throws SQLException {
        if (force) {
            return getAttributeType(idx);
        }

        if (idx != 1) {
            DatabaseError.throwSqlException(68);
        }
        if ((this.sqlName != null) && (this.attrTypeNames != null)) {
            return this.attrTypeNames[0];
        }
        return null;
    }

    public int getNumAttrs() throws SQLException {
        return 0;
    }

    public OracleType getAttrTypeAt(int idx) throws SQLException {
        return null;
    }

    ArrayDescriptor createArrayDescriptor() throws SQLException {
        return new ArrayDescriptor(this, this.connection);
    }

    ArrayDescriptor createArrayDescriptorWithItsOwnTree() throws SQLException {
        if (this.descriptor == null) {
            if ((this.sqlName == null) && (getFullName(false) == null)) {
                this.descriptor = new ArrayDescriptor(this, this.connection);
            } else {
                this.descriptor = ArrayDescriptor.createDescriptor(this.sqlName, this.connection);
            }
        }

        return (ArrayDescriptor) this.descriptor;
    }

    public OracleType getElementType() throws SQLException {
        return this.elementType;
    }

    public int getUserCode() throws SQLException {
        return this.userCode;
    }

    public long getMaxLength() throws SQLException {
        return this.maxSize;
    }

    private long getAccessLength(long coll_len, long beginIdx, int count) throws SQLException {
        if (beginIdx > coll_len) {
            return 0L;
        }
        if (count < 0) {
            return coll_len - beginIdx + 1L;
        }

        return Math.min(coll_len - beginIdx + 1L, count);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.userCode);
        out.writeLong(this.maxSize);
        out.writeObject(this.elementType);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCOLLECTION.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.userCode = in.readInt();
        this.maxSize = in.readLong();
        this.elementType = ((OracleType) in.readObject());
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;

        this.elementType.setConnection(conn);
    }

    public void initMetadataRecursively() throws SQLException {
        initMetadata(this.connection);
        if (this.elementType != null)
            this.elementType.initMetadataRecursively();
    }

    public void initChildNamesRecursively(Map typesMap) throws SQLException {
        TypeTreeElement element = (TypeTreeElement) typesMap.get(this.sqlName);

        if (this.elementType != null) {
            this.elementType.setNames(element.getChildSchemaName(0), element.getChildTypeName(0));
            this.elementType.initChildNamesRecursively(typesMap);
            this.elementType.cacheDescriptor();
        }
    }

    public void cacheDescriptor() throws SQLException {
        this.descriptor = ArrayDescriptor.createDescriptor(this);
    }

    public void printXML(PrintWriter pw, int indent) throws SQLException {
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("<OracleTypeCOLLECTION sqlName=\"" + this.sqlName + "\" " + " toid=\""
                + this.toid + "\" " + ">");

        if (this.elementType != null)
            this.elementType.printXML(pw, indent + 1);
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("</OracleTypeCOLLECTION>");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeCOLLECTION"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeCOLLECTION JD-Core Version: 0.6.0
 */