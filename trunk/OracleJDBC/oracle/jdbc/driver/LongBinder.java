package oracle.jdbc.driver;

class LongBinder extends VarnumBinder
{
  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int i = paramInt6 + 1;
    long l = paramOraclePreparedStatement.parameterLong[paramInt3][paramInt1];
    int j = setLongInternal(arrayOfByte, i, l);

    arrayOfByte[paramInt6] = (byte)j;
    paramArrayOfShort[paramInt9] = 0;
    paramArrayOfShort[paramInt8] = (short)(j + 1);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.LongBinder
 * JD-Core Version:    0.6.0
 */