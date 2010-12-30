package oracle.jdbc.xa.client;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.PooledConnection;
import javax.sql.XAConnection;
import javax.transaction.xa.XAException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.driver.T2CConnection;
import oracle.jdbc.driver.T4CXAConnection;

public class OracleXADataSource extends oracle.jdbc.xa.OracleXADataSource {
    private static final boolean DEBUG = false;
    private int rmid = -1;
    private String xaOpenString = null;
    private static boolean libraryLoaded = false;
    private static final String dbSuffix = "HeteroXA";
    private static final String dllName = "heteroxa10";
    private static final char atSignChar = '@';
    private static int rmidSeed = 0;
    private static final int MAX_RMID_SEED = 65536;
    private String driverCharSetIdString = null;

    private String oldTnsEntry = null;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public OracleXADataSource() throws SQLException {
        this.isOracleDataSource = true;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE,
                                   "oracle.jdbc.xa.client.OracleXADataSource() -- after super()",
                                   this);

            OracleLog.recursiveTrace = false;
        }
    }

    public XAConnection getXAConnection() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXADataSource.getXAConnection()", this);

            OracleLog.recursiveTrace = false;
        }

        return getXAConnection(this.user, this.password);
    }

    public XAConnection getXAConnection(String userName, String passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.INFO, "OracleXADataSource.getXAConnection(userName = "
                    + userName + ", passwd = " + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (this.connCachingEnabled) {
            DatabaseError.throwSqlException(163);

            return null;
        }

        Properties prop = new Properties();
        if (userName != null)
            prop.setProperty("user", userName);
        if (passwd != null) {
            prop.setProperty("password", passwd);
        }
        return getXAConnection(prop);
    }

    public XAConnection getXAConnection(Properties prop) throws SQLException {
        return (XAConnection) getPooledConnection(prop);
    }

    public PooledConnection getPooledConnection(String userName, String passwd) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.getPooledConnection(userName = "
                    + userName + ", passwd = " + passwd + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Properties prop = new Properties();
        prop.setProperty("user", userName);
        prop.setProperty("password", passwd);
        return getPooledConnection(prop);
    }

    public PooledConnection getPooledConnection(Properties info) throws SQLException {
        try {
            String url = getURL();
            String userName = info.getProperty("user");
            String passwd = info.getProperty("password");
            String rmName = null;
            String xaclsstr = null;
            String localXaOpenString = null;
            int localRmid = 0;

            if ((this.useNativeXA)
                    && ((url.startsWith("jdbc:oracle:oci8")) || (url.startsWith("jdbc:oracle:oci")))) {
                long[] ociHandles = { 0L, 0L };

                String tnsentry = null;
                String propNlsLangBackdoor = null;

                synchronized (this) {
                    if (this.tnsEntry != null)
                        tnsentry = this.tnsEntry;
                    else {
                        tnsentry = getTNSEntryFromUrl(url);
                    }

                    if (((tnsentry != null) && (tnsentry.length() == 0))
                            || (tnsentry.startsWith("(DESCRIPTION"))) {
                        DatabaseError.throwSqlException(207);
                    }

                    if (!libraryLoaded) {
                        synchronized (getClass()) {
                            if (!libraryLoaded) {
                                try {
                                    System.loadLibrary("heteroxa10");

                                    libraryLoaded = true;
                                } catch (Error exc) {
                                    if ((TRACE) && (!OracleLog.recursiveTrace)) {
                                        OracleLog.recursiveTrace = true;
                                        OracleLog.xaLogger
                                                .log(
                                                     Level.SEVERE,
                                                     "------ JNI JDBC XA lib not loaded properly. ------",
                                                     this);

                                        OracleLog.recursiveTrace = false;
                                    }

                                    libraryLoaded = false;

                                    throw exc;
                                }

                            }

                        }

                    }

                    if (this.connectionProperties != null) {
                        propNlsLangBackdoor = this.connectionProperties
                                .getProperty("oracle.jdbc.ociNlsLangBackwardCompatible");
                    }

                }

                if ((propNlsLangBackdoor != null) && (propNlsLangBackdoor.equalsIgnoreCase("true"))) {
                    short driverCharSetId = T2CConnection.getDriverCharSetIdFromNLS_LANG(null);
                    this.driverCharSetIdString = Integer.toString(driverCharSetId);
                } else if (!tnsentry.equals(this.oldTnsEntry)) {
                    short driverCharSetId = T2CConnection.getClientCharSetId();

                    this.driverCharSetIdString = Integer.toString(driverCharSetId);
                    this.oldTnsEntry = tnsentry;
                }

                synchronized (this) {
                    rmName = this.databaseName + "HeteroXA" + rmidSeed;

                    this.rmid = (localRmid = rmidSeed);

                    synchronized (getClass()) {
                        rmidSeed = (rmidSeed + 1) % 65536;
                    }

                    int trcLevel = 0;

                    if (OracleLog.TRACE) {
                        trcLevel = 7;

                        OracleLog.print(this, 256, 2, 32, "getXAConnection(user, pwd): trcLevel = "
                                + trcLevel);
                    }

                    String l_xaLoose = this.connectionProperties != null ? this.connectionProperties
                            .getProperty("oracle.jdbc.XATransLoose")
                            : null;

                    this.xaOpenString = (localXaOpenString = generateXAOpenString(
                                                                                  rmName,
                                                                                  tnsentry,
                                                                                  userName,
                                                                                  passwd,
                                                                                  60,
                                                                                  2000,
                                                                                  true,
                                                                                  true,
                                                                                  ".",
                                                                                  trcLevel,
                                                                                  false,
                                                                                  (l_xaLoose != null)
                                                                                          && (l_xaLoose
                                                                                                  .equalsIgnoreCase("true")),
                                                                                  this.driverCharSetIdString,
                                                                                  this.driverCharSetIdString));

                    xaclsstr = generateXACloseString(rmName, false);
                }

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.FINER,
                                           "getPooledConnection(user, pwd): before doXaOpen()",
                                           this);

                    OracleLog.recursiveTrace = false;
                }

                int status = doXaOpen(localXaOpenString, localRmid, 0, 0);

                if (status != 0) {
                    DatabaseError.throwSqlException(-1 * status);
                }

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger
                            .log(Level.FINER,
                                 "getPooledConnection(user, pwd): before convertOciHandles()", this);

                    OracleLog.recursiveTrace = false;
                }

                status = convertOciHandles(rmName, ociHandles);

                if (status != 0) {
                    DatabaseError.throwSqlException(-1 * status);
                }

                info.put("OCISvcCtxHandle", String.valueOf(ociHandles[0]));
                info.put("OCIEnvHandle", String.valueOf(ociHandles[1]));
                info.put("JDBCDriverCharSetId", this.driverCharSetIdString);

                if (this.loginTimeout != 0) {
                    info.put("oracle.net.CONNECT_TIMEOUT", "" + this.loginTimeout * 1000);
                }

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger
                            .log(Level.FINER,
                                 "getPooledConnection(user, pwd): before getConnection(url, info)",
                                 this);

                    OracleLog.recursiveTrace = false;
                }

                Connection conn = this.driver.connect(getURL(), info);

                OracleXAHeteroConnection xaconn = new OracleXAHeteroConnection(conn);

                xaconn.setUserName(userName, passwd.toUpperCase());
                xaconn.setRmid(localRmid);
                xaconn.setXaCloseString(xaclsstr);
                xaconn.registerCloseCallback(new OracleXAHeteroCloseCallback(), xaconn);

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.xaLogger.log(Level.FINE,
                                           "OracleXADataSource.getPooledConnection(user, pwd) return: "
                                                   + xaconn, this);

                    OracleLog.recursiveTrace = false;
                }

                return xaconn;
            }
            if ((this.thinUseNativeXA) && (url.startsWith("jdbc:oracle:thin"))) {
                Properties prop = new Properties();
                synchronized (this) {
                    synchronized (getClass()) {
                        rmidSeed = (rmidSeed + 1) % 65536;
                    }

                    this.rmid = rmidSeed;

                    if (this.connectionProperties == null) {
                        this.connectionProperties = new Properties();
                    }
                    this.connectionProperties
                            .put("RessourceManagerId", Integer.toString(this.rmid));
                    if (userName != null)
                        prop.setProperty("user", userName);
                    if (passwd != null)
                        prop.setProperty("password", passwd);
                    prop.setProperty("stmt_cache_size", "" + this.maxStatements);

                    prop.setProperty("ImplicitStatementCachingEnabled", ""
                            + this.implicitCachingEnabled);

                    prop.setProperty("ExplicitStatementCachingEnabled", ""
                            + this.explicitCachingEnabled);

                    prop.setProperty("LoginTimeout", "" + this.loginTimeout);
                }

                T4CXAConnection conn = new T4CXAConnection(super.getPhysicalConnection(prop));

                conn.setUserName(userName, passwd.toUpperCase());

                String l_xaLoose = this.connectionProperties != null ? this.connectionProperties
                        .getProperty("oracle.jdbc.XATransLoose") : null;

                conn.isXAResourceTransLoose = ((l_xaLoose != null) && ((l_xaLoose.equals("true")) || (l_xaLoose
                        .equalsIgnoreCase("true"))));

                return conn;
            }

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.FINE,
                             "OracleXADataSource.getPooledConnection(user, pwd) return: non-oci",
                             this);

                OracleLog.recursiveTrace = false;
            }

            Properties prop = new Properties();
            synchronized (this) {
                if (userName != null)
                    prop.setProperty("user", userName);
                if (passwd != null)
                    prop.setProperty("password", passwd);
                prop.setProperty("stmt_cache_size", "" + this.maxStatements);

                prop.setProperty("ImplicitStatementCachingEnabled", ""
                        + this.implicitCachingEnabled);

                prop.setProperty("ExplicitStatementCachingEnabled", ""
                        + this.explicitCachingEnabled);

                prop.setProperty("LoginTimeout", "" + this.loginTimeout);
            }

            OracleXAConnection l_xaconn = new OracleXAConnection(super.getPhysicalConnection(prop));

            l_xaconn.setUserName(userName, passwd.toUpperCase());

            String l_xaLoose = this.connectionProperties != null ? this.connectionProperties
                    .getProperty("oracle.jdbc.XATransLoose") : null;

            l_xaconn.isXAResourceTransLoose = ((l_xaLoose != null) && ((l_xaLoose.equals("true")) || (l_xaLoose
                    .equalsIgnoreCase("true"))));

            return l_xaconn;
        } catch (XAException xae) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger.log(Level.SEVERE,
                                       "getPooledConnection(user, pwd): XAException at end " + xae,
                                       this);

                OracleLog.recursiveTrace = false;
            }

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.xaLogger
                        .log(Level.FINE,
                             "OracleXADataSource.getPooledConnection(user, pwd) return: null", this);

                OracleLog.recursiveTrace = false;
            }

        }

        return null;
    }

    private native int doXaOpen(String paramString, int paramInt1, int paramInt2, int paramInt3);

    private native int convertOciHandles(String paramString, long[] paramArrayOfLong);

    synchronized void setRmid(int rmid) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.setRmid(rmid = " + rmid + ")",
                                   this);

            OracleLog.recursiveTrace = false;
        }

        this.rmid = rmid;
    }

    synchronized int getRmid() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.getRmid() return: " + this.rmid,
                                   this);

            OracleLog.recursiveTrace = false;
        }

        return this.rmid;
    }

    synchronized void setXaOpenString(String xaOpenString) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.setXaOpenString(xaOpenString = "
                    + xaOpenString + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.xaOpenString = xaOpenString;
    }

    synchronized String getXaOpenString() {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.getXaOpenString() return: "
                    + this.xaOpenString, this);

            OracleLog.recursiveTrace = false;
        }

        return this.xaOpenString;
    }

    private String generateXAOpenString(String rmName, String connectString, String userName,
            String passwd, int lockTimeout, int sessionTimeout, boolean threadFlag,
            boolean objectFlag, String logDir, int traceLevel, boolean cacheFlag, boolean isLoose,
            String dbCsId, String dbNCharCsId) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.generateXAOpenString()", this);

            OracleLog.recursiveTrace = false;
        }

        return new String("ORACLE_XA+DB=" + rmName + "+ACC=P/" + userName + "/" + passwd
                + "+SESTM=" + sessionTimeout + "+SESWT=" + lockTimeout + "+LOGDIR=" + logDir
                + "+SQLNET=" + connectString + (threadFlag ? "+THREADS=true" : "")
                + (objectFlag ? "+OBJECTS=true" : "") + "+DBGFL=0x" + traceLevel
                + (cacheFlag ? "+CONNCACHE=t" : "+CONNCACHE=f")
                + (isLoose ? "+Loose_Coupling=t" : "") + "+CharSet=" + dbCsId + "+NCharSet="
                + dbNCharCsId);
    }

    private String generateXACloseString(String rmName, boolean cacheFlag) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.generateXACloseString()", this);

            OracleLog.recursiveTrace = false;
        }

        return new String("ORACLE_XA+DB=" + rmName + (cacheFlag ? "+CONNCACHE=t" : "+CONNCACHE=f"));
    }

    private String getTNSEntryFromUrl(String url) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.xaLogger.log(Level.FINE, "OracleXADataSource.getTNSEntryFromUrl()", this);

            OracleLog.recursiveTrace = false;
        }

        int at_sign = url.indexOf('@');

        return url.substring(at_sign + 1);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.xa.client.OracleXADataSource"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.xa.client.OracleXADataSource JD-Core Version: 0.6.0
 */