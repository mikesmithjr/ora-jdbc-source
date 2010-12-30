// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T4CMAREngine.java

package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Vector;
import oracle.net.ns.BreakNetException;
import oracle.net.ns.Communication;
import oracle.net.ns.NetException;

// Referenced classes of package oracle.jdbc.driver:
// T4CTypeRep, DatabaseError, DBConversion, OracleLog

class T4CMAREngine {

    static final int TTCC_MXL = 252;
    static final int TTCC_ESC = 253;
    static final int TTCC_LNG = 254;
    static final int TTCC_ERR = 255;
    static final int TTCC_MXIN = 64;
    static final byte TTCLXMULTI = 1;
    static final byte TTCLXMCONV = 2;
    T4CTypeRep types;
    Communication net;
    DBConversion conv;
    short versionNumber;
    byte proSvrVer;
    InputStream inStream;
    OutputStream outStream;
    final byte ignored[] = new byte[255];
    final byte tmpBuffer1[] = new byte[1];
    final byte tmpBuffer2[] = new byte[2];
    final byte tmpBuffer3[] = new byte[3];
    final byte tmpBuffer4[] = new byte[4];
    final byte tmpBuffer5[] = new byte[5];
    final byte tmpBuffer6[] = new byte[6];
    final byte tmpBuffer7[] = new byte[7];
    final byte tmpBuffer8[] = new byte[8];
    final int retLen[] = new int[1];
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    static String toHex(long value, int bytes) {
        String result;
        switch (bytes) {
        case 1: // '\001'
            result = "00" + Long.toString(value & 255L, 16);
            break;

        case 2: // '\002'
            result = "0000" + Long.toString(value & 65535L, 16);
            break;

        case 3: // '\003'
            result = "000000" + Long.toString(value & 0xffffffL, 16);
            break;

        case 4: // '\004'
            result = "00000000" + Long.toString(value & 0xffffffffL, 16);
            break;

        case 5: // '\005'
            result = "0000000000" + Long.toString(value & 0xffffffffffL, 16);
            break;

        case 6: // '\006'
            result = "000000000000" + Long.toString(value & 0xffffffffffffL, 16);
            break;

        case 7: // '\007'
            result = "00000000000000" + Long.toString(value & 0xffffffffffffffL, 16);
            break;

        case 8: // '\b'
            return toHex(value >> 32, 4) + toHex(value, 4).substring(2);

        default:
            return "more than 8 bytes";
        }
        return "0x" + result.substring(result.length() - 2 * bytes);
    }

    static String toHex(byte value) {
        String result = "00" + Integer.toHexString(value & 0xff);
        return "0x" + result.substring(result.length() - 2);
    }

    static String toHex(short value) {
        return toHex(value, 2);
    }

    static String toHex(int value) {
        return toHex(value, 4);
    }

    static String toHex(byte value[], int length) {
        if (value == null) {
            return "null";
        }
        if (length > value.length) {
            return "byte array not long enough";
        }
        String result = "[";
        int len = Math.min(64, length);
        for (int i = 0; i < len; i++) {
            result = result + toHex(value[i]) + " ";
        }

        if (len < length) {
            result = result + "...";
        }
        return result + "]";
    }

    static String toHex(byte value[]) {
        if (value == null) {
            return "null";
        } else {
            return toHex(value, value.length);
        }
    }

    T4CMAREngine(Communication net) throws SQLException, IOException {
        versionNumber = -1;
        if (net == null) {
            DatabaseError.throwSqlException(433);
        }
        this.net = net;
        try {
            inStream = net.getInputStream();
            outStream = net.getOutputStream();
        } catch (NetException ne) {
            throw new IOException(ne.getMessage());
        }
        types = new T4CTypeRep();
        types.setRep((byte) 1, (byte) 2);
    }

    void initBuffers() {
    }

    void marshalSB1(byte value) throws IOException {
        outStream.write(value);
    }

