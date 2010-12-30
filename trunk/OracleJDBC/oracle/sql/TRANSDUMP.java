package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;

public class TRANSDUMP
{
  public static byte[] getTransitions(Connection paramConnection, int paramInt)
    throws SQLException
  {
    OracleCallableStatement localOracleCallableStatement = (OracleCallableStatement)paramConnection.prepareCall("begin dbms_utility.get_tz_transitions(?,?); end;");

    NUMBER localNUMBER = new NUMBER(paramInt);

    localOracleCallableStatement.setNUMBER(1, localNUMBER);

    localOracleCallableStatement.registerOutParameter(2, -2);

    localOracleCallableStatement.execute();

    byte[] arrayOfByte = localOracleCallableStatement.getBytes(2);

    localOracleCallableStatement.close();

    return arrayOfByte;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.TRANSDUMP
 * JD-Core Version:    0.6.0
 */