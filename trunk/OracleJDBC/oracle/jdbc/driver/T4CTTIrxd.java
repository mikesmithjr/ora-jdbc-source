package oracle.jdbc.driver;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.BitSet;
import oracle.jdbc.oracore.OracleTypeADT;

class T4CTTIrxd extends T4CTTIMsg
{
  static final byte[] NO_BYTES = new byte[0];
  byte[] buffer;
  byte[] bufferCHAR;
  BitSet bvcColSent = null;
  int nbOfColumns = 0;
  boolean bvcFound = false;
  boolean isFirstCol;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:54_PDT_2005";

  T4CTTIrxd(T4CMAREngine paramT4CMAREngine)
  {
    super(7);

    setMarshalingEngine(paramT4CMAREngine);

    this.isFirstCol = true;
  }

  void init()
  {
    this.isFirstCol = true;
  }

  void setNumberOfColumns(int paramInt)
  {
    this.nbOfColumns = paramInt;
    this.bvcFound = false;

    this.bvcColSent = new BitSet(this.nbOfColumns);
  }

  void unmarshalBVC(int paramInt) throws SQLException, IOException
  {
    int i = 0;

    for (int j = 0; j < this.bvcColSent.length(); j++) {
      this.bvcColSent.clear(j);
    }

    j = this.nbOfColumns / 8 + (this.nbOfColumns % 8 != 0 ? 1 : 0);

    for (int k = 0; k < j; k++)
    {
      int m = (byte)(this.meg.unmarshalUB1() & 0xFF);

      for (int n = 0; n < 8; n++)
      {
        if ((m & 1 << n) == 0)
          continue;
        this.bvcColSent.set(k * 8 + n);

        i++;
      }

    }

    if (i != paramInt)
    {
      DatabaseError.throwSqlException(-1, "INTERNAL ERROR: oracle.jdbc.driver.T4CTTIrxd.unmarshalBVC: bits missing in vector");
    }

    this.bvcFound = true;
  }

