package oracle.jdbc;

public class OracleDriver extends oracle.jdbc.driver.OracleDriver
{
  public static final boolean isDMS()
  {
    return false;
  }

  public static final boolean isInServer()
  {
    return false;
  }

  public static final boolean isJDK14()
  {
    return true;
  }

  public static final boolean isDebug()
  {
    return false;
  }

  public static final boolean isPrivateDebug()
  {
    return false;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.OracleDriver
 * JD-Core Version:    0.6.0
 */