package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;

public class OracleBufferedStream extends InputStream
{
  byte[] buf;
  int pos;
  int count;
  boolean closed;
  int chunkSize;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public OracleBufferedStream(int paramInt)
  {
    this.pos = 0;
    this.count = 0;
    this.closed = false;
    this.chunkSize = paramInt;
    this.buf = new byte[paramInt];
  }

  public void close()
    throws IOException
  {
    this.closed = true;
  }

  public boolean needBytes()
    throws IOException
  {
    throw new IOException("You should not call this method");
  }

  public int flushBytes(int paramInt)
  {
    int i = paramInt > this.count - this.pos ? this.count - this.pos : paramInt;

    this.pos += i;

    return i;
  }

  public int writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2 > this.count - this.pos ? this.count - this.pos : paramInt2;

    System.arraycopy(this.buf, this.pos, paramArrayOfByte, paramInt1, i);

    this.pos += i;

    return i;
  }

  public synchronized int read()
    throws IOException
  {
    if ((this.closed) || (isNull())) {
      return -1;
    }
    if (needBytes()) {
      return this.buf[(this.pos++)] & 0xFF;
    }
    return -1;
  }

  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public synchronized int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int j = paramInt1;

    if ((this.closed) || (isNull()))
      return -1;
    int i;
    if (paramInt2 > paramArrayOfByte.length)
      i = j + paramArrayOfByte.length;
    else {
      i = j + paramInt2;
    }
    if (!needBytes()) {
      return -1;
    }
    j += writeBytes(paramArrayOfByte, j, i - j);

    while ((j < i) && (needBytes()))
    {
      j += writeBytes(paramArrayOfByte, j, i - j);
    }

    return j - paramInt1;
  }

  public int available() throws IOException
  {
    if ((this.closed) || (isNull())) {
      return 0;
    }
    return this.count - this.pos;
  }

  public boolean isNull() throws IOException
  {
    return false;
  }

  public synchronized void mark(int paramInt) {
  }

  public synchronized void reset() throws IOException {
    throw new IOException("mark/reset not supported");
  }

  public boolean markSupported()
  {
    return false;
  }

  public long skip(int paramInt)
    throws IOException
  {
    int i = 0;
    int j = paramInt;

    if ((this.closed) || (isNull())) {
      return -1L;
    }
    if (!needBytes()) {
      return -1L;
    }
    while ((i < j) && (needBytes()))
    {
      i += flushBytes(j - i);
    }

    return i;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleBufferedStream
 * JD-Core Version:    0.6.0
 */