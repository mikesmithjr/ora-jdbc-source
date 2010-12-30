package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.oracore.OracleTypeADT;

class T2CStatement extends OracleStatement {
    T2CConnection connection = null;
    int userResultSetType = -1;
    int userResultSetConcur = -1;

    static int T2C_EXTEND_BUFFER = -3;

    long[] t2cOutput = new long[10];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T2CStatement(T2CConnection conn, int batchValue, int rowPrefetchValue) throws SQLException {
        this(conn, batchValue, rowPrefetchValue, -1, -1);

        this.connection = conn;
    }

    T2CStatement(T2CConnection conn, int batch_value, int row_prefetch_value,
            int UserResultSetType, int UserResultSetConcur) throws SQLException {
        super(conn, batch_value, row_prefetch_value, UserResultSetType, UserResultSetConcur);

        this.userResultSetType = UserResultSetType;
        this.userResultSetConcur = UserResultSetConcur;

        this.connection = conn;
    }

    static native int t2cParseExecuteDescribe(OracleStatement paramOracleStatement, long paramLong,
            int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1,
            boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4,
            byte[] paramArrayOfByte1, int paramInt4, byte paramByte, int paramInt5, int paramInt6,
            short[] paramArrayOfShort1, int paramInt7, byte[] paramArrayOfByte2,
            char[] paramArrayOfChar1, int paramInt8, int paramInt9, short[] paramArrayOfShort2,
            int paramInt10, int paramInt11, byte[] paramArrayOfByte3, char[] paramArrayOfChar2,
            int paramInt12, int paramInt13, int[] paramArrayOfInt, short[] paramArrayOfShort3,
            byte[] paramArrayOfByte4, int paramInt14, int paramInt15, int paramInt16,
            int paramInt17, boolean paramBoolean5, boolean paramBoolean6,
            Accessor[] paramArrayOfAccessor, byte[][][] paramArrayOfByte, long[] paramArrayOfLong,
            byte[] paramArrayOfByte5, int paramInt18, char[] paramArrayOfChar3, int paramInt19,
            short[] paramArrayOfShort4, int paramInt20, boolean paramBoolean7);

    static native int t2cDefineExecuteFetch(OracleStatement paramOracleStatement, long paramLong,
            int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1,
            boolean paramBoolean2, byte[] paramArrayOfByte1, int paramInt5, byte paramByte,
            int paramInt6, int paramInt7, short[] paramArrayOfShort1, int paramInt8,
            byte[] paramArrayOfByte2, char[] paramArrayOfChar1, int paramInt9, int paramInt10,
            short[] paramArrayOfShort2, byte[] paramArrayOfByte3, int paramInt11, int paramInt12,
            boolean paramBoolean3, boolean paramBoolean4, Accessor[] paramArrayOfAccessor,
            byte[][][] paramArrayOfByte, long[] paramArrayOfLong, byte[] paramArrayOfByte4,
            int paramInt13, char[] paramArrayOfChar2, int paramInt14, short[] paramArrayOfShort3,
            int paramInt15);

