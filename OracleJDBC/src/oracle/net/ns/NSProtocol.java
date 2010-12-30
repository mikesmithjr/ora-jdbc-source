// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc

package oracle.net.ns;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Properties;
import oracle.net.ano.Ano;
import oracle.net.nl.NLException;
import oracle.net.nl.NVFactory;
import oracle.net.nl.NVNavigator;
import oracle.net.nl.NVPair;
import oracle.net.nt.ConnOption;
import oracle.net.nt.NTAdapter;
import oracle.net.resolver.AddrResolution;

// Referenced classes of package oracle.net.ns:
// SessionAtts, NetException, ClientProfile, ConnectPacket,
// Packet, AcceptPacket, RedirectPacket, RefusePacket,
// MarkerPacket, NetOutputStream, NetInputStream, Communication,
// SQLnetDef

public class NSProtocol implements Communication, SQLnetDef {

    private static final boolean ACTIVATE_ANO = true;
    private AddrResolution addrRes;
    private SessionAtts sAtts;
    private MarkerPacket mkPkt;
    private Packet packet;

    public NSProtocol() {
        sAtts = new SessionAtts(32767, 32767);
        sAtts.connected = false;
    }

    public void connect(String s, Properties properties) throws IOException, NetException {
        if (sAtts.connected) {
            throw new NetException(201);
        }
        if (s == null) {
            throw new NetException(208);
        }
        NVFactory nvfactory = new NVFactory();
        NVNavigator nvnavigator = new NVNavigator();
        Object obj4 = null;
        String s1 = null;
        addrRes = new AddrResolution(s, properties);
        if (addrRes.connection_revised) {
            s = addrRes.getTNSAddress();
            properties = addrRes.getUp();
        }
        sAtts.profile = new ClientProfile(properties);
        establishConnection(s);
        Object obj5 = null;
        try {
            obj5 = Class.forName("oracle.net.ano.Ano").newInstance();
            sAtts.anoEnabled = true;
        } catch (Exception exception) {
            sAtts.anoEnabled = false;
        }
        if (obj5 != null) {
            ((Ano) obj5).init(sAtts);
            sAtts.ano = (Ano) obj5;
            sAtts.anoEnabled = true;
        }
        label0: do {
            ConnectPacket connectpacket = new ConnectPacket(sAtts);
            connectpacket.send();
            packet = new Packet(sAtts, sAtts.getSDU());
            packet.receive();
            switch (packet.type) {
            case 2: // '\002'
                AcceptPacket acceptpacket = new AcceptPacket(packet);
                break label0;

            case 11: // '\013'
                break;

            case 5: // '\005'
                RedirectPacket redirectpacket = new RedirectPacket(packet);
                ConnOption connoption = sAtts.cOption;
                addrRes.connection_redirected = true;
                sAtts.cOption.nt.disconnect();
                sAtts = establishConnection(redirectpacket.getData());
                sAtts.cOption.restoreFromOrigCoption(connoption);
                break;

            case 4: // '\004'
                RefusePacket refusepacket = new RefusePacket(packet);
                sAtts.cOption.nt.disconnect();
                sAtts.cOption = null;
                establishConnection(null);
                if (sAtts.cOption == null) {
                    try {
                        NVPair nvpair = nvnavigator.findNVPairRecurse(nvfactory
                                .createNVPair(refusepacket.getData()), "ERROR");
                        s1 = nvnavigator.findNVPairRecurse(nvpair, "CODE").valueToString();
                    } catch (NLException nlexception) {
                        System.err.println(nlexception.getMessage());
                    }
                    throw new NetException(Integer.parseInt(s1), s + "\n");
                }
                break;

            case 3: // '\003'
            case 6: // '\006'
            case 7: // '\007'
            case 8: // '\b'
            case 9: // '\t'
            case 10: // '\n'
            default:
                sAtts.cOption.nt.disconnect();
                throw new NetException(205);
            }
        } while (true);
        setNetStreams();
        sAtts.connected = true;
        String s2 = (String) sAtts.nt.getOption(6);
        if (s2 != null && s2.equalsIgnoreCase("false")) {
            throw new NetException(405);
        }
        if (sAtts.ano != null) {
            sAtts.ano.negotiation();
            String s3 = (String) sAtts.nt.getOption(2);
            if (s3 != null && s3.equals("TRUE")) {
                try {
                    Method method = sAtts.ano.getClass().getMethod("getEncryptionAlg", null);
                    if (method.invoke(sAtts.ano, null) != null) {
                        throw new NetException(406);
                    }
                } catch (Exception exception1) {
                }
            }
        }
        packet = null;
        Object obj = null;
        Object obj1 = null;
        Object obj3 = null;
        Object obj2 = null;
    }

