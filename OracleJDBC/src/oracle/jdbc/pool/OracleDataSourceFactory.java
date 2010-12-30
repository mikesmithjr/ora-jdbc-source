package oracle.jdbc.pool;

import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;
import oracle.jdbc.xa.client.OracleXADataSource;

public class OracleDataSourceFactory implements ObjectFactory {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:55_PDT_2005";

    public Object getObjectInstance(Object refObj, Name name, Context nameCtx, Hashtable env)
            throws Exception {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSourceFactory.getObjectInstance(refObj=" + refObj
                                             + ", name=" + name + ", nameCtx=" + nameCtx + ", env="
                                             + env + ")", this);

            OracleLog.recursiveTrace = false;
        }

        Reference ref = (Reference) refObj;
        OracleDataSource ods = null;
        String cn = ref.getClassName();
        Properties info = new Properties();

        if ((cn.equals("oracle.jdbc.pool.OracleDataSource"))
                || (cn.equals("oracle.jdbc.xa.client.OracleXADataSource"))) {
            if (cn.equals("oracle.jdbc.pool.OracleDataSource"))
                ods = new OracleDataSource();
            else {
                ods = new OracleXADataSource();
            }
            StringRefAddr st = null;

            if ((st = (StringRefAddr) ref.get("connectionCachingEnabled")) != null) {
                String isCacheEnabled = (String) st.getContent();

                if (isCacheEnabled.equals(String.valueOf("true"))) {
                    ods.setConnectionCachingEnabled(true);
                }
            }
            if ((st = (StringRefAddr) ref.get("connectionCacheName")) != null) {
                ods.setConnectionCacheName((String) st.getContent());
            }

            if ((st = (StringRefAddr) ref.get("connectionCacheProperties")) != null) {
                String val = (String) st.getContent();
                Properties props = extractConnectionCacheProperties(val);

                ods.setConnectionCacheProperties(props);
            }

            if ((st = (StringRefAddr) ref.get("fastConnectionFailoverEnabled")) != null) {
                String isFailoverEnabled = (String) st.getContent();

                if (isFailoverEnabled.equals(String.valueOf("true"))) {
                    ods.setFastConnectionFailoverEnabled(true);
                }
            }
            if ((st = (StringRefAddr) ref.get("onsConfigStr")) != null) {
                ods.setONSConfiguration((String) st.getContent());
            }
        } else if (cn.equals("oracle.jdbc.pool.OracleConnectionPoolDataSource")) {
            ods = new OracleConnectionPoolDataSource();
        } else if (cn.equals("oracle.jdbc.pool.OracleConnectionCacheImpl")) {
            ods = new OracleConnectionCacheImpl();
        } else if (cn.equals("oracle.jdbc.pool.OracleOCIConnectionPool")) {
            ods = new OracleOCIConnectionPool();

            String pn_conn_min_limit = null;
            String pn_conn_max_limit = null;
            String pn_conn_increment = null;
            String pn_conn_active_size = null;
            String pn_conn_pool_size = null;
            String pn_conn_timeout = null;
            String pn_conn_nowait = null;
            StringRefAddr st = null;

            String pn = null;
            String pn_txn_dist = null;

            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_MIN_LIMIT)) != null) {
                pn_conn_min_limit = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_MAX_LIMIT)) != null) {
                pn_conn_max_limit = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_INCREMENT)) != null) {
                pn_conn_increment = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_ACTIVE_SIZE)) != null) {
                pn_conn_active_size = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_POOL_SIZE)) != null) {
                pn_conn_pool_size = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_TIMEOUT)) != null) {
                pn_conn_timeout = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_NOWAIT)) != null) {
                pn_conn_nowait = (String) st.getContent();
            }
            if ((st = (StringRefAddr) ref.get("transactions_distributed")) != null) {
                pn_txn_dist = (String) st.getContent();
            }

            info.put(OracleOCIConnectionPool.CONNPOOL_MIN_LIMIT, pn_conn_min_limit);
            info.put(OracleOCIConnectionPool.CONNPOOL_MAX_LIMIT, pn_conn_max_limit);
            info.put(OracleOCIConnectionPool.CONNPOOL_INCREMENT, pn_conn_increment);
            info.put(OracleOCIConnectionPool.CONNPOOL_ACTIVE_SIZE, pn_conn_active_size);

            info.put(OracleOCIConnectionPool.CONNPOOL_POOL_SIZE, pn_conn_pool_size);
            info.put(OracleOCIConnectionPool.CONNPOOL_TIMEOUT, pn_conn_timeout);

            if (pn_conn_nowait == "true") {
                info.put(OracleOCIConnectionPool.CONNPOOL_NOWAIT, pn_conn_nowait);
            }
            if (pn_txn_dist == "true") {
                info.put("transactions_distributed", pn_txn_dist);
            }

        } else {
            return null;
        }
        if (ods != null) {
            StringRefAddr st = null;

            if ((st = (StringRefAddr) ref.get("url")) != null) {
                ods.setURL((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("userName")) != null)
                    || ((st = (StringRefAddr) ref.get("u")) != null)
                    || ((st = (StringRefAddr) ref.get("user")) != null)) {
                ods.setUser((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("passWord")) != null)
                    || ((st = (StringRefAddr) ref.get("password")) != null)) {
                ods.setPassword((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("description")) != null)
                    || ((st = (StringRefAddr) ref.get("describe")) != null)) {
                ods.setDescription((String) st.getContent());
            }
            if (((st = (StringRefAddr) ref.get("driverType")) != null)
                    || ((st = (StringRefAddr) ref.get("driver")) != null)) {
                ods.setDriverType((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("serverName")) != null)
                    || ((st = (StringRefAddr) ref.get("host")) != null)) {
                ods.setServerName((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("databaseName")) != null)
                    || ((st = (StringRefAddr) ref.get("sid")) != null)) {
                ods.setDatabaseName((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("networkProtocol")) != null)
                    || ((st = (StringRefAddr) ref.get("protocol")) != null)) {
                ods.setNetworkProtocol((String) st.getContent());
            }

            if (((st = (StringRefAddr) ref.get("portNumber")) != null)
                    || ((st = (StringRefAddr) ref.get("port")) != null)) {
                String pn = (String) st.getContent();

                ods.setPortNumber(Integer.parseInt(pn));
            }

            if (((st = (StringRefAddr) ref.get("tnsentryname")) != null)
                    || ((st = (StringRefAddr) ref.get("tns")) != null)) {
                ods.setTNSEntryName((String) st.getContent());
            }

            if (cn.equals("oracle.jdbc.pool.OracleConnectionCacheImpl")) {
                String pn = null;

                if ((st = (StringRefAddr) ref.get("minLimit")) != null) {
                    pn = (String) st.getContent();

                    ((OracleConnectionCacheImpl) ods).setMinLimit(Integer.parseInt(pn));
                }

                if ((st = (StringRefAddr) ref.get("maxLimit")) != null) {
                    pn = (String) st.getContent();

                    ((OracleConnectionCacheImpl) ods).setMaxLimit(Integer.parseInt(pn));
                }

                if ((st = (StringRefAddr) ref.get("cacheScheme")) != null) {
                    pn = (String) st.getContent();

                    ((OracleConnectionCacheImpl) ods).setCacheScheme(Integer.parseInt(pn));
                }

            } else if (cn.equals("oracle.jdbc.pool.OracleOCIConnectionPool")) {
                String pn_is_poolCreated = null;

                if ((st = (StringRefAddr) ref.get(OracleOCIConnectionPool.CONNPOOL_IS_POOLCREATED)) != null) {
                    pn_is_poolCreated = (String) st.getContent();
                }
                if (pn_is_poolCreated.equals(String.valueOf("true"))) {
                    ((OracleOCIConnectionPool) ods).setPoolConfig(info);
                }

            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.poolLogger.log(Level.FINE,
                                     "OracleDataSourceFactory.getObjectInstance(refObj): returned "
                                             + ods, this);

            OracleLog.recursiveTrace = false;
        }

        return ods;
    }

    private Properties extractConnectionCacheProperties(String val) throws SQLException {
        Properties cprops = new Properties();

        val = val.substring(1, val.length() - 1);

        int awIdx = val.indexOf("AttributeWeights", 0);

        if (awIdx >= 0) {
            if ((val.charAt(awIdx + 16) != '=') || ((awIdx > 0) && (val.charAt(awIdx - 1) != ' '))) {
                DatabaseError.throwSqlException(139);
            }

            Properties awProps = new Properties();
            int awLastIdx = val.indexOf("}", awIdx);
            String tmpawStr = val.substring(awIdx, awLastIdx);

            String awStr = tmpawStr.substring(18);

            StringTokenizer strTokens1 = new StringTokenizer(awStr, ", ");

            synchronized (strTokens1) {
                while (strTokens1.hasMoreTokens()) {
                    String strTkn = strTokens1.nextToken();
                    int len = strTkn.length();
                    int equalsIndex = strTkn.indexOf("=");

                    String key = strTkn.substring(0, equalsIndex);
                    String value = strTkn.substring(equalsIndex + 1, len);

                    awProps.setProperty(key, value);
                }
            }

            cprops.put("AttributeWeights", awProps);

            if ((awIdx > 0) && (awLastIdx + 1 == val.length())) {
                val = val.substring(0, awIdx - 2);
            } else if ((awIdx > 0) && (awLastIdx + 1 < val.length())) {
                String valA = val.substring(0, awIdx - 2);
                String valB = val.substring(awLastIdx + 1, val.length());

                val = valA.concat(valB);
            } else {
                val = val.substring(awLastIdx + 2, val.length());
            }

        }

        StringTokenizer strTokens2 = new StringTokenizer(val, ", ");

        synchronized (strTokens2) {
            while (strTokens2.hasMoreTokens()) {
                String strTkn = strTokens2.nextToken();
                int len = strTkn.length();
                int equalsIndex = strTkn.indexOf("=");

                String key = strTkn.substring(0, equalsIndex);
                String value = strTkn.substring(equalsIndex + 1, len);

                cprops.setProperty(key, value);
            }
        }

        return cprops;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.pool.OracleDataSourceFactory"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.pool.OracleDataSourceFactory JD-Core Version: 0.6.0
 */