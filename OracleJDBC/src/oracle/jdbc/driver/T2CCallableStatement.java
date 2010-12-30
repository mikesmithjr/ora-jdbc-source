// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T2CCallableStatement.java

package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.oracore.OracleTypeADT;

// Referenced classes of package oracle.jdbc.driver:
// OracleCallableStatement, Accessor, VarcharAccessor, CharAccessor,
// NumberAccessor, RawAccessor, BinaryFloatAccessor, BinaryDoubleAccessor,
// LongAccessor, LongRawAccessor, RowidAccessor, T2CResultSetAccessor,
// DateAccessor, TimestampAccessor, TimestamptzAccessor, TimestampltzAccessor,
// IntervalymAccessor, IntervaldsAccessor, ClobAccessor, BlobAccessor,
// BfileAccessor, NamedTypeAccessor, RefTypeAccessor, T2CNamedTypeAccessor,
// T2CConnection, DBConversion, OracleLog, DatabaseError,
// T2CStatement, OracleSql, OracleInputStream

class T2CCallableStatement extends OracleCallableStatement {

    T2CConnection connection;
    int userResultSetType;
    int userResultSetConcur;
    static int T2C_EXTEND_BUFFER = -3;
    long t2cOutput[];
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    T2CCallableStatement(T2CConnection conn, String sqlText, int batchValue, int rowPrefetchValue,
            int UserResultSetType, int UserResultSetConcur) throws SQLException {
        super(conn, sqlText, batchValue, rowPrefetchValue, UserResultSetType, UserResultSetConcur);
        connection = null;
        userResultSetType = -1;
        userResultSetConcur = -1;
        t2cOutput = new long[10];
        userResultSetType = UserResultSetType;
        userResultSetConcur = UserResultSetConcur;
        connection = conn;
    }

    String bytes2String(byte bytes[], int offset, int size) throws SQLException {
        byte tmp[] = new byte[size];
        System.arraycopy(bytes, offset, tmp, 0, size);
        return connection.conversion.CharBytesToString(tmp, size);
    }

    void processDescribeData() throws SQLException {
        described = true;
        describedWithNames = true;
        if (accessors == null || numberOfDefinePositions > accessors.length) {
            accessors = new Accessor[numberOfDefinePositions];
        }
        int currentShort = connection.queryMetaData1Offset;
        int currentChar = connection.queryMetaData2Offset;
        short s[] = connection.queryMetaData1;
        byte c[] = connection.queryMetaData2;
        for (int i = 0; i < numberOfDefinePositions;) {
            T2CConnection _tmp = connection;
            int accessorType = s[currentShort + 0];
            T2CConnection _tmp1 = connection;
            int maxLength = s[currentShort + 1];
            T2CConnection _tmp2 = connection;
            int maxCharLength = s[currentShort + 11];
            T2CConnection _tmp3 = connection;
            boolean nullable = s[currentShort + 2] != 0;
            T2CConnection _tmp4 = connection;
            int precision = s[currentShort + 3];
            T2CConnection _tmp5 = connection;
            int scale = s[currentShort + 4];
            int flags = 0;
            int contflag = 0;
            int totalElems = 0;
            T2CConnection _tmp6 = connection;
            short formOfUse = s[currentShort + 5];
            T2CConnection _tmp7 = connection;
            short columnNameLen = s[currentShort + 6];
            String columnName = bytes2String(c, currentChar, columnNameLen);
            T2CConnection _tmp8 = connection;
            short typeNameLen = s[currentShort + 12];
            String typeName = null;
            OracleTypeADT otype = null;
            currentChar += columnNameLen;
            if (typeNameLen > 0) {
                typeName = bytes2String(c, currentChar, typeNameLen);
                currentChar += typeNameLen;
                otype = new OracleTypeADT(typeName, connection);
                T2CConnection _tmp9 = connection;
                T2CConnection _tmp10 = connection;
                T2CConnection _tmp11 = connection;
                T2CConnection _tmp12 = connection;
                otype.tdoCState = ((long) s[currentShort + 7] & 65535L) << 48
                        | ((long) s[currentShort + 8] & 65535L) << 32
                        | ((long) s[currentShort + 9] & 65535L) << 16 | (long) s[currentShort + 10]
                        & 65535L;
            }
            Accessor accessor = accessors[i];
            if (accessor != null
                    && !accessor.useForDescribeIfPossible(accessorType, maxLength, nullable, flags,
                                                          precision, scale, contflag, totalElems,
                                                          formOfUse, typeName)) {
                accessor = null;
            }
            if (accessor == null) {
                switch (accessorType) {
                case 1: // '\001'
                    accessor = new VarcharAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);
                    if (maxCharLength > 0) {
                        accessor.setDisplaySize(maxCharLength);
                    }
                    break;

                case 96: // '`'
                    accessor = new CharAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);
                    if (maxCharLength > 0) {
                        accessor.setDisplaySize(maxCharLength);
                    }
                    break;

                case 2: // '\002'
                    accessor = new NumberAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);
                    break;

                case 23: // '\027'
                    accessor = new RawAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);
                    break;

