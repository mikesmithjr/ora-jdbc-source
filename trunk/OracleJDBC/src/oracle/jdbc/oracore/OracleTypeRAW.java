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
import oracle.sql.RAW;

public class OracleTypeRAW extends OracleType implements Serializable {
    static final long serialVersionUID = -6083664758336974576L;
    int length;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public OracleTypeRAW() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeRAW ()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeRAW(int typecode) {
        super(typecode);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger
                    .log(Level.FINER, "OracleTypeRAW (typcode = " + typecode + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeRAW.toDatum (value = " + value
                    + ", conn = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        RAW datum = null;

        if (value != null) {
            try {
                if ((value instanceof RAW))
                    datum = (RAW) value;
                else
                    datum = new RAW(value);
            } catch (SQLException e) {
                DatabaseError.throwSqlException(59, value);
            }

        }

        return datum;
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeRAW.toDatumArray( obj = " + obj
                    + ", conn = " + conn + ", beginIdx = " + beginIdx + ", count = " + count + ")",
                                    this);

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

        return datumArray;
    }

    public int getTypeCode() {
        return -2;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeRAW.parseTDSrec (tdsReader = "
                    + tdsReader + ")", this);

            OracleLog.recursiveTrace = false;
        }

        super.parseTDSrec(tdsReader);

        this.length = tdsReader.readShort();
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int style, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeRAW.unpickle80rec (context ="
                    + context + ", format = " + format + ", style = " + style + ", map = " + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        switch (format) {
        case 1:
            if (context.isNull(this.nullOffset)) {
                return null;
            }
            context.skipTo(context.ldsOffsets[this.ldsOffset]);

            if (style == 9) {
                context.skipBytes(4);

                return null;
            }

            context.markAndSkip();

            byte[] val = context.readLengthBytes();

            context.reset();

            return toObject(val, style, map);
        case 2:
            if (((context.readByte() & 0x1) == 1) || (style == 9)) {
                context.skipLengthBytes();

                return null;
            }

            return toObject(context.readLengthBytes(), style, map);
        case 3:
            if (style == 9) {
                context.skipLengthBytes();

                return null;
            }

            return toObject(context.readLengthBytes(), style, map);
        }

        DatabaseError.throwSqlException(1, "format=" + format);

        return null;
    }

    protected int pickle81(PickleContext ctx, Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeRAW.pickle81 (ctx = " + ctx
                    + ", data = " + data + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (data.getLength() > this.length) {
            DatabaseError.throwSqlException(72, this);
        }
        int len = ctx.writeLength((int) data.getLength());

        len += ctx.writeData(data.shareBytes());

        return len;
    }

    public int getLength() {
        return this.length;
    }

    protected Object toObject(byte[] val, int style, Map map) throws SQLException {
        if ((val == null) || (val.length == 0)) {
            return null;
        }
        switch (style) {
        case 1:
            return new RAW(val);
        case 2:
        case 3:
            return val;
        }

        DatabaseError.throwSqlException(59, val);

        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeRAW.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.length);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeRAW.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.length = in.readInt();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeRAW"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeRAW JD-Core Version: 0.6.0
 */