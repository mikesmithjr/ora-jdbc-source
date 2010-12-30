package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public abstract class OracleType implements Serializable {
    static final long serialVersionUID = -6719430495533003861L;
    public static final int STYLE_ARRAY_LENGTH = 0;
    public static final int STYLE_DATUM = 1;
    public static final int STYLE_JAVA = 2;
    public static final int STYLE_RAWBYTE = 3;
    public static final int STYLE_INT = 4;
    public static final int STYLE_DOUBLE = 5;
    public static final int STYLE_FLOAT = 6;
    public static final int STYLE_LONG = 7;
    public static final int STYLE_SHORT = 8;
    public static final int STYLE_SKIP = 9;
    static final int FORMAT_ADT_ATTR = 1;
    static final int FORMAT_COLL_ELEM = 2;
    static final int FORMAT_COLL_ELEM_NO_INDICATOR = 3;
    int nullOffset;
    int ldsOffset;
    int sizeForLds;
    int alignForLds;
    int typeCode;
    int dbTypeCode;
    static final int KOPMAP_FLOAT = 2;
    static final int KOPMAP_SB4 = 4;
    static final int KOPMAP_PTR = 5;
    static final int KOPMAP_ORLD = 11;
    static final int KOPMAP_ORLN = 12;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    public OracleType() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleType ()");

            OracleLog.recursiveTrace = false;
        }

        this.nullOffset = 0;
        this.ldsOffset = 0;
        this.sizeForLds = 0;
    }

    public OracleType(int typecode) {
        this();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleType (" + typecode + ")");

            OracleLog.recursiveTrace = false;
        }

        this.typeCode = typecode;
    }

    public boolean isInHierarchyOf(OracleType anOracleType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.isInHierarchyOf(" + anOracleType
                    + "):return false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean isInHierarchyOf(StructDescriptor aStructDescriptor) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.isInHierarchyOf("
                    + aStructDescriptor + "):return false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean isObjectType() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.isObjectType():return false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public TypeDescriptor getTypeDescriptor() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.getTypeDescriptor():return null",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public abstract Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
            throws SQLException;

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.toDatumArray(object = " + obj
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = null;

        if (obj != null) {
            if ((obj instanceof Object[])) {
                Object[] objArray = (Object[]) obj;

                int length = (int) (count == -1 ? objArray.length : Math.min(objArray.length
                        - beginIdx + 1L, count));

                datumArray = new Datum[length];

                for (int i = 0; i < length; i++)
                    datumArray[i] = toDatum(objArray[((int) beginIdx + i - 1)], conn);
            } else {
                DatabaseError.throwSqlException(59, obj);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.toDatumArray:return " + datumArray,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return datumArray;
    }

    public void setTypeCode(int code) {
        this.typeCode = code;
    }

    public int getTypeCode() throws SQLException {
        return this.typeCode;
    }

    public void setDBTypeCode(int code) {
        this.dbTypeCode = code;
    }

    public int getDBTypeCode() throws SQLException {
        return this.dbTypeCode;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.parseTDSrec(" + tdsReader + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        this.nullOffset = tdsReader.nullOffset;
        this.ldsOffset = tdsReader.ldsOffset;

        tdsReader.nullOffset += 1;
        tdsReader.ldsOffset += 1;
    }

    public int getSizeLDS(byte[] FDO) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.getSizeLDS(" + FDO + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sizeForLds == 0) {
            this.sizeForLds = Util.fdoGetSize(FDO, 5);
            this.alignForLds = Util.fdoGetAlign(FDO, 5);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.getSizeLDS:return "
                    + this.sizeForLds, this);

            OracleLog.recursiveTrace = false;
        }

        return this.sizeForLds;
    }

    public int getAlignLDS(byte[] FDO) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.getAlignLDS(" + FDO + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sizeForLds == 0) {
            this.sizeForLds = Util.fdoGetSize(FDO, 5);
            this.alignForLds = Util.fdoGetAlign(FDO, 5);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.getAlignLDS:return "
                    + this.alignForLds, this);

            OracleLog.recursiveTrace = false;
        }

        return this.alignForLds;
    }

    protected abstract Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1,
            int paramInt2, Map paramMap) throws SQLException;

    protected Object unpickle81rec(PickleContext context, int type, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.unpickle81rec(context = " + context
                    + ", type = " + type + ", " + ", map = " + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (type == 9) {
            context.skipDataValue();

            return null;
        }

        byte[] val = context.readDataValue();

        return toObject(val, type, map);
    }

    protected Object unpickle81rec(PickleContext context, byte byte1, int type, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.unpickle81rec(context = " + context
                    + ", byte1 = " + byte1 + ", type = " + type + ", " + ", map = " + map + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if (type == 9) {
            context.skipDataValue();

            return null;
        }

        byte[] val = context.readDataValue(byte1);

        return toObject(val, type, map);
    }

    protected Datum unpickle81datumAsNull(PickleContext context, byte len_flags, byte immemb)
            throws SQLException {
        DatabaseError.throwSqlException(1);
        return null;
    }

    protected Object toObject(byte[] bytes, int type, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.toObject(bytes = " + bytes
                    + ", type = " + type + ", map = " + map + "):return null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    protected int pickle81(PickleContext ctx, Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.pickle81(context = " + ctx
                    + ", datum = " + data + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int len = ctx.writeLength((int) data.getLength());

        len += ctx.writeData(data.shareBytes());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.pickle81:return " + len, this);

            OracleLog.recursiveTrace = false;
        }

        return len;
    }

    void writeSerializedFields(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.writeSerializedFields()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.nullOffset);
        out.writeInt(this.ldsOffset);
        out.writeInt(this.sizeForLds);
        out.writeInt(this.alignForLds);
        out.writeInt(this.typeCode);
        out.writeInt(this.dbTypeCode);
    }

    void readSerializedFields(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINE, "OracleTypeADT.readSerializedFields()", this);

            OracleLog.recursiveTrace = false;
        }

        this.nullOffset = in.readInt();
        this.ldsOffset = in.readInt();
        this.sizeForLds = in.readInt();
        this.alignForLds = in.readInt();
        this.typeCode = in.readInt();
        this.dbTypeCode = in.readInt();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleType.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.nullOffset);
        out.writeInt(this.ldsOffset);
        out.writeInt(this.sizeForLds);
        out.writeInt(this.alignForLds);
        out.writeInt(this.typeCode);
        out.writeInt(this.dbTypeCode);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleType.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.nullOffset = in.readInt();
        this.ldsOffset = in.readInt();
        this.sizeForLds = in.readInt();
        this.alignForLds = in.readInt();
        this.typeCode = in.readInt();
        this.dbTypeCode = in.readInt();
    }

    public void setConnection(OracleConnection conn) throws SQLException {
    }

    public boolean isNCHAR() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleType.isNCHAR ()  false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public int getPrecision() throws SQLException {
        return 0;
    }

    public int getScale() throws SQLException {
        return 0;
    }

    public void initMetadataRecursively() throws SQLException {
    }

    public void initNamesRecursively() throws SQLException {
    }

    public void initChildNamesRecursively(Map typesMap) throws SQLException {
    }

    public void cacheDescriptor() throws SQLException {
    }

    public void setNames(String schemaName, String typeName) throws SQLException {
    }

    public String toXMLString() throws SQLException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        printXMLHeader(pw);
        printXML(pw, 0);
        return sw.toString();
    }

    public void printXML(PrintStream ps) throws SQLException {
        PrintWriter pw = new PrintWriter(ps, true);
        printXMLHeader(pw);
        printXML(pw, 0);
    }

    void printXMLHeader(PrintWriter pw) throws SQLException {
        pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
    }

    public void printXML(PrintWriter pw, int indent) throws SQLException {
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("<OracleType typecode=\"" + this.typeCode + "\"" + " hashCode=\"" + hashCode()
                + "\" " + " ldsOffset=\"" + this.ldsOffset + "\"" + " sizeForLds=\""
                + this.sizeForLds + "\"" + " alignForLds=\"" + this.alignForLds + "\"" + " />");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleType"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleType JD-Core Version: 0.6.0
 */