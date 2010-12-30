package oracle.jdbc.rowset;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncFactoryException;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.driver.OracleDriver;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

public class OracleCachedRowSet extends OracleRowSet
  implements RowSet, RowSetInternal, Serializable, Cloneable, CachedRowSet
{
  private SQLWarning sqlWarning;
  private RowSetWarning rowsetWarning;
  protected int presentRow;
  private int currentPage;
  private boolean isPopulateDone;
  private boolean previousColumnWasNull;
  private OracleRow insertRow;
  private int insertRowPosition;
  private boolean insertRowFlag;
  private int updateRowPosition;
  private boolean updateRowFlag;
  protected ResultSetMetaData rowsetMetaData;
  private transient ResultSet resultSet;
  private transient Connection connection;
  private transient boolean isConnectionStayingOpenForTxnControl = false;
  protected Vector rows;
  private Vector param;
  private String[] metaData;
  protected int colCount;
  protected int rowCount;
  private RowSetReader reader;
  private RowSetWriter writer;
  private int[] keyColumns;
  private int pageSize;
  private SyncProvider syncProvider;
  private static final String DEFAULT_SYNCPROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
  private String tableName;
  private boolean driverManagerInitialized = false;

  public OracleCachedRowSet()
    throws SQLException
  {
    this.insertRowFlag = false;
    this.updateRowFlag = false;

    this.presentRow = 0;
    this.previousColumnWasNull = false;

    this.param = new Vector();

    this.rows = new Vector();

    this.sqlWarning = new SQLWarning();
    try
    {
      this.syncProvider = SyncFactory.getInstance("com.sun.rowset.providers.RIOptimisticProvider");
    }
    catch (SyncFactoryException localSyncFactoryException)
    {
      throw new SQLException("SyncProvider instance not constructed.");
    }

    setReader(new OracleCachedRowSetReader());
    setWriter(new OracleCachedRowSetWriter());

    this.currentPage = 0;
    this.pageSize = 0;
    this.isPopulateDone = false;

    this.keyColumns = null;
    this.tableName = null;
  }

  public Connection getConnection()
    throws SQLException
  {
    return getConnectionInternal();
  }

  Connection getConnectionInternal()
    throws SQLException
  {
    if ((this.connection == null) || (this.connection.isClosed()))
    {
      String str1 = getUsername();
      String str2 = getPassword();
      if (getDataSourceName() != null)
      {
        try
        {
          InitialContext localInitialContext = null;
          try
          {
            Properties localProperties = System.getProperties();
            localInitialContext = new InitialContext(localProperties);
          }
          catch (SecurityException localSecurityException) {
          }
          if (localInitialContext == null)
            localInitialContext = new InitialContext();
          DataSource localDataSource = (DataSource)localInitialContext.lookup(getDataSourceName());

          if ((this.username == null) || (str2 == null))
            this.connection = localDataSource.getConnection();
          else
            this.connection = localDataSource.getConnection(this.username, str2);
        }
        catch (NamingException localNamingException)
        {
          throw new SQLException("Unable to connect through the DataSource\n" + localNamingException.getMessage());
        }

      }
      else if (getUrl() != null)
      {
        if (!this.driverManagerInitialized)
        {
          DriverManager.registerDriver(new OracleDriver());
          this.driverManagerInitialized = true;
        }
        String str3 = getUrl();

        if ((str3.equals("")) || (str1.equals("")) || (str2.equals("")))
        {
          throw new SQLException("One or more of the authenticating parameter not set");
        }

        this.connection = DriverManager.getConnection(str3, str1, str2);
      }
      else
      {
        throw new SQLException("Authentication parameters not set");
      }
    }

    return this.connection;
  }

  public Statement getStatement()
    throws SQLException
  {
    if (this.resultSet == null)
    {
      throw new SQLException("ResultSet not open");
    }

    return this.resultSet.getStatement();
  }

  public RowSetReader getReader()
  {
    return this.reader;
  }

  public RowSetWriter getWriter()
  {
    return this.writer;
  }

  public void setFetchDirection(int paramInt)
    throws SQLException
  {
    if (this.rowsetType == 1005) {
      throw new SQLException("Fetch direction cannot be applied when RowSet type is TYPE_SCROLL_SENSITIVE");
    }
    switch (paramInt)
    {
    case 1000:
    case 1002:
      this.presentRow = 0;
      break;
    case 1001:
      if (this.rowsetType == 1003) {
        throw new SQLException("FETCH_REVERSE cannot be applied when RowSet type is TYPE_FORWARD_ONLY");
      }
      this.presentRow = (this.rowCount + 1);
      break;
    default:
      throw new SQLException("Illegal fetch direction");
    }

    super.setFetchDirection(paramInt);
  }

  public void setReader(RowSetReader paramRowSetReader)
  {
    this.reader = paramRowSetReader;
  }

  public void setWriter(RowSetWriter paramRowSetWriter)
  {
    this.writer = paramRowSetWriter;
  }

  private final int getColumnIndex(String paramString)
    throws SQLException
  {
    paramString = paramString.toUpperCase();
    int i = 0;
    for (; i < this.metaData.length; i++)
    {
      if (paramString.equals(this.metaData[i]))
      {
        break;
      }

    }

    if (i >= this.metaData.length) {
      throw new SQLException("Invalid column name: " + paramString);
    }
    return i + 1;
  }

  private final void checkColumnIndex(int paramInt)
    throws SQLException
  {
    if (this.readOnly)
      throw new SQLException("The RowSet is not write enabled");
    if ((paramInt < 1) || (paramInt > this.colCount))
      throw new SQLException("invalid index : " + paramInt);
  }

  private final boolean isUpdated(int paramInt)
    throws SQLException
  {
    if ((paramInt < 1) || (paramInt > this.colCount))
      throw new SQLException("Invalid index : " + paramInt);
    return getCurrentRow().isColumnChanged(paramInt);
  }

  private final void checkParamIndex(int paramInt)
    throws SQLException
  {
    if (paramInt < 1)
      throw new SQLException("Invalid parameter index : " + paramInt);
  }

  private final void populateInit(ResultSet paramResultSet)
    throws SQLException
  {
    this.resultSet = paramResultSet;
    Statement localStatement = paramResultSet.getStatement();

    this.maxFieldSize = localStatement.getMaxFieldSize();

    this.fetchSize = localStatement.getFetchSize();
    this.queryTimeout = localStatement.getQueryTimeout();

    this.connection = localStatement.getConnection();

    this.transactionIsolation = this.connection.getTransactionIsolation();

    this.typeMap = this.connection.getTypeMap();
    DatabaseMetaData localDatabaseMetaData = this.connection.getMetaData();

    this.url = localDatabaseMetaData.getURL();
    this.username = localDatabaseMetaData.getUserName();
  }

  private synchronized InputStream getStream(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if (localObject == null) {
      return null;
    }
    if ((localObject instanceof InputStream)) {
      return (InputStream)localObject;
    }
    if ((localObject instanceof String))
    {
      return new ByteArrayInputStream(((String)localObject).getBytes());
    }
    if ((localObject instanceof byte[]))
    {
      return new ByteArrayInputStream((byte[])localObject);
    }
    if ((localObject instanceof OracleSerialClob))
      return ((OracleSerialClob)localObject).getAsciiStream();
    if ((localObject instanceof OracleSerialBlob))
      return ((OracleSerialBlob)localObject).getBinaryStream();
    if ((localObject instanceof Reader))
    {
      try
      {
        BufferedReader localBufferedReader = new BufferedReader((Reader)localObject);
        int i = 0;
        PipedInputStream localPipedInputStream = new PipedInputStream();
        PipedOutputStream localPipedOutputStream = new PipedOutputStream(localPipedInputStream);
        while ((i = localBufferedReader.read()) != -1)
          localPipedOutputStream.write(i);
        localPipedOutputStream.close();
        return localPipedInputStream;
      } catch (IOException localIOException) {
        throw new SQLException("Error during conversion: " + localIOException.getMessage());
      }
    }

    throw new SQLException("Could not convert the column into a stream type");
  }

  protected synchronized void notifyCursorMoved()
  {
    if (this.insertRowFlag)
    {
      this.insertRowFlag = false;
      this.insertRow.setRowUpdated(false);
      this.sqlWarning.setNextWarning(new SQLWarning("Cancelling insertion, due to cursor movement."));
    }
    else if (this.updateRowFlag)
    {
      try
      {
        this.updateRowFlag = false;
        int i = this.presentRow;
        this.presentRow = this.updateRowPosition;
        getCurrentRow().setRowUpdated(false);
        this.presentRow = i;
        this.sqlWarning.setNextWarning(new SQLWarning("Cancelling all updates, due to cursor movement."));
      }
      catch (SQLException localSQLException)
      {
      }

    }

    super.notifyCursorMoved();
  }

  protected void checkAndFilterObject(int paramInt, Object paramObject)
    throws SQLException
  {
  }

  OracleRow getCurrentRow()
    throws SQLException
  {
    int i = this.presentRow - 1;

    if ((this.presentRow < 1) || (this.presentRow > this.rowCount)) {
      throw new SQLException("Operation with out calling next/previous");
    }

    return (OracleRow)this.rows.elementAt(this.presentRow - 1);
  }

  boolean isConnectionStayingOpen()
  {
    return this.isConnectionStayingOpenForTxnControl;
  }

  void setOriginal()
    throws SQLException
  {
    int i = 1;
    do
    {
      boolean bool = setOriginalRowInternal(i);

      if (bool)
        continue;
      i++;
    }

    while (i <= this.rowCount);

    notifyRowSetChanged();

    this.presentRow = 0;
  }

  boolean setOriginalRowInternal(int paramInt)
    throws SQLException
  {
    if ((paramInt < 1) || (paramInt > this.rowCount)) {
      throw new SQLException("Invalid cursor position, try next/previous first");
    }
    int i = 0;

    OracleRow localOracleRow = (OracleRow)this.rows.elementAt(paramInt - 1);

    if (localOracleRow.isRowDeleted())
    {
      this.rows.remove(paramInt - 1);
      this.rowCount -= 1;
      i = 1;
    }
    else
    {
      if (localOracleRow.isRowInserted())
      {
        localOracleRow.setInsertedFlag(false);
      }

      if (localOracleRow.isRowUpdated())
      {
        localOracleRow.makeUpdatesOriginal();
      }

    }

    return i;
  }

  public boolean next()
    throws SQLException
  {
    if (this.rowCount < 0)
    {
      return false;
    }

    if ((this.fetchDirection == 1000) || (this.fetchDirection == 1002))
    {
      if (this.presentRow + 1 <= this.rowCount)
      {
        this.presentRow += 1;
        if ((!this.showDeleted) && (getCurrentRow().isRowDeleted())) {
          return next();
        }
        notifyCursorMoved();
        return true;
      }

      this.presentRow = (this.rowCount + 1);
      return false;
    }

    if (this.fetchDirection == 1001)
    {
      if (this.presentRow - 1 > 0)
      {
        this.presentRow -= 1;
        if ((!this.showDeleted) && (getCurrentRow().isRowDeleted()))
          return next();
        notifyCursorMoved();
        return true;
      }

      this.presentRow = 0;
      return false;
    }

    return false;
  }

  public boolean previous()
    throws SQLException
  {
    if (this.rowCount < 0)
    {
      return false;
    }

    if (this.fetchDirection == 1001)
    {
      if (this.presentRow + 1 <= this.rowCount)
      {
        this.presentRow += 1;
        if ((!this.showDeleted) && (getCurrentRow().isRowDeleted()))
          return previous();
        notifyCursorMoved();
        return true;
      }

      this.presentRow = (this.rowCount + 1);
      return false;
    }

    if ((this.fetchDirection == 1000) || (this.fetchDirection == 1002))
    {
      if (this.presentRow - 1 > 0)
      {
        this.presentRow -= 1;
        if ((!this.showDeleted) && (getCurrentRow().isRowDeleted()))
          return previous();
        notifyCursorMoved();
        return true;
      }

      this.presentRow = 0;
      return false;
    }

    return false;
  }

  public boolean isBeforeFirst()
    throws SQLException
  {
    return (this.rowCount > 0) && (this.presentRow == 0);
  }

  public boolean isAfterLast()
    throws SQLException
  {
    return (this.rowCount > 0) && (this.presentRow == this.rowCount + 1);
  }

  public boolean isFirst()
    throws SQLException
  {
    return this.presentRow == 1;
  }

  public boolean isLast()
    throws SQLException
  {
    return this.presentRow == this.rowCount;
  }

  public void beforeFirst()
    throws SQLException
  {
    this.presentRow = 0;
  }

  public void afterLast() throws SQLException
  {
    this.presentRow = (this.rowCount + 1);
  }

  public boolean first()
    throws SQLException
  {
    return absolute(1);
  }

  public boolean last()
    throws SQLException
  {
    return absolute(-1);
  }

  public boolean absolute(int paramInt)
    throws SQLException
  {
    if (this.rowsetType == 1003)
      throw new SQLException("The RowSet type is TYPE_FORWARD_ONLY");
    if ((paramInt == 0) || (Math.abs(paramInt) > this.rowCount)) return false;
    this.presentRow = (paramInt < 0 ? this.rowCount + paramInt + 1 : paramInt);
    notifyCursorMoved();
    return true;
  }

  public boolean relative(int paramInt)
    throws SQLException
  {
    return absolute(this.presentRow + paramInt);
  }

  public synchronized void populate(ResultSet paramResultSet)
    throws SQLException
  {
    if (this.rows == null)
    {
      this.rows = new Vector(50, 10);
    }
    else
    {
      this.rows.clear();
    }this.rowsetMetaData = new OracleRowSetMetaData(paramResultSet.getMetaData());
    this.metaData = new String[this.colCount = this.rowsetMetaData.getColumnCount()];
    for (int i = 0; i < this.colCount; i++) {
      this.metaData[i] = this.rowsetMetaData.getColumnName(i + 1);
    }

    if (!(paramResultSet instanceof OracleCachedRowSet)) {
      populateInit(paramResultSet);
    }

    i = (this.fetchDirection == 1000) || (this.fetchDirection == 1002) ? 1 : 0;

    this.rowCount = 0;
    OracleRow localOracleRow = null;
    int j;
    if ((this.maxRows == 0) && (this.pageSize == 0))
    {
      j = 2147483647;
    }
    else if ((this.maxRows == 0) || (this.pageSize == 0))
    {
      j = Math.max(this.maxRows, this.pageSize);
    }
    else
    {
      j = Math.min(this.maxRows, this.pageSize);
    }

    if ((paramResultSet.getType() != 1003) && (this.rows.size() == 0))
    {
      if (i == 0)
      {
        paramResultSet.afterLast();
      }
    }

    while (this.rowCount < j)
    {
      if (i != 0 ? 
        !paramResultSet.next() : 
        !paramResultSet.previous())
      {
        break;
      }
      localOracleRow = new OracleRow(this.colCount);
      for (int k = 1; k <= this.colCount; k++)
      {
        Object localObject = null;
        try
        {
          localObject = paramResultSet.getObject(k, this.typeMap);
        }
        catch (Exception localException)
        {
          localObject = paramResultSet.getObject(k);
        }
        catch (AbstractMethodError localAbstractMethodError)
        {
          localObject = paramResultSet.getObject(k);
        }

        if (((localObject instanceof Clob)) || ((localObject instanceof CLOB))) {
          localOracleRow.setColumnValue(k, new OracleSerialClob((Clob)localObject));
        }
        else if (((localObject instanceof Blob)) || ((localObject instanceof BLOB))) {
          localOracleRow.setColumnValue(k, new OracleSerialBlob((Blob)localObject));
        }
        else {
          localOracleRow.setColumnValue(k, localObject);
        }
        localOracleRow.markOriginalNull(k, paramResultSet.wasNull());
      }

      if (i != 0)
      {
        this.rows.add(localOracleRow);
      }
      else
      {
        this.rows.add(1, localOracleRow);
      }

      this.rowCount += 1;
    }

    if (((i != 0) && (paramResultSet.isAfterLast())) || ((i == 0) && (paramResultSet.isBeforeFirst())))
    {
      this.isPopulateDone = true;
    }

    this.connection = null;

    notifyRowSetChanged();
  }

  public String getCursorName()
    throws SQLException
  {
    throw new SQLException("Getting the cursor name is not supported.");
  }

  public synchronized void clearParameters()
    throws SQLException
  {
    this.param = null;
    this.param = new Vector();
  }

  public boolean wasNull()
    throws SQLException
  {
    return this.previousColumnWasNull;
  }

  public void close()
    throws SQLException
  {
    release();
  }

  public SQLWarning getWarnings()
    throws SQLException
  {
    return this.sqlWarning;
  }

  public void clearWarnings()
    throws SQLException
  {
    this.sqlWarning = null;
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    return this.rowsetMetaData;
  }

  public int findColumn(String paramString)
    throws SQLException
  {
    return getColumnIndex(paramString);
  }

  public Object[] getParams()
    throws SQLException
  {
    return this.param.toArray();
  }

  public void setMetaData(RowSetMetaData paramRowSetMetaData)
    throws SQLException
  {
    this.rowsetMetaData = paramRowSetMetaData;

    if (paramRowSetMetaData != null)
    {
      this.colCount = paramRowSetMetaData.getColumnCount();
    }
  }

  public synchronized void execute()
    throws SQLException
  {
    this.isConnectionStayingOpenForTxnControl = false;
    getReader().readData(this);
  }

  public void acceptChanges()
    throws SyncProviderException
  {
    try
    {
      getWriter().writeData(this);
    }
    catch (SQLException localSQLException)
    {
      throw new SyncProviderException(localSQLException.getMessage());
    }
  }

  public void acceptChanges(Connection paramConnection)
    throws SyncProviderException
  {
    this.connection = paramConnection;

    this.isConnectionStayingOpenForTxnControl = true;
    acceptChanges();
  }

  public Object clone()
    throws CloneNotSupportedException
  {
    try
    {
      return createCopy();
    } catch (SQLException localSQLException) {
    }
    throw new CloneNotSupportedException("SQL Error occured while cloning,\n" + localSQLException.getMessage());
  }

  public CachedRowSet createCopy()
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = (OracleCachedRowSet)createShared();
    int i = this.rows.size();
    localOracleCachedRowSet.rows = new Vector(i);
    for (int j = 0; j < i; j++) {
      localOracleCachedRowSet.rows.add(((OracleRow)this.rows.elementAt(j)).createCopy());
    }

    return localOracleCachedRowSet;
  }

  public RowSet createShared()
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = new OracleCachedRowSet();

    localOracleCachedRowSet.rows = this.rows;

    localOracleCachedRowSet.setDataSource(getDataSource());
    localOracleCachedRowSet.setDataSourceName(getDataSourceName());
    localOracleCachedRowSet.setUsername(getUsername());
    localOracleCachedRowSet.setPassword(getPassword());
    localOracleCachedRowSet.setUrl(getUrl());
    localOracleCachedRowSet.setTypeMap(getTypeMap());
    localOracleCachedRowSet.setMaxFieldSize(getMaxFieldSize());
    localOracleCachedRowSet.setMaxRows(getMaxRows());
    localOracleCachedRowSet.setQueryTimeout(getQueryTimeout());
    localOracleCachedRowSet.setFetchSize(getFetchSize());
    localOracleCachedRowSet.setEscapeProcessing(getEscapeProcessing());
    localOracleCachedRowSet.setConcurrency(getConcurrency());
    localOracleCachedRowSet.setReadOnly(this.readOnly);

    this.rowsetType = getType();
    this.fetchDirection = getFetchDirection();
    localOracleCachedRowSet.setCommand(getCommand());
    localOracleCachedRowSet.setTransactionIsolation(getTransactionIsolation());

    localOracleCachedRowSet.presentRow = this.presentRow;
    localOracleCachedRowSet.colCount = this.colCount;
    localOracleCachedRowSet.rowCount = this.rowCount;
    localOracleCachedRowSet.showDeleted = this.showDeleted;

    localOracleCachedRowSet.syncProvider = this.syncProvider;

    localOracleCachedRowSet.currentPage = this.currentPage;
    localOracleCachedRowSet.pageSize = this.pageSize;
    localOracleCachedRowSet.tableName = (this.tableName == null ? null : new String(this.tableName));
    localOracleCachedRowSet.keyColumns = (this.keyColumns == null ? null : (int[])this.keyColumns.clone());

    int i = this.listener.size();
    for (int j = 0; j < i; j++) {
      localOracleCachedRowSet.listener.add(this.listener.elementAt(j));
    }

    localOracleCachedRowSet.rowsetMetaData = new OracleRowSetMetaData(this.rowsetMetaData);

    i = this.param.size();
    for (j = 0; j < i; j++) {
      localOracleCachedRowSet.param.add(this.param.elementAt(j));
    }
    localOracleCachedRowSet.metaData = new String[this.metaData.length];
    System.arraycopy(this.metaData, 0, localOracleCachedRowSet.metaData, 0, this.metaData.length);

    return localOracleCachedRowSet;
  }

  public void release()
    throws SQLException
  {
    this.rows = null;
    this.rows = new Vector();
    if ((this.connection != null) && (!this.connection.isClosed()))
      this.connection.close();
    this.rowCount = 0;
    this.presentRow = 0;
    notifyRowSetChanged();
  }

  public void restoreOriginal()
    throws SQLException
  {
    int i = 0;
    for (int j = 0; j < this.rowCount; j++)
    {
      OracleRow localOracleRow = (OracleRow)this.rows.elementAt(j);
      if (localOracleRow.isRowInserted())
      {
        this.rows.remove(j);
        this.rowCount -= 1;

        j--;
        i = 1;
      }
      else if (localOracleRow.isRowUpdated())
      {
        localOracleRow.setRowUpdated(false);
        i = 1;
      } else {
        if (!localOracleRow.isRowDeleted())
          continue;
        localOracleRow.setRowDeleted(false);
        i = 1;
      }

    }

    if (i == 0) {
      throw new SQLException("None of the rows are changed");
    }

    notifyRowSetChanged();

    this.presentRow = 0;
  }

  public Collection toCollection()
    throws SQLException
  {
    Map localMap = Collections.synchronizedMap(new TreeMap());
    try
    {
      for (int i = 0; i < this.rowCount; i++)
      {
        localMap.put(new Integer(i), ((OracleRow)this.rows.elementAt(i)).toCollection());
      }

    }
    catch (Exception localException)
    {
      localMap = null;
      throw new SQLException("Map operation failed in toCollection()");
    }

    return localMap.values();
  }

  public Collection toCollection(int paramInt)
    throws SQLException
  {
    if ((paramInt < 1) || (paramInt > this.colCount)) {
      throw new SQLException("invalid index : " + paramInt);
    }
    Vector localVector = new Vector(this.rowCount);
    for (int i = 0; i < this.rowCount; i++)
    {
      OracleRow localOracleRow = (OracleRow)this.rows.elementAt(i);
      Object localObject = localOracleRow.isColumnChanged(paramInt) ? localOracleRow.getModifiedColumn(paramInt) : localOracleRow.getColumn(paramInt);

      localVector.add(localObject);
    }

    return localVector;
  }

  public Collection toCollection(String paramString)
    throws SQLException
  {
    return toCollection(getColumnIndex(paramString));
  }

  public int getRow()
    throws SQLException
  {
    if (this.presentRow > this.rowCount) {
      return this.rowCount;
    }

    return this.presentRow;
  }

  public void cancelRowInsert()
    throws SQLException
  {
    if (getCurrentRow().isRowInserted())
    {
      this.rows.remove(--this.presentRow);
      this.rowCount -= 1;
      notifyRowChanged();
    }
    else {
      throw new SQLException("The row is not inserted");
    }
  }

  public void cancelRowDelete()
    throws SQLException
  {
    if (getCurrentRow().isRowDeleted())
    {
      getCurrentRow().setRowDeleted(false);
      notifyRowChanged();
    }
    else {
      throw new SQLException("The row is not deleted");
    }
  }

  public void cancelRowUpdates()
    throws SQLException
  {
    if (getCurrentRow().isRowUpdated())
    {
      this.updateRowFlag = false;
      getCurrentRow().setRowUpdated(false);
      notifyRowChanged();
    }
    else {
      throw new SQLException("The row is not updated.");
    }
  }

  public void insertRow()
    throws SQLException
  {
    if (!this.insertRowFlag) {
      throw new SQLException("Current row not inserted/updated.");
    }
    if (!this.insertRow.isRowFullyPopulated()) {
      throw new SQLException("All the columns of the row are not set");
    }

    this.insertRow.insertRow();
    this.rows.insertElementAt(this.insertRow, this.insertRowPosition - 1);
    this.insertRowFlag = false;
    this.rowCount += 1;
    notifyRowChanged();
  }

  public void updateRow()
    throws SQLException
  {
    if (this.updateRowFlag)
    {
      this.updateRowFlag = false;
      getCurrentRow().setRowUpdated(true);
      notifyRowChanged();
    }
    else {
      throw new SQLException("Current row not updated");
    }
  }

  public void deleteRow()
    throws SQLException
  {
    getCurrentRow().setRowDeleted(true);
    notifyRowChanged();
  }

  public void refreshRow()
    throws SQLException
  {
    OracleRow localOracleRow = getCurrentRow();
    if (localOracleRow.isRowUpdated())
      localOracleRow.cancelRowUpdates();
  }

  public void moveToInsertRow()
    throws SQLException
  {
    this.insertRow = new OracleRow(this.colCount, true);
    this.insertRowFlag = true;

    if (isAfterLast())
      this.insertRowPosition = this.presentRow;
    else
      this.insertRowPosition = (this.presentRow + 1);
  }

  public void moveToCurrentRow()
    throws SQLException
  {
    this.insertRowFlag = false;
    this.updateRowFlag = false;
    absolute(this.presentRow);
  }

  public boolean rowUpdated()
    throws SQLException
  {
    return getCurrentRow().isRowUpdated();
  }

  public boolean rowInserted()
    throws SQLException
  {
    return getCurrentRow().isRowInserted();
  }

  public boolean rowDeleted()
    throws SQLException
  {
    return getCurrentRow().isRowDeleted();
  }

  public ResultSet getOriginalRow()
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = new OracleCachedRowSet();
    localOracleCachedRowSet.rowsetMetaData = this.rowsetMetaData;
    localOracleCachedRowSet.rowCount = 1;
    localOracleCachedRowSet.colCount = this.colCount;
    localOracleCachedRowSet.presentRow = 0;
    localOracleCachedRowSet.setReader(null);
    localOracleCachedRowSet.setWriter(null);
    OracleRow localOracleRow = new OracleRow(this.rowsetMetaData.getColumnCount(), getCurrentRow().getOriginalRow());

    localOracleCachedRowSet.rows.add(localOracleRow);

    return localOracleCachedRowSet;
  }

  public ResultSet getOriginal()
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = new OracleCachedRowSet();
    localOracleCachedRowSet.rowsetMetaData = this.rowsetMetaData;
    localOracleCachedRowSet.rowCount = this.rowCount;
    localOracleCachedRowSet.colCount = this.colCount;

    localOracleCachedRowSet.presentRow = 0;

    localOracleCachedRowSet.setType(1004);
    localOracleCachedRowSet.setConcurrency(1008);

    localOracleCachedRowSet.setReader(null);
    localOracleCachedRowSet.setWriter(null);
    int i = this.rowsetMetaData.getColumnCount();
    OracleRow localOracleRow = null;

    Iterator localIterator = this.rows.iterator();
    while (localIterator.hasNext())
    {
      localOracleRow = new OracleRow(i, ((OracleRow)localIterator.next()).getOriginalRow());

      localOracleCachedRowSet.rows.add(localOracleRow);
    }

    return localOracleCachedRowSet;
  }

  public void setNull(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    this.param.add(paramInt1 - 1, null);
  }

  public void setNull(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { new Integer(paramInt2), paramString };
    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, new Boolean(paramBoolean));
  }

  public void setByte(int paramInt, byte paramByte)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, new Byte(paramByte));
  }

  public void setShort(int paramInt, short paramShort)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, new Short(paramShort));
  }

  public void setInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    this.param.add(paramInt1 - 1, new Integer(paramInt2));
  }

  public void setLong(int paramInt, long paramLong)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, new Long(paramLong));
  }

  public void setFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, new Float(paramFloat));
  }

  public void setDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, new Double(paramDouble));
  }

  public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramBigDecimal);
  }

  public void setString(int paramInt, String paramString)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramString);
  }

  public void setBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramArrayOfByte);
  }

  public void setDate(int paramInt, java.sql.Date paramDate)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramDate);
  }

  public void setTime(int paramInt, Time paramTime)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramTime);
  }

  public void setObject(int paramInt, Object paramObject)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramObject);
  }

  public void setRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramRef);
  }

  public void setBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramBlob);
  }

  public void setClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramClob);
  }

  public void setArray(int paramInt, Array paramArray)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramArray);
  }

  public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { paramInputStream, new Integer(paramInt2), new Integer(546) };

    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    checkParamIndex(paramInt);
    Object[] arrayOfObject = { paramTime, paramCalendar };
    this.param.add(paramInt - 1, arrayOfObject);
  }

  public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    checkParamIndex(paramInt);
    Object[] arrayOfObject = { paramTimestamp, paramCalendar };
    this.param.add(paramInt - 1, arrayOfObject);
  }

  public void setTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    checkParamIndex(paramInt);
    this.param.add(paramInt - 1, paramTimestamp);
  }

  public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { paramInputStream, new Integer(paramInt2), new Integer(819) };

    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setUnicodeStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { paramInputStream, new Integer(paramInt2), new Integer(273) };

    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { paramReader, new Integer(paramInt2) };
    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { paramObject, new Integer(paramInt2), new Integer(paramInt3) };
    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    checkParamIndex(paramInt1);
    Object[] arrayOfObject = { paramObject, new Integer(paramInt2) };
    this.param.add(paramInt1 - 1, arrayOfObject);
  }

  public void setDate(int paramInt, java.sql.Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    checkParamIndex(paramInt);
    Object[] arrayOfObject = { paramDate, paramCalendar };
    this.param.add(paramInt - 1, arrayOfObject);
  }

  public synchronized Object getObject(int paramInt)
    throws SQLException
  {
    int i = this.presentRow * this.colCount + paramInt - 1;
    Object localObject = null;

    if (!isUpdated(paramInt))
      localObject = getCurrentRow().getColumn(paramInt);
    else
      localObject = getCurrentRow().getModifiedColumn(paramInt);
    this.previousColumnWasNull = (localObject == null);

    return localObject;
  }

  private synchronized Number getNumber(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if ((localObject == null) || ((localObject instanceof BigDecimal)) || ((localObject instanceof Number)))
    {
      return (Number)localObject;
    }
    if ((localObject instanceof Boolean))
      return new Integer(((Boolean)localObject).booleanValue() ? 1 : 0);
    if ((localObject instanceof String))
    {
      try
      {
        return new BigDecimal((String)localObject);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        throw new SQLException("Fail to convert to internal representation");
      }

    }

    throw new SQLException("Fail to convert to internal representation");
  }

  public Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getObject(paramInt);
  }

  public boolean getBoolean(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if (localObject == null) {
      return false;
    }
    if ((localObject instanceof Boolean)) {
      return ((Boolean)localObject).booleanValue();
    }
    if ((localObject instanceof Number)) {
      return ((Number)localObject).doubleValue() != 0.0D;
    }

    throw new SQLException("Fail to convert to internal representation");
  }

  public byte getByte(int paramInt)
    throws SQLException
  {
    Object localObject1 = getObject(paramInt);

    if (localObject1 == null) {
      return 0;
    }
    if ((localObject1 instanceof Number)) {
      return ((Number)localObject1).byteValue();
    }
    if ((localObject1 instanceof String))
      return ((String)localObject1).getBytes()[0];
    Object localObject2;
    if ((localObject1 instanceof OracleSerialBlob))
    {
      localObject2 = (OracleSerialBlob)localObject1;
      return localObject2.getBytes(0L, 1)[0];
    }
    if ((localObject1 instanceof OracleSerialClob))
    {
      localObject2 = (OracleSerialClob)localObject1;
      return localObject2.getSubString(0L, 1).getBytes()[0];
    }

    throw new SQLException("Fail to convert to internal representation");
  }

  public short getShort(int paramInt)
    throws SQLException
  {
    Number localNumber = getNumber(paramInt);

    return localNumber == null ? 0 : localNumber.shortValue();
  }

  public int getInt(int paramInt)
    throws SQLException
  {
    Number localNumber = getNumber(paramInt);

    return localNumber == null ? 0 : localNumber.intValue();
  }

  public long getLong(int paramInt)
    throws SQLException
  {
    Number localNumber = getNumber(paramInt);

    return localNumber == null ? 0L : localNumber.longValue();
  }

  public float getFloat(int paramInt)
    throws SQLException
  {
    Number localNumber = getNumber(paramInt);

    return localNumber == null ? 0.0F : localNumber.floatValue();
  }

  public double getDouble(int paramInt)
    throws SQLException
  {
    Number localNumber = getNumber(paramInt);

    return localNumber == null ? 0.0D : localNumber.doubleValue();
  }

  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    Number localNumber = getNumber(paramInt);

    if ((localNumber == null) || ((localNumber instanceof BigDecimal)))
      return (BigDecimal)localNumber;
    if ((localNumber instanceof Number)) {
      return new BigDecimal(((Number)localNumber).doubleValue());
    }

    return null;
  }

  public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    return getBigDecimal(paramInt1);
  }

  public java.sql.Date getDate(int paramInt)
    throws SQLException
  {
    Object localObject1 = getObject(paramInt);

    if (localObject1 == null)
      return (java.sql.Date)localObject1;
    Object localObject2;
    if ((localObject1 instanceof Time))
    {
      localObject2 = (Time)localObject1;
      return new java.sql.Date(((Time)localObject2).getTime());
    }

    if ((localObject1 instanceof java.util.Date))
    {
      localObject2 = (java.util.Date)localObject1;
      return new java.sql.Date(((java.util.Date)localObject2).getYear(), ((java.util.Date)localObject2).getMonth(), ((java.util.Date)localObject2).getDate());
    }

    throw new SQLException("Invalid column type");
  }

  public java.sql.Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return getDate(paramInt);
  }

  public Time getTime(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if (localObject == null)
      return (Time)localObject;
    if ((localObject instanceof java.util.Date))
    {
      java.util.Date localDate = (java.util.Date)localObject;
      return new Time(localDate.getHours(), localDate.getMinutes(), localDate.getSeconds());
    }

    throw new SQLException("Invalid column type");
  }

  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return getTime(paramInt);
  }

  public Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if ((localObject == null) || ((localObject instanceof Timestamp)))
      return (Timestamp)localObject;
    if ((localObject instanceof java.util.Date)) {
      return new Timestamp(((java.util.Date)localObject).getTime());
    }
    throw new SQLException("Invalid column type");
  }

  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return getTimestamp(paramInt);
  }

  public byte[] getBytes(int paramInt)
    throws SQLException
  {
    Object localObject1 = getObject(paramInt);

    if (localObject1 == null) {
      return (byte[])localObject1;
    }
    if ((localObject1 instanceof byte[])) {
      return (byte[])localObject1;
    }
    if ((localObject1 instanceof String)) {
      return (byte[])((String)localObject1).getBytes();
    }
    if ((localObject1 instanceof Number)) {
      return (byte[])((Number)localObject1).toString().getBytes();
    }
    if ((localObject1 instanceof BigDecimal))
      return (byte[])((BigDecimal)localObject1).toString().getBytes();
    Object localObject2;
    if ((localObject1 instanceof OracleSerialBlob))
    {
      localObject2 = (OracleSerialBlob)localObject1;
      return ((OracleSerialBlob)localObject2).getBytes(0L, (int)((OracleSerialBlob)localObject2).length());
    }
    if ((localObject1 instanceof OracleSerialClob))
    {
      localObject2 = (OracleSerialClob)localObject1;
      return ((OracleSerialClob)localObject2).getSubString(0L, (int)((OracleSerialClob)localObject2).length()).getBytes();
    }

    throw new SQLException("Fail to convert to internal representation");
  }

  public Ref getRef(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if ((localObject == null) || ((localObject instanceof Ref))) {
      return (Ref)localObject;
    }
    throw new SQLException("Invalid column type");
  }

  public Blob getBlob(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if ((localObject instanceof OracleSerialBlob)) {
      return localObject == null ? null : (Blob)localObject;
    }
    throw new SQLException("Invalid column type");
  }

  public Clob getClob(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if ((localObject instanceof OracleSerialClob)) {
      return localObject == null ? null : (Clob)localObject;
    }
    throw new SQLException("Invalid column type");
  }

  public Array getArray(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if ((localObject == null) || ((localObject instanceof Array))) {
      return (Array)localObject;
    }
    throw new SQLException("Invalid column type");
  }

  public String getString(int paramInt)
    throws SQLException
  {
    Object localObject1 = getObject(paramInt);

    if (localObject1 == null) {
      return (String)localObject1;
    }
    if ((localObject1 instanceof String)) {
      return (String)localObject1;
    }
    if (((localObject1 instanceof Number)) || ((localObject1 instanceof BigDecimal))) {
      return localObject1.toString();
    }
    if ((localObject1 instanceof java.sql.Date)) {
      return ((java.sql.Date)localObject1).toString();
    }
    if ((localObject1 instanceof Timestamp)) {
      return ((java.sql.Date)localObject1).toString();
    }
    if ((localObject1 instanceof byte[]))
      return new String((byte[])localObject1);
    Object localObject2;
    if ((localObject1 instanceof OracleSerialClob))
    {
      localObject2 = (OracleSerialClob)localObject1;
      return ((OracleSerialClob)localObject2).getSubString(0L, (int)((OracleSerialClob)localObject2).length());
    }

    if ((localObject1 instanceof OracleSerialBlob))
    {
      localObject2 = (OracleSerialBlob)localObject1;
      return new String(((OracleSerialBlob)localObject2).getBytes(0L, (int)((OracleSerialBlob)localObject2).length()));
    }

    if ((localObject1 instanceof URL)) {
      return ((URL)localObject1).toString();
    }
    if ((localObject1 instanceof Reader))
    {
      try
      {
        localObject2 = (Reader)localObject1;
        char[] arrayOfChar = new char[1024];
        int i = 0;
        StringBuffer localStringBuffer = new StringBuffer(1024);
        while ((i = ((Reader)localObject2).read(arrayOfChar)) > 0)
          localStringBuffer.append(arrayOfChar, 0, i);
        return localStringBuffer.substring(0, localStringBuffer.length());
      } catch (IOException localIOException) {
        throw new SQLException("Error during conversion: " + localIOException.getMessage());
      }
    }

    throw new SQLException("Fail to convert to internal representation");
  }

  public InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = getStream(paramInt);

    return localInputStream == null ? null : localInputStream;
  }

  public InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if (localObject == null) {
      return (InputStream)localObject;
    }

    if ((localObject instanceof String)) {
      return new StringBufferInputStream((String)localObject);
    }
    throw new SQLException("Fail to convert to internal representation");
  }

  public InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = getStream(paramInt);

    return localInputStream == null ? null : localInputStream;
  }

  public synchronized Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    try
    {
      InputStream localInputStream = getAsciiStream(paramInt);

      if (localInputStream == null)
        return null;
      StringBuffer localStringBuffer = new StringBuffer();
      int i = 0;
      while ((i = localInputStream.read()) != -1) {
        localStringBuffer.append((char)i);
      }

      char[] arrayOfChar = new char[localStringBuffer.length()];
      localStringBuffer.getChars(0, localStringBuffer.length(), arrayOfChar, 0);
      CharArrayReader localCharArrayReader = new CharArrayReader(arrayOfChar);
      arrayOfChar = null;

      return localCharArrayReader;
    }
    catch (IOException localIOException)
    {
    }

    throw new SQLException("Error: could not read from the stream");
  }

  public synchronized Object getObject(String paramString)
    throws SQLException
  {
    return getObject(getColumnIndex(paramString));
  }

  public boolean getBoolean(String paramString)
    throws SQLException
  {
    return getBoolean(getColumnIndex(paramString));
  }

  public byte getByte(String paramString)
    throws SQLException
  {
    return getByte(getColumnIndex(paramString));
  }

  public short getShort(String paramString)
    throws SQLException
  {
    return getShort(getColumnIndex(paramString));
  }

  public int getInt(String paramString)
    throws SQLException
  {
    return getInt(getColumnIndex(paramString));
  }

  public long getLong(String paramString)
    throws SQLException
  {
    return getLong(getColumnIndex(paramString));
  }

  public float getFloat(String paramString)
    throws SQLException
  {
    return getFloat(getColumnIndex(paramString));
  }

  public double getDouble(String paramString)
    throws SQLException
  {
    return getDouble(getColumnIndex(paramString));
  }

  public BigDecimal getBigDecimal(String paramString, int paramInt)
    throws SQLException
  {
    return getBigDecimal(getColumnIndex(paramString), paramInt);
  }

  public byte[] getBytes(String paramString)
    throws SQLException
  {
    return getBytes(getColumnIndex(paramString));
  }

  public java.sql.Date getDate(String paramString)
    throws SQLException
  {
    return getDate(getColumnIndex(paramString));
  }

  public Time getTime(String paramString)
    throws SQLException
  {
    return getTime(getColumnIndex(paramString));
  }

  public Timestamp getTimestamp(String paramString)
    throws SQLException
  {
    return getTimestamp(getColumnIndex(paramString));
  }

  public Time getTime(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getTime(getColumnIndex(paramString), paramCalendar);
  }

  public java.sql.Date getDate(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getDate(getColumnIndex(paramString), paramCalendar);
  }

  public InputStream getAsciiStream(String paramString)
    throws SQLException
  {
    return getAsciiStream(getColumnIndex(paramString));
  }

  public InputStream getUnicodeStream(String paramString)
    throws SQLException
  {
    return getUnicodeStream(getColumnIndex(paramString));
  }

  public String getString(String paramString)
    throws SQLException
  {
    return getString(getColumnIndex(paramString));
  }

  public InputStream getBinaryStream(String paramString)
    throws SQLException
  {
    return getBinaryStream(getColumnIndex(paramString));
  }

  public Reader getCharacterStream(String paramString)
    throws SQLException
  {
    return getCharacterStream(getColumnIndex(paramString));
  }

  public BigDecimal getBigDecimal(String paramString)
    throws SQLException
  {
    return getBigDecimal(getColumnIndex(paramString));
  }

  public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return getTimestamp(getColumnIndex(paramString), paramCalendar);
  }

  public Object getObject(String paramString, Map paramMap)
    throws SQLException
  {
    return getObject(getColumnIndex(paramString), paramMap);
  }

  public Ref getRef(String paramString)
    throws SQLException
  {
    return getRef(getColumnIndex(paramString));
  }

  public Blob getBlob(String paramString)
    throws SQLException
  {
    return getBlob(getColumnIndex(paramString));
  }

  public Clob getClob(String paramString)
    throws SQLException
  {
    return getClob(getColumnIndex(paramString));
  }

  public Array getArray(String paramString)
    throws SQLException
  {
    return getArray(getColumnIndex(paramString));
  }

  public synchronized void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    checkColumnIndex(paramInt);
    if (this.insertRowFlag)
    {
      checkAndFilterObject(paramInt, paramObject);
      this.insertRow.updateObject(paramInt, paramObject);
    }
    else if ((!isBeforeFirst()) && (!isAfterLast()))
    {
      this.updateRowFlag = true;
      this.updateRowPosition = this.presentRow;
      getCurrentRow().updateObject(paramInt, paramObject);
    } else {
      throw new SQLException("Updation not allowed on this column");
    }
  }

  public void updateNull(int paramInt)
    throws SQLException
  {
    updateObject(paramInt, null);
  }

  public synchronized void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    checkColumnIndex(paramInt1);
    try
    {
      char[] arrayOfChar = new char[paramInt2];
      int i = 0;
      do
      {
        i += paramReader.read(arrayOfChar, i, paramInt2 - i);
      }
      while (i < paramInt2);
      updateObject(paramInt1, new CharArrayReader(arrayOfChar));
      arrayOfChar = null;
    }
    catch (IOException localIOException) {
      throw new SQLException("Error while reading the Stream\n" + localIOException.getMessage());
    }
  }

  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    updateCharacterStream(getColumnIndex(paramString), paramReader, paramInt);
  }

  public void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    updateTimestamp(getColumnIndex(paramString), paramTimestamp);
  }

  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateBinaryStream(getColumnIndex(paramString), paramInputStream, paramInt);
  }

  public synchronized void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    try
    {
      byte[] arrayOfByte = new byte[paramInt2];
      int i = 0;
      do
      {
        i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
      }
      while (i < paramInt2);
      updateObject(paramInt1, new ByteArrayInputStream(arrayOfByte));
      arrayOfByte = null;
    }
    catch (IOException localIOException) {
      throw new SQLException("Error while reading the Stream\n" + localIOException.getMessage());
    }
  }

  public synchronized void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    try
    {
      InputStreamReader localInputStreamReader = new InputStreamReader(paramInputStream);
      char[] arrayOfChar = new char[paramInt2];
      int i = 0;
      do
      {
        i += localInputStreamReader.read(arrayOfChar, i, paramInt2 - i);
      }
      while (i < paramInt2);
      updateObject(paramInt1, new CharArrayReader(arrayOfChar));
      arrayOfChar = null;
    }
    catch (IOException localIOException) {
      throw new SQLException("Error while reading the Stream\n" + localIOException.getMessage());
    }
  }

  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    updateObject(paramInt, paramTimestamp);
  }

  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    updateObject(paramInt, new Boolean(paramBoolean));
  }

  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    updateObject(paramInt, new Byte(paramByte));
  }

  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    updateObject(paramInt, new Short(paramShort));
  }

  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    updateObject(paramInt1, new Integer(paramInt2));
  }

  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    updateObject(paramInt, new Long(paramLong));
  }

  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    updateObject(paramInt, new Float(paramFloat));
  }

  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    updateObject(paramInt, new Double(paramDouble));
  }

  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    updateObject(paramInt, paramBigDecimal);
  }

  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    updateObject(paramInt, paramString);
  }

  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    updateObject(paramInt, paramArrayOfByte);
  }

  public void updateDate(int paramInt, java.sql.Date paramDate)
    throws SQLException
  {
    updateObject(paramInt, paramDate);
  }

  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    updateObject(paramInt, new Timestamp(0, 0, 0, paramTime.getHours(), paramTime.getMinutes(), paramTime.getSeconds(), 0));
  }

  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    if (!(paramObject instanceof Number))
      throw new SQLException("Passed object is not Numeric type");
    updateObject(paramInt1, new BigDecimal(new BigInteger(((Number)paramObject).toString()), paramInt2));
  }

  public void updateNull(String paramString)
    throws SQLException
  {
    updateNull(getColumnIndex(paramString));
  }

  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    updateAsciiStream(getColumnIndex(paramString), paramInputStream, paramInt);
  }

  public void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    updateBoolean(getColumnIndex(paramString), paramBoolean);
  }

  public void updateByte(String paramString, byte paramByte)
    throws SQLException
  {
    updateByte(getColumnIndex(paramString), paramByte);
  }

  public void updateShort(String paramString, short paramShort)
    throws SQLException
  {
    updateShort(getColumnIndex(paramString), paramShort);
  }

  public void updateInt(String paramString, int paramInt)
    throws SQLException
  {
    updateInt(getColumnIndex(paramString), paramInt);
  }

  public void updateLong(String paramString, long paramLong)
    throws SQLException
  {
    updateLong(getColumnIndex(paramString), paramLong);
  }

  public void updateFloat(String paramString, float paramFloat)
    throws SQLException
  {
    updateFloat(getColumnIndex(paramString), paramFloat);
  }

  public void updateDouble(String paramString, double paramDouble)
    throws SQLException
  {
    updateDouble(getColumnIndex(paramString), paramDouble);
  }

  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    updateBigDecimal(getColumnIndex(paramString), paramBigDecimal);
  }

  public void updateString(String paramString1, String paramString2)
    throws SQLException
  {
    updateString(getColumnIndex(paramString1), paramString2);
  }

  public void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    updateBytes(getColumnIndex(paramString), paramArrayOfByte);
  }

  public void updateDate(String paramString, java.sql.Date paramDate)
    throws SQLException
  {
    updateDate(getColumnIndex(paramString), paramDate);
  }

  public void updateTime(String paramString, Time paramTime)
    throws SQLException
  {
    updateTime(getColumnIndex(paramString), paramTime);
  }

  public void updateObject(String paramString, Object paramObject)
    throws SQLException
  {
    updateObject(getColumnIndex(paramString), paramObject);
  }

  public void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    updateObject(getColumnIndex(paramString), paramObject, paramInt);
  }

  public URL getURL(int paramInt)
    throws SQLException
  {
    Object localObject = getObject(paramInt);

    if (localObject == null) {
      return (URL)localObject;
    }
    if ((localObject instanceof URL)) {
      return (URL)localObject;
    }
    throw new SQLException("Invalid column type");
  }

  public URL getURL(String paramString)
    throws SQLException
  {
    return getURL(getColumnIndex(paramString));
  }

  public void updateRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    updateObject(paramInt, paramRef);
  }

  public void updateRef(String paramString, Ref paramRef)
    throws SQLException
  {
    updateRef(getColumnIndex(paramString), paramRef);
  }

  public void updateBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    updateObject(paramInt, paramBlob);
  }

  public void updateBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    updateBlob(getColumnIndex(paramString), paramBlob);
  }

  public void updateClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    updateObject(paramInt, paramClob);
  }

  public void updateClob(String paramString, Clob paramClob)
    throws SQLException
  {
    updateClob(getColumnIndex(paramString), paramClob);
  }

  public void updateArray(int paramInt, Array paramArray)
    throws SQLException
  {
    updateObject(paramInt, paramArray);
  }

  public void updateArray(String paramString, Array paramArray)
    throws SQLException
  {
    updateArray(getColumnIndex(paramString), paramArray);
  }

  public int[] getKeyColumns()
    throws SQLException
  {
    return this.keyColumns;
  }

  public void setKeyColumns(int[] paramArrayOfInt)
    throws SQLException
  {
    int i = 0;

    if (this.rowsetMetaData != null)
    {
      i = this.rowsetMetaData.getColumnCount();
      if ((paramArrayOfInt == null) || (paramArrayOfInt.length > i))
      {
        throw new SQLException("Invalid number of key columns");
      }
    }

    int j = paramArrayOfInt.length;
    this.keyColumns = new int[j];

    for (int k = 0; k < j; k++)
    {
      if ((paramArrayOfInt[k] <= 0) || (paramArrayOfInt[k] > i))
      {
        throw new SQLException("Invalid column index: " + paramArrayOfInt[k]);
      }

      this.keyColumns[k] = paramArrayOfInt[k];
    }
  }

  public int getPageSize()
  {
    return this.pageSize;
  }

  public void setPageSize(int paramInt)
    throws SQLException
  {
    if ((paramInt < 0) || ((this.maxRows > 0) && (paramInt > this.maxRows)))
    {
      throw new SQLException("Invalid page size specified");
    }

    this.pageSize = paramInt;
  }

  public SyncProvider getSyncProvider()
    throws SQLException
  {
    return this.syncProvider;
  }

  public void setSyncProvider(String paramString)
    throws SQLException
  {
    this.syncProvider = SyncFactory.getInstance(paramString);
    this.reader = this.syncProvider.getRowSetReader();
    this.writer = this.syncProvider.getRowSetWriter();
  }

  public String getTableName()
    throws SQLException
  {
    return this.tableName;
  }

  public void setTableName(String paramString)
    throws SQLException
  {
    this.tableName = paramString;
  }

  public CachedRowSet createCopyNoConstraints()
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = (OracleCachedRowSet)createCopy();

    localOracleCachedRowSet.initializeProperties();

    localOracleCachedRowSet.listener = new Vector();
    try
    {
      localOracleCachedRowSet.unsetMatchColumn(localOracleCachedRowSet.getMatchColumnIndexes());
      localOracleCachedRowSet.unsetMatchColumn(localOracleCachedRowSet.getMatchColumnNames());
    }
    catch (SQLException localSQLException)
    {
    }

    return localOracleCachedRowSet;
  }

  public CachedRowSet createCopySchema()
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = (OracleCachedRowSet)createCopy();

    localOracleCachedRowSet.rows = null;
    localOracleCachedRowSet.rowCount = 0;
    localOracleCachedRowSet.currentPage = 0;

    return localOracleCachedRowSet;
  }

  public boolean columnUpdated(int paramInt)
    throws SQLException
  {
    if (this.insertRowFlag)
    {
      throw new SQLException("Trying to mark an inserted row as original");
    }

    return getCurrentRow().isColumnChanged(paramInt);
  }

  public boolean columnUpdated(String paramString)
    throws SQLException
  {
    return columnUpdated(getColumnIndex(paramString));
  }

  public synchronized void execute(Connection paramConnection)
    throws SQLException
  {
    this.connection = paramConnection;
    execute();
  }

  public void commit()
    throws SQLException
  {
    getConnectionInternal().commit();
  }

  public void rollback()
    throws SQLException
  {
    getConnectionInternal().rollback();
  }

  public void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    Connection localConnection = getConnectionInternal();
    boolean bool = localConnection.getAutoCommit();
    try
    {
      localConnection.setAutoCommit(false);
      localConnection.rollback(paramSavepoint);
    }
    finally {
      localConnection.setAutoCommit(bool);
    }
  }

  public void oracleRollback(OracleSavepoint paramOracleSavepoint)
    throws SQLException
  {
    ((OracleConnection)getConnectionInternal()).oracleRollback(paramOracleSavepoint);
  }

  public void setOriginalRow()
    throws SQLException
  {
    if (this.insertRowFlag)
    {
      throw new SQLException("Invalid operation on this row before insertRow is called");
    }

    setOriginalRowInternal(this.presentRow);
  }

  public int size()
  {
    return this.rowCount;
  }

  public boolean nextPage()
    throws SQLException
  {
    if ((this.fetchDirection == 1001) && (this.resultSet != null) && (this.resultSet.getType() == 1003))
    {
      throw new SQLException("The underlying ResultSet does not support this operation");
    }
    if ((this.rows.size() == 0) && (!this.isPopulateDone)) {
      throw new SQLException("This operation can not be called without previous paging operations");
    }

    populate(this.resultSet);
    this.currentPage += 1;

    return !this.isPopulateDone;
  }

  public boolean previousPage()
    throws SQLException
  {
    if ((this.resultSet != null) && (this.resultSet.getType() == 1003)) {
      throw new SQLException("The underlying ResultSet does not support this operation");
    }

    if ((this.rows.size() == 0) && (!this.isPopulateDone)) {
      throw new SQLException("This operation can not be called without previous paging operations");
    }

    if (this.fetchDirection == 1001)
    {
      this.resultSet.relative(this.pageSize * 2);
    }
    else
    {
      this.resultSet.relative(-2 * this.pageSize);
    }

    populate(this.resultSet);

    if (this.currentPage > 0)
    {
      this.currentPage -= 1;
    }

    return this.currentPage != 0;
  }

  public void rowSetPopulated(RowSetEvent paramRowSetEvent, int paramInt)
    throws SQLException
  {
    if ((paramInt <= 0) || (paramInt < this.fetchSize))
    {
      throw new SQLException("Invalid number of row parameter specified");
    }

    if (this.rowCount % paramInt == 0)
    {
      this.rowsetEvent = new RowSetEvent(this);
      notifyRowSetChanged();
    }
  }

  public RowSetWarning getRowSetWarnings()
    throws SQLException
  {
    return this.rowsetWarning;
  }

  public void populate(ResultSet paramResultSet, int paramInt)
    throws SQLException
  {
    if (paramInt < 0) {
      throw new SQLException("The start position should not be negative");
    }
    if (paramResultSet == null) {
      throw new SQLException("Null ResultSet supplied to populate");
    }
    int i = paramResultSet.getType();

    if (i == 1003)
    {
      int j = 0;
      while ((paramResultSet.next()) && (j < paramInt))
      {
        j++;
      }

      if (j < paramInt)
        throw new SQLException("Too few rows to start populating at this position");
    }
    else
    {
      paramResultSet.absolute(paramInt);
    }

    populate(paramResultSet);
  }

  public void undoDelete()
    throws SQLException
  {
    cancelRowDelete();
  }

  public void undoInsert()
    throws SQLException
  {
    cancelRowInsert();
  }

  public void undoUpdate()
    throws SQLException
  {
    cancelRowUpdates();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleCachedRowSet
 * JD-Core Version:    0.6.0
 */