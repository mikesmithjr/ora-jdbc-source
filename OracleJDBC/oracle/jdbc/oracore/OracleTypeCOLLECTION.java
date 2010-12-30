package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.Datum;
import oracle.sql.SQLName;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

public class OracleTypeCOLLECTION extends OracleTypeADT
  implements Serializable
{
  static final long serialVersionUID = -7279638692691669378L;
  public static final int TYPE_PLSQL_INDEX_TABLE = 1;
  public static final int TYPE_NESTED_TABLE = 2;
  public static final int TYPE_VARRAY = 3;
  int userCode = 0;
  long maxSize = 0L;
  OracleType elementType = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public OracleTypeCOLLECTION(String paramString, OracleConnection paramOracleConnection)
    throws SQLException
  {
    super(paramString, paramOracleConnection);
  }

  public OracleTypeCOLLECTION(OracleTypeADT paramOracleTypeADT, int paramInt, OracleConnection paramOracleConnection)
    throws SQLException
  {
    super(paramOracleTypeADT, paramInt, paramOracleConnection);
  }

  public OracleTypeCOLLECTION(SQLName paramSQLName, byte[] paramArrayOfByte1, int paramInt, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, OracleConnection paramOracleConnection, byte[] paramArrayOfByte4)
    throws SQLException
  {
    super(paramSQLName, paramArrayOfByte1, paramInt, paramArrayOfByte2, paramArrayOfByte3, paramOracleConnection, paramArrayOfByte4);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    if (paramObject != null)
    {
      if ((paramObject instanceof ARRAY)) {
        return (ARRAY)paramObject;
      }

      ArrayDescriptor localArrayDescriptor = createArrayDescriptor();

      return new ARRAY(localArrayDescriptor, this.connection, paramObject);
    }

    return null;
  }

  public int getTypeCode()
  {
    return 2003;
  }

  public boolean isInHierarchyOf(OracleType paramOracleType)
    throws SQLException
  {
    if (paramOracleType == null) {
      return false;
    }
    if (paramOracleType == this) {
      return true;
    }
    if (paramOracleType.getClass() != getClass()) {
      return false;
    }
    return paramOracleType.getTypeDescriptor().getName().equals(this.descriptor.getName());
  }

  public boolean isInHierarchyOf(StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    return false;
  }

  public boolean isObjectType()
  {
    return false;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    long l = paramTDSReader.readLong();

    this.maxSize = paramTDSReader.readLong();

    this.userCode = paramTDSReader.readByte();

    paramTDSReader.addSimplePatch(l, this);
  }

  public Datum unlinearize(byte[] paramArrayOfByte, long paramLong, Datum paramDatum, int paramInt, Map paramMap)
    throws SQLException
  {
    return unlinearize(paramArrayOfByte, paramLong, paramDatum, 1L, -1, paramInt, paramMap);
  }

  public Datum unlinearize(byte[] paramArrayOfByte, long paramLong1, Datum paramDatum, long paramLong2, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    OracleConnection localOracleConnection = getConnection();
    Datum localDatum = null;

    if (localOracleConnection == null)
    {
      localDatum = unlinearizeInternal(paramArrayOfByte, paramLong1, paramDatum, paramLong2, paramInt1, paramInt2, paramMap);
    }
    else
    {
      synchronized (localOracleConnection)
      {
        localDatum = unlinearizeInternal(paramArrayOfByte, paramLong1, paramDatum, paramLong2, paramInt1, paramInt2, paramMap);
      }

    }

    return localDatum;
  }

  public synchronized Datum unlinearizeInternal(byte[] paramArrayOfByte, long paramLong1, Datum paramDatum, long paramLong2, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    if ((paramArrayOfByte[0] & 0x80) > 0)
    {
      localObject = new PickleContext(paramArrayOfByte, paramLong1);

      return unpickle81((PickleContext)localObject, (ARRAY)paramDatum, paramLong2, paramInt1, 1, paramInt2, paramMap);
    }

    Object localObject = new UnpickleContext(paramArrayOfByte, (int)paramLong1, null, null, this.bigEndian);

    return (Datum)unpickle80((UnpickleContext)localObject, (ARRAY)paramDatum, paramLong2, paramInt1, 1, paramInt2, paramMap);
  }

  public synchronized boolean isInlineImage(byte[] paramArrayOfByte, int paramInt)
    throws SQLException
  {
    if (paramArrayOfByte == null) {
      return false;
    }
    if ((paramArrayOfByte[paramInt] & 0x80) > 0)
    {
      if (PickleContext.isCollectionImage_pctx(paramArrayOfByte[paramInt]))
        return true;
      if (PickleContext.isDegenerateImage_pctx(paramArrayOfByte[paramInt])) {
        return false;
      }
      DatabaseError.throwSqlException(1, "Image is not a collection image");

      return false;
    }

    if ((paramArrayOfByte[(paramInt + 0)] == 0) && (paramArrayOfByte[(paramInt + 1)] == 0) && (paramArrayOfByte[(paramInt + 2)] == 0) && (paramArrayOfByte[(paramInt + 3)] == 0))
    {
      return false;
    }
    return (paramArrayOfByte[(paramInt + 10)] & 0x1) == 1;
  }

  protected ARRAY unpickle80(UnpickleContext paramUnpickleContext, ARRAY paramARRAY, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return unpickle80(paramUnpickleContext, paramARRAY, 1L, -1, paramInt1, paramInt2, paramMap);
  }

  protected ARRAY unpickle80(UnpickleContext paramUnpickleContext, ARRAY paramARRAY, long paramLong, int paramInt1, int paramInt2, int paramInt3, Map paramMap)
    throws SQLException
  {
    ARRAY localARRAY = paramARRAY;

    if (paramInt2 == 3)
    {
      if (localARRAY == null)
      {
        ArrayDescriptor localArrayDescriptor1 = createArrayDescriptor();

        localARRAY = new ARRAY(localArrayDescriptor1, (byte[])null, this.connection);
      }

      localARRAY.setImage(paramUnpickleContext.image(), paramUnpickleContext.absoluteOffset(), 0L);
    }

    long l1 = paramUnpickleContext.readLong();

    if (l1 == 0L)
    {
      return null;
    }
    if (paramInt2 == 9)
    {
      paramUnpickleContext.skipBytes((int)l1);

      return localARRAY;
    }
    if (paramInt2 == 3)
    {
      localARRAY.setImageLength(l1 + 4L);
      paramUnpickleContext.skipBytes((int)l1);

      return localARRAY;
    }

    if (localARRAY == null)
    {
      ArrayDescriptor localArrayDescriptor2 = createArrayDescriptor();

      localARRAY = new ARRAY(localArrayDescriptor2, (byte[])null, this.connection);
    }

    paramUnpickleContext.skipBytes(2);

    long l2 = paramUnpickleContext.readLong();
    int i = paramUnpickleContext.readByte();

    localARRAY.setLocator(paramUnpickleContext.readBytes((int)l2 - 1));

    int j = (i & 0x1) == 1 ? 1 : 0;

    if (j != 0)
    {
      long l3 = paramUnpickleContext.readLong() + paramUnpickleContext.offset();

      UnpickleContext localUnpickleContext = new UnpickleContext(paramUnpickleContext.image(), paramUnpickleContext.absoluteOffset(), null, null, this.bigEndian);

      if ((paramLong == 1L) && (paramInt1 == -1)) {
        localARRAY = (ARRAY)unpickle80rec(localUnpickleContext, localARRAY, paramInt3, paramMap);
      }
      else {
        localARRAY = (ARRAY)unpickle80rec(localUnpickleContext, localARRAY, paramLong, paramInt1, paramInt3, paramMap);
      }

      paramUnpickleContext.skipTo(l3);
    }

    return localARRAY;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return unpickle80rec(paramUnpickleContext, (ARRAY)null, paramInt2, paramMap);
  }

  private Object unpickle80rec(UnpickleContext paramUnpickleContext, ARRAY paramARRAY, int paramInt, Map paramMap)
    throws SQLException
  {
    ARRAY localARRAY = paramARRAY;

    if (localARRAY == null)
    {
      ArrayDescriptor localArrayDescriptor = createArrayDescriptor();

      localARRAY = new ARRAY(localArrayDescriptor, (byte[])null, this.connection);
    }

    int i = (int)paramUnpickleContext.readLong();

    localARRAY.setLength(i);

    if (paramInt == 0) {
      return localARRAY;
    }

    int j = paramUnpickleContext.readByte();

    if ((j != 0) && (j != 2)) {
      DatabaseError.throwSqlException(23, "collection flag = " + j);
    }

    int k = j == 0 ? 2 : 3;

    unpickle80rec_elems(paramUnpickleContext, localARRAY, 1, i, paramInt, paramMap, k);

    return localARRAY;
  }

  private Object unpickle80rec(UnpickleContext paramUnpickleContext, ARRAY paramARRAY, long paramLong, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    ARRAY localARRAY = paramARRAY;

    if (localARRAY == null)
    {
      ArrayDescriptor localArrayDescriptor = createArrayDescriptor();

      localARRAY = new ARRAY(localArrayDescriptor, (byte[])null, this.connection);
    }

    long l1 = paramUnpickleContext.readLong();

    localARRAY.setLength((int)l1);

    int i = paramUnpickleContext.readByte();

    if ((i != 0) && (i != 2)) {
      DatabaseError.throwSqlException(23, "collection flag = " + i);
    }

    int j = i == 0 ? 2 : 3;

    int k = (int)getAccessLength(l1, paramLong, paramInt1);
    int m = ArrayDescriptor.getCacheStyle(localARRAY) == 1 ? 1 : 0;

    if ((paramLong > 1L) && (k > 0))
    {
      if (((this.elementType instanceof OracleTypeNUMBER)) || ((this.elementType instanceof OracleTypeFLOAT)) || ((this.elementType instanceof OracleTypeSINT32)));
      switch (j)
      {
      case 2:
        paramUnpickleContext.skipBytes(23 * ((int)paramLong - 1));

        break;
      case 3:
        paramUnpickleContext.skipBytes(22 * ((int)paramLong - 1));

        break;
      default:
        DatabaseError.throwSqlException(1); break;

        long l2 = localARRAY.getLastIndex();
        long l3;
        if (l2 < paramLong)
        {
          if (l2 > 0L)
            paramUnpickleContext.skipTo(localARRAY.getLastOffset());
          else {
            l2 = 1L;
          }
          if (m != 0)
          {
            for (l3 = l2; l3 < paramLong; l3 += 1L)
            {
              localARRAY.setIndexOffset(l3, paramUnpickleContext.offset());
              this.elementType.unpickle80rec(paramUnpickleContext, j, 9, null);
            }

          }

          for (l3 = l2; l3 < paramLong; l3 += 1L) {
            this.elementType.unpickle80rec(paramUnpickleContext, j, 9, null);
          }
        }
        if (l2 > paramLong)
        {
          l3 = localARRAY.getOffset(paramLong);

          if (l3 != -1L)
          {
            paramUnpickleContext.skipTo(l3);
          }
          else
          {
            if (m != 0)
            {
              for (long l4 = 1L; l4 < paramLong; l4 += 1L)
              {
                localARRAY.setIndexOffset(l4, paramUnpickleContext.offset());
                this.elementType.unpickle80rec(paramUnpickleContext, j, 9, null);
              }

            }

            for (int n = 1; n < paramLong; n++)
              this.elementType.unpickle80rec(paramUnpickleContext, j, 9, null);
          }
        }
        else
        {
          paramUnpickleContext.skipTo(localARRAY.getLastOffset());
        }
        localARRAY.setLastIndexOffset(paramLong, paramUnpickleContext.offset());
      }

    }

    unpickle80rec_elems(paramUnpickleContext, localARRAY, (int)paramLong, k, paramInt2, paramMap, j);

    return localARRAY;
  }

  private Object unpickle80rec_elems(UnpickleContext paramUnpickleContext, ARRAY paramARRAY, int paramInt1, int paramInt2, int paramInt3, Map paramMap, int paramInt4)
    throws SQLException
  {
    int i = ArrayDescriptor.getCacheStyle(paramARRAY) == 1 ? 1 : 0;
    Object localObject;
    int j;
    switch (paramInt3)
    {
    case 1:
      localObject = new Datum[paramInt2];

      if (i != 0)
      {
        for (j = 0; j < paramInt2; j++)
        {
          paramARRAY.setIndexOffset(paramInt1 + j, paramUnpickleContext.offset());

          localObject[j] = ((Datum)this.elementType.unpickle80rec(paramUnpickleContext, paramInt4, paramInt3, paramMap));
        }

      }

      for (j = 0; j < paramInt2; j++)
      {
        localObject[j] = ((Datum)this.elementType.unpickle80rec(paramUnpickleContext, paramInt4, paramInt3, paramMap));
      }

      paramARRAY.setDatumArray(localObject);

      break;
    case 2:
      localObject = ArrayDescriptor.makeJavaArray(paramInt2, this.elementType.getTypeCode());

      if (i != 0)
      {
        for (j = 0; j < paramInt2; j++)
        {
          paramARRAY.setIndexOffset(paramInt1 + j, paramUnpickleContext.offset());

          localObject[j] = this.elementType.unpickle80rec(paramUnpickleContext, paramInt4, paramInt3, paramMap);
        }

      }

      for (j = 0; j < paramInt2; j++) {
        localObject[j] = this.elementType.unpickle80rec(paramUnpickleContext, paramInt4, paramInt3, paramMap);
      }

      paramARRAY.setObjArray(localObject);

      break;
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
      if (((this.elementType instanceof OracleTypeNUMBER)) || ((this.elementType instanceof OracleTypeFLOAT)))
      {
        paramARRAY.setObjArray(OracleTypeNUMBER.unpickle80NativeArray(paramUnpickleContext, 1L, paramInt2, paramInt3, paramInt4));
      }
      else
      {
        DatabaseError.throwSqlException(23);
      }

      break;
    case 3:
    default:
      DatabaseError.throwSqlException(1);
    }

    paramARRAY.setLastIndexOffset(paramInt1 + paramInt2, paramUnpickleContext.offset());

    return paramARRAY;
  }

  protected int pickle81(PickleContext paramPickleContext, Datum paramDatum)
    throws SQLException
  {
    ARRAY localARRAY = (ARRAY)paramDatum;

    boolean bool = localARRAY.hasDataSeg();
    int i = 0;
    int j = paramPickleContext.offset() + 2;

    if (bool)
    {
      Datum[] arrayOfDatum = localARRAY.getOracleArray();

      if (this.userCode == 3)
      {
        if (arrayOfDatum.length > this.maxSize) {
          DatabaseError.throwSqlException(71, null);
        }
      }

      i += paramPickleContext.writeCollImageHeader(arrayOfDatum.length);

      for (int k = 0; k < arrayOfDatum.length; k++)
      {
        if (arrayOfDatum[k] == null)
          i += paramPickleContext.writeElementNull();
        else {
          i += this.elementType.pickle81(paramPickleContext, arrayOfDatum[k]);
        }

      }

    }

    i += paramPickleContext.writeCollImageHeader(localARRAY.getLocator());

    paramPickleContext.patchImageLen(j, i);

    return i;
  }

  ARRAY unpickle81(PickleContext paramPickleContext, ARRAY paramARRAY, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return unpickle81(paramPickleContext, paramARRAY, 1L, -1, paramInt1, paramInt2, paramMap);
  }

  ARRAY unpickle81(PickleContext paramPickleContext, ARRAY paramARRAY, long paramLong, int paramInt1, int paramInt2, int paramInt3, Map paramMap)
    throws SQLException
  {
    ARRAY localARRAY = paramARRAY;

    if (localARRAY == null)
    {
      ArrayDescriptor localArrayDescriptor = createArrayDescriptor();

      localARRAY = new ARRAY(localArrayDescriptor, (byte[])null, this.connection);
    }

    if (unpickle81ImgHeader(paramPickleContext, localARRAY, paramInt2, paramInt3))
    {
      if ((paramLong == 1L) && (paramInt1 == -1))
        unpickle81ImgBody(paramPickleContext, localARRAY, paramInt3, paramMap);
      else {
        unpickle81ImgBody(paramPickleContext, localARRAY, paramLong, paramInt1, paramInt3, paramMap);
      }
    }

    return localARRAY;
  }

  boolean unpickle81ImgHeader(PickleContext paramPickleContext, ARRAY paramARRAY, int paramInt1, int paramInt2)
    throws SQLException
  {
    int i = 1;

    if (paramInt1 == 3)
    {
      paramARRAY.setImage(paramPickleContext.image(), paramPickleContext.absoluteOffset(), 0L);
    }

    byte b = paramPickleContext.readByte();

    if (!PickleContext.is81format(b)) {
      DatabaseError.throwSqlException(1, "Image is not in 8.1 format");
    }

    if (!PickleContext.hasPrefix(b)) {
      DatabaseError.throwSqlException(1, "Image has no prefix segment");
    }

    if (PickleContext.isCollectionImage_pctx(b))
      i = 1;
    else if (PickleContext.isDegenerateImage_pctx(b))
      i = 0;
    else {
      DatabaseError.throwSqlException(1, "Image is not a collection image");
    }

    paramPickleContext.readByte();

    if (paramInt1 == 9)
    {
      paramPickleContext.skipBytes(paramPickleContext.readLength(true) - 2);

      return false;
    }
    if (paramInt1 == 3)
    {
      long l = paramPickleContext.readLength();

      paramARRAY.setImageLength(l);
      paramPickleContext.skipTo(paramARRAY.getImageOffset() + l);

      return false;
    }

    paramPickleContext.skipLength();

    int j = paramPickleContext.readLength();

    paramARRAY.setPrefixFlag(paramPickleContext.readByte());

    if (paramARRAY.isInline())
    {
      paramPickleContext.readDataValue(j - 1);
    }
    else
    {
      paramARRAY.setLocator(paramPickleContext.readDataValue(j - 1));
    }

    return paramARRAY.isInline();
  }

  void unpickle81ImgBody(PickleContext paramPickleContext, ARRAY paramARRAY, long paramLong, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    paramPickleContext.readByte();

    int i = paramPickleContext.readLength();

    paramARRAY.setLength(i);

    if (paramInt2 == 0) {
      return;
    }

    int j = (int)getAccessLength(i, paramLong, paramInt1);
    int k = ArrayDescriptor.getCacheStyle(paramARRAY) == 1 ? 1 : 0;

    if ((paramLong > 1L) && (j > 0))
    {
      long l1 = paramARRAY.getLastIndex();
      long l2;
      if (l1 < paramLong)
      {
        if (l1 > 0L)
          paramPickleContext.skipTo(paramARRAY.getLastOffset());
        else {
          l1 = 1L;
        }
        if (k != 0)
        {
          for (l2 = l1; l2 < paramLong; l2 += 1L)
          {
            paramARRAY.setIndexOffset(l2, paramPickleContext.offset());
            this.elementType.unpickle81rec(paramPickleContext, 9, null);
          }

        }

        for (l2 = l1; l2 < paramLong; l2 += 1L) {
          this.elementType.unpickle81rec(paramPickleContext, 9, null);
        }
      }
      if (l1 > paramLong)
      {
        l2 = paramARRAY.getOffset(paramLong);

        if (l2 != -1L)
        {
          paramPickleContext.skipTo(l2);
        }
        else
        {
          if (k != 0)
          {
            for (int m = 1; m < paramLong; m++)
            {
              paramARRAY.setIndexOffset(m, paramPickleContext.offset());
              this.elementType.unpickle81rec(paramPickleContext, 9, null);
            }

          }

          for (int n = 1; n < paramLong; n++)
            this.elementType.unpickle81rec(paramPickleContext, 9, null);
        }
      }
      else
      {
        paramPickleContext.skipTo(paramARRAY.getLastOffset());
      }
      paramARRAY.setLastIndexOffset(paramLong, paramPickleContext.offset());
    }

    unpickle81ImgBodyElements(paramPickleContext, paramARRAY, (int)paramLong, j, paramInt2, paramMap);
  }

  void unpickle81ImgBody(PickleContext paramPickleContext, ARRAY paramARRAY, int paramInt, Map paramMap)
    throws SQLException
  {
    paramPickleContext.readByte();

    int i = paramPickleContext.readLength();

    paramARRAY.setLength(i);

    if (paramInt == 0) {
      return;
    }

    unpickle81ImgBodyElements(paramPickleContext, paramARRAY, 1, i, paramInt, paramMap);
  }

  private void unpickle81ImgBodyElements(PickleContext paramPickleContext, ARRAY paramARRAY, int paramInt1, int paramInt2, int paramInt3, Map paramMap)
    throws SQLException
  {
    int i = ArrayDescriptor.getCacheStyle(paramARRAY) == 1 ? 1 : 0;
    Object localObject;
    int j;
    switch (paramInt3)
    {
    case 1:
      localObject = new Datum[paramInt2];

      if (i != 0)
      {
        for (j = 0; j < paramInt2; j++)
        {
          paramARRAY.setIndexOffset(paramInt1 + j, paramPickleContext.offset());

          localObject[j] = ((Datum)this.elementType.unpickle81rec(paramPickleContext, paramInt3, paramMap));
        }

      }

      for (j = 0; j < paramInt2; j++) {
        localObject[j] = ((Datum)this.elementType.unpickle81rec(paramPickleContext, paramInt3, paramMap));
      }

      paramARRAY.setDatumArray(localObject);

      break;
    case 2:
      localObject = ArrayDescriptor.makeJavaArray(paramInt2, this.elementType.getTypeCode());

      if (i != 0)
      {
        for (j = 0; j < paramInt2; j++)
        {
          paramARRAY.setIndexOffset(paramInt1 + j, paramPickleContext.offset());

          localObject[j] = this.elementType.unpickle81rec(paramPickleContext, paramInt3, paramMap);
        }

      }

      for (j = 0; j < paramInt2; j++) {
        localObject[j] = this.elementType.unpickle81rec(paramPickleContext, paramInt3, paramMap);
      }

      paramARRAY.setObjArray(localObject);

      break;
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
      if (((this.elementType instanceof OracleTypeNUMBER)) || ((this.elementType instanceof OracleTypeFLOAT)))
      {
        paramARRAY.setObjArray(OracleTypeNUMBER.unpickle81NativeArray(paramPickleContext, 1L, paramInt2, paramInt3));
      }
      else
      {
        DatabaseError.throwSqlException(23, "This feature is limited to numeric collection");
      }

      break;
    case 3:
    default:
      DatabaseError.throwSqlException(68, "Invalid conversion type " + this.elementType);
    }

    paramARRAY.setLastIndexOffset(paramInt1 + paramInt2, paramPickleContext.offset());
  }

  private synchronized void initCollElemTypeName()
    throws SQLException
  {
    Statement localStatement = this.connection.createStatement();
    ResultSet localResultSet = localStatement.executeQuery("SELECT ELEM_TYPE_NAME, ELEM_TYPE_OWNER FROM ALL_COLL_TYPES WHERE TYPE_NAME='" + this.sqlName.getSimpleName() + "' AND OWNER='" + this.sqlName.getSchema() + "'");

    if (localResultSet.next())
    {
      if (this.attrTypeNames == null) {
        this.attrTypeNames = new String[1];
      }
      this.attrTypeNames[0] = (localResultSet.getString(2) + "." + localResultSet.getString(1));
    }
    else {
      DatabaseError.throwSqlException(1);
    }
    localResultSet.close();
    localStatement.close();
  }

  public String getAttributeName(int paramInt) throws SQLException
  {
    DatabaseError.throwSqlException(1);

    return null;
  }

  public String getAttributeName(int paramInt, boolean paramBoolean) throws SQLException
  {
    return getAttributeName(paramInt);
  }

  public String getAttributeType(int paramInt)
    throws SQLException
  {
    if (paramInt != 1) {
      DatabaseError.throwSqlException(68);
    }
    if (this.sqlName == null) {
      getFullName();
    }
    if (this.attrTypeNames == null) {
      initCollElemTypeName();
    }
    return this.attrTypeNames[0];
  }

  public String getAttributeType(int paramInt, boolean paramBoolean) throws SQLException
  {
    if (paramBoolean) {
      return getAttributeType(paramInt);
    }

    if (paramInt != 1) {
      DatabaseError.throwSqlException(68);
    }
    if ((this.sqlName != null) && (this.attrTypeNames != null)) {
      return this.attrTypeNames[0];
    }
    return null;
  }

  public int getNumAttrs()
    throws SQLException
  {
    return 0;
  }

  public OracleType getAttrTypeAt(int paramInt) throws SQLException
  {
    return null;
  }

  ArrayDescriptor createArrayDescriptor() throws SQLException
  {
    return new ArrayDescriptor(this, this.connection);
  }

  ArrayDescriptor createArrayDescriptorWithItsOwnTree() throws SQLException
  {
    if (this.descriptor == null)
    {
      if ((this.sqlName == null) && (getFullName(false) == null))
      {
        this.descriptor = new ArrayDescriptor(this, this.connection);
      }
      else
      {
        this.descriptor = ArrayDescriptor.createDescriptor(this.sqlName, this.connection);
      }
    }

    return (ArrayDescriptor)this.descriptor;
  }

  public OracleType getElementType() throws SQLException
  {
    return this.elementType;
  }

  public int getUserCode() throws SQLException
  {
    return this.userCode;
  }

  public long getMaxLength() throws SQLException
  {
    return this.maxSize;
  }

  private long getAccessLength(long paramLong1, long paramLong2, int paramInt)
    throws SQLException
  {
    if (paramLong2 > paramLong1) {
      return 0L;
    }
    if (paramInt < 0)
    {
      return paramLong1 - paramLong2 + 1L;
    }

    return Math.min(paramLong1 - paramLong2 + 1L, paramInt);
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.userCode);
    paramObjectOutputStream.writeLong(this.maxSize);
    paramObjectOutputStream.writeObject(this.elementType);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.userCode = paramObjectInputStream.readInt();
    this.maxSize = paramObjectInputStream.readLong();
    this.elementType = ((OracleType)paramObjectInputStream.readObject());
  }

  public void setConnection(OracleConnection paramOracleConnection) throws SQLException
  {
    this.connection = paramOracleConnection;

    this.elementType.setConnection(paramOracleConnection);
  }

  public void initMetadataRecursively() throws SQLException
  {
    initMetadata(this.connection);
    if (this.elementType != null) this.elementType.initMetadataRecursively(); 
  }

  public void initChildNamesRecursively(Map paramMap)
    throws SQLException
  {
    TypeTreeElement localTypeTreeElement = (TypeTreeElement)paramMap.get(this.sqlName);

    if (this.elementType != null)
    {
      this.elementType.setNames(localTypeTreeElement.getChildSchemaName(0), localTypeTreeElement.getChildTypeName(0));
      this.elementType.initChildNamesRecursively(paramMap);
      this.elementType.cacheDescriptor();
    }
  }

  public void cacheDescriptor() throws SQLException
  {
    this.descriptor = ArrayDescriptor.createDescriptor(this);
  }

  public void printXML(PrintWriter paramPrintWriter, int paramInt)
    throws SQLException
  {
    for (int i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("<OracleTypeCOLLECTION sqlName=\"" + this.sqlName + "\" " + " toid=\"" + this.toid + "\" " + ">");

    if (this.elementType != null)
      this.elementType.printXML(paramPrintWriter, paramInt + 1);
    for (i = 0; i < paramInt; i++) paramPrintWriter.print("  ");
    paramPrintWriter.println("</OracleTypeCOLLECTION>");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeCOLLECTION
 * JD-Core Version:    0.6.0
 */