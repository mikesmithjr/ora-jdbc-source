package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.BLOB;
import oracle.sql.Datum;

public class OracleTypeBLOB extends OracleType
  implements Serializable
{
  static final long serialVersionUID = -2311211431562030662L;
  static int fixedDataSize = 86;
  transient OracleConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  protected OracleTypeBLOB()
  {
  }

  public OracleTypeBLOB(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    BLOB localBLOB = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof BLOB))
        localBLOB = (BLOB)paramObject;
      else {
        DatabaseError.throwSqlException(59, paramObject);
      }
    }

    return localBLOB;
  }

  public int getTypeCode()
  {
    return 2004;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return lobUnpickle80rec(this, this.nullOffset, this.ldsOffset, paramUnpickleContext, paramInt1, paramInt2, fixedDataSize);
  }

  protected static Object lobUnpickle80rec(OracleType paramOracleType, int paramInt1, int paramInt2, UnpickleContext paramUnpickleContext, int paramInt3, int paramInt4, int paramInt5)
    throws SQLException
  {
    switch (paramInt3)
    {
    case 1:
      if (paramUnpickleContext.isNull(paramInt1)) {
        return null;
      }
      paramUnpickleContext.skipTo(paramUnpickleContext.ldsOffsets[paramInt2]);

      if (paramInt4 == 9)
      {
        paramUnpickleContext.skipBytes(4);

        return null;
      }

      paramUnpickleContext.markAndSkip();

      byte[] arrayOfByte1 = paramUnpickleContext.readPtrBytes();

      paramUnpickleContext.reset();

      return paramOracleType.toObject(arrayOfByte1, paramInt4, null);
    case 2:
      if ((paramUnpickleContext.readByte() & 0x1) != 1)
        break;
      paramUnpickleContext.skipPtrBytes();

      return null;
    case 3:
      long l = paramUnpickleContext.offset() + paramInt5;

      if (paramInt4 == 9)
      {
        paramUnpickleContext.skipTo(l);

        return null;
      }

      byte[] arrayOfByte2 = paramUnpickleContext.readPtrBytes();

      if (paramUnpickleContext.offset() < l) {
        paramUnpickleContext.skipTo(l);
      }
      return paramOracleType.toObject(arrayOfByte2, paramInt4, null);
    }

    DatabaseError.throwSqlException(1, "format=" + paramInt3);

    return null;
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
    case 2:
      return this.connection.createBlob(paramArrayOfByte);
    case 3:
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

  public void setConnection(OracleConnection paramOracleConnection)
    throws SQLException
  {
    this.connection = paramOracleConnection;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeBLOB
 * JD-Core Version:    0.6.0
 */