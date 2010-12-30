package com.oscar.test.db.common;

import java.util.Properties;

public interface DBProvider {
    
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    
    String getClassName();
    
    String buildConnectString(Properties inProperties, Properties outProperties);
}
