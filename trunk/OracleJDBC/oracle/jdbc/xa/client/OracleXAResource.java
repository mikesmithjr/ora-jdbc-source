package oracle.jdbc.xa.client;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.Util;
import oracle.jdbc.xa.OracleXAConnection;
import oracle.jdbc.xa.OracleXAException;

public class OracleXAResource extends oracle.jdbc.xa.OracleXAResource
{
  private short m_version = 0;

  private static String xa_start_816 = "begin ? := JAVA_XA.xa_start(?,?,?,?); end;";

  private static String xa_start_post_816 = "begin ? := JAVA_XA.xa_start_new(?,?,?,?,?); end;";

  private static String xa_end_816 = "begin ? := JAVA_XA.xa_end(?,?); end;";
  private static String xa_end_post_816 = "begin ? := JAVA_XA.xa_end_new(?,?,?,?); end;";

  private static String xa_commit_816 = "begin ? := JAVA_XA.xa_commit (?,?,?); end;";

  private static String xa_commit_post_816 = "begin ? := JAVA_XA.xa_commit_new (?,?,?,?); end;";

  private static String xa_prepare_816 = "begin ? := JAVA_XA.xa_prepare (?,?); end;";

  private static String xa_prepare_post_816 = "begin ? := JAVA_XA.xa_prepare_new (?,?,?); end;";

  private static String xa_rollback_816 = "begin ? := JAVA_XA.xa_rollback (?,?); end;";

  private static String xa_rollback_post_816 = "begin ? := JAVA_XA.xa_rollback_new (?,?,?); end;";

  private static String xa_forget_816 = "begin ? := JAVA_XA.xa_forget (?,?); end;";

  private static String xa_forget_post_816 = "begin ? := JAVA_XA.xa_forget_new (?,?,?); end;";

  boolean isTransLoose = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:56_PDT_2005";

  public OracleXAResource()
  {
  }

  public OracleXAResource(Connection paramConnection, OracleXAConnection paramOracleXAConnection)
    throws XAException
  {
    super(paramConnection, paramOracleXAConnection);
    try
    {
      this.m_version = ((OracleConnection)paramConnection).getVersionNumber();
    }
    catch (SQLException localSQLException) {
    }
    if (this.m_version < 8170)
    {
      throw new XAException(-6);
    }
  }

  public void start(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;
    Object localObject = null;
    try
    {
      if (paramXid == null)
      {
        throw new XAException(-5);
      }

      int j = paramInt & 0xFF00;

      paramInt &= -65281;

      int k = paramInt & 0x10000 | (this.isTransLoose ? 65536 : 0);

      paramInt &= -65537;

      if (((paramInt & 0x8200002) != paramInt) || ((k != 0) && ((k & 0x10000) != 65536)))
      {
        throw new XAException(-5);
      }

      if (((j & 0xFF00) != 0) && (j != 256) && (j != 512) && (j != 1024))
      {
        throw new XAException(-5);
      }

      if (((paramInt & 0x8200000) != 0) && (((j & 0xFF00) != 0) || ((k & 0x10000) != 0)))
      {
        throw new XAException(-5);
      }

      paramInt |= j | k;

      saveAndAlterAutoCommitModeForGlobalTransaction();

      i = doStart(paramXid, paramInt);

      checkError(i);

      super.push(paramXid);
    }
    catch (XAException localXAException)
    {
      restoreAutoCommitModeForGlobalTransaction();

      throw localXAException;
    }
  }

  protected int doStart(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;
    CallableStatement localCallableStatement = null;
    try
    {
      localCallableStatement = this.connection.prepareCall(xa_start_post_816);

      localCallableStatement.registerOutParameter(1, 2);
      localCallableStatement.setInt(2, paramXid.getFormatId());
      localCallableStatement.setBytes(3, paramXid.getGlobalTransactionId());
      localCallableStatement.setBytes(4, paramXid.getBranchQualifier());
      localCallableStatement.setInt(5, this.timeout);
      localCallableStatement.setInt(6, paramInt);

      localCallableStatement.execute();

      i = localCallableStatement.getInt(1);
    }
    catch (SQLException localSQLException1)
    {
      i = localSQLException1.getErrorCode();

      if (i == 0) {
        throw new XAException(-6);
      }

    }
    finally
    {
      try
      {
        if (localCallableStatement != null)
          localCallableStatement.close();
      }
      catch (SQLException localSQLException2) {
      }
      localCallableStatement = null;
    }

    return i;
  }

