// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: SupervisorService

package oracle.net.ano;

import java.io.IOException;
import oracle.net.ns.NetException;
import oracle.net.ns.SQLnetDef;
import oracle.net.ns.SessionAtts;

// Referenced classes of package oracle.net.ano:
// Service, AnoComm, AnoServices

public class SupervisorService extends Service implements SQLnetDef {

    private int h[];
    static final int i = 47;
    static final int j = 3;
    static final int k = 127;
    private byte l[];
    static final int m = 111;
    static final int n = 63;
    static final int o = 95;
    private int p;
    private int q;
    private int r[];
    static final int s = 31;
    static final int t = 79;

    void i(String as[], String as1[], int i1) throws NetException {
        selectedDrivers = new byte[as.length];
        selectedDrivers[0] = k(as1, as[as.length - 1]);
        for (int j1 = 1; j1 < as.length; j1++) {
            selectedDrivers[j1] = k(as1, as[j1 - 1]);
        }

    }

    int f() {
        return 12 + l.length + 4 + 10 + h.length * 2;
    }

    void d() throws NetException, IOException {
        version = comm.receiveVersion();
        status = comm.receiveStatus();
        if (status != 31) {
            throw new NetException(306);
        } else {
            r = comm.receiveUB2Array();
            return;
        }
    }

    void n() throws NetException, IOException {
        comm.sendVersion();
        comm.sendRaw(l);
        comm.sendUB2Array(h);
    }

    void b() throws NetException, IOException {
        for (int i1 = 0; i1 < r.length; i1++) {
            int j1 = 0;
            do {
                if (j1 >= h.length) {
                    break;
                }
                if (r[i1] == h[j1]) {
                    p++;
                    break;
                }
                j1++;
            } while (true);
            if (j1 == h.length) {
                throw new NetException(320);
            }
        }

        if (p != q) {
            throw new NetException(321);
        } else {
            return;
        }
    }

    int a(SessionAtts sessionatts) throws NetException {
        super.a(sessionatts);
        service = 4;
        serviceSubPackets = 3;
        l = g();
        p = 0;
        q = 2;
        availableDrivers = h("oracle.net.ano.", AnoServices.SERV_CLASSNAME);
        i(availableDrivers, AnoServices.SERV_CLASSNAME, level);
        h = new int[selectedDrivers.length];
        for (int i1 = 0; i1 < h.length; i1++) {
            h[i1] = 0;
            h[i1] |= 0xff & selectedDrivers[i1];
        }

        return 1;
    }

    byte[] g() {
        byte abyte0[] = new byte[8];
        for (int i1 = 0; i1 < abyte0.length; i1++) {
            abyte0[i1] = 9;
        }

        return abyte0;
    }
}
