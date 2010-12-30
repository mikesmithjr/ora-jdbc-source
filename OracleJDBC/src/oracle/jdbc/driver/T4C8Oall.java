// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T4C8Oall.java

package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.net.ns.BreakNetException;

// Referenced classes of package oracle.jdbc.driver:
// T4CTTIfun, T4C8TTIrxh, T4CTTIrxd, T4CTTIoac,
// T4CTTIdcb, T4CTTIofetch, T4CTTIoexec, T4CTTIfob,
// T4CTTIiov, PlsqlIndexTableAccessor, TypeAccessor, T4CConnection,
// DBConversion, DatabaseError, T4CTTIoer, OracleStatement,
// PhysicalConnection, T4CMAREngine, Accessor, OracleLog

class T4C8Oall extends T4CTTIfun {

    static final byte EMPTY_BYTES[] = new byte[0];
    static final int UOPF_PRS = 1;
    static final int UOPF_BND = 8;
    static final int UOPF_EXE = 32;
    static final int UOPF_FEX = 512;
    static final int UOPF_FCH = 64;
    static final int UOPF_CAN = 128;
    static final int UOPF_COM = 256;
    static final int UOPF_DSY = 8192;
    static final int UOPF_SIO = 1024;
    static final int UOPF_NPL = 32768;
    static final int UOPF_DFN = 16;
    int receiveState;
    static final int IDLE_RECEIVE_STATE = 0;
    static final int ACTIVE_RECEIVE_STATE = 1;
    static final int READROW_RECEIVE_STATE = 2;
    static final int STREAM_RECEIVE_STATE = 3;
    int rowsProcessed;
    int numberOfDefinePositions;
    long options;
    int cursor;
    byte sqlStmt[];
    final long al8i4[] = new long[13];
    boolean plsql;
    Accessor definesAccessors[];
    int definesLength;
    Accessor outBindAccessors[];
    int numberOfBindPositions;
    InputStream parameterStream[][];
    byte parameterDatum[][][];
    OracleTypeADT parameterOtype[][];
    short bindIndicators[];
    byte bindBytes[];
    char bindChars[];
    int bindIndicatorSubRange;
    byte tmpBindsByteArray[];
    DBConversion conversion;
    byte ibtBindBytes[];
    char ibtBindChars[];
    short ibtBindIndicators[];
    boolean sendBindsDefinition;
    OracleStatement oracleStatement;
    short dbCharSet;
    short NCharSet;
    T4CTTIrxd rxd;
    T4C8TTIrxh rxh;
    T4CTTIoac oac;
    T4CTTIdcb dcb;
    T4CTTIofetch ofetch;
    T4CTTIoexec oexec;
    T4CTTIfob fob;
    byte typeOfStatement;
    boolean sendDefines;
    int defCols;
    int rowsToFetch;
    T4CTTIoac oacdefBindsSent[];
    T4CTTIoac oacdefDefines[];
    int definedColumnSize[];
    int definedColumnType[];
    int definedColumnFormOfUse[];
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8Oall(T4CMAREngine _mrengine, T4CConnection connection, T4CTTIoer _oer) throws IOException,
            SQLException {
        super((byte) 3, 0, (short) 94);
        receiveState = 0;
        sqlStmt = new byte[0];
        plsql = false;
        sendBindsDefinition = false;
        sendDefines = false;
        defCols = 0;
        setMarshalingEngine(_mrengine);
        rxh = new T4C8TTIrxh(meg);
        rxd = new T4CTTIrxd(meg);
        oer = _oer;
        oac = new T4CTTIoac(meg);
        dcb = new T4CTTIdcb(meg);
        ofetch = new T4CTTIofetch(meg);
        oexec = new T4CTTIoexec(meg);
        fob = new T4CTTIfob(meg);
    }

