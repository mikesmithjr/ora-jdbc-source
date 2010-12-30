package oracle.jdbc.util;

public class SQLStateRange
{
  public int low;
  public int high;
  public String sqlState;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  public SQLStateRange(int paramInt1, int paramInt2, String paramString)
  {
    this.low = paramInt1;
    this.high = paramInt2;
    this.sqlState = paramString;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.util.SQLStateRange
 * JD-Core Version:    0.6.0
 */