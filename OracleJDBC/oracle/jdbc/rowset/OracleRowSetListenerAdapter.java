package oracle.jdbc.rowset;

import java.io.Serializable;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

public abstract class OracleRowSetListenerAdapter
  implements RowSetListener, Serializable
{
  public void cursorMoved(RowSetEvent paramRowSetEvent)
  {
  }

  public void rowChanged(RowSetEvent paramRowSetEvent)
  {
  }

  public void rowSetChanged(RowSetEvent paramRowSetEvent)
  {
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleRowSetListenerAdapter
 * JD-Core Version:    0.6.0
 */