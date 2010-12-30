package oracle.sql;

import java.sql.SQLException;
import oracle.jdbc.driver.OracleConnection;
import oracle.jdbc.internal.ObjectData;

public abstract interface CustomDatum extends ObjectData
{
  public abstract Datum toDatum(OracleConnection paramOracleConnection)
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CustomDatum
 * JD-Core Version:    0.6.0
 */