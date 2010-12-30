package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
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
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

class UpdatableResultSet extends BaseResultSet
{
  static final int concurrencyType = 1008;
  static final int beginColumnIndex = 1;
  PhysicalConnection connection;
  OracleResultSet resultSet;
  boolean isCachedRset;
  ScrollRsetStatement scrollStmt;
  ResultSetMetaData rsetMetaData;
  private int rsetType;
  private int columnCount;
  private OraclePreparedStatement deleteStmt;
  private OraclePreparedStatement insertStmt;
  private OraclePreparedStatement updateStmt;
  private int[] indexColsChanged;
  private Object[] rowBuffer;
  private boolean[] m_nullIndicator;
  private int[][] typeInfo;
  private boolean isInserting;
  private boolean isUpdating;
  private int wasNull;
  private static final int VALUE_NULL = 1;
  private static final int VALUE_NOT_NULL = 2;
  private static final int VALUE_UNKNOWN = 3;
  private static final int VALUE_IN_RSET = 4;
  private static final int ASCII_STREAM = 1;
  private static final int BINARY_STREAM = 2;
  private static final int UNICODE_STREAM = 3;
  private static int _MIN_STREAM_SIZE = 4000;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  UpdatableResultSet(ScrollRsetStatement paramScrollRsetStatement, ScrollableResultSet paramScrollableResultSet, int paramInt1, int paramInt2)
    throws SQLException
  {
    init(paramScrollRsetStatement, paramScrollableResultSet, paramInt1, paramInt2);

    paramScrollableResultSet.resetBeginColumnIndex();

    this.isCachedRset = true;
  }

  UpdatableResultSet(ScrollRsetStatement paramScrollRsetStatement, OracleResultSetImpl paramOracleResultSetImpl, int paramInt1, int paramInt2)
    throws SQLException
  {
    init(paramScrollRsetStatement, paramOracleResultSetImpl, paramInt1, paramInt2);

    this.isCachedRset = false;
  }

  private void init(ScrollRsetStatement paramScrollRsetStatement, OracleResultSet paramOracleResultSet, int paramInt1, int paramInt2)
    throws SQLException
  {
    if ((paramScrollRsetStatement == null) || (paramOracleResultSet == null) || (paramInt2 != 1008)) {
      DatabaseError.throwSqlException(68);
    }
    this.connection = ((OracleStatement)paramScrollRsetStatement).connection;
    this.resultSet = paramOracleResultSet;
    this.scrollStmt = paramScrollRsetStatement;
    this.rsetType = paramInt1;
    this.autoRefetch = paramScrollRsetStatement.getAutoRefetch();
    this.deleteStmt = null;
    this.insertStmt = null;
    this.updateStmt = null;
    this.indexColsChanged = null;
    this.rowBuffer = null;
    this.m_nullIndicator = null;
    this.typeInfo = ((int[][])null);
    this.isInserting = false;
    this.isUpdating = false;
    this.wasNull = -1;
    this.rsetMetaData = null;
    this.columnCount = 0;
  }

  public synchronized void close()
    throws SQLException
  {
    if (this.resultSet != null) {
      this.resultSet.close();
    }
    if (this.insertStmt != null) {
      this.insertStmt.close();
    }
    if (this.updateStmt != null) {
      this.updateStmt.close();
    }
    if (this.deleteStmt != null) {
      this.deleteStmt.close();
    }
    if (this.scrollStmt != null) {
      this.scrollStmt.notifyCloseRset();
    }
    cancelRowInserts();

    this.connection = null;
    this.resultSet = null;
    this.scrollStmt = null;
    this.rsetMetaData = null;
    this.scrollStmt = null;
    this.deleteStmt = null;
    this.insertStmt = null;
    this.updateStmt = null;
    this.indexColsChanged = null;
    this.rowBuffer = null;
    this.m_nullIndicator = null;
    this.typeInfo = ((int[][])null);
  }

  public synchronized boolean wasNull() throws SQLException
  {
    switch (this.wasNull)
    {
    case 1:
      return true;
    case 2:
      return false;
    case 4:
      return this.resultSet.wasNull();
    case 3:
    }

    DatabaseError.throwSqlException(24);

    return false;
  }

  int getFirstUserColumnIndex()
  {
    return 1;
  }

  public synchronized Statement getStatement()
    throws SQLException
  {
    return (Statement)this.scrollStmt;
  }

  public SQLWarning getWarnings() throws SQLException
  {
    SQLWarning localSQLWarning1 = this.resultSet.getWarnings();

    if (this.sqlWarning == null) {
      return localSQLWarning1;
    }

    SQLWarning localSQLWarning2 = this.sqlWarning;

    while (localSQLWarning2.getNextWarning() != null) {
      localSQLWarning2 = localSQLWarning2.getNextWarning();
    }
    localSQLWarning2.setNextWarning(localSQLWarning1);

    return this.sqlWarning;
  }

  public void clearWarnings() throws SQLException
  {
    this.sqlWarning = null;

    this.resultSet.clearWarnings();
  }

  public synchronized boolean next()
    throws SQLException
  {
    cancelRowChanges();

    return this.resultSet.next();
  }

  public synchronized boolean isBeforeFirst() throws SQLException
  {
    return this.resultSet.isBeforeFirst();
  }

  public synchronized boolean isAfterLast() throws SQLException
  {
    return this.resultSet.isAfterLast();
  }

  public synchronized boolean isFirst() throws SQLException
  {
    return this.resultSet.isFirst();
  }

  public synchronized boolean isLast() throws SQLException
  {
    return this.resultSet.isLast();
  }

  public synchronized void beforeFirst() throws SQLException
  {
    cancelRowChanges();
    this.resultSet.beforeFirst();
  }

  public synchronized void afterLast() throws SQLException
  {
    cancelRowChanges();
    this.resultSet.afterLast();
  }

  public synchronized boolean first() throws SQLException
  {
    cancelRowChanges();

    return this.resultSet.first();
  }

  public synchronized boolean last() throws SQLException
  {
    cancelRowChanges();

    return this.resultSet.last();
  }

  public synchronized int getRow() throws SQLException
  {
    return this.resultSet.getRow();
  }

  public synchronized boolean absolute(int paramInt) throws SQLException
  {
    cancelRowChanges();

    return this.resultSet.absolute(paramInt);
  }

  public synchronized boolean relative(int paramInt) throws SQLException
  {
    cancelRowChanges();

    return this.resultSet.relative(paramInt);
  }