                case 100: // 'd'
                    accessor = new BinaryFloatAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);
                    break;

                case 101: // 'e'
                    accessor = new BinaryDoubleAccessor(this, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);
                    break;

                case 8: // '\b'
                    accessor = new LongAccessor(this, i + 1, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);
                    rowPrefetch = 1;
                    break;

                case 24: // '\030'
                    accessor = new LongRawAccessor(this, i + 1, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);
                    rowPrefetch = 1;
                    break;

                case 104: // 'h'
                    accessor = new RowidAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);
                    break;

                case 102: // 'f'
                case 116: // 't'
                    accessor = new T2CResultSetAccessor(this, maxLength, nullable, flags,
                            precision, scale, contflag, totalElems, formOfUse);
                    break;

                case 12: // '\f'
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

                case 112: // 'p'
                    accessor = new ClobAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);
                    break;

                case 113: // 'q'
                    accessor = new BlobAccessor(this, maxLength, nullable, flags, precision, scale,
                            contflag, totalElems, formOfUse);
                    break;

                case 114: // 'r'
                    accessor = new BfileAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse);
                    break;

                case 109: // 'm'
                    accessor = new NamedTypeAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse, typeName, otype);
                    break;

                case 111: // 'o'
                    accessor = new RefTypeAccessor(this, maxLength, nullable, flags, precision,
                            scale, contflag, totalElems, formOfUse, typeName, otype);
                    break;

                default:
                    throw new SQLException("Unknown or unimplemented accessor type: "
                            + accessorType);
                }
                accessors[i] = accessor;
            } else if (otype != null) {
                accessor.initMetadata();
            }
            accessor.columnName = columnName;
            i++;
            currentShort += 13;
        }

    }

    void doDescribe(boolean includeNames) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CCallableStatement.do_describe ( includeNames = "
                                            + includeNames + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (closed) {
            DatabaseError.throwSqlException(9);
        }
        if (described) {
            return;
        }
        if (!isOpen) {
            DatabaseError.throwSqlException(144);
        }
        boolean try_again;
        do {
            try_again = false;
            numberOfDefinePositions = T2CStatement.t2cDescribe(c_state, connection.queryMetaData1,
                                                               connection.queryMetaData2,
                                                               connection.queryMetaData1Offset,
                                                               connection.queryMetaData2Offset,
                                                               connection.queryMetaData1Size,
                                                               connection.queryMetaData2Size);
            if (numberOfDefinePositions == -1) {
                connection.checkError(numberOfDefinePositions);
            }
            if (numberOfDefinePositions == T2C_EXTEND_BUFFER) {
                try_again = true;
                connection.reallocateQueryMetaData(connection.queryMetaData1Size * 2,
                                                   connection.queryMetaData2Size * 2);
            }
        } while (try_again);
        processDescribeData();
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.do_describe returns: void",
                                    this);
            OracleLog.recursiveTrace = false;
        }
    }

    void executeForDescribe() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.execute_for_describe () ",
                                    this);
            OracleLog.recursiveTrace = false;
        }
        t2cOutput[0] = 0L;
        t2cOutput[2] = 0L;
        boolean need_to_describe = !described;
        boolean executed = false;
        boolean try_again;
        do {
            try_again = false;
            if (connection.endToEndAnyChanged) {
                pushEndToEndValues();
                connection.endToEndAnyChanged = false;
            }
            byte array_sql[] = sqlObject.getSqlBytes(processEscapes, convertNcharLiterals);
            int status = T2CStatement.t2cParseExecuteDescribe(this, c_state, numberOfBindPositions,
                                                              numberOfBindRowsAllocated,
                                                              firstRowInBatch,
                                                              currentRowBindAccessors != null,
                                                              needToParse, need_to_describe,
                                                              executed, array_sql,
                                                              array_sql.length, sqlKind,
                                                              rowPrefetch, batch, bindIndicators,
                                                              bindIndicatorOffset, bindBytes,
                                                              bindChars, bindByteOffset,
                                                              bindCharOffset, ibtBindIndicators,
                                                              ibtBindIndicatorOffset,
                                                              ibtBindIndicatorSize, ibtBindBytes,
                                                              ibtBindChars, ibtBindByteOffset,
                                                              ibtBindCharOffset, returnParamMeta,
                                                              connection.queryMetaData1,
                                                              connection.queryMetaData2,
                                                              connection.queryMetaData1Offset,
                                                              connection.queryMetaData2Offset,
                                                              connection.queryMetaData1Size,
                                                              connection.queryMetaData2Size,
                                                              preparedAllBinds, preparedCharBinds,
                                                              outBindAccessors, parameterDatum,
                                                              t2cOutput, defineBytes,
                                                              accessorByteOffset, defineChars,
                                                              accessorCharOffset, defineIndicators,
                                                              accessorShortOffset,
                                                              connection.plsqlCompilerWarnings);
            validRows = (int) t2cOutput[1];
            if (status == -1) {
                connection.checkError(status);
            } else if (status == T2C_EXTEND_BUFFER) {
                status = connection.queryMetaData1Size * 2;
            }
            if (t2cOutput[3] != 0L) {
                foundPlsqlCompilerWarning();
            } else if (t2cOutput[2] != 0L) {
                sqlWarning = connection.checkError(1, sqlWarning);
            }
            connection.endToEndECIDSequenceNumber = (short) (int) t2cOutput[4];
            needToParse = false;
            executed = true;
            if (sqlKind == 0) {
                numberOfDefinePositions = status;
                if (numberOfDefinePositions > connection.queryMetaData1Size) {
                    try_again = true;
                    executed = true;
                    connection.reallocateQueryMetaData(numberOfDefinePositions,
                                                       numberOfDefinePositions * 8);
                }
            } else {
                numberOfDefinePositions = 0;
                validRows = status;
            }
        } while (try_again);
        processDescribeData();
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CCallableStatement.execute_for_describe () returns: void",
                                    this);
            OracleLog.recursiveTrace = false;
        }
    }

    void pushEndToEndValues() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger
                    .log(Level.FINE, "T2CCallableStatement.pushdEndToEndValues ()", this);
            OracleLog.recursiveTrace = false;
        }
        T2CConnection c = connection;
        byte e2e_action[] = new byte[0];
        byte e2e_clientid[] = new byte[0];
        byte e2e_ecid[] = new byte[0];
        byte e2e_module[] = new byte[0];
        if (c.endToEndValues != null) {
            T2CConnection _tmp = c;
            if (c.endToEndHasChanged[0]) {
                T2CConnection _tmp1 = c;
                String action = c.endToEndValues[0];
                if (action != null) {
                    e2e_action = DBConversion.stringToDriverCharBytes(action,
                                                                      c.m_clientCharacterSet);
                }
                T2CConnection _tmp2 = c;
                c.endToEndHasChanged[0] = false;
            }
            T2CConnection _tmp3 = c;
            if (c.endToEndHasChanged[1]) {
                T2CConnection _tmp4 = c;
                String clientid = c.endToEndValues[1];
                if (clientid != null) {
                    e2e_clientid = DBConversion.stringToDriverCharBytes(clientid,
                                                                        c.m_clientCharacterSet);
                }
                T2CConnection _tmp5 = c;
                c.endToEndHasChanged[1] = false;
            }
            T2CConnection _tmp6 = c;
            if (c.endToEndHasChanged[2]) {
                T2CConnection _tmp7 = c;
                String ecid = c.endToEndValues[2];
                if (ecid != null) {
                    e2e_ecid = DBConversion.stringToDriverCharBytes(ecid, c.m_clientCharacterSet);
                }
                T2CConnection _tmp8 = c;
                c.endToEndHasChanged[2] = false;
            }
            T2CConnection _tmp9 = c;
            if (c.endToEndHasChanged[3]) {
                T2CConnection _tmp10 = c;
                String module = c.endToEndValues[3];
                if (module != null) {
                    e2e_module = DBConversion.stringToDriverCharBytes(module,
                                                                      c.m_clientCharacterSet);
                }
                T2CConnection _tmp11 = c;
                c.endToEndHasChanged[3] = false;
            }
            T2CStatement.t2cEndToEndUpdate(c_state, e2e_action, e2e_action.length, e2e_clientid,
                                           e2e_clientid.length, e2e_ecid, e2e_ecid.length,
                                           e2e_module, e2e_module.length,
                                           c.endToEndECIDSequenceNumber);
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger
                    .log(Level.FINE, "T2CCallableStatement.pushEndToEndValues() returns: void",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    void executeForRows(boolean executed_for_describe) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CCallableStatement.execute_for_rows (executed_for_describe  = "
                                            + executed_for_describe + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (connection.endToEndAnyChanged) {
            pushEndToEndValues();
            connection.endToEndAnyChanged = false;
        }
        if (!executed_for_describe) {
            if (numberOfDefinePositions > 0) {
                doDefineExecuteFetch();
            } else {
                executeForDescribe();
            }
        } else if (numberOfDefinePositions > 0) {
            doDefineFetch();
        }
        needToPrepareDefineBuffer = false;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "T2CCallableStatement.execute_for_rows () returns: void", this);
            OracleLog.recursiveTrace = false;
        }
    }

    void setupForDefine() throws SQLException {
        short s[] = connection.queryMetaData1;
        int currentShort = connection.queryMetaData1Offset;
        for (int i = 0; i < numberOfDefinePositions;) {
            Accessor currentAccessor = accessors[i];
            if (currentAccessor == null) {
                DatabaseError.throwSqlException(21);
            }
            T2CConnection _tmp = connection;
            s[currentShort + 0] = (short) currentAccessor.defineType;
            T2CConnection _tmp1 = connection;
            s[currentShort + 11] = (short) currentAccessor.charLength;
            T2CConnection _tmp2 = connection;
            s[currentShort + 1] = (short) currentAccessor.byteLength;
            T2CConnection _tmp3 = connection;
            s[currentShort + 5] = currentAccessor.formOfUse;
            if (currentAccessor.internalOtype != null) {
                long tdo = ((OracleTypeADT) currentAccessor.internalOtype).getTdoCState();
                T2CConnection _tmp4 = connection;
                s[currentShort + 7] = (short) (int) ((tdo & 0xffff000000000000L) >> 48);
                T2CConnection _tmp5 = connection;
                s[currentShort + 8] = (short) (int) ((tdo & 0xffff00000000L) >> 32);
                T2CConnection _tmp6 = connection;
                s[currentShort + 9] = (short) (int) ((tdo & 0xffff0000L) >> 16);
                T2CConnection _tmp7 = connection;
                s[currentShort + 10] = (short) (int) (tdo & 65535L);
            }
            i++;
            currentShort += 13;
        }

    }

    void doDefineFetch() throws SQLException {
        if (!needToPrepareDefineBuffer) {
            throw new Error("doDefineFetch called when needToPrepareDefineBuffer=false "
                    + sqlObject.getSql(processEscapes, convertNcharLiterals));
        }
        setupForDefine();
        t2cOutput[2] = 0L;
        validRows = T2CStatement.t2cDefineFetch(c_state, rowPrefetch, connection.queryMetaData1,
                                                connection.queryMetaData2,
                                                connection.queryMetaData1Offset,
                                                connection.queryMetaData2Offset, accessors,
                                                defineBytes, accessorByteOffset, defineChars,
                                                accessorCharOffset, defineIndicators,
                                                accessorShortOffset, t2cOutput);
        if (validRows == -1) {
            connection.checkError(validRows);
        }
        if (t2cOutput[2] != 0L) {
            sqlWarning = connection.checkError(1, sqlWarning);
        }
    }

    void doDefineExecuteFetch() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.doDefineExecuteFetch ()",
                                    this);
            OracleLog.recursiveTrace = false;
        }
        short queryMetaData1[] = null;
        if (needToPrepareDefineBuffer || needToParse) {
            setupForDefine();
            queryMetaData1 = connection.queryMetaData1;
        }
        t2cOutput[0] = 0L;
        t2cOutput[2] = 0L;
        byte array_sql[] = sqlObject.getSqlBytes(processEscapes, convertNcharLiterals);
        validRows = T2CStatement.t2cDefineExecuteFetch(this, c_state, numberOfDefinePositions,
                                                       numberOfBindPositions,
                                                       numberOfBindRowsAllocated, firstRowInBatch,
                                                       currentRowBindAccessors != null,
                                                       needToParse, array_sql, array_sql.length,
                                                       sqlKind, rowPrefetch, batch, bindIndicators,
                                                       bindIndicatorOffset, bindBytes, bindChars,
                                                       bindByteOffset, bindCharOffset,
                                                       queryMetaData1, connection.queryMetaData2,
                                                       connection.queryMetaData1Offset,
                                                       connection.queryMetaData2Offset,
                                                       preparedAllBinds, preparedCharBinds,
                                                       outBindAccessors, parameterDatum, t2cOutput,
                                                       defineBytes, accessorByteOffset,
                                                       defineChars, accessorCharOffset,
                                                       defineIndicators, accessorShortOffset);
        if (validRows == -1) {
            connection.checkError(validRows);
        }
        if (t2cOutput[2] != 0L) {
            sqlWarning = connection.checkError(1, sqlWarning);
        }
        connection.endToEndECIDSequenceNumber = (short) (int) t2cOutput[4];
        needToParse = false;
    }

    void fetch() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.fetch ()", this);
            OracleLog.recursiveTrace = false;
        }
        if (numberOfDefinePositions > 0) {
            if (needToPrepareDefineBuffer) {
                doDefineFetch();
            } else {
                t2cOutput[2] = 0L;
                validRows = T2CStatement.t2cFetch(c_state, needToPrepareDefineBuffer, rowPrefetch,
                                                  accessors, defineBytes, accessorByteOffset,
                                                  defineChars, accessorCharOffset,
                                                  defineIndicators, accessorShortOffset, t2cOutput);
                if (validRows == -1) {
                    connection.checkError(validRows);
                }
                if (t2cOutput[2] != 0L) {
                    sqlWarning = connection.checkError(1, sqlWarning);
                }
            }
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger
                    .log(Level.FINE, "T2CCallableStatement.fetch () returns: void", this);
            OracleLog.recursiveTrace = false;
        }
    }

    void doClose() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.do_close ()", this);
            OracleLog.recursiveTrace = false;
        }
        if (defineBytes != null) {
            defineBytes = null;
            accessorByteOffset = 0;
        }
        if (defineChars != null) {
            defineChars = null;
            accessorCharOffset = 0;
        }
        if (defineIndicators != null) {
            defineIndicators = null;
            accessorShortOffset = 0;
        }
        int returnCode = T2CStatement.t2cCloseStatement(c_state);
        if (returnCode != 0) {
            connection.checkError(returnCode);
        }
        t2cOutput = null;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.do_close () returns: void",
                                    this);
            OracleLog.recursiveTrace = false;
        }
    }

    void closeQuery() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.closeQuery ()", this);
            OracleLog.recursiveTrace = false;
        }
        if (streamList != null) {
            for (; nextStream != null; nextStream = nextStream.nextStream) {
                try {
                    nextStream.close();
                } catch (IOException exc) {
                    DatabaseError.throwSqlException(exc);
                }
            }

        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CCallableStatement.closeQuery () returns: void",
                                    this);
            OracleLog.recursiveTrace = false;
        }
    }

    Accessor allocateAccessor(int internal_type, int external_type, int col_index, int max_len,
            short form, String typeName, boolean forBind) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
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
        if (internal_type == 116 || internal_type == 102) {
            if (forBind && typeName != null) {
                DatabaseError.throwSqlException(12, "sqlType=" + external_type);
            }
            Accessor result = new T2CResultSetAccessor(this, max_len, form, external_type, forBind);
            return result;
        } else {
            return super.allocateAccessor(internal_type, external_type, col_index, max_len, form,
                                          typeName, forBind);
        }
    }

    void closeUsedStreams(int columnIndex) throws SQLException {
        for (; nextStream != null && nextStream.columnIndex < columnIndex; nextStream = nextStream.nextStream) {
            try {
                if (TRACE && !OracleLog.recursiveTrace) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER, "closeUsedStream(" + columnIndex
                            + ") closing " + nextStream + " at index " + nextStream.columnIndex,
                                               this);
                    OracleLog.recursiveTrace = false;
                }
                nextStream.close();
            } catch (IOException exc) {
                DatabaseError.throwSqlException(exc);
            }
        }

        if (nextStream != null) {
            try {
                nextStream.needBytes();
            } catch (IOException e) {
                DatabaseError.throwSqlException(e);
            }
        }
    }

    void fetchDmlReturnParams() throws SQLException {
        rowsDmlReturned = T2CStatement.t2cGetRowsDmlReturned(c_state);
        if (rowsDmlReturned != 0) {
            allocateDmlReturnStorage();
            int status = T2CStatement.t2cFetchDmlReturnParams(c_state, returnParamAccessors,
                                                              returnParamBytes, returnParamChars,
                                                              returnParamIndicators);
        }
        returnParamsFetched = true;
    }

    void initializeIndicatorSubRange() {
        bindIndicatorSubRange = numberOfBindPositions * 5;
    }

    void prepareBindPreambles(int number_of_rows_to_be_bound, int number_of_rows_to_be_set_up) {
        int saveBindIndicatorSubRange = bindIndicatorSubRange;
        initializeIndicatorSubRange();
        int preambleSize = bindIndicatorSubRange;
        bindIndicatorSubRange = saveBindIndicatorSubRange;
        int currentPreambleIndex = bindIndicatorSubRange - preambleSize;
        OracleTypeADT otypes[] = parameterOtype != null ? parameterOtype[firstRowInBatch] : null;
        for (int i = 0; i < numberOfBindPositions; i++) {
            Binder binder = lastBinders[i];
            short inout;
            OracleTypeADT otype;
            if (binder == theReturnParamBinder) {
                otype = (OracleTypeADT) returnParamAccessors[i].internalOtype;
                inout = 0;
            } else {
                otype = otypes != null ? otypes[i] : null;
                if (outBindAccessors == null) {
                    inout = 0;
                } else {
                    Accessor accessor = outBindAccessors[i];
                    if (accessor == null) {
                        inout = 0;
                    } else if (binder == theOutBinder) {
                        inout = 1;
                        if (otype == null) {
                            otype = (OracleTypeADT) accessor.internalOtype;
                        }
                    } else {
                        inout = 2;
                    }
                }
                if (binder == theSetCHARBinder) {
                    inout |= 4;
                }
            }
            bindIndicators[currentPreambleIndex++] = inout;
            if (otype != null) {
                long tdo = otype.getTdoCState();
                bindIndicators[currentPreambleIndex + 0] = (short) (int) (tdo >> 48 & 65535L);
                bindIndicators[currentPreambleIndex + 1] = (short) (int) (tdo >> 32 & 65535L);
                bindIndicators[currentPreambleIndex + 2] = (short) (int) (tdo >> 16 & 65535L);
                bindIndicators[currentPreambleIndex + 3] = (short) (int) (tdo & 65535L);
            }
            currentPreambleIndex += 4;
        }

    }

    void registerOutParameterInternal(int paramIndex, int external_type, int scale, int maxLength,
            String sqlName) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int internal_type = getInternalType(external_type);
        if (internal_type == 995) {
            DatabaseError.throwSqlException(4);
        }
        resetBatch();
        currentRowNeedToPrepareBinds = true;
        if (currentRowBindAccessors == null) {
            currentRowBindAccessors = new Accessor[numberOfBindPositions];
        }
        currentRowBindAccessors[index] = allocateAccessor(internal_type, external_type, paramIndex,
                                                          maxLength, currentRowFormOfUse[index],
                                                          sqlName, true);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T2CCallableStatement"));
        } catch (Exception e) {
        }
    }
}
