package oracle.jdbc.driver;

import java.util.TimeZone;

abstract class DateCommonBinder extends Binder
{
  static final int GREGORIAN_CUTOVER_YEAR = 1582;
  static final long GREGORIAN_CUTOVER = -12219292800000L;
  static final int JAN_1_1_JULIAN_DAY = 1721426;
  static final int EPOCH_JULIAN_DAY = 2440588;
  static final int ONE_SECOND = 1000;
  static final int ONE_MINUTE = 60000;
  static final int ONE_HOUR = 3600000;
  static final long ONE_DAY = 86400000L;
  static final int[] NUM_DAYS = { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };

  static final int[] LEAP_NUM_DAYS = { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };

  static final int[] MONTH_LENGTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

  static final int[] LEAP_MONTH_LENGTH = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
  static final int ORACLE_DATE_CENTURY = 0;
  static final int ORACLE_DATE_YEAR = 1;
  static final int ORACLE_DATE_MONTH = 2;
  static final int ORACLE_DATE_DAY = 3;
  static final int ORACLE_DATE_HOUR = 4;
  static final int ORACLE_DATE_MIN = 5;
  static final int ORACLE_DATE_SEC = 6;
  static final int ORACLE_DATE_NANO1 = 7;
  static final int ORACLE_DATE_NANO2 = 8;
  static final int ORACLE_DATE_NANO3 = 9;
  static final int ORACLE_DATE_NANO4 = 10;

  static final long floorDivide(long paramLong1, long paramLong2)
  {
    return paramLong1 >= 0L ? paramLong1 / paramLong2 : (paramLong1 + 1L) / paramLong2 - 1L;
  }

  static final int floorDivide(int paramInt1, int paramInt2)
  {
    return paramInt1 >= 0 ? paramInt1 / paramInt2 : (paramInt1 + 1) / paramInt2 - 1;
  }

  static final int floorDivide(int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (paramInt1 >= 0)
    {
      paramArrayOfInt[0] = (paramInt1 % paramInt2);

      return paramInt1 / paramInt2;
    }

    int i = (paramInt1 + 1) / paramInt2 - 1;

    paramArrayOfInt[0] = (paramInt1 - i * paramInt2);

    return i;
  }

  static final int floorDivide(long paramLong, int paramInt, int[] paramArrayOfInt)
  {
    if (paramLong >= 0L)
    {
      paramArrayOfInt[0] = (int)(paramLong % paramInt);

      return (int)(paramLong / paramInt);
    }

    int i = (int)((paramLong + 1L) / paramInt - 1L);

    paramArrayOfInt[0] = (int)(paramLong - i * paramInt);

    return i;
  }

  static final long zoneOffset(TimeZone paramTimeZone, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    return paramTimeZone.getOffset(paramInt1 < 0 ? 0 : 1, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5);
  }

  static void setOracleNanos(long paramLong, byte[] paramArrayOfByte, int paramInt)
  {
    paramArrayOfByte[(10 + paramInt)] = (byte)(int)(paramLong & 0xFF);
    paramArrayOfByte[(9 + paramInt)] = (byte)(int)(paramLong >> 8 & 0xFF);
    paramArrayOfByte[(8 + paramInt)] = (byte)(int)(paramLong >> 16 & 0xFF);
    paramArrayOfByte[(7 + paramInt)] = (byte)(int)(paramLong >> 24 & 0xFF);
  }

  static void setOracleHMS(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
  {
    paramInt1 /= 1000;
    paramArrayOfByte[(6 + paramInt2)] = (byte)(paramInt1 % 60 + 1);
    paramInt1 /= 60;
    paramArrayOfByte[(5 + paramInt2)] = (byte)(paramInt1 % 60 + 1);
    paramInt1 /= 60;
    paramArrayOfByte[(4 + paramInt2)] = (byte)(paramInt1 + 1);
  }

  static int setOracleCYMD(long paramLong, byte[] paramArrayOfByte, int paramInt, OraclePreparedStatement paramOraclePreparedStatement)
  {
    TimeZone localTimeZone = paramOraclePreparedStatement.getDefaultTimeZone();

    int i2 = localTimeZone.getRawOffset();
    long l1 = paramLong + i2;
    long l2;
    int n;
    int i;
    int i1;
    int m;
    if (l1 >= -12219292800000L)
    {
      l2 = 2440588L + floorDivide(l1, 86400000L) - 1721426L;
      int i5;
      int i6;
      int i8;
      if (l2 > 0L)
      {
        i5 = (int)(l2 / 146097L);
        n = (int)(l2 % 146097L);
        i6 = n / 36524;
        n %= 36524;
        i7 = n / 1461;
        n %= 1461;
        i8 = n / 365;
        n %= 365;
      }
      else
      {
        int[] arrayOfInt = new int[1];

        i5 = floorDivide(l2, 146097, arrayOfInt);
        i6 = floorDivide(arrayOfInt[0], 36524, arrayOfInt);
        i7 = floorDivide(arrayOfInt[0], 1461, arrayOfInt);
        i8 = floorDivide(arrayOfInt[0], 365, arrayOfInt);
        n = arrayOfInt[0];
      }

      i = 400 * i5 + 100 * i6 + 4 * i7 + i8;

      if ((i6 == 4) || (i8 == 4))
      {
        n = 365;
      }
      else
      {
        i++;
      }

      i1 = ((i & 0x3) == 0) && ((i % 100 != 0) || (i % 400 == 0)) ? 1 : 0;

      m = (int)((l2 + 1L) % 7L);
    }
    else
    {
      l2 = 2440588L + floorDivide(l1, 86400000L) - 1721424L;

      i = (int)floorDivide(4L * l2 + 1464L, 1461L);

      l3 = 365 * (i - 1) + floorDivide(i - 1, 4);

      n = (int)(l2 - l3);
      i1 = (i & 0x3) == 0 ? 1 : 0;

      m = (int)((l2 - 1L) % 7L);
    }

    int i3 = 0;
    int i4 = i1 != 0 ? 60 : 59;

    if (n >= i4)
    {
      i3 = i1 != 0 ? 1 : 2;
    }

    int j = (12 * (n + i3) + 6) / 367;
    int k = n - (i1 != 0 ? LEAP_NUM_DAYS[j] : NUM_DAYS[j]) + 1;

    m += (m < 0 ? 8 : 1);

    long l3 = l1 / 86400000L;
    int i7 = (int)(l1 - l3 * 86400000L);

    if (i7 < 0) {
      i7 = (int)(i7 + 86400000L);
    }

    long l4 = zoneOffset(localTimeZone, i, j, k, m, i7) - i2;

    i7 = (int)(i7 + l4);

    if (i7 >= 86400000L)
    {
      i7 = (int)(i7 - 86400000L);

      k++; if (k > (i1 != 0 ? LEAP_MONTH_LENGTH[j] : MONTH_LENGTH[j]))
      {
        k = 1;

        j++; if (j == 12)
        {
          j = 0;
          i++;
        }
      }

    }

    if (i <= 0) {
      i--;
    }
    paramArrayOfByte[(0 + paramInt)] = (byte)(i / 100 + 100);
    paramArrayOfByte[(1 + paramInt)] = (byte)(i % 100 + 100);
    paramArrayOfByte[(2 + paramInt)] = (byte)(j + 1);
    paramArrayOfByte[(3 + paramInt)] = (byte)k;

    return i7;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.DateCommonBinder
 * JD-Core Version:    0.6.0
 */