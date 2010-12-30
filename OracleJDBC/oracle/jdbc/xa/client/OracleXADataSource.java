package oracle.jdbc.xa.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.T2CConnection;
import oracle.jdbc.driver.T4CXAConnection;

public class OracleXADataSource extends oracle.jdbc.xa.OracleXADataSource
{
  private static final boolean DEBUG = false;
  private int rmid = -1;
  private String xaOpenString = null;
  private static boolean libraryLoaded = false;
  private static final String dbSuffix = "HeteroXA";
  private static final String dllName = "heteroxa10";
  private static final char atSignChar = '@';
  private static int rmidSeed = 0;
  private static final int MAX_RMID_SEED = 65536;
  private String driverCharSetIdString = null;

  private String oldTnsEntry = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:56_PDT_2005";

  public OracleXADataSource()
    throws SQLException
  {
    this.isOracleDataSource = true;
  }

  public XAConnection getXAConnection()
    throws SQLException
  {
    return getXAConnection(this.user, this.password);
  }

  public XAConnection getXAConnection(String paramString1, String paramString2)
    throws SQLException
  {
    if (this.connCachingEnabled)
    {
      DatabaseError.throwSqlException(163);

      return null;
    }

    Properties localProperties = new Properties();
    if (paramString1 != null)
      localProperties.setProperty("user", paramString1);
    if (paramString2 != null) {
      localProperties.setProperty("password", paramString2);
    }
    return getXAConnection(localProperties);
  }

  public XAConnection getXAConnection(Properties paramProperties)
    throws SQLException
  {
    return (XAConnection)getPooledConnection(paramProperties);
  }

  public PooledConnection getPooledConnection(String paramString1, String paramString2)
    throws SQLException
  {
    Properties localProperties = new Properties();
    localProperties.setProperty("user", paramString1);
    localProperties.setProperty("password", paramString2);
    return getPooledConnection(localProperties);
  }

