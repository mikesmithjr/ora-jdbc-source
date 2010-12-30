package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.CLOB;
import oracle.sql.Datum;

public class OracleTypeCLOB extends OracleType
  implements Serializable
{
  static final long serialVersionUID = 1122821330765834411L;
  static int fixedDataSize = 86;
  transient OracleConnection connection;
  int form;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeCLOB()
  {
  }

  public OracleTypeCLOB(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    CLOB localCLOB = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof CLOB))
        localCLOB = (CLOB)paramObject;
      else {
        DatabaseError.throwSqlException(59, paramObject);
      }
    }

    return localCLOB;
  }

  public int getTypeCode()
  {
    return 2005;
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
      return this.connection.createClob(paramArrayOfByte);
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

  public boolean isNCHAR()
    throws SQLException
  {
    return this.form == 2;
  }

  public void setForm(int paramInt)
  {
    this.form = paramInt;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeCLOB
 * JD-Core Version:    0.6.0
 */