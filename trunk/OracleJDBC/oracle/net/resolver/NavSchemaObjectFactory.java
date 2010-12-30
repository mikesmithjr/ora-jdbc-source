package oracle.net.resolver;

import oracle.net.TNSAddress.SchemaObject;
import oracle.net.TNSAddress.SchemaObjectFactoryInterface;

public class NavSchemaObjectFactory
  implements SchemaObjectFactoryInterface
{
  public SchemaObject create(int paramInt)
  {
    switch (paramInt)
    {
    case 0:
      return new NavAddress(this);
    case 1:
      return new NavAddressList(this);
    case 2:
      return new NavDescription(this);
    case 3:
      return new NavDescriptionList(this);
    case 4:
      return new NavServiceAlias(this);
    }
    return null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.resolver.NavSchemaObjectFactory
 * JD-Core Version:    0.6.0
 */