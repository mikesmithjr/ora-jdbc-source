package oracle.sql;

import B;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import oracle.core.lmx.CoreException;

public class NUMBER extends Datum
{
  static byte[] MAX_LONG = toBytes(9223372036854775807L);
  static byte[] MIN_LONG = toBytes(-9223372036854775808L);
  private static final int CHARACTER_ZERO = 48;
  private static final BigDecimal BIGDEC_NEGZERO = new BigDecimal("-0");
  private static final BigDecimal BIGDEC_ZERO = BigDecimal.valueOf(0L);
  private static final BigDecimal BIGDEC_ONE = BigDecimal.valueOf(1L);
  private static final BigInteger BIGINT_ZERO = BigInteger.valueOf(0L);
  private static final BigInteger BIGINT_HUND = BigInteger.valueOf(100L);
  private static final byte DIGEND = 21;
  private static final byte ODIGEND = 9;
  private static final int HUNDIGMAX = 66;
  private static final int BIGINTARRAYMAX = 54;
  private static final double BIGRATIO = 0.150514997831991D;
  private static final int BIGLENMAX = 22;
  static final byte LNXM_NUM = 22;
  static final int LNXSGNBT = 128;
  static final byte LNXDIGS = 20;
  static final byte LNXEXPBS = 64;
  static final double ORANUM_FBASE = 100.0D;
  static final int LNXBASE = 100;
  static final byte IEEE_DBL_DIG = 15;
  private static final byte IEEE_FLT_DIG = 6;
  static final int LNXEXPMX = 127;
  static final int LNXEXPMN = 0;
  static final int LNXMXOUT = 40;
  static final int LNXMXFMT = 64;
  private static final byte BYTE_MAX_VALUE = 127;
  private static final byte BYTE_MIN_VALUE = -128;
  private static final short SHORT_MAX_VALUE = 32767;
  private static final short SHORT_MIN_VALUE = -32768;
  private static final byte[] PI = { -63, 4, 15, 16, 93, 66, 36, 90, 80, 33, 39, 47, 27, 44, 39, 33, 80, 51, 29, 85, 21 };

  private static final byte[] E = { -63, 3, 72, 83, 82, 83, 85, 60, 5, 53, 36, 37, 3, 88, 48, 14, 53, 67, 25, 98, 77 };

  private static final byte[] LN10 = { -63, 3, 31, 26, 86, 10, 30, 95, 5, 57, 85, 2, 80, 92, 46, 47, 85, 37, 43, 8, 61 };
  private static LnxLib _slnxlib;
  private static LnxLib _thinlib = null;

  private static int DBL_MAX = 40;

  private static int INT_MAX = 15;
  private static float FLOAT_MAX_INT = 2.147484E+009F;
  private static float FLOAT_MIN_INT = -2.147484E+009F;
  private static double DOUBLE_MAX_INT = 2147483647.0D;
  private static double DOUBLE_MIN_INT = -2147483648.0D;
  private static double DOUBLE_MAX_INT_2 = 2147483649.0D;
  private static double DOUBLE_MIN_INT_2 = -2147483649.0D;

  private static Object drvType = null;
  private static String LANGID;

  public NUMBER()
  {
    super(_makeZero());
  }

  public NUMBER(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }

  public NUMBER(byte paramByte)
  {
    super(toBytes(paramByte));
  }

  public NUMBER(int paramInt)
  {
    super(toBytes(paramInt));
  }

  public NUMBER(long paramLong)
  {
    super(toBytes(paramLong));
  }

  public NUMBER(short paramShort)
  {
    super(toBytes(paramShort));
  }

  public NUMBER(float paramFloat)
  {
    super(toBytes(paramFloat));
  }

  public NUMBER(double paramDouble)
    throws SQLException
  {
    super(toBytes(paramDouble));
  }

  public NUMBER(BigDecimal paramBigDecimal)
    throws SQLException
  {
    super(toBytes(paramBigDecimal));
  }

  public NUMBER(BigInteger paramBigInteger)
    throws SQLException
  {
    super(toBytes(paramBigInteger));
  }

  public NUMBER(String paramString, int paramInt)
    throws SQLException
  {
    super(toBytes(paramString, paramInt));
  }

  public NUMBER(boolean paramBoolean)
  {
    super(toBytes(paramBoolean));
  }

  public NUMBER(Object paramObject)
    throws SQLException
  {
    if ((paramObject instanceof Integer))
      setShareBytes(toBytes(((Integer)paramObject).intValue()));
    else if ((paramObject instanceof Long))
      setShareBytes(toBytes(((Long)paramObject).longValue()));
    else if ((paramObject instanceof Float))
      setShareBytes(toBytes(((Float)paramObject).floatValue()));
    else if ((paramObject instanceof Double))
      setShareBytes(toBytes(((Double)paramObject).doubleValue()));
    else if ((paramObject instanceof BigInteger))
      setShareBytes(toBytes((BigInteger)paramObject));
    else if ((paramObject instanceof BigDecimal))
      setShareBytes(toBytes((BigDecimal)paramObject));
    else if ((paramObject instanceof Boolean))
      setShareBytes(toBytes(((Boolean)paramObject).booleanValue()));
    else if ((paramObject instanceof String)) {
      setShareBytes(stringToBytes((String)paramObject));
    }
    else
      throw new SQLException("Initialization failed");
  }

  public static double toDouble(byte[] paramArrayOfByte)
  {
    if (_isZero(paramArrayOfByte)) {
      return 0.0D;
    }
    if (_isPosInf(paramArrayOfByte)) {
      return (1.0D / 0.0D);
    }
    if (_isNegInf(paramArrayOfByte)) {
      return (-1.0D / 0.0D);
    }

    String str = null;
    try
    {
      if (drvType == null)
      {
        str = _slnxlib.lnxnuc(paramArrayOfByte, DBL_MAX, null);
      }
      else str = _slnxlib.lnxnuc(paramArrayOfByte, DBL_MAX, LANGID);
    }
    catch (Exception localException)
    {
    }

    double d = Double.valueOf(str).doubleValue();

    return d;
  }

  public static float toFloat(byte[] paramArrayOfByte)
  {
    return (float)toDouble(paramArrayOfByte);
  }

  public static long toLong(byte[] paramArrayOfByte)
    throws SQLException
  {
    if (_isZero(paramArrayOfByte)) {
      return 0L;
    }

    if ((_isInf(paramArrayOfByte)) || (compareBytes(paramArrayOfByte, MAX_LONG) > 0) || (compareBytes(paramArrayOfByte, MIN_LONG) < 0))
    {
      throw new SQLException(CoreException.getMessage(3));
    }
    return _getLnxLib().lnxsni(paramArrayOfByte);
  }

