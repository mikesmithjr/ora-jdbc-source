package oracle.jdbc.driver;

abstract class CharCopyingBinder extends Binder
{
  Binder copyingBinder()
  {
    return this;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    char[] arrayOfChar;
    int i;
    int j;
    if (paramInt2 == 0)
    {
      arrayOfChar = paramOraclePreparedStatement.lastBoundChars;
      i = paramOraclePreparedStatement.lastBoundCharOffsets[paramInt1];
      paramArrayOfShort[paramInt9] = paramOraclePreparedStatement.lastBoundInds[paramInt1];
      paramArrayOfShort[paramInt8] = paramOraclePreparedStatement.lastBoundLens[paramInt1];

      if ((arrayOfChar == paramArrayOfChar) && (i == paramInt7)) {
        return;
      }
      j = paramOraclePreparedStatement.lastBoundCharLens[paramInt1];

      if (j > paramInt5)
        j = paramInt5;
    }
    else
    {
      arrayOfChar = paramArrayOfChar;
      i = paramInt7 - paramInt5;
      paramArrayOfShort[paramInt9] = paramArrayOfShort[(paramInt9 - 1)];
      paramArrayOfShort[paramInt8] = paramArrayOfShort[(paramInt8 - 1)];
      j = paramInt5;
    }

    System.arraycopy(arrayOfChar, i, paramArrayOfChar, paramInt7, j);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.CharCopyingBinder
 * JD-Core Version:    0.6.0
 */