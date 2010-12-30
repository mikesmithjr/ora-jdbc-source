package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4CTTIOtxse extends T4CTTIfun
{
  static final int OTXSTA = 1;
  static final int OTXDET = 2;
  static final int OCI_TRANS_NEW = 1;
  static final int OCI_TRANS_JOIN = 2;
  static final int OCI_TRANS_RESUME = 4;
  static final int OCI_TRANS_STARTMASK = 255;
  static final int OCI_TRANS_READONLY = 256;
  static final int OCI_TRANS_READWRITE = 512;
  static final int OCI_TRANS_SERIALIZABLE = 1024;
  static final int OCI_TRANS_ISOLMASK = 65280;
  static final int OCI_TRANS_LOOSE = 65536;
  static final int OCI_TRANS_TIGHT = 131072;
  static final int OCI_TRANS_TYPEMASK = 983040;
  static final int OCI_TRANS_NOMIGRATE = 1048576;
  static final int OCI_TRANS_SEPARABLE = 2097152;
  boolean sendTransactionContext = false;
  T4CConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIOtxse(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer, T4CConnection paramT4CConnection)
    throws SQLException
  {
    super(3, 0, 103);

    this.oer = paramT4CTTIoer;

    setMarshalingEngine(paramT4CMAREngine);

    this.connection = paramT4CConnection;
  }

  void marshal(int paramInt1, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    throws IOException, SQLException
  {
    if ((paramInt1 != 1) && (paramInt1 != 2)) {
      throw new SQLException("Invalid operation.");
    }
    super.marshalFunHeader();

    int i = paramInt1;

    this.meg.marshalSWORD(i);

    if (paramInt1 == 2)
    {
      if (paramArrayOfByte1 == null) {
        throw new SQLException("Transaction context cannot be null when detach is called.");
      }
      this.sendTransactionContext = true;

      this.meg.marshalPTR();
    }
    else
    {
      this.sendTransactionContext = false;

      this.meg.marshalNULLPTR();
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

    this.meg.marshalUB4(paramInt6);

    this.meg.marshalUWORD(paramInt5);

    this.meg.marshalPTR();

    this.meg.marshalPTR();
    this.meg.marshalPTR();

    if (this.sendTransactionContext) {
      this.meg.marshalB1Array(paramArrayOfByte1);
    }
    if (paramArrayOfByte2 != null) {
      this.meg.marshalB1Array(paramArrayOfByte2);
    }
    this.meg.marshalUB4(0L);
  }

  byte[] receive(int[] paramArrayOfInt)
    throws SQLException, IOException
  {
    byte[] arrayOfByte = null;
    while (true)
    {
      try
      {
        int i = this.meg.unmarshalSB1();
        int k;
        switch (i)
        {
        case 8:
          paramArrayOfInt[0] = (int)this.meg.unmarshalUB4();

          int j = this.meg.unmarshalUB2();

          arrayOfByte = new byte[j];

          k = 0; if (k >= j) continue;
          arrayOfByte[k] = (byte)this.meg.unmarshalUB1();

          k++; break;
        case 9:
          if (this.meg.versionNumber < 10000)
            continue;
          k = (short)this.meg.unmarshalUB2();

          this.connection.endToEndECIDSequenceNumber = k;

          break;
        case 4:
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

          break;
        default:
          DatabaseError.throwSqlException(401);
        }

        continue;
      }
      catch (BreakNetException localBreakNetException) {
      }
    }
    return arrayOfByte;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIOtxse
 * JD-Core Version:    0.6.0
 */