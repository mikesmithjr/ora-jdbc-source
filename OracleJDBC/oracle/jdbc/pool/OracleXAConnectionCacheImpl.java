package oracle.jdbc.pool;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.xa.OracleXAConnection;
import oracle.jdbc.xa.OracleXADataSource;

public class OracleXAConnectionCacheImpl extends OracleConnectionCacheImpl
  implements XADataSource, Serializable
{
  private boolean nativeXA = false;
  private static final String clientXADS = "oracle.jdbc.xa.client.OracleXADataSource";
  private static final String serverXADS = "oracle.jdbc.xa.server.OracleXADataSource";
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleXAConnectionCacheImpl()
    throws SQLException
  {
    this(null);
  }

  public OracleXAConnectionCacheImpl(ConnectionPoolDataSource paramConnectionPoolDataSource)
    throws SQLException
  {
    super(paramConnectionPoolDataSource);

    this.dataSourceName = "OracleXAConnectionCacheImpl";
  }

  void initializeConnectionPoolDataSource()
    throws SQLException
  {
    if (this.cpds == null)
    {
      if ((this.user == null) || (this.password == null)) {
        DatabaseError.throwSqlException(79);
      }
      String str = null;

      if (OracleDriver.getSystemPropertyJserverVersion() == null)
        str = "oracle.jdbc.xa.client.OracleXADataSource";
      else {
        str = "oracle.jdbc.xa.server.OracleXADataSource";
      }
      try
      {
        this.cpds = ((OracleXADataSource)Class.forName(str).newInstance());
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        DatabaseError.throwSqlException(1);
      }

      copy((OracleDataSource)this.cpds);

      ((OracleXADataSource)this.cpds).setNativeXA(this.nativeXA);
    }
  }

  PooledConnection getNewPoolOrXAConnection(Properties paramProperties)
    throws SQLException
  {
    XAConnection localXAConnection = ((OracleXADataSource)this.cpds).getXAConnection(paramProperties);

    ((OracleXAConnection)localXAConnection).setStmtCacheSize(this.stmtCacheSize, this.stmtClearMetaData);

    return localXAConnection;
  }

  public synchronized XAConnection getXAConnection()
    throws SQLException
  {
    XAConnection localXAConnection = (XAConnection)super.getPooledConnection(this.user, this.password);

    return localXAConnection;
  }

  public synchronized XAConnection getXAConnection(String paramString1, String paramString2)
    throws SQLException
  {
    XAConnection localXAConnection = (XAConnection)super.getPooledConnection(paramString1, paramString2);

    return localXAConnection;
  }

  public synchronized boolean getNativeXA()
  {
    return this.nativeXA;
  }

  public synchronized void setNativeXA(boolean paramBoolean)
  {
    this.nativeXA = paramBoolean;
  }

  public synchronized void closeActualConnection(PooledConnection paramPooledConnection)
    throws SQLException
  {
    ((OracleXAConnection)paramPooledConnection).close();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleXAConnectionCacheImpl
 * JD-Core Version:    0.6.0
 */