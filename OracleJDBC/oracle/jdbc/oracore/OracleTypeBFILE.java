package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.BFILE;
import oracle.sql.Datum;

public class OracleTypeBFILE extends OracleType
  implements Serializable
{
  static final long serialVersionUID = -707073491109554687L;
  static int fixedDataSize = 530;
  transient OracleConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  public OracleTypeBFILE()
  {
  }

  public OracleTypeBFILE(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    BFILE localBFILE = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof BFILE))
        localBFILE = (BFILE)paramObject;
      else {
        DatabaseError.throwSqlException(59, paramObject);
      }
    }

    return localBFILE;
  }

  public int getTypeCode()
  {
    return -13;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    return OracleTypeBLOB.lobUnpickle80rec(this, this.nullOffset, this.ldsOffset, paramUnpickleContext, paramInt1, paramInt2, fixedDataSize);
  }

  protected Object toObject(byte[] paramArrayOfByte, int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      return null;
    }
    if ((paramInt == 1) || (paramInt == 2)) {
      return this.connection.createBfile(paramArrayOfByte);
    }

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

  public void setConnection(OracleConnection paramOracleConnection)
    throws SQLException
  {
    this.connection = paramOracleConnection;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeBFILE
 * JD-Core Version:    0.6.0
 */