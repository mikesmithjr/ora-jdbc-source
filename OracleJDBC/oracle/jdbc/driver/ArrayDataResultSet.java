package oracle.jdbc.driver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.CustomDatumFactory;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.ORADataFactory;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;

public class ArrayDataResultSet extends BaseResultSet
{
  Datum[] data;
  Map map;
  private int currentIndex;
  private int lastIndex;
  boolean closed;
  PhysicalConnection connection;
  private Boolean wasNull;
  private static Boolean BOOLEAN_TRUE = new Boolean(true);
  private static Boolean BOOLEAN_FALSE = new Boolean(false);
  private int fetchSize;
  ARRAY array;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:49_PDT_2005";

  public ArrayDataResultSet(OracleConnection paramOracleConnection, Datum[] paramArrayOfDatum, Map paramMap)
    throws SQLException
  {
    this.connection = ((PhysicalConnection)paramOracleConnection);
    this.data = paramArrayOfDatum;
    this.map = paramMap;
    this.currentIndex = 0;
    this.lastIndex = (this.data == null ? 0 : this.data.length);
    this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
  }

  public ArrayDataResultSet(OracleConnection paramOracleConnection, Datum[] paramArrayOfDatum, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    this.connection = ((PhysicalConnection)paramOracleConnection);
    this.data = paramArrayOfDatum;
    this.map = paramMap;
    this.currentIndex = ((int)paramLong - 1);

    int i = this.data == null ? 0 : this.data.length;

    this.lastIndex = (this.currentIndex + Math.min(i - this.currentIndex, paramInt));

    this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
  }

  public ArrayDataResultSet(OracleConnection paramOracleConnection, ARRAY paramARRAY, long paramLong, int paramInt, Map paramMap)
    throws SQLException
  {
    this.connection = ((PhysicalConnection)paramOracleConnection);
    this.array = paramARRAY;
    this.map = paramMap;
    this.currentIndex = ((int)paramLong - 1);

    int i = this.array == null ? 0 : paramARRAY.length();

    this.lastIndex = (this.currentIndex + (paramInt == -1 ? i - this.currentIndex : Math.min(i - this.currentIndex, paramInt)));

    this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
  }

  public boolean next()
    throws SQLException
  {
    if (this.closed) {
      DatabaseError.throwSqlException(10, "next");
    }

    this.currentIndex += 1;

    return this.currentIndex <= this.lastIndex;
  }

  public synchronized void close()
    throws SQLException
  {
    this.closed = true;
  }

  public synchronized boolean wasNull()
    throws SQLException
  {
    if (this.wasNull == null) {
      DatabaseError.throwSqlException(24, null);
    }
    return this.wasNull.booleanValue();
  }

