package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.OracleConnection;
import oracle.sql.BLOB;
import oracle.sql.Datum;

class T4C8TTIBlob extends T4C8TTILob
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4C8TTIBlob(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer)
  {
    super(paramT4CMAREngine, paramT4CTTIoer);
  }

  Datum createTemporaryLob(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException, IOException
  {
    if (paramInt == 12) {
      DatabaseError.throwSqlException(158);
    }

    BLOB localBLOB = null;

    initializeLobdef();

    this.lobops = 272L;
    this.sourceLobLocator = new byte[86];
    this.sourceLobLocator[1] = 84;

    this.characterSet = 1;

    this.lobamt = paramInt;
    this.sendLobamt = true;

    this.destinationOffset = 113L;

    this.nullO2U = true;

    marshalFunHeader();

    marshalOlobops();

    receiveReply();

    if (this.sourceLobLocator != null)
    {
      localBLOB = new BLOB((OracleConnection)paramConnection, this.sourceLobLocator);
    }

    return localBLOB;
  }

  boolean open(byte[] paramArrayOfByte, int paramInt)
    throws SQLException, IOException
  {
    boolean bool = false;

    int i = 2;

    if (paramInt == 0) {
      i = 1;
    }
    bool = _open(paramArrayOfByte, i, 32768);

    return bool;
  }

  boolean close(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = false;

    bool = _close(paramArrayOfByte, 65536);

    return bool;
  }

  boolean isOpen(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = false;

    bool = _isOpen(paramArrayOfByte, 69632);

    return bool;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8TTIBlob
 * JD-Core Version:    0.6.0
 */