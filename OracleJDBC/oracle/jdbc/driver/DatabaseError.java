package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import oracle.jdbc.util.SQLStateMapping;
import oracle.jdbc.util.SQLStateRange;

public class DatabaseError
{
  private static boolean loadedMessages = false;
  private static Message message = null;
  private static String msgClassName = "oracle.jdbc.driver.Message11";

  static final SQLStateMapping[] mappings = { new SQLStateMapping(0, "00000"), new SQLStateMapping(1, "23000"), new SQLStateMapping(22, "42000"), new SQLStateMapping(100, "02000"), new SQLStateMapping(251, "42000"), new SQLStateMapping(1025, "22023"), new SQLStateMapping(1031, "42000"), new SQLStateMapping(1095, "02000"), new SQLStateMapping(1402, "44000"), new SQLStateMapping(1403, "02000"), new SQLStateMapping(1405, "22002"), new SQLStateMapping(1406, "22001"), new SQLStateMapping(1410, "24000"), new SQLStateMapping(1411, "22022"), new SQLStateMapping(1422, "21000"), new SQLStateMapping(1424, "22025"), new SQLStateMapping(1425, "22019"), new SQLStateMapping(1426, "22003"), new SQLStateMapping(1427, "21000"), new SQLStateMapping(1438, "22003"), new SQLStateMapping(1455, "22003"), new SQLStateMapping(1457, "22003"), new SQLStateMapping(1476, "22012"), new SQLStateMapping(1488, "22023"), new SQLStateMapping(8006, "24000") };

