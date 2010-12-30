// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: Service

package oracle.net.ano;

import java.io.IOException;
import java.util.Vector;
import oracle.net.aso.*;
import oracle.net.ns.NetException;
import oracle.net.ns.SQLnetDef;
import oracle.net.ns.SessionAtts;

// Referenced classes of package oracle.net.ano:
// Ano, AnoComm, AnoServices

public class Service implements AnoServices, SQLnetDef {

    protected int receivedService;
    protected long oracleError;
    protected String listOfDrivers[];
    protected Ano ano;
    protected int status;
    protected String availableDrivers[];
    protected AnoComm comm;
    protected int serviceSubPackets;
    protected long version;
    protected short algID;
    protected int numSubPackets;
    protected byte selectedDrivers[];
    protected SessionAtts sAtts;
    protected int service;
    protected int level;

    void a() throws NetException, IOException {
        e();
        n();
    }

    void c() throws NetException, IOException {
    }

    void b(Service service1) throws NetException, IOException {
        receivedService = service1.receivedService;
        numSubPackets = service1.numSubPackets;
        oracleError = service1.oracleError;
        d();
        b();
    }

    void e() throws NetException, IOException {
        comm.writeUB2(service);
        comm.writeUB2(serviceSubPackets);
        comm.writeUB4(0L);
    }

    void b() throws NetException, IOException {
    }

    int f() {
        return 12 + selectedDrivers.length;
    }

    String[] g(String as[], String as1[]) throws NetException {
        String as2[];
        if (as == null || as.length == 0) {
            as2 = as1;
        } else {
            Vector vector = new Vector(10);
            for (int i1 = 0; i1 < as.length; i1++) {
                int k1 = 0;
                do {
                    if (k1 >= as1.length) {
                        break;
                    }
                    if (as1[k1].equals(as[i1])) {
                        vector.addElement(as1[k1]);
                        break;
                    }
                    k1++;
                } while (true);
                if (k1 == as1.length) {
                    throw new NetException(303);
                }
            }

            int l1 = vector.size();
            as2 = new String[l1];
            for (int j1 = 0; j1 < l1; j1++) {
                as2[j1] = (String) vector.elementAt(j1);
            }

        }
        return as2;
    }

    String[] h(String s, String as[]) {
        Vector vector = new Vector(10);
        for (int i1 = 1; i1 < as.length; i1++) {
            if (as[i1].equals("")) {
                continue;
            }
            try {
                if (as[i1].equals("DES56C")) {
                    new C02();
                } else if (as[i1].equals("DES40C")) {
                    new C07();
                } else if (as[i1].startsWith("RC4")) {
                    new C12();
                } else if (as[i1].equals("3DES168")) {
                    new C05();
                } else if (as[i1].equals("3DES112")) {
                    new C10();
                } else {
                    Class.forName(s + as[i1]).newInstance();
                }
                vector.addElement(as[i1]);
            } catch (Exception exception) {
            }
        }

        int j1 = vector.size();
        String as1[] = new String[j1];
        for (int k1 = 0; k1 < j1; k1++) {
            as1[k1] = (String) vector.elementAt(k1);
        }

        return as1;
    }

    void i(String as[], String as1[], int i1) throws NetException {
        label0: switch (i1) {
        case 0: // '\0'
            selectedDrivers = new byte[as.length + 1];
            selectedDrivers[0] = 0;
            int j1 = 0;
            do {
                if (j1 >= as.length) {
                    break label0;
                }
                if (!as[j1].equals("")) {
                    selectedDrivers[j1 + 1] = k(as1, as[j1]);
                }
                j1++;
            } while (true);

        case 1: // '\001'
            selectedDrivers = new byte[1];
            selectedDrivers[0] = 0;
            break;

        case 2: // '\002'
            int k1 = 0;
            selectedDrivers = new byte[as.length + 1];
            for (k1 = 0; k1 < as.length; k1++) {
                if (!as[k1].equals("")) {
                    selectedDrivers[k1] = k(as1, as[k1]);
                }
            }

            selectedDrivers[k1] = 0;
            break;

        case 3: // '\003'
            selectedDrivers = new byte[as.length];
            int l1 = 0;
            do {
                if (l1 >= as.length) {
                    break label0;
                }
                if (!as[l1].equals("")) {
                    selectedDrivers[l1] = k(as1, as[l1]);
                }
                l1++;
            } while (true);

        default:
            throw new NetException(304);
        }
    }

    void d() throws NetException, IOException {
    }

    int a(SessionAtts sessionatts) throws NetException {
        sAtts = sessionatts;
        ano = sessionatts.ano;
        comm = sessionatts.ano.anoComm;
        level = 0;
        selectedDrivers = new byte[0];
        return 1;
    }

    void j() throws NetException, IOException {
        receivedService = comm.readUB2();
        numSubPackets = comm.readUB2();
        oracleError = comm.readUB4();
        if (oracleError != 0L) {
            throw new NetException((int) oracleError);
        } else {
            return;
        }
    }

    byte k(String as[], String s) throws NetException {
        for (byte byte0 = 0; byte0 < as.length; byte0++) {
            if (s.equals(as[byte0])) {
                return byte0;
            }
        }

        throw new NetException(309);
    }

    public boolean isActive() {
        return false;
    }

    int l() {
        return 8 + f();
    }

    void m(String as[]) throws NetException {
    }

    public static String getServiceName(int i1) throws NetException {
        String s;
        switch (i1) {
        case 1: // '\001'
            s = "AUTHENTICATION";
            break;

        case 2: // '\002'
            s = "ENCRYPTION";
            break;

        case 3: // '\003'
            s = "DATAINTEGRITY";
            break;

        case 4: // '\004'
            s = "SUPERVISOR";
            break;

        default:
            throw new NetException(323);
        }
        return s;
    }

    void n() throws NetException, IOException {
        comm.sendVersion();
        comm.sendRaw(selectedDrivers);
    }

    public static String getLevelString(int i1) throws NetException {
        String s;
        switch (i1) {
        case 0: // '\0'
            s = "ACCEPTED";
            break;

        case 1: // '\001'
            s = "REJECTED";
            break;

        case 2: // '\002'
            s = "REQUESTED";
            break;

        case 3: // '\003'
            s = "REQUIRED";
            break;

        default:
            throw new NetException(322);
        }
        return s;
    }
}
