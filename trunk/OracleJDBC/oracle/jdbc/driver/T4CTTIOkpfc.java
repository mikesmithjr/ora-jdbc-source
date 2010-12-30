package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIOkpfc extends T4CTTIfun
{
  static final int KPFCI_CONNECT = 1;
  static final int KPFCI_REVERT = 2;
  static final int KPFCI_TEST = 4;
  static final int KPFCI_STRM = 8;
  static final int KPFCI_PING = 16;
  static final int KPFCI_TCPCONNECT = 32;
  static final int KPFCI_TCPREVERT = 64;
  static final int KPFCV_VENDOR = 0;
  static final int KPFCV_PROTO = 1;
  static final int KPFCV_MAJOR = 2;
  static final int KPFCV_MINOR = 3;
  static final int KPFCO_CONNECT = 1;
  static final int KPFCO_REVERT = 2;
  static final int KPFCO_TCPCONNECT = 4;
  static final int KPFCO_TCPREVERT = 8;

  T4CTTIOkpfc(T4CMAREngine paramT4CMAREngine)
    throws SQLException
  {
    super(3, 0, 139);

    setMarshalingEngine(paramT4CMAREngine);
  }

  void marshal(int paramInt)
    throws IOException, SQLException
  {
    if ((paramInt != 1) && (paramInt != 2) && (paramInt != 4) && (paramInt != 8) && (paramInt != 16) && (paramInt != 32) && (paramInt != 64))
    {
      throw new SQLException("Invalid operation.");
    }
    super.marshalFunHeader();
    this.meg.marshalUB4(paramInt);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalUB4(0L);
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalUB4(0L);
    this.meg.marshalNULLPTR();
    this.meg.marshalPTR();
  }

  void receive()
    throws SQLException, IOException
  {
    byte[] arrayOfByte = null;
    while (true)
    {
      try
      {
        int i = this.meg.unmarshalSB1();

        switch (i)
        {
        case 8:
          int j = (int)this.meg.unmarshalUB4();

          arrayOfByte = new byte[j];

          int k = 0; if (k >= j) continue;
          arrayOfByte[k] = (byte)(int)this.meg.unmarshalUB4();

          k++; break;
        case 4:
          this.oer.init();
          this.oer.unmarshal();

          break;
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
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIOkpfc
 * JD-Core Version:    0.6.0
 */