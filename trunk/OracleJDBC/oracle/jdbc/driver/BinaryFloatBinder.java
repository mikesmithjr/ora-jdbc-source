package oracle.jdbc.driver;

class BinaryFloatBinder extends Binder
{
  Binder theBinaryFloatCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 100;
    paramBinder.bytelen = 4;
  }

  BinaryFloatBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theBinaryFloatCopyingBinder;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int i = paramInt6;
    float f = paramOraclePreparedStatement.parameterFloat[paramInt3][paramInt1];

    if (f == 0.0D)
      f = 0.0F;
    else if (f != f) {
      f = (0.0F / 0.0F);
    }
    int j = Float.floatToIntBits(f);

    int k = j;

    j >>= 8;

    int m = j;

    j >>= 8;

    int n = j;

    j >>= 8;

    int i1 = j;

    if ((i1 & 0x80) == 0) {
      i1 |= 128;
    }
    else {
      i1 ^= -1;
      n ^= -1;
      m ^= -1;
      k ^= -1;
    }

    arrayOfByte[(i + 3)] = (byte)k;
    arrayOfByte[(i + 2)] = (byte)m;
    arrayOfByte[(i + 1)] = (byte)n;
    arrayOfByte[i] = (byte)i1;

    paramArrayOfShort[paramInt9] = 0;
    paramArrayOfShort[paramInt8] = 4;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BinaryFloatBinder
 * JD-Core Version:    0.6.0
 */