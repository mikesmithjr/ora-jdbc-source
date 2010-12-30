package oracle.jdbc.rowset;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class OracleSerialBlob implements Blob, Serializable, Cloneable {
    private byte[] buffer;
    private long length;

    public OracleSerialBlob(byte[] bytes) throws SQLException {
        this.length = bytes.length;
        this.buffer = new byte[(int) this.length];
        for (int i = 0; i < this.length; i++)
            this.buffer[i] = bytes[i];
    }

    public OracleSerialBlob(Blob blob) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 16, "OracleSerialBlob.OracleSerialBlob(" + blob + ")");
        }

        this.length = blob.length();
        this.buffer = new byte[(int) this.length];
        BufferedInputStream bufferedInputStream = new BufferedInputStream(blob.getBinaryStream());
        try {
            int bytesRead = 0;
            int offset = 0;
            do {
                if (OracleLog.TRACE) {
                    OracleLog.print(this, 1, 256, 64,
                                    "OracleSerialBlob.OracleSerialBlob(Blob), inside do-while loop, bytesRead="
                                            + bytesRead + ", offset=" + offset);
                }

                bytesRead = bufferedInputStream.read(this.buffer, offset,
                                                     (int) (this.length - offset));

                offset += bytesRead;
            } while (bytesRead > 0);
        } catch (IOException ioexception) {
            throw new SQLException("SerialBlob: " + ioexception.getMessage());
        }
    }

    public InputStream getBinaryStream() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialBlob.getBinaryStream()");
        }

        return new ByteArrayInputStream(this.buffer);
    }

    public byte[] getBytes(long l, int i) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialBlob.getBytes(" + l + ", " + i + ")");

            OracleLog.print(this, 1, 256, 64, "OracleSerialBlob.getBytes(" + l + ", " + i
                    + "), length=" + this.length);
        }

        if ((l < 0L) || (i > this.length) || (l + i > this.length)) {
            throw new SQLException("Invalid Arguments");
        }

        byte[] b = new byte[i];
        System.arraycopy(this.buffer, (int) l, b, 0, i);

        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 64, "OracleSerialBlob.getBytes(" + l + ", " + i
                    + "), return " + OracleLog.bytesToPrintableForm("byte array=", b));
        }

        return b;
    }

    public long length() throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialBlob.length()");

            OracleLog.print(this, 1, 256, 16, "OracleSerialBlob.length(), return " + this.length);
        }

        return this.length;
    }

    public long position(byte[] ac, long l) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialBlob.position(byte[] ac, " + l + ")");

            OracleLog.print(this, 1, 256, 64, "OracleSerialBlob.position("
                    + OracleLog.bytesToPrintableForm("byte array=", ac) + ", " + l + "), length="
                    + this.length);
        }

        if ((l < 0L) || (l > this.length) || (l + ac.length > this.length))
            throw new SQLException("Invalid Arguments");
        int i = (int) (l - 1L);
        boolean flag = false;
        long l2 = ac.length;

        if ((l < 0L) || (l > this.length)) {
            return -1L;
        }
        while (i < this.length) {
            int j = 0;
            long l1 = i + 1;
            while (ac[(j++)] == this.buffer[(i++)])
                if (j == l2)
                    return l1;
        }
        return -1L;
    }

    public long position(Blob blob, long l) throws SQLException {
        if (OracleLog.TRACE) {
            OracleLog.print(this, 1, 256, 1, "OracleSerialBlob.position(" + blob + ", " + l + ")");
        }

        return position(blob.getBytes(0L, (int) blob.length()), l);
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return -1;
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return -1;
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
        return null;
    }

    public void truncate(long len) throws SQLException {
        DatabaseError.throwUnsupportedFeatureSqlException();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.rowset.OracleSerialBlob JD-Core Version: 0.6.0
 */