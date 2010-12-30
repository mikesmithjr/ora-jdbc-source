package oracle.jdbc.pool;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import oracle.ons.Notification;
import oracle.ons.ONSException;
import oracle.ons.Subscriber;

class OracleRuntimeLoadBalancingEventHandlerThread extends Thread
{
  private Notification event = null;
  private OracleConnectionCacheManager cacheManager = null;
  String m_service;
  private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;
  public static final boolean TRACE = false;
  public static final boolean PRIVATE_TRACE = false;
  public static final String BUILD_DATE = "Wed_Jun_22_11:18:55_PDT_2005";

  OracleRuntimeLoadBalancingEventHandlerThread(String paramString)
    throws SQLException
  {
    this.m_service = paramString;

    this.cacheManager = OracleConnectionCacheManager.getConnectionCacheManagerInstance();
  }

  public void run()
  {
    Subscriber localSubscriber = null;

    String str = "%\"eventType=database/event/servicemetrics/" + this.m_service + "\"";

    while (this.cacheManager.failoverEnabledCacheExists())
    {
      try
      {
        localSubscriber = (Subscriber)AccessController.doPrivileged(new PrivilegedExceptionAction(str)
        {
          private final String val$type;

          public Object run()
            throws ONSException
          {
            return new Subscriber(this.val$type, "", 30000L);
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
            if ((this.event = localSubscriber.receive(300000L)) != null)
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
      this.cacheManager.parseRuntimeLoadBalancingEvent(this.m_service, paramNotification == null ? null : paramNotification.body());
    }
    catch (SQLException localSQLException)
    {
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.pool.OracleRuntimeLoadBalancingEventHandlerThread
 * JD-Core Version:    0.6.0
 */