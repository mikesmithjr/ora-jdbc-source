package oracle.jdbc.driver;

import B;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Vector;
import oracle.net.ns.BreakNetException;
import oracle.net.ns.Communication;
import oracle.net.ns.NetException;

class T4CMAREngine
{
  static final int TTCC_MXL = 252;
  static final int TTCC_ESC = 253;
  static final int TTCC_LNG = 254;
  static final int TTCC_ERR = 255;
  static final int TTCC_MXIN = 64;
  static final byte TTCLXMULTI = 1;
  static final byte TTCLXMCONV = 2;
  T4CTypeRep types;
  Communication net;
  DBConversion conv;
  short versionNumber = -1;
  byte proSvrVer;
  InputStream inStream;
  OutputStream outStream;
  final byte[] ignored = new byte['ÿ'];
  final byte[] tmpBuffer1 = new byte[1];
  final byte[] tmpBuffer2 = new byte[2];
  final byte[] tmpBuffer3 = new byte[3];
  final byte[] tmpBuffer4 = new byte[4];
  final byte[] tmpBuffer5 = new byte[5];
  final byte[] tmpBuffer6 = new byte[6];
  final byte[] tmpBuffer7 = new byte[7];
  final byte[] tmpBuffer8 = new byte[8];
  final int[] retLen = new int[1];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  static String toHex(long paramLong, int paramInt)
  {
    String str;
    switch (paramInt)
    {
    case 1:
      str = "00" + Long.toString(paramLong & 0xFF, 16);

      break;
    case 2:
      str = "0000" + Long.toString(paramLong & 0xFFFF, 16);

      break;
    case 3:
      str = "000000" + Long.toString(paramLong & 0xFFFFFF, 16);

      break;
    case 4:
      str = "00000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 5:
      str = "0000000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 6:
      str = "000000000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 7:
      str = "00000000000000" + Long.toString(paramLong & 0xFFFFFFFF, 16);

      break;
    case 8:
      return toHex(paramLong >> 32, 4) + toHex(paramLong, 4).substring(2);
    default:
      return "more than 8 bytes";
    }

    return "0x" + str.substring(str.length() - 2 * paramInt);
  }

  static String toHex(byte paramByte)
  {
    String str = "00" + Integer.toHexString(paramByte & 0xFF);

    return "0x" + str.substring(str.length() - 2);
  }

  static String toHex(short paramShort)
  {
    return toHex(paramShort, 2);
  }

  static String toHex(int paramInt)
  {
    return toHex(paramInt, 4);
  }

  static String toHex(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte == null) {
      return "null";
    }
    if (paramInt > paramArrayOfByte.length) {
      return "byte array not long enough";
    }
    String str = "[";
    int i = Math.min(64, paramInt);

    for (int j = 0; j < i; j++)
    {
      str = str + toHex(paramArrayOfByte[j]) + " ";
    }