    void marshalUB1(short value) throws IOException {
        outStream.write((byte) (value & 0xff));
    }

    void marshalSB2(short value) throws IOException {
        byte bytes = value2Buffer(value, tmpBuffer2, (byte) 1);
        if (bytes != 0) {
            outStream.write(tmpBuffer2, 0, bytes);
        }
    }

    void marshalUB2(int value) throws IOException {
        marshalSB2((short) (value & 0xffff));
    }

    void marshalSB4(int value) throws IOException {
        byte bytes = value2Buffer(value, tmpBuffer4, (byte) 2);
        if (bytes != 0) {
            outStream.write(tmpBuffer4, 0, bytes);
        }
    }

    void marshalUB4(long value) throws IOException {
        marshalSB4((int) (value & -1L));
    }

    void marshalSB8(long value) throws IOException {
        byte bytes = value2Buffer(value, tmpBuffer8, (byte) 3);
        if (bytes != 0) {
            outStream.write(tmpBuffer8, 0, bytes);
        }
    }

    void marshalSWORD(int value) throws IOException {
        marshalSB4(value);
    }

    void marshalUWORD(long value) throws IOException {
        marshalSB4((int) (value & -1L));
    }

    void marshalB1Array(byte value[]) throws IOException {
        if (value.length > 0) {
            outStream.write(value);
        }
    }

    void marshalB1Array(byte value[], int off, int len) throws IOException {
        if (value.length > 0) {
            outStream.write(value, off, len);
        }
    }

    void marshalUB4Array(long value[]) throws IOException {
        for (int i = 0; i < value.length; i++) {
            marshalSB4((int) (value[i] & -1L));
        }

    }

    void marshalO2U(boolean notnull) throws IOException {
        if (notnull) {
            addPtr((byte) 1);
        } else {
            addPtr((byte) 0);
        }
    }

    void marshalNULLPTR() throws IOException {
        addPtr((byte) 0);
    }

    void marshalPTR() throws IOException {
        addPtr((byte) 1);
    }

    void marshalCHR(byte value[]) throws IOException {
        marshalCHR(value, 0, value.length);
    }

    void marshalCHR(byte value[], int offset, int length) throws IOException {
        if (length > 0) {
            if (types.isConvNeeded()) {
                marshalCLR(value, offset, length);
            } else {
                outStream.write(value, offset, length);
            }
        }
    }

    void marshalCLR(byte value[], int valueLen) throws IOException {
        marshalCLR(value, 0, valueLen);
    }

    void marshalCLR(byte value[], int offset, int valueLen) throws IOException {
        if (valueLen > 64) {
            int nbBytesWritten = 0;
            outStream.write(-2);
            do {
                int bytesLeft = valueLen - nbBytesWritten;
                int len = bytesLeft <= 64 ? bytesLeft : 64;
                outStream.write((byte) (len & 0xff));
                outStream.write(value, offset + nbBytesWritten, len);
                nbBytesWritten += len;
            } while (nbBytesWritten < valueLen);
            outStream.write(0);
        } else {
            outStream.write((byte) (valueLen & 0xff));
            if (value.length != 0) {
                outStream.write(value, offset, valueLen);
            }
        }
    }

    void marshalKEYVAL(byte keys[][], int keysSize[], byte values[][], int valuesSize[],
            byte kvalflg[], int nb) throws SQLException, IOException {
        for (int i = 0; i < nb; i++) {
            if (keys[i] != null && keysSize[i] > 0) {
                marshalUB4(keysSize[i]);
                marshalCLR(keys[i], 0, keysSize[i]);
            } else {
                marshalUB4(0L);
            }
            if (values[i] != null && valuesSize[i] > 0) {
                marshalUB4(valuesSize[i]);
                marshalCLR(values[i], 0, valuesSize[i]);
            } else {
                marshalUB4(0L);
            }
            if (kvalflg[i] != 0) {
                marshalUB4(1L);
            } else {
                marshalUB4(0L);
            }
        }

    }

