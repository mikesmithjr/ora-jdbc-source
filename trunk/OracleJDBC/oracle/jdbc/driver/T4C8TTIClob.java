package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import oracle.jdbc.OracleConnection;
import oracle.sql.CLOB;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;

class T4C8TTIClob extends T4C8TTILob
{
  int[] nBytes;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:53_PDT_2005";

  T4C8TTIClob(T4CMAREngine paramT4CMAREngine, T4CTTIoer paramT4CTTIoer)
  {
    super(paramT4CMAREngine, paramT4CTTIoer);

    this.nBytes = new int[1];
  }

  long read(byte[] paramArrayOfByte, long paramLong1, long paramLong2, boolean paramBoolean, char[] paramArrayOfChar)
    throws SQLException, IOException
  {
    long l1 = 0L;

    byte[] arrayOfByte = null;

    initializeLobdef();

    if ((paramArrayOfByte[6] & 0x80) == 128) {
      this.varWidthChar = true;
    }
    if (this.varWidthChar == true)
      arrayOfByte = new byte[(int)paramLong2 * 2];
    else {
      arrayOfByte = new byte[(int)paramLong2 * 3];
    }

    if ((paramArrayOfByte[7] & 0x40) > 0) {
      this.littleEndianClob = true;
    }

    this.lobops = 2L;
    this.sourceLobLocator = paramArrayOfByte;
    this.sourceOffset = paramLong1;
    this.lobamt = paramLong2;
    this.sendLobamt = true;
    this.outBuffer = arrayOfByte;

    marshalFunHeader();

    marshalOlobops();

    receiveReply();

    long l2 = this.lobamt;

    long l3 = 0L;

    if (this.varWidthChar == true)
    {
      if (this.meg.versionNumber < 10101)
      {
        DBConversion.ucs2BytesToJavaChars(arrayOfByte, arrayOfByte.length, paramArrayOfChar);
      }
      else if (this.littleEndianClob)
      {
        CharacterSet.convertAL16UTF16LEBytesToJavaChars(arrayOfByte, 0, paramArrayOfChar, 0, (int)this.lobBytesRead, true);
      }
      else
      {
        CharacterSet.convertAL16UTF16BytesToJavaChars(arrayOfByte, 0, paramArrayOfChar, 0, (int)this.lobBytesRead, true);
      }

    }
    else if (!paramBoolean)
    {
      this.nBytes[0] = (int)this.lobBytesRead;

      this.meg.conv.CHARBytesToJavaChars(arrayOfByte, 0, paramArrayOfChar, 0, this.nBytes, paramArrayOfChar.length);
    }
    else
    {
      this.nBytes[0] = (int)this.lobBytesRead;

      this.meg.conv.NCHARBytesToJavaChars(arrayOfByte, 0, paramArrayOfChar, 0, this.nBytes, paramArrayOfChar.length);
    }

    return l2;
  }

  long write(byte[] paramArrayOfByte, long paramLong1, boolean paramBoolean, char[] paramArrayOfChar, long paramLong2, long paramLong3)
    throws SQLException, IOException
  {
    int i = 0;

    if ((paramArrayOfByte[6] & 0x80) == 128) {
      i = 1;
    }
    if ((paramArrayOfByte[7] & 0x40) == 64) {
      this.littleEndianClob = true;
    }

    long l1 = 0L;
    byte[] arrayOfByte = null;

    if (i == 1)
    {
      arrayOfByte = new byte[(int)paramLong3 * 2];

      if (this.meg.versionNumber < 10101)
      {
        DBConversion.javaCharsToUcs2Bytes(paramArrayOfChar, (int)paramLong2, arrayOfByte, 0, (int)paramLong3);
      }
      else if (this.littleEndianClob)
      {
        CharacterSet.convertJavaCharsToAL16UTF16LEBytes(paramArrayOfChar, (int)paramLong2, arrayOfByte, 0, (int)paramLong3);
      }
      else
      {
        CharacterSet.convertJavaCharsToAL16UTF16Bytes(paramArrayOfChar, (int)paramLong2, arrayOfByte, 0, (int)paramLong3);
      }

    }
    else
    {
      arrayOfByte = new byte[(int)paramLong3 * 3];

      if (!paramBoolean)
      {
        l1 = this.meg.conv.javaCharsToCHARBytes(paramArrayOfChar, (int)paramLong2, arrayOfByte, 0, (int)paramLong3);
      }
      else
      {
        l1 = this.meg.conv.javaCharsToNCHARBytes(paramArrayOfChar, (int)paramLong2, arrayOfByte, 0, (int)paramLong3);
      }

    }

    initializeLobdef();

    this.lobops = 64L;
    this.sourceLobLocator = paramArrayOfByte;
    this.sourceOffset = paramLong1;
    this.lobamt = paramLong3;
    this.sendLobamt = true;
    this.inBuffer = arrayOfByte;

    marshalFunHeader();

    marshalOlobops();

    if (i == 1)
    {
      if (this.meg.versionNumber < 10101)
        this.lobd.marshalLobDataUB2(arrayOfByte, 0L, paramLong3);
      else {
        this.lobd.marshalLobData(arrayOfByte, 0L, paramLong3 * 2L);
      }

    }
    else
    {
      this.lobd.marshalLobData(arrayOfByte, 0L, l1);
    }

    receiveReply();

    long l2 = this.lobamt;

    return l2;
  }

  Datum createTemporaryLob(Connection paramConnection, boolean paramBoolean, int paramInt)
    throws SQLException, IOException
  {
    return createTemporaryLob(paramConnection, paramBoolean, paramInt, 1);
  }

  Datum createTemporaryLob(Connection paramConnection, boolean paramBoolean, int paramInt, short paramShort)
    throws SQLException, IOException
  {
    if (paramInt == 12) {
      DatabaseError.throwSqlException(158);
    }

    CLOB localCLOB = null;

    initializeLobdef();

    this.lobops = 272L;
    this.sourceLobLocator = new byte[86];
    this.sourceLobLocator[1] = 84;

    this.lobamt = 10L;
    this.sendLobamt = true;

    if (paramShort == 1)
      this.sourceOffset = 1L;
    else {
      this.sourceOffset = 2L;
    }

    this.destinationOffset = 112L;

    this.destinationLength = paramInt;

    this.nullO2U = true;

    this.characterSet = (paramShort == 2 ? this.meg.conv.getNCharSetId() : this.meg.conv.getServerCharSetId());

    marshalFunHeader();

    marshalOlobops();

    receiveReply();

    if (this.sourceLobLocator != null)
    {
      localCLOB = new CLOB((OracleConnection)paramConnection, this.sourceLobLocator);
    }

    return localCLOB;
  }

  boolean open(byte[] paramArrayOfByte, int paramInt)
    throws SQLException, IOException
  {
    boolean bool = false;

    int i = 2;

    if (paramInt == 0) {
      i = 1;
    }
    bool = _open(paramArrayOfByte, i, 32768);

    return bool;
  }

  boolean close(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = false;

    bool = _close(paramArrayOfByte, 65536);

    return bool;
  }

  boolean isOpen(byte[] paramArrayOfByte)
    throws SQLException, IOException
  {
    boolean bool = false;

    bool = _isOpen(paramArrayOfByte, 69632);

    return bool;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T4C8TTIClob
 * JD-Core Version:    0.6.0
 */