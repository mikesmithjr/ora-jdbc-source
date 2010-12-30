package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIk2rpc extends T4CTTIfun
{
  T4CConnection connection;
  static final int K2RPClogon = 1;
  static final int K2RPCbegin = 2;
  static final int K2RPCend = 3;
  static final int K2RPCrecover = 4;
  static final int K2RPCsession = 5;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIk2rpc(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer, T4CConnection paramT4CConnection)
    throws SQLException
  {
    super(3, 0, 67);

    this.oer = paramT4CTTIoer;

    setMarshalingEngine(paramT4CMAREngine);

    this.connection = paramT4CConnection;
  }

  void marshal(int paramInt1, int paramInt2)
    throws IOException, SQLException
  {
    super.marshalFunHeader();

    this.meg.marshalUB4(0L);
    this.meg.marshalUB4(paramInt1);
    this.meg.marshalPTR();
    this.meg.marshalUB4(3L);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalPTR();
    this.meg.marshalUB4(3L);
    this.meg.marshalPTR();
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();

    this.meg.marshalUB4(paramInt2);
    this.meg.marshalUB4(0L);
    this.meg.marshalUB4(0L);
  }

  void receive()
    throws SQLException, IOException
  {
    while (true)
      try
      {
        int i = this.meg.unmarshalSB1();
        int j;
        switch (i)
        {
        case 9:
          if (this.meg.versionNumber < 10000)
            continue;
          j = (short)this.meg.unmarshalUB2();

          this.connection.endToEndECIDSequenceNumber = j;

          break;
        case 8:
          j = this.meg.unmarshalUB2();

          int k = 0; if (k >= j)
            continue;
          this.meg.unmarshalUB4();

          k++; break;
        default:
          DatabaseError.throwSqlException(401);
        }

        continue;
      }
      catch (BreakNetException localBreakNetException)
      {
      }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIk2rpc
 * JD-Core Version:    0.6.0
 */