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
import oracle.jdbc.driver.DatabaseError;
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

public class OracleTypeADT extends OracleNamedType
  implements Serializable
{
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
  static final String[] sqlString = { "SELECT /*+ RULE */ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM USER_TYPE_ATTRS WHERE TYPE_NAME = :1 ORDER BY ATTR_NO", "SELECT /*+ RULE */ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM USER_TYPE_ATTRS WHERE TYPE_NAME in (SELECT TABLE_NAME FROM USER_SYNONYMS START WITH SYNONYM_NAME = :1 CONNECT BY PRIOR TABLE_NAME = SYNONYM_NAME UNION SELECT :1 FROM DUAL) ORDER BY ATTR_NO", "SELECT /*+RULE*/ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM USER_TYPE_ATTRS WHERE TYPE_NAME IN (SELECT TABLE_NAME FROM ALL_SYNONYMS START WITH SYNONYM_NAME = :1 AND  OWNER = 'PUBLIC' CONNECT BY PRIOR TABLE_NAME = SYNONYM_NAME AND TABLE_OWNER = OWNER UNION SELECT :2  FROM DUAL) ORDER BY ATTR_NO", "SELECT /*+ RULE */ ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM ALL_TYPE_ATTRS WHERE OWNER = :1 AND TYPE_NAME = :2 ORDER BY ATTR_NO", "SELECT /*+ RULE */ ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME, ATTR_TYPE_OWNER FROM ALL_TYPE_ATTRS WHERE OWNER = (SELECT TABLE_OWNER FROM ALL_SYNONYMS WHERE SYNONYM_NAME=:1) AND TYPE_NAME = (SELECT TABLE_NAME FROM ALL_SYNONYMS WHERE SYNONYM_NAME=:2) ORDER BY ATTR_NO", "DECLARE /*+RULE*/  the_owner VARCHAR2(100);   the_type  VARCHAR2(100); begin  SELECT /*+ RULE */TABLE_NAME, TABLE_OWNER INTO THE_TYPE, THE_OWNER  FROM ALL_SYNONYMS  WHERE TABLE_NAME IN (SELECT TYPE_NAME FROM ALL_TYPES)  START WITH SYNONYM_NAME = :1 AND OWNER = 'PUBLIC'  CONNECT BY PRIOR TABLE_NAME = SYNONYM_NAME AND TABLE_OWNER = OWNER; OPEN :2 FOR SELECT ATTR_NO, ATTR_NAME, ATTR_TYPE_NAME,  ATTR_TYPE_OWNER FROM ALL_TYPE_ATTRS  WHERE TYPE_NAME = THE_TYPE and OWNER = THE_OWNER; END;" };
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  protected OracleTypeADT()
  {
  }

  public OracleTypeADT(byte[] paramArrayOfByte, int paramInt1, int paramInt2, short paramShort, String paramString)
    throws SQLException
  {
    this(paramString, (OracleConnection)null);

    this.toid = paramArrayOfByte;
    this.typeVersion = paramInt1;
    this.charSetId = paramInt2;
    this.charSetForm = paramShort;
  }

  public OracleTypeADT(String paramString, Connection paramConnection)
    throws SQLException
  {
    super(paramString, (OracleConnection)paramConnection);
  }

  public OracleTypeADT(String paramString, Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    this(paramString, paramConnection);

    this.fdo = paramArrayOfByte;

    initEndianess(paramArrayOfByte);
  }

  public OracleTypeADT(OracleTypeADT paramOracleTypeADT, int paramInt, Connection paramConnection)
    throws SQLException
  {
    super(paramOracleTypeADT, paramInt, (OracleConnection)paramConnection);
  }

  public OracleTypeADT(OracleTypeADT paramOracleTypeADT, int paramInt, Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    this(paramOracleTypeADT, paramInt, paramConnection);

    this.fdo = paramArrayOfByte;

    initEndianess(paramArrayOfByte);
  }

  public OracleTypeADT(SQLName paramSQLName, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, OracleConnection paramOracleConnection, byte[] paramArrayOfByte4)
    throws SQLException
  {
    this.fdo = paramArrayOfByte4;
    init(paramArrayOfByte2, paramOracleConnection);
    this.toid = paramArrayOfByte1;
    this.typeVersion = paramInt;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    if (paramObject != null)
    {
      if ((paramObject instanceof STRUCT))
      {
        return (STRUCT)paramObject;
      }
      if (((paramObject instanceof SQLData)) || ((paramObject instanceof ObjectData)))
      {
        return STRUCT.toSTRUCT(paramObject, paramOracleConnection);
      }
      if ((paramObject instanceof Object[]))
      {
        StructDescriptor localStructDescriptor = createStructDescriptor();
        STRUCT localSTRUCT = createObjSTRUCT(localStructDescriptor, (Object[])paramObject);

        return localSTRUCT;
      }

      DatabaseError.throwSqlException(59, paramObject);
    }

    return null;
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof Object[]))
      {
        Object[] arrayOfObject = (Object[])paramObject;

        int i = (int)(paramInt == -1 ? arrayOfObject.length : Math.min(arrayOfObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (int j = 0; j < i; j++) {
          arrayOfDatum[j] = toDatum(arrayOfObject[((int)paramLong + j - 1)], paramOracleConnection);
        }
      }
      DatabaseError.throwSqlException(59, paramObject);
    }

    return arrayOfDatum;
  }

  public int getTypeCode()
    throws SQLException
  {
    if ((getStatus() & 0x10) != 0) {
      return 2008;
    }
    return 2002;
  }

  public OracleType[] getAttrTypes() throws SQLException
  {
    if (this.attrTypes == null) {
      init(this.connection);
    }
    return this.attrTypes;
  }

  public boolean isInHierarchyOf(OracleType paramOracleType)
    throws SQLException
  {
    if (paramOracleType == null) {
      return false;
    }
    if (!paramOracleType.isObjectType()) {
      return false;
    }
    StructDescriptor localStructDescriptor = (StructDescriptor)paramOracleType.getTypeDescriptor();

    return this.descriptor.isInHierarchyOf(localStructDescriptor.getName());
  }

  public boolean isInHierarchyOf(StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    if (paramStructDescriptor == null) {
      return false;
    }
    return this.descriptor.isInHierarchyOf(paramStructDescriptor.getName());
  }

  public boolean isObjectType()
  {
    return true;
  }

  public TypeDescriptor getTypeDescriptor()
  {
    return this.descriptor;
  }

  public synchronized void init(OracleConnection paramOracleConnection)
    throws SQLException
  {
    byte[] arrayOfByte = initMetadata(paramOracleConnection);
    init(arrayOfByte, paramOracleConnection);
  }

  public synchronized void init(byte[] paramArrayOfByte, OracleConnection paramOracleConnection)
    throws SQLException
  {
    this.statusBits = 1;
    this.connection = paramOracleConnection;

    if (paramArrayOfByte != null) parseTDS(paramArrayOfByte, 0L);
    this.bigEndian = this.connection.getBigEndian();
    setStatusBits(256);
  }

  public byte[] initMetadata(OracleConnection paramOracleConnection)
    throws SQLException
  {
    byte[] arrayOfByte = null;
    if ((this.statusBits & 0x100) != 0) return null;

    if (this.sqlName == null) getFullName();

    synchronized (paramOracleConnection)
    {
      synchronized (this)
      {
        if ((this.statusBits & 0x100) == 0)
        {
          CallableStatement localCallableStatement = null;
          try
          {
            if (this.tdoCState == 0L) this.tdoCState = this.connection.getTdoCState(this.sqlName.getSchema(), this.sqlName.getSimpleName());

            String str = null;
            int i = (this.fdo = this.connection.getFDO(false)) == null ? 1 : 0;

            if (i == 0)
            {
              str = "begin :1 := dbms_pickler.get_type_shape(:2,:3,:4,:5,:6,:7); end;";
            }
            else
            {
              str = "begin :1 := dbms_pickler.get_type_shape(:2,:3,:4,:5,:6,:7);       :8 := dbms_pickler.get_format(:9); end;";
            }

            int j = 0;
            localCallableStatement = this.connection.prepareCall(str);

            localCallableStatement.registerOutParameter(1, 2);
            localCallableStatement.registerOutParameter(4, -4);
            localCallableStatement.registerOutParameter(5, 4);
            localCallableStatement.registerOutParameter(6, -4);
            localCallableStatement.registerOutParameter(7, -4);
            if (i != 0)
            {
              localCallableStatement.registerOutParameter(8, 2);
              localCallableStatement.registerOutParameter(9, -4);
            }
            localCallableStatement.setString(2, this.sqlName.getSchema());
            localCallableStatement.setString(3, this.sqlName.getSimpleName());

            localCallableStatement.execute();

            int k = localCallableStatement.getInt(1);
            if (k != 0)
            {
              if (k != 24331) {
                DatabaseError.throwSqlException(74, this.sqlName.toString());
              }

              if (k == 24331)
              {
                j = 1;
                localCallableStatement.registerOutParameter(6, 2004);

                localCallableStatement.execute();

                k = localCallableStatement.getInt(1);
                if (k != 0)
                {
                  DatabaseError.throwSqlException(74, this.sqlName.toString());
                }
              }

            }

            if (i != 0)
            {
              if (localCallableStatement.getInt(8) != 0) {
                DatabaseError.throwSqlException(1, "dbms_pickler.get_format()");
              }

            }

            this.toid = localCallableStatement.getBytes(4);
            this.typeVersion = NUMBER.toInt(localCallableStatement.getBytes(5));
            if (j == 0)
            {
              arrayOfByte = localCallableStatement.getBytes(6);
            }
            else
            {
              try
              {
                Blob localBlob = ((OracleCallableStatement)localCallableStatement).getBlob(6);
                InputStream localInputStream = localBlob.getBinaryStream();
                arrayOfByte = new byte[(int)localBlob.length()];
                localInputStream.read(arrayOfByte);
                localInputStream.close();
                ((BLOB)localBlob).freeTemporary();
              }
              catch (IOException localIOException) {
                DatabaseError.throwSqlException(localIOException);
              }

            }

            if (i != 0)
            {
              this.fdo = localCallableStatement.getBytes(9);
              this.connection.setFDO(this.fdo);
            }

            this.flattenedAttrNum = (Util.getUnsignedByte(arrayOfByte[8]) * 256 + Util.getUnsignedByte(arrayOfByte[9]));

            localCallableStatement.getBytes(7);
          }
          finally
          {
            if (localCallableStatement != null) localCallableStatement.close();
          }
        }
        setStatusBits(256);
      }
    }
    return arrayOfByte;
  }

  private void initEndianess(byte[] paramArrayOfByte)
  {
    int[] arrayOfInt = Util.toJavaUnsignedBytes(paramArrayOfByte);
    int i = arrayOfInt[(6 + arrayOfInt[5] + arrayOfInt[6] + 5)];

    int j = (byte)(i & 0x10);

    if (j < 0) {
      j += 256;
    }
    this.bigEndian = (j > 0);
  }

  void parseLDS(InputStream paramInputStream)
    throws SQLException
  {
    long l = Util.readLong(paramInputStream);

    this.fixedDataSize = Util.readLong(paramInputStream);
    this.ldsOffsetArray = new long[this.flattenedAttrNum];

    for (int i = 0; i < this.flattenedAttrNum; i++)
    {
      this.ldsOffsetArray[i] = Util.readLong(paramInputStream);
    }
  }

  public void generateLDS()
    throws SQLException
  {
    Vector localVector = generateLDSrec();

    this.ldsOffsetArray = new long[localVector.size()];

    for (int i = 0; i < localVector.size(); i++)
    {
      Integer localInteger = (Integer)localVector.elementAt(i);

      this.ldsOffsetArray[i] = localInteger.longValue();
    }
  }

  private Vector generateLDSrec()
    throws SQLException
  {
    int i = 0;
    int j = 0;

    Vector localVector1 = new Vector();
    int k = getNumAttrs();

    for (int m = 0; m < k; m++)
    {
      Vector localVector2 = null;
      OracleType localOracleType = getAttrTypeAt(m);
      int i1;
      int n;
      if (((localOracleType instanceof OracleTypeADT)) && (!(localOracleType instanceof OracleTypeCOLLECTION)) && (!(localOracleType instanceof OracleTypeUPT)))
      {
        localVector2 = ((OracleTypeADT)localOracleType).generateLDSrec();
        i1 = ((OracleTypeADT)localOracleType).getAlignmentReq();
        n = (int)((OracleTypeADT)localOracleType).getFixedDataSize();
      }
      else
      {
        n = localOracleType.getSizeLDS(this.fdo);
        i1 = localOracleType.getAlignLDS(this.fdo);
      }

      if ((i & i1) > 0)
        i = Util.ldsRound(i, i1);
      int i2;
      if (((localOracleType instanceof OracleTypeADT)) && (!(localOracleType instanceof OracleTypeCOLLECTION)) && (!(localOracleType instanceof OracleTypeUPT)))
      {
        for (i2 = 0; i2 < localVector2.size(); )
        {
          Integer localInteger1 = (Integer)localVector2.elementAt(i2);
          Integer localInteger2 = new Integer(localInteger1.intValue() + i);

          localVector1.addElement(localInteger2);

          i2++; continue;

          localVector1.addElement(new Integer(i));
        }
      }
      i += n;

      if (i1 > j) {
        j = i1;
      }

    }

    if ((i & j) > 0) {
      i = Util.ldsRound(i, j);
    }
    this.alignmentRequirement = j;
    this.fixedDataSize = i;

    return localVector1;
  }

  TDSReader parseTDS(byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    if (this.attrTypes != null) {
      return null;
    }

    TDSReader localTDSReader = new TDSReader(paramArrayOfByte, paramLong);

    long l1 = localTDSReader.readLong() + localTDSReader.offset();

    localTDSReader.checkNextByte(38);

    this.tdsVersion = localTDSReader.readByte();

    localTDSReader.skipBytes(2);

    this.flattenedAttrNum = localTDSReader.readShort();

    if ((localTDSReader.readByte() & 0xFF) == 255) {
      setStatusBits(128);
    }

    long l2 = localTDSReader.offset();

    localTDSReader.checkNextByte(41);

    if (localTDSReader.readShort() != 0) {
      DatabaseError.throwSqlException(47, "parseTDS");
    }

    long l3 = localTDSReader.readLong();

    parseTDSrec(localTDSReader);

    if (this.tdsVersion >= 3)
    {
      localTDSReader.skip_to(l2 + l3 + 2L);

      localTDSReader.skipBytes(2 * this.flattenedAttrNum);

      byte b = localTDSReader.readByte();

      if (localTDSReader.isJavaObject(this.tdsVersion, b)) {
        setStatusBits(16);
      }

      if (localTDSReader.isFinalType(this.tdsVersion, b)) {
        setStatusBits(32);
      }

      if (localTDSReader.readByte() != 1)
        setStatusBits(64);
    }
    else
    {
      setStatusBits(32);
    }

    localTDSReader.skip_to(l1);
    return localTDSReader;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    Vector localVector = new Vector(5);
    OracleType localOracleType = null;

    this.nullOffset = paramTDSReader.nullOffset;
    this.ldsOffset = paramTDSReader.ldsOffset;

    paramTDSReader.nullOffset += 1;

    this.idx = 1;

    while ((localOracleType = getNextTypeObject(paramTDSReader)) != null)
    {
      localVector.addElement(localOracleType);
    }

    if (this.opcode == 42)
    {
      this.endOfAdt = true;

      applyTDSpatches(paramTDSReader);
    }

    this.attrTypes = new OracleType[localVector.size()];

    localVector.copyInto(this.attrTypes);
  }

  private void applyTDSpatches(TDSReader paramTDSReader)
    throws SQLException
  {
    TDSPatch localTDSPatch = paramTDSReader.getNextPatch();

    while (localTDSPatch != null)
    {
      paramTDSReader.moveToPatchPos(localTDSPatch);

      int i = localTDSPatch.getType();

      if (i == 0)
      {
        paramTDSReader.readByte();

        int j = localTDSPatch.getUptTypeCode();
        OracleNamedType localOracleNamedType;
        Object localObject;
        switch (j)
        {
        case -6:
          paramTDSReader.readLong();
        case -5:
          localOracleNamedType = localTDSPatch.getOwner();
          localObject = null;

          if (localOracleNamedType.hasName())
          {
            localObject = new OracleTypeADT(localOracleNamedType.getFullName(), this.connection, this.fdo);
          }
          else
          {
            localObject = new OracleTypeADT(localOracleNamedType.getParent(), localOracleNamedType.getOrder(), this.connection, this.fdo);
          }

          ((OracleTypeADT)localObject).setUptADT();
          TDSReader localTDSReader = ((OracleTypeADT)localObject).parseTDS(paramTDSReader.tds(), paramTDSReader.absoluteOffset());

          paramTDSReader.skipBytes((int)localTDSReader.offset());
          localTDSPatch.apply(((OracleTypeADT)localObject).cleanup());

          break;
        case 58:
          localOracleNamedType = localTDSPatch.getOwner();
          localObject = null;

          if (localOracleNamedType.hasName())
          {
            localObject = new OracleTypeOPAQUE(localOracleNamedType.getFullName(), this.connection);
          }
          else
          {
            localObject = new OracleTypeOPAQUE(localOracleNamedType.getParent(), localOracleNamedType.getOrder(), this.connection);
          }

          ((OracleTypeOPAQUE)localObject).parseTDSrec(paramTDSReader);
          localTDSPatch.apply((OracleType)localObject);

          break;
        default:
          DatabaseError.throwSqlException(1); break;
        }

      }
      else if (i == 1)
      {
        OracleType localOracleType = getNextTypeObject(paramTDSReader);

        localTDSPatch.apply(localOracleType, this.opcode);
      }
      else {
        DatabaseError.throwSqlException(47, "parseTDS");
      }

      localTDSPatch = paramTDSReader.getNextPatch();
    }
  }

  public synchronized OracleNamedType cleanup()
  {
    Object localObject;
    if ((this.attrTypes.length == 1) && ((this.attrTypes[0] instanceof OracleTypeCOLLECTION)))
    {
      localObject = (OracleTypeCOLLECTION)this.attrTypes[0];

      ((OracleTypeCOLLECTION)localObject).copy_properties(this);

      return localObject;
    }
    if ((this.attrTypes.length == 1) && ((this.statusBits & 0x80) != 0) && ((this.attrTypes[0] instanceof OracleTypeUPT)) && ((((OracleTypeUPT)this.attrTypes[0]).realType instanceof OracleTypeOPAQUE)))
    {
      localObject = (OracleTypeOPAQUE)((OracleTypeUPT)this.attrTypes[0]).realType;

      ((OracleTypeOPAQUE)localObject).copy_properties(this);

      return localObject;
    }

    return (OracleNamedType)this;
  }

  void copy_properties(OracleTypeADT paramOracleTypeADT)
  {
    this.sqlName = paramOracleTypeADT.sqlName;
    this.parent = paramOracleTypeADT.parent;
    this.idx = paramOracleTypeADT.idx;
    this.connection = paramOracleTypeADT.connection;
    this.lds = paramOracleTypeADT.lds;
    this.toid = paramOracleTypeADT.toid;
    this.fdo = paramOracleTypeADT.fdo;
    this.tdsVersion = paramOracleTypeADT.tdsVersion;
    this.typeVersion = paramOracleTypeADT.typeVersion;
    this.tdoCState = paramOracleTypeADT.tdoCState;
    this.nullOffset = paramOracleTypeADT.nullOffset;
    this.bigEndian = paramOracleTypeADT.bigEndian;
    this.endOfAdt = paramOracleTypeADT.endOfAdt;
  }

  OracleType getNextTypeObject(TDSReader paramTDSReader)
    throws SQLException
  {
    while (true)
    {
      this.opcode = paramTDSReader.readByte();

      if (this.opcode == 43)
      {
        continue;
      }

      if (this.opcode != 44)
      {
        break;
      }
      byte b = paramTDSReader.readByte();

      if (paramTDSReader.isJavaObject(3, b)) {
        setStatusBits(16);
      }

    }

    switch (this.opcode)
    {
    case 40:
    case 42:
      return null;
    case 2:
      localObject = new OracleTypeDATE();

      ((OracleTypeDATE)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 7:
      localObject = new OracleTypeCHAR(this.connection, 12);

      ((OracleTypeCHAR)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 1:
      localObject = new OracleTypeCHAR(this.connection, 1);

      ((OracleTypeCHAR)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 39:
      localObject = new OracleTypeADT(this, this.idx, this.connection, this.fdo);

      ((OracleTypeADT)localObject).setEmbeddedADT();
      ((OracleTypeADT)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 6:
      localObject = new OracleTypeNUMBER(2);

      ((OracleTypeNUMBER)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 3:
      localObject = new OracleTypeNUMBER(3);

      ((OracleTypeNUMBER)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 4:
      localObject = new OracleTypeNUMBER(8);

      ((OracleTypeNUMBER)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 5:
      localObject = new OracleTypeFLOAT();

      ((OracleTypeFLOAT)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 37:
      localObject = new OracleTypeBINARY_FLOAT();

      ((OracleTypeBINARY_FLOAT)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 45:
      localObject = new OracleTypeBINARY_DOUBLE();

      ((OracleTypeBINARY_DOUBLE)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 8:
      localObject = new OracleTypeSINT32();

      ((OracleTypeSINT32)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 9:
      localObject = new OracleTypeREF(this, this.idx, this.connection);

      ((OracleTypeREF)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 31:
      localObject = new OracleTypeBFILE(this.connection);

      ((OracleTypeBFILE)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 19:
      localObject = new OracleTypeRAW();

      ((OracleTypeRAW)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 29:
      localObject = new OracleTypeCLOB(this.connection);

      ((OracleTypeCLOB)localObject).parseTDSrec(paramTDSReader);

      if ((this.sqlName != null) && (!this.endOfAdt)) {
        this.connection.getForm(this, (OracleTypeCLOB)localObject, this.idx);
      }
      this.idx += 1;

      return localObject;
    case 30:
      localObject = new OracleTypeBLOB(this.connection);

      ((OracleTypeBLOB)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 21:
      localObject = new OracleTypeTIMESTAMP(this.connection);

      ((OracleTypeTIMESTAMP)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 23:
      localObject = new OracleTypeTIMESTAMPTZ(this.connection);

      ((OracleTypeTIMESTAMPTZ)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 33:
      localObject = new OracleTypeTIMESTAMPLTZ(this.connection);

      ((OracleTypeTIMESTAMPLTZ)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 24:
      localObject = new OracleTypeINTERVAL(this.connection);

      ((OracleTypeINTERVAL)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 28:
      localObject = new OracleTypeCOLLECTION(this, this.idx, this.connection);

      ((OracleTypeCOLLECTION)localObject).bigEndian = this.bigEndian;

      ((OracleTypeCOLLECTION)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
    case 27:
      localObject = new OracleTypeUPT(this, this.idx, this.connection);

      ((OracleTypeUPT)localObject).bigEndian = this.bigEndian;

      ((OracleTypeUPT)localObject).parseTDSrec(paramTDSReader);

      this.idx += 1;

      return localObject;
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
    case 44: } Object localObject = null;

    DatabaseError.throwSqlException(48, "get_next_type: " + this.opcode);

    return (OracleType)null;
  }

  public synchronized byte[] linearize(Datum paramDatum)
    throws SQLException
  {
    return pickle81(paramDatum);
  }

  public Datum unlinearize(byte[] paramArrayOfByte, long paramLong, Datum paramDatum, int paramInt, Map paramMap)
    throws SQLException
  {
    OracleConnection localOracleConnection = getConnection();
    Datum localDatum = null;

    if (localOracleConnection == null)
    {
      localDatum = _unlinearize(paramArrayOfByte, paramLong, paramDatum, paramInt, paramMap);
    }
    else
    {
      synchronized (localOracleConnection)
      {
        localDatum = _unlinearize(paramArrayOfByte, paramLong, paramDatum, paramInt, paramMap);
      }
    }

    return localDatum;
  }

  public synchronized Datum _unlinearize(byte[] paramArrayOfByte, long paramLong, Datum paramDatum, int paramInt, Map paramMap)
    throws SQLException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if (PickleContext.is81format(paramArrayOfByte[0]))
    {
      localObject = new PickleContext(paramArrayOfByte, paramLong);

      return unpickle81((PickleContext)localObject, (STRUCT)paramDatum, 1, paramInt, paramMap);
    }

    Object localObject = new UnpickleContext(paramArrayOfByte, (int)paramLong, null, null, this.bigEndian);

    return (Datum)unpickle80((UnpickleContext)localObject, (STRUCT)paramDatum, 1, paramInt, paramMap);
  }

  protected STRUCT unpickle80(UnpickleContext paramUnpickleContext, STRUCT paramSTRUCT, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    STRUCT localSTRUCT1 = paramSTRUCT;

    if (paramInt1 == 3)
    {
      if (localSTRUCT1 == null)
      {
        StructDescriptor localStructDescriptor = createStructDescriptor();

        localSTRUCT1 = createByteSTRUCT(localStructDescriptor, (byte[])null);
      }

      localSTRUCT1.setImage(paramUnpickleContext.image(), paramUnpickleContext.absoluteOffset(), 0L);
    }

    long l1 = paramUnpickleContext.readLong();

    if (l1 == 0L)
    {
      return null;
    }
    if (paramInt1 == 9)
    {
      paramUnpickleContext.skipBytes((int)l1);

      return localSTRUCT1;
    }
    if (paramInt1 == 3)
    {
      localSTRUCT1.setImageLength(l1 + 4L);
      paramUnpickleContext.skipBytes((int)l1);

      return localSTRUCT1;
    }

    paramUnpickleContext.skipBytes(1);

    int i = paramUnpickleContext.readByte();
    boolean[] arrayOfBoolean = unpickle_nulls(paramUnpickleContext);
    long l2 = paramUnpickleContext.readLong();
    long l3 = paramUnpickleContext.offset() + l2;
    try
    {
      if (arrayOfBoolean[0] == 0)
      {
        UnpickleContext localUnpickleContext = new UnpickleContext(paramUnpickleContext.image(), paramUnpickleContext.absoluteOffset(), arrayOfBoolean, getLdsOffsetArray(), this.bigEndian);

        STRUCT localSTRUCT2 = unpickle80rec(localUnpickleContext, localSTRUCT1, paramInt2, paramMap);
        return localSTRUCT2;
      } } finally { paramUnpickleContext.skipTo(l3);
    }

    return localSTRUCT1;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    STRUCT localSTRUCT = unpickle80rec(paramUnpickleContext, null, 1, paramMap);

    return toObject(localSTRUCT, paramInt2, paramMap);
  }

  private STRUCT unpickle80rec(UnpickleContext paramUnpickleContext, STRUCT paramSTRUCT, int paramInt, Map paramMap)
    throws SQLException
  {
    if (paramUnpickleContext.isNull(this.nullOffset)) {
      return null;
    }
    paramUnpickleContext.skipTo(paramUnpickleContext.ldsOffsets[this.ldsOffset]);

    int i = getNumAttrs();
    STRUCT localSTRUCT = paramSTRUCT;
    Object localObject;
    if (localSTRUCT == null)
    {
      localObject = createStructDescriptor();

      localSTRUCT = createByteSTRUCT((StructDescriptor)localObject, (byte[])null);
    }
    int j;
    switch (paramInt)
    {
    case 1:
      localObject = new Datum[i];

      for (j = 0; j < i; j++)
      {
        localObject[j] = ((Datum)getAttrTypeAt(j).unpickle80rec(paramUnpickleContext, 1, paramInt, paramMap));
      }

      localSTRUCT.setDatumArray(localObject);

      break;
    case 2:
      localObject = new Object[i];

      for (j = 0; j < i; j++)
      {
        localObject[j] = getAttrTypeAt(j).unpickle80rec(paramUnpickleContext, 1, paramInt, paramMap);
      }

      localSTRUCT.setObjArray(localObject);

      break;
    default:
      DatabaseError.throwSqlException(1);
    }

    return (STRUCT)localSTRUCT;
  }

  private boolean[] unpickle_nulls(UnpickleContext paramUnpickleContext)
    throws SQLException
  {
    paramUnpickleContext.skipBytes(4);

    byte[] arrayOfByte = paramUnpickleContext.readLengthBytes();

    boolean[] arrayOfBoolean = new boolean[(arrayOfByte.length - 2) * 4];

    int i = 0;

    for (int j = 0; j < arrayOfBoolean.length; j++)
    {
      if (j % 4 == 0) {
        i = arrayOfByte[(2 + j / 4)];
      }
      arrayOfBoolean[j] = ((i & 0x3) != 0 ? 1 : false);
      i = (byte)(i >> 2);
    }

    return arrayOfBoolean;
  }

  protected STRUCT unpickle81(PickleContext paramPickleContext, STRUCT paramSTRUCT, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    STRUCT localSTRUCT = paramSTRUCT;
    long l1 = paramPickleContext.offset();

    byte b = paramPickleContext.readByte();

    if (!PickleContext.is81format(b)) {
      DatabaseError.throwSqlException(1, "Image is not in 8.1 format");
    }

    if (PickleContext.isCollectionImage_pctx(b)) {
      DatabaseError.throwSqlException(1, "Image is a collection image, expecting ADT");
    }

    if (!paramPickleContext.readAndCheckVersion()) {
      DatabaseError.throwSqlException(1, "Image version is not recognized");
    }

    switch (paramInt1)
    {
    case 9:
      paramPickleContext.skipBytes(paramPickleContext.readLength(true) - 2);

      break;
    case 3:
      long l2 = paramPickleContext.readLength();

      localSTRUCT = unpickle81Prefix(paramPickleContext, localSTRUCT, b);

      if (localSTRUCT == null)
      {
        StructDescriptor localStructDescriptor = createStructDescriptor();

        localSTRUCT = createByteSTRUCT(localStructDescriptor, (byte[])null);
      }

      localSTRUCT.setImage(paramPickleContext.image(), l1, 0L);
      localSTRUCT.setImageLength(l2);
      paramPickleContext.skipTo(l1 + l2);

      break;
    default:
      paramPickleContext.skipLength();

      localSTRUCT = unpickle81Prefix(paramPickleContext, localSTRUCT, b);

      if (localSTRUCT == null)
      {
        localObject1 = createStructDescriptor();

        localSTRUCT = createByteSTRUCT((StructDescriptor)localObject1, (byte[])null);
      }

      Object localObject1 = localSTRUCT.getDescriptor().getOracleTypeADT().getAttrTypes();
      Object localObject2;
      int i;
      switch (paramInt2)
      {
      case 1:
        localObject2 = new Datum[localObject1.length];

        for (i = 0; i < localObject1.length; i++)
        {
          localObject2[i] = ((Datum)localObject1[i].unpickle81rec(paramPickleContext, paramInt2, paramMap));
        }

        localSTRUCT.setDatumArray(localObject2);

        break;
      case 2:
        localObject2 = new Object[localObject1.length];

        for (i = 0; i < localObject1.length; i++)
        {
          localObject2[i] = localObject1[i].unpickle81rec(paramPickleContext, paramInt2, paramMap);
        }

        localSTRUCT.setObjArray(localObject2);

        break;
      default:
        DatabaseError.throwSqlException(1);
      }

    }

    return (STRUCT)(STRUCT)localSTRUCT;
  }

  protected STRUCT unpickle81Prefix(PickleContext paramPickleContext, STRUCT paramSTRUCT, byte paramByte)
    throws SQLException
  {
    STRUCT localSTRUCT = paramSTRUCT;

    if (PickleContext.hasPrefix(paramByte))
    {
      long l = paramPickleContext.readLength() + paramPickleContext.absoluteOffset();

      int i = paramPickleContext.readByte();

      int j = (byte)(i & 0xC);
      int k = j == 0 ? 1 : 0;

      int m = j == 4 ? 1 : 0;

      int n = j == 8 ? 1 : 0;

      int i1 = j == 12 ? 1 : 0;

      int i2 = (i & 0x10) != 0 ? 1 : 0;

      if (m != 0)
      {
        byte[] arrayOfByte = paramPickleContext.readBytes(16);
        String str = toid2typename(this.connection, arrayOfByte);

        StructDescriptor localStructDescriptor = (StructDescriptor)TypeDescriptor.getTypeDescriptor(str, this.connection);

        if (localSTRUCT == null)
          localSTRUCT = createByteSTRUCT(localStructDescriptor, (byte[])null);
        else {
          localSTRUCT.setDescriptor(localStructDescriptor);
        }
      }
      if (i2 != 0)
      {
        this.typeVersion = paramPickleContext.readLength();
      }
      else {
        this.typeVersion = 1;
      }
      if ((n | i1) != 0) {
        DatabaseError.throwSqlException(23);
      }
      paramPickleContext.skipTo(l);
    }

    return localSTRUCT;
  }

  protected Object unpickle81rec(PickleContext paramPickleContext, int paramInt, Map paramMap)
    throws SQLException
  {
    byte b1 = paramPickleContext.readByte();
    byte b2 = 0;

    if (PickleContext.isAtomicNull(b1))
      return null;
    if (PickleContext.isImmediatelyEmbeddedNull(b1)) {
      b2 = paramPickleContext.readByte();
    }
    STRUCT localSTRUCT = unpickle81datum(paramPickleContext, b1, b2);

    return toObject(localSTRUCT, paramInt, paramMap);
  }

  protected Object unpickle81rec(PickleContext paramPickleContext, byte paramByte, int paramInt, Map paramMap)
    throws SQLException
  {
    STRUCT localSTRUCT = unpickle81datum(paramPickleContext, paramByte, 0);

    return toObject(localSTRUCT, paramInt, paramMap);
  }

  private STRUCT unpickle81datum(PickleContext paramPickleContext, byte paramByte1, byte paramByte2)
    throws SQLException
  {
    int i = getNumAttrs();

    StructDescriptor localStructDescriptor = createStructDescriptor();
    STRUCT localSTRUCT = createByteSTRUCT(localStructDescriptor, (byte[])null);
    OracleType localOracleType = getAttrTypeAt(0);
    Object localObject = null;

    if ((PickleContext.isImmediatelyEmbeddedNull(paramByte1)) && (paramByte2 == 1))
      localObject = null;
    else if (PickleContext.isImmediatelyEmbeddedNull(paramByte1)) {
      localObject = ((OracleTypeADT)localOracleType).unpickle81datum(paramPickleContext, paramByte1, (byte)(paramByte2 - 1));
    }
    else if (PickleContext.isElementNull(paramByte1))
    {
      if ((localOracleType.getTypeCode() == 2002) || (localOracleType.getTypeCode() == 2008))
      {
        localObject = localOracleType.unpickle81datumAsNull(paramPickleContext, paramByte1, paramByte2);
      }
      else localObject = null;
    }
    else {
      localObject = localOracleType.unpickle81rec(paramPickleContext, paramByte1, 1, null);
    }

    Datum[] arrayOfDatum = new Datum[i];

    arrayOfDatum[0] = ((Datum)localObject);

    for (int j = 1; j < i; j++)
    {
      localOracleType = getAttrTypeAt(j);
      arrayOfDatum[j] = ((Datum)localOracleType.unpickle81rec(paramPickleContext, 1, null));
    }

    localSTRUCT.setDatumArray(arrayOfDatum);

    return (STRUCT)localSTRUCT;
  }

  protected Datum unpickle81datumAsNull(PickleContext paramPickleContext, byte paramByte1, byte paramByte2)
    throws SQLException
  {
    int i = getNumAttrs();

    StructDescriptor localStructDescriptor = createStructDescriptor();
    STRUCT localSTRUCT = createByteSTRUCT(localStructDescriptor, (byte[])null);
    Datum[] arrayOfDatum = new Datum[i];

    for (int j = 0; j < i; j++)
    {
      OracleType localOracleType = getAttrTypeAt(j);
      if ((localOracleType.getTypeCode() == 2002) || (localOracleType.getTypeCode() == 2008))
      {
        arrayOfDatum[j] = localOracleType.unpickle81datumAsNull(paramPickleContext, paramByte1, paramByte2);
      }
      else arrayOfDatum[j] = null;
    }
    localSTRUCT.setDatumArray(arrayOfDatum);
    return localSTRUCT;
  }

  public byte[] pickle81(Datum paramDatum)
    throws SQLException
  {
    PickleContext localPickleContext = new PickleContext();

    localPickleContext.initStream();
    pickle81(localPickleContext, paramDatum);

    byte[] arrayOfByte = localPickleContext.stream2Bytes();

    paramDatum.setShareBytes(arrayOfByte);

    return arrayOfByte;
  }

  protected int pickle81(PickleContext paramPickleContext, Datum paramDatum)
    throws SQLException
  {
    int i = paramPickleContext.offset() + 2;
    int j = 0;

    j += paramPickleContext.writeImageHeader(shouldHavePrefix());

    j += pickle81Prefix(paramPickleContext);
    j += pickle81rec(paramPickleContext, paramDatum, 0);

    paramPickleContext.patchImageLen(i, j);

    return j;
  }

  private boolean hasTypeVersion()
  {
    return this.typeVersion > 1;
  }

  private boolean needsToid()
  {
    return ((this.statusBits & 0x40) != 0) || ((this.statusBits & 0x20) == 0) || (hasTypeVersion());
  }

  private boolean shouldHavePrefix()
  {
    return (hasTypeVersion()) || (needsToid());
  }

  protected int pickle81Prefix(PickleContext paramPickleContext)
    throws SQLException
  {
    if (shouldHavePrefix())
    {
      int i = 0;
      int j = 1;
      int k = 1;

      if (needsToid())
      {
        k += getTOID().length;
        j |= 4;
      }

      if (hasTypeVersion())
      {
        j |= 16;

        if (this.typeVersion > PickleContext.KOPI20_LN_MAXV)
          k += 5;
        else {
          k++;
        }
      }
      i = paramPickleContext.writeLength(k);

      i += paramPickleContext.writeData((byte)j);

      if (needsToid()) {
        i += paramPickleContext.writeData(this.toid);
      }
      if (hasTypeVersion()) {
        i += paramPickleContext.writeLength(this.typeVersion);
      }
      return i;
    }

    return 0;
  }

  private int pickle81rec(PickleContext paramPickleContext, Datum paramDatum, int paramInt)
    throws SQLException
  {
    int i = 0;

    Datum[] arrayOfDatum = ((STRUCT)paramDatum).getOracleAttributes();
    int j = arrayOfDatum.length;
    int k = 0;
    OracleType localOracleType = getAttrTypeAt(0);

    if (((localOracleType instanceof OracleTypeADT)) && (!(localOracleType instanceof OracleTypeCOLLECTION)) && (!(localOracleType instanceof OracleTypeUPT)))
    {
      k = 1;

      if (arrayOfDatum[0] == null)
      {
        if (paramInt > 0)
          i += paramPickleContext.writeImmediatelyEmbeddedElementNull((byte)paramInt);
        else {
          i += paramPickleContext.writeAtomicNull();
        }
      }
      else {
        i += ((OracleTypeADT)localOracleType).pickle81rec(paramPickleContext, arrayOfDatum[0], paramInt + 1);
      }

    }

    for (; k < j; k++)
    {
      localOracleType = getAttrTypeAt(k);

      if (arrayOfDatum[k] == null)
      {
        if (((localOracleType instanceof OracleTypeADT)) && (!(localOracleType instanceof OracleTypeCOLLECTION)) && (!(localOracleType instanceof OracleTypeUPT)))
        {
          i += paramPickleContext.writeAtomicNull();
        }
        else
        {
          i += paramPickleContext.writeElementNull();
        }

      }
      else if (((localOracleType instanceof OracleTypeADT)) && (!(localOracleType instanceof OracleTypeCOLLECTION)) && (!(localOracleType instanceof OracleTypeUPT)))
      {
        i += ((OracleTypeADT)localOracleType).pickle81rec(paramPickleContext, arrayOfDatum[k], 1);
      }
      else
      {
        i += localOracleType.pickle81(paramPickleContext, arrayOfDatum[k]);
      }

    }

    return i;
  }

  private Object toObject(STRUCT paramSTRUCT, int paramInt, Map paramMap)
    throws SQLException
  {
    switch (paramInt)
    {
    case 1:
      return paramSTRUCT;
    case 2:
      if (paramSTRUCT == null) break;
      return paramSTRUCT.toJdbc(paramMap);
    default:
      DatabaseError.throwSqlException(1);
    }

    return null;
  }

  public String getAttributeType(int paramInt)
    throws SQLException
  {
    if (this.sqlName == null) {
      getFullName();
    }
    if (this.attrNames == null) {
      initADTAttrNames();
    }
    if ((paramInt < 1) || (paramInt > this.attrTypeNames.length)) {
      DatabaseError.throwSqlException(1, "Invalid index");
    }
    return this.attrTypeNames[(paramInt - 1)];
  }

  public String getAttributeType(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    if (paramBoolean) {
      return getAttributeType(paramInt);
    }

    if ((paramInt < 1) || ((this.attrTypeNames != null) && (paramInt > this.attrTypeNames.length))) {
      DatabaseError.throwSqlException(1, "Invalid index");
    }

    if (this.attrTypeNames != null) {
      return this.attrTypeNames[(paramInt - 1)];
    }
    return null;
  }

  public String getAttributeName(int paramInt)
    throws SQLException
  {
    if (this.attrNames == null) {
      initADTAttrNames();
    }
    synchronized (this)
    {
      if ((paramInt < 1) || (paramInt > this.attrNames.length)) {
        DatabaseError.throwSqlException(1, "Invalid index");
      }
    }

    return this.attrNames[(paramInt - 1)];
  }

  public String getAttributeName(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    if (paramBoolean) {
      return getAttributeName(paramInt);
    }

    if ((paramInt < 1) || ((this.attrNames != null) && (paramInt > this.attrNames.length))) {
      DatabaseError.throwSqlException(1, "Invalid index");
    }

    if (this.attrNames != null) {
      return this.attrNames[(paramInt - 1)];
    }
    return null;
  }

  private void initADTAttrNames()
    throws SQLException
  {
    CallableStatement localCallableStatement = null;
    PreparedStatement localPreparedStatement = null;
    ResultSet localResultSet = null;
    String[] arrayOfString1 = new String[this.attrTypes.length];
    String[] arrayOfString2 = new String[this.attrTypes.length];
    int i = 0;
    int j = 0;

    synchronized (this.connection)
    {
      synchronized (this)
      {
        if (this.attrNames == null)
        {
          i = this.sqlName.getSchema().equalsIgnoreCase(this.connection.getUserName()) ? 0 : 3;
          while (true)
          {
            if (i != 6)
            {
              switch (i)
              {
              case 0:
                localPreparedStatement = this.connection.prepareStatement(sqlString[i]);
                localPreparedStatement.setString(1, this.sqlName.getSimpleName());
                localResultSet = localPreparedStatement.executeQuery();
                i = 1;
                break;
              case 1:
                localPreparedStatement = this.connection.prepareStatement(sqlString[i]);
                localPreparedStatement.setString(1, this.sqlName.getSimpleName());
                localPreparedStatement.setString(2, this.sqlName.getSimpleName());
                i = 2;
                try { localResultSet = localPreparedStatement.executeQuery();
                }
                catch (SQLException localSQLException)
                {
                }
                if (localSQLException.getErrorCode() == 1436) {
                  continue;
                }
                throw localSQLException;
              case 2:
                localPreparedStatement = this.connection.prepareStatement(sqlString[i]);
                localPreparedStatement.setString(1, this.sqlName.getSimpleName());
                localPreparedStatement.setString(2, this.sqlName.getSimpleName());
                localResultSet = localPreparedStatement.executeQuery();
                i = 4;
                break;
              case 3:
                localPreparedStatement = this.connection.prepareStatement(sqlString[i]);
                localPreparedStatement.setString(1, this.sqlName.getSchema());
                localPreparedStatement.setString(2, this.sqlName.getSimpleName());
                localResultSet = localPreparedStatement.executeQuery();
                i = 4;
                break;
              case 4:
                localPreparedStatement = this.connection.prepareStatement(sqlString[i]);
                localPreparedStatement.setString(1, this.sqlName.getSimpleName());
                localPreparedStatement.setString(2, this.sqlName.getSimpleName());
                localResultSet = localPreparedStatement.executeQuery();
                i = 5;
                break;
              case 5:
                localCallableStatement = this.connection.prepareCall(sqlString[i]);
                localCallableStatement.setString(1, this.sqlName.getSimpleName());
                localCallableStatement.registerOutParameter(2, -10);
                localCallableStatement.execute();
                localResultSet = ((OracleCallableStatement)localCallableStatement).getCursor(2);
                i = 6;
              default:
                try
                {
                  if (localPreparedStatement != null) {
                    localPreparedStatement.setFetchSize(this.idx);
                  }
                  j = 0;
                  while ((j < this.attrTypes.length) && (localResultSet.next()))
                  {
                    if (localResultSet.getInt(1) != j + 1) {
                      DatabaseError.throwSqlException(1, "inconsistent ADT attribute");
                    }

                    arrayOfString1[j] = localResultSet.getString(2);

                    arrayOfString2[j] = (localResultSet.getString(4) + "." + localResultSet.getString(3));

                    j++;
                  }

                  if (j != 0)
                  {
                    this.attrTypeNames = arrayOfString2;

                    this.attrNames = arrayOfString1;
                    i = 6;
                  }
                  else {
                    if (localResultSet != null)
                      localResultSet.close();
                    if (localPreparedStatement != null)
                      localPreparedStatement.close();
                  }
                } finally {
                  if (localResultSet != null)
                    localResultSet.close();
                  if (localPreparedStatement != null)
                    localPreparedStatement.close();
                  if (localCallableStatement != null)
                    localCallableStatement.close();
                }
              }
            }
          }
        }
      }
    }
  }

  StructDescriptor createStructDescriptor()
    throws SQLException
  {
    return new StructDescriptor(this, this.connection);
  }

  STRUCT createObjSTRUCT(StructDescriptor paramStructDescriptor, Object[] paramArrayOfObject)
    throws SQLException
  {
    if ((this.statusBits & 0x10) != 0) {
      return new JAVA_STRUCT(paramStructDescriptor, this.connection, paramArrayOfObject);
    }
    return new STRUCT(paramStructDescriptor, this.connection, paramArrayOfObject);
  }

  STRUCT createByteSTRUCT(StructDescriptor paramStructDescriptor, byte[] paramArrayOfByte)
    throws SQLException
  {
    if ((this.statusBits & 0x10) != 0) {
      return new JAVA_STRUCT(paramStructDescriptor, paramArrayOfByte, this.connection);
    }
    return new STRUCT(paramStructDescriptor, paramArrayOfByte, this.connection);
  }

  public static String getSubtypeName(Connection paramConnection, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    PickleContext localPickleContext = new PickleContext(paramArrayOfByte, paramLong);
    byte b = localPickleContext.readByte();

    if ((!PickleContext.is81format(b)) || (PickleContext.isCollectionImage_pctx(b)) || (!PickleContext.hasPrefix(b)))
    {
      return null;
    }

    if (!localPickleContext.readAndCheckVersion()) {
      DatabaseError.throwSqlException(1, "Image version is not recognized");
    }

    localPickleContext.skipLength();

    localPickleContext.skipLength();

    b = localPickleContext.readByte();

    if ((b & 0x4) != 0)
    {
      byte[] arrayOfByte = localPickleContext.readBytes(16);

      return toid2typename(paramConnection, arrayOfByte);
    }

    return null;
  }

  public static String toid2typename(Connection paramConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    String str = (String)((OracleConnection)paramConnection).getDescriptor(paramArrayOfByte);

    if (str == null)
    {
      PreparedStatement localPreparedStatement = null;
      ResultSet localResultSet = null;
      try
      {
        localPreparedStatement = paramConnection.prepareStatement("select owner, type_name from all_types where type_oid = :1");

        localPreparedStatement.setBytes(1, paramArrayOfByte);

        localResultSet = localPreparedStatement.executeQuery();

        if (localResultSet.next())
        {
          str = localResultSet.getString(1) + "." + localResultSet.getString(2);

          ((OracleConnection)paramConnection).putDescriptor(paramArrayOfByte, str);
        }
        else {
          DatabaseError.throwSqlException(1, "Invalid type oid");
        }
      }
      finally
      {
        if (localResultSet != null) {
          localResultSet.close();
        }
        if (localPreparedStatement != null) {
          localPreparedStatement.close();
        }
      }
    }

    return str;
  }
  public int getTdsVersion() {
    return this.tdsVersion;
  }

  public void printDebug()
  {
  }

  private String debugText()
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);

    localPrintWriter.println("OracleTypeADT = " + this);
    localPrintWriter.println("sqlName = " + this.sqlName);

    localPrintWriter.println("OracleType[] : ");

    if (this.attrTypes != null)
    {
      for (int i = 0; i < this.attrTypes.length; i++) {
        localPrintWriter.println("[" + i + "] = " + this.attrTypes[i]);
      }
    }
    localPrintWriter.println("null");

    localPrintWriter.println("LDS : ");

    if (this.lds != null)
      printUnsignedByteArray(this.lds, localPrintWriter);
    else {
      localPrintWriter.println("null");
    }
    localPrintWriter.println("toid : ");

    if (this.toid != null)
      printUnsignedByteArray(this.toid, localPrintWriter);
    else {
      localPrintWriter.println("null");
    }
    localPrintWriter.println("fdo : ");

    if (this.fdo != null)
      printUnsignedByteArray(this.fdo, localPrintWriter);
    else {
      localPrintWriter.println("null");
    }
    localPrintWriter.println("tds version : " + this.tdsVersion);
    localPrintWriter.println("type version : " + this.typeVersion);
    localPrintWriter.println("type version : " + this.typeVersion);
    localPrintWriter.println("bigEndian : " + (this.bigEndian ? "true" : "false"));
    localPrintWriter.println("opcode : " + this.opcode);

    localPrintWriter.println("tdoCState : " + this.tdoCState);

    return localStringWriter.toString();
  }

  public byte[] getTOID()
  {
    try
    {
      if (this.toid == null)
      {
        initMetadata(this.connection);
      }
    }
    catch (SQLException localSQLException) {
    }
    return this.toid;
  }

  public int getImageFormatVersion()
  {
    return PickleContext.KOPI20_VERSION;
  }

  public int getTypeVersion()
  {
    try
    {
      if (this.typeVersion == -1)
      {
        initMetadata(this.connection);
      }
    } catch (SQLException localSQLException) {
    }
    return this.typeVersion;
  }

  public int getCharSet()
  {
    return this.charSetId;
  }

  public int getCharSetForm()
  {
    return this.charSetForm;
  }

  public synchronized long getTdoCState()
  {
    try
    {
      if (this.tdoCState == 0L)
      {
        getFullName();
        this.tdoCState = this.connection.getTdoCState(this.sqlName.getSchema(), this.sqlName.getSimpleName());
      }
    }
    catch (SQLException localSQLException) {
    }
    return this.tdoCState;
  }

  public long getFIXED_DATA_SIZE()
  {
    try
    {
      return getFixedDataSize();
    }
    catch (SQLException localSQLException) {
    }
    return 0L;
  }

  public long[] getLDS_OFFSET_ARRAY()
  {
    try
    {
      return getLdsOffsetArray();
    }
    catch (SQLException localSQLException) {
    }
    return null;
  }

  synchronized long[] getLdsOffsetArray() throws SQLException
  {
    if ((this.ldsOffsetArray == null) && (this.connection != null))
    {
      generateLDS();
    }
    return this.ldsOffsetArray;
  }

  public long getFixedDataSize() throws SQLException
  {
    return this.fixedDataSize;
  }

  public int getAlignmentReq() throws SQLException
  {
    return this.alignmentRequirement;
  }

  public int getNumAttrs() throws SQLException
  {
    if ((this.attrTypes == null) && (this.connection != null)) {
      init(this.connection);
    }
    return this.attrTypes.length;
  }

  public OracleType getAttrTypeAt(int paramInt) throws SQLException
  {
    if ((this.attrTypes == null) && (this.connection != null)) {
      init(this.connection);
    }
    return this.attrTypes[paramInt];
  }

  public boolean isEmbeddedADT() throws SQLException
  {
    return (this.statusBits & 0x2) != 0;
  }

  public boolean isUptADT() throws SQLException
  {
    return (this.statusBits & 0x4) != 0;
  }

  public boolean isTopADT() throws SQLException
  {
    return (this.statusBits & 0x1) != 0;
  }

  public void setStatus(int paramInt) throws SQLException
  {
    this.statusBits = paramInt;
  }

  void setEmbeddedADT() throws SQLException
  {
    maskAndSetStatusBits(-16, 2);
  }

  void setUptADT() throws SQLException
  {
    maskAndSetStatusBits(-16, 4);
  }

  public boolean isSubType() throws SQLException
  {
    return (this.statusBits & 0x40) != 0;
  }

  public boolean isFinalType()
    throws SQLException
  {
    return ((this.statusBits & 0x20) != 0 ? 1 : 0) | ((this.statusBits & 0x2) != 0 ? 1 : 0);
  }

  public boolean isJavaObject() throws SQLException
  {
    return (this.statusBits & 0x10) != 0;
  }

  public int getStatus()
    throws SQLException
  {
    if (((this.statusBits & 0x1) != 0) && ((this.statusBits & 0x100) == 0))
      init(this.connection);
    return this.statusBits;
  }

  public static OracleTypeADT shallowClone(OracleTypeADT paramOracleTypeADT)
    throws SQLException
  {
    OracleTypeADT localOracleTypeADT = new OracleTypeADT();
    shallowCopy(paramOracleTypeADT, localOracleTypeADT);
    return localOracleTypeADT;
  }

  public static void shallowCopy(OracleTypeADT paramOracleTypeADT1, OracleTypeADT paramOracleTypeADT2)
    throws SQLException
  {
    paramOracleTypeADT2.connection = paramOracleTypeADT1.connection;
    paramOracleTypeADT2.sqlName = paramOracleTypeADT1.sqlName;
    paramOracleTypeADT2.parent = paramOracleTypeADT1.parent;
    paramOracleTypeADT2.idx = paramOracleTypeADT1.idx;
    paramOracleTypeADT2.descriptor = paramOracleTypeADT1.descriptor;
    paramOracleTypeADT2.statusBits = paramOracleTypeADT1.statusBits;

    paramOracleTypeADT2.nullOffset = paramOracleTypeADT1.nullOffset;
    paramOracleTypeADT2.ldsOffset = paramOracleTypeADT1.ldsOffset;
    paramOracleTypeADT2.sizeForLds = paramOracleTypeADT1.sizeForLds;
    paramOracleTypeADT2.alignForLds = paramOracleTypeADT1.alignForLds;
    paramOracleTypeADT2.typeCode = paramOracleTypeADT1.typeCode;
    paramOracleTypeADT2.dbTypeCode = paramOracleTypeADT1.dbTypeCode;
    paramOracleTypeADT2.tdsVersion = paramOracleTypeADT1.tdsVersion;
    paramOracleTypeADT2.typeVersion = paramOracleTypeADT1.typeVersion;
    paramOracleTypeADT2.lds = paramOracleTypeADT1.lds;
    paramOracleTypeADT2.ldsOffsetArray = paramOracleTypeADT1.ldsOffsetArray;
    paramOracleTypeADT2.fixedDataSize = paramOracleTypeADT1.fixedDataSize;
    paramOracleTypeADT2.alignmentRequirement = paramOracleTypeADT1.alignmentRequirement;
    paramOracleTypeADT2.attrTypes = paramOracleTypeADT1.attrTypes;
    paramOracleTypeADT2.sqlName = paramOracleTypeADT1.sqlName;
    paramOracleTypeADT2.tdoCState = paramOracleTypeADT1.tdoCState;
    paramOracleTypeADT2.toid = paramOracleTypeADT1.toid;
    paramOracleTypeADT2.fdo = paramOracleTypeADT1.fdo;
    paramOracleTypeADT2.charSetId = paramOracleTypeADT1.charSetId;
    paramOracleTypeADT2.charSetForm = paramOracleTypeADT1.charSetForm;
    paramOracleTypeADT2.bigEndian = paramOracleTypeADT1.bigEndian;
    paramOracleTypeADT2.flattenedAttrNum = paramOracleTypeADT1.flattenedAttrNum;
    paramOracleTypeADT2.statusBits = paramOracleTypeADT1.statusBits;
    paramOracleTypeADT2.attrNames = paramOracleTypeADT1.attrNames;
    paramOracleTypeADT2.attrTypeNames = paramOracleTypeADT1.attrTypeNames;
    paramOracleTypeADT2.opcode = paramOracleTypeADT1.opcode;
    paramOracleTypeADT2.idx = paramOracleTypeADT1.idx;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.statusBits);
    paramObjectOutputStream.writeInt(this.tdsVersion);
    paramObjectOutputStream.writeInt(this.typeVersion);
    paramObjectOutputStream.writeObject(this.lds);
    paramObjectOutputStream.writeObject(this.ldsOffsetArray);
    paramObjectOutputStream.writeLong(this.fixedDataSize);
    paramObjectOutputStream.writeInt(this.alignmentRequirement);
    paramObjectOutputStream.writeObject(this.attrTypes);
    paramObjectOutputStream.writeObject(this.attrNames);
    paramObjectOutputStream.writeObject(this.attrTypeNames);
    paramObjectOutputStream.writeLong(this.tdoCState);
    paramObjectOutputStream.writeObject(this.toid);
    paramObjectOutputStream.writeObject(this.fdo);
    paramObjectOutputStream.writeInt(this.charSetId);
    paramObjectOutputStream.writeInt(this.charSetForm);
    paramObjectOutputStream.writeBoolean(this.bigEndian);
    paramObjectOutputStream.writeInt(this.flattenedAttrNum);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.statusBits = paramObjectInputStream.readInt();
    this.tdsVersion = paramObjectInputStream.readInt();
    this.typeVersion = paramObjectInputStream.readInt();
    paramObjectInputStream.readObject();
    paramObjectInputStream.readObject();
    paramObjectInputStream.readLong();
    paramObjectInputStream.readInt();
    this.attrTypes = ((OracleType[])paramObjectInputStream.readObject());
    this.attrNames = ((String[])paramObjectInputStream.readObject());
    this.attrTypeNames = ((String[])paramObjectInputStream.readObject());
    paramObjectInputStream.readLong();
    this.toid = ((byte[])paramObjectInputStream.readObject());
    this.fdo = ((byte[])paramObjectInputStream.readObject());
    this.charSetId = paramObjectInputStream.readInt();
    this.charSetForm = paramObjectInputStream.readInt();
    this.bigEndian = paramObjectInputStream.readBoolean();
    this.flattenedAttrNum = paramObjectInputStream.readInt();
  }

  public synchronized void setConnection(OracleConnection paramOracleConnection) throws SQLException
  {
    this.connection = paramOracleConnection;
    for (int i = 0; i < this.attrTypes.length; i++)
      this.attrTypes[i].setConnection(this.connection);
  }

  private synchronized void setStatusBits(int paramInt)
  {
    this.statusBits |= paramInt;
  }

  private synchronized void maskAndSetStatusBits(int paramInt1, int paramInt2)
  {
    this.statusBits &= paramInt1;
    this.statusBits |= paramInt2;
  }

  private void printUnsignedByteArray(byte[] paramArrayOfByte, PrintWriter paramPrintWriter)
  {
    int i = paramArrayOfByte.length;

    int[] arrayOfInt = Util.toJavaUnsignedBytes(paramArrayOfByte);

    for (int j = 0; j < i; j++)
    {
      paramPrintWriter.print("0x" + Integer.toHexString(arrayOfInt[j]) + " ");
    }

    paramPrintWriter.println();

    for (j = 0; j < i; j++)
    {
      paramPrintWriter.print(arrayOfInt[j] + " ");
    }

    paramPrintWriter.println();
  }

  public void initChildNamesRecursively(Map paramMap)
    throws SQLException
  {
    TypeTreeElement localTypeTreeElement = (TypeTreeElement)paramMap.get(this.sqlName);

    if ((this.attrTypes != null) && (this.attrTypes.length > 0))
    {
      for (int i = 0; i < this.attrTypes.length; i++)
      {
        OracleType localOracleType = this.attrTypes[i];
        localOracleType.setNames(localTypeTreeElement.getChildSchemaName(i + 1), localTypeTreeElement.getChildTypeName(i + 1));
        localOracleType.initChildNamesRecursively(paramMap);
        localOracleType.cacheDescriptor();
      }
    }
  }

  public void cacheDescriptor() throws SQLException
  {
    this.descriptor = StructDescriptor.createDescriptor(this);
  }

  public void printXML(PrintWriter paramPrintWriter, int paramInt)
    throws SQLException
  {
    for (int i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.print("<OracleTypeADT sqlName=\"" + this.sqlName + "\" " + " hashCode=\"" + hashCode() + "\" " + " toid=\"");

    if (this.toid != null)
      printUnsignedByteArray(this.toid, paramPrintWriter);
    else {
      paramPrintWriter.print("null");
    }
    paramPrintWriter.println(" \"  typecode=\"" + this.typeCode + "\"" + " tds version=\"" + this.tdsVersion + "\"" + " is embedded=\"" + isEmbeddedADT() + "\"" + " is top level=\"" + isTopADT() + "\"" + " is upt=\"" + isUptADT() + "\"" + " finalType=\"" + isFinalType() + "\"" + " subtype=\"" + isSubType() + "\"" + " ldsOffset=\"" + this.ldsOffset + "\"" + " sizeForLds=\"" + this.sizeForLds + "\"" + " alignForLds=\"" + this.alignForLds + "\"" + " ldsOffsetArray size=\"" + (this.ldsOffsetArray == null ? "null" : Integer.toString(this.ldsOffsetArray.length)) + "\"" + ">");

    if ((this.attrTypes != null) && (this.attrTypes.length > 0))
      for (i = 0; i < this.attrTypes.length; i++)
        this.attrTypes[i].printXML(paramPrintWriter, paramInt + 1);
    for (i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("</OracleTypeADT>");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeADT
 * JD-Core Version:    0.6.0
 */