  public PooledConnection getPooledConnection(Properties paramProperties)
    throws SQLException
  {
    try
    {
      String str1 = getURL();
      String str2 = paramProperties.getProperty("user");
      String str3 = paramProperties.getProperty("password");
      String str4 = null;
      String str5 = null;
      String str6 = null;
      int i = 0;

      if ((this.useNativeXA) && ((str1.startsWith("jdbc:oracle:oci8")) || (str1.startsWith("jdbc:oracle:oci"))))
      {
        localObject1 = new long[] { 0L, 0L };

        String str7 = null;
        String str8 = null;

        synchronized (this)
        {
          if (this.tnsEntry != null)
            str7 = this.tnsEntry;
          else {
            str7 = getTNSEntryFromUrl(str1);
          }

          if (((str7 != null) && (str7.length() == 0)) || (str7.startsWith("(DESCRIPTION")))
          {
            DatabaseError.throwSqlException(207);
          }

          if (!libraryLoaded)
          {
            synchronized (getClass())
            {
              if (!libraryLoaded)
              {
                try
                {
                  System.loadLibrary("heteroxa10");

                  libraryLoaded = true;
                }
                catch (Error localError)
                {
                  libraryLoaded = false;

                  throw localError;
                }

              }

            }

          }

          if (this.connectionProperties != null)
          {
            str8 = this.connectionProperties.getProperty("oracle.jdbc.ociNlsLangBackwardCompatible");
          }

        }

        if ((str8 != null) && (str8.equalsIgnoreCase("true")))
        {
          ??? = T2CConnection.getDriverCharSetIdFromNLS_LANG(null);
          this.driverCharSetIdString = Integer.toString(???);
        }
        else if (!str7.equals(this.oldTnsEntry))
        {
          ??? = T2CConnection.getClientCharSetId();

          this.driverCharSetIdString = Integer.toString(???);
          this.oldTnsEntry = str7;
        }

        synchronized (this)
        {
          str4 = this.databaseName + "HeteroXA" + rmidSeed;

          this.rmid = (i = rmidSeed);

          synchronized (getClass())
          {
            rmidSeed = (rmidSeed + 1) % 65536;
          }

          int k = 0;

          localOracleXAHeteroConnection = this.connectionProperties != null ? this.connectionProperties.getProperty("oracle.jdbc.XATransLoose") : null;

          this.xaOpenString = (str6 = generateXAOpenString(str4, str7, str2, str3, 60, 2000, true, true, ".", k, false, (localOracleXAHeteroConnection != null) && (localOracleXAHeteroConnection.equalsIgnoreCase("true")), this.driverCharSetIdString, this.driverCharSetIdString));

          str5 = generateXACloseString(str4, false);
        }

        int j = doXaOpen(str6, i, 0, 0);

        if (j != 0)
        {
          DatabaseError.throwSqlException(-1 * j);
        }

        j = convertOciHandles(str4, localObject1);

        if (j != 0)
        {
          DatabaseError.throwSqlException(-1 * j);
        }

        paramProperties.put("OCISvcCtxHandle", String.valueOf(localObject1[0]));
        paramProperties.put("OCIEnvHandle", String.valueOf(localObject1[1]));
        paramProperties.put("JDBCDriverCharSetId", this.driverCharSetIdString);

        if (this.loginTimeout != 0) {
          paramProperties.put("oracle.net.CONNECT_TIMEOUT", "" + this.loginTimeout * 1000);
        }

        Connection localConnection = this.driver.connect(getURL(), paramProperties);

        OracleXAHeteroConnection localOracleXAHeteroConnection = new OracleXAHeteroConnection(localConnection);

        localOracleXAHeteroConnection.setUserName(str2, str3.toUpperCase());
        localOracleXAHeteroConnection.setRmid(i);
        localOracleXAHeteroConnection.setXaCloseString(str5);
        localOracleXAHeteroConnection.registerCloseCallback(new OracleXAHeteroCloseCallback(), localOracleXAHeteroConnection);

        return localOracleXAHeteroConnection;
      }
      if ((this.thinUseNativeXA) && (str1.startsWith("jdbc:oracle:thin")))
      {
        localObject1 = new Properties();
        synchronized (this)
        {
          synchronized (getClass())
          {
            rmidSeed = (rmidSeed + 1) % 65536;
          }

          this.rmid = rmidSeed;

          if (this.connectionProperties == null) {
            this.connectionProperties = new Properties();
          }
          this.connectionProperties.put("RessourceManagerId", Integer.toString(this.rmid));
          if (str2 != null)
            ((Properties)localObject1).setProperty("user", str2);
          if (str3 != null)
            ((Properties)localObject1).setProperty("password", str3);
          ((Properties)localObject1).setProperty("stmt_cache_size", "" + this.maxStatements);

          ((Properties)localObject1).setProperty("ImplicitStatementCachingEnabled", "" + this.implicitCachingEnabled);

          ((Properties)localObject1).setProperty("ExplicitStatementCachingEnabled", "" + this.explicitCachingEnabled);

          ((Properties)localObject1).setProperty("LoginTimeout", "" + this.loginTimeout);
        }

        ??? = new T4CXAConnection(super.getPhysicalConnection((Properties)localObject1));

        ((T4CXAConnection)???).setUserName(str2, str3.toUpperCase());

        ??? = this.connectionProperties != null ? this.connectionProperties.getProperty("oracle.jdbc.XATransLoose") : null;

        ((OracleXAConnection)???).isXAResourceTransLoose = ((??? != null) && ((((String)???).equals("true")) || (((String)???).equalsIgnoreCase("true"))));

        return ???;
      }

      Object localObject1 = new Properties();
      synchronized (this)
      {
        if (str2 != null)
          ((Properties)localObject1).setProperty("user", str2);
        if (str3 != null)
          ((Properties)localObject1).setProperty("password", str3);
        ((Properties)localObject1).setProperty("stmt_cache_size", "" + this.maxStatements);

        ((Properties)localObject1).setProperty("ImplicitStatementCachingEnabled", "" + this.implicitCachingEnabled);

        ((Properties)localObject1).setProperty("ExplicitStatementCachingEnabled", "" + this.explicitCachingEnabled);

        ((Properties)localObject1).setProperty("LoginTimeout", "" + this.loginTimeout);
      }

      ??? = new OracleXAConnection(super.getPhysicalConnection((Properties)localObject1));

      ((OracleXAConnection)???).setUserName(str2, str3.toUpperCase());

      ??? = this.connectionProperties != null ? this.connectionProperties.getProperty("oracle.jdbc.XATransLoose") : null;

      ((OracleXAConnection)???).isXAResourceTransLoose = ((??? != null) && ((((String)???).equals("true")) || (((String)???).equalsIgnoreCase("true"))));

      return ???;
    }
    catch (XAException localXAException)
    {
    }

    return (PooledConnection)(PooledConnection)(PooledConnection)null;
  }

  private native int doXaOpen(String paramString, int paramInt1, int paramInt2, int paramInt3);

  private native int convertOciHandles(String paramString, long[] paramArrayOfLong);

  synchronized void setRmid(int paramInt)
  {
    this.rmid = paramInt;
  }

  synchronized int getRmid()
  {
    return this.rmid;
  }

  synchronized void setXaOpenString(String paramString)
  {
    this.xaOpenString = paramString;
  }

  synchronized String getXaOpenString()
  {
    return this.xaOpenString;
  }

  private String generateXAOpenString(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString5, int paramInt3, boolean paramBoolean3, boolean paramBoolean4, String paramString6, String paramString7)
  {
    return new String("ORACLE_XA+DB=" + paramString1 + "+ACC=P/" + paramString3 + "/" + paramString4 + "+SESTM=" + paramInt2 + "+SESWT=" + paramInt1 + "+LOGDIR=" + paramString5 + "+SQLNET=" + paramString2 + (paramBoolean1 ? "+THREADS=true" : "") + (paramBoolean2 ? "+OBJECTS=true" : "") + "+DBGFL=0x" + paramInt3 + (paramBoolean3 ? "+CONNCACHE=t" : "+CONNCACHE=f") + (paramBoolean4 ? "+Loose_Coupling=t" : "") + "+CharSet=" + paramString6 + "+NCharSet=" + paramString7);
  }

  private String generateXACloseString(String paramString, boolean paramBoolean)
  {
    return new String("ORACLE_XA+DB=" + paramString + (paramBoolean ? "+CONNCACHE=t" : "+CONNCACHE=f"));
  }

  private String getTNSEntryFromUrl(String paramString)
  {
    int i = paramString.indexOf('@');

    return paramString.substring(i + 1);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.xa.client.OracleXADataSource
 * JD-Core Version:    0.6.0
 */