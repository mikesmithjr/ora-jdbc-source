package oracle.jdbc.driver;

class OracleTimestampBinder extends DatumBinder
{
  Binder theTimestampCopyingBinder = OraclePreparedStatementReadOnly.theStaticTimestampCopyingBinder;

  static void init(Binder paramBinder)
  {
    paramBinder.type = 180;
    paramBinder.bytelen = 11;
  }

  OracleTimestampBinder()
  {
    init(this);
  }

  Binder copyingBinder()
  {
    return this.theTimestampCopyingBinder;
  }
}

/* Location:           D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14.jar
 * Qualified Name:     oracle.jdbc.driver.OracleTimestampBinder
 * JD-Core Version:    0.6.0
 */