    static native int t2cDescribe(long paramLong, short[] paramArrayOfShort,
            byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

    static native int t2cDefineFetch(long paramLong, int paramInt1, short[] paramArrayOfShort1,
            byte[] paramArrayOfByte1, int paramInt2, int paramInt3,
            Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte2, int paramInt4,
            char[] paramArrayOfChar, int paramInt5, short[] paramArrayOfShort2, int paramInt6,
            long[] paramArrayOfLong);

    static native int t2cFetch(long paramLong, boolean paramBoolean, int paramInt1,
            Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte, int paramInt2,
            char[] paramArrayOfChar, int paramInt3, short[] paramArrayOfShort, int paramInt4,
            long[] paramArrayOfLong);

    static native int t2cCloseStatement(long paramLong);

    static native int t2cEndToEndUpdate(long paramLong, byte[] paramArrayOfByte1, int paramInt1,
            byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3,
            byte[] paramArrayOfByte4, int paramInt4, int paramInt5);

    static native int t2cGetRowsDmlReturned(long paramLong);

    static native int t2cFetchDmlReturnParams(long paramLong, Accessor[] paramArrayOfAccessor,
            byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort);

    String bytes2String(byte[] bytes, int offset, int size) throws SQLException {
        byte[] tmp = new byte[size];

        System.arraycopy(bytes, offset, tmp, 0, size);

        return this.connection.conversion.CharBytesToString(tmp, size);
    }

    void processDescribeData() throws SQLException {
        this.described = true;
        this.describedWithNames = true;

        if ((this.accessors == null) || (this.numberOfDefinePositions > this.accessors.length)) {
            this.accessors = new Accessor[this.numberOfDefinePositions];
        }

        int currentShort = this.connection.queryMetaData1Offset;
        int currentChar = this.connection.queryMetaData2Offset;
        short[] s = this.connection.queryMetaData1;
        byte[] c = this.connection.queryMetaData2;

        for (int i = 0; i < this.numberOfDefinePositions;) {
            int accessorType = s[(currentShort + 0)];
            int maxLength = s[(currentShort + 1)];
            int maxCharLength = s[(currentShort + 11)];
            boolean nullable = s[(currentShort + 2)] != 0;
            int precision = s[(currentShort + 3)];
            int scale = s[(currentShort + 4)];
            int flags = 0;
            int contflag = 0;
            int totalElems = 0;
            short formOfUse = s[(currentShort + 5)];
            short columnNameLen = s[(currentShort + 6)];
            String columnName = bytes2String(c, currentChar, columnNameLen);
            short typeNameLen = s[(currentShort + 12)];
            String typeName = null;
            OracleTypeADT otype = null;

            currentChar += columnNameLen;

            if (typeNameLen > 0) {
                typeName = bytes2String(c, currentChar, typeNameLen);
                currentChar += typeNameLen;
                otype = new OracleTypeADT(typeName, this.connection);
                otype.tdoCState = ((s[(currentShort + 7)] & 0xFFFF) << 48
                        | (s[(currentShort + 8)] & 0xFFFF) << 32
                        | (s[(currentShort + 9)] & 0xFFFF) << 16 | s[(currentShort + 10)] & 0xFFFF);
            }

            Accessor accessor = this.accessors[i];

            if ((accessor != null)
                    && (!accessor.useForDescribeIfPossible(accessorType, maxLength, nullable,
                                                           flags, precision, scale, contflag,
                                                           totalElems, formOfUse, typeName))) {
                accessor = null;
            }
            if (accessor == null) {
                switch (accessorType) {
                case 1:
                    accessor = new VarcharAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    if (maxCharLength <= 0)
                        break;
                    accessor.setDisplaySize(maxCharLength);
                    break;
                case 96:
                    accessor = new CharAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);

                    if (maxCharLength <= 0)
                        break;
                    accessor.setDisplaySize(maxCharLength);
                    break;
                case 2:
                    accessor = new NumberAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 23:
                    accessor = new RawAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);

                    break;
                case 100:
                    accessor = new BinaryFloatAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 101:
                    accessor = new BinaryDoubleAccessor(this, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);

                    break;
                case 8:
                    accessor = new LongAccessor(this, i + 1, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    this.rowPrefetch = 1;

                    break;
                case 24:
                    accessor = new LongRawAccessor(this, i + 1, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);

                    this.rowPrefetch = 1;

                    break;
                case 104:
                    accessor = new RowidAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 102:
                case 116:
                    accessor = new T2CResultSetAccessor(this, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);

                    break;
                case 12:
                    accessor = new DateAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);

                    break;
                case 180:
                    accessor = new TimestampAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 181:
                    accessor = new TimestamptzAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 231:
                    accessor = new TimestampltzAccessor(this, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);

                    break;
                case 182:
                    accessor = new IntervalymAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 183:
                    accessor = new IntervaldsAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 112:
                    accessor = new ClobAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);

                    break;
                case 113:
                    accessor = new BlobAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);

                    break;
                case 114:
                    accessor = new BfileAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);

