package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.net.ns.BreakNetException;

class T4C8Oall extends T4CTTIfun
{
  static final byte[] EMPTY_BYTES = new byte[0];
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
  int receiveState = 0;
  static final int IDLE_RECEIVE_STATE = 0;
  static final int ACTIVE_RECEIVE_STATE = 1;
  static final int READROW_RECEIVE_STATE = 2;
  static final int STREAM_RECEIVE_STATE = 3;
  int rowsProcessed;
  int numberOfDefinePositions;
  long options;
  int cursor;
  byte[] sqlStmt = new byte[0];
  final long[] al8i4 = new long[13];

  boolean plsql = false;
  Accessor[] definesAccessors;
  int definesLength;
  Accessor[] outBindAccessors;
  int numberOfBindPositions;
  InputStream[][] parameterStream;
  byte[][][] parameterDatum;
  OracleTypeADT[][] parameterOtype;
  short[] bindIndicators;
  byte[] bindBytes;
  char[] bindChars;
  int bindIndicatorSubRange;
  byte[] tmpBindsByteArray;
  DBConversion conversion;
  byte[] ibtBindBytes;
  char[] ibtBindChars;
  short[] ibtBindIndicators;
  boolean sendBindsDefinition = false;
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
  boolean sendDefines = false;
  int defCols = 0;
  int rowsToFetch;
  T4CTTIoac[] oacdefBindsSent;
  T4CTTIoac[] oacdefDefines;
  int[] definedColumnSize;
  int[] definedColumnType;
  int[] definedColumnFormOfUse;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  T4C8Oall(T4CMAREngine paramT4CMAREngine, T4CConnection paramT4CConnection, T4CTTIoer paramT4CTTIoer)
    throws IOException, SQLException
  {
    super(3, 0, 94);

    setMarshalingEngine(paramT4CMAREngine);

    this.rxh = new T4C8TTIrxh(this.meg);
    this.rxd = new T4CTTIrxd(this.meg);
    this.oer = paramT4CTTIoer;
    this.oac = new T4CTTIoac(this.meg);
    this.dcb = new T4CTTIdcb(this.meg);
    this.ofetch = new T4CTTIofetch(this.meg);
    this.oexec = new T4CTTIoexec(this.meg);
    this.fob = new T4CTTIfob(this.meg);
  }

