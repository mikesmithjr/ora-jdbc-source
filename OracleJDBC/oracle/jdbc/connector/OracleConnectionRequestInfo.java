package oracle.jdbc.connector;

import javax.resource.spi.ConnectionRequestInfo;

public class OracleConnectionRequestInfo
  implements ConnectionRequestInfo
{
  private String user = null;
  private String password = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:56_PDT_2005";

  public OracleConnectionRequestInfo(String paramString1, String paramString2)
  {
    this.user = paramString1;
    this.password = paramString2;
  }

  public String getUser()
  {
    return this.user;
  }

  public void setUser(String paramString)
  {
    this.user = paramString;
  }

  public String getPassword()
  {
    return this.password;
  }

  public void setPassword(String paramString)
  {
    this.password = paramString;
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof OracleConnectionRequestInfo)) {
      return false;
    }
    OracleConnectionRequestInfo localOracleConnectionRequestInfo = (OracleConnectionRequestInfo)paramObject;

    return (this.user == localOracleConnectionRequestInfo.getUser()) && (this.password == localOracleConnectionRequestInfo.getPassword());
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.connector.OracleConnectionRequestInfo
 * JD-Core Version:    0.6.0
 */