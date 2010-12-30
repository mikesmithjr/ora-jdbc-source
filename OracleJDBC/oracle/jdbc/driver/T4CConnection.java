package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Properties;
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

class T4CConnection extends PhysicalConnection
  implements BfileDBAccess, BlobDBAccess, ClobDBAccess
{
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
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4CConnection(String paramString1, String paramString2, String paramString3, String paramString4, Properties paramProperties, OracleDriverExtension paramOracleDriverExtension)
    throws SQLException
  {
    super(paramString1, paramString2, paramString3, paramString4, paramProperties, paramOracleDriverExtension);
    String str = (String)paramProperties.get("oracle.jdbc.RetainV9LongBindBehavior");
    this.retainV9BehaviorForLong = ((str != null) && (str.equalsIgnoreCase("true")));

    this.cursorToClose = new int[4];
    this.cursorToCloseOffset = 0;

    this.queryToClose = new int[10];
    this.queryToCloseOffset = 0;
    this.minVcsBindSize = 0;
    this.streamChunkSize = 255;

    this.namespaces = new Hashtable(5);
  }

  final void initializePassword(String paramString) throws SQLException
  {
    this.password = paramString;
  }

  void logon()
    throws SQLException
  {
    try
    {
      if (this.isLoggedOn)
      {
        DatabaseError.throwSqlException(428);
      }

      if ((this.user == null) || (this.password == null))
      {
        DatabaseError.throwSqlException(433);
      }

      if ((this.user.length() == 0) || (this.password.length() == 0))
      {
        DatabaseError.throwSqlException(443);
      }

      if (this.database == null)
      {
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

      if (this.versionNumber < 7230)
      {
        DatabaseError.throwSqlException(441);
      }

      this.mare.types.setVersion(this.versionNumber);

      String str = (String)this.connectionProperties.get("internal_logon");

      if (str != null)
      {
        if (str.equalsIgnoreCase("sysoper"))
        {
          this.LOGON_MODE = 64L;
        }
        else if (str.equalsIgnoreCase("sysdba"))
        {
          this.LOGON_MODE = 32L;
        }

      }
      else
      {
        this.LOGON_MODE = 0L;
      }

      this.auth = new T4CTTIoauthenticate(this.mare, this.user, this.password, this.connectionProperties, this.LOGON_MODE, this.ressourceManagerId, this.oer, this);

      this.auth.marshalOsesskey();
      this.auth.receiveOsesskey();

      this.auth.marshalOauth();
      this.auth.receiveOauth();

      this.sessionId = this.auth.getSessionId();
      this.serialNumber = this.auth.getSerialNumber();

      this.isLoggedOn = true;
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);

      DatabaseError.throwSqlException(localIOException);
    }
    catch (SQLException localSQLException)
    {
      try
      {
        this.net.disconnect();
      }
      catch (Exception localException)
      {
      }

      this.isLoggedOn = false;

      throw localSQLException;
    }
  }

  void handleIOException(IOException paramIOException)
    throws SQLException
  {
    try
    {
      this.net.disconnect();
    }
    catch (Exception localException)
    {
    }

    this.isLoggedOn = false;
    this.lifecycle = 4;
  }

  synchronized void logoff()
    throws SQLException
  {
    try
    {
      assertLoggedOn("T4CConnection.logoff");
      if (this.lifecycle == 8)
        return;
      sendPiggyBackedMessages();
      this.commoncall.init(9);
      this.commoncall.marshal();
      this.commoncall.receive();

      this.net.disconnect();
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);

      if (this.lifecycle != 8)
      {
        DatabaseError.throwSqlException(localIOException);
      }

    }
    finally
    {
      this.isLoggedOn = false;
    }
  }

  synchronized void doCommit()
    throws SQLException
  {
    try
    {
      assertLoggedOn("T4CConnection.do_commit");
      sendPiggyBackedMessages();
      this.commoncall.init(14);
      this.commoncall.marshal();
      this.commoncall.receive();
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  synchronized void doRollback()
    throws SQLException
  {
    try
    {
      assertLoggedOn("T4CConnection.do_rollback");
      sendPiggyBackedMessages();
      this.commoncall.init(15);
      this.commoncall.marshal();
      this.commoncall.receive();
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  synchronized void doSetAutoCommit(boolean paramBoolean)
    throws SQLException
  {
  }

  public synchronized void open(OracleStatement paramOracleStatement)
    throws SQLException
  {
    assertLoggedOn("T4CConnection.open");

    paramOracleStatement.setCursorId(0);
  }

  synchronized String doGetDatabaseProductVersion()
    throws SQLException
  {
    assertLoggedOn("T4CConnection.do_getDatabaseProductVersion");

    String str = null;
    byte[] arrayOfByte = this.ver.getVersion();
    try
    {
      str = new String(arrayOfByte, "UTF8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      DatabaseError.throwSqlException(localUnsupportedEncodingException);
    }

    return str;
  }

  synchronized short doGetVersionNumber()
    throws SQLException
  {
    assertLoggedOn("T4CConnection.do_getVersionNumber");

    int i = this.ver.getVersionNumber();

    return i;
  }

  OracleStatement RefCursorBytesToStatement(byte[] paramArrayOfByte, OracleStatement paramOracleStatement)
    throws SQLException
  {
    T4CStatement localT4CStatement = new T4CStatement(this, -1, -1);
    try
    {
      int i = this.mare.unmarshalRefCursor(paramArrayOfByte);

      localT4CStatement.setCursorId(i);

      localT4CStatement.isOpen = true;

      localT4CStatement.sqlObject = paramOracleStatement.sqlObject;
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    localT4CStatement.sqlStringChanged = false;
    localT4CStatement.needToParse = false;

    return localT4CStatement;
  }

  void doCancel()
    throws SQLException
  {
    try
    {
      this.net.sendBreak();
    }
    catch (NetException localNetException)
    {
      DatabaseError.throwSqlException(localNetException);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  void connect(String paramString, Properties paramProperties)
    throws IOException, SQLException
  {
    if ((paramString == null) || (paramProperties == null))
    {
      DatabaseError.throwSqlException(433);
    }

    this.net = new NSProtocol();
    try
    {
      this.net.connect(paramString, paramProperties);
    }
    catch (NetException localNetException)
    {
      throw new IOException(localNetException.getMessage());
    }

    this.mare = new T4CMAREngine(this.net);
    this.oer = new T4CTTIoer(this.mare, this);

    this.pro = new T4C8TTIpro(this.mare);

    this.pro.marshal();
    this.pro.receive();

    short s1 = this.pro.getOracleVersion();
    short s2 = this.pro.getCharacterSet();
    short s3 = DBConversion.findDriverCharSet(s2, s1);

    this.conversion = new DBConversion(s2, s3, this.pro.getncharCHARSET());

    this.mare.types.setServerConversion(s3 != s2);

    this.mare.types.setVersion(s1);

    if (DBConversion.isCharSetMultibyte(s3))
    {
      if (DBConversion.isCharSetMultibyte(this.pro.getCharacterSet()))
        this.mare.types.setFlags(1);
      else
        this.mare.types.setFlags(2);
    }
    else {
      this.mare.types.setFlags(this.pro.getFlags());
    }
    this.mare.conv = this.conversion;
  }

  void sendPiggyBackedMessages()
    throws SQLException, IOException
  {
    sendPiggyBackedClose();

    if ((this.endToEndAnyChanged) && (this.versionNumber >= 10000))
    {
      this.oscid.marshal(this.endToEndHasChanged, this.endToEndValues, this.endToEndECIDSequenceNumber);

      for (int i = 0; i < 4; i++) {
        if (this.endToEndHasChanged[i] != 0)
          this.endToEndHasChanged[i] = false;
      }
    }
    this.endToEndAnyChanged = false;

    if (!this.namespaces.isEmpty())
    {
      if (this.versionNumber >= 10200)
      {
        Object[] arrayOfObject = this.namespaces.values().toArray();
        for (int j = 0; j < arrayOfObject.length; j++)
        {
          this.okeyval.marshal((Namespace)arrayOfObject[j]);
        }
      }
      this.namespaces.clear();
    }
  }

  private void sendPiggyBackedClose()
    throws SQLException, IOException
  {
    if (this.queryToCloseOffset > 0)
    {
      this.close8.initCloseQuery();
      this.close8.marshal(this.queryToClose, this.queryToCloseOffset);

      this.queryToCloseOffset = 0;
    }

    if (this.cursorToCloseOffset > 0)
    {
      this.close8.initCloseStatement();
      this.close8.marshal(this.cursorToClose, this.cursorToCloseOffset);

      this.cursorToCloseOffset = 0;
    }
  }

  void doProxySession(int paramInt, Properties paramProperties) throws SQLException
  {
    try
    {
      sendPiggyBackedMessages();

      this.auth.marshalOauth(paramInt, paramProperties, this.sessionId, this.serialNumber);
      this.auth.receiveOauth();

      int i = this.auth.getSessionId();
      int j = this.auth.getSerialNumber();

      this.oses.marshal(i, j, 1);

      this.savedUser = this.user;

      if (paramInt == 1)
        this.user = paramProperties.getProperty("PROXY_USER_NAME");
      else {
        this.user = null;
      }
      this.isProxy = true;
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  void closeProxySession()
    throws SQLException
  {
    try
    {
      sendPiggyBackedMessages();
      this.commoncall.init(9);
      this.commoncall.marshal();
      this.commoncall.receive();

      this.oses.marshal(this.sessionId, this.serialNumber, 1);

      this.user = this.savedUser;
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public Properties getServerSessionInfo()
    throws SQLException
  {
    Properties localProperties = new Properties();
    localProperties.setProperty("SERVER_HOST", this.auth.connectionValues.getProperty("AUTH_SC_SERVER_HOST", ""));

    localProperties.setProperty("INSTANCE_NAME", this.auth.connectionValues.getProperty("AUTH_SC_INSTANCE_NAME", ""));

    localProperties.setProperty("DATABASE_NAME", this.auth.connectionValues.getProperty("AUTH_SC_DBUNIQUE_NAME", ""));

    localProperties.setProperty("SERVICE_NAME", this.auth.connectionValues.getProperty("AUTH_SC_SERVICE_NAME", ""));

    return localProperties;
  }

  public synchronized BlobDBAccess createBlobDBAccess()
    throws SQLException
  {
    return this;
  }

  public synchronized ClobDBAccess createClobDBAccess()
    throws SQLException
  {
    return this;
  }

  public synchronized BfileDBAccess createBfileDBAccess()
    throws SQLException
  {
    return this;
  }

  public synchronized long length(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("length");
    assertNotNull(paramBFILE.shareBytes(), "length");

    needLine();

    long l = 0L;
    try
    {
      l = this.bfileMsg.getLength(paramBFILE.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return l;
  }

  public synchronized long position(BFILE paramBFILE, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.hasPattern(paramBFILE, paramArrayOfByte, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public long position(BFILE paramBFILE1, BFILE paramBFILE2, long paramLong)
    throws SQLException
  {
    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.isSubLob(paramBFILE1, paramBFILE2, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public synchronized int getBytes(BFILE paramBFILE, long paramLong, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    assertLoggedOn("getBytes");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "getBytes()");
    }

    if ((paramInt <= 0) || (paramArrayOfByte == null)) {
      return 0;
    }
    needLine();

    long l = 0L;

    if (paramInt != 0)
    {
      try
      {
        l = this.bfileMsg.read(paramBFILE.shareBytes(), paramLong, paramInt, paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        handleIOException(localIOException);
        DatabaseError.throwSqlException(localIOException);
      }

    }

    return (int)l;
  }

  public String getName(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("getName");
    assertNotNull(paramBFILE.shareBytes(), "getName");

    String str = LobPlsqlUtil.fileGetName(paramBFILE);

    return str;
  }

  public String getDirAlias(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("getDirAlias");
    assertNotNull(paramBFILE.shareBytes(), "getDirAlias");

    String str = LobPlsqlUtil.fileGetDirAlias(paramBFILE);

    return str;
  }

  public synchronized void openFile(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("openFile");
    assertNotNull(paramBFILE.shareBytes(), "openFile");

    needLine();
    try
    {
      this.bfileMsg.open(paramBFILE.shareBytes(), 11);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized boolean isFileOpen(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("openFile");
    assertNotNull(paramBFILE.shareBytes(), "openFile");

    needLine();

    boolean bool = false;
    try
    {
      bool = this.bfileMsg.isOpen(paramBFILE.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return bool;
  }

  public synchronized boolean fileExists(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("fileExists");
    assertNotNull(paramBFILE.shareBytes(), "fileExists");

    needLine();

    boolean bool = false;
    try
    {
      bool = this.bfileMsg.doesExist(paramBFILE.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return bool;
  }

  public synchronized void closeFile(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("closeFile");
    assertNotNull(paramBFILE.shareBytes(), "closeFile");

    needLine();
    try
    {
      this.bfileMsg.close(paramBFILE.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized void open(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    assertLoggedOn("open");
    assertNotNull(paramBFILE.shareBytes(), "open");

    needLine();
    try
    {
      this.bfileMsg.open(paramBFILE.shareBytes(), paramInt);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized void close(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("close");
    assertNotNull(paramBFILE.shareBytes(), "close");

    needLine();
    try
    {
      this.bfileMsg.close(paramBFILE.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized boolean isOpen(BFILE paramBFILE)
    throws SQLException
  {
    assertLoggedOn("isOpen");
    assertNotNull(paramBFILE.shareBytes(), "isOpen");

    needLine();

    boolean bool = false;
    try
    {
      bool = this.bfileMsg.isOpen(paramBFILE.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return bool;
  }

  public InputStream newInputStream(BFILE paramBFILE, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleBlobInputStream(paramBFILE, paramInt);
    }

    return new OracleBlobInputStream(paramBFILE, paramInt, paramLong);
  }

  public InputStream newConversionInputStream(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    assertNotNull(paramBFILE.shareBytes(), "newConversionInputStream");

    OracleConversionInputStream localOracleConversionInputStream = new OracleConversionInputStream(this.conversion, paramBFILE.getBinaryStream(), paramInt);

    return localOracleConversionInputStream;
  }

  public Reader newConversionReader(BFILE paramBFILE, int paramInt)
    throws SQLException
  {
    assertNotNull(paramBFILE.shareBytes(), "newConversionReader");

    OracleConversionReader localOracleConversionReader = new OracleConversionReader(this.conversion, paramBFILE.getBinaryStream(), paramInt);

    return localOracleConversionReader;
  }

  public synchronized long length(BLOB paramBLOB)
    throws SQLException
  {
    assertLoggedOn("length");
    assertNotNull(paramBLOB.shareBytes(), "length");

    needLine();

    long l = 0L;
    try
    {
      l = this.blobMsg.getLength(paramBLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return l;
  }

  public long position(BLOB paramBLOB, byte[] paramArrayOfByte, long paramLong)
    throws SQLException
  {
    assertLoggedOn("position");
    assertNotNull(paramBLOB.shareBytes(), "position");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.hasPattern(paramBLOB, paramArrayOfByte, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public long position(BLOB paramBLOB1, BLOB paramBLOB2, long paramLong)
    throws SQLException
  {
    assertLoggedOn("position");
    assertNotNull(paramBLOB1.shareBytes(), "position");
    assertNotNull(paramBLOB2.shareBytes(), "position");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.isSubLob(paramBLOB1, paramBLOB2, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public synchronized int getBytes(BLOB paramBLOB, long paramLong, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    assertLoggedOn("getBytes");
    assertNotNull(paramBLOB.shareBytes(), "getBytes");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "getBytes()");
    }

    if ((paramInt <= 0) || (paramArrayOfByte == null)) {
      return 0;
    }
    needLine();

    long l = 0L;

    if (paramInt != 0)
    {
      try
      {
        l = this.blobMsg.read(paramBLOB.shareBytes(), paramLong, paramInt, paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        handleIOException(localIOException);
        DatabaseError.throwSqlException(localIOException);
      }

    }

    return (int)l;
  }

  public synchronized int putBytes(BLOB paramBLOB, long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    assertLoggedOn("putBytes");
    assertNotNull(paramBLOB.shareBytes(), "putBytes");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "putBytes()");
    }

    if ((paramArrayOfByte == null) || (paramInt2 <= 0)) {
      return 0;
    }
    needLine();

    long l = 0L;

    if (paramInt2 != 0)
    {
      try
      {
        l = this.blobMsg.write(paramBLOB.shareBytes(), paramLong, paramArrayOfByte, paramInt1, paramInt2);
      }
      catch (IOException localIOException)
      {
        handleIOException(localIOException);
        DatabaseError.throwSqlException(localIOException);
      }

    }

    return (int)l;
  }

  public synchronized int getChunkSize(BLOB paramBLOB)
    throws SQLException
  {
    assertLoggedOn("getChunkSize");
    assertNotNull(paramBLOB.shareBytes(), "getChunkSize");

    needLine();

    long l = 0L;
    try
    {
      l = this.blobMsg.getChunkSize(paramBLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return (int)l;
  }

  public synchronized void trim(BLOB paramBLOB, long paramLong)
    throws SQLException
  {
    assertLoggedOn("trim");
    assertNotNull(paramBLOB.shareBytes(), "trim");

    if (paramLong < 0L)
    {
      DatabaseError.throwSqlException(68, "trim()");
    }

    needLine();
    try
    {
      this.blobMsg.trim(paramBLOB.shareBytes(), paramLong);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized BLOB createTemporaryBlob(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException
  {
    assertLoggedOn("createTemporaryBlob");

    needLine();

    BLOB localBLOB = null;
    try
    {
      localBLOB = (BLOB)this.blobMsg.createTemporaryLob(this, paramBoolean, paramInt);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return localBLOB;
  }

  public synchronized void freeTemporary(BLOB paramBLOB)
    throws SQLException
  {
    assertLoggedOn("freeTemporary");
    assertNotNull(paramBLOB.shareBytes(), "freeTemporary");

    needLine();
    try
    {
      this.blobMsg.freeTemporaryLob(paramBLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public boolean isTemporary(BLOB paramBLOB)
    throws SQLException
  {
    int i = 0;
    byte[] arrayOfByte = paramBLOB.shareBytes();

    if (((arrayOfByte[7] & 0x1) > 0) || ((arrayOfByte[4] & 0x40) > 0)) {
      i = 1;
    }

    return i;
  }

  public synchronized void open(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    assertLoggedOn("open");
    assertNotNull(paramBLOB.shareBytes(), "open");

    needLine();
    try
    {
      this.blobMsg.open(paramBLOB.shareBytes(), paramInt);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized void close(BLOB paramBLOB)
    throws SQLException
  {
    assertLoggedOn("close");
    assertNotNull(paramBLOB.shareBytes(), "close");

    needLine();
    try
    {
      this.blobMsg.close(paramBLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized boolean isOpen(BLOB paramBLOB)
    throws SQLException
  {
    assertLoggedOn("isOpen");
    assertNotNull(paramBLOB.shareBytes(), "isOpen");

    needLine();

    boolean bool = false;
    try
    {
      bool = this.blobMsg.isOpen(paramBLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return bool;
  }

  public InputStream newInputStream(BLOB paramBLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleBlobInputStream(paramBLOB, paramInt);
    }

    return new OracleBlobInputStream(paramBLOB, paramInt, paramLong);
  }

  public OutputStream newOutputStream(BLOB paramBLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleBlobOutputStream(paramBLOB, paramInt);
    }

    return new OracleBlobOutputStream(paramBLOB, paramInt, paramLong);
  }

  public InputStream newConversionInputStream(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    assertNotNull(paramBLOB.shareBytes(), "newConversionInputStream");

    OracleConversionInputStream localOracleConversionInputStream = new OracleConversionInputStream(this.conversion, paramBLOB.getBinaryStream(), paramInt);

    return localOracleConversionInputStream;
  }

  public Reader newConversionReader(BLOB paramBLOB, int paramInt)
    throws SQLException
  {
    assertNotNull(paramBLOB.shareBytes(), "newConversionReader");

    OracleConversionReader localOracleConversionReader = new OracleConversionReader(this.conversion, paramBLOB.getBinaryStream(), paramInt);

    return localOracleConversionReader;
  }

  public synchronized long length(CLOB paramCLOB)
    throws SQLException
  {
    assertLoggedOn("length");
    assertNotNull(paramCLOB.shareBytes(), "length");

    needLine();

    long l = 0L;
    try
    {
      l = this.clobMsg.getLength(paramCLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return l;
  }

  public long position(CLOB paramCLOB, String paramString, long paramLong)
    throws SQLException
  {
    if (paramString == null) {
      DatabaseError.throwSqlException(68, "position()");
    }

    assertLoggedOn("position");
    assertNotNull(paramCLOB.shareBytes(), "position");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    char[] arrayOfChar = new char[paramString.length()];

    paramString.getChars(0, arrayOfChar.length, arrayOfChar, 0);

    long l = LobPlsqlUtil.hasPattern(paramCLOB, arrayOfChar, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public long position(CLOB paramCLOB1, CLOB paramCLOB2, long paramLong)
    throws SQLException
  {
    if (paramCLOB2 == null) {
      DatabaseError.throwSqlException(68, "position()");
    }

    assertLoggedOn("position");
    assertNotNull(paramCLOB1.shareBytes(), "position");
    assertNotNull(paramCLOB2.shareBytes(), "position");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "position()");
    }

    long l = LobPlsqlUtil.isSubLob(paramCLOB1, paramCLOB2, paramLong);

    l = l == 0L ? -1L : l;

    return l;
  }

  public synchronized int getChars(CLOB paramCLOB, long paramLong, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    assertLoggedOn("getChars");
    assertNotNull(paramCLOB.shareBytes(), "getChars");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "getChars()");
    }

    if ((paramInt <= 0) || (paramArrayOfChar == null)) {
      return 0;
    }
    needLine();

    long l = 0L;

    if (paramInt != 0)
    {
      try
      {
        boolean bool = paramCLOB.isNCLOB();

        l = this.clobMsg.read(paramCLOB.shareBytes(), paramLong, paramInt, bool, paramArrayOfChar);
      }
      catch (IOException localIOException)
      {
        handleIOException(localIOException);
        DatabaseError.throwSqlException(localIOException);
      }

    }

    return (int)l;
  }

  public synchronized int putChars(CLOB paramCLOB, long paramLong, char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SQLException
  {
    assertLoggedOn("putChars");
    assertNotNull(paramCLOB.shareBytes(), "putChars");

    if (paramLong < 1L)
    {
      DatabaseError.throwSqlException(68, "putChars()");
    }

    if ((paramArrayOfChar == null) || (paramInt2 <= 0)) {
      return 0;
    }
    needLine();

    long l = 0L;

    if (paramInt2 != 0)
    {
      try
      {
        boolean bool = paramCLOB.isNCLOB();

        l = this.clobMsg.write(paramCLOB.shareBytes(), paramLong, bool, paramArrayOfChar, paramInt1, paramInt2);
      }
      catch (IOException localIOException)
      {
        handleIOException(localIOException);
        DatabaseError.throwSqlException(localIOException);
      }

    }

    return (int)l;
  }

  public synchronized int getChunkSize(CLOB paramCLOB)
    throws SQLException
  {
    assertLoggedOn("getChunkSize");
    assertNotNull(paramCLOB.shareBytes(), "getChunkSize");

    needLine();

    long l = 0L;
    try
    {
      l = this.clobMsg.getChunkSize(paramCLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return (int)l;
  }

  public synchronized void trim(CLOB paramCLOB, long paramLong)
    throws SQLException
  {
    assertLoggedOn("trim");
    assertNotNull(paramCLOB.shareBytes(), "trim");

    if (paramLong < 0L)
    {
      DatabaseError.throwSqlException(68, "trim()");
    }

    needLine();
    try
    {
      this.clobMsg.trim(paramCLOB.shareBytes(), paramLong);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized CLOB createTemporaryClob(Connection paramConnection, boolean paramBoolean, int paramInt, short paramShort)
    throws SQLException
  {
    assertLoggedOn("createTemporaryClob");

    needLine();

    CLOB localCLOB = null;
    try
    {
      localCLOB = (CLOB)this.clobMsg.createTemporaryLob(this, paramBoolean, paramInt, paramShort);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return localCLOB;
  }

  public synchronized void freeTemporary(CLOB paramCLOB)
    throws SQLException
  {
    assertLoggedOn("freeTemporary");
    assertNotNull(paramCLOB.shareBytes(), "freeTemporary");

    needLine();
    try
    {
      this.clobMsg.freeTemporaryLob(paramCLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public boolean isTemporary(CLOB paramCLOB)
    throws SQLException
  {
    int i = 0;
    byte[] arrayOfByte = paramCLOB.shareBytes();

    if (((arrayOfByte[7] & 0x1) > 0) || ((arrayOfByte[4] & 0x40) > 0)) {
      i = 1;
    }

    return i;
  }

  public synchronized void open(CLOB paramCLOB, int paramInt)
    throws SQLException
  {
    assertLoggedOn("open");
    assertNotNull(paramCLOB.shareBytes(), "open");

    needLine();
    try
    {
      this.clobMsg.open(paramCLOB.shareBytes(), paramInt);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized void close(CLOB paramCLOB)
    throws SQLException
  {
    assertLoggedOn("close");
    assertNotNull(paramCLOB.shareBytes(), "close");

    needLine();
    try
    {
      this.clobMsg.close(paramCLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  public synchronized boolean isOpen(CLOB paramCLOB)
    throws SQLException
  {
    assertLoggedOn("isOpen");
    assertNotNull(paramCLOB.shareBytes(), "isOpen");

    boolean bool = false;

    needLine();
    try
    {
      bool = this.clobMsg.isOpen(paramCLOB.shareBytes());
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    return bool;
  }

  public InputStream newInputStream(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobInputStream(paramCLOB, paramInt);
    }

    return new OracleClobInputStream(paramCLOB, paramInt, paramLong);
  }

  public OutputStream newOutputStream(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobOutputStream(paramCLOB, paramInt);
    }

    return new OracleClobOutputStream(paramCLOB, paramInt, paramLong);
  }

  public Reader newReader(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobReader(paramCLOB, paramInt);
    }

    return new OracleClobReader(paramCLOB, paramInt, paramLong);
  }

  public Writer newWriter(CLOB paramCLOB, int paramInt, long paramLong)
    throws SQLException
  {
    if (paramLong == 0L)
    {
      return new OracleClobWriter(paramCLOB, paramInt);
    }

    return new OracleClobWriter(paramCLOB, paramInt, paramLong);
  }

  void assertLoggedOn(String paramString)
    throws SQLException
  {
    if (!this.isLoggedOn)
    {
      DatabaseError.throwSqlException(430);
    }
  }

  void assertNotNull(byte[] paramArrayOfByte, String paramString)
    throws NullPointerException
  {
    if (paramArrayOfByte == null)
    {
      throw new NullPointerException("bytes are null");
    }
  }

  void internalClose()
    throws SQLException
  {
    super.internalClose();

    this.isLoggedOn = false;
  }

  void doAbort() throws SQLException
  {
    try
    {
      this.net.abort();
    }
    catch (NetException localNetException)
    {
      DatabaseError.throwSqlException(localNetException);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }
  }

  protected void doDescribeTable(AutoKeyInfo paramAutoKeyInfo)
    throws SQLException
  {
    T4CStatement localT4CStatement = new T4CStatement(this, -1, -1);
    localT4CStatement.open();

    String str1 = paramAutoKeyInfo.getTableName();
    String str2 = "SELECT * FROM " + str1;

    localT4CStatement.sqlObject.initialize(str2);

    Accessor[] arrayOfAccessor = null;
    try
    {
      this.describe.init(localT4CStatement, 0);
      this.describe.sqltext = localT4CStatement.sqlObject.getSqlBytes(false, false);
      this.describe.marshal();
      arrayOfAccessor = this.describe.receive(arrayOfAccessor);
    }
    catch (IOException localIOException)
    {
      handleIOException(localIOException);
      DatabaseError.throwSqlException(localIOException);
    }

    int i = this.describe.numuds;

    paramAutoKeyInfo.allocateSpaceForDescribedData(i);

    for (int i1 = 0; i1 < i; i1++)
    {
      Accessor localAccessor = arrayOfAccessor[i1];
      String str3 = localAccessor.columnName;
      int j = localAccessor.describeType;
      int k = localAccessor.describeMaxLength;
      boolean bool = localAccessor.nullable;
      short s = localAccessor.formOfUse;
      int m = localAccessor.precision;
      int n = localAccessor.scale;
      String str4 = localAccessor.describeTypeName;

      paramAutoKeyInfo.fillDescribedData(i1, str3, j, k, bool, s, m, n, str4);
    }

    localT4CStatement.close();
  }

  void doSetApplicationContext(String paramString1, String paramString2, String paramString3)
    throws SQLException
  {
    Namespace localNamespace = (Namespace)this.namespaces.get(paramString1);
    if (localNamespace == null)
    {
      localNamespace = new Namespace(paramString1);
      this.namespaces.put(paramString1, localNamespace);
    }
    localNamespace.setAttribute(paramString2, paramString3);
  }

  void doClearAllApplicationContext(String paramString)
    throws SQLException
  {
    Namespace localNamespace = new Namespace(paramString);
    localNamespace.clear();
    this.namespaces.put(paramString, localNamespace);
  }

  public void getPropertyForPooledConnection(OraclePooledConnection paramOraclePooledConnection)
    throws SQLException
  {
    super.getPropertyForPooledConnection(paramOraclePooledConnection, this.password);
  }

  final void getPasswordInternal(T4CXAResource paramT4CXAResource)
    throws SQLException
  {
    paramT4CXAResource.setPasswordInternal(this.password);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CConnection
 * JD-Core Version:    0.6.0
 */