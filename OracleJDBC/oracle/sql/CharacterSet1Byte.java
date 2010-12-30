package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSet1Byte extends CharacterSetWithConverter
{
  static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverter1Byte";
  static Class m_charConvSuperclass;

  CharacterSet1Byte(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt, paramCharacterConverters);
  }

  static CharacterSet1Byte getInstance(int paramInt, CharacterConverters paramCharacterConverters)
  {
    if (paramCharacterConverters.getGroupId() == 0)
    {
      return new CharacterSet1Byte(paramInt, paramCharacterConverters);
    }

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
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
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSet1Byte
 * JD-Core Version:    0.6.0
 */