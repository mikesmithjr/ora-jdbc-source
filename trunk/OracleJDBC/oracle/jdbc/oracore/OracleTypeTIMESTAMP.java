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
import oracle.sql.TIMESTAMP;

public class OracleTypeTIMESTAMP extends OracleType
  implements Serializable
{
  static final long serialVersionUID = 3948043338303602796L;
  int precision = 0;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeTIMESTAMP()
  {
  }

  public OracleTypeTIMESTAMP(OracleConnection paramOracleConnection)
  {
  }

  public int getTypeCode()
  {
    return 93;
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
      return new TIMESTAMP(paramArrayOfByte);
    case 2:
      return TIMESTAMP.toTimestamp(paramArrayOfByte);
    case 3:
      return paramArrayOfByte;
    }

    DatabaseError.throwSqlException(59);

    return null;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    TIMESTAMP localTIMESTAMP = null;

    if (paramObject != null)
    {
      try
      {
        if ((paramObject instanceof TIMESTAMP))
          localTIMESTAMP = (TIMESTAMP)paramObject;
        else if ((paramObject instanceof byte[]))
          localTIMESTAMP = new TIMESTAMP((byte[])paramObject);
        else if ((paramObject instanceof Timestamp))
          localTIMESTAMP = new TIMESTAMP((Timestamp)paramObject);
        else if ((paramObject instanceof DATE))
          localTIMESTAMP = new TIMESTAMP((DATE)paramObject);
        else if ((paramObject instanceof String))
          localTIMESTAMP = new TIMESTAMP((String)paramObject);
        else if ((paramObject instanceof Date))
          localTIMESTAMP = new TIMESTAMP((Date)paramObject);
        else if ((paramObject instanceof Time))
          localTIMESTAMP = new TIMESTAMP((Time)paramObject);
        else {
          DatabaseError.throwSqlException(59, paramObject);
        }
      }
      catch (Exception localException)
      {
        DatabaseError.throwSqlException(59, paramObject);
      }

    }

    return localTIMESTAMP;
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
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeTIMESTAMP
 * JD-Core Version:    0.6.0
 */