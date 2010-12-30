package oracle.jdbc.driver;

import java.sql.Date;

class DateBinder extends DateCommonBinder
{
  Binder theDateCopyingBinder = OraclePreparedStatementReadOnly.theStaticDateCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 12;
    paramBinder.bytelen = 7;
  }

  DateBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theDateCopyingBinder;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    Date[] arrayOfDate = paramOraclePreparedStatement.parameterDate[paramInt3];
    Date localDate = arrayOfDate[paramInt1];

    if (paramBoolean) {
      arrayOfDate[paramInt1] = null;
    }
    if (localDate == null)
    {
      paramArrayOfShort[paramInt9] = -1;
    }
    else
    {
      paramArrayOfShort[paramInt9] = 0;

      long l = setOracleCYMD(localDate.getTime(), paramArrayOfByte, paramInt6, paramOraclePreparedStatement);

      paramArrayOfByte[(6 + paramInt6)] = 1;
      paramArrayOfByte[(5 + paramInt6)] = 1;
      paramArrayOfByte[(4 + paramInt6)] = 1;

      paramArrayOfShort[paramInt8] = (short)paramInt4;
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.DateBinder
 * JD-Core Version:    0.6.0
 */