package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;

public abstract class DatumWithConnection extends Datum
{
  private oracle.jdbc.internal.OracleConnection physicalConnection = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  oracle.jdbc.internal.OracleConnection getPhysicalConnection()
  {
    if (this.physicalConnection == null)
    {
      try
      {
        this.physicalConnection = ((oracle.jdbc.internal.OracleConnection)new OracleDriver().defaultConnection());
      }
      catch (SQLException localSQLException)
      {
      }

    }

    return this.physicalConnection;
  }

  public DatumWithConnection(byte[] paramArrayOfByte) throws SQLException
  {
    super(paramArrayOfByte);
  }

  public DatumWithConnection()
  {
  }

  public static void assertNotNull(Connection paramConnection)
    throws SQLException
  {
    if (paramConnection == null)
      DatabaseError.throwSqlException(68, "Connection is null");
  }

  public static void assertNotNull(TypeDescriptor paramTypeDescriptor)
    throws SQLException
  {
    if (paramTypeDescriptor == null)
      DatabaseError.throwSqlException(61);
  }

  public void setPhysicalConnectionOf(Connection paramConnection)
  {
    this.physicalConnection = ((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin();
  }

  public Connection getJavaSqlConnection()
    throws SQLException
  {
    return getPhysicalConnection().getWrapper();
  }

  public oracle.jdbc.OracleConnection getOracleConnection()
    throws SQLException
  {
    return getPhysicalConnection().getWrapper();
  }

  public oracle.jdbc.internal.OracleConnection getInternalConnection()
    throws SQLException
  {
    return getPhysicalConnection();
  }

  /** @deprecated */
  public oracle.jdbc.driver.OracleConnection getConnection()
    throws SQLException
  {
    oracle.jdbc.driver.OracleConnection localOracleConnection = null;
    try
    {
      localOracleConnection = (oracle.jdbc.driver.OracleConnection)((oracle.jdbc.driver.OracleConnection)this.physicalConnection).getWrapper();
    }
    catch (ClassCastException localClassCastException)
    {
      DatabaseError.throwSqlException(103);
    }

    return localOracleConnection;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.DatumWithConnection
 * JD-Core Version:    0.6.0
 */