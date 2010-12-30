package oracle.jdbc.driver;

import java.sql.SQLException;

abstract class OracleTimeout
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  static OracleTimeout newTimeout(String paramString)
    throws SQLException
  {
    OracleTimeoutThreadPerVM localOracleTimeoutThreadPerVM = new OracleTimeoutThreadPerVM(paramString);

    return localOracleTimeoutThreadPerVM;
  }

  abstract void setTimeout(long paramLong, OracleStatement paramOracleStatement)
    throws SQLException;

  abstract void cancelTimeout()
    throws SQLException;

  abstract void close()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleTimeout
 * JD-Core Version:    0.6.0
 */