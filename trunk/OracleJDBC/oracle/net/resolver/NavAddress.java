package oracle.net.resolver;

import java.util.Vector;
import oracle.net.TNSAddress.Address;
import oracle.net.TNSAddress.SchemaObjectFactoryInterface;
import oracle.net.nt.ConnOption;
import oracle.net.nt.ConnStrategy;

public class NavAddress extends Address
  implements NavSchemaObject
{
  public NavAddress(SchemaObjectFactoryInterface paramSchemaObjectFactoryInterface)
  {
    super(paramSchemaObjectFactoryInterface);
  }

  public void navigate(ConnStrategy paramConnStrategy, StringBuffer paramStringBuffer)
  {
    ConnOption localConnOption = new ConnOption();
    localConnOption.addr = this.addr;
    localConnOption.conn_data.append(paramStringBuffer.toString());
    localConnOption.conn_data.append(toString());
    paramConnStrategy.addOption(localConnOption);
  }

  public void addToString(ConnStrategy paramConnStrategy)
  {
    String str = toString();
    for (int i = paramConnStrategy.cOpts.size() - 1; (i >= 0) && (!((ConnOption)paramConnStrategy.cOpts.elementAt(i)).done); i--)
      ((ConnOption)paramConnStrategy.cOpts.elementAt(i)).conn_data.append(str);
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.resolver.NavAddress
 * JD-Core Version:    0.6.0
 */