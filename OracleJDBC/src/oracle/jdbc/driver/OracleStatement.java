package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

public abstract class OracleStatement implements oracle.jdbc.internal.OracleStatement,
        ScrollRsetStatement {
    static final int PLAIN_STMT = 0;
    static final int PREP_STMT = 1;
    static final int CALL_STMT = 2;
    static final byte IS_UNINITIALIZED = -1;
    static final byte IS_SELECT = 0;
    static final byte IS_PLSQL_BLOCK = 1;
    static final byte IS_DML = 2;
    static final byte IS_OTHER = 3;
    static final byte IS_CALL_BLOCK = 4;
    int cursorId;
    int numberOfDefinePositions;
    int definesBatchSize;
    Accessor[] accessors;
    int defineByteSubRange;
    int defineCharSubRange;
    int defineIndicatorSubRange;
    int defineLengthSubRange;
    byte[] defineBytes;
    char[] defineChars;
    short[] defineIndicators;
    boolean described = false;
    boolean describedWithNames = false;
    int rowsProcessed;
    int cachedDefineByteSize = 0;
    int cachedDefineCharSize = 0;
    int cachedDefineIndicatorSize = 0;
    OracleStatement children;
    OracleStatement nextChild;
    OracleStatement next;
    OracleStatement prev;
    long c_state;
    int numberOfBindPositions;
    byte[] bindBytes;
    char[] bindChars;
    short[] bindIndicators;
    int bindByteOffset;
    int bindCharOffset;
    int bindIndicatorOffset;
    int bindByteSubRange;
    int bindCharSubRange;
    int bindIndicatorSubRange;
    Accessor[] outBindAccessors;
    InputStream[][] parameterStream;
    int firstRowInBatch;
    boolean hasIbtBind = false;
    byte[] ibtBindBytes;
    char[] ibtBindChars;
    short[] ibtBindIndicators;
    int ibtBindByteOffset;
    int ibtBindCharOffset;
    int ibtBindIndicatorOffset;
    int ibtBindIndicatorSize;
    byte[] tmpByteArray;
    int sizeTmpByteArray = 0;
    byte[] tmpBindsByteArray;
    boolean needToSendOalToFetch = false;

    int[] definedColumnType = null;
    int[] definedColumnSize = null;
    int[] definedColumnFormOfUse = null;

    T4CTTIoac[] oacdefSent = null;

    int accessorByteOffset = 0;
    int accessorCharOffset = 0;
    int accessorShortOffset = 0;
    static final int VALID_ROWS_UNINIT = -999;
    PhysicalConnection connection;
    OracleInputStream streamList;
    OracleInputStream nextStream;
    OracleResultSetImpl currentResultSet;
    boolean processEscapes;
    boolean convertNcharLiterals;
    int queryTimeout;
    int batch;
    int currentRank;
    int currentRow;
    int validRows;
    int maxFieldSize;
    int maxRows;
    int totalRowsVisited;
    int rowPrefetch;
    int saved_rowPrefetch = -1;
    int defaultRowPrefetch;
    boolean rowPrefetchChanged;
    boolean gotLastBatch;
    boolean clearParameters;
    boolean closed;
    boolean sqlStringChanged;
    OracleSql sqlObject;
    boolean needToParse;
    boolean needToPrepareDefineBuffer;
    boolean columnsDefinedByUser;
    byte sqlKind;
    int autoRollback;
    int defaultFetchDirection;
    boolean autoRefetch;
    boolean serverCursor;
    boolean fixedString = false;

    boolean noMoreUpdateCounts = false;

    boolean isExecuting = false;
    static final byte EXECUTE_NONE = -1;
    static final byte EXECUTE_QUERY = 1;
    static final byte EXECUTE_UPDATE = 2;
    static final byte EXECUTE_NORMAL = 3;
    byte executionType = -1;
    OracleResultSet scrollRset;
    oracle.jdbc.OracleResultSetCache rsetCache;
    int userRsetType;
    int realRsetType;
    boolean needToAddIdentifier;
    SQLWarning sqlWarning;
    int cacheState = 1;

    int creationState = 0;

    boolean isOpen = false;

    int statementType = 0;

    boolean columnSetNull = false;
    int[] returnParamMeta;
    static final int DMLR_METADATA_PREFIX_SIZE = 3;
    static final int DMLR_METADATA_NUM_OF_RETURN_PARAMS = 0;
    static final int DMLR_METADATA_ROW_BIND_BYTES = 1;
    static final int DMLR_METADATA_ROW_BIND_CHARS = 2;
    static final int DMLR_METADATA_TYPE_OFFSET = 0;
    static final int DMLR_METADATA_IS_CHAR_TYPE_OFFSET = 1;
    static final int DMLR_METADATA_BIND_SIZE_OFFSET = 2;
    static final int DMLR_METADATA_PER_POSITION_SIZE = 3;
    Accessor[] returnParamAccessors;
    boolean returnParamsFetched;
    int rowsDmlReturned;
    int numReturnParams;
    byte[] returnParamBytes;
    char[] returnParamChars;
    short[] returnParamIndicators;
    int returnParamRowBytes;
    int returnParamRowChars;
    OracleReturnResultSet returnResultSet;
    boolean isAutoGeneratedKey;
    AutoKeyInfo autoKeyInfo;
    TimeZone defaultTZ = null;
    int lastIndex;
    Vector m_batchItems = new Vector();

    ArrayList tempClobsToFree = null;
    ArrayList tempBlobsToFree = null;

    ArrayList oldTempClobsToFree = null;
    ArrayList oldTempBlobsToFree = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    abstract void doDescribe(boolean paramBoolean) throws SQLException;

    abstract void executeForDescribe() throws SQLException;

    abstract void executeForRows(boolean paramBoolean) throws SQLException;

    abstract void fetch() throws SQLException;

    void continueReadRow(int start) throws SQLException {
        throw new SQLException("continueReadRow is only implemented by the T4C statements.");
    }

    abstract void doClose() throws SQLException;

    abstract void closeQuery() throws SQLException;

    OracleStatement(PhysicalConnection conn, int batchValue, int rowPrefetchValue)
            throws SQLException {
        this(conn, batchValue, rowPrefetchValue, -1, -1);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleStatement.OracleStatement(conn, batchValue, rowPrefetchValue):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    OracleStatement(PhysicalConnection conn, int batch_value, int row_prefetch_value,
            int UserResultSetType, int UserResultSetConcur) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.OracleStatement(conn, batchValue="
                                               + batch_value + ", rowPrefetchValue="
                                               + row_prefetch_value + ", UserResultSetType="
                                               + UserResultSetType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.connection = conn;

        this.connection.needLine();

        this.connection.addStatement(this);

        this.sqlObject = new OracleSql(this.connection.conversion);
        this.sqlObject.isV8Compatible = this.connection.v8Compatible;

        this.processEscapes = this.connection.processEscapes;
        this.convertNcharLiterals = this.connection.convertNcharLiterals;
        this.autoRollback = 2;
        this.gotLastBatch = false;
        this.closed = false;
        this.clearParameters = true;
        this.serverCursor = false;
        this.needToAddIdentifier = false;
        this.defaultFetchDirection = 1000;
        this.fixedString = this.connection.getDefaultFixedString();
        this.rowPrefetchChanged = false;
        this.rowPrefetch = row_prefetch_value;
        this.defaultRowPrefetch = row_prefetch_value;
        this.batch = batch_value;
        this.autoRefetch = this.connection.getDefaultAutoRefetch();

        this.sqlStringChanged = true;
        this.needToParse = true;
        this.needToPrepareDefineBuffer = true;
        this.columnsDefinedByUser = false;

        if ((UserResultSetType != -1) || (UserResultSetConcur != -1)) {
            this.realRsetType = 0;
            this.userRsetType = ResultSetUtil.getRsetTypeCode(UserResultSetType,
                                                              UserResultSetConcur);

            this.needToAddIdentifier = ResultSetUtil.needIdentifier(this.userRsetType);
        } else {
            this.userRsetType = 1;
            this.realRsetType = 1;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleStatement.OracleStatement(c, batch_value, UserResultSetType):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    void initializeDefineSubRanges() {
        this.defineByteSubRange = 0;
        this.defineCharSubRange = 0;
        this.defineIndicatorSubRange = 0;
    }

    void prepareDefinePreambles() {
    }

    void prepareAccessors() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "OracleStatement.prepareAccessors() rowPrefetch = "
                                               + this.rowPrefetch + " needToPrepareDefineBuffer = "
                                               + this.needToPrepareDefineBuffer + " streamList "
                                               + this.streamList, this);

            OracleLog.recursiveTrace = false;
        }

        byte[] tmp_defineBytes = null;
        char[] tmp_defineChars = null;
        short[] tmp_defineIndicators = null;
        boolean isIndicatorsReused = false;

        if (this.accessors == null) {
            DatabaseError.throwSqlException(21);
        }

        int defineByteSize = 0;
        int defineCharSize = 0;

        for (int i = 0; i < this.numberOfDefinePositions; i++) {
            Accessor accessor = this.accessors[i];

            if (accessor == null) {
                DatabaseError.throwSqlException(21);
            }
            defineByteSize += accessor.byteLength;
            defineCharSize += accessor.charLength;
        }

        if ((this.streamList != null) && (!this.connection.useFetchSizeWithLongColumn)) {
            this.rowPrefetch = 1;
        }
        int preFetchSize = this.rowPrefetch;

        this.definesBatchSize = preFetchSize;

        initializeDefineSubRanges();

        int totalByteSize = this.defineByteSubRange + defineByteSize * preFetchSize;

        if ((this.defineBytes == null) || (this.defineBytes.length < totalByteSize)) {
            if (this.defineBytes != null)
                tmp_defineBytes = this.defineBytes;
            this.defineBytes = new byte[totalByteSize];
        }

        this.defineByteSubRange += this.accessorByteOffset;

        int totalCharSize = this.defineCharSubRange + defineCharSize * preFetchSize;

        if (((this.defineChars == null) || (this.defineChars.length < totalCharSize))
                && (totalCharSize > 0)) {
            if (this.defineChars != null)
                tmp_defineChars = this.defineChars;
            this.defineChars = new char[totalCharSize];
        }

        this.defineCharSubRange += this.accessorCharOffset;

        int defineIndicatorSizeTerm = this.numberOfDefinePositions * preFetchSize;
        int totalIndicatorSize = this.defineIndicatorSubRange + defineIndicatorSizeTerm
                + defineIndicatorSizeTerm;

        if ((this.defineIndicators == null) || (this.defineIndicators.length < totalIndicatorSize)) {
            if (this.defineIndicators != null)
                tmp_defineIndicators = this.defineIndicators;
            this.defineIndicators = new short[totalIndicatorSize];
        } else if (this.defineIndicators.length > totalIndicatorSize) {
            isIndicatorsReused = true;
            tmp_defineIndicators = this.defineIndicators;
        }

        this.defineIndicatorSubRange += this.accessorShortOffset;

        int defineLengthSubRange = this.defineIndicatorSubRange + defineIndicatorSizeTerm;

        for (int i = 0; i < this.numberOfDefinePositions; i++) {
            Accessor accessor = this.accessors[i];

            accessor.lengthIndexLastRow = accessor.lengthIndex;
            accessor.indicatorIndexLastRow = accessor.indicatorIndex;
            accessor.columnIndexLastRow = accessor.columnIndex;

            accessor.setOffsets(preFetchSize);

            accessor.lengthIndex = defineLengthSubRange;
            accessor.indicatorIndex = this.defineIndicatorSubRange;
            accessor.rowSpaceByte = this.defineBytes;
            accessor.rowSpaceChar = this.defineChars;
            accessor.rowSpaceIndicator = this.defineIndicators;
            this.defineIndicatorSubRange += preFetchSize;
            defineLengthSubRange += preFetchSize;
        }

        prepareDefinePreambles();

        if ((tmp_defineChars != null) || (tmp_defineBytes != null)) {
            saveDefineBuffersIfRequired(tmp_defineChars, tmp_defineBytes,
                                        tmp_defineIndicators != null ? tmp_defineIndicators
                                                : this.defineIndicators, isIndicatorsReused);
        } else if ((isIndicatorsReused) && (this.saved_rowPrefetch != -1)
                && (this.rowPrefetch != this.saved_rowPrefetch)) {
            saveDefineBuffersIfRequired(this.defineChars, this.defineBytes, this.defineIndicators,
                                        isIndicatorsReused);
        }

        this.saved_rowPrefetch = -1;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "OracleStatement.prepareAccessors() return rowPrefetch = "
                                               + this.rowPrefetch + " needToPrepareDefineBuffer = "
                                               + this.needToPrepareDefineBuffer, this);

            OracleLog.recursiveTrace = false;
        }
    }

    boolean checkAccessorsUsable() throws SQLException {
        int len = this.accessors.length;

        if (len < this.numberOfDefinePositions) {
            return false;
        }
        boolean allHaveExternalType = true;
        boolean anyHaveExternalType = false;
        boolean result = false;

        for (int i = 0; i < this.numberOfDefinePositions; i++) {
            Accessor accessor = this.accessors[i];

            if ((accessor == null) || (accessor.externalType == 0))
                allHaveExternalType = false;
            else {
                anyHaveExternalType = true;
            }
        }
        if (allHaveExternalType) {
            result = true;
        } else if (anyHaveExternalType) {
            DatabaseError.throwSqlException(21);
        } else {
            this.columnsDefinedByUser = false;
        }
        return result;
    }

    void executeMaybeDescribe() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.execute_maybe_describe() rowPrefetchChanged = "
                                               + this.rowPrefetchChanged + ", needToParse = "
                                               + this.needToParse
                                               + ", needToPrepareDefineBuffer = "
                                               + this.needToPrepareDefineBuffer
                                               + ", columnsDefinedByUser = "
                                               + this.columnsDefinedByUser, this);

            OracleLog.recursiveTrace = false;
        }

        cleanOldTempLobs();

        if (this.rowPrefetchChanged) {
            if ((this.streamList == null) && (this.rowPrefetch != this.definesBatchSize)) {
                this.needToPrepareDefineBuffer = true;
            }
            this.rowPrefetchChanged = false;
        }

        if (!this.needToPrepareDefineBuffer) {
            if (this.accessors == null) {
                this.needToPrepareDefineBuffer = true;
            } else if (this.columnsDefinedByUser) {
                this.needToPrepareDefineBuffer = (!checkAccessorsUsable());
            }
        }
        boolean executed_for_describe = false;
        try {
            this.isExecuting = true;

            if (this.needToPrepareDefineBuffer) {
                if (!this.columnsDefinedByUser) {
                    executeForDescribe();

                    executed_for_describe = true;
                }

                prepareAccessors();
            }

            int len = this.accessors.length;

            for (int i = this.numberOfDefinePositions; i < len; i++) {
                Accessor accessor = this.accessors[i];

                if (accessor != null) {
                    accessor.rowSpaceIndicator = null;
                }
            }
            executeForRows(executed_for_describe);
        } catch (SQLException ea) {
            this.needToParse = true;
            throw ea;
        } finally {
            this.isExecuting = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.execute_maybe_describe():return validRows = "
                                               + this.validRows + ", needToPrepareDefineBuffer = "
                                               + this.needToPrepareDefineBuffer + " ", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void adjustGotLastBatch() {
    }

    void doExecuteWithTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.doExecuteWithTimeout() needToPrepareDefineBuffer = "
                                               + this.needToPrepareDefineBuffer + "", this);

            OracleLog.recursiveTrace = false;
        }

        this.rowsProcessed = 0;

        if (this.sqlKind == 0) {
            if ((this.connection.j2ee13Compliant) && (this.executionType == 2)) {
                DatabaseError.throwSqlException(129);
            }
            this.connection.needLine();

            if (!this.isOpen) {
                this.connection.open(this);

                this.isOpen = true;
            }

            this.connection.registerHeartbeat();

            if (this.queryTimeout != 0) {
                try {
                    this.connection.getTimeout().setTimeout(this.queryTimeout * 1000, this);
                    executeMaybeDescribe();
                } finally {
                    this.connection.getTimeout().cancelTimeout();
                }
            } else {
                executeMaybeDescribe();
            }
            checkValidRowsStatus();

            if (this.serverCursor)
                adjustGotLastBatch();
        } else {
            if ((this.connection.j2ee13Compliant) && (this.sqlKind != 1) && (this.sqlKind != 4)
                    && (this.executionType == 1)) {
                DatabaseError.throwSqlException(128);
            }
            this.currentRank += 1;

            if (this.currentRank >= this.batch) {
                try {
                    this.connection.needLine();

                    if (!this.isOpen) {
                        this.connection.open(this);

                        this.isOpen = true;
                    }

                    if (this.queryTimeout != 0) {
                        this.connection.getTimeout().setTimeout(this.queryTimeout * 1000, this);
                    }
                    this.isExecuting = true;

                    executeForRows(false);
                } catch (SQLException ea) {
                    this.needToParse = true;
                    throw ea;
                } finally {
                    if (this.queryTimeout != 0) {
                        this.connection.getTimeout().cancelTimeout();
                    }
                    this.currentRank = 0;
                    this.isExecuting = false;

                    checkValidRowsStatus();
                }

            }

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "End of doExecuteWithTimeout(): currentRank="
                                                   + this.currentRank
                                                   + " needToPrepareDefineBuffer = "
                                                   + this.needToPrepareDefineBuffer, this);

                OracleLog.recursiveTrace = false;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.doExecuteWithTimeout():return validRows = "
                                               + this.validRows + ", needToPrepareDefineBuffer = "
                                               + this.needToPrepareDefineBuffer, this);

            OracleLog.recursiveTrace = false;
        }
    }

    void open() throws SQLException {
        if (!this.isOpen) {
            this.connection.needLine();
            this.connection.open(this);

            this.isOpen = true;
        }
    }

    public ResultSet executeQuery(String sql) throws SQLException {
        ResultSet result = null;

        synchronized (this.connection) {
            synchronized (this) {
                try {
                    this.executionType = 1;

                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.INFO,
                                                   "OracleStatement.executeQuery(sql) needToPrepareDefineBuffer = "
                                                           + this.needToPrepareDefineBuffer, this);

                        OracleLog.recursiveTrace = false;
                    }

                    this.noMoreUpdateCounts = false;

                    ensureOpen();
                    checkIfJdbcBatchExists();

                    sendBatch();

                    this.sqlObject.initialize(sql);

                    this.sqlKind = this.sqlObject.getSqlKind();
                    this.needToParse = true;

                    prepareForNewResults(true, true);

                    if (this.userRsetType == 1) {
                        doExecuteWithTimeout();

                        this.currentResultSet = new OracleResultSetImpl(this.connection, this);
                        result = this.currentResultSet;
                    } else {
                        result = doScrollStmtExecuteQuery();

                        if (result == null) {
                            this.currentResultSet = new OracleResultSetImpl(this.connection, this);
                            result = this.currentResultSet;
                        }
                    }
                } finally {
                    this.executionType = -1;
                }
            }
        }

        return result;
    }

    public void closeWithKey(String key) throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public void close() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                closeOrCache(null);
            }
        }
    }

    protected void closeOrCache(String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.closeOrCache(" + key + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            return;
        }

        if ((this.statementType != 0) && (this.cacheState != 0) && (this.cacheState != 3)
                && (this.connection.isStatementCacheInitialized())) {
            if (key == null) {
                if (this.connection.getImplicitCachingEnabled()) {
                    this.connection.cacheImplicitStatement((OraclePreparedStatement) this,
                                                           this.sqlObject.getOriginalSql(),
                                                           this.statementType, this.userRsetType);
                } else {
                    this.cacheState = 0;

                    hardClose();
                }

            } else if (this.connection.getExplicitCachingEnabled()) {
                this.connection.cacheExplicitStatement((OraclePreparedStatement) this, key);
            } else {
                this.cacheState = 0;

                hardClose();
            }

        } else {
            hardClose();
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.closeOrCache : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected void hardClose() throws SQLException {
        hardClose(true);
    }

    private void hardClose(boolean closeCursor) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.hardClose(closeCursor)", this);

            OracleLog.recursiveTrace = false;
        }

        alwaysOnClose();

        this.describedWithNames = false;
        this.described = false;

        this.connection.removeStatement(this);

        cleanupDefines();

        if ((this.isOpen) && (closeCursor) && (!this.connection.isClosed())) {
            doClose();

            this.isOpen = false;
        }

        this.sqlObject = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.hardClose : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected void alwaysOnClose() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.alwaysOnClose()", this);

            OracleLog.recursiveTrace = false;
        }

        OracleStatement child = this.children;

        while (child != null) {
            OracleStatement n = child.nextChild;

            child.close();

            child = n;
        }

        this.closed = true;

        if (!this.connection.isClosed()) {
            this.connection.needLine();

            if (this.currentResultSet != null) {
                this.currentResultSet.internal_close(false);

                this.currentResultSet = null;
            }

            if (this.scrollRset != null) {
                this.scrollRset.close();

                this.scrollRset = null;
            }

            if (this.returnResultSet != null) {
                this.returnResultSet.close();
                this.returnResultSet = null;
            }
        }

        clearWarnings();

        this.m_batchItems = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.alwaysOnClose : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void closeLeaveCursorOpen() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.INFO,
                                               "OracleStatement.closeLeaveCursorOpen() needToPrepareDefineBuffer = "
                                                       + this.needToPrepareDefineBuffer + "", this);

                    OracleLog.recursiveTrace = false;
                }

                if (this.closed) {
                    return;
                }

                hardClose(false);
            }
        }
    }

    public int executeUpdate(String sql) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.executeUpdate(sql)", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                setNonAutoKey();
                return executeUpdateInternal(sql);
            }
        }
    }

    int executeUpdateInternal(String sql) throws SQLException {
        try {
            if (this.executionType == -1) {
                this.executionType = 2;
            }

            this.noMoreUpdateCounts = false;

            ensureOpen();
            checkIfJdbcBatchExists();

            sendBatch();

            this.sqlObject.initialize(sql);

            this.sqlKind = this.sqlObject.getSqlKind();
            this.needToParse = true;

            prepareForNewResults(true, true);

            if (this.userRsetType == 1) {
                doExecuteWithTimeout();
            } else {
                doScrollStmtExecuteQuery();
            }

            int i = this.validRows;
            return i;
        } finally {
            this.executionType = -1;
        }
    }

    public boolean execute(String sql) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.execute(sql)", this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                setNonAutoKey();

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINE, "OracleStatement.execute(sql):return",
                                               this);

                    OracleLog.recursiveTrace = false;
                }

                return executeInternal(sql);
            }
        }
    }

    boolean executeInternal(String sql) throws SQLException {
        try {
            this.executionType = 3;

            this.noMoreUpdateCounts = false;

            ensureOpen();
            checkIfJdbcBatchExists();

            sendBatch();

            this.sqlObject.initialize(sql);

            this.sqlKind = this.sqlObject.getSqlKind();
            this.needToParse = true;

            prepareForNewResults(true, true);

            if (this.userRsetType == 1) {
                doExecuteWithTimeout();
            } else {
                doScrollStmtExecuteQuery();
            }

            boolean i = this.sqlKind == 0 ? true : false;
            return i;
        } finally {
            this.executionType = -1;
        }
    }

    int getNumberOfColumns() throws SQLException {
        if (!this.described) {
            synchronized (this.connection) {
                synchronized (this) {
                    this.connection.needLine();
                    doDescribe(false);

                    this.described = true;
                }
            }
        }
        return this.numberOfDefinePositions;
    }

    Accessor[] getDescription() throws SQLException {
        if (!this.described) {
            synchronized (this.connection) {
                synchronized (this) {
                    this.connection.needLine();
                    doDescribe(false);

                    this.described = true;
                }
            }
        }
        return this.accessors;
    }

    Accessor[] getDescriptionWithNames() throws SQLException {
        if (!this.describedWithNames) {
            synchronized (this.connection) {
                synchronized (this) {
                    this.connection.needLine();
                    doDescribe(true);

                    this.described = true;
                    this.describedWithNames = true;
                }
            }
        }
        return this.accessors;
    }

    byte getSqlKind() {
        return this.sqlKind;
    }

    public synchronized void clearDefines() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.clearDefines()", this);

            OracleLog.recursiveTrace = false;
        }

        freeLine();

        this.streamList = null;

        this.columnsDefinedByUser = false;
        this.needToPrepareDefineBuffer = true;

        this.numberOfDefinePositions = 0;
        this.definesBatchSize = 0;

        this.described = false;
        this.describedWithNames = false;

        cleanupDefines();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.clearDefines():return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void reparseOnRedefineIfNeeded() throws SQLException {
    }

    void defineColumnTypeInternal(int column_index, int type, int size, boolean sizeNotGiven,
            String typeName) throws SQLException {
        defineColumnTypeInternal(column_index, type, size, (short) 1, sizeNotGiven, typeName);
    }

    void defineColumnTypeInternal(int column_index, int type, int size, short form,
            boolean sizeNotGiven, String typeName) throws SQLException {
        if (this.connection.disableDefineColumnType) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINE,
                                           "OracleStatement.defineColumnTypeInternal--DISABLED",
                                           this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.defineColumnTypeInternal("
                    + column_index + ", " + type + ", " + size + ", " + sizeNotGiven + ", "
                    + typeName + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (column_index < 1) {
            DatabaseError.throwSqlException(3);
        }
        if (type == 0) {
            DatabaseError.throwSqlException(4);
        }
        int idx = column_index - 1;
        int max_len = this.maxFieldSize;

        if (sizeNotGiven) {
            if ((type == 1) || (type == 12)) {
                this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 108);
            }

        } else {
            if (size < 0) {
                DatabaseError.throwSqlException(53);
            }
            if ((max_len == 0) || (size < max_len)) {
                max_len = size;
            }
        }
        if ((this.currentResultSet != null) && (!this.currentResultSet.closed)) {
            DatabaseError.throwSqlException(28);
        }
        if (!this.columnsDefinedByUser) {
            clearDefines();

            this.columnsDefinedByUser = true;
        }

        if (this.numberOfDefinePositions < column_index) {
            if ((this.accessors == null) || (this.accessors.length < column_index)) {
                Accessor[] na = new Accessor[column_index << 1];

                if (this.accessors != null) {
                    System.arraycopy(this.accessors, 0, na, 0, this.numberOfDefinePositions);
                }
                this.accessors = na;
            }

            this.numberOfDefinePositions = column_index;
        }

        int internal_type = getInternalType(type);

        if (((internal_type == 109) || (internal_type == 111))
                && ((typeName == null) || (typeName.equals("")))) {
            DatabaseError.throwSqlException(60, "Invalid arguments");
        }

        Accessor accessor = this.accessors[idx];
        boolean need_to_prepare = true;

        if (accessor != null) {
            int reusability = accessor.useForDataAccessIfPossible(internal_type, type, max_len,
                                                                  typeName);

            if (reusability == 0) {
                form = accessor.formOfUse;
                accessor = null;

                reparseOnRedefineIfNeeded();
            } else if (reusability == 1) {
                accessor = null;

                reparseOnRedefineIfNeeded();
            } else if (reusability == 2) {
                need_to_prepare = false;
            }
        }
        if (need_to_prepare) {
            this.needToPrepareDefineBuffer = true;
        }
        if (accessor == null) {
            this.accessors[idx] = allocateAccessor(internal_type, type, column_index, max_len,
                                                   form, typeName, false);

            this.described = false;
            this.describedWithNames = false;
        }
    }

    Accessor allocateAccessor(int internal_type, int external_type, int col_index, int max_len,
            short form, String typeName, boolean forBind) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.allocateAccessor("
                    + internal_type + ", " + external_type + ", " + col_index + ", " + max_len
                    + ", " + form + ", " + typeName + ", " + forBind + ")", this);

            OracleLog.recursiveTrace = false;
        }

        switch (internal_type) {
        case 96:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new CharAccessor(this, max_len, form, external_type, forBind);
        case 8:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            if (forBind)
                break;
            return new LongAccessor(this, col_index, max_len, form, external_type);
        case 1:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new VarcharAccessor(this, max_len, form, external_type, forBind);
        case 2:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new NumberAccessor(this, max_len, form, external_type, forBind);
        case 6:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new VarnumAccessor(this, max_len, form, external_type, forBind);
        case 24:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            if (!forBind) {
                return new LongRawAccessor(this, col_index, max_len, form, external_type);
            }

        case 23:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            if (forBind) {
                return new OutRawAccessor(this, max_len, form, external_type);
            }
            return new RawAccessor(this, max_len, form, external_type, false);
        case 100:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new BinaryFloatAccessor(this, max_len, form, external_type, forBind);
        case 101:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new BinaryDoubleAccessor(this, max_len, form, external_type, forBind);
        case 104:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new RowidAccessor(this, max_len, form, external_type, forBind);
        case 102:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new ResultSetAccessor(this, max_len, form, external_type, forBind);
        case 12:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new DateAccessor(this, max_len, form, external_type, forBind);
        case 113:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new BlobAccessor(this, max_len, form, external_type, forBind);
        case 112:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new ClobAccessor(this, max_len, form, external_type, forBind);
        case 114:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new BfileAccessor(this, max_len, form, external_type, forBind);
        case 109:
        {
            if (typeName == null) {
                if (forBind) {
                    DatabaseError.throwSqlException(12, "sqlType=" + external_type);
                } else {
                    DatabaseError.throwSqlException(60, "Unable to resolve type \"null\"");
                }
            }
            Accessor result = new NamedTypeAccessor(this, typeName, form, external_type, forBind);

            result.initMetadata();

            return result;
        }
        case 111:
        {
            if (typeName == null) {
                if (forBind) {
                    DatabaseError.throwSqlException(12, "sqlType=" + external_type);
                } else {
                    DatabaseError.throwSqlException(60, "Unable to resolve type \"null\"");
                }
            }
            Accessor result = new RefTypeAccessor(this, typeName, form, external_type, forBind);

            result.initMetadata();

            return result;
        }
        case 180:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            if (this.connection.v8Compatible) {
                return new DateAccessor(this, max_len, form, external_type, forBind);
            }
            return new TimestampAccessor(this, max_len, form, external_type, forBind);
        case 181:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new TimestamptzAccessor(this, max_len, form, external_type, forBind);
        case 231:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new TimestampltzAccessor(this, max_len, form, external_type, forBind);
        case 182:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new IntervalymAccessor(this, max_len, form, external_type, forBind);
        case 183:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            return new IntervaldsAccessor(this, max_len, form, external_type, forBind);
        case 995:
            DatabaseError.throwSqlException(89);
        }

        DatabaseError.throwSqlException(4);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.SEVERE,
                                       "OracleStatement.allocateAccessor:return: null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    public synchronized void defineColumnType(int column_index, int type) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.defineColumnType(column_index="
                    + column_index + ", type=" + type + ")", this);

            OracleLog.recursiveTrace = false;
        }

        defineColumnTypeInternal(column_index, type, 0, true, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "OracleStatement.defineColumnType(column_index, type):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void defineColumnType(int column_index, int type, int max_size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.defineColumnType(column_index="
                    + column_index + "type=" + type + "max_size=" + max_size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        defineColumnTypeInternal(column_index, type, max_size, false, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleStatement.defineColumnType(column_index, type, size):return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void defineColumnType(int column_index, int type, int max_size, short form_of_use)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.defineColumnType(column_index="
                    + column_index + "type=" + type + "max_size=" + max_size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        defineColumnTypeInternal(column_index, type, max_size, form_of_use, false, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleStatement.defineColumnType(column_index, type, size):return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void defineColumnTypeBytes(int column_index, int type, int max_size)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.defineColumnTypeBytes(column_index="
                                               + column_index + "type=" + type + "max_size="
                                               + max_size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        defineColumnTypeInternal(column_index, type, max_size, false, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleStatement.defineColumnTypeBytes(column_index, type, max_size):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void defineColumnTypeChars(int column_index, int type, int max_size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.defineColumnTypeChars(column_index="
                                               + column_index + "type=" + type + "max_size="
                                               + max_size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        defineColumnTypeInternal(column_index, type, max_size, false, null);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleStatement.defineColumnTypeChars(column_index, type, max_size):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void defineColumnType(int column_index, int typeCode, String typeName)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.defineColumnType(column_index="
                    + column_index + ", typeCode=" + typeCode + ", typeName=" + typeName + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        synchronized (this.connection) {
            synchronized (this) {
                defineColumnTypeInternal(column_index, typeCode, 0, true, typeName);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleStatement.defineColumnType(column_index, typeCode, typeName):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    void setCursorId(int id) throws SQLException {
        this.cursorId = id;
    }

    void setPrefetchInternal(int new_value, boolean setRowPrefetch, boolean statement)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.setPrefetchInternal(new_value="
                    + new_value + ", setRowPrefetch=" + setRowPrefetch + ", statement=" + statement
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (setRowPrefetch) {
            if (new_value <= 0) {
                DatabaseError.throwSqlException(20);
            }

        } else if (new_value < 0) {
            DatabaseError.throwSqlException(68, "setFetchSize");
        } else if (new_value == 0) {
            new_value = this.connection.getDefaultRowPrefetch();
        }

        if (statement) {
            if (new_value != this.defaultRowPrefetch) {
                this.saved_rowPrefetch = this.rowPrefetch;
                this.defaultRowPrefetch = new_value;

                if ((this.currentResultSet == null) || (this.currentResultSet.closed)) {
                    this.rowPrefetchChanged = true;
                }

            }

        } else if ((new_value != this.rowPrefetch) && (this.streamList == null)) {
            this.saved_rowPrefetch = this.rowPrefetch;
            this.rowPrefetch = new_value;
            this.rowPrefetchChanged = true;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleStatement.setPrefetchInternal(new_value, setRowPrefetch, statement):return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void setRowPrefetch(int value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.setRowPrefetch(value=" + value
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setPrefetchInternal(value, true, true);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.setRowPrefetch(value):return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    int getPrefetchInternal(boolean statement) {
        int ret_val = statement ? this.defaultRowPrefetch : this.rowPrefetch;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.getPrefetchInternal("
                    + statement + ") returned " + ret_val, this);

            OracleLog.recursiveTrace = false;
        }

        return ret_val;
    }

    public synchronized int getRowPrefetch() {
        return getPrefetchInternal(true);
    }

    public void setFixedString(boolean fixedString_value) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleStatement.setDefaultFixedString(fixedString="
                                               + fixedString_value, this);

            OracleLog.recursiveTrace = false;
        }

        this.fixedString = fixedString_value;
    }

    public boolean getFixedString() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.getFixedString() returning "
                    + this.fixedString, this);

            OracleLog.recursiveTrace = false;
        }

        return this.fixedString;
    }

    void check_row_prefetch_changed() throws SQLException {
        if (this.rowPrefetchChanged) {
            if (this.streamList == null) {
                prepareAccessors();

                this.needToPrepareDefineBuffer = true;
            }

            this.rowPrefetchChanged = false;
        }
    }

    void setDefinesInitialized(boolean value) {
    }

    void printState(String message) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINEST, this + ": " + message + ":" + "  sqlObject="
                    + this.sqlObject + "  sqlStringChanged=" + this.sqlStringChanged
                    + "  rowPrefetch=" + this.rowPrefetch + "  defaultRowPrefetch="
                    + this.defaultRowPrefetch + "  rowPrefetchChanged=" + this.rowPrefetchChanged
                    + "  batch=" + this.batch + "  currentRank=" + this.currentRank
                    + "  currentRow=" + this.currentRow + "  validRows=" + this.validRows
                    + "  autoRefetch=" + this.autoRefetch + "  serverCursor=" + this.serverCursor
                    + "  currentResultSet=" + this.currentResultSet + "  processEscapes="
                    + this.processEscapes + "  convertNcharLiterals=" + this.convertNcharLiterals
                    + "  queryTimeout=" + this.queryTimeout + "  needToParse=" + this.needToParse
                    + "  needToPrepareDefineBuffer=" + this.needToPrepareDefineBuffer
                    + "  columnsDefinedByUser=" + this.columnsDefinedByUser, this);

            OracleLog.recursiveTrace = false;
        }
    }

    void checkValidRowsStatus() throws SQLException {
        if (this.validRows == -2) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "checkValidRowStatus " + this
                        + " nextStream = " + this.nextStream, this);

                OracleLog.recursiveTrace = false;
            }

            this.validRows = 1;

            this.connection.holdLine(this);

            OracleInputStream is = this.streamList;

            while (is != null) {
                if (is.hasBeenOpen) {
                    is = is.accessor.initForNewRow();
                }

                is.closed = false;
                is.hasBeenOpen = true;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger
                            .log(Level.FINER, "checkValidRowStatus opens " + is, this);

                    OracleLog.recursiveTrace = false;
                }

                is = is.nextStream;
            }

            this.nextStream = this.streamList;
        } else if (this.sqlKind == 0) {
            if (this.validRows < this.rowPrefetch)
                this.gotLastBatch = true;
        } else if ((this.sqlKind != 1) && (this.sqlKind != 4)) {
            this.rowsProcessed = this.validRows;
        }
    }

    void cleanupDefines() {
        if (this.accessors != null) {
            for (int i = 0; i < this.accessors.length; i++)
                this.accessors[i] = null;
        }
        this.accessors = null;

        if (this.defineBytes != null) {
            this.defineBytes = null;
        }

        if (this.defineChars != null) {
            this.defineChars = null;
        }

        if (this.defineIndicators != null) {
            this.defineIndicators = null;
        }
    }

    public synchronized int getMaxFieldSize() throws SQLException {
        return this.maxFieldSize;
    }

    public synchronized void setMaxFieldSize(int max) throws SQLException {
        if (max < 0) {
            DatabaseError.throwSqlException(68);
        }
        this.maxFieldSize = max;
    }

    public int getMaxRows() throws SQLException {
        return this.maxRows;
    }

    public synchronized void setMaxRows(int max) throws SQLException {
        if (max < 0) {
            DatabaseError.throwSqlException(68);
        }
        this.maxRows = max;
    }

    public synchronized void setEscapeProcessing(boolean enable) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Statement.setEscapeProcessing", this);

            OracleLog.recursiveTrace = false;
        }

        this.processEscapes = enable;
    }

    public synchronized int getQueryTimeout() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.getQueryTimeout()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.queryTimeout;
    }

    public synchronized void setQueryTimeout(int max) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.setQueryTimeout(max=" + max
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (max < 0) {
            DatabaseError.throwSqlException(68);
        }
        this.queryTimeout = max;
    }

    public void cancel() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.cancel()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            return;
        }

        if (this.connection.statementHoldingLine != null)
            freeLine();
        else if (this.isExecuting) {
            this.connection.doCancel();
        }
        this.connection.releaseLineForCancel();
    }

    public SQLWarning getWarnings() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.getWarnings()", this);

            OracleLog.recursiveTrace = false;
        }

        return this.sqlWarning;
    }

    public void clearWarnings() throws SQLException {
        this.sqlWarning = null;
    }

    void foundPlsqlCompilerWarning() throws SQLException {
        SQLWarning w = DatabaseError
                .newSqlWarning("Found Plsql compiler warnings.", "99999", 24439);

        if (this.sqlWarning != null)
            this.sqlWarning.setNextWarning(w);
        else
            this.sqlWarning = w;
    }

    public void setCursorName(String name) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Statement.setCursorName", this);

            OracleLog.recursiveTrace = false;
        }

        DatabaseError.throwSqlException(23);
    }

    public synchronized ResultSet getResultSet() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Statement.getResultSet", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.userRsetType == 1) {
            if (this.sqlKind == 0) {
                if (this.currentResultSet == null) {
                    this.currentResultSet = new OracleResultSetImpl(this.connection, this);
                }
                return this.currentResultSet;
            }
        } else {
            return this.scrollRset;
        }

        return null;
    }

    public synchronized int getUpdateCount() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Statement.getUpdateCount", this);

            OracleLog.recursiveTrace = false;
        }

        int l_updateCount = -1;

        switch (this.sqlKind) {
        case -1:
        case 0:
            break;
        case 3:
            if (!this.noMoreUpdateCounts) {
                l_updateCount = this.rowsProcessed;
            }
            this.noMoreUpdateCounts = true;

            break;
        case 1:
        case 4:
            this.noMoreUpdateCounts = true;

            break;
        case 2:
            if (!this.noMoreUpdateCounts) {
                l_updateCount = this.rowsProcessed;
            }
            this.noMoreUpdateCounts = true;
        }

        return l_updateCount;
    }

    public boolean getMoreResults() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "Statement.getMoreResults", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public int sendBatch() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.sendBatch()", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    void prepareForNewResults(boolean resetPrefetch, boolean clearStreamList) throws SQLException {
        clearWarnings();

        if (this.streamList != null) {
            while (this.nextStream != null) {
                try {
                    this.nextStream.close();
                } catch (IOException exc) {
                    DatabaseError.throwSqlException(exc);
                }

                this.nextStream = this.nextStream.nextStream;
            }

            if (clearStreamList) {
                OracleInputStream s = this.streamList;
                OracleInputStream prev = null;

                this.streamList = null;

                while (s != null) {
                    if (!s.hasBeenOpen) {
                        if (prev == null)
                            this.streamList = s;
                        else {
                            prev.nextStream = s;
                        }
                        prev = s;
                    }

                    s = s.nextStream;
                }
            }
        }

        if (this.currentResultSet != null) {
            this.currentResultSet.internal_close(true);

            this.currentResultSet = null;
        }

        this.currentRow = -1;
        this.validRows = 0;
        this.totalRowsVisited = 0;
        this.gotLastBatch = false;

        if ((this.needToParse) && (!this.columnsDefinedByUser)) {
            if ((clearStreamList) && (this.numberOfDefinePositions != 0)) {
                this.numberOfDefinePositions = 0;
            }
            this.needToPrepareDefineBuffer = true;
        }

        if ((resetPrefetch) && (this.rowPrefetch != this.defaultRowPrefetch)
                && (this.streamList == null)) {
            this.rowPrefetch = this.defaultRowPrefetch;
            this.rowPrefetchChanged = true;
        }
    }

    void reopenStreams() throws SQLException {
        OracleInputStream is = this.streamList;

        while (is != null) {
            if (is.hasBeenOpen) {
                is = is.accessor.initForNewRow();
            }
            is.closed = false;
            is.hasBeenOpen = true;
            is = is.nextStream;
        }

        this.nextStream = this.streamList;
    }

    void endOfResultSet(boolean dont_call_prepare_for_new_result) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.end_of_result_set()", this);

            OracleLog.recursiveTrace = false;
        }

        if (!dont_call_prepare_for_new_result) {
            prepareForNewResults(false, false);
        }

        clearDefines();
    }

    boolean wasNullValue() throws SQLException {
        if (this.lastIndex == 0) {
            DatabaseError.throwSqlException(24);
        }
        if (this.sqlKind == 0) {
            return this.accessors[(this.lastIndex - 1)].isNull(this.currentRow);
        }
        return this.outBindAccessors[(this.lastIndex - 1)].isNull(this.currentRank);
    }

    int getColumnIndex(String columnName) throws SQLException {
        if (!this.describedWithNames) {
            synchronized (this.connection) {
                synchronized (this) {
                    this.connection.needLine();
                    doDescribe(true);

                    this.described = true;
                    this.describedWithNames = true;
                }
            }
        }
        for (int index = 0; index < this.numberOfDefinePositions; index++) {
            if (this.accessors[index].columnName.equalsIgnoreCase(columnName))
                return index + 1;
        }
        DatabaseError.throwSqlException(6);

        return 0;
    }

    int getInternalType(int externalType) throws SQLException {
        int result = 0;

        switch (externalType) {
        case -7:
        case -6:
        case -5:
        case 2:
        case 3:
        case 4:
        case 5:
        case 6:
        case 7:
        case 8:
            result = 6;
            break;
        case 100:
            result = 100;
            break;
        case 101:
            result = 101;
            break;
        case 999:
            result = 999;
            break;
        case 1:
            result = 96;
            break;
        case 12:
            result = 1;
            break;
        case -1:
            result = 8;
            break;
        case 91:
        case 92:
            result = 12;
            break;
        case -100:
        case 93:
            result = 180;
            break;
        case -101:
            result = 181;
            break;
        case -102:
            result = 231;
            break;
        case -103:
            result = 182;
            break;
        case -104:
            result = 183;
            break;
        case -3:
        case -2:
            result = 23;
            break;
        case -4:
            result = 24;
            break;
        case -8:
            result = 104;
            break;
        case 2004:
            result = 113;
            break;
        case 2005:
            result = 112;
            break;
        case -13:
            result = 114;
            break;
        case -10:
            result = 102;
            break;
        case 2002:
        case 2003:
        case 2007:
        case 2008:
            result = 109;
            break;
        case 2006:
            result = 111;
            break;
        case -14:
            result = 998;
            break;
        case 70:
            result = 1;
            break;
        case 0:
            result = 995;
            break;
        default:
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "OracleStatement.getInternalType("
                        + externalType + ") error", this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(4);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OracleStatement.getInternalType("
                    + externalType + ") return " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    void describe() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                if (!this.described) {
                    this.connection.needLine();
                    doDescribe(false);
                }
            }
        }
    }

    void freeLine() throws SQLException {
        if (this.streamList != null) {
            while (this.nextStream != null) {
                try {
                    this.nextStream.close();
                } catch (IOException exc) {
                    DatabaseError.throwSqlException(exc);
                }

                this.nextStream = this.nextStream.nextStream;
            }
        }
    }

    void closeUsedStreams(int columnIndex) throws SQLException {
        while ((this.nextStream != null) && (this.nextStream.columnIndex < columnIndex)) {
            try {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER, "closeUsedStream(" + columnIndex
                            + ") closing " + this.nextStream + " at index "
                            + this.nextStream.columnIndex, this);

                    OracleLog.recursiveTrace = false;
                }

                this.nextStream.close();
            } catch (IOException exc) {
                DatabaseError.throwSqlException(exc);
            }

            this.nextStream = this.nextStream.nextStream;
        }
    }

    final void ensureOpen() throws SQLException {
        if (this.connection.lifecycle != 1)
            DatabaseError.throwSqlException(8);
        if (this.closed)
            DatabaseError.throwSqlException(9);
    }

    void allocateTmpByteArray() {
    }

    public synchronized void setFetchDirection(int direction) throws SQLException {
        if (direction == 1000) {
            this.defaultFetchDirection = direction;
        } else if ((direction == 1001) || (direction == 1002)) {
            this.defaultFetchDirection = 1000;
            this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 87);
        } else {
            DatabaseError.throwSqlException(68, "setFetchDirection");
        }
    }

    public int getFetchDirection() throws SQLException {
        return this.defaultFetchDirection;
    }

    public synchronized void setFetchSize(int rows) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.setFetchSize(rows=" + rows
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        setPrefetchInternal(rows, false, true);
    }

    public int getFetchSize() throws SQLException {
        return getPrefetchInternal(true);
    }

    public int getResultSetConcurrency() throws SQLException {
        return ResultSetUtil.getUpdateConcurrency(this.userRsetType);
    }

    public int getResultSetType() throws SQLException {
        return ResultSetUtil.getScrollType(this.userRsetType);
    }

    public Connection getConnection() throws SQLException {
        return this.connection.getWrapper();
    }

    public synchronized void setResultSetCache(oracle.jdbc.OracleResultSetCache cache)
            throws SQLException {
        try {
            if (cache == null) {
                DatabaseError.throwSqlException(68);
            }
            if (this.rsetCache != null) {
                this.rsetCache.close();
            }
            this.rsetCache = cache;
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
    }

    public synchronized void setResultSetCache(OracleResultSetCache cache) throws SQLException {
        setResultSetCache(cache);
    }

    public synchronized OracleResultSetCache getResultSetCache() throws SQLException {
        return (OracleResultSetCache) this.rsetCache;
    }

    void initBatch() {
    }

    int getBatchSize() {
        return this.m_batchItems.size();
    }

    void addBatchItem(String sql) {
        this.m_batchItems.addElement(sql);
    }

    String getBatchItem(int index) {
        return (String) this.m_batchItems.elementAt(index);
    }

    void clearBatchItems() {
        this.m_batchItems.removeAllElements();
    }

    void checkIfJdbcBatchExists() throws SQLException {
        if (getBatchSize() > 0) {
            DatabaseError.throwSqlException(81, "batch must be either executed or cleared");
        }
    }

    public synchronized void addBatch(String sql) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.addBatch(sql)", this);

            OracleLog.recursiveTrace = false;
        }

        addBatchItem(sql);
    }

    public synchronized void clearBatch() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.clearBatch()", this);

            OracleLog.recursiveTrace = false;
        }

        clearBatchItems();
    }

    public int[] executeBatch() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                cleanOldTempLobs();
                int i = 0;
                int n_batches = getBatchSize();

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.INFO,
                                               "OracleStatement.executeBatch(): batch size is "
                                                       + n_batches, this);

                    OracleLog.recursiveTrace = false;
                }

                if (n_batches <= 0) {
                    return new int[0];
                }

                int[] ret_array = new int[n_batches];

                ensureOpen();

                prepareForNewResults(true, true);

                int number_of_define_positions_saved = this.numberOfDefinePositions;
                String saved_sql = this.sqlObject.getOriginalSql();
                byte saved_sql_kind = this.sqlKind;

                this.noMoreUpdateCounts = false;

                int save_valid_rows = 0;
                try {
                    this.connection.needLine();

                    for (i = 0; i < n_batches; i++) {
                        this.sqlObject.initialize(getBatchItem(i));

                        this.sqlKind = this.sqlObject.getSqlKind();

                        this.needToParse = true;
                        this.numberOfDefinePositions = 0;

                        this.rowsProcessed = 0;
                        this.currentRank = 1;

                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.driverLogger.log(Level.FINER, "sql "
                                    + this.sqlObject.getSql(this.processEscapes,
                                                            this.convertNcharLiterals)
                                    + ", batch item " + i + ": sqlKind=" + this.sqlKind, this);

                            OracleLog.recursiveTrace = false;
                        }

                        if (this.sqlKind == 0) {
                            DatabaseError.throwBatchUpdateException(80,
                                                                    "invalid SELECT batch command "
                                                                            + i, i, ret_array);
                        }

                        if (!this.isOpen) {
                            this.connection.open(this);

                            this.isOpen = true;
                        }

                        int ret_val = -1;
                        try {
                            if (this.queryTimeout != 0) {
                                this.connection.getTimeout().setTimeout(this.queryTimeout * 1000,
                                                                        this);
                            }
                            this.isExecuting = true;

                            executeForRows(false);

                            if (this.validRows > 0) {
                                save_valid_rows += this.validRows;
                            }
                            ret_val = this.validRows;
                        } catch (SQLException ea) {
                            this.needToParse = true;
                            throw ea;
                        } finally {
                            if (this.queryTimeout != 0) {
                                this.connection.getTimeout().cancelTimeout();
                            }
                            this.validRows = save_valid_rows;

                            checkValidRowsStatus();

                            this.isExecuting = false;
                        }

                        if ((TRACE) && (!OracleLog.recursiveTrace)) {
                            OracleLog.recursiveTrace = true;
                            OracleLog.driverLogger.log(Level.FINE, "batch item " + i + ": return="
                                    + ret_val, this);

                            OracleLog.recursiveTrace = false;
                        }

                        ret_array[i] = ret_val;

                        if (ret_array[i] >= 0)
                            continue;
                        DatabaseError.throwBatchUpdateException(81, "command return value "
                                + ret_array[i], i, ret_array);
                    }

                } catch (SQLException e1) {
                    if ((e1 instanceof BatchUpdateException)) {
                        throw e1;
                    }

                    DatabaseError.throwBatchUpdateException(81, e1.getMessage(), i, ret_array);
                } finally {
                    clearBatchItems();

                    this.numberOfDefinePositions = number_of_define_positions_saved;

                    if (saved_sql != null) {
                        this.sqlObject.initialize(saved_sql);

                        this.sqlKind = saved_sql_kind;
                    }

                    this.currentRank = 0;
                }

                return ret_array;
            }
        }
    }

    public int copyBinds(Statement toStmt, int offset) throws SQLException {
        return 0;
    }

    public void notifyCloseRset() throws SQLException {
        this.scrollRset = null;

        endOfResultSet(false);
    }

    public String getOriginalSql() throws SQLException {
        return this.sqlObject.getOriginalSql();
    }

    void doScrollExecuteCommon() throws SQLException {
        if (this.scrollRset != null) {
            this.scrollRset.close();

            this.scrollRset = null;
        }

        if (this.sqlKind != 0) {
            doExecuteWithTimeout();

            return;
        }

        if (!this.needToAddIdentifier) {
            doExecuteWithTimeout();

            this.currentResultSet = new OracleResultSetImpl(this.connection, this);
            this.realRsetType = this.userRsetType;
        } else {
            try {
                this.sqlObject.setIncludeRowid(true);

                this.needToParse = true;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.CONFIG, "revised SQL:<"
                            + this.sqlObject.getSql(this.processEscapes, this.convertNcharLiterals)
                            + ">", this);

                    OracleLog.recursiveTrace = false;
                }

                prepareForNewResults(true, false);

                if (this.columnsDefinedByUser) {
                    Accessor[] oldaccs = this.accessors;

                    if ((this.accessors == null)
                            || (this.accessors.length <= this.numberOfDefinePositions)) {
                        this.accessors = new Accessor[this.numberOfDefinePositions + 1];
                    }
                    if (oldaccs != null) {
                        for (int i = this.numberOfDefinePositions; i > 0; i--) {
                            Accessor accessor = oldaccs[(i - 1)];

                            this.accessors[i] = accessor;

                            if (!accessor.isColumnNumberAware) {
                                continue;
                            }

                            accessor.updateColumnNumber(i);
                        }

                    }

                    allocateRowidAccessor();

                    this.numberOfDefinePositions += 1;
                }

                doExecuteWithTimeout();

                this.currentResultSet = new OracleResultSetImpl(this.connection, this);
                this.realRsetType = this.userRsetType;
            } catch (SQLException e) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    OracleLog.driverLogger.log(Level.FINEST, sw.toString(), this);

                    OracleLog.recursiveTrace = false;
                }

                if (this.userRsetType > 3)
                    this.realRsetType = 3;
                else {
                    this.realRsetType = 1;
                }
                this.sqlObject.setIncludeRowid(false);

                this.needToParse = true;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER, "Trying type " + this.realRsetType
                            + "...", this);

                    OracleLog.recursiveTrace = false;
                }

                prepareForNewResults(true, false);

                if (this.columnsDefinedByUser) {
                    this.needToPrepareDefineBuffer = true;
                    this.numberOfDefinePositions -= 1;

                    System.arraycopy(this.accessors, 1, this.accessors, 0,
                                     this.numberOfDefinePositions);

                    this.accessors[this.numberOfDefinePositions] = null;

                    for (int i = 0; i < this.numberOfDefinePositions; i++) {
                        Accessor accessor = this.accessors[i];

                        if (!accessor.isColumnNumberAware) {
                            continue;
                        }

                        accessor.updateColumnNumber(i);
                    }
                }

                doExecuteWithTimeout();

                this.currentResultSet = new OracleResultSetImpl(this.connection, this);
                this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 91, e.getMessage());
            }

        }

        this.scrollRset = ResultSetUtil.createScrollResultSet(this, this.currentResultSet,
                                                              this.realRsetType);
    }

    void allocateRowidAccessor() throws SQLException {
        this.accessors[0] = new RowidAccessor(this, 128, (short) 1, -8, false);
    }

    OracleResultSet doScrollStmtExecuteQuery() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.doScrollStmtExecuteQuery()",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        doScrollExecuteCommon();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.doScrollStmtExecuteQuery():return", this);

            OracleLog.recursiveTrace = false;
        }

        return this.scrollRset;
    }

    void processDmlReturningBind() throws SQLException {
        if (this.returnResultSet != null)
            this.returnResultSet.close();

        this.returnParamsFetched = false;
        this.returnParamRowBytes = 0;
        this.returnParamRowChars = 0;

        int count = 0;
        for (int i = 0; i < this.numberOfBindPositions; i++) {
            Accessor accessor = this.returnParamAccessors[i];

            if (accessor == null)
                continue;
            count++;

            if (accessor.charLength > 0) {
                this.returnParamRowChars += accessor.charLength;
            } else {
                this.returnParamRowBytes += accessor.byteLength;
            }

        }

        if (this.isAutoGeneratedKey) {
            this.numReturnParams = count;
        } else {
            if (this.numReturnParams <= 0) {
                this.numReturnParams = OraclePreparedStatement
                        .getReturnParameterCount(this.sqlObject.getOriginalSql());
            }

            if (this.numReturnParams != count) {
                DatabaseError.throwSqlException(173);
            }

        }

        this.returnParamMeta[0] = this.numReturnParams;
        this.returnParamMeta[1] = this.returnParamRowBytes;
        this.returnParamMeta[2] = this.returnParamRowChars;
    }

    void allocateDmlReturnStorage() {
        if (this.rowsDmlReturned == 0)
            return;

        int totalBytes = this.returnParamRowBytes * this.rowsDmlReturned;
        int totalChars = this.returnParamRowChars * this.rowsDmlReturned;
        int indicatorLength = 2 * this.numReturnParams * this.rowsDmlReturned;

        this.returnParamBytes = new byte[totalBytes];
        this.returnParamChars = new char[totalChars];
        this.returnParamIndicators = new short[indicatorLength];

        for (int i = 0; i < this.numberOfBindPositions; i++) {
            Accessor accessor = this.returnParamAccessors[i];

            if ((accessor == null)
                    || ((accessor.internalType != 111) && (accessor.internalType != 109))) {
                continue;
            }
            TypeAccessor typeAccessor = (TypeAccessor) accessor;
            if ((typeAccessor.pickledBytes != null)
                    && (typeAccessor.pickledBytes.length >= this.rowsDmlReturned))
                continue;
            typeAccessor.pickledBytes = new byte[this.rowsDmlReturned][];
        }
    }

    void fetchDmlReturnParams() throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    void setupReturnParamAccessors() {
        if (this.rowsDmlReturned == 0)
            return;

        int byteOffset = 0;
        int charOffset = 0;
        int indOffset = 0;
        int lenOffset = this.numReturnParams * this.rowsDmlReturned;

        for (int i = 0; i < this.numberOfBindPositions; i++) {
            Accessor accessor = this.returnParamAccessors[i];

            if (accessor == null)
                continue;
            if (accessor.charLength > 0) {
                accessor.rowSpaceChar = this.returnParamChars;
                accessor.columnIndex = charOffset;
                charOffset += this.rowsDmlReturned * accessor.charLength;
            } else {
                accessor.rowSpaceByte = this.returnParamBytes;
                accessor.columnIndex = byteOffset;
                byteOffset += this.rowsDmlReturned * accessor.byteLength;
            }

            accessor.rowSpaceIndicator = this.returnParamIndicators;
            accessor.indicatorIndex = indOffset;
            indOffset += this.rowsDmlReturned;
            accessor.lengthIndex = lenOffset;
            lenOffset += this.rowsDmlReturned;
        }
    }

    void registerReturnParameterInternal(int index, int internalType, int externalType,
            int maxSize, short form, String typeName) throws SQLException {
        if (this.returnParamAccessors == null) {
            this.returnParamAccessors = new Accessor[this.numberOfBindPositions];
        }
        if (this.returnParamMeta == null) {
            this.returnParamMeta = new int[3 + this.numberOfBindPositions * 3];
        }

        Accessor accessor = allocateAccessor(internalType, externalType, index + 1, maxSize, form,
                                             typeName, true);

        accessor.isDMLReturnedParam = true;
        this.returnParamAccessors[index] = accessor;

        boolean isCharType = accessor.charLength > 0;

        this.returnParamMeta[(3 + index * 3 + 0)] = accessor.defineType;

        this.returnParamMeta[(3 + index * 3 + 1)] = (isCharType ? 1 : 0);

        this.returnParamMeta[(3 + index * 3 + 2)] = (isCharType ? accessor.charLength
                : accessor.byteLength);
    }

    public void setAutoRefetch(boolean autoRefetch) throws SQLException {
        this.autoRefetch = autoRefetch;
    }

    public boolean getAutoRefetch() throws SQLException {
        return this.autoRefetch;
    }

    /** @deprecated */
    public synchronized int creationState() {
        return this.creationState;
    }

    public boolean isColumnSetNull(int index) {
        return this.columnSetNull;
    }

    public boolean isNCHAR(int columnIndex) throws SQLException {
        if (!this.described) {
            describe();
        }
        int index = columnIndex - 1;
        if ((index < 0) || (index >= this.numberOfDefinePositions)) {
            DatabaseError.throwSqlException(3);
        }
        boolean result = this.accessors[index].formOfUse == 2;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "isNCHAR (columnIndex = " + columnIndex + ")  "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    void addChild(OracleStatement child) {
        child.nextChild = this.children;
        this.children = child;
    }

    public boolean getMoreResults(int current) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return false;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleStatement.getGeneratedKeys()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            DatabaseError.throwSqlException(9);
        }
        if (!this.isAutoGeneratedKey) {
            DatabaseError.throwSqlException(90);
        }
        if ((this.returnParamAccessors == null) || (this.numReturnParams == 0)) {
            DatabaseError.throwSqlException(144);
        }
        if (this.returnResultSet == null) {
            this.returnResultSet = new OracleReturnResultSet(this);
        }

        return this.returnResultSet;
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.executeUpdate(" + sql + ", "
                    + autoGeneratedKeys + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((autoGeneratedKeys == 2) || (!AutoKeyInfo.isInsertSqlStmt(sql))) {
            return executeUpdate(sql);
        }
        if (autoGeneratedKeys != 1) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (this.connection) {
            synchronized (this) {
                this.isAutoGeneratedKey = true;
                this.autoKeyInfo = new AutoKeyInfo(sql);
                String newSql = this.autoKeyInfo.getNewSql();
                this.numberOfBindPositions = 1;

                autoKeyRegisterReturnParams();

                processDmlReturningBind();

                return executeUpdateInternal(newSql);
            }
        }
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.executeUpdate(" + sql
                    + ", columnIndexes[])", this);

            OracleLog.recursiveTrace = false;
        }

        if (!AutoKeyInfo.isInsertSqlStmt(sql))
            return executeUpdate(sql);

        if ((columnIndexes == null) || (columnIndexes.length == 0)) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (this.connection) {
            synchronized (this) {
                this.isAutoGeneratedKey = true;
                this.autoKeyInfo = new AutoKeyInfo(sql, columnIndexes);

                this.connection.doDescribeTable(this.autoKeyInfo);

                String newSql = this.autoKeyInfo.getNewSql();
                this.numberOfBindPositions = columnIndexes.length;

                autoKeyRegisterReturnParams();

                processDmlReturningBind();

                return executeUpdateInternal(newSql);
            }
        }
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.executeUpdate(" + sql
                    + ", columnNames[])", this);

            OracleLog.recursiveTrace = false;
        }

        if (!AutoKeyInfo.isInsertSqlStmt(sql))
            return executeUpdate(sql);

        if ((columnNames == null) || (columnNames.length == 0)) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (this.connection) {
            synchronized (this) {
                this.isAutoGeneratedKey = true;
                this.autoKeyInfo = new AutoKeyInfo(sql, columnNames);

                this.connection.doDescribeTable(this.autoKeyInfo);

                String newSql = this.autoKeyInfo.getNewSql();
                this.numberOfBindPositions = columnNames.length;

                autoKeyRegisterReturnParams();

                processDmlReturningBind();

                return executeUpdateInternal(newSql);
            }
        }
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.execute(" + sql + ", "
                    + autoGeneratedKeys + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((autoGeneratedKeys == 2) || (!AutoKeyInfo.isInsertSqlStmt(sql))) {
            return execute(sql);
        }
        if (autoGeneratedKeys != 1) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (this.connection) {
            synchronized (this) {
                this.isAutoGeneratedKey = true;
                this.autoKeyInfo = new AutoKeyInfo(sql);
                String newSql = this.autoKeyInfo.getNewSql();
                this.numberOfBindPositions = 1;

                autoKeyRegisterReturnParams();

                processDmlReturningBind();

                return executeInternal(newSql);
            }
        }
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.execute(" + sql
                    + ", columnIndexes[])", this);

            OracleLog.recursiveTrace = false;
        }

        if (!AutoKeyInfo.isInsertSqlStmt(sql))
            return execute(sql);

        if ((columnIndexes == null) || (columnIndexes.length == 0)) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (this.connection) {
            synchronized (this) {
                this.isAutoGeneratedKey = true;
                this.autoKeyInfo = new AutoKeyInfo(sql, columnIndexes);

                this.connection.doDescribeTable(this.autoKeyInfo);

                String newSql = this.autoKeyInfo.getNewSql();
                this.numberOfBindPositions = columnIndexes.length;

                autoKeyRegisterReturnParams();

                processDmlReturningBind();

                return executeInternal(newSql);
            }
        }
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleStatement.execute(" + sql
                    + ", columnNames[])", this);

            OracleLog.recursiveTrace = false;
        }

        if (!AutoKeyInfo.isInsertSqlStmt(sql))
            return execute(sql);

        if ((columnNames == null) || (columnNames.length == 0)) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (this.connection) {
            synchronized (this) {
                this.isAutoGeneratedKey = true;
                this.autoKeyInfo = new AutoKeyInfo(sql, columnNames);

                this.connection.doDescribeTable(this.autoKeyInfo);

                String newSql = this.autoKeyInfo.getNewSql();
                this.numberOfBindPositions = columnNames.length;

                autoKeyRegisterReturnParams();

                processDmlReturningBind();

                return executeInternal(newSql);
            }
        }
    }

    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    public int getcacheState() {
        return this.cacheState;
    }

    public int getstatementType() {
        return this.statementType;
    }

    public boolean getserverCursor() {
        return this.serverCursor;
    }

    void initializeIndicatorSubRange() {
        this.bindIndicatorSubRange = 0;
    }

    private void autoKeyRegisterReturnParams() throws SQLException {
        initializeIndicatorSubRange();

        int preambleSize = this.bindIndicatorSubRange + 3 + this.numberOfBindPositions * 10;

        int indicatorSize = preambleSize + 2 * this.numberOfBindPositions;

        this.bindIndicators = new short[indicatorSize];

        int metadataOffset = this.bindIndicatorSubRange;

        this.bindIndicators[(metadataOffset + 0)] = (short) this.numberOfBindPositions;

        this.bindIndicators[(metadataOffset + 1)] = 1;

        this.bindIndicators[(metadataOffset + 2)] = 1;

        metadataOffset += 3;

        short[] formOfUses = this.autoKeyInfo.tableFormOfUses;
        int[] columnIndexes = this.autoKeyInfo.columnIndexes;

        for (int i = 0; i < this.numberOfBindPositions; i++) {
            this.bindIndicators[(metadataOffset + 0)] = 994;

            short form = 0;

            if ((formOfUses != null) && (columnIndexes != null)) {
                if (formOfUses[(columnIndexes[i] - 1)] == 2) {
                    form = 2;
                    this.bindIndicators[(metadataOffset + 9)] = form;
                }

            }

            metadataOffset += 10;

            checkTypeForAutoKey(this.autoKeyInfo.returnTypes[i]);

            String typeName = null;
            if (this.autoKeyInfo.returnTypes[i] == 111) {
                typeName = this.autoKeyInfo.tableTypeNames[(columnIndexes[i] - 1)];
            }

            registerReturnParameterInternal(i, this.autoKeyInfo.returnTypes[i],
                                            this.autoKeyInfo.returnTypes[i], -1, form, typeName);
        }
    }

    private final void setNonAutoKey() {
        this.isAutoGeneratedKey = false;
        this.numberOfBindPositions = 0;
        this.bindIndicators = null;
    }

    void saveDefineBuffersIfRequired(char[] tmpDefineChars, byte[] tmpDefineBytes,
            short[] tmpDefineIndicators, boolean isIndicatorsReused) throws SQLException {
    }

    final void checkTypeForAutoKey(int type) throws SQLException {
        if (type == 109)
            DatabaseError.throwSqlException(5);
    }

    void addToTempLobsToFree(CLOB tclob) {
        if (this.tempClobsToFree == null)
            this.tempClobsToFree = new ArrayList();
        this.tempClobsToFree.add(tclob);
    }

    void addToTempLobsToFree(BLOB tblob) {
        if (this.tempBlobsToFree == null)
            this.tempBlobsToFree = new ArrayList();
        this.tempBlobsToFree.add(tblob);
    }

    void addToOldTempLobsToFree(CLOB tclob) {
        if (this.oldTempClobsToFree == null)
            this.oldTempClobsToFree = new ArrayList();
        this.oldTempClobsToFree.add(tclob);
    }

    void addToOldTempLobsToFree(BLOB tblob) {
        if (this.oldTempBlobsToFree == null)
            this.oldTempBlobsToFree = new ArrayList();
        this.oldTempBlobsToFree.add(tblob);
    }

    void cleanAllTempLobs() {
        cleanTempClobs(this.tempClobsToFree);
        this.tempClobsToFree = null;
        cleanTempBlobs(this.tempBlobsToFree);
        this.tempBlobsToFree = null;
        cleanTempClobs(this.oldTempClobsToFree);
        this.oldTempClobsToFree = null;
        cleanTempBlobs(this.oldTempBlobsToFree);
        this.oldTempBlobsToFree = null;
    }

    void cleanOldTempLobs() {
        cleanTempClobs(this.oldTempClobsToFree);
        cleanTempBlobs(this.oldTempBlobsToFree);
        this.oldTempClobsToFree = this.tempClobsToFree;
        this.tempClobsToFree = null;
        this.oldTempBlobsToFree = this.tempBlobsToFree;
        this.tempBlobsToFree = null;
    }

    void cleanTempClobs(ArrayList x) {
        if (x != null) {
            Iterator iter = x.iterator();

            while (iter.hasNext()) {
                try {
                    ((CLOB) iter.next()).freeTemporary();
                } catch (SQLException e) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.FINER,
                                                   "OracleStatement.OracleStatement.cleanTempClobs exception "
                                                           + e.getMessage(), this);

                        OracleLog.recursiveTrace = false;
                    }
                }
            }
        }
    }

    void cleanTempBlobs(ArrayList x) {
        if (x != null) {
            Iterator iter = x.iterator();

            while (iter.hasNext()) {
                try {
                    ((BLOB) iter.next()).freeTemporary();
                } catch (SQLException e) {
                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.FINER,
                                                   "OracleStatement.OracleStatement.cleanTempBlobs exception "
                                                           + e.getMessage(), this);

                        OracleLog.recursiveTrace = false;
                    }
                }
            }
        }
    }

    TimeZone getDefaultTimeZone() {
        if (this.defaultTZ == null) {
            this.defaultTZ = TimeZone.getDefault();
        }
        return this.defaultTZ;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleStatement"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleStatement JD-Core Version: 0.6.0
 */