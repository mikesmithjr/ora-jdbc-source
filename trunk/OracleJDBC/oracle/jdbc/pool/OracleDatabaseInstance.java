package oracle.jdbc.pool;

class OracleDatabaseInstance
{
  String databaseUniqName = null;
  String instanceName = null;
  int percent = 0;
  int flag = 0;
  int attemptedConnRequestCount = 0;
  int numberOfConnectionsCount = 0;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  OracleDatabaseInstance(String paramString1, String paramString2)
  {
    this.databaseUniqName = paramString1;
    this.instanceName = paramString2;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleDatabaseInstance
 * JD-Core Version:    0.6.0
 */