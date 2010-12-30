package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4C7Ocommoncall extends T4CTTIfun
{
  T4CConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  T4C7Ocommoncall(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer, T4CConnection paramT4CConnection)
    throws IOException, SQLException
  {
    super(3, 0);

    setMarshalingEngine(paramT4CMAREngine);

    this.oer = paramT4CTTIoer;
    this.connection = paramT4CConnection;
  }

  void init(short paramShort)
    throws IOException, SQLException
  {
    this.oer.init();

    this.funCode = paramShort;
  }

  void receive()
    throws SQLException, IOException
  {
    int i = 0;
    while (true)
    {
      try
      {
        int j = this.meg.unmarshalSB1();

        switch (j)
        {
        case 9:
          if (this.meg.versionNumber < 10000)
            continue;
          short s = (short)this.meg.unmarshalUB2();

          this.connection.endToEndECIDSequenceNumber = s;

          break;
        case 4:
          this.oer.init();
          this.oer.unmarshal();

          if (this.oer.retCode != 2089) {
            this.oer.processError();
          }
          break;
        default:
          DatabaseError.throwSqlException(401);

          continue;
        }
      }
      catch (BreakNetException localBreakNetException)
      {
      }
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C7Ocommoncall
 * JD-Core Version:    0.6.0
 */