  public static int toInt(byte[] paramArrayOfByte)
    throws SQLException
  {
    if (_isInf(paramArrayOfByte))
      throw new SQLException(CoreException.getMessage(3));
    String str;
    if (drvType == null)
    {
      str = _slnxlib.lnxnuc(paramArrayOfByte, INT_MAX, null);
    }
    else str = _slnxlib.lnxnuc(paramArrayOfByte, INT_MAX, LANGID);

    double d = Double.valueOf(str).doubleValue();

    if (((float)d > FLOAT_MAX_INT) || ((float)d < FLOAT_MIN_INT)) {
      throw new SQLException(CoreException.getMessage(3));
    }

    if ((d > DOUBLE_MAX_INT) && (d <= DOUBLE_MAX_INT_2)) {
      throw new SQLException(CoreException.getMessage(3));
    }
    if ((d < DOUBLE_MIN_INT) && (d >= DOUBLE_MIN_INT_2))
      throw new SQLException(CoreException.getMessage(3));
    int i = (int)d;
    return i;
  }

  public static short toShort(byte[] paramArrayOfByte)
    throws SQLException
  {
    long l = 0L;
    try
    {
      l = toLong(paramArrayOfByte);

      if ((l > 32767L) || (l < -32768L)) {
        throw new SQLException(CoreException.getMessage(3));
      }
    }
    finally
    {
    }

    return (short)(int)l;
  }

  public static byte toByte(byte[] paramArrayOfByte)
    throws SQLException
  {
    long l = 0L;
    try
    {
      l = toLong(paramArrayOfByte);

      if ((l > 127L) || (l < -128L)) {
        throw new SQLException(CoreException.getMessage(3));
      }
    }
    finally
    {
    }

    return (byte)(int)l;
  }

  public static BigInteger toBigInteger(byte[] paramArrayOfByte)
    throws SQLException
  {
    long[] arrayOfLong = new long[10];
    int i = 9;
    int j = 1;

    int i1 = 0;

    if (_isZero(paramArrayOfByte)) {
      return BIGINT_ZERO;
    }
    if (_isInf(paramArrayOfByte)) {
      throw new SQLException(CoreException.getMessage(3));
    }

    boolean bool = _isPositive(paramArrayOfByte);

    byte[] arrayOfByte1 = _fromLnxFmt(paramArrayOfByte);

    if (arrayOfByte1[0] < 0)
    {
      return BIGINT_ZERO;
    }

    int i2 = Math.min(arrayOfByte1[0] + 1, arrayOfByte1.length - 1);
    int k = i2;

    if ((i2 & 0x1) == 1)
    {
      arrayOfLong[i] = arrayOfByte1[j];
      j = (byte)(j + 1);
      k--;
    }
    else
    {
      arrayOfLong[i] = (arrayOfByte1[j] * 100 + arrayOfByte1[(j + 1)]);
      j = (byte)(j + 2);
      k -= 2;
    }

    int m = i;
    while (k != 0)
    {
      long l = arrayOfByte1[j] * 100 + arrayOfByte1[(j + 1)];

      for (i = 9; i >= m; i = (byte)(i - 1))
      {
        l += arrayOfLong[i] * 10000L;
        arrayOfLong[i] = (l & 0xFFFF);
        l >>= 16;
      }

      if (l != 0L);
      m = (byte)(m - 1);
      arrayOfLong[m] = l;

      j = (byte)(j + 2);
      k -= 2;
    }
    int n;
    if (arrayOfLong[m] >> 8 != 0L)
      n = 2 * (9 - m) + 2;
    else {
      n = 2 * (9 - m) + 1;
    }
    byte[] arrayOfByte2 = new byte[n];

    if ((n & 0x1) == 1)
    {
      arrayOfByte2[i1] = (byte)(int)arrayOfLong[m];
      i1++;
    }
    else
    {
      arrayOfByte2[i1] = (byte)(int)(arrayOfLong[m] >> 8);
      i1++;
      arrayOfByte2[i1] = (byte)(int)(arrayOfLong[m] & 0xFF);
      i1++;
    }

    for (m = (byte)(m + 1); m <= 9; m = (byte)(m + 1))
    {
      arrayOfByte2[i1] = (byte)(int)(arrayOfLong[m] >> 8);
      arrayOfByte2[(i1 + 1)] = (byte)(int)(arrayOfLong[m] & 0xFF);
      i1 += 2;
    }

    BigInteger localBigInteger = new BigInteger(bool ? 1 : -1, arrayOfByte2);

    int i3 = arrayOfByte1[0] - (i2 - 1);
    return localBigInteger.multiply(BIGINT_HUND.pow(i3));
  }

  public static BigDecimal toBigDecimal(byte[] paramArrayOfByte)
    throws SQLException
  {
    long[] arrayOfLong = new long[10];
    int i = 9;
    int j = 1;

    int i1 = 0;

    if (_isZero(paramArrayOfByte)) {
      return BIGDEC_ZERO;
    }
    if (_isInf(paramArrayOfByte)) {
      throw new SQLException(CoreException.getMessage(3));
    }

    boolean bool = _isPositive(paramArrayOfByte);

    byte[] arrayOfByte1 = _fromLnxFmt(paramArrayOfByte);
    int i2;
    int k = i2 = arrayOfByte1.length - 1;

    if ((i2 & 0x1) == 1)
    {
      arrayOfLong[i] = arrayOfByte1[j];
      j = (byte)(j + 1);
      k--;
    }
    else
    {
      arrayOfLong[i] = (arrayOfByte1[j] * 100 + arrayOfByte1[(j + 1)]);
      j = (byte)(j + 2);
      k -= 2;
    }

    int m = i;
    while (k != 0)
    {
      long l = arrayOfByte1[j] * 100 + arrayOfByte1[(j + 1)];

      for (i = 9; i >= m; i = (byte)(i - 1))
      {
        l += arrayOfLong[i] * 10000L;
        arrayOfLong[i] = (l & 0xFFFF);
        l >>= 16;
      }

      if (l != 0L);
      m = (byte)(m - 1);
      arrayOfLong[m] = l;

      j = (byte)(j + 2);
      k -= 2;
    }
    int n;
    if (arrayOfLong[m] >> 8 != 0L)
      n = 2 * (9 - m) + 2;
    else {
      n = 2 * (9 - m) + 1;
    }
    byte[] arrayOfByte2 = new byte[n];

    if ((n & 0x1) == 1)
    {
      arrayOfByte2[i1] = (byte)(int)arrayOfLong[m];
      i1++;
    }
    else
    {
      arrayOfByte2[i1] = (byte)(int)(arrayOfLong[m] >> 8);
      i1++;
      arrayOfByte2[i1] = (byte)(int)(arrayOfLong[m] & 0xFF);
      i1++;
    }

    for (m = (byte)(m + 1); m <= 9; m = (byte)(m + 1))
    {
      arrayOfByte2[i1] = (byte)(int)(arrayOfLong[m] >> 8);
      arrayOfByte2[(i1 + 1)] = (byte)(int)(arrayOfLong[m] & 0xFF);
      i1 += 2;
    }

    BigInteger localBigInteger = new BigInteger(bool ? 1 : -1, arrayOfByte2);
    BigDecimal localBigDecimal = new BigDecimal(localBigInteger);

    int i3 = arrayOfByte1[0] - i2 + 1;

    localBigDecimal = localBigDecimal.movePointRight(i3 * 2);

    if ((i3 < 0) && (arrayOfByte1[i2] % 10 == 0))
      localBigDecimal = localBigDecimal.setScale(-(i3 * 2 + 1));
    return localBigDecimal;
  }

