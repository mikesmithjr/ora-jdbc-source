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
import oracle.sql.RAW;

public class OracleTypeRAW extends OracleType
  implements Serializable
{
  static final long serialVersionUID = -6083664758336974576L;
  int length;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public OracleTypeRAW()
  {
  }

  public OracleTypeRAW(int paramInt)
  {
    super(paramInt);
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    RAW localRAW = null;

    if (paramObject != null)
    {
      try
      {
        if ((paramObject instanceof RAW))
          localRAW = (RAW)paramObject;
        else
          localRAW = new RAW(paramObject);
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localRAW;
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
    return -2;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    super.parseTDSrec(paramTDSReader);

    this.length = paramTDSReader.readShort();
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

      if (paramInt2 == 9)
      {
        paramUnpickleContext.skipBytes(4);

        return null;
      }

      paramUnpickleContext.markAndSkip();

      byte[] arrayOfByte = paramUnpickleContext.readLengthBytes();

      paramUnpickleContext.reset();

      return toObject(arrayOfByte, paramInt2, paramMap);
    case 2:
      if (((paramUnpickleContext.readByte() & 0x1) == 1) || (paramInt2 == 9))
      {
        paramUnpickleContext.skipLengthBytes();

        return null;
      }

      return toObject(paramUnpickleContext.readLengthBytes(), paramInt2, paramMap);
    case 3:
      if (paramInt2 == 9)
      {
        paramUnpickleContext.skipLengthBytes();

        return null;
      }

      return toObject(paramUnpickleContext.readLengthBytes(), paramInt2, paramMap);
    }

    DatabaseError.throwSqlException(1, "format=" + paramInt1);

    return null;
  }

  protected int pickle81(PickleContext paramPickleContext, Datum paramDatum)
    throws SQLException
  {
    if (paramDatum.getLength() > this.length) {
      DatabaseError.throwSqlException(72, this);
    }
    int i = paramPickleContext.writeLength((int)paramDatum.getLength());

    i += paramPickleContext.writeData(paramDatum.shareBytes());

    return i;
  }

  public int getLength()
  {
    return this.length;
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    switch (paramInt)
    {
    case 1:
      return new RAW(paramArrayOfByte);
    case 2:
    case 3:
      return paramArrayOfByte;
    }

    DatabaseError.throwSqlException(59, paramArrayOfByte);

    return null;
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeInt(this.length);
  }

  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.length = paramObjectInputStream.readInt();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeRAW
 * JD-Core Version:    0.6.0
 */