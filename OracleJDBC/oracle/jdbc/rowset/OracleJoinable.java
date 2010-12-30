package oracle.jdbc.rowset;

import java.sql.SQLException;
import javax.sql.rowset.Joinable;

public abstract interface OracleJoinable extends Joinable
{
  public abstract int[] getMatchColumnIndexes()
    throws SQLException;

  public abstract String[] getMatchColumnNames()
    throws SQLException;

  public abstract void setMatchColumn(int paramInt)
    throws SQLException;

  public abstract void setMatchColumn(int[] paramArrayOfInt)
    throws SQLException;

  public abstract void setMatchColumn(String paramString)
    throws SQLException;

  public abstract void setMatchColumn(String[] paramArrayOfString)
    throws SQLException;

  public abstract void unsetMatchColumn(int paramInt)
    throws SQLException;

  public abstract void unsetMatchColumn(int[] paramArrayOfInt)
    throws SQLException;

  public abstract void unsetMatchColumn(String paramString)
    throws SQLException;

  public abstract void unsetMatchColumn(String[] paramArrayOfString)
    throws SQLException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.rowset.OracleJoinable
 * JD-Core Version:    0.6.0
 */