// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: T4CTTIoauthenticate.java

package oracle.jdbc.driver;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import oracle.jdbc.util.RepConversion;
import oracle.net.ns.BreakNetException;
import oracle.security.o3logon.O3LoginClientHelper;
import oracle.sql.converter.CharacterSetMetaData;

// Referenced classes of package oracle.jdbc.driver:
// T4CTTIfun, T4CMAREngine, DBConversion, T4CConnection,
// T4C7Oversion, T4CTTIoer, DatabaseError, OracleDriver,
// OracleLog

class T4CTTIoauthenticate extends T4CTTIfun {

    byte user[];
    String userStr;
    byte password[];
    String passwordStr;
    byte terminal[];
    byte machine[];
    byte sysUserName[];
    byte processID[];
    byte programName[];
    long flag;
    byte encryptedSK[];
    byte internalName[];
    byte externalName[];
    byte alterSession[];
    byte aclValue[];
    byte clientname[];
    String ressourceManagerId;
    T4CConnection conn;
    boolean isSessionTZ;
    Properties connectionValues;
    static final int SERVER_VERSION_81 = 8100;
    static final int KPZ_LOGON = 1;
    static final int KPZ_CPW = 2;
    static final int KPZ_SRVAUTH = 4;
    static final int KPZ_ENCRYPTED_PASSWD = 256;
    static final int KPZ_LOGON_MIGRATE = 16;
    static final int KPZ_LOGON_SYSDBA = 32;
    static final int KPZ_LOGON_SYSOPER = 64;
    static final int KPZ_LOGON_PRELIMAUTH = 128;
    static final int KPZ_PASSWD_ENCRYPTED = 256;
    static final int KPZ_LOGON_DBCONC = 512;
    static final int KPZ_PROXY_AUTH = 1024;
    static final int KPZ_SESSION_CACHE = 2048;
    static final int KPZ_PASSWD_IS_VFR = 4096;
    static final String AUTH_TERMINAL = "AUTH_TERMINAL";
    static final String AUTH_PROGRAM_NM = "AUTH_PROGRAM_NM";
    static final String AUTH_MACHINE = "AUTH_MACHINE";
    static final String AUTH_PID = "AUTH_PID";
    static final String AUTH_SESSKEY = "AUTH_SESSKEY";
    static final String AUTH_VFR_DATA = "AUTH_VFR_DATA";
    static final String AUTH_PASSWORD = "AUTH_PASSWORD";
    static final String AUTH_INTERNALNAME = "AUTH_INTERNALNAME_";
    static final String AUTH_EXTERNALNAME = "AUTH_EXTERNALNAME_";
    static final String AUTH_ACL = "AUTH_ACL";
    static final String AUTH_ALTER_SESSION = "AUTH_ALTER_SESSION";
    static final String AUTH_INITIAL_CLIENT_ROLE = "INITIAL_CLIENT_ROLE";
    static final String AUTH_VERSION_SQL = "AUTH_VERSION_SQL";
    static final String AUTH_VERSION_NO = "AUTH_VERSION_NO";
    static final String AUTH_XACTION_TRAITS = "AUTH_XACTION_TRAITS";
    static final String AUTH_VERSION_STATUS = "AUTH_VERSION_STATUS";
    static final String AUTH_SERIAL_NUM = "AUTH_SERIAL_NUM";
    static final String AUTH_SESSION_ID = "AUTH_SESSION_ID";
    static final String AUTH_CLIENT_CERTIFICATE = "AUTH_CLIENT_CERTIFICATE";
    static final String AUTH_PROXY_CLIENT_NAME = "PROXY_CLIENT_NAME";
    static final String AUTH_CLIENT_DN = "AUTH_CLIENT_DISTINGUISHED_NAME";
    static final String AUTH_INSTANCENAME = "AUTH_INSTANCENAME";
    static final String AUTH_DBNAME = "AUTH_DBNAME";
    static final String AUTH_INSTANCE_NO = "AUTH_INSTANCE_NO";
    static final String AUTH_SC_SERVER_HOST = "AUTH_SC_SERVER_HOST";
    static final String AUTH_SC_INSTANCE_NAME = "AUTH_SC_INSTANCE_NAME";
    static final String AUTH_SC_INSTANCE_ID = "AUTH_SC_INSTANCE_ID";
    static final String AUTH_SC_INSTANCE_START_TIME = "AUTH_SC_INSTANCE_START_TIME";
    static final String AUTH_SC_DBUNIQUE_NAME = "AUTH_SC_DBUNIQUE_NAME";
    static final String AUTH_SC_SERVICE_NAME = "AUTH_SC_SERVICE_NAME";
    static final String AUTH_SC_SVC_FLAGS = "AUTH_SC_SVC_FLAGS";
    static final String AUTH_COPYRIGHT = "AUTH_COPYRIGHT";
    static final String COPYRIGHT_STR = "\"Oracle\nEverybody follows\nSpeedy bits exchange\nStars await to glow\"\nThe preceding key is copyrighted by Oracle Corporation.\nDuplication of this key is not allowed without permission\nfrom Oracle Corporation. Copyright 2003 Oracle Corporation.";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:54_PDT_2005";