    void marshalKEYVAL(byte keys[][], byte values[][], byte kvalflg[], int nb) throws SQLException,
            IOException {
        int keysSize[] = new int[nb];
        int valuesSize[] = new int[nb];
        for (int i = 0; i < nb; i++) {
            if (keys[i] != null) {
                keysSize[i] = keys[i].length;
            }
            if (values[i] != null) {
                valuesSize[i] = values[i].length;
            }
        }

        marshalKEYVAL(keys, keysSize, values, valuesSize, kvalflg, nb);
    }

    void marshalDALC(byte buffer[]) throws SQLException, IOException {
        if (buffer == null || buffer.length < 1) {
            outStream.write(0);
        } else {
            marshalSB4(-1 & buffer.length);
            marshalCLR(buffer, buffer.length);
        }
    }

    void addPtr(byte value) throws IOException {
        if ((types.rep[4] & 1) > 0) {
            outStream.write(value);
        } else {
            byte bytes = value2Buffer(value, tmpBuffer4, (byte) 4);
            if (bytes != 0) {
                outStream.write(tmpBuffer4, 0, bytes);
            }
        }
    }

    byte value2Buffer(int value, byte outBuffer[], byte repOffset) throws IOException {
        boolean zeros = true;
        byte bytes = 0;
        for (int i = outBuffer.length - 1; i >= 0; i--) {
            outBuffer[bytes] = (byte) (value >>> 8 * i & 0xff);
            if ((types.rep[repOffset] & 1) > 0) {
                if (!zeros || outBuffer[bytes] != 0) {
                    zeros = false;
                    bytes++;
                }
            } else {
                bytes++;
            }
        }

        if ((types.rep[repOffset] & 1) > 0) {
            outStream.write(bytes);
        }
        if ((types.rep[repOffset] & 2) > 0) {
            reverseArray(outBuffer, bytes);
        }
        return bytes;
    }

    byte value2Buffer(long value, byte outBuffer[], byte repOffset) throws IOException {
        boolean zeros = true;
        byte bytes = 0;
        for (int i = outBuffer.length - 1; i >= 0; i--) {
            outBuffer[bytes] = (byte) (int) (value >>> 8 * i & 255L);
            if ((types.rep[repOffset] & 1) > 0) {
                if (!zeros || outBuffer[bytes] != 0) {
                    zeros = false;
                    bytes++;
                }
            } else {
                bytes++;
            }
        }

        if ((types.rep[repOffset] & 1) > 0) {
            outStream.write(bytes);
        }
        if ((types.rep[repOffset] & 2) > 0) {
            reverseArray(outBuffer, bytes);
        }
        return bytes;
    }

    void reverseArray(byte buffer[], byte bytes) {
        for (int i = 0; i < bytes / 2; i++) {
            byte tmp = buffer[i];
            buffer[i] = buffer[bytes - 1 - i];
            buffer[bytes - 1 - i] = tmp;
        }

    }

    byte unmarshalSB1() throws SQLException, IOException {
        byte result = (byte) unmarshalUB1();
        return result;
    }

    short unmarshalUB1() throws SQLException, IOException {
        short value = 0;
        try {
            value = (short) inStream.read();
        } catch (BreakNetException e) {
            net.sendReset();
            throw e;
        }
        if (value < 0) {
            DatabaseError.throwSqlException(410);
        }
        return value;
    }

    short unmarshalSB2() throws SQLException, IOException {
        short result = (short) unmarshalUB2();
        return result;
    }

    int unmarshalUB2() throws SQLException, IOException {
        int value = (int) buffer2Value((byte) 1);
        return value & 0xffff;
    }

