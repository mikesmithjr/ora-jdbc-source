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
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.pool.OracleConnectionCacheCallback;

public class OracleConnectionWrapper implements OracleConnection {
    protected OracleConnection connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    public OracleConnectionWrapper() {
    }

    public OracleConnectionWrapper(OracleConnection toBeWrapped) {
        this.connection = toBeWrapped;

        toBeWrapped.setWrapper(this);
    }

    public OracleConnection unwrap() {
        return this.connection;
    }

    public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
        return this.connection.physicalConnectionWithin();
    }

    public void setWrapper(OracleConnection wrapper) {
        this.connection.setWrapper(wrapper);
    }

    public Statement createStatement() throws SQLException {
        return this.connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return this.connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException {
        return this.connection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException {
        return this.connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        this.connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return this.connection.getAutoCommit();
    }

    public void commit() throws SQLException {
        this.connection.commit();
    }

    public void rollback() throws SQLException {
        this.connection.rollback();
    }

    public void close() throws SQLException {
        this.connection.close();
    }

    public boolean isClosed() throws SQLException {
        return this.connection.isClosed();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this.connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        this.connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return this.connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException {
        this.connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException {
        return this.connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException {
        this.connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return this.connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException {
        return this.connection.getWarnings();
    }

    public void clearWarnings() throws SQLException {
        this.connection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
            throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map getTypeMap() throws SQLException {
        return this.connection.getTypeMap();
    }

    public void setTypeMap(Map map) throws SQLException {
        this.connection.setTypeMap(map);
    }

    public boolean isProxySession() {
        return this.connection.isProxySession();
    }

    public void openProxySession(int mode, Properties prop) throws SQLException {
        this.connection.openProxySession(mode, prop);
    }

    public void archive(int mode, int aseq, String acstext) throws SQLException {
        this.connection.archive(mode, aseq, acstext);
    }

    public boolean getAutoClose() throws SQLException {
        return this.connection.getAutoClose();
    }

    public CallableStatement getCallWithKey(String key) throws SQLException {
        return this.connection.getCallWithKey(key);
    }

    public int getDefaultExecuteBatch() {
        return this.connection.getDefaultExecuteBatch();
    }

    public int getDefaultRowPrefetch() {
        return this.connection.getDefaultRowPrefetch();
    }

    public Object getDescriptor(String sql_name) {
        return this.connection.getDescriptor(sql_name);
    }

    public String[] getEndToEndMetrics() throws SQLException {
        return this.connection.getEndToEndMetrics();
    }

    public short getEndToEndECIDSequenceNumber() throws SQLException {
        return this.connection.getEndToEndECIDSequenceNumber();
    }

    public boolean getIncludeSynonyms() {
        return this.connection.getIncludeSynonyms();
    }

    public boolean getRestrictGetTables() {
        return this.connection.getRestrictGetTables();
    }

    public boolean getImplicitCachingEnabled() throws SQLException {
        return this.connection.getImplicitCachingEnabled();
    }

    public boolean getExplicitCachingEnabled() throws SQLException {
        return this.connection.getExplicitCachingEnabled();
    }

    public Object getJavaObject(String sql_name) throws SQLException {
        return this.connection.getJavaObject(sql_name);
    }

    public boolean getRemarksReporting() {
        return this.connection.getRemarksReporting();
    }

    public String getSQLType(Object obj) throws SQLException {
        return this.connection.getSQLType(obj);
    }

    public int getStmtCacheSize() {
        return this.connection.getStmtCacheSize();
    }

    public int getStatementCacheSize() throws SQLException {
        return this.connection.getStatementCacheSize();
    }

    public PreparedStatement getStatementWithKey(String key) throws SQLException {
        return this.connection.getStatementWithKey(key);
    }

    public short getStructAttrCsId() throws SQLException {
        return this.connection.getStructAttrCsId();
    }

    public String getUserName() throws SQLException {
        return this.connection.getUserName();
    }

    public boolean getUsingXAFlag() {
        return this.connection.getUsingXAFlag();
    }

    public boolean getXAErrorFlag() {
        return this.connection.getXAErrorFlag();
    }

    public OracleSavepoint oracleSetSavepoint() throws SQLException {
        return this.connection.oracleSetSavepoint();
    }

    public OracleSavepoint oracleSetSavepoint(String name) throws SQLException {
        return this.connection.oracleSetSavepoint(name);
    }

    public void oracleRollback(OracleSavepoint savepoint) throws SQLException {
        this.connection.oracleRollback(savepoint);
    }

    public void oracleReleaseSavepoint(OracleSavepoint savepoint) throws SQLException {
        this.connection.oracleReleaseSavepoint(savepoint);
    }

    public int pingDatabase(int timeOut) throws SQLException {
        return this.connection.pingDatabase(timeOut);
    }

    public void purgeExplicitCache() throws SQLException {
        this.connection.purgeExplicitCache();
    }

    public void purgeImplicitCache() throws SQLException {
        this.connection.purgeImplicitCache();
    }

    public void putDescriptor(String sql_name, Object desc) throws SQLException {
        this.connection.putDescriptor(sql_name, desc);
    }

    public void registerSQLType(String sql_name, Class java_class) throws SQLException {
        this.connection.registerSQLType(sql_name, java_class);
    }

    public void registerSQLType(String sql_name, String java_class_name) throws SQLException {
        this.connection.registerSQLType(sql_name, java_class_name);
    }

    public void setAutoClose(boolean autoClose) throws SQLException {
        this.connection.setAutoClose(autoClose);
    }

    public void setDefaultExecuteBatch(int batch) throws SQLException {
        this.connection.setDefaultExecuteBatch(batch);
    }

    public void setDefaultRowPrefetch(int value) throws SQLException {
        this.connection.setDefaultRowPrefetch(value);
    }

    public void setEndToEndMetrics(String[] metrics, short sequenceNumber) throws SQLException {
        this.connection.setEndToEndMetrics(metrics, sequenceNumber);
    }

    public void setExplicitCachingEnabled(boolean cache) throws SQLException {
        this.connection.setExplicitCachingEnabled(cache);
    }

    public void setImplicitCachingEnabled(boolean cache) throws SQLException {
        this.connection.setImplicitCachingEnabled(cache);
    }

    public void setIncludeSynonyms(boolean synonyms) {
        this.connection.setIncludeSynonyms(synonyms);
    }

    public void setRemarksReporting(boolean reportRemarks) {
        this.connection.setRemarksReporting(reportRemarks);
    }

    public void setRestrictGetTables(boolean restrict) {
        this.connection.setRestrictGetTables(restrict);
    }

    public void setStmtCacheSize(int size) throws SQLException {
        this.connection.setStmtCacheSize(size);
    }

    public void setStatementCacheSize(int size) throws SQLException {
        this.connection.setStatementCacheSize(size);
    }

    public void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
        this.connection.setStmtCacheSize(size, clearMetaData);
    }

    public void setUsingXAFlag(boolean value) {
        this.connection.setUsingXAFlag(value);
    }

    public void setXAErrorFlag(boolean value) {
        this.connection.setXAErrorFlag(value);
    }

    public void shutdown(int mode) throws SQLException {
        this.connection.shutdown(mode);
    }

    public void startup(String startup_str, int mode) throws SQLException {
        this.connection.startup(startup_str, mode);
    }

    public PreparedStatement prepareStatementWithKey(String key) throws SQLException {
        return this.connection.prepareStatementWithKey(key);
    }

    public CallableStatement prepareCallWithKey(String key) throws SQLException {
        return this.connection.prepareCallWithKey(key);
    }

    public void setCreateStatementAsRefCursor(boolean value) {
        this.connection.setCreateStatementAsRefCursor(value);
    }

    public boolean getCreateStatementAsRefCursor() {
        return this.connection.getCreateStatementAsRefCursor();
    }

    public void setSessionTimeZone(String regionName) throws SQLException {
        this.connection.setSessionTimeZone(regionName);
    }

    public String getSessionTimeZone() {
        return this.connection.getSessionTimeZone();
    }

    public Connection _getPC() {
        return this.connection._getPC();
    }

    public boolean isLogicalConnection() {
        return this.connection.isLogicalConnection();
    }

    public void registerTAFCallback(OracleOCIFailover cbk, Object obj) throws SQLException {
        this.connection.registerTAFCallback(cbk, obj);
    }

    public Properties getProperties() {
        return this.connection.getProperties();
    }

    public void close(Properties connAttr) throws SQLException {
        this.connection.close(connAttr);
    }

    public void close(int opt) throws SQLException {
        this.connection.close(opt);
    }

    public void applyConnectionAttributes(Properties connAttr) throws SQLException {
        this.connection.applyConnectionAttributes(connAttr);
    }

    public Properties getConnectionAttributes() throws SQLException {
        return this.connection.getConnectionAttributes();
    }

    public Properties getUnMatchedConnectionAttributes() throws SQLException {
        return this.connection.getUnMatchedConnectionAttributes();
    }

    public void registerConnectionCacheCallback(OracleConnectionCacheCallback occc, Object userObj,
            int cbkFlag) throws SQLException {
        this.connection.registerConnectionCacheCallback(occc, userObj, cbkFlag);
    }

    public void setConnectionReleasePriority(int priority) throws SQLException {
        this.connection.setConnectionReleasePriority(priority);
    }

    public int getConnectionReleasePriority() throws SQLException {
        return this.connection.getConnectionReleasePriority();
    }

    public void setPlsqlWarnings(String setting) throws SQLException {
        this.connection.setPlsqlWarnings(setting);
    }

    public void setHoldability(int holdability) throws SQLException {
        this.connection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException {
        return this.connection.getHoldability();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return this.connection.createStatement(resultSetType, resultSetConcurrency,
                                               resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return this.connection.prepareStatement(sql, resultSetType, resultSetConcurrency,
                                                resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return this.connection.prepareCall(sql, resultSetType, resultSetConcurrency,
                                           resultSetHoldability);
    }

    public synchronized Savepoint setSavepoint() throws SQLException {
        return this.connection.setSavepoint();
    }

    public synchronized Savepoint setSavepoint(String name) throws SQLException {
        return this.connection.setSavepoint(name);
    }

    public synchronized void rollback(Savepoint savepoint) throws SQLException {
        this.connection.rollback(savepoint);
    }

    public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
        this.connection.releaseSavepoint(savepoint);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        return this.connection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return this.connection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return this.connection.prepareStatement(sql, columnNames);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.OracleConnectionWrapper"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.OracleConnectionWrapper JD-Core Version: 0.6.0
 */