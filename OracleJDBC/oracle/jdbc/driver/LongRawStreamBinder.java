package oracle.jdbc.driver;

class LongRawStreamBinder extends Binder
{
  LongRawStreamBinder()
  {
    this.type = 24;
    this.bytelen = 0;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
  }

  Binder copyingBinder()
  {
    return OraclePreparedStatementReadOnly.theStaticRawNullBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.LongRawStreamBinder
 * JD-Core Version:    0.6.0
 */