package com.oscar.test.db;

import java.sql.Connection;
import java.sql.SQLException;

import com.oscar.test.db.common.DBCommonTest;
import com.oscar.test.db.common.OracleThinDBProvider;

public class OracleThinConnTest extends DBCommonTest {

    public OracleThinConnTest() {
        super(new OracleThinDBProvider());
    }

    protected void setUp() throws Exception {
        super.setUp();
        super.setUserName("SYSTEM");
        super.setPassword("system");
        super.setProperty(OracleThinDBProvider.DBNAME, "orcl");
        super.setProperty(OracleThinDBProvider.HOSTNAME, "10.0.4.153");
        super.setProperty(OracleThinDBProvider.PORT, "1521");
    }
    
    public void testConnection() {
        Connection conn;
        try {
            conn = super.getConnection();
            super.releaseConnection(conn, true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            super.fail(e.getLocalizedMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            super.fail(e.getLocalizedMessage());
        }
        
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
