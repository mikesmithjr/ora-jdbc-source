package oracle.jdbc.driver;

import java.sql.SQLException;
import oracle.jdbc.pool.OraclePooledConnection;

class ClosedConnection extends PhysicalConnection {
    ClosedConnection() {
        this.lifecycle = 4;
    }

    void initializePassword(String p) throws SQLException {
    }

    OracleStatement RefCursorBytesToStatement(byte[] bytes, OracleStatement parent)
            throws SQLException {
        DatabaseError.throwSqlException(8);

        return null;
    }

    int getDefaultStreamChunkSize() {
        return -1;
    }

    short doGetVersionNumber() throws SQLException {
        DatabaseError.throwSqlException(8);

        return -1;
    }

    String doGetDatabaseProductVersion() throws SQLException {
        DatabaseError.throwSqlException(8);

        return null;
    }

    void doRollback() throws SQLException {
        DatabaseError.throwSqlException(8);
    }

    void doCommit() throws SQLException {
        DatabaseError.throwSqlException(8);
    }

    void doSetAutoCommit(boolean autoCommit) throws SQLException {
        DatabaseError.throwSqlException(8);
    }

    void doCancel() throws SQLException {
        DatabaseError.throwSqlException(8);
    }

    void doAbort() throws SQLException {
    }

    void open(OracleStatement stmt) throws SQLException {
        DatabaseError.throwSqlException(8);
    }

    void logon() throws SQLException {
        DatabaseError.throwSqlException(8);
    }

    public void getPropertyForPooledConnection(OraclePooledConnection pc) throws SQLException {
        DatabaseError.throwSqlException(8);
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ClosedConnection JD-Core Version: 0.6.0
 */