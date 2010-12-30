package oracle.jdbc.driver;

import java.sql.Connection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import oracle.jdbc.xa.OracleXAResource;
import oracle.jdbc.xa.client.OracleXAConnection;

public class T4CXAConnection extends OracleXAConnection
{
  T4CTTIOtxen otxen;
  T4CTTIOtxse otxse;
  T4CConnection physicalConnection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  public T4CXAConnection(Connection paramConnection)
    throws XAException
  {
    super(paramConnection);

    this.physicalConnection = ((T4CConnection)paramConnection);
    this.xaResource = null;
  }

  public synchronized XAResource getXAResource()
  {
    try
    {
      if (this.xaResource == null)
      {
        this.xaResource = new T4CXAResource(this.physicalConnection, this, this.isXAResourceTransLoose);

        if (this.logicalHandle != null)
        {
          ((OracleXAResource)this.xaResource).setLogicalConnection(this.logicalHandle);
        }

      }

    }
    catch (Exception localException)
    {
      this.xaResource = null;
    }

    return this.xaResource;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CXAConnection
 * JD-Core Version:    0.6.0
 */