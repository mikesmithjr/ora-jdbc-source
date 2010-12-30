// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: DataIntegrityService

package oracle.net.ano;

import java.io.IOException;
import java.io.PrintStream;
import oracle.net.aso.C06;
import oracle.net.aso.C09;
import oracle.net.ns.ClientProfile;
import oracle.net.ns.NetException;
import oracle.net.ns.SQLnetDef;
import oracle.net.ns.SessionAtts;

// Referenced classes of package oracle.net.ano:
// Service, Ano, AnoComm, AnoServices

public class DataIntegrityService extends Service implements AnoServices, SQLnetDef {

    private C06 e;
    static final int f = 2;
    private boolean g;
    private short h;
    private static final int i = 40;
    private byte j[];
    private int k;

    public DataIntegrityService() {
        g = false;
    }

    public boolean isActive() {
        return g;
    }

    int a(SessionAtts sessionatts) throws NetException {
        super.a(sessionatts);
        service = 3;
        serviceSubPackets = 2;
        k = 0;
        level = sessionatts.profile.getDataIntegrityLevelNum();
        availableDrivers = h("oracle.net.aso.", AnoServices.DATAINTEGRITY_CLASSNAME);
        listOfDrivers = g(sessionatts.profile.getDataIntegrityServices(), availableDrivers);
        i(listOfDrivers, AnoServices.DATAINTEGRITY_CLASSNAME, level);
        int l = 1;
        if (selectedDrivers.length == 0) {
            if (level == 3) {
                throw new NetException(315);
            }
            l |= 8;
        } else if (level == 3) {
            l |= 0x10;
        }
        return l;
    }

    void b() throws NetException, IOException {
        int l = 0;
        do {
            if (l >= selectedDrivers.length) {
                break;
            }
            if (selectedDrivers[l] == h) {
                k = l;
                break;
            }
            l++;
        } while (true);
        if (l == selectedDrivers.length) {
            throw new NetException(319);
        } else {
            return;
        }
    }

    void c() throws NetException, IOException {
        if (g) {
            try {
                ano.dataIntegrityAlg = e = (C06) Class
                        .forName("oracle.net.aso." + AnoServices.DATAINTEGRITY_CLASSNAME[h])
                        .newInstance();
            } catch (Exception exception) {
                System.out.println(" alg =" + AnoServices.DATAINTEGRITY_CLASSNAME[h]);
                exception.printStackTrace();
                throw new NetException(318);
            }
            e.init(ano.getSessionKey(), ano.getInitializationVector());
        }
        if (j != null) {
            Ano _tmp = sAtts.ano;
            int l = 13 + 8 + 4 + j.length;
            sAtts.ano.sendANOHeader(l, 1, (short) 0);
            serviceSubPackets = 1;
            e();
            comm.sendRaw(j);
            comm.flush();
        }
    }

    void d() throws NetException, IOException {
        version = comm.receiveVersion();
        h = comm.receiveUB1();
        if (numSubPackets != serviceSubPackets && numSubPackets == 8) {
            short word0 = (short) comm.receiveUB2();
            short word1 = (short) comm.receiveUB2();
            byte abyte0[] = comm.receiveRaw();
            byte abyte1[] = comm.receiveRaw();
            byte abyte2[] = comm.receiveRaw();
            byte abyte3[] = comm.receiveRaw();
            if (word0 <= 0 || word1 <= 0) {
                throw new IOException("Bad parameters from server");
            }
            int l = (word1 + 7) / 8;
            if (abyte2.length != l || abyte1.length != l) {
                throw new IOException("DiffieHellman negotiation out of synch");
            }
            C09 c09 = new C09(abyte0, abyte1, word0, word1);
            j = c09.g();
            sAtts.ano.setClientPK(j);
            sAtts.ano.setInitializationVector(abyte3);
            sAtts.ano.setSessionKey(c09.a(abyte2, abyte2.length));
        }
        g = h > 0;
    }
}
