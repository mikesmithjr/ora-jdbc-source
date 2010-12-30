package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.OracleOCIFailover;
import oracle.jdbc.internal.OracleConnection;
import oracle.jdbc.oracore.OracleTypeADT;
import oracle.jdbc.oracore.OracleTypeCLOB;
import oracle.jdbc.pool.OracleOCIConnectionPool;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.BfileDBAccess;
import oracle.sql.BlobDBAccess;
import oracle.sql.CLOB;
import oracle.sql.ClobDBAccess;
import oracle.sql.LobPlsqlUtil;
import oracle.sql.SQLName;
import oracle.sql.converter.CharacterSetMetaData;

public class T2CConnection extends PhysicalConnection implements BfileDBAccess, BlobDBAccess,
        ClobDBAccess {
    short[] queryMetaData1 = null;
    byte[] queryMetaData2 = null;
    int queryMetaData1Offset = 0;
    int queryMetaData2Offset = 0;
    private String password;
    int fatalErrorNumber = 0;
    String fatalErrorMessage = null;
    static final int QMD_dbtype = 0;
    static final int QMD_dbsize = 1;
    static final int QMD_nullok = 2;
    static final int QMD_precision = 3;
    static final int QMD_scale = 4;
    static final int QMD_formOfUse = 5;
    static final int QMD_columnNameLength = 6;
    static final int QMD_tdo0 = 7;
    static final int QMD_tdo1 = 8;
    static final int QMD_tdo2 = 9;
    static final int QMD_tdo3 = 10;
    static final int QMD_charLength = 11;
    static final int QMD_typeNameLength = 12;
    static final int T2C_LOCATOR_MAX_LEN = 16;
    static final int T2C_LINEARIZED_LOCATOR_MAX_LEN = 4000;
    static final int T2C_LINEARIZED_BFILE_LOCATOR_MAX_LEN = 530;
    static final int METADATA1_INDICES_PER_COLUMN = 13;
    protected static final int SIZEOF_QUERYMETADATA2 = 8;
    int queryMetaData1Size = 100;
    int queryMetaData2Size = 800;
    long m_nativeState;
    short m_clientCharacterSet;
    byte byteAlign;
    private static final int EOJ_SUCCESS = 0;
    private static final int EOJ_ERROR = -1;
    private static final int EOJ_WARNING = 1;
    private static final String OCILIBRARY = "ocijdbc10";
    private int logon_mode = 0;
    static final int LOGON_MODE_DEFAULT = 0;
    static final int LOGON_MODE_SYSDBA = 2;
    static final int LOGON_MODE_SYSOPER = 4;
    static final int LOGON_MODE_CONNECTION_POOL = 5;
    static final int LOGON_MODE_CONNPOOL_CONNECTION = 6;
    static final int LOGON_MODE_CONNPOOL_PROXY_CONNECTION = 7;
    static final int LOGON_MODE_CONNPOOL_ALIASED_CONNECTION = 8;
    static final int T2C_PROXYTYPE_NONE = 0;
    static final int T2C_PROXYTYPE_USER_NAME = 1;
    static final int T2C_PROXYTYPE_DISTINGUISHED_NAME = 2;
    static final int T2C_PROXYTYPE_CERTIFICATE = 3;
    private static boolean isLibraryLoaded;
    OracleOCIFailover appCallback = null;
    Object appCallbackObject = null;
    private Properties nativeInfo;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    protected T2CConnection(String ur, String us, String p, String db, Properties info,
            OracleDriverExtension ext) throws SQLException {
        super(ur, us, p, db, info, ext);

        initialize();
    }

    final void initializePassword(String p) throws SQLException {
        this.password = p;
    }

    protected void initialize() {
        allocQueryMetaDataBuffers();
    }

    private void allocQueryMetaDataBuffers() {
        this.queryMetaData1Offset = 0;
        this.queryMetaData1 = new short[this.queryMetaData1Size * 13];

        this.queryMetaData2Offset = 0;
        this.queryMetaData2 = new byte[this.queryMetaData2Size];

        this.namedTypeAccessorByteLen = 0;
        this.refTypeAccessorByteLen = 0;
    }

    void reallocateQueryMetaData(int numColumns, int charSize) {
        this.queryMetaData1 = null;
        this.queryMetaData2 = null;

        this.queryMetaData1Size = Math.max(numColumns, this.queryMetaData1Size);
        this.queryMetaData2Size = Math.max(charSize, this.queryMetaData2Size);

        allocQueryMetaDataBuffers();
    }

    protected void logon() throws SQLException {
        if (this.database == null) {
            DatabaseError.throwSqlException(64);
        }
        if (!isLibraryLoaded) {
            loadNativeLibrary(this.connectionProperties);
        }

        if (this.connectionProperties.getProperty("is_connection_pooling") == "true") {
            processOCIConnectionPooling();
        } else {
            long OCISvcCtxHandle = 0L;
            long OCIEnvHandle = 0L;
            long OCIErrHandle = 0L;
            String svcCtxString;
            String envString;
            if (((svcCtxString = this.connectionProperties.getProperty("OCISvcCtxHandle")) != null)
                    && ((envString = this.connectionProperties.getProperty("OCIEnvHandle")) != null)) {
                OCISvcCtxHandle = Long.parseLong(svcCtxString);
                OCIEnvHandle = Long.parseLong(envString);
                String errString;
                if ((errString = this.connectionProperties.getProperty("OCIErrHandle")) != null) {
                    OCIErrHandle = Long.parseLong(errString);
                }
                String sDriverCharSetId;
                if ((sDriverCharSetId = this.connectionProperties
                        .getProperty("JDBCDriverCharSetId")) != null) {
                    this.m_clientCharacterSet = new Integer(sDriverCharSetId).shortValue();
                } else {
                    DatabaseError.throwSqlException(89);
                }

                this.conversion = new DBConversion(this.m_clientCharacterSet,
                        this.m_clientCharacterSet, this.m_clientCharacterSet);

                short[] csids = new short[3];

                this.sqlWarning = checkError(
                                             t2cUseConnection(this.m_nativeState, OCIEnvHandle,
                                                              OCISvcCtxHandle, OCIErrHandle, csids),
                                             this.sqlWarning);

                this.conversion = new DBConversion(csids[0], this.m_clientCharacterSet, csids[1]);
                this.byteAlign = (byte) (csids[2] & 0xFF);

                return;
            }

            String mode = this.connectionProperties.getProperty("internal_logon");

            if (mode == null)
                this.logon_mode = 0;
            else if (mode.equalsIgnoreCase("SYSDBA"))
                this.logon_mode = 2;
            else if (mode.equalsIgnoreCase("SYSOPER")) {
                this.logon_mode = 4;
            }
            String propNlsLangBackdoor = this.connectionProperties
                    .getProperty("oracle.jdbc.ociNlsLangBackwardCompatible");

            byte[] l_userName = null;
            byte[] l_password = null;
            String newPassword = null;
            byte[] newPasswordBytes = new byte[0];
            if (this.connectionProperties != null) {
                newPassword = (String) this.connectionProperties.get("OCINewPassword");
            }

            if ((propNlsLangBackdoor != null) && (propNlsLangBackdoor.equalsIgnoreCase("true"))) {
                this.m_clientCharacterSet = getDriverCharSetIdFromNLS_LANG(this.connectionProperties);
            } else {
                this.m_clientCharacterSet = getClientCharSetId();
            }

            if (newPassword != null) {
                newPasswordBytes = DBConversion.stringToAsciiBytes(newPassword);
            }
            l_userName = this.user == null ? new byte[0] : DBConversion
                    .stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

            l_password = this.password == null ? new byte[0] : DBConversion
                    .stringToAsciiBytes(this.password);

            byte[] l_database = DBConversion.stringToAsciiBytes(this.database);

            short[] csids = new short[3];
            byte[] nlslanguage = CharacterSetMetaData.getNLSLanguage(Locale.getDefault())
                    .getBytes();

            byte[] nlsterritory = CharacterSetMetaData.getNLSTerritory(Locale.getDefault())
                    .getBytes();

            this.conversion = new DBConversion(this.m_clientCharacterSet,
                    this.m_clientCharacterSet, this.m_clientCharacterSet);

            if (this.m_nativeState == 0L) {
                this.sqlWarning = checkError(t2cCreateState(l_userName, l_userName.length,
                                                            l_password, l_password.length,
                                                            newPasswordBytes,
                                                            newPasswordBytes.length, l_database,
                                                            l_database.length,
                                                            this.m_clientCharacterSet,
                                                            this.logon_mode, csids, nlslanguage,
                                                            nlsterritory), this.sqlWarning);
            } else {
                this.sqlWarning = checkError(t2cLogon(this.m_nativeState, l_userName,
                                                      l_userName.length, l_password,
                                                      l_password.length, newPasswordBytes,
                                                      newPasswordBytes.length, l_database,
                                                      l_database.length, this.logon_mode, csids,
                                                      nlslanguage, nlsterritory), this.sqlWarning);
            }

            this.conversion = new DBConversion(csids[0], this.m_clientCharacterSet, csids[1]);
            this.byteAlign = (byte) (csids[2] & 0xFF);
        }
    }

    protected void logoff() throws SQLException {
        try {
            if (this.m_nativeState != 0L) {
                checkError(t2cLogoff(this.m_nativeState));
            }

        } catch (NullPointerException ea) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINE, "T2CConnection.logoff() got NullPointerException, " + ea,
                             this);

                OracleLog.recursiveTrace = false;
            }
        }
    }

    public void open(OracleStatement stmt) throws SQLException {
        byte[] l_sqlString = stmt.sqlObject.getSql(stmt.processEscapes, stmt.convertNcharLiterals)
                .getBytes();

        checkError(t2cCreateStatement(this.m_nativeState, 0L, l_sqlString, l_sqlString.length,
                                      stmt, false, stmt.rowPrefetch));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CConnection.open(" + stmt + "), c_state = 0x"
                    + Long.toString(stmt.c_state, 16), this);

            OracleLog.recursiveTrace = false;
        }
    }

    void doCancel() throws SQLException {
        checkError(t2cCancel(this.m_nativeState));
    }

    native int t2cAbort(long paramLong);

    void doAbort() throws SQLException {
        checkError(t2cAbort(this.m_nativeState));

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.doAbort", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected void doSetAutoCommit(boolean autoCommit) throws SQLException {
        checkError(t2cSetAutoCommit(this.m_nativeState, autoCommit));
    }

    protected void doCommit() throws SQLException {
        checkError(t2cCommit(this.m_nativeState));
    }

    protected void doRollback() throws SQLException {
        checkError(t2cRollback(this.m_nativeState));
    }

    protected String doGetDatabaseProductVersion() throws SQLException {
        byte[] l_version = t2cGetProductionVersion(this.m_nativeState);

        return this.conversion.CharBytesToString(l_version, l_version.length);
    }

    protected short doGetVersionNumber() throws SQLException {
        short version_num = 0;
        try {
            String ver_str = doGetDatabaseProductVersion();

            StringTokenizer st = new StringTokenizer(ver_str.trim(), " .", false);
            String tk = null;
            int ct = 0;
            short num = 0;

            while (st.hasMoreTokens()) {
                tk = st.nextToken();
                try {
                    num = Integer.decode(tk).shortValue();
                    version_num = (short) (version_num * 10 + num);
                    ct++;

                    if (ct == 4) {
                        break;
                    }

                } catch (NumberFormatException nfe) {
                }

            }

        } catch (NoSuchElementException ne) {
        }

        if (version_num == -1) {
            version_num = 0;
        }

        return version_num;
    }

    public ClobDBAccess createClobDBAccess() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CConnection.createClobDBAccess()", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public BlobDBAccess createBlobDBAccess() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createBlobDBAccess()", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public BfileDBAccess createBfileDBAccess() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createBfileDBAccess()", this);

            OracleLog.recursiveTrace = false;
        }

        return this;
    }

    public CLOB createClob(byte[] locator_bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createClob()"
                    + OracleLog.bytesToPrintableForm("locator_bytes=", locator_bytes), this);

            OracleLog.recursiveTrace = false;
        }

        return new CLOB(this, locator_bytes);
    }

    public CLOB createClob(byte[] locator_bytes, short csform) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createClob() csform = " + csform
                    + OracleLog.bytesToPrintableForm("locator_bytes=", locator_bytes), this);

            OracleLog.recursiveTrace = false;
        }

        return new CLOB(this, locator_bytes, csform);
    }

    public BLOB createBlob(byte[] locator_bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createBlob()"
                    + OracleLog.bytesToPrintableForm("locator_bytes=", locator_bytes), this);

            OracleLog.recursiveTrace = false;
        }

        return new BLOB(this, locator_bytes);
    }

    public BFILE createBfile(byte[] locator_bytes) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createClob()", this);

            OracleLog.recursiveTrace = false;
        }

        return new BFILE(this, locator_bytes);
    }

    protected SQLWarning checkError(int errStatus) throws SQLException {
        return checkError(errStatus, null);
    }

    protected SQLWarning checkError(int errStatus, SQLWarning warning) throws SQLException {
        switch (errStatus) {
        case 0:
            break;
        case -1:
        case 1:
            T2CError l_error = new T2CError();

            int l_status = -1;

            if (this.m_nativeState != 0L) {
                l_status = t2cDescribeError(this.m_nativeState, l_error, l_error.m_errorMessage);
            } else if (this.fatalErrorNumber != 0) {
                String l_sqlstate = DatabaseError.ErrorToSQLState(this.fatalErrorNumber);

                DatabaseError.throwSqlException(this.fatalErrorMessage, l_sqlstate,
                                                l_error.m_errorNumber);
            } else {
                DatabaseError.throwSqlException(8);
            }

            switch (l_error.m_errorNumber) {
            case 28:
            case 600:
            case 1012:
            case 1041:
            case 3113:
            case 3114:
                internalClose();
            }

            if (l_status == -1) {
                DatabaseError.throwSqlException(1, "Fetch error message failed!");
            }

            int msgLength = 0;

            while ((msgLength < l_error.m_errorMessage.length)
                    && (l_error.m_errorMessage[msgLength] != 0)) {
                msgLength++;
            }
            String l_reason = this.conversion.CharBytesToString(l_error.m_errorMessage, msgLength,
                                                                true);

            String l_sqlstate = DatabaseError.ErrorToSQLState(l_error.m_errorNumber);

            if (errStatus == -1) {
                DatabaseError.throwSqlException(l_reason, l_sqlstate, l_error.m_errorNumber);
            } else {
                warning = DatabaseError.addSqlWarning(warning, l_reason, l_sqlstate,
                                                      l_error.m_errorNumber);
            }

            break;
        }

        return warning;
    }

    OracleStatement RefCursorBytesToStatement(byte[] bytes, OracleStatement parent)
            throws SQLException {
        T2CStatement stmt = new T2CStatement(this, 1, this.defaultRowPrefetch, -1, -1);

        stmt.needToParse = false;
        stmt.serverCursor = true;
        stmt.isOpen = true;
        stmt.processEscapes = false;

        stmt.prepareForNewResults(true, false);
        stmt.sqlObject.initialize("select unknown as ref cursor from whatever");

        stmt.sqlKind = 0;

        checkError(t2cCreateStatement(this.m_nativeState, parent.c_state, bytes, bytes.length,
                                      stmt, true, this.defaultRowPrefetch));

        return stmt;
    }

    public void getForm(OracleTypeADT otypeADT, OracleTypeCLOB otype, int attrIndex)
            throws SQLException {
        byte value = 0;

        if (otype != null) {
            String[] schema = new String[1];
            String[] type = new String[1];

            SQLName.parse(otypeADT.getFullName(), schema, type, true);

            String fullname = "\"" + schema[0] + "\".\"" + type[0] + "\"";

            byte[] utf8bytes = this.conversion.StringToCharBytes(fullname);

            int formOfUse = t2cGetFormOfUse(this.m_nativeState, otype, utf8bytes, utf8bytes.length,
                                            attrIndex);

            if (formOfUse < 0) {
                checkError(formOfUse);
            }

            otype.setForm(formOfUse);
        } else if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.SEVERE, "T2CConnecion.getFrom(otype, attrIndex = "
                    + attrIndex + "): otype is null", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public long getTdoCState(String schemaName, String typeName) throws SQLException {
        String fullNameWithQuotes = "\"" + schemaName + "\".\"" + typeName + "\"";
        byte[] dbcsbytes = this.conversion.StringToCharBytes(fullNameWithQuotes);
        int[] err = new int[1];
        long tdoCState = t2cGetTDO(this.m_nativeState, dbcsbytes, dbcsbytes.length, err);
        if (tdoCState == 0L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger.log(Level.SEVERE, "T2CConnecion.get_tdoCState(fullName = "
                        + fullNameWithQuotes + ") got error " + err[0], this);

                OracleLog.recursiveTrace = false;
            }

            checkError(err[0]);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnecion.get_tdoCState(fullName = "
                    + fullNameWithQuotes + ") got : " + tdoCState, this);

            OracleLog.recursiveTrace = false;
        }

        return tdoCState;
    }

    /** @deprecated */
    public Properties getDBAccessProperties() throws SQLException {
        return getOCIHandles();
    }

    public synchronized Properties getOCIHandles() throws SQLException {
        if (this.nativeInfo == null) {
            long[] handles = new long[3];

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger.log(Level.FINE, "T2CConnection.getOCIHandles()", this);

                OracleLog.recursiveTrace = false;
            }

            checkError(t2cGetHandles(this.m_nativeState, handles));

            this.nativeInfo = new Properties();

            this.nativeInfo.put("OCIEnvHandle", String.valueOf(handles[0]));
            this.nativeInfo.put("OCISvcCtxHandle", String.valueOf(handles[1]));
            this.nativeInfo.put("OCIErrHandle", String.valueOf(handles[2]));
            this.nativeInfo.put("ClientCharSet", String.valueOf(this.m_clientCharacterSet));
        }

        return this.nativeInfo;
    }

    public Properties getServerSessionInfo() throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }
        Properties info = new Properties();
        checkError(t2cGetServerSessionInfo(this.m_nativeState, info));

        return info;
    }

    public Properties getConnectionPoolInfo() throws SQLException {
        if (this.lifecycle != 1)
            DatabaseError.throwSqlException(8);
        Properties info = new Properties();

        checkError(t2cGetConnPoolInfo(this.m_nativeState, info));

        return info;
    }

    public void setConnectionPoolInfo(int cpool_min, int cpool_max, int cpool_incr,
            int cpool_timeout, int cpool_nowait, int cpool_dist_txn) throws SQLException {
        checkError(t2cSetConnPoolInfo(this.m_nativeState, cpool_min, cpool_max, cpool_incr,
                                      cpool_timeout, cpool_nowait, cpool_dist_txn));
    }

    public void ociPasswordChange(String user, String oldPassword, String newPassword)
            throws SQLException {
        if (this.lifecycle != 1)
            DatabaseError.throwSqlException(8);
        byte[] l_userName = user == null ? new byte[0] : DBConversion
                .stringToDriverCharBytes(user, this.m_clientCharacterSet);

        byte[] l_oldPwd = oldPassword == null ? new byte[0] : DBConversion
                .stringToAsciiBytes(oldPassword);

        byte[] l_newPwd = newPassword == null ? new byte[0] : DBConversion
                .stringToAsciiBytes(newPassword);

        this.sqlWarning = checkError(t2cPasswordChange(this.m_nativeState, l_userName,
                                                       l_userName.length, l_oldPwd,
                                                       l_oldPwd.length, l_newPwd, l_newPwd.length),
                                     this.sqlWarning);
    }

    private void processOCIConnectionPooling() throws SQLException {
        if (this.lifecycle != 1)
            DatabaseError.throwSqlException(8);
        byte[] l_userName = null;

        byte[] l_password = this.password == null ? new byte[0] : DBConversion
                .stringToAsciiBytes(this.password);

        byte[] l_database = DBConversion.stringToAsciiBytes(this.database);

        byte[] nlslanguage = CharacterSetMetaData.getNLSLanguage(Locale.getDefault()).getBytes();

        byte[] nlsterritory = CharacterSetMetaData.getNLSTerritory(Locale.getDefault()).getBytes();

        String prop = this.connectionProperties.getProperty("connection_pool");

        short[] csids = new short[3];

        if (prop == "connection_pool") {
            String propNlsLangBackdoor = this.connectionProperties
                    .getProperty("oracle.jdbc.ociNlsLangBackwardCompatible");

            if ((propNlsLangBackdoor != null) && (propNlsLangBackdoor.equalsIgnoreCase("true"))) {
                this.m_clientCharacterSet = getDriverCharSetIdFromNLS_LANG(this.connectionProperties);
            } else {
                this.m_clientCharacterSet = getClientCharSetId();
            }

            l_userName = this.user == null ? new byte[0] : DBConversion
                    .stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

            this.conversion = new DBConversion(this.m_clientCharacterSet,
                    this.m_clientCharacterSet, this.m_clientCharacterSet);

            this.logon_mode = 5;

            if (this.m_nativeState == 0L) {
                int[] p = new int[6];

                OracleOCIConnectionPool.readPoolConfig(this.connectionProperties, p);

                this.sqlWarning = checkError(t2cCreateConnPool(l_userName, l_userName.length,
                                                               l_password, l_password.length,
                                                               l_database, l_database.length,
                                                               this.m_clientCharacterSet,
                                                               this.logon_mode, p[0], p[1], p[2],
                                                               p[3], p[4], p[5]), this.sqlWarning);

                this.versionNumber = 10000;
            } else {
                throw DatabaseError.newSqlException(0, "Internal Error: ");
            }

        } else if (prop == "connpool_connection") {
            this.logon_mode = 6;

            T2CConnection m_conn_pool = (T2CConnection) this.connectionProperties
                    .get("connpool_object");

            this.m_clientCharacterSet = m_conn_pool.m_clientCharacterSet;

            l_userName = this.user == null ? new byte[0] : DBConversion
                    .stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

            this.conversion = new DBConversion(this.m_clientCharacterSet,
                    this.m_clientCharacterSet, this.m_clientCharacterSet);

            this.sqlWarning = checkError(
                                         t2cConnPoolLogon(m_conn_pool.m_nativeState, l_userName,
                                                          l_userName.length, l_password,
                                                          l_password.length, l_database,
                                                          l_database.length, this.logon_mode, 0, 0,
                                                          null, null, 0, null, 0, null, 0, null, 0,
                                                          null, 0, csids, nlslanguage, nlsterritory),
                                         this.sqlWarning);
        } else if (prop == "connpool_alias_connection") {
            this.logon_mode = 8;

            byte[] connection_id = null;

            connection_id = (byte[]) this.connectionProperties.get("connection_id");

            T2CConnection m_conn_pool = (T2CConnection) this.connectionProperties
                    .get("connpool_object");

            this.m_clientCharacterSet = m_conn_pool.m_clientCharacterSet;

            l_userName = this.user == null ? new byte[0] : DBConversion
                    .stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

            this.conversion = new DBConversion(this.m_clientCharacterSet,
                    this.m_clientCharacterSet, this.m_clientCharacterSet);

            this.sqlWarning = checkError(t2cConnPoolLogon(m_conn_pool.m_nativeState, l_userName,
                                                          l_userName.length, l_password,
                                                          l_password.length, l_database,
                                                          l_database.length, this.logon_mode, 0, 0,
                                                          null, null, 0, null, 0, null, 0, null, 0,
                                                          connection_id, connection_id == null ? 0
                                                                  : connection_id.length, csids,
                                                          nlslanguage, nlsterritory),
                                         this.sqlWarning);
        } else if (prop == "connpool_proxy_connection") {
            this.logon_mode = 7;

            String proxyType = this.connectionProperties.getProperty("proxytype");

            Integer num = (Integer) this.connectionProperties.get("proxy_num_roles");

            int num_proxy_roles = num.intValue();

            String[] proxy_roles = null;

            if (num_proxy_roles > 0) {
                proxy_roles = (String[]) this.connectionProperties.get("proxy_roles");
            }

            byte[] proxy_un = null;
            byte[] proxy_pd = null;
            byte[] proxy_dn = null;
            byte[] proxy_cf = null;

            int proxy_type = 0;

            if (proxyType == "proxytype_user_name") {
                proxy_type = 1;

                String p = this.connectionProperties.getProperty("proxy_user_name");

                if (p != null) {
                    proxy_un = p.getBytes();
                }
                p = this.connectionProperties.getProperty("proxy_password");

                if (p != null)
                    proxy_pd = p.getBytes();
            } else if (proxyType == "proxytype_distinguished_name") {
                proxy_type = 2;

                String p = this.connectionProperties.getProperty("proxy_distinguished_name");

                if (p != null)
                    proxy_dn = p.getBytes();
            } else if (proxyType == "proxytype_certificate") {
                proxy_type = 3;

                proxy_cf = (byte[]) this.connectionProperties.get("proxy_certificate");
            } else {
                DatabaseError.throwSqlException(107);
            }

            T2CConnection m_conn_pool = (T2CConnection) this.connectionProperties
                    .get("connpool_object");

            this.m_clientCharacterSet = m_conn_pool.m_clientCharacterSet;

            l_userName = this.user == null ? new byte[0] : DBConversion
                    .stringToDriverCharBytes(this.user, this.m_clientCharacterSet);

            this.conversion = new DBConversion(this.m_clientCharacterSet,
                    this.m_clientCharacterSet, this.m_clientCharacterSet);

            this.sqlWarning = checkError(
                                         t2cConnPoolLogon(m_conn_pool.m_nativeState, l_userName,
                                                          l_userName.length, l_password,
                                                          l_password.length, l_database,
                                                          l_database.length, this.logon_mode,
                                                          proxy_type, num_proxy_roles, proxy_roles,
                                                          proxy_un, proxy_un == null ? 0
                                                                  : proxy_un.length, proxy_pd,
                                                          proxy_pd == null ? 0 : proxy_pd.length,
                                                          proxy_dn, proxy_dn == null ? 0
                                                                  : proxy_dn.length, proxy_cf,
                                                          proxy_cf == null ? 0 : proxy_cf.length,
                                                          null, 0, csids, nlslanguage, nlsterritory),
                                         this.sqlWarning);
        } else {
            DatabaseError.throwSqlException(23, "connection-pool-logon");
        }

        this.conversion = new DBConversion(csids[0], this.m_clientCharacterSet, csids[1]);
        this.byteAlign = (byte) (csids[2] & 0xFF);
    }

    public boolean isDescriptorSharable(OracleConnection conn) throws SQLException {
        PhysicalConnection c1 = this;
        PhysicalConnection c2 = (PhysicalConnection) conn.getPhysicalConnection();

        return c1 == c2;
    }

    native int blobRead(long paramLong1, byte[] paramArrayOfByte1, int paramInt1, long paramLong2,
            int paramInt2, byte[] paramArrayOfByte2, int paramInt3);

    native int clobRead(long paramLong1, byte[] paramArrayOfByte, int paramInt1, long paramLong2,
            int paramInt2, char[] paramArrayOfChar, int paramInt3, boolean paramBoolean);

    native int blobWrite(long paramLong1, byte[] paramArrayOfByte1, int paramInt1, long paramLong2,
            int paramInt2, byte[] paramArrayOfByte2, int paramInt3, byte[][] paramArrayOfByte);

    native int clobWrite(long paramLong1, byte[] paramArrayOfByte, int paramInt1, long paramLong2,
            int paramInt2, char[] paramArrayOfChar, int paramInt3, byte[][] paramArrayOfByte1,
            boolean paramBoolean);

    native long lobGetLength(long paramLong, byte[] paramArrayOfByte, int paramInt);

    native int bfileOpen(long paramLong, byte[] paramArrayOfByte, int paramInt,
            byte[][] paramArrayOfByte1);

    native int bfileIsOpen(long paramLong, byte[] paramArrayOfByte, int paramInt,
            boolean[] paramArrayOfBoolean);

    native int bfileExists(long paramLong, byte[] paramArrayOfByte, int paramInt,
            boolean[] paramArrayOfBoolean);

    native String bfileGetName(long paramLong, byte[] paramArrayOfByte, int paramInt);

    native String bfileGetDirAlias(long paramLong, byte[] paramArrayOfByte, int paramInt);

    native int bfileClose(long paramLong, byte[] paramArrayOfByte, int paramInt,
            byte[][] paramArrayOfByte1);

    native int lobGetChunkSize(long paramLong, byte[] paramArrayOfByte, int paramInt);

    native int lobTrim(long paramLong1, int paramInt1, long paramLong2, byte[] paramArrayOfByte,
            int paramInt2, byte[][] paramArrayOfByte1);

    native int lobCreateTemporary(long paramLong, int paramInt1, boolean paramBoolean,
            int paramInt2, short paramShort, byte[][] paramArrayOfByte);

    native int lobFreeTemporary(long paramLong, int paramInt1, byte[] paramArrayOfByte,
            int paramInt2, byte[][] paramArrayOfByte1);

    native int lobIsTemporary(long paramLong, int paramInt1, byte[] paramArrayOfByte,
            int paramInt2, boolean[] paramArrayOfBoolean);

    native int lobOpen(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2,
            int paramInt3, byte[][] paramArrayOfByte1);

    native int lobIsOpen(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2,
            boolean[] paramArrayOfBoolean);

    native int lobClose(long paramLong, int paramInt1, byte[] paramArrayOfByte, int paramInt2,
            byte[][] paramArrayOfByte1);

    private long lobLength(byte[] locator) throws SQLException {
        long result = 0L;

        result = lobGetLength(this.m_nativeState, locator, locator.length);

        checkError((int) result);

        return result;
    }

    private int blobRead(byte[] locator, long offset, int amount, byte[] buffer)
            throws SQLException {
        int result = 0;

        result = blobRead(this.m_nativeState, locator, locator.length, offset, amount, buffer,
                          buffer.length);

        checkError(result);

        return result;
    }

    private int blobWrite(byte[] locator, long offset, byte[] buffer, byte[][] newLocatorHolder,
            int bytesOffset, int amount) throws SQLException {
        int result = 0;

        result = blobWrite(this.m_nativeState, locator, locator.length, offset, amount, buffer,
                           bytesOffset, newLocatorHolder);

        checkError(result);

        return result;
    }

    private int clobWrite(byte[] locator, long offset, char[] buffer, byte[][] newLocatorHolder,
            boolean isNclob, int charsOffset, int amount) throws SQLException {
        int result = 0;

        result = clobWrite(this.m_nativeState, locator, locator.length, offset, amount, buffer,
                           charsOffset, newLocatorHolder, isNclob);

        checkError(result);

        return result;
    }

    private int lobGetChunkSize(byte[] locator) throws SQLException {
        int result = 0;

        result = lobGetChunkSize(this.m_nativeState, locator, locator.length);

        checkError(result);

        return result;
    }

    public long length(BFILE bfile) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger
                    .log(Level.FINE, "T2CConnection.Length(bfile = " + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        return lobLength(locator);
    }

    public long position(BFILE bfile, byte[] pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "oracle.jdbc.driver.T2CConnection.position( "
                    + bfile + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (start < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger
                        .log(
                             Level.SEVERE,
                             "oracle.jdbc.driver.T2CConnection.position: Invalid argument, 'start' should not be <1.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.hasPattern(bfile, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.position:return: " + result,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(BFILE bfile, BFILE pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "oracle.jdbc.driver.T2CConnection.position( "
                    + bfile + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (start < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger
                        .log(
                             Level.SEVERE,
                             "oracle.jdbc.driver.T2CConnection.position: Invalid argument, 'start' should not be <1.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.isSubLob(bfile, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.position:return: " + result,
                                    this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public int getBytes(BFILE bfile, long offset, int amount, byte[] buffer) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.getBytes(bfile = " + bfile
                    + ", offset = " + offset + ", amount = " + amount + ", buffer = " + buffer
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        if ((amount <= 0) || (buffer == null)) {
            return 0;
        }
        if (amount > buffer.length) {
            amount = buffer.length;
        }
        return blobRead(locator, offset, amount, buffer);
    }

    public String getName(BFILE bfile) throws SQLException {
        byte[] locator = null;
        String fileName = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.GetName(bfile = " + bfile + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        fileName = bfileGetName(this.m_nativeState, locator, locator.length);

        checkError(fileName.length());

        return fileName;
    }

    public String getDirAlias(BFILE bfile) throws SQLException {
        byte[] locator = null;
        String aliasName = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.fileGetDirAlias(bfile = " + bfile
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        aliasName = bfileGetDirAlias(this.m_nativeState, locator, locator.length);

        checkError(aliasName.length());

        return aliasName;
    }

    public void openFile(BFILE bfile) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.openFile(bfile = " + bfile + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(bfileOpen(this.m_nativeState, locator, locator.length, newLocatorHolder));

        bfile.setLocator(newLocatorHolder[0]);
    }

    public boolean isFileOpen(BFILE bfile) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.isFileOpen(bfile = " + bfile + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(bfileIsOpen(this.m_nativeState, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public boolean fileExists(BFILE bfile) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.fileExists(bfile = " + bfile + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(bfileExists(this.m_nativeState, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public void closeFile(BFILE bfile) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.closeFile(bfile = " + bfile + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((bfile != null) && ((locator = bfile.getLocator()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(bfileClose(this.m_nativeState, locator, locator.length, newLocatorHolder));

        bfile.setLocator(newLocatorHolder[0]);
    }

    public void open(BFILE lob, int mode) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.open( lob = " + lob + ", mode = "
                    + mode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobOpen(this.m_nativeState, 114, locator, locator.length, mode, newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);
    }

    public void close(BFILE lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.close(lob = " + lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobClose(this.m_nativeState, 114, locator, locator.length, newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);
    }

    public boolean isOpen(BFILE lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.isOpen(lob = " + lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(lobIsOpen(this.m_nativeState, 114, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public InputStream newInputStream(BFILE bfile, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "oracle.jdbc.driver.T2CConnection.newInputStream("
                    + bfile + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleBlobInputStream(bfile, chunkSize);
        }

        return new OracleBlobInputStream(bfile, chunkSize, pos);
    }

    public InputStream newConversionInputStream(BFILE bfile, int conversionType)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.newConversionInputStream("
                                            + bfile + ", " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue((bfile != null) && (bfile.shareBytes() != null), 54);

        InputStream result = new OracleConversionInputStream(this.conversion, bfile
                .getBinaryStream(), conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.newConversionInputStream: return: "
                                            + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public Reader newConversionReader(BFILE bfile, int conversionType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.newConversionReader(" + bfile
                                            + ", " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue((bfile != null) && (bfile.shareBytes() != null), 54);

        Reader result = new OracleConversionReader(this.conversion, bfile.getBinaryStream(),
                conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newConversionReader: return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long length(BLOB blob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.Length(blob = " + blob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((blob != null) && ((locator = blob.getLocator()) != null), 54);

        return lobLength(locator);
    }

    public long position(BLOB blob, byte[] pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "oracle.jdbc.driver.T2CConnection.position(blob = "
                    + blob + ", pattern = " + pattern + ", start = " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((blob != null) && (blob.shareBytes() != null), 54);

        if (start < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger
                        .log(Level.SEVERE,
                             "T2CConnection.position: Invalid argument, 'start' should not be <1.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.hasPattern(blob, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position:return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(BLOB blob, BLOB pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position( " + blob + ", " + pattern
                    + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((blob != null) && (blob.shareBytes() != null), 54);

        checkTrue((pattern != null) && (pattern.shareBytes() != null), 54);

        if (start < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger
                        .log(Level.SEVERE,
                             "T2CConnection.position: Invalid argument, 'start' should not be <1.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.isSubLob(blob, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position:return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public int getBytes(BLOB blob, long offset, int amount, byte[] buffer) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.getBytes(blob = " + blob
                    + ", offset = " + offset + ", amount = " + amount + ", buffer = " + buffer
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((blob != null) && ((locator = blob.getLocator()) != null), 54);

        if ((amount <= 0) || (buffer == null)) {
            return 0;
        }
        if (amount > buffer.length) {
            amount = buffer.length;
        }
        return blobRead(locator, offset, amount, buffer);
    }

    public int putBytes(BLOB blob, long offset, byte[] buffer, int bytesOffset, int length)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.putbytes(blob = " + blob
                    + ", offset = " + offset + ", buffer = " + buffer + ", length = " + length
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(offset >= 0L, 68);

        int ret = 0;

        if ((buffer == null) || (buffer.length == 0) || (length <= 0)) {
            ret = 0;
        } else {
            byte[] locator = null;

            checkTrue(this.m_nativeState != 0L, 8);
            checkTrue((blob != null) && ((locator = blob.getLocator()) != null), 54);

            if (buffer == null) {
                return 0;
            }
            byte[][] newLocatorHolder = new byte[1][];

            ret = blobWrite(locator, offset, buffer, newLocatorHolder, bytesOffset, length);

            blob.setLocator(newLocatorHolder[0]);
        }

        return ret;
    }

    public int getChunkSize(BLOB blob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.getChunkSize(blob = " + blob + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((blob != null) && ((locator = blob.getLocator()) != null), 54);

        return lobGetChunkSize(locator);
    }

    public void trim(BLOB lob, long newlen) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.trim(lob = " + lob + ", newlen = "
                    + newlen + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobTrim(this.m_nativeState, 113, newlen, locator, locator.length,
                           newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);
    }

    public BLOB createTemporaryBlob(Connection conn, boolean cache, int duration)
            throws SQLException {
        BLOB lob = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createTemporaryBlob(conn = " + conn
                    + ", cache = " + cache + ", duration = " + duration + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);

        lob = new BLOB((PhysicalConnection) conn);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobCreateTemporary(this.m_nativeState, 113, cache, duration, (short) 0, newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);

        return lob;
    }

    public void freeTemporary(BLOB temp_lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.freeTemporary(temp_lob = "
                    + temp_lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((temp_lob != null) && ((locator = temp_lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobFreeTemporary(this.m_nativeState, 113, locator, locator.length,
                                    newLocatorHolder));

        temp_lob.setShareBytes(newLocatorHolder[0]);
    }

    public boolean isTemporary(BLOB lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.isTemporary(lob = " + lob + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(lobIsTemporary(this.m_nativeState, 113, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public void open(BLOB blob, int mode) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.open(blob  = " + blob + ", mode = "
                    + mode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((blob != null) && ((locator = blob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobOpen(this.m_nativeState, 113, locator, locator.length, mode, newLocatorHolder));

        blob.setShareBytes(newLocatorHolder[0]);
    }

    public void close(BLOB lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.close(blob = " + lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobClose(this.m_nativeState, 113, locator, locator.length, newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);
    }

    public boolean isOpen(BLOB lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.isOpen(blob = " + lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(lobIsOpen(this.m_nativeState, 113, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public InputStream newInputStream(BLOB blob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.newInputStream(blob = "
                                            + blob + ", chunkSize = " + chunkSize + ", pos = "
                                            + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleBlobInputStream(blob, chunkSize);
        }

        return new OracleBlobInputStream(blob, chunkSize, pos);
    }

    public OutputStream newOutputStream(BLOB blob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE,
                                    "oracle.jdbc.driver.T2CConnection.newOutputStream(blob = "
                                            + blob + ", chunkSize = " + chunkSize + ", pos = "
                                            + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleBlobOutputStream(blob, chunkSize);
        }

        return new OracleBlobOutputStream(blob, chunkSize, pos);
    }

    public InputStream newConversionInputStream(BLOB blob, int conversionType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newConversionInputStream(blob = "
                    + blob + ", oracle.jdbc.driver. = " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue((blob != null) && (blob.shareBytes() != null), 54);

        InputStream result = new OracleConversionInputStream(this.conversion, blob
                .getBinaryStream(), conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newConversionInputStream: return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public Reader newConversionReader(BLOB blob, int conversionType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newConversionReader(blob = " + blob
                    + ", conversionType = " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue((blob != null) && (blob.shareBytes() != null), 54);

        Reader result = new OracleConversionReader(this.conversion, blob.getBinaryStream(),
                conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newConversionReader: return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long length(CLOB clob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConneciton.Length(clob = " + clob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((clob != null) && ((locator = clob.getLocator()) != null), 54);

        return lobLength(locator);
    }

    public long position(CLOB clob, String pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position(clob = " + clob
                    + ", pattern = " + pattern + ", start = " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pattern == null) {
            throw new SQLException("pattern cannot be null.");
        }
        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((clob != null) && (clob.shareBytes() != null), 54);

        if (start < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger
                        .log(Level.SEVERE,
                             "T2CConnection.position: Invalid argument, 'start' should not be <1.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "position()");
        }

        char[] chars = new char[pattern.length()];

        pattern.getChars(0, chars.length, chars, 0);

        long result = LobPlsqlUtil.hasPattern(clob, chars, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position:return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(CLOB clob, CLOB pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position(clob = " + clob
                    + ", pattern =" + pattern + ", start = " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((clob != null) && (clob.shareBytes() != null), 54);

        checkTrue((pattern != null) && (pattern.shareBytes() != null), 54);

        if (start < 1L) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.ociLogger
                        .log(Level.SEVERE,
                             "T2CConnection.position: Invalid argument, 'start' should not be <1.",
                             this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.isSubLob(clob, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.position:return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public int getChars(CLOB clob, long offset, int amount, char[] buffer) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.getChars(clob = " + clob
                    + ", offset = " + offset + ", amount = " + amount + ", buffer = " + buffer
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((clob != null) && ((locator = clob.getLocator()) != null), 54);

        if ((amount <= 0) || (buffer == null)) {
            return 0;
        }
        if (amount > buffer.length) {
            amount = buffer.length;
        }
        int result = 0;

        result = clobRead(this.m_nativeState, locator, locator.length, offset, amount, buffer,
                          buffer.length, clob.isNCLOB());

        checkError(result);

        return result;
    }

    public int putChars(CLOB clob, long offset, char[] buffer, int charsOffset, int length)
            throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.putChars(clob = " + clob
                    + ", offset = " + offset + ", buffer = " + buffer + ", charsOffset = "
                    + charsOffset + ", length = " + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue(offset >= 0L, 68);

        checkTrue((clob != null) && ((locator = clob.getLocator()) != null), 54);

        if (buffer == null) {
            return 0;
        }
        byte[][] newLocatorHolder = new byte[1][];
        int result = clobWrite(locator, offset, buffer, newLocatorHolder, clob.isNCLOB(),
                               charsOffset, length);

        clob.setLocator(newLocatorHolder[0]);

        return result;
    }

    public int getChunkSize(CLOB clob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.getChunkSize(clob = " + clob + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((clob != null) && ((locator = clob.getLocator()) != null), 54);

        return lobGetChunkSize(locator);
    }

    public void trim(CLOB clob, long newlen) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.trim(clob = " + clob + ", newlen = "
                    + newlen + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((clob != null) && ((locator = clob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobTrim(this.m_nativeState, 112, newlen, locator, locator.length,
                           newLocatorHolder));

        clob.setShareBytes(newLocatorHolder[0]);
    }

    public CLOB createTemporaryClob(Connection conn, boolean cache, int duration, short form_of_use)
            throws SQLException {
        CLOB lob = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.createTemporaryClob(conn = " + conn
                    + ", cache = " + cache + ", duration = " + duration + ", form_of_use = "
                    + form_of_use + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);

        lob = new CLOB((PhysicalConnection) conn);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobCreateTemporary(this.m_nativeState, 112, cache, duration, form_of_use,
                                      newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);

        return lob;
    }

    public void freeTemporary(CLOB temp_lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.freeTemporary(temp_lob = "
                    + temp_lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((temp_lob != null) && ((locator = temp_lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobFreeTemporary(this.m_nativeState, 112, locator, locator.length,
                                    newLocatorHolder));

        temp_lob.setShareBytes(newLocatorHolder[0]);
    }

    public boolean isTemporary(CLOB lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.isTemporary(lob = " + lob + ")",
                                    this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(lobIsTemporary(this.m_nativeState, 112, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public void open(CLOB lob, int mode) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.open(lob = " + lob + ", mode = "
                    + mode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobOpen(this.m_nativeState, 112, locator, locator.length, mode, newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);
    }

    public void close(CLOB lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.close(lob = " + lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        byte[][] newLocatorHolder = new byte[1][];

        checkError(lobClose(this.m_nativeState, 112, locator, locator.length, newLocatorHolder));

        lob.setShareBytes(newLocatorHolder[0]);
    }

    public boolean isOpen(CLOB lob) throws SQLException {
        byte[] locator = null;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.isOpen(lob = " + lob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        checkTrue(this.m_nativeState != 0L, 8);
        checkTrue((lob != null) && ((locator = lob.shareBytes()) != null), 54);

        boolean[] flagHolder = new boolean[1];

        checkError(lobIsOpen(this.m_nativeState, 112, locator, locator.length, flagHolder));

        return flagHolder[0];
    }

    public InputStream newInputStream(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newInputStream(clob = " + clob
                    + ", chunkSize = " + chunkSize + ", pos = " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobInputStream(clob, chunkSize);
        }

        return new OracleClobInputStream(clob, chunkSize, pos);
    }

    public OutputStream newOutputStream(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newOutputStream(clob = " + clob
                    + ", chunkSize = " + chunkSize + ", pos = " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobOutputStream(clob, chunkSize);
        }

        return new OracleClobOutputStream(clob, chunkSize, pos);
    }

    public Reader newReader(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newReader(CLOB = " + clob
                    + ", chunkSize = " + chunkSize + ", pos = " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobReader(clob, chunkSize);
        }

        return new OracleClobReader(clob, chunkSize, pos);
    }

    public Writer newWriter(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.ociLogger.log(Level.FINE, "T2CConnection.newWriter(clob = " + clob
                    + ", chunkSize = " + chunkSize + ", pos = " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobWriter(clob, chunkSize);
        }

        return new OracleClobWriter(clob, chunkSize, pos);
    }

    public synchronized void registerTAFCallback(OracleOCIFailover cbk, Object obj)
            throws SQLException {
        this.appCallback = cbk;
        this.appCallbackObject = obj;

        checkError(t2cRegisterTAFCallback(this.m_nativeState));
    }

    synchronized int callTAFCallbackMethod(int type, int event) {
        int retCode = 0;

        if (this.appCallback != null) {
            retCode = this.appCallback.callbackFn(this, this.appCallbackObject, type, event);
        }
        return retCode;
    }

    public int getHeapAllocSize() throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }

        int size = t2cGetHeapAllocSize(this.m_nativeState);

        if (size < 0) {
            if (size == -999) {
                DatabaseError.throwSqlException(23);
            }

            DatabaseError.throwSqlException(89);
        }

        return size;
    }

    public int getOCIEnvHeapAllocSize() throws SQLException {
        if (this.lifecycle != 1) {
            DatabaseError.throwSqlException(8);
        }

        int size = t2cGetOciEnvHeapAllocSize(this.m_nativeState);

        if (size < 0) {
            if (size == -999)
                DatabaseError.throwSqlException(23);
            else {
                checkError(size);
            }

            DatabaseError.throwSqlException(89);
        }

        return size;
    }

    public static final short getClientCharSetId() {
        return 871;
    }

    public static short getDriverCharSetIdFromNLS_LANG(Properties props) throws SQLException {
        if (!isLibraryLoaded) {
            loadNativeLibrary(props);
        }

        short driverCharSetId = t2cGetDriverCharSetFromNlsLang();

        if (driverCharSetId < 0) {
            DatabaseError.throwSqlException(8);
        }

        return driverCharSetId;
    }

    void doProxySession(int type, Properties prop) throws SQLException {
        byte[][] roles = (byte[][]) null;

        int numProxyRoles = 0;

        this.savedUser = this.user;
        this.user = null;
        byte[] cfBytes;
        byte[] dnBytes;
        byte[] pwdBytes;
        byte[] userBytes = pwdBytes = dnBytes = cfBytes = new byte[0];

        switch (type) {
        case 1:
            this.user = prop.getProperty("PROXY_USER_NAME");
            String pwd = prop.getProperty("PROXY_USER_PASSWORD");
            if (this.user != null) {
                userBytes = DBConversion.stringToDriverCharBytes(this.user,
                                                                 this.m_clientCharacterSet);
            }
            if (pwd == null)
                break;
            pwdBytes = DBConversion.stringToAsciiBytes(pwd);
            break;
        case 2:
            String distName = prop.getProperty("PROXY_DISTINGUISHED_NAME");
            if (distName == null)
                break;
            dnBytes = DBConversion.stringToDriverCharBytes(distName, this.m_clientCharacterSet);
            break;
        case 3:
            Object certif = prop.get("PROXY_CERTIFICATE");
            cfBytes = (byte[]) certif;
        }

        String[] strRoles = (String[]) prop.get("PROXY_ROLES");

        if (strRoles != null) {
            numProxyRoles = strRoles.length;
            roles = new byte[numProxyRoles][];
            for (int i = 0; i < numProxyRoles; i++) {
                if (strRoles[i] == null) {
                    DatabaseError.throwSqlException(150);
                }
                roles[i] = DBConversion.stringToDriverCharBytes(strRoles[i],
                                                                this.m_clientCharacterSet);
            }

        }

        this.sqlWarning = checkError(t2cDoProxySession(this.m_nativeState, type, userBytes,
                                                       userBytes.length, pwdBytes, pwdBytes.length,
                                                       dnBytes, dnBytes.length, cfBytes,
                                                       cfBytes.length, numProxyRoles, roles),
                                     this.sqlWarning);

        this.isProxy = true;
    }

    void closeProxySession() throws SQLException {
        checkError(t2cCloseProxySession(this.m_nativeState));
        this.user = this.savedUser;
    }

    protected void doDescribeTable(AutoKeyInfo info) throws SQLException {
        String tableName = info.getTableName();

        byte[] bytesTableName = DBConversion.stringToDriverCharBytes(tableName,
                                                                     this.m_clientCharacterSet);

        boolean tryAgain = false;
        int numColumns;
        do {
            numColumns = t2cDescribeTable(this.m_nativeState, bytesTableName,
                                          bytesTableName.length, this.queryMetaData1,
                                          this.queryMetaData2, this.queryMetaData1Offset,
                                          this.queryMetaData2Offset, this.queryMetaData1Size,
                                          this.queryMetaData2Size);

            if (numColumns == -1) {
                checkError(numColumns);
            }

            if (numColumns != T2CStatement.T2C_EXTEND_BUFFER)
                continue;
            tryAgain = true;

            reallocateQueryMetaData(this.queryMetaData1Size * 2, this.queryMetaData2Size * 2);
        }

        while (tryAgain);

        processDescribeTableData(numColumns, info);
    }

    private void processDescribeTableData(int numColumns, AutoKeyInfo info) throws SQLException {
        short[] s = this.queryMetaData1;
        byte[] c = this.queryMetaData2;
        int currentShort = this.queryMetaData1Offset;
        int currentChar = this.queryMetaData2Offset;

        info.allocateSpaceForDescribedData(numColumns);

        for (int i = 0; i < numColumns; i++) {
            int type = s[(currentShort + 0)];
            short columnNameLen = s[(currentShort + 6)];
            String columnName = bytes2String(c, currentChar, columnNameLen, this.conversion);

            int maxLength = s[(currentShort + 1)];
            int maxCharLength = s[(currentShort + 11)];
            boolean nullable = s[(currentShort + 2)] != 0;
            short formOfUse = s[(currentShort + 5)];
            int precision = s[(currentShort + 3)];
            int scale = s[(currentShort + 4)];
            int typeNameLen = s[(currentShort + 12)];

            currentChar += columnNameLen;
            currentShort += 13;

            String typeName = null;
            if (typeNameLen > 0) {
                typeName = bytes2String(c, currentChar, typeNameLen, this.conversion);

                currentChar += typeNameLen;
            }

            info.fillDescribedData(i, columnName, type, maxCharLength > 0 ? maxCharLength
                    : maxLength, nullable, formOfUse, precision, scale, typeName);
        }
    }

    void doSetApplicationContext(String nameSpace, String attribute, String value)
            throws SQLException {
        checkError(t2cSetApplicationContext(this.m_nativeState, nameSpace, attribute, value));
    }

    void doClearAllApplicationContext(String nameSpace) throws SQLException {
        checkError(t2cClearAllApplicationContext(this.m_nativeState, nameSpace));
    }

    private static final void loadNativeLibrary(Properties props) throws SQLException {
        String dll_str = null;
        if (props != null) {
            dll_str = props.getProperty("oracle.jdbc.ocinativelibrary");
        }

        if ((dll_str == null) || (dll_str.equals("ocijdbc10"))) {
            synchronized (T2CConnection.class) {
                if (!isLibraryLoaded) {
                    AccessController.doPrivileged(new PrivilegedAction() {
                        public Object run() {
                            System.loadLibrary("ocijdbc10");
                            return null;
                        }
                    });
                    isLibraryLoaded = true;
                }

            }

        }

        synchronized (T2CConnection.class) {
            try {
                System.loadLibrary(dll_str);
                isLibraryLoaded = true;
            } catch (SecurityException e) {
                if (!isLibraryLoaded) {
                    System.loadLibrary(dll_str);
                    isLibraryLoaded = true;
                }
            }
        }
    }

    private final void checkTrue(boolean assertion, int errCode) throws SQLException {
        if (!assertion)
            DatabaseError.throwSqlException(errCode);
    }

    boolean useLittleEndianSetCHARBinder() throws SQLException {
        return t2cPlatformIsLittleEndian(this.m_nativeState);
    }

    public void getPropertyForPooledConnection(OraclePooledConnection pc) throws SQLException {
        super.getPropertyForPooledConnection(pc, this.password);
    }

    static final char[] getCharArray(String s) {
        char[] r = null;

        if (s == null) {
            r = new char[0];
        } else {
            r = new char[s.length()];

            s.getChars(0, s.length(), r, 0);
        }

        return r;
    }

    static String bytes2String(byte[] bytes, int offset, int size, DBConversion conversion)
            throws SQLException {
        byte[] tmp = new byte[size];
        System.arraycopy(bytes, offset, tmp, 0, size);

        return conversion.CharBytesToString(tmp, size);
    }

    static native short t2cGetServerSessionInfo(long paramLong, Properties paramProperties);

    static native short t2cGetDriverCharSetFromNlsLang();

    native int t2cDescribeError(long paramLong, T2CError paramT2CError, byte[] paramArrayOfByte);

    native int t2cCreateState(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2,
            int paramInt2, byte[] paramArrayOfByte3, int paramInt3, byte[] paramArrayOfByte4,
            int paramInt4, short paramShort, int paramInt5, short[] paramArrayOfShort,
            byte[] paramArrayOfByte5, byte[] paramArrayOfByte6);

    native int t2cLogon(long paramLong, byte[] paramArrayOfByte1, int paramInt1,
            byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3,
            byte[] paramArrayOfByte4, int paramInt4, int paramInt5, short[] paramArrayOfShort,
            byte[] paramArrayOfByte5, byte[] paramArrayOfByte6);

    private native int t2cLogoff(long paramLong);

    private native int t2cCancel(long paramLong);

    private native int t2cCreateStatement(long paramLong1, long paramLong2,
            byte[] paramArrayOfByte, int paramInt1, OracleStatement paramOracleStatement,
            boolean paramBoolean, int paramInt2);

    private native int t2cSetAutoCommit(long paramLong, boolean paramBoolean);

    private native int t2cCommit(long paramLong);

    private native int t2cRollback(long paramLong);

    private native byte[] t2cGetProductionVersion(long paramLong);

    private native int t2cGetVersionNumber(long paramLong);

    private native int t2cGetDefaultStreamChunkSize(long paramLong);

    native int t2cGetFormOfUse(long paramLong, OracleTypeCLOB paramOracleTypeCLOB,
            byte[] paramArrayOfByte, int paramInt1, int paramInt2);

    native long t2cGetTDO(long paramLong, byte[] paramArrayOfByte, int paramInt,
            int[] paramArrayOfInt);

    native int t2cCreateConnPool(byte[] paramArrayOfByte1, int paramInt1, byte[] paramArrayOfByte2,
            int paramInt2, byte[] paramArrayOfByte3, int paramInt3, short paramShort,
            int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8,
            int paramInt9, int paramInt10);

    native int t2cConnPoolLogon(long paramLong, byte[] paramArrayOfByte1, int paramInt1,
            byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3,
            int paramInt4, int paramInt5, int paramInt6, String[] paramArrayOfString,
            byte[] paramArrayOfByte4, int paramInt7, byte[] paramArrayOfByte5, int paramInt8,
            byte[] paramArrayOfByte6, int paramInt9, byte[] paramArrayOfByte7, int paramInt10,
            byte[] paramArrayOfByte8, int paramInt11, short[] paramArrayOfShort,
            byte[] paramArrayOfByte9, byte[] paramArrayOfByte10);

    native int t2cGetConnPoolInfo(long paramLong, Properties paramProperties);

    native int t2cSetConnPoolInfo(long paramLong, int paramInt1, int paramInt2, int paramInt3,
            int paramInt4, int paramInt5, int paramInt6);

    native int t2cPasswordChange(long paramLong, byte[] paramArrayOfByte1, int paramInt1,
            byte[] paramArrayOfByte2, int paramInt2, byte[] paramArrayOfByte3, int paramInt3);

    protected native byte[] t2cGetConnectionId(long paramLong);

    native int t2cGetHandles(long paramLong, long[] paramArrayOfLong);

    native int t2cUseConnection(long paramLong1, long paramLong2, long paramLong3, long paramLong4,
            short[] paramArrayOfShort);

    native boolean t2cPlatformIsLittleEndian(long paramLong);

    native int t2cRegisterTAFCallback(long paramLong);

    native int t2cGetHeapAllocSize(long paramLong);

    native int t2cGetOciEnvHeapAllocSize(long paramLong);

    native int t2cDoProxySession(long paramLong, int paramInt1, byte[] paramArrayOfByte1,
            int paramInt2, byte[] paramArrayOfByte2, int paramInt3, byte[] paramArrayOfByte3,
            int paramInt4, byte[] paramArrayOfByte4, int paramInt5, int paramInt6,
            byte[][] paramArrayOfByte);

    native int t2cCloseProxySession(long paramLong);

    static native int t2cDescribeTable(long paramLong, byte[] paramArrayOfByte1, int paramInt1,
            short[] paramArrayOfShort, byte[] paramArrayOfByte2, int paramInt2, int paramInt3,
            int paramInt4, int paramInt5);

    native int t2cSetApplicationContext(long paramLong, String paramString1, String paramString2,
            String paramString3);

    native int t2cClearAllApplicationContext(long paramLong, String paramString);

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T2CConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T2CConnection JD-Core Version: 0.6.0
 */