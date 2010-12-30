package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.sql.Datum;

class PlsqlIbtBinder extends Binder
{
  Binder thePlsqlIbtCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtCopyingBinder;

  PlsqlIbtBinder()
  {
    init(this);
  }

  static void init(Binder paramBinder)
  {
    paramBinder.type = 998;
  }

  void bind(OraclePreparedStatement paramOraclePreparedStatement, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean)
    throws SQLException
  {
    PlsqlIbtBindInfo localPlsqlIbtBindInfo = paramOraclePreparedStatement.parameterPlsqlIbt[paramInt3][paramInt1];

    if (paramBoolean) {
      paramOraclePreparedStatement.parameterPlsqlIbt[paramInt3][paramInt1] = null;
    }
    int i = localPlsqlIbtBindInfo.ibtValueIndex;
    int j;
    switch (localPlsqlIbtBindInfo.element_internal_type)
    {
    case 9:
      for (j = 0; j < localPlsqlIbtBindInfo.curLen; )
      {
        int k = 0;
        String str = (String)localPlsqlIbtBindInfo.arrayData[j];

        if (str != null)
        {
          k = str.length();

          if (k > localPlsqlIbtBindInfo.elemMaxLen - 1) {
            k = localPlsqlIbtBindInfo.elemMaxLen - 1;
          }
          str.getChars(0, k, paramOraclePreparedStatement.ibtBindChars, i + 1);

          paramOraclePreparedStatement.ibtBindIndicators[(localPlsqlIbtBindInfo.ibtIndicatorIndex + j)] = 0;
          k <<= 1;
          paramOraclePreparedStatement.ibtBindChars[i] = (char)k;

          paramOraclePreparedStatement.ibtBindIndicators[(localPlsqlIbtBindInfo.ibtLengthIndex + j)] = (k == 0 ? 3 : (short)(k + 2));
        }
        else
        {
          paramOraclePreparedStatement.ibtBindIndicators[(localPlsqlIbtBindInfo.ibtIndicatorIndex + j)] = -1;
        }
        i += localPlsqlIbtBindInfo.elemMaxLen;

        j++; continue;

        for (j = 0; j < localPlsqlIbtBindInfo.curLen; )
        {
          byte[] arrayOfByte = null;

          if (localPlsqlIbtBindInfo.arrayData[j] != null) {
            arrayOfByte = ((Datum)localPlsqlIbtBindInfo.arrayData[j]).getBytes();
          }
          if (arrayOfByte == null)
          {
            paramOraclePreparedStatement.ibtBindIndicators[(localPlsqlIbtBindInfo.ibtIndicatorIndex + j)] = -1;
          }
          else
          {
            paramOraclePreparedStatement.ibtBindIndicators[(localPlsqlIbtBindInfo.ibtIndicatorIndex + j)] = 0;
            paramOraclePreparedStatement.ibtBindIndicators[(localPlsqlIbtBindInfo.ibtLengthIndex + j)] = (short)(arrayOfByte.length + 1);

            paramOraclePreparedStatement.ibtBindBytes[i] = (byte)arrayOfByte.length;

            System.arraycopy(arrayOfByte, 0, paramOraclePreparedStatement.ibtBindBytes, i + 1, arrayOfByte.length);
          }

          i += localPlsqlIbtBindInfo.elemMaxLen;

          j++; continue;

          DatabaseError.throwSqlException(97);
        }
      }case 6:
    }
  }

  Binder copyingBinder() {
    return this.thePlsqlIbtCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.PlsqlIbtBinder
 * JD-Core Version:    0.6.0
 */