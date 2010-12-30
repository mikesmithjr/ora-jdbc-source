package oracle.jdbc.driver;

class ByteArrayKey {
    private byte[] theBytes;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public ByteArrayKey(byte[] x) {
        this.theBytes = x;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ByteArrayKey)) {
            return false;
        }

        byte[] theOtherBytes = ((ByteArrayKey) o).theBytes;

        if (this.theBytes.length != theOtherBytes.length) {
            return false;
        }
        for (int i = 0; i < this.theBytes.length; i++) {
            if (this.theBytes[i] != theOtherBytes[i])
                return false;
        }
        return true;
    }

    public int hashCode() {
        int answer = 0;

        for (int i = 0; i < this.theBytes.length; i++) {
            answer += this.theBytes[i];
        }
        return answer;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.ByteArrayKey"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.ByteArrayKey JD-Core Version: 0.6.0
 */