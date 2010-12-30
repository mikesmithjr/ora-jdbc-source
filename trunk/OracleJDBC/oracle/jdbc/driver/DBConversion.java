package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import oracle.jdbc.util.RepConversion;
import oracle.sql.CharacterSet;
import oracle.sql.converter.CharacterSetMetaData;

public class DBConversion
{
  public static final boolean DO_CONVERSION_WITH_REPLACEMENT = true;
  public static final short ORACLE8_PROD_VERSION = 8030;
  protected short serverNCharSetId;
  protected short serverCharSetId;
  protected short clientCharSetId;
  protected CharacterSet serverCharSet;
  protected CharacterSet serverNCharSet;
  protected CharacterSet clientCharSet;
  protected CharacterSet asciiCharSet;
  protected boolean isServerCharSetFixedWidth;
  protected boolean isServerNCharSetFixedWidth;
  protected int c2sNlsRatio;
  protected int s2cNlsRatio;
  protected int sMaxCharSize;
  protected int cMaxCharSize;
  protected int maxNCharSize;
  protected boolean isServerCSMultiByte;
  public static final short DBCS_CHARSET = -1;
  public static final short UCS2_CHARSET = -5;
  public static final short ASCII_CHARSET = 1;
  public static final short ISO_LATIN_1_CHARSET = 31;
  public static final short AL24UTFFSS_CHARSET = 870;
  public static final short UTF8_CHARSET = 871;
  public static final short AL32UTF8_CHARSET = 873;
  public static final short AL16UTF16_CHARSET = 2000;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public DBConversion(short paramShort1, short paramShort2, short paramShort3)
    throws SQLException
  {
    switch (paramShort2)
    {
    case -5:
    case -1:
    case 1:
    case 2:
    case 31:
    case 178:
    case 870:
    case 871:
    case 873:
      break;
    default:
      unexpectedCharset(paramShort2);
    }

    this.serverCharSetId = paramShort1;
    this.clientCharSetId = paramShort2;
    this.serverCharSet = CharacterSet.make(this.serverCharSetId);

    this.serverNCharSetId = paramShort3;
    this.serverNCharSet = CharacterSet.make(this.serverNCharSetId);

    if (paramShort2 == -1)
      this.clientCharSet = this.serverCharSet;
    else {
      this.clientCharSet = CharacterSet.make(this.clientCharSetId);
    }
    this.c2sNlsRatio = CharacterSetMetaData.getRatio(paramShort1, paramShort2);
    this.s2cNlsRatio = CharacterSetMetaData.getRatio(paramShort2, paramShort1);
    this.sMaxCharSize = CharacterSetMetaData.getRatio(paramShort1, 1);
    this.cMaxCharSize = CharacterSetMetaData.getRatio(paramShort2, 1);
    this.maxNCharSize = CharacterSetMetaData.getRatio(paramShort3, 1);
    this.isServerCSMultiByte = (CharacterSetMetaData.getRatio(paramShort1, 1) != 1);
    this.isServerCharSetFixedWidth = CharacterSetMetaData.isFixedWidth(paramShort1);
    this.isServerNCharSetFixedWidth = CharacterSetMetaData.isFixedWidth(paramShort3);
  }

  public short getServerCharSetId()
  {
    return this.serverCharSetId;
  }

  public short getNCharSetId()
  {
    return this.serverNCharSetId;
  }

  public boolean IsNCharFixedWith()
  {
    return this.serverNCharSetId == 2000;
  }

  public short getClientCharSet()
  {
    if (this.clientCharSetId == -1) {
      return this.serverCharSetId;
    }
    return this.clientCharSetId;
  }

  public CharacterSet getDbCharSetObj()
  {
    return this.serverCharSet;
  }

  public CharacterSet getDriverCharSetObj()
  {
    return this.clientCharSet;
  }

  public CharacterSet getDriverNCharSetObj()
  {
    return this.serverNCharSet;
  }

