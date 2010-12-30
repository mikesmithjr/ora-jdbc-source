package oracle.jdbc.oracore;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.driver.OracleLog;

public class TDSReader {
    static final int KOPT_NONE_FINAL_TYPE = 1;
    static final int KOPT_JAVA_OBJECT = 2;
    int nullOffset;
    int ldsOffset;
    long fixedDataSize;
    Vector patches;
    byte[] tds;
    int beginIndex;
    int index;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:49_PDT_2005";

    TDSReader(byte[] tds, long beginIndex) {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.adtLogger.log(Level.FINER, "TDSReader( tds = " + tds + ", beginIndex = "
                    + beginIndex + ")", this);

            OracleLog.recursiveTrace = false;
        }

        this.nullOffset = 0;
        this.ldsOffset = 0;
        this.fixedDataSize = 0L;
        this.patches = null;

        this.tds = tds;
        this.beginIndex = (int) beginIndex;
        this.index = (int) beginIndex;
    }

    void skipBytes(int number) throws SQLException {
        this.index += number;
    }

    void checkNextByte(byte value) throws SQLException {
        try {
            if (value != this.tds[this.index]) {
                DatabaseError.throwSqlException(47, "parseTDS");
            }

        } finally {
            this.index += 1;
        }
    }

    byte readByte() throws SQLException {
        try {
            byte i = this.tds[this.index];
            return i;
        } finally {
            this.index += 1;
        }
    }

    int readUnsignedByte() throws SQLException {
        try {
            int i = this.tds[this.index] & 0xFF;
            return i;
        } finally {
            this.index += 1;
        }
    }

    short readShort() throws SQLException {
        try {
            short i = (short) ((this.tds[this.index] & 0xFF) * 256 + (this.tds[(this.index + 1)] & 0xFF));
            return i;
        } finally {
            this.index += 2;
        }
    }

    long readLong() throws SQLException {
        try {
            long l = (((this.tds[this.index] & 0xFF) * 256 + (this.tds[(this.index + 1)] & 0xFF)) * 256 + (this.tds[(this.index + 2)] & 0xFF))
                    * 256 + (this.tds[(this.index + 3)] & 0xFF);
            return l;
        } finally {
            this.index += 4;
        }
    }

    void addNormalPatch(long pos, byte uptStyle, OracleType type) throws SQLException {
        addPatch(new TDSPatch(0, type, pos, uptStyle));
    }

    void addSimplePatch(long pos, OracleType type) throws SQLException {
        addPatch(new TDSPatch(1, type, pos, 0));
    }

    void addPatch(TDSPatch patch) throws SQLException {
        if (this.patches == null) {
            this.patches = new Vector(5);
        }
        this.patches.addElement(patch);
    }

    long moveToPatchPos(TDSPatch patch) throws SQLException {
        long patchPos = patch.getPosition();

        if (this.beginIndex + patchPos > this.tds.length) {
            DatabaseError.throwSqlException(47, "parseTDS");
        }

        skip_to(patchPos);

        return patchPos;
    }

    TDSPatch getNextPatch() throws SQLException {
        TDSPatch patch = null;

        if (this.patches != null) {
            if (this.patches.size() > 0) {
                patch = (TDSPatch) this.patches.firstElement();

                this.patches.removeElementAt(0);
            }
        }

        return patch;
    }

    void skip_to(long offset) {
        this.index = (this.beginIndex + (int) offset);
    }

    long offset() throws SQLException {
        return this.index - this.beginIndex;
    }

    long absoluteOffset() throws SQLException {
        return this.index;
    }

    byte[] tds() throws SQLException {
        return this.tds;
    }

    boolean isJavaObject(int version, byte flag) {
        return (version >= 3) && ((flag & 0x2) != 0);
    }

    boolean isFinalType(int version, byte flag) {
        return (version >= 3) && ((flag & 0x1) == 0);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.oracore.TDSReader"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.oracore.TDSReader JD-Core Version: 0.6.0
 */