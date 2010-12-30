package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIofetch extends T4CTTIfun
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIofetch(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(3, 0, 5);

    setMarshalingEngine(paramT4CMAREngine);
  }

  void marshal(int paramInt1, int paramInt2) throws IOException, SQLException
  {
    super.marshalFunHeader();
    this.meg.marshalSWORD(paramInt1);
    this.meg.marshalSWORD(paramInt2);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIofetch
 * JD-Core Version:    0.6.0
 */