  public static final short findDriverCharSet(short paramShort1, short paramShort2)
  {
    int i = 0;

    switch (paramShort1)
    {
    case 1:
    case 2:
    case 31:
    case 178:
    case 873:
      i = paramShort1;

      break;
    default:
      i = paramShort2 >= 8030 ? 871 : 870;
    }

    return i;
  }

  public static final byte[] stringToDriverCharBytes(String paramString, short paramShort)
    throws SQLException
  {
    if (paramString == null)
    {
      return null;
    }

    byte[] arrayOfByte = null;

    switch (paramShort)
    {
    case -5:
    case 2000:
      arrayOfByte = CharacterSet.stringToAL16UTF16Bytes(paramString);

      break;
    case 1:
    case 2:
    case 31:
    case 178:
      arrayOfByte = CharacterSet.stringToASCII(paramString);

      break;
    case 870:
    case 871:
      arrayOfByte = CharacterSet.stringToUTF(paramString);

      break;
    case 873:
      arrayOfByte = CharacterSet.stringToAL32UTF8(paramString);

      break;
    case -1:
    default:
      unexpectedCharset(paramShort);
    }

    return arrayOfByte;
  }

  public byte[] StringToCharBytes(String paramString)
    throws SQLException
  {
    if (paramString.length() == 0) {
      return null;
    }
    if (this.clientCharSetId == -1)
    {
      return this.serverCharSet.convertWithReplacement(paramString);
    }

    return stringToDriverCharBytes(paramString, this.clientCharSetId);
  }

  public String CharBytesToString(byte[] paramArrayOfByte, int paramInt)
    throws SQLException
  {
    return CharBytesToString(paramArrayOfByte, paramInt, false);
  }

  public String CharBytesToString(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SQLException
  {
    String str = null;
    if (paramArrayOfByte.length == 0) {
      return str;
    }
    switch (this.clientCharSetId)
    {
    case -5:
      str = CharacterSet.AL16UTF16BytesToString(paramArrayOfByte, paramInt);

      break;
    case 1:
    case 2:
    case 31:
    case 178:
      str = new String(paramArrayOfByte, 0, 0, paramInt);

      break;
    case 870:
    case 871:
      str = CharacterSet.UTFToString(paramArrayOfByte, 0, paramInt, paramBoolean);

      break;
    case 873:
      str = CharacterSet.AL32UTF8ToString(paramArrayOfByte, 0, paramInt, paramBoolean);

      break;
    case -1:
      str = this.serverCharSet.toStringWithReplacement(paramArrayOfByte, 0, paramInt);

      break;
    default:
      unexpectedCharset(this.clientCharSetId);
    }

    return str;
  }

  public String NCharBytesToString(byte[] paramArrayOfByte, int paramInt)
    throws SQLException
  {
    String str = null;

    if (this.clientCharSetId == -1)
    {
      str = this.serverNCharSet.toStringWithReplacement(paramArrayOfByte, 0, paramInt);
    }
    else
    {
      switch (this.serverNCharSetId)
      {
      case -5:
      case 2000:
        str = CharacterSet.AL16UTF16BytesToString(paramArrayOfByte, paramInt);

        break;
      case 1:
      case 2:
      case 31:
      case 178:
        str = new String(paramArrayOfByte, 0, 0, paramInt);

        break;
      case 870:
      case 871:
        str = CharacterSet.UTFToString(paramArrayOfByte, 0, paramInt);

        break;
      case 873:
        str = CharacterSet.AL32UTF8ToString(paramArrayOfByte, 0, paramInt);

        break;
      case -1:
        str = this.serverCharSet.toStringWithReplacement(paramArrayOfByte, 0, paramInt);

        break;
      default:
        unexpectedCharset(this.clientCharSetId);
      }

    }

    return str;
  }

  public int javaCharsToCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return javaCharsToCHARBytes(paramArrayOfChar, paramInt, paramArrayOfByte, this.clientCharSetId);
  }

