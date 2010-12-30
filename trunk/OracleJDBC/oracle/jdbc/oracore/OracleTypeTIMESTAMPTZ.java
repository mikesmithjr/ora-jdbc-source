package oracle.jdbc.oracore;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.internal.OracleConnection;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.TIMESTAMPTZ;

public class OracleTypeTIMESTAMPTZ extends OracleType
  implements Serializable
{
  static final long serialVersionUID = 5643686037837085645L;
  int precision = 0;
  transient OracleConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeTIMESTAMPTZ()
  {
  }

  public OracleTypeTIMESTAMPTZ(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection;
  }

  public int getTypeCode()
  {
    return -101;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    this.precision = paramTDSReader.readByte();
  }

  public int getScale() throws SQLException
  {
    return 0;
  }

  public int getPrecision() throws SQLException
  {
    return this.precision;
  }

  public void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.precision = paramObjectInputStream.readByte();
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeByte(this.precision);
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
      return new TIMESTAMPTZ(paramArrayOfByte);
    case 2:
      return TIMESTAMPTZ.toTimestamp(this.connection, paramArrayOfByte);
    case 3:
      return paramArrayOfByte;
    }

    DatabaseError.throwSqlException(59);

    return null;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    TIMESTAMPTZ localTIMESTAMPTZ = null;

    if (paramObject != null)
    {
      try
      {
        if ((paramObject instanceof TIMESTAMPTZ))
          localTIMESTAMPTZ = (TIMESTAMPTZ)paramObject;
        else if ((paramObject instanceof byte[]))
          localTIMESTAMPTZ = new TIMESTAMPTZ((byte[])paramObject);
        else if ((paramObject instanceof Timestamp))
          localTIMESTAMPTZ = new TIMESTAMPTZ(paramOracleConnection, (Timestamp)paramObject);
        else if ((paramObject instanceof DATE))
          localTIMESTAMPTZ = new TIMESTAMPTZ(paramOracleConnection, (DATE)paramObject);
        else if ((paramObject instanceof String))
          localTIMESTAMPTZ = new TIMESTAMPTZ(paramOracleConnection, (String)paramObject);
        else if ((paramObject instanceof Date))
          localTIMESTAMPTZ = new TIMESTAMPTZ(paramOracleConnection, (Date)paramObject);
        else if ((paramObject instanceof Time))
          localTIMESTAMPTZ = new TIMESTAMPTZ(paramOracleConnection, (Time)paramObject);
        else {
          DatabaseError.throwSqlException(59, paramObject);
        }
      }
      catch (Exception localException)
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localTIMESTAMPTZ;
  }

  protected Object unpickle80rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    DatabaseError.throwSqlException(90);

    return null;
  }

  protected Object unpickle81rec(UnpickleContext paramUnpickleContext, int paramInt1, int paramInt2, Map paramMap)
    throws SQLException
  {
    DatabaseError.throwSqlException(90);

    return null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeTIMESTAMPTZ
 * JD-Core Version:    0.6.0
 */