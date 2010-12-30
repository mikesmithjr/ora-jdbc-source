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
import oracle.sql.CLOB;
import oracle.sql.Datum;

public class OracleTypeCLOB extends OracleType implements Serializable {
    static final long serialVersionUID = 1122821330765834411L;
    static int fixedDataSize = 86;
    transient OracleConnection connection;
    int form;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    protected OracleTypeCLOB() {
    }

    public OracleTypeCLOB(OracleConnection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCLOB (" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = conn;
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        CLOB datum = null;

        if (value != null) {
            if ((value instanceof CLOB))
                datum = (CLOB) value;
            else {
                DatabaseError.throwSqlException(59, value);
            }
        }

        return datum;
    }

    public int getTypeCode() {
        return 2005;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        return OracleTypeBLOB.lobUnpickle80rec(this, this.nullOffset, this.ldsOffset, context,
                                               format, type, fixedDataSize);
    }

    protected Object toObject(byte[] bytes, int type, Map map) throws SQLException {
        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }
        if ((type == 1) || (type == 2)) {
            return this.connection.createClob(bytes);
        }

        if (type == 3) {
            return bytes;
        }
        DatabaseError.throwSqlException(59, bytes);

        return null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCLOB.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCLOB.readObject()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;
    }

    public boolean isNCHAR() throws SQLException {
        return this.form == 2;
    }

    public void setForm(int formOfUse) {
        this.form = formOfUse;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeCLOB"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeCLOB JD-Core Version: 0.6.0
 */