  public void end(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;
    try
    {
      if (paramXid == null)
      {
        throw new XAException(-5);
      }

      if ((paramInt != 33554432) && (paramInt != 67108864) && (paramInt != 536870912) && ((paramInt & 0x2) != 2))
      {
        throw new XAException(-5);
      }

      Xid localXid = super.suspendStacked(paramXid, paramInt);

      super.pop();

      i = doEnd(paramXid, paramInt);

      super.resumeStacked(localXid);

      checkError(i);
    }
    finally
    {
      restoreAutoCommitModeForGlobalTransaction();
    }
  }

  protected int doEnd(Xid paramXid, int paramInt)
    throws XAException
  {
    CallableStatement localCallableStatement = null;
    int i = -1;
    try
    {
      localCallableStatement = this.connection.prepareCall(xa_end_post_816);

      localCallableStatement.registerOutParameter(1, 2);
      localCallableStatement.setInt(2, paramXid.getFormatId());
      localCallableStatement.setBytes(3, paramXid.getGlobalTransactionId());
      localCallableStatement.setBytes(4, paramXid.getBranchQualifier());
      localCallableStatement.setInt(5, paramInt);
      localCallableStatement.execute();

      i = localCallableStatement.getInt(1);
    }
    catch (SQLException localSQLException1)
    {
      i = localSQLException1.getErrorCode();

      if (i == 0) {
        throw new XAException(-6);
      }

    }
    finally
    {
      try
      {
        if (localCallableStatement != null)
          localCallableStatement.close();
      }
      catch (SQLException localSQLException2) {
      }
      localCallableStatement = null;
    }

    return i;
  }

  public void commit(Xid paramXid, boolean paramBoolean)
    throws XAException
  {
    int i = -1;
    int j = 0;

    if (paramXid == null)
    {
      throw new XAException(-5);
    }
    int k;
    if (paramBoolean)
      k = 1;
    else {
      k = 0;
    }

    Xid localXid = super.suspendStacked(paramXid);

    i = doCommit(paramXid, k);

    super.resumeStacked(localXid);

    checkError(i);
  }

  protected int doCommit(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;
    CallableStatement localCallableStatement = null;
    int j = 0;
    try
    {
      localCallableStatement = this.connection.prepareCall(xa_commit_post_816);

      localCallableStatement.registerOutParameter(1, 2);
      localCallableStatement.setInt(2, paramXid.getFormatId());
      localCallableStatement.setBytes(3, paramXid.getGlobalTransactionId());
      localCallableStatement.setBytes(4, paramXid.getBranchQualifier());
      localCallableStatement.setInt(5, paramInt);

      localCallableStatement.execute();

      i = localCallableStatement.getInt(1);
    }
    catch (SQLException localSQLException1)
    {
      i = localSQLException1.getErrorCode();

      if (i == 0) {
        throw new XAException(-6);
      }

    }
    finally
    {
      try
      {
        if (localCallableStatement != null)
          localCallableStatement.close();
      }
      catch (SQLException localSQLException2) {
      }
      localCallableStatement = null;
    }

    return i;
  }

  public int prepare(Xid paramXid)
    throws XAException
  {
    int i = 0;

    if (paramXid == null)
    {
      throw new XAException(-5);
    }

    Xid localXid = super.suspendStacked(paramXid);

    i = doPrepare(paramXid);

    super.resumeStacked(localXid);

    int j = i == 0 ? 0 : OracleXAException.errorConvert(i);

    if ((j != 0) && (j != 3)) {
      throw new OracleXAException(i);
    }

    return j;
  }

