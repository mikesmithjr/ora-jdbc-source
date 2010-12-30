package oracle.jdbc.driver;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import oracle.sql.ARRAY;
import oracle.sql.BFILE;
import oracle.sql.BLOB;
import oracle.sql.CHAR;
import oracle.sql.CLOB;
import oracle.sql.CustomDatum;
import oracle.sql.DATE;
import oracle.sql.Datum;
import oracle.sql.INTERVALDS;
import oracle.sql.INTERVALYM;
import oracle.sql.NUMBER;
import oracle.sql.OPAQUE;
import oracle.sql.ORAData;
import oracle.sql.RAW;
import oracle.sql.REF;
import oracle.sql.ROWID;
import oracle.sql.STRUCT;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

abstract class BaseResultSet extends OracleResultSet
{
  SQLWarning sqlWarning = null;
  boolean autoRefetch = true;

  boolean close_statement_on_close = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:50_PDT_2005";

  public synchronized String getCursorName()
    throws SQLException
  {
    DatabaseError.throwSqlException(23, "getCursorName");

    return null;
  }

  public void closeStatementOnClose()
  {
    this.close_statement_on_close = true;
  }

  public void beforeFirst()
    throws SQLException
  {
    DatabaseError.throwSqlException(75, "beforeFirst");
  }

  public void afterLast()
    throws SQLException
  {
    DatabaseError.throwSqlException(75, "afterLast");
  }

  public boolean first()
    throws SQLException
  {
    DatabaseError.throwSqlException(75, "first");

    return false;
  }

  public boolean last() throws SQLException
  {
    DatabaseError.throwSqlException(75, "last");

    return false;
  }

  public boolean absolute(int paramInt) throws SQLException
  {
    DatabaseError.throwSqlException(75, "absolute");

    return false;
  }

  public boolean relative(int paramInt) throws SQLException
  {
    DatabaseError.throwSqlException(75, "relative");

    return false;
  }

  public boolean previous() throws SQLException
  {
    DatabaseError.throwSqlException(75, "previous");

    return false;
  }

  public void setFetchDirection(int paramInt)
    throws SQLException
  {
    if (paramInt == 1000)
      return;
    if ((paramInt == 1001) || (paramInt == 1002)) {
      DatabaseError.throwSqlException(75, "setFetchDirection(FETCH_REVERSE, FETCH_UNKNOWN)");
    }
    else
      DatabaseError.throwSqlException(68, "setFetchDirection");
  }

  public int getFetchDirection()
    throws SQLException
  {
    return 1000;
  }

  public int getType()
    throws SQLException
  {
    return 1003;
  }

  public int getConcurrency()
    throws SQLException
  {
    return 1007;
  }

  public SQLWarning getWarnings()
    throws SQLException
  {
    return this.sqlWarning;
  }

  public void clearWarnings()
    throws SQLException
  {
    this.sqlWarning = null;
  }

  public boolean rowUpdated()
    throws SQLException
  {
    return false;
  }

  public boolean rowInserted()
    throws SQLException
  {
    return false;
  }

  public boolean rowDeleted()
    throws SQLException
  {
    return false;
  }

