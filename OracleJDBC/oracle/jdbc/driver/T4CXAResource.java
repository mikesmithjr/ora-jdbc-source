package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import oracle.jdbc.xa.OracleXAConnection;
import oracle.jdbc.xa.OracleXid;
import oracle.jdbc.xa.client.OracleXADataSource;
import oracle.jdbc.xa.client.OracleXAResource;

class T4CXAResource extends OracleXAResource
{
  T4CConnection physicalConn;
  int[] applicationValueArr = new int[1];
  boolean isTransLoose = false;
  byte[] context;
  int[] errorNumber;
  private String password;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CXAResource(T4CConnection paramT4CConnection, OracleXAConnection paramOracleXAConnection, boolean paramBoolean)
    throws XAException
  {
    super(paramT4CConnection, paramOracleXAConnection);

    this.physicalConn = paramT4CConnection;
    this.isTransLoose = paramBoolean;
    this.errorNumber = new int[1];
  }

  protected int doStart(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;

    synchronized (this.physicalConn)
    {
      synchronized (this)
      {
        if (this.isTransLoose) {
          paramInt |= 65536;
        }

        int j = paramInt & 0x8200000;

        if ((j == 134217728) && (OracleXid.isLocalTransaction(paramXid))) {
          return 0;
        }

        this.applicationValueArr[0] = 0;
        try
        {
          try
          {
            T4CTTIOtxse localT4CTTIOtxse = this.physicalConn.otxse;
            byte[] arrayOfByte1 = null;
            byte[] arrayOfByte2 = paramXid.getGlobalTransactionId();
            byte[] arrayOfByte3 = paramXid.getBranchQualifier();

            int k = 0;
            int m = 0;

            if ((arrayOfByte2 != null) && (arrayOfByte3 != null))
            {
              k = Math.min(arrayOfByte2.length, 64);
              m = Math.min(arrayOfByte3.length, 64);
              arrayOfByte1 = new byte[''];

              System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, k);
              System.arraycopy(arrayOfByte3, 0, arrayOfByte1, k, m);
            }

            int n = 0;

            if (((paramInt & 0x200000) != 0) || ((paramInt & 0x8000000) != 0))
              n |= 4;
            else {
              n |= 1;
            }
            if ((paramInt & 0x100) != 0) {
              n |= 256;
            }
            if ((paramInt & 0x200) != 0) {
              n |= 512;
            }
            if ((paramInt & 0x400) != 0) {
              n |= 1024;
            }
            if ((paramInt & 0x10000) != 0) {
              n |= 65536;
            }
            this.physicalConn.sendPiggyBackedMessages();
            localT4CTTIOtxse.marshal(1, null, arrayOfByte1, paramXid.getFormatId(), k, m, this.timeout, n);

            byte[] arrayOfByte4 = localT4CTTIOtxse.receive(this.applicationValueArr);

            if (arrayOfByte4 != null) {
              this.context = arrayOfByte4;
            }
            i = 0;
          }
          catch (IOException localIOException)
          {
            DatabaseError.throwSqlException(localIOException);
          }

        }
        catch (SQLException localSQLException)
        {
          i = localSQLException.getErrorCode();

          if (i == 0) {
            throw new XAException(-6);
          }

        }

      }

    }

