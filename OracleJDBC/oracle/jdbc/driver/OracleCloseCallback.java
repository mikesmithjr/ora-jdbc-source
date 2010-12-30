package oracle.jdbc.driver;

import oracle.jdbc.internal.OracleConnection;

public abstract interface OracleCloseCallback
{
  public abstract void beforeClose(OracleConnection paramOracleConnection, Object paramObject);

  public abstract void afterClose(Object paramObject);
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleCloseCallback
 * JD-Core Version:    0.6.0
 */