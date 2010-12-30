package oracle.sql;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.Struct;
import java.util.Hashtable;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;

public class STRUCT extends DatumWithConnection
  implements Struct
{
  StructDescriptor descriptor;
  Datum[] datumArray;
  Object[] objectArray;
  boolean enableLocalCache = false;
  long imageOffset;
  long imageLength;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  public STRUCT(StructDescriptor paramStructDescriptor, Connection paramConnection, Object[] paramArrayOfObject)
    throws SQLException
  {
    assertNotNull(paramStructDescriptor);

    this.descriptor = paramStructDescriptor;

    assertNotNull(paramConnection);

    if (!paramStructDescriptor.getInternalConnection().isDescriptorSharable(((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin()))
    {
      throw new SQLException("Cannot construct STRUCT instance, invalid connection");
    }

    paramStructDescriptor.setConnection(paramConnection);

    if (!this.descriptor.isInstantiable())
    {
      throw new SQLException("Cannot construct STRUCT instance for a non-instantiable object type");
    }

    setPhysicalConnectionOf(paramConnection);

    if (paramArrayOfObject != null)
      this.datumArray = this.descriptor.toArray(paramArrayOfObject);
    else
      this.datumArray = new Datum[0];
  }

  public STRUCT(StructDescriptor paramStructDescriptor, Connection paramConnection, Map paramMap)
    throws SQLException
  {
    assertNotNull(paramStructDescriptor);

    this.descriptor = paramStructDescriptor;

    assertNotNull(paramConnection);

    if (!paramStructDescriptor.getInternalConnection().isDescriptorSharable(((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin()))
    {
      throw new SQLException("Cannot construct STRUCT instance, invalid connection");
    }

    paramStructDescriptor.setConnection(paramConnection);

    if (!this.descriptor.isInstantiable())
    {
      throw new SQLException("Cannot construct STRUCT instance for a non-instantiable object type");
    }

    setPhysicalConnectionOf(paramConnection);

    this.datumArray = this.descriptor.toOracleArray(paramMap);
  }

  public STRUCT(StructDescriptor paramStructDescriptor, byte[] paramArrayOfByte, Connection paramConnection)
    throws SQLException
  {
    super(paramArrayOfByte);

    assertNotNull(paramStructDescriptor);

    this.descriptor = paramStructDescriptor;

    assertNotNull(paramConnection);

    if (!paramStructDescriptor.getInternalConnection().isDescriptorSharable(((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin()))
    {
      throw new SQLException("Cannot construct STRUCT instance, invalid connection");
    }

    paramStructDescriptor.setConnection(paramConnection);
    setPhysicalConnectionOf(paramConnection);

    this.datumArray = null;
  }

  public synchronized String getSQLTypeName()
    throws SQLException
  {
    String str = this.descriptor.getName();

    return str;
  }

  public synchronized Object[] getAttributes()
    throws SQLException
  {
    Object[] arrayOfObject = getAttributes(getMap());

    return arrayOfObject;
  }

  public synchronized Object[] getAttributes(Map paramMap)
    throws SQLException
  {
    Object[] arrayOfObject = this.descriptor.toArray(this, paramMap, this.enableLocalCache);

    return arrayOfObject;
  }

  public synchronized StructDescriptor getDescriptor()
    throws SQLException
  {
    return this.descriptor;
  }

  public synchronized void setDescriptor(StructDescriptor paramStructDescriptor)
  {
    this.descriptor = paramStructDescriptor;
  }

  public synchronized Datum[] getOracleAttributes()
    throws SQLException
  {
    Datum[] arrayOfDatum = this.descriptor.toOracleArray(this, this.enableLocalCache);

    return arrayOfDatum;
  }

  public Map getMap()
  {
    Map localMap = null;
    try
    {
      localMap = getInternalConnection().getTypeMap();
    }
    catch (SQLException localSQLException)
    {
    }

    return localMap;
  }

  public synchronized byte[] toBytes()
    throws SQLException
  {
    byte[] arrayOfByte = this.descriptor.toBytes(this, this.enableLocalCache);

    return arrayOfByte;
  }

  public synchronized void setDatumArray(Datum[] paramArrayOfDatum)
  {
    this.datumArray = (paramArrayOfDatum == null ? new Datum[0] : paramArrayOfDatum);
  }

  public synchronized void setObjArray(Object[] paramArrayOfObject)
    throws SQLException
  {
    this.objectArray = (paramArrayOfObject == null ? new Object[0] : paramArrayOfObject);
  }

  public static STRUCT toSTRUCT(Object paramObject, oracle.jdbc.OracleConnection paramOracleConnection)
    throws SQLException
  {
    STRUCT localSTRUCT = null;

    if (paramObject != null) {
      if ((paramObject instanceof STRUCT))
      {
        localSTRUCT = (STRUCT)paramObject;
      }
      else if ((paramObject instanceof ORAData))
      {
        localSTRUCT = (STRUCT)((ORAData)paramObject).toDatum(paramOracleConnection);
      }
      else if ((paramObject instanceof CustomDatum))
      {
        localSTRUCT = (STRUCT)((oracle.jdbc.internal.OracleConnection)paramOracleConnection).toDatum((CustomDatum)paramObject);
      }
      else if ((paramObject instanceof SQLData))
      {
        SQLData localSQLData = (SQLData)paramObject;

        StructDescriptor localStructDescriptor = StructDescriptor.createDescriptor(localSQLData.getSQLTypeName(), paramOracleConnection);

        SQLOutput localSQLOutput = localStructDescriptor.toJdbc2SQLOutput();

        localSQLData.writeSQL(localSQLOutput);

        localSTRUCT = ((OracleSQLOutput)localSQLOutput).getSTRUCT();
      }
      else
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localSTRUCT;
  }

  public Object toJdbc()
    throws SQLException
  {
    Map localMap = getMap();
    Object localObject = toJdbc(localMap);

    return localObject;
  }

  public Object toJdbc(Map paramMap)
    throws SQLException
  {
    Object localObject = this;

    if (paramMap != null)
    {
      Class localClass = this.descriptor.getClass(paramMap);

      if (localClass != null) {
        localObject = toClass(localClass, paramMap);
      }

    }

    return localObject;
  }

  public Object toClass(Class paramClass)
    throws SQLException
  {
    Object localObject = toClass(paramClass, getMap());

    return localObject;
  }

  public Object toClass(Class paramClass, Map paramMap)
    throws SQLException
  {
    Object localObject1 = null;
    try
    {
      if ((paramClass == null) || (paramClass == STRUCT.class) || (paramClass == Struct.class))
      {
        localObject1 = this;
      }
      else
      {
        Object localObject2 = paramClass.newInstance();

        if ((localObject2 instanceof SQLData))
        {
          ((SQLData)localObject2).readSQL(this.descriptor.toJdbc2SQLInput(this, paramMap), this.descriptor.getName());

          localObject1 = localObject2;
        }
        else
        {
          Object localObject3;
          if ((localObject2 instanceof ORADataFactory))
          {
            localObject3 = (ORADataFactory)localObject2;

            localObject1 = ((ORADataFactory)localObject3).create(this, 2002);
          }
          else if ((localObject2 instanceof CustomDatumFactory))
          {
            localObject3 = (CustomDatumFactory)localObject2;

            localObject1 = ((CustomDatumFactory)localObject3).create(this, 2002);
          }
          else
          {
            DatabaseError.throwSqlException(49, this.descriptor.getName());
          }

        }

      }

    }
    catch (InstantiationException localInstantiationException)
    {
      DatabaseError.throwSqlException(49, "InstantiationException: " + localInstantiationException.getMessage());
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      DatabaseError.throwSqlException(49, "IllegalAccessException: " + localIllegalAccessException.getMessage());
    }

    return localObject1;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    return false;
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new Object[paramInt];
  }

  public synchronized void setAutoBuffering(boolean paramBoolean)
    throws SQLException
  {
    this.enableLocalCache = paramBoolean;
  }

  public synchronized boolean getAutoBuffering()
    throws SQLException
  {
    return this.enableLocalCache;
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

  public CustomDatumFactory getFactory(Hashtable paramHashtable, String paramString)
    throws SQLException
  {
    String str = getSQLTypeName();
    Object localObject = paramHashtable.get(str);

    if (localObject == null)
    {
      throw new SQLException("Unable to convert a \"" + str + "\" to a \"" + paramString + "\" or a subclass of \"" + paramString + "\"");
    }

    CustomDatumFactory localCustomDatumFactory = (CustomDatumFactory)localObject;

    return localCustomDatumFactory;
  }

  public ORADataFactory getORADataFactory(Hashtable paramHashtable, String paramString)
    throws SQLException
  {
    String str = getSQLTypeName();
    Object localObject = paramHashtable.get(str);

    if (localObject == null)
    {
      throw new SQLException("Unable to convert a \"" + str + "\" to a \"" + paramString + "\" or a subclass of \"" + paramString + "\"");
    }

    ORADataFactory localORADataFactory = (ORADataFactory)localObject;

    return localORADataFactory;
  }

  public String debugString()
  {
    StringWriter localStringWriter = new StringWriter();
    String str = null;
    try
    {
      StructDescriptor localStructDescriptor = getDescriptor();

      localStringWriter.write("name = " + localStructDescriptor.getName());
      int i;
      localStringWriter.write(" length = " + (i = localStructDescriptor.getLength()));

      Object[] arrayOfObject = getAttributes();

      for (int j = 0; j < i; j++)
      {
        localStringWriter.write(" attribute[" + j + "] = " + arrayOfObject[j]);
      }

    }
    catch (SQLException localSQLException)
    {
      str = "StructDescriptor missing or bad";
    }

    str = localStringWriter.toString();

    return str;
  }

  public boolean isInHierarchyOf(String paramString)
    throws SQLException
  {
    boolean bool = getDescriptor().isInHierarchyOf(paramString);

    return bool;
  }

  public Connection getJavaSqlConnection() throws SQLException
  {
    return super.getJavaSqlConnection();
  }

  public String dump()
    throws SQLException
  {
    return dump(this);
  }

  public static String dump(Object paramObject) throws SQLException
  {
    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
    dump(paramObject, localPrintWriter);
    return localStringWriter.toString();
  }

  public static void dump(Object paramObject, PrintStream paramPrintStream) throws SQLException
  {
    dump(paramObject, new PrintWriter(paramPrintStream, true));
  }

  public static void dump(Object paramObject, PrintWriter paramPrintWriter) throws SQLException
  {
    dump(paramObject, paramPrintWriter, 0);
  }

  static void dump(Object paramObject, PrintWriter paramPrintWriter, int paramInt) throws SQLException
  {
    if ((paramObject instanceof STRUCT)) { dump((STRUCT)paramObject, paramPrintWriter, paramInt); return; }
    if ((paramObject instanceof ARRAY)) { ARRAY.dump((ARRAY)paramObject, paramPrintWriter, paramInt); return; }
    paramPrintWriter.println(paramObject.toString());
  }

  static void dump(STRUCT paramSTRUCT, PrintWriter paramPrintWriter, int paramInt)
    throws SQLException
  {
    StructDescriptor localStructDescriptor = paramSTRUCT.getDescriptor();
    ResultSetMetaData localResultSetMetaData = localStructDescriptor.getMetaData();

    for (int j = 0; j < paramInt; j++) paramPrintWriter.print(' ');
    paramPrintWriter.println("name = " + localStructDescriptor.getName());

    for (j = 0; j < paramInt; j++) paramPrintWriter.print(' ');
    int i;
    paramPrintWriter.println("length = " + (i = localStructDescriptor.getLength()));
    Object[] arrayOfObject = paramSTRUCT.getAttributes();
    for (j = 0; j < i; j++)
    {
      for (int k = 0; k < paramInt; k++) paramPrintWriter.print(' ');
      paramPrintWriter.print(localResultSetMetaData.getColumnName(j + 1) + " = ");
      dump(arrayOfObject[j], paramPrintWriter, paramInt + 1);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.STRUCT
 * JD-Core Version:    0.6.0
 */