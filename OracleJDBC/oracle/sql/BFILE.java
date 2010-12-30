package oracle.sql;

import B;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class BFILE extends DatumWithConnection
{
  public static final int MAX_CHUNK_SIZE = 32512;
  public static final int MODE_READONLY = 0;
  public static final int MODE_READWRITE = 1;
  BfileDBAccess dbaccess;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:46_PDT_2005";

  protected BFILE()
  {
  }

  public BFILE(oracle.jdbc.OracleConnection paramOracleConnection)
    throws SQLException
  {
    this(paramOracleConnection, null);
  }

  public BFILE(oracle.jdbc.OracleConnection paramOracleConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    super(paramArrayOfByte);

    setPhysicalConnectionOf(paramOracleConnection);

    this.dbaccess = getInternalConnection().createBfileDBAccess();
  }

  public long length()
    throws SQLException
  {
    return getDBAccess().length(this);
  }

  public byte[] getBytes(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramLong < 1L) || (paramInt < 0))
    {
      DatabaseError.throwSqlException(68, null);
    }

    Object localObject = null;

    if (paramInt == 0)
    {
      localObject = new byte[0];
    }
    else
    {
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
      else
      {
        localObject = new byte[0];
      }

    }

    return (B)localObject;
  }

  public int getBytes(long paramLong, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = getDBAccess().getBytes(this, paramLong, paramInt, paramArrayOfByte);

    return i;
  }

  public InputStream getBinaryStream()
    throws SQLException
  {
    InputStream localInputStream = getDBAccess().newInputStream(this, 32512, 0L);

    return localInputStream;
  }

  public long position(byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    long l = getDBAccess().position(this, paramArrayOfByte, paramLong);

    return l;
  }

  public long position(BFILE paramBFILE, long paramLong)
    throws SQLException
  {
    long l = getDBAccess().position(this, paramBFILE, paramLong);

    return l;
  }

  public String getName()
    throws SQLException
  {
    String str = getDBAccess().getName(this);

    return str;
  }

  public String getDirAlias()
    throws SQLException
  {
    String str = getDBAccess().getDirAlias(this);

    return str;
  }

  public void openFile()
    throws SQLException
  {
    getDBAccess().openFile(this);
  }

  public boolean isFileOpen()
    throws SQLException
  {
    boolean bool = getDBAccess().isFileOpen(this);

    return bool;
  }

  public boolean fileExists()
    throws SQLException
  {
    boolean bool = getDBAccess().fileExists(this);

    return bool;
  }

  public void closeFile()
    throws SQLException
  {
    getDBAccess().closeFile(this);
  }

  public byte[] getLocator()
  {
    return getBytes();
  }

  public void setLocator(byte[] paramArrayOfByte)
  {
    setBytes(paramArrayOfByte);
  }

  public InputStream getBinaryStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newInputStream(this, 32512, paramLong);
  }

  public void open()
    throws SQLException
  {
    getDBAccess().open(this, 0);
  }

  public void open(int paramInt)
    throws SQLException
  {
    if (paramInt != 0)
    {
      DatabaseError.throwSqlException(102);
    }

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

  public Object toJdbc()
    throws SQLException
  {
    return this;
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    String str = paramClass.getName();

    int i = (str.compareTo("java.io.InputStream") == 0) || (str.compareTo("java.io.Reader") == 0) ? 1 : 0;

    return i;
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
    return new BFILE[paramInt];
  }

  public BfileDBAccess getDBAccess()
    throws SQLException
  {
    if (this.dbaccess == null) {
      this.dbaccess = getInternalConnection().createBfileDBAccess();
    }
    if (getPhysicalConnection().isClosed()) {
      DatabaseError.throwSqlException(8);
    }
    return this.dbaccess;
  }

  public Connection getJavaSqlConnection() throws SQLException
  {
    return super.getJavaSqlConnection();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.BFILE
 * JD-Core Version:    0.6.0
 */