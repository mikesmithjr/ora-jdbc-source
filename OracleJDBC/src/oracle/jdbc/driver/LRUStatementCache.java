package oracle.jdbc.driver;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LRUStatementCache {
    private int cacheSize;
    private int numElements;
    private OracleStatementCacheEntry applicationCacheStart;
    private OracleStatementCacheEntry applicationCacheEnd;
    private OracleStatementCacheEntry implicitCacheStart;
    private OracleStatementCacheEntry explicitCacheStart;
    boolean implicitCacheEnabled;
    boolean explicitCacheEnabled;
    private boolean debug = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    protected LRUStatementCache(int size) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LRUStatementCache(" + size + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (size < 0) {
            DatabaseError.throwSqlException(123);
        }
        this.cacheSize = size;
        this.numElements = 0;

        this.implicitCacheStart = null;
        this.explicitCacheStart = null;

        this.implicitCacheEnabled = false;
        this.explicitCacheEnabled = false;
    }

    protected void resize(int newSize) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "resize(" + newSize + ") entry", this);

            OracleLog.recursiveTrace = false;
        }

        if (newSize < 0) {
            DatabaseError.throwSqlException(123);
        }
        if ((newSize >= this.cacheSize) || (newSize >= this.numElements)) {
            this.cacheSize = newSize;
        } else {
            OracleStatementCacheEntry e = this.applicationCacheEnd;
            for (; this.numElements > newSize; e = e.applicationPrev) {
                purgeCacheEntry(e);
            }
            this.cacheSize = newSize;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "resize(" + newSize + ") exit", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void setImplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "setImplicitCachingEnabled(" + cache + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!cache) {
            purgeImplicitCache();
        }
        this.implicitCacheEnabled = cache;
    }

    public boolean getImplicitCachingEnabled() throws SQLException {
        boolean retValue;
        if (this.cacheSize == 0)
            retValue = false;
        else {
            retValue = this.implicitCacheEnabled;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getImplicitCachingEnabled(), returning "
                    + retValue, this);

            OracleLog.recursiveTrace = false;
        }

        return retValue;
    }

    public void setExplicitCachingEnabled(boolean cache) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE, "setExplicitCachingEnabled(" + cache + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!cache) {
            purgeExplicitCache();
        }
        this.explicitCacheEnabled = cache;
    }

    public boolean getExplicitCachingEnabled() throws SQLException {
        boolean retValue;
        if (this.cacheSize == 0)
            retValue = false;
        else {
            retValue = this.explicitCacheEnabled;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getExplicitCachingEnabled(), returning "
                    + retValue, this);

            OracleLog.recursiveTrace = false;
        }

        return retValue;
    }

    protected void addToImplicitCache(OraclePreparedStatement stmt, String sql, int statementType,
            int scrollType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "addToImplicitCache(" + sql + ", "
                    + statementType + ", " + scrollType + ") entry", this);

            OracleLog.recursiveTrace = false;
        }

        if ((!this.implicitCacheEnabled) || (this.cacheSize == 0) || (stmt.cacheState == 2)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINER,
                             "implicit caching not enabled or already cached, returning...", this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if (this.numElements == this.cacheSize) {
            purgeCacheEntry(this.applicationCacheEnd);
        }

        stmt.enterImplicitCache();

        OracleStatementCacheEntry entry = new OracleStatementCacheEntry();

        entry.statement = stmt;
        entry.onImplicit = true;

        entry.sql = sql;
        entry.statementType = statementType;
        entry.scrollType = scrollType;

        entry.applicationNext = this.applicationCacheStart;
        entry.applicationPrev = null;

        if (this.applicationCacheStart != null) {
            this.applicationCacheStart.applicationPrev = entry;
        }
        this.applicationCacheStart = entry;

        entry.implicitNext = this.implicitCacheStart;
        entry.implicitPrev = null;

        if (this.implicitCacheStart != null) {
            this.implicitCacheStart.implicitPrev = entry;
        }
        this.implicitCacheStart = entry;

        if (this.applicationCacheEnd == null) {
            this.applicationCacheEnd = entry;
        }

        this.numElements += 1;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "addToImplicitCache : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected void addToExplicitCache(OraclePreparedStatement stmt, String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "addToExplicitCache(" + key + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((!this.explicitCacheEnabled) || (this.cacheSize == 0) || (stmt.cacheState == 2)) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINER,
                             "explicit caching not enabled or already cached, returning...", this);

                OracleLog.recursiveTrace = false;
            }

            return;
        }

        if (this.numElements == this.cacheSize) {
            purgeCacheEntry(this.applicationCacheEnd);
        }

        stmt.enterExplicitCache();

        OracleStatementCacheEntry entry = new OracleStatementCacheEntry();

        entry.statement = stmt;
        entry.sql = key;
        entry.onImplicit = false;

        entry.applicationNext = this.applicationCacheStart;
        entry.applicationPrev = null;

        if (this.applicationCacheStart != null) {
            this.applicationCacheStart.applicationPrev = entry;
        }
        this.applicationCacheStart = entry;

        entry.explicitNext = this.explicitCacheStart;
        entry.explicitPrev = null;

        if (this.explicitCacheStart != null) {
            this.explicitCacheStart.explicitPrev = entry;
        }
        this.explicitCacheStart = entry;

        if (this.applicationCacheEnd == null) {
            this.applicationCacheEnd = entry;
        }

        this.numElements += 1;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "addToExplicitCache : return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    protected OracleStatement searchImplicitCache(String sql, int statementType, int scrollType)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "searchImplicitCache(" + sql + ", "
                    + statementType + ", " + scrollType + ") -- current cache size is "
                    + this.numElements + "/" + this.cacheSize, this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.implicitCacheEnabled) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINER,
                             "searchImplicitCache: implicit cache not enabled, returning null",
                             this);

                OracleLog.recursiveTrace = false;
            }

            return null;
        }

        OracleStatementCacheEntry entry = null;

        for (entry = this.implicitCacheStart; entry != null; entry = entry.implicitNext) {
            if ((entry.statementType == statementType) && (entry.scrollType == scrollType)
                    && (entry.sql.equals(sql))) {
                break;
            }
        }
        if (entry != null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINER, "searchImplicitCache: Found statement with key " + sql
                                + "$$" + statementType + "$$" + scrollType, this);

                OracleLog.recursiveTrace = false;
            }

            if (entry.applicationPrev != null) {
                entry.applicationPrev.applicationNext = entry.applicationNext;
            }
            if (entry.applicationNext != null) {
                entry.applicationNext.applicationPrev = entry.applicationPrev;
            }
            if (this.applicationCacheStart == entry) {
                this.applicationCacheStart = entry.applicationNext;
            }
            if (this.applicationCacheEnd == entry) {
                this.applicationCacheEnd = entry.applicationPrev;
            }
            if (entry.implicitPrev != null) {
                entry.implicitPrev.implicitNext = entry.implicitNext;
            }
            if (entry.implicitNext != null) {
                entry.implicitNext.implicitPrev = entry.implicitPrev;
            }
            if (this.implicitCacheStart == entry) {
                this.implicitCacheStart = entry.implicitNext;
            }

            this.numElements -= 1;

            entry.statement.exitImplicitCacheToActive();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINE,
                                           "searchImplicitCache - return statement from cache",
                                           this);

                OracleLog.recursiveTrace = false;
            }

            return entry.statement;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "searchImplicitCache - return null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    protected OracleStatement searchExplicitCache(String key) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "searchExplicitCache(" + key + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (!this.explicitCacheEnabled) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger
                        .log(Level.FINER,
                             "searchExplicitCache: explicit cache not enabled, returning null",
                             this);

                OracleLog.recursiveTrace = false;
            }

            return null;
        }

        OracleStatementCacheEntry entry = null;

        for (entry = this.explicitCacheStart; entry != null; entry = entry.explicitNext) {
            if (entry.sql.equals(key)) {
                break;
            }
        }
        if (entry != null) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "searchExplicitCache: Found statement with key "
                                                   + entry.sql, this);

                OracleLog.recursiveTrace = false;
            }

            if (entry.applicationPrev != null) {
                entry.applicationPrev.applicationNext = entry.applicationNext;
            }
            if (entry.applicationNext != null) {
                entry.applicationNext.applicationPrev = entry.applicationPrev;
            }
            if (this.applicationCacheStart == entry) {
                this.applicationCacheStart = entry.applicationNext;
            }
            if (this.applicationCacheEnd == entry) {
                this.applicationCacheEnd = entry.applicationPrev;
            }
            if (entry.explicitPrev != null) {
                entry.explicitPrev.explicitNext = entry.explicitNext;
            }
            if (entry.explicitNext != null) {
                entry.explicitNext.explicitPrev = entry.explicitPrev;
            }
            if (this.explicitCacheStart == entry) {
                this.explicitCacheStart = entry.explicitNext;
            }

            this.numElements -= 1;

            entry.statement.exitExplicitCacheToActive();

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINE,
                                           "searchExplicitCache - return statement from cache",
                                           this);

                OracleLog.recursiveTrace = false;
            }

            return entry.statement;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "searchExplicitCache - return null", this);

            OracleLog.recursiveTrace = false;
        }

        return null;
    }

    protected void purgeImplicitCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "purgeImplicitCache() entry", this);

            OracleLog.recursiveTrace = false;
        }

        for (OracleStatementCacheEntry entry = this.implicitCacheStart; entry != null;) {
            purgeCacheEntry(entry);

            entry = entry.implicitNext;
        }

        this.implicitCacheStart = null;
    }

    protected void purgeExplicitCache() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "purgeExplicitCache() entry", this);

            OracleLog.recursiveTrace = false;
        }

        for (OracleStatementCacheEntry entry = this.explicitCacheStart; entry != null;) {
            purgeCacheEntry(entry);

            entry = entry.explicitNext;
        }

        this.explicitCacheStart = null;
    }

    private void purgeCacheEntry(OracleStatementCacheEntry entry) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "purgeCacheEntry()", this);

            OracleLog.recursiveTrace = false;
        }

        if (entry.applicationNext != null) {
            entry.applicationNext.applicationPrev = entry.applicationPrev;
        }
        if (entry.applicationPrev != null) {
            entry.applicationPrev.applicationNext = entry.applicationNext;
        }
        if (this.applicationCacheStart == entry) {
            this.applicationCacheStart = entry.applicationNext;
        }
        if (this.applicationCacheEnd == entry) {
            this.applicationCacheEnd = entry.applicationPrev;
        }
        if (entry.onImplicit) {
            if (entry.implicitNext != null) {
                entry.implicitNext.implicitPrev = entry.implicitPrev;
            }
            if (entry.implicitPrev != null) {
                entry.implicitPrev.implicitNext = entry.implicitNext;
            }
            if (this.implicitCacheStart == entry)
                this.implicitCacheStart = entry.implicitNext;
        } else {
            if (entry.explicitNext != null) {
                entry.explicitNext.explicitPrev = entry.explicitPrev;
            }
            if (entry.explicitPrev != null) {
                entry.explicitPrev.explicitNext = entry.explicitNext;
            }
            if (this.explicitCacheStart == entry) {
                this.explicitCacheStart = entry.explicitNext;
            }
        }

        this.numElements -= 1;

        if (entry.onImplicit)
            entry.statement.exitImplicitCacheToClose();
        else
            entry.statement.exitExplicitCacheToClose();
    }

    public int getCacheSize() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "getCacheSize(), returning " + this.cacheSize,
                                       this);

            OracleLog.recursiveTrace = false;
        }

        return this.cacheSize;
    }

    public void printCache(String msg) throws SQLException {
        System.out.println("*** Start of Statement Cache Dump (" + msg + ") ***");
        System.out.println("cache size: " + this.cacheSize + " num elements: " + this.numElements
                + " implicit enabled: " + this.implicitCacheEnabled + " explicit enabled: "
                + this.explicitCacheEnabled);

        System.out.println("applicationStart: " + this.applicationCacheStart + "  applicationEnd: "
                + this.applicationCacheEnd);

        for (OracleStatementCacheEntry e = this.applicationCacheStart; e != null; e = e.applicationNext) {
            e.print();
        }
        System.out.println("implicitStart: " + this.implicitCacheStart);

        for (OracleStatementCacheEntry e = this.implicitCacheStart; e != null; e = e.implicitNext) {
            e.print();
        }
        System.out.println("explicitStart: " + this.explicitCacheStart);

        for (OracleStatementCacheEntry e = this.explicitCacheStart; e != null; e = e.explicitNext) {
            e.print();
        }
        System.out.println("*** End of Statement Cache Dump (" + msg + ") ***");
    }

    public void close() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "LRUStatementCache.close()", this);

            OracleLog.recursiveTrace = false;
        }

        OracleStatementCacheEntry entry = this.applicationCacheStart;
        for (; entry != null; entry = entry.applicationNext) {
            if (entry.onImplicit)
                entry.statement.exitImplicitCacheToClose();
            else {
                entry.statement.exitExplicitCacheToClose();
            }

        }

        this.applicationCacheStart = null;
        this.applicationCacheEnd = null;
        this.implicitCacheStart = null;
        this.explicitCacheStart = null;
        this.numElements = 0;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.LRUStatementCache"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.LRUStatementCache JD-Core Version: 0.6.0
 */