package com.oscar.test.db.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.TestCase;

public abstract class DBCommonTest extends TestCase {

    public final static DBProvider DEFAULT_DB_PROVIDER = new OscarDBProvider();

    private DBProvider dbProvider;

    private ArrayList connPool = new ArrayList();

    private Properties properties = new Properties();
    
    private String username;
    
    private String password;

    public DBCommonTest() {
        this(DEFAULT_DB_PROVIDER);
    }

    public DBCommonTest(DBProvider dbProvider) {
        this.dbProvider = dbProvider;
    }

    public synchronized Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection conn = null;
        while (true) {
            if (connPool.size() != 0) {
                conn = (Connection) connPool.get(0);
                if (conn.isClosed()) {
                    connPool.remove(0);
                    continue;
                } else {
                    connPool.remove(0);
                }
            } else {
                conn = this.CreateNewConnection();
            }
            break;
        }
        return conn;
    }

    public synchronized void releaseConnection(Connection conn, boolean close) throws SQLException {
        if (conn != null) {
            if( close ){
                conn.close();
            }else{
                connPool.add(conn);
            }
        }
    }

    private Connection CreateNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName(dbProvider.getClassName());
        Properties connProp = new Properties();
        String url = dbProvider.buildConnectString(properties, connProp);
        Connection conn = null;
        if( connProp.size() != 0 ){
            conn = DriverManager.getConnection(url, connProp);
        }else{
            conn = DriverManager.getConnection(url, username, password);
        }
        return conn;
    }

    public void setProperty(String name, String value) {
        this.properties.setProperty(name, value);
    }

    public void setUserName(String username) {
        this.username = username;
        this.setProperty(DBProvider.USERNAME, username);
    }

    public void setPassword(String password) {
        this.password = password;
        this.setProperty(DBProvider.PASSWORD, password);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        Iterator iterator = connPool.iterator();
        while (iterator.hasNext()) {
            Connection conn = (Connection) iterator.next();
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        super.tearDown();

    }
}
