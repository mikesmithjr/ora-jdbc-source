package oracle.sql;

import java.sql.SQLException;

class CharacterSetAL16UTF16LE extends CharacterSet
  implements CharacterRepConstants
{
  CharacterSetAL16UTF16LE(int paramInt)
  {
    super(paramInt);

    this.rep = 5;
  }

  public boolean isLossyFrom(CharacterSet paramCharacterSet)
  {
    return !paramCharacterSet.isUnicode();
  }

  public boolean isConvertibleFrom(CharacterSet paramCharacterSet)
  {
    int i = paramCharacterSet.rep <= 1024 ? 1 : 0;

    return i;
  }

  public boolean isUnicode()
  {
    return true;
  }

  public String toStringWithReplacement(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      char[] arrayOfChar = new char[Math.min(paramArrayOfByte.length - paramInt1 >>> 1, paramInt2 >>> 1)];

      int i = CharacterSet.convertAL16UTF16LEBytesToJavaChars(paramArrayOfByte, paramInt1, arrayOfChar, 0, paramInt2, true);

      return new String(arrayOfChar, 0, i);
    }
    catch (SQLException localSQLException)
    {
    }

    return "";
  }

  public String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    try
    {
      char[] arrayOfChar = new char[Math.min(paramArrayOfByte.length - paramInt1 >>> 1, paramInt2 >>> 1)];

      int i = CharacterSet.convertAL16UTF16LEBytesToJavaChars(paramArrayOfByte, paramInt1, arrayOfChar, 0, paramInt2, false);

      return new String(arrayOfChar, 0, i);
    }
    catch (SQLException localSQLException)
    {
      failUTFConversion();
    }
    return "";
  }

  public byte[] convert(String paramString)
    throws SQLException
  {
    return stringToAL16UTF16LEBytes(paramString);
  }

  public byte[] convertWithReplacement(String paramString)
  {
    return stringToAL16UTF16LEBytes(paramString);
  }

  public byte[] convert(CharacterSet paramCharacterSet, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    byte[] arrayOfByte;
    if (paramCharacterSet.rep == 5)
    {
      arrayOfByte = useOrCopy(paramArrayOfByte, paramInt1, paramInt2);
    }
    else
    {
      String str = paramCharacterSet.toString(paramArrayOfByte, paramInt1, paramInt2);

      arrayOfByte = stringToAL16UTF16LEBytes(str);
    }

    return arrayOfByte;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
  {
    byte[] arrayOfByte = paramCharacterWalker.bytes;
    int k = paramCharacterWalker.next;
    int m = paramCharacterWalker.end;

    if (k + 2 >= m)
    {
      failUTFConversion();
    }

    int i = arrayOfByte[(k++)];
    int j = arrayOfByte[(k++)];
    int n = i << 8 & 0xFF00 | j;
    paramCharacterWalker.next = k;

    return n;
  }

  void encode(CharacterBuffer paramCharacterBuffer, int paramInt) throws SQLException
  {
    if (paramInt > 65535)
    {
      failUTFConversion();
    }
    else
    {
      need(paramCharacterBuffer, 2);

      paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt >> 8 & 0xFF);
      paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt & 0xFF);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetAL16UTF16LE
 * JD-Core Version:    0.6.0
 */