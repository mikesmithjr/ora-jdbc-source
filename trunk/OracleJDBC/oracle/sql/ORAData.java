package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.internal.ObjectData;

public abstract interface ORAData extends ObjectData
{
  public abstract Datum toDatum(Connection paramConnection)
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.ORAData
 * JD-Core Version:    0.6.0
 */