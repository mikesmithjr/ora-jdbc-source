package oracle.jdbc.driver;

class CopiedNullBinder extends Binder
{
  CopiedNullBinder(short paramShort, int paramInt)
  {
    this.type = paramShort;
    this.bytelen = paramInt;
  }

  Binder copyingBinder()
  {
    return this;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    paramArrayOfShort[paramInt9] = -1;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.CopiedNullBinder
 * JD-Core Version:    0.6.0
 */