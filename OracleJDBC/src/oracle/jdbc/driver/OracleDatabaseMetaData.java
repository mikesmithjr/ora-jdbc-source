package oracle.jdbc.driver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.internal.OracleResultSet;

public class OracleDatabaseMetaData extends oracle.jdbc.OracleDatabaseMetaData {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    public OracleDatabaseMetaData(oracle.jdbc.OracleConnection conn) {
        super(conn);
    }

    public OracleDatabaseMetaData(oracle.jdbc.driver.OracleConnection conn) {
        this(((oracle.jdbc.OracleConnection) (conn)));
    }

    public String getTimeDateFunctions() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getTimeDateFunctions", this);

            OracleLog.recursiveTrace = false;
        }

        if (((PhysicalConnection) this.connection).v8Compatible) {
            return "MONTH,YEAR";
        }
        return "HOUR,MINUTE,SECOND,MONTH,YEAR";
    }

    public synchronized ResultSet getColumns(String catalog, String schemaPattern,
            String tableNamePattern, String columnNamePattern) throws SQLException {
        String queryPart1 = "SELECT NULL AS table_cat,\n";

        String tableName = "       t.owner AS table_schem,\n       t.table_name AS table_name,\n";

        String synonymName = "       DECODE(s.table_owner, NULL, t.owner, s.table_owner)\n              AS table_schem,\n       DECODE(s.synonym_name, NULL, t.table_name, s.synonym_name)\n              AS table_name,\n";

        String queryPart2 = "       t.column_name AS column_name,\n       DECODE (t.data_type, 'CHAR', 1, 'VARCHAR2', 12, 'NUMBER', 3,\n               'LONG', -1, 'DATE', "
                + (((PhysicalConnection) this.connection).v8Compatible ? "93" : "91")
                + ", 'RAW', -3, 'LONG RAW', -4,  \n"
                + "               'BLOB', 2004, 'CLOB', 2005, 'BFILE', -13, 'FLOAT', 6, \n"
                + "               'TIMESTAMP(6)', 93, 'TIMESTAMP(6) WITH TIME ZONE', -101, \n"
                + "               'TIMESTAMP(6) WITH LOCAL TIME ZONE', -102, \n"
                + "               'INTERVAL YEAR(2) TO MONTH', -103, \n"
                + "               'INTERVAL DAY(2) TO SECOND(6)', -104, \n"
                + "               'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101, \n"
                + "               1111)\n"
                + "              AS data_type,\n"
                + "       t.data_type AS type_name,\n"
                + "       DECODE (t.data_precision, null, t.data_length, t.data_precision)\n"
                + "              AS column_size,\n"
                + "       0 AS buffer_length,\n"
                + "       t.data_scale AS decimal_digits,\n"
                + "       10 AS num_prec_radix,\n"
                + "       DECODE (t.nullable, 'N', 0, 1) AS nullable,\n";

        String remarks = "       c.comments AS remarks,\n";

        String noRemarks = "       NULL AS remarks,\n";

        String queryPart3 = "       t.data_default AS column_def,\n       0 AS sql_data_type,\n       0 AS sql_datetime_sub,\n       t.data_length AS char_octet_length,\n       t.column_id AS ordinal_position,\n       DECODE (t.nullable, 'N', 'NO', 'YES') AS is_nullable\n";

        String fromClause = "FROM all_tab_columns t";

        String synonymFrom = ", all_synonyms s";
        String remarksFrom = ", all_col_comments c";

        String whereClause = "WHERE t.owner LIKE :1 ESCAPE '/'\n  AND t.table_name LIKE :2 ESCAPE '/'\n  AND t.column_name LIKE :3 ESCAPE '/'\n";

        String synonymWhereClause = "WHERE (t.owner LIKE :4 ESCAPE '/' OR\n       (s.owner LIKE :5 ESCAPE '/' AND t.owner = s.table_owner))\n  AND (t.table_name LIKE :6 ESCAPE '/' OR\n       s.synonym_name LIKE :7 ESCAPE '/')\n  AND t.column_name LIKE :8 ESCAPE '/'\n";

        String remarksWhere = "  AND t.owner = c.owner (+)\n  AND t.table_name = c.table_name (+)\n  AND t.column_name = c.column_name (+)\n";

        String synonymWhere = "  AND s.table_name (+) = t.table_name\n  AND ((DECODE(s.owner, t.owner, 'OK',\n                       'PUBLIC', 'OK',\n                       NULL, 'OK',\n                       'NOT OK') = 'OK') OR\n       (s.owner LIKE :9 AND t.owner = s.table_owner))";

        String orderBy = "ORDER BY table_schem, table_name, ordinal_position\n";

        String finalQuery = queryPart1;

        if (this.connection.getIncludeSynonyms())
            finalQuery = finalQuery + synonymName;
        else {
            finalQuery = finalQuery + tableName;
        }
        finalQuery = finalQuery + queryPart2;

        if (this.connection.getRemarksReporting())
            finalQuery = finalQuery + remarks;
        else {
            finalQuery = finalQuery + noRemarks;
        }
        finalQuery = finalQuery + queryPart3 + fromClause;

        if (this.connection.getRemarksReporting()) {
            finalQuery = finalQuery + remarksFrom;
        }
        if (this.connection.getIncludeSynonyms()) {
            finalQuery = finalQuery + synonymFrom;
        }
        if (this.connection.getIncludeSynonyms())
            finalQuery = finalQuery + "\n" + synonymWhereClause;
        else {
            finalQuery = finalQuery + "\n" + whereClause;
        }
        if (this.connection.getRemarksReporting()) {
            finalQuery = finalQuery + remarksWhere;
        }
        if (this.connection.getIncludeSynonyms()) {
            finalQuery = finalQuery + synonymWhere;
        }
        finalQuery = finalQuery + "\n" + orderBy;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getColumns final SQL statement is:\n"
                    + finalQuery, this);

            OracleLog.recursiveTrace = false;
        }

        PreparedStatement s = this.connection.prepareStatement(finalQuery);

        if (this.connection.getIncludeSynonyms()) {
            s.setString(1, schemaPattern == null ? "%" : schemaPattern);
            s.setString(2, schemaPattern == null ? "%" : schemaPattern);
            s.setString(3, tableNamePattern == null ? "%" : tableNamePattern);
            s.setString(4, tableNamePattern == null ? "%" : tableNamePattern);
            s.setString(5, columnNamePattern == null ? "%" : columnNamePattern);
            s.setString(6, schemaPattern == null ? "%" : schemaPattern);
        } else {
            s.setString(1, schemaPattern == null ? "%" : schemaPattern);
            s.setString(2, tableNamePattern == null ? "%" : tableNamePattern);
            s.setString(3, columnNamePattern == null ? "%" : columnNamePattern);
        }

        OracleResultSet rs = (OracleResultSet) s.executeQuery();

        rs.closeStatementOnClose();

        return rs;
    }

    public ResultSet getTypeInfo() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "getTypeInfo", this);

            OracleLog.recursiveTrace = false;
        }

        Statement s = this.connection.createStatement();
        short db_version = this.connection.getVersionNumber();

        String number_query = "select\n 'NUMBER' as type_name, 2 as data_type, 38 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n 'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String bit_query = "union select 'NUMBER' as type_name, -7 as data_type, 1 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(1)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String tinyint_query = "union select 'NUMBER' as type_name, -6 as data_type, 3 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(3)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String smallint_query = "union select 'NUMBER' as type_name, 5 as data_type, 5 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(5)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String integer_query = "union select 'NUMBER' as type_name, 4 as data_type, 10 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(10)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String bigint_query = "union select 'NUMBER' as type_name, -5 as data_type, 38 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \nNULL as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String float_query = "union select 'FLOAT' as type_name, 6 as data_type, 63 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \nNULL as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'FLOAT' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String real_query = "union select 'REAL' as type_name, 7 as data_type, 63 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \nNULL as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'REAL' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String char_query = "union select\n 'CHAR' as type_name, 1 as data_type, "
                + (db_version >= 8100 ? 2000 : 255) + " as precision,\n"
                + " '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n"
                + " 1 as nullable, 1 as case_sensitive, 3 as searchable,\n"
                + " 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n"
                + " 'CHAR' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n"
                + " NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\n"
                + "from dual\n";

        String varchar2_query = "union select\n 'VARCHAR2' as type_name, 12 as data_type, "
                + (db_version >= 8100 ? 4000 : 2000) + " as precision,\n"
                + " '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n"
                + " 1 as nullable, 1 as case_sensitive, 3 as searchable,\n"
                + " 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n"
                + " 'VARCHAR2' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n"
                + " NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\n"
                + "from dual\n";

        String long_query = "union select\n 'LONG' as type_name, -1 as data_type, 2147483647 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'LONG' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String date_query = "union select\n 'DATE' as type_name, "
                + (((PhysicalConnection) this.connection).v8Compatible ? "93" : "91")
                + "as data_type, 7 as precision,\n"
                + " NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n"
                + " 1 as nullable, 0 as case_sensitive, 3 as searchable,\n"
                + " 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n"
                + " 'DATE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n"
                + " NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\n"
                + "from dual\n";

        String time_query = "union select\n 'DATE' as type_name, 92 as data_type, 7 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'DATE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String intervalym_query = "union select\n 'INTERVALYM' as type_name, -103 as data_type, 5 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'INTERVALYM' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String intervalds_query = "union select\n 'INTERVALDS' as type_name, -104 as data_type, 4 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'INTERVALDS' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String timestamp_query = "union select\n 'TIMESTAMP' as type_name, 93 as data_type, 11 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'TIMESTAMP' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String timestamptz_query = "union select\n 'TIMESTAMP WITH TIME ZONE' as type_name, -101 as data_type, 13 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'TIMESTAMP WITH TIME ZONE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String timestampltz_query = "union select\n 'TIMESTAMP WITH LOCAL TIME ZONE' as type_name, -102 as data_type, 11 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'TIMESTAMP WITH LOCAL TIME ZONE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String raw_query = "union select\n 'RAW' as type_name, -3 as data_type, "
                + (db_version >= 8100 ? 2000 : 255) + " as precision,\n"
                + " '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n"
                + " 1 as nullable, 0 as case_sensitive, 3 as searchable,\n"
                + " 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n"
                + " 'RAW' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n"
                + " NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\n"
                + "from dual\n";

        String long_raw_query = "union select\n 'LONG RAW' as type_name, -4 as data_type, 2147483647 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'LONG RAW' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String _lobSz = "-1";

        String blob_query = "union select\n 'BLOB' as type_name, 2004 as data_type, " + _lobSz
                + " as precision,\n"
                + " null as literal_prefix, null as literal_suffix, NULL as create_params,\n"
                + " 1 as nullable, 0 as case_sensitive, 0 as searchable,\n"
                + " 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n"
                + " 'BLOB' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n"
                + " NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\n"
                + "from dual\n";

        String clob_query = "union select\n 'CLOB' as type_name, 2005 as data_type, " + _lobSz
                + " as precision,\n"
                + " '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n"
                + " 1 as nullable, 1 as case_sensitive, 0 as searchable,\n"
                + " 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n"
                + " 'CLOB' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n"
                + " NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\n"
                + "from dual\n";

        String array_query = "union select\n 'ARRAY' as type_name, 2003 as data_type, 0 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'ARRAY' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String ref_query = "union select\n 'REF' as type_name, 2006 as data_type, 0 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'REF' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String struct_query = "union select\n 'STRUCT' as type_name, 2002 as data_type, 0 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'STRUCT' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n";

        String order_by = "order by data_type\n";

        String query = "select\n 'NUMBER' as type_name, 2 as data_type, 38 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n 'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + char_query
                + varchar2_query
                + date_query
                + "union select\n 'DATE' as type_name, 92 as data_type, 7 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'DATE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select\n 'TIMESTAMP' as type_name, 93 as data_type, 11 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'TIMESTAMP' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select\n 'TIMESTAMP WITH TIME ZONE' as type_name, -101 as data_type, 13 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'TIMESTAMP WITH TIME ZONE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select\n 'TIMESTAMP WITH LOCAL TIME ZONE' as type_name, -102 as data_type, 11 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'TIMESTAMP WITH LOCAL TIME ZONE' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select\n 'INTERVALYM' as type_name, -103 as data_type, 5 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'INTERVALYM' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select\n 'INTERVALDS' as type_name, -104 as data_type, 4 as precision,\n NULL as literal_prefix, NULL as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 3 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'INTERVALDS' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + raw_query
                + "union select\n 'LONG' as type_name, -1 as data_type, 2147483647 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'LONG' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select\n 'LONG RAW' as type_name, -4 as data_type, 2147483647 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 0 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'LONG RAW' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'NUMBER' as type_name, -7 as data_type, 1 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(1)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'NUMBER' as type_name, -6 as data_type, 3 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(3)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'NUMBER' as type_name, 5 as data_type, 5 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(5)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'NUMBER' as type_name, 4 as data_type, 10 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \n'(10)' as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'NUMBER' as type_name, -5 as data_type, 38 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \nNULL as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'NUMBER' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'FLOAT' as type_name, 6 as data_type, 63 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \nNULL as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'FLOAT' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + "union select 'REAL' as type_name, 7 as data_type, 63 as precision,\nNULL as literal_prefix, NULL as literal_suffix, \nNULL as create_params, 1 as nullable, 0 as case_sensitive, 3 as searchable,\n0 as unsigned_attribute, 1 as fixed_prec_scale, 0 as auto_increment,\n'REAL' as local_type_name, -84 as minimum_scale, 127 as maximum_scale,\nNULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                + (db_version >= 8100 ? blob_query
                        + clob_query
                        + "union select\n 'REF' as type_name, 2006 as data_type, 0 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'REF' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                        + "union select\n 'ARRAY' as type_name, 2003 as data_type, 0 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'ARRAY' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                        + "union select\n 'STRUCT' as type_name, 2002 as data_type, 0 as precision,\n '''' as literal_prefix, '''' as literal_suffix, NULL as create_params,\n 1 as nullable, 1 as case_sensitive, 0 as searchable,\n 0 as unsigned_attribute, 0 as fixed_prec_scale, 0 as auto_increment,\n 'STRUCT' as local_type_name, 0 as minimum_scale, 0 as maximum_scale,\n NULL as sql_data_type, NULL as sql_datetime_sub, 10 as num_prec_radix\nfrom dual\n"
                        : "") + "order by data_type\n";

        OracleResultSet rs = (OracleResultSet) s.executeQuery(query);

        rs.closeStatementOnClose();

        return rs;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleDatabaseMetaData"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleDatabaseMetaData JD-Core Version: 0.6.0
 */