package oracle.jdbc.rowset;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetReader;
import oracle.jdbc.driver.OracleDriver;
import oracle.jdbc.driver.OracleLog;

public class OracleCachedRowSetReader implements RowSetReader, Serializable {
    static final transient int UNICODESTREAM = 273;
    static final transient int BINARYSTREAM = 546;
    static final transient int ASCIISTREAM = 819;
    static final transient int TWO_PARAMETERS = 2;
    static final transient int THREE_PARAMETERS = 3;
    private static transient boolean driverManagerInitialized = false;

    Connection getConnection(RowSetInternal rowsetInternal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSetReader.getConnection()");
        }

        Connection con = null;

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSetReader.getConnection(RowSetInternal),con=" + con);
        }

        Connection rowsetConnection = rowsetInternal.getConnection();
        if ((rowsetConnection != null) && (!rowsetConnection.isClosed())) {
            con = rowsetConnection;
        } else if (((RowSet) rowsetInternal).getDataSourceName() != null) {
            try {
                InitialContext initialcontext = null;

                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleCachedRowSetReader.getConnection(), initialcontext="
                                            + initialcontext);
                }

                try {
                    Properties props = System.getProperties();
                    initialcontext = new InitialContext(props);
                } catch (SecurityException ea) {
                }
                if (initialcontext == null)
                    initialcontext = new InitialContext();
                DataSource datasource = (DataSource) initialcontext
                        .lookup(((RowSet) rowsetInternal).getDataSourceName());

                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleCachedRowSetReader.getConnection(),datasource="
                                            + datasource);
                }

                String userName = ((RowSet) rowsetInternal).getUsername();
                String password = ((RowSet) rowsetInternal).getPassword();
                if ((userName == null) && (password == null)) {
                    con = datasource.getConnection();
                } else {
                    con = datasource.getConnection(userName, password);
                }
            } catch (NamingException ea) {
                throw new SQLException("Unable to connect through the DataSource\n"
                        + ea.getMessage());
            }

        } else if (((RowSet) rowsetInternal).getUrl() != null) {
            if (!driverManagerInitialized) {
                DriverManager.registerDriver(new OracleDriver());
                driverManagerInitialized = true;
            }
            String url = ((RowSet) rowsetInternal).getUrl();
            String userName = ((RowSet) rowsetInternal).getUsername();
            String password = ((RowSet) rowsetInternal).getPassword();

            if (OracleLog.TRACE) {
                OracleLog.print(this, 1, 256, 64,
                                "OracleCachedRowSetReader.getConnection(): url = " + url
                                        + ", userName = " + userName + ", password = " + password);
            }

            if ((url.equals("")) || (userName.equals("")) || (password.equals(""))) {
                throw new SQLException("One or more of the authenticating parameter not set");
            }

            con = DriverManager.getConnection(url, userName, password);
        }

        return con;
    }

    private void setParams(Object[] paramObject, PreparedStatement preparedStatement)
            throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleCachedRowSetReader.setParams(" + paramObject
                    + ", " + preparedStatement + ")");
        }

        for (int j = 0; j < paramObject.length; j++) {
            int i = 0;
            try {
                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleCachedRowSetReader.setParams(Object[], PreparedStatement), inside for-loop, i="
                                            + i);
                }

                i = Array.getLength(paramObject[j]);
            } catch (IllegalArgumentException ex) {
                preparedStatement.setObject(j + 1, paramObject[j]);
                continue;
            }

            Object[] tmpParmObj = (Object[]) paramObject[j];

            if (OracleLog.TRACE) {
                OracleLog
                        .print(
                               this,
                               1,
                               256,
                               64,
                               "OracleCachedRowSetReader.setParams(Object[], PreparedStatement), inside for-loop, Object tmpParmObj="
                                       + tmpParmObj);
            }

            if (i == 2) {
                if (tmpParmObj[0] == null) {
                    preparedStatement.setNull(j + 1, ((Integer) tmpParmObj[1]).intValue());
                } else if (((tmpParmObj[0] instanceof Date)) || ((tmpParmObj[0] instanceof Time))
                        || ((tmpParmObj[0] instanceof Timestamp))) {
                    if ((tmpParmObj[1] instanceof Calendar)) {
                        preparedStatement.setDate(j + 1, (Date) tmpParmObj[0],
                                                  (Calendar) tmpParmObj[1]);
                    } else {
                        throw new SQLException("Unable to deduce param type");
                    }

                } else if ((tmpParmObj[0] instanceof Reader)) {
                    preparedStatement.setCharacterStream(j + 1, (Reader) tmpParmObj[0],
                                                         ((Integer) tmpParmObj[1]).intValue());
                } else if ((tmpParmObj[1] instanceof Integer)) {
                    preparedStatement.setObject(j + 1, tmpParmObj[0], ((Integer) tmpParmObj[1])
                            .intValue());
                }
            } else {
                if (i != 3)
                    continue;
                if (tmpParmObj[0] == null) {
                    preparedStatement.setNull(j + 1, ((Integer) tmpParmObj[1]).intValue(),
                                              (String) tmpParmObj[2]);
                } else if ((tmpParmObj[0] instanceof InputStream)) {
                    switch (((Integer) tmpParmObj[2]).intValue()) {
                    case 273:
                        preparedStatement.setUnicodeStream(j + 1, (InputStream) tmpParmObj[0],
                                                           ((Integer) tmpParmObj[1]).intValue());
                    case 546:
                        preparedStatement.setBinaryStream(j + 1, (InputStream) tmpParmObj[0],
                                                          ((Integer) tmpParmObj[1]).intValue());
                    case 819:
                        preparedStatement.setAsciiStream(j + 1, (InputStream) tmpParmObj[0],
                                                         ((Integer) tmpParmObj[1]).intValue());
                    }

                    throw new SQLException("Unable to deduce parameter type");
                }
                if (((tmpParmObj[1] instanceof Integer)) && ((tmpParmObj[2] instanceof Integer))) {
                    preparedStatement.setObject(j + 1, tmpParmObj[0], ((Integer) tmpParmObj[1])
                            .intValue(), ((Integer) tmpParmObj[2]).intValue());
                } else {
                    throw new SQLException("Unable to deduce param type");
                }
            }
        }
    }

    public synchronized void readData(RowSetInternal rowsetInternal) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleCachedRowSetReader.readData(" + rowsetInternal
                    + ")");
        }

        OracleCachedRowSet rowset = (OracleCachedRowSet) rowsetInternal;

        Connection connection = getConnection(rowsetInternal);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleCachedRowSetReader.readData(), connection="
                    + connection);
        }

        if ((connection == null) || (rowset.getCommand() == null))
            throw new SQLException("Sorry, Could not obtain connection");
        try {
            connection.setTransactionIsolation(rowset.getTransactionIsolation());
        } catch (Exception ex) {
        }
        PreparedStatement preparedStatement = connection.prepareStatement(rowset.getCommand());

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64,
                            "OracleCachedRowSetReader.readData(), preparedStatement="
                                    + preparedStatement);
        }

        setParams(rowsetInternal.getParams(), preparedStatement);
        try {
            preparedStatement.setMaxRows(rowset.getMaxRows());
            preparedStatement.setMaxFieldSize(rowset.getMaxFieldSize());
            preparedStatement.setEscapeProcessing(rowset.getEscapeProcessing());
            preparedStatement.setQueryTimeout(rowset.getQueryTimeout());
        } catch (Exception ex) {
        }
        ResultSet resultset = preparedStatement.executeQuery();
        rowset.populate(resultset);
        resultset.close();
        preparedStatement.close();
        try {
            connection.commit();
        } catch (SQLException ex) {
        }
        if (!rowset.isConnectionStayingOpen()) {
            connection.close();
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleCachedRowSetReader JD-Core Version: 0.6.0
 */