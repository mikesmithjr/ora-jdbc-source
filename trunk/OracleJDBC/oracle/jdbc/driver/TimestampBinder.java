package oracle.jdbc.driver;

import java.sql.Timestamp;

class TimestampBinder extends DateCommonBinder
{
  Binder theTimestampCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimestampCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 180;
    paramBinder.bytelen = 11;
  }

  TimestampBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theTimestampCopyingBinder;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
  {
    Timestamp[] arrayOfTimestamp = paramOraclePreparedStatement.parameterTimestamp[paramInt3];
    Timestamp localTimestamp = arrayOfTimestamp[paramInt1];

    if (paramBoolean) {
      arrayOfTimestamp[paramInt1] = null;
    }
    if (localTimestamp == null)
    {
      paramArrayOfShort[paramInt9] = -1;
    }
    else
    {
      paramArrayOfShort[paramInt9] = 0;

      setOracleHMS(setOracleCYMD(localTimestamp.getTime(), paramArrayOfByte, paramInt6, paramOraclePreparedStatement), paramArrayOfByte, paramInt6);

      int i = localTimestamp.getNanos();

      if (i != 0)
      {
        setOracleNanos(i, paramArrayOfByte, paramInt6);

        paramArrayOfShort[paramInt8] = (short)paramInt4;
      }
      else
      {
        paramArrayOfShort[paramInt8] = 7;
      }
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TimestampBinder
 * JD-Core Version:    0.6.0
 */