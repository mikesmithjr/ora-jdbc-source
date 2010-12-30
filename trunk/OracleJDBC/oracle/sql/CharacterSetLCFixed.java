package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSetLCFixed extends CharacterSetWithConverter
{
  static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverterLCFixed";
  static final int CHARLENGTH = 4;
  static Class m_charConvSuperclass;
  char[] m_leadingCodes;

  CharacterSetLCFixed(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt, paramCharacterConverters);

    this.m_leadingCodes = paramCharacterConverters.getLeadingCodes();
  }

  static CharacterSetLCFixed getInstance(int paramInt, CharacterConverters paramCharacterConverters)
  {
    if (paramCharacterConverters.getGroupId() == 3)
    {
      return new CharacterSetLCFixed(paramInt, paramCharacterConverters);
    }

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
  {
    if (paramCharacterWalker.bytes.length - paramCharacterWalker.next < 4)
    {
      throw new SQLException("destination too small");
    }

    int i = paramCharacterWalker.bytes[paramCharacterWalker.next] << 8 | paramCharacterWalker.bytes[(paramCharacterWalker.next + 1)];

    for (int j = 0; j < this.m_leadingCodes.length; j++)
    {
      if (i != this.m_leadingCodes[j])
      {
        continue;
      }
      int k = 0;

      for (int m = 0; m < 4; m++)
      {
        k = k << 8 | paramCharacterWalker.bytes[(paramCharacterWalker.next++)];
      }

      return k;
    }

    throw new SQLException("Leading code invalid");
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

      for (int k = 3; k >= 0; k--)
      {
        paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt >> 8 * k & 0xFF);
      }

      return;
    }

    throw new SQLException("Leading code invalid");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetLCFixed
 * JD-Core Version:    0.6.0
 */