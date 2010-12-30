package oracle.sql;

import java.sql.SQLException;
import oracle.sql.converter.CharacterConverterFactory;
import oracle.sql.converter.CharacterConverterFactoryJDBC;
import oracle.sql.converter.CharacterConverters;

public abstract class CharacterSetWithConverter extends CharacterSet
{
  public static CharacterConverterFactory ccFactory = new CharacterConverterFactoryJDBC();
  CharacterConverters m_converter;

  CharacterSetWithConverter(int paramInt, CharacterConverters paramCharacterConverters)
  {
    super(paramInt);

    this.m_converter = paramCharacterConverters;
  }

  static CharacterSet getInstance(int paramInt)
  {
    CharacterConverters localCharacterConverters = ccFactory.make(paramInt);

    if (localCharacterConverters == null)
    {
      return null;
    }

    Object localObject = null;

    if ((localObject = CharacterSet1Byte.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSetSJIS.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSetShift.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSet2ByteFixed.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSetGB18030.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSet12Byte.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSetJAEUC.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    if ((localObject = CharacterSetZHTEUC.getInstance(paramInt, localCharacterConverters)) != null)
    {
      return localObject;
    }

    return (CharacterSet)CharacterSetLCFixed.getInstance(paramInt, localCharacterConverters);
  }

  public boolean isLossyFrom(CharacterSet paramCharacterSet)
  {
    return paramCharacterSet.getOracleId() != getOracleId();
  }

  public boolean isConvertibleFrom(CharacterSet paramCharacterSet)
  {
    return paramCharacterSet.getOracleId() == getOracleId();
  }

  public String toStringWithReplacement(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return this.m_converter.toUnicodeStringWithReplacement(paramArrayOfByte, paramInt1, paramInt2);
  }

  public String toString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    return this.m_converter.toUnicodeString(paramArrayOfByte, paramInt1, paramInt2);
  }

  public byte[] convert(String paramString) throws SQLException
  {
    return this.m_converter.toOracleString(paramString);
  }

  public byte[] convertWithReplacement(String paramString)
  {
    return this.m_converter.toOracleStringWithReplacement(paramString);
  }

  public byte[] convert(CharacterSet paramCharacterSet, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramCharacterSet.getOracleId() == getOracleId())
    {
      return useOrCopy(paramArrayOfByte, paramInt1, paramInt2);
    }

    return convert(paramCharacterSet.toString(paramArrayOfByte, paramInt1, paramInt2));
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetWithConverter
 * JD-Core Version:    0.6.0
 */