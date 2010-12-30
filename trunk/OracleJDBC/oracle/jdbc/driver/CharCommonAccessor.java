package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.Datum;

abstract class CharCommonAccessor extends Accessor
{
  int internalMaxLengthNewer;
  int internalMaxLengthOlder;
  static final int MAX_NB_CHAR_PLSQL = 32512;

  void setOffsets(int paramInt)
  {
    this.columnIndex = this.statement.defineCharSubRange;
    this.statement.defineCharSubRange = (this.columnIndex + paramInt * this.charLength);
  }

  void init(OracleStatement paramOracleStatement, int paramInt1, int paramInt2, int paramInt3, short paramShort, int paramInt4, boolean paramBoolean, int paramInt5, int paramInt6)
    throws SQLException
  {
    if (paramBoolean)
    {
      if (paramInt1 != 23) {
        paramInt1 = 1;
      }
      if ((paramInt3 == -1) || (paramInt3 < paramOracleStatement.maxFieldSize)) {
        paramInt3 = paramOracleStatement.maxFieldSize;
      }
    }
    init(paramOracleStatement, paramInt1, paramInt2, paramShort, paramBoolean);

    if ((paramBoolean) && (paramOracleStatement.connection.defaultNChar)) {
      this.formOfUse = 2;
    }
    this.internalMaxLengthNewer = paramInt5;
    this.internalMaxLengthOlder = paramInt6;

    initForDataAccess(paramInt4, paramInt3, null);
  }

