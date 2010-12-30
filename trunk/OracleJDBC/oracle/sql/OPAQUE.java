package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;

public class OPAQUE extends DatumWithConnection
{
  OpaqueDescriptor descriptor;
  byte[] value;
  long imageOffset;
  long imageLength;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  public OPAQUE(OpaqueDescriptor paramOpaqueDescriptor, Connection paramConnection, Object paramObject)
    throws SQLException
  {
    if (paramOpaqueDescriptor != null) {
      this.descriptor = paramOpaqueDescriptor;
    }
    else
    {
      DatabaseError.throwSqlException(61, "OPAQUE");
    }

    setPhysicalConnectionOf(paramConnection);

    if ((paramObject instanceof byte[])) {
      this.value = ((byte[])paramObject);
    }
    else
    {
      DatabaseError.throwSqlException(59, "OPAQUE()");
    }
  }

  public OPAQUE(OpaqueDescriptor paramOpaqueDescriptor, byte[] paramArrayOfByte, Connection paramConnection)
    throws SQLException
  {
    super(paramArrayOfByte);

    setPhysicalConnectionOf(paramConnection);

    this.descriptor = paramOpaqueDescriptor;
    this.value = null;
  }

  public String getSQLTypeName()
    throws SQLException
  {
    String str = this.descriptor.getName();

    return str;
  }

  public OpaqueDescriptor getDescriptor()
    throws SQLException
  {
    return this.descriptor;
  }

  public void setDescriptor(OpaqueDescriptor paramOpaqueDescriptor)
  {
    this.descriptor = paramOpaqueDescriptor;
  }

  public byte[] toBytes()
    throws SQLException
  {
    return this.descriptor.toBytes(this, false);
  }

  public Object getValue()
    throws SQLException
  {
    return this.descriptor.toValue(this, false);
  }

  public byte[] getBytesValue()
    throws SQLException
  {
    return this.descriptor.toValue(this, false);
  }

  public void setValue(byte[] paramArrayOfByte)
    throws SQLException
  {
    this.value = paramArrayOfByte;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    return false;
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new Object[paramInt];
  }

  public Map getMap()
  {
    try
    {
      return getInternalConnection().getTypeMap();
    }
    catch (SQLException localSQLException)
    {
    }

    return null;
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
      if ((paramClass == null) || (paramClass == OPAQUE.class)) {
        localObject1 = this;
      }
      else {
        ORAData localORAData = null;
        Object localObject2 = paramClass.newInstance();

        if ((localObject2 instanceof ORADataFactory))
        {
          ORADataFactory localORADataFactory = (ORADataFactory)localObject2;

          localORAData = localORADataFactory.create(this, 2007);
        }
        else
        {
          DatabaseError.throwSqlException(49, this.descriptor.getName());
        }

        localObject1 = localORAData;
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
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.OPAQUE
 * JD-Core Version:    0.6.0
 */