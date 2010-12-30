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
import oracle.jdbc.internal.ObjectData;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCOLLECTION;
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

public abstract class OraclePreparedStatement extends OracleStatement
  implements oracle.jdbc.internal.OraclePreparedStatement, ScrollRsetStatement
{
  int numberOfBindRowsAllocated;
  static Binder theStaticVarnumCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarnumCopyingBinder;

  static Binder theStaticVarnumNullBinder = OraclePreparedStatementReadOnly.theStaticVarnumNullBinder;

  Binder theVarnumNullBinder = theStaticVarnumNullBinder;

  static Binder theStaticBooleanBinder = OraclePreparedStatementReadOnly.theStaticBooleanBinder;

  Binder theBooleanBinder = theStaticBooleanBinder;

  static Binder theStaticByteBinder = OraclePreparedStatementReadOnly.theStaticByteBinder;

  Binder theByteBinder = theStaticByteBinder;

  static Binder theStaticShortBinder = OraclePreparedStatementReadOnly.theStaticShortBinder;

  Binder theShortBinder = theStaticShortBinder;

  static Binder theStaticIntBinder = OraclePreparedStatementReadOnly.theStaticIntBinder;

  Binder theIntBinder = theStaticIntBinder;

  static Binder theStaticLongBinder = OraclePreparedStatementReadOnly.theStaticLongBinder;

  Binder theLongBinder = theStaticLongBinder;

  static Binder theStaticFloatBinder = OraclePreparedStatementReadOnly.theStaticFloatBinder;

  Binder theFloatBinder = null;

  static Binder theStaticDoubleBinder = OraclePreparedStatementReadOnly.theStaticDoubleBinder;

  Binder theDoubleBinder = null;

  static Binder theStaticBigDecimalBinder = OraclePreparedStatementReadOnly.theStaticBigDecimalBinder;

  Binder theBigDecimalBinder = theStaticBigDecimalBinder;

  static Binder theStaticVarcharCopyingBinder = OraclePreparedStatementReadOnly.theStaticVarcharCopyingBinder;

  static Binder theStaticVarcharNullBinder = OraclePreparedStatementReadOnly.theStaticVarcharNullBinder;

  Binder theVarcharNullBinder = theStaticVarcharNullBinder;

  static Binder theStaticStringBinder = OraclePreparedStatementReadOnly.theStaticStringBinder;

  Binder theStringBinder = theStaticStringBinder;

  static Binder theStaticSetCHARCopyingBinder = OraclePreparedStatementReadOnly.theStaticSetCHARCopyingBinder;

  static Binder theStaticSetCHARBinder = OraclePreparedStatementReadOnly.theStaticSetCHARBinder;

  static Binder theStaticLittleEndianSetCHARBinder = OraclePreparedStatementReadOnly.theStaticLittleEndianSetCHARBinder;

  static Binder theStaticSetCHARNullBinder = OraclePreparedStatementReadOnly.theStaticSetCHARNullBinder;
  Binder theSetCHARBinder;
  Binder theSetCHARNullBinder = theStaticSetCHARNullBinder;

  static Binder theStaticFixedCHARCopyingBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARCopyingBinder;

  static Binder theStaticFixedCHARBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARBinder;

  static Binder theStaticFixedCHARNullBinder = OraclePreparedStatementReadOnly.theStaticFixedCHARNullBinder;

  Binder theFixedCHARBinder = theStaticFixedCHARBinder;
  Binder theFixedCHARNullBinder = theStaticFixedCHARNullBinder;

  static Binder theStaticDateCopyingBinder = OraclePreparedStatementReadOnly.theStaticDateCopyingBinder;

  static Binder theStaticDateBinder = OraclePreparedStatementReadOnly.theStaticDateBinder;

  static Binder theStaticDateNullBinder = OraclePreparedStatementReadOnly.theStaticDateNullBinder;

  Binder theDateBinder = theStaticDateBinder;
  Binder theDateNullBinder = theStaticDateNullBinder;

  static Binder theStaticTimeCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimeCopyingBinder;

  static Binder theStaticTimeBinder = OraclePreparedStatementReadOnly.theStaticTimeBinder;

  Binder theTimeBinder = theStaticTimeBinder;

  static Binder theStaticTimestampCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimestampCopyingBinder;

  static Binder theStaticTimestampBinder = OraclePreparedStatementReadOnly.theStaticTimestampBinder;

  static Binder theStaticTimestampNullBinder = OraclePreparedStatementReadOnly.theStaticTimestampNullBinder;

  Binder theTimestampBinder = theStaticTimestampBinder;
  Binder theTimestampNullBinder = theStaticTimestampNullBinder;

  static Binder theStaticOracleNumberBinder = OraclePreparedStatementReadOnly.theStaticOracleNumberBinder;

  Binder theOracleNumberBinder = theStaticOracleNumberBinder;

  static Binder theStaticOracleDateBinder = OraclePreparedStatementReadOnly.theStaticOracleDateBinder;

  Binder theOracleDateBinder = theStaticOracleDateBinder;

  static Binder theStaticOracleTimestampBinder = OraclePreparedStatementReadOnly.theStaticOracleTimestampBinder;

  Binder theOracleTimestampBinder = theStaticOracleTimestampBinder;

  static Binder theStaticTSTZCopyingBinder = OraclePreparedStatementReadOnly.theStaticTSTZCopyingBinder;

  static Binder theStaticTSTZBinder = OraclePreparedStatementReadOnly.theStaticTSTZBinder;

  static Binder theStaticTSTZNullBinder = OraclePreparedStatementReadOnly.theStaticTSTZNullBinder;

  Binder theTSTZBinder = theStaticTSTZBinder;
  Binder theTSTZNullBinder = theStaticTSTZNullBinder;

  static Binder theStaticTSLTZCopyingBinder = OraclePreparedStatementReadOnly.theStaticTSLTZCopyingBinder;

  static Binder theStaticTSLTZBinder = OraclePreparedStatementReadOnly.theStaticTSLTZBinder;

  static Binder theStaticTSLTZNullBinder = OraclePreparedStatementReadOnly.theStaticTSLTZNullBinder;

  Binder theTSLTZBinder = theStaticTSLTZBinder;
  Binder theTSLTZNullBinder = theStaticTSLTZNullBinder;

  static Binder theStaticRowidCopyingBinder = OraclePreparedStatementReadOnly.theStaticRowidCopyingBinder;

  static Binder theStaticRowidBinder = OraclePreparedStatementReadOnly.theStaticRowidBinder;

  static Binder theStaticLittleEndianRowidBinder = OraclePreparedStatementReadOnly.theStaticLittleEndianRowidBinder;

  static Binder theStaticRowidNullBinder = OraclePreparedStatementReadOnly.theStaticRowidNullBinder;
  Binder theRowidBinder;
  Binder theRowidNullBinder = theStaticRowidNullBinder;

  static Binder theStaticIntervalDSCopyingBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSCopyingBinder;

  static Binder theStaticIntervalDSBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSBinder;

  static Binder theStaticIntervalDSNullBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSNullBinder;

  Binder theIntervalDSBinder = theStaticIntervalDSBinder;
  Binder theIntervalDSNullBinder = theStaticIntervalDSNullBinder;

  static Binder theStaticIntervalYMCopyingBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMCopyingBinder;

  static Binder theStaticIntervalYMBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMBinder;

  static Binder theStaticIntervalYMNullBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMNullBinder;

  Binder theIntervalYMBinder = theStaticIntervalYMBinder;
  Binder theIntervalYMNullBinder = theStaticIntervalYMNullBinder;

  static Binder theStaticBfileCopyingBinder = OraclePreparedStatementReadOnly.theStaticBfileCopyingBinder;

  static Binder theStaticBfileBinder = OraclePreparedStatementReadOnly.theStaticBfileBinder;

  static Binder theStaticBfileNullBinder = OraclePreparedStatementReadOnly.theStaticBfileNullBinder;

  Binder theBfileBinder = theStaticBfileBinder;
  Binder theBfileNullBinder = theStaticBfileNullBinder;

  static Binder theStaticBlobCopyingBinder = OraclePreparedStatementReadOnly.theStaticBlobCopyingBinder;

  static Binder theStaticBlobBinder = OraclePreparedStatementReadOnly.theStaticBlobBinder;

  static Binder theStaticBlobNullBinder = OraclePreparedStatementReadOnly.theStaticBlobNullBinder;

  Binder theBlobBinder = theStaticBlobBinder;
  Binder theBlobNullBinder = theStaticBlobNullBinder;

  static Binder theStaticClobCopyingBinder = OraclePreparedStatementReadOnly.theStaticClobCopyingBinder;

  static Binder theStaticClobBinder = OraclePreparedStatementReadOnly.theStaticClobBinder;

  static Binder theStaticClobNullBinder = OraclePreparedStatementReadOnly.theStaticClobNullBinder;

  Binder theClobBinder = theStaticClobBinder;
  Binder theClobNullBinder = theStaticClobNullBinder;

  static Binder theStaticRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticRawCopyingBinder;

  static Binder theStaticRawBinder = OraclePreparedStatementReadOnly.theStaticRawBinder;

  static Binder theStaticRawNullBinder = OraclePreparedStatementReadOnly.theStaticRawNullBinder;

  Binder theRawBinder = theStaticRawBinder;
  Binder theRawNullBinder = theStaticRawNullBinder;

  static Binder theStaticPlsqlRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlRawCopyingBinder;

  static Binder theStaticPlsqlRawBinder = OraclePreparedStatementReadOnly.theStaticPlsqlRawBinder;

  Binder thePlsqlRawBinder = theStaticPlsqlRawBinder;

  static Binder theStaticBinaryFloatCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatCopyingBinder;

  static Binder theStaticBinaryFloatBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatBinder;

  static Binder theStaticBinaryFloatNullBinder = OraclePreparedStatementReadOnly.theStaticBinaryFloatNullBinder;

  Binder theBinaryFloatBinder = theStaticBinaryFloatBinder;
  Binder theBinaryFloatNullBinder = theStaticBinaryFloatNullBinder;

  static Binder theStaticBINARY_FLOATCopyingBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATCopyingBinder;

  static Binder theStaticBINARY_FLOATBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATBinder;

  static Binder theStaticBINARY_FLOATNullBinder = OraclePreparedStatementReadOnly.theStaticBINARY_FLOATNullBinder;

  Binder theBINARY_FLOATBinder = theStaticBINARY_FLOATBinder;
  Binder theBINARY_FLOATNullBinder = theStaticBINARY_FLOATNullBinder;

  static Binder theStaticBinaryDoubleCopyingBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleCopyingBinder;

  static Binder theStaticBinaryDoubleBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleBinder;

  static Binder theStaticBinaryDoubleNullBinder = OraclePreparedStatementReadOnly.theStaticBinaryDoubleNullBinder;

  Binder theBinaryDoubleBinder = theStaticBinaryDoubleBinder;
  Binder theBinaryDoubleNullBinder = theStaticBinaryDoubleNullBinder;

  static Binder theStaticBINARY_DOUBLECopyingBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLECopyingBinder;

  static Binder theStaticBINARY_DOUBLEBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLEBinder;

  static Binder theStaticBINARY_DOUBLENullBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLENullBinder;

  Binder theBINARY_DOUBLEBinder = theStaticBINARY_DOUBLEBinder;
  Binder theBINARY_DOUBLENullBinder = theStaticBINARY_DOUBLENullBinder;

  static Binder theStaticLongStreamBinder = OraclePreparedStatementReadOnly.theStaticLongStreamBinder;

  Binder theLongStreamBinder = theStaticLongStreamBinder;

  static Binder theStaticLongRawStreamBinder = OraclePreparedStatementReadOnly.theStaticLongRawStreamBinder;

  Binder theLongRawStreamBinder = theStaticLongRawStreamBinder;

  static Binder theStaticNamedTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeCopyingBinder;

  static Binder theStaticNamedTypeBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeBinder;

  static Binder theStaticNamedTypeNullBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeNullBinder;

  Binder theNamedTypeBinder = theStaticNamedTypeBinder;
  Binder theNamedTypeNullBinder = theStaticNamedTypeNullBinder;

  static Binder theStaticRefTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticRefTypeCopyingBinder;

  static Binder theStaticRefTypeBinder = OraclePreparedStatementReadOnly.theStaticRefTypeBinder;

  static Binder theStaticRefTypeNullBinder = OraclePreparedStatementReadOnly.theStaticRefTypeNullBinder;

  Binder theRefTypeBinder = theStaticRefTypeBinder;
  Binder theRefTypeNullBinder = theStaticRefTypeNullBinder;

  static Binder theStaticPlsqlIbtCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtCopyingBinder;

  static Binder theStaticPlsqlIbtBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtBinder;

  static Binder theStaticPlsqlIbtNullBinder = OraclePreparedStatementReadOnly.theStaticPlsqlIbtNullBinder;

  Binder thePlsqlIbtBinder = theStaticPlsqlIbtBinder;
  Binder thePlsqlNullBinder = theStaticPlsqlIbtNullBinder;

  static Binder theStaticOutBinder = OraclePreparedStatementReadOnly.theStaticOutBinder;

  Binder theOutBinder = theStaticOutBinder;

  static Binder theStaticReturnParamBinder = OraclePreparedStatementReadOnly.theStaticReturnParamBinder;

  Binder theReturnParamBinder = theStaticReturnParamBinder;

  static Binder theStaticT4CRowidBinder = OraclePreparedStatementReadOnly.theStaticT4CRowidBinder;

  static Binder theStaticT4CRowidNullBinder = OraclePreparedStatementReadOnly.theStaticT4CRowidNullBinder;
  public static final int TypeBinder_BYTELEN = 24;
  char[] digits = new char[20];
  Binder[][] binders;
  int[][] parameterInt;
  long[][] parameterLong;
  float[][] parameterFloat;
  double[][] parameterDouble;
  BigDecimal[][] parameterBigDecimal;
  String[][] parameterString;
  Date[][] parameterDate;
  Time[][] parameterTime;
  Timestamp[][] parameterTimestamp;
  byte[][][] parameterDatum;
  OracleTypeADT[][] parameterOtype;
  PlsqlIbtBindInfo[][] parameterPlsqlIbt;
  Binder[] currentRowBinders;
  int[] currentRowCharLens;
  Accessor[] currentRowBindAccessors;
  short[] currentRowFormOfUse;
  boolean currentRowNeedToPrepareBinds = true;
  int[] currentBatchCharLens;
  Accessor[] currentBatchBindAccessors;
  short[] currentBatchFormOfUse;
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
  Binder[] lastBinders;
  byte[] lastBoundBytes;
  int lastBoundByteOffset;
  char[] lastBoundChars;
  int lastBoundCharOffset;
  int[] lastBoundByteOffsets;
  int[] lastBoundCharOffsets;
  int[] lastBoundByteLens;
  int[] lastBoundCharLens;
  short[] lastBoundInds;
  short[] lastBoundLens;
  boolean lastBoundNeeded = false;
  byte[][] lastBoundTypeBytes;
  OracleTypeADT[] lastBoundTypeOtypes;
  private static final int STREAM_MAX_BYTES_SQL = 2147483647;
  int maxRawBytesSql;
  int maxRawBytesPlsql;
  int maxVcsCharsSql;
  int maxVcsBytesPlsql;
  private int maxCharSize = 0;

  private int maxNCharSize = 0;

  private int charMaxCharsSql = 0;

  private int charMaxNCharsSql = 0;

  private int maxVcsCharsPlsql = 0;

  private int maxVcsNCharsPlsql = 0;

  private int maxStreamCharsSql = 0;

  private int maxStreamNCharsSql = 0;

  private boolean isServerCharSetFixedWidth = false;

  private boolean isServerNCharSetFixedWidth = false;
  int minVcsBindSize;
  int prematureBatchCount;
  boolean checkBindTypes = true;
  boolean scrollRsetTypeSolved;
  int SetBigStringTryClob = 0;
  static final int BSTYLE_UNKNOWN = 0;
  static final int BSTYLE_ORACLE = 1;
  static final int BSTYLE_JDBC = 2;
  int m_batchStyle = 0;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  OraclePreparedStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    this(paramPhysicalConnection, paramString, paramInt1, paramInt2, 1003, 1007);
  }

  OraclePreparedStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    super(paramPhysicalConnection, paramInt1, paramInt2, paramInt3, paramInt4);

    this.theSetCHARBinder = (paramPhysicalConnection.useLittleEndianSetCHARBinder() ? theStaticLittleEndianSetCHARBinder : theStaticSetCHARBinder);

    this.theRowidBinder = (paramPhysicalConnection.useLittleEndianSetCHARBinder() ? theStaticLittleEndianRowidBinder : theStaticRowidBinder);

    this.statementType = 1;
    this.currentRow = -1;
    this.needToParse = true;

    this.processEscapes = paramPhysicalConnection.processEscapes;
    this.sqlObject.initialize(paramString);

    this.sqlKind = this.sqlObject.getSqlKind();

    this.clearParameters = true;
    this.scrollRsetTypeSolved = false;
    this.prematureBatchCount = 0;

    initializeBinds();

    this.minVcsBindSize = paramPhysicalConnection.minVcsBindSize;
    this.maxRawBytesSql = paramPhysicalConnection.maxRawBytesSql;
    this.maxRawBytesPlsql = paramPhysicalConnection.maxRawBytesPlsql;
    this.maxVcsCharsSql = paramPhysicalConnection.maxVcsCharsSql;
    this.maxVcsBytesPlsql = paramPhysicalConnection.maxVcsBytesPlsql;

    this.maxCharSize = this.connection.conversion.sMaxCharSize;
    this.maxNCharSize = this.connection.conversion.maxNCharSize;
    this.maxVcsCharsPlsql = (this.maxVcsBytesPlsql / this.maxCharSize);
    this.maxVcsNCharsPlsql = (this.maxVcsBytesPlsql / this.maxNCharSize);
    this.maxStreamCharsSql = (2147483647 / this.maxCharSize);
    this.maxStreamNCharsSql = (this.maxRawBytesSql / this.maxNCharSize);
    this.isServerCharSetFixedWidth = this.connection.conversion.isServerCharSetFixedWidth;
    this.isServerNCharSetFixedWidth = this.connection.conversion.isServerNCharSetFixedWidth;
  }

  void allocBinds(int paramInt)
    throws SQLException
  {
    int i = paramInt > this.numberOfBindRowsAllocated ? 1 : 0;

    initializeIndicatorSubRange();

    int j = this.bindIndicatorSubRange + 3 + this.numberOfBindPositions * 10;

    int k = paramInt * this.numberOfBindPositions;

    int m = j + 2 * k;

    if (m > this.totalBindIndicatorLength)
    {
      short[] arrayOfShort = this.bindIndicators;
      i1 = this.bindIndicatorOffset;

      this.bindIndicatorOffset = 0;
      this.bindIndicators = new short[m];
      this.totalBindIndicatorLength = m;

      if ((arrayOfShort != null) && (i != 0))
      {
        System.arraycopy(arrayOfShort, i1, this.bindIndicators, this.bindIndicatorOffset, j);
      }

    }

    this.bindIndicatorSubRange += this.bindIndicatorOffset;

    this.bindIndicators[(this.bindIndicatorSubRange + 0)] = (short)this.numberOfBindPositions;

    this.indicatorsOffset = (this.bindIndicatorOffset + j);
    this.valueLengthsOffset = (this.indicatorsOffset + k);

    int n = this.indicatorsOffset;
    int i1 = this.valueLengthsOffset;
    int i2 = this.bindIndicatorSubRange + 3;

    for (int i3 = 0; i3 < this.numberOfBindPositions; i3++)
    {
      this.bindIndicators[(i2 + 5)] = (short)(n >> 16);

      this.bindIndicators[(i2 + 6)] = (short)(n & 0xFFFF);

      this.bindIndicators[(i2 + 7)] = (short)(i1 >> 16);

      this.bindIndicators[(i2 + 8)] = (short)(i1 & 0xFFFF);

      n += paramInt;
      i1 += paramInt;
      i2 += 10;
    }
  }

  void initializeBinds()
    throws SQLException
  {
    this.numberOfBindPositions = this.sqlObject.getParameterCount();

    if (this.numberOfBindPositions == 0)
    {
      this.currentRowNeedToPrepareBinds = false;

      return;
    }

    this.numberOfBindRowsAllocated = this.batch;

    this.binders = new Binder[this.numberOfBindRowsAllocated][this.numberOfBindPositions];

    this.currentRowBinders = this.binders[0];

    this.currentRowCharLens = new int[this.numberOfBindPositions];
    this.currentBatchCharLens = new int[this.numberOfBindPositions];

    this.currentRowFormOfUse = new short[this.numberOfBindPositions];
    this.currentBatchFormOfUse = new short[this.numberOfBindPositions];

    int i = 1;

    if (this.connection.defaultNChar) {
      i = 2;
    }
    for (int j = 0; j < this.numberOfBindPositions; j++)
    {
      this.currentRowFormOfUse[j] = i;
      this.currentBatchFormOfUse[j] = i;
    }

    this.lastBinders = new Binder[this.numberOfBindPositions];
    this.lastBoundCharLens = new int[this.numberOfBindPositions];
    this.lastBoundByteOffsets = new int[this.numberOfBindPositions];
    this.lastBoundCharOffsets = new int[this.numberOfBindPositions];
    this.lastBoundByteLens = new int[this.numberOfBindPositions];
    this.lastBoundInds = new short[this.numberOfBindPositions];
    this.lastBoundLens = new short[this.numberOfBindPositions];

    this.lastBoundTypeBytes = new byte[this.numberOfBindPositions][];
    this.lastBoundTypeOtypes = new OracleTypeADT[this.numberOfBindPositions];

    allocBinds(this.numberOfBindRowsAllocated);
  }

  void growBinds(int paramInt)
    throws SQLException
  {
    Binder[][] arrayOfBinder = this.binders;

    this.binders = new Binder[paramInt][];

    if (arrayOfBinder != null) {
      System.arraycopy(arrayOfBinder, 0, this.binders, 0, this.numberOfBindRowsAllocated);
    }

    for (int i = this.numberOfBindRowsAllocated; i < paramInt; )
    {
      this.binders[i] = new Binder[this.numberOfBindPositions];

      i++;
    }

    allocBinds(paramInt);
    Object localObject;
    if (this.parameterInt != null)
    {
      localObject = this.parameterInt;

      this.parameterInt = new int[paramInt][];

      System.arraycopy(localObject, 0, this.parameterInt, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterInt[i] = new int[this.numberOfBindPositions];
      }
    }
    if (this.parameterLong != null)
    {
      localObject = this.parameterLong;

      this.parameterLong = new long[paramInt][];

      System.arraycopy(localObject, 0, this.parameterLong, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterLong[i] = new long[this.numberOfBindPositions];
      }
    }
    if (this.parameterFloat != null)
    {
      localObject = this.parameterFloat;

      this.parameterFloat = new float[paramInt][];

      System.arraycopy(localObject, 0, this.parameterFloat, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterFloat[i] = new float[this.numberOfBindPositions];
      }
    }
    if (this.parameterDouble != null)
    {
      localObject = this.parameterDouble;

      this.parameterDouble = new double[paramInt][];

      System.arraycopy(localObject, 0, this.parameterDouble, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterDouble[i] = new double[this.numberOfBindPositions];
      }
    }
    if (this.parameterBigDecimal != null)
    {
      localObject = this.parameterBigDecimal;

      this.parameterBigDecimal = new BigDecimal[paramInt][];

      System.arraycopy(localObject, 0, this.parameterBigDecimal, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterBigDecimal[i] = new BigDecimal[this.numberOfBindPositions];
      }
    }
    if (this.parameterString != null)
    {
      localObject = this.parameterString;

      this.parameterString = new String[paramInt][];

      System.arraycopy(localObject, 0, this.parameterString, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterString[i] = new String[this.numberOfBindPositions];
      }
    }
    if (this.parameterDate != null)
    {
      localObject = this.parameterDate;

      this.parameterDate = new Date[paramInt][];

      System.arraycopy(localObject, 0, this.parameterDate, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterDate[i] = new Date[this.numberOfBindPositions];
      }
    }
    if (this.parameterTime != null)
    {
      localObject = this.parameterTime;

      this.parameterTime = new Time[paramInt][];

      System.arraycopy(localObject, 0, this.parameterTime, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterTime[i] = new Time[this.numberOfBindPositions];
      }
    }
    if (this.parameterTimestamp != null)
    {
      localObject = this.parameterTimestamp;

      this.parameterTimestamp = new Timestamp[paramInt][];

      System.arraycopy(localObject, 0, this.parameterTimestamp, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterTimestamp[i] = new Timestamp[this.numberOfBindPositions];
      }
    }
    if (this.parameterDatum != null)
    {
      localObject = this.parameterDatum;

      this.parameterDatum = new byte[paramInt][][];

      System.arraycopy(localObject, 0, this.parameterDatum, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterDatum[i] = new byte[this.numberOfBindPositions][];
      }
    }
    if (this.parameterOtype != null)
    {
      localObject = this.parameterOtype;

      this.parameterOtype = new OracleTypeADT[paramInt][];

      System.arraycopy(localObject, 0, this.parameterOtype, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterOtype[i] = new OracleTypeADT[this.numberOfBindPositions];
      }
    }
    if (this.parameterStream != null)
    {
      localObject = this.parameterStream;

      this.parameterStream = new InputStream[paramInt][];

      System.arraycopy(localObject, 0, this.parameterStream, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterStream[i] = new InputStream[this.numberOfBindPositions];
      }
    }
    if (this.parameterPlsqlIbt != null)
    {
      localObject = this.parameterPlsqlIbt;

      this.parameterPlsqlIbt = new PlsqlIbtBindInfo[paramInt][];

      System.arraycopy(localObject, 0, this.parameterPlsqlIbt, 0, this.numberOfBindRowsAllocated);

      i = this.numberOfBindRowsAllocated;
      for (; i < paramInt; i++) {
        this.parameterPlsqlIbt[i] = new PlsqlIbtBindInfo[this.numberOfBindPositions];
      }
    }

    this.numberOfBindRowsAllocated = paramInt;

    this.currentRowNeedToPrepareBinds = true;
  }

  void processCompletedBindRow(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    if (this.numberOfBindPositions == 0)
    {
      return;
    }

    int j = 0;
    int k = 0;
    int m = this.currentRank == this.firstRowInBatch ? 1 : 0;

    Binder[] arrayOfBinder = this.currentRank == 0 ? this.lastBinders : this.lastBinders[0] == null ? null : this.binders[(this.currentRank - 1)];
    Object localObject1;
    Object localObject2;
    if (this.currentRowBindAccessors == null)
    {
      if (arrayOfBinder == null)
      {
        for (i = 0; i < this.numberOfBindPositions; i++) {
          if (this.currentRowBinders[i] == null) {
            DatabaseError.throwSqlException(41, new Integer(i + 1));
          }
        }
      }
      if (this.checkBindTypes)
      {
        localObject1 = this.parameterOtype == null ? null : this.currentRank == 0 ? this.lastBoundTypeOtypes : this.parameterOtype[(this.currentRank - 1)];

        for (i = 0; i < this.numberOfBindPositions; i++)
        {
          localObject2 = this.currentRowBinders[i];

          if (localObject2 == null)
          {
            if (this.clearParameters) {
              DatabaseError.throwSqlException(41, new Integer(i + 1));
            }

            this.currentRowBinders[i] = arrayOfBinder[i].copyingBinder();

            this.currentRowCharLens[i] = -1;

            if (m != 0)
            {
              this.lastBoundNeeded = true;
            }
          }
          else {
            int i3 = ((Binder)localObject2).type;

            if ((i3 == arrayOfBinder[i].type) && (((i3 != 109) && (i3 != 111)) || (this.parameterOtype[this.currentRank][i].isInHierarchyOf(localObject1[i])))) { if (i3 == 9)
              {
                if ((((Binder)localObject2).bytelen == 0 ? 1 : 0) == (arrayOfBinder[i].bytelen == 0 ? 1 : 0));
              }

            }
            else
            {
              j = 1;
            }
          }
          if (this.currentBatchFormOfUse[i] == this.currentRowFormOfUse[i])
          {
            continue;
          }
          j = 1;
        }

      }

      for (i = 0; i < this.numberOfBindPositions; i++)
      {
        localObject1 = this.currentRowBinders[i];

        if (localObject1 != null)
        {
          continue;
        }

        if (this.clearParameters) {
          DatabaseError.throwSqlException(41, new Integer(i + 1));
        }

        this.currentRowBinders[i] = arrayOfBinder[i].copyingBinder();

        this.currentRowCharLens[i] = -1;

        if (m == 0)
        {
          continue;
        }

        this.lastBoundNeeded = true;
      }

    }

    if (arrayOfBinder == null)
    {
      for (i = 0; i < this.numberOfBindPositions; i++)
      {
        localObject1 = this.currentRowBinders[i];
        localObject2 = this.currentRowBindAccessors[i];

        if (localObject1 == null)
        {
          if (localObject2 == null)
          {
            DatabaseError.throwSqlException(41, new Integer(i + 1));
          }
          else
          {
            this.currentRowBinders[i] = this.theOutBinder;
          }
        } else {
          if ((localObject2 == null) || (((Accessor)localObject2).defineType == ((Binder)localObject1).type))
          {
            continue;
          }

          if ((this.connection.looseTimestampDateCheck) && (((Binder)localObject1).type == 180) && (((Accessor)localObject2).defineType == 12))
          {
            continue;
          }
          k = 1;
        }
      }
    }

    if (this.checkBindTypes)
    {
      localObject1 = this.parameterOtype == null ? null : this.currentRank == 0 ? this.lastBoundTypeOtypes : this.parameterOtype[(this.currentRank - 1)];

      for (i = 0; i < this.numberOfBindPositions; i++)
      {
        localObject2 = this.currentRowBinders[i];
        Object localObject3 = this.currentRowBindAccessors[i];

        if (localObject2 == null)
        {
          if ((this.clearParameters) && (arrayOfBinder[i] != this.theOutBinder))
          {
            DatabaseError.throwSqlException(41, new Integer(i + 1));
          }

          localObject2 = arrayOfBinder[i];
          this.currentRowBinders[i] = localObject2;
          this.currentRowCharLens[i] = -1;

          if ((m != 0) && (localObject2 != this.theOutBinder))
          {
            this.lastBoundNeeded = true;
          }
        }
        else {
          int i4 = ((Binder)localObject2).type;

          if ((i4 == arrayOfBinder[i].type) && (((i4 != 109) && (i4 != 111)) || (this.parameterOtype[this.currentRank][i].isInHierarchyOf(localObject1[i])))) { if (i4 == 9)
            {
              if ((((Binder)localObject2).bytelen == 0 ? 1 : 0) == (arrayOfBinder[i].bytelen == 0 ? 1 : 0));
            }

          }
          else
          {
            j = 1;
          }
        }
        if (this.currentBatchFormOfUse[i] != this.currentRowFormOfUse[i])
        {
          j = 1;
        }
        Accessor localAccessor = this.currentBatchBindAccessors[i];

        if (localObject3 == null)
        {
          localObject3 = localAccessor;
          this.currentRowBindAccessors[i] = localObject3;
        }
        else if ((localAccessor != null) && (((Accessor)localObject3).defineType != localAccessor.defineType))
        {
          j = 1;
        }
        if ((localObject3 == null) || (localObject2 == this.theOutBinder) || (((Accessor)localObject3).defineType == ((Binder)localObject2).type))
        {
          continue;
        }

        if ((this.connection.looseTimestampDateCheck) && (((Binder)localObject2).type == 180) && (((Accessor)localObject3).defineType == 12))
        {
          continue;
        }
        k = 1;
      }

    }

    for (int i = 0; i < this.numberOfBindPositions; i++)
    {
      localObject1 = this.currentRowBinders[i];

      if (localObject1 == null)
      {
        if ((this.clearParameters) && (arrayOfBinder[i] != this.theOutBinder)) {
          DatabaseError.throwSqlException(41, new Integer(i + 1));
        }

        localObject1 = arrayOfBinder[i];
        this.currentRowBinders[i] = localObject1;
        this.currentRowCharLens[i] = -1;

        if ((m != 0) && (localObject1 != this.theOutBinder))
        {
          this.lastBoundNeeded = true;
        }
      }
      if (this.currentRowBindAccessors[i] != null)
      {
        continue;
      }

      this.currentRowBindAccessors[i] = this.currentBatchBindAccessors[i];
    }
    int n;
    if (j != 0)
    {
      if (m == 0)
      {
        if (this.m_batchStyle == 2)
        {
          pushBatch(false);
        }
        else
        {
          n = this.validRows;

          this.prematureBatchCount = sendBatch();
          this.validRows = n;
        }

      }

      this.needToParse = true;

      this.currentRowNeedToPrepareBinds = true;
    }
    else if (paramBoolean)
    {
      pushBatch(false);

      this.needToParse = false;

      this.currentBatchNeedToPrepareBinds = false;
    }

    if (k != 0)
    {
      DatabaseError.throwSqlException(12);
    }

    for (i = 0; i < this.numberOfBindPositions; i++)
    {
      n = this.currentRowCharLens[i];

      if (n == -1) {
        n = this.lastBoundCharLens[i];
      }
      if (this.currentBatchCharLens[i] < n) {
        this.currentBatchCharLens[i] = n;
      }
      this.currentRowCharLens[i] = 0;
      this.currentBatchFormOfUse[i] = this.currentRowFormOfUse[i];
    }

    if (this.currentRowNeedToPrepareBinds) {
      this.currentBatchNeedToPrepareBinds = true;
    }

    if (this.currentRowBindAccessors != null)
    {
      Accessor[] arrayOfAccessor = this.currentBatchBindAccessors;

      this.currentBatchBindAccessors = this.currentRowBindAccessors;

      if (arrayOfAccessor == null)
        arrayOfAccessor = new Accessor[this.numberOfBindPositions];
      else {
        for (i = 0; i < this.numberOfBindPositions; i++)
          arrayOfAccessor[i] = null;
      }
      this.currentRowBindAccessors = arrayOfAccessor;
    }

    int i1 = this.currentRank + 1;

    if (i1 < paramInt)
    {
      if (i1 >= this.numberOfBindRowsAllocated)
      {
        int i2 = this.numberOfBindRowsAllocated << 1;

        if (i2 <= i1) {
          i2 = i1 + 1;
        }
        growBinds(i2);

        this.currentBatchNeedToPrepareBinds = true;
      }

      this.currentRowBinders = this.binders[i1];
    }
    else
    {
      setupBindBuffers(0, paramInt);

      this.currentRowBinders = this.binders[0];
    }

    this.currentRowNeedToPrepareBinds = false;

    this.clearParameters = false;
  }

  void processPlsqlIndexTabBinds(int paramInt) throws SQLException
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;

    Binder[] arrayOfBinder = this.binders[paramInt];
    PlsqlIbtBindInfo[] arrayOfPlsqlIbtBindInfo = this.parameterPlsqlIbt == null ? null : this.parameterPlsqlIbt[paramInt];
    Accessor localAccessor3;
    for (Accessor localAccessor1 = 0; localAccessor1 < this.numberOfBindPositions; localAccessor1++)
    {
      Binder localBinder1 = arrayOfBinder[localAccessor1];
      localAccessor3 = this.currentBatchBindAccessors == null ? null : this.currentBatchBindAccessors[localAccessor1];

      PlsqlIndexTableAccessor localPlsqlIndexTableAccessor1 = (localAccessor3 == null) || (localAccessor3.defineType != 998) ? null : (PlsqlIndexTableAccessor)localAccessor3;

      if (localBinder1.type == 998)
      {
        PlsqlIbtBindInfo localPlsqlIbtBindInfo1 = arrayOfPlsqlIbtBindInfo[localAccessor1];

        if (localPlsqlIndexTableAccessor1 != null)
        {
          if (localPlsqlIbtBindInfo1.element_internal_type != localPlsqlIndexTableAccessor1.elementInternalType)
          {
            DatabaseError.throwSqlException(12);
          }
          if (localPlsqlIbtBindInfo1.maxLen < localPlsqlIndexTableAccessor1.maxNumberOfElements) {
            localPlsqlIbtBindInfo1.maxLen = localPlsqlIndexTableAccessor1.maxNumberOfElements;
          }
          if (localPlsqlIbtBindInfo1.elemMaxLen < localPlsqlIndexTableAccessor1.elementMaxLen) {
            localPlsqlIbtBindInfo1.elemMaxLen = localPlsqlIndexTableAccessor1.elementMaxLen;
          }
          if (localPlsqlIbtBindInfo1.ibtByteLength > 0) {
            localPlsqlIbtBindInfo1.ibtByteLength = (localPlsqlIbtBindInfo1.elemMaxLen * localPlsqlIbtBindInfo1.maxLen);
          }
          else {
            localPlsqlIbtBindInfo1.ibtCharLength = (localPlsqlIbtBindInfo1.elemMaxLen * localPlsqlIbtBindInfo1.maxLen);
          }
        }

        i++;
        k += localPlsqlIbtBindInfo1.ibtByteLength;
        m += localPlsqlIbtBindInfo1.ibtCharLength;
        j += localPlsqlIbtBindInfo1.maxLen;
      } else {
        if (localPlsqlIndexTableAccessor1 == null)
          continue;
        i++;
        k += localPlsqlIndexTableAccessor1.ibtByteLength;
        m += localPlsqlIndexTableAccessor1.ibtCharLength;
        j += localPlsqlIndexTableAccessor1.maxNumberOfElements;
      }
    }

    if (i == 0) {
      return;
    }

    this.ibtBindIndicatorSize = (6 + i * 8 + j * 2);

    this.ibtBindIndicators = new short[this.ibtBindIndicatorSize];
    this.ibtBindIndicatorOffset = 0;

    if (k > 0)
      this.ibtBindBytes = new byte[k];
    this.ibtBindByteOffset = 0;

    if (m > 0)
      this.ibtBindChars = new char[m];
    this.ibtBindCharOffset = 0;

    localAccessor1 = this.ibtBindByteOffset;
    Accessor localAccessor2 = this.ibtBindCharOffset;

    int n = this.ibtBindIndicatorOffset;
    int i1 = n + 6 + i * 8;

    this.ibtBindIndicators[(n++)] = (short)(i >> 16);
    this.ibtBindIndicators[(n++)] = (short)(i & 0xFFFF);

    this.ibtBindIndicators[(n++)] = (short)(k >> 16);
    this.ibtBindIndicators[(n++)] = (short)(k & 0xFFFF);
    this.ibtBindIndicators[(n++)] = (short)(m >> 16);
    this.ibtBindIndicators[(n++)] = (short)(m & 0xFFFF);

    for (int i2 = 0; i2 < this.numberOfBindPositions; i2++)
    {
      Binder localBinder2 = arrayOfBinder[i2];
      Accessor localAccessor4 = this.currentBatchBindAccessors == null ? null : this.currentBatchBindAccessors[i2];

      PlsqlIndexTableAccessor localPlsqlIndexTableAccessor2 = (localAccessor4 == null) || (localAccessor4.defineType != 998) ? null : (PlsqlIndexTableAccessor)localAccessor4;

      if (localBinder2.type == 998)
      {
        PlsqlIbtBindInfo localPlsqlIbtBindInfo2 = arrayOfPlsqlIbtBindInfo[i2];
        int i4 = localPlsqlIbtBindInfo2.maxLen;

        this.ibtBindIndicators[(n++)] = (short)localPlsqlIbtBindInfo2.element_internal_type;

        this.ibtBindIndicators[(n++)] = (short)localPlsqlIbtBindInfo2.elemMaxLen;
        this.ibtBindIndicators[(n++)] = (short)(i4 >> 16);
        this.ibtBindIndicators[(n++)] = (short)(i4 & 0xFFFF);
        this.ibtBindIndicators[(n++)] = (short)(localPlsqlIbtBindInfo2.curLen >> 16);
        this.ibtBindIndicators[(n++)] = (short)(localPlsqlIbtBindInfo2.curLen & 0xFFFF);

        if (localPlsqlIbtBindInfo2.ibtByteLength > 0)
        {
          localAccessor3 = localAccessor1;
          localAccessor1 += localPlsqlIbtBindInfo2.ibtByteLength;
        }
        else
        {
          localAccessor3 = localAccessor2;
          localAccessor2 += localPlsqlIbtBindInfo2.ibtCharLength;
        }

        this.ibtBindIndicators[(n++)] = (short)(localAccessor3 >> 16);
        this.ibtBindIndicators[(n++)] = (short)(localAccessor3 & 0xFFFF);
        localPlsqlIbtBindInfo2.ibtValueIndex = localAccessor3;

        localPlsqlIbtBindInfo2.ibtIndicatorIndex = i1;
        localPlsqlIbtBindInfo2.ibtLengthIndex = (i1 + i4);

        if (localPlsqlIndexTableAccessor2 != null)
        {
          localPlsqlIndexTableAccessor2.ibtIndicatorIndex = localPlsqlIbtBindInfo2.ibtIndicatorIndex;
          localPlsqlIndexTableAccessor2.ibtLengthIndex = localPlsqlIbtBindInfo2.ibtLengthIndex;
          localPlsqlIndexTableAccessor2.ibtMetaIndex = (n - 8);
          localPlsqlIndexTableAccessor2.ibtValueIndex = localAccessor3;
        }

        i1 += 2 * i4;
      } else {
        if (localPlsqlIndexTableAccessor2 == null) {
          continue;
        }
        int i3 = localPlsqlIndexTableAccessor2.maxNumberOfElements;

        this.ibtBindIndicators[(n++)] = (short)localPlsqlIndexTableAccessor2.elementInternalType;

        this.ibtBindIndicators[(n++)] = (short)localPlsqlIndexTableAccessor2.elementMaxLen;
        this.ibtBindIndicators[(n++)] = (short)(i3 >> 16);
        this.ibtBindIndicators[(n++)] = (short)(i3 & 0xFFFF);
        this.ibtBindIndicators[(n++)] = 0;
        this.ibtBindIndicators[(n++)] = 0;

        if (localPlsqlIndexTableAccessor2.ibtByteLength > 0)
        {
          localAccessor3 = localAccessor1;
          localAccessor1 += localPlsqlIndexTableAccessor2.ibtByteLength;
        }
        else
        {
          localAccessor3 = localAccessor2;
          localAccessor2 += localPlsqlIndexTableAccessor2.ibtCharLength;
        }

        this.ibtBindIndicators[(n++)] = (short)(localAccessor3 >> 16);
        this.ibtBindIndicators[(n++)] = (short)(localAccessor3 & 0xFFFF);
        localPlsqlIndexTableAccessor2.ibtValueIndex = localAccessor3;

        localPlsqlIndexTableAccessor2.ibtIndicatorIndex = i1;
        localPlsqlIndexTableAccessor2.ibtLengthIndex = (i1 + i3);
        localPlsqlIndexTableAccessor2.ibtMetaIndex = (n - 8);

        i1 += 2 * i3;
      }
    }
  }

  void initializeBindSubRanges(int paramInt1, int paramInt2)
  {
    this.bindByteSubRange = 0;
    this.bindCharSubRange = 0;
  }

  void initializeIndicatorSubRange()
  {
    this.bindIndicatorSubRange = 0;
  }

  void prepareBindPreambles(int paramInt1, int paramInt2)
  {
  }

  void setupBindBuffers(int paramInt1, int paramInt2)
    throws SQLException
  {
    try
    {
      if (this.numberOfBindPositions == 0)
      {
        return;
      }

      this.preparedAllBinds = this.currentBatchNeedToPrepareBinds;
      this.preparedCharBinds = false;

      this.currentBatchNeedToPrepareBinds = false;

      this.numberOfBoundRows = paramInt2;
      this.bindIndicators[(this.bindIndicatorSubRange + 2)] = (short)this.numberOfBoundRows;

      int j = this.bindBufferCapacity;

      if (this.numberOfBoundRows > this.bindBufferCapacity)
      {
        j = this.numberOfBoundRows;
        this.preparedAllBinds = true;
      }

      if (this.currentBatchBindAccessors != null)
      {
        if (this.outBindAccessors == null) {
          this.outBindAccessors = new Accessor[this.numberOfBindPositions];
        }
        for (i = 0; i < this.numberOfBindPositions; i++)
        {
          Accessor localAccessor1 = this.currentBatchBindAccessors[i];

          this.outBindAccessors[i] = localAccessor1;

          if (localAccessor1 == null)
            continue;
          m = localAccessor1.charLength;

          if (this.currentBatchCharLens[i] >= m)
          {
            continue;
          }

          this.currentBatchCharLens[i] = m;
        }

      }

      int k = 0;
      int m = 0;

      int n = this.bindIndicatorSubRange + 3;

      int i1 = n;

      if (this.preparedAllBinds)
      {
        this.preparedCharBinds = true;

        Binder[] arrayOfBinder = this.binders[paramInt1];

        for (i = 0; i < this.numberOfBindPositions; i++)
        {
          Binder localBinder = arrayOfBinder[i];

          i6 = this.currentBatchCharLens[i];

          if (localBinder == this.theOutBinder)
          {
            Accessor localAccessor2 = this.currentBatchBindAccessors[i];
            i5 = localAccessor2.byteLength;
            i4 = (short)localAccessor2.defineType;
          }
          else
          {
            i5 = localBinder.bytelen;
            i4 = localBinder.type;
          }

          m += i5;
          k += i6;
          this.bindIndicators[(i1 + 0)] = i4;
          this.bindIndicators[(i1 + 1)] = (short)i5;

          this.bindIndicators[(i1 + 2)] = (short)i6;

          this.bindIndicators[(i1 + 9)] = this.currentBatchFormOfUse[i];

          i1 += 10;
        }
      }
      if (this.preparedCharBinds)
      {
        for (i = 0; i < this.numberOfBindPositions; i++)
        {
          i2 = this.currentBatchCharLens[i];

          k += i2;
          this.bindIndicators[(i1 + 2)] = (short)i2;

          i1 += 10;
        }

      }

      for (int i = 0; i < this.numberOfBindPositions; i++)
      {
        i2 = i1 + 2;
        i3 = this.currentBatchCharLens[i];
        i4 = this.bindIndicators[i2];

        if ((i4 >= i3) && (!this.preparedCharBinds))
        {
          this.currentBatchCharLens[i] = i4;
          k += i4;
        }
        else
        {
          this.bindIndicators[i2] = (short)i3;
          k += i3;
          this.preparedCharBinds = true;
        }

        i1 += 10;
      }

      if (this.preparedCharBinds) {
        initializeBindSubRanges(this.numberOfBoundRows, j);
      }
      if (this.preparedAllBinds)
      {
        i2 = this.bindByteSubRange + m * j;

        if ((this.lastBoundNeeded) || (i2 > this.totalBindByteLength))
        {
          this.bindByteOffset = 0;
          this.bindBytes = new byte[i2];

          this.totalBindByteLength = i2;
        }

        this.bindBufferCapacity = j;

        this.bindIndicators[(this.bindIndicatorSubRange + 1)] = (short)this.bindBufferCapacity;
      }

      if (this.preparedCharBinds)
      {
        i2 = this.bindCharSubRange + k * this.bindBufferCapacity;

        if ((this.lastBoundNeeded) || (i2 > this.totalBindCharLength))
        {
          this.bindCharOffset = 0;
          this.bindChars = new char[i2];

          this.totalBindCharLength = i2;
        }

        this.bindByteSubRange += this.bindByteOffset;
        this.bindCharSubRange += this.bindCharOffset;
      }

      int i2 = this.bindByteSubRange;
      int i3 = this.bindCharSubRange;
      int i4 = this.indicatorsOffset;
      int i5 = this.valueLengthsOffset;

      i1 = n;

      if (this.preparedCharBinds)
      {
        if (this.currentBatchBindAccessors == null)
        {
          for (i = 0; i < this.numberOfBindPositions; i++)
          {
            i6 = this.bindIndicators[(i1 + 1)];

            i7 = this.currentBatchCharLens[i];

            i8 = i7 == 0 ? i2 : i3;

            this.bindIndicators[(i1 + 3)] = (short)(i8 >> 16);

            this.bindIndicators[(i1 + 4)] = (short)(i8 & 0xFFFF);

            i2 += i6 * this.bindBufferCapacity;
            i3 += i7 * this.bindBufferCapacity;
            i1 += 10;
          }

        }

        for (i = 0; i < this.numberOfBindPositions; i++)
        {
          i6 = this.bindIndicators[(i1 + 1)];

          i7 = this.currentBatchCharLens[i];

          i8 = i7 == 0 ? i2 : i3;

          this.bindIndicators[(i1 + 3)] = (short)(i8 >> 16);

          this.bindIndicators[(i1 + 4)] = (short)(i8 & 0xFFFF);

          localObject = this.currentBatchBindAccessors[i];

          if (localObject != null)
          {
            if (i7 > 0)
            {
              ((Accessor)localObject).columnIndex = i3;
              ((Accessor)localObject).charLength = i7;
            }
            else
            {
              ((Accessor)localObject).columnIndex = i2;
              ((Accessor)localObject).byteLength = i6;
            }

            ((Accessor)localObject).lengthIndex = i5;
            ((Accessor)localObject).indicatorIndex = i4;
            ((Accessor)localObject).rowSpaceByte = this.bindBytes;
            ((Accessor)localObject).rowSpaceChar = this.bindChars;
            ((Accessor)localObject).rowSpaceIndicator = this.bindIndicators;

            if ((((Accessor)localObject).defineType == 109) || (((Accessor)localObject).defineType == 111))
            {
              ((Accessor)localObject).setOffsets(this.bindBufferCapacity);
            }

          }

          i2 += i6 * this.bindBufferCapacity;
          i3 += i7 * this.bindBufferCapacity;
          i4 += this.numberOfBindRowsAllocated;
          i5 += this.numberOfBindRowsAllocated;
          i1 += 10;
        }

        i2 = this.bindByteSubRange;
        i3 = this.bindCharSubRange;
        i4 = this.indicatorsOffset;
        i5 = this.valueLengthsOffset;
        i1 = n;
      }

      int i6 = this.bindBufferCapacity - this.numberOfBoundRows;
      int i7 = this.numberOfBoundRows - 1;
      int i8 = i7 + paramInt1;
      Object localObject = this.binders[i8];

      if (this.parameterOtype != null)
      {
        System.arraycopy(this.parameterDatum[i8], 0, this.lastBoundTypeBytes, 0, this.numberOfBindPositions);

        System.arraycopy(this.parameterOtype[i8], 0, this.lastBoundTypeOtypes, 0, this.numberOfBindPositions);
      }

      if (this.hasIbtBind) {
        processPlsqlIndexTabBinds(paramInt1);
      }
      if (this.returnParamAccessors != null) processDmlReturningBind();

      boolean bool = ((this.sqlKind != 1) && (this.sqlKind != 4)) || (this.currentRowBindAccessors == null);

      for (i = 0; i < this.numberOfBindPositions; i++)
      {
        int i9 = this.bindIndicators[(i1 + 1)];

        int i10 = this.currentBatchCharLens[i];

        this.lastBinders[i] = localObject[i];
        this.lastBoundByteLens[i] = i9;

        for (int i11 = 0; i11 < this.numberOfBoundRows; )
        {
          int i12 = paramInt1 + i11;

          this.binders[i12][i].bind(this, i, i11, i12, this.bindBytes, this.bindChars, this.bindIndicators, i9, i10, i2, i3, i5 + i11, i4 + i11, bool);

          this.binders[i12][i] = null;
          i2 += i9;
          i3 += i10;

          i11++;
        }

        this.lastBoundByteOffsets[i] = (i2 - i9);
        this.lastBoundCharOffsets[i] = (i3 - i10);
        this.lastBoundInds[i] = this.bindIndicators[(i4 + i7)];
        this.lastBoundLens[i] = this.bindIndicators[(i5 + i7)];

        this.lastBoundCharLens[i] = 0;

        i2 += i6 * i9;
        i3 += i6 * i10;
        i4 += this.numberOfBindRowsAllocated;
        i5 += this.numberOfBindRowsAllocated;
        i1 += 10;
      }

      this.lastBoundBytes = this.bindBytes;
      this.lastBoundByteOffset = this.bindByteOffset;
      this.lastBoundChars = this.bindChars;
      this.lastBoundCharOffset = this.bindCharOffset;

      int[] arrayOfInt = this.currentBatchCharLens;

      this.currentBatchCharLens = this.lastBoundCharLens;
      this.lastBoundCharLens = arrayOfInt;

      this.lastBoundNeeded = false;

      prepareBindPreambles(this.numberOfBoundRows, this.bindBufferCapacity);
    }
    catch (NullPointerException localNullPointerException)
    {
      DatabaseError.throwSqlException(89);
    }
  }

  public void enterImplicitCache()
    throws SQLException
  {
    alwaysOnClose();

    if (!this.connection.isClosed())
    {
      cleanAllTempLobs();
    }

    if (this.connection.clearStatementMetaData)
    {
      this.lastBoundBytes = null;
      this.lastBoundChars = null;
    }

    clearParameters();

    this.cacheState = 2;
    this.creationState = 1;

    this.currentResultSet = null;
    this.lastIndex = 0;

    this.queryTimeout = 0;
    this.autoRollback = 2;

    this.rowPrefetchChanged = false;
    this.currentRank = 0;
    this.currentRow = -1;
    this.validRows = 0;
    this.maxRows = 0;
    this.totalRowsVisited = 0;
    this.maxFieldSize = 0;
    this.gotLastBatch = false;
    this.clearParameters = true;
    this.scrollRset = null;
    this.needToAddIdentifier = false;
    this.defaultFetchDirection = 1000;
    this.defaultTZ = null;

    if (this.sqlKind == 3)
    {
      this.needToParse = true;
      this.needToPrepareDefineBuffer = true;
      this.columnsDefinedByUser = false;
    }

    if ((this.connection.isMemoryFreedOnEnteringCache) && (this.defineIndicators != null))
    {
      this.cachedDefineByteSize = (this.defineBytes != null ? this.defineBytes.length : 0);
      this.cachedDefineCharSize = (this.defineChars != null ? this.defineChars.length : 0);
      this.cachedDefineIndicatorSize = (this.defineIndicators != null ? this.defineIndicators.length : 0);
      this.defineChars = null;
      this.defineBytes = null;
      this.defineIndicators = null;
    }

    if (this.accessors != null)
    {
      int i = this.accessors.length;

      for (int j = 0; j < i; j++)
      {
        if (this.accessors[j] == null)
          continue;
        if (this.connection.isMemoryFreedOnEnteringCache)
        {
          this.accessors[j].rowSpaceByte = null;
          this.accessors[j].rowSpaceChar = null;
          this.accessors[j].rowSpaceIndicator = null;
        }

        if (this.columnsDefinedByUser) {
          this.accessors[j].externalType = 0;
        }

      }

    }

    this.fixedString = this.connection.getDefaultFixedString();
    this.defaultRowPrefetch = this.rowPrefetch;

    if (this.connection.clearStatementMetaData)
    {
      this.sqlStringChanged = true;
      this.needToParse = true;
      this.needToPrepareDefineBuffer = true;
      this.columnsDefinedByUser = false;

      if (this.userRsetType == 0)
      {
        this.userRsetType = 1;
        this.realRsetType = 1;
      }
      this.currentRowNeedToPrepareBinds = true;
    }
  }

  public void enterExplicitCache()
    throws SQLException
  {
    this.cacheState = 2;
    this.creationState = 2;
    this.defaultTZ = null;

    alwaysOnClose();
  }

  public void exitImplicitCacheToActive()
    throws SQLException
  {
    this.cacheState = 1;
    this.closed = false;

    if (this.rowPrefetch != this.connection.getDefaultRowPrefetch())
    {
      if (this.streamList == null)
      {
        this.rowPrefetch = this.connection.getDefaultRowPrefetch();
        this.defaultRowPrefetch = this.rowPrefetch;

        this.rowPrefetchChanged = true;
      }

    }

    if (this.batch != this.connection.getDefaultExecuteBatch())
    {
      resetBatch();
    }

    if (this.autoRefetch != this.connection.getDefaultAutoRefetch())
    {
      this.autoRefetch = this.connection.getDefaultAutoRefetch();
    }

    this.processEscapes = this.connection.processEscapes;

    if ((this.connection.isMemoryFreedOnEnteringCache) && (this.cachedDefineIndicatorSize != 0))
    {
      this.defineBytes = new byte[this.cachedDefineByteSize];
      this.defineChars = new char[this.cachedDefineCharSize];
      this.defineIndicators = new short[this.cachedDefineIndicatorSize];
      if (this.accessors != null)
      {
        int i = this.accessors.length;

        for (int j = 0; j < i; j++)
        {
          if (this.accessors[j] == null)
            continue;
          if (!this.connection.isMemoryFreedOnEnteringCache)
            continue;
          this.accessors[j].rowSpaceByte = this.defineBytes;
          this.accessors[j].rowSpaceChar = this.defineChars;
          this.accessors[j].rowSpaceIndicator = this.defineIndicators;
        }
      }
    }
  }

  public void exitExplicitCacheToActive()
    throws SQLException
  {
    this.cacheState = 1;
    this.closed = false;
  }

  public void exitImplicitCacheToClose()
    throws SQLException
  {
    this.cacheState = 0;
    this.closed = false;

    synchronized (this)
    {
      hardClose();
    }
  }

  public void exitExplicitCacheToClose()
    throws SQLException
  {
    this.cacheState = 0;
    this.closed = false;

    synchronized (this)
    {
      hardClose();
    }
  }

  public void closeWithKey(String paramString)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        closeOrCache(paramString);
      }
    }
  }

  int executeInternal() throws SQLException
  {
    this.noMoreUpdateCounts = false;

    ensureOpen();

    if ((this.currentRank > 0) && (this.m_batchStyle == 2))
    {
      DatabaseError.throwSqlException(81, "batch must be either executed or cleared");
    }

    int i = this.userRsetType == 1 ? 1 : 0;

    prepareForNewResults(true, false);

    processCompletedBindRow(this.sqlKind == 0 ? 1 : this.batch, false);

    if ((i == 0) && (!this.scrollRsetTypeSolved)) {
      return doScrollPstmtExecuteUpdate() + this.prematureBatchCount;
    }
    doExecuteWithTimeout();

    int j = (this.prematureBatchCount != 0) && (this.validRows > 0) ? 1 : 0;

    if (i == 0)
    {
      this.currentResultSet = new OracleResultSetImpl(this.connection, this);
      this.scrollRset = ResultSetUtil.createScrollResultSet(this, this.currentResultSet, this.realRsetType);

      if (!this.connection.accumulateBatchResult) {
        j = 0;
      }
    }
    if (j != 0)
    {
      this.validRows += this.prematureBatchCount;
      this.prematureBatchCount = 0;
    }

    return this.validRows;
  }

  public ResultSet executeQuery()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        this.executionType = 1;

        executeInternal();

        if (this.userRsetType == 1)
        {
          this.currentResultSet = new OracleResultSetImpl(this.connection, this);

          return this.currentResultSet;
        }

        if (this.scrollRset == null)
        {
          this.currentResultSet = new OracleResultSetImpl(this.connection, this);
          this.scrollRset = this.currentResultSet;
        }

        return this.scrollRset;
      }
    }
  }

  public int executeUpdate()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        this.executionType = 2;

        return executeInternal();
      }
    }
  }

  public boolean execute()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        this.executionType = 3;

        executeInternal();

        return this.sqlKind == 0;
      }
    }
  }

  void slideDownCurrentRow(int paramInt)
  {
    if (this.binders != null)
    {
      this.binders[paramInt] = this.binders[0];
      this.binders[0] = this.currentRowBinders;
    }
    Object localObject;
    if (this.parameterInt != null)
    {
      localObject = this.parameterInt[0];

      this.parameterInt[0] = this.parameterInt[paramInt];
      this.parameterInt[paramInt] = localObject;
    }

    if (this.parameterLong != null)
    {
      localObject = this.parameterLong[0];

      this.parameterLong[0] = this.parameterLong[paramInt];
      this.parameterLong[paramInt] = localObject;
    }

    if (this.parameterFloat != null)
    {
      localObject = this.parameterFloat[0];

      this.parameterFloat[0] = this.parameterFloat[paramInt];
      this.parameterFloat[paramInt] = localObject;
    }

    if (this.parameterDouble != null)
    {
      localObject = this.parameterDouble[0];

      this.parameterDouble[0] = this.parameterDouble[paramInt];
      this.parameterDouble[paramInt] = localObject;
    }

    if (this.parameterBigDecimal != null)
    {
      localObject = this.parameterBigDecimal[0];

      this.parameterBigDecimal[0] = this.parameterBigDecimal[paramInt];
      this.parameterBigDecimal[paramInt] = localObject;
    }

    if (this.parameterString != null)
    {
      localObject = this.parameterString[0];

      this.parameterString[0] = this.parameterString[paramInt];
      this.parameterString[paramInt] = localObject;
    }

    if (this.parameterDate != null)
    {
      localObject = this.parameterDate[0];

      this.parameterDate[0] = this.parameterDate[paramInt];
      this.parameterDate[paramInt] = localObject;
    }

    if (this.parameterTime != null)
    {
      localObject = this.parameterTime[0];

      this.parameterTime[0] = this.parameterTime[paramInt];
      this.parameterTime[paramInt] = localObject;
    }

    if (this.parameterTimestamp != null)
    {
      localObject = this.parameterTimestamp[0];

      this.parameterTimestamp[0] = this.parameterTimestamp[paramInt];
      this.parameterTimestamp[paramInt] = localObject;
    }

    if (this.parameterDatum != null)
    {
      localObject = this.parameterDatum[0];

      this.parameterDatum[0] = this.parameterDatum[paramInt];
      this.parameterDatum[paramInt] = localObject;
    }

    if (this.parameterOtype != null)
    {
      localObject = this.parameterOtype[0];

      this.parameterOtype[0] = this.parameterOtype[paramInt];
      this.parameterOtype[paramInt] = localObject;
    }

    if (this.parameterStream != null)
    {
      localObject = this.parameterStream[0];

      this.parameterStream[0] = this.parameterStream[paramInt];
      this.parameterStream[paramInt] = localObject;
    }
  }

  void resetBatch()
  {
    this.batch = this.connection.getDefaultExecuteBatch();
  }

  public int sendBatch()
    throws SQLException
  {
    if (isJdbcBatchStyle())
    {
      return 0;
    }

    synchronized (this.connection)
    {
      synchronized (this)
      {
        try
        {
          ensureOpen();

          if (this.currentRank <= 0) {
            i = this.connection.accumulateBatchResult ? 0 : this.validRows;

            this.currentRank = 0; return i;
          }
          int i = this.batch;
          try
          {
            j = this.currentRank;

            if (this.batch != this.currentRank) {
              this.batch = this.currentRank;
            }
            setupBindBuffers(0, this.currentRank);

            this.currentRank -= 1;

            doExecuteWithTimeout();

            slideDownCurrentRow(j);

            if (this.batch != i)
              this.batch = i;
          }
          finally
          {
            if (this.batch != i) {
              this.batch = i;
            }

          }

          if (this.connection.accumulateBatchResult)
          {
            this.validRows += this.prematureBatchCount;
            this.prematureBatchCount = 0;
          }

          int j = this.validRows;

          this.currentRank = 0; return j; } finally { this.currentRank = 0;
        }
      }
    }
  }

  public synchronized void setExecuteBatch(int paramInt)
    throws SQLException
  {
    setOracleBatchStyle();
    set_execute_batch(paramInt);
  }

  synchronized void set_execute_batch(int paramInt)
    throws SQLException
  {
    if (paramInt <= 0) {
      DatabaseError.throwSqlException(42);
    }
    if (paramInt == this.batch) {
      return;
    }

    if (this.currentRank > 0) {
      sendBatch();
    }
    int i = this.batch;

    this.batch = paramInt;

    if (this.numberOfBindRowsAllocated < this.batch)
      growBinds(this.batch);
  }

  public final int getExecuteBatch()
  {
    return this.batch;
  }

  public synchronized void defineParameterTypeBytes(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    if (paramInt3 < 0) {
      DatabaseError.throwSqlException(53);
    }
    if (paramInt1 < 1) {
      DatabaseError.throwSqlException(3);
    }
    switch (paramInt2)
    {
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
      paramInt2 = 6;

      break;
    case 1:
      paramInt2 = 96;

      break;
    case 12:
      paramInt2 = 1;

      break;
    case 91:
    case 92:
      paramInt2 = 12;

      break;
    case -103:
      paramInt2 = 182;

      break;
    case -104:
      paramInt2 = 183;

      break;
    case -100:
    case 93:
      paramInt2 = 180;

      break;
    case -101:
      paramInt2 = 181;

      break;
    case -102:
      paramInt2 = 231;

      break;
    case -3:
    case -2:
      paramInt2 = 23;

      break;
    case 100:
      paramInt2 = 100;

      break;
    case 101:
      paramInt2 = 101;

      break;
    case -8:
      paramInt2 = 104;

      break;
    case 2004:
      paramInt2 = 113;

      break;
    case 2005:
      paramInt2 = 112;

      break;
    case -13:
      paramInt2 = 114;

      break;
    case -10:
      paramInt2 = 102;

      break;
    case 0:
      DatabaseError.throwSqlException(4);
    default:
      DatabaseError.throwSqlException(23);
    }
  }

  public synchronized void defineParameterTypeChars(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    int i = this.connection.getNlsRatio();

    if ((paramInt2 == 1) || (paramInt2 == 12))
      defineParameterTypeBytes(paramInt1, paramInt2, paramInt3 * i);
    else
      defineParameterTypeBytes(paramInt1, paramInt2, paramInt3);
  }

  public synchronized void defineParameterType(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    defineParameterTypeBytes(paramInt1, paramInt2, paramInt3);
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    ResultSet localResultSet = getResultSet();

    if (localResultSet != null) {
      return localResultSet.getMetaData();
    }
    return null;
  }

  public void setNull(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    setNullInternal(paramInt1, paramInt2, paramString);
  }

  void setNullInternal(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if ((paramInt2 == 2002) || (paramInt2 == 2008) || (paramInt2 == 2003) || (paramInt2 == 2007) || (paramInt2 == 2006))
    {
      synchronized (this.connection)
      {
        synchronized (this)
        {
          setNullCritial(i, paramInt2, paramString);

          this.currentRowCharLens[i] = 0;
        }

      }

    }

    setNullInternal(paramInt1, paramInt2);

    return;
  }

  synchronized void setNullInternal(int paramInt1, int paramInt2)
    throws SQLException
  {
    setNullCritical(paramInt1, paramInt2);
  }

  void setNullCritial(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    Object localObject1 = null;
    Binder localBinder = this.theNamedTypeNullBinder;
    Object localObject2;
    switch (paramInt2)
    {
    case 2006:
      localBinder = this.theRefTypeNullBinder;
    case 2002:
    case 2008:
      localObject2 = StructDescriptor.createDescriptor(paramString, this.connection);

      localObject1 = ((StructDescriptor)localObject2).getOracleTypeADT();

      break;
    case 2003:
      localObject2 = ArrayDescriptor.createDescriptor(paramString, this.connection);

      localObject1 = ((ArrayDescriptor)localObject2).getOracleTypeCOLLECTION();

      break;
    case 2007:
      localObject2 = OpaqueDescriptor.createDescriptor(paramString, this.connection);

      localObject1 = (OracleTypeADT)((OpaqueDescriptor)localObject2).getPickler();
    case 2004:
    case 2005:
    }

    this.currentRowBinders[paramInt1] = localBinder;

    if (this.parameterDatum == null) {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDatum[this.currentRank][paramInt1] = null;

    ((OracleTypeADT)localObject1).getTOID();

    if (this.parameterOtype == null) {
      this.parameterOtype = new OracleTypeADT[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterOtype[this.currentRank][paramInt1] = localObject1;
  }

  public void setNullAtName(String paramString1, int paramInt, String paramString2)
    throws SQLException
  {
    String str = paramString1.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setNullInternal(k + 1, paramInt, paramString2);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString1);
  }

  public synchronized void setNull(int paramInt1, int paramInt2)
    throws SQLException
  {
    setNullCritical(paramInt1, paramInt2);
  }

  void setNullCritical(int paramInt1, int paramInt2) throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    Binder localBinder = null;
    int j = getInternalType(paramInt2);

    switch (j)
    {
    case 6:
      localBinder = this.theVarnumNullBinder;

      break;
    case 1:
    case 8:
    case 96:
    case 995:
      localBinder = this.theVarcharNullBinder;
      this.currentRowCharLens[i] = 1;

      break;
    case 999:
      localBinder = this.theFixedCHARNullBinder;

      break;
    case 12:
      localBinder = this.theDateNullBinder;

      break;
    case 180:
      localBinder = this.connection.v8Compatible ? this.theDateNullBinder : this.theTimestampNullBinder;

      break;
    case 181:
      localBinder = this.theTSTZNullBinder;

      break;
    case 231:
      localBinder = this.theTSLTZNullBinder;

      break;
    case 104:
      localBinder = this.theRowidNullBinder;

      break;
    case 183:
      localBinder = this.theIntervalDSNullBinder;

      break;
    case 182:
      localBinder = this.theIntervalYMNullBinder;

      break;
    case 23:
    case 24:
      localBinder = this.theRawNullBinder;

      break;
    case 100:
      localBinder = this.theBinaryFloatNullBinder;

      break;
    case 101:
      localBinder = this.theBinaryDoubleNullBinder;

      break;
    case 113:
      localBinder = this.theBlobNullBinder;

      break;
    case 112:
      localBinder = this.theClobNullBinder;

      break;
    case 114:
      localBinder = this.theBfileNullBinder;

      break;
    case 109:
    case 111:
      DatabaseError.throwSqlException(4, "sqlType=" + paramInt2);
    case 102:
    case 998:
    default:
      DatabaseError.throwSqlException(23, "sqlType=" + paramInt2);
    }

    this.currentRowBinders[i] = localBinder;
  }

  public void setNullAtName(String paramString, int paramInt)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setNull(k + 1, paramInt);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    setBooleanInternal(paramInt, paramBoolean);
  }

  void setBooleanInternal(int paramInt, boolean paramBoolean) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theBooleanBinder;

    if (this.parameterInt == null) {
      this.parameterInt = new int[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterInt[this.currentRank][i] = (paramBoolean ? 1 : 0);
  }

  public void setBooleanAtName(String paramString, boolean paramBoolean)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBoolean(k + 1, paramBoolean);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setByte(int paramInt, byte paramByte)
    throws SQLException
  {
    setByteInternal(paramInt, paramByte);
  }

  void setByteInternal(int paramInt, byte paramByte) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theByteBinder;

    if (this.parameterInt == null) {
      this.parameterInt = new int[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterInt[this.currentRank][i] = paramByte;
  }

  public void setByteAtName(String paramString, byte paramByte)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setByte(k + 1, paramByte);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setShort(int paramInt, short paramShort)
    throws SQLException
  {
    setShortInternal(paramInt, paramShort);
  }

  void setShortInternal(int paramInt, short paramShort) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theShortBinder;

    if (this.parameterInt == null) {
      this.parameterInt = new int[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterInt[this.currentRank][i] = paramShort;
  }

  public void setShortAtName(String paramString, short paramShort)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setShort(k + 1, paramShort);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    setIntInternal(paramInt1, paramInt2);
  }

  void setIntInternal(int paramInt1, int paramInt2) throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theIntBinder;

    if (this.parameterInt == null) {
      this.parameterInt = new int[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterInt[this.currentRank][i] = paramInt2;
  }

  public void setIntAtName(String paramString, int paramInt)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setInt(k + 1, paramInt);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setLong(int paramInt, long paramLong)
    throws SQLException
  {
    setLongInternal(paramInt, paramLong);
  }

  void setLongInternal(int paramInt, long paramLong) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theLongBinder;

    if (this.parameterLong == null) {
      this.parameterLong = new long[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterLong[this.currentRank][i] = paramLong;
  }

  public void setLongAtName(String paramString, long paramLong)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setLong(k + 1, paramLong);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    setFloatInternal(paramInt, paramFloat);
  }

  void setFloatInternal(int paramInt, float paramFloat) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.theFloatBinder == null)
    {
      this.theFloatBinder = theStaticFloatBinder;

      Properties localProperties = this.connection.connectionProperties;

      if (localProperties != null)
      {
        String str = localProperties.getProperty("SetFloatAndDoubleUseBinary");

        if ((str != null) && (str.equalsIgnoreCase("true"))) {
          this.theFloatBinder = theStaticBinaryFloatBinder;
        }
      }
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theFloatBinder;

    if (this.theFloatBinder == theStaticFloatBinder)
    {
      if (this.parameterDouble == null) {
        this.parameterDouble = new double[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDouble[this.currentRank][i] = paramFloat;
    }
    else
    {
      if (this.parameterFloat == null) {
        this.parameterFloat = new float[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterFloat[this.currentRank][i] = paramFloat;
    }
  }

  public synchronized void setBinaryFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    setBinaryFloatInternal(paramInt, paramFloat);
  }

  void setBinaryFloatInternal(int paramInt, float paramFloat) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theBinaryFloatBinder;

    if (this.parameterFloat == null) {
      this.parameterFloat = new float[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterFloat[this.currentRank][i] = paramFloat;
  }

  public void setBinaryFloatAtName(String paramString, float paramFloat)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBinaryFloat(k + 1, paramFloat);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBinaryFloat(int paramInt, BINARY_FLOAT paramBINARY_FLOAT)
    throws SQLException
  {
    setBinaryFloatInternal(paramInt, paramBINARY_FLOAT);
  }

  void setBinaryFloatInternal(int paramInt, BINARY_FLOAT paramBINARY_FLOAT)
    throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramBINARY_FLOAT == null)
    {
      this.currentRowBinders[i] = this.theBINARY_FLOATNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theBINARY_FLOATBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramBINARY_FLOAT.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setBinaryFloatAtName(String paramString, BINARY_FLOAT paramBINARY_FLOAT)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBinaryFloat(k + 1, paramBINARY_FLOAT);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBinaryDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    setBinaryDoubleInternal(paramInt, paramDouble);
  }

  void setBinaryDoubleInternal(int paramInt, double paramDouble) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowBinders[i] = this.theBinaryDoubleBinder;

    if (this.parameterDouble == null) {
      this.parameterDouble = new double[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.currentRowCharLens[i] = 0;

    this.parameterDouble[this.currentRank][i] = paramDouble;
  }

  public synchronized void setBinaryDouble(int paramInt, BINARY_DOUBLE paramBINARY_DOUBLE)
    throws SQLException
  {
    setBinaryDoubleInternal(paramInt, paramBINARY_DOUBLE);
  }

  void setBinaryDoubleInternal(int paramInt, BINARY_DOUBLE paramBINARY_DOUBLE)
    throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramBINARY_DOUBLE == null)
    {
      this.currentRowBinders[i] = this.theBINARY_DOUBLENullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theBINARY_DOUBLEBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramBINARY_DOUBLE.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setBinaryDoubleAtName(String paramString, double paramDouble)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBinaryDouble(k + 1, paramDouble);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setFloatAtName(String paramString, float paramFloat)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setFloat(k + 1, paramFloat);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setBinaryDoubleAtName(String paramString, BINARY_DOUBLE paramBINARY_DOUBLE)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBinaryDouble(k + 1, paramBINARY_DOUBLE);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    setDoubleInternal(paramInt, paramDouble);
  }

  void setDoubleInternal(int paramInt, double paramDouble) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.theDoubleBinder == null)
    {
      this.theDoubleBinder = theStaticDoubleBinder;

      Properties localProperties = this.connection.connectionProperties;

      if (localProperties != null)
      {
        String str = localProperties.getProperty("SetFloatAndDoubleUseBinary");

        if ((str != null) && (str.equalsIgnoreCase("true"))) {
          this.theDoubleBinder = theStaticBinaryDoubleBinder;
        }
      }
    }
    this.currentRowCharLens[i] = 0;

    this.currentRowBinders[i] = this.theDoubleBinder;

    if (this.parameterDouble == null) {
      this.parameterDouble = new double[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDouble[this.currentRank][i] = paramDouble;
  }

  public void setDoubleAtName(String paramString, double paramDouble)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setDouble(k + 1, paramDouble);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    setBigDecimalInternal(paramInt, paramBigDecimal);
  }

  void setBigDecimalInternal(int paramInt, BigDecimal paramBigDecimal) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramBigDecimal == null) {
      this.currentRowBinders[i] = this.theVarnumNullBinder;
    }
    else {
      this.currentRowBinders[i] = this.theBigDecimalBinder;

      if (this.parameterBigDecimal == null) {
        this.parameterBigDecimal = new BigDecimal[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterBigDecimal[this.currentRank][i] = paramBigDecimal;
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setBigDecimalAtName(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBigDecimal(k + 1, paramBigDecimal);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setString(int paramInt, String paramString)
    throws SQLException
  {
    setStringInternal(paramInt, paramString);
  }

  void setStringInternal(int paramInt, String paramString)
    throws SQLException
  {
    int i = paramInt - 1;
    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    int j = paramString != null ? paramString.length() : 0;

    if (j == 0) {
      basicBindNullString(paramInt);
    }
    else
    {
      int k;
      if (this.currentRowFormOfUse[(paramInt - 1)] == 1)
      {
        if ((this.sqlKind == 1) || (this.sqlKind == 4))
        {
          if ((j > this.maxVcsBytesPlsql) || ((j > this.maxVcsCharsPlsql) && (this.isServerCharSetFixedWidth)))
          {
            setStringForClobCritical(paramInt, paramString);
          }
          else if (j > this.maxVcsCharsPlsql)
          {
            k = this.connection.conversion.encodedByteLength(paramString, false);
            if (k > this.maxVcsBytesPlsql)
            {
              setStringForClobCritical(paramInt, paramString);
            }
            else
              basicBindString(paramInt, paramString);
          }
          else {
            basicBindString(paramInt, paramString);
          }

        }
        else if (j <= this.maxVcsCharsSql)
          basicBindString(paramInt, paramString);
        else if (j <= this.maxStreamCharsSql)
          basicBindCharacterStream(paramInt, new StringReader(paramString), j);
        else {
          setStringForClobCritical(paramInt, paramString);
        }

      }
      else if ((this.sqlKind == 1) || (this.sqlKind == 4))
      {
        if ((j > this.maxVcsBytesPlsql) || ((j > this.maxVcsNCharsPlsql) && (this.isServerNCharSetFixedWidth)))
        {
          setStringForClobCritical(paramInt, paramString);
        }
        else if (j > this.maxVcsNCharsPlsql)
        {
          k = this.connection.conversion.encodedByteLength(paramString, true);
          if (k > this.maxVcsBytesPlsql)
          {
            setStringForClobCritical(paramInt, paramString);
          }
          else
            basicBindString(paramInt, paramString);
        }
        else {
          basicBindString(paramInt, paramString);
        }

      }
      else if (j <= this.maxVcsCharsSql)
        basicBindString(paramInt, paramString);
      else if (j <= this.maxStreamNCharsSql)
      {
        setStringForClobCritical(paramInt, paramString);
      }
      else
        setStringForClobCritical(paramInt, paramString);
    }
  }

  void basicBindNullString(int paramInt)
    throws SQLException
  {
    int i = paramInt - 1;
    this.currentRowBinders[i] = this.theVarcharNullBinder;

    if ((this.sqlKind == 1) || (this.sqlKind == 4))
      this.currentRowCharLens[i] = this.minVcsBindSize;
    else
      this.currentRowCharLens[i] = 1;
  }

  void basicBindString(int paramInt, String paramString)
    throws SQLException
  {
    int i = paramInt - 1;
    this.currentRowBinders[i] = this.theStringBinder;
    int j = paramString.length();

    if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      int k = this.connection.minVcsBindSize;
      int m = j + 1;

      this.currentRowCharLens[i] = (m < k ? k : m);
    }
    else {
      this.currentRowCharLens[i] = (j + 1);
    }
    if (this.parameterString == null) {
      this.parameterString = new String[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterString[this.currentRank][i] = paramString;
  }

  public void setStringAtName(String paramString1, String paramString2)
    throws SQLException
  {
    String str = paramString1.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setString(k + 1, paramString2);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString1);
  }

  public void setStringForClob(int paramInt, String paramString)
    throws SQLException
  {
    if (paramString == null)
    {
      setNull(paramInt, 1);
      return;
    }
    int i = paramString.length();
    if (i == 0)
    {
      setNull(paramInt, 1);
      return;
    }

    if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      if (i <= this.maxVcsCharsPlsql)
      {
        setStringInternal(paramInt, paramString);
      }
      else
      {
        setStringForClobCritical(paramInt, paramString);
      }

    }
    else if (i <= this.maxVcsCharsSql)
    {
      setStringInternal(paramInt, paramString);
    }
    else
    {
      setStringForClobCritical(paramInt, paramString);
    }
  }

  void setStringForClobCritical(int paramInt, String paramString)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        CLOB localCLOB = CLOB.createTemporary(this.connection, true, 10, this.currentRowFormOfUse[(paramInt - 1)]);

        localCLOB.setString(1L, paramString);
        addToTempLobsToFree(localCLOB);
        setCLOBInternal(paramInt, localCLOB);
      }
    }
  }

  void setReaderContentsForClobCritical(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        CLOB localCLOB = CLOB.createTemporary(this.connection, true, 10, this.currentRowFormOfUse[(paramInt1 - 1)]);

        OracleClobWriter localOracleClobWriter = (OracleClobWriter)localCLOB.setCharacterStream(1L);
        int i = localCLOB.getBufferSize();
        char[] arrayOfChar = new char[i];
        int j = paramInt2;
        try {
          while (j >= i)
          {
            paramReader.read(arrayOfChar);
            localOracleClobWriter.write(arrayOfChar);
            j -= i;
          }
          if (j > 0)
          {
            paramReader.read(arrayOfChar, 0, j);
            localOracleClobWriter.write(arrayOfChar, 0, j);
          }
          localOracleClobWriter.flush(); } catch (IOException localIOException) {
        }
        addToTempLobsToFree(localCLOB);
        setCLOBInternal(paramInt1, localCLOB);
      }
    }
  }

  void setAsciiStreamContentsForClobCritical(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        CLOB localCLOB = CLOB.createTemporary(this.connection, true, 10, this.currentRowFormOfUse[(paramInt1 - 1)]);

        OracleClobWriter localOracleClobWriter = (OracleClobWriter)localCLOB.setCharacterStream(1L);
        int i = localCLOB.getBufferSize();
        byte[] arrayOfByte = new byte[i];
        char[] arrayOfChar = new char[i];
        int j = paramInt2;
        try {
          while (j >= i)
          {
            paramInputStream.read(arrayOfByte);
            DBConversion.asciiBytesToJavaChars(arrayOfByte, i, arrayOfChar);

            localOracleClobWriter.write(arrayOfChar);
            j -= i;
          }
          if (j > 0)
          {
            paramInputStream.read(arrayOfByte, 0, j);
            DBConversion.asciiBytesToJavaChars(arrayOfByte, j, arrayOfChar);

            localOracleClobWriter.write(arrayOfChar, 0, j);
          }
          localOracleClobWriter.flush(); } catch (IOException localIOException) {
        }
        addToTempLobsToFree(localCLOB);
        setCLOBInternal(paramInt1, localCLOB);
      }
    }
  }

  public void setStringForClobAtName(String paramString1, String paramString2)
    throws SQLException
  {
    String str = paramString1.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setStringForClob(k + 1, paramString2);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString1);
  }

  public synchronized void setFixedCHAR(int paramInt, String paramString)
    throws SQLException
  {
    setFixedCHARInternal(paramInt, paramString);
  }

  void setFixedCHARInternal(int paramInt, String paramString) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    int j = 0;

    if (paramString != null) {
      j = paramString.length();
    }

    if (j > 32766) {
      DatabaseError.throwSqlException(157);
    }
    if (paramString == null)
    {
      this.currentRowBinders[i] = this.theFixedCHARNullBinder;
      this.currentRowCharLens[i] = 1;
    }
    else
    {
      this.currentRowBinders[i] = this.theFixedCHARBinder;
      this.currentRowCharLens[i] = (j + 1);

      if (this.parameterString == null) {
        this.parameterString = new String[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterString[this.currentRank][i] = paramString;
    }
  }

  public void setFixedCHARAtName(String paramString1, String paramString2)
    throws SQLException
  {
    String str = paramString1.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setFixedCHAR(k + 1, paramString2);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString1);
  }

  /** @deprecated */
  public synchronized void setCursor(int paramInt, ResultSet paramResultSet)
    throws SQLException
  {
    setCursorInternal(paramInt, paramResultSet);
  }

  void setCursorInternal(int paramInt, ResultSet paramResultSet) throws SQLException
  {
    throw DatabaseError.newSqlException(23);
  }

  public void setCursorAtName(String paramString, ResultSet paramResultSet)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setCursor(k + 1, paramResultSet);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setROWID(int paramInt, ROWID paramROWID)
    throws SQLException
  {
    setROWIDInternal(paramInt, paramROWID);
  }

  void setROWIDInternal(int paramInt, ROWID paramROWID) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramROWID == null)
    {
      this.currentRowBinders[i] = this.theRowidNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theRowidBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramROWID.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setROWIDAtName(String paramString, ROWID paramROWID)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setROWID(k + 1, paramROWID);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setArray(int paramInt, Array paramArray)
    throws SQLException
  {
    setARRAYInternal(paramInt, (ARRAY)paramArray);
  }

  void setArrayInternal(int paramInt, Array paramArray) throws SQLException
  {
    setARRAYInternal(paramInt, (ARRAY)paramArray);
  }

  public void setArrayAtName(String paramString, Array paramArray)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setArrayInternal(k + 1, paramArray);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setARRAY(int paramInt, ARRAY paramARRAY)
    throws SQLException
  {
    setARRAYInternal(paramInt, paramARRAY);
  }

  void setARRAYInternal(int paramInt, ARRAY paramARRAY) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramARRAY == null)
    {
      DatabaseError.throwSqlException(68);
    }
    else
    {
      synchronized (this.connection)
      {
        synchronized (this)
        {
          setArrayCritical(i, paramARRAY);

          this.currentRowCharLens[i] = 0;
        }
      }
    }
  }

  void setArrayCritical(int paramInt, ARRAY paramARRAY)
    throws SQLException
  {
    ArrayDescriptor localArrayDescriptor = paramARRAY.getDescriptor();

    if (localArrayDescriptor == null)
    {
      DatabaseError.throwSqlException(61);
    }

    this.currentRowBinders[paramInt] = this.theNamedTypeBinder;

    if (this.parameterDatum == null)
    {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDatum[this.currentRank][paramInt] = paramARRAY.toBytes();

    OracleTypeCOLLECTION localOracleTypeCOLLECTION = localArrayDescriptor.getOracleTypeCOLLECTION();

    localOracleTypeCOLLECTION.getTOID();

    if (this.parameterOtype == null)
    {
      this.parameterOtype = new OracleTypeADT[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterOtype[this.currentRank][paramInt] = localOracleTypeCOLLECTION;
  }

  public void setARRAYAtName(String paramString, ARRAY paramARRAY)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setARRAYInternal(k + 1, paramARRAY);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setOPAQUE(int paramInt, OPAQUE paramOPAQUE)
    throws SQLException
  {
    setOPAQUEInternal(paramInt, paramOPAQUE);
  }

  void setOPAQUEInternal(int paramInt, OPAQUE paramOPAQUE) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramOPAQUE == null)
    {
      DatabaseError.throwSqlException(68);
    }
    else
    {
      synchronized (this.connection)
      {
        synchronized (this)
        {
          setOPAQUECritical(i, paramOPAQUE);

          this.currentRowCharLens[i] = 0;
        }
      }
    }
  }

  void setOPAQUECritical(int paramInt, OPAQUE paramOPAQUE)
    throws SQLException
  {
    OpaqueDescriptor localOpaqueDescriptor = paramOPAQUE.getDescriptor();

    if (localOpaqueDescriptor == null)
    {
      DatabaseError.throwSqlException(61);
    }

    this.currentRowBinders[paramInt] = this.theNamedTypeBinder;

    if (this.parameterDatum == null)
    {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDatum[this.currentRank][paramInt] = paramOPAQUE.toBytes();

    OracleTypeADT localOracleTypeADT = (OracleTypeADT)localOpaqueDescriptor.getPickler();

    localOracleTypeADT.getTOID();

    if (this.parameterOtype == null)
    {
      this.parameterOtype = new OracleTypeADT[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterOtype[this.currentRank][paramInt] = localOracleTypeADT;
  }

  public void setOPAQUEAtName(String paramString, OPAQUE paramOPAQUE)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setOPAQUEInternal(k + 1, paramOPAQUE);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setStructDescriptor(int paramInt, StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    setStructDescriptorInternal(paramInt, paramStructDescriptor);
  }

  void setStructDescriptorInternal(int paramInt, StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramStructDescriptor == null) {
      DatabaseError.throwSqlException(68);
    }
    synchronized (this.connection)
    {
      synchronized (this)
      {
        setStructDescriptorCritical(i, paramStructDescriptor);

        this.currentRowCharLens[i] = 0;
      }
    }
  }

  void setStructDescriptorCritical(int paramInt, StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    this.currentRowBinders[paramInt] = this.theNamedTypeBinder;

    if (this.parameterDatum == null) {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    OracleTypeADT localOracleTypeADT = paramStructDescriptor.getOracleTypeADT();

    localOracleTypeADT.getTOID();

    if (this.parameterOtype == null) {
      this.parameterOtype = new OracleTypeADT[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterOtype[this.currentRank][paramInt] = localOracleTypeADT;
  }

  public void setStructDescriptorAtName(String paramString, StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setStructDescriptorInternal(k + 1, paramStructDescriptor);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  synchronized void setPreBindsCompelete()
    throws SQLException
  {
  }

  public void setSTRUCT(int paramInt, STRUCT paramSTRUCT)
    throws SQLException
  {
    setSTRUCTInternal(paramInt, paramSTRUCT);
  }

  void setSTRUCTInternal(int paramInt, STRUCT paramSTRUCT) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramSTRUCT == null)
    {
      DatabaseError.throwSqlException(68);
    }
    else
    {
      synchronized (this.connection)
      {
        synchronized (this)
        {
          setSTRUCTCritical(i, paramSTRUCT);

          this.currentRowCharLens[i] = 0;
        }
      }
    }
  }

  void setSTRUCTCritical(int paramInt, STRUCT paramSTRUCT)
    throws SQLException
  {
    StructDescriptor localStructDescriptor = paramSTRUCT.getDescriptor();

    if (localStructDescriptor == null)
    {
      DatabaseError.throwSqlException(61);
    }

    this.currentRowBinders[paramInt] = this.theNamedTypeBinder;

    if (this.parameterDatum == null)
    {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDatum[this.currentRank][paramInt] = paramSTRUCT.toBytes();

    OracleTypeADT localOracleTypeADT = localStructDescriptor.getOracleTypeADT();

    localOracleTypeADT.getTOID();

    if (this.parameterOtype == null)
    {
      this.parameterOtype = new OracleTypeADT[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterOtype[this.currentRank][paramInt] = localOracleTypeADT;
  }

  public void setSTRUCTAtName(String paramString, STRUCT paramSTRUCT)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setSTRUCTInternal(k + 1, paramSTRUCT);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setRAW(int paramInt, RAW paramRAW)
    throws SQLException
  {
    setRAWInternal(paramInt, paramRAW);
  }

  void setRAWInternal(int paramInt, RAW paramRAW) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    if (paramRAW == null)
      this.currentRowBinders[i] = this.theRawNullBinder;
    else
      setBytesInternal(paramInt, paramRAW.getBytes());
  }

  public void setRAWAtName(String paramString, RAW paramRAW)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setRAW(k + 1, paramRAW);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setCHAR(int paramInt, CHAR paramCHAR)
    throws SQLException
  {
    setCHARInternal(paramInt, paramCHAR);
  }

  void setCHARInternal(int paramInt, CHAR paramCHAR) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if ((paramCHAR == null) || (paramCHAR.shareBytes().length == 0))
    {
      this.currentRowBinders[i] = this.theSetCHARNullBinder;
      this.currentRowCharLens[i] = 1;
    }
    else
    {
      int j = (short)paramCHAR.oracleId();

      this.currentRowBinders[i] = this.theSetCHARBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      CharacterSet localCharacterSet = this.currentRowFormOfUse[i] == 2 ? this.connection.setCHARNCharSetObj : this.connection.setCHARCharSetObj;
      byte[] arrayOfByte1;
      if ((localCharacterSet != null) && (localCharacterSet.getOracleId() != j))
      {
        byte[] arrayOfByte2 = paramCHAR.shareBytes();

        arrayOfByte1 = localCharacterSet.convert(paramCHAR.getCharacterSet(), arrayOfByte2, 0, arrayOfByte2.length);
      }
      else {
        arrayOfByte1 = paramCHAR.getBytes();
      }
      this.parameterDatum[this.currentRank][i] = arrayOfByte1;
      this.currentRowCharLens[i] = ((arrayOfByte1.length + 1 >> 1) + 1);
    }

    if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      if (this.currentRowCharLens[i] < this.minVcsBindSize)
        this.currentRowCharLens[i] = this.minVcsBindSize;
    }
  }

  public void setCHARAtName(String paramString, CHAR paramCHAR)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setCHAR(k + 1, paramCHAR);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setDATE(int paramInt, DATE paramDATE)
    throws SQLException
  {
    setDATEInternal(paramInt, paramDATE);
  }

  void setDATEInternal(int paramInt, DATE paramDATE) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    if (paramDATE == null)
    {
      this.currentRowBinders[i] = this.theDateNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theOracleDateBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramDATE.getBytes();
    }
  }

  public void setDATEAtName(String paramString, DATE paramDATE)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setDATE(k + 1, paramDATE);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setNUMBER(int paramInt, NUMBER paramNUMBER)
    throws SQLException
  {
    setNUMBERInternal(paramInt, paramNUMBER);
  }

  void setNUMBERInternal(int paramInt, NUMBER paramNUMBER) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    if (paramNUMBER == null)
    {
      this.currentRowBinders[i] = this.theVarnumNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theOracleNumberBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramNUMBER.getBytes();
    }
  }

  public void setNUMBERAtName(String paramString, NUMBER paramNUMBER)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setNUMBER(k + 1, paramNUMBER);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBLOB(int paramInt, BLOB paramBLOB)
    throws SQLException
  {
    setBLOBInternal(paramInt, paramBLOB);
  }

  void setBLOBInternal(int paramInt, BLOB paramBLOB) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    if (paramBLOB == null) {
      this.currentRowBinders[i] = this.theBlobNullBinder;
    }
    else {
      this.currentRowBinders[i] = this.theBlobBinder;

      if (this.parameterDatum == null) {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramBLOB.getBytes();
    }
  }

  public void setBLOBAtName(String paramString, BLOB paramBLOB)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBLOB(k + 1, paramBLOB);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    setBLOBInternal(paramInt, (BLOB)paramBlob);
  }

  void setBlobInternal(int paramInt, Blob paramBlob) throws SQLException
  {
    setBLOBInternal(paramInt, (BLOB)paramBlob);
  }

  public void setBlobAtName(String paramString, Blob paramBlob)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBlob(k + 1, paramBlob);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setCLOB(int paramInt, CLOB paramCLOB)
    throws SQLException
  {
    setCLOBInternal(paramInt, paramCLOB);
  }

  void setCLOBInternal(int paramInt, CLOB paramCLOB) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    if (paramCLOB == null) {
      this.currentRowBinders[i] = this.theClobNullBinder;
    }
    else {
      this.currentRowBinders[i] = this.theClobBinder;

      if (this.parameterDatum == null) {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramCLOB.getBytes();
    }
  }

  public void setCLOBAtName(String paramString, CLOB paramCLOB)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setCLOB(k + 1, paramCLOB);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    setCLOBInternal(paramInt, (CLOB)paramClob);
  }

  void setClobInternal(int paramInt, Clob paramClob) throws SQLException
  {
    setCLOBInternal(paramInt, (CLOB)paramClob);
  }

  public void setClobAtName(String paramString, Clob paramClob)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setClob(k + 1, paramClob);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBFILE(int paramInt, BFILE paramBFILE)
    throws SQLException
  {
    setBFILEInternal(paramInt, paramBFILE);
  }

  void setBFILEInternal(int paramInt, BFILE paramBFILE) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    this.currentRowCharLens[i] = 0;

    if (paramBFILE == null) {
      this.currentRowBinders[i] = this.theBfileNullBinder;
    }
    else {
      this.currentRowBinders[i] = this.theBfileBinder;

      if (this.parameterDatum == null) {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramBFILE.getBytes();
    }
  }

  public void setBFILEAtName(String paramString, BFILE paramBFILE)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBFILE(k + 1, paramBFILE);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setBfile(int paramInt, BFILE paramBFILE)
    throws SQLException
  {
    setBFILEInternal(paramInt, paramBFILE);
  }

  void setBfileInternal(int paramInt, BFILE paramBFILE) throws SQLException
  {
    setBFILEInternal(paramInt, paramBFILE);
  }

  public void setBfileAtName(String paramString, BFILE paramBFILE)
    throws SQLException
  {
    setBFILEAtName(paramString, paramBFILE);
  }

  public synchronized void setBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    setBytesInternal(paramInt, paramArrayOfByte);
  }

  void setBytesInternal(int paramInt, byte[] paramArrayOfByte) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions))
      DatabaseError.throwSqlException(3);
    int j = paramArrayOfByte != null ? paramArrayOfByte.length : 0;
    if (j == 0)
    {
      setNullInternal(paramInt, -2);
    }
    else if (this.sqlKind == 1)
    {
      if (j > this.maxRawBytesPlsql)
        setBytesForBlobCritical(paramInt, paramArrayOfByte);
      else
        basicBindBytes(paramInt, paramArrayOfByte);
    }
    else if (this.sqlKind == 4)
    {
      if (j > this.maxRawBytesPlsql)
        setBytesForBlobCritical(paramInt, paramArrayOfByte);
      else {
        basicBindBytes(paramInt, paramArrayOfByte);
      }

    }
    else if (j > this.maxRawBytesSql)
    {
      bindBytesAsStream(paramInt, paramArrayOfByte);
    }
    else
      basicBindBytes(paramInt, paramArrayOfByte);
  }

  void bindBytesAsStream(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = paramArrayOfByte.length;
    byte[] arrayOfByte = new byte[i];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, i);
    basicBindBinaryStream(paramInt, new ByteArrayInputStream(arrayOfByte), i);
  }

  void basicBindBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = paramInt - 1;
    Binder localBinder = (this.sqlKind == 1) || (this.sqlKind == 4) ? this.thePlsqlRawBinder : this.theRawBinder;

    this.currentRowBinders[i] = localBinder;

    if (this.parameterDatum == null) {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDatum[this.currentRank][i] = paramArrayOfByte;
    this.currentRowCharLens[i] = 0;
  }

  void basicBindBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    int i = paramInt1 - 1;
    this.currentRowBinders[i] = this.theLongRawStreamBinder;

    if (this.parameterStream == null) {
      this.parameterStream = new InputStream[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterStream[this.currentRank][i] = this.connection.conversion.ConvertStream(paramInputStream, 6, paramInt2);

    this.currentRowCharLens[i] = 0;
  }

  public void setBytesAtName(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBytes(k + 1, paramArrayOfByte);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setBytesForBlob(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    if (paramArrayOfByte == null)
    {
      setNull(paramInt, -2);
      return;
    }
    int i = paramArrayOfByte.length;
    if (i == 0)
    {
      setNull(paramInt, -2);
      return;
    }
    if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      if (i <= this.maxRawBytesPlsql)
      {
        setBytes(paramInt, paramArrayOfByte);
      }
      else
      {
        setBytesForBlobCritical(paramInt, paramArrayOfByte);
      }

    }
    else if (i <= this.maxRawBytesSql)
    {
      setBytes(paramInt, paramArrayOfByte);
    }
    else
    {
      setBytesForBlobCritical(paramInt, paramArrayOfByte);
    }
  }

  void setBytesForBlobCritical(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        BLOB localBLOB = BLOB.createTemporary(this.connection, true, 10);

        localBLOB.putBytes(1L, paramArrayOfByte);
        addToTempLobsToFree(localBLOB);
        setBLOBInternal(paramInt, localBLOB);
      }
    }
  }

  void setBinaryStreamContentsForBlobCritical(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        BLOB localBLOB = BLOB.createTemporary(this.connection, true, 10);

        OracleBlobOutputStream localOracleBlobOutputStream = (OracleBlobOutputStream)localBLOB.setBinaryStream(1L);
        int i = localBLOB.getBufferSize();
        byte[] arrayOfByte = new byte[i];
        int j = paramInt2;
        try {
          while (j >= i)
          {
            paramInputStream.read(arrayOfByte);
            localOracleBlobOutputStream.write(arrayOfByte);
            j -= i;
          }
          if (j > 0)
          {
            paramInputStream.read(arrayOfByte, 0, j);
            localOracleBlobOutputStream.write(arrayOfByte, 0, j);
          }
          localOracleBlobOutputStream.flush(); } catch (IOException localIOException) {
        }
        addToTempLobsToFree(localBLOB);
        setBLOBInternal(paramInt1, localBLOB);
      }
    }
  }

  public void setBytesForBlobAtName(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setBytesForBlob(k + 1, paramArrayOfByte);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setInternalBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws SQLException
  {
    setInternalBytesInternal(paramInt1, paramArrayOfByte, paramInt2);
  }

  void setInternalBytesInternal(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public synchronized void setDate(int paramInt, Date paramDate)
    throws SQLException
  {
    setDateInternal(paramInt, paramDate);
  }

  void setDateInternal(int paramInt, Date paramDate) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramDate == null) {
      this.currentRowBinders[i] = this.theDateNullBinder;
    }
    else {
      this.currentRowBinders[i] = this.theDateBinder;

      if (this.parameterDate == null) {
        this.parameterDate = new Date[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDate[this.currentRank][i] = paramDate;
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setDateAtName(String paramString, Date paramDate)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setDate(k + 1, paramDate);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setTime(int paramInt, Time paramTime)
    throws SQLException
  {
    setTimeInternal(paramInt, paramTime);
  }

  void setTimeInternal(int paramInt, Time paramTime) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramTime == null) {
      this.currentRowBinders[i] = this.theDateNullBinder;
    }
    else {
      this.currentRowBinders[i] = this.theTimeBinder;

      if (this.parameterTime == null) {
        this.parameterTime = new Time[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterTime[this.currentRank][i] = paramTime;
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setTimeAtName(String paramString, Time paramTime)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setTime(k + 1, paramTime);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    setTimestampInternal(paramInt, paramTimestamp);
  }

  void setTimestampInternal(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    if (this.connection.v8Compatible)
    {
      if (paramTimestamp == null)
      {
        setDATEInternal(paramInt, null);
      }
      else
      {
        DATE localDATE = new DATE(paramTimestamp);

        setDATEInternal(paramInt, localDATE);
      }
    }
    else
    {
      int i = paramInt - 1;

      if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
        DatabaseError.throwSqlException(3);
      }

      if (paramTimestamp == null) {
        this.currentRowBinders[i] = this.theTimestampNullBinder;
      }
      else {
        this.currentRowBinders[i] = this.theTimestampBinder;

        if (this.parameterTimestamp == null) {
          this.parameterTimestamp = new Timestamp[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
        }

        this.parameterTimestamp[this.currentRank][i] = paramTimestamp;
      }

      this.currentRowCharLens[i] = 0;
    }
  }

  public void setTimestampAtName(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setTimestamp(k + 1, paramTimestamp);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setINTERVALYM(int paramInt, INTERVALYM paramINTERVALYM)
    throws SQLException
  {
    setINTERVALYMInternal(paramInt, paramINTERVALYM);
  }

  void setINTERVALYMInternal(int paramInt, INTERVALYM paramINTERVALYM) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramINTERVALYM == null)
    {
      this.currentRowBinders[i] = this.theIntervalYMNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theIntervalYMBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramINTERVALYM.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setINTERVALYMAtName(String paramString, INTERVALYM paramINTERVALYM)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setINTERVALYM(k + 1, paramINTERVALYM);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setINTERVALDS(int paramInt, INTERVALDS paramINTERVALDS)
    throws SQLException
  {
    setINTERVALDSInternal(paramInt, paramINTERVALDS);
  }

  void setINTERVALDSInternal(int paramInt, INTERVALDS paramINTERVALDS) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramINTERVALDS == null)
    {
      this.currentRowBinders[i] = this.theIntervalDSNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theIntervalDSBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramINTERVALDS.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setINTERVALDSAtName(String paramString, INTERVALDS paramINTERVALDS)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setINTERVALDS(k + 1, paramINTERVALDS);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setTIMESTAMP(int paramInt, TIMESTAMP paramTIMESTAMP)
    throws SQLException
  {
    setTIMESTAMPInternal(paramInt, paramTIMESTAMP);
  }

  void setTIMESTAMPInternal(int paramInt, TIMESTAMP paramTIMESTAMP) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramTIMESTAMP == null)
    {
      this.currentRowBinders[i] = this.theTimestampNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theOracleTimestampBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramTIMESTAMP.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setTIMESTAMPAtName(String paramString, TIMESTAMP paramTIMESTAMP)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setTIMESTAMP(k + 1, paramTIMESTAMP);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setTIMESTAMPTZ(int paramInt, TIMESTAMPTZ paramTIMESTAMPTZ)
    throws SQLException
  {
    setTIMESTAMPTZInternal(paramInt, paramTIMESTAMPTZ);
  }

  void setTIMESTAMPTZInternal(int paramInt, TIMESTAMPTZ paramTIMESTAMPTZ)
    throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramTIMESTAMPTZ == null)
    {
      this.currentRowBinders[i] = this.theTSTZNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theTSTZBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramTIMESTAMPTZ.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setTIMESTAMPTZAtName(String paramString, TIMESTAMPTZ paramTIMESTAMPTZ)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setTIMESTAMPTZ(k + 1, paramTIMESTAMPTZ);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setTIMESTAMPLTZ(int paramInt, TIMESTAMPLTZ paramTIMESTAMPLTZ)
    throws SQLException
  {
    setTIMESTAMPLTZInternal(paramInt, paramTIMESTAMPLTZ);
  }

  void setTIMESTAMPLTZInternal(int paramInt, TIMESTAMPLTZ paramTIMESTAMPLTZ)
    throws SQLException
  {
    if (this.connection.getSessionTimeZone() == null)
    {
      DatabaseError.throwSqlException(105);
    }

    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramTIMESTAMPLTZ == null)
    {
      this.currentRowBinders[i] = this.theTSLTZNullBinder;
    }
    else
    {
      this.currentRowBinders[i] = this.theTSLTZBinder;

      if (this.parameterDatum == null)
      {
        this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterDatum[this.currentRank][i] = paramTIMESTAMPLTZ.getBytes();
    }

    this.currentRowCharLens[i] = 0;
  }

  public void setTIMESTAMPLTZAtName(String paramString, TIMESTAMPLTZ paramTIMESTAMPLTZ)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setTIMESTAMPLTZ(k + 1, paramTIMESTAMPLTZ);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public synchronized void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    setAsciiStreamInternal(paramInt1, paramInputStream, paramInt2);
  }

  void setAsciiStreamInternal(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }

    set_execute_batch(1);

    if (paramInputStream == null)
      basicBindNullString(paramInt1);
    else if ((this.userRsetType != 1) && (paramInt2 > this.maxVcsCharsSql))
      DatabaseError.throwSqlException(169);
    else if (this.currentRowFormOfUse[i] == 1)
    {
      if ((this.sqlKind == 1) || (this.sqlKind == 4))
      {
        if (paramInt2 <= this.maxVcsCharsPlsql)
        {
          setAsciiStreamContentsForStringInternal(paramInt1, paramInputStream, paramInt2);
        }
        else
        {
          setAsciiStreamContentsForClobCritical(paramInt1, paramInputStream, paramInt2);
        }

      }
      else if (paramInt2 <= this.maxVcsCharsSql)
      {
        setAsciiStreamContentsForStringInternal(paramInt1, paramInputStream, paramInt2);
      }
      else
      {
        basicBindAsciiStream(paramInt1, paramInputStream, paramInt2);
      }

    }
    else if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      if (paramInt2 <= this.maxVcsNCharsPlsql)
      {
        setAsciiStreamContentsForStringInternal(paramInt1, paramInputStream, paramInt2);
      }
      else
      {
        setAsciiStreamContentsForClobCritical(paramInt1, paramInputStream, paramInt2);
      }

    }
    else if (paramInt2 <= this.maxVcsCharsSql)
    {
      setAsciiStreamContentsForStringInternal(paramInt1, paramInputStream, paramInt2);
    }
    else
    {
      setAsciiStreamContentsForClobCritical(paramInt1, paramInputStream, paramInt2);
    }
  }

  void basicBindAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    if (this.userRsetType != 1)
      DatabaseError.throwSqlException(169);
    int i = paramInt1 - 1;
    this.currentRowBinders[i] = this.theLongStreamBinder;

    if (this.parameterStream == null) {
      this.parameterStream = new InputStream[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterStream[this.currentRank][i] = this.connection.conversion.ConvertStream(paramInputStream, 5, paramInt2);

    this.currentRowCharLens[i] = 0;
  }

  void setAsciiStreamContentsForStringInternal(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    byte[] arrayOfByte = new byte[paramInt2];
    int i = 0;
    try
    {
      i = paramInputStream.read(arrayOfByte, 0, paramInt2);

      if (i == -1) {
        i = 0;
      }

    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    if (i == 0) basicBindNullString(paramInt1);

    char[] arrayOfChar = new char[paramInt2];

    DBConversion.asciiBytesToJavaChars(arrayOfByte, i, arrayOfChar);

    basicBindString(paramInt1, new String(arrayOfChar));
  }

  public void setAsciiStreamAtName(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);
    int j = 1;

    for (int k = 0; k < i; k++)
    {
      if (arrayOfString[k] != str)
        continue;
      if (j != 0)
      {
        setAsciiStream(k + 1, paramInputStream, paramInt);

        j = 0;
      }
      else
      {
        DatabaseError.throwSqlException(135);
      }
    }
  }

  public synchronized void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    setBinaryStreamInternal(paramInt1, paramInputStream, paramInt2);
  }

  void setBinaryStreamInternal(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }

    set_execute_batch(1);

    if (paramInputStream == null)
      setRAWInternal(paramInt1, null);
    else if ((this.userRsetType != 1) && (paramInt2 > this.maxRawBytesSql))
      DatabaseError.throwSqlException(169);
    else if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      if (paramInt2 > this.maxRawBytesPlsql)
      {
        setBinaryStreamContentsForBlobCritical(paramInt1, paramInputStream, paramInt2);
      }
      else
      {
        setBinaryStreamContentsForByteArrayInternal(paramInt1, paramInputStream, paramInt2);
      }

    }
    else if (paramInt2 > this.maxRawBytesSql)
    {
      basicBindBinaryStream(paramInt1, paramInputStream, paramInt2);
    }
    else
    {
      setBinaryStreamContentsForByteArrayInternal(paramInt1, paramInputStream, paramInt2);
    }
  }

  void setBinaryStreamContentsForByteArrayInternal(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    Object localObject = new byte[paramInt2];
    int i = 0;
    try
    {
      i = paramInputStream.read(localObject, 0, paramInt2);

      if (i == -1)
        i = 0;
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    if (i != paramInt2)
    {
      byte[] arrayOfByte = new byte[i];

      System.arraycopy(localObject, 0, arrayOfByte, 0, i);

      localObject = arrayOfByte;
    }
    setBytesInternal(paramInt1, localObject);
  }

  public void setBinaryStreamAtName(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);
    int j = 1;

    for (int k = 0; k < i; k++)
    {
      if (arrayOfString[k] != str)
        continue;
      if (j != 0)
      {
        setBinaryStream(k + 1, paramInputStream, paramInt);

        j = 0;
      }
      else
      {
        DatabaseError.throwSqlException(135);
      }
    }
  }

  /** @deprecated */
  public synchronized void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    setUnicodeStreamInternal(paramInt1, paramInputStream, paramInt2);
  }

  void setUnicodeStreamInternal(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }

    set_execute_batch(1);

    if (paramInputStream == null) {
      setStringInternal(paramInt1, null);
    } else if ((this.userRsetType != 1) && (paramInt2 > this.maxVcsCharsSql)) {
      DatabaseError.throwSqlException(169);
    } else if ((this.sqlKind == 1) || (this.sqlKind == 4) || (paramInt2 <= this.maxVcsCharsSql))
    {
      byte[] arrayOfByte = new byte[paramInt2];
      int j = 0;
      try
      {
        j = paramInputStream.read(arrayOfByte, 0, paramInt2);

        if (j == -1)
          j = 0;
      }
      catch (IOException localIOException)
      {
        DatabaseError.throwSqlException(localIOException);
      }

      char[] arrayOfChar = new char[j >> 1];

      DBConversion.ucs2BytesToJavaChars(arrayOfByte, j, arrayOfChar);

      setStringInternal(paramInt1, new String(arrayOfChar));
    }
    else
    {
      this.currentRowBinders[i] = this.theLongStreamBinder;

      if (this.parameterStream == null) {
        this.parameterStream = new InputStream[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
      }

      this.parameterStream[this.currentRank][i] = this.connection.conversion.ConvertStream(paramInputStream, 4, paramInt2);

      this.currentRowCharLens[i] = 0;
    }
  }

  public void setUnicodeStreamAtName(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);
    int j = 1;

    for (int k = 0; k < i; k++)
    {
      if (arrayOfString[k] != str)
        continue;
      if (j != 0)
      {
        setUnicodeStream(k + 1, paramInputStream, paramInt);

        j = 0;
      }
      else
      {
        DatabaseError.throwSqlException(135);
      }
    }
  }

  /** @deprecated */
  public void setCustomDatum(int paramInt, CustomDatum paramCustomDatum)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        setObjectInternal(paramInt, this.connection.toDatum(paramCustomDatum));
      }
    }
  }

  void setCustomDatumInternal(int paramInt, CustomDatum paramCustomDatum)
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        Datum localDatum = this.connection.toDatum(paramCustomDatum);
        int i = sqlTypeForObject(localDatum);

        setObjectCritical(paramInt, localDatum, i, 0);

        this.currentRowCharLens[(paramInt - 1)] = 0;
      }
    }
  }

  public void setCustomDatumAtName(String paramString, CustomDatum paramCustomDatum)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setCustomDatumInternal(k + 1, paramCustomDatum);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setORAData(int paramInt, ORAData paramORAData)
    throws SQLException
  {
    setORADataInternal(paramInt, paramORAData);
  }

  void setORADataInternal(int paramInt, ORAData paramORAData) throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        Datum localDatum = paramORAData.toDatum(this.connection);
        int i = sqlTypeForObject(localDatum);

        setObjectCritical(paramInt, localDatum, i, 0);

        this.currentRowCharLens[(paramInt - 1)] = 0;
      }
    }
  }

  public void setORADataAtName(String paramString, ORAData paramORAData)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setORADataInternal(k + 1, paramORAData);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    setObjectInternal(paramInt1, paramObject, paramInt2, paramInt3);
  }

  void setObjectInternal(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    if ((paramObject == null) && (paramInt2 != 2002) && (paramInt2 != 2008) && (paramInt2 != 2003) && (paramInt2 != 2007) && (paramInt2 != 2006))
    {
      setNullInternal(paramInt1, paramInt2);
    }
    else
    {
      if ((paramInt2 == 2002) || (paramInt2 == 2008) || (paramInt2 == 2003))
      {
        synchronized (this.connection)
        {
          synchronized (this)
          {
            setObjectCritical(paramInt1, paramObject, paramInt2, paramInt3);

            this.currentRowCharLens[(paramInt1 - 1)] = 0;
          }
        }

      }

      synchronized (this)
      {
        setObjectCritical(paramInt1, paramObject, paramInt2, paramInt3);
      }
    }
  }

  void setObjectCritical(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    switch (paramInt2)
    {
    case 1:
      if ((paramObject instanceof CHAR))
        setCHARInternal(paramInt1, (CHAR)paramObject);
      else if ((paramObject instanceof String))
        setStringInternal(paramInt1, (String)paramObject);
      else if ((paramObject instanceof Boolean)) {
        setStringInternal(paramInt1, "" + (((Boolean)paramObject).booleanValue() ? 1 : 0));
      }
      else if ((paramObject instanceof Integer))
        setStringInternal(paramInt1, "" + ((Integer)paramObject).intValue());
      else if ((paramObject instanceof Long))
        setStringInternal(paramInt1, "" + ((Long)paramObject).longValue());
      else if ((paramObject instanceof Float))
        setStringInternal(paramInt1, "" + ((Float)paramObject).floatValue());
      else if ((paramObject instanceof Double))
        setStringInternal(paramInt1, "" + ((Double)paramObject).doubleValue());
      else if ((paramObject instanceof BigDecimal))
        setStringInternal(paramInt1, ((BigDecimal)paramObject).toString());
      else if ((paramObject instanceof Date))
        setStringInternal(paramInt1, "" + ((Date)paramObject).toString());
      else if ((paramObject instanceof Time))
        setStringInternal(paramInt1, "" + ((Time)paramObject).toString());
      else if ((paramObject instanceof Timestamp))
        setStringInternal(paramInt1, "" + ((Timestamp)paramObject).toString());
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 12:
      if ((paramObject instanceof String))
        setStringInternal(paramInt1, (String)paramObject);
      else if ((paramObject instanceof Boolean)) {
        setStringInternal(paramInt1, "" + (((Boolean)paramObject).booleanValue() ? 1 : 0));
      }
      else if ((paramObject instanceof Integer))
        setStringInternal(paramInt1, "" + ((Integer)paramObject).intValue());
      else if ((paramObject instanceof Long))
        setStringInternal(paramInt1, "" + ((Long)paramObject).longValue());
      else if ((paramObject instanceof Float))
        setStringInternal(paramInt1, "" + ((Float)paramObject).floatValue());
      else if ((paramObject instanceof Double))
        setStringInternal(paramInt1, "" + ((Double)paramObject).doubleValue());
      else if ((paramObject instanceof BigDecimal))
        setStringInternal(paramInt1, ((BigDecimal)paramObject).toString());
      else if ((paramObject instanceof Date))
        setStringInternal(paramInt1, "" + ((Date)paramObject).toString());
      else if ((paramObject instanceof Time))
        setStringInternal(paramInt1, "" + ((Time)paramObject).toString());
      else if ((paramObject instanceof Timestamp))
        setStringInternal(paramInt1, "" + ((Timestamp)paramObject).toString());
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 999:
      setFixedCHARInternal(paramInt1, (String)paramObject);

      break;
    case -1:
      if ((paramObject instanceof String))
        setStringInternal(paramInt1, (String)paramObject);
      else if ((paramObject instanceof Boolean)) {
        setStringInternal(paramInt1, "" + (((Boolean)paramObject).booleanValue() ? 1 : 0));
      }
      else if ((paramObject instanceof Integer)) {
        setStringInternal(paramInt1, "" + ((Integer)paramObject).intValue());
      }
      else if ((paramObject instanceof Long)) {
        setStringInternal(paramInt1, "" + ((Long)paramObject).longValue());
      }
      else if ((paramObject instanceof Float)) {
        setStringInternal(paramInt1, "" + ((Float)paramObject).floatValue());
      }
      else if ((paramObject instanceof Double)) {
        setStringInternal(paramInt1, "" + ((Double)paramObject).doubleValue());
      }
      else if ((paramObject instanceof BigDecimal))
        setStringInternal(paramInt1, ((BigDecimal)paramObject).toString());
      else if ((paramObject instanceof Date))
        setStringInternal(paramInt1, "" + ((Date)paramObject).toString());
      else if ((paramObject instanceof Time))
        setStringInternal(paramInt1, "" + ((Time)paramObject).toString());
      else if ((paramObject instanceof Timestamp)) {
        setStringInternal(paramInt1, "" + ((Timestamp)paramObject).toString());
      }
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 2:
      if ((paramObject instanceof NUMBER))
        setNUMBERInternal(paramInt1, (NUMBER)paramObject);
      else if ((paramObject instanceof Integer))
        setIntInternal(paramInt1, ((Integer)paramObject).intValue());
      else if ((paramObject instanceof Long))
        setLongInternal(paramInt1, ((Long)paramObject).longValue());
      else if ((paramObject instanceof Float))
        setFloatInternal(paramInt1, ((Float)paramObject).floatValue());
      else if ((paramObject instanceof Double))
        setDoubleInternal(paramInt1, ((Double)paramObject).doubleValue());
      else if ((paramObject instanceof BigDecimal))
        setBigDecimalInternal(paramInt1, (BigDecimal)paramObject);
      else if ((paramObject instanceof String))
        setNUMBERInternal(paramInt1, new NUMBER((String)paramObject, 0));
      else if ((paramObject instanceof Boolean))
        setIntInternal(paramInt1, ((Boolean)paramObject).booleanValue() ? 1 : 0);
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 3:
      if ((paramObject instanceof BigDecimal))
        setBigDecimalInternal(paramInt1, (BigDecimal)paramObject);
      else if ((paramObject instanceof Number)) {
        setBigDecimalInternal(paramInt1, new BigDecimal(((Number)paramObject).doubleValue()));
      }
      else if ((paramObject instanceof NUMBER))
        setBigDecimalInternal(paramInt1, ((NUMBER)paramObject).bigDecimalValue());
      else if ((paramObject instanceof String))
        setBigDecimalInternal(paramInt1, new BigDecimal((String)paramObject));
      else if ((paramObject instanceof Boolean)) {
        setBigDecimalInternal(paramInt1, new BigDecimal(((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D));
      }
      else
      {
        DatabaseError.throwSqlException(132);
      }

      break;
    case -7:
      if ((paramObject instanceof Boolean)) {
        setByteInternal(paramInt1, (byte)(((Boolean)paramObject).booleanValue() ? 1 : 0));
      }
      else if ((paramObject instanceof String)) {
        setByteInternal(paramInt1, (byte)(("true".equalsIgnoreCase((String)paramObject)) || ("1".equals(paramObject)) ? 1 : 0));
      }
      else if ((paramObject instanceof Number))
        setIntInternal(paramInt1, ((Number)paramObject).byteValue() != 0 ? 1 : 0);
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case -6:
      if ((paramObject instanceof Number))
        setByteInternal(paramInt1, ((Number)paramObject).byteValue());
      else if ((paramObject instanceof String))
        setByteInternal(paramInt1, Byte.parseByte((String)paramObject));
      else if ((paramObject instanceof Boolean)) {
        setByteInternal(paramInt1, (byte)(((Boolean)paramObject).booleanValue() ? 1 : 0));
      }
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 5:
      if ((paramObject instanceof Number))
        setShortInternal(paramInt1, ((Number)paramObject).shortValue());
      else if ((paramObject instanceof String))
        setShortInternal(paramInt1, Short.parseShort((String)paramObject));
      else if ((paramObject instanceof Boolean)) {
        setShortInternal(paramInt1, (short)(((Boolean)paramObject).booleanValue() ? 1 : 0));
      }
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 4:
      if ((paramObject instanceof Number))
        setIntInternal(paramInt1, ((Number)paramObject).intValue());
      else if ((paramObject instanceof String))
        setIntInternal(paramInt1, Integer.parseInt((String)paramObject));
      else if ((paramObject instanceof Boolean))
        setIntInternal(paramInt1, ((Boolean)paramObject).booleanValue() ? 1 : 0);
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case -5:
      if ((paramObject instanceof Number))
        setLongInternal(paramInt1, ((Number)paramObject).longValue());
      else if ((paramObject instanceof String))
        setLongInternal(paramInt1, Long.parseLong((String)paramObject));
      else if ((paramObject instanceof Boolean))
        setLongInternal(paramInt1, ((Boolean)paramObject).booleanValue() ? 1L : 0L);
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 7:
      if ((paramObject instanceof Number))
        setFloatInternal(paramInt1, ((Number)paramObject).floatValue());
      else if ((paramObject instanceof String))
        setFloatInternal(paramInt1, Float.valueOf((String)paramObject).floatValue());
      else if ((paramObject instanceof Boolean))
        setFloatInternal(paramInt1, ((Boolean)paramObject).booleanValue() ? 1.0F : 0.0F);
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 6:
    case 8:
      if ((paramObject instanceof Number))
        setDoubleInternal(paramInt1, ((Number)paramObject).doubleValue());
      else if ((paramObject instanceof String)) {
        setDoubleInternal(paramInt1, Double.valueOf((String)paramObject).doubleValue());
      }
      else if ((paramObject instanceof Boolean)) {
        setDoubleInternal(paramInt1, ((Boolean)paramObject).booleanValue() ? 1.0D : 0.0D);
      }
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case -2:
      if ((paramObject instanceof RAW))
        setRAWInternal(paramInt1, (RAW)paramObject);
      else {
        setBytesInternal(paramInt1, (byte[])paramObject);
      }
      break;
    case -3:
      setBytesInternal(paramInt1, (byte[])paramObject);

      break;
    case -4:
      setBytesInternal(paramInt1, (byte[])paramObject);

      break;
    case 91:
      if ((paramObject instanceof DATE))
        setDATEInternal(paramInt1, (DATE)paramObject);
      else if ((paramObject instanceof Date))
        setDateInternal(paramInt1, (Date)paramObject);
      else if ((paramObject instanceof Timestamp))
        setTimestampInternal(paramInt1, (Timestamp)paramObject);
      else if ((paramObject instanceof String))
        setDateInternal(paramInt1, Date.valueOf((String)paramObject));
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 92:
      if ((paramObject instanceof Time))
        setTimeInternal(paramInt1, (Time)paramObject);
      else if ((paramObject instanceof Timestamp)) {
        setTimeInternal(paramInt1, new Time(((Timestamp)paramObject).getTime()));
      }
      else if ((paramObject instanceof Date))
        setTimeInternal(paramInt1, new Time(((Date)paramObject).getTime()));
      else if ((paramObject instanceof String))
        setTimeInternal(paramInt1, Time.valueOf((String)paramObject));
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case 93:
      if ((paramObject instanceof TIMESTAMP))
        setTIMESTAMPInternal(paramInt1, (TIMESTAMP)paramObject);
      else if ((paramObject instanceof Timestamp))
        setTimestampInternal(paramInt1, (Timestamp)paramObject);
      else if ((paramObject instanceof Date))
        setDateInternal(paramInt1, (Date)paramObject);
      else if ((paramObject instanceof DATE))
        setDATEInternal(paramInt1, (DATE)paramObject);
      else if ((paramObject instanceof String))
        setTimestampInternal(paramInt1, Timestamp.valueOf((String)paramObject));
      else {
        DatabaseError.throwSqlException(132);
      }

      break;
    case -100:
      setTIMESTAMPInternal(paramInt1, (TIMESTAMP)paramObject);

      break;
    case -101:
      setTIMESTAMPTZInternal(paramInt1, (TIMESTAMPTZ)paramObject);

      break;
    case -102:
      setTIMESTAMPLTZInternal(paramInt1, (TIMESTAMPLTZ)paramObject);

      break;
    case -103:
      setINTERVALYMInternal(paramInt1, (INTERVALYM)paramObject);

      break;
    case -104:
      setINTERVALDSInternal(paramInt1, (INTERVALDS)paramObject);

      break;
    case -8:
      setROWIDInternal(paramInt1, (ROWID)paramObject);

      break;
    case 100:
      setBinaryFloatInternal(paramInt1, (BINARY_FLOAT)paramObject);

      break;
    case 101:
      setBinaryDoubleInternal(paramInt1, (BINARY_DOUBLE)paramObject);

      break;
    case 2004:
      setBLOBInternal(paramInt1, (BLOB)paramObject);

      break;
    case 2005:
      setCLOBInternal(paramInt1, (CLOB)paramObject);

      break;
    case -13:
      setBFILEInternal(paramInt1, (BFILE)paramObject);

      break;
    case 2002:
    case 2008:
      setSTRUCTInternal(paramInt1, STRUCT.toSTRUCT(paramObject, this.connection));

      break;
    case 2003:
      setARRAYInternal(paramInt1, ARRAY.toARRAY(paramObject, this.connection));

      break;
    case 2007:
      setOPAQUEInternal(paramInt1, (OPAQUE)paramObject);

      break;
    case 2006:
      setREFInternal(paramInt1, (REF)paramObject);

      break;
    default:
      DatabaseError.throwSqlException(4);
    }
  }

  public void setObjectAtName(String paramString, Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setObjectInternal(k + 1, paramObject);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    setObjectInternal(paramInt1, paramObject, paramInt2, 0);
  }

  void setObjectInternal(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    setObjectInternal(paramInt1, paramObject, paramInt2, 0);
  }

  public void setObjectAtName(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    setObjectAtName(paramString, paramObject, paramInt, 0);
  }

  public void setRefType(int paramInt, REF paramREF)
    throws SQLException
  {
    setREFInternal(paramInt, paramREF);
  }

  void setRefTypeInternal(int paramInt, REF paramREF) throws SQLException
  {
    setREFInternal(paramInt, paramREF);
  }

  public void setRefTypeAtName(String paramString, REF paramREF)
    throws SQLException
  {
    setREFAtName(paramString, paramREF);
  }

  public void setRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    setREFInternal(paramInt, (REF)paramRef);
  }

  void setRefInternal(int paramInt, Ref paramRef)
    throws SQLException
  {
    setREFInternal(paramInt, (REF)paramRef);
  }

  public void setRefAtName(String paramString, Ref paramRef)
    throws SQLException
  {
    setREFAtName(paramString, (REF)paramRef);
  }

  public void setREF(int paramInt, REF paramREF)
    throws SQLException
  {
    setREFInternal(paramInt, paramREF);
  }

  void setREFInternal(int paramInt, REF paramREF) throws SQLException
  {
    int i = paramInt - 1;

    if ((i < 0) || (paramInt > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramREF == null)
    {
      DatabaseError.throwSqlException(68);
    }
    else
    {
      synchronized (this.connection)
      {
        synchronized (this)
        {
          setREFCritical(i, paramREF);

          this.currentRowCharLens[i] = 0;
        }
      }
    }
  }

  void setREFCritical(int paramInt, REF paramREF)
    throws SQLException
  {
    StructDescriptor localStructDescriptor = paramREF.getDescriptor();

    if (localStructDescriptor == null) {
      DatabaseError.throwSqlException(52);
    }

    this.currentRowBinders[paramInt] = this.theRefTypeBinder;

    if (this.parameterDatum == null)
    {
      this.parameterDatum = new byte[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterDatum[this.currentRank][paramInt] = paramREF.getBytes();

    OracleTypeADT localOracleTypeADT = localStructDescriptor.getOracleTypeADT();

    localOracleTypeADT.getTOID();

    if (this.parameterOtype == null)
    {
      this.parameterOtype = new OracleTypeADT[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterOtype[this.currentRank][paramInt] = localOracleTypeADT;
  }

  public void setREFAtName(String paramString, REF paramREF)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setREFInternal(k + 1, paramREF);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setObject(int paramInt, Object paramObject)
    throws SQLException
  {
    setObjectInternal(paramInt, paramObject);
  }

  void setObjectInternal(int paramInt, Object paramObject) throws SQLException
  {
    if ((paramObject instanceof ORAData))
    {
      setORADataInternal(paramInt, (ORAData)paramObject);
    }
    else if ((paramObject instanceof CustomDatum))
    {
      setCustomDatumInternal(paramInt, (CustomDatum)paramObject);
    }
    else
    {
      int i = sqlTypeForObject(paramObject);

      setObjectInternal(paramInt, paramObject, i, 0);
    }
  }

  public void setObjectAtName(String paramString, Object paramObject)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setObjectInternal(k + 1, paramObject);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public void setOracleObject(int paramInt, Datum paramDatum)
    throws SQLException
  {
    setObjectInternal(paramInt, paramDatum);
  }

  void setOracleObjectInternal(int paramInt, Datum paramDatum)
    throws SQLException
  {
    setObjectInternal(paramInt, paramDatum);
  }

  public void setOracleObjectAtName(String paramString, Datum paramDatum)
    throws SQLException
  {
    setObjectAtName(paramString, paramDatum);
  }

  public synchronized void setPlsqlIndexTable(int paramInt1, Object paramObject, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws SQLException
  {
    setPlsqlIndexTableInternal(paramInt1, paramObject, paramInt2, paramInt3, paramInt4, paramInt5);
  }

  void setPlsqlIndexTableInternal(int paramInt1, Object paramObject, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }

    int j = getInternalType(paramInt4);

    Object localObject = null;

    switch (j)
    {
    case 1:
    case 96:
      String[] arrayOfString = null;
      int k = 0;

      if ((paramObject instanceof CHAR[]))
      {
        CHAR[] arrayOfCHAR = (CHAR[])paramObject;
        k = arrayOfCHAR.length;

        arrayOfString = new String[k];

        for (int n = 0; n < k; n++)
        {
          CHAR localCHAR = arrayOfCHAR[n];
          if (localCHAR != null)
            arrayOfString[n] = localCHAR.getString();
        }
      }
      if ((paramObject instanceof String[]))
      {
        arrayOfString = (String[])paramObject;
        k = arrayOfString.length;
      }

      if ((paramInt5 == 0) && (arrayOfString != null)) {
        for (int m = 0; m < k; m++)
        {
          String str = arrayOfString[m];
          if ((str != null) && (paramInt5 < str.length()))
            paramInt5 = str.length();
        }
      }
      localObject = arrayOfString;

      break;
    case 2:
    case 6:
      localObject = OracleTypeNUMBER.toNUMBERArray(paramObject, this.connection, 1L, paramInt3);

      if ((paramInt5 == 0) && (localObject != null))
      {
        paramInt5 = 22;
      }

      this.currentRowCharLens[i] = 0;

      break;
    default:
      DatabaseError.throwSqlException(97);

      return;
    }

    this.currentRowBinders[i] = this.thePlsqlIbtBinder;

    if (this.parameterPlsqlIbt == null) {
      this.parameterPlsqlIbt = new PlsqlIbtBindInfo[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }

    this.parameterPlsqlIbt[this.currentRank][i] = new PlsqlIbtBindInfo(localObject, paramInt2, paramInt3, j, paramInt5);

    this.hasIbtBind = true;
  }

  public synchronized void setPlsqlIndexTableAtName(String paramString, Object paramObject, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setPlsqlIndexTableInternal(k + 1, paramObject, paramInt1, paramInt2, paramInt3, paramInt4);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  void endOfResultSet(boolean paramBoolean)
    throws SQLException
  {
    if (!paramBoolean)
    {
      prepareForNewResults(false, false);
    }
  }

  int sqlTypeForObject(Object paramObject)
  {
    if (paramObject == null)
    {
      return 0;
    }

    if (!(paramObject instanceof Datum))
    {
      if ((paramObject instanceof String))
      {
        return this.fixedString ? 999 : 12;
      }
      if ((paramObject instanceof BigDecimal)) {
        return 2;
      }
      if ((paramObject instanceof Boolean)) {
        return -7;
      }
      if ((paramObject instanceof Integer)) {
        return 4;
      }
      if ((paramObject instanceof Long)) {
        return -5;
      }
      if ((paramObject instanceof Float)) {
        return 6;
      }
      if ((paramObject instanceof Double)) {
        return 8;
      }
      if ((paramObject instanceof byte[])) {
        return -3;
      }

      if ((paramObject instanceof Short)) {
        return 5;
      }
      if ((paramObject instanceof Byte)) {
        return -6;
      }
      if ((paramObject instanceof Date)) {
        return 91;
      }
      if ((paramObject instanceof Time)) {
        return 92;
      }
      if ((paramObject instanceof Timestamp)) {
        return 93;
      }
      if ((paramObject instanceof SQLData)) {
        return 2002;
      }
      if ((paramObject instanceof ObjectData))
        return 2002;
    }
    else
    {
      if ((paramObject instanceof BINARY_FLOAT)) {
        return 100;
      }
      if ((paramObject instanceof BINARY_DOUBLE)) {
        return 101;
      }
      if ((paramObject instanceof BLOB)) {
        return 2004;
      }
      if ((paramObject instanceof CLOB)) {
        return 2005;
      }
      if ((paramObject instanceof BFILE)) {
        return -13;
      }
      if ((paramObject instanceof ROWID)) {
        return -8;
      }
      if ((paramObject instanceof NUMBER)) {
        return 2;
      }
      if ((paramObject instanceof DATE)) {
        return 91;
      }
      if ((paramObject instanceof TIMESTAMP)) {
        return 93;
      }
      if ((paramObject instanceof TIMESTAMPTZ)) {
        return -101;
      }
      if ((paramObject instanceof TIMESTAMPLTZ)) {
        return -102;
      }
      if ((paramObject instanceof REF)) {
        return 2006;
      }
      if ((paramObject instanceof CHAR)) {
        return 1;
      }
      if ((paramObject instanceof RAW)) {
        return -2;
      }
      if ((paramObject instanceof ARRAY)) {
        return 2003;
      }
      if ((paramObject instanceof STRUCT)) {
        return 2002;
      }
      if ((paramObject instanceof OPAQUE)) {
        return 2007;
      }
      if ((paramObject instanceof INTERVALYM)) {
        return -103;
      }
      if ((paramObject instanceof INTERVALDS)) {
        return -104;
      }
    }
    return 1111;
  }

  public synchronized void clearParameters()
    throws SQLException
  {
    this.clearParameters = true;

    for (int i = 0; i < this.numberOfBindPositions; i++)
      this.currentRowBinders[i] = null;
  }

  void printByteArray(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null)
    {
      int j = paramArrayOfByte.length;

      for (int i = 0; i < j; i++)
      {
        int k = paramArrayOfByte[i] & 0xFF;

        if (k >= 16)
          continue;
      }
    }
  }

  public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    setCharacterStreamInternal(paramInt1, paramReader, paramInt2);
  }

  void setCharacterStreamInternal(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    int i = paramInt1 - 1;

    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }

    set_execute_batch(1);

    if (paramReader == null) {
      basicBindNullString(paramInt1);
    } else if ((this.userRsetType != 1) && (paramInt2 > this.maxVcsCharsSql)) {
      DatabaseError.throwSqlException(169);
    } else if (this.currentRowFormOfUse[i] == 1)
    {
      if ((this.sqlKind == 1) || (this.sqlKind == 4))
      {
        if (paramInt2 <= this.maxVcsBytesPlsql) { if (!(paramInt2 > this.maxVcsCharsPlsql & this.isServerCharSetFixedWidth));
        } else {
          setReaderContentsForClobCritical(paramInt1, paramReader, paramInt2); return;
        }
        if (paramInt2 <= this.maxVcsCharsPlsql)
        {
          setReaderContentsForStringInternal(paramInt1, paramReader, paramInt2);
        }
        else
        {
          setReaderContentsForStringOrClobInVariableWidthCase(paramInt1, paramReader, paramInt2, false);
        }

      }
      else if (paramInt2 <= this.maxVcsCharsSql)
      {
        setReaderContentsForStringInternal(paramInt1, paramReader, paramInt2);
      }
      else
      {
        basicBindCharacterStream(paramInt1, paramReader, paramInt2);
      }

    }
    else if ((this.sqlKind == 1) || (this.sqlKind == 4))
    {
      if (paramInt2 <= this.maxVcsBytesPlsql) { if (!(paramInt2 > this.maxVcsNCharsPlsql & this.isServerCharSetFixedWidth));
      } else {
        setReaderContentsForClobCritical(paramInt1, paramReader, paramInt2); return;
      }
      if (paramInt2 <= this.maxVcsNCharsPlsql)
      {
        setReaderContentsForStringInternal(paramInt1, paramReader, paramInt2);
      }
      else
      {
        setReaderContentsForStringOrClobInVariableWidthCase(paramInt1, paramReader, paramInt2, true);
      }

    }
    else if (paramInt2 <= this.maxVcsCharsSql)
    {
      setReaderContentsForStringInternal(paramInt1, paramReader, paramInt2);
    }
    else
    {
      setReaderContentsForClobCritical(paramInt1, paramReader, paramInt2);
    }
  }

  void basicBindCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    int i = paramInt1 - 1;
    this.currentRowBinders[i] = this.theLongStreamBinder;
    if (this.parameterStream == null) {
      this.parameterStream = new InputStream[this.numberOfBindRowsAllocated][this.numberOfBindPositions];
    }
    this.parameterStream[this.currentRank][i] = this.connection.conversion.ConvertStream(paramReader, 7, paramInt2, this.currentRowFormOfUse[i]);

    this.currentRowCharLens[i] = 0;
  }

  void setReaderContentsForStringOrClobInVariableWidthCase(int paramInt1, Reader paramReader, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    Object localObject = new char[paramInt2];
    int i = 0;
    try
    {
      i = paramReader.read(localObject, 0, paramInt2);

      if (i == -1)
        i = 0;
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
    if (i != paramInt2)
    {
      char[] arrayOfChar = new char[i];

      System.arraycopy(localObject, 0, arrayOfChar, 0, i);

      localObject = arrayOfChar;
    }
    int j = this.connection.conversion.encodedByteLength(localObject, paramBoolean);
    if (j < this.maxVcsBytesPlsql)
    {
      setStringInternal(paramInt1, new String(localObject));
    }
    else
    {
      setStringForClobCritical(paramInt1, new String(localObject));
    }
  }

  void setReaderContentsForStringInternal(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    Object localObject = new char[paramInt2];
    int i = 0;
    try
    {
      i = paramReader.read(localObject, 0, paramInt2);

      if (i == -1)
        i = 0;
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    if (i != paramInt2)
    {
      char[] arrayOfChar = new char[i];

      System.arraycopy(localObject, 0, arrayOfChar, 0, i);

      localObject = arrayOfChar;
    }
    setStringInternal(paramInt1, new String(localObject));
  }

  public void setDate(int paramInt, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    setDATEInternal(paramInt, paramDate == null ? null : new DATE(paramDate, paramCalendar));
  }

  void setDateInternal(int paramInt, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    setDATEInternal(paramInt, paramDate == null ? null : new DATE(paramDate, paramCalendar));
  }

  public void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    setDATEInternal(paramInt, paramTime == null ? null : new DATE(paramTime, paramCalendar));
  }

  void setTimeInternal(int paramInt, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    setDATEInternal(paramInt, paramTime == null ? null : new DATE(paramTime, paramCalendar));
  }

  public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    setTimestampInternal(paramInt, paramTimestamp, paramCalendar);
  }

  void setTimestampInternal(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    if (this.connection.v8Compatible)
    {
      if (paramTimestamp == null)
      {
        setDATEInternal(paramInt, null);
      }
      else
      {
        DATE localDATE = new DATE(paramTimestamp, paramCalendar);

        setDATEInternal(paramInt, localDATE);
      }

      return;
    }

    if (paramTimestamp == null)
    {
      setTIMESTAMPInternal(paramInt, null);
    }
    else
    {
      int i = paramTimestamp.getNanos();
      byte[] arrayOfByte;
      if (i == 0)
        arrayOfByte = new byte[7];
      else {
        arrayOfByte = new byte[11];
      }
      if (paramCalendar == null) {
        paramCalendar = Calendar.getInstance();
      }
      paramCalendar.clear();
      paramCalendar.setTime(paramTimestamp);

      int j = paramCalendar.get(1);

      if (paramCalendar.get(0) == 0) {
        j = -(j - 1);
      }
      arrayOfByte[0] = (byte)(j / 100 + 100);
      arrayOfByte[1] = (byte)(j % 100 + 100);
      arrayOfByte[2] = (byte)(paramCalendar.get(2) + 1);
      arrayOfByte[3] = (byte)paramCalendar.get(5);
      arrayOfByte[4] = (byte)(paramCalendar.get(11) + 1);
      arrayOfByte[5] = (byte)(paramCalendar.get(12) + 1);
      arrayOfByte[6] = (byte)(paramCalendar.get(13) + 1);

      if (i != 0)
      {
        arrayOfByte[7] = (byte)(i >> 24);
        arrayOfByte[8] = (byte)(i >> 16 & 0xFF);
        arrayOfByte[9] = (byte)(i >> 8 & 0xFF);
        arrayOfByte[10] = (byte)(i & 0xFF);
      }

      setTIMESTAMPInternal(paramInt, new TIMESTAMP(arrayOfByte));
    }
  }

  public void setCheckBindTypes(boolean paramBoolean)
  {
    this.checkBindTypes = paramBoolean;
  }

  final void setOracleBatchStyle()
    throws SQLException
  {
    if (this.m_batchStyle == 2)
    {
      DatabaseError.throwSqlException(90, "operation cannot be mixed with JDBC-2.0-style batching");
    }
    else if (this.m_batchStyle != 0);
    this.m_batchStyle = 1;
  }

  final void setJdbcBatchStyle()
    throws SQLException
  {
    if (this.m_batchStyle == 1)
    {
      DatabaseError.throwSqlException(90, "operation cannot be mixed with Oracle-style batching");
    }

    this.m_batchStyle = 2;
  }

  final void checkIfJdbcBatchExists()
    throws SQLException
  {
    if (doesJdbcBatchExist())
    {
      DatabaseError.throwSqlException(81, "batch must be either executed or cleared");
    }
  }

  boolean doesJdbcBatchExist()
  {
    return (this.currentRank > 0) && (this.m_batchStyle == 2);
  }

  boolean isJdbcBatchStyle()
  {
    return this.m_batchStyle == 2;
  }

  public synchronized void addBatch()
    throws SQLException
  {
    setJdbcBatchStyle();

    processCompletedBindRow(this.currentRank + 2, (this.currentRank > 0) && ((this.sqlKind == 1) || (this.sqlKind == 4)));

    this.currentRank += 1;
  }

  public synchronized void addBatch(String paramString)
    throws SQLException
  {
    DatabaseError.throwSqlException(23);
  }

  public synchronized void clearBatch()
    throws SQLException
  {
    for (int i = this.currentRank; i >= 0; i--) {
      for (int j = 0; j < this.numberOfBindPositions; j++)
        this.binders[i][j] = null;
    }
    this.currentRank = 0;

    if (this.binders != null) {
      this.currentRowBinders = this.binders[0];
    }
    this.pushedBatches = null;
    this.pushedBatchesTail = null;
    this.firstRowInBatch = 0;

    this.clearParameters = true;
  }

  public int[] executeBatch()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
        int i = 0;

        setJdbcBatchStyle();

        int[] arrayOfInt = new int[this.currentRank];

        if (this.currentRank > 0)
        {
          ensureOpen();

          prepareForNewResults(true, true);

          if (this.sqlKind == 0)
          {
            DatabaseError.throwBatchUpdateException(80, 0, null);
          }

          this.noMoreUpdateCounts = false;

          int j = 0;
          try
          {
            this.connection.needLine();

            if (!this.isOpen)
            {
              this.connection.open(this);

              this.isOpen = true;
            }

            int k = this.currentRank;

            if (this.pushedBatches == null)
            {
              setupBindBuffers(0, this.currentRank);
              executeForRows(false);
            }
            else
            {
              if (this.currentRank > this.firstRowInBatch)
              {
                pushBatch(true);
              }
              boolean bool = this.needToParse;
              do
              {
                PushedBatch localPushedBatch = this.pushedBatches;

                this.currentBatchCharLens = localPushedBatch.currentBatchCharLens;
                this.lastBoundCharLens = localPushedBatch.lastBoundCharLens;
                this.lastBoundNeeded = localPushedBatch.lastBoundNeeded;
                this.currentBatchBindAccessors = localPushedBatch.currentBatchBindAccessors;
                this.needToParse = localPushedBatch.need_to_parse;
                this.currentBatchNeedToPrepareBinds = localPushedBatch.current_batch_need_to_prepare_binds;

                this.firstRowInBatch = localPushedBatch.first_row_in_batch;

                setupBindBuffers(localPushedBatch.first_row_in_batch, localPushedBatch.number_of_rows_to_be_bound);

                this.currentRank = localPushedBatch.number_of_rows_to_be_bound;

                executeForRows(false);

                if ((this.sqlKind == 1) || (this.sqlKind == 4))
                {
                  j += this.validRows;
                  arrayOfInt[(i++)] = this.validRows;
                }

                this.pushedBatches = localPushedBatch.next;
              }

              while (this.pushedBatches != null);

              this.pushedBatchesTail = null;
              this.firstRowInBatch = 0;

              this.needToParse = bool;
            }

            slideDownCurrentRow(k);
          }
          catch (SQLException localSQLException)
          {
            clearBatch();
            this.needToParse = true;

            if ((this.sqlKind != 1) && (this.sqlKind != 4)) {
              for (i = 0; i < arrayOfInt.length; i++)
                arrayOfInt[i] = -3;
            }
            DatabaseError.throwBatchUpdateException(localSQLException, (this.sqlKind == 1) || (this.sqlKind == 4) ? i : arrayOfInt.length, arrayOfInt);
          }
          finally
          {
            if ((this.sqlKind == 1) || (this.sqlKind == 4)) {
              this.validRows = j;
            }
            checkValidRowsStatus();

            this.currentRank = 0;
            cleanOldTempLobs();
          }

          if (this.validRows < 0)
          {
            for (i = 0; i < arrayOfInt.length; i++) {
              arrayOfInt[i] = -3;
            }
            DatabaseError.throwBatchUpdateException(81, 0, arrayOfInt);
          }
          else if ((this.sqlKind != 1) && (this.sqlKind != 4))
          {
            for (i = 0; i < arrayOfInt.length; i++) {
              arrayOfInt[i] = -2;
            }
          }
        }
        return arrayOfInt;
      }
    }
  }

  void pushBatch(boolean paramBoolean)
  {
    PushedBatch localPushedBatch = new PushedBatch();

    localPushedBatch.currentBatchCharLens = new int[this.numberOfBindPositions];

    System.arraycopy(this.currentBatchCharLens, 0, localPushedBatch.currentBatchCharLens, 0, this.numberOfBindPositions);

    localPushedBatch.lastBoundCharLens = new int[this.numberOfBindPositions];

    System.arraycopy(this.lastBoundCharLens, 0, localPushedBatch.lastBoundCharLens, 0, this.numberOfBindPositions);

    if (this.currentBatchBindAccessors != null)
    {
      localPushedBatch.currentBatchBindAccessors = new Accessor[this.numberOfBindPositions];

      System.arraycopy(this.currentBatchBindAccessors, 0, localPushedBatch.currentBatchBindAccessors, 0, this.numberOfBindPositions);
    }

    localPushedBatch.lastBoundNeeded = this.lastBoundNeeded;
    localPushedBatch.need_to_parse = this.needToParse;
    localPushedBatch.current_batch_need_to_prepare_binds = this.currentBatchNeedToPrepareBinds;
    localPushedBatch.first_row_in_batch = this.firstRowInBatch;
    localPushedBatch.number_of_rows_to_be_bound = (this.currentRank - this.firstRowInBatch);

    if (this.pushedBatches == null)
      this.pushedBatches = localPushedBatch;
    else {
      this.pushedBatchesTail.next = localPushedBatch;
    }
    this.pushedBatchesTail = localPushedBatch;

    if (!paramBoolean)
    {
      int[] arrayOfInt = this.currentBatchCharLens;

      this.currentBatchCharLens = this.lastBoundCharLens;
      this.lastBoundCharLens = arrayOfInt;

      this.lastBoundNeeded = false;

      for (int i = 0; i < this.numberOfBindPositions; i++) {
        this.currentBatchCharLens[i] = 0;
      }
      this.firstRowInBatch = this.currentRank;
    }
  }

  int doScrollPstmtExecuteUpdate()
    throws SQLException
  {
    doScrollExecuteCommon();

    if (this.sqlKind == 0) {
      this.scrollRsetTypeSolved = true;
    }

    return this.validRows;
  }

  public int copyBinds(Statement paramStatement, int paramInt)
    throws SQLException
  {
    if (this.numberOfBindPositions > 0)
    {
      OraclePreparedStatement localOraclePreparedStatement = (OraclePreparedStatement)paramStatement;

      Binder[] arrayOfBinder = this.binders[0];
      int i = this.bindIndicatorSubRange + 3;

      int j = this.bindByteSubRange;
      int k = this.bindCharSubRange;
      int m = this.indicatorsOffset;
      int n = this.valueLengthsOffset;

      for (int i1 = 0; i1 < this.numberOfBindPositions; i1++)
      {
        short s = this.bindIndicators[(i + 0)];

        int i2 = this.bindIndicators[(i + 1)];

        int i3 = this.bindIndicators[(i + 2)];

        int i4 = i1 + paramInt;

        if (this.bindIndicators[m] == -1)
        {
          localOraclePreparedStatement.currentRowBinders[i4] = copiedNullBinder(s, i2);

          if (i3 > 0)
            localOraclePreparedStatement.currentRowCharLens[i4] = 1;
        }
        else if ((s == 109) || (s == 111))
        {
          localOraclePreparedStatement.currentRowBinders[i4] = (s == 109 ? this.theNamedTypeBinder : this.theRefTypeBinder);

          byte[] arrayOfByte1 = this.parameterDatum[0][i1];
          int i5 = arrayOfByte1.length;
          byte[] arrayOfByte2 = new byte[i5];

          localOraclePreparedStatement.parameterDatum[0][i4] = arrayOfByte2;

          System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i5);

          localOraclePreparedStatement.parameterOtype[0][i4] = this.parameterOtype[0][i1];
        }
        else if (i2 > 0)
        {
          localOraclePreparedStatement.currentRowBinders[i4] = copiedByteBinder(s, this.bindBytes, j, i2, this.bindIndicators[n]);
        }
        else if (i3 > 0)
        {
          localOraclePreparedStatement.currentRowBinders[i4] = copiedCharBinder(s, this.bindChars, k, i3, this.bindIndicators[n]);

          localOraclePreparedStatement.currentRowCharLens[i4] = i3;
        }
        else {
          throw new Error("copyBinds doesn't understand type " + s);
        }
        j += this.bindBufferCapacity * i2;
        k += this.bindBufferCapacity * i3;
        m += this.numberOfBindRowsAllocated;
        n += this.numberOfBindRowsAllocated;
        i += 10;
      }
    }

    return this.numberOfBindPositions;
  }

  Binder copiedNullBinder(short paramShort, int paramInt) throws SQLException
  {
    return new CopiedNullBinder(paramShort, paramInt);
  }

  Binder copiedByteBinder(short paramShort1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, short paramShort2)
    throws SQLException
  {
    byte[] arrayOfByte = new byte[paramInt2];

    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);

    return new CopiedByteBinder(paramShort1, paramInt2, arrayOfByte, paramShort2);
  }

  Binder copiedCharBinder(short paramShort1, char[] paramArrayOfChar, int paramInt1, int paramInt2, short paramShort2)
    throws SQLException
  {
    char[] arrayOfChar = new char[paramInt2];

    System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt2);

    return new CopiedCharBinder(paramShort1, arrayOfChar, paramShort2);
  }

  protected void hardClose()
    throws SQLException
  {
    super.hardClose();

    this.bindBytes = null;
    this.bindChars = null;
    this.bindIndicators = null;

    if (!this.connection.isClosed())
    {
      cleanAllTempLobs();
    }

    this.lastBoundBytes = null;
    this.lastBoundChars = null;

    clearParameters();
  }

  protected void alwaysOnClose()
    throws SQLException
  {
    if (this.currentRank > 0)
    {
      if (this.m_batchStyle == 2)
        clearBatch();
      else {
        sendBatch();
      }
    }
    super.alwaysOnClose();
  }

  public synchronized void setDisableStmtCaching(boolean paramBoolean)
  {
    if (paramBoolean == true)
      this.cacheState = 3;
  }

  public synchronized void setFormOfUse(int paramInt, short paramShort)
  {
    int i = paramInt - 1;

    if (this.currentRowFormOfUse[i] != paramShort)
    {
      this.currentRowFormOfUse[i] = paramShort;
      Accessor localAccessor;
      if (this.currentRowBindAccessors != null)
      {
        localAccessor = this.currentRowBindAccessors[i];

        if (localAccessor != null) {
          localAccessor.setFormOfUse(paramShort);
        }
      }

      if (this.returnParamAccessors != null)
      {
        localAccessor = this.returnParamAccessors[i];

        if (localAccessor != null)
          localAccessor.setFormOfUse(paramShort);
      }
    }
  }

  public synchronized void setURL(int paramInt, URL paramURL)
    throws SQLException
  {
    setURLInternal(paramInt, paramURL);
  }

  void setURLInternal(int paramInt, URL paramURL) throws SQLException
  {
    setStringInternal(paramInt, paramURL.toString());
  }

  public void setURLAtName(String paramString, URL paramURL)
    throws SQLException
  {
    String str = paramString.intern();
    String[] arrayOfString = this.sqlObject.getParameterList();
    int i = 0;
    int j = Math.min(this.sqlObject.getParameterCount(), arrayOfString.length);

    for (int k = 0; k < j; k++) {
      if (arrayOfString[k] != str)
        continue;
      setURL(k + 1, paramURL);

      i = 1;
    }

    if (i == 0)
      DatabaseError.throwSqlException(147, paramString);
  }

  public ParameterMetaData getParameterMetaData()
    throws SQLException
  {
    return new OracleParameterMetaData(this.sqlObject.getParameterCount());
  }

  public oracle.jdbc.OracleParameterMetaData OracleGetParameterMetaData()
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public void registerReturnParameter(int paramInt1, int paramInt2)
    throws SQLException
  {
    if (this.numberOfBindPositions <= 0) {
      DatabaseError.throwSqlException(90);
    }
    if (this.numReturnParams <= 0)
    {
      this.numReturnParams = getReturnParameterCount(this.sqlObject.getOriginalSql());
      if (this.numReturnParams <= 0) {
        DatabaseError.throwSqlException(90);
      }
    }
    int i = paramInt1 - 1;
    if ((i < this.numberOfBindPositions - this.numReturnParams) || (paramInt1 > this.numberOfBindPositions))
    {
      DatabaseError.throwSqlException(3);
    }
    int j = getInternalTypeForDmlReturning(paramInt2);

    short s = 0;
    if ((this.currentRowFormOfUse != null) && (this.currentRowFormOfUse[i] != 0)) {
      s = this.currentRowFormOfUse[i];
    }
    registerReturnParameterInternal(i, j, paramInt2, -1, s, null);

    this.currentRowBinders[i] = this.theReturnParamBinder;
  }

  public void registerReturnParameter(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    if (this.numberOfBindPositions <= 0) {
      DatabaseError.throwSqlException(90);
    }
    int i = paramInt1 - 1;
    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if ((paramInt2 != 1) && (paramInt2 != 12) && (paramInt2 != -1) && (paramInt2 != -2) && (paramInt2 != -3) && (paramInt2 != -4) && (paramInt2 != 12))
    {
      DatabaseError.throwSqlException(68);
    }

    if (paramInt3 <= 0)
    {
      DatabaseError.throwSqlException(68);
    }

    int j = getInternalTypeForDmlReturning(paramInt2);

    short s = 0;
    if ((this.currentRowFormOfUse != null) && (this.currentRowFormOfUse[i] != 0)) {
      s = this.currentRowFormOfUse[i];
    }
    registerReturnParameterInternal(i, j, paramInt2, paramInt3, s, null);

    this.currentRowBinders[i] = this.theReturnParamBinder;
  }

  public void registerReturnParameter(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (this.numberOfBindPositions <= 0) {
      DatabaseError.throwSqlException(90);
    }
    int i = paramInt1 - 1;
    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    int j = getInternalTypeForDmlReturning(paramInt2);
    if ((j != 111) && (j != 109))
    {
      DatabaseError.throwSqlException(68);
    }

    registerReturnParameterInternal(i, j, paramInt2, -1, 0, paramString);

    this.currentRowBinders[i] = this.theReturnParamBinder;
  }

  public ResultSet getReturnResultSet()
    throws SQLException
  {
    if (this.closed) {
      DatabaseError.throwSqlException(9);
    }
    if ((this.returnParamAccessors == null) || (this.numReturnParams == 0)) {
      DatabaseError.throwSqlException(144);
    }
    if (this.returnResultSet == null)
    {
      this.returnResultSet = new OracleReturnResultSet(this);
    }

    return this.returnResultSet;
  }

  int getInternalTypeForDmlReturning(int paramInt)
    throws SQLException
  {
    int i = 0;

    switch (paramInt)
    {
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
      i = 6;
      break;
    case 100:
      i = 100;
      break;
    case 101:
      i = 101;
      break;
    case 1:
      i = 96;
      break;
    case 12:
      i = 1;
      break;
    case -1:
      i = 8;
      break;
    case 91:
    case 92:
      i = 12;
      break;
    case 93:
      i = 180;
      break;
    case -101:
      i = 181;
      break;
    case -102:
      i = 231;
      break;
    case -103:
      i = 182;
      break;
    case -104:
      i = 183;
      break;
    case -3:
    case -2:
      i = 23;
      break;
    case -4:
      i = 24;
      break;
    case -8:
      i = 104;
      break;
    case 2004:
      i = 113;
      break;
    case 2005:
      i = 112;
      break;
    case -13:
      i = 114;
      break;
    case 2002:
    case 2003:
    case 2007:
    case 2008:
      i = 109;
      break;
    case 2006:
      i = 111;
      break;
    case 70:
      i = 1;
      break;
    default:
      DatabaseError.throwSqlException(4);
    }

    return i;
  }

  static int getReturnParameterCount(String paramString)
  {
    int i = -1;
    String str = paramString.toUpperCase();
    int j = str.indexOf("RETURNING");
    if (j >= 0)
    {
      char[] arrayOfChar = new char[str.length() - j];
      str.getChars(j, str.length(), arrayOfChar, 0);

      i = 0;
      for (int k = 0; k < arrayOfChar.length; k++)
      {
        if (arrayOfChar[k] != '?') continue; i++;
      }
    }

    return i;
  }

  void registerReturnParamsForAutoKey()
    throws SQLException
  {
    int[] arrayOfInt1 = this.autoKeyInfo.returnTypes;
    short[] arrayOfShort = this.autoKeyInfo.tableFormOfUses;
    int[] arrayOfInt2 = this.autoKeyInfo.columnIndexes;

    int i = arrayOfInt1.length;

    int j = this.numberOfBindPositions - i;

    for (int k = 0; k < i; k++)
    {
      int m = j + k;
      this.currentRowBinders[m] = this.theReturnParamBinder;

      short s = 0;

      if ((arrayOfShort != null) && (arrayOfInt2 != null))
      {
        if (arrayOfShort[(arrayOfInt2[k] - 1)] == 2)
        {
          s = 2;
          setFormOfUse(m + 1, s);
        }
      }

      checkTypeForAutoKey(arrayOfInt1[k]);

      String str = null;
      if (arrayOfInt1[k] == 111) {
        str = this.autoKeyInfo.tableTypeNames[(arrayOfInt2[k] - 1)];
      }
      registerReturnParameterInternal(m, arrayOfInt1[k], arrayOfInt1[k], -1, s, str);
    }
  }

  class PushedBatch
  {
    int[] currentBatchCharLens;
    int[] lastBoundCharLens;
    Accessor[] currentBatchBindAccessors;
    boolean lastBoundNeeded;
    boolean need_to_parse;
    boolean current_batch_need_to_prepare_binds;
    int first_row_in_batch;
    int number_of_rows_to_be_bound;
    PushedBatch next;

    PushedBatch()
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OraclePreparedStatement
 * JD-Core Version:    0.6.0
 */