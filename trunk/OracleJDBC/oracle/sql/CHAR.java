package oracle.sql;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import oracle.jdbc.driver.DatabaseError;

public class CHAR extends Datum
{
  public static final CharacterSet DEFAULT_CHARSET = CharacterSet.make(-1);
  private CharacterSet charSet;
  private int oracleId;
  private static final byte[] empty = new byte[0];

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:46_PDT_2005";

  protected CHAR()
  {
  }

  public CHAR(byte[] paramArrayOfByte, CharacterSet paramCharacterSet)
  {
    setValue(paramArrayOfByte, paramCharacterSet);
  }

  public CHAR(byte[] paramArrayOfByte, int paramInt1, int paramInt2, CharacterSet paramCharacterSet)
  {
    byte[] arrayOfByte = new byte[paramInt2];

    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    setValue(arrayOfByte, paramCharacterSet);
  }

  public CHAR(String paramString, CharacterSet paramCharacterSet)
    throws SQLException
  {
    if (paramCharacterSet == null)
    {
      paramCharacterSet = DEFAULT_CHARSET;
    }

    setValue(paramCharacterSet.convertWithReplacement(paramString), paramCharacterSet);
  }

  public CHAR(Object paramObject, CharacterSet paramCharacterSet)
    throws SQLException
  {
    this(paramObject.toString(), paramCharacterSet);
  }

  public CharacterSet getCharacterSet()
  {
    if (this.charSet == null)
    {
      if (this.oracleId == 0)
      {
        this.oracleId = -1;
      }

      if ((DEFAULT_CHARSET != null) && ((this.oracleId == -1) || (this.oracleId == DEFAULT_CHARSET.getOracleId())))
      {
        this.charSet = DEFAULT_CHARSET;
      }
      else this.charSet = CharacterSet.make(this.oracleId);
    }

    return this.charSet;
  }

  public int oracleId()
  {
    return this.oracleId;
  }

  public String getString()
    throws SQLException
  {
    String str = getCharacterSet().toString(shareBytes(), 0, (int)getLength());

    return str;
  }

  public String getStringWithReplacement()
  {
    byte[] arrayOfByte = shareBytes();
    String str = getCharacterSet().toStringWithReplacement(arrayOfByte, 0, arrayOfByte.length);

    return str;
  }

  public String toString()
  {
    return getStringWithReplacement();
  }

  public boolean equals(Object paramObject)
  {
    return ((paramObject instanceof CHAR)) && (getCharacterSet().equals(((CHAR)paramObject).getCharacterSet())) && (super.equals(paramObject));
  }

  void setValue(byte[] paramArrayOfByte, CharacterSet paramCharacterSet)
  {
    this.charSet = (paramCharacterSet == null ? DEFAULT_CHARSET : paramCharacterSet);
    this.oracleId = this.charSet.getOracleId();

    setShareBytes(paramArrayOfByte == null ? empty : paramArrayOfByte);
  }

  public Object toJdbc()
    throws SQLException
  {
    return stringValue();
  }

  public boolean isConvertibleTo(Class paramClass)
  {
    String str = paramClass.getName();

    return (str.compareTo("java.lang.String") == 0) || (str.compareTo("java.lang.Long") == 0) || (str.compareTo("java.math.BigDecimal") == 0) || (str.compareTo("java.io.InputStream") == 0) || (str.compareTo("java.sql.Date") == 0) || (str.compareTo("java.sql.Time") == 0) || (str.compareTo("java.sql.Timestamp") == 0) || (str.compareTo("java.io.Reader") == 0);
  }

  public String stringValue()
  {
    return toString();
  }

  public boolean booleanValue()
    throws SQLException
  {
    int i = 0;

    String str = stringValue();

    if (str == null)
    {
      i = 0;
    }
    else if ((str.length() == 1) && (str.charAt(0) == '0'))
    {
      i = 0;
    }
    else
    {
      i = 1;
    }

    return i;
  }

  public int intValue()
    throws SQLException
  {
    long l = longValue();

    if ((l > 2147483647L) || (l < -2147483648L))
    {
      DatabaseError.throwSqlException(26);
    }

    int i = (int)l;

    return i;
  }

  public long longValue()
    throws SQLException
  {
    long l = 0L;
    try
    {
      l = Long.valueOf(stringValue()).longValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      DatabaseError.throwSqlException(59);
    }

    return l;
  }

  public float floatValue()
    throws SQLException
  {
    float f = 0.0F;
    try
    {
      f = Float.valueOf(stringValue()).floatValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      DatabaseError.throwSqlException(59);
    }

    return f;
  }

  public double doubleValue()
    throws SQLException
  {
    double d = 0.0D;
    try
    {
      d = Double.valueOf(stringValue()).doubleValue();
    }
    catch (NumberFormatException localNumberFormatException)
    {
      DatabaseError.throwSqlException(59);
    }

    return d;
  }

  public byte byteValue()
    throws SQLException
  {
    long l = longValue();

    if ((l > 127L) || (l < -128L))
    {
      DatabaseError.throwSqlException(26);
    }

    int i = (byte)(int)l;

    return i;
  }

  public Date dateValue()
    throws SQLException
  {
    Date localDate = Date.valueOf(stringValue());

    return localDate;
  }

  public Time timeValue()
    throws SQLException
  {
    Time localTime = Time.valueOf(stringValue());

    return localTime;
  }

  public Timestamp timestampValue()
    throws SQLException
  {
    Timestamp localTimestamp = Timestamp.valueOf(stringValue());

    return localTimestamp;
  }

  public BigDecimal bigDecimalValue()
    throws SQLException
  {
    BigDecimal localBigDecimal = null;
    try
    {
      localBigDecimal = new BigDecimal(stringValue());
    }
    catch (NumberFormatException localNumberFormatException)
    {
      DatabaseError.throwSqlException(12, "bigDecimalValue");
    }

    return localBigDecimal;
  }

  public Reader characterStreamValue()
    throws SQLException
  {
    StringReader localStringReader = new StringReader(getString());

    return localStringReader;
  }

  public InputStream asciiStreamValue()
    throws SQLException
  {
    return getStream();
  }

  public InputStream binaryStreamValue()
    throws SQLException
  {
    return getStream();
  }

  public Object makeJdbcArray(int paramInt)
  {
    return new String[paramInt];
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.sql.CHAR
 * JD-Core Version:    0.6.0
 */