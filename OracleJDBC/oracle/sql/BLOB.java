package oracle.sql;

import B;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class BLOB extends DatumWithConnection
  implements Blob
{
  public static final int MAX_CHUNK_SIZE = 32768;
  public static final int DURATION_SESSION = 10;
  public static final int DURATION_CALL = 12;
  static final int OLD_WRONG_DURATION_SESSION = 1;
  static final int OLD_WRONG_DURATION_CALL = 2;
  public static final int MODE_READONLY = 0;
  public static final int MODE_READWRITE = 1;
  BlobDBAccess dbaccess;
  int dbChunkSize;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:46_PDT_2005";

  protected BLOB()
  {
  }

  public BLOB(oracle.jdbc.OracleConnection paramOracleConnection)
    throws SQLException
  {
    this(paramOracleConnection, null);
  }

  public BLOB(oracle.jdbc.OracleConnection paramOracleConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    super(paramArrayOfByte);

    assertNotNull(paramOracleConnection);
    setPhysicalConnectionOf(paramOracleConnection);

    this.dbaccess = getPhysicalConnection().createBlobDBAccess();
    this.dbChunkSize = -1;
  }

  public long length()
    throws SQLException
  {
    return getDBAccess().length(this);
  }

  public byte[] getBytes(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramInt < 0) || (paramLong < 1L))
    {
      DatabaseError.throwSqlException(68, "getBytes()");
    }

    Object localObject = null;

    if (paramInt == 0) {
      localObject = new byte[0];
    }
    else {
      long l = 0L;
      byte[] arrayOfByte = new byte[paramInt];

      l = getBytes(paramLong, paramInt, arrayOfByte);

      if (l > 0L)
      {
        if (l == paramInt)
        {
          localObject = arrayOfByte;
        }
        else
        {
          localObject = new byte[(int)l];

          System.arraycopy(arrayOfByte, 0, localObject, 0, (int)l);
        }

      }

    }

    return (B)localObject;
  }

  public InputStream getBinaryStream()
    throws SQLException
  {
    return getDBAccess().newInputStream(this, getBufferSize(), 0L);
  }

  public long position(byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    return getDBAccess().position(this, paramArrayOfByte, paramLong);
  }

  public long position(Blob paramBlob, long paramLong)
    throws SQLException
  {
    return getDBAccess().position(this, (BLOB)paramBlob, paramLong);
  }

  public int getBytes(long paramLong, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return getDBAccess().getBytes(this, paramLong, paramInt, paramArrayOfByte);
  }

  /** @deprecated */
  public int putBytes(long paramLong, byte[] paramArrayOfByte)
    throws SQLException
  {
    return setBytes(paramLong, paramArrayOfByte);
  }

  /** @deprecated */
  public int putBytes(long paramLong, byte[] paramArrayOfByte, int paramInt)
    throws SQLException
  {
    return setBytes(paramLong, paramArrayOfByte, 0, paramInt);
  }

  /** @deprecated */
  public OutputStream getBinaryOutputStream()
    throws SQLException
  {
    return setBinaryStream(0L);
  }

  public byte[] getLocator()
  {
    return getBytes();
  }

  public void setLocator(byte[] paramArrayOfByte)
  {
    setBytes(paramArrayOfByte);
  }

  public int getChunkSize()
    throws SQLException
  {
    if (this.dbChunkSize <= 0)
    {
      this.dbChunkSize = getDBAccess().getChunkSize(this);
    }

    return this.dbChunkSize;
  }

  public int getBufferSize()
    throws SQLException
  {
    int i = getChunkSize();
    int j = i;

    if ((i >= 32768) || (i <= 0))
    {
      j = 32768;
    }
    else
    {
      j = 32768 / i * i;
    }

    return j;
  }

  /** @deprecated */
  public static BLOB empty_lob()
    throws SQLException
  {
    return getEmptyBLOB();
  }

  public static BLOB getEmptyBLOB()
    throws SQLException
  {
    byte[] arrayOfByte = new byte[86];

    arrayOfByte[1] = 84;
    arrayOfByte[5] = 24;

    BLOB localBLOB = new BLOB();

    localBLOB.setShareBytes(arrayOfByte);

    return localBLOB;
  }

  public boolean isEmptyLob()
    throws SQLException
  {
    int i = (shareBytes()[5] & 0x10) != 0 ? 1 : 0;

    return i;
  }

  /** @deprecated */
  public OutputStream getBinaryOutputStream(long paramLong)
    throws SQLException
  {
    return setBinaryStream(paramLong);
  }

  public InputStream getBinaryStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newInputStream(this, getBufferSize(), paramLong);
  }

  /** @deprecated */
  public void trim(long paramLong)
    throws SQLException
  {
    truncate(paramLong);
  }

  public static BLOB createTemporary(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException
  {
    int i = paramInt;

    if (paramInt == 1) {
      i = 10;
    }
    if (paramInt == 2) {
      i = 12;
    }
    if ((paramConnection == null) || ((i != 10) && (i != 12)))
    {
      DatabaseError.throwSqlException(68);
    }

    oracle.jdbc.internal.OracleConnection localOracleConnection = ((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin();

    return getDBAccess(localOracleConnection).createTemporaryBlob(localOracleConnection, paramBoolean, i);
  }

  public static void freeTemporary(BLOB paramBLOB)
    throws SQLException
  {
    if (paramBLOB == null) {
      return;
    }
    paramBLOB.freeTemporary();
  }

  public static boolean isTemporary(BLOB paramBLOB)
    throws SQLException
  {
    if (paramBLOB == null) {
      return false;
    }
    return paramBLOB.isTemporary();
  }

  public void freeTemporary()
    throws SQLException
  {
    getDBAccess().freeTemporary(this);
  }

  public boolean isTemporary()
    throws SQLException
  {
    return getDBAccess().isTemporary(this);
  }

  public void open(int paramInt)
    throws SQLException
  {
    getDBAccess().open(this, paramInt);
  }

  public void close()
    throws SQLException
  {
    getDBAccess().close(this);
  }

  public boolean isOpen()
    throws SQLException
  {
    return getDBAccess().isOpen(this);
  }

  public int setBytes(long paramLong, byte[] paramArrayOfByte)
    throws SQLException
  {
    return getDBAccess().putBytes(this, paramLong, paramArrayOfByte, 0, paramArrayOfByte != null ? paramArrayOfByte.length : 0);
  }

  public int setBytes(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    return getDBAccess().putBytes(this, paramLong, paramArrayOfByte, paramInt1, paramInt2);
  }

  public OutputStream setBinaryStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newOutputStream(this, getBufferSize(), paramLong);
  }

  public void truncate(long paramLong)
    throws SQLException
  {
    if (paramLong < 0L)
    {
      DatabaseError.throwSqlException(68);
    }

    getDBAccess().trim(this, paramLong);
  }

  public Object toJdbc()
    throws SQLException
  {
    return this;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    String str = paramClass.getName();

    return (str.compareTo("java.io.InputStream") == 0) || (str.compareTo("java.io.Reader") == 0);
  }

  public Reader characterStreamValue()
    throws SQLException
  {
    getInternalConnection(); return getDBAccess().newConversionReader(this, 8);
  }

  public InputStream asciiStreamValue()
    throws SQLException
  {
    getInternalConnection(); return getDBAccess().newConversionInputStream(this, 2);
  }

  public InputStream binaryStreamValue()
    throws SQLException
  {
    return getBinaryStream();
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new BLOB[paramInt];
  }

  public BlobDBAccess getDBAccess()
    throws SQLException
  {
    if (this.dbaccess == null)
    {
      if (isEmptyLob())
      {
        DatabaseError.throwSqlException(98);
      }

      this.dbaccess = getInternalConnection().createBlobDBAccess();
    }

    if (getPhysicalConnection().isClosed()) {
      DatabaseError.throwSqlException(8);
    }

    return this.dbaccess;
  }

  public static BlobDBAccess getDBAccess(Connection paramConnection)
    throws SQLException
  {
    return ((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin().createBlobDBAccess();
  }

  public Connection getJavaSqlConnection() throws SQLException
  {
    return super.getJavaSqlConnection();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.BLOB
 * JD-Core Version:    0.6.0
 */