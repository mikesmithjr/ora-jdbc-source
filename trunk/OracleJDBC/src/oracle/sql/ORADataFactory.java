package oracle.sql;

import java.sql.SQLException;
import oracle.jdbc.internal.ObjectDataFactory;

public abstract interface ORADataFactory extends ObjectDataFactory {
    public abstract ORAData create(Datum paramDatum, int paramInt) throws SQLException;
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.ORADataFactory JD-Core Version: 0.6.0
 */