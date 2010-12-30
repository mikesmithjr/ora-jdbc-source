package oracle.jdbc.oracore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class Util
{
  private static int[] ldsRoundTable = { 0, 1, 0, 2, 0, 0, 0, 3, 0 };

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  static void checkNextByte(InputStream paramInputStream, byte paramByte)
    throws SQLException
  {
    try
    {
      if (paramInputStream.read() != paramByte) {
        DatabaseError.throwSqlException(47, "parseTDS");
      }
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public static int[] toJavaUnsignedBytes(byte[] paramArrayOfByte)
  {
    int[] arrayOfInt = new int[paramArrayOfByte.length];

    for (int i = 0; i < paramArrayOfByte.length; i++) {
      paramArrayOfByte[i] &= 255;
    }
    return arrayOfInt;
  }

  static byte[] readBytes(InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte1 = new byte[paramInt];
    try
    {
      int i = paramInputStream.read(arrayOfByte1);

      if (i != paramInt)
      {
        byte[] arrayOfByte2 = new byte[i];

        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);

        return arrayOfByte2;
      }
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    return arrayOfByte1;
  }

  static void writeBytes(OutputStream paramOutputStream, byte[] paramArrayOfByte)
    throws SQLException
  {
    try
    {
      paramOutputStream.write(paramArrayOfByte);
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  static void skipBytes(InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    try
    {
      paramInputStream.skip(paramInt);
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  static long readLong(InputStream paramInputStream) throws SQLException
  {
    byte[] arrayOfByte = new byte[4];
    try
    {
      paramInputStream.read(arrayOfByte);

      return (((arrayOfByte[0] & 0xFF) * 256 + (arrayOfByte[1] & 0xFF)) * 256 + (arrayOfByte[2] & 0xFF)) * 256 + (arrayOfByte[3] & 0xFF);
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    return 0L;
  }

  static short readShort(InputStream paramInputStream) throws SQLException
  {
    byte[] arrayOfByte = new byte[2];
    try
    {
      paramInputStream.read(arrayOfByte);

      return (short)((arrayOfByte[0] & 0xFF) * 256 + (arrayOfByte[1] & 0xFF));
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    return 0;
  }

  static byte readByte(InputStream paramInputStream) throws SQLException
  {
    try
    {
      return (byte)paramInputStream.read();
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    return 0;
  }

  static byte fdoGetSize(byte[] paramArrayOfByte, int paramInt)
  {
    int i = fdoGetEntry(paramArrayOfByte, paramInt);

    return (byte)(i >> 3 & 0x1F);
  }

  static byte fdoGetAlign(byte[] paramArrayOfByte, int paramInt)
  {
    int i = fdoGetEntry(paramArrayOfByte, paramInt);

    return (byte)(i & 0x7);
  }

  static int ldsRound(int paramInt1, int paramInt2)
  {
    int i = ldsRoundTable[paramInt2];

    return (paramInt1 >> i) + 1 << i;
  }

  private static byte fdoGetEntry(byte[] paramArrayOfByte, int paramInt)
  {
    int i = getUnsignedByte(paramArrayOfByte[5]);
    int j = paramArrayOfByte[(6 + i + paramInt)];

    return j;
  }

  public static short getUnsignedByte(byte paramByte)
  {
    return (short)(paramByte & 0xFF);
  }

  public static byte[] serializeObject(Object paramObject) throws IOException
  {
    if (paramObject == null) {
      return null;
    }
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);

    localObjectOutputStream.writeObject(paramObject);
    localObjectOutputStream.flush();

    return localByteArrayOutputStream.toByteArray();
  }

  public static Object deserializeObject(byte[] paramArrayOfByte)
    throws IOException, ClassNotFoundException
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);

    return new ObjectInputStream(localByteArrayInputStream).readObject();
  }

  public static void printByteArray(byte[] paramArrayOfByte)
  {
    System.out.println("DONT CALL THIS -- oracle.jdbc.oracore.Util.printByteArray");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.Util
 * JD-Core Version:    0.6.0
 */