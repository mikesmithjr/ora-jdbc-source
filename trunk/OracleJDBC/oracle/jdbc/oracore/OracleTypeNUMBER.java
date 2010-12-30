package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.Datum;
import oracle.sql.NUMBER;

public class OracleTypeNUMBER extends OracleType
  implements Serializable
{
  static final long serialVersionUID = -7182242886677299812L;
  int precision;
  int scale;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeNUMBER()
  {
  }

  protected OracleTypeNUMBER(int paramInt)
  {
    super(paramInt);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    return toNUMBER(paramObject, paramOracleConnection);
  }

  public Datum[] toDatumArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    return toNUMBERArray(paramObject, paramOracleConnection, paramLong, paramInt);
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    this.nullOffset = paramTDSReader.nullOffset;
    this.ldsOffset = paramTDSReader.ldsOffset;
    paramTDSReader.nullOffset += 1;
    paramTDSReader.ldsOffset += 1;

    this.precision = paramTDSReader.readUnsignedByte();

    this.scale = paramTDSReader.readByte();
  }

  public int getSizeLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 12);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 12);
    }

    return this.sizeForLds;
  }

  public int getAlignLDS(byte[] paramArrayOfByte)
  {
    if (this.sizeForLds == 0)
    {
      this.sizeForLds = Util.fdoGetSize(paramArrayOfByte, 12);
      this.alignForLds = Util.fdoGetAlign(paramArrayOfByte, 12);
    }

    return this.alignForLds;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return numericUnpickle80rec(this.ldsOffset, this.nullOffset, paramUnpickleContext, paramInt1, paramInt2, paramMap);
  }

  protected static Object numericUnpickle80rec(int paramInt1, int paramInt2, UnpickleContext paramUnpickleContext, int paramInt3, int paramInt4, Map paramMap)
    throws SQLException
  {
    switch (paramInt3)
    {
    case 1:
      if (paramUnpickleContext.isNull(paramInt2)) {
        return null;
      }
      paramUnpickleContext.skipTo(paramUnpickleContext.ldsOffsets[paramInt1]);

      break;
    case 2:
      if ((paramUnpickleContext.readByte() & 0x1) != 1)
        break;
      paramUnpickleContext.skipBytes(22);

      return null;
    case 3:
      break;
    default:
      DatabaseError.throwSqlException(1, "format=" + paramInt3);
    }

    if (paramInt4 == 9)
    {
      paramUnpickleContext.skipBytes(22);

      return null;
    }

    return toNumericObject(paramUnpickleContext.readVarNumBytes(), paramInt4, paramMap);
  }

  protected static Object unpickle80NativeArray(UnpickleContext paramUnpickleContext, long paramLong, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    int i = 0;

    switch (paramInt3)
    {
    case 2:
      i = 23;

      break;
    case 3:
      i = 22;

      break;
    default:
      DatabaseError.throwSqlException(1, "format=" + paramInt3);
    }

    if (paramInt1 > 0) {
      paramUnpickleContext.skipBytes(i * ((int)paramLong - 1));
    }
    int j = paramUnpickleContext.absoluteOffset();
    byte[] arrayOfByte1 = paramUnpickleContext.image();
    int k = 0;
    Object localObject;
    int m;
    byte[] arrayOfByte2;
    switch (paramInt2)
    {
    case 4:
      localObject = new int[paramInt1];

      for (m = 0; m < paramInt1; m++)
      {
        k = j + m * i;

        if ((paramInt3 != 3) && ((arrayOfByte1[(k++)] & 0x1) != 0))
        {
          continue;
        }

        arrayOfByte2 = new byte[arrayOfByte1[(k++)]];

        System.arraycopy(arrayOfByte1, k, arrayOfByte2, 0, arrayOfByte2.length);

        localObject[m] = NUMBER.toInt(arrayOfByte2);
      }

      paramUnpickleContext.skipBytes(i * paramInt1);

      return localObject;
    case 5:
      localObject = new double[paramInt1];

      for (m = 0; m < paramInt1; m++)
      {
        k = j + m * i;

        if ((paramInt3 != 3) && ((arrayOfByte1[(k++)] & 0x1) != 0)) {
          continue;
        }
        arrayOfByte2 = new byte[arrayOfByte1[(k++)]];

        System.arraycopy(arrayOfByte1, k, arrayOfByte2, 0, arrayOfByte2.length);

        localObject[m] = NUMBER.toDouble(arrayOfByte2);
      }

      paramUnpickleContext.skipBytes(i * paramInt1);

      return localObject;
    case 7:
      localObject = new long[paramInt1];

      for (m = 0; m < paramInt1; m++)
      {
        k = j + m * i;

        if ((paramInt3 != 3) && ((arrayOfByte1[(k++)] & 0x1) != 0)) {
          continue;
        }
        arrayOfByte2 = new byte[arrayOfByte1[(k++)]];

        System.arraycopy(arrayOfByte1, k, arrayOfByte2, 0, arrayOfByte2.length);

        localObject[m] = NUMBER.toLong(arrayOfByte2);
      }

      paramUnpickleContext.skipBytes(i * paramInt1);

      return localObject;
    case 6:
      localObject = new float[paramInt1];

      for (m = 0; m < paramInt1; m++)
      {
        k = j + m * i;

        if ((paramInt3 != 3) && ((arrayOfByte1[(k++)] & 0x1) != 0)) {
          continue;
        }
        arrayOfByte2 = new byte[arrayOfByte1[(k++)]];

        System.arraycopy(arrayOfByte1, k, arrayOfByte2, 0, arrayOfByte2.length);

        localObject[m] = NUMBER.toFloat(arrayOfByte2);
      }

      paramUnpickleContext.skipBytes(i * paramInt1);

      return localObject;
    case 8:
      localObject = new short[paramInt1];

      for (m = 0; m < paramInt1; m++)
      {
        k = j + m * i;

        if ((paramInt3 != 3) && ((arrayOfByte1[(k++)] & 0x1) != 0)) {
          continue;
        }
        arrayOfByte2 = new byte[arrayOfByte1[(k++)]];

        System.arraycopy(arrayOfByte1, k, arrayOfByte2, 0, arrayOfByte2.length);

        localObject[m] = NUMBER.toShort(arrayOfByte2);
      }

      paramUnpickleContext.skipBytes(i * paramInt1);

      return localObject;
    }

    DatabaseError.throwSqlException(23);

    return null;
  }

  protected static Object unpickle81NativeArray(PickleContext paramPickleContext, long paramLong, int paramInt1, int paramInt2)
    throws SQLException
  {
    for (int i = 1; (i < paramLong) && (paramInt1 > 0); i++) {
      paramPickleContext.skipDataValue();
    }
    byte[] arrayOfByte = null;
    Object localObject;
    int j;
    switch (paramInt2)
    {
    case 4:
      localObject = new int[paramInt1];

      for (j = 0; j < paramInt1; j++)
      {
        if ((arrayOfByte = paramPickleContext.readDataValue()) != null) {
          localObject[j] = NUMBER.toInt(arrayOfByte);
        }

      }

      return localObject;
    case 5:
      localObject = new double[paramInt1];

      for (j = 0; j < paramInt1; j++)
      {
        if ((arrayOfByte = paramPickleContext.readDataValue()) != null) {
          localObject[j] = NUMBER.toDouble(arrayOfByte);
        }
      }
      return localObject;
    case 7:
      localObject = new long[paramInt1];

      for (j = 0; j < paramInt1; j++)
      {
        if ((arrayOfByte = paramPickleContext.readDataValue()) != null) {
          localObject[j] = NUMBER.toLong(arrayOfByte);
        }
      }
      return localObject;
    case 6:
      localObject = new float[paramInt1];

      for (j = 0; j < paramInt1; j++)
      {
        if ((arrayOfByte = paramPickleContext.readDataValue()) != null) {
          localObject[j] = NUMBER.toFloat(arrayOfByte);
        }
      }
      return localObject;
    case 8:
      localObject = new short[paramInt1];

      for (j = 0; j < paramInt1; j++)
      {
        if ((arrayOfByte = paramPickleContext.readDataValue()) != null) {
          localObject[j] = NUMBER.toShort(arrayOfByte);
        }
      }
      return localObject;
    }

    DatabaseError.throwSqlException(23);

    return null;
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    return toNumericObject(paramArrayOfByte, paramInt, paramMap);
  }

  static Object toNumericObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    switch (paramInt)
    {
    case 1:
      return new NUMBER(paramArrayOfByte);
    case 2:
      return NUMBER.toBigDecimal(paramArrayOfByte);
    case 3:
      return paramArrayOfByte;
    }

    DatabaseError.throwSqlException(23);

    return null;
  }

  public static NUMBER toNUMBER(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    NUMBER localNUMBER = null;

    if (paramObject != null)
    {
      try
      {
        if ((paramObject instanceof NUMBER))
          localNUMBER = (NUMBER)paramObject;
        else
          localNUMBER = new NUMBER(paramObject);
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localNUMBER;
  }

  public static Datum[] toNUMBERArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;
    Object[] arrayOfObject;
    int j;
    if (paramObject != null)
    {
      if (((paramObject instanceof Object[])) && (!(paramObject instanceof char[][])))
      {
        arrayOfObject = (Object[])paramObject;

        int i = (int)(paramInt == -1 ? arrayOfObject.length : Math.min(arrayOfObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; ) {
          arrayOfDatum[j] = toNUMBER(arrayOfObject[((int)paramLong + j - 1)], paramOracleConnection);

          j++; continue;

          arrayOfDatum = cArrayToNUMBERArray(paramObject, paramOracleConnection, paramLong, paramInt);
        }
      }
    }

    return arrayOfDatum;
  }

  static Datum[] cArrayToNUMBERArray(Object paramObject, OracleConnection paramOracleConnection, long paramLong, int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = null;

    if (paramObject != null)
    {
      Object localObject;
      int i;
      int j;
      if ((paramObject instanceof short[]))
      {
        localObject = (short[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++)
          arrayOfDatum[j] = new NUMBER(localObject[((int)paramLong + j - 1)]);
      }
      if ((paramObject instanceof int[]))
      {
        localObject = (int[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++)
          arrayOfDatum[j] = new NUMBER(localObject[((int)paramLong + j - 1)]);
      }
      if ((paramObject instanceof long[]))
      {
        localObject = (long[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++)
          arrayOfDatum[j] = new NUMBER(localObject[((int)paramLong + j - 1)]);
      }
      if ((paramObject instanceof float[]))
      {
        localObject = (float[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++)
          arrayOfDatum[j] = new NUMBER(localObject[((int)paramLong + j - 1)]);
      }
      if ((paramObject instanceof double[]))
      {
        localObject = (double[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++)
          arrayOfDatum[j] = new NUMBER(localObject[((int)paramLong + j - 1)]);
      }
      if ((paramObject instanceof boolean[]))
      {
        localObject = (boolean[])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new NUMBER(new Boolean(localObject[((int)paramLong + j - 1)]));
        }
      }
      if ((paramObject instanceof char[][]))
      {
        localObject = (char[][])paramObject;
        i = (int)(paramInt == -1 ? localObject.length : Math.min(localObject.length - paramLong + 1L, paramInt));

        arrayOfDatum = new Datum[i];

        for (j = 0; j < i; j++) {
          arrayOfDatum[j] = new NUMBER(new String(localObject[((int)paramLong + j - 1)]));
        }

      }

      DatabaseError.throwSqlException(59, paramObject);
    }

    return (Datum)arrayOfDatum;
  }

  public int getPrecision()
  {
    return this.precision;
  }

  public int getScale()
  {
    return this.scale;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.scale);
    paramObjectOutputStream.writeInt(this.precision);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.scale = paramObjectInputStream.readInt();
    this.precision = paramObjectInputStream.readInt();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeNUMBER
 * JD-Core Version:    0.6.0
 */