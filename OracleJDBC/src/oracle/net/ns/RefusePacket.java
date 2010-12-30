package oracle.net.ns;

import java.io.IOException;

public class RefusePacket extends Packet implements SQLnetDef {
    protected int userReason = this.buffer[8];
    protected int systemReason = this.buffer[9];

    public RefusePacket(Packet paramPacket) throws IOException, NetException {
        super(paramPacket);
        this.dataOff = 12;
        this.dataLen = (this.buffer[10] & 0xFF);
        this.dataLen <<= 8;
        this.dataLen |= this.buffer[11] & 0xFF;
        extractData();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.net.ns.RefusePacket JD-Core Version: 0.6.0
 */