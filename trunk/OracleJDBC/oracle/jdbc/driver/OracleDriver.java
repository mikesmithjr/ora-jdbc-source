package oracle.jdbc.driver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import oracle.jdbc.OracleDatabaseMetaData;
import oracle.security.pki.OracleSecretStore;
import oracle.security.pki.OracleWallet;

public class OracleDriver
  implements Driver
{
  public static final char slash_character = '/';
  public static final char at_sign_character = '@';
  public static final char left_square_bracket_character = '[';
  public static final char right_square_bracket_character = ']';
  public static final String oracle_string = "oracle";
  public static final String protocol_string = "protocol";
  public static final String user_string = "user";
  public static final String password_string = "password";
  public static final String database_string = "database";
  public static final String server_string = "server";

  /** @deprecated */
  public static final String access_string = "access";

  /** @deprecated */
  public static final String protocolFullName_string = "protocolFullName";
  public static final String logon_as_internal_str = "internal_logon";
  public static final String proxy_client_name = "PROXY_CLIENT_NAME";

  /** @deprecated */
  public static final String prefetch_string = "prefetch";

  /** @deprecated */
  public static final String row_prefetch_string = "rowPrefetch";
  public static final String default_row_prefetch_string = "defaultRowPrefetch";

  /** @deprecated */
  public static final String batch_string = "batch";

  /** @deprecated */
  public static final String execute_batch_string = "executeBatch";
  public static final String default_execute_batch_string = "defaultExecuteBatch";
  public static final String process_escapes_string = "processEscapes";
  public static final String dms_parent_name_string = "DMSName";
  public static final String dms_parent_type_string = "DMSType";
  public static final String accumulate_batch_result = "AccumulateBatchResult";
  public static final String j2ee_compliance = "oracle.jdbc.J2EE13Compliant";
  public static final String v8compatible_string = "oracle.jdbc.V8Compatible";
  public static final String permit_timestamp_date_mismatch_string = "oracle.jdbc.internal.permitBindDateDefineTimestampMismatch";
  public static final String StreamChunkSize_string = "oracle.jdbc.StreamChunkSize";
  public static final String SetFloatAndDoubleUseBinary_string = "SetFloatAndDoubleUseBinary";

  /** @deprecated */
  public static final String xa_trans_loose = "oracle.jdbc.XATransLoose";
  public static final String tcp_no_delay = "oracle.jdbc.TcpNoDelay";
  public static final String read_timeout = "oracle.jdbc.ReadTimeout";
  public static final String defaultnchar_string = "oracle.jdbc.defaultNChar";
  public static final String defaultncharprop_string = "defaultNChar";
  public static final String useFetchSizeWithLongColumn_prop_string = "useFetchSizeWithLongColumn";
  public static final String useFetchSizeWithLongColumn_string = "oracle.jdbc.useFetchSizeWithLongColumn";

  /** @deprecated */
  public static final String remarks_string = "remarks";
  public static final String report_remarks_string = "remarksReporting";

  /** @deprecated */
  public static final String synonyms_string = "synonyms";
  public static final String include_synonyms_string = "includeSynonyms";
  public static final String restrict_getTables_string = "restrictGetTables";
  public static final String fixed_string_string = "fixedString";
  public static final String dll_string = "oracle.jdbc.ocinativelibrary";
  public static final String nls_lang_backdoor = "oracle.jdbc.ociNlsLangBackwardCompatible";
  public static final String disable_defineColumnType_string = "disableDefineColumnType";
  public static final String convert_nchar_literals_string = "oracle.jdbc.convertNcharLiterals";

  /** @deprecated */
  public static final String dataSizeUnitsPropertyName = "";

  /** @deprecated */
  public static final String dataSizeBytes = "";

  /** @deprecated */
  public static final String dataSizeChars = "";
  public static final String dms_stmt_metrics_string = "oracle.jdbc.DMSStatementMetrics";
  public static final String dms_stmt_caching_metrics_string = "oracle.jdbc.DMSStatementCachingMetrics";
  public static final String set_new_password_string = "OCINewPassword";
  public static final String retain_v9_bind_behavior_string = "oracle.jdbc.RetainV9LongBindBehavior";
  public static final String no_caching_buffers = "oracle.jdbc.FreeMemoryOnEnterImplicitCache";
  public static final String secret_store_connect = "oracle.security.client.connect_string";
  public static final String secret_store_username = "oracle.security.client.username";
  public static final String secret_store_password = "oracle.security.client.password";
  public static final String secret_store_default_username = "oracle.security.client.default_username";
  public static final String secret_store_default_password = "oracle.security.client.default_password";
  public static final String wallet_location_string = "oracle.net.wallet_location";
  private static String walletLocation = null;
  static final int EXTENSION_TYPE_ORACLE_ERROR = -3;
  static final int EXTENSION_TYPE_GEN_ERROR = -2;
  static final int EXTENSION_TYPE_TYPE4_CLIENT = 0;
  static final int EXTENSION_TYPE_TYPE4_SERVER = 1;
  static final int EXTENSION_TYPE_TYPE2_CLIENT = 2;
  static final int EXTENSION_TYPE_TYPE2_SERVER = 3;
  private static final int NUMBER_OF_EXTENSION_TYPES = 4;
  private OracleDriverExtension[] driverExtensions = new OracleDriverExtension[4];
  private static final String DRIVER_PACKAGE_STRING = "driver";
  private static final String[] driverExtensionClassNames = { "oracle.jdbc.driver.T4CDriverExtension", "oracle.jdbc.driver.T4CDriverExtension", "oracle.jdbc.driver.T2CDriverExtension", "oracle.jdbc.driver.T2SDriverExtension" };
  private static Properties driverAccess;
  protected static Connection defaultConn = null;
  private static OracleDriver defaultDriver = null;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  public Connection connect(String paramString, Properties paramProperties)
    throws SQLException
  {
    if (paramString.regionMatches(0, "jdbc:default:connection", 0, 23))
    {
      String str1 = "jdbc:oracle:kprb";
      int j = paramString.length();

      if (j > 23)
        paramString = str1.concat(paramString.substring(23, paramString.length()));
      else {
        paramString = str1.concat(":");
      }
      str1 = null;
    }

    int i = oracleDriverExtensionTypeFromURL(paramString);

    if (i == -2) {
      return null;
    }
    if (i == -3) {
      DatabaseError.throwSqlException(67);
    }
    OracleDriverExtension localOracleDriverExtension = null;

    localOracleDriverExtension = this.driverExtensions[i];

    if (localOracleDriverExtension == null)
    {
      try
      {
        synchronized (this)
        {
          if (localOracleDriverExtension == null)
          {
            localOracleDriverExtension = (OracleDriverExtension)Class.forName(driverExtensionClassNames[i]).newInstance();

            this.driverExtensions[i] = localOracleDriverExtension;
          }
          else
          {
            localOracleDriverExtension = this.driverExtensions[i];
          }

        }

      }
      catch (Exception localException)
      {
        throw new SQLException(localException.toString());
      }

    }

    walletLocation = paramProperties.getProperty("oracle.net.wallet_location");

    if (walletLocation == null)
    {
      walletLocation = getSystemProperty("oracle.net.wallet_location", null);
    }

    Hashtable localHashtable = parseUrl(paramString);

    if (localHashtable == null)
    {
      return null;
    }

    String str2 = paramProperties.getProperty("user");
    String str3 = paramProperties.getProperty("password");
    String str4 = paramProperties.getProperty("database");

    if (str4 == null) {
      str4 = paramProperties.getProperty("server");
    }
    if (str2 == null) {
      str2 = (String)localHashtable.get("user");
    }
    str2 = parseLoginOption(str2, paramProperties);

    if (str3 == null) {
      str3 = (String)localHashtable.get("password");
    }
    if (str4 == null) {
      str4 = (String)localHashtable.get("database");
    }
    String str5 = (String)localHashtable.get("protocol");

    paramProperties.put("protocol", str5);

    if (str5 == null)
    {
      DatabaseError.throwSqlException(40, "Protocol is not specified in URL");

      return null;
    }

    if ((str5.equals("oci8")) || (str5.equals("oci"))) {
      str4 = translateConnStr(str4);
    }
    String str6 = paramProperties.getProperty("oracle.jdbc.TcpNoDelay");

    if (str6 == null)
    {
      str6 = getSystemProperty("oracle.jdbc.TcpNoDelay", null);
    }
    if ((str6 != null) && (str6.equalsIgnoreCase("true"))) {
      paramProperties.put("TCP.NODELAY", "YES");
    }
    String str7 = paramProperties.getProperty("oracle.jdbc.ReadTimeout");
    if (str7 != null) {
      paramProperties.put("oracle.net.READ_TIMEOUT", str7);
    }

    int k = DriverManager.getLoginTimeout();

    if ((k != 0) && (paramProperties.get("oracle.net.CONNECT_TIMEOUT") == null))
    {
      paramProperties.put("oracle.net.CONNECT_TIMEOUT", "" + k * 1000);
    }

    String str8 = paramProperties.getProperty("prefetch");

    if (str8 == null) {
      str8 = paramProperties.getProperty("rowPrefetch");
    }
    if (str8 == null) {
      str8 = paramProperties.getProperty("defaultRowPrefetch");
    }
    if ((str8 != null) && (Integer.parseInt(str8) <= 0)) {
      str8 = null;
    }
    String str9 = paramProperties.getProperty("batch");

    if (str9 == null) {
      str9 = paramProperties.getProperty("executeBatch");
    }
    if (str9 == null) {
      str9 = paramProperties.getProperty("defaultExecuteBatch");
    }
    if ((str9 != null) && (Integer.parseInt(str9) <= 0)) {
      str9 = null;
    }

    String str10 = paramProperties.getProperty("defaultNChar");

    if (str10 == null) {
      str10 = getSystemProperty("oracle.jdbc.defaultNChar", null);
    }
    String str11 = paramProperties.getProperty("useFetchSizeWithLongColumn");

    if (str11 == null)
    {
      str11 = getSystemProperty("oracle.jdbc.useFetchSizeWithLongColumn", null);
    }

    String str12 = paramProperties.getProperty("remarks");

    if (str12 == null) {
      str12 = paramProperties.getProperty("remarksReporting");
    }

    String str13 = paramProperties.getProperty("synonyms");

    if (str13 == null) {
      str13 = paramProperties.getProperty("includeSynonyms");
    }

    String str14 = paramProperties.getProperty("restrictGetTables");

    String str15 = paramProperties.getProperty("fixedString");

    String str16 = paramProperties.getProperty("AccumulateBatchResult");

    if (str16 == null) {
      str16 = "true";
    }

    String str17 = paramProperties.getProperty("disableDefineColumnType");

    if (str17 == null) {
      str17 = "false";
    }
    String str18 = paramProperties.getProperty("oracle.jdbc.convertNcharLiterals");

    if (str18 == null) {
      str18 = getSystemProperty("oracle.jdbc.convertNcharLiterals", "false");
    }

    Enumeration localEnumeration = DriverManager.getDrivers();

    while (localEnumeration.hasMoreElements())
    {
      localObject2 = (Driver)localEnumeration.nextElement();

      if ((localObject2 instanceof OracleDriver)) {
        break;
      }
    }

    while (localEnumeration.hasMoreElements())
    {
      localObject2 = (Driver)localEnumeration.nextElement();

      if ((localObject2 instanceof OracleDriver)) {
        DriverManager.deregisterDriver((Driver)localObject2);
      }

    }

    Object localObject2 = (PhysicalConnection)localOracleDriverExtension.getConnection(paramString, str2, str3, str4, paramProperties);

    if (str8 != null) {
      ((PhysicalConnection)localObject2).setDefaultRowPrefetch(Integer.parseInt(str8));
    }
    if (str9 != null) {
      ((PhysicalConnection)localObject2).setDefaultExecuteBatch(Integer.parseInt(str9));
    }
    if (str12 != null) {
      ((PhysicalConnection)localObject2).setRemarksReporting(str12.equalsIgnoreCase("true"));
    }
    if (str13 != null) {
      ((PhysicalConnection)localObject2).setIncludeSynonyms(str13.equalsIgnoreCase("true"));
    }
    if (str14 != null) {
      ((PhysicalConnection)localObject2).setRestrictGetTables(str14.equalsIgnoreCase("true"));
    }
    if (str15 != null) {
      ((PhysicalConnection)localObject2).setDefaultFixedString(str15.equalsIgnoreCase("true"));
    }
    if (str10 != null) {
      ((PhysicalConnection)localObject2).setDefaultNChar(str10.equalsIgnoreCase("true"));
    }
    if (str11 != null) {
      ((PhysicalConnection)localObject2).useFetchSizeWithLongColumn = str11.equalsIgnoreCase("true");
    }

    if (str16 != null) {
      ((PhysicalConnection)localObject2).setAccumulateBatchResult(str16.equalsIgnoreCase("true"));
    }

    String str20 = getSystemProperty("oracle.jdbc.J2EE13Compliant", null);
    String str19;
    if (str20 == null)
    {
      str19 = paramProperties.getProperty("oracle.jdbc.J2EE13Compliant");

      if (str19 == null)
      {
        str19 = "false";
      }

    }
    else
    {
      str19 = str20;
    }
    ((PhysicalConnection)localObject2).setJ2EE13Compliant(str19.equalsIgnoreCase("true"));

    ((PhysicalConnection)localObject2).disableDefineColumnType = str17.equalsIgnoreCase("true");

    ((PhysicalConnection)localObject2).convertNcharLiterals = str18.equalsIgnoreCase("true");

    localHashtable = null;

    ((PhysicalConnection)localObject2).protocolId = i;

    return (Connection)localObject2;
  }

  public Connection defaultConnection()
    throws SQLException
  {
    if ((defaultConn == null) || (defaultConn.isClosed()))
    {
      synchronized (OracleDriver.class)
      {
        if ((defaultConn == null) || (defaultConn.isClosed()))
        {
          defaultConn = DriverManager.getConnection("jdbc:oracle:kprb:");
        }
      }
    }

    return defaultConn;
  }

  private int oracleDriverExtensionTypeFromURL(String paramString)
  {
    int i = paramString.indexOf(':') + 1;

    if (i == 0) {
      return -2;
    }
    int j = paramString.indexOf(':', i);

    if (j == -1) {
      return -2;
    }
    if (!paramString.regionMatches(true, i, "oracle", 0, j - i))
    {
      return -2;
    }
    j++;

    int k = paramString.indexOf(':', j);

    if (k == -1) {
      return -3;
    }
    String str = paramString.substring(j, k);

    if (str.equals("thin")) {
      return 0;
    }
    if ((str.equals("oci8")) || (str.equals("oci"))) {
      return 2;
    }

    return -3;
  }

  public boolean acceptsURL(String paramString)
  {
    if (paramString.startsWith("jdbc:oracle:"))
    {
      return oracleDriverExtensionTypeFromURL(paramString) > -2;
    }

    return false;
  }

  public DriverPropertyInfo[] getPropertyInfo(String paramString, Properties paramProperties)
    throws SQLException
  {
    return new DriverPropertyInfo[0];
  }

  public int getMajorVersion()
  {
    return OracleDatabaseMetaData.getDriverMajorVersionInfo();
  }

  public int getMinorVersion()
  {
    return OracleDatabaseMetaData.getDriverMinorVersionInfo();
  }

  public boolean jdbcCompliant()
  {
    return true;
  }

  public String processSqlEscapes(String paramString)
    throws SQLException
  {
    OracleSql localOracleSql = new OracleSql(null);

    localOracleSql.initialize(paramString);

    return localOracleSql.parse(paramString);
  }

  private String parseLoginOption(String paramString, Properties paramProperties)
  {
    int j = 0;
    String str1 = null;
    String str2 = null;

    if (paramString == null) {
      return null;
    }
    int k = paramString.length();

    if (k == 0) {
      return null;
    }

    int i = paramString.indexOf('[');
    if (i > 0) {
      j = paramString.indexOf(']');
      str2 = paramString.substring(i + 1, j);
      str2 = str2.trim();

      if (str2.length() > 0) {
        paramProperties.put("PROXY_CLIENT_NAME", str2);
      }

      paramString = paramString.substring(0, i) + paramString.substring(j + 1, k);
    }

    String str3 = paramString.toLowerCase();

    i = str3.lastIndexOf(" as ");

    if ((i == -1) || (i < str3.lastIndexOf("\""))) {
      return paramString;
    }

    str1 = paramString.substring(0, i);

    i += 4;

    while ((i < k) && (str3.charAt(i) == ' ')) {
      i++;
    }
    if (i == k) {
      return paramString;
    }
    String str4 = str3.substring(i).trim();

    if (str4.length() > 0) {
      paramProperties.put("internal_logon", str4);
    }
    return str1;
  }

  private Hashtable parseUrl(String paramString)
    throws SQLException
  {
    Hashtable localHashtable = new Hashtable(5);
    int i = paramString.indexOf(':', paramString.indexOf(58) + 1) + 1;
    int j = paramString.length();

    if (i == j) {
      return localHashtable;
    }
    int k = paramString.indexOf(':', i);

    if (k == -1) {
      return localHashtable;
    }
    localHashtable.put("protocol", paramString.substring(i, k));

    int m = k + 1;
    int n = paramString.indexOf('/', m);

    int i1 = paramString.indexOf('@', m);

    if ((i1 > m) && (m > i) && (n == -1))
    {
      return null;
    }

    if (i1 == -1) {
      i1 = j;
    }
    if (n == -1) {
      n = i1;
    }
    if ((n < i1) && (n != m) && (i1 != m))
    {
      localHashtable.put("user", paramString.substring(m, n));
      localHashtable.put("password", paramString.substring(n + 1, i1));
    }

    if ((n <= i1) && ((n == m) || (i1 == m)))
    {
      if (i1 < j)
      {
        String str = paramString.substring(i1 + 1);
        String[] arrayOfString = getSecretStoreCredentials(str);
        if ((arrayOfString[0] != null) || (arrayOfString[1] != null))
        {
          localHashtable.put("user", arrayOfString[0]);
          localHashtable.put("password", arrayOfString[1]);
        }

      }

    }

    if (i1 < j) {
      localHashtable.put("database", paramString.substring(i1 + 1));
    }

    return localHashtable;
  }

  private String[] getSecretStoreCredentials(String paramString)
    throws SQLException
  {
    String[] arrayOfString = new String[2];
    arrayOfString[0] = null;
    arrayOfString[1] = null;

    if (walletLocation != null)
    {
      try
      {
        OracleWallet localOracleWallet = new OracleWallet();
        if (localOracleWallet.exists(walletLocation))
        {
          localOracleWallet.open(walletLocation, null);
          OracleSecretStore localOracleSecretStore = localOracleWallet.getSecretStore();

          if (localOracleSecretStore.containsAlias("oracle.security.client.default_username")) {
            arrayOfString[0] = new String(localOracleSecretStore.getSecret("oracle.security.client.default_username"));
          }
          if (localOracleSecretStore.containsAlias("oracle.security.client.default_password")) {
            arrayOfString[1] = new String(localOracleSecretStore.getSecret("oracle.security.client.default_password"));
          }

          Enumeration localEnumeration = localOracleWallet.getSecretStore().internalAliases();

          String str1 = null;
          while (localEnumeration.hasMoreElements())
          {
            str1 = (String)localEnumeration.nextElement();
            if ((!str1.startsWith("oracle.security.client.connect_string")) || 
              (!paramString.equalsIgnoreCase(new String(localOracleSecretStore.getSecret(str1))))) {
              continue;
            }
            String str2 = str1.substring("oracle.security.client.connect_string".length());
            arrayOfString[0] = new String(localOracleSecretStore.getSecret("oracle.security.client.username" + str2));

            arrayOfString[1] = new String(localOracleSecretStore.getSecret("oracle.security.client.password" + str2));
          }

        }

      }
      catch (NoClassDefFoundError localNoClassDefFoundError)
      {
        DatabaseError.throwSqlException(167);
      }
      catch (Exception localException)
      {
        if ((localException instanceof RuntimeException)) throw ((RuntimeException)localException);

        DatabaseError.throwSqlException(168);
      }

    }

    return arrayOfString;
  }

  public static String getCompileTime()
  {
    return "Wed_Jun_22_11:18:51_PDT_2005";
  }

  private String translateConnStr(String paramString)
    throws SQLException
  {
    int i = 0;
    int j = 0;

    if (paramString == null) {
      return paramString;
    }

    if (((i = paramString.indexOf(':')) == -1) || ((j = paramString.indexOf(':', i + 1)) == -1))
    {
      return paramString;
    }

    if (paramString.indexOf(':', j + 1) != -1)
    {
      DatabaseError.throwSqlException(67, paramString);
    }

    String str2 = paramString.substring(0, i);
    String str3 = paramString.substring(i + 1, j);
    String str4 = paramString.substring(j + 1, paramString.length());

    String str1 = "(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=" + str2 + ")(PORT=" + str3 + "))(CONNECT_DATA=(SID=" + str4 + ")))";

    return str1;
  }

  protected static String getSystemPropertyUserName()
  {
    return getSystemProperty("user.name", null);
  }

  protected static String getSystemPropertyV8Compatible()
  {
    return getSystemProperty("oracle.jdbc.V8Compatible", null);
  }

  protected static String getSystemPropertyPollInterval()
  {
    return getSystemProperty("oracle.jdbc.TimeoutPollInterval", "1000");
  }

  public static String getSystemPropertyFastConnectionFailover(String paramString)
  {
    return getSystemProperty("oracle.jdbc.FastConnectionFailover", paramString);
  }

  public static String getSystemPropertyJserverVersion()
  {
    return getSystemProperty("oracle.jserver.version", null);
  }

  private static String getSystemProperty(String paramString)
  {
    return getSystemProperty(paramString, null);
  }

  private static String getSystemProperty(String paramString1, String paramString2)
  {
    if (paramString1 != null)
    {
      String str1 = paramString1;
      String str2 = paramString2;
      String[] arrayOfString = { paramString2 };
      AccessController.doPrivileged(new PrivilegedAction(arrayOfString, str1, str2) { private final String[] val$rets;
        private final String val$fstr;
        private final String val$fdefaultValue;

        public Object run() { this.val$rets[0] = System.getProperty(this.val$fstr, this.val$fdefaultValue);
          return null;
        }
      });
      return arrayOfString[0];
    }

    return paramString2;
  }

  static
  {
    Timestamp localTimestamp = Timestamp.valueOf("2000-01-01 00:00:00.0");
    try
    {
      if (defaultDriver == null)
      {
        defaultDriver = new OracleDriver();

        DriverManager.registerDriver(defaultDriver);
      }

    }
    catch (RuntimeException localRuntimeException)
    {
    }
    catch (SQLException localSQLException)
    {
    }

    _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleDriver
 * JD-Core Version:    0.6.0
 */