                    break;
                case 109:
                    accessor = new NamedTypeAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse, typeName, otype);

                    break;
                case 111:
                    accessor = new RefTypeAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse, typeName, otype);

                    break;
                default:
                    throw new SQLException("Unknown or unimplemented accessor type: "
                            + accessorType);
                }

                this.accessors[i] = accessor;
            } else if (otype != null) {
                accessor.initMetadata();
            }

            accessor.columnName = columnName;

            i++;
            currentShort += 13;
        }
    }

    void doDescribe(boolean includeNames) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.do_describe ( includeNames = "
                    + includeNames + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            DatabaseError.throwSqlException(9);
        }
        if (this.described == true) {
            return;
        }

        if (!this.isOpen) {
            DatabaseError.throwSqlException(144);
        }

        boolean try_again;
        do {
            try_again = false;

            this.numberOfDefinePositions = t2cDescribe(this.c_state,
                                                       this.connection.queryMetaData1,
                                                       this.connection.queryMetaData2,
                                                       this.connection.queryMetaData1Offset,
                                                       this.connection.queryMetaData2Offset,
                                                       this.connection.queryMetaData1Size,
                                                       this.connection.queryMetaData2Size);

            if (this.numberOfDefinePositions == -1) {
                this.connection.checkError(this.numberOfDefinePositions);
            }

            if (this.numberOfDefinePositions != T2C_EXTEND_BUFFER)
                continue;
            try_again = true;

            this.connection.reallocateQueryMetaData(this.connection.queryMetaData1Size * 2,
                                                    this.connection.queryMetaData2Size * 2);
        }

        while (try_again);

        processDescribeData();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.do_describe returns: void", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void executeForDescribe() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.execute_for_describe () ", this);

            OracleLog.recursiveTrace = false;
        }
        this.t2cOutput[0] = 0L;
        this.t2cOutput[2] = 0L;

        boolean need_to_describe = !this.described;
        boolean executed = false;
        boolean try_again;
        do {
            try_again = false;

            if (this.connection.endToEndAnyChanged) {
                pushEndToEndValues();

                this.connection.endToEndAnyChanged = false;
            }

            byte[] array_sql = this.sqlObject.getSqlBytes(this.processEscapes,
                                                          this.convertNcharLiterals);
            int status = t2cParseExecuteDescribe(this, this.c_state, this.numberOfBindPositions, 0,
                                                 0, false, this.needToParse, need_to_describe,
                                                 executed, array_sql, array_sql.length,
                                                 this.sqlKind, this.rowPrefetch, this.batch,
                                                 this.bindIndicators, this.bindIndicatorOffset,
                                                 this.bindBytes, this.bindChars,
                                                 this.bindByteOffset, this.bindCharOffset,
                                                 this.ibtBindIndicators,
                                                 this.ibtBindIndicatorOffset,
                                                 this.ibtBindIndicatorSize, this.ibtBindBytes,
                                                 this.ibtBindChars, this.ibtBindByteOffset,
                                                 this.ibtBindCharOffset, this.returnParamMeta,
                                                 this.connection.queryMetaData1,
                                                 this.connection.queryMetaData2,
                                                 this.connection.queryMetaData1Offset,
                                                 this.connection.queryMetaData2Offset,
                                                 this.connection.queryMetaData1Size,
                                                 this.connection.queryMetaData2Size, true, true,
                                                 this.accessors, (byte[][][]) null, this.t2cOutput,
                                                 this.defineBytes, this.accessorByteOffset,
                                                 this.defineChars, this.accessorCharOffset,
                                                 this.defineIndicators, this.accessorShortOffset,
                                                 this.connection.plsqlCompilerWarnings);

            this.validRows = (int) this.t2cOutput[1];

            if (status == -1) {
                this.connection.checkError(status);
            } else if (status == T2C_EXTEND_BUFFER) {
                status = this.connection.queryMetaData1Size * 2;
            }

            if (this.t2cOutput[3] != 0L) {
                foundPlsqlCompilerWarning();
            } else if (this.t2cOutput[2] != 0L) {
                this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
            }

            this.connection.endToEndECIDSequenceNumber = (short) (int) this.t2cOutput[4];

            this.needToParse = false;
            executed = true;

            if (this.sqlKind == 0) {
                this.numberOfDefinePositions = status;

                if (this.numberOfDefinePositions <= this.connection.queryMetaData1Size)
                    continue;
                try_again = true;
                executed = true;

                this.connection.reallocateQueryMetaData(this.numberOfDefinePositions,
                                                        this.numberOfDefinePositions * 8);
            } else {
                this.numberOfDefinePositions = 0;
                this.validRows = status;
            }
        } while (try_again);

        processDescribeData();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CStatement.execute_for_describe () returns: void", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void pushEndToEndValues() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.pushdEndToEndValues ()", this);

            OracleLog.recursiveTrace = false;
        }

        T2CConnection c = this.connection;
        byte[] e2e_action = new byte[0];
        byte[] e2e_clientid = new byte[0];
        byte[] e2e_ecid = new byte[0];
        byte[] e2e_module = new byte[0];

        if (c.endToEndValues != null) {
            if (c.endToEndHasChanged[0] != false) {
                String action = c.endToEndValues[0];

                if (action != null) {
                    e2e_action = DBConversion.stringToDriverCharBytes(action,
                                                                      c.m_clientCharacterSet);
                }

                c.endToEndHasChanged[0] = false;
            }

            if (c.endToEndHasChanged[1] != false) {
                String clientid = c.endToEndValues[1];

                if (clientid != null) {
                    e2e_clientid = DBConversion.stringToDriverCharBytes(clientid,
                                                                        c.m_clientCharacterSet);
                }

                c.endToEndHasChanged[1] = false;
            }

            if (c.endToEndHasChanged[2] != false) {
                String ecid = c.endToEndValues[2];

                if (ecid != null) {
                    e2e_ecid = DBConversion.stringToDriverCharBytes(ecid, c.m_clientCharacterSet);
                }

                c.endToEndHasChanged[2] = false;
            }

            if (c.endToEndHasChanged[3] != false) {
                String module = c.endToEndValues[3];

                if (module != null) {
                    e2e_module = DBConversion.stringToDriverCharBytes(module,
                                                                      c.m_clientCharacterSet);
                }

                c.endToEndHasChanged[3] = false;
            }

            t2cEndToEndUpdate(this.c_state, e2e_action, e2e_action.length, e2e_clientid,
                              e2e_clientid.length, e2e_ecid, e2e_ecid.length, e2e_module,
                              e2e_module.length, c.endToEndECIDSequenceNumber);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.pushEndToEndValues() returns: void",
                                    this);

            OracleLog.recursiveTrace = false;
        }
    }

    void executeForRows(boolean executed_for_describe) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CStatement.execute_for_rows (executed_for_describe  = "
                                            + executed_for_describe + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.connection.endToEndAnyChanged) {
            pushEndToEndValues();

            this.connection.endToEndAnyChanged = false;
        }

        if (!executed_for_describe) {
            if (this.numberOfDefinePositions > 0) {
                doDefineExecuteFetch();
            } else {
                executeForDescribe();
            }
        } else if (this.numberOfDefinePositions > 0) {
            doDefineFetch();
        }

        this.needToPrepareDefineBuffer = false;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.execute_for_rows () returns: void",
                                    this);

            OracleLog.recursiveTrace = false;
        }
    }

    void setupForDefine() throws SQLException {
        short[] s = this.connection.queryMetaData1;
        int currentShort = this.connection.queryMetaData1Offset;

        for (int i = 0; i < this.numberOfDefinePositions;) {
            Accessor currentAccessor = this.accessors[i];

            if (currentAccessor == null) {
                DatabaseError.throwSqlException(21);
            }
            s[(currentShort + 0)] = (short) currentAccessor.defineType;

            s[(currentShort + 11)] = (short) currentAccessor.charLength;

            s[(currentShort + 1)] = (short) currentAccessor.byteLength;

            s[(currentShort + 5)] = currentAccessor.formOfUse;

            if (currentAccessor.internalOtype != null) {
                long tdo = ((OracleTypeADT) currentAccessor.internalOtype).getTdoCState();

                s[(currentShort + 7)] = (short) (int) ((tdo & 0x0) >> 48);

                s[(currentShort + 8)] = (short) (int) ((tdo & 0x0) >> 32);

                s[(currentShort + 9)] = (short) (int) ((tdo & 0xFFFF0000) >> 16);

                s[(currentShort + 10)] = (short) (int) (tdo & 0xFFFF);
            }
            i++;
            currentShort += 13;
        }
    }

    void doDefineFetch() throws SQLException {
        if (!this.needToPrepareDefineBuffer) {
            throw new Error("doDefineFetch called when needToPrepareDefineBuffer=false "
                    + this.sqlObject.getSql(this.processEscapes, this.convertNcharLiterals));
        }

        setupForDefine();

        this.t2cOutput[2] = 0L;
        this.validRows = t2cDefineFetch(this.c_state, this.rowPrefetch,
                                        this.connection.queryMetaData1,
                                        this.connection.queryMetaData2,
                                        this.connection.queryMetaData1Offset,
                                        this.connection.queryMetaData2Offset, this.accessors,
                                        this.defineBytes, this.accessorByteOffset,
                                        this.defineChars, this.accessorCharOffset,
                                        this.defineIndicators, this.accessorShortOffset,
                                        this.t2cOutput);

        if (this.validRows == -1) {
            this.connection.checkError(this.validRows);
        }

        if (this.t2cOutput[2] != 0L) {
            this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
        }
    }

    void doDefineExecuteFetch() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.doDefineExecuteFetch ()", this);

            OracleLog.recursiveTrace = false;
        }

        short[] queryMetaData1 = null;

        if ((this.needToPrepareDefineBuffer) || (this.needToParse)) {
            setupForDefine();

            queryMetaData1 = this.connection.queryMetaData1;
        }

        this.t2cOutput[0] = 0L;
        this.t2cOutput[2] = 0L;

        byte[] array_sql = this.sqlObject.getSqlBytes(this.processEscapes,
                                                      this.convertNcharLiterals);

        this.validRows = t2cDefineExecuteFetch(this, this.c_state, this.numberOfDefinePositions,
                                               this.numberOfBindPositions, 0, 0, false,
                                               this.needToParse, array_sql, array_sql.length,
                                               this.sqlKind, this.rowPrefetch, this.batch,
                                               this.bindIndicators, this.bindIndicatorOffset,
                                               this.bindBytes, this.bindChars, this.bindByteOffset,
                                               this.bindCharOffset, queryMetaData1,
                                               this.connection.queryMetaData2,
                                               this.connection.queryMetaData1Offset,
                                               this.connection.queryMetaData2Offset, true, true,
                                               this.accessors, (byte[][][]) null, this.t2cOutput,
                                               this.defineBytes, this.accessorByteOffset,
                                               this.defineChars, this.accessorCharOffset,
                                               this.defineIndicators, this.accessorShortOffset);

        if (this.validRows == -1) {
            this.connection.checkError(this.validRows);
        }
        if (this.t2cOutput[2] != 0L) {
            this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
        }

        this.connection.endToEndECIDSequenceNumber = (short) (int) this.t2cOutput[4];

        this.needToParse = false;
    }

    void fetch() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.fetch ()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.numberOfDefinePositions > 0) {
            if (this.needToPrepareDefineBuffer) {
                doDefineFetch();
            } else {
                this.t2cOutput[2] = 0L;
                this.validRows = t2cFetch(this.c_state, this.needToPrepareDefineBuffer,
                                          this.rowPrefetch, this.accessors, this.defineBytes,
                                          this.accessorByteOffset, this.defineChars,
                                          this.accessorCharOffset, this.defineIndicators,
                                          this.accessorShortOffset, this.t2cOutput);

                if (this.validRows == -1) {
                    this.connection.checkError(this.validRows);
                }
                if (this.t2cOutput[2] != 0L) {
                    this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.fetch () returns: void", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void doClose() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.do_close ()", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.defineBytes != null) {
            this.defineBytes = null;
            this.accessorByteOffset = 0;
        }

        if (this.defineChars != null) {
            this.defineChars = null;
            this.accessorCharOffset = 0;
        }

        if (this.defineIndicators != null) {
            this.defineIndicators = null;
            this.accessorShortOffset = 0;
        }

        int returnCode = t2cCloseStatement(this.c_state);

        if (returnCode != 0) {
            this.connection.checkError(returnCode);
        }
        this.t2cOutput = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.do_close () returns: void", this);

            OracleLog.recursiveTrace = false;
        }
    }

    void closeQuery() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.closeQuery ()", this);

            OracleLog.recursiveTrace = false;
        }

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

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CStatement.closeQuery () returns: void", this);

            OracleLog.recursiveTrace = false;
        }
    }

    Accessor allocateAccessor(int internal_type, int external_type, int col_index, int max_len,
            short form, String typeName, boolean forBind) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CStatement.allocateAccessor(" + internal_type
                    + ", " + external_type + ", " + col_index + ", " + max_len + ", " + form + ", "
                    + typeName + ", " + forBind + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (internal_type == 109) {
            if (typeName == null) {
                if (forBind) {
                    DatabaseError.throwSqlException(12, "sqlType=" + external_type);
                } else {
                    DatabaseError.throwSqlException(60, "Unable to resolve type \"null\"");
                }
            }
            Accessor result = new T2CNamedTypeAccessor(this, typeName, form, external_type,
                    forBind, col_index - 1);

            result.initMetadata();

            return result;
        }
        if ((internal_type == 116) || (internal_type == 102)) {
            if ((forBind) && (typeName != null)) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }

            Accessor result = new T2CResultSetAccessor(this, max_len, form, external_type, forBind);

            return result;
        }

        return super.allocateAccessor(internal_type, external_type, col_index, max_len, form,
                                      typeName, forBind);
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

        if (this.nextStream != null)
            try {
                this.nextStream.needBytes();
            } catch (IOException e) {
                DatabaseError.throwSqlException(e);
            }
    }

    void fetchDmlReturnParams() throws SQLException {
        this.rowsDmlReturned = t2cGetRowsDmlReturned(this.c_state);
        int status;
        if (this.rowsDmlReturned != 0) {
            allocateDmlReturnStorage();

            status = t2cFetchDmlReturnParams(this.c_state, this.returnParamAccessors,
                                             this.returnParamBytes, this.returnParamChars,
                                             this.returnParamIndicators);
        }

        this.returnParamsFetched = true;
    }

    void initializeIndicatorSubRange() {
        this.bindIndicatorSubRange = (this.numberOfBindPositions * 5);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T2CStatement"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T2CStatement JD-Core Version: 0.6.0
 */