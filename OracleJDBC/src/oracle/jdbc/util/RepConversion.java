package oracle.jdbc.util;

import java.io.PrintStream;
import oracle.jdbc.driver.OracleLog;

public class RepConversion {
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:56_PDT_2005";

    public static void printInHex(byte b) {
        System.out.print((char) nibbleToHex((byte) ((b & 0xF0) >> 4)));
        System.out.print((char) nibbleToHex((byte) (b & 0xF)));
    }

    public static byte nibbleToHex(byte nibble) {
        nibble = (byte) (nibble & 0xF);

        return (byte) (nibble < 10 ? nibble + 48 : nibble - 10 + 65);
    }

    public static byte asciiHexToNibble(byte b) {
        byte value;
        if ((b >= 97) && (b <= 102)) {
            value = (byte) (b - 97 + 10);
        } else {
            if ((b >= 65) && (b <= 70)) {
                value = (byte) (b - 65 + 10);
            } else {
                if ((b >= 48) && (b <= 57)) {
                    value = (byte) (b - 48);
                } else {
                    value = b;
                }

            }

        }

        return value;
    }

    public static void bArray2Nibbles(byte[] array, byte[] nibbles) {
        for (int i = 0; i < array.length; i++) {
            nibbles[(i * 2)] = nibbleToHex((byte) ((array[i] & 0xF0) >> 4));
            nibbles[(i * 2 + 1)] = nibbleToHex((byte) (array[i] & 0xF));
        }
    }

    public static String bArray2String(byte[] array) {
        StringBuffer result = new StringBuffer(array.length * 2);

        for (int i = 0; i < array.length; i++) {
            result.append((char) nibbleToHex((byte) ((array[i] & 0xF0) >> 4)));
            result.append((char) nibbleToHex((byte) (array[i] & 0xF)));
        }

        return result.toString();
    }

    public static byte[] nibbles2bArray(byte[] nibbles) {
        byte[] array = new byte[nibbles.length / 2];

        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) (asciiHexToNibble(nibbles[(i * 2)]) << 4);
            int tmp31_30 = i;
            byte[] tmp31_29 = array;
            tmp31_29[tmp31_30] = (byte) (tmp31_29[tmp31_30] | asciiHexToNibble(nibbles[(i * 2 + 1)]));
        }

        return array;
    }

    public static void printInHex(long value) {
        byte[] hexValue = toHex(value);

        System.out.print(new String(hexValue, 0));
    }

    public static void printInHex(int value) {
        byte[] hexValue = toHex(value);

        System.out.print(new String(hexValue, 0));
    }

    public static void printInHex(short value) {
        byte[] hexValue = toHex(value);

        System.out.print(new String(hexValue, 0));
    }

    public static byte[] toHex(long value) {
        int lsize = 16;
        byte[] hex = new byte[lsize];

        for (int i = lsize - 1; i >= 0; i--) {
            hex[i] = nibbleToHex((byte) (int) (value & 0xF));
            value >>= 4;
        }

        return hex;
    }

    public static byte[] toHex(int value) {
        int lsize = 8;
        byte[] hex = new byte[lsize];

        for (int i = lsize - 1; i >= 0; i--) {
            hex[i] = nibbleToHex((byte) (value & 0xF));
            value >>= 4;
        }

        return hex;
    }

    public static byte[] toHex(short value) {
        int lsize = 4;
        byte[] hex = new byte[lsize];

        for (int i = lsize - 1; i >= 0; i--) {
            hex[i] = nibbleToHex((byte) (value & 0xF));
            value = (short) (value >> 4);
        }

        return hex;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.util.RepConversion"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.util.RepConversion JD-Core Version: 0.6.0
 */