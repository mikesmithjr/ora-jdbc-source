package oracle.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import oracle.jdbc.pool.OracleConnectionCacheCallback;

public class OracleConnectionWrapper
  implements OracleConnection
{
  protected OracleConnection connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  public OracleConnectionWrapper()
  {
  }

  public OracleConnectionWrapper(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection;

    paramOracleConnection.setWrapper(this);
  }

  public OracleConnection unwrap()
  {
    return this.connection;
  }

  public oracle.jdbc.internal.OracleConnection physicalConnectionWithin()
  {
    return this.connection.physicalConnectionWithin();
  }

  public void setWrapper(OracleConnection paramOracleConnection)
  {
    this.connection.setWrapper(paramOracleConnection);
  }

  public Statement createStatement()
    throws SQLException
  {
    return this.connection.createStatement();
  }

  public PreparedStatement prepareStatement(String paramString) throws SQLException
  {
    return this.connection.prepareStatement(paramString);
  }

  public CallableStatement prepareCall(String paramString) throws SQLException
  {
    return this.connection.prepareCall(paramString);
  }

  public String nativeSQL(String paramString) throws SQLException
  {
    return this.connection.nativeSQL(paramString);
  }

  public void setAutoCommit(boolean paramBoolean) throws SQLException
  {
    this.connection.setAutoCommit(paramBoolean);
  }

  public boolean getAutoCommit() throws SQLException
  {
    return this.connection.getAutoCommit();
  }

  public void commit() throws SQLException
  {
    this.connection.commit();
  }

  public void rollback() throws SQLException
  {
    this.connection.rollback();
  }

  public void close() throws SQLException
  {
    this.connection.close();
  }

  public boolean isClosed() throws SQLException
  {
    return this.connection.isClosed();
  }

  public DatabaseMetaData getMetaData() throws SQLException
  {
    return this.connection.getMetaData();
  }

  public void setReadOnly(boolean paramBoolean) throws SQLException
  {
    this.connection.setReadOnly(paramBoolean);
  }

  public boolean isReadOnly() throws SQLException
  {
    return this.connection.isReadOnly();
  }

  public void setCatalog(String paramString) throws SQLException
  {
    this.connection.setCatalog(paramString);
  }

  public String getCatalog() throws SQLException
  {
    return this.connection.getCatalog();
  }

  public void setTransactionIsolation(int paramInt) throws SQLException
  {
    this.connection.setTransactionIsolation(paramInt);
  }

  public int getTransactionIsolation() throws SQLException
  {
    return this.connection.getTransactionIsolation();
  }

  public SQLWarning getWarnings() throws SQLException
  {
    return this.connection.getWarnings();
  }

  public void clearWarnings() throws SQLException
  {
    this.connection.clearWarnings();
  }

  public Statement createStatement(int paramInt1, int paramInt2)
    throws SQLException
  {
    return this.connection.createStatement(paramInt1, paramInt2);
  }

  public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    return this.connection.prepareStatement(paramString, paramInt1, paramInt2);
  }

  public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    return this.connection.prepareCall(paramString, paramInt1, paramInt2);
  }

  public Map getTypeMap() throws SQLException
  {
    return this.connection.getTypeMap();
  }

  public void setTypeMap(Map paramMap) throws SQLException
  {
    this.connection.setTypeMap(paramMap);
  }

  public boolean isProxySession()
  {
    return this.connection.isProxySession();
  }

  public void openProxySession(int paramInt, Properties paramProperties)
    throws SQLException
  {
    this.connection.openProxySession(paramInt, paramProperties);
  }

  public void archive(int paramInt1, int paramInt2, String paramString) throws SQLException
  {
    this.connection.archive(paramInt1, paramInt2, paramString);
  }

  public boolean getAutoClose() throws SQLException
  {
    return this.connection.getAutoClose();
  }

  public CallableStatement getCallWithKey(String paramString) throws SQLException
  {
    return this.connection.getCallWithKey(paramString);
  }

  public int getDefaultExecuteBatch()
  {
    return this.connection.getDefaultExecuteBatch();
  }

  public int getDefaultRowPrefetch()
  {
    return this.connection.getDefaultRowPrefetch();
  }

  public Object getDescriptor(String paramString)
  {
    return this.connection.getDescriptor(paramString);
  }

  public String[] getEndToEndMetrics() throws SQLException
  {
    return this.connection.getEndToEndMetrics();
  }

  public short getEndToEndECIDSequenceNumber() throws SQLException
  {
    return this.connection.getEndToEndECIDSequenceNumber();
  }

  public boolean getIncludeSynonyms()
  {
    return this.connection.getIncludeSynonyms();
  }

  public boolean getRestrictGetTables()
  {
    return this.connection.getRestrictGetTables();
  }

  public boolean getImplicitCachingEnabled() throws SQLException
  {
    return this.connection.getImplicitCachingEnabled();
  }

  public boolean getExplicitCachingEnabled() throws SQLException
  {
    return this.connection.getExplicitCachingEnabled();
  }

  public Object getJavaObject(String paramString)
    throws SQLException
  {
    return this.connection.getJavaObject(paramString);
  }

  public boolean getRemarksReporting()
  {
    return this.connection.getRemarksReporting();
  }

  public String getSQLType(Object paramObject) throws SQLException
  {
    return this.connection.getSQLType(paramObject);
  }

  public int getStmtCacheSize()
  {
    return this.connection.getStmtCacheSize();
  }

  public int getStatementCacheSize() throws SQLException
  {
    return this.connection.getStatementCacheSize();
  }

  public PreparedStatement getStatementWithKey(String paramString) throws SQLException
  {
    return this.connection.getStatementWithKey(paramString);
  }

  public short getStructAttrCsId() throws SQLException
  {
    return this.connection.getStructAttrCsId();
  }

  public String getUserName() throws SQLException
  {
    return this.connection.getUserName();
  }

  public boolean getUsingXAFlag()
  {
    return this.connection.getUsingXAFlag();
  }

  public boolean getXAErrorFlag()
  {
    return this.connection.getXAErrorFlag();
  }

  public OracleSavepoint oracleSetSavepoint() throws SQLException
  {
    return this.connection.oracleSetSavepoint();
  }

  public OracleSavepoint oracleSetSavepoint(String paramString) throws SQLException
  {
    return this.connection.oracleSetSavepoint(paramString);
  }

  public void oracleRollback(OracleSavepoint paramOracleSavepoint) throws SQLException
  {
    this.connection.oracleRollback(paramOracleSavepoint);
  }

  public void oracleReleaseSavepoint(OracleSavepoint paramOracleSavepoint)
    throws SQLException
  {
    this.connection.oracleReleaseSavepoint(paramOracleSavepoint);
  }

  public int pingDatabase(int paramInt)
    throws SQLException
  {
    return this.connection.pingDatabase(paramInt);
  }

  public void purgeExplicitCache() throws SQLException
  {
    this.connection.purgeExplicitCache();
  }

  public void purgeImplicitCache() throws SQLException
  {
    this.connection.purgeImplicitCache();
  }

  public void putDescriptor(String paramString, Object paramObject) throws SQLException
  {
    this.connection.putDescriptor(paramString, paramObject);
  }

  public void registerSQLType(String paramString, Class paramClass)
    throws SQLException
  {
    this.connection.registerSQLType(paramString, paramClass);
  }

  public void registerSQLType(String paramString1, String paramString2)
    throws SQLException
  {
    this.connection.registerSQLType(paramString1, paramString2);
  }

  public void setAutoClose(boolean paramBoolean) throws SQLException
  {
    this.connection.setAutoClose(paramBoolean);
  }

  public void setDefaultExecuteBatch(int paramInt) throws SQLException
  {
    this.connection.setDefaultExecuteBatch(paramInt);
  }

  public void setDefaultRowPrefetch(int paramInt) throws SQLException
  {
    this.connection.setDefaultRowPrefetch(paramInt);
  }

  public void setEndToEndMetrics(String[] paramArrayOfString, short paramShort)
    throws SQLException
  {
    this.connection.setEndToEndMetrics(paramArrayOfString, paramShort);
  }

  public void setExplicitCachingEnabled(boolean paramBoolean) throws SQLException
  {
    this.connection.setExplicitCachingEnabled(paramBoolean);
  }

  public void setImplicitCachingEnabled(boolean paramBoolean) throws SQLException
  {
    this.connection.setImplicitCachingEnabled(paramBoolean);
  }

  public void setIncludeSynonyms(boolean paramBoolean)
  {
    this.connection.setIncludeSynonyms(paramBoolean);
  }

  public void setRemarksReporting(boolean paramBoolean)
  {
    this.connection.setRemarksReporting(paramBoolean);
  }

  public void setRestrictGetTables(boolean paramBoolean)
  {
    this.connection.setRestrictGetTables(paramBoolean);
  }

  public void setStmtCacheSize(int paramInt) throws SQLException
  {
    this.connection.setStmtCacheSize(paramInt);
  }

  public void setStatementCacheSize(int paramInt) throws SQLException
  {
    this.connection.setStatementCacheSize(paramInt);
  }

  public void setStmtCacheSize(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    this.connection.setStmtCacheSize(paramInt, paramBoolean);
  }

  public void setUsingXAFlag(boolean paramBoolean)
  {
    this.connection.setUsingXAFlag(paramBoolean);
  }

  public void setXAErrorFlag(boolean paramBoolean)
  {
    this.connection.setXAErrorFlag(paramBoolean);
  }

  public void shutdown(int paramInt) throws SQLException
  {
    this.connection.shutdown(paramInt);
  }

  public void startup(String paramString, int paramInt) throws SQLException
  {
    this.connection.startup(paramString, paramInt);
  }

  public PreparedStatement prepareStatementWithKey(String paramString)
    throws SQLException
  {
    return this.connection.prepareStatementWithKey(paramString);
  }

  public CallableStatement prepareCallWithKey(String paramString) throws SQLException
  {
    return this.connection.prepareCallWithKey(paramString);
  }

  public void setCreateStatementAsRefCursor(boolean paramBoolean)
  {
    this.connection.setCreateStatementAsRefCursor(paramBoolean);
  }

  public boolean getCreateStatementAsRefCursor()
  {
    return this.connection.getCreateStatementAsRefCursor();
  }

  public void setSessionTimeZone(String paramString) throws SQLException
  {
    this.connection.setSessionTimeZone(paramString);
  }

  public String getSessionTimeZone()
  {
    return this.connection.getSessionTimeZone();
  }

  public Connection _getPC()
  {
    return this.connection._getPC();
  }

  public boolean isLogicalConnection()
  {
    return this.connection.isLogicalConnection();
  }

  public void registerTAFCallback(OracleOCIFailover paramOracleOCIFailover, Object paramObject)
    throws SQLException
  {
    this.connection.registerTAFCallback(paramOracleOCIFailover, paramObject);
  }

  public Properties getProperties()
  {
    return this.connection.getProperties();
  }

  public void close(Properties paramProperties)
    throws SQLException
  {
    this.connection.close(paramProperties);
  }

  public void close(int paramInt) throws SQLException
  {
    this.connection.close(paramInt);
  }

  public void applyConnectionAttributes(Properties paramProperties)
    throws SQLException
  {
    this.connection.applyConnectionAttributes(paramProperties);
  }

  public Properties getConnectionAttributes() throws SQLException
  {
    return this.connection.getConnectionAttributes();
  }

  public Properties getUnMatchedConnectionAttributes()
    throws SQLException
  {
    return this.connection.getUnMatchedConnectionAttributes();
  }

  public void registerConnectionCacheCallback(OracleConnectionCacheCallback paramOracleConnectionCacheCallback, Object paramObject, int paramInt)
    throws SQLException
  {
    this.connection.registerConnectionCacheCallback(paramOracleConnectionCacheCallback, paramObject, paramInt);
  }

  public void setConnectionReleasePriority(int paramInt) throws SQLException
  {
    this.connection.setConnectionReleasePriority(paramInt);
  }

  public int getConnectionReleasePriority() throws SQLException
  {
    return this.connection.getConnectionReleasePriority();
  }

  public void setPlsqlWarnings(String paramString) throws SQLException
  {
    this.connection.setPlsqlWarnings(paramString);
  }

  public void setHoldability(int paramInt) throws SQLException
  {
    this.connection.setHoldability(paramInt);
  }

  public int getHoldability() throws SQLException
  {
    return this.connection.getHoldability();
  }

  public Statement createStatement(int paramInt1, int paramInt2, int paramInt3) throws SQLException
  {
    return this.connection.createStatement(paramInt1, paramInt2, paramInt3);
  }

  public PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    return this.connection.prepareStatement(paramString, paramInt1, paramInt2, paramInt3);
  }

  public CallableStatement prepareCall(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    return this.connection.prepareCall(paramString, paramInt1, paramInt2, paramInt3);
  }

  public synchronized Savepoint setSavepoint()
    throws SQLException
  {
    return this.connection.setSavepoint();
  }

  public synchronized Savepoint setSavepoint(String paramString)
    throws SQLException
  {
    return this.connection.setSavepoint(paramString);
  }

  public synchronized void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    this.connection.rollback(paramSavepoint);
  }

  public synchronized void releaseSavepoint(Savepoint paramSavepoint)
    throws SQLException
  {
    this.connection.releaseSavepoint(paramSavepoint);
  }

  public PreparedStatement prepareStatement(String paramString, int paramInt)
    throws SQLException
  {
    return this.connection.prepareStatement(paramString, paramInt);
  }

  public PreparedStatement prepareStatement(String paramString, int[] paramArrayOfInt)
    throws SQLException
  {
    return this.connection.prepareStatement(paramString, paramArrayOfInt);
  }

  public PreparedStatement prepareStatement(String paramString, String[] paramArrayOfString)
    throws SQLException
  {
    return this.connection.prepareStatement(paramString, paramArrayOfString);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.OracleConnectionWrapper
 * JD-Core Version:    0.6.0
 */