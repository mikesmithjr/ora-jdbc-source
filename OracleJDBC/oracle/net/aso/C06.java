package oracle.net.aso;

public abstract interface C06
{
  public abstract int takeSessionKey(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public abstract void renew();

  public abstract boolean compare(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public abstract byte[] compute(byte[] paramArrayOfByte, int paramInt);

  public abstract void init(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public abstract int size();
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.aso.C06
 * JD-Core Version:    0.6.0
 */