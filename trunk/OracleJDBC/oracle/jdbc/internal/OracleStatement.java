package oracle.jdbc.internal;

import java.sql.SQLException;

public abstract interface OracleStatement extends oracle.jdbc.OracleStatement
{
  public static final int DEFAULT_RSET_TYPE = 1;
  public static final int CLOSED = 0;
  public static final int ACTIVE = 1;
  public static final int CACHED = 2;
  public static final int NON_CACHED = 3;

  public abstract void setFixedString(boolean paramBoolean);

  public abstract boolean getFixedString();

  public abstract int sendBatch()
    throws SQLException;

  public abstract boolean getserverCursor();

  public abstract int getcacheState();

  public abstract int getstatementType();
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.internal.OracleStatement
 * JD-Core Version:    0.6.0
 */