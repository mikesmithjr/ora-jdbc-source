package oracle.jdbc.driver;

import java.io.PrintStream;
import java.sql.SQLException;

class OracleStatementCacheEntry {
    protected OracleStatementCacheEntry applicationNext = null;
    protected OracleStatementCacheEntry applicationPrev = null;

    protected OracleStatementCacheEntry explicitNext = null;
    protected OracleStatementCacheEntry explicitPrev = null;

    protected OracleStatementCacheEntry implicitNext = null;
    protected OracleStatementCacheEntry implicitPrev = null;
    boolean onImplicit;
    String sql;
    int statementType;
    int scrollType;
    OraclePreparedStatement statement;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:52_PDT_2005";

    public void print() throws SQLException {
        System.out.println("Cache entry " + this);
        System.out.println("  Key: " + this.sql + "$$" + this.statementType + "$$"
                + this.scrollType);

        System.out.println("  Statement: " + this.statement);
        System.out.println("  onImplicit: " + this.onImplicit);
        System.out.println("  applicationNext: " + this.applicationNext + "  applicationPrev: "
                + this.applicationPrev);

        System.out.println("  implicitNext: " + this.implicitNext + "  implicitPrev: "
                + this.implicitPrev);

        System.out.println("  explicitNext: " + this.explicitNext + "  explicitPrev: "
                + this.explicitPrev);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleStatementCacheEntry"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleStatementCacheEntry JD-Core Version: 0.6.0
 */