package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CInputStream extends OracleInputStream
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4CInputStream(OracleStatement paramOracleStatement, int paramInt, Accessor paramAccessor)
  {
    super(paramOracleStatement, paramInt, paramAccessor);
  }

  public boolean isNull()
    throws IOException
  {
    if (!this.statement.connection.useFetchSizeWithLongColumn) {
      return super.isNull();
    }
    boolean bool = false;
    try
    {
      int i = this.statement.currentRow;

      if (i < 0) {
        i = 0;
      }
      if (i >= this.statement.validRows) {
        return true;
      }
      bool = this.accessor.isNull(i);
    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }

    return bool;
  }

  public int getBytes() throws IOException
  {
    int i = 0;
    try
    {
      i = this.accessor.readStream(this.buf, this.chunkSize);
    }
    catch (SQLException localSQLException1)
    {
      throw new IOException(localSQLException1.getMessage());
    }
    catch (IOException localIOException)
    {
      try
      {
        ((T4CConnection)this.statement.connection).handleIOException(localIOException);
      }
      catch (SQLException localSQLException2) {
      }
      throw localIOException;
    }

    return i;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CInputStream
 * JD-Core Version:    0.6.0
 */