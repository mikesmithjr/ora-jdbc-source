package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CTTIiov extends T4CTTIMsg
{
  T4C8TTIrxh rxh;
  T4CTTIrxd rxd;
  byte bindtype = 0;
  byte[] iovector;
  int bindcnt = 0;
  int inbinds = 0;
  int outbinds = 0;
  static final byte BV_IN_V = 32;
  static final byte BV_OUT_V = 16;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIiov(T4CMAREngine paramT4CMAREngine, T4C8TTIrxh paramT4C8TTIrxh, T4CTTIrxd paramT4CTTIrxd)
    throws SQLException, IOException
  {
    setMarshalingEngine(paramT4CMAREngine);

    this.rxh = paramT4C8TTIrxh;
    this.rxd = paramT4CTTIrxd;
  }

  void init()
    throws SQLException, IOException
  {
  }

  Accessor[] processRXD(Accessor[] paramArrayOfAccessor, int paramInt1, byte[] paramArrayOfByte1, char[] paramArrayOfChar1, short[] paramArrayOfShort1, int paramInt2, DBConversion paramDBConversion, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, InputStream[][] paramArrayOfInputStream, byte[][][] paramArrayOfByte, OracleTypeADT[][] paramArrayOfOracleTypeADT, OracleStatement paramOracleStatement, byte[] paramArrayOfByte4, char[] paramArrayOfChar2, short[] paramArrayOfShort2)
    throws SQLException, IOException
  {
    if (paramArrayOfByte3 != null)
    {
      for (int i = 0; i < paramArrayOfByte3.length; i++)
      {
        if (((paramArrayOfByte3[i] & 0x10) != 0) && ((paramArrayOfAccessor == null) || (paramArrayOfAccessor.length <= i) || (paramArrayOfAccessor[i] == null)))
        {
          int j = paramInt2 + 3 + 10 * i;

          int k = paramArrayOfShort1[(j + 0)] & 0xFFFF;

          int m = k;

          if (k == 9) {
            k = 1;
          }
          Accessor localAccessor = paramOracleStatement.allocateAccessor(k, k, i, 0, 0, null, false);

          localAccessor.rowSpaceIndicator = null;

          if (paramArrayOfAccessor == null)
          {
            paramArrayOfAccessor = new Accessor[i + 1];
            paramArrayOfAccessor[i] = localAccessor;
          }
          else if (paramArrayOfAccessor.length <= i)
          {
            Accessor[] arrayOfAccessor = new Accessor[i + 1];

            arrayOfAccessor[i] = localAccessor;

            for (int n = 0; n < paramArrayOfAccessor.length; n++)
            {
              if (paramArrayOfAccessor[n] != null) {
                arrayOfAccessor[n] = paramArrayOfAccessor[n];
              }
            }
            paramArrayOfAccessor = arrayOfAccessor;
          }
          else
          {
            paramArrayOfAccessor[i] = localAccessor;
          }
        } else {
          if (((paramArrayOfByte3[i] & 0x10) != 0) || (paramArrayOfAccessor == null) || (i >= paramArrayOfAccessor.length) || (paramArrayOfAccessor[i] == null))
          {
            continue;
          }

          paramArrayOfAccessor[i].isUseLess = true;
        }

      }

    }

    return paramArrayOfAccessor;
  }

  void unmarshalV10()
    throws IOException, SQLException
  {
    this.rxh.unmarshalV10(this.rxd);

    this.bindcnt = this.rxh.numRqsts;

    this.iovector = new byte[this.bindcnt];

    for (int i = 0; i < this.bindcnt; i++)
    {
      if ((this.bindtype = this.meg.unmarshalSB1()) == 0)
      {
        DatabaseError.throwSqlException(401);
      }

      if ((this.bindtype & 0x20) > 0)
      {
        int tmp78_77 = i;
        byte[] tmp78_74 = this.iovector; tmp78_74[tmp78_77] = (byte)(tmp78_74[tmp78_77] | 0x20);
        this.inbinds += 1;
      }

      if ((this.bindtype & 0x10) <= 0)
        continue;
      int tmp110_109 = i;
      byte[] tmp110_106 = this.iovector; tmp110_106[tmp110_109] = (byte)(tmp110_106[tmp110_109] | 0x10);
      this.outbinds += 1;
    }
  }

  byte[] getIOVector()
  {
    return this.iovector;
  }

  boolean isIOVectorEmpty()
  {
    return this.iovector.length == 0;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIiov
 * JD-Core Version:    0.6.0
 */