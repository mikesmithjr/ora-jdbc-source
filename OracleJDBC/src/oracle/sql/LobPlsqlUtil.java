package oracle.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.internal.OracleCallableStatement;
import oracle.jdbc.internal.OracleConnection;

public class LobPlsqlUtil {
    static boolean PLSQL_DEBUG = false;
    static final int MAX_PLSQL_SIZE = 32512;
    static final int MAX_PLSQL_INSTR_SIZE = 32512;
    static final int MAX_CHUNK_SIZE = 32512;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public static long hasPattern(BLOB blob, byte[] pattern, long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "LobPlsqlUtil.plsql_hasPattern( blob=" + blob + ", pattern="
                            + pattern + ", startPos=" + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return hasPattern(blob.getInternalConnection(), blob, 2004, pattern, startPos);
    }

    public static long isSubLob(BLOB blob, BLOB subLob, long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_isSubLob( blob=" + blob
                    + ", subLob=" + subLob + ", startPos=" + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return isSubLob(blob.getInternalConnection(), blob, 2004, subLob, startPos);
    }

    public static long hasPattern(CLOB clob, char[] pattern, long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "LobPlsqlUtil.plsql_hasPattern( clob=" + clob + ", pattern="
                            + pattern + ", startPos=" + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if ((pattern == null) || (startPos <= 0L)) {
            return 0L;
        }
        OracleConnection conn = clob.getInternalConnection();
        long patternLen = pattern.length;
        long lobLen = length(conn, clob, 2005);

        if ((patternLen == 0L) || (patternLen > lobLen - startPos + 1L) || (startPos > lobLen)) {
            return 0L;
        }

        if (patternLen <= getPlsqlMaxInstrSize(conn)) {
            OracleCallableStatement cstmt = null;
            try {
                cstmt = (OracleCallableStatement) conn
                        .prepareCall("begin :1 := dbms_lob.instr(:2, :3, :4); end;");

                cstmt.registerOutParameter(1, 2);

                if (clob.isNCLOB()) {
                    cstmt.setFormOfUse(2, (short) 2);
                    cstmt.setFormOfUse(3, (short) 2);
                }

                cstmt.setCLOB(2, clob);
                cstmt.setString(3, new String(pattern));
                cstmt.setLong(4, startPos);
                cstmt.execute();

                long l1 = cstmt.getLong(1);
                return l1;
            } finally {
                cstmt.close();

                cstmt = null;
            }

        }

        int matchedLen = 0;
        long subStartPos = startPos;
        boolean done = false;

        long matchedPos = 0L;

        while (!done) {
            if (patternLen > lobLen - subStartPos + 1L) {
                return 0L;
            }
            matchedLen = 0;

            int subPatternLen = (int) Math.min(getPlsqlMaxInstrSize(conn), patternLen - matchedLen);

            char[] subPattern = new char[subPatternLen];

            System.arraycopy(pattern, matchedLen, subPattern, 0, subPatternLen);

            long subMatchedPos = hasPattern(clob, subPattern, subStartPos);

            if (subMatchedPos == 0L) {
                return 0L;
            }

            matchedPos = subMatchedPos;

            matchedLen += subPatternLen;
            subStartPos = subMatchedPos + subPatternLen;

            boolean moreChunks = true;

            while (moreChunks) {
                subPatternLen = (int) Math.min(getPlsqlMaxInstrSize(conn), patternLen - matchedLen);

                subPattern = new char[subPatternLen];

                System.arraycopy(pattern, matchedLen, subPattern, 0, subPatternLen);

                subMatchedPos = hasPattern(clob, subPattern, subStartPos);

                if (subMatchedPos == subStartPos) {
                    matchedLen += subPatternLen;
                    subStartPos += subPatternLen;

                    if (matchedLen != patternLen)
                        continue;
                    moreChunks = false;
                    done = true;
                    continue;
                }

                if (subMatchedPos == 0L) {
                    return 0L;
                }

                subStartPos = subMatchedPos - matchedLen;

                moreChunks = false;
            }

        }

        return matchedPos;
    }

    public static long isSubLob(CLOB clob, CLOB subLob, long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_isSubLob( clob=" + clob
                    + ", subLob=" + subLob + ", startPos=" + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if ((subLob == null) || (startPos <= 0L)) {
            return 0L;
        }
        OracleConnection conn = clob.getInternalConnection();
        long patternLen = length(conn, subLob, 2005);
        long lobLen = length(conn, clob, 2005);

        if ((patternLen == 0L) || (patternLen > lobLen - startPos + 1L) || (startPos > lobLen)) {
            return 0L;
        }

        if (patternLen <= getPlsqlMaxInstrSize(conn)) {
            char[] pattern = new char[(int) patternLen];

            subLob.getChars(1L, (int) patternLen, pattern);

            return hasPattern(clob, pattern, startPos);
        }

        int matchedLen = 0;
        long subStartPos = startPos;
        boolean done = false;

        long matchedPos = 0L;

        while (!done) {
            if (patternLen > lobLen - subStartPos + 1L) {
                return 0L;
            }
            matchedLen = 0;

            int subPatternLen = (int) Math.min(getPlsqlMaxInstrSize(conn), patternLen - matchedLen);

            char[] subPattern = new char[subPatternLen];

            subLob.getChars(matchedLen + 1, subPatternLen, subPattern);

            long subMatchedPos = hasPattern(clob, subPattern, subStartPos);

            if (subMatchedPos == 0L) {
                return 0L;
            }

            matchedPos = subMatchedPos;

            matchedLen += subPatternLen;
            subStartPos = subMatchedPos + subPatternLen;

            boolean moreChunks = true;

            while (moreChunks) {
                subPatternLen = (int) Math.min(getPlsqlMaxInstrSize(conn), patternLen - matchedLen);

                subPattern = new char[subPatternLen];

                subLob.getChars(matchedLen + 1, subPatternLen, subPattern);

                subMatchedPos = hasPattern(clob, subPattern, subStartPos);

                if (subMatchedPos == subStartPos) {
                    matchedLen += subPatternLen;
                    subStartPos += subPatternLen;

                    if (matchedLen != patternLen)
                        continue;
                    moreChunks = false;
                    done = true;
                    continue;
                }

                if (subMatchedPos == 0L) {
                    return 0L;
                }

                subStartPos = subMatchedPos - matchedLen;

                moreChunks = false;
            }

        }

        return matchedPos;
    }

    public static long hasPattern(BFILE bfile, byte[] pattern, long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "LobPlsqlUtil.plsql_hasPattern( bfile=" + bfile + ", pattern="
                            + pattern + ", startPos=" + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return hasPattern(bfile.getInternalConnection(), bfile, -13, pattern, startPos);
    }

    public static long isSubLob(BFILE bfile, BFILE subLob, long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_isSubLob( bfile=" + bfile
                    + ", subLob=" + subLob + ", startPos=" + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        return isSubLob(bfile.getInternalConnection(), bfile, -13, subLob, startPos);
    }

    public static String fileGetName(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_fileGetName( bfile=" + bfile
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        OracleCallableStatement cstmt = null;
        String ret = null;
        try {
            cstmt = (OracleCallableStatement) bfile.getInternalConnection()
                    .prepareCall("begin dbms_lob.fileGetName(:1, :2, :3); end; ");

            cstmt.setBFILE(1, bfile);
            cstmt.registerOutParameter(2, 12);
            cstmt.registerOutParameter(3, 12);
            cstmt.execute();

            ret = cstmt.getString(3);
        } finally {
            if (cstmt != null) {
                cstmt.close();

                cstmt = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_fileGetName: return: " + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static String fileGetDirAlias(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_fileGetDirAlias( bfile="
                    + bfile + ")");

            OracleLog.recursiveTrace = false;
        }

        OracleCallableStatement cstmt = null;
        String ret = null;
        try {
            cstmt = (OracleCallableStatement) bfile.getInternalConnection()
                    .prepareCall("begin dbms_lob.fileGetName(:1, :2, :3); end; ");

            cstmt.setBFILE(1, bfile);
            cstmt.registerOutParameter(2, 12);
            cstmt.registerOutParameter(3, 12);
            cstmt.execute();

            ret = cstmt.getString(2);
        } finally {
            if (cstmt != null) {
                cstmt.close();

                cstmt = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_fileGetDirAlias: return: "
                    + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    private static int getPlsqlMaxInstrSize(OracleConnection conn) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.getPlsqlMaxInstrSize( conn=" + conn
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        boolean isCharSetMultibyte = conn.isCharSetMultibyte(conn.getDriverCharSet());

        int maxCharbyteSize = conn.getMaxCharbyteSize();

        int ret = 32512;

        if (isCharSetMultibyte) {
            ret = 32512 / (conn.getC2SNlsRatio() * maxCharbyteSize);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.getPlsqlMaxInstrSize: return: "
                    + ret);

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static long read(OracleConnection conn, Datum lob, int type, long pos, long length,
            byte[] bytes_read) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_read( conn=" + conn
                    + ", lob=" + lob + ", type=" + type + ", pos=" + pos + ", length=" + length
                    + ", bytes_read=" + bytes_read + ")");

            OracleLog.recursiveTrace = false;
        }

        OracleCallableStatement cstmt = null;
        int totalSizeRead = 0;
        try {
            cstmt = (OracleCallableStatement) conn
                    .prepareCall("begin dbms_lob.read (:1, :2, :3, :4); end;");

            int readThisTime = 0;
            int chunkSize = 0;

            if (isNCLOB(lob)) {
                cstmt.setFormOfUse(1, (short) 2);
                cstmt.setFormOfUse(4, (short) 2);
            }

            cstmt.setObject(1, lob, type);
            cstmt.registerOutParameter(2, 2);
            cstmt.registerOutParameter(4, -3);

            while (totalSizeRead < length) {
                chunkSize = Math.min((int) length, 32512);

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.FINEST,
                                              "LobPlsqlUtil.plsql_read: read chunks: chunkSize="
                                                      + chunkSize);

                    OracleLog.recursiveTrace = false;
                }

                cstmt.setInt(2, chunkSize);
                cstmt.setInt(3, (int) pos + totalSizeRead);
                cstmt.execute();

                readThisTime = cstmt.getInt(2);
                byte[] bytesThisTime = cstmt.getBytes(4);

                System.arraycopy(bytesThisTime, 0, bytes_read, totalSizeRead, readThisTime);

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.FINEST,
                                              "LobPlsqlUtil.plsql_read: ==> sizeRead="
                                                      + readThisTime);

                    OracleLog.recursiveTrace = false;
                }

                totalSizeRead += readThisTime;
                length -= readThisTime;
            }

        } catch (SQLException e) {
            if (e.getErrorCode() != 1403) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.datumLogger.log(Level.SEVERE,
                                              "LobPlsqlUtil.plsql_read: exception caught and thrown."
                                                      + e.getMessage());

                    OracleLog.recursiveTrace = false;
                }

                throw e;
            }

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.SEVERE,
                             "LobPlsqlUtil.plsql_read: ORA-1403 No data found, end of file");

                OracleLog.recursiveTrace = false;
            }

        } finally {
            if (cstmt != null) {
                cstmt.close();

                cstmt = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_read: return: "
                    + totalSizeRead);

            OracleLog.recursiveTrace = false;
        }

        return totalSizeRead;
    }

    public static long length(OracleConnection conn, Datum lob, int type) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_length( conn=" + conn
                    + ", lob=" + lob + ", type=" + type + ")");

            OracleLog.recursiveTrace = false;
        }

        long lob_length = 0L;
        OracleCallableStatement cstmt = null;
        try {
            cstmt = (OracleCallableStatement) conn
                    .prepareCall("begin :1 := dbms_lob.getLength (:2); end;");

            if (isNCLOB(lob)) {
                cstmt.setFormOfUse(2, (short) 2);
            }
            cstmt.setObject(2, lob, type);
            cstmt.registerOutParameter(1, 2);
            cstmt.execute();

            lob_length = cstmt.getLong(1);
        } finally {
            if (cstmt != null) {
                cstmt.close();

                cstmt = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_length: return: "
                    + lob_length);

            OracleLog.recursiveTrace = false;
        }

        return lob_length;
    }

    public static long hasPattern(OracleConnection conn, Datum lob, int type, byte[] pattern,
            long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_hasPattern( conn=" + conn
                    + ", lob=" + lob + ", type=" + type + ", pattern=" + pattern + ", startPos="
                    + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if ((pattern == null) || (startPos <= 0L)) {
            return 0L;
        }
        long patternLen = pattern.length;
        long lobLen = length(conn, lob, type);

        if ((patternLen == 0L) || (patternLen > lobLen - startPos + 1L) || (startPos > lobLen)) {
            return 0L;
        }

        if (patternLen <= 32512L) {
            OracleCallableStatement cstmt = null;
            try {
                cstmt = (OracleCallableStatement) conn
                        .prepareCall("begin :1 := dbms_lob.instr(:2, :3, :4); end;");

                cstmt.registerOutParameter(1, 2);
                cstmt.setObject(2, lob, type);
                cstmt.setBytes(3, pattern);
                cstmt.setLong(4, startPos);
                cstmt.execute();

                long l1 = cstmt.getLong(1);
                return l1;
            } finally {
                cstmt.close();

                cstmt = null;
            }

        }

        int matchedLen = 0;
        long subStartPos = startPos;
        boolean done = false;

        long matchedPos = 0L;

        while (!done) {
            if (patternLen > lobLen - subStartPos + 1L) {
                return 0L;
            }
            matchedLen = 0;

            int subPatternLen = (int) Math.min(32512L, patternLen - matchedLen);

            byte[] subPattern = new byte[subPatternLen];

            System.arraycopy(pattern, matchedLen, subPattern, 0, subPatternLen);

            long subMatchedPos = hasPattern(conn, lob, type, subPattern, subStartPos);

            if (subMatchedPos == 0L) {
                return 0L;
            }

            matchedPos = subMatchedPos;

            matchedLen += subPatternLen;
            subStartPos = subMatchedPos + subPatternLen;

            boolean moreChunks = true;

            while (moreChunks) {
                subPatternLen = (int) Math.min(32512L, patternLen - matchedLen);

                subPattern = new byte[subPatternLen];

                System.arraycopy(pattern, matchedLen, subPattern, 0, subPatternLen);

                subMatchedPos = hasPattern(conn, lob, type, subPattern, subStartPos);

                if (subMatchedPos == subStartPos) {
                    matchedLen += subPatternLen;
                    subStartPos += subPatternLen;

                    if (matchedLen != patternLen)
                        continue;
                    moreChunks = false;
                    done = true;
                    continue;
                }

                if (subMatchedPos == 0L) {
                    return 0L;
                }

                subStartPos = subMatchedPos - matchedLen;

                moreChunks = false;
            }

        }

        return matchedPos;
    }

    public static long isSubLob(OracleConnection conn, Datum lob, int type, Datum subLob,
            long startPos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_isSubLob( conn=" + conn
                    + ", lob=" + lob + ", type=" + type + ", subLob=" + subLob + ", startPos="
                    + startPos + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        if ((subLob == null) || (startPos <= 0L)) {
            return 0L;
        }
        long patternLen = length(conn, subLob, type);
        long lobLen = length(conn, lob, type);

        if ((patternLen == 0L) || (patternLen > lobLen - startPos + 1L) || (startPos > lobLen)) {
            return 0L;
        }

        if (patternLen <= 32512L) {
            byte[] pattern = new byte[(int) patternLen];

            read(conn, subLob, type, 1L, patternLen, pattern);

            return hasPattern(conn, lob, type, pattern, startPos);
        }

        int matchedLen = 0;
        long subStartPos = startPos;
        boolean done = false;

        long matchedPos = 0L;

        while (!done) {
            if (patternLen > lobLen - subStartPos + 1L) {
                return 0L;
            }
            matchedLen = 0;

            int subPatternLen = (int) Math.min(32512L, patternLen - matchedLen);

            byte[] subPattern = new byte[subPatternLen];

            read(conn, subLob, type, matchedLen + 1, subPatternLen, subPattern);

            long subMatchedPos = hasPattern(conn, lob, type, subPattern, subStartPos);

            if (subMatchedPos == 0L) {
                return 0L;
            }

            matchedPos = subMatchedPos;

            matchedLen += subPatternLen;
            subStartPos = subMatchedPos + subPatternLen;

            boolean moreChunks = true;

            while (moreChunks) {
                subPatternLen = (int) Math.min(32512L, patternLen - matchedLen);

                subPattern = new byte[subPatternLen];

                read(conn, subLob, type, matchedLen + 1, subPatternLen, subPattern);

                subMatchedPos = hasPattern(conn, lob, type, subPattern, subStartPos);

                if (subMatchedPos == subStartPos) {
                    matchedLen += subPatternLen;
                    subStartPos += subPatternLen;

                    if (matchedLen != patternLen)
                        continue;
                    moreChunks = false;
                    done = true;
                    continue;
                }

                if (subMatchedPos == 0L) {
                    return 0L;
                }

                subStartPos = subMatchedPos - matchedLen;

                moreChunks = false;
            }

        }

        return matchedPos;
    }

    private static boolean isNCLOB(Datum lob) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.isNCLOB( lob=" + lob
                    + ") -- no return trace --");

            OracleLog.recursiveTrace = false;
        }

        Class cl = null;
        try {
            cl = Class.forName("oracle.sql.CLOB");
        } catch (ClassNotFoundException exp) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.datumLogger
                        .log(Level.FINER,
                             "LobPlsqlUtil.isNCLOB: Could not find class oracle.sql.CLOB ");

                OracleLog.recursiveTrace = false;
            }

            return false;
        }

        if (!cl.isInstance(lob)) {
            return false;
        }
        CLOB clob = (CLOB) lob;

        return clob.isNCLOB();
    }

    public static Datum createTemporaryLob(Connection conn, boolean cache, int duration, int type,
            short form_of_use) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger
                    .log(Level.FINE, "LobPlsqlUtil.plsql_createTemporaryLob( conn=" + conn
                            + ", cache=" + cache + ", duration=" + duration + ", type=" + type
                            + ")");

            OracleLog.recursiveTrace = false;
        }

        OracleCallableStatement cstmt = null;
        Datum ret = null;
        try {
            cstmt = (OracleCallableStatement) conn
                    .prepareCall("begin dbms_lob.createTemporary (:1," + (cache ? "TRUE" : "FALSE")
                            + ", :2); end;");

            cstmt.registerOutParameter(1, type);
            cstmt.setFormOfUse(1, form_of_use);

            cstmt.setInt(2, duration);
            cstmt.execute();

            ret = cstmt.getOracleObject(1);
        } finally {
            if (cstmt != null) {
                cstmt.close();

                cstmt = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_createTemporaryLob: return");

            OracleLog.recursiveTrace = false;
        }

        return ret;
    }

    public static void freeTemporaryLob(Connection conn, Datum temp_lob, int type)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_freeTemporaryLob( conn="
                    + conn + ", temp_lob=" + temp_lob + ", type=" + type + ")");

            OracleLog.recursiveTrace = false;
        }

        OracleCallableStatement cstmt = null;
        try {
            cstmt = (OracleCallableStatement) conn
                    .prepareCall("begin dbms_lob.freeTemporary (:1); end;");

            cstmt.registerOutParameter(1, type);

            if (isNCLOB(temp_lob)) {
                cstmt.setFormOfUse(1, (short) 2);
            }
            cstmt.setOracleObject(1, temp_lob);
            cstmt.execute();
            temp_lob.setShareBytes(cstmt.privateGetBytes(1));
        } finally {
            if (cstmt != null) {
                cstmt.close();

                cstmt = null;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.datumLogger.log(Level.FINE, "LobPlsqlUtil.plsql_freeTemporaryLob: return");

            OracleLog.recursiveTrace = false;
        }
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.LobPlsqlUtil"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.LobPlsqlUtil JD-Core Version: 0.6.0
 */