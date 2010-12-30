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
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;

public class OracleTypeINTERVAL extends OracleType
  implements Serializable
{
  static final long serialVersionUID = 1394800182554224957L;
  final int LDIINTYEARMONTH = 7;
  final int LDIINTDAYSECOND = 10;

  final int SIZE_INTERVAL_YM = 5;
  final int SIZE_INTERVAL_DS = 11;

  byte typeId = 0;
  int scale = 0;
  int precision = 0;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  protected OracleTypeINTERVAL()
  {
  }

  public OracleTypeINTERVAL(OracleConnection paramOracleConnection)
  {
  }

  public int getTypeCode()
  {
    if (this.typeId == 7)
      return -103;
    if (this.typeId == 10) {
      return -104;
    }
    return 1111;
  }

  public void parseTDSrec(TDSReader paramTDSReader)
    throws SQLException
  {
    this.typeId = paramTDSReader.readByte();
    this.precision = paramTDSReader.readByte();
    this.scale = paramTDSReader.readByte();
  }

  public int getScale() throws SQLException
  {
    return this.scale;
  }

  public int getPrecision() throws SQLException
  {
    return this.precision;
  }

  public void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    this.typeId = paramObjectInputStream.readByte();
    this.precision = paramObjectInputStream.readByte();
    this.scale = paramObjectInputStream.readByte();
  }

  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    paramObjectOutputStream.writeByte(this.typeId);
    paramObjectOutputStream.writeByte(this.precision);
    paramObjectOutputStream.writeByte(this.scale);
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
      if (paramArrayOfByte.length == 5)
        return new INTERVALYM(paramArrayOfByte);
      if (paramArrayOfByte.length != 11) break;
      return new INTERVALDS(paramArrayOfByte);
    case 2:
      if (paramArrayOfByte.length == 5)
        return INTERVALYM.toString(paramArrayOfByte);
      if (paramArrayOfByte.length != 11) break;
      return INTERVALDS.toString(paramArrayOfByte);
    case 3:
      return paramArrayOfByte;
    default:
      DatabaseError.throwSqlException(59);
    }

    return null;
  }

  public Datum toDatum(Object paramObject, OracleConnection paramOracleConnection)
    throws SQLException
  {
    Object localObject = null;

    if (paramObject != null)
    {
      if (((paramObject instanceof INTERVALYM)) || ((paramObject instanceof INTERVALDS)))
      {
        localObject = (Datum)paramObject;
      } else if ((paramObject instanceof String))
      {
        try
        {
          localObject = new INTERVALDS((String)paramObject);
        }
        catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
        {
          localObject = new INTERVALYM((String)paramObject);
        }
      }
      else {
        DatabaseError.throwSqlException(59, paramObject);
      }
    }

    return (Datum)localObject;
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
 * Qualified Name:     oracle.jdbc.oracore.OracleTypeINTERVAL
 * JD-Core Version:    0.6.0
 */