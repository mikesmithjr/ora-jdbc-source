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
import oracle.sql.REF;
import oracle.sql.StructDescriptor;

public class OracleTypeREF extends OracleNamedType implements Serializable {
    static final long serialVersionUID = 3186448715463064573L;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    protected OracleTypeREF() {
    }

    public OracleTypeREF(String sql_name, OracleConnection conn) throws SQLException {
        super(sql_name, conn);
    }

    public OracleTypeREF(OracleTypeADT parent, int idx, OracleConnection conn) {
        super(parent, idx, conn);
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        REF datum = null;

        if (value != null) {
            if ((value instanceof REF))
                datum = (REF) value;
            else {
                DatabaseError.throwSqlException(59, value);
            }
        }

        return datum;
    }

    public int getTypeCode() {
        return 2006;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int style, Map map)
            throws SQLException {
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

            byte[] final_locator = context.readPtrBytes();

            context.reset();

            return toObject(final_locator, style, null);
        case 2:
            if ((context.readByte() & 0x1) != 1)
                break;
            context.skipPtrBytes();

            return null;
        case 3:
            if (style == 9) {
                context.skipPtrBytes();

                return null;
            }

            return toObject(context.readPtrBytes(), style, null);
        }

        DatabaseError.throwSqlException(1, "format=" + format);

        return null;
    }

    protected Object toObject(byte[] val, int style, Map map) throws SQLException {
        if ((val == null) || (val.length == 0)) {
            return null;
        }
        if ((style == 1) || (style == 2)) {
            StructDescriptor desc = createStructDescriptor();

            return new REF(desc, this.connection, val);
        }
        if (style == 3) {
            return val;
        }

        DatabaseError.throwSqlException(59, val);

        return null;
    }

    StructDescriptor createStructDescriptor() throws SQLException {
        if (this.descriptor == null) {
            if ((this.sqlName == null) && (getFullName(false) == null)) {
                OracleTypeADT otype = new OracleTypeADT(getParent(), getOrder(), this.connection);

                this.descriptor = new StructDescriptor(otype, this.connection);
            } else {
                this.descriptor = StructDescriptor.createDescriptor(this.sqlName, this.connection);
            }
        }
        return (StructDescriptor) this.descriptor;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeREF.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeREF.readObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeREF"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeREF JD-Core Version: 0.6.0
 */