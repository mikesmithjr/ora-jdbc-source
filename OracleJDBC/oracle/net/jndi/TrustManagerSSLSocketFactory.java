package oracle.net.jndi;

import javax.net.ssl.SSLContext;

public final class TrustManagerSSLSocketFactory extends CustomSSLSocketFactory
{
  private static final boolean DEBUG = false;

  protected void setDefaultFactory()
  {
    try
    {
      SSLContext localSSLContext = SSLContext.getInstance("SSL");
      localSSLContext.init(null, new javax.net.ssl.TrustManager[] { new TrustManager() }, null);
      setFactory(localSSLContext.getSocketFactory());
    }
    catch (Exception localException)
    {
      super.setDefaultFactory();
    }
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.jndi.TrustManagerSSLSocketFactory
 * JD-Core Version:    0.6.0
 */