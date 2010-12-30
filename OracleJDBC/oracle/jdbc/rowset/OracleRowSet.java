package oracle.jdbc.rowset;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.sql.rowset.Joinable;

abstract class OracleRowSet
  implements Serializable, Cloneable, Joinable
{
  protected String dataSource;
  protected String dataSourceName;
  protected String url;
  protected String username;
  protected String password;
  protected Map typeMap;
  protected int maxFieldSize;
  protected int maxRows;
  protected int queryTimeout;
  protected int fetchSize;
  protected int transactionIsolation;
  protected boolean escapeProcessing;
  protected String command;
  protected int concurrency;
  protected boolean readOnly;
  protected int fetchDirection;
  protected int rowsetType;
  protected boolean showDeleted;
  protected Vector listener;
  protected RowSetEvent rowsetEvent;
  protected Vector matchColumnIndexes;
  protected Vector matchColumnNames;

  protected OracleRowSet()
    throws SQLException
  {
    initializeProperties();

    this.matchColumnIndexes = new Vector(10);
    this.matchColumnNames = new Vector(10);

    this.listener = new Vector();
    this.rowsetEvent = new RowSetEvent((RowSet)this);
  }

  protected void initializeProperties()
  {
    this.command = null;
    this.concurrency = 1007;
    this.dataSource = null;
    this.dataSourceName = null;

    this.escapeProcessing = true;
    this.fetchDirection = 1002;
    this.fetchSize = 0;
    this.maxFieldSize = 0;
    this.maxRows = 0;
    this.queryTimeout = 0;
    this.readOnly = true;
    this.showDeleted = false;
    this.transactionIsolation = 2;
    this.rowsetType = 1005;
    this.typeMap = new HashMap();
    this.username = null;
    this.password = null;
    this.url = null;
  }

  public String getCommand()
  {
    return this.command;
  }

  public int getConcurrency()
    throws SQLException
  {
    return this.concurrency;
  }

  /** @deprecated */
  public String getDataSource()
  {
    return this.dataSource;
  }

  public String getDataSourceName()
  {
    return this.dataSourceName;
  }

  public boolean getEscapeProcessing()
    throws SQLException
  {
    return this.escapeProcessing;
  }

  public int getFetchDirection()
    throws SQLException
  {
    return this.fetchDirection;
  }

  public int getFetchSize()
    throws SQLException
  {
    return this.fetchSize;
  }

  public int getMaxFieldSize()
    throws SQLException
  {
    return this.maxFieldSize;
  }

  public int getMaxRows()
    throws SQLException
  {
    return this.maxRows;
  }

  public String getPassword()
  {
    return this.password;
  }

  public int getQueryTimeout()
    throws SQLException
  {
    return this.queryTimeout;
  }

  public boolean getReadOnly()
  {
    return isReadOnly();
  }

  public boolean isReadOnly()
  {
    return this.readOnly;
  }

  public boolean getShowDeleted()
  {
    return this.showDeleted;
  }

  public int getTransactionIsolation()
  {
    return this.transactionIsolation;
  }

  public int getType()
    throws SQLException
  {
    return this.rowsetType;
  }

  public Map getTypeMap()
    throws SQLException
  {
    return this.typeMap;
  }

  public String getUrl()
  {
    return this.url;
  }

  public String getUsername()
  {
    return this.username;
  }

  public void setCommand(String paramString)
    throws SQLException
  {
    this.command = paramString;
  }

  public void setConcurrency(int paramInt)
    throws SQLException
  {
    if ((paramInt == 1007) || (paramInt == 1008))
      this.concurrency = paramInt;
    else
      throw new SQLException("Invalid concurrancy mode");
  }

  /** @deprecated */
  public void setDataSource(String paramString)
  {
    this.dataSource = paramString;
  }

  public void setDataSourceName(String paramString)
    throws SQLException
  {
    this.dataSourceName = paramString;
  }

  public void setEscapeProcessing(boolean paramBoolean)
    throws SQLException
  {
    this.escapeProcessing = paramBoolean;
  }

  public void setFetchDirection(int paramInt)
    throws SQLException
  {
    this.fetchDirection = paramInt;
  }

  public void setFetchSize(int paramInt)
    throws SQLException
  {
    this.fetchSize = paramInt;
  }

  public void setMaxFieldSize(int paramInt)
    throws SQLException
  {
    this.maxFieldSize = paramInt;
  }

  public void setMaxRows(int paramInt)
    throws SQLException
  {
    this.maxRows = paramInt;
  }

  public void setPassword(String paramString)
    throws SQLException
  {
    this.password = paramString;
  }

  public void setQueryTimeout(int paramInt)
    throws SQLException
  {
    this.queryTimeout = paramInt;
  }

  public void setReadOnly(boolean paramBoolean)
    throws SQLException
  {
    this.readOnly = paramBoolean;
  }

  public void setShowDeleted(boolean paramBoolean)
    throws SQLException
  {
    this.showDeleted = paramBoolean;
  }

  public void setTransactionIsolation(int paramInt)
    throws SQLException
  {
    this.transactionIsolation = paramInt;
  }

  public void setType(int paramInt)
    throws SQLException
  {
    if ((paramInt == 1003) || (paramInt == 1004) || (paramInt == 1005))
    {
      this.rowsetType = paramInt;
    }
    else throw new SQLException("Unknown RowSet type");
  }

  public void setTypeMap(Map paramMap)
    throws SQLException
  {
    this.typeMap = paramMap;
  }

  public void setUrl(String paramString)
  {
    this.url = paramString;
  }

  public void setUsername(String paramString)
    throws SQLException
  {
    this.username = paramString;
  }

  public void addRowSetListener(RowSetListener paramRowSetListener)
  {
    for (int i = 0; i < this.listener.size(); i++)
      if (this.listener.elementAt(i).equals(paramRowSetListener))
        return;
    this.listener.add(paramRowSetListener);
  }

  public void removeRowSetListener(RowSetListener paramRowSetListener)
  {
    for (int i = 0; i < this.listener.size(); i++)
      if (this.listener.elementAt(i).equals(paramRowSetListener))
        this.listener.remove(i);
  }

  protected synchronized void notifyCursorMoved()
  {
    int i = this.listener.size();
    if (i > 0)
      for (int j = 0; j < i; j++)
        ((RowSetListener)this.listener.elementAt(j)).cursorMoved(this.rowsetEvent);
  }

  protected void notifyRowChanged()
  {
    int i = this.listener.size();
    if (i > 0)
      for (int j = 0; j < i; j++)
      {
        ((RowSetListener)this.listener.elementAt(j)).rowChanged(this.rowsetEvent);
      }
  }

  protected void notifyRowSetChanged()
  {
    int i = this.listener.size();
    if (i > 0)
      for (int j = 0; j < i; j++)
      {
        ((RowSetListener)this.listener.elementAt(j)).rowSetChanged(this.rowsetEvent);
      }
  }

  public int[] getMatchColumnIndexes()
    throws SQLException
  {
    if ((this.matchColumnIndexes.size() == 0) && (this.matchColumnNames.size() == 0)) {
      throw new SQLException("No match column indexes were set");
    }

    if (this.matchColumnNames.size() > 0)
    {
      String[] arrayOfString = getMatchColumnNames();
      i = arrayOfString.length;
      arrayOfInt = new int[i];

      for (k = 0; k < i; k++)
      {
        arrayOfInt[k] = findColumn(arrayOfString[k]);
      }

    }

    int i = this.matchColumnIndexes.size();
    int[] arrayOfInt = new int[i];
    int j = -1;

    for (int k = 0; k < i; k++)
    {
      try
      {
        j = ((Integer)this.matchColumnIndexes.get(k)).intValue();
      }
      catch (Exception localException)
      {
        throw new SQLException("Invalid match column index");
      }

      if (j <= 0)
      {
        throw new SQLException("Invalid match column index");
      }

      arrayOfInt[k] = j;
    }

    return arrayOfInt;
  }

  public String[] getMatchColumnNames()
    throws SQLException
  {
    checkIfMatchColumnNamesSet();

    int i = this.matchColumnNames.size();
    String[] arrayOfString = new String[i];
    String str = null;

    for (int j = 0; j < i; j++)
    {
      try
      {
        str = (String)this.matchColumnNames.get(j);
      }
      catch (Exception localException)
      {
        throw new SQLException("Invalid match column name");
      }

      if ((str == null) || (str.equals("")))
      {
        throw new SQLException("Invalid match column name");
      }

      arrayOfString[j] = str;
    }

    return arrayOfString;
  }

  public void setMatchColumn(int paramInt)
    throws SQLException
  {
    if (paramInt <= 0)
    {
      throw new SQLException("The match column index should be greater than 0");
    }

    try
    {
      this.matchColumnIndexes.clear();
      this.matchColumnNames.clear();

      this.matchColumnIndexes.add(0, new Integer(paramInt));
    }
    catch (Exception localException)
    {
      throw new SQLException("The match column index could not be set");
    }
  }

  public void setMatchColumn(int[] paramArrayOfInt)
    throws SQLException
  {
    this.matchColumnIndexes.clear();
    this.matchColumnNames.clear();

    if (paramArrayOfInt == null)
    {
      throw new SQLException("The match column parameter is null");
    }

    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      if (paramArrayOfInt[i] <= 0)
      {
        throw new SQLException("The match column index should be greater than 0");
      }

      try
      {
        this.matchColumnIndexes.add(i, new Integer(paramArrayOfInt[i]));
      }
      catch (Exception localException)
      {
        throw new SQLException("The match column index could not be set");
      }
    }
  }

  public void setMatchColumn(String paramString)
    throws SQLException
  {
    if ((paramString == null) || (paramString.equals("")))
    {
      throw new SQLException("The match column name should be non-empty");
    }

    try
    {
      this.matchColumnIndexes.clear();
      this.matchColumnNames.clear();

      this.matchColumnNames.add(0, paramString.trim());
    }
    catch (Exception localException)
    {
      throw new SQLException("The match column name could not be set");
    }
  }

  public void setMatchColumn(String[] paramArrayOfString)
    throws SQLException
  {
    this.matchColumnIndexes.clear();
    this.matchColumnNames.clear();

    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].equals("")))
      {
        throw new SQLException("The match column name should be non-empty");
      }

      try
      {
        this.matchColumnNames.add(i, paramArrayOfString[i].trim());
      }
      catch (Exception localException)
      {
        throw new SQLException("The match column name could not be set");
      }
    }
  }

  public void unsetMatchColumn(int paramInt)
    throws SQLException
  {
    checkIfMatchColumnIndexesSet();

    if (paramInt <= 0)
    {
      throw new SQLException("The match column index should be greater than 0");
    }

    int i = -1;
    try
    {
      i = ((Integer)this.matchColumnIndexes.get(0)).intValue();
    }
    catch (Exception localException)
    {
      throw new SQLException("No match column indexes were set");
    }

    if (i != paramInt)
    {
      throw new SQLException("The column index being unset has not been set");
    }

    this.matchColumnIndexes.clear();
    this.matchColumnNames.clear();
  }

  public void unsetMatchColumn(int[] paramArrayOfInt)
    throws SQLException
  {
    checkIfMatchColumnIndexesSet();

    if (paramArrayOfInt == null)
    {
      throw new SQLException("The match column parameter is null");
    }

    int i = -1;

    for (int j = 0; j < paramArrayOfInt.length; j++)
    {
      if (paramArrayOfInt[j] <= 0)
      {
        throw new SQLException("The match column index should be greater than 0");
      }

      try
      {
        i = ((Integer)this.matchColumnIndexes.get(j)).intValue();
      }
      catch (Exception localException)
      {
        throw new SQLException("No match column indexes were set");
      }

      if (i == paramArrayOfInt[j])
        continue;
      throw new SQLException("The column index being unset has not been set");
    }

    this.matchColumnIndexes.clear();
    this.matchColumnNames.clear();
  }

  public void unsetMatchColumn(String paramString)
    throws SQLException
  {
    checkIfMatchColumnNamesSet();

    if ((paramString == null) || (paramString.equals("")))
    {
      throw new SQLException("The match column name should be non-empty");
    }

    String str = null;
    try
    {
      str = (String)this.matchColumnNames.get(0);
    }
    catch (Exception localException)
    {
      throw new SQLException("No match column names were set");
    }

    if (!str.equals(paramString.trim()))
    {
      throw new SQLException("The column name being unset has not been set");
    }

    this.matchColumnIndexes.clear();
    this.matchColumnNames.clear();
  }

  public void unsetMatchColumn(String[] paramArrayOfString)
    throws SQLException
  {
    checkIfMatchColumnNamesSet();

    if (paramArrayOfString == null)
    {
      throw new SQLException("The match column parameter is null");
    }

    String str = null;

    for (int i = 0; i < paramArrayOfString.length; i++)
    {
      if ((paramArrayOfString[i] == null) || (paramArrayOfString[i].equals("")))
      {
        throw new SQLException("The match column name should be non-empty");
      }

      try
      {
        str = (String)this.matchColumnNames.get(i);
      }
      catch (Exception localException)
      {
        throw new SQLException("No match column names were set");
      }

      if (str.equals(paramArrayOfString[i]))
        continue;
      throw new SQLException("The column name being unset has not been set");
    }

    this.matchColumnIndexes.clear();
    this.matchColumnNames.clear();
  }

  protected void checkIfMatchColumnIndexesSet()
    throws SQLException
  {
    if (this.matchColumnIndexes.size() == 0)
      throw new SQLException("No match column indexes were set");
  }

  protected void checkIfMatchColumnNamesSet()
    throws SQLException
  {
    if (this.matchColumnNames.size() == 0)
      throw new SQLException("No match column names were set");
  }

  public abstract int findColumn(String paramString)
    throws SQLException;

  public abstract ResultSetMetaData getMetaData()
    throws SQLException;

  abstract String getTableName()
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleRowSet
 * JD-Core Version:    0.6.0
 */