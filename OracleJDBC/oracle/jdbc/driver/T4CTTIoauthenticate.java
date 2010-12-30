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

class T4CTTIoauthenticate extends T4CTTIfun
{
  byte[] user;
  String userStr;
  byte[] password;
  String passwordStr;
  byte[] terminal;
  byte[] machine;
  byte[] sysUserName;
  byte[] processID;
  byte[] programName;
  long flag;
  byte[] encryptedSK;
  byte[] internalName;
  byte[] externalName;
  byte[] alterSession;
  byte[] aclValue;
  byte[] clientname;
  String ressourceManagerId;
  T4CConnection conn;
  boolean isSessionTZ = true;

  Properties connectionValues = new Properties();
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIoauthenticate(T4CMAREngine paramT4CMAREngine, String paramString1, String paramString2, Properties paramProperties, long paramLong, String paramString3, T4CTTIoer paramT4CTTIoer, T4CConnection paramT4CConnection)
    throws SQLException
  {
    super(3, 0, 0);

    this.oer = paramT4CTTIoer;

    setMarshalingEngine(paramT4CMAREngine);

    this.user = paramT4CMAREngine.conv.StringToCharBytes(paramString1);
    this.userStr = paramString1;
    this.passwordStr = paramString2;
    this.flag = paramLong;
    this.ressourceManagerId = paramString3;

    setSessionFields(paramProperties);

    this.conn = paramT4CConnection;
    this.isSessionTZ = (this.conn.ver.getVersionNumber() >= 8100);
  }

  void marshalOsesskey()
    throws IOException, SQLException
  {
    this.funCode = 118;

    marshalFunHeader();
    this.meg.marshalPTR();
    this.meg.marshalSB4(this.user.length);
    this.meg.marshalUB4(this.flag | 1L);
    this.meg.marshalPTR();

    int i = 3;

    if (this.programName != null) {
      i++;
    }
    this.meg.marshalUB4(i);
    this.meg.marshalPTR();
    this.meg.marshalPTR();

    this.meg.marshalCHR(this.user);

    byte[][] arrayOfByte1 = new byte[i][];
    byte[][] arrayOfByte2 = new byte[i][];

    byte[] arrayOfByte = new byte[i];
    int j = 0;

    arrayOfByte1[(j++)] = this.meg.conv.StringToCharBytes("AUTH_TERMINAL");

    if (this.programName != null)
    {
      arrayOfByte1[j] = this.meg.conv.StringToCharBytes("AUTH_PROGRAM_NM");

      arrayOfByte2[(j++)] = this.programName;
    }

    arrayOfByte1[j] = this.meg.conv.StringToCharBytes("AUTH_MACHINE");
    arrayOfByte2[(j++)] = this.machine;

    arrayOfByte1[j] = this.meg.conv.StringToCharBytes("AUTH_PID");
    arrayOfByte2[(j++)] = this.processID;

    this.meg.marshalKEYVAL(arrayOfByte1, arrayOfByte2, arrayOfByte, i);
  }

  void receiveOsesskey()
    throws IOException, SQLException
  {
    Object localObject1 = (byte[][])null;
    Object localObject2 = (byte[][])null;
    while (true)
    {
      try
      {
        int i = this.meg.unmarshalSB1();

        switch (i)
        {
        case 8:
          int j = this.meg.unmarshalUB2();

          localObject1 = new byte[j][];
          localObject2 = new byte[j][];

          this.meg.unmarshalKEYVAL(localObject1, localObject2, j);

          break;
        case 4:
          this.oer.init();
          this.oer.unmarshal();
          try
          {
            this.oer.processError();
          }
          catch (SQLException localSQLException)
          {
            throw localSQLException;
          }

          break;
        default:
          DatabaseError.throwSqlException(401);
        }

        continue; } catch (BreakNetException localBreakNetException) {
      }
    }
    if ((localObject1 == null) || (localObject1.length < 1)) {
      DatabaseError.throwSqlException(438);
    }

    this.encryptedSK = localObject2[0];

    if ((this.encryptedSK == null) || (this.encryptedSK.length != 16))
      DatabaseError.throwSqlException(438);
  }

