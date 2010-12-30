package oracle.sql;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class ARRAY extends DatumWithConnection implements Array {
    static final byte KOPUP_INLINE_COLL = 1;
    ArrayDescriptor descriptor;
    Object objArray;
    Datum[] datumArray;
    byte[] locator;
    byte prefixFlag;
    byte[] prefixSegment;
    int numElems = -1;

    boolean enableBuffering = false;
    boolean enableIndexing = false;
    public static final int ACCESS_FORWARD = 1;
    public static final int ACCESS_REVERSE = 2;
    public static final int ACCESS_UNKNOWN = 3;
    int accessDirection = 3;
    long lastIndex;
    long lastOffset;
    long[] indexArray;
    long imageOffset;
    long imageLength;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:46_PDT_2005";

    public ARRAY(ArrayDescriptor type, Connection conn, Object elements) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.ARRAY( type=" + type + " conn=" + conn
                    + "elements=" + elements + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(type);

        this.descriptor = type;

        assertNotNull(conn);

        if (!type.getInternalConnection()
                .isDescriptorSharable(
                                      ((oracle.jdbc.OracleConnection) conn)
                                              .physicalConnectionWithin())) {
            throw new SQLException("Cannot construct ARRAY instance, invalid connection");
        }

        type.setConnection(conn);
        setPhysicalConnectionOf(conn);

        if (elements == null)
            this.datumArray = new Datum[0];
        else {
            this.datumArray = this.descriptor.toOracleArray(elements, 1L, -1);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.ARRAY: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public ARRAY(ArrayDescriptor type, byte[] bytes, Connection conn) throws SQLException {
        super(bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.ARRAY( descriptor=" + this.descriptor
                    + " bytes=" + bytes + "conn=" + conn + ") -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(type);

        this.descriptor = type;

        assertNotNull(conn);

        if (!type.getInternalConnection()
                .isDescriptorSharable(
                                      ((oracle.jdbc.OracleConnection) conn)
                                              .physicalConnectionWithin())) {
            throw new SQLException("Cannot construct ARRAY instance, invalid connection");
        }

        type.setConnection(conn);
        setPhysicalConnectionOf(conn);

        this.datumArray = null;
        this.locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.ARRAY: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public static ARRAY toARRAY(Object obj, oracle.jdbc.OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.toARRAY( obj=" + obj + ", conn=" + conn
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        ARRAY s = null;

        if (obj != null) {
            if ((obj instanceof ARRAY)) {
                s = (ARRAY) obj;
            } else if ((obj instanceof ORAData)) {
                s = (ARRAY) ((ORAData) obj).toDatum(conn);
            } else if ((obj instanceof CustomDatum)) {
                s = (ARRAY) ((oracle.jdbc.internal.OracleConnection) conn)
                        .toDatum((CustomDatum) obj);
            } else {
                DatabaseError.throwSqlException(59, obj);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.toARRAY: return");

            OracleLog.recursiveTrace = false;
        }

        return s;
    }

    public synchronized String getBaseTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getBaseTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = this.descriptor.getBaseName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getBaseTypeName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized int getBaseType() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "ARRAY.getBaseType() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.getBaseType();
    }

    public synchronized Object getArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getArray() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toJavaArray(this, 1L, -1, getMap(), this.enableBuffering);
    }

    public synchronized Object getArray(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getArray( map=" + map
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toJavaArray(this, 1L, -1, map, this.enableBuffering);
    }

    public synchronized Object getArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getArray( index=" + index + ", count="
                    + count + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if ((index < 1L) || (count < 0)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ARRAY.getArray: Invalid arguments, 'index' should be >= 1 and 'count' >= 0. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "getArray()");
        }

        return this.descriptor.toJavaArray(this, index, count, getMap(), false);
    }

    public synchronized Object getArray(long index, int count, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getArray( index=" + index + ", count="
                    + count + ", map=" + map + ")" + " -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if ((index < 1L) || (count < 0)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ARRAY.getArray: Invalid arguments, 'index' should be >= 1 and 'count' >= 0. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "getArray()");
        }

        return this.descriptor.toJavaArray(this, index, count, map, false);
    }

    public synchronized ResultSet getResultSet() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getResultSet() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return getResultSet(getInternalConnection().getTypeMap());
    }

    public synchronized ResultSet getResultSet(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getResultSet( map=" + map
                    + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toResultSet(this, 1L, -1, map, this.enableBuffering);
    }

    public synchronized ResultSet getResultSet(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getResultSet( index=" + index + ", count="
                    + count + ")" + " -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getResultSet(index, count, getInternalConnection().getTypeMap());
    }

    public synchronized ResultSet getResultSet(long index, int count, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getResultSet( index=" + index + ", count="
                    + count + ", map=" + map + ")" + " -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if ((index < 1L) || (count < -1)) {
            DatabaseError.throwSqlException(68, "getResultSet()");
        }

        return this.descriptor.toResultSet(this, index, count, map, false);
    }

    public synchronized Datum[] getOracleArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getOracleArray() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toOracleArray(this, 1L, -1, this.enableBuffering);
    }

    public synchronized int length() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.length() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toLength(this);
    }

    public synchronized Datum[] getOracleArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getOracleArray( index=" + index
                    + ", count=" + count + ")" + " -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if ((index < 1L) || (count < 0)) {
            DatabaseError.throwSqlException(68, "getOracleArray()");
        }

        return this.descriptor.toOracleArray(this, index, count, false);
    }

    public synchronized String getSQLTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getSQLTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = null;

        if (this.descriptor != null) {
            ret = this.descriptor.getName();
        } else {
            DatabaseError.throwSqlException(61, "ARRAY");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getSQLTypeName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Map getMap() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getMap() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return getInternalConnection().getTypeMap();
    }

    public ArrayDescriptor getDescriptor() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getDescriptor(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor;
    }

    public synchronized byte[] toBytes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.toBytes() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor.toBytes(this, this.enableBuffering);
    }

    public synchronized void setDatumArray(Datum[] darray) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setDatumArray( darray=" + darray
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.datumArray = darray;
    }

    public synchronized void setObjArray(Object oarray) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "ARRAY.setObjArray( oarray=" + oarray + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (oarray == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "ARRAY.setObjArray: Invalid argument, 'oarray' should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(1);
        }

        this.objArray = oarray;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setObjArray: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void setLocator(byte[] pseg_bytes) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setLocator( pseg_bytes=" + pseg_bytes
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        if ((pseg_bytes != null) && (pseg_bytes.length != 0))
            this.locator = pseg_bytes;
    }

    public synchronized void setPrefixSegment(byte[] pseg_bytes) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setPrefixSegment ( pseg_bytes="
                    + pseg_bytes + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        if ((pseg_bytes != null) && (pseg_bytes.length != 0))
            this.prefixSegment = pseg_bytes;
    }

    public synchronized void setPrefixFlag(byte psegFlag) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setPrefixFlag ( psegFlag=" + psegFlag
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.prefixFlag = psegFlag;
    }

    public byte[] getLocator() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getLocator(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.locator;
    }

    public synchronized void setLength(int len) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "ARRAY.setLength( len=" + len + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.numElems = len;
    }

    public boolean hasDataSeg() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.hasDataSeg(): return: "
                    + (this.locator == null ? "true" : "false"), this);

            OracleLog.recursiveTrace = false;
        }

        return this.locator == null;
    }

    public boolean isInline() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.isInline (): return: "
                    + ((this.prefixFlag & 0x1) == 1), this);

            OracleLog.recursiveTrace = false;
        }

        return (this.prefixFlag & 0x1) == 1;
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.toJdbc(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.isConvertibleTo( jClass=" + jClass
                    + "): return: false (always)", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.makeJdbcArray( arraysize=" + arraySize
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new Object[arraySize][];
    }

    public synchronized int[] getIntArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "ARRAY.getIntArray() -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (int[]) this.descriptor.toNumericArray(this, 1L, -1, 4, this.enableBuffering);
    }

    public synchronized int[] getIntArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getIntArray( index=" + index + ", count"
                    + count + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (int[]) this.descriptor.toNumericArray(this, index, count, 4, false);
    }

    public synchronized double[] getDoubleArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getDoubleArray() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return (double[]) this.descriptor.toNumericArray(this, 1L, -1, 5, this.enableBuffering);
    }

    public synchronized double[] getDoubleArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getDoubleArray( index=" + index
                    + ", count=" + count + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (double[]) this.descriptor.toNumericArray(this, index, count, 5, false);
    }

    public synchronized short[] getShortArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getShortArray() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return (short[]) this.descriptor.toNumericArray(this, 1L, -1, 8, this.enableBuffering);
    }

    public synchronized short[] getShortArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getShortArray( index=" + index
                    + ", count=" + count + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (short[]) this.descriptor.toNumericArray(this, index, count, 8, false);
    }

    public synchronized long[] getLongArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getLongArray() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return (long[]) this.descriptor.toNumericArray(this, 1L, -1, 7, this.enableBuffering);
    }

    public synchronized long[] getLongArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getLongArray( index=" + index + ", count="
                    + count + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (long[]) this.descriptor.toNumericArray(this, index, count, 7, false);
    }

    public synchronized float[] getFloatArray() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getFloatArray() -- no return trace --",
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return (float[]) this.descriptor.toNumericArray(this, 1L, -1, 6, this.enableBuffering);
    }

    public synchronized float[] getFloatArray(long index, int count) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getFloatArray( index=" + index
                    + ", count=" + count + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        return (float[]) this.descriptor.toNumericArray(this, index, count, 6, false);
    }

    public synchronized void setAutoBuffering(boolean enable) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setAutoBuffering( enable=" + enable
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.enableBuffering = enable;
    }

    public boolean getAutoBuffering() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getAutoBuffering(): return: "
                    + this.enableBuffering, this);

            OracleLog.recursiveTrace = false;
        }

        return this.enableBuffering;
    }

    public synchronized void setAutoIndexing(boolean enable, int direction) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setAutoIndexing( enable=" + enable
                    + ", direction=" + direction + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.enableIndexing = enable;
        this.accessDirection = direction;
    }

    public synchronized void setAutoIndexing(boolean enable) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setAutoIndexing( enable=" + enable
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.enableIndexing = enable;
        this.accessDirection = 3;
    }

    public boolean getAutoIndexing() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getAutoIndexing(): return: "
                    + this.enableIndexing, this);

            OracleLog.recursiveTrace = false;
        }

        return this.enableIndexing;
    }

    public int getAccessDirection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getAccessDirection(): return: "
                    + this.accessDirection, this);

            OracleLog.recursiveTrace = false;
        }

        return this.accessDirection;
    }

    public void setLastIndexOffset(long index, long offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setLastIndexOffset( index=" + index
                    + ", offset=" + offset + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.lastIndex = index;
        this.lastOffset = offset;
    }

    public void setIndexOffset(long index, long offset) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setIndexOffset( index=" + index
                    + ", offset=" + offset + ") -- no return trace --", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.indexArray == null) {
            this.indexArray = new long[this.numElems];
        }
        this.indexArray[((int) index - 1)] = offset;
    }

    public long getLastIndex() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "ARRAY.getLastIndex(): return: " + this.lastIndex, this);

            OracleLog.recursiveTrace = false;
        }

        return this.lastIndex;
    }

    public long getLastOffset() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getLastOffset(): return: "
                    + this.lastOffset, this);

            OracleLog.recursiveTrace = false;
        }

        return this.lastOffset;
    }

    public long getOffset(long index) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getOffset( index=" + index + ")", this);

            OracleLog.recursiveTrace = false;
        }

        long ret = -1L;

        if (this.indexArray != null) {
            ret = this.indexArray[((int) index - 1)];
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getOffset: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public void setImage(byte[] image, long offset, long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setImage( image=" + image + ", offset="
                    + offset + " length=" + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setShareBytes(image);

        this.imageOffset = offset;
        this.imageLength = length;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setImage: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setImageLength(long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.setImageLength( length=" + length
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        this.imageLength = length;
    }

    public long getImageOffset() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getImageOffset(): return: "
                    + this.imageOffset, this);

            OracleLog.recursiveTrace = false;
        }

        return this.imageOffset;
    }

    public long getImageLength() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "ARRAY.getImageLength(): return: "
                    + this.imageLength, this);

            OracleLog.recursiveTrace = false;
        }

        return this.imageLength;
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    public String dump() throws SQLException {
        return STRUCT.dump(this);
    }

    static void dump(ARRAY x, PrintWriter pw, int indent) throws SQLException {
        if (indent > 0)
            pw.println();
        int i = 0;

        ArrayDescriptor desc = x.getDescriptor();
        for (i = 0; i < indent; i++)
            pw.print(' ');
        pw.println("name = " + desc.getName());

        for (i = 0; i < indent; i++)
            pw.print(' ');
        pw.println("max length = " + desc.getMaxLength());
        Object[] elems = (Object[]) x.getArray();
        for (i = 0; i < indent; i++)
            pw.print(' ');
        int length;
        pw.println("length = " + (length = elems.length));
        for (i = 0; i < length; i++) {
            for (int j = 0; j < indent; j++)
                pw.print(' ');
            pw.print("element[" + i + "] = ");
            STRUCT.dump(elems[i], pw, indent + 4);
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.ARRAY"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.ARRAY JD-Core Version: 0.6.0
 */