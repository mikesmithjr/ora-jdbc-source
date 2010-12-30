package oracle.jdbc.driver;

import java.util.Vector;

class OracleResultSetCacheImpl
  implements OracleResultSetCache
{
  private static int DEFAULT_WIDTH = 5;
  private static int DEFAULT_SIZE = 5;

  Vector m_rows = null;
  int m_width;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  OracleResultSetCacheImpl()
  {
    this(DEFAULT_WIDTH);
  }

  OracleResultSetCacheImpl(int paramInt)
  {
    if (paramInt > 0) {
      this.m_width = paramInt;
    }
    this.m_rows = new Vector(DEFAULT_SIZE);
  }

  public void put(int paramInt1, int paramInt2, Object paramObject)
  {
    Vector localVector = null;

    while (this.m_rows.size() < paramInt1)
    {
      localVector = new Vector(this.m_width);

      this.m_rows.addElement(localVector);
    }

    localVector = (Vector)this.m_rows.elementAt(paramInt1 - 1);

    while (localVector.size() < paramInt2) {
      localVector.addElement(null);
    }
    localVector.setElementAt(paramObject, paramInt2 - 1);
  }

  public Object get(int paramInt1, int paramInt2)
  {
    Vector localVector = (Vector)this.m_rows.elementAt(paramInt1 - 1);

    return localVector.elementAt(paramInt2 - 1);
  }

  public void remove(int paramInt)
  {
    this.m_rows.removeElementAt(paramInt - 1);
  }

  public void remove(int paramInt1, int paramInt2)
  {
    this.m_rows.removeElementAt(paramInt1 - 1);
  }

  public void clear()
  {
  }

  public void close() {
  }

  public int getLength() {
    return 0;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleResultSetCacheImpl
 * JD-Core Version:    0.6.0
 */