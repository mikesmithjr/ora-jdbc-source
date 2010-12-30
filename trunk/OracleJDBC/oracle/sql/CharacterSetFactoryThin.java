package oracle.sql;

class CharacterSetFactoryThin extends CharacterSetFactory
{
  public CharacterSet make(int paramInt)
  {
    if (paramInt == -1)
    {
      paramInt = 31;
    }

    if (paramInt == 2000)
    {
      return new CharacterSetAL16UTF16(paramInt);
    }
    if ((paramInt == 870) || (paramInt == 871))
    {
      return new CharacterSetUTF(paramInt);
    }
    if (paramInt == 873)
    {
      return new CharacterSetAL32UTF8(paramInt);
    }
    if (paramInt == 872)
    {
      return new CharacterSetUTFE(paramInt);
    }
    if (paramInt == 2002)
    {
      return new CharacterSetAL16UTF16LE(paramInt);
    }

    CharacterSet localCharacterSet = CharacterSetWithConverter.getInstance(paramInt);

    if (localCharacterSet != null)
    {
      return localCharacterSet;
    }

    return new CharacterSetUnknown(paramInt);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSetFactoryThin
 * JD-Core Version:    0.6.0
 */