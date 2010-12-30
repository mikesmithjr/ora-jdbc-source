package oracle.net.aso;

public class C10 extends C05
  implements C00
{
  public void c(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws C03
  {
    if ((paramArrayOfByte1 == null) && (paramArrayOfByte2 == null))
      throw new C03(102);
    if (paramArrayOfByte1.length < 16)
      throw new C03(102);
    System.arraycopy(paramArrayOfByte1, 0, this.A, 0, 8);
    System.arraycopy(paramArrayOfByte1, 8, this.C, 0, 8);
    System.arraycopy(this.A, 0, this.F, 0, 8);
    this.v = true;
  }

  public void a()
  {
  }

  public void a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws C03
  {
    this.v = true;
    if ((paramArrayOfByte1 == null) && (paramArrayOfByte2 == null))
    {
      if (this.A == null)
        throw new C03(102);
      return;
    }
    if (paramArrayOfByte1.length < 16)
      throw new C03(102);
    System.arraycopy(paramArrayOfByte1, 0, this.A, 0, 8);
    System.arraycopy(paramArrayOfByte1, 8, this.C, 0, 8);
    System.arraycopy(this.A, 0, this.F, 0, 8);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.aso.C10
 * JD-Core Version:    0.6.0
 */