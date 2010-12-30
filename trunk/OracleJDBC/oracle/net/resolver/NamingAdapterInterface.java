package oracle.net.resolver;

import oracle.net.ns.NetException;

public abstract interface NamingAdapterInterface
{
  public static final boolean DEBUG = false;

  public abstract String resolve(String paramString)
    throws NetException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.resolver.NamingAdapterInterface
 * JD-Core Version:    0.6.0
 */