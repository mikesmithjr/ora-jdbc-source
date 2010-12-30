// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: packimports(5) braces fieldsfirst noctor nonlb space lnc
// Source File Name: DBConversion.java

package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.util.RepConversion;
import oracle.sql.CharacterSet;
import oracle.sql.converter.CharacterSetMetaData;

// Referenced classes of package oracle.jdbc.driver:
// OracleConversionInputStream, OracleConversionReader, OracleLog, DatabaseError

public class DBConversion {

    public static final boolean DO_CONVERSION_WITH_REPLACEMENT = true;
    public static final short ORACLE8_PROD_VERSION = 8030;
    protected short serverNCharSetId;
    protected short serverCharSetId;
    protected short clientCharSetId;
    protected CharacterSet serverCharSet;
    protected CharacterSet serverNCharSet;
    protected CharacterSet clientCharSet;
    protected CharacterSet asciiCharSet;
    protected boolean isServerCharSetFixedWidth;
    protected boolean isServerNCharSetFixedWidth;
    protected int c2sNlsRatio;
    protected int s2cNlsRatio;
    protected int sMaxCharSize;
    protected int cMaxCharSize;
    protected int maxNCharSize;
    protected boolean isServerCSMultiByte;
    public static final short DBCS_CHARSET = -1;
    public static final short UCS2_CHARSET = -5;
    public static final short ASCII_CHARSET = 1;
    public static final short ISO_LATIN_1_CHARSET = 31;
    public static final short AL24UTFFSS_CHARSET = 870;
    public static final short UTF8_CHARSET = 871;
    public static final short AL32UTF8_CHARSET = 873;
    public static final short AL16UTF16_CHARSET = 2000;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:50_PDT_2005";