  public static String toString(byte[] paramArrayOfByte)
  {
    int i = 0;

    if (_isZero(paramArrayOfByte)) {
      return new String("0");
    }
    if (_isPosInf(paramArrayOfByte)) {
      return new Double((1.0D / 0.0D)).toString();
    }
    if (_isNegInf(paramArrayOfByte)) {
      return new Double((-1.0D / 0.0D)).toString();
    }

    byte[] arrayOfByte = _fromLnxFmt(paramArrayOfByte);

    int k = arrayOfByte[0];
    int m = arrayOfByte.length - 1;
    int n = k - (m - 1);
    int i2;
    if (n >= 0)
    {
      i2 = 2 * (k + 1) + 1;
    }
    else if (k >= 0)
      i2 = 2 * (m + 1);
    else {
      i2 = 2 * (m - k) + 3;
    }

    char[] arrayOfChar = new char[i2];

    if (!_isPositive(paramArrayOfByte))
    {
      arrayOfChar[(i++)] = '-';
    }
    int j;
    if (n >= 0)
    {
      i += _byteToChars(arrayOfByte[1], arrayOfChar, i);

      for (j = 2; j <= m; k--)
      {
        _byteTo2Chars(arrayOfByte[j], arrayOfChar, i);
        i += 2;

        j++;
      }

      if (k > 0)
        for (; k > 0; k--)
        {
          arrayOfChar[(i++)] = '0';
          arrayOfChar[(i++)] = '0';
        }
    }
    else
    {
      int i1 = m + n;

      if (i1 > 0)
      {
        i += _byteToChars(arrayOfByte[1], arrayOfChar, i);

        if (i1 == 1)
        {
          arrayOfChar[(i++)] = '.';
        }

        for (j = 2; j < m; j++)
        {
          _byteTo2Chars(arrayOfByte[j], arrayOfChar, i);
          i += 2;

          if (i1 != j)
            continue;
          arrayOfChar[(i++)] = '.';
        }

        if (arrayOfByte[j] % 10 == 0)
        {
          i += _byteToChars((byte)(arrayOfByte[j] / 10), arrayOfChar, i);
        }
        else
        {
          _byteTo2Chars(arrayOfByte[j], arrayOfChar, i);
          i += 2;
        }
      }
      else
      {
        arrayOfChar[(i++)] = '0';
        arrayOfChar[(i++)] = '.';

        for (; i1 < 0; i1++)
        {
          arrayOfChar[(i++)] = '0';
          arrayOfChar[(i++)] = '0';
        }

        for (j = 1; j < m; j++)
        {
          _byteTo2Chars(arrayOfByte[j], arrayOfChar, i);
          i += 2;
        }

        if (arrayOfByte[j] % 10 == 0)
        {
          i += _byteToChars((byte)(arrayOfByte[j] / 10), arrayOfChar, i);
        }
        else
        {
          _byteTo2Chars(arrayOfByte[j], arrayOfChar, i);
          i += 2;
        }
      }
    }

    return new String(arrayOfChar, 0, i);
  }

  public static boolean toBoolean(byte[] paramArrayOfByte)
  {
    return !_isZero(paramArrayOfByte);
  }

  public static byte[] toBytes(double paramDouble)
    throws SQLException
  {
    if (Double.isNaN(paramDouble)) {
      throw new IllegalArgumentException(CoreException.getMessage(11));
    }

    if ((paramDouble == 0.0D) || (paramDouble == -0.0D)) {
      return _makeZero();
    }
    if (paramDouble == (1.0D / 0.0D)) {
      return _makePosInf();
    }

    if (paramDouble == (-1.0D / 0.0D)) {
      return _makeNegInf();
    }

    return _getThinLib().lnxren(paramDouble);
  }

  public static byte[] toBytes(float paramFloat)
  {
    if (Float.isNaN(paramFloat)) {
      throw new IllegalArgumentException(CoreException.getMessage(11));
    }

    if ((paramFloat == 0.0F) || (paramFloat == -0.0F)) {
      return _makeZero();
    }
    if (paramFloat == (1.0F / 1.0F)) {
      return _makePosInf();
    }
    if (paramFloat == (1.0F / -1.0F)) {
      return _makeNegInf();
    }

    String str = Float.toString(paramFloat);
    try
    {
      return _getLnxLib().lnxcpn(str, false, 0, false, 0, "AMERICAN_AMERICA");
    }
    catch (Exception localException) {
    }
    return null;
  }

  public static byte[] toBytes(long paramLong)
  {
    return _getLnxLib().lnxmin(paramLong);
  }

  public static byte[] toBytes(int paramInt)
  {
    return toBytes(paramInt);
  }

  public static byte[] toBytes(short paramShort)
  {
    return toBytes(paramShort);
  }

  public static byte[] toBytes(byte paramByte)
  {
    return toBytes(paramByte);
  }