    if (i < paramInt) {
      str = str + "...";
    }
    return str + "]";
  }

  static String toHex(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return "null";
    }
    return toHex(paramArrayOfByte, paramArrayOfByte.length);
  }

  T4CMAREngine(Communication paramCommunication)
    throws SQLException, IOException
  {
    if (paramCommunication == null)
    {
      DatabaseError.throwSqlException(433);
    }

    this.net = paramCommunication;
    try
    {
      this.inStream = paramCommunication.getInputStream();
      this.outStream = paramCommunication.getOutputStream();
    }
    catch (NetException localNetException)
    {
      throw new IOException(localNetException.getMessage());
    }

    this.types = new T4CTypeRep();

    this.types.setRep(1, 2);
  }

  void initBuffers()
  {
  }

  void marshalSB1(byte paramByte)
    throws IOException
  {
    this.outStream.write(paramByte);
  }

  void marshalUB1(short paramShort)
    throws IOException
  {
    this.outStream.write((byte)(paramShort & 0xFF));
  }

  void marshalSB2(short paramShort)
    throws IOException
  {
    int i = value2Buffer(paramShort, this.tmpBuffer2, 1);

    if (i != 0)
      this.outStream.write(this.tmpBuffer2, 0, i);
  }

  void marshalUB2(int paramInt)
    throws IOException
  {
    marshalSB2((short)(paramInt & 0xFFFF));
  }

  void marshalSB4(int paramInt)
    throws IOException
  {
    int i = value2Buffer(paramInt, this.tmpBuffer4, 2);

    if (i != 0)
      this.outStream.write(this.tmpBuffer4, 0, i);
  }

  void marshalUB4(long paramLong)
    throws IOException
  {
    marshalSB4((int)(paramLong & 0xFFFFFFFF));
  }

  void marshalSB8(long paramLong)
    throws IOException
  {
    int i = value2Buffer(paramLong, this.tmpBuffer8, 3);

    if (i != 0)
      this.outStream.write(this.tmpBuffer8, 0, i);
  }

  void marshalSWORD(int paramInt)
    throws IOException
  {
    marshalSB4(paramInt);
  }

  void marshalUWORD(long paramLong)
    throws IOException
  {
    marshalSB4((int)(paramLong & 0xFFFFFFFF));
  }

  void marshalB1Array(byte[] paramArrayOfByte)
    throws IOException
  {
    if (paramArrayOfByte.length > 0)
      this.outStream.write(paramArrayOfByte);
  }

  void marshalB1Array(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramArrayOfByte.length > 0)
      this.outStream.write(paramArrayOfByte, paramInt1, paramInt2);
  }

  void marshalUB4Array(long[] paramArrayOfLong)
    throws IOException
  {
    for (int i = 0; i < paramArrayOfLong.length; i++)
      marshalSB4((int)(paramArrayOfLong[i] & 0xFFFFFFFF));
  }

  void marshalO2U(boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean)
      addPtr(1);
    else
      addPtr(0);
  }

  void marshalNULLPTR()
    throws IOException
  {
    addPtr(0);
  }

  void marshalPTR()
    throws IOException
  {
    addPtr(1);
  }

  void marshalCHR(byte[] paramArrayOfByte)
    throws IOException
  {
    marshalCHR(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  void marshalCHR(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > 0)
    {
      if (this.types.isConvNeeded())
        marshalCLR(paramArrayOfByte, paramInt1, paramInt2);
      else
        this.outStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }

  void marshalCLR(byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    marshalCLR(paramArrayOfByte, 0, paramInt);
  }

  void marshalCLR(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 > 64)
    {
      int k = 0;

      this.outStream.write(-2);
      do
      {
        int i = paramInt2 - k;
        int j = i > 64 ? 64 : i;

        this.outStream.write((byte)(j & 0xFF));
        this.outStream.write(paramArrayOfByte, paramInt1 + k, j);

        k += j;
      }
      while (k < paramInt2);

      this.outStream.write(0);
    }
    else
    {
      this.outStream.write((byte)(paramInt2 & 0xFF));

      if (paramArrayOfByte.length != 0)
        this.outStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }

  void marshalKEYVAL(byte[][] paramArrayOfByte1, int[] paramArrayOfInt1, byte[][] paramArrayOfByte2, int[] paramArrayOfInt2, byte[] paramArrayOfByte, int paramInt)
    throws SQLException, IOException
  {
    for (int i = 0; i < paramInt; i++)
    {
      if ((paramArrayOfByte1[i] != null) && (paramArrayOfInt1[i] > 0))
      {
        marshalUB4(paramArrayOfInt1[i]);
        marshalCLR(paramArrayOfByte1[i], 0, paramArrayOfInt1[i]);
      }
      else {
        marshalUB4(0L);
      }
      if ((paramArrayOfByte2[i] != null) && (paramArrayOfInt2[i] > 0))
      {
        marshalUB4(paramArrayOfInt2[i]);
        marshalCLR(paramArrayOfByte2[i], 0, paramArrayOfInt2[i]);
      }
      else {
        marshalUB4(0L);
      }

      if (paramArrayOfByte[i] != 0)
        marshalUB4(1L);
      else
        marshalUB4(0L);
    }
  }

  void marshalKEYVAL(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2, byte[] paramArrayOfByte, int paramInt)
    throws SQLException, IOException
  {
    int[] arrayOfInt1 = new int[paramInt];
    int[] arrayOfInt2 = new int[paramInt];
    for (int i = 0; i < paramInt; i++)
    {
      if (paramArrayOfByte1[i] != null)
        arrayOfInt1[i] = paramArrayOfByte1[i].length;
      if (paramArrayOfByte2[i] != null)
        arrayOfInt2[i] = paramArrayOfByte2[i].length;
    }
    marshalKEYVAL(paramArrayOfByte1, arrayOfInt1, paramArrayOfByte2, arrayOfInt2, paramArrayOfByte, paramInt);
  }

  void marshalDALC(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 1))
    {
      this.outStream.write(0);
    }
    else
    {
      marshalSB4(0xFFFFFFFF & paramArrayOfByte.length);
      marshalCLR(paramArrayOfByte, paramArrayOfByte.length);
    }
  }

  void addPtr(byte paramByte)
    throws IOException
  {
    if ((this.types.rep[4] & 0x1) > 0) {
      this.outStream.write(paramByte);
    }
    else
    {
      int i = value2Buffer(paramByte, this.tmpBuffer4, 4);

      if (i != 0)
        this.outStream.write(this.tmpBuffer4, 0, i);
    }
  }

  byte value2Buffer(int paramInt, byte[] paramArrayOfByte, byte paramByte)
    throws IOException
  {
    int i = 1;
    byte b = 0;

    for (int j = paramArrayOfByte.length - 1; j >= 0; j--)
    {
      paramArrayOfByte[b] = (byte)(paramInt >>> 8 * j & 0xFF);

      if ((this.types.rep[paramByte] & 0x1) > 0)
      {
        if ((i != 0) && (paramArrayOfByte[b] == 0))
          continue;
        i = 0;
        b = (byte)(b + 1);
      }
      else
      {
        b = (byte)(b + 1);
      }
    }

    if ((this.types.rep[paramByte] & 0x1) > 0) {
      this.outStream.write(b);
    }

    if ((this.types.rep[paramByte] & 0x2) > 0) {
      reverseArray(paramArrayOfByte, b);
    }

    return b;
  }

  byte value2Buffer(long paramLong, byte[] paramArrayOfByte, byte paramByte)
    throws IOException
  {
    int i = 1;
    byte b = 0;

    for (int j = paramArrayOfByte.length - 1; j >= 0; j--)
    {
      paramArrayOfByte[b] = (byte)(int)(paramLong >>> 8 * j & 0xFF);

      if ((this.types.rep[paramByte] & 0x1) > 0)
      {
        if ((i != 0) && (paramArrayOfByte[b] == 0))
          continue;
        i = 0;
        b = (byte)(b + 1);
      }
      else
      {
        b = (byte)(b + 1);
      }
    }

    if ((this.types.rep[paramByte] & 0x1) > 0) {
      this.outStream.write(b);
    }

    if ((this.types.rep[paramByte] & 0x2) > 0) {
      reverseArray(paramArrayOfByte, b);
    }

    return b;
  }

  void reverseArray(byte[] paramArrayOfByte, byte paramByte)
  {
    for (int j = 0; j < paramByte / 2; j++)
    {
      int i = paramArrayOfByte[j];
      paramArrayOfByte[j] = paramArrayOfByte[(paramByte - 1 - j)];
      paramArrayOfByte[(paramByte - 1 - j)] = i;
    }
  }

  byte unmarshalSB1()
    throws SQLException, IOException
  {
    int i = (byte)unmarshalUB1();

    return i;
  }

  short unmarshalUB1()
    throws SQLException, IOException
  {
    int i = 0;
    try
    {
      i = (short)this.inStream.read();
    }
    catch (BreakNetException localBreakNetException)
    {
      this.net.sendReset();
      throw localBreakNetException;
    }

    if (i < 0)
    {
      DatabaseError.throwSqlException(410);
    }

    return i;
  }

  short unmarshalSB2()
    throws SQLException, IOException
  {
    int i = (short)unmarshalUB2();

    return i;
  }

  int unmarshalUB2()
    throws SQLException, IOException
  {
    int i = (int)buffer2Value(1);

    return i & 0xFFFF;
  }

  int unmarshalUCS2(byte[] paramArrayOfByte, long paramLong)
    throws SQLException, IOException
  {
    int i = unmarshalUB2();

    this.tmpBuffer2[0] = (byte)((i & 0xFF00) >> 8);
    this.tmpBuffer2[1] = (byte)(i & 0xFF);

    if (paramLong + 1L < paramArrayOfByte.length)
    {
      paramArrayOfByte[(int)paramLong] = this.tmpBuffer2[0];
      paramArrayOfByte[((int)paramLong + 1)] = this.tmpBuffer2[1];
    }

    return this.tmpBuffer2[0] == 0 ? 2 : this.tmpBuffer2[1] == 0 ? 1 : 3;
  }

  int unmarshalSB4()
    throws SQLException, IOException
  {
    int i = (int)unmarshalUB4();

    return i;
  }

  long unmarshalUB4()
    throws SQLException, IOException
  {
    long l = buffer2Value(2);

    return l;
  }

  int unmarshalSB4(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    long l = buffer2Value(2, new ByteArrayInputStream(paramArrayOfByte));

    return (int)l;
  }

  long unmarshalSB8()
    throws SQLException, IOException
  {
    long l = buffer2Value(3);

    return l;
  }

  int unmarshalRefCursor(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    int i = unmarshalSB4(paramArrayOfByte);

    return i;
  }

  int unmarshalSWORD()
    throws SQLException, IOException
  {
    int i = (int)unmarshalUB4();

    return i;
  }

  long unmarshalUWORD()
    throws SQLException, IOException
  {
    long l = unmarshalUB4();

    return l;
  }

  byte[] unmarshalNBytes(int paramInt)
    throws SQLException, IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    try
    {
      if (this.inStream.read(arrayOfByte) < 0)
      {
        DatabaseError.throwSqlException(410);
      }

    }
    catch (BreakNetException localBreakNetException)
    {
      this.net.sendReset();
      throw localBreakNetException;
    }

    return arrayOfByte;
  }

  int unmarshalNBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException, IOException
  {
    int i = 0;

    while (i < paramInt2) {
      i += getNBytes(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
    }

    return i;
  }

  int getNBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException, IOException
  {
    int i = 0;
    try
    {
      if ((i = this.inStream.read(paramArrayOfByte, paramInt1, paramInt2)) < 0)
      {
        DatabaseError.throwSqlException(410);
      }

    }
    catch (BreakNetException localBreakNetException)
    {
      this.net.sendReset();
      throw localBreakNetException;
    }

    return i;
  }

  byte[] getNBytes(int paramInt)
    throws SQLException, IOException
  {
    byte[] arrayOfByte = new byte[paramInt];
    try
    {
      if (this.inStream.read(arrayOfByte) < 0)
      {
        DatabaseError.throwSqlException(410);
      }

    }
    catch (BreakNetException localBreakNetException)
    {
      this.net.sendReset();
      throw localBreakNetException;
    }

    return arrayOfByte;
  }

  byte[] unmarshalTEXT(int paramInt)
    throws SQLException, IOException
  {
    int i = 0;

    byte[] arrayOfByte1 = new byte[paramInt];

    while (i < paramInt)
    {
      try
      {
        if (this.inStream.read(arrayOfByte1, i, 1) < 0)
        {
          DatabaseError.throwSqlException(410);
        }

      }
      catch (BreakNetException localBreakNetException)
      {
        this.net.sendReset();
        throw localBreakNetException;
      }

      if (arrayOfByte1[(i++)] == 0) {
        break;
      }
    }

    i--;
    byte[] arrayOfByte2;
    if (arrayOfByte1.length == i)
    {
      arrayOfByte2 = arrayOfByte1;
    }
    else
    {
      arrayOfByte2 = new byte[i];

      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
    }

    return arrayOfByte2;
  }

  byte[] unmarshalCHR(int paramInt)
    throws SQLException, IOException
  {
    Object localObject = null;

    if (this.types.isConvNeeded())
    {
      localObject = unmarshalCLR(paramInt, this.retLen);

      if (localObject.length != this.retLen[0])
      {
        byte[] arrayOfByte = new byte[this.retLen[0]];

        System.arraycopy(localObject, 0, arrayOfByte, 0, this.retLen[0]);

        localObject = arrayOfByte;
      }
    }
    else
    {
      localObject = getNBytes(paramInt);
    }

    return (B)localObject;
  }

  void unmarshalCLR(byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
    throws SQLException, IOException
  {
    unmarshalCLR(paramArrayOfByte, paramInt, paramArrayOfInt, 2147483647);
  }

  void unmarshalCLR(byte[] paramArrayOfByte, int paramInt1, int[] paramArrayOfInt, int paramInt2)
    throws SQLException, IOException
  {
    int i = 0;
    int j = paramInt1;
    int k = 0;
    int m = 0;
    int n = 0;

    int i1 = -1;

    i = unmarshalUB1();

    if (i < 0)
    {
      DatabaseError.throwSqlException(401);
    }

    if (i == 0)
    {
      paramArrayOfInt[0] = 0;

      return;
    }

    if (escapeSequenceNull(i))
    {
      paramArrayOfInt[0] = 0;

      return;
    }
    int i2;
    if (i != 254)
    {
      n = Math.min(paramInt2 - m, i);
      j = unmarshalBuffer(paramArrayOfByte, j, n);
      m += n;

      i2 = i - n;

      if (i2 > 0)
        unmarshalBuffer(this.ignored, 0, i2);
    }
    else
    {
      i1 = -1;
      while (true)
      {
        if (i1 != -1)
        {
          i = unmarshalUB1();

          if (i <= 0) {
            break;
          }
        }
        if (i == 254)
        {
          switch (i1)
          {
          case -1:
            i1 = 1;

            break;
          case 1:
            i1 = 0;

            break;
          case 0:
            if (k != 0)
            {
              i1 = 0;
            }
            else
            {
              i1 = 0;
            }
            break;
          }

        }

        if (j == -1)
        {
          unmarshalBuffer(this.ignored, 0, i);
        }
        else
        {
          n = Math.min(paramInt2 - m, i);
          j = unmarshalBuffer(paramArrayOfByte, j, n);
          m += n;

          i2 = i - n;

          if (i2 > 0) {
            unmarshalBuffer(this.ignored, 0, i2);
          }
        }

        i1 = 0;

        if (i > 252) {
          k = 1;
        }
      }

    }

    if (paramArrayOfInt != null)
    {
      if (j != -1) {
        paramArrayOfInt[0] = m;
      }
      else
        paramArrayOfInt[0] = (paramArrayOfByte.length - paramInt1);
    }
  }

  byte[] unmarshalCLR(int paramInt, int[] paramArrayOfInt)
    throws SQLException, IOException
  {
    byte[] arrayOfByte = new byte[paramInt * this.conv.c2sNlsRatio];

    unmarshalCLR(arrayOfByte, 0, paramArrayOfInt, paramInt);

    return arrayOfByte;
  }

  int unmarshalKEYVAL(byte[][] paramArrayOfByte1, byte[][] paramArrayOfByte2, int paramInt)
    throws SQLException, IOException
  {
    byte[] arrayOfByte = new byte[1000];
    int[] arrayOfInt = new int[1];
    int i = 0;

    for (int k = 0; k < paramInt; k++)
    {
      int j = unmarshalSB4();

      if (j > 0)
      {
        unmarshalCLR(arrayOfByte, 0, arrayOfInt);

        paramArrayOfByte1[k] = new byte[arrayOfInt[0]];

        System.arraycopy(arrayOfByte, 0, paramArrayOfByte1[k], 0, arrayOfInt[0]);
      }

      j = unmarshalSB4();

      if (j > 0)
      {
        unmarshalCLR(arrayOfByte, 0, arrayOfInt);

        paramArrayOfByte2[k] = new byte[arrayOfInt[0]];

        System.arraycopy(arrayOfByte, 0, paramArrayOfByte2[k], 0, arrayOfInt[0]);
      }

      i = unmarshalSB4();
    }

    arrayOfByte = null;

    return i;
  }

  int unmarshalBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException, IOException
  {
    if (paramInt2 <= 0) {
      return paramInt1;
    }
    if (paramArrayOfByte.length < paramInt1 + paramInt2)
    {
      unmarshalNBytes(paramArrayOfByte, paramInt1, paramArrayOfByte.length - paramInt1);

      unmarshalNBytes(this.ignored, 0, paramInt1 + paramInt2 - paramArrayOfByte.length);

      paramInt1 = -1;
    }
    else
    {
      unmarshalNBytes(paramArrayOfByte, paramInt1, paramInt2);

      paramInt1 += paramInt2;
    }

    return paramInt1;
  }

  byte[] unmarshalCLRforREFS()
    throws SQLException, IOException
  {
    int i = 0;
    int j = 0;
    byte[] arrayOfByte1 = null;
    Vector localVector = new Vector(10, 10);

    int k = unmarshalUB1();

    if (k < 0)
    {
      DatabaseError.throwSqlException(401);
    }

    if (k == 0)
    {
      return null;
    }

    if (!escapeSequenceNull(k))
    {
      if (k == 254)
      {
        while ((i = unmarshalUB1()) > 0)
        {
          if ((i == 254) && 
            (this.types.isServerConversion()))
          {
            continue;
          }
          j = (short)(j + i);

          arrayOfByte2 = new byte[i];

          unmarshalBuffer(arrayOfByte2, 0, i);
          localVector.addElement(arrayOfByte2);
        }

      }

      j = k;

      byte[] arrayOfByte2 = new byte[k];

      unmarshalBuffer(arrayOfByte2, 0, k);
      localVector.addElement(arrayOfByte2);

      arrayOfByte1 = new byte[j];

      int m = 0;

      while (localVector.size() > 0)
      {
        int n = ((byte[])localVector.elementAt(0)).length;

        System.arraycopy(localVector.elementAt(0), 0, arrayOfByte1, m, n);

        m += n;

        localVector.removeElementAt(0);
      }

    }

    arrayOfByte1 = null;

    return arrayOfByte1;
  }

  boolean escapeSequenceNull(int paramInt)
    throws SQLException
  {
    int i = 0;

    switch (paramInt)
    {
    case 0:
      i = 1;

      break;
    case 253:
      DatabaseError.throwSqlException(401);
    case 255:
      i = 1;

      break;
    case 254:
      break;
    }

    return i;
  }

  int processIndicator(boolean paramBoolean, int paramInt)
    throws SQLException, IOException
  {
    int i = unmarshalSB2();
    int j = 0;

    if (!paramBoolean)
    {
      if (i == 0)
        j = paramInt;
      else if ((i == -2) || (i > 0)) {
        j = i;
      }
      else
      {
        j = 65536 + i;
      }

    }

    return j;
  }

  long unmarshalDALC(byte[] paramArrayOfByte, int paramInt, int[] paramArrayOfInt)
    throws SQLException, IOException
  {
    long l = unmarshalUB4();

    if (l > 0L) {
      unmarshalCLR(paramArrayOfByte, paramInt, paramArrayOfInt);
    }

    return l;
  }

  byte[] unmarshalDALC()
    throws SQLException, IOException
  {
    long l = unmarshalUB4();
    byte[] arrayOfByte = new byte[(int)(0xFFFFFFFF & l)];

    if (arrayOfByte.length > 0)
    {
      arrayOfByte = unmarshalCLR(arrayOfByte.length, this.retLen);

      if (arrayOfByte == null)
      {
        DatabaseError.throwSqlException(401);
      }
    }
    else {
      arrayOfByte = new byte[0];
    }

    return arrayOfByte;
  }

  byte[] unmarshalDALC(int[] paramArrayOfInt)
    throws SQLException, IOException
  {
    long l = unmarshalUB4();
    byte[] arrayOfByte = new byte[(int)(0xFFFFFFFF & l)];

    if (arrayOfByte.length > 0)
    {
      arrayOfByte = unmarshalCLR(arrayOfByte.length, paramArrayOfInt);

      if (arrayOfByte == null)
      {
        DatabaseError.throwSqlException(401);
      }
    }
    else {
      arrayOfByte = new byte[0];
    }

    return arrayOfByte;
  }

  long buffer2Value(byte paramByte)
    throws SQLException, IOException
  {
    int i = 0;

    long l2 = 0L;
    int j = 0;

    if ((this.types.rep[paramByte] & 0x1) > 0)
    {
      try
      {
        i = this.inStream.read();
      }
      catch (BreakNetException localBreakNetException1)
      {
        this.net.sendReset();
        throw localBreakNetException1;
      }

      if ((i & 0x80) > 0)
      {
        i &= 127;
        j = 1;
      }

      if (i < 0)
      {
        DatabaseError.throwSqlException(410);
      }

      if (i == 0)
      {
        return 0L;
      }

      if (((paramByte == 1) && (i > 2)) || ((paramByte == 2) && (i > 4)) || ((paramByte == 3) && (i > 8)))
      {
        DatabaseError.throwSqlException(412);
      }

    }
    else if (paramByte == 1) {
      i = 2;
    } else if (paramByte == 2) {
      i = 4;
    } else if (paramByte == 3) {
      i = 8;
    }
    byte[] arrayOfByte;
    switch (i)
    {
    case 1:
      arrayOfByte = this.tmpBuffer1;

      break;
    case 2:
      arrayOfByte = this.tmpBuffer2;

      break;
    case 3:
      arrayOfByte = this.tmpBuffer3;

      break;
    case 4:
      arrayOfByte = this.tmpBuffer4;

      break;
    case 5:
      arrayOfByte = this.tmpBuffer5;

      break;
    case 6:
      arrayOfByte = this.tmpBuffer6;

      break;
    case 7:
      arrayOfByte = this.tmpBuffer7;

      break;
    case 8:
      arrayOfByte = this.tmpBuffer8;

      break;
    default:
      arrayOfByte = new byte[i];
    }

    try
    {
      if (this.inStream.read(arrayOfByte) < 0)
      {
        DatabaseError.throwSqlException(410);
      }

    }
    catch (BreakNetException localBreakNetException2)
    {
      this.net.sendReset();
      throw localBreakNetException2;
    }

    for (int k = 0; k < arrayOfByte.length; k++)
    {
      long l1;
      if ((this.types.rep[paramByte] & 0x2) > 0)
      {
        l1 = arrayOfByte[(arrayOfByte.length - 1 - k)] & 0xFF & 0xFF;
      }
      else
      {
        l1 = arrayOfByte[k] & 0xFF & 0xFF;
      }

      l2 |= l1 << 8 * (arrayOfByte.length - 1 - k);
    }

    if (paramByte != 3)
    {
      l2 &= -1L;
    }

    if (j != 0) {
      l2 = -l2;
    }

    return l2;
  }

  long buffer2Value(byte paramByte, ByteArrayInputStream paramByteArrayInputStream)
    throws SQLException, IOException
  {
    int j = 0;

    long l = 0L;
    int k = 0;

    if ((this.types.rep[paramByte] & 0x1) > 0)
    {
      j = paramByteArrayInputStream.read();

      if ((j & 0x80) > 0)
      {
        j &= 127;
        k = 1;
      }

      if (j < 0)
      {
        DatabaseError.throwSqlException(410);
      }

      if (j == 0)
      {
        return 0L;
      }

      if (((paramByte == 1) && (j > 2)) || ((paramByte == 2) && (j > 4)))
      {
        DatabaseError.throwSqlException(412);
      }

    }
    else if (paramByte == 1) {
      j = 2;
    } else if (paramByte == 2) {
      j = 4;
    }

    byte[] arrayOfByte = new byte[j];

    if (paramByteArrayInputStream.read(arrayOfByte) < 0)
    {
      DatabaseError.throwSqlException(410);
    }

    for (int m = 0; m < arrayOfByte.length; m++)
    {
      int i;
      if ((this.types.rep[paramByte] & 0x2) > 0)
        i = (short)(arrayOfByte[(arrayOfByte.length - 1 - m)] & 0xFF);
      else {
        i = (short)(arrayOfByte[m] & 0xFF);
      }
      l |= i << 8 * (arrayOfByte.length - 1 - m);
    }

    l &= -1L;

    if (k != 0) {
      l = -l;
    }

    return l;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4CMAREngine
 * JD-Core Version:    0.6.0
 */