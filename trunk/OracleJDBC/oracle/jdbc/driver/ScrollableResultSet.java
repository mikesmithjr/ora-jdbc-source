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
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;
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

class ScrollableResultSet extends BaseResultSet
{
  PhysicalConnection connection;
  OracleResultSetImpl resultSet;
  ScrollRsetStatement scrollStmt;
  ResultSetMetaData metadata;
  private int rsetType;
  private int rsetConcurency;
  private int beginColumnIndex;
  private int columnCount;
  private int wasNull;
  OracleResultSetCache rsetCache;
  int currentRow;
  private int numRowsCached;
  private boolean allRowsCached;
  private int lastRefetchSz;
  private Vector refetchRowids;
  private OraclePreparedStatement refetchStmt;
  private int usrFetchDirection;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  ScrollableResultSet(ScrollRsetStatement paramScrollRsetStatement, OracleResultSetImpl paramOracleResultSetImpl, int paramInt1, int paramInt2)
    throws SQLException
  {
    this.connection = ((OracleStatement)paramScrollRsetStatement).connection;
    this.resultSet = paramOracleResultSetImpl;
    this.metadata = null;
    this.scrollStmt = paramScrollRsetStatement;
    this.rsetType = paramInt1;
    this.rsetConcurency = paramInt2;
    this.autoRefetch = paramScrollRsetStatement.getAutoRefetch();
    this.beginColumnIndex = (needIdentifier(paramInt1, paramInt2) ? 1 : 0);
    this.columnCount = 0;
    this.wasNull = -1;
    this.rsetCache = paramScrollRsetStatement.getResultSetCache();

    if (this.rsetCache == null)
    {
      this.rsetCache = new OracleResultSetCacheImpl();
    }
    else
    {
      try
      {
        this.rsetCache.clear();
      }
      catch (IOException localIOException)
      {
        DatabaseError.throwSqlException(localIOException);
      }
    }

    this.currentRow = 0;
    this.numRowsCached = 0;
    this.allRowsCached = false;
    this.lastRefetchSz = 0;
    this.refetchRowids = null;
    this.refetchStmt = null;
    this.usrFetchDirection = 1000;
  }

