package oracle.jdbc.driver;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

class T4CDriverExtension extends OracleDriverExtension
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  Connection getConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties)
    throws SQLException
  {
    return new T4CConnection(paramString1, paramString2, paramString3, paramString4, paramProperties, this);
  }

  OracleStatement allocateStatement(PhysicalConnection paramPhysicalConnection, int paramInt1, int paramInt2)
    throws SQLException
  {
    return new T4CStatement(paramPhysicalConnection, paramInt1, paramInt2);
  }

  OraclePreparedStatement allocatePreparedStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    return new T4CPreparedStatement(paramPhysicalConnection, paramString, paramInt1, paramInt2);
  }

  OracleCallableStatement allocateCallableStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    return new T4CCallableStatement(paramPhysicalConnection, paramString, paramInt1, paramInt2);
  }

  OracleInputStream createInputStream(OracleStatement paramOracleStatement, int paramInt, Accessor paramAccessor)
    throws SQLException
  {
    return new T4CInputStream(paramOracleStatement, paramInt, paramAccessor);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CDriverExtension
 * JD-Core Version:    0.6.0
 */