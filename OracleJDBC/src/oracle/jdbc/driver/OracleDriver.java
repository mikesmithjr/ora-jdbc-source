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
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleDatabaseMetaData;
import oracle.security.pki.OracleSecretStore;
import oracle.security.pki.OracleWallet;

public class OracleDriver implements Driver {
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
    private static final String[] driverExtensionClassNames = {
            "oracle.jdbc.driver.T4CDriverExtension", "oracle.jdbc.driver.T4CDriverExtension",
            "oracle.jdbc.driver.T2CDriverExtension", "oracle.jdbc.driver.T2SDriverExtension" };
    private static Properties driverAccess;
    protected static Connection defaultConn = null;
    private static OracleDriver defaultDriver = null;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_;
    public static boolean TRACE;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public Connection connect(String url, Properties info) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleDriver.connect(url=" + url + ", info)",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        if (url.regionMatches(0, "jdbc:default:connection", 0, 23)) {
            String url1 = "jdbc:oracle:kprb";
            int l = url.length();

            if (l > 23)
                url = url1.concat(url.substring(23, url.length()));
            else {
                url = url1.concat(":");
            }
            url1 = null;
        }

        int extensionType = oracleDriverExtensionTypeFromURL(url);

        if (extensionType == -2) {
            return null;
        }
        if (extensionType == -3) {
            DatabaseError.throwSqlException(67);
        }
        OracleDriverExtension driverExtension = null;

        driverExtension = this.driverExtensions[extensionType];

        if (driverExtension == null) {
            try {
                synchronized (this) {
                    if (driverExtension == null) {
                        driverExtension = (OracleDriverExtension) Class
                                .forName(driverExtensionClassNames[extensionType]).newInstance();

                        this.driverExtensions[extensionType] = driverExtension;
                    } else {
                        driverExtension = this.driverExtensions[extensionType];
                    }

                }

            } catch (Exception e) {
                throw new SQLException(e.toString());
            }

        }

        walletLocation = info.getProperty("oracle.net.wallet_location");

        if (walletLocation == null) {
            walletLocation = getSystemProperty("oracle.net.wallet_location", null);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OracleDriver.connect() walletLocation:("
                    + walletLocation + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Hashtable url_properties = parseUrl(url);

        if (url_properties == null) {
            return null;
        }

        String user = info.getProperty("user");
        String password = info.getProperty("password");
        String database = info.getProperty("database");

        if (database == null) {
            database = info.getProperty("server");
        }
        if (user == null) {
            user = (String) url_properties.get("user");
        }
        user = parseLoginOption(user, info);

        if (password == null) {
            password = (String) url_properties.get("password");
        }
        if (database == null) {
            database = (String) url_properties.get("database");
        }
        String protocol = (String) url_properties.get("protocol");

        info.put("protocol", protocol);

        if (protocol == null) {
            DatabaseError.throwSqlException(40, "Protocol is not specified in URL");

            return null;
        }

        if ((protocol.equals("oci8")) || (protocol.equals("oci"))) {
            database = translateConnStr(database);
        }
        String tcp_nodelay_value = info.getProperty("oracle.jdbc.TcpNoDelay");

        if (tcp_nodelay_value == null) {
            tcp_nodelay_value = getSystemProperty("oracle.jdbc.TcpNoDelay", null);
        }
        if ((tcp_nodelay_value != null) && (tcp_nodelay_value.equalsIgnoreCase("true"))) {
            info.put("TCP.NODELAY", "YES");
        }
        String read_timeout_value = info.getProperty("oracle.jdbc.ReadTimeout");
        if (read_timeout_value != null) {
            info.put("oracle.net.READ_TIMEOUT", read_timeout_value);
        }

        int login_timeout_value = DriverManager.getLoginTimeout();

        if ((login_timeout_value != 0) && (info.get("oracle.net.CONNECT_TIMEOUT") == null)) {
            info.put("oracle.net.CONNECT_TIMEOUT", "" + login_timeout_value * 1000);
        }

        String prefetch = info.getProperty("prefetch");

        if (prefetch == null) {
            prefetch = info.getProperty("rowPrefetch");
        }
        if (prefetch == null) {
            prefetch = info.getProperty("defaultRowPrefetch");
        }
        if ((prefetch != null) && (Integer.parseInt(prefetch) <= 0)) {
            prefetch = null;
        }
        String batch = info.getProperty("batch");

        if (batch == null) {
            batch = info.getProperty("executeBatch");
        }
        if (batch == null) {
            batch = info.getProperty("defaultExecuteBatch");
        }
        if ((batch != null) && (Integer.parseInt(batch) <= 0)) {
            batch = null;
        }

        String defaultnchar = info.getProperty("defaultNChar");

        if (defaultnchar == null) {
            defaultnchar = getSystemProperty("oracle.jdbc.defaultNChar", null);
        }
        String useFetchSizeWithLongColumn = info.getProperty("useFetchSizeWithLongColumn");

        if (useFetchSizeWithLongColumn == null) {
            useFetchSizeWithLongColumn = getSystemProperty(
                                                           "oracle.jdbc.useFetchSizeWithLongColumn",
                                                           null);
        }

        String remarks = info.getProperty("remarks");

        if (remarks == null) {
            remarks = info.getProperty("remarksReporting");
        }

        String synonyms = info.getProperty("synonyms");

        if (synonyms == null) {
            synonyms = info.getProperty("includeSynonyms");
        }

        String restrict_getTables = info.getProperty("restrictGetTables");

        String fixedString = info.getProperty("fixedString");

        String batching_type = info.getProperty("AccumulateBatchResult");

        if (batching_type == null) {
            batching_type = "true";
        }

        String disableDefineColumnType = info.getProperty("disableDefineColumnType");

        if (disableDefineColumnType == null) {
            disableDefineColumnType = "false";
        }
        String convertNcharLiterals = info.getProperty("oracle.jdbc.convertNcharLiterals");

        if (convertNcharLiterals == null) {
            convertNcharLiterals = getSystemProperty("oracle.jdbc.convertNcharLiterals", "false");
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "user=" + user + ", password=" + password
                    + ", database=" + database + ", protocol=" + protocol + ", prefetch="
                    + prefetch + ", batch=" + batch + ", accumulate batch result =" + batching_type
                    + ", remarks=" + remarks + ", synonyms=" + synonyms, this);

            OracleLog.recursiveTrace = false;
        }

        Enumeration enumdrivers = DriverManager.getDrivers();

        while (enumdrivers.hasMoreElements()) {
            Driver driverobj = (Driver) enumdrivers.nextElement();

            if ((driverobj instanceof OracleDriver)) {
                break;
            }
        }
        while (enumdrivers.hasMoreElements()) {
            Driver driverobj = (Driver) enumdrivers.nextElement();

            if ((driverobj instanceof OracleDriver)) {
                DriverManager.deregisterDriver(driverobj);
            }

        }

        PhysicalConnection conn = (PhysicalConnection) driverExtension
                .getConnection(url, user, password, database, info);

        if (prefetch != null) {
            conn.setDefaultRowPrefetch(Integer.parseInt(prefetch));
        }
        if (batch != null) {
            conn.setDefaultExecuteBatch(Integer.parseInt(batch));
        }
        if (remarks != null) {
            conn.setRemarksReporting(remarks.equalsIgnoreCase("true"));
        }
        if (synonyms != null) {
            conn.setIncludeSynonyms(synonyms.equalsIgnoreCase("true"));
        }
        if (restrict_getTables != null) {
            conn.setRestrictGetTables(restrict_getTables.equalsIgnoreCase("true"));
        }
        if (fixedString != null) {
            conn.setDefaultFixedString(fixedString.equalsIgnoreCase("true"));
        }
        if (defaultnchar != null) {
            conn.setDefaultNChar(defaultnchar.equalsIgnoreCase("true"));
        }
        if (useFetchSizeWithLongColumn != null) {
            conn.useFetchSizeWithLongColumn = useFetchSizeWithLongColumn.equalsIgnoreCase("true");
        }

        if (batching_type != null) {
            conn.setAccumulateBatchResult(batching_type.equalsIgnoreCase("true"));
        }

        String system_j2ee_compliance_value = getSystemProperty("oracle.jdbc.J2EE13Compliant", null);
        String j2ee_compliance_value;
        if (system_j2ee_compliance_value == null) {
            j2ee_compliance_value = info.getProperty("oracle.jdbc.J2EE13Compliant");

            if (j2ee_compliance_value == null) {
                j2ee_compliance_value = "false";
            }

        } else {
            j2ee_compliance_value = system_j2ee_compliance_value;
        }
        conn.setJ2EE13Compliant(j2ee_compliance_value.equalsIgnoreCase("true"));

        conn.disableDefineColumnType = disableDefineColumnType.equalsIgnoreCase("true");

        conn.convertNcharLiterals = convertNcharLiterals.equalsIgnoreCase("true");

        url_properties = null;

        conn.protocolId = extensionType;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleDriver.connect(url,info):return", this);

            OracleLog.recursiveTrace = false;
        }

        return conn;
    }

    public Connection defaultConnection() throws SQLException {
        if ((defaultConn == null) || (defaultConn.isClosed())) {
            synchronized (OracleDriver.class) {
                if ((defaultConn == null) || (defaultConn.isClosed())) {
                    defaultConn = DriverManager.getConnection("jdbc:oracle:kprb:");
                }
            }
        }

        return defaultConn;
    }

    private int oracleDriverExtensionTypeFromURL(String url) {
        int first_colon = url.indexOf(':') + 1;

        if (first_colon == 0) {
            return -2;
        }
        int second_colon = url.indexOf(':', first_colon);

        if (second_colon == -1) {
            return -2;
        }
        if (!url.regionMatches(true, first_colon, "oracle", 0, second_colon - first_colon)) {
            return -2;
        }
        second_colon++;

        int third_colon = url.indexOf(':', second_colon);

        if (third_colon == -1) {
            return -3;
        }
        String sub_name = url.substring(second_colon, third_colon);

        if (sub_name.equals("thin")) {
            return 0;
        }
        if ((sub_name.equals("oci8")) || (sub_name.equals("oci"))) {
            return 2;
        }

        return -3;
    }

    public boolean acceptsURL(String url) {
        if (url.startsWith("jdbc:oracle:")) {
            return oracleDriverExtensionTypeFromURL(url) > -2;
        }

        return false;
    }

    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    public int getMajorVersion() {
        return OracleDatabaseMetaData.getDriverMajorVersionInfo();
    }

    public int getMinorVersion() {
        return OracleDatabaseMetaData.getDriverMinorVersionInfo();
    }

    public boolean jdbcCompliant() {
        return true;
    }

    public String processSqlEscapes(String sqlString) throws SQLException {
        OracleSql sql = new OracleSql(null);

        sql.initialize(sqlString);

        return sql.parse(sqlString);
    }

    private String parseLoginOption(String user, Properties info) {
        int j = 0;
        String userOnly = null;
        String proxyClient = null;

        if (user == null) {
            return null;
        }
        int len = user.length();

        if (len == 0) {
            return null;
        }

        int i = user.indexOf('[');
        if (i > 0) {
            j = user.indexOf(']');
            proxyClient = user.substring(i + 1, j);
            proxyClient = proxyClient.trim();

            if (proxyClient.length() > 0) {
                info.put("PROXY_CLIENT_NAME", proxyClient);
            }

            user = user.substring(0, i) + user.substring(j + 1, len);
        }

        String lowerCaseUser = user.toLowerCase();

        i = lowerCaseUser.lastIndexOf(" as ");

        if ((i == -1) || (i < lowerCaseUser.lastIndexOf("\""))) {
            return user;
        }

        userOnly = user.substring(0, i);

        i += 4;

        while ((i < len) && (lowerCaseUser.charAt(i) == ' ')) {
            i++;
        }
        if (i == len) {
            return user;
        }
        String loginMode = lowerCaseUser.substring(i).trim();

        if (loginMode.length() > 0) {
            info.put("internal_logon", loginMode);
        }
        return userOnly;
    }

    private Hashtable parseUrl(String url) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OracleDriver.parseUrl(url=" + url + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Hashtable result = new Hashtable(5);
        int sub_sub_index = url.indexOf(':', url.indexOf(58) + 1) + 1;
        int end = url.length();

        if (sub_sub_index == end) {
            return result;
        }
        int next_colon_index = url.indexOf(':', sub_sub_index);

        if (next_colon_index == -1) {
            return result;
        }
        result.put("protocol", url.substring(sub_sub_index, next_colon_index));

        int user = next_colon_index + 1;
        int slash = url.indexOf('/', user);

        int at_sign = url.indexOf('@', user);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "sub_sub_index=" + sub_sub_index + ", end="
                    + end + ", next_colon_index=" + next_colon_index + ", user=" + user
                    + ", slash=" + slash + ", at_sign=" + at_sign, this);

            OracleLog.recursiveTrace = false;
        }

        if ((at_sign > user) && (user > sub_sub_index) && (slash == -1)) {
            return null;
        }

        if (at_sign == -1) {
            at_sign = end;
        }
        if (slash == -1) {
            slash = at_sign;
        }
        if ((slash < at_sign) && (slash != user) && (at_sign != user)) {
            result.put("user", url.substring(user, slash));
            result.put("password", url.substring(slash + 1, at_sign));
        }

        if ((slash <= at_sign) && ((slash == user) || (at_sign == user))) {
            if (at_sign < end) {
                String alias = url.substring(at_sign + 1);
                String[] tmpCreds = getSecretStoreCredentials(alias);
                if ((tmpCreds[0] != null) || (tmpCreds[1] != null)) {
                    result.put("user", tmpCreds[0]);
                    result.put("password", tmpCreds[1]);
                }

            }

        }

        if (at_sign < end) {
            result.put("database", url.substring(at_sign + 1));
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OracleDriver.parseUrl(url):return", this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    private String[] getSecretStoreCredentials(String connectString) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleDriver.getSecretStoreCredentials("
                    + connectString + ")", this);

            OracleLog.recursiveTrace = false;
        }

        String[] userPwd = new String[2];
        userPwd[0] = null;
        userPwd[1] = null;

        if (walletLocation != null) {
            try {
                OracleWallet wallet = new OracleWallet();
                if (wallet.exists(walletLocation)) {
                    wallet.open(walletLocation, null);
                    OracleSecretStore secretStore = wallet.getSecretStore();

                    if (secretStore.containsAlias("oracle.security.client.default_username")) {
                        userPwd[0] = new String(secretStore
                                .getSecret("oracle.security.client.default_username"));
                    }
                    if (secretStore.containsAlias("oracle.security.client.default_password")) {
                        userPwd[1] = new String(secretStore
                                .getSecret("oracle.security.client.default_password"));
                    }

                    Enumeration list = wallet.getSecretStore().internalAliases();

                    String alias = null;
                    while (list.hasMoreElements()) {
                        alias = (String) list.nextElement();
                        if ((!alias.startsWith("oracle.security.client.connect_string"))
                                || (!connectString.equalsIgnoreCase(new String(secretStore
                                        .getSecret(alias))))) {
                            continue;
                        }
                        String idx = alias.substring("oracle.security.client.connect_string"
                                .length());
                        userPwd[0] = new String(secretStore
                                .getSecret("oracle.security.client.username" + idx));

                        userPwd[1] = new String(secretStore
                                .getSecret("oracle.security.client.password" + idx));
                    }

                }

            } catch (NoClassDefFoundError ndef) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger
                            .log(Level.SEVERE,
                                 "OracleDriver.getSecretStoreCredentials: oraclepki.jar not found",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(167);
            } catch (Exception s) {
                if ((s instanceof RuntimeException))
                    throw ((RuntimeException) s);

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger
                            .log(
                                 Level.SEVERE,
                                 "OracleDriver.getSecretStoreCredentials: Caught exception from wallet",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                DatabaseError.throwSqlException(168);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleDriver.getSecretStoreCredentials:return:("
                                               + userPwd[0] + ", *****)", this);

            OracleLog.recursiveTrace = false;
        }

        return userPwd;
    }

    public static String getCompileTime() {
        return "Wed_Jun_22_11:30:51_PDT_2005";
    }

    private String translateConnStr(String database) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleDriver.translateConnStr(database ="
                    + database + ")", this);

            OracleLog.recursiveTrace = false;
        }

        int fColon = 0;
        int sColon = 0;

        if (database == null) {
            return database;
        }

        if (((fColon = database.indexOf(':')) == -1)
                || ((sColon = database.indexOf(':', fColon + 1)) == -1)) {
            return database;
        }

        if (database.indexOf(':', sColon + 1) != -1) {
            DatabaseError.throwSqlException(67, database);
        }

        String host = database.substring(0, fColon);
        String port = database.substring(fColon + 1, sColon);
        String sid = database.substring(sColon + 1, database.length());

        String translate_database = "(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=" + host + ")(PORT="
                + port + "))(CONNECT_DATA=(SID=" + sid + ")))";

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "translate_database=" + translate_database, this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleDriver.translateConnStr(database):return", this);

            OracleLog.recursiveTrace = false;
        }

        return translate_database;
    }

    protected static String getSystemPropertyUserName() {
        return getSystemProperty("user.name", null);
    }

    protected static String getSystemPropertyV8Compatible() {
        return getSystemProperty("oracle.jdbc.V8Compatible", null);
    }

    protected static String getSystemPropertyPollInterval() {
        return getSystemProperty("oracle.jdbc.TimeoutPollInterval", "1000");
    }

    public static String getSystemPropertyFastConnectionFailover(String defaultValue) {
        return getSystemProperty("oracle.jdbc.FastConnectionFailover", defaultValue);
    }

    public static String getSystemPropertyJserverVersion() {
        return getSystemProperty("oracle.jserver.version", null);
    }

    private static String getSystemProperty(String str) {
        return getSystemProperty(str, null);
    }

    private static String getSystemProperty(String str, String defaultValue) {
        if (str != null) {
            final String fstr = str;
            final String fdefaultValue = defaultValue;
            final String[] rets = { defaultValue };
            AccessController.doPrivileged(new PrivilegedAction() {
                public Object run() {
                    rets[0] = System.getProperty(fstr, fdefaultValue);
                    return null;
                }
            });
            return rets[0];
        }

        return defaultValue;
    }

    static {
        Timestamp ignore = Timestamp.valueOf("2000-01-01 00:00:00.0");
        try {
            if (defaultDriver == null) {
                defaultDriver = new OracleDriver();

                DriverManager.registerDriver(defaultDriver);
            }

        } catch (RuntimeException e) {
        } catch (SQLException e) {
        }

        _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

        TRACE = false;
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleDriver"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleDriver JD-Core Version: 0.6.0
 */