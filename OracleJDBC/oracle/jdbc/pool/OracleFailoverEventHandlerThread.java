package oracle.jdbc.pool;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import oracle.ons.Notification;
import oracle.ons.ONSException;
import oracle.ons.Subscriber;

class OracleFailoverEventHandlerThread extends Thread
{
  private Notification event = null;
  private OracleConnectionCacheManager cacheManager = null;

  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  OracleFailoverEventHandlerThread()
    throws SQLException
  {
    this.cacheManager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
  }

  public void run()
  {
    Subscriber localSubscriber = null;

    while (this.cacheManager.failoverEnabledCacheExists())
    {
      try
      {
        localSubscriber = (Subscriber)AccessController.doPrivileged(new PrivilegedExceptionAction()
        {
          public Object run()
            throws ONSException
          {
            return new Subscriber("(%\"eventType=database/event/service\")|(%\"eventType=database/event/host\")", "", 30000L);
          }

        });
      }
      catch (PrivilegedActionException localPrivilegedActionException)
      {
      }

      if (localSubscriber != null)
      {
        try
        {
          while (this.cacheManager.failoverEnabledCacheExists())
          {
            if ((this.event = localSubscriber.receive(true)) != null)
              handleEvent(this.event);
          }
        }
        catch (ONSException localONSException)
        {
          localSubscriber.close();
        }

      }

      try
      {
        Thread.currentThread(); Thread.sleep(10000L);
      }
      catch (InterruptedException localInterruptedException)
      {
      }
    }
  }

  void handleEvent(Notification paramNotification)
  {
    try
    {
      int i = 0;

      if (paramNotification.type().equalsIgnoreCase("database/event/service"))
        i = 256;
      else if (paramNotification.type().equalsIgnoreCase("database/event/host")) {
        i = 512;
      }
      if (i != 0)
        this.cacheManager.verifyAndHandleEvent(i, paramNotification.body());
    }
    catch (SQLException localSQLException)
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleFailoverEventHandlerThread
 * JD-Core Version:    0.6.0
 */