  static final SQLStateRange[] ranges = { new SQLStateRange(17, 21, "61000"), new SQLStateRange(23, 35, "61000"), new SQLStateRange(49, 68, "61000"), new SQLStateRange(100, 120, "62000"), new SQLStateRange(149, 159, "63000"), new SQLStateRange(199, 369, "64000"), new SQLStateRange(369, 429, "60000"), new SQLStateRange(429, 439, "67000"), new SQLStateRange(439, 569, "62000"), new SQLStateRange(569, 599, "69000"), new SQLStateRange(599, 899, "60000"), new SQLStateRange(899, 999, "42000"), new SQLStateRange(999, 1099, "72000"), new SQLStateRange(1000, 1003, "24000"), new SQLStateRange(1099, 1250, "64000"), new SQLStateRange(1399, 1401, "23000"), new SQLStateRange(1401, 1478, "72000"), new SQLStateRange(1478, 1480, "22024"), new SQLStateRange(1480, 1489, "72000"), new SQLStateRange(1489, 1493, "42000"), new SQLStateRange(1493, 1499, "72000"), new SQLStateRange(1499, 1699, "72000"), new SQLStateRange(1699, 1799, "42000"), new SQLStateRange(1799, 1899, "22008"), new SQLStateRange(1899, 2099, "42000"), new SQLStateRange(2090, 2092, "40000"), new SQLStateRange(2139, 2289, "42000"), new SQLStateRange(2289, 2299, "23000"), new SQLStateRange(2375, 2399, "61000"), new SQLStateRange(2399, 2419, "72000"), new SQLStateRange(2419, 2424, "42000"), new SQLStateRange(2424, 2449, "72000"), new SQLStateRange(2449, 2499, "42000"), new SQLStateRange(2699, 2899, "63000"), new SQLStateRange(2999, 3099, "0A000"), new SQLStateRange(3099, 3199, "63000"), new SQLStateRange(3275, 3299, "42000"), new SQLStateRange(3999, 4019, "22023"), new SQLStateRange(4019, 4039, "61000"), new SQLStateRange(4039, 4059, "42000"), new SQLStateRange(4059, 4069, "72000"), new SQLStateRange(4069, 4099, "42000"), new SQLStateRange(5999, 6149, "66000"), new SQLStateRange(6199, 6249, "63000"), new SQLStateRange(6249, 6429, "66000"), new SQLStateRange(6429, 6449, "60000"), new SQLStateRange(6499, 6599, "65000"), new SQLStateRange(6510, 6511, "24000"), new SQLStateRange(6599, 6999, "66000"), new SQLStateRange(6999, 7199, "69000"), new SQLStateRange(7199, 7999, "60000"), new SQLStateRange(7999, 8190, "72000"), new SQLStateRange(9699, 9999, "60000"), new SQLStateRange(9999, 10999, "90000"), new SQLStateRange(11999, 12019, "72000"), new SQLStateRange(12299, 12499, "72000"), new SQLStateRange(12699, 21999, "72000"), new SQLStateRange(12099, 12299, "66000"), new SQLStateRange(12499, 12599, "66000") };
  public static final int JDBC_ERROR_BASE = 17000;
  public static final int JDBC_MAX_ERRORS = 500;
  public static final int EOJ_SUCCESS = 0;
  public static final int EOJ_ERROR = 1;
  public static final int EOJ_IOEXCEPTION = 2;
  public static final int EOJ_INVALID_COLUMN_INDEX = 3;
  public static final int EOJ_INVALID_COLUMN_TYPE = 4;
  public static final int EOJ_UNSUPPORTED_COLUMN_TYPE = 5;
  public static final int EOJ_INVALID_COLUMN_NAME = 6;
  public static final int EOJ_INVALID_DYNAMIC_COLUMN = 7;
  public static final int EOJ_CLOSED_CONNECTION = 8;
  public static final int EOJ_CLOSED_STATEMENT = 9;
  public static final int EOJ_CLOSED_RESULTSET = 10;
  public static final int EOJ_EXHAUSTED_RESULTSET = 11;
  public static final int EOJ_TYPE_CONFLICT = 12;
  public static final int EOJ_WAS_NULL = 13;
  public static final int EOJ_RESULTSET_BEFORE_FIRST_ROW = 14;
  public static final int EOJ_STATEMENT_WAS_CANCELLED = 15;
  public static final int EOJ_STATEMENT_TIMED_OUT = 16;
  public static final int EOJ_CURSOR_ALREADY_INITIALIZED = 17;
  public static final int EOJ_INVALID_CURSOR = 18;
  public static final int EOJ_CAN_ONLY_DESCRIBE_A_QUERY = 19;
  public static final int EOJ_INVALID_ROW_PREFETCH = 20;
  public static final int EOJ_MISSING_DEFINES = 21;
  public static final int EOJ_MISSING_DEFINES_AT_INDEX = 22;
  public static final int EOJ_UNSUPPORTED_FEATURE = 23;
  public static final int EOJ_NO_DATA_READ = 24;
  public static final int EOJ_IS_DEFINES_NULL_ERROR = 25;
  public static final int EOJ_NUMERIC_OVERFLOW = 26;
  public static final int EOJ_STREAM_CLOSED = 27;
  public static final int EOJ_NO_NEW_DEFINE_IF_RESULT_SET_NOT_CLOSED = 28;
  public static final int EOJ_READ_ONLY = 29;
  public static final int EOJ_INVALID_TRANSLEVEL = 30;
  public static final int EOJ_AUTO_CLOSE_ONLY = 31;
  public static final int EOJ_ROW_PREFETCH_NOT_ZERO = 32;
  public static final int EOJ_MALFORMED_SQL92 = 33;
  public static final int EOJ_NON_SUPPORTED_SQL92_TOKEN = 34;
  public static final int EOJ_NON_SUPPORTED_CHAR_SET = 35;
  public static final int EOJ_ORACLE_NUMBER_EXCEPTION = 36;
  public static final int EOJ_FAIL_CONVERSION_UTF8_TO_UCS2 = 37;
  public static final int EOJ_CONVERSION_BYTE_ARRAY_ERROR = 38;
  public static final int EOJ_CONVERSION_CHAR_ARRAY_ERROR = 39;
  public static final int EOJ_SUB_SUB_PROTOCOL_ERROR = 40;
  public static final int EOJ_INVALID_IN_OUT_BINDS = 41;
  public static final int EOJ_INVALID_BATCH_VALUE = 42;
  public static final int EOJ_INVALID_STREAM_SIZE = 43;
  public static final int EOJ_DATASET_ITEMS_NOT_ALLOCATED = 44;
  public static final int EOJ_BEYOND_BINDS_BATCH = 45;
  public static final int EOJ_INVALID_RANK = 46;
  public static final int EOJ_TDS_FORMAT_ERROR = 47;
  public static final int EOJ_UNDEFINED_TYPE = 48;
  public static final int EOJ_INCONSISTENT_ADT = 49;
  public static final int EOJ_NOSUCHELEMENT = 50;
  public static final int EOJ_NOT_AN_OBJECT_TYPE = 51;
  public static final int EOJ_INVALID_REF = 52;
  public static final int EOJ_INVALID_SIZE = 53;
  public static final int EOJ_INVALID_LOB_LOCATOR = 54;
  public static final int EOJ_FAIL_CONVERSION_CHARACTER = 55;
  public static final int EOJ_UNSUPPORTED_CHARSET = 56;
  public static final int EOJ_CLOSED_LOB = 57;
  public static final int EOJ_INVALID_NLS_RATIO = 58;
  public static final int EOJ_CONVERSION_JAVA_ERROR = 59;
  public static final int EOJ_FAIL_CREATE_DESC = 60;
  public static final int EOJ_NO_DESCRIPTOR = 61;
  public static final int EOJ_INVALID_REF_CURSOR = 62;
  public static final int EOJ_NOT_IN_A_TRANSACTION = 63;
  public static final int EOJ_DATABASE_IS_NULL = 64;
  public static final int EOJ_CONV_WAS_NULL = 65;
  public static final int EOJ_ACCESS_SPECIFIC_IMPL = 66;
  public static final int EOJ_INVALID_URL = 67;
  public static final int EOJ_INVALID_ARGUMENTS = 68;
  public static final int EOJ_USE_XA_EXPLICIT = 69;
  public static final int EOJ_INVALID_DATASIZE_LENGTH = 70;
  public static final int EOJ_EXCEEDED_VARRAY_LENGTH = 71;
  public static final int EOJ_VALUE_TOO_BIG = 72;
  public static final int EOJ_INVALID_NAME_PATTERN = 74;
  public static final int EOJ_INVALID_FORWARD_RSET_OP = 75;
  public static final int EOJ_INVALID_READONLY_RSET_OP = 76;
  public static final int EOJ_FAIL_REF_SETVALUE = 77;
  public static final int EOJ_CONNECTIONS_ALREADY_EXIST = 78;
  public static final int EOJ_USER_CREDENTIALS_FAIL = 79;
  public static final int EOJ_INVALID_BATCH_COMMAND = 80;
  public static final int EOJ_BATCH_ERROR = 81;
  public static final int EOJ_NO_CURRENT_ROW = 82;
  public static final int EOJ_NOT_ON_INSERT_ROW = 83;
  public static final int EOJ_ON_INSERT_ROW = 84;
  public static final int EOJ_UPDATE_CONFLICTS = 85;
  public static final int EOJ_NULL_INSERET_ROW_VALUE = 86;
  public static final int WARN_IGNORE_FETCH_DIRECTION = 87;
  public static final int EOJ_UNSUPPORTED_SYNTAX = 88;
  public static final int EOJ_INTERNAL_ERROR = 89;
  public static final int EOJ_OPER_NOT_ALLOWED = 90;
  public static final int WARN_ALTERNATE_RSET_TYPE = 91;
  public static final int EOJ_NO_JDBC_AT_END_OF_CALL = 92;
  public static final int EOJ_WARN_SUCCESS_WITH_INFO = 93;
  public static final int EOJ_VERSION_MISMATCH = 94;
  public static final int EOJ_NO_STMT_CACHE_SIZE = 95;
  public static final int EOJ_INVALID_ELEMENT_TYPE = 97;
  public static final int EOJ_INVALID_EMPTYLOB_OP = 98;
  public static final int EOJ_INVALID_INDEXTABLE_ARRAY_LENGTH = 99;
  public static final int EOJ_INVALID_JAVA_OBJECT = 100;
  public static final int EOJ_CONNECTIONPOOL_INVALID_PROPERTIES = 101;
  public static final int EOJ_BFILE_IS_READONLY = 102;
  public static final int EOJ_WRONG_CONNECTION_TYPE_FOR_METHOD = 103;
  public static final int EOJ_NULL_SQL_STRING = 104;
  public static final int EOJ_SESSION_TZ_NOT_SET = 105;
  public static final int EOJ_CONNECTIONPOOL_INVALID_CONFIG = 106;
  public static final int EOJ_CONNECTIONPOOL_INVALID_PROXY_TYPE = 107;
  public static final int WARN_DEFINE_COLUMN_TYPE = 108;
  public static final int EOJ_STANDARD_ENCODING_NOT_FOUND = 109;
  public static final int EOJ_THIN_WARNING = 110;
  public static final int EOJ_WARN_CONN_CACHE_TIMEOUT = 111;
  public static final int EOJ_WARN_THREAD_TIMEOUT_INTERVAL = 112;
  public static final int EOJ_WARN_THREAD_INTERVAL_TOO_BIG = 113;
  public static final int EOJ_LOCAL_COMMIT_IN_GLOBAL_TXN = 114;
  public static final int EOJ_LOCAL_ROLLBACK_IN_GLOBAL_TXN = 115;
  public static final int EOJ_AUTOCOMMIT_IN_GLOBAL_TXN = 116;
  public static final int EOJ_SETSVPT_IN_GLOBAL_TXN = 117;
  public static final int EOJ_GETID_FOR_NAMED_SVPT = 118;
  public static final int EOJ_GETNAME_FOR_UNNAMED_SVPT = 119;
  public static final int EOJ_SETSVPT_WITH_AUTOCOMMIT = 120;
  public static final int EOJ_ROLLBACK_WITH_AUTOCOMMIT = 121;
  public static final int EOJ_ROLLBACK_TO_SVPT_IN_GLOBAL_TXN = 122;
  public static final int EOJ_INVALID_STMT_CACHE_SIZE = 123;
  public static final int EOJ_WARN_CACHE_INACTIVITY_TIMEOUT = 124;
  public static final int EOJ_IMPROPER_STATEMENT_TYPE = 125;
  public static final int EOJ_FIXED_WAIT_TIMEOUT = 126;
  public static final int EOJ_WARN_CACHE_FIXEDWAIT_TIMEOUT = 127;
  public static final int EOJ_INVALID_QUERY_STRING = 128;
  public static final int EOJ_INVALID_DML_STRING = 129;
  public static final int EOJ_QUERY_TIMEOUT_CLASS_NOT_FOUND = 130;
  public static final int EOJ_QUERY_TIMEOUT_INVALID_STATE = 131;
  public static final int EOJ_INVALID_OBJECT_TO_CONVERT = 132;
  public static final int EOJ_PARAMETER_NAME_TOO_LONG = 134;
  public static final int EOJ_PARAMETER_NAME_APPEARS_MORE_THAN_ONCE = 135;
  public static final int EOJ_MALFORMED_DLNK_URL = 136;
  public static final int EOJ_INVALID_CACHE_ENABLED_DATASOURCE = 137;
  public static final int EOJ_INVALID_CONNECTION_CACHE_NAME = 138;
  public static final int EOJ_INVALID_CONNECTION_CACHE_PROPERTIES = 139;
  public static final int EOJ_CONNECTION_CACHE_ALREADY_EXISTS = 140;
  public static final int EOJ_CONNECTION_CACHE_DOESNOT_EXIST = 141;
  public static final int EOJ_CONNECTION_CACHE_DISABLED = 142;
  public static final int EOJ_INVALID_CACHED_CONNECTION = 143;
  public static final int EOJ_STMT_NOT_EXECUTED = 144;
  public static final int EOJ_INVALID_ONS_EVENT = 145;
  public static final int EOJ_INVALID_ONS_EVENT_VERSION = 146;
  public static final int EOJ_UNKNOWN_PARAMETER_NAME = 147;
  public static final int EOJ_T4C_ONLY = 148;
  public static final int EOJ_ALREADY_PROXY = 149;
  public static final int EOJ_PROXY_WRONG_ARG = 150;
  public static final int EOJ_CLOB_TOO_LARGE = 151;
  public static final int EOJ_METHOD_FOR_LOGICAL_CONNECTION_ONLY = 152;
  public static final int EOJ_METHOD_FOR_PHYSICAL_CONNECTION_ONLY = 153;
  public static final int EOJ_EX_MAP_ORACLE_TO_UCS = 154;
  public static final int EOJ_EX_MAP_UCS_TO_ORACLE = 155;
  public static final int EOJ_E2E_METRIC_ARRAY_SIZE = 156;
  public static final int EOJ_SETSTRING_LIMIT = 157;
  public static final int EOJ_INVALID_DURATION = 158;
  public static final int EOJ_E2E_METRIC_TOO_LONG = 159;
  public static final int EOJ_E2E_SEQUENCE_NUMBER_OUT_OF_RANGE = 160;
  public static final int EOJ_INVALID_TXN_MODE = 161;
  public static final int EOJ_UNSUPPORTED_HOLDABILITY = 162;
  public static final int EOJ_GETXACONN_WHEN_CACHE_ENABLED = 163;
  public static final int EOJ_GETXARESOURCE_FROM_PHYSICAL_CONN = 164;
  public static final int EOJ_DBMS_JDBC_NOT_PRESENT = 165;
  public static final int EOJ_NO_FETCH_ON_PLSQL = 166;
  public static final int EOJ_ORACLEPKI_JAR_NOT_FOUND = 167;
  public static final int EOJ_PKI_WALLET_ERROR = 168;
  public static final int EOJ_NO_STREAM_BIND_ALLOWED = 169;
  public static final int EOJ_APP_CTXT_NULL_NAMESPACE = 170;
  public static final int EOJ_APP_CTXT_ATTR_TOO_LONG = 171;
  public static final int EOJ_APP_CTXT_VAL_TOO_LONG = 172;
  public static final int EOJ_DML_RETURNING_PARAM_NOT_REGISTERED = 173;
  public static final int EOJ_APP_CTXT_INVALID_NAMESPACE = 174;
  public static final int EOJ_REMOTE_ONS_CONFIG_ERROR = 175;
  public static final int EOJ_HETEROXA_GET_UTF_OPENSTR = 200;
  public static final int EOJ_HETEROXA_GET_UTF_CLOSESTR = 201;
  public static final int EOJ_HETEROXA_GET_UTF_RMNAME = 202;
  public static final int EOJ_HETEROXA_JHANDLE_SIZE = 203;
  public static final int EOJ_HETEROXA_ARRAY_TOO_SHORT = 204;
  public static final int EOJ_HETEROXA_SVCCTX_HANDLE = 205;
  public static final int EOJ_HETEROXA_ENV_HANDLE = 206;
  public static final int EOJ_HETEROXA_NULL_TNSENTRY = 207;
  public static final int EOJ_HETEROXA_OPEN_RMERR = 213;
  public static final int EOJ_HETEROXA_OPEN_INVAL = 215;
  public static final int EOJ_HETEROXA_OPEN_PROTO = 216;
  public static final int EOJ_HETEROXA_CLOSE_RMERR = 233;
  public static final int EOJ_HETEROXA_CLOSE_INVAL = 235;
  public static final int EOJ_HETEROXA_CLOSE_PROTO = 236;
  public static final int TTC_ERR_BASE = 400;
  public static final int TTC0000 = 401;
  public static final int TTC0001 = 402;
  public static final int TTC0002 = 403;
  public static final int TTC0003 = 404;
  public static final int TTC0004 = 405;
  public static final int TTC0005 = 406;
  public static final int TTC0100 = 407;
  public static final int TTC0101 = 408;
  public static final int TTC0102 = 409;
  public static final int TTC0103 = 410;
  public static final int TTC0104 = 411;
  public static final int TTC0105 = 412;
  public static final int TTC0106 = 413;
  public static final int TTC0107 = 414;
  public static final int TTC0108 = 415;
  public static final int TTC0109 = 416;
  public static final int TTC0110 = 417;
  public static final int TTC0111 = 418;
  public static final int TTC0112 = 419;
  public static final int TTC0113 = 420;
  public static final int TTC0114 = 421;
  public static final int TTC0115 = 422;
  public static final int TTC0116 = 423;
  public static final int TTC0117 = 424;
  public static final int TTC0118 = 425;
  public static final int TTC0119 = 426;
  public static final int TTC0120 = 427;
  public static final int TTC0200 = 428;
  public static final int TTC0201 = 429;
  public static final int TTC0202 = 430;
  public static final int TTC0203 = 431;
  public static final int TTC0204 = 432;
  public static final int TTC0205 = 433;
  public static final int TTC0206 = 434;
  public static final int TTC0207 = 435;
  public static final int TTC0208 = 436;
  public static final int TTC0209 = 437;
  public static final int TTC0210 = 438;
  public static final int TTC0211 = 439;
  public static final int TTC0212 = 440;
  public static final int TTC0213 = 441;
  public static final int TTC0214 = 442;
  public static final int TTC0216 = 443;
  public static final int TTC0217 = 444;
  public static final int TTC0218 = 445;
  public static final int TTC0219 = 446;
  public static final int TTC0220 = 447;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public static SQLException newSqlException(int paramInt, Object paramObject)
  {
    String str = findMessage(paramInt, paramObject);
    int i = getVendorCode(paramInt);

    return new SQLException(str, null, i);
  }

