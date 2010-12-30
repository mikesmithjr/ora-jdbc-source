package oracle.net.nt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import oracle.net.ns.NetException;

public abstract interface NTAdapter
{
  public abstract void connect()
    throws IOException;

  public abstract void disconnect()
    throws IOException;

  public abstract InputStream getInputStream()
    throws IOException;

  public abstract OutputStream getOutputStream()
    throws IOException;

  public abstract void setOption(int paramInt, Object paramObject)
    throws IOException, NetException;

  public abstract Object getOption(int paramInt)
    throws IOException, NetException;

  public abstract void abort()
    throws IOException, NetException;
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.nt.NTAdapter
 * JD-Core Version:    0.6.0
 */