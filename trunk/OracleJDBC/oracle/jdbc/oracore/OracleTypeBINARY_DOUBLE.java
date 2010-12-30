package oracle.jdbc.oracore;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.Datum;

public class OracleTypeBINARY_DOUBLE extends OracleType
  implements Serializable
{
  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    BINARY_DOUBLE localBINARY_DOUBLE = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof BINARY_DOUBLE))
        localBINARY_DOUBLE = (BINARY_DOUBLE)paramObject;
      else if ((paramObject instanceof Double))
        localBINARY_DOUBLE = new BINARY_DOUBLE((Double)paramObject);
      else if ((paramObject instanceof byte[]))
        localBINARY_DOUBLE = new BINARY_DOUBLE((byte[])paramObject);
      else {
        DatabaseError.throwSqlException(59, paramObject);
      }
    }

    return localBINARY_DOUBLE;
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof Object[]))
      {
        Object[] arrayOfObject = (Object[])paramObject;

        int i = (int)(paramInt == -1 ? arrayOfObject.length : Math.min(arrayOfObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (int j = 0; j < i; j++)
        {
          Object localObject = arrayOfObject[((int)paramLong + j - 1)];

          if (localObject != null)
          {
            if ((localObject instanceof Double)) {
              arrayOfDatum[j] = new BINARY_DOUBLE(((Double)localObject).doubleValue());
            }
            else if ((localObject instanceof BINARY_DOUBLE))
              arrayOfDatum[j] = ((BINARY_DOUBLE)localObject);
            else
              DatabaseError.throwSqlException(68);
          }
          else {
            DatabaseError.throwSqlException(68);
          }
        }
      }

    }

    return arrayOfDatum;
  }

  public int getTypeCode()
  {
    return 101;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    DatabaseError.throwSqlException(23, "no 8.0 pickle format for BINARY_DOUBLE");

    return null;
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    if (paramInt == 1)
      return new BINARY_DOUBLE(paramArrayOfByte);
    if (paramInt == 2)
      return new BINARY_DOUBLE(paramArrayOfByte).toJdbc();
    if (paramInt == 3) {
      return paramArrayOfByte;
    }
    DatabaseError.throwSqlException(59, paramArrayOfByte);

    return null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeBINARY_DOUBLE
 * JD-Core Version:    0.6.0
 */