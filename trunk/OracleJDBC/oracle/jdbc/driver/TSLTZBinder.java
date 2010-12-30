package oracle.jdbc.driver;

class TSLTZBinder extends DatumBinder
{
  Binder theTSLTZCopyingBinder = OraclePreparedStatementReadOnly.theStaticTSLTZCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 231;
    paramBinder.bytelen = 11;
  }

  TSLTZBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theTSLTZCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.TSLTZBinder
 * JD-Core Version:    0.6.0
 */