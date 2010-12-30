package oracle.jdbc.rowset;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetReader;
import oracle.jdbc.driver.OracleDriver;

public class OracleCachedRowSetReader
  implements RowSetReader, Serializable
{
  static final transient int UNICODESTREAM = 273;
  static final transient int BINARYSTREAM = 546;
  static final transient int ASCIISTREAM = 819;
  static final transient int TWO_PARAMETERS = 2;
  static final transient int THREE_PARAMETERS = 3;
  private static transient boolean driverManagerInitialized = false;

  Connection getConnection(RowSetInternal paramRowSetInternal)
    throws SQLException
  {
    Object localObject1 = null;

    Connection localConnection = paramRowSetInternal.getConnection();
    if ((localConnection != null) && (!localConnection.isClosed()))
    {
      localObject1 = localConnection;
    }
    else
    {
      Object localObject2;
      String str2;
      if (((RowSet)paramRowSetInternal).getDataSourceName() != null)
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
          localObject2 = (DataSource)localInitialContext.lookup(((RowSet)paramRowSetInternal).getDataSourceName());

          str2 = ((RowSet)paramRowSetInternal).getUsername();
          String str3 = ((RowSet)paramRowSetInternal).getPassword();
          if ((str2 == null) && (str3 == null))
          {
            localObject1 = ((DataSource)localObject2).getConnection();
          }
          else
          {
            localObject1 = ((DataSource)localObject2).getConnection(str2, str3);
          }
        }
        catch (NamingException localNamingException)
        {
          throw new SQLException("Unable to connect through the DataSource\n" + localNamingException.getMessage());
        }

      }
      else if (((RowSet)paramRowSetInternal).getUrl() != null)
      {
        if (!driverManagerInitialized)
        {
          DriverManager.registerDriver(new OracleDriver());
          driverManagerInitialized = true;
        }
        String str1 = ((RowSet)paramRowSetInternal).getUrl();
        localObject2 = ((RowSet)paramRowSetInternal).getUsername();
        str2 = ((RowSet)paramRowSetInternal).getPassword();

        if ((str1.equals("")) || (((String)localObject2).equals("")) || (str2.equals("")))
        {
          throw new SQLException("One or more of the authenticating parameter not set");
        }

        localObject1 = DriverManager.getConnection(str1, (String)localObject2, str2);
      }
    }
    return (Connection)(Connection)localObject1;
  }

  private void setParams(Object[] paramArrayOfObject, PreparedStatement paramPreparedStatement)
    throws SQLException
  {
    for (int i = 0; i < paramArrayOfObject.length; i++)
    {
      int j = 0;
      try
      {
        j = Array.getLength(paramArrayOfObject[i]);
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        paramPreparedStatement.setObject(i + 1, paramArrayOfObject[i]);
        continue;
      }

      Object[] arrayOfObject = (Object[])paramArrayOfObject[i];

      if (j == 2)
      {
        if (arrayOfObject[0] == null) {
          paramPreparedStatement.setNull(i + 1, ((Integer)arrayOfObject[1]).intValue());
        }
        else if (((arrayOfObject[0] instanceof Date)) || ((arrayOfObject[0] instanceof Time)) || ((arrayOfObject[0] instanceof Timestamp)))
        {
          if ((arrayOfObject[1] instanceof Calendar))
          {
            paramPreparedStatement.setDate(i + 1, (Date)arrayOfObject[0], (Calendar)arrayOfObject[1]);
          }
          else
          {
            throw new SQLException("Unable to deduce param type");
          }

        }
        else if ((arrayOfObject[0] instanceof Reader)) {
          paramPreparedStatement.setCharacterStream(i + 1, (Reader)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
        }
        else if ((arrayOfObject[1] instanceof Integer)) {
          paramPreparedStatement.setObject(i + 1, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
        }
      }
      else
      {
        if (j != 3)
          continue;
        if (arrayOfObject[0] == null)
        {
          paramPreparedStatement.setNull(i + 1, ((Integer)arrayOfObject[1]).intValue(), (String)arrayOfObject[2]);
        }
        else if ((arrayOfObject[0] instanceof InputStream)) {
          switch (((Integer)arrayOfObject[2]).intValue())
          {
          case 273:
            paramPreparedStatement.setUnicodeStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          case 546:
            paramPreparedStatement.setBinaryStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          case 819:
            paramPreparedStatement.setAsciiStream(i + 1, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          }

          throw new SQLException("Unable to deduce parameter type");
        }
        if (((arrayOfObject[1] instanceof Integer)) && ((arrayOfObject[2] instanceof Integer)))
        {
          paramPreparedStatement.setObject(i + 1, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue(), ((Integer)arrayOfObject[2]).intValue());
        }
        else
        {
          throw new SQLException("Unable to deduce param type");
        }
      }
    }
  }

  public synchronized void readData(RowSetInternal paramRowSetInternal)
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = (OracleCachedRowSet)paramRowSetInternal;

    Connection localConnection = getConnection(paramRowSetInternal);

    if ((localConnection == null) || (localOracleCachedRowSet.getCommand() == null))
      throw new SQLException("Sorry, Could not obtain connection");
    try
    {
      localConnection.setTransactionIsolation(localOracleCachedRowSet.getTransactionIsolation());
    }
    catch (Exception localException1) {
    }
    PreparedStatement localPreparedStatement = localConnection.prepareStatement(localOracleCachedRowSet.getCommand());

    setParams(paramRowSetInternal.getParams(), localPreparedStatement);
    try
    {
      localPreparedStatement.setMaxRows(localOracleCachedRowSet.getMaxRows());
      localPreparedStatement.setMaxFieldSize(localOracleCachedRowSet.getMaxFieldSize());
      localPreparedStatement.setEscapeProcessing(localOracleCachedRowSet.getEscapeProcessing());
      localPreparedStatement.setQueryTimeout(localOracleCachedRowSet.getQueryTimeout());
    } catch (Exception localException2) {
    }
    ResultSet localResultSet = localPreparedStatement.executeQuery();
    localOracleCachedRowSet.populate(localResultSet);
    localResultSet.close();
    localPreparedStatement.close();
    try
    {
      localConnection.commit();
    }
    catch (SQLException localSQLException)
    {
    }
    if (!localOracleCachedRowSet.isConnectionStayingOpen())
    {
      localConnection.close();
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleCachedRowSetReader
 * JD-Core Version:    0.6.0
 */