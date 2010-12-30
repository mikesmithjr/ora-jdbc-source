package oracle.jdbc.pool;

import java.sql.SQLException;

class OracleFailoverWorkerThread extends Thread
{
  protected OracleImplicitConnectionCache implicitCache = null;
  protected int eventType = 0;
  protected String eventServiceName = null;
  protected String instanceNameKey = null;
  protected String databaseNameKey = null;
  protected String hostNameKey = null;
  protected String status = null;
  protected int cardinality = 0;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  OracleFailoverWorkerThread(OracleImplicitConnectionCache paramOracleImplicitConnectionCache, int paramInt1, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt2)
    throws SQLException
  {
    this.implicitCache = paramOracleImplicitConnectionCache;
    this.eventType = paramInt1;
    this.instanceNameKey = paramString1;
    this.databaseNameKey = paramString2;
    this.hostNameKey = paramString3;
    this.status = paramString4;
    this.cardinality = paramInt2;
  }

  public void run()
  {
    try
    {
      if (this.status != null)
      {
        this.implicitCache.processFailoverEvent(this.eventType, this.instanceNameKey, this.databaseNameKey, this.hostNameKey, this.status, this.cardinality);
      }
    }
    catch (Exception localException)
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleFailoverWorkerThread
 * JD-Core Version:    0.6.0
 */