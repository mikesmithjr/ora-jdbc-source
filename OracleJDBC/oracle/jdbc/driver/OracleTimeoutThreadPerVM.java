package oracle.jdbc.driver;

import java.sql.SQLException;

class OracleTimeoutThreadPerVM extends OracleTimeout
{
  private static final OracleTimeoutPollingThread watchdog = new OracleTimeoutPollingThread();
  private OracleStatement statement;
  private long interruptAfter;
  private String name;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  OracleTimeoutThreadPerVM(String paramString)
  {
    this.name = paramString;
    this.interruptAfter = 9223372036854775807L;

    watchdog.addTimeout(this);
  }

  void close()
  {
    watchdog.removeTimeout(this);
  }

  synchronized void setTimeout(long paramLong, OracleStatement paramOracleStatement)
    throws SQLException
  {
    if (this.interruptAfter != 9223372036854775807L)
    {
      DatabaseError.throwSqlException(131);
    }

    this.statement = paramOracleStatement;
    this.interruptAfter = (System.currentTimeMillis() + paramLong);
  }

  synchronized void cancelTimeout()
    throws SQLException
  {
    this.statement = null;
    this.interruptAfter = 9223372036854775807L;
  }

  void interruptIfAppropriate(long paramLong)
  {
    if (paramLong > this.interruptAfter)
    {
      synchronized (this)
      {
        if (paramLong > this.interruptAfter)
        {
          if (this.statement.connection.spawnNewThreadToCancel) {
            OracleStatement localOracleStatement = this.statement;
            Thread localThread = new Thread(new Runnable(localOracleStatement) { private final OracleStatement val$s;

              public void run() { try { this.val$s.cancel();
                }
                catch (Throwable localThrowable)
                {
                }
              }
            });
            localThread.setName("interruptIfAppropriate_" + this);
            localThread.setDaemon(true);
            localThread.setPriority(10);
            localThread.start();
          }
          else {
            try {
              this.statement.cancel();
            }
            catch (Throwable localThrowable)
            {
            }

          }

          this.statement = null;
          this.interruptAfter = 9223372036854775807L;
        }
      }
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleTimeoutThreadPerVM
 * JD-Core Version:    0.6.0
 */