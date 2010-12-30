package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverters;

class CharacterSet2ByteFixed extends CharacterSetWithConverter
{
  static final String CHAR_CONV_SUPERCLASS_NAME = "oracle.sql.converter.CharacterConverter2ByteFixed";
  static final short MAX_7BIT = 127;
  static final short MIN_8BIT_SB = 161;
  static final short MAX_8BIT_SB = 223;
  static final short CHARLENGTH = 2;
  static Class m_charConvSuperclass;

  CharacterSet2ByteFixed(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt, paramCharacterConverters);
  }

  static CharacterSet2ByteFixed getInstance(int paramInt, CharacterConverters paramCharacterConverters)
  {
    if (paramCharacterConverters.getGroupId() == 6)
    {
      return new CharacterSet2ByteFixed(paramInt, paramCharacterConverters);
    }

    return null;
  }

  int decode(CharacterWalker paramCharacterWalker)
    throws SQLException
  {
    int i = paramCharacterWalker.bytes[paramCharacterWalker.next] & 0xFF;

    paramCharacterWalker.next += 1;

    if (paramCharacterWalker.bytes.length > paramCharacterWalker.next)
    {
      i = i << 8 | paramCharacterWalker.bytes[paramCharacterWalker.next];
      paramCharacterWalker.next += 1;
    }
    else
    {
      throw new SQLException("destination too small");
    }

    return i;
  }

  void encode(CharacterBuffer paramCharacterBuffer, int paramInt) throws SQLException
  {
    need(paramCharacterBuffer, 2);

    paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt >> 8 & 0xFF);
    paramCharacterBuffer.bytes[(paramCharacterBuffer.next++)] = (byte)(paramInt & 0xFF);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSet2ByteFixed
 * JD-Core Version:    0.6.0
 */