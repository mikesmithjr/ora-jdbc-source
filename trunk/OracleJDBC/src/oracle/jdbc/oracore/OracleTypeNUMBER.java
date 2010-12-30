// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: OracleTypeNUMBER.java

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
import oracle.sql.NUMBER;

// Referenced classes of package oracle.jdbc.oracore:
// OracleType, TDSReader, Util, UnpickleContext,
// PickleContext

public class OracleTypeNUMBER extends OracleType implements Serializable {

    static final long serialVersionUID = 0x9c538c25d105fd9cL;
    int precision;
    int scale;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    protected OracleTypeNUMBER(int typecode) {
        super(typecode);
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        return toNUMBER(value, conn);
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        return toNUMBERArray(obj, conn, beginIdx, count);
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.parseTDSrec( tdsReader = "
                    + tdsReader + ")", this);
            OracleLog.recursiveTrace = false;
        }
        nullOffset = tdsReader.nullOffset;
        ldsOffset = tdsReader.ldsOffset;
        tdsReader.nullOffset++;
        tdsReader.ldsOffset++;
        precision = tdsReader.readUnsignedByte();
        scale = tdsReader.readByte();
    }

    public int getSizeLDS(byte FDO[]) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeNUMBER.getSizeLDS( FDO = " + FDO + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (sizeForLds == 0) {
            sizeForLds = Util.fdoGetSize(FDO, 12);
            alignForLds = Util.fdoGetAlign(FDO, 12);
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.getSizeLDS:return "
                    + sizeForLds, this);
            OracleLog.recursiveTrace = false;
        }
        return sizeForLds;
    }

    public int getAlignLDS(byte FDO[]) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.getAlignLDS( FDO = " + FDO
                    + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (sizeForLds == 0) {
            sizeForLds = Util.fdoGetSize(FDO, 12);
            alignForLds = Util.fdoGetAlign(FDO, 12);
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.getAlignLDS:return "
                    + alignForLds, this);
            OracleLog.recursiveTrace = false;
        }
        return alignForLds;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        return numericUnpickle80rec(ldsOffset, nullOffset, context, format, type, map);
    }

    protected static Object numericUnpickle80rec(int lds_offset, int null_offset,
            UnpickleContext context, int format, int style, Map map) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeNUMBER.numericUnpickle80rec( lds_offset = "
                                            + lds_offset + ", null_offset =" + null_offset
                                            + ", context =" + context + ", format = " + format
                                            + ", style = " + style + ", map = " + map + ")");
            OracleLog.recursiveTrace = false;
        }
        switch (format) {
        case 1: // '\001'
            if (context.isNull(null_offset)) {
                return null;
            }
            context.skipTo(context.ldsOffsets[lds_offset]);
            break;

        case 2: // '\002'
            if ((context.readByte() & 1) == 1) {
                context.skipBytes(22);
                return null;
            }
            break;

        default:
            DatabaseError.throwSqlException(1, "format=" + format);
            break;

        case 3: // '\003'
            break;
        }
        if (style == 9) {
            context.skipBytes(22);
            return null;
        } else {
            return toNumericObject(context.readVarNumBytes(), style, map);
        }
    }

    protected static Object unpickle80NativeArray(UnpickleContext context, long beginIdx, int size,
            int style, int format) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeNUMBER.unpickle80_nativeArray( context =" + context
                                            + ", begin index =" + beginIdx + ", size = " + size
                                            + ", style = " + style + ", format = " + format + ")");
            OracleLog.recursiveTrace = false;
        }
        int numSz = 0;
        switch (format) {
        case 2: // '\002'
            numSz = 23;
            break;

        case 3: // '\003'
            numSz = 22;
            break;

        default:
            DatabaseError.throwSqlException(1, "format=" + format);
            break;
        }
        if (size > 0) {
            context.skipBytes(numSz * ((int) beginIdx - 1));
        }
        int offset = context.absoluteOffset();
        byte data[] = context.image();
        int current = 0;
        switch (style) {
        case 4: // '\004'
        {
            int holder[] = new int[size];
            for (int i = 0; i < size; i++) {
                current = offset + i * numSz;
                if (format == 3 || (data[current++] & 1) == 0) {
                    byte array[] = new byte[data[current++]];
                    System.arraycopy(data, current, array, 0, array.length);
                    holder[i] = NUMBER.toInt(array);
                }
            }

            context.skipBytes(numSz * size);
            return holder;
        }

        case 5: // '\005'
        {
            double holder[] = new double[size];
            for (int i = 0; i < size; i++) {
                current = offset + i * numSz;
                if (format == 3 || (data[current++] & 1) == 0) {
                    byte array[] = new byte[data[current++]];
                    System.arraycopy(data, current, array, 0, array.length);
                    holder[i] = NUMBER.toDouble(array);
                }
            }

            context.skipBytes(numSz * size);
            return holder;
        }

        case 7: // '\007'
        {
            long holder[] = new long[size];
            for (int i = 0; i < size; i++) {
                current = offset + i * numSz;
                if (format == 3 || (data[current++] & 1) == 0) {
                    byte array[] = new byte[data[current++]];
                    System.arraycopy(data, current, array, 0, array.length);
                    holder[i] = NUMBER.toLong(array);
                }
            }

            context.skipBytes(numSz * size);
            return holder;
        }

        case 6: // '\006'
        {
            float holder[] = new float[size];
            for (int i = 0; i < size; i++) {
                current = offset + i * numSz;
                if (format == 3 || (data[current++] & 1) == 0) {
                    byte array[] = new byte[data[current++]];
                    System.arraycopy(data, current, array, 0, array.length);
                    holder[i] = NUMBER.toFloat(array);
                }
            }

            context.skipBytes(numSz * size);
            return holder;
        }

        case 8: // '\b'
        {
            short holder[] = new short[size];
            for (int i = 0; i < size; i++) {
                current = offset + i * numSz;
                if (format == 3 || (data[current++] & 1) == 0) {
                    byte array[] = new byte[data[current++]];
                    System.arraycopy(data, current, array, 0, array.length);
                    holder[i] = NUMBER.toShort(array);
                }
            }

            context.skipBytes(numSz * size);
            return holder;
        }
        }
        DatabaseError.throwSqlException(23);
        return null;
    }

    protected static Object unpickle81NativeArray(PickleContext context, long beginIdx, int size,
            int style) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST,
                                    "OracleTypeNUMBER.unpickle81_nativeArray( context =" + context
                                            + ", begin index =" + beginIdx + ", size = " + size
                                            + ", style = " + style + ")");
            OracleLog.recursiveTrace = false;
        }
        for (int i = 1; (long) i < beginIdx && size > 0; i++) {
            context.skipDataValue();
        }

        byte val[] = null;
        switch (style) {
        case 4: // '\004'
        {
            int holder[] = new int[size];
            for (int i = 0; i < size; i++) {
                if ((val = context.readDataValue()) != null) {
                    holder[i] = NUMBER.toInt(val);
                }
            }

            return holder;
        }

        case 5: // '\005'
        {
            double holder[] = new double[size];
            for (int i = 0; i < size; i++) {
                if ((val = context.readDataValue()) != null) {
                    holder[i] = NUMBER.toDouble(val);
                }
            }

            return holder;
        }

        case 7: // '\007'
        {
            long holder[] = new long[size];
            for (int i = 0; i < size; i++) {
                if ((val = context.readDataValue()) != null) {
                    holder[i] = NUMBER.toLong(val);
                }
            }

            return holder;
        }

        case 6: // '\006'
        {
            float holder[] = new float[size];
            for (int i = 0; i < size; i++) {
                if ((val = context.readDataValue()) != null) {
                    holder[i] = NUMBER.toFloat(val);
                }
            }

            return holder;
        }

        case 8: // '\b'
        {
            short holder[] = new short[size];
            for (int i = 0; i < size; i++) {
                if ((val = context.readDataValue()) != null) {
                    holder[i] = NUMBER.toShort(val);
                }
            }

            return holder;
        }
        }
        DatabaseError.throwSqlException(23);
        return null;
    }

    protected Object toObject(byte bytes[], int style, Map map) throws SQLException {
        return toNumericObject(bytes, style, map);
    }

    static Object toNumericObject(byte bytes[], int style, Map map) throws SQLException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        switch (style) {
        case 1: // '\001'
            return new NUMBER(bytes);

        case 2: // '\002'
            return NUMBER.toBigDecimal(bytes);

        case 3: // '\003'
            return bytes;
        }
        DatabaseError.throwSqlException(23);
        return null;
    }

    public static NUMBER toNUMBER(Object value, OracleConnection conn) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.toNUMBER( object = " + value
                    + ", connection = " + conn + ")");
            OracleLog.recursiveTrace = false;
        }
        NUMBER datum = null;
        if (value != null) {
            try {
                if (value instanceof NUMBER) {
                    datum = (NUMBER) value;
                } else {
                    datum = new NUMBER(value);
                }
            } catch (SQLException e) {
                DatabaseError.throwSqlException(59, value);
            }
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.toNUMBER:return " + datum);
            OracleLog.recursiveTrace = false;
        }
        return datum;
    }

    public static Datum[] toNUMBERArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.toNUMBERArray( object = " + obj
                    + ", connection = " + conn + ", begin index = " + beginIdx + ", count = "
                    + count + ")");
            OracleLog.recursiveTrace = false;
        }
        Datum datumArray[] = null;
        if (obj != null) {
            if ((obj instanceof Object[]) && !(obj instanceof char[][])) {
                Object objArray[] = (Object[]) obj;
                int length = (int) (count != -1 ? Math
                        .min(((long) objArray.length - beginIdx) + 1L, count) : objArray.length);
                datumArray = new Datum[length];
                for (int i = 0; i < length; i++) {
                    datumArray[i] = toNUMBER(objArray[((int) beginIdx + i) - 1], conn);
                }

            } else {
                datumArray = cArrayToNUMBERArray(obj, conn, beginIdx, count);
            }
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.toNUMBERArray:return "
                    + datumArray);
            OracleLog.recursiveTrace = false;
        }
        return datumArray;
    }

    static Datum[] cArrayToNUMBERArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.cArrayToNUMBERArray( object = "
                    + obj + ", connection = " + conn + ", begin index = " + beginIdx + ", count = "
                    + count + ")");
            OracleLog.recursiveTrace = false;
        }
        Datum datumArray[] = null;
        if (obj != null) {
            if (obj instanceof short[]) {
                short parray[] = (short[]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(parray[((int) beginIdx + i) - 1]);
                }

            } else if (obj instanceof int[]) {
                int parray[] = (int[]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(parray[((int) beginIdx + i) - 1]);
                }

            } else if (obj instanceof long[]) {
                long parray[] = (long[]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(parray[((int) beginIdx + i) - 1]);
                }

            } else if (obj instanceof float[]) {
                float parray[] = (float[]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(parray[((int) beginIdx + i) - 1]);
                }

            } else if (obj instanceof double[]) {
                double parray[] = (double[]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(parray[((int) beginIdx + i) - 1]);
                }

            } else if (obj instanceof boolean[]) {
                boolean parray[] = (boolean[]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(new Boolean(parray[((int) beginIdx + i) - 1]));
                }

            } else if (obj instanceof char[][]) {
                char parray[][] = (char[][]) obj;
                int len = (int) (count != -1 ? Math.min(((long) parray.length - beginIdx) + 1L,
                                                        count) : parray.length);
                datumArray = new Datum[len];
                for (int i = 0; i < len; i++) {
                    datumArray[i] = new NUMBER(new String(parray[((int) beginIdx + i) - 1]));
                }

            } else {
                DatabaseError.throwSqlException(59, obj);
            }
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeNUMBER.cArrayToNUMBERArray:return "
                    + datumArray);
            OracleLog.recursiveTrace = false;
        }
        return datumArray;
    }

    public int getPrecision() {
        return precision;
    }

    public int getScale() {
        return scale;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeNUMBER.writeObject()", this);
            OracleLog.recursiveTrace = false;
        }
        out.writeInt(scale);
        out.writeInt(precision);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeNUMBER.readObject()", this);
            OracleLog.recursiveTrace = false;
        }
        scale = in.readInt();
        precision = in.readInt();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeNUMBER"));
        } catch (Exception e) {
        }
    }
}
