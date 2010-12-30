package oracle.net.ano;

import oracle.net.ns.NetOutputStream;
import oracle.net.ns.SessionAtts;

public class AnoNetOutputStream extends NetOutputStream
{
  public AnoNetOutputStream(SessionAtts paramSessionAtts)
  {
    super(paramSessionAtts);
    this.daPkt = new CryptoDataPacket(paramSessionAtts);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.ano.AnoNetOutputStream
 * JD-Core Version:    0.6.0
 */