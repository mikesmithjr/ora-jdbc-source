package oracle.jdbc.driver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

public class OracleLog {
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
    private static Vector registeredClassLOG = new Vector();
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
    public static final String[] ModuleName = { "DRVR ", "POOL", "DBCV ", "unused", "PIKL ",
            "JTTC ", "DATM ", "KPRB ", "XA   ", "SQLJ ", "JOCI ", "JPUB ", "T2C" };
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
    public static final String[] CategoryName = { "OPER ", "PERR ", "ERRO ", "WARN ", "FUNC ",
            "DBG1 ", "DBG2 ", "SQLS " };
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
    public static boolean TRACE = false;
    public static boolean recursiveTrace = false;
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

    public static boolean isDebugZip() {
        boolean ret_val = true;

        return ret_val;
    }

    public static boolean isPrivateLogAvailable() {
        boolean ret_val = false;

        return ret_val;
    }

    public static boolean isEnabled() {
        return TRACE;
    }

    /** @deprecated */
    public static void setLogWriter(PrintWriter out) {
        if (!isDebugZip()) {
            if (out != null) {
                out.println("Oracle Jdbc tracing is not avaliable in a non-debug zip/jar file");
                out.flush();
            }

            return;
        }

        if (out != null) {
            out
                    .println("OracleLog.setLogWriter not supported. Use setLogStream instead, or better yet, java.util.logging.");

            out.flush();
        }
    }

    /** @deprecated */
    public static PrintWriter getLogWriter() {
        return logWriter;
    }

