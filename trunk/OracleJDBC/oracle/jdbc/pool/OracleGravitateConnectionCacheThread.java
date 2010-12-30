package oracle.jdbc.pool;

import java.sql.SQLException;

class OracleGravitateConnectionCacheThread extends Thread
{
  protected OracleImplicitConnectionCache implicitCache = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  OracleGravitateConnectionCacheThread(OracleImplicitConnectionCache paramOracleImplicitConnectionCache)
    throws SQLException
  {
    this.implicitCache = paramOracleImplicitConnectionCache;
  }

  public void run()
  {
    this.implicitCache.gravitateCache();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleGravitateConnectionCacheThread
 * JD-Core Version:    0.6.0
 */