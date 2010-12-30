package oracle.jdbc.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class OracleLog
{
  private static StreamHandler printHandler;
  public static Logger rootLogger;
  public static Logger driverLogger;
  public static Logger poolLogger;
  public static Logger conversionLogger;
  public static Logger adtLogger;
  public static Logger thinLogger;
  public static Logger datumLogger;
  public static Logger kprbLogger;
  public static Logger xaLogger;
  public static Logger sqljLogger;
  public static Logger ociLogger;
  public static Logger jpubLogger;
  public static final int MASK_ALL_SET = 268435455;
  public static final int MAX_VECTOR_BITS = 32;
  public static final int MAX_MODULES = 32;
  public static final int MODULE_ALL = 268435455;
  public static final int MODULE_DRIVER = 1;
  public static final int MODULE_POOL = 2;
  public static final int MODULE_DBCONV = 4;
  public static final int MODULE_unused2 = 8;
  public static final int MODULE_PICKLE = 16;
  public static final int MODULE_JTTC = 32;
  public static final int MODULE_DATUM = 64;
  public static final int MODULE_KPRB = 128;
  public static final int MODULE_XA = 256;
  public static final int MODULE_SQLJ = 512;
  public static final int MODULE_JOCI = 1024;
  public static final int MODULE_JPUB = 2048;
  public static final int MODULE_T2C = 4096;
  public static final int MODULE_TOTAL = 13;
  public static final String[] ModuleName = { "DRVR ", "POOL", "DBCV ", "unused", "PIKL ", "JTTC ", "DATM ", "KPRB ", "XA   ", "SQLJ ", "JOCI ", "JPUB ", "T2C" };
  public static final int SUBMOD_ALL = 268435455;
  public static final int SUBMOD_DEFAULT = 1;
  public static final int SUBMOD_DRVR_LOG = 2;
  public static final int SUBMOD_DRVR_ERR = 4;
  public static final int SUBMOD_DRVR_CONN = 8;
  public static final int SUBMOD_DRVR_STMT = 16;
  public static final int SUBMOD_DRVR_RSET = 32;
  public static final int SUBMOD_DRVR_UTIL = 64;
  public static final int SUBMOD_DRVR_SQL = 128;
  public static final int SUBMOD_DRVR_RWST = 256;
  public static final int SUBMOD_DRVR_POOL = 512;
  public static final int SUBMOD_DRVR_SCCH = 1024;
  public static final int SUBMOD_DRVR_CNTR = 2048;
  public static final int SUBMOD_T2C_DRVEXT = 2;
  public static final int SUBMOD_T2C_STATEMENT = 4;
  public static final int SUBMOD_T2C_PREPSTATEMENT = 8;
  public static final int SUBMOD_T2C_CALLSTATEMENT = 16;
  public static final int SUBMOD_T2C_INPUTSTREAM = 32;
  public static final int SUBMOD_T2C_CLOB = 64;
  public static final int SUBMOD_T2C_BLOB = 128;
  public static final int SUBMOD_T2C_BFILE = 256;
  public static final int SUBMOD_DBAC_DATA = 2;
  public static final int SUBMOD_PCKL_INIT = 2;
  public static final int SUBMOD_PCKL_TYPE = 4;
  public static final int SUBMOD_PCKL_PCKL = 8;
  public static final int SUBMOD_PCKL_UNPK = 16;
  public static final int SUBMOD_PCKL_CONV = 32;
  public static final int SUBMOD_PCKL_DESC = 64;
  public static final int SUBMOD_PCKL_PARS = 128;
  public static final int SUBMOD_PCKL_SERL = 256;
  public static final int SUBMOD_JTTC_BASE = 2;
  public static final int SUBMOD_JTTC_TX = 4;
  public static final int SUBMOD_JTTC_RX = 8;
  public static final int SUBMOD_JTTC_MARS = 16;
  public static final int SUBMOD_JTTC_UNMA = 32;
  public static final int SUBMOD_JTTC_CONN = 64;
  public static final int SUBMOD_JTTC_COMM = 128;
  public static final int SUBMOD_JTTC_STMT = 256;
  public static final int SUBMOD_JTTC_LOBS = 512;
  public static final int SUBMOD_JTTC_ADTS = 1024;
  public static final int SUBMOD_JTTC_ACCE = 2048;
  public static final int SUBMOD_KPRB_ERR = 2;
  public static final int SUBMOD_KPRB_CONN = 4;
  public static final int SUBMOD_KPRB_STMT = 8;
  public static final int SUBMOD_KPRB_RSET = 16;
  public static final int SUBMOD_KPRB_UTIL = 32;
  public static final int SUBMOD_KPRB_SQL = 64;
  public static final int SUBMOD_KPRB_DATA = 128;
  public static final int SUBMOD_KPRB_CONV = 256;
  public static final int SUBMOD_POOL_ALL = 2;
  public static final int SUBMOD_XA_DSRC = 2;
  public static final int SUBMOD_XA_CONN = 4;
  public static final int SUBMOD_XA_RSRC = 8;
  public static final int SUBMOD_XA_ARGS = 16;
  public static final int SUBMOD_XA_EXC = 32;
  public static final int SUBMOD_XA_XID = 64;
  public static final int SUBMOD_XA_HCON = 128;
  public static final int SUBMOD_XA_HCCB = 256;
  public static final int SUBMOD_XA_HRSC = 512;
  public static final int SUBMOD_SQLJ_RUN = 2;
  public static final int SUBMOD_JOCI_ACCS = 2;
  public static final int SUBMOD_JOCI_DSET = 4;
  public static final int SUBMOD_JOCI_ITEM = 8;
  public static final int SUBMOD_JOCI_STMT = 16;
  public static final int SUBMOD_JOCI_TYPE = 32;
  public static final int SUBMOD_JOCI_ENV = 64;
  public static final int SUBMOD_JPUB_RUNTIME = 2;
  public static final int CATEGORY_ALL = 268435455;
  public static final int USER_OPER = 1;
  public static final int PROG_ERROR = 2;
  public static final int ERROR = 4;
  public static final int WARNING = 8;
  public static final int FUNCTION = 16;
  public static final int DEBUG1 = 32;
  public static final int DEBUG2 = 64;
  public static final int SQL_STR = 128;
  public static final int CATEGORY_TOTAL = 8;
  public static final String[] CategoryName = { "OPER ", "PERR ", "ERRO ", "WARN ", "FUNC ", "DBG1 ", "DBG2 ", "SQLS " };
  public static final int CATEGORY_LOW_VOL = 142;
  public static final int CATEGORY_MED_VOL = 143;
  public static final int CATEGORY_HIGH_VOL = 268435455;
  public static final int FIELD_NONE = 0;
  public static final int FIELD_ALL = 268435455;
  public static final int FIELD_NUMBER = 1;
  public static final int FIELD_TIME = 2;
  public static final int FIELD_MODULE = 4;
  public static final int FIELD_SUBMOD = 8;
  public static final int FIELD_CATEGORY = 16;
  public static final int FIELD_OBJECT = 32;
  public static final int FIELD_THREAD = 64;
  public static final int FIELD_DEFAULT = 20;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  private static PrintWriter logWriter = null;
  private static PrintStream logStream = null;
  private static int printMask = 20;
  private static int moduleMask = 268435455;
  private static int[] submodMasks = null;
  private static int categoryMask = 143;
  private static int maxPrintBytes = 200;
  private static boolean warningEnabled = true;
  private static int msgNumber = 0;
  static boolean securityExceptionWhileGettingSystemProperties;

  public static boolean isDebugZip()
  {
    int i = 1;

    i = 0;
    return i;
  }

  public static boolean isPrivateLogAvailable()
  {
    int i = 0;

    return i;
  }

  public static boolean isEnabled()
  {
    return false;
  }

  /** @deprecated */
  public static void setLogWriter(PrintWriter paramPrintWriter)
  {
    if (!isDebugZip())
    {
      if (paramPrintWriter != null)
      {
        paramPrintWriter.println("Oracle Jdbc tracing is not avaliable in a non-debug zip/jar file");
        paramPrintWriter.flush();
      }

      return;
    }

    if (paramPrintWriter != null)
    {
      paramPrintWriter.println("OracleLog.setLogWriter not supported. Use setLogStream instead, or better yet, java.util.logging.");

      paramPrintWriter.flush();
    }
  }

  /** @deprecated */
  public static PrintWriter getLogWriter()
  {
    return logWriter;
  }

  private static void initLoggers()
  {
    if (rootLogger == null)
    {
      rootLogger = Logger.getLogger("oracle.jdbc");
      driverLogger = Logger.getLogger("oracle.jdbc.driver");

      poolLogger = Logger.getLogger("oracle.jdbc.pool");

      conversionLogger = Logger.getLogger("oracle.jdbc.conversion");

      adtLogger = Logger.getLogger("oracle.jdbc.adt");

      thinLogger = Logger.getLogger("oracle.jdbc.thin");

      datumLogger = Logger.getLogger("oracle.jdbc.datum");

      kprbLogger = Logger.getLogger("oracle.jdbc.kprb");

      xaLogger = Logger.getLogger("oracle.jdbc.xa");

      sqljLogger = Logger.getLogger("oracle.jdbc.sqlj");

      ociLogger = Logger.getLogger("oracle.jdbc.oci");

      jpubLogger = Logger.getLogger("oracle.jdbc.jpub");
    }
  }

  /** @deprecated */
  public static void setLogStream(PrintStream paramPrintStream)
  {
    if (!isDebugZip())
    {
      if (paramPrintStream != null)
      {
        paramPrintStream.println("Oracle Jdbc tracing is not avaliable in a non-debug zip/jar file");
        paramPrintStream.flush();
      }

      return;
    }

    initLoggers();

    if (printHandler != null)
    {
      localStreamHandler = printHandler;
      AccessController.doPrivileged(new PrivilegedAction(localStreamHandler) {
        private final StreamHandler val$fprintHandler;

        public Object run() { OracleLog.rootLogger.removeHandler(this.val$fprintHandler);
          return null; }
      });
    }
    printHandler = new StreamHandler(paramPrintStream, new SimpleFormatter());

    StreamHandler localStreamHandler = printHandler;
    AccessController.doPrivileged(new PrivilegedAction(localStreamHandler) {
      private final StreamHandler val$fprintHandler2;

      public Object run() { OracleLog.printHandler.setLevel(Level.FINEST);
        OracleLog.rootLogger.addHandler(this.val$fprintHandler2);
        return null;
      }
    });
    setTrace(true);
  }

  /** @deprecated */
  public static PrintStream getLogStream()
  {
    return logStream;
  }

  /** @deprecated */
  public static void enableWarning(boolean paramBoolean)
  {
    warningEnabled = paramBoolean;
  }

  /** @deprecated */
  public static void setLogVolume(int paramInt)
  {
    print(null, 1, 2, 1, "Set logging volume level to " + paramInt);

    int i = 142;

    switch (paramInt)
    {
    case 1:
      i = 142;

      break;
    case 2:
      i = 143;

      break;
    case 3:
      i = 268435455;

      break;
    default:
      print(null, 1, 2, 4, "Logging volume level " + paramInt + " is not in the range of 1 to 3");

      return;
    }

    config(printMask, moduleMask, i);
  }

  /** @deprecated */
  public static void startLogging()
  {
    setLogStream(System.out);
  }

  /** @deprecated */
  public static void stopLogging()
  {
    setLogStream(null);
  }

  /** @deprecated */
  public static void config(int paramInt1, int paramInt2, int paramInt3)
  {
    configForJavaUtilLogging(moduleMask, categoryMask);
  }

  static void configForJavaUtilLogging(int paramInt1, int paramInt2)
  {
    Level localLevel1 = Level.OFF;

    if ((paramInt2 & 0x40) != 0)
      localLevel1 = Level.FINEST;
    else if ((paramInt2 & 0x20) != 0)
      localLevel1 = Level.FINER;
    else if ((paramInt2 & 0x10) != 0)
      localLevel1 = Level.FINE;
    else if ((paramInt2 & 0x1) != 0)
      localLevel1 = Level.INFO;
    else if ((paramInt2 & 0x80) != 0)
      localLevel1 = Level.CONFIG;
    else if ((paramInt2 & 0x8) != 0)
      localLevel1 = Level.WARNING;
    else if ((paramInt2 & 0x2) != 0)
      localLevel1 = Level.SEVERE;
    else if ((paramInt2 & 0x4) != 0) {
      localLevel1 = Level.SEVERE;
    }
    initLoggers();
    Level localLevel2 = localLevel1;
    AccessController.doPrivileged(new PrivilegedAction(localLevel2) {
      private final Level val$flevel;

      public Object run() { OracleLog.rootLogger.setLevel(this.val$flevel);
        return null;
      }
    });
    Level localLevel3 = Level.OFF;
    int i = paramInt1;
    AccessController.doPrivileged(new PrivilegedAction(i, localLevel2, localLevel3) { private final int val$fmoduleMask;
      private final Level val$flevel;
      private final Level val$off;

      public Object run() { OracleLog.driverLogger.setLevel((this.val$fmoduleMask & 0x1) != 0 ? this.val$flevel : this.val$off);

        OracleLog.poolLogger.setLevel((this.val$fmoduleMask & 0x2) != 0 ? this.val$flevel : this.val$off);

        OracleLog.conversionLogger.setLevel((this.val$fmoduleMask & 0x4) != 0 ? this.val$flevel : this.val$off);

        OracleLog.adtLogger.setLevel((this.val$fmoduleMask & 0x10) != 0 ? this.val$flevel : this.val$off);

        OracleLog.thinLogger.setLevel((this.val$fmoduleMask & 0x20) != 0 ? this.val$flevel : this.val$off);

        OracleLog.datumLogger.setLevel((this.val$fmoduleMask & 0x40) != 0 ? this.val$flevel : this.val$off);

        OracleLog.kprbLogger.setLevel((this.val$fmoduleMask & 0x80) != 0 ? this.val$flevel : this.val$off);

        OracleLog.xaLogger.setLevel((this.val$fmoduleMask & 0x100) != 0 ? this.val$flevel : this.val$off);

        OracleLog.sqljLogger.setLevel((this.val$fmoduleMask & 0x200) != 0 ? this.val$flevel : this.val$off);

        OracleLog.ociLogger.setLevel((this.val$fmoduleMask & 0x400) != 0 ? this.val$flevel : this.val$off);

        OracleLog.jpubLogger.setLevel((this.val$fmoduleMask & 0x800) != 0 ? this.val$flevel : this.val$off);

        return null;
      }
    });
  }

  /** @deprecated */
  public static void setSubmodMask(int paramInt1, int paramInt2)
  {
    int i = getBitNumFromVector(paramInt1);

    submodMasks[i] = paramInt2;

    print(null, 1, 2, 1, "Set logging sub-mask for module " + getMaskHexStr(moduleMask) + "(number " + i + ") to " + getMaskHexStr(paramInt2));
  }

  /** @deprecated */
  public static void setMaxPrintBytes(int paramInt)
  {
    if (paramInt > 0) {
      maxPrintBytes = paramInt;
    }
    print(null, 1, 2, 1, "Set the maximum number of bytes to be printed to " + maxPrintBytes);
  }

  public static boolean registerClassNameAndGetCurrentTraceSetting(Class paramClass)
  {
    return false;
  }

  public static boolean registerClassNameAndGetCurrentPrivateTraceSetting(Class paramClass)
  {
    return false;
  }

  public static void setTrace(boolean paramBoolean)
  {
  }

  /** @deprecated */
  public static void setPrivateTrace(boolean paramBoolean)
  {
  }

  private static void initialize()
  {
    internalCodeChecks();

    setupFromSystemProperties();
  }

  private static void internalCodeChecks()
  {
    if (ModuleName.length != 13)
    {
      System.out.println("ERROR: OracleLog.ModuleName[] has " + ModuleName.length + " items (expected " + 13 + ")");
    }

    if (CategoryName.length != 8)
    {
      System.out.println("ERROR: OracleLog.CategoryName[] has " + ModuleName.length + " items (expected " + 13 + ")");
    }
  }

  /** @deprecated */
  public static void setupFromSystemProperties()
  {
    int i = 0;

    securityExceptionWhileGettingSystemProperties = false;

    PrintStream localPrintStream = System.out;
    try
    {
      String str = null;
      str = getSystemProperty("oracle.jdbc.LogFile", null);

      if (str != null)
      {
        try
        {
          File localFile = new File(str);

          localPrintStream = new PrintStream(new FileOutputStream(localFile));
          i = 1;
        }
        catch (IOException localIOException)
        {
          localIOException.printStackTrace(System.out);
        }

      }

      str = getSystemProperty("oracle.jdbc.Trace", null);

      if ((str != null) && (str.compareTo("true") == 0)) {
        i = 1;
      }
      str = getSystemProperty("oracle.jdbc.PrintMask", null);

      if (str != null)
      {
        printMask = Integer.parseInt(str, 16);
        i = 1;
      }

      str = getSystemProperty("oracle.jdbc.PrintFields", null);

      if (str != null)
      {
        if (str.equalsIgnoreCase("default")) {
          printMask = 20;
        }
        if (str.equalsIgnoreCase("all")) {
          printMask = 268435455;
        }
        if (str.equalsIgnoreCase("thread")) {
          printMask = 84;
        }
        if (str.equalsIgnoreCase("none")) {
          printMask = 0;
        }
        i = 1;
      }

      str = getSystemProperty("oracle.jdbc.ModuleMask", null);

      if (str != null)
      {
        moduleMask = Integer.parseInt(str, 16);
        i = 1;
      }

      str = getSystemProperty("oracle.jdbc.CategoryMask", null);

      if (str != null)
      {
        categoryMask = Integer.parseInt(str, 16);
        i = 1;
      }

      for (int j = 0; j < 32; j++)
      {
        str = getSystemProperty("oracle.jdbc.SubmodMask" + j, null);

        if (str == null)
          continue;
        submodMasks[j] = Integer.parseInt(str, 16);
        i = 1;
      }

      str = getSystemProperty("oracle.jdbc.MaxPrintBytes", null);

      if (str != null)
      {
        maxPrintBytes = Integer.parseInt(str, 10);
        i = 1;
      }
    }
    catch (SecurityException localSecurityException)
    {
      securityExceptionWhileGettingSystemProperties = true;
    }

    if (i != 0)
      setLogStream(localPrintStream);
  }

  /** @deprecated */
  private static String getSystemProperty(String paramString)
  {
    return getSystemProperty(paramString, null);
  }

  /** @deprecated */
  private static String getSystemProperty(String paramString1, String paramString2)
  {
    if (paramString1 != null)
    {
      String str1 = paramString1;
      String str2 = paramString2;
      String[] arrayOfString = { paramString2 };
      AccessController.doPrivileged(new PrivilegedAction(arrayOfString, str1, str2) { private final String[] val$retStr;
        private final String val$fstr;
        private final String val$fdefaultValue;

        public Object run() { this.val$retStr[0] = System.getProperty(this.val$fstr, this.val$fdefaultValue);
          return null;
        }
      });
      return arrayOfString[0];
    }
    return paramString2;
  }

  /** @deprecated */
  public static void print(Object paramObject, int paramInt1, int paramInt2, int paramInt3, String paramString)
  {
    if (logWriter == null)
    {
      return;
    }

    int i = 0;

    if (((paramInt1 & moduleMask) != 0) && ((paramInt2 & submodMasks[getBitNumFromVector(paramInt1)]) != 0) && ((paramInt3 & categoryMask) != 0))
    {
      i = 1;
    }

    if ((paramInt3 & 0x4) != 0)
    {
      i = 1;
    }

    if ((warningEnabled == true) && ((paramInt3 & 0x8) != 0))
    {
      i = 1;
    }

    if (i != 0)
    {
      StringBuffer localStringBuffer = new StringBuffer("");

      if ((printMask & 0x1) != 0) {
        localStringBuffer.append(getMessageNumber());
      }
      if ((printMask & 0x2) != 0) {
        localStringBuffer.append(getCurrTimeStr());
      }
      if ((printMask & 0x4) != 0) {
        localStringBuffer.append(getModuleName(paramInt1));
      }
      if ((printMask & 0x8) != 0) {
        localStringBuffer.append(getBitNumFromVector(paramInt2) + " ");
      }
      if ((printMask & 0x10) != 0) {
        localStringBuffer.append(getCategoryName(paramInt3));
      }
      if ((printMask & 0x40) != 0) {
        localStringBuffer.append(Thread.currentThread() + "_" + Thread.currentThread().hashCode() + "_");
      }

      localStringBuffer.append(paramString);

      if ((printMask & 0x20) != 0)
      {
        localStringBuffer.append(" " + paramObject);
      }

      if (logStream == System.out) {
        logWriter.println();
      }
      logWriter.println(localStringBuffer.toString());
      logWriter.flush();
    }
  }

  /** @deprecated */
  public static void print(Object paramObject, int paramInt1, int paramInt2, int paramInt3, String paramString, Exception paramException)
  {
    if (logWriter == null)
    {
      return;
    }

    StringWriter localStringWriter = new StringWriter();
    PrintWriter localPrintWriter = new PrintWriter(localStringWriter);

    paramException.printStackTrace(localPrintWriter);
    print(paramObject, paramInt1, paramInt2, paramInt3, paramString + localStringWriter.toString());
  }

  /** @deprecated */
  public static void print(Object paramObject, int paramInt1, int paramInt2, String paramString)
  {
    print(paramObject, paramInt1, 1, paramInt2, paramString);
  }

  /** @deprecated */
  public static String info()
  {
    String str = null;

    if (isEnabled())
    {
      str = "Enabled logging (moduleMask " + getMaskHexStr(moduleMask) + ", categoryMask " + getMaskHexStr(categoryMask) + ")";
    }
    else
    {
      str = "Disabled logging";
    }

    return str;
  }

  /** @deprecated */
  public static String getModuleName(int paramInt)
  {
    String str = null;
    int i = 1;

    for (int j = 0; j < 13; j++)
    {
      if ((i & paramInt) != 0)
      {
        str = ModuleName[j];

        break;
      }

      i <<= 1;
    }

    return str;
  }

  /** @deprecated */
  public static String getCategoryName(int paramInt)
  {
    String str = null;
    int i = 1;

    for (int j = 0; j < 8; j++)
    {
      if ((i & paramInt) != 0)
      {
        str = CategoryName[j];

        break;
      }

      i <<= 1;
    }

    return str;
  }

  private static String getMessageNumber()
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    int i;
    synchronized (logWriter)
    {
      if (msgNumber == 2147483647)
      {
        msgNumber = 0;
      }
      else
      {
        msgNumber += 1;
      }

      i = msgNumber;
    }

    String str = Integer.toString(i);
    int j = str.length();

    for (int k = j; k < 10; k++)
    {
      localStringBuffer.append("0");
    }

    localStringBuffer.append(str);
    localStringBuffer.append(" ");

    return localStringBuffer.toString();
  }

  private static String getCurrTimeStr()
  {
    Date localDate = new Date();

    return localDate.toString() + " ";
  }

  /** @deprecated */
  public static String getMaskHexStr(int paramInt)
  {
    int i = 8;
    String str1 = Integer.toHexString(paramInt);
    char[] arrayOfChar = new char[i - str1.length()];

    for (int j = 0; j < arrayOfChar.length; j++)
    {
      arrayOfChar[j] = '0';
    }

    String str2 = new String(arrayOfChar);

    return new String("0x" + str2 + str1);
  }

  /** @deprecated */
  public static int getBitNumFromVector(int paramInt)
  {
    int i = 0;
    int j = 1;

    for (int k = 0; k < 32; k++)
    {
      if ((paramInt & j) != 0)
      {
        i = k;

        break;
      }

      j <<= 1;
    }

    return i;
  }

  /** @deprecated */
  public static String byteToHexString(byte paramByte)
  {
    StringBuffer localStringBuffer = new StringBuffer("");
    int i = 0xFF & paramByte;

    if (i <= 15)
      localStringBuffer.append("0x0");
    else {
      localStringBuffer.append("0x");
    }
    localStringBuffer.append(Integer.toHexString(i));

    return localStringBuffer.toString();
  }

  /** @deprecated */
  public static String bytesToPrintableForm(String paramString, byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte == null ? 0 : paramArrayOfByte.length;

    return bytesToPrintableForm(paramString, paramArrayOfByte, i);
  }

  /** @deprecated */
  public static String bytesToPrintableForm(String paramString, byte[] paramArrayOfByte, int paramInt)
  {
    String str = null;

    if (paramArrayOfByte == null)
      str = paramString + ": null";
    else {
      str = paramString + " (" + paramArrayOfByte.length + " bytes):\n" + bytesToFormattedStr(paramArrayOfByte, paramInt, "  ");
    }

    return str;
  }

  /** @deprecated */
  public static String bytesToFormattedStr(byte[] paramArrayOfByte, int paramInt, String paramString)
  {
    StringBuffer localStringBuffer = new StringBuffer("");

    if (paramString == null) {
      paramString = new String("");
    }
    localStringBuffer.append(paramString);

    if (paramArrayOfByte == null)
    {
      localStringBuffer.append("byte [] is null");

      return localStringBuffer.toString();
    }

    for (int i = 0; i < paramInt; i++)
    {
      if (i >= maxPrintBytes)
      {
        localStringBuffer.append("\n" + paramString + "... last " + (paramInt - maxPrintBytes) + " bytes were not printed to limit the output size");

        break;
      }

      if ((i > 0) && (i % 20 == 0)) {
        localStringBuffer.append("\n" + paramString);
      }
      if (i % 20 == 10) {
        localStringBuffer.append(" ");
      }
      int j = 0xFF & paramArrayOfByte[i];

      if (j <= 15) {
        localStringBuffer.append("0");
      }
      localStringBuffer.append(Integer.toHexString(j) + " ");
    }

    return localStringBuffer.toString();
  }

  /** @deprecated */
  public static byte[] strToUcs2Bytes(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    return charsToUcs2Bytes(paramString.toCharArray());
  }

  /** @deprecated */
  public static byte[] charsToUcs2Bytes(char[] paramArrayOfChar)
  {
    if (paramArrayOfChar == null) {
      return null;
    }
    return charsToUcs2Bytes(paramArrayOfChar, paramArrayOfChar.length);
  }

  /** @deprecated */
  public static byte[] charsToUcs2Bytes(char[] paramArrayOfChar, int paramInt)
  {
    if (paramArrayOfChar == null) {
      return null;
    }
    if (paramInt < 0) {
      return null;
    }
    return charsToUcs2Bytes(paramArrayOfChar, paramInt, 0);
  }

  /** @deprecated */
  public static byte[] charsToUcs2Bytes(char[] paramArrayOfChar, int paramInt1, int paramInt2)
  {
    if (paramArrayOfChar == null) {
      return null;
    }
    if (paramInt1 > paramArrayOfChar.length - paramInt2) {
      paramInt1 = paramArrayOfChar.length - paramInt2;
    }
    if (paramInt1 < 0) {
      return null;
    }
    byte[] arrayOfByte = new byte[2 * paramInt1];

    int j = paramInt2; for (int i = 0; j < paramInt1; j++)
    {
      arrayOfByte[(i++)] = (byte)(paramArrayOfChar[j] >> '\b' & 0xFF);
      arrayOfByte[(i++)] = (byte)(paramArrayOfChar[j] & 0xFF);
    }

    return arrayOfByte;
  }

  /** @deprecated */
  public static String toPrintableStr(String paramString, int paramInt)
  {
    if (paramString == null)
    {
      return "null";
    }

    if (paramString.length() > paramInt)
    {
      return paramString.substring(0, paramInt - 1) + "\n ... the actual length was " + paramString.length();
    }

    return paramString;
  }

  public static String toHex(long paramLong, int paramInt)
  {
    String str;
    switch (paramInt)
    {
    case 1:
      str = "00" + Long.toString(paramLong & 0xFF, 16);

      break;
    case 2:
      str = "0000" + Long.toString(paramLong & 0xFFFF, 16);

      break;
    case 3:
      str = "000000" + Long.toString(paramLong & 0xFFFFFF, 16);

      break;
    case 4:
      str = "00000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 5:
      str = "0000000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 6:
      str = "000000000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 7:
      str = "00000000000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 8:
      return toHex(paramLong >> 32, 4) + toHex(paramLong, 4).substring(2);
    default:
      return "more than 8 bytes";
    }

    return "0x" + str.substring(str.length() - 2 * paramInt);
  }

  public static String toHex(byte paramByte)
  {
    String str = "00" + Integer.toHexString(paramByte & 0xFF);

    return "0x" + str.substring(str.length() - 2);
  }

  public static String toHex(short paramShort)
  {
    return toHex(paramShort, 2);
  }

  public static String toHex(int paramInt)
  {
    return toHex(paramInt, 4);
  }

  public static String toHex(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      return "null";
    }
    if (paramInt > paramArrayOfByte.length) {
      return "byte array not long enough";
    }
    String str = "[";
    int i = Math.min(64, paramInt);

    for (int j = 0; j < i; j++)
    {
      str = str + toHex(paramArrayOfByte[j]) + " ";
    }

    if (i < paramInt) {
      str = str + "...";
    }
    return str + "]";
  }

  public static String toHex(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return "null";
    }
    return toHex(paramArrayOfByte, paramArrayOfByte.length);
  }

  static
  {
    submodMasks = new int[32];

    for (int i = 0; i < 32; i++)
    {
      submodMasks[i] = 268435455;
    }

    initialize();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleLog
 * JD-Core Version:    0.6.0
 */