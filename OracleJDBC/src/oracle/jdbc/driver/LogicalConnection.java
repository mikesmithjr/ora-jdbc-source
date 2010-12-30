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
import java.util.logging.Level;
import java.util.logging.Logger;
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

class LogicalConnection extends OracleConnection {
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

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    LogicalConnection(OraclePooledConnection _pooledConnection,
            PhysicalConnection _internalConnection, boolean autoCommit) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "LogicalConnection.LogicalConnection(_pooledConnection, autoCommit="
                                               + autoCommit + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.internalConnection = _internalConnection;
        this.pooledConnection = _pooledConnection;
        this.connection = this.internalConnection;

        this.connection.setWrapper(this);

        this.closed = false;

        this.internalConnection.setAutoCommit(autoCommit);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LogicalConnection.LogicalConnection:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    void registerHeartbeat() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LogicalConnection.registerHeartbeat()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.isAbandonedTimeoutEnabled) {
            try {
                this.heartbeatCount += 1;
            } catch (ArithmeticException ae) {
                this.heartbeatCount = 0;
            }
        }
    }

    public int getHeartbeatNoChangeCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LogicalConnection.getHeartbeatNoChangeCount()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (this.heartbeatCount == this.heartbeatLastCount) {
            this.heartbeatNoChangeCount += 1;
        } else {
            this.heartbeatLastCount = this.heartbeatCount;
            this.heartbeatNoChangeCount = 0;
        }

        return this.heartbeatNoChangeCount;
    }

    public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
        return this.internalConnection;
    }

    public synchronized void registerCloseCallback(OracleCloseCallback occ, Object privData) {
        this.closeCallback = occ;
        this.privateData = privData;
    }

    public Connection _getPC() {
        return this.internalConnection;
    }

    public synchronized boolean isLogicalConnection() {
        return true;
    }

    public oracle.jdbc.internal.OracleConnection getPhysicalConnection() {
        return this.internalConnection;
    }

    public Connection getLogicalConnection(OraclePooledConnection pc, boolean autoCommit)
            throws SQLException {
        DatabaseError.throwSqlException(153);

        return null;
    }

    public void getPropertyForPooledConnection(OraclePooledConnection pc) throws SQLException {
        DatabaseError.throwSqlException(153);
    }

    public synchronized void close() throws SQLException {
        closeInternal(true);
    }

    public void closeInternal(boolean putPhysicalConnBackInCache) throws SQLException {
        if (this.closed) {
            return;
        }
        if (this.closeCallback != null) {
            this.closeCallback.beforeClose(this, this.privateData);
        }

        this.internalConnection.closeLogicalConnection();

        this.startTime = 0L;

        this.closed = true;

        if ((this.pooledConnection != null) && (putPhysicalConnBackInCache)) {
            this.pooledConnection.logicalClose();
        }

        this.internalConnection = closedConnection;
        this.connection = closedConnection;

        if (this.closeCallback != null)
            this.closeCallback.afterClose(this.privateData);
    }

    public void abort() throws SQLException {
        if (this.closed)
            return;
        this.internalConnection.abort();
        this.closed = true;

        this.internalConnection = closedConnection;
        this.connection = closedConnection;
    }

    public synchronized void close(Properties cachedConnectionAttributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "LogicalConnection.close(cachedConnectionAttributes="
                                               + cachedConnectionAttributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.pooledConnection != null) {
            this.pooledConnection.cachedConnectionAttributes.clear();
            this.pooledConnection.cachedConnectionAttributes.putAll(cachedConnectionAttributes);
        }

        close();
    }

    public synchronized void close(int opt) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "LogicalConnection.close(opt=" + opt + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((opt & 0x1000) != 0) {
            if (this.pooledConnection != null) {
                this.pooledConnection.closeOption = opt;
            }
            close();

            return;
        }

        if ((opt & 0x1) != 0) {
            this.internalConnection.close(1);
        }
    }

    public synchronized void applyConnectionAttributes(Properties cachedConnectionAttributes)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "LogicalConnection.applyConnectionAttributes(cachedConnectionAttributes="
                                               + cachedConnectionAttributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.pooledConnection != null)
            this.pooledConnection.cachedConnectionAttributes.putAll(cachedConnectionAttributes);
    }

    public synchronized Properties getConnectionAttributes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "LogicalConnection.getConnectionAttributes()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (this.pooledConnection != null) {
            return this.pooledConnection.cachedConnectionAttributes;
        }
        return null;
    }

    public synchronized Properties getUnMatchedConnectionAttributes() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "LogicalConnection.getUnMatchedConnectionAttributes()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.pooledConnection != null) {
            return this.pooledConnection.unMatchedCachedConnAttr;
        }
        return null;
    }

    public synchronized void setAbandonedTimeoutEnabled(boolean val) throws SQLException {
        this.isAbandonedTimeoutEnabled = true;
    }

    public synchronized void registerConnectionCacheCallback(OracleConnectionCacheCallback occc,
            Object userObj, int cbkFlag) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "LogicalConnection.registerConnectionCacheCallback(OracleConnectionCacheCallback="
                                               + occc + "userObj=" + userObj + "cbkFlag=" + cbkFlag
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connectionCacheCallback = occc;
        this.connectionCacheCallbackUserObj = userObj;
        this.callbackFlag = cbkFlag;
    }

    public OracleConnectionCacheCallback getConnectionCacheCallbackObj() throws SQLException {
        return this.connectionCacheCallback;
    }

    public Object getConnectionCacheCallbackPrivObj() throws SQLException {
        return this.connectionCacheCallbackUserObj;
    }

    public int getConnectionCacheCallbackFlag() throws SQLException {
        return this.callbackFlag;
    }

    public synchronized void setConnectionReleasePriority(int priority) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "LogicalConnection.setConnectionReleasePriority(Priority="
                                               + priority + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.releasePriority = priority;
    }

    public int getConnectionReleasePriority() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "LogicalConnection.getConnectionReleasePriority()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.releasePriority;
    }

    public synchronized boolean isClosed() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LogicalConnection.isClosed() returned "
                    + this.closed, this);

            OracleLog.recursiveTrace = false;
        }

        return this.closed;
    }

    public void setStartTime(long millis) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "LogicalConnection.setStartTime(" + millis + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (millis <= 0L) {
            DatabaseError.throwSqlException(68);
        } else {
            this.startTime = millis;
        }
    }

    public long getStartTime() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.getStartTime(): returned "
                    + this.startTime, this);

            OracleLog.recursiveTrace = false;
        }

        return this.startTime;
    }

    public Properties getServerSessionInfo() throws SQLException {
        return this.internalConnection.getServerSessionInfo();
    }

    public Object getClientData(Object key) {
        return this.internalConnection.getClientData(key);
    }

    public Object setClientData(Object key, Object value) {
        return this.internalConnection.setClientData(key, value);
    }

    public Object removeClientData(Object key) {
        return this.internalConnection.removeClientData(key);
    }

    public void setClientIdentifier(String clientId) throws SQLException {
        this.internalConnection.setClientIdentifier(clientId);
    }

    public void clearClientIdentifier(String clientId) throws SQLException {
        this.internalConnection.clearClientIdentifier(clientId);
    }

    public short getStructAttrNCsId() throws SQLException {
        return this.internalConnection.getStructAttrNCsId();
    }

    public Map getTypeMap() throws SQLException {
        return this.internalConnection.getTypeMap();
    }

    public Properties getDBAccessProperties() throws SQLException {
        return this.internalConnection.getDBAccessProperties();
    }

    public Properties getOCIHandles() throws SQLException {
        return this.internalConnection.getOCIHandles();
    }

    public String getDatabaseProductVersion() throws SQLException {
        return this.internalConnection.getDatabaseProductVersion();
    }

    public void cancel() throws SQLException {
        this.internalConnection.cancel();
    }

    public String getURL() throws SQLException {
        return this.internalConnection.getURL();
    }

    public boolean getIncludeSynonyms() {
        return this.internalConnection.getIncludeSynonyms();
    }

    public boolean getRemarksReporting() {
        return this.internalConnection.getRemarksReporting();
    }

    public boolean getRestrictGetTables() {
        return this.internalConnection.getRestrictGetTables();
    }

    public short getVersionNumber() throws SQLException {
        return this.internalConnection.getVersionNumber();
    }

    public Map getJavaObjectTypeMap() {
        return this.internalConnection.getJavaObjectTypeMap();
    }

    public void setJavaObjectTypeMap(Map map) {
        this.internalConnection.setJavaObjectTypeMap(map);
    }

    public BfileDBAccess createBfileDBAccess() throws SQLException {
        return this.internalConnection.createBfileDBAccess();
    }

    public BlobDBAccess createBlobDBAccess() throws SQLException {
        return this.internalConnection.createBlobDBAccess();
    }

    public ClobDBAccess createClobDBAccess() throws SQLException {
        return this.internalConnection.createClobDBAccess();
    }

    public void setDefaultFixedString(boolean fixedString) {
        this.internalConnection.setDefaultFixedString(fixedString);
    }

    public boolean getDefaultFixedString() {
        return this.internalConnection.getDefaultFixedString();
    }

    public oracle.jdbc.OracleConnection getWrapper() {
        return this;
    }

    public Class classForNameAndSchema(String name, String schemaName)
            throws ClassNotFoundException {
        return this.internalConnection.classForNameAndSchema(name, schemaName);
    }

    public void setFDO(byte[] fdo) throws SQLException {
        this.internalConnection.setFDO(fdo);
    }

    public byte[] getFDO(boolean init) throws SQLException {
        return this.internalConnection.getFDO(init);
    }

    public boolean getBigEndian() throws SQLException {
        return this.internalConnection.getBigEndian();
    }

    public Object getDescriptor(byte[] toid) {
        return this.internalConnection.getDescriptor(toid);
    }

    public void putDescriptor(byte[] toid, Object desc) throws SQLException {
        this.internalConnection.putDescriptor(toid, desc);
    }

    public void removeDescriptor(String sql_name) {
        this.internalConnection.removeDescriptor(sql_name);
    }

    public void removeAllDescriptor() {
        this.internalConnection.removeAllDescriptor();
    }

    public int numberOfDescriptorCacheEntries() {
        return this.internalConnection.numberOfDescriptorCacheEntries();
    }

    public Enumeration descriptorCacheKeys() {
        return this.internalConnection.descriptorCacheKeys();
    }

    public void getOracleTypeADT(OracleTypeADT otype) throws SQLException {
        this.internalConnection.getOracleTypeADT(otype);
    }

    public short getDbCsId() throws SQLException {
        return this.internalConnection.getDbCsId();
    }

    public short getJdbcCsId() throws SQLException {
        return this.internalConnection.getJdbcCsId();
    }

    public short getNCharSet() {
        return this.internalConnection.getNCharSet();
    }

    public ResultSet newArrayDataResultSet(Datum[] data, long index, int count, Map map)
            throws SQLException {
        return this.internalConnection.newArrayDataResultSet(data, index, count, map);
    }

    public ResultSet newArrayDataResultSet(ARRAY array, long index, int count, Map map)
            throws SQLException {
        return this.internalConnection.newArrayDataResultSet(array, index, count, map);
    }

    public ResultSet newArrayLocatorResultSet(ArrayDescriptor desc, byte[] locator, long index,
            int count, Map map) throws SQLException {
        return this.internalConnection.newArrayLocatorResultSet(desc, locator, index, count, map);
    }

    public ResultSetMetaData newStructMetaData(StructDescriptor desc) throws SQLException {
        return this.internalConnection.newStructMetaData(desc);
    }

    public void getForm(OracleTypeADT otypeADT, OracleTypeCLOB otype, int attrIndex)
            throws SQLException {
        this.internalConnection.getForm(otypeADT, otype, attrIndex);
    }

    public int CHARBytesToJavaChars(byte[] bytes, int nbytes, char[] chars) throws SQLException {
        return this.internalConnection.CHARBytesToJavaChars(bytes, nbytes, chars);
    }

    public int NCHARBytesToJavaChars(byte[] bytes, int nbytes, char[] chars) throws SQLException {
        return this.internalConnection.NCHARBytesToJavaChars(bytes, nbytes, chars);
    }

    public boolean IsNCharFixedWith() {
        return this.internalConnection.IsNCharFixedWith();
    }

    public short getDriverCharSet() {
        return this.internalConnection.getDriverCharSet();
    }

    public int getC2SNlsRatio() {
        return this.internalConnection.getC2SNlsRatio();
    }

    public int getMaxCharSize() throws SQLException {
        return this.internalConnection.getMaxCharSize();
    }

    public int getMaxCharbyteSize() {
        return this.internalConnection.getMaxCharbyteSize();
    }

    public int getMaxNCharbyteSize() {
        return this.internalConnection.getMaxNCharbyteSize();
    }

    public boolean isCharSetMultibyte(short charSet) {
        return this.internalConnection.isCharSetMultibyte(charSet);
    }

    public int javaCharsToCHARBytes(char[] chars, int nchars, byte[] bytes) throws SQLException {
        return this.internalConnection.javaCharsToCHARBytes(chars, nchars, bytes);
    }

    public int javaCharsToNCHARBytes(char[] chars, int nchars, byte[] bytes) throws SQLException {
        return this.internalConnection.javaCharsToNCHARBytes(chars, nchars, bytes);
    }

    public int getStmtCacheSize() {
        return this.internalConnection.getStmtCacheSize();
    }

    public int getStatementCacheSize() throws SQLException {
        return this.internalConnection.getStatementCacheSize();
    }

    public boolean getImplicitCachingEnabled() throws SQLException {
        return this.internalConnection.getImplicitCachingEnabled();
    }

    public boolean getExplicitCachingEnabled() throws SQLException {
        return this.internalConnection.getExplicitCachingEnabled();
    }

    public void purgeImplicitCache() throws SQLException {
        this.internalConnection.purgeImplicitCache();
    }

    public void purgeExplicitCache() throws SQLException {
        this.internalConnection.purgeExplicitCache();
    }

    public PreparedStatement getStatementWithKey(String key) throws SQLException {
        return this.internalConnection.getStatementWithKey(key);
    }

    public CallableStatement getCallWithKey(String key) throws SQLException {
        return this.internalConnection.getCallWithKey(key);
    }

    public boolean isStatementCacheInitialized() {
        return this.internalConnection.isStatementCacheInitialized();
    }

    public void setTypeMap(Map map) {
        this.internalConnection.setTypeMap(map);
    }

    public String getProtocolType() {
        return this.internalConnection.getProtocolType();
    }

    public void setTxnMode(int mode) {
        this.internalConnection.setTxnMode(mode);
    }

    public int getTxnMode() {
        return this.internalConnection.getTxnMode();
    }

    public int getHeapAllocSize() throws SQLException {
        return this.internalConnection.getHeapAllocSize();
    }

    public int getOCIEnvHeapAllocSize() throws SQLException {
        return this.internalConnection.getOCIEnvHeapAllocSize();
    }

    public CLOB createClob(byte[] locator_bytes) throws SQLException {
        return this.internalConnection.createClob(locator_bytes);
    }

    public CLOB createClob(byte[] locator_bytes, short csform) throws SQLException {
        return this.internalConnection.createClob(locator_bytes, csform);
    }

    public BLOB createBlob(byte[] locator_bytes) throws SQLException {
        return this.internalConnection.createBlob(locator_bytes);
    }

    public BFILE createBfile(byte[] locator_bytes) throws SQLException {
        return this.internalConnection.createBfile(locator_bytes);
    }

    public boolean isDescriptorSharable(oracle.jdbc.internal.OracleConnection conn)
            throws SQLException {
        return this.internalConnection.isDescriptorSharable(conn);
    }

    public OracleStatement refCursorCursorToStatement(int cursorNumber) throws SQLException {
        return this.internalConnection.refCursorCursorToStatement(cursorNumber);
    }

    public long getTdoCState(String schemaName, String typeName) throws SQLException {
        return this.internalConnection.getTdoCState(schemaName, typeName);
    }

    public Datum toDatum(CustomDatum inObject) throws SQLException {
        return this.internalConnection.toDatum(inObject);
    }

    public XAResource getXAResource() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LogicalConnection.getXAResource()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.pooledConnection.getXAResource();
    }

    public void setApplicationContext(String nameSpace, String attribute, String value)
            throws SQLException {
        this.internalConnection.setApplicationContext(nameSpace, attribute, value);
    }

    public void clearAllApplicationContext(String nameSpace) throws SQLException {
        this.internalConnection.clearAllApplicationContext(nameSpace);
    }

    public boolean isV8Compatible() throws SQLException {
        return this.internalConnection.isV8Compatible();
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.LogicalConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.LogicalConnection JD-Core Version: 0.6.0
 */