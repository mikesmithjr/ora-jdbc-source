package oracle.jdbc.oracore;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class Util {
    private static int[] ldsRoundTable = { 0, 1, 0, 2, 0, 0, 0, 3, 0 };

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    static void checkNextByte(InputStream in, byte value) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "Util.check_next_byte( inputStream = " + in
                    + ", value = " + value + ")");

            OracleLog.recursiveTrace = false;
        }

        try {
            if (in.read() != value) {
                DatabaseError.throwSqlException(47, "parseTDS");
            }
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }
    }

    public static int[] toJavaUnsignedBytes(byte[] array) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "Util.toJavaUnsignedBytes( bytes = " + array
                    + ")");

            OracleLog.recursiveTrace = false;
        }

        int[] result = new int[array.length];

        for (int i = 0; i < array.length; i++) {
            array[i] &= 255;
        }
        return result;
    }

    static byte[] readBytes(InputStream in, int length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "Util.read_bytes( input stream = " + in
                    + ", length = " + length + ")");

            OracleLog.recursiveTrace = false;
        }

        byte[] array = new byte[length];
        try {
            int length_read = in.read(array);

            if (length_read != length) {
                byte[] final_bytes = new byte[length_read];

                System.arraycopy(array, 0, final_bytes, 0, length_read);

                return final_bytes;
            }
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }

        return array;
    }

    static void writeBytes(OutputStream out, byte[] array) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "Util.write_bytes( output stream = " + out
                    + ", bytes = " + array + ")");

            OracleLog.recursiveTrace = false;
        }

        try {
            out.write(array);
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }
    }

    static void skipBytes(InputStream in, int byte_num) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINEST, "Util.skip_bytes( input stream = " + in
                    + ", bytes to skip = " + byte_num + ")");

            OracleLog.recursiveTrace = false;
        }

        try {
            in.skip(byte_num);
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }
    }

    static long readLong(InputStream in) throws SQLException {
        byte[] bytes = new byte[4];
        try {
            in.read(bytes);

            return (((bytes[0] & 0xFF) * 256 + (bytes[1] & 0xFF)) * 256 + (bytes[2] & 0xFF)) * 256
                    + (bytes[3] & 0xFF);
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }

        return 0L;
    }

    static short readShort(InputStream in) throws SQLException {
        byte[] bytes = new byte[2];
        try {
            in.read(bytes);

            return (short) ((bytes[0] & 0xFF) * 256 + (bytes[1] & 0xFF));
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }

        return 0;
    }

    static byte readByte(InputStream in) throws SQLException {
        try {
            return (byte) in.read();
        } catch (IOException ex) {
            DatabaseError.throwSqlException(ex);
        }

        return 0;
    }

    static byte fdoGetSize(byte[] FDO, int mapping_offset) {
        byte value = fdoGetEntry(FDO, mapping_offset);

        return (byte) (value >> 3 & 0x1F);
    }

    static byte fdoGetAlign(byte[] FDO, int mapping_offset) {
        byte value = fdoGetEntry(FDO, mapping_offset);

        return (byte) (value & 0x7);
    }

    static int ldsRound(int size, int alignvalue) {
        int sval = ldsRoundTable[alignvalue];

        return (size >> sval) + 1 << sval;
    }

    private static byte fdoGetEntry(byte[] FDO, int mapping_offset) {
        short fdo_5 = getUnsignedByte(FDO[5]);
        byte value = FDO[(6 + fdo_5 + mapping_offset)];

        return value;
    }

    public static short getUnsignedByte(byte b) {
        return (short) (b & 0xFF);
    }

    public static byte[] serializeObject(Object obj) throws IOException {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        ObjectOutputStream p = new ObjectOutputStream(ostream);

        p.writeObject(obj);
        p.flush();

        return ostream.toByteArray();
    }

    public static Object deserializeObject(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null) {
            return null;
        }
        InputStream istream = new ByteArrayInputStream(bytes);

        return new ObjectInputStream(istream).readObject();
    }

    public static void printByteArray(byte[] x) {
        System.out.println("DONT CALL THIS -- oracle.jdbc.oracore.Util.printByteArray");
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.Util"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.Util JD-Core Version: 0.6.0
 */