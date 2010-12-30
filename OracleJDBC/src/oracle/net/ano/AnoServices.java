// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: AnoServices

package oracle.net.ano;

public interface AnoServices {

    public static final int AUTHENTICATION = 1;
    public static final int MAXSERVICE = 4;
    public static final String ENC_CLASSNAME_EX[] = { "", "RC4_40", "DES56C", "DES40C", "", "",
            "RC4_256", "", "RC4_56", "", "RC4_128", "3DES112", "3DES168" };
    public static final int UB2_TYPE = 3;
    public static final int ARRAY_PACKET_HEADER_LENGTH = 10;
    public static final int STATUS_LENGTH = 2;
    public static final int SERVICE_HEADER_LENGTH = 8;
    public static final int MINSERVICE = 1;
    public static final int REJECTED = 1;
    public static final int MIN_TYPE = 0;
    public static final int REQUESTED = 2;
    public static final int UB1_LENGTH = 1;
    public static final int SUPERVISOR = 4;
    public static final int RELEASE = 1;
    public static final int NA_HEADER_SIZE = 13;
    public static final short NO_ERROR = 0;
    public static final long NA_MAGIC = 0xffffffffdeadbeefL;
    public static final int RAW_TYPE = 1;
    public static final int ENCRYPTION = 2;
    public static final String AUTH_CLASSNAME[] = { "", "", "" };
    public static final int VERSION_LENGTH = 4;
    public static final String DATAINTEGRITY_CLASSNAME[] = { "", "MD5" };
    public static final String ENC_CLASSNAME[] = { "", "RC4_40", "DES56C", "DES40C", "", "",
            "RC4_256", "DES56R", "RC4_56", "DES40R", "RC4_128", "3DES112", "3DES168" };
    public static final String SERV_CLASSNAME[] = { "NULLSERVICE", "AuthenticationService",
            "EncryptionService", "DataIntegrityService", "SupervisorService" };
    public static final int ACCEPTED = 0;
    public static final int UB4_LENGTH = 4;
    public static final int STRING_TYPE = 0;
    public static final int CRYPTO_LEN[] = { 0, 40, 56, 40, 0, 0, 256, 56, 56, 40, 128, 112, 168 };
    public static final byte ENC_ID[] = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
    public static final int DATAINTEGRITY = 3;
    public static final int STATUS_TYPE = 6;
    public static final boolean VERIFYING = false;
    public static final int NULLSERVICE = 0;
    public static final int NA_MAGIC_SIZE = 4;
    public static final String AUTH_NAME[] = { "null", "auth", "beq" };
    public static final byte DATAINTEGRITY_ID[] = { 0, 1 };
    public static final int VERSION_TYPE = 5;
    public static final int REQUIRED = 3;
    public static final int MAX_TYPE = 7;
    public static final long DEADBEEF = 0xffffffffdeadbeefL;
    public static final String SERVICES_INORDER[] = { "Supervisor", "Authentication", "Encryption",
            "DataIntegrity" };
    public static final int VERSION = 8;
    public static final int UB4_TYPE = 4;
    public static final int PORT = 0;
    public static final int PORTUPDATE = 0;
    public static final int SUBPACKET_LENGTH = 4;
    public static final int UPDATE = 5;
    public static final int UB2_LENGTH = 2;
    public static final String SERVICES[] = { "NULLSERVICE", "Authentication", "Encryption",
            "DataIntegrity", "Supervisor" };
    public static final byte AUTH_ID[] = { 0, 1, 2 };
    public static final int UB1_TYPE = 2;
    public static final int ARRAY_TYPE = 7;
    public static final String SERV_INORDER_CLASSNAME[] = { "SupervisorService",
            "AuthenticationService", "EncryptionService", "DataIntegrityService" };

}
