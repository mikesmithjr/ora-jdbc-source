package oracle.sql;

import java.sql.SQLException;
import oracle.jdbc.internal.ObjectDataFactory;

public abstract interface CustomDatumFactory extends ObjectDataFactory
{
  public abstract CustomDatum create(Datum paramDatum, int paramInt)
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CustomDatumFactory
 * JD-Core Version:    0.6.0
 */