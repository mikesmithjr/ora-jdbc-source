package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;

public class JAVA_STRUCT extends STRUCT
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  public JAVA_STRUCT(StructDescriptor paramStructDescriptor, Connection paramConnection, Object[] paramArrayOfObject)
    throws SQLException
  {
    super(paramStructDescriptor, paramConnection, paramArrayOfObject);
  }

  public JAVA_STRUCT(StructDescriptor paramStructDescriptor, byte[] paramArrayOfByte, Connection paramConnection)
    throws SQLException
  {
    super(paramStructDescriptor, paramArrayOfByte, paramConnection);
  }

  public Object toJdbc()
    throws SQLException
  {
    Object localObject1 = getInternalConnection().getJavaObjectTypeMap();

    Class localClass = null;

    if (localObject1 != null) {
      localClass = this.descriptor.getClass((Map)localObject1);
    }
    else {
      localObject1 = new Hashtable(10);

      getInternalConnection().setJavaObjectTypeMap((Map)localObject1);
    }

    if (localClass == null)
    {
      localObject2 = StructDescriptor.getJavaObjectClassName(getInternalConnection(), getDescriptor());

      String str = getDescriptor().getSchemaName();

      if ((localObject2 == null) || (((String)localObject2).length() == 0))
      {
        DatabaseError.throwSqlException(1);
      }

      try
      {
        localClass = getInternalConnection().classForNameAndSchema((String)localObject2, str);
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        DatabaseError.throwSqlException(49, "ClassNotFoundException: " + localClassNotFoundException.getMessage());
      }

      ((Map)localObject1).put(getSQLTypeName(), localClass);
    }

    Object localObject2 = toClass(localClass, getMap());

    return localObject2;
  }

  public Object toJdbc(Map paramMap)
    throws SQLException
  {
    return toJdbc();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.JAVA_STRUCT
 * JD-Core Version:    0.6.0
 */