  public int javaCharsToCHARBytes(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SQLException
  {
    return javaCharsToCHARBytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, this.clientCharSetId, paramInt3);
  }

  public int javaCharsToNCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return javaCharsToCHARBytes(paramArrayOfChar, paramInt, paramArrayOfByte, this.serverNCharSetId);
  }

  public int javaCharsToNCHARBytes(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SQLException
  {
    return javaCharsToCHARBytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, this.serverNCharSetId, paramInt3);
  }

  protected int javaCharsToCHARBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte, short paramShort)
    throws SQLException
  {
    return javaCharsToCHARBytes(paramArrayOfChar, 0, paramArrayOfByte, 0, paramShort, paramInt);
  }

  protected int javaCharsToCHARBytes(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, short paramShort, int paramInt3)
    throws SQLException
  {
    int i = 0;

    switch (paramShort)
    {
    case -5:
    case 2000:
      i = CharacterSet.convertJavaCharsToAL16UTF16Bytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3);

      break;
    case 2:
    case 178:
      byte[] arrayOfByte = new byte[paramInt3];
      arrayOfByte = this.clientCharSet.convertWithReplacement(new String(paramArrayOfChar, paramInt1, paramInt3));
      System.arraycopy(arrayOfByte, 0, paramArrayOfByte, 0, arrayOfByte.length);

      i = arrayOfByte.length;

      break;
    case 1:
      i = CharacterSet.convertJavaCharsToASCIIBytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3);

      break;
    case 31:
      i = CharacterSet.convertJavaCharsToISOLATIN1Bytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3);

      break;
    case 870:
    case 871:
      i = CharacterSet.convertJavaCharsToUTFBytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3);

      break;
    case 873:
      i = CharacterSet.convertJavaCharsToAL32UTF8Bytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3);

      break;
    case -1:
      i = javaCharsToDbCsBytes(paramArrayOfChar, paramInt1, paramArrayOfByte, paramInt2, paramInt3);

      break;
    default:
      unexpectedCharset(this.clientCharSetId);
    }

    return i;
  }

  public int CHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt1, char[] paramArrayOfChar, int paramInt2, int[] paramArrayOfInt, int paramInt3)
    throws SQLException
  {
    return _CHARBytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, this.clientCharSetId, paramArrayOfInt, paramInt3, this.serverCharSet, this.serverNCharSet, this.clientCharSet, false);
  }

  public int NCHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt1, char[] paramArrayOfChar, int paramInt2, int[] paramArrayOfInt, int paramInt3)
    throws SQLException
  {
    return _CHARBytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, this.serverNCharSetId, paramArrayOfInt, paramInt3, this.serverCharSet, this.serverNCharSet, this.clientCharSet, true);
  }

  static final int _CHARBytesToJavaChars(byte[] paramArrayOfByte, int paramInt1, char[] paramArrayOfChar, int paramInt2, short paramShort, int[] paramArrayOfInt, int paramInt3, CharacterSet paramCharacterSet1, CharacterSet paramCharacterSet2, CharacterSet paramCharacterSet3, boolean paramBoolean)
    throws SQLException
  {
    int i = 0;
    int j = 0;

    switch (paramShort)
    {
    case -5:
    case 2000:
      j = paramArrayOfInt[0] - paramArrayOfInt[0] % 2;

      if (paramInt3 > paramArrayOfChar.length - paramInt2) {
        paramInt3 = paramArrayOfChar.length - paramInt2;
      }

      if (paramInt3 * 2 < j) {
        j = paramInt3 * 2;
      }
      i = CharacterSet.convertAL16UTF16BytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, j, true);

      paramArrayOfInt[0] -= j;

      break;
    case 1:
    case 2:
    case 31:
    case 178:
      j = paramArrayOfInt[0];

      if (paramInt3 > paramArrayOfChar.length - paramInt2) {
        paramInt3 = paramArrayOfChar.length - paramInt2;
      }

      if (paramInt3 < j) {
        j = paramInt3;
      }
      i = CharacterSet.convertASCIIBytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, j);

      paramArrayOfInt[0] -= j;

      break;
    case 870:
    case 871:
      if (paramInt3 > paramArrayOfChar.length - paramInt2) {
        paramInt3 = paramArrayOfChar.length - paramInt2;
      }
      i = CharacterSet.convertUTFBytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, paramArrayOfInt, true, paramInt3);

      break;
    case 873:
      if (paramInt3 > paramArrayOfChar.length - paramInt2) {
        paramInt3 = paramArrayOfChar.length - paramInt2;
      }
      i = CharacterSet.convertAL32UTF8BytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, paramArrayOfInt, true, paramInt3);

      break;
    case -1:
      i = dbCsBytesToJavaChars(paramArrayOfByte, paramInt1, paramArrayOfChar, paramInt2, paramArrayOfInt[0], paramCharacterSet1);

      paramArrayOfInt[0] = 0;

      break;
    default:
      CharacterSet localCharacterSet = paramCharacterSet3;

      if (paramBoolean) {
        localCharacterSet = paramCharacterSet2;
      }
      String str = localCharacterSet.toString(paramArrayOfByte, paramInt1, paramArrayOfInt[0]);
      char[] arrayOfChar = str.toCharArray();
      int k = arrayOfChar.length;

      if (k > paramInt3) {
        k = paramInt3;
      }
      System.arraycopy(arrayOfChar, 0, paramArrayOfChar, paramInt2, k);
    }

    return i;
  }

  public byte[] asciiBytesToCHARBytes(byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte = null;
    int i;
    int j;
    switch (this.clientCharSetId)
    {
    case -5:
      arrayOfByte = new byte[paramArrayOfByte.length * 2];

      i = 0; for (j = 0; i < paramArrayOfByte.length; )
      {
        arrayOfByte[(j++)] = 0;
        arrayOfByte[(j++)] = paramArrayOfByte[i];

        i++; continue;

        if (this.asciiCharSet == null) {
          this.asciiCharSet = CharacterSet.make(1);
        }
        try
        {
          arrayOfByte = this.serverCharSet.convert(this.asciiCharSet, paramArrayOfByte, 0, paramArrayOfByte.length);
        }
        catch (SQLException localSQLException)
        {
        }

        arrayOfByte = paramArrayOfByte;
      }

    case -1:
    }

    return arrayOfByte;
  }

  public int javaCharsToDbCsBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = javaCharsToDbCsBytes(paramArrayOfChar, 0, paramArrayOfByte, 0, paramInt);

    return i;
  }

  public int javaCharsToDbCsBytes(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SQLException
  {
    int i = 0;

    catchCharsLen(paramArrayOfChar, paramInt1, paramInt3);

    String str = new String(paramArrayOfChar, paramInt1, paramInt3);
    byte[] arrayOfByte = this.serverCharSet.convertWithReplacement(str);

    str = null;

    if (arrayOfByte != null)
    {
      i = arrayOfByte.length;

      catchBytesLen(paramArrayOfByte, paramInt2, i);
      System.arraycopy(arrayOfByte, 0, paramArrayOfByte, paramInt2, i);

      arrayOfByte = null;
    }

    return i;
  }

  static final int dbCsBytesToJavaChars(byte[] paramArrayOfByte, int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, CharacterSet paramCharacterSet)
    throws SQLException
  {
    int i = 0;

    catchBytesLen(paramArrayOfByte, paramInt1, paramInt3);

    String str = paramCharacterSet.toStringWithReplacement(paramArrayOfByte, paramInt1, paramInt3);

    if (str != null)
    {
      i = str.length();

      catchCharsLen(paramArrayOfChar, paramInt2, i);
      str.getChars(0, i, paramArrayOfChar, paramInt2);

      str = null;
    }

    return i;
  }

  public static final int javaCharsToUcs2Bytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    int i = javaCharsToUcs2Bytes(paramArrayOfChar, 0, paramArrayOfByte, 0, paramInt);

    return i;
  }

  public static final int javaCharsToUcs2Bytes(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    throws SQLException
  {
    catchCharsLen(paramArrayOfChar, paramInt1, paramInt3);
    catchBytesLen(paramArrayOfByte, paramInt2, paramInt3 * 2);

    int k = paramInt3 + paramInt1;

    int i = paramInt1; for (int j = paramInt2; i < k; i++)
    {
      paramArrayOfByte[(j++)] = (byte)(paramArrayOfChar[i] >> '\b' & 0xFF);
      paramArrayOfByte[(j++)] = (byte)(paramArrayOfChar[i] & 0xFF);
    }

    return j - paramInt2;
  }

  public static final int ucs2BytesToJavaChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    return CharacterSet.AL16UTF16BytesToJavaChars(paramArrayOfByte, paramInt, paramArrayOfChar);
  }

  public static final byte[] stringToAsciiBytes(String paramString)
  {
    return CharacterSet.stringToASCII(paramString);
  }

  public static final int asciiBytesToJavaChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
    throws SQLException
  {
    return CharacterSet.convertASCIIBytesToJavaChars(paramArrayOfByte, 0, paramArrayOfChar, 0, paramInt);
  }

  public static final int javaCharsToAsciiBytes(char[] paramArrayOfChar, int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    return CharacterSet.convertJavaCharsToASCIIBytes(paramArrayOfChar, 0, paramArrayOfByte, 0, paramInt);
  }

  public static final boolean isCharSetMultibyte(short paramShort)
  {
    switch (paramShort)
    {
    case 1:
    case 31:
      return false;
    case -5:
    case -1:
    case 870:
    case 871:
    case 873:
      return true;
    }

    return false;
  }

  public int getMaxCharbyteSize()
  {
    return _getMaxCharbyteSize(this.clientCharSetId);
  }

  public int getMaxNCharbyteSize()
  {
    return _getMaxCharbyteSize(this.serverNCharSetId);
  }

  public int _getMaxCharbyteSize(short paramShort)
  {
    switch (paramShort)
    {
    case 1:
      return 1;
    case 31:
      return 1;
    case 870:
    case 871:
      return 3;
    case -5:
    case 2000:
      return 2;
    case -1:
      return 4;
    case 873:
      return 4;
    }

    return 1;
  }

  public boolean isUcs2CharSet()
  {
    return this.clientCharSetId == -5;
  }

  public static final int RAWBytesToHexChars(byte[] paramArrayOfByte, int paramInt, char[] paramArrayOfChar)
  {
    int i = 0; for (int j = 0; i < paramInt; i++)
    {
      paramArrayOfChar[(j++)] = (char)RepConversion.nibbleToHex((byte)(paramArrayOfByte[i] >> 4 & 0xF));

      paramArrayOfChar[(j++)] = (char)RepConversion.nibbleToHex((byte)(paramArrayOfByte[i] & 0xF));
    }

    return j;
  }

  public InputStream ConvertStream(InputStream paramInputStream, int paramInt)
  {
    return new OracleConversionInputStream(this, paramInputStream, paramInt);
  }

  public InputStream ConvertStream(InputStream paramInputStream, int paramInt1, int paramInt2)
  {
    return new OracleConversionInputStream(this, paramInputStream, paramInt1, paramInt2);
  }

  public InputStream ConvertStream(Reader paramReader, int paramInt1, int paramInt2, short paramShort)
  {
    OracleConversionInputStream localOracleConversionInputStream = new OracleConversionInputStream(this, paramReader, paramInt1, paramInt2, paramShort);

    return localOracleConversionInputStream;
  }

  public Reader ConvertCharacterStream(InputStream paramInputStream, int paramInt)
    throws SQLException
  {
    return new OracleConversionReader(this, paramInputStream, paramInt);
  }

  public Reader ConvertCharacterStream(InputStream paramInputStream, int paramInt, short paramShort)
    throws SQLException
  {
    OracleConversionReader localOracleConversionReader = new OracleConversionReader(this, paramInputStream, paramInt);

    localOracleConversionReader.setFormOfUse(paramShort);

    return localOracleConversionReader;
  }

  public InputStream CharsToStream(char[] paramArrayOfChar, int paramInt1, int paramInt2, int paramInt3)
    throws SQLException
  {
    if (paramInt3 == 10) {
      return new AsciiStream(paramArrayOfChar, paramInt1, paramInt2);
    }
    if (paramInt3 == 11) {
      return new UnicodeStream(paramArrayOfChar, paramInt1, paramInt2);
    }
    DatabaseError.throwSqlException(39, "unknownConversion");

    return null;
  }

  static final void unexpectedCharset(short paramShort)
    throws SQLException
  {
    DatabaseError.throwSqlException(35, "DBConversion");
  }

  protected static final void catchBytesLen(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt1 + paramInt2 > paramArrayOfByte.length)
    {
      DatabaseError.throwSqlException(39, "catchBytesLen");
    }
  }

  protected static final void catchCharsLen(char[] paramArrayOfChar, int paramInt1, int paramInt2)
    throws SQLException
  {
    if (paramInt1 + paramInt2 > paramArrayOfChar.length)
    {
      DatabaseError.throwSqlException(39, "catchCharsLen");
    }
  }

  public static final int getUtfLen(char paramChar)
  {
    int i = 0;

    if ((paramChar & 0xFF80) == 0)
    {
      i = 1;
    }
    else if ((paramChar & 0xF800) == 0)
    {
      i = 2;
    }
    else
    {
      i = 3;
    }

    return i;
  }

  int encodedByteLength(String paramString, boolean paramBoolean)
  {
    int i = 0;
    if (paramString != null)
    {
      i = paramString.length();
      if (i != 0)
      {
        if (paramBoolean)
        {
          i = this.isServerNCharSetFixedWidth ? i * this.maxNCharSize : this.serverNCharSet.encodedByteLength(paramString);
        }
        else
        {
          i = this.isServerCharSetFixedWidth ? i * this.sMaxCharSize : this.serverCharSet.encodedByteLength(paramString);
        }
      }
    }
    return i;
  }

  int encodedByteLength(char[] paramArrayOfChar, boolean paramBoolean)
  {
    int i = 0;
    if (paramArrayOfChar != null)
    {
      i = paramArrayOfChar.length;
      if (i != 0)
      {
        if (paramBoolean)
        {
          i = this.isServerNCharSetFixedWidth ? i * this.maxNCharSize : this.serverNCharSet.encodedByteLength(paramArrayOfChar);
        }
        else
        {
          i = this.isServerCharSetFixedWidth ? i * this.sMaxCharSize : this.serverCharSet.encodedByteLength(paramArrayOfChar);
        }
      }
    }
    return i;
  }

  class UnicodeStream extends OracleBufferedStream
  {
    UnicodeStream(char[] paramInt1, int paramInt2, int arg4)
    {
      super();

      int j = paramInt2; for (int k = 0; k < i; )
      {
        int m = paramInt1[(j++)];

        this.buf[(k++)] = (byte)(m >> 8 & 0xFF);
        this.buf[(k++)] = (byte)(m & 0xFF);
      }

      this.count = i;
    }

    public boolean needBytes()
    {
      return (!this.closed) && (this.pos < this.count);
    }
  }

  class AsciiStream extends OracleBufferedStream
  {
    AsciiStream(char[] paramInt1, int paramInt2, int arg4)
    {
      super();

      int j = paramInt2; for (int k = 0; k < i; k++) {
        this.buf[k] = (byte)paramInt1[(j++)];
      }
      this.count = i;
    }

    public boolean needBytes()
    {
      return (!this.closed) && (this.pos < this.count);
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.DBConversion
 * JD-Core Version:    0.6.0
 */