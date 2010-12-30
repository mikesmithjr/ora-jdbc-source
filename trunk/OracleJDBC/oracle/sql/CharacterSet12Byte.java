package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSet12Byte extends CharacterSetWithConverter
{
  static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverter12Byte";
  static final int MAX_7BIT = 127;
  static Class m_charConvSuperclass;

  CharacterSet12Byte(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt, paramCharacterConverters);
  }

  static CharacterSet12Byte getInstance(int paramInt, CharacterConverters paramCharacterConverters)
  {
    if (paramCharacterConverters.getGroupId() == 1)
    {
      return new CharacterSet12Byte(paramInt, paramCharacterConverters);
    }

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
  {
    int i = paramCharacterWalker.bytes[paramCharacterWalker.next] & 0xFF;

    paramCharacterWalker.next += 1;

    if (i > 127)
    {
      if (paramCharacterWalker.bytes.length > paramCharacterWalker.next)
      {
        i = i << 8 | paramCharacterWalker.bytes[paramCharacterWalker.next];
        paramCharacterWalker.next += 1;
      }
      else
      {
        throw new SQLException("destination too small");
      }
    }

    return i;
  }

  void encode(CharacterBuffer paramCharacterBuffer, int paramInt) throws SQLException
  {
    int i = 0;
    int j = 1;

    while (paramInt >> i != 0)
    {
      i = (short)(i + 8);
      j = (short)(j + 1);
    }

    need(paramCharacterBuffer, j);

    while (i >= 0)
    {
      paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt >> i & 0xFF);
      i = (short)(i - 8);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSet12Byte
 * JD-Core Version:    0.6.0
 */