    return i;
  }

  protected int doEnd(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;

    synchronized (this.physicalConn)
    {
      synchronized (this)
      {
        try
        {
          try
          {
            T4CTTIOtxse localT4CTTIOtxse = this.physicalConn.otxse;
            byte[] arrayOfByte1 = null;
            byte[] arrayOfByte2 = paramXid.getGlobalTransactionId();
            byte[] arrayOfByte3 = paramXid.getBranchQualifier();

            int j = 0;
            int k = 0;

            if ((arrayOfByte2 != null) && (arrayOfByte3 != null))
            {
              j = Math.min(arrayOfByte2.length, 64);
              k = Math.min(arrayOfByte3.length, 64);
              arrayOfByte1 = new byte[''];

              System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
              System.arraycopy(arrayOfByte3, 0, arrayOfByte1, j, k);
            }

            byte[] arrayOfByte4 = this.context;
            int m = 0;
            if ((((paramInt & 0x2) == 2) || ((paramInt & 0x100000) != 1048576)) && ((paramInt & 0x2000000) == 33554432))
            {
              m = 1048576;
            }

            this.physicalConn.sendPiggyBackedMessages();
            localT4CTTIOtxse.marshal(2, arrayOfByte4, arrayOfByte1, paramXid.getFormatId(), j, k, this.timeout, m);

            byte[] arrayOfByte5 = localT4CTTIOtxse.receive(this.applicationValueArr);

            if (arrayOfByte5 != null) {
              this.context = arrayOfByte5;
            }
            i = 0;
          }
          catch (IOException localIOException)
          {
            DatabaseError.throwSqlException(localIOException);
          }

        }
        catch (SQLException localSQLException)
        {
          i = localSQLException.getErrorCode();

          if (i == 0) {
            throw new XAException(-6);
          }
        }
      }
    }
    return i;
  }

  protected int doCommit(Xid paramXid, int paramInt)
    throws XAException
  {
    int i = -1;

    synchronized (this.physicalConn)
    {
      synchronized (this)
      {
        int j = paramInt == 1 ? 4 : 2;

        i = doTransaction(paramXid, 1, j);

        if ((paramInt == 1) && ((i == 2) || (i == 4)))
        {
          i = 0;
        }
        else if ((paramInt != 1) && (i == 5))
        {
          i = 0;
        }
        else if (i == 8)
        {
          throw new XAException(106);
        }

        if (i == 24756)
        {
          i = kputxrec(paramXid, 1, this.timeout + 120);
        }
        else if (i == 24780)
        {
          OracleXADataSource localOracleXADataSource = null;
          XAConnection localXAConnection = null;
          try
          {
            localOracleXADataSource = new OracleXADataSource();

            localOracleXADataSource.setURL(this.physicalConn.url);
            localOracleXADataSource.setUser(this.physicalConn.user);
            this.physicalConn.getPasswordInternal(this);
            localOracleXADataSource.setPassword(this.password);

            localXAConnection = localOracleXADataSource.getXAConnection();

            XAResource localXAResource = localXAConnection.getXAResource();

            localXAResource.commit(paramXid, paramInt == 1);

            i = 0;
          }
          catch (SQLException localSQLException)
          {
            throw new XAException(-6);
          }
          finally
          {
            try
            {
              if (localXAConnection != null) {
                localXAConnection.close();
              }
              if (localOracleXADataSource != null) {
                localOracleXADataSource.close();
              }
            }
            catch (Exception localException)
            {
            }
          }
        }
      }
    }

    return i;
  }

  protected int doPrepare(Xid paramXid)
    throws XAException
  {
    int i = -1;

    synchronized (this.physicalConn)
    {
      synchronized (this)
      {
        i = doTransaction(paramXid, 3, 0);

        if (i == 8)
        {
          throw new XAException(106);
        }
        if (i == 4)
        {
          i = 24767;
        }
        else if (i == 1)
        {
          i = 0;
        }
        else if (i == 3)
        {
          i = 24761;
        } else {
          if ((i == 2054) || (i == 24756)) {
            return i;
          }

          i = 24768;
        }

      }

    }

    return i;
  }

  protected int doForget(Xid paramXid)
    throws XAException
  {
    int i = -1;

    synchronized (this.physicalConn)
    {
      synchronized (this)
      {
        try
        {
          if (OracleXid.isLocalTransaction(paramXid)) {
            j = 24771;
            return j;
          }
          int j = doStart(paramXid, 134217728);

          if (j != 24756)
          {
            if (j == 0)
            {
              try
              {
                doEnd(paramXid, 0);
              }
              catch (Exception localException)
              {
              }

            }

            if ((j == 0) || (j == 2079) || (j == 24754) || (j == 24761) || (j == 24774) || (j == 24776) || (j == 25351))
            {
              k = 24769;
              return k;
            }
            if (j == 24752) {
              k = 24771;
              return k;
            }
            int k = j;
            return k;
          }
          i = kputxrec(paramXid, 4, 1);
        }
        finally
        {
        }
      }
    }

    return i;
  }

  protected int doRollback(Xid paramXid)
    throws XAException
  {
    int i = -1;

    synchronized (this.physicalConn)
    {
      synchronized (this)
      {
        i = doTransaction(paramXid, 2, 3);

        if (i == 8)
        {
          throw new XAException(106);
        }
        if ((i == 3) || (i == 25402))
        {
          i = 0;
        }

        if (i == 24756)
        {
          i = kputxrec(paramXid, 2, this.timeout + 120);
        }
        else if (i == 24780)
        {
          OracleXADataSource localOracleXADataSource = null;
          XAConnection localXAConnection = null;
          try
          {
            localOracleXADataSource = new OracleXADataSource();

            localOracleXADataSource.setURL(this.physicalConn.url);
            localOracleXADataSource.setUser(this.physicalConn.user);
            this.physicalConn.getPasswordInternal(this);
            localOracleXADataSource.setPassword(this.password);

            localXAConnection = localOracleXADataSource.getXAConnection();

            XAResource localXAResource = localXAConnection.getXAResource();

            localXAResource.rollback(paramXid);

            i = 0;
          }
          catch (SQLException localSQLException)
          {
            throw new XAException(-6);
          }
          finally
          {
            try
            {
              if (localXAConnection != null) {
                localXAConnection.close();
              }
              if (localOracleXADataSource != null) {
                localOracleXADataSource.close();
              }
            }
            catch (Exception localException)
            {
            }
          }
        }
      }
    }
    return i;
  }

  int doTransaction(Xid paramXid, int paramInt1, int paramInt2)
    throws XAException
  {
    int i = -1;
    try
    {
      try
      {
        T4CTTIOtxen localT4CTTIOtxen = this.physicalConn.otxen;
        byte[] arrayOfByte1 = null;
        byte[] arrayOfByte2 = paramXid.getGlobalTransactionId();
        byte[] arrayOfByte3 = paramXid.getBranchQualifier();

        int j = 0;
        int k = 0;

        if ((arrayOfByte2 != null) && (arrayOfByte3 != null))
        {
          j = Math.min(arrayOfByte2.length, 64);
          k = Math.min(arrayOfByte3.length, 64);
          arrayOfByte1 = new byte[''];

          System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
          System.arraycopy(arrayOfByte3, 0, arrayOfByte1, j, k);
        }

        byte[] arrayOfByte4 = this.context;

        this.physicalConn.sendPiggyBackedMessages();
        localT4CTTIOtxen.marshal(paramInt1, arrayOfByte4, arrayOfByte1, paramXid.getFormatId(), j, k, this.timeout, paramInt2);

        i = localT4CTTIOtxen.receive(this.errorNumber);
      }
      catch (IOException localIOException)
      {
        DatabaseError.throwSqlException(localIOException);
      }

    }
    catch (SQLException localSQLException)
    {
      this.errorNumber[0] = localSQLException.getErrorCode();
    }

    if (this.errorNumber[0] == 0) {
      throw new XAException(-6);
    }
    if (i == -1)
    {
      i = this.errorNumber[0];
    }

    return i;
  }

  protected int kputxrec(Xid paramXid, int paramInt1, int paramInt2)
    throws XAException
  {
    int i = 0;
    int j;
    switch (paramInt1)
    {
    case 1:
      j = 3;

      break;
    case 4:
      j = 2;

      break;
    default:
      j = 0;
    }

    int k = 0;

    while (paramInt2-- > 0)
    {
      k = doTransaction(paramXid, 5, j);

      if (k != 7)
        break;
      try
      {
        Thread.sleep(1000L);
      }
      catch (Exception localException)
      {
      }

    }

    if (k == 7)
    {
      return 24763;
    }
    if (k == 24756)
      return 24756;
    int m;
    switch (k)
    {
    case 3:
      if (paramInt1 == 1) {
        m = 7; break label265;
      }

      m = 8;
      i = -24762;

      break;
    case 0:
      if (paramInt1 == 4)
      {
        m = 8;
        i = 24762; break label265;
      }

      m = 7;
      i = paramInt1 == 1 ? 24756 : 0;

      break;
    case 2:
      if (paramInt1 != 4)
        break;
      m = 8;

      i = 24770;

      break;
    case 1:
    }

    Object localObject = new int[1];

    i = kpuho2oc(k, localObject);

    if ((paramInt1 == 4) && (localObject[0] == 1))
    {
      m = 7;
      i = 0;
    }
    else {
      m = 8;
    }

    label265: localObject = this.physicalConn.k2rpc;
    try
    {
      ((T4CTTIk2rpc)localObject).marshal(3, m);
      ((T4CTTIk2rpc)localObject).receive();
    }
    catch (IOException localIOException)
    {
      throw new XAException(-7);
    }
    catch (SQLException localSQLException)
    {
      throw new XAException(-6);
    }

    return i;
  }

  int kpuho2oc(int paramInt, int[] paramArrayOfInt)
  {
    int i = 0;

    switch (paramInt)
    {
    case 5:
      paramArrayOfInt[0] = 1;
      i = 24764;

      break;
    case 4:
      paramArrayOfInt[0] = 1;
      i = 24765;

      break;
    case 6:
      paramArrayOfInt[0] = 1;
      i = 24766;

      break;
    case 0:
    case 1:
    case 2:
    case 3:
    case 7:
    case 8:
    case 9:
    case 10:
    case 11:
    case 12:
    case 13:
    default:
      i = 24762;
    }

    return i;
  }

  final void setPasswordInternal(String paramString)
  {
    this.password = paramString;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CXAResource
 * JD-Core Version:    0.6.0
 */