package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.net.ns.BreakNetException;

class T4C8Odscrarr extends T4CTTIfun
{
  byte operationflags = 7;
  byte[] sqltext = new byte[0];
  long sqlparseversion = 2L;
  T4CTTIdcb dcb;
  int cursor_id = 0;

  int numuds = 0;

  boolean numitemsO2U = true;

  boolean udsarrayO2U = true;
  boolean numudsO2U = true;
  boolean colnameO2U = true;
  boolean lencolsO2U = true;

  OracleStatement statement = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  T4C8Odscrarr(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer)
    throws IOException, SQLException
  {
    super(3, 0, 43);

    setMarshalingEngine(paramT4CMAREngine);

    this.oer = paramT4CTTIoer;
    this.funCode = 98;
    this.dcb = new T4CTTIdcb(paramT4CMAREngine);
  }

  void init(OracleStatement paramOracleStatement, int paramInt)
    throws IOException, SQLException
  {
    this.numuds = 0;

    this.numitemsO2U = true;
    this.udsarrayO2U = true;
    this.numudsO2U = true;
    this.colnameO2U = true;
    this.lencolsO2U = true;

    this.oer.init();

    this.cursor_id = paramOracleStatement.cursorId;
    this.statement = paramOracleStatement;

    this.operationflags = 7;
    this.sqltext = new byte[0];
    this.sqlparseversion = 2L;

    this.dcb.init(paramOracleStatement, paramInt);
  }

  void marshal()
    throws IOException
  {
    marshalFunHeader();

    this.meg.marshalUB1((short)this.operationflags);
    this.meg.marshalSWORD(this.cursor_id);

    if (this.sqltext.length == 0)
      this.meg.marshalNULLPTR();
    else {
      this.meg.marshalPTR();
    }
    this.meg.marshalSB4(this.sqltext.length);

    this.meg.marshalUB4(this.sqlparseversion);

    this.meg.marshalO2U(this.udsarrayO2U);
    this.meg.marshalO2U(this.numudsO2U);

    this.meg.marshalCHR(this.sqltext);
  }

  Accessor[] receive(Accessor[] paramArrayOfAccessor)
    throws SQLException, IOException
  {
    int i = 0;

    while (i == 0)
    {
      try
      {
        int j = this.meg.unmarshalSB1();

        switch (j)
        {
        case 8:
          paramArrayOfAccessor = this.dcb.receiveCommon(paramArrayOfAccessor, true);
          this.numuds = this.dcb.numuds;

          break;
        case 4:
          this.oer.init();
          this.oer.unmarshal();
          this.oer.processError();

          i = 1;

          break;
        case 9:
          i = 1;

          break;
        default:
          DatabaseError.throwSqlException(401);
        }

      }
      catch (BreakNetException localBreakNetException)
      {
      }

    }

    return paramArrayOfAccessor;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8Odscrarr
 * JD-Core Version:    0.6.0
 */