  public synchronized boolean previous() throws SQLException
  {
    cancelRowChanges();

    return this.resultSet.previous();
  }

  public synchronized Datum getOracleObject(int paramInt)
    throws SQLException
  {
    Datum localDatum = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      setIsNull(localDatum == null);

      localDatum = getRowBufferDatumAt(paramInt);
    }
    else
    {
      setIsNull(4);

      localDatum = this.resultSet.getOracleObject(paramInt + 1);
    }

    return localDatum;
  }

  public synchronized String getString(int paramInt) throws SQLException
  {
    String str = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        str = localDatum.stringValue();
    }
    else
    {
      setIsNull(4);

      str = this.resultSet.getString(paramInt + 1);
    }

    return str;
  }

  public synchronized boolean getBoolean(int paramInt) throws SQLException
  {
    boolean bool = false;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        bool = localDatum.booleanValue();
    }
    else
    {
      setIsNull(4);

      bool = this.resultSet.getBoolean(paramInt + 1);
    }

    return bool;
  }

  public synchronized byte getByte(int paramInt) throws SQLException
  {
    int i = 0;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        i = localDatum.byteValue();
    }
    else
    {
      setIsNull(4);

      i = this.resultSet.getByte(paramInt + 1);
    }

    return i;
  }

  public synchronized short getShort(int paramInt) throws SQLException
  {
    int i = 0;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      long l = getLong(paramInt);

      if ((l > 65537L) || (l < -65538L)) {
        DatabaseError.throwSqlException(26, "getShort");
      }

      i = (short)(int)l;
    }
    else
    {
      setIsNull(4);

      i = this.resultSet.getShort(paramInt + 1);
    }

    return i;
  }

  public synchronized int getInt(int paramInt) throws SQLException
  {
    int i = 0;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        i = localDatum.intValue();
    }
    else
    {
      setIsNull(4);

      i = this.resultSet.getInt(paramInt + 1);
    }

    return i;
  }

  public synchronized long getLong(int paramInt) throws SQLException
  {
    long l = 0L;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        l = localDatum.longValue();
    }
    else
    {
      setIsNull(4);

      l = this.resultSet.getLong(paramInt + 1);
    }

    return l;
  }

  public synchronized float getFloat(int paramInt) throws SQLException
  {
    float f = 0.0F;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        f = localDatum.floatValue();
    }
    else
    {
      setIsNull(4);

      f = this.resultSet.getFloat(paramInt + 1);
    }

    return f;
  }

  public synchronized double getDouble(int paramInt) throws SQLException
  {
    double d = 0.0D;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        d = localDatum.doubleValue();
    }
    else
    {
      setIsNull(4);

      d = this.resultSet.getDouble(paramInt + 1);
    }

    return d;
  }

  public synchronized BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    BigDecimal localBigDecimal = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt1))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt1);

      setIsNull(localDatum == null);

      if (localDatum != null)
        localBigDecimal = localDatum.bigDecimalValue();
    }
    else
    {
      setIsNull(4);

      localBigDecimal = this.resultSet.getBigDecimal(paramInt1 + 1);
    }

    return localBigDecimal;
  }

  public synchronized byte[] getBytes(int paramInt) throws SQLException
  {
    byte[] arrayOfByte = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        arrayOfByte = localDatum.getBytes();
    }
    else
    {
      setIsNull(4);

      arrayOfByte = this.resultSet.getBytes(paramInt + 1);
    }

    return arrayOfByte;
  }

  public synchronized Date getDate(int paramInt) throws SQLException
  {
    Date localDate = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        localDate = localDatum.dateValue();
    }
    else
    {
      setIsNull(4);

      localDate = this.resultSet.getDate(paramInt + 1);
    }

    return localDate;
  }

  public synchronized Time getTime(int paramInt) throws SQLException
  {
    Time localTime = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        localTime = localDatum.timeValue();
    }
    else
    {
      setIsNull(4);

      localTime = this.resultSet.getTime(paramInt + 1);
    }

    return localTime;
  }

  public synchronized Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    Timestamp localTimestamp = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        localTimestamp = localDatum.timestampValue();
    }
    else
    {
      setIsNull(4);

      localTimestamp = this.resultSet.getTimestamp(paramInt + 1);
    }

    return localTimestamp;
  }

  public synchronized InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Object localObject = getRowBufferAt(paramInt);

      setIsNull(localObject == null);

      if (localObject != null)
      {
        if ((localObject instanceof InputStream))
        {
          localInputStream = (InputStream)localObject;
        }
        else
        {
          Datum localDatum = getRowBufferDatumAt(paramInt);

          localInputStream = localDatum.asciiStreamValue();
        }
      }
    }
    else
    {
      setIsNull(4);

      localInputStream = this.resultSet.getAsciiStream(paramInt + 1);
    }

    return localInputStream;
  }

  public synchronized InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Object localObject = getRowBufferAt(paramInt);

      setIsNull(localObject == null);

      if (localObject != null)
      {
        if ((localObject instanceof InputStream))
        {
          localInputStream = (InputStream)localObject;
        }
        else
        {
          Datum localDatum = getRowBufferDatumAt(paramInt);
          DBConversion localDBConversion = this.connection.conversion;
          byte[] arrayOfByte = localDatum.shareBytes();

          if ((localDatum instanceof RAW))
          {
            localInputStream = localDBConversion.ConvertStream(new ByteArrayInputStream(arrayOfByte), 3);
          }
          else if ((localDatum instanceof CHAR))
          {
            localInputStream = localDBConversion.ConvertStream(new ByteArrayInputStream(arrayOfByte), 1);
          }
          else
          {
            DatabaseError.throwSqlException(4, "getUnicodeStream");
          }
        }
      }
    }
    else
    {
      setIsNull(4);

      localInputStream = this.resultSet.getUnicodeStream(paramInt + 1);
    }

    return localInputStream;
  }

  public synchronized InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Object localObject = getRowBufferAt(paramInt);

      setIsNull(localObject == null);

      if (localObject != null)
      {
        if ((localObject instanceof InputStream))
        {
          localInputStream = (InputStream)localObject;
        }
        else
        {
          Datum localDatum = getRowBufferDatumAt(paramInt);

          localInputStream = localDatum.binaryStreamValue();
        }
      }
    }
    else
    {
      setIsNull(4);

      localInputStream = this.resultSet.getBinaryStream(paramInt + 1);
    }

    return localInputStream;
  }

  public synchronized Object getObject(int paramInt) throws SQLException
  {
    Object localObject = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getOracleObject(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        localObject = localDatum.toJdbc();
    }
    else
    {
      setIsNull(4);

      localObject = this.resultSet.getObject(paramInt + 1);
    }

    return localObject;
  }

  public synchronized Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    Reader localReader = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Object localObject = getRowBufferAt(paramInt);

      setIsNull(localObject == null);

      if (localObject != null)
      {
        if ((localObject instanceof Reader))
        {
          localReader = (Reader)localObject;
        }
        else
        {
          Datum localDatum = getRowBufferDatumAt(paramInt);

          localReader = localDatum.characterStreamValue();
        }
      }
    }
    else
    {
      setIsNull(4);

      localReader = this.resultSet.getCharacterStream(paramInt + 1);
    }

    return localReader;
  }

  public synchronized BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    BigDecimal localBigDecimal = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
        localBigDecimal = localDatum.bigDecimalValue();
    }
    else
    {
      setIsNull(4);

      localBigDecimal = this.resultSet.getBigDecimal(paramInt + 1);
    }

    return localBigDecimal;
  }

  public synchronized Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    Object localObject = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getOracleObject(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
      {
        if ((localDatum instanceof STRUCT))
          localObject = ((STRUCT)localDatum).toJdbc(paramMap);
        else
          localObject = localDatum.toJdbc();
      }
    }
    else
    {
      setIsNull(4);

      localObject = this.resultSet.getObject(paramInt + 1, paramMap);
    }

    return localObject;
  }

  public synchronized Ref getRef(int paramInt)
    throws SQLException
  {
    return getREF(paramInt);
  }

  public synchronized Blob getBlob(int paramInt)
    throws SQLException
  {
    return getBLOB(paramInt);
  }

  public synchronized Clob getClob(int paramInt)
    throws SQLException
  {
    return getCLOB(paramInt);
  }

  public synchronized Array getArray(int paramInt)
    throws SQLException
  {
    return getARRAY(paramInt);
  }

  public synchronized Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    Date localDate = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getOracleObject(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
      {
        if ((localDatum instanceof DATE)) {
          localDate = ((DATE)localDatum).dateValue(paramCalendar);
        }
        else {
          DATE localDATE = new DATE(localDatum.stringValue());

          if (localDATE != null)
            localDate = localDATE.dateValue(paramCalendar);
        }
      }
    }
    else
    {
      setIsNull(4);

      localDate = this.resultSet.getDate(paramInt + 1, paramCalendar);
    }

    return localDate;
  }

  public synchronized Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    Time localTime = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getOracleObject(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
      {
        if ((localDatum instanceof DATE)) {
          localTime = ((DATE)localDatum).timeValue(paramCalendar);
        }
        else {
          DATE localDATE = new DATE(localDatum.stringValue());

          if (localDATE != null)
            localTime = localDATE.timeValue(paramCalendar);
        }
      }
    }
    else
    {
      setIsNull(4);

      localTime = this.resultSet.getTime(paramInt + 1, paramCalendar);
    }

    return localTime;
  }

  public synchronized Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    Timestamp localTimestamp = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getOracleObject(paramInt);

      setIsNull(localDatum == null);

      if (localDatum != null)
      {
        if ((localDatum instanceof DATE)) {
          localTimestamp = ((DATE)localDatum).timestampValue(paramCalendar);
        }
        else {
          DATE localDATE = new DATE(localDatum.stringValue());

          if (localDATE != null)
            localTimestamp = localDATE.timestampValue(paramCalendar);
        }
      }
    }
    else
    {
      setIsNull(4);

      localTimestamp = this.resultSet.getTimestamp(paramInt + 1, paramCalendar);
    }

    return localTimestamp;
  }

  public synchronized URL getURL(int paramInt)
    throws SQLException
  {
    URL localURL = null;

    int i = getInternalMetadata().getColumnType(paramInt);
    int j = SQLUtil.getInternalType(i);

    if ((j == 96) || (j == 1) || (j == 8))
    {
      try
      {
        localURL = new URL(getString(paramInt));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        DatabaseError.throwSqlException(136);
      }
    }
    else
    {
      throw new SQLException("Conversion to java.net.URL not supported.");
    }

    return localURL;
  }

  public synchronized ResultSet getCursor(int paramInt)
    throws SQLException
  {
    ResultSet localResultSet = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getOracleObject(paramInt);

      setIsNull(localDatum == null);
      DatabaseError.throwSqlException(4, "getCursor");
    }
    else
    {
      setIsNull(4);

      localResultSet = this.resultSet.getCursor(paramInt + 1);
    }

    return localResultSet;
  }

  public synchronized ROWID getROWID(int paramInt)
    throws SQLException
  {
    ROWID localROWID = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof ROWID))) {
        DatabaseError.throwSqlException(4, "getROWID");
      }

      localROWID = (ROWID)localDatum;
    }
    else
    {
      setIsNull(4);

      localROWID = this.resultSet.getROWID(paramInt + 1);
    }

    return localROWID;
  }

  public synchronized NUMBER getNUMBER(int paramInt)
    throws SQLException
  {
    NUMBER localNUMBER = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof NUMBER))) {
        DatabaseError.throwSqlException(4, "getNUMBER");
      }

      localNUMBER = (NUMBER)localDatum;
    }
    else
    {
      setIsNull(4);

      localNUMBER = this.resultSet.getNUMBER(paramInt + 1);
    }

    return localNUMBER;
  }

  public synchronized DATE getDATE(int paramInt)
    throws SQLException
  {
    DATE localDATE = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof DATE))) {
        DatabaseError.throwSqlException(4, "getDATE");
      }

      localDATE = (DATE)localDatum;
    }
    else
    {
      setIsNull(4);

      localDATE = this.resultSet.getDATE(paramInt + 1);
    }

    return localDATE;
  }

  public synchronized TIMESTAMP getTIMESTAMP(int paramInt)
    throws SQLException
  {
    TIMESTAMP localTIMESTAMP = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof TIMESTAMP))) {
        DatabaseError.throwSqlException(4, "getTIMESTAMP");
      }

      localTIMESTAMP = (TIMESTAMP)localDatum;
    }
    else
    {
      setIsNull(4);

      localTIMESTAMP = this.resultSet.getTIMESTAMP(paramInt + 1);
    }

    return localTIMESTAMP;
  }

  public synchronized TIMESTAMPTZ getTIMESTAMPTZ(int paramInt)
    throws SQLException
  {
    TIMESTAMPTZ localTIMESTAMPTZ = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof TIMESTAMPTZ))) {
        DatabaseError.throwSqlException(4, "getTIMESTAMPTZ");
      }

      localTIMESTAMPTZ = (TIMESTAMPTZ)localDatum;
    }
    else
    {
      setIsNull(4);

      localTIMESTAMPTZ = this.resultSet.getTIMESTAMPTZ(paramInt + 1);
    }

    return localTIMESTAMPTZ;
  }

  public synchronized TIMESTAMPLTZ getTIMESTAMPLTZ(int paramInt)
    throws SQLException
  {
    TIMESTAMPLTZ localTIMESTAMPLTZ = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof TIMESTAMPLTZ))) {
        DatabaseError.throwSqlException(4, "getTIMESTAMPLTZ");
      }

      localTIMESTAMPLTZ = (TIMESTAMPLTZ)localDatum;
    }
    else
    {
      setIsNull(4);

      localTIMESTAMPLTZ = this.resultSet.getTIMESTAMPLTZ(paramInt + 1);
    }

    return localTIMESTAMPLTZ;
  }

  public synchronized INTERVALDS getINTERVALDS(int paramInt)
    throws SQLException
  {
    INTERVALDS localINTERVALDS = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof INTERVALDS))) {
        DatabaseError.throwSqlException(4, "getINTERVALDS");
      }

      localINTERVALDS = (INTERVALDS)localDatum;
    }
    else
    {
      setIsNull(4);

      localINTERVALDS = this.resultSet.getINTERVALDS(paramInt + 1);
    }

    return localINTERVALDS;
  }

  public synchronized INTERVALYM getINTERVALYM(int paramInt)
    throws SQLException
  {
    INTERVALYM localINTERVALYM = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof INTERVALYM))) {
        DatabaseError.throwSqlException(4, "getINTERVALYM");
      }

      localINTERVALYM = (INTERVALYM)localDatum;
    }
    else
    {
      setIsNull(4);

      localINTERVALYM = this.resultSet.getINTERVALYM(paramInt + 1);
    }

    return localINTERVALYM;
  }

  public synchronized ARRAY getARRAY(int paramInt)
    throws SQLException
  {
    ARRAY localARRAY = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof ARRAY))) {
        DatabaseError.throwSqlException(4, "getARRAY");
      }

      localARRAY = (ARRAY)localDatum;
    }
    else
    {
      setIsNull(4);

      localARRAY = this.resultSet.getARRAY(paramInt + 1);
    }

    return localARRAY;
  }

  public synchronized STRUCT getSTRUCT(int paramInt)
    throws SQLException
  {
    STRUCT localSTRUCT = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof STRUCT))) {
        DatabaseError.throwSqlException(4, "getSTRUCT");
      }

      localSTRUCT = (STRUCT)localDatum;
    }
    else
    {
      setIsNull(4);

      localSTRUCT = this.resultSet.getSTRUCT(paramInt + 1);
    }

    return localSTRUCT;
  }

  public synchronized OPAQUE getOPAQUE(int paramInt)
    throws SQLException
  {
    OPAQUE localOPAQUE = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof OPAQUE))) {
        DatabaseError.throwSqlException(4, "getOPAQUE");
      }

      localOPAQUE = (OPAQUE)localDatum;
    }
    else
    {
      setIsNull(4);

      localOPAQUE = this.resultSet.getOPAQUE(paramInt + 1);
    }

    return localOPAQUE;
  }

  public synchronized REF getREF(int paramInt)
    throws SQLException
  {
    REF localREF = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof REF))) {
        DatabaseError.throwSqlException(4, "getREF");
      }

      localREF = (REF)localDatum;
    }
    else
    {
      setIsNull(4);

      localREF = this.resultSet.getREF(paramInt + 1);
    }

    return localREF;
  }

  public synchronized CHAR getCHAR(int paramInt)
    throws SQLException
  {
    CHAR localCHAR = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof CHAR))) {
        DatabaseError.throwSqlException(4, "getCHAR");
      }

      localCHAR = (CHAR)localDatum;
    }
    else
    {
      setIsNull(4);

      localCHAR = this.resultSet.getCHAR(paramInt + 1);
    }

    return localCHAR;
  }

  public synchronized RAW getRAW(int paramInt)
    throws SQLException
  {
    RAW localRAW = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof RAW))) {
        DatabaseError.throwSqlException(4, "getRAW");
      }

      localRAW = (RAW)localDatum;
    }
    else
    {
      setIsNull(4);

      localRAW = this.resultSet.getRAW(paramInt + 1);
    }

    return localRAW;
  }

  public synchronized BLOB getBLOB(int paramInt)
    throws SQLException
  {
    BLOB localBLOB = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof BLOB))) {
        DatabaseError.throwSqlException(4, "getBLOB");
      }

      localBLOB = (BLOB)localDatum;
    }
    else
    {
      setIsNull(4);

      localBLOB = this.resultSet.getBLOB(paramInt + 1);
    }

    return localBLOB;
  }

  public synchronized CLOB getCLOB(int paramInt)
    throws SQLException
  {
    CLOB localCLOB = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof CLOB))) {
        DatabaseError.throwSqlException(4, "getCLOB");
      }

      localCLOB = (CLOB)localDatum;
    }
    else
    {
      setIsNull(4);

      localCLOB = this.resultSet.getCLOB(paramInt + 1);
    }

    return localCLOB;
  }

  public synchronized BFILE getBFILE(int paramInt)
    throws SQLException
  {
    BFILE localBFILE = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      if ((localDatum != null) && (!(localDatum instanceof BFILE))) {
        DatabaseError.throwSqlException(4, "getBFILE");
      }

      localBFILE = (BFILE)localDatum;
    }
    else
    {
      setIsNull(4);

      localBFILE = this.resultSet.getBFILE(paramInt + 1);
    }

    return localBFILE;
  }

  public synchronized BFILE getBfile(int paramInt)
    throws SQLException
  {
    return getBFILE(paramInt);
  }

  public synchronized CustomDatum getCustomDatum(int paramInt, CustomDatumFactory paramCustomDatumFactory)
    throws SQLException
  {
    if (paramCustomDatumFactory == null) {
      DatabaseError.throwSqlException(68);
    }
    CustomDatum localCustomDatum = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      localCustomDatum = paramCustomDatumFactory.create(localDatum, 0);
    }
    else
    {
      setIsNull(4);

      localCustomDatum = this.resultSet.getCustomDatum(paramInt + 1, paramCustomDatumFactory);
    }

    return localCustomDatum;
  }

  public synchronized ORAData getORAData(int paramInt, ORADataFactory paramORADataFactory)
    throws SQLException
  {
    if (paramORADataFactory == null) {
      DatabaseError.throwSqlException(68);
    }
    ORAData localORAData = null;

    setIsNull(3);

    if ((isOnInsertRow()) || ((isUpdatingRow()) && (isRowBufferUpdatedAt(paramInt))))
    {
      Datum localDatum = getRowBufferDatumAt(paramInt);

      setIsNull(localDatum == null);

      localORAData = paramORADataFactory.create(localDatum, 0);
    }
    else
    {
      setIsNull(4);

      localORAData = this.resultSet.getORAData(paramInt + 1, paramORADataFactory);
    }

    return localORAData;
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    if (((OracleStatement)this.scrollStmt).closed) {
      DatabaseError.throwSqlException(9, "getMetaData");
    }

    synchronized (this.connection)
    {
      synchronized (this)
      {
      }

      monitorexit; throw localObject1;
    }
  }

  public synchronized int findColumn(String paramString) throws SQLException
  {
    return this.resultSet.findColumn(paramString) - 1;
  }

  public synchronized void setFetchDirection(int paramInt)
    throws SQLException
  {
    this.resultSet.setFetchDirection(paramInt);
  }

  public synchronized int getFetchDirection() throws SQLException
  {
    return this.resultSet.getFetchDirection();
  }

  public synchronized void setFetchSize(int paramInt) throws SQLException
  {
    this.resultSet.setFetchSize(paramInt);
  }

  public synchronized int getFetchSize() throws SQLException
  {
    return this.resultSet.getFetchSize();
  }

  public int getType() throws SQLException
  {
    return this.rsetType;
  }

  public int getConcurrency() throws SQLException
  {
    return 1008;
  }

  public boolean rowUpdated()
    throws SQLException
  {
    return false;
  }

  public boolean rowInserted() throws SQLException
  {
    return false;
  }

  public boolean rowDeleted() throws SQLException
  {
    return false;
  }

  public synchronized void insertRow() throws SQLException
  {
    if (!isOnInsertRow()) {
      DatabaseError.throwSqlException(83);
    }
    prepareInsertRowStatement();
    prepareInsertRowBinds();
    executeInsertRow();
  }

  public synchronized void updateRow()
    throws SQLException
  {
    if (isOnInsertRow()) {
      DatabaseError.throwSqlException(84);
    }
    int i = getNumColumnsChanged();

    if (i > 0)
    {
      prepareUpdateRowStatement(i);
      prepareUpdateRowBinds(i);
      executeUpdateRow();
    }
  }

  public synchronized void deleteRow()
    throws SQLException
  {
    if (isOnInsertRow()) {
      DatabaseError.throwSqlException(84);
    }
    prepareDeleteRowStatement();
    prepareDeleteRowBinds();
    executeDeleteRow();
  }

  public synchronized void refreshRow()
    throws SQLException
  {
    if (isOnInsertRow()) {
      DatabaseError.throwSqlException(84);
    }
    this.resultSet.refreshRow();
  }

  public synchronized void cancelRowUpdates() throws SQLException
  {
    if (this.isUpdating)
    {
      this.isUpdating = false;

      clearRowBuffer();
    }
  }

  public synchronized void moveToInsertRow() throws SQLException
  {
    if (isOnInsertRow()) {
      return;
    }
    this.isInserting = true;

    if (this.rowBuffer == null) {
      this.rowBuffer = new Object[getColumnCount()];
    }
    if (this.m_nullIndicator == null) {
      this.m_nullIndicator = new boolean[getColumnCount()];
    }
    clearRowBuffer();
  }

  public synchronized void moveToCurrentRow() throws SQLException
  {
    cancelRowInserts();
  }

  public synchronized void updateString(int paramInt, String paramString)
    throws SQLException
  {
    updateObject(paramInt, paramString);
  }

  public synchronized void updateNull(int paramInt) throws SQLException
  {
    setRowBufferAt(paramInt, null);
  }

  public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException
  {
    updateObject(paramInt, new Boolean(paramBoolean));
  }

  public void updateByte(int paramInt, byte paramByte) throws SQLException
  {
    updateObject(paramInt, new Integer(paramByte));
  }

  public void updateShort(int paramInt, short paramShort) throws SQLException
  {
    updateObject(paramInt, new Integer(paramShort));
  }

  public void updateInt(int paramInt1, int paramInt2) throws SQLException
  {
    updateObject(paramInt1, new Integer(paramInt2));
  }

  public void updateLong(int paramInt, long paramLong) throws SQLException
  {
    updateObject(paramInt, new Long(paramLong));
  }

  public void updateFloat(int paramInt, float paramFloat) throws SQLException
  {
    updateObject(paramInt, new Float(paramFloat));
  }

  public void updateDouble(int paramInt, double paramDouble) throws SQLException
  {
    updateObject(paramInt, new Double(paramDouble));
  }

  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    updateObject(paramInt, paramBigDecimal);
  }

  public void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException
  {
    updateObject(paramInt, paramArrayOfByte);
  }

  public void updateDate(int paramInt, Date paramDate) throws SQLException
  {
    updateObject(paramInt, paramDate);
  }

  public void updateTime(int paramInt, Time paramTime) throws SQLException
  {
    updateObject(paramInt, paramTime);
  }

  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    updateObject(paramInt, paramTimestamp);
  }

  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    int i = getInternalMetadata().getColumnType(1 + paramInt1);

    if ((paramInputStream != null) && (paramInt2 > 0))
    {
      Object localObject;
      if (!isStreamType(i))
      {
        localObject = new byte[paramInt2];
        try
        {
          int j = paramInputStream.read(localObject);

          paramInputStream.close();

          String str = new String(localObject, 0, j);

          updateString(paramInt1, str);
        }
        catch (IOException localIOException)
        {
          DatabaseError.throwSqlException(localIOException);
        }
      }
      else
      {
        localObject = new int[] { paramInt2, 1 };

        setRowBufferAt(paramInt1, paramInputStream, localObject);
      }
    }
    else
    {
      setRowBufferAt(paramInt1, null);
    }
  }

  final boolean isStreamType(int paramInt)
  {
    return (paramInt == 2004) || (paramInt == 2005) || (paramInt == -4) || (paramInt == -1);
  }

  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    int i = getInternalMetadata().getColumnType(1 + paramInt1);

    if ((paramInputStream != null) && (paramInt2 > 0))
    {
      Object localObject;
      if (!isStreamType(i))
      {
        localObject = new byte[paramInt2];
        try
        {
          int j = paramInputStream.read(localObject);

          paramInputStream.close();
          updateBytes(paramInt1, localObject);
        }
        catch (IOException localIOException)
        {
          DatabaseError.throwSqlException(localIOException);
        }
      }
      else
      {
        localObject = new int[] { paramInt2, 2 };

        setRowBufferAt(paramInt1, paramInputStream, localObject);
      }
    }
    else
    {
      setRowBufferAt(paramInt1, null);
    }
  }

  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    int i = getInternalMetadata().getColumnType(1 + paramInt1);

    if ((paramReader != null) && (paramInt2 > 0))
    {
      Object localObject;
      if (!isStreamType(i))
      {
        localObject = new char[paramInt2];
        try
        {
          int j = paramReader.read(localObject);

          paramReader.close();
          updateString(paramInt1, new String(localObject));
        }
        catch (IOException localIOException)
        {
          DatabaseError.throwSqlException(localIOException);
        }
      }
      else
      {
        localObject = new int[] { paramInt2 };

        setRowBufferAt(paramInt1, paramReader, localObject);
      }
    }
    else
    {
      setRowBufferAt(paramInt1, null);
    }
  }

  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    updateObject(paramInt1, paramObject);
  }

  public synchronized void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    Datum localDatum = null;

    if (paramObject != null)
    {
      if ((paramObject instanceof Datum)) {
        localDatum = (Datum)paramObject;
      }
      else {
        OracleResultSetMetaData localOracleResultSetMetaData = (OracleResultSetMetaData)getInternalMetadata();
        int i = paramInt + 1;
        localDatum = SQLUtil.makeOracleDatum(this.connection, paramObject, localOracleResultSetMetaData.getColumnType(i), null, localOracleResultSetMetaData.isNCHAR(i));
      }

    }

    setRowBufferAt(paramInt, localDatum);
  }

  public synchronized void updateOracleObject(int paramInt, Datum paramDatum)
    throws SQLException
  {
    setRowBufferAt(paramInt, paramDatum);
  }

  public void updateROWID(int paramInt, ROWID paramROWID) throws SQLException
  {
    updateOracleObject(paramInt, paramROWID);
  }

  public void updateNUMBER(int paramInt, NUMBER paramNUMBER) throws SQLException
  {
    updateOracleObject(paramInt, paramNUMBER);
  }

  public void updateDATE(int paramInt, DATE paramDATE) throws SQLException
  {
    updateOracleObject(paramInt, paramDATE);
  }

  public void updateINTERVALYM(int paramInt, INTERVALYM paramINTERVALYM)
    throws SQLException
  {
    updateOracleObject(paramInt, paramINTERVALYM);
  }

  public void updateINTERVALDS(int paramInt, INTERVALDS paramINTERVALDS)
    throws SQLException
  {
    updateOracleObject(paramInt, paramINTERVALDS);
  }

  public void updateTIMESTAMP(int paramInt, TIMESTAMP paramTIMESTAMP) throws SQLException
  {
    updateOracleObject(paramInt, paramTIMESTAMP);
  }

  public void updateTIMESTAMPTZ(int paramInt, TIMESTAMPTZ paramTIMESTAMPTZ)
    throws SQLException
  {
    updateOracleObject(paramInt, paramTIMESTAMPTZ);
  }

  public void updateTIMESTAMPLTZ(int paramInt, TIMESTAMPLTZ paramTIMESTAMPLTZ)
    throws SQLException
  {
    updateOracleObject(paramInt, paramTIMESTAMPLTZ);
  }

  public void updateARRAY(int paramInt, ARRAY paramARRAY) throws SQLException
  {
    updateOracleObject(paramInt, paramARRAY);
  }

  public void updateSTRUCT(int paramInt, STRUCT paramSTRUCT) throws SQLException
  {
    updateOracleObject(paramInt, paramSTRUCT);
  }

  public void updateOPAQUE(int paramInt, OPAQUE paramOPAQUE) throws SQLException
  {
    updateOracleObject(paramInt, paramOPAQUE);
  }

  public void updateREF(int paramInt, REF paramREF) throws SQLException
  {
    updateOracleObject(paramInt, paramREF);
  }

  public void updateCHAR(int paramInt, CHAR paramCHAR) throws SQLException
  {
    updateOracleObject(paramInt, paramCHAR);
  }

  public void updateRAW(int paramInt, RAW paramRAW) throws SQLException
  {
    updateOracleObject(paramInt, paramRAW);
  }

  public void updateBLOB(int paramInt, BLOB paramBLOB) throws SQLException
  {
    updateOracleObject(paramInt, paramBLOB);
  }

  public void updateCLOB(int paramInt, CLOB paramCLOB) throws SQLException
  {
    updateOracleObject(paramInt, paramCLOB);
  }

  public void updateBFILE(int paramInt, BFILE paramBFILE) throws SQLException
  {
    updateOracleObject(paramInt, paramBFILE);
  }

  public void updateBfile(int paramInt, BFILE paramBFILE) throws SQLException
  {
    updateOracleObject(paramInt, paramBFILE);
  }

  public void updateCustomDatum(int paramInt, CustomDatum paramCustomDatum)
    throws SQLException
  {
    throw new Error("wanna do datum = ((CustomDatum) x).toDatum(m_comm)");
  }

  public void updateORAData(int paramInt, ORAData paramORAData)
    throws SQLException
  {
    Datum localDatum = paramORAData.toDatum(this.connection);

    updateOracleObject(paramInt, localDatum);
  }

  public void updateRef(int paramInt, Ref paramRef) throws SQLException
  {
    updateREF(paramInt, (REF)paramRef);
  }

  public void updateBlob(int paramInt, Blob paramBlob) throws SQLException
  {
    updateBLOB(paramInt, (BLOB)paramBlob);
  }

  public void updateClob(int paramInt, Clob paramClob) throws SQLException
  {
    updateCLOB(paramInt, (CLOB)paramClob);
  }

  public void updateArray(int paramInt, Array paramArray) throws SQLException
  {
    updateARRAY(paramInt, (ARRAY)paramArray);
  }

  int getColumnCount()
    throws SQLException
  {
    if (this.columnCount == 0)
    {
      if ((this.resultSet instanceof OracleResultSetImpl))
      {
        if (((OracleResultSetImpl)this.resultSet).statement.accessors != null) {
          this.columnCount = ((OracleResultSetImpl)this.resultSet).statement.numberOfDefinePositions;
        }
        else
          this.columnCount = getInternalMetadata().getColumnCount();
      }
      else {
        this.columnCount = ((ScrollableResultSet)this.resultSet).getColumnCount();
      }
    }
    return this.columnCount;
  }

  ResultSetMetaData getInternalMetadata()
    throws SQLException
  {
    if (this.rsetMetaData == null) {
      this.rsetMetaData = this.resultSet.getMetaData();
    }
    return this.rsetMetaData;
  }

  private void cancelRowChanges() throws SQLException
  {
    if (this.isInserting) {
      cancelRowInserts();
    }
    if (this.isUpdating)
      cancelRowUpdates();
  }

  boolean isOnInsertRow()
  {
    return this.isInserting;
  }

  private void cancelRowInserts()
  {
    if (this.isInserting)
    {
      this.isInserting = false;

      clearRowBuffer();
    }
  }

  boolean isUpdatingRow()
  {
    return this.isUpdating;
  }

  private void clearRowBuffer()
  {
    int i;
    if (this.rowBuffer != null)
    {
      for (i = 0; i < this.rowBuffer.length; i++) {
        this.rowBuffer[i] = null;
      }
    }
    if (this.m_nullIndicator != null)
    {
      for (i = 0; i < this.m_nullIndicator.length; i++) {
        this.m_nullIndicator[i] = false;
      }
    }
    if (this.typeInfo != null)
    {
      for (i = 0; i < this.typeInfo.length; i++)
        if (this.typeInfo[i] != null)
          for (int j = 0; j < this.typeInfo[i].length; j++)
            this.typeInfo[i][j] = 0;
    }
  }

  private void setRowBufferAt(int paramInt, Datum paramDatum)
    throws SQLException
  {
    setRowBufferAt(paramInt, paramDatum, (int[])null);
  }

  private void setRowBufferAt(int paramInt, Object paramObject, int[] paramArrayOfInt)
    throws SQLException
  {
    if (!this.isInserting)
    {
      if ((isBeforeFirst()) || (isAfterLast()) || (getRow() == 0))
        DatabaseError.throwSqlException(82);
      else {
        this.isUpdating = true;
      }
    }
    if ((paramInt < 1) || (paramInt > getColumnCount() - 1)) {
      DatabaseError.throwSqlException(68, "setRowBufferAt");
    }

    if (this.rowBuffer == null) {
      this.rowBuffer = new Object[getColumnCount()];
    }
    if (this.m_nullIndicator == null)
    {
      this.m_nullIndicator = new boolean[getColumnCount()];

      for (int i = 0; i < getColumnCount(); i++) {
        this.m_nullIndicator[i] = false;
      }
    }
    if (paramArrayOfInt != null)
    {
      if (this.typeInfo == null)
      {
        this.typeInfo = new int[getColumnCount()][];
      }

      this.typeInfo[paramInt] = paramArrayOfInt;
    }

    this.rowBuffer[paramInt] = paramObject;
    this.m_nullIndicator[paramInt] = (paramObject == null ? 1 : false);
  }

  private Datum getRowBufferDatumAt(int paramInt)
    throws SQLException
  {
    if ((paramInt < 1) || (paramInt > getColumnCount() - 1)) {
      DatabaseError.throwSqlException(68, "getRowBufferDatumAt");
    }

    Datum localDatum = null;

    if (this.rowBuffer != null)
    {
      Object localObject = this.rowBuffer[paramInt];

      if (localObject != null)
      {
        if ((localObject instanceof Datum))
        {
          localDatum = (Datum)localObject;
        }
        else
        {
          OracleResultSetMetaData localOracleResultSetMetaData = (OracleResultSetMetaData)getInternalMetadata();
          int i = paramInt + 1;
          localDatum = SQLUtil.makeOracleDatum(this.connection, localObject, localOracleResultSetMetaData.getColumnType(i), null, localOracleResultSetMetaData.isNCHAR(i));

          this.rowBuffer[paramInt] = localDatum;
        }
      }
    }

    return localDatum;
  }

  private Object getRowBufferAt(int paramInt)
    throws SQLException
  {
    if ((paramInt < 1) || (paramInt > getColumnCount() - 1)) {
      DatabaseError.throwSqlException(68, "getRowBufferDatumAt");
    }

    if (this.rowBuffer != null)
    {
      return this.rowBuffer[paramInt];
    }

    return null;
  }

  private boolean isRowBufferUpdatedAt(int paramInt)
  {
    if (this.rowBuffer == null) {
      return false;
    }
    return (this.rowBuffer[paramInt] != null) || (this.m_nullIndicator[paramInt] != 0);
  }

  private void prepareInsertRowStatement()
    throws SQLException
  {
    if (this.insertStmt == null)
    {
      this.insertStmt = ((OraclePreparedStatement)this.connection.prepareStatement(((OracleStatement)this.scrollStmt).sqlObject.getInsertSqlForUpdatableResultSet(this)));

      this.insertStmt.setQueryTimeout(((Statement)this.scrollStmt).getQueryTimeout());
    }
  }

  private void prepareInsertRowBinds()
    throws SQLException
  {
    int i = 1;

    i = prepareSubqueryBinds(this.insertStmt, i);

    OracleResultSetMetaData localOracleResultSetMetaData = (OracleResultSetMetaData)getInternalMetadata();

    for (int j = 1; j < getColumnCount(); j++)
    {
      Object localObject = getRowBufferAt(j);

      if (localObject != null)
      {
        if ((localObject instanceof Reader))
        {
          this.insertStmt.setCharacterStream(i + j - 1, (Reader)localObject, this.typeInfo[j][0]);
        }
        else if ((localObject instanceof InputStream))
        {
          if (this.typeInfo[j][1] == 2) {
            this.insertStmt.setBinaryStream(i + j - 1, (InputStream)localObject, this.typeInfo[j][0]);
          }
          else if (this.typeInfo[j][1] == 1) {
            this.insertStmt.setAsciiStream(i + j - 1, (InputStream)localObject, this.typeInfo[j][0]);
          }

        }
        else
        {
          Datum localDatum = getRowBufferDatumAt(j);

          if (localOracleResultSetMetaData.isNCHAR(j + 1))
            this.insertStmt.setFormOfUse(i + j - 1, 2);
          this.insertStmt.setOracleObject(i + j - 1, localDatum);
        }

      }
      else
      {
        int k = getInternalMetadata().getColumnType(j + 1);

        if ((k == 2006) || (k == 2002) || (k == 2008) || (k == 2007) || (k == 2003))
        {
          this.insertStmt.setNull(i + j - 1, k, getInternalMetadata().getColumnTypeName(j + 1));
        }
        else
        {
          this.insertStmt.setNull(i + j - 1, k);
        }
      }
    }
  }

  private void executeInsertRow()
    throws SQLException
  {
    if (this.insertStmt.executeUpdate() != 1)
      DatabaseError.throwSqlException(85);
  }

  private int getNumColumnsChanged()
    throws SQLException
  {
    int i = 0;

    if (this.indexColsChanged == null) {
      this.indexColsChanged = new int[getColumnCount()];
    }
    if (this.rowBuffer != null)
    {
      for (int j = 1; j < getColumnCount(); j++)
      {
        if ((this.rowBuffer[j] == null) && ((this.rowBuffer[j] != null) || (this.m_nullIndicator[j] == 0))) {
          continue;
        }
        this.indexColsChanged[(i++)] = j;
      }

    }

    return i;
  }

  private void prepareUpdateRowStatement(int paramInt)
    throws SQLException
  {
    if (this.updateStmt != null) {
      this.updateStmt.close();
    }

    this.updateStmt = ((OraclePreparedStatement)this.connection.prepareStatement(((OracleStatement)this.scrollStmt).sqlObject.getUpdateSqlForUpdatableResultSet(this, paramInt, this.rowBuffer, this.indexColsChanged)));

    this.updateStmt.setQueryTimeout(((Statement)this.scrollStmt).getQueryTimeout());
  }

  private void prepareUpdateRowBinds(int paramInt)
    throws SQLException
  {
    int i = 1;

    i = prepareSubqueryBinds(this.updateStmt, i);

    OracleResultSetMetaData localOracleResultSetMetaData = (OracleResultSetMetaData)getInternalMetadata();

    for (int j = 0; j < paramInt; j++)
    {
      int k = this.indexColsChanged[j];
      Object localObject = getRowBufferAt(k);

      if (localObject != null)
      {
        if ((localObject instanceof Reader))
        {
          this.updateStmt.setCharacterStream(i++, (Reader)localObject, this.typeInfo[k][0]);
        }
        else if ((localObject instanceof InputStream))
        {
          if (this.typeInfo[k][1] == 2)
          {
            this.updateStmt.setBinaryStream(i++, (InputStream)localObject, this.typeInfo[k][0]);
          }
          else if (this.typeInfo[k][1] == 1) {
            this.updateStmt.setAsciiStream(i++, (InputStream)localObject, this.typeInfo[k][0]);
          }
        }
        else
        {
          Datum localDatum = getRowBufferDatumAt(k);

          if (localOracleResultSetMetaData.isNCHAR(k + 1))
            this.updateStmt.setFormOfUse(i, 2);
          this.updateStmt.setOracleObject(i++, localDatum);
        }

      }
      else
      {
        int m = getInternalMetadata().getColumnType(k + 1);

        if ((m == 2006) || (m == 2002) || (m == 2008) || (m == 2007) || (m == 2003))
        {
          this.updateStmt.setNull(i++, m, getInternalMetadata().getColumnTypeName(k + 1));
        }
        else
        {
          this.updateStmt.setNull(i++, m);
        }
      }

    }

    prepareCompareSelfBinds(this.updateStmt, i);
  }

  private void executeUpdateRow()
    throws SQLException
  {
    try
    {
      if (this.updateStmt.executeUpdate() == 0) {
        DatabaseError.throwSqlException(85);
      }

      if (this.isCachedRset)
      {
        if (this.autoRefetch)
        {
          ((ScrollableResultSet)this.resultSet).refreshRowsInCache(getRow(), 1, 1000);

          cancelRowUpdates();
        }
        else
        {
          if (this.rowBuffer != null)
          {
            for (int i = 1; i < getColumnCount(); i++)
            {
              if ((this.rowBuffer[i] == null) && ((this.rowBuffer[i] != null) || (this.m_nullIndicator[i] == 0))) {
                continue;
              }
              ((ScrollableResultSet)this.resultSet).setCurrentRowValueAt(this, i + 1, this.rowBuffer[i]);
            }

          }

          cancelRowUpdates();
        }

      }

    }
    finally
    {
      if (this.updateStmt != null)
      {
        this.updateStmt.close();

        this.updateStmt = null;
      }
    }
  }

  private void prepareDeleteRowStatement()
    throws SQLException
  {
    if (this.deleteStmt == null)
    {
      StringBuffer localStringBuffer = new StringBuffer();

      this.deleteStmt = ((OraclePreparedStatement)this.connection.prepareStatement(((OracleStatement)this.scrollStmt).sqlObject.getDeleteSqlForUpdatableResultSet(this)));

      this.deleteStmt.setQueryTimeout(((Statement)this.scrollStmt).getQueryTimeout());
    }
  }

  private void prepareDeleteRowBinds()
    throws SQLException
  {
    int i = 1;

    i = prepareSubqueryBinds(this.deleteStmt, i);

    prepareCompareSelfBinds(this.deleteStmt, i);
  }

  private void executeDeleteRow()
    throws SQLException
  {
    if (this.deleteStmt.executeUpdate() == 0) {
      DatabaseError.throwSqlException(85);
    }

    if (this.isCachedRset)
      ((ScrollableResultSet)this.resultSet).removeRowInCache(getRow());
  }

  private int prepareCompareSelfBinds(OraclePreparedStatement paramOraclePreparedStatement, int paramInt)
    throws SQLException
  {
    Datum localDatum = this.resultSet.getOracleObject(1);

    paramOraclePreparedStatement.setOracleObject(paramInt, this.resultSet.getOracleObject(1));

    return paramInt + 1;
  }

  private int prepareSubqueryBinds(OraclePreparedStatement paramOraclePreparedStatement, int paramInt)
    throws SQLException
  {
    int i = this.scrollStmt.copyBinds(paramOraclePreparedStatement, paramInt - 1);

    return i + 1;
  }

  private void setIsNull(int paramInt)
  {
    this.wasNull = paramInt;
  }

  private void setIsNull(boolean paramBoolean)
  {
    if (paramBoolean)
      this.wasNull = 1;
    else
      this.wasNull = 2;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.UpdatableResultSet
 * JD-Core Version:    0.6.0
 */