  public void updateNull(int paramInt) throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateNull");
  }

  public void updateBoolean(int paramInt, boolean paramBoolean)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBoolean");
  }

  public void updateByte(int paramInt, byte paramByte)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateByte");
  }

  public void updateShort(int paramInt, short paramShort)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateShort");
  }

  public void updateInt(int paramInt1, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateInt");
  }

  public void updateLong(int paramInt, long paramLong)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateLong");
  }

  public void updateFloat(int paramInt, float paramFloat)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateFloat");
  }

  public void updateDouble(int paramInt, double paramDouble)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateDouble");
  }

  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBigDecimal");
  }

  public void updateString(int paramInt, String paramString)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateString");
  }

  public void updateBytes(int paramInt, byte[] paramArrayOfByte)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBytes");
  }

  public void updateDate(int paramInt, Date paramDate)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateDate");
  }

  public void updateTime(int paramInt, Time paramTime)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateTime");
  }

  public void updateTimestamp(int paramInt, Timestamp paramTimestamp)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateTimestamp");
  }

  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateAsciiStream");
  }

  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBinaryStream");
  }

  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateCharacterStream");
  }

  public void updateObject(int paramInt1, Object paramObject, int paramInt2)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateObject");
  }

  public void updateObject(int paramInt, Object paramObject)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateObject");
  }

  public void updateOracleObject(int paramInt, Datum paramDatum)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateOracleObject");
  }

  public void updateROWID(int paramInt, ROWID paramROWID)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateROWID");
  }

  public void updateNUMBER(int paramInt, NUMBER paramNUMBER)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateNUMBER");
  }

  public void updateDATE(int paramInt, DATE paramDATE)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateDATE");
  }

  public void updateARRAY(int paramInt, ARRAY paramARRAY)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateARRAY");
  }

  public void updateSTRUCT(int paramInt, STRUCT paramSTRUCT)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateSTRUCT");
  }

  public void updateOPAQUE(int paramInt, OPAQUE paramOPAQUE)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateOPAQUE");
  }

  public void updateREF(int paramInt, REF paramREF)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateREF");
  }

  public void updateCHAR(int paramInt, CHAR paramCHAR)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateCHAR");
  }

  public void updateRAW(int paramInt, RAW paramRAW)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateRAW");
  }

  public void updateBLOB(int paramInt, BLOB paramBLOB)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBLOB");
  }

  public void updateCLOB(int paramInt, CLOB paramCLOB)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateCLOB");
  }

  public void updateBFILE(int paramInt, BFILE paramBFILE)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBFILE");
  }

  public void updateINTERVALYM(int paramInt, INTERVALYM paramINTERVALYM)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateINTERVALYM");
  }

  public void updateINTERVALDS(int paramInt, INTERVALDS paramINTERVALDS)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateINTERVALDS");
  }

  public void updateTIMESTAMP(int paramInt, TIMESTAMP paramTIMESTAMP)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateTIMESTAMP");
  }

  public void updateTIMESTAMPTZ(int paramInt, TIMESTAMPTZ paramTIMESTAMPTZ)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateTIMESTAMPTZ");
  }

  public void updateTIMESTAMPLTZ(int paramInt, TIMESTAMPLTZ paramTIMESTAMPLTZ)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateTIMESTAMPLTZ");
  }

  public void updateBfile(int paramInt, BFILE paramBFILE)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBfile");
  }

  public void updateCustomDatum(int paramInt, CustomDatum paramCustomDatum)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateCustomDatum");
  }

  public void updateORAData(int paramInt, ORAData paramORAData)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateORAData");
  }

  public void updateRef(int paramInt, Ref paramRef)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateRef");
  }

  public void updateBlob(int paramInt, Blob paramBlob)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateBlob");
  }

  public void updateClob(int paramInt, Clob paramClob)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateClob");
  }

  public void updateArray(int paramInt, Array paramArray)
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateArray");
  }

  public void insertRow()
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "insertRow");
  }

  public void updateRow()
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "updateRow");
  }

  public void deleteRow()
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "deleteRow");
  }

  public void refreshRow()
    throws SQLException
  {
    DatabaseError.throwSqlException(23, null);
  }

  public void cancelRowUpdates()
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "cancelRowUpdates");
  }

  public void moveToInsertRow()
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "moveToInsertRow");
  }

  public void moveToCurrentRow()
    throws SQLException
  {
    DatabaseError.throwSqlException(76, "moveToCurrentRow");
  }

  public void setAutoRefetch(boolean paramBoolean)
    throws SQLException
  {
    this.autoRefetch = paramBoolean;
  }

  public boolean getAutoRefetch()
    throws SQLException
  {
    return this.autoRefetch;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BaseResultSet
 * JD-Core Version:    0.6.0
 */