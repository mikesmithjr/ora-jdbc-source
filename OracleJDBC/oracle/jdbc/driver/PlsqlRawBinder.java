package oracle.jdbc.driver;

class PlsqlRawBinder extends DatumBinder
{
  Binder thePlsqlRawCopyingBinder = OraclePreparedStatementReadOnly.theStaticPlsqlRawCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 23;
    paramBinder.bytelen = 32767;
  }

  PlsqlRawBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.thePlsqlRawCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.PlsqlRawBinder
 * JD-Core Version:    0.6.0
 */