  public synchronized void close()
    throws SQLException
  {
    if (this.resultSet != null) {
      this.resultSet.close();
    }
    if (this.refetchStmt != null) {
      this.refetchStmt.close();
    }
    if (this.scrollStmt != null) {
      this.scrollStmt.notifyCloseRset();
    }
    if (this.refetchRowids != null) {
      this.refetchRowids.removeAllElements();
    }
    this.resultSet = null;
    this.scrollStmt = null;
    this.refetchStmt = null;
    this.refetchRowids = null;
    this.metadata = null;
    this.connection = null;
    try
    {
      if (this.rsetCache != null)
      {
        this.rsetCache.clear();
        this.rsetCache.close();
      }
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    this.rsetCache = null;
  }

  public synchronized boolean wasNull()
    throws SQLException
  {
    if (this.wasNull == -1) {
      DatabaseError.throwSqlException(24);
    }
    return this.wasNull == 1;
  }

  public synchronized Statement getStatement()
    throws SQLException
  {
    return (Statement)this.scrollStmt;
  }

  synchronized void resetBeginColumnIndex()
  {
    this.beginColumnIndex = 0;
  }

  synchronized ResultSet getResultSet()
  {
    return this.resultSet;
  }

  synchronized int removeRowInCache(int paramInt)
    throws SQLException
  {
    if ((!isEmptyResultSet()) && (isValidRow(paramInt)))
    {
      removeCachedRowAt(paramInt);

      this.numRowsCached -= 1;

      if (paramInt >= this.currentRow) {
        this.currentRow -= 1;
      }
      return 1;
    }

    return 0;
  }

  synchronized int refreshRowsInCache(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    OracleResultSet localOracleResultSet = null;
    int i = 0;

    i = get_refetch_size(paramInt1, paramInt2, paramInt3);
    try
    {
      if (i > 0)
      {
        if (i != this.lastRefetchSz)
        {
          if (this.refetchStmt != null) {
            this.refetchStmt.close();
          }
          this.refetchStmt = prepare_refetch_statement(i);
          this.refetchStmt.setQueryTimeout(((OracleStatement)this.scrollStmt).getQueryTimeout());
          this.lastRefetchSz = i;
        }

        prepare_refetch_binds(this.refetchStmt, i);

        localOracleResultSet = (OracleResultSet)this.refetchStmt.executeQuery();

        save_refetch_results(localOracleResultSet, paramInt1, i, paramInt3);
      }

    }
    finally
    {
      if (localOracleResultSet != null) {
        localOracleResultSet.close();
      }
    }
    return i;
  }

  public synchronized boolean next()
    throws SQLException
  {
    if (isEmptyResultSet())
    {
      return false;
    }

    if (this.currentRow < 1)
    {
      this.currentRow = 1;
    }
    else
    {
      this.currentRow += 1;
    }

    return isValidRow(this.currentRow);
  }

  public synchronized boolean isBeforeFirst()
    throws SQLException
  {
    return (!isEmptyResultSet()) && (this.currentRow < 1);
  }

  public synchronized boolean isAfterLast()
    throws SQLException
  {
    return (!isEmptyResultSet()) && (this.currentRow > 0) && (!isValidRow(this.currentRow));
  }

  public synchronized boolean isFirst()
    throws SQLException
  {
    return this.currentRow == 1;
  }

  public synchronized boolean isLast()
    throws SQLException
  {
    return (!isEmptyResultSet()) && (isValidRow(this.currentRow)) && (!isValidRow(this.currentRow + 1));
  }

  public synchronized void beforeFirst()
    throws SQLException
  {
    if (!isEmptyResultSet())
      this.currentRow = 0;
  }

  public synchronized void afterLast()
    throws SQLException
  {
    if (!isEmptyResultSet())
      this.currentRow = (getLastRow() + 1);
  }

  public synchronized boolean first()
    throws SQLException
  {
    if (isEmptyResultSet())
    {
      return false;
    }

    this.currentRow = 1;

    return isValidRow(this.currentRow);
  }

  public synchronized boolean last()
    throws SQLException
  {
    if (isEmptyResultSet())
    {
      return false;
    }

    this.currentRow = getLastRow();

    return isValidRow(this.currentRow);
  }

  public synchronized int getRow()
    throws SQLException
  {
    if (isValidRow(this.currentRow)) {
      return this.currentRow;
    }
    return 0;
  }

  public synchronized boolean absolute(int paramInt)
    throws SQLException
  {
    if (paramInt == 0) {
      DatabaseError.throwSqlException(68, "absolute (0)");
    }

    if (isEmptyResultSet())
    {
      return false;
    }
    if (paramInt > 0)
    {
      this.currentRow = paramInt;
    }
    else if (paramInt < 0)
    {
      this.currentRow = (getLastRow() + 1 + paramInt);
    }

    return isValidRow(this.currentRow);
  }

  public synchronized boolean relative(int paramInt)
    throws SQLException
  {
    if (isEmptyResultSet())
    {
      return false;
    }
    if (isValidRow(this.currentRow))
    {
      this.currentRow += paramInt;

      return isValidRow(this.currentRow);
    }

    DatabaseError.throwSqlException(82, "relative");

    return false;
  }

  public synchronized boolean previous()
    throws SQLException
  {
    if (isEmptyResultSet())
    {
      return false;
    }
    if (isAfterLast())
    {
      this.currentRow = getLastRow();
    }
    else
    {
      this.currentRow -= 1;
    }

    return isValidRow(this.currentRow);
  }

  public synchronized Datum getOracleObject(int paramInt)
    throws SQLException
  {
    this.wasNull = -1;

    if (!isValidRow(this.currentRow)) {
      DatabaseError.throwSqlException(11);
    }
    if ((paramInt < 1) || (paramInt > getColumnCount())) {
      DatabaseError.throwSqlException(3);
    }
    Datum localDatum = getCachedDatumValueAt(this.currentRow, paramInt + this.beginColumnIndex);

    this.wasNull = (localDatum == null ? 1 : 0);

    return localDatum;
  }

  public synchronized String getString(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null) {
      return localDatum.stringValue();
    }
    return null;
  }

  public synchronized boolean getBoolean(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.booleanValue();
    }

