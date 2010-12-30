// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc

package oracle.net.ns;

import java.io.IOException;
import oracle.net.nt.ConnOption;

// Referenced classes of package oracle.net.ns:
// Packet, SQLnetDef, SessionAtts, NetOutputStream

public class ConnectPacket extends Packet implements SQLnetDef {

    private boolean connDataOflow;

    public ConnectPacket(SessionAtts sessionatts) {
        super(sessionatts);
        data = sessionatts.cOption.conn_data.toString();
        dataLen = data != null ? data.length() : 0;
        connDataOflow = dataLen > 230;
        int i = connDataOflow ? 34 : 34 + dataLen;
        createBuffer(i, 1, 0);
        buffer[8] = 1;
        buffer[9] = 52;
        buffer[10] = 1;
        buffer[11] = 44;
        buffer[12] = 0;
        buffer[13] = 0;
        buffer[14] = (byte) (sdu / 256);
        buffer[15] = (byte) (sdu % 256);
        buffer[16] = (byte) (tdu / 256);
        buffer[17] = (byte) (tdu % 256);
        buffer[18] = 79;
        buffer[19] = -104;
        buffer[22] = 0;
        buffer[23] = 1;
        buffer[24] = (byte) (dataLen / 256);
        buffer[25] = (byte) (dataLen % 256);
        buffer[27] = 34;
        if (!sessionatts.anoEnabled) {
            buffer[32] = buffer[33] = 4;
        } else {
            buffer[32] = buffer[33] = (byte) sessionatts.getANOFlags();
        }
        if (!connDataOflow && dataLen > 0) {
            data.getBytes(0, dataLen, buffer, 34);
        }
    }

    protected void send() throws IOException {
        super.send();
        if (connDataOflow) {
            byte abyte0[] = new byte[dataLen];
            data.getBytes(0, dataLen, abyte0, 0);
            sAtts.nsOutputStream.write(abyte0);
            sAtts.nsOutputStream.flush();
        }
    }
}
