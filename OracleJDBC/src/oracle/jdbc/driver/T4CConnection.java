package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import oracle.jdbc.pool.OraclePooledConnection;
import oracle.net.ns.Communication;
import oracle.net.ns.NSProtocol;
import oracle.net.ns.NetException;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.BfileDBAccess;
import oracle.sql.BlobDBAccess;
import oracle.sql.CLOB;
import oracle.sql.ClobDBAccess;
import oracle.sql.LobPlsqlUtil;

class T4CConnection extends PhysicalConnection implements BfileDBAccess, BlobDBAccess, ClobDBAccess {
    static final short MIN_OVERSION_SUPPORTED = 7230;
    static final short MIN_TTCVER_SUPPORTED = 4;
    static final short V8_TTCVER_SUPPORTED = 5;
    static final short MAX_TTCVER_SUPPORTED = 6;
    static final int DEFAULT_LONG_PREFETCH_SIZE = 4080;
    static final String DEFAULT_CONNECT_STRING = "localhost:1521:orcl";
    static final int STREAM_CHUNK_SIZE = 255;
    static final int REFCURSOR_SIZE = 5;
    long LOGON_MODE = 0L;
    static final long SYSDBA = 8L;
    static final long SYSOPER = 16L;
    boolean isLoggedOn;
    private String password;
    Communication net;
    boolean readAsNonStream;
    T4CTTIoer oer;
    T4CMAREngine mare;
    T4C8TTIpro pro;
    T4C8TTIdty dty;
    T4CTTIrxd rxd;
    T4CTTIsto sto;
    T4CTTIoauthenticate auth;
    T4C7Oversion ver;
    T4C8Odscrarr describe;
    T4C8Oall all8;
    T4C8Oclose close8;
    T4C7Ocommoncall commoncall;
    T4C8TTIBfile bfileMsg;
    T4C8TTIBlob blobMsg;
    T4C8TTIClob clobMsg;
    T4CTTIoses oses;
    byte[] EMPTY_BYTE = new byte[0];
    T4CTTIOtxen otxen;
    T4CTTIOtxse otxse;
    T4CTTIk2rpc k2rpc;
    T4CTTIoscid oscid;
    T4CTTIokeyval okeyval;
    int[] cursorToClose;
    int cursorToCloseOffset;
    int[] queryToClose;
    int queryToCloseOffset;
    int sessionId;
    int serialNumber;
    boolean retainV9BehaviorForLong;
    Hashtable namespaces;
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:53_PDT_2005";

    T4CConnection(String ur, String us, String p, String db, Properties info,
            OracleDriverExtension ext) throws SQLException {
        super(ur, us, p, db, info, ext);
        String _property = (String) info.get("oracle.jdbc.RetainV9LongBindBehavior");
        this.retainV9BehaviorForLong = ((_property != null) && (_property.equalsIgnoreCase("true")));

        this.cursorToClose = new int[4];
        this.cursorToCloseOffset = 0;

        this.queryToClose = new int[10];
        this.queryToCloseOffset = 0;
        this.minVcsBindSize = 0;
        this.streamChunkSize = 255;

        this.namespaces = new Hashtable(5);
    }

    final void initializePassword(String p) throws SQLException {
        this.password = p;
    }

    void logon() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;

            String pw = "******";

            StringWriter props = new StringWriter();
            if (this.connectionProperties != null)
                this.connectionProperties.list(new PrintWriter(props));

