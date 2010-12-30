package oracle.jdbc.rowset;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class OracleSerialBlob
  implements Blob, Serializable, Cloneable
{
  private byte[] buffer;
  private long length;

  public OracleSerialBlob(byte[] paramArrayOfByte)
    throws SQLException
  {
    this.length = paramArrayOfByte.length;
    this.buffer = new byte[(int)this.length];
    for (int i = 0; i < this.length; i++)
      this.buffer[i] = paramArrayOfByte[i];
  }

  public OracleSerialBlob(Blob paramBlob)
    throws SQLException
  {
    this.length = paramBlob.length();
    this.buffer = new byte[(int)this.length];
    BufferedInputStream localBufferedInputStream = new BufferedInputStream(paramBlob.getBinaryStream());
    try
    {
      int i = 0;
      int j = 0;
      do
      {
        i = localBufferedInputStream.read(this.buffer, j, (int)(this.length - j));

        j += i;
      }while (i > 0);
    }
    catch (IOException localIOException)
    {
      throw new SQLException("SerialBlob: " + localIOException.getMessage());
    }
  }

  public InputStream getBinaryStream()
    throws SQLException
  {
    return new ByteArrayInputStream(this.buffer);
  }

  public byte[] getBytes(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramLong < 0L) || (paramInt > this.length) || (paramLong + paramInt > this.length)) {
      throw new SQLException("Invalid Arguments");
    }

    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(this.buffer, (int)paramLong, arrayOfByte, 0, paramInt);

    return arrayOfByte;
  }

  public long length()
    throws SQLException
  {
    return this.length;
  }

  public long position(byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    if ((paramLong < 0L) || (paramLong > this.length) || (paramLong + paramArrayOfByte.length > this.length))
      throw new SQLException("Invalid Arguments"); int i = (int)(paramLong - 1L);
    int j = 0;
    long l1 = paramArrayOfByte.length;

    if ((paramLong < 0L) || (paramLong > this.length));
    while (true) { return -1L;
      while (true)
        if (i < this.length)
        {
          int k = 0;
          long l2 = i + 1;
          if (paramArrayOfByte[(k++)] == this.buffer[(i++)]) {
            if (k != l1) break;
            return l2;
          }
        } }
    return -1L;
  }

  public long position(Blob paramBlob, long paramLong)
    throws SQLException
  {
    return position(paramBlob.getBytes(0L, (int)paramBlob.length()), paramLong);
  }

  public int setBytes(long paramLong, byte[] paramArrayOfByte)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
    return -1;
  }

  public int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
    return -1;
  }

  public OutputStream setBinaryStream(long paramLong)
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
 * Qualified Name:     oracle.jdbc.rowset.OracleSerialBlob
 * JD-Core Version:    0.6.0
 */