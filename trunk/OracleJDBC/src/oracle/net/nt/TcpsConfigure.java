// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc

package oracle.net.nt;

import java.io.IOException;
import javax.net.ssl.SSLSocket;
import oracle.net.nl.NLException;
import oracle.net.nl.NVFactory;
import oracle.net.nl.NVPair;
import oracle.net.ns.NetException;

public class TcpsConfigure {

    static final boolean DEBUG = false;
    public static final String VALID_SSL_VERSION_STRINGS[] = { "0", "undetermined", "2", "2.0",
            "version 2", "3", "3.0", "version 3 only", "1", "1.0", "version 1 only", "1 or 3",
            "1.0 or 3.0", "version 1 or version 3" };
    public static final String TABLE_ENABLED_SSL_PROTOCOLS[][] = {
            { "TLSv1", "SSLv3", "SSLv2Hello" }, { "SSLv2Hello" }, { "SSLv3" }, { "TLSv1" },
            { "TLSv1", "SSLv3" } };
    public static final int VALID_SSL_STRING_TO_PROTOCOLS_MAP[] = { 0, 0, 1, 1, 1, 2, 2, 2, 3, 3,
            3, 4, 4, 4 };

    private TcpsConfigure() {
    }

    public static void configureVersion(SSLSocket sslsocket, String s) throws NetException,
            IOException {
        if (s == null) {
            s = System.getProperty("oracle.net.ssl_version");
        }
        int i = 0;
        if (s != null) {
            if (s.startsWith("(") && s.endsWith(")")) {
                s = "(ssl_version=" + s.substring(1);
            } else {
                s = "(ssl_version=" + s + ")";
            }
            try {
                NVPair nvpair = (new NVFactory()).createNVPair(s);
                String s1 = nvpair.getAtom();
                int j = 0;
                do {
                    if (j >= VALID_SSL_VERSION_STRINGS.length) {
                        break;
                    }
                    if (s1.equalsIgnoreCase(VALID_SSL_VERSION_STRINGS[j])) {
                        i = j;
                        break;
                    }
                    j++;
                } while (true);
            } catch (NLException nlexception) {
                throw new NetException(400, s);
            }
        }
        if (i >= VALID_SSL_VERSION_STRINGS.length) {
            throw new NetException(400);
        }
        String as[] = TABLE_ENABLED_SSL_PROTOCOLS[VALID_SSL_STRING_TO_PROTOCOLS_MAP[i]];
        try {
            sslsocket.setEnabledProtocols(as);
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new NetException(401, illegalargumentexception.toString());
        }
    }

    public static void configureCipherSuites(SSLSocket sslsocket, String s) throws NetException,
            IOException {
        if (s == null) {
            s = System.getProperty("oracle.net.ssl_cipher_suites");
        }
        if (s == null) {
            return;
        }
        if (s.startsWith("(") && s.endsWith(")")) {
            s = "(cipher_suites=" + s + ")";
        } else {
            s = "(cipher_suites=(" + s + "))";
        }
        try {
            NVPair nvpair = (new NVFactory()).createNVPair(s);
            String as[] = new String[nvpair.getListSize()];
            if (nvpair.getRHSType() == NVPair.LIST_COMMASEP
                    || nvpair.getRHSType() == NVPair.RHS_LIST) {
                for (int i = 0; i < nvpair.getListSize(); i++) {
                    as[i] = nvpair.getListElement(i).getName();
                }

            } else {
                throw new NetException(403, s);
            }
            sslsocket.setEnabledCipherSuites(as);
        } catch (NLException nlexception) {
            throw new NetException(403, s);
        } catch (IllegalArgumentException illegalargumentexception) {
            throw new NetException(404, illegalargumentexception.toString());
        }
    }

    public static boolean matchServerDN(String s, String s1, boolean flag) {
        s = normalizeDN(s);
        if (s == null) {
            return false;
        }
        if (flag) {
            s1 = normalizeDN(s1);
            if (s1 == null) {
                return false;
            }
            if (s1.equals(s)) {
                return true;
            }
            s = reverseDN(s);
            return s1.equals(s);
        }
        int i = s.indexOf("CN=");
        if (i != -1) {
            if (s.indexOf(',', i) != -1) {
                s = s.substring(i, s.indexOf(',', i));
            } else {
                s = s.substring(i);
            }
            if (s1.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public static String normalizeDN(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        Object obj = null;
        Object obj1 = null;
        int i = 0;
        int j = 0;
        s = s.trim();
        do {
            if ((i = s.indexOf('=', i)) == -1) {
                break;
            }
            String s1 = s.substring(j, i);
            s1 = s1.trim();
            stringbuffer.append(s1.toUpperCase());
            stringbuffer.append('=');
            j = i;
            if (j >= s.length() - 1) {
                return null;
            }
            i = s.indexOf(',', i);
            if (i == -1) {
                String s2 = s.substring(j + 1);
                stringbuffer.append(s2.trim());
                break;
            }
            String s3 = s.substring(j + 1, i);
            stringbuffer.append(s3.trim());
            stringbuffer.append(',');
            if (i >= s.length() - 1) {
                return null;
            }
            j = i + 1;
        } while (true);
        return stringbuffer.toString();
    }

    public static String reverseDN(String s) {
        StringBuffer stringbuffer = new StringBuffer();
        Object obj = null;
        int i = s.length();
        int j = i;
        do {
            i = s.lastIndexOf(',', i);
            if (i == -1) {
                stringbuffer.append(s.substring(0, j));
                break;
            }
            String s1 = s.substring(i + 1, j);
            stringbuffer.append(s1);
            stringbuffer.append(',');
            j = i;
        } while (--i != -1);
        return stringbuffer.toString();
    }

}