    T4CTTIoac[] marshal(boolean doParse, boolean doExecute, boolean doFetch, boolean doDescribe,
            byte _typeOfStatement, int _cursor, byte _sqlStmt[], int _rowsToFetch,
            Accessor _outBindAccessors[], int _numberOfBindPositions, Accessor _definesAccessors[],
            int _definesLength, byte _bindBytes[], char _bindChars[], short _bindIndicators[],
            int _bindIndicatorSubRange, DBConversion _conversion, byte _tmpBindsByteArray[],
            InputStream _parameterStream[][], byte _parameterDatum[][][],
            OracleTypeADT _parameterOtype[][], OracleStatement _oracleStatement,
            byte _ibtBindBytes[], char _ibtBindChars[], short _ibtBindIndicators[],
            T4CTTIoac _oacdefBindsSent[], int _definedColumnType[], int _definedColumnSize[],
            int _definedColumnFormOfUse[]) throws SQLException, IOException {
        typeOfStatement = _typeOfStatement;
        cursor = _cursor;
        sqlStmt = doParse ? _sqlStmt : EMPTY_BYTES;
        rowsToFetch = _rowsToFetch;
        outBindAccessors = _outBindAccessors;
        numberOfBindPositions = _numberOfBindPositions;
        definesAccessors = _definesAccessors;
        definesLength = _definesLength;
        bindBytes = _bindBytes;
        bindChars = _bindChars;
        bindIndicators = _bindIndicators;
        bindIndicatorSubRange = _bindIndicatorSubRange;
        conversion = _conversion;
        tmpBindsByteArray = _tmpBindsByteArray;
        parameterStream = _parameterStream;
        parameterDatum = _parameterDatum;
        parameterOtype = _parameterOtype;
        oracleStatement = _oracleStatement;
        ibtBindBytes = _ibtBindBytes;
        ibtBindChars = _ibtBindChars;
        ibtBindIndicators = _ibtBindIndicators;
        oacdefBindsSent = _oacdefBindsSent;
        definedColumnType = _definedColumnType;
        definedColumnSize = _definedColumnSize;
        definedColumnFormOfUse = _definedColumnFormOfUse;
        dbCharSet = _conversion.getServerCharSetId();
        NCharSet = _conversion.getNCharSetId();
        int number_of_bound_rows = 0;
        if (bindIndicators != null) {
            number_of_bound_rows = bindIndicators[bindIndicatorSubRange + 2] & 0xffff;
        }
        if (_sqlStmt == null) {
            DatabaseError.throwSqlException(431);
        }
        if (typeOfStatement != 2 && number_of_bound_rows > 1) {
            DatabaseError.throwSqlException(433);
        }
        rowsProcessed = 0;
        options = 0L;
        plsql = typeOfStatement == 1 || typeOfStatement == 4;
        sendBindsDefinition = false;
        if (receiveState != 0) {
            receiveState = 0;
            DatabaseError.throwSqlException(447);
        }
        rxh.init();
        rxd.init();
        oer.init();
        sendDefines = false;
        if (doParse && doExecute && !doFetch && definedColumnType != null) {
            sendDefines = true;
            initDefinesDefinition();
        }
        if (numberOfBindPositions > 0 && bindIndicators != null) {
            if (oacdefBindsSent == null) {
                oacdefBindsSent = new T4CTTIoac[numberOfBindPositions];
            }
            sendBindsDefinition = initBindsDefinition(oacdefBindsSent);
        }
        options = setOptions(doParse, doExecute, doFetch);
        if ((options & 1L) > 0L) {
            al8i4[0] = 1L;
        } else {
            al8i4[0] = 0L;
        }
        if (plsql || typeOfStatement == 3) {
            al8i4[1] = 1L;
        } else if (doDescribe) {
            if (doFetch && oracleStatement.connection.useFetchSizeWithLongColumn) {
                al8i4[1] = rowsToFetch;
            } else {
                al8i4[1] = 0L;
            }
        } else if (typeOfStatement == 2) {
            al8i4[1] = number_of_bound_rows != 0 ? number_of_bound_rows : oracleStatement.batch;
        } else if (doFetch && !doDescribe) {
            al8i4[1] = rowsToFetch;
        } else {
            al8i4[1] = 0L;
        }
        if (typeOfStatement == 0) {
            al8i4[7] = 1L;
        } else {
            al8i4[7] = 0L;
        }
        marshalAll();
        return oacdefBindsSent;
    }

