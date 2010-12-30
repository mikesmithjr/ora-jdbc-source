package oracle.sql;

import B;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

class CharacterSetByte extends CharacterSet
  implements CharacterRepConstants
{
  CharacterSetByte(int paramInt)
  {
    super(paramInt);

    this.rep = 1;
  }

  public boolean isLossyFrom(CharacterSet paramCharacterSet)
  {
    return paramCharacterSet.rep != 1;
  }

  public boolean isConvertibleFrom(CharacterSet paramCharacterSet)
  {
    return paramCharacterSet.rep <= 1024;
  }

  private String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2, char paramChar)
    throws SQLException
  {
    try
    {
      return new String(paramArrayOfByte, paramInt1, paramInt2, "ASCII");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }

    throw new SQLException("ascii not supported");
  }

  public String toStringWithReplacement(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      return toString(paramArrayOfByte, paramInt1, paramInt2, '?');
    }
    catch (SQLException localSQLException)
    {
    }

    throw new Error("CharacterSetByte.toString");
  }

  public String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    return toString(paramArrayOfByte, paramInt1, paramInt2, '\000');
  }

  public byte[] convert(String paramString) throws SQLException
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[paramString.length()];

    paramString.getChars(0, i, arrayOfChar, 0);

    return charsToBytes(arrayOfChar, 0);
  }

  public byte[] convertWithReplacement(String paramString)
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[paramString.length()];

    paramString.getChars(0, i, arrayOfChar, 0);
    try
    {
      return charsToBytes(arrayOfChar, 63);
    }
    catch (SQLException localSQLException)
    {
    }

    return new byte[0];
  }

  public byte[] convert(CharacterSet paramCharacterSet, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    byte[] arrayOfByte;
    if (paramCharacterSet.rep == 1)
    {
      arrayOfByte = useOrCopy(paramArrayOfByte, paramInt1, paramInt2);
    }
    else
    {
      Object localObject;
      if (paramCharacterSet.rep == 2)
      {
        localObject = CharacterSetUTF.UTFToJavaChar(paramArrayOfByte, paramInt1, paramInt2);

        arrayOfByte = charsToBytes(localObject, 0);
      }
      else
      {
        localObject = paramCharacterSet.toString(paramArrayOfByte, paramInt1, paramInt2);
        char[] arrayOfChar = ((String)localObject).toCharArray();

        arrayOfByte = charsToBytes(arrayOfChar, 0);
      }
    }
    return (B)arrayOfByte;
  }

  int decode(CharacterWalker paramCharacterWalker)
  {
    int i = paramCharacterWalker.bytes[paramCharacterWalker.next] & 0xFF;

    paramCharacterWalker.next += 1;

    return i;
  }

  void encode(CharacterBuffer paramCharacterBuffer, int paramInt) throws SQLException
  {
    need(paramCharacterBuffer, 1);

    if (paramInt < 256)
    {
      paramCharacterBuffer.bytes[paramCharacterBuffer.next] = (byte)paramInt;
      paramCharacterBuffer.next += 1;
    }
  }

  static byte[] charsToBytes(char[] paramArrayOfChar, byte paramByte)
    throws SQLException
  {
    byte[] arrayOfByte = new byte[paramArrayOfChar.length];

    for (int i = 0; i < paramArrayOfChar.length; i++)
    {
      if (paramArrayOfChar[i] > 'ÿ')
      {
        arrayOfByte[i] = paramByte;

        if (paramByte != 0)
          continue;
        failCharacterConversion(CharacterSet.make(31));
      }
      else
      {
        arrayOfByte[i] = (byte)paramArrayOfChar[i];
      }
    }

    return arrayOfByte;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetByte
 * JD-Core Version:    0.6.0
 */