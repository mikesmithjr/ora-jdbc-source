package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIoses extends T4CTTIfun
{
  static final int OSESSWS = 1;
  static final int OSESDET = 3;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIoses(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(17, 0);

    setMarshalingEngine(paramT4CMAREngine);
    setFunCode(107);
  }

  void marshal(int paramInt1, int paramInt2, int paramInt3)
    throws IOException, SQLException
  {
    if ((paramInt3 != 1) && (paramInt3 != 3)) {
      throw new SQLException("Wrong operation : can only do switch or detach");
    }
    marshalFunHeader();
    this.meg.marshalUB4(paramInt1);
    this.meg.marshalUB4(paramInt2);
    this.meg.marshalUB4(paramInt3);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIoses
 * JD-Core Version:    0.6.0
 */