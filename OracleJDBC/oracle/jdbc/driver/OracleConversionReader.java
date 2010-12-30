package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;

public class OracleConversionReader extends Reader
{
  static final int CHUNK_SIZE = 4096;
  DBConversion dbConversion;
  int conversion;
  InputStream istream;
  char[] buf;
  byte[] byteBuf;
  int pos;
  int count;
  int numUnconvertedBytes;
  boolean isClosed;
  boolean endOfStream;
  private short csform;
  int[] nbytes;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleConversionReader(DBConversion paramDBConversion, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    if ((paramDBConversion == null) || (paramInputStream == null) || ((paramInt != 8) && (paramInt != 9)))
    {
      DatabaseError.throwSqlException(68);
    }
    this.dbConversion = paramDBConversion;
    this.conversion = paramInt;
    this.istream = paramInputStream;
    this.pos = (this.count = 0);
    this.numUnconvertedBytes = 0;

    this.isClosed = false;
    this.nbytes = new int[1];

    if (paramInt == 8)
    {
      this.byteBuf = new byte[2048];
      this.buf = new char[4096];
    }
    else if (paramInt == 9)
    {
      this.byteBuf = new byte[4096];
      this.buf = new char[4096];
    }
  }

  public void setFormOfUse(short paramShort)
  {
    this.csform = paramShort;
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
          int i = this.istream.read(this.byteBuf, this.numUnconvertedBytes, this.byteBuf.length - this.numUnconvertedBytes);

          if (i == -1)
          {
            this.endOfStream = true;

            this.istream.close();

            if (this.numUnconvertedBytes != 0) {
              DatabaseError.throwSqlException(55);
            }
          }
          i += this.numUnconvertedBytes;

          if (i > 0)
          {
            int j;
            switch (this.conversion)
            {
            case 8:
              this.count = DBConversion.RAWBytesToHexChars(this.byteBuf, i, this.buf);

              break;
            case 9:
              this.nbytes[0] = i;

              if (this.csform == 2) {
                this.count = this.dbConversion.NCHARBytesToJavaChars(this.byteBuf, 0, this.buf, 0, this.nbytes, this.buf.length);
              }
              else
              {
                this.count = this.dbConversion.CHARBytesToJavaChars(this.byteBuf, 0, this.buf, 0, this.nbytes, this.buf.length);
              }

              this.numUnconvertedBytes = this.nbytes[0];

              for (j = 0; j < this.numUnconvertedBytes; ) {
                this.byteBuf[j] = this.byteBuf[(i - this.numUnconvertedBytes + j)];

                j++; continue;

                DatabaseError.throwSqlException(23);
              }
            }
            if (this.count > 0)
            {
              this.pos = 0;

              return true;
            }

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
    if (!this.isClosed)
    {
      this.isClosed = true;

      this.istream.close();
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
 * Qualified Name:     oracle.jdbc.driver.OracleConversionReader
 * JD-Core Version:    0.6.0
 */