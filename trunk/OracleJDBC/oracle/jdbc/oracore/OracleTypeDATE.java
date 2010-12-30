package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMP;

public class OracleTypeDATE extends OracleType
  implements Serializable
{
  static final long serialVersionUID = -5858803341118747965L;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public OracleTypeDATE()
  {
  }

  public OracleTypeDATE(int paramInt)
  {
    super(paramInt);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    DATE localDATE = null;

    if (paramObject != null)
    {
      try
      {
        if ((paramObject instanceof DATE))
          localDATE = (DATE)paramObject;
        else if ((paramObject instanceof TIMESTAMP))
          localDATE = new DATE(((TIMESTAMP)paramObject).timestampValue());
        else
          localDATE = new DATE(paramObject);
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localDATE;
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof char[][]))
      {
        char[][] arrayOfChar = (char[][])paramObject;
        int i = (int)(paramInt == -1 ? arrayOfChar.length : Math.min(arrayOfChar.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (int j = 0; j < i; j++) {
          arrayOfDatum[j] = toDatum(new String(arrayOfChar[((int)paramLong + j - 1)]), paramOracleConnection);
        }
      }
      if ((paramObject instanceof Object[]))
      {
        return super.toDatumArray(paramObject, paramOracleConnection, paramLong, paramInt);
      }

      DatabaseError.throwSqlException(59, paramObject);
    }

    return arrayOfDatum;
  }

  public int getTypeCode()
  {
    return 91;
  }

  public int getSizeLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 11);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 11);
    }

    return this.sizeForLds;
  }

  public int getAlignLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 11);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 11);
    }

    return this.alignForLds;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    switch (paramInt1)
    {
    case 1:
      if (paramUnpickleContext.isNull(this.nullOffset)) {
        return null;
      }
      paramUnpickleContext.skipTo(paramUnpickleContext.ldsOffsets[this.ldsOffset]);

      break;
    case 2:
      if ((paramUnpickleContext.readByte() & 0x1) != 1)
        break;
      paramUnpickleContext.skipBytes(8);

      return null;
    case 3:
      break;
    }

    DatabaseError.throwSqlException(1, "format=" + paramInt1);

    if (paramInt2 == 9)
    {
      paramUnpickleContext.skipBytes(8);

      return null;
    }

    byte[] arrayOfByte1 = paramUnpickleContext.image();
    int i = paramUnpickleContext.absoluteOffset();

    byte[] arrayOfByte2 = new byte[7];
    int j;
    if (paramUnpickleContext.bigEndian)
      j = (arrayOfByte1[i] & 0xFF) * 256 + (arrayOfByte1[(i + 1)] & 0xFF);
    else {
      j = (arrayOfByte1[(i + 1)] & 0xFF) * 256 + (arrayOfByte1[i] & 0xFF);
    }
    if (j < 0)
    {
      arrayOfByte2[0] = (byte)(100 - -j / 100);
      arrayOfByte2[1] = (byte)(100 - -j % 100);
    }
    else
    {
      arrayOfByte2[0] = (byte)(j / 100 + 100);
      arrayOfByte2[1] = (byte)(j % 100 + 100);
    }

    arrayOfByte2[2] = arrayOfByte1[(i + 2)];
    arrayOfByte2[3] = arrayOfByte1[(i + 3)];
    arrayOfByte2[4] = (byte)(arrayOfByte1[(i + 4)] + 1);
    arrayOfByte2[5] = (byte)(arrayOfByte1[(i + 5)] + 1);
    arrayOfByte2[6] = (byte)(arrayOfByte1[(i + 6)] + 1);

    paramUnpickleContext.skipBytes(8);

    return toObject(arrayOfByte2, paramInt2, paramMap);
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    if (paramInt == 1)
      return new DATE(paramArrayOfByte);
    if (paramInt == 2)
      return DATE.toTimestamp(paramArrayOfByte);
    if (paramInt == 3) {
      return paramArrayOfByte;
    }
    DatabaseError.throwSqlException(59, paramArrayOfByte);

    return null;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeDATE
 * JD-Core Version:    0.6.0
 */