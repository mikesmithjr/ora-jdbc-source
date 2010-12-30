package oracle.jdbc.driver;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.core.lmx.CoreException;

class BigDecimalBinder extends VarnumBinder
{
  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
    throws SQLException
  {
    byte[] arrayOfByte = paramArrayOfByte;
    int i = paramInt6 + 1;
    BigDecimal localBigDecimal1 = paramOraclePreparedStatement.parameterBigDecimal[paramInt3][paramInt1];
    int j = 0;

    Object localObject1 = localBigDecimal1.toString();
    int k;
    String str6;
    if ((k = ((String)localObject1).indexOf("E")) != -1)
    {
      Object localObject2 = "";
      str2 = 0;
      BigDecimal localBigDecimal2 = null;
      str4 = ((String)localObject1).substring(k + 1);
      String str5 = ((String)localObject1).substring(0, k);
      localBigDecimal2 = new BigDecimal(str5);
      str6 = str4.charAt(0) == '-' ? 1 : 0;
      str4 = str4.substring(1);
      str2 = Integer.parseInt(str4);

      String str7 = localBigDecimal2.toString().indexOf(".");
      if (str7 != -1)
      {
        double d = Math.pow(10.0D, str7);
        localBigDecimal2 = localBigDecimal2.multiply(new BigDecimal(d));
        str2 -= str7;
      } else if (str6 != 0) {
        str2--;
      }
      String str8 = localBigDecimal2.toString();
      str8 = str8.substring(0, str8.length() - (str7 + 1));
      localObject2 = str6 != 0 ? "0." : str8;

      for (String str9 = 0; str9 < str2; str9++) {
        localObject2 = (String)localObject2 + "0";
      }
      if (str6 != 0) {
        localObject2 = (String)localObject2 + str8;
      }
      localObject1 = localObject2;
    }
    String str1 = ((String)localObject1).length();
    String str2 = ((String)localObject1).indexOf('.');
    String str3 = ((String)localObject1).charAt(0) == '-' ? 1 : 0;
    String str4 = str3;

    int i1 = 2;
    int i2 = str1;

    if (str2 == -1)
      str2 = str1;
    else if ((str1 - str2 & 0x1) != 0)
      int i3 = str1 + 1;
    int m;
    while ((str4 < str1) && (((m = ((String)localObject1).charAt(str4)) < '1') || (m > 57))) {
      str4++;
    }
    if (str4 >= str1)
    {
      arrayOfByte[i] = -128;
      j = 1;
    }
    else
    {
      if (str4 < str2)
        str6 = 2 - (str2 - str4 & 0x1);
      else {
        str6 = 1 + (str4 - str2 & 0x1);
      }
      int n = (str2 - str4 - 1) / 2;
      SQLException localSQLException;
      if (n > 62)
      {
        localSQLException = new SQLException(CoreException.getMessage(3) + " trying to bind " + localBigDecimal1);

        throw localSQLException;
      }

      if (n < -65)
      {
        localSQLException = new SQLException(CoreException.getMessage(2) + " trying to bind " + localBigDecimal1);

        throw localSQLException;
      }

      String str10 = str4 + str6 + 38;
      String str11;
      if (str10 > str1) {
        str11 = str1;
      }
      for (String str12 = str4 + str6; str12 < str11; str12 += 2)
      {
        if (str12 == str2)
        {
          str12--;

          if (str11 < str1) {
            str11++;
          }
        }
        else
        {
          if ((((String)localObject1).charAt(str12) == '0') && ((str12 + 1 >= str1) || (((String)localObject1).charAt(str12 + 1) == '0'))) {
            continue;
          }
          i1 = (str12 - str4 - str6) / 2 + 3;
        }
      }

      int i4 = i + 2;

      String str13 = str4 + str6;

      if (str3 == 0)
      {
        arrayOfByte[i] = (byte)(192 + n + 1);
        i5 = ((String)localObject1).charAt(str4) - '0';

        if (str6 == 2) {
          i5 = i5 * 10 + (str4 + 1 < str1 ? ((String)localObject1).charAt(str4 + 1) - '0' : 0);
        }

        arrayOfByte[(i + 1)] = (byte)(i5 + 1);

        while (i4 < i + i1)
        {
          if (str13 == str2) {
            str13++;
          }
          i5 = (((String)localObject1).charAt(str13) - '0') * 10;

          if (str13 + 1 < str1) {
            i5 += ((String)localObject1).charAt(str13 + 1) - '0';
          }
          arrayOfByte[(i4++)] = (byte)(i5 + 1);
          str13 += 2;
        }

      }

      arrayOfByte[i] = (byte)(62 - n);

      int i5 = ((String)localObject1).charAt(str4) - '0';

      if (str6 == 2) {
        i5 = i5 * 10 + (str4 + 1 < str1 ? ((String)localObject1).charAt(str4 + 1) - '0' : 0);
      }

      arrayOfByte[(i + 1)] = (byte)(101 - i5);

      while (i4 < i + i1)
      {
        if (str13 == str2) {
          str13++;
        }
        i5 = (((String)localObject1).charAt(str13) - '0') * 10;

        if (str13 + 1 < str1) {
          i5 += ((String)localObject1).charAt(str13 + 1) - '0';
        }
        arrayOfByte[(i4++)] = (byte)(101 - i5);
        str13 += 2;
      }

      if (i1 < 21) {
        arrayOfByte[(i + i1++)] = 102;
      }

      j = i1;
    }

    arrayOfByte[paramInt6] = (byte)j;
    paramArrayOfShort[paramInt9] = 0;
    paramArrayOfShort[paramInt8] = (short)(j + 1);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BigDecimalBinder
 * JD-Core Version:    0.6.0
 */