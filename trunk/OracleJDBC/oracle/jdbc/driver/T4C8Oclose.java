package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8Oclose extends T4CTTIfun
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  T4C8Oclose(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(17, 0);

    setMarshalingEngine(paramT4CMAREngine);
    setFunCode(120);
  }

  void initCloseQuery()
    throws IOException, SQLException
  {
    setFunCode(120);
  }

  void initCloseStatement()
    throws IOException, SQLException
  {
    setFunCode(105);
  }

  void marshal(int[] paramArrayOfInt, int paramInt)
    throws IOException
  {
    marshalFunHeader();

    this.meg.marshalPTR();
    this.meg.marshalUB4(paramInt);

    for (int i = 0; i < paramInt; i++)
    {
      this.meg.marshalUB4(paramArrayOfInt[i]);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8Oclose
 * JD-Core Version:    0.6.0
 */