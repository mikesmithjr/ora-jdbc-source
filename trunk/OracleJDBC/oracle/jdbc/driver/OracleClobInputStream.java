package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.sql.CLOB;

public class OracleClobInputStream extends OracleBufferedStream
{
  protected long lobOffset;
  protected CLOB clob;
  protected long markedByte;
  protected boolean endOfStream;
  protected char[] charBuf;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleClobInputStream(CLOB paramCLOB)
    throws SQLException
  {
    this(paramCLOB, ((PhysicalConnection)paramCLOB.getJavaSqlConnection()).getDefaultStreamChunkSize(), 1L);
  }

  public OracleClobInputStream(CLOB paramCLOB, int paramInt)
    throws SQLException
  {
    this(paramCLOB, paramInt, 1L);
  }

  public OracleClobInputStream(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    super(paramInt);

    if ((paramCLOB == null) || (paramInt <= 0) || (paramLong < 1L))
    {
      throw new IllegalArgumentException("Illegal Arguments");
    }

    this.lobOffset = paramLong;
    this.clob = paramCLOB;
    this.markedByte = -1L;
    this.endOfStream = false;
    this.charBuf = new char[paramInt];
  }

  public boolean needBytes()
    throws IOException
  {
    ensureOpen();

    if (this.pos >= this.count)
    {
      if (!this.endOfStream)
      {
        try
        {
          this.count = this.clob.getChars(this.lobOffset, this.chunkSize, this.charBuf);

          for (int i = 0; i < this.count; i++) {
            this.buf[i] = (byte)this.charBuf[i];
          }
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

  protected void ensureOpen()
    throws IOException
  {
    try
    {
      if (this.closed)
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

  public synchronized void mark(int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("Read-ahead limit < 0");
    }
    this.markedByte = (this.lobOffset - this.count + this.pos);
  }

  public synchronized void reset()
    throws IOException
  {
    ensureOpen();

    if (this.markedByte < 0L) {
      throw new IOException("Mark invalid or stream not marked.");
    }
    this.lobOffset = this.markedByte;
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
        long l2 = 0L;

        l2 = this.clob.length() - this.lobOffset + 1L;

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
 * Qualified Name:     oracle.jdbc.driver.OracleClobInputStream
 * JD-Core Version:    0.6.0
 */