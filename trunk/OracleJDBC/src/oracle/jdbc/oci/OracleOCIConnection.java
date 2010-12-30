package oracle.jdbc.oci;

import java.sql.SQLException;
import java.util.Properties;

public class OracleOCIConnection extends oracle.jdbc.driver.OracleOCIConnection {
    public OracleOCIConnection(String url, String username, String password, String database,
            Properties info, Object ext) throws SQLException {
        super(url, username, password, database, info, ext);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oci.OracleOCIConnection JD-Core Version: 0.6.0
 */