    void receive() throws SQLException, IOException {
        if ((this.receiveState != 0) && (this.receiveState != 2)) {
            DatabaseError.throwSqlException(447);
        }

        this.receiveState = ACTIVE_RECEIVE_STATE;

        boolean rpaProcessed = false;
        boolean rxhProcessed = false;
        boolean iovProcessed = false;

        this.rowsProcessed = 0;

        this.rxd.setNumberOfColumns(this.definesLength);
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 21:
                    int nbOfColumnSent = this.meg.unmarshalUB2();

                    this.rxd.unmarshalBVC(nbOfColumnSent);

                    break;
                case 11:
                    T4CTTIiov iov = new T4CTTIiov(meg, rxh, rxd);
                    iov.init();
                    iov.unmarshalV10();
                    if (oracleStatement.returnParamAccessors == null && !iov.isIOVectorEmpty()) {
                        byte ioVector[] = iov.getIOVector();
                        outBindAccessors = iov.processRXD(outBindAccessors, numberOfBindPositions,
                                                          bindBytes, bindChars, bindIndicators,
                                                          bindIndicatorSubRange, conversion,
                                                          tmpBindsByteArray, ioVector,
                                                          parameterStream, parameterDatum,
                                                          parameterOtype, oracleStatement, null,
                                                          null, null);
                    }
                    iovProcessed = true;
                    break;
                case 6:
                    rxh.init();
                    rxh.unmarshalV10(rxd);
                    if (rxh.uacBufLength > 0) {
                        DatabaseError.throwSqlException(405);
                    }
                    rxhProcessed = true;
                    break;
                case 7:

                    if (receiveState != 1) {
                        DatabaseError.throwSqlException(447);
                    }
                    receiveState = 2;
                    if (oracleStatement.returnParamAccessors == null || numberOfBindPositions <= 0) {
                        if (!iovProcessed && (outBindAccessors == null || definesAccessors != null)) {
                            if (rxd.unmarshal(definesAccessors, definesLength)) {
                                receiveState = 3;
                                return;
                            }
                        } else {
                            if (rxd.unmarshal(outBindAccessors, numberOfBindPositions)) {
                                receiveState = 3;
                                return;
                            }
                        }
                    } else {
                        boolean memoryHasBeenAllocated = false;
                        for (int col = 0; col < oracleStatement.numberOfBindPositions; col++) {
                            Accessor acc = oracleStatement.returnParamAccessors[col];
                            if (acc == null) {
                                continue;
                            }
                            int nbOfRowsSent = (int) meg.unmarshalUB4();
                            if (!memoryHasBeenAllocated) {
                                oracleStatement.rowsDmlReturned = nbOfRowsSent;
                                oracleStatement.allocateDmlReturnStorage();
                                oracleStatement.setupReturnParamAccessors();
                                memoryHasBeenAllocated = true;
                            }
                            for (int row = 0; row < nbOfRowsSent; row++) {
                                acc.unmarshalOneRow();
                            }

                        }
                        oracleStatement.returnParamsFetched = true;

                    }
                    receiveState = 1;
                    continue;
                case 8:
                    if (rpaProcessed) {
                        DatabaseError.throwSqlException(401);
                    }
                    int length = meg.unmarshalUB2();
                    int outVect[] = new int[length];
                    for (int i = 0; i < length; i++) {
                        outVect[i] = (int) meg.unmarshalUB4();
                    }

                    cursor = outVect[2];
                    meg.unmarshalUB2();
                    int nbIgnore = meg.unmarshalUB2();
                    if (nbIgnore > 0) {
                        for (int i = 0; i < nbIgnore; i++) {
                            int ign9 = (int) meg.unmarshalUB4();
                            meg.unmarshalDALC();
                            int ign8 = meg.unmarshalUB2();
                        }

                    }
                    rpaProcessed = true;
                    break;
                case 16:
                    this.dcb.init(this.oracleStatement, 0);

                    this.definesAccessors = this.dcb.receive(this.definesAccessors);
                    this.numberOfDefinePositions = this.dcb.numuds;
                    this.definesLength = this.numberOfDefinePositions;

                    this.rxd.setNumberOfColumns(this.numberOfDefinePositions);

                    break;
                case 19:
                    this.fob.marshal();
                    break;
                case 4:
                    oer.init();
                    cursor = oer.unmarshal();
                    rowsProcessed = oer.getCurRowNumber();
                    if (typeOfStatement != 0 || typeOfStatement == 0 && oer.retCode != 1403) {
                        try {
                            oer.processError(oracleStatement);
                        } catch (SQLException sqlex) {
                            receiveState = 0;
                            throw sqlex;
                        }
                    }
                    break;
                case 5:
                case 9:
                case 10:
                case 12:
                case 13:
                case 14:
                case 15:
                case 17:
                case 18:
                case 20:
                default:
                    DatabaseError.throwSqlException(401);
                }

            } catch (BreakNetException ea) {
                if (this.receiveState != 1) {
                    DatabaseError.throwSqlException(447);
                }
                this.receiveState = 0;
                return;
            }
        }
    }

    int getCursorId() {
        return cursor;
    }

    void continueReadRow(int start) throws SQLException, IOException {
        receiveState = 2;
        if (rxd.unmarshal(definesAccessors, start, definesLength)) {
            receiveState = 3;
            return;
        } else {
            receive();
            return;
        }
    }

    int getNumRows() {
        int rows = 0;
        if (receiveState == 3) {
            rows = -2;
        } else {
            switch (typeOfStatement) {
            case 1: // '\001'
            case 2: // '\002'
            case 3: // '\003'
            case 4: // '\004'
                rows = rowsProcessed;
                break;

            case 0: // '\0'
                rows = definesAccessors == null || definesLength <= 0 ? 0
                        : definesAccessors[0].lastRowProcessed;
                break;
            }
        }
        return rows;
    }

    void marshalAll() throws SQLException, IOException {
        if ((options & 64L) != 0L && (options & 32L) == 0L && (options & 1L) == 0L
                && (options & 8L) == 0L && !oracleStatement.needToSendOalToFetch) {
            ofetch.marshal(cursor, (int) al8i4[1]);
        } else if (meg.versionNumber >= 10000 && (options & 32L) != 0L && (options & 1L) == 0L
                && (options & 64L) == 0L && (options & 8L) == 0L && typeOfStatement != 2
                && typeOfStatement != 1 && typeOfStatement != 4) {
            int execFlag = 0;
            if ((options & 256L) != 0L) {
                execFlag = 1;
            }
            oexec.marshal(cursor, (int) al8i4[1], execFlag);
            if (numberOfBindPositions > 0 && bindIndicators != null) {
                int oacmxlArr[] = new int[numberOfBindPositions];
                for (int i = 0; i < numberOfBindPositions; i++) {
                    oacmxlArr[i] = oacdefBindsSent[i].oacmxl;
                }

                marshalBinds(oacmxlArr);
            }
        } else {
            if (oracleStatement.needToSendOalToFetch) {
                oracleStatement.needToSendOalToFetch = false;
            }
            marshalPisdef();
            meg.marshalCHR(sqlStmt);
            meg.marshalUB4Array(al8i4);
            int oacmxlArr[] = new int[numberOfBindPositions];
            for (int i = 0; i < numberOfBindPositions; i++) {
                oacmxlArr[i] = oacdefBindsSent[i].oacmxl;
            }

            if ((options & 8L) != 0L && numberOfBindPositions > 0 && bindIndicators != null
                    && sendBindsDefinition) {
                marshalBindsTypes(oacdefBindsSent);
            }
            if (meg.versionNumber >= 9000 && sendDefines) {
                for (int i = 0; i < defCols; i++) {
                    oacdefDefines[i].marshal();
                }

            }
            if ((options & 32L) != 0L && numberOfBindPositions > 0 && bindIndicators != null) {
                marshalBinds(oacmxlArr);
            }
        }
    }

    void marshalPisdef() throws IOException, SQLException {
        super.marshalFunHeader();
        meg.marshalUB4(options);
        meg.marshalSWORD(cursor);
        if (sqlStmt.length == 0) {
            meg.marshalNULLPTR();
        } else {
            meg.marshalPTR();
        }
        meg.marshalSWORD(sqlStmt.length);
        if (al8i4.length == 0) {
            meg.marshalNULLPTR();
        } else {
            meg.marshalPTR();
        }
        meg.marshalSWORD(al8i4.length);
        meg.marshalNULLPTR();
        meg.marshalNULLPTR();
        meg.marshalUB4(0L);
        meg.marshalUB4(0L);
        if (typeOfStatement != 1 && typeOfStatement != 4) {
            meg.marshalUB4(0x7fffffffL);
        } else {
            meg.marshalUB4(32760L);
        }
        if ((options & 8L) != 0L && numberOfBindPositions > 0 && sendBindsDefinition) {
            meg.marshalPTR();
            meg.marshalSWORD(numberOfBindPositions);
        } else {
            meg.marshalNULLPTR();
            meg.marshalSWORD(0);
        }
        meg.marshalNULLPTR();
        meg.marshalNULLPTR();
        meg.marshalNULLPTR();
        meg.marshalNULLPTR();
        meg.marshalNULLPTR();
        if (meg.versionNumber >= 9000) {
            if (defCols > 0 && sendDefines) {
                meg.marshalPTR();
                meg.marshalSWORD(defCols);
            } else {
                meg.marshalNULLPTR();
                meg.marshalSWORD(0);
            }
        }
    }

    boolean initBindsDefinition(T4CTTIoac oacdefArr[]) throws SQLException, IOException {
        boolean needToUpdateDefinition = false;
        if (oacdefArr.length != numberOfBindPositions) {
            needToUpdateDefinition = true;
            oacdefArr = new T4CTTIoac[numberOfBindPositions];
        }
        short l_bindIndicators[] = bindIndicators;
        int _oacmxl = 0;
        int nbOfIbt = 0;
        for (int P = 0; P < numberOfBindPositions; P++) {
            int subRangeOffset = bindIndicatorSubRange + 3 + 10 * P;
            short formOfUse = l_bindIndicators[subRangeOffset + 9];
            int _oacdty = l_bindIndicators[subRangeOffset + 0] & 0xffff;
            if (_oacdty == 8 || _oacdty == 24) {
                if (plsql) {
                    _oacmxl = 32760;
                } else {
                    _oacmxl = 0x7fffffff;
                }
                oac.init((short) _oacdty, _oacmxl, dbCharSet, NCharSet, formOfUse);
            } else if (_oacdty == 998) {
                if (outBindAccessors != null && outBindAccessors[P] != null) {
                    oac.init((PlsqlIndexTableAccessor) outBindAccessors[P]);
                    nbOfIbt++;
                } else if (ibtBindIndicators[6 + nbOfIbt * 8] != 0) {
                    int typeElem = ibtBindIndicators[6 + nbOfIbt * 8];
                    int oacmal = ibtBindIndicators[6 + nbOfIbt * 8 + 2] << 16 & 0xffff000
                            | ibtBindIndicators[6 + nbOfIbt * 8 + 3];
                    _oacmxl = ibtBindIndicators[6 + nbOfIbt * 8 + 1];
                    oac.initIbt((short) typeElem, oacmal, _oacmxl);
                    nbOfIbt++;
                } else {
                    DatabaseError
                            .throwSqlException(
                                               "INTERNAL ERROR: Binding PLSQL index-by table but no type defined",
                                               null, -1);
                }
            } else if (_oacdty == 109 || _oacdty == 111) {
                if (outBindAccessors != null && outBindAccessors[P] != null) {
                    if (outBindAccessors[P].internalOtype != null) {
                        oac
                                .init(
                                      (OracleTypeADT) ((TypeAccessor) outBindAccessors[P]).internalOtype,
                                      _oacdty, _oacdty != 109 ? 4000 : 11);
                    }
                } else if (parameterOtype != null && parameterOtype[0] != null) {
                    oac.init(parameterOtype[0][P], _oacdty, _oacdty != 109 ? 4000 : 11);
                } else {
                    DatabaseError
                            .throwSqlException(
                                               "INTERNAL ERROR: Binding NAMED_TYPE but no type defined",
                                               null, -1);
                }
            } else if (_oacdty == 994) {
                int returnParamMetaLocal[] = oracleStatement.returnParamMeta;
                _oacdty = returnParamMetaLocal[3 + P * 3 + 0];
                _oacmxl = returnParamMetaLocal[3 + P * 3 + 2];
                if (_oacdty == 109 || _oacdty == 111) {
                    TypeAccessor typeAccessor = (TypeAccessor) oracleStatement.returnParamAccessors[P];
                    oac.init((OracleTypeADT) typeAccessor.internalOtype, (short) _oacdty,
                             _oacdty != 109 ? 4000 : 11);
                } else {
                    oac.init((short) _oacdty, _oacmxl, dbCharSet, NCharSet, formOfUse);
                }
            } else {
                _oacmxl = l_bindIndicators[subRangeOffset + 1] & 0xffff;
                if (_oacmxl == 0) {
                    if (typeOfStatement == 1 || oracleStatement.connection.versionNumber >= 10200
                            && typeOfStatement == 4) {
                        _oacmxl = 32512;
                    } else if (typeOfStatement == 4) {
                        int max = l_bindIndicators[subRangeOffset + 2] & 0xffff;
                        _oacmxl = max <= 4001 ? 4001 : max;
                    } else {
                        _oacmxl = l_bindIndicators[subRangeOffset + 2] & 0xffff;
                        if (_oacdty == 996) {
                            _oacmxl *= 2;
                        } else if (_oacmxl > 1) {
                            _oacmxl--;
                        }
                        if (formOfUse == 2) {
                            _oacmxl *= conversion.maxNCharSize;
                        } else if (((T4CConnection) oracleStatement.connection).retainV9BehaviorForLong
                                && _oacmxl <= 4000) {
                            _oacmxl = Math.min(_oacmxl * conversion.sMaxCharSize, 4000);
                        } else {
                            _oacmxl *= conversion.sMaxCharSize;
                        }
                    }
                }
                if (_oacdty == 9 || _oacdty == 996) {
                    _oacdty = 1;
                }
                oac.init((short) _oacdty, _oacmxl, dbCharSet, NCharSet, formOfUse);
            }
            if (oacdefArr[P] == null || !oac.isOldSufficient(oacdefArr[P])) {
                T4CTTIoac newOac = new T4CTTIoac(oac);
                oacdefArr[P] = newOac;
                needToUpdateDefinition = true;
            }
        }

        return needToUpdateDefinition;
    }

    void initDefinesDefinition() throws SQLException, IOException {
        defCols = 0;
        for (int i = 0; i < definedColumnType.length && definedColumnType[i] != 0; i++) {
            defCols++;
        }

        oacdefDefines = new T4CTTIoac[defCols];
        for (int i = 0; i < oacdefDefines.length; i++) {
            oacdefDefines[i] = new T4CTTIoac(meg);
            short internalType = (short) oracleStatement.getInternalType(definedColumnType[i]);
            int maxLength = 0x7fffffff;
            short formOfUse = 1;
            if (definedColumnFormOfUse != null && definedColumnFormOfUse.length > i
                    && definedColumnFormOfUse[i] == 2) {
                formOfUse = 2;
            }
            if (internalType == 8) {
                internalType = 1;
            } else if (internalType == 24) {
                internalType = 23;
            } else if (internalType == 1 || internalType == 96) {
                internalType = 1;
                maxLength = 4000 * conversion.sMaxCharSize;
                if (definedColumnSize != null && definedColumnSize.length > i
                        && definedColumnSize[i] > 0) {
                    maxLength = definedColumnSize[i] * conversion.sMaxCharSize;
                }
            } else if (internalType == 23) {
                maxLength = 4000;
            }
            oacdefDefines[i].init(internalType, maxLength, dbCharSet, NCharSet, formOfUse);
        }

    }

    void marshalBindsTypes(T4CTTIoac oac[]) throws SQLException, IOException {
        if (oac == null) {
            return;
        }
        for (int i = 0; i < oac.length; i++) {
            oac[i].marshal();
        }

    }

    void marshalBinds(int oacmxlArr[]) throws SQLException, IOException {
        int number_of_bound_rows = bindIndicators[bindIndicatorSubRange + 2] & 0xffff;
        for (int rowId = 0; rowId < number_of_bound_rows; rowId++) {
            int parameterIndex = oracleStatement.firstRowInBatch + rowId;
            InputStream streamParameters[] = null;
            if (parameterStream != null) {
                streamParameters = parameterStream[parameterIndex];
            }
            byte datumParameters[][] = (byte[][]) null;
            if (parameterDatum != null) {
                datumParameters = parameterDatum[parameterIndex];
            }
            OracleTypeADT otypeParameters[] = null;
            if (parameterOtype != null) {
                otypeParameters = parameterOtype[parameterIndex];
            }
            rxd.marshal(bindBytes, bindChars, bindIndicators, bindIndicatorSubRange,
                        tmpBindsByteArray, conversion, streamParameters, datumParameters,
                        otypeParameters, ibtBindBytes, ibtBindChars, ibtBindIndicators, null,
                        rowId, oacmxlArr, plsql, oracleStatement.returnParamMeta);
        }

    }

    long setOptions(boolean doParse, boolean doExecute, boolean doFetch) throws SQLException {
        long options = 0L;
        if (doParse && !doExecute && !doFetch) {
            options |= 1L;
        } else if (doParse && doExecute && !doFetch) {
            options = 32801L;
        } else if (doExecute && doFetch) {
            if (doParse) {
                options |= 1L;
            }
            switch (typeOfStatement) {
            case 0: // '\0'
                options |= 32864L;
                break;

            case 1: // '\001'
            case 4: // '\004'
                if (numberOfBindPositions > 0) {
                    options |= 1056L | (long) (oracleStatement.connection.autoCommitSet ? 256 : 0);
                    if (sendBindsDefinition) {
                        options |= 8L;
                    }
                } else {
                    options |= 32L | (long) (oracleStatement.connection.autoCommitSet ? 256 : 0);
                }
                break;

            case 2: // '\002'
            case 3: // '\003'
                if (oracleStatement.returnParamAccessors != null) {
                    options |= 1056L | (long) (oracleStatement.connection.autoCommitSet ? 256 : 0);
                } else {
                    options |= 32800L | (long) (oracleStatement.connection.autoCommitSet ? 256 : 0);
                }
                break;

            default:
                DatabaseError.throwSqlException(432);
                break;
            }
        } else if (!doParse && !doExecute && doFetch) {
            options = 32832L;
        } else {
            DatabaseError.throwSqlException(432);
        }
        if (typeOfStatement != 1 && typeOfStatement != 4) {
            if ((doParse || doExecute || !doFetch) && numberOfBindPositions > 0
                    && sendBindsDefinition) {
                options |= 8L;
            }
            if (meg.versionNumber >= 9000 && sendDefines) {
                options |= 16L;
            }
        }
        options &= -1L;
        return options;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8Oall"));
        } catch (Exception e) {
        }
    }
}
