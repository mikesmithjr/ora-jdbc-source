package oracle.jdbc.driver;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import javax.transaction.xa.XAResource;
import oracle.jdbc.internal.OracleStatement;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCLOB;
import oracle.jdbc.pool.OracleConnectionCacheCallback;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.BfileDBAccess;
import oracle.sql.BlobDBAccess;
import oracle.sql.CLOB;
import oracle.sql.ClobDBAccess;
import oracle.sql.CustomDatum;
import oracle.sql.Datum;
import oracle.sql.StructDescriptor;

class LogicalConnection extends OracleConnection
{
  static final ClosedConnection closedConnection = new ClosedConnection();
  PhysicalConnection internalConnection;
  OraclePooledConnection pooledConnection;
  boolean closed;
  OracleCloseCallback closeCallback = null;
  Object privateData = null;

  long startTime = 0L;

  OracleConnectionCacheCallback connectionCacheCallback = null;
  Object connectionCacheCallbackUserObj = null;
  int callbackFlag = 0;
  int releasePriority = 0;

  int heartbeatCount = 0;
  int heartbeatLastCount = 0;
  int heartbeatNoChangeCount = 0;
  boolean isAbandonedTimeoutEnabled = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  LogicalConnection(OraclePooledConnection paramOraclePooledConnection, PhysicalConnection paramPhysicalConnection, boolean paramBoolean)
    throws SQLException
  {
    this.internalConnection = paramPhysicalConnection;
    this.pooledConnection = paramOraclePooledConnection;
    this.connection = this.internalConnection;

    this.connection.setWrapper(this);

    this.closed = false;

    this.internalConnection.setAutoCommit(paramBoolean);
  }

  void registerHeartbeat()
    throws SQLException
  {
    if (this.isAbandonedTimeoutEnabled)
    {
      try
      {
        this.heartbeatCount += 1;
      }
      catch (ArithmeticException localArithmeticException)
      {
        this.heartbeatCount = 0;
      }
    }
  }

  public int getHeartbeatNoChangeCount()
    throws SQLException
  {
    if (this.heartbeatCount == this.heartbeatLastCount)
    {
      this.heartbeatNoChangeCount += 1;
    }
    else
    {
      this.heartbeatLastCount = this.heartbeatCount;
      this.heartbeatNoChangeCount = 0;
    }

    return this.heartbeatNoChangeCount;
  }

  public oracle.jdbc.internal.OracleConnection physicalConnectionWithin()
  {
    return this.internalConnection;
  }

  public synchronized void registerCloseCallback(OracleCloseCallback paramOracleCloseCallback, Object paramObject)
  {
    this.closeCallback = paramOracleCloseCallback;
    this.privateData = paramObject;
  }

  public Connection _getPC()
  {
    return this.internalConnection;
  }

  public synchronized boolean isLogicalConnection()
  {
    return true;
  }

  public oracle.jdbc.internal.OracleConnection getPhysicalConnection()
  {
    return this.internalConnection;
  }

  public Connection getLogicalConnection(OraclePooledConnection paramOraclePooledConnection, boolean paramBoolean)
    throws SQLException
  {
    DatabaseError.throwSqlException(153);

    return null;
  }

  public void getPropertyForPooledConnection(OraclePooledConnection paramOraclePooledConnection)
    throws SQLException
  {
    DatabaseError.throwSqlException(153);
  }

  public synchronized void close()
    throws SQLException
  {
    closeInternal(true);
  }

  public void closeInternal(boolean paramBoolean)
    throws SQLException
  {
    if (this.closed) {
      return;
    }
    if (this.closeCallback != null) {
      this.closeCallback.beforeClose(this, this.privateData);
    }

    this.internalConnection.closeLogicalConnection();

    this.startTime = 0L;

    this.closed = true;

    if ((this.pooledConnection != null) && (paramBoolean)) {
      this.pooledConnection.logicalClose();
    }

    this.internalConnection = closedConnection;
    this.connection = closedConnection;

    if (this.closeCallback != null)
      this.closeCallback.afterClose(this.privateData);
  }

  public void abort() throws SQLException
  {
    if (this.closed)
      return;
    this.internalConnection.abort();
    this.closed = true;

    this.internalConnection = closedConnection;
    this.connection = closedConnection;
  }

  public synchronized void close(Properties paramProperties)
    throws SQLException
  {
    if (this.pooledConnection != null)
    {
      this.pooledConnection.cachedConnectionAttributes.clear();
      this.pooledConnection.cachedConnectionAttributes.putAll(paramProperties);
    }

    close();
  }