    return false;
  }

  public synchronized byte getByte(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null) {
      return localDatum.byteValue();
    }
    return 0;
  }

  public synchronized short getShort(int paramInt)
    throws SQLException
  {
    long l = getLong(paramInt);

    if ((l > 65537L) || (l < -65538L)) {
      DatabaseError.throwSqlException(26, "getShort");
    }

    return (short)(int)l;
  }

  public synchronized int getInt(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.intValue();
    }

    return 0;
  }

  public synchronized long getLong(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.longValue();
    }

    return 0L;
  }

  public synchronized float getFloat(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.floatValue();
    }

    return 0.0F;
  }

  public synchronized double getDouble(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.doubleValue();
    }

    return 0.0D;
  }

  public synchronized BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt1);

    if (localDatum != null)
    {
      return localDatum.bigDecimalValue();
    }

    return null;
  }

  public synchronized byte[] getBytes(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof RAW)) {
        return ((RAW)localDatum).shareBytes();
      }
      DatabaseError.throwSqlException(4, "getBytes");
    }

    return null;
  }

  public synchronized Date getDate(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.dateValue();
    }

    return null;
  }

  public synchronized Time getTime(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.timeValue();
    }

    return null;
  }

  public synchronized Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.timestampValue();
    }

    return null;
  }

  public synchronized InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.asciiStreamValue();
    }

    return null;
  }

  public synchronized InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      DBConversion localDBConversion = this.connection.conversion;
      byte[] arrayOfByte = localDatum.shareBytes();

      if ((localDatum instanceof RAW))
      {
        return localDBConversion.ConvertStream(new ByteArrayInputStream(arrayOfByte), 3);
      }

      if ((localDatum instanceof CHAR))
      {
        return localDBConversion.ConvertStream(new ByteArrayInputStream(arrayOfByte), 1);
      }

      DatabaseError.throwSqlException(4, "getUnicodeStream");
    }

    return null;
  }

  public synchronized InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.binaryStreamValue();
    }

    return null;
  }

  public synchronized Object getObject(int paramInt)
    throws SQLException
  {
    return getObject(paramInt, this.connection.getTypeMap());
  }

  public synchronized Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.characterStreamValue();
    }

    return null;
  }

  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    return getBigDecimal(paramInt, 0);
  }

  public synchronized Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof STRUCT)) {
        return ((STRUCT)localDatum).toJdbc(paramMap);
      }
      return localDatum.toJdbc();
    }

    return null;
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
    DATE localDATE = getDATE(paramInt);

    return localDATE != null ? localDATE.dateValue(paramCalendar) : null;
  }

  public synchronized Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    DATE localDATE = getDATE(paramInt);

    return localDATE != null ? localDATE.timeValue(paramCalendar) : null;
  }

  public synchronized Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    DATE localDATE = getDATE(paramInt);

    return localDATE != null ? localDATE.timestampValue(paramCalendar) : null;
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
    DatabaseError.throwSqlException(4, "getCursor");

    return null;
  }

  public synchronized ROWID getROWID(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof ROWID)) {
        return (ROWID)localDatum;
      }
      DatabaseError.throwSqlException(4, "getROWID");
    }

    return null;
  }

  public synchronized NUMBER getNUMBER(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof NUMBER)) {
        return (NUMBER)localDatum;
      }
      DatabaseError.throwSqlException(4, "getNUMBER");
    }

    return null;
  }

  public synchronized DATE getDATE(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof DATE)) {
        return (DATE)localDatum;
      }
      DatabaseError.throwSqlException(4, "getDATE");
    }

    return null;
  }

  public synchronized TIMESTAMP getTIMESTAMP(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof TIMESTAMP)) {
        return (TIMESTAMP)localDatum;
      }
      DatabaseError.throwSqlException(4, "getTIMESTAMP");
    }

    return null;
  }

  public synchronized TIMESTAMPTZ getTIMESTAMPTZ(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof TIMESTAMPTZ)) {
        return (TIMESTAMPTZ)localDatum;
      }
      DatabaseError.throwSqlException(4, "getTIMESTAMPTZ");
    }

    return null;
  }

  public synchronized TIMESTAMPLTZ getTIMESTAMPLTZ(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof TIMESTAMPLTZ)) {
        return (TIMESTAMPLTZ)localDatum;
      }
      DatabaseError.throwSqlException(4, "getTIMESTAMPLTZ");
    }

    return null;
  }

  public synchronized INTERVALDS getINTERVALDS(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof INTERVALDS)) {
        return (INTERVALDS)localDatum;
      }
      DatabaseError.throwSqlException(4, "getINTERVALDS");
    }

    return null;
  }

  public synchronized INTERVALYM getINTERVALYM(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof INTERVALYM)) {
        return (INTERVALYM)localDatum;
      }
      DatabaseError.throwSqlException(4, "getINTERVALYM");
    }

    return null;
  }

  public synchronized ARRAY getARRAY(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof ARRAY)) {
        return (ARRAY)localDatum;
      }
      DatabaseError.throwSqlException(4, "getARRAY");
    }

    return null;
  }

  public synchronized STRUCT getSTRUCT(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof STRUCT)) {
        return (STRUCT)localDatum;
      }
      DatabaseError.throwSqlException(4, "getSTRUCT");
    }

    return null;
  }

  public synchronized OPAQUE getOPAQUE(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof OPAQUE)) {
        return (OPAQUE)localDatum;
      }
      DatabaseError.throwSqlException(4, "getOPAQUE");
    }

    return null;
  }

  public synchronized REF getREF(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof REF)) {
        return (REF)localDatum;
      }
      DatabaseError.throwSqlException(4, "getREF");
    }

    return null;
  }

  public synchronized CHAR getCHAR(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof CHAR)) {
        return (CHAR)localDatum;
      }
      DatabaseError.throwSqlException(4, "getCHAR");
    }

    return null;
  }

  public synchronized RAW getRAW(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof RAW)) {
        return (RAW)localDatum;
      }
      DatabaseError.throwSqlException(4, "getRAW");
    }

    return null;
  }

  public synchronized BLOB getBLOB(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof BLOB)) {
        return (BLOB)localDatum;
      }
      DatabaseError.throwSqlException(4, "getBLOB");
    }

    return null;
  }

  public synchronized CLOB getCLOB(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof CLOB)) {
        return (CLOB)localDatum;
      }
      DatabaseError.throwSqlException(4, "getCLOB");
    }

    return null;
  }

  public synchronized BFILE getBFILE(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof BFILE)) {
        return (BFILE)localDatum;
      }
      DatabaseError.throwSqlException(4, "getBFILE");
    }

    return null;
  }

  public synchronized BFILE getBfile(int paramInt)
    throws SQLException
  {
    return getBFILE(paramInt);
  }

  public synchronized CustomDatum getCustomDatum(int paramInt, CustomDatumFactory paramCustomDatumFactory)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    return paramCustomDatumFactory.create(localDatum, 0);
  }

  public synchronized ORAData getORAData(int paramInt, ORADataFactory paramORADataFactory)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    return paramORADataFactory.create(localDatum, 0);
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    synchronized (this.connection)
    {
      synchronized (this)
      {
      }

      monitorexit; throw localObject1;
    }
  }

  public synchronized int findColumn(String paramString)
    throws SQLException
  {
    return this.resultSet.findColumn(paramString) - this.beginColumnIndex;
  }

  public synchronized void setFetchDirection(int paramInt)
    throws SQLException
  {
    if (paramInt == 1000)
    {
      this.usrFetchDirection = paramInt;
    }
    else if ((paramInt == 1001) || (paramInt == 1002))
    {
      this.usrFetchDirection = paramInt;

      this.sqlWarning = DatabaseError.addSqlWarning(this.sqlWarning, 87);
    }
    else
    {
      DatabaseError.throwSqlException(68, "setFetchDirection");
    }
  }

  public synchronized int getFetchDirection()
    throws SQLException
  {
    return 1000;
  }

  public synchronized void setFetchSize(int paramInt)
    throws SQLException
  {
    this.resultSet.setFetchSize(paramInt);
  }

  public synchronized int getFetchSize()
    throws SQLException
  {
    return this.resultSet.getFetchSize();
  }

  public int getType()
    throws SQLException
  {
    return this.rsetType;
  }

  public int getConcurrency()
    throws SQLException
  {
    return this.rsetConcurency;
  }

  public void refreshRow()
    throws SQLException
  {
    if (!needIdentifier(this.rsetType, this.rsetConcurency)) {
      DatabaseError.throwSqlException(23, "refreshRow");
    }

    if (isValidRow(this.currentRow))
    {
      int i = getFetchDirection();
      try
      {
        refreshRowsInCache(this.currentRow, getFetchSize(), i);
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.throwSqlException(localSQLException, 90, "Unsupported syntax for refreshRow()");
      }
    }
    else
    {
      DatabaseError.throwSqlException(82, "refreshRow");
    }
  }

  public void setCurrentRowValueAt(Object paramObject1, int paramInt, Object paramObject2)
    throws SQLException
  {
    if ((paramObject1 instanceof UpdatableResultSet))
      putCachedValueAt(this.currentRow, paramInt, paramObject2);
    else
      DatabaseError.throwSqlException(1);
  }

  private boolean isEmptyResultSet()
    throws SQLException
  {
    if (this.numRowsCached != 0)
    {
      return false;
    }
    if ((this.numRowsCached == 0) && (this.allRowsCached))
    {
      return true;
    }

    return !isValidRow(1);
  }

  boolean isValidRow(int paramInt)
    throws SQLException
  {
    if ((paramInt > 0) && (paramInt <= this.numRowsCached))
    {
      return true;
    }
    if (paramInt <= 0)
    {
      return false;
    }

    return cacheRowAt(paramInt);
  }

  private boolean cacheRowAt(int paramInt)
    throws SQLException
  {
    while ((this.numRowsCached < paramInt) && (this.resultSet.next()))
    {
      for (int i = 0; i < getColumnCount(); i++)
      {
        byte[] arrayOfByte = this.resultSet.privateGetBytes(i + 1);

        putCachedValueAt(this.numRowsCached + 1, i + 1, arrayOfByte);
      }

      this.numRowsCached += 1;
    }

    if (this.numRowsCached < paramInt)
    {
      this.allRowsCached = true;

      return false;
    }

    return true;
  }

  private int cacheAllRows()
    throws SQLException
  {
    while (this.resultSet.next())
    {
      for (int i = 0; i < getColumnCount(); i++) {
        putCachedValueAt(this.numRowsCached + 1, i + 1, this.resultSet.privateGetBytes(i + 1));
      }
      this.numRowsCached += 1;
    }

    this.allRowsCached = true;

    return this.numRowsCached;
  }

  int getColumnCount()
    throws SQLException
  {
    if (this.columnCount == 0)
    {
      int i = this.resultSet.statement.numberOfDefinePositions;

      if ((this.resultSet.statement.accessors != null) && (i > 0))
        this.columnCount = i;
      else {
        this.columnCount = getInternalMetadata().getColumnCount();
      }
    }
    return this.columnCount;
  }

  private ResultSetMetaData getInternalMetadata()
    throws SQLException
  {
    if (this.metadata == null) {
      this.metadata = this.resultSet.getMetaData();
    }
    return this.metadata;
  }

  private int getLastRow()
    throws SQLException
  {
    if (!this.allRowsCached) {
      cacheAllRows();
    }
    return this.numRowsCached;
  }

  private int get_refetch_size(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    int i = paramInt3 == 1001 ? -1 : 1;

    int j = 0;

    if (this.refetchRowids == null)
      this.refetchRowids = new Vector(10);
    else {
      this.refetchRowids.removeAllElements();
    }

    while ((j < paramInt2) && (isValidRow(paramInt1 + j * i)))
    {
      this.refetchRowids.addElement(getCachedDatumValueAt(paramInt1 + j * i, 1));

      j++;
    }

    return j;
  }

  private OraclePreparedStatement prepare_refetch_statement(int paramInt)
    throws SQLException
  {
    if (paramInt < 1) {
      DatabaseError.throwSqlException(68);
    }

    return (OraclePreparedStatement)this.connection.prepareStatement(((OracleStatement)this.scrollStmt).sqlObject.getRefetchSqlForScrollableResultSet(this, paramInt));
  }

  private void prepare_refetch_binds(OraclePreparedStatement paramOraclePreparedStatement, int paramInt)
    throws SQLException
  {
    int i = this.scrollStmt.copyBinds(paramOraclePreparedStatement, 0);

    for (int j = 0; j < paramInt; j++)
    {
      paramOraclePreparedStatement.setROWID(i + j + 1, (ROWID)this.refetchRowids.elementAt(j));
    }
  }

  private void save_refetch_results(OracleResultSet paramOracleResultSet, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    int i = paramInt3 == 1001 ? -1 : 1;

    while (paramOracleResultSet.next())
    {
      ROWID localROWID = paramOracleResultSet.getROWID(1);

      int j = 0;
      int k = paramInt1;

      while ((j == 0) && (k < paramInt1 + paramInt2 * i))
      {
        if (((ROWID)getCachedDatumValueAt(k, 1)).stringValue().equals(localROWID.stringValue()))
        {
          j = 1; continue;
        }
        k += i;
      }

      if (j == 0)
        continue;
      for (int m = 0; m < getColumnCount(); m++)
      {
        putCachedValueAt(k, m + 1, paramOracleResultSet.getOracleObject(m + 1));
      }
    }
  }

  private Object getCachedValueAt(int paramInt1, int paramInt2)
    throws SQLException
  {
    try
    {
      return this.rsetCache.get(paramInt1, paramInt2);
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }

    return null;
  }

  private Datum getCachedDatumValueAt(int paramInt1, int paramInt2)
    throws SQLException
  {
    Object localObject = null;
    try
    {
      localObject = this.rsetCache.get(paramInt1, paramInt2);
    }
    catch (IOException localIOException1)
    {
      DatabaseError.throwSqlException(localIOException1);
    }

    Datum localDatum = null;

    if (localObject != null)
    {
      if ((localObject instanceof Datum))
      {
        localDatum = (Datum)localObject;
      }
      else if (((byte[])localObject).length > 0)
      {
        int i = getInternalMetadata().getColumnType(paramInt2);
        int j = getInternalMetadata().getColumnDisplaySize(paramInt2);

        int k = this.scrollStmt.getMaxFieldSize();

        if ((k > 0) && (k < j)) {
          j = k;
        }
        String str = null;

        if ((i == 2006) || (i == 2002) || (i == 2008) || (i == 2007) || (i == 2003))
        {
          str = getInternalMetadata().getColumnTypeName(paramInt2);
        }
        int m = SQLUtil.getInternalType(i);

        short s = this.resultSet.statement.accessors[(paramInt2 - 1)].formOfUse;

        if ((s == 2) && ((m == 96) || (m == 1) || (m == 8) || (m == 112)))
        {
          localDatum = SQLUtil.makeNDatum(this.connection, (byte[])localObject, m, str, s, j);
        }
        else
        {
          localDatum = SQLUtil.makeDatum(this.connection, (byte[])localObject, m, str, j);
        }

        try
        {
          this.rsetCache.put(paramInt1, paramInt2, localDatum);
        }
        catch (IOException localIOException3)
        {
          DatabaseError.throwSqlException(localIOException3);
        }
      }
      else
      {
        try
        {
          this.rsetCache.put(paramInt1, paramInt2, null);
        }
        catch (IOException localIOException2)
        {
          DatabaseError.throwSqlException(localIOException2);
        }

      }

    }

    return localDatum;
  }

  private void putCachedValueAt(int paramInt1, int paramInt2, Object paramObject)
    throws SQLException
  {
    try
    {
      this.rsetCache.put(paramInt1, paramInt2, paramObject);
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  private void removeCachedRowAt(int paramInt)
    throws SQLException
  {
    try
    {
      this.rsetCache.remove(paramInt);
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public static boolean needIdentifier(int paramInt1, int paramInt2)
  {
    return ((paramInt1 != 1003) || (paramInt2 != 1007)) && ((paramInt1 != 1004) || (paramInt2 != 1007));
  }

  public static boolean needCache(int paramInt1, int paramInt2)
  {
    return (paramInt1 != 1003) && ((paramInt1 != 1004) || (paramInt2 != 1007));
  }

  public static boolean supportRefreshRow(int paramInt1, int paramInt2)
  {
    return (paramInt1 != 1003) && ((paramInt1 != 1004) || (paramInt2 != 1007));
  }

  int getFirstUserColumnIndex()
  {
    return this.beginColumnIndex;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ScrollableResultSet
 * JD-Core Version:    0.6.0
 */