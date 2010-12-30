package oracle.jdbc.driver;

class BINARY_DOUBLEBinder extends DatumBinder
{
  Binder theBINARY_DOUBLECopyingBinder = OraclePreparedStatementReadOnly.theStaticBINARY_DOUBLECopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 101;
    paramBinder.bytelen = 8;
  }

  BINARY_DOUBLEBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theBINARY_DOUBLECopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.BINARY_DOUBLEBinder
 * JD-Core Version:    0.6.0
 */