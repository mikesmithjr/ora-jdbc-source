package oracle.jdbc.rowset;

import java.io.Serializable;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;

public abstract class OracleRowSetListenerAdapter implements RowSetListener, Serializable {
    public void cursorMoved(RowSetEvent event) {
    }

    public void rowChanged(RowSetEvent event) {
    }

    public void rowSetChanged(RowSetEvent event) {
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleRowSetListenerAdapter JD-Core Version: 0.6.0
 */