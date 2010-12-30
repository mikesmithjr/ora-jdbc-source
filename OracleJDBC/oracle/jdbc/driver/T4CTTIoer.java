package oracle.jdbc.driver;

import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

class T4CTTIoer extends T4CTTIMsg
{
  final int MAXERRBUF = 512;
  long curRowNumber;
  int retCode;
  int arrayElemWError;
  int arrayElemErrno;
  int currCursorID;
  short errorPosition;
  short sqlType;
  byte oerFatal;
  short flags;
  short userCursorOpt;
  short upiParam;
  short warningFlag;
  int osError;
  short stmtNumber;
  short callNumber;
  int pad1;
  long successIters;
  int partitionId;
  int tableId;
  int slotNumber;
  long rba;
  long blockNumber;
  int warnLength = 0;
  int warnFlag = 0;

  int[] errorLength = new int[1];
  byte[] errorMsg;
  T4CConnection connection;
  static final int ORA1403 = 1403;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIoer(T4CMAREngine paramT4CMAREngine)
  {
    setMarshalingEngine(paramT4CMAREngine);
  }

  T4CTTIoer(T4CMAREngine paramT4CMAREngine, T4CConnection paramT4CConnection)
  {
    setMarshalingEngine(paramT4CMAREngine);

    this.connection = paramT4CConnection;
  }

  void init()
  {
    this.retCode = 0;
    this.errorMsg = null;
  }

  int unmarshal()
    throws IOException, SQLException
  {
    if (this.meg.versionNumber >= 10000)
    {
      short s = (short)this.meg.unmarshalUB2();

      this.connection.endToEndECIDSequenceNumber = s;
    }

    this.curRowNumber = this.meg.unmarshalUB4();
    this.retCode = this.meg.unmarshalUB2();
    this.arrayElemWError = this.meg.unmarshalUB2();
    this.arrayElemErrno = this.meg.unmarshalUB2();
    this.currCursorID = this.meg.unmarshalUB2();
    this.errorPosition = this.meg.unmarshalSB2();
    this.sqlType = this.meg.unmarshalUB1();
    this.oerFatal = this.meg.unmarshalSB1();
    this.flags = this.meg.unmarshalSB2();
    this.userCursorOpt = this.meg.unmarshalSB2();
    this.upiParam = this.meg.unmarshalUB1();
    this.warningFlag = this.meg.unmarshalUB1();

    this.rba = this.meg.unmarshalUB4();
    this.partitionId = this.meg.unmarshalUB2();
    this.tableId = this.meg.unmarshalUB1();
    this.blockNumber = this.meg.unmarshalUB4();
    this.slotNumber = this.meg.unmarshalUB2();

    this.osError = this.meg.unmarshalSWORD();
    this.stmtNumber = this.meg.unmarshalUB1();
    this.callNumber = this.meg.unmarshalUB1();
    this.pad1 = this.meg.unmarshalUB2();
    this.successIters = this.meg.unmarshalUB4();

    if (this.retCode != 0)
    {
      this.errorMsg = this.meg.unmarshalCLRforREFS();
      this.errorLength[0] = this.errorMsg.length;
    }

    return this.currCursorID;
  }

  void unmarshalWarning()
    throws IOException, SQLException
  {
    this.retCode = this.meg.unmarshalUB2();
    this.warnLength = this.meg.unmarshalUB2();
    this.warnFlag = this.meg.unmarshalUB2();

    if ((this.retCode != 0) && (this.warnLength > 0))
    {
      this.errorMsg = this.meg.unmarshalCHR(this.warnLength);
      this.errorLength[0] = this.warnLength;
    }
  }

  void print(int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    System.out.println("**** Retcode is " + this.retCode);

    if (this.retCode != 0) {
      System.out.println("**** Error Message: " + this.meg.conv.CharBytesToString(this.errorMsg, this.errorLength[0], true));
    }
    else if (this.warnFlag != 0)
      OracleLog.print(this, paramInt1, paramInt2, paramInt3, "Warning Message: " + this.meg.conv.CharBytesToString(this.errorMsg, this.warnLength, true));
  }

  void processError()
    throws SQLException
  {
    processError(true);
  }

  void processError(boolean paramBoolean) throws SQLException
  {
    processError(paramBoolean, null);
  }

  void processError(OracleStatement paramOracleStatement) throws SQLException
  {
    processError(true, paramOracleStatement);
  }

  void processError(boolean paramBoolean, OracleStatement paramOracleStatement)
    throws SQLException
  {
    if (this.retCode != 0)
    {
      switch (this.retCode)
      {
      case 28:
      case 600:
      case 1012:
      case 3113:
      case 3114:
        this.connection.internalClose();
      }

      if (paramBoolean)
      {
        DatabaseError.throwSqlException(this.meg.conv.CharBytesToString(this.errorMsg, this.errorLength[0], true), DatabaseError.ErrorToSQLState(this.retCode), this.retCode);
      }
      else
      {
        return;
      }

    }

    if (!paramBoolean) {
      return;
    }

    if ((this.warningFlag & 0x1) == 1)
    {
      int i = this.warningFlag & 0xFFFFFFFE;

      if (((i & 0x20) == 32) || ((i & 0x4) == 4)) {
        throw DatabaseError.newSqlException(110);
      }
    }

    if ((this.connection != null) && (this.connection.plsqlCompilerWarnings))
    {
      if ((this.flags & 0x4) == 4)
        paramOracleStatement.foundPlsqlCompilerWarning();
    }
  }

  void processWarning()
    throws SQLException
  {
    if (this.retCode != 0)
    {
      throw DatabaseError.newSqlWarning(this.meg.conv.CharBytesToString(this.errorMsg, this.errorLength[0], true), DatabaseError.ErrorToSQLState(this.retCode), this.retCode);
    }
  }

  int getCurRowNumber()
    throws SQLException
  {
    return (int)this.curRowNumber;
  }

  int getRetCode()
  {
    return this.retCode;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIoer
 * JD-Core Version:    0.6.0
 */