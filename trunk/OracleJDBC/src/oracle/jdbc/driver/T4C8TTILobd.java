package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8TTILobd extends T4CTTIMsg {
    static final int LOBD_STATE0 = 0;
    static final int LOBD_STATE1 = 1;
    static final int LOBD_STATE2 = 2;
    static final int LOBD_STATE3 = 3;
    static final int LOBD_STATE_EXIT = 4;
    static final short TTCG_LNG = 254;
    static final short LOBDATALENGTH = 252;
    static byte[] ucs2Char = new byte[2];

    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4C8TTILobd(T4CMAREngine mengine) {
        super((byte) 14);

        setMarshalingEngine(mengine);
    }

    void marshalLobData(byte[] inBuffer, long inBufferOffset, long numBytes) throws SQLException,
            IOException {
        long length = numBytes;
        boolean ttcgLong = false;

        marshalTTCcode();

        if (length > 252L) {
            ttcgLong = true;

            this.meg.marshalUB1((short) 254);
        }

        long count = 0L;

        for (; length > 252L; length -= 252L) {
            this.meg.marshalUB1((short) 252);
            this.meg.marshalB1Array(inBuffer, (int) (inBufferOffset + count * 252L), 252);

            count += 1L;
        }

        if (length > 0L) {
            this.meg.marshalUB1((short) (int) length);
            this.meg.marshalB1Array(inBuffer, (int) (inBufferOffset + count * 252L), (int) length);
        }

        if (ttcgLong == true)
            this.meg.marshalUB1((short) 0);
    }

    void marshalLobDataUB2(byte[] inBuffer, long inBufferOffset, long numChars)
            throws SQLException, IOException {
        long numUB2 = numChars;
        boolean ttcgLong = false;

        marshalTTCcode();

        if (numUB2 > 84L) {
            ttcgLong = true;

            this.meg.marshalUB1((short) 254);
        }

        long count = 0L;

        for (; numUB2 > 84L; numUB2 -= 84L) {
            this.meg.marshalUB1((short) 252);

            for (int j = 0; j < 84; j++) {
                this.meg.marshalUB1((short) 2);

                this.meg.marshalB1Array(inBuffer, (int) (inBufferOffset + count * 168L + j * 2), 2);
            }
            count += 1L;
        }

        if (numUB2 > 0L) {
            long length = numUB2 * 3L;

            this.meg.marshalUB1((short) (int) length);

            for (int j = 0; j < numUB2; j++) {
                this.meg.marshalUB1((short) 2);

                this.meg.marshalB1Array(inBuffer, (int) (inBufferOffset + count * 168L + j * 2), 2);
            }

        }

        if (ttcgLong == true)
            this.meg.marshalUB1((short) 0);
    }

    long unmarshalLobData(byte[] outBuffer) throws SQLException, IOException {
        long bytesRead = 0L;
        long offset = 0L;
        short length = 0;

        int state = 0;

        while (state != 4) {
            switch (state) {
            case 0:
                length = this.meg.unmarshalUB1();

                if (length == 254) {
                    state = 2;
                    continue;
                }

                state = 1;

                break;
            case 1:
                this.meg.getNBytes(outBuffer, (int) offset, length);

                bytesRead += length;
                state = 4;

                break;
            case 2:
                length = this.meg.unmarshalUB1();

                if (length > 0) {
                    state = 3;
                    continue;
                }

                state = 4;

                break;
            case 3:
                this.meg.getNBytes(outBuffer, (int) offset, length);

                bytesRead += length;

                offset += length;

                state = 2;

                continue;
            }

        }

        return bytesRead;
    }

    long unmarshalClobUB2(byte[] outBuffer) throws SQLException, IOException {
        long bytesRead = 0L;
        long offset = 0L;
        short length = 0;
        int i = 0;
        int numBytes = 0;

        int state = 0;

        while (state != 4) {
            switch (state) {
            case 0:
                length = this.meg.unmarshalUB1();

                if (length == 254) {
                    state = 2;
                    continue;
                }

                state = 1;

                break;
            case 1:
                for (i = 0; i < length; offset += 2L) {
                    numBytes = this.meg.unmarshalUCS2(outBuffer, offset);

                    i += numBytes;
                }

                bytesRead += length;
                state = 4;

                break;
            case 2:
                length = this.meg.unmarshalUB1();

                if (length > 0) {
                    state = 3;
                    continue;
                }

                state = 4;

                break;
            case 3:
                for (i = 0; i < length; offset += 2L) {
                    numBytes = this.meg.unmarshalUCS2(outBuffer, offset);

                    i += numBytes;
                }

                bytesRead += length;

                state = 2;

                continue;
            }

        }

        return bytesRead;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4C8TTILobd"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4C8TTILobd JD-Core Version: 0.6.0
 */