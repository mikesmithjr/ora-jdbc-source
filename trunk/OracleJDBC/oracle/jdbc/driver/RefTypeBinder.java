package oracle.jdbc.driver;

class RefTypeBinder extends TypeBinder
{
  Binder theRefTypeCopyingBinder = OraclePreparedStatementReadOnly.theStaticRefTypeCopyingBinder;

  RefTypeBinder()
  {
    init(this);
  }

  static void init(Binder paramBinder)
  {
    paramBinder.type = 111;
    paramBinder.bytelen = 24;
  }

  Binder copyingBinder()
  {
    return this.theRefTypeCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.RefTypeBinder
 * JD-Core Version:    0.6.0
 */