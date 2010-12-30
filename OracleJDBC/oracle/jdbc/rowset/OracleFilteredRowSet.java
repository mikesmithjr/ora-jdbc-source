package oracle.jdbc.rowset;

import java.sql.SQLException;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.Predicate;

public class OracleFilteredRowSet extends OracleWebRowSet
  implements FilteredRowSet
{
  private Predicate predicate;

  public OracleFilteredRowSet()
    throws SQLException
  {
  }

  public void setFilter(Predicate paramPredicate)
    throws SQLException
  {
    this.predicate = paramPredicate;
  }

  public Predicate getFilter()
  {
    return this.predicate;
  }

  public boolean next()
    throws SQLException
  {
    if (this.rowCount <= 0) {
      return false;
    }
    if (this.presentRow >= this.rowCount) {
      return false;
    }
    int i = 0;
    do
    {
      this.presentRow += 1;

      if ((this.predicate != null) && ((this.predicate == null) || (!this.predicate.evaluate(this)))) {
        continue;
      }
      i = 1;
      break;
    }

    while (this.presentRow <= this.rowCount);

    if (i != 0)
    {
      notifyCursorMoved();
      return true;
    }

    return false;
  }

  public boolean previous()
    throws SQLException
  {
    if (this.rowsetType == 1003) {
      throw new SQLException("The RowSet type is TYPE_FORWARD_ONLY");
    }
    if (this.rowCount <= 0) {
      return false;
    }
    if (this.presentRow <= 1) {
      return false;
    }
    int i = 0;
    do
    {
      this.presentRow -= 1;

      if ((this.predicate != null) && ((this.predicate == null) || (!this.predicate.evaluate(this)))) {
        continue;
      }
      i = 1;
      break;
    }

    while (this.presentRow >= 1);

    if (i != 0)
    {
      notifyCursorMoved();
      return true;
    }

    return false;
  }

  public boolean absolute(int paramInt)
    throws SQLException
  {
    if (this.rowsetType == 1003) {
      throw new SQLException("The RowSet type is TYPE_FORWARD_ONLY");
    }
    if ((paramInt == 0) || (Math.abs(paramInt) > this.rowCount)) {
      return false;
    }

    int i = paramInt < 0 ? this.rowCount + paramInt + 1 : paramInt;

    int j = 0;
    this.presentRow = 0;

    while ((j < i) && (this.presentRow <= this.rowCount))
    {
      if (next()) {
        j++; continue;
      }
      return false;
    }

    if (j == i)
    {
      notifyCursorMoved();
      return true;
    }

    return false;
  }

  protected void checkAndFilterObject(int paramInt, Object paramObject)
    throws SQLException
  {
    if ((this.predicate != null) && (!this.predicate.evaluate(paramObject, paramInt)))
      throw new SQLException("The object does not satisfy filtering criterion");
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleFilteredRowSet
 * JD-Core Version:    0.6.0
 */