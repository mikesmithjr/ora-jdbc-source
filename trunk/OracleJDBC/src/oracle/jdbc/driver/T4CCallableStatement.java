package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class T4CCallableStatement extends OracleCallableStatement {
    static final byte[] EMPTY_BYTE = new byte[0];
    T4CConnection t4Connection;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4CCallableStatement(PhysicalConnection conn, String sql, int resultSetType,
            int resultSetConcurrency) throws SQLException {
        super(conn, sql, conn.defaultBatch, conn.defaultRowPrefetch, resultSetType,
                resultSetConcurrency);

        this.t4Connection = ((T4CConnection) conn);

        this.theRowidBinder = theStaticT4CRowidBinder;
        this.theRowidNullBinder = theStaticT4CRowidNullBinder;
    }

    void doOall8(boolean doParse, boolean doExecute, boolean doFetch, boolean doDescribe)
            throws SQLException, IOException {
        if ((doParse) || (doDescribe) || (!doExecute)
                || ((this.sqlKind != 2) && (this.sqlKind != 1) && (this.sqlKind != 4))) {
            this.oacdefSent = null;
        }
        this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CCallableStatement.doOall8");

        if ((this.sqlKind != 1) && (this.sqlKind != 4) && (this.sqlKind != 3)
                && (this.sqlKind != 0) && (this.sqlKind != 2)) {
            DatabaseError.throwSqlException(439);
        }

        int number_of_define_positions_local = this.numberOfDefinePositions;

        if (this.sqlKind == 2) {
            number_of_define_positions_local = 0;
        }

        if (doFetch) {
            if (this.accessors != null) {
                for (int i = 0; i < this.numberOfDefinePositions; i++) {
                    if (this.accessors[i] == null)
                        continue;
                    this.accessors[i].lastRowProcessed = 0;
                }

            }

            if (this.outBindAccessors != null) {
                for (int i = 0; i < this.outBindAccessors.length; i++) {
                    if (this.outBindAccessors[i] == null)
                        continue;
                    this.outBindAccessors[i].lastRowProcessed = 0;
                }
            }

        }

        if (this.returnParamAccessors != null) {
            for (int i = 0; i < this.numberOfBindPositions; i++) {
                if (this.returnParamAccessors[i] == null)
                    continue;
                this.returnParamAccessors[i].lastRowProcessed = 0;
            }

        }

        if (this.bindIndicators != null) {
            int number_of_bound_rows = this.bindIndicators[(this.bindIndicatorSubRange + 2)] & 0xFFFF;

            int maxNbBytes = 0;

            if (this.ibtBindChars != null) {
                maxNbBytes = this.ibtBindChars.length * this.connection.conversion.cMaxCharSize;
            }
            for (int P = 0; P < this.numberOfBindPositions; P++) {
                int subRangeOffset = this.bindIndicatorSubRange + 3 + 10 * P;

                int charPitch = this.bindIndicators[(subRangeOffset + 2)] & 0xFFFF;

                if (charPitch == 0) {
                    continue;
                }
                int formOfUse = this.bindIndicators[(subRangeOffset + 9)] & 0xFFFF;

                if (formOfUse == 2) {
                    maxNbBytes = Math.max(charPitch * this.connection.conversion.maxNCharSize,
                                          maxNbBytes);
                } else {
                    maxNbBytes = Math.max(charPitch * this.connection.conversion.cMaxCharSize,
                                          maxNbBytes);
                }

            }

            if (this.tmpBindsByteArray == null) {
                this.tmpBindsByteArray = new byte[maxNbBytes];
            } else if (this.tmpBindsByteArray.length < maxNbBytes) {
                this.tmpBindsByteArray = null;
                this.tmpBindsByteArray = new byte[maxNbBytes];
            }

        } else {
            this.tmpBindsByteArray = null;
        }

        allocateTmpByteArray();

        T4C8Oall all8 = this.t4Connection.all8;

        this.t4Connection.sendPiggyBackedMessages();

        this.oacdefSent = all8.marshal(doParse, doExecute, doFetch, doDescribe, this.sqlKind,
                                       this.cursorId, this.sqlObject
                                               .getSqlBytes(this.processEscapes,
                                                            this.convertNcharLiterals),
                                       this.rowPrefetch, this.outBindAccessors,
                                       this.numberOfBindPositions, this.accessors,
                                       number_of_define_positions_local, this.bindBytes,
                                       this.bindChars, this.bindIndicators,
                                       this.bindIndicatorSubRange, this.connection.conversion,
                                       this.tmpBindsByteArray, this.parameterStream,
                                       this.parameterDatum, this.parameterOtype, this,
                                       this.ibtBindBytes, this.ibtBindChars,
                                       this.ibtBindIndicators, this.oacdefSent,
                                       this.definedColumnType, this.definedColumnSize,
                                       this.definedColumnFormOfUse);
        try {
            all8.receive();

            this.cursorId = all8.getCursorId();
        } catch (SQLException ea) {
            this.cursorId = all8.getCursorId();

            if (ea.getErrorCode() == DatabaseError.getVendorCode(110)) {
                this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 110);
            } else {
                throw ea;
            }
        }
    }

    void allocateTmpByteArray() {
        if (this.tmpByteArray == null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.FINER,
                                         "oracle.jdbc.driver.T4CCallableStatement.allocateTmpByteArray : allocate byte array of size : "
                                                 + this.sizeTmpByteArray, this);

                OracleLog.recursiveTrace = false;
            }

            this.tmpByteArray = new byte[this.sizeTmpByteArray];
        } else if (this.sizeTmpByteArray > this.tmpByteArray.length) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CCallableStatement.allocateTmpByteArray : Re-allocate byte array of size : "
                                                 + this.sizeTmpByteArray, this);

                OracleLog.recursiveTrace = false;
            }

            this.tmpByteArray = new byte[this.sizeTmpByteArray];
        } else if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(
                         Level.FINER,
                         "oracle.jdbc.driver.T4CCallableStatement.allocateTmpByteArray : don't re-allocate byte array of size="
                                 + this.sizeTmpByteArray
                                 + ". Current size is="
                                 + this.tmpByteArray.length, this);

            OracleLog.recursiveTrace = false;
        }
    }

    void allocateRowidAccessor() throws SQLException {
        this.accessors[0] = new T4CRowidAccessor(this, 128, (short) 1, -8, false, this.t4Connection.mare);
    }

    void reparseOnRedefineIfNeeded() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINER,
                         "oracle.jdbc.driver.T4CCallableStatement.reparesOrRedefineIfNeeded", this);

            OracleLog.recursiveTrace = false;
        }

        this.needToParse = true;
    }

    protected void defineColumnTypeInternal(int column_index, int type, int size, short form,
            boolean sizeNotGiven, String typeName) throws SQLException {
        if (this.connection.disableDefineColumnType) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.FINE,
                                         "T4CStatement.defineColumnTypeInternal--DISABLED", this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if (column_index < 1) {
            DatabaseError.throwSqlException(3);
        }
        if (sizeNotGiven) {
            if ((type == 1) || (type == 12)) {
                this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 108);
            }

        } else if (size < 0) {
            DatabaseError.throwSqlException(53);
        }

        if ((this.currentResultSet != null) && (!this.currentResultSet.closed)) {
            DatabaseError.throwSqlException(28);
        }

        int idx = column_index - 1;

        if ((this.definedColumnType == null) || (this.definedColumnType.length <= idx)) {
            if (this.definedColumnType == null) {
                this.definedColumnType = new int[(idx + 1) * 4];
            } else {
                int[] n_definedColumnType = new int[(idx + 1) * 4];

                System.arraycopy(this.definedColumnType, 0, n_definedColumnType, 0,
                                 this.definedColumnType.length);

                this.definedColumnType = n_definedColumnType;
            }
        }

        this.definedColumnType[idx] = type;

        if ((this.definedColumnSize == null) || (this.definedColumnSize.length <= idx)) {
            if (this.definedColumnSize == null) {
                this.definedColumnSize = new int[(idx + 1) * 4];
            } else {
                int[] n_definedColumnSize = new int[(idx + 1) * 4];

                System.arraycopy(this.definedColumnSize, 0, n_definedColumnSize, 0,
                                 this.definedColumnSize.length);

                this.definedColumnSize = n_definedColumnSize;
            }
        }

        this.definedColumnSize[idx] = size;

        if ((this.definedColumnFormOfUse == null) || (this.definedColumnFormOfUse.length <= idx)) {
            if (this.definedColumnFormOfUse == null) {
                this.definedColumnFormOfUse = new int[(idx + 1) * 4];
            } else {
                int[] n_definedColumnFormOfUse = new int[(idx + 1) * 4];

                System.arraycopy(this.definedColumnFormOfUse, 0, n_definedColumnFormOfUse, 0,
                                 this.definedColumnFormOfUse.length);

                this.definedColumnFormOfUse = n_definedColumnFormOfUse;
            }
        }

        this.definedColumnFormOfUse[idx] = form;

        if ((this.accessors != null) && (idx < this.accessors.length)
                && (this.accessors[idx] != null)) {
            this.accessors[idx].definedColumnSize = size;

            if (((this.accessors[idx].internalType == 96) || (this.accessors[idx].internalType == 1))
                    && ((type == 1) || (type == 12))) {
                if (size <= this.accessors[idx].oacmxl) {
                    this.needToPrepareDefineBuffer = true;
                    this.columnsDefinedByUser = true;

                    this.accessors[idx].initForDataAccess(type, size, null);
                    this.accessors[idx].calculateSizeTmpByteArray();
                }
            }
        }
    }

    public synchronized void clearDefines() throws SQLException {
        super.clearDefines();

        this.definedColumnType = null;
        this.definedColumnSize = null;
        this.definedColumnFormOfUse = null;
    }

    void saveDefineBuffersIfRequired(char[] tmpDefineChars, byte[] tmpDefineBytes,
            short[] tmpDefineIndicators, boolean isIndicatorsReused) throws SQLException {
        if (isIndicatorsReused) {
            tmpDefineIndicators = new short[this.defineIndicators.length];
            int lengthIndex = this.accessors[0].lengthIndexLastRow;
            int indicatorIndex = this.accessors[0].indicatorIndexLastRow;

            for (int i = 1; i <= this.accessors.length; i++) {
                int length = lengthIndex + this.saved_rowPrefetch * i - 1;
                int indicator = indicatorIndex + this.saved_rowPrefetch * i - 1;
                tmpDefineIndicators[indicator] = this.defineIndicators[indicator];
                tmpDefineIndicators[length] = this.defineIndicators[length];
            }

        }

        for (int i = 0; i < this.accessors.length; i++) {
            this.accessors[i]
                    .saveDataFromOldDefineBuffers(
                                                  tmpDefineBytes,
                                                  tmpDefineChars,
                                                  tmpDefineIndicators,
                                                  this.saved_rowPrefetch != -1 ? this.saved_rowPrefetch
                                                          : this.rowPrefetch, this.rowPrefetch);
        }
    }

    Accessor allocateAccessor(int internal_type, int external_type, int col_index, int max_len,
            short form, String typeName, boolean forBind) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.allocateAccessor("
                                             + internal_type + ", " + external_type + ", "
                                             + max_len + ", " + form + ", " + typeName + ", "
                                             + forBind + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Accessor result = null;

        switch (internal_type) {
        case 96:
            result = new T4CCharAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 8:
            if (forBind)
                break;
            result = new T4CLongAccessor(this, col_index, max_len, form, external_type,
                    this.t4Connection.mare);

            break;
        case 1:
            result = new T4CVarcharAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 2:
            result = new T4CNumberAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 6:
            result = new T4CVarnumAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 24:
            if (!forBind) {
                result = new T4CLongRawAccessor(this, col_index, max_len, form, external_type,
                        this.t4Connection.mare);
            }

            break;
        case 23:
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            if (forBind) {
                result = new T4COutRawAccessor(this, max_len, form, external_type,
                        this.t4Connection.mare);
            } else {
                result = new T4CRawAccessor(this, max_len, form, external_type, forBind,
                        this.t4Connection.mare);
            }

            break;
        case 100:
            result = new T4CBinaryFloatAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 101:
            result = new T4CBinaryDoubleAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 104:
            result = new T4CRowidAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 102:
            result = new T4CResultSetAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 12:
            result = new T4CDateAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 113:
            result = new T4CBlobAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 112:
            result = new T4CClobAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 114:
            result = new T4CBfileAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 109:
            result = new T4CNamedTypeAccessor(this, typeName, form, external_type, forBind,
                    this.t4Connection.mare);

            result.initMetadata();

            break;
        case 111:
            result = new T4CRefTypeAccessor(this, typeName, form, external_type, forBind,
                    this.t4Connection.mare);

            result.initMetadata();

            break;
        case 180:
            if (this.connection.v8Compatible) {
                result = new T4CDateAccessor(this, max_len, form, external_type, forBind,
                        this.t4Connection.mare);
            } else {
                result = new T4CTimestampAccessor(this, max_len, form, external_type, forBind,
                        this.t4Connection.mare);
            }

            break;
        case 181:
            result = new T4CTimestamptzAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 231:
            result = new T4CTimestampltzAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 182:
            result = new T4CIntervalymAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 183:
            result = new T4CIntervaldsAccessor(this, max_len, form, external_type, forBind,
                    this.t4Connection.mare);

            break;
        case 995:
            DatabaseError.throwSqlException(89);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.SEVERE,
                                     "oracle.jdbc.driver.T4CCallableStatement.allocateAccessors:return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    void doDescribe(boolean includeNames) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.do_describe("
                                             + includeNames + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.isOpen) {
            DatabaseError.throwSqlException(144);
        }

        try {
            this.t4Connection.sendPiggyBackedMessages();
            this.t4Connection.describe.init(this, 0);
            this.t4Connection.describe.marshal();

            this.accessors = this.t4Connection.describe.receive(this.accessors);
            this.numberOfDefinePositions = this.t4Connection.describe.numuds;

            for (int i = 0; i < this.numberOfDefinePositions; i++) {
                this.accessors[i].initMetadata();
            }

        } catch (IOException ex) {
            ((T4CConnection) this.connection).handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        this.describedWithNames = true;
        this.described = true;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.do_describe:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    void executeForDescribe() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CCallableStatement.execute_for_describe()", this);

            OracleLog.recursiveTrace = false;
        }

        this.t4Connection
                .assertLoggedOn("oracle.jdbc.driver.T4CCallableStatement.execute_for_describe");
        cleanOldTempLobs();
        try {
            if (this.t4Connection.useFetchSizeWithLongColumn) {
                doOall8(true, true, true, true);
            } else {
                doOall8(true, true, false, true);
            }

        } catch (SQLException e) {
            throw e;
        } catch (IOException e) {
            ((T4CConnection) this.connection).handleIOException(e);
            DatabaseError.throwSqlException(e);
        } finally {
            this.rowsProcessed = this.t4Connection.all8.rowsProcessed;
            this.validRows = this.t4Connection.all8.getNumRows();
        }

        this.needToParse = false;

        for (int i = 0; i < this.numberOfDefinePositions; i++) {
            this.accessors[i].initMetadata();
        }
        this.needToPrepareDefineBuffer = false;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CCallableStatement.execute_for_describe:return",
                         this);

            OracleLog.recursiveTrace = false;
        }
    }

    void executeMaybeDescribe() throws SQLException {
        if (!this.t4Connection.useFetchSizeWithLongColumn) {
            super.executeMaybeDescribe();
        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINE,
                                           "T4C__Statement.execute_maybe_describe() rowPrefetchChanged = "
                                                   + this.rowPrefetchChanged + ", needToParse = "
                                                   + this.needToParse
                                                   + ", needToPrepareDefineBuffer = "
                                                   + this.needToPrepareDefineBuffer
                                                   + ", columnsDefinedByUser = "
                                                   + this.columnsDefinedByUser, this);

                OracleLog.recursiveTrace = false;
            }

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
                    executeForDescribe();

                    executed_for_describe = true;
                } else {
                    int len = this.accessors.length;

                    for (int i = this.numberOfDefinePositions; i < len; i++) {
                        Accessor accessor = this.accessors[i];

                        if (accessor != null) {
                            accessor.rowSpaceIndicator = null;
                        }
                    }
                    executeForRows(executed_for_describe);
                }

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
                                                   + this.validRows
                                                   + ", needToPrepareDefineBuffer = "
                                                   + this.needToPrepareDefineBuffer + " ", this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    void executeForRows(boolean executed_for_describe) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.execute_for_rows("
                                             + executed_for_describe + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            try {
                doOall8(this.needToParse, !executed_for_describe, true, false);

                this.needToParse = false;
            } finally {
                this.validRows = this.t4Connection.all8.getNumRows();
            }

        } catch (SQLException e) {
            throw e;
        } catch (IOException e) {
            ((T4CConnection) this.connection).handleIOException(e);
            DatabaseError.throwSqlException(e);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.execute_for_rows:return: validRows = "
                                             + this.validRows, this);

            OracleLog.recursiveTrace = false;
        }
    }

    void fetch() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CCallableStatement.fetch()",
                                     this);

            OracleLog.recursiveTrace = false;
        }

        if (this.streamList != null) {
            while (this.nextStream != null) {
                try {
                    this.nextStream.close();
                } catch (IOException exc) {
                    ((T4CConnection) this.connection).handleIOException(exc);
                    DatabaseError.throwSqlException(exc);
                }

                this.nextStream = this.nextStream.nextStream;
            }
        }

        try {
            doOall8(false, false, true, false);

            this.validRows = this.t4Connection.all8.getNumRows();
        } catch (IOException ex) {
            ((T4CConnection) this.connection).handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.fetch:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void continueReadRow(int start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.continueReadRow(start="
                                             + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            if (!this.connection.useFetchSizeWithLongColumn) {
                T4C8Oall all8 = this.t4Connection.all8;

                all8.continueReadRow(start);
            }

        } catch (IOException ex) {
            ((T4CConnection) this.connection).handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        } catch (SQLException ea) {
            if (ea.getErrorCode() == DatabaseError.getVendorCode(110)) {
                this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 110);
            } else {
                throw ea;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CCallableStatement.continueReadRow:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void doClose() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.do_close()", this);

            OracleLog.recursiveTrace = false;
        }

        this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CCallableStatement.do_close");
        try {
            if (this.cursorId != 0) {
                this.t4Connection.cursorToClose[(this.t4Connection.cursorToCloseOffset++)] = this.cursorId;

                if (this.t4Connection.cursorToCloseOffset >= this.t4Connection.cursorToClose.length) {
                    this.t4Connection.sendPiggyBackedMessages();
                }

            }

        } catch (IOException ex) {
            ((T4CConnection) this.connection).handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        this.tmpByteArray = null;
        this.tmpBindsByteArray = null;
        this.definedColumnType = null;
        this.definedColumnSize = null;
        this.definedColumnFormOfUse = null;
        this.oacdefSent = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.do_close:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    void closeQuery() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.closeQuery()", this);

            OracleLog.recursiveTrace = false;
        }

        this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CCallableStatement.closeQuery");

        if (this.streamList != null) {
            while (this.nextStream != null) {
                try {
                    this.nextStream.close();
                } catch (IOException exc) {
                    ((T4CConnection) this.connection).handleIOException(exc);
                    DatabaseError.throwSqlException(exc);
                }

                this.nextStream = this.nextStream.nextStream;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CCallableStatement.closeQuery:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    PlsqlIndexTableAccessor allocateIndexTableAccessor(int elemSqlType, int elemInternalType,
            int elemMaxLen, int maxNumOfElements, short form, boolean forBind) throws SQLException {
        return new T4CPlsqlIndexTableAccessor(this, elemSqlType, elemInternalType, elemMaxLen,
                maxNumOfElements, form, forBind, this.t4Connection.mare);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CCallableStatement"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CCallableStatement JD-Core Version: 0.6.0
 */