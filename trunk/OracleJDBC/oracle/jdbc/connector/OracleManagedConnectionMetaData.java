package oracle.jdbc.connector;

import java.sql.SQLException;
import javax.resource.ResourceException;
import javax.resource.spi.EISSystemException;
import javax.resource.spi.ManagedConnectionMetaData;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.driver.OracleDatabaseMetaData;

public class OracleManagedConnectionMetaData
  implements ManagedConnectionMetaData
{
  private OracleManagedConnection managedConnection = null;
  private OracleDatabaseMetaData databaseMetaData = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:57_PDT_2005";

  OracleManagedConnectionMetaData(OracleManagedConnection paramOracleManagedConnection)
    throws ResourceException
  {
    try
    {
      this.managedConnection = paramOracleManagedConnection;

      OracleConnection localOracleConnection = (OracleConnection)paramOracleManagedConnection.getPhysicalConnection();

      this.databaseMetaData = ((OracleDatabaseMetaData)localOracleConnection.getMetaData());
    }
    catch (Exception localException)
    {
      EISSystemException localEISSystemException = new EISSystemException("Exception: " + localException.getMessage());

      localEISSystemException.setLinkedException(localException);

      throw localEISSystemException;
    }
  }

  public String getEISProductName()
    throws ResourceException
  {
    EISSystemException localEISSystemException;
    try
    {
      return this.databaseMetaData.getDatabaseProductName();
    }
    catch (SQLException localSQLException)
    {
      localEISSystemException = new EISSystemException("SQLException: " + localSQLException.getMessage());

      localEISSystemException.setLinkedException(localSQLException);
    }
    throw localEISSystemException;
  }

  public String getEISProductVersion()
    throws ResourceException
  {
    EISSystemException localEISSystemException;
    try
    {
      return this.databaseMetaData.getDatabaseProductVersion();
    }
    catch (Exception localException)
    {
      localEISSystemException = new EISSystemException("Exception: " + localException.getMessage());

      localEISSystemException.setLinkedException(localException);
    }
    throw localEISSystemException;
  }

  public int getMaxConnections()
    throws ResourceException
  {
    EISSystemException localEISSystemException;
    try
    {
      return this.databaseMetaData.getMaxConnections();
    }
    catch (SQLException localSQLException)
    {
      localEISSystemException = new EISSystemException("SQLException: " + localSQLException.getMessage());

      localEISSystemException.setLinkedException(localSQLException);
    }
    throw localEISSystemException;
  }

  public String getUserName()
    throws ResourceException
  {
    EISSystemException localEISSystemException;
    try
    {
      return this.databaseMetaData.getUserName();
    }
    catch (SQLException localSQLException)
    {
      localEISSystemException = new EISSystemException("SQLException: " + localSQLException.getMessage());

      localEISSystemException.setLinkedException(localSQLException);
    }
    throw localEISSystemException;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.connector.OracleManagedConnectionMetaData
 * JD-Core Version:    0.6.0
 */