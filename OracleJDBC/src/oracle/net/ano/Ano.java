// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: Ano

package oracle.net.ano;

import java.io.IOException;
import oracle.net.aso.C00;
import oracle.net.aso.C06;
import oracle.net.ns.NetException;
import oracle.net.ns.SQLnetDef;
import oracle.net.ns.SessionAtts;

// Referenced classes of package oracle.net.ano:
// AnoNetInputStream, AnoServices, AnoNetOutputStream, AnoComm,
// Service

public class Ano implements AnoServices, SQLnetDef {

    private short h;
    private Service i[];
    private int j;
    protected byte iv[];
    private int k;
    protected SessionAtts sAtts;
    protected C06 dataIntegrityAlg;
    private long l;
    protected C00 encryptionAlg;
    protected byte skey[];
    private int m;
    private boolean n;
    private int o;
    protected boolean cryptoNeeded;
    protected AnoComm anoComm;
    protected byte clientPK[];
    private Service p[];
    private byte q[];

    public C00 getEncryptionAlg() {
        return encryptionAlg;
    }

    protected void setSessionKey(byte abyte0[]) {
        skey = abyte0;
    }

    public int getNAFlags() {
        return o;
    }

    private void a() throws NetException, IOException {
        anoComm.writeUB4(0xffffffffdeadbeefL);
        anoComm.writeUB2(m);
        anoComm.writeVersion();
        anoComm.writeUB2(AnoServices.SERVICES_INORDER.length);
        anoComm.writeUB1(h);
    }

    public C06 getDataIntegrityAlg() {
        return dataIntegrityAlg;
    }

    protected void setInitializationVector(byte abyte0[]) {
        iv = abyte0;
    }

    private boolean b(long l1) {
        return l1 == 0xffffffffdeadbeefL;
    }

    protected byte[] getInitializationVector() {
        return iv;
    }

    private void c() throws NetException, IOException {
        Service service = new Service();
        service.a(sAtts);
        for (int i1 = 0; i1 < j; i1++) {
            service.j();
            p[service.receivedService].b(service);
        }

        service = null;
    }

    protected void sendANOHeader(int i1, int j1, short word0) throws NetException, IOException {
        anoComm.writeUB4(0xffffffffdeadbeefL);
        anoComm.writeUB2(i1);
        anoComm.writeVersion();
        anoComm.writeUB2(j1);
        anoComm.writeUB1(word0);
    }

    public Ano() {
        cryptoNeeded = false;
        o = 1;
    }

    private void d() throws NetException, IOException {
        for (int i1 = 0; i1 < AnoServices.SERVICES_INORDER.length; i1++) {
            i[i1].a();
        }

        anoComm.flush();
    }

    protected byte[] getSessionKey() {
        return skey;
    }

    private int e() throws NetException {
        int i1 = 0;
        for (int j1 = 0; j1 < AnoServices.SERV_INORDER_CLASSNAME.length; j1++) {
            try {
                i[j1] = (Service) Class.forName(
                                                "oracle.net.ano."
                                                        + AnoServices.SERV_INORDER_CLASSNAME[j1])
                        .newInstance();
            } catch (Exception exception) {
                throw new NetException(308);
            }
            o |= i[j1].a(sAtts);
            i1 += i[j1].l();
            p[i[j1].service] = i[j1];
        }

        if ((o & 0x10) > 0 && (o & 8) > 0) {
            o &= 0xffffffef;
        }
        return i1;
    }

    public boolean getRenewKey() {
        return n;
    }

    public void init(SessionAtts sessionatts) throws NetException {
        sAtts = sessionatts;
        sAtts.ano = this;
        i = new Service[4];
        p = new Service[5];
        anoComm = new AnoComm(sessionatts);
        k = e();
        m = 13 + k;
    }

    public byte[] getO3logSessionKey() {
        return q;
    }

    private void f() throws NetException, IOException {
        for (int i1 = 0; i1 < j; i1++) {
            i[i1].c();
        }

        cryptoNeeded = i[2].isActive() || i[3].isActive();
    }

    public void setRenewKey(boolean flag) {
        n = flag;
    }

    public void negotiation() throws NetException, IOException {
        a();
        d();
        g();
        c();
        f();
        if (cryptoNeeded) {
            sAtts.turnEncryptionOn(new AnoNetInputStream(sAtts), new AnoNetOutputStream(sAtts));
        }
    }

    private int g() throws NetException, IOException {
        long l1 = anoComm.readUB4();
        if (!b(l1)) {
            throw new NetException(302);
        } else {
            m = anoComm.readUB2();
            l = anoComm.readUB4();
            j = anoComm.readUB2();
            h = anoComm.readUB1();
            return j;
        }
    }

    public void setO3logSessionKey(byte abyte0[]) {
        q = abyte0;
    }

    protected void setClientPK(byte abyte0[]) {
        clientPK = abyte0;
    }
}