  void marshalOauth()
    throws IOException, SQLException
  {
    this.funCode = 115;

    if (this.encryptedSK.length > 16) {
      DatabaseError.throwSqlException(413);
    }

    String str1 = this.userStr.trim();
    String str2 = this.passwordStr.trim();

    this.passwordStr = null;

    String str3 = str1;
    String str4 = str2;

    if ((str1.startsWith("\"")) || (str1.endsWith("\""))) {
      str3 = removeQuotes(str1);
    }
    if ((str2.startsWith("\"")) || (str2.endsWith("\""))) {
      str4 = removeQuotes(str2);
    }

    O3LoginClientHelper localO3LoginClientHelper = new O3LoginClientHelper(this.meg.conv.isServerCSMultiByte);

    byte[] arrayOfByte3 = localO3LoginClientHelper.getSessionKey(str3, str4, this.encryptedSK);

    byte[] arrayOfByte1 = this.meg.conv.StringToCharBytes(str4);
    int i;
    if (arrayOfByte1.length % 8 > 0)
      i = (byte)(8 - arrayOfByte1.length % 8);
    else {
      i = 0;
    }
    byte[] arrayOfByte2 = new byte[arrayOfByte1.length + i];

    System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);

    byte[] arrayOfByte4 = localO3LoginClientHelper.getEPasswd(arrayOfByte3, arrayOfByte2);

    this.password = new byte[2 * arrayOfByte2.length + 1];

    if (this.password.length < 2 * arrayOfByte4.length) {
      DatabaseError.throwSqlException(413);
    }
    RepConversion.bArray2Nibbles(arrayOfByte4, this.password);

    this.password[(this.password.length - 1)] = RepConversion.nibbleToHex(i);

    marshalFunHeader();
    this.meg.marshalPTR();
    this.meg.marshalSB4(this.user.length);
    this.meg.marshalUB4(this.flag | 1L | 0x100);

    this.meg.marshalPTR();

    int j = 0;

    if (!this.ressourceManagerId.equals("0000")) {
      j = 1;
    }

    int k = 6;

    if (j != 0) {
      k += 2;
    }
    k++;

    if (this.programName != null) {
      k++;
    }
    if (this.clientname != null) {
      k++;
    }
    this.meg.marshalUB4(k);
    this.meg.marshalPTR();
    this.meg.marshalPTR();

    this.meg.marshalCHR(this.user);

    byte[][] arrayOfByte5 = new byte[k][];
    byte[][] arrayOfByte6 = new byte[k][];
    byte[] arrayOfByte7 = new byte[k];
    int m = 0;

    arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_PASSWORD");
    arrayOfByte6[(m++)] = this.password;

    arrayOfByte5[(m++)] = this.meg.conv.StringToCharBytes("AUTH_TERMINAL");

    if (this.programName != null)
    {
      arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_PROGRAM_NM");

      arrayOfByte6[(m++)] = this.programName;
    }

    if (this.clientname != null)
    {
      arrayOfByte5[m] = this.meg.conv.StringToCharBytes("PROXY_CLIENT_NAME");

      arrayOfByte6[(m++)] = this.clientname;
    }

    arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_MACHINE");
    arrayOfByte6[(m++)] = this.machine;

    arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_PID");
    arrayOfByte6[(m++)] = this.processID;

    if (j != 0)
    {
      arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_INTERNALNAME_");

      arrayOfByte5[m][(arrayOfByte5[m].length - 1)] = 0;
      arrayOfByte6[(m++)] = this.internalName;

      arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_EXTERNALNAME_");

      arrayOfByte5[m][(arrayOfByte5[m].length - 1)] = 0;
      arrayOfByte6[(m++)] = this.externalName;
    }

    arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_ACL");
    arrayOfByte6[(m++)] = this.aclValue;

    arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_ALTER_SESSION");

    arrayOfByte6[m] = this.alterSession;
    arrayOfByte7[(m++)] = 1;

    arrayOfByte5[m] = this.meg.conv.StringToCharBytes("AUTH_COPYRIGHT");

    arrayOfByte6[(m++)] = this.meg.conv.StringToCharBytes("\"Oracle\nEverybody follows\nSpeedy bits exchange\nStars await to glow\"\nThe preceding key is copyrighted by Oracle Corporation.\nDuplication of this key is not allowed without permission\nfrom Oracle Corporation. Copyright 2003 Oracle Corporation.");

    this.meg.marshalKEYVAL(arrayOfByte5, arrayOfByte6, arrayOfByte7, k);
  }

  void marshalOauth(int paramInt1, Properties paramProperties, int paramInt2, int paramInt3)
    throws IOException, SQLException
  {
    this.funCode = 115;

    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    byte[] arrayOfByte1 = null;
    byte[] arrayOfByte2 = null;
    Object localObject1 = (byte[][])null;
    Object localObject2;
    if (paramInt1 == 1)
    {
      localObject2 = paramProperties.getProperty("PROXY_USER_NAME");

      this.user = this.meg.conv.StringToCharBytes((String)localObject2);
      i = 1;
    }
    else if (paramInt1 == 2)
    {
      localObject2 = paramProperties.getProperty("PROXY_DISTINGUISHED_NAME");

      arrayOfByte1 = this.meg.conv.StringToCharBytes((String)localObject2);
      j = 1;
    }
    else
    {
      try
      {
        arrayOfByte2 = (byte[])paramProperties.get("PROXY_CERTIFICATE");

        localObject2 = new StringBuffer();

        for (int i2 = 0; i2 < arrayOfByte2.length; i2++)
        {
          String str = Integer.toHexString(0xFF & arrayOfByte2[i2]);
          int i1 = str.length();

          if (i1 == 0) {
            ((StringBuffer)localObject2).append("00");
          } else if (i1 == 1)
          {
            ((StringBuffer)localObject2).append('0');
            ((StringBuffer)localObject2).append(str);
          }
          else {
            ((StringBuffer)localObject2).append(str);
          }
        }
        arrayOfByte2 = ((StringBuffer)localObject2).toString().getBytes();
      }
      catch (Exception localException1) {
      }
      k = 1;
    }

    String[] arrayOfString = null;
    try
    {
      arrayOfString = (String[])paramProperties.get("PROXY_ROLES");
    }
    catch (Exception localException2) {
    }
    if (arrayOfString != null)
    {
      localObject1 = new byte[arrayOfString.length][];

      for (n = 0; n < arrayOfString.length; n++) {
        localObject1[n] = this.meg.conv.StringToCharBytes(arrayOfString[n]);
      }
      m = 1;
    }

    marshalFunHeader();

    if (i != 0)
    {
      this.meg.marshalPTR();
      this.meg.marshalSB4(this.user.length);
    }
    else
    {
      this.meg.marshalNULLPTR();
      this.meg.marshalSB4(0);
    }

    this.meg.marshalUB4(1025L);
    this.meg.marshalPTR();

    int n = 7;

    n++;

    if ((j != 0) || (k != 0)) {
      n++;
    }
    if (m != 0) {
      n += localObject1.length;
    }
    if (this.programName != null) {
      n++;
    }
    this.meg.marshalUB4(n);
    this.meg.marshalPTR();
    this.meg.marshalPTR();

    if (i != 0) {
      this.meg.marshalCHR(this.user);
    }
    byte[][] arrayOfByte3 = new byte[n][];
    byte[][] arrayOfByte4 = new byte[n][];
    byte[] arrayOfByte5 = new byte[n];

    int i3 = 0;

    if (m != 0)
    {
      for (int i4 = 0; i4 < localObject1.length; i4++)
      {
        arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("INITIAL_CLIENT_ROLE");

        arrayOfByte4[i3] = localObject1[i4];
        i3++;
      }

    }

    if (j != 0)
    {
      arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_CLIENT_DISTINGUISHED_NAME");

      arrayOfByte4[(i3++)] = arrayOfByte1;
    }
    else if (k != 0)
    {
      arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_CLIENT_CERTIFICATE");

      arrayOfByte4[(i3++)] = arrayOfByte2;
    }

    arrayOfByte3[(i3++)] = this.meg.conv.StringToCharBytes("AUTH_TERMINAL");

    if (this.programName != null)
    {
      arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_PROGRAM_NM");

      arrayOfByte4[(i3++)] = this.programName;
    }

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_MACHINE");
    arrayOfByte4[(i3++)] = this.machine;

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_PID");
    arrayOfByte4[(i3++)] = this.processID;

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_ACL");
    arrayOfByte4[(i3++)] = this.aclValue;

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_ALTER_SESSION");

    arrayOfByte4[i3] = this.alterSession;
    arrayOfByte5[(i3++)] = 1;

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_SESSION_ID");

    arrayOfByte4[(i3++)] = this.meg.conv.StringToCharBytes(Integer.toString(paramInt2));

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_SERIAL_NUM");

    arrayOfByte4[(i3++)] = this.meg.conv.StringToCharBytes(Integer.toString(paramInt3));

    arrayOfByte3[i3] = this.meg.conv.StringToCharBytes("AUTH_COPYRIGHT");

    arrayOfByte4[(i3++)] = this.meg.conv.StringToCharBytes("\"Oracle\nEverybody follows\nSpeedy bits exchange\nStars await to glow\"\nThe preceding key is copyrighted by Oracle Corporation.\nDuplication of this key is not allowed without permission\nfrom Oracle Corporation. Copyright 2003 Oracle Corporation.");

    this.meg.marshalKEYVAL(arrayOfByte3, arrayOfByte4, arrayOfByte5, n);
  }

  void receiveOauth()
    throws IOException, SQLException
  {
    Object localObject1 = (byte[][])null;
    Object localObject2 = (byte[][])null;
    int j = 0;
    while (true)
    {
      try
      {
        int i = this.meg.unmarshalSB1();

        switch (i)
        {
        case 8:
          j = this.meg.unmarshalUB2();
          localObject1 = new byte[j][];
          localObject2 = new byte[j][];

          this.meg.unmarshalKEYVAL(localObject1, localObject2, j);

          break;
        case 15:
          this.oer.init();
          this.oer.unmarshalWarning();
          try
          {
            this.oer.processWarning();
          }
          catch (SQLWarning localSQLWarning)
          {
            this.conn.setWarnings(DatabaseError.addSqlWarning(this.conn.getWarnings(), localSQLWarning));
          }

        case 4:
          this.oer.init();
          this.oer.unmarshal();
          try
          {
            this.oer.processError();
          }
          catch (SQLException localSQLException)
          {
            throw localSQLException;
          }

          break;
        default:
          DatabaseError.throwSqlException(401);
        }

        continue;
      }
      catch (BreakNetException localBreakNetException)
      {
      }

    }

    for (int k = 0; k < j; k++)
    {
      String str1 = this.meg.conv.CharBytesToString(localObject1[k], localObject1[k].length);
      String str2 = "";
      if (localObject2[k] != null) {
        str2 = this.meg.conv.CharBytesToString(localObject2[k], localObject2[k].length);
      }
      this.connectionValues.setProperty(str1, str2);
    }
  }

  int getSessionId()
  {
    int i = -1;
    String str = this.connectionValues.getProperty("AUTH_SESSION_ID");
    try
    {
      i = Integer.parseInt(str);
    } catch (NumberFormatException localNumberFormatException) {
    }
    return i;
  }

  int getSerialNumber()
  {
    int i = -1;
    String str = this.connectionValues.getProperty("AUTH_SERIAL_NUM");
    try
    {
      i = Integer.parseInt(str);
    } catch (NumberFormatException localNumberFormatException) {
    }
    return i;
  }

  void setSessionFields(Properties paramProperties)
    throws SQLException
  {
    String str1 = paramProperties.getProperty("v$session.terminal");
    String str2 = paramProperties.getProperty("v$session.machine");
    String str3 = paramProperties.getProperty("v$session.osuser");
    String str4 = paramProperties.getProperty("v$session.program");
    String str5 = paramProperties.getProperty("v$session.process");
    String str6 = paramProperties.getProperty("v$session.iname");
    String str7 = paramProperties.getProperty("v$session.ename");
    String str8 = paramProperties.getProperty("PROXY_CLIENT_NAME");

    if (str1 == null)
    {
      str1 = "unknown";
    }
    if (str2 == null)
    {
      try
      {
        str2 = InetAddress.getLocalHost().getHostName();
      }
      catch (Exception localException)
      {
        str2 = "jdbcclient";
      }
    }

    if (str3 == null)
    {
      str3 = OracleDriver.getSystemPropertyUserName();

      if (str3 == null) {
        str3 = "jdbcuser";
      }

    }

    if (str5 == null) {
      str5 = "1234";
    }
    if (str6 == null) {
      str6 = "jdbc_ttc_impl";
    }

    if (str7 == null) {
      str7 = "jdbc_" + this.ressourceManagerId;
    }

    this.terminal = this.meg.conv.StringToCharBytes(str1);
    this.machine = this.meg.conv.StringToCharBytes(str2);
    this.sysUserName = this.meg.conv.StringToCharBytes(str3);

    if (str4 != null) {
      this.programName = this.meg.conv.StringToCharBytes(str4);
    }
    this.processID = this.meg.conv.StringToCharBytes(str5);
    this.internalName = this.meg.conv.StringToCharBytes(str6);
    this.externalName = this.meg.conv.StringToCharBytes(str7);

    if (str8 != null)
    {
      this.clientname = this.meg.conv.StringToCharBytes(str8);
    }

    TimeZone localTimeZone = TimeZone.getDefault();
    int i = localTimeZone.getRawOffset();
    int j = i / 3600000;
    int k = i / 60000 % 60;

    if ((localTimeZone.useDaylightTime()) && (localTimeZone.inDaylightTime(new Date()))) {
      if (j > 0)
        j--;
      else
        j++;
    }
    String str9 = (j < 0 ? "" + j : new StringBuffer().append("+").append(j).toString()) + (k < 10 ? ":0" + k : new StringBuffer().append(":").append(k).toString());

    this.alterSession = this.meg.conv.StringToCharBytes("ALTER SESSION SET " + (this.isSessionTZ ? "TIME_ZONE='" + str9 + "'" : "") + " NLS_LANGUAGE='" + CharacterSetMetaData.getNLSLanguage(Locale.getDefault()) + "' NLS_TERRITORY='" + CharacterSetMetaData.getNLSTerritory(Locale.getDefault()) + "' ");

    this.aclValue = this.meg.conv.StringToCharBytes("4400");
    this.alterSession[(this.alterSession.length - 1)] = 0;
  }

  String removeQuotes(String paramString)
  {
    int i = 0; int j = paramString.length() - 1;

    for (int k = 0; k < paramString.length(); k++)
    {
      if (paramString.charAt(k) == '"')
        continue;
      i = k;

      break;
    }

    for (k = paramString.length() - 1; k >= 0; k--)
    {
      if (paramString.charAt(k) == '"')
        continue;
      j = k;

      break;
    }

    String str = paramString.substring(i, j + 1);

    return str;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIoauthenticate
 * JD-Core Version:    0.6.0
 */