  public static byte[] toBytes(BigInteger paramBigInteger)
    throws SQLException
  {
    byte[] arrayOfByte1 = new byte[66];
    long[] arrayOfLong1 = new long[54];
    long[] arrayOfLong2 = new long[22];
    int i = 21;
    int j = 0;

    int m = 21;

    int n = 0;

    int i2 = 0;

    boolean bool = true;

    if (paramBigInteger.signum() == 0)
    {
      return _makeZero();
    }
    byte[] arrayOfByte2;
    int i3;
    if (paramBigInteger.signum() == -1)
    {
      localObject = paramBigInteger.abs();
      bool = false;
      arrayOfByte2 = ((BigInteger)localObject).toByteArray();
      i3 = (int)Math.floor(((BigInteger)localObject).bitLength() * 0.150514997831991D);
    }
    else
    {
      arrayOfByte2 = paramBigInteger.toByteArray();
      i3 = (int)Math.floor(paramBigInteger.bitLength() * 0.150514997831991D);
    }

    if (paramBigInteger.compareTo(BIGINT_HUND.pow(i3)) < 0) {
      i3--;
    }
    if (arrayOfByte2.length > 54) {
      throw new SQLException(CoreException.getMessage(3));
    }

    for (int i4 = 0; i4 < arrayOfByte2.length; i4++)
    {
      if (arrayOfByte2[i4] < 0)
        arrayOfByte2[i4] += 256;
      else {
        arrayOfLong1[i4] = arrayOfByte2[i4];
      }
    }
    int k = arrayOfByte2.length;
    long l;
    switch (k % 3)
    {
    case 2:
      arrayOfLong2[i] = ((arrayOfLong1[j] << 8) + arrayOfLong1[(j + 1)]);
      j = (byte)(j + 2);
      k -= 2;
      break;
    case 1:
      arrayOfLong2[i] = arrayOfLong1[j];
      j = (byte)(j + 1);
      k--;
      break;
    default:
      l = (arrayOfLong1[j] << 16) + (arrayOfLong1[(j + 1)] << 8) + arrayOfLong1[(j + 2)];
      arrayOfLong2[i] = (l % 1000000L);
      arrayOfLong2[(i - 1)] = (l / 1000000L);
      m = (byte)(m - (arrayOfLong2[(i - 1)] != 0L ? 1 : 0));
      j = (byte)(j + 3);
      k -= 3;
    }

    while (k != 0)
    {
      l = (arrayOfLong1[j] << 4) + (arrayOfLong1[(j + 1)] >> 4);

      for (i = 21; i >= m; i = (byte)(i - 1))
      {
        l += (arrayOfLong2[i] << 12);
        arrayOfLong2[i] = (l % 1000000L);
        l /= 1000000L;
      }

      if (l != 0L)
      {
        m = (byte)(m - 1);
        arrayOfLong2[m] = l;
      }

      l = ((arrayOfLong1[(j + 1)] & 0xF) << 8) + arrayOfLong1[(j + 2)];

      for (i = 21; i >= m; i = (byte)(i - 1))
      {
        l += (arrayOfLong2[i] << 12);
        arrayOfLong2[i] = (l % 1000000L);
        l /= 1000000L;
      }

      if (l != 0L)
      {
        m = (byte)(m - 1);
        arrayOfLong2[m] = l;
      }

      j = (byte)(j + 3);
      k -= 3;
    }
    int i1;
    if ((arrayOfByte1[i2] = (byte)(int)(arrayOfLong2[m] / 10000L)) != 0)
    {
      i1 = 3 * (21 - m) + 3;
      arrayOfByte1[(i2 + 1)] = (byte)(int)(arrayOfLong2[m] % 10000L / 100L);
      arrayOfByte1[(i2 + 2)] = (byte)(int)(arrayOfLong2[m] % 100L);
      i2 += 3;
    }
    else if ((arrayOfByte1[i2] = (byte)(int)(arrayOfLong2[m] % 10000L / 100L)) != 0)
    {
      i1 = 3 * (21 - m) + 2;
      arrayOfByte1[(i2 + 1)] = (byte)(int)(arrayOfLong2[m] % 100L);
      i2 += 2;
    }
    else
    {
      arrayOfByte1[i2] = (byte)(int)arrayOfLong2[m];
      i1 = 3 * (21 - m) + 1;
      i2++;
    }

    for (i = (byte)(m + 1); i <= 21; i = (byte)(i + 1))
    {
      arrayOfByte1[i2] = (byte)(int)(arrayOfLong2[i] / 10000L);
      arrayOfByte1[(i2 + 1)] = (byte)(int)(arrayOfLong2[i] % 10000L / 100L);
      arrayOfByte1[(i2 + 2)] = (byte)(int)(arrayOfLong2[i] % 100L);
      i2 += 3;
    }

    for (i4 = i2 - 1; i4 >= 0; i4--)
    {
      if (arrayOfByte1[i4] != 0)
        break;
      i1--;
    }

    if (i1 > 19)
    {
      i4 = 20;
      i1 = 19;
      if (arrayOfByte1[i4] >= 50)
      {
        i4--;
        int tmp851_849 = i4;
        byte[] tmp851_848 = arrayOfByte1; tmp851_848[tmp851_849] = (byte)(tmp851_848[tmp851_849] + 1);
        while (arrayOfByte1[i4] == 100)
        {
          if (i4 == 0)
          {
            i3++;
            arrayOfByte1[i4] = 1;
            break;
          }
          arrayOfByte1[i4] = 0;
          i4--;
          int tmp893_891 = i4;
          byte[] tmp893_890 = arrayOfByte1; tmp893_890[tmp893_891] = (byte)(tmp893_890[tmp893_891] + 1);
        }

        for (i4 = i1 - 1; i4 >= 0; i4--)
        {
          if (arrayOfByte1[i4] != 0) break;
          i1--;
        }

      }

    }

    if (i3 > 62) {
      throw new SQLException(CoreException.getMessage(3));
    }

    int tmp893_891 = new byte[i1 + 1];
    tmp893_891[0] = (byte)i3;
    System.arraycopy(arrayOfByte1, 0, tmp893_891, 1, i1);

    return (B)_toLnxFmt(tmp893_891, bool);
  }

