package oracle.net.nl;

public class NVNavigator
{
  public NVPair findNVPairRecurse(NVPair paramNVPair, String paramString)
  {
    if ((paramNVPair == null) || (paramString.equalsIgnoreCase(paramNVPair.getName())))
      return paramNVPair;
    if (paramNVPair.getRHSType() == NVPair.RHS_ATOM)
      return null;
    for (int i = 0; i < paramNVPair.getListSize(); i++)
    {
      NVPair localNVPair = findNVPairRecurse(paramNVPair.getListElement(i), paramString);
      if (localNVPair != null)
        return localNVPair;
    }
    return null;
  }

  public NVPair findNVPair(NVPair paramNVPair, String paramString)
  {
    if ((paramNVPair == null) || (paramNVPair.getRHSType() != NVPair.RHS_LIST))
      return null;
    for (int i = 0; i < paramNVPair.getListSize(); i++)
    {
      NVPair localNVPair = paramNVPair.getListElement(i);
      if (paramString.equalsIgnoreCase(localNVPair.getName()))
        return localNVPair;
    }
    return null;
  }

  public NVPair findNVPair(NVPair paramNVPair, String[] paramArrayOfString)
  {
    NVPair localNVPair = paramNVPair;
    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      localNVPair = findNVPair(localNVPair, paramArrayOfString[i]);
      if (localNVPair == null)
        return null;
    }
    return localNVPair;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.nl.NVNavigator
 * JD-Core Version:    0.6.0
 */