  void readBitVector(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    for (int i = 0; i < this.bvcColSent.length(); i++) {
      this.bvcColSent.clear(i);
    }
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length == 0)) {
      this.bvcFound = false;
    }
    else {
      for (i = 0; i < paramArrayOfByte.length; i++) {
        int j = paramArrayOfByte[i];
        for (int k = 0; k < 8; k++) {
          if ((j & 1 << k) != 0)
            this.bvcColSent.set(i * 8 + k);
        }
      }
      this.bvcFound = true;
    }
  }

  void marshal(byte[] paramArrayOfByte1, char[] paramArrayOfChar1, short[] paramArrayOfShort1, int paramInt1, byte[] paramArrayOfByte2, DBConversion paramDBConversion, InputStream[] paramArrayOfInputStream, byte[][] paramArrayOfByte, OracleTypeADT[] paramArrayOfOracleTypeADT, byte[] paramArrayOfByte3, char[] paramArrayOfChar2, short[] paramArrayOfShort2, byte[] paramArrayOfByte4, int paramInt2, int[] paramArrayOfInt1, boolean paramBoolean, int[] paramArrayOfInt2)
    throws IOException, SQLException
  {
    marshalTTCcode();

    int i = paramArrayOfShort1[(paramInt1 + 0)] & 0xFFFF;

    int i12 = 0;
    Object localObject1 = new int[3];

    int i13 = 0;
    int i15;
    int j;
    int i9;
    int k;
    int i11;
    int i23;
    int i6;
    int i8;
    int i25;
    int i7;
    int i3;
    int i5;
    int i18;
    int i20;
    for (int i14 = 0; i14 < i; i14++)
    {
      i15 = 0;

      j = paramInt1 + 3 + 10 * i14;

      i9 = paramArrayOfShort1[(j + 0)] & 0xFFFF;

      if ((paramArrayOfByte4 != null) && ((paramArrayOfByte4[i14] & 0x20) == 0))
      {
        if (i9 == 998) {
          i13++;
        }
      }
      else
      {
        k = ((paramArrayOfShort1[(j + 7)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 8)] & 0xFFFF) + paramInt2;

        i11 = ((paramArrayOfShort1[(j + 5)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 6)] & 0xFFFF) + paramInt2;

        int m = paramArrayOfShort1[k] & 0xFFFF;

        int i10 = paramArrayOfShort1[i11];

        if (i9 == 116)
        {
          this.meg.marshalUB1(1);
          this.meg.marshalUB1(0);
        }
        else
        {
          if (i9 == 994)
          {
            i10 = -1;
            int i16 = paramArrayOfInt2[(3 + i14 * 3 + 0)];

            if (i16 == 109) {
              i15 = 1;
            }
          }

          if (i10 == -1)
          {
            if ((i9 == 109) || (i15 != 0))
            {
              this.meg.marshalDALC(NO_BYTES);
              this.meg.marshalDALC(NO_BYTES);
              this.meg.marshalDALC(NO_BYTES);
              this.meg.marshalUB2(0);
              this.meg.marshalUB4(0L);
              this.meg.marshalUB2(1);

              continue;
            }
            if (i9 == 998)
            {
              i13++;
              this.meg.marshalUB4(0L);
              continue;
            }
            if ((i9 == 112) || (i9 == 113) || (i9 == 114))
            {
              this.meg.marshalUB4(0L);
              continue;
            }
            if ((i9 != 8) && (i9 != 24))
            {
              if (paramArrayOfInt1[i14] == 0) continue;
              this.meg.marshalUB1(0); continue;
            }

          }

          if ((i9 == 8) || (i9 == 24))
          {
            if (i12 >= localObject1.length)
            {
              int[] arrayOfInt = new int[localObject1.length << 1];

              System.arraycopy(localObject1, 0, arrayOfInt, 0, localObject1.length);

              localObject1 = arrayOfInt;
            }

            localObject1[(i12++)] = i14;
          }
          else
          {
            int i19;
            int i26;
            if (i9 == 998)
            {
              int i17 = (paramArrayOfShort2[(6 + i13 * 8 + 4)] & 0xFFFF) << 16 & 0xFFFF000 | paramArrayOfShort2[(6 + i13 * 8 + 5)] & 0xFFFF;

              i19 = (paramArrayOfShort2[(6 + i13 * 8 + 6)] & 0xFFFF) << 16 & 0xFFFF000 | paramArrayOfShort2[(6 + i13 * 8 + 7)] & 0xFFFF;

              int i21 = paramArrayOfShort2[(6 + i13 * 8)] & 0xFFFF;
              i23 = paramArrayOfShort2[(6 + i13 * 8 + 1)] & 0xFFFF;

              this.meg.marshalUB4(i17);

              for (int i24 = 0; i24 < i17; i24++)
              {
                i26 = i19 + i24 * i23;

                if (i21 == 9)
                {
                  i6 = paramArrayOfChar2[i26] / '\002';
                  i8 = 0;
                  i8 = paramDBConversion.javaCharsToCHARBytes(paramArrayOfChar2, i26 + 1, paramArrayOfByte2, 0, i6);

                  this.meg.marshalCLR(paramArrayOfByte2, i8);
                }
                else
                {
                  m = paramArrayOfByte3[i26];

                  if (m < 1)
                    this.meg.marshalUB1(0);
                  else {
                    this.meg.marshalCLR(paramArrayOfByte3, i26 + 1, m);
                  }
                }
              }
              i13++;
            }
            else
            {
              int i2 = paramArrayOfShort1[(j + 1)] & 0xFFFF;
              Object localObject2;
              int n;
              if (i2 != 0)
              {
                int i4 = ((paramArrayOfShort1[(j + 3)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 4)] & 0xFFFF) + i2 * paramInt2;

                if (i9 == 6)
                {
                  i4++;
                  m--;
                }
                else if (i9 == 9)
                {
                  i4 += 2;

                  m -= 2;
                }
                else if ((i9 == 114) || (i9 == 113) || (i9 == 112))
                {
                  this.meg.marshalUB4(m);
                }

                if ((i9 == 109) || (i9 == 111))
                {
                  if (paramArrayOfByte == null)
                  {
                    DatabaseError.throwSqlException(-1, "INTERNAL ERROR: oracle.jdbc.driver.T4CTTIrxd.marshal: parameterDatum is null");
                  }

                  localObject2 = paramArrayOfByte[i14];

                  n = localObject2 == null ? 0 : localObject2.length;

                  if (i9 == 109)
                  {
                    this.meg.marshalDALC(NO_BYTES);
                    this.meg.marshalDALC(NO_BYTES);
                    this.meg.marshalDALC(NO_BYTES);
                    this.meg.marshalUB2(0);

                    this.meg.marshalUB4(n);
                    this.meg.marshalUB2(1);
                  }

                  if (n > 0)
                    this.meg.marshalCLR(localObject2, 0, n);
                }
                else if (i9 == 104)
                {
                  i4 += 2;

                  localObject2 = T4CRowidAccessor.stringToRowid(paramArrayOfByte1, i4, 18);

                  i19 = 14;
                  long l1 = localObject2[0];
                  i25 = (int)localObject2[1];
                  i26 = 0;
                  long l2 = localObject2[2];
                  int i27 = (int)localObject2[3];

                  if ((l1 == 0L) && (i25 == 0) && (l2 == 0L) && (i27 == 0))
                  {
                    this.meg.marshalUB1(0);
                  }
                  else
                  {
                    this.meg.marshalUB1(i19);
                    this.meg.marshalUB4(l1);
                    this.meg.marshalUB2(i25);
                    this.meg.marshalUB1(i26);
                    this.meg.marshalUB4(l2);
                    this.meg.marshalUB2(i27);
                  }

                }
                else if (n < 1) {
                  this.meg.marshalUB1(0);
                } else {
                  this.meg.marshalCLR(paramArrayOfByte1, i4, n);
                }

              }
              else
              {
                i7 = paramArrayOfShort1[(j + 9)] & 0xFFFF;

                if ((!paramBoolean) && (paramArrayOfInt1 != null) && (paramArrayOfInt1.length > i14) && (paramArrayOfInt1[i14] > 4000))
                {
                  if (i12 >= localObject1.length)
                  {
                    localObject2 = new int[localObject1.length << 1];

                    System.arraycopy(localObject1, 0, localObject2, 0, localObject1.length);

                    localObject1 = localObject2;
                  }

                  localObject1[(i12++)] = i14;
                }
                else
                {
                  i3 = paramArrayOfShort1[(j + 2)] & 0xFFFF;

                  i5 = ((paramArrayOfShort1[(j + 3)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 4)] & 0xFFFF) + i3 * paramInt2 + 1;

                  if (i9 == 996)
                  {
                    i18 = paramArrayOfChar1[(i5 - 1)];

                    if ((this.bufferCHAR == null) || (this.bufferCHAR.length < i18)) {
                      this.bufferCHAR = new byte[i18];
                    }
                    for (i20 = 0; i20 < i18; i20++)
                    {
                      this.bufferCHAR[i20] = (byte)((paramArrayOfChar1[(i5 + i20 / 2)] & 0xFF00) >> '\b' & 0xFF);

                      if (i20 >= i18 - 1)
                        continue;
                      this.bufferCHAR[(i20 + 1)] = (byte)(paramArrayOfChar1[(i5 + i20 / 2)] & 0xFF & 0xFF);

                      i20++;
                    }

                    this.meg.marshalCLR(this.bufferCHAR, i18);

                    if (this.bufferCHAR.length > 4000) {
                      this.bufferCHAR = null;
                    }

                  }
                  else
                  {
                    if (i9 == 96)
                    {
                      i6 = n / 2;
                      i5--;
                    }
                    else
                    {
                      i6 = (n - 2) / 2;
                    }

                    i8 = 0;

                    if (i7 == 2)
                    {
                      i8 = paramDBConversion.javaCharsToNCHARBytes(paramArrayOfChar1, i5, paramArrayOfByte2, 0, i6);
                    }
                    else
                    {
                      i8 = paramDBConversion.javaCharsToCHARBytes(paramArrayOfChar1, i5, paramArrayOfByte2, 0, i6);
                    }

                    this.meg.marshalCLR(paramArrayOfByte2, i8);
                  }
                }
              }
            }
          }
        }
      }
    }

    if (i12 > 0)
    {
      for (i14 = 0; i14 < i12; i14++)
      {
        i15 = localObject1[i14];

        j = paramInt1 + 3 + 10 * i15;

        i9 = paramArrayOfShort1[(j + 0)] & 0xFFFF;

        k = ((paramArrayOfShort1[(j + 7)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 8)] & 0xFFFF) + paramInt2;

        i11 = ((paramArrayOfShort1[(j + 5)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 6)] & 0xFFFF) + paramInt2;

        int i1 = paramArrayOfShort1[k] & 0xFFFF;

        i3 = paramArrayOfShort1[(j + 2)] & 0xFFFF;

        i5 = ((paramArrayOfShort1[(j + 3)] & 0xFFFF) << 16) + (paramArrayOfShort1[(j + 4)] & 0xFFFF) + i3 * paramInt2 + 1;

        if (i9 == 996)
        {
          i18 = paramArrayOfChar1[(i5 - 1)];

          if ((this.bufferCHAR == null) || (this.bufferCHAR.length < i18)) {
            this.bufferCHAR = new byte[i18];
          }
          for (i20 = 0; i20 < i18; i20++)
          {
            this.bufferCHAR[i20] = (byte)((paramArrayOfChar1[(i5 + i20 / 2)] & 0xFF00) >> '\b' & 0xFF);

            if (i20 >= i18 - 1)
              continue;
            this.bufferCHAR[(i20 + 1)] = (byte)(paramArrayOfChar1[(i5 + i20 / 2)] & 0xFF & 0xFF);

            i20++;
          }

          this.meg.marshalCLR(this.bufferCHAR, i18);

          if (this.bufferCHAR.length > 4000)
            this.bufferCHAR = null;
        }
        else if ((i9 != 8) && (i9 != 24))
        {
          if (i9 == 96)
          {
            i6 = i1 / 2;
            i5--;
          }
          else
          {
            i6 = (i1 - 2) / 2;
          }

          i7 = paramArrayOfShort1[(j + 9)] & 0xFFFF;

          i8 = 0;

          if (i7 == 2)
          {
            i8 = paramDBConversion.javaCharsToNCHARBytes(paramArrayOfChar1, i5, paramArrayOfByte2, 0, i6);
          }
          else
          {
            i8 = paramDBConversion.javaCharsToCHARBytes(paramArrayOfChar1, i5, paramArrayOfByte2, 0, i6);
          }

          this.meg.marshalCLR(paramArrayOfByte2, i8);
        }
        else
        {
          i18 = i15;

          if (paramArrayOfInputStream == null)
            continue;
          InputStream localInputStream = paramArrayOfInputStream[i18];

          if (localInputStream == null)
          {
            continue;
          }

          int i22 = 64;

          if (this.buffer == null) {
            this.buffer = new byte[i22];
          }
          i23 = 0;

          this.meg.marshalUB1(254);

          i25 = 0;

          while (i25 == 0)
          {
            i23 = localInputStream.read(this.buffer, 0, i22);

            if (i23 < i22) {
              i25 = 1;
            }
            if (i23 <= 0)
            {
              continue;
            }
            this.meg.marshalUB1((short)(i23 & 0xFF));

            this.meg.marshalB1Array(this.buffer, 0, i23);
          }

          this.meg.marshalSB1(0);
        }
      }
    }
  }

  boolean unmarshal(Accessor[] paramArrayOfAccessor, int paramInt)
    throws SQLException, IOException
  {
    return unmarshal(paramArrayOfAccessor, 0, paramInt);
  }

  boolean unmarshal(Accessor[] paramArrayOfAccessor, int paramInt1, int paramInt2)
    throws SQLException, IOException
  {
    if (paramInt1 == 0) {
      this.isFirstCol = true;
    }
    for (int i = paramInt1; (i < paramInt2) && (i < paramArrayOfAccessor.length); i++)
    {
      if (paramArrayOfAccessor[i] == null)
      {
        continue;
      }

      if (paramArrayOfAccessor[i].physicalColumnIndex < 0)
      {
        int j = 0;

        for (int k = 0; (k < paramInt2) && (k < paramArrayOfAccessor.length); k++)
        {
          if (paramArrayOfAccessor[k] == null)
            continue;
          paramArrayOfAccessor[k].physicalColumnIndex = j;

          if (!paramArrayOfAccessor[k].isUseLess) {
            j++;
          }
        }
      }

      if ((this.bvcFound) && (!paramArrayOfAccessor[i].isUseLess))
      {
        if (this.bvcColSent.get(paramArrayOfAccessor[i].physicalColumnIndex))
        {
          if (paramArrayOfAccessor[i].unmarshalOneRow()) {
            return true;
          }
          this.isFirstCol = false;
        }
        else
        {
          paramArrayOfAccessor[i].copyRow();
        }
      }
      else
      {
        if (paramArrayOfAccessor[i].unmarshalOneRow()) {
          return true;
        }
        this.isFirstCol = false;
      }

    }

    this.bvcFound = false;

    return false;
  }

  boolean unmarshal(Accessor[] paramArrayOfAccessor, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException, IOException
  {
    return false;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CTTIrxd
 * JD-Core Version:    0.6.0
 */