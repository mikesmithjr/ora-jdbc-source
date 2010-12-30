package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCLOB;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.BfileDBAccess;
import oracle.sql.BlobDBAccess;
import oracle.sql.CLOB;
import oracle.sql.ClobDBAccess;
import oracle.sql.LobPlsqlUtil;
import oracle.sql.SQLName;
import oracle.sql.converter.CharacterSetMetaData;

public class T2CConnection extends PhysicalConnection
  implements BfileDBAccess, BlobDBAccess, ClobDBAccess
{
  short[] queryMetaData1 = null;
  byte[] queryMetaData2 = null;
  int queryMetaData1Offset = 0;
  int queryMetaData2Offset = 0;
  private String password;
  int fatalErrorNumber = 0;
  String fatalErrorMessage = null;
  static final int QMD_dbtype = 0;
  static final int QMD_dbsize = 1;
  static final int QMD_nullok = 2;
  static final int QMD_precision = 3;
  static final int QMD_scale = 4;
  static final int QMD_formOfUse = 5;
  static final int QMD_columnNameLength = 6;
  static final int QMD_tdo0 = 7;
  static final int QMD_tdo1 = 8;
  static final int QMD_tdo2 = 9;
  static final int QMD_tdo3 = 10;
  static final int QMD_charLength = 11;
  static final int QMD_typeNameLength = 12;
  static final int T2C_LOCATOR_MAX_LEN = 16;
  static final int T2C_LINEARIZED_LOCATOR_MAX_LEN = 4000;
  static final int T2C_LINEARIZED_BFILE_LOCATOR_MAX_LEN = 530;
  static final int METADATA1_INDICES_PER_COLUMN = 13;
  protected static final int SIZEOF_QUERYMETADATA2 = 8;
  int queryMetaData1Size = 100;
  int queryMetaData2Size = 800;
  long m_nativeState;
  short m_clientCharacterSet;
  byte byteAlign;
  private static final int EOJ_SUCCESS = 0;
  private static final int EOJ_ERROR = -1;
  private static final int EOJ_WARNING = 1;
  private static final String OCILIBRARY = "ocijdbc10";
  private int logon_mode = 0;
  static final int LOGON_MODE_DEFAULT = 0;
  static final int LOGON_MODE_SYSDBA = 2;
  static final int LOGON_MODE_SYSOPER = 4;
  static final int LOGON_MODE_CONNECTION_POOL = 5;
  static final int LOGON_MODE_CONNPOOL_CONNECTION = 6;
  static final int LOGON_MODE_CONNPOOL_PROXY_CONNECTION = 7;
  static final int LOGON_MODE_CONNPOOL_ALIASED_CONNECTION = 8;
  static final int T2C_PROXYTYPE_NONE = 0;
  static final int T2C_PROXYTYPE_USER_NAME = 1;
  static final int T2C_PROXYTYPE_DISTINGUISHED_NAME = 2;
  static final int T2C_PROXYTYPE_CERTIFICATE = 3;
  private static boolean isLibraryLoaded;
  OracleOCIFailover appCallback = null;
  Object appCallbackObject = null;
  private Properties nativeInfo;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  protected T2CConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties, OracleDriverExtension paramOracleDriverExtension)
    throws SQLException
  {
    super(paramString1, paramString2, paramString3, paramString4, paramProperties, paramOracleDriverExtension);

    initialize();
  }

  final void initializePassword(String paramString) throws SQLException
  {
    this.password = paramString;
  }

  protected void initialize()
  {
    allocQueryMetaDataBuffers();
  }

  private void allocQueryMetaDataBuffers()
  {
    this.queryMetaData1Offset = 0;
    this.queryMetaData1 = new short[this.queryMetaData1Size * 13];

    this.queryMetaData2Offset = 0;
    this.queryMetaData2 = new byte[this.queryMetaData2Size];

    this.namedTypeAccessorByteLen = 0;
    this.refTypeAccessorByteLen = 0;
  }

  void reallocateQueryMetaData(int paramInt1, int paramInt2)
  {
    this.queryMetaData1 = null;
    this.queryMetaData2 = null;

    this.queryMetaData1Size = Math.max(paramInt1, this.queryMetaData1Size);
    this.queryMetaData2Size = Math.max(paramInt2, this.queryMetaData2Size);

    allocQueryMetaDataBuffers();
  }

  protected void logon()
    throws SQLException
  {
    if (this.database == null) {
      DatabaseError.throwSqlException(64);
    }
    if (!isLibraryLoaded) {
      loadNativeLibrary(this.connectionProperties);
    }

    if (this.connectionProperties.getProperty("is_connection_pooling") == "true")
    {
      processOCIConnectionPooling();
    }
    else
    {
      long l1 = 0L;
      long l2 = 0L;
      long l3 = 0L;
      String str1;
      String str2;
      if (((str1 = this.connectionProperties.getProperty("OCISvcCtxHandle")) != null) && ((str2 = this.connectionProperties.getProperty("OCIEnvHandle")) != null))
      {
        l1 = Long.parseLong(str1);
        l2 = Long.parseLong(str2);
        String str3;
        if ((str3 = this.connectionProperties.getProperty("OCIErrHandle")) != null)
        {
          l3 = Long.parseLong(str3);
        }

        if ((str4 = this.connectionProperties.getProperty("JDBCDriverCharSetId")) != null)
        {
          this.m_clientCharacterSet = new Integer(str4).shortValue();
        }
        else
        {
          DatabaseError.throwSqlException(89);
        }

        this.conversion = new DBConversion(this.m_clientCharacterSet, this.m_clientCharacterSet, this.m_clientCharacterSet);

        localObject = new short[3];

        this.sqlWarning = checkError(t2cUseConnection(this.m_nativeState, l2, l1, l3, localObject), this.sqlWarning);

        this.conversion = new DBConversion(localObject[0], this.m_clientCharacterSet, localObject[1]);
        this.byteAlign = (byte)(localObject[2] & 0xFF);

        return;
      }

      String str4 = this.connectionProperties.getProperty("internal_logon");

      if (str4 == null)
        this.logon_mode = 0;
      else if (str4.equalsIgnoreCase("SYSDBA"))
        this.logon_mode = 2;
      else if (str4.equalsIgnoreCase("SYSOPER")) {
        this.logon_mode = 4;
      }
      Object localObject = this.connectionProperties.getProperty("oracle.jdbc.ociNlsLangBackwardCompatible");

      byte[] arrayOfByte1 = null;
      byte[] arrayOfByte2 = null;
      String str5 = null;
      byte[] arrayOfByte3 = new byte[0];
      if (this.connectionProperties != null)
      {
        str5 = (String)this.connectionProperties.get("OCINewPassword");
      }

      if ((localObject != null) && (((String)localObject).equalsIgnoreCase("true")))
      {
        this.m_clientCharacterSet = getDriverCharSetIdFromNLS_LANG(this.connectionProperties);
      }
      else
      {
        this.m_clientCharacterSet = getClientCharSetId();
      }

      if (str5 != null) {
        arrayOfByte3 = DBConversion.stringToAsciiBytes(str5);
      }
      arrayOfByte1 = this.user == null ? new byte[0] : DBConversion.stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

      arrayOfByte2 = this.password == null ? new byte[0] : DBConversion.stringToAsciiBytes(this.password);

      byte[] arrayOfByte4 = DBConversion.stringToAsciiBytes(this.database);

      short[] arrayOfShort = new short[3];
      byte[] arrayOfByte5 = CharacterSetMetaData.getNLSLanguage(Locale.getDefault()).getBytes();

      byte[] arrayOfByte6 = CharacterSetMetaData.getNLSTerritory(Locale.getDefault()).getBytes();

      this.conversion = new DBConversion(this.m_clientCharacterSet, this.m_clientCharacterSet, this.m_clientCharacterSet);

      if (this.m_nativeState == 0L)
      {
        this.sqlWarning = checkError(t2cCreateState(arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, arrayOfByte4, arrayOfByte4.length, this.m_clientCharacterSet, this.logon_mode, arrayOfShort, arrayOfByte5, arrayOfByte6), this.sqlWarning);
      }
      else
      {
        this.sqlWarning = checkError(t2cLogon(this.m_nativeState, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, arrayOfByte4, arrayOfByte4.length, this.logon_mode, arrayOfShort, arrayOfByte5, arrayOfByte6), this.sqlWarning);
      }

      this.conversion = new DBConversion(arrayOfShort[0], this.m_clientCharacterSet, arrayOfShort[1]);
      this.byteAlign = (byte)(arrayOfShort[2] & 0xFF);
    }
  }

  protected void logoff()
    throws SQLException
  {
    try
    {
      if (this.m_nativeState != 0L)
      {
        checkError(t2cLogoff(this.m_nativeState));
      }
    }
    catch (NullPointerException localNullPointerException)
    {
    }
  }

  public void open(OracleStatement paramOracleStatement)
    throws SQLException
  {
    byte[] arrayOfByte = paramOracleStatement.sqlObject.getSql(paramOracleStatement.processEscapes, paramOracleStatement.convertNcharLiterals).getBytes();

    checkError(t2cCreateStatement(this.m_nativeState, 0L, arrayOfByte, arrayOfByte.length, paramOracleStatement, false, paramOracleStatement.rowPrefetch));
  }

  void doCancel()
    throws SQLException
  {
    checkError(t2cCancel(this.m_nativeState));
  }

  native int t2cAbort(long paramLong);

  void doAbort() throws SQLException {
    checkError(t2cAbort(this.m_nativeState));
  }

  protected void doSetAutoCommit(boolean paramBoolean)
    throws SQLException
  {
    checkError(t2cSetAutoCommit(this.m_nativeState, paramBoolean));
  }

  protected void doCommit()
    throws SQLException
  {
    checkError(t2cCommit(this.m_nativeState));
  }

  protected void doRollback()
    throws SQLException
  {
    checkError(t2cRollback(this.m_nativeState));
  }

  protected String doGetDatabaseProductVersion() throws SQLException
  {
    byte[] arrayOfByte = t2cGetProductionVersion(this.m_nativeState);

    return this.conversion.CharBytesToString(arrayOfByte, arrayOfByte.length);
  }

  protected short doGetVersionNumber() throws SQLException
  {
    int i = 0;
    try
    {
      String str1 = doGetDatabaseProductVersion();

      StringTokenizer localStringTokenizer = new StringTokenizer(str1.trim(), " .", false);
      String str2 = null;
      int j = 0;
      int k = 0;

      while (localStringTokenizer.hasMoreTokens())
      {
        str2 = localStringTokenizer.nextToken();
        try
        {
          k = Integer.decode(str2).shortValue();
          i = (short)(i * 10 + k);
          j++;

          if (j == 4) {
            break;
          }

        }
        catch (NumberFormatException localNumberFormatException)
        {
        }

      }

    }
    catch (NoSuchElementException localNoSuchElementException)
    {
    }

    if (i == -1) {
      i = 0;
    }

    return i;
  }

  public ClobDBAccess createClobDBAccess()
  {
    return this;
  }

  public BlobDBAccess createBlobDBAccess()
  {
    return this;
  }

  public BfileDBAccess createBfileDBAccess()
  {
    return this;
  }

  public CLOB createClob(byte[] paramArrayOfByte)
    throws SQLException
  {
    return new CLOB(this, paramArrayOfByte);
  }

  public CLOB createClob(byte[] paramArrayOfByte, short paramShort)
    throws SQLException
  {
    return new CLOB(this, paramArrayOfByte, paramShort);
  }

  public BLOB createBlob(byte[] paramArrayOfByte)
    throws SQLException
  {
    return new BLOB(this, paramArrayOfByte);
  }

  public BFILE createBfile(byte[] paramArrayOfByte)
    throws SQLException
  {
    return new BFILE(this, paramArrayOfByte);
  }

  protected SQLWarning checkError(int paramInt) throws SQLException
  {
    return checkError(paramInt, null);
  }

  protected SQLWarning checkError(int paramInt, SQLWarning paramSQLWarning)
    throws SQLException
  {
    switch (paramInt)
    {
    case 0:
      break;
    case -1:
    case 1:
      T2CError localT2CError = new T2CError();

      int i = -1;

      if (this.m_nativeState != 0L)
      {
        i = t2cDescribeError(this.m_nativeState, localT2CError, localT2CError.m_errorMessage);
      }
      else if (this.fatalErrorNumber != 0)
      {
        String str1 = DatabaseError.ErrorToSQLState(this.fatalErrorNumber);

        DatabaseError.throwSqlException(this.fatalErrorMessage, str1, localT2CError.m_errorNumber);
      }
      else
      {
        DatabaseError.throwSqlException(8);
      }

      switch (localT2CError.m_errorNumber)
      {
      case 28:
      case 600:
      case 1012:
      case 1041:
      case 3113:
      case 3114:
        internalClose();
      }

      if (i == -1) {
        DatabaseError.throwSqlException(1, "Fetch error message failed!");
      }

      int j = 0;

      while ((j < localT2CError.m_errorMessage.length) && (localT2CError.m_errorMessage[j] != 0)) {
        j++;
      }
      String str2 = this.conversion.CharBytesToString(localT2CError.m_errorMessage, j, true);

      String str3 = DatabaseError.ErrorToSQLState(localT2CError.m_errorNumber);

      if (paramInt == -1)
      {
        DatabaseError.throwSqlException(str2, str3, localT2CError.m_errorNumber);
      }
      else
      {
        paramSQLWarning = DatabaseError.addSqlWarning(paramSQLWarning, str2, str3, localT2CError.m_errorNumber);
      }

      break;
    }

    return paramSQLWarning;
  }

  OracleStatement RefCursorBytesToStatement(byte[] paramArrayOfByte, OracleStatement paramOracleStatement)
    throws SQLException
  {
    T2CStatement localT2CStatement = new T2CStatement(this, 1, this.defaultRowPrefetch, -1, -1);

    localT2CStatement.needToParse = false;
    localT2CStatement.serverCursor = true;
    localT2CStatement.isOpen = true;
    localT2CStatement.processEscapes = false;

    localT2CStatement.prepareForNewResults(true, false);
    localT2CStatement.sqlObject.initialize("select unknown as ref cursor from whatever");

    localT2CStatement.sqlKind = 0;

    checkError(t2cCreateStatement(this.m_nativeState, paramOracleStatement.c_state, paramArrayOfByte, paramArrayOfByte.length, localT2CStatement, true, this.defaultRowPrefetch));

    return localT2CStatement;
  }

  public void getForm(OracleTypeADT paramOracleTypeADT, OracleTypeCLOB paramOracleTypeCLOB, int paramInt)
    throws SQLException
  {
    int i = 0;

    if (paramOracleTypeCLOB != null)
    {
      String[] arrayOfString1 = new String[1];
      String[] arrayOfString2 = new String[1];

      SQLName.parse(paramOracleTypeADT.getFullName(), arrayOfString1, arrayOfString2, true);

      String str = "\"" + arrayOfString1[0] + "\".\"" + arrayOfString2[0] + "\"";

      byte[] arrayOfByte = this.conversion.StringToCharBytes(str);

      int j = t2cGetFormOfUse(this.m_nativeState, paramOracleTypeCLOB, arrayOfByte, arrayOfByte.length, paramInt);

      if (j < 0) {
        checkError(j);
      }

      paramOracleTypeCLOB.setForm(j);
    }
  }

  public long getTdoCState(String paramString1, String paramString2)
    throws SQLException
  {
    String str = "\"" + paramString1 + "\".\"" + paramString2 + "\"";
    byte[] arrayOfByte = this.conversion.StringToCharBytes(str);
    int[] arrayOfInt = new int[1];
    long l = t2cGetTDO(this.m_nativeState, arrayOfByte, arrayOfByte.length, arrayOfInt);
    if (l == 0L)
    {
      checkError(arrayOfInt[0]);
    }

    return l;
  }

  /** @deprecated */
  public Properties getDBAccessProperties()
    throws SQLException
  {
    return getOCIHandles();
  }

  public synchronized Properties getOCIHandles() throws SQLException
  {
    if (this.nativeInfo == null)
    {
      long[] arrayOfLong = new long[3];

      checkError(t2cGetHandles(this.m_nativeState, arrayOfLong));

      this.nativeInfo = new Properties();

      this.nativeInfo.put("OCIEnvHandle", String.valueOf(arrayOfLong[0]));
      this.nativeInfo.put("OCISvcCtxHandle", String.valueOf(arrayOfLong[1]));
      this.nativeInfo.put("OCIErrHandle", String.valueOf(arrayOfLong[2]));
      this.nativeInfo.put("ClientCharSet", String.valueOf(this.m_clientCharacterSet));
    }

    return this.nativeInfo;
  }

  public Properties getServerSessionInfo() throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    Properties localProperties = new Properties();
    checkError(t2cGetServerSessionInfo(this.m_nativeState, localProperties));

    return localProperties;
  }

  public Properties getConnectionPoolInfo()
    throws SQLException
  {
    if (this.lifecycle != 1)
      DatabaseError.throwSqlException(8);
    Properties localProperties = new Properties();

    checkError(t2cGetConnPoolInfo(this.m_nativeState, localProperties));

    return localProperties;
  }

  public void setConnectionPoolInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    throws SQLException
  {
    checkError(t2cSetConnPoolInfo(this.m_nativeState, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6));
  }

  public void ociPasswordChange(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    if (this.lifecycle != 1)
      DatabaseError.throwSqlException(8);
    byte[] arrayOfByte1 = paramString1 == null ? new byte[0] : DBConversion.stringToDriverCharBytes(paramString1, this.m_clientCharacterSet);

    byte[] arrayOfByte2 = paramString2 == null ? new byte[0] : DBConversion.stringToAsciiBytes(paramString2);

    byte[] arrayOfByte3 = paramString3 == null ? new byte[0] : DBConversion.stringToAsciiBytes(paramString3);

    this.sqlWarning = checkError(t2cPasswordChange(this.m_nativeState, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length), this.sqlWarning);
  }

  private void processOCIConnectionPooling()
    throws SQLException
  {
    if (this.lifecycle != 1)
      DatabaseError.throwSqlException(8);
    byte[] arrayOfByte1 = null;

    byte[] arrayOfByte2 = this.password == null ? new byte[0] : DBConversion.stringToAsciiBytes(this.password);

    byte[] arrayOfByte3 = DBConversion.stringToAsciiBytes(this.database);

    byte[] arrayOfByte4 = CharacterSetMetaData.getNLSLanguage(Locale.getDefault()).getBytes();

    byte[] arrayOfByte5 = CharacterSetMetaData.getNLSTerritory(Locale.getDefault()).getBytes();

    String str1 = this.connectionProperties.getProperty("connection_pool");

    short[] arrayOfShort = new short[3];
    Object localObject1;
    Object localObject2;
    if (str1 == "connection_pool")
    {
      localObject1 = this.connectionProperties.getProperty("oracle.jdbc.ociNlsLangBackwardCompatible");

      if ((localObject1 != null) && (((String)localObject1).equalsIgnoreCase("true")))
      {
        this.m_clientCharacterSet = getDriverCharSetIdFromNLS_LANG(this.connectionProperties);
      }
      else
      {
        this.m_clientCharacterSet = getClientCharSetId();
      }

      arrayOfByte1 = this.user == null ? new byte[0] : DBConversion.stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

      this.conversion = new DBConversion(this.m_clientCharacterSet, this.m_clientCharacterSet, this.m_clientCharacterSet);

      this.logon_mode = 5;

      if (this.m_nativeState == 0L)
      {
        localObject2 = new int[6];

        OracleOCIConnectionPool.readPoolConfig(this.connectionProperties, localObject2);

        this.sqlWarning = checkError(t2cCreateConnPool(arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, this.m_clientCharacterSet, this.logon_mode, localObject2[0], localObject2[1], localObject2[2], localObject2[3], localObject2[4], localObject2[5]), this.sqlWarning);

        this.versionNumber = 10000;
      }
      else
      {
        throw DatabaseError.newSqlException(0, "Internal Error: ");
      }

    }
    else if (str1 == "connpool_connection")
    {
      this.logon_mode = 6;

      localObject1 = (T2CConnection)this.connectionProperties.get("connpool_object");

      this.m_clientCharacterSet = ((T2CConnection)localObject1).m_clientCharacterSet;

      arrayOfByte1 = this.user == null ? new byte[0] : DBConversion.stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

      this.conversion = new DBConversion(this.m_clientCharacterSet, this.m_clientCharacterSet, this.m_clientCharacterSet);

      this.sqlWarning = checkError(t2cConnPoolLogon(((T2CConnection)localObject1).m_nativeState, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, this.logon_mode, 0, 0, null, null, 0, null, 0, null, 0, null, 0, null, 0, arrayOfShort, arrayOfByte4, arrayOfByte5), this.sqlWarning);
    }
    else if (str1 == "connpool_alias_connection")
    {
      this.logon_mode = 8;

      localObject1 = null;

      localObject1 = (byte[])this.connectionProperties.get("connection_id");

      localObject2 = (T2CConnection)this.connectionProperties.get("connpool_object");

      this.m_clientCharacterSet = ((T2CConnection)localObject2).m_clientCharacterSet;

      arrayOfByte1 = this.user == null ? new byte[0] : DBConversion.stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

      this.conversion = new DBConversion(this.m_clientCharacterSet, this.m_clientCharacterSet, this.m_clientCharacterSet);

      this.sqlWarning = checkError(t2cConnPoolLogon(((T2CConnection)localObject2).m_nativeState, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, this.logon_mode, 0, 0, null, null, 0, null, 0, null, 0, null, 0, localObject1, localObject1 == null ? 0 : localObject1.length, arrayOfShort, arrayOfByte4, arrayOfByte5), this.sqlWarning);
    }
    else if (str1 == "connpool_proxy_connection")
    {
      this.logon_mode = 7;

      localObject1 = this.connectionProperties.getProperty("proxytype");

      localObject2 = (Integer)this.connectionProperties.get("proxy_num_roles");

      int i = ((Integer)localObject2).intValue();

      String[] arrayOfString = null;

      if (i > 0)
      {
        arrayOfString = (String[])this.connectionProperties.get("proxy_roles");
      }

      byte[] arrayOfByte6 = null;
      byte[] arrayOfByte7 = null;
      byte[] arrayOfByte8 = null;
      byte[] arrayOfByte9 = null;

      int j = 0;
      String str2;
      if (localObject1 == "proxytype_user_name")
      {
        j = 1;

        str2 = this.connectionProperties.getProperty("proxy_user_name");

        if (str2 != null) {
          arrayOfByte6 = str2.getBytes();
        }
        str2 = this.connectionProperties.getProperty("proxy_password");

        if (str2 != null)
          arrayOfByte7 = str2.getBytes();
      }
      else if (localObject1 == "proxytype_distinguished_name")
      {
        j = 2;

        str2 = this.connectionProperties.getProperty("proxy_distinguished_name");

        if (str2 != null)
          arrayOfByte8 = str2.getBytes();
      }
      else if (localObject1 == "proxytype_certificate")
      {
        j = 3;

        arrayOfByte9 = (byte[])this.connectionProperties.get("proxy_certificate");
      }
      else
      {
        DatabaseError.throwSqlException(107);
      }

      T2CConnection localT2CConnection = (T2CConnection)this.connectionProperties.get("connpool_object");

      this.m_clientCharacterSet = localT2CConnection.m_clientCharacterSet;

      arrayOfByte1 = this.user == null ? new byte[0] : DBConversion.stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

      this.conversion = new DBConversion(this.m_clientCharacterSet, this.m_clientCharacterSet, this.m_clientCharacterSet);

      this.sqlWarning = checkError(t2cConnPoolLogon(localT2CConnection.m_nativeState, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, this.logon_mode, j, i, arrayOfString, arrayOfByte6, arrayOfByte6 == null ? 0 : arrayOfByte6.length, arrayOfByte7, arrayOfByte7 == null ? 0 : arrayOfByte7.length, arrayOfByte8, arrayOfByte8 == null ? 0 : arrayOfByte8.length, arrayOfByte9, arrayOfByte9 == null ? 0 : arrayOfByte9.length, null, 0, arrayOfShort, arrayOfByte4, arrayOfByte5), this.sqlWarning);
    }
    else
    {
      DatabaseError.throwSqlException(23, "connection-pool-logon");
    }

    this.conversion = new DBConversion(arrayOfShort[0], this.m_clientCharacterSet, arrayOfShort[1]);
    this.byteAlign = (byte)(arrayOfShort[2] & 0xFF);
  }

  public boolean isDescriptorSharable(OracleConnection paramOracleConnection)
    throws SQLException
  {
    T2CConnection localT2CConnection = this;
    PhysicalConnection localPhysicalConnection = (PhysicalConnection)paramOracleConnection.getPhysicalConnection();

    return localT2CConnection == localPhysicalConnection;
  }

  native int blobRead(long paramLong1, byte[] paramArrayOfByte1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte2, int paramInt3);

  native int clobRead(long paramLong1, byte[] paramArrayOfByte, int paramInt1, long paramLong2, int paramInt2, char[] paramArrayOfChar, int paramInt3, boolean paramBoolean);

  native int blobWrite(long paramLong1, byte[] paramArrayOfByte1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, byte[][] paramArrayOfByte);

  native int clobWrite(long paramLong1, byte[] paramArrayOfByte, int paramInt1, long paramLong2, int paramInt2, char[] paramArrayOfChar, int paramInt3, byte[][] paramArrayOfByte1, boolean paramBoolean);

  native long lobGetLength(long paramLong, byte[] paramArrayOfByte, int paramInt);

  native int bfileOpen(long paramLong, byte[] paramArrayOfByte, int paramInt, byte[][] paramArrayOfByte1);

  native int bfileIsOpen(long paramLong, byte[] paramArrayOfByte, int paramInt, boolean[] paramArrayOfBoolean);

  native int bfileExists(long paramLong, byte[] paramArrayOfByte, int paramInt, boolean[] paramArrayOfBoolean);

  native String bfileGetName(long paramLong, byte[] paramArrayOfByte, int paramInt);

  native String bfileGetDirAlias(long paramLong, byte[] paramArrayOfByte, int paramInt);

  native int bfileClose(long paramLong, byte[] paramArrayOfByte, int paramInt, byte[][] paramArrayOfByte1);

  native int lobGetChunkSize(long paramLong, byte[] paramArrayOfByte, int paramInt);

  native int lobTrim(long paramLong1, int paramInt1, long paramLong2, byte[] paramArrayOfByte, int paramInt2, byte[][] paramArrayOfByte1);

  native int lobCreateTemporary(long paramLong, int paramInt1, boolean paramBoolean, int paramInt2, short paramShort, byte[][] paramArrayOfByte);

  native int lobFreeTemporary(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, byte[][] paramArrayOfByte1);

  native int lobIsTemporary(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, boolean[] paramArrayOfBoolean);

  native int lobOpen(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, byte[][] paramArrayOfByte1);

  native int lobIsOpen(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, boolean[] paramArrayOfBoolean);

  native int lobClose(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2, byte[][] paramArrayOfByte1);

  private long lobLength(byte[] paramArrayOfByte)
    throws SQLException
  {
    long l = 0L;

    l = lobGetLength(this.m_nativeState, paramArrayOfByte, paramArrayOfByte.length);

    checkError((int)l);

    return l;
  }

  private int blobRead(byte[] paramArrayOfByte1, long paramLong, int paramInt, byte[] paramArrayOfByte2)
    throws SQLException
  {
    int i = 0;

    i = blobRead(this.m_nativeState, paramArrayOfByte1, paramArrayOfByte1.length, paramLong, paramInt, paramArrayOfByte2, paramArrayOfByte2.length);

    checkError(i);

    return i;
  }

  private int blobWrite(byte[] paramArrayOfByte1, long paramLong, byte[] paramArrayOfByte2, byte[][] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    int i = 0;

    i = blobWrite(this.m_nativeState, paramArrayOfByte1, paramArrayOfByte1.length, paramLong, paramInt2, paramArrayOfByte2, paramInt1, paramArrayOfByte);

    checkError(i);

    return i;
  }

  private int clobWrite(byte[] paramArrayOfByte, long paramLong, char[] paramArrayOfChar, byte[][] paramArrayOfByte1, boolean paramBoolean, int paramInt1, int paramInt2)
    throws SQLException
  {
    int i = 0;

    i = clobWrite(this.m_nativeState, paramArrayOfByte, paramArrayOfByte.length, paramLong, paramInt2, paramArrayOfChar, paramInt1, paramArrayOfByte1, paramBoolean);

    checkError(i);

    return i;
  }

  private int lobGetChunkSize(byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = 0;

    i = lobGetChunkSize(this.m_nativeState, paramArrayOfByte, paramArrayOfByte.length);

    checkError(i);

    return i;
  }

  public long length(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    return lobLength(arrayOfByte);
  }

  public long position(BFILE paramBFILE, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.hasPattern(paramBFILE, paramArrayOfByte, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public long position(BFILE paramBFILE1, BFILE paramBFILE2, long paramLong)
    throws SQLException
  {
    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.isSubLob(paramBFILE1, paramBFILE2, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public int getBytes(BFILE paramBFILE, long paramLong, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    if ((paramInt <= 0) || (paramArrayOfByte == null)) {
      return 0;
    }
    if (paramInt > paramArrayOfByte.length) {
      paramInt = paramArrayOfByte.length;
    }
    return blobRead(arrayOfByte, paramLong, paramInt, paramArrayOfByte);
  }

  public String getName(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;
    String str = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    str = bfileGetName(this.m_nativeState, arrayOfByte, arrayOfByte.length);

    checkError(str.length());

    return str;
  }

  public String getDirAlias(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;
    String str = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    str = bfileGetDirAlias(this.m_nativeState, arrayOfByte, arrayOfByte.length);

    checkError(str.length());

    return str;
  }

  public void openFile(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(bfileOpen(this.m_nativeState, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramBFILE.setLocator(arrayOfByte1[0]);
  }

  public boolean isFileOpen(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(bfileIsOpen(this.m_nativeState, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public boolean fileExists(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(bfileExists(this.m_nativeState, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public void closeFile(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.getLocator()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(bfileClose(this.m_nativeState, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramBFILE.setLocator(arrayOfByte1[0]);
  }

  public void open(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobOpen(this.m_nativeState, 114, arrayOfByte, arrayOfByte.length, paramInt, arrayOfByte1));

    paramBFILE.setShareBytes(arrayOfByte1[0]);
  }

  public void close(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobClose(this.m_nativeState, 114, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramBFILE.setShareBytes(arrayOfByte1[0]);
  }

  public boolean isOpen(BFILE paramBFILE)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBFILE != null) && ((arrayOfByte = paramBFILE.shareBytes()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(lobIsOpen(this.m_nativeState, 114, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public InputStream newInputStream(BFILE paramBFILE, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleBlobInputStream(paramBFILE, paramInt);
    }

    return new OracleBlobInputStream(paramBFILE, paramInt, paramLong);
  }

  public InputStream newConversionInputStream(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    checkTrue((paramBFILE != null) && (paramBFILE.shareBytes() != null), 54);

    OracleConversionInputStream localOracleConversionInputStream = new OracleConversionInputStream(this.conversion, paramBFILE.getBinaryStream(), paramInt);

    return localOracleConversionInputStream;
  }

  public Reader newConversionReader(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    checkTrue((paramBFILE != null) && (paramBFILE.shareBytes() != null), 54);

    OracleConversionReader localOracleConversionReader = new OracleConversionReader(this.conversion, paramBFILE.getBinaryStream(), paramInt);

    return localOracleConversionReader;
  }

  public long length(BLOB paramBLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.getLocator()) != null), 54);

    return lobLength(arrayOfByte);
  }

  public long position(BLOB paramBLOB, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && (paramBLOB.shareBytes() != null), 54);

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.hasPattern(paramBLOB, paramArrayOfByte, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public long position(BLOB paramBLOB1, BLOB paramBLOB2, long paramLong)
    throws SQLException
  {
    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB1 != null) && (paramBLOB1.shareBytes() != null), 54);

    checkTrue((paramBLOB2 != null) && (paramBLOB2.shareBytes() != null), 54);

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.isSubLob(paramBLOB1, paramBLOB2, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public int getBytes(BLOB paramBLOB, long paramLong, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.getLocator()) != null), 54);

    if ((paramInt <= 0) || (paramArrayOfByte == null)) {
      return 0;
    }
    if (paramInt > paramArrayOfByte.length) {
      paramInt = paramArrayOfByte.length;
    }
    return blobRead(arrayOfByte, paramLong, paramInt, paramArrayOfByte);
  }

  public int putBytes(BLOB paramBLOB, long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    checkTrue(paramLong >= 0L, 68);

    int i = 0;

    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0) || (paramInt2 <= 0)) {
      i = 0;
    }
    else {
      byte[] arrayOfByte = null;

      checkTrue(this.m_nativeState != 0L, 8);
      checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.getLocator()) != null), 54);

      if (paramArrayOfByte == null) {
        return 0;
      }
      byte[][] arrayOfByte1 = new byte[1][];

      i = blobWrite(arrayOfByte, paramLong, paramArrayOfByte, arrayOfByte1, paramInt1, paramInt2);

      paramBLOB.setLocator(arrayOfByte1[0]);
    }

    return i;
  }

  public int getChunkSize(BLOB paramBLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.getLocator()) != null), 54);

    return lobGetChunkSize(arrayOfByte);
  }

  public void trim(BLOB paramBLOB, long paramLong)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobTrim(this.m_nativeState, 113, paramLong, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramBLOB.setShareBytes(arrayOfByte1[0]);
  }

  public BLOB createTemporaryBlob(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException
  {
    BLOB localBLOB = null;

    checkTrue(this.m_nativeState != 0L, 8);

    localBLOB = new BLOB((PhysicalConnection)paramConnection);

    byte[][] arrayOfByte = new byte[1][];

    checkError(lobCreateTemporary(this.m_nativeState, 113, paramBoolean, paramInt, 0, arrayOfByte));

    localBLOB.setShareBytes(arrayOfByte[0]);

    return localBLOB;
  }

  public void freeTemporary(BLOB paramBLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobFreeTemporary(this.m_nativeState, 113, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramBLOB.setShareBytes(arrayOfByte1[0]);
  }

  public boolean isTemporary(BLOB paramBLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.shareBytes()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(lobIsTemporary(this.m_nativeState, 113, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public void open(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobOpen(this.m_nativeState, 113, arrayOfByte, arrayOfByte.length, paramInt, arrayOfByte1));

    paramBLOB.setShareBytes(arrayOfByte1[0]);
  }

  public void close(BLOB paramBLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobClose(this.m_nativeState, 113, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramBLOB.setShareBytes(arrayOfByte1[0]);
  }

  public boolean isOpen(BLOB paramBLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramBLOB != null) && ((arrayOfByte = paramBLOB.shareBytes()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(lobIsOpen(this.m_nativeState, 113, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public InputStream newInputStream(BLOB paramBLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleBlobInputStream(paramBLOB, paramInt);
    }

    return new OracleBlobInputStream(paramBLOB, paramInt, paramLong);
  }

  public OutputStream newOutputStream(BLOB paramBLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleBlobOutputStream(paramBLOB, paramInt);
    }

    return new OracleBlobOutputStream(paramBLOB, paramInt, paramLong);
  }

  public InputStream newConversionInputStream(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    checkTrue((paramBLOB != null) && (paramBLOB.shareBytes() != null), 54);

    OracleConversionInputStream localOracleConversionInputStream = new OracleConversionInputStream(this.conversion, paramBLOB.getBinaryStream(), paramInt);

    return localOracleConversionInputStream;
  }

  public Reader newConversionReader(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    checkTrue((paramBLOB != null) && (paramBLOB.shareBytes() != null), 54);

    OracleConversionReader localOracleConversionReader = new OracleConversionReader(this.conversion, paramBLOB.getBinaryStream(), paramInt);

    return localOracleConversionReader;
  }

  public long length(CLOB paramCLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.getLocator()) != null), 54);

    return lobLength(arrayOfByte);
  }

  public long position(CLOB paramCLOB, String paramString, long paramLong)
    throws SQLException
  {
    if (paramString == null) {
      throw new SQLException("pattern cannot be null.");
    }
    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && (paramCLOB.shareBytes() != null), 54);

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    char[] arrayOfChar = new char[paramString.length()];

    paramString.getChars(0, arrayOfChar.length, arrayOfChar, 0);

    long l = LobPlsqlUtil.hasPattern(paramCLOB, arrayOfChar, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public long position(CLOB paramCLOB1, CLOB paramCLOB2, long paramLong)
    throws SQLException
  {
    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB1 != null) && (paramCLOB1.shareBytes() != null), 54);

    checkTrue((paramCLOB2 != null) && (paramCLOB2.shareBytes() != null), 54);

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.isSubLob(paramCLOB1, paramCLOB2, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public int getChars(CLOB paramCLOB, long paramLong, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.getLocator()) != null), 54);

    if ((paramInt <= 0) || (paramArrayOfChar == null)) {
      return 0;
    }
    if (paramInt > paramArrayOfChar.length) {
      paramInt = paramArrayOfChar.length;
    }
    int i = 0;

    i = clobRead(this.m_nativeState, arrayOfByte, arrayOfByte.length, paramLong, paramInt, paramArrayOfChar, paramArrayOfChar.length, paramCLOB.isNCLOB());

    checkError(i);

    return i;
  }

  public int putChars(CLOB paramCLOB, long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue(paramLong >= 0L, 68);

    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.getLocator()) != null), 54);

    if (paramArrayOfChar == null) {
      return 0;
    }
    byte[][] arrayOfByte1 = new byte[1][];
    int i = clobWrite(arrayOfByte, paramLong, paramArrayOfChar, arrayOfByte1, paramCLOB.isNCLOB(), paramInt1, paramInt2);

    paramCLOB.setLocator(arrayOfByte1[0]);

    return i;
  }

  public int getChunkSize(CLOB paramCLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.getLocator()) != null), 54);

    return lobGetChunkSize(arrayOfByte);
  }

  public void trim(CLOB paramCLOB, long paramLong)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobTrim(this.m_nativeState, 112, paramLong, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramCLOB.setShareBytes(arrayOfByte1[0]);
  }

  public CLOB createTemporaryClob(Connection paramConnection, boolean paramBoolean, int paramInt, short paramShort)
    throws SQLException
  {
    CLOB localCLOB = null;

    checkTrue(this.m_nativeState != 0L, 8);

    localCLOB = new CLOB((PhysicalConnection)paramConnection);

    byte[][] arrayOfByte = new byte[1][];

    checkError(lobCreateTemporary(this.m_nativeState, 112, paramBoolean, paramInt, paramShort, arrayOfByte));

    localCLOB.setShareBytes(arrayOfByte[0]);

    return localCLOB;
  }

  public void freeTemporary(CLOB paramCLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobFreeTemporary(this.m_nativeState, 112, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramCLOB.setShareBytes(arrayOfByte1[0]);
  }

  public boolean isTemporary(CLOB paramCLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.shareBytes()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(lobIsTemporary(this.m_nativeState, 112, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public void open(CLOB paramCLOB, int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobOpen(this.m_nativeState, 112, arrayOfByte, arrayOfByte.length, paramInt, arrayOfByte1));

    paramCLOB.setShareBytes(arrayOfByte1[0]);
  }

  public void close(CLOB paramCLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.shareBytes()) != null), 54);

    byte[][] arrayOfByte1 = new byte[1][];

    checkError(lobClose(this.m_nativeState, 112, arrayOfByte, arrayOfByte.length, arrayOfByte1));

    paramCLOB.setShareBytes(arrayOfByte1[0]);
  }

  public boolean isOpen(CLOB paramCLOB)
    throws SQLException
  {
    byte[] arrayOfByte = null;

    checkTrue(this.m_nativeState != 0L, 8);
    checkTrue((paramCLOB != null) && ((arrayOfByte = paramCLOB.shareBytes()) != null), 54);

    boolean[] arrayOfBoolean = new boolean[1];

    checkError(lobIsOpen(this.m_nativeState, 112, arrayOfByte, arrayOfByte.length, arrayOfBoolean));

    return arrayOfBoolean[0];
  }

  public InputStream newInputStream(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobInputStream(paramCLOB, paramInt);
    }

    return new OracleClobInputStream(paramCLOB, paramInt, paramLong);
  }

  public OutputStream newOutputStream(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobOutputStream(paramCLOB, paramInt);
    }

    return new OracleClobOutputStream(paramCLOB, paramInt, paramLong);
  }

  public Reader newReader(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobReader(paramCLOB, paramInt);
    }

    return new OracleClobReader(paramCLOB, paramInt, paramLong);
  }

  public Writer newWriter(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobWriter(paramCLOB, paramInt);
    }

    return new OracleClobWriter(paramCLOB, paramInt, paramLong);
  }

  public synchronized void registerTAFCallback(OracleOCIFailover paramOracleOCIFailover, Object paramObject)
    throws SQLException
  {
    this.appCallback = paramOracleOCIFailover;
    this.appCallbackObject = paramObject;

    checkError(t2cRegisterTAFCallback(this.m_nativeState));
  }

  synchronized int callTAFCallbackMethod(int paramInt1, int paramInt2)
  {
    int i = 0;

    if (this.appCallback != null) {
      i = this.appCallback.callbackFn(this, this.appCallbackObject, paramInt1, paramInt2);
    }
    return i;
  }

  public int getHeapAllocSize()
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }

    int i = t2cGetHeapAllocSize(this.m_nativeState);

    if (i < 0)
    {
      if (i == -999) {
        DatabaseError.throwSqlException(23);
      }

      DatabaseError.throwSqlException(89);
    }

    return i;
  }

  public int getOCIEnvHeapAllocSize()
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }

    int i = t2cGetOciEnvHeapAllocSize(this.m_nativeState);

    if (i < 0)
    {
      if (i == -999)
        DatabaseError.throwSqlException(23);
      else {
        checkError(i);
      }

      DatabaseError.throwSqlException(89);
    }

    return i;
  }

  public static final short getClientCharSetId()
  {
    return 871;
  }

  public static short getDriverCharSetIdFromNLS_LANG(Properties paramProperties)
    throws SQLException
  {
    if (!isLibraryLoaded) {
      loadNativeLibrary(paramProperties);
    }

    int i = t2cGetDriverCharSetFromNlsLang();

    if (i < 0) {
      DatabaseError.throwSqlException(8);
    }

    return i;
  }

  void doProxySession(int paramInt, Properties paramProperties)
    throws SQLException
  {
    Object localObject1 = (byte[][])null;

    int i = 0;

    this.savedUser = this.user;
    this.user = null;
    byte[] arrayOfByte4;
    byte[] arrayOfByte3;
    byte[] arrayOfByte2;
    byte[] arrayOfByte1 = arrayOfByte2 = arrayOfByte3 = arrayOfByte4 = new byte[0];

    switch (paramInt)
    {
    case 1:
      this.user = paramProperties.getProperty("PROXY_USER_NAME");
      String str1 = paramProperties.getProperty("PROXY_USER_PASSWORD");
      if (this.user != null) {
        arrayOfByte1 = DBConversion.stringToDriverCharBytes(this.user, this.m_clientCharacterSet);
      }
      if (str1 == null) break;
      arrayOfByte2 = DBConversion.stringToAsciiBytes(str1); break;
    case 2:
      String str2 = paramProperties.getProperty("PROXY_DISTINGUISHED_NAME");
      if (str2 == null) break;
      arrayOfByte3 = DBConversion.stringToDriverCharBytes(str2, this.m_clientCharacterSet); break;
    case 3:
      Object localObject2 = paramProperties.get("PROXY_CERTIFICATE");
      arrayOfByte4 = (byte[])localObject2;
    }

    String[] arrayOfString = (String[])paramProperties.get("PROXY_ROLES");

    if (arrayOfString != null)
    {
      i = arrayOfString.length;
      localObject1 = new byte[i][];
      for (int j = 0; j < i; j++)
      {
        if (arrayOfString[j] == null) {
          DatabaseError.throwSqlException(150);
        }
        localObject1[j] = DBConversion.stringToDriverCharBytes(arrayOfString[j], this.m_clientCharacterSet);
      }

    }

    this.sqlWarning = checkError(t2cDoProxySession(this.m_nativeState, paramInt, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, arrayOfByte4, arrayOfByte4.length, i, localObject1), this.sqlWarning);

    this.isProxy = true;
  }

  void closeProxySession() throws SQLException
  {
    checkError(t2cCloseProxySession(this.m_nativeState));
    this.user = this.savedUser;
  }

  protected void doDescribeTable(AutoKeyInfo paramAutoKeyInfo) throws SQLException
  {
    String str = paramAutoKeyInfo.getTableName();

    byte[] arrayOfByte = DBConversion.stringToDriverCharBytes(str, this.m_clientCharacterSet);

    int i = 0;
    int j;
    do {
      j = t2cDescribeTable(this.m_nativeState, arrayOfByte, arrayOfByte.length, this.queryMetaData1, this.queryMetaData2, this.queryMetaData1Offset, this.queryMetaData2Offset, this.queryMetaData1Size, this.queryMetaData2Size);

      if (j == -1)
      {
        checkError(j);
      }

      if (j != T2CStatement.T2C_EXTEND_BUFFER)
        continue;
      i = 1;

      reallocateQueryMetaData(this.queryMetaData1Size * 2, this.queryMetaData2Size * 2);
    }

    while (i != 0);

    processDescribeTableData(j, paramAutoKeyInfo);
  }

  private void processDescribeTableData(int paramInt, AutoKeyInfo paramAutoKeyInfo)
    throws SQLException
  {
    short[] arrayOfShort = this.queryMetaData1;
    byte[] arrayOfByte = this.queryMetaData2;
    int i = this.queryMetaData1Offset;
    int j = this.queryMetaData2Offset;

    paramAutoKeyInfo.allocateSpaceForDescribedData(paramInt);

    for (int i5 = 0; i5 < paramInt; i5++)
    {
      int m = arrayOfShort[(i + 0)];
      int k = arrayOfShort[(i + 6)];
      String str1 = bytes2String(arrayOfByte, j, k, this.conversion);

      int n = arrayOfShort[(i + 1)];
      int i1 = arrayOfShort[(i + 11)];
      boolean bool = arrayOfShort[(i + 2)] != 0;
      short s = arrayOfShort[(i + 5)];
      int i2 = arrayOfShort[(i + 3)];
      int i3 = arrayOfShort[(i + 4)];
      int i4 = arrayOfShort[(i + 12)];

      j += k;
      i += 13;

      String str2 = null;
      if (i4 > 0)
      {
        str2 = bytes2String(arrayOfByte, j, i4, this.conversion);

        j += i4;
      }

      paramAutoKeyInfo.fillDescribedData(i5, str1, m, i1 > 0 ? i1 : n, bool, s, i2, i3, str2);
    }
  }

  void doSetApplicationContext(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    checkError(t2cSetApplicationContext(this.m_nativeState, paramString1, paramString2, paramString3));
  }

  void doClearAllApplicationContext(String paramString)
    throws SQLException
  {
    checkError(t2cClearAllApplicationContext(this.m_nativeState, paramString));
  }

  private static final void loadNativeLibrary(Properties paramProperties)
    throws SQLException
  {
    String str = null;
    if (paramProperties != null) {
      str = paramProperties.getProperty("oracle.jdbc.ocinativelibrary");
    }

    if ((str == null) || (str.equals("ocijdbc10")))
    {
      synchronized (T2CConnection.class)
      {
        if (!isLibraryLoaded)
        {
          AccessController.doPrivileged(new PrivilegedAction()
          {
            public Object run()
            {
              System.loadLibrary("ocijdbc10");
              return null;
            }
          });
          isLibraryLoaded = true;
        }

      }

    }

    synchronized (T2CConnection.class)
    {
      try
      {
        System.loadLibrary(str);
        isLibraryLoaded = true;
      }
      catch (SecurityException localSecurityException)
      {
        if (!isLibraryLoaded)
        {
          System.loadLibrary(str);
          isLibraryLoaded = true;
        }
      }
    }
  }

  private final void checkTrue(boolean paramBoolean, int paramInt)
    throws SQLException
  {
    if (!paramBoolean)
      DatabaseError.throwSqlException(paramInt);
  }

  boolean useLittleEndianSetCHARBinder() throws SQLException
  {
    return t2cPlatformIsLittleEndian(this.m_nativeState);
  }

  public void getPropertyForPooledConnection(OraclePooledConnection paramOraclePooledConnection)
    throws SQLException
  {
    super.getPropertyForPooledConnection(paramOraclePooledConnection, this.password);
  }

  static final char[] getCharArray(String paramString)
  {
    char[] arrayOfChar = null;

    if (paramString == null)
    {
      arrayOfChar = new char[0];
    }
    else
    {
      arrayOfChar = new char[paramString.length()];

      paramString.getChars(0, paramString.length(), arrayOfChar, 0);
    }

    return arrayOfChar;
  }

  static String bytes2String(byte[] paramArrayOfByte, int paramInt1, int paramInt2, DBConversion paramDBConversion)
    throws SQLException
  {
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);

    return paramDBConversion.CharBytesToString(arrayOfByte, paramInt2);
  }

  static native short t2cGetServerSessionInfo(long paramLong, Properties paramProperties);

  static native short t2cGetDriverCharSetFromNlsLang();

  native int t2cDescribeError(long paramLong, T2CError paramT2CError, byte[] paramArrayOfByte);

  native int t2cCreateState(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3, byte[] paramArrayOfByte4, int paramInt4, short paramShort, int paramInt5, short[] paramArrayOfShort, byte[] paramArrayOfByte5, byte[] paramArrayOfByte6);

  native int t2cLogon(long paramLong, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3, byte[] paramArrayOfByte4, int paramInt4, int paramInt5, short[] paramArrayOfShort, byte[] paramArrayOfByte5, byte[] paramArrayOfByte6);

  private native int t2cLogoff(long paramLong);

  private native int t2cCancel(long paramLong);

  private native int t2cCreateStatement(long paramLong1, long paramLong2, byte[] paramArrayOfByte, int paramInt1, OracleStatement paramOracleStatement, boolean paramBoolean, int paramInt2);

  private native int t2cSetAutoCommit(long paramLong, boolean paramBoolean);

  private native int t2cCommit(long paramLong);

  private native int t2cRollback(long paramLong);

  private native byte[] t2cGetProductionVersion(long paramLong);

  private native int t2cGetVersionNumber(long paramLong);

  private native int t2cGetDefaultStreamChunkSize(long paramLong);

  native int t2cGetFormOfUse(long paramLong, OracleTypeCLOB paramOracleTypeCLOB, byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  native long t2cGetTDO(long paramLong, byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt);

  native int t2cCreateConnPool(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3, short paramShort, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9, int paramInt10);

  native int t2cConnPoolLogon(long paramLong, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3, int paramInt4, int paramInt5, int paramInt6, String[] paramArrayOfString, byte[] paramArrayOfByte4, int paramInt7, byte[] paramArrayOfByte5, int paramInt8, byte[] paramArrayOfByte6, int paramInt9, byte[] paramArrayOfByte7, int paramInt10, byte[] paramArrayOfByte8, int paramInt11, short[] paramArrayOfShort, byte[] paramArrayOfByte9, byte[] paramArrayOfByte10);

  native int t2cGetConnPoolInfo(long paramLong, Properties paramProperties);

  native int t2cSetConnPoolInfo(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);

  native int t2cPasswordChange(long paramLong, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3);

  protected native byte[] t2cGetConnectionId(long paramLong);

  native int t2cGetHandles(long paramLong, long[] paramArrayOfLong);

  native int t2cUseConnection(long paramLong1, long paramLong2, long paramLong3, long paramLong4, short[] paramArrayOfShort);

  native boolean t2cPlatformIsLittleEndian(long paramLong);

  native int t2cRegisterTAFCallback(long paramLong);

  native int t2cGetHeapAllocSize(long paramLong);

  native int t2cGetOciEnvHeapAllocSize(long paramLong);

  native int t2cDoProxySession(long paramLong, int paramInt1, byte[] paramArrayOfByte1, int paramInt2, byte[] paramArrayOfByte2, int paramInt3, byte[] paramArrayOfByte3, int paramInt4, byte[] paramArrayOfByte4, int paramInt5, int paramInt6, byte[][] paramArrayOfByte);

  native int t2cCloseProxySession(long paramLong);

  static native int t2cDescribeTable(long paramLong, byte[] paramArrayOfByte1, int paramInt1, short[] paramArrayOfShort, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4, int paramInt5);

  native int t2cSetApplicationContext(long paramLong, String paramString1, String paramString2, String paramString3);

  native int t2cClearAllApplicationContext(long paramLong, String paramString);
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T2CConnection
 * JD-Core Version:    0.6.0
 */