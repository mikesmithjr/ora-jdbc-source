package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleTypeADT;

public class REF extends DatumWithConnection implements Ref, Serializable, Cloneable {
    static final boolean DEBUG = false;
    static final long serialVersionUID = 1328446996944583167L;
    String typename;
    transient StructDescriptor descriptor;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public String getBaseTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getBaseTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.typename == null) {
            if (this.descriptor != null) {
                this.typename = this.descriptor.getName();
            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(
                                 Level.SEVERE,
                                 "REF.getBaseTypeName: this object is not valid because typename and descriptor are null. An exception is thrown.",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(52);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getBaseTypeName: return: " + this.typename,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.typename;
    }

    public REF(String typename, Connection conn, byte[] bytes) throws SQLException {
        super(bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.REF( typename=" + typename + ", conn="
                    + conn + ", bytes=" + bytes + ")" + " -- after super() -- ", this);

            OracleLog.recursiveTrace = false;
        }

        if ((conn == null) || (typename == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "REF.REF: Invalid argument, 'conn' and 'typename' should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        this.typename = typename;
        this.descriptor = null;

        setPhysicalConnectionOf(conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.REF: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public REF(StructDescriptor desc, Connection conn, byte[] bytes) throws SQLException {
        super(bytes);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.REF( desc=" + desc + ", conn=" + conn
                    + ", bytes=" + bytes + ") -- after super() --", this);

            OracleLog.recursiveTrace = false;
        }

        if ((conn == null) || (desc == null)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "REF.REF: Invalid argument, 'conn' and 'desc' should not be null. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        this.descriptor = desc;

        setPhysicalConnectionOf(conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.REF: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Object getValue(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getValue( map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = getSTRUCT();
        Object ret = s != null ? s.toJdbc(map) : null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object getValue() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getValue()", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = getSTRUCT();
        Object ret = s != null ? s.toJdbc() : null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getValue: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized STRUCT getSTRUCT() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getSTRUCT()", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT ret = null;

        OraclePreparedStatement pstmt = (OraclePreparedStatement) getInternalConnection()
                .prepareStatement("select deref(:1) from dual");

        pstmt.setRowPrefetch(1);
        pstmt.setREF(1, this);

        OracleResultSet rset = (OracleResultSet) pstmt.executeQuery();
        try {
            if (rset.next()) {
                ret = rset.getSTRUCT(1);
            } else {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger
                            .log(Level.SEVERE,
                                 "REF.getSTRUCT: Invalid REF. An exception is thrown.", this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(52);
            }
        } finally {
            rset.close();

            rset = null;

            pstmt.close();

            pstmt = null;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getSTRUCT: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public synchronized void setValue(Object value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.setValue( value=" + value + ")", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT struct = STRUCT.toSTRUCT(value, getInternalConnection());

        if (struct.getInternalConnection() != getInternalConnection()) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.SEVERE,
                             "REF.setValue: Incompatible connection object. An exception is thrown.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(77, "Incompatible connection object");
        }

        if (!getBaseTypeName().equals(struct.getSQLTypeName())) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "REF.setValue: Incompatible type. An exception is thrown.", this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(77, "Incompatible type");
        }

        byte[] pickled_bytes = struct.toBytes();

        byte[] toid_bytes = struct.getDescriptor().getOracleTypeADT().getTOID();

        CallableStatement cstmt = null;
        try {
            cstmt = getInternalConnection()
                    .prepareCall(
                                 "begin :1 := dbms_pickler.update_through_ref (:2, :3, :4, :5); end;");

            cstmt.registerOutParameter(1, 2);

            cstmt.setBytes(2, shareBytes());
            cstmt.setInt(3, 0);
            cstmt.setBytes(4, toid_bytes);
            cstmt.setBytes(5, pickled_bytes);

            cstmt.execute();

            int result = 0;

            if ((result = cstmt.getInt(1)) != 0) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.SEVERE, "REF.setValue: Error ORA-" + result
                            + ". An exception is thrown.", this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(77, "ORA-" + result);
            }

        } finally {
            if (cstmt != null) {
                cstmt.close();
            }
            cstmt = null;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.setValue: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public StructDescriptor getDescriptor() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getDescriptor()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.descriptor == null) {
            this.descriptor = StructDescriptor.createDescriptor(this.typename,
                                                                getInternalConnection());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getDescriptor: return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.descriptor;
    }

    public String getSQLTypeName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getSQLTypeName()", this);

            OracleLog.recursiveTrace = false;
        }

        String ret = getBaseTypeName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getSQLTypeName: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object getObject(Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getObject( map=" + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = getSTRUCT();
        Object ret = s != null ? s.toJdbc(map) : null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getObject: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public Object getObject() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getObject()", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = getSTRUCT();
        Object ret = s != null ? s.toJdbc() : null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.getObject: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public void setObject(Object value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.INFO, "REF.setObject()", this);

            OracleLog.recursiveTrace = false;
        }

        PreparedStatement pstmt = getInternalConnection()
                .prepareStatement("call sys.utl_ref.update_object( :1, :2 )");

        pstmt.setRef(1, this);
        pstmt.setObject(2, value);
        pstmt.execute();
        pstmt.close();
    }

    public Object toJdbc() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.toJdbc(): return", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public boolean isConvertibleTo(Class jClass) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.isConvertibleTo( jClass=" + jClass
                    + "): return: false (always)", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public Object makeJdbcArray(int arraySize) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.makeJdbcArray( arraySize=" + arraySize
                    + "): return", this);

            OracleLog.recursiveTrace = false;
        }

        return new REF[arraySize];
    }

    public Object clone() throws CloneNotSupportedException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.clone()", this);

            OracleLog.recursiveTrace = false;
        }

        Object ret = null;
        try {
            ret = new REF(getBaseTypeName(), getInternalConnection(), getBytes());
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "REF.clone: An exception is caught and thrown. "
                                                  + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            throw new CloneNotSupportedException(e.getMessage());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.clone: return", this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public boolean equals(Object obj) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.equals( obj=" + obj + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean ret = false;
        try {
            ret = ((obj instanceof REF)) && (super.equals(obj))
                    && (getBaseTypeName().equals(((REF) obj).getSQLTypeName()));
        } catch (Exception e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "REF.equals: An exception has been caught. "
                                                  + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.equals: return: " + ret, this);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public int hashCode() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.hashCode()", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] pref = shareBytes();
        int hashcode = 0;

        if ((pref[2] & 0x5) == 5) {
            for (int i = 0; i < 4; i++) {
                hashcode *= 256;
                hashcode += (pref[(8 + i)] & 0xFF);
            }
        } else if ((pref[2] & 0x3) == 3) {
            for (int i = 0; (i < 4) && (i < pref.length); i++) {
                hashcode *= 256;
                hashcode += (pref[(6 + i)] & 0xFF);
            }
        } else if ((pref[2] & 0x2) == 2) {
            for (int i = 0; i < 4; i++) {
                hashcode *= 256;
                hashcode += (pref[(8 + i)] & 0xFF);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.hashCode: return: " + hashcode, this);

            OracleLog.recursiveTrace = false;
        }

        return hashcode;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.writeObject( out=" + out + ")", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeObject(shareBytes());
        try {
            out.writeUTF(getBaseTypeName());
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.SEVERE,
                                          "REF.writeObject: Exception caught and thrown."
                                                  + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            throw new IOException("SQLException ORA-" + e.getErrorCode() + " " + e.getMessage());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.writeObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.readObject( in=" + in + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setBytes((byte[]) in.readObject());

        this.typename = in.readUTF();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "REF.readObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public Connection getJavaSqlConnection() throws SQLException {
        return super.getJavaSqlConnection();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.REF"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name: oracle.sql.REF
 * JD-Core Version: 0.6.0
 */