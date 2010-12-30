package oracle.jdbc.driver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OracleInputStream extends OracleBufferedStream {
    OracleStatement statement;
    int columnIndex;
    Accessor accessor;
    OracleInputStream nextStream;
    boolean hasBeenOpen = false;

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:51_PDT_2005";

    protected OracleInputStream(OracleStatement stmt, int index, Accessor a) {
        super(stmt.connection.getDefaultStreamChunkSize());

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE,
                                       "OracleInputStream.OracleInputStream(stmt, index)", this);

            OracleLog.recursiveTrace = false;
        }

        this.closed = true;
        this.statement = stmt;
        this.columnIndex = index;
        this.accessor = a;
        this.nextStream = null;

        OracleInputStream s = this.statement.streamList;

        if ((s == null) || (this.columnIndex < s.columnIndex)) {
            this.nextStream = this.statement.streamList;
            this.statement.streamList = this;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "OracleInputStream.OracleInputStream putting " + this
                                                   + "\n\n at head of streamList, in front of "
                                                   + this.nextStream, this);

                OracleLog.recursiveTrace = false;
            }

        } else if (this.columnIndex == s.columnIndex) {
            this.nextStream = s.nextStream;

            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "OracleInputStream.OracleInputStream splicing out " + s
                                                   + "\n\n and putting " + this
                                                   + "\n\n at the head of the list "
                                                   + "\n\n and in front of " + this.nextStream
                                                   + " in streamList", this);

                OracleLog.recursiveTrace = false;
            }

            s.nextStream = null;
            this.statement.streamList = this;
        } else {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.driverLogger.log(Level.FINER,
                                           "OracleInputStream.OracleInputStream going past " + s
                                                   + "\n\n when splicing " + this
                                                   + " into streamList", this);

                OracleLog.recursiveTrace = false;
            }

            while ((s.nextStream != null) && (this.columnIndex > s.nextStream.columnIndex)) {
                s = s.nextStream;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER,
                                               "OracleInputStream.OracleInputStream going past "
                                                       + s + "\n\n when splicing " + this
                                                       + " into streamList", this);

                    OracleLog.recursiveTrace = false;
                }

            }

            if ((s.nextStream != null) && (this.columnIndex == s.nextStream.columnIndex)) {
                this.nextStream = s.nextStream.nextStream;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER,
                                               "OracleInputStream.OracleInputStream splicing out "
                                                       + s.nextStream + "\n\n and putting " + this
                                                       + "\n\n after " + s
                                                       + "\n\n and in front of " + this.nextStream
                                                       + " in streamList", this);

                    OracleLog.recursiveTrace = false;
                }

                s.nextStream.nextStream = null;
                s.nextStream = this;
            } else {
                this.nextStream = s.nextStream;

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER,
                                               "OracleInputStream.OracleInputStream inserting "
                                                       + this + "\n\n after " + s
                                                       + "\n\n and in front of " + this.nextStream
                                                       + " in streamList", this);

                    OracleLog.recursiveTrace = false;
                }

                s.nextStream = this;
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINE, "OracleInputStream.OracleInputStream:return",
                                       this);

            OracleLog.recursiveTrace = false;
        }
    }

    public String toString() {
        return "OIS@" + Integer.toHexString(hashCode()) + "{" + "statement = " + this.statement
                + ", accessor = " + this.accessor + ", nextStream = " + this.nextStream
                + ", columnIndex = " + this.columnIndex + ", hasBeenOpen = " + this.hasBeenOpen
                + "}";
    }

    public boolean needBytes() throws IOException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.driverLogger.log(Level.FINER, "OIS.needBytes: " + this + ", closed = "
                    + this.closed + ", index = " + this.columnIndex, this);

            OracleLog.recursiveTrace = false;
        }

        if (this.closed) {
            return false;
        }
        if (this.pos >= this.count) {
            try {
                int readLength = getBytes();

                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER, "OIS.needBytes: read = " + readLength,
                                               this);

                    OracleLog.recursiveTrace = false;
                }

                this.pos = 0;
                this.count = readLength;

                if (this.count == -1) {
                    if (this.nextStream == null) {
                        this.statement.connection.releaseLine();
                    }
                    this.closed = true;

                    this.accessor.fetchNextColumns();

                    return false;
                }

            } catch (SQLException e) {
                if (TRACE) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);

                    e.printStackTrace(pw);

                    OracleLog.print(this, 1, 16, 64, sw.toString());
                }

                DatabaseError.SQLToIOException(e);
            }
        }

        return true;
    }

    public boolean isNull() throws IOException {
        boolean result = false;
        try {
            result = this.accessor.isNull(0);
        } catch (SQLException exc) {
            DatabaseError.SQLToIOException(exc);
        }

        return result;
    }

    public boolean isClosed() {
        return this.closed;
    }

    public void close() throws IOException {
        if ((!this.closed) && (this.hasBeenOpen)) {
            while (this.statement.nextStream != this) {
                this.statement.nextStream.close();

                this.statement.nextStream = this.statement.nextStream.nextStream;
            }

            if (!isNull()) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.driverLogger.log(Level.FINER, "Closing stream " + this, this);

                    OracleLog.recursiveTrace = false;
                }

                while (needBytes()) {
                    this.pos = this.count;
                }
            }

            this.closed = true;
        }
    }

    public abstract int getBytes() throws IOException;

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.OracleInputStream"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleInputStream JD-Core Version: 0.6.0
 */