    int unmarshalUCS2(byte ucs2Char[], long offset) throws SQLException, IOException {
        int value = unmarshalUB2();
        tmpBuffer2[0] = (byte) ((value & 0xff00) >> 8);
        tmpBuffer2[1] = (byte) (value & 0xff);
        if (offset + 1L < (long) ucs2Char.length) {
            ucs2Char[(int) offset] = tmpBuffer2[0];
            ucs2Char[(int) offset + 1] = tmpBuffer2[1];
        }
        return tmpBuffer2[0] != 0 ? 3 : tmpBuffer2[1] != 0 ? 2 : 1;
    }

    int unmarshalSB4() throws SQLException, IOException {
        int result = (int) unmarshalUB4();
        return result;
    }

    long unmarshalUB4() throws SQLException, IOException {
        long value = buffer2Value((byte) 2);
        return value;
    }

    int unmarshalSB4(byte buffer[]) throws SQLException, IOException {
        long value = buffer2Value((byte) 2, new ByteArrayInputStream(buffer));
        return (int) value;
    }

    long unmarshalSB8() throws SQLException, IOException {
        long value = buffer2Value((byte) 3);
        return value;
    }

    int unmarshalRefCursor(byte buffer[]) throws SQLException, IOException {
        int result = unmarshalSB4(buffer);
        return result;
    }

    int unmarshalSWORD() throws SQLException, IOException {
        int result = (int) unmarshalUB4();
        return result;
    }

    long unmarshalUWORD() throws SQLException, IOException {
        long result = unmarshalUB4();
        return result;
    }

    byte[] unmarshalNBytes(int n) throws SQLException, IOException {
        byte tmpBuffer[] = new byte[n];
        try {
            if (inStream.read(tmpBuffer) < 0) {
                DatabaseError.throwSqlException(410);
            }
        } catch (BreakNetException e) {
            net.sendReset();
            throw e;
        }
        return tmpBuffer;
    }

    int unmarshalNBytes(byte buf[], int off, int n) throws SQLException, IOException {
        int bytes;
        for (bytes = 0; bytes < n; bytes += getNBytes(buf, off + bytes, n - bytes)) {
        }
        return bytes;
    }

    int getNBytes(byte buf[], int off, int len) throws SQLException, IOException {
        int cnt = 0;
        try {
            if ((cnt = inStream.read(buf, off, len)) < 0) {
                DatabaseError.throwSqlException(410);
            }
        } catch (BreakNetException e) {
            net.sendReset();
            throw e;
        }
        return cnt;
    }

    byte[] getNBytes(int n) throws SQLException, IOException {
        byte tmpBuffer[] = new byte[n];
        try {
            if (inStream.read(tmpBuffer) < 0) {
                DatabaseError.throwSqlException(410);
            }
        } catch (BreakNetException e) {
            net.sendReset();
            throw e;
        }
        return tmpBuffer;
    }

    byte[] unmarshalTEXT(int bytes) throws SQLException, IOException {
        int offset = 0;
        byte tmpBuffer[] = new byte[bytes];
        do {
            if (offset >= bytes) {
                break;
            }
            try {
                if (inStream.read(tmpBuffer, offset, 1) < 0) {
                    DatabaseError.throwSqlException(410);
                }
            } catch (BreakNetException e) {
                net.sendReset();
                throw e;
            }
        } while (tmpBuffer[offset++] != 0);
        byte buffer[];
        if (tmpBuffer.length == --offset) {
            buffer = tmpBuffer;
        } else {
            buffer = new byte[offset];
            System.arraycopy(tmpBuffer, 0, buffer, 0, offset);
        }
        return buffer;
    }

    byte[] unmarshalCHR(int retLength) throws SQLException, IOException {
        byte resBuffer[] = null;
        if (types.isConvNeeded()) {
            resBuffer = unmarshalCLR(retLength, retLen);
            if (resBuffer.length != retLen[0]) {
                byte tmpBuf[] = new byte[retLen[0]];
                System.arraycopy(resBuffer, 0, tmpBuf, 0, retLen[0]);
                resBuffer = tmpBuf;
            }
        } else {
            resBuffer = getNBytes(retLength);
        }
        return resBuffer;
    }