            OracleLog.recursiveTrace = false;
        }

        try {
            if (this.isLoggedOn) {
                DatabaseError.throwSqlException(428);
            }

            if ((this.user == null) || (this.password == null)) {
                DatabaseError.throwSqlException(433);
            }

            if ((this.user.length() == 0) || (this.password.length() == 0)) {
                DatabaseError.throwSqlException(443);
            }

            if (this.database == null) {
                this.database = "localhost:1521:orcl";
            }

            connect(this.database, this.connectionProperties);

            this.all8 = new T4C8Oall(this.mare, this, this.oer);
            this.close8 = new T4C8Oclose(this.mare);

            this.sto = new T4CTTIsto(this.mare, this.oer);
            this.commoncall = new T4C7Ocommoncall(this.mare, this.oer, this);
            this.describe = new T4C8Odscrarr(this.mare, this.oer);
            this.bfileMsg = new T4C8TTIBfile(this.mare, this.oer);
            this.blobMsg = new T4C8TTIBlob(this.mare, this.oer);
            this.clobMsg = new T4C8TTIClob(this.mare, this.oer);
            this.otxen = new T4CTTIOtxen(this.mare, this.oer, this);
            this.otxse = new T4CTTIOtxse(this.mare, this.oer, this);
            this.k2rpc = new T4CTTIk2rpc(this.mare, this.oer, this);
            this.oses = new T4CTTIoses(this.mare);
            this.okeyval = new T4CTTIokeyval(this.mare);

            this.oscid = new T4CTTIoscid(this.mare);

            this.dty = new T4C8TTIdty(this.mare);

            this.dty.marshal();

            this.dty.receive();

            this.ver = new T4C7Oversion(this.mare, this.oer, this);

            this.ver.marshal();
            this.ver.receive();

            this.versionNumber = this.ver.getVersionNumber();
            this.mare.versionNumber = this.versionNumber;

            if (this.versionNumber < 7230) {
                DatabaseError.throwSqlException(441);
            }

            this.mare.types.setVersion(this.versionNumber);

            String logon_mode = (String) this.connectionProperties.get("internal_logon");

            if (logon_mode != null) {
                if (logon_mode.equalsIgnoreCase("sysoper")) {
                    this.LOGON_MODE = 64L;
                } else if (logon_mode.equalsIgnoreCase("sysdba")) {
                    this.LOGON_MODE = 32L;
                }

            } else {
                this.LOGON_MODE = 0L;
            }

            this.auth = new T4CTTIoauthenticate(this.mare, this.user, this.password,
                    this.connectionProperties, this.LOGON_MODE, this.ressourceManagerId, this.oer,
                    this);

            this.auth.marshalOsesskey();
            this.auth.receiveOsesskey();

            this.auth.marshalOauth();
            this.auth.receiveOauth();

            this.sessionId = this.auth.getSessionId();
            this.serialNumber = this.auth.getSerialNumber();

            this.isLoggedOn = true;
        } catch (IOException ex) {
            handleIOException(ex);

            DatabaseError.throwSqlException(ex);
        } catch (SQLException se) {
            try {
                this.net.disconnect();
            } catch (Exception e) {
            }

            this.isLoggedOn = false;

            throw se;
        }
    }

    void handleIOException(IOException ea) throws SQLException {
        try {
            this.net.disconnect();
        } catch (Exception e) {
        }

        this.isLoggedOn = false;
        this.lifecycle = 4;
    }

    synchronized void logoff() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.logoff()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            assertLoggedOn("T4CConnection.logoff");
            if (this.lifecycle == 8)
                return;
            sendPiggyBackedMessages();
            this.commoncall.init((short) 9);
            this.commoncall.marshal();
            this.commoncall.receive();

            this.net.disconnect();
        } catch (IOException ex) {
            handleIOException(ex);

            if (this.lifecycle != 8) {
                DatabaseError.throwSqlException(ex);
            }

        } finally {
            this.isLoggedOn = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.logoff(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized void doCommit() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.do_commit()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            assertLoggedOn("T4CConnection.do_commit");
            sendPiggyBackedMessages();
            this.commoncall.init((short) 14);
            this.commoncall.marshal();
            this.commoncall.receive();
        } catch (IOException ex) {
            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.do_commit(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized void doRollback() throws SQLException {
        try {
            assertLoggedOn("T4CConnection.do_rollback");
            sendPiggyBackedMessages();
            this.commoncall.init((short) 15);
            this.commoncall.marshal();
            this.commoncall.receive();
        } catch (IOException ex) {
            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.do_rollback(): return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized void doSetAutoCommit(boolean on) throws SQLException {
    }

    public synchronized void open(OracleStatement stmt) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.open(" + stmt + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("T4CConnection.open");

        stmt.setCursorId(0);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.open: return " + stmt, this);

            OracleLog.recursiveTrace = false;
        }
    }

    synchronized String doGetDatabaseProductVersion() throws SQLException {
        assertLoggedOn("T4CConnection.do_getDatabaseProductVersion");

        String result = null;
        byte[] resultBytes = this.ver.getVersion();
        try {
            result = new String(resultBytes, "UTF8");
        } catch (UnsupportedEncodingException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "T4CConnection.do_getDatabaseProductVersion: throwing "
                                                 + ex, this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.do_getDatabaseProductVersion(): "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    synchronized short doGetVersionNumber() throws SQLException {
        assertLoggedOn("T4CConnection.do_getVersionNumber");

        short result = this.ver.getVersionNumber();

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.do_getVersionNumber(): " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    OracleStatement RefCursorBytesToStatement(byte[] bytes, OracleStatement parent)
            throws SQLException {
        T4CStatement newstmt = new T4CStatement(this, -1, -1);
        try {
            int cursor = this.mare.unmarshalRefCursor(bytes);

            newstmt.setCursorId(cursor);

            newstmt.isOpen = true;

            newstmt.sqlObject = parent.sqlObject;
        } catch (IOException e) {
            handleIOException(e);
            DatabaseError.throwSqlException(e);
        }

        newstmt.sqlStringChanged = false;
        newstmt.needToParse = false;

        return newstmt;
    }

    void doCancel() throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "T4CConnection.do_cancel()", this);

            OracleLog.recursiveTrace = false;
        }

        try {
            this.net.sendBreak();
        } catch (NetException ne) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE, "T4CConnection.do_cancel: throwing " + ne,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(ne);
        } catch (IOException ne) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE, "T4CConnection.do_cancel: throwing " + ne,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ne);
            DatabaseError.throwSqlException(ne);
        }
    }

    void connect(String database, Properties userProperties) throws IOException, SQLException {
        if ((database == null) || (userProperties == null)) {
            DatabaseError.throwSqlException(433);
        }

        this.net = new NSProtocol();
        try {
            this.net.connect(database, userProperties);
        } catch (NetException ne) {
            throw new IOException(ne.getMessage());
        }

        this.mare = new T4CMAREngine(this.net);
        this.oer = new T4CTTIoer(this.mare, this);

        this.pro = new T4C8TTIpro(this.mare);

        this.pro.marshal();
        this.pro.receive();

        short oVersion = this.pro.getOracleVersion();
        short ServerCharSet = this.pro.getCharacterSet();
        short accessCharSet = DBConversion.findDriverCharSet(ServerCharSet, oVersion);

        this.conversion = new DBConversion(ServerCharSet, accessCharSet, this.pro.getncharCHARSET());

        this.mare.types.setServerConversion(accessCharSet != ServerCharSet);

        this.mare.types.setVersion(oVersion);

        if (DBConversion.isCharSetMultibyte(accessCharSet)) {
            if (DBConversion.isCharSetMultibyte(this.pro.getCharacterSet()))
                this.mare.types.setFlags((byte) 1);
            else
                this.mare.types.setFlags((byte) 2);
        } else {
            this.mare.types.setFlags(this.pro.getFlags());
        }
        this.mare.conv = this.conversion;
    }

    void sendPiggyBackedMessages() throws SQLException, IOException {
        sendPiggyBackedClose();

        if ((this.endToEndAnyChanged) && (this.versionNumber >= 10000)) {
            this.oscid.marshal(this.endToEndHasChanged, this.endToEndValues,
                               this.endToEndECIDSequenceNumber);

            for (int i = 0; i < 4; i++) {
                if (this.endToEndHasChanged[i] != false)
                    this.endToEndHasChanged[i] = false;
            }
        }
        this.endToEndAnyChanged = false;

        if (!this.namespaces.isEmpty()) {
            if (this.versionNumber >= 10200) {
                Object[] namespacesArr = this.namespaces.values().toArray();
                for (int i = 0; i < namespacesArr.length; i++) {
                    this.okeyval.marshal((Namespace) namespacesArr[i]);
                }
            }
            this.namespaces.clear();
        }
    }

    private void sendPiggyBackedClose() throws SQLException, IOException {
        if (this.queryToCloseOffset > 0) {
            this.close8.initCloseQuery();
            this.close8.marshal(this.queryToClose, this.queryToCloseOffset);

            this.queryToCloseOffset = 0;
        }

        if (this.cursorToCloseOffset > 0) {
            this.close8.initCloseStatement();
            this.close8.marshal(this.cursorToClose, this.cursorToCloseOffset);

            this.cursorToCloseOffset = 0;
        }
    }

    void doProxySession(int type, Properties prop) throws SQLException {
        try {
            sendPiggyBackedMessages();

            this.auth.marshalOauth(type, prop, this.sessionId, this.serialNumber);
            this.auth.receiveOauth();

            int prox_session_id = this.auth.getSessionId();
            int prox_serial_nb = this.auth.getSerialNumber();

            this.oses.marshal(prox_session_id, prox_serial_nb, 1);

            this.savedUser = this.user;

            if (type == 1)
                this.user = prop.getProperty("PROXY_USER_NAME");
            else {
                this.user = null;
            }
            this.isProxy = true;
        } catch (IOException ioe) {
            DatabaseError.throwSqlException(ioe);
        }
    }

    void closeProxySession() throws SQLException {
        try {
            sendPiggyBackedMessages();
            this.commoncall.init((short) 9);
            this.commoncall.marshal();
            this.commoncall.receive();

            this.oses.marshal(this.sessionId, this.serialNumber, 1);

            this.user = this.savedUser;
        } catch (IOException ioe) {
            DatabaseError.throwSqlException(ioe);
        }
    }

    public Properties getServerSessionInfo() throws SQLException {
        Properties prop = new Properties();
        prop.setProperty("SERVER_HOST", this.auth.connectionValues
                .getProperty("AUTH_SC_SERVER_HOST", ""));

        prop.setProperty("INSTANCE_NAME", this.auth.connectionValues
                .getProperty("AUTH_SC_INSTANCE_NAME", ""));

        prop.setProperty("DATABASE_NAME", this.auth.connectionValues
                .getProperty("AUTH_SC_DBUNIQUE_NAME", ""));

        prop.setProperty("SERVICE_NAME", this.auth.connectionValues
                .getProperty("AUTH_SC_SERVICE_NAME", ""));

        return prop;
    }

    public synchronized BlobDBAccess createBlobDBAccess() throws SQLException {
        return this;
    }

    public synchronized ClobDBAccess createClobDBAccess() throws SQLException {
        return this;
    }

    public synchronized BfileDBAccess createBfileDBAccess() throws SQLException {
        return this;
    }

    public synchronized long length(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.length(" + bfile
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("length");
        assertNotNull(bfile.shareBytes(), "length");

        needLine();

        long result = 0L;
        try {
            result = this.bfileMsg.getLength(bfile.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE, "TTC7Protocol.lobLength: IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.length:return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized long position(BFILE bfile, byte[] pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.position( "
                    + bfile + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (start < 1L) {
            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.hasPattern(bfile, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.position:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(BFILE bfile, BFILE pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.position( "
                    + bfile + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (start < 1L) {
            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.isSubLob(bfile, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.position:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized int getBytes(BFILE bfile, long pos, int length, byte[] bytes)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.getBytes("
                    + bfile + ", " + pos + ", " + length + ", " + bytes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getBytes");

        if (pos < 1L) {
            DatabaseError.throwSqlException(68, "getBytes()");
        }

        if ((length <= 0) || (bytes == null)) {
            return 0;
        }
        needLine();

        long result = 0L;

        if (length != 0) {
            try {
                result = this.bfileMsg.read(bfile.shareBytes(), pos, length, bytes);
            } catch (IOException ex) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.thinLogger
                            .log(Level.SEVERE,
                                 "oracle.jdbc.driver.T4CConnection.getBytes:IOException", this);

                    OracleLog.recursiveTrace = false;
                }

                handleIOException(ex);
                DatabaseError.throwSqlException(ex);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CConnection.getBytes: return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public String getName(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.getName("
                    + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getName");
        assertNotNull(bfile.shareBytes(), "getName");

        String result = LobPlsqlUtil.fileGetName(bfile);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.getName:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public String getDirAlias(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.getDirAlias("
                    + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getDirAlias");
        assertNotNull(bfile.shareBytes(), "getDirAlias");

        String result = LobPlsqlUtil.fileGetDirAlias(bfile);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.getDirAlias:return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized void openFile(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.openFile("
                    + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("openFile");
        assertNotNull(bfile.shareBytes(), "openFile");

        needLine();
        try {
            this.bfileMsg.open(bfile.shareBytes(), 11);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.openFile:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.openFile:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized boolean isFileOpen(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.openFile("
                    + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("openFile");
        assertNotNull(bfile.shareBytes(), "openFile");

        needLine();

        boolean result = false;
        try {
            result = this.bfileMsg.isOpen(bfile.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.isFileOpen:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.openFile:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized boolean fileExists(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.fileExists("
                    + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("fileExists");
        assertNotNull(bfile.shareBytes(), "fileExists");

        needLine();

        boolean result = false;
        try {
            result = this.bfileMsg.doesExist(bfile.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.closeFile:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.fileExists:return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized void closeFile(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.closeFile("
                    + bfile + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("closeFile");
        assertNotNull(bfile.shareBytes(), "closeFile");

        needLine();
        try {
            this.bfileMsg.close(bfile.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.closeFile:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.closeFile:return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void open(BFILE bfile, int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.open(" + bfile
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("open");
        assertNotNull(bfile.shareBytes(), "open");

        needLine();
        try {
            this.bfileMsg.open(bfile.shareBytes(), mode);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.open:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.open:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void close(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.close(" + bfile
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("close");
        assertNotNull(bfile.shareBytes(), "close");

        needLine();
        try {
            this.bfileMsg.close(bfile.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE, "oracle.jdbc.driver.T4CConnection.close:IOException",
                             this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.close:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized boolean isOpen(BFILE bfile) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isOpen(" + bfile
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("isOpen");
        assertNotNull(bfile.shareBytes(), "isOpen");

        needLine();

        boolean result = false;
        try {
            result = this.bfileMsg.isOpen(bfile.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.isOpen:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isOpen:return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public InputStream newInputStream(BFILE bfile, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.newInputStream("
                    + bfile + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleBlobInputStream(bfile, chunkSize);
        }

        return new OracleBlobInputStream(bfile, chunkSize, pos);
    }

    public InputStream newConversionInputStream(BFILE bfile, int conversionType)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionInputStream("
                                             + bfile + ", " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(bfile.shareBytes(), "newConversionInputStream");

        InputStream result = new OracleConversionInputStream(this.conversion, bfile
                .getBinaryStream(), conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionInputStream: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public Reader newConversionReader(BFILE bfile, int conversionType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionReader("
                                             + bfile + ", " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(bfile.shareBytes(), "newConversionReader");

        Reader result = new OracleConversionReader(this.conversion, bfile.getBinaryStream(),
                conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionReader: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized long length(BLOB blob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.length(" + blob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("length");
        assertNotNull(blob.shareBytes(), "length");

        needLine();

        long result = 0L;
        try {
            result = this.blobMsg.getLength(blob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE, "T4CConnection.lobLength: IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.length:return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(BLOB blob, byte[] pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.position( "
                    + blob + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("position");
        assertNotNull(blob.shareBytes(), "position");

        if (start < 1L) {
            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.hasPattern(blob, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.position:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(BLOB blob, BLOB pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.position( "
                    + blob + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("position");
        assertNotNull(blob.shareBytes(), "position");
        assertNotNull(pattern.shareBytes(), "position");

        if (start < 1L) {
            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.isSubLob(blob, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.position:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized int getBytes(BLOB blob, long pos, int length, byte[] bytes)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.getBytes("
                    + blob + ", " + pos + ", " + length + ", " + bytes + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getBytes");
        assertNotNull(blob.shareBytes(), "getBytes");

        if (pos < 1L) {
            DatabaseError.throwSqlException(68, "getBytes()");
        }

        if ((length <= 0) || (bytes == null)) {
            return 0;
        }
        needLine();

        long result = 0L;

        if (length != 0) {
            try {
                result = this.blobMsg.read(blob.shareBytes(), pos, length, bytes);
            } catch (IOException ex) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.thinLogger
                            .log(Level.SEVERE,
                                 "oracle.jdbc.driver.T4CConnection.getBytes:IOException", this);

                    OracleLog.recursiveTrace = false;
                }

                handleIOException(ex);
                DatabaseError.throwSqlException(ex);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CConnection.getBytes: return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public synchronized int putBytes(BLOB blob, long pos, byte[] bytes, int bytesOffset, int length)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.putBytes("
                    + blob + ", " + pos + ", " + bytes + ", " + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("putBytes");
        assertNotNull(blob.shareBytes(), "putBytes");

        if (pos < 1L) {
            DatabaseError.throwSqlException(68, "putBytes()");
        }

        if ((bytes == null) || (length <= 0)) {
            return 0;
        }
        needLine();

        long result = 0L;

        if (length != 0) {
            try {
                result = this.blobMsg.write(blob.shareBytes(), pos, bytes, bytesOffset, length);
            } catch (IOException ex) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.thinLogger
                            .log(Level.SEVERE,
                                 "oracle.jdbc.driver.T4CConnection.putBytes:IOException", this);

                    OracleLog.recursiveTrace = false;
                }

                handleIOException(ex);
                DatabaseError.throwSqlException(ex);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CConnection.putBytes: return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public synchronized int getChunkSize(BLOB blob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.getChunkSize("
                    + blob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getChunkSize");
        assertNotNull(blob.shareBytes(), "getChunkSize");

        needLine();

        long result = 0L;
        try {
            result = this.blobMsg.getChunkSize(blob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE,
                             "oracle.jdbc.driver.T4CConnection.getChunkSize:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.getChunkSize: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public synchronized void trim(BLOB blob, long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.trim(" + blob
                    + ", " + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("trim");
        assertNotNull(blob.shareBytes(), "trim");

        if (length < 0L) {
            DatabaseError.throwSqlException(68, "trim()");
        }

        needLine();
        try {
            this.blobMsg.trim(blob.shareBytes(), length);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.trim:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.trim: return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized BLOB createTemporaryBlob(Connection conn, boolean cache, int duration)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.createTemporaryBlob(" + conn
                                             + ", " + cache + ", " + duration + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("createTemporaryBlob");

        needLine();

        BLOB result = null;
        try {
            result = (BLOB) this.blobMsg.createTemporaryLob(this, cache, duration);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE,
                             "oracle.jdbc.driver.T4CConnection.createTemporaryBlob:IOException",
                             this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.createTemporaryBlob: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized void freeTemporary(BLOB blob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.freeTemporary("
                    + blob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("freeTemporary");
        assertNotNull(blob.shareBytes(), "freeTemporary");

        needLine();
        try {
            this.blobMsg.freeTemporaryLob(blob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE,
                             "oracle.jdbc.driver.T4CConnection.freeTemporary:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE, "oracle.jdbc.driver.T4CConnection.freeTemporary: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean isTemporary(BLOB blob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isTemporary("
                    + blob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isTemporaryLob("
                    + blob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean result = false;
        byte[] locator = blob.shareBytes();

        if (((locator[7] & 0x1) > 0) || ((locator[4] & 0x40) > 0)) {
            result = true;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.isTemporary: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized void open(BLOB blob, int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.open(" + blob
                    + ", " + mode + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("open");
        assertNotNull(blob.shareBytes(), "open");

        needLine();
        try {
            this.blobMsg.open(blob.shareBytes(), mode);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.open:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.open:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void close(BLOB blob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.close(" + blob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("close");
        assertNotNull(blob.shareBytes(), "close");

        needLine();
        try {
            this.blobMsg.close(blob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE, "oracle.jdbc.driver.T4CConnection.close:IOException",
                             this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.close:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized boolean isOpen(BLOB blob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isOpen(" + blob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("isOpen");
        assertNotNull(blob.shareBytes(), "isOpen");

        needLine();

        boolean result = false;
        try {
            result = this.blobMsg.isOpen(blob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.isOpen:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isOpen:return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public InputStream newInputStream(BLOB blob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.newInputStream("
                    + blob + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleBlobInputStream(blob, chunkSize);
        }

        return new OracleBlobInputStream(blob, chunkSize, pos);
    }

    public OutputStream newOutputStream(BLOB blob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newOutputStream(" + blob
                                             + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleBlobOutputStream(blob, chunkSize);
        }

        return new OracleBlobOutputStream(blob, chunkSize, pos);
    }

    public InputStream newConversionInputStream(BLOB blob, int conversionType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionInputStream("
                                             + blob + ", " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(blob.shareBytes(), "newConversionInputStream");

        InputStream result = new OracleConversionInputStream(this.conversion, blob
                .getBinaryStream(), conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionInputStream: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public Reader newConversionReader(BLOB blob, int conversionType) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionReader(" + blob
                                             + ", " + conversionType + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertNotNull(blob.shareBytes(), "newConversionReader");

        Reader result = new OracleConversionReader(this.conversion, blob.getBinaryStream(),
                conversionType);

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newConversionReader: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized long length(CLOB clob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.length(" + clob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("length");
        assertNotNull(clob.shareBytes(), "length");

        needLine();

        long result = 0L;
        try {
            result = this.clobMsg.getLength(clob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE, "TTC7Protocol.lobLength: IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.length:return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(CLOB clob, String pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.position( "
                    + clob + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pattern == null) {
            DatabaseError.throwSqlException(68, "position()");
        }

        assertLoggedOn("position");
        assertNotNull(clob.shareBytes(), "position");

        if (start < 1L) {
            DatabaseError.throwSqlException(68, "position()");
        }

        char[] chars = new char[pattern.length()];

        pattern.getChars(0, chars.length, chars, 0);

        long result = LobPlsqlUtil.hasPattern(clob, chars, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.position:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public long position(CLOB clob, CLOB pattern, long start) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.position( "
                    + clob + ", " + pattern + ", " + start + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pattern == null) {
            DatabaseError.throwSqlException(68, "position()");
        }

        assertLoggedOn("position");
        assertNotNull(clob.shareBytes(), "position");
        assertNotNull(pattern.shareBytes(), "position");

        if (start < 1L) {
            DatabaseError.throwSqlException(68, "position()");
        }

        long result = LobPlsqlUtil.isSubLob(clob, pattern, start);

        result = result == 0L ? -1L : result;

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.position:return: " + result,
                                     this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized int getChars(CLOB clob, long pos, int length, char[] chars)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.getChars(" + clob + ", "
                                             + pos + ", " + length + ", " + chars + "["
                                             + chars.length + "] )", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getChars");
        assertNotNull(clob.shareBytes(), "getChars");

        if (pos < 1L) {
            DatabaseError.throwSqlException(68, "getChars()");
        }

        if ((length <= 0) || (chars == null)) {
            return 0;
        }
        needLine();

        long result = 0L;

        if (length != 0) {
            try {
                boolean isNCLOB = clob.isNCLOB();

                result = this.clobMsg.read(clob.shareBytes(), pos, length, isNCLOB, chars);
            } catch (IOException ex) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.thinLogger
                            .log(Level.SEVERE,
                                 "oracle.jdbc.driver.T4CConnection.getChars:IOException", this);

                    OracleLog.recursiveTrace = false;
                }

                handleIOException(ex);
                DatabaseError.throwSqlException(ex);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CConnection.getChars: return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public synchronized int putChars(CLOB clob, long pos, char[] chars, int charsOffset, int length)
            throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.putChars("
                    + clob + ", " + pos + ", " + chars + ", " + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("putChars");
        assertNotNull(clob.shareBytes(), "putChars");

        if (pos < 1L) {
            DatabaseError.throwSqlException(68, "putChars()");
        }

        if ((chars == null) || (length <= 0)) {
            return 0;
        }
        needLine();

        long result = 0L;

        if (length != 0) {
            try {
                boolean isNCLOB = clob.isNCLOB();

                result = this.clobMsg.write(clob.shareBytes(), pos, isNCLOB, chars, charsOffset,
                                            length);
            } catch (IOException ex) {
                if ((TRACE) && (!OracleLog.recursiveTrace)) {
                    OracleLog.recursiveTrace = true;
                    OracleLog.thinLogger
                            .log(Level.SEVERE,
                                 "oracle.jdbc.driver.T4CConnection.putChars:IOException", this);

                    OracleLog.recursiveTrace = false;
                }

                handleIOException(ex);
                DatabaseError.throwSqlException(ex);
            }

        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE,
                         "oracle.jdbc.driver.T4CConnection.putChars: return: " + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public synchronized int getChunkSize(CLOB clob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.getChunkSize("
                    + clob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("getChunkSize");
        assertNotNull(clob.shareBytes(), "getChunkSize");

        needLine();

        long result = 0L;
        try {
            result = this.clobMsg.getChunkSize(clob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE,
                             "oracle.jdbc.driver.T4CConnection.getChunkSize:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.getChunkSize: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return (int) result;
    }

    public synchronized void trim(CLOB clob, long length) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.trim(" + clob
                    + ", " + length + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("trim");
        assertNotNull(clob.shareBytes(), "trim");

        if (length < 0L) {
            DatabaseError.throwSqlException(68, "trim()");
        }

        needLine();
        try {
            this.clobMsg.trim(clob.shareBytes(), length);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.trim:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.trim: return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized CLOB createTemporaryClob(Connection conn, boolean cache, int duration,
            short form_of_use) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.createTemporaryClob(" + conn
                                             + ", " + cache + ", " + duration + ", " + form_of_use
                                             + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("createTemporaryClob");

        needLine();

        CLOB result = null;
        try {
            result = (CLOB) this.clobMsg.createTemporaryLob(this, cache, duration, form_of_use);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE,
                             "oracle.jdbc.driver.T4CConnection.createTemporaryClob:IOException",
                             this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.createTemporaryClob: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized void freeTemporary(CLOB clob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.freeTemporary("
                    + clob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("freeTemporary");
        assertNotNull(clob.shareBytes(), "freeTemporary");

        needLine();
        try {
            this.clobMsg.freeTemporaryLob(clob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE,
                             "oracle.jdbc.driver.T4CConnection.freeTemporary:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger
                    .log(Level.FINE, "oracle.jdbc.driver.T4CConnection.freeTemporary: return", this);

            OracleLog.recursiveTrace = false;
        }
    }

    public boolean isTemporary(CLOB clob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isTemporary("
                    + clob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isTemporaryLob("
                    + clob + ")", this);

            OracleLog.recursiveTrace = false;
        }

        boolean result = false;
        byte[] locator = clob.shareBytes();

        if (((locator[7] & 0x1) > 0) || ((locator[4] & 0x40) > 0)) {
            result = true;
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.isTemporary: return: "
                                             + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public synchronized void open(CLOB clob, int mode) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.open(" + clob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("open");
        assertNotNull(clob.shareBytes(), "open");

        needLine();
        try {
            this.clobMsg.open(clob.shareBytes(), mode);
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.open:IOException", this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.open:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized void close(CLOB clob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.close(" + clob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("close");
        assertNotNull(clob.shareBytes(), "close");

        needLine();
        try {
            this.clobMsg.close(clob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger
                        .log(Level.SEVERE, "oracle.jdbc.driver.T4CConnection.close:IOException",
                             this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.close:return",
                                     this);

            OracleLog.recursiveTrace = false;
        }
    }

    public synchronized boolean isOpen(CLOB clob) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isOpen(" + clob
                    + ")", this);

            OracleLog.recursiveTrace = false;
        }

        assertLoggedOn("isOpen");
        assertNotNull(clob.shareBytes(), "isOpen");

        boolean result = false;

        needLine();
        try {
            result = this.clobMsg.isOpen(clob.shareBytes());
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE,
                                         "oracle.jdbc.driver.T4CConnection.isOpen:IOException",
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.isOpen:return: "
                    + result, this);

            OracleLog.recursiveTrace = false;
        }

        return result;
    }

    public InputStream newInputStream(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.newInputStream("
                    + clob + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobInputStream(clob, chunkSize);
        }

        return new OracleClobInputStream(clob, chunkSize, pos);
    }

    public OutputStream newOutputStream(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE,
                                     "oracle.jdbc.driver.T4CConnection.newOutputStream(" + clob
                                             + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobOutputStream(clob, chunkSize);
        }

        return new OracleClobOutputStream(clob, chunkSize, pos);
    }

    public Reader newReader(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.newReader("
                    + clob + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobReader(clob, chunkSize);
        }

        return new OracleClobReader(clob, chunkSize, pos);
    }

    public Writer newWriter(CLOB clob, int chunkSize, long pos) throws SQLException {
        if ((TRACE) && (!OracleLog.recursiveTrace)) {
            OracleLog.recursiveTrace = true;
            OracleLog.thinLogger.log(Level.FINE, "oracle.jdbc.driver.T4CConnection.newWriter("
                    + clob + ", " + chunkSize + ", " + pos + ")", this);

            OracleLog.recursiveTrace = false;
        }

        if (pos == 0L) {
            return new OracleClobWriter(clob, chunkSize);
        }

        return new OracleClobWriter(clob, chunkSize, pos);
    }

    void assertLoggedOn(String caller) throws SQLException {
        if (!this.isLoggedOn) {
            DatabaseError.throwSqlException(430);
        }
    }

    void assertNotNull(byte[] bytes, String caller) throws NullPointerException {
        if (bytes == null) {
            throw new NullPointerException("bytes are null");
        }
    }

    void internalClose() throws SQLException {
        super.internalClose();

        this.isLoggedOn = false;
    }

    void doAbort() throws SQLException {
        try {
            this.net.abort();
        } catch (NetException ne) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE, "T4CConnection.doAbort: throwing " + ne,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            DatabaseError.throwSqlException(ne);
        } catch (IOException ne) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.thinLogger.log(Level.SEVERE, "T4CConnection.doAbort: throwing " + ne,
                                         this);

                OracleLog.recursiveTrace = false;
            }

            handleIOException(ne);
            DatabaseError.throwSqlException(ne);
        }
    }

    protected void doDescribeTable(AutoKeyInfo info) throws SQLException {
        T4CStatement t4cStmt = new T4CStatement(this, -1, -1);
        t4cStmt.open();

        String tableName = info.getTableName();
        String sql = "SELECT * FROM " + tableName;

        t4cStmt.sqlObject.initialize(sql);

        Accessor[] accessors = null;
        try {
            this.describe.init(t4cStmt, 0);
            this.describe.sqltext = t4cStmt.sqlObject.getSqlBytes(false, false);
            this.describe.marshal();
            accessors = this.describe.receive(accessors);
        } catch (IOException ex) {
            handleIOException(ex);
            DatabaseError.throwSqlException(ex);
        }

        int numColumns = this.describe.numuds;

        info.allocateSpaceForDescribedData(numColumns);

        for (int i = 0; i < numColumns; i++) {
            Accessor accessor = accessors[i];
            String columnName = accessor.columnName;
            int type = accessor.describeType;
            int maxLength = accessor.describeMaxLength;
            boolean nullable = accessor.nullable;
            short formOfUse = accessor.formOfUse;
            int precision = accessor.precision;
            int scale = accessor.scale;
            String typeName = accessor.describeTypeName;

            info.fillDescribedData(i, columnName, type, maxLength, nullable, formOfUse, precision,
                                   scale, typeName);
        }

        t4cStmt.close();
    }

    void doSetApplicationContext(String nameSpace, String attribute, String value)
            throws SQLException {
        Namespace ns = (Namespace) this.namespaces.get(nameSpace);
        if (ns == null) {
            ns = new Namespace(nameSpace);
            this.namespaces.put(nameSpace, ns);
        }
        ns.setAttribute(attribute, value);
    }

    void doClearAllApplicationContext(String nameSpace) throws SQLException {
        Namespace ns = new Namespace(nameSpace);
        ns.clear();
        this.namespaces.put(nameSpace, ns);
    }

    public void getPropertyForPooledConnection(OraclePooledConnection pc) throws SQLException {
        super.getPropertyForPooledConnection(pc, this.password);
    }

    final void getPasswordInternal(T4CXAResource caller) throws SQLException {
        caller.setPasswordInternal(this.password);
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.jdbc.driver.T4CConnection"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.jdbc.driver.T4CConnection JD-Core Version: 0.6.0
 */