package oracle.jdbc.driver;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import javax.transaction.xa.XAResource;
import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCLOB;
import oracle.jdbc.oracore.Util;
import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.BfileDBAccess;
import oracle.sql.BlobDBAccess;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.ClobDBAccess;
import oracle.sql.CustomDatum;
import oracle.sql.Datum;
import oracle.sql.StructDescriptor;
import oracle.sql.TypeDescriptor;

abstract class PhysicalConnection extends OracleConnection
{
  char[][] charOutput = new char[1][];
  byte[][] byteOutput = new byte[1][];
  short[][] shortOutput = new short[1][];
  String url;
  String user;
  String savedUser;
  String database;
  boolean autoCommitSet;
  String protocol;
  int streamChunkSize = 16384;

  public int protocolId = -3;
  OracleTimeout timeout;
  boolean spawnNewThreadToCancel = false;
  DBConversion conversion;
  boolean xaWantsError;
  boolean usingXA;
  int txnMode = 0;
  byte[] fdo;
  Boolean bigEndian;
  OracleStatement statements;
  int lifecycle;
  static final int OPEN = 1;
  static final int CLOSING = 2;
  static final int CLOSED = 4;
  static final int ABORTED = 8;
  boolean clientIdSet = false;
  String clientId = null;
  int defaultBatch;
  int defaultRowPrefetch;
  boolean reportRemarks;
  boolean includeSynonyms = false;

  boolean restrictGetTables = false;

  boolean accumulateBatchResult = true;

  boolean j2ee13Compliant = false;
  int txnLevel;
  Map map;
  Map javaObjectMap;
  Hashtable descriptorCache;
  OracleStatement statementHoldingLine;
  oracle.jdbc.OracleDatabaseMetaData databaseMetaData = null;
  LogicalConnection logicalConnectionAttached;
  boolean isProxy = false;

  boolean useFetchSizeWithLongColumn = false;

  OracleSql sqlObj = null;

  SQLWarning sqlWarning = null;

  boolean readOnly = false;

  LRUStatementCache statementCache = null;

  boolean clearStatementMetaData = false;

  boolean processEscapes = true;

  boolean defaultAutoRefetch = true;

  OracleCloseCallback closeCallback = null;
  Object privateData = null;

  boolean defaultFixedString = false;

  boolean defaultNChar = false;

  Statement savepointStatement = null;

  static final int[] endToEndMaxLength = new int[4];

  boolean endToEndAnyChanged = false;
  final boolean[] endToEndHasChanged = new boolean[4];
  short endToEndECIDSequenceNumber = -32768;

  String[] endToEndValues = null;

  final boolean useDMSForEndToEnd = this.endToEndValues != null;

  oracle.jdbc.OracleConnection wrapper = null;

  Properties connectionProperties = null;

  boolean wellBehavedStatementReuse = false;
  int minVcsBindSize;
  int maxRawBytesSql;
  int maxRawBytesPlsql;
  int maxVcsCharsSql;
  int maxVcsBytesPlsql;
  OracleDriverExtension driverExtension;
  static final String uninitializedMarker = "";
  String databaseProductVersion = "";
  short versionNumber = -1;
  boolean v8Compatible;
  boolean looseTimestampDateCheck = false;
  boolean isMemoryFreedOnEnteringCache = false;
  String ressourceManagerId = "0000";
  int namedTypeAccessorByteLen;
  int refTypeAccessorByteLen;
  boolean disableDefineColumnType = false;
  boolean convertNcharLiterals = true;
  CharacterSet setCHARCharSetObj;
  CharacterSet setCHARNCharSetObj;
  boolean plsqlCompilerWarnings = false;
  static final String DATABASE_NAME = "DATABASE_NAME";
  static final String SERVER_HOST = "SERVER_HOST";
  static final String INSTANCE_NAME = "INSTANCE_NAME";
  static final String SERVICE_NAME = "SERVICE_NAME";
  Hashtable clientData;
  String sessionTimeZone = null;
  Calendar dbTzCalendar = null;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  PhysicalConnection()
  {
  }

  PhysicalConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties, OracleDriverExtension paramOracleDriverExtension)
    throws SQLException
  {
    this.driverExtension = paramOracleDriverExtension;

    String str1 = null;
    String str3;
    if (paramProperties != null)
    {
      str1 = (String)paramProperties.get("protocol");

      String str2 = paramProperties.getProperty("processEscapes");

      if ((str2 != null) && (str2.equalsIgnoreCase("false")))
      {
        this.processEscapes = false;
      }

      str3 = (String)paramProperties.get("RessourceManagerId");

      if (str3 != null) {
        this.ressourceManagerId = str3;
      }
      this.connectionProperties = ((Properties)paramProperties.clone());
    }

    initialize(paramString1, paramString2, str1, null, null, null, paramString4);
    initializePassword(paramString3);

    this.logicalConnectionAttached = null;
    try
    {
      needLine();

      logon();

      boolean bool = true;

      if (paramProperties != null)
      {
        str3 = paramProperties.getProperty("autoCommit");

        if ((str3 != null) && (str3.equalsIgnoreCase("false")))
        {
          bool = false;
        }

        str3 = this.connectionProperties.getProperty("wellBehavedStatementReuse");

        if ((str3 != null) && (str3.equalsIgnoreCase("true"))) {
          this.wellBehavedStatementReuse = true;
        }
      }
      setAutoCommit(bool);

      if (getVersionNumber() >= 10000)
      {
        this.minVcsBindSize = 4001;
        this.maxRawBytesSql = 2000;
        this.maxRawBytesPlsql = 32512;
        this.maxVcsCharsSql = 32766;
        this.maxVcsBytesPlsql = 32512;
      }
      else if (getVersionNumber() >= 9200)
      {
        this.minVcsBindSize = 4001;
        this.maxRawBytesSql = 2000;
        this.maxRawBytesPlsql = 32512;
        this.maxVcsCharsSql = 32766;
        this.maxVcsBytesPlsql = 32512;
      }
      else
      {
        this.minVcsBindSize = 4001;
        this.maxRawBytesSql = 2000;
        this.maxRawBytesPlsql = 2000;
        this.maxVcsCharsSql = 4000;
        this.maxVcsBytesPlsql = 4000;
      }

      str3 = paramProperties != null ? paramProperties.getProperty("oracle.jdbc.V8Compatible") : null;

      if (str3 == null) {
        str3 = OracleDriver.getSystemPropertyV8Compatible();
      }

      if (str3 != null) {
        this.v8Compatible = str3.equalsIgnoreCase("true");
      }
      String str4 = paramProperties != null ? paramProperties.getProperty("oracle.jdbc.StreamChunkSize") : null;

      if (str4 != null) {
        this.streamChunkSize = Math.max(4096, Integer.parseInt(str4));
      }

      str4 = paramProperties != null ? paramProperties.getProperty("oracle.jdbc.internal.permitBindDateDefineTimestampMismatch") : null;

      if ((str4 != null) && (str4.equalsIgnoreCase("true")))
      {
        this.looseTimestampDateCheck = true;
      }

      str4 = paramProperties != null ? paramProperties.getProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache") : null;
      this.isMemoryFreedOnEnteringCache = ((str4 != null) && (str4.equalsIgnoreCase("true")));

      initializeSetCHARCharSetObjs();

      this.spawnNewThreadToCancel = "true".equalsIgnoreCase(paramProperties.getProperty("oracle.jdbc.spawnNewThreadToCancel"));
    }
    catch (SQLException localSQLException1)
    {
      try
      {
        logoff();
      }
      catch (SQLException localSQLException2) {
      }
      throw localSQLException1;
    }

    this.txnMode = 0;
  }

  abstract void initializePassword(String paramString)
    throws SQLException;

  public Properties getProperties()
  {
    return OracleDataSource.filterConnectionProperties(this.connectionProperties);
  }

  /** @deprecated */
  public synchronized Connection _getPC()
  {
    return null;
  }

  public synchronized oracle.jdbc.internal.OracleConnection getPhysicalConnection()
  {
    return this;
  }

  public synchronized boolean isLogicalConnection()
  {
    return false;
  }

  void initialize(String paramString1, String paramString2, String paramString3, Hashtable paramHashtable, Map paramMap1, Map paramMap2, String paramString4)
    throws SQLException
  {
    this.clearStatementMetaData = false;
    this.database = paramString4;

    this.url = paramString1;

    if ((paramString2 == null) || (paramString2.startsWith("\"")))
    {
      this.user = paramString2;
    }
    else this.user = paramString2.toUpperCase();

    this.protocol = paramString3;

    this.defaultRowPrefetch = DEFAULT_ROW_PREFETCH;
    this.defaultBatch = 1;

    if (paramHashtable != null)
      this.descriptorCache = paramHashtable;
    else {
      this.descriptorCache = new Hashtable(10);
    }
    this.map = paramMap1;

    if (paramMap2 != null)
      this.javaObjectMap = paramMap2;
    else {
      this.javaObjectMap = new Hashtable(10);
    }
    this.lifecycle = 1;
    this.txnLevel = 2;

    this.xaWantsError = false;
    this.usingXA = false;

    this.clientIdSet = false;
  }

  void initializeSetCHARCharSetObjs()
  {
    this.setCHARNCharSetObj = this.conversion.getDriverNCharSetObj();
    this.setCHARCharSetObj = this.conversion.getDriverCharSetObj();
  }

  OracleTimeout getTimeout()
    throws SQLException
  {
    if (this.timeout == null)
    {
      this.timeout = OracleTimeout.newTimeout(this.url);
    }

    return this.timeout;
  }

  public synchronized Statement createStatement()
    throws SQLException
  {
    return createStatement(-1, -1);
  }

  public synchronized Statement createStatement(int paramInt1, int paramInt2)
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    OracleStatement localOracleStatement = null;

    localOracleStatement = this.driverExtension.allocateStatement(this, paramInt1, paramInt2);

    return localOracleStatement;
  }

  public synchronized PreparedStatement prepareStatement(String paramString)
    throws SQLException
  {
    return prepareStatement(paramString, -1, -1);
  }

  /** @deprecated */
  public synchronized PreparedStatement prepareStatementWithKey(String paramString)
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    if (paramString == null) {
      return null;
    }
    if (!isStatementCacheInitialized()) {
      DatabaseError.throwSqlException(95);
    }

    OraclePreparedStatement localOraclePreparedStatement = null;

    localOraclePreparedStatement = (OraclePreparedStatement)this.statementCache.searchExplicitCache(paramString);

    return localOraclePreparedStatement;
  }

  public synchronized PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    if ((paramString == null) || (paramString == "")) {
      DatabaseError.throwSqlException(104);
    }
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    OraclePreparedStatement localOraclePreparedStatement = null;

    if (this.statementCache != null) {
      localOraclePreparedStatement = (OraclePreparedStatement)this.statementCache.searchImplicitCache(paramString, 1, (paramInt1 != -1) || (paramInt2 != -1) ? ResultSetUtil.getRsetTypeCode(paramInt1, paramInt2) : 1);
    }

    if (localOraclePreparedStatement == null) {
      localOraclePreparedStatement = this.driverExtension.allocatePreparedStatement(this, paramString, paramInt1, paramInt2);
    }

    return localOraclePreparedStatement;
  }

  public synchronized CallableStatement prepareCall(String paramString)
    throws SQLException
  {
    return prepareCall(paramString, -1, -1);
  }

  public synchronized CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    if ((paramString == null) || (paramString == "")) {
      DatabaseError.throwSqlException(104);
    }
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    OracleCallableStatement localOracleCallableStatement = null;

    if (this.statementCache != null) {
      localOracleCallableStatement = (OracleCallableStatement)this.statementCache.searchImplicitCache(paramString, 2, (paramInt1 != -1) || (paramInt2 != -1) ? ResultSetUtil.getRsetTypeCode(paramInt1, paramInt2) : 1);
    }

    if (localOracleCallableStatement == null) {
      localOracleCallableStatement = this.driverExtension.allocateCallableStatement(this, paramString, paramInt1, paramInt2);
    }

    return localOracleCallableStatement;
  }

  public synchronized CallableStatement prepareCallWithKey(String paramString)
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    if (paramString == null) {
      return null;
    }
    if (!isStatementCacheInitialized()) {
      DatabaseError.throwSqlException(95);
    }

    OracleCallableStatement localOracleCallableStatement = null;

    localOracleCallableStatement = (OracleCallableStatement)this.statementCache.searchExplicitCache(paramString);

    return localOracleCallableStatement;
  }

  public String nativeSQL(String paramString)
    throws SQLException
  {
    if (this.sqlObj == null)
    {
      this.sqlObj = new OracleSql(this.conversion);
      this.sqlObj.isV8Compatible = this.v8Compatible;
    }

    this.sqlObj.initialize(paramString);

    String str = this.sqlObj.getSql(this.processEscapes, this.convertNcharLiterals);

    return str;
  }

  public synchronized void setAutoCommit(boolean paramBoolean)
    throws SQLException
  {
    if (paramBoolean) {
      disallowGlobalTxnMode(116);
    }
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    needLine();
    doSetAutoCommit(paramBoolean);

    this.autoCommitSet = paramBoolean;
  }

  public boolean getAutoCommit()
    throws SQLException
  {
    return this.autoCommitSet;
  }

  public void cancel()
    throws SQLException
  {
    OracleStatement localOracleStatement = this.statements;

    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    while (localOracleStatement != null)
    {
      try
      {
        localOracleStatement.cancel();
      }
      catch (SQLException localSQLException)
      {
      }

      localOracleStatement = localOracleStatement.next;
    }
  }

  public synchronized void commit()
    throws SQLException
  {
    disallowGlobalTxnMode(114);

    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    OracleStatement localOracleStatement = this.statements;

    while (localOracleStatement != null)
    {
      if (!localOracleStatement.closed) {
        localOracleStatement.sendBatch();
      }
      localOracleStatement = localOracleStatement.next;
    }

    needLine();
    doCommit();
  }

  public synchronized void rollback()
    throws SQLException
  {
    disallowGlobalTxnMode(115);

    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    needLine();
    doRollback();
  }

  public synchronized void close()
    throws SQLException
  {
    if ((this.lifecycle == 2) || (this.lifecycle == 4)) {
      return;
    }
    if (this.lifecycle == 1) this.lifecycle = 2;

    try
    {
      if (this.closeCallback != null) {
        this.closeCallback.beforeClose(this, this.privateData);
      }
      closeStatements(true);

      needLine();

      if (this.isProxy)
      {
        close(1);
      }

      logoff();
      cleanup();

      if (this.timeout != null) {
        this.timeout.close();
      }
      if (this.closeCallback != null)
        this.closeCallback.afterClose(this.privateData);
    }
    finally
    {
      this.lifecycle = 4;
    }
  }

  public void closeInternal(boolean paramBoolean)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  synchronized void closeLogicalConnection() throws SQLException
  {
    closeStatements(false);

    if (this.clientIdSet) {
      clearClientIdentifier(this.clientId);
    }
    this.logicalConnectionAttached = null;
  }

  public synchronized void close(Properties paramProperties)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  public synchronized void close(int paramInt)
    throws SQLException
  {
    if ((paramInt & 0x1000) != 0)
    {
      close();

      return;
    }

    if (((paramInt & 0x1) != 0) && (this.isProxy))
    {
      closeProxySession();

      this.isProxy = false;
    }
  }

  public void abort()
    throws SQLException
  {
    if ((this.lifecycle == 4) || (this.lifecycle == 8)) {
      return;
    }

    this.lifecycle = 8;

    doAbort();
  }

  abstract void doAbort()
    throws SQLException;

  void closeProxySession()
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public Properties getServerSessionInfo()
    throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public synchronized void applyConnectionAttributes(Properties paramProperties)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  public synchronized Properties getConnectionAttributes()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return null;
  }

  public synchronized Properties getUnMatchedConnectionAttributes()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return null;
  }

  public synchronized void setAbandonedTimeoutEnabled(boolean paramBoolean)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  public synchronized void registerConnectionCacheCallback(OracleConnectionCacheCallback paramOracleConnectionCacheCallback, Object paramObject, int paramInt)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  public OracleConnectionCacheCallback getConnectionCacheCallbackObj()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return null;
  }

  public Object getConnectionCacheCallbackPrivObj()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return null;
  }

  public int getConnectionCacheCallbackFlag()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return 0;
  }

  public synchronized void setConnectionReleasePriority(int paramInt)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  public int getConnectionReleasePriority()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return 0;
  }

  public synchronized boolean isClosed()
    throws SQLException
  {
    return this.lifecycle != 1;
  }

  public synchronized boolean isProxySession()
  {
    return this.isProxy;
  }

  public synchronized void openProxySession(int paramInt, Properties paramProperties)
    throws SQLException
  {
    if (this.isProxy) {
      DatabaseError.throwSqlException(149);
    }
    String str1 = paramProperties.getProperty("PROXY_USER_NAME");
    String str2 = paramProperties.getProperty("PROXY_USER_PASSWORD");
    String str3 = paramProperties.getProperty("PROXY_DISTINGUISHED_NAME");

    Object localObject = paramProperties.get("PROXY_CERTIFICATE");

    if (paramInt == 1)
    {
      if ((str1 == null) && (str2 == null))
        DatabaseError.throwSqlException(150);
    }
    else if (paramInt == 2)
    {
      if (str3 == null)
        DatabaseError.throwSqlException(150);
    }
    else if (paramInt == 3)
    {
      if (localObject == null) {
        DatabaseError.throwSqlException(150);
      }
      try
      {
        byte[] arrayOfByte = (byte[])localObject;
      }
      catch (ClassCastException localClassCastException)
      {
        DatabaseError.throwSqlException(150);
      }
    }
    else {
      DatabaseError.throwSqlException(150);
    }

    doProxySession(paramInt, paramProperties);
  }

  void doProxySession(int paramInt, Properties paramProperties)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  void cleanup()
  {
    this.fdo = null;
    this.conversion = null;
    this.statements = null;
    this.descriptorCache = null;
    this.map = null;
    this.javaObjectMap = null;
    this.statementHoldingLine = null;
    this.sqlObj = null;
    this.isProxy = false;
  }

  public synchronized DatabaseMetaData getMetaData()
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    if (this.databaseMetaData == null) {
      this.databaseMetaData = new OracleDatabaseMetaData(this);
    }
    return this.databaseMetaData;
  }

  public void setReadOnly(boolean paramBoolean)
    throws SQLException
  {
    this.readOnly = paramBoolean;
  }

  public boolean isReadOnly()
    throws SQLException
  {
    return this.readOnly;
  }

  public void setCatalog(String paramString)
    throws SQLException
  {
  }

  public String getCatalog()
    throws SQLException
  {
    return null;
  }

  public synchronized void setTransactionIsolation(int paramInt)
    throws SQLException
  {
    OracleStatement localOracleStatement = (OracleStatement)createStatement();
    try
    {
      switch (paramInt)
      {
      case 2:
        localOracleStatement.execute("ALTER SESSION SET ISOLATION_LEVEL = READ COMMITTED");

        this.txnLevel = 2;

        break;
      case 8:
        localOracleStatement.execute("ALTER SESSION SET ISOLATION_LEVEL = SERIALIZABLE");

        this.txnLevel = 8;

        break;
      default:
        DatabaseError.throwSqlException(30);
      }

    }
    finally
    {
      localOracleStatement.close();
    }
  }

  public int getTransactionIsolation()
    throws SQLException
  {
    return this.txnLevel;
  }

  public synchronized void setAutoClose(boolean paramBoolean)
    throws SQLException
  {
    if (!paramBoolean)
      DatabaseError.throwSqlException(31);
  }

  public boolean getAutoClose()
    throws SQLException
  {
    return true;
  }

  public SQLWarning getWarnings()
    throws SQLException
  {
    return this.sqlWarning;
  }

  public void clearWarnings()
    throws SQLException
  {
    this.sqlWarning = null;
  }

  public void setWarnings(SQLWarning paramSQLWarning)
  {
    this.sqlWarning = paramSQLWarning;
  }

  public void setDefaultRowPrefetch(int paramInt)
    throws SQLException
  {
    if (paramInt <= 0) {
      DatabaseError.throwSqlException(20);
    }
    this.defaultRowPrefetch = paramInt;
  }

  public int getDefaultRowPrefetch()
  {
    return this.defaultRowPrefetch;
  }

  public synchronized void setDefaultExecuteBatch(int paramInt)
    throws SQLException
  {
    if (paramInt <= 0) {
      DatabaseError.throwSqlException(42);
    }
    this.defaultBatch = paramInt;
  }

  public synchronized int getDefaultExecuteBatch()
  {
    return this.defaultBatch;
  }

  public synchronized void setRemarksReporting(boolean paramBoolean)
  {
    this.reportRemarks = paramBoolean;
  }

  public synchronized boolean getRemarksReporting()
  {
    return this.reportRemarks;
  }

  public void setIncludeSynonyms(boolean paramBoolean)
  {
    this.includeSynonyms = paramBoolean;
  }

  public synchronized String[] getEndToEndMetrics()
    throws SQLException
  {
    String[] arrayOfString;
    if (this.endToEndValues == null)
    {
      arrayOfString = null;
    }
    else
    {
      arrayOfString = new String[this.endToEndValues.length];

      System.arraycopy(this.endToEndValues, 0, arrayOfString, 0, this.endToEndValues.length);
    }

    return arrayOfString;
  }

  public short getEndToEndECIDSequenceNumber()
    throws SQLException
  {
    return this.endToEndECIDSequenceNumber;
  }

  public synchronized void setEndToEndMetrics(String[] paramArrayOfString, short paramShort)
    throws SQLException
  {
    if (!this.useDMSForEndToEnd)
    {
      String[] arrayOfString = new String[paramArrayOfString.length];

      System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, paramArrayOfString.length);
      setEndToEndMetricsInternal(arrayOfString, paramShort);
    }
  }

  void setEndToEndMetricsInternal(String[] paramArrayOfString, short paramShort)
    throws SQLException
  {
    if (paramArrayOfString != this.endToEndValues)
    {
      if (paramArrayOfString.length != 4)
      {
        DatabaseError.throwSqlException(156);
      }
      String str;
      for (int i = 0; i < 4; i++)
      {
        str = paramArrayOfString[i];

        if ((str == null) || (str.length() <= endToEndMaxLength[i]))
          continue;
        DatabaseError.throwSqlException(159, str);
      }

      if (this.endToEndValues != null)
      {
        for (i = 0; i < 4; i++)
        {
          str = paramArrayOfString[i];

          if (((str != null) || (this.endToEndValues[i] == null)) && ((str == null) || (str.equals(this.endToEndValues[i])))) {
            continue;
          }
          this.endToEndHasChanged[i] = true;
          this.endToEndAnyChanged = true;
        }

        this.endToEndHasChanged[0] |= this.endToEndHasChanged[3];
      }
      else
      {
        for (i = 0; i < 4; i++)
        {
          this.endToEndHasChanged[i] = true;
        }

        this.endToEndAnyChanged = true;
      }

      this.endToEndValues = paramArrayOfString;
    }

    this.endToEndECIDSequenceNumber = paramShort;
  }

  void updateEndToEndMetrics()
    throws SQLException
  {
  }

  public boolean getIncludeSynonyms()
  {
    return this.includeSynonyms;
  }

  public void setRestrictGetTables(boolean paramBoolean)
  {
    this.restrictGetTables = paramBoolean;
  }

  public boolean getRestrictGetTables()
  {
    return this.restrictGetTables;
  }

  public void setDefaultFixedString(boolean paramBoolean)
  {
    this.defaultFixedString = paramBoolean;
  }

  public void setDefaultNChar(boolean paramBoolean)
  {
    this.defaultNChar = paramBoolean;
  }

  public boolean getDefaultFixedString()
  {
    return this.defaultFixedString;
  }

  public int getNlsRatio()
  {
    return 1;
  }

  public int getC2SNlsRatio()
  {
    return 1;
  }

  synchronized void addStatement(OracleStatement paramOracleStatement)
  {
    if (paramOracleStatement.next != null) {
      throw new Error("add_statement called twice on " + paramOracleStatement);
    }
    paramOracleStatement.next = this.statements;

    if (this.statements != null) {
      this.statements.prev = paramOracleStatement;
    }
    this.statements = paramOracleStatement;
  }

  synchronized void removeStatement(OracleStatement paramOracleStatement)
  {
    OracleStatement localOracleStatement1 = paramOracleStatement.prev;
    OracleStatement localOracleStatement2 = paramOracleStatement.next;

    if (localOracleStatement1 == null)
    {
      if (this.statements != paramOracleStatement) {
        return;
      }
      this.statements = localOracleStatement2;
    }
    else {
      localOracleStatement1.next = localOracleStatement2;
    }
    if (localOracleStatement2 != null) {
      localOracleStatement2.prev = localOracleStatement1;
    }
    paramOracleStatement.next = null;
    paramOracleStatement.prev = null;
  }

  synchronized void closeStatements(boolean paramBoolean)
    throws SQLException
  {
    if ((paramBoolean) && (isStatementCacheInitialized()))
    {
      this.statementCache.close();

      this.statementCache = null;
      this.clearStatementMetaData = true;
    }

    Object localObject = this.statements;
    OracleStatement localOracleStatement;
    while (localObject != null)
    {
      localOracleStatement = ((OracleStatement)localObject).nextChild;

      if (((OracleStatement)localObject).serverCursor)
      {
        ((OracleStatement)localObject).close();
        removeStatement((OracleStatement)localObject);
      }

      localObject = localOracleStatement;
    }

    localObject = this.statements;

    while (localObject != null)
    {
      localOracleStatement = ((OracleStatement)localObject).next;

      ((OracleStatement)localObject).close();
      removeStatement((OracleStatement)localObject);

      localObject = localOracleStatement;
    }
  }

  synchronized void needLine()
    throws SQLException
  {
    if (this.statementHoldingLine != null)
    {
      this.statementHoldingLine.freeLine();
    }
  }

  synchronized void holdLine(oracle.jdbc.internal.OracleStatement paramOracleStatement)
  {
    holdLine((OracleStatement)paramOracleStatement);
  }

  synchronized void holdLine(OracleStatement paramOracleStatement)
  {
    this.statementHoldingLine = paramOracleStatement;
  }

  synchronized void releaseLine()
  {
    releaseLineForCancel();
  }

  void releaseLineForCancel()
  {
    this.statementHoldingLine = null;
  }

  public synchronized void startup(String paramString, int paramInt)
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    DatabaseError.throwSqlException(23);
  }

  public synchronized void shutdown(int paramInt) throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    DatabaseError.throwSqlException(23);
  }

  public synchronized void archive(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    DatabaseError.throwSqlException(23);
  }

  public synchronized void registerSQLType(String paramString1, String paramString2)
    throws SQLException
  {
    if ((paramString1 == null) || (paramString2 == null)) {
      DatabaseError.throwSqlException(68);
    }
    try
    {
      registerSQLType(paramString1, Class.forName(paramString2));
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
      DatabaseError.throwSqlException(1, "Class not found: " + paramString2);
    }
  }

  public synchronized void registerSQLType(String paramString, Class paramClass)
    throws SQLException
  {
    if ((paramString == null) || (paramClass == null)) {
      DatabaseError.throwSqlException(68);
    }
    ensureClassMapExists();

    this.map.put(paramString, paramClass);
    this.map.put(paramClass.getName(), paramString);
  }

  void ensureClassMapExists()
  {
    if (this.map == null)
      initializeClassMap();
  }

  void initializeClassMap()
  {
    Hashtable localHashtable = new Hashtable(10);

    addDefaultClassMapEntriesTo(localHashtable);

    this.map = localHashtable;
  }

  public synchronized String getSQLType(Object paramObject)
    throws SQLException
  {
    if ((paramObject != null) && (this.map != null))
    {
      String str = paramObject.getClass().getName();

      return (String)this.map.get(str);
    }

    return null;
  }

  public synchronized Object getJavaObject(String paramString)
    throws SQLException
  {
    Object localObject = null;
    try
    {
      if ((paramString != null) && (this.map != null))
      {
        Class localClass = (Class)this.map.get(paramString);

        localObject = localClass.newInstance();
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localIllegalAccessException.printStackTrace();
    }
    catch (InstantiationException localInstantiationException)
    {
      localInstantiationException.printStackTrace();
    }

    return localObject;
  }

  public synchronized void putDescriptor(String paramString, Object paramObject)
    throws SQLException
  {
    if ((paramString != null) && (paramObject != null))
    {
      if (this.descriptorCache == null) {
        this.descriptorCache = new Hashtable(10);
      }
      ((TypeDescriptor)paramObject).fixupConnection(this);
      this.descriptorCache.put(paramString, paramObject);
    }
    else {
      DatabaseError.throwSqlException(68);
    }
  }

  public synchronized Object getDescriptor(String paramString)
  {
    Object localObject = null;

    if ((paramString != null) && (this.descriptorCache != null)) {
      localObject = this.descriptorCache.get(paramString);
    }

    return localObject;
  }

  /** @deprecated */
  public synchronized void removeDecriptor(String paramString)
  {
    removeDescriptor(paramString);
  }

  public synchronized void removeDescriptor(String paramString)
  {
    if ((paramString != null) && (this.descriptorCache != null))
      this.descriptorCache.remove(paramString);
  }

  public synchronized void removeAllDescriptor()
  {
    if (this.descriptorCache != null)
      this.descriptorCache.clear();
  }

  public int numberOfDescriptorCacheEntries()
  {
    if (this.descriptorCache != null) {
      return this.descriptorCache.size();
    }
    return 0;
  }

  public Enumeration descriptorCacheKeys()
  {
    if (this.descriptorCache != null) {
      return this.descriptorCache.keys();
    }
    return null;
  }

  public synchronized void putDescriptor(byte[] paramArrayOfByte, Object paramObject)
    throws SQLException
  {
    if ((paramArrayOfByte != null) && (paramObject != null))
    {
      if (this.descriptorCache == null) {
        this.descriptorCache = new Hashtable(10);
      }
      this.descriptorCache.put(new ByteArrayKey(paramArrayOfByte), paramObject);
    }
    else {
      DatabaseError.throwSqlException(68);
    }
  }

  public synchronized Object getDescriptor(byte[] paramArrayOfByte)
  {
    Object localObject = null;

    if ((paramArrayOfByte != null) && (this.descriptorCache != null)) {
      localObject = this.descriptorCache.get(new ByteArrayKey(paramArrayOfByte));
    }

    return localObject;
  }

  public synchronized void removeDecriptor(byte[] paramArrayOfByte)
  {
    if ((paramArrayOfByte != null) && (this.descriptorCache != null))
      this.descriptorCache.remove(new ByteArrayKey(paramArrayOfByte));
  }

  public short getJdbcCsId()
    throws SQLException
  {
    if (this.conversion == null)
    {
      DatabaseError.throwSqlException(65);
    }

    return this.conversion.getClientCharSet();
  }

  public short getDbCsId()
    throws SQLException
  {
    if (this.conversion == null)
    {
      DatabaseError.throwSqlException(65);
    }

    return this.conversion.getServerCharSetId();
  }

  public short getNCsId() throws SQLException
  {
    if (this.conversion == null)
    {
      DatabaseError.throwSqlException(65);
    }

    return this.conversion.getNCharSetId();
  }

  public short getStructAttrCsId()
    throws SQLException
  {
    return getDbCsId();
  }

  public short getStructAttrNCsId() throws SQLException
  {
    return getNCsId();
  }

  public synchronized Map getTypeMap()
  {
    ensureClassMapExists();

    return this.map;
  }

  public synchronized void setTypeMap(Map paramMap)
  {
    addDefaultClassMapEntriesTo(paramMap);

    this.map = paramMap;
  }

  void addDefaultClassMapEntriesTo(Map paramMap)
  {
    if (paramMap != null)
    {
      addClassMapEntry("SYS.XMLTYPE", "oracle.xdb.XMLTypeFactory", paramMap);
    }
  }

  void addClassMapEntry(String paramString1, String paramString2, Map paramMap)
  {
    if (containsKey(paramMap, paramString1)) {
      return;
    }
    try
    {
      Class localClass = safelyGetClassForName(paramString2);

      paramMap.put(paramString1, localClass);
    }
    catch (ClassNotFoundException localClassNotFoundException)
    {
    }
  }

  public synchronized void setUsingXAFlag(boolean paramBoolean)
  {
    this.usingXA = paramBoolean;
  }

  public synchronized boolean getUsingXAFlag()
  {
    return this.usingXA;
  }

  public synchronized void setXAErrorFlag(boolean paramBoolean)
  {
    this.xaWantsError = paramBoolean;
  }

  public synchronized boolean getXAErrorFlag()
  {
    return this.xaWantsError;
  }

  void initUserName()
    throws SQLException
  {
    if (this.user != null) {
      return;
    }
    Statement localStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localStatement = createStatement();
      ((OracleStatement)localStatement).setRowPrefetch(1);
      localResultSet = localStatement.executeQuery("SELECT USER FROM DUAL");
      if (localResultSet.next())
        this.user = localResultSet.getString(1);
    }
    finally
    {
      if (localResultSet != null)
        localResultSet.close();
      if (localStatement != null)
        localStatement.close();
      localResultSet = null;
      localStatement = null;
    }
  }

  public synchronized String getUserName() throws SQLException
  {
    if (this.user == null) {
      initUserName();
    }
    return this.user;
  }

  public synchronized void setStartTime(long paramLong)
    throws SQLException
  {
    DatabaseError.throwSqlException(152);
  }

  public synchronized long getStartTime()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return -1L;
  }

  void registerHeartbeat()
    throws SQLException
  {
    if (this.logicalConnectionAttached != null)
      this.logicalConnectionAttached.registerHeartbeat();
  }

  public int getHeartbeatNoChangeCount()
    throws SQLException
  {
    DatabaseError.throwSqlException(152);

    return -1;
  }

  public synchronized byte[] getFDO(boolean paramBoolean)
    throws SQLException
  {
    if ((this.fdo == null) && (paramBoolean))
    {
      CallableStatement localCallableStatement = null;
      try
      {
        localCallableStatement = prepareCall("begin ? := dbms_pickler.get_format (?); end;");

        localCallableStatement.registerOutParameter(1, 2);
        localCallableStatement.registerOutParameter(2, -4);
        localCallableStatement.execute();

        this.fdo = localCallableStatement.getBytes(2);
      }
      finally
      {
        if (localCallableStatement != null) {
          localCallableStatement.close();
        }
        localCallableStatement = null;
      }
    }

    return this.fdo;
  }

  public synchronized void setFDO(byte[] paramArrayOfByte)
    throws SQLException
  {
    this.fdo = paramArrayOfByte;
  }

  public synchronized boolean getBigEndian()
    throws SQLException
  {
    if (this.bigEndian == null)
    {
      int[] arrayOfInt = Util.toJavaUnsignedBytes(getFDO(true));

      int i = arrayOfInt[(6 + arrayOfInt[5] + arrayOfInt[6] + 5)];

      int j = (byte)(i & 0x10);

      if (j < 0) {
        j += 256;
      }
      if (j > 0)
        this.bigEndian = new Boolean(true);
      else {
        this.bigEndian = new Boolean(false);
      }
    }
    return this.bigEndian.booleanValue();
  }

  public void setHoldability(int paramInt)
    throws SQLException
  {
  }

  public int getHoldability()
    throws SQLException
  {
    return 1;
  }

  public synchronized Savepoint setSavepoint()
    throws SQLException
  {
    return oracleSetSavepoint();
  }

  public synchronized Savepoint setSavepoint(String paramString)
    throws SQLException
  {
    return oracleSetSavepoint(paramString);
  }

  public synchronized void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    disallowGlobalTxnMode(122);

    if (this.autoCommitSet) {
      DatabaseError.throwSqlException(121);
    }

    if ((this.savepointStatement == null) || (((OracleStatement)this.savepointStatement).closed))
    {
      this.savepointStatement = createStatement();
    }

    String str = null;
    try
    {
      str = paramSavepoint.getSavepointName();
    }
    catch (SQLException localSQLException)
    {
      str = "ORACLE_SVPT_" + paramSavepoint.getSavepointId();
    }

    this.savepointStatement.executeUpdate("ROLLBACK TO " + str);
  }

  public synchronized void releaseSavepoint(Savepoint paramSavepoint)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
  }

  public Statement createStatement(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    return createStatement(paramInt1, paramInt2);
  }

  public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    return prepareStatement(paramString, paramInt1, paramInt2);
  }

  public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    return prepareCall(paramString, paramInt1, paramInt2);
  }

  public PreparedStatement prepareStatement(String paramString, int paramInt)
    throws SQLException
  {
    if ((paramInt == 2) || (!AutoKeyInfo.isInsertSqlStmt(paramString)))
    {
      return prepareStatement(paramString);
    }
    if (paramInt != 1) {
      DatabaseError.throwSqlException(68);
    }
    AutoKeyInfo localAutoKeyInfo = new AutoKeyInfo(paramString);

    String str = localAutoKeyInfo.getNewSql();
    OraclePreparedStatement localOraclePreparedStatement = (OraclePreparedStatement)prepareStatement(str);

    localOraclePreparedStatement.isAutoGeneratedKey = true;
    localOraclePreparedStatement.autoKeyInfo = localAutoKeyInfo;
    localOraclePreparedStatement.registerReturnParamsForAutoKey();
    return localOraclePreparedStatement;
  }

  public PreparedStatement prepareStatement(String paramString, int[] paramArrayOfInt)
    throws SQLException
  {
    if (!AutoKeyInfo.isInsertSqlStmt(paramString)) return prepareStatement(paramString);

    if ((paramArrayOfInt == null) || (paramArrayOfInt.length == 0)) {
      DatabaseError.throwSqlException(68);
    }
    AutoKeyInfo localAutoKeyInfo = new AutoKeyInfo(paramString, paramArrayOfInt);

    doDescribeTable(localAutoKeyInfo);

    String str = localAutoKeyInfo.getNewSql();
    OraclePreparedStatement localOraclePreparedStatement = (OraclePreparedStatement)prepareStatement(str);

    localOraclePreparedStatement.isAutoGeneratedKey = true;
    localOraclePreparedStatement.autoKeyInfo = localAutoKeyInfo;
    localOraclePreparedStatement.registerReturnParamsForAutoKey();
    return localOraclePreparedStatement;
  }

  public PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString)
    throws SQLException
  {
    if (!AutoKeyInfo.isInsertSqlStmt(paramString)) return prepareStatement(paramString);

    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {
      DatabaseError.throwSqlException(68);
    }
    AutoKeyInfo localAutoKeyInfo = new AutoKeyInfo(paramString, paramArrayOfString);

    doDescribeTable(localAutoKeyInfo);

    String str = localAutoKeyInfo.getNewSql();
    OraclePreparedStatement localOraclePreparedStatement = (OraclePreparedStatement)prepareStatement(str);

    localOraclePreparedStatement.isAutoGeneratedKey = true;
    localOraclePreparedStatement.autoKeyInfo = localAutoKeyInfo;
    localOraclePreparedStatement.registerReturnParamsForAutoKey();
    return localOraclePreparedStatement;
  }

  public synchronized oracle.jdbc.OracleSavepoint oracleSetSavepoint()
    throws SQLException
  {
    disallowGlobalTxnMode(117);

    if (this.autoCommitSet) {
      DatabaseError.throwSqlException(120);
    }

    if ((this.savepointStatement == null) || (((OracleStatement)this.savepointStatement).closed))
    {
      this.savepointStatement = createStatement();
    }

    OracleSavepoint localOracleSavepoint = new OracleSavepoint();

    String str = "SAVEPOINT ORACLE_SVPT_" + localOracleSavepoint.getSavepointId();

    this.savepointStatement.executeUpdate(str);

    return localOracleSavepoint;
  }

  public synchronized oracle.jdbc.OracleSavepoint oracleSetSavepoint(String paramString)
    throws SQLException
  {
    disallowGlobalTxnMode(117);

    if (this.autoCommitSet) {
      DatabaseError.throwSqlException(120);
    }

    if ((this.savepointStatement == null) || (((OracleStatement)this.savepointStatement).closed))
    {
      this.savepointStatement = createStatement();
    }

    OracleSavepoint localOracleSavepoint = new OracleSavepoint(paramString);

    String str = "SAVEPOINT " + localOracleSavepoint.getSavepointName();

    this.savepointStatement.executeUpdate(str);

    return localOracleSavepoint;
  }

  public synchronized void oracleRollback(oracle.jdbc.OracleSavepoint paramOracleSavepoint)
    throws SQLException
  {
    disallowGlobalTxnMode(115);

    if (this.autoCommitSet) {
      DatabaseError.throwSqlException(121);
    }

    if ((this.savepointStatement == null) || (((OracleStatement)this.savepointStatement).closed))
    {
      this.savepointStatement = createStatement();
    }

    String str = null;
    try
    {
      str = paramOracleSavepoint.getSavepointName();
    }
    catch (SQLException localSQLException)
    {
      str = "ORACLE_SVPT_" + paramOracleSavepoint.getSavepointId();
    }

    this.savepointStatement.executeUpdate("ROLLBACK TO " + str);
  }

  public synchronized void oracleReleaseSavepoint(oracle.jdbc.OracleSavepoint paramOracleSavepoint)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();
  }

  void disallowGlobalTxnMode(int paramInt)
    throws SQLException
  {
    if (this.txnMode == 1)
      DatabaseError.throwSqlException(paramInt);
  }

  public void setTxnMode(int paramInt)
  {
    this.txnMode = paramInt;
  }

  public int getTxnMode()
  {
    return this.txnMode;
  }

  public synchronized Object getClientData(Object paramObject)
  {
    if (this.clientData == null)
    {
      return null;
    }

    return this.clientData.get(paramObject);
  }

  public synchronized Object setClientData(Object paramObject1, Object paramObject2)
  {
    if (this.clientData == null)
    {
      this.clientData = new Hashtable();
    }

    return this.clientData.put(paramObject1, paramObject2);
  }

  public synchronized Object removeClientData(Object paramObject)
  {
    if (this.clientData == null)
    {
      return null;
    }

    return this.clientData.remove(paramObject);
  }

  public BlobDBAccess createBlobDBAccess() throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public ClobDBAccess createClobDBAccess() throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public BfileDBAccess createBfileDBAccess() throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public void printState()
  {
    try
    {
      int i = getJdbcCsId();

      int j = getDbCsId();

      int k = getStructAttrCsId();
    }
    catch (SQLException localSQLException)
    {
      localSQLException.printStackTrace();
    }
  }

  public String getProtocolType()
  {
    return this.protocol;
  }

  public String getURL()
  {
    return this.url;
  }

  /** @deprecated */
  public synchronized void setStmtCacheSize(int paramInt)
    throws SQLException
  {
    setStatementCacheSize(paramInt);
    setImplicitCachingEnabled(true);
    setExplicitCachingEnabled(true);
  }

  /** @deprecated */
  public synchronized void setStmtCacheSize(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    setStatementCacheSize(paramInt);
    setImplicitCachingEnabled(true);

    setExplicitCachingEnabled(true);

    this.clearStatementMetaData = paramBoolean;
  }

  /** @deprecated */
  public synchronized int getStmtCacheSize()
  {
    int i = 0;
    try
    {
      i = getStatementCacheSize();
    }
    catch (SQLException localSQLException)
    {
    }

    if (i == -1)
    {
      i = 0;
    }

    return i;
  }

  public synchronized void setStatementCacheSize(int paramInt)
    throws SQLException
  {
    if (this.statementCache == null)
    {
      this.statementCache = new LRUStatementCache(paramInt);
    }
    else
    {
      this.statementCache.resize(paramInt);
    }
  }

  public synchronized int getStatementCacheSize()
    throws SQLException
  {
    if (this.statementCache == null) {
      return -1;
    }
    return this.statementCache.getCacheSize();
  }

  public synchronized void setImplicitCachingEnabled(boolean paramBoolean)
    throws SQLException
  {
    if (this.statementCache == null)
    {
      this.statementCache = new LRUStatementCache(0);
    }

    this.statementCache.setImplicitCachingEnabled(paramBoolean);
  }

  public synchronized boolean getImplicitCachingEnabled()
    throws SQLException
  {
    if (this.statementCache == null) {
      return false;
    }
    return this.statementCache.getImplicitCachingEnabled();
  }

  public synchronized void setExplicitCachingEnabled(boolean paramBoolean)
    throws SQLException
  {
    if (this.statementCache == null)
    {
      this.statementCache = new LRUStatementCache(0);
    }

    this.statementCache.setExplicitCachingEnabled(paramBoolean);
  }

  public synchronized boolean getExplicitCachingEnabled()
    throws SQLException
  {
    if (this.statementCache == null) {
      return false;
    }
    return this.statementCache.getExplicitCachingEnabled();
  }

  public synchronized void purgeImplicitCache()
    throws SQLException
  {
    if (this.statementCache != null)
      this.statementCache.purgeImplicitCache();
  }

  public synchronized void purgeExplicitCache()
    throws SQLException
  {
    if (this.statementCache != null)
      this.statementCache.purgeExplicitCache();
  }

  public synchronized PreparedStatement getStatementWithKey(String paramString)
    throws SQLException
  {
    if (this.statementCache != null)
    {
      OracleStatement localOracleStatement = this.statementCache.searchExplicitCache(paramString);

      if ((localOracleStatement == null) || (localOracleStatement.statementType == 1)) {
        return (PreparedStatement)localOracleStatement;
      }

      DatabaseError.throwSqlException(125);

      return null;
    }

    return null;
  }

  public synchronized CallableStatement getCallWithKey(String paramString)
    throws SQLException
  {
    if (this.statementCache != null)
    {
      OracleStatement localOracleStatement = this.statementCache.searchExplicitCache(paramString);

      if ((localOracleStatement == null) || (localOracleStatement.statementType == 2)) {
        return (CallableStatement)localOracleStatement;
      }

      DatabaseError.throwSqlException(125);

      return null;
    }

    return null;
  }

  public synchronized void cacheImplicitStatement(OraclePreparedStatement paramOraclePreparedStatement, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (this.statementCache == null)
      DatabaseError.throwSqlException(95);
    else
      this.statementCache.addToImplicitCache(paramOraclePreparedStatement, paramString, paramInt1, paramInt2);
  }

  public synchronized void cacheExplicitStatement(OraclePreparedStatement paramOraclePreparedStatement, String paramString)
    throws SQLException
  {
    if (this.statementCache == null)
      DatabaseError.throwSqlException(95);
    else
      this.statementCache.addToExplicitCache(paramOraclePreparedStatement, paramString);
  }

  public synchronized boolean isStatementCacheInitialized()
  {
    if (this.statementCache == null) {
      return false;
    }
    return this.statementCache.getCacheSize() != 0;
  }

  public void setDefaultAutoRefetch(boolean paramBoolean)
    throws SQLException
  {
    this.defaultAutoRefetch = paramBoolean;
  }

  public boolean getDefaultAutoRefetch()
    throws SQLException
  {
    return this.defaultAutoRefetch;
  }

  public synchronized void registerTAFCallback(OracleOCIFailover paramOracleOCIFailover, Object paramObject)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public String getDatabaseProductVersion()
    throws SQLException
  {
    if (this.databaseProductVersion == "")
    {
      needLine();

      this.databaseProductVersion = doGetDatabaseProductVersion();
    }

    return this.databaseProductVersion;
  }

  public synchronized boolean getReportRemarks()
  {
    return this.reportRemarks;
  }

  public synchronized short getVersionNumber() throws SQLException
  {
    if (this.versionNumber == -1)
    {
      needLine();

      this.versionNumber = doGetVersionNumber();
    }

    return this.versionNumber;
  }

  public synchronized void registerCloseCallback(OracleCloseCallback paramOracleCloseCallback, Object paramObject)
  {
    this.closeCallback = paramOracleCloseCallback;
    this.privateData = paramObject;
  }

  public void setCreateStatementAsRefCursor(boolean paramBoolean)
  {
  }

  public boolean getCreateStatementAsRefCursor()
  {
    return false;
  }

  public int pingDatabase(int paramInt)
    throws SQLException
  {
    if (this.lifecycle != 1) {
      return -1;
    }
    Statement localStatement = null;
    try
    {
      localStatement = createStatement();

      ((OracleStatement)localStatement).defineColumnType(1, 12, 1);
      localStatement.executeQuery("SELECT 'x' FROM DUAL");
    }
    catch (SQLException localSQLException)
    {
      int i = -1;
      return i;
    }
    finally
    {
      if (localStatement != null) {
        localStatement.close();
      }
    }
    return 0;
  }

  public synchronized Map getJavaObjectTypeMap()
  {
    return this.javaObjectMap;
  }

  public synchronized void setJavaObjectTypeMap(Map paramMap)
  {
    this.javaObjectMap = paramMap;
  }

  /** @deprecated */
  public void clearClientIdentifier(String paramString)
    throws SQLException
  {
    if ((!this.useDMSForEndToEnd) && (paramString != null) && (paramString != ""))
    {
      String[] arrayOfString = getEndToEndMetrics();

      if ((arrayOfString != null) && (paramString.equals(arrayOfString[1])))
      {
        arrayOfString[1] = null;

        setEndToEndMetrics(arrayOfString, getEndToEndECIDSequenceNumber());
      }
    }
  }

  /** @deprecated */
  public void setClientIdentifier(String paramString)
    throws SQLException
  {
    if (!this.useDMSForEndToEnd)
    {
      String[] arrayOfString = getEndToEndMetrics();

      if (arrayOfString == null)
      {
        arrayOfString = new String[4];
      }

      arrayOfString[1] = paramString;

      setEndToEndMetrics(arrayOfString, getEndToEndECIDSequenceNumber());
    }
  }

  public void setSessionTimeZone(String paramString)
    throws SQLException
  {
    Statement localStatement = null;
    ResultSet localResultSet = null;
    try
    {
      localStatement = createStatement();

      localStatement.executeUpdate("ALTER SESSION SET TIME_ZONE = '" + paramString + "'");

      localResultSet = localStatement.executeQuery("SELECT DBTIMEZONE FROM DUAL");

      localResultSet.next();

      String str = localResultSet.getString(1);

      setDbTzCalendar(str);
    }
    catch (SQLException localSQLException)
    {
      throw localSQLException;
    }
    finally
    {
      if (localStatement != null) {
        localStatement.close();
      }
    }
    this.sessionTimeZone = paramString;
  }

  public String getSessionTimeZone()
  {
    return this.sessionTimeZone;
  }

  void setDbTzCalendar(String paramString)
  {
    int i = paramString.charAt(0);

    if ((i == 45) || (i == 43))
      paramString = "GMT" + paramString;
    else {
      paramString = "GMT+" + paramString;
    }

    TimeZone localTimeZone = TimeZone.getTimeZone(paramString);

    this.dbTzCalendar = new GregorianCalendar(localTimeZone);
  }

  public Calendar getDbTzCalendar()
  {
    return this.dbTzCalendar;
  }

  public void setAccumulateBatchResult(boolean paramBoolean)
  {
    this.accumulateBatchResult = paramBoolean;
  }

  public boolean isAccumulateBatchResult()
  {
    return this.accumulateBatchResult;
  }

  public void setJ2EE13Compliant(boolean paramBoolean)
  {
    this.j2ee13Compliant = paramBoolean;
  }

  public boolean getJ2EE13Compliant()
  {
    return this.j2ee13Compliant;
  }

  public Class classForNameAndSchema(String paramString1, String paramString2)
    throws ClassNotFoundException
  {
    return Class.forName(paramString1);
  }

  public Class safelyGetClassForName(String paramString)
    throws ClassNotFoundException
  {
    return Class.forName(paramString);
  }

  public int getHeapAllocSize()
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    DatabaseError.throwSqlException(23);

    return -1;
  }

  public int getOCIEnvHeapAllocSize()
    throws SQLException
  {
    if (this.lifecycle != 1) {
      DatabaseError.throwSqlException(8);
    }
    DatabaseError.throwSqlException(23);

    return -1;
  }

  public static OracleConnection unwrapCompletely(oracle.jdbc.OracleConnection paramOracleConnection)
  {
    Object localObject1 = paramOracleConnection;
    Object localObject2 = localObject1;
    while (true)
    {
      if (localObject2 == null) {
        return (OracleConnection)localObject1;
      }
      localObject1 = localObject2;
      localObject2 = ((oracle.jdbc.OracleConnection)localObject1).unwrap();
    }
  }

  public void setWrapper(oracle.jdbc.OracleConnection paramOracleConnection)
  {
    this.wrapper = paramOracleConnection;
  }

  public oracle.jdbc.OracleConnection unwrap()
  {
    return null;
  }

  public oracle.jdbc.OracleConnection getWrapper()
  {
    if (this.wrapper != null) {
      return this.wrapper;
    }
    return this;
  }

  static oracle.jdbc.internal.OracleConnection _physicalConnectionWithin(Connection paramConnection)
  {
    OracleConnection localOracleConnection = null;

    if (paramConnection != null)
    {
      localOracleConnection = unwrapCompletely((oracle.jdbc.OracleConnection)paramConnection);
    }

    return localOracleConnection;
  }

  public oracle.jdbc.internal.OracleConnection physicalConnectionWithin()
  {
    return this;
  }

  public long getTdoCState(String paramString1, String paramString2)
    throws SQLException
  {
    return 0L;
  }

  public void getOracleTypeADT(OracleTypeADT paramOracleTypeADT) throws SQLException
  {
  }

  public Datum toDatum(CustomDatum paramCustomDatum) throws SQLException {
    return paramCustomDatum.toDatum(this);
  }

  public short getNCharSet()
  {
    return this.conversion.getNCharSetId();
  }

  public ResultSet newArrayDataResultSet(Datum[] paramArrayOfDatum, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    return new ArrayDataResultSet(this, paramArrayOfDatum, paramLong, paramInt, paramMap);
  }

  public ResultSet newArrayDataResultSet(ARRAY paramARRAY, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    return new ArrayDataResultSet(this, paramARRAY, paramLong, paramInt, paramMap);
  }

  public ResultSet newArrayLocatorResultSet(ArrayDescriptor paramArrayDescriptor, byte[] paramArrayOfByte, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    return new ArrayLocatorResultSet(this, paramArrayDescriptor, paramArrayOfByte, paramLong, paramInt, paramMap);
  }

  public ResultSetMetaData newStructMetaData(StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    return new StructMetaData(paramStructDescriptor);
  }

  public int CHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    int[] arrayOfInt = new int[1];

    arrayOfInt[0] = paramInt;

    return this.conversion.CHARBytesToJavaChars(paramArrayOfByte, 0, paramArrayOfChar, 0, arrayOfInt, paramArrayOfChar.length);
  }

  public int NCHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    int[] arrayOfInt = new int[1];

    return this.conversion.NCHARBytesToJavaChars(paramArrayOfByte, 0, paramArrayOfChar, 0, arrayOfInt, paramArrayOfChar.length);
  }

  public boolean IsNCharFixedWith()
  {
    return this.conversion.IsNCharFixedWith();
  }

  public short getDriverCharSet()
  {
    return this.conversion.getClientCharSet();
  }

  public int getMaxCharSize() throws SQLException
  {
    DatabaseError.throwSqlException(58);

    return -1;
  }

  public int getMaxCharbyteSize()
  {
    return this.conversion.getMaxCharbyteSize();
  }

  public int getMaxNCharbyteSize()
  {
    return this.conversion.getMaxNCharbyteSize();
  }

  public boolean isCharSetMultibyte(short paramShort)
  {
    return DBConversion.isCharSetMultibyte(paramShort);
  }

  public int javaCharsToCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return this.conversion.javaCharsToCHARBytes(paramArrayOfChar, paramInt, paramArrayOfByte);
  }

  public int javaCharsToNCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return this.conversion.javaCharsToNCHARBytes(paramArrayOfChar, paramInt, paramArrayOfByte);
  }

  public abstract void getPropertyForPooledConnection(OraclePooledConnection paramOraclePooledConnection)
    throws SQLException;

  final void getPropertyForPooledConnection(OraclePooledConnection paramOraclePooledConnection, String paramString)
    throws SQLException
  {
    Hashtable localHashtable = new Hashtable();

    localHashtable.put("obj_type_map", this.javaObjectMap);

    Properties localProperties = new Properties();

    localProperties.put("user", this.user);
    localProperties.put("password", paramString);

    localProperties.put("connection_url", this.url);
    localProperties.put("connect_auto_commit", "" + this.autoCommitSet);

    localProperties.put("trans_isolation", "" + this.txnLevel);

    if (getStatementCacheSize() != -1)
    {
      localProperties.put("stmt_cache_size", "" + getStatementCacheSize());

      localProperties.put("implicit_cache_enabled", "" + getImplicitCachingEnabled());

      localProperties.put("explict_cache_enabled", "" + getExplicitCachingEnabled());
    }

    localProperties.put("defaultExecuteBatch", "" + this.defaultBatch);
    localProperties.put("prefetch", "" + this.defaultRowPrefetch);
    localProperties.put("remarks", "" + this.reportRemarks);
    localProperties.put("AccumulateBatchResult", "" + this.accumulateBatchResult);
    localProperties.put("oracle.jdbc.J2EE13Compliant", "" + this.j2ee13Compliant);
    localProperties.put("processEscapes", "" + this.processEscapes);

    localProperties.put("restrictGetTables", "" + this.restrictGetTables);
    localProperties.put("synonyms", "" + this.includeSynonyms);
    localProperties.put("fixedString", "" + this.defaultFixedString);

    localHashtable.put("connection_properties", localProperties);

    paramOraclePooledConnection.setProperties(localHashtable);
  }

  public Properties getDBAccessProperties()
    throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public Properties getOCIHandles()
    throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  abstract void logon()
    throws SQLException;

  void logoff()
    throws SQLException
  {
  }

  abstract void open(OracleStatement paramOracleStatement)
    throws SQLException;

  abstract void doCancel()
    throws SQLException;

  abstract void doSetAutoCommit(boolean paramBoolean)
    throws SQLException;

  abstract void doCommit()
    throws SQLException;

  abstract void doRollback()
    throws SQLException;

  abstract String doGetDatabaseProductVersion()
    throws SQLException;

  abstract short doGetVersionNumber()
    throws SQLException;

  int getDefaultStreamChunkSize()
  {
    return this.streamChunkSize;
  }

  abstract OracleStatement RefCursorBytesToStatement(byte[] paramArrayOfByte, OracleStatement paramOracleStatement)
    throws SQLException;

  public oracle.jdbc.internal.OracleStatement refCursorCursorToStatement(int paramInt) throws SQLException
  {
    DatabaseError.throwSqlException(23);

    return null;
  }

  public Connection getLogicalConnection(OraclePooledConnection paramOraclePooledConnection, boolean paramBoolean)
    throws SQLException
  {
    if ((this.logicalConnectionAttached != null) || (paramOraclePooledConnection.getPhysicalHandle() != this)) {
      DatabaseError.throwSqlException(143);
    }
    LogicalConnection localLogicalConnection = new LogicalConnection(paramOraclePooledConnection, this, paramBoolean);

    this.logicalConnectionAttached = localLogicalConnection;

    return localLogicalConnection;
  }

  public void getForm(OracleTypeADT paramOracleTypeADT, OracleTypeCLOB paramOracleTypeCLOB, int paramInt)
    throws SQLException
  {
  }

  public CLOB createClob(byte[] paramArrayOfByte) throws SQLException
  {
    return new CLOB(this, paramArrayOfByte);
  }

  public CLOB createClob(byte[] paramArrayOfByte, short paramShort)
    throws SQLException
  {
    return new CLOB(this, paramArrayOfByte, paramShort);
  }

  public BLOB createBlob(byte[] paramArrayOfByte) throws SQLException
  {
    return new BLOB(this, paramArrayOfByte);
  }

  public BFILE createBfile(byte[] paramArrayOfByte) throws SQLException
  {
    return new BFILE(this, paramArrayOfByte);
  }

  public boolean isDescriptorSharable(oracle.jdbc.internal.OracleConnection paramOracleConnection)
    throws SQLException
  {
    PhysicalConnection localPhysicalConnection1 = this;
    PhysicalConnection localPhysicalConnection2 = (PhysicalConnection)paramOracleConnection.getPhysicalConnection();

    return (localPhysicalConnection1 == localPhysicalConnection2) || (localPhysicalConnection1.url.equals(localPhysicalConnection2.url)) || ((localPhysicalConnection2.protocol != null) && (localPhysicalConnection2.protocol.equals("kprb")));
  }

  boolean useLittleEndianSetCHARBinder()
    throws SQLException
  {
    return false;
  }

  public void setPlsqlWarnings(String paramString)
    throws SQLException
  {
    if (paramString == null) {
      DatabaseError.throwSqlException(68);
    }
    if ((paramString != null) && ((paramString = paramString.trim()).length() > 0) && (!OracleSql.isValidPlsqlWarning(paramString)))
    {
      DatabaseError.throwSqlException(68);
    }
    String str1 = "ALTER SESSION SET PLSQL_WARNINGS=" + paramString;

    String str2 = "ALTER SESSION SET EVENTS='10933 TRACE NAME CONTEXT LEVEL 32768'";

    Statement localStatement = null;
    try
    {
      localStatement = createStatement(-1, -1);

      localStatement.execute(str1);

      if (paramString.equals("'DISABLE:ALL'"))
      {
        this.plsqlCompilerWarnings = false;
      }
      else
      {
        localStatement.execute(str2);

        this.plsqlCompilerWarnings = true;
      }
    }
    finally {
      if (localStatement != null)
        localStatement.close();
    }
  }

  void internalClose()
    throws SQLException
  {
    this.lifecycle = 4;
  }

  public XAResource getXAResource()
    throws SQLException
  {
    DatabaseError.throwSqlException(164);

    return null;
  }

  protected void doDescribeTable(AutoKeyInfo paramAutoKeyInfo)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public void setApplicationContext(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    if ((paramString1 == null) || (paramString2 == null) || (paramString3 == null))
    {
      throw new NullPointerException();
    }
    if (paramString1.equals("")) {
      DatabaseError.throwSqlException(170);
    }

    if (paramString1.compareToIgnoreCase("CLIENTCONTEXT") != 0) {
      DatabaseError.throwSqlException(174);
    }

    if (paramString2.length() > 30) {
      DatabaseError.throwSqlException(171);
    }

    if (paramString3.length() > 4000) {
      DatabaseError.throwSqlException(172);
    }

    doSetApplicationContext(paramString1, paramString2, paramString3);
  }

  void doSetApplicationContext(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public void clearAllApplicationContext(String paramString)
    throws SQLException
  {
    if (paramString == null)
      throw new NullPointerException();
    if (paramString.equals("")) {
      DatabaseError.throwSqlException(170);
    }

    doClearAllApplicationContext(paramString);
  }

  void doClearAllApplicationContext(String paramString)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public boolean isV8Compatible()
    throws SQLException
  {
    return this.v8Compatible;
  }

  static
  {
    endToEndMaxLength[0] = 32;
    endToEndMaxLength[1] = 64;
    endToEndMaxLength[2] = 64;
    endToEndMaxLength[3] = 48;

    _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.PhysicalConnection
 * JD-Core Version:    0.6.0
 */