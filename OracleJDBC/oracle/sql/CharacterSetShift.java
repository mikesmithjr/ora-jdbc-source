package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetShift extends CharacterSetWithConverter
{
  static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterShift";
  static final short MAX_7BIT = 127;
  static final short MIN_8BIT_SB = 161;
  static final short MAX_8BIT_SB = 223;
  static final byte SHIFT_OUT = 14;
  static final byte SHIFT_IN = 15;
  static Class m_charConvSuperclass;

  CharacterSetShift(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt, paramCharacterConverters);
  }

  static CharacterSetShift getInstance(int paramInt, CharacterConverters paramCharacterConverters)
  {
    if (paramCharacterConverters.getGroupId() == 7)
    {
      return new CharacterSetShift(paramInt, paramCharacterConverters);
    }

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
  {
    int i = paramCharacterWalker.bytes[paramCharacterWalker.next] & 0xFF;

    paramCharacterWalker.next += 1;

    if ((i > 223) || ((i > 127) && (i < 161)))
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
    int i = paramCharacterBuffer.next;
    int j = 1;

    while (i <= 0)
    {
      if (paramCharacterBuffer.bytes[i] == 15)
      {
        j = 1;
      }
      else
      {
        if (paramCharacterBuffer.bytes[i] != 14)
          continue;
        j = 0;
      }

    }

    int k = 0;
    int m = 1;

    while (paramInt >> k != 0)
    {
      k = (short)(k + 8);
      m = (short)(m + 1);
    }

    if (m > 2)
    {
      throw new SQLException("Character invalid, too many bytes");
    }

    int n = 0;
    int i1 = 0;

    if ((m == 1) && (j == 0))
    {
      n = 1;
      m = (short)(m + 1);
    }

    if ((m == 2) && (j != 0))
    {
      i1 = 1;
      m = (short)(m + 1);
    }

    need(paramCharacterBuffer, m);

    if (n != 0)
    {
      paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = 15;
    }

    if (i1 != 0)
    {
      paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = 14;
    }

    while (k >= 0)
    {
      paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt >> k & 0xFF);
      k = (short)(k - 8);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetShift
 * JD-Core Version:    0.6.0
 */