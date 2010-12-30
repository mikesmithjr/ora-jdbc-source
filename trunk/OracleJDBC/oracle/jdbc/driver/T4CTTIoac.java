package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CTTIoac
{
  static final short UACFIND = 1;
  static final short UACFALN = 2;
  static final short UACFRCP = 4;
  static final short UACFBBV = 8;
  static final short UACFNCP = 16;
  static final short UACFBLP = 32;
  static final short UACFARR = 64;
  static final short UACFIGN = 128;
  static final short UACFNSCL = 1;
  static final short UACFBUC = 2;
  static final short UACFSKP = 4;
  static final short UACFCHRCNT = 8;
  static final short UACFNOADJ = 16;
  static final short UACFCUS = 4096;
  static final byte[] NO_BYTES = new byte[0];
  boolean isStream;
  int ncs;
  short formOfUse;
  static int maxBindArrayLength;
  T4CMAREngine meg;
  short oacdty;
  short oacflg;
  short oacpre;
  short oacscl;
  int oacmxl;
  int oacmxlc = 0;
  int oacmal;
  int oacfl2;
  byte[] oactoid;
  int oactoidl;
  int oacvsn;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIoac(T4CMAREngine paramT4CMAREngine)
  {
    this.meg = paramT4CMAREngine;
  }

  T4CTTIoac(T4CTTIoac paramT4CTTIoac)
  {
    this.meg = paramT4CTTIoac.meg;
    this.isStream = paramT4CTTIoac.isStream;
    this.ncs = paramT4CTTIoac.ncs;
    this.formOfUse = paramT4CTTIoac.formOfUse;
    this.oacdty = paramT4CTTIoac.oacdty;
    this.oacflg = paramT4CTTIoac.oacflg;
    this.oacpre = paramT4CTTIoac.oacpre;
    this.oacscl = paramT4CTTIoac.oacscl;
    this.oacmxl = paramT4CTTIoac.oacmxl;
    this.oacmal = paramT4CTTIoac.oacmal;
    this.oacfl2 = paramT4CTTIoac.oacfl2;
    this.oactoid = paramT4CTTIoac.oactoid;
    this.oactoidl = paramT4CTTIoac.oactoidl;
    this.oacvsn = paramT4CTTIoac.oacvsn;
  }

  boolean isOldSufficient(T4CTTIoac paramT4CTTIoac)
  {
    int i = 0;

    if ((this.oactoidl != 0) || (paramT4CTTIoac.oactoidl != 0)) {
      return false;
    }
    if ((this.isStream == paramT4CTTIoac.isStream) && (this.ncs == paramT4CTTIoac.ncs) && (this.formOfUse == paramT4CTTIoac.formOfUse) && (this.oacdty == paramT4CTTIoac.oacdty) && (this.oacflg == paramT4CTTIoac.oacflg) && (this.oacpre == paramT4CTTIoac.oacpre) && (this.oacscl == paramT4CTTIoac.oacscl) && ((this.oacmxl == paramT4CTTIoac.oacmxl) || ((paramT4CTTIoac.oacmxl > this.oacmxl) && (paramT4CTTIoac.oacmxl < 4000))) && (this.oacmxlc == paramT4CTTIoac.oacmxlc) && (this.oacmal == paramT4CTTIoac.oacmal) && (this.oacfl2 == paramT4CTTIoac.oacfl2) && (this.oacvsn == paramT4CTTIoac.oacvsn))
    {
      i = 1;
    }
    return i;
  }

  boolean isNType()
  {
    return this.formOfUse == 2;
  }

  void unmarshal()
    throws IOException, SQLException
  {
    this.oacdty = this.meg.unmarshalUB1();
    this.oacflg = this.meg.unmarshalUB1();
    this.oacpre = this.meg.unmarshalUB1();

    if ((this.oacdty == 2) || (this.oacdty == 180) || (this.oacdty == 181) || (this.oacdty == 231) || (this.oacdty == 183))
    {
      this.oacscl = (short)this.meg.unmarshalUB2();
    }
    else this.oacscl = this.meg.unmarshalUB1();

    this.oacmxl = this.meg.unmarshalSB4();
    this.oacmal = this.meg.unmarshalSB4();
    this.oacfl2 = this.meg.unmarshalSB4();

    if (this.oacmxl > 0)
    {
      switch (this.oacdty)
      {
      case 2:
        this.oacmxl = 22;

        break;
      case 12:
        this.oacmxl = 7;

        break;
      case 181:
        this.oacmxl = 13;
      }

    }

    if (this.oacdty == 11) {
      this.oacdty = 104;
    }
    this.oactoid = this.meg.unmarshalDALC();
    this.oactoidl = (this.oactoid == null ? 0 : this.oactoid.length);
    this.oacvsn = this.meg.unmarshalUB2();
    this.ncs = this.meg.unmarshalUB2();
    this.formOfUse = this.meg.unmarshalUB1();

    if (this.meg.versionNumber >= 9000)
    {
      this.oacmxlc = (int)this.meg.unmarshalUB4();
    }
  }

  void init(NamedTypeAccessor paramNamedTypeAccessor)
  {
    this.oacdty = (short)paramNamedTypeAccessor.internalType;
    this.oacflg = 3;
    this.oacpre = 0;
    this.oacscl = 0;
    this.oacmxl = paramNamedTypeAccessor.internalTypeMaxLength;
    this.oacmal = 0;
    this.oacfl2 = 0;
    this.isStream = paramNamedTypeAccessor.isStream;

    OracleTypeADT localOracleTypeADT = (OracleTypeADT)paramNamedTypeAccessor.internalOtype;

    if (localOracleTypeADT != null)
    {
      this.oactoid = localOracleTypeADT.getTOID();
      this.oacvsn = localOracleTypeADT.getTypeVersion();
      this.ncs = localOracleTypeADT.getCharSet();
      this.formOfUse = (short)localOracleTypeADT.getCharSetForm();
    }
    else
    {
      this.oactoid = NO_BYTES;
      this.oactoidl = this.oactoid.length;
      this.oacvsn = 0;
      this.formOfUse = paramNamedTypeAccessor.formOfUse;

      if (isNType())
        this.ncs = this.meg.conv.getNCharSetId();
      else {
        this.ncs = this.meg.conv.getServerCharSetId();
      }
    }
    if (this.oacdty == 102)
      this.oacmxl = 1;
  }

  void init(PlsqlIndexTableAccessor paramPlsqlIndexTableAccessor)
  {
    initIbt((short)paramPlsqlIndexTableAccessor.elementInternalType, paramPlsqlIndexTableAccessor.maxNumberOfElements, paramPlsqlIndexTableAccessor.elementMaxLen);
  }

  void initIbt(short paramShort, int paramInt1, int paramInt2)
  {
    this.oacflg = 67;
    this.oacpre = 0;
    this.oacscl = 0;
    this.oacmal = paramInt1;
    this.oacfl2 = 0;
    this.oacmxl = paramInt2;
    this.oactoid = null;
    this.oacvsn = 0;
    this.ncs = 0;
    this.formOfUse = 0;

    if ((paramShort == 9) || (paramShort == 96) || (paramShort == 1))
    {
      if (this.oacdty != 96) {
        this.oacdty = 1;
      }
      this.oacfl2 = 16;
    }
    else {
      this.oacdty = 2;
    }
  }

  void init(OracleTypeADT paramOracleTypeADT, int paramInt1, int paramInt2)
  {
    this.oacdty = (short)paramInt1;
    this.oacflg = 3;
    this.oacpre = 0;
    this.oacscl = 0;
    this.oacmxl = paramInt2;
    this.oacmal = 0;
    this.oacfl2 = 0;
    this.isStream = false;

    this.oactoid = paramOracleTypeADT.getTOID();
    this.oacvsn = paramOracleTypeADT.getTypeVersion();
    this.ncs = 2;
    this.formOfUse = (short)paramOracleTypeADT.getCharSetForm();

    if (this.oacdty == 102)
      this.oacmxl = 1;
  }

  void init(short paramShort1, int paramInt, short paramShort2, short paramShort3, short paramShort4)
    throws IOException, SQLException
  {
    this.oacflg = 3;
    this.oacpre = 0;
    this.oacscl = 0;
    this.oacmal = 0;
    this.oacfl2 = 0;
    this.oacdty = paramShort1;
    this.oacmxl = paramInt;

    if ((this.oacdty == 96) || (this.oacdty == 9) || (this.oacdty == 1))
    {
      if (this.oacdty != 96) {
        this.oacdty = 1;
      }
      this.oacfl2 = 16;
    }
    else if (this.oacdty == 104)
    {
      this.oacdty = 11;
    }
    else if (this.oacdty == 102) {
      this.oacmxl = 1;
    }
    this.oactoid = NO_BYTES;
    this.oactoidl = 0;
    this.oacvsn = 0;

    this.formOfUse = paramShort4;

    this.ncs = paramShort2;

    if (isNType())
      this.ncs = paramShort3;
  }

  void marshal()
    throws IOException, SQLException
  {
    this.meg.marshalUB1(this.oacdty);
    this.meg.marshalUB1(this.oacflg);
    this.meg.marshalUB1(this.oacpre);
    this.meg.marshalUB1(this.oacscl);

    this.meg.marshalUB4(this.oacmxl);

    this.meg.marshalSB4(this.oacmal);
    this.meg.marshalSB4(this.oacfl2);

    this.meg.marshalDALC(this.oactoid);

    this.meg.marshalUB2(this.oacvsn);
    this.meg.marshalUB2(this.ncs);
    this.meg.marshalUB1(this.formOfUse);

    if (this.meg.versionNumber >= 9000)
    {
      this.meg.marshalUB4(0L);
    }
  }

  boolean isStream()
    throws SQLException
  {
    return this.isStream;
  }

  void print(int paramInt1, int paramInt2, int paramInt3)
  {
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIoac
 * JD-Core Version:    0.6.0
 */