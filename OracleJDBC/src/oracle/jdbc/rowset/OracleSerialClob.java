package oracle.jdbc.rowset;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringBufferInputStream;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class OracleSerialClob implements Clob, Serializable, Cloneable {
    private char[] buffer;
    private long length;

    public OracleSerialClob(char[] chars) throws SQLException {
        this.length = chars.length;
        this.buffer = new char[(int) this.length];
        for (int i = 0; i < this.length; i++)
            this.buffer[i] = chars[i];
    }

    public OracleSerialClob(Clob clob) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleSerialClob.OracleSerialClob(" + clob + ")");
        }

        this.length = clob.length();
        this.buffer = new char[(int) this.length];
        BufferedReader bufferedreader = new BufferedReader(clob.getCharacterStream());
        try {
            int bytesRead = 0;
            int offset = 0;
            do {
                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 16,
                                    "OracleSerialClob.OracleSerialClob(Clob)inside do-while loop, bytesRead="
                                            + bytesRead + ", offset=" + offset);
                }

                bytesRead = bufferedreader.read(this.buffer, offset, (int) (this.length - offset));

                offset += bytesRead;
            } while (bytesRead > 0);
        } catch (IOException ioexception) {
            throw new SQLException("SerialClob: " + ioexception.getMessage());
        }
    }

    public InputStream getAsciiStream() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialClob.getAsciiStream()");
        }

        return new StringBufferInputStream(new String(this.buffer));
    }

    public Reader getCharacterStream() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialClob.getCharacterStream()");
        }

        return new CharArrayReader(this.buffer);
    }

    public String getSubString(long l, int i) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialClob.getSubString(" + l + ", " + i + ")");

            OracleLog.print(this, 1, 256, 32, "OracleSerialClob.getSubString(" + l + ", " + i
                    + "), length=" + this.length);
        }

        if ((l < 0L) || (i > this.length) || (l + i > this.length)) {
            throw new SQLException("Invalid Arguments");
        }
        return new String(this.buffer, (int) l, i);
    }

    public long length() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialClob.length(), return " + this.length);
        }

        return this.length;
    }

    public long position(String s, long l) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialClob.position(" + s + ", " + l + ")");

            OracleLog.print(this, 1, 256, 32, "OracleSerialClob.position(" + s + ", " + l
                    + "), length=" + this.length);
        }

        if ((l < 0L) || (l > this.length) || (l + s.length() > this.length))
            throw new SQLException("Invalid Arguments");
        char[] ac = s.toCharArray();
        int i = (int) (l - 1L);
        boolean flag = false;
        long l2 = ac.length;

        if ((l < 0L) || (l > this.length)) {
            return -1L;
        }
        while (i < this.length) {
            int j = 0;
            long l1 = i + 1;
            while (ac[(j++)] == this.buffer[(i++)]) {
                if (j == l2)
                    return l1;
            }
        }
        return -1L;
    }

    public long position(Clob clob, long l) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialClob.position(clob, " + l + ")");

            OracleLog.print(this, 1, 256, 32, "OracleSerialClob.position(" + clob + ", " + l + ")");
        }

        return position(clob.getSubString(0L, (int) clob.length()), l);
    }

    public int setString(long pos, String str) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return -1;
    }

    public int setString(long pos, String str, int offset, int len) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return -1;
    }

    public OutputStream setAsciiStream(long pos) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return null;
    }

    public Writer setCharacterStream(long pos) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return null;
    }

    public void truncate(long len) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleSerialClob JD-Core Version: 0.6.0
 */