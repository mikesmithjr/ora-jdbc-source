package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;

public class OracleTypeFLOAT extends OracleType implements Serializable {
    static final long serialVersionUID = 4088841548269771109L;
    int precision;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        return OracleTypeNUMBER.toNUMBER(value, conn);
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        return OracleTypeNUMBER.toNUMBERArray(obj, conn, beginIdx, count);
    }

    public int getTypeCode() {
        return 6;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        this.nullOffset = tdsReader.nullOffset;
        this.ldsOffset = tdsReader.ldsOffset;

        tdsReader.nullOffset += 1;
        tdsReader.ldsOffset += 1;

        this.precision = tdsReader.readUnsignedByte();
    }

    public int getScale() {
        return -127;
    }

    public int getPrecision() {
        return this.precision;
    }

    public int getSizeLDS(byte[] FDO) {
        if (this.sizeForLds == 0) {
            this.sizeForLds = Util.fdoGetSize(FDO, 12);
            this.alignForLds = Util.fdoGetAlign(FDO, 12);
        }

        return this.sizeForLds;
    }

    public int getAlignLDS(byte[] FDO) {
        if (this.sizeForLds == 0) {
            this.sizeForLds = Util.fdoGetSize(FDO, 12);
            this.alignForLds = Util.fdoGetAlign(FDO, 12);
        }

        return this.alignForLds;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        return OracleTypeNUMBER.numericUnpickle80rec(this.ldsOffset, this.nullOffset, context,
                                                     format, type, map);
    }

    protected static Object unpickle80NativeArray(UnpickleContext context, long beginIdx, int size,
            int type, int format) throws SQLException {
        return OracleTypeNUMBER.unpickle80NativeArray(context, beginIdx, size, type, format);
    }

    protected static Object unpickle81NativeArray(PickleContext context, long beginIdx, int size,
            int type) throws SQLException {
        return OracleTypeNUMBER.unpickle81NativeArray(context, beginIdx, size, type);
    }

    protected Object toObject(byte[] bytes, int type, Map map) throws SQLException {
        return OracleTypeNUMBER.toNumericObject(bytes, type, map);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeFLOAT.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.precision);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeFLOAT.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.precision = in.readInt();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeFLOAT"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeFLOAT JD-Core Version: 0.6.0
 */