  void init(OracleStatement paramOracleStatement, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, short paramShort, int paramInt9, int paramInt10)
    throws SQLException
  {
    init(paramOracleStatement, paramInt1, paramInt2, paramShort, false);
    initForDescribe(paramInt1, paramInt3, paramBoolean, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramShort, null);

    int i = paramOracleStatement.maxFieldSize;

    if ((i != 0) && (i <= paramInt3)) {
      paramInt3 = i;
    }
    this.internalMaxLengthNewer = paramInt9;
    this.internalMaxLengthOlder = paramInt10;

    initForDataAccess(0, paramInt3, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    if (this.statement.connection.getVersionNumber() >= 8000)
      this.internalTypeMaxLength = this.internalMaxLengthNewer;
    else {
      this.internalTypeMaxLength = this.internalMaxLengthOlder;
    }
    if ((paramInt2 > 0) && (paramInt2 < this.internalTypeMaxLength)) {
      this.internalTypeMaxLength = paramInt2;
    }
    this.charLength = (this.internalTypeMaxLength + 1);
  }

  int getInt(int paramInt)
    throws SQLException
  {
    int i = 0;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        i = Integer.parseInt(getString(paramInt).trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return i;
  }

  boolean getBoolean(int paramInt)
    throws SQLException
  {
    BigDecimal localBigDecimal = getBigDecimal(paramInt);

    return (localBigDecimal != null) && (localBigDecimal.signum() != 0);
  }

  short getShort(int paramInt)
    throws SQLException
  {
    int i = 0;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        i = Short.parseShort(getString(paramInt).trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return i;
  }

  byte getByte(int paramInt)
    throws SQLException
  {
    int i = 0;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        i = Byte.parseByte(getString(paramInt).trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return i;
  }

  long getLong(int paramInt)
    throws SQLException
  {
    long l = 0L;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        l = Long.parseLong(getString(paramInt).trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return l;
  }

  float getFloat(int paramInt)
    throws SQLException
  {
    float f = 0.0F;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        f = Float.parseFloat(getString(paramInt).trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return f;
  }

  double getDouble(int paramInt)
    throws SQLException
  {
    double d = 0.0D;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        d = Double.parseDouble(getString(paramInt).trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return d;
  }

  BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    BigDecimal localBigDecimal = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        String str = getString(paramInt);

        if (str != null)
          localBigDecimal = new BigDecimal(str.trim());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        DatabaseError.throwSqlException(59);
      }

    }

    return localBigDecimal;
  }

  BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    BigDecimal localBigDecimal = getBigDecimal(paramInt1);

    if (localBigDecimal != null) {
      localBigDecimal.setScale(paramInt2, 6);
    }
    return localBigDecimal;
  }

  String getString(int paramInt)
    throws SQLException
  {
    String str = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.charLength * paramInt;
      int j = this.rowSpaceChar[i] >> '\001';

      if (j > this.internalTypeMaxLength) {
        j = this.internalTypeMaxLength;
      }
      str = new String(this.rowSpaceChar, i + 1, j);
    }

    return str;
  }

  Date getDate(int paramInt)
    throws SQLException
  {
    Date localDate = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      localDate = Date.valueOf(getString(paramInt).trim());
    }

    return localDate;
  }

  Time getTime(int paramInt)
    throws SQLException
  {
    Time localTime = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      localTime = Time.valueOf(getString(paramInt).trim());
    }

    return localTime;
  }

  Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    Timestamp localTimestamp = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      localTimestamp = Timestamp.valueOf(getString(paramInt).trim());
    }

    return localTimestamp;
  }

  byte[] getBytes(int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte1 = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.charLength * paramInt;
      int j = this.rowSpaceChar[i] >> '\001';

      if (j > this.internalTypeMaxLength) {
        j = this.internalTypeMaxLength;
      }
      DBConversion localDBConversion = this.statement.connection.conversion;

      byte[] arrayOfByte2 = new byte[j * 6];
      int k = this.formOfUse == 2 ? localDBConversion.javaCharsToNCHARBytes(this.rowSpaceChar, i + 1, arrayOfByte2, 0, j) : localDBConversion.javaCharsToCHARBytes(this.rowSpaceChar, i + 1, arrayOfByte2, 0, j);

      arrayOfByte1 = new byte[k];

      System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, k);
    }

    return arrayOfByte1;
  }

  InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.charLength * paramInt;
      int j = this.rowSpaceChar[i] >> '\001';

      if (j > this.internalTypeMaxLength) {
        j = this.internalTypeMaxLength;
      }
      PhysicalConnection localPhysicalConnection = this.statement.connection;

      localInputStream = localPhysicalConnection.conversion.CharsToStream(this.rowSpaceChar, i + 1, j, 10);
    }

    return localInputStream;
  }

  InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    InputStream localInputStream = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.charLength * paramInt;
      int j = this.rowSpaceChar[i] >> '\001';

      if (j > this.internalTypeMaxLength) {
        j = this.internalTypeMaxLength;
      }
      PhysicalConnection localPhysicalConnection = this.statement.connection;

      localInputStream = localPhysicalConnection.conversion.CharsToStream(this.rowSpaceChar, i + 1, j << 1, 11);
    }

    return localInputStream;
  }

  Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    CharArrayReader localCharArrayReader = null;
    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.charLength * paramInt;
      int j = this.rowSpaceChar[i] >> '\001';

      if (j > this.internalTypeMaxLength) {
        j = this.internalTypeMaxLength;
      }
      localCharArrayReader = new CharArrayReader(this.rowSpaceChar, i + 1, j);
    }
    return localCharArrayReader;
  }

  InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    ByteArrayInputStream localByteArrayInputStream = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.charLength * paramInt;
      int j = this.rowSpaceChar[i] >> '\001';

      if (j > this.internalTypeMaxLength) {
        j = this.internalTypeMaxLength;
      }
      DBConversion localDBConversion = this.statement.connection.conversion;

      byte[] arrayOfByte = new byte[j * 6];
      int k = this.formOfUse == 2 ? localDBConversion.javaCharsToNCHARBytes(this.rowSpaceChar, i + 1, arrayOfByte, 0, j) : localDBConversion.javaCharsToCHARBytes(this.rowSpaceChar, i + 1, arrayOfByte, 0, j);

      localByteArrayInputStream = new ByteArrayInputStream(arrayOfByte, 0, k);
    }

    return localByteArrayInputStream;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getString(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getString(paramInt);
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getCHAR(paramInt);
  }

  CHAR getCHAR(int paramInt)
    throws SQLException
  {
    byte[] arrayOfByte = getBytes(paramInt);

    if ((arrayOfByte == null) || (arrayOfByte.length == 0))
    {
      return null;
    }
    CharacterSet localCharacterSet;
    if (this.formOfUse == 2)
    {
      localCharacterSet = this.statement.connection.conversion.getDriverNCharSetObj();
    }
    else
    {
      localCharacterSet = this.statement.connection.conversion.getDriverCharSetObj();
    }

    return new CHAR(arrayOfByte, localCharacterSet);
  }

  URL getURL(int paramInt)
    throws SQLException
  {
    URL localURL = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      try
      {
        localURL = new URL(getString(paramInt));
      }
      catch (MalformedURLException localMalformedURLException)
      {
        DatabaseError.throwSqlException(136);
      }
    }

    return localURL;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.CharCommonAccessor
 * JD-Core Version:    0.6.0
 */