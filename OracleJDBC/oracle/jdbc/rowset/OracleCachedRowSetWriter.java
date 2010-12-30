package oracle.jdbc.rowset;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.StringTokenizer;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetWriter;

public class OracleCachedRowSetWriter
  implements RowSetWriter, Serializable
{
  private StringBuffer updateClause = new StringBuffer("");

  private StringBuffer deleteClause = new StringBuffer("");

  private StringBuffer insertClause = new StringBuffer("");
  private PreparedStatement insertStmt;
  private PreparedStatement updateStmt;
  private PreparedStatement deleteStmt;
  private ResultSetMetaData rsmd;
  private transient Connection connection;
  private int columnCount;

  private String getSchemaName(RowSet paramRowSet)
    throws SQLException
  {
    return paramRowSet.getUsername();
  }

  private String getTableName(RowSet paramRowSet)
    throws SQLException
  {
    String str1 = ((OracleCachedRowSet)paramRowSet).getTableName();
    if (str1 != null) {
      return str1;
    }
    String str2 = paramRowSet.getCommand().toUpperCase();

    int i = str2.indexOf(" FROM ");

    if (i == -1) {
      throw new SQLException("Could not parse the SQL String to get the table name.\n" + (str2 != "" ? str2 : "Please use RowSet.setCommand (String) to set the SQL query string."));
    }

    String str3 = str2.substring(i + 6).trim();

    StringTokenizer localStringTokenizer = new StringTokenizer(str3);
    if (localStringTokenizer.hasMoreTokens()) {
      str3 = localStringTokenizer.nextToken();
    }

    return str3;
  }

  private void initSQLStatement(RowSet paramRowSet)
    throws SQLException
  {
    this.insertClause = new StringBuffer("INSERT INTO " + getTableName(paramRowSet) + "(");
    this.updateClause = new StringBuffer("UPDATE " + getTableName(paramRowSet) + " SET ");
    this.deleteClause = new StringBuffer("DELETE FROM " + getTableName(paramRowSet) + " WHERE ");

    this.rsmd = paramRowSet.getMetaData();
    this.columnCount = this.rsmd.getColumnCount();

    for (int i = 0; i < this.columnCount; i++)
    {
      if (i != 0) this.insertClause.append(", ");
      this.insertClause.append(this.rsmd.getColumnName(i + 1));

      if (i != 0) this.updateClause.append(", ");
      this.updateClause.append(this.rsmd.getColumnName(i + 1) + " = :" + i);

      if (i != 0) this.deleteClause.append(" AND ");
      this.deleteClause.append(this.rsmd.getColumnName(i + 1) + " = :" + i);
    }
    this.insertClause.append(") VALUES (");
    this.updateClause.append(" WHERE ");

    for (i = 0; i < this.columnCount; i++)
    {
      if (i != 0) this.insertClause.append(", ");
      this.insertClause.append(":" + i);

      if (i != 0) this.updateClause.append(" AND ");
      this.updateClause.append(this.rsmd.getColumnName(i + 1) + " = :" + i);
    }
    this.insertClause.append(")");

    this.insertStmt = this.connection.prepareStatement(this.insertClause.substring(0, this.insertClause.length()));

    this.updateStmt = this.connection.prepareStatement(this.updateClause.substring(0, this.updateClause.length()));

    this.deleteStmt = this.connection.prepareStatement(this.deleteClause.substring(0, this.deleteClause.length()));
  }

  private boolean insertRow(OracleRow paramOracleRow)
    throws SQLException
  {
    this.insertStmt.clearParameters();
    for (int i = 1; i <= this.columnCount; i++)
    {
      Object localObject = null;
      localObject = paramOracleRow.isColumnChanged(i) ? paramOracleRow.getModifiedColumn(i) : paramOracleRow.getColumn(i);

      if (localObject == null)
        this.insertStmt.setNull(i, this.rsmd.getColumnType(i));
      else
        this.insertStmt.setObject(i, localObject);
    }
    return this.insertStmt.executeUpdate() == 1;
  }

  private boolean updateRow(RowSet paramRowSet, OracleRow paramOracleRow)
    throws SQLException
  {
    this.updateStmt.clearParameters();
    for (int i = 1; i <= this.columnCount; i++)
    {
      Object localObject = null;
      localObject = paramOracleRow.isColumnChanged(i) ? paramOracleRow.getModifiedColumn(i) : paramOracleRow.getColumn(i);

      if (localObject == null)
        this.updateStmt.setNull(i, this.rsmd.getColumnType(i));
      else
        this.updateStmt.setObject(i, localObject);
    }
    for (i = 1; i <= this.columnCount; i++)
    {
      if (paramOracleRow.isOriginalNull(i)) {
        return updateRowWithNull(paramRowSet, paramOracleRow);
      }
      this.updateStmt.setObject(i + this.columnCount, paramOracleRow.getColumn(i));
    }

    return this.updateStmt.executeUpdate() == 1;
  }

  private boolean updateRowWithNull(RowSet paramRowSet, OracleRow paramOracleRow)
    throws SQLException
  {
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer("UPDATE " + getTableName(paramRowSet) + " SET ");

    for (int j = 1; j <= this.columnCount; j++)
    {
      if (j != 1) {
        localStringBuffer.append(", ");
      }
      localStringBuffer.append(this.rsmd.getColumnName(j) + " = :" + j);
    }

    localStringBuffer.append(" WHERE ");

    for (j = 1; j <= this.columnCount; j++)
    {
      if (j != 1)
        localStringBuffer.append(" AND ");
      if (paramOracleRow.isOriginalNull(j))
        localStringBuffer.append(this.rsmd.getColumnName(j) + " IS NULL ");
      else {
        localStringBuffer.append(this.rsmd.getColumnName(j) + " = :" + j);
      }
    }
    PreparedStatement localPreparedStatement = null;
    try
    {
      localPreparedStatement = this.connection.prepareStatement(localStringBuffer.substring(0, localStringBuffer.length()));

      for (int k = 1; k <= this.columnCount; k++)
      {
        Object localObject1 = null;
        localObject1 = paramOracleRow.isColumnChanged(k) ? paramOracleRow.getModifiedColumn(k) : paramOracleRow.getColumn(k);

        if (localObject1 == null)
          localPreparedStatement.setNull(k, this.rsmd.getColumnType(k));
        else {
          localPreparedStatement.setObject(k, localObject1);
        }
      }
      k = 1; for (int m = 1; k <= this.columnCount; k++)
      {
        if (paramOracleRow.isOriginalNull(k)) {
          continue;
        }
        localPreparedStatement.setObject(m + this.columnCount, paramOracleRow.getColumn(k));

        m++;
      }
      i = localPreparedStatement.executeUpdate() == 1 ? 1 : 0;
    }
    finally {
      if (localPreparedStatement != null) {
        localPreparedStatement.close();
      }
    }
    return i;
  }

  private boolean deleteRow(RowSet paramRowSet, OracleRow paramOracleRow)
    throws SQLException
  {
    this.deleteStmt.clearParameters();
    for (int i = 1; i <= this.columnCount; i++)
    {
      if (paramOracleRow.isOriginalNull(i)) {
        return deleteRowWithNull(paramRowSet, paramOracleRow);
      }
      Object localObject = paramOracleRow.getColumn(i);
      if (localObject == null)
        this.deleteStmt.setNull(i, this.rsmd.getColumnType(i));
      else {
        this.deleteStmt.setObject(i, localObject);
      }
    }
    return this.deleteStmt.executeUpdate() == 1;
  }

  private boolean deleteRowWithNull(RowSet paramRowSet, OracleRow paramOracleRow)
    throws SQLException
  {
    int i = 0;
    StringBuffer localStringBuffer = new StringBuffer("DELETE FROM " + getTableName(paramRowSet) + " WHERE ");

    for (int j = 1; j <= this.columnCount; j++)
    {
      if (j != 1)
        localStringBuffer.append(" AND ");
      if (paramOracleRow.isOriginalNull(j))
        localStringBuffer.append(this.rsmd.getColumnName(j) + " IS NULL ");
      else {
        localStringBuffer.append(this.rsmd.getColumnName(j) + " = :" + j);
      }
    }
    PreparedStatement localPreparedStatement = null;
    try
    {
      localPreparedStatement = this.connection.prepareStatement(localStringBuffer.substring(0, localStringBuffer.length()));

      int k = 1; for (int m = 1; k <= this.columnCount; k++)
      {
        if (paramOracleRow.isOriginalNull(k)) {
          continue;
        }
        localPreparedStatement.setObject(m++, paramOracleRow.getColumn(k));
      }
      i = localPreparedStatement.executeUpdate() == 1 ? 1 : 0;
    }
    finally {
      if (localPreparedStatement != null) {
        localPreparedStatement.close();
      }
    }
    return i;
  }

  public synchronized boolean writeData(RowSetInternal paramRowSetInternal)
    throws SQLException
  {
    OracleCachedRowSet localOracleCachedRowSet = (OracleCachedRowSet)paramRowSetInternal;
    this.connection = ((OracleCachedRowSetReader)localOracleCachedRowSet.getReader()).getConnection(paramRowSetInternal);

    if (this.connection == null)
      throw new SQLException("Unable to get Connection");
    if (this.connection.getAutoCommit())
      this.connection.setAutoCommit(false);
    this.connection.setTransactionIsolation(localOracleCachedRowSet.getTransactionIsolation());
    initSQLStatement(localOracleCachedRowSet);
    if (this.columnCount < 1)
    {
      this.connection.close();
      return true;
    }
    boolean bool = localOracleCachedRowSet.getShowDeleted();
    localOracleCachedRowSet.setShowDeleted(true);
    localOracleCachedRowSet.beforeFirst();
    int i = 1;
    int j = 1;
    int k = 1;
    OracleRow localOracleRow = null;
    while (localOracleCachedRowSet.next())
    {
      if (localOracleCachedRowSet.rowInserted())
      {
        if (localOracleCachedRowSet.rowDeleted())
          continue;
        localOracleRow = localOracleCachedRowSet.getCurrentRow();

        j = (insertRow(localOracleRow)) || (j != 0) ? 1 : 0; continue;
      }
      if (localOracleCachedRowSet.rowUpdated())
      {
        localOracleRow = localOracleCachedRowSet.getCurrentRow();

        i = (updateRow(localOracleCachedRowSet, localOracleRow)) || (i != 0) ? 1 : 0; continue;
      }
      if (!localOracleCachedRowSet.rowDeleted())
        continue;
      localOracleRow = localOracleCachedRowSet.getCurrentRow();

      k = (deleteRow(localOracleCachedRowSet, localOracleRow)) || (k != 0) ? 1 : 0;
    }

    if ((i != 0) && (j != 0) && (k != 0))
    {
      this.connection.commit();

      localOracleCachedRowSet.setOriginal();
    }
    else {
      this.connection.rollback();
    }
    this.insertStmt.close();
    this.updateStmt.close();
    this.deleteStmt.close();

    if (!localOracleCachedRowSet.isConnectionStayingOpen())
    {
      this.connection.close();
    }

    localOracleCachedRowSet.setShowDeleted(bool);
    return true;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleCachedRowSetWriter
 * JD-Core Version:    0.6.0
 */