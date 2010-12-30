package oracle.jdbc.driver;

import java.io.IOException;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

class OracleResultSetImpl extends BaseResultSet
{
  PhysicalConnection connection;
  OracleStatement statement;
  boolean closed;
  boolean explicitly_closed;
  boolean m_emptyRset;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  OracleResultSetImpl(PhysicalConnection paramPhysicalConnection, OracleStatement paramOracleStatement)
    throws SQLException
  {
    this.connection = paramPhysicalConnection;
    this.statement = paramOracleStatement;
    this.close_statement_on_close = false;
    this.closed = false;
    this.explicitly_closed = false;
    this.m_emptyRset = false;
  }

  public synchronized void close()
    throws SQLException
  {
    internal_close(false);

    if (this.close_statement_on_close)
    {
      try
      {
        this.statement.close();
      }
      catch (SQLException localSQLException)
      {
      }

    }

    this.explicitly_closed = true;
  }

  public synchronized boolean wasNull()
    throws SQLException
  {
    return this.statement.wasNullValue();
  }

  public synchronized ResultSetMetaData getMetaData()
    throws SQLException
  {
    if (this.explicitly_closed) {
      DatabaseError.throwSqlException(10, "getMetaData");
    }

    if (this.statement.closed) {
      DatabaseError.throwSqlException(9, "getMetaData");
    }

    if (!this.statement.isOpen) {
      DatabaseError.throwSqlException(144, "getMetaData");
    }

    return new OracleResultSetMetaData(this.connection, this.statement);
  }

  public synchronized Statement getStatement()
    throws SQLException
  {
    return this.statement;
  }

  public synchronized boolean next()
    throws SQLException
  {
    boolean bool = true;

    PhysicalConnection localPhysicalConnection = this.statement.connection;

    if (this.explicitly_closed) {
      DatabaseError.throwSqlException(10, "next");
    }

    if ((localPhysicalConnection == null) || (localPhysicalConnection.lifecycle != 1)) {
      DatabaseError.throwSqlException(8, "next");
    }

    if (this.statement.closed) {
      DatabaseError.throwSqlException(9, "next");
    }

    if ((this.statement.sqlKind == 1) || (this.statement.sqlKind == 4))
    {
      DatabaseError.throwSqlException(166, "next");
    }

    if (this.closed) {
      return false;
    }
    this.statement.currentRow += 1;
    this.statement.totalRowsVisited += 1;

    if ((this.statement.maxRows != 0) && (this.statement.totalRowsVisited > this.statement.maxRows))
    {
      internal_close(false);

      return false;
    }

    if (this.statement.currentRow >= this.statement.validRows) {
      bool = close_or_fetch_from_next(false);
    }
    if ((bool) && (localPhysicalConnection.useFetchSizeWithLongColumn)) {
      this.statement.reopenStreams();
    }
    return bool;
  }

  private boolean close_or_fetch_from_next(boolean paramBoolean)
    throws SQLException
  {
    if (paramBoolean)
    {
      internal_close(false);

      return false;
    }

    if (this.statement.gotLastBatch)
    {
      internal_close(false);

      return false;
    }

    this.statement.check_row_prefetch_changed();

    PhysicalConnection localPhysicalConnection = this.statement.connection;

    if (localPhysicalConnection.protocolId == 3) {
      this.sqlWarning = null;
    }
    else
    {
      if (this.statement.streamList != null)
      {
        while (this.statement.nextStream != null)
        {
          try
          {
            this.statement.nextStream.close();
          }
          catch (IOException localIOException)
          {
            DatabaseError.throwSqlException(localIOException);
          }

          this.statement.nextStream = this.statement.nextStream.nextStream;
        }
      }

      clearWarnings();

      localPhysicalConnection.needLine();
    }

    synchronized (localPhysicalConnection)
    {
      this.statement.fetch();
    }

    if (this.statement.validRows == 0)
    {
      internal_close(false);

      return false;
    }

    this.statement.currentRow = 0;

    this.statement.checkValidRowsStatus();

    return true;
  }

  public boolean isBeforeFirst()
    throws SQLException
  {
    return (!isEmptyResultSet()) && (this.statement.currentRow == -1) && (!this.closed);
  }

  public boolean isAfterLast()
    throws SQLException
  {
    return (!isEmptyResultSet()) && (this.closed);
  }

  public boolean isFirst()
    throws SQLException
  {
    return getRow() == 1;
  }

  public boolean isLast()
    throws SQLException
  {
    DatabaseError.throwSqlException(75, "isLast");

    return false;
  }

  public int getRow()
    throws SQLException
  {
    return this.statement.totalRowsVisited;
  }

  public synchronized String getString(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getString(i);
  }

  public synchronized boolean getBoolean(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getBoolean(i);
  }

  public synchronized byte getByte(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getByte(i);
  }

  public synchronized short getShort(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getShort(i);
  }

  public synchronized int getInt(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getInt(i);
  }

  public synchronized long getLong(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getLong(i);
  }

  public synchronized float getFloat(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getFloat(i);
  }

  public synchronized double getDouble(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getDouble(i);
  }

