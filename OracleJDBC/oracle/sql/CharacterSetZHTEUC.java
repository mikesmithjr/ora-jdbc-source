package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetZHTEUC extends CharacterSetWithConverter
{
  static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterZHTEUC";
  static final int MAX_7BIT = 127;
  static final int CHARLENGTH = 4;
  static Class m_charConvSuperclass;
  char[] m_leadingCodes;

  CharacterSetZHTEUC(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt, paramCharacterConverters);

    this.m_leadingCodes = paramCharacterConverters.getLeadingCodes();
  }

  static CharacterSetZHTEUC getInstance(int paramInt, CharacterConverters paramCharacterConverters)
  {
    if (paramCharacterConverters.getGroupId() == 5)
    {
      return new CharacterSetZHTEUC(paramInt, paramCharacterConverters);
    }

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
  {
    if (paramCharacterWalker.next + 1 < paramCharacterWalker.bytes.length)
    {
      i = paramCharacterWalker.bytes[paramCharacterWalker.next] << 8 | paramCharacterWalker.bytes[(paramCharacterWalker.next + 1)];

      for (int j = 0; j < this.m_leadingCodes.length; j++)
      {
        if (i != this.m_leadingCodes[j])
        {
          continue;
        }
        if (paramCharacterWalker.bytes.length - paramCharacterWalker.next < 4)
        {
          throw new SQLException("destination too small");
        }

        int k = 0;

        for (int m = 0; m < 4; m++)
        {
          k = k << 8 | paramCharacterWalker.bytes[(paramCharacterWalker.next++)];
        }

        return k;
      }

    }

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

  void encode(CharacterBuffer paramCharacterBuffer, int paramInt)
    throws SQLException
  {
    int i = paramInt >> 16;

    for (int j = 0; j < this.m_leadingCodes.length; j++)
    {
      if (i != this.m_leadingCodes[j])
      {
        continue;
      }
      need(paramCharacterBuffer, 4);

      for (int k = 0; k < 4; k++)
      {
        paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)paramInt;
        paramInt >>= 8;
      }

      return;
    }

    throw new SQLException();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetZHTEUC
 * JD-Core Version:    0.6.0
 */