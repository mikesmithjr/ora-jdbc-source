package oracle.jdbc.driver;

class OracleTimeoutPollingThread extends Thread
{
  protected static final String threadName = "OracleTimeoutPollingThread";
  public static final String pollIntervalProperty = "oracle.jdbc.TimeoutPollInterval";
  public static final String pollIntervalDefault = "1000";
  private OracleTimeoutThreadPerVM[] knownTimeouts;
  private int count;
  private long sleepMillis;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:52_PDT_2005";

  public OracleTimeoutPollingThread()
  {
    super("OracleTimeoutPollingThread");

    setDaemon(true);
    setPriority(10);

    this.knownTimeouts = new OracleTimeoutThreadPerVM[2];
    this.count = 0;
    this.sleepMillis = Long.parseLong(OracleDriver.getSystemPropertyPollInterval());

    start();
  }

  public synchronized void addTimeout(OracleTimeoutThreadPerVM paramOracleTimeoutThreadPerVM)
  {
    int i = 0;

    if (this.count >= this.knownTimeouts.length)
    {
      OracleTimeoutThreadPerVM[] arrayOfOracleTimeoutThreadPerVM = new OracleTimeoutThreadPerVM[this.knownTimeouts.length * 4];

      System.arraycopy(this.knownTimeouts, 0, arrayOfOracleTimeoutThreadPerVM, 0, this.knownTimeouts.length);

      i = this.knownTimeouts.length;
      this.knownTimeouts = arrayOfOracleTimeoutThreadPerVM;
    }

    for (; i < this.knownTimeouts.length; i++)
    {
      if (this.knownTimeouts[i] != null)
        continue;
      this.knownTimeouts[i] = paramOracleTimeoutThreadPerVM;
      this.count += 1;

      break;
    }
  }

  public synchronized void removeTimeout(OracleTimeoutThreadPerVM paramOracleTimeoutThreadPerVM)
  {
    for (int i = 0; i < this.knownTimeouts.length; i++)
    {
      if (this.knownTimeouts[i] != paramOracleTimeoutThreadPerVM)
        continue;
      this.knownTimeouts[i] = null;
      this.count -= 1;

      break;
    }
  }

  public void run()
  {
    while (true)
    {
      try
      {
        Thread.sleep(this.sleepMillis);
      }
      catch (InterruptedException localInterruptedException)
      {
      }

      pollOnce();
    }
  }

  private void pollOnce()
  {
    if (this.count > 0)
    {
      long l = System.currentTimeMillis();

      for (int i = 0; i < this.knownTimeouts.length; i++)
      {
        try
        {
          if (this.knownTimeouts[i] != null)
            this.knownTimeouts[i].interruptIfAppropriate(l);
        }
        catch (NullPointerException localNullPointerException)
        {
        }
      }
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleTimeoutPollingThread
 * JD-Core Version:    0.6.0
 */