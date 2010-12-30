package oracle.jdbc.rowset;

import java.sql.SQLException;
import javax.sql.RowSet;
import javax.sql.rowset.Predicate;

public abstract interface OraclePredicate extends Predicate
{
  public abstract boolean evaluate(RowSet paramRowSet);

  public abstract boolean evaluate(Object paramObject, int paramInt)
    throws SQLException;

  public abstract boolean evaluate(Object paramObject, String paramString)
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OraclePredicate
 * JD-Core Version:    0.6.0
 */