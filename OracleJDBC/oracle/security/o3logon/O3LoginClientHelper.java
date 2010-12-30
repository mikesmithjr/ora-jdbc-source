package oracle.security.o3logon;

public final class O3LoginClientHelper
{
  private boolean a;
  private C1 b;

  public O3LoginClientHelper()
  {
    this.a = true;
    this.b = new C1();
  }

  public byte[] getEPasswd(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return this.b.c(paramArrayOfByte1, paramArrayOfByte2);
  }

  public O3LoginClientHelper(boolean paramBoolean)
  {
    this.a = paramBoolean;
    this.b = new C1();
  }

  public byte[] getSessionKey(String paramString1, String paramString2, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = this.b.m(paramString1, paramString2, this.a);
    return this.b.j(arrayOfByte, paramArrayOfByte);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.security.o3logon.O3LoginClientHelper
 * JD-Core Version:    0.6.0
 */