  public static byte[] toBytes(BigDecimal paramBigDecimal)
    throws SQLException
  {
    byte[] arrayOfByte1 = new byte[66];
    long[] arrayOfLong1 = new long[54];
    long[] arrayOfLong2 = new long[22];
    int i = 21;
    int j = 0;

    int m = 21;

    int i1 = 0;

    int i2 = 0;

    BigDecimal localBigDecimal1 = paramBigDecimal.abs();

    int i6 = 0;

    if (paramBigDecimal.signum() == 0)
    {
      return _makeZero();
    }

    boolean bool = paramBigDecimal.signum() != -1;

    int i4 = paramBigDecimal.scale();

    int i5 = localBigDecimal1.compareTo(BIGDEC_ONE);
    int i7 = 0;
    BigDecimal localBigDecimal2;
    if (i5 == -1)
    {
      do {
        i7++;
        localBigDecimal2 = localBigDecimal1.movePointRight(i7);
      }while (localBigDecimal2.compareTo(BIGDEC_ONE) < 0);

      i6 = -i7;
    }
    else
    {
      do {
        i7++;
        localBigDecimal2 = localBigDecimal1.movePointLeft(i7);
      }while (localBigDecimal2.compareTo(BIGDEC_ONE) >= 0);

      i6 = i7;
    }

    byte[] arrayOfByte2 = localBigDecimal1.movePointRight(i4).toBigInteger().toByteArray();

    if (arrayOfByte2.length > 54) {
      throw new SQLException(CoreException.getMessage(3));
    }

    for (int i3 = 0; i3 < arrayOfByte2.length; i3++)
    {
      if (arrayOfByte2[i3] < 0)
        arrayOfByte2[i3] += 256;
      else {
        arrayOfLong1[i3] = arrayOfByte2[i3];
      }
    }
    int k = arrayOfByte2.length;
    long l;
    switch (k % 3)
    {
    case 2:
      arrayOfLong2[i] = ((arrayOfLong1[j] << 8) + arrayOfLong1[(j + 1)]);
      j = (byte)(j + 2);
      k -= 2;
      break;
    case 1:
      arrayOfLong2[i] = arrayOfLong1[j];
      j = (byte)(j + 1);
      k--;
      break;
    default:
      l = (arrayOfLong1[j] << 16) + (arrayOfLong1[(j + 1)] << 8) + arrayOfLong1[(j + 2)];
      arrayOfLong2[i] = (l % 1000000L);
      arrayOfLong2[(i - 1)] = (l / 1000000L);
      m = (byte)(m - (arrayOfLong2[(i - 1)] != 0L ? 1 : 0));
      j = (byte)(j + 3);
      k -= 3;
    }

    while (k != 0)
    {
      l = (arrayOfLong1[j] << 4) + (arrayOfLong1[(j + 1)] >> 4);

      for (i = 21; i >= m; i = (byte)(i - 1))
      {
        l += (arrayOfLong2[i] << 12);
        arrayOfLong2[i] = (l % 1000000L);
        l /= 1000000L;
      }

      if (l != 0L)
      {
        m = (byte)(m - 1);
        arrayOfLong2[m] = l;
      }

      l = ((arrayOfLong1[(j + 1)] & 0xF) << 8) + arrayOfLong1[(j + 2)];

      for (i = 21; i >= m; i = (byte)(i - 1))
      {
        l += (arrayOfLong2[i] << 12);
        arrayOfLong2[i] = (l % 1000000L);
        l /= 1000000L;
      }

      if (l != 0L)
      {
        m = (byte)(m - 1);
        arrayOfLong2[m] = l;
      }

      j = (byte)(j + 3);
      k -= 3;
    }
    int n;
    if ((arrayOfByte1[i1] = (byte)(int)(arrayOfLong2[m] / 10000L)) != 0)
    {
      n = 3 * (21 - m) + 3;
      arrayOfByte1[(i1 + 1)] = (byte)(int)(arrayOfLong2[m] % 10000L / 100L);
      arrayOfByte1[(i1 + 2)] = (byte)(int)(arrayOfLong2[m] % 100L);
      i1 += 3;
    }
    else if ((arrayOfByte1[i1] = (byte)(int)(arrayOfLong2[m] % 10000L / 100L)) != 0)
    {
      n = 3 * (21 - m) + 2;
      arrayOfByte1[(i1 + 1)] = (byte)(int)(arrayOfLong2[m] % 100L);
      i1 += 2;
    }
    else
    {
      arrayOfByte1[i1] = (byte)(int)arrayOfLong2[m];
      n = 3 * (21 - m) + 1;
      i1++;
    }

    for (i = (byte)(m + 1); i <= 21; i = (byte)(i + 1))
    {
      arrayOfByte1[i1] = (byte)(int)(arrayOfLong2[i] / 10000L);
      arrayOfByte1[(i1 + 1)] = (byte)(int)(arrayOfLong2[i] % 10000L / 100L);
      arrayOfByte1[(i1 + 2)] = (byte)(int)(arrayOfLong2[i] % 100L);
      i1 += 3;
    }

    for (i3 = i1 - 1; i3 >= 0; i3--)
    {
      if (arrayOfByte1[i3] != 0)
        break;
      n--;
    }

    if ((i4 > 0) && ((i4 & 0x1) != 0))
    {
      int i8 = n;
      byte[] arrayOfByte4 = new byte[i8 + 1];

      if (arrayOfByte1[0] <= 9)
      {
        for (i3 = 0; i3 < i8 - 1; i3++)
        {
          arrayOfByte4[i3] = (byte)(arrayOfByte1[i3] % 10 * 10 + arrayOfByte1[(i3 + 1)] / 10);
        }
        arrayOfByte4[i3] = (byte)(arrayOfByte1[i3] % 10 * 10);
        if (arrayOfByte4[(i8 - 1)] == 0)
        {
          n--;
        }
      }
      else
      {
        arrayOfByte4[i8] = (byte)(arrayOfByte1[(i8 - 1)] % 10 * 10);
        for (i3 = i8 - 1; i3 > 0; i3--)
        {
          arrayOfByte4[i3] = (byte)(arrayOfByte1[i3] / 10 + arrayOfByte1[(i3 - 1)] % 10 * 10);
        }
        arrayOfByte4[i3] = (byte)(arrayOfByte1[i3] / 10);
        if (arrayOfByte4[i8] > 0)
        {
          n++;
        }
      }
      System.arraycopy(arrayOfByte4, 0, arrayOfByte1, 0, n);
    }

    if (n > 20)
    {
      i3 = 20;
      n = 20;
      if (arrayOfByte1[i3] >= 50)
      {
        i3--;
        int tmp1090_1088 = i3;
        byte[] tmp1090_1087 = arrayOfByte1; tmp1090_1087[tmp1090_1088] = (byte)(tmp1090_1087[tmp1090_1088] + 1);
        while (arrayOfByte1[i3] == 100)
        {
          if (i3 == 0)
          {
            i6++;
            arrayOfByte1[i3] = 1;
            break;
          }
          arrayOfByte1[i3] = 0;
          i3--;
          int tmp1132_1130 = i3;
          byte[] tmp1132_1129 = arrayOfByte1; tmp1132_1129[tmp1132_1130] = (byte)(tmp1132_1129[tmp1132_1130] + 1);
        }

        for (i3 = n - 1; i3 >= 0; i3--)
        {
          if (arrayOfByte1[i3] != 0) break;
          n--;
        }

      }

    }

    if (i6 <= 0)
    {
      if (arrayOfByte1[0] < 10)
        i2 = -(2 - i6) / 2 + 1;
      else {
        i2 = -(2 - i6) / 2;
      }
    }
    else {
      i2 = (i6 - 1) / 2;
    }

    if (i2 > 62) {
      throw new SQLException(CoreException.getMessage(3));
    }

    if (i2 <= -65) {
      throw new SQLException(CoreException.getMessage(2));
    }

    byte[] arrayOfByte3 = new byte[n + 1];
    arrayOfByte3[0] = (byte)i2;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte3, 1, n);

