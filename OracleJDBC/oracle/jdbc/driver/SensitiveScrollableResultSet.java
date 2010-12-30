package oracle.jdbc.driver;

import java.sql.SQLException;

class SensitiveScrollableResultSet extends ScrollableResultSet
{
  int beginLastFetchedIndex;
  int endLastFetchedIndex;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  SensitiveScrollableResultSet(ScrollRsetStatement paramScrollRsetStatement, OracleResultSetImpl paramOracleResultSetImpl, int paramInt1, int paramInt2)
    throws SQLException
  {
    super(paramScrollRsetStatement, paramOracleResultSetImpl, paramInt1, paramInt2);

    int i = paramOracleResultSetImpl.getValidRows();

    if (i > 0)
    {
      this.beginLastFetchedIndex = 1;
      this.endLastFetchedIndex = i;
    }
    else
    {
      this.beginLastFetchedIndex = 0;
      this.endLastFetchedIndex = 0;
    }
  }

  public synchronized boolean next()
    throws SQLException
  {
    if (super.next())
    {
      handle_refetch();

      return true;
    }

    return false;
  }

  public synchronized boolean first()
    throws SQLException
  {
    if (super.first())
    {
      handle_refetch();

      return true;
    }

    return false;
  }

  public synchronized boolean last()
    throws SQLException
  {
    if (super.last())
    {
      handle_refetch();

      return true;
    }

    return false;
  }

  public synchronized boolean absolute(int paramInt)
    throws SQLException
  {
    if (super.absolute(paramInt))
    {
      handle_refetch();

      return true;
    }

    return false;
  }

  public synchronized boolean relative(int paramInt)
    throws SQLException
  {
    if (super.relative(paramInt))
    {
      handle_refetch();

      return true;
    }

    return false;
  }

  public synchronized boolean previous()
    throws SQLException
  {
    if (super.previous())
    {
      handle_refetch();

      return true;
    }

    return false;
  }

  public synchronized void refreshRow()
    throws SQLException
  {
    if (!isValidRow(this.currentRow)) {
      DatabaseError.throwSqlException(11);
    }
    int i = getFetchDirection();
    int j = 0;
    try
    {
      j = refreshRowsInCache(this.currentRow, getFetchSize(), i);
    }
    catch (SQLException localSQLException)
    {
      DatabaseError.throwSqlException(localSQLException, 90, "Unsupported syntax for refreshRow()");
    }

    if (j != 0)
    {
      this.beginLastFetchedIndex = this.currentRow;

      this.endLastFetchedIndex = (this.currentRow + j - 1);
    }
  }

  synchronized int removeRowInCache(int paramInt)
    throws SQLException
  {
    int i = super.removeRowInCache(paramInt);

    if (i != 0)
    {
      if ((paramInt >= this.beginLastFetchedIndex) && (paramInt <= this.endLastFetchedIndex) && (this.beginLastFetchedIndex != this.endLastFetchedIndex))
      {
        this.endLastFetchedIndex -= 1;
      }
      else
      {
        this.beginLastFetchedIndex = (this.endLastFetchedIndex = 0);
      }
    }
    return i;
  }

  private boolean handle_refetch()
    throws SQLException
  {
    if (((this.currentRow >= this.beginLastFetchedIndex) && (this.currentRow <= this.endLastFetchedIndex)) || ((this.currentRow >= this.endLastFetchedIndex) && (this.currentRow <= this.beginLastFetchedIndex)))
    {
      return false;
    }

    refreshRow();

    return true;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.SensitiveScrollableResultSet
 * JD-Core Version:    0.6.0
 */