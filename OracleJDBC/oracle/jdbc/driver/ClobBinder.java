package oracle.jdbc.driver;

class ClobBinder extends DatumBinder
{
  Binder theClobCopyingBinder = OraclePreparedStatementReadOnly.theStaticClobCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 112;
    paramBinder.bytelen = 4000;
  }

  ClobBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theClobCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.ClobBinder
 * JD-Core Version:    0.6.0
 */