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
import java.util.logging.Level;
import java.util.logging.Logger;
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

abstract class PhysicalConnection extends OracleConnection {
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
    public static boolean TRACE;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    PhysicalConnection() {
    }

    PhysicalConnection(String ur, String us, String p, String db, Properties info,
            OracleDriverExtension ext) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "PhysicalConnection.PhysicalConnection(ur=\"" + ur
                            + "\", us=\"" + us + "\", p=\"" + p + "\", db=\"" + db + "\", info)",
                         this);

            OracleLog.recursiveTrace = false;
        }

        this.driverExtension = ext;

        String prot = null;

        if (info != null) {
            prot = (String) info.get("protocol");

            String processEscapesProp = info.getProperty("processEscapes");

            if ((processEscapesProp != null) && (processEscapesProp.equalsIgnoreCase("false"))) {
                this.processEscapes = false;
            }

            String rmId = (String) info.get("RessourceManagerId");

            if (rmId != null) {
                this.ressourceManagerId = rmId;
            }
            this.connectionProperties = ((Properties) info.clone());

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINEST,
                                           "PhysicalConnection.PhysicalConnection() : connectionProperties="
                                                   + this.connectionProperties, this);

                OracleLog.recursiveTrace = false;
            }

        }

        initialize(ur, us, prot, null, null, null, db);
        initializePassword(p);

        this.logicalConnectionAttached = null;
        try {
            needLine();

            logon();

            boolean default_auto_commit = true;

            if (info != null) {
                String prop_str = info.getProperty("autoCommit");

                if ((prop_str != null) && (prop_str.equalsIgnoreCase("false"))) {
                    default_auto_commit = false;

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.FINER,
                                                   "User overrode autoCommit to false", this);

                        OracleLog.recursiveTrace = false;
                    }

                }

                prop_str = this.connectionProperties.getProperty("wellBehavedStatementReuse");

                if ((prop_str != null) && (prop_str.equalsIgnoreCase("true"))) {
                    this.wellBehavedStatementReuse = true;
                }
            }
            setAutoCommit(default_auto_commit);

            if (getVersionNumber() >= 10000) {
                this.minVcsBindSize = 4001;
                this.maxRawBytesSql = 2000;
                this.maxRawBytesPlsql = 32512;
                this.maxVcsCharsSql = 32766;
                this.maxVcsBytesPlsql = 32512;
            } else if (getVersionNumber() >= 9200) {
                this.minVcsBindSize = 4001;
                this.maxRawBytesSql = 2000;
                this.maxRawBytesPlsql = 32512;
                this.maxVcsCharsSql = 32766;
                this.maxVcsBytesPlsql = 32512;
            } else {
                this.minVcsBindSize = 4001;
                this.maxRawBytesSql = 2000;
                this.maxRawBytesPlsql = 2000;
                this.maxVcsCharsSql = 4000;
                this.maxVcsBytesPlsql = 4000;
            }

            String v8compat_type = info != null ? info.getProperty("oracle.jdbc.V8Compatible")
                    : null;

            if (v8compat_type == null) {
                v8compat_type = OracleDriver.getSystemPropertyV8Compatible();
            }

            if (v8compat_type != null) {
                this.v8Compatible = v8compat_type.equalsIgnoreCase("true");
            }
            String str_prop = info != null ? info.getProperty("oracle.jdbc.StreamChunkSize") : null;

            if (str_prop != null) {
                this.streamChunkSize = Math.max(4096, Integer.parseInt(str_prop));
            }

            str_prop = info != null ? info
                    .getProperty("oracle.jdbc.internal.permitBindDateDefineTimestampMismatch")
                    : null;

            if ((str_prop != null) && (str_prop.equalsIgnoreCase("true"))) {
                this.looseTimestampDateCheck = true;
            }

            str_prop = info != null ? info
                    .getProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache") : null;
            this.isMemoryFreedOnEnteringCache = ((str_prop != null) && (str_prop
                    .equalsIgnoreCase("true")));

            initializeSetCHARCharSetObjs();

            this.spawnNewThreadToCancel = "true".equalsIgnoreCase(info
                    .getProperty("oracle.jdbc.spawnNewThreadToCancel"));
        } catch (SQLException ea) {
            try {
                logoff();
            } catch (SQLException eb) {
            }
            throw ea;
        }

        this.txnMode = 0;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "PhysicalConnection.PhysicalConnection(ur, us, p, db, info):return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    abstract void initializePassword(String paramString) throws SQLException;

    public Properties getProperties() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "PhysicalConnection.getProperties(): no return trace", this);

            OracleLog.recursiveTrace = false;
        }

        return OracleDataSource.filterConnectionProperties(this.connectionProperties);
    }

    /** @deprecated */
    public synchronized Connection _getPC() {
        return null;
    }

    public synchronized oracle.jdbc.internal.OracleConnection getPhysicalConnection() {
        return this;
    }

    public synchronized boolean isLogicalConnection() {
        return false;
    }

    void initialize(String ur, String us, String prot, Hashtable dc, Map pcmap, Map omap, String db)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.initialize(ur=\"" + ur
                    + "\", us=\"" + us + "\", access)", this);

            OracleLog.recursiveTrace = false;
        }

        this.clearStatementMetaData = false;
        this.database = db;

        this.url = ur;

        if ((us == null) || (us.startsWith("\""))) {
            this.user = us;
        } else
            this.user = us.toUpperCase();

        this.protocol = prot;

        this.defaultRowPrefetch = DEFAULT_ROW_PREFETCH;
        this.defaultBatch = 1;

        if (dc != null)
            this.descriptorCache = dc;
        else {
            this.descriptorCache = new Hashtable(10);
        }
        this.map = pcmap;

        if (omap != null)
            this.javaObjectMap = omap;
        else {
            this.javaObjectMap = new Hashtable(10);
        }
        this.lifecycle = 1;
        this.txnLevel = 2;

        this.xaWantsError = false;
        this.usingXA = false;

        this.clientIdSet = false;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.initialize(ur, us):return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    void initializeSetCHARCharSetObjs() {
        this.setCHARNCharSetObj = this.conversion.getDriverNCharSetObj();
        this.setCHARCharSetObj = this.conversion.getDriverCharSetObj();
    }

    OracleTimeout getTimeout() throws SQLException {
        if (this.timeout == null) {
            this.timeout = OracleTimeout.newTimeout(this.url);
        }

        return this.timeout;
    }

    public synchronized Statement createStatement() throws SQLException {
        return createStatement(-1, -1);
    }

    public synchronized Statement createStatement(int resultSetType, int resultSetConcurrency)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "PhysicalConnection.createStatement(resultSetType="
                                               + resultSetType + ", resultSetConcurrency="
                                               + resultSetConcurrency + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        OracleStatement stmt = null;

        stmt = this.driverExtension.allocateStatement(this, resultSetType, resultSetConcurrency);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "PhysicalConnection.createStatement(resultSetType, resultSetConcurrency):return",
                         this);

            OracleLog.recursiveTrace = false;
        }

        return stmt;
    }

    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException {
        return prepareStatement(sql, -1, -1);
    }

    /** @deprecated */
    public synchronized PreparedStatement prepareStatementWithKey(String key) throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        if (key == null) {
            return null;
        }
        if (!isStatementCacheInitialized()) {
            DatabaseError.throwSqlException(95);
        }

        PreparedStatement pstmt = null;

        pstmt = (OraclePreparedStatement) this.statementCache.searchExplicitCache(key);

        return pstmt;
    }

    public synchronized PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "PhysicalConnection.prepareStatement(sql)", this);

            OracleLog.recursiveTrace = false;
        }

        if ((sql == null) || (sql == "")) {
            DatabaseError.throwSqlException(104);
        }
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        PreparedStatement pstmt = null;

        if (this.statementCache != null) {
            pstmt = (OraclePreparedStatement) this.statementCache
                    .searchImplicitCache(sql, 1, (resultSetType != -1)
                            || (resultSetConcurrency != -1) ? ResultSetUtil
                            .getRsetTypeCode(resultSetType, resultSetConcurrency) : 1);
        }

        if (pstmt == null) {
            pstmt = this.driverExtension.allocatePreparedStatement(this, sql, resultSetType,
                                                                   resultSetConcurrency);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "PhysicalConnection.prepareStatement(sql):return", this);

            OracleLog.recursiveTrace = false;
        }

        return pstmt;
    }

    public synchronized CallableStatement prepareCall(String sql) throws SQLException {
        return prepareCall(sql, -1, -1);
    }

    public synchronized CallableStatement prepareCall(String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.prepareCall(sql)", this);

            OracleLog.recursiveTrace = false;
        }

        if ((sql == null) || (sql == "")) {
            DatabaseError.throwSqlException(104);
        }
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        CallableStatement cstmt = null;

        if (this.statementCache != null) {
            cstmt = (OracleCallableStatement) this.statementCache
                    .searchImplicitCache(sql, 2, (resultSetType != -1)
                            || (resultSetConcurrency != -1) ? ResultSetUtil
                            .getRsetTypeCode(resultSetType, resultSetConcurrency) : 1);
        }

        if (cstmt == null) {
            cstmt = this.driverExtension.allocateCallableStatement(this, sql, resultSetType,
                                                                   resultSetConcurrency);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.prepareCall(sql):return",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return cstmt;
    }

    public synchronized CallableStatement prepareCallWithKey(String key) throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        if (key == null) {
            return null;
        }
        if (!isStatementCacheInitialized()) {
            DatabaseError.throwSqlException(95);
        }

        CallableStatement cstmt = null;

        cstmt = (OracleCallableStatement) this.statementCache.searchExplicitCache(key);

        return cstmt;
    }

    public String nativeSQL(String sql) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.nativeSQL(sql)", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.sqlObj == null) {
            this.sqlObj = new OracleSql(this.conversion);
            this.sqlObj.isV8Compatible = this.v8Compatible;
        }

        this.sqlObj.initialize(sql);

        String osql = this.sqlObj.getSql(this.processEscapes, this.convertNcharLiterals);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Output SQL: \"" + osql + "\"", this);

            OracleLog.recursiveTrace = false;
        }

        return osql;
    }

    public synchronized void setAutoCommit(boolean autoCommit) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setAutoCommit(autoCommit="
                    + autoCommit + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (autoCommit) {
            disallowGlobalTxnMode(116);
        }
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        needLine();
        doSetAutoCommit(autoCommit);

        this.autoCommitSet = autoCommit;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "PhysicalConnection.setAutoCommit(autoCommit): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean getAutoCommit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getAutoCommit() returned "
                    + this.autoCommitSet, this);

            OracleLog.recursiveTrace = false;
        }

        return this.autoCommitSet;
    }

    public void cancel() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.cancel()", this);

            OracleLog.recursiveTrace = false;
        }

        OracleStatement stmt = this.statements;

        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        while (stmt != null) {
            try {
                stmt.cancel();
            } catch (SQLException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER, "PhysicalConnection.cancel() failed: "
                            + e.getMessage(), this);

                    OracleLog.recursiveTrace = false;
                }

            }

            stmt = stmt.next;
        }
    }

    public synchronized void commit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.commit()", this);

            OracleLog.recursiveTrace = false;
        }

        disallowGlobalTxnMode(114);

        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        OracleStatement s = this.statements;

        while (s != null) {
            if (!s.closed) {
                s.sendBatch();
            }
            s = s.next;
        }

        needLine();
        doCommit();
    }

    public synchronized void rollback() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.rollback()", this);

            OracleLog.recursiveTrace = false;
        }

        disallowGlobalTxnMode(115);

        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        needLine();
        doRollback();
    }

    public synchronized void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.close()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((this.lifecycle == 2) || (this.lifecycle == 4)) {
            return;
        }
        if (this.lifecycle == 1)
            this.lifecycle = 2;

        try {
            if (this.closeCallback != null) {
                this.closeCallback.beforeClose(this, this.privateData);
            }
            closeStatements(true);

            needLine();

            if (this.isProxy) {
                close(1);
            }

            logoff();
            cleanup();

            if (this.timeout != null) {
                this.timeout.close();
            }
            if (this.closeCallback != null)
                this.closeCallback.afterClose(this.privateData);
        } finally {
            this.lifecycle = 4;
        }
    }

    public void closeInternal(boolean putPhysicalConnBackInCache) throws SQLException {
        DatabaseError.throwSqlException(152);
    }

    synchronized void closeLogicalConnection() throws SQLException {
        closeStatements(false);

        if (this.clientIdSet) {
            clearClientIdentifier(this.clientId);
        }
        this.logicalConnectionAttached = null;
    }

    public synchronized void close(Properties cachedConnectionAttributes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.close(cachedConnectionAttributes="
                                               + cachedConnectionAttributes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(152);
    }

    public synchronized void close(int opt) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.close(opt=" + opt + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if ((opt & 0x1000) != 0) {
            close();

            return;
        }

        if (((opt & 0x1) != 0) && (this.isProxy)) {
            closeProxySession();

            this.isProxy = false;
        }
    }

    public void abort() throws SQLException {
        if ((this.lifecycle == 4) || (this.lifecycle == 8)) {
            return;
        }

        this.lifecycle = 8;

        doAbort();
    }

    abstract void doAbort() throws SQLException;

    void closeProxySession() throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public Properties getServerSessionInfo() throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public synchronized void applyConnectionAttributes(Properties cachedConnectionAttributes)
            throws SQLException {
        DatabaseError.throwSqlException(152);
    }

    public synchronized Properties getConnectionAttributes() throws SQLException {
        DatabaseError.throwSqlException(152);

        return null;
    }

    public synchronized Properties getUnMatchedConnectionAttributes() throws SQLException {
        DatabaseError.throwSqlException(152);

        return null;
    }

    public synchronized void setAbandonedTimeoutEnabled(boolean val) throws SQLException {
        DatabaseError.throwSqlException(152);
    }

    public synchronized void registerConnectionCacheCallback(OracleConnectionCacheCallback occc,
            Object userObj, int cbkFlag) throws SQLException {
        DatabaseError.throwSqlException(152);
    }

    public OracleConnectionCacheCallback getConnectionCacheCallbackObj() throws SQLException {
        DatabaseError.throwSqlException(152);

        return null;
    }

    public Object getConnectionCacheCallbackPrivObj() throws SQLException {
        DatabaseError.throwSqlException(152);

        return null;
    }

    public int getConnectionCacheCallbackFlag() throws SQLException {
        DatabaseError.throwSqlException(152);

        return 0;
    }

    public synchronized void setConnectionReleasePriority(int priority) throws SQLException {
        DatabaseError.throwSqlException(152);
    }

    public int getConnectionReleasePriority() throws SQLException {
        DatabaseError.throwSqlException(152);

        return 0;
    }

    public synchronized boolean isClosed() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.isClosed() returned "
                    + (this.lifecycle == 1), this);

            OracleLog.recursiveTrace = false;
        }

        return this.lifecycle != 1;
    }

    public synchronized boolean isProxySession() {
        return this.isProxy;
    }

    public synchronized void openProxySession(int type, Properties prop) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.proxyConnect(...)", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.isProxy) {
            DatabaseError.throwSqlException(149);
        }
        String user = prop.getProperty("PROXY_USER_NAME");
        String passwd = prop.getProperty("PROXY_USER_PASSWORD");
        String distName = prop.getProperty("PROXY_DISTINGUISHED_NAME");

        Object certif = prop.get("PROXY_CERTIFICATE");

        if (type == 1) {
            if ((user == null) && (passwd == null))
                DatabaseError.throwSqlException(150);
        } else if (type == 2) {
            if (distName == null)
                DatabaseError.throwSqlException(150);
        } else if (type == 3) {
            if (certif == null) {
                DatabaseError.throwSqlException(150);
            }
            try {
                byte[] verify = (byte[]) certif;
            } catch (ClassCastException e) {
                DatabaseError.throwSqlException(150);
            }
        } else {
            DatabaseError.throwSqlException(150);
        }

        doProxySession(type, prop);
    }

    void doProxySession(int type, Properties prop) throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    void cleanup() {
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

    public synchronized DatabaseMetaData getMetaData() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getMetaData()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        if (this.databaseMetaData == null) {
            this.databaseMetaData = new OracleDatabaseMetaData(this);
        }
        return this.databaseMetaData;
    }

    public void setReadOnly(boolean value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setReadOnly(readOnly="
                    + value + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.readOnly = value;
    }

    public boolean isReadOnly() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.isReadOnly()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.readOnly;
    }

    public void setCatalog(String catalog) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setCatalog(catalog=\""
                    + catalog + "\")", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public String getCatalog() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getCatalog()", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public synchronized void setTransactionIsolation(int level) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setTransactionIsolation(level=" + level
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        OracleStatement ostmt = (OracleStatement) createStatement();
        try {
            switch (level) {
            case 2:
                ostmt.execute("ALTER SESSION SET ISOLATION_LEVEL = READ COMMITTED");

                this.txnLevel = 2;

                break;
            case 8:
                ostmt.execute("ALTER SESSION SET ISOLATION_LEVEL = SERIALIZABLE");

                this.txnLevel = 8;

                break;
            default:
                DatabaseError.throwSqlException(30);
            }

        } finally {
            ostmt.close();
        }
    }

    public int getTransactionIsolation() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.getTransactionIsolation() returned "
                                               + this.txnLevel, this);

            OracleLog.recursiveTrace = false;
        }

        return this.txnLevel;
    }

    public synchronized void setAutoClose(boolean autoClose) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setAutoClose(autoClose="
                    + autoClose + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!autoClose)
            DatabaseError.throwSqlException(31);
    }

    public boolean getAutoClose() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getAutoClose()", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public SQLWarning getWarnings() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getWarnings()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlWarning;
    }

    public void clearWarnings() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.clearWarnings()", this);

            OracleLog.recursiveTrace = false;
        }

        this.sqlWarning = null;
    }

    public void setWarnings(SQLWarning warn) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.setWarnings(" + warn + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        this.sqlWarning = warn;
    }

    public void setDefaultRowPrefetch(int value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setDefaultRowPrefetch(value=" + value
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (value <= 0) {
            DatabaseError.throwSqlException(20);
        }
        this.defaultRowPrefetch = value;
    }

    public int getDefaultRowPrefetch() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.getDefaultRowPrefetch() returned "
                                               + this.defaultRowPrefetch, this);

            OracleLog.recursiveTrace = false;
        }

        return this.defaultRowPrefetch;
    }

    public synchronized void setDefaultExecuteBatch(int batch) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setDefaultExecuteBatch(batch=" + batch
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (batch <= 0) {
            DatabaseError.throwSqlException(42);
        }
        this.defaultBatch = batch;
    }

    public synchronized int getDefaultExecuteBatch() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.getDefaultExecuteBatch() returned "
                                               + this.defaultBatch, this);

            OracleLog.recursiveTrace = false;
        }

        return this.defaultBatch;
    }

    public synchronized void setRemarksReporting(boolean reportRemarks) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setRemarksReporting(reportRemarks="
                                               + reportRemarks + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.reportRemarks = reportRemarks;
    }

    public synchronized boolean getRemarksReporting() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "PhysicalConnection.getRemarksReporting()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.reportRemarks;
    }

    public void setIncludeSynonyms(boolean synonyms) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setIncludeSynonyms(synonyms=" + synonyms
                                               + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.includeSynonyms = synonyms;
    }

    public synchronized String[] getEndToEndMetrics() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getEndToEndMetrics()", this);

            OracleLog.recursiveTrace = false;
        }
        String[] result;
        if (this.endToEndValues == null) {
            result = null;
        } else {
            result = new String[this.endToEndValues.length];

            System.arraycopy(this.endToEndValues, 0, result, 0, this.endToEndValues.length);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getEndToEndMetrics:return:"
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public short getEndToEndECIDSequenceNumber() throws SQLException {
        return this.endToEndECIDSequenceNumber;
    }

    public synchronized void setEndToEndMetrics(String[] metrics, short sequenceNumber)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setEndToEndMetrics("
                    + metrics + ", " + sequenceNumber + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.useDMSForEndToEnd) {
            String[] copyMetrics = new String[metrics.length];

            System.arraycopy(metrics, 0, copyMetrics, 0, metrics.length);
            setEndToEndMetricsInternal(copyMetrics, sequenceNumber);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.getEndToEndMetrics:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    void setEndToEndMetricsInternal(String[] metrics, short sequenceNumber) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.setEndToEndMetricsInternal("
                    + metrics + ", " + sequenceNumber + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (metrics != this.endToEndValues) {
            if (metrics.length != 4) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger
                            .log(
                                 Level.SEVERE,
                                 "PhysicalConnection.setEndToEndMetricsInternal: End To End metric array wrong size",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(156);
            }

            for (int i = 0; i < 4; i++) {
                String s = metrics[i];

                if ((s == null) || (s.length() <= endToEndMaxLength[i]))
                    continue;
                DatabaseError.throwSqlException(159, s);
            }

            if (this.endToEndValues != null) {
                for (int i = 0; i < 4; i++) {
                    String s = metrics[i];

                    if (((s != null) || (this.endToEndValues[i] == null))
                            && ((s == null) || (s.equals(this.endToEndValues[i])))) {
                        continue;
                    }
                    this.endToEndHasChanged[i] = true;
                    this.endToEndAnyChanged = true;
                }

                this.endToEndHasChanged[0] |= this.endToEndHasChanged[3];
            } else {
                for (int i = 0; i < 4; i++) {
                    this.endToEndHasChanged[i] = true;
                }

                this.endToEndAnyChanged = true;
            }

            this.endToEndValues = metrics;
        }

        this.endToEndECIDSequenceNumber = sequenceNumber;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "PhysicalConnection.setEndToEndMetricsInternal:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void updateEndToEndMetrics() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.computeEndToEndMetrics()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.updateEndToEndMetrics:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean getIncludeSynonyms() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.getIncludeSynonyms() returned "
                                               + this.includeSynonyms, this);

            OracleLog.recursiveTrace = false;
        }

        return this.includeSynonyms;
    }

    public void setRestrictGetTables(boolean restrict) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setRestrictGetTables(restrict="
                                               + restrict + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.restrictGetTables = restrict;
    }

    public boolean getRestrictGetTables() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.getRestrictGetTables() returned "
                                               + this.restrictGetTables, this);

            OracleLog.recursiveTrace = false;
        }

        return this.restrictGetTables;
    }

    public void setDefaultFixedString(boolean fixedString) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setDefaultFixedString(fixedString="
                                               + fixedString, this);

            OracleLog.recursiveTrace = false;
        }

        this.defaultFixedString = fixedString;
    }

    public void setDefaultNChar(boolean defaultnchar) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setDefaultNChar(defaultnchar="
                                               + defaultnchar, this);

            OracleLog.recursiveTrace = false;
        }

        this.defaultNChar = defaultnchar;
    }

    public boolean getDefaultFixedString() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.getDefaultFixedString() returning "
                                               + this.defaultFixedString, this);

            OracleLog.recursiveTrace = false;
        }

        return this.defaultFixedString;
    }

    public int getNlsRatio() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.getNlsRatio(): returned 1",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return 1;
    }

    public int getC2SNlsRatio() {
        return 1;
    }

    synchronized void addStatement(OracleStatement os) {
        if (os.next != null) {
            throw new Error("add_statement called twice on " + os);
        }
        os.next = this.statements;

        if (this.statements != null) {
            this.statements.prev = os;
        }
        this.statements = os;
    }

    synchronized void removeStatement(OracleStatement os) {
        OracleStatement p = os.prev;
        OracleStatement n = os.next;

        if (p == null) {
            if (this.statements != os) {
                return;
            }
            this.statements = n;
        } else {
            p.next = n;
        }
        if (n != null) {
            n.prev = p;
        }
        os.next = null;
        os.prev = null;
    }

    synchronized void closeStatements(boolean closeStmtCache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.close_statements()", this);

            OracleLog.recursiveTrace = false;
        }

        if ((closeStmtCache) && (isStatementCacheInitialized())) {
            this.statementCache.close();

            this.statementCache = null;
            this.clearStatementMetaData = true;
        }

        OracleStatement s = this.statements;

        while (s != null) {
            OracleStatement n = s.nextChild;

            if (s.serverCursor) {
                s.close();
                removeStatement(s);
            }

            s = n;
        }

        s = this.statements;

        while (s != null) {
            OracleStatement n = s.next;

            s.close();
            removeStatement(s);

            s = n;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.close_statements():return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized void needLine() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "PhysicalConnection.needLine()--no return", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementHoldingLine != null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "Freeing line from "
                        + this.statementHoldingLine, this);

                OracleLog.recursiveTrace = false;
            }

            this.statementHoldingLine.freeLine();
        }
    }

    synchronized void holdLine(oracle.jdbc.internal.OracleStatement stmt) {
        holdLine((OracleStatement) stmt);
    }

    synchronized void holdLine(OracleStatement stmt) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.holdLine(" + stmt
                    + ")--no return", this);

            OracleLog.recursiveTrace = false;
        }

        this.statementHoldingLine = stmt;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "Holding line for " + this.statementHoldingLine, this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized void releaseLine() {
        releaseLineForCancel();
    }

    void releaseLineForCancel() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "PhysicalConnection.releaseLineForCancle(): Releasing line from "
                                               + this.statementHoldingLine, this);

            OracleLog.recursiveTrace = false;
        }

        this.statementHoldingLine = null;
    }

    public synchronized void startup(String startup_str, int mode) throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        DatabaseError.throwSqlException(23);
    }

    public synchronized void shutdown(int mode) throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        DatabaseError.throwSqlException(23);
    }

    public synchronized void archive(int mode, int aseq, String acstext) throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        DatabaseError.throwSqlException(23);
    }

    public synchronized void registerSQLType(String sql_name, String java_class_name)
            throws SQLException {
        if ((sql_name == null) || (java_class_name == null)) {
            DatabaseError.throwSqlException(68);
        }
        try {
            registerSQLType(sql_name, Class.forName(java_class_name));
        } catch (ClassNotFoundException e) {
            DatabaseError.throwSqlException(1, "Class not found: " + java_class_name);
        }
    }

    public synchronized void registerSQLType(String sql_name, Class java_class) throws SQLException {
        if ((sql_name == null) || (java_class == null)) {
            DatabaseError.throwSqlException(68);
        }
        ensureClassMapExists();

        this.map.put(sql_name, java_class);
        this.map.put(java_class.getName(), sql_name);
    }

    void ensureClassMapExists() {
        if (this.map == null)
            initializeClassMap();
    }

    void initializeClassMap() {
        Hashtable aMap = new Hashtable(10);

        addDefaultClassMapEntriesTo(aMap);

        this.map = aMap;
    }

    public synchronized String getSQLType(Object obj) throws SQLException {
        if ((obj != null) && (this.map != null)) {
            String java_class_name = obj.getClass().getName();

            return (String) this.map.get(java_class_name);
        }

        return null;
    }

    public synchronized Object getJavaObject(String sql_name) throws SQLException {
        Object obj = null;
        try {
            if ((sql_name != null) && (this.map != null)) {
                Class java_class = (Class) this.map.get(sql_name);

                obj = java_class.newInstance();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return obj;
    }

    public synchronized void putDescriptor(String sql_name, Object desc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.putDescriptor (" + sql_name
                    + ", " + desc + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((sql_name != null) && (desc != null)) {
            if (this.descriptorCache == null) {
                this.descriptorCache = new Hashtable(10);
            }
            ((TypeDescriptor) desc).fixupConnection(this);
            this.descriptorCache.put(sql_name, desc);
        } else {
            DatabaseError.throwSqlException(68);
        }
    }

    public synchronized Object getDescriptor(String sql_name) {
        Object desc = null;

        if ((sql_name != null) && (this.descriptorCache != null)) {
            desc = this.descriptorCache.get(sql_name);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.getDescriptor (" + sql_name
                    + ") return " + desc);

            OracleLog.recursiveTrace = false;
        }

        return desc;
    }

    /** @deprecated */
    public synchronized void removeDecriptor(String sql_name) {
        removeDescriptor(sql_name);
    }

    public synchronized void removeDescriptor(String sql_name) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.removeDescriptor (" + sql_name
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((sql_name != null) && (this.descriptorCache != null))
            this.descriptorCache.remove(sql_name);
    }

    public synchronized void removeAllDescriptor() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.removeAllDescriptor ()");

            OracleLog.recursiveTrace = false;
        }

        if (this.descriptorCache != null)
            this.descriptorCache.clear();
    }

    public int numberOfDescriptorCacheEntries() {
        if (this.descriptorCache != null) {
            return this.descriptorCache.size();
        }
        return 0;
    }

    public Enumeration descriptorCacheKeys() {
        if (this.descriptorCache != null) {
            return this.descriptorCache.keys();
        }
        return null;
    }

    public synchronized void putDescriptor(byte[] toid, Object desc) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.putDescriptor (" + toid + ", "
                    + desc + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((toid != null) && (desc != null)) {
            if (this.descriptorCache == null) {
                this.descriptorCache = new Hashtable(10);
            }
            this.descriptorCache.put(new ByteArrayKey(toid), desc);
        } else {
            DatabaseError.throwSqlException(68);
        }
    }

    public synchronized Object getDescriptor(byte[] toid) {
        Object desc = null;

        if ((toid != null) && (this.descriptorCache != null)) {
            desc = this.descriptorCache.get(new ByteArrayKey(toid));
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.getDescriptor (" + toid
                    + ") return " + desc);

            OracleLog.recursiveTrace = false;
        }

        return desc;
    }

    public synchronized void removeDecriptor(byte[] toid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "PhysicalConnection.removeDescriptor (" + toid
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        if ((toid != null) && (this.descriptorCache != null))
            this.descriptorCache.remove(new ByteArrayKey(toid));
    }

    public short getJdbcCsId() throws SQLException {
        if (this.conversion == null) {
            DatabaseError.throwSqlException(65);
        }

        return this.conversion.getClientCharSet();
    }

    public short getDbCsId() throws SQLException {
        if (this.conversion == null) {
            DatabaseError.throwSqlException(65);
        }

        return this.conversion.getServerCharSetId();
    }

    public short getNCsId() throws SQLException {
        if (this.conversion == null) {
            DatabaseError.throwSqlException(65);
        }

        return this.conversion.getNCharSetId();
    }

    public short getStructAttrCsId() throws SQLException {
        return getDbCsId();
    }

    public short getStructAttrNCsId() throws SQLException {
        return getNCsId();
    }

    public synchronized Map getTypeMap() {
        ensureClassMapExists();

        return this.map;
    }

    public synchronized void setTypeMap(Map map) {
        addDefaultClassMapEntriesTo(map);

        this.map = map;
    }

    void addDefaultClassMapEntriesTo(Map map) {
        if (map != null) {
            addClassMapEntry("SYS.XMLTYPE", "oracle.xdb.XMLTypeFactory", map);
        }
    }

    void addClassMapEntry(String typeName, String className, Map theMap) {
        if (containsKey(theMap, typeName)) {
            return;
        }
        try {
            Class clazz = safelyGetClassForName(className);

            theMap.put(typeName, clazz);
        } catch (ClassNotFoundException ex) {
        }
    }

    public synchronized void setUsingXAFlag(boolean value) {
        this.usingXA = value;
    }

    public synchronized boolean getUsingXAFlag() {
        return this.usingXA;
    }

    public synchronized void setXAErrorFlag(boolean value) {
        this.xaWantsError = value;
    }

    public synchronized boolean getXAErrorFlag() {
        return this.xaWantsError;
    }

    void initUserName() throws SQLException {
        if (this.user != null) {
            return;
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = createStatement();
            ((OracleStatement) stmt).setRowPrefetch(1);
            rs = stmt.executeQuery("SELECT USER FROM DUAL");
            if (rs.next())
                this.user = rs.getString(1);
        } finally {
            if (rs != null)
                rs.close();
            if (stmt != null)
                stmt.close();
            rs = null;
            stmt = null;
        }
    }

    public synchronized String getUserName() throws SQLException {
        if (this.user == null) {
            initUserName();
        }
        return this.user;
    }

    public synchronized void setStartTime(long startTime) throws SQLException {
        DatabaseError.throwSqlException(152);
    }

    public synchronized long getStartTime() throws SQLException {
        DatabaseError.throwSqlException(152);

        return -1L;
    }

    void registerHeartbeat() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.registerHeartbeat()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.logicalConnectionAttached != null)
            this.logicalConnectionAttached.registerHeartbeat();
    }

    public int getHeartbeatNoChangeCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "PhysicalConnection.getHeartbeatNoChangeCount()", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(152);

        return -1;
    }

    public synchronized byte[] getFDO(boolean init) throws SQLException {
        if ((this.fdo == null) && (init)) {
            CallableStatement cstmt = null;
            try {
                cstmt = prepareCall("begin ? := dbms_pickler.get_format (?); end;");

                cstmt.registerOutParameter(1, 2);
                cstmt.registerOutParameter(2, -4);
                cstmt.execute();

                this.fdo = cstmt.getBytes(2);
            } finally {
                if (cstmt != null) {
                    cstmt.close();
                }
                cstmt = null;
            }
        }

        return this.fdo;
    }

    public synchronized void setFDO(byte[] fdo) throws SQLException {
        this.fdo = fdo;
    }

    public synchronized boolean getBigEndian() throws SQLException {
        if (this.bigEndian == null) {
            int[] ub1fdo = Util.toJavaUnsignedBytes(getFDO(true));

            int kopfdo_auxinfo = ub1fdo[(6 + ub1fdo[5] + ub1fdo[6] + 5)];

            int offset = (byte) (kopfdo_auxinfo & 0x10);

            if (offset < 0) {
                offset += 256;
            }
            if (offset > 0)
                this.bigEndian = new Boolean(true);
            else {
                this.bigEndian = new Boolean(false);
            }
        }
        return this.bigEndian.booleanValue();
    }

    public void setHoldability(int holdability) throws SQLException {
    }

    public int getHoldability() throws SQLException {
        return 1;
    }

    public synchronized Savepoint setSavepoint() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setSavepoint()", this);

            OracleLog.recursiveTrace = false;
        }

        return oracleSetSavepoint();
    }

    public synchronized Savepoint setSavepoint(String name) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.setSavepoint(name = " + name
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        return oracleSetSavepoint(name);
    }

    public synchronized void rollback(Savepoint savepoint) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.rollback(savepoint = "
                    + savepoint + ")", this);

            OracleLog.recursiveTrace = false;
        }

        disallowGlobalTxnMode(122);

        if (this.autoCommitSet) {
            DatabaseError.throwSqlException(121);
        }

        if ((this.savepointStatement == null)
                || (((OracleStatement) this.savepointStatement).closed)) {
            this.savepointStatement = createStatement();
        }

        String _svptName = null;
        try {
            _svptName = savepoint.getSavepointName();
        } catch (SQLException exc) {
            _svptName = "ORACLE_SVPT_" + savepoint.getSavepointId();
        }

        this.savepointStatement.executeUpdate("ROLLBACK TO " + _svptName);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.rollback(savepoint): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void releaseSavepoint(Savepoint savepoint) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType,
            int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        return prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
            throws SQLException {
        if ((autoGeneratedKeys == 2) || (!AutoKeyInfo.isInsertSqlStmt(sql))) {
            return prepareStatement(sql);
        }
        if (autoGeneratedKeys != 1) {
            DatabaseError.throwSqlException(68);
        }
        AutoKeyInfo info = new AutoKeyInfo(sql);

        String newSql = info.getNewSql();
        OraclePreparedStatement pstmt = (OraclePreparedStatement) prepareStatement(newSql);

        pstmt.isAutoGeneratedKey = true;
        pstmt.autoKeyInfo = info;
        pstmt.registerReturnParamsForAutoKey();
        return pstmt;
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        if (!AutoKeyInfo.isInsertSqlStmt(sql))
            return prepareStatement(sql);

        if ((columnIndexes == null) || (columnIndexes.length == 0)) {
            DatabaseError.throwSqlException(68);
        }
        AutoKeyInfo info = new AutoKeyInfo(sql, columnIndexes);

        doDescribeTable(info);

        String newSql = info.getNewSql();
        OraclePreparedStatement pstmt = (OraclePreparedStatement) prepareStatement(newSql);

        pstmt.isAutoGeneratedKey = true;
        pstmt.autoKeyInfo = info;
        pstmt.registerReturnParamsForAutoKey();
        return pstmt;
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        if (!AutoKeyInfo.isInsertSqlStmt(sql))
            return prepareStatement(sql);

        if ((columnNames == null) || (columnNames.length == 0)) {
            DatabaseError.throwSqlException(68);
        }
        AutoKeyInfo info = new AutoKeyInfo(sql, columnNames);

        doDescribeTable(info);

        String newSql = info.getNewSql();
        OraclePreparedStatement pstmt = (OraclePreparedStatement) prepareStatement(newSql);

        pstmt.isAutoGeneratedKey = true;
        pstmt.autoKeyInfo = info;
        pstmt.registerReturnParamsForAutoKey();
        return pstmt;
    }

    public synchronized oracle.jdbc.OracleSavepoint oracleSetSavepoint() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.oracleSetSavepoint()", this);

            OracleLog.recursiveTrace = false;
        }

        disallowGlobalTxnMode(117);

        if (this.autoCommitSet) {
            DatabaseError.throwSqlException(120);
        }

        if ((this.savepointStatement == null)
                || (((OracleStatement) this.savepointStatement).closed)) {
            this.savepointStatement = createStatement();
        }

        OracleSavepoint _osvpt = new OracleSavepoint();

        String _svptSqlString = "SAVEPOINT ORACLE_SVPT_" + _osvpt.getSavepointId();

        this.savepointStatement.executeUpdate(_svptSqlString);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "PhysicalConnection.oracleSetSavepoint(): return: " + _osvpt,
                         this);

            OracleLog.recursiveTrace = false;
        }

        return _osvpt;
    }

    public synchronized oracle.jdbc.OracleSavepoint oracleSetSavepoint(String name)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.oracleSetSavepoint(name = "
                    + name + ")", this);

            OracleLog.recursiveTrace = false;
        }

        disallowGlobalTxnMode(117);

        if (this.autoCommitSet) {
            DatabaseError.throwSqlException(120);
        }

        if ((this.savepointStatement == null)
                || (((OracleStatement) this.savepointStatement).closed)) {
            this.savepointStatement = createStatement();
        }

        OracleSavepoint _osvpt = new OracleSavepoint(name);

        String _svptSqlString = "SAVEPOINT " + _osvpt.getSavepointName();

        this.savepointStatement.executeUpdate(_svptSqlString);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.oracleSetSavepoint(name): returns: "
                                               + _osvpt, this);

            OracleLog.recursiveTrace = false;
        }

        return _osvpt;
    }

    public synchronized void oracleRollback(oracle.jdbc.OracleSavepoint savepoint)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "PhysicalConnection.oracleRollback(savepoint = "
                    + savepoint + ")", this);

            OracleLog.recursiveTrace = false;
        }

        disallowGlobalTxnMode(115);

        if (this.autoCommitSet) {
            DatabaseError.throwSqlException(121);
        }

        if ((this.savepointStatement == null)
                || (((OracleStatement) this.savepointStatement).closed)) {
            this.savepointStatement = createStatement();
        }

        String _svptName = null;
        try {
            _svptName = savepoint.getSavepointName();
        } catch (SQLException exc) {
            _svptName = "ORACLE_SVPT_" + savepoint.getSavepointId();
        }

        this.savepointStatement.executeUpdate("ROLLBACK TO " + _svptName);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.INFO, "PhysicalConnection.oracleRollback(savepoint): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void oracleReleaseSavepoint(oracle.jdbc.OracleSavepoint savepoint)
            throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
    }

    void disallowGlobalTxnMode(int errorCode) throws SQLException {
        if (this.txnMode == 1)
            DatabaseError.throwSqlException(errorCode);
    }

    public void setTxnMode(int mode) {
        this.txnMode = mode;
    }

    public int getTxnMode() {
        return this.txnMode;
    }

    public synchronized Object getClientData(Object key) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.getClientData(key)", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.clientData == null) {
            return null;
        }

        return this.clientData.get(key);
    }

    public synchronized Object setClientData(Object key, Object value) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.setClientData(key, value)",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (this.clientData == null) {
            this.clientData = new Hashtable();
        }

        return this.clientData.put(key, value);
    }

    public synchronized Object removeClientData(Object key) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "PhysicalConnection.removeClientData(key)", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.clientData == null) {
            return null;
        }

        return this.clientData.remove(key);
    }

    public BlobDBAccess createBlobDBAccess() throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public ClobDBAccess createClobDBAccess() throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public BfileDBAccess createBfileDBAccess() throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public void printState() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.printState()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            short jdbc_cs_id = getJdbcCsId();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "Jdbc character set id is   " + jdbc_cs_id,
                                           this);

                OracleLog.recursiveTrace = false;
            }

            short db_cs_id = getDbCsId();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "Db character set id is     " + db_cs_id,
                                           this);

                OracleLog.recursiveTrace = false;
            }

            short struct_cs_id = getStructAttrCsId();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "STRUCT character set id is "
                        + struct_cs_id, this);

                OracleLog.recursiveTrace = false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getProtocolType() {
        return this.protocol;
    }

    public String getURL() {
        return this.url;
    }

    /** @deprecated */
    public synchronized void setStmtCacheSize(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "setStmtCacheSize(" + size + ") (deprecated)",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        setStatementCacheSize(size);
        setImplicitCachingEnabled(true);
        setExplicitCachingEnabled(true);
    }

    /** @deprecated */
    public synchronized void setStmtCacheSize(int size, boolean clearMetaData) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "setStmtCacheSize(" + size + ", "
                    + clearMetaData + ") (deprecated)", this);

            OracleLog.recursiveTrace = false;
        }

        setStatementCacheSize(size);
        setImplicitCachingEnabled(true);

        setExplicitCachingEnabled(true);

        this.clearStatementMetaData = clearMetaData;
    }

    /** @deprecated */
    public synchronized int getStmtCacheSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getStmtCacheSize() (deprecated)", this);

            OracleLog.recursiveTrace = false;
        }

        int returnValue = 0;
        try {
            returnValue = getStatementCacheSize();
        } catch (SQLException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "getStmtCacheSize -- exception occured calling getStatementCacheSize  "
                                                   + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

        }

        if (returnValue == -1) {
            returnValue = 0;
        }

        return returnValue;
    }

    public synchronized void setStatementCacheSize(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "setStatementCacheSize(" + size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null) {
            this.statementCache = new LRUStatementCache(size);
        } else {
            this.statementCache.resize(size);
        }
    }

    public synchronized int getStatementCacheSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getStatementCacheSize()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null) {
            return -1;
        }
        return this.statementCache.getCacheSize();
    }

    public synchronized void setImplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "setImplicitCachingEnabled(" + cache + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null) {
            this.statementCache = new LRUStatementCache(0);
        }

        this.statementCache.setImplicitCachingEnabled(cache);
    }

    public synchronized boolean getImplicitCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getImplicitCachingEnabled()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null) {
            return false;
        }
        return this.statementCache.getImplicitCachingEnabled();
    }

    public synchronized void setExplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "setExplicitCachingEnabled(" + cache + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null) {
            this.statementCache = new LRUStatementCache(0);
        }

        this.statementCache.setExplicitCachingEnabled(cache);
    }

    public synchronized boolean getExplicitCachingEnabled() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getExplicitCachingEnabled()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null) {
            return false;
        }
        return this.statementCache.getExplicitCachingEnabled();
    }

    public synchronized void purgeImplicitCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "purgeImplicitCache()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache != null)
            this.statementCache.purgeImplicitCache();
    }

    public synchronized void purgeExplicitCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "purgeExplicitCache()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache != null)
            this.statementCache.purgeExplicitCache();
    }

    public synchronized PreparedStatement getStatementWithKey(String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getStatementWithKey()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache != null) {
            OracleStatement os = this.statementCache.searchExplicitCache(key);

            if ((os == null) || (os.statementType == 1)) {
                return (PreparedStatement) os;
            }

            DatabaseError.throwSqlException(125);

            return null;
        }

        return null;
    }

    public synchronized CallableStatement getCallWithKey(String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getCallWithKey()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache != null) {
            OracleStatement os = this.statementCache.searchExplicitCache(key);

            if ((os == null) || (os.statementType == 2)) {
                return (CallableStatement) os;
            }

            DatabaseError.throwSqlException(125);

            return null;
        }

        return null;
    }

    public synchronized void cacheImplicitStatement(OraclePreparedStatement stmt, String sql,
            int statementType, int scrollType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "cacheImplicitStatement()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null)
            DatabaseError.throwSqlException(95);
        else
            this.statementCache.addToImplicitCache(stmt, sql, statementType, scrollType);
    }

    public synchronized void cacheExplicitStatement(OraclePreparedStatement stmt, String key)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "cacheExplicitStatement(" + key + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.statementCache == null)
            DatabaseError.throwSqlException(95);
        else
            this.statementCache.addToExplicitCache(stmt, key);
    }

    public synchronized boolean isStatementCacheInitialized() {
        if (this.statementCache == null) {
            return false;
        }
        return this.statementCache.getCacheSize() != 0;
    }

    public void setDefaultAutoRefetch(boolean autoRefetch) throws SQLException {
        this.defaultAutoRefetch = autoRefetch;
    }

    public boolean getDefaultAutoRefetch() throws SQLException {
        return this.defaultAutoRefetch;
    }

    public synchronized void registerTAFCallback(OracleOCIFailover cbk, Object obj)
            throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public String getDatabaseProductVersion() throws SQLException {
        if (this.databaseProductVersion == "") {
            needLine();

            this.databaseProductVersion = doGetDatabaseProductVersion();
        }

        return this.databaseProductVersion;
    }

    public synchronized boolean getReportRemarks() {
        return this.reportRemarks;
    }

    public synchronized short getVersionNumber() throws SQLException {
        if (this.versionNumber == -1) {
            needLine();

            this.versionNumber = doGetVersionNumber();
        }

        return this.versionNumber;
    }

    public synchronized void registerCloseCallback(OracleCloseCallback occ, Object privData) {
        this.closeCallback = occ;
        this.privateData = privData;
    }

    public void setCreateStatementAsRefCursor(boolean value) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "PhysicalConnection.setCreateStatementAsRefCursor(" + value
                                               + "): no op", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean getCreateStatementAsRefCursor() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "PhysicalConnection.getCreateStatementAsRefCursor(): returned false", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public int pingDatabase(int timeOut) throws SQLException {
        if (this.lifecycle != 1) {
            return -1;
        }
        Statement stmt = null;
        try {
            stmt = createStatement();

            ((OracleStatement) stmt).defineColumnType(1, 12, 1);
            stmt.executeQuery("SELECT 'x' FROM DUAL");
        } catch (SQLException ea) {
            int i = -1;
            return i;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        return 0;
    }

    public synchronized Map getJavaObjectTypeMap() {
        return this.javaObjectMap;
    }

    public synchronized void setJavaObjectTypeMap(Map map) {
        this.javaObjectMap = map;
    }

    /** @deprecated */
    public void clearClientIdentifier(String clientId) throws SQLException {
        if ((!this.useDMSForEndToEnd) && (clientId != null) && (clientId != "")) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINE,
                                           "PhysicalConnection.clearClientIdentifier(conn=" + this
                                                   + ", clientId=" + clientId + ")", this);

                OracleLog.recursiveTrace = false;
            }

            String[] metrics = getEndToEndMetrics();

            if ((metrics != null) && (clientId.equals(metrics[1]))) {
                metrics[1] = null;

                setEndToEndMetrics(metrics, getEndToEndECIDSequenceNumber());
            }

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINE,
                             "PhysicalConnection.clearClientIdentifier(clientId) returned\n", this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    /** @deprecated */
    public void setClientIdentifier(String clientId) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.setClientIdentifier(conn="
                    + this + ", clientId=" + clientId + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.useDMSForEndToEnd) {
            String[] metrics = getEndToEndMetrics();

            if (metrics == null) {
                metrics = new String[4];
            }

            metrics[1] = clientId;

            setEndToEndMetrics(metrics, getEndToEndECIDSequenceNumber());
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "PhysicalConnection.setClientIdentifier(conn, clientId) returned\n", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setSessionTimeZone(String regionName) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "PhysicalConnection.setSessionTimeZone(regionName="
                                               + regionName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Statement stmt = null;
        ResultSet rset = null;
        try {
            stmt = createStatement();

            stmt.executeUpdate("ALTER SESSION SET TIME_ZONE = '" + regionName + "'");

            rset = stmt.executeQuery("SELECT DBTIMEZONE FROM DUAL");

            rset.next();

            String db_tz_str = rset.getString(1);

            setDbTzCalendar(db_tz_str);
        } catch (SQLException e) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "setSessionTimeZone() failed: "
                        + e.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }
        this.sessionTimeZone = regionName;
    }

    public String getSessionTimeZone() {
        return this.sessionTimeZone;
    }

    void setDbTzCalendar(String dbTzStr) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "setDbTzCalendar(dbTzStr=" + dbTzStr + ")", this);

            OracleLog.recursiveTrace = false;
        }

        char sign = dbTzStr.charAt(0);

        if ((sign == '-') || (sign == '+'))
            dbTzStr = "GMT" + dbTzStr;
        else {
            dbTzStr = "GMT+" + dbTzStr;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "setDbTzCalendar(dbTzStr=" + dbTzStr + ")", this);

            OracleLog.recursiveTrace = false;
        }

        TimeZone tz = TimeZone.getTimeZone(dbTzStr);

        this.dbTzCalendar = new GregorianCalendar(tz);
    }

    public Calendar getDbTzCalendar() {
        return this.dbTzCalendar;
    }

    public void setAccumulateBatchResult(boolean val) {
        this.accumulateBatchResult = val;
    }

    public boolean isAccumulateBatchResult() {
        return this.accumulateBatchResult;
    }

    public void setJ2EE13Compliant(boolean val) {
        this.j2ee13Compliant = val;
    }

    public boolean getJ2EE13Compliant() {
        return this.j2ee13Compliant;
    }

    public Class classForNameAndSchema(String name, String schemaName)
            throws ClassNotFoundException {
        return Class.forName(name);
    }

    public Class safelyGetClassForName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    public int getHeapAllocSize() throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        DatabaseError.throwSqlException(23);

        return -1;
    }

    public int getOCIEnvHeapAllocSize() throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        DatabaseError.throwSqlException(23);

        return -1;
    }

    public static OracleConnection unwrapCompletely(oracle.jdbc.OracleConnection wrappedConnection) {
        oracle.jdbc.OracleConnection previous = wrappedConnection;
        oracle.jdbc.OracleConnection next = previous;
        while (true) {
            if (next == null) {
                return (OracleConnection) previous;
            }
            previous = next;
            next = previous.unwrap();
        }
    }

    public void setWrapper(oracle.jdbc.OracleConnection w) {
        this.wrapper = w;
    }

    public oracle.jdbc.OracleConnection unwrap() {
        return null;
    }

    public oracle.jdbc.OracleConnection getWrapper() {
        if (this.wrapper != null) {
            return this.wrapper;
        }
        return this;
    }

    static oracle.jdbc.internal.OracleConnection _physicalConnectionWithin(
            Connection possiblyWrappedPossiblyLogicalConnection) {
        oracle.jdbc.internal.OracleConnection unwrappedConnection = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE,
                                      "PhysicalConnection.physicalConnectionWithin( conn="
                                              + possiblyWrappedPossiblyLogicalConnection + ")");

            OracleLog.recursiveTrace = false;
        }

        if (possiblyWrappedPossiblyLogicalConnection != null) {
            unwrappedConnection = unwrapCompletely((oracle.jdbc.OracleConnection) possiblyWrappedPossiblyLogicalConnection);

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger.log(Level.FINER, "unwrappedPossiblyLogicalConnection: "
                        + unwrappedConnection);

                OracleLog.recursiveTrace = false;
            }

        }

        return unwrappedConnection;
    }

    public oracle.jdbc.internal.OracleConnection physicalConnectionWithin() {
        return this;
    }

    public long getTdoCState(String schemaName, String typeName) throws SQLException {
        return 0L;
    }

    public void getOracleTypeADT(OracleTypeADT otype) throws SQLException {
    }

    public Datum toDatum(CustomDatum inObject) throws SQLException {
        return inObject.toDatum(this);
    }

    public short getNCharSet() {
        return this.conversion.getNCharSetId();
    }

    public ResultSet newArrayDataResultSet(Datum[] data, long index, int count, Map map)
            throws SQLException {
        return new ArrayDataResultSet(this, data, index, count, map);
    }

    public ResultSet newArrayDataResultSet(ARRAY array, long index, int count, Map map)
            throws SQLException {
        return new ArrayDataResultSet(this, array, index, count, map);
    }

    public ResultSet newArrayLocatorResultSet(ArrayDescriptor desc, byte[] locator, long index,
            int count, Map map) throws SQLException {
        return new ArrayLocatorResultSet(this, desc, locator, index, count, map);
    }

    public ResultSetMetaData newStructMetaData(StructDescriptor desc) throws SQLException {
        return new StructMetaData(desc);
    }

    public int CHARBytesToJavaChars(byte[] bytes, int nbytes, char[] chars) throws SQLException {
        int[] nBytes = new int[1];

        nBytes[0] = nbytes;

        return this.conversion.CHARBytesToJavaChars(bytes, 0, chars, 0, nBytes, chars.length);
    }

    public int NCHARBytesToJavaChars(byte[] bytes, int nbytes, char[] chars) throws SQLException {
        int[] nBytes = new int[1];

        return this.conversion.NCHARBytesToJavaChars(bytes, 0, chars, 0, nBytes, chars.length);
    }

    public boolean IsNCharFixedWith() {
        return this.conversion.IsNCharFixedWith();
    }

    public short getDriverCharSet() {
        return this.conversion.getClientCharSet();
    }

    public int getMaxCharSize() throws SQLException {
        DatabaseError.throwSqlException(58);

        return -1;
    }

    public int getMaxCharbyteSize() {
        return this.conversion.getMaxCharbyteSize();
    }

    public int getMaxNCharbyteSize() {
        return this.conversion.getMaxNCharbyteSize();
    }

    public boolean isCharSetMultibyte(short charSet) {
        return DBConversion.isCharSetMultibyte(charSet);
    }

    public int javaCharsToCHARBytes(char[] chars, int nchars, byte[] bytes) throws SQLException {
        return this.conversion.javaCharsToCHARBytes(chars, nchars, bytes);
    }

    public int javaCharsToNCHARBytes(char[] chars, int nchars, byte[] bytes) throws SQLException {
        return this.conversion.javaCharsToNCHARBytes(chars, nchars, bytes);
    }

    public abstract void getPropertyForPooledConnection(
            OraclePooledConnection paramOraclePooledConnection) throws SQLException;

    final void getPropertyForPooledConnection(OraclePooledConnection pc, String password)
            throws SQLException {
        Hashtable hashTable = new Hashtable();

        hashTable.put("obj_type_map", this.javaObjectMap);

        Properties prop = new Properties();

        prop.put("user", this.user);
        prop.put("password", password);

        prop.put("connection_url", this.url);
        prop.put("connect_auto_commit", "" + this.autoCommitSet);

        prop.put("trans_isolation", "" + this.txnLevel);

        if (getStatementCacheSize() != -1) {
            prop.put("stmt_cache_size", "" + getStatementCacheSize());

            prop.put("implicit_cache_enabled", "" + getImplicitCachingEnabled());

            prop.put("explict_cache_enabled", "" + getExplicitCachingEnabled());
        }

        prop.put("defaultExecuteBatch", "" + this.defaultBatch);
        prop.put("prefetch", "" + this.defaultRowPrefetch);
        prop.put("remarks", "" + this.reportRemarks);
        prop.put("AccumulateBatchResult", "" + this.accumulateBatchResult);
        prop.put("oracle.jdbc.J2EE13Compliant", "" + this.j2ee13Compliant);
        prop.put("processEscapes", "" + this.processEscapes);

        prop.put("restrictGetTables", "" + this.restrictGetTables);
        prop.put("synonyms", "" + this.includeSynonyms);
        prop.put("fixedString", "" + this.defaultFixedString);

        hashTable.put("connection_properties", prop);

        pc.setProperties(hashTable);
    }

    public Properties getDBAccessProperties() throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public Properties getOCIHandles() throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    abstract void logon() throws SQLException;

    void logoff() throws SQLException {
    }

    abstract void open(OracleStatement paramOracleStatement) throws SQLException;

    abstract void doCancel() throws SQLException;

    abstract void doSetAutoCommit(boolean paramBoolean) throws SQLException;

    abstract void doCommit() throws SQLException;

    abstract void doRollback() throws SQLException;

    abstract String doGetDatabaseProductVersion() throws SQLException;

    abstract short doGetVersionNumber() throws SQLException;

    int getDefaultStreamChunkSize() {
        return this.streamChunkSize;
    }

    abstract OracleStatement RefCursorBytesToStatement(byte[] paramArrayOfByte,
            OracleStatement paramOracleStatement) throws SQLException;

    public oracle.jdbc.internal.OracleStatement refCursorCursorToStatement(int cursorNumber)
            throws SQLException {
        DatabaseError.throwSqlException(23);

        return null;
    }

    public Connection getLogicalConnection(OraclePooledConnection pc, boolean autoCommit)
            throws SQLException {
        if ((this.logicalConnectionAttached != null) || (pc.getPhysicalHandle() != this)) {
            DatabaseError.throwSqlException(143);
        }
        LogicalConnection logicalConn = new LogicalConnection(pc, this, autoCommit);

        this.logicalConnectionAttached = logicalConn;

        return logicalConn;
    }

    public void getForm(OracleTypeADT otypeADT, OracleTypeCLOB otype, int attrIndex)
            throws SQLException {
    }

    public CLOB createClob(byte[] locator_bytes) throws SQLException {
        return new CLOB(this, locator_bytes);
    }

    public CLOB createClob(byte[] locator_bytes, short csform) throws SQLException {
        return new CLOB(this, locator_bytes, csform);
    }

    public BLOB createBlob(byte[] locator_bytes) throws SQLException {
        return new BLOB(this, locator_bytes);
    }

    public BFILE createBfile(byte[] locator_bytes) throws SQLException {
        return new BFILE(this, locator_bytes);
    }

    public boolean isDescriptorSharable(oracle.jdbc.internal.OracleConnection conn)
            throws SQLException {
        PhysicalConnection c1 = this;
        PhysicalConnection c2 = (PhysicalConnection) conn.getPhysicalConnection();

        return (c1 == c2) || (c1.url.equals(c2.url))
                || ((c2.protocol != null) && (c2.protocol.equals("kprb")));
    }

    boolean useLittleEndianSetCHARBinder() throws SQLException {
        return false;
    }

    public void setPlsqlWarnings(String setting) throws SQLException {
        if (setting == null) {
            DatabaseError.throwSqlException(68);
        }
        if ((setting != null) && ((setting = setting.trim()).length() > 0)
                && (!OracleSql.isValidPlsqlWarning(setting))) {
            DatabaseError.throwSqlException(68);
        }
        String alterSession = "ALTER SESSION SET PLSQL_WARNINGS=" + setting;

        String alterSessionEvent = "ALTER SESSION SET EVENTS='10933 TRACE NAME CONTEXT LEVEL 32768'";

        Statement stmt = null;
        try {
            stmt = createStatement(-1, -1);

            stmt.execute(alterSession);

            if (setting.equals("'DISABLE:ALL'")) {
                this.plsqlCompilerWarnings = false;
            } else {
                stmt.execute(alterSessionEvent);

                this.plsqlCompilerWarnings = true;
            }
        } finally {
            if (stmt != null)
                stmt.close();
        }
    }

    void internalClose() throws SQLException {
        this.lifecycle = 4;
    }

    public XAResource getXAResource() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "PhysicalConnection.getXAResource()", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(164);

        return null;
    }

    protected void doDescribeTable(AutoKeyInfo info) throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public void setApplicationContext(String nameSpace, String attribute, String value)
            throws SQLException {
        if ((nameSpace == null) || (attribute == null) || (value == null)) {
            throw new NullPointerException();
        }
        if (nameSpace.equals("")) {
            DatabaseError.throwSqlException(170);
        }

        if (nameSpace.compareToIgnoreCase("CLIENTCONTEXT") != 0) {
            DatabaseError.throwSqlException(174);
        }

        if (attribute.length() > 30) {
            DatabaseError.throwSqlException(171);
        }

        if (value.length() > 4000) {
            DatabaseError.throwSqlException(172);
        }

        doSetApplicationContext(nameSpace, attribute, value);
    }

    void doSetApplicationContext(String nameSpace, String attribute, String value)
            throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public void clearAllApplicationContext(String nameSpace) throws SQLException {
        if (nameSpace == null)
            throw new NullPointerException();
        if (nameSpace.equals("")) {
            DatabaseError.throwSqlException(170);
        }

        doClearAllApplicationContext(nameSpace);
    }

    void doClearAllApplicationContext(String nameSpace) throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public boolean isV8Compatible() throws SQLException {
        return this.v8Compatible;
    }

    static {
        endToEndMaxLength[0] = 32;
        endToEndMaxLength[1] = 64;
        endToEndMaxLength[2] = 64;
        endToEndMaxLength[3] = 48;

        _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

        TRACE = false;
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.PhysicalConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.PhysicalConnection JD-Core Version: 0.6.0
 */