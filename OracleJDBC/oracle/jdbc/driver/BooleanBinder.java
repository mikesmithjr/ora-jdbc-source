package oracle.jdbc.driver;

class BooleanBinder extends VarnumBinder
{
  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int i = paramInt6 + 1;
    int j = paramOraclePreparedStatement.parameterInt[paramInt3][paramInt1];
    int k = 0;

    if (j != 0)
    {
      arrayOfByte[i] = -63;
      arrayOfByte[(i + 1)] = 2;
      k = 2;
    }
    else
    {
      arrayOfByte[i] = -128;
      k = 1;
    }

    arrayOfByte[paramInt6] = (byte)k;
    paramArrayOfShort[paramInt9] = 0;
    paramArrayOfShort[paramInt8] = (short)(k + 1);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BooleanBinder
 * JD-Core Version:    0.6.0
 */