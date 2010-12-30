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
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;

public class OracleTypeDATE extends OracleType implements Serializable {
    static final long serialVersionUID = -5858803341118747965L;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public OracleTypeDATE() {
    }

    public OracleTypeDATE(int typecode) {
        super(typecode);
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeDATE.toDatum( object = " + value
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DATE datum = null;

        if (value != null) {
            try {
                if ((value instanceof DATE))
                    datum = (DATE) value;
                else if ((value instanceof TIMESTAMP))
                    datum = new DATE(((TIMESTAMP) value).timestampValue());
                else
                    datum = new DATE(value);
            } catch (SQLException e) {
                DatabaseError.throwSqlException(59, value);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeDATE.toDatum:return " + datum, this);

            OracleLog.recursiveTrace = false;
        }

        return datum;
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeDATE.toDatumArray( object = " + obj
                    + ", connection = " + conn + ", begin index = " + beginIdx + ", count ="
                    + count + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = null;

        if (obj != null) {
            if ((obj instanceof char[][])) {
                char[][] strArray = (char[][]) obj;
                int length = (int) (count == -1 ? strArray.length : Math.min(strArray.length
                        - beginIdx + 1L, count));

                datumArray = new Datum[length];

                for (int i = 0; i < length; i++)
                    datumArray[i] = toDatum(new String(strArray[((int) beginIdx + i - 1)]), conn);
            } else {
                if ((obj instanceof Object[])) {
                    return super.toDatumArray(obj, conn, beginIdx, count);
                }

                DatabaseError.throwSqlException(59, obj);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER,
                                    "OracleTypeDATE.toDatumArray:returm " + datumArray, this);

            OracleLog.recursiveTrace = false;
        }

        return datumArray;
    }

    public int getTypeCode() {
        return 91;
    }

    public int getSizeLDS(byte[] FDO) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeDATE.getSizeLDS( FDO = " + FDO + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sizeForLds == 0) {
            this.sizeForLds = Util.fdoGetSize(FDO, 11);
            this.alignForLds = Util.fdoGetAlign(FDO, 11);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeDATE.getSizeLDS:return "
                    + this.sizeForLds, this);

            OracleLog.recursiveTrace = false;
        }

        return this.sizeForLds;
    }

    public int getAlignLDS(byte[] FDO) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeDATE.getAlignLDS( FDO = " + FDO + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sizeForLds == 0) {
            this.sizeForLds = Util.fdoGetSize(FDO, 11);
            this.alignForLds = Util.fdoGetAlign(FDO, 11);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeDATE.getAlignLDS:return "
                    + this.alignForLds, this);

            OracleLog.recursiveTrace = false;
        }

        return this.alignForLds;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeDATE.unpickle80rec( context = "
                    + context + ", format =" + format + ", type = " + type + ", map =" + map + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        switch (format) {
        case 1:
            if (context.isNull(this.nullOffset)) {
                return null;
            }
            context.skipTo(context.ldsOffsets[this.ldsOffset]);

            break;
        case 2:
            if ((context.readByte() & 0x1) != 1)
                break;
            context.skipBytes(8);

            return null;
        case 3:
            break;
        }

        DatabaseError.throwSqlException(1, "format=" + format);

        if (type == 9) {
            context.skipBytes(8);

            return null;
        }

        byte[] bytes = context.image();
        int offset = context.absoluteOffset();

        byte[] internal_bytes = new byte[7];
        int kopi0dy;
        if (context.bigEndian)
            kopi0dy = (bytes[offset] & 0xFF) * 256 + (bytes[(offset + 1)] & 0xFF);
        else {
            kopi0dy = (bytes[(offset + 1)] & 0xFF) * 256 + (bytes[offset] & 0xFF);
        }
        if (kopi0dy < 0) {
            internal_bytes[0] = (byte) (100 - -kopi0dy / 100);
            internal_bytes[1] = (byte) (100 - -kopi0dy % 100);
        } else {
            internal_bytes[0] = (byte) (kopi0dy / 100 + 100);
            internal_bytes[1] = (byte) (kopi0dy % 100 + 100);
        }

        internal_bytes[2] = bytes[(offset + 2)];
        internal_bytes[3] = bytes[(offset + 3)];
        internal_bytes[4] = (byte) (bytes[(offset + 4)] + 1);
        internal_bytes[5] = (byte) (bytes[(offset + 5)] + 1);
        internal_bytes[6] = (byte) (bytes[(offset + 6)] + 1);

        context.skipBytes(8);

        return toObject(internal_bytes, type, map);
    }

    protected Object toObject(byte[] bytes, int type, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeDATE.toObject( bytes = " + bytes
                    + ", type = " + type + ", map = " + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        if (type == 1)
            return new DATE(bytes);
        if (type == 2)
            return DATE.toTimestamp(bytes);
        if (type == 3) {
            return bytes;
        }
        DatabaseError.throwSqlException(59, bytes);

        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeDATE.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeDATE.readObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeDATE"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeDATE JD-Core Version: 0.6.0
 */