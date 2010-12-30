package oracle.jdbc.driver;

import java.io.IOException;
import java.sql.SQLException;

public abstract class OracleInputStream extends OracleBufferedStream
{
  OracleStatement statement;
  int columnIndex;
  Accessor accessor;
  OracleInputStream nextStream;
  boolean hasBeenOpen = false;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:51_PDT_2005";

  protected OracleInputStream(OracleStatement paramOracleStatement, int paramInt, Accessor paramAccessor)
  {
    super(paramOracleStatement.connection.getDefaultStreamChunkSize());

    this.closed = true;
    this.statement = paramOracleStatement;
    this.columnIndex = paramInt;
    this.accessor = paramAccessor;
    this.nextStream = null;

    OracleInputStream localOracleInputStream = this.statement.streamList;

    if ((localOracleInputStream == null) || (this.columnIndex < localOracleInputStream.columnIndex))
    {
      this.nextStream = this.statement.streamList;
      this.statement.streamList = this;
    }
    else if (this.columnIndex == localOracleInputStream.columnIndex)
    {
      this.nextStream = localOracleInputStream.nextStream;

      localOracleInputStream.nextStream = null;
      this.statement.streamList = this;
    }
    else
    {
      while ((localOracleInputStream.nextStream != null) && (this.columnIndex > localOracleInputStream.nextStream.columnIndex))
      {
        localOracleInputStream = localOracleInputStream.nextStream;
      }

      if ((localOracleInputStream.nextStream != null) && (this.columnIndex == localOracleInputStream.nextStream.columnIndex))
      {
        this.nextStream = localOracleInputStream.nextStream.nextStream;

        localOracleInputStream.nextStream.nextStream = null;
        localOracleInputStream.nextStream = this;
      }
      else
      {
        this.nextStream = localOracleInputStream.nextStream;

        localOracleInputStream.nextStream = this;
      }
    }
  }

  public String toString()
  {
    return "OIS@" + Integer.toHexString(hashCode()) + "{" + "statement = " + this.statement + ", accessor = " + this.accessor + ", nextStream = " + this.nextStream + ", columnIndex = " + this.columnIndex + ", hasBeenOpen = " + this.hasBeenOpen + "}";
  }

  public boolean needBytes()
    throws IOException
  {
    if (this.closed) {
      return false;
    }
    if (this.pos >= this.count)
    {
      try
      {
        int i = getBytes();

        this.pos = 0;
        this.count = i;

        if (this.count == -1)
        {
          if (this.nextStream == null) {
            this.statement.connection.releaseLine();
          }
          this.closed = true;

          this.accessor.fetchNextColumns();

          return false;
        }

      }
      catch (SQLException localSQLException)
      {
        DatabaseError.SQLToIOException(localSQLException);
      }
    }

    return true;
  }

  public boolean isNull() throws IOException
  {
    boolean bool = false;
    try
    {
      bool = this.accessor.isNull(0);
    }
    catch (SQLException localSQLException)
    {
      DatabaseError.SQLToIOException(localSQLException);
    }

    return bool;
  }

  public boolean isClosed()
  {
    return this.closed;
  }

  public void close() throws IOException
  {
    if ((!this.closed) && (this.hasBeenOpen))
    {
      while (this.statement.nextStream != this)
      {
        this.statement.nextStream.close();

        this.statement.nextStream = this.statement.nextStream.nextStream;
      }

      if (!isNull())
      {
        while (needBytes())
        {
          this.pos = this.count;
        }
      }

      this.closed = true;
    }
  }

  public abstract int getBytes()
    throws IOException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleInputStream
 * JD-Core Version:    0.6.0
 */