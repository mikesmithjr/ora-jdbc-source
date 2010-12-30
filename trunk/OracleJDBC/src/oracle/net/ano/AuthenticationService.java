// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: AuthenticationService

package oracle.net.ano;

import java.io.IOException;
import oracle.net.ns.NetException;
import oracle.net.ns.SQLnetDef;
import oracle.net.ns.SessionAtts;

// Referenced classes of package oracle.net.ano:
// Service, AnoComm, AnoServices

public class AuthenticationService extends Service implements AnoServices, SQLnetDef {

    static final int a = 64255;
    static final int b = 2;
    static final int c = 64767;
    private int d;
    static final int e = 3;
    static final int f = 63999;
    static final int g = 63487;
    static final int h = 64511;
    static final int i = 65023;
    static final int j = 65279;
    static final int k = 57569;
    private boolean l;
    static final boolean m = true;
    static final int n = 63743;

    int a(SessionAtts sessionatts) throws NetException {
        super.a(sessionatts);
        service = 1;
        serviceSubPackets = 3;
        d = 64767;
        d = 64767;
        availableDrivers = new String[0];
        listOfDrivers = new String[0];
        level = 3;
        i(listOfDrivers, AnoServices.AUTH_CLASSNAME, level);
        serviceSubPackets += selectedDrivers.length * 2;
        return 1;
    }

    int f() {
        int i1 = 20;
        for (int j1 = 0; j1 < selectedDrivers.length; j1++) {
            i1 = (i1 += 5) + (4 + AnoServices.AUTH_NAME[selectedDrivers[j1]].length());
        }

        return i1;
    }

    void b() throws NetException, IOException {
        if (!l)
            ;
    }

    public boolean isActive() {
        return l;
    }

    public AuthenticationService() {
        l = false;
    }

    void d() throws NetException, IOException {
        if (numSubPackets != 2) {
            throw new NetException(305);
        }
        version = comm.receiveVersion();
        d = comm.receiveStatus();
        if (d == 64255) {
            for (int i1 = 0; i1 < (numSubPackets - 2) / 2; i1++) {
                comm.receiveUB1();
                comm.receiveString();
                l = true;
            }

        } else if (d == 64511) {
            l = false;
        } else {
            throw new NetException(307);
        }
    }

    void n() throws NetException, IOException {
        comm.sendVersion();
        comm.sendUB2(57569);
        comm.sendStatus(d);
        for (int i1 = 0; i1 < selectedDrivers.length; i1++) {
            comm.sendUB1(selectedDrivers[i1]);
            comm.sendString(AnoServices.AUTH_NAME[selectedDrivers[i1]]);
        }

    }
}