  T4CTTIoac[] marshal(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, byte paramByte, int paramInt1, byte[] paramArrayOfByte1, int paramInt2, Accessor[] paramArrayOfAccessor1, int paramInt3, Accessor[] paramArrayOfAccessor2, int paramInt4, byte[] paramArrayOfByte2, char[] paramArrayOfChar1, short[] paramArrayOfShort1, int paramInt5, DBConversion paramDBConversion, byte[] paramArrayOfByte3, InputStream[][] paramArrayOfInputStream, byte[][][] paramArrayOfByte, OracleTypeADT[][] paramArrayOfOracleTypeADT, OracleStatement paramOracleStatement, byte[] paramArrayOfByte4, char[] paramArrayOfChar2, short[] paramArrayOfShort2, T4CTTIoac[] paramArrayOfT4CTTIoac, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int[] paramArrayOfInt3)
    throws SQLException, IOException
  {
    this.typeOfStatement = paramByte;
    this.cursor = paramInt1;
    this.sqlStmt = (paramBoolean1 ? paramArrayOfByte1 : EMPTY_BYTES);
    this.rowsToFetch = paramInt2;
    this.outBindAccessors = paramArrayOfAccessor1;
    this.numberOfBindPositions = paramInt3;
    this.definesAccessors = paramArrayOfAccessor2;
    this.definesLength = paramInt4;
    this.bindBytes = paramArrayOfByte2;
    this.bindChars = paramArrayOfChar1;
    this.bindIndicators = paramArrayOfShort1;
    this.bindIndicatorSubRange = paramInt5;
    this.conversion = paramDBConversion;
    this.tmpBindsByteArray = paramArrayOfByte3;
    this.parameterStream = paramArrayOfInputStream;
    this.parameterDatum = paramArrayOfByte;
    this.parameterOtype = paramArrayOfOracleTypeADT;
    this.oracleStatement = paramOracleStatement;
    this.ibtBindBytes = paramArrayOfByte4;
    this.ibtBindChars = paramArrayOfChar2;
    this.ibtBindIndicators = paramArrayOfShort2;
    this.oacdefBindsSent = paramArrayOfT4CTTIoac;
    this.definedColumnType = paramArrayOfInt1;
    this.definedColumnSize = paramArrayOfInt2;
    this.definedColumnFormOfUse = paramArrayOfInt3;

    this.dbCharSet = paramDBConversion.getServerCharSetId();
    this.NCharSet = paramDBConversion.getNCharSetId();

    int i = 0;
    if (this.bindIndicators != null) {
      i = this.bindIndicators[(this.bindIndicatorSubRange + 2)] & 0xFFFF;
    }

    if (paramArrayOfByte1 == null)
    {
      DatabaseError.throwSqlException(431);
    }
    if ((this.typeOfStatement != 2) && (i > 1))
    {
      DatabaseError.throwSqlException(433);
    }

    this.rowsProcessed = 0;
    this.options = 0L;
    this.plsql = ((this.typeOfStatement == 1) || (this.typeOfStatement == 4));

    this.sendBindsDefinition = false;

    if (this.receiveState != 0)
    {
      this.receiveState = 0;
      DatabaseError.throwSqlException(447);
    }

    this.rxh.init();
    this.rxd.init();
    this.oer.init();

    this.sendDefines = false;
    if ((paramBoolean1) && (paramBoolean2) && (!paramBoolean3) && (this.definedColumnType != null))
    {
      this.sendDefines = true;
      initDefinesDefinition();
    }

    if ((this.numberOfBindPositions > 0) && (this.bindIndicators != null))
    {
      if (this.oacdefBindsSent == null)
        this.oacdefBindsSent = new T4CTTIoac[this.numberOfBindPositions];
      this.sendBindsDefinition = initBindsDefinition(this.oacdefBindsSent);
    }

    this.options = setOptions(paramBoolean1, paramBoolean2, paramBoolean3);

    if ((this.options & 1L) > 0L)
      this.al8i4[0] = 1L;
    else {
      this.al8i4[0] = 0L;
    }

    if ((this.plsql) || (this.typeOfStatement == 3))
      this.al8i4[1] = 1L;
    else if (paramBoolean4)
    {
      if ((paramBoolean3) && (this.oracleStatement.connection.useFetchSizeWithLongColumn))
        this.al8i4[1] = this.rowsToFetch;
      else
        this.al8i4[1] = 0L;
    }
    else if (this.typeOfStatement == 2)
    {
      this.al8i4[1] = i;
    }
    else if ((paramBoolean3) && (!paramBoolean4))
    {
      this.al8i4[1] = this.rowsToFetch;
    }
    else this.al8i4[1] = 0L;

    if (this.typeOfStatement == 0)
      this.al8i4[7] = 1L;
    else {
      this.al8i4[7] = 0L;
    }
    marshalAll();

    return this.oacdefBindsSent;
  }