  public synchronized BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    if ((paramInt1 <= 0) || (paramInt1 > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt1;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt1);
    }

    return this.statement.accessors[(paramInt1 - 1)].getBigDecimal(this.statement.currentRow, paramInt2);
  }

  synchronized byte[] privateGetBytes(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].privateGetBytes(i);
  }

  public synchronized byte[] getBytes(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getBytes(i);
  }

  public synchronized Date getDate(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getDate(i);
  }

  public synchronized Time getTime(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTime(i);
  }

  public synchronized Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTimestamp(i);
  }

  public synchronized InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getAsciiStream(i);
  }

  public synchronized InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getUnicodeStream(i);
  }

  public synchronized InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getBinaryStream(i);
  }

  public synchronized Object getObject(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getObject(i);
  }

  public synchronized ResultSet getCursor(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getCursor(i);
  }

  public synchronized Datum getOracleObject(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getOracleObject(i);
  }

  public synchronized ROWID getROWID(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getROWID(i);
  }

  public synchronized NUMBER getNUMBER(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getNUMBER(i);
  }

  public synchronized DATE getDATE(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getDATE(i);
  }

  public synchronized ARRAY getARRAY(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getARRAY(i);
  }

  public synchronized STRUCT getSTRUCT(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getSTRUCT(i);
  }

  public synchronized OPAQUE getOPAQUE(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getOPAQUE(i);
  }

  public synchronized REF getREF(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getREF(i);
  }

  public synchronized CHAR getCHAR(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getCHAR(i);
  }

  public synchronized RAW getRAW(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getRAW(i);
  }

  public synchronized BLOB getBLOB(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getBLOB(i);
  }

  public synchronized CLOB getCLOB(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getCLOB(i);
  }

  public synchronized BFILE getBFILE(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getBFILE(i);
  }

  public synchronized BFILE getBfile(int paramInt)
    throws SQLException
  {
    return getBFILE(paramInt);
  }

  public synchronized CustomDatum getCustomDatum(int paramInt, CustomDatumFactory paramCustomDatumFactory)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getCustomDatum(this.statement.currentRow, paramCustomDatumFactory);
  }

  public synchronized ORAData getORAData(int paramInt, ORADataFactory paramORADataFactory)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getORAData(this.statement.currentRow, paramORADataFactory);
  }

  public synchronized Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getObject(this.statement.currentRow, paramMap);
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

  public synchronized Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getCharacterStream(i);
  }

  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getBigDecimal(i);
  }

  public Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getDate(this.statement.currentRow, paramCalendar);
  }

  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTime(this.statement.currentRow, paramCalendar);
  }

  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTimestamp(this.statement.currentRow, paramCalendar);
  }

  public INTERVALYM getINTERVALYM(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getINTERVALYM(i);
  }

  public INTERVALDS getINTERVALDS(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getINTERVALDS(i);
  }

  public TIMESTAMP getTIMESTAMP(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTIMESTAMP(i);
  }

  public TIMESTAMPTZ getTIMESTAMPTZ(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTIMESTAMPTZ(i);
  }

  public TIMESTAMPLTZ getTIMESTAMPLTZ(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getTIMESTAMPLTZ(i);
  }

  public synchronized URL getURL(int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt > this.statement.numberOfDefinePositions)) {
      DatabaseError.throwSqlException(3);
    }
    if (this.closed) {
      DatabaseError.throwSqlException(11);
    }
    int i = this.statement.currentRow;

    if (i < 0) {
      DatabaseError.throwSqlException(14);
    }
    this.statement.lastIndex = paramInt;

    if (this.statement.streamList != null)
    {
      this.statement.closeUsedStreams(paramInt);
    }

    return this.statement.accessors[(paramInt - 1)].getURL(i);
  }

  public void setFetchSize(int paramInt)
    throws SQLException
  {
    this.statement.setPrefetchInternal(paramInt, false, false);
  }

  public int getFetchSize()
    throws SQLException
  {
    return this.statement.getPrefetchInternal(false);
  }

  void internal_close(boolean paramBoolean)
    throws SQLException
  {
    if (this.closed) {
      return;
    }
    this.closed = true;

    if ((this.statement.gotLastBatch) && (this.statement.validRows == 0)) {
      this.m_emptyRset = true;
    }
    PhysicalConnection localPhysicalConnection = this.statement.connection;
    try
    {
      localPhysicalConnection.needLine();

      synchronized (localPhysicalConnection)
      {
        this.statement.closeQuery();
      }

    }
    catch (SQLException localSQLException)
    {
    }

    this.statement.endOfResultSet(paramBoolean);
  }

  public synchronized int findColumn(String paramString)
    throws SQLException
  {
    return this.statement.getColumnIndex(paramString);
  }

  boolean isEmptyResultSet()
  {
    return (this.m_emptyRset) || ((!this.m_emptyRset) && (this.statement.gotLastBatch) && (this.statement.validRows == 0));
  }

  int getValidRows()
  {
    return this.statement.validRows;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleResultSetImpl
 * JD-Core Version:    0.6.0
 */