package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.Datum;

public class OracleBlobInputStream extends OracleBufferedStream
{
  long lobOffset;
  Datum lob;
  long markedByte;
  boolean endOfStream = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public OracleBlobInputStream(BLOB paramBLOB)
    throws SQLException
  {
    this(paramBLOB, ((PhysicalConnection)paramBLOB.getJavaSqlConnection()).getDefaultStreamChunkSize(), 1L);
  }

  public OracleBlobInputStream(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    this(paramBLOB, paramInt, 1L);
  }

  public OracleBlobInputStream(BLOB paramBLOB, int paramInt, long paramLong)
    throws SQLException
  {
    super(paramInt);

    if ((paramBLOB == null) || (paramInt <= 0) || (paramLong < 1L))
    {
      throw new IllegalArgumentException("Illegal Arguments");
    }

    this.lob = paramBLOB;
    this.markedByte = -1L;
    this.lobOffset = paramLong;
  }

  public OracleBlobInputStream(BFILE paramBFILE)
    throws SQLException
  {
    this(paramBFILE, ((PhysicalConnection)paramBFILE.getJavaSqlConnection()).getDefaultStreamChunkSize(), 1L);
  }

  public OracleBlobInputStream(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    this(paramBFILE, paramInt, 1L);
  }

  public OracleBlobInputStream(BFILE paramBFILE, int paramInt, long paramLong)
    throws SQLException
  {
    super(paramInt);

    if ((paramBFILE == null) || (paramInt <= 0) || (paramLong < 1L))
    {
      throw new IllegalArgumentException("Illegal Arguments");
    }

    this.lob = paramBFILE;
    this.markedByte = -1L;
    this.lobOffset = paramLong;
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
          if ((this.lob instanceof BLOB))
            this.count = ((BLOB)this.lob).getBytes(this.lobOffset, this.chunkSize, this.buf);
          else {
            this.count = ((BFILE)this.lob).getBytes(this.lobOffset, this.chunkSize, this.buf);
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

  void ensureOpen()
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

        if ((this.lob instanceof BLOB))
          l2 = ((BLOB)this.lob).length() - this.lobOffset + 1L;
        else {
          l2 = ((BFILE)this.lob).length() - this.lobOffset + 1L;
        }
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
 * Qualified Name:     oracle.jdbc.driver.OracleBlobInputStream
 * JD-Core Version:    0.6.0
 */