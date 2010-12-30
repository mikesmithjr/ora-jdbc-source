package oracle.jdbc.driver;

class ByteArrayKey
{
  private byte[] theBytes;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public ByteArrayKey(byte[] paramArrayOfByte)
  {
    this.theBytes = paramArrayOfByte;
  }

  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof ByteArrayKey))
    {
      return false;
    }

    byte[] arrayOfByte = ((ByteArrayKey)paramObject).theBytes;

    if (this.theBytes.length != arrayOfByte.length) {
      return false;
    }
    for (int i = 0; i < this.theBytes.length; i++) {
      if (this.theBytes[i] != arrayOfByte[i])
        return false;
    }
    return true;
  }

  public int hashCode()
  {
    int i = 0;

    for (int j = 0; j < this.theBytes.length; j++) {
      i += this.theBytes[j];
    }
    return i;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ByteArrayKey
 * JD-Core Version:    0.6.0
 */