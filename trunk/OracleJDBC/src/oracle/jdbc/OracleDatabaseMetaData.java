package oracle.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.driver.OracleSql;
import oracle.jdbc.internal.OracleResultSet;
import oracle.sql.SQLName;

public class OracleDatabaseMetaData implements DatabaseMetaData {
    private static String DRIVER_NAME = "Oracle JDBC driver";
    private static String DRIVER_VERSION = "10.2.0.1.0";
    private static int DRIVER_MAJOR_VERSION = 10;
    private static int DRIVER_MINOR_VERSION = 2;
    private static String LOB_MAXSIZE = "4294967295";
    private static long LOB_MAXLENGTH_32BIT = 4294967295L;
    protected oracle.jdbc.internal.OracleConnection connection;
    int procedureResultUnknown = 0;

    int procedureNoResult = 1;

    int procedureReturnsResult = 2;

    int procedureColumnUnknown = 0;

    int procedureColumnIn = 1;

    int procedureColumnInOut = 2;

    int procedureColumnOut = 4;

    int procedureColumnReturn = 5;

    int procedureColumnResult = 3;

    int procedureNoNulls = 0;

    int procedureNullable = 1;

    int procedureNullableUnknown = 2;

    int columnNoNulls = 0;

    int columnNullable = 1;

    int columnNullableUnknown = 2;
    static final int bestRowTemporary = 0;
    static final int bestRowTransaction = 1;
    static final int bestRowSession = 2;
    static final int bestRowUnknown = 0;
    static final int bestRowNotPseudo = 1;
    static final int bestRowPseudo = 2;
    int versionColumnUnknown = 0;

    int versionColumnNotPseudo = 1;

    int versionColumnPseudo = 2;

    int importedKeyCascade = 0;

    int importedKeyRestrict = 1;

    int importedKeySetNull = 2;

    int typeNoNulls = 0;

    int typeNullable = 1;

    int typeNullableUnknown = 2;

    int typePredNone = 0;

    int typePredChar = 1;

    int typePredBasic = 2;

    int typeSearchable = 3;

    short tableIndexStatistic = 0;

    short tableIndexClustered = 1;

    short tableIndexHashed = 2;

    short tableIndexOther = 3;

    short attributeNoNulls = 0;

    short attributeNullable = 1;

    short attributeNullableUnknown = 2;

    int sqlStateXOpen = 1;

    int sqlStateSQL99 = 2;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:48_PDT_2005";

    public OracleDatabaseMetaData(OracleConnection conn) {
        this.connection = conn.physicalConnectionWithin();
    }

    public boolean allProceduresAreCallable() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "allProceduresAreCallable", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean allTablesAreSelectable() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "allTablesAreSelectable", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public String getURL() throws SQLException {
        return this.connection.getURL();
    }

    public String getUserName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getUserName", this);

