package oracle.jdbc.driver;

import java.io.PrintStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class OracleSql
{
  static final int UNINITIALIZED = -1;
  static final String[] EMPTY_LIST = new String[0];
  DBConversion conversion;
  String originalSql;
  String parameterSql;
  String utickSql;
  String processedSql;
  String rowidSql;
  String actualSql;
  byte[] sqlBytes;
  byte sqlKind = -1;
  int parameterCount = -1;
  boolean currentConvertNcharLiterals = true;
  boolean currentProcessEscapes = true;
  boolean includeRowid = false;
  String[] parameterList = EMPTY_LIST;
  char[] currentParameter = null;
  boolean isV8Compatible = false;

  int bindParameterCount = -1;
  String[] bindParameterList = null;
  int cachedBindParameterCount = -1;
  String[] cachedBindParameterList = null;
  String cachedParameterSql;
  String cachedUtickSql;
  String cachedProcessedSql;
  String cachedRowidSql;
  String cachedActualSql;
  byte[] cachedSqlBytes;
  int selectEndIndex = -1;
  int orderByStartIndex = -1;
  int orderByEndIndex = -1;
  int whereStartIndex = -1;
  int whereEndIndex = -1;
  int forUpdateStartIndex = -1;
  int forUpdateEndIndex = -1;

  final int[] ncharLiteralLocation = new int[513];
  int lastNcharLiteralLocation = -1;
  static final String paramPrefix = "rowid";
  int paramSuffix = 0;

  StringBuffer stringBufferForScrollableStatement = null;
  private static final int cMax = 127;
  private static final int[][] TRANSITION = OracleSqlReadOnly.TRANSITION;
  private static final int[][] ACTION = OracleSqlReadOnly.ACTION;
  private static final int NO_ACTION = 0;
  private static final int DML_ACTION = 1;
  private static final int PLSQL_ACTION = 2;
  private static final int CALL_ACTION = 3;
  private static final int SELECT_ACTION = 4;
  private static final int ORDER_ACTION = 7;
  private static final int ORDER_BY_ACTION = 8;
  private static final int WHERE_ACTION = 6;
  private static final int FOR_ACTION = 9;
  private static final int FOR_UPDATE_ACTION = 10;
  private static final int OTHER_ACTION = 5;
  private static final int QUESTION_ACTION = 11;
  private static final int PARAMETER_ACTION = 12;
  private static final int END_PARAMETER_ACTION = 13;
  private static final int START_NCHAR_LITERAL_ACTION = 14;
  private static final int END_NCHAR_LITERAL_ACTION = 15;
  int current_argument;
  int i;
  int length;
  char c;
  boolean first;
  boolean in_string;
  String odbc_sql;
  StringBuffer oracle_sql;
  StringBuffer token_buffer;
  boolean isLocate = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  OracleSql(DBConversion paramDBConversion)
  {
    this.conversion = paramDBConversion;
  }

  void initialize(String paramString)
    throws SQLException
  {
    if ((paramString == null) || (paramString == "")) {
      DatabaseError.throwSqlException(104);
    }
    this.originalSql = paramString;
    this.utickSql = null;
    this.processedSql = null;
    this.rowidSql = null;
    this.actualSql = null;
    this.sqlBytes = null;
    this.sqlKind = -1;
    this.parameterCount = -1;
    this.includeRowid = false;

    this.parameterSql = this.originalSql;
    this.bindParameterCount = -1;
    this.bindParameterList = null;
    this.cachedBindParameterCount = -1;
    this.cachedBindParameterList = null;
    this.cachedParameterSql = null;
    this.cachedActualSql = null;
    this.cachedProcessedSql = null;
    this.cachedRowidSql = null;
    this.cachedSqlBytes = null;
    this.selectEndIndex = -1;
    this.orderByStartIndex = -1;
    this.orderByEndIndex = -1;
    this.whereStartIndex = -1;
    this.whereEndIndex = -1;
    this.forUpdateStartIndex = -1;
    this.forUpdateEndIndex = -1;
  }

  String getOriginalSql()
  {
    return this.originalSql;
  }

  boolean setNamedParameters(int paramInt, String[] paramArrayOfString)
    throws SQLException
  {
    int j = 0;

    if (paramInt == 0)
    {
      this.bindParameterCount = -1;
      j = this.bindParameterCount != this.cachedBindParameterCount ? 1 : 0;
    }
    else
    {
      this.bindParameterCount = paramInt;
      this.bindParameterList = paramArrayOfString;
      j = this.bindParameterCount != this.cachedBindParameterCount ? 1 : 0;

      if (j == 0) {
        for (int k = 0; k < paramInt; k++) {
          if (this.bindParameterList[k] == this.cachedBindParameterList[k])
            continue;
          j = 1;

          break;
        }
      }
      if (j != 0)
      {
        if (this.bindParameterCount != getParameterCount()) {
          throw new SQLException("Incorrectly set or registered parameters.");
        }
        char[] arrayOfChar = this.originalSql.toCharArray();
        StringBuffer localStringBuffer = new StringBuffer();

        int m = 0;

        for (int n = 0; n < arrayOfChar.length; n++)
        {
          if (arrayOfChar[n] != '?')
          {
            localStringBuffer.append(arrayOfChar[n]);
          }
          else
          {
            localStringBuffer.append(this.bindParameterList[(m++)]);
            localStringBuffer.append("=>?");
          }
        }

        if (m != this.bindParameterCount) {
          throw new SQLException("Incorrectly set or registered parameters.");
        }
        this.parameterSql = new String(localStringBuffer);
        this.actualSql = null;
        this.utickSql = null;
        this.processedSql = null;
        this.rowidSql = null;
        this.sqlBytes = null;
      }
      else
      {
        this.parameterSql = this.cachedParameterSql;
        this.actualSql = this.cachedActualSql;
        this.utickSql = this.cachedUtickSql;
        this.processedSql = this.cachedProcessedSql;
        this.rowidSql = this.cachedRowidSql;
        this.sqlBytes = this.cachedSqlBytes;
      }
    }

    this.cachedBindParameterList = null;
    this.cachedParameterSql = null;
    this.cachedActualSql = null;
    this.cachedUtickSql = null;
    this.cachedProcessedSql = null;
    this.cachedRowidSql = null;
    this.cachedSqlBytes = null;

    return j;
  }

  void resetNamedParameters()
  {
    this.cachedBindParameterCount = this.bindParameterCount;

    if (this.bindParameterCount != -1)
    {
      if ((this.cachedBindParameterList == null) || (this.cachedBindParameterList == this.bindParameterList) || (this.cachedBindParameterList.length < this.bindParameterCount))
      {
        this.cachedBindParameterList = new String[this.bindParameterCount];
      }
      System.arraycopy(this.bindParameterList, 0, this.cachedBindParameterList, 0, this.bindParameterCount);

      this.cachedParameterSql = this.parameterSql;
      this.cachedActualSql = this.actualSql;
      this.cachedUtickSql = this.utickSql;
      this.cachedProcessedSql = this.processedSql;
      this.cachedRowidSql = this.rowidSql;
      this.cachedSqlBytes = this.sqlBytes;

      this.bindParameterCount = -1;
      this.bindParameterList = null;
      this.parameterSql = this.originalSql;
      this.actualSql = null;
      this.utickSql = null;
      this.processedSql = null;
      this.rowidSql = null;
      this.sqlBytes = null;
    }
  }

  String getSql(boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException
  {
    if (this.sqlKind == -1) computeBasicInfo(this.parameterSql);
    if ((paramBoolean1 != this.currentProcessEscapes) || (paramBoolean2 != this.currentConvertNcharLiterals))
    {
      if (paramBoolean2 != this.currentConvertNcharLiterals) this.utickSql = null;
      this.processedSql = null;
      this.rowidSql = null;
      this.actualSql = null;
      this.sqlBytes = null;
    }

    this.currentConvertNcharLiterals = paramBoolean2;
    this.currentProcessEscapes = paramBoolean1;

    if (this.actualSql == null)
    {
      if (this.utickSql == null)
        this.utickSql = (this.currentConvertNcharLiterals ? convertNcharLiterals(this.parameterSql) : this.parameterSql);
      if (this.processedSql == null)
        this.processedSql = (this.currentProcessEscapes ? parse(this.utickSql) : this.utickSql);
      if (this.rowidSql == null)
        this.rowidSql = (this.includeRowid ? addRowid(this.processedSql) : this.processedSql);
      this.actualSql = this.rowidSql;
    }

    return this.actualSql;
  }

  String getRevisedSql()
    throws SQLException
  {
    String str = this.originalSql;

    if (this.sqlKind == -1) computeBasicInfo(this.parameterSql);

    str = removeForUpdate(str);

    return addRowid(str);
  }

  String removeForUpdate(String paramString)
    throws SQLException
  {
    if ((this.orderByStartIndex != -1) && ((this.forUpdateStartIndex == -1) || (this.forUpdateStartIndex > this.orderByStartIndex)))
    {
      paramString = paramString.substring(0, this.orderByStartIndex);
    }
    else if (this.forUpdateStartIndex != -1)
    {
      paramString = paramString.substring(0, this.forUpdateStartIndex);
    }

    return paramString;
  }

  void appendForUpdate(StringBuffer paramStringBuffer)
    throws SQLException
  {
    if ((this.orderByStartIndex != -1) && ((this.forUpdateStartIndex == -1) || (this.forUpdateStartIndex > this.orderByStartIndex)))
    {
      paramStringBuffer.append(this.originalSql.substring(this.orderByStartIndex));
    }
    else if (this.forUpdateStartIndex != -1)
    {
      paramStringBuffer.append(this.originalSql.substring(this.forUpdateStartIndex));
    }
  }

  String getInsertSqlForUpdatableResultSet(UpdatableResultSet paramUpdatableResultSet)
    throws SQLException
  {
    String str = getOriginalSql();

    if (this.stringBufferForScrollableStatement == null)
      this.stringBufferForScrollableStatement = new StringBuffer(str.length() + 100);
    else {
      this.stringBufferForScrollableStatement.delete(0, this.stringBufferForScrollableStatement.length());
    }
    this.stringBufferForScrollableStatement.append("insert into (");

    this.stringBufferForScrollableStatement.append(removeForUpdate(str));
    this.stringBufferForScrollableStatement.append(") values ( ");

    int j = 1;
    while (j < paramUpdatableResultSet.getColumnCount())
    {
      if (j != 1) {
        this.stringBufferForScrollableStatement.append(", ");
      }
      this.stringBufferForScrollableStatement.append(":" + generateParameterName());

      j++;
    }

    this.stringBufferForScrollableStatement.append(")");

    return this.stringBufferForScrollableStatement.substring(0, this.stringBufferForScrollableStatement.length());
  }

  String getRefetchSqlForScrollableResultSet(ScrollableResultSet paramScrollableResultSet, int paramInt)
    throws SQLException
  {
    String str = getRevisedSql();

    if (this.stringBufferForScrollableStatement == null)
      this.stringBufferForScrollableStatement = new StringBuffer(str.length() + 100);
    else {
      this.stringBufferForScrollableStatement.delete(0, this.stringBufferForScrollableStatement.length());
    }

    this.stringBufferForScrollableStatement.append(str);

    this.stringBufferForScrollableStatement.append(" WHERE ( ROWID = :" + generateParameterName());

    for (int j = 0; j < paramInt - 1; j++) {
      this.stringBufferForScrollableStatement.append(" OR ROWID = :" + generateParameterName());
    }
    this.stringBufferForScrollableStatement.append(" ) ");

    appendForUpdate(this.stringBufferForScrollableStatement);

    return this.stringBufferForScrollableStatement.substring(0, this.stringBufferForScrollableStatement.length());
  }

  String getUpdateSqlForUpdatableResultSet(UpdatableResultSet paramUpdatableResultSet, int paramInt, Object[] paramArrayOfObject, int[] paramArrayOfInt)
    throws SQLException
  {
    String str = getRevisedSql();

    if (this.stringBufferForScrollableStatement == null)
      this.stringBufferForScrollableStatement = new StringBuffer(str.length() + 100);
    else {
      this.stringBufferForScrollableStatement.delete(0, this.stringBufferForScrollableStatement.length());
    }

    this.stringBufferForScrollableStatement.append("update (");
    this.stringBufferForScrollableStatement.append(str);
    this.stringBufferForScrollableStatement.append(") set ");

    if (paramArrayOfObject != null)
    {
      for (int j = 0; j < paramInt; j++)
      {
        if (j > 0) {
          this.stringBufferForScrollableStatement.append(", ");
        }
        this.stringBufferForScrollableStatement.append(paramUpdatableResultSet.getInternalMetadata().getColumnName(paramArrayOfInt[j] + 1));

        this.stringBufferForScrollableStatement.append(" = :" + generateParameterName());
      }

    }

    this.stringBufferForScrollableStatement.append(" WHERE ");
    this.stringBufferForScrollableStatement.append(" ROWID = :" + generateParameterName());

    return this.stringBufferForScrollableStatement.substring(0, this.stringBufferForScrollableStatement.length());
  }

  String getDeleteSqlForUpdatableResultSet(UpdatableResultSet paramUpdatableResultSet)
    throws SQLException
  {
    String str = getRevisedSql();

    if (this.stringBufferForScrollableStatement == null)
      this.stringBufferForScrollableStatement = new StringBuffer(str.length() + 100);
    else {
      this.stringBufferForScrollableStatement.delete(0, this.stringBufferForScrollableStatement.length());
    }

    this.stringBufferForScrollableStatement.append("delete from (");
    this.stringBufferForScrollableStatement.append(str);
    this.stringBufferForScrollableStatement.append(") where ");

    this.stringBufferForScrollableStatement.append(" ROWID = :" + generateParameterName());

    return this.stringBufferForScrollableStatement.substring(0, this.stringBufferForScrollableStatement.length());
  }

  byte[] getSqlBytes(boolean paramBoolean1, boolean paramBoolean2)
    throws SQLException
  {
    if ((this.sqlBytes == null) || (paramBoolean1 != this.currentProcessEscapes))
    {
      this.sqlBytes = this.conversion.StringToCharBytes(getSql(paramBoolean1, paramBoolean2));
    }

    return this.sqlBytes;
  }

  byte getSqlKind()
    throws SQLException
  {
    if (this.sqlKind == -1) {
      computeBasicInfo(this.parameterSql);
    }
    return this.sqlKind;
  }

  int getParameterCount()
    throws SQLException
  {
    if (this.parameterCount == -1)
    {
      computeBasicInfo(this.parameterSql);
    }

    return this.parameterCount;
  }

  String[] getParameterList()
    throws SQLException
  {
    if (this.parameterCount == -1)
    {
      computeBasicInfo(this.parameterSql);
    }

    return this.parameterList;
  }

  void setIncludeRowid(boolean paramBoolean)
  {
    if (paramBoolean != this.includeRowid)
    {
      this.includeRowid = paramBoolean;
      this.rowidSql = null;
      this.actualSql = null;
      this.sqlBytes = null;
    }
  }

  public String toString()
  {
    return this.parameterSql == null ? "null" : this.parameterSql;
  }

  private String hexUnicode(int paramInt)
    throws SQLException
  {
    String str = Integer.toHexString(paramInt);
    switch (str.length()) {
    case 0:
      return "\\0000";
    case 1:
      return "\\000" + str;
    case 2:
      return "\\00" + str;
    case 3:
      return "\\0" + str;
    case 4:
      return "\\" + str;
    }
    DatabaseError.throwSqlException(89, "Unexpected case in OracleSql.hexUnicode: " + paramInt);

    return "never happen";
  }

  String convertNcharLiterals(String paramString)
    throws SQLException
  {
    if (this.lastNcharLiteralLocation <= 2) return paramString;
    String str = "";
    int j = 0;
    while (true)
    {
      int k = this.ncharLiteralLocation[(j++)];
      int m = this.ncharLiteralLocation[(j++)];

      str = str + paramString.substring(k, m);
      if (j >= this.lastNcharLiteralLocation) break;
      k = this.ncharLiteralLocation[j];
      str = str + "u'";

      for (int n = m + 2; n < k; n++)
      {
        char c1 = paramString.charAt(n);
        if (c1 == '\\') str = str + "\\\\";
        else if (c1 < '') str = str + c1; else
          str = str + hexUnicode(c1);
      }
    }
    return str;
  }

  void computeBasicInfo(String paramString)
    throws SQLException
  {
    this.parameterCount = 0;

    this.lastNcharLiteralLocation = 0;
    this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = 0;

    int j = 0;

    int k = 0;
    int m = paramString.length();
    int n = -1;
    int i1 = -1;
    int i2 = m + 1;

    for (int i3 = 0; i3 < i2; i3++)
    {
      char c1 = i3 < m ? paramString.charAt(i3) : ' ';
      char c2 = c1;

      if (c1 > '')
      {
        if (Character.isLetterOrDigit(c1))
          c2 = 'X';
        else {
          c2 = ' ';
        }

      }

      switch (ACTION[k][c2])
      {
      case 0:
        break;
      case 1:
        this.sqlKind = 2;

        break;
      case 2:
        this.sqlKind = 1;

        break;
      case 3:
        this.sqlKind = 4;

        break;
      case 4:
        this.sqlKind = 0;
        this.selectEndIndex = i3;

        break;
      case 5:
        this.sqlKind = 3;

        break;
      case 6:
        this.whereStartIndex = (i3 - 5);
        this.whereEndIndex = i3;

        break;
      case 7:
        n = i3 - 5;

        break;
      case 8:
        this.orderByStartIndex = n;
        this.orderByEndIndex = i3;

        break;
      case 9:
        i1 = i3 - 3;

        break;
      case 10:
        this.forUpdateStartIndex = i1;
        this.forUpdateEndIndex = i3;

        break;
      case 11:
        this.parameterCount += 1;

        break;
      case 12:
        if (this.currentParameter == null) {
          this.currentParameter = new char[32];
        }
        if (j >= this.currentParameter.length) {
          DatabaseError.throwSqlException(134, new String(this.currentParameter));
        }

        this.currentParameter[(j++)] = c1;

        break;
      case 13:
        if (j <= 0)
          break;
        if (this.parameterList == EMPTY_LIST)
        {
          this.parameterList = new String[8];
        }
        else if (this.parameterList.length <= this.parameterCount)
        {
          String[] arrayOfString = new String[this.parameterList.length * 4];

          System.arraycopy(this.parameterList, 0, arrayOfString, 0, this.parameterList.length);

          this.parameterList = arrayOfString;
        }

        this.parameterList[this.parameterCount] = new String(this.currentParameter, 0, j).intern();

        j = 0;
        this.parameterCount += 1; break;
      case 14:
        this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = (i3 - 1);

        break;
      case 15:
        this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = (i3 + 1);
      }

      k = TRANSITION[k][c2];
    }

    this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = m;
    this.ncharLiteralLocation[this.lastNcharLiteralLocation] = m;
  }

  private String addRowid(String paramString)
    throws SQLException
  {
    if (this.selectEndIndex == -1) {
      DatabaseError.throwSqlException(88);
    }
    String str = "select rowid," + paramString.substring(this.selectEndIndex);

    return str;
  }

  String parse(String paramString)
    throws SQLException
  {
    this.current_argument = 1;
    this.i = 0;
    this.first = true;
    this.in_string = false;
    this.odbc_sql = paramString;
    this.length = this.odbc_sql.length();

    if (this.oracle_sql == null)
    {
      this.oracle_sql = new StringBuffer(this.length);
      this.token_buffer = new StringBuffer(32);
    }
    else
    {
      this.oracle_sql.ensureCapacity(this.length);
    }

    this.oracle_sql.delete(0, this.oracle_sql.length());
    skipSpace();
    handleODBC();

    if (this.i < this.length)
    {
      Integer localInteger = new Integer(this.i);

      DatabaseError.throwSqlException(33, localInteger);
    }

    return this.oracle_sql.substring(0, this.oracle_sql.length());
  }

  void handleODBC() throws SQLException
  {
    StringBuffer localStringBuffer1 = null;
    StringBuffer localStringBuffer2 = null;
    int j = 0;

    while (this.i < this.length)
    {
      this.c = this.odbc_sql.charAt(this.i);

      if (this.in_string)
      {
        this.oracle_sql.append(this.c);

        if (this.c == '\'') {
          this.in_string = false;
        }
        this.i += 1; continue;
      }

      switch (this.c)
      {
      case '\'':
        if (this.isLocate)
        {
          if (localStringBuffer1 == null)
          {
            localStringBuffer1 = new StringBuffer();
            localStringBuffer2 = new StringBuffer();
          }

          if (j == 0)
            localStringBuffer1.append(this.c);
          else {
            localStringBuffer2.append(this.c);
          }
          this.i += 1;

          continue;
        }

        this.oracle_sql.append(this.c);

        this.in_string = true;
        this.i += 1;
        this.first = false;

        break;
      case '{':
        this.token_buffer.delete(0, this.token_buffer.length());

        this.i += 1;

        skipSpace();

        while ((this.i < this.length) && ((Character.isJavaLetterOrDigit(this.c = this.odbc_sql.charAt(this.i))) || (this.c == '?')))
        {
          this.token_buffer.append(this.c);

          this.i += 1;
        }

        handleToken(this.token_buffer.substring(0, this.token_buffer.length()));

        this.c = this.odbc_sql.charAt(this.i);

        if (this.c != '}')
        {
          String str = new String(this.i + ": Expecting \"}\" got \"" + this.c + "\"");

          DatabaseError.throwSqlException(33, str);
        }

        this.i += 1;

        break;
      case '}':
        return;
      default:
        if ((this.c != ' ') && (this.c != '(') && (this.isLocate))
        {
          if (this.c == ')')
          {
            if (localStringBuffer2.substring(0, localStringBuffer2.length()).trim().equals("?"))
              this.oracle_sql.append(nextArgument());
            else {
              this.oracle_sql.append(localStringBuffer2);
            }
            this.oracle_sql.append(", ");

            if (localStringBuffer1.substring(0, localStringBuffer1.length()).trim().equals("?"))
              this.oracle_sql.append(nextArgument());
            else {
              this.oracle_sql.append(localStringBuffer1);
            }
            appendChar(this.oracle_sql, this.c);

            this.isLocate = false;
          }

          if (localStringBuffer1 == null)
          {
            localStringBuffer1 = new StringBuffer();
            localStringBuffer2 = new StringBuffer();
          }

          if (this.c == ',')
          {
            j = 1;
            this.i += 1;
            this.first = false;

            continue;
          }

          if (j == 0)
            localStringBuffer1.append(this.c);
          else {
            localStringBuffer2.append(this.c);
          }
        }
        else
        {
          appendChar(this.oracle_sql, this.c);
        }

        this.i += 1;
        this.first = false;
      }
    }
  }

  void handleToken(String paramString) throws SQLException
  {
    if (paramString.equalsIgnoreCase("?")) {
      handleFunction();
    } else if (paramString.equalsIgnoreCase("call")) {
      handleCall();
    } else if (paramString.equalsIgnoreCase("ts")) {
      handleTimestamp();
    } else if (paramString.equalsIgnoreCase("t")) {
      handleTime();
    } else if (paramString.equalsIgnoreCase("d")) {
      handleDate();
    } else if (paramString.equalsIgnoreCase("escape")) {
      handleEscape();
    } else if (paramString.equalsIgnoreCase("fn")) {
      handleScalarFunction();
    } else if (paramString.equalsIgnoreCase("oj")) {
      handleOuterJoin();
    }
    else {
      String str = new String(this.i + ": " + paramString);

      DatabaseError.throwSqlException(34, str);
    }
  }

  void handleFunction()
    throws SQLException
  {
    boolean bool = this.first;

    if (bool) {
      this.oracle_sql.append("BEGIN ");
    }
    appendChar(this.oracle_sql, '?');
    skipSpace();
    String str;
    if (this.c != '=')
    {
      str = new String(this.i + ". Expecting \"=\" got \"" + this.c + "\"");

      DatabaseError.throwSqlException(33, str);
    }

    this.i += 1;

    skipSpace();

    if (!this.odbc_sql.startsWith("call", this.i))
    {
      str = new String(this.i + ". Expecting \"call\"");

      DatabaseError.throwSqlException(33, str);
    }

    this.i += 4;

    this.oracle_sql.append(" := ");
    skipSpace();
    handleODBC();

    if (bool)
      this.oracle_sql.append("; END;");
  }

  void handleCall()
    throws SQLException
  {
    boolean bool = this.first;

    if (bool) {
      this.oracle_sql.append("BEGIN ");
    }
    skipSpace();
    handleODBC();
    skipSpace();

    if (bool)
      this.oracle_sql.append("; END;");
  }

  void handleTimestamp() throws SQLException
  {
    if (this.isV8Compatible)
    {
      this.oracle_sql.append("TO_DATE (");
      skipSpace();

      int j = 0;

      while ((this.i < this.length) && ((this.c = this.odbc_sql.charAt(this.i)) != '}'))
      {
        if (j == 0)
        {
          if (this.c == '.')
            j = 1;
          else {
            this.oracle_sql.append(this.c);
          }
        }
        this.i += 1;
      }

      if (j != 0) {
        this.oracle_sql.append('\'');
      }
      this.oracle_sql.append(", 'YYYY-MM-DD HH24:MI:SS')");
    }
    else
    {
      this.oracle_sql.append("TO_TIMESTAMP (");
      skipSpace();
      handleODBC();
      this.oracle_sql.append(", 'YYYY-MM-DD HH24:MI:SS.FF')");
    }
  }

  void handleTime() throws SQLException
  {
    this.oracle_sql.append("TO_DATE (");
    skipSpace();
    handleODBC();
    this.oracle_sql.append(", 'HH24:MI:SS')");
  }

  void handleDate() throws SQLException
  {
    this.oracle_sql.append("TO_DATE (");
    skipSpace();
    handleODBC();
    this.oracle_sql.append(", 'YYYY-MM-DD')");
  }

  void handleEscape() throws SQLException
  {
    this.oracle_sql.append("ESCAPE ");
    skipSpace();
    handleODBC();
  }

  void handleScalarFunction() throws SQLException
  {
    this.token_buffer.delete(0, this.token_buffer.length());

    this.i += 1;

    skipSpace();

    while ((this.i < this.length) && ((Character.isJavaLetterOrDigit(this.c = this.odbc_sql.charAt(this.i))) || (this.c == '?')))
    {
      this.token_buffer.append(this.c);

      this.i += 1;
    }

    String str = this.token_buffer.substring(0, this.token_buffer.length()).toUpperCase().intern();

    if (str == "ABS") {
      usingFunctionName(str);
    } else if (str == "ACOS") {
      usingFunctionName(str);
    } else if (str == "ASIN") {
      usingFunctionName(str);
    } else if (str == "ATAN") {
      usingFunctionName(str);
    } else if (str == "ATAN2") {
      usingFunctionName(str);
    } else if (str == "CEILING") {
      usingFunctionName("CEIL");
    } else if (str == "COS") {
      usingFunctionName(str);
    } else if (str == "COT") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "DEGREES") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "EXP") {
      usingFunctionName(str);
    } else if (str == "FLOOR") {
      usingFunctionName(str);
    } else if (str == "LOG") {
      usingFunctionName("LN");
    } else if (str == "LOG10") {
      replacingFunctionPrefix("LOG ( 10, ");
    } else if (str == "MOD") {
      usingFunctionName(str);
    } else if (str == "PI") {
      replacingFunctionPrefix("( 3.141592653589793238462643383279502884197169399375 ");
    } else if (str == "POWER") {
      usingFunctionName(str);
    } else if (str == "RADIANS") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "RAND") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "ROUND") {
      usingFunctionName(str);
    } else if (str == "SIGN") {
      usingFunctionName(str);
    } else if (str == "SIN") {
      usingFunctionName(str);
    } else if (str == "SQRT") {
      usingFunctionName(str);
    } else if (str == "TAN") {
      usingFunctionName(str);
    } else if (str == "TRUNCATE") {
      usingFunctionName("TRUNC");
    }
    else if (str == "ASCII") {
      usingFunctionName(str);
    } else if (str == "CHAR") {
      usingFunctionName("CHR");
    } else if (str == "CONCAT") {
      usingFunctionName(str);
    } else if (str == "DIFFERENCE") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "INSERT") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "LCASE") {
      usingFunctionName("LOWER");
    } else if (str == "LEFT") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "LENGTH") {
      usingFunctionName(str);
    } else if (str == "LOCATE")
    {
      this.isLocate = true;

      usingFunctionName("INSTR");
    }
    else if (str == "LTRIM") {
      usingFunctionName(str);
    } else if (str == "REPEAT") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "REPLACE") {
      usingFunctionName(str);
    } else if (str == "RIGHT") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "RTRIM") {
      usingFunctionName(str);
    } else if (str == "SOUNDEX") {
      usingFunctionName(str);
    } else if (str == "SPACE") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "SUBSTRING") {
      usingFunctionName("SUBSTR");
    } else if (str == "UCASE") {
      usingFunctionName("UPPER");
    }
    else if (str == "CURDATE") {
      replacingFunctionPrefix("(CURRENT_DATE");
    } else if (str == "CURTIME") {
      replacingFunctionPrefix("(CURRENT_TIMESTAMP");
    } else if (str == "DAYNAME") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "DAYOFMONTH") {
      replacingFunctionPrefix("EXTRACT ( DAY FROM ");
    } else if (str == "DAYOFWEEK") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "DAYOFYEAR") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "HOUR") {
      replacingFunctionPrefix("EXTRACT ( HOUR FROM ");
    } else if (str == "MINUTE") {
      replacingFunctionPrefix("EXTRACT ( MINUTE FROM ");
    } else if (str == "MONTH") {
      replacingFunctionPrefix("EXTRACT ( MONTH FROM ");
    } else if (str == "MONTHNAME") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "NOW") {
      replacingFunctionPrefix("(CURRENT_TIMESTAMP");
    } else if (str == "QUARTER") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "SECOND") {
      replacingFunctionPrefix("EXTRACT ( SECOND FROM ");
    } else if (str == "TIMESTAMPADD") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "TIMESTAMPDIFF") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "WEEK") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "YEAR") {
      replacingFunctionPrefix("EXTRACT ( YEAR FROM ");
    }
    else if (str == "DATABASE") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "IFNULL") {
      DatabaseError.throwSqlException(34, str);
    }
    else if (str == "USER") {
      replacingFunctionPrefix("(USER");
    }
    else if (str == "CONVERT") {
      DatabaseError.throwSqlException(34, str);
    }
    else
    {
      DatabaseError.throwSqlException(34, str);
    }
  }

  void usingFunctionName(String paramString) throws SQLException
  {
    this.oracle_sql.append(paramString);
    skipSpace();
    handleODBC();
  }

  void replacingFunctionPrefix(String paramString) throws SQLException
  {
    skipSpace();

    if ((this.i < this.length) && ((this.c = this.odbc_sql.charAt(this.i)) == '('))
      this.i += 1;
    else {
      DatabaseError.throwSqlException(33);
    }
    this.oracle_sql.append(paramString);
    skipSpace();
    handleODBC();
  }

  void handleOuterJoin() throws SQLException
  {
    this.oracle_sql.append(" ( ");
    skipSpace();
    handleODBC();
    this.oracle_sql.append(" ) ");
  }

  String nextArgument()
  {
    String str = ":" + this.current_argument;

    this.current_argument += 1;

    return str;
  }

  void appendChar(StringBuffer paramStringBuffer, char paramChar)
  {
    if (paramChar == '?')
      paramStringBuffer.append(nextArgument());
    else
      paramStringBuffer.append(paramChar);
  }

  void skipSpace()
  {
    while ((this.i < this.length) && ((this.c = this.odbc_sql.charAt(this.i)) == ' '))
      this.i += 1;
  }

  String generateParameterName()
  {
    if ((this.parameterCount == 0) || (this.parameterList == null))
    {
      return "rowid" + this.paramSuffix++;
    }

    String str = "rowid" + this.paramSuffix++;
    for (int j = 0; ; j++) { if (j >= this.parameterList.length) break label109;
      if (str.equals(this.parameterList[j]))
        break; }
    label109: return str;
  }

  static boolean isValidPlsqlWarning(String paramString)
    throws SQLException
  {
    return paramString.matches("('\\s*([a-zA-Z0-9:,\\(\\)\\s])*')\\s*(,\\s*'([a-zA-Z0-9:,\\(\\)\\s])*')*");
  }

  public static boolean isValidObjectName(String paramString)
    throws SQLException
  {
    return paramString.matches("([a-zA-Z]{1}\\w*(\\$|\\#)*\\w*)|(\".*)");
  }

  public static void main(String[] paramArrayOfString)
  {
    String[] arrayOfString1 = { "IS_UNINITIALIZED", "IS_SELECT", "IS_PLSQL_BLOCK", "IS_DML", "IS_OTHER", "IS_CALL_BLOCK" };
    try
    {
      OracleSql localOracleSql = new OracleSql(null);

      boolean bool1 = paramArrayOfString[0].equals("true");
      boolean bool2 = paramArrayOfString[1].equals("true");

      localOracleSql.initialize(paramArrayOfString[2]);
      String str = localOracleSql.getSql(bool1, bool2);

      System.out.println(arrayOfString1[(localOracleSql.sqlKind + 1)] + ", " + localOracleSql.parameterCount);

      String[] arrayOfString2 = localOracleSql.getParameterList();
      int j;
      if (arrayOfString2 == EMPTY_LIST)
        System.out.println("parameterList is empty");
      else {
        for (j = 0; j < arrayOfString2.length; j++)
          System.out.println("parameterList[" + j + "] = " + arrayOfString2[j]);
      }
      if (localOracleSql.lastNcharLiteralLocation == 2) { System.out.println("No NCHAR literals");
      } else
      {
        System.out.println("NCHAR Literals");
        for (j = 1; j < localOracleSql.lastNcharLiteralLocation - 1; )
          System.out.println(str.substring(localOracleSql.ncharLiteralLocation[(j++)], localOracleSql.ncharLiteralLocation[(j++)]));
      }
      System.out.println("Keywords");
      if (localOracleSql.selectEndIndex == -1) System.out.println("no select"); else
        System.out.println("'" + str.substring(localOracleSql.selectEndIndex - 6, localOracleSql.selectEndIndex) + "'");
      if (localOracleSql.orderByStartIndex == -1) System.out.println("no order by"); else
        System.out.println("'" + str.substring(localOracleSql.orderByStartIndex, localOracleSql.orderByEndIndex) + "'");
      if (localOracleSql.whereStartIndex == -1) System.out.println("no where"); else
        System.out.println("'" + str.substring(localOracleSql.whereStartIndex, localOracleSql.whereEndIndex) + "'");
      if (localOracleSql.forUpdateStartIndex == -1) System.out.println("no for update"); else {
        System.out.println("'" + str.substring(localOracleSql.forUpdateStartIndex, localOracleSql.forUpdateEndIndex) + "'");
      }
      System.out.println("\"" + str + "\"");
      System.out.println("\"" + localOracleSql.getRevisedSql() + "\"");
    }
    catch (Exception localException)
    {
      localException.printStackTrace(System.out);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleSql
 * JD-Core Version:    0.6.0
 */