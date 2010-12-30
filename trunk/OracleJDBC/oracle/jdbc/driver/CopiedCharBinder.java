package oracle.jdbc.driver;

class CopiedCharBinder extends Binder
{
  char[] value;
  short len;

  CopiedCharBinder(short paramShort1, char[] paramArrayOfChar, short paramShort2)
  {
    this.type = paramShort1;
    this.bytelen = 0;
    this.value = paramArrayOfChar;
    this.len = paramShort2;
  }

  Binder copyingBinder()
  {
    return this;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    paramArrayOfShort[paramInt9] = 0;
    paramArrayOfShort[paramInt8] = this.len;

    System.arraycopy(this.value, 0, paramArrayOfChar, paramInt7, this.value.length);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.CopiedCharBinder
 * JD-Core Version:    0.6.0
 */