package oracle.jdbc.driver;

import java.sql.Time;

class TimeBinder extends DateCommonBinder
{
  Binder theTimeCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimeCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 12;
    paramBinder.bytelen = 7;
  }

  TimeBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theTimeCopyingBinder;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    Time[] arrayOfTime = paramOraclePreparedStatement.parameterTime[paramInt3];
    Time localTime = arrayOfTime[paramInt1];

    if (paramBoolean) {
      arrayOfTime[paramInt1] = null;
    }
    if (localTime == null)
    {
      paramArrayOfShort[paramInt9] = -1;
    }
    else
    {
      paramArrayOfShort[paramInt9] = 0;

      setOracleHMS(setOracleCYMD(localTime.getTime(), paramArrayOfByte, paramInt6, paramOraclePreparedStatement), paramArrayOfByte, paramInt6);

      paramArrayOfShort[paramInt8] = (short)paramInt4;
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TimeBinder
 * JD-Core Version:    0.6.0
 */