    void unmarshalCLR(byte bytes[], int offsetRow, int intArray[]) throws SQLException, IOException {
        unmarshalCLR(bytes, offsetRow, intArray, 0x7fffffff);
    }

    void unmarshalCLR(byte bytes[], int offsetRow, int intArray[], int maxSize)
            throws SQLException, IOException {
        short len = 0;
        int offset = offsetRow;
        boolean optimized = false;
        int nbBytesWritten = 0;
        int keepThem = 0;
        int lastread = -1;
        len = unmarshalUB1();
        if (len < 0) {
            DatabaseError.throwSqlException(401);
        }
        if (len == 0) {
            intArray[0] = 0;
            return;
        }
        if (escapeSequenceNull(len)) {
            intArray[0] = 0;
            return;
        }
        if (len != 254) {
            keepThem = Math.min(maxSize - nbBytesWritten, len);
            offset = unmarshalBuffer(bytes, offset, keepThem);
            nbBytesWritten += keepThem;
            int rest = len - keepThem;
            if (rest > 0) {
                unmarshalBuffer(ignored, 0, rest);
            }
        } else {
            lastread = -1;
            do {
                if (lastread != -1) {
                    len = unmarshalUB1();
                    if (len <= 0) {
                        break;
                    }
                }
                if (len == 254) {
                    switch (lastread) {
                    default:
                        break;

                    case -1:
                        lastread = 1;
                        continue;

                    case 1: // '\001'
                        lastread = 0;
                        break;

                    case 0: // '\0'
                        if (optimized) {
                            lastread = 0;
                            break;
                        }
                        lastread = 0;
                        continue;
                    }
                }
                if (offset == -1) {
                    unmarshalBuffer(ignored, 0, len);
                } else {
                    keepThem = Math.min(maxSize - nbBytesWritten, len);
                    offset = unmarshalBuffer(bytes, offset, keepThem);
                    nbBytesWritten += keepThem;
                    int rest = len - keepThem;
                    if (rest > 0) {
                        unmarshalBuffer(ignored, 0, rest);
                    }
                }
                lastread = 0;
                if (len > 252) {
                    optimized = true;
                }
            } while (true);
        }
        if (intArray != null) {
            if (offset != -1) {
                intArray[0] = nbBytesWritten;
            } else {
                intArray[0] = bytes.length - offsetRow;
            }
        }
    }

    byte[] unmarshalCLR(int buflen, int intArray[]) throws SQLException, IOException {
        byte tmpBuf[] = new byte[buflen * conv.c2sNlsRatio];
        unmarshalCLR(tmpBuf, 0, intArray, buflen);
        return tmpBuf;
    }

    int unmarshalKEYVAL(byte keys[][], byte values[][], int nb) throws SQLException, IOException {
        byte buff[] = new byte[1000];
        int length[] = new int[1];
        int kvalflg = 0;
        for (int i = 0; i < nb; i++) {
            int len = unmarshalSB4();
            if (len > 0) {
                unmarshalCLR(buff, 0, length);
                keys[i] = new byte[length[0]];
                System.arraycopy(buff, 0, keys[i], 0, length[0]);
            }
            len = unmarshalSB4();
            if (len > 0) {
                unmarshalCLR(buff, 0, length);
                values[i] = new byte[length[0]];
                System.arraycopy(buff, 0, values[i], 0, length[0]);
            }
            kvalflg = unmarshalSB4();
        }

        buff = null;
        return kvalflg;
    }

