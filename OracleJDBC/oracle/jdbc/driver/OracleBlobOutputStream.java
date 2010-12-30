package oracle.jdbc.driver;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import oracle.sql.BLOB;

public class OracleBlobOutputStream extends OutputStream
{
  long lobOffset;
  BLOB blob;
  byte[] buf;
  int count;
  int bufSize;
  boolean isClosed;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public OracleBlobOutputStream(BLOB paramBLOB)
    throws SQLException
  {
    this(paramBLOB, ((PhysicalConnection)paramBLOB.getJavaSqlConnection()).getDefaultStreamChunkSize());
  }

  public OracleBlobOutputStream(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    this(paramBLOB, paramInt, 1L);
  }

  public OracleBlobOutputStream(BLOB paramBLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if ((paramBLOB == null) || (paramInt <= 0) || (paramLong < 1L))
    {
      throw new IllegalArgumentException("Illegal Arguments");
    }

    this.blob = paramBLOB;
    this.lobOffset = paramLong;

    this.buf = new byte[paramInt];
    this.count = 0;
    this.bufSize = paramInt;

    this.isClosed = false;
  }

  public void write(int paramInt)
    throws IOException
  {
    ensureOpen();

    if (this.count >= this.buf.length) {
      flushBuffer();
    }
    this.buf[(this.count++)] = (byte)paramInt;
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();

    int i = paramInt1;
    int j = i + Math.min(paramInt2, paramArrayOfByte.length - paramInt1);

    while (i < j)
    {
      int k = Math.min(this.bufSize - this.count, j - i);

      System.arraycopy(paramArrayOfByte, i, this.buf, this.count, k);

      i += k;
      this.count += k;

      if (this.count >= this.bufSize)
        flushBuffer();
    }
  }

  public void flush()
    throws IOException
  {
    ensureOpen();

    flushBuffer();
  }

  public void close()
    throws IOException
  {
    flushBuffer();

    this.isClosed = true;
  }

  private void flushBuffer()
    throws IOException
  {
    try
    {
      if (this.count > 0)
      {
        if (this.count < this.buf.length)
        {
          byte[] arrayOfByte = new byte[this.count];

          System.arraycopy(this.buf, 0, arrayOfByte, 0, this.count);

          this.lobOffset += this.blob.putBytes(this.lobOffset, arrayOfByte);
        }
        else {
          this.lobOffset += this.blob.putBytes(this.lobOffset, this.buf);
        }
        this.count = 0;
      }
    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }
  }

  void ensureOpen()
    throws IOException
  {
    try
    {
      if (this.isClosed)
        DatabaseError.throwSqlException(57, null);
    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleBlobOutputStream
 * JD-Core Version:    0.6.0
 */