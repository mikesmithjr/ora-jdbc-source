package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIsto extends T4CTTIfun
{
  static final int OV6STRT = 48;
  static final int OV6STOP = 49;
  static final int STOMFDBA = 1;
  static final int STOMFACA = 2;
  static final int STOMFALO = 4;
  static final int STOMFSHU = 8;
  static final int STOMFFRC = 16;
  static final int STOMFPOL = 32;
  static final int STOMFABO = 64;
  static final int STOMFATX = 128;
  static final int STOMFLTX = 256;
  static final int STOSDONE = 1;
  static final int STOSINPR = 2;
  static final int STOSERR = 3;
  T4CTTIoer oer;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIsto(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer)
    throws IOException, SQLException
  {
    super(3, 0, 0);

    this.oer = paramT4CTTIoer;

    setMarshalingEngine(paramT4CMAREngine);
  }

  void marshal(int paramInt) throws IOException, SQLException
  {
    if ((paramInt == 1) || (paramInt == 16))
      this.funCode = 48;
    else if ((paramInt == 2) || (paramInt == 4) || (paramInt == 8) || (paramInt == 32) || (paramInt == 64) || (paramInt == 128) || (paramInt == 256))
    {
      this.funCode = 49;
    }
    else throw new SQLException("Invalid mode.");

    super.marshalFunHeader();
    this.meg.marshalSWORD(paramInt);
    this.meg.marshalPTR();
  }

  int receive()
    throws SQLException, IOException
  {
    int j = 0;
    try
    {
      i = this.meg.unmarshalSB1();

      if (i == 8)
      {
        j = (int)this.meg.unmarshalUB4();

        if (j == 3) {
          throw new SQLException("An error occurred during startup/shutdown");
        }
        int k = this.meg.unmarshalUB1();
      }
      else if (i == 4)
      {
        this.oer.init();
        this.oer.unmarshal();
        try
        {
          this.oer.processError();
        }
        catch (SQLException localSQLException)
        {
          throw localSQLException;
        }
      }
      else
      {
        DatabaseError.throwSqlException(401);
      }

    }
    catch (BreakNetException localBreakNetException)
    {
      int i = this.meg.unmarshalSB1();
      if (i == 4)
      {
        this.oer.init();
        this.oer.unmarshal();
        this.oer.processError();
      }
    }

    return j;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIsto
 * JD-Core Version:    0.6.0
 */