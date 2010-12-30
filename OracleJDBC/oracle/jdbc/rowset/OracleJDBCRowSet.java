package oracle.jdbc.rowset;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.RowSetWarning;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleResultSet;
import oracle.jdbc.OracleSavepoint;
import oracle.jdbc.driver.OracleDriver;

public class OracleJDBCRowSet extends OracleRowSet
  implements RowSet, JdbcRowSet
{
  private Connection connection;
  private static boolean driverManagerInitialized;
  private PreparedStatement preparedStatement;
  private ResultSet resultSet;

  public OracleJDBCRowSet()
    throws SQLException
  {
    driverManagerInitialized = false;
  }

  public OracleJDBCRowSet(Connection paramConnection)
    throws SQLException
  {
    this();
    this.connection = paramConnection;
  }

  public void execute()
    throws SQLException
  {
    this.connection = getConnection(this);

    this.connection.setTransactionIsolation(getTransactionIsolation());
    this.connection.setTypeMap(getTypeMap());

    if (this.preparedStatement == null) {
      this.preparedStatement = this.connection.prepareStatement(getCommand(), getType(), getConcurrency());
    }
    this.preparedStatement.setFetchSize(getFetchSize());
    this.preparedStatement.setFetchDirection(getFetchDirection());
    this.preparedStatement.setMaxFieldSize(getMaxFieldSize());
    this.preparedStatement.setMaxRows(getMaxRows());
    this.preparedStatement.setQueryTimeout(getQueryTimeout());
    this.preparedStatement.setEscapeProcessing(getEscapeProcessing());

    this.resultSet = this.preparedStatement.executeQuery();
    notifyRowSetChanged();
  }

  public void close()
    throws SQLException
  {
    if (this.resultSet != null) {
      this.resultSet.close();
    }
    if (this.preparedStatement != null) {
      this.preparedStatement.close();
    }
    if ((this.connection != null) && (!this.connection.isClosed()))
    {
      this.connection.commit();
      this.connection.close();
    }
    notifyRowSetChanged();
  }

  private Connection getConnection(RowSet paramRowSet)
    throws SQLException
  {
    Connection localConnection = null;

    if ((this.connection != null) && (!this.connection.isClosed()))
    {
      localConnection = this.connection;
    }
    else
    {
      Object localObject;
      if (paramRowSet.getDataSourceName() != null)
      {
        try
        {
          InitialContext localInitialContext = new InitialContext();
          localObject = (DataSource)localInitialContext.lookup(paramRowSet.getDataSourceName());

          if ((paramRowSet.getUsername() == null) || (paramRowSet.getPassword() == null))
          {
            localConnection = ((DataSource)localObject).getConnection();
          }
          else
          {
            localConnection = ((DataSource)localObject).getConnection(paramRowSet.getUsername(), paramRowSet.getPassword());
          }

        }
        catch (NamingException localNamingException)
        {
          throw new SQLException("Unable to connect through the DataSource\n" + localNamingException.getMessage());
        }

      }
      else if (paramRowSet.getUrl() != null)
      {
        if (!driverManagerInitialized)
        {
          DriverManager.registerDriver(new OracleDriver());
          driverManagerInitialized = true;
        }
        String str1 = paramRowSet.getUrl();
        localObject = paramRowSet.getUsername();
        String str2 = paramRowSet.getPassword();

        if ((str1.equals("")) || (((String)localObject).equals("")) || (str2.equals("")))
        {
          throw new SQLException("One or more of the authenticating parameter not set");
        }

        localConnection = DriverManager.getConnection(str1, (String)localObject, str2);
      }
    }
    return (Connection)localConnection;
  }

  public boolean wasNull()
    throws SQLException
  {
    return this.resultSet.wasNull();
  }

  public SQLWarning getWarnings()
    throws SQLException
  {
    return this.resultSet.getWarnings();
  }

  public void clearWarnings()
    throws SQLException
  {
    this.resultSet.clearWarnings();
  }

  public String getCursorName()
    throws SQLException
  {
    return this.resultSet.getCursorName();
  }

  public ResultSetMetaData getMetaData()
    throws SQLException
  {
    return new OracleRowSetMetaData(this.resultSet.getMetaData());
  }

  public int findColumn(String paramString)
    throws SQLException
  {
    return this.resultSet.findColumn(paramString);
  }

  public void clearParameters()
    throws SQLException
  {
    this.preparedStatement.clearParameters();
  }

  public Statement getStatement()
    throws SQLException
  {
    return this.resultSet.getStatement();
  }

  public void setCommand(String paramString)
    throws SQLException
  {
    super.setCommand(paramString);

    if ((this.connection == null) || (this.connection.isClosed())) {
      this.connection = getConnection(this);
    }
    if (this.preparedStatement != null)
    {
      try
      {
        this.preparedStatement.close();
        this.preparedStatement = null;
      } catch (SQLException localSQLException) {
      }
    }
    this.preparedStatement = this.connection.prepareStatement(paramString, getType(), getConcurrency());
  }

  public void setReadOnly(boolean paramBoolean)
    throws SQLException
  {
    super.setReadOnly(paramBoolean);

    if (this.connection != null)
    {
      this.connection.setReadOnly(paramBoolean);
    }
    else
    {
      throw new SQLException("Connection not open");
    }
  }

  public void setFetchDirection(int paramInt)
    throws SQLException
  {
    super.setFetchDirection(paramInt);

    this.resultSet.setFetchDirection(this.fetchDirection);
  }

  public void setShowDeleted(boolean paramBoolean)
    throws SQLException
  {
    if (paramBoolean)
    {
      throw new SQLException("This JdbcRowSet implementation does not allow deleted rows to be visible");
    }

    super.setShowDeleted(paramBoolean);
  }

  public boolean next()
    throws SQLException
  {
    boolean bool = this.resultSet.next();

    if (bool)
      notifyCursorMoved();
    return bool;
  }

  public boolean previous()
    throws SQLException
  {
    boolean bool = this.resultSet.previous();

    if (bool)
      notifyCursorMoved();
    return bool;
  }

  public void beforeFirst()
    throws SQLException
  {
    if (!isBeforeFirst())
    {
      this.resultSet.beforeFirst();
      notifyCursorMoved();
    }
  }

  public void afterLast()
    throws SQLException
  {
    if (!isAfterLast())
    {
      this.resultSet.afterLast();
      notifyCursorMoved();
    }
  }

  public boolean first()
    throws SQLException
  {
    boolean bool = this.resultSet.first();

    if (bool)
      notifyCursorMoved();
    return bool;
  }

  public boolean last()
    throws SQLException
  {
    boolean bool = this.resultSet.last();

    if (bool)
      notifyCursorMoved();
    return bool;
  }

  public boolean absolute(int paramInt)
    throws SQLException
  {
    boolean bool = this.resultSet.absolute(paramInt);

    if (bool)
      notifyCursorMoved();
    return bool;
  }

  public boolean relative(int paramInt)
    throws SQLException
  {
    boolean bool = this.resultSet.relative(paramInt);

    if (bool)
      notifyCursorMoved();
    return bool;
  }

  public boolean isBeforeFirst()
    throws SQLException
  {
    return this.resultSet.isBeforeFirst();
  }

  public boolean isAfterLast()
    throws SQLException
  {
    return this.resultSet.isAfterLast();
  }

  public boolean isFirst()
    throws SQLException
  {
    return this.resultSet.isFirst();
  }

  public boolean isLast()
    throws SQLException
  {
    return this.resultSet.isLast();
  }

  public void insertRow()
    throws SQLException
  {
    this.resultSet.insertRow();
    notifyRowChanged();
  }

  public void updateRow()
    throws SQLException
  {
    this.resultSet.updateRow();
    notifyRowChanged();
  }

  public void deleteRow()
    throws SQLException
  {
    this.resultSet.deleteRow();
    notifyRowChanged();
  }

  public void refreshRow()
    throws SQLException
  {
    this.resultSet.refreshRow();
  }

  public void cancelRowUpdates()
    throws SQLException
  {
    this.resultSet.cancelRowUpdates();
    notifyRowChanged();
  }

  public void moveToInsertRow()
    throws SQLException
  {
    this.resultSet.moveToInsertRow();
  }

  public void moveToCurrentRow()
    throws SQLException
  {
    this.resultSet.moveToCurrentRow();
  }

  public int getRow()
    throws SQLException
  {
    return this.resultSet.getRow();
  }

  public boolean rowUpdated()
    throws SQLException
  {
    return this.resultSet.rowUpdated();
  }

  public boolean rowInserted()
    throws SQLException
  {
    return this.resultSet.rowInserted();
  }

  public boolean rowDeleted()
    throws SQLException
  {
    return this.resultSet.rowDeleted();
  }

  public void setNull(int paramInt1, int paramInt2)
    throws SQLException
  {
    this.preparedStatement.setNull(paramInt1, paramInt2);
  }

  public void setNull(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    this.preparedStatement.setNull(paramInt1, paramInt2, paramString);
  }

  public void setBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    this.preparedStatement.setBoolean(paramInt, paramBoolean);
  }

  public void setByte(int paramInt, byte paramByte)
    throws SQLException
  {
    this.preparedStatement.setByte(paramInt, paramByte);
  }

  public void setShort(int paramInt, short paramShort)
    throws SQLException
  {
    this.preparedStatement.setShort(paramInt, paramShort);
  }

  public void setInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    this.preparedStatement.setInt(paramInt1, paramInt2);
  }

  public void setLong(int paramInt, long paramLong)
    throws SQLException
  {
    this.preparedStatement.setLong(paramInt, paramLong);
  }

  public void setFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    this.preparedStatement.setFloat(paramInt, paramFloat);
  }

  public void setDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    this.preparedStatement.setDouble(paramInt, paramDouble);
  }

  public void setBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    this.preparedStatement.setBigDecimal(paramInt, paramBigDecimal);
  }

  public void setString(int paramInt, String paramString)
    throws SQLException
  {
    this.preparedStatement.setString(paramInt, paramString);
  }

  public void setBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    this.preparedStatement.setBytes(paramInt, paramArrayOfByte);
  }

  public void setDate(int paramInt, Date paramDate)
    throws SQLException
  {
    this.preparedStatement.setDate(paramInt, paramDate);
  }

  public void setTime(int paramInt, Time paramTime)
    throws SQLException
  {
    this.preparedStatement.setTime(paramInt, paramTime);
  }

  public void setObject(int paramInt, Object paramObject)
    throws SQLException
  {
    this.preparedStatement.setObject(paramInt, paramObject);
  }

  public void setRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    this.preparedStatement.setRef(paramInt, paramRef);
  }

  public void setBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    this.preparedStatement.setBlob(paramInt, paramBlob);
  }

  public void setClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    this.preparedStatement.setClob(paramInt, paramClob);
  }

  public void setArray(int paramInt, Array paramArray)
    throws SQLException
  {
    this.preparedStatement.setArray(paramInt, paramArray);
  }

  public void setBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.preparedStatement.setBinaryStream(paramInt1, paramInputStream, paramInt2);
  }

  public void setTime(int paramInt, Time paramTime, Calendar paramCalendar)
    throws SQLException
  {
    this.preparedStatement.setTime(paramInt, paramTime, paramCalendar);
  }

  public void setTimestamp(int paramInt, Timestamp paramTimestamp, Calendar paramCalendar)
    throws SQLException
  {
    this.preparedStatement.setTimestamp(paramInt, paramTimestamp, paramCalendar);
  }

  public void setTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    this.preparedStatement.setTimestamp(paramInt, paramTimestamp);
  }

  public void setAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.preparedStatement.setAsciiStream(paramInt1, paramInputStream, paramInt2);
  }

  public void setCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    this.preparedStatement.setCharacterStream(paramInt1, paramReader, paramInt2);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2, int paramInt3)
    throws SQLException
  {
    this.preparedStatement.setObject(paramInt1, paramObject, paramInt2, paramInt3);
  }

  public void setObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    this.preparedStatement.setObject(paramInt1, paramObject, paramInt2);
  }

  public void setDate(int paramInt, Date paramDate, Calendar paramCalendar)
    throws SQLException
  {
    this.preparedStatement.setDate(paramInt, paramDate, paramCalendar);
  }

  public Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return this.resultSet.getObject(paramInt, paramMap);
  }

  public BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    return this.resultSet.getBigDecimal(paramInt);
  }

  public Ref getRef(int paramInt)
    throws SQLException
  {
    return this.resultSet.getRef(paramInt);
  }

  public Blob getBlob(int paramInt)
    throws SQLException
  {
    return this.resultSet.getBlob(paramInt);
  }

  public Clob getClob(int paramInt)
    throws SQLException
  {
    return this.resultSet.getClob(paramInt);
  }

  public Array getArray(int paramInt)
    throws SQLException
  {
    return this.resultSet.getArray(paramInt);
  }

  public Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return this.resultSet.getDate(paramInt, paramCalendar);
  }

  public Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    return this.resultSet.getCharacterStream(paramInt);
  }

  public Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return this.resultSet.getTime(paramInt, paramCalendar);
  }

  public InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    return this.resultSet.getBinaryStream(paramInt);
  }

  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    return this.resultSet.getTimestamp(paramInt, paramCalendar);
  }

  public String getString(int paramInt)
    throws SQLException
  {
    return this.resultSet.getString(paramInt);
  }

  public boolean getBoolean(int paramInt)
    throws SQLException
  {
    return this.resultSet.getBoolean(paramInt);
  }

  public byte getByte(int paramInt)
    throws SQLException
  {
    return this.resultSet.getByte(paramInt);
  }

  public short getShort(int paramInt)
    throws SQLException
  {
    return this.resultSet.getShort(paramInt);
  }

  public long getLong(int paramInt)
    throws SQLException
  {
    return this.resultSet.getLong(paramInt);
  }

  public float getFloat(int paramInt)
    throws SQLException
  {
    return this.resultSet.getFloat(paramInt);
  }

  public double getDouble(int paramInt)
    throws SQLException
  {
    return this.resultSet.getDouble(paramInt);
  }

  public BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    return this.resultSet.getBigDecimal(paramInt1, paramInt2);
  }

  public byte[] getBytes(int paramInt)
    throws SQLException
  {
    return this.resultSet.getBytes(paramInt);
  }

  public Date getDate(int paramInt)
    throws SQLException
  {
    return this.resultSet.getDate(paramInt);
  }

  public Time getTime(int paramInt)
    throws SQLException
  {
    return this.resultSet.getTime(paramInt);
  }

  public Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    return this.resultSet.getTimestamp(paramInt);
  }

  public InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    return this.resultSet.getAsciiStream(paramInt);
  }

  public InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    return this.resultSet.getUnicodeStream(paramInt);
  }

  public int getInt(int paramInt)
    throws SQLException
  {
    return this.resultSet.getInt(paramInt);
  }

  public Object getObject(int paramInt)
    throws SQLException
  {
    return this.resultSet.getObject(paramInt);
  }

  public int getInt(String paramString)
    throws SQLException
  {
    return this.resultSet.getInt(paramString);
  }

  public long getLong(String paramString)
    throws SQLException
  {
    return this.resultSet.getLong(paramString);
  }

  public float getFloat(String paramString)
    throws SQLException
  {
    return this.resultSet.getFloat(paramString);
  }

  public double getDouble(String paramString)
    throws SQLException
  {
    return this.resultSet.getDouble(paramString);
  }

  public BigDecimal getBigDecimal(String paramString, int paramInt)
    throws SQLException
  {
    return this.resultSet.getBigDecimal(paramString, paramInt);
  }

  public byte[] getBytes(String paramString)
    throws SQLException
  {
    return this.resultSet.getBytes(paramString);
  }

  public Date getDate(String paramString)
    throws SQLException
  {
    return this.resultSet.getDate(paramString);
  }

  public Time getTime(String paramString)
    throws SQLException
  {
    return this.resultSet.getTime(paramString);
  }

  public Timestamp getTimestamp(String paramString)
    throws SQLException
  {
    return this.resultSet.getTimestamp(paramString);
  }

  public InputStream getAsciiStream(String paramString)
    throws SQLException
  {
    return this.resultSet.getAsciiStream(paramString);
  }

  public InputStream getUnicodeStream(String paramString)
    throws SQLException
  {
    return this.resultSet.getUnicodeStream(paramString);
  }

  public Object getObject(String paramString)
    throws SQLException
  {
    return this.resultSet.getObject(paramString);
  }

  public Reader getCharacterStream(String paramString)
    throws SQLException
  {
    return this.resultSet.getCharacterStream(paramString);
  }

  public Object getObject(String paramString, Map paramMap)
    throws SQLException
  {
    return this.resultSet.getObject(paramString, paramMap);
  }

  public Ref getRef(String paramString)
    throws SQLException
  {
    return this.resultSet.getRef(paramString);
  }

  public Blob getBlob(String paramString)
    throws SQLException
  {
    return this.resultSet.getBlob(paramString);
  }

  public Clob getClob(String paramString)
    throws SQLException
  {
    return this.resultSet.getClob(paramString);
  }

  public Array getArray(String paramString)
    throws SQLException
  {
    return this.resultSet.getArray(paramString);
  }

  public BigDecimal getBigDecimal(String paramString)
    throws SQLException
  {
    return this.resultSet.getBigDecimal(paramString);
  }

  public Date getDate(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return this.resultSet.getDate(paramString, paramCalendar);
  }

  public Time getTime(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return this.resultSet.getTime(paramString, paramCalendar);
  }

  public InputStream getBinaryStream(String paramString)
    throws SQLException
  {
    return this.resultSet.getBinaryStream(paramString);
  }

  public Timestamp getTimestamp(String paramString, Calendar paramCalendar)
    throws SQLException
  {
    return this.resultSet.getTimestamp(paramString, paramCalendar);
  }

  public String getString(String paramString)
    throws SQLException
  {
    return this.resultSet.getString(paramString);
  }

  public boolean getBoolean(String paramString)
    throws SQLException
  {
    return this.resultSet.getBoolean(paramString);
  }

  public byte getByte(String paramString)
    throws SQLException
  {
    return this.resultSet.getByte(paramString);
  }

  public short getShort(String paramString)
    throws SQLException
  {
    return this.resultSet.getShort(paramString);
  }

  public void updateNull(int paramInt)
    throws SQLException
  {
    this.resultSet.updateNull(paramInt);
  }

  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    this.resultSet.updateCharacterStream(paramInt1, paramReader, paramInt2);
  }

  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    this.resultSet.updateTimestamp(paramInt, paramTimestamp);
  }

  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.resultSet.updateBinaryStream(paramInt1, paramInputStream, paramInt2);
  }

  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    this.resultSet.updateAsciiStream(paramInt1, paramInputStream, paramInt2);
  }

  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    this.resultSet.updateBoolean(paramInt, paramBoolean);
  }

  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    this.resultSet.updateByte(paramInt, paramByte);
  }

  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    this.resultSet.updateShort(paramInt, paramShort);
  }

  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    this.resultSet.updateInt(paramInt1, paramInt2);
  }

  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    this.resultSet.updateLong(paramInt, paramLong);
  }

  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    this.resultSet.updateFloat(paramInt, paramFloat);
  }

  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    this.resultSet.updateDouble(paramInt, paramDouble);
  }

  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    this.resultSet.updateBigDecimal(paramInt, paramBigDecimal);
  }

  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    this.resultSet.updateString(paramInt, paramString);
  }

  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    this.resultSet.updateBytes(paramInt, paramArrayOfByte);
  }

  public void updateDate(int paramInt, Date paramDate)
    throws SQLException
  {
    this.resultSet.updateDate(paramInt, paramDate);
  }

  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    this.resultSet.updateTime(paramInt, paramTime);
  }

  public void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    this.resultSet.updateObject(paramInt, paramObject);
  }

  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    this.resultSet.updateObject(paramInt1, paramObject, paramInt2);
  }

  public void updateNull(String paramString)
    throws SQLException
  {
    this.resultSet.updateNull(paramString);
  }

  public void updateBoolean(String paramString, boolean paramBoolean)
    throws SQLException
  {
    this.resultSet.updateBoolean(paramString, paramBoolean);
  }

  public void updateByte(String paramString, byte paramByte)
    throws SQLException
  {
    this.resultSet.updateByte(paramString, paramByte);
  }

  public void updateShort(String paramString, short paramShort)
    throws SQLException
  {
    this.resultSet.updateShort(paramString, paramShort);
  }

  public void updateInt(String paramString, int paramInt)
    throws SQLException
  {
    this.resultSet.updateInt(paramString, paramInt);
  }

  public void updateLong(String paramString, long paramLong)
    throws SQLException
  {
    this.resultSet.updateLong(paramString, paramLong);
  }

  public void updateFloat(String paramString, float paramFloat)
    throws SQLException
  {
    this.resultSet.updateFloat(paramString, paramFloat);
  }

  public void updateDouble(String paramString, double paramDouble)
    throws SQLException
  {
    this.resultSet.updateDouble(paramString, paramDouble);
  }

  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal)
    throws SQLException
  {
    this.resultSet.updateBigDecimal(paramString, paramBigDecimal);
  }

  public void updateString(String paramString1, String paramString2)
    throws SQLException
  {
    this.resultSet.updateString(paramString1, paramString2);
  }

  public void updateBytes(String paramString, byte[] paramArrayOfByte)
    throws SQLException
  {
    this.resultSet.updateBytes(paramString, paramArrayOfByte);
  }

  public void updateDate(String paramString, Date paramDate)
    throws SQLException
  {
    this.resultSet.updateDate(paramString, paramDate);
  }

  public void updateTime(String paramString, Time paramTime)
    throws SQLException
  {
    this.resultSet.updateTime(paramString, paramTime);
  }

  public void updateObject(String paramString, Object paramObject)
    throws SQLException
  {
    this.resultSet.updateObject(paramString, paramObject);
  }

  public void updateObject(String paramString, Object paramObject, int paramInt)
    throws SQLException
  {
    this.resultSet.updateObject(paramString, paramObject, paramInt);
  }

  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    this.resultSet.updateBinaryStream(paramString, paramInputStream, paramInt);
  }

  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    this.resultSet.updateAsciiStream(paramString, paramInputStream, paramInt);
  }

  public void updateTimestamp(String paramString, Timestamp paramTimestamp)
    throws SQLException
  {
    this.resultSet.updateTimestamp(paramString, paramTimestamp);
  }

  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt)
    throws SQLException
  {
    this.resultSet.updateCharacterStream(paramString, paramReader, paramInt);
  }

  public URL getURL(int paramInt)
    throws SQLException
  {
    return ((OracleResultSet)this.resultSet).getURL(paramInt);
  }

  public URL getURL(String paramString)
    throws SQLException
  {
    return ((OracleResultSet)this.resultSet).getURL(paramString);
  }

  public void updateRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateRef(paramInt, paramRef);
  }

  public void updateRef(String paramString, Ref paramRef)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateRef(paramString, paramRef);
  }

  public void updateBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateBlob(paramInt, paramBlob);
  }

  public void updateBlob(String paramString, Blob paramBlob)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateBlob(paramString, paramBlob);
  }

  public void updateClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateClob(paramInt, paramClob);
  }

  public void updateClob(String paramString, Clob paramClob)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateClob(paramString, paramClob);
  }

  public void updateArray(int paramInt, Array paramArray)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateArray(paramInt, paramArray);
  }

  public void updateArray(String paramString, Array paramArray)
    throws SQLException
  {
    ((OracleResultSet)this.resultSet).updateArray(paramString, paramArray);
  }

  public void commit()
    throws SQLException
  {
    if (this.connection != null)
    {
      this.connection.commit();
    }
    else
    {
      throw new SQLException("Connection not open");
    }
  }

  public void rollback()
    throws SQLException
  {
    if (this.connection != null)
    {
      this.connection.rollback();
    }
    else
    {
      throw new SQLException("Connection not open");
    }
  }

  public void rollback(Savepoint paramSavepoint)
    throws SQLException
  {
    if (this.connection != null)
    {
      this.connection.rollback(paramSavepoint);
    }
    else
    {
      throw new SQLException("Connection not open");
    }
  }

  public void oracleRollback(OracleSavepoint paramOracleSavepoint)
    throws SQLException
  {
    if (this.connection != null)
    {
      ((OracleConnection)this.connection).oracleRollback(paramOracleSavepoint);
    }
    else
    {
      throw new SQLException("Connection not open");
    }
  }

  public boolean getAutoCommit()
    throws SQLException
  {
    if (this.connection != null)
    {
      return this.connection.getAutoCommit();
    }

    throw new SQLException("Connection not open");
  }

  public void setAutoCommit(boolean paramBoolean)
    throws SQLException
  {
    if (this.connection != null)
    {
      this.connection.setAutoCommit(paramBoolean);
    }
    else
    {
      throw new SQLException("Connection not open");
    }
  }

  public RowSetWarning getRowSetWarnings()
    throws SQLException
  {
    return null;
  }

  String getTableName()
    throws SQLException
  {
    return getMetaData().getTableName(getMatchColumnIndexes()[0]);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleJDBCRowSet
 * JD-Core Version:    0.6.0
 */