  public synchronized void close(int paramInt)
    throws SQLException
  {
    if ((paramInt & 0x1000) != 0)
    {
      if (this.pooledConnection != null) {
        this.pooledConnection.closeOption = paramInt;
      }
      close();

      return;
    }

    if ((paramInt & 0x1) != 0)
    {
      this.internalConnection.close(1);
    }
  }

  public synchronized void applyConnectionAttributes(Properties paramProperties)
    throws SQLException
  {
    if (this.pooledConnection != null)
      this.pooledConnection.cachedConnectionAttributes.putAll(paramProperties);
  }

  public synchronized Properties getConnectionAttributes()
    throws SQLException
  {
    if (this.pooledConnection != null) {
      return this.pooledConnection.cachedConnectionAttributes;
    }
    return null;
  }

  public synchronized Properties getUnMatchedConnectionAttributes()
    throws SQLException
  {
    if (this.pooledConnection != null) {
      return this.pooledConnection.unMatchedCachedConnAttr;
    }
    return null;
  }

  public synchronized void setAbandonedTimeoutEnabled(boolean paramBoolean)
    throws SQLException
  {
    this.isAbandonedTimeoutEnabled = true;
  }

  public synchronized void registerConnectionCacheCallback(OracleConnectionCacheCallback paramOracleConnectionCacheCallback, Object paramObject, int paramInt)
    throws SQLException
  {
    this.connectionCacheCallback = paramOracleConnectionCacheCallback;
    this.connectionCacheCallbackUserObj = paramObject;
    this.callbackFlag = paramInt;
  }

  public OracleConnectionCacheCallback getConnectionCacheCallbackObj()
    throws SQLException
  {
    return this.connectionCacheCallback;
  }

  public Object getConnectionCacheCallbackPrivObj()
    throws SQLException
  {
    return this.connectionCacheCallbackUserObj;
  }

  public int getConnectionCacheCallbackFlag()
    throws SQLException
  {
    return this.callbackFlag;
  }

  public synchronized void setConnectionReleasePriority(int paramInt)
    throws SQLException
  {
    this.releasePriority = paramInt;
  }

  public int getConnectionReleasePriority()
    throws SQLException
  {
    return this.releasePriority;
  }

  public synchronized boolean isClosed()
    throws SQLException
  {
    return this.closed;
  }

  public void setStartTime(long paramLong)
    throws SQLException
  {
    if (paramLong <= 0L)
    {
      DatabaseError.throwSqlException(68);
    }
    else
    {
      this.startTime = paramLong;
    }
  }

  public long getStartTime()
    throws SQLException
  {
    return this.startTime;
  }

  public Properties getServerSessionInfo()
    throws SQLException
  {
    return this.internalConnection.getServerSessionInfo();
  }

  public Object getClientData(Object paramObject)
  {
    return this.internalConnection.getClientData(paramObject);
  }

  public Object setClientData(Object paramObject1, Object paramObject2)
  {
    return this.internalConnection.setClientData(paramObject1, paramObject2);
  }

  public Object removeClientData(Object paramObject)
  {
    return this.internalConnection.removeClientData(paramObject);
  }

  public void setClientIdentifier(String paramString) throws SQLException
  {
    this.internalConnection.setClientIdentifier(paramString);
  }

  public void clearClientIdentifier(String paramString) throws SQLException
  {
    this.internalConnection.clearClientIdentifier(paramString);
  }

  public short getStructAttrNCsId() throws SQLException
  {
    return this.internalConnection.getStructAttrNCsId();
  }

  public Map getTypeMap() throws SQLException
  {
    return this.internalConnection.getTypeMap();
  }

  public Properties getDBAccessProperties() throws SQLException
  {
    return this.internalConnection.getDBAccessProperties();
  }

  public Properties getOCIHandles() throws SQLException
  {
    return this.internalConnection.getOCIHandles();
  }

  public String getDatabaseProductVersion() throws SQLException
  {
    return this.internalConnection.getDatabaseProductVersion();
  }

  public void cancel() throws SQLException
  {
    this.internalConnection.cancel();
  }

  public String getURL() throws SQLException
  {
    return this.internalConnection.getURL();
  }

  public boolean getIncludeSynonyms()
  {
    return this.internalConnection.getIncludeSynonyms();
  }

  public boolean getRemarksReporting()
  {
    return this.internalConnection.getRemarksReporting();
  }

  public boolean getRestrictGetTables()
  {
    return this.internalConnection.getRestrictGetTables();
  }

  public short getVersionNumber() throws SQLException
  {
    return this.internalConnection.getVersionNumber();
  }

