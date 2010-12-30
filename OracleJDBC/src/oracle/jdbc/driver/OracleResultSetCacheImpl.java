package oracle.jdbc.driver;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

class OracleResultSetCacheImpl implements OracleResultSetCache {
    private static int DEFAULT_WIDTH = 5;
    private static int DEFAULT_SIZE = 5;

    Vector m_rows = null;
    int m_width;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    OracleResultSetCacheImpl() {
        this(DEFAULT_WIDTH);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetCacheImpl()", this);

            OracleLog.recursiveTrace = false;
        }
    }

    OracleResultSetCacheImpl(int width) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleResultSetCacheImpl(" + width + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (width > 0) {
            this.m_width = width;
        }
        this.m_rows = new Vector(DEFAULT_SIZE);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger
                    .log(Level.FINE,
                         "OracleResultSetCacheImpl:OracleResultSetCacheImpl(width):return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public void put(int i, int j, Object value) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "put(" + i + ", " + j + ", " + value + ")",
                                       this);

            OracleLog.recursiveTrace = false;
        }

        Vector row = null;

        while (this.m_rows.size() < i) {
            row = new Vector(this.m_width);

            this.m_rows.addElement(row);
        }

        row = (Vector) this.m_rows.elementAt(i - 1);

        while (row.size() < j) {
            row.addElement(null);
        }
        row.setElementAt(value, j - 1);
    }

    public Object get(int i, int j) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "get(" + i + ", " + j + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Vector row = (Vector) this.m_rows.elementAt(i - 1);

        return row.elementAt(j - 1);
    }

    public void remove(int i) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "remove(" + i + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.m_rows.removeElementAt(i - 1);
    }

    public void remove(int i, int j) {
        this.m_rows.removeElementAt(i - 1);
    }

    public void clear() {
    }

    public void close() {
    }

    public int getLength() {
        return 0;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleResultSetCacheImpl"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleResultSetCacheImpl JD-Core Version: 0.6.0
 */