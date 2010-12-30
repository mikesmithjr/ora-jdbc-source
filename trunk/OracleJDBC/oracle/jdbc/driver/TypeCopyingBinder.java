package oracle.jdbc.driver;

abstract class TypeCopyingBinder extends Binder
{
  Binder copyingBinder()
  {
    return this;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    if (paramInt2 == 0)
    {
      paramOraclePreparedStatement.parameterDatum[0][paramInt1] = paramOraclePreparedStatement.lastBoundTypeBytes[paramInt1];

      paramOraclePreparedStatement.parameterOtype[0][paramInt1] = paramOraclePreparedStatement.lastBoundTypeOtypes[paramInt1];
    }
    else
    {
      paramOraclePreparedStatement.parameterDatum[paramInt2][paramInt1] = paramOraclePreparedStatement.parameterDatum[(paramInt2 - 1)][paramInt1];

      paramOraclePreparedStatement.parameterOtype[paramInt2][paramInt1] = paramOraclePreparedStatement.parameterOtype[(paramInt2 - 1)][paramInt1];
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TypeCopyingBinder
 * JD-Core Version:    0.6.0
 */