package oracle.jdbc.xa;

import java.io.Serializable;
import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

public class OracleXid
  implements Xid, Serializable
{
  private int formatId;
  private byte[] gtrid = null;
  private byte[] bqual = null;
  private byte[] txctx = null;
  public static final int MAXGTRIDSIZE = 64;
  public static final int MAXBQUALSIZE = 64;
  private int state;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public OracleXid(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws XAException
  {
    this(paramInt, paramArrayOfByte1, paramArrayOfByte2, null);
  }

  public OracleXid(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    throws XAException
  {
    this.formatId = paramInt;

    if ((paramArrayOfByte1 != null) && (paramArrayOfByte1.length > 64)) {
      throw new XAException(-4);
    }
    this.gtrid = paramArrayOfByte1;

    if ((paramArrayOfByte2 != null) && (paramArrayOfByte2.length > 64)) {
      throw new XAException(-4);
    }
    this.bqual = paramArrayOfByte2;
    this.txctx = paramArrayOfByte3;
    this.state = 0;
  }

  public void setState(int paramInt)
  {
    this.state = paramInt;
  }

  public int getState()
  {
    return this.state;
  }

  public int getFormatId()
  {
    return this.formatId;
  }

  public byte[] getGlobalTransactionId()
  {
    return this.gtrid;
  }

  public byte[] getBranchQualifier()
  {
    return this.bqual;
  }

  public byte[] getTxContext()
  {
    return this.txctx;
  }

  public void setTxContext(byte[] paramArrayOfByte)
  {
    this.txctx = paramArrayOfByte;
  }

  public static final boolean isLocalTransaction(Xid paramXid)
  {
    byte[] arrayOfByte = paramXid.getGlobalTransactionId();

    if (arrayOfByte == null) {
      return true;
    }
    for (int i = 0; i < arrayOfByte.length; i++)
    {
      if (arrayOfByte[i] != 0) {
        return false;
      }
    }
    return true;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.OracleXid
 * JD-Core Version:    0.6.0
 */