    return _toLnxFmt(arrayOfByte3, bool);
  }

  public static byte[] toBytes(String paramString, int paramInt)
    throws SQLException
  {
    int i = 0;
    int j = 0;

    byte[] arrayOfByte1 = new byte[22];

    int n = 0;

    boolean bool = true;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;

    int i5 = 0;
    int i6 = 0;
    int i7 = 40;

    int i8 = 0;
    int i9 = 0;
    int i10 = 0;

    int i12 = 0;

    paramString = paramString.trim();

    int i11 = paramString.length();

    if (paramString.charAt(0) == '-')
    {
      i11--;
      bool = false;
      i10 = 1;
    }

    i = i11;

    char[] arrayOfChar = new char[i11];
    paramString.getChars(i10, i11 + i10, arrayOfChar, 0);

    for (int k = 0; k < i11; k++)
    {
      if (arrayOfChar[k] != '.')
        continue;
      i2 = 1;
      break;
    }

    while ((j < i) && (arrayOfChar[j] == '0'))
    {
      j++;
      if (i2 == 1) {
        i12++;
      }
    }
    if (j == i)
      return _makeZero();
    int m;
    if ((i11 >= 2) && (arrayOfChar[j] == '.'))
    {
      while ((i > 0) && (arrayOfChar[(i - 1)] == '0')) i--;

      j++;

      if (j == i)
      {
        return _makeZero();
      }

      i3--;

      while ((j < i - 1) && (arrayOfChar[j] == '0') && (arrayOfChar[(j + 1)] == '0'))
      {
        i3--;
        i6 += 2;

        j += 2;
      }

      if (i3 <= -65) {
        throw new SQLException(CoreException.getMessage(2));
      }

      if (i - j > i7)
      {
        m = 2 + i7;

        if (i6 > 0) {
          m += i6;
        }
        if (m <= i)
          i = m;
        i8 = i;
        i1 = 1;
      }

      n = i - j >> 1;

      if ((i - j) % 2 != 0)
      {
        arrayOfByte1[n] = (byte)(Integer.parseInt(new String(arrayOfChar, i - 1, 1)) * 10);

        i5++;
        i--;
      }
    }
    int i13;
    while (i > j)
    {
      n--;
      arrayOfByte1[n] = (byte)Integer.parseInt(new String(arrayOfChar, i - 2, 2));

      i -= 2;
      i5++; continue;

      while ((paramInt > 0) && (i > 0) && (arrayOfChar[(i - 1)] == '0'))
      {
        i--;
        paramInt--;
      }

      if ((paramInt == 0) && (i > 1))
      {
        if (arrayOfChar[(i - 1)] == '.') {
          i--;
        }
        if (j == i) {
          return _makeZero();
        }

        while ((i > 1) && (arrayOfChar[(i - 2)] == '0') && (arrayOfChar[(i - 1)] == '0'))
        {
          i -= 2;
          i3++;
        }

      }

      if (i3 > 62) {
        throw new SQLException(CoreException.getMessage(3));
      }

      if (i - j - (i2 != 0 ? 1 : 0) > i7)
      {
        m = i7 + (i2 != 0 ? 1 : 0);
        i13 = i - m;
        i = m;
        paramInt -= i13;
        if (paramInt < 0)
          paramInt = 0;
        i1 = 1;
        i8 = i;
      }

      int i4 = paramInt == 0 ? i - j : i - paramInt - 1;

      if (i12 > 0) {
        i4 -= i12;
      }

      if (i4 % 2 != 0)
      {
        i9 = Integer.parseInt(new String(arrayOfChar, j, 1));
        j++;
        i4--;
        if (i - 1 == i7) {
          paramInt--;
          i--;
          i1 = 1;
          i8 = i;
        }
      }
      else
      {
        i9 = Integer.parseInt(new String(arrayOfChar, j, 2));
        j += 2;
        i4 -= 2;
      }

      arrayOfByte1[n] = (byte)i9;
      n++;
      i5++;

      while (i4 > 0)
      {
        arrayOfByte1[n] = (byte)Integer.parseInt(new String(arrayOfChar, j, 2));

        n++;
        j += 2;
        i3++;
        i4 -= 2;
        i5++;
      }

      if (j < i)
      {
        if (paramInt % 2 != 0)
        {
          n += paramInt / 2;
          arrayOfByte1[n] = (byte)(Integer.parseInt(new String(arrayOfChar, i - 1, 1)) * 10);

          i--;
          paramInt--;
        }
        else
        {
          n += paramInt / 2 - 1;
          arrayOfByte1[n] = (byte)Integer.parseInt(new String(arrayOfChar, i - 2, 2));

          i -= 2;
          paramInt -= 2;
        }

        i5++;
        n--;
      }

      while (paramInt > 0)
      {
        arrayOfByte1[n] = (byte)Integer.parseInt(new String(arrayOfChar, i - 2, 2));

        n--;
        i -= 2;
        paramInt -= 2;
        i5++;
      }

    }

    if (i1 != 0)
    {
      i13 = i5;
      i9 = Integer.parseInt(new String(arrayOfChar, i8, 1));
      if (i9 >= 5)
      {
        i13--;
        int tmp917_915 = i13;
        byte[] tmp917_913 = arrayOfByte1; tmp917_913[tmp917_915] = (byte)(tmp917_913[tmp917_915] + 1);
        while (arrayOfByte1[i13] == 100)
        {
          if (i13 == 0)
          {
            i3++;
            arrayOfByte1[i13] = 1;
            break;
          }
          arrayOfByte1[i13] = 0;
          i13--;
          int tmp963_961 = i13;
          byte[] tmp963_959 = arrayOfByte1; tmp963_959[tmp963_961] = (byte)(tmp963_959[tmp963_961] + 1);
        }

        for (k = i5 - 1; k >= 0; k--)
        {
          if (arrayOfByte1[k] != 0) break;
          i5--;
        }

      }

    }

    byte[] arrayOfByte2 = new byte[i5 + 1];
    arrayOfByte2[0] = (byte)i3;
    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 1, i5);

    return _toLnxFmt(arrayOfByte2, bool);
  }

  public static byte[] toBytes(boolean paramBoolean)
  {
    if (paramBoolean) {
      return toBytes(1L);
    }
    return toBytes(0L);
  }

  public byte[] toBytes()
  {
    return getBytes();
  }

  public double doubleValue()
  {
    return toDouble(shareBytes());
  }

  public float floatValue()
  {
    return toFloat(shareBytes());
  }

  public long longValue()
    throws SQLException
  {
    return toLong(shareBytes());
  }

  public int intValue()
    throws SQLException
  {
    return toInt(shareBytes());
  }

  public short shortValue()
    throws SQLException
  {
    return toShort(shareBytes());
  }

  public byte byteValue()
    throws SQLException
  {
    return toByte(shareBytes());
  }

  public BigInteger bigIntegerValue()
    throws SQLException
  {
    return toBigInteger(shareBytes());
  }

  public BigDecimal bigDecimalValue()
    throws SQLException
  {
    return toBigDecimal(shareBytes());
  }

  public String stringValue()
  {
    return toString(shareBytes());
  }

  public boolean booleanValue()
  {
    return toBoolean(shareBytes());
  }

  public Object toJdbc()
    throws SQLException
  {
    try
    {
      return bigDecimalValue();
    }
    catch (SQLException localSQLException) {
    }
    return new SQLException(localSQLException.getMessage());
  }

  public Object makeJdbcArray(int paramInt)
  {
    BigDecimal[] arrayOfBigDecimal = new BigDecimal[paramInt];

    return arrayOfBigDecimal;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    String str = paramClass.getName();

    return (str.compareTo("java.lang.Integer") == 0) || (str.compareTo("java.lang.Long") == 0) || (str.compareTo("java.lang.Float") == 0) || (str.compareTo("java.lang.Double") == 0) || (str.compareTo("java.math.BigInteger") == 0) || (str.compareTo("java.math.BigDecimal") == 0) || (str.compareTo("java.lang.String") == 0) || (str.compareTo("java.lang.Boolean") == 0);
  }

  public NUMBER abs()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxabs(shareBytes()));
  }

  public NUMBER acos()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxacos(shareBytes()));
  }

  public NUMBER add(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxadd(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER asin()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxasin(shareBytes()));
  }

  public NUMBER atan()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxatan(shareBytes()));
  }

  public NUMBER atan2(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxatan2(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER ceil()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxceil(shareBytes()));
  }

  public NUMBER cos()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxcos(shareBytes()));
  }

  public NUMBER cosh()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxcsh(shareBytes()));
  }

  public NUMBER decrement()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxdec(shareBytes()));
  }

  public NUMBER div(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxdiv(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER exp()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxexp(shareBytes()));
  }

  public NUMBER floatingPointRound(int paramInt)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxfpr(shareBytes(), paramInt));
  }

  public NUMBER floor()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxflo(shareBytes()));
  }

  public NUMBER increment()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxinc(shareBytes()));
  }

  public NUMBER ln()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxln(shareBytes()));
  }

  public NUMBER log(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxlog(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER mod(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxmod(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER mul(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxmul(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER negate()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxneg(shareBytes()));
  }

  public NUMBER pow(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxbex(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER pow(int paramInt)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxpow(shareBytes(), paramInt));
  }

  public NUMBER round(int paramInt)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxrou(shareBytes(), paramInt));
  }

  public NUMBER scale(int paramInt1, int paramInt2, boolean[] paramArrayOfBoolean)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxsca(shareBytes(), paramInt1, paramInt2, paramArrayOfBoolean));
  }

  public NUMBER shift(int paramInt)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxshift(shareBytes(), paramInt));
  }

  public NUMBER sin()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxsin(shareBytes()));
  }

  public NUMBER sinh()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxsnh(shareBytes()));
  }

  public NUMBER sqroot()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxsqr(shareBytes()));
  }

  public NUMBER sub(NUMBER paramNUMBER)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxsub(shareBytes(), paramNUMBER.shareBytes()));
  }

  public NUMBER tan()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxtan(shareBytes()));
  }

  public NUMBER tanh()
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxtnh(shareBytes()));
  }

  public NUMBER truncate(int paramInt)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxtru(shareBytes(), paramInt));
  }

  public static NUMBER formattedTextToNumber(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxfcn(paramString1, paramString2, paramString3));
  }

  public static NUMBER textToPrecisionNumber(String paramString1, boolean paramBoolean1, int paramInt1, boolean paramBoolean2, int paramInt2, String paramString2)
    throws SQLException
  {
    return new NUMBER(_getLnxLib().lnxcpn(paramString1, paramBoolean1, paramInt1, paramBoolean2, paramInt2, paramString2));
  }

  public String toFormattedText(String paramString1, String paramString2)
    throws SQLException
  {
    return _getLnxLib().lnxnfn(shareBytes(), paramString1, paramString2);
  }

  public String toText(int paramInt, String paramString)
    throws SQLException
  {
    return _getLnxLib().lnxnuc(shareBytes(), paramInt, paramString);
  }

  public int compareTo(NUMBER paramNUMBER)
  {
    return compareBytes(shareBytes(), paramNUMBER.shareBytes());
  }

  public boolean isInf()
  {
    return _isInf(shareBytes());
  }

  public boolean isNegInf()
  {
    return _isNegInf(shareBytes());
  }

  public boolean isPosInf()
  {
    return _isPosInf(shareBytes());
  }

  public boolean isInt()
  {
    return _isInt(shareBytes());
  }

  public static boolean isValid(byte[] paramArrayOfByte)
  {
    int i = (byte)paramArrayOfByte.length;
    int j;
    if (_isPositive(paramArrayOfByte))
    {
      if (i == 1) {
        return _isZero(paramArrayOfByte);
      }

      if ((paramArrayOfByte[0] == -1) && (paramArrayOfByte[1] == 101))
      {
        return i == 2;
      }
      if (i > 21) {
        return false;
      }

      if ((paramArrayOfByte[1] < 2) || (paramArrayOfByte[(i - 1)] < 2)) {
        return false;
      }

      for (k = 1; k < i; k++)
      {
        j = paramArrayOfByte[k];
        if ((j < 1) || (j > 100)) {
          return false;
        }
      }
      return true;
    }

    if (i < 3) {
      return _isNegInf(paramArrayOfByte);
    }
    if (i > 21) {
      return false;
    }

    if (paramArrayOfByte[(i - 1)] != 102)
    {
      if (i <= 20) {
        return false;
      }
    }
    else {
      i = (byte)(i - 1);
    }

    if ((paramArrayOfByte[1] > 100) || (paramArrayOfByte[(i - 1)] > 100)) {
      return false;
    }

    for (int k = 1; k < i; k++)
    {
      j = paramArrayOfByte[k];

      if ((j < 2) || (j > 101)) {
        return false;
      }
    }
    return true;
  }

  public boolean isZero()
  {
    return _isZero(shareBytes());
  }

  public static NUMBER e()
  {
    return new NUMBER(E);
  }

  public static NUMBER ln10()
  {
    return new NUMBER(LN10);
  }

  public static NUMBER negInf()
  {
    return new NUMBER(_makeNegInf());
  }

  public static NUMBER pi()
  {
    return new NUMBER(PI);
  }

  public static NUMBER posInf()
  {
    return new NUMBER(_makePosInf());
  }

  public static NUMBER zero()
  {
    return new NUMBER(_makeZero());
  }

  public int sign()
  {
    if (_isZero(shareBytes())) {
      return 0;
    }
    return _isPositive(shareBytes()) ? 1 : -1;
  }

  static boolean _isInf(byte[] paramArrayOfByte)
  {
    return ((paramArrayOfByte.length == 2) && (paramArrayOfByte[0] == -1) && (paramArrayOfByte[1] == 101)) || ((paramArrayOfByte[0] == 0) && (paramArrayOfByte.length == 1));
  }

  private static boolean _isInt(byte[] paramArrayOfByte)
  {
    if (_isZero(paramArrayOfByte)) {
      return true;
    }
    if (_isInf(paramArrayOfByte)) {
      return false;
    }
    byte[] arrayOfByte = _fromLnxFmt(paramArrayOfByte);
    int i = arrayOfByte[0];
    int j = (byte)(arrayOfByte.length - 1);

    return j <= i + 1;
  }

  static boolean _isNegInf(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == 0) && (paramArrayOfByte.length == 1);
  }

  static boolean _isPosInf(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte.length == 2) && (paramArrayOfByte[0] == -1) && (paramArrayOfByte[1] == 101);
  }

  static boolean _isPositive(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] & 0xFFFFFF80) != 0;
  }

  static boolean _isZero(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] == -128) && (paramArrayOfByte.length == 1);
  }

  static byte[] _makePosInf()
  {
    byte[] arrayOfByte = new byte[2];

    arrayOfByte[0] = -1;
    arrayOfByte[1] = 101;

    return arrayOfByte;
  }

  static byte[] _makeNegInf()
  {
    byte[] arrayOfByte = new byte[1];

    arrayOfByte[0] = 0;

    return arrayOfByte;
  }

  static byte[] _makeZero()
  {
    byte[] arrayOfByte = new byte[1];

    arrayOfByte[0] = -128;

    return arrayOfByte;
  }

  static byte[] _fromLnxFmt(byte[] paramArrayOfByte)
  {
    int j = paramArrayOfByte.length;
    byte[] arrayOfByte;
    if (_isPositive(paramArrayOfByte))
    {
      arrayOfByte = new byte[j];
      arrayOfByte[0] = (byte)((paramArrayOfByte[0] & 0xFFFFFF7F) - 65);

      for (i = 1; i < j; i++) {
        arrayOfByte[i] = (byte)(paramArrayOfByte[i] - 1);
      }
    }

    if ((j - 1 == 20) && (paramArrayOfByte[(j - 1)] != 102))
    {
      arrayOfByte = new byte[j];
    }
    else arrayOfByte = new byte[j - 1];

    arrayOfByte[0] = (byte)(((paramArrayOfByte[0] ^ 0xFFFFFFFF) & 0xFFFFFF7F) - 65);

    for (int i = 1; i < arrayOfByte.length; i++) {
      arrayOfByte[i] = (byte)(101 - paramArrayOfByte[i]);
    }

    return arrayOfByte;
  }

  static byte[] _toLnxFmt(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    int j = paramArrayOfByte.length;
    byte[] arrayOfByte;
    if (paramBoolean)
    {
      arrayOfByte = new byte[j];
      arrayOfByte[0] = (byte)(paramArrayOfByte[0] + 128 + 64 + 1);

      for (i = 1; i < j; i++) {
        arrayOfByte[i] = (byte)(paramArrayOfByte[i] + 1);
      }

    }

    if (j - 1 < 20)
      arrayOfByte = new byte[j + 1];
    else {
      arrayOfByte = new byte[j];
    }
    arrayOfByte[0] = (byte)(paramArrayOfByte[0] + 128 + 64 + 1 ^ 0xFFFFFFFF);

    for (int i = 1; i < j; i++) {
      arrayOfByte[i] = (byte)(101 - paramArrayOfByte[i]);
    }
    if (i <= 20) {
      arrayOfByte[i] = 102;
    }

    return arrayOfByte;
  }

  private static LnxLib _getLnxLib()
  {
    if (_slnxlib == null)
    {
      try
      {
        if (System.getProperty("oracle.jserver.version") != null)
        {
          _slnxlib = new LnxLibServer();
        }
        else
        {
          _slnxlib = new LnxLibThin();
        }
      }
      catch (SecurityException localSecurityException)
      {
        _slnxlib = new LnxLibThin();
      }
    }

    return _slnxlib;
  }

  private static LnxLib _getThinLib()
  {
    if (_thinlib == null)
    {
      _thinlib = new LnxLibThin();
    }

    return _thinlib;
  }

  private static int _byteToChars(byte paramByte, char[] paramArrayOfChar, int paramInt)
  {
    if (paramByte < 0)
    {
      return 0;
    }
    if (paramByte < 10)
    {
      paramArrayOfChar[paramInt] = (char)(48 + paramByte);
      return 1;
    }
    if (paramByte < 100)
    {
      paramArrayOfChar[paramInt] = (char)(48 + paramByte / 10);
      paramArrayOfChar[(paramInt + 1)] = (char)(48 + paramByte % 10);
      return 2;
    }

    paramArrayOfChar[paramInt] = '1';
    paramArrayOfChar[(paramInt + 1)] = (char)(48 + paramByte / 10 - 10);
    paramArrayOfChar[(paramInt + 2)] = (char)(48 + paramByte % 10);
    return 3;
  }

  private static void _byteTo2Chars(byte paramByte, char[] paramArrayOfChar, int paramInt)
  {
    if (paramByte < 0)
    {
      paramArrayOfChar[paramInt] = '0';
      paramArrayOfChar[(paramInt + 1)] = '0';
    }
    else if (paramByte < 10)
    {
      paramArrayOfChar[paramInt] = '0';
      paramArrayOfChar[(paramInt + 1)] = (char)(48 + paramByte);
    }
    else if (paramByte < 100)
    {
      paramArrayOfChar[paramInt] = (char)(48 + paramByte / 10);
      paramArrayOfChar[(paramInt + 1)] = (char)(48 + paramByte % 10);
    }
    else
    {
      paramArrayOfChar[paramInt] = '0';
      paramArrayOfChar[(paramInt + 1)] = '0';
    }
  }

  private static void _printBytes(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length;

    System.out.print(i + ": ");

    for (int j = 0; j < i; j++)
    {
      System.out.print(paramArrayOfByte[j] + " ");
    }

    System.out.println();
  }

  private byte[] stringToBytes(String paramString)
    throws SQLException
  {
    int i = 0;

    paramString = paramString.trim();

    if (paramString.indexOf('.') >= 0)
    {
      i = paramString.length() - 1 - paramString.indexOf('.');
    }

    return toBytes(paramString, i);
  }

  static
  {
    try
    {
      drvType = System.getProperty("oracle.jserver.version");
    }
    catch (SecurityException localSecurityException)
    {
      drvType = null;
    }

    LANGID = "AMERICAN";
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.NUMBER
 * JD-Core Version:    0.6.0
 */