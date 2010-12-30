package oracle.jdbc.driver;

import java.sql.SQLException;
import java.util.Map;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class ArrayLocatorResultSet extends OracleResultSetImpl
{
  static int COUNT_UNLIMITED = -1;
  Map map;
  long beginIndex;
  int count;
  long currentIndex;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public ArrayLocatorResultSet(OracleConnection paramOracleConnection, ArrayDescriptor paramArrayDescriptor, byte[] paramArrayOfByte, Map paramMap)
    throws SQLException
  {
    this(paramOracleConnection, paramArrayDescriptor, paramArrayOfByte, 0L, COUNT_UNLIMITED, paramMap);
  }

  public ArrayLocatorResultSet(OracleConnection paramOracleConnection, ArrayDescriptor paramArrayDescriptor, byte[] paramArrayOfByte, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    super((PhysicalConnection)paramOracleConnection, (OracleStatement)null);

    if ((paramArrayDescriptor == null) || (paramOracleConnection == null)) {
      DatabaseError.throwSqlException(1, "Invalid arguments");
    }

    this.close_statement_on_close = true;

    this.count = paramInt;
    this.currentIndex = 0L;
    this.beginIndex = paramLong;

    this.map = paramMap;

    OraclePreparedStatement localOraclePreparedStatement = null;

    ARRAY localARRAY = new ARRAY(paramArrayDescriptor, paramOracleConnection, (byte[])null);

    localARRAY.setLocator(paramArrayOfByte);

    if ((paramArrayDescriptor.getBaseType() == 2002) || (paramArrayDescriptor.getBaseType() == 2008))
    {
      localOraclePreparedStatement = (OraclePreparedStatement)paramOracleConnection.prepareStatement("SELECT ROWNUM, SYS_NC_ROWINFO$ FROM TABLE( CAST(:1 AS " + paramArrayDescriptor.getName() + ") )");
    }
    else
    {
      localOraclePreparedStatement = (OraclePreparedStatement)paramOracleConnection.prepareStatement("SELECT ROWNUM, COLUMN_VALUE FROM TABLE( CAST(:1 AS " + paramArrayDescriptor.getName() + ") )");
    }

    localOraclePreparedStatement.setArray(1, localARRAY);
    localOraclePreparedStatement.executeQuery();

    this.statement = localOraclePreparedStatement;
  }

  public synchronized boolean next()
    throws SQLException
  {
    if (this.currentIndex < this.beginIndex)
    {
      while (this.currentIndex < this.beginIndex)
      {
        this.currentIndex += 1L;

        if (!super.next()) {
          return false;
        }
      }
      return true;
    }

    if (this.count == COUNT_UNLIMITED)
    {
      return super.next();
    }
    if (this.currentIndex < this.beginIndex + this.count - 1L)
    {
      this.currentIndex += 1L;

      return super.next();
    }

    return false;
  }

  public synchronized Object getObject(int paramInt)
    throws SQLException
  {
    return getObject(paramInt, this.map);
  }

  public synchronized int findColumn(String paramString)
    throws SQLException
  {
    if (paramString.equalsIgnoreCase("index"))
      return 1;
    if (paramString.equalsIgnoreCase("value")) {
      return 2;
    }
    DatabaseError.throwSqlException(6, "get_column_index");

    return 0;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ArrayLocatorResultSet
 * JD-Core Version:    0.6.0
 */