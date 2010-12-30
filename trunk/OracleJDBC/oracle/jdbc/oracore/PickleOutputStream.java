package oracle.jdbc.oracore;

import java.io.ByteArrayOutputStream;

public class PickleOutputStream extends ByteArrayOutputStream
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public PickleOutputStream()
  {
  }

  public PickleOutputStream(int paramInt)
  {
    super(paramInt);
  }

  public synchronized int offset()
  {
    return this.count;
  }

  public synchronized void overwrite(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    if ((paramInt2 < 0) || (paramInt2 > paramArrayOfByte.length) || (paramInt3 < 0) || (paramInt2 + paramInt3 > paramArrayOfByte.length) || (paramInt2 + paramInt3 < 0) || (paramInt1 + paramInt3 > this.buf.length))
    {
      throw new IndexOutOfBoundsException();
    }
    if (paramInt3 == 0)
    {
      return;
    }

    for (int i = 0; i < paramInt3; i++)
    {
      this.buf[(paramInt1 + i)] = paramArrayOfByte[(paramInt2 + i)];
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.PickleOutputStream
 * JD-Core Version:    0.6.0
 */