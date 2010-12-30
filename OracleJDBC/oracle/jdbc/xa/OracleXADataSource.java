package oracle.jdbc.xa;

import java.sql.SQLException;
import java.util.Properties;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;
import oracle.jdbc.pool.OracleDataSource;

public abstract class OracleXADataSource extends OracleConnectionPoolDataSource
  implements XADataSource
{
  protected boolean useNativeXA = false;
  protected boolean thinUseNativeXA = true;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleXADataSource()
    throws SQLException
  {
    this.dataSourceName = "OracleXADataSource";
  }

  public abstract XAConnection getXAConnection()
    throws SQLException;

  public abstract XAConnection getXAConnection(String paramString1, String paramString2)
    throws SQLException;

  public abstract XAConnection getXAConnection(Properties paramProperties)
    throws SQLException;

  public synchronized void setNativeXA(boolean paramBoolean)
  {
    this.useNativeXA = paramBoolean;
    this.thinUseNativeXA = paramBoolean;
  }

  public synchronized boolean getNativeXA()
  {
    return this.useNativeXA;
  }

  protected void copy(OracleDataSource paramOracleDataSource)
    throws SQLException
  {
    super.copy(paramOracleDataSource);

    ((OracleXADataSource)paramOracleDataSource).useNativeXA = this.useNativeXA;
    ((OracleXADataSource)paramOracleDataSource).thinUseNativeXA = this.thinUseNativeXA;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.OracleXADataSource
 * JD-Core Version:    0.6.0
 */