  public Map getJavaObjectTypeMap()
  {
    return this.internalConnection.getJavaObjectTypeMap();
  }

  public void setJavaObjectTypeMap(Map paramMap)
  {
    this.internalConnection.setJavaObjectTypeMap(paramMap);
  }

  public BfileDBAccess createBfileDBAccess() throws SQLException
  {
    return this.internalConnection.createBfileDBAccess();
  }

  public BlobDBAccess createBlobDBAccess() throws SQLException
  {
    return this.internalConnection.createBlobDBAccess();
  }

  public ClobDBAccess createClobDBAccess() throws SQLException
  {
    return this.internalConnection.createClobDBAccess();
  }

  public void setDefaultFixedString(boolean paramBoolean)
  {
    this.internalConnection.setDefaultFixedString(paramBoolean);
  }

  public boolean getDefaultFixedString()
  {
    return this.internalConnection.getDefaultFixedString();
  }

  public oracle.jdbc.OracleConnection getWrapper()
  {
    return this;
  }

  public Class classForNameAndSchema(String paramString1, String paramString2)
    throws ClassNotFoundException
  {
    return this.internalConnection.classForNameAndSchema(paramString1, paramString2);
  }

  public void setFDO(byte[] paramArrayOfByte) throws SQLException
  {
    this.internalConnection.setFDO(paramArrayOfByte);
  }

  public byte[] getFDO(boolean paramBoolean) throws SQLException
  {
    return this.internalConnection.getFDO(paramBoolean);
  }

  public boolean getBigEndian() throws SQLException
  {
    return this.internalConnection.getBigEndian();
  }

  public Object getDescriptor(byte[] paramArrayOfByte)
  {
    return this.internalConnection.getDescriptor(paramArrayOfByte);
  }

  public void putDescriptor(byte[] paramArrayOfByte, Object paramObject) throws SQLException
  {
    this.internalConnection.putDescriptor(paramArrayOfByte, paramObject);
  }

  public void removeDescriptor(String paramString)
  {
    this.internalConnection.removeDescriptor(paramString);
  }

  public void removeAllDescriptor()
  {
    this.internalConnection.removeAllDescriptor();
  }

  public int numberOfDescriptorCacheEntries()
  {
    return this.internalConnection.numberOfDescriptorCacheEntries();
  }

  public Enumeration descriptorCacheKeys()
  {
    return this.internalConnection.descriptorCacheKeys();
  }

  public void getOracleTypeADT(OracleTypeADT paramOracleTypeADT)
    throws SQLException
  {
    this.internalConnection.getOracleTypeADT(paramOracleTypeADT);
  }

  public short getDbCsId() throws SQLException
  {
    return this.internalConnection.getDbCsId();
  }

  public short getJdbcCsId() throws SQLException
  {
    return this.internalConnection.getJdbcCsId();
  }

  public short getNCharSet()
  {
    return this.internalConnection.getNCharSet();
  }

  public ResultSet newArrayDataResultSet(Datum[] paramArrayOfDatum, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    return this.internalConnection.newArrayDataResultSet(paramArrayOfDatum, paramLong, paramInt, paramMap);
  }

  public ResultSet newArrayDataResultSet(ARRAY paramARRAY, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    return this.internalConnection.newArrayDataResultSet(paramARRAY, paramLong, paramInt, paramMap);
  }

  public ResultSet newArrayLocatorResultSet(ArrayDescriptor paramArrayDescriptor, byte[] paramArrayOfByte, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    return this.internalConnection.newArrayLocatorResultSet(paramArrayDescriptor, paramArrayOfByte, paramLong, paramInt, paramMap);
  }

  public ResultSetMetaData newStructMetaData(StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    return this.internalConnection.newStructMetaData(paramStructDescriptor);
  }

  public void getForm(OracleTypeADT paramOracleTypeADT, OracleTypeCLOB paramOracleTypeCLOB, int paramInt)
    throws SQLException
  {
    this.internalConnection.getForm(paramOracleTypeADT, paramOracleTypeCLOB, paramInt);
  }