  void receive()
    throws SQLException, IOException
  {
    if ((this.receiveState != 0) && (this.receiveState != 2))
    {
      DatabaseError.throwSqlException(447);
    }

    this.receiveState = 1;

    int i = 0;
    int j = 0;
    int k = 0;

    this.rowsProcessed = 0;

    this.rxd.setNumberOfColumns(this.definesLength);
    while (true)
    {
      try
      {
        int m = this.meg.unmarshalSB1();
        int i1;
        int i4;
        int i5;
        switch (m)
        {
        case 21:
          int n = this.meg.unmarshalUB2();

          this.rxd.unmarshalBVC(n);

          break;
        case 11:
          T4CTTIiov localT4CTTIiov = new T4CTTIiov(this.meg, this.rxh, this.rxd);

          localT4CTTIiov.init();
          localT4CTTIiov.unmarshalV10();

          if ((this.oracleStatement.returnParamAccessors != null) || (localT4CTTIiov.isIOVectorEmpty())) {
            continue;
          }
          byte[] arrayOfByte = localT4CTTIiov.getIOVector();

          this.outBindAccessors = localT4CTTIiov.processRXD(this.outBindAccessors, this.numberOfBindPositions, this.bindBytes, this.bindChars, this.bindIndicators, this.bindIndicatorSubRange, this.conversion, this.tmpBindsByteArray, arrayOfByte, this.parameterStream, this.parameterDatum, this.parameterOtype, this.oracleStatement, null, null, null);

          k = 1;

          break;
        case 6:
          this.rxh.init();
          this.rxh.unmarshalV10(this.rxd);

          if (this.rxh.uacBufLength <= 0)
          {
            continue;
          }

          DatabaseError.throwSqlException(405);

          j = 1;

          break;
        case 7:
          if (this.receiveState == 1)
            continue;
          DatabaseError.throwSqlException(447);

          this.receiveState = 2;

          if ((this.oracleStatement.returnParamAccessors == null) || (this.numberOfBindPositions <= 0)) {
            continue;
          }
          i1 = 0;
          int i2 = 0; if (i2 >= this.oracleStatement.numberOfBindPositions)
            continue;
          Accessor localAccessor = this.oracleStatement.returnParamAccessors[i2];
          if (localAccessor == null)
            continue;
          i4 = (int)this.meg.unmarshalUB4();
          if (i1 != 0)
            continue;
          this.oracleStatement.rowsDmlReturned = i4;
          this.oracleStatement.allocateDmlReturnStorage();
          this.oracleStatement.setupReturnParamAccessors();
          i1 = 1;

          i5 = 0; if (i5 >= i4)
          {
            continue;
          }

          localAccessor.unmarshalOneRow();

          i5++; continue;

          i2++; continue;

          this.oracleStatement.returnParamsFetched = true; continue;

          if ((k == 0) && ((this.outBindAccessors == null) || (this.definesAccessors != null)))
          {
            continue;
          }
          if (!this.rxd.unmarshal(this.outBindAccessors, this.numberOfBindPositions))
            continue;
          this.receiveState = 3;

          return;

          if (!this.rxd.unmarshal(this.definesAccessors, this.definesLength))
          {
            continue;
          }

          this.receiveState = 3;

          return;

          this.receiveState = 1;

          break;
        case 8:
          if (i == 0)
          {
            continue;
          }

          DatabaseError.throwSqlException(401);

          i1 = this.meg.unmarshalUB2();
          int[] arrayOfInt = new int[i1];

          int i3 = 0; if (i3 < i1) {
            arrayOfInt[i3] = (int)this.meg.unmarshalUB4();

            i3++; continue;
          }

          this.cursor = arrayOfInt[2];

          this.meg.unmarshalUB2();

          i3 = this.meg.unmarshalUB2();

          if (i3 <= 0)
            continue;
          i4 = 0; if (i4 >= i3)
            continue;
          i5 = (int)this.meg.unmarshalUB4();

          this.meg.unmarshalDALC();

          int i6 = this.meg.unmarshalUB2();

          i4++; continue;

          i = 1;

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
          this.oer.init();

          this.cursor = this.oer.unmarshal();

          this.rowsProcessed = this.oer.getCurRowNumber();

          if ((this.typeOfStatement == 0) && ((this.typeOfStatement != 0) || (this.oer.retCode == 1403)))
          {
            continue;
          }

          try
          {
            this.oer.processError(this.oracleStatement);
          }
          catch (SQLException localSQLException)
          {
            this.receiveState = 0;

            throw localSQLException;
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

        continue; } catch (BreakNetException localBreakNetException) {
      }
    }
    if (this.receiveState != 1)
    {
      DatabaseError.throwSqlException(447);
    }

    this.receiveState = 0;
  }

  int getCursorId()
  {
    return this.cursor;
  }

  void continueReadRow(int paramInt)
    throws SQLException, IOException
  {
    this.receiveState = 2;

    if (this.rxd.unmarshal(this.definesAccessors, paramInt, this.definesLength))
    {
      this.receiveState = 3;

      return;
    }

    receive();
  }

  int getNumRows()
  {
    int i = 0;

    if (this.receiveState == 3) {
      i = -2;
    }
    else {
      switch (this.typeOfStatement)
      {
      case 1:
      case 2:
      case 3:
      case 4:
        i = this.rowsProcessed;

        break;
      case 0:
        i = (this.definesAccessors != null) && (this.definesLength > 0) ? this.definesAccessors[0].lastRowProcessed : 0;
      }

    }

    return i;
  }

  void marshalAll()
    throws SQLException, IOException
  {
    if (((this.options & 0x40) != 0L) && ((this.options & 0x20) == 0L) && ((this.options & 1L) == 0L) && ((this.options & 0x8) == 0L) && (!this.oracleStatement.needToSendOalToFetch))
    {
      this.ofetch.marshal(this.cursor, (int)this.al8i4[1]);
    }
    else if ((this.meg.versionNumber >= 10000) && ((this.options & 0x20) != 0L) && ((this.options & 1L) == 0L) && ((this.options & 0x40) == 0L) && ((this.options & 0x8) == 0L) && (this.typeOfStatement != 2) && (this.typeOfStatement != 1) && (this.typeOfStatement != 4))
    {
      int i = 0;

      if ((this.options & 0x100) != 0L) {
        i = 1;
      }
      this.oexec.marshal(this.cursor, (int)this.al8i4[1], i);

      if ((this.numberOfBindPositions > 0) && (this.bindIndicators != null))
      {
        int[] arrayOfInt2 = new int[this.numberOfBindPositions];

        for (int k = 0; k < this.numberOfBindPositions; k++)
        {
          arrayOfInt2[k] = this.oacdefBindsSent[k].oacmxl;
        }

        marshalBinds(arrayOfInt2);
      }
    }
    else
    {
      if (this.oracleStatement.needToSendOalToFetch) {
        this.oracleStatement.needToSendOalToFetch = false;
      }

      marshalPisdef();

      this.meg.marshalCHR(this.sqlStmt);

      this.meg.marshalUB4Array(this.al8i4);

      int[] arrayOfInt1 = new int[this.numberOfBindPositions];

      for (int j = 0; j < this.numberOfBindPositions; j++)
      {
        arrayOfInt1[j] = this.oacdefBindsSent[j].oacmxl;
      }

      if (((this.options & 0x8) != 0L) && (this.numberOfBindPositions > 0) && (this.bindIndicators != null) && (this.sendBindsDefinition))
      {
        marshalBindsTypes(this.oacdefBindsSent);
      }

      if ((this.meg.versionNumber >= 9000) && (this.sendDefines)) {
        for (j = 0; j < this.defCols; j++) {
          this.oacdefDefines[j].marshal();
        }

      }

      if (((this.options & 0x20) != 0L) && (this.numberOfBindPositions > 0) && (this.bindIndicators != null))
      {
        marshalBinds(arrayOfInt1);
      }
    }
  }

  void marshalPisdef()
    throws IOException, SQLException
  {
    super.marshalFunHeader();

    this.meg.marshalUB4(this.options);

    this.meg.marshalSWORD(this.cursor);

    if (this.sqlStmt.length == 0)
      this.meg.marshalNULLPTR();
    else {
      this.meg.marshalPTR();
    }

    this.meg.marshalSWORD(this.sqlStmt.length);

    if (this.al8i4.length == 0)
      this.meg.marshalNULLPTR();
    else {
      this.meg.marshalPTR();
    }

    this.meg.marshalSWORD(this.al8i4.length);

    this.meg.marshalNULLPTR();

    this.meg.marshalNULLPTR();

    this.meg.marshalUB4(0L);

    this.meg.marshalUB4(0L);

    if ((this.typeOfStatement != 1) && (this.typeOfStatement != 4))
    {
      this.meg.marshalUB4(2147483647L);
    }
    else this.meg.marshalUB4(32760L);

    if (((this.options & 0x8) != 0L) && (this.numberOfBindPositions > 0) && (this.sendBindsDefinition))
    {
      this.meg.marshalPTR();

      this.meg.marshalSWORD(this.numberOfBindPositions);
    }
    else
    {
      this.meg.marshalNULLPTR();

      this.meg.marshalSWORD(0);
    }

    this.meg.marshalNULLPTR();

    this.meg.marshalNULLPTR();

    this.meg.marshalNULLPTR();

    this.meg.marshalNULLPTR();

    this.meg.marshalNULLPTR();

    if (this.meg.versionNumber >= 9000)
    {
      if ((this.defCols > 0) && (this.sendDefines))
      {
        this.meg.marshalPTR();

        this.meg.marshalSWORD(this.defCols);
      }
      else
      {
        this.meg.marshalNULLPTR();

        this.meg.marshalSWORD(0);
      }
    }
  }

  boolean initBindsDefinition(T4CTTIoac[] paramArrayOfT4CTTIoac)
    throws SQLException, IOException
  {
    int i = 0;

    if (paramArrayOfT4CTTIoac.length != this.numberOfBindPositions)
    {
      i = 1;
      paramArrayOfT4CTTIoac = new T4CTTIoac[this.numberOfBindPositions];
    }

    short[] arrayOfShort = this.bindIndicators;

    int k = 0;

    int n = 0;

    for (int i1 = 0; i1 < this.numberOfBindPositions; i1++)
    {
      int j = this.bindIndicatorSubRange + 3 + 10 * i1;

      short s = arrayOfShort[(j + 9)];

      int m = arrayOfShort[(j + 0)] & 0xFFFF;

      if ((m == 8) || (m == 24))
      {
        if (this.plsql)
          k = 32760;
        else
          k = 2147483647;
        this.oac.init((short)m, k, this.dbCharSet, this.NCharSet, s);
      }
      else if (m == 998)
      {
        if ((this.outBindAccessors != null) && (this.outBindAccessors[i1] != null))
        {
          this.oac.init((PlsqlIndexTableAccessor)this.outBindAccessors[i1]);
          n++;
        }
        else if (this.ibtBindIndicators[(6 + n * 8)] != 0)
        {
          int i2 = this.ibtBindIndicators[(6 + n * 8)];
          int i4 = this.ibtBindIndicators[(6 + n * 8 + 2)] << 16 & 0xFFFF000 | this.ibtBindIndicators[(6 + n * 8 + 3)];

          k = this.ibtBindIndicators[(6 + n * 8 + 1)];

          this.oac.initIbt((short)i2, i4, k);

          n++;
        }
        else {
          DatabaseError.throwSqlException("INTERNAL ERROR: Binding PLSQL index-by table but no type defined", null, -1);
        }

      }
      else if ((m == 109) || (m == 111))
      {
        if ((this.outBindAccessors != null) && (this.outBindAccessors[i1] != null))
        {
          if (this.outBindAccessors[i1].internalOtype != null) {
            this.oac.init((OracleTypeADT)((TypeAccessor)this.outBindAccessors[i1]).internalOtype, m, m == 109 ? 11 : 4000);
          }

        }
        else if ((this.parameterOtype != null) && (this.parameterOtype[0] != null))
        {
          this.oac.init(this.parameterOtype[0][i1], m, m == 109 ? 11 : 4000);
        }
        else
        {
          DatabaseError.throwSqlException("INTERNAL ERROR: Binding NAMED_TYPE but no type defined", null, -1);
        }

      }
      else if (m == 994)
      {
        int[] arrayOfInt = this.oracleStatement.returnParamMeta;
        m = arrayOfInt[(3 + i1 * 3 + 0)];

        k = arrayOfInt[(3 + i1 * 3 + 2)];

        if ((m == 109) || (m == 111))
        {
          TypeAccessor localTypeAccessor = (TypeAccessor)this.oracleStatement.returnParamAccessors[i1];

          this.oac.init((OracleTypeADT)localTypeAccessor.internalOtype, (short)m, m == 109 ? 11 : 4000);
        }
        else
        {
          this.oac.init((short)m, k, this.dbCharSet, this.NCharSet, s);
        }

      }
      else
      {
        k = arrayOfShort[(j + 1)] & 0xFFFF;

        if (k == 0)
        {
          if ((this.typeOfStatement == 1) || ((this.oracleStatement.connection.versionNumber >= 10200) && (this.typeOfStatement == 4)))
          {
            k = 32512;
          }
          else if (this.typeOfStatement == 4)
          {
            int i3 = arrayOfShort[(j + 2)] & 0xFFFF;

            k = i3 > 4001 ? i3 : 4001;
          }
          else
          {
            k = arrayOfShort[(j + 2)] & 0xFFFF;

            if (m == 996) {
              k *= 2;
            }
            else if (k > 1) {
              k--;
            }

            if (s == 2)
            {
              k *= this.conversion.maxNCharSize;
            }
            else if ((((T4CConnection)this.oracleStatement.connection).retainV9BehaviorForLong) && (k <= 4000))
            {
              k = Math.min(k * this.conversion.sMaxCharSize, 4000);
            }
            else
            {
              k *= this.conversion.sMaxCharSize;
            }

          }

        }

        if ((m == 9) || (m == 996)) {
          m = 1;
        }
        this.oac.init((short)m, k, this.dbCharSet, this.NCharSet, s);
      }

      if ((paramArrayOfT4CTTIoac[i1] != null) && (this.oac.isOldSufficient(paramArrayOfT4CTTIoac[i1])))
      {
        continue;
      }

      T4CTTIoac localT4CTTIoac = new T4CTTIoac(this.oac);

      paramArrayOfT4CTTIoac[i1] = localT4CTTIoac;
      i = 1;
    }

    return i;
  }

  void initDefinesDefinition()
    throws SQLException, IOException
  {
    this.defCols = 0;
    for (int i = 0; i < this.definedColumnType.length; i++)
    {
      if (this.definedColumnType[i] == 0)
        break;
      this.defCols += 1;
    }
    this.oacdefDefines = new T4CTTIoac[this.defCols];
    for (i = 0; i < this.oacdefDefines.length; i++)
    {
      this.oacdefDefines[i] = new T4CTTIoac(this.meg);
      short s1 = (short)this.oracleStatement.getInternalType(this.definedColumnType[i]);
      int j = 2147483647;

      short s2 = 1;
      if ((this.definedColumnFormOfUse != null) && (this.definedColumnFormOfUse.length > i) && (this.definedColumnFormOfUse[i] == 2))
      {
        s2 = 2;
      }
      if (s1 == 8) {
        s1 = 1;
      } else if (s1 == 24) {
        s1 = 23;
      } else if ((s1 == 1) || (s1 == 96))
      {
        s1 = 1;

        j = 4000 * this.conversion.sMaxCharSize;

        if ((this.definedColumnSize != null) && (this.definedColumnSize.length > i) && (this.definedColumnSize[i] > 0))
        {
          j = this.definedColumnSize[i] * this.conversion.sMaxCharSize;
        }
      } else if (s1 == 23) {
        j = 4000;
      }
      this.oacdefDefines[i].init(s1, j, this.dbCharSet, this.NCharSet, s2);
    }
  }

  void marshalBindsTypes(T4CTTIoac[] paramArrayOfT4CTTIoac)
    throws SQLException, IOException
  {
    if (paramArrayOfT4CTTIoac == null) {
      return;
    }
    for (int i = 0; i < paramArrayOfT4CTTIoac.length; i++)
    {
      paramArrayOfT4CTTIoac[i].marshal();
    }
  }

  void marshalBinds(int[] paramArrayOfInt)
    throws SQLException, IOException
  {
    int i = this.bindIndicators[(this.bindIndicatorSubRange + 2)] & 0xFFFF;

    for (int j = 0; j < i; j++)
    {
      int k = this.oracleStatement.firstRowInBatch + j;
      InputStream[] arrayOfInputStream = null;
      if (this.parameterStream != null)
        arrayOfInputStream = this.parameterStream[k];
      byte[][] arrayOfByte = (byte[][])null;
      if (this.parameterDatum != null)
        arrayOfByte = this.parameterDatum[k];
      OracleTypeADT[] arrayOfOracleTypeADT = null;
      if (this.parameterOtype != null) {
        arrayOfOracleTypeADT = this.parameterOtype[k];
      }

      this.rxd.marshal(this.bindBytes, this.bindChars, this.bindIndicators, this.bindIndicatorSubRange, this.tmpBindsByteArray, this.conversion, arrayOfInputStream, arrayOfByte, arrayOfOracleTypeADT, this.ibtBindBytes, this.ibtBindChars, this.ibtBindIndicators, null, j, paramArrayOfInt, this.plsql, this.oracleStatement.returnParamMeta);
    }
  }

  long setOptions(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    throws SQLException
  {
    long l = 0L;

    if ((paramBoolean1) && (!paramBoolean2) && (!paramBoolean3)) {
      l |= 1L;
    } else if ((paramBoolean1) && (paramBoolean2) && (!paramBoolean3)) {
      l = 32801L; } else {
      if ((paramBoolean2) && (paramBoolean3))
      {
        if (paramBoolean1) {
          l |= 1L;
        }

      }

      switch (this.typeOfStatement)
      {
      case 0:
        l |= 32864L;

        break;
      case 1:
      case 4:
        if (this.numberOfBindPositions > 0)
        {
          l |= 0x420 | (this.oracleStatement.connection.autoCommitSet ? 256 : 0);

          if (!this.sendBindsDefinition) break;
          l |= 8L;
        }
        else {
          l |= 0x20 | (this.oracleStatement.connection.autoCommitSet ? 256 : 0);
        }

        break;
      case 2:
      case 3:
        if (this.oracleStatement.returnParamAccessors != null) {
          l |= 0x420 | (this.oracleStatement.connection.autoCommitSet ? 256 : 0);
        }
        else {
          l |= 0x8020 | (this.oracleStatement.connection.autoCommitSet ? 256 : 0);
        }

        break;
      default:
        DatabaseError.throwSqlException(432); break;

        if ((!paramBoolean1) && (!paramBoolean2) && (paramBoolean3)) {
          l = 32832L;
        }
        else
        {
          DatabaseError.throwSqlException(432);
        }
      }
    }
    if ((this.typeOfStatement != 1) && (this.typeOfStatement != 4))
    {
      if ((paramBoolean1) || (paramBoolean2) || (!paramBoolean3))
      {
        if ((this.numberOfBindPositions > 0) && (this.sendBindsDefinition)) {
          l |= 8L;
        }
      }
      if ((this.meg.versionNumber >= 9000) && (this.sendDefines))
      {
        l |= 16L;
      }
    }

    l &= -1L;

    return l;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8Oall
 * JD-Core Version:    0.6.0
 */