package oracle.jdbc.driver;

class NamedTypeBinder extends TypeBinder
{
  Binder theNamedTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticNamedTypeCopyingBinder;

  NamedTypeBinder()
  {
    init(this);
  }

  static void init(Binder paramBinder)
  {
    paramBinder.type = 109;
    paramBinder.bytelen = 24;
  }

  Binder copyingBinder()
  {
    return this.theNamedTypeCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.NamedTypeBinder
 * JD-Core Version:    0.6.0
 */