  public int CHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    return this.internalConnection.CHARBytesToJavaChars(paramArrayOfByte, paramInt, paramArrayOfChar);
  }

  public int NCHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    return this.internalConnection.NCHARBytesToJavaChars(paramArrayOfByte, paramInt, paramArrayOfChar);
  }

  public boolean IsNCharFixedWith()
  {
    return this.internalConnection.IsNCharFixedWith();
  }

  public short getDriverCharSet()
  {
    return this.internalConnection.getDriverCharSet();
  }

  public int getC2SNlsRatio()
  {
    return this.internalConnection.getC2SNlsRatio();
  }

  public int getMaxCharSize() throws SQLException
  {
    return this.internalConnection.getMaxCharSize();
  }

  public int getMaxCharbyteSize()
  {
    return this.internalConnection.getMaxCharbyteSize();
  }

  public int getMaxNCharbyteSize()
  {
    return this.internalConnection.getMaxNCharbyteSize();
  }

  public boolean isCharSetMultibyte(short paramShort)
  {
    return this.internalConnection.isCharSetMultibyte(paramShort);
  }

  public int javaCharsToCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return this.internalConnection.javaCharsToCHARBytes(paramArrayOfChar, paramInt, paramArrayOfByte);
  }

  public int javaCharsToNCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return this.internalConnection.javaCharsToNCHARBytes(paramArrayOfChar, paramInt, paramArrayOfByte);
  }

  public int getStmtCacheSize()
  {
    return this.internalConnection.getStmtCacheSize();
  }

  public int getStatementCacheSize() throws SQLException
  {
    return this.internalConnection.getStatementCacheSize();
  }

  public boolean getImplicitCachingEnabled() throws SQLException
  {
    return this.internalConnection.getImplicitCachingEnabled();
  }

  public boolean getExplicitCachingEnabled() throws SQLException
  {
    return this.internalConnection.getExplicitCachingEnabled();
  }

  public void purgeImplicitCache() throws SQLException
  {
    this.internalConnection.purgeImplicitCache();
  }

  public void purgeExplicitCache() throws SQLException
  {
    this.internalConnection.purgeExplicitCache();
  }

  public PreparedStatement getStatementWithKey(String paramString)
    throws SQLException
  {
    return this.internalConnection.getStatementWithKey(paramString);
  }

  public CallableStatement getCallWithKey(String paramString)
    throws SQLException
  {
    return this.internalConnection.getCallWithKey(paramString);
  }

  public boolean isStatementCacheInitialized()
  {
    return this.internalConnection.isStatementCacheInitialized();
  }

  public void setTypeMap(Map paramMap)
  {
    this.internalConnection.setTypeMap(paramMap);
  }

  public String getProtocolType()
  {
    return this.internalConnection.getProtocolType();
  }

  public void setTxnMode(int paramInt)
  {
    this.internalConnection.setTxnMode(paramInt);
  }

  public int getTxnMode()
  {
    return this.internalConnection.getTxnMode();
  }

  public int getHeapAllocSize()
    throws SQLException
  {
    return this.internalConnection.getHeapAllocSize();
  }

  public int getOCIEnvHeapAllocSize() throws SQLException
  {
    return this.internalConnection.getOCIEnvHeapAllocSize();
  }

  public CLOB createClob(byte[] paramArrayOfByte) throws SQLException
  {
    return this.internalConnection.createClob(paramArrayOfByte);
  }

  public CLOB createClob(byte[] paramArrayOfByte, short paramShort)
    throws SQLException
  {
    return this.internalConnection.createClob(paramArrayOfByte, paramShort);
  }

  public BLOB createBlob(byte[] paramArrayOfByte) throws SQLException
  {
    return this.internalConnection.createBlob(paramArrayOfByte);
  }

  public BFILE createBfile(byte[] paramArrayOfByte) throws SQLException
  {
    return this.internalConnection.createBfile(paramArrayOfByte);
  }

  public boolean isDescriptorSharable(oracle.jdbc.internal.OracleConnection paramOracleConnection)
    throws SQLException
  {
    return this.internalConnection.isDescriptorSharable(paramOracleConnection);
  }

  public OracleStatement refCursorCursorToStatement(int paramInt)
    throws SQLException
  {
    return this.internalConnection.refCursorCursorToStatement(paramInt);
  }

  public long getTdoCState(String paramString1, String paramString2) throws SQLException
  {
    return this.internalConnection.getTdoCState(paramString1, paramString2);
  }

  public Datum toDatum(CustomDatum paramCustomDatum) throws SQLException {
    return this.internalConnection.toDatum(paramCustomDatum);
  }

  public XAResource getXAResource()
    throws SQLException
  {
    return this.pooledConnection.getXAResource();
  }

  public void setApplicationContext(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    this.internalConnection.setApplicationContext(paramString1, paramString2, paramString3);
  }

  public void clearAllApplicationContext(String paramString)
    throws SQLException
  {
    this.internalConnection.clearAllApplicationContext(paramString);
  }

  public boolean isV8Compatible()
    throws SQLException
  {
    return this.internalConnection.isV8Compatible();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.LogicalConnection
 * JD-Core Version:    0.6.0
 */