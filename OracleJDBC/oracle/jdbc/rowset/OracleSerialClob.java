package oracle.jdbc.rowset;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class OracleSerialClob
  implements Clob, Serializable, Cloneable
{
  private char[] buffer;
  private long length;

  public OracleSerialClob(char[] paramArrayOfChar)
    throws SQLException
  {
    this.length = paramArrayOfChar.length;
    this.buffer = new char[(int)this.length];
    for (int i = 0; i < this.length; i++)
      this.buffer[i] = paramArrayOfChar[i];
  }

  public OracleSerialClob(Clob paramClob)
    throws SQLException
  {
    this.length = paramClob.length();
    this.buffer = new char[(int)this.length];
    BufferedReader localBufferedReader = new BufferedReader(paramClob.getCharacterStream());
    try
    {
      int i = 0;
      int j = 0;
      do
      {
        i = localBufferedReader.read(this.buffer, j, (int)(this.length - j));

        j += i;
      }while (i > 0);
    }
    catch (IOException localIOException)
    {
      throw new SQLException("SerialClob: " + localIOException.getMessage());
    }
  }

  public InputStream getAsciiStream()
    throws SQLException
  {
    return new StringBufferInputStream(new String(this.buffer));
  }

  public Reader getCharacterStream()
    throws SQLException
  {
    return new CharArrayReader(this.buffer);
  }

  public String getSubString(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramLong < 0L) || (paramInt > this.length) || (paramLong + paramInt > this.length)) {
      throw new SQLException("Invalid Arguments");
    }
    return new String(this.buffer, (int)paramLong, paramInt);
  }

  public long length()
    throws SQLException
  {
    return this.length;
  }

  public long position(String paramString, long paramLong)
    throws SQLException
  {
    if ((paramLong < 0L) || (paramLong > this.length) || (paramLong + paramString.length() > this.length))
      throw new SQLException("Invalid Arguments"); char[] arrayOfChar = paramString.toCharArray();
    int i = (int)(paramLong - 1L);
    int j = 0;
    long l1 = arrayOfChar.length;

    if ((paramLong < 0L) || (paramLong > this.length));
    while (true) { return -1L;
      while (true)
        if (i < this.length)
        {
          int k = 0;
          long l2 = i + 1;
          if (arrayOfChar[(k++)] == this.buffer[(i++)]) {
            if (k != l1) break;
            return l2;
          }
        } }
    return -1L;
  }

  public long position(Clob paramClob, long paramLong)
    throws SQLException
  {
    return position(paramClob.getSubString(0L, (int)paramClob.length()), paramLong);
  }

  public int setString(long paramLong, String paramString)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
    return -1;
  }

  public int setString(long paramLong, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
    return -1;
  }

  public OutputStream setAsciiStream(long paramLong)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
    return null;
  }

  public Writer setCharacterStream(long paramLong)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
    return null;
  }

  public void truncate(long paramLong)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleSerialClob
 * JD-Core Version:    0.6.0
 */