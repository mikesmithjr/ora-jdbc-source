package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIOtxen extends T4CTTIfun
{
  static final int OTXCOMIT = 1;
  static final int OTXABORT = 2;
  static final int OTXPREPA = 3;
  static final int OTXFORGT = 4;
  static final int OTXRECOV = 5;
  static final int OTXMLPRE = 6;
  static final int K2CMDprepare = 0;
  static final int K2CMDrqcommit = 1;
  static final int K2CMDcommit = 2;
  static final int K2CMDabort = 3;
  static final int K2CMDrdonly = 4;
  static final int K2CMDforget = 5;
  static final int K2CMDrecovered = 7;
  static final int K2CMDtimeout = 8;
  static final int K2STAidle = 0;
  static final int K2STAcollecting = 1;
  static final int K2STAprepared = 2;
  static final int K2STAcommitted = 3;
  static final int K2STAhabort = 4;
  static final int K2STAhcommit = 5;
  static final int K2STAhdamage = 6;
  static final int K2STAtimeout = 7;
  static final int K2STAinactive = 9;
  static final int K2STAactive = 10;
  static final int K2STAptprepared = 11;
  static final int K2STAptcommitted = 12;
  static final int K2STAmax = 13;
  T4CConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIOtxen(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer, T4CConnection paramT4CConnection)
    throws SQLException
  {
    super(3, 0, 104);

    this.oer = paramT4CTTIoer;

    setMarshalingEngine(paramT4CMAREngine);

    this.connection = paramT4CConnection;
  }

  void marshal(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    throws IOException, SQLException
  {
    if ((paramInt1 != 1) && (paramInt1 != 2) && (paramInt1 != 3) && (paramInt1 != 4) && (paramInt1 != 5) && (paramInt1 != 6))
    {
      throw new SQLException("Invalid operation.");
    }
    super.marshalFunHeader();

    int i = paramInt1;

    this.meg.marshalSWORD(i);

    if (paramArrayOfByte1 == null)
      this.meg.marshalNULLPTR();
    else {
      this.meg.marshalPTR();
    }

    if (paramArrayOfByte1 == null)
      this.meg.marshalUB4(0L);
    else {
      this.meg.marshalUB4(paramArrayOfByte1.length);
    }

    this.meg.marshalUB4(paramInt2);

    this.meg.marshalUB4(paramInt3);

    this.meg.marshalUB4(paramInt4);

    if (paramArrayOfByte2 != null)
      this.meg.marshalPTR();
    else {
      this.meg.marshalNULLPTR();
    }

    if (paramArrayOfByte2 != null)
      this.meg.marshalUB4(paramArrayOfByte2.length);
    else {
      this.meg.marshalUB4(0L);
    }

    this.meg.marshalUWORD(paramInt5);

    this.meg.marshalUB4(paramInt6);

    this.meg.marshalPTR();

    if (paramArrayOfByte1 != null) {
      this.meg.marshalB1Array(paramArrayOfByte1);
    }
    if (paramArrayOfByte2 != null)
      this.meg.marshalB1Array(paramArrayOfByte2);
  }

  int receive(int[] paramArrayOfInt)
    throws IOException, SQLException
  {
    int j = -1;

    paramArrayOfInt[0] = -1;
    while (true)
    {
      try
      {
        int i = this.meg.unmarshalSB1();

        switch (i)
        {
        case 8:
          j = (int)this.meg.unmarshalUB4();

          break;
        case 9:
          if (this.meg.versionNumber < 10000)
            continue;
          short s = (short)this.meg.unmarshalUB2();

          this.connection.endToEndECIDSequenceNumber = s;

          break;
        case 4:
          this.oer.init();
          this.oer.unmarshal();

          this.oer.processError(false);

          paramArrayOfInt[0] = this.oer.retCode;

          break;
        default:
          DatabaseError.throwSqlException(401);
        }

        continue;
      }
      catch (BreakNetException localBreakNetException) {
      }
    }
    return j;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIOtxen
 * JD-Core Version:    0.6.0
 */