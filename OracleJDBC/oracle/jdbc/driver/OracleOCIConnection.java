package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.pool.OracleOCIConnectionPool;

public abstract class OracleOCIConnection extends T2CConnection
{
  OracleOCIConnectionPool ociConnectionPool = null;
  boolean isPool = false;
  boolean aliasing = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleOCIConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties, Object paramObject)
    throws SQLException
  {
    this(paramString1, paramString2, paramString3, paramString4, paramProperties, (OracleDriverExtension)paramObject);
  }

  OracleOCIConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties, OracleDriverExtension paramOracleDriverExtension)
    throws SQLException
  {
    super(paramString1, paramString2, paramString3, paramString4, paramProperties, paramOracleDriverExtension);
  }

  public synchronized byte[] getConnectionId()
    throws SQLException
  {
    byte[] arrayOfByte = t2cGetConnectionId(this.m_nativeState);

    this.aliasing = true;

    return arrayOfByte;
  }

  public synchronized void passwordChange(String paramString1, String paramString2, String paramString3)
    throws SQLException, IOException
  {
    ociPasswordChange(paramString1, paramString2, paramString3);
  }

  public synchronized void close()
    throws SQLException
  {
    if ((this.lifecycle == 2) || (this.lifecycle == 4) || (this.aliasing)) {
      return;
    }
    super.close();

    this.ociConnectionPool.connectionClosed((oracle.jdbc.oci.OracleOCIConnection)this);
  }

  public synchronized void setConnectionPool(OracleOCIConnectionPool paramOracleOCIConnectionPool)
  {
    this.ociConnectionPool = paramOracleOCIConnectionPool;
  }

  public synchronized void setStmtCacheSize(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    super.setStmtCacheSize(paramInt, paramBoolean);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleOCIConnection
 * JD-Core Version:    0.6.0
 */