package oracle.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

public class CLOB extends DatumWithConnection
  implements Clob
{
  public static final int MAX_CHUNK_SIZE = 32768;
  public static final int DURATION_SESSION = 10;
  public static final int DURATION_CALL = 12;
  static final int OLD_WRONG_DURATION_SESSION = 1;
  static final int OLD_WRONG_DURATION_CALL = 2;
  public static final int MODE_READONLY = 0;
  public static final int MODE_READWRITE = 1;
  ClobDBAccess dbaccess;
  private int dbChunkSize;
  private short csform;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:46_PDT_2005";

  protected CLOB()
  {
  }

  public CLOB(oracle.jdbc.OracleConnection paramOracleConnection)
    throws SQLException
  {
    this(paramOracleConnection, null);
  }

  public CLOB(oracle.jdbc.OracleConnection paramOracleConnection, byte[] paramArrayOfByte)
    throws SQLException
  {
    super(paramArrayOfByte);

    if (paramArrayOfByte != null)
    {
      if ((paramArrayOfByte[5] & 0xC0) == 64)
        this.csform = 2;
      else {
        this.csform = 1;
      }

    }

    assertNotNull(paramOracleConnection);
    setPhysicalConnectionOf(paramOracleConnection);

    this.dbaccess = ((oracle.jdbc.internal.OracleConnection)paramOracleConnection).createClobDBAccess();

    this.dbChunkSize = -1;
  }

  public CLOB(oracle.jdbc.OracleConnection paramOracleConnection, byte[] paramArrayOfByte, short paramShort)
    throws SQLException
  {
    this(paramOracleConnection, paramArrayOfByte);

    this.csform = paramShort;
  }

  public boolean isNCLOB()
  {
    int i = this.csform == 2 ? 1 : 0;

    return i;
  }

  public long length()
    throws SQLException
  {
    return getDBAccess().length(this);
  }

  public String getSubString(long paramLong, int paramInt)
    throws SQLException
  {
    if ((paramInt < 0) || (paramLong < 1L))
    {
      DatabaseError.throwSqlException(68, "getSubString");
    }

    String str = null;

    if (paramInt == 0) {
      str = new String();
    }
    else {
      char[] arrayOfChar = new char[paramInt];

      int i = getChars(paramLong, paramInt, arrayOfChar);

      if (i > 0)
      {
        str = new String(arrayOfChar, 0, i);
      }
      else
      {
        str = new String();
      }

    }

    return str;
  }

  public Reader getCharacterStream()
    throws SQLException
  {
    return getDBAccess().newReader(this, getBufferSize(), 0L);
  }

  public InputStream getAsciiStream()
    throws SQLException
  {
    return getDBAccess().newInputStream(this, getBufferSize(), 0L);
  }

  public long position(String paramString, long paramLong)
    throws SQLException
  {
    return getDBAccess().position(this, paramString, paramLong);
  }

  public long position(Clob paramClob, long paramLong)
    throws SQLException
  {
    return getDBAccess().position(this, (CLOB)paramClob, paramLong);
  }

  public int getChars(long paramLong, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    return getDBAccess().getChars(this, paramLong, paramInt, paramArrayOfChar);
  }

  /** @deprecated */
  public Writer getCharacterOutputStream()
    throws SQLException
  {
    return setCharacterStream(0L);
  }

  /** @deprecated */
  public OutputStream getAsciiOutputStream()
    throws SQLException
  {
    return setAsciiStream(0L);
  }

  public byte[] getLocator()
  {
    return getBytes();
  }

  public void setLocator(byte[] paramArrayOfByte)
  {
    setBytes(paramArrayOfByte);
  }

  public int putChars(long paramLong, char[] paramArrayOfChar)
    throws SQLException
  {
    int i = getDBAccess().putChars(this, paramLong, paramArrayOfChar, 0, paramArrayOfChar != null ? paramArrayOfChar.length : 0);

    return i;
  }

  public int putChars(long paramLong, char[] paramArrayOfChar, int paramInt)
    throws SQLException
  {
    return getDBAccess().putChars(this, paramLong, paramArrayOfChar, 0, paramInt);
  }

  public int putChars(long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SQLException
  {
    return getDBAccess().putChars(this, paramLong, paramArrayOfChar, paramInt1, paramInt2);
  }

  /** @deprecated */
  public int putString(long paramLong, String paramString)
    throws SQLException
  {
    return setString(paramLong, paramString);
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
    int j = 0;

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
  public static CLOB empty_lob()
    throws SQLException
  {
    return getEmptyCLOB();
  }

  public static CLOB getEmptyCLOB()
    throws SQLException
  {
    byte[] arrayOfByte = new byte[86];

    arrayOfByte[1] = 84;
    arrayOfByte[5] = 24;

    CLOB localCLOB = new CLOB();

    localCLOB.setShareBytes(arrayOfByte);

    return localCLOB;
  }

  public boolean isEmptyLob()
    throws SQLException
  {
    return (shareBytes()[5] & 0x10) != 0;
  }

  /** @deprecated */
  public OutputStream getAsciiOutputStream(long paramLong)
    throws SQLException
  {
    return setAsciiStream(paramLong);
  }

  /** @deprecated */
  public Writer getCharacterOutputStream(long paramLong)
    throws SQLException
  {
    return setCharacterStream(paramLong);
  }

  public InputStream getAsciiStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newInputStream(this, getBufferSize(), paramLong);
  }

  public Reader getCharacterStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newReader(this, getBufferSize(), paramLong);
  }

  /** @deprecated */
  public void trim(long paramLong)
    throws SQLException
  {
    truncate(paramLong);
  }

  public static CLOB createTemporary(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException
  {
    return createTemporary(paramConnection, paramBoolean, paramInt, 1);
  }

  public static CLOB createTemporary(Connection paramConnection, boolean paramBoolean, int paramInt, short paramShort)
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

    CLOB localCLOB = getDBAccess(localOracleConnection).createTemporaryClob(localOracleConnection, paramBoolean, i, paramShort);

    localCLOB.csform = paramShort;

    return localCLOB;
  }

  public static void freeTemporary(CLOB paramCLOB)
    throws SQLException
  {
    if (paramCLOB == null) {
      return;
    }
    paramCLOB.freeTemporary();
  }

  public static boolean isTemporary(CLOB paramCLOB)
    throws SQLException
  {
    if (paramCLOB == null) {
      return false;
    }
    return paramCLOB.isTemporary();
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

  public int setString(long paramLong, String paramString)
    throws SQLException
  {
    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "setString()");
    }

    int i = 0;

    if ((paramString != null) && (paramString.length() != 0)) {
      i = putChars(paramLong, paramString.toCharArray());
    }

    return i;
  }

  public int setString(long paramLong, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "setString()");
    }

    if (paramInt1 < 0)
    {
      DatabaseError.throwSqlException(68, "setString()");
    }

    if (paramInt1 + paramInt2 > paramString.length())
    {
      DatabaseError.throwSqlException(68, "setString()");
    }

    int i = 0;

    if ((paramString != null) && (paramString.length() != 0)) {
      i = putChars(paramLong, paramString.toCharArray(), paramInt1, paramInt2);
    }

    return i;
  }

  public OutputStream setAsciiStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newOutputStream(this, getBufferSize(), paramLong);
  }

  public Writer setCharacterStream(long paramLong)
    throws SQLException
  {
    return getDBAccess().newWriter(this, getBufferSize(), paramLong);
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
    return getCharacterStream();
  }

  public InputStream asciiStreamValue()
    throws SQLException
  {
    return getAsciiStream();
  }

  public InputStream binaryStreamValue()
    throws SQLException
  {
    return getAsciiStream();
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new CLOB[paramInt];
  }

  public ClobDBAccess getDBAccess()
    throws SQLException
  {
    if (this.dbaccess == null)
    {
      if (isEmptyLob())
      {
        DatabaseError.throwSqlException(98);
      }

      this.dbaccess = getInternalConnection().createClobDBAccess();
    }

    if (getPhysicalConnection().isClosed()) {
      DatabaseError.throwSqlException(8);
    }

    return this.dbaccess;
  }

  public static ClobDBAccess getDBAccess(Connection paramConnection)
    throws SQLException
  {
    return ((oracle.jdbc.OracleConnection)paramConnection).physicalConnectionWithin().createClobDBAccess();
  }

  public Connection getJavaSqlConnection()
    throws SQLException
  {
    return super.getJavaSqlConnection();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CLOB
 * JD-Core Version:    0.6.0
 */