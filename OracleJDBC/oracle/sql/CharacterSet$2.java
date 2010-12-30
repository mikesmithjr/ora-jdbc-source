package oracle.sql;

import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;

class CharacterSet$2 extends CharacterSet.CharacterConverterBehavior
{
  public void onFailConversion()
    throws SQLException
  {
    DatabaseError.throwSqlException(55);
  }

  public void onFailConversion(char paramChar)
    throws SQLException
  {
    if (paramChar == 65533)
    {
      DatabaseError.throwSqlException(55);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CharacterSet.2
 * JD-Core Version:    0.6.0
 */