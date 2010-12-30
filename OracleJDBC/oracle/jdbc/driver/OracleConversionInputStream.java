package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;

public class OracleConversionInputStream extends OracleBufferedStream
{
  static final int CHUNK_SIZE = 4096;
  DBConversion converter;
  int conversion;
  InputStream istream;
  Reader reader;
  byte[] convbuf;
  char[] javaChars;
  int maxSize;
  int totalSize;
  int numUnconvertedBytes;
  boolean endOfStream;
  private short csform;
  int[] nbytes;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleConversionInputStream(DBConversion paramDBConversion, InputStream paramInputStream, int paramInt)
  {
    this(paramDBConversion, paramInputStream, paramInt, 1);
  }

  public OracleConversionInputStream(DBConversion paramDBConversion, InputStream paramInputStream, int paramInt, short paramShort)
  {
    super(4096);

    this.istream = paramInputStream;
    this.conversion = paramInt;
    this.converter = paramDBConversion;
    this.maxSize = 0;
    this.totalSize = 0;
    this.numUnconvertedBytes = 0;
    this.endOfStream = false;
    this.nbytes = new int[1];
    this.csform = paramShort;
    int i;
    switch (paramInt)
    {
    case 0:
      this.javaChars = new char[4096];
      this.convbuf = new byte[4096];

      break;
    case 1:
      this.convbuf = new byte[2048];
      this.javaChars = new char[2048];

      break;
    case 2:
      this.convbuf = new byte[2048];
      this.javaChars = new char[4096];

      break;
    case 3:
      this.convbuf = new byte[1024];
      this.javaChars = new char[2048];

      break;
    case 4:
      i = 4096 / this.converter.getMaxCharbyteSize();

      this.convbuf = new byte[i * 2];
      this.javaChars = new char[i];

      break;
    case 5:
      if (this.converter.isUcs2CharSet())
      {
        this.convbuf = new byte[2048];
        this.javaChars = new char[2048];
      }
      else
      {
        this.convbuf = new byte[4096];
        this.javaChars = new char[4096];
      }

      break;
    case 7:
      i = 4096 / (paramShort == 2 ? this.converter.getMaxNCharbyteSize() : this.converter.getMaxCharbyteSize());

      this.javaChars = new char[i];

      break;
    case 6:
    default:
      this.convbuf = new byte[4096];
      this.javaChars = new char[4096];
    }
  }

  public OracleConversionInputStream(DBConversion paramDBConversion, InputStream paramInputStream, int paramInt1, int paramInt2)
  {
    this(paramDBConversion, paramInputStream, paramInt1, 1);

    this.maxSize = paramInt2;
    this.totalSize = 0;
  }

  public OracleConversionInputStream(DBConversion paramDBConversion, Reader paramReader, int paramInt1, int paramInt2, short paramShort)
  {
    this(paramDBConversion, (InputStream)null, paramInt1, paramShort);

    this.reader = paramReader;
    this.maxSize = paramInt2;
    this.totalSize = 0;
  }

  public void setFormOfUse(short paramShort)
  {
    this.csform = paramShort;
  }

  public boolean needBytes()
    throws IOException
  {
    if (this.closed) {
      return false;
    }

    if (this.pos < this.count) {
      return true;
    }
    if (this.istream != null)
    {
      return needBytesFromStream();
    }
    if (this.reader != null)
    {
      return needBytesFromReader();
    }

    return false;
  }

