package oracle.jdbc.xa.client;

import java.sql.Connection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

public class OracleXAConnection extends oracle.jdbc.xa.OracleXAConnection
{
  protected boolean isXAResourceTransLoose = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:56_PDT_2005";

  public OracleXAConnection()
    throws XAException
  {
  }

  public OracleXAConnection(Connection paramConnection)
    throws XAException
  {
    super(paramConnection);
  }

  public synchronized XAResource getXAResource()
  {
    try
    {
      this.xaResource = new OracleXAResource(this.physicalConn, this);
      ((OracleXAResource)this.xaResource).isTransLoose = this.isXAResourceTransLoose;

      if (this.logicalHandle != null)
      {
        ((oracle.jdbc.xa.OracleXAResource)this.xaResource).setLogicalConnection(this.logicalHandle);
      }
    }
    catch (XAException localXAException)
    {
      this.xaResource = null;
    }

    return this.xaResource;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.client.OracleXAConnection
 * JD-Core Version:    0.6.0
 */