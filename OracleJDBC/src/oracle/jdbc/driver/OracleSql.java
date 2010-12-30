package oracle.jdbc.driver;

import java.io.PrintStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OracleSql {
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

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    OracleSql(DBConversion conv) {
        this.conversion = conv;
    }

    void initialize(String newSql) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleSql.initialize(" + newSql + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((newSql == null) || (newSql == "")) {
            DatabaseError.throwSqlException(104);
        }
        this.originalSql = newSql;
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

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleSql.initialize:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    String getOriginalSql() {
        return this.originalSql;
    }

    boolean setNamedParameters(int paramCount, String[] paramList) throws SQLException {
        boolean needToParse = false;

        if (paramCount == 0) {
            this.bindParameterCount = -1;
            needToParse = this.bindParameterCount != this.cachedBindParameterCount;
        } else {
            this.bindParameterCount = paramCount;
            this.bindParameterList = paramList;
            needToParse = this.bindParameterCount != this.cachedBindParameterCount;

            if (!needToParse) {
                for (int i = 0; i < paramCount; i++) {
                    if (this.bindParameterList[i] == this.cachedBindParameterList[i])
                        continue;
                    needToParse = true;

                    break;
                }
            }
            if (needToParse) {
                if (this.bindParameterCount != getParameterCount()) {
                    throw new SQLException("Incorrectly set or registered parameters.");
                }
                char[] sqlArray = this.originalSql.toCharArray();
                StringBuffer strBuf = new StringBuffer();

                int j = 0;

                for (int i = 0; i < sqlArray.length; i++) {
                    if (sqlArray[i] != '?') {
                        strBuf.append(sqlArray[i]);
                    } else {
                        strBuf.append(this.bindParameterList[(j++)]);
                        strBuf.append("=>?");
                    }
                }

                if (j != this.bindParameterCount) {
                    throw new SQLException("Incorrectly set or registered parameters.");
                }
                this.parameterSql = new String(strBuf);
                this.actualSql = null;
                this.utickSql = null;
                this.processedSql = null;
                this.rowidSql = null;
                this.sqlBytes = null;
            } else {
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

        return needToParse;
    }

    void resetNamedParameters() {
        this.cachedBindParameterCount = this.bindParameterCount;

        if (this.bindParameterCount != -1) {
            if ((this.cachedBindParameterList == null)
                    || (this.cachedBindParameterList == this.bindParameterList)
                    || (this.cachedBindParameterList.length < this.bindParameterCount)) {
                this.cachedBindParameterList = new String[this.bindParameterCount];
            }
            System.arraycopy(this.bindParameterList, 0, this.cachedBindParameterList, 0,
                             this.bindParameterCount);

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

    String getSql(boolean desiredProcessEscapes, boolean desiredConvertNcharLiterals)
            throws SQLException {
        if (this.sqlKind == -1)
            computeBasicInfo(this.parameterSql);
        if ((desiredProcessEscapes != this.currentProcessEscapes)
                || (desiredConvertNcharLiterals != this.currentConvertNcharLiterals)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER, "OracleSql.getSql(" + desiredProcessEscapes
                        + ", " + desiredConvertNcharLiterals + "): recomputing...", this);

                OracleLog.recursiveTrace = false;
            }

            if (desiredConvertNcharLiterals != this.currentConvertNcharLiterals)
                this.utickSql = null;
            this.processedSql = null;
            this.rowidSql = null;
            this.actualSql = null;
            this.sqlBytes = null;
        }

        this.currentConvertNcharLiterals = desiredConvertNcharLiterals;
        this.currentProcessEscapes = desiredProcessEscapes;

        if (this.actualSql == null) {
            if (this.utickSql == null)
                this.utickSql = (this.currentConvertNcharLiterals ? convertNcharLiterals(this.parameterSql)
                        : this.parameterSql);
            if (this.processedSql == null)
                this.processedSql = (this.currentProcessEscapes ? parse(this.utickSql)
                        : this.utickSql);
            if (this.rowidSql == null)
                this.rowidSql = (this.includeRowid ? addRowid(this.processedSql)
                        : this.processedSql);
            this.actualSql = this.rowidSql;
        }

        return this.actualSql;
    }

    String getRevisedSql() throws SQLException {
        String result = this.originalSql;

        if (this.sqlKind == -1)
            computeBasicInfo(this.parameterSql);

        result = removeForUpdate(result);

        return addRowid(result);
    }

    String removeForUpdate(String revisedSql) throws SQLException {
        if ((this.orderByStartIndex != -1)
                && ((this.forUpdateStartIndex == -1) || (this.forUpdateStartIndex > this.orderByStartIndex))) {
            revisedSql = revisedSql.substring(0, this.orderByStartIndex);
        } else if (this.forUpdateStartIndex != -1) {
            revisedSql = revisedSql.substring(0, this.forUpdateStartIndex);
        }

        return revisedSql;
    }

    void appendForUpdate(StringBuffer stringBuffer) throws SQLException {
        if ((this.orderByStartIndex != -1)
                && ((this.forUpdateStartIndex == -1) || (this.forUpdateStartIndex > this.orderByStartIndex))) {
            stringBuffer.append(this.originalSql.substring(this.orderByStartIndex));
        } else if (this.forUpdateStartIndex != -1) {
            stringBuffer.append(this.originalSql.substring(this.forUpdateStartIndex));
        }
    }

    String getInsertSqlForUpdatableResultSet(UpdatableResultSet updatableResultSet)
            throws SQLException {
        String originalSql = getOriginalSql();

        if (this.stringBufferForScrollableStatement == null)
            this.stringBufferForScrollableStatement = new StringBuffer(originalSql.length() + 100);
        else {
            this.stringBufferForScrollableStatement.delete(0,
                                                           this.stringBufferForScrollableStatement
                                                                   .length());
        }
        this.stringBufferForScrollableStatement.append("insert into (");

        this.stringBufferForScrollableStatement.append(removeForUpdate(originalSql));
        this.stringBufferForScrollableStatement.append(") values ( ");

        int i = 1;
        while (i < updatableResultSet.getColumnCount()) {
            if (i != 1) {
                this.stringBufferForScrollableStatement.append(", ");
            }
            this.stringBufferForScrollableStatement.append(":" + generateParameterName());

            i++;
        }

        this.stringBufferForScrollableStatement.append(")");

        return this.stringBufferForScrollableStatement
                .substring(0, this.stringBufferForScrollableStatement.length());
    }

    String getRefetchSqlForScrollableResultSet(ScrollableResultSet scrollableResultSet,
            int realRefreshSize) throws SQLException {
        String revisedSql = getRevisedSql();

        if (this.stringBufferForScrollableStatement == null)
            this.stringBufferForScrollableStatement = new StringBuffer(revisedSql.length() + 100);
        else {
            this.stringBufferForScrollableStatement.delete(0,
                                                           this.stringBufferForScrollableStatement
                                                                   .length());
        }

        this.stringBufferForScrollableStatement.append(revisedSql);

        this.stringBufferForScrollableStatement.append(" WHERE ( ROWID = :"
                + generateParameterName());

        for (int i = 0; i < realRefreshSize - 1; i++) {
            this.stringBufferForScrollableStatement.append(" OR ROWID = :"
                    + generateParameterName());
        }
        this.stringBufferForScrollableStatement.append(" ) ");

        appendForUpdate(this.stringBufferForScrollableStatement);

        return this.stringBufferForScrollableStatement
                .substring(0, this.stringBufferForScrollableStatement.length());
    }

    String getUpdateSqlForUpdatableResultSet(UpdatableResultSet updatableResultSet,
            int numberOfColumnsChanged, Object[] rowBuffer, int[] indexColsChanged)
            throws SQLException {
        String revisedSql = getRevisedSql();

        if (this.stringBufferForScrollableStatement == null)
            this.stringBufferForScrollableStatement = new StringBuffer(revisedSql.length() + 100);
        else {
            this.stringBufferForScrollableStatement.delete(0,
                                                           this.stringBufferForScrollableStatement
                                                                   .length());
        }

        this.stringBufferForScrollableStatement.append("update (");
        this.stringBufferForScrollableStatement.append(revisedSql);
        this.stringBufferForScrollableStatement.append(") set ");

        if (rowBuffer != null) {
            for (int i = 0; i < numberOfColumnsChanged; i++) {
                if (i > 0) {
                    this.stringBufferForScrollableStatement.append(", ");
                }
                this.stringBufferForScrollableStatement.append(updatableResultSet
                        .getInternalMetadata().getColumnName(indexColsChanged[i] + 1));

                this.stringBufferForScrollableStatement.append(" = :" + generateParameterName());
            }

        }

        this.stringBufferForScrollableStatement.append(" WHERE ");
        this.stringBufferForScrollableStatement.append(" ROWID = :" + generateParameterName());

        return this.stringBufferForScrollableStatement
                .substring(0, this.stringBufferForScrollableStatement.length());
    }

    String getDeleteSqlForUpdatableResultSet(UpdatableResultSet updatableResultSet)
            throws SQLException {
        String revisedSql = getRevisedSql();

        if (this.stringBufferForScrollableStatement == null)
            this.stringBufferForScrollableStatement = new StringBuffer(revisedSql.length() + 100);
        else {
            this.stringBufferForScrollableStatement.delete(0,
                                                           this.stringBufferForScrollableStatement
                                                                   .length());
        }

        this.stringBufferForScrollableStatement.append("delete from (");
        this.stringBufferForScrollableStatement.append(revisedSql);
        this.stringBufferForScrollableStatement.append(") where ");

        this.stringBufferForScrollableStatement.append(" ROWID = :" + generateParameterName());

        return this.stringBufferForScrollableStatement
                .substring(0, this.stringBufferForScrollableStatement.length());
    }

    byte[] getSqlBytes(boolean desiredProcessEscapes, boolean desiredConvertNcharLiterals)
            throws SQLException {
        if ((this.sqlBytes == null) || (desiredProcessEscapes != this.currentProcessEscapes)) {
            this.sqlBytes = this.conversion.StringToCharBytes(getSql(desiredProcessEscapes,
                                                                     desiredConvertNcharLiterals));
        }

        return this.sqlBytes;
    }

    byte getSqlKind() throws SQLException {
        if (this.sqlKind == -1) {
            computeBasicInfo(this.parameterSql);
        }
        return this.sqlKind;
    }

    int getParameterCount() throws SQLException {
        if (this.parameterCount == -1) {
            computeBasicInfo(this.parameterSql);
        }

        return this.parameterCount;
    }

    String[] getParameterList() throws SQLException {
        if (this.parameterCount == -1) {
            computeBasicInfo(this.parameterSql);
        }

        return this.parameterList;
    }

    void setIncludeRowid(boolean enable) {
        if (enable != this.includeRowid) {
            this.includeRowid = enable;
            this.rowidSql = null;
            this.actualSql = null;
            this.sqlBytes = null;
        }
    }

    public String toString() {
        return this.parameterSql == null ? "null" : this.parameterSql;
    }

    private String hexUnicode(int c) throws SQLException {
        String hex = Integer.toHexString(c);
        switch (hex.length()) {
        case 0:
            return "\\0000";
        case 1:
            return "\\000" + hex;
        case 2:
            return "\\00" + hex;
        case 3:
            return "\\0" + hex;
        case 4:
            return "\\" + hex;
        }
        DatabaseError.throwSqlException(89, "Unexpected case in OracleSql.hexUnicode: " + c);

        return "never happen";
    }

    String convertNcharLiterals(String sql) throws SQLException {
        if (this.lastNcharLiteralLocation <= 2)
            return sql;
        String buf = "";
        int i = 0;
        while (true) {
            int end = this.ncharLiteralLocation[(i++)];
            int start = this.ncharLiteralLocation[(i++)];

            buf = buf + sql.substring(end, start);
            if (i >= this.lastNcharLiteralLocation)
                break;
            end = this.ncharLiteralLocation[i];
            buf = buf + "u'";

            for (int j = start + 2; j < end; j++) {
                char c = sql.charAt(j);
                if (c == '\\')
                    buf = buf + "\\\\";
                else if (c < '')
                    buf = buf + c;
                else
                    buf = buf + hexUnicode(c);
            }
        }
        return buf;
    }

    void computeBasicInfo(String sql) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleSql.computeBasicInfo(" + sql + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.parameterCount = 0;

        this.lastNcharLiteralLocation = 0;
        this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = 0;

        int currentParameterLength = 0;

        int s = 0;
        int len = sql.length();
        int temp_orderByStartIndex = -1;
        int temp_forUpdateStartIndex = -1;
        int stop = len + 1;

        for (int i = 0; i < stop; i++) {
            char unicodeChar = i < len ? sql.charAt(i) : ' ';
            char c = unicodeChar;

            if (unicodeChar > '') {
                if (Character.isLetterOrDigit(unicodeChar))
                    c = 'X';
                else {
                    c = ' ';
                }

            }

            switch (ACTION[s][c]) {
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
                this.selectEndIndex = i;

                break;
            case 5:
                this.sqlKind = 3;

                break;
            case 6:
                this.whereStartIndex = (i - 5);
                this.whereEndIndex = i;

                break;
            case 7:
                temp_orderByStartIndex = i - 5;

                break;
            case 8:
                this.orderByStartIndex = temp_orderByStartIndex;
                this.orderByEndIndex = i;

                break;
            case 9:
                temp_forUpdateStartIndex = i - 3;

                break;
            case 10:
                this.forUpdateStartIndex = temp_forUpdateStartIndex;
                this.forUpdateEndIndex = i;

                break;
            case 11:
                this.parameterCount += 1;

                break;
            case 12:
                if (this.currentParameter == null) {
                    this.currentParameter = new char[32];
                }
                if (currentParameterLength >= this.currentParameter.length) {
                    DatabaseError.throwSqlException(134, new String(this.currentParameter));
                }

                this.currentParameter[(currentParameterLength++)] = unicodeChar;

                break;
            case 13:
                if (currentParameterLength <= 0)
                    break;
                if (this.parameterList == EMPTY_LIST) {
                    this.parameterList = new String[8];
                } else if (this.parameterList.length <= this.parameterCount) {
                    String[] newList = new String[this.parameterList.length * 4];

                    System.arraycopy(this.parameterList, 0, newList, 0, this.parameterList.length);

                    this.parameterList = newList;
                }

                this.parameterList[this.parameterCount] = new String(this.currentParameter, 0,
                        currentParameterLength).intern();

                currentParameterLength = 0;
                this.parameterCount += 1;
                break;
            case 14:
                this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = (i - 1);

                break;
            case 15:
                this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = (i + 1);
            }

            s = TRANSITION[s][c];
        }

        this.ncharLiteralLocation[(this.lastNcharLiteralLocation++)] = len;
        this.ncharLiteralLocation[this.lastNcharLiteralLocation] = len;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleSql.computeBasicInfo:return: sqlKind = "
                    + this.sqlKind + " parameterCount = " + this.parameterCount, this);

            OracleLog.recursiveTrace = false;
        }
    }

    private String addRowid(String sql) throws SQLException {
        if (this.selectEndIndex == -1) {
            DatabaseError.throwSqlException(88);
        }
        String result = "select rowid," + sql.substring(this.selectEndIndex);

        return result;
    }

    String parse(String os) throws SQLException {
        this.current_argument = 1;
        this.i = 0;
        this.first = true;
        this.in_string = false;
        this.odbc_sql = os;
        this.length = this.odbc_sql.length();

        if (this.oracle_sql == null) {
            this.oracle_sql = new StringBuffer(this.length);
            this.token_buffer = new StringBuffer(32);
        } else {
            this.oracle_sql.ensureCapacity(this.length);
        }

        this.oracle_sql.delete(0, this.oracle_sql.length());
        skipSpace();
        handleODBC();

        if (this.i < this.length) {
            Integer index = new Integer(this.i);

            DatabaseError.throwSqlException(33, index);
        }

        return this.oracle_sql.substring(0, this.oracle_sql.length());
    }

    void handleODBC() throws SQLException {
        StringBuffer arg1 = null;
        StringBuffer arg2 = null;
        boolean arg2Start = false;

        while (this.i < this.length) {
            this.c = this.odbc_sql.charAt(this.i);

            if (this.in_string) {
                this.oracle_sql.append(this.c);

                if (this.c == '\'') {
                    this.in_string = false;
                }
                this.i += 1;
                continue;
            }

            switch (this.c) {
            case '\'':
                if (this.isLocate) {
                    if (arg1 == null) {
                        arg1 = new StringBuffer();
                        arg2 = new StringBuffer();
                    }

                    if (!arg2Start)
                        arg1.append(this.c);
                    else {
                        arg2.append(this.c);
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

                while ((this.i < this.length)
                        && ((Character.isJavaLetterOrDigit(this.c = this.odbc_sql.charAt(this.i))) || (this.c == '?'))) {
                    this.token_buffer.append(this.c);

                    this.i += 1;
                }

                handleToken(this.token_buffer.substring(0, this.token_buffer.length()));

                this.c = this.odbc_sql.charAt(this.i);

                if (this.c != '}') {
                    String s = new String(this.i + ": Expecting \"}\" got \"" + this.c + "\"");

                    DatabaseError.throwSqlException(33, s);
                }

                this.i += 1;

                break;
            case '}':
                return;
            default:
                if ((this.c != ' ') && (this.c != '(') && (this.isLocate)) {
                    if (this.c == ')') {
                        if (arg2.substring(0, arg2.length()).trim().equals("?"))
                            this.oracle_sql.append(nextArgument());
                        else {
                            this.oracle_sql.append(arg2);
                        }
                        this.oracle_sql.append(", ");

                        if (arg1.substring(0, arg1.length()).trim().equals("?"))
                            this.oracle_sql.append(nextArgument());
                        else {
                            this.oracle_sql.append(arg1);
                        }
                        appendChar(this.oracle_sql, this.c);

                        this.isLocate = false;
                    }

                    if (arg1 == null) {
                        arg1 = new StringBuffer();
                        arg2 = new StringBuffer();
                    }

                    if (this.c == ',') {
                        arg2Start = true;
                        this.i += 1;
                        this.first = false;

                        continue;
                    }

                    if (!arg2Start)
                        arg1.append(this.c);
                    else {
                        arg2.append(this.c);
                    }
                } else {
                    appendChar(this.oracle_sql, this.c);
                }

                this.i += 1;
                this.first = false;
            }
        }
    }

    void handleToken(String token) throws SQLException {
        if (token.equalsIgnoreCase("?")) {
            handleFunction();
        } else if (token.equalsIgnoreCase("call")) {
            handleCall();
        } else if (token.equalsIgnoreCase("ts")) {
            handleTimestamp();
        } else if (token.equalsIgnoreCase("t")) {
            handleTime();
        } else if (token.equalsIgnoreCase("d")) {
            handleDate();
        } else if (token.equalsIgnoreCase("escape")) {
            handleEscape();
        } else if (token.equalsIgnoreCase("fn")) {
            handleScalarFunction();
        } else if (token.equalsIgnoreCase("oj")) {
            handleOuterJoin();
        } else {
            String s = new String(this.i + ": " + token);

            DatabaseError.throwSqlException(34, s);
        }
    }

    void handleFunction() throws SQLException {
        boolean need_block = this.first;

        if (need_block) {
            this.oracle_sql.append("BEGIN ");
        }
        appendChar(this.oracle_sql, '?');
        skipSpace();

        if (this.c != '=') {
            String s = new String(this.i + ". Expecting \"=\" got \"" + this.c + "\"");

            DatabaseError.throwSqlException(33, s);
        }

        this.i += 1;

        skipSpace();

        if (!this.odbc_sql.startsWith("call", this.i)) {
            String s = new String(this.i + ". Expecting \"call\"");

            DatabaseError.throwSqlException(33, s);
        }

        this.i += 4;

        this.oracle_sql.append(" := ");
        skipSpace();
        handleODBC();

        if (need_block)
            this.oracle_sql.append("; END;");
    }

    void handleCall() throws SQLException {
        boolean need_block = this.first;

        if (need_block) {
            this.oracle_sql.append("BEGIN ");
        }
        skipSpace();
        handleODBC();
        skipSpace();

        if (need_block)
            this.oracle_sql.append("; END;");
    }

    void handleTimestamp() throws SQLException {
        if (this.isV8Compatible) {
            this.oracle_sql.append("TO_DATE (");
            skipSpace();

            boolean in_nanos = false;

            while ((this.i < this.length) && ((this.c = this.odbc_sql.charAt(this.i)) != '}')) {
                if (!in_nanos) {
                    if (this.c == '.')
                        in_nanos = true;
                    else {
                        this.oracle_sql.append(this.c);
                    }
                }
                this.i += 1;
            }

            if (in_nanos) {
                this.oracle_sql.append('\'');
            }
            this.oracle_sql.append(", 'YYYY-MM-DD HH24:MI:SS')");
        } else {
            this.oracle_sql.append("TO_TIMESTAMP (");
            skipSpace();
            handleODBC();
            this.oracle_sql.append(", 'YYYY-MM-DD HH24:MI:SS.FF')");
        }
    }

    void handleTime() throws SQLException {
        this.oracle_sql.append("TO_DATE (");
        skipSpace();
        handleODBC();
        this.oracle_sql.append(", 'HH24:MI:SS')");
    }

    void handleDate() throws SQLException {
        this.oracle_sql.append("TO_DATE (");
        skipSpace();
        handleODBC();
        this.oracle_sql.append(", 'YYYY-MM-DD')");
    }

    void handleEscape() throws SQLException {
        this.oracle_sql.append("ESCAPE ");
        skipSpace();
        handleODBC();
    }

    void handleScalarFunction() throws SQLException {
        this.token_buffer.delete(0, this.token_buffer.length());

        this.i += 1;

        skipSpace();

        while ((this.i < this.length)
                && ((Character.isJavaLetterOrDigit(this.c = this.odbc_sql.charAt(this.i))) || (this.c == '?'))) {
            this.token_buffer.append(this.c);

            this.i += 1;
        }

        String functionName = this.token_buffer.substring(0, this.token_buffer.length())
                .toUpperCase().intern();

        if (functionName == "ABS") {
            usingFunctionName(functionName);
        } else if (functionName == "ACOS") {
            usingFunctionName(functionName);
        } else if (functionName == "ASIN") {
            usingFunctionName(functionName);
        } else if (functionName == "ATAN") {
            usingFunctionName(functionName);
        } else if (functionName == "ATAN2") {
            usingFunctionName(functionName);
        } else if (functionName == "CEILING") {
            usingFunctionName("CEIL");
        } else if (functionName == "COS") {
            usingFunctionName(functionName);
        } else if (functionName == "COT") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "DEGREES") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "EXP") {
            usingFunctionName(functionName);
        } else if (functionName == "FLOOR") {
            usingFunctionName(functionName);
        } else if (functionName == "LOG") {
            usingFunctionName("LN");
        } else if (functionName == "LOG10") {
            replacingFunctionPrefix("LOG ( 10, ");
        } else if (functionName == "MOD") {
            usingFunctionName(functionName);
        } else if (functionName == "PI") {
            replacingFunctionPrefix("( 3.141592653589793238462643383279502884197169399375 ");
        } else if (functionName == "POWER") {
            usingFunctionName(functionName);
        } else if (functionName == "RADIANS") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "RAND") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "ROUND") {
            usingFunctionName(functionName);
        } else if (functionName == "SIGN") {
            usingFunctionName(functionName);
        } else if (functionName == "SIN") {
            usingFunctionName(functionName);
        } else if (functionName == "SQRT") {
            usingFunctionName(functionName);
        } else if (functionName == "TAN") {
            usingFunctionName(functionName);
        } else if (functionName == "TRUNCATE") {
            usingFunctionName("TRUNC");
        } else if (functionName == "ASCII") {
            usingFunctionName(functionName);
        } else if (functionName == "CHAR") {
            usingFunctionName("CHR");
        } else if (functionName == "CONCAT") {
            usingFunctionName(functionName);
        } else if (functionName == "DIFFERENCE") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "INSERT") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "LCASE") {
            usingFunctionName("LOWER");
        } else if (functionName == "LEFT") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "LENGTH") {
            usingFunctionName(functionName);
        } else if (functionName == "LOCATE") {
            this.isLocate = true;

            usingFunctionName("INSTR");
        } else if (functionName == "LTRIM") {
            usingFunctionName(functionName);
        } else if (functionName == "REPEAT") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "REPLACE") {
            usingFunctionName(functionName);
        } else if (functionName == "RIGHT") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "RTRIM") {
            usingFunctionName(functionName);
        } else if (functionName == "SOUNDEX") {
            usingFunctionName(functionName);
        } else if (functionName == "SPACE") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "SUBSTRING") {
            usingFunctionName("SUBSTR");
        } else if (functionName == "UCASE") {
            usingFunctionName("UPPER");
        } else if (functionName == "CURDATE") {
            replacingFunctionPrefix("(CURRENT_DATE");
        } else if (functionName == "CURTIME") {
            replacingFunctionPrefix("(CURRENT_TIMESTAMP");
        } else if (functionName == "DAYNAME") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "DAYOFMONTH") {
            replacingFunctionPrefix("EXTRACT ( DAY FROM ");
        } else if (functionName == "DAYOFWEEK") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "DAYOFYEAR") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "HOUR") {
            replacingFunctionPrefix("EXTRACT ( HOUR FROM ");
        } else if (functionName == "MINUTE") {
            replacingFunctionPrefix("EXTRACT ( MINUTE FROM ");
        } else if (functionName == "MONTH") {
            replacingFunctionPrefix("EXTRACT ( MONTH FROM ");
        } else if (functionName == "MONTHNAME") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "NOW") {
            replacingFunctionPrefix("(CURRENT_TIMESTAMP");
        } else if (functionName == "QUARTER") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "SECOND") {
            replacingFunctionPrefix("EXTRACT ( SECOND FROM ");
        } else if (functionName == "TIMESTAMPADD") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "TIMESTAMPDIFF") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "WEEK") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "YEAR") {
            replacingFunctionPrefix("EXTRACT ( YEAR FROM ");
        } else if (functionName == "DATABASE") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "IFNULL") {
            DatabaseError.throwSqlException(34, functionName);
        } else if (functionName == "USER") {
            replacingFunctionPrefix("(USER");
        } else if (functionName == "CONVERT") {
            DatabaseError.throwSqlException(34, functionName);
        } else {
            DatabaseError.throwSqlException(34, functionName);
        }
    }

    void usingFunctionName(String newName) throws SQLException {
        this.oracle_sql.append(newName);
        skipSpace();
        handleODBC();
    }

    void replacingFunctionPrefix(String newPrefix) throws SQLException {
        skipSpace();

        if ((this.i < this.length) && ((this.c = this.odbc_sql.charAt(this.i)) == '('))
            this.i += 1;
        else {
            DatabaseError.throwSqlException(33);
        }
        this.oracle_sql.append(newPrefix);
        skipSpace();
        handleODBC();
    }

    void handleOuterJoin() throws SQLException {
        this.oracle_sql.append(" ( ");
        skipSpace();
        handleODBC();
        this.oracle_sql.append(" ) ");
    }

    String nextArgument() {
        String result = ":" + this.current_argument;

        this.current_argument += 1;

        return result;
    }

    void appendChar(StringBuffer oracle_sql, char c) {
        if (c == '?')
            oracle_sql.append(nextArgument());
        else
            oracle_sql.append(c);
    }

    void skipSpace() {
        while ((this.i < this.length) && ((this.c = this.odbc_sql.charAt(this.i)) == ' '))
            this.i += 1;
    }

    String generateParameterName() {
        if ((this.parameterCount == 0) || (this.parameterList == null)) {
            return "rowid" + this.paramSuffix++;
        }

        String newParameter = "rowid" + this.paramSuffix++;
        label109: for (int i = 0;; i++) {
            if (i >= this.parameterList.length)
                break label109;
            if (newParameter.equals(this.parameterList[i]))
                break;
        }
        return newParameter;
    }

    static boolean isValidPlsqlWarning(String setting) throws SQLException {
        return setting
                .matches("('\\s*([a-zA-Z0-9:,\\(\\)\\s])*')\\s*(,\\s*'([a-zA-Z0-9:,\\(\\)\\s])*')*");
    }

    public static boolean isValidObjectName(String name) throws SQLException {
        return name.matches("([a-zA-Z]{1}\\w*(\\$|\\#)*\\w*)|(\".*)");
    }

    public static void main(String[] args) {
        String[] sqlKindStrings = { "IS_UNINITIALIZED", "IS_SELECT", "IS_PLSQL_BLOCK", "IS_DML",
                "IS_OTHER", "IS_CALL_BLOCK" };
        try {
            OracleSql o = new OracleSql(null);

            boolean escapes = args[0].equals("true");
            boolean nchar = args[1].equals("true");

            o.initialize(args[2]);
            String sql = o.getSql(escapes, nchar);

            System.out.println(sqlKindStrings[(o.sqlKind + 1)] + ", " + o.parameterCount);

            String[] p = o.getParameterList();

            if (p == EMPTY_LIST)
                System.out.println("parameterList is empty");
            else
                for (int i = 0; i < p.length; i++)
                    System.out.println("parameterList[" + i + "] = " + p[i]);
            int i;
            if (o.lastNcharLiteralLocation == 2) {
                System.out.println("No NCHAR literals");
            } else {
                System.out.println("NCHAR Literals");
                for (i = 1; i < o.lastNcharLiteralLocation - 1;)
                    System.out.println(sql.substring(o.ncharLiteralLocation[(i++)],
                                                     o.ncharLiteralLocation[(i++)]));
            }
            System.out.println("Keywords");
            if (o.selectEndIndex == -1)
                System.out.println("no select");
            else
                System.out.println("'" + sql.substring(o.selectEndIndex - 6, o.selectEndIndex)
                        + "'");
            if (o.orderByStartIndex == -1)
                System.out.println("no order by");
            else
                System.out.println("'" + sql.substring(o.orderByStartIndex, o.orderByEndIndex)
                        + "'");
            if (o.whereStartIndex == -1)
                System.out.println("no where");
            else
                System.out.println("'" + sql.substring(o.whereStartIndex, o.whereEndIndex) + "'");
            if (o.forUpdateStartIndex == -1)
                System.out.println("no for update");
            else {
                System.out.println("'" + sql.substring(o.forUpdateStartIndex, o.forUpdateEndIndex)
                        + "'");
            }
            System.out.println("\"" + sql + "\"");
            System.out.println("\"" + o.getRevisedSql() + "\"");
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleSql"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleSql JD-Core Version: 0.6.0
 */