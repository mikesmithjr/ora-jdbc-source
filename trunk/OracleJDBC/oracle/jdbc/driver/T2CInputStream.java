package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

class T2CInputStream extends OracleInputStream
{
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  native int t2cGetBytes(long paramLong, int paramInt1, byte[] paramArrayOfByte1, int paramInt2, Accessor[] paramArrayOfAccessor, byte[] paramArrayOfByte2, int paramInt3, char[] paramArrayOfChar, int paramInt4, short[] paramArrayOfShort, int paramInt5);

  T2CInputStream(OracleStatement paramOracleStatement, int paramInt, Accessor paramAccessor)
  {
    super(paramOracleStatement, paramInt, paramAccessor);
  }

  T2CInputStream(int paramInt)
  {
    super(null, paramInt, null);
  }

  public int getBytes()
    throws IOException
  {
    int i = t2cGetBytes(this.statement.c_state, this.columnIndex, this.buf, this.chunkSize, this.statement.accessors, this.statement.defineBytes, this.statement.accessorByteOffset, this.statement.defineChars, this.statement.accessorCharOffset, this.statement.defineIndicators, this.statement.accessorShortOffset);

    if (i == -2)
    {
      try
      {
        this.accessor.setNull(this.statement.currentRow);
      }
      catch (SQLException localSQLException)
      {
        DatabaseError.SQLToIOException(localSQLException);
      }

      i = 0;
    }

    if (i <= 0) {
      i = -1;
    }
    return i;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.T2CInputStream
 * JD-Core Version:    0.6.0
 */