package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

public abstract class OracleCallableStatement extends OraclePreparedStatement
  implements oracle.jdbc.internal.OracleCallableStatement
{
  boolean atLeastOneOrdinalParameter = false;
  boolean atLeastOneNamedParameter = false;

  String[] namedParameters = new String[8];

  int parameterCount = 0;

  final String errMsgMixedBind = "Ordinal binding and Named binding cannot be combined!";

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  OracleCallableStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    this(paramPhysicalConnection, paramString, paramInt1, paramInt2, 1003, 1007);
  }

  OracleCallableStatement(PhysicalConnection paramPhysicalConnection, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    super(paramPhysicalConnection, paramString, 1, paramInt2, paramInt3, paramInt4);

    this.statementType = 2;
  }

  void registerOutParameterInternal(int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString)
    throws SQLException
  {
    int i = paramInt1 - 1;
    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (paramInt2 == 0)
      DatabaseError.throwSqlException(4);
    int j = getInternalType(paramInt2);

    resetBatch();
    this.currentRowNeedToPrepareBinds = true;

    if (this.currentRowBindAccessors == null) {
      this.currentRowBindAccessors = new Accessor[this.numberOfBindPositions];
    }
    this.currentRowBindAccessors[i] = allocateAccessor(j, paramInt2, i + 1, paramInt4, this.currentRowFormOfUse[i], paramString, true);
  }

  public void registerOutParameter(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if ((paramString == null) || (paramString.length() == 0)) {
      DatabaseError.throwSqlException(60, "empty Object name");
    }

    synchronized (this.connection)
    {
      synchronized (this)
      {
        registerOutParameterInternal(paramInt1, paramInt2, 0, 0, paramString);
      }
    }
  }

  /** @deprecated */
  public synchronized void registerOutParameterBytes(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    registerOutParameterInternal(paramInt1, paramInt2, paramInt3, paramInt4, null);
  }

  /** @deprecated */
  public synchronized void registerOutParameterChars(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    registerOutParameterInternal(paramInt1, paramInt2, paramInt3, paramInt4, null);
  }

  public synchronized void registerOutParameter(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    registerOutParameterInternal(paramInt1, paramInt2, paramInt3, paramInt4, null);
  }

  public synchronized void registerOutParameter(String paramString, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    registerOutParameter(i, paramInt1, paramInt2, paramInt3);
  }

  void resetBatch()
  {
    this.batch = 1;
  }

  public synchronized void setExecuteBatch(int paramInt)
    throws SQLException
  {
  }

  public synchronized int sendBatch()
    throws SQLException
  {
    return this.validRows;
  }

  public void registerOutParameter(int paramInt1, int paramInt2)
    throws SQLException
  {
    registerOutParameter(paramInt1, paramInt2, 0, -1);
  }

  public void registerOutParameter(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    registerOutParameter(paramInt1, paramInt2, paramInt3, -1);
  }

  public boolean wasNull()
    throws SQLException
  {
    return wasNullValue();
  }

  public String getString(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getString(this.currentRank);
  }

  public Datum getOracleObject(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getOracleObject(this.currentRank);
  }

  public ROWID getROWID(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getROWID(this.currentRank);
  }

  public NUMBER getNUMBER(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getNUMBER(this.currentRank);
  }

  public DATE getDATE(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getDATE(this.currentRank);
  }

  public INTERVALYM getINTERVALYM(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getINTERVALYM(this.currentRank);
  }

  public INTERVALDS getINTERVALDS(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getINTERVALDS(this.currentRank);
  }

  public TIMESTAMP getTIMESTAMP(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTIMESTAMP(this.currentRank);
  }

  public TIMESTAMPTZ getTIMESTAMPTZ(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTIMESTAMPTZ(this.currentRank);
  }

  public TIMESTAMPLTZ getTIMESTAMPLTZ(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTIMESTAMPLTZ(this.currentRank);
  }

  public REF getREF(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getREF(this.currentRank);
  }

  public ARRAY getARRAY(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getARRAY(this.currentRank);
  }

  public STRUCT getSTRUCT(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getSTRUCT(this.currentRank);
  }

  public OPAQUE getOPAQUE(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getOPAQUE(this.currentRank);
  }

  public CHAR getCHAR(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getCHAR(this.currentRank);
  }

  public Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getCharacterStream(this.currentRank);
  }

  public RAW getRAW(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getRAW(this.currentRank);
  }

  public BLOB getBLOB(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBLOB(this.currentRank);
  }

  public CLOB getCLOB(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getCLOB(this.currentRank);
  }

  public BFILE getBFILE(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBFILE(this.currentRank);
  }

  public boolean getBoolean(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBoolean(this.currentRank);
  }

  public byte getByte(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getByte(this.currentRank);
  }

  public short getShort(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getShort(this.currentRank);
  }

  public int getInt(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getInt(this.currentRank);
  }

  public long getLong(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getLong(this.currentRank);
  }

  public float getFloat(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getFloat(this.currentRank);
  }

  public double getDouble(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getDouble(this.currentRank);
  }

  public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt1 <= 0) || (paramInt1 > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt1 - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt1;

    if (this.streamList != null) {
      closeUsedStreams(paramInt1);
    }

    return localAccessor.getBigDecimal(this.currentRank);
  }

  public byte[] getBytes(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBytes(this.currentRank);
  }

  public byte[] privateGetBytes(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.privateGetBytes(this.currentRank);
  }

  public Date getDate(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getDate(this.currentRank);
  }

  public Time getTime(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTime(this.currentRank);
  }

  public Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTimestamp(this.currentRank);
  }

  public InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getAsciiStream(this.currentRank);
  }

  public InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getUnicodeStream(this.currentRank);
  }

  public InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBinaryStream(this.currentRank);
  }

  public Object getObject(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getObject(this.currentRank);
  }

  public Object getAnyDataEmbeddedObject(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getAnyDataEmbeddedObject(this.currentRank);
  }

  public Object getCustomDatum(int paramInt, CustomDatumFactory paramCustomDatumFactory)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getCustomDatum(this.currentRank, paramCustomDatumFactory);
  }

  public Object getORAData(int paramInt, ORADataFactory paramORADataFactory)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getORAData(this.currentRank, paramORADataFactory);
  }

  public ResultSet getCursor(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getCursor(this.currentRank);
  }

  public synchronized void clearParameters()
    throws SQLException
  {
    super.clearParameters();
  }

  public Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getObject(this.currentRank, paramMap);
  }

  public Ref getRef(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getREF(this.currentRank);
  }

  public Blob getBlob(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBLOB(this.currentRank);
  }

  public Clob getClob(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getCLOB(this.currentRank);
  }

  public Array getArray(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getARRAY(this.currentRank);
  }

  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getBigDecimal(this.currentRank);
  }

  public Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getDate(this.currentRank, paramCalendar);
  }

  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTime(this.currentRank, paramCalendar);
  }

  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getTimestamp(this.currentRank, paramCalendar);
  }

  public void addBatch()
    throws SQLException
  {
    if (this.currentRowBindAccessors != null)
    {
      DatabaseError.throwSqlException(90, "Stored procedure with out or inout parameters cannot be batched");
    }

    super.addBatch();
  }

  protected void alwaysOnClose()
    throws SQLException
  {
    this.sqlObject.resetNamedParameters();

    this.parameterCount = 0;
    this.atLeastOneOrdinalParameter = false;
    this.atLeastOneNamedParameter = false;

    super.alwaysOnClose();
  }

  public void registerOutParameter(String paramString, int paramInt)
    throws SQLException
  {
    registerOutParameter(paramString, paramInt, 0, -1);
  }

  public void registerOutParameter(String paramString, int paramInt1, int paramInt2)
    throws SQLException
  {
    registerOutParameter(paramString, paramInt1, paramInt2, -1);
  }

  public void registerOutParameter(String paramString1, int paramInt, String paramString2)
    throws SQLException
  {
    int i = addNamedPara(paramString1);
    registerOutParameter(i, paramInt, paramString2);
  }

  public URL getURL(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getURL(this.currentRank);
  }

  public void setURL(String paramString, URL paramURL)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setURLInternal(i, paramURL);
  }

  public void setNull(String paramString, int paramInt)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setNullInternal(i, paramInt);
  }

  public void setBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBooleanInternal(i, paramBoolean);
  }

  public void setByte(String paramString, byte paramByte)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setByteInternal(i, paramByte);
  }

  public void setShort(String paramString, short paramShort)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setShortInternal(i, paramShort);
  }

  public void setInt(String paramString, int paramInt)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setIntInternal(i, paramInt);
  }

  public void setLong(String paramString, long paramLong)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setLongInternal(i, paramLong);
  }

  public void setFloat(String paramString, float paramFloat)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setFloatInternal(i, paramFloat);
  }

  public void setBinaryFloat(String paramString, float paramFloat)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBinaryFloatInternal(i, paramFloat);
  }

  public void setBinaryFloat(String paramString, BINARY_FLOAT paramBINARY_FLOAT)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBinaryFloatInternal(i, paramBINARY_FLOAT);
  }

  public void setDouble(String paramString, double paramDouble)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setDoubleInternal(i, paramDouble);
  }

  public void setBinaryDouble(String paramString, double paramDouble)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBinaryDoubleInternal(i, paramDouble);
  }

  public void setBinaryDouble(String paramString, BINARY_DOUBLE paramBINARY_DOUBLE)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBinaryDoubleInternal(i, paramBINARY_DOUBLE);
  }

  public void setBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBigDecimalInternal(i, paramBigDecimal);
  }

  public void setString(String paramString1, String paramString2)
    throws SQLException
  {
    int i = addNamedPara(paramString1);
    setStringInternal(i, paramString2);
  }

  public void setStringForClob(String paramString1, String paramString2)
    throws SQLException
  {
    int i = addNamedPara(paramString1);
    if ((paramString2 == null) || (paramString2.length() == 0))
    {
      setNull(i, 2005);
      return;
    }
    setStringForClob(i, paramString2);
  }

  public void setStringForClob(int paramInt, String paramString)
    throws SQLException
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      setNull(paramInt, 2005);
      return;
    }
    synchronized (this.connection)
    {
      synchronized (this.connection)
      {
        setStringForClobCritical(paramInt, paramString);
      }
    }
  }

  public void setBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBytesInternal(i, paramArrayOfByte);
  }

  public void setBytesForBlob(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBytesForBlob(i, paramArrayOfByte);
  }

  public void setBytesForBlob(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0))
    {
      setNull(paramInt, 2004);
      return;
    }
    synchronized (this.connection)
    {
      synchronized (this.connection)
      {
        setBytesForBlobCritical(paramInt, paramArrayOfByte);
      }
    }
  }

  public void setDate(String paramString, Date paramDate)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setDateInternal(i, paramDate);
  }

  public void setTime(String paramString, Time paramTime)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setTimeInternal(i, paramTime);
  }

  public void setTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setTimestampInternal(i, paramTimestamp);
  }

  public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setAsciiStreamInternal(i, paramInputStream, paramInt);
  }

  public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setBinaryStreamInternal(i, paramInputStream, paramInt);
  }

  public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setObjectInternal(i, paramObject, paramInt1, paramInt2);
  }

  public void setObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    setObject(paramString, paramObject, paramInt, 0);
  }

  public void setObject(String paramString, Object paramObject)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setObjectInternal(i, paramObject);
  }

  public void setCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setCharacterStreamInternal(i, paramReader, paramInt);
  }

  public void setDate(String paramString, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setDateInternal(i, paramDate, paramCalendar);
  }

  public void setTime(String paramString, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setTimeInternal(i, paramTime, paramCalendar);
  }

  public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    int i = addNamedPara(paramString);
    setTimestampInternal(i, paramTimestamp, paramCalendar);
  }

  public void setNull(String paramString1, int paramInt, String paramString2)
    throws SQLException
  {
    int i = addNamedPara(paramString1);
    setNullInternal(i, paramInt, paramString2);
  }

  public String getString(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getString(this.currentRank);
  }

  public boolean getBoolean(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getBoolean(this.currentRank);
  }

  public byte getByte(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getByte(this.currentRank);
  }

  public short getShort(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getShort(this.currentRank);
  }

  public int getInt(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getInt(this.currentRank);
  }

  public long getLong(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getLong(this.currentRank);
  }

  public float getFloat(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getFloat(this.currentRank);
  }

  public double getDouble(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getDouble(this.currentRank);
  }

  public byte[] getBytes(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getBytes(this.currentRank);
  }

  public Date getDate(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getDate(this.currentRank);
  }

  public Time getTime(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getTime(this.currentRank);
  }

  public Timestamp getTimestamp(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getTimestamp(this.currentRank);
  }

  public Object getObject(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getObject(this.currentRank);
  }

  public BigDecimal getBigDecimal(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getBigDecimal(this.currentRank);
  }

  public Object getObject(String paramString, Map paramMap)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getObject(this.currentRank, paramMap);
  }

  public Ref getRef(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getREF(this.currentRank);
  }

  public Blob getBlob(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getBLOB(this.currentRank);
  }

  public Clob getClob(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getCLOB(this.currentRank);
  }

  public Array getArray(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getARRAY(this.currentRank);
  }

  public Date getDate(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getDate(this.currentRank, paramCalendar);
  }

  public Time getTime(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getTime(this.currentRank, paramCalendar);
  }

  public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getTimestamp(this.currentRank, paramCalendar);
  }

  public URL getURL(String paramString)
    throws SQLException
  {
    if (!this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i])
        break;
    }
    i++;

    Accessor localAccessor = null;
    if ((i <= 0) || (i > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(i - 1)]) == null))
    {
      DatabaseError.throwSqlException(6);
    }
    this.lastIndex = i;

    if (this.streamList != null) {
      closeUsedStreams(i);
    }

    return localAccessor.getURL(this.currentRank);
  }

  public synchronized void registerIndexTableOutParameter(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws SQLException
  {
    int i = paramInt1 - 1;
    if ((i < 0) || (paramInt1 > this.numberOfBindPositions)) {
      DatabaseError.throwSqlException(3);
    }
    int j = getInternalType(paramInt3);

    resetBatch();
    this.currentRowNeedToPrepareBinds = true;

    if (this.currentRowBindAccessors == null) {
      this.currentRowBindAccessors = new Accessor[this.numberOfBindPositions];
    }
    this.currentRowBindAccessors[i] = allocateIndexTableAccessor(paramInt3, j, paramInt4, paramInt2, this.currentRowFormOfUse[i], true);

    this.hasIbtBind = true;
  }

  PlsqlIndexTableAccessor allocateIndexTableAccessor(int paramInt1, int paramInt2, int paramInt3, int paramInt4, short paramShort, boolean paramBoolean)
    throws SQLException
  {
    return new PlsqlIndexTableAccessor(this, paramInt1, paramInt2, paramInt3, paramInt4, paramShort, paramBoolean);
  }

  public synchronized Object getPlsqlIndexTable(int paramInt)
    throws SQLException
  {
    Datum[] arrayOfDatum = getOraclePlsqlIndexTable(paramInt);

    PlsqlIndexTableAccessor localPlsqlIndexTableAccessor = (PlsqlIndexTableAccessor)this.outBindAccessors[(paramInt - 1)];

    int i = localPlsqlIndexTableAccessor.elementInternalType;

    Object localObject = null;

    switch (i)
    {
    case 9:
      localObject = new String[arrayOfDatum.length];
      break;
    case 6:
      localObject = new BigDecimal[arrayOfDatum.length];
      break;
    default:
      DatabaseError.throwSqlException(1, "Invalid column type");
    }

    for (int j = 0; j < localObject.length; j++) {
      localObject[j] = ((arrayOfDatum[j] != null) && (arrayOfDatum[j].getLength() != 0L) ? arrayOfDatum[j].toJdbc() : null);
    }

    return localObject;
  }

  public synchronized Object getPlsqlIndexTable(int paramInt, Class paramClass)
    throws SQLException
  {
    Datum[] arrayOfDatum = getOraclePlsqlIndexTable(paramInt);

    if ((paramClass == null) || (!paramClass.isPrimitive())) {
      DatabaseError.throwSqlException(68);
    }
    String str = paramClass.getName();
    Object localObject;
    int i;
    if (str.equals("byte"))
    {
      localObject = new byte[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++)
        localObject[i] = (arrayOfDatum[i] != null ? arrayOfDatum[i].byteValue() : 0);
      return localObject;
    }
    if (str.equals("char"))
    {
      localObject = new char[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? (char)arrayOfDatum[i].intValue() : 0);
      }
      return localObject;
    }
    if (str.equals("double"))
    {
      localObject = new double[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? arrayOfDatum[i].doubleValue() : 0.0D);
      }
      return localObject;
    }
    if (str.equals("float"))
    {
      localObject = new float[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? arrayOfDatum[i].floatValue() : 0.0F);
      }
      return localObject;
    }
    if (str.equals("int"))
    {
      localObject = new int[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? arrayOfDatum[i].intValue() : 0);
      }
      return localObject;
    }
    if (str.equals("long"))
    {
      localObject = new long[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? arrayOfDatum[i].longValue() : 0L);
      }
      return localObject;
    }
    if (str.equals("short"))
    {
      localObject = new short[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? (short)arrayOfDatum[i].intValue() : 0);
      }
      return localObject;
    }
    if (str.equals("boolean"))
    {
      localObject = new boolean[arrayOfDatum.length];
      for (i = 0; i < arrayOfDatum.length; i++) {
        localObject[i] = ((arrayOfDatum[i] != null) && (arrayOfDatum[i].getLength() != 0L) ? arrayOfDatum[i].booleanValue() : 0);
      }
      return localObject;
    }

    DatabaseError.throwSqlException(23);

    return null;
  }

  public synchronized Datum[] getOraclePlsqlIndexTable(int paramInt)
    throws SQLException
  {
    if (this.atLeastOneNamedParameter)
    {
      DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
    }

    Accessor localAccessor = null;
    if ((paramInt <= 0) || (paramInt > this.numberOfBindPositions) || ((localAccessor = this.outBindAccessors[(paramInt - 1)]) == null))
    {
      DatabaseError.throwSqlException(3);
    }
    this.lastIndex = paramInt;

    if (this.streamList != null) {
      closeUsedStreams(paramInt);
    }

    return localAccessor.getOraclePlsqlIndexTable(this.currentRank);
  }

  public boolean execute()
    throws SQLException
  {
    synchronized (this.connection) {
      synchronized (this) {
        ensureOpen();
        if ((this.atLeastOneNamedParameter) && (this.atLeastOneOrdinalParameter)) {
          DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        if (this.sqlObject.setNamedParameters(this.parameterCount, this.namedParameters))
          this.needToParse = true;
        return super.execute();
      }
    }
  }

  public int executeUpdate()
    throws SQLException
  {
    synchronized (this.connection) {
      synchronized (this) {
        ensureOpen();
        if ((this.atLeastOneNamedParameter) && (this.atLeastOneOrdinalParameter)) {
          DatabaseError.throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        if (this.sqlObject.setNamedParameters(this.parameterCount, this.namedParameters))
          this.needToParse = true;
        return super.executeUpdate();
      }
    }
  }

  public synchronized void setNull(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setNullInternal(paramInt1, paramInt2, paramString);
  }

  public synchronized void setNull(int paramInt1, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setNullInternal(paramInt1, paramInt2);
  }

  public synchronized void setBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBooleanInternal(paramInt, paramBoolean);
  }

  public synchronized void setByte(int paramInt, byte paramByte)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setByteInternal(paramInt, paramByte);
  }

  public synchronized void setShort(int paramInt, short paramShort)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setShortInternal(paramInt, paramShort);
  }

  public synchronized void setInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setIntInternal(paramInt1, paramInt2);
  }

  public synchronized void setLong(int paramInt, long paramLong)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setLongInternal(paramInt, paramLong);
  }

  public synchronized void setFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setFloatInternal(paramInt, paramFloat);
  }

  public synchronized void setBinaryFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBinaryFloatInternal(paramInt, paramFloat);
  }

  public synchronized void setBinaryFloat(int paramInt, BINARY_FLOAT paramBINARY_FLOAT)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBinaryFloatInternal(paramInt, paramBINARY_FLOAT);
  }

  public synchronized void setBinaryDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBinaryDoubleInternal(paramInt, paramDouble);
  }

  public synchronized void setBinaryDouble(int paramInt, BINARY_DOUBLE paramBINARY_DOUBLE)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBinaryDoubleInternal(paramInt, paramBINARY_DOUBLE);
  }

  public synchronized void setDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setDoubleInternal(paramInt, paramDouble);
  }

  public synchronized void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBigDecimalInternal(paramInt, paramBigDecimal);
  }

  public synchronized void setString(int paramInt, String paramString)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setStringInternal(paramInt, paramString);
  }

  public synchronized void setFixedCHAR(int paramInt, String paramString)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setFixedCHARInternal(paramInt, paramString);
  }

  public synchronized void setCursor(int paramInt, ResultSet paramResultSet)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setCursorInternal(paramInt, paramResultSet);
  }

  public synchronized void setROWID(int paramInt, ROWID paramROWID)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setROWIDInternal(paramInt, paramROWID);
  }

  public synchronized void setRAW(int paramInt, RAW paramRAW)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setRAWInternal(paramInt, paramRAW);
  }

  public synchronized void setCHAR(int paramInt, CHAR paramCHAR)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setCHARInternal(paramInt, paramCHAR);
  }

  public synchronized void setDATE(int paramInt, DATE paramDATE)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setDATEInternal(paramInt, paramDATE);
  }

  public synchronized void setNUMBER(int paramInt, NUMBER paramNUMBER)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setNUMBERInternal(paramInt, paramNUMBER);
  }

  public synchronized void setBLOB(int paramInt, BLOB paramBLOB)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBLOBInternal(paramInt, paramBLOB);
  }

  public synchronized void setBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBlobInternal(paramInt, paramBlob);
  }

  public synchronized void setCLOB(int paramInt, CLOB paramCLOB)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setCLOBInternal(paramInt, paramCLOB);
  }

  public synchronized void setClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setClobInternal(paramInt, paramClob);
  }

  public synchronized void setBFILE(int paramInt, BFILE paramBFILE)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBFILEInternal(paramInt, paramBFILE);
  }

  public synchronized void setBfile(int paramInt, BFILE paramBFILE)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBfileInternal(paramInt, paramBFILE);
  }

  public synchronized void setBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBytesInternal(paramInt, paramArrayOfByte);
  }

  public synchronized void setInternalBytes(int paramInt1, byte[] paramArrayOfByte, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setInternalBytesInternal(paramInt1, paramArrayOfByte, paramInt2);
  }

  public synchronized void setDate(int paramInt, Date paramDate)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setDateInternal(paramInt, paramDate);
  }

  public synchronized void setTime(int paramInt, Time paramTime)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTimeInternal(paramInt, paramTime);
  }

  public synchronized void setTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTimestampInternal(paramInt, paramTimestamp);
  }

  public synchronized void setINTERVALYM(int paramInt, INTERVALYM paramINTERVALYM)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setINTERVALYMInternal(paramInt, paramINTERVALYM);
  }

  public synchronized void setINTERVALDS(int paramInt, INTERVALDS paramINTERVALDS)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setINTERVALDSInternal(paramInt, paramINTERVALDS);
  }

  public synchronized void setTIMESTAMP(int paramInt, TIMESTAMP paramTIMESTAMP)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTIMESTAMPInternal(paramInt, paramTIMESTAMP);
  }

  public synchronized void setTIMESTAMPTZ(int paramInt, TIMESTAMPTZ paramTIMESTAMPTZ)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTIMESTAMPTZInternal(paramInt, paramTIMESTAMPTZ);
  }

  public synchronized void setTIMESTAMPLTZ(int paramInt, TIMESTAMPLTZ paramTIMESTAMPLTZ)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTIMESTAMPLTZInternal(paramInt, paramTIMESTAMPLTZ);
  }

  public synchronized void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setAsciiStreamInternal(paramInt1, paramInputStream, paramInt2);
  }

  public synchronized void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setBinaryStreamInternal(paramInt1, paramInputStream, paramInt2);
  }

  public synchronized void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setUnicodeStreamInternal(paramInt1, paramInputStream, paramInt2);
  }

  public synchronized void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setCharacterStreamInternal(paramInt1, paramReader, paramInt2);
  }

  public synchronized void setDate(int paramInt, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setDateInternal(paramInt, paramDate, paramCalendar);
  }

  public synchronized void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTimeInternal(paramInt, paramTime, paramCalendar);
  }

  public synchronized void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setTimestampInternal(paramInt, paramTimestamp, paramCalendar);
  }

  public synchronized void setURL(int paramInt, URL paramURL)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setURLInternal(paramInt, paramURL);
  }

  public void setArray(int paramInt, Array paramArray)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setArrayInternal(paramInt, paramArray);
  }

  public void setARRAY(int paramInt, ARRAY paramARRAY)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setARRAYInternal(paramInt, paramARRAY);
  }

  public void setOPAQUE(int paramInt, OPAQUE paramOPAQUE)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setOPAQUEInternal(paramInt, paramOPAQUE);
  }

  public void setStructDescriptor(int paramInt, StructDescriptor paramStructDescriptor)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setStructDescriptorInternal(paramInt, paramStructDescriptor);
  }

  public void setSTRUCT(int paramInt, STRUCT paramSTRUCT)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setSTRUCTInternal(paramInt, paramSTRUCT);
  }

  public void setCustomDatum(int paramInt, CustomDatum paramCustomDatum)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setCustomDatumInternal(paramInt, paramCustomDatum);
  }

  public void setORAData(int paramInt, ORAData paramORAData)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setORADataInternal(paramInt, paramORAData);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setObjectInternal(paramInt1, paramObject, paramInt2, paramInt3);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setObjectInternal(paramInt1, paramObject, paramInt2);
  }

  public void setRefType(int paramInt, REF paramREF)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setRefTypeInternal(paramInt, paramREF);
  }

  public void setRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setRefInternal(paramInt, paramRef);
  }

  public void setREF(int paramInt, REF paramREF)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setREFInternal(paramInt, paramREF);
  }

  public void setObject(int paramInt, Object paramObject)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setObjectInternal(paramInt, paramObject);
  }

  public void setOracleObject(int paramInt, Datum paramDatum)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setOracleObjectInternal(paramInt, paramDatum);
  }

  public synchronized void setPlsqlIndexTable(int paramInt1, Object paramObject, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
    throws SQLException
  {
    this.atLeastOneOrdinalParameter = true;
    setPlsqlIndexTableInternal(paramInt1, paramObject, paramInt2, paramInt3, paramInt4, paramInt5);
  }

  int addNamedPara(String paramString)
    throws SQLException
  {
    String str = paramString.toUpperCase().intern();

    for (int i = 0; i < this.parameterCount; i++)
    {
      if (str == this.namedParameters[i]) {
        return i + 1;
      }
    }
    if (this.parameterCount >= this.namedParameters.length)
    {
      String[] arrayOfString = new String[this.namedParameters.length * 2];
      System.arraycopy(this.namedParameters, 0, arrayOfString, 0, this.namedParameters.length);
      this.namedParameters = arrayOfString;
    }

    this.namedParameters[(this.parameterCount++)] = str;

    this.atLeastOneNamedParameter = true;
    return this.parameterCount;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleCallableStatement
 * JD-Core Version:    0.6.0
 */