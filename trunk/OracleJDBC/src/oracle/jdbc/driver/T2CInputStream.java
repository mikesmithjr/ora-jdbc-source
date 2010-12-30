package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

class T2CInputStream extends OracleInputStream {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    native int t2cGetBytes(long paramLong, int paramInt1, byte[] paramArrayOfByte1, int paramInt2,
            Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte2, int paramInt3,
            char[] paramArrayOfChar, int paramInt4, short[] paramArrayOfShort, int paramInt5);

    T2CInputStream(OracleStatement stmt, int index, Accessor a) {
        super(stmt, index, a);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CInputStream.<init>(" + stmt + ", " + index
                    + ", " + a + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CInputStream.<init> returns " + this, this);

            OracleLog.recursiveTrace = false;
        }
    }

    T2CInputStream(int index) {
        super(null, index, null);
    }

    public int getBytes() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CInputStream.getBytes(), statement"
                    + this.statement + ", c_state" + this.statement.c_state, this);

            OracleLog.recursiveTrace = false;
        }

        int dataSize = t2cGetBytes(this.statement.c_state, this.columnIndex, this.buf,
                                   this.chunkSize, this.statement.accessors,
                                   this.statement.defineBytes, this.statement.accessorByteOffset,
                                   this.statement.defineChars, this.statement.accessorCharOffset,
                                   this.statement.defineIndicators,
                                   this.statement.accessorShortOffset);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "T2CInputStream.getBytes() returns dataSize = "
                    + dataSize, this);

            OracleLog.recursiveTrace = false;
        }

        if (dataSize == -2) {
            try {
                this.accessor.setNull(this.statement.currentRow);
            } catch (SQLException e) {
                DatabaseError.SQLToIOException(e);
            }

            dataSize = 0;
        }

        if (dataSize <= 0) {
            dataSize = -1;
        }
        return dataSize;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T2CInputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T2CInputStream JD-Core Version: 0.6.0
 */