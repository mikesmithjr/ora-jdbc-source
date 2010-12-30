package oracle.jdbc.driver;

abstract class TypeBinder extends Binder
{
  public static final int BYTELEN = 24;

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    byte[][] arrayOfByte = paramOraclePreparedStatement.parameterDatum[paramInt3];
    byte[] arrayOfByte1 = arrayOfByte[paramInt1];

    if (arrayOfByte1 == null)
      paramArrayOfShort[paramInt9] = -1;
    else
      paramArrayOfShort[paramInt9] = 0;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TypeBinder
 * JD-Core Version:    0.6.0
 */