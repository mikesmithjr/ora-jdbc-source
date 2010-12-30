package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CTTIdcb extends T4CTTIMsg
{
  T4C8TTIuds[] uds;
  int numuds;
  String[] colnames;
  int colOffset;
  byte[] ignoreBuff;
  StringBuffer colNameSB = null;

  OracleStatement statement = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIdcb(T4CMAREngine paramT4CMAREngine)
    throws IOException, SQLException
  {
    super(16);

    setMarshalingEngine(paramT4CMAREngine);

    this.ignoreBuff = new byte[40];
  }

  void init(OracleStatement paramOracleStatement, int paramInt)
  {
    this.statement = paramOracleStatement;
    this.colOffset = paramInt;
  }

  Accessor[] receive(Accessor[] paramArrayOfAccessor)
    throws SQLException, IOException
  {
    int i = this.meg.unmarshalUB1();

    if (this.ignoreBuff.length < i) {
      this.ignoreBuff = new byte[i];
    }
    this.meg.unmarshalNBytes(this.ignoreBuff, 0, i);

    int j = (int)this.meg.unmarshalUB4();

    paramArrayOfAccessor = receiveCommon(paramArrayOfAccessor, false);

    return paramArrayOfAccessor;
  }

  Accessor[] receiveFromRefCursor(Accessor[] paramArrayOfAccessor)
    throws SQLException, IOException
  {
    int i = this.meg.unmarshalUB1();
    int j = (int)this.meg.unmarshalUB4();

    paramArrayOfAccessor = receiveCommon(paramArrayOfAccessor, false);

    return paramArrayOfAccessor;
  }

  Accessor[] receiveCommon(Accessor[] paramArrayOfAccessor, boolean paramBoolean)
    throws SQLException, IOException
  {
    if (paramBoolean) {
      this.numuds = this.meg.unmarshalUB2();
    }
    else {
      this.numuds = (int)this.meg.unmarshalUB4();
      if (this.numuds > 0)
      {
        i = this.meg.unmarshalUB1();
      }
    }

    this.uds = new T4C8TTIuds[this.numuds];
    this.colnames = new String[this.numuds];
    int j;
    for (int i = 0; i < this.numuds; i++)
    {
      this.uds[i] = new T4C8TTIuds(this.meg);

      this.uds[i].unmarshal();

      if (this.meg.versionNumber >= 10000)
      {
        j = this.meg.unmarshalUB2();
      }

      this.colnames[i] = this.meg.conv.CharBytesToString(this.uds[i].getColumName(), this.uds[i].getColumNameByteLength());
    }

    if (!paramBoolean)
    {
      this.meg.unmarshalDALC();

      if (this.meg.versionNumber >= 10000)
      {
        i = (int)this.meg.unmarshalUB4();
        j = (int)this.meg.unmarshalUB4();
      }

    }

    if (this.statement.needToPrepareDefineBuffer)
    {
      if ((paramArrayOfAccessor == null) || (paramArrayOfAccessor.length != this.numuds + this.colOffset))
      {
        Accessor[] arrayOfAccessor = new Accessor[this.numuds + this.colOffset];

        if ((paramArrayOfAccessor != null) && (paramArrayOfAccessor.length == this.colOffset))
        {
          System.arraycopy(paramArrayOfAccessor, 0, arrayOfAccessor, 0, this.colOffset);
        }

        paramArrayOfAccessor = arrayOfAccessor;
      }

      fillupAccessors(paramArrayOfAccessor, this.colOffset);

      if (!paramBoolean)
      {
        this.statement.describedWithNames = true;
        this.statement.described = true;
        this.statement.numberOfDefinePositions = this.numuds;
        this.statement.accessors = paramArrayOfAccessor;

        if (this.statement.connection.useFetchSizeWithLongColumn)
        {
          this.statement.prepareAccessors();

          this.statement.allocateTmpByteArray();
        }
      }
    }

    return paramArrayOfAccessor;
  }

  void fillupAccessors(Accessor[] paramArrayOfAccessor, int paramInt)
    throws SQLException
  {
    int[] arrayOfInt1 = this.statement.definedColumnType;
    int[] arrayOfInt2 = this.statement.definedColumnSize;
    int[] arrayOfInt3 = this.statement.definedColumnFormOfUse;

    for (int k = 0; k < this.numuds; k++)
    {
      int m = 0;
      int n = 0;
      int i1 = 0;

      if ((arrayOfInt1 != null) && (arrayOfInt1.length > paramInt + k) && (arrayOfInt1[(paramInt + k)] != 0))
      {
        m = arrayOfInt1[(paramInt + k)];
      }
      if ((arrayOfInt2 != null) && (arrayOfInt2.length > paramInt + k) && (arrayOfInt2[(paramInt + k)] > 0))
      {
        n = arrayOfInt2[(paramInt + k)];
      }
      if ((arrayOfInt3 != null) && (arrayOfInt3.length > paramInt + k) && (arrayOfInt3[(paramInt + k)] > 0))
      {
        i1 = arrayOfInt3[(paramInt + k)];
      }
      T4C8TTIuds localT4C8TTIuds = this.uds[k];

      String str1 = this.meg.conv.CharBytesToString(this.uds[k].getTypeName(), this.uds[k].getTypeCharLength());

      String str2 = this.meg.conv.CharBytesToString(this.uds[k].getSchemaName(), this.uds[k].getSchemaCharLength());

      String str3 = str2 + "." + str1;
      int i = localT4C8TTIuds.udsoac.oacmxl;
      int j;
      switch (localT4C8TTIuds.udsoac.oacdty)
      {
      case 96:
        if ((localT4C8TTIuds.udsoac.oacmxlc != 0) && (localT4C8TTIuds.udsoac.oacmxlc < i))
        {
          i = 2 * localT4C8TTIuds.udsoac.oacmxlc;
        }

        j = i;

        if (((m == 1) || (m == 12)) && (n > 0) && (n < i))
        {
          j = n;
        }
        paramArrayOfAccessor[(paramInt + k)] = new T4CCharAccessor(this.statement, j, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, i, m, n, this.meg);

        if (((localT4C8TTIuds.udsoac.oacfl2 & 0x1000) != 4096) && (localT4C8TTIuds.udsoac.oacmxlc == 0))
          break;
        paramArrayOfAccessor[(this.colOffset + k)].setDisplaySize(localT4C8TTIuds.udsoac.oacmxlc); break;
      case 2:
        paramArrayOfAccessor[(paramInt + k)] = new T4CNumberAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 1:
        if ((localT4C8TTIuds.udsoac.oacmxlc != 0) && (localT4C8TTIuds.udsoac.oacmxlc < i)) {
          i = 2 * localT4C8TTIuds.udsoac.oacmxlc;
        }
        j = i;

        if (((m == 1) || (m == 12)) && (n > 0) && (n < i))
        {
          j = n;
        }
        paramArrayOfAccessor[(paramInt + k)] = new T4CVarcharAccessor(this.statement, j, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, i, m, n, this.meg);

        if (((localT4C8TTIuds.udsoac.oacfl2 & 0x1000) != 4096) && (localT4C8TTIuds.udsoac.oacmxlc == 0))
          break;
        paramArrayOfAccessor[(this.colOffset + k)].setDisplaySize(localT4C8TTIuds.udsoac.oacmxlc); break;
      case 8:
        if (((m == 1) || (m == 12)) && (this.meg.versionNumber >= 9000) && (n < 4001))
        {
          if (n > 0) {
            j = n;
          }
          else
          {
            j = 4000;
          }

          i = -1;
          paramArrayOfAccessor[(paramInt + k)] = new T4CVarcharAccessor(this.statement, j, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, i, m, n, this.meg);

          paramArrayOfAccessor[(paramInt + k)].describeType = 8;
        }
        else
        {
          i = 0;

          paramArrayOfAccessor[(paramInt + k)] = new T4CLongAccessor(this.statement, paramInt + k + 1, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);
        }

        break;
      case 6:
        paramArrayOfAccessor[(paramInt + k)] = new T4CVarnumAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 100:
        paramArrayOfAccessor[(paramInt + k)] = new T4CBinaryFloatAccessor(this.statement, 4, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 101:
        paramArrayOfAccessor[(paramInt + k)] = new T4CBinaryDoubleAccessor(this.statement, 8, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 23:
        paramArrayOfAccessor[(paramInt + k)] = new T4CRawAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 24:
        if ((m == -2) && (n < 2001) && (this.meg.versionNumber >= 9000))
        {
          i = -1;
          paramArrayOfAccessor[(paramInt + k)] = new T4CRawAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

          paramArrayOfAccessor[(paramInt + k)].describeType = 24;
        }
        else {
          paramArrayOfAccessor[(paramInt + k)] = new T4CLongRawAccessor(this.statement, paramInt + k + 1, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);
        }

        break;
      case 104:
      case 208:
        paramArrayOfAccessor[(paramInt + k)] = new T4CRowidAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        if (localT4C8TTIuds.udsoac.oacdty != 208) break;
        paramArrayOfAccessor[k].describeType = localT4C8TTIuds.udsoac.oacdty; break;
      case 102:
        paramArrayOfAccessor[(paramInt + k)] = new T4CResultSetAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 12:
        paramArrayOfAccessor[(paramInt + k)] = new T4CDateAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 113:
        if ((m == -4) && (this.meg.versionNumber >= 9000))
        {
          paramArrayOfAccessor[(paramInt + k)] = new T4CLongRawAccessor(this.statement, paramInt + k + 1, 2147483647, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

          paramArrayOfAccessor[(paramInt + k)].describeType = 113;
        }
        else if ((m == -3) && (this.meg.versionNumber >= 9000))
        {
          paramArrayOfAccessor[(paramInt + k)] = new T4CRawAccessor(this.statement, 4000, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

          paramArrayOfAccessor[(paramInt + k)].describeType = 113;
        }
        else
        {
          paramArrayOfAccessor[(paramInt + k)] = new T4CBlobAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);
        }

        break;
      case 112:
        short s = 1;
        if (i1 != 0) {
          s = (short)i1;
        }
        if ((m == -1) && (this.meg.versionNumber >= 9000))
        {
          i = 0;
          paramArrayOfAccessor[(paramInt + k)] = new T4CLongAccessor(this.statement, paramInt + k + 1, 2147483647, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, s, m, n, this.meg);

          paramArrayOfAccessor[(paramInt + k)].describeType = 112;
        }
        else if (((m == 12) || (m == 1)) && (this.meg.versionNumber >= 9000))
        {
          j = 4000;
          if ((n > 0) && (n < j))
          {
            j = n;
          }
          paramArrayOfAccessor[(paramInt + k)] = new T4CVarcharAccessor(this.statement, j, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, s, 4000, m, n, this.meg);

          paramArrayOfAccessor[(paramInt + k)].describeType = 112;
        }
        else {
          paramArrayOfAccessor[(paramInt + k)] = new T4CClobAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);
        }

        break;
      case 114:
        paramArrayOfAccessor[(paramInt + k)] = new T4CBfileAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 109:
        paramArrayOfAccessor[(paramInt + k)] = new T4CNamedTypeAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, str3, m, n, this.meg);

        break;
      case 111:
        paramArrayOfAccessor[(paramInt + k)] = new T4CRefTypeAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, str3, m, n, this.meg);

        break;
      case 180:
        paramArrayOfAccessor[(paramInt + k)] = new T4CTimestampAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 181:
        paramArrayOfAccessor[(paramInt + k)] = new T4CTimestamptzAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 231:
        paramArrayOfAccessor[(paramInt + k)] = new T4CTimestampltzAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 182:
        paramArrayOfAccessor[(paramInt + k)] = new T4CIntervalymAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      case 183:
        paramArrayOfAccessor[(paramInt + k)] = new T4CIntervaldsAccessor(this.statement, i, localT4C8TTIuds.udsnull, localT4C8TTIuds.udsoac.oacflg, localT4C8TTIuds.udsoac.oacpre, localT4C8TTIuds.udsoac.oacscl, localT4C8TTIuds.udsoac.oacfl2, localT4C8TTIuds.udsoac.oacmal, localT4C8TTIuds.udsoac.formOfUse, m, n, this.meg);

        break;
      default:
        paramArrayOfAccessor[(paramInt + k)] = null;
      }

      if (localT4C8TTIuds.udsoac.oactoid.length > 0)
      {
        paramArrayOfAccessor[(paramInt + k)].internalOtype = new OracleTypeADT(localT4C8TTIuds.udsoac.oactoid, localT4C8TTIuds.udsoac.oacvsn, localT4C8TTIuds.udsoac.ncs, localT4C8TTIuds.udsoac.formOfUse, str2 + "." + str1);
      }
      else
      {
        paramArrayOfAccessor[(paramInt + k)].internalOtype = null;
      }

      paramArrayOfAccessor[(paramInt + k)].columnName = this.colnames[k];

      if (this.uds[k].udsoac.oacmxl == 0) {
        paramArrayOfAccessor[k].isNullByDescribe = true;
      }

    }

    this.colNameSB = null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIdcb
 * JD-Core Version:    0.6.0
 */