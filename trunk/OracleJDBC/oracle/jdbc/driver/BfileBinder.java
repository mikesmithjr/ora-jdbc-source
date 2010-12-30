package oracle.jdbc.driver;

class BfileBinder extends DatumBinder
{
  Binder theBfileCopyingBinder = OraclePreparedStatementReadOnly.theStaticBfileCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 114;
    paramBinder.bytelen = 530;
  }

  BfileBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theBfileCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BfileBinder
 * JD-Core Version:    0.6.0
 */