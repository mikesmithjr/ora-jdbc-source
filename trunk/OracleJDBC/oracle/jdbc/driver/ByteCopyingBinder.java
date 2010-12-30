package oracle.jdbc.driver;

abstract class ByteCopyingBinder extends Binder
{
  Binder copyingBinder()
  {
    return this;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    byte[] arrayOfByte;
    int i;
    int j;
    if (paramInt2 == 0)
    {
      arrayOfByte = paramOraclePreparedStatement.lastBoundBytes;
      i = paramOraclePreparedStatement.lastBoundByteOffsets[paramInt1];
      paramArrayOfShort[paramInt9] = paramOraclePreparedStatement.lastBoundInds[paramInt1];
      paramArrayOfShort[paramInt8] = paramOraclePreparedStatement.lastBoundLens[paramInt1];

      if ((arrayOfByte == paramArrayOfByte) && (i == paramInt6)) {
        return;
      }
      j = paramOraclePreparedStatement.lastBoundByteLens[paramInt1];

      if (j > paramInt4)
        j = paramInt4;
    }
    else
    {
      arrayOfByte = paramArrayOfByte;
      i = paramInt6 - paramInt4;
      paramArrayOfShort[paramInt9] = paramArrayOfShort[(paramInt9 - 1)];
      paramArrayOfShort[paramInt8] = paramArrayOfShort[(paramInt8 - 1)];
      j = paramInt4;
    }

    System.arraycopy(arrayOfByte, i, paramArrayOfByte, paramInt6, j);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ByteCopyingBinder
 * JD-Core Version:    0.6.0
 */