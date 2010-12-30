package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T4C8TTILobd extends T4CTTIMsg
{
  static final int LOBD_STATE0 = 0;
  static final int LOBD_STATE1 = 1;
  static final int LOBD_STATE2 = 2;
  static final int LOBD_STATE3 = 3;
  static final int LOBD_STATE_EXIT = 4;
  static final short TTCG_LNG = 254;
  static final short LOBDATALENGTH = 252;
  static byte[] ucs2Char = new byte[2];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4C8TTILobd(T4CMAREngine paramT4CMAREngine)
  {
    super(14);

    setMarshalingEngine(paramT4CMAREngine);
  }

  void marshalLobData(byte[] paramArrayOfByte, long paramLong1, long paramLong2)
    throws SQLException, IOException
  {
    long l1 = paramLong2;
    int i = 0;

    marshalTTCcode();

    if (l1 > 252L)
    {
      i = 1;

      this.meg.marshalUB1(254);
    }

    long l2 = 0L;

    for (; l1 > 252L; l1 -= 252L)
    {
      this.meg.marshalUB1(252);
      this.meg.marshalB1Array(paramArrayOfByte, (int)(paramLong1 + l2 * 252L), 252);

      l2 += 1L;
    }

    if (l1 > 0L)
    {
      this.meg.marshalUB1((short)(int)l1);
      this.meg.marshalB1Array(paramArrayOfByte, (int)(paramLong1 + l2 * 252L), (int)l1);
    }

    if (i == 1)
      this.meg.marshalUB1(0);
  }

  void marshalLobDataUB2(byte[] paramArrayOfByte, long paramLong1, long paramLong2)
    throws SQLException, IOException
  {
    long l1 = paramLong2;
    int i = 0;

    marshalTTCcode();

    if (l1 > 84L)
    {
      i = 1;

      this.meg.marshalUB1(254);
    }

    long l2 = 0L;

    for (; l1 > 84L; l1 -= 84L)
    {
      this.meg.marshalUB1(252);

      for (int j = 0; j < 84; j++)
      {
        this.meg.marshalUB1(2);

        this.meg.marshalB1Array(paramArrayOfByte, (int)(paramLong1 + l2 * 168L + j * 2), 2);
      }
      l2 += 1L;
    }

    if (l1 > 0L)
    {
      long l3 = l1 * 3L;

      this.meg.marshalUB1((short)(int)l3);

      for (int k = 0; k < l1; k++)
      {
        this.meg.marshalUB1(2);

        this.meg.marshalB1Array(paramArrayOfByte, (int)(paramLong1 + l2 * 168L + k * 2), 2);
      }

    }

    if (i == 1)
      this.meg.marshalUB1(0);
  }

  long unmarshalLobData(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    long l1 = 0L;
    long l2 = 0L;
    int i = 0;

    int j = 0;

    while (j != 4)
    {
      switch (j)
      {
      case 0:
        i = this.meg.unmarshalUB1();

        if (i == 254) {
          j = 2; continue;
        }

        j = 1;

        break;
      case 1:
        this.meg.getNBytes(paramArrayOfByte, (int)l2, i);

        l1 += i;
        j = 4;

        break;
      case 2:
        i = this.meg.unmarshalUB1();

        if (i > 0) {
          j = 3; continue;
        }

        j = 4;

        break;
      case 3:
        this.meg.getNBytes(paramArrayOfByte, (int)l2, i);

        l1 += i;

        l2 += i;

        j = 2;

        continue;
      }

    }

    return l1;
  }

  long unmarshalClobUB2(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    long l1 = 0L;
    long l2 = 0L;
    int i = 0;
    int j = 0;
    int k = 0;

    int m = 0;

    while (m != 4)
    {
      switch (m)
      {
      case 0:
        i = this.meg.unmarshalUB1();

        if (i == 254) {
          m = 2; continue;
        }

        m = 1;

        break;
      case 1:
        for (j = 0; j < i; l2 += 2L)
        {
          k = this.meg.unmarshalUCS2(paramArrayOfByte, l2);

          j += k;
        }

        l1 += i;
        m = 4;

        break;
      case 2:
        i = this.meg.unmarshalUB1();

        if (i > 0) {
          m = 3; continue;
        }

        m = 4;

        break;
      case 3:
        for (j = 0; j < i; l2 += 2L)
        {
          k = this.meg.unmarshalUCS2(paramArrayOfByte, l2);

          j += k;
        }

        l1 += i;

        m = 2;

        continue;
      }

    }

    return l1;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8TTILobd
 * JD-Core Version:    0.6.0
 */