package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.core.lmx.CoreException;

class DoubleBinder extends VarnumBinder
{
  char[] digits = new char[20];

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
    throws SQLException
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int i = paramInt6 + 1;
    double d = paramOraclePreparedStatement.parameterDouble[paramInt3][paramInt1];
    int j = 0;

    if (d == 0.0D)
    {
      arrayOfByte[i] = -128;
      j = 1;
    }
    else if (d == (1.0D / 0.0D))
    {
      arrayOfByte[i] = -1;
      arrayOfByte[(i + 1)] = 101;
      j = 2;
    }
    else if (d == (-1.0D / 0.0D))
    {
      arrayOfByte[i] = 0;
      j = 1;
    }
    else
    {
      boolean bool = d < 0.0D;

      if (bool) {
        d = -d;
      }
      long l1 = Double.doubleToLongBits(d);
      int k = (int)(l1 >> 52 & 0x7FF);
      int m = (k > 1023 ? 126 : 127) - (int)((k - 1023) / 6.643856189774725D);
      SQLException localSQLException;
      if (m < 0)
      {
        localSQLException = new SQLException(CoreException.getMessage(3) + " trying to bind " + d);

        throw localSQLException;
      }

      if (m > 192)
      {
        localSQLException = new SQLException(CoreException.getMessage(2) + " trying to bind " + d);

        throw localSQLException;
      }

      if (d > factorTable[m]) {
        for (; m > 0; d <= factorTable[m]) m--;
      }
      while ((m < 193) && (d <= factorTable[(m + 1)])) {
        m++;
      }
      if (d == factorTable[m])
      {
        if (m < 65)
        {
          localSQLException = new SQLException(CoreException.getMessage(3) + " trying to bind " + d);

          throw localSQLException;
        }

        if (m > 192)
        {
          localSQLException = new SQLException(CoreException.getMessage(2) + " trying to bind " + d);

          throw localSQLException;
        }

        if (bool)
        {
          arrayOfByte[i] = (byte)(62 - (127 - m));
          arrayOfByte[(i + 1)] = 100;
          arrayOfByte[(i + 2)] = 102;
          j = 3;
        }
        else
        {
          arrayOfByte[i] = (byte)(192 + (128 - m));
          arrayOfByte[(i + 1)] = 2;
          j = 2;
        }

      }
      else
      {
        if (m < 64)
        {
          localSQLException = new SQLException(CoreException.getMessage(3) + " trying to bind " + d);

          throw localSQLException;
        }

        if (m > 191)
        {
          localSQLException = new SQLException(CoreException.getMessage(2) + " trying to bind " + d);

          throw localSQLException;
        }

        long l2 = bool ? l1 & 0xFFFFFFFF : l1;
        long l3 = l2 & 0xFFFFFFFF;
        int n = k;

        char[] arrayOfChar = paramOraclePreparedStatement.digits;
        int i1;
        if (n == 0)
        {
          while ((l3 & 0x0) == 0L)
          {
            l3 <<= 1;
            n--;
          }

          i1 = 53 + n;
          n++;
        }
        else
        {
          l3 |= 4503599627370496L;
          i1 = 53;
        }

        n -= 1023;

        j = dtoa(arrayOfByte, i, d, bool, false, arrayOfChar, n, l3, i1);
      }

    }

    arrayOfByte[paramInt6] = (byte)j;
    paramArrayOfShort[paramInt9] = 0;
    paramArrayOfShort[paramInt8] = (short)(j + 1);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.DoubleBinder
 * JD-Core Version:    0.6.0
 */