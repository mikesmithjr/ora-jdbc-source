// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: EncryptionService

package oracle.net.ano;

import java.io.IOException;
import oracle.net.aso.*;
import oracle.net.ns.ClientProfile;
import oracle.net.ns.NetException;
import oracle.net.ns.SQLnetDef;
import oracle.net.ns.SessionAtts;

// Referenced classes of package oracle.net.ano:
// Service, Ano, AnoServices, AnoComm

public class EncryptionService extends Service implements AnoServices, SQLnetDef {

    private int a;
    static final int b = 2;
    private boolean c;
    private static boolean d = true;
    private C00 e;

    int a(SessionAtts sessionatts) throws NetException {
        super.a(sessionatts);
        service = 2;
        serviceSubPackets = 2;
        level = sessionatts.profile.getEncryptionLevelNum();
        availableDrivers = h("oracle.net.aso.", d ? AnoServices.ENC_CLASSNAME_EX
                : AnoServices.ENC_CLASSNAME);
        listOfDrivers = g(sessionatts.profile.getEncryptionServices(), availableDrivers);
        i(listOfDrivers, d ? AnoServices.ENC_CLASSNAME_EX : AnoServices.ENC_CLASSNAME, level);
        int i = 1;
        if (selectedDrivers.length == 0) {
            if (level == 3) {
                throw new NetException(315);
            }
            i |= 8;
        } else if (level == 3) {
            i |= 0x10;
        }
        return i;
    }

    void b() throws NetException, IOException {
        int i = 0;
        do {
            if (i >= selectedDrivers.length) {
                break;
            }
            if (selectedDrivers[i] == algID) {
                a = i;
                break;
            }
            i++;
        } while (true);
        if (i == selectedDrivers.length) {
            throw new NetException(316);
        } else {
            return;
        }
    }

    public boolean isActive() {
        return c;
    }

    public EncryptionService() {
        c = false;
    }

    void d() throws NetException, IOException {
        if (numSubPackets != serviceSubPackets) {
            throw new NetException(305);
        } else {
            version = comm.receiveVersion();
            algID = comm.receiveUB1();
            c = algID > 0;
            return;
        }
    }

    void c() throws NetException, IOException {
        if (c) {
            try {
                if (d) {
                    if (AnoServices.ENC_CLASSNAME_EX[algID].equals("DES56C")) {
                        ano.encryptionAlg = e = new C02();
                    } else if (AnoServices.ENC_CLASSNAME_EX[algID].equals("DES40C")) {
                        ano.encryptionAlg = e = new C07();
                    } else if (AnoServices.ENC_CLASSNAME_EX[algID].equals("3DES168")) {
                        ano.encryptionAlg = e = new C05();
                    } else if (AnoServices.ENC_CLASSNAME_EX[algID].equals("3DES112")) {
                        ano.encryptionAlg = e = new C10();
                    } else if (AnoServices.ENC_CLASSNAME_EX[algID].startsWith("RC4")) {
                        ano.encryptionAlg = e = new C12(true, AnoServices.CRYPTO_LEN[algID]);
                    }
                } else {
                    ano.encryptionAlg = e = (C00) Class
                            .forName("oracle.net.aso." + AnoServices.ENC_CLASSNAME[algID])
                            .newInstance();
                }
                e.c(ano.getSessionKey(), ano.getInitializationVector());
            } catch (Exception exception) {
                throw new NetException(317);
            }
        }
    }

}
