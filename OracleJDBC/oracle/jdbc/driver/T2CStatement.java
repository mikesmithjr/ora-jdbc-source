package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T2CStatement extends OracleStatement
{
  T2CConnection connection = null;
  int userResultSetType = -1;
  int userResultSetConcur = -1;

  static int T2C_EXTEND_BUFFER = -3;

  long[] t2cOutput = new long[10];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  T2CStatement(T2CConnection paramT2CConnection, int paramInt1, int paramInt2)
    throws SQLException
  {
    this(paramT2CConnection, paramInt1, paramInt2, -1, -1);

    this.connection = paramT2CConnection;
  }

  T2CStatement(T2CConnection paramT2CConnection, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    super(paramT2CConnection, paramInt1, paramInt2, paramInt3, paramInt4);

    this.userResultSetType = paramInt3;
    this.userResultSetConcur = paramInt4;

    this.connection = paramT2CConnection;
  }

  static native int t2cParseExecuteDescribe(OracleStatement paramOracleStatement, long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, byte[] paramArrayOfByte1, int paramInt4, byte paramByte, int paramInt5, int paramInt6, short[] paramArrayOfShort1, int paramInt7, byte[] paramArrayOfByte2, char[] paramArrayOfChar1, int paramInt8, int paramInt9, short[] paramArrayOfShort2, int paramInt10, int paramInt11, byte[] paramArrayOfByte3, char[] paramArrayOfChar2, int paramInt12, int paramInt13, int[] paramArrayOfInt, short[] paramArrayOfShort3, byte[] paramArrayOfByte4, int paramInt14, int paramInt15, int paramInt16, int paramInt17, boolean paramBoolean5, boolean paramBoolean6, Accessor[] paramArrayOfAccessor, byte[][][] paramArrayOfByte, long[] paramArrayOfLong, byte[] paramArrayOfByte5, int paramInt18, char[] paramArrayOfChar3, int paramInt19, short[] paramArrayOfShort4, int paramInt20, boolean paramBoolean7);

  static native int t2cDefineExecuteFetch(OracleStatement paramOracleStatement, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, byte[] paramArrayOfByte1, int paramInt5, byte paramByte, int paramInt6, int paramInt7, short[] paramArrayOfShort1, int paramInt8, byte[] paramArrayOfByte2, char[] paramArrayOfChar1, int paramInt9, int paramInt10, short[] paramArrayOfShort2, byte[] paramArrayOfByte3, int paramInt11, int paramInt12, boolean paramBoolean3, boolean paramBoolean4, Accessor[] paramArrayOfAccessor, byte[][][] paramArrayOfByte, long[] paramArrayOfLong, byte[] paramArrayOfByte4, int paramInt13, char[] paramArrayOfChar2, int paramInt14, short[] paramArrayOfShort3, int paramInt15);

  static native int t2cDescribe(long paramLong, short[] paramArrayOfShort, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4);

  static native int t2cDefineFetch(long paramLong, int paramInt1, short[] paramArrayOfShort1, byte[] paramArrayOfByte1, int paramInt2, int paramInt3, Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte2, int paramInt4, char[] paramArrayOfChar, int paramInt5, short[] paramArrayOfShort2, int paramInt6, long[] paramArrayOfLong);

  static native int t2cFetch(long paramLong, boolean paramBoolean, int paramInt1, Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte, int paramInt2, char[] paramArrayOfChar, int paramInt3, short[] paramArrayOfShort, int paramInt4, long[] paramArrayOfLong);

  static native int t2cCloseStatement(long paramLong);

  static native int t2cEndToEndUpdate(long paramLong, byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3, byte[] paramArrayOfByte4, int paramInt4, int paramInt5);

  static native int t2cGetRowsDmlReturned(long paramLong);

  static native int t2cFetchDmlReturnParams(long paramLong, Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte, char[] paramArrayOfChar, short[] paramArrayOfShort);

  String bytes2String(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    byte[] arrayOfByte = new byte[paramInt2];

    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);

    return this.connection.conversion.CharBytesToString(arrayOfByte, paramInt2);
  }

  void processDescribeData()
    throws SQLException
  {
    this.described = true;
    this.describedWithNames = true;

    if ((this.accessors == null) || (this.numberOfDefinePositions > this.accessors.length)) {
      this.accessors = new Accessor[this.numberOfDefinePositions];
    }

    int i = this.connection.queryMetaData1Offset;
    int j = this.connection.queryMetaData2Offset;
    short[] arrayOfShort = this.connection.queryMetaData1;
    byte[] arrayOfByte = this.connection.queryMetaData2;

    for (int k = 0; k < this.numberOfDefinePositions; )
    {
      int m = arrayOfShort[(i + 0)];
      int n = arrayOfShort[(i + 1)];
      int i1 = arrayOfShort[(i + 11)];
      boolean bool = arrayOfShort[(i + 2)] != 0;
      int i2 = arrayOfShort[(i + 3)];
      int i3 = arrayOfShort[(i + 4)];
      int i4 = 0;
      int i5 = 0;
      int i6 = 0;
      short s = arrayOfShort[(i + 5)];
      int i7 = arrayOfShort[(i + 6)];
      String str1 = bytes2String(arrayOfByte, j, i7);
      int i8 = arrayOfShort[(i + 12)];
      String str2 = null;
      OracleTypeADT localOracleTypeADT = null;

      j += i7;

      if (i8 > 0)
      {
        str2 = bytes2String(arrayOfByte, j, i8);
        j += i8;
        localOracleTypeADT = new OracleTypeADT(str2, this.connection);
        localOracleTypeADT.tdoCState = ((arrayOfShort[(i + 7)] & 0xFFFF) << 48 | (arrayOfShort[(i + 8)] & 0xFFFF) << 32 | (arrayOfShort[(i + 9)] & 0xFFFF) << 16 | arrayOfShort[(i + 10)] & 0xFFFF);
      }

      Object localObject = this.accessors[k];

      if ((localObject != null) && (!((Accessor)localObject).useForDescribeIfPossible(m, n, bool, i4, i2, i3, i5, i6, s, str2)))
      {
        localObject = null;
      }
      if (localObject == null)
      {
        switch (m)
        {
        case 1:
          localObject = new VarcharAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          if (i1 <= 0) break;
          ((Accessor)localObject).setDisplaySize(i1); break;
        case 96:
          localObject = new CharAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          if (i1 <= 0) break;
          ((Accessor)localObject).setDisplaySize(i1); break;
        case 2:
          localObject = new NumberAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 23:
          localObject = new RawAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 100:
          localObject = new BinaryFloatAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 101:
          localObject = new BinaryDoubleAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 8:
          localObject = new LongAccessor(this, k + 1, n, bool, i4, i2, i3, i5, i6, s);

          this.rowPrefetch = 1;

          break;
        case 24:
          localObject = new LongRawAccessor(this, k + 1, n, bool, i4, i2, i3, i5, i6, s);

          this.rowPrefetch = 1;

          break;
        case 104:
          localObject = new RowidAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 102:
        case 116:
          localObject = new T2CResultSetAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 12:
          localObject = new DateAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 180:
          localObject = new TimestampAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 181:
          localObject = new TimestamptzAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 231:
          localObject = new TimestampltzAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 182:
          localObject = new IntervalymAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 183:
          localObject = new IntervaldsAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 112:
          localObject = new ClobAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 113:
          localObject = new BlobAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 114:
          localObject = new BfileAccessor(this, n, bool, i4, i2, i3, i5, i6, s);

          break;
        case 109:
          localObject = new NamedTypeAccessor(this, n, bool, i4, i2, i3, i5, i6, s, str2, localOracleTypeADT);

          break;
        case 111:
          localObject = new RefTypeAccessor(this, n, bool, i4, i2, i3, i5, i6, s, str2, localOracleTypeADT);

          break;
        default:
          throw new SQLException("Unknown or unimplemented accessor type: " + m);
        }

        this.accessors[k] = localObject;
      }
      else if (localOracleTypeADT != null)
      {
        ((Accessor)localObject).initMetadata();
      }

      ((Accessor)localObject).columnName = str1;

      k++; i += 13;
    }
  }

  void doDescribe(boolean paramBoolean)
    throws SQLException
  {
    if (this.closed) {
      DatabaseError.throwSqlException(9);
    }
    if (this.described == true)
    {
      return;
    }

    if (!this.isOpen)
    {
      DatabaseError.throwSqlException(144);
    }

    int i;
    do
    {
      i = 0;

      this.numberOfDefinePositions = t2cDescribe(this.c_state, this.connection.queryMetaData1, this.connection.queryMetaData2, this.connection.queryMetaData1Offset, this.connection.queryMetaData2Offset, this.connection.queryMetaData1Size, this.connection.queryMetaData2Size);

      if (this.numberOfDefinePositions == -1)
      {
        this.connection.checkError(this.numberOfDefinePositions);
      }

      if (this.numberOfDefinePositions != T2C_EXTEND_BUFFER)
        continue;
      i = 1;

      this.connection.reallocateQueryMetaData(this.connection.queryMetaData1Size * 2, this.connection.queryMetaData2Size * 2);
    }

    while (i != 0);

    processDescribeData();
  }
  void executeForDescribe() throws SQLException {
    this.t2cOutput[0] = 0L;
    this.t2cOutput[2] = 0L;

    boolean bool1 = !this.described;
    boolean bool2 = false;
    int i;
    do {
      i = 0;

      if (this.connection.endToEndAnyChanged)
      {
        pushEndToEndValues();

        this.connection.endToEndAnyChanged = false;
      }

      byte[] arrayOfByte = this.sqlObject.getSqlBytes(this.processEscapes, this.convertNcharLiterals);
      int j = t2cParseExecuteDescribe(this, this.c_state, this.numberOfBindPositions, 0, 0, false, this.needToParse, bool1, bool2, arrayOfByte, arrayOfByte.length, this.sqlKind, this.rowPrefetch, this.batch, this.bindIndicators, this.bindIndicatorOffset, this.bindBytes, this.bindChars, this.bindByteOffset, this.bindCharOffset, this.ibtBindIndicators, this.ibtBindIndicatorOffset, this.ibtBindIndicatorSize, this.ibtBindBytes, this.ibtBindChars, this.ibtBindByteOffset, this.ibtBindCharOffset, this.returnParamMeta, this.connection.queryMetaData1, this.connection.queryMetaData2, this.connection.queryMetaData1Offset, this.connection.queryMetaData2Offset, this.connection.queryMetaData1Size, this.connection.queryMetaData2Size, true, true, this.accessors, (byte[][][])null, this.t2cOutput, this.defineBytes, this.accessorByteOffset, this.defineChars, this.accessorCharOffset, this.defineIndicators, this.accessorShortOffset, this.connection.plsqlCompilerWarnings);

      this.validRows = (int)this.t2cOutput[1];

      if (j == -1)
      {
        this.connection.checkError(j);
      }
      else if (j == T2C_EXTEND_BUFFER)
      {
        j = this.connection.queryMetaData1Size * 2;
      }

      if (this.t2cOutput[3] != 0L)
      {
        foundPlsqlCompilerWarning();
      }
      else if (this.t2cOutput[2] != 0L)
      {
        this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
      }

      this.connection.endToEndECIDSequenceNumber = (short)(int)this.t2cOutput[4];

      this.needToParse = false;
      bool2 = true;

      if (this.sqlKind == 0)
      {
        this.numberOfDefinePositions = j;

        if (this.numberOfDefinePositions <= this.connection.queryMetaData1Size)
          continue;
        i = 1;
        bool2 = true;

        this.connection.reallocateQueryMetaData(this.numberOfDefinePositions, this.numberOfDefinePositions * 8);
      }
      else
      {
        this.numberOfDefinePositions = 0;
        this.validRows = j;
      }
    }
    while (i != 0);

    processDescribeData();
  }

  void pushEndToEndValues()
    throws SQLException
  {
    T2CConnection localT2CConnection = this.connection;
    byte[] arrayOfByte1 = new byte[0];
    byte[] arrayOfByte2 = new byte[0];
    byte[] arrayOfByte3 = new byte[0];
    byte[] arrayOfByte4 = new byte[0];

    if (localT2CConnection.endToEndValues != null)
    {
      String str;
      if (localT2CConnection.endToEndHasChanged[0] != 0)
      {
        str = localT2CConnection.endToEndValues[0];

        if (str != null) {
          arrayOfByte1 = DBConversion.stringToDriverCharBytes(str, localT2CConnection.m_clientCharacterSet);
        }

        localT2CConnection.endToEndHasChanged[0] = false;
      }

      if (localT2CConnection.endToEndHasChanged[1] != 0)
      {
        str = localT2CConnection.endToEndValues[1];

        if (str != null) {
          arrayOfByte2 = DBConversion.stringToDriverCharBytes(str, localT2CConnection.m_clientCharacterSet);
        }

        localT2CConnection.endToEndHasChanged[1] = false;
      }

      if (localT2CConnection.endToEndHasChanged[2] != 0)
      {
        str = localT2CConnection.endToEndValues[2];

        if (str != null) {
          arrayOfByte3 = DBConversion.stringToDriverCharBytes(str, localT2CConnection.m_clientCharacterSet);
        }

        localT2CConnection.endToEndHasChanged[2] = false;
      }

      if (localT2CConnection.endToEndHasChanged[3] != 0)
      {
        str = localT2CConnection.endToEndValues[3];

        if (str != null) {
          arrayOfByte4 = DBConversion.stringToDriverCharBytes(str, localT2CConnection.m_clientCharacterSet);
        }

        localT2CConnection.endToEndHasChanged[3] = false;
      }

      t2cEndToEndUpdate(this.c_state, arrayOfByte1, arrayOfByte1.length, arrayOfByte2, arrayOfByte2.length, arrayOfByte3, arrayOfByte3.length, arrayOfByte4, arrayOfByte4.length, localT2CConnection.endToEndECIDSequenceNumber);
    }
  }

  void executeForRows(boolean paramBoolean)
    throws SQLException
  {
    if (this.connection.endToEndAnyChanged)
    {
      pushEndToEndValues();

      this.connection.endToEndAnyChanged = false;
    }

    if (!paramBoolean)
    {
      if (this.numberOfDefinePositions > 0)
      {
        doDefineExecuteFetch();
      }
      else
      {
        executeForDescribe();
      }
    }
    else if (this.numberOfDefinePositions > 0) {
      doDefineFetch();
    }

    this.needToPrepareDefineBuffer = false;
  }

  void setupForDefine()
    throws SQLException
  {
    short[] arrayOfShort = this.connection.queryMetaData1;
    int i = this.connection.queryMetaData1Offset;

    for (int j = 0; j < this.numberOfDefinePositions; )
    {
      Accessor localAccessor = this.accessors[j];

      if (localAccessor == null) {
        DatabaseError.throwSqlException(21);
      }
      arrayOfShort[(i + 0)] = (short)localAccessor.defineType;

      arrayOfShort[(i + 11)] = (short)localAccessor.charLength;

      arrayOfShort[(i + 1)] = (short)localAccessor.byteLength;

      arrayOfShort[(i + 5)] = localAccessor.formOfUse;

      if (localAccessor.internalOtype != null)
      {
        long l = ((OracleTypeADT)localAccessor.internalOtype).getTdoCState();

        arrayOfShort[(i + 7)] = (short)(int)((l & 0x0) >> 48);

        arrayOfShort[(i + 8)] = (short)(int)((l & 0x0) >> 32);

        arrayOfShort[(i + 9)] = (short)(int)((l & 0xFFFF0000) >> 16);

        arrayOfShort[(i + 10)] = (short)(int)(l & 0xFFFF);
      }
      j++; i += 13;
    }
  }

  void doDefineFetch()
    throws SQLException
  {
    if (!this.needToPrepareDefineBuffer) {
      throw new Error("doDefineFetch called when needToPrepareDefineBuffer=false " + this.sqlObject.getSql(this.processEscapes, this.convertNcharLiterals));
    }

    setupForDefine();

    this.t2cOutput[2] = 0L;
    this.validRows = t2cDefineFetch(this.c_state, this.rowPrefetch, this.connection.queryMetaData1, this.connection.queryMetaData2, this.connection.queryMetaData1Offset, this.connection.queryMetaData2Offset, this.accessors, this.defineBytes, this.accessorByteOffset, this.defineChars, this.accessorCharOffset, this.defineIndicators, this.accessorShortOffset, this.t2cOutput);

    if (this.validRows == -1) {
      this.connection.checkError(this.validRows);
    }

    if (this.t2cOutput[2] != 0L)
    {
      this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
    }
  }

  void doDefineExecuteFetch()
    throws SQLException
  {
    short[] arrayOfShort = null;

    if ((this.needToPrepareDefineBuffer) || (this.needToParse))
    {
      setupForDefine();

      arrayOfShort = this.connection.queryMetaData1;
    }

    this.t2cOutput[0] = 0L;
    this.t2cOutput[2] = 0L;

    byte[] arrayOfByte = this.sqlObject.getSqlBytes(this.processEscapes, this.convertNcharLiterals);

    this.validRows = t2cDefineExecuteFetch(this, this.c_state, this.numberOfDefinePositions, this.numberOfBindPositions, 0, 0, false, this.needToParse, arrayOfByte, arrayOfByte.length, this.sqlKind, this.rowPrefetch, this.batch, this.bindIndicators, this.bindIndicatorOffset, this.bindBytes, this.bindChars, this.bindByteOffset, this.bindCharOffset, arrayOfShort, this.connection.queryMetaData2, this.connection.queryMetaData1Offset, this.connection.queryMetaData2Offset, true, true, this.accessors, (byte[][][])null, this.t2cOutput, this.defineBytes, this.accessorByteOffset, this.defineChars, this.accessorCharOffset, this.defineIndicators, this.accessorShortOffset);

    if (this.validRows == -1) {
      this.connection.checkError(this.validRows);
    }
    if (this.t2cOutput[2] != 0L) {
      this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
    }

    this.connection.endToEndECIDSequenceNumber = (short)(int)this.t2cOutput[4];

    this.needToParse = false;
  }

  void fetch()
    throws SQLException
  {
    if (this.numberOfDefinePositions > 0)
    {
      if (this.needToPrepareDefineBuffer) {
        doDefineFetch();
      }
      else {
        this.t2cOutput[2] = 0L;
        this.validRows = t2cFetch(this.c_state, this.needToPrepareDefineBuffer, this.rowPrefetch, this.accessors, this.defineBytes, this.accessorByteOffset, this.defineChars, this.accessorCharOffset, this.defineIndicators, this.accessorShortOffset, this.t2cOutput);

        if (this.validRows == -1) {
          this.connection.checkError(this.validRows);
        }
        if (this.t2cOutput[2] != 0L)
        {
          this.sqlWarning = this.connection.checkError(1, this.sqlWarning);
        }
      }
    }
  }

  void doClose()
    throws SQLException
  {
    if (this.defineBytes != null)
    {
      this.defineBytes = null;
      this.accessorByteOffset = 0;
    }

    if (this.defineChars != null)
    {
      this.defineChars = null;
      this.accessorCharOffset = 0;
    }

    if (this.defineIndicators != null)
    {
      this.defineIndicators = null;
      this.accessorShortOffset = 0;
    }

    int i = t2cCloseStatement(this.c_state);

    if (i != 0) {
      this.connection.checkError(i);
    }
    this.t2cOutput = null;
  }

  void closeQuery()
    throws SQLException
  {
    if (this.streamList != null)
    {
      while (this.nextStream != null)
      {
        try
        {
          this.nextStream.close();
        }
        catch (IOException localIOException)
        {
          DatabaseError.throwSqlException(localIOException);
        }

        this.nextStream = this.nextStream.nextStream;
      }
    }
  }

  Accessor allocateAccessor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short paramShort, String paramString, boolean paramBoolean)
    throws SQLException
  {
    Object localObject;
    if (paramInt1 == 109)
    {
      if (paramString == null) {
        if (paramBoolean) {
          DatabaseError.throwSqlException(12, "sqlType=" + paramInt2);
        }
        else {
          DatabaseError.throwSqlException(60, "Unable to resolve type \"null\"");
        }
      }
      localObject = new T2CNamedTypeAccessor(this, paramString, paramShort, paramInt2, paramBoolean, paramInt3 - 1);

      ((Accessor)localObject).initMetadata();

      return localObject;
    }
    if ((paramInt1 == 116) || (paramInt1 == 102))
    {
      if ((paramBoolean) && (paramString != null)) {
        DatabaseError.throwSqlException(12, "sqlType=" + paramInt2);
      }

      localObject = new T2CResultSetAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean);

      return localObject;
    }

    return (Accessor)super.allocateAccessor(paramInt1, paramInt2, paramInt3, paramInt4, paramShort, paramString, paramBoolean);
  }

  void closeUsedStreams(int paramInt)
    throws SQLException
  {
    while ((this.nextStream != null) && (this.nextStream.columnIndex < paramInt))
    {
      try
      {
        this.nextStream.close();
      }
      catch (IOException localIOException1)
      {
        DatabaseError.throwSqlException(localIOException1);
      }

      this.nextStream = this.nextStream.nextStream;
    }

    if (this.nextStream != null)
      try
      {
        this.nextStream.needBytes();
      }
      catch (IOException localIOException2)
      {
        DatabaseError.throwSqlException(localIOException2);
      }
  }

  void fetchDmlReturnParams()
    throws SQLException
  {
    this.rowsDmlReturned = t2cGetRowsDmlReturned(this.c_state);

    if (this.rowsDmlReturned != 0)
    {
      allocateDmlReturnStorage();

      int i = t2cFetchDmlReturnParams(this.c_state, this.returnParamAccessors, this.returnParamBytes, this.returnParamChars, this.returnParamIndicators);
    }

    this.returnParamsFetched = true;
  }

  void initializeIndicatorSubRange()
  {
    this.bindIndicatorSubRange = (this.numberOfBindPositions * 5);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T2CStatement
 * JD-Core Version:    0.6.0
 */