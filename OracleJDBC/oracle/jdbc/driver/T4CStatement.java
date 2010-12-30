package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CStatement extends OracleStatement
{
  static final byte[][][] parameterDatum = (byte[][][])null;
  static final OracleTypeADT[][] parameterOtype = (OracleTypeADT[][])null;

  static final byte[] EMPTY_BYTE = new byte[0];
  T4CConnection t4Connection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  void doOall8(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
    throws SQLException, IOException
  {
    if ((paramBoolean1) || (paramBoolean4) || (!paramBoolean2) || ((this.sqlKind != 2) && (this.sqlKind != 1) && (this.sqlKind != 4))) {
      this.oacdefSent = null;
    }
    this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CStatement.doOall8");

    if ((this.sqlKind != 1) && (this.sqlKind != 4) && (this.sqlKind != 3) && (this.sqlKind != 0) && (this.sqlKind != 2))
    {
      DatabaseError.throwSqlException(439);
    }

    int i = this.numberOfDefinePositions;

    if (this.sqlKind == 2)
      i = 0;
    int j;
    if (paramBoolean3)
    {
      if (this.accessors != null)
      {
        for (j = 0; j < this.numberOfDefinePositions; j++)
        {
          if (this.accessors[j] == null)
            continue;
          this.accessors[j].lastRowProcessed = 0;
        }

      }

      if (this.outBindAccessors != null)
      {
        for (j = 0; j < this.outBindAccessors.length; j++)
        {
          if (this.outBindAccessors[j] == null)
            continue;
          this.outBindAccessors[j].lastRowProcessed = 0;
        }
      }

    }

    if (this.returnParamAccessors != null)
    {
      for (j = 0; j < this.numberOfBindPositions; j++)
      {
        if (this.returnParamAccessors[j] == null)
          continue;
        this.returnParamAccessors[j].lastRowProcessed = 0;
      }

    }

    if (this.bindIndicators != null)
    {
      j = this.bindIndicators[(this.bindIndicatorSubRange + 2)] & 0xFFFF;

      int k = 0;

      if (this.ibtBindChars != null) {
        k = this.ibtBindChars.length * this.connection.conversion.cMaxCharSize;
      }
      for (int m = 0; m < this.numberOfBindPositions; m++)
      {
        int n = this.bindIndicatorSubRange + 3 + 10 * m;

        int i1 = this.bindIndicators[(n + 2)] & 0xFFFF;

        if (i1 == 0) {
          continue;
        }
        int i2 = this.bindIndicators[(n + 9)] & 0xFFFF;

        if (i2 == 2)
        {
          k = Math.max(i1 * this.connection.conversion.maxNCharSize, k);
        }
        else
        {
          k = Math.max(i1 * this.connection.conversion.cMaxCharSize, k);
        }

      }

      if (this.tmpBindsByteArray == null)
      {
        this.tmpBindsByteArray = new byte[k];
      }
      else if (this.tmpBindsByteArray.length < k)
      {
        this.tmpBindsByteArray = null;
        this.tmpBindsByteArray = new byte[k];
      }

    }
    else
    {
      this.tmpBindsByteArray = null;
    }

    allocateTmpByteArray();

    T4C8Oall localT4C8Oall = this.t4Connection.all8;

    this.t4Connection.sendPiggyBackedMessages();

    this.oacdefSent = localT4C8Oall.marshal(paramBoolean1, paramBoolean2, paramBoolean3, paramBoolean4, this.sqlKind, this.cursorId, this.sqlObject.getSqlBytes(this.processEscapes, this.convertNcharLiterals), this.rowPrefetch, this.outBindAccessors, this.numberOfBindPositions, this.accessors, i, this.bindBytes, this.bindChars, this.bindIndicators, this.bindIndicatorSubRange, this.connection.conversion, this.tmpBindsByteArray, this.parameterStream, parameterDatum, parameterOtype, this, this.ibtBindBytes, this.ibtBindChars, this.ibtBindIndicators, this.oacdefSent, this.definedColumnType, this.definedColumnSize, this.definedColumnFormOfUse);
    try
    {
      localT4C8Oall.receive();

      this.cursorId = localT4C8Oall.getCursorId();
    }
    catch (SQLException localSQLException)
    {
      this.cursorId = localT4C8Oall.getCursorId();

      if (localSQLException.getErrorCode() == DatabaseError.getVendorCode(110))
      {
        this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 110);
      }
      else
      {
        throw localSQLException;
      }
    }
  }

  void allocateTmpByteArray()
  {
    if (this.tmpByteArray == null)
    {
      this.tmpByteArray = new byte[this.sizeTmpByteArray];
    }
    else if (this.sizeTmpByteArray > this.tmpByteArray.length)
    {
      this.tmpByteArray = new byte[this.sizeTmpByteArray];
    }
  }

  void allocateRowidAccessor()
    throws SQLException
  {
    this.accessors[0] = new T4CRowidAccessor(this, 128, 1, -8, false, this.t4Connection.mare);
  }

  void reparseOnRedefineIfNeeded()
    throws SQLException
  {
    this.needToParse = true;
  }

  protected void defineColumnTypeInternal(int paramInt1, int paramInt2, int paramInt3, short paramShort, boolean paramBoolean, String paramString)
    throws SQLException
  {
    if (this.connection.disableDefineColumnType)
    {
      return;
    }

    if (paramInt1 < 1) {
      DatabaseError.throwSqlException(3);
    }
    if (paramBoolean)
    {
      if ((paramInt2 == 1) || (paramInt2 == 12)) {
        this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 108);
      }

    }
    else if (paramInt3 < 0) {
      DatabaseError.throwSqlException(53);
    }

    if ((this.currentResultSet != null) && (!this.currentResultSet.closed)) {
      DatabaseError.throwSqlException(28);
    }

    int i = paramInt1 - 1;
    int[] arrayOfInt;
    if ((this.definedColumnType == null) || (this.definedColumnType.length <= i))
    {
      if (this.definedColumnType == null) {
        this.definedColumnType = new int[(i + 1) * 4];
      }
      else {
        arrayOfInt = new int[(i + 1) * 4];

        System.arraycopy(this.definedColumnType, 0, arrayOfInt, 0, this.definedColumnType.length);

        this.definedColumnType = arrayOfInt;
      }
    }

    this.definedColumnType[i] = paramInt2;

    if ((this.definedColumnSize == null) || (this.definedColumnSize.length <= i))
    {
      if (this.definedColumnSize == null) {
        this.definedColumnSize = new int[(i + 1) * 4];
      }
      else {
        arrayOfInt = new int[(i + 1) * 4];

        System.arraycopy(this.definedColumnSize, 0, arrayOfInt, 0, this.definedColumnSize.length);

        this.definedColumnSize = arrayOfInt;
      }
    }

    this.definedColumnSize[i] = paramInt3;

    if ((this.definedColumnFormOfUse == null) || (this.definedColumnFormOfUse.length <= i))
    {
      if (this.definedColumnFormOfUse == null) {
        this.definedColumnFormOfUse = new int[(i + 1) * 4];
      }
      else {
        arrayOfInt = new int[(i + 1) * 4];

        System.arraycopy(this.definedColumnFormOfUse, 0, arrayOfInt, 0, this.definedColumnFormOfUse.length);

        this.definedColumnFormOfUse = arrayOfInt;
      }
    }

    this.definedColumnFormOfUse[i] = paramShort;

    if ((this.accessors != null) && (i < this.accessors.length) && (this.accessors[i] != null))
    {
      this.accessors[i].definedColumnSize = paramInt3;

      if (((this.accessors[i].internalType == 96) || (this.accessors[i].internalType == 1)) && ((paramInt2 == 1) || (paramInt2 == 12)))
      {
        if (paramInt3 <= this.accessors[i].oacmxl)
        {
          this.needToPrepareDefineBuffer = true;
          this.columnsDefinedByUser = true;

          this.accessors[i].initForDataAccess(paramInt2, paramInt3, null);
          this.accessors[i].calculateSizeTmpByteArray();
        }
      }
    }
  }

  public synchronized void clearDefines() throws SQLException
  {
    super.clearDefines();

    this.definedColumnType = null;
    this.definedColumnSize = null;
    this.definedColumnFormOfUse = null;
  }

  void saveDefineBuffersIfRequired(char[] paramArrayOfChar, byte[] paramArrayOfByte, short[] paramArrayOfShort, boolean paramBoolean)
    throws SQLException
  {
    if (paramBoolean)
    {
      paramArrayOfShort = new short[this.defineIndicators.length];
      i = this.accessors[0].lengthIndexLastRow;
      int j = this.accessors[0].indicatorIndexLastRow;

      for (int n = 1; n <= this.accessors.length; n++)
      {
        int k = i + this.saved_rowPrefetch * n - 1;
        int m = j + this.saved_rowPrefetch * n - 1;
        paramArrayOfShort[m] = this.defineIndicators[m];
        paramArrayOfShort[k] = this.defineIndicators[k];
      }

    }

    for (int i = 0; i < this.accessors.length; i++)
    {
      this.accessors[i].saveDataFromOldDefineBuffers(paramArrayOfByte, paramArrayOfChar, paramArrayOfShort, this.saved_rowPrefetch != -1 ? this.saved_rowPrefetch : this.rowPrefetch, this.rowPrefetch);
    }
  }

  Accessor allocateAccessor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short paramShort, String paramString, boolean paramBoolean)
    throws SQLException
  {
    Object localObject = null;

    switch (paramInt1)
    {
    case 96:
      localObject = new T4CCharAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 8:
      if (paramBoolean)
        break;
      localObject = new T4CLongAccessor(this, paramInt3, paramInt4, paramShort, paramInt2, this.t4Connection.mare);

      break;
    case 1:
      localObject = new T4CVarcharAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 2:
      localObject = new T4CNumberAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 6:
      localObject = new T4CVarnumAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 24:
      if (!paramBoolean)
      {
        localObject = new T4CLongRawAccessor(this, paramInt3, paramInt4, paramShort, paramInt2, this.t4Connection.mare);
      }

      break;
    case 23:
      if ((paramBoolean) && (paramString != null)) {
        DatabaseError.throwSqlException(12, "sqlType=" + paramInt2);
      }

      if (paramBoolean) {
        localObject = new T4COutRawAccessor(this, paramInt4, paramShort, paramInt2, this.t4Connection.mare);
      }
      else {
        localObject = new T4CRawAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);
      }

      break;
    case 100:
      localObject = new T4CBinaryFloatAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 101:
      localObject = new T4CBinaryDoubleAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 104:
      localObject = new T4CRowidAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 102:
      localObject = new T4CResultSetAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 12:
      localObject = new T4CDateAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 113:
      localObject = new T4CBlobAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 112:
      localObject = new T4CClobAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 114:
      localObject = new T4CBfileAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 109:
      localObject = new T4CNamedTypeAccessor(this, paramString, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      ((Accessor)localObject).initMetadata();

      break;
    case 111:
      localObject = new T4CRefTypeAccessor(this, paramString, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      ((Accessor)localObject).initMetadata();

      break;
    case 180:
      if (this.connection.v8Compatible) {
        localObject = new T4CDateAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);
      }
      else {
        localObject = new T4CTimestampAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);
      }

      break;
    case 181:
      localObject = new T4CTimestamptzAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 231:
      localObject = new T4CTimestampltzAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 182:
      localObject = new T4CIntervalymAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 183:
      localObject = new T4CIntervaldsAccessor(this, paramInt4, paramShort, paramInt2, paramBoolean, this.t4Connection.mare);

      break;
    case 995:
      DatabaseError.throwSqlException(89);
    }

    return (Accessor)localObject;
  }

  void doDescribe(boolean paramBoolean)
    throws SQLException
  {
    if (!this.isOpen)
    {
      DatabaseError.throwSqlException(144);
    }

    try
    {
      this.t4Connection.sendPiggyBackedMessages();
      this.t4Connection.describe.init(this, 0);
      this.t4Connection.describe.marshal();

      this.accessors = this.t4Connection.describe.receive(this.accessors);
      this.numberOfDefinePositions = this.t4Connection.describe.numuds;

      for (int i = 0; i < this.numberOfDefinePositions; i++) {
        this.accessors[i].initMetadata();
      }

    }
    catch (IOException localIOException)
    {
      ((T4CConnection)this.connection).handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    this.describedWithNames = true;
    this.described = true;
  }

  void executeForDescribe()
    throws SQLException
  {
    this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CStatement.execute_for_describe");
    cleanOldTempLobs();
    try
    {
      if (this.t4Connection.useFetchSizeWithLongColumn)
      {
        doOall8(true, true, true, true);
      }
      else
      {
        doOall8(true, true, false, true);
      }

    }
    catch (SQLException localSQLException)
    {
      throw localSQLException;
    }
    catch (IOException localIOException)
    {
      ((T4CConnection)this.connection).handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
    finally
    {
      this.rowsProcessed = this.t4Connection.all8.rowsProcessed;
      this.validRows = this.t4Connection.all8.getNumRows();
    }

    this.needToParse = false;

    for (int i = 0; i < this.numberOfDefinePositions; i++) {
      this.accessors[i].initMetadata();
    }
    this.needToPrepareDefineBuffer = false;
  }

  void executeMaybeDescribe()
    throws SQLException
  {
    if (!this.t4Connection.useFetchSizeWithLongColumn)
    {
      super.executeMaybeDescribe();
    }
    else
    {
      if (this.rowPrefetchChanged)
      {
        if ((this.streamList == null) && (this.rowPrefetch != this.definesBatchSize)) {
          this.needToPrepareDefineBuffer = true;
        }
        this.rowPrefetchChanged = false;
      }

      if (!this.needToPrepareDefineBuffer)
      {
        if (this.accessors == null)
        {
          this.needToPrepareDefineBuffer = true;
        } else if (this.columnsDefinedByUser) {
          this.needToPrepareDefineBuffer = (!checkAccessorsUsable());
        }
      }
      boolean bool = false;
      try
      {
        this.isExecuting = true;

        if (this.needToPrepareDefineBuffer)
        {
          executeForDescribe();

          bool = true;
        }
        else
        {
          int i = this.accessors.length;

          for (int j = this.numberOfDefinePositions; j < i; j++)
          {
            Accessor localAccessor = this.accessors[j];

            if (localAccessor != null) {
              localAccessor.rowSpaceIndicator = null;
            }
          }
          executeForRows(bool);
        }

      }
      catch (SQLException localSQLException)
      {
        this.needToParse = true;
        throw localSQLException;
      }
      finally
      {
        this.isExecuting = false;
      }
    }
  }

  void executeForRows(boolean paramBoolean)
    throws SQLException
  {
    try
    {
      try
      {
        doOall8(this.needToParse, !paramBoolean, true, false);

        this.needToParse = false;
      }
      finally
      {
        this.validRows = this.t4Connection.all8.getNumRows();
      }

    }
    catch (SQLException localSQLException)
    {
      throw localSQLException;
    }
    catch (IOException localIOException)
    {
      ((T4CConnection)this.connection).handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  void fetch()
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
        catch (IOException localIOException1)
        {
          ((T4CConnection)this.connection).handleIOException(localIOException1);
          DatabaseError.throwSqlException(localIOException1);
        }

        this.nextStream = this.nextStream.nextStream;
      }
    }

    try
    {
      doOall8(false, false, true, false);

      this.validRows = this.t4Connection.all8.getNumRows();
    }
    catch (IOException localIOException2)
    {
      ((T4CConnection)this.connection).handleIOException(localIOException2);
      DatabaseError.throwSqlException(localIOException2);
    }
  }

  void continueReadRow(int paramInt)
    throws SQLException
  {
    try
    {
      if (!this.connection.useFetchSizeWithLongColumn)
      {
        T4C8Oall localT4C8Oall = this.t4Connection.all8;

        localT4C8Oall.continueReadRow(paramInt);
      }

    }
    catch (IOException localIOException)
    {
      ((T4CConnection)this.connection).handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
    catch (SQLException localSQLException)
    {
      if (localSQLException.getErrorCode() == DatabaseError.getVendorCode(110))
      {
        this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 110);
      }
      else
      {
        throw localSQLException;
      }
    }
  }

  void doClose()
    throws SQLException
  {
    this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CStatement.do_close");
    try
    {
      if (this.cursorId != 0)
      {
        this.t4Connection.cursorToClose[(this.t4Connection.cursorToCloseOffset++)] = this.cursorId;

        if (this.t4Connection.cursorToCloseOffset >= this.t4Connection.cursorToClose.length)
        {
          this.t4Connection.sendPiggyBackedMessages();
        }

      }

    }
    catch (IOException localIOException)
    {
      ((T4CConnection)this.connection).handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    this.tmpByteArray = null;
    this.tmpBindsByteArray = null;
    this.definedColumnType = null;
    this.definedColumnSize = null;
    this.definedColumnFormOfUse = null;
    this.oacdefSent = null;
  }

  void closeQuery()
    throws SQLException
  {
    this.t4Connection.assertLoggedOn("oracle.jdbc.driver.T4CStatement.closeQuery");

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
          ((T4CConnection)this.connection).handleIOException(localIOException);
          DatabaseError.throwSqlException(localIOException);
        }

        this.nextStream = this.nextStream.nextStream;
      }
    }
  }

  T4CStatement(PhysicalConnection paramPhysicalConnection, int paramInt1, int paramInt2)
    throws SQLException
  {
    super(paramPhysicalConnection, 1, paramPhysicalConnection.defaultRowPrefetch, paramInt1, paramInt2);
    this.t4Connection = ((T4CConnection)paramPhysicalConnection);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CStatement
 * JD-Core Version:    0.6.0
 */