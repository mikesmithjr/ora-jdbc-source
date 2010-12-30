package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.OPAQUE;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.StructDescriptor;

public class OracleTypeOPAQUE extends OracleTypeADT implements Serializable {
    static final long KOLOFLLB = 1L;
    static final long KOLOFLCL = 2L;
    static final long KOLOFLUB = 4L;
    static final long KOLOFLFX = 8L;
    static final long serialVersionUID = -7279638692691669378L;
    long flagBits;
    long maxLen;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public OracleTypeOPAQUE(String name, OracleConnection conn) throws SQLException {
        super(name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeOPAQUE (+name = " + name + ", conn = "
                    + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeOPAQUE(OracleTypeADT parent, int idx, OracleConnection conn)
            throws SQLException {
        super(parent, idx, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeOPAQUE (parent = " + parent
                    + ", idx = " + idx + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeOPAQUE.toDatum(value = " + value
                    + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value != null) {
            if ((value instanceof OPAQUE)) {
                return (OPAQUE) value;
            }

            OpaqueDescriptor desc = createOpaqueDescriptor();

            return new OPAQUE(desc, this.connection, value);
        }

        return null;
    }

    public int getTypeCode() {
        return 2007;
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
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeOPAQUE.parseTDSrec(tdsReader = "
                    + tdsReader + ") sqlName = " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        tdsReader.skipBytes(5);

        this.flagBits = tdsReader.readLong();
        this.maxLen = tdsReader.readLong();
    }

    public Datum unlinearize(byte[] pickled_bytes, long offset, Datum container, int style,
            Map objmap) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.unlinearize(pickled_bytes = "
                    + pickled_bytes + ", sqlName = " + this.sqlName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pickled_bytes == null) {
            return null;
        }
        if ((pickled_bytes[0] & 0x80) > 0) {
            PickleContext context = new PickleContext(pickled_bytes, offset);

            return unpickle81(context, (OPAQUE) container, style, objmap);
        }

        return null;
    }

    public byte[] linearize(Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.lindarize(" + data + ") "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        return pickle81(data);
    }

    protected int pickle81(PickleContext ctx, Datum datum) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeOPAQUE.pickle81(ctx = " + ctx
                    + ", datum = " + datum + ") sqlName = " + this.sqlName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OPAQUE obj = (OPAQUE) datum;
        byte[] data = obj.getBytesValue();
        int imglen = 0;

        imglen += ctx.writeOpaqueImageHeader(data.length);
        imglen += ctx.writeData(data);

        return imglen;
    }

    OPAQUE unpickle81(PickleContext context, OPAQUE container, int style, Map elemMap)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "unpickle81( context = " + context
                    + ", container = " + container + ", style = " + style + ", ememMap = "
                    + elemMap + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return unpickle81datum(context, container, style);
    }

    protected Object unpickle81rec(PickleContext context, int style, Map map) throws SQLException {
        byte b = context.readByte();
        Object returnValue = null;

        if (PickleContext.isElementNull(b)) {
            return null;
        }
        context.skipRestOfLength(b);

        switch (style) {
        case 1:
            returnValue = unpickle81datum(context, null);
            break;
        case 2:
            returnValue = unpickle81datum(context, null).toJdbc();
            break;
        case 3:
            returnValue = new OPAQUE(createOpaqueDescriptor(), context.readDataValue(),
                    this.connection);

            break;
        case 9:
            context.skipDataValue();
            break;
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
        default:
            DatabaseError.throwSqlException(1);
        }

        return returnValue;
    }

    private OPAQUE unpickle81datum(PickleContext context, OPAQUE container) throws SQLException {
        return unpickle81datum(context, container, 1);
    }

    private OPAQUE unpickle81datum(PickleContext context, OPAQUE container, int style)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "unpickle81datum( context = " + context
                    + ", container = " + container + ")", this);

            OracleLog.recursiveTrace = false;
        }

        context.skipBytes(2);

        long length = context.readLength(true) - 2;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "unpickle81datum: byte[] length = " + length,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if (container == null) {
            if (style == 2) {
                return new OPAQUE(createOpaqueDescriptor(), context.readBytes((int) length),
                        this.connection);
            }

            return new OPAQUE(createOpaqueDescriptor(), this.connection, context
                    .readBytes((int) length));
        }

        container.setValue(context.readBytes((int) length));

        return container;
    }

    OpaqueDescriptor createOpaqueDescriptor() throws SQLException {
        if (this.sqlName == null) {
            return new OpaqueDescriptor(this, this.connection);
        }
        return OpaqueDescriptor.createDescriptor(this.sqlName, this.connection);
    }

    public long getMaxLength() throws SQLException {
        return this.maxLen;
    }

    public boolean isTrustedLibrary() throws SQLException {
        return (this.flagBits & 1L) != 0L;
    }

    public boolean isModeledInC() throws SQLException {
        return (this.flagBits & 0x2) != 0L;
    }

    public boolean isUnboundedSized() throws SQLException {
        return (this.flagBits & 0x4) != 0L;
    }

    public boolean isFixedSized() throws SQLException {
        return (this.flagBits & 0x8) != 0L;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeOPAQUE.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeOPAQUE.readObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeOPAQUE"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeOPAQUE JD-Core Version: 0.6.0
 */