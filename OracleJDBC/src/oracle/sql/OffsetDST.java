package oracle.sql;

import java.sql.Timestamp;

public class OffsetDST {
    private Timestamp timestamp;
    private int offset;
    private byte DSTflag;

    public OffsetDST(Timestamp paramTimestamp, int paramInt, byte paramByte) {
        this.timestamp = paramTimestamp;
        this.offset = paramInt;
        this.DSTflag = paramByte;
    }

    public OffsetDST() {
        this.timestamp = new Timestamp(0L);
        this.offset = 0;
        this.DSTflag = 0;
    }

    public int getOFFSET() {
        return this.offset;
    }

    public byte getDSTFLAG() {
        return this.DSTflag;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setOFFSET(int paramInt) {
        this.offset = paramInt;
    }

    public void setDSTFLAG(byte paramByte) {
        this.DSTflag = paramByte;
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.OffsetDST JD-Core Version: 0.6.0
 */