  public boolean needBytesFromReader()
    throws IOException
  {
    try
    {
      int i = 0;

      if (this.maxSize == 0)
      {
        i = this.javaChars.length;
      }
      else
      {
        i = Math.min(this.maxSize - this.totalSize, this.javaChars.length);
      }

      if (i <= 0)
      {
        this.reader.close();
        close();

        return false;
      }

      int j = this.reader.read(this.javaChars, 0, i);

      if (j == -1)
      {
        this.reader.close();
        close();

        return false;
      }

      this.totalSize += j;

      switch (this.conversion)
      {
      case 7:
        if (this.csform == 2)
          this.count = this.converter.javaCharsToNCHARBytes(this.javaChars, j, this.buf);
        else {
          this.count = this.converter.javaCharsToCHARBytes(this.javaChars, j, this.buf);
        }

        break;
      default:
        System.arraycopy(this.convbuf, 0, this.buf, 0, j);

        this.count = j;
      }

    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }

    this.pos = 0;

    return true;
  }

  public boolean needBytesFromStream()
    throws IOException
  {
    if (!this.endOfStream)
    {
      try
      {
        int i = 0;

        if (this.maxSize == 0)
        {
          i = this.convbuf.length;
        }
        else
        {
          i = Math.min(this.maxSize - this.totalSize, this.convbuf.length);
        }

        int j = 0;

        if (i <= 0)
        {
          this.endOfStream = true;

          this.istream.close();

          if (this.numUnconvertedBytes != 0) {
            DatabaseError.throwSqlException(55);
          }

        }
        else
        {
          j = this.istream.read(this.convbuf, this.numUnconvertedBytes, i - this.numUnconvertedBytes);
        }

        if (j == -1)
        {
          this.endOfStream = true;

          this.istream.close();

          if (this.numUnconvertedBytes != 0)
            DatabaseError.throwSqlException(55);
        }
        else
        {
          j += this.numUnconvertedBytes;
          this.totalSize += j;
        }

        if (j <= 0)
        {
          return false;
        }
        int k;
        int m;
        switch (this.conversion)
        {
        case 0:
          this.nbytes[0] = j;

          k = this.converter.CHARBytesToJavaChars(this.convbuf, 0, this.javaChars, 0, this.nbytes, this.javaChars.length);

          this.numUnconvertedBytes = this.nbytes[0];

          for (m = 0; m < this.numUnconvertedBytes; m++) {
            this.convbuf[m] = this.convbuf[(j - this.numUnconvertedBytes)];
          }

          this.count = DBConversion.javaCharsToAsciiBytes(this.javaChars, k, this.buf);

          break;
        case 1:
          this.nbytes[0] = j;

          k = this.converter.CHARBytesToJavaChars(this.convbuf, 0, this.javaChars, 0, this.nbytes, this.javaChars.length);

          this.numUnconvertedBytes = this.nbytes[0];

          for (m = 0; m < this.numUnconvertedBytes; m++) {
            this.convbuf[m] = this.convbuf[(j - this.numUnconvertedBytes)];
          }

          this.count = DBConversion.javaCharsToUcs2Bytes(this.javaChars, k, this.buf);

          break;
        case 2:
          k = DBConversion.RAWBytesToHexChars(this.convbuf, j, this.javaChars);

          this.count = DBConversion.javaCharsToAsciiBytes(this.javaChars, k, this.buf);

          break;
        case 3:
          k = DBConversion.RAWBytesToHexChars(this.convbuf, j, this.javaChars);

          this.count = DBConversion.javaCharsToUcs2Bytes(this.javaChars, k, this.buf);

          break;
        case 4:
          k = DBConversion.ucs2BytesToJavaChars(this.convbuf, j, this.javaChars);

          this.count = this.converter.javaCharsToCHARBytes(this.javaChars, k, this.buf);

          break;
        case 5:
          DBConversion.asciiBytesToJavaChars(this.convbuf, j, this.javaChars);

          this.count = this.converter.javaCharsToCHARBytes(this.javaChars, j, this.buf);

          break;
        default:
          System.arraycopy(this.convbuf, 0, this.buf, 0, j);

          this.count = j;
        }

      }
      catch (SQLException localSQLException)
      {
        DatabaseError.SQLToIOException(localSQLException);
      }

      this.pos = 0;

      return true;
    }

    return false;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleConversionInputStream
 * JD-Core Version:    0.6.0
 */