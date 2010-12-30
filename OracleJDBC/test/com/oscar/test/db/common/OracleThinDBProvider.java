package com.oscar.test.db.common;

import java.util.Properties;

public class OracleThinDBProvider implements DBProvider {

    public static final String HOSTNAME = "hostname";
    public static final String DBNAME = "dbname";
    public static final String PORT = "port";


    public String buildConnectString(Properties inProperties, Properties outProperties) {
        StringBuilder url = new StringBuilder("jdbc:oracle:thin:@");
        url.append(inProperties.getProperty(HOSTNAME)).append(":")
                .append(inProperties.getProperty(PORT)).append(":")
                .append(inProperties.getProperty(DBNAME));
        
//        outProperties.setProperty(key, value)

        return url.toString();
    }

    public String getClassName() {
        return "oracle.jdbc.driver.OracleDriver";
    }

}
