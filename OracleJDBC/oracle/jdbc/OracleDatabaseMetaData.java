package oracle.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleSql;
import oracle.jdbc.internal.OracleResultSet;
import oracle.sql.SQLName;

public class OracleDatabaseMetaData
  implements DatabaseMetaData
{
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:48_PDT_2005";

  public OracleDatabaseMetaData(OracleConnection paramOracleConnection)
  {
    this.connection = paramOracleConnection.physicalConnectionWithin();
  }

  public boolean allProceduresAreCallable()
    throws SQLException
  {
    return false;
  }

  public boolean allTablesAreSelectable()
    throws SQLException
  {
    return false;
  }

  public String getURL()
    throws SQLException
  {
    return this.connection.getURL();
  }

  public String getUserName()
    throws SQLException
  {
    return this.connection.getUserName();
  }

  public boolean isReadOnly()
    throws SQLException
  {
    return false;
  }

  public boolean nullsAreSortedHigh()
    throws SQLException
  {
    return false;
  }

  public boolean nullsAreSortedLow()
    throws SQLException
  {
    return true;
  }

  public boolean nullsAreSortedAtStart()
    throws SQLException
  {
    return false;
  }

  public boolean nullsAreSortedAtEnd()
    throws SQLException
  {
    return false;
  }

  public String getDatabaseProductName()
    throws SQLException
  {
    return "Oracle";
  }

  public String getDatabaseProductVersion()
    throws SQLException
  {
    return this.connection.getDatabaseProductVersion();
  }

  public String getDriverName()
    throws SQLException
  {
    return DRIVER_NAME;
  }

  public String getDriverVersion()
    throws SQLException
  {
    return DRIVER_VERSION;
  }

  public int getDriverMajorVersion()
  {
    return DRIVER_MAJOR_VERSION;
  }

  public int getDriverMinorVersion()
  {
    return DRIVER_MINOR_VERSION;
  }

  public boolean usesLocalFiles()
    throws SQLException
  {
    return false;
  }

  public boolean usesLocalFilePerTable()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMixedCaseIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesUpperCaseIdentifiers()
    throws SQLException
  {
    return true;
  }

  public boolean storesLowerCaseIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesMixedCaseIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMixedCaseQuotedIdentifiers()
    throws SQLException
  {
    return true;
  }

  public boolean storesUpperCaseQuotedIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesLowerCaseQuotedIdentifiers()
    throws SQLException
  {
    return false;
  }

  public boolean storesMixedCaseQuotedIdentifiers()
    throws SQLException
  {
    return true;
  }

  public String getIdentifierQuoteString()
    throws SQLException
  {
    return "\"";
  }

  public String getSQLKeywords()
    throws SQLException
  {
    return "ACCESS, ADD, ALTER, AUDIT, CLUSTER, COLUMN, COMMENT, COMPRESS, CONNECT, DATE, DROP, EXCLUSIVE, FILE, IDENTIFIED, IMMEDIATE, INCREMENT, INDEX, INITIAL, INTERSECT, LEVEL, LOCK, LONG, MAXEXTENTS, MINUS, MODE, NOAUDIT, NOCOMPRESS, NOWAIT, NUMBER, OFFLINE, ONLINE, PCTFREE, PRIOR, all_PL_SQL_reserved_ words";
  }

  public String getNumericFunctions()
    throws SQLException
  {
    return "ABS,ACOS,ASIN,ATAN,ATAN2,CEILING,COS,EXP,FLOOR,LOG,LOG10,MOD,PI,POWER,ROUND,SIGN,SIN,SQRT,TAN,TRUNCATE";
  }

  public String getStringFunctions()
    throws SQLException
  {
    return "ASCII,CHAR,CONCAT,LCASE,LENGTH,LTRIM,REPLACE,RTRIM,SOUNDEX,SUBSTRING,UCASE";
  }

  public String getSystemFunctions()
    throws SQLException
  {
    return "USER";
  }

  public String getTimeDateFunctions()
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public String getSearchStringEscape()
    throws SQLException
  {
    return "//";
  }

  public String getExtraNameCharacters()
    throws SQLException
  {
    return "$#";
  }

  public boolean supportsAlterTableWithAddColumn()
    throws SQLException
  {
    return true;
  }

  public boolean supportsAlterTableWithDropColumn()
    throws SQLException
  {
    return false;
  }

  public boolean supportsColumnAliasing()
    throws SQLException
  {
    return true;
  }

  public boolean nullPlusNonNullIsNull()
    throws SQLException
  {
    return true;
  }

  public boolean supportsConvert()
    throws SQLException
  {
    return false;
  }

  public boolean supportsConvert(int paramInt1, int paramInt2)
    throws SQLException
  {
    return false;
  }

  public boolean supportsTableCorrelationNames()
    throws SQLException
  {
    return true;
  }

  public boolean supportsDifferentTableCorrelationNames()
    throws SQLException
  {
    return true;
  }

  public boolean supportsExpressionsInOrderBy()
    throws SQLException
  {
    return true;
  }

  public boolean supportsOrderByUnrelated()
    throws SQLException
  {
    return true;
  }

  public boolean supportsGroupBy()
    throws SQLException
  {
    return true;
  }

  public boolean supportsGroupByUnrelated()
    throws SQLException
  {
    return true;
  }

  public boolean supportsGroupByBeyondSelect()
    throws SQLException
  {
    return true;
  }

  public boolean supportsLikeEscapeClause()
    throws SQLException
  {
    return true;
  }

  public boolean supportsMultipleResultSets()
    throws SQLException
  {
    return false;
  }

  public boolean supportsMultipleTransactions()
    throws SQLException
  {
    return true;
  }

  public boolean supportsNonNullableColumns()
    throws SQLException
  {
    return true;
  }

  public boolean supportsMinimumSQLGrammar()
    throws SQLException
  {
    return true;
  }

  public boolean supportsCoreSQLGrammar()
    throws SQLException
  {
    return true;
  }

  public boolean supportsExtendedSQLGrammar()
    throws SQLException
  {
    return true;
  }

  public boolean supportsANSI92EntryLevelSQL()
    throws SQLException
  {
    return true;
  }

  public boolean supportsANSI92IntermediateSQL()
    throws SQLException
  {
    return false;
  }

  public boolean supportsANSI92FullSQL()
    throws SQLException
  {
    return false;
  }

  public boolean supportsIntegrityEnhancementFacility()
    throws SQLException
  {
    return true;
  }

  public boolean supportsOuterJoins()
    throws SQLException
  {
    return true;
  }

  public boolean supportsFullOuterJoins()
    throws SQLException
  {
    return true;
  }

  public boolean supportsLimitedOuterJoins()
    throws SQLException
  {
    return true;
  }

  public String getSchemaTerm()
    throws SQLException
  {
    return "schema";
  }

  public String getProcedureTerm()
    throws SQLException
  {
    return "procedure";
  }

  public String getCatalogTerm()
    throws SQLException
  {
    return "";
  }

  public boolean isCatalogAtStart()
    throws SQLException
  {
    return false;
  }

  public String getCatalogSeparator()
    throws SQLException
  {
    return "";
  }

  public boolean supportsSchemasInDataManipulation()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSchemasInProcedureCalls()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSchemasInTableDefinitions()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSchemasInIndexDefinitions()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSchemasInPrivilegeDefinitions()
    throws SQLException
  {
    return true;
  }

  public boolean supportsCatalogsInDataManipulation()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInProcedureCalls()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInTableDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInIndexDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsCatalogsInPrivilegeDefinitions()
    throws SQLException
  {
    return false;
  }

  public boolean supportsPositionedDelete()
    throws SQLException
  {
    return false;
  }

  public boolean supportsPositionedUpdate()
    throws SQLException
  {
    return false;
  }

  public boolean supportsSelectForUpdate()
    throws SQLException
  {
    return true;
  }

  public boolean supportsStoredProcedures()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSubqueriesInComparisons()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSubqueriesInExists()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSubqueriesInIns()
    throws SQLException
  {
    return true;
  }

  public boolean supportsSubqueriesInQuantifieds()
    throws SQLException
  {
    return true;
  }

  public boolean supportsCorrelatedSubqueries()
    throws SQLException
  {
    return true;
  }

  public boolean supportsUnion()
    throws SQLException
  {
    return true;
  }

  public boolean supportsUnionAll()
    throws SQLException
  {
    return true;
  }

  public boolean supportsOpenCursorsAcrossCommit()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenCursorsAcrossRollback()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenStatementsAcrossCommit()
    throws SQLException
  {
    return false;
  }

  public boolean supportsOpenStatementsAcrossRollback()
    throws SQLException
  {
    return false;
  }

  public int getMaxBinaryLiteralLength()
    throws SQLException
  {
    return 1000;
  }

  public int getMaxCharLiteralLength()
    throws SQLException
  {
    return 2000;
  }

  public int getMaxColumnNameLength()
    throws SQLException
  {
    return 30;
  }

  public int getMaxColumnsInGroupBy()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInIndex()
    throws SQLException
  {
    return 32;
  }

  public int getMaxColumnsInOrderBy()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInSelect()
    throws SQLException
  {
    return 0;
  }

  public int getMaxColumnsInTable()
    throws SQLException
  {
    return 1000;
  }

  public int getMaxConnections()
    throws SQLException
  {
    return 0;
  }

  public int getMaxCursorNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxIndexLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxSchemaNameLength()
    throws SQLException
  {
    return 30;
  }

  public int getMaxProcedureNameLength()
    throws SQLException
  {
    return 30;
  }

  public int getMaxCatalogNameLength()
    throws SQLException
  {
    return 0;
  }

  public int getMaxRowSize()
    throws SQLException
  {
    return 2000;
  }

  public boolean doesMaxRowSizeIncludeBlobs()
    throws SQLException
  {
    return true;
  }

  public int getMaxStatementLength()
    throws SQLException
  {
    return 65535;
  }

  public int getMaxStatements()
    throws SQLException
  {
    return 0;
  }

  public int getMaxTableNameLength()
    throws SQLException
  {
    return 30;
  }

  public int getMaxTablesInSelect()
    throws SQLException
  {
    return 0;
  }

  public int getMaxUserNameLength()
    throws SQLException
  {
    return 30;
  }

  public int getDefaultTransactionIsolation()
    throws SQLException
  {
    return 2;
  }

  public boolean supportsTransactions()
    throws SQLException
  {
    return true;
  }

  public boolean supportsTransactionIsolationLevel(int paramInt)
    throws SQLException
  {
    return (paramInt == 2) || (paramInt == 8);
  }

  public boolean supportsDataDefinitionAndDataManipulationTransactions()
    throws SQLException
  {
    return true;
  }

  public boolean supportsDataManipulationTransactionsOnly()
    throws SQLException
  {
    return true;
  }

  public boolean dataDefinitionCausesTransactionCommit()
    throws SQLException
  {
    return true;
  }

  public boolean dataDefinitionIgnoredInTransactions()
    throws SQLException
  {
    return false;
  }

  public synchronized ResultSet getProcedures(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    String str1 = "SELECT\n  -- Standalone procedures and functions\n  NULL AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Standalone procedure or function' AS remarks,\n  DECODE(object_type, 'PROCEDURE', 1,\n                      'FUNCTION', 2,\n                      0) AS procedure_type\nFROM all_objects\nWHERE (object_type = 'PROCEDURE' OR object_type = 'FUNCTION')\n  AND owner LIKE :1 ESCAPE '/'\n  AND object_name LIKE :2 ESCAPE '/'\n";

    String str2 = "SELECT\n  -- Packaged procedures with no arguments\n  package_name AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Packaged procedure' AS remarks,\n  1 AS procedure_type\nFROM all_arguments\nWHERE argument_name IS NULL\n  AND data_type IS NULL\n  AND ";

    String str3 = "SELECT\n  -- Packaged procedures with arguments\n  package_name AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Packaged procedure' AS remarks,\n  1 AS procedure_type\nFROM all_arguments\nWHERE argument_name IS NOT NULL\n  AND position = 1\n  AND position = sequence\n  AND ";

    String str4 = "SELECT\n  -- Packaged functions\n  package_name AS procedure_cat,\n  owner AS procedure_schem,\n  object_name AS procedure_name,\n  NULL,\n  NULL,\n  NULL,\n  'Packaged function' AS remarks,\n  2 AS procedure_type\nFROM all_arguments\nWHERE argument_name IS NULL\n  AND in_out = 'OUT'\n  AND ";

    String str5 = "package_name LIKE :3 ESCAPE '/'\n  AND owner LIKE :4 ESCAPE '/'\n  AND object_name LIKE :5 ESCAPE '/'\n";

    String str6 = "package_name IS NOT NULL\n  AND owner LIKE :6 ESCAPE '/'\n  AND object_name LIKE :7 ESCAPE '/'\n";

    String str7 = "ORDER BY procedure_schem, procedure_name\n";

    PreparedStatement localPreparedStatement = null;
    String str8 = null;

    String str9 = paramString2;

    if (paramString2 == null)
      str9 = "%";
    else if (paramString2.equals("")) {
      str9 = getUserName().toUpperCase();
    }
    String str10 = paramString3;

    if (paramString3 == null)
      str10 = "%";
    else if (paramString3.equals("")) {
      DatabaseError.throwSqlException(74);
    }
    if (paramString1 == null)
    {
      str8 = str1 + "UNION ALL " + str2 + str6 + "UNION ALL " + str3 + str6 + "UNION ALL " + str4 + str6 + str7;

      localPreparedStatement = this.connection.prepareStatement(str8);

      localPreparedStatement.setString(1, str9);
      localPreparedStatement.setString(2, str10);
      localPreparedStatement.setString(3, str9);
      localPreparedStatement.setString(4, str10);
      localPreparedStatement.setString(5, str9);
      localPreparedStatement.setString(6, str10);
      localPreparedStatement.setString(7, str9);
      localPreparedStatement.setString(8, str10);
    }
    else if (paramString1.equals(""))
    {
      str8 = str1;

      localPreparedStatement = this.connection.prepareStatement(str8);

      localPreparedStatement.setString(1, str9);
      localPreparedStatement.setString(2, str10);
    }
    else
    {
      str8 = str2 + str5 + "UNION ALL " + str3 + str5 + "UNION ALL " + str4 + str5 + str7;

      localPreparedStatement = this.connection.prepareStatement(str8);

      localPreparedStatement.setString(1, paramString1);
      localPreparedStatement.setString(2, str9);
      localPreparedStatement.setString(3, str10);
      localPreparedStatement.setString(4, paramString1);
      localPreparedStatement.setString(5, str9);
      localPreparedStatement.setString(6, str10);
      localPreparedStatement.setString(7, paramString1);
      localPreparedStatement.setString(8, str9);
      localPreparedStatement.setString(9, str10);
    }

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public synchronized ResultSet getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SQLException
  {
    String str1 = "SELECT package_name AS procedure_cat,\n       owner AS procedure_schem,\n       object_name AS procedure_name,\n       argument_name AS column_name,\n       DECODE(position, 0, 5,\n                        DECODE(in_out, 'IN', 1,\n                                       'OUT', 4,\n                                       'IN/OUT', 2,\n                                       0)) AS column_type,\n       DECODE (data_type, 'CHAR', 1,\n                          'VARCHAR2', 12,\n                          'NUMBER', 3,\n                          'LONG', -1,\n                          'DATE', " + (this.connection.isV8Compatible() ? "93,\n" : "91,\n") + "                          'RAW', -3,\n" + "                          'LONG RAW', -4,\n" + "                          'TIMESTAMP', 93, \n" + "                          'TIMESTAMP WITH TIME ZONE', -101, \n" + "               'TIMESTAMP WITH LOCAL TIME ZONE', -102, \n" + "               'INTERVAL YEAR TO MONTH', -103, \n" + "               'INTERVAL DAY TO SECOND', -104, \n" + "               'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101," + "               1111) AS data_type,\n" + "       DECODE(data_type, 'OBJECT', type_owner || '.' || type_name, " + "              data_type) AS type_name,\n" + "       DECODE (data_precision, NULL, data_length,\n" + "                               data_precision) AS precision,\n" + "       data_length AS length,\n" + "       data_scale AS scale,\n" + "       10 AS radix,\n" + "       1 AS nullable,\n" + "       NULL AS remarks,\n" + "       sequence,\n" + "       overload,\n" + "       default_value\n" + " FROM all_arguments\n" + "WHERE owner LIKE :1 ESCAPE '/'\n" + "  AND object_name LIKE :2 ESCAPE '/'\n";

    String str2 = "  AND package_name LIKE :3 ESCAPE '/'\n";
    String str3 = "  AND package_name IS NULL\n";

    String str4 = "  AND argument_name LIKE :4 ESCAPE '/'\n";
    String str5 = "  AND (argument_name LIKE :5 ESCAPE '/'\n       OR (argument_name IS NULL\n           AND data_type IS NOT NULL))\n";

    String str6 = "ORDER BY procedure_schem, procedure_name, overload, sequence\n";

    String str7 = null;
    PreparedStatement localPreparedStatement = null;
    String str8 = null;

    String str9 = paramString2;

    if (paramString2 == null)
      str9 = "%";
    else if (paramString2.equals("")) {
      str9 = getUserName().toUpperCase();
    }
    String str10 = paramString3;

    if (paramString3 == null)
      str10 = "%";
    else if (paramString3.equals("")) {
      DatabaseError.throwSqlException(74);
    }
    String str11 = paramString4;

    if ((paramString4 == null) || (paramString4.equals("%")))
    {
      str11 = "%";
      str8 = str5;
    }
    else if (paramString4.equals("")) {
      DatabaseError.throwSqlException(74);
    } else {
      str8 = str4;
    }
    if (paramString1 == null)
    {
      str7 = str1 + str8 + str6;

      localPreparedStatement = this.connection.prepareStatement(str7);

      localPreparedStatement.setString(1, str9);
      localPreparedStatement.setString(2, str10);
      localPreparedStatement.setString(3, str11);
    }
    else if (paramString1.equals(""))
    {
      str7 = str1 + str3 + str8 + str6;

      localPreparedStatement = this.connection.prepareStatement(str7);

      localPreparedStatement.setString(1, str9);
      localPreparedStatement.setString(2, str10);
      localPreparedStatement.setString(3, str11);
    }
    else
    {
      str7 = str1 + str2 + str8 + str6;

      localPreparedStatement = this.connection.prepareStatement(str7);

      localPreparedStatement.setString(1, str9);
      localPreparedStatement.setString(2, str10);
      localPreparedStatement.setString(3, paramString1);
      localPreparedStatement.setString(4, str11);
    }

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public synchronized ResultSet getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
    throws SQLException
  {
    String str1 = "SELECT NULL AS table_cat,\n       o.owner AS table_schem,\n       o.object_name AS table_name,\n       o.object_type AS table_type,\n";

    String str2 = "       c.comments AS remarks\n";
    String str3 = "       NULL AS remarks\n";

    String str4 = "  FROM all_objects o, all_tab_comments c\n";
    String str5 = "  FROM all_objects o\n";

    String str6 = "  WHERE o.owner LIKE :1 ESCAPE '/'\n    AND o.object_name LIKE :2 ESCAPE '/'\n";

    String str7 = "    AND o.owner = c.owner (+)\n    AND o.object_name = c.table_name (+)\n";

    int i = 0;

    String str8 = "";
    String str9 = "";

    if (paramArrayOfString != null)
    {
      str8 = "    AND o.object_type IN ('xxx'";
      str9 = "    AND o.object_type IN ('xxx'";

      for (int j = 0; j < paramArrayOfString.length; j++)
      {
        if (paramArrayOfString[j].equals("SYNONYM"))
        {
          str8 = str8 + ", '" + paramArrayOfString[j] + "'";
          i = 1;
        }
        else
        {
          str8 = str8 + ", '" + paramArrayOfString[j] + "'";
          str9 = str9 + ", '" + paramArrayOfString[j] + "'";
        }
      }

      str8 = str8 + ")\n";
      str9 = str9 + ")\n";
    }
    else
    {
      i = 1;
      str8 = "    AND o.object_type IN ('TABLE', 'SYNONYM', 'VIEW')\n";
      str9 = "    AND o.object_type IN ('TABLE', 'VIEW')\n";
    }

    String str10 = "  ORDER BY table_type, table_schem, table_name\n";

    String str11 = "SELECT NULL AS table_cat,\n       s.owner AS table_schem,\n       s.synonym_name AS table_name,\n       'SYNONYM' AS table_table_type,\n";

    String str12 = "       c.comments AS remarks\n";
    String str13 = "       NULL AS remarks\n";

    String str14 = "  FROM all_synonyms s, all_objects o, all_tab_comments c\n";

    String str15 = "  FROM all_synonyms s, all_objects o\n";

    String str16 = "  WHERE s.owner LIKE :3 ESCAPE '/'\n    AND s.synonym_name LIKE :4 ESCAPE '/'\n    AND s.table_owner = o.owner\n    AND s.table_name = o.object_name\n    AND o.object_type IN ('TABLE', 'VIEW')\n";

    String str17 = "";

    str17 = str17 + str1;

    if (this.connection.getRemarksReporting())
      str17 = str17 + str2 + str4;
    else {
      str17 = str17 + str3 + str5;
    }
    str17 = str17 + str6;

    if (this.connection.getRestrictGetTables())
      str17 = str17 + str9;
    else {
      str17 = str17 + str8;
    }
    if (this.connection.getRemarksReporting()) {
      str17 = str17 + str7;
    }
    if ((i != 0) && (this.connection.getRestrictGetTables()))
    {
      str17 = str17 + "UNION\n" + str11;

      if (this.connection.getRemarksReporting())
        str17 = str17 + str12 + str14;
      else {
        str17 = str17 + str13 + str15;
      }
      str17 = str17 + str16;

      if (this.connection.getRemarksReporting()) {
        str17 = str17 + str7;
      }
    }
    str17 = str17 + str10;

    PreparedStatement localPreparedStatement = this.connection.prepareStatement(str17);

    localPreparedStatement.setString(1, paramString2 == null ? "%" : paramString2);
    localPreparedStatement.setString(2, paramString3 == null ? "%" : paramString3);

    if ((i != 0) && (this.connection.getRestrictGetTables()))
    {
      localPreparedStatement.setString(3, paramString2 == null ? "%" : paramString2);
      localPreparedStatement.setString(4, paramString3 == null ? "%" : paramString3);
    }

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public ResultSet getSchemas()
    throws SQLException
  {
    Statement localStatement = this.connection.createStatement();
    String str = "SELECT username AS table_schem FROM all_users ORDER BY table_schem";

    OracleResultSet localOracleResultSet = (OracleResultSet)localStatement.executeQuery(str);

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public ResultSet getCatalogs()
    throws SQLException
  {
    Statement localStatement = this.connection.createStatement();
    String str = "select 'nothing' as table_cat from dual where 1 = 2";
    OracleResultSet localOracleResultSet = (OracleResultSet)localStatement.executeQuery(str);

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public ResultSet getTableTypes()
    throws SQLException
  {
    Statement localStatement = this.connection.createStatement();
    String str = "select 'TABLE' as table_type from dual\nunion select 'VIEW' as table_type from dual\nunion select 'SYNONYM' as table_type from dual\n";

    OracleResultSet localOracleResultSet = (OracleResultSet)localStatement.executeQuery(str);

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public ResultSet getColumns(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public synchronized ResultSet getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT NULL AS table_cat,\n       table_schema AS table_schem,\n       table_name,\n       column_name,\n       grantor,\n       grantee,\n       privilege,\n       grantable AS is_grantable\nFROM all_col_privs\nWHERE table_schema LIKE :1 ESCAPE '/'\n  AND table_name LIKE :2 ESCAPE '/'\n  AND column_name LIKE :3 ESCAPE '/'\nORDER BY column_name, privilege\n");

    localPreparedStatement.setString(1, paramString2 == null ? "%" : paramString2);
    localPreparedStatement.setString(2, paramString3 == null ? "%" : paramString3);
    localPreparedStatement.setString(3, paramString4 == null ? "%" : paramString4);

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public synchronized ResultSet getTablePrivileges(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT NULL AS table_cat,\n       table_schema AS table_schem,\n       table_name,\n       grantor,\n       grantee,\n       privilege,\n       grantable AS is_grantable\nFROM all_tab_privs\nWHERE table_schema LIKE :1 ESCAPE '/'\n  AND table_name LIKE :2 ESCAPE '/'\nORDER BY table_schem, table_name, privilege\n");

    localPreparedStatement.setString(1, paramString2 == null ? "%" : paramString2);
    localPreparedStatement.setString(2, paramString3 == null ? "%" : paramString3);

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public synchronized ResultSet getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT 1 AS scope, 'ROWID' AS column_name, -8 AS data_type,\n 'ROWID' AS type_name, 0 AS column_size, 0 AS buffer_length,\n       0 AS decimal_digits, 2 AS pseudo_column\nFROM DUAL\nWHERE :1 = 1\nUNION\nSELECT 2 AS scope,\n  t.column_name,\n DECODE (t.data_type, 'CHAR', 1, 'VARCHAR2', 12, 'NUMBER', 3,\n 'LONG', -1, 'DATE', " + (this.connection.isV8Compatible() ? "93,\n" : "91,\n") + " 'RAW', -3, 'LONG RAW', -4, \n" + " 'TIMESTAMP(6)', 93, " + " 'TIMESTAMP(6) WITH TIME ZONE', -101, \n" + " 'TIMESTAMP(6) WITH LOCAL TIME ZONE', -102, \n" + " 'INTERVAL YEAR(2) TO MONTH', -103, \n" + " 'INTERVAL DAY(2) TO SECOND(6)', -104, \n" + " 'BINARY_FLOAT', 100, " + " 'BINARY_DOUBLE', 101," + " 1111)\n" + " AS data_type,\n" + " t.data_type AS type_name,\n" + " DECODE (t.data_precision, null, t.data_length, t.data_precision)\n" + "  AS column_size,\n" + "  0 AS buffer_length,\n" + "  t.data_scale AS decimal_digits,\n" + "       1 AS pseudo_column\n" + "FROM all_tab_columns t, all_ind_columns i\n" + "WHERE :2 = 1\n" + "  AND t.table_name = :3\n" + "  AND t.owner like :4 escape '/'\n" + "  AND t.nullable != :5\n" + "  AND t.owner = i.table_owner\n" + "  AND t.table_name = i.table_name\n" + "  AND t.column_name = i.column_name\n");

    switch (paramInt)
    {
    case 0:
      localPreparedStatement.setInt(1, 0);
      localPreparedStatement.setInt(2, 0);

      break;
    case 1:
      localPreparedStatement.setInt(1, 1);
      localPreparedStatement.setInt(2, 1);

      break;
    case 2:
      localPreparedStatement.setInt(1, 0);
      localPreparedStatement.setInt(2, 1);
    }

    localPreparedStatement.setString(3, paramString3);
    localPreparedStatement.setString(4, paramString2 == null ? "%" : paramString2);
    localPreparedStatement.setString(5, paramBoolean ? "X" : "Y");

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public synchronized ResultSet getVersionColumns(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT 0 AS scope,\n t.column_name,\n DECODE (c.data_type, 'CHAR', 1, 'VARCHAR2', 12, 'NUMBER', 3,\n  'LONG', -1, 'DATE',  " + (this.connection.isV8Compatible() ? "93,\n" : "91,\n") + "  'RAW', -3, 'LONG RAW', -4, " + "  'TIMESTAMP(6)', 93, 'TIMESTAMP(6) WITH TIME ZONE', -101, \n" + "  'TIMESTAMP(6) WITH LOCAL TIME ZONE', -102, \n" + "  'INTERVAL YEAR(2) TO MONTH', -103, \n" + "  'INTERVAL DAY(2) TO SECOND(6)', -104, \n" + "  'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101," + "   1111)\n " + " AS data_type,\n" + "       c.data_type AS type_name,\n" + " DECODE (c.data_precision, null, c.data_length, c.data_precision)\n" + "   AS column_size,\n" + "       0 as buffer_length,\n" + "   c.data_scale as decimal_digits,\n" + "   0 as pseudo_column\n" + "FROM all_trigger_cols t, all_tab_columns c\n" + "WHERE t.table_name = :1\n" + "  AND c.owner like :2 escape '/'\n" + " AND t.table_owner = c.owner\n" + "  AND t.table_name = c.table_name\n" + " AND t.column_name = c.column_name\n");

    localPreparedStatement.setString(1, paramString3);
    localPreparedStatement.setString(2, paramString2 == null ? "%" : paramString2);

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public ResultSet getPrimaryKeys(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT NULL AS table_cat,\n       c.owner AS table_schem,\n       c.table_name,\n       c.column_name,\n       c.position AS key_seq,\n       c.constraint_name AS pk_name\nFROM all_cons_columns c, all_constraints k\nWHERE k.constraint_type = 'P'\n  AND k.table_name = :1\n  AND k.owner like :2 escape '/'\n  AND k.constraint_name = c.constraint_name \n  AND k.table_name = c.table_name \n  AND k.owner = c.owner \nORDER BY column_name\n");

    localPreparedStatement.setString(1, paramString3);
    localPreparedStatement.setString(2, paramString2 == null ? "%" : paramString2);

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  ResultSet keys_query(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5)
    throws SQLException
  {
    int i = 1;
    int j = paramString2 != null ? i++ : 0;
    int k = paramString4 != null ? i++ : 0;
    int m = (paramString1 != null) && (paramString1.length() > 0) ? i++ : 0;
    int n = (paramString3 != null) && (paramString3.length() > 0) ? i++ : 0;

    PreparedStatement localPreparedStatement = this.connection.prepareStatement("SELECT NULL AS pktable_cat,\n       p.owner as pktable_schem,\n       p.table_name as pktable_name,\n       pc.column_name as pkcolumn_name,\n       NULL as fktable_cat,\n       f.owner as fktable_schem,\n       f.table_name as fktable_name,\n       fc.column_name as fkcolumn_name,\n       fc.position as key_seq,\n       NULL as update_rule,\n       decode (f.delete_rule, 'CASCADE', 0, 'SET NULL', 2, 1) as delete_rule,\n       f.constraint_name as fk_name,\n       p.constraint_name as pk_name,\n       decode(f.deferrable,       'DEFERRABLE',5      ,'NOT DEFERRABLE',7      , 'DEFERRED', 6      ) deferrability \n      FROM all_cons_columns pc, all_constraints p,\n      all_cons_columns fc, all_constraints f\nWHERE 1 = 1\n" + (j != 0 ? "  AND p.table_name = :1\n" : "") + (k != 0 ? "  AND f.table_name = :2\n" : "") + (m != 0 ? "  AND p.owner = :3\n" : "") + (n != 0 ? "  AND f.owner = :4\n" : "") + "  AND f.constraint_type = 'R'\n" + "  AND p.owner = f.r_owner\n" + "  AND p.constraint_name = f.r_constraint_name\n" + "  AND p.constraint_type = 'P'\n" + "  AND pc.owner = p.owner\n" + "  AND pc.constraint_name = p.constraint_name\n" + "  AND pc.table_name = p.table_name\n" + "  AND fc.owner = f.owner\n" + "  AND fc.constraint_name = f.constraint_name\n" + "  AND fc.table_name = f.table_name\n" + "  AND fc.position = pc.position\n" + paramString5);

    if (j != 0)
    {
      localPreparedStatement.setString(j, paramString2);
    }

    if (k != 0)
    {
      localPreparedStatement.setString(k, paramString4);
    }

    if (m != 0)
    {
      localPreparedStatement.setString(m, paramString1);
    }

    if (n != 0)
    {
      localPreparedStatement.setString(n, paramString3);
    }

    OracleResultSet localOracleResultSet = (OracleResultSet)localPreparedStatement.executeQuery();

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  public synchronized ResultSet getImportedKeys(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    return keys_query(null, null, paramString2, paramString3, "ORDER BY pktable_schem, pktable_name, key_seq");
  }

  public ResultSet getExportedKeys(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    return keys_query(paramString2, paramString3, null, null, "ORDER BY fktable_schem, fktable_name, key_seq");
  }

  public ResultSet getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
    throws SQLException
  {
    return keys_query(paramString2, paramString3, paramString5, paramString6, "ORDER BY fktable_schem, fktable_name, key_seq");
  }

  public ResultSet getTypeInfo()
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public synchronized ResultSet getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException
  {
    Statement localStatement = this.connection.createStatement();

    if (((paramString2 != null) && (paramString2.length() != 0) && (!OracleSql.isValidObjectName(paramString2))) || ((paramString3 != null) && (paramString3.length() != 0) && (!OracleSql.isValidObjectName(paramString3))))
    {
      DatabaseError.throwSqlException(68);
    }

    String str1 = "analyze table " + (paramString2 == null ? "" : new StringBuffer().append(paramString2).append(".").toString()) + paramString3 + (paramBoolean2 ? " estimate statistics" : " compute statistics");

    localStatement.executeUpdate(str1);

    String str2 = "select null as table_cat,\n       owner as table_schem,\n       table_name,\n       0 as NON_UNIQUE,\n       null as index_qualifier,\n       null as index_name, 0 as type,\n       0 as ordinal_position, null as column_name,\n       null as asc_or_desc,\n       num_rows as cardinality,\n       blocks as pages,\n       null as filter_condition\nfrom all_tables\nwhere table_name = '" + paramString3 + "'\n";

    String str3 = "";

    if ((paramString2 != null) && (paramString2.length() > 0)) {
      str3 = "  and owner = '" + paramString2 + "'\n";
    }

    String str4 = "select null as table_cat,\n       i.owner as table_schem,\n       i.table_name,\n       decode (i.uniqueness, 'UNIQUE', 0, 1),\n       null as index_qualifier,\n       i.index_name,\n       1 as type,\n       c.column_position as ordinal_position,\n       c.column_name,\n       null as asc_or_desc,\n       i.distinct_keys as cardinality,\n       i.leaf_blocks as pages,\n       null as filter_condition\nfrom all_indexes i, all_ind_columns c\nwhere i.table_name = '" + paramString3 + "'\n";

    String str5 = "";

    if ((paramString2 != null) && (paramString2.length() > 0)) {
      str5 = "  and i.owner = '" + paramString2 + "'\n";
    }
    String str6 = "";

    if (paramBoolean1) {
      str6 = "  and i.uniqueness = 'UNIQUE'\n";
    }
    String str7 = "  and i.index_name = c.index_name\n  and i.table_owner = c.table_owner\n  and i.table_name = c.table_name\n  and i.owner = c.index_owner\n";

    String str8 = "order by non_unique, type, index_name, ordinal_position\n";

    String str9 = str2 + str3 + "union\n" + str4 + str5 + str6 + str7 + str8;

    OracleResultSet localOracleResultSet = (OracleResultSet)localStatement.executeQuery(str9);

    localOracleResultSet.closeStatementOnClose();

    return localOracleResultSet;
  }

  SQLException fail()
  {
    SQLException localSQLException = new SQLException("Not implemented yet");

    return localSQLException;
  }

  public boolean supportsResultSetType(int paramInt)
    throws SQLException
  {
    return true;
  }

  public boolean supportsResultSetConcurrency(int paramInt1, int paramInt2)
    throws SQLException
  {
    return true;
  }

  public boolean ownUpdatesAreVisible(int paramInt)
    throws SQLException
  {
    return paramInt != 1003;
  }

  public boolean ownDeletesAreVisible(int paramInt)
    throws SQLException
  {
    return paramInt != 1003;
  }

  public boolean ownInsertsAreVisible(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean othersUpdatesAreVisible(int paramInt)
    throws SQLException
  {
    return paramInt == 1005;
  }

  public boolean othersDeletesAreVisible(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean othersInsertsAreVisible(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean updatesAreDetected(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean deletesAreDetected(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean insertsAreDetected(int paramInt)
    throws SQLException
  {
    return false;
  }

  public boolean supportsBatchUpdates()
    throws SQLException
  {
    return true;
  }

  public ResultSet getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfInt)
    throws SQLException
  {
    int i = 0;

    if ((paramString3 == null) || (paramString3.length() == 0))
    {
      i = 0;
    }
    else if (paramArrayOfInt == null)
    {
      i = 1;
    }
    else
    {
      for (int j = 0; j < paramArrayOfInt.length; j++)
      {
        if (paramArrayOfInt[j] != 2002)
          continue;
        i = 1;

        break;
      }

    }

    StringBuffer localStringBuffer = new StringBuffer();

    localStringBuffer.append("SELECT NULL AS TYPE_CAT, owner AS TYPE_SCHEM, type_name, NULL AS CLASS_NAME, 'STRUCT' AS DATA_TYPE, NULL AS REMARKS FROM all_types ");

    if (i != 0)
    {
      localStringBuffer.append("WHERE typecode = 'OBJECT' AND owner LIKE :1 ESCAPE '/' AND type_name LIKE :2 ESCAPE '/'");
    }
    else
    {
      localStringBuffer.append("WHERE 1 = 2");
    }

    PreparedStatement localPreparedStatement = this.connection.prepareStatement(localStringBuffer.substring(0, localStringBuffer.length()));

    if (i != 0)
    {
      localObject = new String[1];
      String[] arrayOfString = new String[1];

      if (SQLName.parse(paramString3, localObject, arrayOfString))
      {
        localPreparedStatement.setString(1, localObject[0]);
        localPreparedStatement.setString(2, arrayOfString[0]);
      }
      else
      {
        if (paramString2 != null)
          localPreparedStatement.setString(1, paramString2);
        else {
          localPreparedStatement.setNull(1, 12);
        }
        localPreparedStatement.setString(2, paramString3);
      }

    }

    Object localObject = (OracleResultSet)localPreparedStatement.executeQuery();

    ((OracleResultSet)localObject).closeStatementOnClose();

    return (ResultSet)localObject;
  }

  public Connection getConnection()
    throws SQLException
  {
    return this.connection.getWrapper();
  }

  public boolean supportsSavepoints()
    throws SQLException
  {
    return true;
  }

  public boolean supportsNamedParameters()
    throws SQLException
  {
    return true;
  }

  public boolean supportsMultipleOpenResults()
    throws SQLException
  {
    return false;
  }

  public boolean supportsGetGeneratedKeys()
    throws SQLException
  {
    return true;
  }

  public ResultSet getSuperTypes(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public ResultSet getSuperTables(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public ResultSet getAttributes(String paramString1, String paramString2, String paramString3, String paramString4)
    throws SQLException
  {
    DatabaseError.throwUnsupportedFeatureSqlException();

    return null;
  }

  public boolean supportsResultSetHoldability(int paramInt)
    throws SQLException
  {
    return paramInt == 1;
  }

  public int getResultSetHoldability()
    throws SQLException
  {
    return 1;
  }

  public int getDatabaseMajorVersion()
    throws SQLException
  {
    return this.connection.getVersionNumber() / 1000;
  }

  public int getDatabaseMinorVersion()
    throws SQLException
  {
    return this.connection.getVersionNumber() % 1000 / 100;
  }

  public int getJDBCMajorVersion()
    throws SQLException
  {
    return DRIVER_MAJOR_VERSION;
  }

  public int getJDBCMinorVersion()
    throws SQLException
  {
    return DRIVER_MINOR_VERSION;
  }

  public int getSQLStateType()
    throws SQLException
  {
    return 0;
  }

  public boolean locatorsUpdateCopy()
    throws SQLException
  {
    return true;
  }

  public boolean supportsStatementPooling()
    throws SQLException
  {
    return true;
  }

  public static String getDriverNameInfo()
    throws SQLException
  {
    return DRIVER_NAME;
  }

  public static String getDriverVersionInfo()
    throws SQLException
  {
    return DRIVER_VERSION;
  }

  public static int getDriverMajorVersionInfo()
  {
    return DRIVER_MAJOR_VERSION;
  }

  public static int getDriverMinorVersionInfo()
  {
    return DRIVER_MINOR_VERSION;
  }

  public static void setGetLobPrecision(boolean paramBoolean)
    throws SQLException
  {
  }

  public static boolean getGetLobPrecision()
    throws SQLException
  {
    return false;
  }

  public static String getLobPrecision() throws SQLException
  {
    return getGetLobPrecision() ? LOB_MAXSIZE : "-1";
  }

  public long getLobMaxLength()
    throws SQLException
  {
    return this.connection.getVersionNumber() >= 10000 ? 9223372036854775807L : LOB_MAXLENGTH_32BIT;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.OracleDatabaseMetaData
 * JD-Core Version:    0.6.0
 */