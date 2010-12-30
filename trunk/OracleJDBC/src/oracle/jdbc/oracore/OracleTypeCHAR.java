package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;

public class OracleTypeCHAR extends OracleType implements Serializable {
    static final long serialVersionUID = -6899444518695804629L;
    int form;
    int charset;
    int length;
    int characterSemantic;
    private transient OracleConnection connection;
    private short pickleCharaterSetId;
    private transient CharacterSet pickleCharacterSet;
    private short pickleNcharCharacterSet;
    static final int SQLCS_IMPLICIT = 1;
    static final int SQLCS_NCHAR = 2;
    static final int SQLCS_EXPLICIT = 3;
    static final int SQLCS_FLEXIBLE = 4;
    static final int SQLCS_LIT_NULL = 5;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    protected OracleTypeCHAR() {
    }

    public OracleTypeCHAR(OracleConnection conn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCHAR (" + this.connection + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.form = 0;
        this.charset = 0;
        this.length = 0;
        this.connection = conn;
        this.pickleCharaterSetId = 0;
        this.pickleNcharCharacterSet = 0;
        this.pickleCharacterSet = null;
        try {
            this.pickleCharaterSetId = this.connection.getStructAttrCsId();
        } catch (SQLException ex) {
            if (TRACE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                ex.printStackTrace(pw);
                OracleLog.print(this, 16, 32, 64, "OracleTypeCHAR( " + ex.toString()
                        + sw.toString());
            }

            this.pickleCharaterSetId = -1;
        }

        this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
    }

    protected OracleTypeCHAR(OracleConnection conn, int typecode) {
        super(typecode);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCHAR (connection = " + this.connection
                    + ", typecode = " + typecode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.form = 0;
        this.charset = 0;
        this.length = 0;
        this.connection = conn;
        this.pickleCharaterSetId = 0;
        this.pickleNcharCharacterSet = 0;
        this.pickleCharacterSet = null;
        try {
            this.pickleCharaterSetId = this.connection.getStructAttrCsId();
        } catch (SQLException ex) {
            if (TRACE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                ex.printStackTrace(pw);
                OracleLog.print(this, 16, 32, 64, "OracleTypeCHAR( " + ex.toString()
                        + sw.toString());
            }

            this.pickleCharaterSetId = -1;
        }

        this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.toDatum( object = " + value
                    + ", connection = " + this.connection + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value == null) {
            return null;
        }
        CHAR datum = (value instanceof CHAR) ? (CHAR) value : new CHAR(value,
                this.pickleCharacterSet);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.toDatum:return " + datum, this);

            OracleLog.recursiveTrace = false;
        }

        return datum;
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.toDatumArray( object = " + obj
                    + ", connection = " + this.connection + ", begin index = " + beginIdx
                    + ", count = " + count + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = null;

        if (obj != null) {
            if (((obj instanceof Object[])) && (!(obj instanceof char[][]))) {
                return super.toDatumArray(obj, conn, beginIdx, count);
            }
            datumArray = cArrayToDatumArray(obj, conn, beginIdx, count);
        }

        return datumArray;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.parseTDSrec( tdsReader  = "
                    + tdsReader + ")", this);

            OracleLog.recursiveTrace = false;
        }

        super.parseTDSrec(tdsReader);
        try {
            this.length = tdsReader.readShort();
            this.form = tdsReader.readByte();
            this.characterSemantic = (this.form & 0x80);
            this.form &= 127;
            this.charset = tdsReader.readShort();
        } catch (SQLException ex) {
            DatabaseError.throwSqlException(47, "parseTDS");
        }

        if ((this.form != 2) || (this.pickleNcharCharacterSet != 0)) {
            return;
        }

        try {
            this.pickleNcharCharacterSet = this.connection.getStructAttrNCsId();
        } catch (SQLException ex) {
            if (TRACE) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);

                ex.printStackTrace(pw);
                OracleLog.print(this, 16, 32, 64, "OracleTypeCHAR( " + ex.toString()
                        + sw.toString());
            }

            this.pickleNcharCharacterSet = 2000;
        }

        this.pickleCharaterSetId = this.pickleNcharCharacterSet;
        this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int type, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.unpickle80rec( context = "
                    + context + ", format = " + format + ", type = " + type + ", map = " + map
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        switch (format) {
        case 1:
            if (context.isNull(this.nullOffset)) {
                return null;
            }
            context.skipTo(context.ldsOffsets[this.ldsOffset]);

            if (type == 9) {
                context.skipBytes(6);

                return null;
            }

            long offset = context.readLong();

            if (offset == 0L) {
                context.skipBytes(2);
                context.mark();
            } else {
                context.markAndSkip(offset);
            }
            byte[] val = context.readLengthBytes();

            context.reset();

            return toObject(val, type, map);
        case 2:
            if ((context.readByte() & 0x1) != 1)
                break;
            context.skipBytes(4);

            return null;
        case 3:
            if (type == 9) {
                context.skipLengthBytes();

                return null;
            }

            return toObject(context.readLengthBytes(), type, map);
        }

        DatabaseError.throwSqlException(1, "format=" + format);

        return null;
    }

