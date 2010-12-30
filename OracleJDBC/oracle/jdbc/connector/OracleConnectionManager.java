package oracle.jdbc.connector;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;

public class OracleConnectionManager
  implements ConnectionManager
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:56_PDT_2005";

  public Object allocateConnection(ManagedConnectionFactory paramManagedConnectionFactory, ConnectionRequestInfo paramConnectionRequestInfo)
    throws ResourceException
  {
    ManagedConnection localManagedConnection = paramManagedConnectionFactory.createManagedConnection(null, paramConnectionRequestInfo);

    return localManagedConnection.getConnection(null, paramConnectionRequestInfo);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.connector.OracleConnectionManager
 * JD-Core Version:    0.6.0
 */