    public void disconnect() throws IOException, NetException {
        if (!sAtts.connected) {
            throw new NetException(200);
        }
        IOException ioexception = null;
        try {
            sAtts.nsOutputStream.close();
        } catch (IOException ioexception1) {
            ioexception = ioexception1;
        }
        sAtts.connected = false;
        sAtts.cOption.nt.disconnect();
        if (ioexception != null) {
            throw ioexception;
        } else {
            return;
        }
    }

    public void sendBreak() throws IOException, NetException {
        if (!sAtts.connected) {
            throw new NetException(200);
        } else {
            sendMarker(1);
            mkPkt = null;
            return;
        }
    }

    public void sendReset() throws IOException, NetException {
        if (!sAtts.connected) {
            throw new NetException(200);
        }
        sendMarker(2);
        do {
            if (!sAtts.onBreakReset) {
                break;
            }
            packet = new Packet(sAtts, sAtts.getSDU());
            packet.receive();
            if (packet.type == 12) {
                mkPkt = new MarkerPacket(packet);
                if (mkPkt.data == 2) {
                    sAtts.onBreakReset = false;
                }
            }
        } while (true);
        mkPkt = null;
    }

    public InputStream getInputStream() throws NetException {
        if (!sAtts.connected) {
            throw new NetException(200);
        } else {
            return sAtts.nsInputStream;
        }
    }

    public OutputStream getOutputStream() throws NetException {
        if (!sAtts.connected) {
            throw new NetException(200);
        } else {
            return sAtts.nsOutputStream;
        }
    }

    private SessionAtts establishConnection(String s) throws NetException, IOException {
        sAtts.cOption = addrRes.resolveAndExecute(s);
        if (sAtts.cOption == null) {
            return null;
        } else {
            sAtts.nt = sAtts.cOption.nt;
            sAtts.ntInputStream = sAtts.cOption.nt.getInputStream();
            sAtts.ntOutputStream = sAtts.cOption.nt.getOutputStream();
            sAtts.setTDU(sAtts.cOption.tdu);
            sAtts.setSDU(sAtts.cOption.sdu);
            sAtts.nsOutputStream = new NetOutputStream(sAtts, 255);
            sAtts.nsInputStream = new NetInputStream(sAtts);
            return sAtts;
        }
    }

    private void setNetStreams() throws NetException, IOException {
        sAtts.nsOutputStream = new NetOutputStream(sAtts);
        sAtts.nsInputStream = new NetInputStream(sAtts);
    }

    private void sendMarker(int i) throws IOException, NetException {
        if (i == 1) {
            mkPkt = new MarkerPacket(sAtts);
        } else {
            mkPkt = new MarkerPacket(sAtts, i);
        }
        mkPkt.send();
    }

    public void setO3logSessionKey(byte abyte0[]) throws NetException, NetException {
        if (abyte0 != null) {
            sAtts.ano.setO3logSessionKey(abyte0);
        }
    }

    public void setOption(int i, Object obj) throws NetException, IOException {
        if (i > 0 && i < 10) {
            NTAdapter ntadapter = sAtts.getNTAdapter();
            ntadapter.setOption(i, obj);
        }
    }

    public Object getOption(int i) throws NetException, IOException {
        if (i > 0 && i < 10) {
            NTAdapter ntadapter = sAtts.getNTAdapter();
            return ntadapter.getOption(i);
        } else {
            return null;
        }
    }

    public void abort() throws NetException, IOException {
        NTAdapter ntadapter = sAtts.getNTAdapter();
        if (ntadapter != null) {
            ntadapter.abort();
        }
    }
}