    int unmarshalBuffer(byte _byteValue[], int offset, int len) throws SQLException, IOException {
        if (len <= 0) {
            return offset;
        }
        if (_byteValue.length < offset + len) {
            unmarshalNBytes(_byteValue, offset, _byteValue.length - offset);
            unmarshalNBytes(ignored, 0, (offset + len) - _byteValue.length);
            offset = -1;
        } else {
            unmarshalNBytes(_byteValue, offset, len);
            offset += len;
        }
        return offset;
    }

    byte[] unmarshalCLRforREFS() throws SQLException, IOException {
        short len = 0;
        short totalLen = 0;
        byte finalBuffer[] = null;
        Vector refVector = new Vector(10, 10);
        short bytes = unmarshalUB1();
        if (bytes < 0) {
            DatabaseError.throwSqlException(401);
        }
        if (bytes == 0) {
            return null;
        }
        if (!escapeSequenceNull(bytes)) {
            if (bytes == 254) {
                do {
                    if ((len = unmarshalUB1()) <= 0) {
                        break;
                    }
                    if (len != 254 || !types.isServerConversion()) {
                        totalLen += len;
                        byte tmpBuf[] = new byte[len];
                        unmarshalBuffer(tmpBuf, 0, len);
                        refVector.addElement(tmpBuf);
                    }
                } while (true);
            } else {
                totalLen = bytes;
                byte tmpBuf[] = new byte[bytes];
                unmarshalBuffer(tmpBuf, 0, bytes);
                refVector.addElement(tmpBuf);
            }
            finalBuffer = new byte[totalLen];
            int start = 0;
            for (; refVector.size() > 0; refVector.removeElementAt(0)) {
                int arrayLen = ((byte[]) refVector.elementAt(0)).length;
                System.arraycopy(refVector.elementAt(0), 0, finalBuffer, start, arrayLen);
                start += arrayLen;
            }

        } else {
            finalBuffer = null;
        }
        return finalBuffer;
    }

    boolean escapeSequenceNull(int bytes) throws SQLException {
        boolean is_null = false;
        switch (bytes) {
        case 0: // '\0'
            is_null = true;
            break;

        case 253:
            DatabaseError.throwSqlException(401);
            // fall through

        case 255:
            is_null = true;
            break;
        }
        return is_null;
    }

    int processIndicator(boolean isNull, int dataSize) throws SQLException, IOException {
        short ind = unmarshalSB2();
        int res = 0;
        if (!isNull) {
            if (ind == 0) {
                res = dataSize;
            } else if (ind == -2 || ind > 0) {
                res = ind;
            } else {
                res = 0x10000 + ind;
            }
        }
        return res;
    }

    long unmarshalDALC(byte buffer[], int offset, int returnLength[]) throws SQLException,
            IOException {
        long len = unmarshalUB4();
        if (len > 0L) {
            unmarshalCLR(buffer, offset, returnLength);
        }
        return len;
    }

    byte[] unmarshalDALC() throws SQLException, IOException {
        long len = unmarshalUB4();
        byte buffer[] = new byte[(int) (-1L & len)];
        if (buffer.length > 0) {
            buffer = unmarshalCLR(buffer.length, retLen);
            if (buffer == null) {
                DatabaseError.throwSqlException(401);
            }
        } else {
            buffer = new byte[0];
        }
        return buffer;
    }

    byte[] unmarshalDALC(int CLRRetLen[]) throws SQLException, IOException {
        long len = unmarshalUB4();
        byte buffer[] = new byte[(int) (-1L & len)];
        if (buffer.length > 0) {
            buffer = unmarshalCLR(buffer.length, CLRRetLen);
            if (buffer == null) {
                DatabaseError.throwSqlException(401);
            }
        } else {
            buffer = new byte[0];
        }
        return buffer;
    }

