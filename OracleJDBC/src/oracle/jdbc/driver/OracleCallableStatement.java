// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: OracleCallableStatement.java

package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BINARY_DOUBLE;
import oracle.sql.BINARY_FLOAT;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

// Referenced classes of package oracle.jdbc.driver:
// OraclePreparedStatement, Accessor, PlsqlIndexTableAccessor, OracleLog,
// DatabaseError, OracleSql, PhysicalConnection

public abstract class OracleCallableStatement extends OraclePreparedStatement implements
        oracle.jdbc.internal.OracleCallableStatement {

    boolean atLeastOneOrdinalParameter;
    boolean atLeastOneNamedParameter;
    String namedParameters[];
    int parameterCount;
    final String errMsgMixedBind = "Ordinal binding and Named binding cannot be combined!";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    OracleCallableStatement(PhysicalConnection conn, String sql, int batchValue,
            int rowPrefetchValue) throws SQLException {
        this(conn, sql, batchValue, rowPrefetchValue, 1003, 1007);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.OracleCallableStatement(conn, sql,batchValue, rowPrefetchValue)",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    OracleCallableStatement(PhysicalConnection conn, String sql, int batch_value,
            int row_prefetch_value, int UserResultSetType, int UserResultSetConcur)
            throws SQLException {
        super(conn, sql, 1, row_prefetch_value, UserResultSetType, UserResultSetConcur);
        atLeastOneOrdinalParameter = false;
        atLeastOneNamedParameter = false;
        namedParameters = new String[8];
        parameterCount = 0;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.OracleCallableStatement(conn, sql, batchValue, rowPrefetchValue, UserResultSetType="
                                 + UserResultSetType + ", UserResultSetConcur)", this);
            OracleLog.recursiveTrace = false;
        }
        statementType = 2;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleCallableStatement.OracleCallableStatement:return",
                                       this);
            OracleLog.recursiveTrace = false;
        }
    }

    void registerOutParameterInternal(int paramIndex, int external_type, int scale, int maxLength,
            String sqlName) throws SQLException {
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        if (external_type == 0) {
            DatabaseError.throwSqlException(4);
        }
        int internal_type = getInternalType(external_type);
        resetBatch();
        currentRowNeedToPrepareBinds = true;
        if (currentRowBindAccessors == null) {
            currentRowBindAccessors = new Accessor[numberOfBindPositions];
        }
        currentRowBindAccessors[index] = allocateAccessor(internal_type, external_type, index + 1,
                                                          maxLength, currentRowFormOfUse[index],
                                                          sqlName, true);
    }

    public void registerOutParameter(int paramIndex, int sqlType, String sqlName)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleCallableStatement.registerOutParameter(paramIndex="
                                               + paramIndex + ", sqlType=" + sqlType + ", sqlName="
                                               + sqlName + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (sqlName == null || sqlName.length() == 0) {
            DatabaseError.throwSqlException(60, "empty Object name");
        }
        synchronized (connection) {
            synchronized (this) {
                registerOutParameterInternal(paramIndex, sqlType, 0, 0, sqlName);
            }
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.registerOutParameter(paramIndex, sqlType, sqlName):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    /**
     * @deprecated Method registerOutParameterBytes is deprecated
     */

    public synchronized void registerOutParameterBytes(int paramIndex, int sqlType, int scale,
            int maxLength) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleCallableStatement.registerOutParameterBytes(paramIndex="
                                               + paramIndex + ", sqlType=" + sqlType + ", scale="
                                               + scale + ", maxLength=" + maxLength + ")", this);
            OracleLog.recursiveTrace = false;
        }
        registerOutParameterInternal(paramIndex, sqlType, scale, maxLength, null);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.registerOutParameterBytes(paramIndex, sqlType, scale, maxLength):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    /**
     * @deprecated Method registerOutParameterChars is deprecated
     */

    public synchronized void registerOutParameterChars(int paramIndex, int sqlType, int scale,
            int maxLength) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleCallableStatement.registerOutParameterChars(paramIndex="
                                               + paramIndex + ", sqlType=" + sqlType + ", scale="
                                               + scale + ", maxLength=" + maxLength + ")", this);
            OracleLog.recursiveTrace = false;
        }
        registerOutParameterInternal(paramIndex, sqlType, scale, maxLength, null);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.registerOutParameterChars(paramIndex, sqlType, scale, maxLength):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void registerOutParameter(int paramIndex, int sqlType, int scale,
            int maxLength) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleCallableStatement.registerOutParameter(paramIndex="
                                               + paramIndex + ", sqlType=" + sqlType + ", scale="
                                               + scale + ", maxLength=" + maxLength + ")", this);
            OracleLog.recursiveTrace = false;
        }
        registerOutParameterInternal(paramIndex, sqlType, scale, maxLength, null);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.registerOutParameter(paramIndex, sqlType, scale, maxLength):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void registerOutParameter(String parameterName, int sqlType, int scale,
            int maxLength) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleCallableStatement.registerOutParameter(parameterName="
                                               + parameterName + ", sqlType=" + sqlType
                                               + ", scale=" + scale + ", maxLength=" + maxLength
                                               + ")", this);
            OracleLog.recursiveTrace = false;
        }
        int index = addNamedPara(parameterName);
        registerOutParameter(index, sqlType, scale, maxLength);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.registerOutParameter(parameterName, sqlType, scale, maxLength):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    void resetBatch() {
        batch = 1;
    }

    public synchronized void setExecuteBatch(int nrows) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.setExecuteBatch(nrows="
                    + nrows + ")", this);
            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized int sendBatch() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.sendBatch()", this);
            OracleLog.recursiveTrace = false;
        }
        return validRows;
    }

    public void registerOutParameter(int paramIndex, int sqlType) throws SQLException {
        registerOutParameter(paramIndex, sqlType, 0, -1);
    }

    public void registerOutParameter(int paramIndex, int sqlType, int scale) throws SQLException {
        registerOutParameter(paramIndex, sqlType, scale, -1);
    }

    public boolean wasNull() throws SQLException {
        return wasNullValue();
    }

    public String getString(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getString("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getString(currentRank);
    }

    public Datum getOracleObject(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getOracleObject("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getOracleObject(currentRank);
    }

    public ROWID getROWID(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getROWID("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getROWID(currentRank);
    }

    public NUMBER getNUMBER(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getNUMBER("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getNUMBER(currentRank);
    }

    public DATE getDATE(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getDATE("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDATE(currentRank);
    }

    public INTERVALYM getINTERVALYM(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getINTERVALYM("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getINTERVALYM(currentRank);
    }

    public INTERVALDS getINTERVALDS(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getINTERVALDS("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getINTERVALDS(currentRank);
    }

    public TIMESTAMP getTIMESTAMP(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTIMESTAMP("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTIMESTAMP(currentRank);
    }

    public TIMESTAMPTZ getTIMESTAMPTZ(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTIMESTAMPTZ("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTIMESTAMPTZ(currentRank);
    }

    public TIMESTAMPLTZ getTIMESTAMPLTZ(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTIMESTAMPTLTZ("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTIMESTAMPLTZ(currentRank);
    }

    public REF getREF(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getREF("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getREF(currentRank);
    }

    public ARRAY getARRAY(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getARRAY("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getARRAY(currentRank);
    }

    public STRUCT getSTRUCT(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getSTRUCT("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getSTRUCT(currentRank);
    }

    public OPAQUE getOPAQUE(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getOPAQUE("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getOPAQUE(currentRank);
    }

    public CHAR getCHAR(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getCHAR("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCHAR(currentRank);
    }

    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getCharacterStream("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCharacterStream(currentRank);
    }

    public RAW getRAW(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getRAW("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getRAW(currentRank);
    }

    public BLOB getBLOB(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBLOB("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBLOB(currentRank);
    }

    public CLOB getCLOB(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getCLOB("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCLOB(currentRank);
    }

    public BFILE getBFILE(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBFILE("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBFILE(currentRank);
    }

    public boolean getBoolean(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBoolean("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBoolean(currentRank);
    }

    public byte getByte(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getByte("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getByte(currentRank);
    }

    public short getShort(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getShort("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getShort(currentRank);
    }

    public int getInt(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getInt("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getInt(currentRank);
    }

    public long getLong(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getLong("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getLong(currentRank);
    }

    public float getFloat(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getFloat("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getFloat(currentRank);
    }

    public double getDouble(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getDouble("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDouble(currentRank);
    }

    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBigDecimal("
                    + parameterIndex + ", " + scale + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBigDecimal(currentRank);
    }

    public byte[] getBytes(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBytes("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBytes(currentRank);
    }

    public byte[] privateGetBytes(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBytes("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.privateGetBytes(currentRank);
    }

    public Date getDate(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getDate("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDate(currentRank);
    }

    public Time getTime(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTime("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTime(currentRank);
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTimestamp("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTimestamp(currentRank);
    }

    public InputStream getAsciiStream(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getAsciiStream("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getAsciiStream(currentRank);
    }

    public InputStream getUnicodeStream(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getUnicodeStream("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getUnicodeStream(currentRank);
    }

    public InputStream getBinaryStream(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBinaryStream("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBinaryStream(currentRank);
    }

    public Object getObject(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getObject("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getObject(currentRank);
    }

    public Object getAnyDataEmbeddedObject(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleCallableStatement.getAnyDataEmbeddedObject("
                                               + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getAnyDataEmbeddedObject(currentRank);
    }

    public Object getCustomDatum(int parameterIndex, CustomDatumFactory factory)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getCustomDatum("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCustomDatum(currentRank, factory);
    }

    public Object getORAData(int parameterIndex, ORADataFactory factory) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getORAData("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getORAData(currentRank, factory);
    }

    public ResultSet getCursor(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getCursor("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCursor(currentRank);
    }

    public synchronized void clearParameters() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.clearParameters()",
                                       this);
            OracleLog.recursiveTrace = false;
        }
        super.clearParameters();
    }

    public Object getObject(int parameterIndex, Map map) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getObject("
                    + parameterIndex + ", " + map + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getObject(currentRank, map);
    }

    public Ref getRef(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getRef("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getREF(currentRank);
    }

    public Blob getBlob(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBlob("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBLOB(currentRank);
    }

    public Clob getClob(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getClob("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCLOB(currentRank);
    }

    public Array getArray(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getArray("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getARRAY(currentRank);
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getBigDecimal("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBigDecimal(currentRank);
    }

    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getDate("
                    + parameterIndex + ", " + cal + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDate(currentRank, cal);
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTime("
                    + parameterIndex + ", " + cal + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTime(currentRank, cal);
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getTimestamp("
                    + parameterIndex + ", " + cal + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTimestamp(currentRank, cal);
    }

    public void addBatch() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.addBatch()", this);
            OracleLog.recursiveTrace = false;
        }
        if (currentRowBindAccessors != null) {
            DatabaseError
                    .throwSqlException(90,
                                       "Stored procedure with out or inout parameters cannot be batched");
        }
        super.addBatch();
    }

    protected void alwaysOnClose() throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleCallableStatement.alwaysOnClose()", this);
            OracleLog.recursiveTrace = false;
        }
        sqlObject.resetNamedParameters();
        parameterCount = 0;
        atLeastOneOrdinalParameter = false;
        atLeastOneNamedParameter = false;
        super.alwaysOnClose();
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleCallableStatement.alwaysOnClose : return", this);
            OracleLog.recursiveTrace = false;
        }
    }

    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        registerOutParameter(parameterName, sqlType, 0, -1);
    }

    public void registerOutParameter(String parameterName, int sqlType, int scale)
            throws SQLException {
        registerOutParameter(parameterName, sqlType, scale, -1);
    }

    public void registerOutParameter(String parameterName, int sqlType, String typeName)
            throws SQLException {
        int index = addNamedPara(parameterName);
        registerOutParameter(index, sqlType, typeName);
    }

    public URL getURL(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO, "OracleCallableStatement.getURL("
                    + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getURL(currentRank);
    }

    public void setURL(String parameterName, URL val) throws SQLException {
        int index = addNamedPara(parameterName);
        setURLInternal(index, val);
    }

    public void setNull(String parameterName, int sqlType) throws SQLException {
        int index = addNamedPara(parameterName);
        setNullInternal(index, sqlType);
    }

    public void setBoolean(String parameterName, boolean x) throws SQLException {
        int index = addNamedPara(parameterName);
        setBooleanInternal(index, x);
    }

    public void setByte(String parameterName, byte x) throws SQLException {
        int index = addNamedPara(parameterName);
        setByteInternal(index, x);
    }

    public void setShort(String parameterName, short x) throws SQLException {
        int index = addNamedPara(parameterName);
        setShortInternal(index, x);
    }

    public void setInt(String parameterName, int x) throws SQLException {
        int index = addNamedPara(parameterName);
        setIntInternal(index, x);
    }

    public void setLong(String parameterName, long x) throws SQLException {
        int index = addNamedPara(parameterName);
        setLongInternal(index, x);
    }

    public void setFloat(String parameterName, float x) throws SQLException {
        int index = addNamedPara(parameterName);
        setFloatInternal(index, x);
    }

    public void setBinaryFloat(String parameterName, float x) throws SQLException {
        int index = addNamedPara(parameterName);
        setBinaryFloatInternal(index, x);
    }

    public void setBinaryFloat(String parameterName, BINARY_FLOAT x) throws SQLException {
        int index = addNamedPara(parameterName);
        setBinaryFloatInternal(index, x);
    }

    public void setDouble(String parameterName, double x) throws SQLException {
        int index = addNamedPara(parameterName);
        setDoubleInternal(index, x);
    }

    public void setBinaryDouble(String parameterName, double x) throws SQLException {
        int index = addNamedPara(parameterName);
        setBinaryDoubleInternal(index, x);
    }

    public void setBinaryDouble(String parameterName, BINARY_DOUBLE x) throws SQLException {
        int index = addNamedPara(parameterName);
        setBinaryDoubleInternal(index, x);
    }

    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        int index = addNamedPara(parameterName);
        setBigDecimalInternal(index, x);
    }

    public void setString(String parameterName, String x) throws SQLException {
        int index = addNamedPara(parameterName);
        setStringInternal(index, x);
    }

    public void setStringForClob(String parameterName, String x) throws SQLException {
        int index = addNamedPara(parameterName);
        if (x == null || x.length() == 0) {
            setNull(index, 2005);
            return;
        } else {
            setStringForClob(index, x);
            return;
        }
    }

    public void setStringForClob(int paramIndex, String x) throws SQLException {
        if (x == null || x.length() == 0) {
            setNull(paramIndex, 2005);
            return;
        }
        synchronized (connection) {
            synchronized (connection) {
                setStringForClobCritical(paramIndex, x);
            }
        }
    }

    public void setBytes(String parameterName, byte x[]) throws SQLException {
        int index = addNamedPara(parameterName);
        setBytesInternal(index, x);
    }

    public void setBytesForBlob(String parameterName, byte x[]) throws SQLException {
        int index = addNamedPara(parameterName);
        setBytesForBlob(index, x);
    }

    public void setBytesForBlob(int paramIndex, byte x[]) throws SQLException {
        if (x == null || x.length == 0) {
            setNull(paramIndex, 2004);
            return;
        }
        synchronized (connection) {
            synchronized (connection) {
                setBytesForBlobCritical(paramIndex, x);
            }
        }
    }

    public void setDate(String parameterName, Date x) throws SQLException {
        int index = addNamedPara(parameterName);
        setDateInternal(index, x);
    }

    public void setTime(String parameterName, Time x) throws SQLException {
        int index = addNamedPara(parameterName);
        setTimeInternal(index, x);
    }

    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        int index = addNamedPara(parameterName);
        setTimestampInternal(index, x);
    }

    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        int index = addNamedPara(parameterName);
        setAsciiStreamInternal(index, x, length);
    }

    public void setBinaryStream(String parameterName, InputStream x, int length)
            throws SQLException {
        int index = addNamedPara(parameterName);
        setBinaryStreamInternal(index, x, length);
    }

    public void setObject(String parameterName, Object x, int targetSqlType, int scale)
            throws SQLException {
        int index = addNamedPara(parameterName);
        setObjectInternal(index, x, targetSqlType, scale);
    }

    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        setObject(parameterName, x, targetSqlType, 0);
    }

    public void setObject(String parameterName, Object x) throws SQLException {
        int index = addNamedPara(parameterName);
        setObjectInternal(index, x);
    }

    public void setCharacterStream(String parameterName, Reader reader, int length)
            throws SQLException {
        int index = addNamedPara(parameterName);
        setCharacterStreamInternal(index, reader, length);
    }

    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        int index = addNamedPara(parameterName);
        setDateInternal(index, x, cal);
    }

    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        int index = addNamedPara(parameterName);
        setTimeInternal(index, x, cal);
    }

    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        int index = addNamedPara(parameterName);
        setTimestampInternal(index, x, cal);
    }

    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        int index = addNamedPara(parameterName);
        setNullInternal(index, sqlType, typeName);
    }

    public String getString(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getString(currentRank);
    }

    public boolean getBoolean(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBoolean(currentRank);
    }

    public byte getByte(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getByte(currentRank);
    }

    public short getShort(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getShort(currentRank);
    }

    public int getInt(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getInt(currentRank);
    }

    public long getLong(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getLong(currentRank);
    }

    public float getFloat(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getFloat(currentRank);
    }

    public double getDouble(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDouble(currentRank);
    }

    public byte[] getBytes(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBytes(currentRank);
    }

    public Date getDate(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDate(currentRank);
    }

    public Time getTime(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTime(currentRank);
    }

    public Timestamp getTimestamp(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTimestamp(currentRank);
    }

    public Object getObject(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getObject(currentRank);
    }

    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBigDecimal(currentRank);
    }

    public Object getObject(String parameterName, Map map) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getObject(currentRank, map);
    }

    public Ref getRef(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getREF(currentRank);
    }

    public Blob getBlob(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getBLOB(currentRank);
    }

    public Clob getClob(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getCLOB(currentRank);
    }

    public Array getArray(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getARRAY(currentRank);
    }

    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getDate(currentRank, cal);
    }

    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTime(currentRank, cal);
    }

    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getTimestamp(currentRank, cal);
    }

    public URL getURL(String parameterName) throws SQLException {
        if (!atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        String iName = parameterName.toUpperCase().intern();
        int parameterIndex;
        for (parameterIndex = 0; parameterIndex < parameterCount
                && iName != namedParameters[parameterIndex]; parameterIndex++) {
        }
        parameterIndex++;
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(6);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getURL(currentRank);
    }

    public synchronized void registerIndexTableOutParameter(int paramIndex, int maxLen,
            int elemSqlType, int elemMaxLen) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleCallableStatement.registerIndexTableOutParameter(paramIndex="
                                               + paramIndex + ", maxLen=" + maxLen
                                               + ", elemSqlType=" + elemSqlType + ", elemMaxlen="
                                               + elemMaxLen + ")", this);
            OracleLog.recursiveTrace = false;
        }
        int index = paramIndex - 1;
        if (index < 0 || paramIndex > numberOfBindPositions) {
            DatabaseError.throwSqlException(3);
        }
        int internal_type = getInternalType(elemSqlType);
        resetBatch();
        currentRowNeedToPrepareBinds = true;
        if (currentRowBindAccessors == null) {
            currentRowBindAccessors = new Accessor[numberOfBindPositions];
        }
        currentRowBindAccessors[index] = allocateIndexTableAccessor(elemSqlType, internal_type,
                                                                    elemMaxLen, maxLen,
                                                                    currentRowFormOfUse[index],
                                                                    true);
        hasIbtBind = true;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(
                         Level.FINE,
                         "OracleCallableStatement.registerIndexTableOutParameter(paramIndex, maxLen, elemSqlType, elemMaxLen):return",
                         this);
            OracleLog.recursiveTrace = false;
        }
    }

    PlsqlIndexTableAccessor allocateIndexTableAccessor(int elemSqlType, int elemInternalType,
            int elemMaxLen, int maxNumOfElements, short form, boolean forBind) throws SQLException {
        return new PlsqlIndexTableAccessor(this, elemSqlType, elemInternalType, elemMaxLen,
                maxNumOfElements, form, forBind);
    }

    public synchronized Object getPlsqlIndexTable(int paramIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleCallableStatement.getPlsqlIndexTable(paramIndex="
                                               + paramIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        Datum darray[] = getOraclePlsqlIndexTable(paramIndex);
        PlsqlIndexTableAccessor accessor = (PlsqlIndexTableAccessor) outBindAccessors[paramIndex - 1];
        int type = accessor.elementInternalType;
        Object jarray[] = null;
        switch (type) {
        case 9: // '\t'
            jarray = new String[darray.length];
            break;

        case 6: // '\006'
            jarray = new BigDecimal[darray.length];
            break;

        default:
            DatabaseError.throwSqlException(1, "Invalid column type");
            break;
        }
        for (int i = 0; i < jarray.length; i++) {
            jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? null : darray[i]
                    .toJdbc();
        }

        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleCallableStatement.getPlsqlIndexTable(paramIndex):return()", this);
            OracleLog.recursiveTrace = false;
        }
        return ((Object) (jarray));
    }

    public synchronized Object getPlsqlIndexTable(int paramIndex, Class primitiveType)
            throws SQLException {
        Datum darray[] = getOraclePlsqlIndexTable(paramIndex);
        if (primitiveType == null || !primitiveType.isPrimitive()) {
            DatabaseError.throwSqlException(68);
        }
        String type = primitiveType.getName();
        if (type.equals("byte")) {
            byte jarray[] = new byte[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null ? 0 : darray[i].byteValue();
            }

            return jarray;
        }
        if (type.equals("char")) {
            char jarray[] = new char[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? '\0'
                        : (char) darray[i].intValue();
            }

            return jarray;
        }
        if (type.equals("double")) {
            double jarray[] = new double[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? 0.0D : darray[i]
                        .doubleValue();
            }

            return jarray;
        }
        if (type.equals("float")) {
            float jarray[] = new float[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? 0.0F : darray[i]
                        .floatValue();
            }

            return jarray;
        }
        if (type.equals("int")) {
            int jarray[] = new int[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? 0 : darray[i]
                        .intValue();
            }

            return jarray;
        }
        if (type.equals("long")) {
            long jarray[] = new long[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? 0L : darray[i]
                        .longValue();
            }

            return jarray;
        }
        if (type.equals("short")) {
            short jarray[] = new short[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? 0
                        : (short) darray[i].intValue();
            }

            return jarray;
        }
        if (type.equals("boolean")) {
            boolean jarray[] = new boolean[darray.length];
            for (int i = 0; i < darray.length; i++) {
                jarray[i] = darray[i] == null || darray[i].getLength() == 0L ? false : darray[i]
                        .booleanValue();
            }

            return jarray;
        } else {
            DatabaseError.throwSqlException(23);
            return null;
        }
    }

    public synchronized Datum[] getOraclePlsqlIndexTable(int parameterIndex) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.INFO,
                                       "OracleCallableStatement.getOraclePlsqlIndexTable("
                                               + parameterIndex + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (atLeastOneNamedParameter) {
            DatabaseError
                    .throwSqlException(90, "Ordinal binding and Named binding cannot be combined!");
        }
        Accessor accessor = null;
        if (parameterIndex <= 0 || parameterIndex > numberOfBindPositions
                || (accessor = outBindAccessors[parameterIndex - 1]) == null) {
            DatabaseError.throwSqlException(3);
        }
        lastIndex = parameterIndex;
        if (streamList != null) {
            closeUsedStreams(parameterIndex);
        }
        return accessor.getOraclePlsqlIndexTable(currentRank);
    }

    public boolean execute() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                ensureOpen();
                if ((this.atLeastOneNamedParameter) && (this.atLeastOneOrdinalParameter)) {
                    DatabaseError
                            .throwSqlException(90,
                                               "Ordinal binding and Named binding cannot be combined!");
                }
                if (this.sqlObject.setNamedParameters(this.parameterCount, this.namedParameters))
                    this.needToParse = true;
                return super.execute();
            }
        }
    }

    public int executeUpdate() throws SQLException {
        synchronized (this.connection) {
            synchronized (this) {
                ensureOpen();
                if ((this.atLeastOneNamedParameter) && (this.atLeastOneOrdinalParameter)) {
                    DatabaseError
                            .throwSqlException(90,
                                               "Ordinal binding and Named binding cannot be combined!");
                }
                if (this.sqlObject.setNamedParameters(this.parameterCount, this.namedParameters))
                    this.needToParse = true;
                return super.executeUpdate();
            }
        }
    }

    public synchronized void setNull(int paramIndex, int x, String y) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setNullInternal(paramIndex, x, y);
    }

    public synchronized void setNull(int paramIndex, int x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setNullInternal(paramIndex, x);
    }

    public synchronized void setBoolean(int paramIndex, boolean x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBooleanInternal(paramIndex, x);
    }

    public synchronized void setByte(int paramIndex, byte x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setByteInternal(paramIndex, x);
    }

    public synchronized void setShort(int paramIndex, short x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setShortInternal(paramIndex, x);
    }

    public synchronized void setInt(int paramIndex, int x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setIntInternal(paramIndex, x);
    }

    public synchronized void setLong(int paramIndex, long x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setLongInternal(paramIndex, x);
    }

    public synchronized void setFloat(int paramIndex, float x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setFloatInternal(paramIndex, x);
    }

    public synchronized void setBinaryFloat(int paramIndex, float x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBinaryFloatInternal(paramIndex, x);
    }

    public synchronized void setBinaryFloat(int paramIndex, BINARY_FLOAT x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBinaryFloatInternal(paramIndex, x);
    }

    public synchronized void setBinaryDouble(int paramIndex, double x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBinaryDoubleInternal(paramIndex, x);
    }

    public synchronized void setBinaryDouble(int paramIndex, BINARY_DOUBLE x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBinaryDoubleInternal(paramIndex, x);
    }

    public synchronized void setDouble(int paramIndex, double x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setDoubleInternal(paramIndex, x);
    }

    public synchronized void setBigDecimal(int paramIndex, BigDecimal x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBigDecimalInternal(paramIndex, x);
    }

    public synchronized void setString(int paramIndex, String x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setStringInternal(paramIndex, x);
    }

    public synchronized void setFixedCHAR(int paramIndex, String x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setFixedCHARInternal(paramIndex, x);
    }

    public synchronized void setCursor(int paramIndex, ResultSet x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setCursorInternal(paramIndex, x);
    }

    public synchronized void setROWID(int paramIndex, ROWID x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setROWIDInternal(paramIndex, x);
    }

    public synchronized void setRAW(int paramIndex, RAW x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setRAWInternal(paramIndex, x);
    }

    public synchronized void setCHAR(int paramIndex, CHAR x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setCHARInternal(paramIndex, x);
    }

    public synchronized void setDATE(int paramIndex, DATE x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setDATEInternal(paramIndex, x);
    }

    public synchronized void setNUMBER(int paramIndex, NUMBER x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setNUMBERInternal(paramIndex, x);
    }

    public synchronized void setBLOB(int paramIndex, BLOB x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBLOBInternal(paramIndex, x);
    }

    public synchronized void setBlob(int paramIndex, Blob x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBlobInternal(paramIndex, x);
    }

    public synchronized void setCLOB(int paramIndex, CLOB x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setCLOBInternal(paramIndex, x);
    }

    public synchronized void setClob(int paramIndex, Clob x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setClobInternal(paramIndex, x);
    }

    public synchronized void setBFILE(int paramIndex, BFILE x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBFILEInternal(paramIndex, x);
    }

    public synchronized void setBfile(int paramIndex, BFILE x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBfileInternal(paramIndex, x);
    }

    public synchronized void setBytes(int paramIndex, byte x[]) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBytesInternal(paramIndex, x);
    }

    public synchronized void setInternalBytes(int paramIndex, byte x[], int y) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setInternalBytesInternal(paramIndex, x, y);
    }

    public synchronized void setDate(int paramIndex, Date x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setDateInternal(paramIndex, x);
    }

    public synchronized void setTime(int paramIndex, Time x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTimeInternal(paramIndex, x);
    }

    public synchronized void setTimestamp(int paramIndex, Timestamp x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTimestampInternal(paramIndex, x);
    }

    public synchronized void setINTERVALYM(int paramIndex, INTERVALYM x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setINTERVALYMInternal(paramIndex, x);
    }

    public synchronized void setINTERVALDS(int paramIndex, INTERVALDS x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setINTERVALDSInternal(paramIndex, x);
    }

    public synchronized void setTIMESTAMP(int paramIndex, TIMESTAMP x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTIMESTAMPInternal(paramIndex, x);
    }

    public synchronized void setTIMESTAMPTZ(int paramIndex, TIMESTAMPTZ x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTIMESTAMPTZInternal(paramIndex, x);
    }

    public synchronized void setTIMESTAMPLTZ(int paramIndex, TIMESTAMPLTZ x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTIMESTAMPLTZInternal(paramIndex, x);
    }

    public synchronized void setAsciiStream(int paramIndex, InputStream x, int y)
            throws SQLException {
        atLeastOneOrdinalParameter = true;
        setAsciiStreamInternal(paramIndex, x, y);
    }

    public synchronized void setBinaryStream(int paramIndex, InputStream x, int y)
            throws SQLException {
        atLeastOneOrdinalParameter = true;
        setBinaryStreamInternal(paramIndex, x, y);
    }

    public synchronized void setUnicodeStream(int paramIndex, InputStream x, int y)
            throws SQLException {
        atLeastOneOrdinalParameter = true;
        setUnicodeStreamInternal(paramIndex, x, y);
    }

    public synchronized void setCharacterStream(int paramIndex, Reader x, int y)
            throws SQLException {
        atLeastOneOrdinalParameter = true;
        setCharacterStreamInternal(paramIndex, x, y);
    }

    public synchronized void setDate(int paramIndex, Date x, Calendar y) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setDateInternal(paramIndex, x, y);
    }

    public synchronized void setTime(int paramIndex, Time x, Calendar y) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTimeInternal(paramIndex, x, y);
    }

    public synchronized void setTimestamp(int paramIndex, Timestamp x, Calendar y)
            throws SQLException {
        atLeastOneOrdinalParameter = true;
        setTimestampInternal(paramIndex, x, y);
    }

    public synchronized void setURL(int paramIndex, URL x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setURLInternal(paramIndex, x);
    }

    public void setArray(int paramIndex, Array x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setArrayInternal(paramIndex, x);
    }

    public void setARRAY(int paramIndex, ARRAY x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setARRAYInternal(paramIndex, x);
    }

    public void setOPAQUE(int paramIndex, OPAQUE x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setOPAQUEInternal(paramIndex, x);
    }

    public void setStructDescriptor(int paramIndex, StructDescriptor x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setStructDescriptorInternal(paramIndex, x);
    }

    public void setSTRUCT(int paramIndex, STRUCT x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setSTRUCTInternal(paramIndex, x);
    }

    public void setCustomDatum(int paramIndex, CustomDatum x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setCustomDatumInternal(paramIndex, x);
    }

    public void setORAData(int paramIndex, ORAData x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setORADataInternal(paramIndex, x);
    }

    public void setObject(int paramIndex, Object x, int y, int z) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setObjectInternal(paramIndex, x, y, z);
    }

    public void setObject(int paramIndex, Object x, int y) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setObjectInternal(paramIndex, x, y);
    }

    public void setRefType(int paramIndex, REF x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setRefTypeInternal(paramIndex, x);
    }

    public void setRef(int paramIndex, Ref x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setRefInternal(paramIndex, x);
    }

    public void setREF(int paramIndex, REF x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setREFInternal(paramIndex, x);
    }

    public void setObject(int paramIndex, Object x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setObjectInternal(paramIndex, x);
    }

    public void setOracleObject(int paramIndex, Datum x) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setOracleObjectInternal(paramIndex, x);
    }

    public synchronized void setPlsqlIndexTable(int paramIndex, Object arrayData, int maxLen,
            int curLen, int elemSqlType, int elemMaxLen) throws SQLException {
        atLeastOneOrdinalParameter = true;
        setPlsqlIndexTableInternal(paramIndex, arrayData, maxLen, curLen, elemSqlType, elemMaxLen);
    }

    int addNamedPara(String parameterName) throws SQLException {
        String iParameterName = parameterName.toUpperCase().intern();
        for (int i = 0; i < parameterCount; i++) {
            if (iParameterName == namedParameters[i]) {
                return i + 1;
            }
        }

        if (parameterCount >= namedParameters.length) {
            String newList[] = new String[namedParameters.length * 2];
            System.arraycopy(namedParameters, 0, newList, 0, namedParameters.length);
            namedParameters = newList;
        }
        namedParameters[parameterCount++] = iParameterName;
        atLeastOneNamedParameter = true;
        return parameterCount;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleCallableStatement"));
        } catch (Exception e) {
        }
    }
}
