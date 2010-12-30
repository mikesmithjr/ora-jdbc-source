package oracle.jdbc.driver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.SQLException;
import java.util.Map;
import oracle.sql.BLOB;
import oracle.sql.Datum;

class BlobAccessor extends Accessor
{
  static final int maxLength = 4000;

  BlobAccessor(OracleStatement paramOracleStatement, int paramInt1, short paramShort, int paramInt2, boolean paramBoolean)
    throws SQLException
  {
    init(paramOracleStatement, 113, 113, paramShort, paramBoolean);
    initForDataAccess(paramInt2, paramInt1, null);
  }

  BlobAccessor(OracleStatement paramOracleStatement, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, short paramShort)
    throws SQLException
  {
    init(paramOracleStatement, 113, 113, paramShort, false);
    initForDescribe(113, paramInt1, paramBoolean, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramShort, null);

    initForDataAccess(0, paramInt1, null);
  }

  void initForDataAccess(int paramInt1, int paramInt2, String paramString)
    throws SQLException
  {
    if (paramInt1 != 0) {
      this.externalType = paramInt1;
    }
    this.internalTypeMaxLength = 4000;

    if ((paramInt2 > 0) && (paramInt2 < this.internalTypeMaxLength)) {
      this.internalTypeMaxLength = paramInt2;
    }
    this.byteLength = this.internalTypeMaxLength;
  }

  Object getObject(int paramInt)
    throws SQLException
  {
    return getBLOB(paramInt);
  }

  Object getObject(int paramInt, Map paramMap)
    throws SQLException
  {
    return getBLOB(paramInt);
  }

  Datum getOracleObject(int paramInt)
    throws SQLException
  {
    return getBLOB(paramInt);
  }

  BLOB getBLOB(int paramInt)
    throws SQLException
  {
    BLOB localBLOB = null;

    if (this.rowSpaceIndicator == null)
    {
      DatabaseError.throwSqlException(21);
    }

    if (this.rowSpaceIndicator[(this.indicatorIndex + paramInt)] != -1)
    {
      int i = this.columnIndex + this.byteLength * paramInt;
      int j = this.rowSpaceIndicator[(this.lengthIndex + paramInt)];

      byte[] arrayOfByte = new byte[j];
      System.arraycopy(this.rowSpaceByte, i, arrayOfByte, 0, j);

      localBLOB = new BLOB(this.statement.connection, arrayOfByte);
    }

    return localBLOB;
  }

  InputStream getAsciiStream(int paramInt)
    throws SQLException
  {
    BLOB localBLOB = getBLOB(paramInt);

    if (localBLOB == null) {
      return null;
    }
    return localBLOB.asciiStreamValue();
  }

  Reader getCharacterStream(int paramInt)
    throws SQLException
  {
    BLOB localBLOB = getBLOB(paramInt);

    if (localBLOB == null) {
      return null;
    }
    return localBLOB.characterStreamValue();
  }

  InputStream getBinaryStream(int paramInt)
    throws SQLException
  {
    BLOB localBLOB = getBLOB(paramInt);

    if (localBLOB == null) {
      return null;
    }
    return localBLOB.getBinaryStream();
  }

  byte[] privateGetBytes(int paramInt)
    throws SQLException
  {
    return super.getBytes(paramInt);
  }

  byte[] getBytes(int paramInt)
    throws SQLException
  {
    BLOB localBLOB = getBLOB(paramInt);

    if (localBLOB == null) {
      return null;
    }
    InputStream localInputStream = localBLOB.getBinaryStream();
    int i = localBLOB.getBufferSize();
    int j = 0;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(i);
    byte[] arrayOfByte = new byte[i];
    try
    {
      while ((j = localInputStream.read(arrayOfByte)) != -1)
      {
        localByteArrayOutputStream.write(arrayOfByte, 0, j);
      }
    }
    catch (IOException localIOException)
    {
      DatabaseError.throwSqlException(localIOException);
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      DatabaseError.throwSqlException(151);
    }
    if (localBLOB.isTemporary()) this.statement.addToTempLobsToFree(localBLOB);
    return localByteArrayOutputStream.toByteArray();
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BlobAccessor
 * JD-Core Version:    0.6.0
 */