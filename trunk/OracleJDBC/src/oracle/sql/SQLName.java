package oracle.sql;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class SQLName implements Serializable {
    static boolean DEBUG = false;
    static boolean s_parseAllFormat = false;
    static final long serialVersionUID = 2266340348729491526L;
    String name;
    String schema;
    String simple;
    int version;
    boolean synonym;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    protected SQLName() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.SQLName(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public SQLName(String name, OracleConnection connection) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.SQLName( name=" + name + ", connection="
                    + connection + ")", this);

            OracleLog.recursiveTrace = false;
        }

        init(name, connection);

        this.version = 2;
        this.synonym = false;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.SQLName: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public SQLName(String schema, String typename, OracleConnection connection) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.SQLName( schema=" + schema
                    + ", typename=" + typename + ", connection=" + connection + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.schema = schema;
        this.simple = typename;
        this.name = (this.schema + "." + this.simple);

        this.version = 2;
        this.synonym = false;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.SQLName: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void init(String sql_name, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.init( sql_name=" + sql_name + ", conn="
                    + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String[] _schema = new String[1];
        String[] _type = new String[1];

        if (parse(sql_name, _schema, _type, true)) {
            this.schema = _schema[0];
            this.simple = _type[0];
        } else {
            this.schema = conn.getUserName();
            this.simple = _type[0];
        }

        this.name = (this.schema + "." + this.simple);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.init: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public String getName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.getName(): return: " + this.name, this);

            OracleLog.recursiveTrace = false;
        }

        return this.name;
    }

    public String getSchema() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.getSchema(): return: " + this.schema,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.schema;
    }

    public String getSimpleName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "SQLName.getSimpleName(): return: " + this.simple, this);

            OracleLog.recursiveTrace = false;
        }

        return this.simple;
    }

    public int getVersion() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.getVersion(): return: " + this.version,
                                      this);

            OracleLog.recursiveTrace = false;
        }

        return this.version;
    }

    public static boolean parse(String sql_name, String[] schema_name, String[] type_name)
            throws SQLException {
        return parse(sql_name, schema_name, type_name, s_parseAllFormat);
    }

    public static boolean parse(String sql_name, String[] schema_name, String[] type_name,
            boolean l_parseAllFormat) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.parse( sql_name=" + sql_name
                    + ", schema_name=" + schema_name + ", type_name=" + type_name
                    + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if (sql_name == null) {
            return false;
        }
        if ((schema_name == null) || (schema_name.length < 1) || (type_name == null)
                || (type_name.length < 1)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(
                             Level.FINE,
                             "SQLName.parse: Invalid argument, 'type_name' and 'schema_name' should not be empty strings. An exception is thrown.");

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68);
        }

        if (!l_parseAllFormat) {
            int idxDot = sql_name.indexOf(".");

            if (idxDot < 0) {
                type_name[0] = sql_name;

                return false;
            }

            schema_name[0] = sql_name.substring(0, idxDot);
            type_name[0] = sql_name.substring(idxDot + 1);

            return true;
        }

        int length = sql_name.length();
        int idxOpenQuote = sql_name.indexOf("\"");
        int idxCloseQuote = sql_name.indexOf("\"", idxOpenQuote + 1);
        int idxDot = -1;

        if (idxOpenQuote < 0) {
            idxDot = sql_name.indexOf(".");

            if (idxDot < 0) {
                type_name[0] = sql_name;

                return false;
            }

            schema_name[0] = sql_name.substring(0, idxDot);
            type_name[0] = sql_name.substring(idxDot + 1);

            return true;
        }

        if (idxOpenQuote == 0) {
            if (idxCloseQuote == length - 1) {
                type_name[0] = sql_name.substring(idxOpenQuote + 1, idxCloseQuote);

                return false;
            }

            idxDot = sql_name.indexOf(".", idxCloseQuote);
            schema_name[0] = sql_name.substring(idxOpenQuote + 1, idxCloseQuote);

            idxOpenQuote = sql_name.indexOf("\"", idxDot);
            idxCloseQuote = sql_name.indexOf("\"", idxOpenQuote + 1);

            if (idxOpenQuote < 0) {
                type_name[0] = sql_name.substring(idxDot + 1);

                return true;
            }

            type_name[0] = sql_name.substring(idxOpenQuote + 1, idxCloseQuote);

            return true;
        }

        idxDot = sql_name.indexOf(".");
        schema_name[0] = sql_name.substring(0, idxDot);
        type_name[0] = sql_name.substring(idxOpenQuote + 1, idxCloseQuote);

        return true;
    }

    public static void setHandleDoubleQuote(boolean enable) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.setHandleDoubleQuote( enable=" + enable
                    + "): return");

            OracleLog.recursiveTrace = false;
        }

        s_parseAllFormat = enable;
    }

    public static boolean getHandleDoubleQuote() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.getHandleDoubleQuote(): return: "
                    + s_parseAllFormat);

            OracleLog.recursiveTrace = false;
        }

        return s_parseAllFormat;
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof SQLName))
            return false;
        return ((SQLName) o).name.equals(this.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public String toString() {
        return this.name;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.writeObject( out=" + out + ")", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeUTF(this.name);
        out.writeUTF(this.schema);
        out.writeUTF(this.simple);
        out.writeInt(this.version);
        out.writeBoolean(this.synonym);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.writeObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.readObject( in=" + in + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.name = in.readUTF();
        this.schema = in.readUTF();
        this.simple = in.readUTF();
        this.version = in.readInt();
        this.synonym = in.readBoolean();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "SQLName.readObject: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.SQLName"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.SQLName JD-Core Version: 0.6.0
 */