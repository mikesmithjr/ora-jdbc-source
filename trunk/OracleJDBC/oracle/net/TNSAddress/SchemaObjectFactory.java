package oracle.net.TNSAddress;

public class SchemaObjectFactory
  implements SchemaObjectFactoryInterface
{
  public SchemaObject create(int paramInt)
  {
    switch (paramInt)
    {
    case 0:
      return new Address(this);
    case 1:
      return new AddressList(this);
    case 2:
      return new Description(this);
    case 3:
      return new DescriptionList(this);
    case 4:
      return new ServiceAlias(this);
    }
    return null;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.net.TNSAddress.SchemaObjectFactory
 * JD-Core Version:    0.6.0
 */