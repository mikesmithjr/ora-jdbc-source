package oracle.jdbc.connector;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterInternalException;
import javax.resource.spi.endpoint.MessageEndpointFactory;
import javax.transaction.xa.XAResource;

public class OracleResourceAdapter
  implements ResourceAdapter
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:57_PDT_2005";

  public void start(BootstrapContext paramBootstrapContext)
    throws ResourceAdapterInternalException
  {
  }

  public void stop()
  {
  }

  public void endpointActivation(MessageEndpointFactory paramMessageEndpointFactory, ActivationSpec paramActivationSpec)
    throws NotSupportedException
  {
  }

  public void endpointDeactivation(MessageEndpointFactory paramMessageEndpointFactory, ActivationSpec paramActivationSpec)
  {
  }

  public XAResource[] getXAResources(ActivationSpec[] paramArrayOfActivationSpec)
    throws ResourceException
  {
    return new XAResource[0];
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.connector.OracleResourceAdapter
 * JD-Core Version:    0.6.0
 */