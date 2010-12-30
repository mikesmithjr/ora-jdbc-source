package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4CTTIoexec extends T4CTTIfun
{
  static final int EXE_COMMIT_ON_SUCCESS = 1;
  static final int EXE_LEAVE_CUR_MAPPED = 2;
  static final int EXE_BATCH_DML_ERRORS = 4;
  static final int EXE_SCROL_READ_ONLY = 8;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIoexec(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(3, 0, 4);

    setMarshalingEngine(paramT4CMAREngine);
  }

  void marshal(int paramInt1, int paramInt2, int paramInt3)
    throws IOException, SQLException
  {
    super.marshalFunHeader();
    this.meg.marshalSWORD(paramInt1);
    this.meg.marshalSWORD(paramInt2);
    this.meg.marshalSWORD(0);
    this.meg.marshalSWORD(paramInt3);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIoexec
 * JD-Core Version:    0.6.0
 */