    long buffer2Value(byte repOffset) throws SQLException, IOException {
        int bufLength = 0;
        long value = 0L;
        boolean negative = false;
        if ((types.rep[repOffset] & 1) > 0) {
            try {
                bufLength = inStream.read();
            } catch (BreakNetException e) {
                net.sendReset();
                throw e;
            }
            if ((bufLength & 0x80) > 0) {
                bufLength &= 0x7f;
                negative = true;
            }
            if (bufLength < 0) {
                DatabaseError.throwSqlException(410);
            }
            if (bufLength == 0) {
                return 0L;
            }
            if (repOffset == 1 && bufLength > 2 || repOffset == 2 && bufLength > 4
                    || repOffset == 3 && bufLength > 8) {
                DatabaseError.throwSqlException(412);
            }
        } else if (repOffset == 1) {
            bufLength = 2;
        } else if (repOffset == 2) {
            bufLength = 4;
        } else if (repOffset == 3) {
            bufLength = 8;
        }
        byte tmpBuffer[];
        switch (bufLength) {
        case 1: // '\001'
            tmpBuffer = tmpBuffer1;
            break;

        case 2: // '\002'
            tmpBuffer = tmpBuffer2;
            break;

        case 3: // '\003'
            tmpBuffer = tmpBuffer3;
            break;

        case 4: // '\004'
            tmpBuffer = tmpBuffer4;
            break;

        case 5: // '\005'
            tmpBuffer = tmpBuffer5;
            break;

        case 6: // '\006'
            tmpBuffer = tmpBuffer6;
            break;

        case 7: // '\007'
            tmpBuffer = tmpBuffer7;
            break;

        case 8: // '\b'
            tmpBuffer = tmpBuffer8;
            break;

        default:
            tmpBuffer = new byte[bufLength];
            break;
        }
        try {
            if (inStream.read(tmpBuffer) < 0) {
                DatabaseError.throwSqlException(410);
            }
        } catch (BreakNetException e) {
            net.sendReset();
            throw e;
        }
        for (int i = 0; i < tmpBuffer.length; i++) {
            long tmpLong;
            if ((types.rep[repOffset] & 2) > 0) {
                tmpLong = (long) (tmpBuffer[tmpBuffer.length - 1 - i] & 0xff) & 255L;
            } else {
                tmpLong = (long) (tmpBuffer[i] & 0xff) & 255L;
            }
            value |= tmpLong << 8 * (tmpBuffer.length - 1 - i);
        }

        if (repOffset != 3) {
            value &= -1L;
        }
        if (negative) {
            value = -value;
        }
        return value;
    }

    long buffer2Value(byte repOffset, ByteArrayInputStream in) throws SQLException, IOException {
        int bufLength = 0;
        long value = 0L;
        boolean negative = false;
        if ((types.rep[repOffset] & 1) > 0) {
            bufLength = in.read();
            if ((bufLength & 0x80) > 0) {
                bufLength &= 0x7f;
                negative = true;
            }
            if (bufLength < 0) {
                DatabaseError.throwSqlException(410);
            }
            if (bufLength == 0) {
                return 0L;
            }
            if (repOffset == 1 && bufLength > 2 || repOffset == 2 && bufLength > 4) {
                DatabaseError.throwSqlException(412);
            }
        } else if (repOffset == 1) {
            bufLength = 2;
        } else if (repOffset == 2) {
            bufLength = 4;
        }
        byte tmpBuffer[] = new byte[bufLength];
        if (in.read(tmpBuffer) < 0) {
            DatabaseError.throwSqlException(410);
        }
        for (int i = 0; i < tmpBuffer.length; i++) {
            short tmpByte;
            if ((types.rep[repOffset] & 2) > 0) {
                tmpByte = (short) (tmpBuffer[tmpBuffer.length - 1 - i] & 0xff);
            } else {
                tmpByte = (short) (tmpBuffer[i] & 0xff);
            }
            value |= tmpByte << 8 * (tmpBuffer.length - 1 - i);
        }

        value &= -1L;
        if (negative) {
            value = -value;
        }
        return value;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CMAREngine"));
        } catch (Exception e) {
        }
    }
}
