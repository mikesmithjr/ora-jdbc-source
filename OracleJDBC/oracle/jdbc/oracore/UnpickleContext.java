package oracle.jdbc.oracore;

import java.sql.SQLException;
import java.util.Vector;

public final class UnpickleContext
{
  byte[] image;
  int absoluteOffset;
  int beginOffset;
  int markedOffset;
  Vector patches;
  long[] ldsOffsets;
  boolean[] nullIndicators;
  boolean bigEndian;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public UnpickleContext()
  {
  }

  public UnpickleContext(byte[] paramArrayOfByte, int paramInt, boolean[] paramArrayOfBoolean, long[] paramArrayOfLong, boolean paramBoolean)
  {
    this.image = paramArrayOfByte;
    this.beginOffset = paramInt;
    this.absoluteOffset = paramInt;
    this.bigEndian = paramBoolean;
    this.nullIndicators = paramArrayOfBoolean;
    this.patches = null;
    this.ldsOffsets = paramArrayOfLong;
  }

  public byte readByte()
    throws SQLException
  {
    try
    {
      int i = this.image[this.absoluteOffset];
      return i; } finally { this.absoluteOffset += 1; } throw localObject;
  }

  public byte[] readVarNumBytes()
    throws SQLException
  {
    byte[] arrayOfByte = new byte[this.image[this.absoluteOffset] & 0xFF];
    try
    {
      System.arraycopy(this.image, this.absoluteOffset + 1, arrayOfByte, 0, arrayOfByte.length);
    }
    finally
    {
      this.absoluteOffset += 22;
    }

    return arrayOfByte;
  }

  public byte[] readPtrBytes()
    throws SQLException
  {
    byte[] arrayOfByte = new byte[(this.image[this.absoluteOffset] & 0xFF) * 256 + (this.image[(this.absoluteOffset + 1)] & 0xFF) + 2];

    System.arraycopy(this.image, this.absoluteOffset, arrayOfByte, 0, arrayOfByte.length);

    this.absoluteOffset += arrayOfByte.length;

    return arrayOfByte;
  }

  public void skipPtrBytes()
    throws SQLException
  {
    this.absoluteOffset += (this.image[this.absoluteOffset] & 0xFF) * 256 + (this.image[(this.absoluteOffset + 1)] & 0xFF) + 2;
  }

  public byte[] readBytes(int paramInt)
    throws SQLException
  {
    try
    {
      byte[] arrayOfByte1 = new byte[paramInt];

      System.arraycopy(this.image, this.absoluteOffset, arrayOfByte1, 0, paramInt);

      byte[] arrayOfByte2 = arrayOfByte1;
      return arrayOfByte2; } finally { this.absoluteOffset += paramInt; } throw localObject;
  }

  public long readLong()
    throws SQLException
  {
    try
    {
      long l = (((this.image[this.absoluteOffset] & 0xFF) * 256 + (this.image[(this.absoluteOffset + 1)] & 0xFF)) * 256 + (this.image[(this.absoluteOffset + 2)] & 0xFF)) * 256 + (this.image[(this.absoluteOffset + 3)] & 0xFF);
      return l; } finally { this.absoluteOffset += 4; } throw localObject;
  }

  public short readShort()
    throws SQLException
  {
    try
    {
      int i = (short)((this.image[this.absoluteOffset] & 0xFF) * 256 + (this.image[(this.absoluteOffset + 1)] & 0xFF));
      return i; } finally { this.absoluteOffset += 2; } throw localObject;
  }

  public byte[] readLengthBytes()
    throws SQLException
  {
    long l = readLong();

    return readBytes((int)l);
  }

  public void skipLengthBytes()
    throws SQLException
  {
    long l = readLong();

    this.absoluteOffset = (int)(this.absoluteOffset + l);
  }

  public void skipTo(long paramLong)
    throws SQLException
  {
    if (paramLong > this.absoluteOffset - this.beginOffset)
      this.absoluteOffset = (this.beginOffset + (int)paramLong);
  }

  public void skipTo(int paramInt)
    throws SQLException
  {
    if (paramInt > this.absoluteOffset - this.beginOffset)
      this.absoluteOffset = (this.beginOffset + paramInt);
  }

  public void mark()
    throws SQLException
  {
    this.markedOffset = this.absoluteOffset;
  }

  public void reset()
    throws SQLException
  {
    this.absoluteOffset = this.markedOffset;
  }

  public void markAndSkip()
    throws SQLException
  {
    this.markedOffset = (this.absoluteOffset + 4);
    this.absoluteOffset = (this.beginOffset + (int)readLong());
  }

  public void markAndSkip(long paramLong)
    throws SQLException
  {
    this.markedOffset = this.absoluteOffset;
    this.absoluteOffset = (this.beginOffset + (int)paramLong);
  }

  public void skipBytes(int paramInt)
    throws SQLException
  {
    if (paramInt >= 0)
      this.absoluteOffset += paramInt;
  }

  public boolean isNull(int paramInt)
  {
    return this.nullIndicators[paramInt];
  }

  public int absoluteOffset()
    throws SQLException
  {
    return this.absoluteOffset;
  }

  public int offset()
    throws SQLException
  {
    return this.absoluteOffset - this.beginOffset;
  }

  public byte[] image()
    throws SQLException
  {
    return this.image;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.UnpickleContext
 * JD-Core Version:    0.6.0
 */