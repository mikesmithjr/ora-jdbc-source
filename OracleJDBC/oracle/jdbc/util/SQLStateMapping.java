package oracle.jdbc.util;

public class SQLStateMapping
{
  public int err;
  public String sqlState;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public SQLStateMapping(int paramInt, String paramString)
  {
    this.err = paramInt;
    this.sqlState = paramString;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.util.SQLStateMapping
 * JD-Core Version:    0.6.0
 */