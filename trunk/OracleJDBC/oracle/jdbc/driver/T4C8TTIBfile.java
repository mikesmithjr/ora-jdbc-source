package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.sql.Datum;

class T4C8TTIBfile extends T4C8TTILob
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  T4C8TTIBfile(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer)
  {
    super(paramT4CMAREngine, paramT4CTTIoer);
  }

  Datum createTemporaryLob(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException, IOException
  {
    Object localObject = null;

    DatabaseError.throwSqlException("cannot create a temporary BFILE", DatabaseError.ErrorToSQLState(-1), -1);

    return localObject;
  }

  boolean open(byte[] paramArrayOfByte, int paramInt)
    throws SQLException, IOException
  {
    boolean bool = false;

    bool = _open(paramArrayOfByte, 11, 256);

    return bool;
  }

  boolean close(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = false;

    bool = _close(paramArrayOfByte, 512);

    return bool;
  }

  boolean isOpen(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = _isOpen(paramArrayOfByte, 1024);

    return bool;
  }

  boolean doesExist(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = false;

    initializeLobdef();

    this.sourceLobLocator = paramArrayOfByte;
    this.lobops = 2048L;
    this.nullO2U = true;

    marshalFunHeader();

    marshalOlobops();

    receiveReply();

    bool = this.lobnull;

    return bool;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8TTIBfile
 * JD-Core Version:    0.6.0
 */