    public DBConversion(short svrCharSet, short drvrCharSet, short svrNCharSet) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.DBConversion( svrCharSet="
                    + svrCharSet + ", clientCharSet=" + clientCharSet + ", svrNCharSet="
                    + svrNCharSet + ")", this);
            OracleLog.recursiveTrace = false;
        }
        switch (drvrCharSet) {
        default:
            unexpectedCharset(drvrCharSet);
            // fall through

        case -5:
        case -1:
        case 1: // '\001'
        case 2: // '\002'
        case 31: // '\037'
        case 178:
        case 870:
        case 871:
        case 873:
            serverCharSetId = svrCharSet;
            break;
        }
        clientCharSetId = drvrCharSet;
        serverCharSet = CharacterSet.make(serverCharSetId);
        serverNCharSetId = svrNCharSet;
        serverNCharSet = CharacterSet.make(serverNCharSetId);
        if (drvrCharSet == -1) {
            clientCharSet = serverCharSet;
        } else {
            clientCharSet = CharacterSet.make(clientCharSetId);
        }
        c2sNlsRatio = CharacterSetMetaData.getRatio(svrCharSet, drvrCharSet);
        s2cNlsRatio = CharacterSetMetaData.getRatio(drvrCharSet, svrCharSet);
        sMaxCharSize = CharacterSetMetaData.getRatio(svrCharSet, 1);
        cMaxCharSize = CharacterSetMetaData.getRatio(drvrCharSet, 1);
        maxNCharSize = CharacterSetMetaData.getRatio(svrNCharSet, 1);
        isServerCSMultiByte = CharacterSetMetaData.getRatio(svrCharSet, 1) != 1;
        isServerCharSetFixedWidth = CharacterSetMetaData.isFixedWidth(svrCharSet);
        isServerNCharSetFixedWidth = CharacterSetMetaData.isFixedWidth(svrNCharSet);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINER, "DBConversion.c2sNlsRatio=" + c2sNlsRatio
                    + "DBConversion.s2cNlsRatio=" + s2cNlsRatio, this);
            OracleLog.recursiveTrace = false;
        }
    }

    public short getServerCharSetId() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getDbCharSet(): returned "
                    + serverCharSetId, this);
            OracleLog.recursiveTrace = false;
        }
        return serverCharSetId;
    }

    public short getNCharSetId() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getNCharSetId(): returned "
                    + serverNCharSetId, this);
            OracleLog.recursiveTrace = false;
        }
        return serverNCharSetId;
    }

    public boolean IsNCharFixedWith() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.IsNCharFixedWith()", this);
            OracleLog.recursiveTrace = false;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINER,
                                           "DBConversion.IsNCharFixedWith(): serverNCharSetId="
                                                   + serverNCharSetId + ", AL16UTF16_CHARSET="
                                                   + 2000, this);
            OracleLog.recursiveTrace = false;
        }
        return serverNCharSetId == 2000;
    }

    public short getClientCharSet() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getDriverCharSet()", this);
            OracleLog.recursiveTrace = false;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINER,
                                           "DBConversion.getDriverCharSet(): clientCharSetId="
                                                   + clientCharSetId + ", serverCharSetId="
                                                   + serverCharSetId, this);
            OracleLog.recursiveTrace = false;
        }
        if (clientCharSetId == -1) {
            return serverCharSetId;
        } else {
            return clientCharSetId;
        }
    }

    public CharacterSet getDbCharSetObj() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getDbCharSetObj(): returned "
                    + serverCharSet, this);
            OracleLog.recursiveTrace = false;
        }
        return serverCharSet;
    }

    public CharacterSet getDriverCharSetObj() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.getDriverCharSetObj(): returned "
                                                   + clientCharSet, this);
            OracleLog.recursiveTrace = false;
        }
        return clientCharSet;
    }

    public CharacterSet getDriverNCharSetObj() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.getDriverNCharSetObj(): returned "
                                                   + serverNCharSet, this);
            OracleLog.recursiveTrace = false;
        }
        return serverNCharSet;
    }

    public static final short findDriverCharSet(short svrCharSet, short oraVersion) {
        short driver_charset = 0;
        switch (svrCharSet) {
        case 1: // '\001'
        case 2: // '\002'
        case 31: // '\037'
        case 178:
        case 873:
            driver_charset = svrCharSet;
            break;

        default:
            driver_charset = (short) (oraVersion < 8030 ? 870 : 871);
            break;
        }
        return driver_charset;
    }

    public static final byte[] stringToDriverCharBytes(String str, short charset)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.stringToDriverCharBytes(str="
                    + str + ", charset=" + charset + ")");
            OracleLog.recursiveTrace = false;
        }
        if (str == null) {
            return null;
        }
        byte ret_bytes[] = null;
        switch (charset) {
        case -5:
        case 2000:
            ret_bytes = CharacterSet.stringToAL16UTF16Bytes(str);
            break;

        case 1: // '\001'
        case 2: // '\002'
        case 31: // '\037'
        case 178:
            ret_bytes = CharacterSet.stringToASCII(str);
            break;

        case 870:
        case 871:
            ret_bytes = CharacterSet.stringToUTF(str);
            break;

        case 873:
            ret_bytes = CharacterSet.stringToAL32UTF8(str);
            break;

        case -1:
        default:
            unexpectedCharset(charset);
            break;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.stringToDriverCharBytes(str, charset): returned "
                                                   + OracleLog
                                                           .bytesToPrintableForm("Driver bytes=",
                                                                                 ret_bytes));
            OracleLog.recursiveTrace = false;
        }
        return ret_bytes;
    }

    public byte[] StringToCharBytes(String str) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.StringToCharBytes(" + str
                    + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINER,
                                           "DBConversion.StringToCharBytes(str): clientCharSetId="
                                                   + clientCharSetId, this);
            OracleLog.recursiveTrace = false;
        }
        if (str.length() == 0) {
            return null;
        }
        if (clientCharSetId == -1) {
            return serverCharSet.convertWithReplacement(str);
        } else {
            return stringToDriverCharBytes(str, clientCharSetId);
        }
    }

    public String CharBytesToString(byte bytes[], int nbytes) throws SQLException {
        return CharBytesToString(bytes, nbytes, false);
    }

    public String CharBytesToString(byte bytes[], int nbytes, boolean useReplacementChar)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.CharBytesToString("
                    + OracleLog.bytesToPrintableForm("bytes=", bytes) + ", nbytes=" + nbytes + ")",
                                           this);
            OracleLog.recursiveTrace = false;
        }
        String ret_str = null;
        if (bytes.length == 0) {
            return ret_str;
        }
        switch (clientCharSetId) {
        case -5:
            ret_str = CharacterSet.AL16UTF16BytesToString(bytes, nbytes);
            break;

        case 1: // '\001'
        case 2: // '\002'
        case 31: // '\037'
        case 178:
            ret_str = new String(bytes, 0, 0, nbytes);
            break;

        case 870:
        case 871:
            ret_str = CharacterSet.UTFToString(bytes, 0, nbytes, useReplacementChar);
            break;

        case 873:
            ret_str = CharacterSet.AL32UTF8ToString(bytes, 0, nbytes, useReplacementChar);
            break;

        case -1:
            ret_str = serverCharSet.toStringWithReplacement(bytes, 0, nbytes);
            break;

        default:
            unexpectedCharset(clientCharSetId);
            break;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.CharBytesToString(bytes, nbytes): returned "
                                                   + ret_str, this);
            OracleLog.recursiveTrace = false;
        }
        return ret_str;
    }

    public String NCharBytesToString(byte bytes[], int nbytes) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.NCharBytesToString("
                    + OracleLog.bytesToPrintableForm("bytes[], nbytes=", bytes) + ", nbytes="
                    + nbytes + ")", this);
            OracleLog.recursiveTrace = false;
        }
        String ret_str = null;
        if (clientCharSetId == -1) {
            ret_str = serverNCharSet.toStringWithReplacement(bytes, 0, nbytes);
        } else {
            switch (serverNCharSetId) {
            case -5:
            case 2000:
                ret_str = CharacterSet.AL16UTF16BytesToString(bytes, nbytes);
                break;

            case 1: // '\001'
            case 2: // '\002'
            case 31: // '\037'
            case 178:
                ret_str = new String(bytes, 0, 0, nbytes);
                break;

            case 870:
            case 871:
                ret_str = CharacterSet.UTFToString(bytes, 0, nbytes);
                break;

            case 873:
                ret_str = CharacterSet.AL32UTF8ToString(bytes, 0, nbytes);
                break;

            case -1:
                ret_str = serverCharSet.toStringWithReplacement(bytes, 0, nbytes);
                break;

            default:
                unexpectedCharset(clientCharSetId);
                break;
            }
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.NCharBytesToString(bytes[], nbytes): returned "
                                                   + ret_str, this);
            OracleLog.recursiveTrace = false;
        }
        return ret_str;
    }

    public int javaCharsToCHARBytes(char chars[], int nchars, byte bytes[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToCHARBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", nchars=" + nchars + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return javaCharsToCHARBytes(chars, nchars, bytes, clientCharSetId);
    }

    public int javaCharsToCHARBytes(char chars[], int charOffset, byte bytes[], int byteOffset,
            int nchars) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToCHARBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", charOffset=" + charOffset + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", byteOffset="
                    + byteOffset + ", nchars=" + nchars + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return javaCharsToCHARBytes(chars, charOffset, bytes, byteOffset, clientCharSetId, nchars);
    }

    public int javaCharsToNCHARBytes(char chars[], int nchars, byte bytes[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToNCHARBytes(chars="
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", nchars=" + nchars + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return javaCharsToCHARBytes(chars, nchars, bytes, serverNCharSetId);
    }

    public int javaCharsToNCHARBytes(char chars[], int charOffset, byte bytes[], int byteOffset,
            int nchars) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToNCHARBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", charOffset=" + charOffset + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", byteOffset="
                    + byteOffset + ", nchars=" + nchars + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return javaCharsToCHARBytes(chars, charOffset, bytes, byteOffset, serverNCharSetId, nchars);
    }

    protected int javaCharsToCHARBytes(char chars[], int nchars, byte bytes[], short cs)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToCHARBytes("
                    + OracleLog
                            .bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars,
                                                                                         nchars))
                    + ", nchars=" + nchars + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", cs=" + cs, this);
            OracleLog.recursiveTrace = false;
        }
        return javaCharsToCHARBytes(chars, 0, bytes, 0, cs, nchars);
    }

    protected int javaCharsToCHARBytes(char chars[], int charOffset, byte bytes[], int byteOffset,
            short cs, int nchars) throws SQLException {
        int ret = 0;
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToCHARBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", charOffset=" + charOffset + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", byteOffset="
                    + byteOffset + ", cs=" + cs + ", nchars=" + nchars + ")", this);
            OracleLog.recursiveTrace = false;
        }
        switch (cs) {
        case -5:
        case 2000:
            ret = CharacterSet.convertJavaCharsToAL16UTF16Bytes(chars, charOffset, bytes,
                                                                byteOffset, nchars);
            break;

        case 2: // '\002'
        case 178:
            byte tempBytes[] = new byte[nchars];
            tempBytes = clientCharSet.convertWithReplacement(new String(chars, charOffset, nchars));
            System.arraycopy(tempBytes, 0, bytes, 0, tempBytes.length);
            ret = tempBytes.length;
            break;

        case 1: // '\001'
            ret = CharacterSet.convertJavaCharsToASCIIBytes(chars, charOffset, bytes, byteOffset,
                                                            nchars);
            break;

        case 31: // '\037'
            ret = CharacterSet.convertJavaCharsToISOLATIN1Bytes(chars, charOffset, bytes,
                                                                byteOffset, nchars);
            break;

        case 870:
        case 871:
            ret = CharacterSet.convertJavaCharsToUTFBytes(chars, charOffset, bytes, byteOffset,
                                                          nchars);
            break;

        case 873:
            ret = CharacterSet.convertJavaCharsToAL32UTF8Bytes(chars, charOffset, bytes,
                                                               byteOffset, nchars);
            break;

        case -1:
            ret = javaCharsToDbCsBytes(chars, charOffset, bytes, byteOffset, nchars);
            break;

        default:
            unexpectedCharset(clientCharSetId);
            break;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.javaCharsToCHARBytes(chars, nchars, bytes[], cs): returned "
                                                   + ret, this);
            OracleLog.recursiveTrace = false;
        }
        return ret;
    }

    public int CHARBytesToJavaChars(byte bytes[], int byteOffset, char chars[], int charOffset,
            int nbytes[], int charSize) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.CHARBytesToJavaChars("
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + " byteOffset="
                    + byteOffset + ", "
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + "charOffset=" + charOffset + ", nbytes[0]=" + nbytes[0] + ", charSize="
                    + charSize + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return _CHARBytesToJavaChars(bytes, byteOffset, chars, charOffset, clientCharSetId, nbytes,
                                     charSize, serverCharSet, serverNCharSet, clientCharSet, false);
    }

    public int NCHARBytesToJavaChars(byte bytes[], int byteOffset, char chars[], int charOffset,
            int nbytes[], int charSize) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.NCHARBytesToJavaChars("
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + " byteOffset="
                    + byteOffset + ", "
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + "charOffset=" + charOffset + ", nbytes[0]=" + nbytes[0] + ", charSize="
                    + charSize + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return _CHARBytesToJavaChars(bytes, byteOffset, chars, charOffset, serverNCharSetId,
                                     nbytes, charSize, serverCharSet, serverNCharSet,
                                     clientCharSet, true);
    }

    static final int _CHARBytesToJavaChars(byte bytes[], int byteOffset, char chars[],
            int charOffset, short cs, int nbytes[], int charSize, CharacterSet _m_databaseCs,
            CharacterSet _m_databaseNCs, CharacterSet _m_driverCs, boolean isNCharData)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion._CHARBytesToJavaChars("
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", byteOffset="
                    + byteOffset + ", "
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", charOffset=" + charOffset + ", cs=" + cs + ",  nbytes[0]=" + nbytes[0]
                    + ",charSize=" + charSize + ")");
            OracleLog.recursiveTrace = false;
        }
        int count = 0;
        int nbBytes = 0;
        switch (cs) {
        case -5:
        case 2000:
            nbBytes = nbytes[0] - nbytes[0] % 2;
            if (charSize > chars.length - charOffset) {
                charSize = chars.length - charOffset;
            }
            if (charSize * 2 < nbBytes) {
                nbBytes = charSize * 2;
            }
            count = CharacterSet.convertAL16UTF16BytesToJavaChars(bytes, byteOffset, chars,
                                                                  charOffset, nbBytes, true);
            nbytes[0] = nbytes[0] - nbBytes;
            break;

        case 1: // '\001'
        case 2: // '\002'
        case 31: // '\037'
        case 178:
            nbBytes = nbytes[0];
            if (charSize > chars.length - charOffset) {
                charSize = chars.length - charOffset;
            }
            if (charSize < nbBytes) {
                nbBytes = charSize;
            }
            count = CharacterSet.convertASCIIBytesToJavaChars(bytes, byteOffset, chars, charOffset,
                                                              nbBytes);
            nbytes[0] = nbytes[0] - nbBytes;
            break;

        case 870:
        case 871:
            if (charSize > chars.length - charOffset) {
                charSize = chars.length - charOffset;
            }
            count = CharacterSet.convertUTFBytesToJavaChars(bytes, byteOffset, chars, charOffset,
                                                            nbytes, true, charSize);
            break;

        case 873:
            if (charSize > chars.length - charOffset) {
                charSize = chars.length - charOffset;
            }
            count = CharacterSet
                    .convertAL32UTF8BytesToJavaChars(bytes, byteOffset, chars, charOffset, nbytes,
                                                     true, charSize);
            break;

        case -1:
            count = dbCsBytesToJavaChars(bytes, byteOffset, chars, charOffset, nbytes[0],
                                         _m_databaseCs);
            nbytes[0] = 0;
            break;

        default:
            CharacterSet chSet = _m_driverCs;
            if (isNCharData) {
                chSet = _m_databaseNCs;
            }
            String converted = chSet.toString(bytes, byteOffset, nbytes[0]);
            char convertedChars[] = converted.toCharArray();
            int nbCharsToCopy = convertedChars.length;
            if (nbCharsToCopy > charSize) {
                nbCharsToCopy = charSize;
            }
            System.arraycopy(convertedChars, 0, chars, charOffset, nbCharsToCopy);
            break;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion._CHARBytesToJavaChars(bytes[], nbytes[], chars[], cs): returned "
                                                   + count);
            OracleLog.recursiveTrace = false;
        }
        return count;
    }

    public byte[] asciiBytesToCHARBytes(byte bytes[]) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.asciiBytesToCHARBytes("
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINER,
                                           "DBConversion.asciiBytesToCHARBytes(bytes[]): clientCharSetId="
                                                   + clientCharSetId, this);
            OracleLog.recursiveTrace = false;
        }
        byte retbytes[] = null;
        switch (clientCharSetId) {
        case -5:
            retbytes = new byte[bytes.length * 2];
            int i = 0;
            int retbyte_i = 0;
            for (; i < bytes.length; i++) {
                retbytes[retbyte_i++] = 0;
                retbytes[retbyte_i++] = bytes[i];
            }

            break;

        case -1:
            if (asciiCharSet == null) {
                asciiCharSet = CharacterSet.make(1);
            }
            try {
                retbytes = serverCharSet.convert(asciiCharSet, bytes, 0, bytes.length);
            } catch (SQLException e) {
                if (TRACE && !OracleLog.recursiveTrace) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.conversionLogger.log(Level.SEVERE,
                                                   "DBConversion.asciiBytesToCHARBytes:" + e, this);
                    OracleLog.recursiveTrace = false;
                }
            }
            break;

        default:
            retbytes = bytes;
            break;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger
                    .log(Level.FINE, "DBConversion.asciiBytesToCHARBytes(bytes[]): returned "
                            + OracleLog.bytesToPrintableForm("retbytes=", retbytes), this);
            OracleLog.recursiveTrace = false;
        }
        return retbytes;
    }

    public int javaCharsToDbCsBytes(char chars[], int nchars, byte bytes[]) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToDbCsBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", nchars=" + nchars + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ")", this);
            OracleLog.recursiveTrace = false;
        }
        int num_conv_bytes = javaCharsToDbCsBytes(chars, 0, bytes, 0, nchars);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.javaCharsToDbCsBytes(chars[], nchars, bytes[]): returned "
                                                   + num_conv_bytes, this);
            OracleLog.recursiveTrace = false;
        }
        return num_conv_bytes;
    }

    public int javaCharsToDbCsBytes(char chars[], int charOffset, byte bytes[], int byteOffset,
            int nchars) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToDbCsBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", charOffset=" + charOffset + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", byteOffset="
                    + byteOffset + ", " + "nchars=" + nchars + ")", this);
            OracleLog.recursiveTrace = false;
        }
        int num_conv_bytes = 0;
        catchCharsLen(chars, charOffset, nchars);
        String str = new String(chars, charOffset, nchars);
        byte dbcs_bytes[] = serverCharSet.convertWithReplacement(str);
        str = null;
        if (dbcs_bytes != null) {
            num_conv_bytes = dbcs_bytes.length;
            catchBytesLen(bytes, byteOffset, num_conv_bytes);
            System.arraycopy(dbcs_bytes, 0, bytes, byteOffset, num_conv_bytes);
            dbcs_bytes = null;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.javaCharsToDbCsBytes(chars[], offset, bytes[], offset, nchars): returned "
                                                   + num_conv_bytes, this);
            OracleLog.recursiveTrace = false;
        }
        return num_conv_bytes;
    }

    static final int dbCsBytesToJavaChars(byte bytes[], int byteOffset, char chars[],
            int charOffset, int nbytes, CharacterSet _m_databaseCs) throws SQLException {
        int num_conv_chars = 0;
        catchBytesLen(bytes, byteOffset, nbytes);
        String conv_str = _m_databaseCs.toStringWithReplacement(bytes, byteOffset, nbytes);
        if (conv_str != null) {
            num_conv_chars = conv_str.length();
            catchCharsLen(chars, charOffset, num_conv_chars);
            conv_str.getChars(0, num_conv_chars, chars, charOffset);
            conv_str = null;
        }
        return num_conv_chars;
    }

    public static final int javaCharsToUcs2Bytes(char chars[], int nchars, byte bytes[])
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToUcs2Bytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", nchars=" + nchars + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ")");
            OracleLog.recursiveTrace = false;
        }
        int byte_i = javaCharsToUcs2Bytes(chars, 0, bytes, 0, nchars);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.javaCharsToUcs2Bytes(chars[], nchars, bytes[]): returned "
                                                   + byte_i);
            OracleLog.recursiveTrace = false;
        }
        return byte_i;
    }

    public static final int javaCharsToUcs2Bytes(char chars[], int charOffset, byte bytes[],
            int byteOffset, int nchars) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToUcs2Bytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", charOffset=" + charOffset + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", byteOffset="
                    + byteOffset + ", nchars=" + nchars + ")");
            OracleLog.recursiveTrace = false;
        }
        catchCharsLen(chars, charOffset, nchars);
        catchBytesLen(bytes, byteOffset, nchars * 2);
        int lastChar = nchars + charOffset;
        int char_i = charOffset;
        int byte_i = byteOffset;
        for (; char_i < lastChar; char_i++) {
            bytes[byte_i++] = (byte) (chars[char_i] >> 8 & 0xff);
            bytes[byte_i++] = (byte) (chars[char_i] & 0xff);
        }

        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger
                    .log(Level.FINE,
                         "DBConversion.javaCharsToUcs2Bytes(chars[], offset, bytes[], byteOffset, nchars): return");
            OracleLog.recursiveTrace = false;
        }
        return byte_i - byteOffset;
    }

    public static final int ucs2BytesToJavaChars(byte bytes[], int nbytes, char chars[])
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.ucs2BytesToJavaChars("
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes)
                    + ", nbytes="
                    + nbytes
                    + ", "
                    + OracleLog.bytesToPrintableForm("\n chars[]=", OracleLog
                            .charsToUcs2Bytes(chars)) + ")");
            OracleLog.recursiveTrace = false;
        }
        return CharacterSet.AL16UTF16BytesToJavaChars(bytes, nbytes, chars);
    }

    public static final byte[] stringToAsciiBytes(String str) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.stringToAsciiBytes(" + str
                    + ")");
            OracleLog.recursiveTrace = false;
        }
        return CharacterSet.stringToASCII(str);
    }

    public static final int asciiBytesToJavaChars(byte bytes[], int nbytes, char chars[])
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.asciiBytesToJavaChars("
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", nbytes=" + nbytes
                    + ", "
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ")");
            OracleLog.recursiveTrace = false;
        }
        return CharacterSet.convertASCIIBytesToJavaChars(bytes, 0, chars, 0, nbytes);
    }

    public static final int javaCharsToAsciiBytes(char chars[], int nchars, byte bytes[])
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.javaCharsToAsciiBytes("
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ", nchars=" + nchars + ", "
                    + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ")");
            OracleLog.recursiveTrace = false;
        }
        return CharacterSet.convertJavaCharsToASCIIBytes(chars, 0, bytes, 0, nchars);
    }

    public static final boolean isCharSetMultibyte(short charSet) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.isCharSetMultibyte(" + charSet
                    + ")");
            OracleLog.recursiveTrace = false;
        }
        switch (charSet) {
        case 1: // '\001'
        case 31: // '\037'
            return false;

        case -5:
        case -1:
        case 870:
        case 871:
        case 873:
            return true;
        }
        return false;
    }

    public int getMaxCharbyteSize() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getMaxCharbyteSize()", this);
            OracleLog.recursiveTrace = false;
        }
        return _getMaxCharbyteSize(clientCharSetId);
    }

    public int getMaxNCharbyteSize() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getMaxNCharbyteSize()", this);
            OracleLog.recursiveTrace = false;
        }
        return _getMaxCharbyteSize(serverNCharSetId);
    }

    public int _getMaxCharbyteSize(short cs) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion._getMaxCharbyteSize(" + cs
                    + ")", this);
            OracleLog.recursiveTrace = false;
        }
        switch (cs) {
        case 1: // '\001'
            return 1;

        case 31: // '\037'
            return 1;

        case 870:
        case 871:
            return 3;

        case -5:
        case 2000:
            return 2;

        case -1:
            return 4;

        case 873:
            return 4;
        }
        return 1;
    }

    public boolean isUcs2CharSet() {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.isUcs2CharSet()", this);
            OracleLog.recursiveTrace = false;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINER,
                                           "DBConversion.isUcs2CharSet(): clientCharSetId="
                                                   + clientCharSetId, this);
            OracleLog.recursiveTrace = false;
        }
        return clientCharSetId == -5;
    }

    public static final int RAWBytesToHexChars(byte bytes[], int nbytes, char chars[]) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.RAWBytesToHexChars("
                    + OracleLog.bytesToPrintableForm("bytes[]", bytes) + ", nbytes=" + nbytes
                    + ", "
                    + OracleLog.bytesToPrintableForm("chars[]=", OracleLog.charsToUcs2Bytes(chars))
                    + ")");
            OracleLog.recursiveTrace = false;
        }
        int byte_i = 0;
        int char_i = 0;
        for (; byte_i < nbytes; byte_i++) {
            chars[char_i++] = (char) RepConversion.nibbleToHex((byte) (bytes[byte_i] >> 4 & 0xf));
            chars[char_i++] = (char) RepConversion.nibbleToHex((byte) (bytes[byte_i] & 0xf));
        }

        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.RAWBytesToHexChars(bytes[], nbytes, chars[]): returned "
                                                   + char_i);
            OracleLog.recursiveTrace = false;
        }
        return char_i;
    }

    public InputStream ConvertStream(InputStream stream, int conversion) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.ConvertStream(stream="
                    + stream + ", conversion=" + conversion + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return new OracleConversionInputStream(this, stream, conversion);
    }

    public InputStream ConvertStream(InputStream stream, int conversion, int max_bytes) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.ConvertStream(stream="
                    + stream + ", conversion=" + conversion + ", max_bytes=" + max_bytes + ")",
                                           this);
            OracleLog.recursiveTrace = false;
        }
        return new OracleConversionInputStream(this, stream, conversion, max_bytes);
    }

    public InputStream ConvertStream(Reader stream, int conversion, int max_chars, short form_of_use) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.ConvertStream(stream="
                    + stream + ", conversion=" + conversion + ", max_chars=" + max_chars
                    + ", form_of_use=" + form_of_use + ")", this);
            OracleLog.recursiveTrace = false;
        }
        OracleConversionInputStream ocis = new OracleConversionInputStream(this, stream,
                conversion, max_chars, form_of_use);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.ConvertStream(stream, conversion, max_chars, form_of_use): returned "
                                                   + ocis, this);
            OracleLog.recursiveTrace = false;
        }
        return ocis;
    }

    public Reader ConvertCharacterStream(InputStream stream, int conversion) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.ConvertCharacterStream(stream=" + stream
                                                   + ", conversion=" + conversion + ")", this);
            OracleLog.recursiveTrace = false;
        }
        return new OracleConversionReader(this, stream, conversion);
    }

    public Reader ConvertCharacterStream(InputStream stream, int conversion, short form_of_use)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.ConvertCharacterStream(stream=" + stream
                                                   + ", conversion=" + conversion
                                                   + ", form_of_use=" + form_of_use + ")", this);
            OracleLog.recursiveTrace = false;
        }
        OracleConversionReader ocr = new OracleConversionReader(this, stream, conversion);
        ocr.setFormOfUse(form_of_use);
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE,
                                           "DBConversion.ConvertCharacterStream(stream, conversion, form_of_use): returned "
                                                   + ocr, this);
            OracleLog.recursiveTrace = false;
        }
        return ocr;
    }

    public InputStream CharsToStream(char javachars[], int offset, int len, int conversion)
            throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.CharsToStream(javachars="
                    + javachars + ", offset=" + offset + ", length=" + len + ", conversion="
                    + conversion + ")", this);
            OracleLog.recursiveTrace = false;
        }
        if (conversion == 10) {
            return new AsciiStream(javachars, offset, len);
        }
        if (conversion == 11) {
            return new UnicodeStream(javachars, offset, len);
        } else {
            DatabaseError.throwSqlException(39, "unknownConversion");
            return null;
        }
    }

    static final void unexpectedCharset(short charSet) throws SQLException {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.SEVERE, "DBConversion.unexpectedCharset("
                    + charSet + ")");
            OracleLog.recursiveTrace = false;
        }
        DatabaseError.throwSqlException(35, "DBConversion");
    }

    protected static final void catchBytesLen(byte bytes[], int offset, int nbytes)
            throws SQLException {
        if (offset + nbytes > bytes.length) {
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.conversionLogger.log(Level.SEVERE, "DBConversion.catchBytesLen("
                        + OracleLog.bytesToPrintableForm("bytes[]=", bytes) + ", nbytes=" + nbytes
                        + "): " + "Invalid byte array length of " + bytes.length + "(expected "
                        + nbytes + ")");
                OracleLog.recursiveTrace = false;
            }
            DatabaseError.throwSqlException(39, "catchBytesLen");
        }
    }

    protected static final void catchCharsLen(char chars[], int offset, int nchars)
            throws SQLException {
        if (offset + nchars > chars.length) {
            if (TRACE && !OracleLog.recursiveTrace) {
                OracleLog.recursiveTrace = true;
                OracleLog.conversionLogger.log(Level.SEVERE, "DBConversion.catchCharsLen("
                        + OracleLog.bytesToPrintableForm("chars[]=", OracleLog
                                .charsToUcs2Bytes(chars)) + "Invalid char array length of "
                        + chars.length + "(expected " + offset + nchars + ")");
                OracleLog.recursiveTrace = false;
            }
            DatabaseError.throwSqlException(39, "catchCharsLen");
        }
    }

    public static final int getUtfLen(char c) {
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getUtfLen(" + c + ")");
            OracleLog.recursiveTrace = false;
        }
        int utf_len = 0;
        if ((c & 0xff80) == 0) {
            utf_len = 1;
        } else if ((c & 0xf800) == 0) {
            utf_len = 2;
        } else {
            utf_len = 3;
        }
        if (TRACE && !OracleLog.recursiveTrace) {
            OracleLog.recursiveTrace = true;
            OracleLog.conversionLogger.log(Level.FINE, "DBConversion.getUtfLen(c): returned "
                    + utf_len);
            OracleLog.recursiveTrace = false;
        }
        return utf_len;
    }

    int encodedByteLength(String s, boolean isNChar) {
        int len = 0;
        if (s != null) {
            len = s.length();
            if (len != 0) {
                if (isNChar) {
                    len = isServerNCharSetFixedWidth ? len * maxNCharSize : serverNCharSet
                            .encodedByteLength(s);
                } else {
                    len = isServerCharSetFixedWidth ? len * sMaxCharSize : serverCharSet
                            .encodedByteLength(s);
                }
            }
        }
        return len;
    }

    int encodedByteLength(char c[], boolean isNChar) {
        int len = 0;
        if (c != null) {
            len = c.length;
            if (len != 0) {
                if (isNChar) {
                    len = isServerNCharSetFixedWidth ? len * maxNCharSize : serverNCharSet
                            .encodedByteLength(c);
                } else {
                    len = isServerCharSetFixedWidth ? len * sMaxCharSize : serverCharSet
                            .encodedByteLength(c);
                }
            }
        }
        return len;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.DBConversion"));
        } catch (Exception e) {
        }
    }

    class UnicodeStream extends OracleBufferedStream {
        UnicodeStream(char[] javachars, int offset, int len) {
            super(8192);

            int cind = offset;
            for (int bind = 0; bind < len;) {
                char c = javachars[(cind++)];

                this.buf[(bind++)] = (byte) (c >> '\b' & 0xFF);
                this.buf[(bind++)] = (byte) (c & 0xFF);
            }

            this.count = len;
        }

        public boolean needBytes() {
            return (!this.closed) && (this.pos < this.count);
        }
    }

    class AsciiStream extends OracleBufferedStream {
        AsciiStream(char[] javachars, int offset, int len) {
            super(8192);

            int cind = offset;
            for (int bind = 0; bind < len; bind++) {
                this.buf[bind] = (byte) javachars[(cind++)];
            }
            this.count = len;
        }

        public boolean needBytes() {
            return (!this.closed) && (this.pos < this.count);
        }
    }
}