    T4CTTIoauthenticate(T4CMAREngine _mrengine, String _user, String _password, Properties info,
            long _mode, String _ressourceManagerId, T4CTTIoer _oer, T4CConnection _conn)
            throws SQLException {
        super((byte) 3, 0, (short) 0);
        isSessionTZ = true;
        connectionValues = new Properties();
        oer = _oer;
        setMarshalingEngine(_mrengine);
        user = _mrengine.conv.StringToCharBytes(_user);
        userStr = _user;
        passwordStr = _password;
        flag = _mode;
        ressourceManagerId = _ressourceManagerId;
        setSessionFields(info);
        conn = _conn;
        isSessionTZ = conn.ver.getVersionNumber() >= 8100;
    }

    void marshalOsesskey() throws IOException, SQLException {
        funCode = 118;
        marshalFunHeader();
        meg.marshalPTR();
        meg.marshalSB4(user.length);
        meg.marshalUB4(flag | 1L);
        meg.marshalPTR();
        int nbPairs = 3;
        if (programName != null) {
            nbPairs++;
        }
        meg.marshalUB4(nbPairs);
        meg.marshalPTR();
        meg.marshalPTR();
        meg.marshalCHR(user);
        byte keys[][] = new byte[nbPairs][];
        byte values[][] = new byte[nbPairs][];
        byte flags[] = new byte[nbPairs];
        int offset = 0;
        keys[offset++] = meg.conv.StringToCharBytes("AUTH_TERMINAL");
        if (programName != null) {
            keys[offset] = meg.conv.StringToCharBytes("AUTH_PROGRAM_NM");
            values[offset++] = programName;
        }
        keys[offset] = meg.conv.StringToCharBytes("AUTH_MACHINE");
        values[offset++] = machine;
        keys[offset] = meg.conv.StringToCharBytes("AUTH_PID");
        values[offset++] = processID;
        meg.marshalKEYVAL(keys, values, flags, nbPairs);
    }

