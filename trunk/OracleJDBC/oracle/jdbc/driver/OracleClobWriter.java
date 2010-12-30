package oracle.jdbc.driver;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import oracle.sql.CLOB;

public class OracleClobWriter extends Writer
{
  DBConversion dbConversion;
  CLOB clob;
  long lobOffset;
  char[] charBuf;
  byte[] nativeBuf;
  int pos;
  int count;
  int chunkSize;
  boolean isClosed;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleClobWriter(CLOB paramCLOB)
    throws SQLException
  {
    this(paramCLOB, ((PhysicalConnection)paramCLOB.getInternalConnection()).getDefaultStreamChunkSize() / 3, 1L);
  }

  public OracleClobWriter(CLOB paramCLOB, int paramInt)
    throws SQLException
  {
    this(paramCLOB, paramInt, 1L);
  }

  public OracleClobWriter(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if ((paramCLOB == null) || (paramInt <= 0) || (paramCLOB.getJavaSqlConnection() == null) || (paramLong < 1L))
    {
      throw new IllegalArgumentException("Illegal Arguments");
    }

    this.dbConversion = ((PhysicalConnection)paramCLOB.getInternalConnection()).conversion;

    this.clob = paramCLOB;
    this.lobOffset = paramLong;

    this.charBuf = new char[paramInt];
    this.nativeBuf = new byte[paramInt * 3];
    this.pos = (this.count = 0);
    this.chunkSize = paramInt;

    this.isClosed = false;
  }

  public void write(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();

      int i = paramInt1;
      int j = i + Math.min(paramInt2, paramArrayOfChar.length - paramInt1);

      while (i < j)
      {
        int k = Math.min(this.chunkSize - this.count, j - i);

        System.arraycopy(paramArrayOfChar, i, this.charBuf, this.count, k);

        i += k;
        this.count += k;

        if (this.count >= this.chunkSize)
          flushBuffer();
      }
    }
  }

  public void flush()
    throws IOException
  {
    synchronized (this.lock)
    {
      ensureOpen();
      flushBuffer();
    }
  }

  public void close()
    throws IOException
  {
    synchronized (this.lock)
    {
      flushBuffer();

      this.isClosed = true;
    }
  }

  private void flushBuffer()
    throws IOException
  {
    synchronized (this.lock)
    {
      try
      {
        if (this.count > 0)
        {
          this.clob.setString(this.lobOffset, new String(this.charBuf, 0, this.count));

          this.lobOffset += this.count;

          this.count = 0;
        }
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.SQLToIOException(localSQLException);
      }
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
 * Qualified Name:     oracle.jdbc.driver.OracleClobWriter
 * JD-Core Version:    0.6.0
 */