  protected int doPrepare(Xid paramXid)
    throws XAException
  {
    int i = 0;
    int j = 0;
    CallableStatement localCallableStatement = null;
    try
    {
      localCallableStatement = this.connection.prepareCall(xa_prepare_post_816);

      localCallableStatement.registerOutParameter(1, 2);
      localCallableStatement.setInt(2, paramXid.getFormatId());
      localCallableStatement.setBytes(3, paramXid.getGlobalTransactionId());
      localCallableStatement.setBytes(4, paramXid.getBranchQualifier());

      localCallableStatement.execute();

      i = localCallableStatement.getInt(1);
    }
    catch (SQLException localSQLException1)
    {
      i = localSQLException1.getErrorCode();

      if (i == 0) {
        throw new XAException(-6);
      }

    }
    finally
    {
      try
      {
        if (localCallableStatement != null)
          localCallableStatement.close();
      }
      catch (SQLException localSQLException2) {
      }
      localCallableStatement = null;
    }

    return i;
  }

  public void forget(Xid paramXid)
    throws XAException
  {
    int i = 0;

    if (paramXid == null)
    {
      throw new XAException(-5);
    }

    i = doForget(paramXid);

    checkError(i);
  }

  protected int doForget(Xid paramXid)
    throws XAException
  {
    int i = 0;
    int j = 0;
    CallableStatement localCallableStatement = null;
    try
    {
      localCallableStatement = this.connection.prepareCall(xa_forget_post_816);

      localCallableStatement.registerOutParameter(1, 2);
      localCallableStatement.setInt(2, paramXid.getFormatId());
      localCallableStatement.setBytes(3, paramXid.getGlobalTransactionId());
      localCallableStatement.setBytes(4, paramXid.getBranchQualifier());

      localCallableStatement.execute();

      i = localCallableStatement.getInt(1);
    }
    catch (SQLException localSQLException1)
    {
      i = localSQLException1.getErrorCode();

      if (i == 0) {
        throw new XAException(-6);
      }

    }
    finally
    {
      try
      {
        if (localCallableStatement != null)
          localCallableStatement.close();
      }
      catch (SQLException localSQLException2) {
      }
      localCallableStatement = null;
    }

    return i;
  }

  public void rollback(Xid paramXid)
    throws XAException
  {
    int i = 0;
    int j = 0;

    if (paramXid == null)
    {
      throw new XAException(-5);
    }

    Xid localXid = super.suspendStacked(paramXid);

    i = doRollback(paramXid);

    super.resumeStacked(localXid);

    checkError(i);
  }

  protected int doRollback(Xid paramXid)
    throws XAException
  {
    int i = 0;
    int j = 0;
    CallableStatement localCallableStatement = null;
    try
    {
      localCallableStatement = this.connection.prepareCall(xa_rollback_post_816);

      localCallableStatement.registerOutParameter(1, 2);
      localCallableStatement.setInt(2, paramXid.getFormatId());
      localCallableStatement.setBytes(3, paramXid.getGlobalTransactionId());
      localCallableStatement.setBytes(4, paramXid.getBranchQualifier());

      localCallableStatement.execute();

      i = localCallableStatement.getInt(1);
    }
    catch (SQLException localSQLException1)
    {
      i = localSQLException1.getErrorCode();

      if (i == 0) {
        throw new XAException(-6);
      }

    }
    finally
    {
      try
      {
        if (localCallableStatement != null)
          localCallableStatement.close();
      }
      catch (SQLException localSQLException2) {
      }
      localCallableStatement = null;
    }

    return i;
  }

  public void doTwoPhaseAction(int paramInt1, int paramInt2, String[] paramArrayOfString, Xid[] paramArrayOfXid)
    throws XAException
  {
    doDoTwoPhaseAction(paramInt1, paramInt2, paramArrayOfString, paramArrayOfXid);
  }

  protected int doDoTwoPhaseAction(int paramInt1, int paramInt2, String[] paramArrayOfString, Xid[] paramArrayOfXid)
    throws XAException
  {
    throw new XAException(-6);
  }

  private static byte[] getSerializedBytes(Xid paramXid)
  {
    try
    {
      return Util.serializeObject(paramXid);
    }
    catch (IOException localIOException)
    {
    }

    return null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.client.OracleXAResource
 * JD-Core Version:    0.6.0
 */