    protected int pickle81(PickleContext context, Datum data) throws SQLException {
        CHAR dbchar = getDbCHAR(data);

        if ((this.characterSemantic != 0) && (this.form != 2)) {
            if (dbchar.getString().length() > this.length) {
                DatabaseError.throwSqlException(72, "\"" + dbchar.getString() + "\"");
            }

        } else if (dbchar.getLength() > this.length) {
            DatabaseError.throwSqlException(72, "\"" + dbchar.getString() + "\"");
        }

        return super.pickle81(context, dbchar);
    }

    protected Object toObject(byte[] bytes, int type, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.toObject( bytes = " + bytes
                    + ", type = " + type + ", map = " + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((bytes == null) || (bytes.length == 0)) {
            return null;
        }

        CHAR result = null;

        switch (this.form) {
        case 1:
        case 2:
            result = new CHAR(bytes, this.pickleCharacterSet);

            break;
        case 3:
        case 4:
        case 5:
            result = new CHAR(bytes, null);
        }

        if (type == 1)
            return result;
        if (type == 2)
            return result.stringValue();
        if (type == 3) {
            return bytes;
        }
        DatabaseError.throwSqlException(59, bytes);

        return null;
    }

    private CHAR getDbCHAR(Datum data) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.getDbCHAR( datum = " + data
                    + ") and form = " + this.form + " character semantic = "
                    + this.characterSemantic + " length = " + this.length, this);

            OracleLog.recursiveTrace = false;
        }

        CHAR in_char_obj = (CHAR) data;
        CHAR db_char_obj = null;

        if (in_char_obj.getCharacterSet().getOracleId() == this.pickleCharaterSetId) {
            db_char_obj = in_char_obj;
        } else {
            try {
                db_char_obj = new CHAR(in_char_obj.toString(), this.pickleCharacterSet);
            } catch (SQLException ex) {
                if (TRACE) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);

                    ex.printStackTrace(pw);
                    OracleLog.print(this, 16, 32, 64, "OracleTypeCHAR.getDbCHAR( " + ex.toString()
                            + sw.toString());
                }

                db_char_obj = in_char_obj;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.getDbCHAR:return " + db_char_obj,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return db_char_obj;
    }

    private Datum[] cArrayToDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeCHAR.cArrayToDatumArray( object = "
                    + obj + ", connection = " + this.connection + ", begin index = " + beginIdx
                    + ", count = " + count + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = null;

        if (obj != null) {
            if ((obj instanceof char[][])) {
                char[][] parray = (char[][]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new String(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }
            } else if ((obj instanceof boolean[])) {
                boolean[] parray = (boolean[]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new Boolean(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }
            } else if ((obj instanceof short[])) {
                short[] parray = (short[]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new Integer(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }

            } else if ((obj instanceof int[])) {
                int[] parray = (int[]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new Integer(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }
            } else if ((obj instanceof long[])) {
                long[] parray = (long[]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new Long(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }
            } else if ((obj instanceof float[])) {
                float[] parray = (float[]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new Float(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }
            } else if ((obj instanceof double[])) {
                double[] parray = (double[]) obj;
                int len = (int) (count == -1 ? parray.length : Math.min(parray.length - beginIdx
                        + 1L, count));

                datumArray = new Datum[len];

                for (int i = 0; i < len; i++) {
                    datumArray[i] = new CHAR(new Double(parray[((int) beginIdx + i - 1)]),
                            this.pickleCharacterSet);
                }
            } else {
                DatabaseError.throwSqlException(59, obj);
            }

        }

        return datumArray;
    }

    public int getLength() {
        return this.length;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCHAR.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.form);
        out.writeInt(this.charset);
        out.writeInt(this.length);
        out.writeInt(this.characterSemantic);
        out.writeShort(this.pickleCharaterSetId);
        out.writeShort(this.pickleNcharCharacterSet);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeCHAR.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.form = in.readInt();
        this.charset = in.readInt();
        this.length = in.readInt();
        this.characterSemantic = in.readInt();
        this.pickleCharaterSetId = in.readShort();
        this.pickleNcharCharacterSet = in.readShort();

        if (this.pickleNcharCharacterSet != 0)
            this.pickleCharacterSet = CharacterSet.make(this.pickleNcharCharacterSet);
        else
            this.pickleCharacterSet = CharacterSet.make(this.pickleCharaterSetId);
    }

    public void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;
    }

    public boolean isNCHAR() throws SQLException {
        return this.form == 2;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeCHAR"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeCHAR JD-Core Version: 0.6.0
 */