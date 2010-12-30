package oracle.jdbc.driver;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import oracle.sql.CLOB;

public class OracleClobReader extends Reader
{
  CLOB clob;
  DBConversion dbConversion;
  long lobOffset;
  long markedChar;
  char[] buf;
  int pos;
  int count;
  int chunkSize;
  boolean isClosed;
  boolean endOfStream;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleClobReader(CLOB paramCLOB)
    throws SQLException
  {
    this(paramCLOB, ((PhysicalConnection)paramCLOB.getInternalConnection()).getDefaultStreamChunkSize() / 3);
  }

  public OracleClobReader(CLOB paramCLOB, int paramInt)
    throws SQLException
  {
    this(paramCLOB, paramInt, 1L);
  }

  public OracleClobReader(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if ((paramCLOB == null) || (paramInt <= 0) || (paramCLOB.getInternalConnection() == null) || (paramLong < 1L))
    {
      throw new IllegalArgumentException("Illegal Arguments");
    }

    this.dbConversion = ((PhysicalConnection)paramCLOB.getInternalConnection()).conversion;

    this.clob = paramCLOB;
    this.lobOffset = paramLong;
    this.markedChar = -1L;

    this.buf = new char[paramInt];
    this.pos = (this.count = 0);
    this.chunkSize = paramInt;

    this.isClosed = false;
  }

  public int read(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws IOException
  {
    ensureOpen();

    if (!needChars()) {
      return -1;
    }
    int i = paramInt1;
    int j = i + Math.min(paramInt2, paramArrayOfChar.length - paramInt1);

    i += writeChars(paramArrayOfChar, i, j - i);

    while ((i < j) && (needChars()))
    {
      i += writeChars(paramArrayOfChar, i, j - i);
    }

    return i - paramInt1;
  }

  protected boolean needChars()
    throws IOException
  {
    ensureOpen();

    if (this.pos >= this.count)
    {
      if (!this.endOfStream)
      {
        try
        {
          this.count = this.clob.getChars(this.lobOffset, this.chunkSize, this.buf);

          if (this.count < this.chunkSize) {
            this.endOfStream = true;
          }
          if (this.count > 0)
          {
            this.pos = 0;
            this.lobOffset += this.count;

            return true;
          }
        }
        catch (SQLException localSQLException)
        {
          DatabaseError.SQLToIOException(localSQLException);
        }
      }

      return false;
    }

    return true;
  }

  protected int writeChars(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    int i = Math.min(paramInt2, this.count - this.pos);

    System.arraycopy(this.buf, this.pos, paramArrayOfChar, paramInt1, i);

    this.pos += i;

    return i;
  }

  public boolean ready()
    throws IOException
  {
    ensureOpen();

    return this.pos < this.count;
  }

  public void close()
    throws IOException
  {
    this.isClosed = true;
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

  public boolean markSupported()
  {
    return true;
  }

  public void mark(int paramInt)
    throws IOException
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Read-ahead limit < 0");
    }
    this.markedChar = (this.lobOffset - this.count + this.pos);
  }

  public void reset()
    throws IOException
  {
    ensureOpen();

    if (this.markedChar < 0L) {
      throw new IOException("Mark invalid or stream not marked.");
    }
    this.lobOffset = this.markedChar;
    this.pos = this.count;
    this.endOfStream = false;
  }

  public long skip(long paramLong)
    throws IOException
  {
    ensureOpen();

    long l1 = 0L;

    if (this.count - this.pos >= paramLong)
    {
      this.pos = (int)(this.pos + paramLong);
      l1 += paramLong;
    }
    else
    {
      l1 += this.count - this.pos;
      this.pos = this.count;
      try
      {
        long l2 = this.clob.length() - this.lobOffset + 1L;

        if (l2 >= paramLong - l1)
        {
          this.lobOffset += paramLong - l1;
          l1 += paramLong - l1;
        }
        else
        {
          this.lobOffset += l2;
          l1 += l2;
        }
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.SQLToIOException(localSQLException);
      }
    }

    return l1;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleClobReader
 * JD-Core Version:    0.6.0
 */