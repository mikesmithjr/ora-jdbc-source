package oracle.sql;

import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;

public class ARRAY extends DatumWithConnection
  implements Array
{
  static final byte KOPUP_INLINE_COLL = 1;
  ArrayDescriptor descriptor;
  Object objArray;
  Datum[] datumArray;
  byte[] locator;
  byte prefixFlag;
  byte[] prefixSegment;
  int numElems = -1;

  boolean enableBuffering = false;
  boolean enableIndexing = false;
  public static final int ACCESS_FORWARD = 1;
  public static final int ACCESS_REVERSE = 2;
  public static final int ACCESS_UNKNOWN = 3;
  int accessDirection = 3;
  long lastIndex;
  long lastOffset;
  long[] indexArray;
  long imageOffset;
  long imageLength;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:46_PDT_2005";

  public ARRAY(ArrayDescriptor paramArrayDescriptor, Connection paramConnection, Object paramObject)
    throws SQLException
  {
    assertNotNull(paramArrayDescriptor);

    this.descriptor = paramArrayDescriptor;

    assertNotNull(paramConnection);

    if (!paramArrayDescriptor.getInternalConnection().isDescriptorSharable(((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin()))
    {
      throw new SQLException("Cannot construct ARRAY instance, invalid connection");
    }

    paramArrayDescriptor.setConnection(paramConnection);
    setPhysicalConnectionOf(paramConnection);

    if (paramObject == null)
      this.datumArray = new Datum[0];
    else
      this.datumArray = this.descriptor.toOracleArray(paramObject, 1L, -1);
  }

  public ARRAY(ArrayDescriptor paramArrayDescriptor, byte[] paramArrayOfByte, Connection paramConnection)
    throws SQLException
  {
    super(paramArrayOfByte);

    assertNotNull(paramArrayDescriptor);

    this.descriptor = paramArrayDescriptor;

    assertNotNull(paramConnection);

    if (!paramArrayDescriptor.getInternalConnection().isDescriptorSharable(((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin()))
    {
      throw new SQLException("Cannot construct ARRAY instance, invalid connection");
    }

    paramArrayDescriptor.setConnection(paramConnection);
    setPhysicalConnectionOf(paramConnection);

    this.datumArray = null;
    this.locator = null;
  }

  public static ARRAY toARRAY(Object paramObject, oracle.jdbc.OracleConnection paramOracleConnection)
    throws SQLException
  {
    ARRAY localARRAY = null;

    if (paramObject != null) {
      if ((paramObject instanceof ARRAY))
      {
        localARRAY = (ARRAY)paramObject;
      }
      else if ((paramObject instanceof ORAData))
      {
        localARRAY = (ARRAY)((ORAData)paramObject).toDatum(paramOracleConnection);
      }
      else if ((paramObject instanceof CustomDatum))
      {
        localARRAY = (ARRAY)((oracle.jdbc.internal.OracleConnection)paramOracleConnection).toDatum((CustomDatum)paramObject);
      }
      else
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localARRAY;
  }

  public synchronized String getBaseTypeName()
    throws SQLException
  {
    String str = this.descriptor.getBaseName();

    return str;
  }

  public synchronized int getBaseType()
    throws SQLException
  {
    return this.descriptor.getBaseType();
  }

  public synchronized Object getArray()
    throws SQLException
  {
    return this.descriptor.toJavaArray(this, 1L, -1, getMap(), this.enableBuffering);
  }

  public synchronized Object getArray(Map paramMap)
    throws SQLException
  {
    return this.descriptor.toJavaArray(this, 1L, -1, paramMap, this.enableBuffering);
  }

  public synchronized Object getArray(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramLong < 1L) || (paramInt < 0))
    {
      DatabaseError.throwSqlException(68, "getArray()");
    }

    return this.descriptor.toJavaArray(this, paramLong, paramInt, getMap(), false);
  }

  public synchronized Object getArray(long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramLong < 1L) || (paramInt < 0))
    {
      DatabaseError.throwSqlException(68, "getArray()");
    }

    return this.descriptor.toJavaArray(this, paramLong, paramInt, paramMap, false);
  }

  public synchronized ResultSet getResultSet()
    throws SQLException
  {
    return getResultSet(getInternalConnection().getTypeMap());
  }

  public synchronized ResultSet getResultSet(Map paramMap)
    throws SQLException
  {
    return this.descriptor.toResultSet(this, 1L, -1, paramMap, this.enableBuffering);
  }

  public synchronized ResultSet getResultSet(long paramLong, int paramInt)
    throws SQLException
  {
    return getResultSet(paramLong, paramInt, getInternalConnection().getTypeMap());
  }

  public synchronized ResultSet getResultSet(long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramLong < 1L) || (paramInt < -1))
    {
      DatabaseError.throwSqlException(68, "getResultSet()");
    }

    return this.descriptor.toResultSet(this, paramLong, paramInt, paramMap, false);
  }

  public synchronized Datum[] getOracleArray()
    throws SQLException
  {
    return this.descriptor.toOracleArray(this, 1L, -1, this.enableBuffering);
  }

  public synchronized int length()
    throws SQLException
  {
    return this.descriptor.toLength(this);
  }

  public synchronized Datum[] getOracleArray(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramLong < 1L) || (paramInt < 0))
    {
      DatabaseError.throwSqlException(68, "getOracleArray()");
    }

    return this.descriptor.toOracleArray(this, paramLong, paramInt, false);
  }

  public synchronized String getSQLTypeName()
    throws SQLException
  {
    String str = null;

    if (this.descriptor != null)
    {
      str = this.descriptor.getName();
    }
    else
    {
      DatabaseError.throwSqlException(61, "ARRAY");
    }

    return str;
  }

  public Map getMap()
    throws SQLException
  {
    return getInternalConnection().getTypeMap();
  }

  public ArrayDescriptor getDescriptor()
    throws SQLException
  {
    return this.descriptor;
  }

  public synchronized byte[] toBytes()
    throws SQLException
  {
    return this.descriptor.toBytes(this, this.enableBuffering);
  }

  public synchronized void setDatumArray(Datum[] paramArrayOfDatum)
  {
    this.datumArray = paramArrayOfDatum;
  }

  public synchronized void setObjArray(Object paramObject)
    throws SQLException
  {
    if (paramObject == null)
    {
      DatabaseError.throwSqlException(1);
    }

    this.objArray = paramObject;
  }

  public synchronized void setLocator(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0))
      this.locator = paramArrayOfByte;
  }

  public synchronized void setPrefixSegment(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte != null) && (paramArrayOfByte.length != 0))
      this.prefixSegment = paramArrayOfByte;
  }

  public synchronized void setPrefixFlag(byte paramByte)
  {
    this.prefixFlag = paramByte;
  }

  public byte[] getLocator()
  {
    return this.locator;
  }

  public synchronized void setLength(int paramInt)
  {
    this.numElems = paramInt;
  }

  public boolean hasDataSeg()
  {
    return this.locator == null;
  }

  public boolean isInline()
  {
    return (this.prefixFlag & 0x1) == 1;
  }

  public Object toJdbc()
    throws SQLException
  {
    return this;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    return false;
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new Object[paramInt][];
  }

  public synchronized int[] getIntArray()
    throws SQLException
  {
    return (int[])this.descriptor.toNumericArray(this, 1L, -1, 4, this.enableBuffering);
  }

  public synchronized int[] getIntArray(long paramLong, int paramInt)
    throws SQLException
  {
    return (int[])this.descriptor.toNumericArray(this, paramLong, paramInt, 4, false);
  }

  public synchronized double[] getDoubleArray()
    throws SQLException
  {
    return (double[])this.descriptor.toNumericArray(this, 1L, -1, 5, this.enableBuffering);
  }

  public synchronized double[] getDoubleArray(long paramLong, int paramInt)
    throws SQLException
  {
    return (double[])this.descriptor.toNumericArray(this, paramLong, paramInt, 5, false);
  }

  public synchronized short[] getShortArray()
    throws SQLException
  {
    return (short[])this.descriptor.toNumericArray(this, 1L, -1, 8, this.enableBuffering);
  }

  public synchronized short[] getShortArray(long paramLong, int paramInt)
    throws SQLException
  {
    return (short[])this.descriptor.toNumericArray(this, paramLong, paramInt, 8, false);
  }

  public synchronized long[] getLongArray()
    throws SQLException
  {
    return (long[])this.descriptor.toNumericArray(this, 1L, -1, 7, this.enableBuffering);
  }

  public synchronized long[] getLongArray(long paramLong, int paramInt)
    throws SQLException
  {
    return (long[])this.descriptor.toNumericArray(this, paramLong, paramInt, 7, false);
  }

  public synchronized float[] getFloatArray()
    throws SQLException
  {
    return (float[])this.descriptor.toNumericArray(this, 1L, -1, 6, this.enableBuffering);
  }

  public synchronized float[] getFloatArray(long paramLong, int paramInt)
    throws SQLException
  {
    return (float[])this.descriptor.toNumericArray(this, paramLong, paramInt, 6, false);
  }

  public synchronized void setAutoBuffering(boolean paramBoolean)
    throws SQLException
  {
    this.enableBuffering = paramBoolean;
  }

  public boolean getAutoBuffering()
    throws SQLException
  {
    return this.enableBuffering;
  }

  public synchronized void setAutoIndexing(boolean paramBoolean, int paramInt)
    throws SQLException
  {
    this.enableIndexing = paramBoolean;
    this.accessDirection = paramInt;
  }

  public synchronized void setAutoIndexing(boolean paramBoolean)
    throws SQLException
  {
    this.enableIndexing = paramBoolean;
    this.accessDirection = 3;
  }

  public boolean getAutoIndexing()
    throws SQLException
  {
    return this.enableIndexing;
  }

  public int getAccessDirection()
    throws SQLException
  {
    return this.accessDirection;
  }

  public void setLastIndexOffset(long paramLong1, long paramLong2)
    throws SQLException
  {
    this.lastIndex = paramLong1;
    this.lastOffset = paramLong2;
  }

  public void setIndexOffset(long paramLong1, long paramLong2)
    throws SQLException
  {
    if (this.indexArray == null) {
      this.indexArray = new long[this.numElems];
    }
    this.indexArray[((int)paramLong1 - 1)] = paramLong2;
  }

  public long getLastIndex()
    throws SQLException
  {
    return this.lastIndex;
  }

  public long getLastOffset()
    throws SQLException
  {
    return this.lastOffset;
  }

  public long getOffset(long paramLong)
    throws SQLException
  {
    long l = -1L;

    if (this.indexArray != null) {
      l = this.indexArray[((int)paramLong - 1)];
    }

    return l;
  }

  public void setImage(byte[] paramArrayOfByte, long paramLong1, long paramLong2)
    throws SQLException
  {
    setShareBytes(paramArrayOfByte);

    this.imageOffset = paramLong1;
    this.imageLength = paramLong2;
  }

  public void setImageLength(long paramLong)
    throws SQLException
  {
    this.imageLength = paramLong;
  }

  public long getImageOffset()
  {
    return this.imageOffset;
  }

  public long getImageLength()
  {
    return this.imageLength;
  }

  public Connection getJavaSqlConnection() throws SQLException
  {
    return super.getJavaSqlConnection();
  }

  public String dump()
    throws SQLException
  {
    return STRUCT.dump(this);
  }

  static void dump(ARRAY paramARRAY, PrintWriter paramPrintWriter, int paramInt) throws SQLException
  {
    if (paramInt > 0) paramPrintWriter.println();

    ArrayDescriptor localArrayDescriptor = paramARRAY.getDescriptor();
    for (int j = 0; j < paramInt; j++) paramPrintWriter.print(' ');
    paramPrintWriter.println("name = " + localArrayDescriptor.getName());

    for (j = 0; j < paramInt; j++) paramPrintWriter.print(' ');
    paramPrintWriter.println("max length = " + localArrayDescriptor.getMaxLength());
    Object[] arrayOfObject = (Object[])paramARRAY.getArray();
    for (j = 0; j < paramInt; j++) paramPrintWriter.print(' ');
    int i;
    paramPrintWriter.println("length = " + (i = arrayOfObject.length));
    for (j = 0; j < i; j++)
    {
      for (int k = 0; k < paramInt; k++) paramPrintWriter.print(' ');
      paramPrintWriter.print("element[" + j + "] = ");
      STRUCT.dump(arrayOfObject[j], paramPrintWriter, paramInt + 4);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.ARRAY
 * JD-Core Version:    0.6.0
 */