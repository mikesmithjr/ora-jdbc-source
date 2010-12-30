package oracle.jdbc.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.oci.OracleOCIConnection;

class T2CDriverExtension extends OracleDriverExtension
{
  static final int T2C_DEFAULT_BATCHSIZE = 1;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  Connection getConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties)
    throws SQLException
  {
    Object localObject = null;

    if (paramProperties.getProperty("is_connection_pooling") == "true")
    {
      paramProperties.put("database", paramString4 == null ? "" : paramString4);

      localObject = new OracleOCIConnection(paramString1, paramString2, paramString3, paramString4, paramProperties, this);
    }
    else
    {
      localObject = new T2CConnection(paramString1, paramString2, paramString3, paramString4, paramProperties, this);
    }

    return (Connection)localObject;
  }

  OracleStatement allocateStatement(PhysicalConnection paramPhysicalConnection, int paramInt1, int paramInt2)
    throws SQLException
  {
    T2CStatement localT2CStatement = null;

    localT2CStatement = new T2CStatement((T2CConnection)paramPhysicalConnection, 1, paramPhysicalConnection.defaultRowPrefetch, paramInt1, paramInt2);

    return localT2CStatement;
  }

  OraclePreparedStatement allocatePreparedStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    T2CPreparedStatement localT2CPreparedStatement = null;

    localT2CPreparedStatement = new T2CPreparedStatement((T2CConnection)paramPhysicalConnection, paramString, paramPhysicalConnection.defaultBatch, paramPhysicalConnection.defaultRowPrefetch, paramInt1, paramInt2);

    return localT2CPreparedStatement;
  }

  OracleCallableStatement allocateCallableStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    T2CCallableStatement localT2CCallableStatement = null;

    Object localObject = null;

    localT2CCallableStatement = new T2CCallableStatement((T2CConnection)paramPhysicalConnection, paramString, paramPhysicalConnection.defaultBatch, paramPhysicalConnection.defaultRowPrefetch, paramInt1, paramInt2);

    return localT2CCallableStatement;
  }

  OracleInputStream createInputStream(OracleStatement paramOracleStatement, int paramInt, Accessor paramAccessor)
    throws SQLException
  {
    T2CInputStream localT2CInputStream = null;

    localT2CInputStream = new T2CInputStream(paramOracleStatement, paramInt, paramAccessor);

    return localT2CInputStream;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T2CDriverExtension
 * JD-Core Version:    0.6.0
 */