  public static SQLException newSqlException(int paramInt)
  {
    return newSqlException(paramInt, null);
  }

  public static void throwSqlException(String paramString1, String paramString2, int paramInt)
    throws SQLException
  {
    throw new SQLException(paramString1, paramString2, paramInt);
  }

  public static void throwSqlException(int paramInt, Object paramObject)
    throws SQLException
  {
    if ((paramInt == 0) || (paramInt == 13))
    {
      return;
    }

    String str = findMessage(paramInt, paramObject);
    int i = getVendorCode(paramInt);

    throwSqlException(str, null, i);
  }

  public static void throwSqlException(SQLException paramSQLException, int paramInt, Object paramObject)
    throws SQLException
  {
    if ((paramInt == 0) || (paramInt == 13))
    {
      return;
    }

    String str = findMessage(paramInt, paramObject);
    int i = getVendorCode(paramInt);

    SQLException localSQLException = new SQLException(str, null, i);

    localSQLException.setNextException(paramSQLException);

    throw localSQLException;
  }

  public static void throwSqlException(int paramInt)
    throws SQLException
  {
    throwSqlException(paramInt, null);
  }

  public static void throwSqlException(IOException paramIOException)
    throws SQLException
  {
    String str = paramIOException.getMessage();

    int k = 0;
    int i;
    int j;
    if (((i = str.indexOf("ORA-")) != -1) && ((j = str.indexOf(":")) != -1))
    {
      i += 4;
      try
      {
        k = Integer.parseInt(str.substring(i, j));
      }
      catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException)
      {
      }
      catch (NumberFormatException localNumberFormatException)
      {
      }
    }
    else
    {
      throwSqlException(2, str);
    }

