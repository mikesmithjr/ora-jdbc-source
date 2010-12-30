package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.ObjectData;
import oracle.jdbc.internal.OracleCallableStatement;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.BLOB;
import oracle.sql.Datum;
import oracle.sql.JAVA_STRUCT;
import oracle.sql.NUMBER;
import oracle.sql.SQLName;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public class OracleTypeADT extends OracleNamedType implements Serializable {
    static final long serialVersionUID = 3031304012507165702L;
    static final int S_TOP = 1;
    static final int S_EMBEDDED = 2;
    static final int S_UPT_ADT = 4;
    static final int S_JAVA_OBJECT = 16;
    static final int S_FINAL_TYPE = 32;
    static final int S_SUB_TYPE = 64;
    static final int S_ATTR_TDS = 128;
    static final int S_HAS_METADATA = 256;
    static final int S_TDS_PARSED = 512;
    private int statusBits = 1;

    int tdsVersion = -9999;
    static final int KOPT_V80 = 1;
    static final int KOPT_V81 = 2;
    static final int KOPT_VNFT = 3;
    static final int KOPT_VERSION = 3;
    boolean endOfAdt = false;

    int typeVersion = -1;

    byte[] lds = null;
    long[] ldsOffsetArray = null;
    long fixedDataSize = -1L;
    int alignmentRequirement = -1;

    OracleType[] attrTypes = null;
    String[] attrNames;
    String[] attrTypeNames;
    public long tdoCState = 0L;

    byte[] toid = null;
    byte[] fdo;
    int charSetId;
    int charSetForm;
    boolean bigEndian;
    int flattenedAttrNum;
    transient int opcode;
    transient int idx = 1;
    static final int CURRENT_USER_OBJECT = 0;
    static final int CURRENT_USER_SYNONYM = 1;
    static final int CURRENT_USER_PUBLIC_SYNONYM = 2;
    static final int OTHER_USER_OBJECT = 3;
    static final int OTHER_USER_SYNONYM = 4;
    static final int PUBLIC_SYNONYM = 5;
    static final int BREAK = 6;
    static final String[] sqlString = {
            "SELECT /*+ RULE */ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM USER_TYPE_ATTRS WHERE TYPE_NAME = :1 ORDER BY ATTR_NO",
            "SELECT /*+ RULE */ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM USER_TYPE_ATTRS WHERE TYPE_NAME in (SELECT TABLE_NAME FROM USER_SYNONYMS START WITH SYNONYM_NAME = :1 CONNECT BY PRIOR TABLE_NAME = SYNONYM_NAME UNION SELECT :1 FROM DUAL) ORDER BY ATTR_NO",
            "SELECT /*+RULE*/ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM USER_TYPE_ATTRS WHERE TYPE_NAME IN (SELECT TABLE_NAME FROM ALL_SYNONYMS START WITH SYNONYM_NAME = :1 AND  OWNER = 'PUBLIC' CONNECT BY PRIOR TABLE_NAME = SYNONYM_NAME AND TABLE_OWNER = OWNER UNION SELECT :2  FROM DUAL) ORDER BY ATTR_NO",
            "SELECT /*+ RULE */ ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM ALL_TYPE_ATTRS WHERE OWNER = :1 AND TYPE_NAME = :2 ORDER BY ATTR_NO",
            "SELECT /*+ RULE */ ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM ALL_TYPE_ATTRS WHERE OWNER = (SELECT TABLE_OWNER FROM ALL_SYNONYMS WHERE SYNONYM_NAME=:1) AND TYPE_NAME = (SELECT TABLE_NAME FROM ALL_SYNONYMS WHERE SYNONYM_NAME=:2) ORDER BY ATTR_NO",
            "DECLARE /*+RULE*/  the_owner VARCHAR2(100);   the_type  VARCHAR2(100); begin  SELECT /*+ RULE */TABLE_NAME, TABLE_OWNER INTO THE_TYPE, THE_OWNER  FROM ALL_SYNONYMS  WHERE TABLE_NAME IN (SELECT TYPE_NAME FROM ALL_TYPES)  START WITH SYNONYM_NAME = :1 AND OWNER = 'PUBLIC'  CONNECT BY PRIOR TABLE_NAME = SYNONYM_NAME AND TABLE_OWNER = OWNER; OPEN :2 FOR SELECT ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME,  ATTR_TYPE_OWNER FROM ALL_TYPE_ATTRS  WHERE TYPE_NAME = THE_TYPE and OWNER = THE_OWNER; END;" };
    static final int TDS_SIZE = 4;
    static final int TDS_NUMBER = 1;
    static final int KOPM_OTS_SQL_CHAR = 1;
    static final int KOPM_OTS_DATE = 2;
    static final int KOPM_OTS_DECIMAL = 3;
    static final int KOPM_OTS_DOUBLE = 4;
    static final int KOPM_OTS_FLOAT = 5;
    static final int KOPM_OTS_NUMBER = 6;
    static final int KOPM_OTS_SQL_VARCHAR2 = 7;
    static final int KOPM_OTS_SINT32 = 8;
    static final int KOPM_OTS_REF = 9;
    static final int KOPM_OTS_VARRAY = 10;
    static final int KOPM_OTS_UINT8 = 11;
    static final int KOPM_OTS_SINT8 = 12;
    static final int KOPM_OTS_UINT16 = 13;
    static final int KOPM_OTS_UINT32 = 14;
    static final int KOPM_OTS_LOB = 15;
    static final int KOPM_OTS_CANONICAL = 17;
    static final int KOPM_OTS_OCTET = 18;
    static final int KOPM_OTS_RAW = 19;
    static final int KOPM_OTS_ROWID = 20;
    static final int KOPM_OTS_STAMP = 21;
    static final int KOPM_OTS_TZSTAMP = 23;
    static final int KOPM_OTS_INTERVAL = 24;
    static final int KOPM_OTS_PTR = 25;
    static final int KOPM_OTS_SINT16 = 26;
    static final int KOPM_OTS_UPT = 27;
    static final int KOPM_OTS_COLLECTION = 28;
    static final int KOPM_OTS_CLOB = 29;
    static final int KOPM_OTS_BLOB = 30;
    static final int KOPM_OTS_BFILE = 31;
    static final int KOPM_OTS_BINARY_INTEGE = 32;
    static final int KOPM_OTS_IMPTZSTAMP = 33;
    static final int KOPM_OTS_BFLOAT = 37;
    static final int KOPM_OTS_BDOUBLE = 45;
    static final int KOTTCOPQ = 58;
    static final int KOPT_OP_STARTEMBADT = 39;
    static final int KOPT_OP_ENDEMBADT = 40;
    static final int KOPT_OP_STARTADT = 41;
    static final int KOPT_OP_ENDADT = 42;
    static final int KOPT_OP_SUBTYPE_MARKER = 43;
    static final int KOPT_OP_EMBADT_INFO = 44;
    static final int KOPT_OPCODE_START = 38;
    static final int KOPT_OP_VERSION = 38;
    static final int REGULAR_PATCH = 0;
    static final int SIMPLE_PATCH = 1;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    protected OracleTypeADT() {
    }

    public OracleTypeADT(byte[] toid, int vsn, int csi, short csfrm, String fullName)
            throws SQLException {
        this(fullName, (OracleConnection) null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT (toid = " + toid + ", vsn = " + vsn
                    + ", csi = " + csi + ", csfrm = " + csfrm + ", fullName =" + fullName + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        this.toid = toid;
        this.typeVersion = vsn;
        this.charSetId = csi;
        this.charSetForm = csfrm;
    }

    public OracleTypeADT(String sql_name, Connection conn) throws SQLException {
        super(sql_name, (OracleConnection) conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT (sql_name = " + sql_name
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeADT(String sql_name, Connection conn, byte[] fdo) throws SQLException {
        this(sql_name, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT (sql_name = " + sql_name
                    + ", connection = " + conn + ", fdo = " + fdo + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.fdo = fdo;

        initEndianess(fdo);
    }

    public OracleTypeADT(OracleTypeADT parent, int idx, Connection conn) throws SQLException {
        super(parent, idx, (OracleConnection) conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT (parent = " + parent + ", idx = "
                    + idx + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public OracleTypeADT(OracleTypeADT parent, int idx, Connection conn, byte[] fdo)
            throws SQLException {
        this(parent, idx, conn);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT (parent = " + parent + ", idx = "
                    + idx + ", connection = " + conn + ", fdo = " + fdo + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.fdo = fdo;

        initEndianess(fdo);
    }

    public OracleTypeADT(SQLName sqlName, byte[] typoid, int version, byte[] tds, byte[] lds,
            OracleConnection conn, byte[] fdo) throws SQLException {
        this.fdo = fdo;
        init(tds, conn);
        this.toid = typoid;
        this.typeVersion = version;
    }

    public Datum toDatum(Object value, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.toDatum(value = " + value
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value != null) {
            if ((value instanceof STRUCT)) {
                return (STRUCT) value;
            }
            if (((value instanceof SQLData)) || ((value instanceof ObjectData))) {
                return STRUCT.toSTRUCT(value, conn);
            }
            if ((value instanceof Object[])) {
                StructDescriptor desc = createStructDescriptor();
                Datum result = createObjSTRUCT(desc, (Object[]) value);

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.toDatum:return + result",
                                            this);

                    OracleLog.recursiveTrace = false;
                }

                return result;
            }

            DatabaseError.throwSqlException(59, value);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.toDatum:return null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public Datum[] toDatumArray(Object obj, OracleConnection conn, long beginIdx, int count)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.toDatumArray(object = " + obj
                    + ", connection = " + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Datum[] datumArray = null;

        if (obj != null) {
            if ((obj instanceof Object[])) {
                Object[] objArray = (Object[]) obj;

                int length = (int) (count == -1 ? objArray.length : Math.min(objArray.length
                        - beginIdx + 1L, count));

                datumArray = new Datum[length];

                for (int i = 0; i < length; i++)
                    datumArray[i] = toDatum(objArray[((int) beginIdx + i - 1)], conn);
            } else {
                DatabaseError.throwSqlException(59, obj);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.toDatumArray:return " + datumArray,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return datumArray;
    }

    public int getTypeCode() throws SQLException {
        if ((getStatus() & 0x10) != 0) {
            return 2008;
        }
        return 2002;
    }

    public OracleType[] getAttrTypes() throws SQLException {
        if (this.attrTypes == null) {
            init(this.connection);
        }
        return this.attrTypes;
    }

    public boolean isInHierarchyOf(OracleType anOracleType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.isInHierarchyOf(" + anOracleType
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (anOracleType == null) {
            return false;
        }
        if (!anOracleType.isObjectType()) {
            return false;
        }
        StructDescriptor theOtherDesc = (StructDescriptor) anOracleType.getTypeDescriptor();

        return this.descriptor.isInHierarchyOf(theOtherDesc.getName());
    }

    public boolean isInHierarchyOf(StructDescriptor aStructDescriptor) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.isInHierarchyOf("
                    + aStructDescriptor + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (aStructDescriptor == null) {
            return false;
        }
        return this.descriptor.isInHierarchyOf(aStructDescriptor.getName());
    }

    public boolean isObjectType() {
        return true;
    }

    public TypeDescriptor getTypeDescriptor() {
        return this.descriptor;
    }

    public synchronized void init(OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.init(" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte[] tds = initMetadata(conn);
        init(tds, conn);
    }

    public synchronized void init(byte[] tds, OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger
                    .log(Level.FINER, "OracleTypeADT.init(tds, conn=" + conn + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.statusBits = 1;
        this.connection = conn;

        if (tds != null)
            parseTDS(tds, 0L);
        this.bigEndian = this.connection.getBigEndian();
        setStatusBits(256);
    }

    public byte[] initMetadata(OracleConnection conn) throws SQLException {
        byte[] myTDS = null;
        if ((this.statusBits & 0x100) != 0)
            return null;

        if (this.sqlName == null)
            getFullName();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.initMetadata(connection = " + conn
                    + ", sqlName = " + this.sqlName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (conn) {
            synchronized (this) {
                if ((this.statusBits & 0x100) == 0) {
                    CallableStatement cstmt = null;
                    try {
                        if (this.tdoCState == 0L)
                            this.tdoCState = this.connection.getTdoCState(this.sqlName.getSchema(),
                                                                          this.sqlName
                                                                                  .getSimpleName());

                        String sql_query = null;
                        boolean handleFDO = (this.fdo = this.connection.getFDO(false)) == null;

                        if (!handleFDO) {
                            sql_query = "begin :1 := dbms_pickler.get_type_shape(:2,:3,:4,:5,:6,:7); end;";
                        } else {
                            sql_query = "begin :1 := dbms_pickler.get_type_shape(:2,:3,:4,:5,:6,:7);       :8 := dbms_pickler.get_format(:9); end;";
                        }

                        boolean useBlob = false;
                        cstmt = this.connection.prepareCall(sql_query);

                        cstmt.registerOutParameter(1, 2);
                        cstmt.registerOutParameter(4, -4);
                        cstmt.registerOutParameter(5, 4);
                        cstmt.registerOutParameter(6, -4);
                        cstmt.registerOutParameter(7, -4);
                        if (handleFDO) {
                            cstmt.registerOutParameter(8, 2);
                            cstmt.registerOutParameter(9, -4);
                        }
                        cstmt.setString(2, this.sqlName.getSchema());
                        cstmt.setString(3, this.sqlName.getSimpleName());

                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.adtLogger.log(Level.FINEST,
                                                    "OracleTypeADT.initMetadata: qualified_name="
                                                            + this.sqlName);

                            OracleLog.recursiveTrace = false;
                        }

                        cstmt.execute();

                        int returnCode = cstmt.getInt(1);
                        if (returnCode != 0) {
                            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                OracleLog.recursiveTrace = true;
                                OracleLog.adtLogger.log(Level.FINEST,
                                                        "OracleTypeADT.initMetadata: describing type: returnCode="
                                                                + returnCode);

                                OracleLog.recursiveTrace = false;
                            }

                            if (returnCode != 24331) {
                                DatabaseError.throwSqlException(74, this.sqlName.toString());
                            }

                            if (returnCode == 24331) {
                                useBlob = true;
                                cstmt.registerOutParameter(6, 2004);

                                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                    OracleLog.recursiveTrace = true;
                                    OracleLog.adtLogger.log(Level.FINEST,
                                                            "OracleTypeADT.initMetadata: sqlName="
                                                                    + this.sqlName);

                                    OracleLog.recursiveTrace = false;
                                }

                                cstmt.execute();

                                returnCode = cstmt.getInt(1);
                                if (returnCode != 0) {
                                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                        OracleLog.recursiveTrace = true;
                                        OracleLog.adtLogger.log(Level.SEVERE,
                                                                "OracleTypeADT.initMetadata: describing type: returnCode="
                                                                        + returnCode);

                                        OracleLog.recursiveTrace = false;
                                    }

                                    DatabaseError.throwSqlException(74, this.sqlName.toString());
                                }
                            }

                        }

                        if (handleFDO) {
                            if (cstmt.getInt(8) != 0) {
                                DatabaseError.throwSqlException(1, "dbms_pickler.get_format()");
                            }

                        }

                        this.toid = cstmt.getBytes(4);
                        this.typeVersion = NUMBER.toInt(cstmt.getBytes(5));
                        if (!useBlob) {
                            myTDS = cstmt.getBytes(6);
                        } else {
                            try {
                                Blob l_blob = ((OracleCallableStatement) cstmt).getBlob(6);
                                InputStream l_OutputStream = l_blob.getBinaryStream();
                                myTDS = new byte[(int) l_blob.length()];
                                l_OutputStream.read(myTDS);
                                l_OutputStream.close();
                                ((BLOB) l_blob).freeTemporary();
                            } catch (IOException ea) {
                                DatabaseError.throwSqlException(ea);
                            }

                        }

                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.adtLogger
                                    .log(Level.FINEST, "OracleTypeADT.initMetadata: tds = "
                                            + OracleLog.bytesToPrintableForm("", myTDS), this);

                            OracleLog.recursiveTrace = false;
                        }

                        if (handleFDO) {
                            this.fdo = cstmt.getBytes(9);
                            this.connection.setFDO(this.fdo);
                        }

                        this.flattenedAttrNum = (Util.getUnsignedByte(myTDS[8]) * 256 + Util
                                .getUnsignedByte(myTDS[9]));

                        cstmt.getBytes(7);
                    } finally {
                        if (cstmt != null)
                            cstmt.close();
                    }
                }
                setStatusBits(256);
            }
        }
        return myTDS;
    }

    private void initEndianess(byte[] fdo) {
        int[] ub1fdo = Util.toJavaUnsignedBytes(fdo);
        int kopfdo_auxinfo = ub1fdo[(6 + ub1fdo[5] + ub1fdo[6] + 5)];

        int offset = (byte) (kopfdo_auxinfo & 0x10);

        if (offset < 0) {
            offset += 256;
        }
        this.bigEndian = (offset > 0);
    }

    void parseLDS(InputStream stream) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.parseLDS(" + stream + ")", this);

            OracleLog.recursiveTrace = false;
        }

        long lds_num = Util.readLong(stream);

        this.fixedDataSize = Util.readLong(stream);
        this.ldsOffsetArray = new long[this.flattenedAttrNum];

        for (int i = 0; i < this.flattenedAttrNum; i++) {
            this.ldsOffsetArray[i] = Util.readLong(stream);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.parseLDS(): lds_offset_array["
                        + i + "]=" + this.ldsOffsetArray[i], this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    public void generateLDS() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.generateLDS() " + this.sqlName,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        Vector V = generateLDSrec();

        this.ldsOffsetArray = new long[V.size()];

        for (int ctr = 0; ctr < V.size(); ctr++) {
            Integer I = (Integer) V.elementAt(ctr);

            this.ldsOffsetArray[ctr] = I.longValue();
        }
    }

    private Vector generateLDSrec() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.generateLDSrec() " + this.sqlName,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        int adtsize = 0;
        int adtalign = 0;

        Vector offsets = new Vector();
        int size = getNumAttrs();

        for (int ctr = 0; ctr < size; ctr++) {
            Vector emboffsets = null;
            OracleType this_attr = getAttrTypeAt(ctr);
            int attrsize;
            int attralign = 0;
            if (((this_attr instanceof OracleTypeADT))
                    && (!(this_attr instanceof OracleTypeCOLLECTION))
                    && (!(this_attr instanceof OracleTypeUPT))) {
                emboffsets = ((OracleTypeADT) this_attr).generateLDSrec();
                attralign = ((OracleTypeADT) this_attr).getAlignmentReq();
                attrsize = (int) ((OracleTypeADT) this_attr).getFixedDataSize();
            } else {
                attrsize = this_attr.getSizeLDS(this.fdo);
                attralign = this_attr.getAlignLDS(this.fdo);
            }

            if ((adtsize & attralign) > 0) {
                adtsize = Util.ldsRound(adtsize, attralign);
            }
            if (((this_attr instanceof OracleTypeADT))
                    && (!(this_attr instanceof OracleTypeCOLLECTION))
                    && (!(this_attr instanceof OracleTypeUPT))) {
                for (int vctr = 0; vctr < emboffsets.size(); vctr++) {
                    Integer elt = (Integer) emboffsets.elementAt(vctr);
                    Integer newval = new Integer(elt.intValue() + adtsize);

                    offsets.addElement(newval);
                }
            } else {
                offsets.addElement(new Integer(adtsize));
            }
            adtsize += attrsize;

            if (attralign > adtalign) {
                adtalign = attralign;
            }

        }

        if ((adtsize & adtalign) > 0) {
            adtsize = Util.ldsRound(adtsize, adtalign);
        }
        this.alignmentRequirement = adtalign;
        this.fixedDataSize = adtsize;

        return offsets;
    }

    TDSReader parseTDS(byte[] tds_bytes, long index) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.parseTDS("
                    + OracleLog.bytesToPrintableForm("", tds_bytes) + "," + index + ") "
                    + this.sqlName + " flattenedAttrNum=" + this.flattenedAttrNum, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.attrTypes != null) {
            return null;
        }

        TDSReader tdsReader = new TDSReader(tds_bytes, index);

        long _endOffset = tdsReader.readLong() + tdsReader.offset();

        tdsReader.checkNextByte((byte) 38);

        this.tdsVersion = tdsReader.readByte();

        tdsReader.skipBytes(2);

        this.flattenedAttrNum = tdsReader.readShort();

        if ((tdsReader.readByte() & 0xFF) == 255) {
            setStatusBits(128);
        }

        long _tdsStart = tdsReader.offset();

        tdsReader.checkNextByte((byte) 41);

        if (tdsReader.readShort() != 0) {
            DatabaseError.throwSqlException(47, "parseTDS");
        }

        long _offsetIndexTable = tdsReader.readLong();

        parseTDSrec(tdsReader);

        if (this.tdsVersion >= 3) {
            tdsReader.skip_to(_tdsStart + _offsetIndexTable + 2L);

            tdsReader.skipBytes(2 * this.flattenedAttrNum);

            byte _flag = tdsReader.readByte();

            if (tdsReader.isJavaObject(this.tdsVersion, _flag)) {
                setStatusBits(16);
            }

            if (tdsReader.isFinalType(this.tdsVersion, _flag)) {
                setStatusBits(32);
            }

            if (tdsReader.readByte() != 1)
                setStatusBits(64);
        } else {
            setStatusBits(32);
        }

        tdsReader.skip_to(_endOffset);
        return tdsReader;
    }

    public void parseTDSrec(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.parseTDSrec(" + tdsReader + ") "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        Vector type_tree = new Vector(5);
        OracleType type_object = null;

        this.nullOffset = tdsReader.nullOffset;
        this.ldsOffset = tdsReader.ldsOffset;

        tdsReader.nullOffset += 1;

        this.idx = 1;

        while ((type_object = getNextTypeObject(tdsReader)) != null) {
            type_tree.addElement(type_object);
        }

        if (this.opcode == 42) {
            this.endOfAdt = true;

            applyTDSpatches(tdsReader);
        }

        this.attrTypes = new OracleType[type_tree.size()];

        type_tree.copyInto(this.attrTypes);
    }

    private void applyTDSpatches(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.applyTDSpatches(" + tdsReader
                    + ") " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        TDSPatch _patch = tdsReader.getNextPatch();

        while (_patch != null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.applyTDSpatches(): "
                        + this.sqlName, this);

                OracleLog.recursiveTrace = false;
            }

            tdsReader.moveToPatchPos(_patch);

            int patchSystem = _patch.getType();

            if (patchSystem == 0) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.adtLogger.log(Level.FINEST,
                                            "OracleTypeADT.applyTDSpatches(): normal", this);

                    OracleLog.recursiveTrace = false;
                }

                tdsReader.readByte();

                byte UPTcode = _patch.getUptTypeCode();

                switch (UPTcode) {
                case -6:
                    tdsReader.readLong();
                case -5:
                {
                    OracleNamedType patchElem = _patch.getOwner();
                    OracleTypeADT newAdt = null;

                    if (patchElem.hasName()) {
                        newAdt = new OracleTypeADT(patchElem.getFullName(), this.connection,
                                this.fdo);
                    } else {
                        newAdt = new OracleTypeADT(patchElem.getParent(), patchElem.getOrder(),
                                this.connection, this.fdo);
                    }

                    newAdt.setUptADT();
                    TDSReader newTdsReader = newAdt.parseTDS(tdsReader.tds(), tdsReader
                            .absoluteOffset());

                    tdsReader.skipBytes((int) newTdsReader.offset());
                    _patch.apply(newAdt.cleanup());

                    break;
                }
                case 58:
                {
                    OracleNamedType patchElem = _patch.getOwner();
                    OracleTypeOPAQUE opaque = null;

                    if (patchElem.hasName()) {
                        opaque = new OracleTypeOPAQUE(patchElem.getFullName(), this.connection);
                    } else {
                        opaque = new OracleTypeOPAQUE(patchElem.getParent(), patchElem.getOrder(),
                                this.connection);
                    }

                    opaque.parseTDSrec(tdsReader);
                    _patch.apply(opaque);

                    break;
                }
                default:
                    DatabaseError.throwSqlException(1);
                }

            } else if (patchSystem == 1) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.adtLogger.log(Level.FINEST,
                                            "OracleTypeADT.applyTDSpatches(): simple", this);

                    OracleLog.recursiveTrace = false;
                }

                OracleType newType = getNextTypeObject(tdsReader);

                _patch.apply(newType, this.opcode);
            } else {
                DatabaseError.throwSqlException(47, "parseTDS");
            }

            _patch = tdsReader.getNextPatch();
        }
    }

    public synchronized OracleNamedType cleanup() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.cleanup() " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.attrTypes.length == 1) && ((this.attrTypes[0] instanceof OracleTypeCOLLECTION))) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.cleanup() " + this.sqlName
                        + " is collection", this);

                OracleLog.recursiveTrace = false;
            }

            OracleTypeCOLLECTION col = (OracleTypeCOLLECTION) this.attrTypes[0];

            col.copy_properties(this);

            return col;
        }
        if ((this.attrTypes.length == 1) && ((this.statusBits & 0x80) != 0)
                && ((this.attrTypes[0] instanceof OracleTypeUPT))
                && ((((OracleTypeUPT) this.attrTypes[0]).realType instanceof OracleTypeOPAQUE))) {
            OracleTypeOPAQUE opq = (OracleTypeOPAQUE) ((OracleTypeUPT) this.attrTypes[0]).realType;

            opq.copy_properties(this);

            return opq;
        }

        return this;
    }

    void copy_properties(OracleTypeADT source) {
        this.sqlName = source.sqlName;
        this.parent = source.parent;
        this.idx = source.idx;
        this.connection = source.connection;
        this.lds = source.lds;
        this.toid = source.toid;
        this.fdo = source.fdo;
        this.tdsVersion = source.tdsVersion;
        this.typeVersion = source.typeVersion;
        this.tdoCState = source.tdoCState;
        this.nullOffset = source.nullOffset;
        this.bigEndian = source.bigEndian;
        this.endOfAdt = source.endOfAdt;
    }

    OracleType getNextTypeObject(TDSReader tdsReader) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.getNextTypeObject(" + tdsReader
                    + ") " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        while (true) {
            this.opcode = tdsReader.readByte();

            if (this.opcode == 43) {
                continue;
            }

            if (this.opcode != 44) {
                break;
            }
            byte _flag = tdsReader.readByte();

            if (tdsReader.isJavaObject(3, _flag)) {
                setStatusBits(16);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.getNextTypeObject(): opcode="
                    + this.opcode, this);

            OracleLog.recursiveTrace = false;
        }

        switch (this.opcode) {
        case 40:
        case 42:
            return null;
        case 2:
        {
            OracleTypeDATE odate = new OracleTypeDATE();

            odate.parseTDSrec(tdsReader);

            this.idx += 1;

            return odate;
        }
        case 7:
        {
            OracleTypeCHAR ochar = new OracleTypeCHAR(this.connection, 12);

            ochar.parseTDSrec(tdsReader);

            this.idx += 1;

            return ochar;
        }
        case 1:
        {
            OracleTypeCHAR ochar = new OracleTypeCHAR(this.connection, 1);

            ochar.parseTDSrec(tdsReader);

            this.idx += 1;


            return ochar;
        }
        case 39:
        {
            OracleTypeADT oadt = new OracleTypeADT(this, this.idx, this.connection, this.fdo);

            oadt.setEmbeddedADT();
            oadt.parseTDSrec(tdsReader);

            this.idx += 1;

            return oadt;
        }
        case 6:
        {
            OracleTypeNUMBER onum = new OracleTypeNUMBER(2);

            onum.parseTDSrec(tdsReader);

            this.idx += 1;

            return onum;
        }
        case 3:
        {
            OracleTypeNUMBER onum = new OracleTypeNUMBER(3);

            onum.parseTDSrec(tdsReader);

            this.idx += 1;

            return onum;
        }
        case 4:
        {
            OracleTypeNUMBER onum = new OracleTypeNUMBER(8);

            onum.parseTDSrec(tdsReader);

            this.idx += 1;

            return onum;
        }
        case 5:
        {
            OracleTypeFLOAT onum = new OracleTypeFLOAT();

            onum.parseTDSrec(tdsReader);

            this.idx += 1;

            return onum;
        }
        case 37:
        {
            OracleTypeBINARY_FLOAT bfloat = new OracleTypeBINARY_FLOAT();

            bfloat.parseTDSrec(tdsReader);

            this.idx += 1;

            return bfloat;
        }
        case 45:
        {
            OracleTypeBINARY_DOUBLE bdouble = new OracleTypeBINARY_DOUBLE();

            bdouble.parseTDSrec(tdsReader);

            this.idx += 1;

            return bdouble;
        }
        case 8:
        {
            OracleTypeSINT32 onum = new OracleTypeSINT32();

            onum.parseTDSrec(tdsReader);

            this.idx += 1;

            return onum;
        }
        case 9:
        {
            OracleTypeREF oref = new OracleTypeREF(this, this.idx, this.connection);

            oref.parseTDSrec(tdsReader);

            this.idx += 1;

            return oref;
        }
        case 31:
        {
            OracleTypeBFILE obfile = new OracleTypeBFILE(this.connection);

            obfile.parseTDSrec(tdsReader);

            this.idx += 1;

            return obfile;
        }
        case 19:
        {
            OracleTypeRAW oraw = new OracleTypeRAW();

            oraw.parseTDSrec(tdsReader);

            this.idx += 1;

            return oraw;
        }
        case 29:
        {
            OracleTypeCLOB oclob = new OracleTypeCLOB(this.connection);

            oclob.parseTDSrec(tdsReader);

            if ((this.sqlName != null) && (!this.endOfAdt)) {
                this.connection.getForm(this, oclob, this.idx);
            }
            this.idx += 1;

            return oclob;
        }
        case 30:
        {
            OracleTypeBLOB oblob = new OracleTypeBLOB(this.connection);

            oblob.parseTDSrec(tdsReader);

            this.idx += 1;

            return oblob;
        }
        case 21:
        {
            OracleTypeTIMESTAMP timestamp = new OracleTypeTIMESTAMP(this.connection);

            timestamp.parseTDSrec(tdsReader);

            this.idx += 1;

            return timestamp;
        }
        case 23:
        {
            OracleTypeTIMESTAMPTZ timestamp = new OracleTypeTIMESTAMPTZ(this.connection);

            timestamp.parseTDSrec(tdsReader);

            this.idx += 1;

            return timestamp;
        }
        case 33:
        {
            OracleTypeTIMESTAMPLTZ timestamp = new OracleTypeTIMESTAMPLTZ(this.connection);

            timestamp.parseTDSrec(tdsReader);

            this.idx += 1;

            return timestamp;
        }
        case 24:
        {
            OracleTypeINTERVAL interval = new OracleTypeINTERVAL(this.connection);

            interval.parseTDSrec(tdsReader);

            this.idx += 1;

            return interval;
        }
        case 28:
        {
            OracleTypeCOLLECTION ocollection = new OracleTypeCOLLECTION(this, this.idx,
                    this.connection);

            ocollection.bigEndian = this.bigEndian;

            ocollection.parseTDSrec(tdsReader);

            this.idx += 1;

            return ocollection;
        }
        case 27:
        {
            OracleTypeUPT oupt = new OracleTypeUPT(this, this.idx, this.connection);

            oupt.bigEndian = this.bigEndian;

            oupt.parseTDSrec(tdsReader);

            this.idx += 1;

            return oupt;
        }
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
        case 17:
        case 18:
        case 20:
        case 22:
        case 25:
        case 26:
        case 32:
        case 34:
        case 35:
        case 36:
        case 38:
        case 41:
        case 43:
        case 44:
        }
        OracleType type_object = null;

        DatabaseError.throwSqlException(48, "get_next_type: " + this.opcode);

        return null;
    }

    public synchronized byte[] linearize(Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.lindarize(" + data + ") "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        return pickle81(data);
    }

    public Datum unlinearize(byte[] pickled_bytes, long offset, Datum container, int style, Map map)
            throws SQLException {
        OracleConnection mc = getConnection();
        Datum ret = null;

        if (mc == null) {
            ret = _unlinearize(pickled_bytes, offset, container, style, map);
        } else {
            synchronized (mc) {
                ret = _unlinearize(pickled_bytes, offset, container, style, map);
            }
        }

        return ret;
    }

    public synchronized Datum _unlinearize(byte[] pickled_bytes, long offset, Datum container,
            int style, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unlinearize() " + this.sqlName,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger
                    .log(Level.FINEST, "OracleTypeADT.unlinearize("
                            + OracleLog.bytesToPrintableForm("", pickled_bytes) + ") "
                            + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        if (pickled_bytes == null) {
            return null;
        }
        if (PickleContext.is81format(pickled_bytes[0])) {
            PickleContext context = new PickleContext(pickled_bytes, offset);

            return unpickle81(context, (STRUCT) container, 1, style, map);
        }

        UnpickleContext newContext = new UnpickleContext(pickled_bytes, (int) offset, null, null,
                this.bigEndian);

        return unpickle80(newContext, (STRUCT) container, 1, style, map);
    }

    protected STRUCT unpickle80(UnpickleContext context, STRUCT container, int style,
            int attrStyle, Map attrMap) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle80(context = " + context
                    + ", container =" + container + ", sqlName = " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT struct_obj = container;

        if (style == 3) {
            if (struct_obj == null) {
                StructDescriptor desc = createStructDescriptor();

                struct_obj = createByteSTRUCT(desc, (byte[]) null);
            }

            struct_obj.setImage(context.image(), context.absoluteOffset(), 0L);
        }

        long length = context.readLong();

        if (length == 0L) {
            return null;
        }
        if (style == 9) {
            context.skipBytes((int) length);

            return struct_obj;
        }
        if (style == 3) {
            struct_obj.setImageLength(length + 4L);
            context.skipBytes((int) length);

            return struct_obj;
        }

        context.skipBytes(1);

        byte flag = context.readByte();
        boolean[] null_array = unpickle_nulls(context);
        long data_length = context.readLong();
        long endOffset = context.offset() + data_length;
        try {
            if (null_array[0] == false) {
                UnpickleContext newContext = new UnpickleContext(context.image(), context
                        .absoluteOffset(), null_array, getLdsOffsetArray(), this.bigEndian);

                STRUCT localSTRUCT1 = unpickle80rec(newContext, struct_obj, attrStyle, attrMap);
                return localSTRUCT1;
            }
        } finally {
            context.skipTo(endOffset);
        }

        return struct_obj;
    }

    protected Object unpickle80rec(UnpickleContext context, int format, int style, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpicklerec(" + context + ") "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = unpickle80rec(context, null, 1, map);

        return toObject(s, style, map);
    }

    private STRUCT unpickle80rec(UnpickleContext context, STRUCT structDatum, int attrStyle,
            Map attrMap) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpicklerec(context = " + context
                    + ", struct = " + structDatum + ", sqlName = " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        if (context.isNull(this.nullOffset)) {
            return null;
        }
        context.skipTo(context.ldsOffsets[this.ldsOffset]);

        int size = getNumAttrs();
        STRUCT struct_obj = structDatum;

        if (struct_obj == null) {
            StructDescriptor desc = createStructDescriptor();

            struct_obj = createByteSTRUCT(desc, (byte[]) null);
        }

        switch (attrStyle) {
        case 1:
            Datum[] datums = new Datum[size];

            for (int i = 0; i < size; i++) {
                datums[i] = ((Datum) getAttrTypeAt(i).unpickle80rec(context, 1, attrStyle, attrMap));
            }

            struct_obj.setDatumArray(datums);

            break;
        case 2:
            Object[] oarray = new Object[size];

            for (int i = 0; i < size; i++) {
                oarray[i] = getAttrTypeAt(i).unpickle80rec(context, 1, attrStyle, attrMap);
            }

            struct_obj.setObjArray(oarray);

            break;
        default:
            DatabaseError.throwSqlException(1);
        }

        return struct_obj;
    }

    private boolean[] unpickle_nulls(UnpickleContext context) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle_nulls(" + context + ") "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        context.skipBytes(4);

        byte[] bytes = context.readLengthBytes();

        boolean[] null_bytes = new boolean[(bytes.length - 2) * 4];

        byte b = 0;

        for (int i = 0; i < null_bytes.length; i++) {
            if (i % 4 == 0) {
                b = bytes[(2 + i / 4)];
            }
            null_bytes[i] = ((b & 0x3) != 0 ? true : false);
            b = (byte) (b >> 2);
        }

        return null_bytes;
    }

    protected STRUCT unpickle81(PickleContext context, STRUCT container, int style, int attrStyle,
            Map attrMap) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle81(" + context + ") "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT the_adt = container;
        long _offset = context.offset();

        byte flags = context.readByte();

        if (!PickleContext.is81format(flags)) {
            DatabaseError.throwSqlException(1, "Image is not in 8.1 format");
        }

        if (PickleContext.isCollectionImage_pctx(flags)) {
            DatabaseError.throwSqlException(1, "Image is a collection image, expecting ADT");
        }

        if (!context.readAndCheckVersion()) {
            DatabaseError.throwSqlException(1, "Image version is not recognized");
        }

        switch (style) {
        case 9:
            context.skipBytes(context.readLength(true) - 2);

            break;
        case 3:
            long length = context.readLength();

            the_adt = unpickle81Prefix(context, the_adt, flags);

            if (the_adt == null) {
                StructDescriptor desc = createStructDescriptor();

                the_adt = createByteSTRUCT(desc, (byte[]) null);
            }

            the_adt.setImage(context.image(), _offset, 0L);
            the_adt.setImageLength(length);
            context.skipTo(_offset + length);

            break;
        default:
            context.skipLength();

            the_adt = unpickle81Prefix(context, the_adt, flags);

            if (the_adt == null) {
                StructDescriptor desc = createStructDescriptor();

                the_adt = createByteSTRUCT(desc, (byte[]) null);
            }

            OracleType[] _attrs = the_adt.getDescriptor().getOracleTypeADT().getAttrTypes();

            switch (attrStyle) {
            case 1:
                Datum[] datums = new Datum[_attrs.length];

                for (int i = 0; i < _attrs.length; i++) {
                    datums[i] = ((Datum) _attrs[i].unpickle81rec(context, attrStyle, attrMap));
                }

                the_adt.setDatumArray(datums);

                break;
            case 2:
                Object[] oarray = new Object[_attrs.length];

                for (int i = 0; i < _attrs.length; i++) {
                    oarray[i] = _attrs[i].unpickle81rec(context, attrStyle, attrMap);
                }

                the_adt.setObjArray(oarray);

                break;
            default:
                DatabaseError.throwSqlException(1);
            }

        }

        return the_adt;
    }

    protected STRUCT unpickle81Prefix(PickleContext context, STRUCT container, byte flags)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle81_prefix( context = "
                    + context + ", container =  " + container + ", flags = " + flags + ")", this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT the_adt = container;

        if (PickleContext.hasPrefix(flags)) {
            long _endOffset = context.readLength() + context.absoluteOffset();

            byte _prefixFlag = context.readByte();

            byte _TypeInfoEncodedBits = (byte) (_prefixFlag & 0xC);
            boolean hasTypeInfoNONE = _TypeInfoEncodedBits == 0;

            boolean hasTypeInfoTOID = _TypeInfoEncodedBits == 4;

            boolean hasTypeInfoTOBJN = _TypeInfoEncodedBits == 8;

            boolean hasTypeInfoTDS = _TypeInfoEncodedBits == 12;

            boolean hasTypeVersion = (_prefixFlag & 0x10) != 0;

            if (hasTypeInfoTOID) {
                byte[] _toid = context.readBytes(16);
                String _subTypename = toid2typename(this.connection, _toid);

                StructDescriptor desc = (StructDescriptor) TypeDescriptor
                        .getTypeDescriptor(_subTypename, this.connection);

                if (the_adt == null)
                    the_adt = createByteSTRUCT(desc, (byte[]) null);
                else {
                    the_adt.setDescriptor(desc);
                }
            }
            if (hasTypeVersion) {
                this.typeVersion = context.readLength();
            } else {
                this.typeVersion = 1;
            }
            if ((hasTypeInfoTOBJN | hasTypeInfoTDS)) {
                DatabaseError.throwSqlException(23);
            }
            context.skipTo(_endOffset);
        }

        return the_adt;
    }

    protected Object unpickle81rec(PickleContext context, int type, Map map) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle81rec( context = "
                    + context + ", type =  " + type + ", map = " + map + ")", this);

            OracleLog.recursiveTrace = false;
        }

        byte firstbyte = context.readByte();
        byte immemb = 0;

        if (PickleContext.isAtomicNull(firstbyte))
            return null;
        if (PickleContext.isImmediatelyEmbeddedNull(firstbyte)) {
            immemb = context.readByte();
        }
        STRUCT s = unpickle81datum(context, firstbyte, immemb);

        return toObject(s, type, map);
    }

    protected Object unpickle81rec(PickleContext context, byte len_flags, int type, Map map)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle81rec(context = " + context
                    + ", len_flags = " + len_flags + ", sqlName = " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        STRUCT s = unpickle81datum(context, len_flags, (byte) 0);

        return toObject(s, type, map);
    }

    private STRUCT unpickle81datum(PickleContext context, byte len_flags, byte immemb)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.unpickle81rec(context = " + context
                    + ", len_flags = " + len_flags + ", immemb = " + immemb + ", sqlName = "
                    + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        int size = getNumAttrs();

        StructDescriptor desc = createStructDescriptor();
        STRUCT the_adt = createByteSTRUCT(desc, (byte[]) null);
        OracleType this_attr = getAttrTypeAt(0);
        Object firstElem = null;

        if ((PickleContext.isImmediatelyEmbeddedNull(len_flags)) && (immemb == 1))
            firstElem = null;
        else if (PickleContext.isImmediatelyEmbeddedNull(len_flags)) {
            firstElem = ((OracleTypeADT) this_attr).unpickle81datum(context, len_flags,
                                                                    (byte) (immemb - 1));
        } else if (PickleContext.isElementNull(len_flags)) {
            if ((this_attr.getTypeCode() == 2002) || (this_attr.getTypeCode() == 2008)) {
                firstElem = this_attr.unpickle81datumAsNull(context, len_flags, immemb);
            } else
                firstElem = null;
        } else {
            firstElem = this_attr.unpickle81rec(context, len_flags, 1, null);
        }

        Datum[] oarray = new Datum[size];

        oarray[0] = ((Datum) firstElem);

        for (int ctr = 1; ctr < size; ctr++) {
            this_attr = getAttrTypeAt(ctr);
            oarray[ctr] = ((Datum) this_attr.unpickle81rec(context, 1, null));
        }

        the_adt.setDatumArray(oarray);

        return the_adt;
    }

    protected Datum unpickle81datumAsNull(PickleContext context, byte len_flags, byte immemb)
            throws SQLException {
        int size = getNumAttrs();

        StructDescriptor desc = createStructDescriptor();
        STRUCT the_adt = createByteSTRUCT(desc, (byte[]) null);
        Datum[] oarray = new Datum[size];

        for (int ctr = 0; ctr < size; ctr++) {
            OracleType this_attr = getAttrTypeAt(ctr);
            if ((this_attr.getTypeCode() == 2002) || (this_attr.getTypeCode() == 2008)) {
                oarray[ctr] = this_attr.unpickle81datumAsNull(context, len_flags, immemb);
            } else
                oarray[ctr] = null;
        }
        the_adt.setDatumArray(oarray);
        return the_adt;
    }

    public byte[] pickle81(Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.pickle81(" + data + ") "
                    + this.sqlName + "statusBits" + this.statusBits, this);

            OracleLog.recursiveTrace = false;
        }

        PickleContext ctx = new PickleContext();

        ctx.initStream();
        pickle81(ctx, data);

        byte[] pickledBytes = ctx.stream2Bytes();

        data.setShareBytes(pickledBytes);

        return pickledBytes;
    }

    protected int pickle81(PickleContext ctx, Datum data) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.pickle81(context = " + ctx
                    + ", datum = " + data + ", sqlName =  " + this.sqlName, this);

            OracleLog.recursiveTrace = false;
        }

        int lenOffset = ctx.offset() + 2;
        int imglen = 0;

        imglen += ctx.writeImageHeader(shouldHavePrefix());

        imglen += pickle81Prefix(ctx);
        imglen += pickle81rec(ctx, data, 0);

        ctx.patchImageLen(lenOffset, imglen);

        return imglen;
    }

    private boolean hasTypeVersion() {
        return this.typeVersion > 1;
    }

    private boolean needsToid() {
        return ((this.statusBits & 0x40) != 0) || ((this.statusBits & 0x20) == 0)
                || (hasTypeVersion());
    }

    private boolean shouldHavePrefix() {
        return (hasTypeVersion()) || (needsToid());
    }

    protected int pickle81Prefix(PickleContext context) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.pickle81_prefix(context = "
                    + context + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (shouldHavePrefix()) {
            int len = 0;
            int _prefix_flag = 1;
            int precalculated_length = 1;

            if (needsToid()) {
                precalculated_length += getTOID().length;
                _prefix_flag |= 4;
            }

            if (hasTypeVersion()) {
                _prefix_flag |= 16;

                if (this.typeVersion > PickleContext.KOPI20_LN_MAXV)
                    precalculated_length += 5;
                else {
                    precalculated_length++;
                }
            }
            len = context.writeLength(precalculated_length);

            len += context.writeData((byte) _prefix_flag);

            if (needsToid()) {
                len += context.writeData(this.toid);
            }
            if (hasTypeVersion()) {
                len += context.writeLength(this.typeVersion);
            }
            return len;
        }

        return 0;
    }

    private int pickle81rec(PickleContext ctx, Datum data, int depth) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.pickle81rec(context = " + ctx
                    + ", datum = " + data + ", depth = " + depth + ", sqlName = " + this.sqlName,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        int imglen = 0;

        Datum[] values = ((STRUCT) data).getOracleAttributes();
        int field_num = values.length;
        int ctr = 0;
        OracleType this_attr = getAttrTypeAt(0);

        if (((this_attr instanceof OracleTypeADT))
                && (!(this_attr instanceof OracleTypeCOLLECTION))
                && (!(this_attr instanceof OracleTypeUPT))) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.pickle81rec(): "
                        + this.sqlName + " check first null case and this attr=" + values[0]
                        + " depth=" + depth, this);

                OracleLog.recursiveTrace = false;
            }

            ctr = 1;

            if (values[0] == null) {
                if (depth > 0)
                    imglen += ctx.writeImmediatelyEmbeddedElementNull((byte) depth);
                else {
                    imglen += ctx.writeAtomicNull();
                }
            } else {
                imglen += ((OracleTypeADT) this_attr).pickle81rec(ctx, values[0], depth + 1);
            }

        }

        for (; ctr < field_num; ctr++) {
            this_attr = getAttrTypeAt(ctr);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.pickle81rec(): "
                        + this.sqlName + " check idx=" + ctr + "  attr=" + values[ctr] + " depth="
                        + depth, this);

                OracleLog.recursiveTrace = false;
            }

            if (values[ctr] == null) {
                if (((this_attr instanceof OracleTypeADT))
                        && (!(this_attr instanceof OracleTypeCOLLECTION))
                        && (!(this_attr instanceof OracleTypeUPT))) {
                    imglen += ctx.writeAtomicNull();
                } else {
                    imglen += ctx.writeElementNull();
                }

            } else if (((this_attr instanceof OracleTypeADT))
                    && (!(this_attr instanceof OracleTypeCOLLECTION))
                    && (!(this_attr instanceof OracleTypeUPT))) {
                imglen += ((OracleTypeADT) this_attr).pickle81rec(ctx, values[ctr], 1);
            } else {
                imglen += this_attr.pickle81(ctx, values[ctr]);
            }

        }

        return imglen;
    }

    private Object toObject(STRUCT s, int type, Map map) throws SQLException {
        switch (type) {
        case 1:
            return s;
        case 2:
            if (s == null)
                break;
            return s.toJdbc(map);
        default:
            DatabaseError.throwSqlException(1);
        }

        return null;
    }

    public String getAttributeType(int idx) throws SQLException {
        if (this.sqlName == null) {
            getFullName();
        }
        if (this.attrNames == null) {
            initADTAttrNames();
        }
        if ((idx < 1) || (idx > this.attrTypeNames.length)) {
            DatabaseError.throwSqlException(1, "Invalid index");
        }
        return this.attrTypeNames[(idx - 1)];
    }

    public String getAttributeType(int idx, boolean force) throws SQLException {
        if (force) {
            return getAttributeType(idx);
        }

        if ((idx < 1) || ((this.attrTypeNames != null) && (idx > this.attrTypeNames.length))) {
            DatabaseError.throwSqlException(1, "Invalid index");
        }

        if (this.attrTypeNames != null) {
            return this.attrTypeNames[(idx - 1)];
        }
        return null;
    }

    public String getAttributeName(int idx) throws SQLException {
        if (this.attrNames == null) {
            initADTAttrNames();
        }
        synchronized (this) {
            if ((idx < 1) || (idx > this.attrNames.length)) {
                DatabaseError.throwSqlException(1, "Invalid index");
            }
        }

        return this.attrNames[(idx - 1)];
    }

    public String getAttributeName(int idx, boolean force) throws SQLException {
        if (force) {
            return getAttributeName(idx);
        }

        if ((idx < 1) || ((this.attrNames != null) && (idx > this.attrNames.length))) {
            DatabaseError.throwSqlException(1, "Invalid index");
        }

        if (this.attrNames != null) {
            return this.attrNames[(idx - 1)];
        }
        return null;
    }

    private void initADTAttrNames() throws SQLException {
        CallableStatement cstmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String[] temp_attrNames = new String[this.attrTypes.length];
        String[] temp_attrTypeNames = new String[this.attrTypes.length];
        int state = 0;
        int l_index = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.initADTAttrNames()", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                if (this.attrNames == null) {
                    state = this.sqlName.getSchema()
                            .equalsIgnoreCase(this.connection.getUserName()) ? 0 : 3;
                    while (true) {
                        if (state != 6) {
                            switch (state) {
                            case 0:
                                ps = this.connection.prepareStatement(sqlString[state]);
                                ps.setString(1, this.sqlName.getSimpleName());
                                rs = ps.executeQuery();
                                state = 1;
                                break;
                            case 1:
                                ps = this.connection.prepareStatement(sqlString[state]);
                                ps.setString(1, this.sqlName.getSimpleName());
                                ps.setString(2, this.sqlName.getSimpleName());
                                state = 2;
                                try {
                                    rs = ps.executeQuery();
                                } catch (SQLException ea) {
                                    if (ea.getErrorCode() == 1436) {
                                        continue;
                                    }
                                    throw ea;
                                }
                                
                            case 2:
                                ps = this.connection.prepareStatement(sqlString[state]);
                                ps.setString(1, this.sqlName.getSimpleName());
                                ps.setString(2, this.sqlName.getSimpleName());
                                rs = ps.executeQuery();
                                state = 4;
                                break;
                            case 3:
                                ps = this.connection.prepareStatement(sqlString[state]);
                                ps.setString(1, this.sqlName.getSchema());
                                ps.setString(2, this.sqlName.getSimpleName());
                                rs = ps.executeQuery();
                                state = 4;
                                break;
                            case 4:
                                ps = this.connection.prepareStatement(sqlString[state]);
                                ps.setString(1, this.sqlName.getSimpleName());
                                ps.setString(2, this.sqlName.getSimpleName());
                                rs = ps.executeQuery();
                                state = 5;
                                break;
                            case 5:
                                cstmt = this.connection.prepareCall(sqlString[state]);
                                cstmt.setString(1, this.sqlName.getSimpleName());
                                cstmt.registerOutParameter(2, -10);
                                cstmt.execute();
                                rs = ((OracleCallableStatement) cstmt).getCursor(2);
                                state = 6;
                            default:
                                try {
                                    if (ps != null) {
                                        ps.setFetchSize(this.idx);
                                    }
                                    l_index = 0;
                                    while ((l_index < this.attrTypes.length) && (rs.next())) {
                                        if (rs.getInt(1) != l_index + 1) {
                                            DatabaseError
                                                    .throwSqlException(1,
                                                                       "inconsistent ADT attribute");
                                        }

                                        temp_attrNames[l_index] = rs.getString(2);

                                        temp_attrTypeNames[l_index] = (rs.getString(4) + "." + rs
                                                .getString(3));

                                        l_index++;
                                    }

                                    if (l_index != 0) {
                                        this.attrTypeNames = temp_attrTypeNames;

                                        this.attrNames = temp_attrNames;
                                        state = 6;
                                    } else {
                                        if (rs != null)
                                            rs.close();
                                        if (ps != null)
                                            ps.close();
                                    }
                                } finally {
                                    if (rs != null)
                                        rs.close();
                                    if (ps != null)
                                        ps.close();
                                    if (cstmt != null)
                                        cstmt.close();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    StructDescriptor createStructDescriptor() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.createStructDescriptor()", this);

            OracleLog.recursiveTrace = false;
        }

        return new StructDescriptor(this, this.connection);
    }

    STRUCT createObjSTRUCT(StructDescriptor desc, Object[] value) throws SQLException {
        if ((this.statusBits & 0x10) != 0) {
            return new JAVA_STRUCT(desc, this.connection, value);
        }
        return new STRUCT(desc, this.connection, value);
    }

    STRUCT createByteSTRUCT(StructDescriptor desc, byte[] value) throws SQLException {
        if ((this.statusBits & 0x10) != 0) {
            return new JAVA_STRUCT(desc, value, this.connection);
        }
        return new STRUCT(desc, value, this.connection);
    }

    public static String getSubtypeName(Connection conn, byte[] image, long offset)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "OracleTypeADT.getSubtypeName( connection = "
                    + conn + ", image = " + image + ", offset = " + offset + ")");

            OracleLog.recursiveTrace = false;
        }

        PickleContext context = new PickleContext(image, offset);
        byte flags = context.readByte();

        if ((!PickleContext.is81format(flags)) || (PickleContext.isCollectionImage_pctx(flags))
                || (!PickleContext.hasPrefix(flags))) {
            return null;
        }

        if (!context.readAndCheckVersion()) {
            DatabaseError.throwSqlException(1, "Image version is not recognized");
        }

        context.skipLength();

        context.skipLength();

        flags = context.readByte();

        if ((flags & 0x4) != 0) {
            byte[] _toid = context.readBytes(16);

            return toid2typename(conn, _toid);
        }

        return null;
    }

    public static String toid2typename(Connection conn, byte[] toid) throws SQLException {
        String typename = (String) ((OracleConnection) conn).getDescriptor(toid);

        if (typename == null) {
            PreparedStatement pstmt = null;
            ResultSet rset = null;
            try {
                pstmt = conn
                        .prepareStatement("select owner, type_name from all_types where type_oid = :1");

                pstmt.setBytes(1, toid);

                rset = pstmt.executeQuery();

                if (rset.next()) {
                    typename = rset.getString(1) + "." + rset.getString(2);

                    ((OracleConnection) conn).putDescriptor(toid, typename);
                } else {
                    DatabaseError.throwSqlException(1, "Invalid type oid");
                }
            } finally {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }

        return typename;
    }

    public int getTdsVersion() {
        return this.tdsVersion;
    }

    public void printDebug() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger
                    .log(Level.FINER, "OracleTypeADT.printDebug(): " + debugText(), this);

            OracleLog.recursiveTrace = false;
        }
    }

    private String debugText() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        pw.println("OracleTypeADT = " + this);
        pw.println("sqlName = " + this.sqlName);

        pw.println("OracleType[] : ");

        if (this.attrTypes != null) {
            for (int i = 0; i < this.attrTypes.length; i++)
                pw.println("[" + i + "] = " + this.attrTypes[i]);
        } else {
            pw.println("null");
        }
        pw.println("LDS : ");

        if (this.lds != null)
            printUnsignedByteArray(this.lds, pw);
        else {
            pw.println("null");
        }
        pw.println("toid : ");

        if (this.toid != null)
            printUnsignedByteArray(this.toid, pw);
        else {
            pw.println("null");
        }
        pw.println("fdo : ");

        if (this.fdo != null)
            printUnsignedByteArray(this.fdo, pw);
        else {
            pw.println("null");
        }
        pw.println("tds version : " + this.tdsVersion);
        pw.println("type version : " + this.typeVersion);
        pw.println("type version : " + this.typeVersion);
        pw.println("bigEndian : " + (this.bigEndian ? "true" : "false"));
        pw.println("opcode : " + this.opcode);

        pw.println("tdoCState : " + this.tdoCState);

        return sw.toString();
    }

    public byte[] getTOID() {
        try {
            if (this.toid == null) {
                initMetadata(this.connection);
            }
        } catch (SQLException e) {
        }
        return this.toid;
    }

    public int getImageFormatVersion() {
        return PickleContext.KOPI20_VERSION;
    }

    public int getTypeVersion() {
        try {
            if (this.typeVersion == -1) {
                initMetadata(this.connection);
            }
        } catch (SQLException ex) {
        }
        return this.typeVersion;
    }

    public int getCharSet() {
        return this.charSetId;
    }

    public int getCharSetForm() {
        return this.charSetForm;
    }

    public synchronized long getTdoCState() {
        try {
            if (this.tdoCState == 0L) {
                getFullName();
                this.tdoCState = this.connection.getTdoCState(this.sqlName.getSchema(),
                                                              this.sqlName.getSimpleName());
            }
        } catch (SQLException e) {
        }
        return this.tdoCState;
    }

    public long getFIXED_DATA_SIZE() {
        try {
            return getFixedDataSize();
        } catch (SQLException e) {
        }
        return 0L;
    }

    public long[] getLDS_OFFSET_ARRAY() {
        try {
            return getLdsOffsetArray();
        } catch (SQLException e) {
        }
        return null;
    }

    synchronized long[] getLdsOffsetArray() throws SQLException {
        if ((this.ldsOffsetArray == null) && (this.connection != null)) {
            generateLDS();
        }
        return this.ldsOffsetArray;
    }

    public long getFixedDataSize() throws SQLException {
        return this.fixedDataSize;
    }

    public int getAlignmentReq() throws SQLException {
        return this.alignmentRequirement;
    }

    public int getNumAttrs() throws SQLException {
        if ((this.attrTypes == null) && (this.connection != null)) {
            init(this.connection);
        }
        return this.attrTypes.length;
    }

    public OracleType getAttrTypeAt(int idx) throws SQLException {
        if ((this.attrTypes == null) && (this.connection != null)) {
            init(this.connection);
        }
        return this.attrTypes[idx];
    }

    public boolean isEmbeddedADT() throws SQLException {
        return (this.statusBits & 0x2) != 0;
    }

    public boolean isUptADT() throws SQLException {
        return (this.statusBits & 0x4) != 0;
    }

    public boolean isTopADT() throws SQLException {
        return (this.statusBits & 0x1) != 0;
    }

    public void setStatus(int status) throws SQLException {
        this.statusBits = status;
    }

    void setEmbeddedADT() throws SQLException {
        maskAndSetStatusBits(-16, 2);
    }

    void setUptADT() throws SQLException {
        maskAndSetStatusBits(-16, 4);
    }

    public boolean isSubType() throws SQLException {
        return (this.statusBits & 0x40) != 0;
    }

    public boolean isFinalType() throws SQLException {
        return ((this.statusBits & 0x20) != 0 ? true : false) | ((this.statusBits & 0x2) != 0 ? true : false);
    }

    public boolean isJavaObject() throws SQLException {
        return (this.statusBits & 0x10) != 0;
    }

    public int getStatus() throws SQLException {
        if (((this.statusBits & 0x1) != 0) && ((this.statusBits & 0x100) == 0))
            init(this.connection);
        return this.statusBits;
    }

    public static OracleTypeADT shallowClone(OracleTypeADT adt) throws SQLException {
        OracleTypeADT newADT = new OracleTypeADT();
        shallowCopy(adt, newADT);
        return newADT;
    }

    public static void shallowCopy(OracleTypeADT from, OracleTypeADT to) throws SQLException {
        to.connection = from.connection;
        to.sqlName = from.sqlName;
        to.parent = from.parent;
        to.idx = from.idx;
        to.descriptor = from.descriptor;
        to.statusBits = from.statusBits;

        to.nullOffset = from.nullOffset;
        to.ldsOffset = from.ldsOffset;
        to.sizeForLds = from.sizeForLds;
        to.alignForLds = from.alignForLds;
        to.typeCode = from.typeCode;
        to.dbTypeCode = from.dbTypeCode;
        to.tdsVersion = from.tdsVersion;
        to.typeVersion = from.typeVersion;
        to.lds = from.lds;
        to.ldsOffsetArray = from.ldsOffsetArray;
        to.fixedDataSize = from.fixedDataSize;
        to.alignmentRequirement = from.alignmentRequirement;
        to.attrTypes = from.attrTypes;
        to.sqlName = from.sqlName;
        to.tdoCState = from.tdoCState;
        to.toid = from.toid;
        to.fdo = from.fdo;
        to.charSetId = from.charSetId;
        to.charSetForm = from.charSetForm;
        to.bigEndian = from.bigEndian;
        to.flattenedAttrNum = from.flattenedAttrNum;
        to.statusBits = from.statusBits;
        to.attrNames = from.attrNames;
        to.attrTypeNames = from.attrTypeNames;
        to.opcode = from.opcode;
        to.idx = from.idx;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.writeObject()", this);

            OracleLog.recursiveTrace = false;
        }

        out.writeInt(this.statusBits);
        out.writeInt(this.tdsVersion);
        out.writeInt(this.typeVersion);
        out.writeObject(this.lds);
        out.writeObject(this.ldsOffsetArray);
        out.writeLong(this.fixedDataSize);
        out.writeInt(this.alignmentRequirement);
        out.writeObject(this.attrTypes);
        out.writeObject(this.attrNames);
        out.writeObject(this.attrTypeNames);
        out.writeLong(this.tdoCState);
        out.writeObject(this.toid);
        out.writeObject(this.fdo);
        out.writeInt(this.charSetId);
        out.writeInt(this.charSetForm);
        out.writeBoolean(this.bigEndian);
        out.writeInt(this.flattenedAttrNum);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "OracleTypeADT.readObject()", this);

            OracleLog.recursiveTrace = false;
        }

        this.statusBits = in.readInt();
        this.tdsVersion = in.readInt();
        this.typeVersion = in.readInt();
        in.readObject();
        in.readObject();
        in.readLong();
        in.readInt();
        this.attrTypes = ((OracleType[]) in.readObject());
        this.attrNames = ((String[]) in.readObject());
        this.attrTypeNames = ((String[]) in.readObject());
        in.readLong();
        this.toid = ((byte[]) in.readObject());
        this.fdo = ((byte[]) in.readObject());
        this.charSetId = in.readInt();
        this.charSetForm = in.readInt();
        this.bigEndian = in.readBoolean();
        this.flattenedAttrNum = in.readInt();
    }

    public synchronized void setConnection(OracleConnection conn) throws SQLException {
        this.connection = conn;
        for (int i = 0; i < this.attrTypes.length; i++)
            this.attrTypes[i].setConnection(this.connection);
    }

    private synchronized void setStatusBits(int bits) {
        this.statusBits |= bits;
    }

    private synchronized void maskAndSetStatusBits(int mask, int bits) {
        this.statusBits &= mask;
        this.statusBits |= bits;
    }

    private void printUnsignedByteArray(byte[] b, PrintWriter pw) {
        int length = b.length;

        int[] intArray = Util.toJavaUnsignedBytes(b);
        int i = 0;

        for (i = 0; i < length; i++) {
            pw.print("0x" + Integer.toHexString(intArray[i]) + " ");
        }

        pw.println();

        for (i = 0; i < length; i++) {
            pw.print(intArray[i] + " ");
        }

        pw.println();
    }

    public void initChildNamesRecursively(Map typesMap) throws SQLException {
        TypeTreeElement element = (TypeTreeElement) typesMap.get(this.sqlName);

        if ((this.attrTypes != null) && (this.attrTypes.length > 0)) {
            for (int j = 0; j < this.attrTypes.length; j++) {
                OracleType child = this.attrTypes[j];
                child.setNames(element.getChildSchemaName(j + 1), element.getChildTypeName(j + 1));
                child.initChildNamesRecursively(typesMap);
                child.cacheDescriptor();
            }
        }
    }

    public void cacheDescriptor() throws SQLException {
        this.descriptor = StructDescriptor.createDescriptor(this);
    }

    public void printXML(PrintWriter pw, int indent) throws SQLException {
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.print("<OracleTypeADT sqlName=\"" + this.sqlName + "\" " + " hashCode=\"" + hashCode()
                + "\" " + " toid=\"");

        if (this.toid != null)
            printUnsignedByteArray(this.toid, pw);
        else {
            pw.print("null");
        }
        pw.println(" \"  typecode=\""
                + this.typeCode
                + "\""
                + " tds version=\""
                + this.tdsVersion
                + "\""
                + " is embedded=\""
                + isEmbeddedADT()
                + "\""
                + " is top level=\""
                + isTopADT()
                + "\""
                + " is upt=\""
                + isUptADT()
                + "\""
                + " finalType=\""
                + isFinalType()
                + "\""
                + " subtype=\""
                + isSubType()
                + "\""
                + " ldsOffset=\""
                + this.ldsOffset
                + "\""
                + " sizeForLds=\""
                + this.sizeForLds
                + "\""
                + " alignForLds=\""
                + this.alignForLds
                + "\""
                + " ldsOffsetArray size=\""
                + (this.ldsOffsetArray == null ? "null" : Integer
                        .toString(this.ldsOffsetArray.length)) + "\"" + ">");

        if ((this.attrTypes != null) && (this.attrTypes.length > 0))
            for (int j = 0; j < this.attrTypes.length; j++)
                this.attrTypes[j].printXML(pw, indent + 1);
        for (int i = 0; i < indent; i++)
            pw.print("  ");
        pw.println("</OracleTypeADT>");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.OracleTypeADT"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.OracleTypeADT JD-Core Version: 0.6.0
 */