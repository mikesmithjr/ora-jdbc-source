package oracle.jdbc.driver;

import java.sql.SQLException;

public class OracleSQLException extends SQLException
{
  private Object[] m_parameters;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public OracleSQLException()
  {
    this(null, null, 0);
  }

  public OracleSQLException(String paramString)
  {
    this(paramString, null, 0);
  }

  public OracleSQLException(String paramString1, String paramString2)
  {
    this(paramString1, paramString2, 0);
  }

  public OracleSQLException(String paramString1, String paramString2, int paramInt)
  {
    this(paramString1, paramString2, paramInt, null);
  }

  public OracleSQLException(String paramString1, String paramString2, int paramInt, Object[] paramArrayOfObject)
  {
    super(paramString1, paramString2, paramInt);

    this.m_parameters = paramArrayOfObject;
  }

  public Object[] getParameters()
  {
    if (this.m_parameters == null) {
      this.m_parameters = new Object[0];
    }

    return this.m_parameters;
  }

  public int getNumParameters()
  {
    if (this.m_parameters == null) {
      this.m_parameters = new Object[0];
    }

    return this.m_parameters.length;
  }

  public void setParameters(Object[] paramArrayOfObject)
  {
    this.m_parameters = paramArrayOfObject;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleSQLException
 * JD-Core Version:    0.6.0
 */