    throwSqlException(str, null, k);
  }

  public static void addSqlException(SQLException paramSQLException, String paramString1, String paramString2, int paramInt)
  {
    SQLException localSQLException = new SQLException(paramString1, paramString2, paramInt);

    paramSQLException.setNextException(localSQLException);
  }

  public static void throwBatchUpdateException(SQLException paramSQLException, int paramInt, int[] paramArrayOfInt)
    throws BatchUpdateException
  {
    int i = 0;
    int[] arrayOfInt = null;

    if (paramInt < 0)
    {
      paramInt = 0;
    }

    if (paramArrayOfInt == null)
    {
      arrayOfInt = new int[0];
    }
    else if (paramInt >= paramArrayOfInt.length)
    {
      arrayOfInt = paramArrayOfInt;
    }
    else
    {
      arrayOfInt = new int[paramInt];

      for (i = 0; i < paramInt; i++)
      {
        arrayOfInt[i] = paramArrayOfInt[i];
      }
    }

    BatchUpdateException localBatchUpdateException = new BatchUpdateException(paramSQLException.getMessage(), paramSQLException.getSQLState(), paramSQLException.getErrorCode(), arrayOfInt);

    localBatchUpdateException.setNextException(paramSQLException);

    throw localBatchUpdateException;
  }

  public static void throwBatchUpdateException(String paramString1, String paramString2, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    throws BatchUpdateException
  {
    int i = 0;
    int[] arrayOfInt = null;

    if (paramInt2 < 0)
    {
      paramInt2 = 0;
    }

    if (paramArrayOfInt == null)
    {
      arrayOfInt = new int[0];
    }
    else if (paramInt2 >= paramArrayOfInt.length)
    {
      arrayOfInt = paramArrayOfInt;
    }
    else
    {
      arrayOfInt = new int[paramInt2];

      for (i = 0; i < paramInt2; i++)
      {
        arrayOfInt[i] = paramArrayOfInt[i];
      }

    }

    throw new BatchUpdateException(paramString1, paramString2, paramInt1, arrayOfInt);
  }

  public static void throwBatchUpdateException(int paramInt1, Object paramObject, int paramInt2, int[] paramArrayOfInt)
    throws BatchUpdateException
  {
    if (paramInt1 == 0)
    {
      return;
    }

    String str = findMessage(paramInt1, paramObject);
    int i = getVendorCode(paramInt1);

    throwBatchUpdateException(str, null, i, paramInt2, paramArrayOfInt);
  }

  public static void throwBatchUpdateException(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    throws BatchUpdateException
  {
    throwBatchUpdateException(paramInt1, null, paramInt2, paramArrayOfInt);
  }

  public static void throwUnsupportedFeatureSqlException()
    throws SQLException
  {
    throwSqlException(23);
  }

  public static void SQLToIOException(SQLException paramSQLException)
    throws IOException
  {
    throw new IOException(paramSQLException.getMessage());
  }

  static String findMessage(int paramInt, Object paramObject)
  {
    String str1 = null;
    String str2 = null;

    if (!loadedMessages)
    {
      try
      {
        message = (Message)Class.forName(msgClassName).newInstance();
        loadedMessages = true;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
      }
      catch (InstantiationException localInstantiationException)
      {
      }
      finally {
        if (loadedMessages);
      }
    }
    str1 = getMsgKey(paramInt);

    if (message == null)
    {
      if (paramObject == null)
      {
        str2 = str1 + ": (no message for error)";
      }
      else
      {
        str2 = str1 + ": (no message for error) " + paramObject;
      }
    }
    else
    {
      str2 = message.msg(str1, paramObject);
    }

    return str2;
  }

  public static SQLWarning newSqlWarning(String paramString1, String paramString2, int paramInt)
    throws SQLException
  {
    return addSqlWarning(null, paramString1, paramString2, paramInt);
  }

  public static SQLWarning newSqlWarning(int paramInt, Object paramObject)
    throws SQLException
  {
    return addSqlWarning(null, paramInt, paramObject);
  }

  public static SQLWarning newSqlWarning(int paramInt)
    throws SQLException
  {
    return addSqlWarning(null, paramInt);
  }

  public static SQLWarning addSqlWarning(SQLWarning paramSQLWarning, String paramString1, String paramString2, int paramInt)
    throws SQLException
  {
    SQLWarning localSQLWarning = new SQLWarning(paramString1, paramString2, paramInt);

    return addSqlWarning(paramSQLWarning, localSQLWarning);
  }

  public static SQLWarning addSqlWarning(SQLWarning paramSQLWarning1, SQLWarning paramSQLWarning2)
    throws SQLException
  {
    if (paramSQLWarning1 == null) {
      return paramSQLWarning2;
    }

    paramSQLWarning1.setNextWarning(paramSQLWarning2);

    return paramSQLWarning1;
  }

  public static SQLWarning addSqlWarning(SQLWarning paramSQLWarning, int paramInt, Object paramObject)
    throws SQLException
  {
    if ((paramInt == 0) || (paramInt == 13))
    {
      return paramSQLWarning;
    }

    String str = findMessage(paramInt, paramObject);
    int i = getVendorCode(paramInt);

    return addSqlWarning(paramSQLWarning, "Warning: " + str, null, i);
  }

  public static SQLWarning addSqlWarning(SQLWarning paramSQLWarning, int paramInt)
    throws SQLException
  {
    return addSqlWarning(paramSQLWarning, paramInt, null);
  }

  public static String ErrorToSQLState(int paramInt)
  {
    for (int i = 0; i < mappings.length; i++) {
      if (paramInt == mappings[i].err) {
        return mappings[i].sqlState;
      }
    }

    for (i = 0; i < ranges.length; i++) {
      if ((paramInt > ranges[i].low) && (paramInt <= ranges[i].high)) {
        return ranges[i].sqlState;
      }

    }

    return "99999";
  }

  public static int getVendorCode(int paramInt)
  {
    if (paramInt >= 500);
    return 17000 + paramInt;
  }

  static String getMsgKey(int paramInt)
  {
    int i = getVendorCode(paramInt);
    String str = "ORA-" + Integer.toString(i);

    return str;
  }

  public static void test()
  {
    OracleLog.config(0, 1, 32);
    OracleLog.setSubmodMask(1, 4);
    OracleLog.setLogStream(System.out);
    try
    {
      throwSqlException("exception_message_1", "sql_state_1", 25);
    }
    catch (SQLException localSQLException1)
    {
      printSqlException(localSQLException1);
    }

    try
    {
      throwSqlException(412, new String("object_string"));
    }
    catch (SQLException localSQLException2)
    {
      printSqlException(localSQLException2);
    }

    try
    {
      throwSqlException(6);
    }
    catch (SQLException localSQLException3)
    {
      printSqlException(localSQLException3);
    }

    try
    {
      throwSqlException(999);
    }
    catch (SQLException localSQLException4)
    {
      printSqlException(localSQLException4);
    }

    try
    {
      throwSqlException(13);
    }
    catch (SQLException localSQLException5)
    {
      printSqlException(localSQLException5);
    }

    try
    {
      IOException localIOException1 = new IOException("ORA-00601: cleanup lock conflict");

      throwSqlException(localIOException1);
    }
    catch (SQLException localSQLException6)
    {
      printSqlException(localSQLException6);
    }

    try
    {
      IOException localIOException2 = new IOException("some unknown io exception");

      throwSqlException(localIOException2);
    }
    catch (SQLException localSQLException7)
    {
      printSqlException(localSQLException7);
    }
  }

  public static void printSqlException(SQLException paramSQLException)
  {
    OracleLog.print(null, 1, 4, 32, "SQLException:");

    OracleLog.print(null, 1, 4, 32, "  message  = \"" + paramSQLException.getMessage() + "\"");

    OracleLog.print(null, 1, 4, 32, "  sqlState = \"" + paramSQLException.getSQLState() + "\"");

    OracleLog.print(null, 1, 4, 32, "  errCode  = " + paramSQLException.getErrorCode());
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.DatabaseError
 * JD-Core Version:    0.6.0
 */