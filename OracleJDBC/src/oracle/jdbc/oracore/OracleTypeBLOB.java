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
import oracle.sql.BLOB;
import oracle.sql.Datum;

public class OracleTypeBLOB extends OracleType implements Serializable {
    static final long serialVersionUID = -2311211431562030662L;
    static int fixedDataSize = 86;
    transient OracleConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    protected OracleTypeBLOB() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeBLOB ()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeBLOB(OracleConnection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeBLOB (" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = conn;
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeBLOB.toDatum(value = " + value
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        BLOB datum = null;

        if (value != null) {
            if ((value instanceof BLOB))
                datum = (BLOB) value;
            else {
                DatabaseError.throwSqlException(59, value);
            }
        }

        return datum;
    }

    public int getTypeCode() {
        return 2004;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        return lobUnpickle80rec(this, this.nullOffset, this.ldsOffset, context, format, type,
                                fixedDataSize);
    }

    protected static Object lobUnpickle80rec(OracleType t, int null_offset, int lds_offset,
            UnpickleContext context, int format, int style, int data_size) throws SQLException {
        switch (format) {
        case 1:
            if (context.isNull(null_offset)) {
                return null;
            }
            context.skipTo(context.ldsOffsets[lds_offset]);

            if (style == 9) {
                context.skipBytes(4);

                return null;
            }

            context.markAndSkip();

            byte[] final_locator = context.readPtrBytes();

            context.reset();

            return t.toObject(final_locator, style, null);
        case 2:
            if ((context.readByte() & 0x1) != 1)
                break;
            context.skipPtrBytes();

            return null;
        case 3:
            long endOffset = context.offset() + data_size;

            if (style == 9) {
                context.skipTo(endOffset);

                return null;
            }

            byte[] final_locator2 = context.readPtrBytes();

            if (context.offset() < endOffset) {
                context.skipTo(endOffset);
            }
            return t.toObject(final_locator2, style, null);
        }

        DatabaseError.throwSqlException(1, "format=" + format);

        return null;
    }

    protected Object toObject(byte[] bytes, int style, Map map) throws SQLException {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        switch (style) {
        case 1:
        case 2:
            return this.connection.createBlob(bytes);
        case 3:
            return bytes;
        }

        DatabaseError.throwSqlException(59, bytes);

        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeBLOB.readObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeBLOB.readObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeBLOB"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeBLOB JD-Core Version: 0.6.0
 */