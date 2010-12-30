package oracle.jdbc.driver;

import java.sql.SQLException;

public class OracleSavepoint
  implements oracle.jdbc.OracleSavepoint
{
  private static final int MAX_ID_VALUE = 65535;
  private static final int INVALID_ID_VALUE = -1;
  static final int NAMED_SAVEPOINT_TYPE = 2;
  static final int UNNAMED_SAVEPOINT_TYPE = 1;
  static final int UNKNOWN_SAVEPOINT_TYPE = 0;
  private static int s_seedId = 0;
  private int m_id = -1;
  private String m_name = null;
  private int m_type = 0;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  OracleSavepoint()
  {
    this.m_type = 1;
    this.m_id = getNextId();
    this.m_name = null;
  }

  OracleSavepoint(String paramString)
    throws SQLException
  {
    if ((paramString != null) && (paramString.length() != 0) && (!OracleSql.isValidObjectName(paramString)))
    {
      DatabaseError.throwSqlException(68);
    }
    this.m_type = 2;
    this.m_name = paramString;
    this.m_id = -1;
  }

  public int getSavepointId()
    throws SQLException
  {
    if (this.m_type == 2) {
      DatabaseError.throwSqlException(118);
    }
    return this.m_id;
  }

  public String getSavepointName()
    throws SQLException
  {
    if (this.m_type == 1) {
      DatabaseError.throwSqlException(119);
    }
    return this.m_name;
  }

  int getType()
  {
    return this.m_type;
  }

  private synchronized int getNextId()
  {
    s_seedId = (s_seedId + 1) % 65535;

    return s_seedId;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleSavepoint
 * JD-Core Version:    0.6.0
 */