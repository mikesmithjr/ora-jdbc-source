package oracle.sql;

import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;

class TableClass extends Hashtable {
    private Vector v = new Vector();

    public Object put(Object paramObject, Integer paramInteger) {
        if (this.v.size() < paramInteger.intValue() + 1) {
            this.v.setSize(paramInteger.intValue() + 1);
        }
        if (this.v.elementAt(paramInteger.intValue()) != null) {
            return super.get(paramObject);
        }

        super.put(paramObject, paramInteger);
        this.v.setElementAt(paramObject, paramInteger.intValue());
        return null;
    }

    public Object getKey(int paramInt) {
        return this.v.elementAt(paramInt);
    }

    public void dispTable() {
        for (int i = 0; i < this.v.size(); i++)
            System.out.println(i + "   " + this.v.elementAt(i));
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.TableClass JD-Core Version: 0.6.0
 */