    private static void initLoggers() {
        if (rootLogger == null) {
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
    public static void setLogStream(PrintStream out) {
        if (!isDebugZip()) {
            if (out != null) {
                out.println("Oracle Jdbc tracing is not avaliable in a non-debug zip/jar file");
                out.flush();
            }

            return;
        }

        initLoggers();

        if (printHandler != null) {
            final StreamHandler fprintHandler = printHandler;
            AccessController.doPrivileged(new PrivilegedAction() {

                public Object run() {
                    OracleLog.rootLogger.removeHandler(fprintHandler);
                    return null;
                }
            });
        }
        printHandler = new StreamHandler(out, new SimpleFormatter());

        final StreamHandler fprintHandler2 = printHandler;
        AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                OracleLog.printHandler.setLevel(Level.FINEST);
                OracleLog.rootLogger.addHandler(fprintHandler2);
                return null;
            }
        });
        setTrace(true);
    }

    /** @deprecated */
    public static PrintStream getLogStream() {
        return logStream;
    }

    /** @deprecated */
    public static void enableWarning(boolean enable) {
        warningEnabled = enable;
    }

    /** @deprecated */
    public static void setLogVolume(int level) {
        print(null, 1, 2, 1, "Set logging volume level to " + level);

        int category = 142;

        switch (level) {
        case 1:
            category = 142;

            break;
        case 2:
            category = 143;

            break;
        case 3:
            category = 268435455;

            break;
        default:
            print(null, 1, 2, 4, "Logging volume level " + level + " is not in the range of 1 to 3");

            return;
        }

        config(printMask, moduleMask, category);
    }

    /** @deprecated */
    public static void startLogging() {
        setLogStream(System.out);
    }

    /** @deprecated */
    public static void stopLogging() {
        setLogStream(null);
    }

    /** @deprecated */
    public static void config(int _printMask, int _moduleMask, int _categoryMask) {
        configForJavaUtilLogging(moduleMask, categoryMask);
    }

    static void configForJavaUtilLogging(int moduleMask, int categoryMask) {
        Level level = Level.OFF;

        if ((categoryMask & 0x40) != 0)
            level = Level.FINEST;
        else if ((categoryMask & 0x20) != 0)
            level = Level.FINER;
        else if ((categoryMask & 0x10) != 0)
            level = Level.FINE;
        else if ((categoryMask & 0x1) != 0)
            level = Level.INFO;
        else if ((categoryMask & 0x80) != 0)
            level = Level.CONFIG;
        else if ((categoryMask & 0x8) != 0)
            level = Level.WARNING;
        else if ((categoryMask & 0x2) != 0)
            level = Level.SEVERE;
        else if ((categoryMask & 0x4) != 0) {
            level = Level.SEVERE;
        }
        initLoggers();
        final Level flevel = level;
        AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                OracleLog.rootLogger.setLevel(flevel);
                return null;
            }
        });
        final Level off = Level.OFF;
        final int fmoduleMask = moduleMask;
        AccessController.doPrivileged(new PrivilegedAction() {

            public Object run() {
                OracleLog.driverLogger.setLevel((fmoduleMask & 0x1) != 0 ? flevel
                        : off);

                OracleLog.poolLogger.setLevel((fmoduleMask & 0x2) != 0 ? flevel
                        : off);

                OracleLog.conversionLogger
                        .setLevel((fmoduleMask & 0x4) != 0 ? flevel
                                : off);

                OracleLog.adtLogger.setLevel((fmoduleMask & 0x10) != 0 ? flevel
                        : off);

                OracleLog.thinLogger.setLevel((fmoduleMask & 0x20) != 0 ? flevel
                        : off);

                OracleLog.datumLogger.setLevel((fmoduleMask & 0x40) != 0 ? flevel
                        : off);

                OracleLog.kprbLogger.setLevel((fmoduleMask & 0x80) != 0 ? flevel
                        : off);

                OracleLog.xaLogger.setLevel((fmoduleMask & 0x100) != 0 ? flevel
                        : off);

                OracleLog.sqljLogger.setLevel((fmoduleMask & 0x200) != 0 ? flevel
                        : off);

                OracleLog.ociLogger.setLevel((fmoduleMask & 0x400) != 0 ? flevel
                        : off);

                OracleLog.jpubLogger.setLevel((fmoduleMask & 0x800) != 0 ? flevel
                        : off);

                return null;
            }
        });
    }

    /** @deprecated */
    public static void setSubmodMask(int module, int submodMask) {
        int mod_num = getBitNumFromVector(module);

        submodMasks[mod_num] = submodMask;

        print(null, 1, 2, 1, "Set logging sub-mask for module " + getMaskHexStr(moduleMask)
                + "(number " + mod_num + ") to " + getMaskHexStr(submodMask));
    }

    /** @deprecated */
    public static void setMaxPrintBytes(int maxbytes) {
        if (maxbytes > 0) {
            maxPrintBytes = maxbytes;
        }
        print(null, 1, 2, 1, "Set the maximum number of bytes to be printed to " + maxPrintBytes);
    }

    public static boolean registerClassNameAndGetCurrentTraceSetting(Class classObj) {
        if (!registeredClassLOG.contains(classObj))
            registeredClassLOG.addElement(classObj);
        return TRACE;
    }

    public static boolean registerClassNameAndGetCurrentPrivateTraceSetting(Class classObj) {
        return false;
    }

    public static void setTrace(boolean enable) {
        Enumeration regClasses = registeredClassLOG.elements();
        while (regClasses.hasMoreElements()) {
            Class classObj = (Class) regClasses.nextElement();
            try {
                classObj.getField("TRACE").setBoolean(null, enable);
            } catch (Exception e) {
            }
        }

        initLoggers();

        TRACE = enable;
    }

    /** @deprecated */
    public static void setPrivateTrace(boolean enable) {
    }

    private static void initialize() {
        internalCodeChecks();

        setupFromSystemProperties();
    }

    private static void internalCodeChecks() {
        if (ModuleName.length != 13) {
            System.out.println("ERROR: OracleLog.ModuleName[] has " + ModuleName.length
                    + " items (expected " + 13 + ")");
        }

        if (CategoryName.length != 8) {
            System.out.println("ERROR: OracleLog.CategoryName[] has " + ModuleName.length
                    + " items (expected " + 13 + ")");
        }
    }

    /** @deprecated */
    public static void setupFromSystemProperties() {
        boolean turnLoggingOn = false;

        securityExceptionWhileGettingSystemProperties = false;

        PrintStream logStream = System.out;
        try {
            String prop_str = null;
            prop_str = getSystemProperty("oracle.jdbc.LogFile", null);

            if (prop_str != null) {
                try {
                    File f = new File(prop_str);

                    logStream = new PrintStream(new FileOutputStream(f));
                    turnLoggingOn = true;
                } catch (IOException ex) {
                    ex.printStackTrace(System.out);
                }

            }

            prop_str = getSystemProperty("oracle.jdbc.Trace", null);

            if ((prop_str != null) && (prop_str.compareTo("true") == 0)) {
                turnLoggingOn = true;
            }
            prop_str = getSystemProperty("oracle.jdbc.PrintMask", null);

            if (prop_str != null) {
                printMask = Integer.parseInt(prop_str, 16);
                turnLoggingOn = true;
            }

            prop_str = getSystemProperty("oracle.jdbc.PrintFields", null);

            if (prop_str != null) {
                if (prop_str.equalsIgnoreCase("default")) {
                    printMask = 20;
                }
                if (prop_str.equalsIgnoreCase("all")) {
                    printMask = 268435455;
                }
                if (prop_str.equalsIgnoreCase("thread")) {
                    printMask = 84;
                }
                if (prop_str.equalsIgnoreCase("none")) {
                    printMask = 0;
                }
                turnLoggingOn = true;
            }

            prop_str = getSystemProperty("oracle.jdbc.ModuleMask", null);

            if (prop_str != null) {
                moduleMask = Integer.parseInt(prop_str, 16);
                turnLoggingOn = true;
            }

            prop_str = getSystemProperty("oracle.jdbc.CategoryMask", null);

            if (prop_str != null) {
                categoryMask = Integer.parseInt(prop_str, 16);
                turnLoggingOn = true;
            }

            for (int i = 0; i < 32; i++) {
                prop_str = getSystemProperty("oracle.jdbc.SubmodMask" + i, null);

                if (prop_str == null)
                    continue;
                submodMasks[i] = Integer.parseInt(prop_str, 16);
                turnLoggingOn = true;
            }

            prop_str = getSystemProperty("oracle.jdbc.MaxPrintBytes", null);

            if (prop_str != null) {
                maxPrintBytes = Integer.parseInt(prop_str, 10);
                turnLoggingOn = true;
            }
        } catch (SecurityException e) {
            securityExceptionWhileGettingSystemProperties = true;
        }

        if (turnLoggingOn)
            setLogStream(logStream);
    }

    /** @deprecated */
    private static String getSystemProperty(String str) {
        return getSystemProperty(str, null);
    }

    /** @deprecated */
    private static String getSystemProperty(String str, String defaultValue) {
        if (str != null) {
            final String fstr = str;
            final String fdefaultValue = defaultValue;
            final String[] retStr = { defaultValue };
            AccessController.doPrivileged(new PrivilegedAction() {

                public Object run() {
                    retStr[0] = System.getProperty(fstr, fdefaultValue);
                    return null;
                }
            });
            return retStr[0];
        }
        return defaultValue;
    }

    /** @deprecated */
    public static void print(Object thisObject, int module, int submodule, int category,
            String message) {
        if (logWriter == null) {
            return;
        }

        boolean show_output = false;

        if (((module & moduleMask) != 0)
                && ((submodule & submodMasks[getBitNumFromVector(module)]) != 0)
                && ((category & categoryMask) != 0)) {
            show_output = true;
        }

        if ((category & 0x4) != 0) {
            show_output = true;
        }

        if ((warningEnabled == true) && ((category & 0x8) != 0)) {
            show_output = true;
        }

        if (show_output) {
            StringBuffer strbuf = new StringBuffer("");

            if ((printMask & 0x1) != 0) {
                strbuf.append(getMessageNumber());
            }
            if ((printMask & 0x2) != 0) {
                strbuf.append(getCurrTimeStr());
            }
            if ((printMask & 0x4) != 0) {
                strbuf.append(getModuleName(module));
            }
            if ((printMask & 0x8) != 0) {
                strbuf.append(getBitNumFromVector(submodule) + " ");
            }
            if ((printMask & 0x10) != 0) {
                strbuf.append(getCategoryName(category));
            }
            if ((printMask & 0x40) != 0) {
                strbuf.append(Thread.currentThread() + "_" + Thread.currentThread().hashCode()
                        + "_");
            }

            strbuf.append(message);

            if ((printMask & 0x20) != 0) {
                strbuf.append(" " + thisObject);
            }

            if (logStream == System.out) {
                logWriter.println();
            }
            logWriter.println(strbuf.toString());
            logWriter.flush();
        }
    }

    /** @deprecated */
    public static void print(Object thisObject, int module, int submodule, int category,
            String message, Exception exception) {
        if (logWriter == null) {
            return;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        exception.printStackTrace(pw);
        print(thisObject, module, submodule, category, message + sw.toString());
    }

    /** @deprecated */
    public static void print(Object thisObject, int module, int category, String message) {
        print(thisObject, module, 1, category, message);
    }

    /** @deprecated */
    public static String info() {
        String ret_str = null;

        if (isEnabled()) {
            ret_str = "Enabled logging (moduleMask " + getMaskHexStr(moduleMask)
                    + ", categoryMask " + getMaskHexStr(categoryMask) + ")";
        } else {
            ret_str = "Disabled logging";
        }

        return ret_str;
    }

    /** @deprecated */
    public static String getModuleName(int module) {
        String ret_str = null;
        int mask = 1;

        for (int i = 0; i < 13; i++) {
            if ((mask & module) != 0) {
                ret_str = ModuleName[i];

                break;
            }

            mask <<= 1;
        }

        return ret_str;
    }

    /** @deprecated */
    public static String getCategoryName(int category) {
        String ret_str = null;
        int mask = 1;

        for (int i = 0; i < 8; i++) {
            if ((mask & category) != 0) {
                ret_str = CategoryName[i];

                break;
            }

            mask <<= 1;
        }

        return ret_str;
    }

    private static String getMessageNumber() {
        StringBuffer str_buf = new StringBuffer("");
        int num;
        synchronized (logWriter) {
            if (msgNumber == 2147483647) {
                msgNumber = 0;
            } else {
                msgNumber += 1;
            }

            num = msgNumber;
        }
        String num_str = Integer.toString(num);
        int num_str_len = num_str.length();

        for (int i = num_str_len; i < 10; i++) {
            str_buf.append("0");
        }

        str_buf.append(num_str);
        str_buf.append(" ");

        return str_buf.toString();
    }

    private static String getCurrTimeStr() {
        Date date = new Date();

        return date.toString() + " ";
    }

    /** @deprecated */
    public static String getMaskHexStr(int mask) {
        int n_nibbles = 8;
        String hex_str = Integer.toHexString(mask);
        char[] pad_array = new char[n_nibbles - hex_str.length()];

        for (int i = 0; i < pad_array.length; i++) {
            pad_array[i] = '0';
        }

        String pad_str = new String(pad_array);

        return new String("0x" + pad_str + hex_str);
    }

    /** @deprecated */
    public static int getBitNumFromVector(int bitVector) {
        int bit_num = 0;
        int test_mask = 1;

        for (int i = 0; i < 32; i++) {
            if ((bitVector & test_mask) != 0) {
                bit_num = i;

                break;
            }

            test_mask <<= 1;
        }

        return bit_num;
    }

    /** @deprecated */
    public static String byteToHexString(byte b) {
        StringBuffer buf = new StringBuffer("");
        int b_value = 0xFF & b;

        if (b_value <= 15)
            buf.append("0x0");
        else {
            buf.append("0x");
        }
        buf.append(Integer.toHexString(b_value));

        return buf.toString();
    }

    /** @deprecated */
    public static String bytesToPrintableForm(String header, byte[] bytes) {
        int bytes_len = bytes == null ? 0 : bytes.length;

        return bytesToPrintableForm(header, bytes, bytes_len);
    }

    /** @deprecated */
    public static String bytesToPrintableForm(String header, byte[] bytes, int nbytes) {
        String ret_str = null;

        if (bytes == null)
            ret_str = header + ": null";
        else {
            ret_str = header + " (" + bytes.length + " bytes):\n"
                    + bytesToFormattedStr(bytes, nbytes, "  ");
        }

        return ret_str;
    }

    /** @deprecated */
    public static String bytesToFormattedStr(byte[] bytes, int nbytes, String margin) {
        StringBuffer buf = new StringBuffer("");

        if (margin == null) {
            margin = new String("");
        }
        buf.append(margin);

        if (bytes == null) {
            buf.append("byte [] is null");

            return buf.toString();
        }

        for (int i = 0; i < nbytes; i++) {
            if (i >= maxPrintBytes) {
                buf.append("\n" + margin + "... last " + (nbytes - maxPrintBytes)
                        + " bytes were not printed to limit the output size");

                break;
            }

            if ((i > 0) && (i % 20 == 0)) {
                buf.append("\n" + margin);
            }
            if (i % 20 == 10) {
                buf.append(" ");
            }
            int b_value = 0xFF & bytes[i];

            if (b_value <= 15) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(b_value) + " ");
        }

        return buf.toString();
    }

    /** @deprecated */
    public static byte[] strToUcs2Bytes(String str) {
        if (str == null) {
            return null;
        }
        return charsToUcs2Bytes(str.toCharArray());
    }

    /** @deprecated */
    public static byte[] charsToUcs2Bytes(char[] chars) {
        if (chars == null) {
            return null;
        }
        return charsToUcs2Bytes(chars, chars.length);
    }

    /** @deprecated */
    public static byte[] charsToUcs2Bytes(char[] chars, int nchars) {
        if (chars == null) {
            return null;
        }
        if (nchars < 0) {
            return null;
        }
        return charsToUcs2Bytes(chars, nchars, 0);
    }

    /** @deprecated */
    public static byte[] charsToUcs2Bytes(char[] chars, int nchars, int offset) {
        if (chars == null) {
            return null;
        }
        if (nchars > chars.length - offset) {
            nchars = chars.length - offset;
        }
        if (nchars < 0) {
            return null;
        }
        byte[] bytes = new byte[2 * nchars];

        int char_i = offset;
        for (int byte_i = 0; char_i < nchars; char_i++) {
            bytes[(byte_i++)] = (byte) (chars[char_i] >> '\b' & 0xFF);
            bytes[(byte_i++)] = (byte) (chars[char_i] & 0xFF);
        }

        return bytes;
    }

    /** @deprecated */
    public static String toPrintableStr(String str, int maxchars) {
        if (str == null) {
            return "null";
        }

        if (str.length() > maxchars) {
            return str.substring(0, maxchars - 1) + "\n ... the actual length was " + str.length();
        }

        return str;
    }

    public static String toHex(long value, int bytes) {
        String result;
        switch (bytes) {
        case 1:
            result = "00" + Long.toString(value & 0xFF, 16);

            break;
        case 2:
            result = "0000" + Long.toString(value & 0xFFFF, 16);

            break;
        case 3:
            result = "000000" + Long.toString(value & 0xFFFFFF, 16);

            break;
        case 4:
            result = "00000000" + Long.toString(value & 0xFFFFFFFF, 16);

            break;
        case 5:
            result = "0000000000" + Long.toString(value & 0xFFFFFFFF, 16);

            break;
        case 6:
            result = "000000000000" + Long.toString(value & 0xFFFFFFFF, 16);

            break;
        case 7:
            result = "00000000000000" + Long.toString(value & 0xFFFFFFFF, 16);

            break;
        case 8:
            return toHex(value >> 32, 4) + toHex(value, 4).substring(2);
        default:
            return "more than 8 bytes";
        }

        return "0x" + result.substring(result.length() - 2 * bytes);
    }

    public static String toHex(byte value) {
        String result = "00" + Integer.toHexString(value & 0xFF);

        return "0x" + result.substring(result.length() - 2);
    }

    public static String toHex(short value) {
        return toHex(value, 2);
    }

    public static String toHex(int value) {
        return toHex(value, 4);
    }

    public static String toHex(byte[] value, int length) {
        if (value == null) {
            return "null";
        }
        if (length > value.length) {
            return "byte array not long enough";
        }
        String result = "[";
        int len = Math.min(64, length);

        for (int i = 0; i < len; i++) {
            result = result + toHex(value[i]) + " ";
        }

        if (len < length) {
            result = result + "...";
        }
        return result + "]";
    }

    public static String toHex(byte[] value) {
        if (value == null) {
            return "null";
        }
        return toHex(value, value.length);
    }

    static {
        submodMasks = new int[32];

        for (int i = 0; i < 32; i++) {
            submodMasks[i] = 268435455;
        }

        initialize();
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.OracleLog JD-Core Version: 0.6.0
 */