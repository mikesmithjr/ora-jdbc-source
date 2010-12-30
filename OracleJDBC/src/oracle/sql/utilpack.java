package oracle.sql;

class utilpack {
    private static int INTYM3BYTE = 24;
    private static int INTYM2BYTE = 16;
    private static int INTYM1BYTE = 8;

    protected static int LEFTSHIFTFIRSTNIBBLE(byte paramByte) {
        int i = 0;

        i = (paramByte & 0xFF) << INTYM3BYTE;

        return i;
    }

    protected static int LEFTSHIFTSECONDNIBBLE(byte paramByte) {
        int i = 0;

        i = (paramByte & 0xFF) << INTYM2BYTE;

        return i;
    }

    protected static int LEFTSHIFTTHIRDNIBBLE(byte paramByte) {
        int i = 0;

        i = (paramByte & 0xFF) << INTYM1BYTE;

        return i;
    }

    protected static byte RIGHTSHIFTFIRSTNIBBLE(int paramInt) {
        byte i = 0;

        i = (byte) (paramInt >> INTYM3BYTE & 0xFF);

        return i;
    }

    protected static byte RIGHTSHIFTSECONDNIBBLE(int paramInt) {
        byte i = 0;

        i = (byte) (paramInt >> INTYM2BYTE & 0xFF);

        return i;
    }

    protected static byte RIGHTSHIFTTHIRDNIBBLE(int paramInt) {
        byte i = 0;

        i = (byte) (paramInt >> INTYM1BYTE & 0xFF);

        return i;
    }

    protected static byte RIGHTSHIFTFOURTHNIBBLE(int paramInt) {
        byte i = 0;

        i = (byte) (paramInt & 0xFF);

        return i;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.utilpack JD-Core Version: 0.6.0
 */