            OracleLog.recursiveTrace = false;
        }

        return this.connection.getUserName();
    }

    public boolean isReadOnly() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "isReadOnly", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "nullsAreSortedHigh", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean nullsAreSortedLow() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "nullsAreSortedLow", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "nullsAreSortedAtStart", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "nullsAreSortedAtEnd", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public String getDatabaseProductName() throws SQLException {
        return "Oracle";
    }

    public String getDatabaseProductVersion() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getDatabaseProductVersion", this);

            OracleLog.recursiveTrace = false;
        }

        return this.connection.getDatabaseProductVersion();
    }

    public String getDriverName() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getDriverName", this);

            OracleLog.recursiveTrace = false;
        }

        return DRIVER_NAME;
    }

    public String getDriverVersion() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getDriverVersion", this);

            OracleLog.recursiveTrace = false;
        }

        return DRIVER_VERSION;
    }

    public int getDriverMajorVersion() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getDriverMajorVersion", this);

            OracleLog.recursiveTrace = false;
        }

        return DRIVER_MAJOR_VERSION;
    }

    public int getDriverMinorVersion() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getDriverMinorVersion", this);

            OracleLog.recursiveTrace = false;
        }

        return DRIVER_MINOR_VERSION;
    }

    public boolean usesLocalFiles() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "usesLocalFiles", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "usesLocalFilePerTable", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsMixedCaseIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "storesUpperCaseIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "storesLowerCaseIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "storesMixedCaseIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsMixedCaseQuotedIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "storesUpperCaseQuotedIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "storesLowerCaseQuotedIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "storesMixedCaseQuotedIdentifiers", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public String getIdentifierQuoteString() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getIdentifierQuoteString", this);

            OracleLog.recursiveTrace = false;
        }

        return "\"";
    }

    public String getSQLKeywords() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getSQLKeywords", this);

            OracleLog.recursiveTrace = false;
        }

        return "ACCESS, ADD, ALTER, AUDIT, CLUSTER, COLUMN, COMMENT, COMPRESS, CONNECT, DATE, DROP, EXCLUSIVE, FILE, IDENTIFIED, IMMEDIATE, INCREMENT, INDEX, INITIAL, INTERSECT, LEVEL, LOCK, LONG, MAXEXTENTS, MINUS, MODE, NOAUDIT, NOCOMPRESS, NOWAIT, NUMBER, OFFLINE, ONLINE, PCTFREE, PRIOR, all_PL_SQL_reserved_ words";
    }

    public String getNumericFunctions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getNumericFunctions", this);

            OracleLog.recursiveTrace = false;
        }

        return "ABS,ACOS,ASIN,ATAN,ATAN2,CEILING,COS,EXP,FLOOR,LOG,LOG10,MOD,PI,POWER,ROUND,SIGN,SIN,SQRT,TAN,TRUNCATE";
    }

    public String getStringFunctions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getStringFunctions", this);

            OracleLog.recursiveTrace = false;
        }

        return "ASCII,CHAR,CONCAT,LCASE,LENGTH,LTRIM,REPLACE,RTRIM,SOUNDEX,SUBSTRING,UCASE";
    }

    public String getSystemFunctions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getSystemFunctions", this);

            OracleLog.recursiveTrace = false;
        }

        return "USER";
    }

    public String getTimeDateFunctions() throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public String getSearchStringEscape() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getSearchStringEscape", this);

            OracleLog.recursiveTrace = false;
        }

        return "//";
    }

    public String getExtraNameCharacters() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getExtraNameCharacters", this);

            OracleLog.recursiveTrace = false;
        }

        return "$#";
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsAlterTableWithAddColumn", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsAlterTableWithDropColumn", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsColumnAliasing() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsColumnAliasing", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "nullPlusNonNullIsNull", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsConvert() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsConvert", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsConvert(int fromType, int toType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsConvert", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsTableCorrelationNames", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsDifferentTableCorrelationNames() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsDifferentTableCorrelationNames", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsExpressionsInOrderBy", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsOrderByUnrelated", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsGroupBy() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsGroupBy", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsGroupByUnrelated", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsGroupByBeyondSelect", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsLikeEscapeClause", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsMultipleResultSets", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsMultipleTransactions", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsNonNullableColumns", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsMinimumSQLGrammar", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCoreSQLGrammar", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsExtendedSQLGrammar", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsANSI92EntryLevelSQL", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsANSI92IntermediateSQL", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsANSI92FullSQL", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsIntegrityEnhancementFacility", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsOuterJoins() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsOuterJoins", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsFullOuterJoins", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsLimitedOuterJoins", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public String getSchemaTerm() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getSchemaTerm", this);

            OracleLog.recursiveTrace = false;
        }

        return "schema";
    }

    public String getProcedureTerm() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getProcedureTerm", this);

            OracleLog.recursiveTrace = false;
        }

        return "procedure";
    }

    public String getCatalogTerm() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getCatalogTerm", this);

            OracleLog.recursiveTrace = false;
        }

        return "";
    }

    public boolean isCatalogAtStart() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "isCatalogAtStart", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public String getCatalogSeparator() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getCatalogSeparator", this);

            OracleLog.recursiveTrace = false;
        }

        return "";
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSchemasInDataManipulation", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSchemasInProcedureCalls", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSchemasInTableDefinitions", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSchemasInIndexDefinitions", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSchemasInPrivilegeDefinitions", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCatalogsInDataManipulation", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCatalogsInProcedureCalls", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCatalogsInTableDefinitions", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCatalogsInIndexDefinitions", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCatalogsInPrivilegeDefinitions", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsPositionedDelete() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsPositionedDelete", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsPositionedUpdate", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSelectForUpdate", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsStoredProcedures() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsStoredProcedures", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSubqueriesInComparisons", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSubqueriesInExists", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSubqueriesInIns", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsSubqueriesInQuantifieds", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsCorrelatedSubqueries", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsUnion() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsUnion", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsUnionAll() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsUnionAll", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsOpenCursorsAcrossCommit", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsOpenCursorsAcrossRollback", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsOpenStatementsAcrossCommit", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsOpenStatementsAcrossRollback", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxBinaryLiteralLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 1000;
    }

    public int getMaxCharLiteralLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxCharLiteralLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 2000;
    }

    public int getMaxColumnNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxColumnNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 30;
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxColumnsInGroupBy", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxColumnsInIndex() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxColumnsInIndex", this);

            OracleLog.recursiveTrace = false;
        }

        return 32;
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxColumnsInOrderBy", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxColumnsInSelect() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxColumnsInSelect", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxColumnsInTable() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxColumnsInTable", this);

            OracleLog.recursiveTrace = false;
        }

        return 1000;
    }

    public int getMaxConnections() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxConnections", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxCursorNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxCursorNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxIndexLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxIndexLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxSchemaNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxSchemaNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 30;
    }

    public int getMaxProcedureNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxProcedureNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 30;
    }

    public int getMaxCatalogNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxCatalogNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxRowSize() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxRowSize", this);

            OracleLog.recursiveTrace = false;
        }

        return 2000;
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "doesMaxRowSizeIncludeBlobs", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public int getMaxStatementLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxStatementLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 65535;
    }

    public int getMaxStatements() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxStatements", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxTableNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxTableNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 30;
    }

    public int getMaxTablesInSelect() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxTablesInSelect", this);

            OracleLog.recursiveTrace = false;
        }

        return 0;
    }

    public int getMaxUserNameLength() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getMaxUserNameLength", this);

            OracleLog.recursiveTrace = false;
        }

        return 30;
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getDefaultTransactionIsolation", this);

            OracleLog.recursiveTrace = false;
        }

        return 2;
    }

    public boolean supportsTransactions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsTransactions", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsTransactionIsolationLevel", this);

            OracleLog.recursiveTrace = false;
        }

        return (level == 2) || (level == 8);
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER,
                                       "supportsDataDefinitionAndDataManipulationTransactions",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "supportsDataManipulationTransactionsOnly",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "dataDefinitionCausesTransactionCommit", this);

            OracleLog.recursiveTrace = false;
        }

        return true;
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "dataDefinitionIgnoredInTransactions", this);

            OracleLog.recursiveTrace = false;
        }

        return false;
    }

    public synchronized ResultSet getProcedures(String catalog, String schemaPattern,
            String procedureNamePattern) throws SQLException {
        String standaloneQuery = "SELECT\n  -- Standalone procedures and functions\n  NULL AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Standalone procedure or function' AS remarks,\n  DECODE(object_type, 'PROCEDURE', 1,\n                      'FUNCTION', 2,\n                      0) AS procedure_type\nFROM all_objects\nWHERE (object_type = 'PROCEDURE' OR object_type = 'FUNCTION')\n  AND owner LIKE :1 ESCAPE '/'\n  AND object_name LIKE :2 ESCAPE '/'\n";

        String packagedProcsNoArgsSelect = "SELECT\n  -- Packaged procedures with no arguments\n  package_name AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Packaged procedure' AS remarks,\n  1 AS procedure_type\nFROM all_arguments\nWHERE argument_name IS NULL\n  AND data_type IS NULL\n  AND ";

        String packagedProcsArgsSelect = "SELECT\n  -- Packaged procedures with arguments\n  package_name AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Packaged procedure' AS remarks,\n  1 AS procedure_type\nFROM all_arguments\nWHERE argument_name IS NOT NULL\n  AND position = 1\n  AND position = sequence\n  AND ";

        String packagedFunctionsSelect = "SELECT\n  -- Packaged functions\n  package_name AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Packaged function' AS remarks,\n  2 AS procedure_type\nFROM all_arguments\nWHERE argument_name IS NULL\n  AND in_out = 'OUT'\n  AND ";

        String catalogSpecifiedWhere = "package_name LIKE :3 ESCAPE '/'\n  AND owner LIKE :4 ESCAPE '/'\n  AND object_name LIKE :5 ESCAPE '/'\n";

        String catalogNotSpecifiedWhere = "package_name IS NOT NULL\n  AND owner LIKE :6 ESCAPE '/'\n  AND object_name LIKE :7 ESCAPE '/'\n";

        String orderBy = "ORDER BY procedure_schem, procedure_name\n";

        PreparedStatement s = null;
        String finalQuery = null;

        String schemaBind = schemaPattern;

        if (schemaPattern == null)
            schemaBind = "%";
        else if (schemaPattern.equals("")) {
            schemaBind = getUserName().toUpperCase();
        }
        String procedureNameBind = procedureNamePattern;

        if (procedureNamePattern == null)
            procedureNameBind = "%";
        else if (procedureNamePattern.equals("")) {
            DatabaseError.throwSqlException(74);
        }
        if (catalog == null) {
            finalQuery = standaloneQuery + "UNION ALL " + packagedProcsNoArgsSelect
                    + catalogNotSpecifiedWhere + "UNION ALL " + packagedProcsArgsSelect
                    + catalogNotSpecifiedWhere + "UNION ALL " + packagedFunctionsSelect
                    + catalogNotSpecifiedWhere + orderBy;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "getProcedures Final SQL statement:\n"
                        + finalQuery, this);

                OracleLog.recursiveTrace = false;
            }

            s = this.connection.prepareStatement(finalQuery);

            s.setString(1, schemaBind);
            s.setString(2, procedureNameBind);
            s.setString(3, schemaBind);
            s.setString(4, procedureNameBind);
            s.setString(5, schemaBind);
            s.setString(6, procedureNameBind);
            s.setString(7, schemaBind);
            s.setString(8, procedureNameBind);
        } else if (catalog.equals("")) {
            finalQuery = standaloneQuery;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "getProcedures Final SQL statement:\n"
                        + finalQuery, this);

                OracleLog.recursiveTrace = false;
            }

            s = this.connection.prepareStatement(finalQuery);

            s.setString(1, schemaBind);
            s.setString(2, procedureNameBind);
        } else {
            finalQuery = packagedProcsNoArgsSelect + catalogSpecifiedWhere + "UNION ALL "
                    + packagedProcsArgsSelect + catalogSpecifiedWhere + "UNION ALL "
                    + packagedFunctionsSelect + catalogSpecifiedWhere + orderBy;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "getProcedures Final SQL statement:\n"
                        + finalQuery, this);

                OracleLog.recursiveTrace = false;
            }

            s = this.connection.prepareStatement(finalQuery);

            s.setString(1, catalog);
            s.setString(2, schemaBind);
            s.setString(3, procedureNameBind);
            s.setString(4, catalog);
            s.setString(5, schemaBind);
            s.setString(6, procedureNameBind);
            s.setString(7, catalog);
            s.setString(8, schemaBind);
            s.setString(9, procedureNameBind);
        }

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public synchronized ResultSet getProcedureColumns(String catalog, String schemaPattern,
            String procedureNamePattern, String columnNamePattern) throws SQLException {
        String baseQuery = "SELECT package_name AS procedure_cat,\n       owner AS procedure_schem,\n       object_name AS procedure_name,\n       argument_name AS column_name,\n       DECODE(position, 0, 5,\n                        DECODE(in_out, 'IN', 1,\n                                       'OUT', 4,\n                                       'IN/OUT', 2,\n                                       0)) AS column_type,\n       DECODE (data_type, 'CHAR', 1,\n                          'VARCHAR2', 12,\n                          'NUMBER', 3,\n                          'LONG', -1,\n                          'DATE', "
                + (this.connection.isV8Compatible() ? "93,\n" : "91,\n")
                + "                          'RAW', -3,\n"
                + "                          'LONG RAW', -4,\n"
                + "                          'TIMESTAMP', 93, \n"
                + "                          'TIMESTAMP WITH TIME ZONE', -101, \n"
                + "               'TIMESTAMP WITH LOCAL TIME ZONE', -102, \n"
                + "               'INTERVAL YEAR TO MONTH', -103, \n"
                + "               'INTERVAL DAY TO SECOND', -104, \n"
                + "               'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101,"
                + "               1111) AS data_type,\n"
                + "       DECODE(data_type, 'OBJECT', type_owner || '.' || type_name, "
                + "              data_type) AS type_name,\n"
                + "       DECODE (data_precision, NULL, data_length,\n"
                + "                               data_precision) AS precision,\n"
                + "       data_length AS length,\n"
                + "       data_scale AS scale,\n"
                + "       10 AS radix,\n"
                + "       1 AS nullable,\n"
                + "       NULL AS remarks,\n"
                + "       sequence,\n"
                + "       overload,\n"
                + "       default_value\n"
                + " FROM all_arguments\n"
                + "WHERE owner LIKE :1 ESCAPE '/'\n"
                + "  AND object_name LIKE :2 ESCAPE '/'\n";

        String catalogSpecifiedWhere = "  AND package_name LIKE :3 ESCAPE '/'\n";
        String catalogEmptyWhere = "  AND package_name IS NULL\n";

        String columnSpecifiedWhere = "  AND argument_name LIKE :4 ESCAPE '/'\n";
        String columnNotSpecifiedWhere = "  AND (argument_name LIKE :5 ESCAPE '/'\n       OR (argument_name IS NULL\n           AND data_type IS NOT NULL))\n";

        String orderBy = "ORDER BY procedure_schem, procedure_name, overload, sequence\n";

        String finalQuery = null;
        PreparedStatement s = null;
        String columnWhere = null;

        String schemaBind = schemaPattern;

        if (schemaPattern == null)
            schemaBind = "%";
        else if (schemaPattern.equals("")) {
            schemaBind = getUserName().toUpperCase();
        }
        String procedureNameBind = procedureNamePattern;

        if (procedureNamePattern == null)
            procedureNameBind = "%";
        else if (procedureNamePattern.equals("")) {
            DatabaseError.throwSqlException(74);
        }
        String columnNameBind = columnNamePattern;

        if ((columnNamePattern == null) || (columnNamePattern.equals("%"))) {
            columnNameBind = "%";
            columnWhere = columnNotSpecifiedWhere;
        } else if (columnNamePattern.equals("")) {
            DatabaseError.throwSqlException(74);
        } else {
            columnWhere = columnSpecifiedWhere;
        }
        if (catalog == null) {
            finalQuery = baseQuery + columnWhere + orderBy;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "getProcedureColumns Final SQL statement:\n"
                                                   + finalQuery, this);

                OracleLog.recursiveTrace = false;
            }

            s = this.connection.prepareStatement(finalQuery);

            s.setString(1, schemaBind);
            s.setString(2, procedureNameBind);
            s.setString(3, columnNameBind);
        } else if (catalog.equals("")) {
            finalQuery = baseQuery + catalogEmptyWhere + columnWhere + orderBy;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "getProcedureColumns Final SQL statement:\n"
                                                   + finalQuery, this);

                OracleLog.recursiveTrace = false;
            }

            s = this.connection.prepareStatement(finalQuery);

            s.setString(1, schemaBind);
            s.setString(2, procedureNameBind);
            s.setString(3, columnNameBind);
        } else {
            finalQuery = baseQuery + catalogSpecifiedWhere + columnWhere + orderBy;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "getProcedureColumns Final SQL statement:\n"
                                                   + finalQuery, this);

                OracleLog.recursiveTrace = false;
            }

            s = this.connection.prepareStatement(finalQuery);

            s.setString(1, schemaBind);
            s.setString(2, procedureNameBind);
            s.setString(3, catalog);
            s.setString(4, columnNameBind);
        }

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public synchronized ResultSet getTables(String catalog, String schemaPattern,
            String tableNamePattern, String[] types) throws SQLException {
        String queryPart1 = "SELECT NULL AS table_cat,\n       o.owner AS table_schem,\n       o.object_name AS table_name,\n       o.object_type AS table_type,\n";

        String remarksSelect = "       c.comments AS remarks\n";
        String noRemarksSelect = "       NULL AS remarks\n";

        String remarksFrom = "  FROM all_objects o, all_tab_comments c\n";
        String noRemarksFrom = "  FROM all_objects o\n";

        String wherePart1 = "  WHERE o.owner LIKE :1 ESCAPE '/'\n    AND o.object_name LIKE :2 ESCAPE '/'\n";

        String whereRemarks = "    AND o.owner = c.owner (+)\n    AND o.object_name = c.table_name (+)\n";

        boolean wantSynonyms = false;

        String whereType = "";
        String whereTypeNoSyn = "";

        if (types != null) {
            whereType = "    AND o.object_type IN ('xxx'";
            whereTypeNoSyn = "    AND o.object_type IN ('xxx'";

            for (int i = 0; i < types.length; i++) {
                if (types[i].equals("SYNONYM")) {
                    whereType = whereType + ", '" + types[i] + "'";
                    wantSynonyms = true;
                } else {
                    whereType = whereType + ", '" + types[i] + "'";
                    whereTypeNoSyn = whereTypeNoSyn + ", '" + types[i] + "'";
                }
            }

            whereType = whereType + ")\n";
            whereTypeNoSyn = whereTypeNoSyn + ")\n";
        } else {
            wantSynonyms = true;
            whereType = "    AND o.object_type IN ('TABLE', 'SYNONYM', 'VIEW')\n";
            whereTypeNoSyn = "    AND o.object_type IN ('TABLE', 'VIEW')\n";
        }

        String orderBy = "  ORDER BY table_type, table_schem, table_name\n";

        String synonymsUnion = "SELECT NULL AS table_cat,\n       s.owner AS table_schem,\n       s.synonym_name AS table_name,\n       'SYNONYM' AS table_table_type,\n";

        String remarksSynSelect = "       c.comments AS remarks\n";
        String noRemarksSynSelect = "       NULL AS remarks\n";

        String remarksSynFrom = "  FROM all_synonyms s, all_objects o, all_tab_comments c\n";

        String noRemarksSynFrom = "  FROM all_synonyms s, all_objects o\n";

        String synWherePart1 = "  WHERE s.owner LIKE :3 ESCAPE '/'\n    AND s.synonym_name LIKE :4 ESCAPE '/'\n    AND s.table_owner = o.owner\n    AND s.table_name = o.object_name\n    AND o.object_type IN ('TABLE', 'VIEW')\n";

        String finalQuery = "";

        finalQuery = finalQuery + queryPart1;

        if (this.connection.getRemarksReporting())
            finalQuery = finalQuery + remarksSelect + remarksFrom;
        else {
            finalQuery = finalQuery + noRemarksSelect + noRemarksFrom;
        }
        finalQuery = finalQuery + wherePart1;

        if (this.connection.getRestrictGetTables())
            finalQuery = finalQuery + whereTypeNoSyn;
        else {
            finalQuery = finalQuery + whereType;
        }
        if (this.connection.getRemarksReporting()) {
            finalQuery = finalQuery + whereRemarks;
        }
        if ((wantSynonyms) && (this.connection.getRestrictGetTables())) {
            finalQuery = finalQuery + "UNION\n" + synonymsUnion;

            if (this.connection.getRemarksReporting())
                finalQuery = finalQuery + remarksSynSelect + remarksSynFrom;
            else {
                finalQuery = finalQuery + noRemarksSynSelect + noRemarksSynFrom;
            }
            finalQuery = finalQuery + synWherePart1;

            if (this.connection.getRemarksReporting()) {
                finalQuery = finalQuery + whereRemarks;
            }
        }
        finalQuery = finalQuery + orderBy;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getTables final SQL statement is:\n"
                    + finalQuery, this);

            OracleLog.recursiveTrace = false;
        }

        PreparedStatement s = this.connection.prepareStatement(finalQuery);

        s.setString(1, schemaPattern == null ? "%" : schemaPattern);
        s.setString(2, tableNamePattern == null ? "%" : tableNamePattern);

        if ((wantSynonyms) && (this.connection.getRestrictGetTables())) {
            s.setString(3, schemaPattern == null ? "%" : schemaPattern);
            s.setString(4, tableNamePattern == null ? "%" : tableNamePattern);
        }

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public ResultSet getSchemas() throws SQLException {
        Statement s = this.connection.createStatement();
        String basic_query = "SELECT username AS table_schem FROM all_users ORDER BY table_schem";

        OracleResultSet rs = (OracleResultSet) s.executeQuery(basic_query);

        rs.closeStatementOnClose();

        return rs;
    }

    public ResultSet getCatalogs() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getCatalogs", this);

            OracleLog.recursiveTrace = false;
        }

        Statement s = this.connection.createStatement();
        String query = "select 'nothing' as table_cat from dual where 1 = 2";
        OracleResultSet rs = (OracleResultSet) s.executeQuery(query);

        rs.closeStatementOnClose();

        return rs;
    }

    public ResultSet getTableTypes() throws SQLException {
        Statement s = this.connection.createStatement();
        String query = "select 'TABLE' as table_type from dual\nunion select 'VIEW' as table_type from dual\nunion select 'SYNONYM' as table_type from dual\n";

        OracleResultSet rs = (OracleResultSet) s.executeQuery(query);

        rs.closeStatementOnClose();

        return rs;
    }

    public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern,
            String columnNamePattern) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public synchronized ResultSet getColumnPrivileges(String catalog, String schemaPattern,
            String tableNamePattern, String columnNamePattern) throws SQLException {
        PreparedStatement s = this.connection
                .prepareStatement("SELECT NULL AS table_cat,\n       table_schema AS table_schem,\n       table_name,\n       column_name,\n       grantor,\n       grantee,\n       privilege,\n       grantable AS is_grantable\nFROM all_col_privs\nWHERE table_schema LIKE :1 ESCAPE '/'\n  AND table_name LIKE :2 ESCAPE '/'\n  AND column_name LIKE :3 ESCAPE '/'\nORDER BY column_name, privilege\n");

        s.setString(1, schemaPattern == null ? "%" : schemaPattern);
        s.setString(2, tableNamePattern == null ? "%" : tableNamePattern);
        s.setString(3, columnNamePattern == null ? "%" : columnNamePattern);

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public synchronized ResultSet getTablePrivileges(String catalog, String schemaPattern,
            String tableNamePattern) throws SQLException {
        PreparedStatement s = this.connection
                .prepareStatement("SELECT NULL AS table_cat,\n       table_schema AS table_schem,\n       table_name,\n       grantor,\n       grantee,\n       privilege,\n       grantable AS is_grantable\nFROM all_tab_privs\nWHERE table_schema LIKE :1 ESCAPE '/'\n  AND table_name LIKE :2 ESCAPE '/'\nORDER BY table_schem, table_name, privilege\n");

        s.setString(1, schemaPattern == null ? "%" : schemaPattern);
        s.setString(2, tableNamePattern == null ? "%" : tableNamePattern);

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public synchronized ResultSet getBestRowIdentifier(String catalog, String schema, String table,
            int scope, boolean nullable) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getBestRowIdentifier", this);

            OracleLog.recursiveTrace = false;
        }

        PreparedStatement s = this.connection
                .prepareStatement("SELECT 1 AS scope, 'ROWID' AS column_name, -8 AS data_type,\n 'ROWID' AS type_name, 0 AS column_size, 0 AS buffer_length,\n       0 AS decimal_digits, 2 AS pseudo_column\nFROM DUAL\nWHERE :1 = 1\nUNION\nSELECT 2 AS scope,\n  t.column_name,\n DECODE (t.data_type, 'CHAR', 1, 'VARCHAR2', 12, 'NUMBER', 3,\n 'LONG', -1, 'DATE', "
                        + (this.connection.isV8Compatible() ? "93,\n" : "91,\n")
                        + " 'RAW', -3, 'LONG RAW', -4, \n"
                        + " 'TIMESTAMP(6)', 93, "
                        + " 'TIMESTAMP(6) WITH TIME ZONE', -101, \n"
                        + " 'TIMESTAMP(6) WITH LOCAL TIME ZONE', -102, \n"
                        + " 'INTERVAL YEAR(2) TO MONTH', -103, \n"
                        + " 'INTERVAL DAY(2) TO SECOND(6)', -104, \n"
                        + " 'BINARY_FLOAT', 100, "
                        + " 'BINARY_DOUBLE', 101,"
                        + " 1111)\n"
                        + " AS data_type,\n"
                        + " t.data_type AS type_name,\n"
                        + " DECODE (t.data_precision, null, t.data_length, t.data_precision)\n"
                        + "  AS column_size,\n"
                        + "  0 AS buffer_length,\n"
                        + "  t.data_scale AS decimal_digits,\n"
                        + "       1 AS pseudo_column\n"
                        + "FROM all_tab_columns t, all_ind_columns i\n"
                        + "WHERE :2 = 1\n"
                        + "  AND t.table_name = :3\n"
                        + "  AND t.owner like :4 escape '/'\n"
                        + "  AND t.nullable != :5\n"
                        + "  AND t.owner = i.table_owner\n"
                        + "  AND t.table_name = i.table_name\n"
                        + "  AND t.column_name = i.column_name\n");

        switch (scope) {
        case 0:
            s.setInt(1, 0);
            s.setInt(2, 0);

            break;
        case 1:
            s.setInt(1, 1);
            s.setInt(2, 1);

            break;
        case 2:
            s.setInt(1, 0);
            s.setInt(2, 1);
        }

        s.setString(3, table);
        s.setString(4, schema == null ? "%" : schema);
        s.setString(5, nullable ? "X" : "Y");

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public synchronized ResultSet getVersionColumns(String catalog, String schema, String table)
            throws SQLException {
        PreparedStatement s = this.connection
                .prepareStatement("SELECT 0 AS scope,\n t.column_name,\n DECODE (c.data_type, 'CHAR', 1, 'VARCHAR2', 12, 'NUMBER', 3,\n  'LONG', -1, 'DATE',  "
                        + (this.connection.isV8Compatible() ? "93,\n" : "91,\n")
                        + "  'RAW', -3, 'LONG RAW', -4, "
                        + "  'TIMESTAMP(6)', 93, 'TIMESTAMP(6) WITH TIME ZONE', -101, \n"
                        + "  'TIMESTAMP(6) WITH LOCAL TIME ZONE', -102, \n"
                        + "  'INTERVAL YEAR(2) TO MONTH', -103, \n"
                        + "  'INTERVAL DAY(2) TO SECOND(6)', -104, \n"
                        + "  'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101,"
                        + "   1111)\n "
                        + " AS data_type,\n"
                        + "       c.data_type AS type_name,\n"
                        + " DECODE (c.data_precision, null, c.data_length, c.data_precision)\n"
                        + "   AS column_size,\n"
                        + "       0 as buffer_length,\n"
                        + "   c.data_scale as decimal_digits,\n"
                        + "   0 as pseudo_column\n"
                        + "FROM all_trigger_cols t, all_tab_columns c\n"
                        + "WHERE t.table_name = :1\n"
                        + "  AND c.owner like :2 escape '/'\n"
                        + " AND t.table_owner = c.owner\n"
                        + "  AND t.table_name = c.table_name\n"
                        + " AND t.column_name = c.column_name\n");

        s.setString(1, table);
        s.setString(2, schema == null ? "%" : schema);

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public ResultSet getPrimaryKeys(String catalog, String schema, String table)
            throws SQLException {
        PreparedStatement s = this.connection
                .prepareStatement("SELECT NULL AS table_cat,\n       c.owner AS table_schem,\n       c.table_name,\n       c.column_name,\n       c.position AS key_seq,\n       c.constraint_name AS pk_name\nFROM all_cons_columns c, all_constraints k\nWHERE k.constraint_type = 'P'\n  AND k.table_name = :1\n  AND k.owner like :2 escape '/'\n  AND k.constraint_name = c.constraint_name \n  AND k.table_name = c.table_name \n  AND k.owner = c.owner \nORDER BY column_name\n");

        s.setString(1, table);
        s.setString(2, schema == null ? "%" : schema);

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    ResultSet keys_query(String pSchema, String pTable, String fSchema, String fTable,
            String orderByClause) throws SQLException {
        int i = 1;
        int pTBnd = pTable != null ? i++ : 0;
        int fTBnd = fTable != null ? i++ : 0;
        int pSBnd = (pSchema != null) && (pSchema.length() > 0) ? i++ : 0;
        int fSBnd = (fSchema != null) && (fSchema.length() > 0) ? i++ : 0;

        PreparedStatement s = this.connection
                .prepareStatement("SELECT NULL AS pktable_cat,\n       p.owner as pktable_schem,\n       p.table_name as pktable_name,\n       pc.column_name as pkcolumn_name,\n       NULL as fktable_cat,\n       f.owner as fktable_schem,\n       f.table_name as fktable_name,\n       fc.column_name as fkcolumn_name,\n       fc.position as key_seq,\n       NULL as update_rule,\n       decode (f.delete_rule, 'CASCADE', 0, 'SET NULL', 2, 1) as delete_rule,\n       f.constraint_name as fk_name,\n       p.constraint_name as pk_name,\n       decode(f.deferrable,       'DEFERRABLE',5      ,'NOT DEFERRABLE',7      , 'DEFERRED', 6      ) deferrability \n      FROM all_cons_columns pc, all_constraints p,\n      all_cons_columns fc, all_constraints f\nWHERE 1 = 1\n"
                        + (pTBnd != 0 ? "  AND p.table_name = :1\n" : "")
                        + (fTBnd != 0 ? "  AND f.table_name = :2\n" : "")
                        + (pSBnd != 0 ? "  AND p.owner = :3\n" : "")
                        + (fSBnd != 0 ? "  AND f.owner = :4\n" : "")
                        + "  AND f.constraint_type = 'R'\n"
                        + "  AND p.owner = f.r_owner\n"
                        + "  AND p.constraint_name = f.r_constraint_name\n"
                        + "  AND p.constraint_type = 'P'\n"
                        + "  AND pc.owner = p.owner\n"
                        + "  AND pc.constraint_name = p.constraint_name\n"
                        + "  AND pc.table_name = p.table_name\n"
                        + "  AND fc.owner = f.owner\n"
                        + "  AND fc.constraint_name = f.constraint_name\n"
                        + "  AND fc.table_name = f.table_name\n"
                        + "  AND fc.position = pc.position\n" + orderByClause);

        if (pTBnd != 0) {
            s.setString(pTBnd, pTable);
        }

        if (fTBnd != 0) {
            s.setString(fTBnd, fTable);
        }

        if (pSBnd != 0) {
            s.setString(pSBnd, pSchema);
        }

        if (fSBnd != 0) {
            s.setString(fSBnd, fSchema);
        }

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public synchronized ResultSet getImportedKeys(String catalog, String schema, String table)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getImportedKeys", this);

            OracleLog.recursiveTrace = false;
        }

        return keys_query(null, null, schema, table,
                          "ORDER BY pktable_schem, pktable_name, key_seq");
    }

    public ResultSet getExportedKeys(String catalog, String schema, String table)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getExportedKeys", this);

            OracleLog.recursiveTrace = false;
        }

        return keys_query(schema, table, null, null,
                          "ORDER BY fktable_schem, fktable_name, key_seq");
    }

    public ResultSet getCrossReference(String primaryCatalog, String primarySchema,
            String primaryTable, String foreignCatalog, String foreignSchema, String foreignTable)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getCrossReference", this);

            OracleLog.recursiveTrace = false;
        }

        return keys_query(primarySchema, primaryTable, foreignSchema, foreignTable,
                          "ORDER BY fktable_schem, fktable_name, key_seq");
    }

    public ResultSet getTypeInfo() throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public synchronized ResultSet getIndexInfo(String catalog, String schema, String table,
            boolean unique, boolean approximate) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getIndexInfo", this);

            OracleLog.recursiveTrace = false;
        }

        Statement s = this.connection.createStatement();

        if (((schema != null) && (schema.length() != 0) && (!OracleSql.isValidObjectName(schema)))
                || ((table != null) && (table.length() != 0) && (!OracleSql
                        .isValidObjectName(table)))) {
            DatabaseError.throwSqlException(68);
        }

        String analyze = "analyze table "
                + (schema == null ? "" : new StringBuffer().append(schema).append(".").toString())
                + table + (approximate ? " estimate statistics" : " compute statistics");

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, analyze, this);

            OracleLog.recursiveTrace = false;
        }

        s.executeUpdate(analyze);

        String table_query = "select null as table_cat,\n       owner as table_schem,\n       table_name,\n       0 as NON_UNIQUE,\n       null as index_qualifier,\n       null as index_name, 0 as type,\n       0 as ordinal_position, null as column_name,\n       null as asc_or_desc,\n       num_rows as cardinality,\n       blocks as pages,\n       null as filter_condition\nfrom all_tables\nwhere table_name = '"
                + table + "'\n";

        String table_schema_select = "";

        if ((schema != null) && (schema.length() > 0)) {
            table_schema_select = "  and owner = '" + schema + "'\n";
        }

        String index_query = "select null as table_cat,\n       i.owner as table_schem,\n       i.table_name,\n       decode (i.uniqueness, 'UNIQUE', 0, 1),\n       null as index_qualifier,\n       i.index_name,\n       1 as type,\n       c.column_position as ordinal_position,\n       c.column_name,\n       null as asc_or_desc,\n       i.distinct_keys as cardinality,\n       i.leaf_blocks as pages,\n       null as filter_condition\nfrom all_indexes i, all_ind_columns c\nwhere i.table_name = '"
                + table + "'\n";

        String index_schema_select = "";

        if ((schema != null) && (schema.length() > 0)) {
            index_schema_select = "  and i.owner = '" + schema + "'\n";
        }
        String index_unique_select = "";

        if (unique) {
            index_unique_select = "  and i.uniqueness = 'UNIQUE'\n";
        }
        String index_join_select = "  and i.index_name = c.index_name\n  and i.table_owner = c.table_owner\n  and i.table_name = c.table_name\n  and i.owner = c.index_owner\n";

        String index_order_by = "order by non_unique, type, index_name, ordinal_position\n";

        String query = table_query + table_schema_select + "union\n" + index_query
                + index_schema_select + index_unique_select + index_join_select + index_order_by;

        OracleResultSet rs = (OracleResultSet) s.executeQuery(query);

        rs.closeStatementOnClose();

        return rs;
    }

    SQLException fail() {
        SQLException ex = new SQLException("Not implemented yet");

        return ex;
    }

    public boolean supportsResultSetType(int type) throws SQLException {
        return true;
    }

    public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
        return true;
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        return type != OracleResultSet.TYPE_FORWARD_ONLY;
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {
        return type != OracleResultSet.TYPE_FORWARD_ONLY;
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        return type == OracleResultSet.TYPE_SCROLL_SENSITIVE;
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {
        return false;
    }

    public boolean updatesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean deletesAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean insertsAreDetected(int type) throws SQLException {
        return false;
    }

    public boolean supportsBatchUpdates() throws SQLException {
        return true;
    }

    public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern,
            int[] types) throws SQLException {
        boolean _queryADT = false;

        if ((typeNamePattern == null) || (typeNamePattern.length() == 0)) {
            _queryADT = false;
        } else if (types == null) {
            _queryADT = true;
        } else {
            for (int i = 0; i < types.length; i++) {
                if (types[i] != 2002)
                    continue;
                _queryADT = true;

                break;
            }

        }

        StringBuffer sql = new StringBuffer();

        sql
                .append("SELECT NULL AS TYPE_CAT, owner AS TYPE_SCHEM, type_name, NULL AS CLASS_NAME, 'STRUCT' AS DATA_TYPE, NULL AS REMARKS FROM all_types ");

        if (_queryADT) {
            sql
                    .append("WHERE typecode = 'OBJECT' AND owner LIKE :1 ESCAPE '/' AND type_name LIKE :2 ESCAPE '/'");
        } else {
            sql.append("WHERE 1 = 2");
        }

        PreparedStatement s = this.connection.prepareStatement(sql.substring(0, sql.length()));

        if (_queryADT) {
            String[] _schema = new String[1];
            String[] _type = new String[1];

            if (SQLName.parse(typeNamePattern, _schema, _type)) {
                s.setString(1, _schema[0]);
                s.setString(2, _type[0]);
            } else {
                if (schemaPattern != null)
                    s.setString(1, schemaPattern);
                else {
                    s.setNull(1, 12);
                }
                s.setString(2, typeNamePattern);
            }

        }

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public Connection getConnection() throws SQLException {
        return this.connection.getWrapper();
    }

    public boolean supportsSavepoints() throws SQLException {
        return true;
    }

    public boolean supportsNamedParameters() throws SQLException {
        return true;
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        return false;
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        return true;
    }

    public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern)
            throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern)
            throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern,
            String attributeNamePattern) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();

        return null;
    }

    public boolean supportsResultSetHoldability(int holdability) throws SQLException {
        return holdability == 1;
    }

    public int getResultSetHoldability() throws SQLException {
        return 1;
    }

    public int getDatabaseMajorVersion() throws SQLException {
        return this.connection.getVersionNumber() / 1000;
    }

    public int getDatabaseMinorVersion() throws SQLException {
        return this.connection.getVersionNumber() % 1000 / 100;
    }

    public int getJDBCMajorVersion() throws SQLException {
        return DRIVER_MAJOR_VERSION;
    }

    public int getJDBCMinorVersion() throws SQLException {
        return DRIVER_MINOR_VERSION;
    }

    public int getSQLStateType() throws SQLException {
        return 0;
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        return true;
    }

    public boolean supportsStatementPooling() throws SQLException {
        return true;
    }

    public static String getDriverNameInfo() throws SQLException {
        return DRIVER_NAME;
    }

    public static String getDriverVersionInfo() throws SQLException {
        return DRIVER_VERSION;
    }

    public static int getDriverMajorVersionInfo() {
        return DRIVER_MAJOR_VERSION;
    }

    public static int getDriverMinorVersionInfo() {
        return DRIVER_MINOR_VERSION;
    }

    public static void setGetLobPrecision(boolean enable) throws SQLException {
    }

    public static boolean getGetLobPrecision() throws SQLException {
        return false;
    }

    public static String getLobPrecision() throws SQLException {
        return getGetLobPrecision() ? LOB_MAXSIZE : "-1";
    }

    public long getLobMaxLength() throws SQLException {
        return this.connection.getVersionNumber() >= 10000 ? 9223372036854775807L
                : LOB_MAXLENGTH_32BIT;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.OracleDatabaseMetaData"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.OracleDatabaseMetaData JD-Core Version: 0.6.0
 */