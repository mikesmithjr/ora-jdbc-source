package oracle.jdbc.driver;

class IntervalDSBinder extends DatumBinder
{
  Binder theIntervalDSCopyingBinder = OraclePreparedStatementReadOnly.theStaticIntervalDSCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 183;
    paramBinder.bytelen = 11;
  }

  IntervalDSBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theIntervalDSCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.IntervalDSBinder
 * JD-Core Version:    0.6.0
 */