  public synchronized String getString(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null) {
      return localDatum.stringValue();
    }
    return null;
  }

  public synchronized ResultSet getCursor(int paramInt)
    throws SQLException
  {
    DatabaseError.throwSqlException(4, "getCursor");

    return null;
  }

  public synchronized Datum getOracleObject(int paramInt)
    throws SQLException
  {
    if (this.currentIndex <= 0) {
      DatabaseError.throwSqlException(14, null);
    }

    if (paramInt == 1)
    {
      this.wasNull = BOOLEAN_FALSE;

      return new NUMBER(this.currentIndex);
    }
    if (paramInt == 2)
    {
      if (this.data != null)
      {
        this.wasNull = (this.data[(this.currentIndex - 1)] == null ? BOOLEAN_TRUE : BOOLEAN_FALSE);

        return this.data[(this.currentIndex - 1)];
      }
      if (this.array != null)
      {
        Datum[] arrayOfDatum = this.array.getOracleArray(this.currentIndex, 1);

        if ((arrayOfDatum != null) && (arrayOfDatum.length >= 1))
        {
          this.wasNull = (arrayOfDatum[0] == null ? BOOLEAN_TRUE : BOOLEAN_FALSE);

          return arrayOfDatum[0];
        }
      }

      DatabaseError.throwSqlException(1, "Out of sync");
    }

    DatabaseError.throwSqlException(3, null);

    return null;
  }

  public synchronized ROWID getROWID(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof ROWID)) {
        return (ROWID)localDatum;
      }
      DatabaseError.throwSqlException(4, "getROWID");
    }

    return null;
  }

  public synchronized NUMBER getNUMBER(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof NUMBER)) {
        return (NUMBER)localDatum;
      }
      DatabaseError.throwSqlException(4, "getNUMBER");
    }

    return null;
  }

  public synchronized DATE getDATE(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof DATE)) {
        return (DATE)localDatum;
      }
      DatabaseError.throwSqlException(4, "getDATE");
    }

    return null;
  }

  public synchronized ARRAY getARRAY(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof ARRAY)) {
        return (ARRAY)localDatum;
      }
      DatabaseError.throwSqlException(4, "getARRAY");
    }

    return null;
  }

  public synchronized STRUCT getSTRUCT(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof STRUCT)) {
        return (STRUCT)localDatum;
      }
      DatabaseError.throwSqlException(4, "getSTRUCT");
    }

    return null;
  }

  public synchronized OPAQUE getOPAQUE(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof OPAQUE)) {
        return (OPAQUE)localDatum;
      }
      DatabaseError.throwSqlException(4, "getOPAQUE");
    }

    return null;
  }

  public synchronized REF getREF(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof REF)) {
        return (REF)localDatum;
      }
      DatabaseError.throwSqlException(4, "getREF");
    }

    return null;
  }

  public synchronized CHAR getCHAR(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof CHAR)) {
        return (CHAR)localDatum;
      }
      DatabaseError.throwSqlException(4, "getCHAR");
    }

    return null;
  }

  public synchronized RAW getRAW(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof RAW)) {
        return (RAW)localDatum;
      }
      DatabaseError.throwSqlException(4, "getRAW");
    }

    return null;
  }

  public synchronized BLOB getBLOB(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof BLOB)) {
        return (BLOB)localDatum;
      }
      DatabaseError.throwSqlException(4, "getBLOB");
    }

    return null;
  }

  public synchronized CLOB getCLOB(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof CLOB)) {
        return (CLOB)localDatum;
      }
      DatabaseError.throwSqlException(4, "getCLOB");
    }

    return null;
  }

  public synchronized BFILE getBFILE(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof BFILE)) {
        return (BFILE)localDatum;
      }
      DatabaseError.throwSqlException(4, "getBFILE");
    }

    return null;
  }

  public synchronized BFILE getBfile(int paramInt)
    throws SQLException
  {
    return getBFILE(paramInt);
  }

  public synchronized boolean getBoolean(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null) {
      return localDatum.booleanValue();
    }
    return false;
  }

  public synchronized byte getByte(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null) {
      return localDatum.byteValue();
    }
    return 0;
  }

  public synchronized short getShort(int paramInt)
    throws SQLException
  {
    long l = getLong(paramInt);

    if ((l > 65537L) || (l < -65538L)) {
      DatabaseError.throwSqlException(26, "getShort");
    }

    return (short)(int)l;
  }

  public synchronized int getInt(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.intValue();
    }

    return 0;
  }

  public synchronized long getLong(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.longValue();
    }

    return 0L;
  }

  public synchronized float getFloat(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.floatValue();
    }

    return 0.0F;
  }

  public synchronized double getDouble(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.doubleValue();
    }

    return 0.0D;
  }

  public synchronized BigDecimal getBigDecimal(int paramInt1, int paramInt2)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt1);

    if (localDatum != null)
    {
      return localDatum.bigDecimalValue();
    }

    return null;
  }

  public synchronized byte[] getBytes(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof RAW)) {
        return ((RAW)localDatum).shareBytes();
      }
      DatabaseError.throwSqlException(4, "getBytes");
    }

    return null;
  }

  public synchronized Date getDate(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.dateValue();
    }

    return null;
  }

  public synchronized Time getTime(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.timeValue();
    }

    return null;
  }

  public synchronized Timestamp getTimestamp(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.timestampValue();
    }

    return null;
  }

  public synchronized InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      localDatum.asciiStreamValue();
    }

    return null;
  }

  public synchronized InputStream getUnicodeStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      DBConversion localDBConversion = this.connection.conversion;
      byte[] arrayOfByte = localDatum.shareBytes();

      if ((localDatum instanceof RAW))
      {
        return localDBConversion.ConvertStream(new ByteArrayInputStream(arrayOfByte), 3);
      }

      if ((localDatum instanceof CHAR))
      {
        return localDBConversion.ConvertStream(new ByteArrayInputStream(arrayOfByte), 1);
      }

      DatabaseError.throwSqlException(4, "getUnicodeStream");
    }

    return null;
  }

  public synchronized InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.binaryStreamValue();
    }

    return null;
  }

  public synchronized Object getObject(int paramInt)
    throws SQLException
  {
    return getObject(paramInt, this.map);
  }

  /** @deprecated */
  public synchronized CustomDatum getCustomDatum(int paramInt, CustomDatumFactory paramCustomDatumFactory)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    return paramCustomDatumFactory.create(localDatum, 0);
  }

  public synchronized ORAData getORAData(int paramInt, ORADataFactory paramORADataFactory)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    return paramORADataFactory.create(localDatum, 0);
  }

  public synchronized ResultSetMetaData getMetaData()
    throws SQLException
  {
    if (this.closed) {
      DatabaseError.throwSqlException(10, "getMetaData");
    }

    DatabaseError.throwSqlException(23, "getMetaData");

    return null;
  }

  public synchronized int findColumn(String paramString)
    throws SQLException
  {
    if (paramString.equalsIgnoreCase("index"))
      return 1;
    if (paramString.equalsIgnoreCase("value")) {
      return 2;
    }
    DatabaseError.throwSqlException(6, "get_column_index");

    return 0;
  }

  public synchronized Statement getStatement()
    throws SQLException
  {
    return null;
  }

  public synchronized Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      if ((localDatum instanceof STRUCT)) {
        return ((STRUCT)localDatum).toJdbc(paramMap);
      }
      return localDatum.toJdbc();
    }

    return null;
  }

  public synchronized Ref getRef(int paramInt)
    throws SQLException
  {
    return getREF(paramInt);
  }

  public synchronized Blob getBlob(int paramInt)
    throws SQLException
  {
    return getBLOB(paramInt);
  }

  public synchronized Clob getClob(int paramInt)
    throws SQLException
  {
    return getCLOB(paramInt);
  }

  public synchronized Array getArray(int paramInt)
    throws SQLException
  {
    return getARRAY(paramInt);
  }

  public synchronized Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.characterStreamValue();
    }

    return null;
  }

  public synchronized BigDecimal getBigDecimal(int paramInt)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      return localDatum.bigDecimalValue();
    }

    return null;
  }

  public synchronized Date getDate(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      DATE localDATE = null;

      if ((localDatum instanceof DATE))
        localDATE = (DATE)localDatum;
      else {
        localDATE = new DATE(localDatum.stringValue());
      }
      if (localDATE != null) {
        return localDATE.dateValue(paramCalendar);
      }
    }
    return null;
  }

  public synchronized Time getTime(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      DATE localDATE = null;

      if ((localDatum instanceof DATE))
        localDATE = (DATE)localDatum;
      else {
        localDATE = new DATE(localDatum.stringValue());
      }
      if (localDATE != null) {
        return localDATE.timeValue(paramCalendar);
      }
    }
    return null;
  }

  public synchronized Timestamp getTimestamp(int paramInt, Calendar paramCalendar)
    throws SQLException
  {
    Datum localDatum = getOracleObject(paramInt);

    if (localDatum != null)
    {
      DATE localDATE = null;

      if ((localDatum instanceof DATE))
        localDATE = (DATE)localDatum;
      else {
        localDATE = new DATE(localDatum.stringValue());
      }
      if (localDATE != null) {
        return localDATE.timestampValue(paramCalendar);
      }
    }
    return null;
  }

  public synchronized URL getURL(int paramInt)
    throws SQLException
  {
    throw new SQLException("Conversion to java.net.URL not supported.");
  }

  public boolean isBeforeFirst()
    throws SQLException
  {
    return this.currentIndex < 1;
  }

  public boolean isAfterLast()
    throws SQLException
  {
    return this.currentIndex > this.lastIndex;
  }

  public boolean isFirst()
    throws SQLException
  {
    return this.currentIndex == 1;
  }

  public boolean isLast()
    throws SQLException
  {
    return this.currentIndex == this.lastIndex;
  }

  public int getRow()
    throws SQLException
  {
    return this.currentIndex;
  }

  public void setFetchSize(int paramInt)
    throws SQLException
  {
    if (paramInt < 0)
      DatabaseError.throwSqlException(68);
    else if (paramInt == 0)
      this.fetchSize = OracleConnection.DEFAULT_ROW_PREFETCH;
    else
      this.fetchSize = paramInt;
  }

  public int getFetchSize()
    throws SQLException
  {
    return this.fetchSize;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ArrayDataResultSet
 * JD-Core Version:    0.6.0
 */