    void receiveOsesskey() throws IOException, SQLException {
        byte[][] keys = (byte[][]) null;
        byte[][] values = (byte[][]) null;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    int nbPairs = this.meg.unmarshalUB2();

                    keys = new byte[nbPairs][];
                    values = new byte[nbPairs][];

                    this.meg.unmarshalKEYVAL(keys, values, nbPairs);

                    break;
                case 4:
                    this.oer.init();
                    this.oer.unmarshal();
                    try {
                        this.oer.processError();
                    } catch (SQLException sqlex) {
                        throw sqlex;
                    }

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
                if ((keys == null) || (keys.length < 1)) {
                    DatabaseError.throwSqlException(438);
                }

                this.encryptedSK = values[0];

                if ((this.encryptedSK == null) || (this.encryptedSK.length != 16))
                    DatabaseError.throwSqlException(438);
            }
        }

    }

    void marshalOauth() throws IOException, SQLException {
        funCode = 115;
        if (encryptedSK.length > 16) {
            DatabaseError.throwSqlException(413);
        }
        String trimUser = userStr.trim();
        String trimPwd = passwordStr.trim();
        passwordStr = null;
        String noQuotesUser = trimUser;
        String noQuotesPwd = trimPwd;
        if (trimUser.startsWith("\"") || trimUser.endsWith("\"")) {
            noQuotesUser = removeQuotes(trimUser);
        }
        if (trimPwd.startsWith("\"") || trimPwd.endsWith("\"")) {
            noQuotesPwd = removeQuotes(trimPwd);
        }
        O3LoginClientHelper loginHelper = new O3LoginClientHelper(meg.conv.isServerCSMultiByte);
        byte sessionKey[] = loginHelper.getSessionKey(noQuotesUser, noQuotesPwd, encryptedSK);
        byte passwordNet[] = meg.conv.StringToCharBytes(noQuotesPwd);
        byte pwdPadLen;
        if (passwordNet.length % 8 > 0) {
            pwdPadLen = (byte) (8 - passwordNet.length % 8);
        } else {
            pwdPadLen = 0;
        }
        byte paddedPwd[] = new byte[passwordNet.length + pwdPadLen];
        System.arraycopy(passwordNet, 0, paddedPwd, 0, passwordNet.length);
        byte ePwdOnSessKey[] = loginHelper.getEPasswd(sessionKey, paddedPwd);
        password = new byte[2 * paddedPwd.length + 1];
        if (password.length < 2 * ePwdOnSessKey.length) {
            DatabaseError.throwSqlException(413);
        }
        RepConversion.bArray2Nibbles(ePwdOnSessKey, password);
        password[password.length - 1] = RepConversion.nibbleToHex(pwdPadLen);
        marshalFunHeader();
        meg.marshalPTR();
        meg.marshalSB4(user.length);
        meg.marshalUB4(flag | 1L | 256L);
        meg.marshalPTR();
        boolean xa_case = false;
        if (!ressourceManagerId.equals("0000")) {
            xa_case = true;
        }
        int nbPairs = 6;
        if (xa_case) {
            nbPairs += 2;
        }
        nbPairs++;
        if (programName != null) {
            nbPairs++;
        }
        if (clientname != null) {
            nbPairs++;
        }
        meg.marshalUB4(nbPairs);
        meg.marshalPTR();
        meg.marshalPTR();
        meg.marshalCHR(user);
        byte keys[][] = new byte[nbPairs][];
        byte values[][] = new byte[nbPairs][];
        byte flags[] = new byte[nbPairs];
        int index = 0;
        keys[index] = meg.conv.StringToCharBytes("AUTH_PASSWORD");
        values[index++] = password;
        keys[index++] = meg.conv.StringToCharBytes("AUTH_TERMINAL");
        if (programName != null) {
            keys[index] = meg.conv.StringToCharBytes("AUTH_PROGRAM_NM");
            values[index++] = programName;
        }
        if (clientname != null) {
            keys[index] = meg.conv.StringToCharBytes("PROXY_CLIENT_NAME");
            values[index++] = clientname;
        }
        keys[index] = meg.conv.StringToCharBytes("AUTH_MACHINE");
        values[index++] = machine;
        keys[index] = meg.conv.StringToCharBytes("AUTH_PID");
        values[index++] = processID;
        if (xa_case) {
            keys[index] = meg.conv.StringToCharBytes("AUTH_INTERNALNAME_");
            keys[index][keys[index].length - 1] = 0;
            values[index++] = internalName;
            keys[index] = meg.conv.StringToCharBytes("AUTH_EXTERNALNAME_");
            keys[index][keys[index].length - 1] = 0;
            values[index++] = externalName;
        }
        keys[index] = meg.conv.StringToCharBytes("AUTH_ACL");
        values[index++] = aclValue;
        keys[index] = meg.conv.StringToCharBytes("AUTH_ALTER_SESSION");
        values[index] = alterSession;
        flags[index++] = 1;
        keys[index] = meg.conv.StringToCharBytes("AUTH_COPYRIGHT");
        values[index++] = meg.conv
                .StringToCharBytes("\"Oracle\nEverybody follows\nSpeedy bits exchange\nStars await to glow\"\nThe preceding key is copyrighted by Oracle Corporation.\nDuplication of this key is not allowed without permission\nfrom Oracle Corporation. Copyright 2003 Oracle Corporation.");
        meg.marshalKEYVAL(keys, values, flags, nbPairs);
    }

    void marshalOauth(int type, Properties prop, int gl_session_id, int gl_serial_nb)
            throws IOException, SQLException {
        funCode = 115;
        boolean sendUser = false;
        boolean sendDN = false;
        boolean sendCertificate = false;
        boolean sendRoles = false;
        byte dn[] = null;
        byte certificate[] = null;
        byte roles[][] = (byte[][]) null;
        if (type == 1) {
            String _userStr = prop.getProperty("PROXY_USER_NAME");
            user = meg.conv.StringToCharBytes(_userStr);
            sendUser = true;
        } else if (type == 2) {
            String _dnStr = prop.getProperty("PROXY_DISTINGUISHED_NAME");
            dn = meg.conv.StringToCharBytes(_dnStr);
            sendDN = true;
        } else {
            try {
                certificate = (byte[]) prop.get("PROXY_CERTIFICATE");
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < certificate.length; i++) {
                    String str = Integer.toHexString(0xff & certificate[i]);
                    int length = str.length();
                    if (length == 0) {
                        sb.append("00");
                        continue;
                    }
                    if (length == 1) {
                        sb.append('0');
                        sb.append(str);
                    } else {
                        sb.append(str);
                    }
                }

                certificate = sb.toString().getBytes();
            } catch (Exception e) {
            }
            sendCertificate = true;
        }
        String strRoles[] = null;
        try {
            strRoles = (String[]) prop.get("PROXY_ROLES");
        } catch (Exception e) {
        }
        if (strRoles != null) {
            roles = new byte[strRoles.length][];
            for (int i = 0; i < strRoles.length; i++) {
                roles[i] = meg.conv.StringToCharBytes(strRoles[i]);
            }

            sendRoles = true;
        }
        marshalFunHeader();
        if (sendUser) {
            meg.marshalPTR();
            meg.marshalSB4(user.length);
        } else {
            meg.marshalNULLPTR();
            meg.marshalSB4(0);
        }
        meg.marshalUB4(1025L);
        meg.marshalPTR();
        int nbPairs = 7;
        nbPairs++;
        if (sendDN || sendCertificate) {
            nbPairs++;
        }
        if (sendRoles) {
            nbPairs += roles.length;
        }
        if (programName != null) {
            nbPairs++;
        }
        meg.marshalUB4(nbPairs);
        meg.marshalPTR();
        meg.marshalPTR();
        if (sendUser) {
            meg.marshalCHR(user);
        }
        byte keys[][] = new byte[nbPairs][];
        byte values[][] = new byte[nbPairs][];
        byte flags[] = new byte[nbPairs];
        int index = 0;
        if (sendRoles) {
            for (int i = 0; i < roles.length; i++) {
                keys[index] = meg.conv.StringToCharBytes("INITIAL_CLIENT_ROLE");
                values[index] = roles[i];
                index++;
            }

        }
        if (sendDN) {
            keys[index] = meg.conv.StringToCharBytes("AUTH_CLIENT_DISTINGUISHED_NAME");
            values[index++] = dn;
        } else if (sendCertificate) {
            keys[index] = meg.conv.StringToCharBytes("AUTH_CLIENT_CERTIFICATE");
            values[index++] = certificate;
        }
        keys[index++] = meg.conv.StringToCharBytes("AUTH_TERMINAL");
        if (programName != null) {
            keys[index] = meg.conv.StringToCharBytes("AUTH_PROGRAM_NM");
            values[index++] = programName;
        }
        keys[index] = meg.conv.StringToCharBytes("AUTH_MACHINE");
        values[index++] = machine;
        keys[index] = meg.conv.StringToCharBytes("AUTH_PID");
        values[index++] = processID;
        keys[index] = meg.conv.StringToCharBytes("AUTH_ACL");
        values[index++] = aclValue;
        keys[index] = meg.conv.StringToCharBytes("AUTH_ALTER_SESSION");
        values[index] = alterSession;
        flags[index++] = 1;
        keys[index] = meg.conv.StringToCharBytes("AUTH_SESSION_ID");
        values[index++] = meg.conv.StringToCharBytes(Integer.toString(gl_session_id));
        keys[index] = meg.conv.StringToCharBytes("AUTH_SERIAL_NUM");
        values[index++] = meg.conv.StringToCharBytes(Integer.toString(gl_serial_nb));
        keys[index] = meg.conv.StringToCharBytes("AUTH_COPYRIGHT");
        values[index++] = meg.conv
                .StringToCharBytes("\"Oracle\nEverybody follows\nSpeedy bits exchange\nStars await to glow\"\nThe preceding key is copyrighted by Oracle Corporation.\nDuplication of this key is not allowed without permission\nfrom Oracle Corporation. Copyright 2003 Oracle Corporation.");
        meg.marshalKEYVAL(keys, values, flags, nbPairs);
    }

    void receiveOauth() throws IOException, SQLException {
        byte[][] keys = (byte[][]) null;
        byte[][] values = (byte[][]) null;
        int nbPairs = 0;
        while (true) {
            try {
                byte code = this.meg.unmarshalSB1();

                switch (code) {
                case 8:
                    nbPairs = this.meg.unmarshalUB2();
                    keys = new byte[nbPairs][];
                    values = new byte[nbPairs][];

                    this.meg.unmarshalKEYVAL(keys, values, nbPairs);

                    break;
                case 15:
                    this.oer.init();
                    this.oer.unmarshalWarning();
                    try {
                        this.oer.processWarning();
                    } catch (SQLWarning ea) {
                        this.conn.setWarnings(DatabaseError.addSqlWarning(this.conn.getWarnings(),
                                                                          ea));
                    }

                case 4:
                    this.oer.init();
                    this.oer.unmarshal();
                    try {
                        this.oer.processError();
                    } catch (SQLException sqlex) {
                        throw sqlex;
                    }

                    break;
                default:
                    DatabaseError.throwSqlException(401);
                }

                continue;
            } catch (BreakNetException ea) {
                for (int i = 0; i < nbPairs; i++) {
                    String keyStr = this.meg.conv.CharBytesToString(keys[i], keys[i].length);
                    String valueStr = "";
                    if (values[i] != null) {
                        valueStr = this.meg.conv.CharBytesToString(values[i], values[i].length);
                    }
                    this.connectionValues.setProperty(keyStr, valueStr);
                }
            }
        }

    }

    int getSessionId() {
        int sessionId = -1;
        String valueStr = connectionValues.getProperty("AUTH_SESSION_ID");
        try {
            sessionId = Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
        }
        return sessionId;
    }

    int getSerialNumber() {
        int serialNumber = -1;
        String valueStr = connectionValues.getProperty("AUTH_SERIAL_NUM");
        try {
            serialNumber = Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
        }
        return serialNumber;
    }

    void setSessionFields(Properties info) throws SQLException {
        String terminalStr = info.getProperty("v$session.terminal");
        String machineStr = info.getProperty("v$session.machine");
        String osuserStr = info.getProperty("v$session.osuser");
        String programStr = info.getProperty("v$session.program");
        String processStr = info.getProperty("v$session.process");
        String internalNameStr = info.getProperty("v$session.iname");
        String externalNameStr = info.getProperty("v$session.ename");
        String clientNameStr = info.getProperty("PROXY_CLIENT_NAME");
        if (terminalStr == null) {
            terminalStr = "unknown";
        }
        if (machineStr == null) {
            try {
                machineStr = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                machineStr = "jdbcclient";
            }
        }
        if (osuserStr == null) {
            osuserStr = OracleDriver.getSystemPropertyUserName();
            if (osuserStr == null) {
                osuserStr = "jdbcuser";
            }
        }
        if (processStr == null) {
            processStr = "1234";
        }
        if (internalNameStr == null) {
            internalNameStr = "jdbc_ttc_impl";
        }
        if (externalNameStr == null) {
            externalNameStr = "jdbc_" + ressourceManagerId;
        }
        terminal = meg.conv.StringToCharBytes(terminalStr);
        machine = meg.conv.StringToCharBytes(machineStr);
        sysUserName = meg.conv.StringToCharBytes(osuserStr);
        if (programStr != null) {
            programName = meg.conv.StringToCharBytes(programStr);
        }
        processID = meg.conv.StringToCharBytes(processStr);
        internalName = meg.conv.StringToCharBytes(internalNameStr);
        externalName = meg.conv.StringToCharBytes(externalNameStr);
        if (clientNameStr != null) {
            clientname = meg.conv.StringToCharBytes(clientNameStr);
        }
        TimeZone tz = TimeZone.getDefault();
        int l_rawOffset = tz.getRawOffset();
        int hr = l_rawOffset / 0x36ee80;
        int mi = (l_rawOffset / 60000) % 60;
        if (tz.useDaylightTime() && tz.inDaylightTime(new Date())) {
            if (hr > 0) {
                hr--;
            } else {
                hr++;
            }
        }
        String TZ = (hr >= 0 ? "+" + hr : "" + hr) + (mi >= 10 ? ":" + mi : ":0" + mi);
        alterSession = meg.conv.StringToCharBytes("ALTER SESSION SET "
                + (isSessionTZ ? "TIME_ZONE='" + TZ + "'" : "") + " NLS_LANGUAGE='"
                + CharacterSetMetaData.getNLSLanguage(Locale.getDefault()) + "' NLS_TERRITORY='"
                + CharacterSetMetaData.getNLSTerritory(Locale.getDefault()) + "' ");
        aclValue = meg.conv.StringToCharBytes("4400");
        alterSession[alterSession.length - 1] = 0;
    }

    String removeQuotes(String str) {
        int first = 0;
        int last = str.length() - 1;
        int i = 0;
        do {
            if (i >= str.length()) {
                break;
            }
            if (str.charAt(i) != '"') {
                first = i;
                break;
            }
            i++;
        } while (true);
        i = str.length() - 1;
        do {
            if (i < 0) {
                break;
            }
            if (str.charAt(i) != '"') {
                last = i;
                break;
            }
            i--;
        } while (true);
        String result = str.substring(first, last + 1);
        return result;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CTTIoauthenticate"));
        } catch (Exception e) {
        }
    }
}
