package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.internal.ObjectData;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeNUMBER;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.CustomDatum;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.OpaqueDescriptor;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

public abstract class OraclePreparedStatement extends OracleStatement implements
        oracle.jdbc.internal.OraclePreparedStatement, ScrollRsetStatement {

    int numberOfBindRowsAllocated;
    static Binder theStaticVarnumCopyingBinder;
    static Binder theStaticVarnumNullBinder;
    Binder theVarnumNullBinder;
    static Binder theStaticBooleanBinder;
    Binder theBooleanBinder;
    static Binder theStaticByteBinder;
    Binder theByteBinder;
    static Binder theStaticShortBinder;
    Binder theShortBinder;
    static Binder theStaticIntBinder;
    Binder theIntBinder;
    static Binder theStaticLongBinder;
    Binder theLongBinder;
    static Binder theStaticFloatBinder;
    Binder theFloatBinder;
    static Binder theStaticDoubleBinder;
    Binder theDoubleBinder;
    static Binder theStaticBigDecimalBinder;
    Binder theBigDecimalBinder;
    static Binder theStaticVarcharCopyingBinder;
    static Binder theStaticVarcharNullBinder;
    Binder theVarcharNullBinder;
    static Binder theStaticStringBinder;
    Binder theStringBinder;
    static Binder theStaticSetCHARCopyingBinder;
    static Binder theStaticSetCHARBinder;
    static Binder theStaticLittleEndianSetCHARBinder;
    static Binder theStaticSetCHARNullBinder;
    Binder theSetCHARBinder;
    Binder theSetCHARNullBinder;
    static Binder theStaticFixedCHARCopyingBinder;
    static Binder theStaticFixedCHARBinder;
    static Binder theStaticFixedCHARNullBinder;
    Binder theFixedCHARBinder;
    Binder theFixedCHARNullBinder;
    static Binder theStaticDateCopyingBinder;
    static Binder theStaticDateBinder;
    static Binder theStaticDateNullBinder;
    Binder theDateBinder;
    Binder theDateNullBinder;
    static Binder theStaticTimeCopyingBinder;
    static Binder theStaticTimeBinder;
    Binder theTimeBinder;
    static Binder theStaticTimestampCopyingBinder;
    static Binder theStaticTimestampBinder;
    static Binder theStaticTimestampNullBinder;
    Binder theTimestampBinder;
    Binder theTimestampNullBinder;
    static Binder theStaticOracleNumberBinder;
    Binder theOracleNumberBinder;
    static Binder theStaticOracleDateBinder;
    Binder theOracleDateBinder;
    static Binder theStaticOracleTimestampBinder;
    Binder theOracleTimestampBinder;
    static Binder theStaticTSTZCopyingBinder;
    static Binder theStaticTSTZBinder;
    static Binder theStaticTSTZNullBinder;
    Binder theTSTZBinder;
    Binder theTSTZNullBinder;
    static Binder theStaticTSLTZCopyingBinder;
    static Binder theStaticTSLTZBinder;
    static Binder theStaticTSLTZNullBinder;
    Binder theTSLTZBinder;
    Binder theTSLTZNullBinder;
    static Binder theStaticRowidCopyingBinder;
    static Binder theStaticRowidBinder;
    static Binder theStaticLittleEndianRowidBinder;
    static Binder theStaticRowidNullBinder;
    Binder theRowidBinder;
    Binder theRowidNullBinder;
    static Binder theStaticIntervalDSCopyingBinder;
    static Binder theStaticIntervalDSBinder;
    static Binder theStaticIntervalDSNullBinder;
    Binder theIntervalDSBinder;
    Binder theIntervalDSNullBinder;
    static Binder theStaticIntervalYMCopyingBinder;
    static Binder theStaticIntervalYMBinder;
    static Binder theStaticIntervalYMNullBinder;
    Binder theIntervalYMBinder;
    Binder theIntervalYMNullBinder;
    static Binder theStaticBfileCopyingBinder;
    static Binder theStaticBfileBinder;
    static Binder theStaticBfileNullBinder;
    Binder theBfileBinder;
    Binder theBfileNullBinder;
    static Binder theStaticBlobCopyingBinder;
    static Binder theStaticBlobBinder;
    static Binder theStaticBlobNullBinder;
    Binder theBlobBinder;
    Binder theBlobNullBinder;
    static Binder theStaticClobCopyingBinder;
    static Binder theStaticClobBinder;
    static Binder theStaticClobNullBinder;
    Binder theClobBinder;
    Binder theClobNullBinder;
    static Binder theStaticRawCopyingBinder;
    static Binder theStaticRawBinder;
    static Binder theStaticRawNullBinder;
    Binder theRawBinder;
    Binder theRawNullBinder;
    static Binder theStaticPlsqlRawCopyingBinder;
    static Binder theStaticPlsqlRawBinder;
    Binder thePlsqlRawBinder;
    static Binder theStaticBinaryFloatCopyingBinder;
    static Binder theStaticBinaryFloatBinder;
    static Binder theStaticBinaryFloatNullBinder;
    Binder theBinaryFloatBinder;
    Binder theBinaryFloatNullBinder;
    static Binder theStaticBINARY_FLOATCopyingBinder;
    static Binder theStaticBINARY_FLOATBinder;
    static Binder theStaticBINARY_FLOATNullBinder;
    Binder theBINARY_FLOATBinder;
    Binder theBINARY_FLOATNullBinder;
    static Binder theStaticBinaryDoubleCopyingBinder;
    static Binder theStaticBinaryDoubleBinder;
    static Binder theStaticBinaryDoubleNullBinder;
    Binder theBinaryDoubleBinder;
    Binder theBinaryDoubleNullBinder;
    static Binder theStaticBINARY_DOUBLECopyingBinder;
    static Binder theStaticBINARY_DOUBLEBinder;
    static Binder theStaticBINARY_DOUBLENullBinder;
    Binder theBINARY_DOUBLEBinder;
    Binder theBINARY_DOUBLENullBinder;
    static Binder theStaticLongStreamBinder;
    Binder theLongStreamBinder;
    static Binder theStaticLongRawStreamBinder;
    Binder theLongRawStreamBinder;
    static Binder theStaticNamedTypeCopyingBinder;
    static Binder theStaticNamedTypeBinder;
    static Binder theStaticNamedTypeNullBinder;
    Binder theNamedTypeBinder;
    Binder theNamedTypeNullBinder;
    static Binder theStaticRefTypeCopyingBinder;
    static Binder theStaticRefTypeBinder;
    static Binder theStaticRefTypeNullBinder;
    Binder theRefTypeBinder;
    Binder theRefTypeNullBinder;
    static Binder theStaticPlsqlIbtCopyingBinder;
    static Binder theStaticPlsqlIbtBinder;
    static Binder theStaticPlsqlIbtNullBinder;
    Binder thePlsqlIbtBinder;
    Binder thePlsqlNullBinder;
    static Binder theStaticOutBinder;
    Binder theOutBinder;
    static Binder theStaticReturnParamBinder;
    Binder theReturnParamBinder;
    static Binder theStaticT4CRowidBinder;
    static Binder theStaticT4CRowidNullBinder;
    public static final int TypeBinder_BYTELEN = 24;
    char digits[];
    Binder binders[][];
    int parameterInt[][];
    long parameterLong[][];
    float parameterFloat[][];
    double parameterDouble[][];
    BigDecimal parameterBigDecimal[][];
    String parameterString[][];
    Date parameterDate[][];
    Time parameterTime[][];
    Timestamp parameterTimestamp[][];
    byte parameterDatum[][][];
    OracleTypeADT parameterOtype[][];
    PlsqlIbtBindInfo parameterPlsqlIbt[][];
    Binder currentRowBinders[];
    int currentRowCharLens[];
    Accessor currentRowBindAccessors[];
    short currentRowFormOfUse[];
    boolean currentRowNeedToPrepareBinds;
    int currentBatchCharLens[];
    Accessor currentBatchBindAccessors[];
    short currentBatchFormOfUse[];
    boolean currentBatchNeedToPrepareBinds;
    PushedBatch pushedBatches;
    PushedBatch pushedBatchesTail;
    int totalBindByteLength;
    int totalBindCharLength;
    int totalBindIndicatorLength;
    static final int BIND_METADATA_NUMBER_OF_BIND_POSITIONS_OFFSET = 0;
    static final int BIND_METADATA_BIND_BUFFER_CAPACITY_OFFSET = 1;
    static final int BIND_METADATA_NUMBER_OF_BOUND_ROWS_OFFSET = 2;
    static final int BIND_METADATA_PER_POSITION_DATA_OFFSET = 3;
    static final int BIND_METADATA_TYPE_OFFSET = 0;
    static final int BIND_METADATA_BYTE_PITCH_OFFSET = 1;
    static final int BIND_METADATA_CHAR_PITCH_OFFSET = 2;
    static final int BIND_METADATA_VALUE_DATA_OFFSET_HI = 3;
    static final int BIND_METADATA_VALUE_DATA_OFFSET_LO = 4;
    static final int BIND_METADATA_NULL_INDICATORS_OFFSET_HI = 5;
    static final int BIND_METADATA_NULL_INDICATORS_OFFSET_LO = 6;
    static final int BIND_METADATA_VALUE_LENGTHS_OFFSET_HI = 7;
    static final int BIND_METADATA_VALUE_LENGTHS_OFFSET_LO = 8;
    static final int BIND_METADATA_FORM_OF_USE_OFFSET = 9;
    static final int BIND_METADATA_PER_POSITION_SIZE = 10;
    int bindBufferCapacity;
    int numberOfBoundRows;
    int indicatorsOffset;
    int valueLengthsOffset;
    boolean preparedAllBinds;
    boolean preparedCharBinds;
    Binder lastBinders[];
    byte lastBoundBytes[];
    int lastBoundByteOffset;
    char lastBoundChars[];
    int lastBoundCharOffset;
    int lastBoundByteOffsets[];
    int lastBoundCharOffsets[];
    int lastBoundByteLens[];
    int lastBoundCharLens[];
    short lastBoundInds[];
    short lastBoundLens[];
    boolean lastBoundNeeded;
    byte lastBoundTypeBytes[][];
    OracleTypeADT lastBoundTypeOtypes[];
    private static final int STREAM_MAX_BYTES_SQL = 0x7fffffff;
    int maxRawBytesSql;
    int maxRawBytesPlsql;
    int maxVcsCharsSql;
    int maxVcsBytesPlsql;
    private int maxCharSize;
    private int maxNCharSize;
    private int charMaxCharsSql;
    private int charMaxNCharsSql;
    private int maxVcsCharsPlsql;
    private int maxVcsNCharsPlsql;
    private int maxStreamCharsSql;
    private int maxStreamNCharsSql;
    private boolean isServerCharSetFixedWidth;
    private boolean isServerNCharSetFixedWidth;
    int minVcsBindSize;
    int prematureBatchCount;
    boolean checkBindTypes;
    boolean scrollRsetTypeSolved;
    int SetBigStringTryClob;
    static final int BSTYLE_UNKNOWN = 0;
    static final int BSTYLE_ORACLE = 1;
    static final int BSTYLE_JDBC = 2;
    int m_batchStyle;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    OraclePreparedStatement(PhysicalConnection conn, String sql, int batchValue,
            int rowPrefetchValue) throws SQLException {
        this(conn, sql, batchValue, rowPrefetchValue, 1003, 1007);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OraclePreparedStatement.OraclePreparedStatement(conn, sql, batchValue, rowPrefetchValue)",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    OraclePreparedStatement(PhysicalConnection conn, String sql, int batch_value,
            int row_prefetch_value, int UserResultSetType, int UserResultSetConcur)
            throws SQLException {
        super(conn, batch_value, row_prefetch_value, UserResultSetType, UserResultSetConcur);
        theVarnumNullBinder = theStaticVarnumNullBinder;
        theBooleanBinder = theStaticBooleanBinder;
        theByteBinder = theStaticByteBinder;
        theShortBinder = theStaticShortBinder;
        theIntBinder = theStaticIntBinder;
        theLongBinder = theStaticLongBinder;
        theFloatBinder = null;
        theDoubleBinder = null;
        theBigDecimalBinder = theStaticBigDecimalBinder;
        theVarcharNullBinder = theStaticVarcharNullBinder;
        theStringBinder = theStaticStringBinder;
        theSetCHARNullBinder = theStaticSetCHARNullBinder;
        theFixedCHARBinder = theStaticFixedCHARBinder;
        theFixedCHARNullBinder = theStaticFixedCHARNullBinder;
        theDateBinder = theStaticDateBinder;
        theDateNullBinder = theStaticDateNullBinder;
        theTimeBinder = theStaticTimeBinder;
        theTimestampBinder = theStaticTimestampBinder;
        theTimestampNullBinder = theStaticTimestampNullBinder;
        theOracleNumberBinder = theStaticOracleNumberBinder;
        theOracleDateBinder = theStaticOracleDateBinder;
        theOracleTimestampBinder = theStaticOracleTimestampBinder;
        theTSTZBinder = theStaticTSTZBinder;
        theTSTZNullBinder = theStaticTSTZNullBinder;
        theTSLTZBinder = theStaticTSLTZBinder;
        theTSLTZNullBinder = theStaticTSLTZNullBinder;
        theRowidNullBinder = theStaticRowidNullBinder;
        theIntervalDSBinder = theStaticIntervalDSBinder;
        theIntervalDSNullBinder = theStaticIntervalDSNullBinder;
        theIntervalYMBinder = theStaticIntervalYMBinder;
        theIntervalYMNullBinder = theStaticIntervalYMNullBinder;
        theBfileBinder = theStaticBfileBinder;
        theBfileNullBinder = theStaticBfileNullBinder;
        theBlobBinder = theStaticBlobBinder;
        theBlobNullBinder = theStaticBlobNullBinder;
        theClobBinder = theStaticClobBinder;
        theClobNullBinder = theStaticClobNullBinder;
        theRawBinder = theStaticRawBinder;
        theRawNullBinder = theStaticRawNullBinder;
        thePlsqlRawBinder = theStaticPlsqlRawBinder;
        theBinaryFloatBinder = theStaticBinaryFloatBinder;
        theBinaryFloatNullBinder = theStaticBinaryFloatNullBinder;
        theBINARY_FLOATBinder = theStaticBINARY_FLOATBinder;
        theBINARY_FLOATNullBinder = theStaticBINARY_FLOATNullBinder;
        theBinaryDoubleBinder = theStaticBinaryDoubleBinder;
        theBinaryDoubleNullBinder = theStaticBinaryDoubleNullBinder;
        theBINARY_DOUBLEBinder = theStaticBINARY_DOUBLEBinder;
        theBINARY_DOUBLENullBinder = theStaticBINARY_DOUBLENullBinder;
        theLongStreamBinder = theStaticLongStreamBinder;
        theLongRawStreamBinder = theStaticLongRawStreamBinder;
        theNamedTypeBinder = theStaticNamedTypeBinder;
        theNamedTypeNullBinder = theStaticNamedTypeNullBinder;
        theRefTypeBinder = theStaticRefTypeBinder;
        theRefTypeNullBinder = theStaticRefTypeNullBinder;
        thePlsqlIbtBinder = theStaticPlsqlIbtBinder;
        thePlsqlNullBinder = theStaticPlsqlIbtNullBinder;
        theOutBinder = theStaticOutBinder;
        theReturnParamBinder = theStaticReturnParamBinder;
        digits = new char[20];
        currentRowNeedToPrepareBinds = true;
        lastBoundNeeded = false;
        maxCharSize = 0;
        maxNCharSize = 0;
        charMaxCharsSql = 0;
        charMaxNCharsSql = 0;
        maxVcsCharsPlsql = 0;
        maxVcsNCharsPlsql = 0;
        maxStreamCharsSql = 0;
        maxStreamNCharsSql = 0;
        isServerCharSetFixedWidth = false;
        isServerNCharSetFixedWidth = false;
        checkBindTypes = true;
        SetBigStringTryClob = 0;
        m_batchStyle = 0;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OraclePreparedStatement.OraclePreparedStatement(conn, sql, batchValue, rowPrefetchValue, UserResultSetType)",
                         this);
            OracleLog.recursiveTrace = false;
        }
        theSetCHARBinder = conn.useLittleEndianSetCHARBinder() ? theStaticLittleEndianSetCHARBinder
                : theStaticSetCHARBinder;
        theRowidBinder = conn.useLittleEndianSetCHARBinder() ? theStaticLittleEndianRowidBinder
                : theStaticRowidBinder;
        statementType = 1;
        currentRow = -1;
        needToParse = true;
        processEscapes = conn.processEscapes;
        sqlObject.initialize(sql);
        sqlKind = sqlObject.getSqlKind();
        clearParameters = true;
        scrollRsetTypeSolved = false;
        prematureBatchCount = 0;
        initializeBinds();
        minVcsBindSize = conn.minVcsBindSize;
        maxRawBytesSql = conn.maxRawBytesSql;
        maxRawBytesPlsql = conn.maxRawBytesPlsql;
        maxVcsCharsSql = conn.maxVcsCharsSql;
        maxVcsBytesPlsql = conn.maxVcsBytesPlsql;
        maxCharSize = connection.conversion.sMaxCharSize;
        maxNCharSize = connection.conversion.maxNCharSize;
        maxVcsCharsPlsql = maxVcsBytesPlsql / maxCharSize;
        maxVcsNCharsPlsql = maxVcsBytesPlsql / maxNCharSize;
        maxStreamCharsSql = 0x7fffffff / maxCharSize;
        maxStreamNCharsSql = maxRawBytesSql / maxNCharSize;
        isServerCharSetFixedWidth = connection.conversion.isServerCharSetFixedWidth;
        isServerNCharSetFixedWidth = connection.conversion.isServerNCharSetFixedWidth;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OraclePreparedStatement.OraclePreparedStatement(conn, sql, UserResultSetType):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    void allocBinds(int new_number_of_bind_rows_allocated) throws SQLException {
        boolean growing = new_number_of_bind_rows_allocated > numberOfBindRowsAllocated;
        initializeIndicatorSubRange();
        int preambleSize = bindIndicatorSubRange + 3 + numberOfBindPositions * 10;
        int dataSizeTerm = new_number_of_bind_rows_allocated * numberOfBindPositions;
        int indicatorSize = preambleSize + 2 * dataSizeTerm;
        if (indicatorSize > totalBindIndicatorLength) {
            short oldBindIndicators[] = bindIndicators;
            int oldBindIndicatorOffset = bindIndicatorOffset;
            bindIndicatorOffset = 0;
            bindIndicators = new short[indicatorSize];
            totalBindIndicatorLength = indicatorSize;
            if (oldBindIndicators != null && growing) {
                System.arraycopy(oldBindIndicators, oldBindIndicatorOffset, bindIndicators,
                                 bindIndicatorOffset, preambleSize);
            }
        }
        bindIndicatorSubRange += bindIndicatorOffset;
        bindIndicators[bindIndicatorSubRange + 0] = (short) numberOfBindPositions;
        indicatorsOffset = bindIndicatorOffset + preambleSize;
        valueLengthsOffset = indicatorsOffset + dataSizeTerm;
        int indoffset = indicatorsOffset;
        int lenoffset = valueLengthsOffset;
        int metadata_offset = bindIndicatorSubRange + 3;
        for (int i = 0; i < numberOfBindPositions; i++) {
            bindIndicators[metadata_offset + 5] = (short) (indoffset >> 16);
            bindIndicators[metadata_offset + 6] = (short) (indoffset & 0xffff);
            bindIndicators[metadata_offset + 7] = (short) (lenoffset >> 16);
            bindIndicators[metadata_offset + 8] = (short) (lenoffset & 0xffff);
            indoffset += new_number_of_bind_rows_allocated;
            lenoffset += new_number_of_bind_rows_allocated;
            metadata_offset += 10;
        }

    }

    void initializeBinds() throws SQLException {
        numberOfBindPositions = sqlObject.getParameterCount();
        if (numberOfBindPositions == 0) {
            currentRowNeedToPrepareBinds = false;
            return;
        }
        numberOfBindRowsAllocated = batch;
        binders = new Binder[numberOfBindRowsAllocated][numberOfBindPositions];
        currentRowBinders = binders[0];
        currentRowCharLens = new int[numberOfBindPositions];
        currentBatchCharLens = new int[numberOfBindPositions];
        currentRowFormOfUse = new short[numberOfBindPositions];
        currentBatchFormOfUse = new short[numberOfBindPositions];
        short defaultFormOfUse = 1;
        if (connection.defaultNChar) {
            defaultFormOfUse = 2;
        }
        for (int i = 0; i < numberOfBindPositions; i++) {
            currentRowFormOfUse[i] = defaultFormOfUse;
            currentBatchFormOfUse[i] = defaultFormOfUse;
        }

        lastBinders = new Binder[numberOfBindPositions];
        lastBoundCharLens = new int[numberOfBindPositions];
        lastBoundByteOffsets = new int[numberOfBindPositions];
        lastBoundCharOffsets = new int[numberOfBindPositions];
        lastBoundByteLens = new int[numberOfBindPositions];
        lastBoundInds = new short[numberOfBindPositions];
        lastBoundLens = new short[numberOfBindPositions];
        lastBoundTypeBytes = new byte[numberOfBindPositions][];
        lastBoundTypeOtypes = new OracleTypeADT[numberOfBindPositions];
        allocBinds(numberOfBindRowsAllocated);
    }

    void growBinds(int new_number_of_bind_rows_allocated) throws SQLException {
        Binder oldBinders[][] = binders;
        binders = new Binder[new_number_of_bind_rows_allocated][];
        if (oldBinders != null) {
            System.arraycopy(oldBinders, 0, binders, 0, numberOfBindRowsAllocated);
        }
        for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
            binders[i] = new Binder[numberOfBindPositions];
        }

        allocBinds(new_number_of_bind_rows_allocated);
        if (parameterInt != null) {
            int oldParameterInt[][] = parameterInt;
            parameterInt = new int[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterInt, 0, parameterInt, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterInt[i] = new int[numberOfBindPositions];
            }

        }
        if (parameterLong != null) {
            long oldParameterLong[][] = parameterLong;
            parameterLong = new long[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterLong, 0, parameterLong, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterLong[i] = new long[numberOfBindPositions];
            }

        }
        if (parameterFloat != null) {
            float oldParameterFloat[][] = parameterFloat;
            parameterFloat = new float[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterFloat, 0, parameterFloat, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterFloat[i] = new float[numberOfBindPositions];
            }

        }
        if (parameterDouble != null) {
            double oldParameterDouble[][] = parameterDouble;
            parameterDouble = new double[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterDouble, 0, parameterDouble, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterDouble[i] = new double[numberOfBindPositions];
            }

        }
        if (parameterBigDecimal != null) {
            BigDecimal oldParameterBigDecimal[][] = parameterBigDecimal;
            parameterBigDecimal = new BigDecimal[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterBigDecimal, 0, parameterBigDecimal, 0,
                             numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterBigDecimal[i] = new BigDecimal[numberOfBindPositions];
            }

        }
        if (parameterString != null) {
            String oldParameterString[][] = parameterString;
            parameterString = new String[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterString, 0, parameterString, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterString[i] = new String[numberOfBindPositions];
            }

        }
        if (parameterDate != null) {
            Date oldParameterDate[][] = parameterDate;
            parameterDate = new Date[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterDate, 0, parameterDate, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterDate[i] = new Date[numberOfBindPositions];
            }

        }
        if (parameterTime != null) {
            Time oldParameterTime[][] = parameterTime;
            parameterTime = new Time[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterTime, 0, parameterTime, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterTime[i] = new Time[numberOfBindPositions];
            }

        }
        if (parameterTimestamp != null) {
            Timestamp oldParameterTimestamp[][] = parameterTimestamp;
            parameterTimestamp = new Timestamp[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterTimestamp, 0, parameterTimestamp, 0,
                             numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterTimestamp[i] = new Timestamp[numberOfBindPositions];
            }

        }
        if (parameterDatum != null) {
            byte oldParameterDatum[][][] = parameterDatum;
            parameterDatum = new byte[new_number_of_bind_rows_allocated][][];
            System.arraycopy(oldParameterDatum, 0, parameterDatum, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterDatum[i] = new byte[numberOfBindPositions][];
            }

        }
        if (parameterOtype != null) {
            OracleTypeADT oldParameterOtype[][] = parameterOtype;
            parameterOtype = new OracleTypeADT[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterOtype, 0, parameterOtype, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterOtype[i] = new OracleTypeADT[numberOfBindPositions];
            }

        }
        if (parameterStream != null) {
            InputStream oldParameterStream[][] = parameterStream;
            parameterStream = new InputStream[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterStream, 0, parameterStream, 0, numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterStream[i] = new InputStream[numberOfBindPositions];
            }

        }
        if (parameterPlsqlIbt != null) {
            PlsqlIbtBindInfo oldParameterPlsqlIbt[][] = parameterPlsqlIbt;
            parameterPlsqlIbt = new PlsqlIbtBindInfo[new_number_of_bind_rows_allocated][];
            System.arraycopy(oldParameterPlsqlIbt, 0, parameterPlsqlIbt, 0,
                             numberOfBindRowsAllocated);
            for (int i = numberOfBindRowsAllocated; i < new_number_of_bind_rows_allocated; i++) {
                parameterPlsqlIbt[i] = new PlsqlIbtBindInfo[numberOfBindPositions];
            }

        }
        numberOfBindRowsAllocated = new_number_of_bind_rows_allocated;
        currentRowNeedToPrepareBinds = true;
    }

    void processCompletedBindRow(int number_of_rows_in_batch, boolean batchForPlsql)
            throws SQLException {
        if (numberOfBindPositions == 0) {
            return;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "processCompletedBindRow("
                    + number_of_rows_in_batch + ", " + batchForPlsql + ")" + " currentRank "
                    + currentRank + " checkBindTypes " + checkBindTypes, this);
            OracleLog.recursiveTrace = false;
        }
        boolean bindTypeChange = false;
        boolean bindTypeConflict = false;
        boolean isFirstRowInBatch = currentRank == firstRowInBatch;
        Binder prevBinders[] = currentRank != 0 ? binders[currentRank - 1]
                : lastBinders[0] != null ? lastBinders : null;
        if (currentRowBindAccessors == null) {
            if (prevBinders == null) {
                for (int i = 0; i < numberOfBindPositions; i++) {
                    if (currentRowBinders[i] == null) {
                        DatabaseError.throwSqlException(41, new Integer(i + 1));
                    }
                }

            } else if (checkBindTypes) {
                OracleTypeADT prevOtypes[] = currentRank != 0 ? parameterOtype != null ? parameterOtype[currentRank - 1]
                        : null
                        : lastBoundTypeOtypes;
                for (int i = 0; i < numberOfBindPositions; i++) {
                    Binder binder = currentRowBinders[i];
                    if (binder == null) {
                        if (clearParameters) {
                            DatabaseError.throwSqlException(41, new Integer(i + 1));
                        }
                        currentRowBinders[i] = prevBinders[i].copyingBinder();
                        currentRowCharLens[i] = -1;
                        if (isFirstRowInBatch) {
                            lastBoundNeeded = true;
                        }
                    } else {
                        short type = binder.type;
                        if (type != prevBinders[i].type || (type == 109 || type == 111)
                                && !parameterOtype[currentRank][i].isInHierarchyOf(prevOtypes[i])
                                || type == 9
                                && (binder.bytelen == 0) != (prevBinders[i].bytelen == 0)) {
                            bindTypeChange = true;
                        }
                    }
                    if (currentBatchFormOfUse[i] != currentRowFormOfUse[i]) {
                        bindTypeChange = true;
                    }
                }

            } else {
                for (int i = 0; i < numberOfBindPositions; i++) {
                    Binder binder = currentRowBinders[i];
                    if (binder == null) {
                        if (clearParameters) {
                            DatabaseError.throwSqlException(41, new Integer(i + 1));
                        }
                        currentRowBinders[i] = prevBinders[i].copyingBinder();
                        currentRowCharLens[i] = -1;
                        if (isFirstRowInBatch) {
                            lastBoundNeeded = true;
                        }
                    }
                }

            }
        } else if (prevBinders == null) {
            for (int i = 0; i < numberOfBindPositions; i++) {
                Binder binder = currentRowBinders[i];
                Accessor accessor = currentRowBindAccessors[i];
                if (binder == null) {
                    if (accessor == null) {
                        DatabaseError.throwSqlException(41, new Integer(i + 1));
                    } else {
                        currentRowBinders[i] = theOutBinder;
                    }
                } else if (accessor != null
                        && accessor.defineType != binder.type
                        && (!connection.looseTimestampDateCheck || binder.type != 180 || accessor.defineType != 12)) {
                    bindTypeConflict = true;
                }
            }

        } else if (checkBindTypes) {
            OracleTypeADT prevOtypes[] = currentRank != 0 ? parameterOtype != null ? parameterOtype[currentRank - 1]
                    : null
                    : lastBoundTypeOtypes;
            for (int i = 0; i < numberOfBindPositions; i++) {
                Binder binder = currentRowBinders[i];
                Accessor accessor = currentRowBindAccessors[i];
                if (binder == null) {
                    if (clearParameters && prevBinders[i] != theOutBinder) {
                        DatabaseError.throwSqlException(41, new Integer(i + 1));
                    }
                    binder = prevBinders[i];
                    currentRowBinders[i] = binder;
                    currentRowCharLens[i] = -1;
                    if (isFirstRowInBatch && binder != theOutBinder) {
                        lastBoundNeeded = true;
                    }
                } else {
                    short type = binder.type;
                    if (type != prevBinders[i].type || (type == 109 || type == 111)
                            && !parameterOtype[currentRank][i].isInHierarchyOf(prevOtypes[i])
                            || type == 9 && (binder.bytelen == 0) != (prevBinders[i].bytelen == 0)) {
                        bindTypeChange = true;
                    }
                }
                if (currentBatchFormOfUse[i] != currentRowFormOfUse[i]) {
                    bindTypeChange = true;
                }
                Accessor lastAccessor = currentBatchBindAccessors[i];
                if (accessor == null) {
                    accessor = lastAccessor;
                    currentRowBindAccessors[i] = accessor;
                } else if (lastAccessor != null && accessor.defineType != lastAccessor.defineType) {
                    bindTypeChange = true;
                }
                if (accessor != null
                        && binder != theOutBinder
                        && accessor.defineType != binder.type
                        && (!connection.looseTimestampDateCheck || binder.type != 180 || accessor.defineType != 12)) {
                    bindTypeConflict = true;
                }
            }

        } else {
            for (int i = 0; i < numberOfBindPositions; i++) {
                Binder binder = currentRowBinders[i];
                if (binder == null) {
                    if (clearParameters && prevBinders[i] != theOutBinder) {
                        DatabaseError.throwSqlException(41, new Integer(i + 1));
                    }
                    binder = prevBinders[i];
                    currentRowBinders[i] = binder;
                    currentRowCharLens[i] = -1;
                    if (isFirstRowInBatch && binder != theOutBinder) {
                        lastBoundNeeded = true;
                    }
                }
                if (currentRowBindAccessors[i] == null) {
                    currentRowBindAccessors[i] = currentBatchBindAccessors[i];
                }
            }

        }
        if (bindTypeChange) {
            if (!isFirstRowInBatch) {
                if (m_batchStyle == 2) {
                    pushBatch(false);
                } else {
                    int save_valid_rows = validRows;
                    prematureBatchCount = sendBatch();
                    validRows = save_valid_rows;
                }
            }
            needToParse = true;
            currentRowNeedToPrepareBinds = true;
        } else if (batchForPlsql) {
            pushBatch(false);
            needToParse = false;
            currentBatchNeedToPrepareBinds = false;
        }
        if (bindTypeConflict) {
            DatabaseError.throwSqlException(12);
        }
        for (int i = 0; i < numberOfBindPositions; i++) {
            int charlen = currentRowCharLens[i];
            if (charlen == -1) {
                charlen = lastBoundCharLens[i];
            }
            if (currentBatchCharLens[i] < charlen) {
                currentBatchCharLens[i] = charlen;
            }
            currentRowCharLens[i] = 0;
            currentBatchFormOfUse[i] = currentRowFormOfUse[i];
        }

        if (currentRowNeedToPrepareBinds) {
            currentBatchNeedToPrepareBinds = true;
        }
        if (currentRowBindAccessors != null) {
            Accessor tmp[] = currentBatchBindAccessors;
            currentBatchBindAccessors = currentRowBindAccessors;
            if (tmp == null) {
                tmp = new Accessor[numberOfBindPositions];
            } else {
                for (int i = 0; i < numberOfBindPositions; i++) {
                    tmp[i] = null;
                }

            }
            currentRowBindAccessors = tmp;
        }
        int nextRank = currentRank + 1;
        if (nextRank < number_of_rows_in_batch) {
            if (nextRank >= numberOfBindRowsAllocated) {
                int new_number_of_bind_rows_allocated = numberOfBindRowsAllocated << 1;
                if (new_number_of_bind_rows_allocated <= nextRank) {
                    new_number_of_bind_rows_allocated = nextRank + 1;
                }
                growBinds(new_number_of_bind_rows_allocated);
                currentBatchNeedToPrepareBinds = true;
            }
            currentRowBinders = binders[nextRank];
        } else {
            setupBindBuffers(0, number_of_rows_in_batch);
            currentRowBinders = binders[0];
        }
        currentRowNeedToPrepareBinds = false;
        clearParameters = false;
    }

    void processPlsqlIndexTabBinds(int cur_row_to_be_bound) throws SQLException {
        int num_plsql_index_tab_binds = 0;
        int total_elements = 0;
        int bytes = 0;
        int chars = 0;
        Binder curRowBinders[] = binders[cur_row_to_be_bound];
        PlsqlIbtBindInfo inBindInfos[] = parameterPlsqlIbt != null ? parameterPlsqlIbt[cur_row_to_be_bound]
                : null;
        for (int i = 0; i < numberOfBindPositions; i++) {
            Binder binder = curRowBinders[i];
            Accessor accessor = currentBatchBindAccessors != null ? currentBatchBindAccessors[i]
                    : null;
            PlsqlIndexTableAccessor ibtAccessor = accessor != null && accessor.defineType == 998 ? (PlsqlIndexTableAccessor) accessor
                    : null;
            if (binder.type == 998) {
                PlsqlIbtBindInfo inBindInfo = inBindInfos[i];
                if (ibtAccessor != null) {
                    if (inBindInfo.element_internal_type != ibtAccessor.elementInternalType) {
                        DatabaseError.throwSqlException(12);
                    }
                    if (inBindInfo.maxLen < ibtAccessor.maxNumberOfElements) {
                        inBindInfo.maxLen = ibtAccessor.maxNumberOfElements;
                    }
                    if (inBindInfo.elemMaxLen < ibtAccessor.elementMaxLen) {
                        inBindInfo.elemMaxLen = ibtAccessor.elementMaxLen;
                    }
                    if (inBindInfo.ibtByteLength > 0) {
                        inBindInfo.ibtByteLength = inBindInfo.elemMaxLen * inBindInfo.maxLen;
                    } else {
                        inBindInfo.ibtCharLength = inBindInfo.elemMaxLen * inBindInfo.maxLen;
                    }
                }
                num_plsql_index_tab_binds++;
                bytes += inBindInfo.ibtByteLength;
                chars += inBindInfo.ibtCharLength;
                total_elements += inBindInfo.maxLen;
                continue;
            }
            if (ibtAccessor != null) {
                num_plsql_index_tab_binds++;
                bytes += ibtAccessor.ibtByteLength;
                chars += ibtAccessor.ibtCharLength;
                total_elements += ibtAccessor.maxNumberOfElements;
            }
        }

        if (num_plsql_index_tab_binds == 0) {
            return;
        }
        ibtBindIndicatorSize = 6 + num_plsql_index_tab_binds * 8 + total_elements * 2;
        ibtBindIndicators = new short[ibtBindIndicatorSize];
        ibtBindIndicatorOffset = 0;
        if (bytes > 0) {
            ibtBindBytes = new byte[bytes];
        }
        ibtBindByteOffset = 0;
        if (chars > 0) {
            ibtBindChars = new char[chars];
        }
        ibtBindCharOffset = 0;
        int byteOffset = ibtBindByteOffset;
        int charOffset = ibtBindCharOffset;
        int indOffset = ibtBindIndicatorOffset;
        int offset = indOffset + 6 + num_plsql_index_tab_binds * 8;
        ibtBindIndicators[indOffset++] = (short) (num_plsql_index_tab_binds >> 16);
        ibtBindIndicators[indOffset++] = (short) (num_plsql_index_tab_binds & 0xffff);
        ibtBindIndicators[indOffset++] = (short) (bytes >> 16);
        ibtBindIndicators[indOffset++] = (short) (bytes & 0xffff);
        ibtBindIndicators[indOffset++] = (short) (chars >> 16);
        ibtBindIndicators[indOffset++] = (short) (chars & 0xffff);
        for (int i = 0; i < numberOfBindPositions; i++) {
            Binder binder = curRowBinders[i];
            Accessor accessor = currentBatchBindAccessors != null ? currentBatchBindAccessors[i]
                    : null;
            PlsqlIndexTableAccessor ibtAccessor = accessor != null && accessor.defineType == 998 ? (PlsqlIndexTableAccessor) accessor
                    : null;
            int byteOrCharOffset;
            if (binder.type == 998) {
                PlsqlIbtBindInfo inBindInfo = inBindInfos[i];
                int maxlen = inBindInfo.maxLen;
                ibtBindIndicators[indOffset++] = (short) inBindInfo.element_internal_type;
                ibtBindIndicators[indOffset++] = (short) inBindInfo.elemMaxLen;
                ibtBindIndicators[indOffset++] = (short) (maxlen >> 16);
                ibtBindIndicators[indOffset++] = (short) (maxlen & 0xffff);
                ibtBindIndicators[indOffset++] = (short) (inBindInfo.curLen >> 16);
                ibtBindIndicators[indOffset++] = (short) (inBindInfo.curLen & 0xffff);
                if (inBindInfo.ibtByteLength > 0) {
                    byteOrCharOffset = byteOffset;
                    byteOffset += inBindInfo.ibtByteLength;
                } else {
                    byteOrCharOffset = charOffset;
                    charOffset += inBindInfo.ibtCharLength;
                }
                ibtBindIndicators[indOffset++] = (short) (byteOrCharOffset >> 16);
                ibtBindIndicators[indOffset++] = (short) (byteOrCharOffset & 0xffff);
                inBindInfo.ibtValueIndex = byteOrCharOffset;
                inBindInfo.ibtIndicatorIndex = offset;
                inBindInfo.ibtLengthIndex = offset + maxlen;
                if (ibtAccessor != null) {
                    ibtAccessor.ibtIndicatorIndex = inBindInfo.ibtIndicatorIndex;
                    ibtAccessor.ibtLengthIndex = inBindInfo.ibtLengthIndex;
                    ibtAccessor.ibtMetaIndex = indOffset - 8;
                    ibtAccessor.ibtValueIndex = byteOrCharOffset;
                }
                offset += 2 * maxlen;
                continue;
            }
            if (ibtAccessor == null) {
                continue;
            }
            int maxlen = ibtAccessor.maxNumberOfElements;
            ibtBindIndicators[indOffset++] = (short) ibtAccessor.elementInternalType;
            ibtBindIndicators[indOffset++] = (short) ibtAccessor.elementMaxLen;
            ibtBindIndicators[indOffset++] = (short) (maxlen >> 16);
            ibtBindIndicators[indOffset++] = (short) (maxlen & 0xffff);
            ibtBindIndicators[indOffset++] = 0;
            ibtBindIndicators[indOffset++] = 0;
            if (ibtAccessor.ibtByteLength > 0) {
                byteOrCharOffset = byteOffset;
                byteOffset += ibtAccessor.ibtByteLength;
            } else {
                byteOrCharOffset = charOffset;
                charOffset += ibtAccessor.ibtCharLength;
            }
            ibtBindIndicators[indOffset++] = (short) (byteOrCharOffset >> 16);
            ibtBindIndicators[indOffset++] = (short) (byteOrCharOffset & 0xffff);
            ibtAccessor.ibtValueIndex = byteOrCharOffset;
            ibtAccessor.ibtIndicatorIndex = offset;
            ibtAccessor.ibtLengthIndex = offset + maxlen;
            ibtAccessor.ibtMetaIndex = indOffset - 8;
            offset += 2 * maxlen;
        }

    }

    void initializeBindSubRanges(int number_of_rows_to_be_bound, int number_of_rows_to_be_set_up) {
        bindByteSubRange = 0;
        bindCharSubRange = 0;
    }

    void initializeIndicatorSubRange() {
        bindIndicatorSubRange = 0;
    }

    void prepareBindPreambles(int i, int j) {
    }

    void setupBindBuffers(int first_row_to_be_bound, int number_of_rows_to_be_bound)
            throws SQLException {
        if (numberOfBindPositions == 0) {
            return;
        }
        try {
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINE, "setupBindBuffers(" + first_row_to_be_bound + ", "
                                + number_of_rows_to_be_bound + ")"
                                + " currentBatchNeedToPrepareBinds="
                                + currentBatchNeedToPrepareBinds, this);
                OracleLog.recursiveTrace = false;
            }
            preparedAllBinds = currentBatchNeedToPrepareBinds;
            preparedCharBinds = false;
            currentBatchNeedToPrepareBinds = false;
            numberOfBoundRows = number_of_rows_to_be_bound;
            bindIndicators[bindIndicatorSubRange + 2] = (short) numberOfBoundRows;
            int number_of_rows_to_be_set_up = bindBufferCapacity;
            if (numberOfBoundRows > bindBufferCapacity) {
                number_of_rows_to_be_set_up = numberOfBoundRows;
                preparedAllBinds = true;
            }
            if (currentBatchBindAccessors != null) {
                if (outBindAccessors == null) {
                    outBindAccessors = new Accessor[numberOfBindPositions];
                }
                for (int i = 0; i < numberOfBindPositions; i++) {
                    Accessor accessor = currentBatchBindAccessors[i];
                    outBindAccessors[i] = accessor;
                    if (accessor == null) {
                        continue;
                    }
                    int charlen = accessor.charLength;
                    if (currentBatchCharLens[i] < charlen) {
                        currentBatchCharLens[i] = charlen;
                    }
                }

            }
            int row_charlen = 0;
            int row_bytelen = 0;
            int per_position_metadata_offset = bindIndicatorSubRange + 3;
            int metadata_offset = per_position_metadata_offset;
            if (preparedAllBinds) {
                preparedCharBinds = true;
                Binder firstRankBinders[] = binders[first_row_to_be_bound];
                for (int i = 0; i < numberOfBindPositions; i++) {
                    Binder binder = firstRankBinders[i];
                    int charlen = currentBatchCharLens[i];
                    short type;
                    int bytelen;
                    if (binder == theOutBinder) {
                        Accessor accessor = currentBatchBindAccessors[i];
                        bytelen = accessor.byteLength;
                        type = (short) accessor.defineType;
                    } else {
                        bytelen = binder.bytelen;
                        type = binder.type;
                    }
                    row_bytelen += bytelen;
                    row_charlen += charlen;
                    bindIndicators[metadata_offset + 0] = type;
                    bindIndicators[metadata_offset + 1] = (short) bytelen;
                    bindIndicators[metadata_offset + 2] = (short) charlen;
                    bindIndicators[metadata_offset + 9] = currentBatchFormOfUse[i];
                    metadata_offset += 10;
                }

            } else if (preparedCharBinds) {
                for (int i = 0; i < numberOfBindPositions; i++) {
                    int charlen = currentBatchCharLens[i];
                    row_charlen += charlen;
                    bindIndicators[metadata_offset + 2] = (short) charlen;
                    metadata_offset += 10;
                }

            } else {
                for (int i = 0; i < numberOfBindPositions; i++) {
                    int pitchOffset = metadata_offset + 2;
                    int charlen = currentBatchCharLens[i];
                    int oldcharlen = bindIndicators[pitchOffset];
                    if (oldcharlen >= charlen && !preparedCharBinds) {
                        currentBatchCharLens[i] = oldcharlen;
                        row_charlen += oldcharlen;
                    } else {
                        bindIndicators[pitchOffset] = (short) charlen;
                        row_charlen += charlen;
                        preparedCharBinds = true;
                    }
                    metadata_offset += 10;
                }

            }
            if (preparedCharBinds) {
                initializeBindSubRanges(numberOfBoundRows, number_of_rows_to_be_set_up);
            }
            if (preparedAllBinds) {
                int total_bytelen = bindByteSubRange + row_bytelen * number_of_rows_to_be_set_up;
                if (lastBoundNeeded || total_bytelen > totalBindByteLength) {
                    bindByteOffset = 0;
                    bindBytes = new byte[total_bytelen];
                    totalBindByteLength = total_bytelen;
                }
                bindBufferCapacity = number_of_rows_to_be_set_up;
                bindIndicators[bindIndicatorSubRange + 1] = (short) bindBufferCapacity;
            }
            if (preparedCharBinds) {
                int total_charlen = bindCharSubRange + row_charlen * bindBufferCapacity;
                if (lastBoundNeeded || total_charlen > totalBindCharLength) {
                    bindCharOffset = 0;
                    bindChars = new char[total_charlen];
                    totalBindCharLength = total_charlen;
                }
                bindByteSubRange += bindByteOffset;
                bindCharSubRange += bindCharOffset;
            }
            int byteoffset = bindByteSubRange;
            int charoffset = bindCharSubRange;
            int indoffset = indicatorsOffset;
            int lenoffset = valueLengthsOffset;
            metadata_offset = per_position_metadata_offset;
            if (preparedCharBinds) {
                if (currentBatchBindAccessors == null) {
                    for (int i = 0; i < numberOfBindPositions; i++) {
                        int bytelen = bindIndicators[metadata_offset + 1];
                        int charlen = currentBatchCharLens[i];
                        int valueoffset = charlen != 0 ? charoffset : byteoffset;
                        bindIndicators[metadata_offset + 3] = (short) (valueoffset >> 16);
                        bindIndicators[metadata_offset + 4] = (short) (valueoffset & 0xffff);
                        byteoffset += bytelen * bindBufferCapacity;
                        charoffset += charlen * bindBufferCapacity;
                        metadata_offset += 10;
                    }

                } else {
                    for (int i = 0; i < numberOfBindPositions; i++) {
                        int bytelen = bindIndicators[metadata_offset + 1];
                        int charlen = currentBatchCharLens[i];
                        int valueoffset = charlen != 0 ? charoffset : byteoffset;
                        bindIndicators[metadata_offset + 3] = (short) (valueoffset >> 16);
                        bindIndicators[metadata_offset + 4] = (short) (valueoffset & 0xffff);
                        Accessor accessor = currentBatchBindAccessors[i];
                        if (accessor != null) {
                            if (charlen > 0) {
                                accessor.columnIndex = charoffset;
                                accessor.charLength = charlen;
                            } else {
                                accessor.columnIndex = byteoffset;
                                accessor.byteLength = bytelen;
                            }
                            accessor.lengthIndex = lenoffset;
                            accessor.indicatorIndex = indoffset;
                            accessor.rowSpaceByte = bindBytes;
                            accessor.rowSpaceChar = bindChars;
                            accessor.rowSpaceIndicator = bindIndicators;
                            if (accessor.defineType == 109 || accessor.defineType == 111) {
                                accessor.setOffsets(bindBufferCapacity);
                            }
                        }
                        byteoffset += bytelen * bindBufferCapacity;
                        charoffset += charlen * bindBufferCapacity;
                        indoffset += numberOfBindRowsAllocated;
                        lenoffset += numberOfBindRowsAllocated;
                        metadata_offset += 10;
                    }

                }
                byteoffset = bindByteSubRange;
                charoffset = bindCharSubRange;
                indoffset = indicatorsOffset;
                lenoffset = valueLengthsOffset;
                metadata_offset = per_position_metadata_offset;
            }
            int unused_rows = bindBufferCapacity - numberOfBoundRows;
            int lastrow_in_buffers = numberOfBoundRows - 1;
            int lastrow_in_binders = lastrow_in_buffers + first_row_to_be_bound;
            Binder lastRowBinders[] = binders[lastrow_in_binders];
            if (parameterOtype != null) {
                System.arraycopy(parameterDatum[lastrow_in_binders], 0, lastBoundTypeBytes, 0,
                                 numberOfBindPositions);
                System.arraycopy(parameterOtype[lastrow_in_binders], 0, lastBoundTypeOtypes, 0,
                                 numberOfBindPositions);
            }
            if (hasIbtBind) {
                processPlsqlIndexTabBinds(first_row_to_be_bound);
            }
            if (returnParamAccessors != null) {
                processDmlReturningBind();
            }
            boolean clearPriorBindValues = sqlKind != 1 && sqlKind != 4
                    || currentRowBindAccessors == null;
            for (int i = 0; i < numberOfBindPositions; i++) {
                int bytelen = bindIndicators[metadata_offset + 1];
                int charlen = currentBatchCharLens[i];
                lastBinders[i] = lastRowBinders[i];
                lastBoundByteLens[i] = bytelen;
                for (int rowInBuffers = 0; rowInBuffers < numberOfBoundRows; rowInBuffers++) {
                    int rowInBinders = first_row_to_be_bound + rowInBuffers;
                    binders[rowInBinders][i].bind(this, i, rowInBuffers, rowInBinders, bindBytes,
                                                  bindChars, bindIndicators, bytelen, charlen,
                                                  byteoffset, charoffset, lenoffset + rowInBuffers,
                                                  indoffset + rowInBuffers, clearPriorBindValues);
                    binders[rowInBinders][i] = null;
                    byteoffset += bytelen;
                    charoffset += charlen;
                }

                lastBoundByteOffsets[i] = byteoffset - bytelen;
                lastBoundCharOffsets[i] = charoffset - charlen;
                lastBoundInds[i] = bindIndicators[indoffset + lastrow_in_buffers];
                lastBoundLens[i] = bindIndicators[lenoffset + lastrow_in_buffers];
                lastBoundCharLens[i] = 0;
                byteoffset += unused_rows * bytelen;
                charoffset += unused_rows * charlen;
                indoffset += numberOfBindRowsAllocated;
                lenoffset += numberOfBindRowsAllocated;
                metadata_offset += 10;
            }

            lastBoundBytes = bindBytes;
            lastBoundByteOffset = bindByteOffset;
            lastBoundChars = bindChars;
            lastBoundCharOffset = bindCharOffset;
            int tmp[] = currentBatchCharLens;
            currentBatchCharLens = lastBoundCharLens;
            lastBoundCharLens = tmp;
            lastBoundNeeded = false;
            prepareBindPreambles(numberOfBoundRows, bindBufferCapacity);
        } catch (NullPointerException e) {
            DatabaseError.throwSqlException(89);
        }
        return;
    }

    public void enterImplicitCache() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "enterImplicitCache() entry", this);
            OracleLog.recursiveTrace = false;
        }
        alwaysOnClose();
        if (!connection.isClosed()) {
            cleanAllTempLobs();
        }
        if (connection.clearStatementMetaData) {
            lastBoundBytes = null;
            lastBoundChars = null;
        }
        clearParameters();
        cacheState = 2;
        creationState = 1;
        currentResultSet = null;
        lastIndex = 0;
        queryTimeout = 0;
        autoRollback = 2;
        rowPrefetchChanged = false;
        currentRank = 0;
        currentRow = -1;
        validRows = 0;
        maxRows = 0;
        totalRowsVisited = 0;
        maxFieldSize = 0;
        gotLastBatch = false;
        clearParameters = true;
        scrollRset = null;
        needToAddIdentifier = false;
        defaultFetchDirection = 1000;
        defaultTZ = null;
        if (sqlKind == 3) {
            needToParse = true;
            needToPrepareDefineBuffer = true;
            columnsDefinedByUser = false;
        }
        if (connection.isMemoryFreedOnEnteringCache && defineIndicators != null) {
            cachedDefineByteSize = defineBytes == null ? 0 : defineBytes.length;
            cachedDefineCharSize = defineChars == null ? 0 : defineChars.length;
            cachedDefineIndicatorSize = defineIndicators == null ? 0 : defineIndicators.length;
            defineChars = null;
            defineBytes = null;
            defineIndicators = null;
        }
        if (accessors != null) {
            int len = accessors.length;
            for (int i = 0; i < len; i++) {
                if (accessors[i] == null) {
                    continue;
                }
                if (connection.isMemoryFreedOnEnteringCache) {
                    accessors[i].rowSpaceByte = null;
                    accessors[i].rowSpaceChar = null;
                    accessors[i].rowSpaceIndicator = null;
                }
                if (columnsDefinedByUser) {
                    accessors[i].externalType = 0;
                }
            }

        }
        fixedString = connection.getDefaultFixedString();
        defaultRowPrefetch = rowPrefetch;
        if (connection.clearStatementMetaData) {
            sqlStringChanged = true;
            needToParse = true;
            needToPrepareDefineBuffer = true;
            columnsDefinedByUser = false;
            if (userRsetType == 0) {
                userRsetType = 1;
                realRsetType = 1;
            }
            currentRowNeedToPrepareBinds = true;
        }
    }

    public void enterExplicitCache() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "enterExplicitCache() entry", this);
            OracleLog.recursiveTrace = false;
        }
        cacheState = 2;
        creationState = 2;
        defaultTZ = null;
        alwaysOnClose();
    }

    public void exitImplicitCacheToActive() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "exitImplicitCacheToActive() entry", this);
            OracleLog.recursiveTrace = false;
        }
        cacheState = 1;
        closed = false;
        if (rowPrefetch != connection.getDefaultRowPrefetch() && streamList == null) {
            rowPrefetch = connection.getDefaultRowPrefetch();
            defaultRowPrefetch = rowPrefetch;
            rowPrefetchChanged = true;
        }
        if (batch != connection.getDefaultExecuteBatch()) {
            resetBatch();
        }
        if (autoRefetch != connection.getDefaultAutoRefetch()) {
            autoRefetch = connection.getDefaultAutoRefetch();
        }
        processEscapes = connection.processEscapes;
        if (connection.isMemoryFreedOnEnteringCache && cachedDefineIndicatorSize != 0) {
            defineBytes = new byte[cachedDefineByteSize];
            defineChars = new char[cachedDefineCharSize];
            defineIndicators = new short[cachedDefineIndicatorSize];
            if (accessors != null) {
                int len = accessors.length;
                for (int i = 0; i < len; i++) {
                    if (accessors[i] != null && connection.isMemoryFreedOnEnteringCache) {
                        accessors[i].rowSpaceByte = defineBytes;
                        accessors[i].rowSpaceChar = defineChars;
                        accessors[i].rowSpaceIndicator = defineIndicators;
                    }
                }

            }
        }
    }

    public void exitExplicitCacheToActive() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.exitImplicitCacheToActive()", this);
            OracleLog.recursiveTrace = false;
        }
        cacheState = 1;
        closed = false;
    }

    public void exitImplicitCacheToClose() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.exitImplicitCacheToClose()", this);
            OracleLog.recursiveTrace = false;
        }
        cacheState = 0;
        closed = false;
        synchronized (this) {
            hardClose();
        }
    }

    public void exitExplicitCacheToClose() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.exitExplicitCacheToClose()", this);
            OracleLog.recursiveTrace = false;
        }
        cacheState = 0;
        closed = false;
        synchronized (this) {
            hardClose();
        }
    }

    public void closeWithKey(String key) throws SQLException {
        synchronized (connection) {
            synchronized (this) {
                closeOrCache(key);
            }
        }
    }

    int executeInternal() throws SQLException {
        noMoreUpdateCounts = false;
        ensureOpen();
        if (currentRank > 0 && m_batchStyle == 2) {
            DatabaseError.throwSqlException(81, "batch must be either executed or cleared");
        }
        boolean regularResultSet = userRsetType == 1;
        prepareForNewResults(true, false);
        processCompletedBindRow(sqlKind != 0 ? batch : 1, false);
        if (!regularResultSet && !scrollRsetTypeSolved) {
            return doScrollPstmtExecuteUpdate() + prematureBatchCount;
        }
        doExecuteWithTimeout();
        boolean incrementRowCount = prematureBatchCount != 0 && validRows > 0;
        if (!regularResultSet) {
            currentResultSet = new OracleResultSetImpl(connection, this);
            scrollRset = ResultSetUtil.createScrollResultSet(this, currentResultSet, realRsetType);
            if (!connection.accumulateBatchResult) {
                incrementRowCount = false;
            }
        }
        if (incrementRowCount) {
            validRows += prematureBatchCount;
            prematureBatchCount = 0;
        }
        return validRows;
    }

    public ResultSet executeQuery() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                this.executionType = 1;

                executeInternal();

                if (this.userRsetType == 1) {
                    this.currentResultSet = new OracleResultSetImpl(this.connection, this);

                    return this.currentResultSet;
                }

                if (this.scrollRset == null) {
                    this.currentResultSet = new OracleResultSetImpl(this.connection, this);
                    this.scrollRset = this.currentResultSet;
                }

                return this.scrollRset;
            }
        }
    }

    public int executeUpdate() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                this.executionType = 2;

                return executeInternal();
            }
        }
    }

    public boolean execute() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                this.executionType = 3;

                executeInternal();

                return this.sqlKind == 0;
            }
        }
    }

    void slideDownCurrentRow(int fromRow) {
        if (binders != null) {
            binders[fromRow] = binders[0];
            binders[0] = currentRowBinders;
        }
        if (parameterInt != null) {
            int tmp[] = parameterInt[0];
            parameterInt[0] = parameterInt[fromRow];
            parameterInt[fromRow] = tmp;
        }
        if (parameterLong != null) {
            long tmp[] = parameterLong[0];
            parameterLong[0] = parameterLong[fromRow];
            parameterLong[fromRow] = tmp;
        }
        if (parameterFloat != null) {
            float tmp[] = parameterFloat[0];
            parameterFloat[0] = parameterFloat[fromRow];
            parameterFloat[fromRow] = tmp;
        }
        if (parameterDouble != null) {
            double tmp[] = parameterDouble[0];
            parameterDouble[0] = parameterDouble[fromRow];
            parameterDouble[fromRow] = tmp;
        }
        if (parameterBigDecimal != null) {
            BigDecimal tmp[] = parameterBigDecimal[0];
            parameterBigDecimal[0] = parameterBigDecimal[fromRow];
            parameterBigDecimal[fromRow] = tmp;
        }
        if (parameterString != null) {
            String tmp[] = parameterString[0];
            parameterString[0] = parameterString[fromRow];
            parameterString[fromRow] = tmp;
        }
        if (parameterDate != null) {
            Date tmp[] = parameterDate[0];
            parameterDate[0] = parameterDate[fromRow];
            parameterDate[fromRow] = tmp;
        }
        if (parameterTime != null) {
            Time tmp[] = parameterTime[0];
            parameterTime[0] = parameterTime[fromRow];
            parameterTime[fromRow] = tmp;
        }
        if (parameterTimestamp != null) {
            Timestamp tmp[] = parameterTimestamp[0];
            parameterTimestamp[0] = parameterTimestamp[fromRow];
            parameterTimestamp[fromRow] = tmp;
        }
        if (parameterDatum != null) {
            byte tmp[][] = parameterDatum[0];
            parameterDatum[0] = parameterDatum[fromRow];
            parameterDatum[fromRow] = tmp;
        }
        if (parameterOtype != null) {
            OracleTypeADT tmp[] = parameterOtype[0];
            parameterOtype[0] = parameterOtype[fromRow];
            parameterOtype[fromRow] = tmp;
        }
        if (parameterStream != null) {
            InputStream tmp[] = parameterStream[0];
            parameterStream[0] = parameterStream[fromRow];
            parameterStream[fromRow] = tmp;
        }
    }

    void resetBatch() {
        batch = connection.getDefaultExecuteBatch();
    }

    public int sendBatch() throws SQLException {
        if (isJdbcBatchStyle()) {
            return 0;
        }

        synchronized (this.connection) {
            synchronized (this) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.sendBatch()",
                                               this);

                    OracleLog.recursiveTrace = false;
                }

                try {
                    ensureOpen();

                    if (this.currentRank <= 0) {
                        int i = this.connection.accumulateBatchResult ? 0 : this.validRows;

                        this.currentRank = 0;
                        return i;
                    }
                    int actual_batch = this.batch;
                    try {
                        int saved_current_rank = this.currentRank;

                        if (this.batch != this.currentRank) {
                            this.batch = this.currentRank;
                        }
                        setupBindBuffers(0, this.currentRank);

                        this.currentRank -= 1;

                        doExecuteWithTimeout();

                        slideDownCurrentRow(saved_current_rank);

                        if (this.batch != actual_batch)
                            this.batch = actual_batch;
                    } finally {
                        if (this.batch != actual_batch) {
                            this.batch = actual_batch;
                        }

                    }

                    if (this.connection.accumulateBatchResult) {
                        this.validRows += this.prematureBatchCount;
                        this.prematureBatchCount = 0;
                    }

                    int saved_current_rank = this.validRows;

                    this.currentRank = 0;
                    return saved_current_rank;
                } finally {
                    this.currentRank = 0;
                }
            }
        }
    }

    public synchronized void setExecuteBatch(int batchValue) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setExecuteBatch(batchValue="
                                               + batchValue + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setOracleBatchStyle();
        set_execute_batch(batchValue);
    }

    synchronized void set_execute_batch(int batchValue) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setExecuteBatch(batchValue="
                                               + batchValue + "): batch=" + batch, this);
            OracleLog.recursiveTrace = false;
        }
        if (batchValue <= 0) {
            DatabaseError.throwSqlException(42);
        }
        if (batchValue == batch) {
            return;
        }
        if (currentRank > 0) {
            sendBatch();
        }
        int oldBatch = batch;
        batch = batchValue;
        if (numberOfBindRowsAllocated < batch) {
            growBinds(batch);
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setExecuteBatch:return", this);
            OracleLog.recursiveTrace = false;
        }
    }

    public final int getExecuteBatch() {
        return batch;
    }

    public synchronized void defineParameterTypeBytes(int param_index, int type, int max_size)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.defineParameterType(param_index="
                                               + param_index + ", type=" + type + ", max_size="
                                               + max_size + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (max_size < 0) {
            DatabaseError.throwSqlException(53);
        }
        if (param_index < 1) {
            DatabaseError.throwSqlException(3);
        }
        switch (type) {
        case -7:
        case -6:
        case -5:
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
            type = 6;
            break;

        case 1: // '\001'
            type = 96;
            break;

        case 12: // '\f'
            type = 1;
            break;

        case 91: // '['
        case 92: // '\\'
            type = 12;
            break;

        case -103:
            type = 182;
            break;

        case -104:
            type = 183;
            break;

        case -100:
        case 93: // ']'
            type = 180;
            break;

        case -101:
            type = 181;
            break;

        case -102:
            type = 231;
            break;

        case -3:
        case -2:
            type = 23;
            break;

        case 100: // 'd'
            type = 100;
            break;

        case 101: // 'e'
            type = 101;
            break;

        case -8:
            type = 104;
            break;

        case 2004:
            type = 113;
            break;

        case 2005:
            type = 112;
            break;

        case -13:
            type = 114;
            break;

        case -10:
            type = 102;
            break;

        case 0: // '\0'
            DatabaseError.throwSqlException(4);
            // fall through

        default:
            DatabaseError.throwSqlException(23);
            break;
        }
    }

    public synchronized void defineParameterTypeChars(int param_index, int type, int max_size)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.defineParameterTypeChars(param_index="
                                               + param_index + ", type=" + type + ", max_size="
                                               + max_size + ")", this);
            OracleLog.recursiveTrace = false;
        }
        int nlsratio = connection.getNlsRatio();
        if (type == 1 || type == 12) {
            defineParameterTypeBytes(param_index, type, max_size * nlsratio);
        } else {
            defineParameterTypeBytes(param_index, type, max_size);
        }
    }

    public synchronized void defineParameterType(int param_index, int type, int max_size)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleStatement.defineParameterType(param_index="
                                               + param_index + "type=" + type + "max_size="
                                               + max_size + ")", this);
            OracleLog.recursiveTrace = false;
        }
        defineParameterTypeBytes(param_index, type, max_size);
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        ResultSet _rset = getResultSet();
        if (_rset != null) {
            return _rset.getMetaData();
        } else {
            return null;
        }
    }

    public void setNull(int paramIndex, int sqlType, String sqlName) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OraclePreparedStatement.setNull(paramIndex="
                    + paramIndex + ", sqlType=" + sqlType + ", sqlName=" + sqlName + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setNullInternal(paramIndex, sqlType, sqlName);
    }

    void setNullInternal(int paramIndex, int sqlType, String sqlName) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (sqlType == 2002 || sqlType == 2008 || sqlType == 2003 || sqlType == 2007
                || sqlType == 2006) {
            synchronized (connection) {
                synchronized (this) {
                    setNullCritial(index, sqlType, sqlName);
                    currentRowCharLens[index] = 0;
                }
            }
        } else {
            setNullInternal(paramIndex, sqlType);
            return;
        }
    }

    synchronized void setNullInternal(int paramIndex, int sqlType) throws SQLException {
        setNullCritical(paramIndex, sqlType);
    }

    void setNullCritial(int index, int sqlType, String sqlName) throws SQLException {
        OracleTypeADT otype = null;
        Binder binder = theNamedTypeNullBinder;
        switch (sqlType) {
        case 2006: {
            binder = theRefTypeNullBinder;
            // fall through
        }

        case 2002:
        case 2008: {
            StructDescriptor desc = StructDescriptor.createDescriptor(sqlName, connection);
            otype = desc.getOracleTypeADT();
            break;
        }

        case 2003: {
            ArrayDescriptor desc = ArrayDescriptor.createDescriptor(sqlName, connection);
            otype = desc.getOracleTypeCOLLECTION();
            break;
        }

        case 2007: {
            OpaqueDescriptor desc = OpaqueDescriptor.createDescriptor(sqlName, connection);
            otype = (OracleTypeADT) desc.getPickler();
            break;
        }
        }
        currentRowBinders[index] = binder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        parameterDatum[currentRank][index] = null;
        otype.getTOID();
        if (parameterOtype == null) {
            parameterOtype = new OracleTypeADT[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterOtype[currentRank][index] = otype;
    }

    public void setNullAtName(String paramName, int sqlType, String sqlName) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setNull(" + paramName
                    + ", " + sqlType + ", " + sqlName, this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setNullInternal(i + 1, sqlType, sqlName);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setNull(int paramIndex, int sqlType) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OraclePreparedStatement.setNull(paramIndex="
                    + paramIndex + ", sqlType=" + sqlType + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setNullCritical(paramIndex, sqlType);
    }

    void setNullCritical(int paramIndex, int sqlType) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        Binder binder = null;
        int dbtype = getInternalType(sqlType);
        switch (dbtype) {
        case 6: // '\006'
            binder = theVarnumNullBinder;
            break;

        case 1: // '\001'
        case 8: // '\b'
        case 96: // '`'
        case 995:
            binder = theVarcharNullBinder;
            currentRowCharLens[index] = 1;
            break;

        case 999:
            binder = theFixedCHARNullBinder;
            break;

        case 12: // '\f'
            binder = theDateNullBinder;
            break;

        case 180:
            binder = connection.v8Compatible ? theDateNullBinder : theTimestampNullBinder;
            break;

        case 181:
            binder = theTSTZNullBinder;
            break;

        case 231:
            binder = theTSLTZNullBinder;
            break;

        case 104: // 'h'
            binder = theRowidNullBinder;
            break;

        case 183:
            binder = theIntervalDSNullBinder;
            break;

        case 182:
            binder = theIntervalYMNullBinder;
            break;

        case 23: // '\027'
        case 24: // '\030'
            binder = theRawNullBinder;
            break;

        case 100: // 'd'
            binder = theBinaryFloatNullBinder;
            break;

        case 101: // 'e'
            binder = theBinaryDoubleNullBinder;
            break;

        case 113: // 'q'
            binder = theBlobNullBinder;
            break;

        case 112: // 'p'
            binder = theClobNullBinder;
            break;

        case 114: // 'r'
            binder = theBfileNullBinder;
            break;

        case 109: // 'm'
        case 111: // 'o'
            DatabaseError.throwSqlException(4, "sqlType=" + sqlType);
            // fall through

        case 102: // 'f'
        case 998:
        default:
            DatabaseError.throwSqlException(23, "sqlType=" + sqlType);
            break;
        }
        currentRowBinders[index] = binder;
    }

    public void setNullAtName(String paramName, int sqlType) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setNull(" + paramName
                    + ", " + sqlType + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setNull(i + 1, sqlType);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBoolean(int paramIndex, boolean x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setBoolean(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setBooleanInternal(paramIndex, x);
    }

    void setBooleanInternal(int paramIndex, boolean x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theBooleanBinder;
        if (parameterInt == null) {
            parameterInt = new int[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterInt[currentRank][index] = x ? 1 : 0;
    }

    public void setBooleanAtName(String paramName, boolean x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBooleanAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBoolean(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setByte(int paramIndex, byte x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setShort(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setByteInternal(paramIndex, x);
    }

    void setByteInternal(int paramIndex, byte x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theByteBinder;
        if (parameterInt == null) {
            parameterInt = new int[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterInt[currentRank][index] = x;
    }

    public void setByteAtName(String paramName, byte x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setByteAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setByte(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setShort(int paramIndex, short x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setShort(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setShortInternal(paramIndex, x);
    }

    void setShortInternal(int paramIndex, short x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theShortBinder;
        if (parameterInt == null) {
            parameterInt = new int[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterInt[currentRank][index] = x;
    }

    public void setShortAtName(String paramName, short x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setShortAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setShort(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setInt(int paramIndex, int x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setInt(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setIntInternal(paramIndex, x);
    }

    void setIntInternal(int paramIndex, int x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theIntBinder;
        if (parameterInt == null) {
            parameterInt = new int[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterInt[currentRank][index] = x;
    }

    public void setIntAtName(String paramName, int x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setIntAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setInt(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setLong(int paramIndex, long x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setLong(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setLongInternal(paramIndex, x);
    }

    void setLongInternal(int paramIndex, long x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theLongBinder;
        if (parameterLong == null) {
            parameterLong = new long[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterLong[currentRank][index] = x;
    }

    public void setLongAtName(String paramName, long x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setLongAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setLong(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setFloat(int paramIndex, float x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setFloat(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setFloatInternal(paramIndex, x);
    }

    void setFloatInternal(int paramIndex, float x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (theFloatBinder == null) {
            theFloatBinder = theStaticFloatBinder;
            Properties props = connection.connectionProperties;
            if (props != null) {
                String prop_str = props.getProperty("SetFloatAndDoubleUseBinary");
                if (prop_str != null && prop_str.equalsIgnoreCase("true")) {
                    theFloatBinder = theStaticBinaryFloatBinder;
                }
            }
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theFloatBinder;
        if (theFloatBinder == theStaticFloatBinder) {
            if (parameterDouble == null) {
                parameterDouble = new double[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            parameterDouble[currentRank][index] = x;
        } else {
            if (parameterFloat == null) {
                parameterFloat = new float[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            parameterFloat[currentRank][index] = x;
        }
    }

    public synchronized void setBinaryFloat(int paramIndex, float x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryFloat(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setBinaryFloatInternal(paramIndex, x);
    }

    void setBinaryFloatInternal(int paramIndex, float x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theBinaryFloatBinder;
        if (parameterFloat == null) {
            parameterFloat = new float[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterFloat[currentRank][index] = x;
    }

    public void setBinaryFloatAtName(String paramName, float x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryFloatAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBinaryFloat(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBinaryFloat(int paramIndex, BINARY_FLOAT bf) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryFloat(paramIndex="
                                               + paramIndex + ", bf=" + bf + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setBinaryFloatInternal(paramIndex, bf);
    }

    void setBinaryFloatInternal(int paramIndex, BINARY_FLOAT bf) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (bf == null) {
            currentRowBinders[index] = theBINARY_FLOATNullBinder;
        } else {
            currentRowBinders[index] = theBINARY_FLOATBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = bf.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setBinaryFloatAtName(String paramName, BINARY_FLOAT x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryFloatAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBinaryFloat(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBinaryDouble(int paramIndex, double x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryDouble(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setBinaryDoubleInternal(paramIndex, x);
    }

    void setBinaryDoubleInternal(int paramIndex, double x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowBinders[index] = theBinaryDoubleBinder;
        if (parameterDouble == null) {
            parameterDouble = new double[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        currentRowCharLens[index] = 0;
        parameterDouble[currentRank][index] = x;
    }

    public synchronized void setBinaryDouble(int paramIndex, BINARY_DOUBLE bd) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryDouble(paramIndex="
                                               + paramIndex + ", bd=" + bd + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setBinaryDoubleInternal(paramIndex, bd);
    }

    void setBinaryDoubleInternal(int paramIndex, BINARY_DOUBLE bd) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (bd == null) {
            currentRowBinders[index] = theBINARY_DOUBLENullBinder;
        } else {
            currentRowBinders[index] = theBINARY_DOUBLEBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = bd.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setBinaryDoubleAtName(String paramName, double x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryDoubleAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBinaryDouble(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setFloatAtName(String paramName, float x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setFloatAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setFloat(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setBinaryDoubleAtName(String paramName, BINARY_DOUBLE x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryDoubleAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBinaryDouble(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setDouble(int paramIndex, double x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setDouble(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setDoubleInternal(paramIndex, x);
    }

    void setDoubleInternal(int paramIndex, double x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (theDoubleBinder == null) {
            theDoubleBinder = theStaticDoubleBinder;
            Properties props = connection.connectionProperties;
            if (props != null) {
                String prop_str = props.getProperty("SetFloatAndDoubleUseBinary");
                if (prop_str != null && prop_str.equalsIgnoreCase("true")) {
                    theDoubleBinder = theStaticBinaryDoubleBinder;
                }
            }
        }
        currentRowCharLens[index] = 0;
        currentRowBinders[index] = theDoubleBinder;
        if (parameterDouble == null) {
            parameterDouble = new double[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterDouble[currentRank][index] = x;
    }

    public void setDoubleAtName(String paramName, double x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setDoubleAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setDouble(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBigDecimal(int paramIndex, BigDecimal x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBigDecimal(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setBigDecimalInternal(paramIndex, x);
    }

    void setBigDecimalInternal(int paramIndex, BigDecimal x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theVarnumNullBinder;
        } else {
            currentRowBinders[index] = theBigDecimalBinder;
            if (parameterBigDecimal == null) {
                parameterBigDecimal = new BigDecimal[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            parameterBigDecimal[currentRank][index] = x;
        }
        currentRowCharLens[index] = 0;
    }

    public void setBigDecimalAtName(String paramName, BigDecimal x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBigDecimalAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBigDecimal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setString(int paramIndex, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setString(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setStringInternal(paramIndex, x);
    }

    void setStringInternal(int paramIndex, String x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int stringLength = x == null ? 0 : x.length();
        if (stringLength == 0) {
            basicBindNullString(paramIndex);
        } else if (currentRowFormOfUse[paramIndex - 1] == 1) {
            if (sqlKind == 1 || sqlKind == 4) {
                if (stringLength > maxVcsBytesPlsql || stringLength > maxVcsCharsPlsql
                        && isServerCharSetFixedWidth) {
                    setStringForClobCritical(paramIndex, x);
                } else if (stringLength > maxVcsCharsPlsql) {
                    int bytes_length = connection.conversion.encodedByteLength(x, false);
                    if (bytes_length > maxVcsBytesPlsql) {
                        setStringForClobCritical(paramIndex, x);
                    } else {
                        basicBindString(paramIndex, x);
                    }
                } else {
                    basicBindString(paramIndex, x);
                }
            } else if (stringLength <= maxVcsCharsSql) {
                basicBindString(paramIndex, x);
            } else if (stringLength <= maxStreamCharsSql) {
                basicBindCharacterStream(paramIndex, new StringReader(x), stringLength);
            } else {
                setStringForClobCritical(paramIndex, x);
            }
        } else if (sqlKind == 1 || sqlKind == 4) {
            if (stringLength > maxVcsBytesPlsql || stringLength > maxVcsNCharsPlsql
                    && isServerNCharSetFixedWidth) {
                setStringForClobCritical(paramIndex, x);
            } else if (stringLength > maxVcsNCharsPlsql) {
                int bytes_length = connection.conversion.encodedByteLength(x, true);
                if (bytes_length > maxVcsBytesPlsql) {
                    setStringForClobCritical(paramIndex, x);
                } else {
                    basicBindString(paramIndex, x);
                }
            } else {
                basicBindString(paramIndex, x);
            }
        } else if (stringLength <= maxVcsCharsSql) {
            basicBindString(paramIndex, x);
        } else if (stringLength <= maxStreamNCharsSql) {
            setStringForClobCritical(paramIndex, x);
        } else {
            setStringForClobCritical(paramIndex, x);
        }
    }

    void basicBindNullString(int paramIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OraclePreparedStatement.basicBindNullString=",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        int index = paramIndex - 1;
        currentRowBinders[index] = theVarcharNullBinder;
        if (sqlKind == 1 || sqlKind == 4) {
            currentRowCharLens[index] = minVcsBindSize;
        } else {
            currentRowCharLens[index] = 1;
        }
    }

    void basicBindString(int paramIndex, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OraclePreparedStatement.basicBindString=",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        int index = paramIndex - 1;
        currentRowBinders[index] = theStringBinder;
        int stringLength = x.length();
        if (sqlKind == 1 || sqlKind == 4) {
            int min_size = connection.minVcsBindSize;
            int cur_len = stringLength + 1;
            currentRowCharLens[index] = cur_len >= min_size ? cur_len : min_size;
        } else {
            currentRowCharLens[index] = stringLength + 1;
        }
        if (parameterString == null) {
            parameterString = new String[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterString[currentRank][index] = x;
    }

    public void setStringAtName(String paramName, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setStringAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setString(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setStringForClob(int paramIndex, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setStringForClob(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (x == null) {
            setNull(paramIndex, 1);
            return;
        }
        int stringLength = x.length();
        if (stringLength == 0) {
            setNull(paramIndex, 1);
            return;
        }
        if (sqlKind == 1 || sqlKind == 4) {
            if (stringLength <= maxVcsCharsPlsql) {
                setStringInternal(paramIndex, x);
            } else {
                setStringForClobCritical(paramIndex, x);
            }
        } else if (stringLength <= maxVcsCharsSql) {
            setStringInternal(paramIndex, x);
        } else {
            setStringForClobCritical(paramIndex, x);
        }
    }

    void setStringForClobCritical(int paramIndex, String x) throws SQLException {
        synchronized (connection) {
            synchronized (this) {
                CLOB tclob = CLOB.createTemporary(connection, true, 10,
                                                  currentRowFormOfUse[paramIndex - 1]);
                tclob.setString(1L, x);
                addToTempLobsToFree(tclob);
                setCLOBInternal(paramIndex, tclob);
            }
        }
    }

    void setReaderContentsForClobCritical(int paramIndex, Reader reader, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setReaderContentsForClobCritical(paramIndex="
                                               + paramIndex + ", reader, length=" + length + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        synchronized (connection) {
            synchronized (this) {
                CLOB tclob = CLOB.createTemporary(connection, true, 10,
                                                  currentRowFormOfUse[paramIndex - 1]);
                OracleClobWriter writer = (OracleClobWriter) tclob.setCharacterStream(1L);
                int bufferSize = tclob.getBufferSize();
                char charbuf[] = new char[bufferSize];
                int lengthRemaining = length;
                try {
                    while (lengthRemaining >= bufferSize) {
                        reader.read(charbuf);
                        writer.write(charbuf);
                        lengthRemaining -= bufferSize;
                    }
                    if (lengthRemaining > 0) {
                        reader.read(charbuf, 0, lengthRemaining);
                        writer.write(charbuf, 0, lengthRemaining);
                    }
                    writer.flush();
                } catch (IOException x) {
                }
                addToTempLobsToFree(tclob);
                setCLOBInternal(paramIndex, tclob);
            }
        }
    }

    void setAsciiStreamContentsForClobCritical(int paramIndex, InputStream inputStream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setAsciiStreamContentsForClobCritical(paramIndex="
                                               + paramIndex + ", inputStream, length=" + length
                                               + ")", this);
            OracleLog.recursiveTrace = false;
        }
        synchronized (connection) {
            synchronized (this) {
                CLOB tclob = CLOB.createTemporary(connection, true, 10,
                                                  currentRowFormOfUse[paramIndex - 1]);
                OracleClobWriter writer = (OracleClobWriter) tclob.setCharacterStream(1L);
                int bufferSize = tclob.getBufferSize();
                byte bytebuf[] = new byte[bufferSize];
                char charbuf[] = new char[bufferSize];
                int lengthRemaining = length;
                try {
                    while (lengthRemaining >= bufferSize) {
                        inputStream.read(bytebuf);
                        DBConversion _tmp = connection.conversion;
                        DBConversion.asciiBytesToJavaChars(bytebuf, bufferSize, charbuf);
                        writer.write(charbuf);
                        lengthRemaining -= bufferSize;
                    }
                    if (lengthRemaining > 0) {
                        inputStream.read(bytebuf, 0, lengthRemaining);
                        DBConversion _tmp1 = connection.conversion;
                        DBConversion.asciiBytesToJavaChars(bytebuf, lengthRemaining, charbuf);
                        writer.write(charbuf, 0, lengthRemaining);
                    }
                    writer.flush();
                } catch (IOException x) {
                }
                addToTempLobsToFree(tclob);
                setCLOBInternal(paramIndex, tclob);
            }
        }
    }

    public void setStringForClobAtName(String paramName, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setStringForClobAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setStringForClob(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setFixedCHAR(int paramIndex, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setFixedCHAR(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setFixedCHARInternal(paramIndex, x);
    }

    void setFixedCHARInternal(int paramIndex, String x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int stringLength = 0;
        if (x != null) {
            stringLength = x.length();
        }
        if (stringLength > 32766) {
            DatabaseError.throwSqlException(157);
        }
        if (x == null) {
            currentRowBinders[index] = theFixedCHARNullBinder;
            currentRowCharLens[index] = 1;
        } else {
            currentRowBinders[index] = theFixedCHARBinder;
            currentRowCharLens[index] = stringLength + 1;
            if (parameterString == null) {
                parameterString = new String[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            parameterString[currentRank][index] = x;
        }
    }

    public void setFixedCHARAtName(String paramName, String x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setFixedCHARAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setFixedCHAR(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    /**
     * @deprecated Method setCursor is deprecated
     */

    public synchronized void setCursor(int paramIndex, ResultSet rs) throws SQLException {
        setCursorInternal(paramIndex, rs);
    }

    void setCursorInternal(int paramIndex, ResultSet rs) throws SQLException {
        throw DatabaseError.newSqlException(23);
    }

    public void setCursorAtName(String paramName, ResultSet x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setCursorAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setCursor(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setROWID(int paramIndex, ROWID rowid) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setROWID(paramIndex="
                    + paramIndex + ", rowid=" + rowid + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setROWIDInternal(paramIndex, rowid);
    }

    void setROWIDInternal(int paramIndex, ROWID rowid) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (rowid == null) {
            currentRowBinders[index] = theRowidNullBinder;
        } else {
            currentRowBinders[index] = theRowidBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = rowid.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setROWIDAtName(String paramName, ROWID x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setROWIDAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setROWID(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setArray(int paramIndex, Array arr) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setArray(paramIndex="
                    + paramIndex + ", array)", this);
            OracleLog.recursiveTrace = false;
        }
        setARRAYInternal(paramIndex, (ARRAY) arr);
    }

    void setArrayInternal(int paramIndex, Array arr) throws SQLException {
        setARRAYInternal(paramIndex, (ARRAY) arr);
    }

    public void setArrayAtName(String paramName, Array x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setArrayAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setArrayInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setARRAY(int paramIndex, ARRAY arr) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setARRAY(paramIndex="
                    + paramIndex + ", array)", this);
            OracleLog.recursiveTrace = false;
        }
        setARRAYInternal(paramIndex, arr);
    }

    void setARRAYInternal(int paramIndex, ARRAY arr) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (arr == null) {
            DatabaseError.throwSqlException(68);
        } else {
            synchronized (connection) {
                synchronized (this) {
                    setArrayCritical(index, arr);
                    currentRowCharLens[index] = 0;
                }
            }
        }
    }

    void setArrayCritical(int index, ARRAY arr) throws SQLException {
        ArrayDescriptor desc = arr.getDescriptor();
        if (desc == null) {
            DatabaseError.throwSqlException(61);
        }
        currentRowBinders[index] = theNamedTypeBinder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        parameterDatum[currentRank][index] = arr.toBytes();
        OracleTypeADT otype = desc.getOracleTypeCOLLECTION();
        otype.getTOID();
        if (parameterOtype == null) {
            parameterOtype = new OracleTypeADT[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterOtype[currentRank][index] = otype;
    }

    public void setARRAYAtName(String paramName, ARRAY x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setARRAYAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setARRAYInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setOPAQUE(int paramIndex, OPAQUE opaque) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setOPAQUE(paramIndex="
                    + paramIndex + ", struct)", this);
            OracleLog.recursiveTrace = false;
        }
        setOPAQUEInternal(paramIndex, opaque);
    }

    void setOPAQUEInternal(int paramIndex, OPAQUE opaque) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (opaque == null) {
            DatabaseError.throwSqlException(68);
        } else {
            synchronized (connection) {
                synchronized (this) {
                    setOPAQUECritical(index, opaque);
                    currentRowCharLens[index] = 0;
                }
            }
        }
    }

    void setOPAQUECritical(int index, OPAQUE opaque) throws SQLException {
        OpaqueDescriptor desc = opaque.getDescriptor();
        if (desc == null) {
            DatabaseError.throwSqlException(61);
        }
        currentRowBinders[index] = theNamedTypeBinder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        parameterDatum[currentRank][index] = opaque.toBytes();
        OracleTypeADT otype = (OracleTypeADT) desc.getPickler();
        otype.getTOID();
        if (parameterOtype == null) {
            parameterOtype = new OracleTypeADT[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterOtype[currentRank][index] = otype;
    }

    public void setOPAQUEAtName(String paramName, OPAQUE x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setOPAQUEAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setOPAQUEInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setStructDescriptor(int paramIndex, StructDescriptor desc) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setStructDescriptor(paramIndex="
                                               + paramIndex + ", desc)", this);
            OracleLog.recursiveTrace = false;
        }
        setStructDescriptorInternal(paramIndex, desc);
    }

    void setStructDescriptorInternal(int paramIndex, StructDescriptor desc) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (desc == null) {
            DatabaseError.throwSqlException(68);
        }
        synchronized (connection) {
            synchronized (this) {
                setStructDescriptorCritical(index, desc);
                currentRowCharLens[index] = 0;
            }
        }
    }

    void setStructDescriptorCritical(int index, StructDescriptor desc) throws SQLException {
        currentRowBinders[index] = theNamedTypeBinder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        OracleTypeADT otype = desc.getOracleTypeADT();
        otype.getTOID();
        if (parameterOtype == null) {
            parameterOtype = new OracleTypeADT[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterOtype[currentRank][index] = otype;
    }

    public void setStructDescriptorAtName(String paramName, StructDescriptor desc)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setStructDescriptor("
                    + paramName + ", " + desc + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setStructDescriptorInternal(i + 1, desc);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    synchronized void setPreBindsCompelete() throws SQLException {
    }

    public void setSTRUCT(int paramIndex, STRUCT struct) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setSTRUCT(paramIndex="
                    + paramIndex + ", struct)", this);
            OracleLog.recursiveTrace = false;
        }
        setSTRUCTInternal(paramIndex, struct);
    }

    void setSTRUCTInternal(int paramIndex, STRUCT struct) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (struct == null) {
            DatabaseError.throwSqlException(68);
        } else {
            synchronized (connection) {
                synchronized (this) {
                    setSTRUCTCritical(index, struct);
                    currentRowCharLens[index] = 0;
                }
            }
        }
    }

    void setSTRUCTCritical(int index, STRUCT struct) throws SQLException {
        StructDescriptor desc = struct.getDescriptor();
        if (desc == null) {
            DatabaseError.throwSqlException(61);
        }
        currentRowBinders[index] = theNamedTypeBinder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        parameterDatum[currentRank][index] = struct.toBytes();
        OracleTypeADT otype = desc.getOracleTypeADT();
        otype.getTOID();
        if (parameterOtype == null) {
            parameterOtype = new OracleTypeADT[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterOtype[currentRank][index] = otype;
    }

    public void setSTRUCTAtName(String paramName, STRUCT x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setSTRUCTAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setSTRUCTInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setRAW(int paramIndex, RAW raw) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setRAW(paramIndex="
                    + paramIndex + ", raw)", this);
            OracleLog.recursiveTrace = false;
        }
        setRAWInternal(paramIndex, raw);
    }

    void setRAWInternal(int paramIndex, RAW raw) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        if (raw == null) {
            currentRowBinders[index] = theRawNullBinder;
        } else {
            setBytesInternal(paramIndex, raw.getBytes());
        }
    }

    public void setRAWAtName(String paramName, RAW x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setRAWAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setRAW(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setCHAR(int paramIndex, CHAR ch) throws SQLException {
        setCHARInternal(paramIndex, ch);
    }

    void setCHARInternal(int paramIndex, CHAR ch) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (ch == null || ch.shareBytes().length == 0) {
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.INFO,
                                           "OraclePreparedStatement.setCHARInternal(paramIdx="
                                                   + paramIndex + ", CHAR=null)", this);
                OracleLog.recursiveTrace = false;
            }
            currentRowBinders[index] = theSetCHARNullBinder;
            currentRowCharLens[index] = 1;
        } else {
            short char_cs_id = (short) ch.oracleId();
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.INFO,
                                           "OraclePreparedStatement.setCHARInternal(paramIdx="
                                                   + paramIndex + ", CHAR=(charsetid=" + char_cs_id
                                                   + ")", this);
                OracleLog.recursiveTrace = false;
            }
            currentRowBinders[index] = theSetCHARBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            CharacterSet targetCS = currentRowFormOfUse[index] != 2 ? connection.setCHARCharSetObj
                    : connection.setCHARNCharSetObj;
            byte b[];
            if (targetCS != null && targetCS.getOracleId() != char_cs_id) {
                byte cb[] = ch.shareBytes();
                b = targetCS.convert(ch.getCharacterSet(), cb, 0, cb.length);
            } else {
                b = ch.getBytes();
            }
            parameterDatum[currentRank][index] = b;
            currentRowCharLens[index] = (b.length + 1 >> 1) + 1;
        }
        if ((sqlKind == 1 || sqlKind == 4) && currentRowCharLens[index] < minVcsBindSize) {
            currentRowCharLens[index] = minVcsBindSize;
        }
    }

    public void setCHARAtName(String paramName, CHAR x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setCHARAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setCHAR(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setDATE(int paramIndex, DATE date) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setDATE(paramIndex="
                    + paramIndex + ", date=" + date + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setDATEInternal(paramIndex, date);
    }

    void setDATEInternal(int paramIndex, DATE date) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        if (date == null) {
            currentRowBinders[index] = theDateNullBinder;
        } else {
            currentRowBinders[index] = theOracleDateBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = date.getBytes();
        }
    }

    public void setDATEAtName(String paramName, DATE x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setDATEAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setDATE(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setNUMBER(int paramIndex, NUMBER num) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setNUMBER(paramIndex="
                    + paramIndex + ", num)", this);
            OracleLog.recursiveTrace = false;
        }
        setNUMBERInternal(paramIndex, num);
    }

    void setNUMBERInternal(int paramIndex, NUMBER num) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        if (num == null) {
            currentRowBinders[index] = theVarnumNullBinder;
        } else {
            currentRowBinders[index] = theOracleNumberBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = num.getBytes();
        }
    }

    public void setNUMBERAtName(String paramName, NUMBER x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setNUMBERAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setNUMBER(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBLOB(int paramIndex, BLOB lob) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setBLOB(paramIndex="
                    + paramIndex + ", lob)", this);
            OracleLog.recursiveTrace = false;
        }
        setBLOBInternal(paramIndex, lob);
    }

    void setBLOBInternal(int paramIndex, BLOB lob) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        if (lob == null) {
            currentRowBinders[index] = theBlobNullBinder;
        } else {
            currentRowBinders[index] = theBlobBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = lob.getBytes();
        }
    }

    public void setBLOBAtName(String paramName, BLOB x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBLOBAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBLOB(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBlob(int paramIndex, Blob lob) throws SQLException {
        setBLOBInternal(paramIndex, (BLOB) lob);
    }

    void setBlobInternal(int paramIndex, Blob lob) throws SQLException {
        setBLOBInternal(paramIndex, (BLOB) lob);
    }

    public void setBlobAtName(String paramName, Blob x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBlobAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBlob(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setCLOB(int paramIndex, CLOB lob) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setCLOB(" + paramIndex
                    + ", " + lob + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setCLOBInternal(paramIndex, lob);
    }

    void setCLOBInternal(int paramIndex, CLOB lob) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        if (lob == null) {
            currentRowBinders[index] = theClobNullBinder;
        } else {
            currentRowBinders[index] = theClobBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = lob.getBytes();
        }
    }

    public void setCLOBAtName(String paramName, CLOB x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setCLOBAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setCLOB(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setClob(int paramIndex, Clob lob) throws SQLException {
        setCLOBInternal(paramIndex, (CLOB) lob);
    }

    void setClobInternal(int paramIndex, Clob lob) throws SQLException {
        setCLOBInternal(paramIndex, (CLOB) lob);
    }

    public void setClobAtName(String paramName, Clob x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setClobAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setClob(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBFILE(int paramIndex, BFILE file) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setBFILE(paramIndex="
                    + paramIndex + ", file)", this);
            OracleLog.recursiveTrace = false;
        }
        setBFILEInternal(paramIndex, file);
    }

    void setBFILEInternal(int paramIndex, BFILE file) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        currentRowCharLens[index] = 0;
        if (file == null) {
            currentRowBinders[index] = theBfileNullBinder;
        } else {
            currentRowBinders[index] = theBfileBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = file.getBytes();
        }
    }

    public void setBFILEAtName(String paramName, BFILE x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBFILEAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBFILE(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setBfile(int paramIndex, BFILE file) throws SQLException {
        setBFILEInternal(paramIndex, file);
    }

    void setBfileInternal(int paramIndex, BFILE file) throws SQLException {
        setBFILEInternal(paramIndex, file);
    }

    public void setBfileAtName(String paramName, BFILE x) throws SQLException {
        setBFILEAtName(paramName, x);
    }

    public synchronized void setBytes(int paramIndex, byte x[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setBytes(paramIndex="
                    + paramIndex + ", bytes)", this);
            OracleLog.recursiveTrace = false;
        }
        setBytesInternal(paramIndex, x);
    }

    void setBytesInternal(int paramIndex, byte x[]) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int byteLength = x == null ? 0 : x.length;
        if (byteLength == 0) {
            setNullInternal(paramIndex, -2);
        } else if (sqlKind == 1) {
            if (byteLength > maxRawBytesPlsql) {
                setBytesForBlobCritical(paramIndex, x);
            } else {
                basicBindBytes(paramIndex, x);
            }
        } else if (sqlKind == 4) {
            if (byteLength > maxRawBytesPlsql) {
                setBytesForBlobCritical(paramIndex, x);
            } else {
                basicBindBytes(paramIndex, x);
            }
        } else if (byteLength > maxRawBytesSql) {
            bindBytesAsStream(paramIndex, x);
        } else {
            basicBindBytes(paramIndex, x);
        }
    }

    void bindBytesAsStream(int paramIndex, byte x[]) throws SQLException {
        int byteLength = x.length;
        byte bytebuf[] = new byte[byteLength];
        System.arraycopy(x, 0, bytebuf, 0, byteLength);
        basicBindBinaryStream(paramIndex, new ByteArrayInputStream(bytebuf), byteLength);
    }

    void basicBindBytes(int paramIndex, byte x[]) throws SQLException {
        int index = paramIndex - 1;
        Binder binder = sqlKind != 1 && sqlKind != 4 ? theRawBinder : thePlsqlRawBinder;
        currentRowBinders[index] = binder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        parameterDatum[currentRank][index] = x;
        currentRowCharLens[index] = 0;
    }

    void basicBindBinaryStream(int paramIndex, InputStream istream, int length) throws SQLException {
        int index = paramIndex - 1;
        currentRowBinders[index] = theLongRawStreamBinder;
        if (parameterStream == null) {
            parameterStream = new InputStream[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        PhysicalConnection _tmp = connection;
        parameterStream[currentRank][index] = connection.conversion.ConvertStream(istream, 6,
                                                                                  length);
        currentRowCharLens[index] = 0;
    }

    public void setBytesAtName(String paramName, byte x[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBytesAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBytes(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setBytesForBlob(int paramIndex, byte x[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBytesForBlob(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (x == null) {
            setNull(paramIndex, -2);
            return;
        }
        int arrayLength = x.length;
        if (arrayLength == 0) {
            setNull(paramIndex, -2);
            return;
        }
        if (sqlKind == 1 || sqlKind == 4) {
            if (arrayLength <= maxRawBytesPlsql) {
                setBytes(paramIndex, x);
            } else {
                setBytesForBlobCritical(paramIndex, x);
            }
        } else if (arrayLength <= maxRawBytesSql) {
            setBytes(paramIndex, x);
        } else {
            setBytesForBlobCritical(paramIndex, x);
        }
    }

    void setBytesForBlobCritical(int paramIndex, byte x[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setBytesForBlobCritical(paramIndex="
                                               + paramIndex + " , byte array )", this);
            OracleLog.recursiveTrace = false;
        }
        synchronized (connection) {
            synchronized (this) {
                BLOB tblob = BLOB.createTemporary(connection, true, 10);
                tblob.putBytes(1L, x);
                addToTempLobsToFree(tblob);
                setBLOBInternal(paramIndex, tblob);
            }
        }
    }

    void setBinaryStreamContentsForBlobCritical(int paramIndex, InputStream inputStream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setBinaryStreamContentsForBlobCritical(paramIndex="
                                               + paramIndex + ", inputStream, length=" + length
                                               + ")", this);
            OracleLog.recursiveTrace = false;
        }
        synchronized (connection) {
            synchronized (this) {
                BLOB tblob = BLOB.createTemporary(connection, true, 10);
                OracleBlobOutputStream outputStream = (OracleBlobOutputStream) tblob
                        .setBinaryStream(1L);
                int bufferSize = tblob.getBufferSize();
                byte bytebuf[] = new byte[bufferSize];
                int lengthRemaining = length;
                try {
                    while (lengthRemaining >= bufferSize) {
                        inputStream.read(bytebuf);
                        outputStream.write(bytebuf);
                        lengthRemaining -= bufferSize;
                    }
                    if (lengthRemaining > 0) {
                        inputStream.read(bytebuf, 0, lengthRemaining);
                        outputStream.write(bytebuf, 0, lengthRemaining);
                    }
                    outputStream.flush();
                } catch (IOException x) {
                }
                addToTempLobsToFree(tblob);
                setBLOBInternal(paramIndex, tblob);
            }
        }
    }

    public void setBytesForBlobAtName(String paramName, byte x[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBytesForBlobAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setBytesForBlob(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setInternalBytes(int paramIndex, byte x[], int dbtype)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setInternalBytes(paramIndex="
                                               + paramIndex + ", bytes, dbtype=" + dbtype, this);
            OracleLog.recursiveTrace = false;
        }
        setInternalBytesInternal(paramIndex, x, dbtype);
    }

    void setInternalBytesInternal(int paramIndex, byte x[], int dbtype) throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public synchronized void setDate(int paramIndex, Date x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setDate(paramIndex="
                    + paramIndex + ", date)", this);
            OracleLog.recursiveTrace = false;
        }
        setDateInternal(paramIndex, x);
    }

    void setDateInternal(int paramIndex, Date x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theDateNullBinder;
        } else {
            currentRowBinders[index] = theDateBinder;
            if (parameterDate == null) {
                parameterDate = new Date[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            parameterDate[currentRank][index] = x;
        }
        currentRowCharLens[index] = 0;
    }

    public void setDateAtName(String paramName, Date x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setDateAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setDate(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setTime(int paramIndex, Time x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setTime(paramIndex="
                    + paramIndex + ", time)", this);
            OracleLog.recursiveTrace = false;
        }
        setTimeInternal(paramIndex, x);
    }

    void setTimeInternal(int paramIndex, Time x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theDateNullBinder;
        } else {
            currentRowBinders[index] = theTimeBinder;
            if (parameterTime == null) {
                parameterTime = new Time[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            parameterTime[currentRank][index] = x;
        }
        currentRowCharLens[index] = 0;
    }

    public void setTimeAtName(String paramName, Time x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTimeAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setTime(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setTimestamp(int paramIndex, Timestamp x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTimestamp(paramIndex="
                                               + paramIndex + ", timestamp)", this);
            OracleLog.recursiveTrace = false;
        }
        setTimestampInternal(paramIndex, x);
    }

    void setTimestampInternal(int paramIndex, Timestamp x) throws SQLException {
        if (connection.v8Compatible) {
            if (x == null) {
                setDATEInternal(paramIndex, null);
            } else {
                DATE d = new DATE(x);
                setDATEInternal(paramIndex, d);
            }
        } else {
            int index = paramIndex - 1;
            if (index < 0 || paramIndex > numberOfBindPositions) {
                DatabaseError.throwSqlException(3);
            }
            if (x == null) {
                currentRowBinders[index] = theTimestampNullBinder;
            } else {
                currentRowBinders[index] = theTimestampBinder;
                if (parameterTimestamp == null) {
                    parameterTimestamp = new Timestamp[numberOfBindRowsAllocated][numberOfBindPositions];
                }
                parameterTimestamp[currentRank][index] = x;
            }
            currentRowCharLens[index] = 0;
        }
    }

    public void setTimestampAtName(String paramName, Timestamp x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTimestampAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setTimestamp(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setINTERVALYM(int paramIndex, INTERVALYM x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setINTERVALYM(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setINTERVALYMInternal(paramIndex, x);
    }

    void setINTERVALYMInternal(int paramIndex, INTERVALYM x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theIntervalYMNullBinder;
        } else {
            currentRowBinders[index] = theIntervalYMBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = x.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setINTERVALYMAtName(String paramName, INTERVALYM x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setINTERVALYMAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setINTERVALYM(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setINTERVALDS(int paramIndex, INTERVALDS x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setINTERVALDS(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setINTERVALDSInternal(paramIndex, x);
    }

    void setINTERVALDSInternal(int paramIndex, INTERVALDS x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theIntervalDSNullBinder;
        } else {
            currentRowBinders[index] = theIntervalDSBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = x.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setINTERVALDSAtName(String paramName, INTERVALDS x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setINTERVALDSAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setINTERVALDS(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setTIMESTAMP(int paramIndex, TIMESTAMP x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTIMESTAMP(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setTIMESTAMPInternal(paramIndex, x);
    }

    void setTIMESTAMPInternal(int paramIndex, TIMESTAMP x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theTimestampNullBinder;
        } else {
            currentRowBinders[index] = theOracleTimestampBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = x.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setTIMESTAMPAtName(String paramName, TIMESTAMP x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTIMESTAMPAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setTIMESTAMP(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setTIMESTAMPTZ(int paramIndex, TIMESTAMPTZ x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTIMESTAMPTZ(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setTIMESTAMPTZInternal(paramIndex, x);
    }

    void setTIMESTAMPTZInternal(int paramIndex, TIMESTAMPTZ x) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theTSTZNullBinder;
        } else {
            currentRowBinders[index] = theTSTZBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = x.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setTIMESTAMPTZAtName(String paramName, TIMESTAMPTZ x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTIMESTAMPTZAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setTIMESTAMPTZ(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setTIMESTAMPLTZ(int paramIndex, TIMESTAMPLTZ x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTIMESTAMPLTZ(paramIndex="
                                               + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setTIMESTAMPLTZInternal(paramIndex, x);
    }

    void setTIMESTAMPLTZInternal(int paramIndex, TIMESTAMPLTZ x) throws SQLException {
        if (connection.getSessionTimeZone() == null) {
            DatabaseError.throwSqlException(105);
        }
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (x == null) {
            currentRowBinders[index] = theTSLTZNullBinder;
        } else {
            currentRowBinders[index] = theTSLTZBinder;
            if (parameterDatum == null) {
                parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
            }
            parameterDatum[currentRank][index] = x.getBytes();
        }
        currentRowCharLens[index] = 0;
    }

    public void setTIMESTAMPLTZAtName(String paramName, TIMESTAMPLTZ x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setTIMESTAMPLTZAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setTIMESTAMPLTZ(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public synchronized void setAsciiStream(int paramIndex, InputStream istream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setAsciiStream(paramIndex="
                                               + paramIndex + ", istream, length=" + length + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        setAsciiStreamInternal(paramIndex, istream, length);
    }

    void setAsciiStreamInternal(int paramIndex, InputStream istream, int length)
            throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        set_execute_batch(1);
        if (istream == null) {
            basicBindNullString(paramIndex);
        } else if (userRsetType != 1 && length > maxVcsCharsSql) {
            DatabaseError.throwSqlException(169);
        } else if (currentRowFormOfUse[index] == 1) {
            if (sqlKind == 1 || sqlKind == 4) {
                if (length <= maxVcsCharsPlsql) {
                    setAsciiStreamContentsForStringInternal(paramIndex, istream, length);
                } else {
                    setAsciiStreamContentsForClobCritical(paramIndex, istream, length);
                }
            } else if (length <= maxVcsCharsSql) {
                setAsciiStreamContentsForStringInternal(paramIndex, istream, length);
            } else {
                basicBindAsciiStream(paramIndex, istream, length);
            }
        } else if (sqlKind == 1 || sqlKind == 4) {
            if (length <= maxVcsNCharsPlsql) {
                setAsciiStreamContentsForStringInternal(paramIndex, istream, length);
            } else {
                setAsciiStreamContentsForClobCritical(paramIndex, istream, length);
            }
        } else if (length <= maxVcsCharsSql) {
            setAsciiStreamContentsForStringInternal(paramIndex, istream, length);
        } else {
            setAsciiStreamContentsForClobCritical(paramIndex, istream, length);
        }
    }

    void basicBindAsciiStream(int paramIndex, InputStream istream, int length) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "OraclePreparedStatement.basicBindAsciiStream=", this);
            OracleLog.recursiveTrace = false;
        }
        if (userRsetType != 1) {
            DatabaseError.throwSqlException(169);
        }
        int index = paramIndex - 1;
        currentRowBinders[index] = theLongStreamBinder;
        if (parameterStream == null) {
            parameterStream = new InputStream[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        PhysicalConnection _tmp = connection;
        parameterStream[currentRank][index] = connection.conversion.ConvertStream(istream, 5,
                                                                                  length);
        currentRowCharLens[index] = 0;
    }

    void setAsciiStreamContentsForStringInternal(int paramIndex, InputStream istream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setAsciiStreamContentsForStringInternal(paramIndex="
                                               + paramIndex + ", inputStream, length=" + length
                                               + ")", this);
            OracleLog.recursiveTrace = false;
        }
        byte bytebuf[] = new byte[length];
        int actual_length = 0;
        try {
            actual_length = istream.read(bytebuf, 0, length);
            if (actual_length == -1) {
                actual_length = 0;
            }
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
        if (actual_length == 0) {
            basicBindNullString(paramIndex);
        }
        char charbuf[] = new char[length];
        DBConversion _tmp = connection.conversion;
        DBConversion.asciiBytesToJavaChars(bytebuf, actual_length, charbuf);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            byte crap[] = new byte[length];
            int crud = connection.conversion.javaCharsToDbCsBytes(charbuf, 0, crap, 0, length);
            OracleLog.conversionLogger
                    .log(Level.FINE, "OraclePrepareStatement.setAsciiStreamInternal("
                            + OracleLog.bytesToPrintableForm("chars[]=", OracleLog
                                    .charsToUcs2Bytes(charbuf))
                            + OracleLog.bytesToPrintableForm("bytes[]=", crap) + "crud=" + crud
                            + ")", this);
            crap = connection.conversion.asciiBytesToCHARBytes(bytebuf);
            OracleLog.conversionLogger.log(Level.FINE,
                                           "OraclePrepareStatement.setAsciiStreamInternal2("
                                                   + OracleLog.bytesToPrintableForm("in bytes[]=",
                                                                                    bytebuf)
                                                   + OracleLog.bytesToPrintableForm("outbytes[]=",
                                                                                    crap)
                                                   + "dbCS: "
                                                   + connection.conversion.getDbCharSetObj() + ")",
                                           this);
            OracleLog.recursiveTrace = false;
        }
        basicBindString(paramIndex, new String(charbuf));
    }

    public void setAsciiStreamAtName(String paramName, InputStream stream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setAsciiStream("
                    + paramName + ", " + stream + ", " + length + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        boolean firstOccurrence = true;
        for (int i = 0; i < count; i++) {
            if (names[i] != iName) {
                continue;
            }
            if (firstOccurrence) {
                setAsciiStream(i + 1, stream, length);
                firstOccurrence = false;
            } else {
                DatabaseError.throwSqlException(135);
            }
        }

    }

    public synchronized void setBinaryStream(int paramIndex, InputStream istream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setBinaryStream(paramIndex="
                                               + paramIndex + ", istream, length=" + length + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        setBinaryStreamInternal(paramIndex, istream, length);
    }

    void setBinaryStreamInternal(int paramIndex, InputStream istream, int length)
            throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        set_execute_batch(1);
        if (istream == null) {
            setRAWInternal(paramIndex, null);
        } else if (userRsetType != 1 && length > maxRawBytesSql) {
            DatabaseError.throwSqlException(169);
        } else if (sqlKind == 1 || sqlKind == 4) {
            if (length > maxRawBytesPlsql) {
                setBinaryStreamContentsForBlobCritical(paramIndex, istream, length);
            } else {
                setBinaryStreamContentsForByteArrayInternal(paramIndex, istream, length);
            }
        } else if (length > maxRawBytesSql) {
            basicBindBinaryStream(paramIndex, istream, length);
        } else {
            setBinaryStreamContentsForByteArrayInternal(paramIndex, istream, length);
        }
    }

    void setBinaryStreamContentsForByteArrayInternal(int paramIndex, InputStream inputStream,
            int length) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setBinaryStreamContentsForByteArrayInternal(paramIndex="
                                               + paramIndex + ", inputStream, length=" + length
                                               + ")", this);
            OracleLog.recursiveTrace = false;
        }
        byte bytebuf[] = new byte[length];
        int actual_length = 0;
        try {
            actual_length = inputStream.read(bytebuf, 0, length);
            if (actual_length == -1) {
                actual_length = 0;
            }
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
        if (actual_length != length) {
            byte newbuf[] = new byte[actual_length];
            System.arraycopy(bytebuf, 0, newbuf, 0, actual_length);
            bytebuf = newbuf;
        }
        setBytesInternal(paramIndex, bytebuf);
    }

    public void setBinaryStreamAtName(String paramName, InputStream stream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setBinaryStream("
                    + paramName + ", " + stream + ", " + length + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        boolean firstOccurrence = true;
        for (int i = 0; i < count; i++) {
            if (names[i] != iName) {
                continue;
            }
            if (firstOccurrence) {
                setBinaryStream(i + 1, stream, length);
                firstOccurrence = false;
            } else {
                DatabaseError.throwSqlException(135);
            }
        }

    }

    /**
     * @deprecated Method setUnicodeStream is deprecated
     */

    public synchronized void setUnicodeStream(int paramIndex, InputStream istream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setUnicodeStream(paramIndex="
                                               + paramIndex + ", istream, length=" + length + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        setUnicodeStreamInternal(paramIndex, istream, length);
    }

    void setUnicodeStreamInternal(int paramIndex, InputStream istream, int length)
            throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        set_execute_batch(1);
        if (istream == null) {
            setStringInternal(paramIndex, null);
        } else if (userRsetType != 1 && length > maxVcsCharsSql) {
            DatabaseError.throwSqlException(169);
        } else if (sqlKind == 1 || sqlKind == 4 || length <= maxVcsCharsSql) {
            byte bytebuf[] = new byte[length];
            int actual_length = 0;
            try {
                actual_length = istream.read(bytebuf, 0, length);
                if (actual_length == -1) {
                    actual_length = 0;
                }
            } catch (IOException e) {
                DatabaseError.throwSqlException(e);
            }
            char charbuf[] = new char[actual_length >> 1];
            DBConversion _tmp = connection.conversion;
            DBConversion.ucs2BytesToJavaChars(bytebuf, actual_length, charbuf);
            setStringInternal(paramIndex, new String(charbuf));
        } else {
            currentRowBinders[index] = theLongStreamBinder;
            if (parameterStream == null) {
                parameterStream = new InputStream[numberOfBindRowsAllocated][numberOfBindPositions];
            }
            PhysicalConnection _tmp1 = connection;
            parameterStream[currentRank][index] = connection.conversion.ConvertStream(istream, 4,
                                                                                      length);
            currentRowCharLens[index] = 0;
        }
    }

    public void setUnicodeStreamAtName(String paramName, InputStream stream, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setUnicodeStream("
                    + paramName + ", " + stream + ", " + length + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        boolean firstOccurrence = true;
        for (int i = 0; i < count; i++) {
            if (names[i] != iName) {
                continue;
            }
            if (firstOccurrence) {
                setUnicodeStream(i + 1, stream, length);
                firstOccurrence = false;
            } else {
                DatabaseError.throwSqlException(135);
            }
        }

    }

    /**
     * @deprecated Method setCustomDatum is deprecated
     */

    public void setCustomDatum(int paramIndex, CustomDatum x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setCustomDatum(paramIndex="
                                               + paramIndex + ", customDatum)", this);
            OracleLog.recursiveTrace = false;
        }
        synchronized (connection) {
            synchronized (this) {
                setObjectInternal(paramIndex, connection.toDatum(x));
            }
        }
    }

    void setCustomDatumInternal(int paramIndex, CustomDatum x) throws SQLException {
        synchronized (connection) {
            synchronized (this) {
                Datum datum = connection.toDatum(x);
                int sqlType = sqlTypeForObject(datum);
                setObjectCritical(paramIndex, datum, sqlType, 0);
                currentRowCharLens[paramIndex - 1] = 0;
            }
        }
    }

    public void setCustomDatumAtName(String paramName, CustomDatum x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setCustomDatumAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setCustomDatumInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setORAData(int paramIndex, ORAData x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setORAData(paramIndex="
                    + paramIndex + ", ORAData)", this);
            OracleLog.recursiveTrace = false;
        }
        setORADataInternal(paramIndex, x);
    }

    void setORADataInternal(int paramIndex, ORAData x) throws SQLException {
        synchronized (connection) {
            synchronized (this) {
                Datum datum = x.toDatum(connection);
                int sqlType = sqlTypeForObject(datum);
                setObjectCritical(paramIndex, datum, sqlType, 0);
                currentRowCharLens[paramIndex - 1] = 0;
            }
        }
    }

    public void setORADataAtName(String paramName, ORAData x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setORADataAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setORADataInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setObject(int paramIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setObject(paramIndex="
                    + paramIndex + ", object, targetSqlType=" + targetSqlType + ", scale=" + scale
                    + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setObjectInternal(paramIndex, x, targetSqlType, scale);
    }

    void setObjectInternal(int paramIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        if (x == null && targetSqlType != 2002 && targetSqlType != 2008 && targetSqlType != 2003
                && targetSqlType != 2007 && targetSqlType != 2006) {
            setNullInternal(paramIndex, targetSqlType);
        } else if (targetSqlType == 2002 || targetSqlType == 2008 || targetSqlType == 2003) {
            synchronized (connection) {
                synchronized (this) {
                    setObjectCritical(paramIndex, x, targetSqlType, scale);
                    currentRowCharLens[paramIndex - 1] = 0;
                }
            }
        } else {
            synchronized (this) {
                setObjectCritical(paramIndex, x, targetSqlType, scale);
            }
        }
    }

    void setObjectCritical(int paramIndex, Object x, int targetSqlType, int scale)
            throws SQLException {
        switch (targetSqlType) {
        case 1: // '\001'
            if (x instanceof CHAR) {
                setCHARInternal(paramIndex, (CHAR) x);
                break;
            }
            if (x instanceof String) {
                setStringInternal(paramIndex, (String) x);
                break;
            }
            if (x instanceof Boolean) {
                setStringInternal(paramIndex, "" + (((Boolean) x).booleanValue() ? 1 : 0));
                break;
            }
            if (x instanceof Integer) {
                setStringInternal(paramIndex, "" + ((Integer) x).intValue());
                break;
            }
            if (x instanceof Long) {
                setStringInternal(paramIndex, "" + ((Long) x).longValue());
                break;
            }
            if (x instanceof Float) {
                setStringInternal(paramIndex, "" + ((Float) x).floatValue());
                break;
            }
            if (x instanceof Double) {
                setStringInternal(paramIndex, "" + ((Double) x).doubleValue());
                break;
            }
            if (x instanceof BigDecimal) {
                setStringInternal(paramIndex, ((BigDecimal) x).toString());
                break;
            }
            if (x instanceof Date) {
                setStringInternal(paramIndex, "" + ((Date) x).toString());
                break;
            }
            if (x instanceof Time) {
                setStringInternal(paramIndex, "" + ((Time) x).toString());
                break;
            }
            if (x instanceof Timestamp) {
                setStringInternal(paramIndex, "" + ((Timestamp) x).toString());
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 12: // '\f'
            if (x instanceof String) {
                setStringInternal(paramIndex, (String) x);
                break;
            }
            if (x instanceof Boolean) {
                setStringInternal(paramIndex, "" + (((Boolean) x).booleanValue() ? 1 : 0));
                break;
            }
            if (x instanceof Integer) {
                setStringInternal(paramIndex, "" + ((Integer) x).intValue());
                break;
            }
            if (x instanceof Long) {
                setStringInternal(paramIndex, "" + ((Long) x).longValue());
                break;
            }
            if (x instanceof Float) {
                setStringInternal(paramIndex, "" + ((Float) x).floatValue());
                break;
            }
            if (x instanceof Double) {
                setStringInternal(paramIndex, "" + ((Double) x).doubleValue());
                break;
            }
            if (x instanceof BigDecimal) {
                setStringInternal(paramIndex, ((BigDecimal) x).toString());
                break;
            }
            if (x instanceof Date) {
                setStringInternal(paramIndex, "" + ((Date) x).toString());
                break;
            }
            if (x instanceof Time) {
                setStringInternal(paramIndex, "" + ((Time) x).toString());
                break;
            }
            if (x instanceof Timestamp) {
                setStringInternal(paramIndex, "" + ((Timestamp) x).toString());
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 999:
            setFixedCHARInternal(paramIndex, (String) x);
            break;

        case -1:
            if (x instanceof String) {
                setStringInternal(paramIndex, (String) x);
                break;
            }
            if (x instanceof Boolean) {
                setStringInternal(paramIndex, "" + (((Boolean) x).booleanValue() ? 1 : 0));
                break;
            }
            if (x instanceof Integer) {
                setStringInternal(paramIndex, "" + ((Integer) x).intValue());
                break;
            }
            if (x instanceof Long) {
                setStringInternal(paramIndex, "" + ((Long) x).longValue());
                break;
            }
            if (x instanceof Float) {
                setStringInternal(paramIndex, "" + ((Float) x).floatValue());
                break;
            }
            if (x instanceof Double) {
                setStringInternal(paramIndex, "" + ((Double) x).doubleValue());
                break;
            }
            if (x instanceof BigDecimal) {
                setStringInternal(paramIndex, ((BigDecimal) x).toString());
                break;
            }
            if (x instanceof Date) {
                setStringInternal(paramIndex, "" + ((Date) x).toString());
                break;
            }
            if (x instanceof Time) {
                setStringInternal(paramIndex, "" + ((Time) x).toString());
                break;
            }
            if (x instanceof Timestamp) {
                setStringInternal(paramIndex, "" + ((Timestamp) x).toString());
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 2: // '\002'
            if (x instanceof NUMBER) {
                setNUMBERInternal(paramIndex, (NUMBER) x);
                break;
            }
            if (x instanceof Integer) {
                setIntInternal(paramIndex, ((Integer) x).intValue());
                break;
            }
            if (x instanceof Long) {
                setLongInternal(paramIndex, ((Long) x).longValue());
                break;
            }
            if (x instanceof Float) {
                setFloatInternal(paramIndex, ((Float) x).floatValue());
                break;
            }
            if (x instanceof Double) {
                setDoubleInternal(paramIndex, ((Double) x).doubleValue());
                break;
            }
            if (x instanceof BigDecimal) {
                setBigDecimalInternal(paramIndex, (BigDecimal) x);
                break;
            }
            if (x instanceof String) {
                setNUMBERInternal(paramIndex, new NUMBER((String) x, 0));
                break;
            }
            if (x instanceof Boolean) {
                setIntInternal(paramIndex, ((Boolean) x).booleanValue() ? 1 : 0);
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 3: // '\003'
            if (x instanceof BigDecimal) {
                setBigDecimalInternal(paramIndex, (BigDecimal) x);
                break;
            }
            if (x instanceof Number) {
                setBigDecimalInternal(paramIndex, new BigDecimal(((Number) x).doubleValue()));
                break;
            }
            if (x instanceof NUMBER) {
                setBigDecimalInternal(paramIndex, ((NUMBER) x).bigDecimalValue());
                break;
            }
            if (x instanceof String) {
                setBigDecimalInternal(paramIndex, new BigDecimal((String) x));
                break;
            }
            if (x instanceof Boolean) {
                setBigDecimalInternal(paramIndex, new BigDecimal(
                        ((Boolean) x).booleanValue() ? 1.0D : 0.0D));
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case -7:
            if (x instanceof Boolean) {
                setByteInternal(paramIndex, (byte) (((Boolean) x).booleanValue() ? 1 : 0));
                break;
            }
            if (x instanceof String) {
                setByteInternal(paramIndex, (byte) (!"true".equalsIgnoreCase((String) x)
                        && !"1".equals(x) ? 0 : 1));
                break;
            }
            if (x instanceof Number) {
                setIntInternal(paramIndex, ((Number) x).byteValue() == 0 ? 0 : 1);
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case -6:
            if (x instanceof Number) {
                setByteInternal(paramIndex, ((Number) x).byteValue());
                break;
            }
            if (x instanceof String) {
                setByteInternal(paramIndex, Byte.parseByte((String) x));
                break;
            }
            if (x instanceof Boolean) {
                setByteInternal(paramIndex, (byte) (((Boolean) x).booleanValue() ? 1 : 0));
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 5: // '\005'
            if (x instanceof Number) {
                setShortInternal(paramIndex, ((Number) x).shortValue());
                break;
            }
            if (x instanceof String) {
                setShortInternal(paramIndex, Short.parseShort((String) x));
                break;
            }
            if (x instanceof Boolean) {
                setShortInternal(paramIndex, (short) (((Boolean) x).booleanValue() ? 1 : 0));
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 4: // '\004'
            if (x instanceof Number) {
                setIntInternal(paramIndex, ((Number) x).intValue());
                break;
            }
            if (x instanceof String) {
                setIntInternal(paramIndex, Integer.parseInt((String) x));
                break;
            }
            if (x instanceof Boolean) {
                setIntInternal(paramIndex, ((Boolean) x).booleanValue() ? 1 : 0);
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case -5:
            if (x instanceof Number) {
                setLongInternal(paramIndex, ((Number) x).longValue());
                break;
            }
            if (x instanceof String) {
                setLongInternal(paramIndex, Long.parseLong((String) x));
                break;
            }
            if (x instanceof Boolean) {
                setLongInternal(paramIndex, ((Boolean) x).booleanValue() ? 1L : 0L);
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 7: // '\007'
            if (x instanceof Number) {
                setFloatInternal(paramIndex, ((Number) x).floatValue());
                break;
            }
            if (x instanceof String) {
                setFloatInternal(paramIndex, Float.valueOf((String) x).floatValue());
                break;
            }
            if (x instanceof Boolean) {
                setFloatInternal(paramIndex, ((Boolean) x).booleanValue() ? 1.0F : 0.0F);
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 6: // '\006'
        case 8: // '\b'
            if (x instanceof Number) {
                setDoubleInternal(paramIndex, ((Number) x).doubleValue());
                break;
            }
            if (x instanceof String) {
                setDoubleInternal(paramIndex, Double.valueOf((String) x).doubleValue());
                break;
            }
            if (x instanceof Boolean) {
                setDoubleInternal(paramIndex, ((Boolean) x).booleanValue() ? 1.0D : 0.0D);
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case -2:
            if (x instanceof RAW) {
                setRAWInternal(paramIndex, (RAW) x);
            } else {
                setBytesInternal(paramIndex, (byte[]) x);
            }
            break;

        case -3:
            setBytesInternal(paramIndex, (byte[]) x);
            break;

        case -4:
            setBytesInternal(paramIndex, (byte[]) x);
            break;

        case 91: // '['
            if (x instanceof DATE) {
                setDATEInternal(paramIndex, (DATE) x);
                break;
            }
            if (x instanceof Date) {
                setDateInternal(paramIndex, (Date) x);
                break;
            }
            if (x instanceof Timestamp) {
                setTimestampInternal(paramIndex, (Timestamp) x);
                break;
            }
            if (x instanceof String) {
                setDateInternal(paramIndex, Date.valueOf((String) x));
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 92: // '\\'
            if (x instanceof Time) {
                setTimeInternal(paramIndex, (Time) x);
                break;
            }
            if (x instanceof Timestamp) {
                setTimeInternal(paramIndex, new Time(((Timestamp) x).getTime()));
                break;
            }
            if (x instanceof Date) {
                setTimeInternal(paramIndex, new Time(((Date) x).getTime()));
                break;
            }
            if (x instanceof String) {
                setTimeInternal(paramIndex, Time.valueOf((String) x));
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case 93: // ']'
            if (x instanceof TIMESTAMP) {
                setTIMESTAMPInternal(paramIndex, (TIMESTAMP) x);
                break;
            }
            if (x instanceof Timestamp) {
                setTimestampInternal(paramIndex, (Timestamp) x);
                break;
            }
            if (x instanceof Date) {
                setDateInternal(paramIndex, (Date) x);
                break;
            }
            if (x instanceof DATE) {
                setDATEInternal(paramIndex, (DATE) x);
                break;
            }
            if (x instanceof String) {
                setTimestampInternal(paramIndex, Timestamp.valueOf((String) x));
            } else {
                DatabaseError.throwSqlException(132);
            }
            break;

        case -100:
            setTIMESTAMPInternal(paramIndex, (TIMESTAMP) x);
            break;

        case -101:
            setTIMESTAMPTZInternal(paramIndex, (TIMESTAMPTZ) x);
            break;

        case -102:
            setTIMESTAMPLTZInternal(paramIndex, (TIMESTAMPLTZ) x);
            break;

        case -103:
            setINTERVALYMInternal(paramIndex, (INTERVALYM) x);
            break;

        case -104:
            setINTERVALDSInternal(paramIndex, (INTERVALDS) x);
            break;

        case -8:
            setROWIDInternal(paramIndex, (ROWID) x);
            break;

        case 100: // 'd'
            setBinaryFloatInternal(paramIndex, (BINARY_FLOAT) x);
            break;

        case 101: // 'e'
            setBinaryDoubleInternal(paramIndex, (BINARY_DOUBLE) x);
            break;

        case 2004:
            setBLOBInternal(paramIndex, (BLOB) x);
            break;

        case 2005:
            setCLOBInternal(paramIndex, (CLOB) x);
            break;

        case -13:
            setBFILEInternal(paramIndex, (BFILE) x);
            break;

        case 2002:
        case 2008:
            setSTRUCTInternal(paramIndex, STRUCT.toSTRUCT(x, connection));
            break;

        case 2003:
            setARRAYInternal(paramIndex, ARRAY.toARRAY(x, connection));
            break;

        case 2007:
            setOPAQUEInternal(paramIndex, (OPAQUE) x);
            break;

        case 2006:
            setREFInternal(paramIndex, (REF) x);
            break;

        default:
            DatabaseError.throwSqlException(4);
            break;
        }
    }

    public void setObjectAtName(String paramName, Object x, int targetSqlType, int scale)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setObject(" + paramName
                    + ", " + x + ", " + targetSqlType + ", " + scale + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setObjectInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setObject(int paramIndex, Object x, int targetSqlType) throws SQLException {
        setObjectInternal(paramIndex, x, targetSqlType, 0);
    }

    void setObjectInternal(int paramIndex, Object x, int targetSqlType) throws SQLException {
        setObjectInternal(paramIndex, x, targetSqlType, 0);
    }

    public void setObjectAtName(String paramName, Object x, int targetSqlType) throws SQLException {
        setObjectAtName(paramName, x, targetSqlType, 0);
    }

    public void setRefType(int paramIndex, REF ref) throws SQLException {
        setREFInternal(paramIndex, ref);
    }

    void setRefTypeInternal(int paramIndex, REF ref) throws SQLException {
        setREFInternal(paramIndex, ref);
    }

    public void setRefTypeAtName(String paramName, REF x) throws SQLException {
        setREFAtName(paramName, x);
    }

    public void setRef(int paramIndex, Ref ref) throws SQLException {
        setREFInternal(paramIndex, (REF) ref);
    }

    void setRefInternal(int paramIndex, Ref ref) throws SQLException {
        setREFInternal(paramIndex, (REF) ref);
    }

    public void setRefAtName(String paramName, Ref x) throws SQLException {
        setREFAtName(paramName, (REF) x);
    }

    public void setREF(int paramIndex, REF ref) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setREF(paramIndex="
                    + paramIndex + ", ref)", this);
            OracleLog.recursiveTrace = false;
        }
        setREFInternal(paramIndex, ref);
    }

    void setREFInternal(int paramIndex, REF ref) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (ref == null) {
            DatabaseError.throwSqlException(68);
        } else {
            synchronized (connection) {
                synchronized (this) {
                    setREFCritical(index, ref);
                    currentRowCharLens[index] = 0;
                }
            }
        }
    }

    void setREFCritical(int index, REF ref) throws SQLException {
        StructDescriptor desc = ref.getDescriptor();
        if (desc == null) {
            DatabaseError.throwSqlException(52);
        }
        currentRowBinders[index] = theRefTypeBinder;
        if (parameterDatum == null) {
            parameterDatum = new byte[numberOfBindRowsAllocated][numberOfBindPositions][];
        }
        parameterDatum[currentRank][index] = ref.getBytes();
        OracleTypeADT otype = desc.getOracleTypeADT();
        otype.getTOID();
        if (parameterOtype == null) {
            parameterOtype = new OracleTypeADT[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterOtype[currentRank][index] = otype;
    }

    public void setREFAtName(String paramName, REF x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setREFAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setREFInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setObject(int paramIndex, Object x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setObject(paramIndex="
                    + paramIndex + ", object)", this);
            OracleLog.recursiveTrace = false;
        }
        setObjectInternal(paramIndex, x);
    }

    void setObjectInternal(int paramIndex, Object x) throws SQLException {
        if (x instanceof ORAData) {
            setORADataInternal(paramIndex, (ORAData) x);
        } else if (x instanceof CustomDatum) {
            setCustomDatumInternal(paramIndex, (CustomDatum) x);
        } else {
            int sqlType = sqlTypeForObject(x);
            setObjectInternal(paramIndex, x, sqlType, 0);
        }
    }

    public void setObjectAtName(String paramName, Object x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setObjectAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setObjectInternal(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public void setOracleObject(int paramIndex, Datum x) throws SQLException {
        setObjectInternal(paramIndex, x);
    }

    void setOracleObjectInternal(int paramIndex, Datum x) throws SQLException {
        setObjectInternal(paramIndex, x);
    }

    public void setOracleObjectAtName(String paramName, Datum x) throws SQLException {
        setObjectAtName(paramName, x);
    }

    public synchronized void setPlsqlIndexTable(int paramIndex, Object arrayData, int maxLen,
            int curLen, int elemSqlType, int elemMaxLen) throws SQLException {
        setPlsqlIndexTableInternal(paramIndex, arrayData, maxLen, curLen, elemSqlType, elemMaxLen);
    }

    void setPlsqlIndexTableInternal(int paramIndex, Object arrayData, int maxLen, int curLen,
            int elemSqlType, int elemMaxLen) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int elem_internal_type = getInternalType(elemSqlType);
        Object darray[] = null;
        switch (elem_internal_type) {
        case 1: // '\001'
        case 96: // '`'
            String l_darray[] = null;
            int datalen = 0;
            if (arrayData instanceof CHAR[]) {
                CHAR data[] = (CHAR[]) arrayData;
                datalen = data.length;
                l_darray = new String[datalen];
                for (int i = 0; i < datalen; i++) {
                    CHAR elem = data[i];
                    if (elem != null) {
                        l_darray[i] = elem.getString();
                    }
                }

            } else if (arrayData instanceof String[]) {
                l_darray = (String[]) arrayData;
                datalen = l_darray.length;
            }
            if (elemMaxLen == 0 && l_darray != null) {
                for (int i = 0; i < datalen; i++) {
                    String elem = l_darray[i];
                    if (elem != null && elemMaxLen < elem.length()) {
                        elemMaxLen = elem.length();
                    }
                }

            }
            darray = l_darray;
            break;

        case 2: // '\002'
        case 6: // '\006'
            darray = OracleTypeNUMBER.toNUMBERArray(arrayData, connection, 1L, curLen);
            if (elemMaxLen == 0 && darray != null) {
                elemMaxLen = 22;
            }
            currentRowCharLens[index] = 0;
            break;

        default:
            DatabaseError.throwSqlException(97);
            return;
        }
        currentRowBinders[index] = thePlsqlIbtBinder;
        if (parameterPlsqlIbt == null) {
            parameterPlsqlIbt = new PlsqlIbtBindInfo[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        parameterPlsqlIbt[currentRank][index] = new PlsqlIbtBindInfo(darray, maxLen, curLen,
                elem_internal_type, elemMaxLen);
        hasIbtBind = true;
    }

    public synchronized void setPlsqlIndexTableAtName(String paramName, Object arrayData,
            int maxLen, int curLen, int elemSqlType, int elemMaxLen) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setPlsqlIndexTableAtName("
                                               + paramName + ", " + arrayData + ", " + maxLen
                                               + ", " + curLen + ", " + elemSqlType + ", "
                                               + elemMaxLen + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setPlsqlIndexTableInternal(i + 1, arrayData, maxLen, curLen, elemSqlType,
                                           elemMaxLen);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    void endOfResultSet(boolean dont_call_prepare_for_new_result) throws SQLException {
        if (!dont_call_prepare_for_new_result) {
            prepareForNewResults(false, false);
        }
    }

    int sqlTypeForObject(Object x) {
        if (x == null) {
            return 0;
        }
        if (!(x instanceof Datum)) {
            if (x instanceof String) {
                return fixedString ? 999 : 12;
            }
            if (x instanceof BigDecimal) {
                return 2;
            }
            if (x instanceof Boolean) {
                return -7;
            }
            if (x instanceof Integer) {
                return 4;
            }
            if (x instanceof Long) {
                return -5;
            }
            if (x instanceof Float) {
                return 6;
            }
            if (x instanceof Double) {
                return 8;
            }
            if (x instanceof byte[]) {
                return -3;
            }
            if (x instanceof Short) {
                return 5;
            }
            if (x instanceof Byte) {
                return -6;
            }
            if (x instanceof Date) {
                return 91;
            }
            if (x instanceof Time) {
                return 92;
            }
            if (x instanceof Timestamp) {
                return 93;
            }
            if (x instanceof SQLData) {
                return 2002;
            }
            if (x instanceof ObjectData) {
                return 2002;
            }
        } else {
            if (x instanceof BINARY_FLOAT) {
                return 100;
            }
            if (x instanceof BINARY_DOUBLE) {
                return 101;
            }
            if (x instanceof BLOB) {
                return 2004;
            }
            if (x instanceof CLOB) {
                return 2005;
            }
            if (x instanceof BFILE) {
                return -13;
            }
            if (x instanceof ROWID) {
                return -8;
            }
            if (x instanceof NUMBER) {
                return 2;
            }
            if (x instanceof DATE) {
                return 91;
            }
            if (x instanceof TIMESTAMP) {
                return 93;
            }
            if (x instanceof TIMESTAMPTZ) {
                return -101;
            }
            if (x instanceof TIMESTAMPLTZ) {
                return -102;
            }
            if (x instanceof REF) {
                return 2006;
            }
            if (x instanceof CHAR) {
                return 1;
            }
            if (x instanceof RAW) {
                return -2;
            }
            if (x instanceof ARRAY) {
                return 2003;
            }
            if (x instanceof STRUCT) {
                return 2002;
            }
            if (x instanceof OPAQUE) {
                return 2007;
            }
            if (x instanceof INTERVALYM) {
                return -103;
            }
            if (x instanceof INTERVALDS) {
                return -104;
            }
        }
        return 1111;
    }

    public synchronized void clearParameters() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OraclePreparedStatement.clearParameters()",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        clearParameters = true;
        for (int i = 0; i < numberOfBindPositions; i++) {
            currentRowBinders[i] = null;
        }

    }

    void printByteArray(byte b[]) {
        if (b == null) {
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINEST, "<Null byte array!>", this);
                OracleLog.recursiveTrace = false;
            }
        } else {
            int n = b.length;
            for (int i = 0; i < n; i++) {
                int v = b[i] & 0xff;
                if (v < 16) {
                    if (TRACE && !OracleLog.recursiveTrace) {
                        OracleLog.recursiveTrace = true;
                        OracleLog.driverLogger.log(Level.FINEST,
                                                   "0" + Integer.toHexString(v) + " ", this);
                        OracleLog.recursiveTrace = false;
                    }
                    continue;
                }
                if (TRACE && !OracleLog.recursiveTrace) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINEST, Integer.toHexString(v) + " ", this);
                    OracleLog.recursiveTrace = false;
                }
            }

        }
    }

    public void setCharacterStream(int paramIndex, Reader reader, int length) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setCharacterStream(paramIndex="
                                               + paramIndex + ", reader, length=" + length + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        setCharacterStreamInternal(paramIndex, reader, length);
    }

    void setCharacterStreamInternal(int paramIndex, Reader reader, int length) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        set_execute_batch(1);
        if (reader == null) {
            basicBindNullString(paramIndex);
        } else if (userRsetType != 1 && length > maxVcsCharsSql) {
            DatabaseError.throwSqlException(169);
        } else if (currentRowFormOfUse[index] == 1) {
            if (sqlKind == 1 || sqlKind == 4) {
                if (length > maxVcsBytesPlsql || (length > maxVcsCharsPlsql)
                        & isServerCharSetFixedWidth) {
                    setReaderContentsForClobCritical(paramIndex, reader, length);
                } else if (length <= maxVcsCharsPlsql) {
                    setReaderContentsForStringInternal(paramIndex, reader, length);
                } else {
                    setReaderContentsForStringOrClobInVariableWidthCase(paramIndex, reader, length,
                                                                        false);
                }
            } else if (length <= maxVcsCharsSql) {
                setReaderContentsForStringInternal(paramIndex, reader, length);
            } else {
                basicBindCharacterStream(paramIndex, reader, length);
            }
        } else if (sqlKind == 1 || sqlKind == 4) {
            if (length > maxVcsBytesPlsql || (length > maxVcsNCharsPlsql)
                    & isServerCharSetFixedWidth) {
                setReaderContentsForClobCritical(paramIndex, reader, length);
            } else if (length <= maxVcsNCharsPlsql) {
                setReaderContentsForStringInternal(paramIndex, reader, length);
            } else {
                setReaderContentsForStringOrClobInVariableWidthCase(paramIndex, reader, length,
                                                                    true);
            }
        } else if (length <= maxVcsCharsSql) {
            setReaderContentsForStringInternal(paramIndex, reader, length);
        } else {
            setReaderContentsForClobCritical(paramIndex, reader, length);
        }
    }

    void basicBindCharacterStream(int paramIndex, Reader reader, int length) throws SQLException {
        int index = paramIndex - 1;
        currentRowBinders[index] = theLongStreamBinder;
        if (parameterStream == null) {
            parameterStream = new InputStream[numberOfBindRowsAllocated][numberOfBindPositions];
        }
        PhysicalConnection _tmp = connection;
        parameterStream[currentRank][index] = connection.conversion
                .ConvertStream(reader, 7, length, currentRowFormOfUse[index]);
        currentRowCharLens[index] = 0;
    }

    void setReaderContentsForStringOrClobInVariableWidthCase(int paramIndex, Reader reader,
            int length, boolean isNChar) throws SQLException {
        char charbuf[] = new char[length];
        int chars_length = 0;
        try {
            chars_length = reader.read(charbuf, 0, length);
            if (chars_length == -1) {
                chars_length = 0;
            }
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
        if (chars_length != length) {
            char newbuf[] = new char[chars_length];
            System.arraycopy(charbuf, 0, newbuf, 0, chars_length);
            charbuf = newbuf;
        }
        int bytes_length = connection.conversion.encodedByteLength(charbuf, isNChar);
        if (bytes_length < maxVcsBytesPlsql) {
            setStringInternal(paramIndex, new String(charbuf));
        } else {
            setStringForClobCritical(paramIndex, new String(charbuf));
        }
    }

    void setReaderContentsForStringInternal(int paramIndex, Reader reader, int length)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.setReaderContentsForStringInternal(paramIndex="
                                               + paramIndex + ", reader, length=" + length + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        char charbuf[] = new char[length];
        int actual_length = 0;
        try {
            actual_length = reader.read(charbuf, 0, length);
            if (actual_length == -1) {
                actual_length = 0;
            }
        } catch (IOException e) {
            DatabaseError.throwSqlException(e);
        }
        if (actual_length != length) {
            char newbuf[] = new char[actual_length];
            System.arraycopy(charbuf, 0, newbuf, 0, actual_length);
            charbuf = newbuf;
        }
        setStringInternal(paramIndex, new String(charbuf));
    }

    public void setDate(int paramIndex, Date x, Calendar cal) throws SQLException {
        setDATEInternal(paramIndex, x != null ? new DATE(x, cal) : null);
    }

    void setDateInternal(int paramIndex, Date x, Calendar cal) throws SQLException {
        setDATEInternal(paramIndex, x != null ? new DATE(x, cal) : null);
    }

    public void setTime(int paramIndex, Time x, Calendar cal) throws SQLException {
        setDATEInternal(paramIndex, x != null ? new DATE(x, cal) : null);
    }

    void setTimeInternal(int paramIndex, Time x, Calendar cal) throws SQLException {
        setDATEInternal(paramIndex, x != null ? new DATE(x, cal) : null);
    }

    public void setTimestamp(int paramIndex, Timestamp x, Calendar cal) throws SQLException {
        setTimestampInternal(paramIndex, x, cal);
    }

    void setTimestampInternal(int paramIndex, Timestamp x, Calendar cal) throws SQLException {
        if (connection.v8Compatible) {
            if (x == null) {
                setDATEInternal(paramIndex, null);
            } else {
                DATE d = new DATE(x, cal);
                setDATEInternal(paramIndex, d);
            }
            return;
        }
        if (x == null) {
            setTIMESTAMPInternal(paramIndex, null);
        } else {
            int nanos = x.getNanos();
            byte result[];
            if (nanos == 0) {
                result = new byte[7];
            } else {
                result = new byte[11];
            }
            if (cal == null) {
                cal = Calendar.getInstance();
            }
            cal.clear();
            cal.setTime(x);
            int year = cal.get(1);
            if (cal.get(0) == 0) {
                year = -(year - 1);
            }
            result[0] = (byte) (year / 100 + 100);
            result[1] = (byte) (year % 100 + 100);
            result[2] = (byte) (cal.get(2) + 1);
            result[3] = (byte) cal.get(5);
            result[4] = (byte) (cal.get(11) + 1);
            result[5] = (byte) (cal.get(12) + 1);
            result[6] = (byte) (cal.get(13) + 1);
            if (nanos != 0) {
                result[7] = (byte) (nanos >> 24);
                result[8] = (byte) (nanos >> 16 & 0xff);
                result[9] = (byte) (nanos >> 8 & 0xff);
                result[10] = (byte) (nanos & 0xff);
            }
            setTIMESTAMPInternal(paramIndex, new TIMESTAMP(result));
        }
    }

    public void setCheckBindTypes(boolean flag) {
        checkBindTypes = flag;
    }

    final void setOracleBatchStyle() throws SQLException {
        if (m_batchStyle == 2) {
            DatabaseError
                    .throwSqlException(90, "operation cannot be mixed with JDBC-2.0-style batching");
        } else if (m_batchStyle == 0 && TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "First detected Oracle-style batching", this);
            OracleLog.recursiveTrace = false;
        }
        m_batchStyle = 1;
    }

    final void setJdbcBatchStyle() throws SQLException {
        if (m_batchStyle == 1) {
            DatabaseError.throwSqlException(90,
                                            "operation cannot be mixed with Oracle-style batching");
        }
        m_batchStyle = 2;
    }

    final void checkIfJdbcBatchExists() throws SQLException {
        if (doesJdbcBatchExist()) {
            DatabaseError.throwSqlException(81, "batch must be either executed or cleared");
        }
    }

    boolean doesJdbcBatchExist() {
        return currentRank > 0 && m_batchStyle == 2;
    }

    boolean isJdbcBatchStyle() {
        return m_batchStyle == 2;
    }

    public synchronized void addBatch() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.addBatch()", this);
            OracleLog.recursiveTrace = false;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            if (m_batchStyle == 0) {
                OracleLog.driverLogger.log(Level.FINER, "First detected Jdbc-style batching", this);
            }
            OracleLog.recursiveTrace = false;
        }
        setJdbcBatchStyle();
        processCompletedBindRow(currentRank + 2, currentRank > 0 && (sqlKind == 1 || sqlKind == 4));
        currentRank++;
    }

    public synchronized void addBatch(String sql) throws SQLException {
        DatabaseError.throwSqlException(23);
    }

    public synchronized void clearBatch() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.clearBatch()", this);
            OracleLog.recursiveTrace = false;
        }
        for (int r = currentRank; r >= 0; r--) {
            for (int i = 0; i < numberOfBindPositions; i++) {
                binders[r][i] = null;
            }

        }

        currentRank = 0;
        if (binders != null) {
            currentRowBinders = binders[0];
        }
        pushedBatches = null;
        pushedBatchesTail = null;
        firstRowInBatch = 0;
        clearParameters = true;
    }

    public int[] executeBatch() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                int i = 0;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.INFO,
                                               "OraclePreparedStatement.executeBatch()", this);

                    OracleLog.recursiveTrace = false;
                }

                setJdbcBatchStyle();

                int[] ret_array = new int[this.currentRank];

                if (this.currentRank > 0) {
                    ensureOpen();

                    prepareForNewResults(true, true);

                    if (this.sqlKind == 0) {
                        DatabaseError.throwBatchUpdateException(80, 0, null);
                    }

                    this.noMoreUpdateCounts = false;

                    int save_valid_rows = 0;
                    try {
                        this.connection.needLine();

                        if (!this.isOpen) {
                            this.connection.open(this);

                            this.isOpen = true;
                        }

                        int saved_current_rank = this.currentRank;

                        if (this.pushedBatches == null) {
                            setupBindBuffers(0, this.currentRank);
                            executeForRows(false);
                        } else {
                            if (this.currentRank > this.firstRowInBatch) {
                                pushBatch(true);
                            }
                            boolean saved_need_to_parse = this.needToParse;
                            do {
                                PushedBatch pb = this.pushedBatches;

                                this.currentBatchCharLens = pb.currentBatchCharLens;
                                this.lastBoundCharLens = pb.lastBoundCharLens;
                                this.lastBoundNeeded = pb.lastBoundNeeded;
                                this.currentBatchBindAccessors = pb.currentBatchBindAccessors;
                                this.needToParse = pb.need_to_parse;
                                this.currentBatchNeedToPrepareBinds = pb.current_batch_need_to_prepare_binds;

                                this.firstRowInBatch = pb.first_row_in_batch;

                                setupBindBuffers(pb.first_row_in_batch,
                                                 pb.number_of_rows_to_be_bound);

                                this.currentRank = pb.number_of_rows_to_be_bound;

                                executeForRows(false);

                                if ((this.sqlKind == 1) || (this.sqlKind == 4)) {
                                    save_valid_rows += this.validRows;
                                    ret_array[(i++)] = this.validRows;
                                }

                                this.pushedBatches = pb.next;
                            }

                            while (this.pushedBatches != null);

                            this.pushedBatchesTail = null;
                            this.firstRowInBatch = 0;

                            this.needToParse = saved_need_to_parse;
                        }

                        slideDownCurrentRow(saved_current_rank);
                    } catch (SQLException e) {
                        clearBatch();
                        this.needToParse = true;

                        if ((this.sqlKind != 1) && (this.sqlKind != 4)) {
                            for (i = 0; i < ret_array.length; i++)
                                ret_array[i] = -3;
                        }
                        DatabaseError.throwBatchUpdateException(e, (this.sqlKind == 1)
                                || (this.sqlKind == 4) ? i : ret_array.length, ret_array);
                    } finally {
                        if ((this.sqlKind == 1) || (this.sqlKind == 4)) {
                            this.validRows = save_valid_rows;
                        }
                        checkValidRowsStatus();

                        this.currentRank = 0;
                        cleanOldTempLobs();
                    }

                    if (this.validRows < 0) {
                        for (i = 0; i < ret_array.length; i++) {
                            ret_array[i] = -3;
                        }
                        DatabaseError.throwBatchUpdateException(81, 0, ret_array);
                    } else if ((this.sqlKind != 1) && (this.sqlKind != 4)) {
                        for (i = 0; i < ret_array.length; i++) {
                            ret_array[i] = -2;
                        }
                    }
                }
                return ret_array;
            }
        }
    }

    void pushBatch(boolean fromExecuteBatch) {
        PushedBatch pb = new PushedBatch();
        pb.currentBatchCharLens = new int[numberOfBindPositions];
        System
                .arraycopy(currentBatchCharLens, 0, pb.currentBatchCharLens, 0,
                           numberOfBindPositions);
        pb.lastBoundCharLens = new int[numberOfBindPositions];
        System.arraycopy(lastBoundCharLens, 0, pb.lastBoundCharLens, 0, numberOfBindPositions);
        if (currentBatchBindAccessors != null) {
            pb.currentBatchBindAccessors = new Accessor[numberOfBindPositions];
            System.arraycopy(currentBatchBindAccessors, 0, pb.currentBatchBindAccessors, 0,
                             numberOfBindPositions);
        }
        pb.lastBoundNeeded = lastBoundNeeded;
        pb.need_to_parse = needToParse;
        pb.current_batch_need_to_prepare_binds = currentBatchNeedToPrepareBinds;
        pb.first_row_in_batch = firstRowInBatch;
        pb.number_of_rows_to_be_bound = currentRank - firstRowInBatch;
        if (pushedBatches == null) {
            pushedBatches = pb;
        } else {
            pushedBatchesTail.next = pb;
        }
        pushedBatchesTail = pb;
        if (!fromExecuteBatch) {
            int tmp[] = currentBatchCharLens;
            currentBatchCharLens = lastBoundCharLens;
            lastBoundCharLens = tmp;
            lastBoundNeeded = false;
            for (int i = 0; i < numberOfBindPositions; i++) {
                currentBatchCharLens[i] = 0;
            }

            firstRowInBatch = currentRank;
        }
    }

    int doScrollPstmtExecuteUpdate() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "OraclePreparedStatement.doScrollPstmtExecuteUpdate()", this);
            OracleLog.recursiveTrace = false;
        }
        doScrollExecuteCommon();
        if (sqlKind == 0) {
            scrollRsetTypeSolved = true;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.doScrollPstmtExecuteUpdate():returnvalidRows = "
                                               + validRows, this);
            OracleLog.recursiveTrace = false;
        }
        return validRows;
    }

    public int copyBinds(Statement toStmt, int offset) throws SQLException {
        if (numberOfBindPositions > 0) {
            OraclePreparedStatement to = (OraclePreparedStatement) toStmt;
            Binder binds[] = binders[0];
            int metadata_offset = bindIndicatorSubRange + 3;
            int byteoffset = bindByteSubRange;
            int charoffset = bindCharSubRange;
            int indoffset = indicatorsOffset;
            int lenoffset = valueLengthsOffset;
            for (int i = 0; i < numberOfBindPositions; i++) {
                short type = bindIndicators[metadata_offset + 0];
                int bytePitch = bindIndicators[metadata_offset + 1];
                int charPitch = bindIndicators[metadata_offset + 2];
                int toPos = i + offset;
                if (bindIndicators[indoffset] == -1) {
                    to.currentRowBinders[toPos] = copiedNullBinder(type, bytePitch);
                    if (charPitch > 0) {
                        to.currentRowCharLens[toPos] = 1;
                    }
                } else if (type == 109 || type == 111) {
                    to.currentRowBinders[toPos] = type != 109 ? theRefTypeBinder
                            : theNamedTypeBinder;
                    byte from[] = parameterDatum[0][i];
                    int len = from.length;
                    byte tob[] = new byte[len];
                    to.parameterDatum[0][toPos] = tob;
                    System.arraycopy(from, 0, tob, 0, len);
                    to.parameterOtype[0][toPos] = parameterOtype[0][i];
                } else if (bytePitch > 0) {
                    to.currentRowBinders[toPos] = copiedByteBinder(type, bindBytes, byteoffset,
                                                                   bytePitch,
                                                                   bindIndicators[lenoffset]);
                } else if (charPitch > 0) {
                    to.currentRowBinders[toPos] = copiedCharBinder(type, bindChars, charoffset,
                                                                   charPitch,
                                                                   bindIndicators[lenoffset]);
                    to.currentRowCharLens[toPos] = charPitch;
                } else {
                    throw new Error("copyBinds doesn't understand type " + type);
                }
                byteoffset += bindBufferCapacity * bytePitch;
                charoffset += bindBufferCapacity * charPitch;
                indoffset += numberOfBindRowsAllocated;
                lenoffset += numberOfBindRowsAllocated;
                metadata_offset += 10;
            }

        }
        return numberOfBindPositions;
    }

    Binder copiedNullBinder(short type, int bytelen) throws SQLException {
        return new CopiedNullBinder(type, bytelen);
    }

    Binder copiedByteBinder(short type, byte bytes[], int offset, int pitch, short len)
            throws SQLException {
        byte b[] = new byte[pitch];
        System.arraycopy(bytes, offset, b, 0, pitch);
        return new CopiedByteBinder(type, pitch, b, len);
    }

    Binder copiedCharBinder(short type, char chars[], int offset, int pitch, short len)
            throws SQLException {
        char c[] = new char[pitch];
        System.arraycopy(chars, offset, c, 0, pitch);
        return new CopiedCharBinder(type, c, len);
    }

    protected void hardClose() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OraclePreparedStatement.hardClose()", this);
            OracleLog.recursiveTrace = false;
        }
        super.hardClose();
        bindBytes = null;
        bindChars = null;
        bindIndicators = null;
        if (!connection.isClosed()) {
            cleanAllTempLobs();
        }
        lastBoundBytes = null;
        lastBoundChars = null;
        clearParameters();
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OraclePreparedStatement.hardClose : return",
                                       this);
            OracleLog.recursiveTrace = false;
        }
    }

    protected void alwaysOnClose() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OraclePreparedStatement.alwaysOnClose()", this);
            OracleLog.recursiveTrace = false;
        }
        if (currentRank > 0) {
            if (m_batchStyle == 2) {
                clearBatch();
            } else {
                sendBatch();
            }
        }
        super.alwaysOnClose();
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OraclePreparedStatement.alwaysOnClose : return", this);
            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void setDisableStmtCaching(boolean cache) {
        if (cache) {
            cacheState = 3;
        }
    }

    public synchronized void setFormOfUse(int paramIndex, short formOfUse) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setFormOfUse(paramIndex="
                                               + paramIndex + ", formOfUse=" + formOfUse + ")",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        int i = paramIndex - 1;
        if (currentRowFormOfUse[i] != formOfUse) {
            currentRowFormOfUse[i] = formOfUse;
            if (currentRowBindAccessors != null) {
                Accessor accessor = currentRowBindAccessors[i];
                if (accessor != null) {
                    accessor.setFormOfUse(formOfUse);
                }
            }
            if (returnParamAccessors != null) {
                Accessor accessor = returnParamAccessors[i];
                if (accessor != null) {
                    accessor.setFormOfUse(formOfUse);
                }
            }
        }
    }

    public synchronized void setURL(int paramIndex, URL x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.setURL(paramIndex="
                    + paramIndex + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        setURLInternal(paramIndex, x);
    }

    void setURLInternal(int paramIndex, URL x) throws SQLException {
        setStringInternal(paramIndex, x.toString());
    }

    public void setURLAtName(String paramName, URL x) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.setURLAtName(paramName="
                                               + paramName + ", x=" + x + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String iName = paramName.intern();
        String names[] = sqlObject.getParameterList();
        boolean nameMatchedAtLeastOnce = false;
        int count = Math.min(sqlObject.getParameterCount(), names.length);
        for (int i = 0; i < count; i++) {
            if (names[i] == iName) {
                setURL(i + 1, x);
                nameMatchedAtLeastOnce = true;
            }
        }

        if (!nameMatchedAtLeastOnce) {
            DatabaseError.throwSqlException(147, paramName);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        return new oracle.jdbc.driver.OracleParameterMetaData(sqlObject.getParameterCount());
    }

    public oracle.jdbc.OracleParameterMetaData OracleGetParameterMetaData() throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public void registerReturnParameter(int paramIndex, int externalType) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.registerReturnParameter(paramIndex="
                                               + paramIndex + ", externalType =" + externalType
                                               + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (numberOfBindPositions <= 0) {
            DatabaseError.throwSqlException(90);
        }
        if (numReturnParams <= 0) {
            numReturnParams = getReturnParameterCount(sqlObject.getOriginalSql());
            if (numReturnParams <= 0) {
                DatabaseError.throwSqlException(90);
            }
        }
        int index = paramIndex - 1;
        if (index < numberOfBindPositions - numReturnParams || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int internalType = getInternalTypeForDmlReturning(externalType);
        short form = 0;
        if (currentRowFormOfUse != null && currentRowFormOfUse[index] != 0) {
            form = currentRowFormOfUse[index];
        }
        registerReturnParameterInternal(index, internalType, externalType, -1, form, null);
        currentRowBinders[index] = theReturnParamBinder;
    }

    public void registerReturnParameter(int paramIndex, int externalType, int maxSize)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.registerReturnParameter(paramIndex="
                                               + paramIndex + ", externalType=" + externalType
                                               + ", maxSize=" + maxSize + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (numberOfBindPositions <= 0) {
            DatabaseError.throwSqlException(90);
        }
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (externalType != 1 && externalType != 12 && externalType != -1 && externalType != -2
                && externalType != -3 && externalType != -4 && externalType != 12) {
            DatabaseError.throwSqlException(68);
        }
        if (maxSize <= 0) {
            DatabaseError.throwSqlException(68);
        }
        int internalType = getInternalTypeForDmlReturning(externalType);
        short form = 0;
        if (currentRowFormOfUse != null && currentRowFormOfUse[index] != 0) {
            form = currentRowFormOfUse[index];
        }
        registerReturnParameterInternal(index, internalType, externalType, maxSize, form, null);
        currentRowBinders[index] = theReturnParamBinder;
    }

    public void registerReturnParameter(int paramIndex, int externalType, String typeName)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OraclePreparedStatement.registerReturnParameter(paramIndex="
                                               + paramIndex + ", externalType=" + externalType
                                               + ", typeName=" + typeName + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (numberOfBindPositions <= 0) {
            DatabaseError.throwSqlException(90);
        }
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int internalType = getInternalTypeForDmlReturning(externalType);
        if (internalType != 111 && internalType != 109) {
            DatabaseError.throwSqlException(68);
        }
        registerReturnParameterInternal(index, internalType, externalType, -1, (short) 0, typeName);
        currentRowBinders[index] = theReturnParamBinder;
    }

    public ResultSet getReturnResultSet() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OraclePreparedStatement.getReturnResultSet()",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        if (closed) {
            DatabaseError.throwSqlException(9);
        }
        if (returnParamAccessors == null || numReturnParams == 0) {
            DatabaseError.throwSqlException(144);
        }
        if (returnResultSet == null) {
            returnResultSet = new OracleReturnResultSet(this);
        }
        return returnResultSet;
    }

    int getInternalTypeForDmlReturning(int externalType) throws SQLException {
        int result = 0;
        switch (externalType) {
        case -7:
        case -6:
        case -5:
        case 2: // '\002'
        case 3: // '\003'
        case 4: // '\004'
        case 5: // '\005'
        case 6: // '\006'
        case 7: // '\007'
        case 8: // '\b'
            result = 6;
            break;

        case 100: // 'd'
            result = 100;
            break;

        case 101: // 'e'
            result = 101;
            break;

        case 1: // '\001'
            result = 96;
            break;

        case 12: // '\f'
            result = 1;
            break;

        case -1:
            result = 8;
            break;

        case 91: // '['
        case 92: // '\\'
            result = 12;
            break;

        case 93: // ']'
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

        case 2002:
        case 2003:
        case 2007:
        case 2008:
            result = 109;
            break;

        case 2006:
            result = 111;
            break;

        case 70: // 'F'
            result = 1;
            break;

        default:
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "OraclePreparedStatement.getInternalTypeForDmlReturning("
                                                   + externalType + ") error", this);
                OracleLog.recursiveTrace = false;
            }
            DatabaseError.throwSqlException(4);
            break;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "OraclePreparedStatement.getInternalTypeForDmlReturning("
                                               + externalType + ") return " + result, this);
            OracleLog.recursiveTrace = false;
        }
        return result;
    }

    static int getReturnParameterCount(String sql) {
        int returnParameterCount = -1;
        String s = sql.toUpperCase();
        int pos = s.indexOf("RETURNING");
        if (pos >= 0) {
            char clauseOfReturning[] = new char[s.length() - pos];
            s.getChars(pos, s.length(), clauseOfReturning, 0);
            returnParameterCount = 0;
            for (int i = 0; i < clauseOfReturning.length; i++) {
                if (clauseOfReturning[i] == '?') {
                    returnParameterCount++;
                }
            }

        }
        return returnParameterCount;
    }

    void registerReturnParamsForAutoKey() throws SQLException {
        int returnTypes[] = autoKeyInfo.returnTypes;
        short formOfUses[] = autoKeyInfo.tableFormOfUses;
        int columnIndexes[] = autoKeyInfo.columnIndexes;
        int numberOfReturnParams = returnTypes.length;
        int startPos = numberOfBindPositions - numberOfReturnParams;
        for (int i = 0; i < numberOfReturnParams; i++) {
            int index = startPos + i;
            currentRowBinders[index] = theReturnParamBinder;
            short form = 0;
            if (formOfUses != null && columnIndexes != null
                    && formOfUses[columnIndexes[i] - 1] == 2) {
                form = 2;
                setFormOfUse(index + 1, form);
            }
            checkTypeForAutoKey(returnTypes[i]);
            String typeName = null;
            if (returnTypes[i] == 111) {
                typeName = autoKeyInfo.tableTypeNames[columnIndexes[i] - 1];
            }
            registerReturnParameterInternal(index, returnTypes[i], returnTypes[i], -1, form,
                                            typeName);
        }

    }

    static {
        theStaticVarnumCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarnumCopyingBinder;
        theStaticVarnumNullBinder = OraclePreparedStatementReadOnly.theStaticVarnumNullBinder;
        theStaticBooleanBinder = OraclePreparedStatementReadOnly.theStaticBooleanBinder;
        theStaticByteBinder = OraclePreparedStatementReadOnly.theStaticByteBinder;
        theStaticShortBinder = OraclePreparedStatementReadOnly.theStaticShortBinder;
        theStaticIntBinder = OraclePreparedStatementReadOnly.theStaticIntBinder;
        theStaticLongBinder = OraclePreparedStatementReadOnly.theStaticLongBinder;
        theStaticFloatBinder = OraclePreparedStatementReadOnly.theStaticFloatBinder;
        theStaticDoubleBinder = OraclePreparedStatementReadOnly.theStaticDoubleBinder;
        theStaticBigDecimalBinder = OraclePreparedStatementReadOnly.theStaticBigDecimalBinder;
        theStaticVarcharCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarcharCopyingBinder;
        theStaticVarcharNullBinder = OraclePreparedStatementReadOnly.theStaticVarcharNullBinder;
        theStaticStringBinder = OraclePreparedStatementReadOnly.theStaticStringBinder;
        theStaticSetCHARCopyingBinder = OraclePreparedStatementReadOnly.theStaticSetCHARCopyingBinder;
        theStaticSetCHARBinder = OraclePreparedStatementReadOnly.theStaticSetCHARBinder;
        theStaticLittleEndianSetCHARBinder = OraclePreparedStatementReadOnly.theStaticLittleEndianSetCHARBinder;
        theStaticSetCHARNullBinder = OraclePreparedStatementReadOnly.theStaticSetCHARNullBinder;
        theStaticFixedCHARCopyingBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARCopyingBinder;
        theStaticFixedCHARBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARBinder;
        theStaticFixedCHARNullBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARNullBinder;
        theStaticDateCopyingBinder = OraclePreparedStatementReadOnly.theStaticDateCopyingBinder;
        theStaticDateBinder = OraclePreparedStatementReadOnly.theStaticDateBinder;
        theStaticDateNullBinder = OraclePreparedStatementReadOnly.theStaticDateNullBinder;
        theStaticTimeCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimeCopyingBinder;
        theStaticTimeBinder = OraclePreparedStatementReadOnly.theStaticTimeBinder;
        theStaticTimestampCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimestampCopyingBinder;
        theStaticTimestampBinder = OraclePreparedStatementReadOnly.theStaticTimestampBinder;
        theStaticTimestampNullBinder = OraclePreparedStatementReadOnly.theStaticTimestampNullBinder;
        theStaticOracleNumberBinder = OraclePreparedStatementReadOnly.theStaticOracleNumberBinder;
        theStaticOracleDateBinder = OraclePreparedStatementReadOnly.theStaticOracleDateBinder;
        theStaticOracleTimestampBinder = OraclePreparedStatementReadOnly.theStaticOracleTimestampBinder;
        theStaticTSTZCopyingBinder = OraclePreparedStatementReadOnly.theStaticTSTZCopyingBinder;
        theStaticTSTZBinder = OraclePreparedStatementReadOnly.theStaticTSTZBinder;
        theStaticTSTZNullBinder = OraclePreparedStatementReadOnly.theStaticTSTZNullBinder;
        theStaticTSLTZCopyingBinder = OraclePreparedStatementReadOnly.theStaticTSLTZCopyingBinder;
        theStaticTSLTZBinder = OraclePreparedStatementReadOnly.theStaticTSLTZBinder;
        theStaticTSLTZNullBinder = OraclePreparedStatementReadOnly.theStaticTSLTZNullBinder;
        theStaticRowidCopyingBinder = OraclePreparedStatementReadOnly.theStaticRowidCopyingBinder;
        theStaticRowidBinder = OraclePreparedStatementReadOnly.theStaticRowidBinder;
        theStaticLittleEndianRowidBinder = OraclePreparedStatementReadOnly.theStaticLittleEndianRowidBinder;
        theStaticRowidNullBinder = OraclePreparedStatementReadOnly.theStaticRowidNullBinder;
        theStaticIntervalDSCopyingBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSCopyingBinder;
        theStaticIntervalDSBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSBinder;
        theStaticIntervalDSNullBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSNullBinder;
        theStaticIntervalYMCopyingBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMCopyingBinder;
        theStaticIntervalYMBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMBinder;
        theStaticIntervalYMNullBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMNullBinder;
        theStaticBfileCopyingBinder = OraclePreparedStatementReadOnly.theStaticBfileCopyingBinder;
        theStaticBfileBinder = OraclePreparedStatementReadOnly.theStaticBfileBinder;
        theStaticBfileNullBinder = OraclePreparedStatementReadOnly.theStaticBfileNullBinder;
        theStaticBlobCopyingBinder = OraclePreparedStatementReadOnly.theStaticBlobCopyingBinder;
        theStaticBlobBinder = OraclePreparedStatementReadOnly.theStaticBlobBinder;
        theStaticBlobNullBinder = OraclePreparedStatementReadOnly.theStaticBlobNullBinder;
        theStaticClobCopyingBinder = OraclePreparedStatementReadOnly.theStaticClobCopyingBinder;
        theStaticClobBinder = OraclePreparedStatementReadOnly.theStaticClobBinder;
        theStaticClobNullBinder = OraclePreparedStatementReadOnly.theStaticClobNullBinder;
        theStaticRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticRawCopyingBinder;
        theStaticRawBinder = OraclePreparedStatementReadOnly.theStaticRawBinder;
        theStaticRawNullBinder = OraclePreparedStatementReadOnly.theStaticRawNullBinder;
        theStaticPlsqlRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlRawCopyingBinder;
        theStaticPlsqlRawBinder = OraclePreparedStatementReadOnly.theStaticPlsqlRawBinder;
        theStaticBinaryFloatCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatCopyingBinder;
        theStaticBinaryFloatBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatBinder;
        theStaticBinaryFloatNullBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatNullBinder;
        theStaticBINARY_FLOATCopyingBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATCopyingBinder;
        theStaticBINARY_FLOATBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATBinder;
        theStaticBINARY_FLOATNullBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATNullBinder;
        theStaticBinaryDoubleCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleCopyingBinder;
        theStaticBinaryDoubleBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleBinder;
        theStaticBinaryDoubleNullBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleNullBinder;
        theStaticBINARY_DOUBLECopyingBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLECopyingBinder;
        theStaticBINARY_DOUBLEBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLEBinder;
        theStaticBINARY_DOUBLENullBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLENullBinder;
        theStaticLongStreamBinder = OraclePreparedStatementReadOnly.theStaticLongStreamBinder;
        theStaticLongRawStreamBinder = OraclePreparedStatementReadOnly.theStaticLongRawStreamBinder;
        theStaticNamedTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeCopyingBinder;
        theStaticNamedTypeBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeBinder;
        theStaticNamedTypeNullBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeNullBinder;
        theStaticRefTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticRefTypeCopyingBinder;
        theStaticRefTypeBinder = OraclePreparedStatementReadOnly.theStaticRefTypeBinder;
        theStaticRefTypeNullBinder = OraclePreparedStatementReadOnly.theStaticRefTypeNullBinder;
        theStaticPlsqlIbtCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtCopyingBinder;
        theStaticPlsqlIbtBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtBinder;
        theStaticPlsqlIbtNullBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtNullBinder;
        theStaticOutBinder = OraclePreparedStatementReadOnly.theStaticOutBinder;
        theStaticReturnParamBinder = OraclePreparedStatementReadOnly.theStaticReturnParamBinder;
        theStaticT4CRowidBinder = OraclePreparedStatementReadOnly.theStaticT4CRowidBinder;
        theStaticT4CRowidNullBinder = OraclePreparedStatementReadOnly.theStaticT4CRowidNullBinder;
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OraclePreparedStatement"));
        } catch (Exception e) {
        }
    }

    class PushedBatch {
        int[] currentBatchCharLens;
        int[] lastBoundCharLens;
        Accessor[] currentBatchBindAccessors;
        boolean lastBoundNeeded;
        boolean need_to_parse;
        boolean current_batch_need_to_prepare_binds;
        int first_row_in_batch;
        int number_of_rows_to_be_bound;
        PushedBatch next;

        PushedBatch() {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OraclePreparedStatement JD-Core Version: 0.6.0
 */