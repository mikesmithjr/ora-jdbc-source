package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CInputStream extends OracleInputStream {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4CInputStream(OracleStatement stmt, int index, Accessor a) {
        super(stmt, index, a);
    }

    public boolean isNull() throws IOException {
        if (!this.statement.connection.useFetchSizeWithLongColumn) {
            return super.isNull();
        }
        boolean result = false;
        try {
            int currentRow = this.statement.currentRow;

            if (currentRow < 0) {
                currentRow = 0;
            }
            if (currentRow >= this.statement.validRows) {
                return true;
            }
            result = this.accessor.isNull(currentRow);
        } catch (SQLException exc) {
            DatabaseError.SQLToIOException(exc);
        }

        return result;
    }

    public int getBytes() throws IOException {
        int ret = 0;
        try {
            ret = this.accessor.readStream(this.buf, this.chunkSize);
        } catch (SQLException e) {
            throw new IOException(e.getMessage());
        } catch (IOException ea) {
            try {
                ((T4CConnection) this.statement.connection).handleIOException(ea);
            } catch (SQLException eb) {
            }
            throw ea;
        }

        return ret;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CInputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CInputStream JD-Core Version: 0.6.0
 */