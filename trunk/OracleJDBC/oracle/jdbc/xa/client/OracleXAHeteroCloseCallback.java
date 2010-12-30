package oracle.jdbc.xa.client;

import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleCloseCallback;
import oracle.jdbc.internal.OracleConnection;

public class OracleXAHeteroCloseCallback
  implements OracleCloseCallback
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:56_PDT_2005";

  public synchronized void beforeClose(OracleConnection paramOracleConnection, Object paramObject)
  {
  }

  public synchronized void afterClose(Object paramObject)
  {
    int i = ((OracleXAHeteroConnection)paramObject).getRmid();
    String str = ((OracleXAHeteroConnection)paramObject).getXaCloseString();
    try
    {
      int j = doXaClose(str, i, 0, 0);

      if (j != 0)
      {
        DatabaseError.throwSqlException(-1 * j);
      }
    }
    catch (SQLException localSQLException)
    {
    }
  }

  private native int doXaClose(String paramString, int paramInt1, int paramInt2, int paramInt3);
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.client.OracleXAHeteroCloseCallback
 * JD-Core Version:    0.6.0
 */