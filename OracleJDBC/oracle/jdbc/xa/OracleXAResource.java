package oracle.jdbc.xa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import oracle.jdbc.internal.OracleConnection;

public abstract class OracleXAResource
  implements XAResource
{
  public static final int XA_OK = 0;
  public static final short DEFAULT_XA_TIMEOUT = 60;
  protected boolean savedConnectionAutoCommit = false;
  protected boolean savedXAConnectionAutoCommit = false;
  public static final int TMNOFLAGS = 0;
  public static final int TMNOMIGRATE = 2;
  public static final int TMENDRSCAN = 8388608;
  public static final int TMFAIL = 536870912;
  public static final int TMMIGRATE = 1048576;
  public static final int TMJOIN = 2097152;
  public static final int TMONEPHASE = 1073741824;
  public static final int TMRESUME = 134217728;
  public static final int TMSTARTRSCAN = 16777216;
  public static final int TMSUCCESS = 67108864;
  public static final int TMSUSPEND = 33554432;
  public static final int ORATMREADONLY = 256;
  public static final int ORATMREADWRITE = 512;
  public static final int ORATMSERIALIZABLE = 1024;
  public static final int ORAISOLATIONMASK = 65280;
  public static final int ORATRANSLOOSE = 65536;
  protected Connection connection = null;
  protected OracleXAConnection xaconnection = null;
  protected int timeout = 60;
  protected String dblink = null;

  private Connection logicalConnection = null;

  private String synchronizeBeforeRecover = "BEGIN sys.dbms_system.dist_txn_sync(0); END;";

  private String recoverySqlRows = "SELECT formatid, globalid, branchid FROM SYS.DBA_PENDING_TRANSACTIONS";

  protected Vector locallySuspendedTransactions = new Vector();

  protected boolean canBeMigratablySuspended = false;

  protected Xid currentStackedXid = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleXAResource()
  {
  }

  public OracleXAResource(Connection paramConnection, OracleXAConnection paramOracleXAConnection)
    throws XAException
  {
    this.connection = paramConnection;
    this.xaconnection = paramOracleXAConnection;

    if (this.connection == null)
      throw new XAException(-7);
  }

  public synchronized void setConnection(Connection paramConnection)
    throws XAException
  {
    this.connection = paramConnection;

    if (this.connection == null)
      throw new XAException(-7);
  }

  protected void push(Xid paramXid)
  {
    this.currentStackedXid = paramXid;

    enterGlobalTxnMode();
  }

  protected void pop()
  {
    this.currentStackedXid = null;

    exitGlobalTxnMode();
  }

  protected Xid suspendStacked(Xid paramXid)
    throws XAException
  {
    Xid localXid = null;

    if ((this.currentStackedXid != null) && (this.currentStackedXid != paramXid))
    {
      localXid = this.currentStackedXid;

      end(localXid, 33554432);
    }

    return localXid;
  }

  protected Xid suspendStacked(Xid paramXid, int paramInt)
    throws XAException
  {
    Xid localXid = null;

    allowGlobalTxnModeOnly(-3);

    if ((paramInt == 67108864) && (this.currentStackedXid != null) && (paramXid != this.currentStackedXid))
    {
      localXid = this.currentStackedXid;

      end(localXid, 33554432);

      start(paramXid, 134217728);
    }

    return localXid;
  }

  protected void resumeStacked(Xid paramXid)
    throws XAException
  {
    if (paramXid != null)
    {
      start(paramXid, 134217728);
    }
  }

  public abstract void start(Xid paramXid, int paramInt)
    throws XAException;

  public abstract void end(Xid paramXid, int paramInt)
    throws XAException;

  public abstract void commit(Xid paramXid, boolean paramBoolean)
    throws XAException;

  public abstract int prepare(Xid paramXid)
    throws XAException;

  public abstract void forget(Xid paramXid)
    throws XAException;

  public abstract void rollback(Xid paramXid)
    throws XAException;

  public Xid[] recover(int paramInt)
    throws XAException
  {
    if ((paramInt & 0x1800000) != paramInt)
    {
      throw new XAException(-5);
    }

    Statement localStatement = null;
    ResultSet localResultSet = null;
    ArrayList localArrayList = new ArrayList(50);
    try
    {
      localStatement = this.connection.createStatement();
      localStatement.execute(this.synchronizeBeforeRecover);

      localResultSet = localStatement.executeQuery(this.recoverySqlRows);

      while (localResultSet.next())
      {
        localArrayList.add(new OracleXid(localResultSet.getInt(1), localResultSet.getBytes(2), localResultSet.getBytes(3)));
      }

    }
    catch (SQLException localSQLException)
    {
      throw new XAException(-3);
    }
    finally
    {
      try
      {
        if (localStatement != null) {
          localStatement.close();
        }
        if (localResultSet != null)
          localResultSet.close();
      }
      catch (Exception localException) {
      }
    }
    int i = localArrayList.size();
    Xid[] arrayOfXid = new Xid[i];
    System.arraycopy(localArrayList.toArray(), 0, arrayOfXid, 0, i);

    return arrayOfXid;
  }

  protected void restoreAutoCommitModeForGlobalTransaction()
    throws XAException
  {
    if ((this.savedConnectionAutoCommit) && (((OracleConnection)this.connection).getTxnMode() != 1))
    {
      try
      {
        this.connection.setAutoCommit(this.savedConnectionAutoCommit);
        this.xaconnection.setAutoCommit(this.savedXAConnectionAutoCommit);
      }
      catch (SQLException localSQLException)
      {
      }
    }
  }

  protected void saveAndAlterAutoCommitModeForGlobalTransaction()
    throws XAException
  {
    try
    {
      this.savedConnectionAutoCommit = this.connection.getAutoCommit();
      this.connection.setAutoCommit(false);
      this.savedXAConnectionAutoCommit = this.xaconnection.getAutoCommit();
      this.xaconnection.setAutoCommit(false);
    }
    catch (SQLException localSQLException)
    {
    }
  }

  public void resume(Xid paramXid)
    throws XAException
  {
    start(paramXid, 134217728);
  }

  public void join(Xid paramXid)
    throws XAException
  {
    start(paramXid, 2097152);
  }

  public void suspend(Xid paramXid)
    throws XAException
  {
    end(paramXid, 33554432);
  }

  public void join(Xid paramXid, int paramInt)
    throws XAException
  {
    this.timeout = paramInt;

    start(paramXid, 2097152);
  }

  public void resume(Xid paramXid, int paramInt)
    throws XAException
  {
    this.timeout = paramInt;

    start(paramXid, 134217728);
  }

  public Connection getConnection()
  {
    return this.connection;
  }

  public int getTransactionTimeout()
    throws XAException
  {
    return this.timeout;
  }

  public boolean isSameRM(XAResource paramXAResource)
    throws XAException
  {
    Connection localConnection = null;

    if ((paramXAResource instanceof OracleXAResource)) {
      localConnection = ((OracleXAResource)paramXAResource).getConnection();
    }
    else
    {
      return false;
    }

    try
    {
      String str1 = ((OracleConnection)this.connection).getURL();
      String str2 = ((OracleConnection)this.connection).getProtocolType();

      if (localConnection != null)
      {
        int i = (localConnection.equals(this.connection)) || (((OracleConnection)localConnection).getURL().equals(str1)) || ((((OracleConnection)localConnection).getProtocolType().equals(str2)) && (str2.equals("kprb"))) ? 1 : 0;

        return i;
      }

    }
    catch (SQLException localSQLException)
    {
      throw new XAException(-3);
    }

    return false;
  }

  public boolean setTransactionTimeout(int paramInt)
    throws XAException
  {
    if (paramInt < 0) {
      throw new XAException(-5);
    }
    this.timeout = paramInt;

    return true;
  }

  public String getDBLink()
  {
    return this.dblink;
  }

  public void setDBLink(String paramString)
  {
    this.dblink = paramString;
  }

  public void setLogicalConnection(Connection paramConnection)
  {
    this.logicalConnection = paramConnection;
  }

  protected void allowGlobalTxnModeOnly(int paramInt)
    throws XAException
  {
    if (((OracleConnection)this.connection).getTxnMode() != 1)
    {
      throw new XAException(paramInt);
    }
  }

  protected void exitGlobalTxnMode()
  {
    ((OracleConnection)this.connection).setTxnMode(0);
  }

  protected void enterGlobalTxnMode()
  {
    ((OracleConnection)this.connection).setTxnMode(1);
  }

  protected void checkError(int paramInt)
    throws OracleXAException
  {
    if ((paramInt & 0xFFFF) != 0)
      throw new OracleXAException(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.OracleXAResource
 * JD-Core Version:    0.6.0
 */