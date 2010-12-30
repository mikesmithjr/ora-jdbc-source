package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8TTIuds extends T4CTTIMsg
{
  T4CTTIoac udsoac;
  boolean udsnull;
  short udscnl;
  byte optimizeOAC;
  byte[] udscolnm;
  short udscolnl;
  byte[] udssnm;
  long udssnl;
  int[] snnumchar;
  byte[] udstnm;
  long udstnl;
  int[] tnnumchar;
  int[] numBytes;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4C8TTIuds(T4CMAREngine paramT4CMAREngine)
    throws SQLException, IOException
  {
    setMarshalingEngine(paramT4CMAREngine);

    this.udsoac = new T4CTTIoac(paramT4CMAREngine);
  }

  void unmarshal()
    throws IOException, SQLException
  {
    this.udsoac.unmarshal();

    int i = this.meg.unmarshalUB1();
    this.udsnull = (i > 0);
    this.udscnl = this.meg.unmarshalUB1();

    this.numBytes = new int[1];
    this.udscolnm = this.meg.unmarshalDALC(this.numBytes);

    this.snnumchar = new int[1];
    this.udssnm = this.meg.unmarshalDALC(this.snnumchar);
    this.udssnl = this.udssnm.length;

    this.tnnumchar = new int[1];
    this.udstnm = this.meg.unmarshalDALC(this.tnnumchar);
    this.udstnl = this.udstnm.length;
  }

  byte[] getColumName()
  {
    return this.udscolnm;
  }

  byte[] getTypeName()
  {
    return this.udstnm;
  }

  byte[] getSchemaName()
  {
    return this.udssnm;
  }

  short getTypeCharLength()
  {
    return (short)this.tnnumchar[0];
  }

  short getColumNameByteLength()
  {
    return (short)this.numBytes[0];
  }

  short getSchemaCharLength()
  {
    return (short)this.snnumchar[0];
  }

  void print(int paramInt1, int paramInt2, int paramInt3)
  {
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8TTIuds
 * JD-Core Version:    0.6.0
 */