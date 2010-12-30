package oracle.jdbc.pool;

import oracle.jdbc.OracleConnection;

public abstract interface OracleConnectionCacheCallback
{
  public abstract boolean handleAbandonedConnection(OracleConnection paramOracleConnection, Object paramObject);

  public abstract void releaseConnection(OracleConnection paramOracleConnection, Object paramObject);
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleConnectionCacheCallback
 * JD-Core Version:    0.6.0
 */