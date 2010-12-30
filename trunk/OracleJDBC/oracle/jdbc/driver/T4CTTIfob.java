package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIfob extends T4CTTIMsg
{
  T4CTTIfob(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(19);
    setMarshalingEngine(paramT4CMAREngine);
  }

  void marshal() throws IOException, SQLException
  {
    this.meg.marshalUB1(19);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIfob
 * JD-Core Version:    0.6.0
 */