package oracle.jdbc.driver;

class RawBinder extends DatumBinder
{
  Binder theRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticRawCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 23;
    paramBinder.bytelen = 2000;
  }

  RawBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theRawCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.RawBinder
 * JD-Core Version:    0.6.0
 */