package oracle.sql;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import oracle.jdbc.driver.DBConversion;
import oracle.jdbc.driver.DatabaseError;
import oracle.jdbc.util.RepConversion;

public class RAW extends Datum
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:47_PDT_2005";

  static int hexDigit2Nibble(char paramChar)
    throws SQLException
  {
    int i = Character.digit(paramChar, 16);

    if (i == -1)
    {
      throw new SQLException("Invalid hex digit: " + paramChar);
    }

    return i;
  }

  public static byte[] hexString2Bytes(String paramString)
    throws SQLException
  {
    int i = paramString.length();
    char[] arrayOfChar = new char[i];

    paramString.getChars(0, i, arrayOfChar, 0);

    int j = 0;
    int k = 0;

    if (i == 0)
      return new byte[0];
    byte[] arrayOfByte;
    if (i % 2 > 0)
    {
      arrayOfByte = new byte[(i + 1) / 2];
      arrayOfByte[(j++)] = (byte)hexDigit2Nibble(arrayOfChar[(k++)]);
    }
    else
    {
      arrayOfByte = new byte[i / 2];
    }

    for (; j < arrayOfByte.length; j++)
    {
      arrayOfByte[j] = (byte)(hexDigit2Nibble(arrayOfChar[(k++)]) << 4 | hexDigit2Nibble(arrayOfChar[(k++)]));
    }

    return arrayOfByte;
  }

  public static RAW newRAW(Object paramObject)
    throws SQLException
  {
    RAW localRAW = new RAW(paramObject);

    return localRAW;
  }

  public static RAW oldRAW(Object paramObject)
    throws SQLException
  {
    RAW localRAW;
    if ((paramObject instanceof String))
    {
      String str = (String)paramObject;
      byte[] arrayOfByte = null;
      try
      {
        arrayOfByte = str.getBytes("ISO8859_1");
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        DatabaseError.throwSqlException(109, "ISO8859_1 character encoding not found");
      }

      localRAW = new RAW(arrayOfByte);
    }
    else
    {
      localRAW = new RAW(paramObject);
    }

    return localRAW;
  }

  public RAW()
  {
  }

  public RAW(byte[] paramArrayOfByte)
  {
    super(paramArrayOfByte);
  }

  /** @deprecated */
  public RAW(Object paramObject)
    throws SQLException
  {
    this();

    if ((paramObject instanceof byte[]))
    {
      setShareBytes((byte[])paramObject);
    }
    else if ((paramObject instanceof String))
    {
      setShareBytes(hexString2Bytes((String)paramObject));
    }
    else
    {
      DatabaseError.throwSqlException(59, paramObject);
    }
  }

  public Object toJdbc()
    throws SQLException
  {
    return getBytes();
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    String str = paramClass.getName();

    return (str.compareTo("java.lang.String") == 0) || (str.compareTo("java.io.InputStream") == 0) || (str.compareTo("java.io.Reader") == 0);
  }

  public String stringValue()
  {
    String str = RepConversion.bArray2String(getBytes());

    return str;
  }

  public Reader characterStreamValue()
    throws SQLException
  {
    int i = (int)getLength();
    char[] arrayOfChar = new char[i * 2];
    byte[] arrayOfByte = shareBytes();

    DBConversion.RAWBytesToHexChars(arrayOfByte, i, arrayOfChar);

    CharArrayReader localCharArrayReader = new CharArrayReader(arrayOfChar);

    return localCharArrayReader;
  }

  public InputStream asciiStreamValue()
    throws SQLException
  {
    int i = (int)getLength();
    char[] arrayOfChar = new char[i * 2];
    byte[] arrayOfByte1 = shareBytes();

    DBConversion.RAWBytesToHexChars(arrayOfByte1, i, arrayOfChar);

    byte[] arrayOfByte2 = new byte[i * 2];

    DBConversion.javaCharsToAsciiBytes(arrayOfChar, i * 2, arrayOfByte2);

    ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte2);

    return localByteArrayInputStream;
  }

  public InputStream binaryStreamValue()
    throws SQLException
  {
    return getStream();
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new byte[paramInt][];
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.RAW
 * JD-Core Version:    0.6.0
 */