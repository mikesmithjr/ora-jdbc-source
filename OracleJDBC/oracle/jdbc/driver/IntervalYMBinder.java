package oracle.jdbc.driver;

class IntervalYMBinder extends DatumBinder
{
  Binder theIntervalYMCopyingBinder = OraclePreparedStatementReadOnly.theStaticIntervalYMCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 182;
    paramBinder.bytelen = 5;
  }

  IntervalYMBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theIntervalYMCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.IntervalYMBinder
 * JD-Core Version:    0.6.0
 */